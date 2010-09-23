/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package core;

/**
 *
 * @author Will
 */
public class PluginRegistry
{
//	public String loadPlugin(String path)
//	{
//	    try
//	    {
//			RemoteClassLoader l = new RemoteClassLoader();
//
//	    	Class<?> p = l.findClass(path);
//
//	    	Object o = p.newInstance();
//
//    		Plugin t = (Plugin)o;
//
//	    	plugins.add(t);
//
//	    	String[] msg = t.onLoad();
//
//	    	if (msg != null)
//	    		for (int i = 0; i < msg.length; ++i)
//	    			sendData(msg[i]);
//
//	    	return t.getVersion();
//	    }
//	    catch (InstantiationException e)
//	    {
//	    	e.printStackTrace(System.err);
//	    }
//	    catch (IllegalAccessException e)
//	    {
//	    	e.printStackTrace(System.err);
//	    }
//	    catch (Exception e)
//	    {
//	    	printDebugMsg("Failed to load plugin: " + e.toString());
//	    }
//
//	    return null;
//	}
//
//	public String executeScript(String path)
//	{
//	    try
//	    {
//	    	Scanner in = new Scanner(new File(path));
//
//	    	if (!in.hasNextLine())
//	    		return null;
//
//	    	String header = in.nextLine();
//
//			while (in.hasNextLine())
//				sendMessage(in.nextLine().trim(), CONSOLE);
//
//			return header;
//	    }
//	    catch (Exception e)
//	    {
//	    	printDebugMsg("Failed to execute script:" + e.toString());
//	    }
//
//	    return null;
//	}
}
