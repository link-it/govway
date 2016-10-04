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



package org.openspcoop2.testsuite.core;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;

/**
 * Reader delle proprieta' della TestSuite
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class TestSuiteProperties {

	
	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;
	
	/** Reader delle proprieta' impostate nel file 'testsuite.properties' */
	private TestSuiteInstanceProperties reader;

	/** Copia Statica */
	private static TestSuiteProperties testsuiteProperties = null;


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 */
	public TestSuiteProperties() throws Exception {

		this.log = LoggerWrapperFactory.getLogger(TestSuiteProperties.class);
		
		/* ---- Lettura del cammino del file di configurazione ---- */
		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		String confDir = null;
		try{  
			properties = TestSuiteProperties.class.getResourceAsStream("/"+CostantiTestSuite.TESTSUITE_PROPERTIES);
			if(properties==null){
				throw new Exception("File '"+"/"+CostantiTestSuite.TESTSUITE_PROPERTIES+"' not found");
			}
			propertiesReader.load(properties);
			
			confDir = propertiesReader.getProperty("confDirectory");
			if(confDir!=null){
				confDir = confDir.trim();
			}
		}catch(java.io.IOException e) {
			this.log.error("Riscontrato errore durante la lettura del file '"+CostantiTestSuite.TESTSUITE_PROPERTIES+"': \n\n"+e.getMessage());
			throw new Exception("ClassName initialize error: "+e.getMessage());
		}finally{
		    try{
				if(properties!=null)
				    properties.close();
		    }catch(Exception er){}
		}
		
		this.reader = new TestSuiteInstanceProperties(propertiesReader, this.log, confDir);

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
			LoggerWrapperFactory.getLogger(TestSuiteProperties.class).error("Errore durante l'inizializzazione del TestSuiteProperties: "+e.getMessage(),e);		   
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
	 * @throws UtilsException 
	 */
	public static TestSuiteProperties getInstance() {
		if(TestSuiteProperties.testsuiteProperties==null){
			initialize();
		}
		return TestSuiteProperties.testsuiteProperties;
	}
	
	public static void updateLocalImplementation(Properties prop){
		TestSuiteProperties.testsuiteProperties.reader.setLocalObjectImplementation(prop);
	}










	/* ********  M E T O D I  ******** */

	/**
	 * Ritorna l'indicazione se la servlet deve tracciare il flag 'isArrived' nella tabella tracciamento
	 *
	 */
	public boolean traceArrivedIntoDB(){
		try{
			return Boolean.parseBoolean(this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_IS_ARRIVED).trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_IS_ARRIVED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return false;
		}
	}
	
	public boolean loadMailcap(){
		try{
			return Boolean.parseBoolean(this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_MAILCAP).trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_MAILCAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return false;
		}
	}

	
	
	
	/* ********  HEADER RISPOSTA  ******** */
	
	public String getHeaderRispostaServletName(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_HEADER_RISPOSTA_SERVLET_NAME).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_HEADER_RISPOSTA_SERVLET_NAME+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	public Properties getHeaderRisposta(){
		try{
			return this.reader.readProperties_convertEnvProperties(CostantiTestSuite.PROPERTY_HEADER_RISPOSTA_GENERICO);
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura delle proprieta' '"+CostantiTestSuite.PROPERTY_HEADER_RISPOSTA_GENERICO+"*':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	
	
	/* ************* TRASPORTO ************ */
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente l'id della richiesta in corso
	 *
	 */
	public String getIdMessaggioTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_ID_MESSAGGIO_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_ID_MESSAGGIO_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente il riferimento asincrono
	 *
	 */
	public String getRiferimentoAsincronoTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_RIFERIMENTO_ASINCRONO_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_RIFERIMENTO_ASINCRONO_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente la collaborazione 
	 * 
	 */
	public String getCollaborazioneTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_COLLABORAZIONE_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_COLLABORAZIONE_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente il tipo mittente della richiesta in corso
	 *
	 */
	public String getTipoMittenteTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_TIPO_MITTENTE_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_TIPO_MITTENTE_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente il mittente della richiesta in corso
	 *
	 */
	public String getMittenteTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_MITTENTE_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_MITTENTE_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente il tipo destinatario della richiesta in corso
	 *
	 */
	public String getTipoDestinatarioTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_TIPO_DESTINATARIO_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_TIPO_DESTINATARIO_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente il destinatario della richiesta in corso
	 *
	 */
	public String getDestinatarioTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_DESTINATARIO_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_DESTINATARIO_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente il tipo servizio della richiesta in corso
	 *
	 */
	public String getTipoServizioTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_TIPO_SERVIZIO_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_TIPO_SERVIZIO_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente il servizio della richiesta in corso
	 *
	 */
	public String getServizioTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_SERVIZIO_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_SERVIZIO_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente il azione della richiesta in corso
	 *
	 */
	public String getAzioneTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_AZIONE_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_AZIONE_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}

	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente il servizio applicativo
	 *
	 */
	public String getServizioApplicativoTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_SERVIZIO_APPLICATIVO_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_SERVIZIO_APPLICATIVO_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nel trasporto contenente l'id applicativo
	 *
	 */
	public String getIDApplicativoTrasporto(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_ID_APPLICATIVO_TRASPORTO).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_ID_APPLICATIVO_TRASPORTO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	/* ************* URL BASED ************ */
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente l'id della richiesta in corso
	 *
	 */
	public String getIdUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_ID_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_ID_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente il riferimento asincrono
	 *
	 */
	public String getRiferimentoAsincronoUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_RIFERIMENTO_ASINCRONO_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_RIFERIMENTO_ASINCRONO_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente la collaborazione 
	 * 
	 */
	public String getCollaborazioneUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_COLLABORAZIONE_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_COLLABORAZIONE_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente il tipo mittente della richiesta in corso
	 *
	 */
	public String getTipoMittenteUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_TIPO_MITTENTE_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_TIPO_MITTENTE_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente il mittente della richiesta in corso
	 *
	 */
	public String getMittenteUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_MITTENTE_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_MITTENTE_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente il tipo destinatario della richiesta in corso
	 *
	 */
	public String getTipoDestinatarioUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_TIPO_DESTINATARIO_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_TIPO_DESTINATARIO_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente il destinatario della richiesta in corso
	 *
	 */
	public String getDestinatarioUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_DESTINATARIO_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_DESTINATARIO_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente il tipo servizio della richiesta in corso
	 *
	 */
	public String getTipoServizioUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_TIPO_SERVIZIO_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_TIPO_SERVIZIO_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente il servizio della richiesta in corso
	 *
	 */
	public String getServizioUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_SERVIZIO_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_SERVIZIO_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente il azione della richiesta in corso
	 *
	 */
	public String getAzioneUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_AZIONE_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_AZIONE_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}

	/**
	 * Ritorna il nome della proprieta' nella url contenente il servizio applicativo
	 *
	 */
	public String getServizioApplicativoUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_SERVIZIO_APPLICATIVO_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_SERVIZIO_APPLICATIVO_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nella url contenente l'id applicativo
	 *
	 */
	public String getIDApplicativoUrlBased(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_ID_APPLICATIVO_URL_BASED).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_ID_APPLICATIVO_URL_BASED+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	/* ************* SOAP ************ */
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente l'id della richiesta in corso
	 *
	 */
	public String getIdMessaggioSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_ID_MESSAGGIO_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_ID_MESSAGGIO_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente il riferimento asincrono
	 *
	 */
	public String getRiferimentoAsincronoSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_RIFERIMENTO_ASINCRONO_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_RIFERIMENTO_ASINCRONO_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente la collaborazione 
	 * 
	 */
	public String getCollaborazioneSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_COLLABORAZIONE_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_COLLABORAZIONE_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente il tipo mittente della richiesta in corso
	 *
	 */
	public String getTipoMittenteSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_TIPO_MITTENTE_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_TIPO_MITTENTE_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente il mittente della richiesta in corso
	 *
	 */
	public String getMittenteSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_MITTENTE_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_MITTENTE_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente il tipo destinatario della richiesta in corso
	 *
	 */
	public String getTipoDestinatarioSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_TIPO_DESTINATARIO_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_TIPO_DESTINATARIO_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente il destinatario della richiesta in corso
	 *
	 */
	public String getDestinatarioSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_DESTINATARIO_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_DESTINATARIO_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente il tipo servizio della richiesta in corso
	 *
	 */
	public String getTipoServizioSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_TIPO_SERVIZIO_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_TIPO_SERVIZIO_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente il servizio della richiesta in corso
	 *
	 */
	public String getServizioSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_SERVIZIO_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_SERVIZIO_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente il azione della richiesta in corso
	 *
	 */
	public String getAzioneSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_AZIONE_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_AZIONE_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}

	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente il servizio applicativo
	 *
	 */
	public String getServizioApplicativoSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_SERVIZIO_APPLICATIVO_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_SERVIZIO_APPLICATIVO_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	/**
	 * Ritorna il nome della proprieta' nell'header soap contenente l'id applicativo
	 *
	 */
	public String getIDApplicativoSoap(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_ID_APPLICATIVO_SOAP).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_ID_APPLICATIVO_SOAP+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	
	
	/**
	 * Ritorna il  Tempo di attesa in millisecondi, prima della generazione della risposta asincrona
	 *
	 */
	public long timeToSleep_generazioneRispostaAsincrona(){
		try{
			return Long.parseLong(this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_TIME_TO_SLEEP_SERVER_ASINCRONO).trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_TIME_TO_SLEEP_SERVER_ASINCRONO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return -1;
		}
	}
	
	/**
	 * Ritorna l'indicazione se attendere la terminazione dei messaggi, prima della generazione della risposta asincrona
	 *
	 */
	public boolean attendiTerminazioneMessaggi_generazioneRispostaAsincrona(){
		try{
			return Boolean.parseBoolean(this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_ATTESA_TERMINAZIONI_MESSAGGI_SERVER_ASINCRONO).trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_ATTESA_TERMINAZIONI_MESSAGGI_SERVER_ASINCRONO+"':"+e.getMessage();
			this.log.error(msgErrore);
			return false;
		}
	}
	
	/**
	 * Ritorna il timeout per il processamento dei messaggi
	 *
	 */
	public int getTimeoutProcessamentoMessaggiOpenSPCoop(){
		try{
			return Integer.parseInt(this.reader.getValue_convertEnvProperties("org.openspcoop2.testsuite.completamentoProcessamento.timeout").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.completamentoProcessamento.timeout':"+e.getMessage();
			this.log.error(msgErrore);
			return -1;
		}
	}
	
	/**
	 * Ritorna il timeout per il processamento dei messaggi
	 *
	 */
	public int getCheckIntervalProcessamentoMessaggiOpenSPCoop(){
		try{
			return Integer.parseInt(this.reader.getValue_convertEnvProperties("org.openspcoop2.testsuite.completamentoProcessamento.checkInterval").trim());
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' 'org.openspcoop2.testsuite.completamentoProcessamento.checkInterval':"+e.getMessage();
			this.log.error(msgErrore);
			return -1;
		}
	}
	
	
	/**
	 * Ritorna il Servizio di ricezione contenuti applicativi della porta di dominio erogatore
	 *
	 */
	public String getOpenSPCoopPDConsegnaRispostaAsincronaSimmetrica(String protocol){
		try{
			String url = this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROPERTY_OPENSPCOOP_PD_CONSEGNA_RISPOSTA_ASINCRONA_SIMMETRICA).trim();
			return url.replace("<protocol>", protocol);
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROPERTY_OPENSPCOOP_PD_CONSEGNA_RISPOSTA_ASINCRONA_SIMMETRICA+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	
	public String getTipoRepositoryBuste(){
		try{
			return this.reader.getValue_convertEnvProperties(CostantiTestSuite.TIPO_REPOSITORY_BUSTE).trim();
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.TIPO_REPOSITORY_BUSTE+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
	
	public String getProtocolloDefault(){
		try{
			String p = this.reader.getValue_convertEnvProperties(CostantiTestSuite.PROTOCOLLO_DEFAULT);
			if(p!=null)
				return p.trim();
			else
				return null;
		}catch(Exception e){
			String msgErrore = "TestSuiteProperties, errore durante la lettura della proprieta' '"+CostantiTestSuite.PROTOCOLLO_DEFAULT+"':"+e.getMessage();
			this.log.error(msgErrore);
			return null;
		}
	}
	
}
