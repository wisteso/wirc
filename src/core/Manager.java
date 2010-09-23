package core;
import data.ServerChannel;
import gui.TextColor;
import static data.Constants.*;
import data.ServerMessage;
import gui.SwingGUI;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.net.URL;
import java.lang.reflect.Constructor;
import handlers.DefaultOutputHandler;
import handlers.DefaultInputHandler;
import handlers.OutputHandler;
import handlers.InputHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.UIManager;
import plugins.Plugin;

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
public class Manager implements Runnable
{
	private HandlerRegistry handlerReg;
	private List<Plugin> plugins;
	private SwingGUI window;

	private final Map<String, IRCSocket> outboundMap;
	private final BlockingQueue<ServerMessage> inboundQueue;
	
	public UserProfile profile;

	public boolean debug;
	public boolean running;

	private Thread ioThread;
	
	public Manager() throws Exception
	{
		debug = true;

		running = true;

		profile = new UserProfile();

		plugins = new ArrayList<Plugin>();

		outboundMap = new HashMap<String, IRCSocket>();

		inboundQueue = new LinkedBlockingQueue<ServerMessage>();

		window = new SwingGUI(this);

		handlerReg = new HandlerRegistry(this, new DefaultOutputHandler(this), new DefaultInputHandler(this));

		ioThread = new Thread(this);

		ioThread.start();
		
		endInit();
	}

	private void endInit()
	{
		printSystemMsg("Profiles path: " + UserProfile.PROFILE_PATH, TextColor.BLUEGRAY);
		printSystemMsg("Working path: " + RUN_PATH, TextColor.BLUEGRAY);

		printSystemMsg("Loading message handlers...", TextColor.GREEN);
		loadHandlers(this, new File(RUN_PATH + SLASH + "wIRC.jar"));

		printSystemMsg("Requesting login info...", TextColor.GREEN);
		addServer(profile.hostName, IRCSocket.DEFAULT_PORT, profile);
	}

	public void loadHandlers(Manager mgr, File path)
	{
		try
		{
			ClassLoader loader = new RemoteClassLoader();

			String name, urlStr;
			URL urlFile = path.toURI().toURL();

			ZipInputStream zipIn = new ZipInputStream(urlFile.openStream());
			ZipEntry entry;
			
			while ((entry = zipIn.getNextEntry()) != null)
			{
				name = entry.getName();

				if (name.matches("handlers/input/.+\\.class"))
				{
					System.out.println("Loading input handler: " + name);

					urlStr = getClass().getResource("/" + name).toString();
					Class<InputHandler> cls = (Class<InputHandler>)loader.loadClass(urlStr);
					Constructor<InputHandler> con = (Constructor<InputHandler>)cls.getConstructors()[0];

					InputHandler in = con.newInstance(this);	// store me

					registerHandler(in, in.getHooks());
				}
				else if (name.matches("handlers/output/.+\\.class"))
				{
					System.out.println("Loading output handler: " + name);

					urlStr = getClass().getResource("/" + name).toString();
					Class<OutputHandler> cls = (Class<OutputHandler>)loader.loadClass(urlStr);
					Constructor<OutputHandler> con = (Constructor<OutputHandler>)cls.getConstructors()[0];

					OutputHandler out = con.newInstance(this);	// store me
					
					registerHandler(out, out.getHooks());
				}
			}

			zipIn.close();
		}
		catch (Exception e)
		{
			System.out.println("Could not load handlers: " + e);
		}
	}
	
	public void registerHandler(Object handler, String[] handles)
	{
		if (handler == null || handles == null || handles.length == 0)
			return;

		if (handler instanceof handlers.OutputHandler)
		{
			handlerReg.addOutputHandler((handlers.OutputHandler)handler, handles);
		}
		else if (handler instanceof handlers.InputHandler)
		{
			handlerReg.addInputHandler((handlers.InputHandler)handler, handles);
		}
	}

	public void unregisterHandler(Object handler, String[] handles)
	{

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

	public void disconnectAll(String reason)
	{
		Iterator<String> keys = outboundMap.keySet().iterator();

		while (keys.hasNext())
			outboundMap.get(keys.next()).disconnect(reason);
	}

	public void disconnect(String reason, String server)
	{
		outboundMap.get(server).disconnect(reason);

		outboundMap.remove(server);
	}

	/*
	 * END OF HANDLER API
	 */

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

//		oldHandleMessage(data);
	}

	public void addServer(String hostname, Integer port, UserProfile user)
	{
		IRCSocket sock = new IRCSocket(inboundQueue);

		// TODO add asking

		try
		{
			sock.connect(hostname, port, user);

			outboundMap.put(hostname, sock);
		}
		catch (Exception ex)
		{
			sock.disconnect("failed connection");
		}
	}

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
		new Manager();
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