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
package org.openspcoop2.protocol.modipa.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * MockServletInputStream semplice stream usato per poter scrivere
 * su un richiesta servlet http
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MockServletInputStream extends ServletInputStream {

	
	private ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
	private ReadListener cb = null;
	
	public MockServletInputStream() {
		this(new byte[0]);
	}
	
	public MockServletInputStream(byte[] buf) {
		this.is = new ByteArrayInputStream(buf);
	}
	
	@Override
	public boolean isFinished() {
		return this.is.available() > 0;
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setReadListener(ReadListener arg0) {
		try {
			arg0.onDataAvailable();
		} catch (IOException e) {
			arg0.onError(e);
		}
		
		this.cb = arg0;
	}

	@Override
	public int read() throws IOException {
		int v = this.is.read();
		if (this.isFinished() && this.cb != null)
			this.cb.onAllDataRead();
		return v;
	}

}
