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

	public static void main(String[] args) throws Exception {
		
		System.out.println("======================");
		// init resources: 1GB
		int size = 1024*1024*1024;
		test(size);
		
		System.out.println("======================");
		// init resources: 1MB
		size = 1024*1024;
		test(size);
		
		System.out.println("======================");
		// init resources: 1KB
		size = 1024;
		test(size);
		
	}
	
	public static void test(int size) throws Exception {
		
		byte [] buffer = new byte[size];
		for (int i = 0; i < size; i++) {
			buffer[i] = 'a';
		}
		
		CopyStreamMethod [] methods = CopyStreamMethod.values();
				
		System.out.println("Creato buffer di dimensione: "+Utilities.convertBytesToFormatString(buffer.length, true, " "));
				
		File fSRC = File.createTempFile("testCopyStream", ".bin");
		try {
			FileOutputStream fos =new FileOutputStream(fSRC);
			fos.write(buffer);
			fos.flush();
			fos.close();
			System.out.println("Creato file di dimensione: "+Utilities.convertBytesToFormatString(fSRC.length(), true, " "));
		
		
			System.out.println("\n");
			
			for (CopyStreamMethod copyStreamMethod : methods) {
				try (ByteArrayInputStream bin = new ByteArrayInputStream(buffer)){
					testBuffer("Buffer", copyStreamMethod, bin, size);
				}
			}
			
			System.out.println("\n");
			
			for (CopyStreamMethod copyStreamMethod : methods) {
				try (FileInputStream fin = new FileInputStream(fSRC)){
					testBuffer("File", copyStreamMethod, fin, size);
				}
			}
			testBuffer("File", fSRC, size);
			
			System.out.println("\n");
			
			for (CopyStreamMethod copyStreamMethod : methods) {
				File fout = File.createTempFile("testCopyStreamOut", ".bin");
				try {
					try (ByteArrayInputStream bin = new ByteArrayInputStream(buffer)){
						testFile("Buffer", copyStreamMethod, bin, fout, size);
					}
				}finally {
					fout.delete();
				}
			}
			File fout = File.createTempFile("testCopyFileOut", ".bin");
			try {
				try (ByteArrayInputStream bin = new ByteArrayInputStream(buffer)){
					String path = fout.getAbsolutePath();
					fout.delete();
					testFile("Buffer", bin, new File(path), size);
				}
			}finally {
				fout.delete();
			}
			
			System.out.println("\n");
			
			for (CopyStreamMethod copyStreamMethod : methods) {
				fout = File.createTempFile("testCopyStreamOut", ".bin");
				try {
					try (FileInputStream fin = new FileInputStream(fSRC)){
						testFile("File", copyStreamMethod, fin, fout, size);
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
	
	private static void testBuffer(String src, CopyStreamMethod method, InputStream is, int size) throws Exception {
		//System.out.println("["+src+"->Buffer]["+method+"] .... ");
		Date startDate = new Date();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CopyStream.copy(method, is, bout);
		bout.flush();
		bout.close();
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(bout.size()!=size) {
			throw new Exception("Buffer destinazione con dimensione differente");
		}
		System.out.println("["+src+"->Buffer]["+method+"] "+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
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
			throw new Exception("Buffer destinazione con dimensione differente");
		}
		System.out.println("["+src+"->Buffer][COPY-FILE] "+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
	}
	
	private static void testFile(String src, CopyStreamMethod method, InputStream is, File f, int size) throws Exception {
		//System.out.println("["+src+"->File]["+method+"] .... ");
		Date startDate = new Date();
		FileOutputStream fout = new FileOutputStream(f);
		CopyStream.copy(method, is, fout);
		fout.flush();
		fout.close();
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(f.length()!=size) {
			throw new Exception("File destinazione con dimensione differente");
		}
		System.out.println("["+src+"->File]["+method+"] "+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
	}
	
	private static void testFile(String src, InputStream is, File f, int size) throws Exception {
		//System.out.println("["+src+"->File]["+method+"] .... ");
		Date startDate = new Date();
		CopyStream.copy(is, f);
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(f.length()!=size) {
			throw new Exception("File destinazione con dimensione differente");
		}
		System.out.println("["+src+"->File][COPY-FILE] "+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
	}
	
	private static void testFile(String src, File is, File f, int size) throws Exception {
		//System.out.println("["+src+"->File]["+method+"] .... ");
		Date startDate = new Date();
		CopyStream.copy(is, f);
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(f.length()!=size) {
			throw new Exception("File destinazione con dimensione differente");
		}
		System.out.println("["+src+"->File][COPY-FILE] "+Utilities.convertSystemTimeIntoString_millisecondi(time, true));
	}
}
