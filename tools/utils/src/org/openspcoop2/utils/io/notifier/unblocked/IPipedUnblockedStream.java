/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import java.io.InputStream;

import org.slf4j.Logger;

/**
 * IPipedUnblockedStream
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class IPipedUnblockedStream extends InputStream {

	@Override
	public abstract int read(byte[] b) throws IOException;
	@Override
	public abstract int read(byte[] b, int off, int len) throws IOException;
	@Override
	public abstract int read() throws IOException;
	
	@Override
	public abstract void close() throws IOException;
	
	public abstract void write(byte b) throws IOException;
	public abstract void write(byte [] b) throws IOException;
	public abstract void write(byte[] b, int off, int len) throws IOException;
	
	public abstract void init(Logger log, long sizeBuffer, int timeoutMs, String source);
	public abstract void setTimeout(int timeoutMs);
}
