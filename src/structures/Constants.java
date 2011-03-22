package structures;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Will
 */
public class Constants
{
	public static final File RUN_PATH = new File(System.getProperty("user.dir"));

	public static final DateFormat DATETIME = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
	public static final DateFormat DATE = DateFormat.getDateInstance(DateFormat.SHORT);
	public static final DateFormat TIME = new SimpleDateFormat("HH:mm:ss");

	public static final String CHAR_NULL = String.valueOf(0);
	public static final String CHAR_CTCP = String.valueOf(1);
	public static final String SLASH = File.separator;
	
	public static class Codes
	{
		public static final int RPL_WELCOME = 1;
		public static final int RPY_YOURHOST = 2;
		public static final int RPL_CREATED = 3;
		public static final int RPL_MYINFO = 4;

		public static final int RPL_BOUNCE = 5;

		public static final int RPL_LUSERCLIENT = 251;
		public static final int RPL_USEROP = 252;
		public static final int RPL_LUSERCHANNELS = 254;
		public static final int RPL_LUSERME = 255;
		public static final int RPL_TRYAGAIN = 263;
		public static final int RPL_LOCALUSERS = 265;
		public static final int RPL_GLOBALUSERS = 266;
		public static final int CHAN_TOPIC = 332;
		public static final int RPL_TOPICWHOTIME = 333;

		public static final int NAMELIST_CONTENT = 353;
		public static final int NAMELIST_FOOTER = 366;

		public static final int MOTD_CONTENT = 372;
		public static final int MOTD_HEADER = 375;
		public static final int MOTD_FOOTER = 376;

		public static final int ERR_YOUREBANNEDCREEP = 465;
		public static final int ERR_YOUWILLBEBANNED = 466;
		public static final int ERR_KEYSET = 467;
		public static final int ERR_CHANNELISFULL = 471;
		public static final int ERR_UNKNOWNMODE = 472;
		public static final int ERR_INVITEONLYCHAN = 473;
		public static final int ERR_BANNEDFROMCHAN = 474;
		public static final int ERR_BADCHANNELKEY = 475;
		public static final int ERR_BADCHANMASK = 476;
		public static final int ERR_NOCHANMODES = 477;
		public static final int ERR_BANLISTFULL = 478;
		public static final int ERR_NOPRIVILEGES = 481;
		public static final int ERR_CHANOPRIVSNEEDED = 482;

		public static final int ERR_CANTKILLSERVER = 483;
		public static final int ERR_RESTRICTED = 484;
		public static final int ERR_UNIQOPPRIVSNEEDED = 485;
		public static final int ERR_NOOPERHOST = 491;
		public static final int ERR_UMODEUNKNOWNFLAG = 501;
		public static final int ERR_USERSDONTMATCH = 502;
	}
}
