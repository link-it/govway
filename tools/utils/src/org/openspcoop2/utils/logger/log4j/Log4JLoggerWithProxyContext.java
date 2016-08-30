/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.IContext;
import org.openspcoop2.utils.logger.beans.Property;
import org.openspcoop2.utils.logger.beans.proxy.ProxyContext;
import org.openspcoop2.utils.logger.config.DiagnosticConfig;
import org.openspcoop2.utils.logger.config.Log4jConfig;
import org.openspcoop2.utils.logger.config.MultiLoggerConfig;

/**
 * Log4JLogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Log4JLoggerWithProxyContext extends AbstractLog4JLogger  {

	public Log4JLoggerWithProxyContext(MultiLoggerConfig config) throws UtilsException {
		super(config.getDiagnosticConfig(),config.getLog4jConfig());
	}

	public Log4JLoggerWithProxyContext(DiagnosticConfig diagnosticConfig, Log4jConfig logConfig) throws UtilsException {
		super(diagnosticConfig, logConfig);
	}
	
	private ProxyContext context;
	
	@Override
	public void initLogger() throws UtilsException{
		this.initLogger(new ProxyContext());
	}
	@Override
	public void initLogger(String idTransazione) throws UtilsException{
		this.initLogger(idTransazione, new ProxyContext());
	}
	@Override
	public void initLogger(IContext contextParam) throws UtilsException{
		this.initLogger(null,contextParam);
	}
	@Override
	public void initLogger(String idTransazione, IContext contextParam) throws UtilsException{
		this.context = (ProxyContext) contextParam;
		super.initLogger(idTransazione);
		this.context.setIdTransaction(this.idTransaction);
	}
	
	@Override
	public IContext getContext() throws UtilsException {
		return this.context;
	}
	
	@Override
	protected void logContext(IContext contextParam, StringBuffer showContext){
		
		SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
		
		ProxyContext context = (ProxyContext) contextParam;
		
		if( context.getTransaction().getDomain()!=null){
			showContext.append("Domain="+context.getTransaction().getDomain()+"\n");
		} 
		if( context.getTransaction().getRole()!=null){
			showContext.append("Role="+context.getTransaction().getRole().name()+"\n");
		} 
		if( context.getTransaction().getResult()!=null){
			showContext.append("Result="+context.getTransaction().getResult().name()+"\n");
		}
		if( context.getTransaction().getState()!=null){
			showContext.append("State="+context.getTransaction().getState()+"\n");
		} 
		if( context.getTransaction().getClusterId()!=null){
			showContext.append("ClusterId="+context.getTransaction().getClusterId()+"\n");
		} 
		
		if( context.getTransaction().getClient()!=null){
			showContext.append("*** Client ***\n");
			if( context.getTransaction().getClient().getPrincipal()!=null){
				showContext.append("Principal="+context.getTransaction().getClient().getPrincipal()+"\n");
			}
			if( context.getTransaction().getClient().getName()!=null){
				showContext.append("Name="+context.getTransaction().getClient().getName()+"\n");
			}
			if( context.getTransaction().getClient().getInvocationEndpoint()!=null){
				showContext.append("InvocationEndpoint="+context.getTransaction().getClient().getInvocationEndpoint()+"\n");
			}
			if( context.getTransaction().getClient().getInterfaceName()!=null){
				showContext.append("InterfaceName="+context.getTransaction().getClient().getInterfaceName()+"\n");
			}
			if( context.getTransaction().getClient().getTransportResponseCode()!=null){
				showContext.append("TransportResponseCode="+context.getTransaction().getClient().getTransportResponseCode()+"\n");
			}
		} 
		
		if( context.getTransaction().getServer()!=null){
			showContext.append("*** Server ***\n");
			if( context.getTransaction().getServer().getName()!=null){
				showContext.append("Name="+context.getTransaction().getServer().getName()+"\n");
			}
			if( context.getTransaction().getServer().getEndpointType()!=null){
				showContext.append("EndpointType="+context.getTransaction().getServer().getEndpointType()+"\n");
			}
			if( context.getTransaction().getServer().getEndpoint()!=null){
				showContext.append("Endpoint="+context.getTransaction().getServer().getEndpoint()+"\n");
			}
			if( context.getTransaction().getServer().getTransportCode()!=null){
				showContext.append("TransportCode="+context.getTransaction().getServer().getTransportCode()+"\n");
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
		
		if( context.getRequest()!=null){
			showContext.append("*** Request ***\n");
			if( context.getRequest().getIdentifier()!=null){
				if( context.getRequest().getIdentifier().getId()!=null){
					showContext.append("Identifier="+context.getRequest().getIdentifier().getId()+"\n");
				}
				if( context.getRequest().getIdentifier().getDate()!=null){
					showContext.append("DateIdentifier="+dateformat.format(context.getRequest().getIdentifier().getDate())+"\n");
				}
			}
			if( context.getRequest().getInDate()!=null){
				showContext.append("InDate="+dateformat.format(context.getRequest().getInDate())+"\n");
			}
			if( context.getRequest().getOutDate()!=null){
				showContext.append("OutDate="+dateformat.format(context.getRequest().getOutDate())+"\n");
			}
			if( context.getRequest().getInSize()!=null){
				showContext.append("InSize="+context.getRequest().getInSize()+"\n");
			}
			if( context.getRequest().getOutSize()!=null){
				showContext.append("OutSize="+context.getRequest().getOutSize()+"\n");
			}
			if( context.getRequest().getResultProcessing()!=null){
				showContext.append("ResultProcessing="+context.getRequest().getResultProcessing().name()+"\n");
			}
			if( context.getRequest().getCorrelationIdentifier()!=null){
				showContext.append("CorrelationIdentifier="+context.getRequest().getCorrelationIdentifier()+"\n");		
			}
			if( context.getRequest().getGenericProperties()!=null && context.getRequest().getGenericProperties().size()>0 ){
				for (Property property : context.getRequest().getGenericPropertiesAsList()) {
					showContext.append("Property."+property.getName()+"="+property.getValue()+"\n");		
				}
			}
		} 
		
		if( context.getResponse()!=null){
			showContext.append("*** Response ***\n");
			if( context.getResponse().getIdentifier()!=null){
				if( context.getResponse().getIdentifier().getId()!=null){
					showContext.append("Identifier="+context.getResponse().getIdentifier().getId()+"\n");
				}
				if( context.getResponse().getIdentifier().getDate()!=null){
					showContext.append("DateIdentifier="+dateformat.format(context.getResponse().getIdentifier().getDate())+"\n");
				}
			}
			if( context.getResponse().getInDate()!=null){
				showContext.append("InDate="+dateformat.format(context.getResponse().getInDate())+"\n");
			}
			if( context.getResponse().getOutDate()!=null){
				showContext.append("OutDate="+dateformat.format(context.getResponse().getOutDate())+"\n");
			}
			if( context.getResponse().getInSize()!=null){
				showContext.append("InSize="+context.getResponse().getInSize()+"\n");
			}
			if( context.getResponse().getOutSize()!=null){
				showContext.append("OutSize="+context.getResponse().getOutSize()+"\n");
			}
			if( context.getResponse().getResultProcessing()!=null){
				showContext.append("ResultProcessing="+context.getResponse().getResultProcessing().name()+"\n");
			}
			if( context.getResponse().getCorrelationIdentifier()!=null){
				showContext.append("CorrelationIdentifier="+context.getResponse().getCorrelationIdentifier()+"\n");		
			}
			if( context.getResponse().getGenericProperties()!=null && context.getResponse().getGenericProperties().size()>0 ){
				for (Property property : context.getResponse().getGenericPropertiesAsList()) {
					showContext.append("Property."+property.getName()+"="+property.getValue()+"\n");		
				}
			}
			if( context.getResponse().getInFault()!=null){
				int max = 250 * 1024;
				String text = null;
				try{
					text = org.openspcoop2.utils.Utilities.convertToPrintableText(context.getResponse().getInFault(),max);
				}catch(Exception e){
					text = e.getMessage();
				}
				showContext.append("InFault="+text+"\n");
			}
			if( context.getResponse().getOutFault()!=null){
				int max = 250 * 1024;
				String text = null;
				try{
					text = org.openspcoop2.utils.Utilities.convertToPrintableText(context.getResponse().getOutFault(),max);
				}catch(Exception e){
					text = e.getMessage();
				}
				showContext.append("OutFault="+text+"\n");
			}
		} 
		
		showContext.append("\n");
		
	}
		

	@Override
	protected String getDomain(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		return context.getTransaction().getDomain();
	}
	@Override
	protected String getRequestIdentifier(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		return context.getRequest().getId();
	}
	@Override
	protected String getResponseIdentifier(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		return context.getResponse().getId();
	}
	@Override
	protected String getRequestCorrelationIdentifier(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		return context.getRequest().getCorrelationIdentifier();
	}
	@Override
	protected String getResponseCorrelationIdentifier(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		return context.getResponse().getCorrelationIdentifier();
	}
	@Override
	protected String getClient(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		if(context.getTransaction().getClient()!=null){
			if(context.getTransaction().getClient().getName()!=null){
				return context.getTransaction().getClient().getName();
			}
		}
		return null;
	}
	@Override
	protected String getClientPrincipal(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		if(context.getTransaction().getClient()!=null){
			if(context.getTransaction().getClient().getPrincipal()!=null){
				return context.getTransaction().getClient().getPrincipal();
			}
		}
		return null;
	}
	@Override
	protected String getFrom(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		if(context.getTransaction().getFrom()!=null){
			if(context.getTransaction().getFrom().getName()!=null){
				return context.getTransaction().getFrom().getName();
			}
		}
		return null;
	}
	@Override
	protected String getTo(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		if(context.getTransaction().getTo()!=null){
			if(context.getTransaction().getTo().getName()!=null){
				return context.getTransaction().getTo().getName();
			}
		}
		return null;
	}
	@Override
	protected String getService(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		if(context.getTransaction().getService()!=null){
			if(context.getTransaction().getService().getName()!=null){
				return context.getTransaction().getService().getName();
			}
		}
		return null;
	}
	@Override
	protected String getOperation(IContext contextParam){
		ProxyContext context = (ProxyContext) contextParam;
		if(context.getTransaction().getOperation()!=null){
			if(context.getTransaction().getOperation().getName()!=null){
				return context.getTransaction().getOperation().getName();
			}
		}
		return null;
	}


	
}
