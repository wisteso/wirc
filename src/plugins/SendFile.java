package plugins;

import wIRC.interfaces.Plugin;
import java.util.ArrayList;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

public class SendFile implements Plugin
{
	public static final String ID = "file transfer plug-in";
	public static final double VERSION = 0.1;
	private static final int cSize = 400;
	
	public String[] processInput(String input, String channel)
	{
		return null;
	}
	
	public String[] processOutput(String output, String channel)
	{
		if (output.startsWith("/send "))
		{
			ArrayList<String> temp = new ArrayList<String>();
			
			String path = output.substring(6).trim();
			
			BufferedInputStream in;
			
			try
			{
				if (path.startsWith("http://"))
					in = new BufferedInputStream(new URL(path).openConnection().getInputStream());
				else
					in = new BufferedInputStream(new FileInputStream(new File(path)));
				
				int a = in.available();
				
				byte b[] = new byte[0];
				
				int packs = (int)Math.ceil(a / 400.0);
				
				for (int i = 0; i < a; ++i)
				{
					if (i % cSize == 0)
					{
						int pack = (int)Math.ceil(i / 400.0);
						
						if (b.length > 0)
							temp.add("\001\000" + pack + "|" + packs + "\002" + new String(b) + "\000\001");
						
						if (a - i > 399)
							b = new byte[cSize];
						else
							b = new byte[a - i];
					}
					
					// FIXME: IRC doesn't like some characters < 32 and possibly in the upper range.
					b[i % cSize] = (byte)in.read();
				}
				
				temp.add("\001\000" + packs + "|" + packs + "\002" + new String(b) + "\000\001");
				
				temp.add("\002HALT\003");
			}
			catch (Exception e)
			{
				System.err.println("Error reading file " + path);
				return null;
			}
			
			return temp.toArray(new String[temp.size()]);
		}
		
		return null;
	}
	
	public String[] onLoad()
	{
		return null;
	}
	
	public String getVersion()
	{
		return ID + " v" + VERSION;
	}
}
