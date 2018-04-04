// Class on the server side, to handle the register function of the system.
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerRegister extends Thread{

  private BufferedReader fromClient;
  private Socket socket;
  private ContentTable nicknameTable;
  private ContentTable loginTable;
  private ClientTable clientTable;

  public ServerRegister(BufferedReader fromClient, Socket socket, ContentTable nicknameTable, ContentTable loginTable, ClientTable clientTable){
    this.fromClient = fromClient;
    this.socket = socket;
    this.nicknameTable = nicknameTable;
    this.loginTable = loginTable;
    this.clientTable = clientTable;
  }

  public void run(){
    while(true){
      try{
        PrintStream toClient = new PrintStream(socket.getOutputStream());
        String nickname = fromClient.readLine();
        System.out.println("Recieved nickname");

        if(nicknameTable.inTable(nickname) == false && !(nickname.toLowerCase().equals("logout"))){
          nicknameTable.add(nickname);
          System.out.println("added nickname to table");
          toClient.println("true"); // Sends acknowledgement to client that everything is okay.
          String password = fromClient.readLine();
          nicknameTable.add(password); // Password will always be placed after username.
          System.out.println("Password has been added to table");
          ServerLogin login = new ServerLogin(fromClient, socket, loginTable, clientTable, nicknameTable);
          login.start();
          break;
        }else{
          System.out.println("Name has already been registered or name is invalid, please try again.");
          toClient.println("false");
        }
      }catch(IOException e){
        Report.error("IO error " + e.getMessage());
      }
    }
  }
}
