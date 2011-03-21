package handlers.input;
import handlers.InputHandler;
import core.Facade;
import data.Constants;
import data.ServerChannel;
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

		ServerChannel sc = new ServerChannel(source.server, ServerChannel.CONSOLE.channel);
		getManager().println("(MOTD) " + line, sc, TextColor.GREEN);
	}

}
