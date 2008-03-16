package wIRC;
import SortedListModel.SortedListModel;
import wIRC.interfaces.UserInput;
import java.util.Scanner;

/**
 * Default CLI object
 * <br><br>
 * This class handles the input and output from the user
 * via the command line interface and is not critical 
 * to the core operation.
 * <br><br>
 * @author 	wisteso@gmail.com
 */
public class DefaultCLI implements UserInput
{
	private Manager m;
	
	private String focus = "Console";
	
	public DefaultCLI(String subtitle, Manager source)
	{
		m = source;
		
		Thread io = new Thread()
		{
			Scanner in = new Scanner(System.in);
			String input = new String();
			int i;
			
			public void run()
			{
				while (!input.equalsIgnoreCase("/QUIT"))
				{
					input = in.nextLine();
					
					if (input.length() > 0)
					{
						i = input.toUpperCase().indexOf("/FOCUS ");
						
						if (i == 0)
						{
							i = input.indexOf(" ");
							
							focus = input.substring(i + 1);
							
							i = focus.indexOf(" ");
							
							if (i > -1)
								focus = focus.substring(0, i);
						}
						else
						{
							i = input.toUpperCase().indexOf("/JOIN ");
								
							if (i == 0)
							{
								focus = focus.substring(i + 1).trim();
								
								i = focus.indexOf(" ");
								
								if (i > -1)
									focus = focus.substring(0, i);
							}
							
							m.sendMsg(input, focus);
						}
					}
				}
				
				Main.sendData("QUIT :program terminated");
				
				Main.disconnect("termination via interface");
			}
		};
		
		io.start();
	}
	
	public Object[] addChat(String title)
	{
		focus = title;
		
		return null;
	}
	
	public boolean removeChat(String title)
	{
		if (focus.equals(title))
			focus = "Console";
		
		return true;
	}
	
	public String getFocusedChat()
	{
		return focus;
	}
	
	public void println(String input, int style)
	{
		this.println(input, "Console", style);
	}
	
	public void println(String input, String channel, int style)
	{
		System.out.print("\n(" + channel + ") " + input);
	}
	
	public void print(String input, String channel, int style)
	{
		System.out.print(input);
	}
	
	public void addNicks(String channel, String... nicks)
	{
		
	}
	
	public void removeNicks(String channel, String... nicks)
	{
		
	}
	
	public void removeNick(String nick)
	{
		
	}
	
	public void replaceNick(String oldNick, String newNick)
	{
		
	}
	
	public SortedListModel getNickList(String channel)
	{
		return null;
	}
}
