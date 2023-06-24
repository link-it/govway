/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.services.connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.Dump;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.Tracciamento;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.pdd.timers.TimerMonitoraggioRisorseThread;
import org.openspcoop2.pdd.timers.TimerThresholdThread;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpServletCredential;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * Servlet che serve per verificare l'installazione di OpenSPCoop.
 * Ritorna 200 in caso l'installazione sia correttamente inizializzata e tutte le risorse disponibili.
 * Ritorna 500 in caso la PdD non sia in funzione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class CheckStatoPdD extends HttpServlet {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private static void sendError(HttpServletResponse res, Logger log, String msg, int code) {
		sendError(res, log, msg, code, msg, null);
	}
	private static void sendError(HttpServletResponse res, Logger log, String msg, int code, Throwable e) {
		sendError(res, log, msg, code, msg, e);
	}
	private static void sendError(HttpServletResponse res, Logger log, String msg, int code, String logMsg) {
		sendError(res, log, msg, code, logMsg, null);
	}
	private static void sendError(HttpServletResponse res, Logger log, String msg, int code, String logMsg, Throwable e)  {
		String prefix = "[GovWayCheck] ";
		String msgLog = prefix+logMsg;
		if(e!=null) {
			log.error(msgLog, e);
		}
		else {
			log.error(msgLog);
		}
		res.setStatus(code);
		res.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
		try {
			res.getOutputStream().write(msg.getBytes());
		}catch(Exception t) {
			log.error("[CheckStato] SendError failed: "+t.getMessage(),t);
		}
	}
	
	private String getPrefixLetturaRisorsa(String resourceName) {
		return "Lettura risorsa ["+resourceName+"] ";
	}
	private String getMsgRisorsaNonAutorizzata(String resourceName) {
		return getPrefixLetturaRisorsa(resourceName)+"non autorizzata";
	}

	private String getMsgDellaRisorsaNonRiuscita(String resourceName) {
		 return "della risorsa ["+resourceName+"] non riuscita";
	}
	
	public static void serializeNotInitializedResponse(HttpServletResponse res, Logger log)  {
		String msg = "API Gateway GovWay non inzializzato";
		sendError(res, log, msg, 503);  // viene volutamente utilizzato il codice 503
	}

	@Override public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
				
		Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(log==null)
			log = LoggerWrapperFactory.getLogger(CheckStatoPdD.class);
		
		if( !OpenSPCoop2Startup.initialize){
			serializeNotInitializedResponse(res, log);
			return;
		}
		
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
			
		// verifico se l'invocazione richiede una lettura di una risorsa jmx
		String resourceName = req.getParameter(CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME);
		if(resourceName!=null && !"".equals(resourceName)){

			boolean checkReadEnabled = false;
			if(properties!=null && properties.isCheckReadJMXResourcesEnabled() ){
				checkReadEnabled = true;
			}
			if(!checkReadEnabled){
				String msg = "Servizio non abilitato";
				sendError(res, log, msg, 500); 
				return;
			}
			
			// prima di procedere verifico una eventuale autenticazione
			String username = properties.getCheckReadJMXResourcesUsername();
			String password = properties.getCheckReadJMXResourcesPassword();
			if(username!=null && password!=null){
				HttpServletCredential identity = new HttpServletCredential(req, log);
				if(!username.equals(identity.getUsername())){
					String msg = getMsgRisorsaNonAutorizzata(resourceName);
					String logMsg = msg+". Richiesta effettuata da username ["+identity.getUsername()+"] sconosciuto";
					sendError(res, log, msg, 500, logMsg); 
					return;
				}
				if(!password.equals(identity.getPassword())){
					String msg = getMsgRisorsaNonAutorizzata(resourceName);
					String logMsg = msg+". Richiesta effettuata da username ["+identity.getUsername()+"] (password errata)";
					sendError(res, log, msg, 500, logMsg); 
					return;
				}
			}
			
			String attributeName = req.getParameter(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_NAME);
			String attributeValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_VALUE);
			String attributeBooleanValue = req.getParameter(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_BOOLEAN_VALUE);
			String methodName = req.getParameter(CostantiPdD.CHECK_STATO_PDD_METHOD_NAME);
			if(attributeName!=null){
				if(attributeValue!=null || attributeBooleanValue!=null){
					try{
						Object v = attributeValue;
						if(attributeBooleanValue!=null){
							v = Boolean.parseBoolean(attributeBooleanValue);
						}
						OpenSPCoop2Startup.gestoreRisorseJMX_staticInstance.setAttribute(resourceName, attributeName, v);	
					}catch(Exception e){
						String msg = "Aggiornamento attributo ["+attributeName+"] "+getMsgDellaRisorsaNonRiuscita(resourceName)+" (valore:"+attributeValue+"): "+e.getMessage();
						sendError(res, log, msg, 500, e); 	
						return;
					}
				}
				else{
					try{
						Object value = OpenSPCoop2Startup.gestoreRisorseJMX_staticInstance.getAttribute(resourceName, attributeName);
						res.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
						res.getOutputStream().write(value.toString().getBytes());	
					}catch(Exception e){
						String msg = "Lettura attributo ["+attributeName+"] "+getMsgDellaRisorsaNonRiuscita(resourceName)+": "+e.getMessage();
						sendError(res, log, msg, 500, e); 	
						return;
					}
				}
			}
			else if(attributeValue!=null){
				String msg = getPrefixLetturaRisorsa(resourceName)+"non effettuata, fornito un valore di attributo senza aver indicato il nome";
				sendError(res, log, msg, 500); 	
				return;
			}
			else if(methodName!=null){
				
				Object [] params = null;
				String [] signatures = null;
				try {
					List<Object> paramsL = new ArrayList<>();
					List<String> signaturesL = new ArrayList<>();
					addParameter(paramsL, signaturesL, req);
					if(!paramsL.isEmpty() && !signaturesL.isEmpty()) {
						params = paramsL.toArray(new Object[1]);
						signatures = signaturesL.toArray(new String[1]);
					}
				}catch(Exception e) {
					String msg = "Invocazione metodo ["+methodName+"] "+getMsgDellaRisorsaNonRiuscita(resourceName)+": "+e.getMessage();
					sendError(res, log, msg, 500, e); 
					return;
				}
				
				try{
					Object value = OpenSPCoop2Startup.gestoreRisorseJMX_staticInstance.invoke(resourceName, methodName, params, signatures);
					res.setContentType(HttpConstants.CONTENT_TYPE_PLAIN);
					res.getOutputStream().write(value.toString().getBytes());	
				}catch(Exception e){
					String msg = "Invocazione metodo ["+methodName+"] "+getMsgDellaRisorsaNonRiuscita(resourceName)+": "+e.getMessage();
					sendError(res, log, msg, 500, e); 
					return;
				}
			}
			else{
				String msg = getPrefixLetturaRisorsa(resourceName)+"non effettuata, nessun attributo o metodo richiesto";
				sendError(res, log, msg, 500); 
				return;
			}
			
		}
			
		
		boolean checkEnabled = false;
		if(properties!=null && properties.isCheckEnabled() ){
			checkEnabled = true;
		}
		if(!checkEnabled){
			String msg = "Servizio non abilitato";
			sendError(res, log, msg, 500); 
			return;
		}
		
						
		if( !OpenSPCoop2Startup.initialize){
			serializeNotInitializedResponse(res, log);
			return;
		}
		else if( !TimerMonitoraggioRisorseThread.isRisorseDisponibili()){
			String msg = "Risorse di sistema non disponibili: "+TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile().getMessage();
			sendError(res, log, msg, 500, TimerMonitoraggioRisorseThread.getRisorsaNonDisponibile()); 
			return;
		}
		else if( !TimerThresholdThread.freeSpace){
			String msg = "Non sono disponibili abbastanza risorse per la gestione della richiesta";
			sendError(res, log, msg, 500); 
			return;
		}
		else if( !Tracciamento.tracciamentoDisponibile){
			String msg = "Tracciatura non disponibile: "+Tracciamento.motivoMalfunzionamentoTracciamento.getMessage();
			sendError(res, log, msg, 500, Tracciamento.motivoMalfunzionamentoTracciamento); 
			return;
		}
		else if( !MsgDiagnostico.gestoreDiagnosticaDisponibile){
			String msg = "Sistema di diagnostica non disponibile: "+MsgDiagnostico.motivoMalfunzionamentoDiagnostici.getMessage();
			sendError(res, log, msg, 500, MsgDiagnostico.motivoMalfunzionamentoDiagnostici); 	
			return;
		}
		else if( !Dump.isSistemaDumpDisponibile()){
			String msg = "Sistema di dump dei contenuti applicativi non disponibile: "+Dump.getMotivoMalfunzionamentoDump().getMessage();
			sendError(res, log, msg, 500, Dump.getMotivoMalfunzionamentoDump()); 
			return;
		}
		
		if(properties.isCheckHealthCheckApiRestEnabled()) {
			String endpoint = properties.getCheckHealthCheckApiRestEndpoint();
			
			HttpRequest request = new HttpRequest();
			if(endpoint.toLowerCase().startsWith("https")) {
				request.setHostnameVerifier(false);
				request.setTrustAllCerts(true);
			}
			request.setMethod(HttpRequestMethod.GET);
			request.setUrl(endpoint);
			
			try {
				HttpResponse response = HttpUtilities.httpInvoke(request);
				
				if(response.getResultHTTPOperation()!=200) {
					StringBuilder sb = new StringBuilder();
					if(response.getHeadersValues()!=null && !response.getHeadersValues().isEmpty()) {
						for (String hdrName : response.getHeadersValues().keySet()) {
							if(HttpConstants.RETURN_CODE.equals(hdrName)) {
								continue;
							}
							List<String> values = response.getHeaderValues(hdrName);
							if(values!=null && !values.isEmpty()) {
								for (String v : values) {
									sb.append("\n").append(hdrName).append(":").append(v);
								}
							}
						}
					}
					else {
						if(response.getContentType()!=null) {
							sb.append("\n").append(HttpConstants.CONTENT_TYPE).append(":").append(response.getContentType());	
						}
					}
					if(response.getContent()!=null) {
						sb.append("\n\n").append(new String(response.getContent()));
					}
					throw new CoreException("HTTP Result:"+response.getResultHTTPOperation()+sb.toString());
				}
				
			}catch(Exception t) {
				String msg = "API REST HealthCheck failed ("+endpoint+")\n"+t.getMessage();
				sendError(res, log, msg, 500, t); 
				return;
			}
		}
		
		if(properties.isCheckHealthCheckApiSoapEnabled()) {
			String endpoint = properties.getCheckHealthCheckApiSoapEndpoint();
			
			HttpRequest request = new HttpRequest();
			if(endpoint.toLowerCase().startsWith("https")) {
				request.setHostnameVerifier(false);
				request.setTrustAllCerts(true);
			}
			request.setContentType(HttpConstants.CONTENT_TYPE_SOAP_1_1);
			request.addHeader(HttpConstants.SOAP11_MANDATORY_HEADER_HTTP_SOAP_ACTION, "echo");
			request.setMethod(HttpRequestMethod.POST);
			request.setUrl(endpoint);
			request.setContent("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns1:getStatus xmlns:ns1=\"https://govway.org/apiStatus\"/></soapenv:Body></soapenv:Envelope>".getBytes());
			
			try {
				HttpResponse response = HttpUtilities.httpInvoke(request);
				
				if(response.getResultHTTPOperation()!=200) {
					StringBuilder sb = new StringBuilder();
					if(response.getHeadersValues()!=null && !response.getHeadersValues().isEmpty()) {
						for (String hdrName : response.getHeadersValues().keySet()) {
							if(HttpConstants.RETURN_CODE.equals(hdrName)) {
								continue;
							}
							List<String> values = response.getHeaderValues(hdrName);
							if(values!=null && !values.isEmpty()) {
								for (String v : values) {
									sb.append("\n").append(hdrName).append(":").append(v);
								}
							}
						}
					}
					else {
						if(response.getContentType()!=null) {
							sb.append("\n").append(HttpConstants.CONTENT_TYPE).append(":").append(response.getContentType());
						}
					}
					if(response.getContent()!=null) {
						sb.append("\n\n").append(new String(response.getContent()));
					}
					throw new ServletException("HTTP Result:"+response.getResultHTTPOperation()+sb.toString());
				}
				
			}catch(Exception t) {
				String msg = "API SOAP HealthCheck failed ("+endpoint+")\n"+t.getMessage();
				sendError(res, log, msg, 500, t); 
			}
		}

	}

	private void addParameter(List<Object> params, List<String> signatures, HttpServletRequest req) throws CoreException {
		
		boolean add = addParameter(params, signatures, req,
				CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE,
				CostantiPdD.CHECK_STATO_PDD_PARAM_INT_VALUE, 
				CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE,
				CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE);
		if(!add) {
			return;
		}
		
		add = addParameter(params, signatures, req,
				CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE_2,
				CostantiPdD.CHECK_STATO_PDD_PARAM_INT_VALUE_2, 
				CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE_2,
				CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE_2);
		if(!add) {
			return;
		}
		
		addParameter(params, signatures, req,
				CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE_3,
				CostantiPdD.CHECK_STATO_PDD_PARAM_INT_VALUE_3, 
				CostantiPdD.CHECK_STATO_PDD_PARAM_LONG_VALUE_3,
				CostantiPdD.CHECK_STATO_PDD_PARAM_BOOLEAN_VALUE_3);
			
	}
	
	private boolean addParameter(List<Object> params, List<String> signatures, HttpServletRequest req,
			String stringNameParameter, String intNameParameter, String longNameParameter, String booleanNameParameter) throws CoreException {
		
		int count = 0;
		List<String> pFound = new ArrayList<>();
		
		String paramStringValue = req.getParameter(stringNameParameter);
		boolean paramStringDefined = paramStringValue!=null && !"".equals(paramStringValue);
		if(paramStringDefined) {
			count++;
			pFound.add(stringNameParameter);
		}
		
		String paramIntValue = req.getParameter(intNameParameter);
		boolean paramIntDefined = paramIntValue!=null && !"".equals(paramIntValue);
		if(paramIntDefined) {
			count++;
			pFound.add(intNameParameter);
		}
		
		String paramLongValue = req.getParameter(longNameParameter);
		boolean paramLongDefined = paramLongValue!=null && !"".equals(paramLongValue);
		if(paramLongDefined) {
			count++;
			pFound.add(longNameParameter);
		}
		
		String paramBooleanValue = req.getParameter(booleanNameParameter);
		boolean paramBooleanDefined = paramBooleanValue!=null && !"".equals(paramBooleanValue);
		if(paramBooleanDefined) {
			count++;
			pFound.add(booleanNameParameter);
		}
		
		if(count==0) {
			return false;
		}
		if(count>1) {
			throw new CoreException("È stato fornito più di un tipo di parametro per la stessa posizione: "+pFound);
		}
		
		if(paramStringDefined){
			params.add(paramStringValue);
			signatures.add(String.class.getName());
		}
		else if(paramIntDefined){
			params.add(Integer.valueOf(paramIntValue));
			signatures.add(Integer.class.getName());
		}
		else if(paramLongDefined){
			params.add(Long.valueOf(paramLongValue));
			signatures.add(Long.class.getName());
		}
		/**else if(paramBooleanDefined){*/
		else {
			params.add(Boolean.valueOf(paramBooleanValue));
			signatures.add(Boolean.class.getName());
		}
		return true;
		
	}
}
