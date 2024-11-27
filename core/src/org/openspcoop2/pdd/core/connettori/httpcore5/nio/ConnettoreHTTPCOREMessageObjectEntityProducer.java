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

import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;

/**
 * ConnettoreHTTPCOREMessageObjectEntityProducer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCOREMessageObjectEntityProducer extends AbstractConnettoreHTTPCOREInputStreamEntityProducer<OpenSPCoop2Message>{

	private boolean consume;
	private boolean consumed = false;
	
	public ConnettoreHTTPCOREMessageObjectEntityProducer(OpenSPCoop2Message msg, boolean consume, org.apache.hc.core5.http.ContentType contentType, String contentEncoding, ConnettoreLogger logger) {
		super(msg, contentType, contentEncoding, logger);
		this.consume = consume;
	}
	
	@Override
	public int available() {
		System.out.println("@@ AVAILABLE ???");
		if(this.consumed) {
			System.out.println("@@ AVAILABLE rETURN -1");
			return -1;
		}
		else {
			System.out.println("@@ AVAILABLE rETURN 1");
			return 1;
		}
	}

	@Override
	public void produce(DataStreamChannel channel) throws IOException {
		if(this.consumed) {
			throw new IOException("Already consumed");
		}
		else {
			DataStreamChannelOutputStream outStream = new DataStreamChannelOutputStream(channel);
			try {
				this.object.writeTo(outStream, this.consume);
			}catch(Exception e) {
				throw new IOException(e.getMessage(), e);
			}
			outStream.close(); /** channel.endStream(); */
			this.consumed = this.consume;
		}
	}

}
