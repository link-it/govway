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

package org.openspcoop2.utils.wadl;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XSDUtils;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * SchemaCallback che riceve gli schemi, presenti nella grammatica, durante la lettura di un wadl
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class SchemaCallback implements org.jvnet.ws.wadl.ast.WadlAstBuilder.SchemaCallback {

	/*
	 * La classe può essere utilizzata pre raccogliere gli schemi direttamente riferiti nel WADL.
	 * Gli altri schemi volendo possono essere aggiunti tramite il metodo 'addResource' 
	 **/
	
	private Hashtable<String, byte[]> resources = new Hashtable<String, byte[]>();
	private Hashtable<String, String> mappingNamespaceLocations = new Hashtable<String, String>();
	private Logger log;
	private AbstractXMLUtils xmlUtils = null;
	private XSDUtils xsdUtils = null;
	private boolean processInclude;
	private boolean processInlineSchema;
	
	public SchemaCallback(Logger log, AbstractXMLUtils xmlUtils, boolean processInclude, boolean processInlineSchema){
		this.log = log;
		this.xmlUtils = xmlUtils;
		this.xsdUtils = new XSDUtils(this.xmlUtils);
		this.processInclude = processInclude;
		this.processInlineSchema = processInlineSchema;
	}
	
	@Override
	public void processSchema(InputSource isSource) {
		if(this.processInclude){
			try{
			
				String publicId = isSource.getPublicId();
				String systemId = isSource.getSystemId();
				byte[] resource = Utilities.getAsByteArray(isSource.getByteStream());
			
				this.addResource(this.xsdUtils.getBaseNameXSDLocation(systemId), resource);
				
				if(this.log!=null)
					this.log.debug("SCHEMA CALLBACK (import) ["+publicId+"]  ["+systemId+"] ");
				else
					System.out.println("SCHEMA CALLBACK (import) ["+publicId+"]  ["+systemId+"] ");
				
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}
	}

	@Override
	public void processSchema(String systemId, Element element) {
		if(this.processInlineSchema){
			try{
				
				byte[] resource = this.xmlUtils.toByteArray(element);
			
				this.addResource(this.xsdUtils.getBaseNameXSDLocation(systemId), resource);
				
				if(this.log!=null)
					this.log.debug("SCHEMA CALLBACK (inline) ["+systemId+"] ");
				else
					System.out.println("SCHEMA CALLBACK (inline) ["+systemId+"] ");
				
			}catch(Exception e){
				throw new RuntimeException(e.getMessage(),e);
			}
		}
	}

	public Hashtable<String, byte[]> getResources() {
		return this.resources;
	}

	public Hashtable<String, String> getMappingNamespaceLocations() {
		return this.mappingNamespaceLocations;
	}
	
	public void addResource(String systemId,byte[]resource) throws XMLException{
		
		if(this.resources.containsKey(systemId)==false){
		
			// registro risorsa con systemid
			this.resources.put(systemId, resource);
	
			// registro namespace
			this.xsdUtils.registraMappingNamespaceLocations(resource, systemId, this.mappingNamespaceLocations);
			
		}
		
	} 

}
