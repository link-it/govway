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

package org.openspcoop2.utils.io.notifier.unblocked;

import java.io.IOException;
import java.io.OutputStream;

/**
 * PipedOutputStream
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PipedUnblockedOutputStream extends OutputStream {

	private PipedUnblockedStream wrappedPipe;
	
	public PipedUnblockedOutputStream(PipedUnblockedStream wrappedPipe) {
		this.wrappedPipe = wrappedPipe;
	}

	@Override
	public void write(int b) throws IOException {
		this.wrappedPipe.write((byte)b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		this.wrappedPipe.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.wrappedPipe.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		// nop
	}

	@Override
	public void close() throws IOException {
		this.wrappedPipe.close();
	}
}
