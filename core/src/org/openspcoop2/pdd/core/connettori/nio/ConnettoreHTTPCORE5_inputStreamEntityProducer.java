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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Set;

import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.openspcoop2.utils.Utilities;

/**
 * ConnettoreHTTPCORE5_inputStreamEntityProducer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE5_inputStreamEntityProducer implements AsyncEntityProducer{

	private InputStream is;
	private org.apache.hc.core5.http.ContentType contentType;
	private String contentEncoding;
	private boolean chunked;
	private long count = 0;
	
	public ConnettoreHTTPCORE5_inputStreamEntityProducer(InputStream is, org.apache.hc.core5.http.ContentType contentType, String contentEncoding,
			boolean chunked) {
		this.is = is;
		this.contentType = contentType;
		this.contentEncoding = contentEncoding;
		this.chunked = chunked;
	}
	
	@Override
	public int available() {
		try {
			return this.is.available();
		}catch(Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void produce(DataStreamChannel channel) throws IOException {
		byte [] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
		int letti = 0;
		while( (letti=this.is.read(buffer)) != -1 ){
			if ( letti != 0 ) {
				int startIx = 0;
				while ( startIx < letti ) {
					ByteBuffer bb = ByteBuffer.wrap(buffer, startIx, letti - startIx);
					int writtenBytes = channel.write(bb);
					if ( writtenBytes == 0 )
						channel.requestOutput();
					else
						startIx += writtenBytes;
				}
				this.count = this.count+letti;
			}
		}
		//System.out.println("======== writeBytes (RICHIESTA) (scritti: "+this.count+") ("+Utilities.convertBytesToFormatString(this.count)+")");
		channel.endStream();
	}

	@Override
	public void releaseResources() {
		//this.is.close();
	}

	@Override
	public String getContentEncoding() {
		return this.contentEncoding;
	}

	@Override
	public long getContentLength() {
		return 0;
	}

	@Override
	public String getContentType() {
		if(this.contentType!=null)
			return this.contentType.getMimeType();
		return null;
	}

	@Override
	public Set<String> getTrailerNames() {
		return null;
	}

	@Override
	public boolean isChunked() {
		return this.chunked;
	}

	@Override
	public void failed(Exception exc) {
		//
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}
	
}
