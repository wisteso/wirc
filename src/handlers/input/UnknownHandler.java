package handlers.input;
import handlers.InputHandler;
import core.Manager;
import data.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class UnknownHandler extends InputHandler
{
	private static final String[] HOOKS = {"421"};

	public UnknownHandler(Manager mgr)
	{
		super(mgr);
	}

	@Override
	public String[] getHooks()
	{
		return HOOKS;
	}

	@Override
	public void process(String msg, ServerSource source)
	{
		// :leguin.freenode.net 421 Wisteso auth :Unknown command
		Manager mgr = getManager();

		mgr.print("(ERROR) Server did not recognize the command: " + msg.split("\\s")[2], mgr.getFocusedChat(), TextColor.ORANGE);
	}
}
