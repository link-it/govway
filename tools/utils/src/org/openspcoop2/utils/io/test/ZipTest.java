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


package org.openspcoop2.utils.io.test;

import java.io.File;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;

/**
 * ZipTest
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ZipTest {

	public static void main(String[] args) throws Exception {
		test();
	}
	
	public static void test() throws Exception {

		// TEST zip unzip singolo contenuto
		String contenuto = "Hello World";
		byte [] test = ZipUtilities.zip(contenuto.getBytes(), "prova");
		File f = File.createTempFile("test", ".zip");
		FileSystemUtilities.writeFile(f, test);
		System.out.println("Zip File scritto in ["+f.getAbsolutePath()+"]");
		
		byte [] zipCompress = FileSystemUtilities.readBytesFromFile(f);
		byte [] decompress = ZipUtilities.unzip(zipCompress);
		String letto = new String(decompress);
		System.out.println("SCRITTO [	"+contenuto+"]");
		System.out.println("LETTO   ["+letto+"]");
		if(letto.equals(contenuto)) {
			System.out.println("UGUALI");
		}
		else {
			System.out.println("DIVERSO");
			throw new Exception("Diverso!");
		}
		
		// L'Enumeration ritornato dal metodo standard java.util.zip.ZipFile.entries() 
		// attraversa le entries presenti nello zip nello stesso ordine in cui sono state salvate.
		
		ZipFile zipFile = new ZipFile(new File(ZipTest.class.getResource("/org/openspcoop2/utils/io/test.zip").toURI()));
		
		System.out.println("=============================================");
		@SuppressWarnings("unchecked")
		Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zipFile.entries();
		while (en.hasMoreElements()) {
			ZipEntry zipEntry = (ZipEntry) en.nextElement();
			System.out.println("- "+zipEntry.getName());
		}
		
		System.out.println("\n\n\n============= ASC ================================");
		Iterator<ZipEntry> it = ZipUtilities.entries(zipFile, true);
		while (it.hasNext()) {
			ZipEntry zipEntry = (ZipEntry) it.next();
			System.out.println("- "+zipEntry.getName());
		}
		
		System.out.println("\n\n\n============= DESC ================================");
		it = ZipUtilities.entries(zipFile, false);
		while (it.hasNext()) {
			ZipEntry zipEntry = (ZipEntry) it.next();
			System.out.println("- "+zipEntry.getName());
		}
	}
	
}
