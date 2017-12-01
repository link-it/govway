/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;

import org.apache.ws.security.util.UUIDGenerator;
import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;
import org.openspcoop2.utils.mime.MimeMultipart;

import com.sun.istack.internal.ByteArrayDataSource;

/**
 * FileCache
 *
 * @author Bussu Giovanni (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileCache {

	private static Map<String, TestFileEntry> cache;
	
	static {
		try {
			cache = new HashMap<String, TestFileEntry>();
	
			TestFileEntry jsonFileEntry = new TestFileEntry();
			jsonFileEntry.setExtRichiesta("json");
			jsonFileEntry.setFilenameRichiesta(Utilities.testSuiteProperties.getJSONFileName());
			jsonFileEntry.setExtRispostaKo("json");
			jsonFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getJSONFileNameRispostaKo());
			cache.put("json", jsonFileEntry);
			
			TestFileEntry pdfFileEntry = new TestFileEntry();
			pdfFileEntry.setExtRichiesta("pdf");
			pdfFileEntry.setFilenameRichiesta(Utilities.testSuiteProperties.getPDFFileName());
			pdfFileEntry.setExtRispostaKo("json");
			pdfFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getJSONFileNameRispostaKo());
			cache.put("pdf", pdfFileEntry);
			
			TestFileEntry xmlFileEntry = new TestFileEntry();
			xmlFileEntry.setExtRichiesta("xml");
			xmlFileEntry.setFilenameRichiesta(Utilities.testSuiteProperties.getXMLFileName());
			xmlFileEntry.setExtRispostaKo("xml");
			xmlFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getXMLFileNameRispostaKo());
			cache.put("xml", xmlFileEntry);

			TestFileEntry pngFileEntry = new TestFileEntry();
			pngFileEntry.setExtRichiesta("png");
			pngFileEntry.setFilenameRichiesta(Utilities.testSuiteProperties.getPngFileName());
			pngFileEntry.setExtRispostaKo("json");
			pngFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getJSONFileNameRispostaKo());
			cache.put("png", pngFileEntry);
			
			TestFileEntry zipFileEntry = new TestFileEntry();
			zipFileEntry.setExtRichiesta("zip");
			zipFileEntry.setFilenameRichiesta(Utilities.testSuiteProperties.getZIPFileName());
			zipFileEntry.setExtRispostaKo("json");
			zipFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getJSONFileNameRispostaKo());
			cache.put("zip", zipFileEntry);

			TestFileEntry docFileEntry = new TestFileEntry();
			docFileEntry.setExtRichiesta("doc");
			docFileEntry.setFilenameRichiesta(Utilities.testSuiteProperties.getDOCFileName());
			docFileEntry.setExtRispostaKo("json");
			docFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getJSONFileNameRispostaKo());
			cache.put("doc", docFileEntry);

			createFileMulti("related", "multi", xmlFileEntry, pdfFileEntry, pngFileEntry);
			
			createFileMulti("mixed", "multi-mixed", xmlFileEntry, pdfFileEntry, pngFileEntry);
			
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void createFileMulti(String subType,String alias,
			TestFileEntry xmlFileEntry, TestFileEntry pdfFileEntry, TestFileEntry pngFileEntry) throws Exception {
		
		TestFileEntry multiFileEntry = new TestFileEntry();
		MimeMultipart mm = new MimeMultipart(subType);
		BodyPart bodyXml = new MimeBodyPart();
		bodyXml.setDataHandler(new DataHandler(new ByteArrayDataSource(xmlFileEntry.getBytesRichiesta(), xmlFileEntry.getExtRichiesta())));
		bodyXml.addHeader("Content-Type", xmlFileEntry.getExtRichiesta());
		bodyXml.addHeader("Content-Id", UUIDGenerator.getUUID().replace("-", ""));
		mm.addBodyPart(bodyXml);

		BodyPart bodyPdf = new MimeBodyPart();
		bodyPdf.setDataHandler(new DataHandler(new ByteArrayDataSource(pdfFileEntry.getBytesRichiesta(), pdfFileEntry.getExtRichiesta())));
		bodyPdf.addHeader("Content-Type", pdfFileEntry.getExtRichiesta());
		bodyPdf.addHeader("Content-Id", UUIDGenerator.getUUID().replace("-", ""));
		mm.addBodyPart(bodyPdf);

		BodyPart bodyTxt = new MimeBodyPart();
		bodyTxt.setDataHandler(new DataHandler(new ByteArrayDataSource("Hello world".getBytes(), "text/plain")));
		bodyTxt.addHeader("Content-Type", "text/plain");
		bodyTxt.addHeader("Content-Id", UUIDGenerator.getUUID().replace("-", ""));
		mm.addBodyPart(bodyTxt);

		BodyPart bodyPng = new MimeBodyPart();
		bodyPng.setDataHandler(new DataHandler(new ByteArrayDataSource(pngFileEntry.getBytesRichiesta(), pngFileEntry.getExtRichiesta())));
		bodyPng.addHeader("Content-Type", pngFileEntry.getExtRichiesta());
		bodyPng.addHeader("Content-Id", UUIDGenerator.getUUID().replace("-", ""));
		mm.addBodyPart(bodyPng);

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		mm.writeTo(os);
		
		multiFileEntry.setFilename(Utilities.testSuiteProperties.getMultipartFileName(), os.toByteArray());
//		String boundary = org.openspcoop2.utils.mime.MultipartUtils.findBoundary(multiFileEntry.getBytesRichiesta()).substring(2);
//		String firstCid = org.openspcoop2.utils.mime.MultipartUtils.firstContentID(multiFileEntry.getBytesRichiesta());
		String ctDefault = mm.getBodyPart(0).getContentType();
		
		String contentTypeSwA_Default = mm.getContentType();
		//String contentTypeSwA_Default = "multipart/related; type=\""+ctDefault+"\"; boundary=\""+boundary+"\"; start=\""+firstCid+"\"";
		//String contentTypeSwARisp = "multipart/related; type=\"text/xml\"; boundary=\"----=_Part_0_324745530.1482245107305\"; start=\"50AC46723310A39E4414822451073271\"";
		if(contentTypeSwA_Default.contains("type=")==false) {
			contentTypeSwA_Default = contentTypeSwA_Default + "; type=\""+ctDefault+"\"";
		}
		
		multiFileEntry.setMimeTypeRichiesta(contentTypeSwA_Default);
		multiFileEntry.setMimeTypeRisposta(contentTypeSwA_Default);
		multiFileEntry.setExtRispostaKo("json");
		multiFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getJSONFileNameRispostaKo());
		
		cache.put(alias, multiFileEntry);
	}
	
	public static TestFileEntry get(String ext) throws Exception {
		if(cache.containsKey(ext)) {
			return cache.get(ext);
		} else {
			throw new Exception("File con ext ["+ext+"] non supportato");
		}
	}
}
