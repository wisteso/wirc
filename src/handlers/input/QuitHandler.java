/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package handlers.input;

import handlers.InputHandler;
import core.Facade;
import structures.ServerSource;

/**
 *
 * @author Will
 */
public class QuitHandler extends InputHandler
{
	private static final String[] HOOKS = {"QUIT"};

	public QuitHandler(Facade mgr)
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
		getManager().remName(source.nickname);

		// broken until channels can be found out again

//		String quitMsg = msg.substring(msg.indexOf(" :") + 2);
//
//		if (msg.length() < 2)
//			window.println("<" + x.getNick() + " has quit>", chans[a], TextColor.BLUEGRAY);
//		else
//			window.println("<" + x.getNick() + " has quit - " + msg.toLowerCase() + ">", chans[a], TextColor.BLUEGRAY);
	}
}
