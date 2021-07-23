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

package org.openspcoop2.pdd.core.connettori.nio;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;

/**
 * ConnettoreHTTPCORE_connection
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE_connection extends AbstractConnettoreHTTPCORE_connection<CloseableHttpAsyncClient> {

	public ConnettoreHTTPCORE_connection(String key, CloseableHttpAsyncClient client) {
		super(key, client);
	}
	
	@Override
	public String getStatus() {
		return this.httpclient.isRunning()? "running" : "stopped";
	}

	@Override
	public void close() throws ConnettoreException {
		try {
			this.httpclient.close();
		}catch(Throwable e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
	}

}
