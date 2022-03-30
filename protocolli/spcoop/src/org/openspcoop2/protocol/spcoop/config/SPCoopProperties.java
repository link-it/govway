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

package org.openspcoop2.protocol.spcoop.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;

/**
 * Classe che gestisce il file di properties 'spcoop.properties' del protocollo SPCoop
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SPCoopProperties {

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static SPCoopProperties spcoopProperties = null;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'spcoop.properties' */
	private SPCoopInstanceProperties reader;


	


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public SPCoopProperties(String confDir,Logger log) throws ProtocolException{

		if(log != null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger("SPCoopProperties");

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = SPCoopProperties.class.getResourceAsStream("/spcoop.properties");
			if(properties==null){
				throw new Exception("File '/spcoop.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'spcoop.properties': "+e.getMessage());
			throw new ProtocolException("SPCoopProperties initialize error: "+e.getMessage(),e);
		}finally{
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
		}
		try{
			this.reader = new SPCoopInstanceProperties(confDir, propertiesReader, this.log);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}

	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static synchronized void initialize(String confDir,Logger log) throws ProtocolException{

		if(SPCoopProperties.spcoopProperties==null)
			SPCoopProperties.spcoopProperties = new SPCoopProperties(confDir,log);	

	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di OpenSPCoopProperties
	 * @throws Exception 
	 * 
	 */
	public static SPCoopProperties getInstance(Logger log) throws ProtocolException{

		if(SPCoopProperties.spcoopProperties==null)
			throw new ProtocolException("SPCoopProperties not initialized (use init method in factory)");

		return SPCoopProperties.spcoopProperties;
	}




	public void validaConfigurazione(Loader loader) throws ProtocolException  {	
		try{  

			/* **** LIBRERIA **** */

			// Identificativo
			String tipo = this.getTipoSeriale_IdentificativoBusta();
			if( CostantiProtocollo.IDENTIFICATIVO_SERIALE_DB.equals(tipo) == false && 
					CostantiProtocollo.IDENTIFICATIVO_SERIALE_MYSQL.equals(tipo) == false &&
					CostantiProtocollo.IDENTIFICATIVO_SERIALE_STATIC.equals(tipo) == false &&
					CostantiProtocollo.IDENTIFICATIVO_SERIALE_DYNAMIC.equals(tipo) == false ){
				String msg = "Riscontrato errore durante la lettura della proprieta': 'org.openspcoop2.protocol.spcoop.id.tipo': tipo non gestito";
				this.log.error(msg);
				throw new ProtocolException(msg);
			}
			if(CostantiProtocollo.IDENTIFICATIVO_SERIALE_STATIC.equals(tipo)){
				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
				Integer prefix = (op2Properties!=null) ? 
						(op2Properties.getClusterIdNumerico()!=null ? Integer.valueOf(op2Properties.getClusterIdNumerico()) : null) 
						: null;
				if(prefix!=null) {
					if(prefix<0 || prefix>99){
						String msg = "La generazione dell'identificativo eGov richiede un identificativo del cluster compreso tra 0 e 99";
						this.log.error(msg);
						throw new ProtocolException(msg);
					}
				}
			}
			else if(CostantiProtocollo.IDENTIFICATIVO_SERIALE_DYNAMIC.equals(tipo)){
				OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
				if(op2Properties!=null && !op2Properties.isClusterDinamico()) {
					String msg = "La generazione dell'identificativo eGov richiede un identificativo del cluster dinamico";
					this.log.error(msg);
					throw new ProtocolException(msg);
				}
			}
			this.isHttpEmptyResponseOneWay();
			this.getHttpReturnCodeEmptyResponseOneWay();
			this.isHttpOneWay_PD_HTTPEmptyResponse();
			this.isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo();
			this.isGenerazioneBustaErrore_actorScorretto();
			this.getIntervalloScadenzaBuste();
			this.isRepositoryBusteFiltraBusteScaduteRispettoOraRegistrazione();
			this.getIntervalloMinutiTolleranzaDateFuture();
			this.getKeywordTipoMittenteSconosciuto();
			this.getKeywordMittenteSconosciuto();
			this.getSchemaXSDValidazioneXSDBusta();
			this.getSchemiXSDImportatiValidazioneXSDBusta();
			this.isGenerazioneElementiNonValidabiliRispettoXSD();
			this.isIgnoraEccezioniNonGravi();
			this.isGenerazioneListaEccezioniErroreProcessamento();
			
			
			/* **** MANIFEST **** */
			this.getRoleRichiestaManifest();
			this.getRoleRispostaManifest();
			this.getRoleAllegatoManifest();
			
			
			/* **** SOAP FAULT **** */
			
			this.isGenerazioneDetailsSOAPFaultProtocolValidazione();
			this.isGenerazioneDetailsSOAPFaultProtocolProcessamento();
			this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace();
			this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche();
			this.isGenerazioneDetailsSOAPFaultIntegrationServerError();
			this.isGenerazioneDetailsSOAPFaultIntegrationClientError();
			this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace();
			this.isGenerazioneDetailsSOAPFaultIntegrazionConInformazioniGeneriche();
			this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
			this.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
			
			
			/* **** PACKAGE CNIPA **** */
			
			this.isGestionePackageSICA();

			
			/* **** Static instance config **** */
			
			this.useConfigStaticInstance();
			this.useErroreApplicativoStaticInstance();
			this.useEsitoStaticInstance();
			this.getStaticInstanceConfig();
			
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la validazione della proprieta' del protocollo SPCoop, "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		}
	}




	/* **** LIBRERIA **** */

	/**
	 * Restituisce il tipo di gestione dell'identificatore intrapresa dalla porta di dominio
	 * 
	 * @return Restituisce il tipo di gestione dell'identificatore intrapresa dalla porta di dominio 
	 */
	private static String tipoSeriale_IdentificativoBusta = null;
	public String getTipoSeriale_IdentificativoBusta() throws ProtocolException {
		if(SPCoopProperties.tipoSeriale_IdentificativoBusta == null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.id.tipo");
				if(name==null)
					throw new Exception("proprieta non definita");
				SPCoopProperties.tipoSeriale_IdentificativoBusta  = (name !=null) ? name.trim() : null;
			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.id.tipo': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			} 	   
		}

		return SPCoopProperties.tipoSeriale_IdentificativoBusta;
	}

	/**
	 * Il carico http di risposta per un profilo oneway (e per asincroni in modalita asincrona) non dovrebbe contenere alcun messaggio applicativo,
	 * come viene descritto dalla specifica SPCoop.
	 * Alcuni framework SOAP, invece, tendono a ritornare come messaggi di risposta a invocazioni di operation che non prevedono un output:
	 * - SoapEnvelope con SoapBody empty (es. <soapenv:Body />)
	 * - SoapEnvelope contenente msg applicativi con root element vuoto (es. <soapenv:Body><operationResponse/></soapenv:Body>)
	 * - ....
	 * La seguente opzione permette di forzare un carico http vuoto, nei casi sopra descritti,
	 * per la risposta generata dalla PdD in seguito alla gestione dei profili oneway (e asincroni in modalita asincrona)
	 *   
	 * @return Restituisce l'indicazione su come impostare il Carico http di risposta per un profilo oneway
	 * 
	 */
	private static Boolean isHttpEmptyResponseOneWay = null;
	public boolean isHttpEmptyResponseOneWay(){
		if(SPCoopProperties.isHttpEmptyResponseOneWay==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.isHttpEmptyResponseOneWay = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse' non impostata, viene utilizzato il default=true");
					SPCoopProperties.isHttpEmptyResponseOneWay = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				SPCoopProperties.isHttpEmptyResponseOneWay = true;
			}
		}

		return SPCoopProperties.isHttpEmptyResponseOneWay;
	}

	/**
	 * Il return code di una risposta http, come descritto nella specifica http://www.ws-i.org/profiles/basicprofile-1.1.html (3.4.4),
	 * puo' assumere entrambi i valori 200 o 202, in caso il carico http di risposta non contiene una soap envelope.
	 * La seguente opzione permette di impostare il return code generato dalla PdD in seguito alla gestione dei profili oneway (e asincroni in modalita asincrona)
	 *   
	 * @return Restituisce l'indicazione su come impostare il return code http di risposta per un profilo oneway
	 * 
	 */
	private static Integer getHttpReturnCodeEmptyResponseOneWay = null;
	public Integer getHttpReturnCodeEmptyResponseOneWay(){
		if(SPCoopProperties.getHttpReturnCodeEmptyResponseOneWay==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.returnCode"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.getHttpReturnCodeEmptyResponseOneWay = Integer.parseInt(value);
					if(SPCoopProperties.getHttpReturnCodeEmptyResponseOneWay!=200 && SPCoopProperties.getHttpReturnCodeEmptyResponseOneWay!=202){
						throw new Exception("Unici valori ammessi sono 200 o 202, trovato ["+SPCoopProperties.getHttpReturnCodeEmptyResponseOneWay+"]");
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.returnCode' non impostata, viene utilizzato il default=200");
					SPCoopProperties.getHttpReturnCodeEmptyResponseOneWay = 200;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.returnCode' non impostata, viene utilizzato il default=200, errore: "+e.getMessage());
				SPCoopProperties.getHttpReturnCodeEmptyResponseOneWay = 200;
			}
		}

		return SPCoopProperties.getHttpReturnCodeEmptyResponseOneWay;
	}

	/**
	 * Restituisce l'indicazione su come impostare il Carico http di risposta per un profilo oneway puo' essere:
	 * - vuoto con codice http 202 (true)
	 * - msg soap vuoto con codice http 200 (false)
	 *   
	 * @return Restituisce l'indicazione su come impostare il Carico http di risposta per un profilo oneway
	 * 
	 */
	private static Boolean isHttpOneWay_PD_HTTPEmptyResponse = null;
	public boolean isHttpOneWay_PD_HTTPEmptyResponse(){
		if(SPCoopProperties.isHttpOneWay_PD_HTTPEmptyResponse==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.bodyEmptyWithHeader.enable"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.isHttpOneWay_PD_HTTPEmptyResponse = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.bodyEmptyWithHeader.enable' non impostata, viene utilizzato il default=true");
					SPCoopProperties.isHttpOneWay_PD_HTTPEmptyResponse = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.bodyEmptyWithHeader.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				SPCoopProperties.isHttpOneWay_PD_HTTPEmptyResponse = true;
			}
		}

		return SPCoopProperties.isHttpOneWay_PD_HTTPEmptyResponse;
	}
	
	private static Boolean isResponseMessageWithTransportCodeError_blockedTransaction = null;
	public boolean isResponseMessageWithTransportCodeError_blockedTransaction(){
		if(SPCoopProperties.isResponseMessageWithTransportCodeError_blockedTransaction==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.responseMessageWithTransportCodeError.blockedTransaction"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.isResponseMessageWithTransportCodeError_blockedTransaction = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.responseMessageWithTransportCodeError.blockedTransaction' non impostata, viene utilizzato il default=true");
					SPCoopProperties.isResponseMessageWithTransportCodeError_blockedTransaction = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.responseMessageWithTransportCodeError.blockedTransaction' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				SPCoopProperties.isResponseMessageWithTransportCodeError_blockedTransaction = true;
			}
		}

		return SPCoopProperties.isResponseMessageWithTransportCodeError_blockedTransaction;
	}



	/**
	 * Indicazione se ritornare solo SoapFault o busteErrore in caso di buste con struttura malformata.
	 *   
	 * @return Indicazione se ritornare solo SoapFault o busteErrore in caso di buste con struttura malformata.
	 * 
	 */
	private static Boolean isGenerazioneBustaErrore_strutturaMalformataHeaderBusta = null;
	public boolean isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo(){
		if(SPCoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderBusta==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.generazioneBustaRisposta"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderBusta = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.generazioneBustaRisposta' non impostata, viene utilizzato il default=false");
					SPCoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderBusta = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.generazioneBustaRisposta' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderBusta = false;
			}
		}

		return SPCoopProperties.isGenerazioneBustaErrore_strutturaMalformataHeaderBusta;
	}

	/**
	 * Indicazione se ritornare solo SoapFault o buste in caso di buste con actor scorretto.
	 *   
	 * @return Indicazione se ritornare solo SoapFault o buste in caso di buste con actor scorretto.
	 * 
	 */
	private static Boolean isGenerazioneBustaErrore_actorScorretto = null;
	public boolean isGenerazioneBustaErrore_actorScorretto(){
		if(SPCoopProperties.isGenerazioneBustaErrore_actorScorretto==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.actorNonCorretto.generazioneBustaRisposta"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneBustaErrore_actorScorretto = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.actorNonCorretto.generazioneBustaRisposta' non impostata, viene utilizzato il default=true");
					SPCoopProperties.isGenerazioneBustaErrore_actorScorretto = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.actorNonCorretto.generazioneBustaRisposta' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneBustaErrore_actorScorretto = true;
			}
		}

		return SPCoopProperties.isGenerazioneBustaErrore_actorScorretto;
	}

	/**
	 * Restituisce l'intervallo di scadenza delle buste
	 *
	 * @return Restituisce l'intervallo di scadenza delle buste
	 * 
	 */
	private static Long intervalloScadenzaBuste = null;
	public long getIntervalloScadenzaBuste() throws ProtocolException {	
		if(SPCoopProperties.intervalloScadenzaBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.scadenzaMessaggio");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				SPCoopProperties.intervalloScadenzaBuste = java.lang.Long.parseLong(name);

				if(SPCoopProperties.intervalloScadenzaBuste<=0){
					if(SPCoopProperties.intervalloScadenzaBuste!=-1){
						throw new Exception("Valore non valido ["+SPCoopProperties.intervalloScadenzaBuste+"].");			
					}
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.scadenzaMessaggio': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}   
		}

		return SPCoopProperties.intervalloScadenzaBuste;
	}
	
	private static Integer dateFutureIntervalloTolleranza = null;
	public int getIntervalloMinutiTolleranzaDateFuture() {	
		if(SPCoopProperties.dateFutureIntervalloTolleranza==null){
			try{ 
				String name = null;
				name = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.date.intervalloTolleranza");
				if(name!=null){
					name = name.trim();
					SPCoopProperties.dateFutureIntervalloTolleranza = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.date.intervalloTolleranza' non impostata, viene utilizzato il default=-1 (accettate qualsiasi date future)");
					SPCoopProperties.dateFutureIntervalloTolleranza = -1;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.date.intervalloTolleranza' non impostata, viene utilizzato il default=-1 (accettate qualsiasi date future): "+e.getMessage());
				SPCoopProperties.dateFutureIntervalloTolleranza = -1;
			}  
		}

		return SPCoopProperties.dateFutureIntervalloTolleranza;
	}
	public boolean isCheckTolleranzaDateFutureAttivo(){
		return getIntervalloMinutiTolleranzaDateFuture() > 0;
	}
	

	/**
	 * Restituisce Indicazione se filtrare le buste scadute rispetto all'ora di registrazione
	 *
	 * @return Indicazione se filtrare le buste scadute rispetto all'ora di registrazione
	 * 
	 */
	private static Boolean repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = null;
	public boolean isRepositoryBusteFiltraBusteScaduteRispettoOraRegistrazione() {	
		if(SPCoopProperties.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione==null){
			try{ 
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.scadenzaMessaggio.filtraBusteScaduteRispettoOraRegistrazione");
				if(value==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.scadenzaMessaggio.filtraBusteScaduteRispettoOraRegistrazione' non definita (Viene utilizzato il default:true)");
					SPCoopProperties.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = true;
				}else{
					SPCoopProperties.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = Boolean.parseBoolean(value);
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.scadenzaMessaggio.filtraBusteScaduteRispettoOraRegistrazione' (Viene utilizzato il default:true): "+e.getMessage());
				SPCoopProperties.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = true;
			}    
		}

		return SPCoopProperties.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione;
	}

	/**
	 * Keyword utilizzata per identificare il tipo mittente di una busta dove il tipo mittente non e' indicato
	 *   
	 * @return Keyword utilizzata per identificare il tipo mittente di una busta dove il tipo mittente non e' indicato
	 * 
	 */
	private static String getKeywordTipoMittenteSconosciuto = null;
	public String getKeywordTipoMittenteSconosciuto(){
		if(SPCoopProperties.getKeywordTipoMittenteSconosciuto==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.mittenteSconosciuto.tipo"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.getKeywordTipoMittenteSconosciuto = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.mittenteSconosciuto.tipo' non impostata, viene utilizzato il default=Sconosciuto");
					SPCoopProperties.getKeywordTipoMittenteSconosciuto = "Sconosciuto";
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.mittenteSconosciuto.tipo' non impostata, viene utilizzato il default=Sconosciuto, errore:"+e.getMessage());
				SPCoopProperties.getKeywordTipoMittenteSconosciuto = "Sconosciuto";
			}
		}

		return SPCoopProperties.getKeywordTipoMittenteSconosciuto;
	}

	/**
	 * Keyword utilizzata per identificare il mittente di una busta dove il tipo mittente non e' indicato
	 *   
	 * @return Keyword utilizzata per identificare il mittente di una busta dove il tipo mittente non e' indicato
	 * 
	 */
	private static String getKeywordMittenteSconosciuto = null;
	public String getKeywordMittenteSconosciuto(){
		if(SPCoopProperties.getKeywordMittenteSconosciuto==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.mittenteSconosciuto.nome"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.getKeywordMittenteSconosciuto = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.mittenteSconosciuto.nome' non impostata, viene utilizzato il default=Sconosciuto");
					SPCoopProperties.getKeywordMittenteSconosciuto = "Sconosciuto";
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.mittenteSconosciuto.nome' non impostata, viene utilizzato il default=Sconosciuto, errore:"+e.getMessage());
				SPCoopProperties.getKeywordMittenteSconosciuto = "Sconosciuto";
			}
		}

		return SPCoopProperties.getKeywordMittenteSconosciuto;
	}


	private static String getSchemaXSDValidazioneXSDBusta = null;
	public String getSchemaXSDValidazioneXSDBusta(){
		if(SPCoopProperties.getSchemaXSDValidazioneXSDBusta==null){
			String defaultSchema = "Busta.xsd";
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.validazione_xsd.schema"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.getSchemaXSDValidazioneXSDBusta = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.validazione_xsd.schema' non impostata, viene utilizzato il default=["+defaultSchema+"]");
					SPCoopProperties.getSchemaXSDValidazioneXSDBusta = defaultSchema;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.validazione_xsd.schema' non impostata, viene utilizzato il default=["+defaultSchema+"], errore:"+e.getMessage());
				SPCoopProperties.getSchemaXSDValidazioneXSDBusta = defaultSchema;
			}
		}

		return SPCoopProperties.getSchemaXSDValidazioneXSDBusta;
	}


	private static String [] schemiImportatiValidazioneXSDBusta = null;
	public String[] getSchemiXSDImportatiValidazioneXSDBusta(){

		String defaults = "soapEnvelope.xsd,wssecurityUtility.xsd";
		String [] arrayDefault = {"soapEnvelope.xsd","wssecurityUtility.xsd"};

		if(SPCoopProperties.schemiImportatiValidazioneXSDBusta==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.validazione_xsd.schemiImportati"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.schemiImportatiValidazioneXSDBusta = value.split(",");
					if(SPCoopProperties.schemiImportatiValidazioneXSDBusta==null || SPCoopProperties.schemiImportatiValidazioneXSDBusta.length<=0){
						throw new Exception("schemi non definiti");
					}
					else{
						for(int i=0;i<SPCoopProperties.schemiImportatiValidazioneXSDBusta.length;i++){
							SPCoopProperties.schemiImportatiValidazioneXSDBusta[i] = SPCoopProperties.schemiImportatiValidazioneXSDBusta[i].trim();
						}
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.validazione_xsd.schemiImportati' non impostata, viene utilizzato il default="+defaults);
					SPCoopProperties.schemiImportatiValidazioneXSDBusta = arrayDefault;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.validazione_xsd.schemiImportati' non impostata, viene utilizzato il default="+defaults+", errore:"+e.getMessage());
				SPCoopProperties.schemiImportatiValidazioneXSDBusta = arrayDefault;
			}
		}

		return SPCoopProperties.schemiImportatiValidazioneXSDBusta;
	}

	/**
	 * Indicazione se devono essere generati in risposte errore, elementi non validabili rispetto xsd.
	 *   
	 * @return Indicazione se devono essere generati in risposte errore, elementi non validabili rispetto xsd.
	 * 
	 */
	private static Boolean isGenerazioneElementiNonValidabiliRispettoXSD = null;
	public boolean isGenerazioneElementiNonValidabiliRispettoXSD(){
		if(SPCoopProperties.isGenerazioneElementiNonValidabiliRispettoXSD==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneElementiNonValidabiliRispettoXSD"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneElementiNonValidabiliRispettoXSD = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneElementiNonValidabiliRispettoXSD' non impostata, viene utilizzato il default=false");
					SPCoopProperties.isGenerazioneElementiNonValidabiliRispettoXSD = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneElementiNonValidabiliRispettoXSD' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneElementiNonValidabiliRispettoXSD = false;
			}
		}

		return SPCoopProperties.isGenerazioneElementiNonValidabiliRispettoXSD;
	}
	
	/**
	 * Indicazione se ritenere errore eccezioni di livello non gravi
	 *   
	 * @return Indicazione se ritenere errore eccezioni di livello non gravi
	 * 
	 */
	private static Boolean isBustaErrore_EccezioniNonGravi = null;
	public boolean isIgnoraEccezioniNonGravi(){
		if(SPCoopProperties.isBustaErrore_EccezioniNonGravi==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.bustaErrore.ignoraEccezioniNonGravi.enable"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.isBustaErrore_EccezioniNonGravi = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.bustaErrore.ignoraEccezioniNonGravi.enable' non impostata, viene utilizzato il default=false");
					SPCoopProperties.isBustaErrore_EccezioniNonGravi = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.bustaErrore.ignoraEccezioniNonGravi.enable' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SPCoopProperties.isBustaErrore_EccezioniNonGravi = false;
			}
		}

		return SPCoopProperties.isBustaErrore_EccezioniNonGravi;
	}

	 /**
     * Indicazione se generare MessaggipErrore Processamento senza ListaEccezione
     *   
     * @return Indicazione se generare MessaggiErrore Processamento senza ListaEccezione
     * 
     */
	private static Boolean isGenerazioneListaEccezioniErroreProcessamento = null;
    public boolean isGenerazioneListaEccezioniErroreProcessamento(){
    	if(SPCoopProperties.isGenerazioneListaEccezioniErroreProcessamento==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.eccezioneProcessamento.generazioneListaEccezioni"); 
				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneListaEccezioniErroreProcessamento = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.eccezioneProcessamento.generazioneListaEccezioni' non impostata, viene utilizzato il default=false");
					SPCoopProperties.isGenerazioneListaEccezioniErroreProcessamento = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.eccezioneProcessamento.generazioneListaEccezioni' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneListaEccezioniErroreProcessamento = false;
			}
    	}
    	
    	return SPCoopProperties.isGenerazioneListaEccezioniErroreProcessamento;
	}
	

    
	
    
    
    /* **** MANIFEST **** */
        
    /**
     * Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest
     * Valore utilizzato per identificare una richiesta
     *   
     * @return Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest, valore utilizzato per identificare una richiesta
     * 
     */
	private static String roleRichiestaManifest = null;
	public String getRoleRichiestaManifest(){

		if(SPCoopProperties.roleRichiestaManifest==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.manifestAttachments.role.richiesta"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.roleRichiestaManifest = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.richiesta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_RICHIESTA);
					SPCoopProperties.roleRichiestaManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_RICHIESTA;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.richiesta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_RICHIESTA+", errore:"+e.getMessage());
				SPCoopProperties.roleRichiestaManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_RICHIESTA;
			}
		}

		return SPCoopProperties.roleRichiestaManifest;
	}

	/**
	 * Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest
	 * Valore utilizzato per identificare una risposta
	 *   
	 * @return Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest, valore utilizzato per identificare una risposta
	 * 
	 */
	private static String roleRispostaManifest = null;
	public String getRoleRispostaManifest(){

		if(SPCoopProperties.roleRispostaManifest==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.manifestAttachments.role.risposta"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.roleRispostaManifest = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.risposta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_RISPOSTA);
					SPCoopProperties.roleRispostaManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_RISPOSTA;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.risposta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_RISPOSTA+", errore:"+e.getMessage());
				SPCoopProperties.roleRispostaManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_RISPOSTA;
			}
		}

		return SPCoopProperties.roleRispostaManifest;
	}

	/**
	 * Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest
	 * Valore utilizzato per identificare un allegato
	 *   
	 * @return Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest, valore utilizzato per identificare un allegato
	 * 
	 */
	private static String roleAllegatoManifest = null;
	public String getRoleAllegatoManifest(){

		if(SPCoopProperties.roleAllegatoManifest==null){
			try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.manifestAttachments.role.allegato"); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.roleAllegatoManifest = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.richiesta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_ALLEGATO);
					SPCoopProperties.roleAllegatoManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_ALLEGATO;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.richiesta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_ALLEGATO+", errore:"+e.getMessage());
				SPCoopProperties.roleAllegatoManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_ALLEGATO;
			}
		}

		return SPCoopProperties.roleAllegatoManifest;
	}

    /**
     * Indicazione se i riferimenti presenti all'interno del Manifest 'eGov_IT:Riferimento' nell'attributo 'href'
     * devono contenere i caratteri '<' e '>' dei Content-ID che riferiscono gli attachments 
     *   
     * @return Indicazione se generare i caratteri '<' e '>' dei Content-ID che riferiscono gli attachments 
     * 
     */
	private static Boolean generateManifestAttachmentsIdWithBrackets = null;
    public boolean isGenerateManifestAttachmentsIdWithBrackets(){
    	if(SPCoopProperties.generateManifestAttachmentsIdWithBrackets==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.manifestAttachments.id.brackets"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.generateManifestAttachmentsIdWithBrackets = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.id.brackets' non impostata, viene utilizzato il default=false");
					SPCoopProperties.generateManifestAttachmentsIdWithBrackets = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.id.brackets' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SPCoopProperties.generateManifestAttachmentsIdWithBrackets = false;
			}
    	}
    	
    	return SPCoopProperties.generateManifestAttachmentsIdWithBrackets;
	}
    
    
    
    
    
	
	
	
	/* **** SOAP FAULT (Protocollo, Porta Applicativa) **** */
	
    /**
     * Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolValidazione = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolValidazione(){
    	if(SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneIntestazione"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false");
					SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
			}
    	}
    	
    	return SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione;
	}
    
    /**
     * Indicazione se generare i details in caso di SOAPFault *_300
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_300
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento(){
    	if(SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneProcessamento"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true");
					SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
			}
    	}
    	
    	return SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento;
	}
    
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace(){
    	if(SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false");
					SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
			}
    	}
    	
    	return SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace;
	}
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche(){
    	if(SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true");
					SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
			}
    	}
    	
    	return SPCoopProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche;
	}
    
    
    
    /* **** SOAP FAULT (Integrazione, Porta Delegata) **** */
    
    /**
     * Indicazione se generare i details in Casi di errore 5XX
     *   
     * @return Indicazione se generare i details in Casi di errore 5XX
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationServerError = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationServerError(){
    	if(SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.serverError"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true");
					SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
			}
    	}
    	
    	return SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError;
	}
    
    /**
     * Indicazione se generare i details in Casi di errore 4XX
     *   
     * @return Indicazione se generare i details in Casi di errore 4XX
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationClientError = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationClientError(){
    	if(SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.clientError"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false");
					SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
			}
    	}
    	
    	return SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError;
	}
    
    /**
     * Indicazione se generare nei details lo stack trace all'interno
     *   
     * @return Indicazione se generare nei details lo stack trace all'interno
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace(){
    	if(SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false");
					SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
			}
    	}
    	
    	return SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace;
	}
    
    /**
     * Indicazione se generare nei details informazioni dettagliate o solo di carattere generale
     *   
     * @return Indicazione se generare nei details informazioni dettagliate o solo di carattere generale
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche= null;
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead= null;
    public Boolean isGenerazioneDetailsSOAPFaultIntegrazionConInformazioniGeneriche(){
    	if(SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode)");
					SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				}
				
				SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode), errore:"+e.getMessage());
				SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				
				SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
			}
    	}
    	
    	return SPCoopProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche;
	}
    
    
    /* **** SOAP FAULT (Generati dagli attori esterni) **** */
    
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     * 
     */
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo= null;
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead= null;
    public Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo(){
    	if(SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				}
				
				SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				
				SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
			}
    	}
    	
    	return SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo;
	}
    
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     * 
     */
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD= null;
	private static Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdDRead= null;
    public Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD(){
    	if(SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				}
				
				SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				
				SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return SPCoopProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}
    
    
    
    
    /* **** Package CNIPA **** */
    
    private static Boolean isGestionePackageSICA= null;
    public Boolean isGestionePackageSICA(){
    	if(SPCoopProperties.isGestionePackageSICA==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.spcoop.packageSICA"); 
				
				if (value != null){
					value = value.trim();
					SPCoopProperties.isGestionePackageSICA = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.packageSICA' non impostata, viene utilizzato il default=false");
					SPCoopProperties.isGestionePackageSICA = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.packageSICA' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SPCoopProperties.isGestionePackageSICA = false;
			}
    	}
    	
    	return SPCoopProperties.isGestionePackageSICA;
	}
    
    
    
	/* **** Static instance config **** */
	
	private static Boolean useConfigStaticInstance = null;
	private Boolean useConfigStaticInstance(){
		if(SPCoopProperties.useConfigStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.spcoop.factory.config.staticInstance";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.useConfigStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					SPCoopProperties.useConfigStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				SPCoopProperties.useConfigStaticInstance = defaultValue;
			}
		}

		return SPCoopProperties.useConfigStaticInstance;
	}
	
	private static Boolean useErroreApplicativoStaticInstance = null;
	private Boolean useErroreApplicativoStaticInstance(){
		if(SPCoopProperties.useErroreApplicativoStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.spcoop.factory.erroreApplicativo.staticInstance";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.useErroreApplicativoStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					SPCoopProperties.useErroreApplicativoStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				SPCoopProperties.useErroreApplicativoStaticInstance = defaultValue;
			}
		}

		return SPCoopProperties.useErroreApplicativoStaticInstance;
	}
	
	private static Boolean useEsitoStaticInstance = null;
	private Boolean useEsitoStaticInstance(){
		if(SPCoopProperties.useEsitoStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.spcoop.factory.esito.staticInstance";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SPCoopProperties.useEsitoStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					SPCoopProperties.useEsitoStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				SPCoopProperties.useEsitoStaticInstance = defaultValue;
			}
		}

		return SPCoopProperties.useEsitoStaticInstance;
	}
	
	private static BasicStaticInstanceConfig staticInstanceConfig = null;
	public BasicStaticInstanceConfig getStaticInstanceConfig(){
		if(SPCoopProperties.staticInstanceConfig==null){
			staticInstanceConfig = new BasicStaticInstanceConfig();
			if(useConfigStaticInstance()!=null) {
				staticInstanceConfig.setStaticConfig(useConfigStaticInstance());
			}
			if(useErroreApplicativoStaticInstance()!=null) {
				staticInstanceConfig.setStaticErrorBuilder(useErroreApplicativoStaticInstance());
			}
			if(useEsitoStaticInstance()!=null) {
				staticInstanceConfig.setStaticEsitoBuilder(useEsitoStaticInstance());
			}
		}
		return staticInstanceConfig;
	} 
}
