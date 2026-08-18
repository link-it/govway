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

package org.openspcoop2.utils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TimeoutInputStrem
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimeoutInputStreamEngine extends InputStream {

	private static final long NANOSECONDI_PER_MILLISECONDO = 1000000L;


	/** Istante di creazione dello stream, rilevato con 'System.nanoTime()'.
	 *  Trattandosi della misura di un intervallo trascorso va utilizzato un orologio monotono: 'System.currentTimeMillis()'
	 *  e' un orologio di parete, soggetto agli aggiustamenti dell'ora di sistema, ed ha inoltre una granularita' di un
	 *  millisecondo intero che, sui timeout piu' brevi, rendeva il controllo impreciso fino ad un millisecondo. */
	private long createDateNs;
	private volatile int timeoutMs;
	private InputStream isWrapped = null;
	private String prefixError = "";
	private Map<Object> ctx;
	private volatile boolean checkDisabled = false;
	
	private ITimeoutNotifier notifier;
	
	protected TimeoutInputStreamEngine(InputStream is, int timeoutMs, String prefixError, Map<Object> ctx, ITimeoutNotifier notifier) throws IOException {
		this.createDateNs = System.nanoTime();
		this.timeoutMs = timeoutMs;
		this.isWrapped = is;
		if(prefixError!=null) {
			this.prefixError = prefixError;
		}
		this.ctx = ctx;
		if(this.timeoutMs<=0) {
			throw new IOException("Invalid timeout");
		}
		
		this.notifier = notifier;
	}
	
	public InputStream getIsWrapped() {
		return this.isWrapped;
	}
	
	protected void disableCheckTimeout() {
		this.checkDisabled = true;
	}
	protected void updateThreshold(int timeoutMs) throws IOException {
		if(this.timeoutMs<=0) {
			throw new IOException("Invalid timeout");
		}
		this.timeoutMs = timeoutMs;
	}
	protected void updateContext(Map<Object> ctx) {
		this.ctx = ctx;
	}
	protected void updateNotifier(ITimeoutNotifier notifier) {
		this.notifier = notifier;
	}
	
	private void checkTimeout() throws IOException {
		if(this.checkDisabled) {
			return; // e' stato disabilitato dopo averlo creato
		}
		long nowNs = System.nanoTime() - this.createDateNs;
		if(nowNs>(this.timeoutMs * NANOSECONDI_PER_MILLISECONDO)) {
			long now = nowNs / NANOSECONDI_PER_MILLISECONDO;
			String errorMsg = this.prefixError+TimeoutInputStream.ERROR_MSG;
			if(this.ctx!=null) {
				this.ctx.put(TimeoutInputStream.ERROR_MSG_KEY, errorMsg);
			}
			TimeoutIOException exc = new TimeoutIOException(errorMsg);
			if(this.ctx!=null) {
				this.ctx.put(TimeoutInputStream.EXCEPTION_KEY, exc);
			}
			
			if(this.notifier!=null) {
				this.notifier.notify(now);
			}
			
			throw exc;
		}
	}
	
	@Override
	public int read() throws IOException {
		checkTimeout();
		return this.isWrapped.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		checkTimeout();
		return this.isWrapped.read(b);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		checkTimeout();
		return this.isWrapped.read(b, off, len);
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		checkTimeout();
		return this.isWrapped.readAllBytes();
	}

	@Override
	public byte[] readNBytes(int len) throws IOException {
		checkTimeout();
		return this.isWrapped.readNBytes(len);
	}

	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException {
		checkTimeout();
		return this.isWrapped.readNBytes(b, off, len);
	}
	
	@Override
	public long skip(long n) throws IOException {
		checkTimeout();
		return this.isWrapped.skip(n);
	}

	@Override
	public int available() throws IOException {
		checkTimeout();
		return this.isWrapped.available();
	}

	@Override
	public synchronized void reset() throws IOException {
		checkTimeout();
		this.isWrapped.reset();
	}

	@Override
	public long transferTo(OutputStream out) throws IOException {
		checkTimeout();
		return this.isWrapped.transferTo(out);
	}

	@Override
	public void close() throws IOException {
		checkTimeout(); // devo lanciare eccezione prima di chiamare la close, altrimenti rimane bloccato sulla close
		this.isWrapped.close();
	}
	
	// METODI SENZA CONTROLLO
	
	@Override
	public synchronized void mark(int readlimit) {
		this.isWrapped.mark(readlimit);
	}
	
	@Override
	public boolean markSupported() {
		return this.isWrapped.markSupported();
	}

}
