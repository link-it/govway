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



package org.openspcoop2.protocol.trasparente.testsuite.core;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.testsuite.units.UnitsTestSuiteProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;

/**
 * Reader delle proprieta' della TestSuite
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TestSuiteProperties implements UnitsTestSuiteProperties {

	/** Logger utilizzato per errori eventuali. */
	private static Logger log = LoggerWrapperFactory.getLogger("TestSuiteProperties");



	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'testsuite.properties' */
	private Properties reader;

	/** Copia Statica */
	private static TestSuiteProperties testsuiteProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 */
	public TestSuiteProperties() throws Exception {

		/* ---- Lettura del cammino del file di configurazione ---- */
		this.reader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = TestSuiteProperties.class.getResourceAsStream("/testsuite_testunits.properties");
			this.reader.load(properties);
			properties.close();
		}catch(java.io.IOException e) {
			TestSuiteProperties.log.error("Riscontrato errore durante la lettura del file 'testsuite.properties': \n\n"+e.getMessage());
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
			throw new Exception("ClassName initialize error: "+e.getMessage());
		}

	}


	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 */
	public static boolean initialize(){

		try {
			TestSuiteProperties.testsuiteProperties = new TestSuiteProperties();	
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}

	/**
	 * Ritorna l'indicazione se questa classe e' inizializzata
	 *
	 * @return indicazione se questa classe e' inizializzata
	 */
	public static synchronized boolean isInitialized(){
		if(TestSuiteProperties.testsuiteProperties!=null)
			return true;
		else
			return false;
	}
	
	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di TestSuiteProperties
	 */
	public static TestSuiteProperties getInstance(){
		if(TestSuiteProperties.isInitialized()==false||TestSuiteProperties.testsuiteProperties==null)
			TestSuiteProperties.initialize();
		return TestSuiteProperties.testsuiteProperties;
	}










	/* ********  M E T O D I  ******** */

	@Override
	public String getApplicationServerVersion() {
		try{
			return org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.readApplicationServerVersion();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della versione dell'A.S.:"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	

	/**
	 * Ritorna Servizio di ricezione contenuti applicativi della Porta di Dominio
	 * della porta di dominio fruitore
	 *
	 */
	@Override
	public String getServizioRicezioneContenutiApplicativiFruitore(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.openspcoop.PD.fruitore").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.openspcoop.PD.fruitore':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	/**
	 * Ritorna Servizio di ricezione contenuti applicativi della Porta di Dominio
	 * della porta di dominio erogatore
	 *
	 */
	@Override
	public String getServizioRicezioneContenutiApplicativiErogatore(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.openspcoop.PD.erogatore").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.openspcoop.PD.erogatore':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}

	/**
	 * Ritorna Servizio di ricezione contenuti applicativi della Porta di Dominio frutore (https con autenticazione client)
	 *
	 */
	@Override
	public String getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.openspcoop.PD.fruitore.https").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.openspcoop.PD.fruitore.https':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna Servizio di ricezione buste della Porta di Dominio
	 * della porta di dominio fruitore
	 *
	 */
	@Override
	public String getServizioRicezioneBusteFruitore(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.openspcoop.PA.fruitore").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.openspcoop.PA.fruitore':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	/**
	 * Ritorna Servizio di ricezione buste della Porta di Dominio
	 * della porta di dominio erogatore
	 *
	 */
	@Override
	public String getServizioRicezioneBusteErogatore(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.openspcoop.PA.erogatore").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.openspcoop.PA.erogatore':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna Servizio di ricezione buste della Porta di Dominio erogatore (https con autenticazione client)
	 *
	 */
	@Override
	public String getServizioRicezioneBusteErogatore_httpsConAutenticazioneClient(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.openspcoop.PA.erogatore.https").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.openspcoop.PA.erogatore.https':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	@Override
	public int getConnectionTimeout(){
		try{
			return Integer.parseInt(this.reader.getProperty("connection.timeout").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'connection.timeout':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	
	@Override
	public int getReadConnectionTimeout(){
		try{
			return Integer.parseInt(this.reader.getProperty("readConnection.timeout").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi Soap
	 *
	 */
	@Override
	public String getSoap11FileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soap11").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soap11':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	@Override
	public String getSoap12FileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soap12").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soap12':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi Soap With Attachments
	 *
	 */
	@Override
	public String getSoap11WithAttachmentsFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soap11WithAttachments").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soap11WithAttachments':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	@Override
	public String getSoap12WithAttachmentsFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soap12WithAttachments").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soap12WithAttachments':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	/**
	 * Ritorna il Numero di Worker per Test
	 *
	 */
	@Override
	public int getWorkerNumber(){
		try{
			return Integer.parseInt(this.reader.getProperty("org.openspcoop2.testsuite.workerNumber").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.workerNumber':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	
	
	/**
	 * Ritorna la dimensione del Pool dei thread
	 *
	 */
	@Override
	public int getPoolSize(){
		try{
			return Integer.parseInt(this.reader.getProperty("org.openspcoop2.testsuite.poolSize").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.poolSize':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	

	
	/**
	 * Ritorna l'intervallo in millisecondi per la generazione della risposta asincrona
	 *
	 */
	public long getIntervalloGenerazioneRispostaAsincrona(){
		try{
			return Long.parseLong(this.reader.getProperty("org.openspcoop2.testsuite.generazioneRispostaAsincrona").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.generazioneRispostaAsincrona':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	

	

	
	
	/**
	 * Ritorna l'indicazione se sequenzializzare i test
	 *
	 */
	@Override
	public boolean sequentialTests(){
		try{
			return Boolean.parseBoolean(this.reader.getProperty("org.openspcoop2.testsuite.sequential").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.sequential':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return false;
		}
	}
	
	
	
	
	
	/**
	 * Ritorna il  Tempo di attesa in millisecondi, prima della verifica sul database
	 *
	 */
	@Override
	public long timeToSleep_verificaDatabase(){
		try{
			return Long.parseLong(this.reader.getProperty("org.openspcoop2.testsuite.repository.timeToSleep").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.repository.timeToSleep':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	
	/**
	 * Ritorna true se devono essere verificati la terminazione dei msg sul database, prima della verifica sul database
	 *
	 */
	@Override
	public boolean attendiTerminazioneMessaggi_verificaDatabase(){
		try{
			return Boolean.parseBoolean(this.reader.getProperty("org.openspcoop2.testsuite.repository.attesaTerminazioneMessaggi").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.repository.attesaTerminazioneMessaggi':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return false;
		}
	}
	
	
	
	
	
	
	@Override
	public String getLogDirectoryOpenSPCoop(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.openspcoop.log").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.openspcoop.log':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	
	@Override
	public String getJMXFactory(){
		try{
			return this.reader.getProperty("jmx.factory").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'jmx.factory':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	@Override
	public String getJMXServer(){
		try{
			return this.reader.getProperty("jmx.server").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'jmx.server':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	@Override
	public String getJMXServiceURL(){
		try{
			return this.reader.getProperty("jmx.service.url").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'jmx.service.url':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	@Override
	public String getJMXServiceURL(String applicationServer){
		try{
			String url = this.reader.getProperty("jmx.service.url."+applicationServer);
			if(url!=null){
				return url.trim();
			}
			else{
				return this.getJMXServiceURL();
			}
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'jmx.service.url."+applicationServer+"':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	@Override
	public String getJMXUsername(){
		try{
			return this.reader.getProperty("jmx.username").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'jmx.username':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	@Override
	public String getJMXPassword(){
		try{
			return this.reader.getProperty("jmx.password").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'jmx.password':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	@Override
	public boolean isSoapEngineAxis14(){
		try{
			return "axis14".equals(this.reader.getProperty("org.openspcoop2.testsuite.soapEngine").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.soapEngine':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return false;
		}
	}
	@Override
	public boolean isSoapEngineCxf(){
		try{
			return "cxf".equals(this.reader.getProperty("org.openspcoop2.testsuite.soapEngine").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.soapEngine':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return false;
		}
	}

	
	@Override
	public boolean isUseTransazioni(){
		try{
			return "true".equals(this.reader.getProperty("org.openspcoop2.testsuite.useTransazioni").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.useTransazioni':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return false;
		}
	}
}
