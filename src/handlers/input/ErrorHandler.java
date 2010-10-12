/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package handlers.input;

import handlers.InputHandler;
import core.Facade;
import data.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class ErrorHandler extends InputHandler
{
	private static final String[] HOOKS = {"ERROR"};

	public ErrorHandler(Facade mgr)
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
		String errorMsg = msg.substring(msg.indexOf(" :") + 2);

		if (msg.indexOf("Closing Link") > -1)
			getManager().disconnect(false, source.server);
		else
			getManager().println("(ERROR) " + errorMsg, TextColor.RED);
	}
}