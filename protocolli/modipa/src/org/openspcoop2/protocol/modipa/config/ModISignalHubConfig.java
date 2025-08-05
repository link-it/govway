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
package org.openspcoop2.protocol.modipa.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * ModISignalHubConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModISignalHubConfig {

	public static final String PROPERTY_PARAMS = "params";
	
	private List<ModISignalHubParamConfig> params;
		
	public ModISignalHubConfig copyNewInstance() {
		ModISignalHubConfig config = new ModISignalHubConfig();
		
		config.params = new ArrayList<>();
		if(this.params!=null && !this.params.isEmpty()) {			
			for (ModISignalHubParamConfig modISignalHubParamConfig : this.params) {
				config.params.add(modISignalHubParamConfig.copyNewInstance());
			}
		}
		
		return config;
	}
	
	private ModISignalHubConfig() {}
	ModISignalHubConfig(String prefix, Properties p) throws ProtocolException {
		
		String claimsP = getProperty(prefix, p, PROPERTY_PARAMS, true);
		String [] tmp = claimsP.split(",");
		if(tmp==null || tmp.length<=0) {
			throw new ProtocolException("Property '"+PROPERTY_PARAMS+"' empty");
		}
		this.params = new ArrayList<>();
		for (String c : tmp) {
			c = c.trim();
			this.params.add(new ModISignalHubParamConfig(prefix, c, p));
		}
		
		// Verifico univocita
		if(!this.params.isEmpty()) {
			verify();
		}
		
	}
	private void verify() throws ProtocolException {
		for (ModISignalHubParamConfig modISignalHubParamConfig : this.params) {
			String name = modISignalHubParamConfig.getNome();
			int count = 0;
			for (ModISignalHubParamConfig modISignalHubParamConfigCheck : this.params) {
				if(name.equals(modISignalHubParamConfigCheck.getNome())) {
					count++;
				}
			}
			if(count>1) {
				throw new ProtocolException("Property "+PROPERTY_PARAMS+".xx."+ModISignalHubParamConfig.PROPERTY_NOME+"="+name+" defined more then one time ("+count+")");
			}
		}
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
	

	public List<ModISignalHubParamConfig> getClaims() {
		return this.params;
	}
	public void setClaims(List<ModISignalHubParamConfig> claims) {
		this.params = claims;
	}

}
