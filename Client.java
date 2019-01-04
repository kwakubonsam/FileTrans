package submission2;


import java.io.*;
import java.net.Socket;


/**
* 
* @author Ebenezer Osei
*
* Written December 1 2018
* 
* A TCP Client that reads Home directory and sends it to Server using multi threading 
* 
*/

public class Client 
{
  private Socket socket = null;
  private DataInputStream inStream = null;
  private DataOutputStream outStream = null;
  private int fileCount = 0;
  /**
   * Constructor
   */
  public Client() 
  {

  }

  /**
   * Create a socket and sends request to Server
   */
  public void createSocket() 
  {
      try 
      {
      	//connect to localHost at given port #
      	  socket = new Socket("150.243.152.154", 3387);
          inStream = new DataInputStream(socket.getInputStream());
          outStream = new DataOutputStream(socket.getOutputStream());
          System.out.println("Connected");
      }
      catch (IOException io) 
      {
          io.printStackTrace();
      }
  }
 /**
  * Scans through the Home directory and sends all Files to Server 
  */
  public void locateFiles(String sourceDirectory) {
  	
      File srcDir = new File(sourceDirectory);
      if (!srcDir.isDirectory()) {
        System.out.println("Source directory is not valid ..Exiting the client");
        System.exit(0);
      }
      File[] files = srcDir.listFiles();
      fileCount += files.length;
      if (fileCount == 0) {
        System.out.println("Empty directory ..Exiting the client");
        System.exit(0);
      }

    for (int i = 0; i < files.length; i++)
    {
    	 if (files[i].isDirectory()) {
    		 locateFiles(files[i].getAbsolutePath());
    	 }
      SendFile send = new SendFile(files[i].getAbsolutePath(),--fileCount,  outStream,inStream);
      send.start();
      try {
			send.join();
    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
      System.out.println(files[i].getAbsolutePath());
     
    }
  	}
    
  
  /**
   * Main thread
   * @param args
   */
  public static void main(String[] args)
  {
	 
  	Client file = new Client();
      file.createSocket();
      try {
      file.locateFiles("/Users/kwaku/Desktop/Home/");
  } catch (Exception ex) {
  	ex.printStackTrace();
  }
      
  }
}