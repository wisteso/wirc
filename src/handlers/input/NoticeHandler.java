/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package handlers.input;

import handlers.InputHandler;
import core.Manager;
import data.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class NoticeHandler extends InputHandler
{
	private static final String[] HOOKS = {"NOTICE"};

	public NoticeHandler(Manager mgr)
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
			getManager().println("(NOTICE) " + msg.substring(sIndex + 2), TextColor.ORANGE);
		else
			getManager().printDebugMsg("Malformed notice message: " + msg);
	}
}
