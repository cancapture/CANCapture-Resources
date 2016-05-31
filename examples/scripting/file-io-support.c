//Class that represents a single file handle
class file
{
    //OpenClose
    int open(const string &in, const string &in)
    int close()
 
    //Position Functions
    int getSize();
    int setPos(const int pos);
    int getPos();
    int movePos(const int mov_delta);
    bool isEOF();
 
    //Read Functions
    uint32 readString(string &out str, uint readlength);
    int8 readInt8();
    int16 readInt16();
    int32 readInt32();
    int64 readInt64();
    uint8 readUInt8();
    uint16 readUInt16();
    uint32 readUInt32();
    uint64 readUInt64();
    float readFloat();
    double readDouble();
    uint readLine(string &out, uint maxlength); //Added to CANCapture V2.47 and later
 
    // Writing Functions
    uint32 writeString(const string &in str, uint32 writelength);
    uint32 writeInt8(const int8 val);
    uint32 writeInt16(const int16 val);
    uint32 writeInt32(const int32 val);
    uint32 writeInt64(const int64 val);
    uint32 writeUInt8(const uint8 val);
    uint32 writeUInt16(const uint16 val);
    uint32 writeUInt32(const uint32 val);
    uint32 writeUInt64(const uint64 val);
    uint32 writeFloat(const float val);
 
    uint32 writeDouble(const double val);
 
};