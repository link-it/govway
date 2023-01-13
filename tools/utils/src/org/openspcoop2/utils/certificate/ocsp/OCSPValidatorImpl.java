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

import java.security.cert.X509Certificate;

import org.openspcoop2.utils.LoggerBuffer;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.transport.http.OCSPTrustManager;
import org.openspcoop2.utils.transport.http.IOCSPValidator;
import org.openspcoop2.utils.transport.http.OCSPResponseException;

/**
 * OCSPValidatorImpl
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OCSPValidatorImpl implements IOCSPValidator {

	private LoggerBuffer log;
	private KeyStore trustStore;
	private String crlInput;
	private String configType;
	private OCSPConfig ocspConfig;
	private IOCSPResourceReader ocspResourceReader;
	private OCSPTrustManager ocspTrustManager;
	
	public OCSPValidatorImpl(LoggerBuffer log, 
			String crlInput, 
			String configType, IOCSPResourceReader ocspResourceReader) throws UtilsException {
		this(log,
			null, crlInput,
			configType, ocspResourceReader);
	}
	public OCSPValidatorImpl(LoggerBuffer log, 
			KeyStore trustStore, String crlInput, 
			String configType, IOCSPResourceReader ocspResourceReader) throws UtilsException {
		this.log = log;
		this.trustStore = trustStore;
		this.crlInput = crlInput;
		this.configType = configType;
		
		OCSPManager ocspManager = OCSPManager.getInstance();
		this.ocspConfig = ocspManager.getOCSPConfig(this.configType);
		
		this.ocspResourceReader = ocspResourceReader;
	}
	
	@Override
	public void valid(X509Certificate cert) throws OCSPResponseException, UtilsException {
		
		OCSPRequestParams params = OCSPRequestParams.build(this.log, cert, this.trustStore, this.ocspConfig, this.ocspResourceReader);
		CertificateStatus certificatePrincipalStatus = OCSPValidator.check(this.log, params, this.crlInput);
		certificatePrincipalStatus.checkValid();
		
	}
	
	@Override
	public KeyStore getTrustStore() {
		return this.trustStore;
	}
	@Override
	public void setTrustStore(KeyStore truststore) {
		this.trustStore = truststore;
	}
	
	@Override
	public OCSPTrustManager getOCSPTrustManager() {
		return this.ocspTrustManager;
	}
	@Override
	public void setOCSPTrustManager(OCSPTrustManager trustManager) {
		this.ocspTrustManager = trustManager;
	}

}
