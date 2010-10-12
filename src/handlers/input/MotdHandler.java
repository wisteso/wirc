package handlers.input;
import static data.Constants.*;
import handlers.InputHandler;
import core.Facade;
import data.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class MotdHandler extends InputHandler
{
	private static final String[] HOOKS = {"372", "375", "376"};

	public MotdHandler(Facade mgr)
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
		String line = msg.substring(msg.indexOf(" :") + 2);

		getManager().println("(MOTD) " + line, CONSOLE, TextColor.GREEN);
	}

}
