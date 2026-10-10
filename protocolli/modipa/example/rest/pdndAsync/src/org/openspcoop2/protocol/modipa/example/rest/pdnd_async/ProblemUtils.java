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

package org.openspcoop2.protocol.modipa.example.rest.pdnd_async;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Costruzione delle risposte di errore (RFC 7807)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProblemUtils {
	
	private ProblemUtils() {}

	static final ObjectMapper MAPPER = new ObjectMapper();
	
	public static WebApplicationException newProblem(int status, String title, String detail) {
		ObjectNode problem = MAPPER.createObjectNode();
		problem.put("type", "https://govway.org/handling-errors/"+status);
		problem.put("title", title);
		problem.put("status", status);
		problem.put("detail", detail);
		Response response = Response.status(status).entity(problem.toString()).type("application/problem+json").build();
		return new WebApplicationException(detail, response);
	}
}
