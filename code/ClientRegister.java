import java.io.*;
import java.net.*;
import java.util.*;

public class ClientRegister extends Thread{

  private PrintStream toServer;
  private BufferedReader fromServer;
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private Socket server;
  private ContentTable messageTable;

  public ClientRegister(PrintStream toServer, BufferedReader fromServer, Socket server, ContentTable messageTable){
    this.toServer = toServer;
    this.fromServer = fromServer;
    this.server = server;
    this.messageTable = messageTable;
  }

  public void run(){
    try{
      while(true){
        System.out.print("Please enter the name of the user you want to register: ");
        String nickname = reader.readLine();
        toServer.println(nickname); // Tells the server who we are.

        String acknowledge = fromServer.readLine(); // Waits for acknowledgement from the server.
        if(acknowledge.equals("true")){
          System.out.print("Please enter the password that you want to associate with the user: ");
          String password = reader.readLine();
          toServer.println(password); // Sends the server the password that we want to associate our user with.
          System.out.println("User has been created, going to login page.");
          ClientLogin loginThread = new ClientLogin(toServer, fromServer, server, messageTable);
          loginThread.start();
          loginThread.join();
          break;
        }
        else{
          System.out.println("Name has already been registered or name is invalid, please try again.");
        }
      }
    }catch(IOException e){
      Report.error("Problem in bufferedReader " + e.getMessage());
    }catch(InterruptedException e){
      Report.error("Interruption with thread " + e.getMessage());
    }
  }
}
