package core;
import java.io.BufferedInputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Plug-in loading library
 * <br><br>
 * This class is responsible for the translation 
 * of a file path (remote or local) to a usable 
 * Class of unknown type which may cast later on.
 * <br><br>
 * @author 	see http://code.google.com/p/wirc/wiki/AUTHORS
 */
public class RemoteClassLoader extends ClassLoader
{
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException
	{
		try
		{
			return findClass(new URL(name));
		}
		catch (MalformedURLException ex)
		{
			throw new ClassNotFoundException("Could not find class due to malformed URL: " + name, ex);
		}
	}

	public Class<?> findClass(URL path) throws ClassNotFoundException
	{
	    byte[] bytes = loadClassData(path);

		return defineClass(null, bytes, 0, bytes.length);
	}

	private byte[] loadClassData(URL path) throws ClassNotFoundException
	{
		try
		{
			BufferedInputStream in = new BufferedInputStream(path.openStream());

			byte[] bytesRead = new byte[in.available()];

			in.read(bytesRead);

			return bytesRead;
		}
		catch (Exception ex)
		{
			throw new ClassNotFoundException();
		}
    }
}