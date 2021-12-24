/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

/**
 * ContextOutInterceptor
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContextOutInterceptor extends AbstractPhaseInterceptor<Message> {

	private ContextConfig contextConfig = null;
	
	public ContextOutInterceptor() {
		super(Phase.SETUP_ENDING);
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
			IContext context = ContextThreadLocal.get();
			if(this.contextConfig.isEmitTransaction()) {
				context.getApplicationLogger().log();
			}
			ContextThreadLocal.unset();
		} catch (Throwable e) {
			LoggerWrapperFactory.getLogger(ContextInInterceptor.class).error(e.getMessage(),e);
			throw new Fault(e);
		}
	}

}
