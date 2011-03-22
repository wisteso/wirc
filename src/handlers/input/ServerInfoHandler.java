package handlers.input;
import handlers.InputHandler;
import core.Facade;
import structures.ServerChannel;
import structures.ServerSource;
import gui.TextColor;

/**
 *
 * @author Will
 */
public class ServerInfoHandler extends InputHandler
{
	private static final String[] HOOKS = {"001", "002", "003", "004", "005", "006"};

	public ServerInfoHandler(Facade mgr)
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
		Integer cmd = Integer.parseInt(msg.substring(0, msg.indexOf(" ")));
		ServerChannel sc = new ServerChannel(source.server, ServerChannel.CONSOLE.channel);

		switch (cmd)
		{
			case 001: // RPL_WELCOME
			case 002: // RPL_YOURHOST
			case 003: // RPL_CREATED
				String line = msg.substring(msg.indexOf(" :"));
				getManager().println("(INFO) " + line, sc, TextColor.BLUEGRAY);
				break;
			case 004: // RPL_MYINFO
				getManager().println("(INFO) Recieving server information.", sc, TextColor.BLUEGRAY);
				break;
			case 005: // RPL_BOUNCE or server's supported modes???
				getManager().println("(INFO) Recieving server's supported modes.", sc, TextColor.BLUEGRAY);
				break;
			default:
				getManager().println("(INFO) Recieving server configuration.", sc, TextColor.BLUEGRAY);
				break;
		}
	}
}