import java.io.*;
import java.net.*;
import java.util.*;

public class ClientLogin extends Thread{

  private PrintStream toServer;
  private BufferedReader fromServer;
  private Socket server;
  private ContentTable messageTable; // Table created to store messages. Acts like a message history.
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

  public ClientLogin(PrintStream toServer, BufferedReader fromServer, Socket server, ContentTable messageTable){
    this.toServer = toServer;
    this.fromServer = fromServer;
    this.server = server;
    this.messageTable = messageTable;
  }

  public void run(){
    try{
      while(true){
        System.out.println("Welcome to the login page. Type 'back' to go back to the main menu");
        System.out.print("Please type in your username: ");
        String nickname = reader.readLine();
        if(nickname.toLowerCase().equals("back")){
          break;
        }
        // Tell the server what my nickname is:
        toServer.println(nickname);

        String acknowledge = fromServer.readLine(); // Recieves confirmation from server if login is correct.
        if(acknowledge.equals("false")){
          System.out.println("Inexistant login or user may already be logged in.");
        }
        else if(acknowledge.equals("empty")){
          System.out.println("Attempted to log in but no one to log in with, please register a user");
          break;
        }
        else{
          System.out.print("Please enter your password: ");
          // Have user input a password here.
          String password = reader.readLine();
          toServer.println(password);

          acknowledge = fromServer.readLine(); // Get acknowledgement from the server if the input password is okay.

          if(acknowledge.equals("false")){
            System.out.println("Password has been entered incorrectly or user may already be logged in. Please try again.");
          }
          else{
            System.out.println("Login successful");
            // Create two client threads of a diferent nature:
            ClientSender sender = new ClientSender(nickname,toServer, messageTable);
            ClientReceiver receiver = new ClientReceiver(fromServer, messageTable);

            // Run them in parallel:
            // These need to be terminated upon quit in order for a client to close without affecting the server.
            sender.start();
            receiver.start();

            // Wait for them to end and close sockets.
            receiver.join();
            fromServer.close();
            sender.join();
            toServer.close();
            server.close();
            break;
          }
        }
      }
    }
    catch (IOException e) {
      Report.errorAndGiveUp("Something wrong " + e.getMessage());
    }
    catch (InterruptedException e) {
      Report.errorAndGiveUp("Unexpected interruption " + e.getMessage());
    }
  }
}
