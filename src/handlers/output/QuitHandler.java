package handlers.output;

import handlers.OutputHandler;
import core.Manager;
import data.ServerChannel;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class QuitHandler extends OutputHandler
{
	private static final String[] HOOKS = {"QUIT", "DISCONNECT"};

	public QuitHandler(Manager mgr)
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
			mgr.disconnect("user termination", dest.server);
		}
		else if (splitMsg.length == 2)
		{
			mgr.disconnect("user termination", dest.server);	// pass parts[1] to disconnect
		}
		else
		{
			mgr.println("(ERROR) Command was passed malformed arguments.", dest, TextColor.RED);
			mgr.printDebugMsg(this.getClass().getName() + " was passed illegal arguments. [" + dest + "] " + msg);
		}
	}
}
