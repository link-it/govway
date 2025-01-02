/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk.state;

import jakarta.servlet.http.HttpServletRequest;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.slf4j.Logger;

/**
 * URL Protocol Context
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public abstract class URLProtocolContext extends HttpServletTransportRequestContext implements java.io.Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String PA_FUNCTION = "PA";
	public static final String PA_FUNCTION_GOVWAY = "in";
	public static final String PD_FUNCTION = "PD";
	public static final String PD_FUNCTION_GOVWAY = "out";
	public static final String PD_TO_SOAP_FUNCTION = "PDtoSOAP";
	public static final String PD_TO_SOAP_FUNCTION_GOVWAY = "out/xml2soap";
	public static final String INTEGRATION_MANAGER_FUNCTION = "IntegrationManager";
	public static final String INTEGRATION_MANAGER_ENGINE = "IntegrationManagerEngine";
	public static final String INTEGRATION_MANAGER_SERVICE_PD = "PD";
	public static final String INTEGRATION_MANAGER_SERVICE_PD_GOVWAY = "out";
	public static final String INTEGRATION_MANAGER_SERVICE_MESSAGE_BOX = "MessageBox";
	public static final String INTEGRATION_MANAGER_FUNCTION_PD = INTEGRATION_MANAGER_FUNCTION+"/"+INTEGRATION_MANAGER_SERVICE_PD;
	public static final String INTEGRATION_MANAGER_FUNCTION_MESSAGE_BOX = INTEGRATION_MANAGER_FUNCTION+"/"+INTEGRATION_MANAGER_SERVICE_MESSAGE_BOX;
	public static final String INTEGRATION_MANAGER_ENGINE_FUNCTION_PD = INTEGRATION_MANAGER_ENGINE+"/"+INTEGRATION_MANAGER_SERVICE_PD;
	public static final String INTEGRATION_MANAGER_ENGINE_FUNCTION_MESSAGE_BOX = INTEGRATION_MANAGER_ENGINE+"/"+INTEGRATION_MANAGER_SERVICE_MESSAGE_BOX;
	public static final String CHECK_FUNCTION = "check";
	public static final String PROXY_FUNCTION = "proxy";
	
	protected IDService idServiceCustom;
	
	public IDService getIdServiceCustom() {
		return this.idServiceCustom;
	}
	
	public boolean isPortaApplicativaService() {
		if(this.idServiceCustom!=null) {
			return IDService.PORTA_APPLICATIVA.equals(this.idServiceCustom) ||
					IDService.PORTA_APPLICATIVA_BIO.equals(this.idServiceCustom) ||
					IDService.PORTA_APPLICATIVA_NIO.equals(this.idServiceCustom);
		}
		else {
			return URLProtocolContext.PA_FUNCTION.equals(this.function);
		}
	}
	public boolean isPortaDelegataService() {
		if(this.idServiceCustom!=null) {
			return IDService.PORTA_DELEGATA.equals(this.idServiceCustom) || 
					IDService.PORTA_DELEGATA_BIO.equals(this.idServiceCustom) || 
					IDService.PORTA_DELEGATA_NIO.equals(this.idServiceCustom) || 
					IDService.PORTA_DELEGATA_INTEGRATION_MANAGER.equals(this.idServiceCustom) || 
					IDService.PORTA_DELEGATA_XML_TO_SOAP.equals(this.idServiceCustom) ||
					IDService.PORTA_DELEGATA_XML_TO_SOAP_BIO.equals(this.idServiceCustom) ||
					IDService.PORTA_DELEGATA_XML_TO_SOAP_NIO.equals(this.idServiceCustom);
		}
		else {
			return URLProtocolContext.PD_FUNCTION.equals(this.function);
		}
	}
	
	protected URLProtocolContext(Logger logCore) throws UtilsException{
		super(logCore);
	}
	protected URLProtocolContext(HttpServletRequest req,Logger logCore, boolean debug, FunctionContextsCustom customContexts) throws ProtocolException, UtilsException{
		this(req, logCore, debug, false, customContexts);
	}
	protected URLProtocolContext(HttpServletRequest req,Logger logCore, boolean debug, boolean integrationManagerEngine, FunctionContextsCustom customContexts) throws ProtocolException, UtilsException{
		super(req, logCore, debug);
		init(req, logCore, debug, integrationManagerEngine, customContexts);
	}
	
	protected abstract void init(HttpServletRequest req,Logger logCore, boolean debug, boolean integrationManagerEngine, FunctionContextsCustom customContexts) throws ProtocolException, UtilsException;
}
