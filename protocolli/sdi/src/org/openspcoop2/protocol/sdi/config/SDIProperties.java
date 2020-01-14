/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

import org.openspcoop2.protocol.sdk.ProtocolException;
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
			}catch(Exception er){}
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
	private static String tipoSoggettoSDI = null;
	public String getTipoSoggettoSDI() throws ProtocolException{
		if(SDIProperties.tipoSoggettoSDI==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.soggetto.tipo";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.tipoSoggettoSDI = value;
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.tipoSoggettoSDI;
	}
	
	/**
	 * Nome Soggetto Sistema di Interscambio
	 *   
	 * @return Nome Soggetto Sistema di Interscambio
	 * 
	 */
	private static String nomeSoggettoSDI = null;
	public String getNomeSoggettoSDI() throws ProtocolException{
		if(SDIProperties.nomeSoggettoSDI==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.soggetto.nome";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.nomeSoggettoSDI = value;
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.nomeSoggettoSDI;
	}
	
	/**
	 * Indicazione se abiltiare la Compatibilita' sulle Notifiche da inviare con  Namespace errato 'http://www.fatturapa.it/sdi/messaggi/v1.0'
	 *   
	 * @return Indicazione se abiltiare la Compatibilita' sulle Notifiche da inviare con  Namespace errato 'http://www.fatturapa.it/sdi/messaggi/v1.0'
	 * 
	 */
	private static Boolean isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov = null;
	public Boolean isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov() throws ProtocolException{
		if(SDIProperties.isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.generazione.messaggi.compatibilitaNamespaceSenzaGov";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableGenerazioneMessaggiCompatibilitaNamespaceSenzaGov;
	}
	
	/**
	 * Indicazione se abiltiare la Compatibilita' sulle Notifiche ricevute con  Namespace errato 'http://www.fatturapa.it/sdi/messaggi/v1.0'
	 *   
	 * @return Indicazione se abiltiare la Compatibilita' sulle Notifiche ricevute con  Namespace errato 'http://www.fatturapa.it/sdi/messaggi/v1.0'
	 * 
	 */
	private static Boolean isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov = null;
	public Boolean isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov() throws ProtocolException{
		if(SDIProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.messaggi.compatibilitaNamespaceSenzaGov";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov;
	}
	
	/**
	 * Indicazione se effettuare la validazione dei nomi di file
	 *   
	 * @return Indicazione se effettuare la validazione dei nomi di file
	 * 
	 */
	private static Boolean isEnableValidazioneNomeFile = null;
	public Boolean isEnableValidazioneNomeFile() throws ProtocolException{
		if(SDIProperties.isEnableValidazioneNomeFile==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.nomeFile.enable";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableValidazioneNomeFile = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableValidazioneNomeFile;
	}

	/**
	 * Indicazione se effettuare la validazione xsd della fattura da inviare
	 *   
	 * @return Indicazione se effettuare la validazione xsd della fattura da inviare
	 * 
	 */
	private static Boolean isEnableValidazioneXsdFatturaDaInviare = null;
	public Boolean isEnableValidazioneXsdFatturaDaInviare() throws ProtocolException{
		if(SDIProperties.isEnableValidazioneXsdFatturaDaInviare==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.xsd.fatturaDaInviare";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableValidazioneXsdFatturaDaInviare = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableValidazioneXsdFatturaDaInviare;
	}
	
	/**
	 * Indicazione se accedere al database delle tracce per aggiungere alle notifiche informazioni prese dalla fattura inviata precedentemente (es. IdTrasmittente (IdPaese + IdCodice), Applicativo che ha inviato la fattura).
	 *   
	 * @return Indicazione se accedere al database delle tracce per aggiungere alle notifiche informazioni prese dalla fattura inviata precedentemente (es. IdTrasmittente (IdPaese + IdCodice), Applicativo che ha inviato la fattura).
	 * 
	 */
	private static Boolean isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura = null;
	public Boolean isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura() throws ProtocolException{
		if(SDIProperties.isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazioneAttiva.notifiche.enrichInfoFromFattura";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura;
	}
	
	/**
	 * Indicazione se il nome file associato alla fattura viene generato da GovWay o viene fornito dall'Applicativo mittente.
	 *   
	 * @return Indicazione se il nome file associato alla fattura viene generato da GovWay o viene fornito dall'Applicativo mittente.
	 * 
	 */
	private static Boolean isEnable_fatturazioneAttiva_generazioneNomeFileFattura = null;
	public Boolean isEnable_fatturazioneAttiva_generazioneNomeFileFattura() throws ProtocolException{
		if(SDIProperties.isEnable_fatturazioneAttiva_generazioneNomeFileFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazioneAttiva.nomeFile.gestione";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnable_fatturazioneAttiva_generazioneNomeFileFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnable_fatturazioneAttiva_generazioneNomeFileFattura;
	}
	
	/**
	 * Indicazione se effettuare la validazione xsd della notifica da inviare
	 *   
	 * @return Indicazione se effettuare la validazione xsd della notifica da inviare
	 * 
	 */
	private static Boolean isEnableValidazioneXsdNotificaDaInviare = null;
	public Boolean isEnableValidazioneXsdNotificaDaInviare() throws ProtocolException{
		if(SDIProperties.isEnableValidazioneXsdNotificaDaInviare==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.xsd.notificaDaInviare";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableValidazioneXsdNotificaDaInviare = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableValidazioneXsdNotificaDaInviare;
	}
	
	private static Boolean isEnableAccessoNotificaDaInviare = null;
	public Boolean isEnableAccessoNotificaDaInviare() throws ProtocolException{
		if(SDIProperties.isEnableAccessoNotificaDaInviare==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.access.notificaDaInviare";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableAccessoNotificaDaInviare = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableAccessoNotificaDaInviare;
	}
	
	private static Boolean isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare = null;
	public Boolean isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare() throws ProtocolException{
		if(SDIProperties.isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.inputIdSDI.validationAsBigInteger.notificaDaInviare";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnable_InputIdSDIValidationAsBigInteger_NotificaDaInviare;
	}
	
	/**
	 * Indicazione se serializzare il File MetaDati come header HTTP 'GovWay-SDI-FileMetadati' codificato in BASE64
	 *   
	 * @return Indicazione se serializzare il File MetaDati come header HTTP 'GovWay-SDI-FileMetadati' codificato in BASE64
	 * 
	 */
	private static Boolean isEnable_fatturazionePassiva_consegnaFileMetadati = null;
	public Boolean isEnable_fatturazionePassiva_consegnaFileMetadati() throws ProtocolException{
		if(SDIProperties.isEnable_fatturazionePassiva_consegnaFileMetadati==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazionePassiva.consegnaFileMetadati";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnable_fatturazionePassiva_consegnaFileMetadati = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnable_fatturazionePassiva_consegnaFileMetadati;
	}
	
	/**
	 * Indicazione se accedere al database delle tracce per aggiungere alla notifica decorrenza termini informazioni prese dalla fattura ricevuta precedentemente (es. CodiceDestinatario).
	 *   
	 * @return Indicazione se accedere al database delle tracce per aggiungere alla notifica decorrenza termini informazioni prese dalla fattura ricevuta precedentemente (es. CodiceDestinatario).
	 * 
	 */
	private static Boolean isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura = null;
	public Boolean isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura() throws ProtocolException{
		if(SDIProperties.isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazionePassiva.notifiche.enrichInfoFromFattura";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura;
	}
	
	/**
	 * Indicazione se il nome file associato alla fattura viene generato da GovWay o viene fornito dall'Applicativo mittente.
	 *   
	 * @return Indicazione se il nome file associato alla fattura viene generato da GovWay o viene fornito dall'Applicativo mittente.
	 * 
	 */
	private static Boolean isEnable_fatturazionePassiva_generazioneNomeFileEsito = null;
	public Boolean isEnable_fatturazionePassiva_generazioneNomeFileEsito() throws ProtocolException{
		if(SDIProperties.isEnable_fatturazionePassiva_generazioneNomeFileEsito==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.fatturazionePassiva.nomeFile.gestione";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnable_fatturazionePassiva_generazioneNomeFileEsito = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnable_fatturazionePassiva_generazioneNomeFileEsito;
	}
	
	public boolean isTracciamentoRequiredFromConfiguration() throws ProtocolException {
		return this.isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura() ||
				this.isEnable_fatturazionePassiva_notifiche_enrichInfoFromFattura();
	}
	
	private static Boolean tracciamentoDatasource_read;
	private static String tracciamentoDatasource;
	public String getTracciamentoDatasource() throws ProtocolException {
		if(SDIProperties.tracciamentoDatasource_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.tracce.dataSource"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.tracciamentoDatasource = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.tracce.dataSource', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				tracciamentoDatasource_read = true;
			}
    	}
		return SDIProperties.tracciamentoDatasource;
	}
	
	private static Boolean tracciamentoTipoDatabase_read;
	private static String tracciamentoTipoDatabase;
	public String getTracciamentoTipoDatabase() throws ProtocolException {
		if(SDIProperties.tracciamentoTipoDatabase_read==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.tracce.tipoDatabase"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.tracciamentoTipoDatabase = value;
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.tracce.tipoDatabase', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}finally {
				tracciamentoTipoDatabase_read = true;
			}
    	}
		return SDIProperties.tracciamentoTipoDatabase;
	}
	
	private static Properties tracciamentoDatasource_jndiContext = null;
	public Properties getTracciamentoDatasource_jndiContext() throws ProtocolException {
		if(SDIProperties.tracciamentoDatasource_jndiContext==null){
	    	try{  
	    		SDIProperties.tracciamentoDatasource_jndiContext = this.reader.readProperties_convertEnvProperties("org.openspcoop2.protocol.sdi.tracce.dataSource.property.");
	    		if (SDIProperties.tracciamentoDatasource_jndiContext == null || SDIProperties.tracciamentoDatasource_jndiContext.size()<0){
	    			SDIProperties.tracciamentoDatasource_jndiContext = new Properties(); // context jndi vuoto
				}
				
			}catch(java.lang.Exception e) {
				this.log.error("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.tracce.dataSource.property.*', errore:"+e.getMessage());
				throw new ProtocolException(e);
			}
    	}
		return SDIProperties.tracciamentoDatasource_jndiContext;
	}
	
	
	/**
	 * Indicazione se effettuare l'accesso ai metadati
	 *   
	 * @return Indicazione se effettuare l'accesso ai metadati
	 * 
	 */
	private static Boolean isEnableAccessoMetadati = null;
	public Boolean isEnableAccessoMetadati() throws ProtocolException{
		if(SDIProperties.isEnableAccessoMetadati==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiMetadati.enable";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableAccessoMetadati = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableAccessoMetadati;
	}
	
	/**
	 * Indicazione se effettuare l'accesso alla fattura
	 *   
	 * @return Indicazione se effettuare l'accesso alla fattura
	 * 
	 */
	private static Boolean isEnableAccessoFattura = null;
	public Boolean isEnableAccessoFattura() throws ProtocolException{
		if(SDIProperties.isEnableAccessoFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiFattura.enable";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableAccessoFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableAccessoFattura;
	}
	
	/**
	 * Indicazione se effettuare l'accesso ai messaggi
	 *   
	 * @return Indicazione se effettuare l'accesso ai messaggi
	 * 
	 */
	private static Boolean isEnableAccessoMessaggi = null;
	public Boolean isEnableAccessoMessaggi() throws ProtocolException{
		if(SDIProperties.isEnableAccessoMessaggi==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiMessaggi.enable";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableAccessoMessaggi = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableAccessoMessaggi;
	}
	
	/**
	 * Indicazione se effettuare l'accesso ai metadati in warning mode
	 *   
	 * @return Indicazione se effettuare l'accesso ai metadati in warning mode
	 * 
	 */
	private static Boolean isEnableAccessoMetadatiWarningMode = null;
	public Boolean isEnableAccessoMetadatiWarningMode() throws ProtocolException{
		if(SDIProperties.isEnableAccessoMetadatiWarningMode==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiMetadati.enable";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableAccessoMetadatiWarningMode = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableAccessoMetadatiWarningMode;
	}
	
	/**
	 * Indicazione se effettuare l'accesso alla fattura in warning mode
	 *   
	 * @return Indicazione se effettuare l'accesso alla fattura in warning mode
	 * 
	 */
	private static Boolean isEnableAccessoFatturaWarningMode = null;
	public Boolean isEnableAccessoFatturaWarningMode() throws ProtocolException{
		if(SDIProperties.isEnableAccessoFatturaWarningMode==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiFattura.enable";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableAccessoFatturaWarningMode = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableAccessoFatturaWarningMode;
	}
	
	/**
	 * Indicazione se effettuare l'accesso ai messaggi in warning mode
	 *   
	 * @return Indicazione se effettuare l'accesso ai messaggi in warning mode
	 * 
	 */
	private static Boolean isEnableAccessoMessaggiWarningMode = null;
	public Boolean isEnableAccessoMessaggiWarningMode() throws ProtocolException{
		if(SDIProperties.isEnableAccessoMessaggiWarningMode==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.accesso.campiMessaggi.enable";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableAccessoMessaggiWarningMode = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableAccessoMessaggiWarningMode;
	}
	
	/**
	 * Indicazione se effettuare la validazione xsd dei metadati
	 *   
	 * @return Indicazione se effettuare la validazione xsd dei metadati
	 * 
	 */
	private static Boolean isEnableValidazioneXsdMetadati = null;
	public Boolean isEnableValidazioneXsdMetadati() throws ProtocolException{
		if(SDIProperties.isEnableValidazioneXsdMetadati==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.xsd.metadati";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableValidazioneXsdMetadati = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableValidazioneXsdMetadati;
	}
	
	/**
	 * Indicazione se effettuare la validazione xsd della fattura
	 *   
	 * @return Indicazione se effettuare la validazione xsd della fattura
	 * 
	 */
	private static Boolean isEnableValidazioneXsdFattura = null;
	public Boolean isEnableValidazioneXsdFattura() throws ProtocolException{
		if(SDIProperties.isEnableValidazioneXsdFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.xsd.fattura";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableValidazioneXsdFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableValidazioneXsdFattura;
	}
	
	/**
	 * Indicazione se effettuare la validazione xsd dei messaggi
	 *   
	 * @return Indicazione se effettuare la validazione xsd dei messaggi
	 * 
	 */
	private static Boolean isEnableValidazioneXsdMessaggi = null;
	public Boolean isEnableValidazioneXsdMessaggi() throws ProtocolException{
		if(SDIProperties.isEnableValidazioneXsdMessaggi==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.xsd.messaggi";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableValidazioneXsdMessaggi = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableValidazioneXsdMessaggi;
	}
	
	/**
	 * Indicazione se effettuare la validazione dei campi interni ai metadati
	 *   
	 * @return Indicazione se effettuare la validazione dei campi interni ai metadati
	 * 
	 */
	private static Boolean isEnableValidazioneCampiInterniMetadati = null;
	public Boolean isEnableValidazioneCampiInterniMetadati() throws ProtocolException{
		if(SDIProperties.isEnableValidazioneCampiInterniMetadati==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.campiMetadati.enable";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableValidazioneCampiInterniMetadati = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableValidazioneCampiInterniMetadati;
	}
	
	/**
	 * Indicazione se effettuare la validazione dei campi interni alla fattura
	 *   
	 * @return Indicazione se effettuare la validazione dei campi interni alla fattura
	 * 
	 */
	private static Boolean isEnableValidazioneCampiInterniFattura = null;
	public Boolean isEnableValidazioneCampiInterniFattura() throws ProtocolException{
		if(SDIProperties.isEnableValidazioneCampiInterniFattura==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.campiFattura.enable";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableValidazioneCampiInterniFattura = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableValidazioneCampiInterniFattura;
	}
	
	/**
	 * Indicazione se effettuare la validazione dei campi interni ai messaggi
	 *   
	 * @return Indicazione se effettuare la validazione dei campi interni ai messaggi
	 * 
	 */
	private static Boolean isEnableValidazioneCampiInterniMessaggi = null;
	public Boolean isEnableValidazioneCampiInterniMessaggi() throws ProtocolException{
		if(SDIProperties.isEnableValidazioneCampiInterniMessaggi==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.validazione.campiMessaggi.enable";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isEnableValidazioneCampiInterniMessaggi = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isEnableValidazioneCampiInterniMessaggi;
	}
	
		
	
	
	/**
	 * Indicazione se effettuare il salvataggio della fattura letta nel context
	 *   
	 * @return Indicazione se effettuare il salvataggio della fattura letta nel context
	 * 
	 */
	private static Boolean isSaveFatturaInContext = null;
	public Boolean isSaveFatturaInContext() throws ProtocolException{
		if(SDIProperties.isSaveFatturaInContext==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.parse.fattura.saveInContext";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isSaveFatturaInContext = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isSaveFatturaInContext;
	}
	
	/**
	 * Indicazione se effettuare il salvataggio dei messsaggi di servizio letti nel context
	 *   
	 * @return Indicazione se effettuare il salvataggio dei messsaggi di servizio letti nel context
	 * 
	 */
	private static Boolean isSaveMessaggiInContext = null;
	public Boolean isSaveMessaggiInContext() throws ProtocolException{
		if(SDIProperties.isSaveMessaggiInContext==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.parse.messaggi.saveInContext";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isSaveMessaggiInContext = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isSaveMessaggiInContext;
	}
	
	
	
	/**
	 * Indicazione se effettuare il salvataggio dei messsaggi di servizio letti nel context
	 *   
	 * @return Indicazione se effettuare il salvataggio dei messsaggi di servizio letti nel context
	 * 
	 */
	private static Boolean isNotificaATConsegnaSoloAttestato = null;
	public Boolean isNotificaATConsegnaSoloAttestato() throws ProtocolException{
		if(SDIProperties.isNotificaATConsegnaSoloAttestato==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.notifica.attestazioneTrasmissioneImpossibilitaRecapito.consegnaSoloAttestato";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isNotificaATConsegnaSoloAttestato = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isNotificaATConsegnaSoloAttestato;
	}
	
	
	
	/**
	 * Indicazione se viene ricreato il protocollo sdi, in tal caso e' possibile utilizzare l'opzione sbustamento informazioni protocollo disabilitato.
	 *   
	 * @return Indicazione se viene ricreato il protocollo sdi, in tal caso e' possibile utilizzare l'opzione sbustamento informazioni protocollo disabilitato.
	 * 
	 */
	private static Boolean isBehaviourCreaProtocolloSDI = null;
	public Boolean isBehaviourCreaProtocolloSDI() throws ProtocolException{
		if(SDIProperties.isBehaviourCreaProtocolloSDI==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.behaviour.creaProtocolloSDI";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				if (value != null){
					value = value.trim();
					SDIProperties.isBehaviourCreaProtocolloSDI = Boolean.parseBoolean(value);
				}else{
					throw new Exception("Non definita");
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.isBehaviourCreaProtocolloSDI;
	}
	
	
	// Utile per sonde applicative
	
	private static List<String> soggettiWhiteList = null;
	public List<String> getSoggettiWhiteList() throws ProtocolException{
		if(SDIProperties.soggettiWhiteList==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.whiteList.soggetti";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				SDIProperties.soggettiWhiteList = new ArrayList<String>();
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						SDIProperties.soggettiWhiteList.add(tmp[i]);
					}
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.soggettiWhiteList;
	}
	
	private static List<String> serviziWhiteList = null;
	public List<String> getServiziWhiteList() throws ProtocolException{
		if(SDIProperties.serviziWhiteList==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.whiteList.servizi";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				SDIProperties.serviziWhiteList = new ArrayList<String>();
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						SDIProperties.serviziWhiteList.add(tmp[i]);
					}
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.serviziWhiteList;
	}
	
	private static List<String> azioniWhiteList = null;
	public List<String> getAzioniWhiteList() throws ProtocolException{
		if(SDIProperties.azioniWhiteList==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.whiteList.azioni";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				SDIProperties.azioniWhiteList = new ArrayList<String>();
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						SDIProperties.azioniWhiteList.add(tmp[i]);
					}
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.azioniWhiteList;
	}
	
	private static List<String> namespaceWhiteList = null;
	public List<String> getNamespaceWhiteList() throws ProtocolException{
		if(SDIProperties.namespaceWhiteList==null){
			
			String propertyName = "org.openspcoop2.protocol.sdi.whiteList.namespace";
			
			try{  
				String value = this.reader.getValue_convertEnvProperties(propertyName); 

				SDIProperties.namespaceWhiteList = new ArrayList<String>();
				
				if (value != null){
					value = value.trim();
					String [] tmp = value.split(",");
					for (int i = 0; i < tmp.length; i++) {
						SDIProperties.namespaceWhiteList.add(tmp[i]);
					}
				}

			}catch(java.lang.Exception e) {
				String msg = "Riscontrato errore durante la lettura della proprieta' '"+propertyName+"': "+e.getMessage();
				this.log.error(msg,e);
				throw new ProtocolException(msg,e);
			}
		}

		return SDIProperties.namespaceWhiteList;
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
    	if(SDIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.pa.bustaErrore.personalizzaElementiFault"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					SDIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				}
				
				SDIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.pa.bustaErrore.personalizzaElementiFault' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				SDIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault = null;
				
				SDIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFaultRead = true;
			}
    	}
    	
    	return SDIProperties.isPortaApplicativaBustaErrore_personalizzaElementiFault;
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
    	if(SDIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.pa.bustaErrore.aggiungiErroreApplicativo"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					SDIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				}
				
				SDIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.pa.bustaErrore.aggiungiErroreApplicativo' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				SDIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo = null;
				
				SDIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativoRead = true;
			}
    	}
    	
    	return SDIProperties.isPortaApplicativaBustaErrore_aggiungiErroreApplicativo;
	}
	
    /**
     * Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore)
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolValidazione = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolValidazione(){
    	if(SDIProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneIntestazione"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false");
					SDIProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneIntestazione' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SDIProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione = false;
			}
    	}
    	
    	return SDIProperties.isGenerazioneDetailsSOAPFaultProtocolValidazione;
	}
    
    /**
     * Indicazione se generare i details in caso di SOAPFault *_300
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_300
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolProcessamento(){
    	if(SDIProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneProcessamento"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true");
					SDIProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.eccezioneProcessamento' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				SDIProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento = true;
			}
    	}
    	
    	return SDIProperties.isGenerazioneDetailsSOAPFaultProtocolProcessamento;
	}
    
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault *_300 lo stack trace
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolWithStackTrace(){
    	if(SDIProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false");
					SDIProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SDIProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace = false;
			}
    	}
    	
    	return SDIProperties.isGenerazioneDetailsSOAPFaultProtocolWithStackTrace;
	}
    
    /**
     * Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     *   
     * @return Indicazione se generare nei details in caso di SOAPFault informazioni generiche
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = null;
    public boolean isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche(){
    	if(SDIProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true");
					SDIProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.protocol.informazioniGeneriche' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				SDIProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche = true;
			}
    	}
    	
    	return SDIProperties.isGenerazioneDetailsSOAPFaultProtocolConInformazioniGeneriche;
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
    	if(SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.serverError"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true");
					SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.serverError' non impostata, viene utilizzato il default=true, errore:"+e.getMessage());
				SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError = true;
			}
    	}
    	
    	return SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationServerError;
	}
    
    /**
     * Indicazione se generare i details in Casi di errore 4XX
     *   
     * @return Indicazione se generare i details in Casi di errore 4XX
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationClientError = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationClientError(){
    	if(SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.clientError"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false");
					SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.clientError' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError = false;
			}
    	}
    	
    	return SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationClientError;
	}
    
    /**
     * Indicazione se generare nei details lo stack trace all'interno
     *   
     * @return Indicazione se generare nei details lo stack trace all'interno
     * 
     */
	private static Boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = null;
    public boolean isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace(){
    	if(SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.stackTrace"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = Boolean.parseBoolean(value);
				}else{
					this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false");
					SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
				}
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.stackTrace' non impostata, viene utilizzato il default=false, errore:"+e.getMessage());
				SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace = false;
			}
    	}
    	
    	return SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationWithStackTrace;
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
    	if(SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.informazioniGeneriche"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode)");
					SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				}
				
				SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.generazioneDetailsSoapFault.integration.informazioniGeneriche' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultAsGenericCode), errore:"+e.getMessage());
				SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche = null;
				
				SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGenericheRead = true;
			}
    	}
    	
    	return SDIProperties.isGenerazioneDetailsSOAPFaultIntegrationConInformazioniGeneriche;
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
    	if(SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.erroreApplicativo.faultApplicativo.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails)");
					SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				}
				
				SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.erroreApplicativo.faultApplicativo.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultApplicativo.enrichDetails), errore:"+e.getMessage());
				SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo = null;
				
				SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativoRead = true;
			}
    	}
    	
    	return SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultApplicativo;
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
    	if(SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead==null){
	    	try{  
				String value = this.reader.getValue_convertEnvProperties("org.openspcoop2.protocol.sdi.erroreApplicativo.faultPdD.enrichDetails"); 
				
				if (value != null){
					value = value.trim();
					SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = Boolean.parseBoolean(value);
				}else{
					this.log.debug("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails)");
					SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				}
				
				SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
				
			}catch(java.lang.Exception e) {
				this.log.warn("Proprieta' di openspcoop 'org.openspcoop2.protocol.sdi.erroreApplicativo.faultPdD.enrichDetails' non impostata, viene utilizzato il default associato al Servizio Applicativo (faultPdD.enrichDetails), errore:"+e.getMessage());
				SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD = null;
				
				SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdDRead = true;
			}
    	}
    	
    	return SDIProperties.isAggiungiDetailErroreApplicativo_SoapFaultPdD;
	}
}
