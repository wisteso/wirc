package wIRC;
import java.awt.Color;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

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
 * @author 	wisteso@gmail.com
 */
public final class C 
{
	// Protocol IRC message codes:
	
	public static final int MOTD_FOOTER = 376;
	public static final int MOTD_CONTENT = 372;
	public static final int MOTD_HEADER = 375;
	
	public static final int NAMELIST_FOOTER = 366;
	public static final int NAMELIST_CONTENT = 353;
	
	public static final int CHAN_TOPIC = 332;
	
	// wIRC message codes:
	
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
	
	protected static SimpleAttributeSet BASE = new SimpleAttributeSet();
	protected static SimpleAttributeSet BOLD = new SimpleAttributeSet();
	
	protected static SimpleAttributeSet BLACK = new SimpleAttributeSet();
	protected static SimpleAttributeSet GREY = new SimpleAttributeSet();
	protected static SimpleAttributeSet RED = new SimpleAttributeSet();
	protected static SimpleAttributeSet ORANGE = new SimpleAttributeSet();
	protected static SimpleAttributeSet GREEN = new SimpleAttributeSet();
	protected static SimpleAttributeSet BLUE = new SimpleAttributeSet();
	protected static SimpleAttributeSet BLUEGREY = new SimpleAttributeSet();
	protected static SimpleAttributeSet VIOLET = new SimpleAttributeSet();
	
	protected static SimpleAttributeSet BLUE_BOLD = new SimpleAttributeSet();
	
	// Misc constants:
	
	public static final String NULL_CHAR = String.valueOf(0);
	public static final String CTCP_CHAR = String.valueOf(1);
	
	public static void init()
	{
		StyleConstants.setFontFamily(BASE, "Monospace");
		StyleConstants.setFontSize(BASE, 11);
		StyleConstants.setBold(BOLD, true);
		
		BOLD.addAttributes(BASE);
		
		BLACK.addAttributes(BASE);
		GREY.addAttributes(BASE);
		RED.addAttributes(BASE);
		ORANGE.addAttributes(BASE);
		GREEN.addAttributes(BASE);
		BLUE.addAttributes(BASE);
		BLUEGREY.addAttributes(BASE);
		VIOLET.addAttributes(BASE);
		
		BLUE_BOLD.addAttributes(BOLD);
		
		StyleConstants.setForeground(BLACK,		Color.getHSBColor(new Float(0.000), new Float(0.000), new Float(0.000)));
		StyleConstants.setForeground(GREY,		Color.getHSBColor(new Float(0.000), new Float(0.000), new Float(0.666)));
		StyleConstants.setForeground(RED,		Color.getHSBColor(new Float(0.000), new Float(0.666), new Float(0.666)));
		StyleConstants.setForeground(ORANGE,	Color.getHSBColor(new Float(0.111), new Float(0.666), new Float(0.666)));
		StyleConstants.setForeground(GREEN,		Color.getHSBColor(new Float(0.333), new Float(0.666), new Float(0.666)));
		StyleConstants.setForeground(BLUE,		Color.getHSBColor(new Float(0.666), new Float(0.666), new Float(0.666)));
		StyleConstants.setForeground(BLUEGREY,	Color.getHSBColor(new Float(0.666), new Float(0.333), new Float(0.777)));
		StyleConstants.setForeground(VIOLET,	Color.getHSBColor(new Float(0.888), new Float(0.666), new Float(0.666)));
		
		StyleConstants.setForeground(BLUE_BOLD,	Color.getHSBColor(new Float(0.666), new Float(0.666), new Float(0.666)));
	}
}