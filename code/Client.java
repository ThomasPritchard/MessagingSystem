// Usage:
//        java Client user-nickname server-hostname
//
// After initializing and opening appropriate sockets, clients get two possible options. They can either register or login.
// A user must input a username and password in order to register they account.
// When they logout, the threads will close but client will be kept open. If the user then wishes to log back in, new sockets and threads will be created.
//
// Another limitation is that there is no provision to terminate when
// the server dies.

import java.io.*;
import java.net.*;
import java.util.*;

class Client {

  private static BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
  private static boolean socketOpen;
  private static ContentTable messageTable;

  public static void main(String[] args) {

    // Check correct usage:
    if (args.length != 1) {
      Report.errorAndGiveUp("Usage: java Client server-hostname");
    }

    // Initialize information:
    String hostname = args[0];

    // Open sockets:
    PrintStream toServer = null;
    BufferedReader fromServer = null;
    Socket server = null;
    socketOpen = false;
    messageTable = new ContentTable(); // Need to have this user specific when logging out.

    String command;

    try{
      while(true){
        System.out.println("Awaiting command. Please use the following commands:");
        System.out.println("1) quit \n" +
                          "2) login \n" +
                          "3) register");
        command = reader.readLine(); // Awaits for a login or register command.

        if(command.toLowerCase().equals("quit")){ // Exits out of the client program in general i.e. deletes the window.
          socketOpen = false;
          System.exit(0); // Closes the client without server disruption.
        }

        if(socketOpen == false){ // For the user to log back in if needed.
          try{
            toServer = null;
            fromServer = null;
            server = null;
            server = new Socket(hostname, Port.number);
            toServer = new PrintStream(server.getOutputStream());
            fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
            socketOpen = true;
          }
          catch (UnknownHostException e) {
            Report.errorAndGiveUp("Unknown host: " + hostname);
          }
          catch (IOException e) {
            Report.errorAndGiveUp("The server doesn't seem to be running " + e.getMessage());
          }
        }

        ClientLogin loginThread = new ClientLogin(toServer, fromServer, server, messageTable);
        ClientRegister registerThread = new ClientRegister(toServer, fromServer, server, messageTable);

        if(command.toLowerCase().equals("login")){
          toServer.println(command);
          socketOpen = false; // Used for when client logs out, socket will then reopen.
          loginThread.start();
          loginThread.join();
        }
        else{
          if(command.toLowerCase().equals("register")){
            toServer.println(command);
            socketOpen = false;
            registerThread.start();
            registerThread.join();
          }else{
            System.out.println("Unknown command, please try again.");
          }
        }
      }
    }catch(IOException e){
      Report.error("problem with BufferedReader " + e.getMessage());
    }catch(InterruptedException e){
      Report.error("Interruption with thread " + e.getMessage());
    }
  }
}
