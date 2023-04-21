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
package org.openspcoop2.utils.certificate;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;

/**
 * CertificateFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CertificateFactory {
	
	private CertificateFactory() {}

	private static boolean useBouncyCastleProvider = false;
	public static boolean isUseBouncyCastleProvider() {
		return useBouncyCastleProvider;
	}
	public static void setUseBouncyCastleProvider(boolean useBouncyCastleProvider) {
		CertificateFactory.useBouncyCastleProvider = useBouncyCastleProvider;
	}
	
	private static Provider provider = null;
	private static synchronized Provider getProviderEngine() {
		if ( provider == null )
			provider = Security.getProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
		return provider;
	}
	private static Provider getProvider() {
		Provider p = null;
		if(!useBouncyCastleProvider) {
			return p;
		}
		if ( provider == null )
			return getProviderEngine();
		return provider;
	}
	
	public static java.security.cert.CertificateFactory getCertificateFactory() throws CertificateException {
		java.security.cert.CertificateFactory certificateFactory = null;
		if(useBouncyCastleProvider) {
			certificateFactory = java.security.cert.CertificateFactory.getInstance("X.509", getProvider());
		}
		else {
			certificateFactory = java.security.cert.CertificateFactory.getInstance("X.509");
		}
		return certificateFactory;
	}
	
	private static String certPathDefaultType = null;
	private static synchronized String getCertPathDefaultTypeEngine() {
		if ( certPathDefaultType == null )
			certPathDefaultType = CertPathValidator.getDefaultType();
		return certPathDefaultType;
	}
	private static String getCertPathDefaultType() {
		if ( certPathDefaultType == null )
			return getCertPathDefaultTypeEngine();
		return certPathDefaultType;
	}
	
	public static CertPathValidator getCertPathValidator() throws NoSuchAlgorithmException {
		CertPathValidator certPathValidator = null;
		if(useBouncyCastleProvider) {
			certPathValidator = CertPathValidator.getInstance(getCertPathDefaultType(), getProvider());
		}
		else {
			certPathValidator = CertPathValidator.getInstance(getCertPathDefaultType());
		}
		return certPathValidator;
	}
}
