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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;

/**
* Utility per la gestione di xml tramite JiBX
*
* @author Poli Andrea <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class JiBXUtils {

	private static Hashtable<String, IBindingFactory> mapBindingFactory = new Hashtable<String, IBindingFactory>();
	private static synchronized void initBindingFactory(Class<?> classType) throws org.jibx.runtime.JiBXException{
		if(mapBindingFactory.containsKey(classType.getName())==false){
			mapBindingFactory.put(classType.getName(), BindingDirectory.getFactory(classType) );
		}
	}
	private static IBindingFactory getBindingFactory(Class<?> classType) throws org.jibx.runtime.JiBXException{
		if(mapBindingFactory.containsKey(classType.getName())==false){
			initBindingFactory(classType);
		}
		return mapBindingFactory.get(classType.getName());
	}
	
	
	
	/**
	 * Metodo che si occupa della generazione di un oggetto a partire da un file XML. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML. 
	 * 
	 * @param xmlFileName file XML
	 * @param classType Tipo di classe dell'oggetto da leggere
	 * @return L'oggetto letto dal file XML.
	 */   
	public static Object xmlToObj(String xmlFileName,Class<?> classType) 
			throws org.jibx.runtime.JiBXException,java.io.FileNotFoundException{  

		IBindingFactory bfact = getBindingFactory(classType);                      
		IUnmarshallingContext uctx = bfact.createUnmarshallingContext();   
		FileInputStream fis = new FileInputStream(xmlFileName);
		Object objectRead = null;
		try{
			objectRead = uctx.unmarshalDocument(fis,null);
		}catch(org.jibx.runtime.JiBXException e){
			throw e; 
		}finally{
			try{
				fis.close();
			}catch(Exception eis){}
		}
		return objectRead;
	}       

	/**
	 * Metodo che si occupa della generazione di un file XML a partire da un oggetto RegistroServizi. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML da generare. 
	 * 
	 * @param xmlFileName Nome del file XML da utilizzare
	 * @param classType Tipo di classe dell'oggetto da scrivere
	 * @param object Oggetto da Scrivere
	 * 
	 */   
	public static void objToXml(String xmlFileName,Class<?> classType,Object object) 
			throws org.jibx.runtime.JiBXException,java.io.FileNotFoundException{
		objToXml(xmlFileName,classType,object,false);
	}
	public static void objToXml(String xmlFileName,Class<?> classType,Object object,boolean prettyDocument) 
			throws org.jibx.runtime.JiBXException,java.io.FileNotFoundException{

		// Se esiste gia' lo sovrascrive
		File file = new File(xmlFileName);
		if(file.exists()){
			file.delete();
		}  

		IBindingFactory bfact = getBindingFactory(classType);
		FileOutputStream fileOut =  new FileOutputStream(xmlFileName);
		try{
			IMarshallingContext uctx = bfact.createMarshallingContext();
			if(prettyDocument){
				uctx.setIndent(4);
			}else{
				uctx.setIndent(0);
			}
			uctx.marshalDocument(object,"UTF-8", null,fileOut);
		}catch(org.jibx.runtime.JiBXException e){
			throw e; 
		}finally{
			try{
				fileOut.close();
			}catch(Exception eis){}
		}
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
	 */   
	public static Object xmlToObj(InputStream i,Class<?> classType) 
			throws org.jibx.runtime.JiBXException,java.io.FileNotFoundException{  

		IBindingFactory bfact = getBindingFactory(classType);                      
		IUnmarshallingContext uctx = bfact.createUnmarshallingContext();   
		Object objectRead = null;
		try{
			objectRead = uctx.unmarshalDocument(i,null);
		}catch(org.jibx.runtime.JiBXException e){
			throw e; 
		}finally{
			try{
				i.close();
			}catch(Exception eis){}
		}
		return objectRead;
	}       

	/**
	 * Metodo che si occupa della generazione di un file XML a partire da un oggetto RegistroServizi. 
	 * Sono richiesti interattivamente i parametri che identificano il file XML da generare. 
	 * 
	 * @param classType Tipo di classe dell'oggetto da scrivere
	 * @param object Oggetto da Scrivere
	 * 
	 */   
	public static void objToXml(OutputStream out,Class<?> classType,Object object) 
			throws org.jibx.runtime.JiBXException,java.io.FileNotFoundException{
		objToXml(out,classType,object,false);
	}
	public static void objToXml(OutputStream out,Class<?> classType,Object object,boolean prettyDocument) 
			throws org.jibx.runtime.JiBXException,java.io.FileNotFoundException{

		IBindingFactory bfact = getBindingFactory(classType);
		try{
			IMarshallingContext uctx = bfact.createMarshallingContext();
			if(prettyDocument){
				uctx.setIndent(4);
			}else{
				uctx.setIndent(0);
			}
			uctx.marshalDocument(object,"UTF-8", null,out);
		}catch(org.jibx.runtime.JiBXException e){
			throw e; 
		}finally{
			try{
				out.close();
			}catch(Exception eis){}
		}
	}    
	
}
