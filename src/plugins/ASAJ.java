package plugins;
import wIRC.interfaces.Plugin;

/**
 * Anti SAJoin plugin
 * <br><br>
 * This plugin will automatically change your nick to 
 * a series of random characters of random length when 
 * you have been forced to join a channel via SAJOIN.
 * <br><br>
 * @author 	wisteso@gmail.com
 */
public class ASAJ implements Plugin
{
	public static final String ID = "anti-sajoin plug-in";
	public static final double VERSION = 1.0;
	
	public String processMessage(String input, String channel)
	{
		if (input.indexOf("*** You were forced to join") > -1)
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
