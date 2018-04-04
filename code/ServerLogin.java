// Separate class to initiate a separate thread for logging in.
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerLogin extends Thread{

  private BufferedReader fromClient;
  private Socket socket;
  private ContentTable loginTable;
  private ContentTable nicknameTable;
  private ClientTable clientTable;
  private PrintStream toClient;

  public ServerLogin(BufferedReader fromClient, Socket socket, ContentTable loginTable, ClientTable clientTable, ContentTable nicknameTable){
    this.fromClient = fromClient;
    this.socket = socket;
    this.loginTable = loginTable;
    this.clientTable = clientTable;
    this.nicknameTable = nicknameTable;
    try{
      toClient = new PrintStream(socket.getOutputStream());
    }catch(IOException e){
      Report.error("IO error " + e.getMessage());
    }
  }

  public void run(){
    while(true){
      try{
        System.out.println("inside login block");
        String clientName = fromClient.readLine();

        if(nicknameTable.contentListSize() == 0){
          toClient.println("empty");
          break;
        }
        else if(nicknameTable.inTable(clientName) == true){
          toClient.println("true");
          String clientPassword = fromClient.readLine();

          if(nicknameTable.contentListSize() >= nicknameTable.indexOf(clientName) + 2){
            System.out.println("nicknameTable size : " + nicknameTable.contentListSize());
            if(clientPassword.equals(nicknameTable.getItem(nicknameTable.indexOf(clientName) + 1))){
              if(loginTable.inTable(clientName) == false){
                loginTable.add(clientName);
                toClient.println("true");
                // Check in table if nickname is there.
                Report.behaviour(clientName + " connected");

                // We add the client to the table:
                // If user is logged out only, we want to keep user in the clientTable.
                if(clientTable.getQueue(clientName) == null){
                  clientTable.add(clientName);
                }

                // We create and start a new thread to read from the client:
                (new ServerReceiver(clientName, fromClient, clientTable, loginTable)).start();

                // We create and start a new thread to write to the client:
                // Would need to consider if we need to close these connections off too.
                (new ServerSender(clientTable.getQueue(clientName), toClient)).start();
                break;
              }
              else{
                toClient.println("false");
              }
            }
            else{
              toClient.println("false");
            }
          }else{
            System.out.println("Unable to login, no such client exists");
            toClient.println("false");
          }
        }else{
          System.out.println("Unable to login, no such client exists");
          toClient.println("false");
        }
      }catch(IOException e){
        Report.error("IO error " + e.getMessage());
      }
    }
  }
}
