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



package org.openspcoop2.utils.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.slf4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;


/**
 * Classe utilizzata per effettuare una validazione con schema xsd di un messaggio.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class AbstractValidatoreXSD {


	/** StreamSource */
	private Schema schema;
	public Schema getSchema() {
		return this.schema;
	}
	
	
	
	/** ----------------- COSTRUTTORE SCHEMA -----------------
	
	/** 
	 * Costruttore
	 * 
	 * @param schema
	 */
	public AbstractValidatoreXSD(Schema schema)throws Exception{
		try{
			if(schema == null)
				throw new Exception("Schema is null?");
			this.schema = schema;
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (InputStream): "+e.getMessage(),e);
		}
	}
	
	
	
	
	/** ----------------- COSTRUTTORE INPUT STREAM ----------------- */
	
	private static final String FACTORY_DEFAULT = "FactoryDefault";
	
	/** 
	 * Costruttore InputStream
	 * 
	 * @param inputStream
	 */
	public AbstractValidatoreXSD(Logger log,InputStream inputStream)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,inputStream);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,InputStream inputStream)throws Exception{
		try{
			StreamSource streamSource = new StreamSource(inputStream);	
			this.initializeSchema(log,schemaFactory,null,streamSource);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (InputStream): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,LSResourceResolver lsResourceResolver,InputStream inputStream)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,lsResourceResolver,inputStream);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,InputStream inputStream)throws Exception{
		try{
			StreamSource streamSource = new StreamSource(inputStream);	
			this.initializeSchema(log,schemaFactory,lsResourceResolver,streamSource);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (InputStream): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,InputStream... inputStream)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,inputStream);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,InputStream... inputStream)throws Exception{
		try{
			StreamSource [] ss = new StreamSource[inputStream.length];
			for(int i=0; i<inputStream.length; i++){
				ss[i] = new StreamSource(inputStream[i]);
			}
			this.initializeSchema(log,schemaFactory,null,ss);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (InputStream): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,LSResourceResolver lsResourceResolver,InputStream... inputStream)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,lsResourceResolver,inputStream);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,InputStream... inputStream)throws Exception{
		try{
			StreamSource [] ss = new StreamSource[inputStream.length];
			for(int i=0; i<inputStream.length; i++){
				ss[i] = new StreamSource(inputStream[i]);
			}
			this.initializeSchema(log,schemaFactory,lsResourceResolver,ss);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (InputStream): "+e.getMessage(),e);
		}
	}
	
	
	
	/** ----------------- COSTRUTTORE FILE -----------------
	
	/**
	 * Costruttore File
	 * 
	 * @param file
	 */
	public AbstractValidatoreXSD(Logger log,File file)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,file);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,File file)throws Exception{
		try{
			StreamSource streamSource = new StreamSource(file);	
			this.initializeSchema(log,schemaFactory,null,streamSource);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (File:"+file.getName()+"): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,LSResourceResolver lsResourceResolver,File file)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,lsResourceResolver,file);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,File file)throws Exception{
		try{
			StreamSource streamSource = new StreamSource(file);	
			this.initializeSchema(log,schemaFactory,lsResourceResolver,streamSource);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (File:"+file.getName()+"): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,File... file)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,file);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,File... file)throws Exception{
		try{
			StreamSource [] ss = new StreamSource[file.length];
			for(int i=0; i<file.length; i++){
				ss[i] = new StreamSource(file[i]);
			}
			this.initializeSchema(log,schemaFactory,null,ss);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (Files): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,LSResourceResolver lsResourceResolver,File... file)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,lsResourceResolver,file);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,File... file)throws Exception{
		try{
			StreamSource [] ss = new StreamSource[file.length];
			for(int i=0; i<file.length; i++){
				ss[i] = new StreamSource(file[i]);
			}
			this.initializeSchema(log,schemaFactory,lsResourceResolver,ss);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (Files): "+e.getMessage(),e);
		}
	}
	
	
	
	/** ----------------- COSTRUTTORE URL -----------------
	
	/**
	 * Costruttore url
	 * 
	 * @param url
	 */
	public AbstractValidatoreXSD(Logger log,String url)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,url);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,String url)throws Exception{
		try{
			StreamSource streamSource = new StreamSource(url);	
			this.initializeSchema(log,schemaFactory,null,streamSource);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (URL:"+url+"): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,LSResourceResolver lsResourceResolver,String url)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,lsResourceResolver,url);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,String url)throws Exception{
		try{
			StreamSource streamSource = new StreamSource(url);	
			this.initializeSchema(log,schemaFactory,lsResourceResolver,streamSource);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (URL:"+url+"): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,String... url)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,url);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,String... url)throws Exception{
		try{
			StreamSource [] ss = new StreamSource[url.length];
			for(int i=0; i<url.length; i++){
				ss[i] = new StreamSource(url[i]);
			}
			this.initializeSchema(log,schemaFactory,null,ss);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (URL:"+url+"): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,LSResourceResolver lsResourceResolver,String... url)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,lsResourceResolver,url);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,String... url)throws Exception{
		try{
			StreamSource [] ss = new StreamSource[url.length];
			for(int i=0; i<url.length; i++){
				ss[i] = new StreamSource(url[i]);
			}
			this.initializeSchema(log,schemaFactory,lsResourceResolver,ss);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (URL:"+url+"): "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	/** ----------------- COSTRUTTORE NODE -----------------
	
	/**
	 * Costruttore node
	 * 
	 * @param schema
	 */
	public AbstractValidatoreXSD(Logger log,Node schema)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,schema);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,Node schema)throws Exception{
		try{
			DOMSource streamSource = new DOMSource(schema);	
			this.initializeSchema(log,schemaFactory,null,streamSource);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (Node): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,LSResourceResolver lsResourceResolver,Node schema)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,lsResourceResolver,schema);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,Node schema)throws Exception{
		try{
			DOMSource streamSource = new DOMSource(schema);	
			this.initializeSchema(log,schemaFactory,lsResourceResolver,streamSource);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (Node): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,Node... schema)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,schema);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,Node... schema)throws Exception{
		try{
			DOMSource [] ss = new DOMSource[schema.length];
			for(int i=0; i<schema.length; i++){
				ss[i] = new DOMSource(schema[i]);
			}
			this.initializeSchema(log,schemaFactory,null,ss);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (Node): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,LSResourceResolver lsResourceResolver,Node... schema)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,lsResourceResolver,schema);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,Node... schema)throws Exception{
		try{
			DOMSource [] ss = new DOMSource[schema.length];
			for(int i=0; i<schema.length; i++){
				ss[i] = new DOMSource(schema[i]);
			}
			this.initializeSchema(log,schemaFactory,lsResourceResolver,ss);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (Node): "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ----------------- COSTRUTTORE SOURCE GENERICO -----------------
	
	/**
	 * Costruttore source
	 * 
	 * @param source
	 */
	public AbstractValidatoreXSD(Logger log,Source source)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,source);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,Source source)throws Exception{
		try{
			this.initializeSchema(log,schemaFactory,null,source);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (Source): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,LSResourceResolver lsResourceResolver,Source source)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,lsResourceResolver,source);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,Source source)throws Exception{
		try{
			this.initializeSchema(log,schemaFactory,lsResourceResolver,source);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (Source): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,Source... source)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,source);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,Source... source)throws Exception{
		try{
			this.initializeSchema(log,schemaFactory,null,source);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (Source): "+e.getMessage(),e);
		}
	}
	
	public AbstractValidatoreXSD(Logger log,LSResourceResolver lsResourceResolver,Source... source)throws Exception{
		this(log,AbstractValidatoreXSD.FACTORY_DEFAULT,lsResourceResolver,source);
	}
	public AbstractValidatoreXSD(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,Source... source)throws Exception{
		try{
			this.initializeSchema(log,schemaFactory,lsResourceResolver,source);
		}catch(Exception e){
			throw new Exception("Riscontrato errore durante la costruzione dello schema (Source): "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ----------------- INIT SCHEMA -----------------
	
	/**
	 * Metodo che si occupa di inizializzare lo schema per la validazione.
	 *
	 * 
	 */
	private void initializeSchema(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,Source streamSource) throws Exception{
		try{
			
			// La gestione dello schemaFactory e' servito per il seguente motivo:
			// UndeclaredPrefix: Cannot resolve 'messaggioSII:xxxxMessaggioSIIType' as a QName: the prefix 'messaggioSII' is not declared.
			// After some debugging, I've found out that this is a bug of the JAXP api's built in to the JDK.
			// You can fix it by making sure that you use the Xerces version of the SchemaFactory, and not the JDK internal one. 
			// The algorithm for choosing a SchemaFactory is explained at http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/validation/SchemaFactory.html#newInstance(java.lang.String).
			// It comes down to setting the System property "javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema" to the value "org.apache.xerces.jaxp.validation.XMLSchemaFactory".
			// Note that just adding Xerces to your classpath won't fix this, for reasons explained at http://xerces.apache.org/xerces2-j/faq-general.html#faq-4
			
			String oldSchemaFactorySetup = null;
			String propertySystem = "javax.xml.validation.SchemaFactory:"+XMLConstants.W3C_XML_SCHEMA_NS_URI;
			if(schemaFactory!=null && !AbstractValidatoreXSD.FACTORY_DEFAULT.equals(schemaFactory)){
				oldSchemaFactorySetup = System.getenv(propertySystem);
				if(oldSchemaFactorySetup==null){
					oldSchemaFactorySetup = System.getProperty(propertySystem);
					if(oldSchemaFactorySetup==null){
						oldSchemaFactorySetup = "";
					}
				}
				System.setProperty(propertySystem, schemaFactory);
			}
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			log.info("SchemaFactory["+factory.getClass().getName()+"]");
			if(lsResourceResolver!=null){
				factory.setResourceResolver(lsResourceResolver);
			}
			this.schema = factory.newSchema(streamSource);
			if(schemaFactory!=null && !AbstractValidatoreXSD.FACTORY_DEFAULT.equals(schemaFactory) && oldSchemaFactorySetup!=null){
				log.debug("Ripristino oldSchemaFactory ["+oldSchemaFactorySetup+"]");
				System.setProperty(propertySystem, oldSchemaFactorySetup);
			}
		}catch (Exception e) {
			throw new Exception("Riscontrato errore durante l'inizializzazione dello schema: "+e.getMessage(),e);
		}
	}
	private void initializeSchema(Logger log,String schemaFactory,LSResourceResolver lsResourceResolver,Source[] streamSource) throws Exception{
		try{
			
			// La gestione dello schemaFactory e' servito per il seguente motivo:
			// UndeclaredPrefix: Cannot resolve 'messaggioSII:xxxxMessaggioSIIType' as a QName: the prefix 'messaggioSII' is not declared.
			// After some debugging, I've found out that this is a bug of the JAXP api's built in to the JDK.
			// You can fix it by making sure that you use the Xerces version of the SchemaFactory, and not the JDK internal one. 
			// The algorithm for choosing a SchemaFactory is explained at http://java.sun.com/j2se/1.5.0/docs/api/javax/xml/validation/SchemaFactory.html#newInstance(java.lang.String).
			// It comes down to setting the System property "javax.xml.validation.SchemaFactory:http://www.w3.org/2001/XMLSchema" to the value "org.apache.xerces.jaxp.validation.XMLSchemaFactory".
			// Note that just adding Xerces to your classpath won't fix this, for reasons explained at http://xerces.apache.org/xerces2-j/faq-general.html#faq-4
			
			String oldSchemaFactorySetup = null;
			String propertySystem = "javax.xml.validation.SchemaFactory:"+XMLConstants.W3C_XML_SCHEMA_NS_URI;
			if(schemaFactory!=null && !AbstractValidatoreXSD.FACTORY_DEFAULT.equals(schemaFactory)){
				oldSchemaFactorySetup = System.getenv(propertySystem);
				if(oldSchemaFactorySetup==null){
					oldSchemaFactorySetup = System.getProperty(propertySystem);
					if(oldSchemaFactorySetup==null){
						oldSchemaFactorySetup = "";
					}
				}
				System.setProperty(propertySystem, schemaFactory);
			}
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			log.info("SchemaFactory["+factory.getClass().getName()+"]");
			if(lsResourceResolver!=null){
				factory.setResourceResolver(lsResourceResolver);
			}
			this.schema = factory.newSchema(streamSource);
			if(schemaFactory!=null && !AbstractValidatoreXSD.FACTORY_DEFAULT.equals(schemaFactory) && oldSchemaFactorySetup!=null){
				log.debug("Ripristino oldSchemaFactory ["+oldSchemaFactorySetup+"]");
				System.setProperty(propertySystem, oldSchemaFactorySetup);
			}
		}catch (Exception e) {
			throw new Exception("Riscontrato errore durante l'inizializzazione dello schema: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	/** ----------------- VALIDAZIONI -----------------
	
	
	/**
	 * Metodo che effettua la validazione xsd. 
	 *
	 * @param nodeXML Node
	 */
	public void valida(Node nodeXML) throws Exception{
		valida(new DOMSource(nodeXML));
	}
	public void valida(Node nodeXML,ErrorHandler errorHandler) throws Exception{
		valida(new DOMSource(nodeXML),errorHandler);
	}
	
	/**
	 * Metodo che effettua la validazione xsd. 
	 *
	 * @param nodeXML Node
	 */
	public void valida(Node nodeXML,boolean streamSource) throws Exception{
		this.valida(nodeXML,streamSource,null);
	}
	public abstract byte[] getAsByte(Node nodeXML) throws XMLException;
	public void valida(Node nodeXML,boolean streamSource,ErrorHandler errorHandler) throws Exception{
		if(streamSource){
			// Risolve il problema di validare gli attributi
			ByteArrayInputStream bin = new ByteArrayInputStream(this.getAsByte(nodeXML));
			valida(new StreamSource(bin),errorHandler);
		}else{
			valida(nodeXML,errorHandler);
		}
	}
	
	/**
	 * Metodo che effettua la validazione xsd. 
	 *
	 * @param inputStreamXML Input Stream di un contenuto xml
	 */
	public void valida(InputStream inputStreamXML) throws Exception{
		valida(new StreamSource(inputStreamXML));
	}
	public void valida(InputStream inputStreamXML,ErrorHandler errorHandler) throws Exception{
		valida(new StreamSource(inputStreamXML),errorHandler);
	}
	
	/**
	 * Metodo che effettua la validazione xsd. 
	 *
	 * @param fileXML File xml
	 */
	public void valida(File fileXML) throws Exception{
		valida(new StreamSource(fileXML));
	}
	public void valida(File fileXML,ErrorHandler errorHandler) throws Exception{
		valida(new StreamSource(fileXML),errorHandler);
	}
	
	/**
	 * Metodo che effettua la validazione xsd. 
	 *
	 * @param urlXML URL di un file xml
	 */
	public void valida(String urlXML) throws Exception{
		valida(new StreamSource(urlXML));
	}
	public void valida(String urlXML,ErrorHandler errorHandler) throws Exception{
		valida(new StreamSource(urlXML),errorHandler);
	}
	
	/**
	 * Metodo che effettua la validazione xsd. 
	 *
	 * @param source Sorgente da validare
	 */
	public void valida(Source source) throws Exception{
		this.valida(source, null);
	}
	public void valida(Source source,ErrorHandler errorHandler) throws Exception{
		Validator validator  = this.schema.newValidator();
		try {
			if(errorHandler!=null){
				validator.setErrorHandler(errorHandler);
			}
			validator.validate(source);
		} catch (SAXException e) {
			// instance document is invalid!
			throw e;
		}
	}
	
	
}
