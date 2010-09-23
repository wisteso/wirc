package core;
import data.ServerMessage;
import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;

/**
 * IRCSocket
 * <br><br>
 * This class serves as the parent for all the classes.
 * It manages the connection and grants the manager binded 
 * to it a great deal of access to it's methods.
 * <br><br>
 * @author	see http://code.google.com/p/wirc/wiki/AUTHORS
 */
public class IRCSocket extends Thread
{
	/**
	 * The default port for the IRC sever.
	 */
	public static final int DEFAULT_PORT = 6667;
	
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
	 * List containing messages received by the server.
	 */
	private BlockingQueue<ServerMessage> buffer; // blockinglinkedlist?

	/**
	 * Runnable class to handle the <code>Socket</code> to <code>Manager</code> IO.
	 */
	private SocketPoller poller;
	
	/**
	 * Thread to handle the runnable class, <code>cThread</code>.
	 */
	private Thread messageThread;
	
	/**
	 * The current mode of this socket.
	 * @see	Mode
	 */
	private Mode mode = Mode.INITIATING;
	
	/**
	 * Option to reconnect on disconnect.
	 */
	public boolean reconnect = true;

//	/**
//	 *
//	 */
//	private Thread readingThread;

	public IRCSocket(BlockingQueue<ServerMessage> queue)
	{
		buffer = queue;
	}

	/**
	 * Writes a string to this socket. The string is written
	 * as is; no parsing is done.
	 * @param	output The String to write.
	 */
	public void sendData(String output)
	{
		if (sock != null && sock.isConnected())
			out.println(output);
	}

//	public ServerMessage pollData()
//	{
//		try
//		{
//			return buffer.take();
//		}
//		catch (InterruptedException ex)
//		{
//			return null;
//		}
//	}

	/**
	 *
	 * @param input
	 */
	public void pushDebugMessage(String input)
	{
		buffer.add(new ServerMessage("localhost", ":debug!localhost NOTICE * :" + input));
	}

	/**
	 *
	 * @param input
	 */
	public void addSystemMessage(String input)
	{
		buffer.add(new ServerMessage("localhost", ":system!localhost NOTICE * :" + input));
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
	public void connect(String host, Integer port, UserProfile user) throws IOException
	{
		this.mode = Mode.CONNECTING;
		
		try
		{
			addSystemMessage("Connecting to " + host + "..."); // green

			this.sock = new Socket();
			this.sock.connect(new InetSocketAddress(host, port), 5000);

			this.out = new PrintWriter(sock.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}
		catch (IOException ex)
		{
			addSystemMessage("Connection was aborted."); // orange

			this.sock = null;

			this.out = null;
			this.in = null;

			throw new IOException("Connection failed.", ex);
		}
		
		this.mode = Mode.CONNECTED;

		this.buffer.clear();
		this.poller = new SocketPoller();
		this.messageThread = new Thread(this.poller);
		this.messageThread.start();
		
		this.sendData("NICK " + user.nickName);
		this.sendData("USER " + user.nickName + " 0 * :" + user.realName);
	}

	/**
	 *
	 * @param reason
	 */
	public void disconnect(String reason)
	{
		if (mode.isActive())
		{
			this.sendData("QUIT :client quit");

			if (reason.startsWith("user termination"))
			{
				mode = Mode.USER_DISCONNECT;
			}
			else
			{
				mode = Mode.PEER_DISCONNECT;
			}
			
			try
			{
				if (out != null) out.close();
				if (in != null) in.close();
				if (sock != null) sock.close();

				pushDebugMessage("Connection closed: " + reason);
			}
			catch (IOException e)
			{
				pushDebugMessage("Connection closing error: " + e.toString());
			}
		}
	}
	
	/**
	 * A thread to read in from this socket.
	 */
	private class SocketPoller implements Runnable
	{
		public void run() 
		{
			try
			{
				String temp = in.readLine();
				String realHost = temp.substring(1, temp.indexOf(" "));
				String aliasHost = sock.getInetAddress().getHostName();

				buffer.add(new ServerMessage(aliasHost, temp));

				while (mode == Mode.CONNECTED)
				{
					if (in.ready())
					{
						temp = in.readLine();

						if (!temp.startsWith(":"))	// for consistency
							temp = ":" + realHost + " " + temp;

						buffer.add(new ServerMessage(aliasHost, temp));
					}
					
					Thread.sleep(5);
				}
			}
			catch (IOException ex0)
			{
				pushDebugMessage("IO error occured while reading socket.");
			}
			catch (InterruptedException ex1)
			{
				pushDebugMessage("Socket reading thread was interrupted.");
			}

			pushDebugMessage("You have been disconnected.");

			if (mode != Mode.USER_DISCONNECT)
			{
				addSystemMessage("Reconnecting..."); // green
				disconnect("abnormal termination");
			}
			else
			{
				disconnect("user termination");
			}
		}
	}

	public enum Mode
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
}