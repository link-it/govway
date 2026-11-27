/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**	
 * ValidationExceptionMapper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {

	private Logger log = org.openspcoop2.utils.LoggerWrapperFactory.getLogger(ValidationExceptionMapper.class);
	
	private boolean updateTitle = true;
	private ITypeGenerator typeGenerator;
	
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
	public Response toResponse(ValidationException exception) {
		
		this.log.error(exception.getMessage(),exception);
		
		ProblemValidation problem = new ProblemValidation(FaultCode.RICHIESTA_NON_VALIDA.toFault());
		if (exception instanceof ConstraintViolationException) {
			final ConstraintViolationException constraint = (ConstraintViolationException) exception;

			// Le violazioni arrivano in un Set senza ordine garantito: vengono ordinate per parametro e messaggio
			// in modo che la stessa richiesta produca sempre la stessa risposta.
			List<ConstraintViolation< ? >> violations = new ArrayList<>();
			if(constraint.getConstraintViolations()!=null) {
				violations.addAll(constraint.getConstraintViolations());
			}
			violations.sort(Comparator.comparing((ConstraintViolation< ? > v) -> String.valueOf(v.getPropertyPath()))
					.thenComparing(v -> String.valueOf(v.getMessage())));
			for (final ConstraintViolation< ? > violation: violations) {
				problem.addInvalidParam(violation.getPropertyPath().toString(), violation.getMessage(), violation.getInvalidValue());
			}
		} 
        this.updateProblem(problem, exception); // customizzabile
        this._setType(problem, exception);
        if(this.updateTitle) {
        	// aggiorno rispetto al code modificabile nel metodo sopra
        	problem.setTitle(HttpUtilities.getHttpReason(problem.getStatus()));
        }
		return Response.status(problem.getStatus()).entity(problem).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build();
	}
	
	public void _setType(ProblemRFC7807 problem, ValidationException e) {
		if(this.typeGenerator==null) {
			return;
		}
		problem.setType(this.typeGenerator.getType(problem.getStatus(), e));
	}
	
	
	public void updateProblem(ProblemRFC7807 problem, ValidationException e) {
		
	}
}

