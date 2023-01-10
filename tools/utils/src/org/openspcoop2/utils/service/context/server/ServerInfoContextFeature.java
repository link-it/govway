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

package org.openspcoop2.utils.service.context.server;


import org.apache.cxf.Bus;
import org.apache.cxf.annotations.Provider;
import org.apache.cxf.annotations.Provider.Type;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.openspcoop2.utils.logger.beans.context.core.Role;
import org.openspcoop2.utils.service.context.dump.DumpConfig;
import org.openspcoop2.utils.service.context.dump.DumpFeature;

/**
 * ServerInfoContextFeature
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@NoJSR250Annotations
@Provider(value = Type.Feature)
public class ServerInfoContextFeature extends AbstractFeature {
	
	private ServerInfoInInterceptor in;
	private ServerInfoOutInterceptor out;
	private ServerConfig serverConfig;

	public ServerInfoContextFeature() {
		this.in = new ServerInfoInInterceptor();
		this.out = new ServerInfoOutInterceptor();
	}


	public ServerConfig getServerConfig() {
		return this.serverConfig;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		ServerInfoUtilities.checkOperationId(this.serverConfig);
		this.in.setServerConfig(this.serverConfig);
		this.out.setServerConfig(this.serverConfig);
	}
	
	@Override
	protected void initializeProvider(InterceptorProvider provider, Bus bus) {
		
		if(this.serverConfig==null) {
			this.serverConfig = new ServerConfig();
			ServerInfoUtilities.checkOperationId(this.serverConfig); // add id
			this.in.setServerConfig(this.serverConfig);
			this.out.setServerConfig(this.serverConfig);
		}
		
		provider.getInInterceptors().add(this.in);
		provider.getInFaultInterceptors().add(this.in);
		
		provider.getOutInterceptors().add(this.out);
		provider.getOutFaultInterceptors().add(this.out);
		
		if(this.serverConfig.isDump()) {
			
			DumpFeature dumpFeature = new DumpFeature();
			DumpConfig dumpConfig = this.serverConfig.getDumpConfig();
			if(dumpConfig==null) {
				dumpConfig = new DumpConfig();
			}
			dumpConfig.setRole(Role.CLIENT);
			dumpFeature.setDumpConfig(dumpConfig);
			dumpFeature.setServerConfig(this.serverConfig);
			dumpFeature.doInitializeProvider(provider,bus);
			
		}
		
	}
}
