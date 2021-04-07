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

package org.openspcoop2.utils;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 * TimeoutInputStrem
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimeoutInputStream extends FilterInputStream {

	public final static String EXCEPTION_KEY = "TimeoutIOException";
	public final static String ERROR_MSG_KEY = "TimeoutInputStream";
	public final static String ERROR_MSG = "Read timed out";
	
	public TimeoutInputStream(InputStream is, int timeoutMs) throws IOException {
		this(is, timeoutMs, null, null);
	}
	public TimeoutInputStream(InputStream is, int timeoutMs, Hashtable<String, Object> ctx) throws IOException {
		this(is, timeoutMs, null, ctx);
	}
	public TimeoutInputStream(InputStream is, int timeoutMs, String prefixError, Hashtable<String, Object> ctx) throws IOException {
		super(new TimeoutInputStreamEngine(is, timeoutMs, prefixError, ctx));
	}
	
	public InputStream getIsWrapped() {
		InputStream is = super.in;
		if(is instanceof TimeoutInputStreamEngine) {
			return ((TimeoutInputStreamEngine)is).getIsWrapped();
		}
		return is;
	}

}
