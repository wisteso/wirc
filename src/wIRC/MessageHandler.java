/**
 * 
 */
package wIRC;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import wIRC.interfaces.MessageParser;

/**
 * @author Victor
 * 
 */
public class MessageHandler {

	private Map<Integer, List<MessageParser>> parserMap;
	private MessageParser unkownHandler;

	public MessageHandler(MessageParser unkownHandler) {
		parserMap = new HashMap<Integer, List<MessageParser>>();
		this.unkownHandler = unkownHandler;
	}

	public void addParser(Integer code, MessageParser par) {
		if (parserMap.containsKey(code)) {
			parserMap.get(code).add(par);
		} else {
			LinkedList<MessageParser> parl = new LinkedList<MessageParser>();
			parl.add(par);
			parserMap.put(code, parl);
		}
	}

	public boolean removeParser(MessageParser par) {
		List<MessageParser> list = null;
		for (Integer code : parserMap.keySet()) {
			for (MessageParser temppar : parserMap.get(code)) {
				if (temppar.equals(par)) {
					list = parserMap.get(code);
				}
			}
		}
		if (list != null) {
			list.remove(par);
			return true;
		}
		return false;
	}

	public void ParseMessage(Message mesg) {
		int code = mesg.getCode();
		if (parserMap.containsKey(code)) {
			for (MessageParser mp : parserMap.get(code)) {
				mp.parseMessage(mesg);
			}
		} else {
			unkownHandler.parseMessage(mesg);
		}
	}

}
