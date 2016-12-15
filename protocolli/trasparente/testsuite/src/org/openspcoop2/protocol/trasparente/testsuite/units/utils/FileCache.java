package org.openspcoop2.protocol.trasparente.testsuite.units.utils;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.protocol.trasparente.testsuite.core.Utilities;

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
			cache.put(jsonFileEntry.getExtRichiesta(), jsonFileEntry);
			
			TestFileEntry pdfFileEntry = new TestFileEntry();
			pdfFileEntry.setExtRichiesta("pdf");
			pdfFileEntry.setFilenameRichiesta(Utilities.testSuiteProperties.getPDFFileName());
			pdfFileEntry.setExtRispostaKo("json");
			pdfFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getJSONFileNameRispostaKo());
			cache.put(pdfFileEntry.getExtRichiesta(), pdfFileEntry);
			
			TestFileEntry xmlFileEntry = new TestFileEntry();
			xmlFileEntry.setExtRichiesta("xml");
			xmlFileEntry.setFilenameRichiesta(Utilities.testSuiteProperties.getXMLFileName());
			xmlFileEntry.setExtRispostaKo("xml");
			xmlFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getXMLFileNameRispostaKo());
			cache.put(xmlFileEntry.getExtRichiesta(), xmlFileEntry);
			
			TestFileEntry zipFileEntry = new TestFileEntry();
			zipFileEntry.setExtRichiesta("zip");
			zipFileEntry.setFilenameRichiesta(Utilities.testSuiteProperties.getZIPFileName());
			zipFileEntry.setExtRispostaKo("json");
			zipFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getJSONFileNameRispostaKo());
			cache.put(zipFileEntry.getExtRichiesta(), zipFileEntry);

			TestFileEntry docFileEntry = new TestFileEntry();
			docFileEntry.setExtRichiesta("doc");
			docFileEntry.setFilenameRichiesta(Utilities.testSuiteProperties.getDOCFileName());
			docFileEntry.setExtRispostaKo("json");
			docFileEntry.setFilenameRispostaKo(Utilities.testSuiteProperties.getJSONFileNameRispostaKo());
			cache.put(docFileEntry.getExtRichiesta(), docFileEntry);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static TestFileEntry get(String ext) throws Exception {
		if(cache.containsKey(ext)) {
			return cache.get(ext);
		} else {
			throw new Exception("File con ext ["+ext+"] non supportato");
		}
	}
}
