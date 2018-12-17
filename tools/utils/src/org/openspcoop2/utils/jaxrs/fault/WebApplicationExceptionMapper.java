/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.utils.jaxrs.fault;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;

/**	
 * WebApplicationExceptionMapper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WebApplicationExceptionMapper implements ExceptionMapper<javax.ws.rs.WebApplicationException> {

	private boolean excludeFaultBean;
	private Logger log = org.openspcoop2.utils.LoggerWrapperFactory.getLogger(WebApplicationExceptionMapper.class);
	
	@Override
	public Response toResponse(javax.ws.rs.WebApplicationException e) {
		if(e.getResponse()==null || e.getResponse().getEntity()==null || !(e.getResponse().getEntity() instanceof Problem)) {
			this.log.error(e.getMessage(),e);
			return FaultCode.RICHIESTA_NON_VALIDA.toFaultResponse(e);
		}
		else {
			if(this.excludeFaultBean) {
				ResponseBuilder responseBuilder = Response.status(e.getResponse().getStatus());
				if(e.getResponse().getHeaders()!=null) {
					MultivaluedMap<String, Object> map = e.getResponse().getHeaders();
					if(!map.isEmpty()) {
						map.keySet().stream().forEach(k -> {
							responseBuilder.header(k, map.get(k));
						});
					}
				}
				return responseBuilder.build();
			} else {
				return e.getResponse();
			}
		}
	}

	public boolean isExcludeFaultBean() {
		return this.excludeFaultBean;
	}

	public void setExcludeFaultBean(boolean excludeFaultBean) {
		this.excludeFaultBean = excludeFaultBean;
	}
}
