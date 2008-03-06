package plugins;
import wIRC.interfaces.Plugin;

/**
 * Test plugin
 * <br><br>
 * This is used for the testing and development of 
 * the plugin system.
 * <br><br>
 * @author 	wisteso@gmail.com
 */
public class TestPlugin implements Plugin
{
	public static final String ID = "basic plug-in";
	public static final double VERSION = 1.0;
	
	public String processMessage(String input, String channel)
	{
		if (input.toUpperCase().indexOf("BAN ME") > -1)
		{
			String host = new String(input.substring(1, input.indexOf(" ")));
			
			return("MODE " + channel + " +b " + host.substring(host.indexOf("@")));
		}
		
		return null;
	}
	
	public String getVersion()
	{
		return ID + " v" + VERSION;
	}
}
