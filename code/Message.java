// Class for putting together the messages to send to each client. 
public class Message {

  private final String sender;
  private final String text;

  Message(String sender, String text) {
    this.sender = sender;
    this.text = text;
  }

  public String getSender() {
    return sender;
  }

  public String getText() {
    return text;
  }

  public String toString() {
    if(text.equals("shutdown"))
      return " ";
    if(text.equals("logout"))
      return "logout";
    if(sender.equals("Message for unexistent client "))
      return sender + text;
    else
      return "From " + sender + ": " + text;
  }
}
