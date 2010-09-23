package plugins;

/**
 * Anti SAJoin plugin
 * <br><br>
 * This plugin will automatically change your nick to 
 * a series of random characters of random length when 
 * you have been forced to join a channel via SAJOIN.
 * <br><br>
 * @author 	see http://code.google.com/p/wirc/wiki/AUTHORS
 */
public class ASAJ implements Plugin
{
	public static final String ID = "anti-sajoin plug-in";
	public static final double VERSION = 1.0;
	
	public String[] processInput(String input, String channel)
	{
		if (input.indexOf("*** You were forced to join") > -1)
		{
			String newNick = new String();
			
			int charCount = 6 + (int)(Math.random() * 4.99);
			
			for (int i = 0; i < charCount; ++i)
			{
				newNick += (char)(97 + (Math.random() * 25.99));
			}
			
			String[] t = {"NICK " + newNick};
			
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
