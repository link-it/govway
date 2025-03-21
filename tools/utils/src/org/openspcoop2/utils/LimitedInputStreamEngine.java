/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * LimitedInputStream
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LimitedInputStreamEngine extends FilterInputStream {

	private long limitBytes;
	private long count;
	
	private InputStream isWrapped = null;
	private String prefixError = "";
	private Map<Object> ctx;
	private boolean checkDisabled = false;

	private ILimitExceededNotifier notifier;
	
	protected LimitedInputStreamEngine(InputStream inputStream, long limitBytes, String prefixError, Map<Object> ctx, ILimitExceededNotifier notifier) throws IOException {
		super(inputStream);
		this.limitBytes = limitBytes;
		
		this.isWrapped = inputStream;
		if(prefixError!=null) {
			this.prefixError = prefixError;
		}
		this.ctx = ctx;
		if(this.limitBytes<=0) {
			throw new IOException("Invalid limit");
		}
		
		this.notifier = notifier;
	}
	
	public InputStream getIsWrapped() {
		return this.isWrapped;
	}

	protected void disableCheck() {
		this.checkDisabled = true;
	}
	protected void updateThreshold(long limitBytes) throws IOException {
		if(this.limitBytes<=0) {
			throw new IOException("Invalid limit");
		}
		this.limitBytes = limitBytes;
	}
	protected void updateContext(Map<Object> ctx) {
		this.ctx = ctx;
	}
	protected void updateNotifier(ILimitExceededNotifier notifier) {
		this.notifier = notifier;
	}

	private void checkLimit() throws IOException {
		if(this.checkDisabled) {
			return; // e' stato disabilitato dopo averlo creato
		}
		if (this.count > this.limitBytes) {
			
			/**System.out.println("Raggiunto limite dopo aver letto: "+this.count);*/
			
			payloadTooLarge(this.prefixError, this.ctx, this.notifier, this.count);
			
		}
	}
	public static void payloadTooLarge(String prefixError, Map<Object> ctx, ILimitExceededNotifier notifier, long count) throws LimitExceededIOException {
		limitExceeded(prefixError, LimitedInputStream.ERROR_PAYLOAD_TOO_LARGE_MSG, false, ctx, notifier, count);
	}
	public static void contentLenghtLimitExceeded(String prefixError, Map<Object> ctx, ILimitExceededNotifier notifier, long count) throws LimitExceededIOException {
		limitExceeded(prefixError, LimitedInputStream.ERROR_CONTENT_LENGTH_EXCEEDED_MSG, true, ctx, notifier, count);
	}
	private static void limitExceeded(String prefixError, String error, boolean contentLengthExceeded, Map<Object> ctx, ILimitExceededNotifier notifier, long count) throws LimitExceededIOException {
		String errorMsg = prefixError+error;
		if(ctx!=null) {
			ctx.put(LimitedInputStream.ERROR_MSG_KEY, errorMsg);
		}
		LimitExceededIOException exc = new LimitExceededIOException(errorMsg);
		if(ctx!=null) {
			ctx.put(LimitedInputStream.EXCEPTION_KEY, exc);
		}
		
		if(notifier!=null) {
			notifier.notify(count, contentLengthExceeded);
		}
		
		throw exc;
	}

	@Override
	public int read() throws IOException {
		int res = super.read();
		if (res != -1) {
			this.count++;
			checkLimit();
		}
		return res;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int res = super.read(b, off, len);
		if (res > 0) {
			this.count += res;
			checkLimit();
		}
		return res;
	}

}
