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
public class PartHandler extends InputHandler
{
	private static final String[] HOOKS = {"PART"};

	public PartHandler(Facade mgr)
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
		ServerChannel channel = new ServerChannel(source.server, msg.split("\\s")[1]);

		if (source.nickname.equals(mgr.profile.getNick()))
		{
			ServerChannel sc = new ServerChannel(source.server, ServerChannel.CONSOLE.channel);
			mgr.println("<You have left " + channel + ">", sc, TextColor.BLUEGRAY);
		}
		else
		{
			int ind = msg.indexOf(" :");

			if (ind > -1)
				mgr.println("<" + source.nickname + " has left - " + msg.substring(ind + 2) + ">", channel, TextColor.BLUEGRAY);
			else
				mgr.println("<" + source.nickname + " has left>", channel, TextColor.BLUEGRAY);

			mgr.remName(channel, source.nickname);
		}
	}
}
