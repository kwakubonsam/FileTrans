package submission2;

//package networking;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
* 
* @author Ebenezer Osei
*
* UPDATED: December 1 2018
* @version 1.0
* A TCP Server for receiving a Home directory from Client
* @invariant Socket has to be connected 
*/

public class Server 
{
  private Socket socket = null;
  private DataInputStream inStream = null;
  private DataOutputStream outStream = null;
  private ServerSocket serverSocket = null;
  /**
   * Constructs a new Client object
   */
  public Server() 
  {

  }
  /**
   * Function to create a new socket and waits for clients request
   */
  public void createSocket()
  {
      try 
      {
      	//create Server and start listening
      	serverSocket = new ServerSocket(3336);
      	socket = serverSocket.accept();            
          //fetch the streams
          inStream = new DataInputStream(socket.getInputStream());
          outStream = new DataOutputStream(socket.getOutputStream());
          System.out.println("Connected");
      } 
      catch (IOException u) 
      {
          u.printStackTrace();
      } 
  }

  /**
   * function to receives the file sent from server and makes sure it asks for files needed to be downloaded 
   * @return the number of files left
   */
  public int receiveFile()
  {
  	int count = 0;
  	byte [] data = null;
  	//decide the max buffer size in bytes
  	//a typical value for a tcp payload is 1000 bytes, this is because of
  	//the common MTU of the underlying ethernet of 1500 bytes
  	//HOWEVER their is no optimal value for tcp payload, just a best guess i.e. 1000 bytes
  	final int MAX_BUFFER = 1000;
  	try
  	{
  		count = inStream.readInt();
  		
  		//read the size of the file <- coming from Server
  		String name = inStream.readUTF();
  		String something = name.substring(0, name.lastIndexOf("/"));
  		Files.createDirectories(Paths.get(something));
  		System.out.println(name);
  		
  		long lastModified = inStream.readLong();
  		
  		
  		if(!Files.exists(Paths.get(name)) ) {
  			outStream.writeUTF("Send");
  		}
  		else if (lastModified > new File(name).lastModified()){
  			outStream.writeUTF("Send");
  		}
  		else{
  			outStream.writeUTF("Dont");
  			return count;
  		}
  		
  		long fileSize = inStream.readLong();
  		int bufferSize=0;
  		
  		//decide the data reading bufferSize
  		if(fileSize > MAX_BUFFER)
  			bufferSize = MAX_BUFFER;
  		else
  			bufferSize = (int)fileSize;
  		
  		data = new byte[bufferSize];
  		
  		//insert the path/name of your target file
  		FileOutputStream fileOut = new FileOutputStream(name,true);		
  		
  		//now read the file coming from Server & save it onto disk

  		long totalBytesRead = 0;
  		while(socket.isConnected())
  		{
  			//read bufferSize number of bytes from Server
  			int readBytes = inStream.read(data,0,bufferSize);

  			byte[] arrayBytes = new byte[readBytes];
  			System.arraycopy(data, 0, arrayBytes, 0, readBytes);
  			totalBytesRead = totalBytesRead + readBytes;
  			
  			if(readBytes>0)
  			{
  				//write the data to the file
  				fileOut.write(arrayBytes);
  	    		fileOut.flush();
  			}

  			//stop if fileSize number of bytes are read
  			if(totalBytesRead == fileSize)
  				break;
  			
  			//update fileSize for the last remaining block of data
  			if((fileSize-totalBytesRead) < MAX_BUFFER)
  				bufferSize = (int) (fileSize-totalBytesRead);
  			
  			//reinitialize the data buffer
  			data = new byte[bufferSize];
  		}
  		System.out.println("File Size is: "+fileSize + ", number of bytes read are: " + totalBytesRead);
  		//fileOut.close();
  		//inStream.close();
  		
  		}
  		
  	
  	
  	catch(Exception e)
  	{
  		System.out.println("Its done!");
  	}
  	return  count;
  	
  }
  
  /**
   * Main thread for the Server Class
   * @param args
   */
  public static void main(String[] args) 
  {
	  
      Server file = new Server();
      file.createSocket();
     
      int count = 10;
      try {
      while(count!=0){
      	count  = file.receiveFile();
      }
      } catch (Exception ex) {
      	System.out.println("Done");
      }

  }
}
