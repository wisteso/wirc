package handlers.input;
import static structures.Constants.*;
import handlers.InputHandler;
import core.Facade;
import structures.ServerChannel;
import structures.ServerSource;
import gui.TextColor;
import java.util.Date;
import java.util.regex.Pattern;

/**
 *
 * @author Will
 */
public class PrivMsgHandler extends InputHandler
{
	private static final Pattern PM_REGEX = Pattern.compile("[Pp][Rr][Ii][Vv][Mm][Ss][Gg](\\s)+:(\\s)*\001.+\001(\\s)*");

	private static final String[] HOOKS = {"PRIVMSG"};

	public PrivMsgHandler(Facade mgr)
	{
		super(mgr);
	}

	@Override
	public String[] getHooks()
	{
		return HOOKS;
	}

	@Override
	public void process(String msg, ServerSource source)
	{
		String body = msg.substring(msg.indexOf(" :") + 2);

		if (msg.startsWith("\001") && msg.endsWith("\001"))
		{
			processCTCP(body, source);
		}
		else
		{
			String recip = msg.split("\\s")[1];
			
			processSTD(body, source, recip);
		}
	}

	public void processCTCP(String msg, ServerSource source)
	{
		Facade mgr = getManager();

		ServerChannel temp = new ServerChannel(source.server, source.nickname);
		
		StringBuilder reply = new StringBuilder();

		if (msg.indexOf("ACTION") == 0)
		{
			mgr.println("<" + source.nickname + msg.substring(msg.indexOf(" ")) + ">", temp, TextColor.VIOLET);
		}
		else if (msg.indexOf("PING") == 0)
		{
			mgr.println("<" + source.nickname + " has requested your ping>", temp, TextColor.VIOLET);
			reply.append("PING ");
			reply.append(msg.substring(msg.indexOf(" ") + 1));
			mgr.sendData("NOTICE " + source.nickname + " :\001" + reply + "\001", source.server);
		}
		else if (msg.indexOf("VERSION") == 0)
		{
			mgr.println("<" + source.nickname + " has requested your version>", temp, TextColor.VIOLET);
			reply.append("VERSION wIRC v0.2 <wisteso@gmail.com>");
			mgr.sendData("NOTICE " + source.nickname + " :\001" + reply + "\001", source.server);
		}
		else if (msg.indexOf("TIME") == 0)
		{
			mgr.println("<" + source.nickname + " has requested your local time>", temp, TextColor.VIOLET);

			reply.append("TIME :");
			reply.append(DATETIME.format(new Date()));

			mgr.sendData("NOTICE " + source.nickname + " :\001" + reply + "\001", source.server);
		}
		else if (msg.indexOf("USERINFO") == 0)
		{
			mgr.println("<" + source.nickname + " has requested your user info>", temp, TextColor.VIOLET);
			mgr.sendData("NOTICE " + source.nickname + " :\001" + reply + "\001", source.server);
		}
	}

	public void processSTD(String msg, ServerSource sender, String recip)
	{
		Facade mgr = getManager();

		if (recip.startsWith("#"))
		{
			ServerChannel servChan = new ServerChannel(sender.server, recip);

			mgr.println("<" + sender.nickname + "> ", servChan, TextColor.BLUE);

			String[] split = msg.split(mgr.profile.getName());

			mgr.print(split[0], servChan, TextColor.BLACK);

			for (int i = 1; i < split.length; ++i)
			{
				mgr.print(mgr.profile.getNick(), servChan, TextColor.BLACK_BOLD);
				mgr.print(split[i], servChan, TextColor.BLACK);
			}

//			int i = msg.indexOf(mgr.nickName);
//
//			if (i > -1)
//			{
//				int j = 0;
//				int nLen =  mgr.nickName.length();
//
//				while (i > -1)
//				{
//					mgr.print(msg.substring(j, i), recip, TextColor.BLACK);
//
//					j = i + nLen;
//
//					mgr.print(msg.substring(i, j), recip, TextColor.BLACK_BOLD);
//
//					i = msg.indexOf(mgr.nickName, j);
//				}
//
//				mgr.print(msg.substring(j), recip, TextColor.BLACK);
//			}
//			else
//			{
//				mgr.print(msg, recip, TextColor.BLACK);
//			}
		}
		else
		{
			ServerChannel respondTo = new ServerChannel(sender.server, sender.nickname);

			mgr.println("<" + sender.nickname + "> ", respondTo, TextColor.VIOLET);
			mgr.print(msg, respondTo, TextColor.BLACK);
		}
	}
}
