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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.validation.Schema;

import org.slf4j.Logger;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* XSDSchemaCollection
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class XSDSchemaCollection {

	private byte[] schemaRoot;
	private Hashtable<String, byte[]> resources;
	private Hashtable<String, String> mappingNamespaceLocations;
	
	private boolean serializeXSDSchemi_buildSchemaSuccess = false;
	private boolean serializeXSDSchemi_buildSchemaError = true;
	
	
	public boolean isSerializeXSDSchemi_buildSchemaSuccess() {
		return this.serializeXSDSchemi_buildSchemaSuccess;
	}
	public void setSerializeXSDSchemi_buildSchemaSuccess(boolean serializeXSDSchemi_buildSchemaSuccess) {
		this.serializeXSDSchemi_buildSchemaSuccess = serializeXSDSchemi_buildSchemaSuccess;
	}
	public boolean isSerializeXSDSchemi_buildSchemaError() {
		return this.serializeXSDSchemi_buildSchemaError;
	}
	public void setSerializeXSDSchemi_buildSchemaError(boolean serializeXSDSchemi_buildSchemaErrror) {
		this.serializeXSDSchemi_buildSchemaError = serializeXSDSchemi_buildSchemaErrror;
	}
	public byte[] getSchemaRoot() {
		return this.schemaRoot;
	}
	public void setSchemaRoot(byte[] schemaRoot) {
		this.schemaRoot = schemaRoot;
	}
	public Hashtable<String, byte[]> getResources() {
		return this.resources;
	}
	public void setResources(Hashtable<String, byte[]> resources) {
		this.resources = resources;
	}
	public Hashtable<String, String> getMappingNamespaceLocations() {
		return this.mappingNamespaceLocations;
	}
	public void setMappingNamespaceLocations(Hashtable<String, String> mappingNamespaceLocations) {
		this.mappingNamespaceLocations = mappingNamespaceLocations;
	}
	
	public void serialize(Logger log,File file) throws XMLException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(file);
			serialize(log,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public void serialize(Logger log,String fileName) throws XMLException{
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream(fileName);
			serialize(log,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}finally{
			try{
				if(fout!=null)
					fout.close();
			}catch(Exception eClose){}
		}
	}
	
	public byte[] serialize(Logger log) throws XMLException{
		try{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			serialize(log,bout);
			bout.flush();
			bout.close();
			return bout.toByteArray();
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}


	public void serialize(Logger log,OutputStream out) throws XMLException{
	
		ZipOutputStream zipOut = null;
		try{
			zipOut = new ZipOutputStream(out);

			this.zipSerialize(log,zipOut);
			
			zipOut.flush();

		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}finally{
			try{
				if(zipOut!=null)
					zipOut.close();
			}catch(Exception eClose){}
		}
	}
	
	public void zipSerialize(Logger log,ZipOutputStream zipOut) throws XMLException{
		
		try{
			String rootPackageDir = "";
			// Il codice dopo fissa il problema di inserire una directory nel package.
			// Commentare la riga sotto per ripristinare il vecchio comportamento.
			rootPackageDir = "schemi"+File.separatorChar;
			
			String nomeFile = "RootSchema.xsd";
			zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
			zipOut.write(this.schemaRoot);
			
			if(this.resources!=null && this.resources.size()>0){
				Enumeration<String> kesy = this.resources.keys();
				while (kesy.hasMoreElements()) {
					String name = (String) kesy.nextElement();
					
					nomeFile = name;
					zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
					zipOut.write(this.resources.get(name));
					
					Enumeration<String> namespaces = this.mappingNamespaceLocations.keys();
					String namespaceFound = null;
					String locationFound = null;
					while (namespaces.hasMoreElements()) {
						String namespace = (String) namespaces.nextElement();
						String location = this.mappingNamespaceLocations.get(namespace);
						String [] split = location.split(" ");
						if(split!=null){
						for (int i = 0; i < split.length; i++) {
							if(split[i]!=null && split[i].equals(nomeFile)){
								namespaceFound = namespace;
								locationFound = location;
								break;
							}
						}
						if(namespaceFound!=null){
							break;}
						}
					}
						
					if(namespaceFound!=null){
						nomeFile = name+".namespace.txt";
						zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
						String valore = namespaceFound;
						if(locationFound!=null){
							valore = locationFound + "\n" + valore;
						}
						zipOut.write(valore.getBytes());
					}
					
				}
			}
			
			try{
				this._buildSchema(log,false,false);
			}catch(Throwable e){
				log.error("Costruzione Struttura degli Schemi XSD fallita: "+e.getMessage(),e);
				nomeFile = "BuildSchemaFailed.txt";
				zipOut.putNextEntry(new ZipEntry(nomeFile));
				String msg = e.getMessage();
				if(msg==null || msg.equals("")){
					if(e instanceof NullPointerException){
						msg = "Internal Error (NP)";
					}
					else{
						msg = e.toString();
						if(msg==null || msg.equals("")){
							msg = "Internal Error";
						}
					}
				}
				zipOut.write(msg.getBytes());
			}
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	
	/**
	 * Costruisce un unico schema unendo tutti gli schemi importati
	 * 
	 * @param logger logger
	 * @return Schema
	 * @throws XMLException
	 */
	public Schema buildSchema(Logger logger) throws XMLException {
		return this._buildSchema(logger, this.serializeXSDSchemi_buildSchemaSuccess, this.serializeXSDSchemi_buildSchemaError);
	}
	private Schema _buildSchema(Logger logger, boolean serializeXSDSchemi_buildSchemaSuccess, boolean serializeXSDSchemi_buildSchemaErrror) throws XMLException {
		
		// Creo XSDResolver con le risorse localizzate e procedo con la validazione
		XSDResourceResolver resourceResolver = new XSDResourceResolver(this.resources);
		try{
			// UndeclaredPrefix: Cannot resolve 'example:xxxxType' as a QName: the prefix 'example' is not declared.
			// After some debugging, I've found out that this is a bug of the JAXP api's built in to the JDK.
			// You can fix it by making sure that you use the Xerces version of the SchemaFactory, and not the JDK internal one. 
			// The algorithm for choosing a SchemaFactory is explained at http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/validation/SchemaFactory.html#newInstance(java.lang.String).
			// It comes down to setting the System property "javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema" to the value "org.apache.xerces.jaxp.validation.XMLSchemaFactory".
			// Note that just adding Xerces to your classpath won't fix this, for reasons explained at http://xerces.apache.org/xerces2-j/faq-general.html#faq-4
			//return new ValidatoreXSD(org.apache.xerces.jaxp.validation.XMLSchemaFactory.class.getName(),xsdResourceResolver,is);
			ValidatoreXSD validatoreXSD = new ValidatoreXSD(logger,"org.apache.xerces.jaxp.validation.XMLSchemaFactory",resourceResolver,
					new ByteArrayInputStream(this.schemaRoot));
			//ValidatoreXSD validatoreXSD = new ValidatoreXSD(this.logger,resourceResolver,new ByteArrayInputStream(schemaPerValidazione));

			//System.out.println("CANCELLAMI");
			if(serializeXSDSchemi_buildSchemaSuccess){
				debugPrintXSDSchemi(this.schemaRoot, resourceResolver, logger, true);
			}

			return validatoreXSD.getSchema();

		}catch (Exception e) {

			if(serializeXSDSchemi_buildSchemaErrror){
				debugPrintXSDSchemi(this.schemaRoot, resourceResolver, logger, false);
			}

			throw new XMLException("Riscontrato errore durante l'inizializzazione dello schema: "+e.getMessage(),e);
		}
		
	}
	
	private void debugPrintXSDSchemi(byte[]schemaPerValidazione,XSDResourceResolver resourceResolver,Logger logger, boolean success){
		try{
			File dir = File.createTempFile("xsd_dir_", "");
			dir.delete();
			boolean dirCreate = dir.mkdir();
			//System.out.println("FILE?["+dir.getAbsolutePath()+"] ["+dirCreate+"] ["+dir.isDirectory()+"]");
			dirCreate = dirCreate & dir.isDirectory();
			//System.out.println("DIR CREATE ["+dirCreate+"]");
			
			// Provo a registrare lo schema principale
			String uniqueID = XSDSchemaCollection.getIdForDebug();
			File f = null;
			if(dirCreate)
				f = File.createTempFile("root_"+uniqueID+"_", ".xsd",dir);
			else
				f =	File.createTempFile("root_"+uniqueID+"_", ".xsd");
			FileSystemUtilities.writeFile(f, schemaPerValidazione);
			
			// Provo a registrare gli schemi utilizzati
			if(resourceResolver!=null){
				if(resourceResolver instanceof XSDResourceResolver){
					XSDResourceResolver xsdResolver = resourceResolver;
					Enumeration<String> keys = xsdResolver.getResources().keys();
					while (keys.hasMoreElements()) {
						String systemId = keys.nextElement();
						byte[] contenuto = xsdResolver.getResources().get(systemId);
						File schemaTmpLog = null;
						if(dirCreate)
							schemaTmpLog = File.createTempFile("import_"+uniqueID+"_"+systemId+"_", ".xsd", dir);
						else
							schemaTmpLog = File.createTempFile("import_"+uniqueID+"_"+systemId+"_", ".xsd");
						FileSystemUtilities.writeFile(schemaTmpLog, contenuto);
					}
				}
			}
			
			String motivo = null;
			if(success){
				motivo = "completata con successo";
			}
			else{
				motivo = "completata con errore";
			}
			
			String msg = null;
			if(dirCreate)
				msg = "Inizializzazione dello schema "+motivo+", gli schemi sono stati registrati nella directory "+dir.getAbsolutePath();
			else
				msg = "Inizializzazione dello schema "+motivo+", gli schemi sono stati registrati nella area temporanea (root schema: "+f.getAbsolutePath()+")";
			
			if(success){
				logger.info(msg);
			}
			else{
				logger.error(msg);
			}
							
		}catch(Exception eDebug){
			logger.error("Registrazione xsd per debug non riuscita: "+eDebug.getMessage(),eDebug);
		}
	}
	
	private static long counter = 0;
	private static synchronized String getIdForDebug(){
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
		XSDSchemaCollection.counter++;
		return "ID_"+XSDSchemaCollection.counter+"_"+dateformat.format(DateManager.getDate());
	}
}
