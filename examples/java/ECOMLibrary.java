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
 * 
 *   This file was converted from the C/C++ file ecommlib.h for 
 *   interfacing with the ECOM device using Java's JNA
 *   Java Native Access libraries.  
**/

/**
 * @file ECOMLibrary.java
 * @author Jonathan Kaufmann (jkaufmann@econtrols.com)
 * @company EControls, Inc. (http://www.econtrols.com, http://www.CANCapture.com)
 * @date May 15, 2009
**/

import java.nio.ByteBuffer;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.win32.StdCallLibrary;


/// JNA Wrapper for library <b>ECOMLib</b>
public interface ECOMLibrary extends StdCallLibrary {
	ECOMLibrary INSTANCE = (ECOMLibrary)
    Native.loadLibrary("ecommlib", ECOMLibrary.class);
    // Optional: wraps every call to the native library in a
    // synchronized block, limiting native calls to one at a time
	ECOMLibrary SYNC_INSTANCE = (ECOMLibrary)Native.synchronizedLibrary(INSTANCE);
	
	//Error Return Values
	//Note that if CANTransmit() or CANTransmitEx() returns
	//an error code other than one defined below that it
	//is returning the Error Code Capture Register from the SJA1000
	//CAN controller.  These error codes are explained in
	//Section 5.2.3 of the SJA1000 Application Note AN97076
	//and Section 6.4.9 of SJA1000 Data-Sheet
	public static final byte ERR_COULD_NOT_START_CAN			= (byte)0xFF; //failed to send commend to start CAN controller
	public static final byte ERR_COULD_NOT_ENUMERATE			= (byte)0xFE; //switching firmware and/or enumeration on USB bus failed
	public static final byte ERR_SERIAL_NUMBER_NOT_FOUND		= (byte)0xFD; //device with passed serial number not found
	public static final byte ERR_DEVICE_CLOSED					= (byte)0xFC; //the device at the received handle is closed
	public static final byte ERR_NO_DEVICES_ATTACHED			= (byte)0xFB; //No devices found (wait/unplug and try again)
	public static final byte ERR_INVALID_FIRMWARE				= (byte)0xFA; //multiple causes - possibly a bad DeviceHandle
	public static final byte ERR_ALREADY_OPEN_AS_CAN			= (byte)0xF9; //device open already (existing device handle returned)
	public static final byte ERR_ALREADY_OPEN_AS_SERIAL			= (byte)0xF8; //device open already (existing device handle returned)
	public static final byte ERR_NO_FREE_DEVICE					= (byte)0xF7; //all attached devices are already open
	public static final byte ERR_INVALID_HANDLE					= (byte)0xF6; //invalid device handle passed
	public static final byte ERR_CAN_COULD_NOT_READ_STATUS		= (byte)0xF5; //Could not retrieve status from CAN device
	public static final byte ERR_USB_TX_FAILED					= (byte)0xF4; //A failured occurred transfering on the USB bus to the device
	public static final byte ERR_USB_RX_FAILED					= (byte)0xF3; //A failured occurred transfering on the USB bus to the device
	public static final byte ERR_USB_TX_LENGTH_MISMATCH			= (byte)0xF2; //Unexpected error transfering on USB bus
	public static final byte ERR_CAN_TX_TIMEOUT					= (byte)0xF1; //tx timeout occurred (msg may send on bus)
	public static final byte ERR_CAN_TX_ABORTED					= (byte)0xF0; //synch. transfer aborted due to timeout
	public static final byte ERR_CAN_TX_ABORTED_UNEXPECTED		= (byte)0xEF; //synch. transfer unexpectedly aborted
	public static final byte ERR_NULL_DEVICE_HANDLE				= (byte)0xEE; //You passed a NULL device handle
	public static final byte ERR_INVALID_DEVICE_HANDLE			= (byte)0xED;
	public static final byte ERR_CAN_TX_BUFFER_FULL				= (byte)0xEC; //The async transfer buffer is full, wait and try again
	public static final byte ERR_CAN_RX_ZEROLENGTH_READ			= (byte)0xEB; //Reading the CAN bus returned a zero length msg (unexpected)
	public static final byte ERR_CAN_NOT_OPENED					= (byte)0xEA; //Device has not been opened as CAN
	public static final byte ERR_SERIAL_NOT_OPENED				= (byte)0xE9; //Device has not been opened as Serial
	public static final byte ERR_COULD_NOT_START_THREAD			= (byte)0xE8; //Thread could not be started
	public static final byte ERR_THREAD_STOP_TIMED_OUT			= (byte)0xE7; //Thread did not stop in a reasonable amount of time
	public static final byte ERR_THREAD_ALREADY_RUNNING			= (byte)0xE6;
	public static final byte ERR_RXTHREAD_ALREADY_RUNNING		= (byte)0xE5; //The receive MessageHandler thread is already running
	public static final byte ERR_CAN_INVALID_SETUP_PROPERTY		= (byte)0xE4; //An invalid property was received by the CANSetupDevice() function
	public static final byte ERR_CAN_INVALID_SETUP_COMMAND		= (byte)0xE3; //An invalid flag was received by the CANSetupDevice() function
	public static final byte ERR_COMMAND_FAILED					= (byte)0xE2; //The command passed to SetupDevice failed
	public static final byte ERR_SERIAL_INVALID_BAUD			= (byte)0xE1;
	public static final byte ERR_DEVICE_UNPLUGGED				= (byte)0xE0; //The device was physically removed from the CAN bus after being attached
	public static final byte ERR_ALREADY_OPEN					= (byte)0xDF; //The device is already open
	public static final byte ERR_NULL_DRIVER_HANDLE				= (byte)0xDE; //Could not retrieve a handle to the USB driver
	public static final byte ERR_SER_TX_BUFFER_FULL				= (byte)0xDD;
	public static final byte ERR_NULL_DEV_SEARCH_HANDLE			= (byte)0xDC; //A null device search handle was passed
	public static final byte ERR_INVALID_DEV_SEARCH_HANDLE		= (byte)0xDB; //An invalid search handle was passed
	public static final byte ERR_CONFIG_COMMAND_TIME_OUT		= (byte)0xD9;
	public static final byte ERR_NO_LONGER_SUPPORTED			= (byte)0xD8; //This feature has been removed and is only supported for legacy purposes
	public static final byte ERR_NULL_PTR_PASSED				= (byte)0xD7;
	 

	//Pass this instead of a serial number to CANOpen() or CANOpenFiltered() to find 
	//the first CAN device attached to the USB bus that is not in use.
	//You can then retrieve the serial number by passing the returned handle to GetDeviceInfo()
	public static final byte CAN_FIND_NEXT_FREE					= (byte)0x00;

	//ErrorMessage Control Bytes
	public static final byte CAN_ERR_BUS						= (byte)0x11; //A CAN Bus error has occurred (DataByte contains ErrorCaptureCode Register)
	public static final byte CAN_ERR_BUS_OFF_EVENT				= (byte)0x12; //Bus off due to error
	public static final byte CAN_ERR_RESET_AFTER_BUS_OFF		= (byte)0x13; //Error reseting SJA1000 after bus off event
	public static final byte CAN_ERR_RX_LIMIT_REACHED 			= (byte)0x16; //The default rx error limit (96) has been reached
	public static final byte CAN_ERR_TX_LIMIT_REACHED 			= (byte)0x17; //The default tx error limit (96) has been reached
	public static final byte CAN_BUS_BACK_ON_EVENT				= (byte)0x18; //Bus has come back on after a bus off event due to errors
	public static final byte CAN_ARBITRATION_LOST				= (byte)0x19; //Arbitration lost (DataByte contains location lost) see SJA1000 datasheet
	public static final byte CAN_ERR_PASSIVE					= (byte)0x1A; //SJA1000 has entered error passive mode
	public static final byte CAN_ERR_OVERRUN					= (byte)0x1B; //Software hasn't read messages fast enough and hardware overrun occurred
	public static final byte ERR_ERROR_FIFO_OVERRUN				= (byte)0x20; //Error buffer full - new errors will be lost
	public static final byte ERR_EFF_RX_FIFO_OVERRUN			= (byte)0x21; //EFF Receive buffer full - messages will be lost
	public static final byte ERR_SFF_RX_FIFO_OVERRUN			= (byte)0x22; //SFF Receive buffer full - messages will be lost

	public static final byte CAN_RECEIVED_ERROR_MESSAGES		= (byte)0x23; //Received error messages 
	public static final byte CAN_RECEIVED_SFF_MESSAGES			= (byte)0x24; //Received error messages 
	public static final byte CAN_RECEIVED_EFF_MESSAGES			= (byte)0x25; //Received error messages 

	//These are some error codes returned by CANTransmit() and CANTransmitEx()
	public static final byte ERR_CAN_TX_NO_ACK					= (byte)0xD9; //device is probably alone on bus

	public static final byte ERR_SJA1000_EXIT_RESET				= (byte)0x14;
	public static final byte ERR_SJA1000_ENTER_RESET			= (byte)0x15;


	//Status Return Values
	//The following return codes signify the error free
	// completion of a function
	public static final byte ECI_NO_ERROR						= (byte)0x00;
	public static final byte CAN_NO_RX_MESSAGES					= (byte)0x88;
	public static final byte CAN_NO_ERROR_MESSAGES				= (byte)0x89;
	public static final byte ECI_NO_MORE_DEVICES				= (byte)0x80;

	//Setup Commands and valid properties for each used by CANSetupDevice()
	public static final byte CAN_CMD_TRANSMIT					= (byte)0x00;
		public static final byte CAN_PROPERTY_ASYNC					= (byte)0x00;
		public static final byte CAN_PROPERTY_SYNC					= (byte)0x01;

	public static final byte CAN_CMD_TIMESTAMPS					= (byte)0x10;
		public static final byte CAN_PROPERTY_RECEIVE_TS			= (byte)0x10;
		public static final byte CAN_PROPERTY_DONT_RECEIVE_TS		= (byte)0x11;

	//Setup Properties for CANSetupDevice()



	//The following constants are flags that are passed in the second parameter
	//of the ReceiveCallback function
	public static final byte CAN_EFF_MESSAGES					= (byte)0x30; //context byte is number of messages in EFF buffer
	public static final byte CAN_SFF_MESSAGES					= (byte)0x31; //context byte is number of messages in SFF buffer 
	public static final byte CAN_ERR_MESSAGES					= (byte)0x32; //context byte is number of messages in error buffer
	public static final byte SER_BYTES_RECEIVED					= (byte)0x33; //context byte is number of messages in Serial receive buffer

	//The following flags are passed to CANQueueSize to set which queue to check the size of
	//for a device open as CAN
	public static final byte CAN_GET_EFF_SIZE		= 0;  //Retrieve the current number of messages waiting to be received
	public static final byte CAN_GET_MAX_EFF_SIZE	= 1;  //Get the max size of the EFF buffer  (fixed)
	public static final byte CAN_GET_SFF_SIZE		= 2;  //...
	public static final byte CAN_GET_MAX_SFF_SIZE	= 3;  //...  (fixed)
	public static final byte CAN_GET_ERROR_SIZE		= 4;  //...
	public static final byte CAN_GET_MAX_ERROR_SIZE	= 5;  //...  (fixed)
	public static final byte CAN_GET_TX_SIZE			= 6;  //...
	public static final byte CAN_GET_MAX_TX_SIZE		= 7;  //...  (fixed)

	//for a device open as serial
	public static final byte SER_GET_RX_SIZE			= 8;  //...
	public static final byte SER_GET_MAX_RX_SIZE		= 9;  //...  (fixed)
	public static final byte SER_GET_TX_SIZE			= 10; //...
	public static final byte SER_GET_MAX_TX_SIZE		= 11; //...  (fixed)


	//The following constants are flags that
	//can be passed to the StartDeviceSearch function
	public static final byte FIND_OPEN							= (byte)0x82;
	public static final byte FIND_UNOPEN						= (byte)0x83;
	public static final byte FIND_ALL							= (byte)0x87;
	public static final byte FIND_NEXT							= (byte)0x00;

	//The following are the defined baud rates for CAN
	public static final byte CAN_BAUD_250K						= (byte)0x00;
	public static final byte CAN_BAUD_500K						= (byte)0x01;
	public static final byte CAN_BAUD_1MB						= (byte)0x02;
	public static final byte CAN_BAUD_125K						= (byte)0x03;

	//Serial Baud Rates
	public static final byte SERIAL_BAUD_2400	= 0;
	public static final byte SERIAL_BAUD_4800	= 1;
	public static final byte SERIAL_BAUD_9600	= 2;
	public static final byte SERIAL_BAUD_19200	= 3;
	public static final byte SERIAL_BAUD_28800	= 4;
	public static final byte SERIAL_BAUD_38400	= 5;
	public static final byte SERIAL_BAUD_57600	= 6;

	
	//Structure for EFFMessages - 29-bit CAN packets
	public static class EFFMessage extends Structure {
		/// Allocate a new EFFMessage struct on the heap
		public EFFMessage() {}
		/// Cast data at given memory location (pointer + offset) as an existing EFFMessage struct
		public EFFMessage(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}
		/// Create an instance that shares its memory with another EFFMessage instance
		public EFFMessage(EFFMessage struct) { this(struct.getPointer(), 0); }
		public static class ByReference extends EFFMessage implements Structure.ByReference {
			/// Allocate a new EFFMessage.ByRef struct on the heap
			public ByReference() {}
			/// Create an instance that shares its memory with another EFFMessage instance
			public ByReference(EFFMessage struct) { super(struct.getPointer(), 0); }
		}
		public static class ByValue extends EFFMessage implements Structure.ByValue {
			/// Allocate a new EFFMessage.ByVal struct on the heap
			public ByValue() {}
			/// Create an instance that shares its memory with another EFFMessage instance
			public ByValue(EFFMessage struct) { super(struct.getPointer(), 0); }
		}
		public int ID;
		public byte[] data = new byte[(8)];
		
		/// BIT 6 = remote frame bit
		/// set BIT 4 on transmissions for self reception
		public byte options;				
			
		public byte DataLength;		
		
		/// Extending timestamp to support 4 byte TS mode... shouldnt hurt anything for older code using 2 byte mode
		public int TimeStamp;				
	}
	
	///Message structure for 11-bit messages
	public static class SFFMessage extends Structure {
		/// Allocate a new SFFMessage struct on the heap
		public SFFMessage() {}
		/// Cast data at given memory location (pointer + offset) as an existing SFFMessage struct
		public SFFMessage(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}
		/// Create an instance that shares its memory with another SFFMessage instance
		public SFFMessage(SFFMessage struct) { this(struct.getPointer(), 0); }
		public static class ByReference extends SFFMessage implements Structure.ByReference {
			/// Allocate a new SFFMessage.ByRef struct on the heap
			public ByReference() {}
			/// Create an instance that shares its memory with another SFFMessage instance
			public ByReference(SFFMessage struct) { super(struct.getPointer(), 0); }
		}
		public static class ByValue extends SFFMessage implements Structure.ByValue {
			/// Allocate a new SFFMessage.ByVal struct on the heap
			public ByValue() {}
			/// Create an instance that shares its memory with another SFFMessage instance
			public ByValue(SFFMessage struct) { super(struct.getPointer(), 0); }
		}
		public byte IDH;
		public byte IDL;
		public byte[] data = new byte[(8)];		
		
		/// BIT 6 = remote frame bit
		/// set BIT 4 on transmissions for self reception
		public byte options;					
		public byte DataLength;
		
		/// Extending timestamp to support 4 byte TS mode... shouldnt hurt anything for older code using 2 byte mode
		public int TimeStamp;					
	}

	///Structure for storing 
	public static class DeviceInfo extends Structure {
		/// Allocate a new DeviceInfo struct on the heap
		public DeviceInfo() {}
		/// Cast data at given memory location (pointer + offset) as an existing DeviceInfo struct
		public DeviceInfo(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}
		/// Create an instance that shares its memory with another DeviceInfo instance
		public DeviceInfo(DeviceInfo struct) { this(struct.getPointer(), 0); }
		public static class ByReference extends DeviceInfo implements Structure.ByReference {
			/// Allocate a new DeviceInfo.ByRef struct on the heap
			public ByReference() {}
			/// Create an instance that shares its memory with another DeviceInfo instance
			public ByReference(DeviceInfo struct) { super(struct.getPointer(), 0); }
		}
		public static class ByValue extends DeviceInfo implements Structure.ByValue {
			/// Allocate a new DeviceInfo.ByVal struct on the heap
			public ByValue() {}
			/// Create an instance that shares its memory with another DeviceInfo instance
			public ByValue(DeviceInfo struct) { super(struct.getPointer(), 0); }
		}
		/// Device serial number
		public int SerialNumber;
		/// is device opened as CAN
		public byte CANOpen;
		/// is device opened as Serial
		public byte SEROpen;
		/// legacy support - was used to indicate if message handler was running - now its always running
		public byte _reserved;
		/// always FALSE if returned by FindNextDevice
		public byte SyncCANTx;
		/// NULL if structure returned by FindNextDevice - required b/c search is across all processes using the DLL and
		public Pointer DeviceHandle;
		/// HANDLE will be invalid across multiple processes.  Each process must keep track of their open HANDLEs
		public byte[] reserved = new byte[(10)];
	}
	/// <i>native declaration : C:\Documents and Settings\jkaufmann\Desktop\null:186</i>
	public static class ErrorMessage extends Structure {
		/// Allocate a new ErrorMessage struct on the heap
		public ErrorMessage() {}
		/// Cast data at given memory location (pointer + offset) as an existing ErrorMessage struct
		public ErrorMessage(Pointer pointer, int offset) {
			super();
			useMemory(pointer, offset);
			read();
		}
		/// Create an instance that shares its memory with another ErrorMessage instance
		public ErrorMessage(ErrorMessage struct) { this(struct.getPointer(), 0); }
		public static class ByReference extends ErrorMessage implements Structure.ByReference {
			/// Allocate a new ErrorMessage.ByRef struct on the heap
			public ByReference() {}
			/// Create an instance that shares its memory with another ErrorMessage instance
			public ByReference(ErrorMessage struct) { super(struct.getPointer(), 0); }
		}
		public static class ByValue extends ErrorMessage implements Structure.ByValue {
			/// Allocate a new ErrorMessage.ByVal struct on the heap
			public ByValue() {}
			/// Create an instance that shares its memory with another ErrorMessage instance
			public ByValue(ErrorMessage struct) { super(struct.getPointer(), 0); }
		}
		public int ErrorFIFOSize;
		public byte ErrorCode;
		public byte ErrorData;
		public double Timestamp;
		public byte[] reserved = new byte[(2)];
	}
	
	/**
	 * WARNING!!!!!! The callback function data may not work right in all cases - I'm not sure how the Java GC 
	 * will handle callback data pointers since its being called to an outside library where the GC can't keep 
	 * track of its usage.    	 
	**/
	public interface pMessageHandler extends Callback {
		byte invoke(Pointer DeviceHandle, byte flag, int flag_info, Pointer data);
	}
	/**
	 * The following are functions that are exported by the DLL<br>
	 * Original signature : <code>__stdcall HANDLE CANOpen(ULONG, BYTE, BYTE*)</code><br>
	 */
	Pointer CANOpen(int SerialNumber, byte baud, ByteByReference error);
	/**
	 * The following are functions that are exported by the DLL<br>
	 * Original signature : <code>__stdcall HANDLE CANOpen(ULONG, BYTE, BYTE*)</code><br>
	 */
	Pointer CANOpen(int SerialNumber, byte baud, ByteBuffer error);
	/**
	 * Original signature : <code>__stdcall BYTE CANTransmitMessageEx(HANDLE, EFFMessage*)</code><br>
	 */
	byte CANTransmitMessageEx(Pointer cdev, EFFMessage message);
	/**
	 * Original signature : <code>__stdcall BYTE CANTransmitMessage(HANDLE, SFFMessage*)</code><br>
	 */
	byte CANTransmitMessage(Pointer cdev, SFFMessage message);
	/**
	 * Original signature : <code>__stdcall BYTE CANReceiveMessageEx(HANDLE, EFFMessage*)</code><br>
	 */
	byte CANReceiveMessageEx(Pointer cdev, EFFMessage message);
	/**
	 * Original signature : <code>__stdcall BYTE CANReceiveMessage(HANDLE, SFFMessage*)</code><br>
	 */
	byte CANReceiveMessage(Pointer cdev, SFFMessage message);
	/**
	 * Original signature : <code>__stdcall BYTE GetErrorMessage(HANDLE, ErrorMessage*)</code><br>
	 */
	byte GetErrorMessage(Pointer cdev, ErrorMessage message);
	/**
	 * Original signature : <code>__stdcall BYTE GetDeviceInfo(HANDLE, DeviceInfo*)</code><br>
	 */
	byte GetDeviceInfo(Pointer cdev, DeviceInfo deviceInfo);
	/**
	 * Original signature : <code>__stdcall BYTE CloseDevice(HANDLE)</code><br>
	 */
	byte CloseDevice(Pointer DeviceHandle);
	/**
	 * Original signature : <code>__stdcall HANDLE CANOpenFiltered(ULONG, BYTE, DWORD, DWORD, BYTE*)</code><br>
	 */
	Pointer CANOpenFiltered(int SerialNumber, byte baud, int code, int mask, ByteByReference error);
	/**
	 * Original signature : <code>__stdcall HANDLE CANOpenFiltered(ULONG, BYTE, DWORD, DWORD, BYTE*)</code><br>
	 */
	Pointer CANOpenFiltered(int SerialNumber, byte baud, int code, int mask, ByteBuffer error);
	/**
	 * Original signature : <code>__stdcall HANDLE vbCANOpenFiltered(ULONG, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE*)</code><br>
	 */
	Pointer vbCANOpenFiltered(int SerialNumber, byte baud, byte code1, byte code2, byte code3, byte code4, byte mask1, byte mask2, byte mask3, byte mask4, ByteByReference error);
	/**
	 * Original signature : <code>__stdcall HANDLE vbCANOpenFiltered(ULONG, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE, BYTE*)</code><br>
	 */
	Pointer vbCANOpenFiltered(int SerialNumber, byte baud, byte code1, byte code2, byte code3, byte code4, byte mask1, byte mask2, byte mask3, byte mask4, ByteBuffer error);
	/**
	 * Original signature : <code>__stdcall BYTE SetCallbackFunction(HANDLE, pMessageHandler, void*)</code><br>
	 * 
	 * WARNING!!!!!! The callback function data may not work right in all cases - I'm not sure how the Java GC 
	 * will handle callback data pointers since its being called to an outside library where the GC can't keep 
	 * track of its usage.    	 
	 * 
	 */
	byte SetCallbackFunction(Pointer DeviceHandle, pMessageHandler ReceiveCallback, Pointer data);
	/**
	 * CANStartMessageHandler is outdated and should no longer be used - instead use CANSetCallbackFunction<br>
	 * Original signature : <code>__stdcall BYTE CANStartMessageHandler(HANDLE, pMessageHandler, void*)</code><br>
	 * 
	 * WARNING!!!!!! The callback function data may not work right in all cases - I'm not sure how the Java GC 
	 * will handle callback data pointers since its being called to an outside library where the GC can't keep 
	 * track of its usage.     	
	 * 
	 */
	@java.lang.Deprecated
	byte CANStartMessageHandler(Pointer DeviceHandle, pMessageHandler ReceiveCallback, Pointer data);
	/**
	 * CANStopMessageHandler is outdated and should no longer be used - it is equivalent to calling CANSetCallbackFunction with NULL parameters<br>
	 * Original signature : <code>__stdcall BYTE CANStopMessageHandler(HANDLE)</code><br>
	 */
	@java.lang.Deprecated
	byte CANStopMessageHandler(Pointer DeviceHandle);
	/**
	 * Original signature : <code>__stdcall BYTE CANSetupDevice(HANDLE, BYTE, BYTE)</code><br>
	 */
	byte CANSetupDevice(Pointer DeviceHandle, byte SetupCommand, byte SetupProperty);
	/**
	 * Original signature : <code>__stdcall HANDLE SerialOpen(USHORT, BYTE, BYTE*)</code><br>
	 */
	Pointer SerialOpen(short SerialNumber, byte baud, ByteByReference error);
	/**
	 * Original signature : <code>__stdcall HANDLE SerialOpen(USHORT, BYTE, BYTE*)</code><br>
	 */
	Pointer SerialOpen(short SerialNumber, byte baud, ByteBuffer error);
	/**
	 * Original signature : <code>__stdcall BYTE SerialWrite(HANDLE, BYTE*, LONG*)</code><br>
	 */
	byte SerialWrite(Pointer DeviceHandle, ByteByReference buffer, NativeLongByReference length);
	/**
	 * Original signature : <code>__stdcall BYTE SerialWrite(HANDLE, BYTE*, LONG*)</code><br>
	 */
	byte SerialWrite(Pointer DeviceHandle, java.nio.ByteBuffer buffer, NativeLongByReference length);
	/**
	 * Original signature : <code>__stdcall BYTE SerialRead(HANDLE, BYTE*, LONG*)</code><br>
	 */
	byte SerialRead(Pointer DeviceHandle, ByteByReference buffer, NativeLongByReference buffer_length);
	/**
	 * Original signature : <code>__stdcall BYTE SerialRead(HANDLE, BYTE*, LONG*)</code><br>
	 */
	byte SerialRead(Pointer DeviceHandle, ByteBuffer buffer, NativeLongByReference buffer_length);
	/**
	 * Original signature : <code>__stdcall int GetQueueSize(HANDLE, BYTE)</code><br>
	 */
	int GetQueueSize(Pointer cdev, byte flag);
	/**
	 * Original signature : <code>__stdcall void GetFriendlyErrorMessage(BYTE, char*, int)</code><br>
	 */
	void GetFriendlyErrorMessage(byte error, ByteBuffer errMsg, int buffer_size);
	/**
	 * Use the following functions to enumerate through devices<br>
	 * Original signature : <code>__stdcall HANDLE StartDeviceSearch(BYTE)</code><br>
	 */
	Pointer StartDeviceSearch(byte flag);
	/**
	 * Original signature : <code>__stdcall BYTE CloseDeviceSearch(HANDLE)</code><br>
	 */
	byte CloseDeviceSearch(Pointer searchHandle);
	/**
	 * Original signature : <code>__stdcall BYTE FindNextDevice(HANDLE, DeviceInfo*)</code><br>
	 */
	byte FindNextDevice(Pointer searchHandle, DeviceInfo deviceInfo);
}
