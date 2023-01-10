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
package org.openspcoop2.utils.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
* IDumpByteArrayOutputStream
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public interface IDumpByteArrayOutputStream {

	public boolean isSerializedOnFileSystem();
	public File getSerializedFile();
	
	public void lock();
	public void unlock();
	
	public void writeInBuffer(int b) throws IOException;
	public void writeInBuffer(byte[] b, int off, int len) throws IOException;
	public void writeInBuffer(byte[] b) throws IOException;
	
	public void reset();
	public void clearResources();
	
	public int size();
	
	public void writeTo(OutputStream out) throws IOException;
	public byte[] serializeToByteArray();
	public String serializeToString();
	public String serializeToString(String charsetName) throws UnsupportedEncodingException;
	public String serializeToString(Charset charset) throws UnsupportedEncodingException;
	
	public void flush() throws IOException;
	public void close() throws IOException;
	
	public InputStream getInputStream();
}
