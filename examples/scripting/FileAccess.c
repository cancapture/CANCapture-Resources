void OnStartCapture(ScriptBlock block)
{
       //This is the new file I/O class that encapsulates all file access
       file f;

       //Open file
       f.open("c:\\test.csv", "rb");
       string str = "";

       //Loop through file until end
       int fsize = f.getSize();
       while (f.getPos() < fsize)
       {      
              //Test all reading of variables as well as the get/set/movepos functions
              str += f.readInt8() + "\n";
              f.movePos(-1);
              str += f.readInt8() + "\n";
              f.setPos(f.getPos() - 1);
              str += f.readInt8() + "\n";
              str += f.readInt16() + "\n";
              str += f.readInt32() + "\n";
              str += f.readInt64() + "\n";
              str += f.readUInt8() + "\n";
              str += f.readUInt16() + "\n";
              str += f.readUInt32() + "\n";
              str += f.readUInt64() + "\n";
              str += f.readFloat() + "\n";
              str += f.readDouble() + "\n";
              
              //Test reading of string
              string s;                  
              f.readString(s, 3);
              str += s + "\n";

              //Test reading of large string and treating it as a 
              //BYTE buffer
              int size = f.readString(s, 1000);
              str += "Length " + s.length() + "\n";
              str += s += "\n";

              int x;
              for (x = 0; x < size; ++x)
              {
                     uint8 b = s[x];
                     str += b + " ";
              }
              str += "\n";
       }
       
       block.ClearOutputText();
       block.PrintOutputText(str, true, false, false, false);
       f.close();
}

void OnStopCapture(ScriptBlock block)
{
       //This is the new file I/O class that encapsulates all file access
       file f;
       
       //Open file
       f.open("c:\\test.csv", "wb");
       int i = 0;
       for (i = 0; i < 5; ++i)
       {
              //Test writing of all variable types
              f.writeInt8(i);
              f.writeInt16(i);
              f.writeInt32(i);
              f.writeInt64(i);
              f.writeUInt8(i);
              f.writeUInt16(i);
              f.writeUInt32(i);
              f.writeUInt64(i);
              f.writeFloat(i);
              f.writeDouble(i);          
              
              //Test wtriting of constant string
              f.writeString("abc", 3);          
              
              //Test writing of string, but using as if it were a byte
              //buffer and accessed by index
              string s;
              int x;
              for (x = 0; x < 1000; ++x)
              {
                     s += "0";
                     s[x] = x % 32 + 48;
              }
              f.writeString(s, 1000);
       }
              
       //Close the file - destructor will safely do this too
       f.close();
       
       block.ClearOutputText(); //Clear this scripts output window
       block.PrintOutputText("Capture stopped\n", false, false, false, false); //Print to the output window
}

 
