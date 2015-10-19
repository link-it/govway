/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package org.openspcoop2.utils.logger.basic;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.Attachment;
import org.openspcoop2.utils.logger.Event;
import org.openspcoop2.utils.logger.IContext;
import org.openspcoop2.utils.logger.Message;
import org.openspcoop2.utils.logger.Property;
import org.openspcoop2.utils.logger.Severity;
import org.openspcoop2.utils.logger.proxy.ProxyContext;

/**
 * Log4JLogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Log4JLogger extends AbstractBasicLogger  {

	
	public Log4JLogger(Properties diagnosticProperties, Boolean throwExceptionPlaceholderFailedResolution, String resourceLogProperties) throws UtilsException {
		super(diagnosticProperties,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties);
	}

	public Log4JLogger(String diagnosticPropertiesResourceURI, Boolean throwExceptionPlaceholderFailedResolution, String resourceLogProperties) throws UtilsException {
		super(diagnosticPropertiesResourceURI,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties);
	}

	public Log4JLogger(File diagnosticPropertiesResource, Boolean throwExceptionPlaceholderFailedResolution, String resourceLogProperties) throws UtilsException {
		super(diagnosticPropertiesResource,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties);
	}
	
	public Log4JLogger(Properties diagnosticProperties, Boolean throwExceptionPlaceholderFailedResolution, Properties resourceLogProperties) throws UtilsException {
		super(diagnosticProperties,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties);
	}

	public Log4JLogger(String diagnosticPropertiesResourceURI, Boolean throwExceptionPlaceholderFailedResolution, Properties resourceLogProperties) throws UtilsException {
		super(diagnosticPropertiesResourceURI,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties);
	}

	public Log4JLogger(File diagnosticPropertiesResource, Boolean throwExceptionPlaceholderFailedResolution, Properties resourceLogProperties) throws UtilsException {
		super(diagnosticPropertiesResource,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties);
	}

	private Logger logTransaction;
	private Logger logDiagnostic;
	private Logger logDump;
	private Logger logEvent;
	
	private static Severity diagnosticSeverity = Severity.DEBUG_HIGH;
	public static void setDiagnosticSeverity(Severity logSeverity) {
		diagnosticSeverity = logSeverity;
	}
	private static boolean emitDiagnostic(Severity severity){
		return severity.intValue() <= diagnosticSeverity.intValue();
	}
	
	private static Severity eventSeverity = Severity.DEBUG_HIGH;
	public static void setEventSeverity(Severity logSeverity) {
		eventSeverity = logSeverity;
	}
	private static boolean emitEvent(Severity severity){
		return severity.intValue() <= eventSeverity.intValue();
	}

	private void init(String resourceLogProperties) throws UtilsException{
		InputStream is = null;
		try{
			is = Log4JLogger.class.getResourceAsStream(resourceLogProperties);
			if(is==null){
				throw new Exception("Resource ["+resourceLogProperties+"] not found");
			}
			Properties p = new Properties();
			p.load(is);
			this.init(p);
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
		finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception eClose){}
		}
	}
	
	private void init(Properties p) throws UtilsException{
		try{
			PropertyConfigurator.configure(p);
			
			this.logTransaction = Logger.getLogger("transaction");
			if(this.logTransaction==null){
				throw new UtilsException("Category [transaction] not found; expected in log4j configuration");
			}
			
			this.logDiagnostic = Logger.getLogger("diagnostic");
			if(this.logDiagnostic==null){
				throw new UtilsException("Category [diagnostic] not found; expected in log4j configuration");
			}
			
			this.logDump = Logger.getLogger("dump");
			if(this.logDump==null){
				throw new UtilsException("Category [dump] not found; expected in log4j configuration");
			}
			
			this.logEvent = Logger.getLogger("event");
			if(this.logEvent==null){
				throw new UtilsException("Category [event] not found; expected in log4j configuration");
			}
			
		}catch(Exception e){
			throw new UtilsException(e.getMessage(),e);
		}
	}
	
	
	@Override
	public void log() throws UtilsException {
		this.log(this.getContext());
	}
	
	@Override
	public void log(IContext contextParam) throws UtilsException {
		
		SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
		
		StringBuffer showContext = new StringBuffer();
		showContext.append("<").append(this.getContext().getIdTransaction()).append(">\n");
		
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
				showContext.append("InSize="+dateformat.format(context.getRequest().getInSize())+"\n");
			}
			if( context.getRequest().getOutSize()!=null){
				showContext.append("OutSize="+dateformat.format(context.getRequest().getOutSize())+"\n");
			}
			if( context.getRequest().getCorrelationIdentifier()!=null){
				showContext.append("CorrelationIdentifier="+context.getRequest().getCorrelationIdentifier()+"\n");
			}
			if( context.getRequest().getGenericProperties()!=null && context.getRequest().getGenericProperties().size()>0 ){
				for (Property property : context.getRequest().getGenericProperties()) {
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
				showContext.append("InSize="+dateformat.format(context.getResponse().getInSize())+"\n");
			}
			if( context.getResponse().getOutSize()!=null){
				showContext.append("OutSize="+dateformat.format(context.getResponse().getOutSize())+"\n");
			}
			if( context.getResponse().getCorrelationIdentifier()!=null){
				showContext.append("CorrelationIdentifier="+context.getResponse().getCorrelationIdentifier()+"\n");
			}
			if( context.getResponse().getGenericProperties()!=null && context.getResponse().getGenericProperties().size()>0 ){
				for (Property property : context.getResponse().getGenericProperties()) {
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
		
		this.logTransaction.info(showContext.toString());
	}

	@Override
	public void log(Event event) throws UtilsException {

		StringBuffer out = new StringBuffer();

		if(event.getSeverity()==null){
			throw new UtilsException("Severity undefined");
		}
		if(emitEvent(event.getSeverity())==false){
			return;
		}
		
		if(this.getContext().getIdTransaction()!=null){
			out.append("<").append(this.getContext().getIdTransaction()).append(">");
			out.append(" \n");
		}
		
		if(event.getDate()!=null){
			SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
			out.append("Date:"+dateformat.format(event.getDate()));
			out.append(" \n");
		}
		
		if(event.getSeverity()!=null){
			out.append("Severity:"+event.getSeverity().name());
			out.append(" \n");
		}
				
		if(event.getSource()!=null){
			out.append("Source:"+event.getSource());
			out.append(" \n");
		}
		
		if(event.getCode()!=null){
			out.append("Code:"+event.getCode());
			out.append(" \n");
		}
		
		if(event.getClusterId()!=null){
			out.append("ClusterId:"+event.getClusterId());
			out.append(" \n");
		}
		
		if(event.getDescription()!=null){
			out.append("Description:"+event.getDescription());
			out.append(" \n");
		}	
		
		if(event.getCorrelationIdentifier()!=null){
			out.append("CorrelationIdentifier:"+event.getCorrelationIdentifier());
			out.append(" \n");
		}
	    
	    out.append("\n");
	    
	    this.logEvent.info(out.toString()); 
	}

	@Override
	public String getLogParam(String logParam) throws UtilsException {
		//throw new UtilsException("Not Implemented");
		return "DEMOValoreCallback";
	}
	
	@Override
	protected void log(Diagnostic diagnostic, ProxyContext context) throws UtilsException {
		
		if(diagnostic.getSeverity()==null){
			throw new UtilsException("Severity undefined");
		}
		if(emitDiagnostic(diagnostic.getSeverity())==false){
			return;
		}
		
		StringBuffer showMsg = new StringBuffer();
		
		if(diagnostic.getIdTransaction()!=null)
			showMsg.append("<").append(diagnostic.getIdTransaction()).append(">");
		
		if(diagnostic.getCode()!=null){
			if(showMsg.length()>0){
				showMsg.append(" ");
			}
			showMsg.append(diagnostic.getCode());
			showMsg.append(" ");
		}
		if(context.getTransaction().getDomain()!=null){
			showMsg.append(context.getTransaction().getDomain());
			showMsg.append(".");
		}
		showMsg.append(diagnostic.getFunction());
		showMsg.append(" <");
		SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
		showMsg.append(dateformat.format(diagnostic.getDate()));
		showMsg.append("> ");
		showMsg.append("(");
		showMsg.append(diagnostic.getSeverity().name());
		showMsg.append(")");
		if(context.getTransaction().getClient()!=null){
			if(context.getTransaction().getClient().getName()!=null){
				showMsg.append(" Client:"+context.getTransaction().getClient().getName());
			}
			else if(context.getTransaction().getClient().getPrincipal()!=null){
				showMsg.append(" Client-Principal:"+context.getTransaction().getClient().getPrincipal());
			}
		}
		
		String from = null;
		if( context.getTransaction().getFrom()!=null && context.getTransaction().getFrom().getName()!=null ){
			from = context.getTransaction().getFrom().getName();
		}
		String to = null;
		if( context.getTransaction().getTo()!=null && context.getTransaction().getTo().getName()!=null ){
			to = context.getTransaction().getTo().getName();
		}
		String service = null;
		if( context.getTransaction().getService()!=null && context.getTransaction().getService().getName()!=null ){
			service = context.getTransaction().getService().getName();
		}
		
		if(from!=null){
			showMsg.append(" From:");
			showMsg.append(from);
		}
		if( from!=null && (service!=null || to!=null))
			showMsg.append(" -> ");
		
		if(to!=null){
			showMsg.append(" To:");
			showMsg.append(to);
		}
		
		if(service!=null){
			showMsg.append(" S:");
			showMsg.append(service);
		}
		
		if(context.getTransaction().getOperation()!=null && context.getTransaction().getOperation().getName()!=null){
			showMsg.append(" O:");
			showMsg.append(context.getTransaction().getOperation().getName());
		}
				
		showMsg.append("\n");
		showMsg.append(diagnostic.getMessage());
		showMsg.append("\n");

		this.logDiagnostic.log(SeverityLog4J.getSeverityLog4J(diagnostic.getSeverity()), showMsg.toString());


	}

	@Override
	public void log(Message message) throws UtilsException {
		
		StringBuffer out = new StringBuffer();
		
		if(this.getContext().getIdTransaction()!=null){
			out.append("<").append(this.getContext().getIdTransaction()).append(">");
			out.append(" \n");
		}
		
		if(message.getSignature()!=null){
			out.append("Signature:"+message.getSignature());
			out.append(" \n");
		}
		
		if(message.getType()!=null){
			out.append("Type:"+message.getType().name());
			out.append(" \n");
		}
		
		if(message.getContentType()!=null){
			out.append("Content-Type:"+message.getContentType());
			out.append(" \n");
		}
		
		// HEADER
		if(message.getHeaders()!=null && message.getHeaders().size()>0){
			out.append("------ Header ------\n");
			for (Property header : message.getHeaders()) {
				out.append(header.getName()+"="+header.getValue()+"\n");
			}
		}
		
		// RESOURCES
		if(message.getResources()!=null && message.getResources().size()>0){
			out.append("------ Resource ------\n");
			for (Property header : message.getResources()) {
				out.append(header.getName()+"="+header.getValue()+"\n");
			}
		}
		
		// CONTENT
		if(message.getContent()!=null){
			out.append("------ Content ------\n");
			// 1024 = 1K
			// Visualizzo al massimo 250K
			int max = 250 * 1024;
			String text = null;
			try{
				text = org.openspcoop2.utils.Utilities.convertToPrintableText(message.getContent(),max);
			}catch(Exception e){
				text = e.getMessage();
			}
			out.append(text);
			out.append("\n");
		}
		
		// ATTACH
		if(message.getAttachments()!=null && message.getAttachments().size()>0){
			for (Attachment attachment : message.getAttachments()) {
				out.append("------ Attachment id["+attachment.getContentId()+"] ------\n");
				if(attachment.getSignature()!=null){
					out.append("Signature:"+attachment.getSignature());
					out.append(" \n");
				}
				if(attachment.getContentType()!=null){
					out.append("Content-Type:"+attachment.getContentType());
					out.append(" \n");
				}
				if(attachment.getContent()!=null){
					// 1024 = 1K
					 // Visualizzo al massimo 250K
					 int max = 250 * 1024;
					 String text = null;
					 try{
						 text = org.openspcoop2.utils.Utilities.convertToPrintableText(attachment.getContent(),max);
					 }catch(Exception e){
						 text = e.getMessage();
					 }
					 out.append(text);
					 out.append("\n");
				}
			}
		}
	    
	    out.append("\n");
	    
	    this.logDump.info(out.toString()); 
	}
	


	

	
}
