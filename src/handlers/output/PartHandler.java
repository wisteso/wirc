package handlers.output;
import static data.Constants.*;
import handlers.OutputHandler;
import core.Facade;
import data.ServerChannel;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class PartHandler extends OutputHandler
{
	private static final String[] HOOKS = {"PART"};

	public PartHandler(Facade mgr)
	{
		super(mgr);
	}

	@Override
	public String[] getHooks()
	{
		return HOOKS;
	}

	@Override
	public void process(String msg, ServerChannel dest)
	{
		String[] splitMsg = msg.split("\\s");

		Facade mgr = getManager();

		if (splitMsg.length == 2)
		{
			sendChatPart(new ServerChannel(dest.server, splitMsg[1]));
		}
		else if (splitMsg.length == 1)
		{
			if (dest.equals(CONSOLE))
				mgr.println("(ERROR) Cannot leave the console.", dest, TextColor.RED);
			else
				sendChatPart(dest);
		}
		else
		{
			mgr.println("(ERROR) Command was passed malformed arguments.", dest, TextColor.RED);
			mgr.printDebugMsg(this.getClass().getName() + " was passed illegal arguments. [" + dest + "] " + msg);
		}
	}

	public void sendChatPart(ServerChannel chan)
	{
		Facade mgr = getManager();
		
		if (mgr.remChat(chan))
		{
			if (chan.channel.charAt(0) == '#')
				mgr.sendData("PART " + chan.channel, chan.server);
		}
		else
		{
			mgr.println("(ERROR) You are not in the channel: " + chan.channel, chan, TextColor.RED);
		}
	}
}
