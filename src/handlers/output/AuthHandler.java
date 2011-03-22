package handlers.output;

import handlers.OutputHandler;
import core.Facade;
import structures.ServerChannel;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class AuthHandler extends OutputHandler
{
	private static final String[] HOOKS = {"AUTH"};

	public AuthHandler(Facade mgr)
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
		Facade mgr = getManager();

		String[] splitMsg = msg.split("\\s");
		
		if (splitMsg.length == 2)
		{
			mgr.sendData("PRIVMSG NICKSERV :IDENTIFY " + splitMsg[1], dest.server);
		}
		else
		{
			mgr.println("(ERROR) Command was passed malformed arguments.", dest, TextColor.RED);
			mgr.printDebugMsg(this.getClass().getName() + " was passed illegal arguments. [" + dest + "] " + msg);
		}
	}
}

