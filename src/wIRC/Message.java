package wIRC;

/**
 * Message structural-object
 * <br><br>
 * This class takes in raw data for it's constructor 
 * value and creates an organized data-structure object.
 * <br><br>
 * @author 	see http://code.google.com/p/wirc/wiki/AUTHORS
 */
public class Message 
{
	public static enum Code
	{
		UNKNOWN,
		
		MESSAGE,
		NOTICE,
		PING,
		JOIN,
		PART,
		QUIT,
		MODE,
		NICK,
		CTCP_MSG,
		TOPIC,
		ERROR,
		DISCONNECT,
		
		RPL_WELCOME(001),
		RPL_YOURHOST(002),
		RPL_CREATED(003),
		RPL_MYINFO(004),
		RPL_ISUPPORT(005),
		RPL_LUSERCLIENT(251),
		RPL_LUSEROP(252),
		RPL_LUSERUNKNOWN(253),
		RPL_LUSERCHANNELS(254),
		RPL_LUSERME(255),
		RPL_LOCALUSERS(265),
		RPL_GLOBALUSERS(266),
		CHAN_TOPIC(332),
		NAMELIST_CONTENT(353),
		NAMELIST_FOOTER(366),
		MOTD_CONTENT(372),
		MOTD_HEADER(375),
		MOTD_FOOTER(376);
		
		public int ircCode;
		
		private Code()
		{
			ircCode = -1;
		}
		
		private Code(int signature)
		{
			ircCode = signature;
		}
		
		public static Code parseCode(int signature)
		{
			switch (signature)
			{
				case 001: return RPL_WELCOME;
				case 002: return RPL_YOURHOST;
				case 003: return RPL_CREATED;
				case 004: return RPL_MYINFO;
				case 005: return RPL_ISUPPORT;
				case 251: return RPL_LUSERCLIENT;
				case 252: return RPL_LUSEROP;
				case 254: return RPL_LUSERCHANNELS;
				case 255: return RPL_LUSERME;
				case 265: return RPL_LOCALUSERS;
				case 266: return RPL_GLOBALUSERS;
				case 376: return MOTD_FOOTER; 
				case 372: return MOTD_CONTENT;
				case 375: return MOTD_HEADER;
				case 366: return NAMELIST_FOOTER;
				case 353: return NAMELIST_CONTENT;
				case 332: return CHAN_TOPIC;
			}
			
			Code unknown = UNKNOWN;
			
			unknown.ircCode = signature;
			
			return unknown;
		}
		
		/*
		public static Code parseCode(String signature)
		{
			return NULL;
		}
		*/
	}
	
	// Color constants:
	
	public static enum TextColor
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
	
	private String sender = new String();
	private String nickname = new String();
	private String command = new String();
	private String target = new String();
	private String message = new String();
	private String channel = new String("Console");
	private Code code = Code.UNKNOWN;
	
	public Message(String rawData, Manager m)
	{
		String rawMsg = new String();
		
		if (rawData.charAt(0) == ':')
		{	
			if (rawData.indexOf(" ") > -1)
			{
				sender = rawData.substring(1, rawData.indexOf(" "));
				rawMsg = rawData.substring(rawData.indexOf(" ") + 1);
				
				if (sender.indexOf("!") > -1)
				{
					nickname = sender.substring(0, sender.indexOf("!"));
				}
			}
			else
				m.printDebugMsg("Unhandled header: " + rawData);
		}
		else
		{
			sender = "SERVER";
			rawMsg = rawData;
		}
		
		//TODO - Fix extraction of message code so not always calling exceptions.
		
		if (rawMsg.indexOf(" ") > -1)
		{
			command = rawMsg.substring(0, rawMsg.indexOf(" "));
			rawMsg = rawMsg.substring(rawMsg.indexOf(" ") + 1);
			
			try
			{
				code = Code.parseCode(Integer.parseInt(command));
			}
			catch (NumberFormatException e)
			{
				code = Code.UNKNOWN;
			}
		}
		
		if (code.ircCode > 0)  // Numeric command.
		{
			int i;
			
			if ((i = rawMsg.indexOf(" :")) > -1)
			{
				target = rawMsg.substring(0, i);
				message = rawMsg.substring(i + 2);
			}
			else if ((i = rawMsg.indexOf(" ")) > -1)
			{
				target = rawMsg.substring(0, i);
				message = rawMsg.substring(i + 1);
			}
			else
			{
				target = "NULL";
				message = rawMsg;
			}
			
			if (code == Code.CHAN_TOPIC || code.ircCode == 333 || 
					code == Code.NAMELIST_CONTENT || code == Code.NAMELIST_FOOTER)
			{
				int x = rawMsg.indexOf("#");
				int y = rawMsg.indexOf(" ", x);
				
				if (x > -1 && y > -1)
					channel = rawMsg.substring(x, y);
			}
		}
		else  // Textual command.
		{	
			if (command.indexOf("PRIVMSG") == 0)
			{
				if (rawMsg.charAt(rawMsg.indexOf(":") + 1) == 0x1)  // CTCP type message.
				{
					if (rawMsg.charAt(rawMsg.length() - 1) == 0x1)
					{
						code = Code.CTCP_MSG;
						message = rawMsg.substring(rawMsg.indexOf(":") + 2, rawMsg.length() - 1);
						channel = rawMsg.substring(0, rawMsg.indexOf(":") - 1);
						
						if (channel.equalsIgnoreCase(m.nickName))
							channel = sender.substring(0, sender.indexOf("!"));
					}
				}
				else  // Normal message.
				{
					code = Code.MESSAGE;
					message = rawMsg.substring(rawMsg.indexOf(":") + 1);
					channel = rawMsg.substring(0, rawMsg.indexOf(":") - 1);
					
					if (channel.equalsIgnoreCase(m.nickName))
						channel = sender.substring(0, sender.indexOf("!"));
				}
			}
			else if (command.indexOf("NOTICE") == 0)
			{
				code = Code.NOTICE;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1);
			}
			else if (command.indexOf("PING") == 0)
			{
				code = Code.PING;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1);
			}
			else if (command.indexOf("JOIN") == 0)
			{
				code = Code.JOIN;
				channel = rawMsg.substring(rawMsg.indexOf("#"));
			}
			else if (command.indexOf("PART") == 0)
			{
				code = Code.PART;
				
				int cIndex = rawData.indexOf(" PART ") + 1;
				int mIndex = rawData.indexOf(":", cIndex + 5);
				
				if (mIndex > -1)
					message = rawData.substring(mIndex + 1).trim();
				
				if (rawData.charAt(cIndex + 5) == '#')
				{
					channel = rawData.substring(cIndex + 5);
					
					int sIndex = channel.indexOf(' ');
					
					if (sIndex > -1)
						channel = channel.substring(0, sIndex);
				}
				else
					channel = "Console";
			}
			else if (command.indexOf("QUIT") == 0)
			{
				code = Code.QUIT;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1).trim();
			}
			else if (command.indexOf("MODE") == 0)
			{
				code = Code.MODE;
				
				if (rawMsg.indexOf(":") > -1)
				{
					message = rawMsg.substring(rawMsg.indexOf(":") + 1).trim();
					nickname = rawMsg.substring(0, rawMsg.indexOf(":")).trim();
				}
				else if (rawMsg.indexOf("#") == 0)
				{
					message = rawMsg.substring(rawMsg.indexOf(" ") + 1).trim();
					nickname = m.hostName;
					channel = rawMsg.substring(0, rawMsg.indexOf(" ")).trim();
				}
				else
				{
					message = rawMsg.trim();
					nickname = "UNKNOWN";
					m.printDebugMsg("Bad mode: " + rawData);
				}
			}
			else if (command.indexOf("NICK") == 0)
			{
				code = Code.NICK;
				
				if (rawMsg.indexOf(":") > -1)
					message = rawMsg.substring(rawMsg.indexOf(":") + 1);
				else
					message = rawMsg;
			}
			else if (command.indexOf("TOPIC") == 0)
			{
				code = Code.TOPIC;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1);
			}
			else if (command.indexOf("ERROR") == 0)
			{
				code = Code.ERROR;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1);
			}
			else
			{
				m.printDebugMsg("Unhandled textual header: " + command);
				message = rawMsg;
			}
		}
	}
	
	public String getSender()
	{
		return sender;
	}
	
	public String getNick()
	{
		return nickname;
	}
	
	public Code getCode()
	{
		return code;
	}
	
	public String getTarget()
	{
		return target;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public String getChannel()
	{
		return channel;
	}
}