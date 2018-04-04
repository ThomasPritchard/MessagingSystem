// Each nickname has a different incomming-message queue.

import java.util.concurrent.*;

public class ClientTable {

  private ConcurrentMap<String,BlockingQueue<Message>> queueTable
    = new ConcurrentHashMap<String,BlockingQueue<Message>>();

  public void add(String nickname) {
    queueTable.put(nickname, new LinkedBlockingQueue<Message>());
  }

  // Returns null if the nickname is not in the table:
  public BlockingQueue<Message> getQueue(String nickname) {
    return queueTable.get(nickname);
  }

  public void remove(String nickname){
    queueTable.remove(nickname);
  }
}
