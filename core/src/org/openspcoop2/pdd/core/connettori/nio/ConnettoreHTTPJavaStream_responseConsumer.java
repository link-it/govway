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

import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;

/**
 * ConnettoreHTTPJava_inputStreamEntityConsumer
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPJavaStream_responseConsumer {
	private final HttpResponse< InputStream > res;

	public ConnettoreHTTPJavaStream_responseConsumer( HttpResponse<InputStream> response ) {
		this.res = response;
	}

	public int getResponseCode() {
		return this.res.statusCode();
	}

	public HttpHeaders getHeaders() {
		return this.res.headers();
	}

	public long getContentLength() {
		return this.res.headers().firstValueAsLong( "Content-Length" ).orElse( -1L );
	}

	public InputStream getContent() {
		return this.res.body();
	}
}
