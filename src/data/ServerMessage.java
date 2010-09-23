package data;

/**
 * @author Will
 */
public class ServerMessage
{
	public final String server;
	public final String message;

	public ServerMessage(String server, String message)
	{
		if (message == null || message.isEmpty())
			throw new IllegalArgumentException("Cannot have null/empty message.");

		this.server = server;
		this.message = message;
	}

	@Override
	public String toString()
	{
		return server + " " + message;
	}

}
