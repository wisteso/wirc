
package handlers.input;

import handlers.InputHandler;
import core.Manager;
import data.ServerChannel;
import data.ServerSource;

/**
 *
 * @author Will
 */
public class NamesHandler extends InputHandler
{
	private static final String[] HOOKS = {"333", "353", "366"};

	public NamesHandler(Manager mgr)
	{
		super(mgr);
	}

	@Override
	public String[] getHooks()
	{
		return HOOKS;
	}

	@Override
	public void process(String msg, ServerSource source)
	{
		// currently ignoring 333 (admin of chan?) and 366 (end of list)

		int cmd = Integer.parseInt(msg.substring(0, msg.indexOf(" ")));

		if (cmd == 353)
		{
			// Supported formats seen:
			// :serverhostname 353 Wisteso = #channel :Wisteso othernick1 othernick2
			// :serverhostname 353 Wisteso * #channel :Wisteso othernick1 othernick2

			int chanStartIndex = msg.indexOf("#");
			int chanEndIndex = msg.indexOf(" ", chanStartIndex + 2);
			String channel = msg.substring(chanStartIndex, chanEndIndex);

			int bodyIndex = msg.indexOf(":", chanEndIndex) + 1;
			String names = msg.substring(bodyIndex);

			if (chanEndIndex > bodyIndex)
				throw new RuntimeException("Malformed topic message");

			ServerChannel temp = new ServerChannel(source.server, channel);
			
			getManager().addNames(temp, names.split("\\s"));
		}

//		if (cmd == 353)
//		{
//			SortedListModel l = window.getNickList(channel);
//
//			ArrayList<String> tList = new ArrayList<String>();
//
//			if (l == null) return;
//
//			String t = new String();
//			int i;
//
//			if ((i = arguments.indexOf(" :")) > -1)
//			{
//				temp = arguments.substring(i + 2);
//			}
//			else if ((i = arguments.indexOf(" ")) > -1)
//			{
//				temp = arguments.substring(i + 1);
//			}
//			else
//			{
//				temp = arguments;
//			}
//
//			for (int c = 0; c < temp.length(); ++c)
//			{
//				if (temp.charAt(c) > 32)
//				{
//					t += temp.charAt(c);
//				}
//				else if (t.length() > 1)
//				{
//					tList.add(t);
//
//					t = new String();
//				}
//				else
//				{
//					getManager().printDebugMsg("Invalid name: " + t);
//					t = new String();
//				}
//			}
//
//			tList.add(t);
//
//			Object[] tListObjs = tList.toArray();
//
//			for (int j = 0; j < tList.size(); ++j)
//			{
//				if (!l.contains(tListObjs[j]))
//					l.addElement(tListObjs[j]);
//			}
//
//			//l.addElement(tList.toArray(new String[tList.size()]));
//		}
	}
}
