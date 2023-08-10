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


package org.openspcoop2.core.registry.driver.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.Level;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.IdSoggetto;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.JavaDate;
import org.openspcoop2.utils.serialization.Filter;
import org.openspcoop2.utils.serialization.JavaDeserializer;
import org.openspcoop2.utils.serialization.JavaSerializer;
import org.openspcoop2.utils.serialization.JsonJacksonDeserializer;
import org.openspcoop2.utils.serialization.JsonJacksonSerializer;
import org.openspcoop2.utils.serialization.SerializationConfig;
import org.slf4j.Logger;

/**	
 * Contiene una batteria di test
 *
 * org.openspcoop.utils.serialization.ClientTest DIR_OUTPUT
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SerializationClientTest {

	private static Logger log = null;
	
	public static void main(String [] args) throws Exception{
		test(args);
	}
	public static void test(String ... params) throws Exception{
		
		File logFile = File.createTempFile("testSerializzazione_", ".log");
		System.out.println("LogMessages write in "+logFile.getAbsolutePath());
		LoggerWrapperFactory.setDefaultLogConfiguration(Level.ALL, false, null, logFile, "%m %n");
		log = LoggerWrapperFactory.getLogger(SerializationClientTest.class);	
		
		// Factory
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		long dataInizio = -1;
		long dataFine = -1;
		
		File dir = new File(".");
		if(params!=null && params.length>0){
			dir = new File(params[0].trim());
			if(dir.exists()){
				if(dir.isDirectory()){
					throw new Exception("Directory ["+dir.getAbsolutePath()+"] gia' esistente");
				}else{
					throw new Exception("Location ["+dir.getAbsolutePath()+"] gia' esistente e inoltre non e' una directory");
				}
			}
			if(!dir.mkdir()){
				throw new Exception("Directory ["+dir.getAbsolutePath()+"] non creabile");
			}
		}
		log.info("Directory '"+dir.getAbsolutePath()+"'");
		
		DateManager.initializeDataManager(JavaDate.class.getName(), new Properties(), null);
		
		// Oggetto di test
		Soggetto soggettoTest = new Soggetto();
		soggettoTest.setTipo("SPC");
		soggettoTest.setNome("SoggettoTest");
		soggettoTest.setCodiceIpa("o=test,c=it");
		soggettoTest.setIdentificativoPorta("PROVAID");
		soggettoTest.setDescrizione("Test package");
		soggettoTest.setOraRegistrazione(DateManager.getDate());
		soggettoTest.setPortaDominio("pdd");
		soggettoTest.setPrivato(false);
		soggettoTest.setVersioneProtocollo("linee guida");
		soggettoTest.setSuperUser("amministratore");
		Connettore connettore = new Connettore();
		connettore.setNome("ConnettoreTest");
		connettore.setTipo("http");
		Property pr = new Property();
		pr.setNome("location");
		pr.setValore("http://localhost:8080/govway/in");
		connettore.addProperty(pr);
		Property pr2 = new Property();
		pr2.setNome("location2");
		pr2.setValore("http://localhost:8080/govway/in");
		connettore.addProperty(pr2);
		soggettoTest.setConnettore(connettore);
		
		AccordoServizioParteComune as = new AccordoServizioParteComune();
		as.setNome("ASTEST");
		as.setVersione(1);
		IdSoggetto sogg = new IdSoggetto();
		sogg.setTipo("SPC");
		sogg.setNome("SoggettoReferente");
		as.setSoggettoReferente(sogg);
		as.setByteWsdlDefinitorio("<xsd>TEST<xsd>".getBytes());
		as.setByteWsdlConcettuale("<wsdl>TEST<wsdl>".getBytes());
		as.setByteWsdlLogicoErogatore("<wsdl>TEST<wsdl>".getBytes());
		as.setByteSpecificaConversazioneConcettuale("<spe>TEST</spe>".getBytes());
		as.setByteSpecificaConversazioneErogatore("<spe>TEST</spe>".getBytes());
		as.setByteSpecificaConversazioneFruitore("<spe>TEST</spe>".getBytes());
		as.setPrivato(false);
		Documento allegato1 = new Documento();
		allegato1.setFile("TEST.txt");
		allegato1.setByteContenuto("TEST HELLO WORLD".getBytes());
		as.addAllegato(allegato1);
		
		Soggetto s1 = ((Soggetto)soggettoTest.clone());
		s1.setNome("SoggettoTest1");
		Soggetto s2 = ((Soggetto)soggettoTest.clone());
		s2.setNome("SoggettoTest2");
		Soggetto s3 = ((Soggetto)soggettoTest.clone());
		s3.setNome("SoggettoTest3");
		Soggetto s4 = ((Soggetto)soggettoTest.clone());
		s4.setNome("SoggettoTest4");
		Soggetto s5 = ((Soggetto)soggettoTest.clone());
		s5.setNome("SoggettoTest5");
		Soggetto s6 = ((Soggetto)soggettoTest.clone());
		s6.setNome("SoggettoTest6");
		Soggetto s7 = ((Soggetto)soggettoTest.clone());
		s7.setNome("SoggettoTest7");
		
		// Excludes per Json
		List<String> excludesJsonSoggetto = new ArrayList<> ();
		excludesJsonSoggetto.add("servizio");
		excludesJsonSoggetto.add("servizioCorrelato");
		excludesJsonSoggetto.add("property"); // del connettore

		List<String> excludesJsonAccordiServizio = new ArrayList<> ();
		excludesJsonAccordiServizio.add("specificaSemiformale");
		excludesJsonAccordiServizio.add("allegato");
		excludesJsonAccordiServizio.add("azione");
		excludesJsonAccordiServizio.add("portType");
				
		List<String> excludesJson_date = new ArrayList<> ();
		excludesJson_date.add("day");
		excludesJson_date.add("timezoneOffset");
		
		// LISTE
		
		List<Object> testSerializzazioneLista = new LinkedList<>();
		testSerializzazioneLista.add(as);
		testSerializzazioneLista.add(s1);
		testSerializzazioneLista.add(s2);
		testSerializzazioneLista.add(s3);
		testSerializzazioneLista.add(s4);
		testSerializzazioneLista.add(s5);
		testSerializzazioneLista.add(s6);
		testSerializzazioneLista.add(s7);
		
		List<Soggetto> testSerializzazioneListaSoggetti = new LinkedList<>();
		testSerializzazioneListaSoggetti.add(s1);
		testSerializzazioneListaSoggetti.add(s2);
		testSerializzazioneListaSoggetti.add(s3);
		testSerializzazioneListaSoggetti.add(s4);
		testSerializzazioneListaSoggetti.add(s5);
		testSerializzazioneListaSoggetti.add(s6);
		testSerializzazioneListaSoggetti.add(s7);
		
		List<AccordoServizioParteComune> testSerializzazioneListaAccordi = new LinkedList<>();
		testSerializzazioneListaAccordi.add(as);
		
		// SET
		
		Set<Object> testSerializzazioneSet = new HashSet<Object>();
		testSerializzazioneSet.add(as);
		testSerializzazioneSet.add(s1);
		testSerializzazioneSet.add(s2);
		testSerializzazioneSet.add(s3);
		testSerializzazioneSet.add(s4);
		testSerializzazioneSet.add(s5);
		testSerializzazioneSet.add(s6);
		testSerializzazioneSet.add(s7);
		
		Set<Object> testSerializzazioneSetSoggetti = new HashSet<Object>();
		testSerializzazioneSetSoggetti.add(s1);
		testSerializzazioneSetSoggetti.add(s2);
		testSerializzazioneSetSoggetti.add(s3);
		testSerializzazioneSetSoggetti.add(s4);
		testSerializzazioneSetSoggetti.add(s5);
		testSerializzazioneSetSoggetti.add(s6);
		testSerializzazioneSetSoggetti.add(s7);
		
		Set<Object> testSerializzazioneSetAccordi = new HashSet<Object>();
		testSerializzazioneSetAccordi.add(as);
		
		
		
		
		
		
		
		
		
		
		// **************** TEST SERIALIZZAZIONE JAVA **************************************
		log.info("\n");
		log.info("********** Serializzazione Java (SingleObject) ****************");
		log.info("\n");
		
		// Serializzazione tramite stream in/out
		
		File javaFileStream = new File(dir,"singleObjectStream.ser");
		try {
			FileOutputStream foutJava = new FileOutputStream(javaFileStream);
			FileInputStream finJava = new FileInputStream(javaFileStream);
			
			log.info("- Serializzazione (Stream in/out): ");
			JavaSerializer javaSerializer = new JavaSerializer();
			dataInizio = DateManager.getTimeMillis();
			javaSerializer.writeObject(soggettoTest, foutJava);
			dataFine = DateManager.getTimeMillis();
			foutJava.flush();
			foutJava.close();
			log.info("- OK");
			log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileStream.length()));
			log.info("\n");
			
			log.info("- Deserializzazione (Stream in/out): ");
			JavaDeserializer javaDeserializer = new JavaDeserializer();
			dataInizio = DateManager.getTimeMillis();
			Object oJavaRead = javaDeserializer.readObject(finJava, soggettoTest.getClass());
			dataFine = DateManager.getTimeMillis();
			finJava.close();
			SerializationClientTest.equals(soggettoTest, (Soggetto)oJavaRead, dir, false);
			log.info("- OK, nome soggetto: "+((Soggetto)oJavaRead).getTipo()+"/"+((Soggetto)oJavaRead).getNome());
			log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("\n");
		}finally {
			Files.delete(javaFileStream.toPath());
		}
		
		// Serializzazione tramite reader e writer
		
		File javaFileReader = new File(dir,"singleObjectReader.ser");
		try {
			FileWriter fwriterJava = new FileWriter(javaFileReader);
			FileReader freaderJava = new FileReader(javaFileReader);
			
			log.info("- Serializzazione (Reader/Writer): ");
			JavaSerializer javaSerializer = new JavaSerializer();
			dataInizio = DateManager.getTimeMillis();
			javaSerializer.writeObject(as, fwriterJava);
			dataFine = DateManager.getTimeMillis();
			fwriterJava.flush();
			fwriterJava.close();
			log.info("- OK");
			log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileReader.length()));
			log.info("\n");
			
			log.info("- Deserializzazione (Reader/Writer): ");
			JavaDeserializer javaDeserializer = new JavaDeserializer();
			dataInizio = DateManager.getTimeMillis();
			Object oJavaRead = javaDeserializer.readObject(freaderJava, as.getClass());
			dataFine = DateManager.getTimeMillis();
			freaderJava.close();
			SerializationClientTest.equals(as, (AccordoServizioParteComune)oJavaRead, dir);
			log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJavaRead))+" allegati:("+((AccordoServizioParteComune)oJavaRead).sizeAllegatoList()+")");
			log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("\n");
		}finally {
			Files.delete(javaFileReader.toPath());
		}
		
		// Serializzazione tramite get e read in Object
		
		log.info("- Serializzazione (Object): ");
		JavaSerializer javaSerializer = new JavaSerializer();
		dataInizio = DateManager.getTimeMillis();
		String javaSerializationObject = javaSerializer.getObject(as);
		dataFine = DateManager.getTimeMillis();
		log.info("- OK");
		log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Object): ");
		JavaDeserializer javaDeserializer = new JavaDeserializer();
		dataInizio = DateManager.getTimeMillis();
		Object oJavaRead = javaDeserializer.getObject(javaSerializationObject, as.getClass());
		dataFine = DateManager.getTimeMillis();
		SerializationClientTest.equals(as, (AccordoServizioParteComune)oJavaRead, dir);
		log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJavaRead))+" allegati:("+((AccordoServizioParteComune)oJavaRead).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione Enumerations
		
		log.info("- Serializzazione (Enumerations): ");
		javaSerializer = new JavaSerializer();
		dataInizio = DateManager.getTimeMillis();
		javaSerializationObject = javaSerializer.getObject(TestEnumerations.VALORE1);
		dataFine = DateManager.getTimeMillis();
		log.info("- OK");
		log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Enumerations): ");
		javaDeserializer = new JavaDeserializer();
		dataInizio = DateManager.getTimeMillis();
		oJavaRead = javaDeserializer.getObject(javaSerializationObject, TestEnumerations.class);
		dataFine = DateManager.getTimeMillis();
		if(((TestEnumerations)oJavaRead).toString().equals(TestEnumerations.VALORE1.toString())==false){
			throw new Exception("Enumeration originale ["+TestEnumerations.VALORE1.toString()+"] e ricostruita["+((TestEnumerations)oJavaRead).toString()+"] differiscono");
		}
		log.info("- OK, enum value: "+oJavaRead);
		log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		log.info("\n");
		log.info("********** Serializzazione Java (Array/List/Set) ****************");
		log.info("\n");
		
		// Serializzazione di array
		
		javaFileStream = new File(dir,"array.ser");
		try {
			FileOutputStream foutJava = new FileOutputStream(javaFileStream);
			FileInputStream finJava = new FileInputStream(javaFileStream);
			
			log.info("- Serializzazione (Array): ");
			javaSerializer = new JavaSerializer();
			dataInizio = DateManager.getTimeMillis();
			javaSerializer.writeObject(testSerializzazioneLista.toArray(), foutJava);
			dataFine = DateManager.getTimeMillis();
			foutJava.flush();
			foutJava.close();
			log.info("- OK");
			log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileStream.length()));
			log.info("\n");
			
			log.info("- Deserializzazione (Array): ");
			javaDeserializer = new JavaDeserializer();
			dataInizio = DateManager.getTimeMillis();
			oJavaRead = javaDeserializer.readObject(finJava, testSerializzazioneLista.toArray().getClass());
			dataFine = DateManager.getTimeMillis();
			finJava.close();
			Object [] vJavaArray = (Object [])oJavaRead;
			SerializationClientTest.equalsOpenSPCoopObject(testSerializzazioneLista.toArray(),vJavaArray,dir);
			log.info("- OK, nome soggetto: "+((Soggetto)vJavaArray[1]).getTipo()+"/"+((Soggetto)vJavaArray[1]).getNome()+"   nome accordo: "+
					idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)vJavaArray[0]))+" allegati:("+((AccordoServizioParteComune)vJavaArray[0]).sizeAllegatoList()+")");
			log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("\n");
		}finally {
			Files.delete(javaFileStream.toPath());
		}
		
		// Serializzazione di list
		
		javaFileStream = new File(dir,"list.ser");
		try {
			FileOutputStream foutJava = new FileOutputStream(javaFileStream);
			FileInputStream finJava = new FileInputStream(javaFileStream);
			
			log.info("- Serializzazione (List): ");
			javaSerializer = new JavaSerializer();
			dataInizio = DateManager.getTimeMillis();
			javaSerializer.writeObject(testSerializzazioneLista, foutJava);
			dataFine = DateManager.getTimeMillis();
			foutJava.flush();
			foutJava.close();
			log.info("- OK");
			log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileStream.length()));
			log.info("\n");
			
			log.info("- Deserializzazione (List): ");
			javaDeserializer = new JavaDeserializer();
			dataInizio = DateManager.getTimeMillis();
			oJavaRead = javaDeserializer.readObject(finJava, testSerializzazioneLista.getClass());
			dataFine = DateManager.getTimeMillis();
			finJava.close();
			List<?> vJavaList = (List<?>)oJavaRead;
			SerializationClientTest.equalsOpenSPCoopObject(testSerializzazioneLista.toArray(),vJavaList.toArray(),dir);
			log.info("- OK, nome soggetto: "+((Soggetto)vJavaList.get(1)).getTipo()+"/"+((Soggetto)vJavaList.get(1)).getNome()+"   nome accordo: "+
					idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)vJavaList.get(0)))+" allegati:("+((AccordoServizioParteComune)vJavaList.get(0)).sizeAllegatoList()+")");
			log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("\n");
		}finally {
			Files.delete(javaFileStream.toPath());
		}

		
		// Serializzazione di Set
		
		javaFileStream = new File(dir,"set.ser");
		try {
			FileOutputStream foutJava = new FileOutputStream(javaFileStream);
			FileInputStream finJava = new FileInputStream(javaFileStream);
			
			log.info("- Serializzazione (Set): ");
			javaSerializer = new JavaSerializer();
			dataInizio = DateManager.getTimeMillis();
			javaSerializer.writeObject(testSerializzazioneSet, foutJava);
			dataFine = DateManager.getTimeMillis();
			foutJava.flush();
			foutJava.close();
			log.info("- OK");
			log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileStream.length()));
			log.info("\n");
			
			log.info("- Deserializzazione (Set): ");
			javaDeserializer = new JavaDeserializer();
			dataInizio = DateManager.getTimeMillis();
			oJavaRead = javaDeserializer.readObject(finJava, testSerializzazioneSet.getClass());
			dataFine = DateManager.getTimeMillis();
			finJava.close();
			Set<?> vJavaSet = (Set<?>)oJavaRead;
			SerializationClientTest.equalsOpenSPCoopObject(testSerializzazioneLista.toArray(),vJavaSet.toArray(),dir);
			log.info("- OK, dimensione set: "+vJavaSet.size()+")");
			log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("\n");
			log.info("\n");
		}finally {
			Files.delete(javaFileStream.toPath());
		}
		
		
		
		
		
		log.info("\n");
		log.info("\n");
		log.info("\n");
		log.info("\n");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		// **************** TEST SERIALIZZAZIONE JSON **************************************
		log.info("\n");
		log.info("********** Serializzazione Json (SingleObject) ****************");
		log.info("\n");
		
		// Serializzazione tramite stream in/out
		
		File jsonFileStream = new File(dir,"singleObjectStream.json");
		try {
			FileOutputStream foutJson = new FileOutputStream(jsonFileStream);
			FileInputStream finJson = new FileInputStream(jsonFileStream);
			
			log.info("- Serializzazione (Stream in/out): ");
			Filter filter = new Filter();
			SerializationConfig config = new SerializationConfig();
			config.setFilter(filter);
			config.setExcludes(excludesJsonSoggetto);
			JsonJacksonSerializer jsonSerializer = new JsonJacksonSerializer(config);
			dataInizio = DateManager.getTimeMillis();
			jsonSerializer.writeObject(soggettoTest, foutJson);
			dataFine = DateManager.getTimeMillis();
			foutJson.flush();
			foutJson.close();
			log.info("- OK");
			log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
			log.info("\n");
			
			log.info("- Deserializzazione (Stream in/out): ");
			config = new SerializationConfig();
			config.setExcludes(excludesJson_date);
			JsonJacksonDeserializer jsonDeserializer = new JsonJacksonDeserializer(config);
			dataInizio = DateManager.getTimeMillis();
			Object oJsonRead = jsonDeserializer.readObject(finJson, soggettoTest.getClass());
			dataFine = DateManager.getTimeMillis();
			finJson.close();
			Soggetto soggettoTestEquals = (Soggetto) soggettoTest.clone();
			soggettoTestEquals.getConnettore().getPropertyList().clear(); // escluso dalle direttive
			soggettoTestEquals.setSuperUser(null); // annotato come transient
			SerializationClientTest.equals(soggettoTestEquals, (Soggetto)oJsonRead, dir, false);
			log.info("- OK, nome soggetto: "+((Soggetto)oJsonRead).getTipo()+"/"+((Soggetto)oJsonRead).getNome()+"");
			log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("\n");
		}finally {
			Files.delete(jsonFileStream.toPath());
		}
		
		// Serializzazione tramite reader e writer
		
		File jsonFileReader = new File(dir,"singleObjectReader.json");
		try {
			FileWriter fwriterJson = new FileWriter(jsonFileReader);
			FileReader freaderJson = new FileReader(jsonFileReader);
			
			log.info("- Serializzazione (Reader/Writer): ");
			Filter filter = new Filter();
			SerializationConfig config = new SerializationConfig();
			config.setFilter(filter);
			config.setExcludes(excludesJsonAccordiServizio);
			JsonJacksonSerializer jsonSerializer = new JsonJacksonSerializer(config);
			dataInizio = DateManager.getTimeMillis();
			jsonSerializer.writeObject(as, fwriterJson);
			dataFine = DateManager.getTimeMillis();
			fwriterJson.close();
			log.info("- OK");
			log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
			log.info("\n");
			
			log.info("- Deserializzazione (Reader/Writer): ");
			config = new SerializationConfig();
			config.setExcludes(excludesJson_date);
			JsonJacksonDeserializer jsonDeserializer = new JsonJacksonDeserializer(config);
			dataInizio = DateManager.getTimeMillis();
			Object oJsonRead = jsonDeserializer.readObject(freaderJson, as.getClass());
			dataFine = DateManager.getTimeMillis();
			freaderJson.close();
			AccordoServizioParteComune asEquals = (AccordoServizioParteComune) as.clone();
			asEquals.getAllegatoList().clear(); // escluso dalle direttive
			SerializationClientTest.equals(asEquals, (AccordoServizioParteComune)oJsonRead, dir);
			log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJsonRead))+" allegati:("+((AccordoServizioParteComune)oJsonRead).sizeAllegatoList()+")");
			log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("\n");
		}finally {
			Files.delete(jsonFileReader.toPath());
		}
		
		// Serializzazione tramite get e read in Object
		
		log.info("- Serializzazione (Object): ");
		Filter filter = new Filter();
		SerializationConfig config = new SerializationConfig();
		config.setFilter(filter);
		config.setExcludes(excludesJsonAccordiServizio);
		JsonJacksonSerializer jsonSerializer = new JsonJacksonSerializer(config);
		dataInizio = DateManager.getTimeMillis();
		String jsonSerializationObject = jsonSerializer.getObject(as);
		dataFine = DateManager.getTimeMillis();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Object): ");
		config = new SerializationConfig();
		config.setExcludes(excludesJson_date);
		JsonJacksonDeserializer jsonDeserializer = new JsonJacksonDeserializer(config);
		dataInizio = DateManager.getTimeMillis();
		Object oJsonRead = jsonDeserializer.getObject(jsonSerializationObject, as.getClass());
		dataFine = DateManager.getTimeMillis();
		AccordoServizioParteComune asEquals = (AccordoServizioParteComune) as.clone();
		asEquals.getAllegatoList().clear(); // escluso dalle direttive
		SerializationClientTest.equals(asEquals, (AccordoServizioParteComune)oJsonRead, dir);
		log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJsonRead))+" allegati:("+((AccordoServizioParteComune)oJsonRead).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione tramite reader e writer senza eclusioni
		
		jsonFileReader = new File(dir,"singleObjectReader.json");
		try {
			FileWriter fwriterJson = new FileWriter(jsonFileReader);
			FileReader freaderJson = new FileReader(jsonFileReader);
			
			log.info("- Serializzazione (Reader/Writer) senza esclusioni: ");
			config = new SerializationConfig();
			config.setFilter(filter);
			//config.setExcludes(excludesJsonAccordiServizio);
			jsonSerializer = new JsonJacksonSerializer(config);
			dataInizio = DateManager.getTimeMillis();
			jsonSerializer.writeObject(as, fwriterJson);
			dataFine = DateManager.getTimeMillis();
			fwriterJson.close();
			log.info("- OK");
			log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
			log.info("\n");
			
			log.info("- Deserializzazione (Reader/Writer) senza esclusioni: ");
			config = new SerializationConfig();
			config.setExcludes(excludesJson_date);
			jsonDeserializer = new JsonJacksonDeserializer(config);
			dataInizio = DateManager.getTimeMillis();
			oJsonRead = jsonDeserializer.readObject(freaderJson, as.getClass());
			dataFine = DateManager.getTimeMillis();
			freaderJson.close();
			SerializationClientTest.equals(as, (AccordoServizioParteComune)oJsonRead, dir);
			log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJsonRead))+" allegati:("+((AccordoServizioParteComune)oJsonRead).sizeAllegatoList()+")");
			log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("\n");
		}finally {
			Files.delete(jsonFileReader.toPath());
		}
		
		// Serializzazione Enumerations
		
		log.info("- Serializzazione (Enumerations): ");
		config = new SerializationConfig();
		config.setFilter(filter);
		jsonSerializer = new JsonJacksonSerializer(config);
		dataInizio = DateManager.getTimeMillis();
		jsonSerializationObject = jsonSerializer.getObject(TestEnumerations.VALORE1);
		dataFine = DateManager.getTimeMillis();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Enumerations): ");
		jsonDeserializer = new JsonJacksonDeserializer();
		dataInizio = DateManager.getTimeMillis();
		oJsonRead = jsonDeserializer.getObject(jsonSerializationObject, TestEnumerations.class);
		dataFine = DateManager.getTimeMillis();
		if(((TestEnumerations)oJsonRead).toString().equals(TestEnumerations.VALORE1.toString())==false){
			throw new Exception("Enumeration originale ["+TestEnumerations.VALORE1.toString()+"] e ricostruita["+((TestEnumerations)oJsonRead).toString()+"] differiscono");
		}
		log.info("- OK, enum value: "+oJsonRead+"");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		log.info("\n");
		log.info("********** Serializzazione Json (Array/List/Set) ****************");
		log.info("\n");
		
		// NOTA: JSON Deve conoscere ESATTAMENTE il tipo utilizzato nelle liste!!
		
		// Serializzazione di array
		
		jsonFileStream = new File(dir,"array.accordi.json");
		try {
			FileOutputStream foutJson = new FileOutputStream(jsonFileStream);
			FileInputStream finJson = new FileInputStream(jsonFileStream);
			
			log.info("- Serializzazione (Array)(Accordi): ");
			config = new SerializationConfig();
			config.setFilter(filter);
			config.setExcludes(excludesJsonAccordiServizio);
			jsonSerializer = new JsonJacksonSerializer(config);
			dataInizio = DateManager.getTimeMillis();
			AccordoServizioParteComune [] arrayJsonTestAccordi = testSerializzazioneListaAccordi.toArray(new AccordoServizioParteComune[1]);
			jsonSerializer.writeObject(arrayJsonTestAccordi, foutJson);
			dataFine = DateManager.getTimeMillis();
			foutJson.flush();
			foutJson.close();
			log.info("- OK");
			log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
			log.info("\n");
			
			log.info("- Deserializzazione (Array)(Accordi): ");
			config = new SerializationConfig();
			config.setExcludes(excludesJson_date);
			jsonDeserializer = new JsonJacksonDeserializer(config);
			dataInizio = DateManager.getTimeMillis();
			oJsonRead = jsonDeserializer.readObject(finJson, AccordoServizioParteComune[].class);
			dataFine = DateManager.getTimeMillis();
			finJson.close();
			Object [] vJsonArray = (Object [])oJsonRead;
			AccordoServizioParteComune [] vJsonArrayTestAccordi = new AccordoServizioParteComune[vJsonArray.length];
			for (int i = 0; i < vJsonArray.length; i++) {
				vJsonArrayTestAccordi[i] = (AccordoServizioParteComune) vJsonArray[i];
			}
			AccordoServizioParteComune [] arrayJsonTestAccordiEquals = new AccordoServizioParteComune[arrayJsonTestAccordi.length];
			int i = 0;
			for (AccordoServizioParteComune asSource : arrayJsonTestAccordi) {
				arrayJsonTestAccordiEquals[i]=(AccordoServizioParteComune) asSource.clone();
				arrayJsonTestAccordiEquals[i].getAllegatoList().clear(); // escluso dalle direttive
				i++;
			}
			SerializationClientTest.equals(arrayJsonTestAccordiEquals,vJsonArrayTestAccordi,dir);
			log.info("- OK, nome accordo: "+
					idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)vJsonArray[0]))+" allegati:("+((AccordoServizioParteComune)vJsonArray[0]).sizeAllegatoList()+")");
			log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("\n");
		}finally {
			Files.delete(jsonFileStream.toPath());
		}
		
		jsonFileStream = new File(dir,"array.soggetti.json");
		try {
			FileOutputStream foutJson = new FileOutputStream(jsonFileStream);
			FileInputStream finJson = new FileInputStream(jsonFileStream);
			
			log.info("- Serializzazione (Array)(Soggetti): ");
			config = new SerializationConfig();
			config.setFilter(filter);
			config.setExcludes(excludesJsonSoggetto);
			jsonSerializer = new JsonJacksonSerializer(config);
			dataInizio = DateManager.getTimeMillis();
			Soggetto [] arrayJsonTestSoggetti = testSerializzazioneListaSoggetti.toArray(new Soggetto[testSerializzazioneListaSoggetti.size()]);
			jsonSerializer.writeObject(arrayJsonTestSoggetti, foutJson);
			dataFine = DateManager.getTimeMillis();
			foutJson.flush();
			foutJson.close();
			log.info("- OK");
			log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
			log.info("\n");
			
			log.info("- Deserializzazione (Array)(Soggetti): ");
			config = new SerializationConfig();
			config.setExcludes(excludesJson_date);
			jsonDeserializer = new JsonJacksonDeserializer(config);
			dataInizio = DateManager.getTimeMillis();
			oJsonRead = jsonDeserializer.readObject(finJson, Soggetto[].class);
			dataFine = DateManager.getTimeMillis();
			finJson.close();
			Object [] vJsonArray = (Object [])oJsonRead;
			Soggetto [] vJsonArrayTestSoggetti = new Soggetto[vJsonArray.length];
			for (int i = 0; i < vJsonArray.length; i++) {
				vJsonArrayTestSoggetti[i] = (Soggetto) vJsonArray[i];
			}
			Soggetto [] arrayJsonTestSoggettiEquals = new Soggetto[arrayJsonTestSoggetti.length];
			int i = 0;
			for (Soggetto soggettoSource : arrayJsonTestSoggetti) {
				arrayJsonTestSoggettiEquals[i]=(Soggetto) soggettoSource.clone();
				arrayJsonTestSoggettiEquals[i].getConnettore().getPropertyList().clear(); // escluso dalle direttive
				arrayJsonTestSoggettiEquals[i].setSuperUser(null); // annotato come transient
				i++;
			}
			SerializationClientTest.equals(arrayJsonTestSoggettiEquals,vJsonArrayTestSoggetti,dir);
			log.info("- OK, nome soggetto: "+((Soggetto)vJsonArray[1]).getTipo()+"/"+((Soggetto)vJsonArray[1]).getNome());
			log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoStringMillisecondi((dataFine-dataInizio), true));
			log.info("\n");
		}finally {
			Files.delete(jsonFileStream.toPath());
		}
		
		
		System.out.println("Testsuite terminata");
	}
	
	
	private static void equalsOpenSPCoopObject(Object[] originale,Object[] ricostruito,File dir) throws Exception{
		for(int i=0; i<originale.length;i++){
			boolean find = false;
			for(int j=0; j<ricostruito.length;j++){
				try{
					if(originale[i] instanceof Soggetto){
						if(ricostruito[j] instanceof Soggetto){
							SerializationClientTest.equals((Soggetto)originale[i],(Soggetto)ricostruito[j],dir,true);
							find = true;	
						}
					}
					if(originale[i] instanceof AccordoServizioParteComune){
						if(ricostruito[j] instanceof AccordoServizioParteComune){
							SerializationClientTest.equals((AccordoServizioParteComune)originale[i],(AccordoServizioParteComune)ricostruito[j],dir);
							find = true;	
						}
					}
				}catch(Exception e){}
			}
			if(!find){
				File tmp = new File(dir,"originaleTestFailed.xml");
				FileOutputStream fout = new FileOutputStream(tmp);
				if(originale[i] instanceof Soggetto){
					fout.write(((Soggetto)originale[i]).toString(false,true).getBytes());
				}
				else{
					fout.write(((AccordoServizioParteComune)originale[i]).toString(false,true).getBytes());
				}
				fout.flush();
				fout.close();
				for(int j=0; j<ricostruito.length;j++){
					tmp = new File(dir,"ricostruitoTestFailed("+j+").xml");
					fout = new FileOutputStream(tmp);
					if(ricostruito[j] instanceof Soggetto){
						fout.write(((Soggetto)ricostruito[j]).toString(false,true).getBytes());
					}
					else{
						fout.write(((AccordoServizioParteComune)ricostruito[j]).toString(false,true).getBytes());
					}
					fout.flush();
					fout.close();
				}
				throw new Exception("Liste non uguali: vedi file originaleTestFailed.xml e files ricostruitoTestFailed(i).xml per capire le differenze");
			}
		}
	}
	
	private static void equals(Soggetto[] originale,Soggetto[] ricostruito,File dir) throws Exception{
		for(int i=0; i<originale.length;i++){
			boolean find = false;
			for(int j=0; j<ricostruito.length;j++){
				try{
					SerializationClientTest.equals(originale[i],ricostruito[j],dir,true);
					find = true;	
				}catch(Exception e){}
			}
			if(!find){
				File tmp = new File(dir,"originaleTestFailed.xml");
				FileOutputStream fout = new FileOutputStream(tmp);
				fout.write(originale[i].toString(false,true).getBytes());
				fout.flush();
				fout.close();
				for(int j=0; j<ricostruito.length;j++){
					tmp = new File(dir,"ricostruitoTestFailed("+j+").xml");
					fout = new FileOutputStream(tmp);
					fout.write(ricostruito[j].toString(false,true).getBytes());
					fout.flush();
					fout.close();
				}
				throw new Exception("Liste non uguali: vedi file originaleTestFailed.xml e files ricostruitoTestFailed(i).xml per capire le differenze");
			}
		}
	}
	private static void equals(AccordoServizioParteComune[] originale,AccordoServizioParteComune[] ricostruito,File dir) throws Exception{
		for(int i=0; i<originale.length;i++){
			boolean find = false;
			for(int j=0; j<ricostruito.length;j++){
				try{
					SerializationClientTest.equals(originale[i],ricostruito[j],dir);
					find = true;	
				}catch(Exception e){}
			}
			if(!find){
				File tmp = new File(dir,"originaleTestFailed.xml");
				FileOutputStream fout = new FileOutputStream(tmp);
				fout.write(originale[i].toString(false,true).getBytes());
				fout.flush();
				fout.close();
				for(int j=0; j<ricostruito.length;j++){
					tmp = new File(dir,"ricostruitoTestFailed("+j+").xml");
					fout = new FileOutputStream(tmp);
					fout.write(ricostruito[j].toString(false,true).getBytes());
					fout.flush();
					fout.close();
				}
				throw new Exception("Liste non uguali: vedi file originaleTestFailed.xml e files ricostruitoTestFailed(i).xml per capire le differenze");
			}
		}
	}
	private static void equals(Soggetto o1,Soggetto o2,File dir, boolean deleteFile) throws Exception{
		if(o1.toString(false,true).equals(o2.toString(false,true))==false){
			File tmp1 = new File(dir,"originaleTestFailedSoggetto.xml");
			File tmp2 = new File(dir,"ricostruitoTestFailedSoggetto.xml");
			try {
				FileOutputStream fout1 = new FileOutputStream(tmp1);
				fout1.write(o1.toString(false,true).getBytes());
				fout1.flush();
				fout1.close();
				FileOutputStream fout2 = new FileOutputStream(tmp2);
				fout2.write(o2.toString(false,true).getBytes());
				fout2.flush();
				fout2.close();
				throw new Exception("Soggetti non uguali: vedi file originaleTestFailedSoggetto.xml e ricostruitoTestFailedSoggetto.xml per capire le differenze");
			}finally {
				if(deleteFile) {
					Files.delete(tmp1.toPath());
					Files.delete(tmp2.toPath());
				}
			}
		}
	}
	private static void equals(AccordoServizioParteComune o1,AccordoServizioParteComune o2,File dir) throws Exception{
		if(o1.toString(false,true).equals(o2.toString(false,true))==false){
			File tmp1 = new File(dir,"originaleTestFailedAS.xml");
			File tmp2 = new File(dir,"ricostruitoTestFailedAS.xml");
			FileOutputStream fout1 = new FileOutputStream(tmp1);
			fout1.write(o1.toString(false,true).getBytes());
			fout1.flush();
			fout1.close();
			FileOutputStream fout2 = new FileOutputStream(tmp2);
			fout2.write(o2.toString(false,true).getBytes());
			fout2.flush();
			fout2.close();
			throw new Exception("Accordi non uguali: vedi file originaleTestFailedAS.xml e ricostruitoTestFailedAS.xml per capire le differenze");
		}
	}
}


