package wIRC;
import java.io.File;

/**
 * Global constants
 * <br><br>
 * Rather than use a memory intensive object to store 
 * our constant values, we'll just use nice low-memory primitives with 
 * descriptive variable names.
 * <br><br>
 * "Protocol IRC" message codes use exact values required by the IRC 
 * specification while wIRC message codes have values based on our 
 * usage of them. However, both cases relate to real commands used by 
 * the IRC specification.
 * <br><br>
 * @author 	see http://code.google.com/p/wirc/wiki/AUTHORS
 */
public final class C 
{
	//	Protocol IRC message codes:
	
	public static final int MOTD_FOOTER = 376;
	public static final int MOTD_CONTENT = 372;
	public static final int MOTD_HEADER = 375;
	
	public static final int NAMELIST_FOOTER = 366;
	public static final int NAMELIST_CONTENT = 353;
	
	public static final int CHAN_TOPIC = 332;
	
	//	wIRC message codes:
	
	public static final int NULL = 0;
	
	public static final int MESSAGE = -1;
	public static final int NOTICE = -2;
	public static final int PING = -3;
	public static final int JOIN = -4;
	public static final int PART = -5;
	public static final int QUIT = -6;
	public static final int MODE = -7;
	public static final int NICK = -8;
	public static final int CTCP_MSG = -9;
	public static final int TOPIC = -10;
	
	public static final int ERROR = -100;
	public static final int DISCONNECT = -101;
	
	// Color constants:
	
	public static enum COLOR
	{
		BLACK_BOLD,
		BLACK,
		GRAY,
		RED,
		ORANGE,
		YELLOW,
		GREEN,
		BLUE,
		BLUE_BOLD,
		BLUEGRAY,
		VIOLET;
	}
	
	// Misc constants:
	
	public static final String NULL_CHAR = String.valueOf(0);
	public static final String CTCP_CHAR = String.valueOf(1);
	public static final String PSLASH = File.separator;
}