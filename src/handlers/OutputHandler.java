package handlers;
import core.Manager;
import data.ServerChannel;

/**
 *
 * @author Will
 */
public abstract class OutputHandler
{
	private final Manager mgr;

	public OutputHandler(Manager mgr)
	{
		this.mgr = mgr;
	}

	public Manager getManager()
	{
		return mgr;
	}

	public abstract String[] getHooks();

	public abstract void process(String msg, ServerChannel dest);
}
