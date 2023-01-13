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

package org.openspcoop2.utils.transport.http;

import javax.net.ssl.X509TrustManager;

/**
 * SSLTrustAllManager: Create a trust manager that does not validate certificate chains
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SSLTrustAllManager implements X509TrustManager {

	@Override
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return null;
    }
    @Override
	public void checkClientTrusted(
        java.security.cert.X509Certificate[] certs, String authType) {
    }
    @Override
	public void checkServerTrusted(
        java.security.cert.X509Certificate[] certs, String authType) {
    }
    
}
