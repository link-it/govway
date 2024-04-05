/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
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
	
	private LoggerWrapperFactory () {}

	
	
	// ** Ritorna il logger */
	
	// NAME[org.apache.logging.slf4j.Log4jLogger] FACT[org.apache.logging.slf4j.Log4jLoggerFactory]
	// I valori sopra sono se si possiede un corretto binding di log4j su slf.
	// Se esistono più jar contenenti l'implementazione di un binding, non è detto che venga preso log4j.
	
	public static org.slf4j.Logger getLogger(Class<?> c){
		return org.slf4j.LoggerFactory.getLogger(c);
	}
	public static org.slf4j.Logger getLogger(String name){
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
	
	private static final String FORMAT_MSG = "%p <%d{dd-MM-yyyy HH:mm:ss.SSS}> %C.%M(%L): %m %n %n";
	
	public static void setDefaultConsoleLogConfiguration(Level level) {
		setDefaultLogConfiguration(level, true, FORMAT_MSG, null, null);
	}
	public static void setDefaultConsoleLogConfiguration(Level level,
			String layout) {
		setDefaultLogConfiguration(level, true, layout, null, null);
	}
	public static void setDefaultLogConfiguration(Level level,boolean console,String layoutConsole,
			File file,String layoutFile) {
		
		// log4j2.disable.jmx=true
		System.setProperty("log4j2.disable.jmx", "true");
		
		if(layoutConsole==null){
			layoutConsole=FORMAT_MSG;
		}
		if(layoutFile==null){
			layoutFile=FORMAT_MSG;
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
				throw new UtilsException("Resource file undefined");
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
				throw new UtilsException("Resource name undefined");
			}
			File f = new File(name);
			setLogConfiguration(context, name, append, f);
		}catch(Exception e){
			throw new UtilsException("Setting Logging Configuration failed (resource ["+name+"]): "+e.getMessage(),e);
		}
	}
	private static void setLogConfiguration(LoggerContext context, String name,boolean append, File f) throws UtilsException, URISyntaxException{
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
				throw new UtilsException("Resource URL undefined");
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
				throw new UtilsException("Resource URI undefined");
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
		if(props==null){
			throw new UtilsException("Resource Properties undefined");
		}
		
		File fTmp = null;
		try {
			fTmp = Utilities.createTempPath("op2_log", ".properties").toFile();
		}catch(Exception e){
			throw new UtilsException("Setting Logging Configuration failed: "+e.getMessage(),e);
		}
		
		try(FileOutputStream foutTmp = new FileOutputStream(fTmp);){
			props.store(foutTmp, "Tmp Configuration");
			foutTmp.flush();
			
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
				if(fTmp!=null){
					Files.delete(fTmp.toPath());
				}
			}catch(Exception e){
				// close
			}
		}
	}

	
	private static void newConfiguration(LoggerContext context, URI configUri) {
		context.setConfigLocation(configUri);
	}
	private static synchronized void appendConfiguration(LoggerContext context, URI configUri) {
		
		/**System.out.println("APPEND LOG ["+configUri+"]");*/
		
		Configuration actualConfiguration = context.getConfiguration();
		
		ConfigurationFactory configurationFactory = ConfigurationFactory.getInstance();
		Configuration appendConfiguration = configurationFactory.getConfiguration(new LoggerContext(actualConfiguration.getName()), actualConfiguration.getName(), configUri);
		appendConfiguration.initialize();
		
		Map<String, Appender> mapAppenders = appendConfiguration.getAppenders();
		if(mapAppenders.size()>0){
			Iterator<String> appenderNameIterator = mapAppenders.keySet().iterator();
			while (appenderNameIterator.hasNext()) {
				String appenderName = appenderNameIterator.next();
				Appender appender = mapAppenders.get(appenderName);
				appender.start();
				/**System.out.println("ADD APPENDER ["+appenderName+"]");*/
				actualConfiguration.addAppender(appender);
			}
		}
		
		Map<String, LoggerConfig> mapLoggers = appendConfiguration.getLoggers();
		if(mapLoggers.size()>0){
			Iterator<String> loggerNameIterator = mapLoggers.keySet().iterator();
			while (loggerNameIterator.hasNext()) {
				String loggerName = loggerNameIterator.next();				
				LoggerConfig logger = mapLoggers.get(loggerName);
				/**System.out.println("ADD LOGGER ["+loggerName+"]");*/
				actualConfiguration.addLogger(loggerName, logger);
			}
		}
		
		/**System.out.println("APPEND LOG ["+configUri+"] FINE");*/
		
	}

}
