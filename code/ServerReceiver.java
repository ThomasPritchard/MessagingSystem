import java.net.*;
import java.io.*;
import java.util.concurrent.*;

// Gets messages from client and puts them in a queue, for another
// thread to forward to the appropriate client.

public class ServerReceiver extends Thread {

  private String myClientsName;
  private BufferedReader myClient;
  private ClientTable clientTable;
  private ContentTable loginTable;
  private ContentTable messageTable; // This will be one table for each individual client. A new instance is made whenever a client has been registered

  public ServerReceiver(String n, BufferedReader c, ClientTable t, ContentTable l) {
    myClientsName = n;
    myClient = c;
    clientTable = t;
    loginTable = l;
  }

  public void run() {
    try {
      while (true) {
        String recipient = myClient.readLine();
        String text = myClient.readLine();

        // If recipient is equal to quit, return something that will stop client reciever.
        // Send a message back to the same client. Change recipient to client name and forward a 'quit' message to tell the system to end.
        // In this way, serverSender should remain unchanged.

        if (recipient != null && text != null) {

          Message msg = new Message(myClientsName, text);
          if(recipient.toLowerCase().equals("quit")){
            BlockingQueue<Message> recipientsQueue
              = clientTable.getQueue(myClientsName); // Matches EEEEE in ServerSender.java
            recipientsQueue.offer(msg);
            clientTable.remove(myClientsName); // Removing client's name after quitting.
            loginTable.remove(myClientsName);
            System.out.println(myClientsName + " has disconnected");
            break;
          }
          if(recipient.toLowerCase().equals("logout")){
            // Keep messages being stored in a table.
            // remove client from login table.
            // Send client back to 'awaiting command' section.
            BlockingQueue<Message> recipientsQueue
              = clientTable.getQueue(myClientsName);
            recipientsQueue.offer(msg);
            loginTable.remove(myClientsName); // Removing client's name after quitting to allow someone else to login with that user.
            // Name stays in clientTable in order to have the feature to have messages stored in the table still.
            System.out.println(myClientsName + " has logged out");
            break;
          }
          else{
            BlockingQueue<Message> recipientsQueue
              = clientTable.getQueue(recipient); 
            if (recipientsQueue != null)
              recipientsQueue.offer(msg);
            else
              clientTable.getQueue(myClientsName).offer(new Message("Message for unexistent client ", recipient + " : " + text));
              Report.error("Message for unexistent client "
                           + recipient + ": " + text);
          }
        }
        else
          // No point in closing socket. Just give up.
          return;
        }
      }
    catch (IOException e) {
      Report.error("Something went wrong with the client "
                   + myClientsName + " " + e.getMessage());
      // No point in trying to close sockets. Just give up.
      // We end this thread (we don't do System.exit(1)).
    }
  }
}
