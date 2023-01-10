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

package org.openspcoop2.protocol.trasparente.config;

import java.util.Properties;

import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

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
			}catch(Throwable er){
				// close
			}
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
	public static TrasparenteProperties getInstance() throws ProtocolException{

		if(TrasparenteProperties.trasparenteProperties==null)
			throw new ProtocolException("TrasparenteProperties not initialized (use init method in factory)");

		return TrasparenteProperties.trasparenteProperties;
	}




	public void validaConfigurazione(Loader loader) throws ProtocolException  {	
		try{  

			generateIDasUUID();
			
			isRiferimentoIDRichiesta_PD_Required();
			isRiferimentoIDRichiesta_PA_Required();
			
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
			
			this.isUtilizzaTestSuiteGenerazioneTracce();
			this.isUtilizzaTestSuiteProtocolProperties();
			
			this.useConfigStaticInstance();
			this.useErroreApplicativoStaticInstance();
			this.useEsitoStaticInstance();
			this.getStaticInstanceConfig();

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
	private Boolean generateIDasUUID = null;
	public Boolean generateIDasUUID(){
		if(this.generateIDasUUID==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.trasparente.id.uuid";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.generateIDasUUID = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.generateIDasUUID = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.generateIDasUUID = defaultValue;
			}
		}

		return this.generateIDasUUID;
	}
	
	
	
	/* **** CONFIGURAZIONE **** */
	
    /**
     * Restituisce l'indicazione se la funzionalita' 'Riferimento ID Richiesta' richiede che venga fornito obbligatoriamente l'informazione sull'identificativo della richiesta tramite i meccanismi di integrazione
	 * 
	 * @return True se la funzionalita' 'Riferimento ID Richiesta' richiede che venga fornito obbligatoriamente l'informazione sull'identificativo della richiesta tramite i meccanismi di integrazione
     * 
     */
	private Boolean isRiferimentoIDRichiesta_PD_Required= null;
	private Boolean isRiferimentoIDRichiesta_PD_RequiredRead= null;
    public Boolean isRiferimentoIDRichiesta_PD_Required(){
    	if(this.isRiferimentoIDRichiesta_PD_RequiredRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.pd.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					this.isRiferimentoIDRichiesta_PD_Required = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true'");
					this.isRiferimentoIDRichiesta_PD_Required = true;
				}
				
				this.isRiferimentoIDRichiesta_PD_RequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pd.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				this.isRiferimentoIDRichiesta_PD_Required = true;
				
				this.isRiferimentoIDRichiesta_PD_RequiredRead = true;
			}
    	}
    	
    	return this.isRiferimentoIDRichiesta_PD_Required;
	}
	
	private Boolean isRiferimentoIDRichiesta_PA_Required= null;
	private Boolean isRiferimentoIDRichiesta_PA_RequiredRead= null;
    public Boolean isRiferimentoIDRichiesta_PA_Required(){
    	if(this.isRiferimentoIDRichiesta_PA_RequiredRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.pa.riferimentoIdRichiesta.required"); 
				
				if (value != null){
					value = value.trim();
					this.isRiferimentoIDRichiesta_PA_Required = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true'");
					this.isRiferimentoIDRichiesta_PA_Required = true;
				}
				
				this.isRiferimentoIDRichiesta_PA_RequiredRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pa.riferimentoIdRichiesta.required' non impostata, viene utilizzato il default 'true', errore:"+e.getMessage());
				this.isRiferimentoIDRichiesta_PA_Required = true;
				
				this.isRiferimentoIDRichiesta_PA_RequiredRead = true;
			}
    	}
    	
    	return this.isRiferimentoIDRichiesta_PA_Required;
	}
	
	

	/* **** SOAP FAULT (Protocollo, Porta Applicativa) **** */
	
    /**
     * Indicazione se ritornare un soap fault personalizzato nel codice/actor/faultString per i messaggi di errore di protocollo (Porta Applicativa)
     *   
     * @return Indicazione se ritornare un soap fault personalizzato nel codice/actor/faultString per i messaggi di errore di protocollo (Porta Applicativa)
     * 
     */
	private Boolean isPortaApplicativaBustaErrore_personalizzaElementiFault= null;
	private Boolean isPortaApplicativaBustaErrore_personalizzaElementiFaultRead= null;
    public Boolean isPortaApplicativaBustaErrore_personalizzaElementiFault(){
    	if(this.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.pa.bustaErrore.personalizzaElementiFault"); 
				
				if (value != null){
					value = value.trim();
					this.isPortaApplicativaBustaErrore_personalizzaElementiFault = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				}
				
				this.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				this.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				
				this.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
			}
    	}
    	
    	return this.isPortaApplicativaBustaErrore_personalizzaElementiFault;
	}
    
    
    /**
     * Indicazione se deve essere aggiunto un errore-applicativo nei details di un messaggio di errore di protocollo (Porta Applicativa)
     *   
     * @return Indicazione se deve essere aggiunto un errore-applicativo nei details di un messaggio di errore di protocollo (Porta Applicativa)
     * 
     */
	private Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativo= null;
	private Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead= null;
    public Boolean isPortaApplicativaBustaErrore_aggiungiErroreApplicativo(){
    	if(this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.pa.bustaErrore.aggiungiErroreApplicativo"); 
				
				if (value != null){
					value = value.trim();
					this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				}
				
				this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				
				this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
			}
    	}
    	
    	return this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo;
	}
	
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneIntestazione"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolValidazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneProcessamento"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.serverError"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationServerError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.clientError"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationClientError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode)");
					this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				}
				
				this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode), errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = b ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
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
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.trasparente.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = b ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.trasparente.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = BooleanNullable.NULL();
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return this.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}

    
    /* **** TESTSUITE PROTOCOL PROPERTIES **** */ 
    
    private Boolean utilizzaTestSuiteGenerazioneTracce = null;
	public Boolean isUtilizzaTestSuiteGenerazioneTracce(){
		if(this.utilizzaTestSuiteGenerazioneTracce==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.trasparente.generazioneTracce.testsuite.enabled";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.utilizzaTestSuiteGenerazioneTracce = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.utilizzaTestSuiteGenerazioneTracce = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.utilizzaTestSuiteGenerazioneTracce = defaultValue;
			}
		}

		return this.utilizzaTestSuiteGenerazioneTracce;
	}
    
	private Boolean utilizzaTestSuiteProtocolProperties = null;
	public Boolean isUtilizzaTestSuiteProtocolProperties(){
		if(this.utilizzaTestSuiteProtocolProperties==null){
			
			Boolean defaultValue = false;
			String propertyName = "org.openspcoop2.protocol.trasparente.protocolProperties.testsuite.enabled";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.utilizzaTestSuiteProtocolProperties = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue);
					this.utilizzaTestSuiteProtocolProperties = defaultValue;
				}

			}catch(java.lang.Exception e) {
				this.log.debug("Proprieta' di openspcoop '"+propertyName+"' non impostata, viene utilizzato il default="+defaultValue+", errore:"+e.getMessage());
				this.utilizzaTestSuiteProtocolProperties = defaultValue;
			}
		}

		return this.utilizzaTestSuiteProtocolProperties;
	}
	
	
	
	private Boolean useConfigStaticInstance = null;
	private Boolean useConfigStaticInstance(){
		if(this.useConfigStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.trasparente.factory.config.staticInstance";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

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
			String propertyName = "org.openspcoop2.protocol.trasparente.factory.erroreApplicativo.staticInstance";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

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
			String propertyName = "org.openspcoop2.protocol.trasparente.factory.esito.staticInstance";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

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
