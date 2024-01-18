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
package org.openspcoop2.utils.logger.log4j;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.logger.AbstractBasicLogger;
import org.openspcoop2.utils.logger.IContext;
import org.openspcoop2.utils.logger.beans.Attachment;
import org.openspcoop2.utils.logger.beans.Diagnostic;
import org.openspcoop2.utils.logger.beans.Event;
import org.openspcoop2.utils.logger.beans.Message;
import org.openspcoop2.utils.logger.beans.Property;
import org.openspcoop2.utils.logger.config.DiagnosticConfig;
import org.openspcoop2.utils.logger.config.Log4jConfig;
import org.openspcoop2.utils.logger.constants.Severity;
import org.slf4j.Logger;

/**
 * Log4JLogger
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractLog4JLogger extends AbstractBasicLogger  {


	public AbstractLog4JLogger(DiagnosticConfig diagnosticConfig, Log4jConfig logConfig) throws UtilsException {
		super(diagnosticConfig);
		
		Log4jConfig.validate(logConfig);
		Log4jConfig.setConfigurationLogger(logConfig);
		
		this.logTransaction = LoggerWrapperFactory.getLogger("transaction");
		if(this.logTransaction==null){
			throw new UtilsException("Category [transaction] not found; expected in log4j configuration");
		}
		
		this.logDiagnostic = LoggerWrapperFactory.getLoggerImpl("diagnostic");
		if(this.logDiagnostic==null){
			throw new UtilsException("Category [diagnostic] not found; expected in log4j configuration");
		}
		
		this.logDump = LoggerWrapperFactory.getLogger("dump");
		if(this.logDump==null){
			throw new UtilsException("Category [dump] not found; expected in log4j configuration");
		}
		
		this.logEvent = LoggerWrapperFactory.getLogger("event");
		if(this.logEvent==null){
			throw new UtilsException("Category [event] not found; expected in log4j configuration");
		}
		
	}

	protected Logger logTransaction;
	protected org.apache.logging.log4j.Logger logDiagnostic;
	protected Map<String, org.apache.logging.log4j.Logger> mapFunctionToLogDiagnostic = null;
	protected Logger logDump;
	protected Logger logEvent;
	
	private synchronized void initLogFunctionToDiagnostic(){
		if(this.mapFunctionToLogDiagnostic==null){
				
			List<String> loggers = new ArrayList<>();
			org.apache.logging.log4j.core.LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager.getContext(false);
			Iterator<String> it = context.getConfiguration().getLoggers().keySet().iterator();
			while (it.hasNext()) {
				String logger = (String) it.next();
				loggers.add(logger);
			}

			this.mapFunctionToLogDiagnostic = new HashMap<String, org.apache.logging.log4j.Logger>();
			List<String> functions = this.diagnosticManager.getFunctions();
			if(functions!=null && !functions.isEmpty()) {
				for (String function : functions) {
					//System.out.println("FUNCTION ["+function+"] ");
					if(loggers.contains("diagnostic."+function)){
						//System.out.println("REGISTRO");
						this.mapFunctionToLogDiagnostic.put(function, LoggerWrapperFactory.getLoggerImpl("diagnostic."+function));
					}
				}
			}
		}
	}
	
	private static Severity diagnosticSeverity = Severity.DEBUG_HIGH;
	public static void setDiagnosticSeverity(Severity logSeverity) {
		if(logSeverity!=null){
			diagnosticSeverity = logSeverity;
		}
	}
	private static boolean emitDiagnostic(Severity severity){
		return severity.intValue() <= diagnosticSeverity.intValue();
	}
	
	private static Severity eventSeverity = Severity.DEBUG_HIGH;
	public static void setEventSeverity(Severity logSeverity) {
		if(logSeverity!=null){
			eventSeverity = logSeverity;
		}
	}
	private static boolean emitEvent(Severity severity){
		return severity.intValue() <= eventSeverity.intValue();
	}

	
	@Override
	public void log() throws UtilsException {
		this.log(this.getContext());
	}
	
	@Override
	public void log(IContext contextParam) throws UtilsException {
				
		StringBuilder showContext = new StringBuilder();
		showContext.append("<").append(this.getContext().getIdTransaction()).append(">\n");
		
		this.logContext(contextParam, showContext);
		
		this.logTransaction.info(showContext.toString());
		
		
	}
	protected abstract void logContext(IContext contextParam, StringBuilder showContext);
		

	@Override
	public void log(Event event) throws UtilsException {

		StringBuilder out = new StringBuilder();

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
			SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
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
	protected abstract String getRequestIdentifier(IContext contextParam);
	protected abstract String getResponseIdentifier(IContext contextParam);
	protected abstract String getRequestCorrelationIdentifier(IContext contextParam);
	protected abstract String getResponseCorrelationIdentifier(IContext contextParam);
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
		
		StringBuilder showMsg = new StringBuilder();
		
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
		SimpleDateFormat dateformat = DateUtils.getSimpleDateFormatMs();
		showMsg.append(dateformat.format(diagnostic.getDate()));
		showMsg.append("> ");
		showMsg.append("(");
		showMsg.append(diagnostic.getSeverity().name());
		showMsg.append(")");
				
		String idRichiesta = this.getRequestIdentifier(context);
		String idRisposta = this.getResponseIdentifier(context);
		if(idRichiesta!=null){
			showMsg.append(" [id-req:").append(idRichiesta).append("]");
		}
		if(idRisposta!=null){
			showMsg.append(" [id-resp:").append(idRisposta).append("]");
		}
		
		String idCorrelazioneRichiesta = this.getRequestCorrelationIdentifier(context);
		String idCorrelazioneRisposta = this.getResponseCorrelationIdentifier(context);
		if(idCorrelazioneRichiesta!=null){
			showMsg.append(" {correlation-req:").append(idCorrelazioneRichiesta).append("}");
		}
		if(idCorrelazioneRisposta!=null){
			showMsg.append(" {correlation-resp:").append(idCorrelazioneRisposta).append("}");
		}
		
		String client = this.getClient(context);
		String clientPrincipal = this.getClientPrincipal(context);
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

		org.apache.logging.log4j.Logger log = this.logDiagnostic;
		
		if(this.mapFunctionToLogDiagnostic==null){
			this.initLogFunctionToDiagnostic();
		}
		if(this.mapFunctionToLogDiagnostic.containsKey(diagnostic.getFunction())){
			log = this.mapFunctionToLogDiagnostic.get(diagnostic.getFunction());
		}
		
		log.log(this.convertToPriority(diagnostic.getSeverity()), showMsg.toString());

	}
	
	protected Level convertToPriority(Severity severity){
		return SeverityLog4J.getSeverityLog4J(severity);
	}

	@Override
	public void log(Message message) throws UtilsException {
		
		StringBuilder out = new StringBuilder();
		
		if(message.getIdTransaction()!=null){
			out.append("<").append(message.getIdTransaction()).append(">");
			out.append(" \n");
		}
		else if(this.getContext().getIdTransaction()!=null){
			out.append("<").append(this.getContext().getIdTransaction()).append(">");
			out.append(" \n");
		}
		
		if(message.getIdMessage()!=null){
			out.append("IdMessage:"+message.getIdMessage());
			out.append(" \n");
		}
		
		if(message.getIdServer()!=null){
			out.append("IdServer:"+message.getIdServer());
			out.append(" \n");
		}
		
		if(message.getIdOperation()!=null){
			out.append("IdOperation:"+message.getIdOperation());
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
			for (Property header : message.getHeadersAsList()) {
				out.append(header.getName()+"="+header.getValue()+"\n");
			}
		}
		
		// RESOURCES
		if(message.getResources()!=null && message.getResources().size()>0){
			out.append("------ Resource ------\n");
			for (Property header : message.getResourcesAsList()) {
				out.append(header.getName()+"="+header.getValue()+"\n");
			}
		}
		
		// CONTENT
		if(message.getContent()!=null){
			out.append("------ Content ------\n");
			out.append("Size:"+message.getContent().length+"\n");
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
				if(attachment.getContentType()!=null){
					out.append("Content-Type:"+attachment.getContentType());
					out.append(" \n");
				}
				if(attachment.getContent()!=null){
					out.append("Size:"+attachment.getContent().length+"\n");
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
