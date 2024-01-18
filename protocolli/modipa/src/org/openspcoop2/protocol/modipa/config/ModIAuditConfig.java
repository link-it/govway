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
package org.openspcoop2.protocol.modipa.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ModIAuditConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIAuditConfig {

	public static final String PROPERTY_NOME = "nome";
	public static final String PROPERTY_LABEL = "label";
	public static final String PROPERTY_CLAIMS = "claims";
	
	public static final String PROPERTY_ISS_LOCALE = "iss.locale";
	public static final String PROPERTY_ISS_OAUTH = "iss.oauth";
	
	public static final String PROPERTY_SUB_LOCALE = "sub.locale";
	public static final String PROPERTY_SUB_OAUTH = "sub.oauth";
	
	public static final String PROPERTY_CLIENT_ID_LOCALE = "client_id.locale";
	public static final String PROPERTY_CLIENT_ID_OAUTH = "client_id.oauth";
	
	private String propertyId;
	
	private String nome;
	private String label;
	private List<ModIAuditClaimConfig> claims;
	
	private boolean issLocale = false;
	private boolean issOAuth = true;
	
	private boolean subLocale = false;
	private boolean subOAuth = false;
	
	private boolean clientIdLocale = false;
	private boolean clientIdOAuth = false;
	
	public ModIAuditConfig copyNewInstance() {
		ModIAuditConfig config = new ModIAuditConfig();
		
		config.propertyId = this.propertyId;
		
		config.nome = this.nome;
		config.label = this.label;
		
		config.claims = new ArrayList<>();
		if(this.claims!=null && !this.claims.isEmpty()) {			
			for (ModIAuditClaimConfig modIAuditClaimConfig : this.claims) {
				config.claims.add(modIAuditClaimConfig.copyNewInstance());
			}
		}
		
		config.issLocale = this.issLocale;
		config.issOAuth = this.issOAuth;
		
		config.subLocale = this.subLocale;
		config.subOAuth = this.subOAuth;
		
		config.clientIdLocale = this.clientIdLocale;
		config.clientIdOAuth = this.clientIdOAuth;
		
		return config;
	}
	
	private ModIAuditConfig() {}
	ModIAuditConfig(String prefix, String propertyId, Properties p) throws ProtocolException {
		
		this.propertyId = propertyId;
		
		this.nome = getProperty(prefix, p, PROPERTY_NOME, true);
		this.label = getProperty(prefix, p, PROPERTY_LABEL, true);
		
		String claimsP = getProperty(prefix, p, PROPERTY_CLAIMS, true);
		String [] tmp = claimsP.split(",");
		if(tmp==null || tmp.length<=0) {
			throw new ProtocolException("Property '"+PROPERTY_CLAIMS+"' empty");
		}
		this.claims = new ArrayList<>();
		for (String c : tmp) {
			c = c.trim();
			this.claims.add(new ModIAuditClaimConfig(prefix, c, p));
		}
		
		// Verifico univocita
		if(this.claims!=null && !this.claims.isEmpty()) {
			for (ModIAuditClaimConfig modIAuditClaimConfig : this.claims) {
				String name = modIAuditClaimConfig.getNome();
				int count = 0;
				for (ModIAuditClaimConfig modIAuditClaimConfigCheck : this.claims) {
					if(name.equals(modIAuditClaimConfigCheck.getNome())) {
						count++;
					}
				}
				if(count>1) {
					throw new ProtocolException("Property "+PROPERTY_CLAIMS+".xx."+ModIAuditClaimConfig.PROPERTY_NOME+"="+name+" defined more then one time ("+count+")");
				}
			}
		}
		
		this.issLocale = getBooleanProperty(prefix, p, PROPERTY_ISS_LOCALE, false, false);
		this.issOAuth = getBooleanProperty(prefix, p, PROPERTY_ISS_OAUTH, false, true);
		
		this.subLocale = getBooleanProperty(prefix, p, PROPERTY_SUB_LOCALE, false, false);
		this.subOAuth = getBooleanProperty(prefix, p, PROPERTY_SUB_OAUTH, false, false);
		
		this.clientIdLocale = getBooleanProperty(prefix, p, PROPERTY_CLIENT_ID_LOCALE, false, false);
		this.clientIdOAuth = getBooleanProperty(prefix, p, PROPERTY_CLIENT_ID_OAUTH, false, false);
	}
	
	
	
	static String getProperty(String prefixProperty, Properties p, String name, boolean required) throws ProtocolException {
		String tmp = p.getProperty(name);
		if(tmp!=null) {
			return tmp.trim();
		}
		else {
			if(required) {
				throw new ProtocolException("Property '"+prefixProperty+"."+name+"' notFound");
			}
			return null;
		}
	}
	static boolean getBooleanProperty(String prefixProperty, Properties p, String name, boolean required, boolean defaultValue) throws ProtocolException {
		String tmp = getProperty(prefixProperty, p, name, required);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			try {
				return Boolean.valueOf(tmp);
			}catch(Exception t) {
				throw new ProtocolException("Boolean property '"+prefixProperty+"."+name+"' invalid (found value:["+tmp+"]): "+t.getMessage(),t);
			}
		}
		return defaultValue;
	}
	static int getIntProperty(String prefixProperty, Properties p, String name, boolean required, int defaultValue) throws ProtocolException {
		String tmp = getProperty(prefixProperty, p, name, required);
		if(tmp!=null && StringUtils.isNotEmpty(tmp)) {
			try {
				return Integer.valueOf(tmp);
			}catch(Exception t) {
				throw new ProtocolException("Int property '"+prefixProperty+"."+name+"' invalid (found value:["+tmp+"]): "+t.getMessage(),t);
			}
		}
		return defaultValue;
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

	public List<ModIAuditClaimConfig> getClaims() {
		return this.claims;
	}
	public void setClaims(List<ModIAuditClaimConfig> claims) {
		this.claims = claims;
	}

	public boolean isIssLocale() {
		return this.issLocale;
	}

	public boolean isIssOAuth() {
		return this.issOAuth;
	}

	public boolean isSubLocale() {
		return this.subLocale;
	}

	public boolean isSubOAuth() {
		return this.subOAuth;
	}

	public boolean isClientIdLocale() {
		return this.clientIdLocale;
	}

	public boolean isClientIdOAuth() {
		return this.clientIdOAuth;
	}
}
