package handlers.input;
import handlers.InputHandler;
import core.Facade;
import structures.Constants;
import structures.ServerChannel;
import structures.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class NoticeHandler extends InputHandler
{
	private static final String[] HOOKS = {"NOTICE"};

	public NoticeHandler(Facade mgr)
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
		int sIndex = msg.indexOf(" :");

		if (sIndex > -1)
		{
			ServerChannel sc = new ServerChannel(source.server, ServerChannel.CONSOLE.channel);
			getManager().println("(NOTICE) " + msg.substring(sIndex + 2), sc, TextColor.ORANGE);
		}
		else
		{
			getManager().printDebugMsg("Malformed notice message: " + msg);
		}
	}
}
