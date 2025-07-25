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

package org.openspcoop2.pdd.core.connettori.httpcore5.nio;

import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;

/**
 * ConnettoreHTTPCOREResponse
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCOREResponse {

	private HttpEntity entity;
	private HttpResponse httpResponse;
	private long count = 0;

	public ConnettoreHTTPCOREResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}

	public HttpEntity getEntity() {
		return this.entity;
	}

	public void setEntity(final HttpEntity entity) {
		this.entity = entity;
	}

	public HttpResponse getHttpResponse() {
		return this.httpResponse;
	}

	public void setHttpResponse(HttpResponse httpResponse) {
		this.httpResponse = httpResponse;
	}
	
	public long getCount() {
		return this.count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
