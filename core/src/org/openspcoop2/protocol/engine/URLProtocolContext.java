/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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
import org.openspcoop2.protocol.manifest.constants.Costanti;
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
	public static final String IntegrationManager_FUNCTION_PD = "IntegrationManager/PD";
	public static final String IntegrationManager_FUNCTION_MessageBox = "IntegrationManager/MessageBox";
	public static final String CheckPdD_FUNCTION = "checkPdD";
	
	public URLProtocolContext() throws UtilsException{
		super();
	}
	public URLProtocolContext(HttpServletRequest req,Logger logCore, boolean debug) throws ProtocolException, UtilsException{
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
			if(protocollo.equals(URLProtocolContext.PA_FUNCTION) || 
					protocollo.equals(URLProtocolContext.PD_FUNCTION) || 
					protocollo.equals(URLProtocolContext.PDtoSOAP_FUNCTION) || 
					protocollo.equals(URLProtocolContext.IntegrationManager_FUNCTION) || 
					protocollo.equals(URLProtocolContext.CheckPdD_FUNCTION)) {
				// ContextProtocol Empty
				if(logCore!=null)
					logCore.debug("SERVLET PATH EMPTY");
				function = protocollo;
				protocollo = Costanti.CONTEXT_EMPTY;
				
				int sizePrefix = (req.getContextPath() + "/" + function + "/").length();
				if(req.getRequestURI().length()>sizePrefix){
					functionParameters = req.getRequestURI().substring(sizePrefix);
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

			}
						
			if(logCore!=null)
				logCore.debug("Elaborazione finale Protocollo["+protocollo+"] Function["+function+"] FunctionParameters ["+functionParameters+"]");
			
			this.protocol = protocollo;
			this.function = function;
			this.functionParameters = functionParameters;		
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
}
