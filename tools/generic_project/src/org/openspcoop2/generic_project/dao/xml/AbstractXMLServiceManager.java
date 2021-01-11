/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.dao.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.serializer.AbstractDeserializer;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.xml.ValidatoreXSD;
import org.slf4j.Logger;

/**
 * AbstractXMLServiceManager
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractXMLServiceManager<XML> {

	/** Contesto di Unmarshall. */
	private AbstractDeserializer deserializer;
	private Class<XML> cXmlRoot;
	/** XML Path */
	protected String xmlPath;
	/** 'Root' XML */
	protected XML rootXml;
	/** XSD Validator */
	private ValidatoreXSD xsdValidator = null;
	/** LastModified */
	private long lastModified = 0;
	/** Logger */
	private Logger log = null;
	/** Refresh Timeout */
	private static final int timeoutRefresh = 30;
	
	
	/** ************* parsing XML ************** */
	private void parsingXML() throws ServiceException{

		/* --- XSD -- */
		FileInputStream fXML = null;
		try{
			if(this.xmlPath.startsWith("http://") || this.xmlPath.startsWith("file://")){
				this.xsdValidator.valida(this.xmlPath);  
			}else{
				fXML = new FileInputStream(this.xmlPath);
				this.xsdValidator.valida(fXML);
			}
		}catch (Exception e) {
			throw new ServiceException("Xsd validation failure: "+e.getMessage(),e);
		}finally{
			if(fXML!=null){
				try{
					fXML.close();
				}catch(Exception e){}
			}
		}

		/* ---- InputStream ---- */
		InputStream iStream = null;
		HttpURLConnection httpConn = null;
		if(this.xmlPath.startsWith("http://") || this.xmlPath.startsWith("file://")){
			try{ 
				URL url = new URL(this.xmlPath);
				URLConnection connection = url.openConnection();
				httpConn = (HttpURLConnection) connection;
				httpConn.setRequestMethod("GET");
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				iStream = httpConn.getInputStream();
			}catch(Exception e) {
				try{  
					if(iStream!=null)
						iStream.close();
					if(httpConn !=null)
						httpConn.disconnect();
				} catch(Exception ef) {}
				throw new ServiceException("Creating InputStream (HTTP) failure: "+e.getMessage(),e);
			}
			this.lastModified = System.currentTimeMillis();
		}else{
			try{  
				iStream = new FileInputStream(this.xmlPath);
			}catch(java.io.FileNotFoundException e) {
				throw new ServiceException("Creating InputStream (FILE) failure"+e.getMessage(),e);
			}
			try{
				this.lastModified = (new File(this.xmlPath)).lastModified();
			}catch(Exception e){
				try{  
					if(iStream!=null)
						iStream.close();
				} catch(java.io.IOException ef) {}
				throw new ServiceException("Reading xml ["+this.xmlPath+"] failure: "+e.getMessage(),e);
			}
		}

		/* ---- Unmarshall ---- */
		try{  
			this.rootXml = (XML) this.deserializer.xmlToObj(iStream, this.cXmlRoot);
		} catch(Exception e) {
			try{  
				if(iStream!=null)
					iStream.close();
				if(httpConn !=null)
					httpConn.disconnect();
			} catch(Exception ef) {}
			throw new ServiceException("Unmarshall document xml failure: "+e.getMessage(),e);
		}

		/* ---- Close ---- */
		try{  
			if(iStream!=null)
				iStream.close();
			if(httpConn !=null)
				httpConn.disconnect();
		} catch(Exception e) {
			throw new ServiceException("Close InputStream failure: "+e.getMessage(),e);
		}
	}




	/* ********  COSTRUTTORI e METODI DI RELOAD  ******** */
	protected AbstractXMLServiceManager(Class<XML> cXmlRoot, AbstractDeserializer deserializer, String xsdPath, File xmlPath) throws ServiceException{
		this(cXmlRoot, deserializer, xsdPath, xmlPath,null);
	}
	protected AbstractXMLServiceManager(Class<XML> cXmlRoot, AbstractDeserializer deserializer, String xsdPath, File xmlPath,Logger alog) throws ServiceException{
		this(cXmlRoot,deserializer, xsdPath, xmlPath.getAbsolutePath(),alog);
	}
	protected AbstractXMLServiceManager(Class<XML> cXmlRoot, AbstractDeserializer deserializer, String xsdPath, String xmlPath) throws ServiceException{
		this(cXmlRoot, deserializer, xsdPath,  xmlPath,null);
	}
	protected AbstractXMLServiceManager(Class<XML> cXmlRoot, AbstractDeserializer deserializer, String xsdPath, String xmlPath,Logger alog) throws ServiceException{

		if(alog==null){
			this.log = LoggerWrapperFactory.getLogger(AbstractXMLServiceManager.class);
		}else
			this.log = alog;

		if(xmlPath == null){
			this.log.error("New Instance failure: url/xmlPath is null");
			throw new ServiceException("New Instance failure: url/xmlPath is null");
		}
		this.xmlPath = xmlPath;
		
		/* --- XSD Validator -- */
		if(xsdPath == null){
			this.log.error("New Instance failure: xsdPath is null");
			throw new ServiceException("New Instance failure: xsdPath is null");
		}
		InputStream is = null;
		try{
			File f = new File(xsdPath);
			if(f.exists()){
				is = new FileInputStream(f);
			}else{
				is = cXmlRoot.getResourceAsStream(xsdPath);
				if(is==null){
					is = cXmlRoot.getResourceAsStream("/"+xsdPath);
				}
			}
			if(is==null){
				throw new Exception("Creating InputStream from xsdPath["+xsdPath+"] failure");
			}
			this.xsdValidator = new ValidatoreXSD(null,is);
		}catch (Exception e) {
			this.log.error("Init xsd schema failure: "+e.getMessage(),e);
			throw new ServiceException("Init xsd schema failure: "+e.getMessage(),e);
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}

		/* ---- Uunmarshall  ---- */
		this.cXmlRoot = cXmlRoot;
		this.deserializer = deserializer;

		this.parsingXML();
	}
	
	public void refreshXML() throws ServiceException{
		refreshXML_engine(false);
	}
	public void refreshXML(boolean forcedWithoutCheckModified) throws ServiceException{
		refreshXML_engine(forcedWithoutCheckModified);
	}
	
	private synchronized void refreshXML_engine(boolean forcedWithoutCheckModified) throws ServiceException{

		File fTest = null;
		boolean refresh = forcedWithoutCheckModified;
		if(forcedWithoutCheckModified==false){
			if(this.xmlPath.startsWith("http://") || this.xmlPath.startsWith("file://")){
				long now = System.currentTimeMillis();
				if( (now-this.lastModified) > (AbstractXMLServiceManager.timeoutRefresh*1000) ){
					refresh=true;
				}
			}else{
				fTest = new File(this.xmlPath);
				if(this.lastModified != fTest.lastModified()){
					refresh = true;
				}
			}
		}
		if(refresh){

			try{
				this.parsingXML();
			}catch(Exception e){
				this.log.error("Refresh failure: "+e.getMessage(),e);
				throw new ServiceException("Refresh failure: "+e.getMessage(),e);
			}
			if(this.xmlPath.startsWith("http://")==false && this.xmlPath.startsWith("file://")==false){
				this.log.warn("Reloaded context.");
			}
			
		}

		if(this.rootXml == null){
			this.log.error("Refresh failure: rootXml is null after the refresh");
			throw new ServiceException("Refresh failure: rootXml is null after the refresh");
		}

	}
	
	public void checkRemoteXML() throws ServiceException{
		if(this.xmlPath.startsWith("http://")){
			throw new ServiceException("You can not use the CRUD service with a remote XML");
		}
	}

	public Logger getLog() {
		return this.log;
	}

	public XML getRootXml() {
		return this.rootXml;
	}

	/* Logger */
	public static void configureDefaultLog4jProperties(IProjectInfo project) throws ServiceException{
		XMLLoggerProperties loggerProperties = new XMLLoggerProperties(project);
		loggerProperties.configureLog4j();
	}
	public static void configureLog4jProperties(File log4jProperties) throws ServiceException{
		XMLLoggerProperties loggerProperties = new XMLLoggerProperties(null,log4jProperties);
		loggerProperties.configureLog4j();
	}
}
