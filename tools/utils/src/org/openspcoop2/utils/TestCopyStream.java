/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;

/**
 * TestCopyStream
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestCopyStream {

	/** Ritardo introdotto ad ogni lettura dallo stream sorgente nei casi in cui ci si attende il timeout.
	 *  Deve essere maggiore del timeout impostato in quei casi (2 ms). */
	private static final int RITARDO_SORGENTE_MS = 20;

	/** Stream che introduce un ritardo ad ogni lettura.
	 *
	 *  I casi che si attendono il timeout non possono affidarsi al tempo impiegato a copiare il payload: su una macchina
	 *  sufficientemente veloce la copia di 10 MB si conclude in circa 2,6 ms, cioe' prima che il controllo del timeout
	 *  possa rilevare il superamento della soglia, ed il test fallisce in modo non deterministico a seconda dell'hardware,
	 *  del carico e dello stato della page cache. Interponendo questo stream il tempo trascorso e' garantito: il controllo
	 *  effettuato prima della seconda lettura rileva il superamento della soglia ed il trasferimento viene interrotto,
	 *  quindi il ritardo viene di fatto pagato una sola volta per ogni stream. */
	private static class SlowInputStream extends InputStream {

		private final InputStream isWrapped;

		private SlowInputStream(InputStream isWrapped){
			this.isWrapped = isWrapped;
		}

		private void ritarda() {
			Utilities.sleep(RITARDO_SORGENTE_MS);
		}

		@Override
		public int read() throws java.io.IOException {
			ritarda();
			return this.isWrapped.read();
		}
		@Override
		public int read(byte[] b) throws java.io.IOException {
			ritarda();
			return this.isWrapped.read(b);
		}
		@Override
		public int read(byte[] b, int off, int len) throws java.io.IOException {
			ritarda();
			return this.isWrapped.read(b, off, len);
		}
		@Override
		public int available() throws java.io.IOException {
			return this.isWrapped.available();
		}
		@Override
		public long skip(long n) throws java.io.IOException {
			return this.isWrapped.skip(n);
		}
		@Override
		public void close() throws java.io.IOException {
			this.isWrapped.close();
		}
	}

	/** Restituisce lo stream sorgente, rallentandolo nei soli casi in cui ci si attende il timeout */
	private static InputStream sorgente(InputStream is, boolean expectedTimeout){
		if(expectedTimeout) {
			return new SlowInputStream(is);
		}
		return is;
	}


	public static void main(String[] args) throws Exception {
		
		
		// init resources: 1GB
		int size = 1024*1024*1024;
		
		test(size, -1, false, -1, false);
		
		int timeoutMs = 120000;
		test(size, timeoutMs, false, size, false);
		test(size, 60, true, -1, false); // atteso timeout dopo 60ms
		test(size, -1,false, 1024*10, true); // atteso timeout dopo 10K
		test(size, 6000, false, 1024*10, true); // atteso timeout dopo 10K
		test(size, 60, true, 1024*1024*1024, false); // atteso timeout dopo 60ms
		test(size, 2, true, 1024*1024*1024, false); // atteso timeout dopo 2ms
		
		// init resources: 1MB
		size = 1024*1024;
		test(size, timeoutMs, false, size, false);
		
		// init resources: 1KB
		size = 1024;
		test(size, timeoutMs, false, size, false);
		
		System.out.println("\n\n\nTESTSUITE COMPLETATA");
		
	}
	
	public static void test(int size, 
			int timeoutMs, boolean expectedTimeout,
			long limitBytes, boolean expectedLimitExceeded) throws Exception {
		
		System.out.println("\n\n========= (timeoutMs:"+timeoutMs+") (limit:"+limitBytes+") =============");
		
		byte [] buffer = new byte[size];
		for (int i = 0; i < size; i++) {
			buffer[i] = 'a';
		}
		
		CopyStreamMethod [] methods = CopyStreamMethod.values();
//		CopyStreamMethod [] methods = new CopyStreamMethod[1];
//		methods[0] = CopyStreamMethod.JAVA_TRANSFER_TO;
		
		System.out.println("Creato buffer di dimensione: "+Utilities.convertBytesToFormatString(buffer.length, true, " "));
				
		File fSRC = File.createTempFile("testCopyStream", ".bin");
		try {
			FileOutputStream fos =new FileOutputStream(fSRC);
			fos.write(buffer);
			fos.flush();
			fos.close();
			System.out.println("Creato file di dimensione: "+Utilities.convertBytesToFormatString(fSRC.length(), true, " "));
		

			// Il primo giro impiega più tempo (per qualsiasi metodo, impatta anche su transfer)
			byte [] bufferInit = new byte[Utilities.DIMENSIONE_BUFFER];
			for (int i = 0; i < Utilities.DIMENSIONE_BUFFER; i++) {
				bufferInit[i] = 'a';
			}
			try (ByteArrayInputStream bin = new ByteArrayInputStream(bufferInit)){
				testBuffer("Buffer-Java-InitResources", CopyStreamMethod.JAVA, bin, Utilities.DIMENSIONE_BUFFER, 
						-1, false, 
						-1, false);
			}
						
			
			System.out.println("\n");
			
			for (CopyStreamMethod copyStreamMethod : methods) {
				
				try (InputStream bin = sorgente(new ByteArrayInputStream(buffer), expectedTimeout)){
					testBuffer("Buffer", copyStreamMethod, bin, size, 
							timeoutMs, expectedTimeout,
							limitBytes, expectedLimitExceeded);
				}
				
				if(CopyStreamMethod.JAVA.equals(copyStreamMethod)) {
					try (InputStream bin = sorgente(new ByteArrayInputStream(buffer), expectedTimeout)){
						testBuffer("Buffer-Java-IterazioneBufferSize8192", copyStreamMethod, bin, size, 8192, 
								timeoutMs, expectedTimeout,
								limitBytes, expectedLimitExceeded);
					}
				}
			}
			
			System.out.println("\n");
			
			for (CopyStreamMethod copyStreamMethod : methods) {
				try (InputStream fin = sorgente(new FileInputStream(fSRC), expectedTimeout)){
					testBuffer("File", copyStreamMethod, fin, size, 
							timeoutMs, expectedTimeout,
							limitBytes, expectedLimitExceeded);
				}
			}
			testBuffer("File", fSRC, size);
			
			System.out.println("\n");
			
			for (CopyStreamMethod copyStreamMethod : methods) {
				File fout = File.createTempFile("testCopyStreamOut", ".bin");
				try {
					try (InputStream bin = sorgente(new ByteArrayInputStream(buffer), expectedTimeout)){
						testFile("Buffer", copyStreamMethod, bin, fout, size, 
								timeoutMs, expectedTimeout,
								limitBytes, expectedLimitExceeded);
					}
				}finally {
					fout.delete();
				}
			}
			File fout = File.createTempFile("testCopyFileOut", ".bin");
			try {
				try (InputStream bin = sorgente(new ByteArrayInputStream(buffer), expectedTimeout)){
					String path = fout.getAbsolutePath();
					fout.delete();
					testFile("Buffer", bin, new File(path), size, 
							timeoutMs, expectedTimeout,
							limitBytes, expectedLimitExceeded);
				}
			}finally {
				fout.delete();
			}
			
			System.out.println("\n");
			
			for (CopyStreamMethod copyStreamMethod : methods) {
				fout = File.createTempFile("testCopyStreamOut", ".bin");
				try {
					try (InputStream fin = sorgente(new FileInputStream(fSRC), expectedTimeout)){
						testFile("File", copyStreamMethod, fin, fout, size, 
								timeoutMs, expectedTimeout,
								limitBytes, expectedLimitExceeded);
					}
				}finally {
					fout.delete();
				}
			}
			fout = File.createTempFile("testCopyStreamOut", ".bin");
			try {
				String path = fout.getAbsolutePath();
				fout.delete();
				testFile("File", fSRC, new File(path), size);
			}finally {
				fout.delete();
			}
			
		}finally {
			fSRC.delete();
		}
		
	}
	
	private static void testBuffer(String src, CopyStreamMethod method, InputStream is, int size, 
			int timeout, boolean expectedTimeout,
			long limitBytes, boolean expectedLimitExceeded) throws Exception {
		testBuffer(src, method, is, size, -1, 
				timeout, expectedTimeout,
				limitBytes, expectedLimitExceeded);
	}
	private static void testBuffer(String src, CopyStreamMethod method, InputStream isParam, int size, int sizeBuffer, 
			int timeout, boolean expectedTimeout,
			long limitBytes, boolean expectedLimitExceeded) throws Exception {
		//System.out.println("["+src+"->Buffer]["+method+"] .... ");
		Date startDate = new Date();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		InputStream is = isParam;
		if(limitBytes>0) {
			is = new LimitedInputStream(is, limitBytes);
		}
		if(timeout>0) {
			is = new TimeoutInputStream(is, timeout);
		}
		try {
			if(sizeBuffer>0) {
				CopyStream.copyBuffer(is, bout, sizeBuffer);
			}
			else {
				CopyStream.copy(method, is, bout);
			}
			if(expectedTimeout) {
				Date endDate = new Date();
				long time = endDate.getTime() - startDate.getTime(); 
				throw new Exception("["+src+"->Buffer]["+method+"] Eccezione attesa di timeout non si è verificata dopo "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true)+"; buffer expected: "+size+", found: "+bout.size()+"");
			}
			else if(expectedLimitExceeded) {
				Date endDate = new Date();
				long time = endDate.getTime() - startDate.getTime(); 
				throw new Exception("["+src+"->Buffer]["+method+"] Eccezione attesa 'limit exceeded' non si è verificata dopo "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true)+"; buffer expected: "+size+", found: "+bout.size()+"");
			}
		}catch(Exception e) {
			if(expectedTimeout && e.getMessage().equals(TimeoutInputStream.ERROR_MSG)) {
				System.out.println("["+src+"->Buffer]["+method+"] eccezione attesa ricevuta: "+e.getMessage());
			}
			else if(expectedLimitExceeded && e.getMessage().equals(LimitedInputStream.ERROR_PAYLOAD_TOO_LARGE_MSG)) {
				System.out.println("["+src+"->Buffer]["+method+"] eccezione attesa ricevuta: "+e.getMessage());
			}
			else if(expectedLimitExceeded && e.getMessage().equals(LimitedInputStream.ERROR_CONTENT_LENGTH_EXCEEDED_MSG)) {
				System.out.println("["+src+"->Buffer]["+method+"] eccezione attesa ricevuta: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		bout.flush();
		bout.close();
		if(!expectedTimeout && !expectedLimitExceeded) {
			Date endDate = new Date();
			long time = endDate.getTime() - startDate.getTime(); 
			if(bout.size()!=size) {
				throw new Exception("["+src+"->Buffer]["+method+"] Buffer destinazione con dimensione differente (expected: "+size+", found: "+bout.size()+")");
			}
			System.out.println("["+src+"->Buffer]["+method+"] "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true));
		}
	}
	
	private static void testBuffer(String src, File is, int size) throws Exception {
		//System.out.println("["+src+"->Buffer]["+method+"] .... ");
		Date startDate = new Date();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CopyStream.copy(is, bout);
		bout.flush();
		bout.close();
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(bout.size()!=size) {
			throw new Exception("["+src+"->Buffer][COPY-FILE] Buffer destinazione con dimensione differente (expected: "+size+", found: "+bout.size()+")");
		}
		System.out.println("["+src+"->Buffer][COPY-FILE] "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true));
	}
	
	private static void testFile(String src, CopyStreamMethod method, InputStream is, File f, int size, 
			int timeout, boolean expectedTimeout,
			long limitBytes, boolean expectedLimitExceeded) throws Exception {
		//System.out.println("["+src+"->File]["+method+"] .... ");
		Date startDate = new Date();
		FileOutputStream fout = new FileOutputStream(f);
		try {
			if(timeout>0 && limitBytes>0) {
				CopyStream.copy(method, is, fout, timeout, limitBytes);
			}
			else if(timeout>0) {
				CopyStream.copy(method, is, fout, timeout);
			}
			else if(limitBytes>0) {
				CopyStream.copy(method, is, fout, limitBytes);
			}
			else {
				CopyStream.copy(method, is, fout);
			}
			if(expectedTimeout) {
				Date endDate = new Date();
				long time = endDate.getTime() - startDate.getTime(); 
				throw new Exception("["+src+"->File]["+method+"] Eccezione attesa di timeout non si è verificata dopo "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true)+"; buffer expected: "+size+", found: "+f.length()+"");
			}
			if(expectedLimitExceeded) {
				Date endDate = new Date();
				long time = endDate.getTime() - startDate.getTime(); 
				throw new Exception("["+src+"->File]["+method+"] Eccezione attesa 'limit exceeded' non si è verificata dopo "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true)+"; buffer expected: "+size+", found: "+f.length()+"");
			}
		}catch(Exception e) {
			if(expectedTimeout && e.getMessage().equals(TimeoutInputStream.ERROR_MSG)) {
				System.out.println("["+src+"->File]["+method+"] eccezione attesa ricevuta: "+e.getMessage());
			}
			else if(expectedLimitExceeded && e.getMessage().equals(LimitedInputStream.ERROR_PAYLOAD_TOO_LARGE_MSG)) {
				System.out.println("["+src+"->File]["+method+"] eccezione attesa ricevuta: "+e.getMessage());
			}
			else if(expectedLimitExceeded && e.getMessage().equals(LimitedInputStream.ERROR_CONTENT_LENGTH_EXCEEDED_MSG)) {
				System.out.println("["+src+"->File]["+method+"] eccezione attesa ricevuta: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		fout.flush();
		fout.close();
		if(!expectedTimeout && !expectedLimitExceeded) {
			Date endDate = new Date();
			long time = endDate.getTime() - startDate.getTime(); 
			if(f.length()!=size) {
				throw new Exception("["+src+"->File]["+method+"] File destinazione con dimensione differente (expected: "+size+", found: "+f.length()+")");
			}
			System.out.println("["+src+"->File]["+method+"] "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true));
		}
	}
	
	private static void testFile(String src, InputStream is, File f, int size, 
			int timeout, boolean expectedTimeout,
			long limitBytes, boolean expectedLimitExceeded) throws Exception {
		//System.out.println("["+src+"->File]["+method+"] .... ");
		Date startDate = new Date();
		try {
			if(timeout>0 && limitBytes>0) {
				CopyStream.copy(is, f, timeout, limitBytes);
			}
			else if(timeout>0) {
				CopyStream.copy(is, f, timeout);
			}
			else if(limitBytes>0) {
				CopyStream.copy(is, f, limitBytes);
			}
			else {
				CopyStream.copy(is, f);
			}
			if(expectedTimeout) {
				Date endDate = new Date();
				long time = endDate.getTime() - startDate.getTime(); 
				throw new Exception("["+src+"->File][COPY-FILE] Eccezione attesa di timeout non si è verificata dopo "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true)+"; buffer expected: "+size+", found: "+f.length()+"");
			}
			if(expectedLimitExceeded) {
				Date endDate = new Date();
				long time = endDate.getTime() - startDate.getTime(); 
				throw new Exception("["+src+"->File][COPY-FILE] Eccezione attesa 'limit exceeded' non si è verificata dopo "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true)+"; buffer expected: "+size+", found: "+f.length()+"");
			}
		}catch(Exception e) {
			if(expectedTimeout && e.getMessage().equals(TimeoutInputStream.ERROR_MSG)) {
				System.out.println("["+src+"->File][COPY-FILE] eccezione attesa ricevuta: "+e.getMessage());
			}
			else if(expectedLimitExceeded && e.getMessage().equals(LimitedInputStream.ERROR_PAYLOAD_TOO_LARGE_MSG)) {
				System.out.println("["+src+"->File][COPY-FILE] eccezione attesa ricevuta: "+e.getMessage());
			}
			else if(expectedLimitExceeded && e.getMessage().equals(LimitedInputStream.ERROR_CONTENT_LENGTH_EXCEEDED_MSG)) {
				System.out.println("["+src+"->File][COPY-FILE] eccezione attesa ricevuta: "+e.getMessage());
			}
			else {
				throw e;
			}
		}
		if(!expectedTimeout && !expectedLimitExceeded) {
			Date endDate = new Date();
			long time = endDate.getTime() - startDate.getTime(); 
			if(f.length()!=size) {
				throw new Exception("["+src+"->File][COPY-FILE] File destinazione con dimensione differente (expected: "+size+", found: "+f.length()+")");
			}
			System.out.println("["+src+"->File][COPY-FILE] "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true));
		}
	}
	
	private static void testFile(String src, File is, File f, int size) throws Exception {
		//System.out.println("["+src+"->File]["+method+"] .... ");
		Date startDate = new Date();
		CopyStream.copy(is, f);
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(f.length()!=size) {
			throw new Exception("["+src+"->File][COPY-FILE] File destinazione con dimensione differente (expected: "+size+", found: "+f.length()+")");
		}
		System.out.println("["+src+"->File][COPY-FILE] "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true));
	}
}
