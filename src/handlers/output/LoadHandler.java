package handlers.output;
import static data.Constants.*;
import handlers.OutputHandler;
import core.Manager;
import data.ServerChannel;
import gui.TextColor;
import core.UserProfile;

/**
 *
 * @author Will
 */
public class LoadHandler extends OutputHandler
{
	private static final String[] HOOKS = {"LOAD"};

	public LoadHandler(Manager mgr)
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
		String[] splitMsg = msg.split("\\s");

		Manager mgr = getManager();
		
		if (splitMsg.length == 2)
		{
			mgr.println("(SYSTEM) Loading plugin...", dest, TextColor.BLUE);

			String input = splitMsg[1];

			String pluginPath;

			if (input.startsWith("http://"))
				pluginPath = input;
			else
				pluginPath = UserProfile.PROFILE_PATH + SLASH + "plugins" + SLASH + input;

			if (!pluginPath.endsWith(".class"))
				pluginPath += ".class";

			String pluginName = null; // = mgr.loadPlugin(pluginPath);

			if (pluginName != null)
				mgr.println("(SYSTEM) " + pluginName + " loaded.", dest, TextColor.BLUE);
			else
				mgr.println("(SYSTEM) Plug-in failed to load or does not exist using path: " + pluginPath, dest, TextColor.BLUE);
		}
		else
		{
			mgr.println("(ERROR) Command was passed malformed arguments.", dest, TextColor.RED);
			mgr.printDebugMsg(this.getClass().getName() + " was passed illegal arguments. [" + dest + "] " + msg);
		}
	}
}
