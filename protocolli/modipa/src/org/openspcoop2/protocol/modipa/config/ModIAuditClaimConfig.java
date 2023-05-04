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
	public static final String PROPERTY_STRING_TYPE = "stringType";
	public static final String PROPERTY_INFO = "info";
	public static final String PROPERTY_DEFAULT_RULE = "default.rule";
	public static final String PROPERTY_DEFAULT_INFO = "default.info";
	public static final String PROPERTY_FORWARD_BACKEND = "forwardBackend";
	
	ModIAuditClaimConfig(String prefix, String propertyId, Properties p) throws ProtocolException {
		
		this.propertyId = propertyId;
		
		this.nome = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_NOME, true);
		this.label = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_LABEL, true);
		this.required = ModIAuditConfig.getBooleanProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_REQUIRED, true, true);
		this.stringType = ModIAuditConfig.getBooleanProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_STRING_TYPE, true, true);
		this.info = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_INFO, true);
		
		String tmp = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_DEFAULT_RULE, true);
		this.defaultRule = new ArrayList<>();
		setList(tmp, this.defaultRule);
			
		tmp = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_DEFAULT_INFO, true);
		this.defaultInfo = new ArrayList<>();
		setList(tmp, this.defaultInfo);
		
		this.forwardBackend = ModIAuditConfig.getProperty(prefix, p, ModIAuditConfig.PROPERTY_CLAIMS+"."+propertyId+"."+PROPERTY_FORWARD_BACKEND, false);
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
	private String info;
	private List<String> defaultRule;
	private List<String> defaultInfo;
	private String forwardBackend;
	
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

	public String getInfo() {
		return this.info;
	}

	public List<String> getDefaultRule() {
		return this.defaultRule;
	}

	public List<String> getDefaultInfo() {
		return this.defaultInfo;
	}

	public String getForwardBackend() {
		return this.forwardBackend;
	}
}
