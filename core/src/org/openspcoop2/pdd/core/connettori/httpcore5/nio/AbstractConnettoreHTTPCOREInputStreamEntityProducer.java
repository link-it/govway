/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import java.util.Set;

import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;

/**
 * AbstractConnettoreHTTPCOREInputStreamEntityProducer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractConnettoreHTTPCOREInputStreamEntityProducer<T> implements AsyncEntityProducer{

	protected T object;
	protected org.apache.hc.core5.http.ContentType contentType;
	protected String contentEncoding;
	protected boolean chunked;
	protected ConnettoreLogger logger;
	
	protected AbstractConnettoreHTTPCOREInputStreamEntityProducer(T object, org.apache.hc.core5.http.ContentType contentType, String contentEncoding, ConnettoreLogger logger) throws TransactionNotExistsException {
		this.object = object;
		this.contentType = contentType;
		this.contentEncoding = contentEncoding;
		this.chunked = true;
		this.logger = logger;
	}

	@Override
	public void releaseResources() {
		/**this.is.close();*/
	}

	@Override
	public String getContentEncoding() {
		return this.contentEncoding;
	}

	@Override
	public long getContentLength() {
		 return -1; // Lunghezza sconosciuta, utilizzo del chunked transfer encoding
	}

	@Override
	public String getContentType() {
		if(this.contentType!=null)
			return this.contentType.getMimeType();
		return null;
	}

	@Override
	public Set<String> getTrailerNames() {
		Set<String> s = null;
		if(this.contentType!=null) {
			// nop
		}
		return s;
	}

	@Override
	public boolean isChunked() {
		return this.chunked;
	}

	@Override
	public void failed(Exception exc) {
		if(this.logger!=null) {
			this.logger.error(exc.getMessage(),exc);
		}
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}
	
}
