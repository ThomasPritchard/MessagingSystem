// Generic class for content based array lists.
import java.util.ArrayList;

public class ContentTable{

  private ArrayList<String> contentList;
  private int contentPointer;

  public ContentTable(){
    contentList = new ArrayList<String>();
  }

  public void add (String content){
    contentList.add(content);
  }

  public void remove (String content){
    contentList.remove(content);
  }

  public boolean inTable (String content){
    if(contentList.contains(content)){
      return true;
    }
    else{
      return false;
    }
  }

  public ArrayList<String> getContentList (){
    return contentList;
  }

  public int getContentPointer(){
    return contentPointer;
  }

  public void setContentPointer(int contentPointer){
    this.contentPointer = contentPointer;
  }

  public int contentListSize(){
    return contentList.size();
  }

  public String getItem(int i){
    return contentList.get(i);
  }

  public int indexOf(String content){
    return contentList.indexOf(content);
  }

  public void clear(){ // Empties all items in the table.
    for(int i = 0 ; i < contentList.size() ; i++){
      remove(getItem(i));
    }
  }
}
