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

package org.openspcoop2.protocol.sdi.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;

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
}
