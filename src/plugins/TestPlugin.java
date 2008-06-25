package plugins;
import wIRC.interfaces.Plugin;

/**
 * Test plugin
 * <br><br>
 * This is used for the testing and development of 
 * the plugin system.
 * <br><br>
 * @author 	see http://code.google.com/p/wirc/wiki/AUTHORS
 */
public class TestPlugin implements Plugin
{
	public static final String ID = "basic plug-in";
	public static final double VERSION = 1.0;
	
	public String[] processInput(String input, String channel)
	{
		if (input.toUpperCase().indexOf("BAN ME") > -1)
		{
			String host = new String(input.substring(1, input.indexOf(" ")));
			
			String[] t = {"MODE " + channel + " +b " + host.substring(host.indexOf("@"))};
			
			return(t);
		}
		
		return null;
	}
	
	public String[] processOutput(String output, String channel)
	{
		return null;
	}
	
	public String[] onLoad()
	{
		return null;
	}
	
	public String getVersion()
	{
		return ID + " v" + VERSION;
	}
}
