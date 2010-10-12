package core;
import java.net.InetSocketAddress;
import gui.TextColor;
import gui.SwingGUI;
import data.ServerChannel;
import data.ServerMessage;
import handlers.DefaultOutputHandler;
import handlers.DefaultInputHandler;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.UIManager;
import static data.Constants.*;

/**
 * Chat structural-object
 * <br><br>
 * This class manages the logical operations which happen between 
 * users IO and remote IO. It also serves as the medium by which 
 * both are allowed to communicate through, ideally. Though there 
 * are currently some hacks in place.
 * <br><br>
 * @author 	see http://code.google.com/p/wirc/wiki/AUTHORS
 */
public class Facade
{
	private HandlerRegistry handlerReg;
	//private List<Plugin> plugins;
	private SwingGUI window;

	private final Map<String, ChatSocket> outboundMap;
	private final BlockingQueue<ServerMessage> inboundQueue;
	
	public UserProfile profile;

	public boolean debug;
	public boolean running;

	private Thread chatSocketPoller;
	
	public Facade() throws Exception
	{
		debug = true;

		running = true;

		window = new SwingGUI(this);

		outboundMap = new HashMap<String, ChatSocket>();

		inboundQueue = new LinkedBlockingQueue<ServerMessage>();

		handlerReg = new HandlerRegistry(this, new DefaultOutputHandler(this), new DefaultInputHandler(this));

		chatSocketPoller = new InputListener();

		chatSocketPoller.start();
		
		endInit();
	}

	private void endInit()
	{
		printSystemMsg("Profiles path: " + UserProfile.PROFILE_PATH, TextColor.BLUEGRAY);
		printSystemMsg("Working path: " + RUN_PATH, TextColor.BLUEGRAY);

		printSystemMsg("Loading message handlers...", TextColor.GREEN);
		handlerReg.loadHandlers(new File(RUN_PATH + SLASH + "wIRC.jar"));

		try
		{
			profile = new UserProfile();
			addServer(profile);
			//printSystemMsg("Requesting login info...", TextColor.GREEN);
		}
		catch (Exception ex)
		{
			printSystemMsg("Invalid profile.", TextColor.RED);
		}
	}

	/*
	 * START OF HANDLER API
	 */

	public void updateProfile()
	{
		profile.writeProfile();
	}

	public void sendData(String msg, String server)
	{
		if (msg != null && !msg.isEmpty())
			outboundMap.get(server).sendData(msg);
	}

	public void sendDataAll(String msg)
	{
		throw new UnsupportedOperationException("Method not supported yet.");
	}

	public void print(String msg, TextColor style)
	{
		window.print(msg, style);
	}

	public void print(String msg, ServerChannel channel, TextColor style)
	{
		window.print(msg, channel, style);
	}

	public void println(String msg, TextColor style)
	{
		window.println(msg, style);
	}

	public void println(String msg, ServerChannel channel, TextColor style)
	{
		window.println(msg, channel, style);
	}

	public void printSystemMsg(String msg, TextColor style)
	{
		window.println("(SYSTEM) " + msg, style);
	}
	
	public void printDebugMsg(String msg)
	{
		if (debug)
		{
			String timeStamp = TIME.format(new java.util.Date());
	        
			window.println("[" + timeStamp + "] " + msg, DEBUG, TextColor.RED);
		}
	}

	public void addChat(ServerChannel channel)
	{
		try
		{
			window.addChat(channel);
		}
		catch (Exception ex)
		{
			this.printDebugMsg("Error adding chat tab: " + ex);
		}
	}

	public boolean remChat(ServerChannel channel)
	{
		return window.removeChat(channel);
	}

	public void addNames(ServerChannel channel, String... nicks)
	{
		window.addNicks(channel, nicks);
	}

	public void remName(ServerChannel channel, String nick)
	{
		window.removeNicks(channel, nick);
	}

	public void remName(String nick)
	{
		window.removeNick(nick);
	}

	public ServerChannel[] replaceNick(String oldName, String newName)
	{
		return window.replaceNick(oldName, newName);
	}

	public void focusChat(ServerChannel channel)
	{
		window.setFocusedChat(channel);
	}

	public ServerChannel getFocusedChat()
	{
		return window.getFocusedChat();
	}

	public void disconnectAll(boolean userTriggered)
	{
		Iterator<String> keys = outboundMap.keySet().iterator();

		while (keys.hasNext())
		{
			outboundMap.get(keys.next()).disconnect(true);
		}
	}

	public void disconnect(boolean userTriggered, String server)
	{
		outboundMap.get(server).disconnect(true);

		outboundMap.remove(server);
	}

	public void sendMessage(String msg, ServerChannel sChan)
	{
		// TODO: add cases for all consoles, not just master

		if (msg.startsWith("/"))
		{
			handlerReg.postOutputMessage(msg.substring(1), sChan);
		}
		else if (!sChan.channel.equals("console"))
		{
			handlerReg.postOutputMessage("PRIVMSG " + sChan.channel + " :" + msg, sChan);
		}
		else
		{
			printSystemMsg("Cannot send a message to the console.", TextColor.ORANGE);
		}
	}

	public void handleMessage(ServerMessage msg)
	{
		System.out.println(msg);

		handlerReg.postInputMessage(msg.message, msg.server);
	}

	public void addServer(UserProfile profile)
	{
		ChatSocket sock = new ChatSocket(inboundQueue);

		try
		{
			sock.connect(profile.getAddress(), profile);

			outboundMap.put(profile.getHost(), sock);
		}
		catch (Exception ex)
		{
			sock.disconnect(false);
		}
	}

	/*
	 * END OF HANDLER API
	 */

	private class InputListener extends Thread
	{
		@Override
		public void run()
		{
			ServerMessage buffer;

			try
			{
				while (running)
				{
					if (!inboundQueue.isEmpty())
					{
						buffer = inboundQueue.take();

						handleMessage(buffer);
					}

					Thread.sleep(5);
				}
			}
			catch (InterruptedException ex)
			{
				inboundQueue.add(new ServerMessage("localhost",
					":debug!debug@localhost Socket reading thread was interrupted."));
			}
		}
	}

	static
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
		catch (Exception e)
		{
            System.out.println("Unable to match window style to operating system.");
        }
	}

	/**
	 * Main method to run this IRC client.
	 */
	public static void main(String[] args) throws Exception
	{
		Facade m = new Facade();
	}
}

//	@Deprecated
//	public void oldHandleMessage(String data)
//	{
//		// get rid of all code below by converting to handlers
//
//		int code = 0;
//		try
//		{
//			code = Integer.parseInt(data.substring(0, data.indexOf(" ")));
//		}
//		catch (NumberFormatException e) { }
//
//		if (code > 0)
//		{
//			String temp;
//			String arguments = data.substring(data.indexOf(" ") + 1);
//			String channel = CONSOLE;
//
//			if (code == Codes.CHAN_TOPIC || code == Codes.RPL_TOPICWHOTIME || code == Codes.NAMELIST_CONTENT || code == Codes.NAMELIST_FOOTER)
//			{
//				int p1 = arguments.indexOf("#");
//				int p2 = arguments.indexOf(" ", p1);
//
//				if (p1 > -1 && p2 > -1)
//					channel = arguments.substring(p1, p2);
//			}
//
//			else if (code == 433)
//			{
//				String tempNick = "Nullname" + (int)(Math.random() * 10000);
//				window.println("(ERROR) Nickname already in use. Nickname set to: " + tempNick, channel, TextColor.RED);
//
//				//sock.sendData("NICK " + tempNick);
//				profile.nickName = tempNick;
//			}
//		}
//	}