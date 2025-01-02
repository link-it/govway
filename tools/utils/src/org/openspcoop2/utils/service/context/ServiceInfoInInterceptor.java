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

package org.openspcoop2.utils.service.context;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.ext.logging.event.DefaultLogEventMapper;
import org.apache.cxf.ext.logging.event.LogEvent;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransaction;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransactionWithClient;
import org.openspcoop2.utils.logger.beans.context.core.HttpClient;
import org.openspcoop2.utils.logger.beans.context.core.Operation;
import org.openspcoop2.utils.logger.beans.context.core.Request;
import org.openspcoop2.utils.logger.beans.context.core.Service;
import org.openspcoop2.utils.logger.constants.context.FlowMode;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;


/**
 * ServiceInfoInInterceptor
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@NoJSR250Annotations
public class ServiceInfoInInterceptor extends AbstractPhaseInterceptor<Message> {
	//extends org.apache.cxf.ext.logging.LoggingInInterceptor {

	public ServiceInfoInInterceptor() {
		//super();
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		
		try {
		
			/*
			java.util.Iterator<String> it = message.keySet().iterator();
			while (it.hasNext()) {
				String string = (String) it.next();
				System.out.println("KEY["+string+"]");
				Object o = message.get(string);
				if(o==null) {
					System.out.println("["+string+"] NULL");
				}
				else {
					System.out.println("["+string+"] '"+o.getClass().getName()+"': "+o);
				}
			}
			*/
			
			Set<String> sensitiveProtocolHeaders = new HashSet<String>();
			final LogEvent event = new DefaultLogEventMapper().map(message, sensitiveProtocolHeaders);
			
	
			IContext ctx = ContextThreadLocal.get();
	
			Map<String, String> headers = event.getHeaders();
			
			AbstractTransaction transaction = ctx.getApplicationContext().getTransaction();
			
			if (event.getServiceName() != null) {
				transaction.setProtocol("SOAP");
				
				if(transaction.getService()==null) {
					transaction.setService(new Service());
				}
				transaction.getService().setName(event.getServiceName().getLocalPart());
			}
			else {
				transaction.setProtocol("REST");
			}
	
			if(transaction.getOperation()==null) {
				transaction.setOperation(new Operation());
			}
			transaction.getOperation().setMode(FlowMode.INPUT_OUTPUT);
			transaction.getOperation().setName(event.getOperationName());
			
			AbstractTransactionWithClient transactionWithClient = null;
			if(transaction instanceof AbstractTransactionWithClient) {
				transactionWithClient = (AbstractTransactionWithClient) transaction;
				
				if(transactionWithClient.getClient()==null) {
					transactionWithClient.setClient(new HttpClient());
				}
				
				transactionWithClient.getClient().setInvocationEndpoint(event.getAddress());
				transactionWithClient.getClient().setPrincipal(event.getPrincipal());
				if(event.getPortName() != null) {
					transactionWithClient.getClient().setInterfaceName(event.getPortName().getLocalPart());
				}
				
				if(transactionWithClient.getClient() instanceof HttpClient) {
					((HttpClient)transactionWithClient.getClient()).setTransportRequestMethod(HttpRequestMethod.valueOf(event.getHttpMethod().toUpperCase()));
					if(headers!=null && headers.size()>0) {
						((HttpClient)transactionWithClient.getClient()).setTransportClientAddress(getIPClientAddressFromHeader(headers));
					}
				}
				
				if(transactionWithClient.getRequest()==null) {
					transactionWithClient.setRequest(new Request());
				}
				if(transactionWithClient.getRequest().getDate()==null) {
					transactionWithClient.getRequest().setDate(new Date());
				}
			}
			
			Object o = message.get(AbstractHTTPDestination.HTTP_REQUEST);
			if(o!=null && o instanceof HttpServletRequest) {
				HttpServletRequest httpServletRequest = (HttpServletRequest) o;
				
				if(transactionWithClient!=null) {
					((HttpClient)transactionWithClient.getClient()).setSocketClientAddress(httpServletRequest.getRemoteAddr());
					
					int contentLength = httpServletRequest.getContentLength();
					if(contentLength>=0) {
						transactionWithClient.getRequest().setSize(Long.valueOf(contentLength));
					}
				}
			}

			
		} catch (Throwable e) {
			LoggerWrapperFactory.getLogger(ServiceInfoInInterceptor.class).error(e.getMessage(),e);
			throw new Fault(e);
		}
	}

	private static String getIPClientAddressFromHeader(Map<String, String> req) throws UtilsException {
		
		List<String> headers = HttpUtilities.getClientAddressHeaders();
		if(headers.size()>0){
			for (String header : headers) {
				String transportAddr = TransportUtils.getObjectAsString(req, header);
				if(transportAddr!=null){
					return transportAddr;
				}
			}
		}
		return null;
	}

}

