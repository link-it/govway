/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpUtilities;


/**	
 * FaultCode
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FaultCode {

	public static final FaultCode RICHIESTA_NON_VALIDA = new FaultCode(400, "RICHIESTA NON VALIDA", "Richiesta non correttamente formata");
	public static final FaultCode AUTENTICAZIONE = new FaultCode(401, "AUTENTICAZIONE", "Mittente della richiesta non autenticato"); 
	public static final FaultCode AUTORIZZAZIONE = new FaultCode(403, "AUTORIZZAZIONE", "Richiesta non permessa");
	public static final FaultCode NOT_FOUND = new FaultCode(404, "NOT_FOUND", "Risorsa non trovata");
	public static final FaultCode CONFLITTO = new FaultCode(409,"CONFLITTO","L'entità che si vuole creare risulta già esistente");
	public static final FaultCode RICHIESTA_NON_VALIDA_SEMANTICAMENTE = new FaultCode(422,"RICHIESTA NON VALIDA SEMANTICAMENTE",
			"Richiesta non processabile");
	public static final FaultCode ERRORE_INTERNO = new FaultCode(500,"ERRORE INTERNO","Errore interno");
	
	public static final FaultCode STATUS_OK = new FaultCode(200,"STATUS OK","Il servizio funziona correttamente");


	private final String name;
	private final String descrizione;
	private final int code;

	public FaultCode(int code, String name, String descrizione)
	{
		this.code = code;
		this.name = name;
		this.descrizione = descrizione;
	}

	public int getCode() {
		return this.code;
	}
	public String getName() {
		return this.name;
	}
	public String getDescrizione()
	{
		return this.descrizione;
	}

	@Override
	public String toString() {
		return this.descrizione;
	}

	public ProblemRFC7807 toFault() {
		ProblemRFC7807 problem = new ProblemRFC7807();
		problem.setType("https://httpstatuses.com/"+this.code);
		problem.setStatus(this.code);
		problem.setTitle(HttpUtilities.getHttpReason(this.code));
		problem.setDetail(this.descrizione);
		return problem;
	}
	public ProblemRFC7807 toFault(String dettaglio) {
		ProblemRFC7807 problem = this.toFault();
		problem.setDetail(dettaglio);
		return problem;
	}
	public ProblemRFC7807 toFault(Throwable e) {
		ProblemRFC7807 problem = this.toFault();
		problem.setDetail(e.getMessage());
		return problem;
	}

	public ResponseBuilder toFaultResponseBuilder() {
		return this.toFaultResponseBuilder(true);
	}
	public ResponseBuilder toFaultResponseBuilder(boolean addFault) {
		ProblemRFC7807 problem = this.toFault();
		ResponseBuilder rb = Response.status(this.code);
		if(addFault) {
			rb.entity(problem).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
		}
		return rb;
	}
	public ResponseBuilder toFaultResponseBuilder(String dettaglio) {
		ProblemRFC7807 problem = this.toFault(dettaglio);
		return Response.status(this.code).entity(problem).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
	}
	public ResponseBuilder toFaultResponseBuilder(Throwable e) {
		ProblemRFC7807 problem = this.toFault(e);
		return Response.status(this.code).entity(problem).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807);
	}
	
	public Response toFaultResponse() {
		return this.toFaultResponse(true);
	}
	public Response toFaultResponse(boolean addFault) {
		return this.toFaultResponseBuilder(addFault).build();
	}
	public Response toFaultResponse(String dettaglio) {
		return this.toFaultResponseBuilder(dettaglio).build();
	}
	public Response toFaultResponse(Throwable e) {
		return this.toFaultResponseBuilder(e).build();
	}

	public javax.ws.rs.WebApplicationException toException(ResponseBuilder responseBuilder){
		return new javax.ws.rs.WebApplicationException(responseBuilder.build());
	}
	public javax.ws.rs.WebApplicationException toException(ResponseBuilder responseBuilder, Map<String, String> headers){
		if(headers!=null && !headers.isEmpty()) {
			headers.keySet().stream().forEach(k -> {
				responseBuilder.header(k, headers.get(k));
			});
		}
		return new javax.ws.rs.WebApplicationException(responseBuilder.build());
	}
	public javax.ws.rs.WebApplicationException toException(Response response){
		return toException(response, null);
	}
	public javax.ws.rs.WebApplicationException toException(Response response, Throwable e){
		// Aggiunta eccezione nel costruttore, in modo che cxf chiami la classe WebApplicationExceptionMapper
		Exception exception = null;
		String msgException = response.getEntity().toString();
		if(e!=null) {
			exception = new Exception(msgException,e);
		}
		else {
			exception = new Exception(msgException);
		}
		return new javax.ws.rs.WebApplicationException(exception,response);
	}
	public javax.ws.rs.WebApplicationException toException(){
		return this.toException(true);
	}
	public javax.ws.rs.WebApplicationException toException(boolean addFault){
		Response r = this.toFaultResponse(addFault);
		return this.toException(r);
	}
	public javax.ws.rs.WebApplicationException toException(String dettaglio){
		Response r = this.toFaultResponse(dettaglio);
		return this.toException(r);
	}
	public javax.ws.rs.WebApplicationException toException(Throwable e){
		Response r = this.toFaultResponse(e);
		return this.toException(r,e);
	}

	public void throwException(ResponseBuilder responseBuilder) throws javax.ws.rs.WebApplicationException{
		throw this.toException(responseBuilder);
	}
	public void throwException(Response response) throws javax.ws.rs.WebApplicationException{
		throw this.toException(response);
	}
	public void throwException() throws javax.ws.rs.WebApplicationException{
		throw this.toException();
	}
	public void throwException(boolean addFault) throws javax.ws.rs.WebApplicationException{
		throw toException(addFault);
	}
	public void throwException(String dettaglio) throws javax.ws.rs.WebApplicationException{
		throw toException(dettaglio);
	}
	public void throwException(Throwable e) throws javax.ws.rs.WebApplicationException{
		throw toException(e);
	}
	

}
