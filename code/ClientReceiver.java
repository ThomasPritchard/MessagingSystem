import java.io.*;
import java.net.*;

// Gets messages from other clients via the server (by the
// ServerSender thread).

public class ClientReceiver extends Thread {

  private BufferedReader server;
  private ContentTable messageTable;

  ClientReceiver(BufferedReader server, ContentTable messageTable) {
    this.server = server;
    this.messageTable = messageTable;
  }

  public void run() {

    // Print to the user whatever we get from the server:
    try {
      while (true) {
        String s = server.readLine();

        // If string is equal to quit message from server, then we need to break out of the while loop.

        if(s.equals(" ")){
          System.exit(-1); // Works fine. We break clientSender, serverReciever and serverSender and this can close all threads without problem.
        }
        if(s.equals("logout")){
          break;
        }
        else{
          if (s != null){
            messageTable.add(s); // Adds message (if valid) to the message table for the user to refer to.
            messageTable.setContentPointer(messageTable.contentListSize() - 1);
            System.out.println(s);
          }
          else{
            Report.errorAndGiveUp("Server seems to have died");
          }
        }
      }
    }
    catch (IOException e) {
      Report.errorAndGiveUp("User has quit: " + e.getMessage());
    }
  }
}

/*

 * The method readLine returns null at the end of the stream

 * It may throw IoException if an I/O error occurs

 * See https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html#readLine--


 */
