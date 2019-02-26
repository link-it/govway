/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.sdk.transaction;

import java.util.HashMap;
import java.util.Set;

import org.openspcoop2.monitor.sdk.constants.ContentResourceNames;
import org.openspcoop2.monitor.sdk.constants.MessageType;

/**
 * TransportHeaderResource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransportHeaderResource extends AbstractContentResource {
	
	private HashMap<String,String> header = null;
	public TransportHeaderResource(MessageType messageType) {
		super(messageType);
		this.header = new HashMap<String, String>();
	}
	
	public void setProperty(String name, String value) {
		this.header.put(name, value);
	}

	public void setValue(HashMap<String,String> header) {
		this.header = header;
	}
	

	@Override
	public String getName() {
		if (this.isRequest())
			return ContentResourceNames.REQ_TRANSPORT_HEADER;
		else
			return ContentResourceNames.RES_TRANSPORT_HEADER;
	}
	
	public String getProperty(String key) {
		return this.header.get(key);
	}
	
	@Override
	public HashMap<String,String> getValue() {
		return this.header;
	}
	
	public Set<String> keys(){
		return this.header.keySet();
	}



}
