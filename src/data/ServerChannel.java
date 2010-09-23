package data;

/**
 * @author Will
 */
public class ServerChannel
{
	public final String server;
	public final String channel;

	public ServerChannel(String server, String channel)
	{
		// make uniform, lowercase probably
		this.server = server;
		this.channel = channel;
	}

	@Override
	public String toString()
	{
		return server + "/" + channel;
	}

	public static ServerChannel parseServerChan(String toString)
	{
		String[] split = toString.split("/");

		return new ServerChannel(split[0], split[1]);
	}
}
