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
package org.openspcoop2.generic_project.dao.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.openspcoop2.generic_project.beans.IProjectInfo;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.xml.ValidatoreXSD;

/**
 * AbstractXMLServiceManager
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractXMLServiceManager<XML> {

	/** Contesto di Unmarshall. */
	private IUnmarshallingContext uctx;
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
	@SuppressWarnings("unchecked")
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
		InputStreamReader iStream = null;
		HttpURLConnection httpConn = null;
		if(this.xmlPath.startsWith("http://") || this.xmlPath.startsWith("file://")){
			try{ 
				URL url = new URL(this.xmlPath);
				URLConnection connection = url.openConnection();
				httpConn = (HttpURLConnection) connection;
				httpConn.setRequestMethod("GET");
				httpConn.setDoOutput(true);
				httpConn.setDoInput(true);
				iStream = new InputStreamReader(httpConn.getInputStream());
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
				iStream = new InputStreamReader(new FileInputStream(this.xmlPath));
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
			this.rootXml = (XML) this.uctx.unmarshalDocument(iStream, null);
		} catch(org.jibx.runtime.JiBXException e) {
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
	public AbstractXMLServiceManager(Class<XML> cXmlRoot, String xsdPath, File xmlPath) throws ServiceException{
		this(cXmlRoot, xsdPath, xmlPath,null);
	}
	public AbstractXMLServiceManager(Class<XML> cXmlRoot, String xsdPath, File xmlPath,Logger alog) throws ServiceException{
		this(cXmlRoot,xsdPath,xmlPath.getAbsolutePath(),alog);
	}
	public AbstractXMLServiceManager(Class<XML> cXmlRoot, String xsdPath, String xmlPath) throws ServiceException{
		this(cXmlRoot, xsdPath, xmlPath,null);
	}
	public AbstractXMLServiceManager(Class<XML> cXmlRoot, String xsdPath, String xmlPath,Logger alog) throws ServiceException{

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

		/* ---- Uunmarshall Context ---- */
		try{
 			IBindingFactory bfact = BindingDirectory.getFactory(cXmlRoot);
			this.uctx = bfact.createUnmarshallingContext();
		} catch(org.jibx.runtime.JiBXException e) {
			this.log.error("Creating Unmarshall contest failure: "+e.getMessage(),e);
			throw new ServiceException("Creating Unmarshall contest failure: "+e.getMessage(),e);
		}

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
