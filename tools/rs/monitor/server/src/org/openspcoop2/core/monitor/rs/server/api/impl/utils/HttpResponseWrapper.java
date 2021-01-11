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
package org.openspcoop2.core.monitor.rs.server.api.impl.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.utils.transport.http.WrappedHttpServletResponse;

/**
 * HttpResponseWrapper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class HttpResponseWrapper extends WrappedHttpServletResponse  {

	private ResponseOutputStream responseOutputStream;
	
	public HttpResponseWrapper(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
		this.responseOutputStream = new ResponseOutputStream();
	}
	
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return this.responseOutputStream;
	}
	
	public byte[] toByteArray() {
		return this.responseOutputStream.toByteArray();
	}
	
}

class ResponseOutputStream extends ServletOutputStream{

	private ByteArrayOutputStream bout = new ByteArrayOutputStream();
	
	public byte[] toByteArray() {
		return this.bout.toByteArray();
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		this.bout.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.bout.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		this.bout.flush();
	}

	@Override
	public void close() throws IOException {
		this.bout.close();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public void setWriteListener(WriteListener arg0) {
		
	}

	@Override
	public void write(int b) throws IOException {
		this.bout.write(b);
	}
	
}
