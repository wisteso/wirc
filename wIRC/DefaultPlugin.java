package wIRC;
import wIRC.interfaces.Plugin;

public class DefaultPlugin implements Plugin
{	
	public static final String ID = "wIRC basic plug-in";
	public static final double VERSION = 1.0;
	
	public String processMessage(String input)
	{
		return("plugin data received: " + input);
	}
	
	public String getVersion()
	{
		return ID + " v" + VERSION;
	}
}
