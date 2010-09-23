package handlers;
import core.Manager;
import data.ServerSource;

/**
 *
 * @author Will
 */
public abstract class InputHandler
{
	private final Manager mgr;

	public InputHandler(Manager mgr)
	{
		this.mgr = mgr;
	}

	public Manager getManager()
	{
		return mgr;
	}

	public abstract String[] getHooks();

	public abstract void process(String msg, ServerSource source);
}
