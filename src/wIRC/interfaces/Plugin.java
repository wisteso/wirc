package wIRC.interfaces;

public interface Plugin
{	
	public String[] processInput(String input, String channel);
	
	public String[] processOutput(String output);
	
	public String[] onLoad();
	
	public String getVersion();
}