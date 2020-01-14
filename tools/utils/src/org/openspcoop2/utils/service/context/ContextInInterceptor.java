/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.logger.beans.context.core.AbstractContext;
import org.openspcoop2.utils.logger.beans.context.core.Service;
import org.slf4j.MDC;


/**
 * ContextInInterceptor
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContextInInterceptor extends AbstractPhaseInterceptor<Message> {
	
	private IContextFactory contextFactory = new ContextFactory();
	private ContextConfig contextConfig = null;
		
	public ContextInInterceptor() {
		super(Phase.RECEIVE);
	}

	public IContextFactory getContextFactory() {
		return this.contextFactory;
	}
	public void setContextFactory(IContextFactory contextFactory) {
		this.contextFactory = contextFactory;
	}
	
	public ContextConfig getContextConfig() {
		return this.contextConfig;
	}
	public void setContextConfig(ContextConfig contextConfig) {
		this.contextConfig = contextConfig;
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		try {
			
			IContext context = this.contextFactory.newContext();
			if(context.getApplicationContext() instanceof AbstractContext) {
				AbstractContext applicationContext = (AbstractContext) context.getApplicationContext();
				if(applicationContext.getTransaction()!=null) {
					
					applicationContext.getTransaction().setContext(this.contextConfig.getContext());
					
					applicationContext.getTransaction().setDomain(this.contextConfig.getDomain());
					applicationContext.getTransaction().setRole(this.contextConfig.getRole());
					
					if(this.contextConfig.getServiceType()!=null || this.contextConfig.getServiceName()!=null || this.contextConfig.getServiceVersion()!=null) {
						Service service = new Service();
						service.setType(this.contextConfig.getServiceType());
						service.setName(this.contextConfig.getServiceName());
						service.setVersion(this.contextConfig.getServiceVersion());
						applicationContext.getTransaction().setService(service);
					}
					
					applicationContext.getTransaction().setClusterId(this.contextConfig.getClusterId());
				}
			}
			ContextThreadLocal.set(context);
			MDC.put(MD5Constants.TRANSACTION_ID, context.getTransactionId());
		} catch (Throwable e) {
			LoggerWrapperFactory.getLogger(ContextInInterceptor.class).error(e.getMessage(),e);
			throw new Fault(e);
		}
	}
}
