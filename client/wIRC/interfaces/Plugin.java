package wIRC.interfaces;

public interface Plugin
{
	public String processMessage(String input);
	
	public String getVersion();
}
