/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.engine;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.manifest.constants.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;

/**
 * URL Protocol Context
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class URLProtocolContext extends HttpServletTransportRequestContext implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PA_FUNCTION = "PA";
	public static final String PD_FUNCTION = "PD";
	public static final String PDtoSOAP_FUNCTION = "PDtoSOAP";
	public static final String IntegrationManager_FUNCTION = "IntegrationManager";
	public static final String IntegrationManager_ENGINE = "IntegrationManagerEngine";
	public static final String IntegrationManager_SERVICE_PD = "PD";
	public static final String IntegrationManager_SERVICE_MessageBox = "MessageBox";
	public static final String IntegrationManager_FUNCTION_PD = IntegrationManager_FUNCTION+"/"+IntegrationManager_SERVICE_PD;
	public static final String IntegrationManager_FUNCTION_MessageBox = IntegrationManager_FUNCTION+"/"+IntegrationManager_SERVICE_MessageBox;
	public static final String IntegrationManager_ENGINE_FUNCTION_PD = IntegrationManager_ENGINE+"/"+IntegrationManager_SERVICE_PD;
	public static final String IntegrationManager_ENGINE_FUNCTION_MessageBox = IntegrationManager_ENGINE+"/"+IntegrationManager_SERVICE_MessageBox;
	public static final String CheckPdD_FUNCTION = "checkPdD";
	
	private IDService idServiceCustom;
	
	public IDService getIdServiceCustom() {
		return this.idServiceCustom;
	}
	
	public boolean isPortaApplicativaService() {
		if(this.idServiceCustom!=null) {
			return IDService.PORTA_APPLICATIVA.equals(this.idServiceCustom);
		}
		else {
			return URLProtocolContext.PA_FUNCTION.equals(this.function);
		}
	}
	public boolean isPortaDelegataService() {
		if(this.idServiceCustom!=null) {
			return IDService.PORTA_DELEGATA.equals(this.idServiceCustom) || 
					IDService.PORTA_DELEGATA_INTEGRATION_MANAGER.equals(this.idServiceCustom) || 
					IDService.PORTA_DELEGATA_XML_TO_SOAP.equals(this.idServiceCustom);
		}
		else {
			return URLProtocolContext.PD_FUNCTION.equals(this.function);
		}
	}
	
	public URLProtocolContext() throws UtilsException{
		super();
	}
	public URLProtocolContext(HttpServletRequest req,Logger logCore, boolean debug, FunctionContextsCustom customContexts) throws ProtocolException, UtilsException{
		this(req, logCore, debug, false, customContexts);
	}
	public URLProtocolContext(HttpServletRequest req,Logger logCore, boolean debug, boolean integrationManagerEngine, FunctionContextsCustom customContexts) throws ProtocolException, UtilsException{
		super(req, logCore, debug);
				
		String servletContext = req.getContextPath();
		String urlInvocazione = req.getRequestURI();
		String servizioInvocato = null;
		if(logCore!=null)
			logCore.debug("SERVLET CONTEXT ["+servletContext+"] URL["+urlInvocazione+"]");
			// es SERVLET CONTEXT [/openspcoop2] URL[/openspcoop2/dededede]
		try {
					
			// Altro...
			if(urlInvocazione.startsWith(servletContext+"/")==false){
				throw new Exception("OpenSPCoop2 [protocol/]service to be used not supplied (context error)");
			}
			
			servizioInvocato = urlInvocazione.substring((servletContext+"/").length());
			if(logCore!=null)
				logCore.debug("SERVIZIO RICHIESTO: ["+servizioInvocato+"]");
			
			// verifico che dopo "openspcoop2" sia stato fornito qualcosa
			if(servizioInvocato==null || "".equals(servizioInvocato.trim())){
				throw new Exception("OpenSPCoop2 [protocol/]service to be used not supplied");
			}
			
			// Esamino il servizio fornito. Puo' darsi che prima del servizio ci sia il protocollo.
			String protocollo = null;
			String function = null;
			String functionParameters = null;
			if(servizioInvocato.indexOf("/") >= 0){
				protocollo = servizioInvocato.substring(0,servizioInvocato.indexOf("/"));
				function = servizioInvocato.substring(servizioInvocato.indexOf("/")+1,servizioInvocato.length());
			}else{
				protocollo = servizioInvocato;
				function = servizioInvocato;
			}
			if(logCore!=null)
				logCore.debug("PROTOCOLLO["+protocollo+"] FUNCTION["+function+"]");
			
			// Vedo se ho un protocollo prima della funzione o direttamente il protocollo
			boolean IMengine = false;
			if(integrationManagerEngine && protocollo.equals(URLProtocolContext.IntegrationManager_ENGINE)) {
				if(logCore!=null)
					logCore.debug("SERVLET INTEGRATION MANAGER SERVICE");
				function = protocollo;
				
				IMengine = true;
				
				Object o = getHttpServletRequest().getAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME);
				if(o == null || !(o instanceof String)){
					throw new Exception("Indicazione del protocollo non presente");
				}
				this.protocolName = (String) o;
				IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.protocolName);
				if(pf==null){
					throw new Exception("Non risulta registrato un protocollo con nome ["+this.protocolName+"]");
				}
				
				o = getHttpServletRequest().getAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_WEB_CONTEXT);
				if(o == null || !(o instanceof String)){
					throw new Exception("Indicazione del web context del protocollo non presente");
				}
				this.protocolWebContext = (String) o;
				pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByServletContext(this.protocolWebContext);
				if(pf==null){
					if(!Costanti.CONTEXT_EMPTY.equals(this.protocolWebContext))
						throw new Exception("Non risulta registrato un protocollo con contesto ["+this.protocolWebContext+"]");
					else
						throw new Exception("Non risulta registrato un protocollo con contesto speciale 'vuoto'");
				}
				
				int sizePrefix = (req.getContextPath() + "/" + function + "/").length();
				if(req.getRequestURI().length()>sizePrefix){
					functionParameters = req.getRequestURI().substring(sizePrefix);
				}
				
				if(functionParameters.startsWith(IntegrationManager_SERVICE_PD)) {
					function+="_"+IntegrationManager_SERVICE_PD;
					
					Object oPD = getHttpServletRequest().getAttribute(org.openspcoop2.core.constants.Costanti.PORTA_DELEGATA);
					if(oPD == null || !(oPD instanceof String)){
						throw new Exception("Indicazione della porta delegata non presente");
					}
					
					functionParameters=(String)oPD;
					
				} 
				else if(functionParameters.startsWith(IntegrationManager_SERVICE_MessageBox)) {
					function+="_"+IntegrationManager_SERVICE_MessageBox;
					
					if(functionParameters.length()>IntegrationManager_SERVICE_MessageBox.length()) {
						functionParameters = functionParameters.substring(IntegrationManager_SERVICE_MessageBox.length());
					}
//					else {
//						functionParameters = null;
//					}
				}
			}
			else if(protocollo.equals(URLProtocolContext.PA_FUNCTION) || 
					protocollo.equals(URLProtocolContext.PD_FUNCTION) || 
					protocollo.equals(URLProtocolContext.PDtoSOAP_FUNCTION) || 
					protocollo.equals(URLProtocolContext.IntegrationManager_FUNCTION) ||
					protocollo.equals(URLProtocolContext.CheckPdD_FUNCTION) ||
					(customContexts!=null && customContexts.isMatch(protocollo, function))) {
				// ContextProtocol Empty
				if(logCore!=null)
					logCore.debug("SERVLET PATH EMPTY");
				if((customContexts!=null && customContexts.isMatch(protocollo, function))) {
					this.idServiceCustom = customContexts.getServiceMatch(protocollo, function);
					function = customContexts.getFunctionMatch(protocollo, function);
					if(logCore!=null)
						logCore.debug("CUSTOM FUNCTION ["+function+"] ["+this.idServiceCustom+"]");
				}
				else {
					function = protocollo;
				}
				protocollo = Costanti.CONTEXT_EMPTY;
				
				int sizePrefix = (req.getContextPath() + "/" + function + "/").length();
				if(req.getRequestURI().length()>sizePrefix){
					functionParameters = req.getRequestURI().substring(sizePrefix);
				}
				else {
					// Serve nei casi custom
					functionParameters = null;
				}
			}
			else{
				// Calcolo function
				if(function.indexOf("/") > 0){
					function = function.substring(0,function.indexOf("/"));
				}
				else if(function.indexOf("?") > 0){
					function = function.substring(0,function.indexOf("?"));
				}
				if(logCore!=null)
					logCore.debug("FUNCTION ["+function+"]");
				
				int sizePrefix = (req.getContextPath() + "/"+ protocollo + "/" + function + "/").length();
				if(req.getRequestURI().length()>sizePrefix){
					functionParameters = req.getRequestURI().substring(sizePrefix);
				}
				else {
					// Serve nei casi custom
					functionParameters = null;
				}
				
				if((customContexts!=null && customContexts.isMatch(function, functionParameters))) {
					this.idServiceCustom = customContexts.getServiceMatch(function, functionParameters);
					function = customContexts.getFunctionMatch(function, functionParameters);
					if(logCore!=null)
						logCore.debug("CUSTOM FUNCTION ["+function+"] ["+this.idServiceCustom+"]");
					// ricalcolo function parameters
					sizePrefix = (req.getContextPath() + "/"+ protocollo + "/" + function + "/").length();
					if(req.getRequestURI().length()>sizePrefix){
						functionParameters = req.getRequestURI().substring(sizePrefix);
					}
					else {
						// Serve nei casi custom
						functionParameters = null;
					}
				}
			}
									
			if(logCore!=null)
				logCore.debug("Elaborazione finale Protocollo["+protocollo+"] Function["+function+"] FunctionParameters ["+functionParameters+"]");
			
			if(!IMengine) {
				this.protocolWebContext = protocollo;
				IProtocolFactory<?> pf = ProtocolFactoryManager.getInstance().getProtocolFactoryByServletContext(this.protocolWebContext);
				if(pf==null){
					if(!Costanti.CONTEXT_EMPTY.equals(this.protocolWebContext))
						throw new Exception("Non risulta registrato un protocollo con contesto ["+this.protocolWebContext+"]");
					else
						throw new Exception("Non risulta registrato un protocollo con contesto speciale 'vuoto'");
				}
				this.protocolName = pf.getProtocol();
			}

			this.function = function;
			this.functionParameters = functionParameters;		
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
}
