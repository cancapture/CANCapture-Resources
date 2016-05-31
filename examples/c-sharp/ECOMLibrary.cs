using System;
using System.Collections.Generic;
using System.Text;

//This is required for the DLL Importing
using System.Runtime.InteropServices;


class ECOMLibrary
{

    //Error Return Values
    //Note that if CANTransmit() or CANTransmitEx() returns
    //an error code other than one defined below that it
    //is returning the Error Code Capture Register from the SJA1000
    //CAN controller.  These error codes are explained in
    //Section 5.2.3 of the SJA1000 Application Note AN97076
    //and Section 6.4.9 of SJA1000 Data-Sheet    
    public const Byte ERR_COULD_NOT_START_CAN =         0xFF; //failed to send commend to start CAN controller
    public const Byte ERR_COULD_NOT_ENUMERATE =         0xFE; //switching firmware and/or enumeration on USB bus failed
    public const Byte ERR_SERIAL_NUMBER_NOT_FOUND =     0xFD; //device with passed serial number not found
    public const Byte ERR_DEVICE_CLOSED =               0xFC; //the device at the received handle is closed
    public const Byte ERR_NO_DEVICES_ATTACHED =         0xFB; //No devices found (wait/unplug and try again)
    public const Byte ERR_INVALID_FIRMWARE =            0xFA; //multiple causes - possibly a bad DeviceHandle
    public const Byte ERR_ALREADY_OPEN_AS_CAN =         0xF9; //device open already (existing device handle returned)
    public const Byte ERR_ALREADY_OPEN_AS_SERIAL =      0xF8; //device open already (existing device handle returned)
    public const Byte ERR_NO_FREE_DEVICE =              0xF7; //all attached devices are already open
    public const Byte ERR_INVALID_HANDLE =              0xF6; //invalid device handle passed
    public const Byte ERR_CAN_COULD_NOT_READ_STATUS =   0xF5; //Could not retrieve status from CAN device
    public const Byte ERR_USB_TX_FAILED =               0xF4; //A failured occurred transfering on the USB bus to the device
    public const Byte ERR_USB_RX_FAILED =               0xF3; //A failured occurred transfering on the USB bus to the device
    public const Byte ERR_USB_TX_LENGTH_MISMATCH =      0xF2; //Unexpected error transfering on USB bus
    public const Byte ERR_CAN_TX_TIMEOUT =              0xF1; //tx timeout occurred (msg may send on bus)
    public const Byte ERR_CAN_TX_ABORTED =              0xF0; //synch. transfer aborted due to timeout
    public const Byte ERR_CAN_TX_ABORTED_UNEXPECTED =   0xEF; //synch. transfer unexpectedly aborted
    public const Byte  ERR_NULL_DEVICE_HANDLE =			0xEE; //You passed a NULL device handle
    public const Byte  ERR_INVALID_DEVICE_HANDLE =		0xED;
    public const Byte  ERR_CAN_TX_BUFFER_FULL =			0xEC; //The async transfer buffer is full, wait and try again
    public const Byte  ERR_CAN_RX_ZEROLENGTH_READ =		0xEB; //Reading the CAN bus returned a zero length msg (unexpected)
    public const Byte  ERR_CAN_NOT_OPENED =				0xEA; //Device has not been opened as CAN
    public const Byte  ERR_SERIAL_NOT_OPENED =			0xE9; //Device has not been opened as Serial
    public const Byte  ERR_COULD_NOT_START_THREAD =		0xE8; //Thread could not be started
    public const Byte  ERR_THREAD_STOP_TIMED_OUT =		0xE7; //Thread did not stop in a reasonable amount of time
    public const Byte  ERR_THREAD_ALREADY_RUNNING =		0xE6;
    public const Byte  ERR_RXTHREAD_ALREADY_RUNNING =	0xE5; //The receive MessageHandler thread is already running
    public const Byte  ERR_CAN_INVALID_SETUP_PROPERTY =	0xE4; //An invalid property was received by the CANSetupDevice() function
    public const Byte  ERR_CAN_INVALID_SETUP_COMMAND =	0xE3; //An invalid flag was received by the CANSetupDevice() function
    public const Byte  ERR_COMMAND_FAILED =				0xE2; //The command passed to SetupDevice failed
    public const Byte  ERR_SERIAL_INVALID_BAUD =		0xE1;
    public const Byte  ERR_DEVICE_UNPLUGGED =			0xE0; //The device was physically removed from the CAN bus after being attached
    public const Byte  ERR_ALREADY_OPEN =				0xDF; //The device is already open
    public const Byte  ERR_NULL_DRIVER_HANDLE =			0xDE; //Could not retrieve a handle to the USB driver
    public const Byte  ERR_SER_TX_BUFFER_FULL =			0xDD;
    public const Byte  ERR_NULL_DEV_SEARCH_HANDLE =		0xDC; //A null device search handle was passed
    public const Byte  ERR_INVALID_DEV_SEARCH_HANDLE =	0xDB; //An invalid search handle was passed
    public const Byte  ERR_CONFIG_COMMAND_TIME_OUT =	0xD9;
    public const Byte  ERR_NO_LONGER_SUPPORTED =		0xD8; //This feature has been removed and is only supported for legacy purposes
    public const Byte  ERR_NULL_PTR_PASSED =			0xD7;
    public const Byte  ERR_INVALID_INPUT_BUFFER =       0xD6; //Unexpected error, driver received invalid input buffer to IOCTL command
    public const Byte  ERR_INVALID_INPUT_COMMAND =      0xD5; //Unexpected error, driver received invalid input buffer to IOCTL command
    public const Byte  ERR_ALREADY_OPEN_DIFFERENT_BAUD= 0xD4; //CAN device is already opened by another application but at a different baud rate
    public const Byte  ERR_ALREADY_OPEN_BY_PROCESS =    0xD3; //Calling process already has this device open, or the unique ID is already in use
    public const Byte  ERR_TOO_MANY_CONNECTS =          0xD2; //Too many applications have connected to the driver (limit to 16)
     

    //Pass this instead of a serial number to CANOpen() or CANOpenFiltered() to find 
    //the first CAN device attached to the USB bus that is not in use.
    //You can then retrieve the serial number by passing the returned handle to GetDeviceInfo()
    public const Byte  CAN_FIND_NEXT_FREE =				0x00;

    //ErrorMessage Control Bytes
    public const Byte  CAN_ERR_BUS =				    0x11; //A CAN Bus error has occurred (DataByte contains ErrorCaptureCode Register)
    public const Byte  CAN_ERR_BUS_OFF_EVENT =		    0x12; //Bus off due to error
    public const Byte  CAN_ERR_RESET_AFTER_BUS_OFF =	0x13; //Error reseting SJA1000 after bus off event
    public const Byte  CAN_ERR_RX_LIMIT_REACHED = 	    0x16; //The default rx error limit (96) has been reached
    public const Byte  CAN_ERR_TX_LIMIT_REACHED = 	    0x17; //The default tx error limit (96) has been reached
    public const Byte  CAN_BUS_BACK_ON_EVENT =		    0x18; //Bus has come back on after a bus off event due to errors
    public const Byte  CAN_ARBITRATION_LOST =		    0x19; //Arbitration lost (DataByte contains location lost) see SJA1000 datasheet
    public const Byte  CAN_ERR_PASSIVE =				0x1A; //SJA1000 has entered error passive mode
    public const Byte  CAN_ERR_OVERRUN =				0x1B; //Embedded firmware has received a receive overrun
    public const Byte  CAN_ERR_OVERRUN_PC =			    0x1C; //PC driver received a receive overrun
    public const Byte  ERR_ERROR_FIFO_OVERRUN =		    0x20; //Error buffer full - new errors will be lost
    public const Byte  ERR_EFF_RX_FIFO_OVERRUN =		0x21; //EFF Receive buffer full - messages will be lost
    public const Byte  ERR_SFF_RX_FIFO_OVERRUN =		0x22; //SFF Receive buffer full - messages will be lost

    public const Byte  CAN_RECEIVED_ERROR_MESSAGES =	0x23; //Received error messages 
    public const Byte  CAN_RECEIVED_SFF_MESSAGES =	    0x24; //Received error messages 
    public const Byte  CAN_RECEIVED_EFF_MESSAGES =	    0x25; //Received error messages 

    //These are some error codes returned by CANTransmit() and CANTransmitEx()
    public const Byte  ERR_CAN_TX_NO_ACK =				0xD9; //device is probably alone on bus

    public const Byte  ERR_SJA1000_EXIT_RESET =		    0x14;
    public const Byte  ERR_SJA1000_ENTER_RESET =		0x15;


    //Status Return Values
    //The following return codes signify the error free
    // completion of a function
    public const Byte  ECI_NO_ERROR =					0x00;
    public const Byte  CAN_NO_RX_MESSAGES =			    0x88;
    public const Byte  CAN_NO_ERROR_MESSAGES =			0x89;
    public const Byte  ECI_NO_MORE_DEVICES =			0x80;

    //Setup Commands and valid properties for each used by CANSetupDevice()
    public const Byte  CAN_CMD_TRANSMIT =				0x00;
        public const Byte  CAN_PROPERTY_ASYNC =		    0x00;
        public const Byte  CAN_PROPERTY_SYNC =			    0x01;

    public const Byte  CAN_CMD_TIMESTAMPS =			    0x10;
        public const Byte  CAN_PROPERTY_RECEIVE_TS =		0x10;
        public const Byte  CAN_PROPERTY_DONT_RECEIVE_TS =	0x11;

    //Setup Properties for CANSetupDevice()



    //The following constants are flags that are passed in the second parameter
    //of the ReceiveCallback function
    public const Byte  CAN_EFF_MESSAGES =			0x30; //context byte is number of messages in EFF buffer
    public const Byte  CAN_SFF_MESSAGES =			0x31; //context byte is number of messages in SFF buffer 
    public const Byte  CAN_ERR_MESSAGES =			0x32; //context byte is number of messages in error buffer
    public const Byte  SER_BYTES_RECEIVED =			0x33; //context byte is number of messages in Serial receive buffer

    //The following flags are passed to CANQueueSize to set which queue to check the size of
    //for a device open as CAN
    public const Byte  CAN_GET_EFF_SIZE =		    0;  //Retrieve the current number of messages waiting to be received
    public const Byte  CAN_GET_MAX_EFF_SIZE =	    1;  //Get the max size of the EFF buffer  (fixed)
    public const Byte  CAN_GET_SFF_SIZE =		    2;  //...
    public const Byte  CAN_GET_MAX_SFF_SIZE =	    3;  //...  (fixed)
    public const Byte  CAN_GET_ERROR_SIZE =		    4;  //...
    public const Byte  CAN_GET_MAX_ERROR_SIZE =	    5;  //...  (fixed)
    public const Byte  CAN_GET_TX_SIZE =			6;  //...
    public const Byte  CAN_GET_MAX_TX_SIZE =		7;  //...  (fixed)

    //for a device open as serial
    public const Byte  SER_GET_RX_SIZE =			8;  //...
    public const Byte  SER_GET_MAX_RX_SIZE =		9;  //...  (fixed)
    public const Byte  SER_GET_TX_SIZE =			10; //...
    public const Byte  SER_GET_MAX_TX_SIZE =		11; //...  (fixed)


    //The following constants are flags that
    //can be passed to the StartDeviceSearch function
    public const Byte  FIND_OPEN =							0x82;
    public const Byte  FIND_UNOPEN =						0x83;
    public const Byte  FIND_ALL =							0x87;

    //The following are the defined baud rates for CAN
    public const Byte  CAN_BAUD_250K =						0x00;
    public const Byte  CAN_BAUD_500K =						0x01;
    public const Byte  CAN_BAUD_1MB =						0x02;
    public const Byte  CAN_BAUD_125K =						0x03;

    //OR this with the baud rate to enable listen-only mode
    public const Byte  CAN_LISTEN_ONLY =					0x80;

    //Serial Baud Rates
    public const Byte  SERIAL_BAUD_2400 =	0;
    public const Byte  SERIAL_BAUD_4800 =	1;
    public const Byte  SERIAL_BAUD_9600 =	2;
    public const Byte  SERIAL_BAUD_19200 =	3;
    public const Byte  SERIAL_BAUD_28800 =	4;
    public const Byte  SERIAL_BAUD_38400 =	5;
    public const Byte  SERIAL_BAUD_57600 =  6;

    //Organize structure in sequential order
    [StructLayout(LayoutKind.Sequential)]
    public struct EFFMessage {
        public UInt32  ID;
        public byte data1;
        public byte data2;
        public byte data3;
        public byte data4;
        public byte data5;
        public byte data6;
        public byte data7;
        public byte data8;
        public byte options;  //BIT 6 = remote frame bit 
		          //set BIT 4 on transmissions for self reception
        public byte DataLength;
        public UInt32 TimeStamp;  //Extending timestamp to support 4 byte TS mode... shouldnt hurt anything for older code using 2 byte mode
    }
    
    //Organize structure in sequential order
    [StructLayout(LayoutKind.Sequential)]
    public struct SFFMessage {
        public byte IDH;
        public byte IDL;
        public byte data1;
        public byte data2;
        public byte data3;
        public byte data4;
        public byte data5;
        public byte data6;
        public byte data7;
        public byte data8;
        public byte options;  //BIT 6 = remote frame bit 
				          //set BIT 4 on transmissions for self reception
        public byte DataLength;
        public UInt32 TimeStamp;  //Extending timestamp to support 4 byte TS mode... shouldnt hurt anything for older code using 2 byte mode
    }

    //Organize structure in sequential order
    [StructLayout(LayoutKind.Sequential)]
    public struct DeviceInfo {
        public UInt32 SerialNumber;  //Device serial number
        public Byte CANOpen;	   //is device opened as CAN
        public Byte SEROpen;	   //is device opened as Serial
        public Byte _reserved;	   //legacy support - was used to indicate if message handler was running - now its always running
        public Byte SyncCANTx;	   //always FALSE if returned by FindNextDevice
        public UInt32 DeviceHandle;  //NULL if structure returned by FindNextDevice - required b/c search is across all processes using the DLL and
				               //HANDLE will be invalid across multiple processes.  Each process must keep track of their open HANDLEs

        public UInt32 reserved1;
        public UInt32 reserved2;
        public UInt16 reserved3;            
    }

    //Organize structure in sequential order
    [StructLayout(LayoutKind.Sequential)]
    public struct ErrorMessage {
        public UInt32 ErrorFIFOSize;
        public Byte ErrorCode;
        public Byte ErrorData;
        public double Timestamp;
        public Byte reserved1;
        public Byte reserved2;
    }

    [DllImport("ecommlib.dll")]
    public static extern UInt32 CANOpen(UInt32 Serial, Byte baud, ref Byte error);
    [DllImport("ecommlib.dll")]
    public static extern Byte CANTransmitMessageEx(UInt32 DeviceHandle, ref EFFMessage message);
    [DllImport("ecommlib.dll")]
    public static extern Byte CANTransmitMessage(UInt32 DeviceHandle, ref SFFMessage message);
    [DllImport("ecommlib.dll")]
    public static extern Byte CANReceiveMessageEx(UInt32 DeviceHandle, ref EFFMessage message);
    [DllImport("ecommlib.dll")]
    public static extern Byte CANReceiveMessage(UInt32 DeviceHandle, ref SFFMessage message);
    [DllImport("ecommlib.dll")]
    public static extern Byte GetErrorMessage(UInt32 DeviceHandle, ref ErrorMessage message);
    [DllImport("ecommlib.dll")]
    public static extern Byte GetDeviceInfo(UInt32 DeviceHandle, ref DeviceInfo deviceInfo);
    [DllImport("ecommlib.dll")]
    public static extern Byte CloseDevice(UInt32 DeviceHandle);
    [DllImport("ecommlib.dll")]
    public static extern UInt32 CANOpenFiltered(UInt32 SerialNumber, Byte baud, UInt32 code, UInt32 mask, ref Byte error);        
    [DllImport("ecommlib.dll")]
    public static extern Byte CANSetupDevice(UInt32 DeviceHandle, Byte SetupCommand, Byte SetupProperty);
    [DllImport("ecommlib.dll")]        
    public static extern UInt32 SerialOpen(UInt16 SerialNumber, Byte baud, ref Byte error);
    [DllImport("ecommlib.dll")]
    public static extern Byte SerialWrite(UInt32 DeviceHandle, ref Byte buffer, ref Int32 length);
    [DllImport("ecommlib.dll")]
    public static extern Byte SerialRead(UInt32 DeviceHandle, ref Byte buffer, ref Int32 buffer_length);
    [DllImport("ecommlib.dll")]
    public static extern Int32 GetQueueSize(UInt32 DeviceHandle, Byte flag);
    [DllImport("ecommlib.dll")]
    public static extern void GetFriendlyErrorMessage(Byte error, StringBuilder buffer, Int32 buffer_size);
    
    //Use the following functions to enumerate through devices
    [DllImport("ecommlib.dll")]
    public static extern UInt32 StartDeviceSearch(Byte flag);
    [DllImport("ecommlib.dll")]
    public static extern Byte CloseDeviceSearch(UInt32 searchHandle);
    [DllImport("ecommlib.dll")]
    public static extern Byte FindNextDevice(UInt32 searchHandle, ref DeviceInfo deviceInfo);
   
}
 
//This is a simple example of using the class above.  Remove the code below when using 
//the library in your own custom application
class Program
{
    static int Main(string[] args)
    {
        Byte ReturnError = 0;  //This will be used for all error return status codes
	    UInt32 EcomHandle; //This will be the HANDLE to our ECOM device used for all function calls	
	    long EndTime;	    
        
        //List all available devices.  This is not necessary - just used for reference.  
        //Use this to create a list of ECOM devices if you want your users to be presented
        //with which serial number to open.
        ECOMLibrary.DeviceInfo di = new ECOMLibrary.DeviceInfo();
        UInt32 deviceSearch = ECOMLibrary.StartDeviceSearch(ECOMLibrary.FIND_ALL);
        System.Console.Write("Available Devices:\n");
        while (deviceSearch != 0 && ECOMLibrary.FindNextDevice(deviceSearch, ref di) == ECOMLibrary.ECI_NO_ERROR) {
            System.Console.Write("\tS/N: " + di.SerialNumber + "\n");
        }
        System.Console.Write("\n");
        ECOMLibrary.CloseDeviceSearch(deviceSearch);

        //Open a handle to the first available ECOM device (SN:0)
        EcomHandle = ECOMLibrary.CANOpen(0, ECOMLibrary.CAN_BAUD_250K, ref ReturnError); //ECOM library call to open the first available ECOM device on the computer
	    if (EcomHandle == 0 || ReturnError != 0)
	    {		    
            StringBuilder ErrMsg = new StringBuilder(400);
            ECOMLibrary.GetFriendlyErrorMessage(ReturnError, ErrMsg, 400);  //ECOM Library call to get text describing error code
		    System.Console.Write("CANOpen failed with error message: ");
            System.Console.Write(ErrMsg);
		    return -1;
	    }

        //Print info about which serial number was opened
        ECOMLibrary.GetDeviceInfo(EcomHandle, ref di);
        System.Console.Write("Opened device with S/N: " + di.SerialNumber);               
        
        //This is the recommended behavious for all apps.  Transmits will return immediately w/o indication
        //if they successfully made it out onto the CAN bus or not.
        ECOMLibrary.CANSetupDevice(EcomHandle, ECOMLibrary.CAN_CMD_TRANSMIT, ECOMLibrary.CAN_PROPERTY_ASYNC);    

	    //Just a simple example, this will display all received 29-bit CAN packets for 10 seconds	
        EndTime = System.Environment.TickCount + 10000;
        while (System.Environment.TickCount < EndTime)
	    {
            ECOMLibrary.EFFMessage RxMessage = new ECOMLibrary.EFFMessage();  //This structure will hold the incoming CAN message
            
            ReturnError = ECOMLibrary.CANReceiveMessageEx(EcomHandle, ref RxMessage);  //ECOM library call to get a 29-bit message
            if (ReturnError == ECOMLibrary.CAN_NO_RX_MESSAGES)
		    {
			    System.Threading.Thread.Sleep(1); //wait for messages since there are none yet
		    }
		    else if (ReturnError != 0)
		    {
			    //We got an error message (besides CAN_NO_RX_MESSAGES), so return and exit
                StringBuilder ErrMsg = new StringBuilder(400);            
                ECOMLibrary.GetFriendlyErrorMessage(ReturnError, ErrMsg, 400);
			    System.Console.Write("CANReceiveMessageEx failed with error message: ");
                System.Console.Write(ErrMsg);
			    return -1;
		    }
		    else  //ReturnError == 0, which means we successfully received a message
		    {
			    //Display the important fields all on one line  
                UInt32 ts = RxMessage.TimeStamp;
                //((UInt32)RxMessage.TimeStamp1 << 24) | ((UInt32)RxMessage.TimeStamp2 << 16) | ((UInt32)RxMessage.TimeStamp3 << 8) | ((UInt32)RxMessage.TimeStamp4);
                //ts = ((ts & 0xFF) << 24) | ((ts & 0xFF00) << 8) | ((ts & 0xFF0000) >> 8) | ((ts & 0xFF000000) >> 24);
                
			    System.Console.Write("Timestamp: " + ts + " CAN ID: " + RxMessage.ID + ", Len: " + RxMessage.DataLength + ", Data:");
			    if (RxMessage.DataLength > 0)
                    System.Console.Write(" " + RxMessage.data1);
                if (RxMessage.DataLength > 1)
                    System.Console.Write(" " + RxMessage.data2);
                if (RxMessage.DataLength > 2)
                    System.Console.Write(" " + RxMessage.data3);
                if (RxMessage.DataLength > 3)
                    System.Console.Write(" " + RxMessage.data4);
                if (RxMessage.DataLength > 4)
                    System.Console.Write(" " + RxMessage.data5);
                if (RxMessage.DataLength > 5)
                    System.Console.Write(" " + RxMessage.data6);
                if (RxMessage.DataLength > 6)
                    System.Console.Write(" " + RxMessage.data7);
                if (RxMessage.DataLength > 7)
                    System.Console.Write(" " + RxMessage.data8);                

			    System.Console.Write("\n");
		    }
	    }

	    //Now we are all done, so clean up by closing the ECOM
        ECOMLibrary.CloseDevice(EcomHandle);
	    return 0;
    }
}
