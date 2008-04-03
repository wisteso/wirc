package plugins;
import wIRC.interfaces.Plugin;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;

public class SendFile implements Plugin
{
	public static final String ID = "file transfer plug-in";
	public static final double VERSION = 0.2;
	
	private static final int cSize = 200;
	private static final HashMap<String, BufferedOutputStream> buffer = new HashMap<String, BufferedOutputStream> ();
	
	public String[] processInput(String input, String channel)
	{	
		int offset;
		
		if ((offset = input.indexOf(":", 1)) > -1)
			input = input.substring(offset + 1);
		
		if (input.startsWith("\002") && input.endsWith("\003"))
		{
			try
			{
				int m1 =  input.indexOf("]"), m2 = input.indexOf("|", m1), m3 = input.indexOf("[", m2);
				
				String id = input.substring(1, m1);
				int cPack = Integer.valueOf(input.substring(m1 + 1, m2));
				int cPacks = Integer.valueOf(input.substring(m2 + 1, m3));
				
				byte b[] = input.substring(m3 + 1, input.length() - 1).getBytes();
				
				if (b.length % 2 == 0)
				{
					byte db[] = new byte[b.length / 2];
					
					for (int i = 0; i < db.length; ++i)
					{
						int[] chunk = {(int)b[i * 2], (int)b[(i * 2) + 1]};
						
						db[i] = (byte)decode(chunk);
					}
					
					if (!buffer.containsKey(id))
						buffer.put(id, new BufferedOutputStream(new FileOutputStream(new File(id))));
					
					BufferedOutputStream out = buffer.get(id);
					
					out.write(db);
					
					if (cPack == cPacks)
						out.close();
					
					System.out.println(new String(db));
				}
			}
			catch (Exception e)
			{
				System.err.println("Decoding error");
				e.printStackTrace();
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
				
				int id = (int)(Math.random() * 8999) + 1000;
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
					
					int[] chunk = encode(in.read());
					
					b[cCursor * 2] = (byte)chunk[0];
					b[cCursor * 2 + 1] = (byte)chunk[1];
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
	
	private static int[] encode(int decByte)
	{	
		int[] hexBytes = {byte2Hex((decByte >> 4) & 15), byte2Hex(decByte & 15)};
		
		return hexBytes;
	}
	
	private static int byte2Hex(int b)
	{
		b = b & 15;
		
		if (b < 10)
			b += 48;
		else
			b += 55;
		
		return b;
	}
	
	private static int decode(int[] hexBytes)
	{	
		int b = (hex2Byte(hexBytes[0]) << 4) | hex2Byte(hexBytes[1]);
		
		//System.out.print("(" + hexBytes[0] + "|" + hexBytes[1] + ")");
		//System.out.print("[" + hex2Byte(hexBytes[0]) + "|" + hex2Byte(hexBytes[1]) + "]-");
		
		return b;
	}
	
	private static int hex2Byte(int hex)
	{
		hex = hex & 127;
		
		if ('0' <= hex && hex <= '9')
			hex -= 48;
		else if ('A' <= hex && hex <= 'F')
			hex -= 55;
		
		return hex;
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

//private static int[] byte2Bits(int b)
//{
//	int[] byt = new int[8];
//	
//	byt[0] = (b %= 256) / 128;
//	byt[1] = (b %= 128) / 64;
//	byt[2] = (b %= 64) / 32;
//	byt[3] = (b %= 32) / 16;
//	byt[4] = (b %= 16) / 8;
//	byt[5] = (b %= 8) / 4;
//	byt[6] = (b %= 4) / 2;
//	byt[7] = (b %= 2) / 1;
//	
//	return byt;
//}