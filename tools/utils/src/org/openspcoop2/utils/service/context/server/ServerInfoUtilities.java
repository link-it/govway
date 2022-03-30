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

package org.openspcoop2.utils.service.context.server;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.cxf.interceptor.Fault;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.logger.beans.context.application.ApplicationTransaction;
import org.openspcoop2.utils.logger.beans.context.batch.BatchTransaction;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransaction;
import org.openspcoop2.utils.logger.beans.context.core.BaseServer;
import org.openspcoop2.utils.logger.beans.context.core.ConnectionMessage;
import org.openspcoop2.utils.logger.beans.context.core.HttpServer;
import org.openspcoop2.utils.logger.beans.context.proxy.ProxyTransaction;
import org.openspcoop2.utils.service.context.ContextThreadLocal;
import org.openspcoop2.utils.service.context.IContext;

/**
 * ServerInfoUtilities
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerInfoUtilities {
	
	private ServerConfig serverConfig;

	public static void checkOperationId(ServerConfig serverConfig) {
		if(serverConfig.getOperationId()==null) {
			serverConfig.setOperationId(UUID.randomUUID().toString());
		}
	}
	
	public ServerInfoUtilities(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		ServerInfoUtilities.checkOperationId(this.serverConfig);
	}

	
	public void processBeforeSend(ServerInfoRequest request) throws Fault {
		
		try {

			if(this.serverConfig==null) {
				throw new Exception("ServerConfig undefined");
			}
			if(this.serverConfig.getOperationId()==null) {
				throw new Exception("ServerConfig.operationId undefined");
			}
				
			IContext ctx = ContextThreadLocal.get();
	
			AbstractTransaction transaction = ctx.getApplicationContext().getTransaction();
			
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
				server = new HttpServer();
				if(transaction instanceof ApplicationTransaction) {
					((ApplicationTransaction)transaction).addServer(server);
				}
				else if(transaction instanceof BatchTransaction) {
					((BatchTransaction)transaction).addServer(server);
				}
				else if(transaction instanceof ProxyTransaction) {
					((ProxyTransaction)transaction).setServer(server);
				}
			}
			server.setName(this.serverConfig.getServerId());
			server.setIdOperation(this.serverConfig.getOperationId());
			server.setEndpoint(request.getAddress());
			if(server instanceof HttpServer) {
				((HttpServer)server).setTransportRequestMethod(request.getHttpRequestMethod());
			}
			
			if(server.getRequest()==null) {
				server.setRequest(new ConnectionMessage());
			}
			server.getRequest().setDate(new Date());
			
		} catch (Throwable e) {
			LoggerWrapperFactory.getLogger(ServerInfoInInterceptor.class).error(e.getMessage(),e);
			throw new Fault(e);
		}
		
	}
	
	public void processAfterSend(ServerInfoResponse response) throws Fault {
		
		try {
			
			if(this.serverConfig==null) {
				throw new Exception("ServerConfig undefined");
			}
			if(this.serverConfig.getOperationId()==null) {
				throw new Exception("ServerConfig.operationId undefined");
			}
			
			IContext ctx = ContextThreadLocal.get();
	
			AbstractTransaction transaction = ctx.getApplicationContext().getTransaction();
			
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
			server.getResponse().setDate(new Date());
			
			if(server instanceof HttpServer) {
				try {
					if(response.getResponseCode()!=null) {
						((HttpServer)server).setResponseStatusCode(response.getResponseCode());
					}
				}catch(Throwable t) {
				}
			}
			
		} catch (Throwable e) {
			LoggerWrapperFactory.getLogger(ServerInfoInInterceptor.class).error(e.getMessage(),e);
			throw new Fault(e);
		}
		
	}
	
}
