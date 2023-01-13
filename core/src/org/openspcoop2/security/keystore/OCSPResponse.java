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

package org.openspcoop2.security.keystore;

import java.io.Serializable;
import java.security.cert.X509Certificate;

import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.openspcoop2.utils.transport.http.OCSPResponseException;

/**
 * OCSPResponse
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean valid = false;
	private Throwable exception = null;
	
	public OCSPResponse(IOCSPValidator ocspValidator, X509Certificate cert) throws SecurityException {
		try {
			ocspValidator.valid(cert);
			this.valid = true;
		}
		catch(OCSPResponseException t) {
			this.valid = false;
			this.exception = t;
		}
		catch(Throwable t) { 
			// Devo rilanciare le altre eccezioni per non cacharle (es. non voglio cachare connection refused)
			throw new SecurityException(t.getMessage(),t);
		}
	}
	
	public boolean isValid() {
		return this.valid;
	}
	public Throwable getException() {
		return this.exception;
	}
}
