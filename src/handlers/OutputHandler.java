package handlers;
import core.Facade;
import structures.ServerChannel;

/**
 *
 * @author Will
 */
public abstract class OutputHandler
{
	private final Facade mgr;

	public OutputHandler(Facade mgr)
	{
		this.mgr = mgr;
	}

	public Facade getManager()
	{
		return mgr;
	}

	public abstract String[] getHooks();

	public abstract void process(String msg, ServerChannel dest);
}
