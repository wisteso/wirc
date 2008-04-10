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
	public static final int MODE_CONNECTING = 1;
	public static final int MODE_CONNECTED = 2;
	public static final int MODE_INITIATING = 0;
	public static final int MODE_PEER_DISCONNECT = -1;
	public static final int MODE_USER_DISCONNECT = -2;
	
	
	/**
	 * Internal Socket
	 */
	private Socket sock = null;
	/**
	 * Print writer to the socket.
	 */
	private PrintWriter out = null;
	/**
	 * Reader from the socket
	 */
	private BufferedReader in = null;
	
	/**
	 * Does all the work; connects things together
	 */
	private Manager m;
	
	/**
	 * The mode of this socket?
	 *  1 = connecting
	 *  2 = connected
	 *  0 = ?
	 *  -1 = Been Disconected
	 *  -2 = user_disconnect
	 */
	protected int mode = MODE_INITIATING;
	
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
		mode = MODE_CONNECTING;
		
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
		
		if (mode != MODE_USER_DISCONNECT)
			connect(retry);
	}
	
	protected void cycle()
	{
		mode = MODE_CONNECTED;
		
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
		if (mode > MODE_INITIATING)
		{
			if (reason.startsWith("user termination"))
			{
				mode = MODE_USER_DISCONNECT;
			}
			else
			{
				mode = MODE_PEER_DISCONNECT;
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