/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
 * ContextFeature
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@NoJSR250Annotations
@Provider(value = Type.Feature)
public class ContextFeature extends AbstractFeature {
	
	private ContextInInterceptor in;
	private ContextOutInterceptor out;
	private ContextConfig contextConfig;

	public ContextFeature() {
		this.in = new ContextInInterceptor();
		this.out = new ContextOutInterceptor();
	}

	public IContextFactory getContextFactory() {
		return this.in.getContextFactory();
	}
	public void setContextFactory(IContextFactory contextFactory) {
		this.in.setContextFactory(contextFactory);
	}
	
	public ContextConfig getContextConfig() {
		return this.contextConfig;
	}
	public void setContextConfig(ContextConfig contextConfig) {
		this.contextConfig = contextConfig;
		this.in.setContextConfig(contextConfig);
		this.out.setContextConfig(contextConfig);
	}
	
	@Override
	protected void initializeProvider(InterceptorProvider provider, Bus bus) {
		
		if(this.contextConfig==null) {
			this.contextConfig = new ContextConfig();
			this.in.setContextConfig(this.contextConfig);
			this.out.setContextConfig(this.contextConfig);
		}
		
		provider.getInInterceptors().add(this.in);
		provider.getInFaultInterceptors().add(this.in);
				
		if(this.contextConfig.isFillServiceInfo()) {
			ServiceInfoInInterceptor infoIn = new ServiceInfoInInterceptor();
			provider.getInInterceptors().add(infoIn);
			provider.getInFaultInterceptors().add(infoIn);
		}
		
		provider.getOutInterceptors().add(this.out);
		provider.getOutFaultInterceptors().add(this.out);
		
		if(this.contextConfig.isFillServiceInfo()) {
			ServiceInfoOutInterceptor infoOut = new ServiceInfoOutInterceptor();
			provider.getOutInterceptors().add(infoOut);
			provider.getOutFaultInterceptors().add(infoOut);
		}
		
		if(this.contextConfig.isDump()) {
			DumpFeature dumpFeature = new DumpFeature();
			DumpConfig dumpConfig = this.contextConfig.getDumpConfig();
			if(dumpConfig==null) {
				dumpConfig = new DumpConfig();
			}
			dumpConfig.setRole(Role.SERVER);
			dumpFeature.setDumpConfig(dumpConfig);
			dumpFeature.initialize(provider,bus);
		}

	}
}
