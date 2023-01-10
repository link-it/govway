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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * TestDumpByteArrayOutputStream
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TestDumpByteArrayOutputStream {

	public static void main(String [] args) throws Exception {

		testFastImpl();
		
		testDefaultImpl();

	}

	public static void testFastImpl() throws Exception {
		File dir = File.createTempFile("test", "");
		dir.delete();
		FileSystemUtilities.mkdir(dir);

		System.out.println("\n\n");
		// Default: DumpByteArrayOutputStream_FastImpl
		System.out.println("*** CLASSE: "+DumpByteArrayOutputStream.getClassImpl());
		if(!DumpByteArrayOutputStream_FastImpl.class.getName().equals(DumpByteArrayOutputStream.getClassImpl())) {
			throw new Exception("Attesa '"+DumpByteArrayOutputStream_FastImpl.class.getName()+"' trovata '"+DumpByteArrayOutputStream.getClassImpl()+"'");
		}
		test(dir);
		
		FileSystemUtilities.deleteDir(dir);
	}
	
	public static void testDefaultImpl() throws Exception {
		File dir = File.createTempFile("test", "");
		dir.delete();
		FileSystemUtilities.mkdir(dir);

		System.out.println("\n\n");
		String defaultClass = DumpByteArrayOutputStream.getClassImpl();
		try {
			DumpByteArrayOutputStream.setClassImpl(DumpByteArrayOutputStream_DefaultImpl.class.getName());
			System.out.println("*** CLASSE: "+DumpByteArrayOutputStream.getClassImpl());
			if(!DumpByteArrayOutputStream_DefaultImpl.class.getName().equals(DumpByteArrayOutputStream.getClassImpl())) {
				throw new Exception("Attesa '"+DumpByteArrayOutputStream_DefaultImpl.class.getName()+"' trovata '"+DumpByteArrayOutputStream.getClassImpl()+"'");
			}
			test(dir);
		}finally {
			DumpByteArrayOutputStream.setClassImpl(defaultClass);
		}

		FileSystemUtilities.deleteDir(dir);
	}
	
	private static void test(File dir) throws Exception {

		DumpByteArrayOutputStream dump = null;
		try {

			String testoSerializzato = "Hello World";

			StringBuilder testoLungo = new StringBuilder();
			for (int i = 0; i < (1024*100); i++) {
				testoLungo.append("Riga-").append(i).append("\n");
			}
			
			
			
			dump = new DumpByteArrayOutputStream(); // senza limiti
			
			dump.write(testoSerializzato.getBytes());
			//		dump.flush();
			//		dump.close(); // volutamente non chiudo

			test("A. write", testoSerializzato, false, dump);

			dump.clearResources();
			
			
			
			dump = new DumpByteArrayOutputStream(-1, null, null, null); // senza limiti con costruttore con parametri
			
			dump.write(testoSerializzato.getBytes());
			//		dump.flush();
			//		dump.close(); // volutamente non chiudo

			test("A2. write", testoSerializzato, false, dump);

			dump.clearResources();
			
			
			
			dump = new DumpByteArrayOutputStream(1024, dir, "3", "test");
			
			dump.write(testoSerializzato.getBytes());
			//		dump.flush();
			//		dump.close(); // volutamente non chiudo

			test("A. write", testoSerializzato, false, dump);

			dump.clearResources();


			dump = new DumpByteArrayOutputStream(1024, dir, "3", "test");
			dump.write(testoSerializzato.getBytes(),0,testoSerializzato.length());
			char c = '=';
			dump.write(c);
			dump.flush();
			dump.close();

			testoSerializzato = testoSerializzato + c; 

			test("B. write2", testoSerializzato, false, dump);

			dump.clearResources();



			dump = new DumpByteArrayOutputStream(5, dir, "3", "test");

			dump.write(testoSerializzato.getBytes());
			dump.flush();
			dump.close();

			test("C. scritturaSuFile", testoSerializzato, true, dump);

			dump.clearResources();
			
			
			
			String testoLungoAsString = testoLungo.toString();
			
			dump = new DumpByteArrayOutputStream(testoLungo.length()+1, dir, "3", "test");
			
			dump.write(testoLungoAsString.getBytes());
			dump.flush();
			dump.close();

			test("D. testoLungo in memoria", testoLungoAsString, false, dump);

			dump.clearResources();
			
			
			
			dump = new DumpByteArrayOutputStream(1024, dir, "3", "test");
			
			dump.write(testoLungoAsString.getBytes());
			dump.flush();
			dump.close();

			test("E. testoLungo su file", testoLungoAsString, true, dump);

			dump.clearResources();
			
			

		}finally {
			if(dump!=null) {
				dump.close();
			}
		}

	}

	private static void test(String tipoTest, String testoSerializzato, boolean expectedFile, DumpByteArrayOutputStream dump) throws Exception {

		System.out.println("\n\n");
		System.out.println("=====================================================");
		
		int size = dump.size();
		if(testoSerializzato.length()!=size) {
			throw new Exception("Atteso ["+testoSerializzato.length()+"] trovato ["+size+"]");
		}
		
		String s = dump.toString();
		System.out.println("@"+tipoTest+" AS STRING: ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		if(!testoSerializzato.equals(s)) {
			throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		}
		s = dump.toString(Charset.defaultCharset());
		System.out.println("@"+tipoTest+" AS STRING 2: ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		if(!testoSerializzato.equals(s)) {
			throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		}
		s = dump.toString(Charset.defaultCharset().name());
		System.out.println("@"+tipoTest+" AS STRING 3: ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		if(!testoSerializzato.equals(s)) {
			throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		}
		byte [] b = dump.toByteArray();
		String sb = new String(b);
		System.out.println("@"+tipoTest+" AS BYTE: ["+b.length+"] ["+(sb.length()>20 ? sb.substring(0, 20)+" ..." : sb)+"]");
		if(testoSerializzato.length()!=b.length) {
			throw new Exception("Atteso ["+testoSerializzato+":"+testoSerializzato.length()+"] trovato ["+(sb.length()>20 ? sb.substring(0, 20)+" ..." : sb)+":"+b.length+"]");
		}
		if(!testoSerializzato.equals(sb)) {
			throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		}

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		dump.writeTo(bout);
		bout.flush();
		bout.close();
		s = bout.toString();
		System.out.println("@"+tipoTest+" AS STRING after writeTo: ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		if(!testoSerializzato.equals(s)) {
			throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		}
		b = bout.toByteArray();
		sb = new String(b);
		System.out.println("@"+tipoTest+" AS BYTE after writeTo: ["+b.length+"] ["+(sb.length()>20 ? sb.substring(0, 20)+" ..." : sb)+"]");
		if(testoSerializzato.length()!=b.length) {
			throw new Exception("Atteso ["+testoSerializzato+":"+testoSerializzato.length()+"] trovato ["+(sb.length()>20 ? sb.substring(0, 20)+" ..." : sb)+":"+b.length+"]");
		}
		if(!testoSerializzato.equals(sb)) {
			throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		}

		File fileS = dump.getSerializedFile();
		System.out.println("@"+tipoTest+" FILE ["+fileS+"]");
		if(expectedFile && fileS==null) {
			throw new Exception("Atteso getSerializedFile");
		}
		else if(!expectedFile && fileS!=null)
			throw new Exception("Atteso null getSerializedFile; found: "+fileS.getAbsolutePath());
		
		boolean serF = dump.isSerializedOnFileSystem();
		if(expectedFile && !serF) {
			throw new Exception("Atteso isSerializedOnFileSystem");
		}
		else if(!expectedFile && serF)
			throw new Exception("Atteso false per isSerializedOnFileSystem");
		
		InputStream is = dump.getInputStream();
		s = Utilities.getAsString(is, "UTF-8");
		System.out.println("@"+tipoTest+" AS STRING from inputStream: ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		if(!testoSerializzato.equals(s)) {
			throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		}
		
		is = dump.getInputStream();
		b = Utilities.getAsByteArray(is);
		sb = new String(b);
		System.out.println("@"+tipoTest+" AS BYTE from inputStream: ["+b.length+"] ["+(sb.length()>20 ? sb.substring(0, 20)+" ..." : sb)+"]");
		if(testoSerializzato.length()!=b.length) {
			throw new Exception("Atteso ["+testoSerializzato+":"+testoSerializzato.length()+"] trovato ["+(sb.length()>20 ? sb.substring(0, 20)+" ..." : sb)+":"+b.length+"]");
		}
		if(!testoSerializzato.equals(sb)) {
			throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
		}
		
		size = dump.size();
		if(testoSerializzato.length()!=size) {
			throw new Exception("Atteso ["+testoSerializzato.length()+"] trovato ["+size+"]");
		}
		
		if(fileS!=null) {
			s = FileSystemUtilities.readFile(fileS);
			System.out.println("@"+tipoTest+" AS STRING from file: ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
			if(!testoSerializzato.equals(s)) {
				throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
			}
			b = FileSystemUtilities.readBytesFromFile(fileS);
			sb = new String(b);
			System.out.println("@"+tipoTest+" AS BYTE from file: ["+b.length+"] ["+(sb.length()>20 ? sb.substring(0, 20)+" ..." : sb)+"]");
			if(testoSerializzato.length()!=b.length) {
				throw new Exception("Atteso ["+testoSerializzato+":"+testoSerializzato.length()+"] trovato ["+(sb.length()>20 ? sb.substring(0, 20)+" ..." : sb)+":"+b.length+"]");
			}
			if(!testoSerializzato.equals(sb)) {
				throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
			}
			
			dump.lock();
			
			dump.clearResources();
			
			s = FileSystemUtilities.readFile(fileS);
			System.out.println("@"+tipoTest+" AS STRING from file: ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
			if(!testoSerializzato.equals(s)) {
				throw new Exception("Atteso ["+testoSerializzato+"] trovato ["+(s.length()>20 ? s.substring(0, 20)+" ..." : s)+"]");
			}
			
			dump.unlock();
			
			dump.clearResources();
			
			try {
				s = FileSystemUtilities.readFile(fileS);
				throw new Exception("Eccezione attesa FileNotFoundException non rilevata");
			}catch(FileNotFoundException notFound) {
				System.out.println("@"+tipoTest+" Eccezione attesa: "+notFound.getMessage());
			}

		}
		
	}

}
