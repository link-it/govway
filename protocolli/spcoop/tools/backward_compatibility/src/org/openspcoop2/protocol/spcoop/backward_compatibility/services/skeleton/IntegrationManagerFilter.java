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

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.services.connector.ConnectorCostanti;
import org.openspcoop2.pdd.services.connector.ConnectorUtils;

public class IntegrationManagerFilter implements Filter {

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpRes = (HttpServletResponse) response;
		
		if("GET".equalsIgnoreCase(httpReq.getMethod())){
			
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
							
			boolean wsdl = false;
			Enumeration<?> parameters = httpReq.getParameterNames();
			while(parameters.hasMoreElements()){
				String key = (String) parameters.nextElement();
				String value = httpReq.getParameter(key);
				if("wsdl".equalsIgnoreCase(key) && (value==null || "".equals(value)) ){
					// richiesta del wsdl
					if(op2Properties!=null && op2Properties.isGenerazioneWsdlIntegrationManagerEnabled()==false){
						httpRes.sendError(404, ConnectorUtils.generateError404Message(ConnectorCostanti.INTEGRATION_MANAGER_WSDL));
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
				if(op2Properties!=null && !op2Properties.isGenerazioneErroreHttpGetIntegrationManagerEnabled()){
					errore404 = true;
				}
				
				if(errore404){
					httpRes.sendError(404,ConnectorUtils.generateError404Message(ConnectorCostanti.INTEGRATION_MANAGER_HTTP_GET));
					return;
				}
				else{
				
					httpRes.setStatus(500);
					
					httpRes.getOutputStream().write(ConnectorUtils.generateErrorMessage(httpReq, ConnectorCostanti.METHOD_HTTP_GET_NOT_SUPPORTED, false, true).getBytes());
							
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
