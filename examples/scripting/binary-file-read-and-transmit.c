////////////////////////////////////////////////////////////////////////////////////
//
// Script: Binary File Read and Transmit
//
//  Description:
//   This script is used to read data from a file and transmit it onto the CANbus
//
//   All that needs to be done is to change the variable FIRMWARE_FILE_NAME below to
//   path of the file that you wish to transmit.  Before the script transmits the
//   next 8 bytes of data from the file, it expects a packet with ID=1234 to be
//   received that indicates "data send ready".  Typically reprogramming or firmware
//   update protocols will have a similar "data ready" packet and thus this attempts
//   to mimick similar behavior of these protocols.  For testing, you can simply
//   wire a transmitter block to the scripts input that sends the packet 12345 at a
//   recurring interval.
// 
//  Author:
//    Jonathan Kaufmann
//  Date:
//    9/21/2009
//
/////////////////////////////////////////////////////////////////////////////////////
string FIRMWARE_FILE_NAME = "c:\\test.img";
file handle;
bool bFileOpen;
//This function is called when a new capture is started
//"block" holds an object representing the current script block on the flowchart
//Type "block." for a list of available functions for use in the script
void OnStartCapture(ScriptBlock block)
{
    block.ClearOutputText(); //Clear this script's output window
    block.PrintOutputText("Binary file write started\n", false, false, false, false); //Print to the output window
 
    //Open the firmware image file
    int ret = handle.open(FIRMWARE_FILE_NAME, "rb");
    if (ret != 0) {
        block.PrintOutputText("Failed to open firmware image: \"" + FIRMWARE_FILE_NAME + "\"\n", true, false, false, true);
        bFileOpen = false;  //mark file as not open
    } else {
        block.PrintOutputText("Sending contents of \"" + FIRMWARE_FILE_NAME + "\" onto the CAN bus\n", false, false, false, true);       
        bFileOpen = true; //mark file as open
    }
}
 
//This function is called when a capture is stopped
//"block" holds an object representing the current script block on the flowchart
void OnStopCapture(ScriptBlock block)
{
    if (bFileOpen == true) {
        handle.close();
    }
 
    //block.ClearOutputText(); //Clear this scripts output window
    block.PrintOutputText("Binary file write stopped\n", false, false, false, false); //Print to the output window
}
 
//This function is called everytime a message is received on an input port
//"inPort" will hold the port the message was received by (1, 2, or 3)
//"block" holds an object representing the current script block on the flowchart
//"msg" holds an object representing the message received
//type "block." or "msg." to see a list of available parameters and functions
void OnReceiveMessage(uint8 inPort, ScriptBlock block, Message &msg)
{
    if (!bFileOpen) {
        return;
    }
 
    //Here we pretend that we get some sort of ACK or response from the embedded device that indicates that
    //it is ready to receive the next packet of data containing the firmware image.  Most reprogramming/flash
    //protocols has some sort of similar indicator meaing something to the effect of "read for data".
    //For testing, you can simply use a 29-bit transmitter that sends a packet with ID=1234 at a repeating
    //interval
    if (msg.GetID() == 1234) {
 
        //Prep our data payload packet that will be sent to the embedded device
        Message dataMsg;
        dataMsg.SetID(12345);       
        dataMsg.Set11Bit(0);          //I am assuming its 29-bit CAN messages       
 
        block.PrintOutputText("Sending CAN packet ID=" + dataMsg.GetID() + " with data:", false, false, true, false);
        //Read up to 8 bytes of data from the firmware image to fill the next packet of CAN data   
        int i = 0;
        for (i = 0; i < 8; ++i) {
            if (handle.isEOF()) {   //Check for end of file
                block.PrintOutputText("\nEnd of file reached", false, false, false, true);  //print a message indicating the data
                handle.close();        //Close file and release flag that indicates its open
                bFileOpen = false;
                break;
            }
            uint8 dataByte = handle.readUInt8();  //read a single byte of data from the file
            block.PrintOutputText(" " + dataByte, false, false, true, false);  //print a message indicating the data
            dataMsg.SetData(i, dataByte);  //and store the byte from the file directly into our CAN packet structure       
        }
        block.PrintOutputText("\n", false, false, true, false);
 
        dataMsg.SetDataLength(i);  //set the length to the number of bytes read from the file
 
        //And finally send the data packet
        block.SendMessage(1, dataMsg);               
    }
} 