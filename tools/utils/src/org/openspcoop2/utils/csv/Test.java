/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.utils.csv;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 *  Test
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Test {

	public static void main(String[] args) throws Exception{
		
		FormatReader formatReader = new FormatReader(Test.class.getResourceAsStream("/org/openspcoop2/utils/csv/example.format.properties"));
		Format format = formatReader.getFormat();
		
		// Test Lettura con mapping indicati tramite posizionamento CSV
		testMappingFilePosizionamentoCSV(format);
		
		// Test Lettura con mapping indicati tramite nomi colonne CSV
		testMappingFileNomiColonneCSV(format);
		
		// Test Lettura con mapping realizzato via java
		testMappingProgrammazione(format);

		
		FormatReader formatWriter = new FormatReader(Test.class.getResourceAsStream("/org/openspcoop2/utils/csv/example.formatWriter.properties"));
		Format formatW = formatWriter.getFormat();
		
		// Test Serializzazione
		testScrittura(formatW);
	}

	private static void testScrittura(Format format) throws Exception{
		System.out.println("============================================================");
		System.out.println("Printer.testScrittura");
		System.out.println("============================================================");
		String  file = "/tmp/prova.csv";
		Printer printer = new Printer(format, new File(file));
		List<String> header = new ArrayList<String>();
		header.add("Valore1");
		header.add("Valore2");
		header.add("Valore3");
		List<String> valori = new ArrayList<String>();
		valori.add("V1");
		valori.add("V2");
		valori.add("V3,AltroValoreConVirgola");
		printer.printComment("Intestazione");
		printer.printRecord(header);
		printer.println();
		printer.printComment("Valori");
		printer.printRecord(valori);
		printer.printRecord(valori);
		printer.printRecord(valori);
		printer.printRecord(valori);
		List<String> valoriWithNull = new ArrayList<String>();
		valoriWithNull.add("V1");
		valoriWithNull.add("");
		valoriWithNull.add(null);
		printer.printRecord(valoriWithNull);
		printer.close();
		System.out.println("File scritto in ["+file+"]");
		System.out.println("\n\n");
	}
	
	private static void testMappingFilePosizionamentoCSV(Format format) throws Exception{
		
		System.out.println("============================================================");
		System.out.println("Parser.testMappingFilePosizionamentoCSV");
		System.out.println("============================================================");
		Parser parser = new Parser(Test.class.getResourceAsStream("/org/openspcoop2/utils/csv/example.mapping.properties"), true);
		ParserResult pr = parser.parseCsvFile(format, Test.class.getResourceAsStream("/org/openspcoop2/utils/csv/example.csv"));
		print(pr);	
		System.out.println("\n\n");
	}
	
	private static void testMappingFileNomiColonneCSV(Format format) throws Exception{
		
		System.out.println("============================================================");
		System.out.println("Parser.testMappingFileNomiColonneCSV");
		System.out.println("============================================================");
		Parser parser = new Parser(Test.class.getResourceAsStream("/org/openspcoop2/utils/csv/example.mappingNames.properties"), false);
		ParserResult pr = parser.parseCsvFile(format, Test.class.getResourceAsStream("/org/openspcoop2/utils/csv/example.csv"));
		print(pr);	
		System.out.println("\n\n");
	}
	
	private static void testMappingProgrammazione(Format format) throws Exception{
		
		List<ParserMappingRecord> listParserInstruction = new ArrayList<ParserMappingRecord>();
		
		listParserInstruction.add(ParserMappingRecord.newCsvConstantRecord("tipologia", "Fruizione"));
		
		ParserMappingRecord pmcNomeServizio = ParserMappingRecord.newCsvColumnPositionRecord("nomeServizio",0);
		pmcNomeServizio.setRequired(true);
		listParserInstruction.add(pmcNomeServizio);
		
		listParserInstruction.add(ParserMappingRecord.newCsvColumnPositionRecord("nomeAccordo",1));
		
		listParserInstruction.add(ParserMappingRecord.newCsvColumnPositionRecord("nomeSoggettoErogatore",2));
		
		listParserInstruction.add(ParserMappingRecord.newCsvColumnPositionRecord("nomeSoggettoFruitore",3,"FruitoreNonDisponibile"));
		
		listParserInstruction.add(ParserMappingRecord.newCsvColumnNameRecord("saFruitoreUsername","SA FRUITORE",ParserRegexpNotFound.ORIGINAL, 
				new String [] {"(.*)MatchCheNonFunziona/.*","(.*)/.*","(.*)AltroMatchCheNonFunziona/.*"}));
		
		listParserInstruction.add(ParserMappingRecord.newCsvColumnPositionRecord("saFruitorePassword",4, new String [] {".*/([^/|^?]*).*"}));
		
		listParserInstruction.add(ParserMappingRecord.newCsvColumnNameRecord("saErogatore","SA EROGATORE"));
	
		System.out.println("============================================================");
		System.out.println("Parser.testMappingProgrammazione");
		System.out.println("============================================================");
		Parser parser = new Parser(listParserInstruction);
		ParserResult pr = parser.parseCsvFile(format, Test.class.getResourceAsStream("/org/openspcoop2/utils/csv/example.csv"));
		print(pr);	
		System.out.println("\n\n");
	}
	
	
	private static void print(ParserResult pr) throws Exception{
		
		System.out.println("ExistsHeader ["+pr.existsHeader()+"]");
		if(pr.existsHeader()){
			Iterator<String> itString = pr.getHeaderMap().keySet().iterator();
			while (itString.hasNext()) {
				String key = (String) itString.next();
				System.out.println("Header ["+key+"] ["+pr.getHeaderMap().get(key)+"]");
			}
		}
	
		List<Record> results = pr.getRecords();
		for (Record record : results) {
			if(record.getComment()!=null){
				System.out.println("CommentLine["+record.getCsvLine()+"] -> ["+record.getComment()+"]");
			}
			System.out.println("CsvLine["+record.getCsvLine()+"] tipologia["+record.getMap().get("tipologia")+"] nomeServizio["+record.getMap().get("nomeServizio")+
					"] nomeAccordo["+record.getMap().get("nomeAccordo")+
					"] nomeSoggettoErogatore["+record.getMap().get("nomeSoggettoErogatore")+
					"] nomeSoggettoFruitore["+record.getMap().get("nomeSoggettoFruitore")+
					"] saFruitoreUsername["+record.getMap().get("saFruitoreUsername")+
					"] saFruitorePassword["+record.getMap().get("saFruitorePassword")+
					"] saErogatore["+record.getMap().get("saErogatore")+"]");
		}
	}
}
