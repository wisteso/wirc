package wIRC.interfaces;
import SortedListModel.SortedListModel;
import wIRC.C;

public interface UserInput
{
	public abstract String askQuestion(String query, String defaultAnswer);
	
	public abstract String getFocusedChat();
	
	public abstract void setServerInfo(String newServer);
	
	public abstract Object[] addChat(String title);
	
	public abstract boolean removeChat(String title);
	
	public abstract void println(String input, C.COLOR style);
	
	public abstract void println(String input, String channel, C.COLOR style);
	
	public abstract void print(String input, String channel, C.COLOR style);
	
	public abstract void addNicks(String channel, String... nicks);
	
	public abstract void removeNicks(String channel, String... nicks);
	
	public abstract void removeNick(String nick);
	
	public abstract void replaceNick(String oldNick, String newNick);
	
	public abstract SortedListModel getNickList(String channel);
}

