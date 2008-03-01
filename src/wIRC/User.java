package wIRC;
import java.util.ArrayList;

/**
 * User-data structural-object
 * <br><br>
 * This class holds server-wide data about a user.
 * <br><br>
 * @author wisteso@gmail.com
 */
public class User 
{
	private ArrayList<String> nickNames = new ArrayList<String>();
	private ArrayList<String> hostNames = new ArrayList<String>();
	private ArrayList<String> realNames = new ArrayList<String>();
	private ArrayList<String> chanNames = new ArrayList<String>();
	
	//private int status = 0;
	
	public User(String nick)
	{
		nickNames.add(nick.toLowerCase());
	}
	
	public User(String nick, String host)
	{
		nickNames.add(nick.toLowerCase());
		hostNames.add(host.toLowerCase());
	}
	
	public User(String nick, String host, String chan)
	{
		nickNames.add(nick.toLowerCase());
		if (host != null) hostNames.add(host.toLowerCase());
		chanNames.add(chan.toLowerCase());
	}
	
	protected String[] getNicks()
	{
		String[] t = new String[nickNames.size()];
		
		for (int x = 0; x < nickNames.size(); ++x)
			t[x] = nickNames.get(x);
		
		return t;
	}
	
	protected String[] getHosts()
	{
		String[] t = new String[hostNames.size()];
		
		for (int x = 0; x < hostNames.size(); ++x)
			t[x] = hostNames.get(x);
		
		return t;
	}
	
	protected String[] getReals()
	{
		String[] t = new String[hostNames.size()];
		
		for (int x = 0; x < hostNames.size(); ++x)
			t[x] = hostNames.get(x);
		
		return t;
	}
	
	protected String[] getChans()
	{
		String[] t = new String[chanNames.size()];
		
		for (int x = 0; x < chanNames.size(); ++x)
			t[x] = chanNames.get(x);
		
		return t;
	}
	
	protected void addNick(String nick)
	{
		nickNames.add(nick.toLowerCase());
	}
	
	protected void addHost(String host)
	{
		hostNames.add(host.toLowerCase());
	}
	
	protected void addReal(String real)
	{
		realNames.add(real.toLowerCase());
	}
	
	protected void addChans(String chan)
	{
		realNames.add(chan.toLowerCase());
	}
}