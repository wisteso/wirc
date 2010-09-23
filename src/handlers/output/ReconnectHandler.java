package handlers.output;

import handlers.OutputHandler;
import core.Manager;
import data.ServerChannel;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class ReconnectHandler extends OutputHandler
{
	private static final String[] HOOKS = {"RECONNECT"};

	public ReconnectHandler(Manager mgr)
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

		Manager mgr = getManager();
		
		if (splitMsg.length == 1)
		{
			mgr.println("\n(SYSTEM) Reconnecting...", dest, TextColor.ORANGE);
			mgr.disconnect("reconnecting", dest.server);
		}
		else
		{
			mgr.println("(ERROR) Command was passed malformed arguments.", dest, TextColor.RED);
			mgr.printDebugMsg(this.getClass().getName() + " was passed illegal arguments. [" + dest + "] " + msg);
		}
	}
}

