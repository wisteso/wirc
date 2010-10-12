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
public class ChatSocket extends Thread
{
	public static final int TIMEOUT = 5000;
	
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
	 * Thread to handle the runnable class, <code>cThread</code>.
	 */
	private Thread socketPoller;
	
	/**
	 * The current mode of this socket.
	 * @see	Mode
	 */
	private Mode mode = Mode.INITIATING;

	/**
	 *
	 * @param queue
	 */
	public ChatSocket(BlockingQueue<ServerMessage> queue)
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
	public void pushSystemMessage(String input)
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
	public void connect(SocketAddress server, UserProfile user) throws IOException
	{
		this.mode = Mode.CONNECTING;
		
		try
		{
			pushSystemMessage("Connecting to [" + server + "]..."); // green

			this.sock = new Socket();
			this.sock.connect(server, TIMEOUT);

			this.out = new PrintWriter(sock.getOutputStream(), true);
			this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		}
		catch (IOException ex)
		{
			pushSystemMessage("Connection was aborted."); // orange

			this.sock = null;
			this.out = null;
			this.in = null;

			return;
			//throw new IOException("Connection failed.", ex);
		}
		
		this.mode = Mode.CONNECTED;

		this.buffer.clear();

		this.socketPoller = new SocketPoller();
		this.socketPoller.start();
		
		this.sendData("NICK " + user.getNick());
		this.sendData("USER " + user.getNick() + " 0 * :" + user.getName());
	}

	/**
	 *
	 * @param reason
	 */
	public void disconnect(boolean userTriggered)
	{
		if (mode.isActive())
		{
			this.sendData("QUIT :client quit");

			if (userTriggered)
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

				if (userTriggered)
					pushDebugMessage("Connection closed by user.");
				else
					pushDebugMessage("Connection closed by host.");
			}
			catch (IOException e)
			{
				pushDebugMessage("Connection closing error: " + e.toString());
			}

			out = null;
			in = null;
			sock = null;
		}
	}
	
	/**
	 * A thread to read in from this socket.
	 */
	private class SocketPoller extends Thread
	{
		@Override
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

			if (mode == Mode.USER_DISCONNECT)
			{
				disconnect(false);
			}
			else
			{
				disconnect(true);
				pushSystemMessage("Reconnecting..."); // green
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

		public boolean isDisconnected()
		{
			if (this == PEER_DISCONNECT || this == USER_DISCONNECT)
				return true;

			return false;
		}
	}
}