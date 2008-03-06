package wIRC;
import wIRC.interfaces.Plugin;

/**
 * Test I/O plugin
 * <br><br>
 * This is used for the testing and development of 
 * the plugin system.
 * <br><br>
 * @author 	wisteso@gmail.com
 */
public class TestPlugin implements Plugin
{
	public static final String ID = "wIRC basic plug-in";
	public static final double VERSION = 1.0;
	
	public String processMessage(String input, String channel)
	{
		if (input.toUpperCase().indexOf("BAN ME") > -1)
		{
			String host = new String(input.substring(1, input.indexOf(" ")));
			
			return("MODE " + channel + " +b " + host.substring(host.indexOf("@")));
		}
		else if (input.toUpperCase().indexOf("GRIEF ") > -1)
		{
			int nickIndex = input.indexOf("GRIEF ") + 6;
			
			String nick = input.substring(nickIndex).trim();
			
			if (input.indexOf(" ", nickIndex) > -1)
			{
				String chan = " #GTFO_" + nick + "_";
			
				return("SAJOIN " + nick + chan);
			}
		}
		else if (input.indexOf("*** You were forced to join") > -1)
		{
			String newNick = new String();
			
			int charCount = 6 + (int)(Math.random() * 4.99);
			
			for (int i = 0; i < charCount; ++i)
			{
				newNick += (char)(97 + (Math.random() * 25.99));
			}
			
			return("NICK " + newNick);
		}
		
		return null;
	}
	
	public String getVersion()
	{
		return ID + " v" + VERSION;
	}
}
