/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.security.cert.X509Certificate;

/**
 * Classe che contiene la risposta http
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpResponse extends AbstractHttp {

	private int resultHTTPOperation;
	
	private X509Certificate[] serverCertificate;
	
	public X509Certificate[] getServerCertificate() {
		return this.serverCertificate;
	}
	public void setServerCertificate(X509Certificate[] serverCertificate) {
		this.serverCertificate = serverCertificate;
	}
	public int getResultHTTPOperation() {
		return this.resultHTTPOperation;
	}
	public void setResultHTTPOperation(int resultHTTPOperation) {
		this.resultHTTPOperation = resultHTTPOperation;
	}
	
}
