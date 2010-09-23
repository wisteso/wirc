package handlers;

import core.Manager;
import data.ServerChannel;

/**
 *
 * @author Will
 */
public class DefaultOutputHandler extends OutputHandler
{
	private static final String[] HOOKS = null;

	public DefaultOutputHandler(Manager mgr)
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
		getManager().sendData(msg, dest.server);
	}
}
