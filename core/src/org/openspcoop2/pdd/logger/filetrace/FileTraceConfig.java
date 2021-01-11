/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

/**     
 * LogTraceConfig
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileTraceConfig {
	
	private static FileTraceConfig staticInstance = null;
	protected static synchronized void init(InputStream is) throws CoreException {
		if(staticInstance==null) {
			staticInstance = new FileTraceConfig(is);
		}
	}
	public static synchronized void init(File file) throws CoreException {
		if(staticInstance==null) {
			staticInstance = new FileTraceConfig(file);
		}
	}
	public static synchronized void update(File file) throws CoreException {
		FileTraceConfig newConfig = new FileTraceConfig(file);
		staticInstance = newConfig;
	}
	public static FileTraceConfig getConfig(File file) throws CoreException {
		if(staticInstance==null) {
			init(file);
		}
		return staticInstance;
	}
	
	
	private LogSeverity logSeverity = LogSeverity.info;
	
	private Map<String, String> escape = new HashMap<String, String>();
		
	private String headersSeparator = ",";
	private String headerSeparator = "=";
	private String headerPrefix = "";
	private String headerSuffix = "";
	
	private List<String> propertiesSortKeys = new ArrayList<String>();
	private Map<String, String> propertiesNames = new HashMap<String, String>();
	private Map<String, String> propertiesValues = new HashMap<String, String>();
	
	private List<String> topicErogazioni = new ArrayList<String>();
	private Map<String, Topic> topicErogazioniMap = new HashMap<String, Topic>();
	
	private List<String> topicFruizioni = new ArrayList<String>();
	private Map<String, Topic> topicFruizioneMap = new HashMap<String, Topic>();
	
	public FileTraceConfig(File file) throws CoreException {
		try(FileInputStream fin = new FileInputStream(file)){
			_init(fin);
		}catch(Exception e) {
			throw new CoreException(e.getMessage(),e);
		}
	}
	public FileTraceConfig(InputStream is) throws CoreException {
		_init(is);
	}
	private void _init(InputStream is) throws CoreException {
		try {
			Properties p = new Properties();
			
			boolean escapeInFile = true;
			
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
					//System.out.println("LINE ["+line+"]");
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
				
				File fTmp = File.createTempFile("test", ".properties");
				try {
					try(FileOutputStream fout = new FileOutputStream(fTmp)){
						p.store(fout, "test");
					}
					try(FileInputStream finNewP = new FileInputStream(fTmp)){
						 p = new Properties();
						 p.load(finNewP);
					}
				}finally {
					fTmp.delete();
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
				
		}catch(Throwable t) {
			throw new CoreException(t.getMessage(),t);
		}
	}
	
	private InputStream getInputStreamLogFile(PropertiesReader reader) throws Exception {
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
	
		throw new Exception("File '"+tmp+"' not found");
		
	}
	
	private void registerTopic(PropertiesReader reader, boolean erogazioni) throws Exception {
		
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
					
					String propertyNameOnlyRequestSended = propertyName+"."+nome+".requestSended";
					String tmpOnlyRequestSended = getProperty(reader, propertyNameOnlyRequestSended, false);
					if(tmpOnlyRequestSended!=null)
						topic.setOnlyRequestSended(Boolean.valueOf(tmpOnlyRequestSended));
					
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
	private List<String> getList(PropertiesReader reader, String propertyName) throws Exception{
		List<String> list = new ArrayList<String>();		
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
		List<String> listPosition = new ArrayList<String>();
		List<String> listPnames = new ArrayList<String>();
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
}
