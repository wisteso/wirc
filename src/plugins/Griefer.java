package plugins;
import wIRC.interfaces.Plugin;

/**
 * Griefing tools plugin
 * <br><br>
 * This plugin will automatically change your nick to 
 * a series of random characters of random length when 
 * you have been forced to join a channel via SAJOIN.
 * <br><br>
 * @author 	wisteso@gmail.com
 */
public class Griefer implements Plugin
{
	public static final String ID = "griefing tools plug-in";
	public static final double VERSION = 0.1;
	
	public String processMessage(String input, String channel)
	{
		if (input.toUpperCase().indexOf("GRIEF ") > -1)
		{
			int nickIndex = input.indexOf("GRIEF ") + 6;
			
			String nick = input.substring(nickIndex).trim();
			
			if (input.indexOf(" ", nickIndex) > -1)
			{
				String chan = " #GTFO_" + nick + "_";
			
				return("SAJOIN " + nick + chan);
			}
		}
		
		return null;
	}
	
	public String getVersion()
	{
		return ID + " v" + VERSION;
	}
}
