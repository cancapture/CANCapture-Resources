/**
 *  EControls, Inc. has placed this source code in the public domain. You can use, modify, and
 *  distribute the source code and executable programs based on the source code. However, note
 *  the following:
 *
 *   DISCLAIMER OF WARRANTY
 *   This source code is provided "as is" and without warranties as to performance or merchantability.
 *   The author and/or distributors of this source code may have made statements about this source code; any
 *   such statements do not constitute warranties and shall not be relied on by the user in deciding whether
 *   to use this source code.  This source code is provided without any express or implied warranties whatsoever.
 *   Because of the diversity of conditions and hardware under which this source code may be used, no warranty of
 *   fitness for a particular purpose is offered. The user is advised to test the source code thoroughly before
 *   relying on it. The user must assume the entire risk of using the source code.
**/
 
/**
 * Author: Jonathan Kaufmann (jkaufmann@econtrols.com)
 * Company: EControls, Inc. (http://www.econtrols.com, http://www.CANCapture.com)
 * Date: June 2, 2009
 * Description:
 *      This file implements a trivial example for how to transmit a multipacket message
 *   using the J1939 transport protocol.  The example transmits one
 *   Broadcast Announce Message (BAM) sequence for the respective multipacket data
 *   that is filled in the mpSA, mpPGN, and mpData variables.
 *
**/
 
/********* Function TransmitBAM ***********************************
 * Transmits one sequence of BAM packets.
 *
 * BAM = Broadcast Announce Message.  It is a multipacket message
 * defined by the SAE J1939 Transport prototol for sending
 * messages longer than 8 bytes to all (global) devices on the
 * network
 *
 * block: Script block object that will be used to transmit message
 *
 * outPort: Script block port to transmit message out (1,2, or 3)
 *
 * SA: source address to use for BAM
 *
 * PGN: Parameter Group Number of respective message being
 *      transmitted
 *
 * data: Array of data to be sent for multipacket message. Should
 *       be greater than 8
 *
 ******************************************************************/
void TransmitBAM(ScriptBlock block, uint8 outPort, uint8 mpSA, uint32 mpPGN, uint8[] mpData)
{
    int arrlen = mpData.length();  //retrieve data length
    int packetcount = (arrlen / 7) + 1; //calculate number of TP.DT packets that will be sent
 
    if (arrlen < 8) {
        block.PrintOutputText("No need for multipacket message with data length = " + arrlen, true, false, false, true);
        return;
    }
    if (packetcount > 255) {
        block.PrintOutputText("Multipacket message too long" + arrlen, true, false, false, true);
        return;
    }
 
    //Now fill the BAM packet with proper values
    Message bam;
    bam.Set11Bit(0);
    bam.SetDataLength(8);
    bam.SetPGN(60416);  //TP.CM.xx packet
    bam.SetPS(255);  //BAM is inheritely to global address
    bam.SetSA(mpSA);  //set SA
 
    //Now fill the BAM variables
    bam.SetData(0, 32);                      //Byte 1 is Control Byte (TP.CM): 32=BAM
    bam.SetData(1, arrlen & 0xFF);          //Byte 2 and 3 is total message size, J1939 is LSB first
    bam.SetData(2, (arrlen >> 8) & 0xFF);    //
    bam.SetData(3, (arrlen / 7) + 1);        //Byte 3 is total number of packets
    bam.SetData(4, 255);                      //Byte 4 is Maximun number of packets
    bam.SetData(5, mpPGN & 0xFF);              //Byte 5, 6 and 7 is PGN of transmitted MP message
    bam.SetData(6, (mpPGN >> 8) & 0xFF);    //
    bam.SetData(7, (mpPGN >> 16) & 0xFF);
    block.SendMessage(1, bam);  //and send the packet
 
    //Prep our TP.DT packet
    Message tpdt;
    tpdt.Set11Bit(0);
    tpdt.SetDataLength(8);  //This packet is always 8 bytes, filled with 255's when <8
    tpdt.SetPGN(60160); //TP.DT
    tpdt.SetPS(255); //BAM is global address
    tpdt.SetSA(mpSA);
 
    //Now send all the necessary TP.DT packets
    int i;
    int tpdt_count = 1;
    int dataPos = 0;
    int len;
    while (arrlen > 0) {
        len = arrlen;    //retrieve the length of next packet
        if (len > 7)
            len = 7;
        arrlen -= len;
 
        //Fill data
        tpdt.SetData(0, tpdt_count++);       
        for (i = 0; i < len; ++i) {
            tpdt.SetData(i + 1, mpData[dataPos]);
            dataPos++;
        }
        //Pad remaining bytes w/ 255 (will only happen on our last packet)
        for (; i < 7; ++i) {
            tpdt.SetData(i + 1, 255);
        }
 
        block.SendMessage(outPort, tpdt);
    }   
}
 
/********* Function OnStartCapture **********************************
 * Event callback that is called by the framework every time the
 * user clicks the "Start Capture" button.
 *
 * Simply clear the output error window in this case
 *
 ******************************************************************/
void OnStartCapture(ScriptBlock block)
{
    block.ClearOutputText(); //Clear this script's output window
}
 
/********* Function OnStartCapture **********************************
 * Event callback that is called by the framework every time a 
 * message is received by one of the input ports on the respective
 * script block
 *
 * This simply calls TransmitBAM once for every packet that is
 * received
 *
 ******************************************************************/
void OnReceiveMessage(uint8 inPort, ScriptBlock block, Message &msg)
{
    //*********************************************************************************   
    // Fill information about our MultiPacket message.
    // mpPGN is the PGN of the transmitted message
    // mpData is an array the holds the data for our multipacket message
    // mpSA is the source address we are pretending to be
    //*********************************************************************************   
    uint8 mpSA = 0;  //ECU
    uint32 mpPGN = 65226;
    uint8[] mpData(10); 
    mpData[0] = 1;
    mpData[1] = 2;
    mpData[2] = 3;
    mpData[3] = 4;
    mpData[4] = 5;
    mpData[5] = 6;
    mpData[6] = 7;
    mpData[7] = 8;
    mpData[8] = 9;
    mpData[9] = 10;
    //*********************************************************************************   
 
    TransmitBAM(block, 1, mpSA, mpPGN, mpData);
 
}