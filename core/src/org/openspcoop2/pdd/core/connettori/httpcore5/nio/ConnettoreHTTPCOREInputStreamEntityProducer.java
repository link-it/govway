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
package org.openspcoop2.pdd.core.connettori.httpcore5.nio;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.pdd.core.transazioni.TransactionNotExistsException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsRuntimeException;

/**
 * ConnettoreHTTPCOREInputStreamEntityProducer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCOREInputStreamEntityProducer extends AbstractConnettoreHTTPCOREInputStreamEntityProducer<InputStream>{

	private int count = 0;
	
	public ConnettoreHTTPCOREInputStreamEntityProducer(InputStream is, org.apache.hc.core5.http.ContentType contentType, String contentEncoding, ConnettoreLogger logger) throws TransactionNotExistsException {
		super(is, contentType, contentEncoding, logger);
	}
	
	@Override
	public int available() {
		try {
			return this.object.available();
		}catch(Exception e) {
			throw new UtilsRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void produce(DataStreamChannel channel) throws IOException {
		byte [] buffer = new byte[Utilities.DIMENSIONE_BUFFER];
		int letti = 0;
		while( (letti=this.object.read(buffer)) != -1 ){
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
		/**System.out.println("======== writeBytes (RICHIESTA) (scritti: "+this.count+") ("+Utilities.convertBytesToFormatString(this.count)+")");*/
		channel.endStream();
	}

	/**@Override
	public void releaseResources() {
		this.object.close();
	}*/
	
}
