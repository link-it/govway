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
/**
 * 
 */
package org.openspcoop2.protocol.as4.pmode.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.protocol.as4.pmode.TranslatorPropertiesDefault;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Properties {

	private String propertyDefault;
	private List<Property> property;
	
	private String propertySetDefault;
	private List<PropertySet> propertySet;
	
	public Properties(List<byte[]> contents) throws Exception {

		TranslatorPropertiesDefault translator = TranslatorPropertiesDefault.getTranslator();
		
		this.propertyDefault = translator.getPropertyDefault(true);
		this.propertySetDefault = translator.getPropertySetDefault();
		
		this.property = new ArrayList<>();
		this.propertySet = new ArrayList<>();

		List<eu.domibus.configuration.Property> listPropertyDefault = translator.getListPropertyDefault();
		List<eu.domibus.configuration.PropertySet> listPropertySetDefault = translator.getListPropertySetDefault();
		
		Map<String, Property> propertyMap = new HashMap<>();
		Map<String, PropertySet> propertySetMap = new HashMap<>();
		
		for(byte[] content: contents) {
			
			Document doc = MessageXMLUtils.DEFAULT.newDocument(content);
			doc.getDocumentElement().normalize();
	
			NodeList propertyList = doc.getElementsByTagName("property");
			for (int temp = 0; temp < propertyList.getLength(); temp++) {
				Node node = propertyList.item(temp);
				Property property = new Property(node);
				
				if(listPropertyDefault!=null && listPropertyDefault.size()>0) {
					for (eu.domibus.configuration.Property propertyDefault : listPropertyDefault) {
						if(propertyDefault.getName().equals(property.getName())) {
							throw new Exception("La proprietà con nome ["+property.getName()+"] è già presente nella configurazione di default; modificare il nome");
						}
					}
				}
				
				if(propertyMap.containsKey(property.getName())) {
					if(!propertyMap.get(property.getName()).equals(property)) {
						throw new Exception("La proprietà con nome ["+property.getName()+"] risulta utilizzato su più accordi");
					}
				}
				propertyMap.put(property.getName(), property);
			}
	
			NodeList propertySetList = doc.getElementsByTagName("propertySet");
			for (int temp = 0; temp < propertySetList.getLength(); temp++) {
				Node node = propertySetList.item(temp);
				PropertySet propertySet = new PropertySet(node);

				if(listPropertySetDefault!=null && listPropertySetDefault.size()>0) {
					for (eu.domibus.configuration.PropertySet propertySetDefault : listPropertySetDefault) {
						if(propertySetDefault.getName().equals(propertySet.getName())) {
							throw new Exception("L'insieme di proprietà (property-set) con nome ["+propertySet.getName()+"] è già presente nella configurazione di default; modificare il nome");
						}
					}
				}
				
				if(propertySetMap.containsKey(propertySet.getName())) {
					if(!propertySetMap.get(propertySet.getName()).equals(propertySet)) {
						throw new Exception("L'insieme di proprietà (property-set) con nome ["+propertySet.getName()+"] risulta utilizzato su più accordi");
					}
				}
				propertySetMap.put(propertySet.getName(), propertySet);
			}
		}
		
		this.property.addAll(propertyMap.values());
		this.propertySet.addAll(propertySetMap.values());
	}
	
	public List<Property> getProperty() {
		return this.property;
	}
	public void setProperty(List<Property> property) {
		this.property = property;
	}
	public List<PropertySet> getPropertySet() {
		return this.propertySet;
	}
	public void setPropertySet(List<PropertySet> propertySet) {
		this.propertySet = propertySet;
	}
	
	public String getPropertyDefault() {
		return this.propertyDefault;
	}
	public void setPropertyDefault(String propertyDefault) {
		this.propertyDefault = propertyDefault;
	}
	public String getPropertySetDefault() {
		return this.propertySetDefault;
	}
	public void setPropertySetDefault(String propertySetDefault) {
		this.propertySetDefault = propertySetDefault;
	}
}
