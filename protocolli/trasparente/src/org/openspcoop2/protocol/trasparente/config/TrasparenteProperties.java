/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.trasparente.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.trasparente.config.TrasparenteProperties;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;

/**
 * Classe che gestisce il file di properties 'trasparente.properties' del protocollo Trasparente
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasparenteProperties {

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static TrasparenteProperties trasparenteProperties = null;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'trasparente.properties' */
	private TrasparenteInstanceProperties reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public TrasparenteProperties(String confDir,Logger log) throws ProtocolException{

		if(log != null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger("TrasparenteProperties");

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = TrasparenteProperties.class.getResourceAsStream("/trasparente.properties");
			if(properties==null){
				throw new Exception("File '/trasparente.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'trasparente.properties': "+e.getMessage());
			throw new ProtocolException("TrasparenteProperties initialize error: "+e.getMessage(),e);
		}finally{
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){}
		}
		try{
			this.reader = new TrasparenteInstanceProperties(propertiesReader, this.log);
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

		if(TrasparenteProperties.trasparenteProperties==null)
			TrasparenteProperties.trasparenteProperties = new TrasparenteProperties(confDir,log);	

	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di OpenTrasparenteProperties
	 * @throws Exception 
	 * 
	 */
	public static TrasparenteProperties getInstance(Logger log) throws ProtocolException{

		if(TrasparenteProperties.trasparenteProperties==null)
			throw new ProtocolException("TrasparenteProperties not initialized (use init method in factory)");

		return TrasparenteProperties.trasparenteProperties;
	}




	public void validaConfigurazione(Loader loader) throws ProtocolException  {	
		try{  

			generateIDasUUID();
			
			this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
			this.isAggiungiDetailErroreApplicativo_SoapFaultPdD();
			this.isGenerazioneDetailsSOAPFaultProtocolValidazione();
			this.isGenerazioneDetailsSOAPFaultProtocolProcessamento();
			this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace();
			this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche();
			
			this.isGenerazioneDetailsSOAPFaultIntegrationServerError();
			this.isGenerazioneDetailsSOAPFaultIntegrationClientError();
			this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace();
			this.isGenerazioneDetailsSOAPFaultIntegrazionConInformazioniGeneriche();
			
			this.isPortaApplicativaBustaErrore_personalizzaElementiFault();
			this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo();

		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la validazione della proprieta' del protocollo trasparente, "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		}
	}


	/**
	 * Esempio di read property
	 *   
	 * @return Valore della property
	 * 
	 */
	private static Boolean generateIDasUUID = null;
	public Boolean generateIDasUUID(){
		if(TrasparenteProperties.generateIDasUUID==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.trasparente.id.uuid";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					TrasparenteProperties.generateIDasUUID = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					TrasparenteProperties.generateIDasUUID = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				TrasparenteProperties.generateIDasUUID = defaultValue;
			}
		}

		return TrasparenteProperties.generateIDasUUID;
	}
	
	

	/* **** SOAP FAULT (Protocollo, Porta Applicativa) **** */
	
    /**
     * Indicazione se ritornare un soap fault personalizzato nel codice/actor/faultString per i messaggi di errore di protocollo (Porta Applicativa)
     *   
     * @return Indicazione se ritornare un soap fault personalizzato nel codice/actor/faultString per i messaggi di errore di protocollo (Porta Applicativa)
     * 
     */
	private static Boolean isPortaApplicativaBustaErrore_personalizzaElementiFault= null;
	private static Boolean isPortaApplicativaBustaErrore_personalizzaElementiFaultRead= null;
    public Boolean isPortaApplicativaBustaErrore_personalizzaElementiFault(){
    	if(TrasparenteProperties.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.pa.bustaErrore.personalizzaElementiFault"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					TrasparenteProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				}
				
				TrasparenteProperties.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				TrasparenteProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				
				TrasparenteProperties.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
			}
    	}
    	
    	return TrasparenteProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault;
	}
    
    
    /**
     * Indicazione se deve essere aggiunto un errore-applicativo nei details di un messaggio di errore di protocollo (Porta Applicativa)
     *   
     * @return Indicazione se deve essere aggiunto un errore-applicativo nei details di un messaggio di errore di protocollo (Porta Applicativa)
     * 
     */
	private static Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativo= null;
	private static Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead= null;
    public Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativo(){
    	if(TrasparenteProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.pa.bustaErrore.aggiungiErroreApplicativo"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					TrasparenteProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				}
				
				TrasparenteProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				TrasparenteProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				
				TrasparenteProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
			}
    	}
    	
    	return TrasparenteProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo;
	}
	
    /**
     * Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolValidazione = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolValidazione(){
    	if(TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneIntestazione"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false");
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
			}
    	}
    	
    	return TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione;
	}
    
    /**
     * Indicazione se generare i details in caso di SOAPFault *_300
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_300
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento(){
    	if(TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneProcessamento"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true");
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
			}
    	}
    	
    	return TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento;
	}
    
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace(){
    	if(TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false");
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
			}
    	}
    	
    	return TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace;
	}
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche(){
    	if(TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true");
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
			}
    	}
    	
    	return TrasparenteProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche;
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
    	if(TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.serverError"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true");
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
			}
    	}
    	
    	return TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError;
	}
    
    /**
     * Indicazione se generare i details in Casi di errore 4XX
     *   
     * @return Indicazione se generare i details in Casi di errore 4XX
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationClientError = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationClientError(){
    	if(TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.clientError"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false");
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
			}
    	}
    	
    	return TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError;
	}
    
    /**
     * Indicazione se generare nei details lo stack trace all'interno
     *   
     * @return Indicazione se generare nei details lo stack trace all'interno
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace(){
    	if(TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false");
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
			}
    	}
    	
    	return TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace;
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
    	if(TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode)");
					TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				}
				
				TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode), errore:"+e.getMessage());
				TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				
				TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
			}
    	}
    	
    	return TrasparenteProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche;
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
    	if(TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				}
				
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
			}
    	}
    	
    	return TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo;
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
    	if(TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				}
				
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				
				TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return TrasparenteProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}

    
    /* **** TESTSUITE PROTOCOL PROPERTIES **** */ 
    
	private static Boolean utilizzaTestSuiteProtocolProperties = null;
	public Boolean isUtilizzaTestSuiteProtocolProperties(){
		if(TrasparenteProperties.utilizzaTestSuiteProtocolProperties==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.trasparente.protocolProperties.testsuite.enabled";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					TrasparenteProperties.utilizzaTestSuiteProtocolProperties = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					TrasparenteProperties.utilizzaTestSuiteProtocolProperties = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				TrasparenteProperties.utilizzaTestSuiteProtocolProperties = defaultValue;
			}
		}

		return TrasparenteProperties.utilizzaTestSuiteProtocolProperties;
	}
}
