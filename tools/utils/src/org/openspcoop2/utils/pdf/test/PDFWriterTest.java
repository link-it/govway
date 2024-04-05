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
package org.openspcoop2.utils.pdf.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.certificate.ArchiveType;
import org.openspcoop2.utils.certificate.KeyStore;
import org.openspcoop2.utils.certificate.test.KeystoreTest;
import org.openspcoop2.utils.pdf.PDFSigner;
import org.openspcoop2.utils.pdf.PDFWriter;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
* WriterTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class PDFWriterTest {

	public static void main(String[] args) throws Exception {
		
		testEmbedded();
		
		testMultipleEmbedded();
		
		testEmbeddedKid();
		
		testMultipleEmbeddedKids();
		
		testAnnotation();
		
		testMultipleAnnotation();
		
		testXFAFile(false); // !xfaDataSets
		
		testXFAFile(true); // xfaDataSets

		testSignature();
		
	}

	public static void testEmbedded() throws Exception {
		
		System.out.println("=====================");
		System.out.println("test add embeddedFile");
		
		File fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			w.addEmbeddedFile(PDFReaderTest.NOME_FILE_XML_EMBEDDED,isXML);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileEmbedded(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		System.out.println("\t- inputStream");
		
		fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			
			byte []content = Utilities.getAsByteArray(isXML);
			w.addEmbeddedFile(PDFReaderTest.NOME_FILE_XML_EMBEDDED, content);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileEmbedded(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		System.out.println("\t- byte []");
		
		fileOUTPUT = null;
		File fTest = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			
			fTest = new File("/tmp/"+PDFReaderTest.NOME_FILE_XML_EMBEDDED); 
			FileSystemUtilities.writeFile(fTest, Utilities.getAsByteArray(isXML));
			
			w.addEmbeddedFile(fTest);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileEmbedded(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
			if(fTest!=null) {
				java.nio.file.Files.delete(fTest.toPath());
			}
		}
		System.out.println("\t- file");
		
		System.out.println("test add embeddedFile completed");
		System.out.println("=====================");
		
	}
	
	public static void testMultipleEmbedded() throws Exception {
		
		System.out.println("=====================");
		System.out.println("test multiple add embeddedFile");
		
		File fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");
			InputStream isZIP = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.zip");
			InputStream isDOC = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.doc");){
			PDFWriter w = new PDFWriter(isPDF);
			
			w.addEmbeddedFile(PDFReaderTest.NOME_FILE_XML_MULTIPLE_EMBEDDED,isXML,HttpConstants.CONTENT_TYPE_XML);
			w.addEmbeddedFile(PDFReaderTest.NOME_FILE_ZIP_MULTIPLE_EMBEDDED,isZIP,HttpConstants.CONTENT_TYPE_ZIP);
			w.addEmbeddedFile(PDFReaderTest.NOME_FILE_DOC_MULTIPLE_EMBEDDED,isDOC,HttpConstants.CONTENT_TYPE_PLAIN);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileMultipleEmbedded(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		
		System.out.println("test multiple add embeddedFile completed");
		System.out.println("=====================");
		
	}
	
	public static void testEmbeddedKid() throws Exception {
		
		System.out.println("=====================");
		System.out.println("test add embeddedFile (kid)");
		
		File fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			w.addEmbeddedFileAsKid(PDFReaderTest.NOME_FILE_XML_EMBEDDED_KID,isXML);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileEmbeddedKid(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		System.out.println("\t- inputStream");
		
		fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			
			byte []content = Utilities.getAsByteArray(isXML);
			w.addEmbeddedFileAsKid(PDFReaderTest.NOME_FILE_XML_EMBEDDED_KID, content);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileEmbeddedKid(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		System.out.println("\t- byte []");
		
		fileOUTPUT = null;
		File fTest = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			
			fTest = new File("/tmp/"+PDFReaderTest.NOME_FILE_XML_EMBEDDED_KID); 
			FileSystemUtilities.writeFile(fTest, Utilities.getAsByteArray(isXML));
			
			w.addEmbeddedFileAsKid(fTest);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileEmbeddedKid(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
			if(fTest!=null) {
				java.nio.file.Files.delete(fTest.toPath());
			}
		}
		System.out.println("\t- file");
		
		System.out.println("test add embeddedFile (kid) completed");
		System.out.println("=====================");
		
	}
	
	public static void testMultipleEmbeddedKids() throws Exception {
		
		System.out.println("=====================");
		System.out.println("test multiple add embeddedFile (kids)");
		
		File fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");
			InputStream isZIP = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.zip");
			InputStream isDOC = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.doc");){
			PDFWriter w = new PDFWriter(isPDF);
			
			w.addEmbeddedFileAsKid(PDFReaderTest.NOME_FILE_XML_MULTIPLE_EMBEDDED_KID,isXML,HttpConstants.CONTENT_TYPE_XML);
			w.addEmbeddedFileAsKid(PDFReaderTest.NOME_FILE_ZIP_MULTIPLE_EMBEDDED_KID,isZIP,HttpConstants.CONTENT_TYPE_ZIP);
			w.addEmbeddedFileAsKid(PDFReaderTest.NOME_FILE_DOC_MULTIPLE_EMBEDDED_KID,isDOC,HttpConstants.CONTENT_TYPE_PLAIN);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileMultipleEmbeddedKids(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		
		System.out.println("test multiple add embeddedFile (kids) completed");
		System.out.println("=====================");
		
	}
	
	public static void testAnnotation() throws Exception {
		
		System.out.println("=====================");
		System.out.println("test add annotationFile");
		
		File fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			w.addAnnotationFile(PDFReaderTest.NOME_FILE_XML_ANNOTATION,isXML);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileAnnotation(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		System.out.println("\t- inputStream");
		
		fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			
			byte []content = Utilities.getAsByteArray(isXML);
			w.addAnnotationFile(PDFReaderTest.NOME_FILE_XML_ANNOTATION, content);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileAnnotation(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		System.out.println("\t- byte []");
		
		fileOUTPUT = null;
		File fTest = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			
			fTest = new File("/tmp/"+PDFReaderTest.NOME_FILE_XML_ANNOTATION); 
			FileSystemUtilities.writeFile(fTest, Utilities.getAsByteArray(isXML));
			
			w.addAnnotationFile(fTest);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileAnnotation(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
			if(fTest!=null) {
				java.nio.file.Files.delete(fTest.toPath());
			}
		}
		System.out.println("\t- file");
		
		System.out.println("test add annotationFile completed");
		System.out.println("=====================");
		
	}
	
	public static void testMultipleAnnotation() throws Exception {
		
		System.out.println("=====================");
		System.out.println("test multiple add annotationFile");
		
		File fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");
			InputStream isZIP = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.zip");
			InputStream isDOC = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.doc");){
			PDFWriter w = new PDFWriter(isPDF);
				
			w.addAnnotationFile(PDFReaderTest.NOME_FILE_XML_MULTIPLE_ANNOTATION,isXML,HttpConstants.CONTENT_TYPE_XML);
			w.addAnnotationFile(PDFReaderTest.NOME_FILE_ZIP_MULTIPLE_ANNOTATION,isZIP,HttpConstants.CONTENT_TYPE_ZIP);
			w.addAnnotationFile(PDFReaderTest.NOME_FILE_DOC_MULTIPLE_ANNOTATION,isDOC,HttpConstants.CONTENT_TYPE_PLAIN);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileMultipleAnnotations(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		
		System.out.println("test multiple add annotationFile completed");
		System.out.println("=====================");
		
	}
	
	
	public static void testXFAFile(boolean xfaDataSets) throws Exception {
		
		System.out.println("=====================");
		System.out.println("test add xfaFile (datasets:"+xfaDataSets+")");
		
		File fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			w.setXFAFile(isXML, xfaDataSets);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileXFA(fin, xfaDataSets);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		System.out.println("\t- inputStream");
		
		fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			
			byte []content = Utilities.getAsByteArray(isXML);
			w.setXFAFile(content, xfaDataSets);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileXFA(fin, xfaDataSets);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		System.out.println("\t- byte []");
		
		fileOUTPUT = null;
		File fTest = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");
			InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			PDFWriter w = new PDFWriter(isPDF);
			
			fTest = new File("/tmp/"+PDFReaderTest.NOME_FILE_XML_XFA); 
			FileSystemUtilities.writeFile(fTest, Utilities.getAsByteArray(isXML));
			
			w.setXFAFile(fTest, xfaDataSets);
			
			fileOUTPUT = File.createTempFile("output", ".pdf");
			w.writeTo(fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileXFA(fin, xfaDataSets);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
			if(fTest!=null) {
				java.nio.file.Files.delete(fTest.toPath());
			}
		}
		System.out.println("\t- file");
		
		System.out.println("test add xfaFile (datasets:"+xfaDataSets+") completed");
		System.out.println("=====================");
		
	}
	
	
	
	public static void testSignature() throws Exception {
		
		System.out.println("=====================");
		System.out.println("test signature");
		
		KeyStore keystore = new KeyStore(Utilities.getAsByteArray(KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+"govway_test.jks")), ArchiveType.JKS.name(), KeystoreTest.PASSWORD);
		
		File fileOUTPUT = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");){
			
			PDFSigner pdfSigner = new PDFSigner(isPDF, PDFReaderTest.SIGNATURE_NAME, PDFReaderTest.SIGNATURE_FILTER, PDFReaderTest.SIGNATURE_SUBFILTER, 
					PDFReaderTest.SIGNATURE_LOCALITY, PDFReaderTest.SIGNATURE_REASON, PDFReaderTest.SIGNATURE_CONTACT_INFO);
		
			fileOUTPUT = File.createTempFile("output", ".pdf");
			pdfSigner.sign(keystore, KeystoreTest.ALIAS_1, KeystoreTest.PASSWORD, fileOUTPUT);
			
			try(FileInputStream fin = new FileInputStream(fileOUTPUT)){
				PDFReaderTest.verificaFileSignature(fin);
			}
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
		}
		System.out.println("\t- inputStream");
		
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");){
			
			byte []content = Utilities.getAsByteArray(isPDF);
			PDFSigner pdfSigner = new PDFSigner(content, PDFReaderTest.SIGNATURE_NAME, PDFReaderTest.SIGNATURE_FILTER, PDFReaderTest.SIGNATURE_SUBFILTER, 
					PDFReaderTest.SIGNATURE_LOCALITY, PDFReaderTest.SIGNATURE_REASON, PDFReaderTest.SIGNATURE_CONTACT_INFO);
		
			byte [] contentSigned = pdfSigner.sign(keystore, KeystoreTest.ALIAS_1, KeystoreTest.PASSWORD);
			
			PDFReaderTest.verificaFileSignature(contentSigned);
		} 
		System.out.println("\t- byte []");
		
		fileOUTPUT = null;
		File fTest = null;
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.pdf");){
			
			fTest = new File("/tmp/"+PDFReaderTest.NOME_FILE_XML_EMBEDDED); 
			FileSystemUtilities.writeFile(fTest, Utilities.getAsByteArray(isPDF));
			
			PDFSigner pdfSigner = new PDFSigner(fTest, PDFReaderTest.SIGNATURE_NAME, PDFReaderTest.SIGNATURE_FILTER, PDFReaderTest.SIGNATURE_SUBFILTER, 
					PDFReaderTest.SIGNATURE_LOCALITY, PDFReaderTest.SIGNATURE_REASON, PDFReaderTest.SIGNATURE_CONTACT_INFO);
		
			fileOUTPUT = File.createTempFile("output", ".pdf");
			pdfSigner.sign(keystore, KeystoreTest.ALIAS_1, KeystoreTest.PASSWORD, fileOUTPUT);
			
			PDFReaderTest.verificaFileSignature(fileOUTPUT);
		} 
		finally {
			if(fileOUTPUT!=null) {
				java.nio.file.Files.delete(fileOUTPUT.toPath());
			}
			if(fTest!=null) {
				java.nio.file.Files.delete(fTest.toPath());
			}
		}
		System.out.println("\t- file");
		
		System.out.println("test signature completed");
		System.out.println("=====================");
		
	}
}
