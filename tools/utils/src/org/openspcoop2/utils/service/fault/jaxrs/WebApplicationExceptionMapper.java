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

package org.openspcoop2.utils.service.fault.jaxrs;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;

import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonMappingException;

/**	
 * WebApplicationExceptionMapper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WebApplicationExceptionMapper implements ExceptionMapper<javax.ws.rs.WebApplicationException> {

	private Logger log = org.openspcoop2.utils.LoggerWrapperFactory.getLogger(WebApplicationExceptionMapper.class);
	
	private boolean excludeFaultBean;
	private ITypeGenerator typeGenerator;
	
	public boolean isExcludeFaultBean() {
		return this.excludeFaultBean;
	}
	public void setExcludeFaultBean(boolean excludeFaultBean) {
		this.excludeFaultBean = excludeFaultBean;
	}
	
	public ITypeGenerator getTypeGenerator() {
		return this.typeGenerator;
	}
	public void setTypeGenerator(ITypeGenerator typeGenerator) {
		this.typeGenerator = typeGenerator;
	}
	
	@Override
	public Response toResponse(javax.ws.rs.WebApplicationException e) {
		if(e.getResponse()==null || e.getResponse().getEntity()==null || !(e.getResponse().getEntity() instanceof Problem)) {
			
			this.log.error(e.getMessage(),e);
			
			Problem problem = null;
			
            if(e.getCause() instanceof JsonMappingException) {
            	JsonMappingException jsonMappingException = (JsonMappingException) e.getCause();
            	problem = new ProblemValidation(FaultCode.RICHIESTA_NON_VALIDA.toFault());
            	((ProblemValidation) problem).addInvalidParam(jsonMappingException.getPathReference(), jsonMappingException.getMessage(), null);
            } 
            else {
            	problem = this.toProblem(e);
            }
            
            if(problem!=null) {
            	this.updateProblem(problem, e);
            	return Response.status(problem.getStatus()).entity(problem).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build();
            }
            else {
            	return FaultCode.RICHIESTA_NON_VALIDA.toFaultResponse(e);
            }
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
				
				this.updateProblem(e);
				
				return e.getResponse();
			}
		}
	}
	
	public Problem toProblem(javax.ws.rs.WebApplicationException e) {
		return null;
	}
	
	
	
	private void updateProblem(javax.ws.rs.WebApplicationException e) {
		if(this.typeGenerator==null) {
			return;
		}
		if(e==null || e.getResponse()==null || e.getResponse().getEntity()==null) {
			return;
		}
		if(!(e.getResponse().getEntity() instanceof Problem)) {
			return;
		}
		Problem problem = (Problem) e.getResponse().getEntity();
		this.updateProblem(problem, e);
	}
	public void updateProblem(Problem problem, javax.ws.rs.WebApplicationException e) {
		if(this.typeGenerator==null) {
			return;
		}
		problem.setType(this.typeGenerator.getType(problem.getStatus(), e));
	}

}
