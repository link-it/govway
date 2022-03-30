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
package org.openspcoop2.pdd.core.connettori.nio;

import javax.net.ssl.SSLContext;

import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.security.keystore.cache.GestoreKeystoreCache;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.SSLConfig;
import org.openspcoop2.utils.transport.http.SSLUtilities;

/**
 * TlsContextBuilder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TlsContextBuilder {

	public static SSLContext buildSSLContext(SSLConfig httpsProperties, ConnettoreLogger logger, StringBuilder sb) throws UtilsException {
		// provo a leggere i keystore dalla cache
		if(httpsProperties.getKeyStore()==null) {
			if(httpsProperties.getKeyStoreLocation()!=null) {
				try {
					httpsProperties.setKeyStore(GestoreKeystoreCache.getMerlinKeystore(httpsProperties.getKeyStoreLocation(), 
							httpsProperties.getKeyStoreType(), httpsProperties.getKeyStorePassword()).getKeyStore().getKeystore());
				}catch(Exception e) {
					String msgError = "Lettura keystore '"+httpsProperties.getKeyStoreLocation()+"' dalla cache fallita: "+e.getMessage();
					logger.error(msgError, e);
				}
			}
		}
		if(httpsProperties.getTrustStore()==null) {
			if(httpsProperties.getTrustStoreLocation()!=null) {
				try {
					httpsProperties.setTrustStore(GestoreKeystoreCache.getMerlinTruststore(httpsProperties.getTrustStoreLocation(), 
							httpsProperties.getTrustStoreType(), httpsProperties.getTrustStorePassword()).getTrustStore().getKeystore());
				}catch(Exception e) {
					String msgError = "Lettura truststore '"+httpsProperties.getTrustStoreLocation()+"' dalla cache fallita: "+e.getMessage();
					logger.error(msgError, e);
				}
			}
		}
		if(httpsProperties.getTrustStoreCRLs()==null) {
			if(httpsProperties.getTrustStoreCRLsLocation()!=null) {
				try {
					httpsProperties.setTrustStoreCRLs(GestoreKeystoreCache.getCRLCertstore(httpsProperties.getTrustStoreCRLsLocation()).getCertStore());
				}catch(Exception e) {
					String msgError = "Lettura CRLs '"+httpsProperties.getTrustStoreLocation()+"' dalla cache fallita: "+e.getMessage();
					logger.error(msgError, e);
				}
			}
		}
		
		return SSLUtilities.generateSSLContext(httpsProperties, sb);
	}
	
}
