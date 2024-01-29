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

package org.openspcoop2.pdd.logger.filetrace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**     
 * LogTraceConfig
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileTraceConfig {
	
	private static HashMap<String, FileTraceConfig> staticInstanceMap = new HashMap<>();
	private static final org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("FileTraceConfig");

	public static void init(InputStream is, String fileNamePath, boolean globale) throws CoreException {
		semaphore.acquireThrowRuntime("init_InputStream");
		try {
			if(!staticInstanceMap.containsKey(fileNamePath)){
				FileTraceConfig instance = new FileTraceConfig(is, globale);
				staticInstanceMap.put(fileNamePath, instance);
			}
		}finally {
			semaphore.release("init_InputStream");
		}
	}
	public static void init(File file, boolean globale) throws CoreException {
		semaphore.acquireThrowRuntime("init_File");
		try {
			if(!staticInstanceMap.containsKey(file.getAbsolutePath())){
				FileTraceConfig instance = new FileTraceConfig(file, globale);
				staticInstanceMap.put(file.getAbsolutePath(), instance);
			}
		}finally {
			semaphore.release("init_File");
		}
	}
	public static void update(File file, boolean globale) throws CoreException {
		semaphore.acquireThrowRuntime("update");
		try {
			updateWithoutSynchronizedEngine(file, globale);
		}finally {
			semaphore.release("update_File");
		}
	}
	public static void resetFileTraceAssociatePorte() throws CoreException {
		semaphore.acquireThrowRuntime("resetFileTraceAssociatePorte");
		try {
			if(!staticInstanceMap.isEmpty()) {
				List<String> removeEntries = new ArrayList<>();
				for (String path : staticInstanceMap.keySet()) {
					FileTraceConfig config = staticInstanceMap.get(path);
					if(config.isGlobale()) {
						continue;
					}
					/**updateWithoutSynchronizedEngine(new File(path), config.isGlobale());*/
					removeEntries.add(path); // verra poi ricreato
				}
				while(!removeEntries.isEmpty()) {
					String path = removeEntries.remove(0);
					staticInstanceMap.remove(path);
				}
			}
		}finally {
			semaphore.release("resetFileTraceAssociatePorte");
		}
	}
	private static void updateWithoutSynchronizedEngine(File file, boolean globale) throws CoreException {
		FileTraceConfig newConfig = new FileTraceConfig(file, globale);
		FileTraceConfig instance = newConfig;
		staticInstanceMap.remove(file.getAbsolutePath());
		staticInstanceMap.put(file.getAbsolutePath(), instance);
	}
	public static FileTraceConfig getConfig(File file, boolean globale) throws CoreException {
		if(!staticInstanceMap.containsKey(file.getAbsolutePath())){
			init(file, globale);
		}
		return staticInstanceMap.get(file.getAbsolutePath());
	}
	
	private boolean globale = true;
	
	private LogSeverity logSeverity = LogSeverity.info;
	
	private Map<String, String> escape = new HashMap<>();
		
	private String headersSeparator = ",";
	private String headerSeparator = "=";
	private String headerPrefix = "";
	private String headerSuffix = "";
	private List<String> headerBlackList;
	private List<String> headerWhiteList;
	
	private String headerMultiValueSeparator = ","; // per singolo header
	
	private List<String> propertiesSortKeys = new ArrayList<>();
	private Map<String, String> propertiesNames = new HashMap<>();
	private Map<String, String> propertiesValues = new HashMap<>();
	
	private List<String> topicErogazioni = new ArrayList<>();
	private Map<String, Topic> topicErogazioniMap = new HashMap<>();
	
	private List<String> topicFruizioni = new ArrayList<>();
	private Map<String, Topic> topicFruizioneMap = new HashMap<>();
	
	public FileTraceConfig(File file, boolean globale) throws CoreException {
		try(FileInputStream fin = new FileInputStream(file)){
			initEngine(fin, globale);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	public FileTraceConfig(InputStream is, boolean globale) throws CoreException {
		initEngine(is, globale);
	}
	
	private static boolean escapeInFile = true;
	public static boolean isEscapeInFile() {
		return escapeInFile;
	}
	public static void setEscapeInFile(boolean escapeInFile) {
		FileTraceConfig.escapeInFile = escapeInFile;
	}
	private void initEngine(InputStream is, boolean globale) throws CoreException {
		try {
			this.globale = globale;
			
			Properties p = new Properties();
			
			if(escapeInFile) {
				p.load(is); // non tratta bene i caratteri speciali
			}
			else {
				Scanner scanner = new Scanner(is);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if(line.startsWith("#")) {
						continue;
					}
					if(StringUtils.isEmpty(line)) {
						continue;
					}
					/**System.out.println("LINE ["+line+"]");*/
					String key = line;
					String value = "";
					if(line.endsWith("=")) {
						if(line.length()==1) {
							continue;
						}
						key = line.substring(0, line.length()-1);
					}
					else {
						int indexOf = line.indexOf("=");
						if(indexOf<=0) {
							continue;
						}
						key = line.substring(0, indexOf);
						value = line.substring(indexOf+1, line.length());
					}
					p.put(key, value);
				}
				scanner.close();
				
				File fTmp = FileSystemUtilities.createTempFile("test", ".properties");
				try {
					try(FileOutputStream fout = new FileOutputStream(fTmp)){
						p.store(fout, "test");
					}
					try(FileInputStream finNewP = new FileInputStream(fTmp)){
						 p = new Properties();
						 p.load(finNewP);
					}
				}finally {
					if(!fTmp.delete()) {
						// ignore
					}
				}
			}
			
			PropertiesReader reader = new PropertiesReader(p,true);
			
			// ** Log **
			
			String tmp = getProperty(reader, "log.severity", false);
			if(tmp!=null) {
				this.logSeverity = LogSeverity.valueOf(tmp);
			}
			
			InputStream isLog = getInputStreamLogFile(reader);
			Properties pLog = new Properties();
			pLog.load(isLog);
			LoggerWrapperFactory.setLogConfiguration(pLog, true);
				
			
			// ** Topics **
			
			boolean erogazioni = true;
			registerTopic(reader, erogazioni);
			registerTopic(reader, !erogazioni);
			
			// ** Format **
			
			readFormatProperties(reader);
				
		}catch(Exception t) {
			throw new CoreException(t.getMessage(),t);
		}
	}
	
	private InputStream getInputStreamLogFile(PropertiesReader reader) throws UtilsException, FileNotFoundException {
		String tmp = getProperty(reader, "log.config.file", true);
		
		File fTmp = new File(tmp);
		if(fTmp.exists()) {
			return new FileInputStream(fTmp);
		}
		
		InputStream isTmp = FileTraceConfig.class.getResourceAsStream(tmp);
		if(isTmp!=null) {
			return isTmp;
		}
		
		if(!tmp.startsWith("/")) {
			isTmp = FileTraceConfig.class.getResourceAsStream("/"+tmp);
			if(isTmp!=null) {
				return isTmp;
			}
		}
		
		fTmp = new File(OpenSPCoop2Properties.getInstance().getRootDirectory(), tmp);
		if(fTmp.exists()) {
			return new FileInputStream(fTmp);
		}
	
		throw new UtilsException("File '"+tmp+"' not found");
		
	}
	
	private void registerTopic(PropertiesReader reader, boolean erogazioni) throws UtilsException {
		
		String tipo = erogazioni ? "erogazioni" : "fruizioni";
		
		String propertyName = "topic."+tipo;
		String tmp = getProperty(reader, propertyName, false);

		if(tmp!=null && !StringUtils.isEmpty(tmp)) {
			
			List<String> list = erogazioni ? this.topicErogazioni : this.topicFruizioni;
			Map<String, Topic> map = erogazioni ? this.topicErogazioniMap : this.topicFruizioneMap;
		
			String [] split = tmp.split(",");
			if(split!=null && split.length>0) {
				for (int i = 0; i < split.length; i++) {
					String nome = split[i].trim();
					
					if(list.contains(nome)) {
						throw new UtilsException("Found duplicate topic '"+nome+"' ("+tipo+")");
					}
					list.add(nome);
					
					Topic topic = new Topic();
					topic.setErogazioni(erogazioni);
					topic.setNome(nome);
					
					String propertyNameOnlyRequestSent = propertyName+"."+nome+".requestSent";
					String tmpOnlyRequestSent = getProperty(reader, propertyNameOnlyRequestSent, false);
					if(tmpOnlyRequestSent!=null) {
						topic.setOnlyRequestSent(Boolean.valueOf(tmpOnlyRequestSent));
					}
					else {
						// backward
						propertyNameOnlyRequestSent = propertyName+"."+nome+".requestSended";
						tmpOnlyRequestSent = getProperty(reader, propertyNameOnlyRequestSent, false);
						if(tmpOnlyRequestSent!=null) {
							topic.setOnlyRequestSent(Boolean.valueOf(tmpOnlyRequestSent));
						}
					}
					
					String propertyNameInRequestContentDefined = propertyName+"."+nome+".inRequestContentDefined";
					String tmpInRequestContentDefined = getProperty(reader, propertyNameInRequestContentDefined, false);
					if(tmpInRequestContentDefined!=null)
						topic.setOnlyInRequestContentDefined(Boolean.valueOf(tmpInRequestContentDefined));
					
					String propertyNameOutRequestContentDefined = propertyName+"."+nome+".outRequestContentDefined";
					String tmpOutRequestContentDefined = getProperty(reader, propertyNameOutRequestContentDefined, false);
					if(tmpOutRequestContentDefined!=null)
						topic.setOnlyOutRequestContentDefined(Boolean.valueOf(tmpOutRequestContentDefined));
					
					String propertyNameInResponseContentDefined = propertyName+"."+nome+".inResponseContentDefined";
					String tmpInResponseContentDefined = getProperty(reader, propertyNameInResponseContentDefined, false);
					if(tmpInResponseContentDefined!=null)
						topic.setOnlyInResponseContentDefined(Boolean.valueOf(tmpInResponseContentDefined));
					
					String propertyNameOutResponseContentDefined = propertyName+"."+nome+".outResponseContentDefined";
					String tmpOutResponseContentDefined = getProperty(reader, propertyNameOutResponseContentDefined, false);
					if(tmpOutResponseContentDefined!=null)
						topic.setOnlyOutResponseContentDefined(Boolean.valueOf(tmpOutResponseContentDefined));
					
					String propertyNameCategory = "category.topic."+tipo+"."+nome;
					String category = getProperty(reader, propertyNameCategory, true);
					topic.setCategoryName(category);
					topic.setLog(LoggerWrapperFactory.getLogger(category));
					
					String propertyNameFormat = "format.topic."+tipo+"."+nome;
					String format = getProperty(reader, propertyNameFormat, true);
					topic.setFormat(format);
					
					map.put(nome, topic);
				}
			}
		}
	}
	@SuppressWarnings("unused")
	private List<String> getList(PropertiesReader reader, String propertyName) throws UtilsException {
		List<String> list = new ArrayList<>();		
		String tmp = getProperty(reader, propertyName, false);
		if(tmp!=null && !StringUtils.isEmpty(tmp)) {
			String [] split = tmp.split(",");
			if(split!=null && split.length>0) {
				for (int i = 0; i < split.length; i++) {
					String nome = split[i].trim();
					if(list.contains(nome)) {
						throw new UtilsException("Found duplicate topic '"+nome+"' in property '"+propertyName+"'");
					}
					list.add(nome);
				}
			}
		}
		return list;
	}
	
	private void readFormatProperties(PropertiesReader reader) throws UtilsException {
		Properties escapeMap = reader.readProperties("format.escape.");
		if(escapeMap!=null && !escapeMap.isEmpty()) {
			for (Object s : escapeMap.keySet()) {
				String key = (String) s;
				this.escape.put(key, escapeMap.getProperty(key));
			}
		}
		
		Properties propertiesMap = reader.readProperties("format.property.");
		List<String> listPosition = new ArrayList<>();
		List<String> listPnames = new ArrayList<>();
		if(propertiesMap!=null && !propertiesMap.isEmpty()) {
			for (Object s : propertiesMap.keySet()) {
				String key = (String) s;
				if(!key.contains(".") || key.length()<3 || key.startsWith(".") || key.endsWith(".")) {
					throw new UtilsException("Format property 'format.property."+key+"' wrong (expected: format.property.<intPosition>.<nomeProperty>)");
				}
				int indexOf = key.indexOf(".");
				String pos = key.substring(0, indexOf);
				String nomeP = key.substring(indexOf+1,key.length());
				String posPadded = StringUtils.leftPad(pos, 10, "0");
				if(listPosition.contains(posPadded)) {
					throw new UtilsException("Bad property 'format.property."+key+"': contains duplicate position '"+pos+"'");
				}
				if(listPnames.contains(nomeP)) {
					throw new UtilsException("Bad property 'format.property."+key+"'. contains duplicate name '"+nomeP+"'");
				}
				listPosition.add(posPadded);
				this.propertiesNames.put(posPadded, nomeP);
				this.propertiesValues.put(posPadded, propertiesMap.getProperty(key));
			}
			Collections.sort(listPosition);
			this.propertiesSortKeys = listPosition;
		}
		
		String tmp = getProperty(reader, "format.headers.separator", false);
		if(tmp!=null) {
			this.headersSeparator = tmp;
		}
		
		tmp = getProperty(reader, "format.headers.header.separator", false);
		if(tmp!=null) {
			this.headerSeparator = tmp;
		}
		
		tmp = getProperty(reader, "format.headers.header.prefix", false);
		if(tmp!=null) {
			this.headerPrefix = tmp;
		}
		
		tmp = getProperty(reader, "format.headers.header.suffix", false);
		if(tmp!=null) {
			this.headerSuffix = tmp;
		}
		
		tmp = getProperty(reader, "format.header.multiValueSeparator", false);
		if(tmp!=null) {
			this.headerMultiValueSeparator = tmp;
		}
		
		tmp = getProperty(reader, "format.header.whiteList", false);
		if(tmp!=null && !"".equals(tmp.trim())) {
			tmp = tmp.trim();
			this.headerWhiteList = new ArrayList<>();
			if(tmp.contains(",")) {
				String [] split = tmp.split(",");
				if(split!=null && split.length>0) {
					for (String s : split) {
						if(s!=null) {
							s = s.trim();
							if(!"".equals(s)) {
								this.headerWhiteList.add(s);
							}
						}
					}
				}
			}
			else {
				this.headerWhiteList.add(tmp);
			}
		}
		
		tmp = getProperty(reader, "format.header.blackList", false);
		if(tmp!=null && !"".equals(tmp.trim())) {
			tmp = tmp.trim();
			this.headerBlackList = new ArrayList<>();
			if(tmp.contains(",")) {
				String [] split = tmp.split(",");
				if(split!=null && split.length>0) {
					for (String s : split) {
						if(s!=null) {
							s = s.trim();
							if(!"".equals(s)) {
								this.headerBlackList.add(s);
							}
						}
					}
				}
			}
			else {
				this.headerBlackList.add(tmp);
			}
		}
	}
	
	private String getProperty(PropertiesReader reader, String key, boolean required) throws UtilsException {
		String tmp = reader.getValue(key);
		if(tmp==null) {
			if(required) {
				throw new UtilsException("Property '"+key+"' not found");
			}
			return null;
		}
		else {
			tmp = tmp.trim();
			return tmp;
		}
	}
	
	public LogSeverity getLogSeverity() {
		return this.logSeverity;
	}
	
	public Map<String, String> getEscape() {
		return this.escape;
	}
	
	public String getHeadersSeparator() {
		return this.headersSeparator;
	}
	public String getHeaderSeparator() {
		return this.headerSeparator;
	}
	public String getHeaderPrefix() {
		return this.headerPrefix;
	}
	public String getHeaderSuffix() {
		return this.headerSuffix;
	}
	public List<String> getHeaderBlackList() {
		return this.headerBlackList;
	}
	public List<String> getHeaderWhiteList() {
		return this.headerWhiteList;
	}
	
	public String getHeaderMultiValueSeparator() {
		return this.headerMultiValueSeparator;
	}
	
	public List<String> getPropertiesSortKeys() {
		return this.propertiesSortKeys;
	}
	public Map<String, String> getPropertiesNames() {
		return this.propertiesNames;
	}
	public Map<String, String> getPropertiesValues() {
		return this.propertiesValues;
	}
	
	public List<String> getTopicErogazioni() {
		return this.topicErogazioni;
	}
	public Map<String, Topic> getTopicErogazioniMap() {
		return this.topicErogazioniMap;
	}
	public List<String> getTopicFruizioni() {
		return this.topicFruizioni;
	}
	public Map<String, Topic> getTopicFruizioneMap() {
		return this.topicFruizioneMap;
	}
	
	public boolean isGlobale() {
		return this.globale;
	}
}
