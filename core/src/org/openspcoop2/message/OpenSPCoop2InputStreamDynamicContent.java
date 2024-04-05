/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.message;

import java.io.IOException;
import java.io.OutputStream;

import org.openspcoop2.utils.io.DumpByteArrayOutputStream;

/**
 * OpenSPCoop2InputStreamDynamicContent
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2InputStreamDynamicContent extends org.apache.commons.io.input.CountingInputStream {

	private org.apache.commons.io.input.CountingInputStream is;

	private DumpByteArrayOutputStream buffer;
	
	private boolean isReadAll = false;
	
	public OpenSPCoop2InputStreamDynamicContent(org.apache.commons.io.input.CountingInputStream is, DumpByteArrayOutputStream buffer) {
		super(null);
		this.is = is;
		this.buffer = buffer;
	}

	public org.apache.commons.io.input.CountingInputStream getWrappedInputStream() {
		return this.is;
	}
	
	public DumpByteArrayOutputStream getBuffer() {
		return this.buffer;
	}
	
	public boolean isInputStreamConsumed() {
		return this.isReadAll;
	}

	// implementati con buffer
	@Override
	public int read() throws IOException {
		int r = this.is.read();
		if(this.buffer!=null && r!=-1) {
			this.buffer.write(r);
		}
		//System.out.println("read(); [readAll:"+this.isReadAll+"] (r:"+r+") buffer size: "+((this.buffer!=null) ? this.buffer.size() : "--n.d.--"));
		if(r==-1) {
			this.isReadAll = true;
		}
		return r;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int r = this.is.read(b, off, len);
		if(this.buffer!=null && r!=-1) {
			this.buffer.write(b, off, r);
		}
		//System.out.println("read(byte[],off:"+off+",len:"+len+"); [readAll:"+this.isReadAll+"] (r:"+r+") buffer size: "+((this.buffer!=null) ? this.buffer.size() : "--n.d.--"));
		//System.out.println("\t\t (leng:"+b.length+") ["+new String(b, off, r)+"]");
		if(r==-1) {
			this.isReadAll = true;
		}
		return r;
	}

	@Override
	public int read(byte[] b) throws IOException {
		int r = this.is.read(b);
		if(this.buffer!=null && r!=-1) {
			this.buffer.write(b, 0, r);
		}
		//System.out.println("read(byte[]); [readAll:"+this.isReadAll+"] (r:"+r+") buffer size: "+((this.buffer!=null) ? this.buffer.size() : "--n.d.--"));
		//System.out.println("\t\t (leng:"+b.length+") ["+new String(b)+"]");
		if(r==-1) {
			this.isReadAll = true;
		}
		return r;
	}
	
	// wrapped only
	
//	@Override
//	protected synchronized void afterRead(int n) {
//		this.is.afterRead(n);
//	}

	@Override
	public synchronized long getByteCount() {
		return this.is.getByteCount();
	}

	@Override
	public int getCount() {
		return this.is.getCount();
	}

	@Override
	public synchronized long resetByteCount() {
		return this.is.resetByteCount();
	}

	@Override
	public int resetCount() {
		return this.is.resetCount();
	}

	@Override
	public synchronized long skip(long length) throws IOException {
		return this.is.skip(length);
	}

	@Override
	public int available() throws IOException {
		return this.is.available();
	}

//	@Override
//	protected void beforeRead(int n) throws IOException {
//		this.is.beforeRead(n);
//	}

	@Override
	public void close() throws IOException {
		this.is.close();
	}

//	@Override
//	protected void handleIOException(IOException e) throws IOException {
//		this.is.handleIOException(e);
//	}

	@Override
	public synchronized void mark(int readlimit) {
		this.is.mark(readlimit);
	}

	@Override
	public boolean markSupported() {
		return this.is.markSupported();
	}

	@Override
	public synchronized void reset() throws IOException {
		this.is.reset();
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		return this.is.readAllBytes();
	}

	@Override
	public byte[] readNBytes(int len) throws IOException {
		return this.is.readNBytes(len);
	}

	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException {
		return this.is.readNBytes(b, off, len);
	}

	@Override
	public long transferTo(OutputStream out) throws IOException {
		return this.is.transferTo(out);
	}

	@Override
	public int hashCode() {
		return this.is.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.is.equals(obj);
	}

//	@Override
//	protected Object clone() throws CloneNotSupportedException {
//		return this.is.clone();
//	}

	@Override
	public String toString() {
		return this.is.toString();
	}

//	@Override
//	protected void finalize() throws Throwable {
//		this.is.finalize();
//	}
}
