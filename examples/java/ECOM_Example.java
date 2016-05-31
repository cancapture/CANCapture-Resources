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
 * @file ECOM_Example.java
 * @author Jonathan Kaufmann (jkaufmann@econtrols.com)
 * @company EControls, Inc. (http://www.econtrols.com, http://www.CANCapture.com)
 * @date May 15, 2009
**/


import java.nio.ByteBuffer;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;

public class ECOM_Example {
    public static void main(String[] args) throws InterruptedException {
    	ECOMLibrary lib = ECOMLibrary.INSTANCE;    	
    	ByteByReference ReturnErrorRef = new ByteByReference((byte)255);						//create a byref value to return the error code    	
    	Pointer EcomHandle = lib.CANOpen(0, ECOMLibrary.CAN_BAUD_250K, ReturnErrorRef);	//and make the call to the DLL
    	byte ReturnError = ReturnErrorRef.getValue();
        	
    	if (EcomHandle == Pointer.NULL || ReturnError != 0)
    	{
    		ByteBuffer ErrMsg = ByteBuffer.allocate(400);
    		lib.GetFriendlyErrorMessage(ReturnError, ErrMsg, 400);  //ECOM Library call to get text describing error code    		
    		System.out.print("CANOpen failed with error message: ");
    		System.out.print(new String(ErrMsg.array()));
    		return;
    	}
    	

    	//Just a simple example, this will display all received 29-bit CAN packets for 10 seconds	
    	long EndTime = System.currentTimeMillis() + 10000;  
    	while (System.currentTimeMillis() < EndTime)
    	{
    		ECOMLibrary.EFFMessage RxMessage = new ECOMLibrary.EFFMessage();  //This structure will hold the incoming CAN message    		
    		
    		ReturnError = lib.CANReceiveMessageEx(EcomHandle, RxMessage);  //ECOM library call to get a 29-bit message
    		if (ReturnError == ECOMLibrary.CAN_NO_RX_MESSAGES)
    		{
    			Thread.sleep(1); //wait for messages since there are none yet
    		}
    		else if (ReturnError != 0)
    		{
    			//We got an error message (besides CAN_NO_RX_MESSAGES), so return and exit
    			ByteBuffer ErrMsg = ByteBuffer.allocate(400);
    			lib.GetFriendlyErrorMessage(ReturnError, ErrMsg, 400);  //ECOM library call to retrieve text based error interpretation
    			System.out.print("CANReceiveMessageEx failed with error message: ");
    			System.out.print(new String(ErrMsg.array()));
    			return;
    		}
    		else  //ReturnError == 0, which means we successfully received a message
    		{
    			//Display the important fields all on one line
    			System.out.print("CAN ID: 0x" + ConvertToHex(RxMessage.ID, 8) + ", Len: " + RxMessage.DataLength + ", Data: ");
    			for (int i = 0; i < RxMessage.DataLength; i++)
    				System.out.print(ConvertToHex(RxMessage.data[i], 2) + " ");

    			System.out.print("\n");
    		}
    	}

    	//Now we are all done, so clean up by closing the ECOM
    	lib.CloseDevice(EcomHandle);    	
    }       
    
    //Simple little function that converts a byte into hex
    static String ConvertToHex(byte value, int padZeros)
    {
    	int i = value & 0xFF;
    	String str = Integer.toHexString(i);
    	str = str.toUpperCase();
    	
    	int pad = padZeros - str.length();
    	if (pad < 0)
    		return str;
    	
    	for (int x = 0; x < pad; ++x)
    		str = "0" + str;
    	return str;    	    
    }
    
    static String ConvertToHex(int value, int padZeros)
    {
    	String str = Integer.toHexString(value);
    	str = str.toUpperCase();
    	
    	int pad = padZeros - str.length();
    	if (pad < 0)
    		return str;
    	
    	for (int x = 0; x < pad; ++x)
    		str = "0" + str;
    	return str;    	    
    }
}




