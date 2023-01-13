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

import org.openspcoop2.utils.transport.http.OCSPResponseException;

/**
 * OCSPResponseStatusException
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPResponseStatusException extends OCSPResponseException {

	private CertificateStatus certificateStatus;
	
	public CertificateStatus getCertificateStatus() {
		return this.certificateStatus;
	}

	public OCSPResponseStatusException(CertificateStatus certificateStatus) {
		super();
		this.certificateStatus = certificateStatus;
	}

	public OCSPResponseStatusException(CertificateStatus certificateStatus, String message, Throwable cause) {
		super(message, cause);
		this.certificateStatus = certificateStatus;
	}

	public OCSPResponseStatusException(CertificateStatus certificateStatus, String msg) {
		super(msg);
		this.certificateStatus = certificateStatus;
	}

	public OCSPResponseStatusException(CertificateStatus certificateStatus, Throwable cause) {
		super(cause);
		this.certificateStatus = certificateStatus;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
