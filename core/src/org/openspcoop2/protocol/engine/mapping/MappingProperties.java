/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.protocol.engine.mapping;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * Service Mappings
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MappingProperties {
	private Logger log;
	private Properties propertiesReader; 
	protected MappingProperties(String fileNameProperties,java.io.InputStream properties,Logger log) throws IOException{

		this.log = log;
		this.propertiesReader = new Properties();
		try {  
			this.propertiesReader.load(properties);
			properties.close();
		} catch(IOException e) {
			this.log.error("Riscontrato errore durante la lettura del file '"+fileNameProperties+"': \n\n"+e.getMessage());
			try {
				if(properties!=null)
					properties.close();
			} catch(Exception er) { }
			throw e;
		}	
	}
	
	public String getUrlWithoutContext(String protocol,String urlWithContext,IDService idService) throws ProtocolException{
		IProtocolFactory pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocol);
		Openspcoop2 manifestProtocol = pf.getManifest();
		String urlWithoutContext = null;
		
		String paContext = null;
		if(IDService.PORTA_APPLICATIVA_SOAP.equals(idService)){
			paContext = "/PA";
		}
		else if(IDService.PORTA_APPLICATIVA_API.equals(idService)){
			paContext = "/API/PA";
		}
		else{
			throw new ProtocolException("Service ["+idService+"] non gestito tramite UrlMapping");
		}
		
		// context con un nome
		for (int i = 0; i < manifestProtocol.getWeb().sizeContextList(); i++) {
			String context = manifestProtocol.getWeb().getContext(i);
			String prefixProtocol = context + paContext;
			if(urlWithContext.contains(prefixProtocol)){
				if(urlWithContext.endsWith(prefixProtocol)){
					urlWithoutContext = "";
				}
				else{
					int offset = urlWithContext.indexOf(prefixProtocol);
					urlWithoutContext = urlWithContext.substring(offset+prefixProtocol.length(), urlWithContext.length());
				}
				break;
			}
		}
		// empty context
		if(urlWithoutContext==null){
			if(manifestProtocol.getWeb().getEmptyContext()!=null && manifestProtocol.getWeb().getEmptyContext().getEnabled() ){
				String prefixProtocol = paContext;
				if(urlWithContext.contains(prefixProtocol)){
					if(urlWithContext.endsWith(prefixProtocol)){
						urlWithoutContext = "";
					}
					else{
						int offset = urlWithContext.indexOf(prefixProtocol);
						urlWithoutContext = urlWithContext.substring(offset+prefixProtocol.length(), urlWithContext.length());
					}
				}
			}
		}
		if(urlWithoutContext==null){
			throw new ProtocolException("Identificazione URL senza contesto applicazione e protocollo non riuscita (protocollo ["+ protocol + "], url [" + urlWithContext + "])");
		}
		//System.out.println("URL["+urlWithContext+"] URL_SENZA_CONTEXT["+urlWithoutContext+"]");
		return urlWithoutContext;
	}
	
	protected String getMappingName(String protocol, String urlWithContext,IDService idService) throws ProtocolException{
		
		//devo recuperare tutte le url configurate e vedere se una matcha
		//le proprieta' che mi interessano sono nella forma:
		//protocol.pa.xxxxx.url
		String prefix = protocol + ".pa.";
		String suffix = ".url";		
				
		String urlWithoutContext = this.getUrlWithoutContext(protocol,urlWithContext, idService);
		
		Enumeration<?> keys = this.propertiesReader.keys();
		
		while(keys.hasMoreElements()){
			String propertyName = (String) keys.nextElement(); 
			if(propertyName.startsWith(prefix) && propertyName.endsWith(suffix)){
				//Trovata una URL registrata per il protocollo che mi interessa
				String tmpurl = this.propertiesReader.getProperty(propertyName);
				
				if(tmpurl != null) {
					tmpurl = tmpurl.trim();
				} else { 
					continue;
				}
				
				if(tmpurl.startsWith("/")==false && !tmpurl.equals("*")){
					tmpurl = "/" + tmpurl;
				}
				
//				if(tmpurl.endsWith("/")==false && !tmpurl.equals("*"))
//					tmpurl = tmpurl + "/";
				
				//Verifico se la URL matcha.
				//Due casi: sono uguali o il prefisso e' lo stesso e c'e' la wildcard (*)
				
				boolean match = false;
				if(tmpurl.endsWith("*")){
					match = urlWithoutContext.startsWith(tmpurl.substring(0,tmpurl.length()-1));
				} else {
					match = urlWithoutContext.equals(tmpurl);
				}
				
				if(match){
					//Trovato. Estraggo il nome del mapping.
					//Mi accerto che sia valido, altrimenti continuo nella ricerca.
					String mapping = propertyName.substring(prefix.length(), propertyName.length() - suffix.length());
					if(mapping != null && !mapping.equals(""))
						return mapping;
				}
			}
		}
		throw new ProtocolException("Non risulta configurata nessuna configurazione di mapping nel protocollo ["+ protocol + "] per la url [" + urlWithContext + "]");
	}
	
	protected ModalitaIdentificazione getModalita(String protocol, String mappingName, String attribute) throws ProtocolException{
		String propertyName = protocol + ".pa." + mappingName + "." + attribute;	
		String modalita = this.propertiesReader.getProperty(propertyName);
		if(modalita != null){
			modalita = modalita.trim();
		} else {
			throw new ProtocolException("Nessuna modalita configurata per la proprieta' [" + propertyName + "].");
		}
		ModalitaIdentificazione m = ModalitaIdentificazione.toEnumConstant(modalita);
		if(m==null){
			throw new ProtocolException("La modalita' [" + modalita + "] configurata per la proprieta [" + propertyName + "] non e' supportata.");
		}
		return m;
	}
	
	protected String getValue(String protocol, String mappingName, String attribute) {
		String propertyName = protocol + ".pa." + mappingName + "." + attribute + ".value";	
		String value = this.propertiesReader.getProperty(propertyName);
		if(value != null) 
			value = value.trim();
		return value;
	}
	
	protected String getPattern(String protocol, String mappingName, String attribute) {
		String propertyName = protocol + ".pa." + mappingName + "." + attribute + ".pattern";	
		String value = this.propertiesReader.getProperty(propertyName);
		if(value != null) value = value.trim();
		return value;
	}
	
	protected String getName(String protocol, String mappingName, String attribute) {
		String propertyName = protocol + ".pa." + mappingName + "." + attribute + ".name";	
		String value = this.propertiesReader.getProperty(propertyName);
		if(value != null) value = value.trim();
		return value;
	}
	
	protected String getAnonymous(String protocol, String mappingName, String attribute) {
		String propertyName = protocol + ".pa." + mappingName + "." + attribute + ".anonymous";	
		String value = this.propertiesReader.getProperty(propertyName);
		if(value != null) value = value.trim();
		return value;
	}
	
	protected Boolean isForceWsdlBased(String protocol, String mappingName, String attribute) {
		String propertyName = protocol + ".pa." + mappingName + "." + attribute + ".forceWsdlBased";	
		String value = this.propertiesReader.getProperty(propertyName);
		if(value != null) {
			value = value.trim();
			return Boolean.parseBoolean(value);
		}
		else{
			return null;
		}
	}
}
