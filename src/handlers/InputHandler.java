package handlers;
import core.Facade;
import structures.ServerSource;

/**
 *
 * @author Will
 */
public abstract class InputHandler
{
	private final Facade mgr;

	public InputHandler(Facade mgr)
	{
		this.mgr = mgr;
	}

	public Facade getManager()
	{
		return mgr;
	}

	public abstract String[] getHooks();

	public abstract void process(String msg, ServerSource source);
}
