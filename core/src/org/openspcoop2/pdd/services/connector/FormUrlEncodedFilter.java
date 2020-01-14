/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;


/**
 * FormUrlEncodedFilter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FormUrlEncodedFilter implements Filter {

	/*
	 * NOTA: da Wilfdly 8.1 questo filtro deve essere abilitato nella configurazione:
	 * In the standalone/configuration/standalone.xml file change the servlet-container XML element so that it has the attribute allow-non-standard-wrappers="true".
	 * 
	 *  <servlet-container name="default" allow-non-standard-wrappers="true">
     *           ...
     *  </servlet-container>
	 **/
	
	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		boolean doFilter = false;
		try {
			OpenSPCoop2Properties op2PropertieS = OpenSPCoop2Properties.getInstance();
			doFilter = op2PropertieS.isFormUrlEncodedFilterEnabled();
		}catch(Throwable t) {}
		
		
		if(doFilter && req instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) req;
			if(FormUrlEncodedHttpServletRequest.isFormUrlEncodedRequest(httpServletRequest)) {
				req = FormUrlEncodedHttpServletRequest.convert(httpServletRequest);
			}
		}
		
		chain.doFilter(req, res);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
	}

}