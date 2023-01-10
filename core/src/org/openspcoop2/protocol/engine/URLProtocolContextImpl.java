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

package org.openspcoop2.protocol.engine;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.protocol.manifest.constants.Costanti;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.FunctionContextsCustom;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;

/**
 * URL Protocol Context
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class URLProtocolContextImpl extends org.openspcoop2.protocol.sdk.state.URLProtocolContext implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public URLProtocolContextImpl(Logger logCore) throws UtilsException{
		super(logCore);
	}
	public URLProtocolContextImpl(HttpServletRequest req,Logger logCore, boolean debug, FunctionContextsCustom customContexts) throws ProtocolException, UtilsException{
		super(req, logCore, debug, customContexts);
	}
	public URLProtocolContextImpl(HttpServletRequest req,Logger logCore, boolean debug, boolean integrationManagerEngine, FunctionContextsCustom customContexts) throws ProtocolException, UtilsException{
		super(req, logCore, debug, integrationManagerEngine, customContexts);
	}
	
	@Override
	protected void init(HttpServletRequest req,Logger logCore, boolean debug, boolean integrationManagerEngine, FunctionContextsCustom customContexts) throws ProtocolException, UtilsException{
				
		String servletContext = req.getContextPath();
		String urlInvocazione = req.getRequestURI();
		String servizioInvocato = null;
		if(logCore!=null)
			logCore.debug("SERVLET CONTEXT ["+servletContext+"] URL["+urlInvocazione+"]");
			// es SERVLET CONTEXT [/govway] URL[/govway/altriParametri]
		try {
			
			ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
			IProtocolFactory<?> pfEmptyContext = null;
			IDService idServiceDefaultEmptyContext = null;
			try {
				pfEmptyContext = protocolFactoryManager.getProtocolFactoryWithEmptyContext();
				idServiceDefaultEmptyContext = protocolFactoryManager.getDefaultServiceForEmptyContext();
			}catch(Throwable e) {
				// ignore
			}
			
			// Altro...
			if(urlInvocazione.startsWith(servletContext+"/")){
				servizioInvocato = urlInvocazione.substring((servletContext+"/").length());
			}
			else if(urlInvocazione.equals(servletContext)){
				servizioInvocato = null;
			}
			else {
				throw new Exception("GovWay [protocol/]service to be used not supplied (context error)");
			}
			
			
			if(logCore!=null)
				logCore.debug("SERVIZIO RICHIESTO: ["+servizioInvocato+"]");
			
			// verifico che dopo "openspcoop2" sia stato fornito qualcosa
			if(servizioInvocato==null || "".equals(servizioInvocato.trim())){
				if(pfEmptyContext!=null && idServiceDefaultEmptyContext!=null) {
					this.protocolName = pfEmptyContext.getProtocol();
					this.idServiceCustom = idServiceDefaultEmptyContext;
					switch (idServiceDefaultEmptyContext) {
					case PORTA_DELEGATA:
						this.function = org.openspcoop2.protocol.sdk.state.URLProtocolContext.PD_FUNCTION_GOVWAY;
						break;
					case PORTA_APPLICATIVA:
						this.function = org.openspcoop2.protocol.sdk.state.URLProtocolContext.PA_FUNCTION_GOVWAY;
						break;
					case PORTA_DELEGATA_XML_TO_SOAP:
						this.function = org.openspcoop2.protocol.sdk.state.URLProtocolContext.PDtoSOAP_FUNCTION_GOVWAY;
						break;
					default:
						throw new Exception("GovWay [protocol/]service to be used not supplied");
					}
					this.functionParameters = servizioInvocato;
					this.protocolWebContext = Costanti.CONTEXT_EMPTY;
				}
				else {
					throw new Exception("GovWay [protocol/]service to be used not supplied");
				}
			}
			else {
			
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
				if(integrationManagerEngine && protocollo.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.IntegrationManager_ENGINE)) {
					if(logCore!=null)
						logCore.debug("SERVLET INTEGRATION MANAGER SERVICE");
					function = protocollo;
					
					IMengine = true;
					
					Object o = getHttpServletRequest().getAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_NAME.getValue());
					if(o == null || !(o instanceof String)){
						throw new Exception("Indicazione del protocollo non presente");
					}
					this.protocolName = (String) o;
					IProtocolFactory<?> pf = protocolFactoryManager.getProtocolFactoryByName(this.protocolName);
					if(pf==null){
						throw new Exception("Non risulta registrato un protocollo con nome ["+this.protocolName+"]");
					}
					
					o = getHttpServletRequest().getAttribute(org.openspcoop2.core.constants.Costanti.PROTOCOL_WEB_CONTEXT.getValue());
					if(o == null || !(o instanceof String)){
						throw new Exception("Indicazione del web context del protocollo non presente");
					}
					this.protocolWebContext = (String) o;
					pf = protocolFactoryManager.getProtocolFactoryByServletContext(this.protocolWebContext);
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
					
					if(functionParameters!=null && functionParameters.startsWith(IntegrationManager_SERVICE_PD)) {
						function+="_"+IntegrationManager_SERVICE_PD;
						
						Object oPD = getHttpServletRequest().getAttribute(org.openspcoop2.core.constants.Costanti.PORTA_DELEGATA.getValue());
						if(oPD == null || !(oPD instanceof String)){
							throw new Exception("Indicazione della porta delegata non presente");
						}
						
						functionParameters=(String)oPD;
						
					} 
					else if(functionParameters!=null && functionParameters.startsWith(IntegrationManager_SERVICE_MessageBox)) {
						function+="_"+IntegrationManager_SERVICE_MessageBox;
						
						if(functionParameters.length()>IntegrationManager_SERVICE_MessageBox.length()) {
							functionParameters = functionParameters.substring(IntegrationManager_SERVICE_MessageBox.length());
						}
	//					else {
	//						functionParameters = null;
	//					}
					}
				}
				else if(protocollo.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.PA_FUNCTION) || 
						protocollo.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.PD_FUNCTION) || 
						protocollo.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.PDtoSOAP_FUNCTION) || 
						protocollo.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.IntegrationManager_FUNCTION) ||
						protocollo.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.Check_FUNCTION) ||
						protocollo.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.Proxy_FUNCTION) ||
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
					// Verifico se esiste o meno il protocollo tra quelli registrati
					boolean casoSpeciale_noProtocollo_noFunction = false;
					try {
						IProtocolFactory<?> pfCheck = protocolFactoryManager.getProtocolFactoryByServletContext(protocollo);
						if(pfCheck==null) {
							throw new Exception("NotFound");
						}
					}catch(Exception e) {
						if(pfEmptyContext!=null) {
							if(function!=null && !"".equals(function)) {
								function = protocollo + "/" + function;
							}
							else {
								function = protocollo;
							}
							protocollo = Costanti.CONTEXT_EMPTY;
							casoSpeciale_noProtocollo_noFunction = true;
						}
					}
						
					
					// Calcolo function
					String functionParameterForCheckCustom = null;
					if(function.indexOf("/") > 0){
						functionParameterForCheckCustom = function.substring(function.indexOf("/"));
						if(functionParameterForCheckCustom.length()>1 && functionParameterForCheckCustom.startsWith("/")) {
							functionParameterForCheckCustom = functionParameterForCheckCustom.substring(1);
						}
					}
					if(function.indexOf("/") > 0){
						function = function.substring(0,function.indexOf("/"));
					}
					else if(function.indexOf("?") > 0){
						function = function.substring(0,function.indexOf("?"));
					}
					if(logCore!=null)
						logCore.debug("FUNCTION ["+function+"]");
					
					boolean emptyFunction = false;
					if(casoSpeciale_noProtocollo_noFunction ||
							(
									! (function.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.PA_FUNCTION) || 
											function.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.PD_FUNCTION) || 
											function.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.PDtoSOAP_FUNCTION) || 
											function.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.IntegrationManager_FUNCTION) ||
											function.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.Check_FUNCTION) ||
											function.equals(org.openspcoop2.protocol.sdk.state.URLProtocolContext.Proxy_FUNCTION) ||
										(customContexts!=null && customContexts.isMatch(function, functionParameterForCheckCustom))) 
									)
							){
						IDService idS = null;
						if(casoSpeciale_noProtocollo_noFunction) {
							if(pfEmptyContext!=null && idServiceDefaultEmptyContext!=null) {
								idS = idServiceDefaultEmptyContext;							
							}
						}
						else {
							idS = protocolFactoryManager.getDefaultServiceForWebContext(protocollo);
						}
						if(idS!=null) {
							this.protocolName = pfEmptyContext.getProtocol();
							this.idServiceCustom = idS;
							switch (idS) {
							case PORTA_DELEGATA:
								function = org.openspcoop2.protocol.sdk.state.URLProtocolContext.PD_FUNCTION_GOVWAY;
								emptyFunction = true;
								break;
							case PORTA_APPLICATIVA:
								function = org.openspcoop2.protocol.sdk.state.URLProtocolContext.PA_FUNCTION_GOVWAY;
								emptyFunction = true;
								break;
							case PORTA_DELEGATA_XML_TO_SOAP:
								function = org.openspcoop2.protocol.sdk.state.URLProtocolContext.PDtoSOAP_FUNCTION_GOVWAY;
								emptyFunction = true;
								break;
							default:
							}
						}
					}
					
					StringBuilder bfSizePrefix = new StringBuilder(req.getContextPath());
					if(!Costanti.CONTEXT_EMPTY.equals(protocollo)) {
						bfSizePrefix.append("/").append(protocollo);
					}
					if(!emptyFunction) {
						bfSizePrefix.append("/").append(function);
					}
					bfSizePrefix.append("/");
					
					int sizePrefix = bfSizePrefix.length();
					if(req.getRequestURI().length()>sizePrefix){
						functionParameters = req.getRequestURI().substring(sizePrefix);
					}
					else {
						// Serve nei casi custom
						functionParameters = null;
					}
					
					if(!casoSpeciale_noProtocollo_noFunction && !emptyFunction) {
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
					
					if(casoSpeciale_noProtocollo_noFunction && emptyFunction) {
						this.requestURI = this.requestURI.replace(servletContext+"/", servletContext+"/"+function+"/");
					}
					else if(emptyFunction) {
						this.requestURI = this.requestURI.replace(servletContext+"/"+protocollo+"/", servletContext+"/"+protocollo+"/"+function+"/");
					}
				}
										
				if(logCore!=null)
					logCore.debug("Elaborazione finale Protocollo["+protocollo+"] Function["+function+"] FunctionParameters ["+functionParameters+"]");
				
				if(!IMengine) {
					this.protocolWebContext = protocollo;
					IProtocolFactory<?> pf = protocolFactoryManager.getProtocolFactoryByServletContext(this.protocolWebContext);
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
				
			}
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	@Override
	public String toString() {
		return this.toString("");
	}
	@Override
	public String toString(String prefix) {
		StringBuilder sb = new StringBuilder(super.toString(prefix));
		if(this.idServiceCustom!=null) {
			sb.append("\n").append(prefix).append("idServiceCustom: ").append(this.idServiceCustom);
		}
		return sb.toString();
	}
}
