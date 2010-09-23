package data;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author Will
 */
public class Constants
{
	public static final File RUN_PATH = new File(System.getProperty("user.dir"));

	public static final DateFormat DATETIME = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
	public static final DateFormat DATE = DateFormat.getDateInstance(DateFormat.SHORT);
	public static final DateFormat TIME = new SimpleDateFormat("HH:mm:ss");

	public static final String CHAR_NULL = String.valueOf(0);
	public static final String CHAR_CTCP = String.valueOf(1);
	public static final String SLASH = File.separator;
	public static final ServerChannel CONSOLE = new ServerChannel("localhost", "console");
	public static final ServerChannel DEBUG = new ServerChannel("localhost", "debug");
}
