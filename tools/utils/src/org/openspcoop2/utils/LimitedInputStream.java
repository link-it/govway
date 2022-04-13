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

package org.openspcoop2.utils;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * TimeoutInputStrem
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LimitedInputStream extends FilterInputStream {

	public final static MapKey<String> EXCEPTION_KEY = Map.newMapKey("LimitExceededIOException");
	public final static MapKey<String> ERROR_MSG_KEY = Map.newMapKey("LimitedInputStream");
	public final static String ERROR_MSG = "Payload too large";
	
	public LimitedInputStream(InputStream is, long limitBytes) throws IOException {
		this(is, limitBytes, null, null, null);
	}
	public LimitedInputStream(InputStream is, long limitBytes, Map<Object> ctx) throws IOException {
		this(is, limitBytes, null, ctx, null);
	}
	public LimitedInputStream(InputStream is, long limitBytes, Map<Object> ctx, ILimitExceededNotifier notifier) throws IOException {
		this(is, limitBytes, null, ctx, notifier);
	}
	public LimitedInputStream(InputStream is, long limitBytes, String prefixError, Map<Object> ctx, ILimitExceededNotifier notifier) throws IOException {
		super(new LimitedInputStreamEngine(is, limitBytes, prefixError, ctx, notifier));
	}
	
	public InputStream getIsWrapped() {
		InputStream is = super.in;
		if(is instanceof LimitedInputStreamEngine) {
			return ((LimitedInputStreamEngine)is).getIsWrapped();
		}
		return is;
	}
	
	public void disableCheck() {
		InputStream is = super.in;
		if(is instanceof LimitedInputStreamEngine) {
			((LimitedInputStreamEngine)is).disableCheck();
		}
	}
	public void updateThreshold(long limitBytes) throws IOException {
		InputStream is = super.in;
		if(is instanceof LimitedInputStreamEngine) {
			((LimitedInputStreamEngine)is).updateThreshold(limitBytes);
		}
	}
	public void updateContext(Map<Object> ctx) {
		InputStream is = super.in;
		if(is instanceof LimitedInputStreamEngine) {
			((LimitedInputStreamEngine)is).updateContext(ctx);
		}
	}
	public void updateNotifier(ILimitExceededNotifier notifier) {
		InputStream is = super.in;
		if(is instanceof LimitedInputStreamEngine) {
			((LimitedInputStreamEngine)is).updateNotifier(notifier);
		}
	}

}
