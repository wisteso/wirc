package handlers.input;
import handlers.InputHandler;
import core.Facade;
import data.ServerChannel;
import data.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class JoinHandler extends InputHandler
{
	private static final String[] HOOKS = {"JOIN"};

	public JoinHandler(Facade mgr)
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
		Facade mgr = getManager();
		// below code supports formats seen:
		// :Wisteso!Wisteso@CPE-70-92-227-228.wi.res.rr.com JOIN #chan
		// :Wisteso!~Wisteso@CPE-70-92-227-228.wi.res.rr.com JOIN :#chan
		int index = msg.indexOf(" :");
		index = (index < 0) ? msg.indexOf(" ") + 1 : index + 2;

		String[] channels = msg.substring(index).split("\\s");
		ServerChannel temp;

		for (String channel : channels)
		{
			temp = new ServerChannel(source.server, channel);

			mgr.println("<" + source.nickname + " has joined>", temp, TextColor.BLUEGRAY);

			if (source.nickname.equalsIgnoreCase(mgr.profile.getNick()))
			{
				mgr.focusChat(temp);
			}
			else
			{
				mgr.addNames(temp, source.nickname);
			}
		}
	}
}
