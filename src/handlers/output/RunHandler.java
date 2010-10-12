package handlers.output;
import static data.Constants.*;
import handlers.OutputHandler;
import core.Facade;
import data.ServerChannel;
import gui.TextColor;
import core.UserProfile;

/**
 *
 * @author Will
 */
public class RunHandler extends OutputHandler
{
	private static final String[] HOOKS = {"RUN"};

	public RunHandler(Facade mgr)
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

		Facade mgr = getManager();
		
		if (splitMsg.length == 2)
		{
			mgr.println("(SYSTEM) Executing script...", dest, TextColor.BLUE);

			String input = splitMsg[1];

			String scriptPath;

			if (input.startsWith("http://"))
				scriptPath = input;
			else
				scriptPath = UserProfile.PROFILE_PATH + SLASH + UserProfile.PROFILE_DIRS[1] + SLASH + input;

			if (!scriptPath.endsWith(".script"))
				scriptPath += ".script";

			String scriptName = null; // = mgr.executeScript(scriptPath);

			if (scriptName != null)
				mgr.println("(SYSTEM) " + scriptName + " finished.", dest, TextColor.BLUE);
			else
				mgr.println("(SYSTEM) Script failed to load or does not exist using path: " + scriptPath, dest, TextColor.BLUE);
		}
		else
		{
			mgr.println("(ERROR) Command was passed malformed arguments.", dest, TextColor.RED);
			mgr.printDebugMsg(this.getClass().getName() + " was passed illegal arguments. [" + dest + "] " + msg);
		}
	}
}
