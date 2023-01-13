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

package org.openspcoop2.utils.certificate.ocsp;

import java.io.Serializable;

/**
 * CertificateStatusCode
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum CertificateStatusCode implements Serializable{
	
	GOOD, 
	
	// Altri casi OK:
	SELF_SIGNED, ISSUER_NOT_FOUND, OCSP_RESPONDER_NOT_FOUND, CRL_NOT_FOUND,
	
	// Casi errore:
	REVOKED, EXPIRED, UNKNOWN;
	
	public boolean isValid() {
		return !isInvalid();
	}
	
	public boolean isInvalid() {
		return this.equals(REVOKED) || this.equals(EXPIRED) || this.equals(UNKNOWN);
	}
}
