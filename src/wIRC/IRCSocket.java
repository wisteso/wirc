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
 * @author	Will (wisteso@gmail.com)
 * @author 	Victor (virtualbitt@gmail.com)
 */
public class IRCSocket
{
	/**
	 * The port on the IRC sever (constant).
	 */
	private static final int IRC_PORT = 6667;
	
	/**
	 * Mode if the socket is connecting.
	 */
	protected static final int MODE_CONNECTING = 1;
	
	/**
	 * Mode if successfully connected.
	 */
	protected static final int MODE_CONNECTED = 2;
	
	/**
	 * Initial mode.
	 */
	protected static final int MODE_INITIATING = 0;
	
	/**
	 * Mode if disconnected for some reason, besides user disconnects.
	 */
	protected static final int MODE_PEER_DISCONNECT = -1;
	
	/**
	 * Mode if user disconnected from the socket.
	 */
	protected static final int MODE_USER_DISCONNECT = -2;
	
	/**
	 * Internal Socket
	 */
	private Socket sock = null;
	
	/**
	 * Print writer to the socket.
	 */
	private PrintWriter out = null;

	/**
	 * Reader from the socket.
	 */
	private BufferedReader in = null;

	/**
	 * Does all the work; connects things together
	 */
	private Manager m;
	
	private MessageHandlerThread msgThread;
	
	/**
	 * The current mode of this socket.
	 * 
	 * @see	MODE_CONNECTED, MODE_CONNECTING, MODE_INITIATING,
	 * MODE_PEER_DISCONNECT, MODE_USER_DISCONNECT
	 */
	protected int mode = MODE_INITIATING;
	
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
		
		m = new Manager(this);
		
		if (m.initialize(true))
			connect(true);
		else
			m.printSystemMsg("Connection aborted.", C.ORANGE);
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
	 * Connects this socket. Retires if retry. 
	 * <b>This method will not return</b>
	 * Attempts to connect to the sever and then cycle(),
	 * if it fails & retry is true, then it attempts to connect again, recrusively.
	 * 
	 * @param	whether or not to retry after a connection failure.
	 */
	protected void connect(boolean retry)
	{
		mode = MODE_CONNECTING;
		
		Thread messageThread = null;
		
		do
		{
			try
			{
				m.printSystemMsg("Connecting to " + m.hostName + "...", C.GREEN);
				
				sock = new Socket();
				
				sock.connect(new InetSocketAddress(m.hostName, IRC_PORT), 5000);
				
				out = new PrintWriter(sock.getOutputStream(), true);
				
	            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				
				msgThread = new MessageHandlerThread();
				
				messageThread = new Thread(msgThread);
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
		// block until the socket is not null.
		while (sock == null);
		
		m.notifyConnect();

		messageThread.start();

//		cycle();
//		
//		if (mode != MODE_USER_DISCONNECT)
//			connect(retry);
	}
	
	/**
	 * Infinitely reads data that is coming to this socket,
	 * only stops if an IO exception is caught.
	 */
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
	
	/**
	 * A thread to read in from this socket.
	 * 
	 * @author 	Victor
	 * @author	Will
	 */
	private class MessageHandlerThread implements Runnable
	{	
		/* 
		 * (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() 
		{
			cycle();
			
			if (mode != MODE_USER_DISCONNECT)
				connect(true);
		}
	}
}