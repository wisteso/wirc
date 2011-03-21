package data;

/**
 * @author Will
 */
public class ServerChannel
{
	public static final ServerChannel CONSOLE = new ServerChannel("localhost", "console");
	public static final ServerChannel DEBUG = new ServerChannel("localhost", "debug");
	
	public final String server;
	public final String channel;
	private final String merged;

	public ServerChannel(String server, String channel)
	{
		// make uniform, lowercase probably
		this.server = server.toLowerCase();
		this.channel = channel.toLowerCase();
		this.merged = this.server + "/" + this.channel;
	}

	@Override
	public String toString()
	{
		return merged;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof ServerChannel)
		{
			ServerChannel castedOther = (ServerChannel)other;
			return merged.equals(castedOther.merged);
		}

		return false;
	}

	@Override
	public int hashCode()
	{
		return merged.hashCode();
	}

	public static ServerChannel parseServerChan(String toString)
	{
		String[] split = toString.split("/");

		return new ServerChannel(split[0], split[1]);
	}
}
