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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


/**
* Utility per la gestione di xml tramite Jaxb
*
* @author Poli Andrea <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class JaxbUtils {

	private static Hashtable<String, JAXBContext> mapJAXBContext = new Hashtable<String, JAXBContext>();
	private static synchronized void initJAXBContext(String packageName) throws JAXBException{
		if(mapJAXBContext.containsKey(packageName)==false){
			mapJAXBContext.put(packageName, JAXBContext.newInstance(packageName) );
		}
	}
	private static JAXBContext getJAXBContext(String packageName) throws JAXBException{
		if(mapJAXBContext.containsKey(packageName)==false){
			initJAXBContext(packageName);
		}
		return mapJAXBContext.get(packageName);
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

		JAXBContext jc = getJAXBContext(classType.getPackage().getName());
		Unmarshaller uctx = jc.createUnmarshaller(); 
		FileInputStream fis = new FileInputStream(xmlFileName);
		Object objectRead = null;
		try{
			objectRead = uctx.unmarshal(fis);
		}finally{
			try{
				fis.close();
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
	 * @param xmlFileName Nome del file XML da utilizzare
	 * @param classType Tipo di classe dell'oggetto da scrivere
	 * @param object Oggetto da Scrivere
	 * @throws JAXBException 
	 * 
	 */   
	public static void objToXml(String xmlFileName,Class<?> classType,Object object) 
			throws java.io.FileNotFoundException, JAXBException{
		objToXml(xmlFileName,classType,object,false);
	}
	public static void objToXml(String xmlFileName,Class<?> classType,Object object,boolean prettyDocument) 
			throws java.io.FileNotFoundException, JAXBException{

		// Se esiste gia' lo sovrascrive
		File file = new File(xmlFileName);
		if(file.exists()){
			file.delete();
		}  

		JAXBContext jc = getJAXBContext(classType.getPackage().getName());
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
			file.delete();
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

		JAXBContext jc = getJAXBContext(classType.getPackage().getName());
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
		objToXml(out,classType,object,false);
	}
	public static void objToXml(OutputStream out,Class<?> classType,Object object,boolean prettyDocument) 
			throws java.io.FileNotFoundException, JAXBException{

		JAXBContext jc = getJAXBContext(classType.getPackage().getName());
		Marshaller uctx = jc.createMarshaller();
		if(prettyDocument)
			uctx.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		else
			uctx.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
		uctx.marshal(object, out);
		
	}    
	
}
