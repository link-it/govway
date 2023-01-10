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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* DumpByteArrayOutputStream_DefaultImpl
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
class DumpByteArrayOutputStream_DefaultImpl extends ByteArrayOutputStream implements IDumpByteArrayOutputStream {

	private int soglia = -1; // -1 senza soglia
	private int attuale = 0;
	private File repositoryFile = null;
	private String idTransazione;
	private String tipoMessaggio;
	private File f = null;
	private boolean fLocked = false;

	private FileOutputStream fout = null;
	
	DumpByteArrayOutputStream_DefaultImpl() {
		// non e' attivo alcun controllo di soglia
	}
	DumpByteArrayOutputStream_DefaultImpl(Integer soglia, File repositoryFile, String idTransazione, String tipoMessaggio) {
		this.soglia = soglia;
		this.repositoryFile = repositoryFile;
		this.idTransazione = idTransazione;
		this.tipoMessaggio = tipoMessaggio;
	}
	

	private void checkInitFile() throws Exception {
		if(this.f==null) {
			this._checkInitFile();
		}
	}
	private synchronized void _checkInitFile() throws Exception {
		if(this.f==null) {
			this.f = DumpByteArrayOutputStream.newFile(this.repositoryFile, this.tipoMessaggio, this.idTransazione);
			super.flush();
			super.close();
			this.fout = new FileOutputStream(this.f);
			if(super.size()>0) {
				this.fout.write(super.toByteArray());
			}
		}
	}
	
	@Override
	public boolean isSerializedOnFileSystem() {
		return this.f!=null;
	}
	@Override
	public File getSerializedFile() {
		return this.f;
	}

	@Override
	public void lock() {
		this.fLocked = true;
	}
	@Override
	public void unlock() {
		this.fLocked = false;
	}
	
	@Override
	public synchronized void writeInBuffer(int b) {
		if(this.soglia>0) {
			if(this.attuale>this.soglia) {
				try {
					this.attuale++;
					
					if(this.f==null) {
						checkInitFile();
					}
					if(this.fout!=null) {
						this.fout.write(b);
					}
					else {
						throw new Exception("FileOutputStream '"+this.f.getAbsolutePath()+"' closed");
					}
				}catch(Throwable e) {
					throw new RuntimeException(e.getMessage(),e);
				}
				return;
			}
		}
		this.attuale++;
		super.write(b);
	}

	@Override
	public synchronized void writeInBuffer(byte[] b, int off, int len) {
		if(this.soglia>0) {
			if( (this.attuale>this.soglia) || ((this.attuale+len)>this.soglia) ) {
				try {
					this.attuale=this.attuale+len;
					
					if(this.f==null) {
						checkInitFile();
					}
					if(this.fout!=null) {
						this.fout.write(b, off, len);
					}
					else {
						throw new Exception("FileOutputStream '"+this.f.getAbsolutePath()+"' closed");
					}
				}catch(Throwable e) {
					throw new RuntimeException(e.getMessage(),e);
				}
				return;
			}
		}
		this.attuale=this.attuale+len;
		super.write(b, off, len);
	}


	@Override
	public void writeInBuffer(byte[] b) throws IOException {
		if(this.soglia>0) {
			if( (this.attuale>this.soglia) || ((this.attuale+b.length)>this.soglia) ) {
				try {
					this.attuale=this.attuale+b.length;
					
					if(this.f==null) {
						checkInitFile();
					}
					if(this.fout!=null) {
						this.fout.write(b);
					}
					else {
						throw new Exception("FileOutputStream '"+this.f.getAbsolutePath()+"' closed");
					}
				}catch(Throwable e) {
					throw new RuntimeException(e.getMessage(),e);
				}
				return;
			}
		}
		this.attuale=this.attuale+b.length;
		super.write(b);
	}
//	
//	@Override
//	public void writeBytes(byte[] b) {
//		if(this.soglia>0) {
//			if( (this.attuale>this.soglia) || ((this.attuale+b.length)>this.soglia) ) {
//				try {
//					if(this.f==null) {
//						checkInitFile();
//					}
//					if(this.fout!=null) {
//						this.fout.write(b);
//					}
//					else {
//						throw new Exception("FileOutputStream '"+this.f.getAbsolutePath()+"' closed");
//					}
//				}catch(Throwable e) {
//					throw new RuntimeException(e.getMessage(),e);
//				}
//				return;
//			}
//		}
//		this.attuale=this.attuale+b.length;
//		super.writeBytes(b);
//	}

	@Override
	public synchronized void reset() {
		clearResources();
	}
	@Override
	public void clearResources() {
		if(this.f!=null) {
			_clearResources();
		}
	}
	private synchronized void _clearResources() {
		if(this.f!=null && !this.fLocked) {
			try {
				if(this.fout!=null) {
					this.fout.flush();
					this.fout.close();
					this.fout = null;
				}
				this.f.delete();
				this.f = null;
			}catch(Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
	}

	
	@Override
	public synchronized int size() {
		if(this.f!=null) {
			try {
				return (int) this.f.length();
			}catch(Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		else {
			return super.size();
		}
	}
	
	
	
	@Override
	public synchronized void writeTo(OutputStream out) throws IOException {
		try {
			this.close();
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		if(this.f!=null) {
			try {
				CopyStream.copy(this.f, out);
			}catch(Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		else {
			super.writeTo(out);
		}
	}

	
	@Override
	public byte[] toByteArray() {
		return _serializeToByteArray();
	}
	@Override
	public byte[] serializeToByteArray() {
		return _serializeToByteArray();
	}
	private synchronized byte[] _serializeToByteArray() {
		try {
			this.close();
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		if(this.f!=null) {
			try {
				return FileSystemUtilities.readBytesFromFile(this.f);
			}catch(Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		else {
			return super.toByteArray();
		}
	}

	
	@Override
	public String toString() {
		return this._serializeToString();
	}
	@Override
	public String serializeToString() {
		return this._serializeToString();
	}
	private synchronized String _serializeToString() {
		try {
			this.close();
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		if(this.f!=null) {
			try {
				return FileSystemUtilities.readFile(this.f);
			}catch(Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		else {
			return super.toString();
		}
	}

	@Override
	public String toString(String charsetName) throws UnsupportedEncodingException {
		return this._serializeToString(charsetName);
	}
	@Override
	public String serializeToString(String charsetName) throws UnsupportedEncodingException {
		return this._serializeToString(charsetName);
	}
	private synchronized String _serializeToString(String charsetName) throws UnsupportedEncodingException {
		try {
			this.close();
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		if(this.f!=null) {
			try {
				return FileSystemUtilities.readFile(this.f, charsetName);
			}catch(Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		else {
			return super.toString(charsetName);
		}
	}

	@Override
	public String toString(Charset charset) {
		return this._serializeToString(charset);
	}
	@Override
	public String serializeToString(Charset charset) {
		return this._serializeToString(charset);
	}
	private synchronized String _serializeToString(Charset charset) {
		try {
			this.close();
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		if(this.f!=null) {
			try {
				return FileSystemUtilities.readFile(this.f, charset);
			}catch(Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		else {
			return super.toString(charset);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public synchronized String toString(int hibyte) {
		throw new RuntimeException("NotImplemented");
	}

	@Override
	public void close() throws IOException {
		if(this.f!=null) {
			try {
				if(this.fout!=null) {
					this.fout.flush();
					this.fout.close();
					this.fout = null;
				}
			}catch(Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		else {
			super.flush();
			super.close();
		}
	}


	@Override
	public void flush() throws IOException {
		if(this.f!=null) {
			try {
				if(this.fout!=null) {
					this.fout.flush();
				}
			}catch(Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		else {
			super.flush();
		}
	}
	
	@Override
	public InputStream getInputStream() {
		try {
			this.close();
		}catch(Throwable e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		if(this.f!=null) {
			try {
				return new FileInputStream(this.f);
			}catch(Throwable e) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
		else {
			return new ByteArrayInputStream(super.toByteArray());
		}
	}
}
