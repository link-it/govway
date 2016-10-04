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



package org.openspcoop2.protocol.spcoop.testsuite.core;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.testsuite.units.UnitsTestSuiteProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;

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
			return org.openspcoop2.protocol.spcoop.testsuite.core.Utilities.readApplicationServerVersion();
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
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soap").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soap':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	@Override
	public String getSoap12FileName(){
		// non usato in spcoop
		return null;
	}
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi Soap per LocalForward
	 *
	 */
	public String getLocalForwardFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.localForward").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.localForward':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getSoapSenzaBodyFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soapSenzaBody").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soapSenzaBody':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getSoapWithAttachmentsSenzaBodyFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soapWithAttachments.senzaBody").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soapWithAttachments.senzaBody':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getSoapSenzaHeaderFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soapHeader.nonPresente").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soapHeader.nonPresente':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getSoapWithAttachmentsSenzaHeaderFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soapWithAttachments.soapHeader.nonPresente").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soapWithAttachments.soapHeader.nonPresente':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getSoapHeaderEmptyFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soapHeader.empty").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soapHeader.empty':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getSoapWithAttachmentsHeaderEmptyFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soapWithAttachments.soapHeader.empty").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soapWithAttachments.soapHeader.empty':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getXmlSenzaSoapFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.xmlSenzaSoap").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.xmlSenzaSoap':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getXmlMultipartRelatedSenzaSoapFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.xmlMultipartRelatedSenzaSoap").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.xmlMultipartRelatedSenzaSoap':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	public String getSoapContentBased1FileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soap.contentBased1").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soap.contentBased1':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getSoapContentBased2FileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soap.contentBased2").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soap.contentBased2':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getSoapContentBased2_servizioNonIdentificabileFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soap.contentBased2_servizioNonIdentificabile").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soap.contentBased2_servizioNonIdentificabile':"+e.getMessage();
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
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soapWithAttachments").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soapWithAttachments':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	@Override
	public String getSoap12WithAttachmentsFileName(){
		// non usato in spcoop
		return null;
	}
	
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi Soap With Attachments (attachments sono XML)
	 *
	 */
	public String getSoapWithAttachments_attachAsXML_FileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soapWithAttachments.attachAsXML").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soapWithAttachments.attachAsXML':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi SoapWithAttachments con Mime
	 *
	 */
	public String getSoapWithAttachmentsAndMimeFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soapWithAttachmentsAndMime").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soapWithAttachmentsAndMime':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getSoapTestWSSecurityAnnidato(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.testWSSecurityAnnidato").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.testWSSecurityAnnidato':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getSoapTestWSSecuritySoapBox(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.testWSSecuritySoapBox").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.testWSSecuritySoapBox':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getSoapTestWSSecuritySoapBoxAllegatoXml(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.testWSSecuritySoapBox.allegatoXml").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.testWSSecuritySoapBox.allegatoXml':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getSoapTestWSSecuritySoapBoxAllegatoBinario(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.testWSSecuritySoapBox.allegatoBin").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.testWSSecuritySoapBox.allegatoBin':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	public String getSoapTestSOAPScorretto_erroreInCima(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.testSOAPScorretto.erroreInCima").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.testSOAPScorretto.erroreInCima':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getSoapTestSOAPScorretto_erroreInMezzo(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.testSOAPScorretto.erroreInMezzo").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.testSOAPScorretto.erroreInMezzo':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getSoapTestSOAPScorretto_erroreInFondo(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.testSOAPScorretto.erroreInFondo").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.testSOAPScorretto.erroreInFondo':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	public String getDOCFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.doc").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.doc':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getPDFFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.pdf").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.pdf':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getZIPFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.zip").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.zip':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi Soap con caratteri particolari per test XMLEncoding
	 *
	 */
	public String getXMLEncodingSoapFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.xmlencoding.soap").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.xmlencoding.soap':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna la location del file da utilizzare per la spedizione di messaggi Soap With Attachments con caratteri particolari per test XMLEncoding
	 *
	 */
	public String getXMLEncodingSoapWithAttachmentsFileName(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.xmlencoding.soapWithAttachments").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.xmlencoding.soapWithAttachments':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	
	public String getSoapWithHeaderMustUnderstandUnknonw(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.files.soapHeader.mustUnderstandUnknown").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.files.soapHeader.mustUnderstandUnknown':"+e.getMessage();
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
	 * Ritorna l'indicazione se sequenzializzare i test
	 *
	 */
	public boolean isNewConnectionForResponse(){
		try{
			return Boolean.parseBoolean(this.reader.getProperty("org.openspcoop2.testsuite.risposte.newconnection").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.risposte.newconnection':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return false;
		}
	}
	
	public IDSoggetto getIdentitaDefault(){
		return new IDSoggetto(getIdentitaDefault_tipo(), getIdentitaDefault_nome(), getIdentitaDefault_dominio());
	}
	public String getIdentitaDefault_dominio(){
		try{
			return this.reader.getProperty("org.openspcoop2.pdd.identificativoPorta.dominio").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.pdd.identificativoPorta.dominio':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getIdentitaDefault_tipo(){
		try{
			return this.reader.getProperty("org.openspcoop2.pdd.identificativoPorta.tipo").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.pdd.identificativoPorta.tipo':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getIdentitaDefault_nome(){
		try{
			return this.reader.getProperty("org.openspcoop2.pdd.identificativoPorta.nome").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.pdd.identificativoPorta.nome':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 *  
	 *
	 */
	public boolean isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta(){
		try{
			return Boolean.parseBoolean(this.reader.getProperty("org.openspcoop2.testsuite.strutturaHeaderNonCorretta.generazioneRispostaSPCoop").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.strutturaHeaderNonCorretta.generazioneRispostaSPCoop':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return false;
		}
	}
	
	
	/**
	 *  
	 *
	 */
	public boolean isGenerazioneRispostaSPCoopActorNonCorretto(){
		try{
			return Boolean.parseBoolean(this.reader.getProperty("org.openspcoop2.egov.strutturaHeaderNonCorretta.actorNonCorretto.generazioneRispostaSPCoop").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.egov.strutturaHeaderNonCorretta.actorNonCorretto.generazioneRispostaSPCoop':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return false;
		}
	}
	
	
	/**
	 *  
	 *
	 */
	public boolean isGenerazioneElementiXSDNonValidabili(){
		try{
			return Boolean.parseBoolean(this.reader.getProperty("org.openspcoop2.egov.generazioneElementiNonValidabiliRispettoXSD").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.egov.generazioneElementiNonValidabiliRispettoXSD':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return false;
		}
	}
	
	public String getKeywordTipoMittenteSconosciuto(){
		try{
			return this.reader.getProperty("org.openspcoop2.egov.mittenteSconosciuto.tipo").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.egov.mittenteSconosciuto.tipo':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getKeywordMittenteSconosciuto(){
		try{
			return this.reader.getProperty("org.openspcoop2.egov.mittenteSconosciuto.nome").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.egov.mittenteSconosciuto.nome':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public String getKeywordDominioSconosciuto(){
		try{
			return getKeywordMittenteSconosciuto()+"SPCoopIT";
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.egov.mittenteSconosciuto.nome':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	public int getHttpReturnCode_onewayHttpResponse(){
		try{
			return Integer.parseInt(this.reader.getProperty("org.openspcoop2.egov.oneway.httpEmptyResponse.returnCode").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.egov.oneway.httpEmptyResponse.returnCode':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
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
	
	
	
	
	
	/**
	 * Ritorna Socket utilizzato per la ricezione della risposta nel profilo Asincrono Simmetrico, modalita sincrona
	 *
	 */
	public int getSocketAsincronoSimmetrico_modalitaSincrona(){
		try{
			return Integer.parseInt(this.reader.getProperty("org.openspcoop2.testsuite.asincronoSimmetrico.modalitaSincrona.socket").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.asincronoSimmetrico.modalitaSincrona.socket':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	/**
	 * Ritorna Socket utilizzato per la ricezione della risposta nel profilo Asincrono Simmetrico, modalita asincrona
	 *
	 */
	public int getSocketAsincronoSimmetrico_modalitaAsincrona(){
		try{
			return Integer.parseInt(this.reader.getProperty("org.openspcoop2.testsuite.asincronoSimmetrico.modalitaAsincrona.socket").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.asincronoSimmetrico.modalitaAsincrona.socket':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	
	/**
	 * Ritorna Socket utilizzato per la ricezione della risposta nel profilo Asincrono Simmetrico, modalita sincrona, utilizzato per WSSecurity
	 *
	 */
	public int getSocketAsincronoSimmetrico_modalitaSincrona_WSSecurity(){
		try{
			return Integer.parseInt(this.reader.getProperty("org.openspcoop2.testsuite.asincronoSimmetrico.modalitaSincrona.wssecurity.socket").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.asincronoSimmetrico.modalitaSincrona.wssecurity.socket':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	
	/**
	 * Ritorna Socket utilizzato per la ricezione della risposta nel profilo Asincrono Simmetrico, modalita sincrona, per SOAPWithAttachments
	 *
	 */
	public int getSocketAsincronoSimmetrico_modalitaSincrona_SOAPWithAttachments(){
		try{
			return Integer.parseInt(this.reader.getProperty("org.openspcoop2.testsuite.asincronoSimmetrico.modalitaSincrona.soapWithAttachments.socket").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.asincronoSimmetrico.modalitaSincrona.soapWithAttachments.socket':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	/**
	 * Ritorna Socket utilizzato per la ricezione della risposta nel profilo Asincrono Simmetrico, modalita asincrona, per SOAPWithAttachments
	 *
	 */
	public int getSocketAsincronoSimmetrico_modalitaAsincrona_SOAPWithAttachments(){
		try{
			return Integer.parseInt(this.reader.getProperty("org.openspcoop2.testsuite.asincronoSimmetrico.modalitaAsincrona.soapWithAttachments.socket").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.asincronoSimmetrico.modalitaAsincrona.soapWithAttachments.socket':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	
	/**
	 * Ritorna Socket utilizzato per la ricezione della risposta nel profilo Asincrono Simmetrico, modalita sincrona (STATEFUL)
	 *
	 */
	public int getSocketAsincronoSimmetrico_modalitaSincrona_Stateful(){
		try{
			return Integer.parseInt(this.reader.getProperty("org.openspcoop2.testsuite.asincronoSimmetrico.modalitaSincrona.stateful.socket").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.asincronoSimmetrico.modalitaSincrona.stateful.socket':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	/**
	 * Ritorna Socket utilizzato per la ricezione della risposta nel profilo Asincrono Simmetrico, modalita asincrona (STATEFUL)
	 *
	 */
	public int getSocketAsincronoSimmetrico_modalitaAsincrona_Stateful(){
		try{
			return Integer.parseInt(this.reader.getProperty("org.openspcoop2.testsuite.asincronoSimmetrico.modalitaAsincrona.stateful.socket").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.asincronoSimmetrico.modalitaAsincrona.stateful.socket':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	
	/**
	 * Ritorna il  Tempo di attesa in millisecondi, tempo di attesa nel repository asincrono
	 *
	 */
	public long timeToSleep_repositoryAsincronoSimmetrico(){
		try{
			return Long.parseLong(this.reader.getProperty("org.openspcoop2.testsuite.repository.asincronoSimmetrico.timeToSleep").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.repository.asincronoSimmetrico.timeToSleep':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return -1;
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Ritorna il dir dei file delle buste errate
	 *
	 */
	public String getPathBusteErrate(){
		String nProperties = "org.openspcoop2.testsuite.path.busteErrate";
		try{
			return this.reader.getProperty(nProperties).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+nProperties+"':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	/**
	 * Ritorna il dir dei file delle buste Con Eccezioni
	 *
	 */
	public String getPathBusteConEccezioni(){
		String nProperties = "org.openspcoop2.testsuite.path.busteConEccezioni";
		try{
			return this.reader.getProperty(nProperties).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+nProperties+"':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	/**
	 * Ritorna il dir dei file delle buste Con campi duplicati
	 *
	 */
	public String getPathBusteConCampiDuplicati(){
		String nProperties = "org.openspcoop2.testsuite.path.busteConCampiDuplicati";
		try{
			return this.reader.getProperty(nProperties).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+nProperties+"':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	/**
	 * Ritorna il dir dei file delle buste con profilo linee guida 1.1
	 *
	 */
	public String getPathBusteLineeGuida11(){
		String nProperties = "org.openspcoop2.testsuite.path.busteLineeGuida1.1";
		try{
			return this.reader.getProperty(nProperties).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+nProperties+"':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il dir dei file delle buste per i test della validazione contenuti applicativi
	 *
	 */
	public String getPathTestValidazioneContenutiApplicativi(){
		String nProperties = "org.openspcoop2.testsuite.path.validazioneContenutiApplicativi";
		try{
			return this.reader.getProperty(nProperties).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+nProperties+"':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
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
	
	
	
	public java.util.Properties getJMS_JNDIContext() {
		java.util.Properties prop = new java.util.Properties();
		try{ 
			prop = Utilities.readProperties("org.openspcoop2.testsuite.jms-lookup.property.",this.reader);  
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.jms-lookup.property.*':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
		return prop;
	}
	public String getJMSQueue(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.jms.queue").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.jms.queue':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getJMSTopic(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.jms.topic").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.jms.topic':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getJMSConnectionFactory(){
		try{
			return this.reader.getProperty("org.openspcoop2.testsuite.jms.connectionFactory").trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.jms.connectionFactory':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getJMSUsername(){
		try{
			String v = this.reader.getProperty("org.openspcoop2.testsuite.jms.username");
			if(v==null){
				String msgErrore = "TestSuiteProperties, proprieta' 'org.openspcoop2.testsuite.jms.username' non definita";
				TestSuiteProperties.log.warn(msgErrore);
				return null;
			}
			return v.trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.jms.username':"+e.getMessage();
			TestSuiteProperties.log.error(msgErrore);
			return null;
		}
	}
	public String getJMSPassword(){
		try{
			String v = this.reader.getProperty("org.openspcoop2.testsuite.jms.password");
			if(v==null){
				String msgErrore = "TestSuiteProperties, proprieta' 'org.openspcoop2.testsuite.jms.password' non definita";
				TestSuiteProperties.log.warn(msgErrore);
				return null;
			}
			return v.trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.jms.password':"+e.getMessage();
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
