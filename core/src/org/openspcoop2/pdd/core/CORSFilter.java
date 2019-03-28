/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.pdd.core;

import java.io.IOException;

import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.utils.transport.http.AbstractCORSFilter;
import org.openspcoop2.utils.transport.http.CORSFilterConfiguration;
import org.slf4j.Logger;

/**
 * AbstractCORSFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CORSFilter extends AbstractCORSFilter {

	private CORSFilterConfiguration config;
	private Logger log;
	
	
	@Override
	protected CORSFilterConfiguration getConfig() throws IOException {
		return this.config;
	}

	@Override
	protected Logger getLog() {
		return this.log;
	}

	public CORSFilter(Logger log, CorsConfigurazione config){
		this.log = log;
		this.config = toCORSFilterConfiguration(config);
	}
	
	private static CORSFilterConfiguration toCORSFilterConfiguration(CorsConfigurazione config) {
	
		CORSFilterConfiguration cors = new CORSFilterConfiguration();
		
		cors.setAllowCredentials(StatoFunzionalita.ABILITATO.equals(config.getAccessControlAllowCredentials()));
			
		if(config.getAccessControlAllowHeaders()!=null && config.getAccessControlAllowHeaders().sizeHeaderList()>0) {
			for (String header: config.getAccessControlAllowHeaders().getHeaderList()) {
				cors.addAllowHeader(header);
			}
		}
		
		if(config.getAccessControlAllowMethods()!=null && config.getAccessControlAllowMethods().sizeMethodList()>0) {
			for (String method: config.getAccessControlAllowMethods().getMethodList()) {
				cors.addAllowMethod(method);
			}
		}
		
		cors.setAllowAllOrigin(StatoFunzionalita.ABILITATO.equals(config.getAccessControlAllAllowOrigins()));
		if(cors.getAllowAllOrigin()==false) {
			if(config.getAccessControlAllowOrigins()!=null && config.getAccessControlAllowOrigins().sizeOriginList()>0) {
				for (String origin: config.getAccessControlAllowOrigins().getOriginList()) {
					cors.addAllowOrigin(origin);
				}
			}
		}
		
		if(config.getAccessControlExposeHeaders()!=null && config.getAccessControlExposeHeaders().sizeHeaderList()>0) {
			for (String header: config.getAccessControlExposeHeaders().getHeaderList()) {
				cors.addAllowHeader(header);
			}
		}
		
		if(config.getAccessControlMaxAge()!=null) {
			if(config.getAccessControlMaxAge()<=0) {
				cors.setCachingAccessControl_disable(true);
			}
			else {
				cors.setCachingAccessControl_maxAgeSeconds(config.getAccessControlMaxAge());
			}
		}
		
		return cors;
		
	}
}
