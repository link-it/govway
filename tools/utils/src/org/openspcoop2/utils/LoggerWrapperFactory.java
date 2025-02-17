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


package org.openspcoop2.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
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
 * @author Tommaso Burlon (tommaso.burlon@link.it)
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
	
	
	private static String readProperty(Properties loggerProperties, 
			String propName, Map<String, String> applicationEnv, String defaultValue) {
		String value = System.getenv(applicationEnv.get(propName));
		if (value != null && StringUtils.isNotEmpty(value))
			return value;
		
		value = System.getenv(Costanti.ENV_LOG.get(propName));
		if (value != null && StringUtils.isNotEmpty(value))
			return value;
		
		value = System.getProperty(applicationEnv.get(propName));
		if (value != null && StringUtils.isNotEmpty(value))
			return value;
		
		value = System.getProperty(Costanti.ENV_LOG.get(propName));
		if (value != null && StringUtils.isNotEmpty(value))
			return value;
		
		value = loggerProperties.getProperty(propName);
		if (value != null && StringUtils.isNotEmpty(value))
			return value.trim();
		
		return defaultValue;
	}
	
	private static final String LOGGER_CATEGORY_NAME_SUFFIX = ".name";
	
	private static Set<String> getLoggersIdByName(Properties loggerProperties, List<String> loggerNames) {
		Set<String> enabledLoggers = new HashSet<>(loggerNames);
		Set<String> loggersId = new HashSet<>();
		boolean enabledAll = enabledLoggers.contains(Boolean.TRUE.toString());
		
		if (enabledLoggers.contains(Boolean.FALSE.toString()) && enabledLoggers.size() == 1)
			return loggersId;
		
		for (Map.Entry<Object, Object> p : loggerProperties.entrySet()) {
			String key = p.getKey().toString().trim();
			
			
			if (key.startsWith("logger.") && key.endsWith(LOGGER_CATEGORY_NAME_SUFFIX)) {
				String value = p.getValue().toString().trim();
				
				if (enabledAll || enabledLoggers.contains(value)) {
					String loggerId = key.substring(0, key.length() - LOGGER_CATEGORY_NAME_SUFFIX.length());
					loggersId.add(loggerId);
				}
			}
		}
		
		return loggersId;
	}
	
	private static Set<String> getLoggersIdByName(Properties loggerProperties, String propName, Map<String, String> applicationEnv) {
		List<String> enabledList = List.of(readProperty(loggerProperties, propName, applicationEnv, "false").split(","));
		enabledList = enabledList.stream().map(String::trim).collect(Collectors.toList());
		return getLoggersIdByName(loggerProperties, enabledList);
	}
	
	private static final String LOGGER_APPENDER_PREFIX = "appender.";
	private static final String LOGGER_APPENDER_NAME_SUFFIX = ".name";
	
	private static Set<String> getAppendersIdByName(Properties loggerProperties, Set<String> appenderNames) {
		Set<String> appenderIds = new HashSet<>();
		for (Map.Entry<Object, Object> p : loggerProperties.entrySet()) {
			String key = p.getKey().toString().trim();
			
			
			if (key.startsWith(LOGGER_APPENDER_PREFIX) 
					&& key.endsWith(LOGGER_APPENDER_NAME_SUFFIX)
					&& appenderNames.contains(p.getValue().toString().trim())) {
				
					String appenderId = key.substring(0, key.length() - LOGGER_APPENDER_NAME_SUFFIX.length());
					appenderIds.add(appenderId);
				}
			
		}
		
		return appenderIds;
	}
	
	private static Set<String> getAppenderIdByLoggerName(Properties loggerProperties, String propName, Map<String, String> applicationEnv) {
		Set<String> loggerIds = getLoggersIdByName(loggerProperties, propName, applicationEnv);
		Set<String> appenderNames = new HashSet<>();
		
		if (loggerIds.isEmpty())
			return Set.of();
		
		for (Map.Entry<Object, Object> p : loggerProperties.entrySet()) {
			String key = p.getKey().toString().trim();
			
			
			if (key.startsWith("logger.") && key.endsWith(".ref")) {
				String loggerId = key.substring(0, key.indexOf(".appenderRef"));
				if (loggerIds.contains(loggerId)) {
					appenderNames.add(p.getValue().toString().trim());
				}
			}
		}
		
		return getAppendersIdByName(loggerProperties, appenderNames);
	}
	
	
	/**
	 * Applica le patch necessarie per far utilizzare ai logger indicati lo stdout come appender
	 * @param loggerProperties
	 * @param applicationEnv
	 * @param vars
	 * @return
	 */
	private static final Pattern LOG_VARIABLE_PATTERN = Pattern.compile("%\\{([^\\}]+)\\}");
	private static Properties patchLoggersStdout(Properties loggerProperties, Map<String, String> applicationEnv, Map<String, String> vars) {
		Set<String> loggerIds = getLoggersIdByName(loggerProperties, 
				Costanti.PROP_ENABLE_STDOUT, 
				applicationEnv);
		
		for (String loggerId : loggerIds) {
			loggerProperties.put(loggerId + ".appenderRef.stdout.ref", "STDOUT");
		}
		
		for (Map.Entry<Object, Object> p : loggerProperties.entrySet()) {
			String key = p.getKey().toString().trim();
			if (key.startsWith(LOGGER_APPENDER_PREFIX)
					&& key.endsWith(LAYOUT_PATTERN_SUFFIX)) {
				String value = p.getValue().toString().trim();
				Matcher matcher = LOG_VARIABLE_PATTERN.matcher(value);
				
				value = matcher.replaceAll(m -> {
					String out = vars.get(m.group(1).trim());
					return Objects.requireNonNullElse(out, m.group());
				});
				
				p.setValue(value);
			}
			
		}
		
		return loggerProperties;
	}
	
	private static final String LAYOUT_TYPE_SUFFIX = ".layout.type";
	private static final String LAYOUT_PATTERN_SUFFIX= ".layout.pattern";
	private static final String LAYOUT_EVENT_TEMPLATE_URI_SUFFIX= ".layout.eventTemplateUri";
	
	/**
	 * Applica le patch necessarie per formattare in JSON l'output dei vari logger indicati
	 * @param loggerProperties
	 * @param applicationEnv
	 * @param vars
	 * @return
	 */
	private static Properties patchLoggersJSON(Properties loggerProperties, Map<String, String> applicationEnv, Map<String, String> vars) {
		Set<String> appenderIds = getAppenderIdByLoggerName(
				loggerProperties, 
				Costanti.PROP_ENABLE_JSON, 
				applicationEnv);
		
		String jsonTemplate = readProperty(loggerProperties, 
				Costanti.PROP_ENABLE_JSON_TEMPLATE,
				applicationEnv,
				"classpath:JsonLayout.json");
		if (jsonTemplate != null) {
			for (String appenderId : appenderIds) {
				loggerProperties.remove(appenderId + LAYOUT_TYPE_SUFFIX);
				loggerProperties.remove(appenderId + LAYOUT_PATTERN_SUFFIX);
				loggerProperties.put(appenderId + LAYOUT_TYPE_SUFFIX, "JsonTemplateLayout");
				loggerProperties.put(appenderId + LAYOUT_EVENT_TEMPLATE_URI_SUFFIX, jsonTemplate.trim());
			}
		}
		
		return applyPatchLoggersJSON(loggerProperties, vars);
	}
	private static Properties applyPatchLoggersJSON(Properties loggerProperties, Map<String, String> vars) {
		
		Set<String> patchedAppenderIds = new HashSet<>();
		for (Map.Entry<Object, Object> p : loggerProperties.entrySet()) {
			String key = p.getKey().toString().trim();
			addPatchLoggersJSONAppender(key, patchedAppenderIds, p, loggerProperties, vars);
		}
		
		return loggerProperties;
	}
	private static void addPatchLoggersJSONAppender(String key, Set<String> patchedAppenderIds, Map.Entry<Object, Object> p, Properties loggerProperties, Map<String, String> vars) {
		if (key.startsWith("appender")
				&& key.endsWith(LAYOUT_TYPE_SUFFIX)) {
			String value = p.getValue().toString().trim();
			if (!value.equals("JsonTemplateLayout"))
				return;
			
			String appenderId = key.substring(0, key.length() - LAYOUT_TYPE_SUFFIX.length());
			if (patchedAppenderIds.contains(appenderId))
				return;
				
			int counter = 0;
			final String TEMPLATE_ADDITIONAL_FIELD = "%s.layout.eventTemplateAdditionalField[%d]";
			while(counter < 1000 && loggerProperties.contains(String.format(TEMPLATE_ADDITIONAL_FIELD, appenderId, counter) + ".key")) {
				counter++;
			}
			for (Map.Entry<String, String> variable : vars.entrySet()) {
				loggerProperties.put(String.format(TEMPLATE_ADDITIONAL_FIELD, appenderId, counter) + ".type", "EventTemplateAdditionalField");
				loggerProperties.put(String.format(TEMPLATE_ADDITIONAL_FIELD, appenderId, counter) + ".key", variable.getKey());
				loggerProperties.put(String.format(TEMPLATE_ADDITIONAL_FIELD, appenderId, counter) + ".value", variable.getValue());
			}
				
			patchedAppenderIds.add(appenderId);
		}
	}
	
	
	private static String clusterIdEnv;
	private static String clusterIdStrategy;
	private static Set<String> clusterId;
	

	/**
	 * Modifica il percorso di un file aggiungendo un identificativo univoco del
	 * nodo
	 * @param filePath percorso originario del file
	 * @param id id della risorsa da modificare
	 * @return
	 */
	public static String applyClusterIdStrategy(String filePath, String id) {
		if (clusterId.contains(id) || clusterId.contains(Boolean.TRUE.toString()))
			return applyClusterIdStrategy(filePath);
		return filePath;
	}
	
	private static String applyClusterIdStrategy(String filePath) {
		Path oldPath = Path.of(filePath);
		String fileName = oldPath.getFileName().toString();
		Path dir = oldPath.getParent();
		
		if (clusterIdStrategy.equals(Costanti.LOG_CLUSTERID_STRATEGY_FILENAME)) {
			int index = fileName.lastIndexOf('.');
			index = index == -1 ? fileName.length() : index;
			String name = fileName.substring(0, index);
			String extension = fileName.substring(index);
			fileName = name + "." + clusterIdEnv + extension;
		} else if (clusterIdStrategy.equals(Costanti.LOG_CLUSTERID_STRATEGY_DIRECTORY)) {
			dir = dir.resolve(clusterIdEnv);
		}
		
		return (dir.resolve(fileName).toString());
	}
	
	/**
	 * Inserisce le informazioni relative al cluster Id nel path del file di log,
	 * usato per condividere lo stesso file system da nodi diversi mantenendo log separati
	 * @param loggerProperties proprieta dei logger
	 * @param applicationEnv mappa contenente le variabile d'ambiente delle applicazioni
	 * @return
	 */
	private static Properties patchClusterIdPath(Properties loggerProperties, Map<String, String> applicationEnv) {
		clusterIdEnv = readProperty(loggerProperties, 
				Costanti.PROP_ENABLE_LOG_CLUSTERID_ENV, 
				applicationEnv, null);
		if(clusterIdEnv!=null && StringUtils.isNotEmpty(clusterIdEnv)) {
			String id = clusterIdEnv;
			clusterIdEnv = System.getenv(id);
			if(clusterIdEnv==null || StringUtils.isEmpty(clusterIdEnv)) {
				clusterIdEnv = System.getProperty(id);
			}
		}
		if(clusterIdEnv==null || StringUtils.isEmpty(clusterIdEnv)) {
			clusterIdEnv = System.getenv("HOSTNAME");
		}
		if(clusterIdEnv==null || StringUtils.isEmpty(clusterIdEnv)) {
			clusterIdEnv = System.getProperty("HOSTNAME");
		}
		if (clusterIdEnv == null || StringUtils.isEmpty(clusterIdEnv)) {
			return loggerProperties;
		}
		clusterId = List.of(readProperty(loggerProperties, 
				Costanti.PROP_ENABLE_LOG_CLUSTERID, 
				applicationEnv,
				Boolean.FALSE.toString()).split(","))
				.stream()
				.map(String::trim)
				.collect(Collectors.toSet());

		Set<String> appenderIds = LoggerWrapperFactory.getAppenderIdByLoggerName(loggerProperties, 
				Costanti.PROP_ENABLE_LOG_CLUSTERID, 
				applicationEnv);
		
		if (appenderIds.isEmpty())
			return loggerProperties;
		
		return applyPatchClusterIdPath(loggerProperties, applicationEnv, appenderIds);
	}
	private static Properties applyPatchClusterIdPath(Properties loggerProperties, Map<String, String> applicationEnv, Set<String> appenderIds) {	
	
		clusterIdStrategy = readProperty(loggerProperties, 
				Costanti.PROP_ENABLE_LOG_CLUSTERID_STRATEGY, 
				applicationEnv,
				Costanti.LOG_CLUSTERID_STRATEGY_DIRECTORY);
		clusterIdStrategy = clusterIdStrategy.trim();
		
		for (Map.Entry<Object, Object> p : loggerProperties.entrySet()) {
			String key = p.getKey().toString().trim();
			String appenderId = null;
			
			if (key.startsWith(LOGGER_APPENDER_PREFIX)) {
				if (key.endsWith(".fileName"))
					appenderId = key.substring(0, key.length() - ".fileName".length());
				if (key.endsWith(".filePattern"))
					appenderId = key.substring(0, key.length() - ".filePattern".length());
			}
			
			if (appenderId != null && appenderIds.contains(appenderId)) {
					String value = p.getValue().toString().trim();
					p.setValue(applyClusterIdStrategy(value));
			}
			
		}
		
		
		return loggerProperties;
	}
	
	/**
	 * Aggiorna i logger per usufruire di tutti i servizi pilotati tramite variabili d'ambiente
	 * o proprieta nei vari file log4j2.properties, le varie proprieta sono presenti nella classe
	 * org.openspcoop2.utils.Costanti
	 * @param loggerProperties
	 * @param propertyToEnv
	 * @param vars
	 * @return
	 */
	public static Properties patchLoggers(Properties loggerProperties, Map<String, String> propertyToEnv, Map<String, String> vars) {
		patchClusterIdPath(loggerProperties, propertyToEnv);
		patchLoggersStdout(loggerProperties, propertyToEnv, vars);
		patchLoggersJSON(loggerProperties, propertyToEnv, vars);
		return loggerProperties;
	}


}
