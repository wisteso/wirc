package wIRC;
import java.io.File;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Scanner;
import SortedListModel.*;
import wIRC.interfaces.*;

/**
 * Chat structural-object
 * <br><br>
 * This class manages the logical operations which happen between 
 * users IO and remote IO. It also serves as the medium by which 
 * both are allowed to communicate through, ideally. Though there 
 * are currently some hacks in place.
 * <br><br>
 * @author 	wisteso@gmail.com
 */
public class Manager 
{
	protected String nickName = "Nullname" + (int)(Math.random() * 9000 + 999);
	protected String realName = "Anonymous";
	protected String userInfo = "No info set.";
	protected String hostName = "st0rage.org";
	
	protected TreeMap<String, User> users = new TreeMap<String, User>();
	protected ArrayList<Plugin> plugins = new ArrayList<Plugin>();
	protected UserInput window = new DefaultGUI(hostName, this);
	
	private IRCSocket s;
	
	public Manager(IRCSocket s)
	{
		this.s = s;
		
		window.println("(SYSTEM) Home path: " + s.homePath, C.BLUEGRAY);
		
		window.println("(SYSTEM) Requesting login info...", C.GREEN);
	}
	
	public boolean initialize(boolean askAll)
	{
		if (askAll)
		{
			nickName = window.askQuestion("Enter your nick-name:", nickName);
			
			if (nickName == null)
				return false;
	
			realName = window.askQuestion("Enter your user-name:", realName);
			
			if (realName == null)
				return false;
			
			hostName = window.askQuestion("Enter the host-name:", hostName);
			
			if (hostName == null)
				return false;
		}
		else
		{
			hostName = window.askQuestion("Invalid Host. Re-enter the host-name:", hostName);
			
			if (hostName == null)
				return false;
		}
		
		window.setServerInfo(hostName);
		
		return true;
	}
	
	protected void notifyConnect()
	{
		sendData("NICK " + nickName);
		sendData("USER " + nickName + " 0 * :" + realName);
	}
	
	protected void sendData(String msg)
	{
		if (msg != null)
			s.sendData(msg);
	}
	
	protected void printSystemMsg(String msg, int style)
	{
		window.println("(SYSTEM) " + msg, style);
	}
	
	protected void sendMsg(String msg, String chanName)
	{
		if (msg.charAt(0) == '/')
		{
			String command = new String();
			int spaceIndex = msg.indexOf(' ');
			
			if (spaceIndex > -1)
			{
				command = msg.substring(1, spaceIndex).toUpperCase();
			}
			else
			{
				command = msg.substring(1).toUpperCase();
			}
			
			if (command.equals("MSG"))
			{
				if (spaceIndex > -1)
				{
					int m1 = spaceIndex + 1;
					int m2 = msg.indexOf(" ", m1 + 1);
					
					if (m2 > m1)
					{
						String recipient = msg.substring(m1, m2);
						String message = msg.substring(m2 + 1);
						
						s.sendData("PRIVMSG " + recipient + " :" + msg);
						
						window.println("<" + nickName + "> ", recipient, C.BLUE_BOLD);
						window.print(message, recipient, C.BLACK);
					}
				}
			}
			else if (command.equals("JOIN"))
			{
				if (spaceIndex > -1)
					s.sendData(msg.substring(1));
				else if (!chanName.equals("Console"))
					s.sendData("JOIN " + chanName);
			}
			else if (command.equals("REJOIN"))
			{
				if (spaceIndex > -1)
				{
					s.sendData("PART" + msg.substring(7));
					
					s.sendData(msg.substring(3));
				}
				else if (!chanName.equals("Console"))
				{
					s.sendData("JOIN " + chanName);
				}
			}
			else if (command.equals("PART"))
			{
				if (spaceIndex > -1)
					closeChat(msg.substring(spaceIndex + 1));
				else if (!chanName.equals("Console"))
					closeChat(chanName);
			}
			else if (command.equals("AUTH"))
			{
				s.sendData("PRIVMSG NICKSERV :IDENTIFY " + msg.substring(spaceIndex + 1));
			}
			else if (command.equals("RECONNECT"))
			{
				s.disconnect("reconnecting");
				window.println("\n(SYSTEM) Reconnecting...", chanName.toLowerCase(), C.ORANGE);
			}
			else if (command.equals("DISCONNECT"))
			{
				s.disconnect("user termination");
				window.println("(SYSTEM) Disconnecting...", chanName.toLowerCase(), C.ORANGE);
			}
			else if (command.equals("SLOAD"))
			{
				window.println("(SYSTEM) Loading plugin (strict)...", chanName.toLowerCase(), C.BLUE);
				
				String pluginPath = msg.substring(spaceIndex + 1).trim();
				
				String pluginName = loadPlugin(pluginPath);
				
				if (pluginName != null)
					window.println("(SYSTEM) " + pluginName + " loaded.", chanName.toLowerCase(), C.BLUE);
				else
					window.println("(SYSTEM) Plugin loading failed - path: " + pluginPath, chanName.toLowerCase(), C.BLUE);
			}
			else if (command.equals("LOAD"))
			{
				window.println("(SYSTEM) Loading plugin...", chanName.toLowerCase(), C.BLUE);
				
				String input = msg.substring(spaceIndex + 1).trim();
				
				String pluginPath;
				
				if (input.startsWith("http://"))
					pluginPath = input;
				else
					pluginPath = s.homePath + C.PSLASH + "plugins" + C.PSLASH + input;
				
				if (!pluginPath.endsWith(".class"))
					pluginPath += ".class";
				
				String pluginName = loadPlugin(pluginPath);
				
				if (pluginName != null)
					window.println("(SYSTEM) " + pluginName + " loaded.", chanName.toLowerCase(), C.BLUE);
				else
					window.println("(SYSTEM) Plugin loading failed - path: " + pluginPath, chanName.toLowerCase(), C.BLUE);
			}
			else if (command.equals("SCRIPT"))
			{
				window.println("(SYSTEM) Executing script...", chanName.toLowerCase(), C.BLUE);
				
				String input = msg.substring(spaceIndex + 1).trim();
				
				String scriptPath;
				
				if (input.startsWith("http://"))
					scriptPath = input;
				else
					scriptPath = s.homePath + C.PSLASH + "scripts" + C.PSLASH + input;
				
				if (!scriptPath.endsWith(".script"))
					scriptPath += ".script";
				
				String scriptName = executeScript(scriptPath);
				
				if (scriptName != null)
					window.println("(SYSTEM) " + scriptName + " finished.", chanName.toLowerCase(), C.BLUE);
				else
					window.println("(SYSTEM) Script loading failed - path: " + scriptPath, chanName.toLowerCase(), C.BLUE);
			}
			else
				s.sendData(msg.substring(1));
		}
		else
		{
			if (!chanName.equals("Console"))
			{
				s.sendData("PRIVMSG " + chanName + " :" + msg);
				window.println("<" + nickName + "> ", chanName.toLowerCase(), C.BLUE_BOLD);
				window.print(msg, chanName.toLowerCase(), C.BLACK);
			}
		}
	}
	
	protected String loadPlugin(String path)
	{
	    PlugInLoader l = new PlugInLoader();
	    
	    try
	    {
	    	Class<?> p = l.findClass(path);
	    	
	    	Object o = p.newInstance();
	    	
    		Plugin t = (Plugin)o;
    		
	    	plugins.add(t);
	    	
	    	String[] msg = t.onLoad();
	    	
	    	if (msg != null)
	    		for (int i = 0; i < msg.length; ++i)
	    			sendData(msg[i]);
	    	
	    	return t.getVersion();
	    }
	    catch (InstantiationException e)
	    {
	    	e.printStackTrace();
	    }
	    catch (IllegalAccessException e)
	    {
	    	e.printStackTrace();
	    }
	    catch (Exception e)
	    {
	    	/*<HACK>*
	    	if (!path.startsWith("bin" + File.separator))
	    	{
	    		System.err.println("Attempting alternate path...");
	    		
	    		return loadPlugin("bin" + File.separator + path);
	    	}
	    	*<END HACK>*/
	    	
	    	System.err.println(e.toString());
	    }
	    
	    return null;
	}
	
	protected String executeScript(String path)
	{
	    try
	    {
	    	Scanner in = new Scanner(new File(path));
			
	    	if (!in.hasNextLine())
	    		return null;
	    		
	    	String header = in.nextLine();
	    	
			while (in.hasNextLine())
				sendMsg(in.nextLine().trim(), "Console");
			
			return header;
	    }
	    catch (Exception e)
	    {
	    	/*<HACK>*
	    	if (!path.startsWith("bin" + File.separator))
	    	{
	    		System.err.println(e.getMessage() + "\nAttempting alternate path...");
	    		
	    		return executeScript("bin" + File.separator + path);
	    	}
	    	*<END HACK>*/
	    	
	    	System.err.println(e.toString());
	    }
	    
	    return null;
	}
	
	protected void closeChat(String chan)
	{
		if (window.removeChat(chan) == true)
		{
			if (chan.charAt(0) == '#')
				s.sendData("PART " + chan);
		}
		else
		{
			window.println("(ERROR) That chat does not exist.", "Console", C.RED);
		}
	}
	
	protected void ProcessMessage(String rawIn)
	{
		Message x = new Message(rawIn, this);
		
		int code = x.getCode();
		String msg = x.getMessage();
		String chan = x.getChannel();
		
		if (!plugins.isEmpty())
		{
			String[] output;
			
			for (int i = 0; i < plugins.size(); ++i)
			{
				output = plugins.get(i).processInput(rawIn, chan);
				
				if (output != null)
					for (int j = 0; j < output.length; ++j)
						s.sendData(output[j]);
			}
		}

		if (code < 0)
		{
			if (code == C.MESSAGE)
			{
				if (x.getChannel().charAt(0) == 0x23)
				{
					window.println("<" + x.getNick() + "> ", chan, C.BLUE);
					
					int i = msg.indexOf(nickName);
					
					if (i > -1)
					{
						int j = 0;
						
						while (i > -1)
						{
							window.print(msg.substring(j, i), chan, C.BLACK);
							
							j = i + nickName.length();
							
							window.print(msg.substring(i, j), chan, C.BLACK_BOLD);
							
							i = msg.indexOf(nickName, j);
						}
						
						window.print(msg.substring(j), chan, C.BLACK);
					}
					else
					{
						window.print(msg, chan, C.BLACK);
					}
				}
				else
				{
					window.println("<" + x.getNick() + "> ", chan, C.VIOLET);
					window.print(msg, chan, C.BLACK);
				}
			}
			else if (code == C.NOTICE)
			{
				window.println("(NOTICE) " + msg, chan, C.ORANGE);
			}
			else if (code == C.PING)
			{
				s.sendData("PONG " + msg);
				window.println("(PING) " + msg, chan, C.GREEN);
			}
			else if (code == C.JOIN)
			{
				window.println("<" + x.getNick() + " has joined>", chan, C.BLUEGRAY);
				
				if (!x.getNick().equals(nickName))
				{
					window.addNicks(x.getChannel(), x.getNick());
					users.put(x.getNick(), new User(x.getNick(), null, x.getChannel()));
				}
			}
			else if (code == C.PART)
			{	
				if (!chan.equals("Console"))
				{
					if (x.getNick().equals(nickName))
					{
						window.println("<You have left " + chan + ">", C.BLUEGRAY);
					}
					else
					{
						if (msg.length() < 2)
							window.println("<" + x.getNick() + " has left>", chan, C.BLUEGRAY);
						else
							window.println("<" + x.getNick() + " has left - " + msg + ">", chan, C.BLUEGRAY);
						
						window.removeNicks(chan, x.getNick());
						
						if (!removeUser(x.getNick()))
							System.err.println(x.getNick() + " not found in user-treemap. (PART)");
					}
				}
				else
				{
					System.err.println(x.getNick() + " cannot be removed from the Console. (PART)");
					System.err.println(x.getMessage() + " | " + rawIn);
				}
			}
			else if (code == C.QUIT)
			{
				if (users.containsKey(x.getNick()))
				{
					String[] chans = users.get(x.getNick()).getChans();
					
					for (int a = 0; a < chans.length; ++a)
					{
						window.removeNicks(chans[a], x.getNick());
						
						if (msg.length() < 2)
							window.println("<" + x.getNick() + " has quit>", chans[a], C.BLUEGRAY);
						else
							window.println("<" + x.getNick() + " has quit - " + msg.toLowerCase() + ">", chans[a], C.BLUEGRAY);
						
						if (!removeUser(x.getNick()))
							System.err.println(x.getNick() + " not found in user-treemap. (QUIT)");
					}
				}
				else
				{
					System.err.println(x.getNick() + " not found in user-treemap. (QUIT)");
				}
			}
			else if (code == C.MODE)
			{
				if (x.getNick() != hostName)
				{
					window.println("<" + x.getNick() + " is now " + msg + ">", chan, C.BLUEGRAY);
					window.replaceNick(x.getNick(), msg);
				}
				else
					window.println("<" + x.getChannel() + " is now " + msg + ">", chan, C.BLUEGRAY);
			}
			else if (code == C.NICK)
			{
				// TODO: Show this notification on the right channels.
				if (x.getNick().equals(nickName) == true)
				{
					nickName = msg;
					window.replaceNick(x.getNick(), msg);
					window.println("<You are now known as " + msg + ">", window.getFocusedChat(), C.BLUE);
				}
				else
				{
					window.replaceNick(x.getNick(), msg);
					window.println("<" + x.getNick() + " is now known as " + msg + ">", chan, C.BLUEGRAY);
				}
				
/*		FIXME : keys are not being correctly changed.

				if (users.containsKey(x.getNick()))
                {
                        String[] chans = users.get(x.getNick()).getChans();
                        
                        for (int a = 0; a < chans.length; ++a)
                        {
                                window.println("<" + x.getNick() + " is now known as " + msg + ">", chans[a], C.BLUEGREY);
                                updateUser(x.getNick(), msg);
                        }
                }
                else
                {
                        System.err.println(x.getNick() + " not found in user-treemap. (NICK)");
                }
*/
			}
			else if (code == C.CTCP_MSG)
			{	
				String reply = new String();
				
				if (msg.indexOf("ACTION") == 0)
				{
					window.println("<" + x.getNick() + msg.substring(msg.indexOf(" ")) + ">", chan, C.VIOLET);
				}
				else if (msg.indexOf("PING") == 0)
				{
					window.println("<" + x.getNick() + " has requested your ping>", chan, C.VIOLET);
					reply = "PING " + msg.substring(msg.indexOf(" ") + 1);
					s.sendData("NOTICE " + x.getNick() + " :\1" + reply + "\1");
				}
				else if (msg.indexOf("VERSION") == 0)
				{
					window.println("<" + x.getNick() + " has requested your version>", chan, C.VIOLET);
					reply = "VERSION wIRC v0.2 <wisteso@gmail.com>";
					s.sendData("NOTICE " + x.getNick() + " :\1" + reply + "\1");
				}
				else if (msg.indexOf("TIME") == 0)
				{
					window.println("<" + x.getNick() + " has requested your local time>", chan, C.VIOLET);
					
					Calendar T = Calendar.getInstance();
					
					reply = "TIME " + T.get(5) + "/" + T.get(2) + "/" + T.get(1) +
						" " + T.get(10) + ":" + T.get(12) + ":" + T.get(13);
						
					if (T.get(9) == 0) reply += " AM";
					else reply += " PM";
					
					s.sendData("NOTICE " + x.getNick() + " :\1" + reply + "\1");
				}
				else if (msg.indexOf("USERINFO") == 0)
				{
					window.println("<" + x.getNick() + " has requested your user info>", chan, C.VIOLET);
					s.sendData("NOTICE " + x.getNick() + " :\1" + reply + "\1");
				}
				else
					System.out.println("!!! " + msg);
			}
			else if (code == C.TOPIC)
			{
				window.println("(TOPIC) " + msg, chan, C.BLUE);
			}
			else if (code == C.ERROR)
			{
				if (x.getMessage().indexOf("Closing Link") > -1)
					s.disconnect(msg);
				else
					window.println("(ERROR) " + msg, chan, C.RED);
			}
			else
			{
				window.println("(" + code + ") " + msg, chan, C.GRAY);
			}
		}
		else if (code >= 0)
		{
			if (code > 0 && code < 7)  // Post-registration greeting.
			{
				if (code < 4)
					window.println("(GREET) " + msg, chan, C.GREEN);
				
				// TODO - Add the rest of the code for the statistic crap here.
			}
			else if (code > 249 && code < 270)  // Misc. information.
			{
				window.println("(INFO) " + msg, chan, C.GRAY);
			}
			else if (code == 332)  // Topic.
			{
				window.println("(TOPIC) " + msg, chan, C.BLUE);
			}
			else if (code == 333 || code == 353 || code == 366)  // Name list.
			{
				if (code == 353)
				{
					SortedListModel l = window.getNickList(x.getChannel());
					
					if (l == null) return;
					
					String t = new String();
					
					for (int c = 0; c < msg.length(); ++c)
					{
						if (msg.charAt(c) > 32)
						{
							t += msg.charAt(c);
						}
						else if (t.length() > 1)
						{
							l.add(t);
							
							if (!users.containsKey(t))
								users.put(t, new User(t, null, x.getChannel()));
							else
								users.get(t).addChans(x.getChannel());
							
							t = new String();
						}
						else
						{
							System.err.println("Invalid name: " + t);
							t = new String();
						}
					}
					l.add(t);
					if (!users.containsKey(t))
						users.put(t, new User(t, null, x.getChannel()));
					else
						users.get(t).addChans(x.getChannel());
				}
			}
			else if (code > 371 && code < 377)  // Message of the day.
			{
				window.println("(MOTD) " + msg, chan, C.GREEN);
				
				if (code == 376)
					sendMsg("/SCRIPT default.script", "Console");
			}
			else if (code == 433)
			{
				String tempNick = "Nullname" + (int)(Math.random() * 10000);
				window.println("(ERROR) Nickname already in use. Nickname set to: " + tempNick, chan, C.RED);
				
				s.sendData("NICK " + tempNick);
				nickName = tempNick;
			}
			else
			{
				window.println("(" + code + ") " + msg, chan, C.GRAY);
			}
		}
	}
	
	protected void disconnect(String reason)
	{
		s.disconnect(reason);
	}
	
	public boolean addUser(String name)
	{
		if (users.containsKey(name))
		{
			return false;
		}
		else
		{
			users.put(name, new User(name));
			return true;
		}
	}
	
	public boolean removeUser(String name)
	{
		if (users.containsKey(name))
		{
			users.remove(name);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean updateUser(String oldName, String newName)
	{
		if (users.containsKey(oldName))
		{
			users.get(oldName).addNick(newName);
			users.put(newName, users.get(oldName));
			users.remove(oldName);
			return true;
		}
		else
		{
			return false;
		}
	}
}