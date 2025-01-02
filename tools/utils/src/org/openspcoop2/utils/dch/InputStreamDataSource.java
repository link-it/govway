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

package org.openspcoop2.utils.dch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jakarta.activation.DataSource;

/**
 * InputStreamDataSource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class InputStreamDataSource implements DataSource {

	private String name;
	private String contentType;
	private byte[] bytes;

	public InputStreamDataSource(String name, String contentType, InputStream inputStream) throws IOException {
		int read;			
		this.name = name;
		this.contentType = contentType;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		byte[] buff = new byte[256];			
		while ((read = inputStream.read(buff)) != -1) {
			baos.write(buff, 0, read);
		}
		baos.flush();
		baos.close();
		this.bytes = baos.toByteArray();
	}
	public InputStreamDataSource(String name, String contentType, byte[] b) throws IOException {
		this.name = name;
		this.contentType = contentType;
		this.bytes = b;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(this.bytes);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new IOException("Cannot write to this read-only resource");
	}

}