package submission2;

/**
 * This class is a thread that sends a File and it's info to Server

 * @author Ebenezer Osei
 * @version 1.0
 * @invariant the socket has to be connected
 */
import java.io.DataInputStream;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class SendFile extends Thread implements Runnable {
	private DataInputStream inStream = null;
    private DataOutputStream outStream = null;
    private String sourceDirectory = "/Users/kwaku/Desktop/Home/";
    private String dest = "/Users/chiemelienwanisobi/Documents/home-backup/";
    private String fileName;
    private int count;
    
    /**
     * Constructs a new thread
     * @param fileName is the absolute path of the client File
     * @param count is the number of files left in Client home directory
     * @param outStream is the DataOutput Stream of the connection 
     * @param inStream is the DataInput Stream of the connection
     */
	public SendFile (String fileName, int count, DataOutputStream outStream, DataInputStream inStream) {
		this.fileName = fileName;
		this.count = count;
		this.inStream = inStream;
		this.outStream = outStream;
	}
	
	/**
	 * Run Method of the thread. It sends files bits by bits
	 */
		public void run() {
    	final int MAX_BUFFER = 1000;
    	byte [] data = null;
    	int bufferSize = 0;
    	try
    	{
    		
    		//	write the filename below in the File constructor
    		File file = new File(fileName);
    	
    			
    		FileInputStream fileInput = new FileInputStream(file);
    		//get the file length
    		long fileSize = file.length();
    		
    		//System.out.println("File size at server is: " + fileSize + " bytes");
    		//first send the size of the file to the client
    		outStream.writeInt(count);
    		
    		String name = file.getAbsolutePath().replace(sourceDirectory, "");
    		
    		
    		String destName = dest + name;
    		
    		outStream.writeUTF(destName);
    		
    		outStream.writeLong(file.lastModified());
    		
    		String response = inStream.readUTF();
    		
    		System.out.println(response);   		
    		
    		if (response.equals("Send")){
    			
    		System.out.println("Sending " + file.getAbsolutePath());
    		outStream.writeLong(fileSize);
    		outStream.flush();
    		

    		//Now send the file contents
    		if(fileSize > MAX_BUFFER)
    			bufferSize = MAX_BUFFER;
    		else 
    			bufferSize = (int)fileSize;
    		
    		data = new byte[bufferSize];
    		
    		long totalBytesRead = 0;
    		while(true)
    		{
    			//read upto MAX_BUFFER number of bytes from file
    			int readBytes = fileInput.read(data);
    			//send readBytes number of bytes to the client
        		outStream.write(data);
        		outStream.flush();

        		//stop if EOF
    			if(readBytes == -1)//EOF
    				break;
    			
    			totalBytesRead = totalBytesRead + readBytes;
    			
    			//stop if fileLength number of bytes are read
    			if(totalBytesRead == fileSize)
    				break;
    			
    			////update fileSize for the last remaining block of data
    			if((fileSize-totalBytesRead) < MAX_BUFFER)
    				bufferSize = (int) (fileSize-totalBytesRead);
    			
    			//reinitialize the data buffer
    			data = new byte[bufferSize];
    		}
    		fileInput.close();
    		
     	}
    }
    	catch(Exception e)
    	{
    		System.out.println("Done");
    	}
    }
}

