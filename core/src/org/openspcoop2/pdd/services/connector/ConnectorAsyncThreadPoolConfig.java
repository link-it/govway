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

package org.openspcoop2.pdd.services.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.properties.PropertiesReader;

/**
 * ConnectorAsyncThreadPoolConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnectorAsyncThreadPoolConfig {
	
	public static final String PROPERTY_PREFIX = "org.openspcoop2.pdd.connettori.asyncThreadPool.";
	
	// erogazioni
	private static final String PROPERTY_IN_REQUEST = "inRequest";
	private static final String PROPERTY_OUT_RESPONSE = "outResponse";
	
	// fruizioni
	private static final String PROPERTY_OUT_REQUEST = "outRequest";
	private static final String PROPERTY_IN_RESPONSE = "inResponse";
	
	// pool
	private static final String PROPERTY_EXECUTOR_PREFIX = "executor.";
	private static final String PROPERTY_EXECUTOR_TYPE_SUFFIX = ".type";
	private static final String PROPERTY_EXECUTOR_TYPE_VIRTUAL = "virtual";
	private static final String PROPERTY_EXECUTOR_TYPE_FIXED = "fixed";
	private static final String PROPERTY_EXECUTOR_SIZE_SUFFIX = ".size";
	
	// erogazioni
	private String inRequestThreadPoolId = null;
	public String getInRequestThreadPoolId() {
		return this.inRequestThreadPoolId;
	}
	private String outResponseThreadPoolId = null;
	public String getOutResponseThreadPoolId() {
		return this.outResponseThreadPoolId;
	}

	// fruizioni
	private String outRequestThreadPoolId = null;
	public String getOutRequestThreadPoolId() {
		return this.outRequestThreadPoolId;
	}
	private String inResponseThreadPoolId = null;
	public String getInResponseThreadPoolId() {
		return this.inResponseThreadPoolId;
	}
	
	// pool
	private Map<String, Boolean> poolVirtualThreadType = new HashMap<>();
	public Map<String, Boolean> getPoolVirtualThreadType() {
		return this.poolVirtualThreadType;
	}
	private Map<String, Integer> poolSize = new HashMap<>();
	public Map<String, Integer> getPoolSize() {
		return this.poolSize;
	}
	
	public ConnectorAsyncThreadPoolConfig(Properties properties, boolean requestStreamEnabled, boolean responseStreamEnabled) throws UtilsException {
		
		PropertiesReader pr = new PropertiesReader(properties, false);
		
		this.inRequestThreadPoolId = getProperty(pr, PROPERTY_IN_REQUEST, requestStreamEnabled);
		this.outResponseThreadPoolId = getProperty(pr, PROPERTY_OUT_RESPONSE, responseStreamEnabled);
		
		this.outRequestThreadPoolId = getProperty(pr, PROPERTY_OUT_REQUEST, requestStreamEnabled);
		this.inResponseThreadPoolId = getProperty(pr, PROPERTY_IN_RESPONSE, responseStreamEnabled);
		
		List<String> poolNames = new ArrayList<>();
		fillPoolNames(pr, poolNames);
		if(poolNames.isEmpty()) {
			throw new UtilsException("Property "+PROPERTY_PREFIX+"."+PROPERTY_EXECUTOR_PREFIX+"<id>"+PROPERTY_EXECUTOR_TYPE_SUFFIX+" not found");
		}
		else {
			for (String pName : poolNames) {
				
				boolean virtualThread = isVirtualTypeProperty(pr, PROPERTY_EXECUTOR_PREFIX+pName+PROPERTY_EXECUTOR_TYPE_SUFFIX, true);
				this.poolVirtualThreadType.put(pName, virtualThread);
				
				if(!virtualThread) {
					Integer size = getSizeProperty(pr, PROPERTY_EXECUTOR_PREFIX+pName+PROPERTY_EXECUTOR_SIZE_SUFFIX, true);
					if(size!=null) {
						this.poolSize.put(pName, size);
					}
				}
			}
		}
		if(this.poolVirtualThreadType.isEmpty()) {
			throw new UtilsException("Pool is empty?");
		}
	}
	
	private String getProperty(PropertiesReader pr, String id, boolean required) throws UtilsException {
		String v = pr.getValue_convertEnvProperties(id);
		if(v!=null) {
			return v.trim();
		}
		else if(required) {
			throw new UtilsException("Property '"+PROPERTY_PREFIX+id+"' not found");
		}
		return null;
	}
	private Boolean isVirtualTypeProperty(PropertiesReader pr, String id, boolean required) throws UtilsException {
		String v = getProperty(pr, id, required);
		if(v!=null) {
			if(PROPERTY_EXECUTOR_TYPE_VIRTUAL.equals(v)) {
				return true;
			}
			else if(PROPERTY_EXECUTOR_TYPE_FIXED.equals(v)) {
				return false;
			}
			throw new UtilsException("Property '"+PROPERTY_PREFIX+id+"' uncorrect value ("+v+"): use '"+PROPERTY_EXECUTOR_TYPE_VIRTUAL+"' or '"+PROPERTY_EXECUTOR_TYPE_FIXED+"'");
		}
		return true; // default
	}
	private Integer getSizeProperty(PropertiesReader pr, String id, boolean required) throws UtilsException {
		String v = getProperty(pr, id, required);
		if(v!=null) {
			if(CostantiPdD.CONNETTORE_NIO_ASYNC_CONFIG_AVAILABLE_PROCESSORS.equals(v)) {
				return CostantiPdD.getAvailableProcessors();
			}
			try {
				return Integer.parseInt(v);
			}catch(Exception e) {
				throw new UtilsException("Property '"+PROPERTY_PREFIX+id+"' uncorrect value ("+v+"): "+e.getMessage(),e);
			}
		}
		return null;
	}
	private void fillPoolNames(PropertiesReader pr, List<String> poolNames) throws UtilsException {
		Properties p = pr.readProperties(PROPERTY_EXECUTOR_PREFIX);
		if(!p.isEmpty()) {
			for (Map.Entry<Object,Object> entry : p.entrySet()) {
				if(entry.getKey() instanceof String key &&
					key.contains(".")) {
					String name = key.substring(0,key.indexOf("."));
					if(!poolNames.contains(name)) {
						poolNames.add(name);	
					}
				}
			}
		}
	}
}
