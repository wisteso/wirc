package wIRC.interfaces;

public interface Plugin
{
	public String processMessage(String input, String channel);
	
	public String getVersion();
}