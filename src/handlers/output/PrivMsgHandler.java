package handlers.output;

import handlers.OutputHandler;
import core.Manager;
import data.ServerChannel;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class PrivMsgHandler extends OutputHandler
{
	private static final String[] HOOKS = {"PRIVMSG", "MSG"};

	public PrivMsgHandler(Manager mgr)
	{
		super(mgr);
	}

	@Override
	public String[] getHooks()
	{
		return HOOKS;
	}

	@Override
	public void process(String msg, ServerChannel dest)
	{
		Manager mgr = getManager();
		
		mgr.sendMessage(msg, dest);

		mgr.println("<" + mgr.profile.nickName + "> ", dest, TextColor.BLUE_BOLD);
		mgr.print(msg.substring(msg.indexOf(" :") + 2), dest, TextColor.BLACK);
	}
}
