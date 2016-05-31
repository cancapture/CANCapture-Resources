//This function is called at the start of every capture
void OnStartCapture(ScriptBlock block)
{
    //Clear this script's output window
    block.ClearOutputText();
 
    //Now print in plain text the command
    block.PrintOutputText("Encryption Engine Started\n", false, false, false, false);
}
 
//This function is called at stop of capture
void OnStopCapture(ScriptBlock block)
{
    //print that the capture was stopped
    block.PrintOutputText("Encryption engine stopped\n", false, false, false, false); //Print to the output window
}
 
uint16 EncryptWord(uint16 val, uint16 key, uint16 seed)
{
    uint16 temp = val ^ seed;   
    temp = temp ^ 0xAF82C58A;
    temp = RollLeft16(temp, 7);
    temp = temp ^ 0x9375A34B;
    temp = temp ^ key;
    return temp;
}
 
uint16 DecryptWord(uint16 val, uint16 key, uint16 seed)
{
    uint16 temp = val ^ key;
    temp = temp ^ 0x9375A34B;
    temp = RollRight16(temp, 7);
    temp = temp ^ 0xAF82C58A;
    temp = temp ^ seed;
    return temp;
}
 
 
//Perform a right roll on 16 bit value
uint16 RollRight16(uint16 val, uint8 roll)
{
    return (val >> roll) | (val << (16 - roll));
}
 
//Perform a left roll on 16 bit value
uint16 RollLeft16(uint16 val, uint8 roll)
{
    return RollRight16(val, 16 - roll);
}
 
//Proprietary Decryption algorithm.
//Algorithm uses a 16 bit key in the last 2 data bytes of the CAN
//message followed by a few rolls and xors on the remaining bytes
void DecryptMessage(Message &msg)
{   
    // X = key, Z1-3 are data byes 0-5
    uint16 X, Z1, Z2, Z3, temp; // Unsigned 16 bit variables
 
    X = (msg.GetData(6) & 0x00ff) + ((msg.GetData(7) << 8) & 0xff00); //this is our key       
    Z1 = (msg.GetData(0) & 0x00ff) + ((msg.GetData(1) << 8) & 0xff00);
    Z2 = (msg.GetData(2) & 0x00ff) + ((msg.GetData(3) << 8) & 0xff00);
    Z3 = (msg.GetData(4) & 0x00ff) + ((msg.GetData(5) << 8) & 0xff00);   
 
    Z3 = DecryptWord(Z3, Z2, X);
    Z2 = DecryptWord(Z2, Z1, X);
    Z1 = DecryptWord(Z1, X, X);
 
    //Now set the decrypted data back into the CAN message
    msg.SetData(0, Z1 & 0x00FF);
    msg.SetData(1, (Z1 >> 8) & 0x00FF);
    msg.SetData(2, Z2 & 0x00FF);
    msg.SetData(3, (Z2 >> 8) & 0x00FF);
    msg.SetData(4, Z3 & 0x00FF);
    msg.SetData(5, (Z3 >> 8) & 0x00FF);
    return;
}
 
void EncryptMessage(Message &msg)
{
        // X = key, Z1-3 are data byes 0-5
    uint16 X, Z1, Z2, Z3, temp; // Unsigned 16 bit variables
 
    //Ideally the algorithm would use rand()..
    uint16 t = GetSystemTime();
    X = EncryptWord(t, 0x1234, 0x5678);
    X = EncryptWord(X, 0x9ABC, 0xDEF4);
 
    //and continue the rest of the algorithm
    Z1 = (msg.GetData(0) & 0x00ff) + ((msg.GetData(1) << 8) & 0xff00);
    Z2 = (msg.GetData(2) & 0x00ff) + ((msg.GetData(3) << 8) & 0xff00);
    Z3 = (msg.GetData(4) & 0x00ff) + ((msg.GetData(5) << 8) & 0xff00);   
 
    Z1 = EncryptWord(Z1, X, X);
    Z2 = EncryptWord(Z2, Z1, X);
    Z3 = EncryptWord(Z3, Z2, X);
 
    //Now set the encrypted data into the CAN message
    msg.SetData(0, Z1 & 0x00FF);
    msg.SetData(1, (Z1 >> 8) & 0x00FF);
    msg.SetData(2, Z2 & 0x00FF);
    msg.SetData(3, (Z2 >> 8) & 0x00FF);
    msg.SetData(4, Z3 & 0x00FF);
    msg.SetData(5, (Z3 >> 8) & 0x00FF);
    msg.SetData(6, X & 0x00FF);
    msg.SetData(7, (X >> 8) & 0x00FF);
 
}
 
//Everytime a message is received, check to see if it is an encrypted packet, and then do
//the work.
void OnReceiveMessage(uint8 inPort, ScriptBlock block, Message &msg)
{
    uint32 desired_pgn = 61184;
 
    if (msg.GetPGN() != desired_pgn)  //only encrypt messages with specified PGN
    {
        block.SendMessage(inPort, msg);
        return;
    }
 
    //port 1 is for decrypting incoming, port 3 is for encrypting outgoing
    if (inPort == 3) 
    {       
        //block.PrintOutputText("Got incoming encrypted packet\n", true, false, false, false);
        DecryptMessage(msg);
    }
    else
    {
        //block.PrintOutputText("Got outgoing encrypt packet\n", false, false, true, false);
        EncryptMessage(msg);
    }
 
    uint8 outPort = inPort;  //transmit out the same set of ports that message was received on
    block.SendMessage(outPort, msg);
}