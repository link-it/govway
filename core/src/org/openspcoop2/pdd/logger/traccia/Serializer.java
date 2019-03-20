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

package org.openspcoop2.pdd.logger.traccia;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.utils.json.JacksonJsonUtils;
import org.openspcoop2.utils.json.JacksonXmlUtils;
import org.openspcoop2.utils.service.beans.TransazioneBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**	
 * Serializer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Serializer {

	private boolean prettyPrint = false;
	private String xml_namespace = "http://govway.org/traccia";
	private String xml_localName = "traccia";
	
	public Serializer() {		
	}
	public Serializer(Properties pConf) throws TracciaException {
		try {
			Field [] fields = Serializer.class.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				fieldName = fieldName.replace("_", ".");
				if(pConf.containsKey(fieldName)) {
					String value = pConf.getProperty(fieldName);
					if(boolean.class.getName().equals(field.getType().getName())) {
						field.set(this, "true".equalsIgnoreCase(value));
					}else {
						field.set(this, value);
					}
				}
			}
		}catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
		
	}
	
	
	public byte[] toJsonByteArray(TransazioneBase transazione) throws TracciaException {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.toJson(bout, transazione);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}
		catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
	}
	public String toJson(TransazioneBase transazione) throws TracciaException {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.toJson(bout, transazione);
			bout.flush();
			bout.close();
			return bout.toString();
		}
		catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
	}
	public void toJson(OutputStream os , TransazioneBase transazione) throws TracciaException {
		try {
			JacksonJsonUtils jsonUtils = JacksonJsonUtils.getInstance(this.prettyPrint);
			jsonUtils.writeTo(transazione, os);
		}
		catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
	}
	
	public byte[] toXmlByteArray(TransazioneBase transazione) throws TracciaException {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.toXml(bout, transazione);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}
		catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
	}
	public String toXml(TransazioneBase transazione) throws TracciaException {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			this.toXml(bout, transazione);
			bout.flush();
			bout.close();
			return bout.toString();
		}
		catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
	}
	public void toXml(OutputStream os , TransazioneBase transazione) throws TracciaException {
		try {
			JacksonXmlUtils jacksonXmlUtils = JacksonXmlUtils.getInstance(this.prettyPrint);
			
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			jacksonXmlUtils.writeTo(transazione, bout);
			bout.flush();
			bout.close();
			
			XMLUtils xmlUtils = XMLUtils.getInstance();
			Element element = xmlUtils.newElement(bout.toByteArray());
			Document dom = xmlUtils.newDocument();
			Element elementNew = dom.createElementNS(this.xml_namespace, this.xml_localName);
			NodeList list = element.getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);
				Node nAdopt = dom.adoptNode(n.cloneNode(true));
				elementNew.appendChild(nAdopt);
			}
			xmlUtils.writeTo(elementNew, os);
			
		}
		catch(Exception e) {
			throw new TracciaException(e.getMessage(),e);
		}
	}
	
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}
	public void setXml_namespace(String xml_namespace) {
		this.xml_namespace = xml_namespace;
	}
	public void setXml_localName(String xml_localName) {
		this.xml_localName = xml_localName;
	}
}
