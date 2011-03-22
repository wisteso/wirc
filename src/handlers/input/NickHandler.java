package handlers.input;
import static structures.Constants.*;
import handlers.InputHandler;
import core.Facade;
import structures.ServerChannel;
import structures.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class NickHandler extends InputHandler
{
	private static final String[] HOOKS = {"NICK"};

	public NickHandler(Facade mgr)
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
		
		String nickMsg;

		if (msg.indexOf(":") > -1)
			nickMsg = msg.substring(msg.indexOf(":") + 1);
		else
			nickMsg = "";

		// TODO: Show this notification on the right channels.
		if (source.nickname.equals(mgr.profile.getNick()) == true)
		{
			mgr.profile.setNick(nickMsg);
			mgr.replaceNick(source.nickname, nickMsg);
			mgr.println("<You are now known as " + nickMsg + ">", mgr.getFocusedChat(), TextColor.BLUE);

			mgr.updateProfile();
		}
		else
		{
			ServerChannel[] channels = mgr.replaceNick(source.nickname, nickMsg);
			
			for (ServerChannel chan : channels)
				mgr.println("<" + source.nickname + " is now known as " + nickMsg + ">", chan, TextColor.BLUEGRAY);
		}
	}
}
