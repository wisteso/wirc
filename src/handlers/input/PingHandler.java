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
public class PingHandler extends InputHandler
{
	private static final String[] HOOKS = {"PING"};

	public PingHandler(Facade mgr)
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
		String host = msg.split("\\s")[1].replaceFirst(":", "");

		mgr.sendData("PONG " + host, source.server);
		mgr.println("(PING) " + host, TextColor.GREEN);
	}
}
