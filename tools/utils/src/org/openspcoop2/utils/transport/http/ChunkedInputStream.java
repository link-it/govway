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
package org.openspcoop2.utils.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.openspcoop2.utils.Utilities;

/**
 * ChunkedInputStream
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ChunkedInputStream extends InputStream {

	private final InputStream is;
	private final int chunkedSize;
	private final int chunkedSleepMs;
	
	private int counter;
	
	public ChunkedInputStream(InputStream is, Integer chunkedSize, Integer chunkedSleepMs) {
		this.is = is;
		this.chunkedSize = Objects.requireNonNullElse(chunkedSize, -1);
		this.chunkedSleepMs = Objects.requireNonNullElse(chunkedSleepMs, 0);
		this.counter = 0;
	}
	
	private void trySleep() {
		if (this.chunkedSize > 0 && this.counter == this.chunkedSize) {
			Utilities.sleep(this.chunkedSleepMs);
		}
		this.counter = this.counter % this.chunkedSize;
	}
	
	@Override
	public int read() throws IOException {
		this.trySleep();
		this.counter++;
		return this.is.read();
	}
	
	@Override
	public int read(byte[] buf, int off, int len) throws IOException {
		this.trySleep();
		
		int readLength = Math.min(len, this.chunkedSize - this.counter);
		readLength = this.is.read(buf, off, readLength);
		this.counter += readLength;
		
		return readLength;
		
	}

}
