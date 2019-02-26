/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.utils.service.context;

import org.openspcoop2.utils.logger.beans.context.core.Role;
import org.openspcoop2.utils.logger.constants.context.Context;
import org.openspcoop2.utils.service.context.dump.DumpConfig;

/**
 * ContextConfig
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContextConfig {

	private boolean fillServiceInfo = true;
	
	private boolean emitTransaction;
	
	private boolean dump;
	private DumpConfig dumpConfig;
	
	private Context context; // contesto della transazione
	
	private String domain;
	private Role role = Role.SERVER;
	
	private String clusterId;
	
	private String serviceType;
	private String serviceName;
	private Integer serviceVersion;
	
	
	
	public boolean isEmitTransaction() {
		return this.emitTransaction;
	}

	public boolean isDump() {
		return this.dump;
	}
	public void setDump(boolean dump) {
		this.dump = dump;
	}
	public DumpConfig getDumpConfig() {
		return this.dumpConfig;
	}
	public void setDumpConfig(DumpConfig dumpConfig) {
		this.dumpConfig = dumpConfig;
	}
	
	public void setEmitTransaction(boolean emitTransaction) {
		this.emitTransaction = emitTransaction;
	}

	public String getClusterId() {
		return this.clusterId;
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
	
	public Context getContext() {
		return this.context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getServiceType() {
		return this.serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Integer getServiceVersion() {
		return this.serviceVersion;
	}

	public void setServiceVersion(Integer serviceVersion) {
		this.serviceVersion = serviceVersion;
	}
	
	public boolean isFillServiceInfo() {
		return this.fillServiceInfo;
	}

	public void setFillServiceInfo(boolean fillServiceInfo) {
		this.fillServiceInfo = fillServiceInfo;
	}
}
