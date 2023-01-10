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
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.resources.Loader;

/**
* DumpByteArrayOutputStream
*
* @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class DumpByteArrayOutputStream extends OutputStream {

	private static String classImpl = DumpByteArrayOutputStream_FastImpl.class.getName();
	public static String getClassImpl() {
		return classImpl;
	}
	public static void setClassImpl(String classImpl) {
		DumpByteArrayOutputStream.classImpl = classImpl;
	}

	private OutputStream impl;
	private IDumpByteArrayOutputStream iImpl;
	
	public DumpByteArrayOutputStream() {
		// non e' attivo alcun controllo di soglia
		try {	
			Loader l = new Loader();
			this.impl = (OutputStream) l.newInstance_declaredConstructor(classImpl);
			this.iImpl = (IDumpByteArrayOutputStream) this.impl;
		}
		catch(Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	public DumpByteArrayOutputStream(int soglia, File repositoryFile, String idTransazione, String tipoMessaggio) {
		try {
			Loader l = new Loader();
			Integer s = soglia;
			if(s>0) {
				if(repositoryFile==null) {
					throw new Exception("RepositoryFile undefined");
				}
				this.impl = (OutputStream) l.newInstance_declaredConstructor(classImpl, s, repositoryFile, 
						idTransazione!=null ? idTransazione : getUniqueSerial(), 
						tipoMessaggio!=null ? tipoMessaggio : "dump");
			}
			else {
				// non e' attivo alcun controllo di soglia
				this.impl = (OutputStream) l.newInstance_declaredConstructor(classImpl);
			}
			this.iImpl = (IDumpByteArrayOutputStream) this.impl;
		}
		catch(Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static DumpByteArrayOutputStream newInstance(byte[] content) {
		DumpByteArrayOutputStream out = null;
		if(content!=null && content.length>0) {
			out = new DumpByteArrayOutputStream();
			if(content!=null && content.length>0) {
				try {
					out.write(content);
				}catch(Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
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
	protected static File newFile(File repositoryFile, String tipoMessaggio, String idTransazione) throws Exception {
		Date d = DateManager.getDate();
		SimpleDateFormat dateformatDir = DateUtils.getDefaultDateTimeFormatter(formatDir);
		String dateFormatDir = dateformatDir.format(d);
		File fData = new File(repositoryFile, dateFormatDir);			
		
		SimpleDateFormat dateformat = DateUtils.getDefaultDateTimeFormatter(formatNew);
		String dateFormat = dateformat.format(d);
		String nomeFile = prefix+tipoMessaggio+"_"+dateFormat+"_"+idTransazione.replaceAll("-", "_")+".bin";
		File f = new File(fData, nomeFile);
		FileSystemUtilities.mkdirParentDirectory(f);
		return f;
	}
	
	
	public boolean isSerializedOnFileSystem() {
		return this.iImpl.isSerializedOnFileSystem();
	}
	public File getSerializedFile() {
		return this.iImpl.getSerializedFile();
	}

	public void lock() {
		this.iImpl.lock();
	}
	public void unlock() {
		this.iImpl.unlock();
	}
	
	@Override
	public void write(int b) throws IOException {
		this.iImpl.writeInBuffer(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		this.iImpl.writeInBuffer(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		this.iImpl.writeInBuffer(b);
	}

	
	public void reset() {
		this.iImpl.reset();
	}
	public void clearResources() {
		this.iImpl.clearResources();
	}

	
	public int size() {
		return this.iImpl.size();
	}
	
	
	
	public void writeTo(OutputStream out) throws IOException {
		this.iImpl.writeTo(out);
	}

	public byte[] toByteArray() {
		return this.iImpl.serializeToByteArray();
	}

	@Override
	public String toString() {
		return this.iImpl.toString();
	}

	public String toString(String charsetName) throws UnsupportedEncodingException {
		return this.iImpl.serializeToString(charsetName);
	}

	public String toString(Charset charset) throws UnsupportedEncodingException {
		return this.iImpl.serializeToString(charset);
	}


	@Override
	public void close() throws IOException {
		this.iImpl.close();
	}


	@Override
	public void flush() throws IOException {
		this.iImpl.flush();
	}
	
	public InputStream getInputStream() {
		return this.iImpl.getInputStream();
	}
}
