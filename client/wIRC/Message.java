package wIRC;

/**
 * <b>Message structural-object</b>
 * <br><br>
 * This class takes in raw data for it's constructor 
 * value and creates an organized data-structure object.
 * 
 * @author wisteso@gmail.com
 */
public class Message 
{
	private String sender = new String();
	private String nickname = new String();
	private String command = new String();
	private String target = new String();
	private String message = new String();
	private String channel = new String("Console");
	private Integer code = C.NULL;
	
	public Message(String rawData)
	{
		String rawMsg = new String();
		
		if (rawData.charAt(0) == 0x3A)  // We have a sender.
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
				System.err.println("Unhandled header: " + rawData);
		}
		else  // No sender data; assume server.
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
				code = Integer.parseInt(command);
			}
			catch (NumberFormatException e)
			{
				code = C.NULL;
			}
		}
		
		if (code > 0)  // Numeric command.
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
			
			if (code == 332 || code == 333 || code == 353 || code == 366)
			{
				channel = rawMsg.substring(rawMsg.indexOf("#"), rawMsg.indexOf(" ", rawMsg.indexOf("#")));
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
						code = C.CTCP_MSG;
						message = rawMsg.substring(rawMsg.indexOf(":") + 2, rawMsg.length() - 1);
						channel = rawMsg.substring(0, rawMsg.indexOf(":") - 1);
						
						if (channel.equalsIgnoreCase(Main.nickName))
							channel = sender.substring(0, sender.indexOf("!"));
					}
				}
				else  // Normal message.
				{
					code = C.MESSAGE;
					message = rawMsg.substring(rawMsg.indexOf(":") + 1);
					channel = rawMsg.substring(0, rawMsg.indexOf(":") - 1);
					
					if (channel.equalsIgnoreCase(Main.nickName))
						channel = sender.substring(0, sender.indexOf("!"));
				}
			}
			else if (command.indexOf("NOTICE") == 0)
			{
				code = C.NOTICE;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1);
			}
			else if (command.indexOf("PING") == 0)
			{
				code = C.PING;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1);
			}
			else if (command.indexOf("JOIN") == 0)
			{
				code = C.JOIN;
				channel = rawMsg.substring(rawMsg.indexOf("#"));
			}
			else if (command.indexOf("PART") == 0)
			{
				code = C.PART;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1).trim();
				
				if (rawMsg.indexOf("#") > -1 && rawMsg.indexOf(":") > -1)  // Apparently this is optional...
					channel = rawMsg.substring(rawMsg.indexOf("#"), rawMsg.indexOf(":") - 1);
			}
			else if (command.indexOf("QUIT") == 0)
			{
				code = C.QUIT;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1).trim();
			}
			else if (command.indexOf("MODE") == 0)
			{
				code = C.MODE;
				
				if (rawMsg.indexOf(":") > -1)
				{
					message = rawMsg.substring(rawMsg.indexOf(":") + 1).trim();
					nickname = rawMsg.substring(0, rawMsg.indexOf(":")).trim();
				}
				else if (rawMsg.indexOf("#") == 0)
				{
					message = rawMsg.substring(rawMsg.indexOf(" ") + 1).trim();
					nickname = Main.hostName;
					channel = rawMsg.substring(0, rawMsg.indexOf(" ")).trim();
				}
				else
				{
					message = rawMsg.trim();
					nickname = "UNKNOWN";
					System.out.println("Bad mode: " + rawData);
				}
			}
			else if (command.indexOf("NICK") == 0)
			{
				code = C.NICK;
				
				if (rawMsg.indexOf(":") > -1)
					message = rawMsg.substring(rawMsg.indexOf(":") + 1);
				else
					message = rawMsg;
			}
			else if (command.indexOf("TOPIC") == 0)
			{
				code = C.TOPIC;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1);
			}
			else if (command.indexOf("ERROR") == 0)
			{
				code = C.ERROR;
				message = rawMsg.substring(rawMsg.indexOf(":") + 1);
			}
			else
			{
				System.err.println("Unhandled textual header: " + command);
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
	
	public int getCode()
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
