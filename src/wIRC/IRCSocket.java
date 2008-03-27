package wIRC;
import java.io.*;
import java.net.*;
import javax.swing.UIManager;

/**
 * Root structural-object
 * <br><br>
 * This class serves as the root structure of the client but 
 * is gradually forming a specialized socket class which will 
 * eventually branch off and become an IRCSocket class.
 * <br><br>
 * @author	wisteso@gmail.com
 */
public class IRCSocket
{
	protected File localPath = new File("");
	
	//java.util.prefs.Preferences 
	
	private Socket sock = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	private Manager m;
	
	protected int mode = 0;
	
	public static void main(String[] args)
	{
		new IRCSocket();
	}
	
	public IRCSocket()
	{	
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
		catch (Exception e)
		{
            System.out.println("Cannot find resources for default style interface.");
        }
		
		m = new Manager(this);
		
		if (m.initialize(true))
			connect(true);
		else
			m.printSystemMsg("Connection aborted.", C.ORANGE);
	}
	
	protected void sendData(String output)
	{
		if (sock != null && sock.isConnected())
			out.println(output);
	}
	
	protected void connect(boolean retry)
	{
		mode = 1;
		
		do
		{
			try
			{				
				m.printSystemMsg("Connecting to " + m.hostName + "...", C.GREEN);
				
				sock = new Socket();
				
				sock.connect(new InetSocketAddress(m.hostName, 6667), 5000);
				
				out = new PrintWriter(sock.getOutputStream(), true);
				
	            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			} 
			catch (Exception e)
			{
				if (m.initialize(false))
				{
					sock = null;
				}
				else
				{
					m.printSystemMsg("Connection aborted.", C.ORANGE);
					
					return;
				}
			}
		}
		while (sock == null);
		
		m.notifyConnect();

		cycle();
		
		if (mode > -2)
			connect(retry);
	}
	
	protected void cycle()
	{
		mode = 2;
		
		try
		{
			String dataIn;
			
			while (true)
			{
				if ((dataIn = in.readLine()) != null)
					m.ProcessMessage(dataIn);
			}
		}
		catch (IOException e)
		{
			disconnect(e.toString());
			m.printSystemMsg("You have been disconnected.", C.ORANGE);
		}
	}
	
	protected void disconnect(String reason)
	{
		if (mode > 0)
		{
			mode = -1;
			
			if (reason.equals("user termination"))
				mode = -2;
			
			System.out.println("Connection closed: " + reason);
			
			try
			{
				if (out != null) out.close();
				if (in != null) in.close();
				if (sock != null) sock.close();
			} 
			catch (IOException e)
			{
				System.err.println("SHUTDOWN ERROR: " + e.toString());
			}
		}
	}
}