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
package org.openspcoop2.utils.logger.log4j;

import java.text.SimpleDateFormat;
import java.util.List;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.IContext;
import org.openspcoop2.utils.logger.beans.Property;
import org.openspcoop2.utils.logger.beans.context.core.AbstractContext;
import org.openspcoop2.utils.logger.beans.context.core.AbstractContextWithClient;
import org.openspcoop2.utils.logger.beans.context.core.AbstractTransactionWithClient;
import org.openspcoop2.utils.logger.beans.context.core.BaseClient;
import org.openspcoop2.utils.logger.beans.context.core.BaseServer;
import org.openspcoop2.utils.logger.beans.context.core.ConnectionMessage;
import org.openspcoop2.utils.logger.beans.context.core.HttpClient;
import org.openspcoop2.utils.logger.beans.context.core.HttpServer;
import org.openspcoop2.utils.logger.config.DiagnosticConfig;
import org.openspcoop2.utils.logger.config.Log4jConfig;
import org.openspcoop2.utils.logger.config.MultiLoggerConfig;

/**
 * AbstractLog4JLoggerWithContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractLog4JLoggerWithContext extends AbstractLog4JLogger  {

	public AbstractLog4JLoggerWithContext(MultiLoggerConfig config) throws UtilsException {
		super(config.getDiagnosticConfig(),config.getLog4jConfig());
		if(config.isLog4jLoggerEnabled()){
			if(config.getDiagnosticSeverityFilter()!=null){
				AbstractLog4JLogger.setDiagnosticSeverity(config.getDiagnosticSeverityFilter());
			}
			if(config.getEventSeverityFilter()!=null){
				AbstractLog4JLogger.setEventSeverity(config.getEventSeverityFilter());
			}
		}
	}

	public AbstractLog4JLoggerWithContext(DiagnosticConfig diagnosticConfig, Log4jConfig logConfig) throws UtilsException {
		super(diagnosticConfig, logConfig);
	}
	
	protected AbstractContext context;
	protected abstract AbstractContext newContext();
	protected abstract BaseClient getClient();
	protected abstract List<BaseServer> getServers();
	
	@Override
	public void initLogger() throws UtilsException{
		this.initLogger(this.newContext());
	}
	@Override
	public void initLogger(String idTransazione) throws UtilsException{
		this.initLogger(idTransazione, this.newContext());
	}
	@Override
	public void initLogger(IContext contextParam) throws UtilsException{
		this.initLogger(null,contextParam);
	}
	@Override
	public void initLogger(String idTransazione, IContext contextParam) throws UtilsException{
		this.context = (AbstractContext) contextParam;
		super.initLogger(idTransazione);
		this.context.setIdTransaction(this.idTransaction);
	}
	
	@Override
	public IContext getContext() throws UtilsException {
		return this.context;
	}
	
	@Override
	protected void logContext(IContext contextParam, StringBuffer showContext){
		
		SimpleDateFormat dateformat = Utilities.getSimpleDateFormatMs();
		
		AbstractContext context = (AbstractContext) contextParam;
		
		if( context.getTransaction().getProtocol()!=null){
			showContext.append("Protocol="+context.getTransaction().getProtocol()+"\n");
		}
		if( context.getTransaction().getDomain()!=null){
			showContext.append("Domain="+context.getTransaction().getDomain()+"\n");
		} 
		if( context.getTransaction().getRole()!=null){
			showContext.append("Role="+context.getTransaction().getRole().name()+"\n");
		} 
		if( context.getTransaction().getResult()!=null){
			showContext.append("Result="+context.getTransaction().getResult().name()+"\n");
		}
		if( context.getTransaction().getConversationId()!=null){
			showContext.append("ConversationId="+context.getTransaction().getConversationId()+"\n");
		} 
		if( context.getTransaction().getClusterId()!=null){
			showContext.append("ClusterId="+context.getTransaction().getClusterId()+"\n");
		} 
		
		if( this.getClient()!=null){
			showContext.append("*** Client ***\n");
			if( this.getClient().getPrincipal()!=null){
				showContext.append("Principal="+this.getClient().getPrincipal()+"\n");
			}
			if( this.getClient().getName()!=null){
				showContext.append("Name="+this.getClient().getName()+"\n");
			}
			if( this.getClient().getInvocationEndpoint()!=null){
				showContext.append("InvocationEndpoint="+this.getClient().getInvocationEndpoint()+"\n");
			}
			if( this.getClient().getInterfaceName()!=null){
				showContext.append("InterfaceName="+this.getClient().getInterfaceName()+"\n");
			}
			if( this.getClient() instanceof HttpClient) {
				HttpClient httpClient = (HttpClient) this.getClient();
				if( httpClient.getResponseStatusCode()>0){
					showContext.append("ResponseStatusCode="+httpClient.getResponseStatusCode()+"\n");
				}
				if(httpClient.getTransportRequestMethod()!=null) {
					showContext.append("HttpMethod="+httpClient.getTransportRequestMethod().name()+"\n");
				}
				if(httpClient.getSocketClientAddress()!=null) {
					showContext.append("SocketClientAddress="+httpClient.getSocketClientAddress()+"\n");
				}
				if(httpClient.getTransportClientAddress()!=null) {
					showContext.append("TransportClientAddress="+httpClient.getTransportClientAddress()+"\n");
				}
			}
			if( this.getClient().getGenericProperties()!=null && this.getClient().getGenericProperties().size()>0 ){
				for (Property property : this.getClient().getGenericPropertiesAsList()) {
					showContext.append("Property."+property.getName()+"="+property.getValue()+"\n");		
				}
			}
		} 
				
		if( context.getTransaction().getFrom()!=null){
			showContext.append("*** From ***\n");
			if( context.getTransaction().getFrom().getType()!=null){
				showContext.append("Type="+context.getTransaction().getFrom().getType()+"\n");
			}
			if( context.getTransaction().getFrom().getName()!=null){
				showContext.append("Name="+context.getTransaction().getFrom().getName()+"\n");
			}
			if( context.getTransaction().getFrom().getAddress()!=null){
				showContext.append("Address="+context.getTransaction().getFrom().getAddress()+"\n");
			}
		} 
		
		if( context.getTransaction().getTo()!=null){
			showContext.append("*** To ***\n");
			if( context.getTransaction().getTo().getType()!=null){
				showContext.append("Type="+context.getTransaction().getTo().getType()+"\n");
			}
			if( context.getTransaction().getTo().getName()!=null){
				showContext.append("Name="+context.getTransaction().getTo().getName()+"\n");
			}
			if( context.getTransaction().getTo().getAddress()!=null){
				showContext.append("Address="+context.getTransaction().getTo().getAddress()+"\n");
			}
		} 
		
		if( context.getTransaction().getService()!=null){
			showContext.append("*** Service ***\n");
			if( context.getTransaction().getService().getType()!=null){
				showContext.append("Type="+context.getTransaction().getService().getType()+"\n");
			}
			if( context.getTransaction().getService().getName()!=null){
				showContext.append("Name="+context.getTransaction().getService().getName()+"\n");
			}
			if( context.getTransaction().getService().getVersion()!=null){
				showContext.append("Version="+context.getTransaction().getService().getVersion()+"\n");
			}
		} 
		
		if( context.getTransaction().getOperation()!=null){
			showContext.append("*** Operation ***\n");
			if( context.getTransaction().getOperation().getName()!=null){
				showContext.append("Name="+context.getTransaction().getOperation().getName()+"\n");
			}
			if( context.getTransaction().getOperation().getMode()!=null){
				showContext.append("FlowMode="+context.getTransaction().getOperation().getMode().name()+"\n");
			}
		} 
		
		List<BaseServer> listServer = this.getServers();
		if(listServer!=null && !listServer.isEmpty()) {
			int indexServer = 1;
			for (BaseServer baseServer : listServer) {
				if( baseServer!=null){
					if(listServer.size()<2) {
						showContext.append("*** Server ***\n");
						if( baseServer.getName()!=null){
							showContext.append("Name="+baseServer.getName()+"\n");
						}
					}
					else if(baseServer.getName()!=null) {
						showContext.append("*** Server '"+baseServer.getName()+"' ***\n");
					}
					else {
						showContext.append("*** Server-"+indexServer+" ***\n");
					}
					if( baseServer.getIdOperation()!=null){
						showContext.append("IdOperation="+baseServer.getIdOperation()+"\n");
					}
					if( baseServer.getEndpointType()!=null){
						showContext.append("EndpointType="+baseServer.getEndpointType()+"\n");
					}
					if( baseServer.getEndpoint()!=null){
						showContext.append("Endpoint="+baseServer.getEndpoint()+"\n");
					}
					if(baseServer instanceof HttpServer) {
						HttpServer httpServer = (HttpServer) baseServer;
						if( httpServer.getResponseStatusCode()>0){
							showContext.append("ResponseStatusCode="+httpServer.getResponseStatusCode()+"\n");
						}
						if(httpServer.getTransportRequestMethod()!=null) {
							showContext.append("HttpMethod="+httpServer.getTransportRequestMethod().name()+"\n");
						}
					}
					if( baseServer.getGenericProperties()!=null && baseServer.getGenericProperties().size()>0 ){
						for (Property property : baseServer.getGenericPropertiesAsList()) {
							showContext.append("Property."+property.getName()+"="+property.getValue()+"\n");		
						}
					}
					if(baseServer.getRequest()!=null) {
						this._log(true, baseServer.getRequest(), showContext, dateformat);
					}
					if(baseServer.getResponse()!=null) {
						this._log(false, baseServer.getResponse(), showContext, dateformat);
					}
				
				}
				indexServer++;
			}
		}

		if(context.getTransaction() instanceof AbstractTransactionWithClient) {
			
			AbstractTransactionWithClient trWithClient = (AbstractTransactionWithClient) context.getTransaction();
			
			if( trWithClient.getRequest()!=null){
				showContext.append("*** Request ***\n");
				if( trWithClient.getRequest().getIdentifier()!=null){
					if( trWithClient.getRequest().getIdentifier().getId()!=null){
						showContext.append("Identifier="+trWithClient.getRequest().getIdentifier().getId()+"\n");
					}
					if( trWithClient.getRequest().getIdentifier().getDate()!=null){
						showContext.append("DateIdentifier="+dateformat.format(trWithClient.getRequest().getIdentifier().getDate())+"\n");
					}
				}
				if( trWithClient.getRequest().getDate()!=null){
					showContext.append("Date="+dateformat.format(trWithClient.getRequest().getDate())+"\n");
				}
				if( trWithClient.getRequest().getSize()!=null){
					showContext.append("Size="+trWithClient.getRequest().getSize()+"\n");
				}
				if( trWithClient.getRequest().getCorrelationIdentifier()!=null){
					showContext.append("CorrelationIdentifier="+trWithClient.getRequest().getCorrelationIdentifier()+"\n");		
				}
				if( trWithClient.getRequest().getGenericProperties()!=null && trWithClient.getRequest().getGenericProperties().size()>0 ){
					for (Property property : trWithClient.getRequest().getGenericPropertiesAsList()) {
						showContext.append("Property."+property.getName()+"="+property.getValue()+"\n");		
					}
				}
			} 
			
			if( trWithClient.getResponse()!=null){
				showContext.append("*** Response ***\n");
				if( trWithClient.getResponse().getIdentifier()!=null){
					if( trWithClient.getResponse().getIdentifier().getId()!=null){
						showContext.append("Identifier="+trWithClient.getResponse().getIdentifier().getId()+"\n");
					}
					if( trWithClient.getResponse().getIdentifier().getDate()!=null){
						showContext.append("DateIdentifier="+dateformat.format(trWithClient.getResponse().getIdentifier().getDate())+"\n");
					}
				}
				if( trWithClient.getResponse().getDate()!=null){
					showContext.append("Date="+dateformat.format(trWithClient.getResponse().getDate())+"\n");
				}
				if( trWithClient.getResponse().getSize()!=null){
					showContext.append("Size="+trWithClient.getResponse().getSize()+"\n");
				}
				if( trWithClient.getResponse().getCorrelationIdentifier()!=null){
					showContext.append("CorrelationIdentifier="+trWithClient.getResponse().getCorrelationIdentifier()+"\n");		
				}
				if( trWithClient.getResponse().getGenericProperties()!=null && trWithClient.getResponse().getGenericProperties().size()>0 ){
					for (Property property : trWithClient.getResponse().getGenericPropertiesAsList()) {
						showContext.append("Property."+property.getName()+"="+property.getValue()+"\n");		
					}
				}
				if( trWithClient.getResponse().getFault()!=null){
					int max = 250 * 1024;
					String text = null;
					try{
						text = org.openspcoop2.utils.Utilities.convertToPrintableText(trWithClient.getResponse().getFault(),max);
					}catch(Exception e){
						text = e.getMessage();
					}
					showContext.append("Fault="+text+"\n");
				}
			} 
			
		}
		
		showContext.append("\n");
		
	}
		
	private void _log(boolean request, ConnectionMessage message ,StringBuffer showContext,SimpleDateFormat dateformat) {
		if( message!=null){
			String prefix = request ? "Request-" : "Response-";
			if( message.getIdMessage()!=null){
				showContext.append(prefix+"IdMessage="+message.getIdMessage()+"\n");
			}
			if( message.getDate()!=null){
				showContext.append(prefix+"Date="+dateformat.format(message.getDate())+"\n");
			}
			if( message.getSize()!=null){
				showContext.append(prefix+"Size="+message.getSize()+"\n");
			}
		} 
	}
	

	@Override
	protected String getDomain(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		return context.getTransaction().getDomain();
	}
	@Override
	protected String getRequestIdentifier(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		if(context instanceof AbstractContextWithClient) {
			if(((AbstractContextWithClient)context).getRequest()!=null)
				return ((AbstractContextWithClient)context).getRequest().getId();
		}
		return null;
	}
	@Override
	protected String getResponseIdentifier(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		if(context instanceof AbstractContextWithClient) {
			if(((AbstractContextWithClient)context).getResponse()!=null)
				return ((AbstractContextWithClient)context).getResponse().getId();
		}
		return null;
	}
	@Override
	protected String getRequestCorrelationIdentifier(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		if(context instanceof AbstractContextWithClient) {
			if(((AbstractContextWithClient)context).getRequest()!=null)
				return ((AbstractContextWithClient)context).getRequest().getCorrelationIdentifier();
		}
		return null;
	}
	@Override
	protected String getResponseCorrelationIdentifier(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		if(context instanceof AbstractContextWithClient) {
			if(((AbstractContextWithClient)context).getResponse()!=null)
				return ((AbstractContextWithClient)context).getResponse().getCorrelationIdentifier();
		}
		return null;
	}
	@Override
	protected String getClient(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		if(context.getTransaction()==null) {
			return null;
		}
		if(context.getTransaction() instanceof AbstractTransactionWithClient) {
			BaseClient baseClient = ((AbstractTransactionWithClient)context.getTransaction()).getClient();
			if(baseClient!=null) {
				return baseClient.getName();
			}
		}
		return null;
	}
	@Override
	protected String getClientPrincipal(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		if(context.getTransaction()==null) {
			return null;
		}
		if(context.getTransaction() instanceof AbstractTransactionWithClient) {
			BaseClient baseClient = ((AbstractTransactionWithClient)context.getTransaction()).getClient();
			if(baseClient!=null) {
				return baseClient.getPrincipal();
			}
		}
		return null;
	}
	@Override
	protected String getFrom(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		if(context.getTransaction()==null) {
			return null;
		}
		if(context.getTransaction().getFrom()!=null){
			if(context.getTransaction().getFrom().getName()!=null){
				return context.getTransaction().getFrom().getName();
			}
		}
		return null;
	}
	@Override
	protected String getTo(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		if(context.getTransaction()==null) {
			return null;
		}
		if(context.getTransaction().getTo()!=null){
			if(context.getTransaction().getTo().getName()!=null){
				return context.getTransaction().getTo().getName();
			}
		}
		return null;
	}
	@Override
	protected String getService(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		if(context.getTransaction()==null) {
			return null;
		}
		if(context.getTransaction().getService()!=null){
			if(context.getTransaction().getService().getName()!=null){
				return context.getTransaction().getService().getName();
			}
		}
		return null;
	}
	@Override
	protected String getOperation(IContext contextParam){
		AbstractContext context = (AbstractContext) contextParam;
		if(context.getTransaction()==null) {
			return null;
		}
		if(context.getTransaction().getOperation()!=null){
			if(context.getTransaction().getOperation().getName()!=null){
				return context.getTransaction().getOperation().getName();
			}
		}
		return null;
	}


	
}
