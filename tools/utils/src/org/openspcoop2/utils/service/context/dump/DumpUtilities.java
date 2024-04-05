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

package org.openspcoop2.utils.service.context.dump;


import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.Fault;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.logger.beans.Property;
import org.openspcoop2.utils.logger.beans.context.application.ApplicationTransaction;
import org.openspcoop2.utils.logger.beans.context.batch.BatchTransaction;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransaction;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransactionWithClient;
import org.openspcoop2.utils.logger.beans.context.core.BaseServer;
import org.openspcoop2.utils.logger.beans.context.core.ConnectionMessage;
import org.openspcoop2.utils.logger.beans.context.core.HttpClient;
import org.openspcoop2.utils.logger.beans.context.core.HttpServer;
import org.openspcoop2.utils.logger.beans.context.core.Request;
import org.openspcoop2.utils.logger.beans.context.core.Response;
import org.openspcoop2.utils.logger.beans.context.proxy.ProxyTransaction;
import org.openspcoop2.utils.logger.constants.MessageType;
import org.openspcoop2.utils.service.context.ContextThreadLocal;
import org.openspcoop2.utils.service.context.IContext;
import org.openspcoop2.utils.service.context.server.ServerConfig;

/**
 * DumpUtilities
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpUtilities {
	
	private ServerConfig serverConfig;
	private DumpConfig dumpConfig;

	public DumpUtilities(DumpConfig dumpConfig) {
		this.dumpConfig = dumpConfig;
	}
	public DumpUtilities(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		this.dumpConfig = serverConfig.getDumpConfig();
	}

	
	public void processBeforeSend(DumpRequest request) throws Fault {
		
		try {
			
			//
			// Realizzo la stessa logica di MessageLoggingHandlerUtils
			//
			IContext ctx = ContextThreadLocal.get();
	
			AbstractTransaction transaction = ctx.getApplicationContext().getTransaction();
			
			AbstractTransactionWithClient transactionWithClient = null;
			if(transaction instanceof AbstractTransactionWithClient) {
				transactionWithClient = (AbstractTransactionWithClient) transaction;
			}
			
			org.openspcoop2.utils.logger.beans.Message msg = new org.openspcoop2.utils.logger.beans.Message();
			if(request.getPayload()!=null) {
				msg.setContent(request.getPayload());
			}
			msg.setContentType(request.getContentType());
			if(this.serverConfig!=null) {
				msg.setIdOperation(this.serverConfig.getOperationId());
				msg.setIdServer(this.serverConfig.getServerId());
			}
			msg.setIdTransaction(ctx.getTransactionId());
			switch (this.dumpConfig.getRole()) {
			case SERVER:
				msg.setType(MessageType.REQUEST_IN);
				
				if(transactionWithClient!=null) {
					if(transactionWithClient.getRequest()==null) {
						transactionWithClient.setRequest(new Request());
					}
					if(msg.getContent()!=null ) {
						transactionWithClient.getRequest().setSize(Long.valueOf(msg.getContent().length));
					}
					else {
						transactionWithClient.getRequest().setSize(0l);
					}
				}
				
				break;
			case CLIENT:
				msg.setType(MessageType.REQUEST_OUT);
				
				if(this.serverConfig!=null) {
					
					if(this.serverConfig.getOperationId()==null) {
						throw new Exception("ServerConfig.operationId undefined");
					}
						
					List<BaseServer> list = null;
					BaseServer server = null;
					if(transaction instanceof ApplicationTransaction) {
						list = ((ApplicationTransaction)transaction).getServers();
					}
					else if(transaction instanceof BatchTransaction) {
						list = ((BatchTransaction)transaction).getServers();
					}
					else if(transaction instanceof ProxyTransaction) {
						server = ((ProxyTransaction)transaction).getServer();
					}
					if(list!=null && list.size()>0) {
						for (BaseServer baseServer : list) {
							if(this.serverConfig.getOperationId().equals(baseServer.getIdOperation())) {
								server = baseServer;
								break;
							}
						}
					}
					
					if(server==null) {
						throw new Exception("Server '"+this.serverConfig.getOperationId()+"' not found");
					}
					
					if(server.getRequest()==null) {
						server.setRequest(new ConnectionMessage());
					}
					if(msg.getContent()!=null ) {
						server.getRequest().setSize(Long.valueOf(msg.getContent().length));
					}
					else {
						server.getRequest().setSize(0l);
					}
				}
				
				break;
			}
			
			Map<String, String> headers = request.getHeaders();
			for (Map.Entry<String, String> entry : headers.entrySet())
			{
				String key = entry.getKey();
				String value = entry.getValue();
				msg.addHeader(new Property(key, value));
			}
		
			ctx.getApplicationLogger().log(msg);
			
		} catch (Throwable e) {
			LoggerWrapperFactory.getLogger(DumpInInterceptor.class).error(e.getMessage(),e);
			throw new Fault(e); 
		}
		
	}
	
	public void processAfterSend(DumpResponse response) throws Fault {
		
		try {
			
			// sender.send(event);
			IContext ctx = ContextThreadLocal.get();

			AbstractTransaction transaction = ctx.getApplicationContext().getTransaction();
			
			AbstractTransactionWithClient transactionWithClient = null;
			if(transaction instanceof AbstractTransactionWithClient) {
				transactionWithClient = (AbstractTransactionWithClient) transaction;
			}
			
			org.openspcoop2.utils.logger.beans.Message msg = new org.openspcoop2.utils.logger.beans.Message();
			if(response.getPayload()!=null) {
				msg.setContent(response.getPayload());
			}
			msg.setContentType(response.getContentType());
			if(this.serverConfig!=null) {
				msg.setIdOperation(this.serverConfig.getOperationId());
				msg.setIdServer(this.serverConfig.getServerId());
			}
			msg.setIdTransaction(ctx.getTransactionId());
			switch (this.dumpConfig.getRole()) {
			case SERVER:
				msg.setType(MessageType.RESPONSE_OUT);
				
				if(transactionWithClient!=null) {
					if(transactionWithClient.getResponse()==null) {
						transactionWithClient.setResponse(new Response());
					}
					if(msg.getContent()!=null ) {
						transactionWithClient.getResponse().setSize(Long.valueOf(msg.getContent().length));
					}
					else {
						transactionWithClient.getResponse().setSize(0l);
					}
				}
				
				if(transactionWithClient!=null && transactionWithClient.getClient() instanceof HttpClient) {
					try {
						if(response.getResponseCode()!=null) {
							((HttpClient)transactionWithClient.getClient()).setResponseStatusCode(response.getResponseCode());
						}
					}catch(Throwable t) {
					}
				}
				
				break;
			case CLIENT:
				msg.setType(MessageType.RESPONSE_IN);
				
				if(this.serverConfig!=null) {
					
					if(this.serverConfig.getOperationId()==null) {
						throw new Exception("ServerConfig.operationId undefined");
					}
						
					List<BaseServer> list = null;
					BaseServer server = null;
					if(transaction instanceof ApplicationTransaction) {
						list = ((ApplicationTransaction)transaction).getServers();
					}
					else if(transaction instanceof BatchTransaction) {
						list = ((BatchTransaction)transaction).getServers();
					}
					else if(transaction instanceof ProxyTransaction) {
						server = ((ProxyTransaction)transaction).getServer();
					}
					if(list!=null && list.size()>0) {
						for (BaseServer baseServer : list) {
							if(this.serverConfig.getOperationId().equals(baseServer.getIdOperation())) {
								server = baseServer;
								break;
							}
						}
					}
					
					if(server==null) {
						throw new Exception("Server '"+this.serverConfig.getOperationId()+"' not found");
					}
					
					if(server.getResponse()==null) {
						server.setResponse(new ConnectionMessage());
					}
					if(msg.getContent()!=null ) {
						server.getResponse().setSize(Long.valueOf(msg.getContent().length));
					}
					else {
						server.getResponse().setSize(0l);
					}
					
					if(server instanceof HttpServer) {
						try {
							if(response.getResponseCode()!=null) {
								((HttpServer)server).setResponseStatusCode(response.getResponseCode());
							}
						}catch(Throwable t) {
						}
					}
				}
				
				break;
			}
			
			Map<String, String> headers = response.getHeaders();
			for (Map.Entry<String, String> entry : headers.entrySet())
			{
				String key = entry.getKey();
				String value = entry.getValue();
				msg.addHeader(new Property(key, value));
			}
		
			ctx.getApplicationLogger().log(msg);
							
		} catch (Throwable e) {
			LoggerWrapperFactory.getLogger(DumpInInterceptor.class).error(e.getMessage(),e);
			throw new Fault(e); 
		}
		
	}
	
}
