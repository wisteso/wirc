package wIRC;
import java.io.File;
import java.io.*;
import java.util.ArrayList;

public class PlugInLoader extends ClassLoader
{
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
	    	catch (java.lang.ClassFormatError e)
	    	{
	    		return null;
	    	}
	    }
	    
	    return null;
	}
	
	private byte[] loadClassData(String path)
	{
	    if (path.indexOf("http://") == 0)
        {
        	return null;
        }
        else
        {
        	File f = new File(path);
        	
        	if (!f.canRead()) return null;
        	
        	try
        	{
        		BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
    			ArrayList<Byte> out = new ArrayList<Byte>();
    			
    			byte b = (byte)in.read();

    			while (b != -1)
    			{
    				out.add(b);
    				b = (byte)in.read();
    			}
    			
    			byte[] data = new byte[out.size()];
        		
        		for (int i = 0; i < out.size(); ++i)
        			data[i] = out.get(i);
        		
        		return data;
        	}
        	catch (Exception e)
        	{
        		System.err.println("Error loading class data for " + path);
        		return null;
        	}
        }
    }
}

