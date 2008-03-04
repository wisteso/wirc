package wIRC.interfaces;
import javax.swing.text.SimpleAttributeSet;
import SortedListModel.SortedListModel;

public interface UserInput
{
	public abstract Object[] addChat(String title);
	
	public abstract boolean removeChat(String title);
	
	public abstract String getFocusedChat();
	
	public abstract void println(String input, SimpleAttributeSet color);
	
	public abstract void println(String input, String channel, SimpleAttributeSet style);
	
	public abstract void print(String input, String channel, SimpleAttributeSet style);
	
	public abstract void addNicks(String channel, String... nicks);
	
	public abstract void removeNicks(String channel, String... nicks);
	
	public abstract void removeNick(String nick);
	
	public abstract void replaceNick(String oldNick, String newNick);
	
	public abstract SortedListModel getNickList(String channel);
}

