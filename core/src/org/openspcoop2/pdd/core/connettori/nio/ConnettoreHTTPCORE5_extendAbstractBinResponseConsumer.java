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

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.hc.client5.http.async.methods.AbstractBinResponseConsumer;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedStream;

/**
 * ConnettoreHTTPCORE5_extendAbstractBinResponseConsumer
 * NOTA: non funziona in streaming. Il complete viene chiamato solamente quando è stato letto tutto lo stream
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE5_extendAbstractBinResponseConsumer extends AbstractBinResponseConsumer<ConnettoreHTTPCORE5_httpResponse>{

	private ConnettoreHTTPCORE5_httpResponse res = null;
	private ContentType ct = null;
	private PipedUnblockedStream stream = null;

	@Override
	public void releaseResources() {
		if(this.stream!=null) {
			try {
				this.stream.close();
			}catch(Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
	}

	@Override
	protected ConnettoreHTTPCORE5_httpResponse buildResult() {
		// NOTA: non funziona in streaming. Il complete viene chiamato solamente quando è stato letto tutto lo stream
		//System.out.println("BUILD RESULT!");
		return this.res;
	}

	@Override
	protected void start(HttpResponse res, ContentType ct) throws HttpException, IOException {
		//System.out.println("START RESPONSE");
		this.res = new ConnettoreHTTPCORE5_httpResponse(res);
		this.ct = ct;
	}

	@Override
	protected int capacityIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void data(ByteBuffer bb, boolean endOfStream) throws IOException {
		//System.out.println("DATA! endOfStream["+endOfStream+"]");
		if(this.stream==null) {
			this.stream = new PipedUnblockedStream(null, Utilities.DIMENSIONE_BUFFER);
			this.stream.setSource("Response");
			this.res.setEntity(new InputStreamEntity(this.stream, this.ct));
		}
		while (bb.hasRemaining()) {
			//System.out.println("SCRIVOOOOOO");
			this.stream.write(bb.get());
        }
        if (endOfStream) {
        	releaseResources();
        }
	}

}
