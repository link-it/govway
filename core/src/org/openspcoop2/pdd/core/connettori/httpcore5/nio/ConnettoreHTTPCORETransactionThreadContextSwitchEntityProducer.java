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

import java.io.IOException;
import java.util.Set;

import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.UtilsRuntimeException;

/**
 * AbstractConnettoreHTTPCOREInputStreamEntityProducer
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCORETransactionThreadContextSwitchEntityProducer<T extends AsyncEntityProducer> implements AsyncEntityProducer{

	protected T wrapped;
	
	private ConnettoreHTTPCORE connettore;
	private boolean switchThreadContext = false; 
	private String function;

	private BooleanNullable switchThreadLocalContextDoneHolder = BooleanNullable.FALSE();
	
	protected ConnettoreHTTPCORETransactionThreadContextSwitchEntityProducer(ConnettoreHTTPCORE connettore, T wrapped) {
		this.wrapped = wrapped;
		
		// L'inizializzazione avviene nel worker thread del webserver o nel worker thread della richiesta a seconda della configurazione (vedi org.openspcoop2.pdd.services.connector.ConnectorApplicativeThreadPool)
		// Se viene usato il thread local per gestire il contesto, gli oggetti devono essere riportati sul thread local del thread della libreria client nio che effettuerà la spedizione (metodi successivi)
		if(TransactionContext.isUseThreadLocal()) {
			this.switchThreadContext = true;
			this.connettore = connettore;
			this.function = "EntityProducer-"+this.wrapped.getClass().getName();
		}
	}
	
	@Override
	public int available() {
		if(this.switchThreadContext) {
			try {
				this.connettore.checkThreadLocalContext(this.function, this.switchThreadLocalContextDoneHolder);
			}catch(Exception e) {
				throw new UtilsRuntimeException(e.getMessage(),e);
			}
		}
		return this.wrapped.available();
	}
	@Override
	public void produce(DataStreamChannel channel) throws IOException {
		
		if(this.switchThreadContext) {
			this.connettore.checkThreadLocalContext(this.function, this.switchThreadLocalContextDoneHolder);
		}
	
		this.wrapped.produce(channel);
	}
	
	private boolean released = false;
	
	@Override
	public void releaseResources() {
		
		if(this.switchThreadContext && !this.released) {
			this.connettore.removeThreadLocalContext(this.function, this.switchThreadLocalContextDoneHolder);
			this.released = true;
		}

		this.wrapped.releaseResources();
	}
	
	@Override
	public void failed(Exception e) {
		
		if(this.switchThreadContext && !this.released) {
			this.connettore.removeThreadLocalContext(this.function, this.switchThreadLocalContextDoneHolder);
			this.released = true;
		}
		
		this.wrapped.failed(e);
	}
	
	
	// proxy only
	
	@Override
	public String getContentEncoding() {
		return this.wrapped.getContentEncoding();
	}
	@Override
	public long getContentLength() {
		return this.wrapped.getContentLength();
	}
	@Override
	public String getContentType() {
		return this.wrapped.getContentType();
	}
	@Override
	public Set<String> getTrailerNames() {
		return this.wrapped.getTrailerNames();
	}
	@Override
	public boolean isChunked() {
		return this.wrapped.isChunked();
	}
	@Override
	public boolean isRepeatable() {
		return this.wrapped.isRepeatable();
	}
	
	
	
}
