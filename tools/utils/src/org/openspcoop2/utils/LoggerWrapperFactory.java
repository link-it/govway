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


package org.openspcoop2.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.json.JsonConfigurationFactory;
import org.apache.logging.log4j.core.config.properties.PropertiesConfigurationFactory;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.logging.log4j.core.config.yaml.YamlConfigurationFactory;

/**
 * Libreria contenente metodi di utilità per gestire i log con log4j2
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
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
	
	public static void setDefaultConsoleLogConfiguration(Level level) throws UtilsException{
		setDefaultLogConfiguration(level, true, "%p <%d{dd-MM-yyyy HH:mm:ss.SSS}> %C.%M(%L): %m %n %n", null, null);
	}
	public static void setDefaultConsoleLogConfiguration(Level level,
			String layout) throws UtilsException{
		setDefaultLogConfiguration(level, true, layout, null, null);
	}
	public static void setDefaultLogConfiguration(Level level,boolean console,String layoutConsole,
			File file,String layoutFile) throws UtilsException{
		
		if(layoutConsole==null){
			layoutConsole="%p <%d{dd-MM-yyyy HH:mm:ss.SSS}> %C.%M(%L): %m %n %n";
		}
		if(layoutFile==null){
			layoutFile="%p <%d{dd-MM-yyyy HH:mm:ss.SSS}> %C.%M(%L): %m %n %n";
		}
		
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setConfigurationName("ConsoleDefault");
		builder.setStatusLevel(Level.ERROR);
		// Console Appender
		if(console){
			AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").
					addAttribute("target",  ConsoleAppender.Target.SYSTEM_OUT);
			appenderBuilder.add(builder.newLayout("PatternLayout")
					.addAttribute("pattern", layoutConsole));
			builder.add(appenderBuilder);
		}
		if(file!=null){
			// create a rolling file appender
			LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
			    .addAttribute("pattern", layoutFile);
			ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
			    .addComponent(builder.newComponent("CronTriggeringPolicy").addAttribute("schedule", "0 0 0 * * ?"))
			    .addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "100M"));
			AppenderComponentBuilder appenderBuilder = builder.newAppender("rolling", "RollingFile")
			    .addAttribute("fileName", file.getAbsolutePath())
			    .addAttribute("filePattern", file.getAbsolutePath()+".%i")
			    .add(layoutBuilder)
			    .addComponent(triggeringPolicy);
			builder.add(appenderBuilder);
		}
		// RootLogger
		RootLoggerComponentBuilder rootLoggerBuilder = builder.newRootLogger(level);
		if(console){
			rootLoggerBuilder.add(builder.newAppenderRef("Stdout"));
		}
		if(file!=null){
			rootLoggerBuilder.add(builder.newAppenderRef("rolling"));
		}
		builder.add(rootLoggerBuilder);
		// Initialize
		Configurator.initialize(builder.build());
	}
	
	public static void setLogConfiguration(File file) throws UtilsException{
		setLogConfiguration(getContext(), file, false);
	}
	public static void setLogConfiguration(File file,boolean append) throws UtilsException{
		setLogConfiguration(getContext(), file, append);
	}
	private static void setLogConfiguration(LoggerContext context, File file,boolean append) throws UtilsException{
		String filePath = "fs";
		try{
			if(file==null){
				throw new Exception("Resource file undefined");
			}
			filePath = file.getAbsolutePath();
			if(file.exists()){
				if(append){
					appendConfiguration(context, file.toURI());
				}
				else{
					newConfiguration(context, file.toURI());
				}
			}
			else{
				throw new UtilsException("Resource not exists");
			}
		}catch(Exception e){
			throw new UtilsException("Setting Logging Configuration (resource ["+filePath+"]): "+e.getMessage(),e);
		}
	}
	public static void setLogConfiguration(String name) throws UtilsException{
		setLogConfiguration(getContext(), name, false);
	}
	public static void setLogConfiguration(String name,boolean append) throws UtilsException{
		setLogConfiguration(getContext(), name, append);
	}
	private static void setLogConfiguration(LoggerContext context, String name,boolean append) throws UtilsException{
		try{
			if(name==null){
				throw new Exception("Resource name undefined");
			}
			File f = new File(name);
			if(f.exists()){
				if(append){
					appendConfiguration(context, f.toURI());
				}
				else{
					newConfiguration(context, f.toURI());
				}
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
					if(append){
						appendConfiguration(context, url.toURI());
					}
					else{
						newConfiguration(context, url.toURI());
					}
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
		setLogConfiguration(getContext(), url, false);
	}
	public static void setLogConfiguration(URL url,boolean append) throws UtilsException{
		setLogConfiguration(getContext(), url, append);
	}
	private static void setLogConfiguration(LoggerContext context, URL url,boolean append) throws UtilsException{
		try{
			if(url==null){
				throw new Exception("Resource URL undefined");
			}
			if(append){
				appendConfiguration(context, url.toURI());
			}
			else{
				newConfiguration(context, url.toURI());
			}
		}catch(Exception e){
			throw new UtilsException("Setting Logging Configuration failed (url ["+url+"]): "+e.getMessage(),e);
		}
	}
	public static void setLogConfiguration(URI uri) throws UtilsException{
		setLogConfiguration(getContext(), uri, false);
	}
	public static void setLogConfiguration(URI uri,boolean append) throws UtilsException{
		setLogConfiguration(getContext(), uri, append);
	}
	private static void setLogConfiguration(LoggerContext context, URI uri,boolean append) throws UtilsException{
		try{
			if(uri==null){
				throw new Exception("Resource URI undefined");
			}
			if(append){
				appendConfiguration(context, uri);
			}
			else{
				newConfiguration(context, uri);
			}
		}catch(Exception e){
			throw new UtilsException("Setting Logging Configuration failed (uri ["+uri+"]): "+e.getMessage(),e);
		}
	}
	public static void setLogConfiguration(Properties props) throws UtilsException{
		setLogConfiguration(getContext(), props, false);
	}
	public static void setLogConfiguration(Properties props,boolean append) throws UtilsException{
		setLogConfiguration(getContext(), props, append);
	}
	private static void setLogConfiguration(LoggerContext context, Properties props,boolean append) throws UtilsException{
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

			if(append){
				appendConfiguration(context, fTmp.toURI());
			}
			else{
				newConfiguration(context, fTmp.toURI());
			}
			
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

	
	private static void newConfiguration(LoggerContext context, URI configUri) throws Exception{
		context.setConfigLocation(configUri);
	}
	private static synchronized void appendConfiguration(LoggerContext context, URI configUri) throws Exception{
		
		//System.out.println("APPEND LOG ["+configUri+"]");
		
		Configuration actualConfiguration = context.getConfiguration();
		
		ConfigurationFactory configurationFactory = ConfigurationFactory.getInstance();
		Configuration appendConfiguration = configurationFactory.getConfiguration(actualConfiguration.getName(), configUri);
		appendConfiguration.initialize();
		
		Map<String, Appender> mapAppenders = appendConfiguration.getAppenders();
		if(mapAppenders.size()>0){
			Iterator<String> appenderNameIterator = mapAppenders.keySet().iterator();
			while (appenderNameIterator.hasNext()) {
				String appenderName = (String) appenderNameIterator.next();
				Appender appender = mapAppenders.get(appenderName);
				appender.start();
				//System.out.println("ADD APPENDER ["+appenderName+"]");
				actualConfiguration.addAppender(appender);
			}
		}
		
		Map<String, LoggerConfig> mapLoggers = appendConfiguration.getLoggers();
		if(mapLoggers.size()>0){
			Iterator<String> loggerNameIterator = mapLoggers.keySet().iterator();
			while (loggerNameIterator.hasNext()) {
				String loggerName = (String) loggerNameIterator.next();				
				LoggerConfig logger = mapLoggers.get(loggerName);
				//System.out.println("ADD LOGGER ["+loggerName+"]");
				actualConfiguration.addLogger(loggerName, logger);
			}
		}
		
		//System.out.println("APPEND LOG ["+configUri+"] FINE");
		
	}

}
