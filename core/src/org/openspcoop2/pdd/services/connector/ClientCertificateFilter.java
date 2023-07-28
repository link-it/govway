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

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.transport.http.HttpServletCredential;
import org.openspcoop2.utils.transport.http.SSLUtilities;
import org.slf4j.Logger;


/**
 * FormUrlEncodedFilter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ClientCertificateFilter implements Filter {

	/*
	 * NOTA: da Wilfdly 25 disabilitando l'application-security-domain name="other" non viene più popolato l'attributo dei certificati client
	 * 
	 **/
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		boolean doFilter = false;
		Logger log = null;
		try {
			OpenSPCoop2Properties op2PropertieS = OpenSPCoop2Properties.getInstance();
			doFilter = op2PropertieS.isWildflyUndertowClientCertificateFilterEnabled();
			log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		}catch(Throwable t) {}
		
		
		if(doFilter && req instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) req;
			Object o = req.getAttribute(HttpServletCredential.SERVLET_REQUEST_X509CERTIFICATE);
			if(o==null) {
				java.security.cert.X509Certificate[] certs = SSLUtilities.readCertificatesFromUndertowServlet(httpServletRequest, log);
				if(certs!=null && certs.length>0) {
					req.setAttribute(HttpServletCredential.SERVLET_REQUEST_X509CERTIFICATE, certs);
				}
			}
		}
		
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

}