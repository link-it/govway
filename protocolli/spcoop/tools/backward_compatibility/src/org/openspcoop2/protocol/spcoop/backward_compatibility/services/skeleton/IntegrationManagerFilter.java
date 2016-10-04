/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.protocol.spcoop.backward_compatibility.services.skeleton;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.core.api.constants.MethodType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.services.connector.ConnectorUtils;
import org.openspcoop2.protocol.engine.constants.IDService;

/**
 * IntegrationManagerFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrationManagerFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		
		MethodType method = MethodType.toEnumConstant(httpReq.getMethod());
		
		if(MethodType.GET.equals(method)){
			
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
							
			boolean wsdl = false;
			Enumeration<?> parameters = httpReq.getParameterNames();
			while(parameters.hasMoreElements()){
				String key = (String) parameters.nextElement();
				String value = httpReq.getParameter(key);
				if("wsdl".equalsIgnoreCase(key) && (value==null || "".equals(value) || "IntegrationManager.wsdl".equals(value) ) ){
					// richiesta del wsdl
					if(op2Properties!=null && op2Properties.isGenerazioneWsdlIntegrationManagerEnabled()==false){
						httpRes.sendError(404, ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeWsdlUnsupported(IDService.INTEGRATION_MANAGER_SOAP)));
						return;
					}
					else{
						wsdl = true;
						break;
					}
				}
			}
			
			if(!wsdl){
				// messaggio di errore
				boolean errore404 = false;
				if(op2Properties!=null && !op2Properties.isGenerazioneErroreHttpMethodUnsupportedIntegrationManagerEnabled()){
					errore404 = true;
				}
				
				if(errore404){
					httpRes.sendError(404,ConnectorUtils.generateError404Message(ConnectorUtils.getFullCodeHttpMethodNotSupported(IDService.INTEGRATION_MANAGER_SOAP, method)));
					return;
				}
				else{
				
					httpRes.setStatus(500);
					
					ConnectorUtils.generateErrorMessage(IDService.INTEGRATION_MANAGER_SOAP, method, httpReq, httpRes, ConnectorUtils.getMessageHttpMethodNotSupported(method), false, true);
							
					try{
						httpRes.getOutputStream().flush();
					}catch(Exception eClose){}
					try{
						httpRes.getOutputStream().close();
					}catch(Exception eClose){}
					
					return;
				}
			}
			
		}
		
		
		// pass the request along the filter chain
        chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
