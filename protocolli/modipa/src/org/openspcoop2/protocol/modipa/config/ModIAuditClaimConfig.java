/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
 * ModIAuditClaimConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIAuditClaimConfig {

	public static final String PROPERTY_NOME = "nome";
	public static final String PROPERTY_LABEL = "label";
	public static final String PROPERTY_REQUIRED = "required";
	public static final String PROPERTY_CACHEABLE = "cacheable";
	public static final String PROPERTY_STRING_TYPE = "stringType";
	public static final String PROPERTY_REGEXP = "regexp";
	public static final String PROPERTY_ENUM = "enum";
	public static final String PROPERTY_MIN_LENGTH = "minLength";
	public static final String PROPERTY_MAX_LENGTH = "maxLength";
	public static final String PROPERTY_INFO = "info";
	public static final String PROPERTY_DEFAULT_RULE = "rule";
	public static final String PROPERTY_DEFAULT_RULE_INFO = "rule.info";
	public static final String PROPERTY_FORWARD_BACKEND = "forwardBackend";
	public static final String PROPERTY_TRACE = "trace";
	
	private ModIAuditClaimConfig() {}
	ModIAuditClaimConfig(String prefix, String propertyId, Properties p) throws ProtocolException {
		
		this.propertyId = propertyId;
		
		this.nome = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_NOME, true);
		this.label = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_LABEL, true);
		this.required = ModIAuditConfig.getBooleanProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_REQUIRED, true, true);
		this.cacheable = ModIAuditConfig.getBooleanProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_CACHEABLE, false, true);
		this.stringType = ModIAuditConfig.getBooleanProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_STRING_TYPE, true, true);
		
		this.regexp = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_REGEXP, false);
		
		String tmp = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_ENUM, false);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			this.values = new ArrayList<>();
			setList(tmp, this.values);
		}
		
		this.minLength = ModIAuditConfig.getIntProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_MIN_LENGTH, false, -1);
		this.maxLength = ModIAuditConfig.getIntProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_MAX_LENGTH, false, -1);
		
		this.info = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_INFO, true);
		
		tmp = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_DEFAULT_RULE, true);
		this.rules = new ArrayList<>();
		setList(tmp, this.rules);
			
		tmp = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_DEFAULT_RULE_INFO, true);
		this.rulesInfo = new ArrayList<>();
		setList(tmp, this.rulesInfo);
		
		this.forwardBackend = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_FORWARD_BACKEND, false);
		
		this.trace = ModIAuditConfig.getBooleanProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_TRACE, true, true);
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
	private boolean cacheable;
	private boolean stringType;
	private String regexp;
	private List<String> values;
	private int minLength;
	private int maxLength;
	private String info;
	private List<String> rules;
	private List<String> rulesInfo;
	private String forwardBackend;
	private boolean trace;
	
	public ModIAuditClaimConfig copyNewInstance() {
		ModIAuditClaimConfig newInstance = new ModIAuditClaimConfig();
		newInstance.propertyId = this.propertyId;
		newInstance.nome = this.nome;
		newInstance.label = this.label;
		newInstance.required = this.required;
		newInstance.cacheable = this.cacheable;
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
		
		newInstance.forwardBackend = this.forwardBackend;
		newInstance.trace = this.trace;
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
	
	public boolean isCacheable() {
		return this.cacheable;
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

	public String getForwardBackend() {
		return this.forwardBackend;
	}
	public void setForwardBackend(String forwardBackend) {
		this.forwardBackend = forwardBackend;
	}
	
	public boolean isTrace() {
		return this.trace;
	}
	public void setTrace(boolean trace) {
		this.trace = trace;
	}
}
