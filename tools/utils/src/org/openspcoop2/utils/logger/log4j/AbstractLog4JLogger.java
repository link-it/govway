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

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.AbstractBasicLogger;
import org.openspcoop2.utils.logger.IContext;
import org.openspcoop2.utils.logger.beans.Attachment;
import org.openspcoop2.utils.logger.beans.Diagnostic;
import org.openspcoop2.utils.logger.beans.Event;
import org.openspcoop2.utils.logger.beans.Message;
import org.openspcoop2.utils.logger.beans.Property;
import org.openspcoop2.utils.logger.constants.Severity;

/**
 * Log4JLogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractLog4JLogger extends AbstractBasicLogger  {


	public AbstractLog4JLogger(Properties diagnosticProperties, Boolean throwExceptionPlaceholderFailedResolution, String resourceLogProperties) throws UtilsException {
		this(diagnosticProperties,throwExceptionPlaceholderFailedResolution,resourceLogProperties,Log4jType.LOG4Jv1);
	}
	public AbstractLog4JLogger(Properties diagnosticProperties, Boolean throwExceptionPlaceholderFailedResolution, String resourceLogProperties, Log4jType log4jType) throws UtilsException {
		super(diagnosticProperties,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties,log4jType);
	}

	public AbstractLog4JLogger(String diagnosticPropertiesResourceURI, Boolean throwExceptionPlaceholderFailedResolution, String resourceLogProperties) throws UtilsException {
		this(diagnosticPropertiesResourceURI,throwExceptionPlaceholderFailedResolution,resourceLogProperties,Log4jType.LOG4Jv1);
	}	
	public AbstractLog4JLogger(String diagnosticPropertiesResourceURI, Boolean throwExceptionPlaceholderFailedResolution, String resourceLogProperties, Log4jType log4jType) throws UtilsException {
		super(diagnosticPropertiesResourceURI,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties,log4jType);
	}

	public AbstractLog4JLogger(File diagnosticPropertiesResource, Boolean throwExceptionPlaceholderFailedResolution, String resourceLogProperties) throws UtilsException {
		this(diagnosticPropertiesResource,throwExceptionPlaceholderFailedResolution,resourceLogProperties,Log4jType.LOG4Jv1);
	}
	public AbstractLog4JLogger(File diagnosticPropertiesResource, Boolean throwExceptionPlaceholderFailedResolution, String resourceLogProperties, Log4jType log4jType) throws UtilsException {
		super(diagnosticPropertiesResource,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties,log4jType);
	}
	
	public AbstractLog4JLogger(Properties diagnosticProperties, Boolean throwExceptionPlaceholderFailedResolution, Properties resourceLogProperties) throws UtilsException {
		this(diagnosticProperties,throwExceptionPlaceholderFailedResolution,resourceLogProperties,Log4jType.LOG4Jv1);
	}
	public AbstractLog4JLogger(Properties diagnosticProperties, Boolean throwExceptionPlaceholderFailedResolution, Properties resourceLogProperties, Log4jType log4jType) throws UtilsException {
		super(diagnosticProperties,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties,log4jType);
	}

	public AbstractLog4JLogger(String diagnosticPropertiesResourceURI, Boolean throwExceptionPlaceholderFailedResolution, Properties resourceLogProperties) throws UtilsException {
		this(diagnosticPropertiesResourceURI,throwExceptionPlaceholderFailedResolution,resourceLogProperties,Log4jType.LOG4Jv1);
	}
	public AbstractLog4JLogger(String diagnosticPropertiesResourceURI, Boolean throwExceptionPlaceholderFailedResolution, Properties resourceLogProperties, Log4jType log4jType) throws UtilsException {
		super(diagnosticPropertiesResourceURI,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties,log4jType);
	}

	public AbstractLog4JLogger(File diagnosticPropertiesResource, Boolean throwExceptionPlaceholderFailedResolution, Properties resourceLogProperties) throws UtilsException {
		this(diagnosticPropertiesResource,throwExceptionPlaceholderFailedResolution,resourceLogProperties,Log4jType.LOG4Jv1);
	}
	public AbstractLog4JLogger(File diagnosticPropertiesResource, Boolean throwExceptionPlaceholderFailedResolution, Properties resourceLogProperties, Log4jType log4jType) throws UtilsException {
		super(diagnosticPropertiesResource,throwExceptionPlaceholderFailedResolution);
		this.init(resourceLogProperties,log4jType);
	}

	protected Logger logTransaction;
	protected Logger logDiagnostic;
	protected Logger logDump;
	protected Logger logEvent;
	
	protected Log4jType log4jType;
	
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

	private void init(String resourceLogProperties, Log4jType log4jType) throws UtilsException{
		InputStream is = null;
		try{
			is = AbstractLog4JLogger.class.getResourceAsStream(resourceLogProperties);
			if(is==null){
				throw new Exception("Resource ["+resourceLogProperties+"] not found");
			}
			Properties p = new Properties();
			p.load(is);
			this.init(p,log4jType);
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
	
	private void init(Properties p, Log4jType log4jType) throws UtilsException{
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
				
		StringBuffer showContext = new StringBuffer();
		showContext.append("<").append(this.getContext().getIdTransaction()).append(">\n");
		
		this.logContext(contextParam, showContext);
		
		this.logTransaction.info(showContext.toString());
		
		
	}
	protected abstract void logContext(IContext contextParam, StringBuffer showContext);
		

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
	
	protected abstract String getDomain(IContext contextParam);
	protected abstract String getClient(IContext contextParam);
	protected abstract String getClientPrincipal(IContext contextParam);
	protected abstract String getFrom(IContext contextParam);
	protected abstract String getTo(IContext contextParam);
	protected abstract String getService(IContext contextParam);
	protected abstract String getOperation(IContext contextParam);
	
	@Override
	protected void log(Diagnostic diagnostic, IContext context) throws UtilsException {
		
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
		if(this.getDomain(context)!=null){
			showMsg.append(this.getDomain(context));
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
		String client = this.getClient(context);
		String clientPrincipal = this.getClient(context);
		if(client!=null){
			showMsg.append(" Client:"+client);
		}
		else if(clientPrincipal!=null){
			showMsg.append(" Client-Principal:"+clientPrincipal);
		}
		
		String from = this.getFrom(context);
		String to = this.getTo(context);
		String service = this.getService(context);
		String operation = this.getOperation(context);
		
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
		
		if(operation!=null){
			showMsg.append(" O:");
			showMsg.append(operation);
		}
				
		showMsg.append("\n");
		showMsg.append(diagnostic.getMessage());
		showMsg.append("\n");

		// TODO: Per log4j 2 non funziona la conversione in Priority. Per i custom level è tutto differente: http://logging.apache.org/log4j/2.x/manual/customloglevels.html
		// Viene quindi implementato in un if
		if(Log4jType.LOG4Jv1.equals(this.log4jType)){
			this.logDiagnostic.log(this.convertToPriority(diagnostic.getSeverity()), showMsg.toString());
		}
		else{
			SeverityLog4J.log4j2(this.logDiagnostic, diagnostic.getSeverity(), showMsg.toString());
		}

	}
	
	protected Priority convertToPriority(Severity severity){
		return SeverityLog4J.getSeverityLog4J(severity,this.log4jType);
	}

	@Override
	public void log(Message message) throws UtilsException {
		
		StringBuffer out = new StringBuffer();
		
		if(message.getIdTransaction()!=null){
			out.append("<").append(message.getIdTransaction()).append(">");
			out.append(" \n");
		}
		else if(this.getContext().getIdTransaction()!=null){
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
