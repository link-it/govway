/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;


/**
* Utility per la gestione di xml tramite Jaxb
*
* @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class JaxbUtils {

	private static Map<String, JAXBContext> mapJAXBContext = new ConcurrentHashMap<String, JAXBContext>();
	private static synchronized void initJAXBContext(String packageName) throws JAXBException{
		if(JaxbUtils.mapJAXBContext.containsKey(packageName)==false){
			JaxbUtils.mapJAXBContext.put(packageName, JAXBContext.newInstance(packageName) );
		}
	}
	private static JAXBContext getJAXBContext(String packageName) throws JAXBException{
		if(JaxbUtils.mapJAXBContext.containsKey(packageName)==false){
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
			throws java.io.FileNotFoundException, JAXBException{  

		JAXBContext jc = JaxbUtils.getJAXBContext(classType.getPackage().getName());
		Unmarshaller uctx = jc.createUnmarshaller(); 
		FileInputStream fis = new FileInputStream(xmlFileName);
		Object objectRead = null;
		try{
			objectRead = uctx.unmarshal(fis);
		}finally{
			try{
				fis.close();
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
			throws java.io.FileNotFoundException, JAXBException{
		JaxbUtils.objToXml(xmlFileName,classType,object,false);
	}
	public static void objToXml(String xmlFileName,Class<?> classType,Object object,boolean prettyDocument) 
			throws java.io.FileNotFoundException, JAXBException{

		// Se esiste gia' lo sovrascrive
		File file = new File(xmlFileName);
		if(file.exists()){
			if(!file.delete()) {
				// ignore
			}
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
			throws java.io.FileNotFoundException,IOException{

		// Se esiste gia' lo sovrascrive
		File file = new File(xmlFileName);
		if(file.exists()){
			if(!file.delete()) {
				// ignore
			}
		}  

		FileOutputStream fileOut =  new FileOutputStream(xmlFileName);
		try{
			fileOut.write(object);
			fileOut.flush();
		}catch(IOException e){
			throw e; 
		}finally{
			try{
				fileOut.close();
			}catch(Exception eis){}	
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
			throws java.io.FileNotFoundException, JAXBException{  

		JAXBContext jc = JaxbUtils.getJAXBContext(classType.getPackage().getName());
		Unmarshaller uctx = jc.createUnmarshaller(); 
		Object objectRead = null;
		try{
			objectRead = uctx.unmarshal(i);
		}finally{
			try{
				i.close();
			}catch(Exception eis){}
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
			throws java.io.FileNotFoundException, JAXBException{
		JaxbUtils.objToXml(out,classType,object,false);
	}
	public static void objToXml(OutputStream out,Class<?> classType,Object object,boolean prettyDocument) 
			throws java.io.FileNotFoundException, JAXBException{
		JaxbUtils.objToXml(out,classType,object,prettyDocument,false);
	}
	public static void objToXml(OutputStream out,Class<?> classType,Object object,boolean prettyDocument, boolean omitXmlDeclaration) 
			throws java.io.FileNotFoundException, JAXBException{

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
