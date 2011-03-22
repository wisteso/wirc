package handlers.output;

import handlers.OutputHandler;
import core.Facade;
import structures.ServerChannel;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class QuitHandler extends OutputHandler
{
	private static final String[] HOOKS = {"QUIT", "DISCONNECT"};

	public QuitHandler(Facade mgr)
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
		
		if (splitMsg.length == 1)
		{
			mgr.disconnect(true, dest.server);
		}
		else if (splitMsg.length == 2)
		{
			mgr.disconnect(true, dest.server);	// pass parts[1] to disconnect
		}
		else
		{
			mgr.println("(ERROR) Command was passed malformed arguments.", dest, TextColor.RED);
			mgr.printDebugMsg(this.getClass().getName() + " was passed illegal arguments. [" + dest + "] " + msg);
		}
	}
}
