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
 * @author	see http://code.google.com/p/wirc/wiki/AUTHORS
 */
public class IRCSocket
{
	/**
	 * The default port for the IRC sever.
	 */
	private static final int IRC_PORT = 6667;
	
	public enum MODE
	{
		CONNECTING,			// The socket is connecting.
		CONNECTED,			// Successfully connected.
		INITIATING,			// Default start-up state.
		PEER_DISCONNECT,	// Disconnected for some reason, besides user disconnects.
		USER_DISCONNECT;	// User disconnected from the socket.
		
		public boolean isActive()
		{
			if (this == CONNECTING || this == CONNECTED)
				return true;
			
			return false;
		}
	}
	
	/**
	 * Internal Socket
	 */
	private Socket sock = null;
	
	/**
	 * Output stream to the socket.
	 */
	private PrintWriter out = null;

	/**
	 * Input stream from the socket.
	 */
	private BufferedReader in = null;

	/**
	 * Does all the work; connects things together
	 */
	private Manager m;
	
	/**
	 * Runnable class to handle the <code>Socket</code> to <code>Manager</code> IO.
	 */
	private CycleThread cThread;
	
	/**
	 * Thread to handle the runnable class, <code>cThread</code>.
	 */
	private Thread messageThread;
	
	/**
	 * The current mode of this socket.
	 * 
	 * @see	MODE_CONNECTED, MODE_CONNECTING, MODE_INITIATING,
	 * MODE_PEER_DISCONNECT, MODE_USER_DISCONNECT
	 */
	protected MODE mode = MODE.INITIATING;
	
	/**
	 * Option to reconnect on disconnect.
	 */
	public boolean reconnect = true;
	
	/**
	 * Main method to run this IRC client.
	 */
	public static void main(String[] args)
	{
		new IRCSocket();
	}
	
	/**
	 * Constructs a new IRC socket.
	 * Configures the UIManager, creates managers, etc.
	 * This constructor will not return; initalizes an 
	 * infinite loop. 
	 */
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
		
		this.m = new Manager(this);
		
		if (m.initialize(true))
			connect();
		else
			m.printSystemMsg("Connection aborted.", C.COLOR.ORANGE);
	}
	
	/**
	 * Writes a string to this socket. The string is written
	 * as is; no parsing is done.
	 * 
	 * @param	output The String to write.
	 */
	protected void sendData(String output)
	{
		if (sock != null && sock.isConnected())
			out.println(output);
	}
	
	/**
	 * Connects this socket. Retries if <code>retry</code> is true. 
	 * Attempts to connect to the sever and then <code>cycle()</code>.
	 * If it fails and <code>retry</code> is true, 
	 * then it attempts to connect again, recursively.
	 * <b>This method will not return</b>
	 * 
	 * @param	whether or not to retry after a connection failure.
	 */
	protected void connect()
	{
		this.mode = MODE.CONNECTING;
		
		do
		{
			try
			{
				m.printSystemMsg("Connecting to " + m.hostName + "...", C.COLOR.GREEN);
				
				sock = new Socket();
				
				sock.connect(new InetSocketAddress(m.hostName, IRC_PORT), 5000);
				
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
					m.printSystemMsg("Connection aborted.", C.COLOR.ORANGE);
					
					return;
				}
			}
		}
		while (sock == null);
		
		cThread = new CycleThread();
		
		messageThread = new Thread(cThread);

		messageThread.start();
		
		m.notifyConnect();
	}
	
	/**
	 * A thread to read in from this socket.
	 */
	private class CycleThread implements Runnable
	{	
		public void run() 
		{
			mode = MODE.CONNECTED;
			
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
				m.printSystemMsg("You have been disconnected.", C.COLOR.ORANGE);
				
				if (reconnect && mode != MODE.USER_DISCONNECT)
				{
					disconnect(e.toString());
					m.printSystemMsg("Reconnecting...", C.COLOR.GREEN);
					connect();
				}
				else
				{
					disconnect("user termination");
				}
			}
		}
	}
	
	protected void disconnect(String reason)
	{
		if (mode.isActive())
		{
			sendData("QUIT :client quit");
			
			if (reason.startsWith("user termination"))
			{
				mode = MODE.USER_DISCONNECT;
			}
			else
			{
				mode = MODE.PEER_DISCONNECT;
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