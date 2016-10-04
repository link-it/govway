/*
 * OpenSPCoop - Customizable API Gateway 
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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.jvnet.ws.wadl.ast.ApplicationNode;
import org.jvnet.ws.wadl.ast.InvalidWADLException;
import org.jvnet.ws.wadl.ast.ResourceTypeNode;
import org.jvnet.ws.wadl.ast.WadlAstBuilder;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.w3c.dom.Document;

/**
 * WADLReader
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WADLReader {
	// concettualmente è come si estenda il builder di wadl
	//extends org.jvnet.ws.wadl.ast.WadlAstBuilder {

	private org.openspcoop2.utils.wadl.SchemaCallback schemaCallback;
	private boolean processInclude;
	private org.openspcoop2.utils.wadl.MessageListener messageListener;
	private org.jvnet.ws.wadl.ast.WadlAstBuilder wadlBuilder;
	private AbstractXMLUtils xmlUtils;
	private WADLUtilities wadlUtilities;
	private Logger log;
	
	public WADLReader(Logger log,AbstractXMLUtils xmlUtils,boolean verbose,boolean processInclude, boolean processInlineSchema){
		this.log = log;
		this.xmlUtils = xmlUtils;
		this.wadlUtilities = new WADLUtilities(this.xmlUtils);
		
		this.schemaCallback = new org.openspcoop2.utils.wadl.SchemaCallback(this.log, this.xmlUtils, processInclude, processInlineSchema);
		this.processInclude = processInclude;
		
		this.messageListener = new org.openspcoop2.utils.wadl.MessageListener(this.log, verbose, true);
		
		this.wadlBuilder = new WadlAstBuilder(this.schemaCallback, this.messageListener);
	}

	public ApplicationNode readWADL(String file) throws InvalidWADLException,
	IOException {
		return this.readWADL(new File(file));
	}
	
	public ApplicationNode readWADL(File file) throws InvalidWADLException,
	IOException {
		File fTpm = null;
		try{
			if(this.processInclude){
				return this.wadlBuilder.buildAst(file.toURI());
			}
			else{
				byte[]wadl = FileSystemUtilities.readBytesFromFile(file);
				Document d = this.xmlUtils.newDocument(wadl);
				this.wadlUtilities.removeIncludes(d);
				fTpm = File.createTempFile("wadl", "tmp");
				this.xmlUtils.writeTo(d, fTpm);
				return this.wadlBuilder.buildAst(fTpm.toURI());
			}
			
		}catch(InvalidWADLException e){
			throw e; 
		}catch(IOException e){
			throw e; 
		}catch(Exception e){
			throw new IOException(e.getMessage(),e); 
		}finally{
			if(fTpm!=null)
				fTpm.delete();
		}
	}
	
	public ApplicationNode readWADL(URI uri) throws InvalidWADLException,
			IOException {
		if(this.processInclude){
			return this.wadlBuilder.buildAst(uri);
		}
		else{
			return this.readWADL(new File(uri));
		}
	}

	public Map<String, ResourceTypeNode> getInterfaceMap() {
		return this.wadlBuilder.getInterfaceMap();
	}
	
	public Hashtable<String, byte[]> getResources() {
		return this.schemaCallback.getResources();
	}

	public Hashtable<String, String> getMappingNamespaceLocations() {
		return this.schemaCallback.getMappingNamespaceLocations();
	}
}
