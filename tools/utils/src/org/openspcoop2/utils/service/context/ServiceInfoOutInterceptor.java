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

import java.util.Date;

import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.ext.logging.event.DefaultLogEventMapper;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransaction;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransactionWithClient;
import org.openspcoop2.utils.logger.beans.context.core.HttpClient;
import org.openspcoop2.utils.logger.beans.context.core.Response;


/**
 * ServiceInfoOutInterceptor
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@NoJSR250Annotations
public class ServiceInfoOutInterceptor extends org.apache.cxf.ext.logging.LoggingOutInterceptor {

	public ServiceInfoOutInterceptor() {
		super();
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		
		/*
		java.util.Iterator<String> it = message.keySet().iterator();
		while (it.hasNext()) {
			String string = (String) it.next();
			System.out.println("RESPONSE KEY["+string+"]");
			Object o = message.get(string);
			if(o==null) {
				System.out.println("["+string+"] NULL");
			}
			else {
				System.out.println("["+string+"] '"+o.getClass().getName()+"': "+o);
			}
		}
		*/
		
		final LogEvent event = new DefaultLogEventMapper().map(message);
		
		IContext ctx = ContextThreadLocal.get();
	
		AbstractTransaction transaction = ctx.getApplicationContext().getTransaction();
		
		AbstractTransactionWithClient transactionWithClient = null;
		if(transaction instanceof AbstractTransactionWithClient) {
			transactionWithClient = (AbstractTransactionWithClient) transaction;
			
			if(transactionWithClient.getClient() instanceof HttpClient) {
				try {
					if(event.getResponseCode()!=null) {
						((HttpClient)transactionWithClient.getClient()).setResponseStatusCode(Integer.parseInt(event.getResponseCode()));
					}
				}catch(Throwable t) {
				}
			}
			
			if(transactionWithClient.getResponse()==null) {
				transactionWithClient.setResponse(new Response());
			}
			if(transactionWithClient.getResponse().getDate()==null) {
				transactionWithClient.getResponse().setDate(new Date());
			}
		}
	}
}

