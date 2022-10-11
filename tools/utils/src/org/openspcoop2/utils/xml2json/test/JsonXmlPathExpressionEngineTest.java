/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
/**
 * 
 */
package org.openspcoop2.utils.xml2json.test;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.slf4j.Logger;

/**
 * JsonXmlPathExpressionEngineTest
 * 
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class JsonXmlPathExpressionEngineTest {


	public static void main(String[] args) throws Exception {

		Logger log = LoggerWrapperFactory.getLogger(JsonXmlPathExpressionEngineTest.class);
		
		String testJsonSemplice = "{prova:\"test1\",prova2:23}";
	
		System.out.println("======== TEST ==========");
		
		
		String pattern = "$.prova";
		JsonXmlPathExpressionEngine.validate(pattern, log);
		String v = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(testJsonSemplice, pattern, log);
		if(v!=null && !"".equals(v)) {
			if("test1".equals(v)) {
				System.out.println("Test 'default' con namespace completato con successo: "+v);
			}
			else {
				throw new Exception("Test 'default' con namespace fallito; ritornato valore inatteso: "+v);
			}
		}
		else {
			throw new Exception("Test 'default' con namespace fallito");
		}
		
		
		pattern = "xpath /json2xml/prova/text()";
		JsonXmlPathExpressionEngine.validate(pattern, log);
		v = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(testJsonSemplice, pattern, log);
		if(v!=null && !"".equals(v)) {
			if("test1".equals(v)) {
				System.out.println("Test 'default' con namespace completato con successo: "+v);
			}
			else {
				throw new Exception("Test 'default' con namespace fallito; ritornato valore inatteso: "+v);
			}
		}
		else {
			throw new Exception("Test 'default' con namespace fallito");
		}
		
		
		pattern = "xpath //prova/text()";
		JsonXmlPathExpressionEngine.validate(pattern, log);
		v = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(testJsonSemplice, pattern, log);
		if(v!=null && !"".equals(v)) {
			if("test1".equals(v)) {
				System.out.println("Test 'default' con namespace completato con successo: "+v);
			}
			else {
				throw new Exception("Test 'default' con namespace fallito; ritornato valore inatteso: "+v);
			}
		}
		else {
			throw new Exception("Test 'default' con namespace fallito");
		}

		
		pattern = "xpath /json2xml/*";
		JsonXmlPathExpressionEngine.validate(pattern, log);
		v = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(testJsonSemplice, pattern, log);
		if(v!=null && !"".equals(v)) {
			if("<prova>test1</prova><prova2>23</prova2>".equals(v)) {
				System.out.println("Test 'default' con namespace completato con successo: "+v);
			}
			else {
				throw new Exception("Test 'default' con namespace fallito; ritornato valore inatteso: "+v);
			}
		}
		else {
			throw new Exception("Test 'default' con namespace fallito");
		}
		
		
		pattern = "xpath local-name(/json2xml/*[last()])";
		JsonXmlPathExpressionEngine.validate(pattern, log);
		v = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(testJsonSemplice, pattern, log);
		if(v!=null && !"".equals(v)) {
			if("prova2".equals(v)) {
				System.out.println("Test 'default' con namespace completato con successo: "+v);
			}
			else {
				throw new Exception("Test 'default' con namespace fallito; ritornato valore inatteso: "+v);
			}
		}
		else {
			throw new Exception("Test 'default' con namespace fallito");
		}
		
		
		
		System.out.println("======== TEST NAMESPACE ==========");
		
		String testJsonComplessoConNamespace = "{\n"+
				"\"m:NomeAzioneTestRequest\":\n"+
				"{\n"+
				"\"bodyWithNS\" : \"true\",\n"+
				"\"xmlns:m\": \"http://testNamespace\",\n"+
				"\"prodotto\": {\n"+
				"\"codice\":\"26\",\n"+
				"\"altro:codice3\":\"34\",\n"+
				"\"xmlns:altro\" : \"http://testNamespaceAltro\"\n"+
				"}\n"+
				"}\n"+
				"}";
		
		pattern = "xpath namespace(m:http://testNamespace, altro:http://altro) substring-before(local-name(//json2xml/*),\"Request\")";
		JsonXmlPathExpressionEngine.validate(pattern, log);
		v = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(testJsonComplessoConNamespace, pattern, log);
		if(v!=null && !"".equals(v)) {
			if("NomeAzioneTest".equals(v)) {
				System.out.println("Test con namespace completato con successo: "+v);
			}else {
				throw new Exception("Test 'default' con namespace fallito; ritornato valore inatteso: "+v);
			}
		}
		else {
			throw new Exception("Test con namespace fallito");
		}
		
		pattern = "xpath substring-before(local-name(//json2xml/*),\"Request\")";
		JsonXmlPathExpressionEngine.validate(pattern, log);
		try {
			v = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(testJsonComplessoConNamespace, pattern, log);
			throw new Exception("Atteso eccezione");
		}catch(Exception e) {
			if(e.getMessage().contains("The prefix \"m\" for element \"m:NomeAzioneTestRequest\" is not bound")) {
				System.out.println("Test con namespace, senza dichiarazione dei namespace, completato con successo; riscontrato errore atteso");
			}
			else {
				throw e;
			}
		}

		pattern = "xpath namespace(altro:http://altro) substring-before(local-name(//json2xml/*),\"Request\")";
		JsonXmlPathExpressionEngine.validate(pattern, log);
		try {
			v = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(testJsonComplessoConNamespace, pattern, log);
			throw new Exception("Atteso eccezione");
		}catch(Exception e) {
			if(e.getMessage().contains("The prefix \"m\" for element \"m:NomeAzioneTestRequest\" is not bound")) {
				System.out.println("Test con namespace, senza dichiarazione dei namespace, completato con successo; riscontrato errore atteso");
			}
			else {
				throw e;
			}
		}
		
		
		pattern = "xpath namespace(altro:) substring-before(local-name(//json2xml/*),\"Request\")";
		try {
			JsonXmlPathExpressionEngine.validate(pattern, log);
			throw new Exception("Atteso eccezione");
		}catch(Exception e) {
			String msgError = "Espressione '"+pattern+"' da utilizzare non corretta; dichiarazione dei namespace in un formato non corretto: attesa dichiarazione namespace. (altro:) (altro:)";
			if(e.getMessage().contains(msgError)) {
				System.out.println("Test validazione pattern errato, caso 1; riscontrato errore atteso");
			}
			else {
				throw e;
			}
		}
		
		pattern = "xpath namespace(sa) substring-before(local-name(//json2xml/*),\"Request\")";
		try {
			JsonXmlPathExpressionEngine.validate(pattern, log);
			throw new Exception("Atteso eccezione");
		}catch(Exception e) {
			String msgError = "Espressione '"+pattern+"' da utilizzare non corretta; dichiarazione dei namespace in un formato non corretto: atteso ':' separator. (sa) (sa)";
			if(e.getMessage().contains(msgError)) {
				System.out.println("Test validazione pattern errato, caso 2; riscontrato errore atteso");
			}
			else {
				throw e;
			}
		}
		
		pattern = "xpath dedee/";
		try {
			JsonXmlPathExpressionEngine.validate(pattern, log);
			throw new Exception("Atteso eccezione");
		}catch(Exception e) {
			String msgError = "Trasformazione json2xml 'Mapped' fallita: Validazione dell'xpath indicato [dedee/] fallita: Compilazione dell'espressione XPATH ha causato un errore (A location step was expected following the '/' or '//' token.)";
			if(e.getMessage().contains(msgError)) {
				System.out.println("Test validazione pattern errato, caso xpath 1; riscontrato errore atteso");
			}
			else {
				throw e;
			}
		}
		
		pattern = "xpath namespace(altro:http://altro) altro/TEXT()";
		try {
			JsonXmlPathExpressionEngine.validate(pattern, log);
			throw new Exception("Atteso eccezione");
		}catch(Exception e) {
			String msgError = "Trasformazione json2xml 'Mapped' fallita: Validazione dell'xpath indicato [altro/TEXT()] fallita: Compilazione dell'espressione XPATH ha causato un errore (Unknown nodetype: TEXT)";
			if(e.getMessage().contains(msgError)) {
				System.out.println("Test validazione pattern errato, caso xpath 2; riscontrato errore atteso");
			}
			else {
				throw e;
			}
		}
		
		pattern = "$. DE$$";
		try {
			JsonXmlPathExpressionEngine.validate(pattern, log);
			throw new Exception("Atteso eccezione");
		}catch(Exception e) {
			String msgError = "Validazione del jsonPath indicato [$. DE$$] fallita: Could not parse token starting at position 2";
			if(e.getMessage().contains(msgError)) {
				System.out.println("Test validazione pattern errato, caso jsonpath 1; riscontrato errore atteso");
			}
			else {
				throw e;
			}
		}
	}

}
