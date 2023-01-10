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
package org.openspcoop2.utils.certificate.ocsp;

import org.bouncycastle.cert.ocsp.OCSPResp;

/**
 * OCSPResponseCode
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum OCSPResponseCode {
	
	SUCCESSFUL(OCSPResp.SUCCESSFUL), // 0

	// https://www.rfc-editor.org/rfc/rfc6960#section-2.3

	// A server produces the "malformedRequest" response if the request
	//received does not conform to the OCSP syntax.
	MALFORMED_REQUEST(OCSPResp.MALFORMED_REQUEST),  // 1
	
	// The response "internalError" indicates that the OCSP responder
	//  reached an inconsistent internal state.  The query should be retried,
	//   potentially with another responder.
	INTERNAL_ERROR(OCSPResp.INTERNAL_ERROR),  // 2
	
	// In the event that the OCSP responder is operational but unable to
	//   return a status for the requested certificate, the "tryLater"
	//   response can be used to indicate that the service exists but is
	//   temporarily unable to respond.
	TRY_LATER(OCSPResp.TRY_LATER),  // 3
	
	// The response "sigRequired" is returned in cases where the server
	//   requires that the client sign the request in order to construct a
	//   response.
	SIG_REQUIRED(OCSPResp.SIG_REQUIRED),  // 5
	
	// The response "unauthorized" is returned in cases where the client is
	//   not authorized to make this query to this server or the server is not
	//   capable of responding authoritatively
	UNAUTHORIZED(OCSPResp.UNAUTHORIZED),  // 6
	
	// Unknown
	UNKNOWN(-1);
	
	private int code;
	
	OCSPResponseCode(int code)
	{
		this.code = code;
	}

	public int getCode()
	{
		return this.code;
	}
	
	public String getMessage() {
		switch (this) {
        case SUCCESSFUL:
           return "Successful";
        case INTERNAL_ERROR:
        	return "Internal error";
        case TRY_LATER:
        	return "Internal error, try later.";
        case SIG_REQUIRED:
        	return "Invalid or missing signature";
        case UNAUTHORIZED:
            return "Unauthorized request";
        case MALFORMED_REQUEST:
        	return "Malformed request";
        default:
        	return "Error";
		}
	}
	
	@Override
	public String toString(){
		return this.name();
	}
	
	public boolean equals(int code){
		return this.code == code;
	}
	
	public static final OCSPResponseCode toOCSPResponseCode(int code){
		if(code == SUCCESSFUL.code) {
			return OCSPResponseCode.SUCCESSFUL;
		}
		else if(code == MALFORMED_REQUEST.code) {
			return OCSPResponseCode.MALFORMED_REQUEST;
		}
		else if(code == INTERNAL_ERROR.code) {
			return OCSPResponseCode.INTERNAL_ERROR;
		}
		else if(code == TRY_LATER.code) {
			return OCSPResponseCode.TRY_LATER;
		}
		else if(code == SIG_REQUIRED.code) {
			return OCSPResponseCode.SIG_REQUIRED;
		}
		else if(code == UNAUTHORIZED.code) {
			return OCSPResponseCode.UNAUTHORIZED;
		}
		else if(code == UNKNOWN.code) {
			return OCSPResponseCode.UNKNOWN;
		}
		
		return OCSPResponseCode.UNKNOWN;
	}
}
