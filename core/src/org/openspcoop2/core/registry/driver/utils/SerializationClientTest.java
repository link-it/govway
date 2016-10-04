/*
 * OpenSPCoop - Customizable API Gateway 
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


package org.openspcoop2.core.registry.driver.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import org.openspcoop2.utils.serialization.JSonDeserializer;
import org.openspcoop2.utils.serialization.JSonSerializer;
import org.openspcoop2.utils.serialization.JavaDeserializer;
import org.openspcoop2.utils.serialization.JavaSerializer;
import org.openspcoop2.utils.serialization.XMLDeserializer;
import org.openspcoop2.utils.serialization.XMLSerializer;
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
		
		File logFile = File.createTempFile("testSerializzazione_", ".log");
		System.out.println("LogMessages write in "+logFile.getAbsolutePath());
		LoggerWrapperFactory.setDefaultLogConfiguration(Level.ALL, false, null, logFile, "%m %n");
		log = LoggerWrapperFactory.getLogger(SerializationClientTest.class);	
		
		// Factory
		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();
		
		long dataInizio = -1;
		long dataFine = -1;
		
		File dir = new File(".");
		if(args.length>0){
			dir = new File(args[0].trim());
			if(dir.exists()){
				if(dir.isDirectory()){
					throw new Exception("Directory ["+dir.getAbsolutePath()+"] gia' esistente");
				}else{
					throw new Exception("Location ["+dir.getAbsolutePath()+"] gia' esistente e inoltre non e' una directory");
				}
			}
			if(dir.mkdir()==false){
				throw new Exception("Directory ["+dir.getAbsolutePath()+"] non creabile");
			}
		}
		
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
		pr.setValore("http://localhost:8080/openspcoop/PA");
		connettore.addProperty(pr);
		soggettoTest.setConnettore(connettore);
		
		AccordoServizioParteComune as = new AccordoServizioParteComune();
		as.setNome("ASTEST");
		as.setVersione("1");
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
		String [] excludesJson_Soggetto = new String [] {"servizio","servizioCorrelato","property"};
		String [] excludesJson_AccordiServizio = new String [] {"specificaSemiformale","allegato","azione","portType"};
		String [] excludesJson_date = new String [] {"day","timezoneOffset"};
		
		// LISTE
		
		List<Object> testSerializzazioneLista = new LinkedList<Object>();
		testSerializzazioneLista.add(as);
		testSerializzazioneLista.add(s1);
		testSerializzazioneLista.add(s2);
		testSerializzazioneLista.add(s3);
		testSerializzazioneLista.add(s4);
		testSerializzazioneLista.add(s5);
		testSerializzazioneLista.add(s6);
		testSerializzazioneLista.add(s7);
		
		List<Soggetto> testSerializzazioneListaSoggetti = new LinkedList<Soggetto>();
		testSerializzazioneListaSoggetti.add(s1);
		testSerializzazioneListaSoggetti.add(s2);
		testSerializzazioneListaSoggetti.add(s3);
		testSerializzazioneListaSoggetti.add(s4);
		testSerializzazioneListaSoggetti.add(s5);
		testSerializzazioneListaSoggetti.add(s6);
		testSerializzazioneListaSoggetti.add(s7);
		
		List<AccordoServizioParteComune> testSerializzazioneListaAccordi = new LinkedList<AccordoServizioParteComune>();
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
		log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Stream in/out): ");
		JavaDeserializer javaDeserializer = new JavaDeserializer();
		dataInizio = DateManager.getTimeMillis();
		Object oJavaRead = javaDeserializer.readObject(finJava, soggettoTest.getClass());
		dataFine = DateManager.getTimeMillis();
		finJava.close();
		SerializationClientTest.equals(soggettoTest, (Soggetto)oJavaRead, dir);
		log.info("- OK, nome soggetto: "+((Soggetto)oJavaRead).getTipo()+"/"+((Soggetto)oJavaRead).getNome());
		log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione tramite reader e writer
		
		File javaFileReader = new File(dir,"singleObjectReader.ser");
		FileWriter fwriterJava = new FileWriter(javaFileReader);
		FileReader freaderJava = new FileReader(javaFileReader);
		
		log.info("- Serializzazione (Reader/Writer): ");
		javaSerializer = new JavaSerializer();
		dataInizio = DateManager.getTimeMillis();
		javaSerializer.writeObject(as, fwriterJava);
		dataFine = DateManager.getTimeMillis();
		fwriterJava.flush();
		fwriterJava.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Reader/Writer): ");
		javaDeserializer = new JavaDeserializer();
		dataInizio = DateManager.getTimeMillis();
		oJavaRead = javaDeserializer.readObject(freaderJava, as.getClass());
		dataFine = DateManager.getTimeMillis();
		freaderJava.close();
		SerializationClientTest.equals(as, (AccordoServizioParteComune)oJavaRead, dir);
		log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJavaRead))+" allegati:("+((AccordoServizioParteComune)oJavaRead).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione tramite get e read in Object
		
		log.info("- Serializzazione (Object): ");
		javaSerializer = new JavaSerializer();
		dataInizio = DateManager.getTimeMillis();
		String javaSerializationObject = javaSerializer.getObject(as);
		dataFine = DateManager.getTimeMillis();
		log.info("- OK");
		log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Object): ");
		javaDeserializer = new JavaDeserializer();
		dataInizio = DateManager.getTimeMillis();
		oJavaRead = javaDeserializer.getObject(javaSerializationObject, as.getClass());
		dataFine = DateManager.getTimeMillis();
		freaderJava.close();
		SerializationClientTest.equals(as, (AccordoServizioParteComune)oJavaRead, dir);
		log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJavaRead))+" allegati:("+((AccordoServizioParteComune)oJavaRead).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione Enumerations
		
		log.info("- Serializzazione (Enumerations): ");
		javaSerializer = new JavaSerializer();
		dataInizio = DateManager.getTimeMillis();
		javaSerializationObject = javaSerializer.getObject(TestEnumerations.VALORE1);
		dataFine = DateManager.getTimeMillis();
		log.info("- OK");
		log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(javaFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Enumerations): ");
		javaDeserializer = new JavaDeserializer();
		dataInizio = DateManager.getTimeMillis();
		oJavaRead = javaDeserializer.getObject(javaSerializationObject, TestEnumerations.class);
		dataFine = DateManager.getTimeMillis();
		freaderJava.close();
		if(((TestEnumerations)oJavaRead).toString().equals(TestEnumerations.VALORE1.toString())==false){
			throw new Exception("Enumeration originale ["+TestEnumerations.VALORE1.toString()+"] e ricostruita["+((TestEnumerations)oJavaRead).toString()+"] differiscono");
		}
		log.info("- OK, enum value: "+oJavaRead);
		log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		log.info("\n");
		log.info("********** Serializzazione Java (Array/List/Set) ****************");
		log.info("\n");
		
		// Serializzazione di array
		
		javaFileStream = new File(dir,"array.ser");
		foutJava = new FileOutputStream(javaFileStream);
		finJava = new FileInputStream(javaFileStream);
		
		log.info("- Serializzazione (Array): ");
		javaSerializer = new JavaSerializer();
		dataInizio = DateManager.getTimeMillis();
		javaSerializer.writeObject(testSerializzazioneLista.toArray(), foutJava);
		dataFine = DateManager.getTimeMillis();
		foutJava.flush();
		foutJava.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
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
		log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");

		
		// Serializzazione di list
		
		javaFileStream = new File(dir,"list.ser");
		foutJava = new FileOutputStream(javaFileStream);
		finJava = new FileInputStream(javaFileStream);
		
		log.info("- Serializzazione (List): ");
		javaSerializer = new JavaSerializer();
		dataInizio = DateManager.getTimeMillis();
		javaSerializer.writeObject(testSerializzazioneLista, foutJava);
		dataFine = DateManager.getTimeMillis();
		foutJava.flush();
		foutJava.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
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
		log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");

		
		// Serializzazione di Set
		
		javaFileStream = new File(dir,"set.ser");
		foutJava = new FileOutputStream(javaFileStream);
		finJava = new FileInputStream(javaFileStream);
		
		log.info("- Serializzazione (Set): ");
		javaSerializer = new JavaSerializer();
		dataInizio = DateManager.getTimeMillis();
		javaSerializer.writeObject(testSerializzazioneSet, foutJava);
		dataFine = DateManager.getTimeMillis();
		foutJava.flush();
		foutJava.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
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
		log.info("- Costo ms deserializzazione java: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		log.info("\n");
		
		
		
		
		
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
		FileOutputStream foutJson = new FileOutputStream(jsonFileStream);
		FileInputStream finJson = new FileInputStream(jsonFileStream);
		
		log.info("- Serializzazione (Stream in/out): ");
		Filter filter = new Filter();
		JSonSerializer jsonSerializer = new JSonSerializer(filter,excludesJson_Soggetto);
		dataInizio = DateManager.getTimeMillis();
		jsonSerializer.writeObject(soggettoTest, foutJson);
		dataFine = DateManager.getTimeMillis();
		foutJson.flush();
		foutJson.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Stream in/out): ");
		JSonDeserializer jsonDeserializer = new JSonDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		Object oJsonRead = jsonDeserializer.readObject(finJson, soggettoTest.getClass());
		dataFine = DateManager.getTimeMillis();
		finJson.close();
		SerializationClientTest.equals(soggettoTest, (Soggetto)oJsonRead, dir);
		log.info("- OK, nome soggetto: "+((Soggetto)oJsonRead).getTipo()+"/"+((Soggetto)oJsonRead).getNome()+"");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione tramite reader e writer
		
		File jsonFileReader = new File(dir,"singleObjectReader.json");
		FileWriter fwriterJson = new FileWriter(jsonFileReader);
		FileReader freaderJson = new FileReader(jsonFileReader);
		
		log.info("- Serializzazione (Reader/Writer): ");
		jsonSerializer = new JSonSerializer(filter,excludesJson_AccordiServizio);
		dataInizio = DateManager.getTimeMillis();
		jsonSerializer.writeObject(as, fwriterJson);
		dataFine = DateManager.getTimeMillis();
		fwriterJson.flush();
		fwriterJson.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Reader/Writer): ");
		jsonDeserializer = new JSonDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonRead = jsonDeserializer.readObject(freaderJson, as.getClass());
		dataFine = DateManager.getTimeMillis();
		freaderJson.close();
		SerializationClientTest.equals(as, (AccordoServizioParteComune)oJsonRead, dir);
		log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJsonRead))+" allegati:("+((AccordoServizioParteComune)oJsonRead).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione tramite get e read in Object
		
		log.info("- Serializzazione (Object): ");
		jsonSerializer = new JSonSerializer(filter,excludesJson_AccordiServizio);
		dataInizio = DateManager.getTimeMillis();
		String jsonSerializationObject = jsonSerializer.getObject(as);
		dataFine = DateManager.getTimeMillis();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Object): ");
		jsonDeserializer = new JSonDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonRead = jsonDeserializer.getObject(jsonSerializationObject, as.getClass());
		dataFine = DateManager.getTimeMillis();
		freaderJson.close();
		SerializationClientTest.equals(as, (AccordoServizioParteComune)oJsonRead, dir);
		log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJsonRead))+" allegati:("+((AccordoServizioParteComune)oJsonRead).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione Enumerations
		
		log.info("- Serializzazione (Enumerations): ");
		jsonSerializer = new JSonSerializer(filter);
		dataInizio = DateManager.getTimeMillis();
		jsonSerializationObject = jsonSerializer.getObject(TestEnumerations.VALORE1);
		dataFine = DateManager.getTimeMillis();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Enumerations): ");
		jsonDeserializer = new JSonDeserializer();
		dataInizio = DateManager.getTimeMillis();
		oJsonRead = jsonDeserializer.getObject(jsonSerializationObject, TestEnumerations.class);
		dataFine = DateManager.getTimeMillis();
		freaderJson.close();
		if(((TestEnumerations)oJsonRead).toString().equals(TestEnumerations.VALORE1.toString())==false){
			throw new Exception("Enumeration originale ["+TestEnumerations.VALORE1.toString()+"] e ricostruita["+((TestEnumerations)oJsonRead).toString()+"] differiscono");
		}
		log.info("- OK, enum value: "+oJsonRead+"");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		log.info("\n");
		log.info("********** Serializzazione Json (Array/List/Set) ****************");
		log.info("\n");
		
		// NOTA: JSON Deve conoscere ESATTAMENTE il tipo utilizzato nelle liste!!
		
		// Serializzazione di array
		
		jsonFileStream = new File(dir,"array.accordi.json");
		foutJson = new FileOutputStream(jsonFileStream);
		finJson = new FileInputStream(jsonFileStream);
		
		log.info("- Serializzazione (Array)(Accordi): ");
		jsonSerializer = new JSonSerializer(filter,excludesJson_AccordiServizio);
		dataInizio = DateManager.getTimeMillis();
		AccordoServizioParteComune [] arrayJsonTestAccordi = testSerializzazioneListaAccordi.toArray(new AccordoServizioParteComune[1]);
		jsonSerializer.writeObject(arrayJsonTestAccordi, foutJson);
		dataFine = DateManager.getTimeMillis();
		foutJson.flush();
		foutJson.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Array)(Accordi): ");
		jsonDeserializer = new JSonDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonRead = jsonDeserializer.readObject(finJson, AccordoServizioParteComune[].class);
		dataFine = DateManager.getTimeMillis();
		finJson.close();
		Object [] vJsonArray = (Object [])oJsonRead;
		AccordoServizioParteComune [] vJsonArrayTestAccordi = new AccordoServizioParteComune[vJsonArray.length];
		for (int i = 0; i < vJsonArray.length; i++) {
			vJsonArrayTestAccordi[i] = (AccordoServizioParteComune) vJsonArray[i];
		}
		SerializationClientTest.equals(arrayJsonTestAccordi,vJsonArrayTestAccordi,dir);
		log.info("- OK, nome accordo: "+
				idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)vJsonArray[0]))+" allegati:("+((AccordoServizioParteComune)vJsonArray[0]).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		jsonFileStream = new File(dir,"array.soggetti.json");
		foutJson = new FileOutputStream(jsonFileStream);
		finJson = new FileInputStream(jsonFileStream);
		
		log.info("- Serializzazione (Array)(Soggetti): ");
		jsonSerializer = new JSonSerializer(filter,excludesJson_Soggetto);
		dataInizio = DateManager.getTimeMillis();
		Soggetto [] arrayJsonTestSoggetti = testSerializzazioneListaSoggetti.toArray(new Soggetto[testSerializzazioneListaSoggetti.size()]);
		jsonSerializer.writeObject(arrayJsonTestSoggetti, foutJson);
		dataFine = DateManager.getTimeMillis();
		foutJson.flush();
		foutJson.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Array)(Soggetti): ");
		jsonDeserializer = new JSonDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonRead = jsonDeserializer.readObject(finJson, Soggetto[].class);
		dataFine = DateManager.getTimeMillis();
		finJson.close();
		vJsonArray = (Object [])oJsonRead;
		Soggetto [] vJsonArrayTestSoggetti = new Soggetto[vJsonArray.length];
		for (int i = 0; i < vJsonArray.length; i++) {
			vJsonArrayTestSoggetti[i] = (Soggetto) vJsonArray[i];
		}
		SerializationClientTest.equals(arrayJsonTestSoggetti,vJsonArrayTestSoggetti,dir);
		log.info("- OK, nome soggetto: "+((Soggetto)vJsonArray[1]).getTipo()+"/"+((Soggetto)vJsonArray[1]).getNome());
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");

		
		// Serializzazione di list
		
		jsonFileStream = new File(dir,"list.accordi.json");
		foutJson = new FileOutputStream(jsonFileStream);
		finJson = new FileInputStream(jsonFileStream);
		
		log.info("- Serializzazione (List)(Accordi): ");
		jsonSerializer = new JSonSerializer(filter,excludesJson_AccordiServizio);
		dataInizio = DateManager.getTimeMillis();
		jsonSerializer.writeObject(testSerializzazioneListaAccordi, foutJson);
		dataFine = DateManager.getTimeMillis();
		foutJson.flush();
		foutJson.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (List)(Accordi): ");
		jsonDeserializer = new JSonDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonRead = jsonDeserializer.readListObject(finJson, testSerializzazioneListaAccordi.getClass(), AccordoServizioParteComune.class);
		dataFine = DateManager.getTimeMillis();
		finJson.close();
		List<?> vJsonList = (List<?>)oJsonRead;
		AccordoServizioParteComune [] originaleAccordi = testSerializzazioneSetAccordi.toArray(new AccordoServizioParteComune[1]);
		AccordoServizioParteComune [] ricostruitoAccordi = vJsonList.toArray(new AccordoServizioParteComune[1]);
		SerializationClientTest.equals(originaleAccordi,ricostruitoAccordi,dir);
		log.info("- OK, nome accordo: "+
				idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)vJsonList.get(0)))+" allegati:("+((AccordoServizioParteComune)vJsonList.get(0)).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		jsonFileStream = new File(dir,"list.soggetti.json");
		foutJson = new FileOutputStream(jsonFileStream);
		finJson = new FileInputStream(jsonFileStream);
		
		log.info("- Serializzazione (List)(Soggetti): ");
		jsonSerializer = new JSonSerializer(filter,excludesJson_Soggetto);
		dataInizio = DateManager.getTimeMillis();
		jsonSerializer.writeObject(testSerializzazioneListaSoggetti, foutJson);
		dataFine = DateManager.getTimeMillis();
		foutJson.flush();
		foutJson.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (List)(Soggetti): ");
		jsonDeserializer = new JSonDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonRead = jsonDeserializer.readListObject(finJson, testSerializzazioneListaSoggetti.getClass(), Soggetto.class);
		dataFine = DateManager.getTimeMillis();
		finJson.close();
		vJsonList = (List<?>)oJsonRead;
		Soggetto [] originaleSoggetti = testSerializzazioneSetSoggetti.toArray(new Soggetto[1]);
		Soggetto [] ricostruitoSoggetti = vJsonList.toArray(new Soggetto[1]);
		log.info("- OK, nome soggetto: "+((Soggetto)vJsonList.get(1)).getTipo()+"/"+((Soggetto)vJsonList.get(1)).getNome()+")");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");

		
		// Serializzazione di Set
		
		jsonFileStream = new File(dir,"set.accordi.json");
		foutJson = new FileOutputStream(jsonFileStream);
		finJson = new FileInputStream(jsonFileStream);
		
		log.info("- Serializzazione (Set)(Accordi) (E' normale uno stackatrace: IllegalArgumentException): ");
		jsonSerializer = new JSonSerializer(filter,excludesJson_AccordiServizio);
		dataInizio = DateManager.getTimeMillis();
		jsonSerializer.writeObject(testSerializzazioneSetAccordi, foutJson); // NOTA: e' normale che stampa un errore, e' l'equals degli oggetti Openspcoop che non e' perfetto.
		dataFine = DateManager.getTimeMillis();
		foutJson.flush();
		foutJson.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Set)(Accordi): ");
		jsonDeserializer = new JSonDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonRead = jsonDeserializer.readSetObject(finJson, testSerializzazioneSetAccordi.getClass(), AccordoServizioParteComune.class);
		dataFine = DateManager.getTimeMillis();
		finJson.close();
		Set<?> vJsonSet = (Set<?>)oJsonRead;
		originaleAccordi = testSerializzazioneSetAccordi.toArray(new AccordoServizioParteComune[1]);
		ricostruitoAccordi = vJsonSet.toArray(new AccordoServizioParteComune[1]);
		SerializationClientTest.equals(originaleAccordi,ricostruitoAccordi,dir);
		log.info("- OK, dimensione set: "+vJsonSet.size()+")");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		log.info("\n");
		
		jsonFileStream = new File(dir,"set.soggetti.json");
		foutJson = new FileOutputStream(jsonFileStream);
		finJson = new FileInputStream(jsonFileStream);
		
		log.info("- Serializzazione (Set)(Soggetti): ");
		jsonSerializer = new JSonSerializer(filter,excludesJson_Soggetto);
		dataInizio = DateManager.getTimeMillis();
		jsonSerializer.writeObject(testSerializzazioneSetSoggetti, foutJson);
		dataFine = DateManager.getTimeMillis();
		foutJson.flush();
		foutJson.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Set)(Soggetti): ");
		jsonDeserializer = new JSonDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonRead = jsonDeserializer.readSetObject(finJson, testSerializzazioneSetSoggetti.getClass(), Soggetto.class);
		dataFine = DateManager.getTimeMillis();
		finJson.close();
		vJsonSet = (Set<?>)oJsonRead;
		originaleSoggetti = testSerializzazioneSetSoggetti.toArray(new Soggetto[1]);
		ricostruitoSoggetti = vJsonSet.toArray(new Soggetto[1]);
		SerializationClientTest.equals(originaleSoggetti,ricostruitoSoggetti,dir);
		log.info("- OK, dimensione set: "+vJsonSet.size()+")");
		log.info("- Costo ms deserializzazione json: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		log.info("\n");
		
		
		
		
		
		
		
		
	
		
		
		
		
		
		
		// **************** TEST SERIALIZZAZIONE JSON+XML **************************************
		log.info("\n");
		log.info("********** Serializzazione JsonXML (SingleObject) ****************");
		log.info("\n");
		
		// Serializzazione tramite stream in/out
		
		File jsonXMLFileStream = new File(dir,"singleObjectStream.jsonXML");
		FileOutputStream foutJsonXML = new FileOutputStream(jsonXMLFileStream);
		FileInputStream finJsonXML = new FileInputStream(jsonXMLFileStream);
		
		log.info("- Serializzazione (Stream in/out): ");
		XMLSerializer jsonXMLSerializer = new XMLSerializer(filter,excludesJson_Soggetto);
		dataInizio = DateManager.getTimeMillis();
		jsonXMLSerializer.writeObject(soggettoTest, foutJsonXML);
		dataFine = DateManager.getTimeMillis();
		foutJsonXML.flush();
		foutJsonXML.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonXMLFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Stream in/out): ");
		XMLDeserializer jsonXMLDeserializer = new XMLDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		Object oJsonXMLRead = jsonXMLDeserializer.readObject(finJsonXML, soggettoTest.getClass());
		dataFine = DateManager.getTimeMillis();
		finJsonXML.close();
		SerializationClientTest.equals(soggettoTest,(Soggetto)oJsonXMLRead,dir);
		log.info("- OK, nome soggetto: "+((Soggetto)oJsonXMLRead).getTipo()+"/"+((Soggetto)oJsonXMLRead).getNome()+"");
		log.info("- Costo ms deserializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione tramite reader e writer
		
		File jsonXMLFileReader = new File(dir,"singleObjectReader.jsonXML");
		FileWriter fwriterJsonXML = new FileWriter(jsonXMLFileReader);
		FileReader freaderJsonXML = new FileReader(jsonXMLFileReader);
		
		log.info("- Serializzazione (Reader/Writer): ");
		jsonXMLSerializer = new XMLSerializer(filter,excludesJson_AccordiServizio);
		dataInizio = DateManager.getTimeMillis();
		jsonXMLSerializer.writeObject(as, fwriterJsonXML);
		dataFine = DateManager.getTimeMillis();
		fwriterJsonXML.flush();
		fwriterJsonXML.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonXMLFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Reader/Writer): ");
		jsonXMLDeserializer = new XMLDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonXMLRead = jsonXMLDeserializer.readObject(freaderJsonXML, as.getClass());
		dataFine = DateManager.getTimeMillis();
		freaderJsonXML.close();
		SerializationClientTest.equals(as, (AccordoServizioParteComune)oJsonXMLRead, dir);
		log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJsonXMLRead))+" allegati:("+((AccordoServizioParteComune)oJsonXMLRead).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione tramite get e read in Object
		
		log.info("- Serializzazione (Object): ");
		jsonXMLSerializer = new XMLSerializer(filter,excludesJson_AccordiServizio);
		dataInizio = DateManager.getTimeMillis();
		String jsonXMLSerializationObject = jsonXMLSerializer.getObject(as);
		dataFine = DateManager.getTimeMillis();
		log.info("- OK");
		log.info("- Costo ms serializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonXMLFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Object): ");
		jsonXMLDeserializer = new XMLDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonXMLRead = jsonXMLDeserializer.getObject(jsonXMLSerializationObject, as.getClass());
		dataFine = DateManager.getTimeMillis();
		freaderJsonXML.close();
		SerializationClientTest.equals(as, (AccordoServizioParteComune)oJsonXMLRead, dir);
		log.info("- OK, accordo: "+idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)oJsonXMLRead))+" allegati:("+((AccordoServizioParteComune)oJsonXMLRead).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		// Serializzazione Enumerations
		
		log.info("- Serializzazione (Enumerations): ");
		jsonXMLSerializer = new XMLSerializer(filter);
		dataInizio = DateManager.getTimeMillis();
		jsonXMLSerializationObject = jsonXMLSerializer.getObject(TestEnumerations.VALORE1);
		dataFine = DateManager.getTimeMillis();
		log.info("- OK");
		log.info("- Costo ms serializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonXMLFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Enumerations): ");
		jsonXMLDeserializer = new XMLDeserializer();
		dataInizio = DateManager.getTimeMillis();
		oJsonXMLRead = jsonXMLDeserializer.getObject(jsonXMLSerializationObject, TestEnumerations.class);
		dataFine = DateManager.getTimeMillis();
		freaderJsonXML.close();
		if(((TestEnumerations)oJsonXMLRead).toString().equals(TestEnumerations.VALORE1.toString())==false){
			throw new Exception("Enumeration originale ["+TestEnumerations.VALORE1.toString()+"] e ricostruita["+((TestEnumerations)oJsonXMLRead).toString()+"] differiscono");
		}
		log.info("- OK, enum value: "+oJsonXMLRead);
		log.info("- Costo ms deserializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		log.info("\n");
		log.info("********** Serializzazione JsonXML (Array/List/Set) ****************");
		log.info("\n");
		
		// NOTA: JSON Deve conoscere ESATTAMENTE il tipo utilizzato nelle liste!!
		
		// Serializzazione di array
		
		jsonXMLFileStream = new File(dir,"array.accordi.jsonXML");
		foutJsonXML = new FileOutputStream(jsonXMLFileStream);
		finJsonXML = new FileInputStream(jsonXMLFileStream);
		
		log.info("- Serializzazione (Array)(Accordi): ");
		jsonXMLSerializer = new XMLSerializer(filter,excludesJson_AccordiServizio);
		dataInizio = DateManager.getTimeMillis();
		AccordoServizioParteComune [] arrayJsonXMLTestAccordi = testSerializzazioneListaAccordi.toArray(new AccordoServizioParteComune[1]);
		jsonXMLSerializer.writeObject(arrayJsonXMLTestAccordi, foutJsonXML);
		dataFine = DateManager.getTimeMillis();
		foutJsonXML.flush();
		foutJsonXML.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonXMLFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Array)(Accordi): ");
		jsonXMLDeserializer = new XMLDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonXMLRead = jsonXMLDeserializer.readObject(finJsonXML, AccordoServizioParteComune[].class);
		dataFine = DateManager.getTimeMillis();
		finJsonXML.close();
		Object [] vJsonXMLArray = (Object [])oJsonXMLRead;
		AccordoServizioParteComune [] vJsonXMLArrayTestAccordi = new AccordoServizioParteComune[vJsonXMLArray.length];
		for (int i = 0; i < vJsonXMLArray.length; i++) {
			vJsonXMLArrayTestAccordi[i] = (AccordoServizioParteComune) vJsonXMLArray[i];
		}
		SerializationClientTest.equals(arrayJsonXMLTestAccordi,vJsonXMLArrayTestAccordi,dir);
		log.info("- OK, nome accordo: "+
				idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)vJsonXMLArray[0]))+" allegati:("+((AccordoServizioParteComune)vJsonXMLArray[0]).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		jsonXMLFileStream = new File(dir,"array.soggetti.jsonXML");
		foutJsonXML = new FileOutputStream(jsonXMLFileStream);
		finJsonXML = new FileInputStream(jsonXMLFileStream);
		
		log.info("- Serializzazione (Array)(Soggetti): ");
		jsonXMLSerializer = new XMLSerializer(filter,excludesJson_Soggetto);
		dataInizio = DateManager.getTimeMillis();
		Soggetto [] arrayJsonXMLTestSoggetti = testSerializzazioneListaSoggetti.toArray(new Soggetto[testSerializzazioneListaSoggetti.size()]);
		jsonXMLSerializer.writeObject(arrayJsonXMLTestSoggetti, foutJsonXML);
		dataFine = DateManager.getTimeMillis();
		foutJsonXML.flush();
		foutJsonXML.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonXMLFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Array)(Soggetti): ");
		jsonXMLDeserializer = new XMLDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonXMLRead = jsonXMLDeserializer.readObject(finJsonXML, Soggetto[].class);
		dataFine = DateManager.getTimeMillis();
		finJsonXML.close();
		vJsonXMLArray = (Object [])oJsonXMLRead;
		Soggetto [] vJsonXMLArrayTestSoggetti = new Soggetto[vJsonXMLArray.length];
		for (int i = 0; i < vJsonXMLArray.length; i++) {
			vJsonXMLArrayTestSoggetti[i] = (Soggetto) vJsonXMLArray[i];
		}
		SerializationClientTest.equals(arrayJsonXMLTestSoggetti,vJsonXMLArrayTestSoggetti,dir);
		log.info("- OK, nome soggetto: "+((Soggetto)vJsonXMLArray[1]).getTipo()+"/"+((Soggetto)vJsonXMLArray[1]).getNome());
		log.info("- Costo ms deserializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");

		
		// Serializzazione di list
		
		jsonXMLFileStream = new File(dir,"list.accordi.jsonXML");
		foutJsonXML = new FileOutputStream(jsonXMLFileStream);
		finJsonXML = new FileInputStream(jsonXMLFileStream);
		
		log.info("- Serializzazione (List)(Accordi): ");
		jsonXMLSerializer = new XMLSerializer(filter,excludesJson_AccordiServizio);
		dataInizio = DateManager.getTimeMillis();
		jsonXMLSerializer.writeObject(testSerializzazioneListaAccordi, foutJsonXML);
		dataFine = DateManager.getTimeMillis();
		foutJsonXML.flush();
		foutJsonXML.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonXMLFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (List)(Accordi): ");
		jsonXMLDeserializer = new XMLDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonXMLRead = jsonXMLDeserializer.readListObject(finJsonXML, testSerializzazioneListaAccordi.getClass(), AccordoServizioParteComune.class);
		dataFine = DateManager.getTimeMillis();
		finJsonXML.close();
		List<?> vJsonXMLList = (List<?>)oJsonXMLRead;
		originaleAccordi = testSerializzazioneSetAccordi.toArray(new AccordoServizioParteComune[1]);
		ricostruitoAccordi = vJsonXMLList.toArray(new AccordoServizioParteComune[1]);
		SerializationClientTest.equals(originaleAccordi,ricostruitoAccordi,dir);
		log.info("- OK, nome accordo: "+
				idAccordoFactory.getUriFromAccordo(((AccordoServizioParteComune)vJsonXMLList.get(0)))+" allegati:("+((AccordoServizioParteComune)vJsonXMLList.get(0)).sizeAllegatoList()+")");
		log.info("- Costo ms deserializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		
		jsonXMLFileStream = new File(dir,"list.soggetti.jsonXML");
		foutJsonXML = new FileOutputStream(jsonXMLFileStream);
		finJsonXML = new FileInputStream(jsonXMLFileStream);
		
		log.info("- Serializzazione (List)(Soggetti): ");
		jsonXMLSerializer = new XMLSerializer(filter,excludesJson_Soggetto);
		dataInizio = DateManager.getTimeMillis();
		jsonXMLSerializer.writeObject(testSerializzazioneListaSoggetti, foutJsonXML);
		dataFine = DateManager.getTimeMillis();
		foutJsonXML.flush();
		foutJsonXML.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonXMLFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (List)(Soggetti): ");
		jsonXMLDeserializer = new XMLDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonXMLRead = jsonXMLDeserializer.readListObject(finJsonXML, testSerializzazioneListaSoggetti.getClass(), Soggetto.class);
		dataFine = DateManager.getTimeMillis();
		finJsonXML.close();
		vJsonXMLList = (List<?>)oJsonXMLRead;
		originaleSoggetti = testSerializzazioneSetSoggetti.toArray(new Soggetto[1]);
		ricostruitoSoggetti = vJsonXMLList.toArray(new Soggetto[1]);
		log.info("- OK, nome soggetto: "+((Soggetto)vJsonXMLList.get(1)).getTipo()+"/"+((Soggetto)vJsonXMLList.get(1)).getNome()+")");
		log.info("- Costo ms deserializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");

		
		// Serializzazione di Set
		
		jsonXMLFileStream = new File(dir,"set.accordi.jsonXML");
		foutJsonXML = new FileOutputStream(jsonXMLFileStream);
		finJsonXML = new FileInputStream(jsonXMLFileStream);
		
		log.info("- Serializzazione (Set)(Accordi) (E' normale uno stackatrace: IllegalArgumentException): ");
		jsonXMLSerializer = new XMLSerializer(filter,excludesJson_AccordiServizio);
		dataInizio = DateManager.getTimeMillis();
		jsonXMLSerializer.writeObject(testSerializzazioneSetAccordi, foutJsonXML); // NOTA: e' normale che stampa un errore, e' l'equals degli oggetti Openspcoop che non e' perfetto.
		dataFine = DateManager.getTimeMillis();
		foutJsonXML.flush();
		foutJsonXML.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonXMLFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Set)(Accordi): ");
		jsonXMLDeserializer = new XMLDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonXMLRead = jsonXMLDeserializer.readSetObject(finJsonXML, testSerializzazioneSetAccordi.getClass(), AccordoServizioParteComune.class);
		dataFine = DateManager.getTimeMillis();
		finJsonXML.close();
		Set<?> vJsonXMLSet = (Set<?>)oJsonXMLRead;
		originaleAccordi = testSerializzazioneSetAccordi.toArray(new AccordoServizioParteComune[1]);
		ricostruitoAccordi = vJsonXMLSet.toArray(new AccordoServizioParteComune[1]);
		SerializationClientTest.equals(originaleAccordi,ricostruitoAccordi,dir);
		log.info("- OK, dimensione set: "+vJsonXMLSet.size()+")");
		log.info("- Costo ms deserializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		log.info("\n");
		
		jsonXMLFileStream = new File(dir,"set.soggetti.jsonXML");
		foutJsonXML = new FileOutputStream(jsonXMLFileStream);
		finJsonXML = new FileInputStream(jsonXMLFileStream);
		
		log.info("- Serializzazione (Set)(Soggetti): ");
		jsonXMLSerializer = new XMLSerializer(filter,excludesJson_Soggetto);
		dataInizio = DateManager.getTimeMillis();
		jsonXMLSerializer.writeObject(testSerializzazioneSetSoggetti, foutJsonXML);
		dataFine = DateManager.getTimeMillis();
		foutJsonXML.flush();
		foutJsonXML.close();
		log.info("- OK");
		log.info("- Costo ms serializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("- Dimensione oggetto serializzato: "+Utilities.convertBytesToFormatString(jsonXMLFileStream.length()));
		log.info("\n");
		
		log.info("- Deserializzazione (Set)(Soggetti): ");
		jsonXMLDeserializer = new XMLDeserializer(excludesJson_date);
		dataInizio = DateManager.getTimeMillis();
		oJsonXMLRead = jsonXMLDeserializer.readSetObject(finJsonXML, testSerializzazioneSetSoggetti.getClass(), Soggetto.class);
		dataFine = DateManager.getTimeMillis();
		finJsonXML.close();
		vJsonXMLSet = (Set<?>)oJsonXMLRead;
		originaleSoggetti = testSerializzazioneSetSoggetti.toArray(new Soggetto[1]);
		ricostruitoSoggetti = vJsonXMLSet.toArray(new Soggetto[1]);
		SerializationClientTest.equals(originaleSoggetti,ricostruitoSoggetti,dir);
		log.info("- OK, dimensione set: "+vJsonXMLSet.size()+")");
		log.info("- Costo ms deserializzazione jsonXML: "+Utilities.convertSystemTimeIntoString_millisecondi((dataFine-dataInizio), true));
		log.info("\n");
		log.info("\n");
		
		
		
	}
	
	
	private static void equalsOpenSPCoopObject(Object[] originale,Object[] ricostruito,File dir) throws Exception{
		for(int i=0; i<originale.length;i++){
			boolean find = false;
			for(int j=0; j<ricostruito.length;j++){
				try{
					if(originale[i] instanceof Soggetto){
						if(ricostruito[j] instanceof Soggetto){
							SerializationClientTest.equals((Soggetto)originale[i],(Soggetto)ricostruito[j],dir);
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
	private static void equals(Soggetto o1,Soggetto o2,File dir) throws Exception{
		if(o1.toString(false,true).equals(o2.toString(false,true))==false){
			File tmp1 = new File(dir,"originaleTestFailed.xml");
			File tmp2 = new File(dir,"ricostruitoTestFailed.xml");
			FileOutputStream fout1 = new FileOutputStream(tmp1);
			fout1.write(o1.toString(false,true).getBytes());
			fout1.flush();
			fout1.close();
			FileOutputStream fout2 = new FileOutputStream(tmp2);
			fout2.write(o2.toString(false,true).getBytes());
			fout2.flush();
			fout2.close();
			throw new Exception("Soggetti non uguali: vedi file originaleTestFailed.xml e ricostruitoTestFailed.xml per capire le differenze");
		}
	}
	private static void equals(AccordoServizioParteComune o1,AccordoServizioParteComune o2,File dir) throws Exception{
		if(o1.toString(false,true).equals(o2.toString(false,true))==false){
			File tmp1 = new File(dir,"originaleTestFailed.xml");
			File tmp2 = new File(dir,"ricostruitoTestFailed.xml");
			FileOutputStream fout1 = new FileOutputStream(tmp1);
			fout1.write(o1.toString(false,true).getBytes());
			fout1.flush();
			fout1.close();
			FileOutputStream fout2 = new FileOutputStream(tmp2);
			fout2.write(o2.toString(false,true).getBytes());
			fout2.flush();
			fout2.close();
			throw new Exception("Accordi non uguali: vedi file originaleTestFailed.xml e ricostruitoTestFailed.xml per capire le differenze");
		}
	}
}


