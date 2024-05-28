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
package org.openspcoop2.pdd.core.byok;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.core.byok.BYOKWrappedValue;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntimeBYOKRemoteConfig;
import org.openspcoop2.pdd.core.dynamic.DynamicInfo;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.byok.BYOKManager;
import org.slf4j.Logger;

/**
 * DriverBYOKUtilities
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverBYOKUtilities {

	public static DriverBYOK newInstanceDriverBYOKRuntimeNode(Logger log, boolean wrap, boolean unwrap) throws UtilsException {
		String securityRuntimePolicy = BYOKManager.getSecurityEngineGovWayPolicy();
		if(securityRuntimePolicy!=null) {
			return new DriverBYOKUtilities(log).getDriverBYOKRuntimeNode(wrap, unwrap);
		}
		return null;
	}
	
	private Logger log;
	private ConfigurazioneNodiRuntime configurazioneNodiRuntime;
	private boolean nodoruntime;
	
	public DriverBYOKUtilities(Logger log) {
		this.log = log;
		this.nodoruntime = true;
	}
	public DriverBYOKUtilities(boolean nodoruntime, Logger log, ConfigurazioneNodiRuntime config) {
		this.log = log;
		this.configurazioneNodiRuntime = config;
		this.nodoruntime = nodoruntime;
	}
	
	private DriverBYOK getDriverBYOK(boolean wrap, boolean unwrap) throws UtilsException{
		return this.nodoruntime ?
				getDriverBYOKRuntimeNode(wrap, unwrap) :
				getDriverBYOKManagerNode(wrap, unwrap);
	}
	public DriverBYOK getDriverBYOKRuntimeNode(boolean wrap, boolean unwrap) throws UtilsException{
		// Se definito un remoteEngine, e sono su un nodo runtime, significa che non c'è una distinzione tra ambiente runtime e ambiente manager
		String securityManagerPolicy = BYOKManager.getSecurityRemoteEngineGovWayPolicy();
		if(securityManagerPolicy==null || StringUtils.isEmpty(securityManagerPolicy)) {
			securityManagerPolicy = BYOKManager.getSecurityEngineGovWayPolicy();
		}
		return getDriverBYOK(securityManagerPolicy, null, wrap, unwrap);
	}
	public DriverBYOK getDriverBYOKManagerNode(boolean wrap, boolean unwrap) throws UtilsException{
		return getDriverBYOK(BYOKManager.getSecurityEngineGovWayPolicy(), BYOKManager.getSecurityRemoteEngineGovWayPolicy(), wrap, unwrap);
	}
	
	private DriverBYOK getDriverBYOK(String securityManagerPolicy, String securityManagerRemotePolicy, boolean wrap, boolean unwrap) throws UtilsException{
		if(securityManagerPolicy!=null && StringUtils.isNotEmpty(securityManagerPolicy)) {
			Map<String, Object> dynamicMap = new HashMap<>();
			DynamicInfo dynamicInfo = new  DynamicInfo();
			DynamicUtils.fillDynamicMap(this.log, dynamicMap, dynamicInfo);
			
			if(securityManagerRemotePolicy!=null && StringUtils.isNotEmpty(securityManagerRemotePolicy) &&
					!securityManagerPolicy.equals(securityManagerRemotePolicy) && 
					isBYOKRemoteGovWayNodeConfig(securityManagerPolicy, wrap, unwrap) // quella attuale sarà una chiamata http verso la remote
					) {
				initBYOKDynamicMapRemoteGovWayNode(dynamicMap, wrap, unwrap);
			}
			
			return new DriverBYOK(this.log, securityManagerPolicy, securityManagerRemotePolicy, dynamicMap, true);
		}
		return null;
	}
	private boolean isBYOKRemoteGovWayNodeConfig(String securityManagerPolicy, boolean wrap, boolean unwrap) throws UtilsException {
		BYOKManager byokManager = BYOKManager.getInstance();
		if(byokManager!=null) {
			return byokManager.isBYOKRemoteGovWayNodeConfig(securityManagerPolicy, wrap, unwrap);
		}
		return false;
	}
	
	private void initBYOKDynamicMapRemoteGovWayNode(Map<String, Object> dynamicMap, boolean wrap, boolean unwrap) {
		ConfigurazioneNodiRuntimeBYOKRemoteConfig remoteConfig = new ConfigurazioneNodiRuntimeBYOKRemoteConfig();
		this.configurazioneNodiRuntime.initBYOKDynamicMapRemoteGovWayNode(this.log,dynamicMap, wrap, unwrap, remoteConfig);
	}
	
	public String wrap(String value) throws UtilsException {
		try {
			if(value==null || StringUtils.isEmpty(value)) {
				return value;
			}
			DriverBYOK driverBYOK = getDriverBYOK(true, false);
			if(driverBYOK==null) {
				return value;
			}
			BYOKWrappedValue v = driverBYOK.wrap(value);
			if(v!=null && v.getWrappedValue()!=null) {
				return v.getWrappedValue();
			}
			throw new UtilsException("Wrap value failed");
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public String unwrap(String value) throws UtilsException {
		try {
			if(value==null || StringUtils.isEmpty(value)) {
				return value;
			}
			DriverBYOK driverBYOK = getDriverBYOK(false, true);
			if(driverBYOK==null) {
				return value;
			}
			return driverBYOK.unwrapAsString(value);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public byte[] wrap(byte[] value) throws UtilsException {
		try {
			if(value==null || value.length<=0) {
				return value;
			}
			DriverBYOK driverBYOK = getDriverBYOK(true, false);
			if(driverBYOK==null) {
				return value;
			}
			BYOKWrappedValue v = driverBYOK.wrap(value);
			if(v!=null && v.getWrappedValue()!=null) {
				return v.getWrappedValue().getBytes();
			}
			throw new UtilsException("Wrap value failed");
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	public byte[] unwrap(byte[] value) throws UtilsException {
		try {
			if(value==null || value.length<=0) {
				return value;
			}
			DriverBYOK driverBYOK = getDriverBYOK(false, true);
			if(driverBYOK==null) {
				return value;
			}
			return driverBYOK.unwrap(value);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	public boolean isWrapped(String value) {
		try {
			if(value==null || StringUtils.isEmpty(value) || !isEnabledBYOK()) {
				return false;
			}
			String securityManagerPolicy = BYOKManager.getSecurityEngineGovWayPolicy();
			String driverSecurityManagerPolicy = BYOKManager.getSecurityRemoteEngineGovWayPolicy();
			if(driverSecurityManagerPolicy==null || StringUtils.isEmpty(driverSecurityManagerPolicy)) {
				driverSecurityManagerPolicy = securityManagerPolicy;
			}
			
			String prefix = BYOKUtilities.newPrefixWrappedValue(driverSecurityManagerPolicy);
			return value.startsWith(prefix) && value.length()>prefix.length();
			
		}catch(Exception e) {
			this.log.error("isWrapped failed ["+value+"]: "+e.getMessage(),e);
			/**throw new DriverControlStationException(e.getMessage(),e);*/
			return false; 
		}
	}
	
	public boolean isEnabledBYOK() {
		return BYOKManager.isEnabledBYOK();
	}
}
