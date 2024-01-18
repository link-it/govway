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

package org.openspcoop2.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.Date;

/**
 * TestCopyString
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestCopyCharStream {

	public static void main(String[] args) throws Exception {
		
		// init resources: 1GB
		int size = 1024*1024*1024;
		test(size);
		
		// init resources: 1MB
		size = 1024*1024;
		test(size);
		
		// init resources: 1KB
		size = 1024;
		test(size);
		
	}
	
	public static void test(int size) throws Exception {
		
		System.out.println("======================");
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			char c = 'a';
			sb.append(c);
		}
		
		CopyStreamMethod [] methods = CopyStreamMethod.values();
				
		System.out.println("Creato buffer di dimensione: "+Utilities.convertBytesToFormatString(sb.length(), true, " "));
				
		File fSRC = File.createTempFile("testCopyString", ".bin");
		try {
			FileWriter fos = new FileWriter(fSRC);
			fos.write(sb.toString());
			fos.flush();
			fos.close();
			System.out.println("Creato file di dimensione: "+Utilities.convertBytesToFormatString(fSRC.length(), true, " "));
		
		
			for (int i = 0; i < 2; i++) {
				String tipoTest = "wrap manuale";
				boolean wrapping = true;
				if(i==1) {
					tipoTest = "wrap da libreria";
					wrapping = false;
				}
		
				System.out.println("\n");
								
				for (CopyStreamMethod CopyStreamMethod : methods) {
					try (StringReader bin = new StringReader(sb.toString())){
						testBuffer("Buffer ("+tipoTest+")", CopyStreamMethod, bin, size, wrapping);
					}
				}
				
				System.out.println("\n");
				
				for (CopyStreamMethod CopyStreamMethod : methods) {
					try (FileReader fin = new FileReader(fSRC)){
						testBuffer("File ("+tipoTest+")", CopyStreamMethod, fin, size, wrapping);
					}
				}
			}
			testBuffer("File", fSRC, size);
			
			System.out.println("\n");
			
			for (CopyStreamMethod CopyStreamMethod : methods) {
				File fout = File.createTempFile("testCopyStringOut", ".bin");
				try {
					try (StringReader bin = new StringReader(sb.toString())){
						testFile("Buffer", CopyStreamMethod, bin, fout, size);
					}
				}finally {
					fout.delete();
				}
			}
			File fout = File.createTempFile("testCopyFileOut", ".bin");
			try {
				try (StringReader bin = new StringReader(sb.toString())){
					String path = fout.getAbsolutePath();
					fout.delete();
					testFile("Buffer", bin, new File(path), size);
				}
			}finally {
				fout.delete();
			}
			
			System.out.println("\n");
			
			for (CopyStreamMethod CopyStreamMethod : methods) {
				fout = File.createTempFile("testCopyStringOut", ".bin");
				try {
					try (FileReader fin = new FileReader(fSRC)){
						testFile("File", CopyStreamMethod, fin, fout, size);
					}
				}finally {
					fout.delete();
				}
			}
			fout = File.createTempFile("testCopyStringOut", ".bin");
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
	
	private static void testBuffer(String src, CopyStreamMethod method, Reader reader, int size, boolean wrapping) throws Exception {
		//System.out.println("["+src+"->Buffer]["+method+"] .... ");
		Date startDate = new Date();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		if(wrapping) {
			Writer writer = new OutputStreamWriter(bout);
			CopyCharStream.copy(method, reader, writer);
			bout.flush();
			writer.close();
		}
		else {
			CopyCharStream.copy(method, reader, bout);
		}
		bout.flush();
		bout.close();
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(bout.size()!=size) {
			throw new Exception("Buffer destinazione con dimensione differente (expected: "+size+", found: "+bout.size()+")");
		}
		System.out.println("["+src+"->Buffer]["+method+"] "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true));
	}
	
	private static void testBuffer(String src, File is, int size) throws Exception {
		//System.out.println("["+src+"->Buffer]["+method+"] .... ");
		Date startDate = new Date();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Writer writer = new OutputStreamWriter(bout);
		CopyCharStream.copy(is, writer);
		bout.flush();
		writer.close();
		bout.close();
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(bout.size()!=size) {
			throw new Exception("Buffer destinazione con dimensione differente (expected: "+size+", found: "+bout.size()+")");
		}
		System.out.println("["+src+"->Buffer][COPY-FILE] "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true));
	}
	
	private static void testFile(String src, CopyStreamMethod method, Reader reader, File f, int size) throws Exception {
		//System.out.println("["+src+"->File]["+method+"] .... ");
		Date startDate = new Date();
		FileOutputStream fout = new FileOutputStream(f);
		Writer writer = new OutputStreamWriter(fout);
		CopyCharStream.copy(method, reader, writer);
		fout.flush();
		writer.close();
		fout.close();
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(f.length()!=size) {
			throw new Exception("File destinazione con dimensione differente (expected: "+size+", found: "+f.length()+")");
		}
		System.out.println("["+src+"->File]["+method+"] "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true));
	}
	
	private static void testFile(String src, Reader reader, File f, int size) throws Exception {
		//System.out.println("["+src+"->File]["+method+"] .... ");
		Date startDate = new Date();
		CopyCharStream.copy(reader, f);
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(f.length()!=size) {
			throw new Exception("File destinazione con dimensione differente (expected: "+size+", found: "+f.length()+")");
		}
		System.out.println("["+src+"->File][COPY-FILE] "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true));
	}
	
	private static void testFile(String src, File is, File f, int size) throws Exception {
		//System.out.println("["+src+"->File]["+method+"] .... ");
		Date startDate = new Date();
		CopyCharStream.copy(is, f);
		Date endDate = new Date();
		long time = endDate.getTime() - startDate.getTime(); 
		if(f.length()!=size) {
			throw new Exception("File destinazione con dimensione differente (expected: "+size+", found: "+f.length()+")");
		}
		System.out.println("["+src+"->File][COPY-FILE] "+Utilities.convertSystemTimeIntoStringMillisecondi(time, true));
	}
}
