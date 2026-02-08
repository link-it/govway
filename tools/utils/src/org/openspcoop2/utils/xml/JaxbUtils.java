/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.openspcoop2.utils.resources.FileSystemUtilities;


/**
* Utility per la gestione di xml tramite Jaxb
*
* @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class JaxbUtils {
	
	private JaxbUtils() {}

	// Rimuove i caratteri non validi in XML 1.0 da un array di byte UTF-8.
	// I caratteri validi in XML 1.0 sono: 0x09 (tab), 0x0A (LF), 0x0D (CR), 0x20-0xD7FF, 0xE000-0xFFFD, 0x10000-0x10FFFF
	// In UTF-8 i caratteri 0x00-0x1F (esclusi 0x09, 0x0A, 0x0D) sono codificati come singolo byte,
	// quindi è sicuro filtrarli direttamente a livello di byte.
	private static byte[] sanitizeXml10(byte[] xml) {
		ByteArrayOutputStream out = new ByteArrayOutputStream(xml.length);
		for (byte b : xml) {
			int unsigned = b & 0xFF;
			if (unsigned < 0x20) {
				// Mantieni solo tab (0x09), line feed (0x0A) e carriage return (0x0D)
				if (unsigned == 0x09 || unsigned == 0x0A || unsigned == 0x0D) {
					out.write(b);
				}
				// Altrimenti scarta il byte (caratteri di controllo invalidi in XML 1.0)
			} else {
				out.write(b);
			}
		}
		return out.toByteArray();
	}

	private static InputStream sanitizeXml10(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int len;
		while ((len = is.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		byte[] sanitized = sanitizeXml10(baos.toByteArray());
		return new ByteArrayInputStream(sanitized);
	}

	private static Map<String, JAXBContext> mapJAXBContext = new ConcurrentHashMap<>();
	private static synchronized void initJAXBContext(String packageName) throws JAXBException{
		if(!JaxbUtils.mapJAXBContext.containsKey(packageName)){
			JaxbUtils.mapJAXBContext.put(packageName, JAXBContext.newInstance(packageName) );
		}
	}
	private static JAXBContext getJAXBContext(String packageName) throws JAXBException{
		if(!JaxbUtils.mapJAXBContext.containsKey(packageName)){
			JaxbUtils.initJAXBContext(packageName);
		}
		return JaxbUtils.mapJAXBContext.get(packageName);
	}
	
	
	/**
	 * Metodo che si occupa della generazione di un oggetto a partire da un file XML. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML. 
	 * 
	 * @param xmlFileName file XML
	 * @param classType Tipo di classe dell'oggetto da leggere
	 * @return L'oggetto letto dal file XML.
	 * @throws JAXBException 
	 */   
	public static Object xmlToObj(String xmlFileName,Class<?> classType)
			throws JAXBException, IOException{

		JAXBContext jc = JaxbUtils.getJAXBContext(classType.getPackage().getName());
		Unmarshaller uctx = jc.createUnmarshaller();
		FileInputStream fis = new FileInputStream(xmlFileName);
		InputStream sanitizedIs = null;
		Object objectRead = null;
		try{
			sanitizedIs = sanitizeXml10(fis);
			objectRead = uctx.unmarshal(sanitizedIs);
		}finally{
			try{
				fis.close();
			}catch(Exception eis){
				// close
			}
			try{
				if(sanitizedIs!=null) {
					sanitizedIs.close();
				}
			}catch(Exception eis){
				// close
			}
		}
		if(objectRead instanceof JAXBElement<?>){
			return ((JAXBElement<?>)objectRead).getValue();
		}
		else{
			return objectRead;
		}
	}       

	/**
	 * Metodo che si occupa della generazione di un file XML a partire da un oggetto RegistroServizi. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML da generare. 
	 * 
	 * @param xmlFileName Nome del file XML da utilizzare
	 * @param classType Tipo di classe dell'oggetto da scrivere
	 * @param object Oggetto da Scrivere
	 * @throws JAXBException 
	 * 
	 */   
	public static void objToXml(String xmlFileName,Class<?> classType,Object object) 
			throws JAXBException{
		JaxbUtils.objToXml(xmlFileName,classType,object,false);
	}
	public static void objToXml(String xmlFileName,Class<?> classType,Object object,boolean prettyDocument) 
			throws JAXBException{

		// Se esiste gia' lo sovrascrive
		File file = new File(xmlFileName);
		if(file.exists()){
			FileSystemUtilities.deleteFile(file);
		}  

		JAXBContext jc = JaxbUtils.getJAXBContext(classType.getPackage().getName());
		Marshaller uctx = jc.createMarshaller();
		if(prettyDocument)
			uctx.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		else
			uctx.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		uctx.marshal(object, file);
		
	}    
	
	
	/**
	 * Metodo che si occupa di generare un file a partire da un oggetto
	 * 
	 * @param xmlFileName Nome del file XML da utilizzare
	 * @param object Oggetto da Scrivere
	 * 
	 */   
	public static void objToXml(String xmlFileName,byte[] object) 
			throws IOException{

		// Se esiste gia' lo sovrascrive
		File file = new File(xmlFileName);
		if(file.exists()){
			FileSystemUtilities.deleteFile(file);
		}  

		FileOutputStream fileOut =  new FileOutputStream(xmlFileName);
		try{
			fileOut.write(object);
			fileOut.flush();
		}finally{
			try{
				fileOut.close();
			}catch(Exception eis){
				// ignore
			}	
		}
	}   
	
	
	/**
	 * Metodo che si occupa della generazione di un oggetto a partire da un file XML. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML. 
	 * 
	 * @param classType Tipo di classe dell'oggetto da leggere
	 * @return L'oggetto letto dal file XML.
	 * @throws JAXBException 
	 */   
	public static Object xmlToObj(InputStream i,Class<?> classType)
			throws JAXBException, IOException{

		JAXBContext jc = JaxbUtils.getJAXBContext(classType.getPackage().getName());
		Unmarshaller uctx = jc.createUnmarshaller();
		InputStream sanitizedIs = null;
		Object objectRead = null;
		try{
			sanitizedIs = sanitizeXml10(i);
			objectRead = uctx.unmarshal(sanitizedIs);
		}finally{
			try{
				i.close();
			}catch(Exception eis){
				// close
			}
			try{
				if(sanitizedIs!=null) {
					sanitizedIs.close();
				}
			}catch(Exception eis){
				// close
			}
		}
		if(objectRead instanceof JAXBElement<?>){
			return ((JAXBElement<?>)objectRead).getValue();
		}
		else{
			return objectRead;
		}

	}       

	/**
	 * Metodo che si occupa della generazione di un file XML a partire da un oggetto RegistroServizi. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML da generare. 
	 * 
	 * @param classType Tipo di classe dell'oggetto da scrivere
	 * @param object Oggetto da Scrivere
	 * @throws JAXBException 
	 * 
	 */   
	public static void objToXml(OutputStream out,Class<?> classType,Object object) 
			throws JAXBException{
		JaxbUtils.objToXml(out,classType,object,false);
	}
	public static void objToXml(OutputStream out,Class<?> classType,Object object,boolean prettyDocument) 
			throws JAXBException{
		JaxbUtils.objToXml(out,classType,object,prettyDocument,false);
	}
	public static void objToXml(OutputStream out,Class<?> classType,Object object,boolean prettyDocument, boolean omitXmlDeclaration) 
			throws JAXBException{

		JAXBContext jc = JaxbUtils.getJAXBContext(classType.getPackage().getName());
		Marshaller uctx = jc.createMarshaller();
		if(prettyDocument)
			uctx.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		else
			uctx.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		
		if(omitXmlDeclaration)
			uctx.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		
		uctx.marshal(object, out);
		
	}    
	
}
