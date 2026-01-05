/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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
package org.openspcoop2.protocol.modipa.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ModISignalHubClaimConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModISignalHubParamConfig {

	public static final String PROPERTY_NOME = "nome";
	public static final String PROPERTY_LABEL = "label";
	public static final String PROPERTY_REQUIRED = "required";
	public static final String PROPERTY_STRING_TYPE = "stringType";
	public static final String PROPERTY_REGEXP = "regexp";
	public static final String PROPERTY_ENUM = "enum";
	public static final String PROPERTY_MIN_LENGTH = "minLength";
	public static final String PROPERTY_MAX_LENGTH = "maxLength";
	public static final String PROPERTY_INFO = "info";
	public static final String PROPERTY_DEFAULT_RULE = "rule";
	public static final String PROPERTY_DEFAULT_RULE_INFO = "rule.info";
	
	private ModISignalHubParamConfig() {}
	ModISignalHubParamConfig(String prefix, String propertyId, Properties p) throws ProtocolException {
		
		this.propertyId = propertyId;
		
		this.nome = ModISignalHubConfig.getProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_NOME, true);
		this.label = ModISignalHubConfig.getProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_LABEL, true);
		this.required = ModISignalHubConfig.getBooleanProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_REQUIRED, true, true);
		this.stringType = ModISignalHubConfig.getBooleanProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_STRING_TYPE, true, true);
		
		this.regexp = ModISignalHubConfig.getProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_REGEXP, false);
		
		String tmp = ModISignalHubConfig.getProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_ENUM, false);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			this.values = new ArrayList<>();
			setList(tmp, this.values);
		}
		
		this.minLength = ModISignalHubConfig.getIntProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_MIN_LENGTH, false, -1);
		this.maxLength = ModISignalHubConfig.getIntProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_MAX_LENGTH, false, -1);
		
		this.info = ModISignalHubConfig.getProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_INFO, true);
		
		tmp = ModISignalHubConfig.getProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_DEFAULT_RULE, true);
		this.rules = new ArrayList<>();
		setList(tmp, this.rules);
			
		tmp = ModISignalHubConfig.getProperty(prefix, p, ModISignalHubConfig.PROPERTY_PARAMS+"."+propertyId+"."+PROPERTY_DEFAULT_RULE_INFO, true);
		this.rulesInfo = new ArrayList<>();
		setList(tmp, this.rulesInfo);

	}
	
	private static void setList(String v, List<String> list) {
		String [] split = v.split(",");
		if(split!=null && split.length>0) {
			for (String s : split) {
				if(s!=null) {
					s = s.trim();
					if(StringUtils.isNotEmpty(s)) {
						list.add(s);
					}
				}
			}
		}
	}
		
	private String propertyId;

	private String nome;
	private String label;
	private boolean required;
	private boolean stringType;
	private String regexp;
	private List<String> values;
	private int minLength;
	private int maxLength;
	private String info;
	private List<String> rules;
	private List<String> rulesInfo;
	
	public ModISignalHubParamConfig copyNewInstance() {
		ModISignalHubParamConfig newInstance = new ModISignalHubParamConfig();
		newInstance.propertyId = this.propertyId;
		newInstance.nome = this.nome;
		newInstance.label = this.label;
		newInstance.required = this.required;
		newInstance.stringType = this.stringType;
		newInstance.regexp = this.regexp;
		
		newInstance.values = new ArrayList<>();
		if(this.values!=null && !this.values.isEmpty()) {
			newInstance.values.addAll(this.values);
		}
		
		newInstance.minLength = this.minLength;
		newInstance.maxLength = this.maxLength;
		
		newInstance.info = this.info;
		
		newInstance.rules = new ArrayList<>();
		if(this.rules!=null && !this.rules.isEmpty()) {
			newInstance.rules.addAll(this.rules);
		}
		
		newInstance.rulesInfo = new ArrayList<>();
		if(this.rulesInfo!=null && !this.rulesInfo.isEmpty()) {
			newInstance.rulesInfo.addAll(this.rulesInfo);
		}
		
		return newInstance;
	}
	
	public String getPropertyId() {
		return this.propertyId;
	}

	public String getNome() {
		return this.nome;
	}
	
	public String getLabel() {
		return this.label;
	}

	public boolean isRequired() {
		return this.required;
	}

	public boolean isStringType() {
		return this.stringType;
	}

	public String getRegexp() {
		return this.regexp;
	}
	
	public List<String> getValues() {
		return this.values;
	}
	
	public int getMinLength() {
		return this.minLength;
	}
	public int getMaxLength() {
		return this.maxLength;
	}
	
	public String getInfo() {
		return this.info;
	}

	public List<String> getRules() {
		return this.rules;
	}
	public void setRules(List<String> rules) {
		this.rules = rules;
	}
	
	public List<String> getRulesInfo() {
		return this.rulesInfo;
	}
	public void setRulesInfo(List<String> rulesInfo) {
		this.rulesInfo = rulesInfo;
	}
}
