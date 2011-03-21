package handlers;

import core.Facade;
import data.Constants;
import data.ServerChannel;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class DefaultOutputHandler extends OutputHandler
{
	private static final String[] HOOKS = null;

	public DefaultOutputHandler(Facade mgr)
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
		if (dest.equals(ServerChannel.CONSOLE) || dest.equals(ServerChannel.DEBUG))
		{
			getManager().println("(ERROR) Cannot do that on a virtual channel.", dest, TextColor.RED);
		}
		else
		{
			getManager().printDebugMsg("Sending unhandled command: " + msg);
			getManager().sendData(msg, dest.server);
		}
	}
}
