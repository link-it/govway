/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.message.soap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.SOAPMessage;

import org.openspcoop2.message.OpenSPCoop2MessageProperties;

/**
 * OpenSPCoop2MessageProperties
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2MessageMimeHeaderProperties extends OpenSPCoop2MessageProperties {

	private SOAPMessage soapMessage;
	protected OpenSPCoop2MessageMimeHeaderProperties(SOAPMessage soapMessage, OpenSPCoop2MessageProperties original) {
		super();
		this.soapMessage = soapMessage;
		
		// inizializzo eventuali header gia' inseriti
		if(original!=null && original.size()>0) {
			Map<String, List<String>> p = original.map();
			Iterator<String> keys = p.keySet().iterator();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				List<String> values = p.get(key);
				if(values!=null && !values.isEmpty()) {
					for (String value : values) {
						super.addProperty(key, value);
						this.soapMessage.getMimeHeaders().addHeader(key, value);		
					}
				}
			}
		}
	}
	
	@Override
	public void addProperty(String key,String value){
		super.addProperty(key, value);
		this.soapMessage.getMimeHeaders().addHeader(key, value);
	}

}
