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
	protected static File localPath = new File("");
	
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
		
		m.initialize(true);
		
		connect(true);
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
				sock = new Socket();
				
				sock.connect(new InetSocketAddress(m.hostName, 6667), 5000);
				
				out = new PrintWriter(sock.getOutputStream(), true);
				
	            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			} 
			catch (UnknownHostException e)
			{
				sock = null;
					
				m.initialize(false);
			}
			catch (SocketTimeoutException e)
			{
				sock = null;
				
				m.initialize(false);
			}
			catch (IOException e)
			{
				System.err.println(e.toString());
	            System.exit(1);
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
		catch (Exception e)
		{
			disconnect(e.toString());
			m.window.println("(NOTICE) You have been disconnected.", C.ORANGE);
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