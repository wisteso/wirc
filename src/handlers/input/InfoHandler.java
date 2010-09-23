package handlers.input;
import handlers.InputHandler;
import core.Manager;
import data.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class InfoHandler extends InputHandler
{
	private static final String[] HOOKS = {"250", "251", "252", "253", "254", "255", "265", "266"};

	public InfoHandler(Manager mgr)
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
		String[] splitMsg = msg.split("\\s");

		String line = msg.substring(msg.indexOf(" :") + 2);
		Integer cmd = Integer.parseInt(splitMsg[0]);

		switch (cmd)
		{
			case 252:
			case 253:
			case 254:
				getManager().println("(INFO) " + splitMsg[2] + " " + line, TextColor.BLUEGRAY);
				break;
			default:
				getManager().println("(INFO) " + line, TextColor.BLUEGRAY);
				break;
		}
	}
}
