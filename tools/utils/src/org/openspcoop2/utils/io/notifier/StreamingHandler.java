/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.utils.io.notifier;

import java.io.IOException;
/**
 * Interface for streaming handlers
 * Provides a single method, feed, which takes, encoded in an integer,
 * the next byte read from the stream. Each subclass can define its behavior
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface StreamingHandler {

	/**
	 * Streaming Handler Identifier
	 * 
	 * @return Streaming Handler Identifier
	 */
	public String getID();
	
	/**
	 * feeds each handler, one byte a time
	 * @param b byte to feed the handler with
	 * @throws IOException
	 */
	public void feed(byte b) throws IOException;
	
	/**
	 * feeds each handler, one byte a time
	 * @param b bytes to feed the handler with
	 * @throws IOException
	 */
	public void feed(byte [] b) throws IOException;
		
	/**
	 * end stream
	 * @throws IOException
	 */
	public void end() throws IOException;
	
	/**
	 * closes resources used by streaming handler
	 * @throws IOException
	 */
	public void closeResources() throws IOException;
}
