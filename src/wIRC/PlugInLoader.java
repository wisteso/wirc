package wIRC;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Plug-in loading library
 * <br><br>
 * This class is responsible for the translation 
 * of a file path (remote or local) to a useable 
 * Class of unknown type which may cast later on.
 * <br><br>
 * @author 	see AUTHORS.TXT
 */
public class PlugInLoader extends ClassLoader
{
	private Manager m;
	
	public PlugInLoader(Manager m)
	{
		this.m = m;
	}
	
	public Class<?> findClass(String name)
	{
	    byte[] b = loadClassData(name);
	    
	    if (b != null)
	    {
	    	try
	    	{
	    		Class<?> x = defineClass(null, b, 0, b.length);
	    		
	    		if (x != null)
			    	return x;
	    	}
	    	catch (Exception e)
	    	{
	    		m.printDebugMsg(e.toString());
	    	}
	    }
	    
	    return null;
	}
	
	private byte[] loadClassData(String path)
	{
		try
		{
			BufferedInputStream in;
			
			if (path.indexOf("http://") == 0)
			{
				in = new BufferedInputStream(new URL(path).openConnection().getInputStream());
			}
			else
			{
				File f = new File(path);
				
				in = new BufferedInputStream(new FileInputStream(f));
			}
			
			ArrayList<Byte> out = new ArrayList<Byte>();
			
			int a = in.available();
			
			for (int i = 0; i < a; ++i)
				out.add((byte)in.read());
			
			byte[] data = new byte[out.size()];
			
			for (int i = 0; i < out.size(); ++i)
				data[i] = out.get(i);
			
			return data;
		}
		catch (java.io.FileNotFoundException e)
		{
			m.printDebugMsg(e.getMessage());
		}
		catch (Exception e)
		{
			m.printDebugMsg(e.toString());
		}
		
		return null;
    }
}