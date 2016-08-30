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
package org.openspcoop2.generic_project.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.openspcoop2.generic_project.exception.ServiceException;
import org.openspcoop2.generic_project.exception.ValidationException;
import org.openspcoop2.utils.beans.BaseBean;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.utils.xml.XMLException;
import org.openspcoop2.utils.xml.XMLUtils;
import org.openspcoop2.utils.xml.XSDResourceResolver;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * XSDValidator
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class XSDValidator {

	/** XSD Validator */
	private AbstractValidatoreXSD xsdValidator = null;

	public XSDValidator(Logger log,Class<?> cXmlRoot,String xsdPath) throws ServiceException{
		this(log, cXmlRoot, org.openspcoop2.utils.xml.ValidatoreXSD.class, xsdPath, new String[]{});
	}
	public XSDValidator(Logger log,Class<?> cXmlRoot,String xsdPath,String ... xsdImported) throws ServiceException{
		this(log, cXmlRoot, org.openspcoop2.utils.xml.ValidatoreXSD.class, xsdPath, xsdImported);
	}
	public XSDValidator(Logger log,Class<?> cXmlRoot,Class<?> xsdValidatorImpl,String xsdPath) throws ServiceException{
		this(log, cXmlRoot, xsdValidatorImpl, xsdPath, new String[]{});
	}
	public XSDValidator(Logger log,Class<?> cXmlRoot,Class<?> xsdValidatorImpl,String xsdPath,String ... xsdImported) throws ServiceException{

		/* --- XSD Validator -- */
		if(xsdPath == null){
			throw new ServiceException("New Instance failure: xsdPath is null");
		}
		InputStream isSchema = null;
		List<InputStream> listInputStreams = new ArrayList<InputStream>();
		XSDResourceResolver xsdResourceResolver = null;
		
		// check schema
		try{
			File f = new File(xsdPath);
			if(f.exists()){
				isSchema = new FileInputStream(f);
			}else{
				isSchema = cXmlRoot.getResourceAsStream(xsdPath);
				if(isSchema==null){
					isSchema = cXmlRoot.getResourceAsStream("/"+xsdPath);
				}
			}
			if(isSchema==null){
				throw new Exception("Creating InputStream from xsdPath["+xsdPath+"] failure");
			}
		}catch (Exception e) {
			try{
				if(isSchema!=null){
					isSchema.close();
				}
			}catch(Exception eClose){}
			throw new ServiceException("Init xsd schema failure: "+e.getMessage(),e);
		}
		
		// check schema imported
		if(xsdImported!=null && xsdImported.length>0){
		
			xsdResourceResolver = new XSDResourceResolver();
		
			for (int i = 0; i < xsdImported.length; i++) {
				InputStream is = null;
				try{
					File f = new File(xsdImported[i]);
					if(f.exists()){
						is = new FileInputStream(f);
					}else{
						is = cXmlRoot.getResourceAsStream(xsdImported[i]);
						if(is==null){
							is = cXmlRoot.getResourceAsStream("/"+xsdImported[i]);
						}
					}
					if(is==null){
						throw new Exception("Creating InputStream from xsdPath["+i+"]["+xsdImported[i]+"] failure");
					}
					xsdResourceResolver.addResource(f.getName(), is);
					listInputStreams.add(is);
				}catch (Exception e) {
					try{
						if(isSchema!=null){
							isSchema.close();
						}
					}catch(Exception eClose){}
					for (InputStream inputStream : listInputStreams) {
						try{
							if(inputStream!=null){
								inputStream.close();
							}
						}catch(Exception eClose){}
					}
					throw new ServiceException("Init xsd schema failure: "+e.getMessage(),e);
				}
			}
		}
		
		// init schema Validator
		try{
			if(xsdResourceResolver!=null){
				Constructor<?> constructor = xsdValidatorImpl.getConstructor(Logger.class,String.class,LSResourceResolver.class,InputStream.class);
				this.xsdValidator = (AbstractValidatoreXSD) constructor.newInstance(log,"org.apache.xerces.jaxp.validation.XMLSchemaFactory",xsdResourceResolver,isSchema);
			}else{
				Constructor<?> constructor = xsdValidatorImpl.getConstructor(Logger.class,String.class,InputStream.class);
				this.xsdValidator = (AbstractValidatoreXSD) constructor.newInstance(log,"org.apache.xerces.jaxp.validation.XMLSchemaFactory",isSchema);
			}
		}catch (Exception e) {
			throw new ServiceException("Init xsd schema failure: "+e.getMessage(),e);
		}finally{
			try{
				if(isSchema!=null){
					isSchema.close();
				}
			}catch(Exception eClose){}
			for (InputStream inputStream : listInputStreams) {
				try{
					if(inputStream!=null){
						inputStream.close();
					}
				}catch(Exception eClose){}
			}
		}
	}

	public AbstractValidatoreXSD getXsdValidator() {
		return this.xsdValidator;
	}

	
	private static AbstractXMLUtils xmlUtils = null;
	private static synchronized void initXmlUtils(){
		if(xmlUtils==null){
			xmlUtils = XMLUtils.getInstance();
		}
	}
	public static void validate(BaseBean object,Logger log, AbstractValidatoreXSD xsdValidator) throws ServiceException, ValidationException {

		if(object==null){
			throw new ServiceException("Paramter object is not defined");
		}
		if(log==null){
			throw new ServiceException("Paramter log is not defined");
		}
		if(xsdValidator==null){
			throw new ServiceException("Paramter xsdValidator is not defined");
		}
		
		String xml = null;
		try{
			xml = object.toXml_Jaxb();
		}catch(Exception e){
			log.debug("XMLSerialization error with jaxb: "+e.getMessage(),e);
			try{
				xml = object.toXml_Jibx();
			}catch(Exception eInternal){
				log.error("XMLSerialization error with jibx: "+e.getMessage(),e);
				// La serializzazione jibx ritorna anche il motivo della mancata serializzazione
				// Es. quando un oggetto required is null
				throw new ValidationException(eInternal.getMessage(),eInternal);
			}
		}
		
		if(xmlUtils==null){
			initXmlUtils();
		}

		try{
			xsdValidator.valida(xmlUtils.newDocument(xml.getBytes()));
		}catch(XMLException xmlException){
			throw new ValidationException("Object is not valid for xml structure: "+xmlException.getMessage(),xmlException);
		}catch(Exception e){
			throw new ValidationException("Object is not valid: "+e.getMessage(),e);
		}

	}
}
