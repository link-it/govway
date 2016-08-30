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


package org.openspcoop2.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.properties.PropertiesConfigurationFactory;
import org.apache.logging.log4j.core.config.json.JsonConfigurationFactory;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory;

/**
 * Libreria contenente metodi di utilità per gestire i log con log4j2
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: mergefairy $
 * @version $Rev: 12095 $, $Date: 2016-07-27 11:35:23 +0200 (Wed, 27 Jul 2016) $
 */
public class LoggerWrapperFactory {

	
	
	// ** Ritorna il logger */
	
	// NAME[org.apache.logging.slf4j.Log4jLogger] FACT[org.apache.logging.slf4j.Log4jLoggerFactory]
	// I valori sopra sono se si possiede un corretto binding di log4j su slf.
	// Se esistono più jar contenenti l'implementazione di un binding, non è detto che venga preso log4j.
	
	public static org.slf4j.Logger getLogger(Class<?> c){
		//System.out.println("INFO NAME["+org.slf4j.LoggerFactory.getLogger(c).getClass().getName()+"] FACT["+org.slf4j.LoggerFactory.getILoggerFactory().getClass().getName()+"]");
		return org.slf4j.LoggerFactory.getLogger(c);
	}
	public static org.slf4j.Logger getLogger(String name){
		//System.out.println("INFO NAME["+org.slf4j.LoggerFactory.getLogger(name).getClass().getName()+"] FACT["+org.slf4j.LoggerFactory.getILoggerFactory().getClass().getName()+"]");
		return org.slf4j.LoggerFactory.getLogger(name);
	}
	public static org.apache.logging.log4j.Logger getLoggerImpl(Class<?> c){
		return org.apache.logging.log4j.LogManager.getLogger(c);
	}
	public static org.apache.logging.log4j.Logger getLoggerImpl(String name){
		return org.apache.logging.log4j.LogManager.getLogger(name);
	}
	
	
	
	
	// ** Imposta tipo di ConnectionFactory (Default is xml) */
	
	public static void setPropertiesConfigurationFactory(){
		ConfigurationFactory.setConfigurationFactory(new PropertiesConfigurationFactory());
	}
	public static void setJSonConfigurationFactory(){
		ConfigurationFactory.setConfigurationFactory(new JsonConfigurationFactory());
	}
	public static void setXmlConfigurationFactory(){
		ConfigurationFactory.setConfigurationFactory(new XmlConfigurationFactory());
	}
	public static void setYamlConfigurationFactory(){
		ConfigurationFactory.setConfigurationFactory(new YamlConfigurationFactory());
	}
	
	
	
	
	// ** Ritorna Context log4J2 */
	
	private static LoggerContext getContext(){
		return (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
	}
	@SuppressWarnings("unused")
	private static LoggerContext getContext(boolean currentContext){
		return (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(currentContext);
	}
	

	// ** Imposta proprietà in configurazione log4J2 */
	
	public static void setLogConfiguration(File file) throws UtilsException{
		setLogConfiguration(getContext(), file);
	}
	private static void setLogConfiguration(LoggerContext context, File file) throws UtilsException{
		String filePath = "fs";
		try{
			if(file==null){
				throw new Exception("Resource file undefined");
			}
			filePath = file.getAbsolutePath();
			if(file.exists()){
				context.setConfigLocation(file.toURI());
			}
			else{
				throw new UtilsException("Resource not exists");
			}
		}catch(Exception e){
			throw new UtilsException("Setting Logging Configuration (resource ["+filePath+"]): "+e.getMessage(),e);
		}
	}
	public static void setLogConfiguration(String name) throws UtilsException{
		setLogConfiguration(getContext(), name);
	}
	private static void setLogConfiguration(LoggerContext context, String name) throws UtilsException{
		try{
			if(name==null){
				throw new Exception("Resource name undefined");
			}
			File f = new File(name);
			if(f.exists()){
				context.setConfigLocation(f.toURI());
			}
			else{
				String newName = null;
				if(name.trim().startsWith("/")){
					newName = name;
				}
				else{
					newName = "/" + name;
				}
				URL url = Utilities.class.getResource(newName);
				if(url!=null){
					context.setConfigLocation(url.toURI());
				}
				else{
					throw new UtilsException("Resource ["+name+"] not found");
				}
			}
		}catch(Exception e){
			throw new UtilsException("Setting Logging Configuration failed (resource ["+name+"]): "+e.getMessage(),e);
		}
	}
	public static void setLogConfiguration(URL url) throws UtilsException{
		setLogConfiguration(getContext(), url);
	}
	private static void setLogConfiguration(LoggerContext context, URL url) throws UtilsException{
		try{
			if(url==null){
				throw new Exception("Resource URL undefined");
			}
			context.setConfigLocation(url.toURI());
		}catch(Exception e){
			throw new UtilsException("Setting Logging Configuration failed (url ["+url+"]): "+e.getMessage(),e);
		}
	}
	public static void setLogConfiguration(URI uri) throws UtilsException{
		setLogConfiguration(getContext(), uri);
	}
	private static void setLogConfiguration(LoggerContext context, URI uri) throws UtilsException{
		try{
			if(uri==null){
				throw new Exception("Resource URI undefined");
			}
			context.setConfigLocation(uri);
		}catch(Exception e){
			throw new UtilsException("Setting Logging Configuration failed (uri ["+uri+"]): "+e.getMessage(),e);
		}
	}
	public static void setLogConfiguration(Properties props) throws UtilsException{
		setLogConfiguration(getContext(), props);
	}
	private static void setLogConfiguration(LoggerContext context, Properties props) throws UtilsException{
		File fTmp = null;
		FileOutputStream foutTmp = null;
		try{
			if(props==null){
				throw new Exception("Resource Properties undefined");
			}
			fTmp = File.createTempFile("op2_log", ".properties");
			foutTmp = new FileOutputStream(fTmp);
			props.store(foutTmp, "Tmp Configuration");
			foutTmp.flush();
			foutTmp.close();
			foutTmp = null;

			context.setConfigLocation(fTmp.toURI());
			
		}catch(Exception e){
			throw new UtilsException("Setting Logging Configuration failed: "+e.getMessage(),e);
		}finally{
			try{
				if(foutTmp!=null){
					foutTmp.close();
				}
			}catch(Exception e){}
			try{
				if(fTmp!=null){
					fTmp.delete();
				}
			}catch(Exception e){}
		}
	}


}
