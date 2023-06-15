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

package org.openspcoop2.protocol.sdi.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * Classe che gestisce il file di properties 'sdi.properties' del protocollo SdI
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIProperties {

	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static SDIProperties sdiProperties = null;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'sdi.properties' */
	private SDIInstanceProperties reader;





	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	public SDIProperties(String confDir,Logger log) throws ProtocolException{

		if(log != null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger("SDIProperties");

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = SDIProperties.class.getResourceAsStream("/sdi.properties");
			if(properties==null){
				throw new Exception("File '/sdi.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'sdi.properties': "+e.getMessage());
			throw new ProtocolException("SDIProperties initialize error: "+e.getMessage(),e);
		}finally{
			try{
				if(properties!=null)
					properties.close();
			}catch(Throwable er){
				// close
			}
		}
		try{
			this.reader = new SDIInstanceProperties(propertiesReader, this.log);
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

		if(SDIProperties.sdiProperties==null)
			SDIProperties.sdiProperties = new SDIProperties(confDir,log);	

	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di OpenSPCoopProperties
	 * @throws Exception 
	 * 
	 */
	public static SDIProperties getInstance(Logger log) throws ProtocolException{

		if(SDIProperties.sdiProperties==null)
			throw new ProtocolException("SDIProperties not initialized (use init method in factory)");

		return SDIProperties.sdiProperties;
	}




	public void validaConfigurazione(Loader loader) throws ProtocolException  {	
		try{  

			this.getTipoSoggettoSDI();
			this.getNomeSoggettoSDI();

			this.isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov();
			this.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov();
			
			this.isEnableValidazioneNomeFile();
			
			this.isEnableValidazioneXsdFatturaDaInviare();
			this.isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura();
			this.isEnable_fatturazioneAttiva_generazioneNomeFileFattura();
			this.isEnable_fatturazioneAttiva_generazioneNomeFileFatturaOpzionale();
			
			this.isEnableValidazioneXsdNotificaDaInviare();
			this.isEnableAccessoNotificaDaInviare();
			this.isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare();
			this.isEnable_fatturazionePassiva_consegnaFileMetadati();
			this.isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura();
			this.isEnable_fatturazionePassiva_generazioneNomeFileEsito();
			
			if(this.isTracciamentoRequiredFromConfiguration()) {
				if(this.getTracciamentoDatasource()==null) {
					throw new Exception("Datasource non definito per l'accesso al database delle tracce");
				}
				this.getTracciamentoDatasource_jndiContext();
				this.getTracciamentoTipoDatabase();
			}
			
			this.isEnableAccessoMetadati();
			this.isEnableAccessoFattura();
			this.isEnableAccessoMessaggi();
			
			this.isEnableAccessoMetadatiWarningMode();
			this.isEnableAccessoFatturaWarningMode();
			this.isEnableAccessoMessaggiWarningMode();
			
			this.isEnableValidazioneXsdMetadati();
			this.isEnableValidazioneXsdFattura();
			this.isEnableValidazioneXsdMessaggi();
			
			this.isEnableValidazioneCampiInterniMetadati();
			this.isEnableValidazioneCampiInterniFattura();
			this.isEnableValidazioneCampiInterniMessaggi();
			
			this.isSaveFatturaInContext();
			this.isSaveMessaggiInContext();
			
			this.isNotificaATConsegnaSoloAttestato();
			
			this.getSoggettiWhiteList();
			this.getServiziWhiteList();
			this.getAzioniWhiteList();
			this.getNamespaceWhiteList();
			
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
			
			this.useConfigStaticInstance();
			this.useErroreApplicativoStaticInstance();
			this.useEsitoStaticInstance();
			this.getStaticInstanceConfig();
			
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la validazione della proprieta' del protocollo sdi, "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		}
	}


	/**
	 * Tipo Soggetto Sistema di Interscambio
	 *   
	 * @return Tipo Soggetto Sistema di Interscambio
	 * 
	 */
	private String tipoSoggettoSDI = null;
	public String getTipoSoggettoSDI() throws ProtocolException{
		if(this.tipoSoggettoSDI==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.soggetto.tipo";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.tipoSoggettoSDI = value;
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.tipoSoggettoSDI;
	}
	
	/**
	 * Nome Soggetto Sistema di Interscambio
	 *   
	 * @return Nome Soggetto Sistema di Interscambio
	 * 
	 */
	private String nomeSoggettoSDI = null;
	public String getNomeSoggettoSDI() throws ProtocolException{
		if(this.nomeSoggettoSDI==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.soggetto.nome";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.nomeSoggettoSDI = value;
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.nomeSoggettoSDI;
	}
	
	/**
	 * Indicazione se abiltiare la Compatibilita' sulle Notifiche da inviare con  Namespace errato 'http://www.fatturapa.it/sdi/messaggi/v1.0'
	 *   
	 * @return Indicazione se abiltiare la Compatibilita' sulle Notifiche da inviare con  Namespace errato 'http://www.fatturapa.it/sdi/messaggi/v1.0'
	 * 
	 */
	private Boolean isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov = null;
	public Boolean isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov() throws ProtocolException{
		if(this.isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.generazione.messaggi.compatibilitaNamespaceSenzaGov";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov;
	}
	
	/**
	 * Indicazione se abiltiare la Compatibilita' sulle Notifiche ricevute con  Namespace errato 'http://www.fatturapa.it/sdi/messaggi/v1.0'
	 *   
	 * @return Indicazione se abiltiare la Compatibilita' sulle Notifiche ricevute con  Namespace errato 'http://www.fatturapa.it/sdi/messaggi/v1.0'
	 * 
	 */
	private Boolean isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov = null;
	public Boolean isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov() throws ProtocolException{
		if(this.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.messaggi.compatibilitaNamespaceSenzaGov";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov;
	}
	
	/**
	 * Indicazione se effettuare la validazione dei nomi di file
	 *   
	 * @return Indicazione se effettuare la validazione dei nomi di file
	 * 
	 */
	private Boolean isEnableValidazioneNomeFile = null;
	public Boolean isEnableValidazioneNomeFile() throws ProtocolException{
		if(this.isEnableValidazioneNomeFile==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.nomeFile.enable";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableValidazioneNomeFile = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableValidazioneNomeFile;
	}

	/**
	 * Indicazione se effettuare la validazione xsd della fattura da inviare
	 *   
	 * @return Indicazione se effettuare la validazione xsd della fattura da inviare
	 * 
	 */
	private Boolean isEnableValidazioneXsdFatturaDaInviare = null;
	public Boolean isEnableValidazioneXsdFatturaDaInviare() throws ProtocolException{
		if(this.isEnableValidazioneXsdFatturaDaInviare==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.xsd.fatturaDaInviare";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableValidazioneXsdFatturaDaInviare = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableValidazioneXsdFatturaDaInviare;
	}
	
	/**
	 * Indicazione se accedere al database delle tracce per aggiungere alle notifiche informazioni prese dalla fattura inviata precedentemente (es. IdTrasmittente (IdPaese + IdCodice), Applicativo che ha inviato la fattura).
	 *   
	 * @return Indicazione se accedere al database delle tracce per aggiungere alle notifiche informazioni prese dalla fattura inviata precedentemente (es. IdTrasmittente (IdPaese + IdCodice), Applicativo che ha inviato la fattura).
	 * 
	 */
	private Boolean isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura = null;
	public Boolean isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura() throws ProtocolException{
		if(this.isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazioneAttiva.notifiche.enrichInfoFromFattura";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura;
	}
	
	/**
	 * Indicazione se il nome file associato alla fattura viene generato da GovWay o viene fornito dall'Applicativo mittente.
	 *   
	 * @return Indicazione se il nome file associato alla fattura viene generato da GovWay o viene fornito dall'Applicativo mittente.
	 * 
	 */
	private Boolean isEnable_fatturazioneAttiva_generazioneNomeFileFattura = null;
	public Boolean isEnable_fatturazioneAttiva_generazioneNomeFileFattura() throws ProtocolException{
		if(this.isEnable_fatturazioneAttiva_generazioneNomeFileFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazioneAttiva.nomeFile.gestione";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnable_fatturazioneAttiva_generazioneNomeFileFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnable_fatturazioneAttiva_generazioneNomeFileFattura;
	}
	
	private Boolean isEnable_fatturazioneAttiva_generazioneNomeFileFatturaOpzionale = null;
	public Boolean isEnable_fatturazioneAttiva_generazioneNomeFileFatturaOpzionale() throws ProtocolException{
		if(this.isEnable_fatturazioneAttiva_generazioneNomeFileFatturaOpzionale==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazioneAttiva.nomeFile.gestioneOpzionale";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnable_fatturazioneAttiva_generazioneNomeFileFatturaOpzionale = Boolean.parseBoolean(value);
				}else{
					this.isEnable_fatturazioneAttiva_generazioneNomeFileFatturaOpzionale = false;
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnable_fatturazioneAttiva_generazioneNomeFileFatturaOpzionale;
	}
	
	/**
	 * Indicazione se effettuare la validazione xsd della notifica da inviare
	 *   
	 * @return Indicazione se effettuare la validazione xsd della notifica da inviare
	 * 
	 */
	private Boolean isEnableValidazioneXsdNotificaDaInviare = null;
	public Boolean isEnableValidazioneXsdNotificaDaInviare() throws ProtocolException{
		if(this.isEnableValidazioneXsdNotificaDaInviare==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.xsd.notificaDaInviare";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableValidazioneXsdNotificaDaInviare = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableValidazioneXsdNotificaDaInviare;
	}
	
	private Boolean isEnableAccessoNotificaDaInviare = null;
	public Boolean isEnableAccessoNotificaDaInviare() throws ProtocolException{
		if(this.isEnableAccessoNotificaDaInviare==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.access.notificaDaInviare";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableAccessoNotificaDaInviare = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableAccessoNotificaDaInviare;
	}
	
	private Boolean isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare = null;
	public Boolean isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare() throws ProtocolException{
		if(this.isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.inputIdSDI.validationAsBigInteger.notificaDaInviare";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare;
	}
	
	/**
	 * Indicazione se serializzare il File MetaDati come header HTTP 'GovWay-SDI-FileMetadati' codificato in BASE64
	 *   
	 * @return Indicazione se serializzare il File MetaDati come header HTTP 'GovWay-SDI-FileMetadati' codificato in BASE64
	 * 
	 */
	private Boolean isEnable_fatturazionePassiva_consegnaFileMetadati = null;
	public Boolean isEnable_fatturazionePassiva_consegnaFileMetadati() throws ProtocolException{
		if(this.isEnable_fatturazionePassiva_consegnaFileMetadati==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazionePassiva.consegnaFileMetadati";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnable_fatturazionePassiva_consegnaFileMetadati = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnable_fatturazionePassiva_consegnaFileMetadati;
	}
	
	/**
	 * Indicazione se accedere al database delle tracce per aggiungere alla notifica decorrenza termini informazioni prese dalla fattura ricevuta precedentemente (es. CodiceDestinatario).
	 *   
	 * @return Indicazione se accedere al database delle tracce per aggiungere alla notifica decorrenza termini informazioni prese dalla fattura ricevuta precedentemente (es. CodiceDestinatario).
	 * 
	 */
	private Boolean isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura = null;
	public Boolean isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura() throws ProtocolException{
		if(this.isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazionePassiva.notifiche.enrichInfoFromFattura";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura;
	}
	
	/**
	 * Indicazione se il nome file associato alla fattura viene generato da GovWay o viene fornito dall'Applicativo mittente.
	 *   
	 * @return Indicazione se il nome file associato alla fattura viene generato da GovWay o viene fornito dall'Applicativo mittente.
	 * 
	 */
	private Boolean isEnable_fatturazionePassiva_generazioneNomeFileEsito = null;
	public Boolean isEnable_fatturazionePassiva_generazioneNomeFileEsito() throws ProtocolException{
		if(this.isEnable_fatturazionePassiva_generazioneNomeFileEsito==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazionePassiva.nomeFile.gestione";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnable_fatturazionePassiva_generazioneNomeFileEsito = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnable_fatturazionePassiva_generazioneNomeFileEsito;
	}
	
	public boolean isTracciamentoRequiredFromConfiguration() throws ProtocolException {
		return this.isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura() ||
				this.isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura();
	}
	
	private Boolean tracciamentoDatasource_read;
	private String tracciamentoDatasource;
	public String getTracciamentoDatasource() throws ProtocolException {
		if(this.tracciamentoDatasource_read==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.tracce.dataSource"); 
				
				if (value != null){
					value = value.trim();
					this.tracciamentoDatasource = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.tracce.dataSource', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.tracciamentoDatasource_read = true;
			}
    	}
		return this.tracciamentoDatasource;
	}
	
	private Boolean tracciamentoTipoDatabase_read;
	private String tracciamentoTipoDatabase;
	public String getTracciamentoTipoDatabase() throws ProtocolException {
		if(this.tracciamentoTipoDatabase_read==null){
	    	try{  
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.tracce.tipoDatabase"); 
				
				if (value != null){
					value = value.trim();
					this.tracciamentoTipoDatabase = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.tracce.tipoDatabase', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				this.tracciamentoTipoDatabase_read = true;
			}
    	}
		return this.tracciamentoTipoDatabase;
	}
	
	private Properties tracciamentoDatasource_jndiContext = null;
	public Properties getTracciamentoDatasource_jndiContext() throws ProtocolException {
		if(this.tracciamentoDatasource_jndiContext==null){
	    	try{  
	    		this.tracciamentoDatasource_jndiContext = this.reader.readPropertiesConvertEnvProperties("org.openspcoop2.protocol.sdi.tracce.dataSource.property.");
	    		if (this.tracciamentoDatasource_jndiContext == null || this.tracciamentoDatasource_jndiContext.size()<0){
	    			this.tracciamentoDatasource_jndiContext = new Properties(); // context jndi vuoto
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.tracce.dataSource.property.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return this.tracciamentoDatasource_jndiContext;
	}
	
	
	/**
	 * Indicazione se effettuare l'accesso ai metadati
	 *   
	 * @return Indicazione se effettuare l'accesso ai metadati
	 * 
	 */
	private Boolean isEnableAccessoMetadati = null;
	public Boolean isEnableAccessoMetadati() throws ProtocolException{
		if(this.isEnableAccessoMetadati==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiMetadati.enable";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableAccessoMetadati = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableAccessoMetadati;
	}
	
	/**
	 * Indicazione se effettuare l'accesso alla fattura
	 *   
	 * @return Indicazione se effettuare l'accesso alla fattura
	 * 
	 */
	private Boolean isEnableAccessoFattura = null;
	public Boolean isEnableAccessoFattura() throws ProtocolException{
		if(this.isEnableAccessoFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiFattura.enable";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableAccessoFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableAccessoFattura;
	}
	
	/**
	 * Indicazione se effettuare l'accesso ai messaggi
	 *   
	 * @return Indicazione se effettuare l'accesso ai messaggi
	 * 
	 */
	private Boolean isEnableAccessoMessaggi = null;
	public Boolean isEnableAccessoMessaggi() throws ProtocolException{
		if(this.isEnableAccessoMessaggi==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiMessaggi.enable";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableAccessoMessaggi = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableAccessoMessaggi;
	}
	
	/**
	 * Indicazione se effettuare l'accesso ai metadati in warning mode
	 *   
	 * @return Indicazione se effettuare l'accesso ai metadati in warning mode
	 * 
	 */
	private Boolean isEnableAccessoMetadatiWarningMode = null;
	public Boolean isEnableAccessoMetadatiWarningMode() throws ProtocolException{
		if(this.isEnableAccessoMetadatiWarningMode==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiMetadati.enable.throwError";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableAccessoMetadatiWarningMode = !Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableAccessoMetadatiWarningMode;
	}
	
	/**
	 * Indicazione se effettuare l'accesso alla fattura in warning mode
	 *   
	 * @return Indicazione se effettuare l'accesso alla fattura in warning mode
	 * 
	 */
	private Boolean isEnableAccessoFatturaWarningMode = null;
	public Boolean isEnableAccessoFatturaWarningMode() throws ProtocolException{
		if(this.isEnableAccessoFatturaWarningMode==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiFattura.enable.throwError";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableAccessoFatturaWarningMode = !Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableAccessoFatturaWarningMode;
	}
	
	/**
	 * Indicazione se effettuare l'accesso ai messaggi in warning mode
	 *   
	 * @return Indicazione se effettuare l'accesso ai messaggi in warning mode
	 * 
	 */
	private Boolean isEnableAccessoMessaggiWarningMode = null;
	public Boolean isEnableAccessoMessaggiWarningMode() throws ProtocolException{
		if(this.isEnableAccessoMessaggiWarningMode==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiMessaggi.enable.throwError";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableAccessoMessaggiWarningMode = !Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableAccessoMessaggiWarningMode;
	}
	
	/**
	 * Indicazione se effettuare la validazione xsd dei metadati
	 *   
	 * @return Indicazione se effettuare la validazione xsd dei metadati
	 * 
	 */
	private Boolean isEnableValidazioneXsdMetadati = null;
	public Boolean isEnableValidazioneXsdMetadati() throws ProtocolException{
		if(this.isEnableValidazioneXsdMetadati==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.xsd.metadati";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableValidazioneXsdMetadati = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableValidazioneXsdMetadati;
	}
	
	/**
	 * Indicazione se effettuare la validazione xsd della fattura
	 *   
	 * @return Indicazione se effettuare la validazione xsd della fattura
	 * 
	 */
	private Boolean isEnableValidazioneXsdFattura = null;
	public Boolean isEnableValidazioneXsdFattura() throws ProtocolException{
		if(this.isEnableValidazioneXsdFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.xsd.fattura";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableValidazioneXsdFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableValidazioneXsdFattura;
	}
	
	/**
	 * Indicazione se effettuare la validazione xsd dei messaggi
	 *   
	 * @return Indicazione se effettuare la validazione xsd dei messaggi
	 * 
	 */
	private Boolean isEnableValidazioneXsdMessaggi = null;
	public Boolean isEnableValidazioneXsdMessaggi() throws ProtocolException{
		if(this.isEnableValidazioneXsdMessaggi==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.xsd.messaggi";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableValidazioneXsdMessaggi = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableValidazioneXsdMessaggi;
	}
	
	/**
	 * Indicazione se effettuare la validazione dei campi interni ai metadati
	 *   
	 * @return Indicazione se effettuare la validazione dei campi interni ai metadati
	 * 
	 */
	private Boolean isEnableValidazioneCampiInterniMetadati = null;
	public Boolean isEnableValidazioneCampiInterniMetadati() throws ProtocolException{
		if(this.isEnableValidazioneCampiInterniMetadati==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.campiMetadati.enable";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableValidazioneCampiInterniMetadati = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableValidazioneCampiInterniMetadati;
	}
	
	/**
	 * Indicazione se effettuare la validazione dei campi interni alla fattura
	 *   
	 * @return Indicazione se effettuare la validazione dei campi interni alla fattura
	 * 
	 */
	private Boolean isEnableValidazioneCampiInterniFattura = null;
	public Boolean isEnableValidazioneCampiInterniFattura() throws ProtocolException{
		if(this.isEnableValidazioneCampiInterniFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.campiFattura.enable";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableValidazioneCampiInterniFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableValidazioneCampiInterniFattura;
	}
	
	/**
	 * Indicazione se effettuare la validazione dei campi interni ai messaggi
	 *   
	 * @return Indicazione se effettuare la validazione dei campi interni ai messaggi
	 * 
	 */
	private Boolean isEnableValidazioneCampiInterniMessaggi = null;
	public Boolean isEnableValidazioneCampiInterniMessaggi() throws ProtocolException{
		if(this.isEnableValidazioneCampiInterniMessaggi==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.campiMessaggi.enable";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isEnableValidazioneCampiInterniMessaggi = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isEnableValidazioneCampiInterniMessaggi;
	}
	
		
	
	
	/**
	 * Indicazione se effettuare il salvataggio della fattura letta nel context
	 *   
	 * @return Indicazione se effettuare il salvataggio della fattura letta nel context
	 * 
	 */
	private Boolean isSaveFatturaInContext = null;
	public Boolean isSaveFatturaInContext() throws ProtocolException{
		if(this.isSaveFatturaInContext==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.parse.fattura.saveInContext";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSaveFatturaInContext = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isSaveFatturaInContext;
	}
	
	/**
	 * Indicazione se effettuare il salvataggio dei messsaggi di servizio letti nel context
	 *   
	 * @return Indicazione se effettuare il salvataggio dei messsaggi di servizio letti nel context
	 * 
	 */
	private Boolean isSaveMessaggiInContext = null;
	public Boolean isSaveMessaggiInContext() throws ProtocolException{
		if(this.isSaveMessaggiInContext==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.parse.messaggi.saveInContext";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isSaveMessaggiInContext = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isSaveMessaggiInContext;
	}
	
	
	
	/**
	 * Indicazione se effettuare il salvataggio dei messsaggi di servizio letti nel context
	 *   
	 * @return Indicazione se effettuare il salvataggio dei messsaggi di servizio letti nel context
	 * 
	 */
	private Boolean isNotificaATConsegnaSoloAttestato = null;
	public Boolean isNotificaATConsegnaSoloAttestato() throws ProtocolException{
		if(this.isNotificaATConsegnaSoloAttestato==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.notifica.attestazioneTrasmissioneImpossibilitaRecapito.consegnaSoloAttestato";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isNotificaATConsegnaSoloAttestato = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isNotificaATConsegnaSoloAttestato;
	}
	
	
	
	/**
	 * Indicazione se viene ricreato il protocollo sdi, in tal caso e' possibile utilizzare l'opzione sbustamento informazioni protocollo disabilitato.
	 *   
	 * @return Indicazione se viene ricreato il protocollo sdi, in tal caso e' possibile utilizzare l'opzione sbustamento informazioni protocollo disabilitato.
	 * 
	 */
	private Boolean isBehaviourCreaProtocolloSDI = null;
	public Boolean isBehaviourCreaProtocolloSDI() throws ProtocolException{
		if(this.isBehaviourCreaProtocolloSDI==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.behaviour.creaProtocolloSDI";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					this.isBehaviourCreaProtocolloSDI = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.isBehaviourCreaProtocolloSDI;
	}
	
	
	// Utile per sonde applicative
	
	private List<String> soggettiWhiteList = null;
	public List<String> getSoggettiWhiteList() throws ProtocolException{
		if(this.soggettiWhiteList==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.whiteList.soggetti";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				this.soggettiWhiteList = new ArrayList<>();
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.soggettiWhiteList.add(tmp[i]);
					}
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.soggettiWhiteList;
	}
	
	private List<String> serviziWhiteList = null;
	public List<String> getServiziWhiteList() throws ProtocolException{
		if(this.serviziWhiteList==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.whiteList.servizi";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				this.serviziWhiteList = new ArrayList<>();
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.serviziWhiteList.add(tmp[i]);
					}
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.serviziWhiteList;
	}
	
	private List<String> azioniWhiteList = null;
	public List<String> getAzioniWhiteList() throws ProtocolException{
		if(this.azioniWhiteList==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.whiteList.azioni";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				this.azioniWhiteList = new ArrayList<>();
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.azioniWhiteList.add(tmp[i]);
					}
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.azioniWhiteList;
	}
	
	private List<String> namespaceWhiteList = null;
	public List<String> getNamespaceWhiteList() throws ProtocolException{
		if(this.namespaceWhiteList==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.whiteList.namespace";
			
			try{  
				String value = this.reader.getValueConvertEnvProperties(propertyName); 

				this.namespaceWhiteList = new ArrayList<>();
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						this.namespaceWhiteList.add(tmp[i]);
					}
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return this.namespaceWhiteList;
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.pa.bustaErrore.personalizzaElementiFault"); 
				
				if (value != null){
					value = value.trim();
					this.isPortaApplicativaBustaErrore_personalizzaElementiFault = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				}
				
				this.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.pa.bustaErrore.aggiungiErroreApplicativo"); 
				
				if (value != null){
					value = value.trim();
					this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				}
				
				this.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneIntestazione"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolValidazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneProcessamento"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.serverError"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationServerError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true");
					this.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.clientError"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationClientError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false");
					this.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode)");
					this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				}
				
				this.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode), errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = b ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
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
				String value = this.reader.getValueConvertEnvProperties("org.openspcoop2.protocol.sdi.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					Boolean b = Boolean.parseBoolean(value);
					this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = b ? BooleanNullable.TRUE() : BooleanNullable.FALSE();
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = BooleanNullable.NULL();
				}
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdD = BooleanNullable.NULL();
				
				this.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return this.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}
    
    
	private Boolean useConfigStaticInstance = null;
	private Boolean useConfigStaticInstance(){
		if(this.useConfigStaticInstance==null){
			
			Boolean defaultValue = true;
			String propertyName = "org.openspcoop2.protocol.sdi.factory.config.staticInstance";
			
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
			String propertyName = "org.openspcoop2.protocol.sdi.factory.erroreApplicativo.staticInstance";
			
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
			String propertyName = "org.openspcoop2.protocol.sdi.factory.esito.staticInstance";
			
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
