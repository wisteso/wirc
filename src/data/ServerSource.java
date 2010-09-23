package data;

/**
 * @author Will
 */
public class ServerSource
{
	public final String nickname;
	public final String origin;
	public final String server;

	public ServerSource(String server, String origin)
	{
		// make uniform, lowercase probably
		this.server = server;

		int index = origin.indexOf("!");

		if (index > 0)
		{
			this.nickname = origin.substring(0, index);
			this.origin = origin.substring(index + 1);
		}
		else
		{
			this.nickname = null;
			this.origin = origin;
		}
	}

	@Override
	public String toString()
	{
		if (nickname != null)
			return nickname + "!" + origin + "@" + server;
		else
			return origin + "@" + server;
	}
}
