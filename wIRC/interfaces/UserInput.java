package wIRC.interfaces;

import javax.swing.text.SimpleAttributeSet;

import SortedListModel.SortedListModel;

public interface UserInput
{
	public Object[] addChat(String title);
	
	public boolean remChat(String title);
	
	public String getChat();
	
	public void println(String input, SimpleAttributeSet color);
	
	public void println(String input, String channel, SimpleAttributeSet color);
	
	public void print(String input, String channel, SimpleAttributeSet color);
	
	public void addNicks(String chan, String... usrs);
	
	public void remNicks(String chan, String... usrs);
	
	public SortedListModel getList(String channel);
}
