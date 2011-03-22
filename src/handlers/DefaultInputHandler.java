package handlers;

import core.Facade;
import structures.ServerSource;

/**
 *
 * @author Will
 */
public class DefaultInputHandler extends InputHandler
{
	public DefaultInputHandler(Facade mgr)
	{
		super(mgr);
	}

	@Override
	public String[] getHooks()
	{
		return null;
	}

	@Override
	public void process(String msg, ServerSource source)
	{
		if (source == null)
			getManager().printDebugMsg("(UNHANDLED) " + msg);
		else
			getManager().printDebugMsg("(UNHANDLED) [" + source + "] " + msg);
	}
}
