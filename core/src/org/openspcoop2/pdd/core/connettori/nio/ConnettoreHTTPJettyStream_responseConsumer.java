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

import java.io.InputStream;

import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;

/**
 * ConnettoreHTTPJettyStream_responseConsumer
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPJettyStream_responseConsumer {
	private final Response res;
	private final InputStream content;

	public ConnettoreHTTPJettyStream_responseConsumer( Response response, InputStream content ) {
		this.res = response;
		this.content = content;
	}

	public int getResponseCode() {
		return this.res.getStatus();
	}

	public HttpFields getHeaders() {
		return this.res.getHeaders();
	}

	public long getContentLength() {
		var field = getHeaders().getField( "Content-Length" );
		if ( field == null )
			return -1L;
		return field.getLongValue();
	}

	public InputStream getContent() {
		return this.content;
	}
}
