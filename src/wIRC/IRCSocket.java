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
	protected File homePath = new File(System.getProperty("user.home") + File.separator + ".wIRC");
	
	private Socket sock = null;
	private PrintWriter out = null;
	private BufferedReader in = null;
	
	private Manager m;
	
	protected int mode = 0;
	
	public static void main(String[] args) throws Exception
	{
		new IRCSocket();
	}
	
	public IRCSocket() throws Exception
	{	
		if (!homePath.isDirectory())
		{
			if (homePath.mkdir() == false)
				System.err.println("Unable to create user folder.");
			else
				System.out.println("User folder created.");
		}
		else
		{
			System.out.println("Using existing user folder.");
		}
		
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
	
	protected void connect(boolean retry) throws Exception
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