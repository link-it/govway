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
package org.openspcoop2.utils.pdf.test;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.certificate.test.KeystoreTest;
import org.openspcoop2.utils.pdf.EmbeddedFile;
import org.openspcoop2.utils.pdf.PDFReader;
import org.openspcoop2.utils.pdf.PDFSignature;
import org.openspcoop2.utils.pdf.PDFSignerInformation;
import org.openspcoop2.utils.pdf.XFADocument;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.xml.XMLDiff;
import org.openspcoop2.utils.xml.XMLDiffImplType;
import org.openspcoop2.utils.xml.XMLDiffOptions;
import org.openspcoop2.utils.xml.XMLUtils;
import org.w3c.dom.Document;

/**
* PDFReaderTest
*
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class PDFReaderTest {

	public static void main(String[] args) throws Exception {

		testEmbedded();
		
		testMultipleEmbedded();
		
		testEmbeddedKid();
		
		testMultipleEmbeddedKids();
		
		testAnnotation();
		
		testMultipleAnnotations();

		testXFAFile();
		
		testXFAFileDatasets();
		
		testSignature();
	}

	public static void testEmbedded() throws Exception {
		
		System.out.println("\n=====================");
		System.out.println("test read embeddedFile ...");
		
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld_fileEmbedded.pdf");){
			verificaFileEmbedded(isPDF);
		} 
		
		System.out.println("test read embeddedFile completed");
		System.out.println("=====================");
		
	}
	
	public static void testMultipleEmbedded() throws Exception {
		
		System.out.println("\n=====================");
		System.out.println("test read multiple embeddedFile ...");
		
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld_fileMultipleEmbedded.pdf");){
			verificaFileMultipleEmbedded(isPDF);
		} 
		
		System.out.println("test read multiple embeddedFile completed");
		System.out.println("=====================");
		
	}
	
	public static void testEmbeddedKid() throws Exception {
		
		System.out.println("\n=====================");
		System.out.println("test read embeddedFile (kid) ...");
		
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld_fileEmbeddedKid.pdf");){
			verificaFileEmbeddedKid(isPDF);
		} 
		
		System.out.println("test read embeddedFile (kid) completed");
		System.out.println("=====================");
		
	}
	
	public static void testMultipleEmbeddedKids() throws Exception {
		
		System.out.println("\n=====================");
		System.out.println("test read multiple embeddedFile (kids) ...");
		
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld_fileMultipleEmbeddedKids.pdf");){
			verificaFileMultipleEmbeddedKids(isPDF);
		} 
		
		System.out.println("test read multiple embeddedFile (kids) completed");
		System.out.println("=====================");
		
	}
	
	public static void testAnnotation() throws Exception {
		
		System.out.println("\n=====================");
		System.out.println("test read annotation ...");
		
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld_fileAnnotation.pdf");){
			verificaFileAnnotation(isPDF);
		} 
		
		System.out.println("test read annotation completed");
		System.out.println("=====================");
		
	}
	
	public static void testMultipleAnnotations() throws Exception {
		
		System.out.println("\n=====================");
		System.out.println("test read multiple annotationFile ...");
		
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld_fileMultipleAnnotations.pdf");){
			verificaFileMultipleAnnotations(isPDF);
		} 
		
		System.out.println("test read multiple annotationFile completed");
		System.out.println("=====================");
		
	}
	
	public static void testXFAFile() throws Exception {
		testXFAFile(false, "HelloWorld_fileXFA.pdf");
	}
	public static void testXFAFileDatasets() throws Exception {
		testXFAFile(true, "HelloWorld_fileXFA_datasets.pdf");
	}
	private static void testXFAFile(boolean xfaDataSets, String fileName) throws Exception {
		
		System.out.println("\n=====================");
		System.out.println("test read xfaFile (datasets:"+xfaDataSets+") ...");
		
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/"+fileName);){
			verificaFileXFA(isPDF, xfaDataSets);
		} 
		
		System.out.println("test read xfaFile (datasets:"+xfaDataSets+") completed");
		System.out.println("=====================");
		
	}
	
	
	public static void testSignature() throws Exception {
		
		System.out.println("\n=====================");
		System.out.println("test signature ...");
		
		try(InputStream isPDF = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld_signed.pdf");){
			verificaFileSignature(isPDF);
		} 
		
		System.out.println("test signature completed");
		System.out.println("=====================");
		
	}
	
	
	protected static final String NOME_FILE_XML_EMBEDDED = "EsempioFile.xml";
	public static void verificaFileEmbedded(InputStream isPDF) throws Exception {
		try(InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			
			PDFReader r = new PDFReader(isPDF);
			
			Map<String, EmbeddedFile> map = r.getEmbeddedFiles(false);
			if(map==null || map.isEmpty()) {
				throw new Exception("Embedded files non trovati");
			}
			if(map.size()!=1) {
				throw new Exception("Atteso 1 embedded file, trovati "+map.size()+" files");
			}
			String fileName = map.keySet().iterator().next();
			String atteso = NOME_FILE_XML_EMBEDDED;
			if(!atteso.equals(fileName)) {
				throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+fileName+"'");
			}
			
			EmbeddedFile ef = map.get(fileName);
			if(ef==null) {
				throw new Exception("Embedded file is null");
			}
			
			if(!atteso.equals(ef.getFilename())) {
				throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getFilename()+"'");
			}
			
			atteso = HttpConstants.CONTENT_TYPE_TEXT_XML;
			if(!atteso.equals(ef.getMediaType())) {
				throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getMediaType()+"'");
			}
			
			byte [] attesoContent = Utilities.getAsByteArray(isXML);
			if(!Arrays.equals(attesoContent, ef.getContent())) {
				throw new Exception("Rilevato embedded file con un contenuto diverso da quello atteso");
			}
			
			if(ef.getEmbeddedFile()==null) {
				throw new Exception("Oggetto EmbeddedFile non trovato");
			}
			if(ef.getFileSpec()==null) {
				throw new Exception("Oggetto FileSpec non trovato");
			}
				
		} 
	}
	
	
	protected static final String NOME_FILE_XML_MULTIPLE_EMBEDDED = "EsempioContenutoXML.xml";
	protected static final String NOME_FILE_ZIP_MULTIPLE_EMBEDDED = "EsempioArchivio.zip";
	protected static final String NOME_FILE_DOC_MULTIPLE_EMBEDDED = "README.doc";
	public static void verificaFileMultipleEmbedded(InputStream isPDF) throws Exception {
		try(InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");
			InputStream isZIP = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.zip");
			InputStream isDOC = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.doc");){
			
			PDFReader r = new PDFReader(isPDF);
			
			Map<String, EmbeddedFile> map = r.getEmbeddedFiles(false);
			if(map==null || map.isEmpty()) {
				throw new Exception("Embedded files non trovati");
			}
			if(map.size()!=3) {
				throw new Exception("Atteso 3 embedded files, trovati "+map.size()+" files");
			}
			
			boolean xml = false;
			boolean zip = false;
			boolean doc = false;
			
			for (String fileName : map.keySet()) {
				
				String tipo = null;
				
				if(NOME_FILE_XML_MULTIPLE_EMBEDDED.equals(fileName)) {
					xml = true;
					tipo = "XML";
				}
				else if(NOME_FILE_ZIP_MULTIPLE_EMBEDDED.equals(fileName)) {
					zip = true;
					tipo = "ZIP";
				}
				else if(NOME_FILE_DOC_MULTIPLE_EMBEDDED.equals(fileName)) {
					doc = true;
					tipo = "DOC";
				}
				else {
					throw new Exception("File '"+fileName+"' unknown");
				}
				
				String atteso = null;
				if("XML".equals(tipo)) {
					atteso = NOME_FILE_XML_MULTIPLE_EMBEDDED;
				}
				else if("ZIP".equals(tipo)) {
					atteso = NOME_FILE_ZIP_MULTIPLE_EMBEDDED;
				}
				else {
					atteso = NOME_FILE_DOC_MULTIPLE_EMBEDDED;
				}
				
				if(!atteso.equals(fileName)) {
					throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+fileName+"'");
				}
				
				EmbeddedFile ef = map.get(fileName);
				if(ef==null) {
					throw new Exception("Embedded file is null");
				}
				
				if(!atteso.equals(ef.getFilename())) {
					throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getFilename()+"'");
				}
				
				if("XML".equals(tipo)) {
					atteso = HttpConstants.CONTENT_TYPE_XML;
				}
				else if("ZIP".equals(tipo)) {
					atteso = HttpConstants.CONTENT_TYPE_ZIP;
				}
				else {
					atteso = HttpConstants.CONTENT_TYPE_PLAIN;
				}

				if(!atteso.equals(ef.getMediaType())) {
					throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getMediaType()+"'");
				}
				
				byte [] attesoContent = null;
				if("XML".equals(tipo)) {
					attesoContent = Utilities.getAsByteArray(isXML);
				}
				else if("ZIP".equals(tipo)) {
					attesoContent = Utilities.getAsByteArray(isZIP);
				}
				else {
					attesoContent = Utilities.getAsByteArray(isDOC);
				}
				if(!Arrays.equals(attesoContent, ef.getContent())) {
					throw new Exception("Rilevato embedded file '"+fileName+"' con un contenuto diverso da quello atteso");
				}
				
				if(ef.getEmbeddedFile()==null) {
					throw new Exception("Oggetto EmbeddedFile non trovato");
				}
				if(ef.getFileSpec()==null) {
					throw new Exception("Oggetto FileSpec non trovato");
				}
				
			}
			
			if(!xml || !zip || !doc) {
				throw new Exception("Tutti gli allegati attesi non sono stati trovati");
			}
				
		} 
	}
	
	protected static final String NOME_FILE_XML_EMBEDDED_KID = "EsempioFileKid.xml";
	public static void verificaFileEmbeddedKid(InputStream isPDF) throws Exception {
		try(InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			
			PDFReader r = new PDFReader(isPDF);
			
			Map<String, EmbeddedFile> map = r.getEmbeddedFiles(false);
			if(map!=null && !map.isEmpty()) {
				throw new Exception("Embedded files non attesi");
			}
			
			map = r.getEmbeddedFiles(true); // kid
			if(map==null || map.isEmpty()) {
				throw new Exception("Embedded files non trovati");
			}
			if(map.size()!=1) {
				throw new Exception("Atteso 1 embedded file, trovati "+map.size()+" files");
			}
			String fileName = map.keySet().iterator().next();
			String atteso = NOME_FILE_XML_EMBEDDED_KID;
			if(!atteso.equals(fileName)) {
				throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+fileName+"'");
			}
			
			EmbeddedFile ef = map.get(fileName);
			if(ef==null) {
				throw new Exception("Embedded file is null");
			}
			
			if(!atteso.equals(ef.getFilename())) {
				throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getFilename()+"'");
			}
			
			atteso = HttpConstants.CONTENT_TYPE_TEXT_XML;
			if(!atteso.equals(ef.getMediaType())) {
				throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getMediaType()+"'");
			}
			
			byte [] attesoContent = Utilities.getAsByteArray(isXML);
			if(!Arrays.equals(attesoContent, ef.getContent())) {
				throw new Exception("Rilevato embedded file con un contenuto diverso da quello atteso");
			}
			
			if(ef.getEmbeddedFile()==null) {
				throw new Exception("Oggetto EmbeddedFile non trovato");
			}
			if(ef.getFileSpec()==null) {
				throw new Exception("Oggetto FileSpec non trovato");
			}
				
		} 
	}
	
	protected static final String NOME_FILE_XML_MULTIPLE_EMBEDDED_KID = "EsempioContenutoXML_kid.xml";
	protected static final String NOME_FILE_ZIP_MULTIPLE_EMBEDDED_KID = "EsempioArchivio_kid.zip";
	protected static final String NOME_FILE_DOC_MULTIPLE_EMBEDDED_KID = "README_kid.doc";
	public static void verificaFileMultipleEmbeddedKids(InputStream isPDF) throws Exception {
		try(InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");
			InputStream isZIP = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.zip");
			InputStream isDOC = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.doc");){
			
			PDFReader r = new PDFReader(isPDF);
			
			Map<String, EmbeddedFile> map = r.getEmbeddedFiles(false);
			if(map!=null && !map.isEmpty()) {
				throw new Exception("Embedded files non attesi");
			}
			
			map = r.getEmbeddedFiles(true); // kids
			if(map==null || map.isEmpty()) {
				throw new Exception("Embedded files non trovati");
			}
			if(map.size()!=3) {
				throw new Exception("Atteso 3 embedded files, trovati "+map.size()+" files");
			}
			
			boolean xml = false;
			boolean zip = false;
			boolean doc = false;
			
			for (String fileName : map.keySet()) {
				
				String tipo = null;
				
				if(NOME_FILE_XML_MULTIPLE_EMBEDDED_KID.equals(fileName)) {
					xml = true;
					tipo = "XML";
				}
				else if(NOME_FILE_ZIP_MULTIPLE_EMBEDDED_KID.equals(fileName)) {
					zip = true;
					tipo = "ZIP";
				}
				else if(NOME_FILE_DOC_MULTIPLE_EMBEDDED_KID.equals(fileName)) {
					doc = true;
					tipo = "DOC";
				}
				else {
					throw new Exception("File '"+fileName+"' unknown");
				}
				
				String atteso = null;
				if("XML".equals(tipo)) {
					atteso = NOME_FILE_XML_MULTIPLE_EMBEDDED_KID;
				}
				else if("ZIP".equals(tipo)) {
					atteso = NOME_FILE_ZIP_MULTIPLE_EMBEDDED_KID;
				}
				else {
					atteso = NOME_FILE_DOC_MULTIPLE_EMBEDDED_KID;
				}
				
				if(!atteso.equals(fileName)) {
					throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+fileName+"'");
				}
				
				EmbeddedFile ef = map.get(fileName);
				if(ef==null) {
					throw new Exception("Embedded file is null");
				}
				
				if(!atteso.equals(ef.getFilename())) {
					throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getFilename()+"'");
				}
				
				if("XML".equals(tipo)) {
					atteso = HttpConstants.CONTENT_TYPE_XML;
				}
				else if("ZIP".equals(tipo)) {
					atteso = HttpConstants.CONTENT_TYPE_ZIP;
				}
				else {
					atteso = HttpConstants.CONTENT_TYPE_PLAIN;
				}

				if(!atteso.equals(ef.getMediaType())) {
					throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getMediaType()+"'");
				}
				
				byte [] attesoContent = null;
				if("XML".equals(tipo)) {
					attesoContent = Utilities.getAsByteArray(isXML);
				}
				else if("ZIP".equals(tipo)) {
					attesoContent = Utilities.getAsByteArray(isZIP);
				}
				else {
					attesoContent = Utilities.getAsByteArray(isDOC);
				}
				if(!Arrays.equals(attesoContent, ef.getContent())) {
					throw new Exception("Rilevato embedded file '"+fileName+"' con un contenuto diverso da quello atteso");
				}
				
				if(ef.getEmbeddedFile()==null) {
					throw new Exception("Oggetto EmbeddedFile non trovato");
				}
				if(ef.getFileSpec()==null) {
					throw new Exception("Oggetto FileSpec non trovato");
				}
				
			}
			
			if(!xml || !zip || !doc) {
				throw new Exception("Tutti gli allegati attesi non sono stati trovati");
			}
				
		} 
	}
	
	
	protected static final String NOME_FILE_XML_ANNOTATION = "EsempioAnnotation.xml";
	public static void verificaFileAnnotation(InputStream isPDF) throws Exception {
		try(InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			
			PDFReader r = new PDFReader(isPDF);
			
			Map<String, EmbeddedFile> map = r.getAnnotationFiles();
			if(map==null || map.isEmpty()) {
				throw new Exception("Annotation files non trovati");
			}
			if(map.size()!=1) {
				throw new Exception("Atteso 1 embedded file, trovati "+map.size()+" files");
			}
			String fileName = map.keySet().iterator().next();
			String atteso = NOME_FILE_XML_ANNOTATION;
			if(!atteso.equals(fileName)) {
				throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+fileName+"'");
			}
			
			EmbeddedFile ef = map.get(fileName);
			if(ef==null) {
				throw new Exception("Embedded file is null");
			}
			
			if(!atteso.equals(ef.getFilename())) {
				throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getFilename()+"'");
			}
			
			atteso = HttpConstants.CONTENT_TYPE_TEXT_XML;
			if(!atteso.equals(ef.getMediaType())) {
				throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getMediaType()+"'");
			}
			
			byte [] attesoContent = Utilities.getAsByteArray(isXML);
			if(!Arrays.equals(attesoContent, ef.getContent())) {
				throw new Exception("Rilevato embedded file con un contenuto diverso da quello atteso");
			}
			
			if(ef.getEmbeddedFile()==null) {
				throw new Exception("Oggetto EmbeddedFile non trovato");
			}
			if(ef.getFileSpec()==null) {
				throw new Exception("Oggetto FileSpec non trovato");
			}
				
		} 
	}
	
	
	protected static final String NOME_FILE_XML_MULTIPLE_ANNOTATION = "EsempioContenutoXML_annotation.xml";
	protected static final String NOME_FILE_ZIP_MULTIPLE_ANNOTATION = "EsempioArchivio_annotation.zip";
	protected static final String NOME_FILE_DOC_MULTIPLE_ANNOTATION = "README_annotation.doc";
	public static void verificaFileMultipleAnnotations(InputStream isPDF) throws Exception {
		try(InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");
			InputStream isZIP = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.zip");
			InputStream isDOC = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.doc");){
			
			PDFReader r = new PDFReader(isPDF);
			
			Map<String, EmbeddedFile> map = r.getAnnotationFiles();
			if(map==null || map.isEmpty()) {
				throw new Exception("Annotation files non trovati");
			}
			if(map.size()!=3) {
				throw new Exception("Atteso 3 embedded files, trovati "+map.size()+" files");
			}
			
			boolean xml = false;
			boolean zip = false;
			boolean doc = false;
			
			for (String fileName : map.keySet()) {
				
				String tipo = null;
				
				if(NOME_FILE_XML_MULTIPLE_ANNOTATION.equals(fileName)) {
					xml = true;
					tipo = "XML";
				}
				else if(NOME_FILE_ZIP_MULTIPLE_ANNOTATION.equals(fileName)) {
					zip = true;
					tipo = "ZIP";
				}
				else if(NOME_FILE_DOC_MULTIPLE_ANNOTATION.equals(fileName)) {
					doc = true;
					tipo = "DOC";
				}
				else {
					throw new Exception("File '"+fileName+"' unknown");
				}
				
				String atteso = null;
				if("XML".equals(tipo)) {
					atteso = NOME_FILE_XML_MULTIPLE_ANNOTATION;
				}
				else if("ZIP".equals(tipo)) {
					atteso = NOME_FILE_ZIP_MULTIPLE_ANNOTATION;
				}
				else {
					atteso = NOME_FILE_DOC_MULTIPLE_ANNOTATION;
				}
				
				if(!atteso.equals(fileName)) {
					throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+fileName+"'");
				}
				
				EmbeddedFile ef = map.get(fileName);
				if(ef==null) {
					throw new Exception("Embedded file is null");
				}
				
				if(!atteso.equals(ef.getFilename())) {
					throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getFilename()+"'");
				}
				
				if("XML".equals(tipo)) {
					atteso = HttpConstants.CONTENT_TYPE_XML;
				}
				else if("ZIP".equals(tipo)) {
					atteso = HttpConstants.CONTENT_TYPE_ZIP;
				}
				else {
					atteso = HttpConstants.CONTENT_TYPE_PLAIN;
				}

				if(!atteso.equals(ef.getMediaType())) {
					throw new Exception("Atteso embedded file con nome "+atteso+", trovato '"+ef.getMediaType()+"'");
				}
				
				byte [] attesoContent = null;
				if("XML".equals(tipo)) {
					attesoContent = Utilities.getAsByteArray(isXML);
				}
				else if("ZIP".equals(tipo)) {
					attesoContent = Utilities.getAsByteArray(isZIP);
				}
				else {
					attesoContent = Utilities.getAsByteArray(isDOC);
				}
				if(!Arrays.equals(attesoContent, ef.getContent())) {
					throw new Exception("Rilevato embedded file '"+fileName+"' con un contenuto diverso da quello atteso");
				}
				
				if(ef.getEmbeddedFile()==null) {
					throw new Exception("Oggetto EmbeddedFile non trovato");
				}
				if(ef.getFileSpec()==null) {
					throw new Exception("Oggetto FileSpec non trovato");
				}
				
			}
			
			if(!xml || !zip || !doc) {
				throw new Exception("Tutti gli allegati attesi non sono stati trovati");
			}
				
		} 
	}
	
	
	protected static final String NOME_FILE_XML_XFA = "EsempioXFA.xml";
	public static void verificaFileXFA(InputStream isPDF, boolean xfaDataSets) throws Exception {
		
		XMLDiffOptions xmlDiffOptions = new XMLDiffOptions();
		XMLDiff xmlDiffEngine = new XMLDiff();
		
		xmlDiffEngine.initialize(XMLDiffImplType.XML_UNIT, xmlDiffOptions);
		
		try(InputStream isXML = PDFWriterTest.class.getResourceAsStream("/org/openspcoop2/utils/pdf/test/HelloWorld.xml");){
			
			PDFReader r = new PDFReader(isPDF);
			
			XFADocument xfaDocument = r.getXFAFile();
			
			if(xfaDocument==null) {
				throw new Exception("XFADocument non trovato");
			}
			if(xfaDocument.getContent()==null) {
				throw new Exception("XFADocument.content non trovato");
			}
			if(xfaDocument.getDocument()==null) {
				throw new Exception("XFADocument.document non trovato");
			}
			if(xfaDocument.getXfa()==null) {
				throw new Exception("XFADocument.xfaResource non trovato");
			}

			if(xfaDataSets) {
				
				if(!xfaDocument.isXfaDataContent()) {
					throw new Exception("Datasets atteso");
				}
				
				byte [] attesoContent = Utilities.getAsByteArray(isXML);
				
				byte[] contentConDataSets = XFADocument.addXfaDatasets(attesoContent);
				if(!Arrays.equals(contentConDataSets, xfaDocument.getContent())) {
					throw new Exception("Rilevato xfa file con un contenuto diverso da quello atteso");
				}
				
				Document dAtteso = XMLUtils.getInstance().newDocument(contentConDataSets);
				if(!xmlDiffEngine.diff(dAtteso, xfaDocument.getDocument())) {
					throw new Exception("Rilevato xfa file con un document diverso da quello atteso: "+xmlDiffEngine.getDifferenceDetails());
				}
				
				if(xfaDocument.getXfaDataContent()==null) {
					throw new Exception("Atteso un xfa data contenuto");
				}
				if(xfaDocument.getXfaDataContentNode()==null) {
					throw new Exception("Atteso un xfa data contenuto come nodo");
				}
				
				dAtteso = XMLUtils.getInstance().newDocument(attesoContent);
				Document trovato = XMLUtils.getInstance().newDocument(xfaDocument.getXfaDataContent());
				if(!xmlDiffEngine.diff(dAtteso, trovato)) {
					System.out.println("ATTESO: "+XMLUtils.getInstance().toString(dAtteso));
					System.out.println("TROVATO: "+XMLUtils.getInstance().toString(trovato));
					throw new Exception("Rilevato xfa file con un xfa data document diverso da quello atteso: "+xmlDiffEngine.getDifferenceDetails());
				}
				
			}
			else {
			
				if(xfaDocument.isXfaDataContent()) {
					throw new Exception("Datasets non atteso");
				}
				
				byte [] attesoContent = Utilities.getAsByteArray(isXML);
				if(!Arrays.equals(attesoContent, xfaDocument.getContent())) {
					throw new Exception("Rilevato xfa file con un contenuto diverso da quello atteso");
				}
				
				Document dAtteso = XMLUtils.getInstance().newDocument(attesoContent);
				if(!xmlDiffEngine.diff(dAtteso, xfaDocument.getDocument())) {
					throw new Exception("Rilevato xfa file con un document diverso da quello atteso: "+xmlDiffEngine.getDifferenceDetails());
				}
			
				if(xfaDocument.getXfaDataContent()!=null) {
					throw new Exception("Non atteso un xfa data contenuto");
				}
				if(xfaDocument.getXfaDataContentNode()!=null) {
					throw new Exception("Non atteso un xfa data contenuto come nodo");
				}
			}
				
		} 
	}
	
	
	protected static final String SIGNATURE_NAME = "SignatureTest";
	protected static final COSName SIGNATURE_FILTER = PDSignature.FILTER_ADOBE_PPKLITE;
	protected static final COSName SIGNATURE_SUBFILTER = PDSignature.SUBFILTER_ETSI_CADES_DETACHED;
	protected static final String SIGNATURE_LOCALITY = "Pisa";
	protected static final String SIGNATURE_REASON = "Test Signature";
	protected static final String SIGNATURE_CONTACT_INFO = "Esempio informazione di conjtatto";
	public static void verificaFileSignature(InputStream isPDF) throws Exception {
		PDFReader r = new PDFReader(isPDF, true);
		verificaFileSignature(r);
	}
	public static void verificaFileSignature(File isPDF) throws Exception {
		PDFReader r = new PDFReader(isPDF, true);
		verificaFileSignature(r);
	}
	public static void verificaFileSignature(byte[] isPDF) throws Exception {
		PDFReader r = new PDFReader(isPDF, true);
		verificaFileSignature(r);
	}
	private static void verificaFileSignature(PDFReader pdfReader) throws Exception {
		
		if(!pdfReader.isSignaturesExist()) {
			throw new Exception("Signature non trovata");
		}
		
		List<PDFSignature> signs = pdfReader.getSignature();
		if(signs==null || signs.isEmpty()) {
			throw new Exception("Signature non trovata");
		}
		if(signs.size()!=1) {
			throw new Exception("Attesa 1 Signature, trovate "+signs.size());
		}
		
		PDFSignature pdfSignature = signs.get(0);
		if(pdfSignature==null) {
			throw new Exception("Signature non trovata");
		}
		if(pdfSignature.getSignature()==null) {
			throw new Exception("Signature.signature non trovata");
		}
		if(pdfSignature.getSignatureField()==null) {
			throw new Exception("Signature.signatureField non trovata");
		}
		if(!SIGNATURE_NAME.equals(pdfSignature.getName())) {
			throw new Exception("Attesa signature con nome '"+SIGNATURE_NAME+"'; trovata '"+pdfSignature.getName()+"'");
		}
		if(!SIGNATURE_FILTER.getName().equals(pdfSignature.getSignature().getFilter())) {
			throw new Exception("Attesa signature con filtro '"+SIGNATURE_FILTER.getName()+"'; trovata '"+pdfSignature.getSignature().getFilter()+"'");
		}
		if(!SIGNATURE_SUBFILTER.getName().equals(pdfSignature.getSignature().getSubFilter())) {
			throw new Exception("Attesa signature con sub-filtro '"+SIGNATURE_SUBFILTER.getName()+"'; trovata '"+pdfSignature.getSignature().getSubFilter()+"'");
		}
		if(!SIGNATURE_LOCALITY.equals(pdfSignature.getSignature().getLocation())) {
			throw new Exception("Attesa signature con location '"+SIGNATURE_LOCALITY+"'; trovata '"+pdfSignature.getSignature().getLocation()+"'");
		}
		if(!SIGNATURE_REASON.equals(pdfSignature.getSignature().getReason())) {
			throw new Exception("Attesa signature con reason '"+SIGNATURE_REASON+"'; trovata '"+pdfSignature.getSignature().getReason()+"'");
		}
		if(!SIGNATURE_CONTACT_INFO.equals(pdfSignature.getSignature().getContactInfo())) {
			throw new Exception("Attesa signature con concact info '"+SIGNATURE_CONTACT_INFO+"'; trovata '"+pdfSignature.getSignature().getContactInfo()+"'");
		}
		
		List<PDFSignerInformation> signers = pdfSignature.getSigners();
		if(signers==null || signers.isEmpty()) {
			throw new Exception("Signer information non trovata");
		}
		if(signers.size()!=1) {
			throw new Exception("Attesa 1 signer information, trovate "+signers.size());
		}
		PDFSignerInformation signerInfo = signers.get(0);
		if(signerInfo==null) {
			throw new Exception("Signer information non trovata");
		}
		if(signerInfo.getSigner()==null) {
			throw new Exception("Signer information (signer) non trovata");
		}
		
		List<Certificate> certs = signerInfo.getCertificates();
		if(certs==null || certs.isEmpty()) {
			throw new Exception("Certificati non trovati");
		}
		if(certs.size()!=1) {
			throw new Exception("Atteso 1 certificato, trovati "+certs.size());
		}
		
		Certificate cert = certs.get(0);
		if(cert==null) {
			throw new Exception("Certificato non trovato");
		}
		String subject = cert.getCertificate().getSubject().toString();
		if(!subject.equals(KeystoreTest.DN_1)) {
			throw new UtilsException("Subject recuperato ("+subject+") differente da quello atteso: "+KeystoreTest.DN_1);
		}
	}

}
