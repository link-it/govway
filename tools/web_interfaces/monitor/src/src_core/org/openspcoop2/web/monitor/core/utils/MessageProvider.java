package org.openspcoop2.web.monitor.core.utils;

import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class MessageProvider extends HashMap{
	
	private static final long serialVersionUID = -1658180152509756791L;
	private MessageManager msgMgr;
	public MessageProvider() {
	}

	@Override
	public Object get(Object key) {
		try {
			return this.msgMgr.getMessage((String)key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public void setMsgMgr(MessageManager msgMgr) {
		this.msgMgr = msgMgr;
	}

	public MessageManager getMsgMgr() {
		return this.msgMgr;
	}

}
