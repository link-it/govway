/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.Enumeration;
import java.util.Properties;

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
			Properties p = original.getAsProperties();
			Enumeration<?> en = p.keys();
			while (en.hasMoreElements()) {
				Object object = (Object) en.nextElement();
				if(object instanceof String) {
					String key = (String) object;
					String value = p.getProperty(key);
					super.addProperty(key, value);
					this.soapMessage.getMimeHeaders().addHeader(key, value);
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
