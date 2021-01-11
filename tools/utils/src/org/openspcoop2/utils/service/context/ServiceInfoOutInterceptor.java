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

package org.openspcoop2.utils.service.context;

import java.util.Date;

import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.ext.logging.event.DefaultLogEventMapper;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
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
public class ServiceInfoOutInterceptor extends AbstractPhaseInterceptor<Message> {
	// extends org.apache.cxf.ext.logging.LoggingOutInterceptor {

	public ServiceInfoOutInterceptor() {
		//super();
		super(Phase.SEND);
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
				
				HttpClient httpClient = (HttpClient) transactionWithClient.getClient();
				
				try {
					if(event.getResponseCode()!=null) {
						httpClient.setResponseStatusCode(Integer.parseInt(event.getResponseCode()));
					}
				}catch(Throwable t) {
				}
				
				// NON FUNZIONA
//				if(httpClient.getResponseStatusCode()<=0) {
//					Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
//				    if (null == responseCode) {
//				    	Object o = message.get(org.apache.cxf.transport.http.AbstractHTTPDestination.HTTP_RESPONSE);
//				    	if(o!=null && o instanceof javax.servlet.http.HttpServletResponseWrapper) {
//					        javax.servlet.http.HttpServletResponseWrapper responseWrapper = 
//					           (javax.servlet.http.HttpServletResponseWrapper) o;
//					        responseCode = responseWrapper.getStatus();
//				    	}
//				    }
//				    if(responseCode!=null) {
//				    	httpClient.setResponseStatusCode(responseCode);
//				    }
//				}
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

