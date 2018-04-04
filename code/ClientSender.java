import java.io.*;


// Repeatedly reads recipient's nickname and text from the user in two
// separate lines, sending them to the server (read by ServerReceiver
// thread).

public class ClientSender extends Thread {

  private String nickname;
  private PrintStream server;
  private ContentTable messageTable;

  ClientSender(String nickname, PrintStream server, ContentTable messageTable) {
    this.nickname = nickname;
    this.server = server;
    this.messageTable = messageTable;
  }

  public void run() {
    // So that we can use the method readLine:
    BufferedReader user = new BufferedReader(new InputStreamReader(System.in));

    messageTable.setContentPointer(0);

    try {
      System.out.println("These are the available commands you can use: \n" +
                        "1) send \n" +
                        "2) logout \n" +
                        "3) quit \n" +
                        "4) logout \n" +
                        "5) previous \n" +
                        "6) current \n" +
                        "7) next \n" +
                        "8) delete \n");
      // Then loop forever sending messages to recipients via the server:
      while (true) {
        String command = user.readLine();
        if(command.toLowerCase().equals("send")){
          System.out.print("Recipient: ");
          String recipient = user.readLine();
          if(recipient.equals("logout")){
            Report.error("Unable to have logout as a recipient. Please try again.");
          }else{
            System.out.print("Text: ");
            String text = user.readLine();
            messageTable.add(recipient + " : " + text); // Makes it possible to perform a regex to extract recipient and text separately.
            messageTable.setContentPointer(messageTable.contentListSize() - 1);
            // Sends the recipient and text to the server in order to communicate the message to the other side.
            server.println(recipient);
            server.println(text);
          }
        }
        else if(command.toLowerCase().equals("quit")){
          server.println(command);
          server.println("shutdown"); // Sends the shutdown signal to the server.
          break;
        }
        else if(command.toLowerCase().equals("logout")){
          messageTable.clear(); // Clears all previous messages so new user gets a completely new messageTable.
          server.println(command);
          server.println("logout"); // Initialises the logout phase.
          break;
        }
        else if(command.toLowerCase().equals("previous")){ // Go to previous message.

          if(messageTable.contentListSize() <= 1 || messageTable.getContentPointer() <= 0){
            Report.error("Unable to fetch previous message");
          }
          else{
            messageTable.setContentPointer(messageTable.getContentPointer() - 1);
            int messagePosition = messageTable.getContentPointer();
            System.out.println(messageTable.getItem(messagePosition));
          }

        }
        else if(command.toLowerCase().equals("current")){
          if(!(messageTable.contentListSize() == 0)){
            int messagePosition = messageTable.getContentPointer();
            System.out.println(messageTable.getItem(messagePosition));
          }
          else{
            Report.error("There are no messages to get");
          }
        }
        else if(command.toLowerCase().equals("next")){ // Go to next message.

          if(!(messageTable.contentListSize() == 0) || !(messageTable.getContentPointer() == messageTable.contentListSize() - 1)){
            System.out.println("unable to fetch a next message");
          }
          else{
            messageTable.setContentPointer(messageTable.getContentPointer() + 1);
            int messagePosition = messageTable.getContentPointer();
            System.out.println(messageTable.getItem(messagePosition));
          }

        }
        else if(command.toLowerCase().equals("delete")){ // Delete current message. (Possible the last item in the messageTable).

          if(messageTable.contentListSize() == 0){
            Report.error("Unable to delete current message. No current message available");
          }else{
            messageTable.remove(messageTable.getItem(messageTable.getContentPointer()));
            messageTable.setContentPointer(messageTable.getContentPointer() - 1);
          }

        }
        else{
          Report.error("Unknown command");
        }
      }
    }
    catch (IOException e) {
      Report.errorAndGiveUp("Communication broke in ClientSender"
                        + e.getMessage());
    }
  }
}

/*

What happens if recipient is null? Then, according to the Java
documentation, println will send the string "null" (not the same as
null!). So maye we should check for that case! Paticularly in
extensions of this system.

 */
