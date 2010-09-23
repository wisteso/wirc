package core;
import gui.SwingGUI;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import static data.Constants.*;

/**
 *
 * @author Will
 */
public class UserProfile
{
	public File profilePath;

	public String nickName;
	public String realName;
	public String userInfo;
	public String hostName;

	public UserProfile()
	{
		// defaults
		nickName = "Nullname" + (int)(Math.random() * 8999 + 1000);
		realName = "Anonymous";
		userInfo = "No info set.";
		hostName = "chat.freenode.net";

		this.askAll();
	}

	/**
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean askAll()
	{
		if (askProfile()) // using file profile
		{
			return readProfile();
		}
		else // using temp profile or making a new one
		{
			if (askProfileDetails())
			{
				if (isPersistent())
				{
					writeProfile();
				}

				return true;
			}
			else // could not gather enough data for a temp profile
			{
				return false;
			 }
		}
	}

	public boolean isPersistent()
	{
		return profilePath != null;
	}

	/**
	 *
	 * @return true if the profilePath is write safe
	 */
	public boolean isProfileWritable()
	{
		try
		{
			return PROFILE_DIRS_EXIST && profilePath != null &&
				(profilePath.isFile() || profilePath.createNewFile());
		}
		catch (Exception ex)
		{
			return false;
		}
	}

	/**
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean askProfile()
	{
		if (PROFILE_DIRS_EXIST)
		{
			String answer = SwingGUI.askQuestion("Enter the new or existing profile to use:", "default");

			if (answer != null)
				profilePath = new File(PROFILE_PATH + SLASH + PROFILE_DIRS[0] + SLASH + answer.toLowerCase());
			else
				profilePath = PROFILE_PATH_DNE;

			return isProfileWritable();
		}

		return false;
	}

	/**
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean askProfileDetails()
	{
		return askNick(true) && askName(true) && askHost(true);
	}

	/**
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean askNick(boolean first)
	{
		String answer;

		if (first)
			answer = SwingGUI.askQuestion("Enter your nick-name:", nickName);
		else
			answer = SwingGUI.askQuestion("Invalid nick. Re-enter the nick-name:", nickName);

		if (answer != null)
		{
			nickName = answer;
		}

		return answer != null;
	}

	/**
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean askName(boolean first)
	{
		String answer;

		if (first)
			answer = SwingGUI.askQuestion("Enter your user-name:", realName);
		else
			answer = SwingGUI.askQuestion("Invalid user. Re-enter the user-name:", realName);

		if (answer != null)
		{
			realName = answer;
		}

		return answer != null;
	}

	/**
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean askHost(boolean first)
	{
		String answer;

		if (first)
			answer = SwingGUI.askQuestion("Enter the host-name:", hostName);
		else
			answer = SwingGUI.askQuestion("Invalid Host. Re-enter the host-name:", hostName);

		if (answer != null)
		{
			hostName = answer;
		}

		return answer != null;
	}

	/**
	 *
	 * @return true if successful, false otherwise
	 */
	public boolean readProfile()
	{
		try
		{
			Scanner in = new Scanner(profilePath);

			nickName = in.nextLine().trim();
			realName = in.nextLine().trim();
			hostName = in.nextLine().trim();

			in.close();

			return true;
		}
		catch (IOException ex)
		{
			return false;
		}
	}

	public boolean writeProfile()
	{
		if (!isProfileWritable())
			throw new IllegalStateException("Profile is not writable");

		try
		{
			BufferedOutputStream out =
				new BufferedOutputStream(new FileOutputStream(profilePath));

			out.write((nickName + "\n").getBytes());
			out.write((realName + "\n").getBytes());
			out.write((hostName + "\n").getBytes());

			out.close();

			return true;
		}
		catch (IOException ex)
		{
			System.err.println("Could not write profile.");

			return false;
		}
	}

	public static final String[] PROFILE_DIRS = {"profiles", "scripts", "plugins"};
	public static final File PROFILE_PATH = new File(System.getProperty("user.home") + File.separator + ".wIRC");
	public static final File PROFILE_PATH_DNE = null;
	public static boolean PROFILE_DIRS_EXIST;
	
	static
	{
		PROFILE_DIRS_EXIST = true;

		if (!PROFILE_PATH.isDirectory() && !PROFILE_PATH.mkdir())
			PROFILE_DIRS_EXIST = false;

		File tempFile;

		for (int i = 0; i < PROFILE_DIRS.length; ++i)
		{
			tempFile = new File(PROFILE_PATH + SLASH + PROFILE_DIRS[i]);

			if (!tempFile.isDirectory() && !tempFile.mkdir())
			{
				PROFILE_DIRS_EXIST = false;
			}
		}
	}
}
