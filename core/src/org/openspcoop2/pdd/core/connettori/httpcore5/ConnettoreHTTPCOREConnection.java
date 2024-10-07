/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.connettori.httpcore5;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.openspcoop2.pdd.core.connettori.AbstractConnettoreConnection;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;

/**
 * ConnettoreHTTPCOREConnection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCOREConnection extends AbstractConnettoreConnection<CloseableHttpClient>{

	private RequestConfig requestConfig;
	
	public ConnettoreHTTPCOREConnection(String key, CloseableHttpClient client, RequestConfig requestConfig,
			int expireUnusedAfterSeconds, int closeUnusedAfterSeconds) {
		super(key, client,
				expireUnusedAfterSeconds, closeUnusedAfterSeconds);
		this.requestConfig = requestConfig;
	}

	@Override
	public String getStatus() {
		/**return this.httpclient.getStatus().toString();*/
		return "";
	}

	@Override
	public void close() throws ConnettoreException {
		try {
			this.httpclient.close(org.apache.hc.core5.io.CloseMode.GRACEFUL); // tenta di chiudere le connessioni in modo ordinato, permettendo alle richieste in corso di completarsi.
		}catch(Exception e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
	}

	public RequestConfig getRequestConfig() {
		return this.requestConfig;
	}
}
