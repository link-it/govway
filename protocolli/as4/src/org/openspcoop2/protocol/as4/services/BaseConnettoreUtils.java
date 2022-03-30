/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.as4.services;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.openspcoop2.utils.threads.RunnableLogger;

/**
 * AbstractConnettoreUtils
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BaseConnettoreUtils {

	protected RunnableLogger log;
	public BaseConnettoreUtils(RunnableLogger log) {
		this.log = log;
	}
	
	public void debug(Message mapParam) throws Exception {
		if(mapParam instanceof MapMessage) {
			
			MapMessage map = (MapMessage) mapParam;
			
			Enumeration<?> mapNames = map.getMapNames();
			while (mapNames.hasMoreElements()) {
				Object name = (Object) mapNames.nextElement();
				if(name instanceof String) {
					String key = (String) name;
					Object value = map.getObjectProperty(key);
					if(value!=null) {
						this.log.debug("\t-Map["+key+"]("+value.getClass().getName()+"): "+value);
					}
					else {
						byte[] bytes = map.getBytes(key);
						if(bytes!=null) {
							File f = File.createTempFile("content", ".bin");
							FileOutputStream fos =new FileOutputStream(f);
							fos.write(bytes);
							fos.flush();
							fos.close();
							this.log.debug("\t-Map["+key+"] scritto in "+f.getAbsolutePath());
						}
						else {
							this.log.debug("\t-Map["+key+"]: "+value);
						}
					}
				}
				else {
					this.log.debug("\t-Map con key diverso dal tipo String: "+name);
				}
			}
		}
		else if(mapParam instanceof TextMessage) {
			
			TextMessage map = (TextMessage) mapParam;
			
			String text = map.getText();
			this.log.debug("Ricevuto text: "+text);
			
		}
		
		this.log.debug("Ricevuto msg: "+mapParam.getJMSMessageID());
		this.log.debug("Ricevuto msg: "+mapParam.getClass().getName());
		Enumeration<?> en = mapParam.getPropertyNames();
		while (en.hasMoreElements()) {
			Object name = (Object) en.nextElement();
			if(name instanceof String) {
				String key = (String) name;
				Object value = mapParam.getObjectProperty(key);
				if(value!=null) {
					this.log.debug("\t-Property["+key+"]("+value.getClass().getName()+"): "+value);
				}
				else {
					this.log.debug("\t-Property["+key+"]: "+value);
				}
			}
			else {
				this.log.debug("\t-Property con key diverso dal tipo String: "+name);
			}
		}
	}

	protected String getPropertyJms(Message map, String property, boolean required) throws Exception {
		Object value = map.getObjectProperty(property);
		if(value==null) {
			if(required) {
				throw new Exception("Property '"+property+"' not found");
			}
			else {
				return null;
			}
		}
		if(!(value instanceof String)) {
			throw new Exception("Property '"+property+"' with wrong type (expected:String found:"+value.getClass().getName()+")");
		}
		return (String) value;
	}
	protected Integer getIntPropertyJms(Message map, String property, boolean required) throws Exception {
		Object value = map.getObjectProperty(property);
		if(value==null) {
			if(required) {
				throw new Exception("Property '"+property+"' not found");
			}
			else {
				return null;
			}
		}
		if(!(value instanceof Integer)) {
			throw new Exception("Property '"+property+"' with wrong type (expected:Integer found:"+value.getClass().getName()+")");
		}
		return (Integer) value;
	}
}
