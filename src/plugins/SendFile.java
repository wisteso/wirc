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
	
	private static final int cSize = 200;
	//private static final ArrayList<Object> buffer = new ArrayList<Object>();
	
	public String[] processInput(String input, String channel)
	{
		if (input.startsWith("\002") && input.endsWith("\003"))
		{
			try
			{
				int m1 =  input.indexOf("]"), m2 = input.indexOf("|", m1), m3 = input.indexOf("[", m2);
				
				int id = Integer.valueOf(input.substring(1, m1));
				int cPack = Integer.valueOf(input.substring(m1 + 1, m2));
				int cPacks = Integer.valueOf(input.substring(m2 + 1, m3));
				
				byte b[] = input.substring(m3 + 1, input.length() - 2).getBytes();
				
				System.out.println("id: " + id + " pack: " + cPack + " packs: " + cPacks + " data: " + new String(b));
			}
			catch (Exception e)
			{
				System.err.println("Decoding error");
			}
		}
		
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
				
				int id = (int)(Math.random() * 5000);
				int bCount = in.available();
				int cCursor, cPack, cPacks = (int)Math.ceil(bCount * 2.0 / cSize);
				
				byte b[] = new byte[0];
				
				for (int cursor = 0; cursor < bCount; ++cursor)
				{
					cCursor = cursor % cSize;
					
					if (cCursor == 0)
					{
						cPack = (int)Math.ceil(2.0 * cursor / cSize);
						
						if (b.length > 0)
							temp.add("\002" + id + "]" + cPack + "|" + cPacks + "[" + new String(b) + "\003");
						
						if (bCount - cursor > cSize - 1)
							b = new byte[cSize * 2];
						else
							b = new byte[(bCount - cursor) * 2];
					}
					
					byte[] byt = encode(in.read());
					
					b[cCursor * 2] = byt[0];
					b[cCursor * 2 + 1] = byt[1];
				}
				
				temp.add("\002" + id + "]" + cPacks + "|" + cPacks + "[" + new String(b) + "\003");
				
				temp.add("\002HALT\003");
			}
			catch (Exception e)
			{
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
