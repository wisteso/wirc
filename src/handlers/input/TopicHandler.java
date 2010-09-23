package handlers.input;

import handlers.InputHandler;
import core.Manager;
import data.ServerChannel;
import data.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class TopicHandler extends InputHandler
{
	private static final String[] HOOKS = {"TOPIC", "332"};

	public TopicHandler(Manager mgr)
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
		// Supported formats seen:
		// :niven.freenode.net 332 Wisteso #freenode :Welcome to #freenode
		// :Amsterdam.NL.AfterNET.Org 332 Wisteso #gamedev :Official channel of www.gamedev.net
		
		int chanStartIndex = msg.indexOf("#");
		int chanEndIndex = msg.indexOf(" ", chanStartIndex + 2);
		int bodyIndex = msg.indexOf(" :") + 2;

		String topicMsg = msg.substring(bodyIndex);
		
		if (chanEndIndex > bodyIndex)
			throw new RuntimeException("Malformed topic message");

		ServerChannel channel = new ServerChannel(source.server, msg.substring(chanStartIndex, chanEndIndex));

		getManager().println("(TOPIC) " + topicMsg, channel, TextColor.GREEN);
	}
}
