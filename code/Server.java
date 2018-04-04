// Usage:
//        java Server
//

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server {

  private static ContentTable nicknameTable = new ContentTable();
  private static ContentTable loginTable = new ContentTable();
  // This table will be shared by the server threads:
  private static ClientTable clientTable = new ClientTable();

  public static void main(String [] args) {

    ServerSocket serverSocket = null;

    try {
      serverSocket = new ServerSocket(Port.number);
    }
    catch (IOException e) {
      Report.errorAndGiveUp("Couldn't listen on port " + Port.number);
    }

    try {
      // We loop for ever, as servers usually do.
      while (true) {
        // Listen to the socket, accepting connections from new clients:
        Socket socket = serverSocket.accept();

        // This is so that we can use readLine():
        BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Call a method instead for the 'awaiting command' part of the client.
        String command = fromClient.readLine(); // read the 'command' from the client.

        if(command.toLowerCase().equals("register")){
          System.out.println("Recieved register command");
          ServerRegister register = new ServerRegister(fromClient, socket, nicknameTable, loginTable, clientTable);
          register.start();
          register.join();
        }
        if(command.toLowerCase().equals("login")){
          ServerLogin login = new ServerLogin(fromClient, socket, loginTable, clientTable, nicknameTable);
          login.start();
          login.join();
        }
      }
    }
    catch (IOException e) {
      // Lazy approach:
      Report.error("IO error " + e.getMessage());
      // A more sophisticated approach could try to establish a new
      // connection. But this is beyond the scope of this simple exercise.
    }
    catch(InterruptedException e){
      Report.error("Interruption of thread");
    }
  }
}
