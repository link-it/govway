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
package org.openspcoop2.security.keystore.cache;

import java.security.cert.X509Certificate;

import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.security.keystore.OCSPResponse;
import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.ocsp.IOCSPResourceReader;
import org.openspcoop2.utils.certificate.ocsp.OCSPValidatorImpl;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.openspcoop2.utils.transport.http.OCSPResponseException;
import org.openspcoop2.utils.transport.http.OCSPTrustManager;

/**
 * GestoreOCSPValidator
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreOCSPValidator implements IOCSPValidator {

	private OCSPValidatorImpl validatorImpl;
	private RequestInfo requestInfo;
	
	public GestoreOCSPValidator(RequestInfo requestInfo, LoggerBuffer log, 
			String crlInput, 
			String configType, IOCSPResourceReader ocspResourceReader) throws UtilsException {
		this.validatorImpl = new OCSPValidatorImpl(log, crlInput, configType, ocspResourceReader);
		this.requestInfo = requestInfo;
	}
	public GestoreOCSPValidator(RequestInfo requestInfo, LoggerBuffer log, 
			KeyStore trustStore, String crlInput, 
			String configType, IOCSPResourceReader ocspResourceReader) throws UtilsException {
		this.validatorImpl = new OCSPValidatorImpl(log, trustStore, crlInput, configType, ocspResourceReader);
		this.requestInfo = requestInfo;
	}
	
	@Override
	public void valid(X509Certificate cert) throws OCSPResponseException, UtilsException {
		OCSPResponse response = null;
		try {
			response = GestoreKeystoreCache.getOCSPResponse(this.requestInfo, this.validatorImpl, cert);
			if(response==null) {
				throw new Exception("OCSPResponse unavailable");
			}
		}catch(Throwable t) {
			throw new UtilsException(t.getMessage(),t);
		}
		if(!response.isValid()) {
			if(response.getException()==null) {
				throw new UtilsException("Invalid Certificate");
			}
			else {
				if(response.getException() instanceof OCSPResponseException) {
					throw (OCSPResponseException) response.getException();
				}
				else if(response.getException() instanceof UtilsException) {
					throw (UtilsException) response.getException();
				}
				else {
					throw new UtilsException(response.getException().getMessage(),response.getException());
				}
			}
		}
	}
	@Override
	public KeyStore getTrustStore() {
		return this.validatorImpl.getTrustStore();
	}
	@Override
	public void setTrustStore(KeyStore keystore) {
		this.validatorImpl.setTrustStore(keystore);
	}
	@Override
	public OCSPTrustManager getOCSPTrustManager() {
		return this.validatorImpl.getOCSPTrustManager();
	}
	@Override
	public void setOCSPTrustManager(OCSPTrustManager trustManager) {
		this.validatorImpl.setOCSPTrustManager(trustManager);
	}
}
