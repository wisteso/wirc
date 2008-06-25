package plugins;
import wIRC.interfaces.Plugin;

/**
 * Griefing tools plugin
 * <br><br>
 * This plugin will automatically change your nick to 
 * a series of random characters of random length when 
 * you have been forced to join a channel via SAJOIN.
 * <br><br>
 * @author 	see http://code.google.com/p/wirc/wiki/AUTHORS
 */
public class Griefer implements Plugin
{
	public static final String ID = "griefing tools plug-in";
	public static final double VERSION = 0.1;
	
	public String[] processInput(String input, String channel)
	{
		if (input.toUpperCase().indexOf("GRIEF ") > -1)
		{
			int nickIndex = input.indexOf("GRIEF ") + 6;
			
			String nick = input.substring(nickIndex).trim();
			
			if (input.indexOf(" ", nickIndex) > -1)
			{
				String chan = " #GTFO_" + nick + "_";
				
				String[] t = {"SAJOIN " + nick + chan};
				
				return(t);
			}
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
