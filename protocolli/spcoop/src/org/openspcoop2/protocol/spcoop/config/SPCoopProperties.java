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

package org.openspcoop2.protocol.spcoop.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.utils.BooleanNullable;
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
			}catch(Throwable er){
				// close
			}
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
			
			
			/* **** Prefisso 'SOAP_ENV' **** */
			
			this.isAddPrefixSOAPENV();

			
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
	private String tipoSeriale_IdentificativoBusta = null;
	public String getTipoSeriale_IdentificativoBusta() throws ProtocolException {
		if(this.tipoSeriale_IdentificativoBusta == null){
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.id.tipo");
				if(name==null)
					throw new Exception("proprieta non definita");
				this.tipoSeriale_IdentificativoBusta  = name.trim();
			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.id.tipo': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			} 	   
		}

		return this.tipoSeriale_IdentificativoBusta;
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
	private Boolean isHttpEmptyResponseOneWay = null;
	public boolean isHttpEmptyResponseOneWay(){
		if(this.isHttpEmptyResponseOneWay==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse"); 

				if (value != null){
					value = value.trim();
					this.isHttpEmptyResponseOneWay = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse' non impostata, viene utilizzato il default=true");
					this.isHttpEmptyResponseOneWay = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isHttpEmptyResponseOneWay = true;
			}
		}

		return this.isHttpEmptyResponseOneWay;
	}

	/**
	 * Il return code di una risposta http, come descritto nella specifica http://www.ws-i.org/profiles/basicprofile-1.1.html (3.4.4),
	 * puo' assumere entrambi i valori 200 o 202, in caso il carico http di risposta non contiene una soap envelope.
	 * La seguente opzione permette di impostare il return code generato dalla PdD in seguito alla gestione dei profili oneway (e asincroni in modalita asincrona)
	 *   
	 * @return Restituisce l'indicazione su come impostare il return code http di risposta per un profilo oneway
	 * 
	 */
	private Integer getHttpReturnCodeEmptyResponseOneWay = null;
	public Integer getHttpReturnCodeEmptyResponseOneWay(){
		if(this.getHttpReturnCodeEmptyResponseOneWay==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.returnCode"); 

				if (value != null){
					value = value.trim();
					this.getHttpReturnCodeEmptyResponseOneWay = Integer.parseInt(value);
					if(this.getHttpReturnCodeEmptyResponseOneWay!=200 && this.getHttpReturnCodeEmptyResponseOneWay!=202){
						throw new Exception("Unici valori ammessi sono 200 o 202, trovato ["+this.getHttpReturnCodeEmptyResponseOneWay+"]");
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.returnCode' non impostata, viene utilizzato il default=200");
					this.getHttpReturnCodeEmptyResponseOneWay = 200;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.returnCode' non impostata, viene utilizzato il default=200, errore: "+e.getMessage());
				this.getHttpReturnCodeEmptyResponseOneWay = 200;
			}
		}

		return this.getHttpReturnCodeEmptyResponseOneWay;
	}

	/**
	 * Restituisce l'indicazione su come impostare il Carico http di risposta per un profilo oneway puo' essere:
	 * - vuoto con codice http 202 (true)
	 * - msg soap vuoto con codice http 200 (false)
	 *   
	 * @return Restituisce l'indicazione su come impostare il Carico http di risposta per un profilo oneway
	 * 
	 */
	private Boolean isHttpOneWay_PD_HTTPEmptyResponse = null;
	public boolean isHttpOneWay_PD_HTTPEmptyResponse(){
		if(this.isHttpOneWay_PD_HTTPEmptyResponse==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.bodyEmptyWithHeader.enable"); 

				if (value != null){
					value = value.trim();
					this.isHttpOneWay_PD_HTTPEmptyResponse = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.bodyEmptyWithHeader.enable' non impostata, viene utilizzato il default=true");
					this.isHttpOneWay_PD_HTTPEmptyResponse = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.oneway.httpEmptyResponse.bodyEmptyWithHeader.enable' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isHttpOneWay_PD_HTTPEmptyResponse = true;
			}
		}

		return this.isHttpOneWay_PD_HTTPEmptyResponse;
	}
	
	private Boolean isResponseMessageWithTransportCodeError_blockedTransaction = null;
	public boolean isResponseMessageWithTransportCodeError_blockedTransaction(){
		if(this.isResponseMessageWithTransportCodeError_blockedTransaction==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.responseMessageWithTransportCodeError.blockedTransaction"); 

				if (value != null){
					value = value.trim();
					this.isResponseMessageWithTransportCodeError_blockedTransaction = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.responseMessageWithTransportCodeError.blockedTransaction' non impostata, viene utilizzato il default=true");
					this.isResponseMessageWithTransportCodeError_blockedTransaction = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.responseMessageWithTransportCodeError.blockedTransaction' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isResponseMessageWithTransportCodeError_blockedTransaction = true;
			}
		}

		return this.isResponseMessageWithTransportCodeError_blockedTransaction;
	}



	/**
	 * Indicazione se ritornare solo SoapFault o busteErrore in caso di buste con struttura malformata.
	 *   
	 * @return Indicazione se ritornare solo SoapFault o busteErrore in caso di buste con struttura malformata.
	 * 
	 */
	private Boolean isGenerazioneBustaErrore_strutturaMalformataHeaderBusta = null;
	public boolean isGenerazioneBustaErrore_strutturaMalformataHeaderProtocollo(){
		if(this.isGenerazioneBustaErrore_strutturaMalformataHeaderBusta==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.generazioneBustaRisposta"); 

				if (value != null){
					value = value.trim();
					this.isGenerazioneBustaErrore_strutturaMalformataHeaderBusta = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.generazioneBustaRisposta' non impostata, viene utilizzato il default=false");
					this.isGenerazioneBustaErrore_strutturaMalformataHeaderBusta = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.generazioneBustaRisposta' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneBustaErrore_strutturaMalformataHeaderBusta = false;
			}
		}

		return this.isGenerazioneBustaErrore_strutturaMalformataHeaderBusta;
	}

	/**
	 * Indicazione se ritornare solo SoapFault o buste in caso di buste con actor scorretto.
	 *   
	 * @return Indicazione se ritornare solo SoapFault o buste in caso di buste con actor scorretto.
	 * 
	 */
	private Boolean isGenerazioneBustaErrore_actorScorretto = null;
	public boolean isGenerazioneBustaErrore_actorScorretto(){
		if(this.isGenerazioneBustaErrore_actorScorretto==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.actorNonCorretto.generazioneBustaRisposta"); 

				if (value != null){
					value = value.trim();
					this.isGenerazioneBustaErrore_actorScorretto = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.actorNonCorretto.generazioneBustaRisposta' non impostata, viene utilizzato il default=true");
					this.isGenerazioneBustaErrore_actorScorretto = true;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.strutturaHeaderNonCorretta.actorNonCorretto.generazioneBustaRisposta' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isGenerazioneBustaErrore_actorScorretto = true;
			}
		}

		return this.isGenerazioneBustaErrore_actorScorretto;
	}

	/**
	 * Restituisce l'intervallo di scadenza delle buste
	 *
	 * @return Restituisce l'intervallo di scadenza delle buste
	 * 
	 */
	private Long intervalloScadenzaBuste = null;
	public long getIntervalloScadenzaBuste() throws ProtocolException {	
		if(this.intervalloScadenzaBuste==null){
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.scadenzaMessaggio");
				if(name==null)
					throw new Exception("non definita");
				name = name.trim();
				this.intervalloScadenzaBuste = java.lang.Long.parseLong(name);

				if(this.intervalloScadenzaBuste<=0){
					if(this.intervalloScadenzaBuste!=-1){
						throw new Exception("Valore non valido ["+this.intervalloScadenzaBuste+"].");			
					}
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.scadenzaMessaggio': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}   
		}

		return this.intervalloScadenzaBuste;
	}
	
	private Integer dateFutureIntervalloTolleranza = null;
	public int getIntervalloMinutiTolleranzaDateFuture() {	
		if(this.dateFutureIntervalloTolleranza==null){
			try{ 
				String name = null;
				name = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.date.intervalloTolleranza");
				if(name!=null){
					name = name.trim();
					this.dateFutureIntervalloTolleranza = java.lang.Integer.parseInt(name);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.date.intervalloTolleranza' non impostata, viene utilizzato il default=-1 (accettate qualsiasi date future)");
					this.dateFutureIntervalloTolleranza = -1;
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.date.intervalloTolleranza' non impostata, viene utilizzato il default=-1 (accettate qualsiasi date future): "+e.getMessage());
				this.dateFutureIntervalloTolleranza = -1;
			}  
		}

		return this.dateFutureIntervalloTolleranza;
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
	private Boolean repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = null;
	public boolean isRepositoryBusteFiltraBusteScaduteRispettoOraRegistrazione() {	
		if(this.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione==null){
			try{ 
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.scadenzaMessaggio.filtraBusteScaduteRispettoOraRegistrazione");
				if(value==null){
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.scadenzaMessaggio.filtraBusteScaduteRispettoOraRegistrazione' non definita (Viene utilizzato il default:true)");
					this.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = true;
				}else{
					this.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = Boolean.parseBoolean(value);
				}
			}catch(java.lang.Exception e) {
				this.log.warn("Riscontrato errore durante la lettura della proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.scadenzaMessaggio.filtraBusteScaduteRispettoOraRegistrazione' (Viene utilizzato il default:true): "+e.getMessage());
				this.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione = true;
			}    
		}

		return this.repositoryBusteFiltraBusteScaduteRispettoOraRegistrazione;
	}

	/**
	 * Keyword utilizzata per identificare il tipo mittente di una busta dove il tipo mittente non e' indicato
	 *   
	 * @return Keyword utilizzata per identificare il tipo mittente di una busta dove il tipo mittente non e' indicato
	 * 
	 */
	private String getKeywordTipoMittenteSconosciuto = null;
	public String getKeywordTipoMittenteSconosciuto(){
		if(this.getKeywordTipoMittenteSconosciuto==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.mittenteSconosciuto.tipo"); 

				if (value != null){
					value = value.trim();
					this.getKeywordTipoMittenteSconosciuto = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.mittenteSconosciuto.tipo' non impostata, viene utilizzato il default=Sconosciuto");
					this.getKeywordTipoMittenteSconosciuto = "Sconosciuto";
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.mittenteSconosciuto.tipo' non impostata, viene utilizzato il default=Sconosciuto, errore:"+e.getMessage());
				this.getKeywordTipoMittenteSconosciuto = "Sconosciuto";
			}
		}

		return this.getKeywordTipoMittenteSconosciuto;
	}

	/**
	 * Keyword utilizzata per identificare il mittente di una busta dove il tipo mittente non e' indicato
	 *   
	 * @return Keyword utilizzata per identificare il mittente di una busta dove il tipo mittente non e' indicato
	 * 
	 */
	private String getKeywordMittenteSconosciuto = null;
	public String getKeywordMittenteSconosciuto(){
		if(this.getKeywordMittenteSconosciuto==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.mittenteSconosciuto.nome"); 

				if (value != null){
					value = value.trim();
					this.getKeywordMittenteSconosciuto = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.mittenteSconosciuto.nome' non impostata, viene utilizzato il default=Sconosciuto");
					this.getKeywordMittenteSconosciuto = "Sconosciuto";
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.mittenteSconosciuto.nome' non impostata, viene utilizzato il default=Sconosciuto, errore:"+e.getMessage());
				this.getKeywordMittenteSconosciuto = "Sconosciuto";
			}
		}

		return this.getKeywordMittenteSconosciuto;
	}


	private String getSchemaXSDValidazioneXSDBusta = null;
	public String getSchemaXSDValidazioneXSDBusta(){
		if(this.getSchemaXSDValidazioneXSDBusta==null){
			String defaultSchema = "Busta.xsd";
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.validazione_xsd.schema"); 

				if (value != null){
					value = value.trim();
					this.getSchemaXSDValidazioneXSDBusta = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.validazione_xsd.schema' non impostata, viene utilizzato il default=["+defaultSchema+"]");
					this.getSchemaXSDValidazioneXSDBusta = defaultSchema;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.validazione_xsd.schema' non impostata, viene utilizzato il default=["+defaultSchema+"], errore:"+e.getMessage());
				this.getSchemaXSDValidazioneXSDBusta = defaultSchema;
			}
		}

		return this.getSchemaXSDValidazioneXSDBusta;
	}


	private String [] schemiImportatiValidazioneXSDBusta = null;
	public String[] getSchemiXSDImportatiValidazioneXSDBusta(){

		String defaults = "soapEnvelope.xsd,wssecurityUtility.xsd";
		String [] arrayDefault = {"soapEnvelope.xsd","wssecurityUtility.xsd"};

		if(this.schemiImportatiValidazioneXSDBusta==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.validazione_xsd.schemiImportati"); 

				if (value != null){
					value = value.trim();
					this.schemiImportatiValidazioneXSDBusta = value.split(",");
					if(this.schemiImportatiValidazioneXSDBusta==null || this.schemiImportatiValidazioneXSDBusta.length<=0){
						throw new Exception("schemi non definiti");
					}
					else{
						for(int i=0;i<this.schemiImportatiValidazioneXSDBusta.length;i++){
							this.schemiImportatiValidazioneXSDBusta[i] = this.schemiImportatiValidazioneXSDBusta[i].trim();
						}
					}
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.validazione_xsd.schemiImportati' non impostata, viene utilizzato il default="+defaults);
					this.schemiImportatiValidazioneXSDBusta = arrayDefault;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.validazione_xsd.schemiImportati' non impostata, viene utilizzato il default="+defaults+", errore:"+e.getMessage());
				this.schemiImportatiValidazioneXSDBusta = arrayDefault;
			}
		}

		return this.schemiImportatiValidazioneXSDBusta;
	}

	/**
	 * Indicazione se devono essere generati in risposte errore, elementi non validabili rispetto xsd.
	 *   
	 * @return Indicazione se devono essere generati in risposte errore, elementi non validabili rispetto xsd.
	 * 
	 */
	private Boolean isGenerazioneElementiNonValidabiliRispettoXSD = null;
	public boolean isGenerazioneElementiNonValidabiliRispettoXSD(){
		if(this.isGenerazioneElementiNonValidabiliRispettoXSD==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneElementiNonValidabiliRispettoXSD"); 

				if (value != null){
					value = value.trim();
					this.isGenerazioneElementiNonValidabiliRispettoXSD = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneElementiNonValidabiliRispettoXSD' non impostata, viene utilizzato il default=false");
					this.isGenerazioneElementiNonValidabiliRispettoXSD = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneElementiNonValidabiliRispettoXSD' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneElementiNonValidabiliRispettoXSD = false;
			}
		}

		return this.isGenerazioneElementiNonValidabiliRispettoXSD;
	}
	
	/**
	 * Indicazione se ritenere errore eccezioni di livello non gravi
	 *   
	 * @return Indicazione se ritenere errore eccezioni di livello non gravi
	 * 
	 */
	private Boolean isBustaErrore_EccezioniNonGravi = null;
	public boolean isIgnoraEccezioniNonGravi(){
		if(this.isBustaErrore_EccezioniNonGravi==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.bustaErrore.ignoraEccezioniNonGravi.enable"); 

				if (value != null){
					value = value.trim();
					this.isBustaErrore_EccezioniNonGravi = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.bustaErrore.ignoraEccezioniNonGravi.enable' non impostata, viene utilizzato il default=false");
					this.isBustaErrore_EccezioniNonGravi = false;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.bustaErrore.ignoraEccezioniNonGravi.enable' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isBustaErrore_EccezioniNonGravi = false;
			}
		}

		return this.isBustaErrore_EccezioniNonGravi;
	}

	 /**
     * Indicazione se generare MessaggipErrore Processamento senza ListaEccezione
     *   
     * @return Indicazione se generare MessaggiErrore Processamento senza ListaEccezione
     * 
     */
	private Boolean isGenerazioneListaEccezioniErroreProcessamento = null;
    public boolean isGenerazioneListaEccezioniErroreProcessamento(){
    	if(this.isGenerazioneListaEccezioniErroreProcessamento==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.eccezioneProcessamento.generazioneListaEccezioni"); 
				if (value != null){
					value = value.trim();
					this.isGenerazioneListaEccezioniErroreProcessamento = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.eccezioneProcessamento.generazioneListaEccezioni' non impostata, viene utilizzato il default=false");
					this.isGenerazioneListaEccezioniErroreProcessamento = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.eccezioneProcessamento.generazioneListaEccezioni' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneListaEccezioniErroreProcessamento = false;
			}
    	}
    	
    	return this.isGenerazioneListaEccezioniErroreProcessamento;
	}
	

    
	
    
    
    /* **** MANIFEST **** */
        
    /**
     * Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest
     * Valore utilizzato per identificare una richiesta
     *   
     * @return Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest, valore utilizzato per identificare una richiesta
     * 
     */
	private String roleRichiestaManifest = null;
	public String getRoleRichiestaManifest(){

		if(this.roleRichiestaManifest==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.manifestAttachments.role.richiesta"); 

				if (value != null){
					value = value.trim();
					this.roleRichiestaManifest = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.richiesta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_RICHIESTA);
					this.roleRichiestaManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_RICHIESTA;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.richiesta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_RICHIESTA+", errore:"+e.getMessage());
				this.roleRichiestaManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_RICHIESTA;
			}
		}

		return this.roleRichiestaManifest;
	}

	/**
	 * Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest
	 * Valore utilizzato per identificare una risposta
	 *   
	 * @return Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest, valore utilizzato per identificare una risposta
	 * 
	 */
	private String roleRispostaManifest = null;
	public String getRoleRispostaManifest(){

		if(this.roleRispostaManifest==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.manifestAttachments.role.risposta"); 

				if (value != null){
					value = value.trim();
					this.roleRispostaManifest = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.risposta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_RISPOSTA);
					this.roleRispostaManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_RISPOSTA;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.risposta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_RISPOSTA+", errore:"+e.getMessage());
				this.roleRispostaManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_RISPOSTA;
			}
		}

		return this.roleRispostaManifest;
	}

	/**
	 * Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest
	 * Valore utilizzato per identificare un allegato
	 *   
	 * @return Restituisce Attributo 'role' di un elemento 'Riferimento' di un descrittore presente in un manifest, valore utilizzato per identificare un allegato
	 * 
	 */
	private String roleAllegatoManifest = null;
	public String getRoleAllegatoManifest(){

		if(this.roleAllegatoManifest==null){
			try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.manifestAttachments.role.allegato"); 

				if (value != null){
					value = value.trim();
					this.roleAllegatoManifest = value;
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.richiesta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_ALLEGATO);
					this.roleAllegatoManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_ALLEGATO;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.role.richiesta' non impostata, viene utilizzato il default="+SPCoopCostanti.ATTACHMENTS_MANIFEST_ALLEGATO+", errore:"+e.getMessage());
				this.roleAllegatoManifest = SPCoopCostanti.ATTACHMENTS_MANIFEST_ALLEGATO;
			}
		}

		return this.roleAllegatoManifest;
	}

    /**
     * Indicazione se i riferimenti presenti all'interno del Manifest 'eGov_IT:Riferimento' nell'attributo 'href'
     * devono contenere i caratteri '<' e '>' dei Content-ID che riferiscono gli attachments 
     *   
     * @return Indicazione se generare i caratteri '<' e '>' dei Content-ID che riferiscono gli attachments 
     * 
     */
	private Boolean generateManifestAttachmentsIdWithBrackets = null;
    public boolean isGenerateManifestAttachmentsIdWithBrackets(){
    	if(this.generateManifestAttachmentsIdWithBrackets==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.manifestAttachments.id.brackets"); 
				
				if (value != null){
					value = value.trim();
					this.generateManifestAttachmentsIdWithBrackets = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.id.brackets' non impostata, viene utilizzato il default=false");
					this.generateManifestAttachmentsIdWithBrackets = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.manifestAttachments.id.brackets' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.generateManifestAttachmentsIdWithBrackets = false;
			}
    	}
    	
    	return this.generateManifestAttachmentsIdWithBrackets;
	}
    
    
    
    
    
	
	
	
	/* **** SOAP FAULT (Protocollo, Porta Applicativa) **** */
	
    /**
     * Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultProtocolValidazione = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolValidazione(){
    	if(this.isGenerazioneDetailsSOAPFaultProtocolValidazione==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneIntestazione"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolValidazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultProtocolValidazione;
	}
    
    /**
     * Indicazione se generare i details in caso di SOAPFault *_300
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_300
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento(){
    	if(this.isGenerazioneDetailsSOAPFaultProtocolProcessamento==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneProcessamento"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultProtocolProcessamento;
	}
    
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace(){
    	if(this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace;
	}
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche(){
    	if(this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche;
	}
    
    
    
    /* **** SOAP FAULT (Integrazione, Porta Delegata) **** */
    
    /**
     * Indicazione se generare i details in Casi di errore 5XX
     *   
     * @return Indicazione se generare i details in Casi di errore 5XX
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultIntegrationServerError = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationServerError(){
    	if(this.isGenerazioneDetailsSOAPFaultIntegrationServerError==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.serverError"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationServerError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultIntegrationServerError;
	}
    
    /**
     * Indicazione se generare i details in Casi di errore 4XX
     *   
     * @return Indicazione se generare i details in Casi di errore 4XX
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultIntegrationClientError = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationClientError(){
    	if(this.isGenerazioneDetailsSOAPFaultIntegrationClientError==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.clientError"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationClientError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultIntegrationClientError;
	}
    
    /**
     * Indicazione se generare nei details lo stack trace all'interno
     *   
     * @return Indicazione se generare nei details lo stack trace all'interno
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace(){
    	if(this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace;
	}
    
    /**
     * Indicazione se generare nei details informazioni dettagliate o solo di carattere generale
     *   
     * @return Indicazione se generare nei details informazioni dettagliate o solo di carattere generale
     * 
     */
	private Boolean isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche= null;
	private Boolean isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead= null;
    public Boolean isGenerazioneDetailsSOAPFaultIntegrazionConInformazioniGeneriche(){
    	if(this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode)");
					this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				}
				
				this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode), errore:"+e.getMessage());
				this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				
				this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
			}
    	}
    	
    	return this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche;
	}
    
    
    /* **** SOAP FAULT (Generati dagli attori esterni) **** */
    
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
     * 
     */
	private BooleanNullable isAggiungiDetailErroreApplicativo_SoapFaultApplicativo= null;
	private Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead= null;
    public BooleanNullable isAggiungiDetailErroreApplicativo_SoapFaultApplicativo(){
    	if(this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = b ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = BooleanNullable.NULL();
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
			}
    	}
    	
    	return this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo;
	}
    
    /**
     * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     *   
     * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
     * 
     */
	private BooleanNullable isAggiungiDetailErroreApplicativo_SoapFaultPdD= null;
	private Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdDRead= null;
    public BooleanNullable isAggiungiDetailErroreApplicativo_SoapFaultPdD(){
    	if(this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = b ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = BooleanNullable.NULL();
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return this.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}
    
    
    
    
    /* **** Package CNIPA **** */
    
    private Boolean isGestionePackageSICA= null;
    public Boolean isGestionePackageSICA(){
    	if(this.isGestionePackageSICA==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.spcoop.packageSICA"); 
				
				if (value != null){
					value = value.trim();
					this.isGestionePackageSICA = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.packageSICA' non impostata, viene utilizzato il default=false");
					this.isGestionePackageSICA = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.spcoop.packageSICA' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isGestionePackageSICA = false;
			}
    	}
    	
    	return this.isGestionePackageSICA;
	}
    
    
    
    /* **** Prefisso 'SOAP_ENV' **** */
    
    private Boolean isAddPrefixSOAPENV= null;
    public Boolean isAddPrefixSOAPENV(){
    	if(this.isAddPrefixSOAPENV==null){
	    	String pName = "org.openspcoop2.protocol.spcoop.addPrefixSOAP_ENV";
    		try{  
				String value = this.reader.getValueConvertEnvProperties(pName); 
				
				if (value != null){
					value = value.trim();
					this.isAddPrefixSOAPENV = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false");
					this.isAddPrefixSOAPENV = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+pName+"' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				this.isAddPrefixSOAPENV = false;
			}
    	}
    	
    	return this.isAddPrefixSOAPENV;
	}
    
    
    
	/* **** Static instance config **** */
	
	private Boolean useConfigStaticInstance = null;
	private Boolean useConfigStaticInstance(){
		if(this.useConfigStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.spcoop.factory.config.staticInstance";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.useConfigStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.useConfigStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.useConfigStaticInstance = defaultValue;
			}
		}

		return this.useConfigStaticInstance;
	}
	
	private Boolean useErroreApplicativoStaticInstance = null;
	private Boolean useErroreApplicativoStaticInstance(){
		if(this.useErroreApplicativoStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.spcoop.factory.erroreApplicativo.staticInstance";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.useErroreApplicativoStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.useErroreApplicativoStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.useErroreApplicativoStaticInstance = defaultValue;
			}
		}

		return this.useErroreApplicativoStaticInstance;
	}
	
	private Boolean useEsitoStaticInstance = null;
	private Boolean useEsitoStaticInstance(){
		if(this.useEsitoStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.spcoop.factory.esito.staticInstance";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.useEsitoStaticInstance = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.useEsitoStaticInstance = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.useEsitoStaticInstance = defaultValue;
			}
		}

		return this.useEsitoStaticInstance;
	}
	
	private BasicStaticInstanceConfig staticInstanceConfig = null;
	public BasicStaticInstanceConfig getStaticInstanceConfig(){
		if(this.staticInstanceConfig==null){
			this.staticInstanceConfig = new BasicStaticInstanceConfig();
			if(useConfigStaticInstance()!=null) {
				this.staticInstanceConfig.setStaticConfig(useConfigStaticInstance());
			}
			if(useErroreApplicativoStaticInstance()!=null) {
				this.staticInstanceConfig.setStaticErrorBuilder(useErroreApplicativoStaticInstance());
			}
			if(useEsitoStaticInstance()!=null) {
				this.staticInstanceConfig.setStaticEsitoBuilder(useEsitoStaticInstance());
			}
		}
		return this.staticInstanceConfig;
	} 
}
