package wIRC;
import java.io.*;
import java.net.*;
import javax.swing.UIManager;

/**
 * IRCSocket
 * <br><br>
 * This class serves as the parent for all the classes.
 * It manages the connection and grants the manager binded 
 * to it a great deal of access to it's methods.
 * <br><br>
 * @author	wisteso@gmail.com
 */
public class IRCSocket
{
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
            System.out.println("Unable to match window style to operating system.");
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
			if (reason.startsWith("user termination"))
			{
				mode = -2;
			}
			else
			{
				mode = -1;
			}
			
			m.printDebugMsg("Connection closed: " + reason);
			
			try
			{
				if (out != null) out.close();
				if (in != null) in.close();
				if (sock != null) sock.close();
			} 
			catch (IOException e)
			{
				m.printDebugMsg("SHUTDOWN ERROR: " + e.toString());
			}
		}
	}
}