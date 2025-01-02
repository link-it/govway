/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.as4.pmode;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.protocol.as4.config.AS4Properties;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.resources.TemplateUtils;

import eu.domibus.configuration.Property;
import eu.domibus.configuration.PropertySet;
import freemarker.template.Template;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class TranslatorPropertiesDefault {

	private static TranslatorPropertiesDefault translatorInstance;
	private static synchronized void initTranslator() throws ProtocolException {
		if(translatorInstance==null) {
			translatorInstance = new TranslatorPropertiesDefault();
		}
	}
	public static TranslatorPropertiesDefault getTranslator() throws ProtocolException {
		if(translatorInstance==null) {
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (TranslatorPropertiesDefault.class) {
				initTranslator();
			}
		}
		return translatorInstance;
	}
	
	
	
	private byte[] propertyDefault = null;
	private Template templatePropertyDefault;
	
	private byte[] payloadPropertySetDefault = null;
	private Template templatePropertySetDefault;
	
	private TranslatorPropertiesDefault() throws ProtocolException {
		try {
			AS4Properties props = AS4Properties.getInstance();
			
			this.propertyDefault = props.getPropertiesDefault();
			if(this.propertyDefault==null) {
				this.templatePropertyDefault = TemplateUtils.getTemplate("/org/openspcoop2/protocol/as4/pmode", "pmode-propertyDefault.ftl");	
			}
			
			this.payloadPropertySetDefault = props.getPropertiesSetDefault();
			if(this.payloadPropertySetDefault==null) {
				this.templatePropertySetDefault = TemplateUtils.getTemplate("/org/openspcoop2/protocol/as4/pmode", "pmode-propertySetDefault.ftl");
			}
			
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	// PROPERTY DEFAULT
	
	public String getPropertyDefault(boolean deleteValue) throws ProtocolException {
		try {
			if(deleteValue) {
				List<Property> list  = this.getListPropertyDefault();
				StringBuilder bf = new StringBuilder();
				if(list!=null && !list.isEmpty()) {
					for (Property property : list) {
						bf.append("\t\t\t<property name=\"");
						bf.append(property.getName());
						bf.append("\"\n");
						bf.append("\t\t\t\t\tkey=\"");
						bf.append(property.getKey());
						bf.append("\"\n");
						bf.append("\t\t\t\t\tdatatype=\"");
						bf.append(property.getDatatype());
						bf.append("\"\n");
						bf.append("\t\t\t\t\trequired=\"");
						bf.append(property.getRequired());
						bf.append("\"/>\n");
						bf.append("\n");
						
					}
				}
				return bf.toString();
			}
			else {
				if(this.propertyDefault==null) {
					/**System.out.println("================ getPropertyDefault ==================");*/
					Map<String, Object> map = new HashMap<>();
					StringWriter writer = new StringWriter();
					this.templatePropertyDefault.process(map, writer);
					/**System.out.println("LETTO: "+writer.toString());*/
					return writer.toString();
				}
				else {
					return new String(this.propertyDefault);
				}
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	public List<Property> getListPropertyDefault() throws ProtocolException {
		try {
			
			BufferedReader br = new BufferedReader(new StringReader(this.getPropertyDefault(false)));
			List<Property> list = new ArrayList<>();
			String line;
			StringBuilder bf = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append(line);
				if(line.contains("</property>")) {
					String finalString = bf.toString();
					if(finalString!=null && !"".equals(finalString)) {
						String sWithNamespace = finalString.replace("<property>", 
								"<ns:property xmlns:ns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\">");
						sWithNamespace = sWithNamespace.replace("<property ", 
								"<ns:property xmlns:ns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\" ");
						sWithNamespace = sWithNamespace.replace("</property","</ns:property"); 
						/**System.out.println("Deserializzo ["+sWithNamespace+"]");*/
						
						eu.domibus.configuration.utils.serializer.JaxbDeserializer deserializer = new eu.domibus.configuration.utils.serializer.JaxbDeserializer();
						Property p = deserializer.readProperty(sWithNamespace.getBytes());
						/**System.out.println("Test ["+p.getName()+"] ValueNotNull["+p.getValue()!=null+"]");*/
						list.add(p);
					}
					bf = new StringBuilder();
				}
			}
			return list;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	
	// PROPERTY SET DEFAULT
	
	public String getPropertySetDefault() throws ProtocolException {
		try {
			if(this.payloadPropertySetDefault==null) {
				/**System.out.println("================ getPropertySetDefault ==================");*/
				Map<String, Object> map = new HashMap<>();
				StringWriter writer = new StringWriter();
				this.templatePropertySetDefault.process(map, writer);
				/**System.out.println("LETTO: "+writer.toString());*/
				return writer.toString();
			}
			else {
				return new String(this.payloadPropertySetDefault);
			}
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public List<PropertySet> getListPropertySetDefault() throws ProtocolException {
		try {
			
			BufferedReader br = new BufferedReader(new StringReader(this.getPropertySetDefault()));
			List<PropertySet> list = new ArrayList<>();
			String line;
			StringBuilder bf = new StringBuilder();
			while ((line = br.readLine()) != null) {
				if(bf.length()>0) {
					bf.append("\n");
				}
				bf.append(line);
				if(line.contains("</propertySet>")) {
					String finalString = bf.toString();
					if(finalString!=null && !"".equals(finalString)) {
						String sWithNamespace = 
								finalString.replace("<propertySet>", "<ns:propertySet xmlns:ns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\">");
						sWithNamespace = sWithNamespace.replace("<propertySet ", 
								"<ns:propertySet xmlns:ns=\""+eu.domibus.configuration.utils.ProjectInfo.getInstance().getProjectNamespace()+"\" ");
						sWithNamespace = sWithNamespace.replace("</propertySet","</ns:propertySet"); 
						/**System.out.println("Deserializzo ["+sWithNamespace+"]");*/
						
						eu.domibus.configuration.utils.serializer.JaxbDeserializer deserializer = new eu.domibus.configuration.utils.serializer.JaxbDeserializer();
						PropertySet p = deserializer.readPropertySet(sWithNamespace.getBytes());
						/**System.out.println("Test ["+p.getName()+"] size["+p.getPropertyRefList().size()+"]");*/
						list.add(p);
					}
					bf = new StringBuilder();
				}
			}
			return list;
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}

	
	// PAYLOAD PROFILES DEFAULT COMPLETE
	
	public String getAsStringPropertiesDefaultAsCompleteXml(boolean deleteValue) throws ProtocolException {
		StringBuilder bf = new StringBuilder();
		
		bf.append("\t<properties>\n\n");
		
		bf.append(this.getPropertyDefault(deleteValue));
		
		bf.append("\n\n");
		
		bf.append(this.getPropertySetDefault());
		
		bf.append("\n\n");
		bf.append("\t</properties>");
		return bf.toString();
	}
	public byte[] getPropertiesDefaultAsCompleteXml(boolean deleteValue) throws ProtocolException {
		try {
			return getAsStringPropertiesDefaultAsCompleteXml(deleteValue).getBytes();
		}catch(Exception e) {
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	

	
}
