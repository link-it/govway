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
import java.util.List;

import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.CapacityChannel;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.openspcoop2.pdd.services.connector.AsyncThreadPool;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedStream;

/**
 * ConnettoreHTTPCORE5_inputStreamEntityConsumer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORE5_inputStreamEntityConsumer implements AsyncResponseConsumer<ConnettoreHTTPCORE5_httpResponse>{

	private ConnettoreHTTPCORE5_httpResponse res = null;
	private ContentType ct = null;
	private PipedUnblockedStream stream = null;
	private FutureCallback<ConnettoreHTTPCORE5_httpResponse> callback;
	private long count = 0;
	private boolean complete = false;
	
	private void invokeCallback() {
		if(this.callback!=null) {
			Runnable runnable = new Runnable() {
				
				private FutureCallback<ConnettoreHTTPCORE5_httpResponse> callback;
				private ConnettoreHTTPCORE5_httpResponse res = null;
			
				public Runnable init(FutureCallback<ConnettoreHTTPCORE5_httpResponse> callback, ConnettoreHTTPCORE5_httpResponse res) {
					this.callback = callback;
					this.res = res;
					return this;
				}
				
				@Override
				public void run() {
					
					this.callback.completed(this.res);
				}
			}.init(this.callback, this.res);
//			System.out.println("ESEGUO!");
			AsyncThreadPool.execute(runnable);
			this.callback = null;
		}
	}
	
	
	@Override
	// Triggered to signal receipt of an intermediate (1xx) HTTP response
	public void informationResponse(HttpResponse res, HttpContext context) throws HttpException, IOException {
//		System.out.println("======== informationResponse");
	}
	
	@Override
	// Triggered to signal receipt of a response message head.
	public void consumeResponse(HttpResponse res, EntityDetails entityDetails, HttpContext context,
			FutureCallback<ConnettoreHTTPCORE5_httpResponse> callback) throws HttpException, IOException {
//		System.out.println("======== consumeResponse");
		if(entityDetails!=null && entityDetails.getContentType()!=null) {
			this.ct = ContentType.parse(entityDetails.getContentType());
		}
		this.res = new ConnettoreHTTPCORE5_httpResponse(res);
		this.callback = callback;
	}
	
	@Override
	// Triggered to pass incoming data to the data consumer
	public void consume(ByteBuffer bb) throws IOException {
		if(bb!=null && bb.remaining()>0) {
//			System.out.println("======== consume: "+ bb.remaining());
			
			if(this.stream==null) {
				this.stream = new PipedUnblockedStream(null, Utilities.DIMENSIONE_BUFFER);
				this.stream.setSource("Response");
				this.res.setEntity(new InputStreamEntity(this.stream, this.ct));
			}
			
			if(this.callback!=null) {
				//System.out.println("invoco la callback tramite CONSUME");
				invokeCallback();
			}
			
			/*while (bb.hasRemaining()) {
				this.stream.write(bb.get());
			}*/
			while (bb.remaining()>0) {
				byte[] buf = new byte[bb.remaining()];
				bb.get(buf);
				this.stream.write(buf);
				this.count=this.count+buf.length;
			}
		}
	}
	
	@Override
	// Triggered to signal ability of the underlying data stream to receive data capacity update
	public void updateCapacity(CapacityChannel channel) throws IOException {
//		System.out.println("======== updateCapacity");
		channel.update(Utilities.DIMENSIONE_BUFFER);
	}

	private void _streamEnd() {
		this.complete = true;
		if(this.stream!=null) {
			try {
				this.stream.close();
				this.stream = null;
			}catch(Exception e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
	}
	
	@Override
	// Triggered to signal termination of the data stream.
	public void streamEnd(List<? extends Header> list) throws HttpException, IOException {
//		System.out.println("======== streamEnd: " + this.count);
		if(this.callback!=null) {
//			System.out.println("invoco la callback via END");
			//this.callback.completed(this.res);
			invokeCallback();
		}
		
		if(this.count>0 && this.res!=null) {
			this.res.setCount(this.count);
		}

		_streamEnd();
	}

	@Override
	public void releaseResources() {
//		System.out.println("======== releaseResources (RISPOSTA) (scritti: "+this.count+") ("+Utilities.convertBytesToFormatString(this.count)+")");
		_streamEnd();
	}

	@Override
	// Triggered to signal a failure in data processing
	public void failed(Exception exception) {
		
		/*
		 * Viene chiamato anche quando la connessione viene rilasciata al pool. 
		 * Non deve essere sollevata l'eccezione se la risposta è stata correttamente gestita
		 * Esempio di stacktrace:
			java.io.InterruptedIOException
			at deployment.govway.ear//org.apache.hc.client5.http.impl.async.HttpAsyncMainClientExec$1.cancel(HttpAsyncMainClientExec.java:129)
			at deployment.govway.ear//org.apache.hc.client5.http.impl.async.InternalHttpAsyncExecRuntime$3.cancel(InternalHttpAsyncExecRuntime.java:267)
			at deployment.govway.ear//org.apache.hc.core5.concurrent.ComplexFuture.setDependency(ComplexFuture.java:55)
			   ...
			at deployment.govway.ear//org.apache.hc.client5.http.impl.async.InternalHttpAsyncExecRuntime$1.completed(InternalHttpAsyncExecRuntime.java:114)
			 ....
			at deployment.govway.ear//org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager$1.leaseCompleted(PoolingAsyncClientConnectionManager.java:240)
			at deployment.govway.ear//org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager$1.completed(PoolingAsyncClientConnectionManager.java:277)
			at deployment.govway.ear//org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager$1.completed(PoolingAsyncClientConnectionManager.java:226)
			at deployment.govway.ear//org.apache.hc.core5.concurrent.BasicFuture.completed(BasicFuture.java:123)
			at deployment.govway.ear//org.apache.hc.core5.pool.LaxConnPool$PerRoutePool.lease(LaxConnPool.java:496)
			at deployment.govway.ear//org.apache.hc.core5.pool.LaxConnPool.lease(LaxConnPool.java:165)
			at deployment.govway.ear//org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager.lease(PoolingAsyncClientConnectionManager.java:225)
			at deployment.govway.ear//org.apache.hc.client5.http.impl.async.InternalHttpAsyncExecRuntime.acquireEndpoint(InternalHttpAsyncExecRuntime.java:100)
			.....
			at deployment.govway.ear//org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient.execute(CloseableHttpAsyncClient.java:107)
			at deployment.govway.ear//org.openspcoop2.pdd.core.connettori.nio.ConnettoreHTTPCORE5.sendHTTP(ConnettoreHTTPCORE5.java:505)
		 **/
		
		if(!this.complete) {
		
			/*
			 * TODO:
			 * 1) Timeout
			 * 2) Pool diviso tra client e server
			 * 3) Questa eccezione deve essere gestita chiamando la callback se non ancora chiamata o sollevandola sull'input stream altrimenti 
			 * 
			 * */
			System.out.println("======== exception: " + this.stream + " - " + this.count );
			exception.printStackTrace();
			
		}
	}

}
