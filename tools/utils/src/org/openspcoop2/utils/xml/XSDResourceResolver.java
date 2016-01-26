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


package org.openspcoop2.utils.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Classe utilizzabile per Risolvere gli import di eventuali schemi
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XSDResourceResolver implements LSResourceResolver {

	private Hashtable<String, byte[]> resources = new Hashtable<String, byte[]>();
	
	public Hashtable<String, byte[]> getResources() {
		return this.resources;
	}
	public XSDResourceResolver(){}
	public XSDResourceResolver(Hashtable<String, byte[]> resources){
		this.resources = resources;
	}
	
	public void addResource(String systemId,byte[] resource){
		this.resources.put(systemId, resource);
	}
	
	public void addResource(String systemId,InputStream resource) throws IOException{
		if(resource==null){
			throw new IOException("InputStream is null");
		}
		byte [] buffer = new byte[org.openspcoop2.utils.Utilities.DIMENSIONE_BUFFER];
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		int letti = 0;
		while( (letti=resource.read(buffer))!=-1 ){
			bout.write(buffer, 0, letti);
		}
		bout.flush();
		bout.close();
		addResource(systemId, bout.toByteArray());
	}
	
	@Override
	public LSInput resolveResource(String type, String namespaceURI,
			String publicId, String systemId, String baseURI) {
		
		try{
		
			//System.out.println("RICHIESTO type["+type+"] namespaceURI["+namespaceURI+"] publicId["+publicId+"]" +
			//		" systemId["+systemId+"] baseURI["+baseURI+"]");
			
			// Algoritmo:
			// 1. Cerco con systemId completo		
			byte[] resource = this.resources.get(systemId);
			
			if(resource==null){
				
				String baseName = null;
				String parentName = null;
				if(systemId.startsWith("http://") || systemId.startsWith("https://") || systemId.startsWith("file://")){
					URL url = new URL(systemId);
					File fileUrl = new File(url.getFile());
					baseName = fileUrl.getName();
					if(fileUrl.getParentFile()!=null){
						parentName = fileUrl.getParentFile().getName();
					}
				}
				else{
					File f = new File(systemId);
					baseName = f.getName();
					if(f.getParentFile()!=null){
						parentName = f.getParentFile().getName();
					}
				}
				
				//System.out.println("NON TROVATO, cerco con (BASE["+baseName+"] PARENT["+parentName+"])");
				
				if(parentName!=null){
					//System.out.println("NON TROVATO, cerco con parent["+parentName+"]/baseName["+baseName+"]");
					resource = this.resources.get(parentName+"/"+baseName);
				}
				
				if(resource==null){
					///System.out.println("NON TROVATO, cerco con solo base name ["+baseName+"]");
					resource = this.resources.get(baseName);
				}
				
				if(resource==null){
					if(baseURI!=null){
						//System.out.println("NON TROVATO, cerco con baseURI ["+baseURI+"]");
						String ricerca = null;
						if(baseURI.startsWith("http://") || baseURI.startsWith("file://")){	
							URL url = new URL(baseURI);
							File fileUrl = new File(url.getFile());
							String baseNameParent = fileUrl.getName();
							if(baseURI.length()>baseNameParent.length()){
								String prefix = baseURI.substring(0, baseURI.length()-baseNameParent.length());
								ricerca = prefix + baseName;
							}
						}
						else{
							File f = new File(baseURI);
							if(f.getParentFile()!=null){
								String prefix = f.getParentFile().getAbsolutePath();
								ricerca = prefix + File.separatorChar + baseName;
							}
						}
						//System.out.println("RICERCO COME ["+ricerca+"]");
						resource = this.resources.get(ricerca);
					}
				}
			}
			
			if(resource!=null){
				//System.out.println("TROVATO ["+new String(resource)+"]");
				return new LSInputImpl(type, namespaceURI, publicId, systemId, baseURI, resource);
			}
			else{
				//System.out.println("NON TROVATO");
				throw new Exception("non trovata tra le risorse registrate (type["+type+"] namespaceURI["+namespaceURI+"] publicId["+publicId+"] systemId["+systemId+"] baseURI["+baseURI+"])");
			}
			
		}catch(Exception e){
			throw new RuntimeException("Risoluzione risorsa non riuscita: "+e.getMessage(),e);
		}
		
	}

}
