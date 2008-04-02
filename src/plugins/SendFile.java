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
				
				int count = in.available();
				
				int packs = (int)Math.ceil(count / cSize);
				
				byte b[] = new byte[0];
				
				for (int index = 0; index < count; index += 2)
				{
					if (index % cSize == 0)
					{
						int pack = (int)Math.ceil(1.0 * index / cSize);
						
						if (b.length > 0)
							temp.add("\001\000" + pack + "|" + packs + "\002" + new String(b) + "\000\001");
						
						if (count - index > cSize - 1)
							b = new byte[cSize];
						else
							b = new byte[count - index];
					}
					
					byte[] byt = encode(in.read());
					
					b[index % cSize] = byt[0];
					b[index % cSize + 1] = byt[1];
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
	
	private static byte[] encode(int b)
	{	
		byte[] hexBytes = {0, 0};
		
		hexBytes[0] = byte2Hex(b >>> 4);
		hexBytes[1] = byte2Hex(b);
		
		return hexBytes;
	}
	
	private static byte byte2Hex(int b)
	{
		b = b & 15;
		
		byte b2;
		
		if (b < 10)
			b2 = (byte)(b + 48);
		else
			b2 = (byte)(b + 55);
		
		return b2;
	}
	
//	private static int[] byte2Bits(int b)
//	{
//		int[] byt = new int[8];
//		
//		byt[0] = (b %= 256) / 128;
//		byt[1] = (b %= 128) / 64;
//		byt[2] = (b %= 64) / 32;
//		byt[3] = (b %= 32) / 16;
//		byt[4] = (b %= 16) / 8;
//		byt[5] = (b %= 8) / 4;
//		byt[6] = (b %= 4) / 2;
//		byt[7] = (b %= 2) / 1;
//		
//		return byt;
//	}
	
	public String[] onLoad()
	{
		return null;
	}
	
	public String getVersion()
	{
		return ID + " v" + VERSION;
	}
}
