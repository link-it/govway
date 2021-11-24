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
import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.notifier.unblocked.PipedUnblockedStream;

/**
 * ConnettoreHTTPJava_inputStreamEntityConsumer
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPJavaFlow_responseConsumer {
	private static final int SUBSCRIPTION_MIN_SIZE = 4;
	private static final int SUBSCRIPTION_MAX_SIZE = 16;

	private static ExecutorService executors = Executors.newFixedThreadPool( 2 );

	private final HttpResponse< Publisher<List<ByteBuffer>> > res;
	private PipedUnblockedStream stream = null;

	public ConnettoreHTTPJavaFlow_responseConsumer( HttpResponse<Publisher<List<ByteBuffer>>> response ) {
		this.res = response;
	}

	public int getResponseCode() {
		return res.statusCode();
	}

	public HttpHeaders getHeaders() {
		return res.headers();
	}

	public long getContentLength() {
		return this.res.headers().firstValueAsLong( "Content-Length" ).orElse( -1L );
	}

	public InputStream getContent() {
		if ( this.stream == null ) {
			this.stream = new PipedUnblockedStream( null, Utilities.DIMENSIONE_BUFFER );
			CompletableFuture.runAsync( () -> {
				this.res.body().subscribe( new Subscriber< List<ByteBuffer> >() {
					private Subscription subscription = null;
					private int requestedItems = 0;
					private long readBytes = 0L;

					@Override
					public void onSubscribe( Subscription subscription ) {
						this.subscription = subscription;
						this.requestedItems = SUBSCRIPTION_MAX_SIZE;
						this.subscription.request( SUBSCRIPTION_MAX_SIZE );
					}

					@Override
					public void onNext( List<ByteBuffer> itemList ) {
						if ( itemList != null && itemList.size() > 0 ) {
							itemList.forEach( (i) -> {
								try {
									byte[] bytes = new byte[ i.remaining() ];
									i.get( bytes );
									ConnettoreHTTPJavaFlow_responseConsumer.this.stream.write( bytes );
									this.readBytes += bytes.length;
								} catch (IOException e) {
									// TBK
									e.printStackTrace();
									throw new RuntimeException( e );
								}
								this.requestedItems--;
							});
						}
						if ( this.requestedItems >= 0 && this.requestedItems < SUBSCRIPTION_MIN_SIZE ) {
							int toBeRequest = SUBSCRIPTION_MAX_SIZE - this.requestedItems;
							this.requestedItems = SUBSCRIPTION_MAX_SIZE;
							this.subscription.request( toBeRequest );
						} else
						if ( this.requestedItems < 0 ) {
							this.requestedItems = SUBSCRIPTION_MAX_SIZE;
							this.subscription.request( SUBSCRIPTION_MAX_SIZE );
						}
					}

					@Override
					public void onError( Throwable e ) {
						// TBK
						e.printStackTrace();
						this.subscription.cancel();
					}

					@Override
					public void onComplete() {
						this.requestedItems = -1;
						try {
							ConnettoreHTTPJavaFlow_responseConsumer.this.stream.close();
						} catch (IOException e) {
							// TBK
							throw new RuntimeException( e );
						}
					}
				});
			}, executors );
		}
		return this.stream;
	}
}
