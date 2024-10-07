/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hc.core5.http.io.entity.AbstractHttpEntity;
import org.openspcoop2.message.OpenSPCoop2Message;

/**
 * MessageObjectEntity
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageObjectEntity extends AbstractHttpEntity {

	private OpenSPCoop2Message msg;
	private boolean consume;
	private boolean chunked;
	
	public MessageObjectEntity(OpenSPCoop2Message msg, boolean consume, org.apache.hc.core5.http.ContentType ct, String contentEncoding) {
		super(ct, contentEncoding, 
				true); // chunked
		this.msg = msg;
		this.consume = consume;
		this.chunked = true;
	}

	@Override
	public InputStream getContent() throws IOException, UnsupportedOperationException {
		throw new UnsupportedOperationException("getContent() non è supportato.");
	}

	@Override
	public boolean isStreaming() {
		return this.chunked;
	}

	@Override
	public long getContentLength() {
        return -1; // Lunghezza sconosciuta, utilizzo del chunked transfer encoding
	}

	@Override
	public void close() throws IOException {
		// nop
	}
	
	@Override
	public void writeTo(OutputStream outStream) throws IOException {
		try {
			this.msg.writeTo(outStream, this.consume);
		}catch(Exception e) {
			throw new IOException(e.getMessage(), e);
		}
	}
}
