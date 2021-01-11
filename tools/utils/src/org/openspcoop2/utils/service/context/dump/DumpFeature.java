/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.service.context.dump;


import org.apache.cxf.Bus;
import org.apache.cxf.annotations.Provider;
import org.apache.cxf.annotations.Provider.Type;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.openspcoop2.utils.service.context.server.ServerConfig;

/**
 * DumpFeature
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@NoJSR250Annotations
@Provider(value = Type.Feature)
public class DumpFeature extends org.apache.cxf.ext.logging.LoggingFeature {
	
	private DumpInInterceptor in;
	private DumpOutInterceptor out;

	public DumpFeature() {
		this.in = new DumpInInterceptor();
		this.out = new DumpOutInterceptor();
	}

	public DumpConfig getDumpConfig() {
		return this.in.getDumpConfig();
	}
	public void setDumpConfig(DumpConfig dumpConfig) {
		this.in.setDumpConfig(dumpConfig);
		this.out.setDumpConfig(dumpConfig);
	}
	
	public ServerConfig getServerConfig() {
		return this.in.getServerConfig();
	}
	public void setServerConfig(ServerConfig serverConfig) {
		this.in.setServerConfig(serverConfig);
		this.out.setServerConfig(serverConfig);
	}
	
	@Override
	protected void initializeProvider(InterceptorProvider provider, Bus bus) {
		provider.getInInterceptors().add(this.in);
		provider.getInFaultInterceptors().add(this.in);

		provider.getOutInterceptors().add(this.out);
		provider.getOutFaultInterceptors().add(this.out);
	}

}
