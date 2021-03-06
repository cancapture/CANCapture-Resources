////////////////////////////////////////////////////////////////////////////////////
//
// Script: ASC File Logger
//
//  Description:
//   This script is used to save or record a log file in the ASC format
//
//   All that needs to be done is to change the variable ASC_FILE_NAME below to
//   path of the file that you wish to record.  All packets received by the respective
//   script block will then be recorded to the file in the ASC format.
//  
//  Author:
//    Jonathan Kaufmann
//  Date:
//    03/19/2009
//
/////////////////////////////////////////////////////////////////////////////////////
 
//filename to record
string ASC_FILE_NAME = "c:\\test.asc";               
 
//Global vars
file ascFile;                                        //global file object
bool bFailed;
bool bFileOpen;
 
//This function is called when a new capture is started
void OnStartCapture(ScriptBlock block)
{   
    bFileOpen = false;                        //only create the file if we receive a message       
    bFailed = false;                       
    block.ClearOutputText();                //clear all text in output window   
 
    //Print log message
    block.PrintOutputText("Recording data stream to file: " + ASC_FILE_NAME + "\n", false, false, true, false);   
}
 
//This function is called when a capture is stopped
void OnStopCapture(ScriptBlock block)
{   
    //Write end and close file
    if (bFileOpen == true)
    {
        ascFile.writeString("End Triggerblock\n", 0xFFFFFFFF);
        ascFile.close();   
    }
}
 
//This function is called everytime a message is received on an input port
//"inPort" will hold the port the message was received by (1, 2, or 3)
void OnReceiveMessage(uint8 inPort, ScriptBlock block, Message &msg)
{
    //If we failed to open the file, exit always for this capture
    if (bFailed == true)
        return;
 
    //Open the file when the first message is received
    if (bFileOpen == false)
    {                                   
        //Open the ASC file for writing
        if (ascFile.open(ASC_FILE_NAME, "w") != 0) {
            block.PrintOutputText("Error opening file for write access: " + ASC_FILE_NAME + "\n", true, false, false, true);
            bFailed = true;
            return;
        }               
        bFileOpen = true;   
 
        //Write header to file
        ascFile.writeString("date XXX XXX XX XX:XX:XX am XXXX\n", 0xFFFFFFFF);
        ascFile.writeString("base hex  timestamps absolute\n", 0xFFFFFFFF);
        ascFile.writeString("internal events logged\n", 0xFFFFFFFF);
        ascFile.writeString("// This file was generated by CANCapture - http://www.cancapture.com\n", 0xFFFFFFFF);
        ascFile.writeString("Begin Triggerblock XX XX XX XX:XX:XX am XXXX\n", 0xFFFFFFFF);
        ascFile.writeString("   0.000000 Start of measurement\n", 0xFFFFFFFF);   
    }
 
    //Retrieve various values from the received message
    string sts = msg.GetTimeStamp();
    string sport = inPort;
    string sid = ConvertToHex(msg.GetID(), 0);
    int len = msg.GetDataLength();   
    string sdl = len;
    string s29bit = msg.Is11Bit() != 0 ? "" : "x";
 
    //Prepare the string for this message to be saved to file
    string outLine = "   " + sts + "\t" + sport + " " + sid + s29bit + "\t  Rx   d " + sdl;   
    for (int i = 0; i < len; ++i)
        outLine += " " + ConvertToHex(msg.GetData(i), 2);
    outLine += "\n";
 
    //And write it out to the file
    //block.PrintOutputText(outLine, false, true, false, false);
    ascFile.writeString(outLine, 0xFFFFFFFF);
}
 
//There's no conversion of decimal to hex so this is a simple
//function that will do it.  padZeros is used to define the total number
//of hex characters to use (the remaining characters are padded with 0).
string ConvertToHex(uint value, int padZeros)
{
    string str = "";
    int chars = 0;
 
    while (value != 0)
    {
        chars++;
        uint val = value & 0xF;
        switch (val)
        {
            case 0: str = "0" + str; break;
            case 1: str = "1" + str; break;
            case 2: str = "2" + str; break;
            case 3: str = "3" + str; break;
            case 4: str = "4" + str; break;
            case 5: str = "5" + str; break;
            case 6: str = "6" + str; break;
            case 7: str = "7" + str; break;
            case 8: str = "8" + str; break;
            case 9: str = "9" + str; break;
            case 10: str = "A" + str; break;
            case 11: str = "B" + str; break;
            case 12: str = "C" + str; break;
            case 13: str = "D" + str; break;
            case 14: str = "E" + str; break;
            case 15: str = "F" + str; break;
            default: return "";
        }           
        value = value / 16;                   
    }
 
    //Append extra characters if needed
    for ( ; chars < padZeros; ++chars)
        str = "0" + str;
 
    return str;
}
