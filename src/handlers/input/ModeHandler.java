package handlers.input;
import static data.Constants.*;
import handlers.InputHandler;
import core.Manager;
import data.ServerChannel;
import data.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class ModeHandler extends InputHandler
{
	private static final String[] HOOKS = {"MODE"};

	public ModeHandler(Manager mgr)
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
		// NEED EXAMPLES
		// :Wisteso MODE Wisteso :+i

		System.out.println("^^^ ADD THIS ^^^");

		Manager mgr = getManager();
		
		String modeMsg;
		ServerChannel channel;

		String src = (source.nickname != null) ? source.nickname : source.origin;

		if (msg.indexOf(":") > -1)
		{
			channel = CONSOLE;
			modeMsg = msg.substring(msg.indexOf(":") + 1).trim();
			//nickname = input.substring(0, input.indexOf(":")).trim();
		}
		else if (msg.indexOf("#") == 0)
		{
			channel = new ServerChannel(source.server, msg.substring(0, msg.indexOf(" ")).trim());
			modeMsg = msg.substring(msg.indexOf(" ") + 1).trim();
			//nickname = hostName;
		}
		else
		{
			channel = CONSOLE;
			modeMsg = msg.trim();
			//nickname = "UNKNOWN";

			mgr.printDebugMsg("Unsupported MODE arguments: " + msg);
		}

		if (!src.equals(mgr.profile.hostName))
		{
			mgr.println("<" + src + " is now " + modeMsg + ">", channel, TextColor.BLUEGRAY);
			//mgr.replaceNick(nick, msg);	// why was this added?
		}
		else
		{
			mgr.println("<" + channel + " is now " + modeMsg + ">", channel, TextColor.BLUEGRAY);
		}
	}
}
