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

package org.openspcoop2.utils.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
* DumpByteArrayOutputStream
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class DumpByteArrayOutputStream extends ByteArrayOutputStream {

	private int soglia = -1; // -1 senza soglia
	private int attuale = 0;
	private File repositoryFile = null;
	private String idTransazione;
	private String tipoMessaggio;
	private File f = null;
	private boolean fLocked = false;

	private FileOutputStream fout = null;
	
	public DumpByteArrayOutputStream() {
		// non e' attivo alcun controllo di soglia
	}
	public DumpByteArrayOutputStream(int soglia, File repositoryFile, String idTransazione, String tipoMessaggio) {
		this.soglia = soglia;
		this.repositoryFile = repositoryFile;
		this.idTransazione = idTransazione!=null ? idTransazione : getUniqueSerial();
		this.tipoMessaggio = tipoMessaggio;
	}

	public static DumpByteArrayOutputStream newInstance(byte[] content) {
		DumpByteArrayOutputStream out = null;
		if(content!=null && content.length>0) {
			out = new DumpByteArrayOutputStream();
			if(content!=null && content.length>0) {
				out.writeBytes(content);
			}
		}
		return out;
	}
	
	private static long uniqueSerialNumber = 0;
	private static synchronized long getUniqueSerialNumber(){
		if((uniqueSerialNumber+1) > Long.MAX_VALUE){
			uniqueSerialNumber = 0;
		} 
		uniqueSerialNumber++;
		return uniqueSerialNumber;
	}
	private static final String SIMPLE_DATE_FORMAT_MINUTE = "yyyyMMddHHmm";
	private static String getUniqueSerial() {
		return getUniqueSerialNumber()+""+DateUtils.getSimpleDateFormat(SIMPLE_DATE_FORMAT_MINUTE).format(DateManager.getDate());
	}
	
	private static final String formatDir = "yyyyMMdd"; // compatibile con windows S.O.
	private static final String formatNew = "yyyyMMdd_HHmmssSSS"; // compatibile con windows S.O.
	private static final String prefix = "dump";
	
	private synchronized void checkInitFile() throws Exception {
		if(this.f==null) {
			Date d = DateManager.getDate();
			SimpleDateFormat dateformatDir = DateUtils.getDefaultDateTimeFormatter(formatDir);
			String dateFormatDir = dateformatDir.format(d);
			File fData = new File(this.repositoryFile, dateFormatDir);			
			
			SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(formatNew);
			String dateFormat = dateformat.format(d);
			String nomeFile = prefix+this.tipoMessaggio+"_"+dateFormat+"_"+this.idTransazione.replaceAll("-", "_")+".bin";
			this.f = new File(fData, nomeFile);
			FileSystemUtilities.mkdirParentDirectory(this.f);
			super.flush();
			super.close();
			this.fout = new FileOutputStream(this.f);
			if(super.size()>0) {
				this.fout.write(super.toByteArray());
			}
		}
	}
	
	public boolean isSerializedOnFileSystem() {
		return this.f!=null;
	}
	public File getSerializedFile() {
		return this.f;
	}

	public void lock() {
		this.fLocked = true;
	}
	public void unlock() {
		this.fLocked = false;
	}
	
	@Override
	public synchronized void write(int b) {
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
	public synchronized void write(byte[] b, int off, int len) {
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
	public void write(byte[] b) throws IOException {
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
	
	@Override
	public void writeBytes(byte[] b) {
		if(this.soglia>0) {
			if( (this.attuale>this.soglia) || ((this.attuale+b.length)>this.soglia) ) {
				try {
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
		super.writeBytes(b);
	}
	
	@Override
	public synchronized void writeTo(OutputStream out) throws IOException {
		throw new IOException("NotImplemented");
	}

	@Override
	public synchronized void reset() {
		clearResources();
	}
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
	public synchronized byte[] toByteArray() {
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
	public synchronized String toString() {
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
	public synchronized String toString(String charsetName) throws UnsupportedEncodingException {
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
	public synchronized String toString(Charset charset) {
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
}
