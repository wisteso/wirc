package plugins;

public interface Plugin
{	
	public String[] processInput(String input, String channel);
	
	public String[] processOutput(String output, String channel);
	
	public String[] onLoad();
	
	public String getVersion();
}