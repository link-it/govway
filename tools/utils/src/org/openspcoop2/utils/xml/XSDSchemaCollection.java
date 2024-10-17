/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.validation.Schema;

import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;

/**
* XSDSchemaCollection
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class XSDSchemaCollection {

	private static boolean serializeXSDSchemiBuildSchemaSuccessDefault = false;
	private static boolean serializeXSDSchemiBuildSchemaErrorDefault = true;
	public static boolean isSerializeXSDSchemiBuildSchemaSuccessDefault() {
		return serializeXSDSchemiBuildSchemaSuccessDefault;
	}
	public static void setSerializeXSDSchemiBuildSchemaSuccessDefault(boolean serializeXSDSchemiBuildSchemaSuccessDefault) {
		XSDSchemaCollection.serializeXSDSchemiBuildSchemaSuccessDefault = serializeXSDSchemiBuildSchemaSuccessDefault;
	}
	public static boolean isSerializeXSDSchemiBuildSchemaErrorDefault() {
		return serializeXSDSchemiBuildSchemaErrorDefault;
	}
	public static void setSerializeXSDSchemiBuildSchemaErrorDefault(boolean serializeXSDSchemiBuildSchemaErrorDefault) {
		XSDSchemaCollection.serializeXSDSchemiBuildSchemaErrorDefault = serializeXSDSchemiBuildSchemaErrorDefault;
	}
	
	private byte[] schemaRoot;
	private Map<String, byte[]> resources;
	private Map<String, String> mappingNamespaceLocations;
		
	private boolean serializeXSDSchemiBuildSchemaSuccess = false;
	private boolean serializeXSDSchemiBuildSchemaError = true;
	
	public XSDSchemaCollection() {
		this.serializeXSDSchemiBuildSchemaSuccess = isSerializeXSDSchemiBuildSchemaSuccessDefault();
		this.serializeXSDSchemiBuildSchemaError = isSerializeXSDSchemiBuildSchemaErrorDefault();
	}
	
	public boolean isSerializeXSDSchemiBuildSchemaSuccess() {
		return this.serializeXSDSchemiBuildSchemaSuccess;
	}
	public void setSerializeXSDSchemiBuildSchemaSuccess(boolean serializeXSDSchemiBuildSchemaSuccess) {
		this.serializeXSDSchemiBuildSchemaSuccess = serializeXSDSchemiBuildSchemaSuccess;
	}
	public boolean isSerializeXSDSchemiBuildSchemaError() {
		return this.serializeXSDSchemiBuildSchemaError;
	}
	public void setSerializeXSDSchemiBuildSchemaError(boolean serializeXSDSchemiBuildSchemaErrror) {
		this.serializeXSDSchemiBuildSchemaError = serializeXSDSchemiBuildSchemaErrror;
	}
	public byte[] getSchemaRoot() {
		return this.schemaRoot;
	}
	public void setSchemaRoot(byte[] schemaRoot) {
		this.schemaRoot = schemaRoot;
	}
	public Map<String, byte[]> getResources() {
		return this.resources;
	}
	public void setResources(Map<String, byte[]> resources) {
		this.resources = resources;
	}
	public Map<String, String> getMappingNamespaceLocations() {
		return this.mappingNamespaceLocations;
	}
	public void setMappingNamespaceLocations(Map<String, String> mappingNamespaceLocations) {
		this.mappingNamespaceLocations = mappingNamespaceLocations;
	}
	
	public void serialize(Logger log,File file) throws XMLException{
		try (FileOutputStream fout = new FileOutputStream(file);){
			serialize(log,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
		}
	}
	
	public void serialize(Logger log,String fileName) throws XMLException{
		try (FileOutputStream fout = new FileOutputStream(fileName);){
			serialize(log,fout);
			fout.flush();
		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
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
	
		try (ZipOutputStream zipOut = new ZipOutputStream(out);){
			this.zipSerialize(log,zipOut);
			
			zipOut.flush();

		}catch(Exception e){
			throw new XMLException(e.getMessage(),e);
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
				for (String name : this.resources.keySet()) {
					
					nomeFile = name;
					zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
					zipOut.write(this.resources.get(name));
					
					String namespaceFound = null;
					String locationFound = null;
					for (String namespace : this.mappingNamespaceLocations.keySet()) {
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
				this.buildSchemaEngine(log,false,false);
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
		return this.buildSchemaEngine(logger, this.serializeXSDSchemiBuildSchemaSuccess, this.serializeXSDSchemiBuildSchemaError);
	}
	private Schema buildSchemaEngine(Logger logger, boolean serializeXSDSchemiBuildSchemaSuccess, boolean serializeXSDSchemiBuildSchemaErrror) throws XMLException {
		
		// Creo XSDResolver con le risorse localizzate e procedo con la validazione
		XSDResourceResolver resourceResolver = new XSDResourceResolver(this.resources);
		try{
			// UndeclaredPrefix: Cannot resolve 'example:xxxxType' as a QName: the prefix 'example' is not declared.
			// After some debugging, I've found out that this is a bug of the JAXP api's built in to the JDK.
			// You can fix it by making sure that you use the Xerces version of the SchemaFactory, and not the JDK internal one. 
			// The algorithm for choosing a SchemaFactory is explained at http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/validation/SchemaFactory.html#newInstance(java.lang.String).
			// It comes down to setting the System property "javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema" to the value "org.apache.xerces.jaxp.validation.XMLSchemaFactory".
			// Note that just adding Xerces to your classpath won't fix this, for reasons explained at http://xerces.apache.org/xerces2-j/faq-general.html#faq-4
			/**return new ValidatoreXSD(org.apache.xerces.jaxp.validation.XMLSchemaFactory.class.getName(),xsdResourceResolver,is);*/
			ValidatoreXSD validatoreXSD = new ValidatoreXSD(logger,"org.apache.xerces.jaxp.validation.XMLSchemaFactory",resourceResolver,
					new ByteArrayInputStream(this.schemaRoot));
			/**ValidatoreXSD validatoreXSD = new ValidatoreXSD(this.logger,resourceResolver,new ByteArrayInputStream(schemaPerValidazione));*/

			if(serializeXSDSchemiBuildSchemaSuccess){
				debugPrintXSDSchemi(this.schemaRoot, resourceResolver, logger, true);
			}

			return validatoreXSD.getSchema();

		}catch (Exception e) {

			if(serializeXSDSchemiBuildSchemaErrror){
				debugPrintXSDSchemi(this.schemaRoot, resourceResolver, logger, false);
			}

			throw new XMLException("Riscontrato errore durante l'inizializzazione dello schema: "+e.getMessage(),e);
		}
		
	}
	
	private void debugPrintXSDSchemi(byte[]schemaPerValidazione,XSDResourceResolver resourceResolver,Logger logger, boolean success){
		try{
			FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwx------"));
			File dir = Files.createTempDirectory("xsd_dir_", attr).toFile();
			boolean dirCreate = dir.exists();
			/**System.out.println("FILE?["+dir.getAbsolutePath()+"] ["+dirCreate+"] ["+dir.isDirectory()+"]");*/
			dirCreate = dirCreate && dir.isDirectory();
			/**System.out.println("DIR CREATE ["+dirCreate+"]");*/
			
			// Provo a registrare lo schema principale
			String uniqueID = XSDSchemaCollection.getIdForDebug();
			File f = null;
			if(dirCreate)
				f = File.createTempFile("root_"+uniqueID+"_", ".xsd",dir);
			else
				f =	FileSystemUtilities.createTempFile("root_"+uniqueID+"_", ".xsd");
			FileSystemUtilities.writeFile(f, schemaPerValidazione);
			
			// Provo a registrare gli schemi utilizzati
			if(resourceResolver!=null){
				XSDResourceResolver xsdResolver = resourceResolver;
				for (String systemId : xsdResolver.getResources().keySet()) {
					byte[] contenuto = xsdResolver.getResources().get(systemId);
					File schemaTmpLog = null;
					if(dirCreate)
						schemaTmpLog = File.createTempFile("import_"+uniqueID+"_"+systemId+"_", ".xsd", dir);
					else
						schemaTmpLog = FileSystemUtilities.createTempFile("import_"+uniqueID+"_"+systemId+"_", ".xsd");
					FileSystemUtilities.writeFile(schemaTmpLog, contenuto);
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
		SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
		XSDSchemaCollection.counter++;
		return "ID_"+XSDSchemaCollection.counter+"_"+dateformat.format(DateManager.getDate());
	}
}
