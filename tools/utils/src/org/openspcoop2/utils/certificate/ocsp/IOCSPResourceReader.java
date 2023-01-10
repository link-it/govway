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

import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.CRLCertstore;
import org.openspcoop2.utils.certificate.KeyStore;

/**
 * IOCSPResourceReader
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IOCSPResourceReader {

	public void initConfig(OCSPConfig config) throws UtilsException;
	
	public KeyStore getIssuerAlternativeTrustStore() throws UtilsException;
	
	public void readExternalResource(String resource, Map<String, byte[]> holderResource) throws UtilsException;
	
	public CRLCertstore readCRL(List<String> crl, Map<String, byte[]> localResources) throws UtilsException;
	
	public KeyStore getSignerTrustStore() throws UtilsException;
	
	public KeyStore getHttpsTrustStore() throws UtilsException;
	
	public KeyStore getCrlAlternativeTrustStore() throws UtilsException;
	
}
