/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.web.monitor.core.utils;

import java.util.HashMap;

/**
 * MessageProvider
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
