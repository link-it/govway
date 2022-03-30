/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
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
	private boolean updateTitle = true;
	private ITypeGenerator typeGenerator;
	
	public boolean isExcludeFaultBean() {
		return this.excludeFaultBean;
	}
	public void setExcludeFaultBean(boolean excludeFaultBean) {
		this.excludeFaultBean = excludeFaultBean;
	}
	
	public boolean isUpdateTitle() {
		return this.updateTitle;
	}
	public void setUpdateTitle(boolean updateTitle) {
		this.updateTitle = updateTitle;
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
            else if(e.getCause() instanceof JsonParseException) {
            	JsonParseException jsonParseException = (JsonParseException) e.getCause();
            	problem = new ProblemValidation(FaultCode.RICHIESTA_NON_VALIDA.toFault());
            	if(jsonParseException.getOriginalMessage()!=null) {
            		((ProblemValidation) problem).addInvalidParam("body", jsonParseException.getOriginalMessage(), null);
            	}
            	else {
            		((ProblemValidation) problem).addInvalidParam("body", jsonParseException.getMessage(), null);
            	}
            	if(jsonParseException.getLocation()!=null) {
            		StringBuilder bf = new StringBuilder();
            		bf.append("line: ").append(jsonParseException.getLocation().getLineNr()).append(", column: ").append(jsonParseException.getLocation().getColumnNr());
            		((ProblemValidation) problem).addInvalidParam("position", bf.toString(), null);
            	}
            }
            else {
            	problem = FaultCode.RICHIESTA_NON_VALIDA.toFault();
            }
            
            this.updateProblem(problem, e); // customizzabile
            this._setType(problem, e);
            if(this.updateTitle) {
            	// aggiorno rispetto al code modificabile nel metodo sopra
            	problem.setTitle(HttpUtilities.getHttpReason(problem.getStatus()));
            }
            return Response.status(problem.getStatus()).entity(problem).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build();

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
				
				Problem problem = this._getProblem(e);
				if(problem!=null) {
		            this.updateProblem(problem, e); // customizzabile
		            this._setType(problem, e);
		            if(this.updateTitle) {
		            	// aggiorno rispetto al code modificabile nel metodo sopra
		            	problem.setTitle(HttpUtilities.getHttpReason(problem.getStatus()));
		            }
				}
				
				return e.getResponse();
			}
		}
	}
	
	private Problem _getProblem(javax.ws.rs.WebApplicationException e) {
		if(e==null || e.getResponse()==null || e.getResponse().getEntity()==null) {
			return null;
		}
		if(!(e.getResponse().getEntity() instanceof Problem)) {
			return null;
		}
		Problem problem = (Problem) e.getResponse().getEntity();
		return problem;
	}
	private void _setType(Problem problem, javax.ws.rs.WebApplicationException e) {
		if(this.typeGenerator==null) {
			return;
		}
		problem.setType(this.typeGenerator.getType(problem.getStatus(), e));
	}
	
	
	public void updateProblem(Problem problem, javax.ws.rs.WebApplicationException e) {
		
	}

}
