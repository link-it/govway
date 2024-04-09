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

package org.openspcoop2.utils.certificate.byok;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * BYOKConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3572589461109860459L;
		
	private String id;
	
	private String type;
	private String label;
	private BYOKMode mode;
	
	private BYOKEncryptionMode encryptionMode;
	
	private List<String>inputParametersIds;
	private List<BYOKConfigParameter> inputParameters = new ArrayList<>();
	
	private BYOKRemoteConfig remoteConfig;
	
	private BYOKLocalConfig localConfig;
	
	
	protected BYOKConfig(String id, Properties p, Logger log) throws UtilsException {
		this.id = id;
		
		if(p==null || p.isEmpty()) {
			log.error("Properties is null");
			throw new UtilsException("Properties '"+BYOKCostanti.PROPERTY_PREFIX+id+".*' undefined");
		}
		
		this.type = getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_TYPE, true);	
		this.label = getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_LABEL, true);	
		
		String tmpMode = getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_MODE, true);
		try {
			this.mode = BYOKMode.valueOf(tmpMode.toUpperCase());
		}catch(Exception e) {
			throw new UtilsException("Invalid property '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+BYOKCostanti.PROPERTY_SUFFIX_MODE+"' enum value '"+tmpMode+"': "+e.getMessage());
		}
		
		tmpMode = getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_ENCRYPTION_MODE, false);
		if(tmpMode!=null && StringUtils.isNotEmpty(tmpMode)) {
			try {
				this.encryptionMode = BYOKEncryptionMode.valueOf(tmpMode.toUpperCase());
			}catch(Exception e) {
				throw new UtilsException("Invalid property '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+BYOKCostanti.PROPERTY_SUFFIX_ENCRYPTION_MODE+"' enum value '"+tmpMode+"': "+e.getMessage());
			}
		}
		else {
			this.encryptionMode = BYOKEncryptionMode.REMOTE;
		}
		
		this.inputParametersIds = new ArrayList<>();
		initInput(p, this.inputParametersIds);
		if(this.inputParametersIds!=null && !this.inputParametersIds.isEmpty()) {
			for (String inputId : this.inputParametersIds) {
				String nameP = getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_INPUT+inputId+BYOKCostanti.PROPERTY_SUFFIX_INPUT_NAME, true);	
				String labelP = getProperty(id, p, BYOKCostanti.PROPERTY_SUFFIX_INPUT+inputId+BYOKCostanti.PROPERTY_SUFFIX_INPUT_LABEL, true);	
				this.inputParameters.add(new BYOKConfigParameter(inputId, nameP, labelP));
			}
		}
		
		if(BYOKEncryptionMode.REMOTE.equals(this.encryptionMode)) {
			this.remoteConfig = new BYOKRemoteConfig(id, p, log);
		}
		else {
			this.localConfig = new BYOKLocalConfig(id, p, log, this);
		}
		
	}

	void initInput(Properties p, List<String> idKeystore) {
		Enumeration<?> enKeys = p.keys();
		while (enKeys.hasMoreElements()) {
			Object object = enKeys.nextElement();
			if(object instanceof String) {
				String key = (String) object;
				initInput(key, BYOKCostanti.PROPERTY_SUFFIX_INPUT, idKeystore);	
			}
		}
	}
	void initInput(String key, String prefix, List<String> idKeystore) {
		if(key.startsWith(prefix) && key.length()>(prefix.length())) {
			String tmp = key.substring(prefix.length());
			if(tmp!=null && tmp.contains(".")) {
				int indeoOf = tmp.indexOf(".");
				if(indeoOf>0) {
					String idK = tmp.substring(0,indeoOf);
					if(!idKeystore.contains(idK)) {
						idKeystore.add(idK);
					}
				}
			}
		}
	}
	
	static String getProperty(String id, Properties p, String name, boolean required) throws UtilsException {
		String tmp = p.getProperty(name);
		if(tmp!=null) {
			return tmp.trim();
		}
		else {
			if(required) {
				throw new UtilsException("Property '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+name+"' notFound");
			}
			return null;
		}
	}
	static Integer getIntegerProperty(String id, Properties p, String name, boolean required) throws UtilsException {
		String v = getProperty(id, p, name, required);
		if(v!=null && StringUtils.isNotEmpty(v)) {
			try {
				return Integer.valueOf(v);
			}catch(Exception e) {
				throw new UtilsException("Invalid integer property '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+name+"' value '"+e.getMessage()+"': "+e.getMessage());
			}
		}
		return null;
	}
	static Boolean getBooleanProperty(String id, Properties p, String name, boolean required, Boolean defaultValue) throws UtilsException {
		String v = getProperty(id, p, name, required);
		if(v!=null && StringUtils.isNotEmpty(v)) {
			try {
				return Boolean.parseBoolean(v);
			}catch(Exception e) {
				throw new UtilsException("Invalid boolean property '"+BYOKCostanti.PROPERTY_PREFIX+id+"."+name+"' value '"+e.getMessage()+"': "+e.getMessage());
			}
		}
		return defaultValue;
	}
	
	public String getId() {
		return this.id;
	}
	
	
	
	public String getPrefixForLog() {
		return "[KSM '"+this.getId()+"' type:"+this.type+" label:"+this.label+"] ";
	}

	
	public String getType() {
		return this.type;
	}
	public String getLabel() {
		return this.label;
	}
	public BYOKMode getMode() {
		return this.mode;
	}

	public BYOKEncryptionMode getEncryptionMode() {
		return this.encryptionMode;
	}
	
	public List<String> getInputParametersIds() {
		return this.inputParametersIds;
	}
	public List<BYOKConfigParameter> getInputParameters() {
		return this.inputParameters;
	}
	
	public BYOKRemoteConfig getRemoteConfig() {
		return this.remoteConfig;
	}
	
	public BYOKLocalConfig getLocalConfig() {
		return this.localConfig;
	}
}
