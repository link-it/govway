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

import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * BYOKSecurityConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BYOKSecurityConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3572589461109860459L;
		
	private String id;
	
	private String wrapId;
	private String unwrapId;
	
	private List<String>inputParametersIds;
	private List<BYOKSecurityConfigParameter> inputParameters = new ArrayList<>();
		
	protected BYOKSecurityConfig(String id, Properties p, Logger log) throws UtilsException {
		this.id = id;
		
		if(p==null || p.isEmpty()) {
			log.error("Properties is null");
			throw new UtilsException("Properties '"+BYOKCostanti.SECURITY_PROPERTY_PREFIX+id+".*' undefined");
		}
		
		this.wrapId = getProperty(id, p, BYOKCostanti.SECURITY_PROPERTY_SUFFIX_WRAP, true);	
		this.unwrapId = getProperty(id, p, BYOKCostanti.SECURITY_PROPERTY_SUFFIX_UNWRAP, true);	
				
		this.inputParametersIds = new ArrayList<>();
		initInput(p, this.inputParametersIds);
		if(this.inputParametersIds!=null && !this.inputParametersIds.isEmpty()) {
			for (String inputId : this.inputParametersIds) {
				String value = getProperty(id, p, BYOKCostanti.SECURITY_PROPERTY_SUFFIX_INPUT+inputId, true);	
				this.inputParameters.add(new BYOKSecurityConfigParameter(inputId, value));
			}
		}
		
	}

	void initInput(Properties p, List<String> idKeystore) {
		Enumeration<?> enKeys = p.keys();
		while (enKeys.hasMoreElements()) {
			Object object = enKeys.nextElement();
			if(object instanceof String) {
				String key = (String) object;
				initInput(key, BYOKCostanti.SECURITY_PROPERTY_SUFFIX_INPUT, idKeystore);	
			}
		}
	}
	void initInput(String key, String prefix, List<String> idKeystore) {
		if(key.startsWith(prefix) && key.length()>(prefix.length())) {
			String tmp = key.substring(prefix.length());
			if(!idKeystore.contains(tmp)) {
				idKeystore.add(tmp);
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
				throw new UtilsException("Property '"+BYOKCostanti.SECURITY_PROPERTY_PREFIX+id+"."+name+"' notFound");
			}
			return null;
		}
	}

	
	public String getId() {
		return this.id;
	}
		
	public String getPrefixForLog() {
		return "[Security BYOK '"+this.getId()+"'] ";
	}

	public String getWrapId() {
		return this.wrapId;
	}

	public String getUnwrapId() {
		return this.unwrapId;
	}
	
	public List<String> getInputParametersIds() {
		return this.inputParametersIds;
	}
	public List<BYOKSecurityConfigParameter> getInputParameters() {
		return this.inputParameters;
	}
	

}
