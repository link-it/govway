/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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



package org.openspcoop2.pdd.config;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.xml.soap.SOAPEnvelope;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.InvocazionePortaGestioneErrore;
import org.openspcoop2.core.config.InvocazioneServizio;
import org.openspcoop2.core.config.MessageSecurity;
import org.openspcoop2.core.config.MessageSecurityFlow;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.MtomProcessor;
import org.openspcoop2.core.config.MtomProcessorFlow;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.driver.BeanUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.ValidazioneSemantica;
import org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.DynamicNamespaceContextFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.mtom.MtomXomPackageInfo;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.GestoreErroreConnettore;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.mapping.ModalitaIdentificazione;
import org.openspcoop2.protocol.engine.mapping.OperationFinder;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;


/**
 * Classe utilizzata per ottenere informazioni che interessano ad OpenSPCoop
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazionePdDReader {

	/* istanza di ConfigurazioneReader */
	private static  ConfigurazionePdDReader configurazionePdDReader;
	/* informazione sull'inizializzazione dell'istanza */
	private static boolean initialize = false;

	/* Configurazione della PdD */
	private ConfigurazionePdD configurazionePdD;
	/** ConfigurazioneDinamica */
	private boolean configurazioneDinamica = false;

	/** Registro dei Servizi Reader per il routing */
//	private RegistroServiziReader registroServiziReader;

	/** Logger utilizzato per debug. */
	private Logger log = null;
	
	/** OpenSPCoopProperties */
	private OpenSPCoop2Properties openspcoopProperties = null;
	private PddProperties pddProperties = null;
	
	/** Server J2EE */
	private boolean serverJ2EE;



	/* --------------- Cache --------------------*/
	public static boolean isCacheAbilitata() throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				return configurazionePdDReader.configurazionePdD.isCacheAbilitata();
			}
			return false;
		}catch(Exception e){
			throw new DriverConfigurazioneException("IsCacheAbilitata, recupero informazione della cache della Configurazione della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}
	public static void resetCache() throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				configurazionePdDReader.configurazionePdD.resetCache();
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Reset della cache della Configurazione della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}
	public static String printStatsCache(String separator) throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				return configurazionePdDReader.configurazionePdD.printStatsCache(separator);
			}
			else{
				throw new Exception("ConfigurazionePdD Non disponibile");
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Visualizzazione Statistiche riguardante la cache della Configurazione della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				configurazionePdDReader.configurazionePdD.abilitaCache();
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Abilitazione cache della Configurazione della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond) throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				configurazionePdDReader.configurazionePdD.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond);
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Abilitazione cache della Configurazione della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}
	public static void disabilitaCache() throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				configurazionePdDReader.configurazionePdD.disabilitaCache();
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Disabilitazione cache della Configurazione della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}
	public static String listKeysCache(String separator) throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				return configurazionePdDReader.configurazionePdD.listKeysCache(separator);
			}
			else{
				throw new Exception("ConfigurazionePdD Non disponibile");
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Visualizzazione chiavi presenti nella cache della Configurazione della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static String getObjectCache(String key) throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				return configurazionePdDReader.configurazionePdD.getObjectCache(key);
			}
			else{
				throw new Exception("ConfigurazionePdD Non disponibile");
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Visualizzazione oggetto presente nella cache della Configurazione della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}
	
	public static void removeObjectCache(String key) throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				configurazionePdDReader.configurazionePdD.removeObjectCache(key);
			}
			else{
				throw new Exception("ConfigurazionePdD Non disponibile");
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Rimozione oggetto presente nella cache della Configurazione della Porta di Dominio non riuscita: "+e.getMessage(),e);
		}
	}


	/*   -------------- Metodi di inizializzazione -----------------  */
	/**
	 * Si occupa di inizializzare l'engine che permette di effettuare
	 * query alla configurazione di OpenSPCoop.
	 * L'engine inizializzato sara' diverso a seconda del <var>tipo</var> di configurazione :
	 * <ul>
	 * <li> {@link org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML}, interroga una configurazione realizzata tramite un file xml.
	 * <li> {@link org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB}, interroga una configurazione realizzata tramite un database relazionale.
	 * </ul>
	 *
	 * @param accessoConfigurazione Informazioni per accedere alla configurazione della PdD OpenSPCoop.
	 * @return true se l'inizializzazione ha successo, false altrimenti.
	 */
	public static boolean initialize(AccessoConfigurazionePdD accessoConfigurazione,Logger aLog,Logger aLogconsole,Properties localProperties, 
			String jndiNameDatasourcePdD, boolean forceDisableCache,
			boolean useOp2UtilsDatasource, boolean bindJMX){

		try {
			ConfigurazionePdDReader.configurazionePdDReader = new ConfigurazionePdDReader(accessoConfigurazione,aLog,aLogconsole,localProperties,jndiNameDatasourcePdD, 
					forceDisableCache, useOp2UtilsDatasource, bindJMX);	
			return ConfigurazionePdDReader.initialize;
		}
		catch(Exception e) {
			aLog.error(e.getMessage(),e);
			aLogconsole.error(e.getMessage());
			return false;
		}
	}

	/**
	 * Ritorna lo stato dell'engine che permette di effettuare
	 * query alla configurazione
	 *
	 * @return true se l'inizializzazione all'engine e' stata precedentemente effettuata, false altrimenti.
	 */
	public static boolean isInitialize(){
		return ConfigurazionePdDReader.initialize;
	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza del Reader della Configurazione
	 */
	public static ConfigurazionePdDReader getInstance(){
		return ConfigurazionePdDReader.configurazionePdDReader;
	}












	/*   -------------- Costruttore -----------------  */ 

	/**
	 * Inizializza il reader
	 *
	 * @param accessoConfigurazione Informazioni per accedere alla configurazione della PdD OpenSPCoop.
	 */
	public ConfigurazionePdDReader(AccessoConfigurazionePdD accessoConfigurazione,Logger aLog,Logger aLogconsole,Properties localProperties, 
			String jndiNameDatasourcePdD, boolean forceDisableCache,
			boolean useOp2UtilsDatasource, boolean bindJMX)throws DriverConfigurazioneException{
		try{
			if(aLog!=null)
				this.log = aLog;
			else
				this.log = LoggerWrapperFactory.getLogger(ConfigurazionePdDReader.class);
			this.configurazionePdD = new ConfigurazionePdD(accessoConfigurazione,this.log,aLogconsole,localProperties,jndiNameDatasourcePdD, forceDisableCache,
					useOp2UtilsDatasource, bindJMX);

			// OpenSPCoop Properties
			this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
			this.pddProperties = PddProperties.getInstance();
			
			// configurazioneDinamica
			this.configurazioneDinamica = this.openspcoopProperties.isConfigurazioneDinamica();
			
			// Server J2EE
			this.serverJ2EE = this.openspcoopProperties.isServerJ2EE();
			
			ConfigurazionePdDReader.initialize = true;
		}catch(Exception e){
			if(this.log!=null)
				this.log.error("Configurazione non inizializzata",e);
			else
				aLogconsole.error("Configurazione non inizializzata:"+e.getMessage(),e);
			ConfigurazionePdDReader.initialize = false;
		}
	}



	/**
	 * Inixializza il reader del registro servizi, per il routing
	 * 
	 * @throws DriverConfigurazioneException
	 */
//	public void initializeRegistroServiziReader()throws DriverConfigurazioneException{
//		//	 RegistroServizi
//		if(RegistroServiziReader.isInitialize()==false){
//			throw new DriverConfigurazioneException("Registro dei Servizi non risulta inizializzato");
//		}
//		this.registroServiziReader = RegistroServiziReader.getInstance();
//	}








	/**
	 * Metodo che verica la connessione ad una risorsa.
	 * Se la connessione non e' presente, viene lanciata una eccezione che contiene il motivo della mancata connessione
	 * 
	 * @throws CoreException eccezione che contiene il motivo della mancata connessione
	 */
	protected void isAlive() throws CoreException{
		((IMonitoraggioRisorsa)this.configurazionePdD.getDriverConfigurazionePdD()).isAlive();
	}
	
	
	/**
	 * Validazione semantica dei registri servizi
	 * 
	 * @throws CoreException eccezione che contiene il motivo della validazione semantica errata
	 */
	protected void validazioneSemantica(String[] tipiConnettori,String[] tipiMsgDiagnosticoAppender,String[] tipiTracciamentoAppender,
			String [] tipiAutenticazione, String [] tipiAutorizzazione,
			String [] tipiAutorizzazioneContenuto,String [] tipiAutorizzazioneContenutoBuste,
			String [] tipiIntegrazionePD, String [] tipiIntegrazionePA,
			boolean validazioneSemanticaAbilitataXML,boolean validazioneSemanticaAbilitataAltreConfigurazioni,boolean validaConfigurazione,
			Logger logConsole) throws CoreException{
		try{
			Object o = this.configurazionePdD.getDriverConfigurazionePdD();
			boolean validazione = false;
			if(o instanceof DriverConfigurazioneXML){
				validazione = validazioneSemanticaAbilitataXML;
			}else{
				validazione = validazioneSemanticaAbilitataAltreConfigurazioni;
			}
			if(validazione){
				BeanUtilities driverConfigurazione = (BeanUtilities) o;
				org.openspcoop2.core.config.Openspcoop2 configurazionePdD = driverConfigurazione.getImmagineCompletaConfigurazionePdD();
				
				ValidazioneSemantica validazioneSemantica = new ValidazioneSemantica(configurazionePdD,
						tipiConnettori,ProtocolFactoryManager.getInstance().getSubjectTypesAsArray(),
						ProtocolFactoryManager.getInstance().getServiceTypesAsArray(),tipiMsgDiagnosticoAppender,tipiTracciamentoAppender,
						tipiAutenticazione,tipiAutorizzazione,
						tipiAutorizzazioneContenuto,tipiAutorizzazioneContenutoBuste,
						tipiIntegrazionePD,tipiIntegrazionePA,validaConfigurazione);
				validazioneSemantica.validazioneSemantica(false);
				if(logConsole!=null){
					logConsole.info("Validazione semantica della configurazione della Porta di Dominio effettuata.");
				}
			}
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	protected void setValidazioneSemanticaModificaConfigurazionePdDXML(String[] tipiConnettori,
			String[]tipoMsgDiagnosticiAppender,String[]tipoTracciamentoAppender,
			String[]tipoAutenticazione,String[]tipoAutorizzazione,
			String[]tipiAutorizzazioneContenuto,String [] tipiAutorizzazioneContenutoBuste,
			String[]tipoIntegrazionePD,String[]tipoIntegrazionePA) throws CoreException{
		try{
			Object o = this.configurazionePdD.getDriverConfigurazionePdD();
			if(o instanceof DriverConfigurazioneXML){
				DriverConfigurazioneXML driver = (DriverConfigurazioneXML) o;
				driver.abilitazioneValidazioneSemanticaDuranteModificaXML(tipiConnettori, 
						ProtocolFactoryManager.getInstance().getSubjectTypesAsArray(),
						ProtocolFactoryManager.getInstance().getServiceTypesAsArray(),
						tipoMsgDiagnosticiAppender, tipoTracciamentoAppender, tipoAutenticazione, tipoAutorizzazione, 
						tipiAutorizzazioneContenuto,tipiAutorizzazioneContenutoBuste,
						tipoIntegrazionePD, tipoIntegrazionePA);
			}
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}
	
	protected void verificaConsistenzaConfigurazione() throws DriverConfigurazioneException {
		Object o = this.configurazionePdD.getDriverConfigurazionePdD();
		if(o instanceof DriverConfigurazioneXML){
			DriverConfigurazioneXML driver = (DriverConfigurazioneXML) o;
			driver.refreshConfigurazioneXML();
		}
	}
	
	
	
	








	/* ********  SOGGETTI (Interfaccia)  ******** */

	/**
	 * Restituisce Il soggetto che include la porta delegata identificata da <var>idPD</var>
	 *
	 * @param location Location che identifica una porta delegata
	 * @return Il Soggetto che include la porta delegata fornita come parametro.
	 * 
	 */
	protected IDSoggetto getIDSoggetto(Connection connectionPdD, String location,IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		Soggetto soggetto = this.configurazionePdD.getSoggetto(connectionPdD,location);
		if(soggetto==null)
			throw new DriverConfigurazioneNotFound("Soggetto che possiede la porta delegata ["+location+"] non esistente");

		IDSoggetto idSoggetto = new IDSoggetto(soggetto.getTipo(),soggetto.getNome());
		if(soggetto.getIdentificativoPorta() != null){
			idSoggetto.setCodicePorta(soggetto.getIdentificativoPorta());
		}else{
			try{
				idSoggetto.setCodicePorta(protocolFactory.createTraduttore().getIdentificativoPortaDefault(idSoggetto));
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		return idSoggetto;
	}
	/**
	 * Restituisce Il dominio di un soggetto  identificato da <var>idSoggetto</var>
	 *
	 * @param idSoggetto Identificativo di un soggetto
	 * @return Il dominio del Soggetto.
	 * 
	 */
	protected String getIdentificativoPorta(Connection connectionPdD, IDSoggetto idSoggetto,IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		Soggetto soggetto = this.configurazionePdD.getSoggetto(connectionPdD,idSoggetto);
		if(soggetto == null)
			throw new DriverConfigurazioneNotFound("Soggetto["+idSoggetto.toString()+"] per lettura Dominio non trovato");

		if( soggetto.getIdentificativoPorta() != null  )
			return soggetto.getIdentificativoPorta();
		else{
			if(soggetto.getNome() != null){
				try{
					return protocolFactory.createTraduttore().getIdentificativoPortaDefault(idSoggetto);
				}catch(Exception e){
					throw new DriverConfigurazioneException(e.getMessage(),e);
				}
			}else{
				OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
				return properties.getIdentificativoPortaDefault(protocolFactory.getProtocol());
			}
		}
	}

	/**
	 * Restituisce L'indicazione se il soggetto e' un soggetto virtuale
	 *
	 * @param idSoggetto Identificativo di un soggetto 
	 * @return true se il soggetto e' un soggetto virtuale gestito dalla porta di dominio.
	 * 
	 */
	protected boolean isSoggettoVirtuale(Connection connectionPdD, IDSoggetto idSoggetto) throws DriverConfigurazioneException { 

		// il soggetto virtuale e' stato registrato come tale?
		
		if(idSoggetto == null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null)
			return false;

		/** Lista di Soggetti Virtuali */
		HashSet<String> listaSoggettiVirtuali = null;
		try{
			listaSoggettiVirtuali = this.configurazionePdD.getSoggettiVirtuali(connectionPdD);
		}catch(DriverConfigurazioneNotFound de){
			this.log.info("Soggetti virtuali non presenti.");
			return false;
		}
		
		String keySoggetto = idSoggetto.getTipo() + idSoggetto.getNome();

		return listaSoggettiVirtuali.contains(keySoggetto);
	}

	/**
	 * Restituisce l'indicazione se la PdD gestisce il soggetto indicato
	 *
	 * @param idSoggetto Identificativo di un soggetto 
	 * @return true se il soggetto e' un soggetto gestito dalla porta di dominio.
	 * 
	 */
	protected boolean existsSoggetto(Connection connectionPdD, IDSoggetto idSoggetto)throws DriverConfigurazioneException{  
		Soggetto soggetto = null;
		try{
			soggetto = this.configurazionePdD.getSoggetto(connectionPdD, idSoggetto);
		}catch(DriverConfigurazioneNotFound e){
			//this.log.debug("existsSoggetto (not found): "+e.getMessage());
			return false;
		}
		return soggetto!=null;
	}

	/**
	 * Restituisce la lista dei soggetti virtuali gestiti dalla PdD
	 * 
	 * @return Restituisce la lista dei servizi associati a soggetti virtuali gestiti dalla PdD
	 * @throws DriverConfigurazioneException
	 */
	protected  HashSet<IDServizio> getServizi_SoggettiVirtuali(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getServizi_SoggettiVirtuali(connectionPdD);
	}










	/* ************* ROUTING **************** */

	/**
	 * Restituisce la rotta per il dato soggetto seguendo queste regole:
	 * <ul>
	 * </li>1) Se la PdD non contiene una entry per il soggetto indicato, 
	 * viene acceduto normalmente il registro per ottenere il connettore del destinatario reale della busta.
	 * </li>2) Se la PdD contiene una entry specifica (destinazione) per il soggetto destinatario
	 *         vengono eseguite le rotte elencate nella entry fino a che non viene trovato un connettore.
	 *         Nel caso non venga trovato un connettore si rimanda al punto 3.
	 * </li> 3) Viene utilizzata l'entry di default (obbligatorio) per trovare un connettore.
	 *          Vengono eseguite le rotte elencate nella entry fino a che non viene trovato un connettore
	 * </ul>
	 *
	 * @param idSoggettoDestinatario Identificativo del soggetto destinatario della busta 
	 * @return Il connettore da utilizzare per la spedizione della busta.
	 * 
	 */
	protected Connettore getForwardRoute(Connection connectionPdD, RegistroServiziManager registroServiziManager ,IDSoggetto idSoggettoDestinatario,boolean functionAsRouter) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getForwardRoute(connectionPdD,registroServiziManager,null,new IDServizio(idSoggettoDestinatario),functionAsRouter);
	}

	/**
	 * Restituisce la rotta per il dato soggetto seguendo queste regole:
	 * <ul>
	 * </li>1) Se la PdD non contiene una entry per il soggetto indicato, 
	 * viene acceduto normalmente il registro per ottenere il connettore del destinatario reale della busta.
	 * </li>2) Se la PdD contiene una entry specifica (destinazione) per il soggetto destinatario
	 *         vengono eseguite le rotte elencate nella entry fino a che non viene trovato un connettore.
	 *         Nel caso non venga trovato un connettore si rimanda al punto 3.
	 * </li> 3) Viene utilizzata l'entry di default (obbligatorio) per trovare un connettore.
	 *          Vengono eseguite le rotte elencate nella entry fino a che non viene trovato un connettore
	 * </ul>
	 *
	 * @param idSoggettoMittente Identificativo del mittente della busta
	 * @param idServizio Identificativo del servizio richiesto (soggetto destinatario, servizio e azione della busta) 
	 * @return Il connettore da utilizzare per la spedizione della busta.
	 * 
	 */
	protected Connettore getForwardRoute(Connection connectionPdD, RegistroServiziManager registroServiziManager , IDSoggetto idSoggettoMittente, IDServizio idServizio,boolean functionAsRouter) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{


		if(idServizio == null)
			throw new DriverConfigurazioneException("getForwardRoute error: Identificativo servizio non definito");
		if(idServizio.getSoggettoErogatore()==null)
			throw new DriverConfigurazioneException("getForwardRoute error: Soggetto Erogatore non definito");
		String nome = idServizio.getSoggettoErogatore().getNome();
		String tipo= idServizio.getSoggettoErogatore().getTipo();
		if(nome==null || tipo==null)
			throw new DriverConfigurazioneException("getForwardRoute error: Soggetto Erogatore con tipo/nome non definito");

		// 1) Se la PdD non contiene una forward route, utilizza normalmente il registro
		RoutingTable routingTable = this.configurazionePdD.getRoutingTable(connectionPdD);
		/*if(routingTable.isAbilitata()==false){
			System.out.println("ROUTING TABLE DISABILITATA");
		}
		else{
			Route[] routesDefault = routingTable.get_defaultList();
			if(routesDefault!=null){
				for (int i = 0; i < routesDefault.length; i++) {
					if(routesDefault[i].getGateway()!=null){
						System.out.println("ROUTING TABLE DEFAULT ["+i+"] GATEWAY: "+routesDefault[i].getGateway().getTipo()+"/"+routesDefault[i].getGateway().getNome());
					}else{
						System.out.println("ROUTING TABLE DEFAULT ["+i+"] REGISTRO: "+routesDefault[i].getRegistro().getNome());
					}
				}
			}
			RoutingTableDestinazione [] rdt = routingTable.getDestinazioneList();
			if(rdt!=null){
				for (int i = 0; i < rdt.length; i++) {
					if(rdt[i].getRoute(0).getGateway()!=null){
						System.out.println("ROUTING TABLE ("+rdt[i].getTipo()+"/"+rdt[i].getNome()+") ["+i+"] GATEWAY: "+rdt[i].getRoute(0).getGateway().getTipo()+"/"+rdt[i].getRoute(0).getGateway().getNome());
					}else{
						System.out.println("ROUTING TABLE ("+rdt[i].getTipo()+"/"+rdt[i].getNome()+") ["+i+"] REGISTRO: "+rdt[i].getRoute(0).getRegistro().getNome());
					}
				}
			}
		}	*/
		if( (routingTable.getAbilitata()==null || routingTable.getAbilitata()==false) 
				|| 
			( 
				( (routingTable.getDefault()==null) || (routingTable.getDefault().sizeRouteList()==0) ) 
					&& 
				(routingTable.sizeDestinazioneList()==0) 
			) 
		){

			if(routingTable.getAbilitata()==null || routingTable.getAbilitata()==false)
				this.log.debug("getForwardRoute: routing table disabilitata");
			else
				this.log.debug("getForwardRoute: routing table senza rotte");

			Connettore connettoreDominio = null;
			try{
				if(idSoggettoMittente!=null && idServizio.getServizio()!=null){
					connettoreDominio = registroServiziManager.getConnettore(idSoggettoMittente,idServizio,null); // null=allRegistri
					if(!functionAsRouter)
						setPDUrlPrefixRewriter(connectionPdD,connettoreDominio, idSoggettoMittente);
				}else{
					connettoreDominio = registroServiziManager.getConnettore(idServizio.getSoggettoErogatore(),null); // null=allRegistri
				}
			}catch(DriverRegistroServiziNotFound e){
				throw new DriverConfigurazioneNotFound("getForwardRoute[RoutingTable Non Abilitata], ricerca nel RegistroServizi effettuata: "+e.getMessage(),e);
			}
			catch(Exception e){
				throw new DriverConfigurazioneException("getForwardRoute[RoutingTable Non Abilitata], errore durante la lettura dal Registro dei Servizi: "+e.getMessage(),e);
			}
			if(connettoreDominio!=null && !CostantiConfigurazione.NONE.equals(connettoreDominio.getTipo()) 
					&& !CostantiConfigurazione.DISABILITATO.equals(connettoreDominio.getTipo()) )
				return connettoreDominio;
			else
				throw new DriverConfigurazioneException("getForwardRoute[RoutingTable Non Abilitata], connettore per la busta non trovato.");
		}

		StringBuffer bf = new StringBuffer();
		bf.append("Ricerca connettore del servizio...");
		this.log.debug("getForwardRoute: routing table abilitata");

		// Se la PdD contiene una forward route, utilizza la tabella di routing	
		// 2) Destinazioni specifiche
		for(int i=0;i<routingTable.sizeDestinazioneList();i++){
			if(nome.equals(routingTable.getDestinazione(i).getNome()) &&
					tipo.equals(routingTable.getDestinazione(i).getTipo())){	
				bf.append("\nRotta di destinazione ["+routingTable.getDestinazione(i).getTipo()+"/"+routingTable.getDestinazione(i).getNome()+"]...\n");
				this.log.debug("getForwardRoute: esamino routing table, destinazione ["+routingTable.getDestinazione(i).getTipo()+"/"+routingTable.getDestinazione(i).getNome()+"]");

				// Utilizzo le rotte della destinazione specifica fino a trovare un connettore
				// Se un connettore non viene trovato, utilizzero poi il Default della tabella di routing.
				RoutingTableDestinazione routingTableDest = routingTable.getDestinazione(i);
				for(int j=0;j<routingTableDest.sizeRouteList();j++){
					Route route = routingTableDest.getRoute(j);
					Connettore connettoreDominio = null;
					boolean error = false;
					String soggettoGateway = "";
					try{
						if(route.getRegistro()!=null){ 
							this.log.debug("getForwardRoute: esamino routing table, destinazione ["
									+routingTable.getDestinazione(i).getTipo()+"/"+routingTable.getDestinazione(i).getNome()+"] RegistroNome["+route.getRegistro().getNome()+"]");

							bf.append("\tRegistro nomeRegistro["+route.getRegistro().getNome()+"]: ");

							// Utilizzo del registro con l'identita reale della busta
							if(idSoggettoMittente!=null && idServizio.getServizio()!=null){
								connettoreDominio = registroServiziManager.getConnettore(idSoggettoMittente,idServizio,route.getRegistro().getNome());
								if(!functionAsRouter)
									setPDUrlPrefixRewriter(connectionPdD,connettoreDominio, idSoggettoMittente);
							}else{
								connettoreDominio = registroServiziManager.getConnettore(idServizio.getSoggettoErogatore(),route.getRegistro().getNome());
							}
							// Registro da utilizzare anche per l'imbustamento
							if(connettoreDominio!=null && !CostantiConfigurazione.NONE.equals(connettoreDominio.getTipo()) &&
									!CostantiConfigurazione.DISABILITATO.equals(connettoreDominio.getTipo()) )
								connettoreDominio.setNomeRegistro(route.getRegistro().getNome());
						}else if(route.getGateway()!=null){ 
							this.log.debug("getForwardRoute: esamino routing table, destinazione ["
									+routingTable.getDestinazione(i).getTipo()+"/"+routingTable.getDestinazione(i).getNome()+"] GateWay["+route.getGateway().getTipo()+"/"+route.getGateway().getNome()+"]"); 
							bf.append("\tGateWay["+route.getGateway().getTipo()+"/"+route.getGateway().getNome()+"]: ");

							// Utilizzo del gateway
							IDSoggetto gateway = new IDSoggetto(route.getGateway().getTipo(),
									route.getGateway().getNome());
							soggettoGateway = " [Gateway:"+gateway.toString()+"]";
							connettoreDominio = registroServiziManager.getConnettore(gateway,null); //null=allRegistri
							// Rotta diversa dalla destinazione della busta per l'elemento trasmissione
							if(connettoreDominio!=null && !CostantiConfigurazione.NONE.equals(connettoreDominio.getTipo()) &&
									!CostantiConfigurazione.DISABILITATO.equals(connettoreDominio.getTipo())){
								connettoreDominio.setNomeDestinatarioTrasmissioneBusta(gateway.getNome());
								connettoreDominio.setTipoDestinatarioTrasmissioneBusta(gateway.getTipo());
							}
						}
					}catch(DriverRegistroServiziNotFound e){
						// Ricerca Connettore fallita
						error = true;
						bf.append(" non trovata: "+e.getMessage());
					}
					catch(Exception e){
						throw new DriverConfigurazioneException("getForwardRoute: esamino routing table, destinazione ["
								+routingTable.getDestinazione(i).getTipo()+"/"+routingTable.getDestinazione(i).getNome()+"]: Accesso al registro non riuscito"+soggettoGateway+": "+e.getMessage(),e);
					}
					if(connettoreDominio!=null && !CostantiConfigurazione.NONE.equals(connettoreDominio.getTipo()) 
							&& !CostantiConfigurazione.DISABILITATO.equals(connettoreDominio.getTipo()))
						return connettoreDominio;
					else{
						if(!error){
							bf.append(" non trovata: connettore non definito");
						}
					}
				}
			}
		}

		// 3) Default (obbligatorio)	
		// Utilizzo le rotte del default fino a trovare un connettore
		// Se un connettore non viene trovato, significa che e' stata effettuata una scorretta installazione
		if(routingTable.getDefault()!=null){
			for(int i=0;i<routingTable.getDefault().sizeRouteList();i++){
				this.log.debug("getForwardRoute: esamino routing table, rotta di default");
				bf.append("\nRotta di default");
				Route route = routingTable.getDefault().getRoute(i);
				Connettore connettoreDominio = null;
				boolean error = false;
				String soggettoGateway = "";
				try{
					if(route.getRegistro()!=null){ 
						this.log.debug("getForwardRoute: esamino routing table, rotta di default, Registro nomeRegistro["+route.getRegistro().getNome()+"]");
						bf.append(" Registro nomeRegistro["+route.getRegistro().getNome()+"]: ");
	
						// Utilizzo del registro con l'identita reale della busta
						if(idSoggettoMittente!=null && idServizio.getServizio()!=null){
							connettoreDominio = registroServiziManager.getConnettore(idSoggettoMittente,idServizio,route.getRegistro().getNome());
							if(!functionAsRouter)
								setPDUrlPrefixRewriter(connectionPdD,connettoreDominio, idSoggettoMittente);
						}else{
							connettoreDominio = registroServiziManager.getConnettore(idServizio.getSoggettoErogatore(),route.getRegistro().getNome());
						}
						// Registro da utilizzare anche per l'imbustamento
						if(connettoreDominio!=null && !CostantiConfigurazione.NONE.equals(connettoreDominio.getTipo()) && 
								!CostantiConfigurazione.DISABILITATO.equals(connettoreDominio.getTipo()))
							connettoreDominio.setNomeRegistro(route.getRegistro().getNome());
					}else if(route.getGateway()!=null){ 
						this.log.debug("getForwardRoute: esamino routing table, rotta di default, GateWay["+route.getGateway().getTipo()+"/"+route.getGateway().getNome()+"]");
						bf.append(" GateWay["+route.getGateway().getTipo()+"/"+route.getGateway().getNome()+"]: ");
	
						// Utilizzo del gateway
						IDSoggetto gateway = new IDSoggetto(route.getGateway().getTipo(),
								route.getGateway().getNome());
						soggettoGateway = " [Gateway:"+gateway.toString()+"]";
						connettoreDominio = registroServiziManager.getConnettore(gateway,null);//null=allRegistri
						// Rotta diversa dalla destinazione della busta per l'elemento trasmissione
						if(connettoreDominio!=null && !CostantiConfigurazione.NONE.equals(connettoreDominio.getTipo()) &&
								!CostantiConfigurazione.DISABILITATO.equals(connettoreDominio.getTipo())){
							connettoreDominio.setNomeDestinatarioTrasmissioneBusta(gateway.getNome());
							connettoreDominio.setTipoDestinatarioTrasmissioneBusta(gateway.getTipo());
						}
					}
				}catch(DriverRegistroServiziNotFound e){
					// Ricerca Connettore fallita
					error = true;
					bf.append(" non trovata: "+e.getMessage());
				}
				catch(Exception e){
					throw new DriverConfigurazioneException("getForwardRoute: esamino routing table, rotta di default: Accesso al registro non riuscito"+soggettoGateway+": "+e.getMessage(),e);
				}
				if(connettoreDominio!=null && !CostantiConfigurazione.NONE.equals(connettoreDominio.getTipo())
						&& !CostantiConfigurazione.DISABILITATO.equals(connettoreDominio.getTipo()))
					return connettoreDominio;
				else{
					if(!error){
						bf.append(" non trovata: connettore non definito");
					}
				}
			}
		}

		// connettore non trovato?
		throw new DriverConfigurazioneNotFound("getForwardRoute [Routing Table] error: connettore per la busta non trovato: \n"+bf.toString());
	}

	/**
	 * Restituisce il nome di un registro dei servizi da utilizzare per effettuare il processo di imbustamento.
	 * Viene restituito null se e' possibile utilizzare qualsiasi registro, in ordine di definizione.
	 *
	 * @param idSoggettoMittente Identificativo del mittente della busta
	 * @param idServizio Identificativo del servizio richiesto (soggetto destinatario, servizio e azione della busta) 
	 * @return Il nome di un registro dei servizi da utilizzare per effettuare il processo di imbustamento.
	 * 
	 */
	protected String getRegistroForImbustamento(Connection connectionPdD, RegistroServiziManager registroServiziManager , IDSoggetto idSoggettoMittente, IDServizio idServizio,boolean functionAsRouter)throws DriverConfigurazioneException{
		Connettore conn = null;
		try {
			conn = getForwardRoute(connectionPdD,registroServiziManager,idSoggettoMittente,idServizio,functionAsRouter);
		}catch(DriverConfigurazioneNotFound e){}
		if(conn!=null)
			return conn.getNomeRegistro();
		else
			return null;
	}

	/**
	 * Restituisce l'indicazione se la PdD e' stata configurata per funzionare anche come router.
	 *
	 * @return true se la PdD deve funzionare anche come router.
	 * 
	 */
	protected boolean routerFunctionActive(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Per essere configurata come Router, una PdD deve possedere una tabella di routing:
		RoutingTable routingTable = null;
		try{
			routingTable = this.configurazionePdD.getRoutingTable(connectionPdD);
		}catch(Exception e){
			this.log.debug("routerFunctionActive[getRoutingTable]",e);
		}
		if(routingTable == null || (routingTable.getAbilitata()==null || routingTable.getAbilitata()==false) || 
				( 
					(routingTable.getDefault()==null || routingTable.getDefault().sizeRouteList()==0) 
						&& 
					(routingTable.sizeDestinazioneList()==0) 
				) 
		){
			return false;
		}

		// Inoltre deve possedere un soggetto configurato come Router
		Soggetto router = null;
		try{
			router = this.configurazionePdD.getRouter(connectionPdD);
		}catch(DriverConfigurazioneNotFound e){
			return false;
			//this.log.debug("routerFunctionActive[getRouter] (not found): "+e.getMessage());
		}	
		return router!=null;
	}

	/**
	 * Restituisce l'identita della PdD nel caso debba funzionare come router.
	 *
	 * @return idSoggetto Identita della Porta di Dominio 
	 * 
	 */
	protected IDSoggetto getRouterIdentity(Connection connectionPdD,IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Per essere configurata come Router, una PdD deve possedere una tabella di routing:
		RoutingTable routingTable = null;
		try{
			routingTable = this.configurazionePdD.getRoutingTable(connectionPdD);
		}catch(Exception e){
			this.log.debug("getRouterIdentity[routingTable]",e);
		}
		if(routingTable == null || (routingTable.getAbilitata()==null || routingTable.getAbilitata()==false) || 
				( 
					(routingTable.getDefault()==null || routingTable.getDefault().sizeRouteList()==0) 
						&& 
					(routingTable.sizeDestinazioneList()==0) 
				) 
		){
			throw new DriverConfigurazioneException("getRouterIdentity error: RoutingTable non definita");
		}

		// Inoltre deve possedere un soggetto configurato come Router
		Soggetto router = this.configurazionePdD.getRouter(connectionPdD);
		if(router==null)
			throw new DriverConfigurazioneNotFound("Router non trovato");

		String codicePorta = router.getIdentificativoPorta();
		if(codicePorta==null){
			if(router.getNome()!=null)
				try{
					codicePorta = protocolFactory.createTraduttore().getIdentificativoPortaDefault(new IDSoggetto(router.getTipo(), router.getNome()));
				}catch(Exception e){
					throw new DriverConfigurazioneException("getRouterIdentity error: costruzione IdentificativoPorta per soggetto "+router.getTipo()+"/"+router.getNome()+" non riuscita: "+e.getMessage(),e);
				}
			else{
				OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
				codicePorta = properties.getIdentificativoPortaDefault(protocolFactory.getProtocol());
			}
		}
		return new IDSoggetto(router.getTipo(),router.getNome(),codicePorta);
	}








	
	
	
	/* ********  URLPrefixRewriter  ******** */
	
	protected void setPDUrlPrefixRewriter(Connection connectionPdD, org.openspcoop2.core.config.Connettore connettore, IDSoggetto idSoggettoFruitore) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		if(idSoggettoFruitore==null)
			return;
		Soggetto soggettoFruitore = this.configurazionePdD.getSoggetto(connectionPdD,idSoggettoFruitore);
		setUrlPrefixRewriter(soggettoFruitore.getPdUrlPrefixRewriter(),"pdUrlPrefixRewriter", connettore);
	}
	protected void setPAUrlPrefixRewriter(Connection connectionPdD, org.openspcoop2.core.config.Connettore connettore, IDSoggetto idSoggettoErogatore) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		if(idSoggettoErogatore==null)
			return;
		Soggetto soggettoErogatore = this.configurazionePdD.getSoggetto(connectionPdD,idSoggettoErogatore);
		setUrlPrefixRewriter(soggettoErogatore.getPaUrlPrefixRewriter(),"paUrlPrefixRewriter", connettore);
	}
	private void setUrlPrefixRewriter(String urlPrefix,String funzione,org.openspcoop2.core.config.Connettore connettore) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
			
		if( (urlPrefix!=null) && (!"".equals(urlPrefix)) ){
			// search location property
			for (int i = 0; i < connettore.sizePropertyList(); i++) {
				Property cp = connettore.getProperty(i);
				if(cp.getNome().equalsIgnoreCase(CostantiConnettori.CONNETTORE_LOCATION)){
					String originale = cp.getValore();
					cp.setValore(urlPrefixRewriter(funzione,originale, urlPrefix.trim()));
					break;
				}
			}
			
		}
		
	}
	private String urlPrefixRewriter(String funzione,String urlOriginale, String urlPrefix) throws DriverConfigurazioneException{
		
		try{
		
			String urlFinale = urlPrefix;
						
			String originale = urlOriginale;
			if(originale==null){
				throw new DriverRegistroServiziException("["+funzione+"] Url originale non fornita");
			}
			originale = originale.trim();
			
			// Per evitare replace multipli (in caso di risultati cachati)
			if(originale.startsWith(urlPrefix)){
				// replace gia' effettuato
				return originale;
			}
			
			this.log.debug("["+funzione+"]  Originale["+originale+"] UrlPrefix["+urlPrefix+"] ...");
			
			String tmp = null;
			if(originale.contains("://")){
				tmp = originale.substring(originale.indexOf("://")+3);
			}else{
				return urlOriginale; // url prefix effettuato solo se definito un protocollo es. http://
			}
			
			this.log.debug("["+funzione+"]  eliminazioneProtocollo["+tmp+"] ...");
			
			if(tmp.contains("/")){
				tmp = tmp.substring(tmp.indexOf("/")+1);
				this.log.debug("["+funzione+"]  salvataggioContesto["+tmp+"] ...");
				if(urlFinale.endsWith("/")==false){
					urlFinale = urlFinale + "/"; 
				}
				urlFinale = urlFinale + tmp;
			}
			// else sostituisco completamente tutta la url, non avendo un contesto.
			
			this.log.debug("["+funzione+"]  nuova url: ["+urlFinale+"]");
			
			return urlFinale;
			
		}catch(Exception e){
			this.log.error("Processo di ["+funzione+"]  fallito (urlOriginale:"+urlOriginale+") (urlPrefix:"+urlPrefix+")",e);
			throw new DriverConfigurazioneException("Processo di ["+funzione+"]  fallito (urlOriginale:"+urlOriginale+") (urlPrefix:"+urlPrefix+")",e);
		}
	}
	
	
	







	/* ********  PORTE DELEGATE (Interfaccia)  ******** */

	/**
	 * Ritorna la porta delegata
	 * 
	 * @param idPD
	 * @return porta delegata
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	protected PortaDelegata getPortaDelegata(Connection connectionPdD,IDPortaDelegata idPD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPortaDelegata(connectionPdD,idPD);
	}
	
	protected PortaDelegata getPortaDelegata_SafeMethod(Connection connectionPdD,IDPortaDelegata idPD)throws DriverConfigurazioneException{
		try{
			if(idPD.getLocationPD()!=null)
				return this.getPortaDelegata(connectionPdD,idPD);
			else
				return null;
		}catch(DriverConfigurazioneNotFound e){
			return null;
		}
	}
	
	/**
	 * Restituisce true, se alcuni parametri della porta delegata richiedono una identificazione content-based
	 *
	 * @param pd identificatore di una porta delegata
	 * @return true se alcuni parametri della porta delegata richiedono una identificazione content-based
	 * 
	 */
	protected boolean identificazioneContentBased(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}
		if(CostantiConfigurazione.PORTA_DELEGATA_SOGGETTO_EROGATORE_CONTENT_BASED.equals(pd.getSoggettoErogatore().getIdentificazione())){
			return true;
		}else if(CostantiConfigurazione.PORTA_DELEGATA_SERVIZIO_CONTENT_BASED.equals(pd.getServizio().getIdentificazione())){
			return true;
		}else if( pd.getAzione() != null  ){
			if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_CONTENT_BASED.equals(pd.getAzione().getIdentificazione())){
				return true;
			}else{
				return false;
			}
		}else
			return false;
	}

	/**
	 * Restituisce il servizio associata alla porta delegata identificata dai parametri. 
	 *
	 * @param pd identificatore di una porta delegata
	 * @param urlProtocolContext parametri di invocazione della porta delegata.
	 * @param envelope ByteApplicativo utilizzato per l'invocazione della porta delegata
	 * @param headerIntegrazione Header di Integrazione
	 * @return La definizione del servizio
	 * 
	 */
	protected IDServizio getIDServizio(RegistroServiziManager registroServiziManager,PortaDelegata pd,URLProtocolContext urlProtocolContext,
			OpenSPCoop2Message message, SOAPEnvelope envelope,HeaderIntegrazione headerIntegrazione,boolean readFirstHeaderIntegrazione,
			String soapAction,IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound,Exception { 
		
		try{

			if(pd==null){
				throw new DriverConfigurazioneException("Porta Delegata non fornita");
			}
			
			// Calcolo url di invocazione, se esiste un parametro identificato con urlBased
			String urlInvocazionePD = null;
			boolean urlBased = false;
			if( CostantiConfigurazione.PORTA_DELEGATA_SOGGETTO_EROGATORE_URL_BASED.equals(pd.getSoggettoErogatore().getIdentificazione()))
				urlBased = true;
			else if(CostantiConfigurazione.PORTA_DELEGATA_SERVIZIO_URL_BASED.equals(pd.getServizio().getIdentificazione()))
				urlBased = true;
			else if( pd.getAzione() != null ){
				if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_URL_BASED.equals(pd.getAzione().getIdentificazione()))
					urlBased = true;
			}
			if(urlBased){
				urlInvocazionePD = urlProtocolContext.getUrlInvocazione_formBased();
			}

			// Calcolo NamespaceContext, se esiste un parametro identificato con contentBased
			DynamicNamespaceContext dnc = null;
			if( CostantiConfigurazione.PORTA_DELEGATA_SOGGETTO_EROGATORE_CONTENT_BASED.equals(pd.getSoggettoErogatore().getIdentificazione()))
				dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(envelope);
			else if(CostantiConfigurazione.PORTA_DELEGATA_SERVIZIO_CONTENT_BASED.equals(pd.getServizio().getIdentificazione()))
				dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(envelope);
			else if( pd.getAzione() != null ){
				if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_CONTENT_BASED.equals(pd.getAzione().getIdentificazione()))
					dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(envelope);
			}
			AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.XPathExpressionEngine();

			
			
			// ** Soggetto Erogatore **
			IDSoggetto soggettoErogatore = new IDSoggetto();
			soggettoErogatore.setTipo(pd.getSoggettoErogatore().getTipo());
			try{
				if(CostantiConfigurazione.PORTA_DELEGATA_SOGGETTO_EROGATORE_STATIC.equals(pd.getSoggettoErogatore().getIdentificazione()) || 
						pd.getSoggettoErogatore().getIdentificazione()==null){ //default is static	
					// STATIC-BASED
					soggettoErogatore.setNome(pd.getSoggettoErogatore().getNome());
				}else{
					if(readFirstHeaderIntegrazione && headerIntegrazione.getBusta()!=null && headerIntegrazione.getBusta().getDestinatario()!=null){
						// INTEGRATIONMANAGER INPUT
						soggettoErogatore.setNome(headerIntegrazione.getBusta().getDestinatario());
						if(headerIntegrazione.getBusta().getTipoDestinatario()!=null)
							soggettoErogatore.setTipo(headerIntegrazione.getBusta().getTipoDestinatario());
					}
					else{			
						if(CostantiConfigurazione.PORTA_DELEGATA_SOGGETTO_EROGATORE_URL_BASED.equals(pd.getSoggettoErogatore().getIdentificazione())){			
							// URL-BASED
							String sog = RegularExpressionEngine.getStringMatchPattern(urlInvocazionePD, 
									pd.getSoggettoErogatore().getPattern());
							if(sog!=null)
								sog = sog.replaceAll("_", "");
							soggettoErogatore.setNome(sog);		    
						}else if(CostantiConfigurazione.PORTA_DELEGATA_SOGGETTO_EROGATORE_CONTENT_BASED.equals(pd.getSoggettoErogatore().getIdentificazione())){
							// CONTENT-BASED
							String sog = xPathEngine.getStringMatchPattern(envelope,dnc, 
									pd.getSoggettoErogatore().getPattern());
							soggettoErogatore.setNome(sog);	
						}else if(CostantiConfigurazione.PORTA_DELEGATA_SOGGETTO_EROGATORE_INPUT_BASED.equals(pd.getSoggettoErogatore().getIdentificazione())){
							// INPUT-BASED
							if(headerIntegrazione.getBusta()!=null){
								soggettoErogatore.setNome(headerIntegrazione.getBusta().getDestinatario());
								if(headerIntegrazione.getBusta().getTipoDestinatario()!=null)
									soggettoErogatore.setTipo(headerIntegrazione.getBusta().getTipoDestinatario());
							}
						}
					}
	
				}
				try{
					soggettoErogatore.setCodicePorta(registroServiziManager.getDominio(soggettoErogatore, null, protocolFactory));
				}catch(Exception e){}
			}catch(Exception e){
				throw new PDIdentificazioneDinamicaException(PDIdentificazioneDinamica.SOGGETTO_EROGATORE,e.getMessage(),e);
			}


			// *** Servizio ***
			String servizio = null;
			String tipoServizio = pd.getServizio().getTipo();
			try{
				if(CostantiConfigurazione.PORTA_DELEGATA_SERVIZIO_STATIC.equals(pd.getServizio().getIdentificazione()) || 
						pd.getServizio().getIdentificazione()==null){ //default is static	
					// STATIC-BASED
					servizio = pd.getServizio().getNome();
				}else{
					if(readFirstHeaderIntegrazione && headerIntegrazione.getBusta()!=null && headerIntegrazione.getBusta().getServizio()!=null){
						// INTEGRATIONMANAGER INPUT
						servizio = headerIntegrazione.getBusta().getServizio();
						if(headerIntegrazione.getBusta().getTipoServizio()!=null)
							tipoServizio = headerIntegrazione.getBusta().getTipoServizio();
					}else{
						if(CostantiConfigurazione.PORTA_DELEGATA_SERVIZIO_URL_BASED.equals(pd.getServizio().getIdentificazione())){
							// URL-BASED
							servizio = RegularExpressionEngine.getStringMatchPattern(urlInvocazionePD, 
									pd.getServizio().getPattern());		
						}else if(CostantiConfigurazione.PORTA_DELEGATA_SERVIZIO_CONTENT_BASED.equals(pd.getServizio().getIdentificazione())){
							// CONTENT-BASED
							servizio = xPathEngine.getStringMatchPattern(envelope,dnc,pd.getServizio().getPattern());
						}else if(CostantiConfigurazione.PORTA_DELEGATA_SERVIZIO_INPUT_BASED.equals(pd.getServizio().getIdentificazione())){
							// INPUT-BASED
							if(headerIntegrazione.getBusta()!=null){
								servizio = headerIntegrazione.getBusta().getServizio();
								if(headerIntegrazione.getBusta().getTipoServizio()!=null)
									tipoServizio = headerIntegrazione.getBusta().getTipoServizio();
							}
						}
					}
				}
			}catch(Exception e){
				throw new PDIdentificazioneDinamicaException(PDIdentificazioneDinamica.SERVIZIO,e.getMessage(),e);
			}

			
			// ** Azione **
			String azione = null;
			Exception eAzione = null;
			try{
				if( pd.getAzione() != null  ){
					if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_STATIC.equals(pd.getAzione().getIdentificazione()) || 
							pd.getAzione().getIdentificazione()==null){ //default is static	
						// STATIC-BASED
						azione = pd.getAzione().getNome();
					}else{
						if(readFirstHeaderIntegrazione && headerIntegrazione.getBusta()!=null && headerIntegrazione.getBusta().getAzione()!=null){
							// INTEGRATIONMANAGER INPUT
							azione = headerIntegrazione.getBusta().getAzione();
						}else{
							if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_URL_BASED.equals(pd.getAzione().getIdentificazione())){
								// URL-BASED
								// L'azione se non viene trovata viene interpretata come null.
								// VINCOLO RILASCIATO DA 1.3.5
								//try{
								azione = RegularExpressionEngine.getStringMatchPattern(urlInvocazionePD, 
										pd.getAzione().getPattern());
								//}catch(Exception e){}
							}else if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_CONTENT_BASED.equals(pd.getAzione().getIdentificazione())){
								// CONTENT-BASED
								//	L'azione se non viene trovata viene interpretata come null.
								// VINCOLO RILASCIATO DA 1.3.5
								//try{
								azione = xPathEngine.getStringMatchPattern(envelope,dnc,pd.getAzione().getPattern());
								//}catch(Exception e){}
							}else if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_INPUT_BASED.equals(pd.getAzione().getIdentificazione())){
								// INPUT-BASED
								if(headerIntegrazione.getBusta()!=null){
									azione = headerIntegrazione.getBusta().getAzione();
								}
								// DA 1.3.5
								else{
									throw new DriverConfigurazioneNotFound("Azione non indicata negli header di integrazione");
								}
							}else if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_SOAP_ACTION_BASED.equals(pd.getAzione().getIdentificazione())){
								// SOAP-ACTION-BASED
								azione = soapAction;
								azione = azione.trim();
								// Nota: la soap action potrebbe essere quotata con "" 
								if(azione.startsWith("\"")){
									azione = azione.substring(1);
								}
								if(azione.endsWith("\"")){
									azione = azione.substring(0,(azione.length()-1));
								}	
								if("".equals(azione)){
									azione = null;
									throw new DriverConfigurazioneNotFound("SoapAction vuota ("+soapAction+") non è utilizzabile con una identificazione 'soapActionBased'");
								}
							}else if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_WSDL_BASED.equals(pd.getAzione().getIdentificazione())){
								IDServizio idServizio = new IDServizio(soggettoErogatore,tipoServizio,	servizio);
								OperationFinder.checkIDServizioPerRiconoscimentoAzione(idServizio, ModalitaIdentificazione.WSDL_BASED);
								azione = OperationFinder.searchOperationByRequestMessage(message, registroServiziManager, idServizio, this.log);
							}
						}
					}
				}
			}catch(Exception e){
				eAzione = e;
			}
			// Se non ho riconosciuto una azione, provo con la modalita' wsdlBased se e' abilitata.
			if(azione==null && 
					(pd.getAzione() != null) && 
					(StatoFunzionalita.ABILITATO.equals(pd.getAzione().getForceWsdlBased())) ){
				try{
					IDServizio idServizio = new IDServizio(soggettoErogatore,tipoServizio,	servizio);
					OperationFinder.checkIDServizioPerRiconoscimentoAzione(idServizio, ModalitaIdentificazione.WSDL_BASED);
					azione = OperationFinder.searchOperationByRequestMessage(message, registroServiziManager, idServizio, this.log);
				}catch(Exception eForceWsdl){
					this.log.debug("Riconoscimento forzato dell'azione non riuscito: "+eForceWsdl.getMessage(),eForceWsdl);
				}
			}
			// Se non ho riconosciuto una azione a questo punto, e durante il processo standard di riconoscimento era stato sollevata una eccezione
			// viene rilanciato
			if(azione==null && eAzione!=null)
				throw new PDIdentificazioneDinamicaException(PDIdentificazioneDinamica.AZIONE,eAzione.getMessage(),eAzione);

			
			// Build IDServizio...
			IDServizio service = new IDServizio(soggettoErogatore,
					tipoServizio,
					servizio,azione);
			return service;

		}catch(PDIdentificazioneDinamicaException e){
			throw e;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}

	/**
	 * Restituisce una tabella hash che contiene le informazioni MTOM riguradanti la gestione di una
	 * richiesta associata ad una Porta Delegata 
	 *
	 * @param pd Identificatore di una Porta Delegata
	 * @return Le proprieta' MTOM
	 * 
	 */
	protected MTOMProcessorConfig getPD_MTOMProcessorForSender(PortaDelegata pd) throws DriverConfigurazioneException{

		MTOMProcessorConfig config = new MTOMProcessorConfig();
		List<MtomXomPackageInfo> list = new ArrayList<MtomXomPackageInfo>();
		config.setInfo(list);
		
		if(pd == null)
            return config;

		MtomProcessor mtomProcessor = pd.getMtomProcessor();
		if(mtomProcessor == null)
			return config;

		if(mtomProcessor.getRequestFlow()!=null){
			MtomProcessorFlow mtomFlow = mtomProcessor.getRequestFlow();
			config.setMtomProcessorType(mtomFlow.getMode());
			for(int i=0 ; i<mtomFlow.sizeParameterList() ; i++){
				String nome = mtomFlow.getParameter(i).getNome();
				if(nome!=null){
					MtomXomPackageInfo info = new MtomXomPackageInfo();
					info.setName(nome);
					info.setXpathExpression(mtomFlow.getParameter(i).getPattern());
					info.setContentType(mtomFlow.getParameter(i).getContentType());
					info.setRequired(mtomFlow.getParameter(i).getRequired());
					list.add(info);
				}
			}
		}

		return config;
	}

	/**
	 * Restituisce una tabella hash che contiene le informazioni MTOM riguardanti la gestione di una
	 * risposta associate ad una Porta Delegata 
	 *
	 * @param pd Identificatore di una Porta Delegata
	 * @return Le proprieta' MTOM
	 * 
	 */
	protected MTOMProcessorConfig getPD_MTOMProcessorForReceiver(PortaDelegata pd) throws DriverConfigurazioneException{

		MTOMProcessorConfig config = new MTOMProcessorConfig();
		List<MtomXomPackageInfo> list = new ArrayList<MtomXomPackageInfo>();
		config.setInfo(list);
		
		if(pd == null)
            return config;

		MtomProcessor mtomProcessor = pd.getMtomProcessor();
		if(mtomProcessor == null)
			return config;

		if(mtomProcessor.getResponseFlow()!=null){
			MtomProcessorFlow mtomFlow = mtomProcessor.getResponseFlow();
			config.setMtomProcessorType(mtomFlow.getMode());
			for(int i=0 ; i<mtomFlow.sizeParameterList() ; i++){
				String nome = mtomFlow.getParameter(i).getNome();
				if(nome!=null){
					MtomXomPackageInfo info = new MtomXomPackageInfo();
					info.setName(nome);
					info.setXpathExpression(mtomFlow.getParameter(i).getPattern());
					info.setContentType(mtomFlow.getParameter(i).getContentType());
					info.setRequired(mtomFlow.getParameter(i).getRequired());
					list.add(info);
				}
			}
		}

		return config;
	}
	
	/**
	 * Restituisce una tabella hash che contiene le informazioni Message-Security riguradanti la gestione di una
	 * richiesta associata ad una Porta Delegata 
	 *
	 * @param pd Identificatore di una Porta Delegata
	 * @return Le proprieta' Message-Security
	 * 
	 */
	protected MessageSecurityConfig getPD_MessageSecurityForSender(PortaDelegata pd) throws DriverConfigurazioneException{

		MessageSecurityConfig securityConfig = new MessageSecurityConfig();
		java.util.Hashtable<String,Object> table = new java.util.Hashtable<String,Object>();
		securityConfig.setFlowParameters(table);
		
		if(pd == null)
            return securityConfig;

		if(pd.getStatoMessageSecurity()!=null && CostantiConfigurazione.DISABILITATO.toString().equals(pd.getStatoMessageSecurity())){
			return securityConfig;
		}
		
		MessageSecurity messageSecurity = pd.getMessageSecurity();
		if(messageSecurity == null)
			return securityConfig;

		if(messageSecurity.getRequestFlow()!=null){
			MessageSecurityFlow securityFlow = messageSecurity.getRequestFlow();
			if(securityFlow.getApplyToMtom()!=null){
				securityConfig.setApplyToMtom(StatoFunzionalita.ABILITATO.equals(securityFlow.getApplyToMtom()));
			}
			for(int i=0 ; i<securityFlow.sizeParameterList() ; i++){
				String nome = securityFlow.getParameter(i).getNome();
				if(nome!=null){
					String valore = securityFlow.getParameter(i).getValore();
					if("actor".equals(nome)){
						// patch per header senza actor (dove non si vuole che venga assunto il default=openspcoop)
						if(valore == null)
							valore = "";
						else if("notDefined".equals(valore)){
							valore = "";
						}
					}
					table.put(nome,valore);
				}
			}
		}

		return securityConfig;
	}

	/**
	 * Restituisce una tabella hash che contiene le informazioni Message-Security riguardanti la gestione di una
	 * risposta associate ad una Porta Delegata 
	 *
	 * @param pd Identificatore di una Porta Delegata
	 * @return Le proprieta' MessageSecurity
	 * 
	 */
	protected MessageSecurityConfig getPD_MessageSecurityForReceiver(PortaDelegata pd) throws DriverConfigurazioneException{

		MessageSecurityConfig securityConfig = new MessageSecurityConfig();
		java.util.Hashtable<String,Object> table = new java.util.Hashtable<String,Object>();
		securityConfig.setFlowParameters(table);
		
		if(pd == null)
            return securityConfig;

		if(pd.getStatoMessageSecurity()!=null && CostantiConfigurazione.DISABILITATO.toString().equals(pd.getStatoMessageSecurity())){
			return securityConfig;
		}
		
		MessageSecurity messageSecurity = pd.getMessageSecurity();
		if(messageSecurity == null)
			return securityConfig;

		if(messageSecurity.getResponseFlow()!=null){
			MessageSecurityFlow securityFlow = messageSecurity.getResponseFlow();
			if(securityFlow.getApplyToMtom()!=null){
				securityConfig.setApplyToMtom(StatoFunzionalita.ABILITATO.equals(securityFlow.getApplyToMtom()));
			}
			for(int i=0 ; i<securityFlow.sizeParameterList() ; i++){
				String nome = securityFlow.getParameter(i).getNome();
				if(nome!=null){
					String valore = securityFlow.getParameter(i).getValore();
					if("actor".equals(nome)){
						// patch per header senza actor (dove non si vuole che venga assunto il default=openspcoop)
						if(valore == null)
							valore = "";
						else if("notDefined".equals(valore)){
							valore = "";
						}
					}
					table.put(nome,valore);
				}
			}
		}

		return securityConfig;
	}

	/**
	 * Restituisce l'autenticazione associata alla porta delegata identificata dai parametri. 
	 *
	 * @param pd identificatore di una porta delegata
	 * @return autenticazione associata alla porta delegata.
	 * 
	 */
	protected String getAutenticazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		
		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}
		
		if(pd.getAutenticazione() == null || "".equals(pd.getAutenticazione()))
			return CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_SSL.toString();
		else
			return pd.getAutenticazione();
	}

	/**
	 * Restituisce l'autorizzazione associata alla porta delegata identificata dai parametri. 
	 *
	 * @param pd identificatore di una porta delegata
	 * @return autorizzazione associata alla porta delegata.
	 * 
	 */
	protected String getAutorizzazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		
		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}
		
		if(pd.getAutorizzazione() == null || "".equals(pd.getAutorizzazione()) )
			return CostantiConfigurazione.AUTORIZZAZIONE_OPENSPCOOP;
		else
			return pd.getAutorizzazione();
	}
	
	/**
	 * Restituisce l'autorizzazione per contenuto associata alla porta delegata identificata dai parametri. 
	 *
	 * @param pd identificatore di una porta delegata
	 * @return autorizzazione associata alla porta delegata.
	 * 
	 */
	protected String getAutorizzazioneContenuto(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		
		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}
		
		if(pd.getAutorizzazioneContenuto() == null || "".equals(pd.getAutorizzazioneContenuto()))
			return CostantiConfigurazione.NONE;
		else
			return pd.getAutorizzazioneContenuto();
	}

	/**
	 * Restituisce true , se la porta delegata richiesta  e' registrata
	 * con la funziona di ricevuta asincrona simmetrica abilitata.
	 *
	 * @param pd Identificatore di una Porta Delegata
	 * @return true se la porta delegata richiesta, risulta registrata con la funziona di ricevuta asincrona simmetrica abilitata.
	 * 
	 */
	protected boolean ricevutaAsincronaSimmetricaAbilitata(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null)
			return true; // default Abilitata-CNIPA
		return !CostantiConfigurazione.DISABILITATO.equals(pd.getRicevutaAsincronaSimmetrica());
	}

	/**
	 * Restituisce true , se la porta delegata richiesta  e' registrata
	 * con la funziona di ricevuta asincrona asimmetrica abilitata.
	 *
	 * @param pd Identificatore di una Porta Delegata
	 * @return true se la porta delegata richiesta, risulta registrata con la funziona di ricevuta asincrona asimmetrica abilitata.
	 * 
	 */
	protected boolean ricevutaAsincronaAsimmetricaAbilitata(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null)
			return true; // default Abilitata-CNIPA
		return !CostantiConfigurazione.DISABILITATO.equals(pd.getRicevutaAsincronaAsimmetrica());
	}

	/**
	 * Restituisce il tipo di validazione xsd  attiva nella porta delegata
	 * 
	 * @param pd identificatore di una porta delegata
	 * @return Restituisce il tipo di validazione xsd attiva nella porta delegata
	 * 
	 */
	protected ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(Connection connectionPdD, PortaDelegata pd,String implementazionePdDSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// default in configurazione
		ValidazioneContenutiApplicativi val = this.getTipoValidazioneContenutoApplicativo(connectionPdD,implementazionePdDSoggetto);
		
		if( pd==null || pd.getValidazioneContenutiApplicativi() == null ){
			return val;
		}

		ValidazioneContenutiApplicativi valPD = new ValidazioneContenutiApplicativi();
		
		if( CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(pd.getValidazioneContenutiApplicativi().getStato())  )
			valPD.setStato(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO);
		else if( CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(pd.getValidazioneContenutiApplicativi().getStato())  )
			valPD.setStato(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY);
		else if( CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.equals(pd.getValidazioneContenutiApplicativi().getStato())  )
			valPD.setStato(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO);

		if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(pd.getValidazioneContenutiApplicativi().getTipo())  )
			valPD.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP);
		else if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.equals(pd.getValidazioneContenutiApplicativi().getTipo())  )
			valPD.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL);
		else if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(pd.getValidazioneContenutiApplicativi().getTipo())  )
			valPD.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD);
		
		if( CostantiConfigurazione.ABILITATO.equals(pd.getValidazioneContenutiApplicativi().getAcceptMtomMessage())  )
			valPD.setAcceptMtomMessage(CostantiConfigurazione.ABILITATO);
		else if( CostantiConfigurazione.DISABILITATO.equals(pd.getValidazioneContenutiApplicativi().getAcceptMtomMessage())  )
			valPD.setAcceptMtomMessage(CostantiConfigurazione.DISABILITATO);
		
		return valPD;
	}

	/**
	 * Restituisce le eventuali correlazioni applicative da effettuare.
	 * 
	 * @param pd identificatore di una porta delegata
	 * @return Restituisce le eventuali correlazioni applicative da effettuare.
	 * 
	 */
	protected CorrelazioneApplicativa getCorrelazioneApplicativa(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}
		return pd.getCorrelazioneApplicativa();

	}

	/**
	 * Restituisce le eventuali correlazioni applicative sulla risposta da effettuare.
	 * 
	 * @param pd identificatore di una porta delegata
	 * @return Restituisce le eventuali correlazioni applicative sulla risposta da effettuare.
	 * 
	 */
	protected CorrelazioneApplicativaRisposta getCorrelazioneApplicativaRisposta(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}
		return pd.getCorrelazioneApplicativaRisposta();

	}

	/**
	 * Restituisce il meccanismo di integrazione associato alla porta delegata identificata dai parametri, se esiste. 
	 *
	 * @param pd identificatore di una porta delegata
	 * @return meccanismo di integrazione associata alla porta delegata se esiste, null altrimenti.
	 * 
	 */
	protected String[] getTipiIntegrazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		String[]tipi = null;
		if(pd!=null){
			if(pd.getIntegrazione() != null && ("".equals(pd.getIntegrazione())==false) ){
				tipi = pd.getIntegrazione().trim().split(",");
			}
		}  

		return tipi;
	}

	/**
	 * Restituisce l'indicazione se deve essere effettuata la gestione degli attachments
	 * 
	 * @param pd identificatore di una porta delegata
	 * @return Restituisce l'indicazione se deve essere effettuata la gestione degli attachments
	 * 
	 */
	protected boolean isGestioneManifestAttachments(Connection connectionPdD, PortaDelegata pd, IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			try{
				if(protocolFactory.createProtocolConfiguration().isSupportato(FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
					// Bug: 286: devo usare il default della configurazione SOLO SE il protocollo lo supporta.
					return this.isGestioneManifestAttachments(connectionPdD); //configurazione di default
				}
				else{
					return false;
				}
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		if( CostantiConfigurazione.ABILITATO.equals(pd.getGestioneManifest())  ){
			return true;
		}else if( CostantiConfigurazione.DISABILITATO.equals(pd.getGestioneManifest())  ){
			return false;
		}else{
			try{
				if(protocolFactory.createProtocolConfiguration().isSupportato(FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
					// Bug: 286: devo usare il default della configurazione SOLO SE il protocollo lo supporta.
					return this.isGestioneManifestAttachments(connectionPdD); //configurazione di default
				}
				else{
					return false;
				}
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Restituisce l'indicazione se deve essere allegato il messaggio Soap come allegato in un msg SoapWithAttachmetns con un Manifest 
	 * 
	 * @param pd identificatore di una porta delegata
	 * @return Restituisce l'indicazione se deve essere allegato il messaggio Soap come allegato in un msg SoapWithAttachmetns con un Manifest 
	 * 
	 */
	protected boolean isAllegaBody(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			return false; //configurazione di default
		}
		if( CostantiConfigurazione.ABILITATO.equals(pd.getAllegaBody())  )
			return true;
		else if( CostantiConfigurazione.DISABILITATO.equals(pd.getAllegaBody())  )
			return false;
		else 
			return false; //configurazione di default
	}
	
	/**
	 * Restituisce l'indicazione se deve essere scartato il SoapBody tra gli allegati inseriti in un msg SoapWithAttachmetns con un Manifest 
	 * 
	 * @param pd identificatore di una porta delegata
	 * @return Restituisce l'indicazione se deve essere scartato il SoapBody tra gli allegati inseriti in un msg SoapWithAttachmetns con un Manifest 
	 * 
	 */
	protected boolean isScartaBody(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			return false; //configurazione di default
		}
		if( CostantiConfigurazione.ABILITATO.equals(pd.getScartaBody())  )
			return true;
		else if( CostantiConfigurazione.DISABILITATO.equals(pd.getScartaBody())  )
			return false;
		else 
			return false; //configurazione di default
	}
	

	/**
	 * Restituisce l'indicazione se il profilo oneway o il profilo sincrono deve essere gestito in modalita stateless 
	 * 
	 * @param pd identificatore di una porta delegata
	 * @return Restituisce l'indicazione se il profilo oneway o il profilo sincrono deve essere gestito in modalita stateless 
	 * 
	 */
	protected boolean isModalitaStateless(PortaDelegata pd, ProfiloDiCollaborazione profiloCollaborazione) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(this.serverJ2EE==false){
			// Stateless obbligatorio in server di tipo web (non j2ee)
			return true;
		}
		
		if(pd==null){
			// configurazione di default
			if(ProfiloDiCollaborazione.ONEWAY.equals(profiloCollaborazione))
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessOneWay());
			else if(ProfiloDiCollaborazione.SINCRONO.equals(profiloCollaborazione))
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessSincrono());
			else
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessAsincroni());
		}
		
		if( CostantiConfigurazione.ABILITATO.equals(pd.getStateless())  )
			return true;
		else if( CostantiConfigurazione.DISABILITATO.equals(pd.getStateless())  )
			return false;
		else {
			//configurazione di default
			if(ProfiloDiCollaborazione.ONEWAY.equals(profiloCollaborazione))
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessOneWay());
			else if(ProfiloDiCollaborazione.SINCRONO.equals(profiloCollaborazione))
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessSincrono());
			else
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessAsincroni());
		}
	}
	
	/**
	 * Restituisce l'indicazione se la porta agisce in modalita' local-forward
	 * 
	 * @param pd identificatore di una porta delegata
	 * @return Restituisce l'indicazione se la porta agisce in modalita' local-forward
	 * 
	 */
	protected boolean isLocalForwardMode(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
				
		if(pd==null){
			// configurazione di default
			return false;
		}
		
		if( CostantiConfigurazione.ABILITATO.equals(pd.getLocalForward())  )
			return true;
		else if( CostantiConfigurazione.DISABILITATO.equals(pd.getLocalForward())  )
			return false;
		else {
			//configurazione di default
			return false;
		}
	}

	protected List<Object> getExtendedInfo(PortaDelegata pd)throws DriverConfigurazioneException{
		
		if(pd == null || pd.sizeExtendedInfoList()<=0)
            return null;

		return pd.getExtendedInfoList();
	}











	/* ********  PORTE APPLICATIVE  (Interfaccia) ******** */

	/**
	 * Restituisce un array di soggetti reali (e associata porta applicativa) 
	 * che possiedono il soggetto SoggettoVirtuale identificato da <var>idPA</var>
	 *
	 * @param idPA Identificatore di una Porta Applicativa con soggetto Virtuale
	 * @return una porta applicativa
	 * 
	 */
	protected Hashtable<IDSoggetto,PortaApplicativa> getPorteApplicative_SoggettiVirtuali(Connection connectionPdD,IDPortaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		 return this.configurazionePdD.getPorteApplicative_SoggettiVirtuali(connectionPdD,idPA,null,false);
	 }
	
	/**
	 * Restituisce true , se la porta applicativa richiesta  e' tra quelle registrate nella porta di dominio.
	 * La porta applicativa viene cercata utilizzando i parametri presenti in <var>idPA</var>:
	 * TipoServiceProvider,ServiceProvider,TipoServizio,Servizio,Azione
	 *
	 * @param idPA Identificatore di una Porta Applicativa
	 * @return true se la porta applicativa richiesta, risulta registrata nella porta di dominio, false altrimenti.
	 * 
	 */
	 protected boolean existsPA(Connection connectionPdD,RichiestaApplicativa idPA) throws DriverConfigurazioneException{	

		// Se non c'e' un servizio non puo' esistere una porta applicativa
		if(idPA.getIDServizio()==null)
			return false;
		if( (idPA.getIDServizio().getServizio()==null) || (idPA.getIDServizio().getTipoServizio()==null)  )
			return false;

		if( isSoggettoVirtuale(connectionPdD,idPA.getIDServizio().getSoggettoErogatore())  ){
			Hashtable<IDSoggetto,PortaApplicativa> paConSoggetti = null;
			try{
				paConSoggetti = this.configurazionePdD.getPorteApplicative_SoggettiVirtuali(connectionPdD,idPA.getIdPA(),idPA.getFiltroProprietaPorteApplicative(),true);
			}catch(DriverConfigurazioneNotFound e){
				return false;
			}
			if( paConSoggetti!=null && paConSoggetti.size()>0)
				return true;
			else
				return false;
		}else{
			PortaApplicativa pa = null;
			try{
				IDPortaApplicativaByNome idByNome = idPA.getIdPAbyNome();
				if(idByNome==null){
					return false;
				}
				pa = this.configurazionePdD.getPortaApplicativa(connectionPdD,idByNome.getNome(),idByNome.getSoggetto());
			}catch(DriverConfigurazioneNotFound e){
				return false;
			}
			return pa!=null;
		}
	}

	 public IDPortaApplicativaByNome convertTo(Connection connectionPdD,IDServizio idServizio,Hashtable<String,String> proprietaPresentiBustaRicevuta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		 IDPortaApplicativa idPA = new IDPortaApplicativa();
		 idPA.setIDServizio(idServizio);
		 PortaApplicativa pa = this.configurazionePdD.getPortaApplicativa(connectionPdD, idPA, proprietaPresentiBustaRicevuta);
		 IDPortaApplicativaByNome id = new IDPortaApplicativaByNome();
		 id.setNome(pa.getNome());
		 id.setSoggetto(idPA.getIDServizio().getSoggettoErogatore());
		 return id;
	 }
	 public IDPortaApplicativaByNome convertTo_SafeMethod(Connection connectionPdD,IDServizio idServizio,Hashtable<String,String> proprietaPresentiBustaRicevuta) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		 try{
			 if(idServizio!=null && idServizio.getTipoServizio()!=null &&
					 idServizio.getServizio()!=null &&
					 idServizio.getSoggettoErogatore()!=null &&
					 idServizio.getSoggettoErogatore().getTipo()!=null &&
					 idServizio.getSoggettoErogatore().getNome()!=null){
				 return this.convertTo(connectionPdD, idServizio, proprietaPresentiBustaRicevuta);
			 }
			 return null;
		 }catch(DriverConfigurazioneNotFound e){
			 return null;
		 }
	 }
	 
	/**
	 * Ritorna la porta applicativa
	 * 
	 * @param idPA
	 * @return porta applicativa
	 * @throws DriverConfigurazioneException
	 * @throws DriverConfigurazioneNotFound
	 */
	protected PortaApplicativa getPortaApplicativa(Connection connectionPdD,IDPortaApplicativaByNome idPA) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		if(idPA==null){
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (idPA is null)");
		}
		return this.getPortaApplicativa(connectionPdD,idPA.getNome(),idPA.getSoggetto());
	}
	
	protected PortaApplicativa getPortaApplicativa_SafeMethod(Connection connectionPdD,IDPortaApplicativaByNome idPA)throws DriverConfigurazioneException{
		try{
			if(idPA!=null && idPA.getNome()!=null &&
					idPA.getSoggetto()!=null &&
					idPA.getSoggetto().getTipo()!=null &&
					idPA.getSoggetto().getNome()!=null)
				return this.getPortaApplicativa(connectionPdD,idPA.getNome(),idPA.getSoggetto());
			else
				return null;
		}catch(DriverConfigurazioneNotFound e){
			return null;
		}
	}
	
	protected PortaApplicativa getPortaApplicativa(Connection connectionPdD,String nomePA, IDSoggetto soggettoProprietario) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPortaApplicativa(connectionPdD,nomePA, soggettoProprietario);
	}
	
	/**
	 * Restituisce un array 
	 * contenente le informazioni su ogni servizio applicativo collegato ad una porta applicativa 
	 * identificata dal parametro <var>idPA</var>. 
	 * La porta applicativa viene cercata utilizzando le proprieta'
	 * 'TipoDestinatario', 'Destinatario' , 'TipoServizio', 'Servizio' , 'Azione' 
	 * del parametro <var>idPA</var> di tipo {@link org.openspcoop2.pdd.config.RichiestaApplicativa}.
	 *
	 * @param pa Identificatore di una Porta Applicativa
	 * @return un array di identificatori di servizi applicativi.
	 * 
	 */
	protected String[] getServiziApplicativi(PortaApplicativa pa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}
		
		String[]sa = new String[pa.sizeServizioApplicativoList()];
		for(int i=0;i<pa.sizeServizioApplicativoList();i++){
			sa[i] = pa.getServizioApplicativo(i).getNome();
		}

		return sa;
	}


	/**
	 * Restituisce un oggetto SoggettoVirtuale contenente le informazioni sui servizi applicativi erogati
	 * dai reali soggetti mappati in soggetti virtuali
	 *
	 * @param idPA Identificatore di una Porta Applicativa con soggetto Virtuale
	 * @return un oggetto SoggettoVirtuale
	 * 
	 */
	protected SoggettoVirtuale getServiziApplicativi_SoggettiVirtuali(Connection connectionPdD,RichiestaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Hashtable<IDSoggetto,PortaApplicativa> paConSoggetti = this.configurazionePdD.getPorteApplicative_SoggettiVirtuali(connectionPdD,idPA.getIdPA()
				,idPA.getFiltroProprietaPorteApplicative(),true);
		if(paConSoggetti == null)
			throw new DriverConfigurazioneNotFound("PorteApplicative contenenti SoggettiVirtuali di ["+idPA.getIdPA()+"] non trovate");
		if(paConSoggetti.size() ==0)
			throw new DriverConfigurazioneNotFound("PorteApplicative contenenti SoggettiVirtuali di ["+idPA.getIdPA()+"] non trovate");

		java.util.List<SoggettoVirtualeServizioApplicativo> trovati = new java.util.ArrayList<SoggettoVirtualeServizioApplicativo>();

		for (Enumeration<?> e = paConSoggetti.keys() ; e.hasMoreElements() ;) {
			IDSoggetto soggReale = (IDSoggetto) e.nextElement();
			PortaApplicativa pa = paConSoggetti.get(soggReale);
			for(int k=0; k<pa.sizeServizioApplicativoList(); k++){
				
				SoggettoVirtualeServizioApplicativo sa = new SoggettoVirtualeServizioApplicativo();
				sa.setNomeServizioApplicativo(pa.getServizioApplicativo(k).getNome());
				sa.setIdSoggettoReale(soggReale);
				sa.setPortaApplicativa(pa);
				trovati.add(sa);
				
			}
		}

		if(trovati.size() == 0)
			throw new DriverConfigurazioneNotFound("PorteApplicative contenenti SoggettiVirtuali di ["+idPA.getIdPA()+"] non trovati soggetti virtuali");
		else{
			SoggettoVirtuale soggVirtuale = new SoggettoVirtuale();
			for (SoggettoVirtualeServizioApplicativo soggettoVirtualeServizioApplicativo : trovati) {
				soggVirtuale.addServizioApplicativo(soggettoVirtualeServizioApplicativo);
			}
			return soggVirtuale;
		}
	}

	protected List<PortaApplicativa> getPorteApplicative(Connection connectionPdD,String nomePA,String tipoSoggettoProprietario,String nomeSoggettoProprietario) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPorteApplicative(connectionPdD, nomePA, tipoSoggettoProprietario, nomeSoggettoProprietario);
	}
	
	
	/**
	 * Restituisce una tabella hash che contiene le informazioni MTOM riguradanti la gestione di una
	 * risposta associata ad una Porta Applicativa 
	 *
	 * @param pa Identificatore di una Porta Applicativa
	 * @return Le proprieta' MTOM
	 * 
	 */
	protected MTOMProcessorConfig getPA_MTOMProcessorForSender(PortaApplicativa pa)throws DriverConfigurazioneException{

		MTOMProcessorConfig config = new MTOMProcessorConfig();
		List<MtomXomPackageInfo> list = new ArrayList<MtomXomPackageInfo>();
		config.setInfo(list);
		
		if(pa == null)
            return config;

		MtomProcessor mtomProcessor = pa.getMtomProcessor();
		if(mtomProcessor == null)
			return config;

		if(mtomProcessor.getResponseFlow()!=null){
			MtomProcessorFlow mtomFlow = mtomProcessor.getResponseFlow();
			config.setMtomProcessorType(mtomFlow.getMode());
			for(int i=0 ; i<mtomFlow.sizeParameterList() ; i++){
				String nome = mtomFlow.getParameter(i).getNome();
				if(nome!=null){
					MtomXomPackageInfo info = new MtomXomPackageInfo();
					info.setName(nome);
					info.setXpathExpression(mtomFlow.getParameter(i).getPattern());
					info.setContentType(mtomFlow.getParameter(i).getContentType());
					info.setRequired(mtomFlow.getParameter(i).getRequired());
					list.add(info);
				}
			}
		}

		return config;
	}

	/**
	 * Restituisce una tabella hash che contiene le informazioni Message-Security riguardanti la gestione di una
	 * richiesta associate ad una Porta Applicativa 
	 *
	 * @param pa Identificatore di una Porta Applicativa
	 * @return Le proprieta' MTOM
	 * 
	 */
	protected MTOMProcessorConfig getPA_MTOMProcessorForReceiver(PortaApplicativa pa)throws DriverConfigurazioneException{

		MTOMProcessorConfig config = new MTOMProcessorConfig();
		List<MtomXomPackageInfo> list = new ArrayList<MtomXomPackageInfo>();
		config.setInfo(list);
		
		if(pa == null)
            return config;

		MtomProcessor mtomProcessor = pa.getMtomProcessor();
		if(mtomProcessor == null)
			return config;

		if(mtomProcessor.getRequestFlow()!=null){
			MtomProcessorFlow mtomFlow = mtomProcessor.getRequestFlow();
			config.setMtomProcessorType(mtomFlow.getMode());
			for(int i=0 ; i<mtomFlow.sizeParameterList() ; i++){
				String nome = mtomFlow.getParameter(i).getNome();
				if(nome!=null){
					MtomXomPackageInfo info = new MtomXomPackageInfo();
					info.setName(nome);
					info.setXpathExpression(mtomFlow.getParameter(i).getPattern());
					info.setContentType(mtomFlow.getParameter(i).getContentType());
					info.setRequired(mtomFlow.getParameter(i).getRequired());
					list.add(info);
				}
			}
		}

		return config; 
	}
	
	/**
	 * Restituisce una tabella hash che contiene le informazioni Message-Security riguradanti la gestione di una
	 * risposta associata ad una Porta Applicativa 
	 *
	 * @param pa Identificatore di una Porta Applicativa
	 * @return Le proprieta' MessageSecurity
	 * 
	 */
	protected MessageSecurityConfig getPA_MessageSecurityForSender(PortaApplicativa pa)throws DriverConfigurazioneException{

		MessageSecurityConfig securityConfig = new MessageSecurityConfig();
		java.util.Hashtable<String,Object> table = new java.util.Hashtable<String,Object>();
		securityConfig.setFlowParameters(table);
		
		if(pa == null)
            return securityConfig;

		if(pa.getStatoMessageSecurity()!=null && CostantiConfigurazione.DISABILITATO.toString().equals(pa.getStatoMessageSecurity())){
			return securityConfig;
		}
		
		MessageSecurity messageSecurity = pa.getMessageSecurity();
		if(messageSecurity == null)
			return securityConfig;

		if(messageSecurity.getResponseFlow()!=null){
			MessageSecurityFlow securityFlow = messageSecurity.getResponseFlow();
			if(securityFlow.getApplyToMtom()!=null){
				securityConfig.setApplyToMtom(StatoFunzionalita.ABILITATO.equals(securityFlow.getApplyToMtom()));
			}
			for(int i=0 ; i<securityFlow.sizeParameterList() ; i++){
				String nome = securityFlow.getParameter(i).getNome();
				if(nome!=null){
					String valore = securityFlow.getParameter(i).getValore();
					if("actor".equals(nome)){
						// patch per header senza actor (dove non si vuole che venga assunto il default=openspcoop)
						if(valore == null)
							valore = "";
						else if("notDefined".equals(valore)){
							valore = "";
						}
					}
					table.put(nome,valore);
				}
			}
		}

		return securityConfig;
	}

	/**
	 * Restituisce una tabella hash che contiene le informazioni Message-Security riguardanti la gestione di una
	 * richiesta associate ad una Porta Applicativa 
	 *
	 * @param pa Identificatore di una Porta Applicativa
	 * @return Le proprieta' MessageSecurity
	 * 
	 */
	protected MessageSecurityConfig getPA_MessageSecurityForReceiver(PortaApplicativa pa)throws DriverConfigurazioneException{

		MessageSecurityConfig securityConfig = new MessageSecurityConfig();
		java.util.Hashtable<String,Object> table = new java.util.Hashtable<String,Object>();
		securityConfig.setFlowParameters(table);
		
		if(pa == null)
            return securityConfig;

		if(pa.getStatoMessageSecurity()!=null && CostantiConfigurazione.DISABILITATO.toString().equals(pa.getStatoMessageSecurity())){
			return securityConfig;
		}
		
		MessageSecurity messageSecurity = pa.getMessageSecurity();
		if(messageSecurity == null)
			return securityConfig;

		if(messageSecurity.getRequestFlow()!=null){
			MessageSecurityFlow securityFlow = messageSecurity.getRequestFlow();
			if(securityFlow.getApplyToMtom()!=null){
				securityConfig.setApplyToMtom(StatoFunzionalita.ABILITATO.equals(securityFlow.getApplyToMtom()));
			}
			for(int i=0 ; i<securityFlow.sizeParameterList() ; i++){
				String nome = securityFlow.getParameter(i).getNome();
				if(nome!=null){
					String valore = securityFlow.getParameter(i).getValore();
					if("actor".equals(nome)){
						// patch per header senza actor (dove non si vuole che venga assunto il default=openspcoop)
						if(valore == null)
							valore = "";
						else if("notDefined".equals(valore)){
							valore = "";
						}
					}
					table.put(nome,valore);
				}
			}
		}

		return securityConfig; 
	}
	
	/**
	 * Restituisce true , se la porta applicativa richiesta  e' registrata
	 * con la funziona di ricevuta asincrona simmetrica abilitata.
	 *
	 * @param pa Identificatore di una Porta Applicativa
	 * @return true se la porta applicativa richiesta, risulta registrata con la funziona di ricevuta asincrona simmetrica abilitata.
	 * 
	 */
	protected boolean ricevutaAsincronaSimmetricaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pa==null)
			return true; // default Abilitata-CNIPA
		return !CostantiConfigurazione.DISABILITATO.equals(pa.getRicevutaAsincronaSimmetrica());
	}

	/**
	 * Restituisce true , se la porta applicativa richiesta  e' registrata
	 * con la funziona di ricevuta asincrona asimmetrica abilitata.
	 *
	 * @param pa Identificatore di una Porta Applicativa
	 * @return true se la porta applicativa richiesta, risulta registrata con la funziona di ricevuta asincrona asimmetrica abilitata.
	 * 
	 */
	protected boolean ricevutaAsincronaAsimmetricaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pa==null)
			return true; // default Abilitata-CNIPA
		return !CostantiConfigurazione.DISABILITATO.equals(pa.getRicevutaAsincronaAsimmetrica());
	}

	/**
	 * Restituisce il tipo di validazione xsd  attiva nella porta applicativa
	 * 
	 * @param pa identificatore di una porta applicativa
	 * @return Restituisce il tipo di validazione xsd attiva nella porta delegata
	 * 
	 */
	protected ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(Connection connectionPdD, PortaApplicativa pa,String implementazionePdDSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		// default in configurazione
		ValidazioneContenutiApplicativi val = this.getTipoValidazioneContenutoApplicativo(connectionPdD,implementazionePdDSoggetto);
		
		if( pa==null || pa.getValidazioneContenutiApplicativi() == null ){
			return val;
		}

		ValidazioneContenutiApplicativi valPA = new ValidazioneContenutiApplicativi();
		
		if( CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(pa.getValidazioneContenutiApplicativi().getStato())  )
			valPA.setStato(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO);
		else if( CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(pa.getValidazioneContenutiApplicativi().getStato())  )
			valPA.setStato(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY);
		else if( CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.equals(pa.getValidazioneContenutiApplicativi().getStato())  )
			valPA.setStato(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO);

		if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(pa.getValidazioneContenutiApplicativi().getTipo())  )
			valPA.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP);
		else if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.equals(pa.getValidazioneContenutiApplicativi().getTipo())  )
			valPA.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL);
		else if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(pa.getValidazioneContenutiApplicativi().getTipo())  )
			valPA.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD);
		
		if( CostantiConfigurazione.ABILITATO.equals(pa.getValidazioneContenutiApplicativi().getAcceptMtomMessage())  )
			valPA.setAcceptMtomMessage(CostantiConfigurazione.ABILITATO);
		else if( CostantiConfigurazione.DISABILITATO.equals(pa.getValidazioneContenutiApplicativi().getAcceptMtomMessage())  )
			valPA.setAcceptMtomMessage(CostantiConfigurazione.DISABILITATO);
		
		return valPA;
	}

	
	/**
	 * Restituisce le eventuali correlazioni applicative da effettuare.
	 * 
	 * @param pa identificatore di una porta applicativa
	 * @return Restituisce le eventuali correlazioni applicative da effettuare.
	 * 
	 */
	protected CorrelazioneApplicativa getCorrelazioneApplicativa(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}
		return pa.getCorrelazioneApplicativa();

	}
	
	/**
	 * Restituisce le eventuali correlazioni applicative sulla risposta da effettuare.
	 * 
	 * @param pa identificatore di una porta applicativa
	 * @return Restituisce le eventuali correlazioni applicative sulla risposta da effettuare.
	 * 
	 */
	protected CorrelazioneApplicativaRisposta getCorrelazioneApplicativaRisposta(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}
		return pa.getCorrelazioneApplicativaRisposta();

	}
	
	/**
	 * Restituisce il meccanismo di integrazione associato alla porta applicativa identificata dai parametri, se esiste. 
	 *
	 * @param pa identificatore di una porta applicativa
	 * @return meccanismo di integrazione associata alla porta applicativa se esiste, null altrimenti.
	 * 
	 */
	protected String[] getTipiIntegrazione(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 
		
		String[]tipi = null;
		if(pa!=null){
			if(pa.getIntegrazione() != null && ("".equals(pa.getIntegrazione())==false) ){
				tipi = pa.getIntegrazione().trim().split(",");
			}  
		}

		return tipi;
	}
	
	/**
	 * Restituisce l'indicazione se deve essere effettuata la gestione degli attachments
	 * 
	 * @param pa identificatore di una porta applicativa
	 * @return Restituisce l'indicazione se deve essere effettuata la gestione degli attachments
	 * 
	 */
	protected boolean isGestioneManifestAttachments(Connection connectionPdD, PortaApplicativa pa, IProtocolFactory protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(pa==null){
			try{
				if(protocolFactory.createProtocolConfiguration().isSupportato(FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
					// Bug: 286: devo usare il default della configurazione SOLO SE il protocollo lo supporta.
					return this.isGestioneManifestAttachments(connectionPdD); //configurazione di default
				}
				else{
					return false;
				}
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		
		if( CostantiConfigurazione.ABILITATO.equals(pa.getGestioneManifest())  ){
			return true;
		}else if( CostantiConfigurazione.DISABILITATO.equals(pa.getGestioneManifest())  ){
			return false;
		}else {
			try{
				if(protocolFactory.createProtocolConfiguration().isSupportato(FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
					// Bug: 286: devo usare il default della configurazione SOLO SE il protocollo lo supporta.
					return this.isGestioneManifestAttachments(connectionPdD); //configurazione di default
				}
				else{
					return false;
				}
			}catch(Exception e){
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
	}
	
	/**
	 * Restituisce l'indicazione se deve essere allegato il messaggio Soap come allegato in un msg SoapWithAttachmetns con un Manifest 
	 * 
	 * @param pa identificatore di una porta applicativa
	 * @return Restituisce l'indicazione se deve essere allegato il messaggio Soap come allegato in un msg SoapWithAttachmetns con un Manifest 
	 * 
	 */
	protected boolean isAllegaBody(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(pa==null)
			return false; //configurazione di default
		if( CostantiConfigurazione.ABILITATO.equals(pa.getAllegaBody())  )
			return true;
		else if( CostantiConfigurazione.DISABILITATO.equals(pa.getAllegaBody())  )
			return false;
		else 
			return false; //configurazione di default
	}
	
	/**
	 * Restituisce l'indicazione se deve essere scartato il SoapBody tra gli allegati inseriti in un msg SoapWithAttachmetns con un Manifest 
	 * 
	 * @param pa identificatore di una porta applicativa
	 * @return Restituisce l'indicazione se deve essere scartato il SoapBody tra gli allegati inseriti in un msg SoapWithAttachmetns con un Manifest 
	 * 
	 */
	protected boolean isScartaBody(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(pa==null){
			return false; //configurazione di default
		}

		if( CostantiConfigurazione.ABILITATO.equals(pa.getScartaBody())  )
			return true;
		else if( CostantiConfigurazione.DISABILITATO.equals(pa.getScartaBody())  )
			return false;
		else 
			return false; //configurazione di default
	}
	
	/**
	 * Restituisce l'indicazione se il profilo oneway o il profilo sincrono deve essere gestito in modalita stateless 
	 * 
	 * @param pa identificatore di una porta delegata
	 * @return Restituisce l'indicazione se il profilo oneway o il profilo sincrono deve essere gestito in modalita stateless 
	 * 
	 */
	protected boolean isModalitaStateless(PortaApplicativa pa, ProfiloDiCollaborazione profiloCollaborazione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		if(this.serverJ2EE==false){
			// Stateless obbligatorio in server di tipo web (non j2ee)
			return true;
		}
		
		if(pa==null){
			//configurazione di default
			if(ProfiloDiCollaborazione.ONEWAY.equals(profiloCollaborazione))
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessOneWay());
			else if(ProfiloDiCollaborazione.SINCRONO.equals(profiloCollaborazione))
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessSincrono());
			else
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessAsincroni()); 
		}

		if( CostantiConfigurazione.ABILITATO.equals(pa.getStateless())  )
			return true;
		else if( CostantiConfigurazione.DISABILITATO.equals(pa.getStateless())  )
			return false;
		else {
			//configurazione di default
			if(ProfiloDiCollaborazione.ONEWAY.equals(profiloCollaborazione))
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessOneWay());
			else if(ProfiloDiCollaborazione.SINCRONO.equals(profiloCollaborazione))
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessSincrono());
			else
				return CostantiConfigurazione.ABILITATO.equals(this.openspcoopProperties.getStatelessAsincroni()); 
		}
	}
	
	/**
	 * Restituisce l'autorizzazione per contenuto associata alla porta applicativa identificata dai parametri. 
	 *
	 * @param pa identificatore di una porta applicativa
	 * @return autorizzazione associata alla porta applicativa.
	 * 
	 */
	protected String getAutorizzazioneContenuto(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}
		
		if(pa.getAutorizzazioneContenuto() == null || "".equals(pa.getAutorizzazioneContenuto()))
			return CostantiConfigurazione.NONE;
		else
			return pa.getAutorizzazioneContenuto();
	}
	
	protected List<Object> getExtendedInfo(PortaApplicativa pa)throws DriverConfigurazioneException{
	
		if(pa == null || pa.sizeExtendedInfoList()<=0)
            return null;

		return pa.getExtendedInfoList();
	}
	







	/* ********  Servizi Applicativi (Interfaccia)  ******** */

	/**
	 * Indicazione se un servizio applicativo con il nome del parametro esiste.
	 * 
	 * @param serv Nome del servizio Applicativo
	 * @return  Indicazione se un servizio applicativo con il nome del parametro esiste.
	 */
	protected boolean existsServizioApplicativo(Connection connectionPdD,IDPortaDelegata idPD,String serv) throws DriverConfigurazioneException{ 

		ServizioApplicativo servizioApplicativo = null;
		try{
			servizioApplicativo = this.configurazionePdD.getServizioApplicativo(connectionPdD,idPD,serv);
		}catch(DriverConfigurazioneNotFound e){
			return false;
		}

		return servizioApplicativo!=null;

	}

	/**
	 * @param idPD
	 * @param serv
	 * @return servizio applicativo
	 * @throws DriverConfigurazioneNotFound
	 * @throws DriverConfigurazioneException
	 */
	protected ServizioApplicativo getServizioApplicativo(Connection connectionPdD,IDPortaDelegata idPD, String serv) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		return this.configurazionePdD.getServizioApplicativo(connectionPdD,idPD, serv);
	}
	
	/**
	 * @param idPA
	 * @param serv
	 * @return servizio applicativo
	 * @throws DriverConfigurazioneNotFound
	 * @throws DriverConfigurazioneException
	 */
	protected ServizioApplicativo getServizioApplicativo(Connection connectionPdD,IDPortaApplicativa idPA, String serv) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		return this.configurazionePdD.getServizioApplicativo(connectionPdD,idPA, serv);
	}
	
	/**
	 * Restituisce Il nome del servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param aSoggetto Soggetto su cui cercare il servizio applicativo con le credenziali passate come parametro.
	 * @param location Location che identifica una porta delegata.
	 * @param aUser User utilizzato nell'header HTTP Authentication.
	 * @param aPassword Password utilizzato nell'header HTTP Authentication.
	 * @return Il nome del servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	protected IDServizioApplicativo autenticazioneHTTP(Connection connectionPdD,IDSoggetto aSoggetto,String location, String aUser,String aPassword) throws DriverConfigurazioneException{ 

		ServizioApplicativo servizioApplicativo = null;
		try{
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setSoggettoFruitore(aSoggetto);
			idPD.setLocationPD(location);
			servizioApplicativo = this.configurazionePdD.getServizioApplicativoAutenticato(connectionPdD,idPD, aUser, aPassword);
		}catch(DriverConfigurazioneNotFound e){
			//this.log.debug("autenticazioneHTTP (not found): "+e.getMessage());
		}

		if(servizioApplicativo!=null){
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setNome(servizioApplicativo.getNome());
			if(servizioApplicativo.getTipoSoggettoProprietario()!=null && servizioApplicativo.getNomeSoggettoProprietario()!=null){
				idSA.setIdSoggettoProprietario(new IDSoggetto(servizioApplicativo.getTipoSoggettoProprietario(), servizioApplicativo.getNomeSoggettoProprietario()));
			}
			return idSA;
		}
		else
			return null;
	}
	protected IDServizioApplicativo autenticazioneHTTP(Connection connectionPdD,String aUser,String aPassword) throws DriverConfigurazioneException{ 

		ServizioApplicativo servizioApplicativo = null;
		try{
			servizioApplicativo = this.configurazionePdD.getServizioApplicativoAutenticato(connectionPdD,aUser, aPassword);
		}catch(DriverConfigurazioneNotFound e){
			//this.log.debug("autenticazioneHTTP (not found): "+e.getMessage());
		}

		if(servizioApplicativo!=null){
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setNome(servizioApplicativo.getNome());
			if(servizioApplicativo.getTipoSoggettoProprietario()!=null && servizioApplicativo.getNomeSoggettoProprietario()!=null){
				idSA.setIdSoggettoProprietario(new IDSoggetto(servizioApplicativo.getTipoSoggettoProprietario(), servizioApplicativo.getNomeSoggettoProprietario()));
			}
			return idSA;
		}
		else
			return null;
	}

	/**
	 * Restituisce Il nome del servizio applicativo che include le credenziali passate come parametro. 
	 *
	 * @param aSoggetto Soggetto su cui cercare il servizio applicativo con le credenziali passate come parametro.
	 * @param location Location che identifica una porta delegata.
	 * @param aSubject Subject utilizzato nella connessione HTTPS.
	 * @return Il nome del servizio applicativo che include le credenziali passate come parametro. 
	 * 
	 */
	protected IDServizioApplicativo autenticazioneHTTPS(Connection connectionPdD,IDSoggetto aSoggetto,String location, String aSubject) throws DriverConfigurazioneException{ 

		ServizioApplicativo servizioApplicativo = null;
		try{
			IDPortaDelegata idPD = new IDPortaDelegata();
			idPD.setSoggettoFruitore(aSoggetto);
			idPD.setLocationPD(location);
			servizioApplicativo = this.configurazionePdD.getServizioApplicativoAutenticato(connectionPdD,idPD, aSubject);
		}catch(DriverConfigurazioneNotFound e){
			//this.log.debug("autenticazioneHTTPS (not found): "+e.getMessage());
		}

		if(servizioApplicativo!=null){
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setNome(servizioApplicativo.getNome());
			if(servizioApplicativo.getTipoSoggettoProprietario()!=null && servizioApplicativo.getNomeSoggettoProprietario()!=null){
				idSA.setIdSoggettoProprietario(new IDSoggetto(servizioApplicativo.getTipoSoggettoProprietario(), servizioApplicativo.getNomeSoggettoProprietario()));
			}
			return idSA;
		}
		else
			return null;
	}
	
	protected IDServizioApplicativo autenticazioneHTTPS(Connection connectionPdD,String aSubject) throws DriverConfigurazioneException{ 

		ServizioApplicativo servizioApplicativo = null;
		try{
			servizioApplicativo = this.configurazionePdD.getServizioApplicativoAutenticato(connectionPdD,aSubject);
		}catch(DriverConfigurazioneNotFound e){
			//this.log.debug("autenticazioneHTTPS (not found): "+e.getMessage());
		}

		if(servizioApplicativo!=null){
			IDServizioApplicativo idSA = new IDServizioApplicativo();
			idSA.setNome(servizioApplicativo.getNome());
			if(servizioApplicativo.getTipoSoggettoProprietario()!=null && servizioApplicativo.getNomeSoggettoProprietario()!=null){
				idSA.setIdSoggettoProprietario(new IDSoggetto(servizioApplicativo.getTipoSoggettoProprietario(), servizioApplicativo.getNomeSoggettoProprietario()));
			}
			return idSA;
		}
		else
			return null;
	}

	/**
	 * Restituisce true se il servizio applicativo <var>servizio</var> puo' invocare la porta delegata <var>location</var>,
	 * gestita dal soggetto <var>aSoggetto</var>. 
	 *
	 * @param pd Porta Delegata
	 * @param servizio Identificativo del servizio applicativo.
	 * @return Effettua un controllo di autorizzazione per il servizio applicativo <var>servizio</var>.
	 * 
	 */
	protected boolean autorizzazione(PortaDelegata pd, String servizio) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if( (pd == null) || (servizio==null) )
			return false;

		for(int j=0; j<pd.sizeServizioApplicativoList(); j++){
			ServizioApplicativo sa = pd.getServizioApplicativo(j);
			if(servizio.equals(sa.getNome()))
				return true;
		}

		return false;

	}

	/**
	 * Restituisce le proprieta' di fault specifiche per il servizio applicativo.
	 *
	 * @param gestioneErrore Proprieta di fault da rendere specifiche per il servizio applicativo.
	 * @param sa Identificativo del servizio applicativo.

	 * 
	 */
	protected void aggiornaProprietaGestioneErrorePD(ProprietaErroreApplicativo gestioneErrore, ServizioApplicativo sa) throws DriverConfigurazioneException {

		if(sa==null)
			return;
		if(sa.getInvocazionePorta()==null)
			return;
		if(sa.getInvocazionePorta().getGestioneErrore()==null)
			return;
		InvocazionePortaGestioneErrore proprietaSA = sa.getInvocazionePorta().getGestioneErrore();


		// Aggiornamento proprieta
		// tipo di fault xml/soap
		if(CostantiConfigurazione.ERRORE_APPLICATIVO_SOAP.equals(proprietaSA.getFault()))
			gestioneErrore.setFaultAsXML(false);
		else if(CostantiConfigurazione.ERRORE_APPLICATIVO_XML.equals(proprietaSA.getFault()))
			gestioneErrore.setFaultAsXML(true);
		// fault actor
		if(proprietaSA.getFaultActor()!=null)
			gestioneErrore.setFaultActor(proprietaSA.getFaultActor());
		// fault generic code abilitato/disabilitato
		if(CostantiConfigurazione.ABILITATO.equals(proprietaSA.getGenericFaultCode()))
			gestioneErrore.setFaultAsGenericCode(true);
		else if(CostantiConfigurazione.DISABILITATO.equals(proprietaSA.getGenericFaultCode()))
			gestioneErrore.setFaultAsGenericCode(false);
		// fault prefix code
		if(proprietaSA.getPrefixFaultCode()!=null)
			gestioneErrore.setFaultPrefixCode(proprietaSA.getPrefixFaultCode());

		return;
	}

	/**
	 * Restituisce l'indicazione se deve essere effettuato un invio per riferimento.
	 *
	 * @param sa Identificativo del servizio applicativo.
	 * @return true se l'invocazione e' per riferimento.
	 * 
	 */
	protected boolean invocazionePortaDelegataPerRiferimento(ServizioApplicativo sa) throws DriverConfigurazioneException{
		if(sa==null)
			return false;
		if(sa.getInvocazionePorta()==null)
			return false;
		return CostantiConfigurazione.ABILITATO.equals(sa.getInvocazionePorta().getInvioPerRiferimento());
	}
	
	/**
	 * Restituisce l'indicazione se deve essere effettuato lo sbustamento delle informazioni di protocollo
	 *
	 * @param sa Identificativo del servizio applicativo.
	 * @return true se l'invocazione e' per riferimento.
	 * 
	 */
	protected boolean invocazionePortaDelegataSbustamentoInformazioniProtocollo(ServizioApplicativo sa) throws DriverConfigurazioneException{
		if(sa==null)
			return true;
		if(sa.getInvocazionePorta()==null)
			return true;
		return !CostantiConfigurazione.DISABILITATO.equals(sa.getInvocazionePorta().getSbustamentoInformazioniProtocollo());
	}

	
	
	
	






	// INVOCAZIONE SERVIZIO

	/**
	 * Restituisce l'indicazione se il servizio applicativo associato alla porta applicativa,
	 * possiede abilitata la caratteristica del servizio GetMessage per quanto riguarda l'invocazione del servizio.
	 *
	 * @param sa ServizioApplicativo
	 * @return true se il servizio applicativo puo' ricevere i messaggi anche dal servizio GOP
	 * 
	 */
	protected boolean invocazioneServizioConGetMessage(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getInvocazioneServizio()==null)
			return false;
		InvocazioneServizio serv = sa.getInvocazioneServizio();
		return CostantiConfigurazione.ABILITATO.equals(serv.getGetMessage());
	}

	/**
	 * Restituisce l'indicazione se il servizio applicativo associato alla porta applicativa,
	 * possiede abilitata la caratteristica del servizio di sbustamento Soap per quanto riguarda l'invocazione del servizio.
	 *
	 * @param sa ServizioApplicativo
	 * @return true se il servizio applicativo ha associata la funzionalita' di sbustamento Soap
	 * 
	 */
	protected boolean invocazioneServizioConSbustamento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getInvocazioneServizio()==null)
			return false;
		InvocazioneServizio serv = sa.getInvocazioneServizio();
		return CostantiConfigurazione.ABILITATO.equals(serv.getSbustamentoSoap());
	}

	protected boolean invocazioneServizioConSbustamentoInformazioniProtocollo(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return true;
		if(sa.getInvocazioneServizio()==null)
			return true;
		InvocazioneServizio serv = sa.getInvocazioneServizio();
		return !CostantiConfigurazione.DISABILITATO.equals(serv.getSbustamentoInformazioniProtocollo());
	}
	
	/**
	 * Restituisce l'indicazione se il servizio applicativo associato alla porta applicativa,
	 * possiede la definizione di un connettore
	 *
	 * @param sa ServizioApplicativo
	 * @return true se il servizio applicativo possiede la definizione di un connettore
	 * 
	 */
	protected boolean invocazioneServizioConConnettore(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getInvocazioneServizio()==null)
			return false;

		InvocazioneServizio serv = sa.getInvocazioneServizio();
		Connettore connettore = serv.getConnettore();
		if(connettore!=null && 
				!CostantiConfigurazione.NONE.equals(connettore.getTipo()) &&
				!CostantiConfigurazione.DISABILITATO.equals(connettore.getTipo()))
			return true;
		else
			return false;
	}

	/**
	 * Restituisce l'oggetto {@link org.openspcoop2.pdd.core.connettori.ConnettoreMsg} 
	 * contenente le informazioni sul servizio applicativo collegato ad una porta applicativa 
	 * identificata dal parametro <var>idPA</var>. 
	 *
	 * @param sa ServizioApplicativo
	 * @param idPA Identificatore di una Porta Applicativa
	 * @return l'oggetto {@link org.openspcoop2.pdd.core.connettori.ConnettoreMsg} associati al servizio applicativo.
	 * 
	 */
	protected ConnettoreMsg getInvocazioneServizio(Connection connectionPdD,ServizioApplicativo sa,RichiestaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Servizio applicativo
		if(sa.getInvocazioneServizio()==null)
			throw new DriverConfigurazioneNotFound("Servizio applicativo ["+idPA.getServizioApplicativo()+"] del soggetto["+idPA.getIDServizio().getSoggettoErogatore()+"] non possieder l'elemento invocazione servizio");
		InvocazioneServizio serv = sa.getInvocazioneServizio();


		// Soggetto Erogatore
		IDSoggetto aSoggetto = idPA.getIDServizio().getSoggettoErogatore();
		Soggetto soggetto = this.configurazionePdD.getSoggetto(connectionPdD,aSoggetto);
		if(soggetto==null)
			throw new DriverConfigurazioneNotFound("[getInvocazioneServizio] Soggetto erogatore non trovato");


		// Porta Applicativa
		PortaApplicativa pa = this.configurazionePdD.getPortaApplicativa(connectionPdD,idPA.getIdPAbyNome().getNome(),idPA.getIdPAbyNome().getSoggetto());


		// CONNETTORE
		Connettore connettore = serv.getConnettore();
		java.util.Hashtable<String,String> properties = null;
		if(connettore != null && CostantiConfigurazione.DISABILITATO.equals(connettore.getTipo())==false){
			String nome = connettore.getNome();
			if(connettore.getTipo() == null){
				// la definizione nel servizio applicativo e' un puntatore, lo cerca nel soggetto.
				for(int i=0;i<soggetto.sizeConnettoreList();i++){
					if(nome.equals(soggetto.getConnettore(i).getNome())){
						connettore = soggetto.getConnettore(i);
						break;
					}
				}
			}
			
			// urlPrefixRewriter
			setPAUrlPrefixRewriter(connectionPdD,connettore, aSoggetto);
				
			// Properties connettore
			properties = new java.util.Hashtable<String,String>();
			for(int i=0;i<connettore.sizePropertyList();i++){
				properties.put(connettore.getProperty(i).getNome(),connettore.getProperty(i).getValore());
			}
		}


		// PROTOCOL-PROPERTIES
		java.util.Properties protocol_properties = new java.util.Properties();
		for(int i=0;i<pa.sizeProprietaProtocolloList();i++){
			protocol_properties.put(pa.getProprietaProtocollo(i).getNome(),pa.getProprietaProtocollo(i).getValore());
		}

		// Autenticazione
		String autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString();
		String tmp = null;
		if(serv.getAutenticazione()!=null){
			tmp = serv.getAutenticazione().toString();
		}
		if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_SSL.toString().equalsIgnoreCase(tmp))
			autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_SSL.toString();
		if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString().equalsIgnoreCase(tmp))
			autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString();


		// Credenziali
		Credenziali credenziali = null;
		if(serv.getCredenziali()!=null && serv.getCredenziali().getTipo()!=null){
			if(autenticazione.equals(serv.getCredenziali().getTipo().toString())){
				credenziali = new Credenziali();
				credenziali.setUsername(serv.getCredenziali().getUser());
				credenziali.setPassword(serv.getCredenziali().getPassword());
				credenziali.setSubject(serv.getCredenziali().getSubject());
			}
		}

		// Costruisco connettoreMsg
		ConnettoreMsg connettoreMsg = null;
		if(connettore != null){
			connettoreMsg = new ConnettoreMsg();
			connettoreMsg.setTipoConnettore(connettore.getTipo());
			connettoreMsg.setConnectorProperties(properties);
			connettoreMsg.setSbustamentoSOAP(CostantiConfigurazione.ABILITATO.equals(serv.getSbustamentoSoap()));
			connettoreMsg.setSbustamentoInformazioniProtocollo(!CostantiConfigurazione.DISABILITATO.equals(serv.getSbustamentoInformazioniProtocollo()));
			connettoreMsg.setPropertiesTrasporto(protocol_properties);
			connettoreMsg.setPropertiesUrlBased(protocol_properties);
			connettoreMsg.setAutenticazione(autenticazione);
			connettoreMsg.setCredenziali(credenziali);
		}


		return connettoreMsg;
	}

	/**
	 * Restituisce la politica di gestione della consegna tramite connettore, 
	 * contenente le informazioni sul servizio applicativo collegato ad una porta applicativa 
	 * identificata dal parametro <var>idPA</var>. 
	 *
	 * @param sa ServizioApplicativo
	 * @return la politica di gestione della consegna tramite connettore
	 * 
	 */
	protected GestioneErrore getGestioneErroreConnettore_InvocazioneServizio(Connection connectionPdD,ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		//  Servizio applicativo
		if(sa.getInvocazioneServizio()==null || 
				sa.getInvocazioneServizio().getGestioneErrore()==null)
			return getGestioneErroreConnettoreComponenteIntegrazione(connectionPdD);
		InvocazioneServizio invocazione = sa.getInvocazioneServizio();
		return invocazione.getGestioneErrore();
	}

	/**
	 * Restituisce l'indicazione se deve essere effettuato un invio per riferimento.
	 *
	 * @param sa ServizioApplicativo
	 * @return true se l'invocazione e' per riferimento.
	 * 
	 */
	protected boolean invocazioneServizioPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		//	  Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getInvocazioneServizio()==null)
			return false;
		return CostantiConfigurazione.ABILITATO.equals(sa.getInvocazioneServizio().getInvioPerRiferimento());
	}

	/**
	 * Restituisce l'indicazione se deve essere ricevuta una risposto per riferimento.
	 *
	 * @param sa ServizioApplicativo
	 * @return true se l'invocazione e' per riferimento.
	 * 
	 */
	protected boolean invocazioneServizioRispostaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		//		  Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getInvocazioneServizio()==null)
			return false;
		return CostantiConfigurazione.ABILITATO.equals(sa.getInvocazioneServizio().getRispostaPerRiferimento());
	}

	






	// RICEZIONE RISPOSTA ASINCRONA

	/**
	 * Restituisce true se il servizio applicativo <var>servizio</var> contiene una ricezione risposta asincrona. 
	 * Cioe' dispone di un connettore e/o di un servizio di getMessage abilitato
	 *
	 * @param sa ServizioApplicativo
	 * @return Controlla se il servizio applicativo <var>servizio</var> possiede una ricezione risposta asincrona.
	 * 
	 */
	protected boolean existsConsegnaRispostaAsincrona(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 

		if(sa==null)
			return false;
		if(sa.getRispostaAsincrona()==null)
			return false;

		if(CostantiConfigurazione.ABILITATO.equals(sa.getRispostaAsincrona().getGetMessage()))
			return true;
		if( sa.getRispostaAsincrona().getConnettore()!=null 
				&& !CostantiConfigurazione.DISABILITATO.equals(sa.getRispostaAsincrona().getConnettore().getTipo()) )
			return true;

		return false;

	}

	/**
	 * Restituisce l'indicazione se il servizio applicativo associato alla porta delegata,
	 * possiede abilitata la caratteristica del servizio GetMessage per quanto riguarda la ricezione di risposte asincrone.
	 *
	 * @param sa ServizioApplicativo
	 * @return true se il servizio applicativo puo' ricevere i messaggi anche dal servizio GetMessage
	 * 
	 */
	protected boolean consegnaRispostaAsincronaConGetMessage(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getRispostaAsincrona()==null)
			return false;
		RispostaAsincrona serv = sa.getRispostaAsincrona();
		return CostantiConfigurazione.ABILITATO.equals(serv.getGetMessage());
	}

	/**
	 * Restituisce l'indicazione se il servizio applicativo associato alla porta delegata,
	 * possiede abilitata la caratteristica del servizio di sbustamento Soap per quanto riguarda la ricezione di contenuti asincroni.
	 *
	 * @param sa ServizioApplicativo
	 * @return true se il servizio applicativo ha associata la funzionalita' di sbustamento Soap
	 * 
	 */
	protected boolean consegnaRispostaAsincronaConSbustamento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getRispostaAsincrona()==null)
			return false;
		RispostaAsincrona serv = sa.getRispostaAsincrona();
		return CostantiConfigurazione.ABILITATO.equals(serv.getSbustamentoSoap());
	}
	
	protected boolean consegnaRispostaAsincronaConSbustamentoInformazioniProtocollo(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return true;
		if(sa.getRispostaAsincrona()==null)
			return true;
		RispostaAsincrona serv = sa.getRispostaAsincrona();
		return !CostantiConfigurazione.DISABILITATO.equals(serv.getSbustamentoInformazioniProtocollo());
	}

	/**
	 * Restituisce l'indicazione se il servizio applicativo associato alla porta delegata,
	 * possiede la definizione di un connettore
	 *
	 * @param sa ServizioApplicativo
	 * @return true se il servizio applicativo possiede la definizione di un connettore
	 * 
	 */
	protected boolean consegnaRispostaAsincronaConConnettore(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getRispostaAsincrona()==null)
			return false;
		RispostaAsincrona serv = sa.getRispostaAsincrona();
		Connettore connettore = serv.getConnettore();
		if(connettore!=null && 
				!CostantiConfigurazione.NONE.equals(connettore.getTipo()) &&
				!CostantiConfigurazione.DISABILITATO.equals(connettore.getTipo()))
			return true;
		else
			return false;
	}

	/**
	 * Restituisce l'oggetto {@link org.openspcoop2.pdd.core.connettori.ConnettoreMsg} 
	 * contenente le informazioni sul servizio applicativo, per quanto riguarda il servizio di ricezione risposte asincrono,
	 * collegato ad una porta delegata 
	 * identificata dal parametro <var>idPD</var>. 
	 *
	 * @param sa ServizioApplicativo
	 * @param idPD Identificatore di una Porta Delegata
	 * @return l'oggetto {@link org.openspcoop2.pdd.core.connettori.ConnettoreMsg} associati al servizio applicativo.
	 * 
	 */
	protected ConnettoreMsg getConsegnaRispostaAsincrona(Connection connectionPdD,ServizioApplicativo sa,RichiestaDelegata idPD)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Servizio applicativo
		if(sa.getRispostaAsincrona()==null)
			throw new DriverConfigurazioneNotFound("Servizio applicativo ["+idPD.getServizioApplicativo()+"] del soggetto["+idPD.getSoggettoFruitore()+"] non possiede una risposta Asincrona");
		RispostaAsincrona serv = sa.getRispostaAsincrona();


		// Soggetto che possiede il connettore
		IDSoggetto aSoggetto = idPD.getSoggettoFruitore();
		Soggetto soggetto = this.configurazionePdD.getSoggetto(connectionPdD,aSoggetto);
		if(soggetto==null)
			throw new DriverConfigurazioneNotFound("[getConsegnaRispostaAsincrona] Soggetto non trovato");


		// CONNETTORE
		Connettore connettore = serv.getConnettore();
		java.util.Hashtable<String,String> properties = null;
		if(connettore != null && CostantiConfigurazione.DISABILITATO.equals(connettore.getTipo())==false){
			String nome = connettore.getNome();
			if(connettore.getTipo() == null){
				// la definizione nel servizio applicativo e' un puntatore, lo cerca nel soggetto.
				for(int i=0;i<soggetto.sizeConnettoreList();i++){
					if(nome.equals(soggetto.getConnettore(i).getNome())){
						connettore = soggetto.getConnettore(i);
						break;
					}
				}
			}
			
			// urlPrefixRewriter
			setPAUrlPrefixRewriter(connectionPdD,connettore, aSoggetto);
			
			// set properties
			properties = new java.util.Hashtable<String,String>();
			for(int i=0;i<connettore.sizePropertyList();i++){
				properties.put(connettore.getProperty(i).getNome(),connettore.getProperty(i).getValore());
			}
		}


		// Autenticazione
		String autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString();
		String tmp = null;
		if(serv.getAutenticazione()!=null){
			tmp = serv.getAutenticazione().toString();
		}
		if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_SSL.toString().equalsIgnoreCase(tmp))
			autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_SSL.toString();
		if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString().equalsIgnoreCase(tmp))
			autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString();


		// Credenziali
		Credenziali credenziali = null;
		if(serv.getCredenziali()!=null && serv.getCredenziali().getTipo()!=null){
			if(autenticazione.equals(serv.getCredenziali().getTipo().toString())){
				credenziali = new Credenziali();
				credenziali.setUsername(serv.getCredenziali().getUser());
				credenziali.setPassword(serv.getCredenziali().getPassword());
				credenziali.setSubject(serv.getCredenziali().getSubject());
			}
		}

		// Costruisco connettoreMsg
		ConnettoreMsg connettoreMsg = null;
		if(connettore != null){
			connettoreMsg = new ConnettoreMsg();
			connettoreMsg.setTipoConnettore(connettore.getTipo());
			connettoreMsg.setConnectorProperties(properties);

			connettoreMsg.setSbustamentoSOAP(CostantiConfigurazione.ABILITATO.equals(serv.getSbustamentoSoap()));
			connettoreMsg.setSbustamentoInformazioniProtocollo(!CostantiConfigurazione.DISABILITATO.equals(serv.getSbustamentoInformazioniProtocollo()));
			connettoreMsg.setAutenticazione(autenticazione);
			connettoreMsg.setCredenziali(credenziali);
		}


		return connettoreMsg;
	}

	/**
	 * Restituisce l'oggetto {@link org.openspcoop2.pdd.core.connettori.ConnettoreMsg} 
	 * contenente le informazioni sul servizio applicativo, per quanto riguarda il servizio di ricezione risposte asincrono,
	 * collegato ad una porta applicativa 
	 * identificata dal parametro <var>idPA</var>. 
	 *
	 * @param sa ServizioApplicativo
	 * @param idPA Identificatore di una Porta Applicativa
	 * @return l'oggetto {@link org.openspcoop2.pdd.core.connettori.ConnettoreMsg} associati al servizio applicativo.
	 * 
	 */
	protected ConnettoreMsg getConsegnaRispostaAsincrona(Connection connectionPdD,ServizioApplicativo sa,RichiestaApplicativa idPA)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		//		 Servizio applicativo
		if(sa.getRispostaAsincrona()==null)
			throw new DriverConfigurazioneNotFound("Servizio applicativo ["+idPA.getServizioApplicativo()+"] del soggetto["+idPA.getSoggettoFruitore()+"] non possiede l'elemento invocazione servizio");
		RispostaAsincrona serv = sa.getRispostaAsincrona();


		// Soggetto che contiene il connettore
		IDSoggetto aSoggetto = idPA.getIDServizio().getSoggettoErogatore();
		Soggetto soggetto = this.configurazionePdD.getSoggetto(connectionPdD,aSoggetto);
		if(soggetto==null)
			throw new DriverConfigurazioneNotFound("[getConsegnaRispostaAsincrona] Soggetto non trovato");


		// Porta Applicativa
		PortaApplicativa pa = this.configurazionePdD.getPortaApplicativa(connectionPdD,idPA.getIdPAbyNome().getNome(),idPA.getIdPAbyNome().getSoggetto());


		// CONNETTORE
		Connettore connettore = serv.getConnettore();
		java.util.Hashtable<String,String> properties = null;
		if(connettore != null && CostantiConfigurazione.DISABILITATO.equals(connettore.getTipo())==false){
			String nome = connettore.getNome();
			if(connettore.getTipo() == null){
				// la definizione nel servizio applicativo e' un puntatore, lo cerca nel soggetto.
				for(int i=0;i<soggetto.sizeConnettoreList();i++){
					if(nome.equals(soggetto.getConnettore(i).getNome())){
						connettore = soggetto.getConnettore(i);
						break;
					}
				}
			}
			
			// urlPrefixRewriter
			setPAUrlPrefixRewriter(connectionPdD,connettore, aSoggetto);
				
			// Properties connettore
			properties = new java.util.Hashtable<String,String>();
			for(int i=0;i<connettore.sizePropertyList();i++){
				properties.put(connettore.getProperty(i).getNome(),connettore.getProperty(i).getValore());
			}
		}


		// PROTOCOL-PROPERTIES
		java.util.Properties protocol_properties = new java.util.Properties();
		for(int i=0;i<pa.sizeProprietaProtocolloList();i++){
			protocol_properties.put(pa.getProprietaProtocollo(i).getNome(),pa.getProprietaProtocollo(i).getValore());
		}

		// Autenticazione
		String autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString();
		String tmp = null;
		if(serv.getAutenticazione()!=null){
			tmp = serv.getAutenticazione().toString();
		}
		if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_SSL.toString().equalsIgnoreCase(tmp))
			autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_SSL.toString();
		if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString().equalsIgnoreCase(tmp))
			autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString();


		// Credenziali
		Credenziali credenziali = null;
		if(serv.getCredenziali()!=null && serv.getCredenziali().getTipo()!=null){
			if(autenticazione.equals(serv.getCredenziali().getTipo().toString())){
				credenziali = new Credenziali();
				credenziali.setUsername(serv.getCredenziali().getUser());
				credenziali.setPassword(serv.getCredenziali().getPassword());
				credenziali.setSubject(serv.getCredenziali().getSubject());
			}
		}

		// Costruisco connettoreMsg
		ConnettoreMsg connettoreMsg = null;
		if(connettore != null){
			connettoreMsg = new ConnettoreMsg();
			connettoreMsg.setTipoConnettore(connettore.getTipo());
			connettoreMsg.setConnectorProperties(properties);
			connettoreMsg.setSbustamentoSOAP(CostantiConfigurazione.ABILITATO.equals(serv.getSbustamentoSoap()));
			connettoreMsg.setSbustamentoInformazioniProtocollo(!CostantiConfigurazione.DISABILITATO.equals(serv.getSbustamentoInformazioniProtocollo()));
			connettoreMsg.setPropertiesTrasporto(protocol_properties);
			connettoreMsg.setPropertiesUrlBased(protocol_properties);
			connettoreMsg.setAutenticazione(autenticazione);
			connettoreMsg.setCredenziali(credenziali);
		}


		return connettoreMsg;
	}
	
	/**
	 * Restituisce la politica di gestione della consegna tramite connettore, 
	 * per quanto riguarda il servizio di ricezione risposte asincrono,
	 * collegato ad una porta delegata 
	 * identificata dal parametro <var>idPD</var>. 
	 *
	 * @param sa ServizioApplicativo
	 * @return la politica di gestione della consegna tramite connettore
	 * 
	 */
	protected GestioneErrore getGestioneErroreConnettore_RispostaAsincrona(Connection connectionPdD,ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		//  Servizio applicativo
		if(sa.getRispostaAsincrona()==null ||
				sa.getRispostaAsincrona().getGestioneErrore()==null	)
			return getGestioneErroreConnettoreComponenteIntegrazione(connectionPdD);
		RispostaAsincrona asincrona = sa.getRispostaAsincrona();
		return asincrona.getGestioneErrore();

	}

	/**
	 * Restituisce l'indicazione se deve essere effettuato un invio per riferimento.
	 *
	 * @param sa ServizioApplicativo
	 * @return true se l'invocazione e' per riferimento.
	 * 
	 */
	protected boolean consegnaRispostaAsincronaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		//	  Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getRispostaAsincrona()==null)
			return false;
		return CostantiConfigurazione.ABILITATO.equals(sa.getRispostaAsincrona().getInvioPerRiferimento());
	}

	/**
	 * Restituisce l'indicazione se deve essere ricevuta una risposto per riferimento.
	 *
	 * @param sa ServizioApplicativo
	 * @return true se l'invocazione e' per riferimento.
	 * 
	 */
	protected boolean consegnaRispostaAsincronaRispostaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getRispostaAsincrona()==null)
			return false;
		return CostantiConfigurazione.ABILITATO.equals(sa.getRispostaAsincrona().getRispostaPerRiferimento());
	}

	
	
	
	


















	/* ********  CONFIGURAZIONE  ******** */

	/**
	 * Restituisce le informazioni necessarie alla porta di dominio per accedere ai registri di servizio.
	 *
	 * @return informazioni sui registri di servizio.
	 * 
	 */
	private static AccessoRegistro accessoRegistroServizi = null;
	private static Boolean accessoRegistroServiziLetto = false;
	protected AccessoRegistro getAccessoRegistroServizi(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.accessoRegistroServiziLetto==false){
			AccessoRegistro configRegistro = null;
			try{
				configRegistro = this.configurazionePdD.getAccessoRegistro(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("getAccessoRegistroServizi (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("getAccessoRegistroServizi",e);
			}

			ConfigurazionePdDReader.accessoRegistroServizi = configRegistro;
			ConfigurazionePdDReader.accessoRegistroServiziLetto = true;
		}

		/*
		if(ConfigurazionePdDReader.accessoRegistroServizi.getCache()==null){
			System.out.println("REGISTRO CACHE DISABILITATA");
		}else{
			System.out.println("REGISTRO CACHE ABILITATA");
			System.out.println("REGISTRO CACHE ALGORITMO: "+ConfigurazionePdDReader.accessoRegistroServizi.getCache().getAlgoritmo());
			System.out.println("REGISTRO CACHE DIMENSIONE: "+ConfigurazionePdDReader.accessoRegistroServizi.getCache().getDimensione());
			System.out.println("REGISTRO CACHE ITEM IDLE: "+ConfigurazionePdDReader.accessoRegistroServizi.getCache().getItemIdleTime());
			System.out.println("REGISTRO CACHE ITEM LIFE SECOND: "+ConfigurazionePdDReader.accessoRegistroServizi.getCache().getItemLifeSecond());
		}
		
		for(int i=0; i<ConfigurazionePdDReader.accessoRegistroServizi.sizeRegistroList(); i++){
			System.out.println("REGISTRO ["+i+"] NOME ["+ConfigurazionePdDReader.accessoRegistroServizi.getRegistro(i).getNome()+"]");
			System.out.println("REGISTRO ["+i+"] TIPO ["+ConfigurazionePdDReader.accessoRegistroServizi.getRegistro(i).getTipo()+"]");
			System.out.println("REGISTRO ["+i+"] LOCATION ["+ConfigurazionePdDReader.accessoRegistroServizi.getRegistro(i).getLocation()+"]");
			System.out.println("REGISTRO ["+i+"] USER ["+ConfigurazionePdDReader.accessoRegistroServizi.getRegistro(i).getUser()+"]");
			System.out.println("REGISTRO ["+i+"] PASSWORD ["+ConfigurazionePdDReader.accessoRegistroServizi.getRegistro(i).getPassword()+"]");
		}*/
		
		return ConfigurazionePdDReader.accessoRegistroServizi;
	}

	/**
	 * Restituisce le informazioni necessarie alla porta di dominio per accedere alla configurazione
	 *
	 * @return informazioni
	 * 
	 */
	private static AccessoConfigurazione accessoConfigurazione = null;
	private static Boolean accessoConfigurazioneLetto = false;
	protected AccessoConfigurazione getAccessoConfigurazione(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.accessoConfigurazioneLetto==false){
			AccessoConfigurazione tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoConfigurazione(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("getAccessoConfigurazione (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("getAccessoConfigurazione",e);
			}

			ConfigurazionePdDReader.accessoConfigurazione = tmp;
			ConfigurazionePdDReader.accessoConfigurazioneLetto = true;
		}

		/*
		if(ConfigurazionePdDReader.accessoConfigurazione.getCache()==null){
			System.out.println("ACCESSO_CONFIG CACHE DISABILITATA");
		}else{
			System.out.println("ACCESSO_CONFIG CACHE ABILITATA");
			System.out.println("ACCESSO_CONFIG CACHE ALGORITMO: "+ConfigurazionePdDReader.accessoConfigurazione.getCache().getAlgoritmo());
			System.out.println("ACCESSO_CONFIG CACHE DIMENSIONE: "+ConfigurazionePdDReader.accessoConfigurazione.getCache().getDimensione());
			System.out.println("ACCESSO_CONFIG CACHE ITEM IDLE: "+ConfigurazionePdDReader.accessoConfigurazione.getCache().getItemIdleTime());
			System.out.println("ACCESSO_CONFIG CACHE ITEM LIFE SECOND: "+ConfigurazionePdDReader.accessoConfigurazione.getCache().getItemLifeSecond());
		}
		*/
		
		return ConfigurazionePdDReader.accessoConfigurazione;
	}
	
	/**
	 * Restituisce le informazioni necessarie alla porta di dominio per accedere ai dati di autorizzazione
	 *
	 * @return informazioni
	 * 
	 */
	private static AccessoDatiAutorizzazione accessoDatiAutorizzazione = null;
	private static Boolean accessoDatiAutorizzazioneLetto = false;
	protected AccessoDatiAutorizzazione getAccessoDatiAutorizzazione(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.accessoDatiAutorizzazioneLetto==false){
			AccessoDatiAutorizzazione tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoDatiAutorizzazione(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("getAccessoDatiAutorizzazione (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("getAccessoDatiAutorizzazione",e);
			}

			ConfigurazionePdDReader.accessoDatiAutorizzazione = tmp;
			ConfigurazionePdDReader.accessoDatiAutorizzazioneLetto = true;
		}

		/*
		if(ConfigurazionePdDReader.accessoDatiAutorizzazione.getCache()==null){
			System.out.println("ACCESSO_DATI_AUTH CACHE DISABILITATA");
		}else{
			System.out.println("ACCESSO_DATI_AUTH CACHE ABILITATA");
			System.out.println("ACCESSO_DATI_AUTH CACHE ALGORITMO: "+ConfigurazionePdDReader.accessoDatiAutorizzazione.getCache().getAlgoritmo());
			System.out.println("ACCESSO_DATI_AUTH CACHE DIMENSIONE: "+ConfigurazionePdDReader.accessoDatiAutorizzazione.getCache().getDimensione());
			System.out.println("ACCESSO_DATI_AUTH CACHE ITEM IDLE: "+ConfigurazionePdDReader.accessoDatiAutorizzazione.getCache().getItemIdleTime());
			System.out.println("ACCESSO_DATI_AUTH CACHE ITEM LIFE SECOND: "+ConfigurazionePdDReader.accessoDatiAutorizzazione.getCache().getItemLifeSecond());
		}
		*/
		
		return ConfigurazionePdDReader.accessoDatiAutorizzazione;
	}
	
	/**
	 * Restituisce il tipo di validazione richiesta alla porta di dominio.
	 * Se un valore non viene impostato, il tipo ritornato sara' 'attivo'.
	 *
	 * @return la validazione richiesta a OpenSPCoop.
	 * 
	 */
	private static StatoFunzionalitaConWarning tipoValidazione = null;
	protected StatoFunzionalitaConWarning getTipoValidazione(Connection connectionPdD,String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		String tipo = this.pddProperties.getValidazioneBuste_Stato(implementazionePdDSoggetto);
		if(tipo!=null && ( 
				CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.toString().equals(tipo) || 
				CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.toString().equals(tipo) || 
				CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.toString().equals(tipo) ) 
		   ){
			if(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.toString().equals(tipo))
				return CostantiConfigurazione.STATO_CON_WARNING_ABILITATO;
			else if(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.toString().equals(tipo))
				return CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO;
			else 
				return CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY;
		}
		
		if( this.configurazioneDinamica || ConfigurazionePdDReader.tipoValidazione==null){
			
			Configurazione configurazione = null;
			try{
				configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				
				/*if(configurazione.getValidazioneBuste()!=null){
					System.out.println("VALIDAZIONE tipo["+configurazione.getValidazioneBuste().getStato()+"] Controllo["+
							configurazione.getValidazioneBuste().getControllo()+"] Profilo["+
							configurazione.getValidazioneBuste().getProfiloCollaborazione()+"] Manifest["+
							configurazione.getValidazioneBuste().getManifestAttachments()+"]");
				}*/
				
				
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("getTipoValidazione (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("getTipoValidazione",e);
			}
			if( configurazione  == null || configurazione.getValidazioneBuste()==null){
				ConfigurazionePdDReader.tipoValidazione = CostantiConfigurazione.STATO_CON_WARNING_ABILITATO;
			}else if( CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(configurazione.getValidazioneBuste().getStato())  )
				ConfigurazionePdDReader.tipoValidazione = CostantiConfigurazione.STATO_CON_WARNING_ABILITATO;
			else if( CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.equals(configurazione.getValidazioneBuste().getStato())  )
				ConfigurazionePdDReader.tipoValidazione = CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO;
			else if( CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(configurazione.getValidazioneBuste().getStato())  )
				ConfigurazionePdDReader.tipoValidazione = CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY;
			else
				ConfigurazionePdDReader.tipoValidazione = CostantiConfigurazione.STATO_CON_WARNING_ABILITATO;
			
		}

		return ConfigurazionePdDReader.tipoValidazione;
	}

	/**
	 * Restituisce true se il livello di validazione richiesta alla porta di dominio e' normale.
	 * False se il livello di validazione e' rigido.
	 * Se un valore non e' stato impostato, il livello scelto sara' 'normale' (return true).
	 *
	 * @return true se il livello di validazione richiesta a OpenSPCoop e 'normale'.
	 * 
	 */
	private static Boolean isLivelloValidazioneNormale = null;
	protected boolean isLivelloValidazioneNormale(Connection connectionPdD,String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		String value = this.pddProperties.getValidazioneBuste_Controllo(implementazionePdDSoggetto);
		if(value!=null && ( 
				CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_NORMALE.toString().equalsIgnoreCase(value) || 
				CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_RIGIDO.toString().equalsIgnoreCase(value) ) 
		   ){
			if(CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_NORMALE.toString().equalsIgnoreCase(value))
				return true;
			else 
				return false;
		}
		
		if( this.configurazioneDinamica || ConfigurazionePdDReader.isLivelloValidazioneNormale==null){
					
			Configurazione configurazione = null;
			try{
				configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("isLivelloValidazioneNormale (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("isLivelloValidazioneNormale",e);
			}
			if( configurazione  == null  || configurazione.getValidazioneBuste()==null){
				ConfigurazionePdDReader.isLivelloValidazioneNormale = true; // default is normale
			}else if( CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_NORMALE.equals(configurazione.getValidazioneBuste().getControllo())  )
				ConfigurazionePdDReader.isLivelloValidazioneNormale = true;
			else 
				ConfigurazionePdDReader.isLivelloValidazioneNormale = false;		
			
		}

		return ConfigurazionePdDReader.isLivelloValidazioneNormale;

	}

	/**
	 * Restituisce true se il livello di validazione richiesta alla porta di dominio e' rigido.
	 * False se il livello di validazione e' normale.
	 * Se un valore non e' stato impostato, il livello scelto sara' 'normale' (return false).
	 *
	 * @return true se il livello di validazione richiesta a OpenSPCoop e 'rigido'.
	 * 
	 */
	private static Boolean isLivelloValidazioneRigido = null;
	protected boolean isLivelloValidazioneRigido(Connection connectionPdD, String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		String value = this.pddProperties.getValidazioneBuste_Controllo(implementazionePdDSoggetto);
		if(value!=null && ( 
				CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_NORMALE.toString().equalsIgnoreCase(value) || 
				CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_RIGIDO.toString().equalsIgnoreCase(value) ) 
		   ){
			if(CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_RIGIDO.toString().equalsIgnoreCase(value))
				return true;
			else 
				return false;
		}
		
		if( this.configurazioneDinamica || ConfigurazionePdDReader.isLivelloValidazioneRigido==null){
				
			Configurazione configurazione = null;
			try{
				configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("isLivelloValidazioneRigido (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("isLivelloValidazioneRigido",e);
			}
			if( configurazione  == null  || configurazione.getValidazioneBuste()==null){
				ConfigurazionePdDReader.isLivelloValidazioneRigido = false; // default is normale
			}else if( CostantiConfigurazione.VALIDAZIONE_PROTOCOL_LIVELLO_RIGIDO.equals(configurazione.getValidazioneBuste().getControllo())  )
				ConfigurazionePdDReader.isLivelloValidazioneRigido = true;
			else
				ConfigurazionePdDReader.isLivelloValidazioneRigido = false;
			
		}

		return ConfigurazionePdDReader.isLivelloValidazioneRigido;
	}

	/**
	 * Restituisce l'indicazione se il profilo di collaborazione deve essere validato controllando
	 * il profilo registrato nel registro.
	 * Se un valore non viene impostato, il tipo ritornato sara' 'disabilitato'.
	 *
	 * @return la validazione del profilo di collaborazione richiesta a OpenSPCoop.
	 * 
	 */
	private static Boolean validazioneProfiloCollaborazione = null;
	protected boolean isValidazioneProfiloCollaborazione(Connection connectionPdD, String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		String value = this.pddProperties.getValidazioneBuste_ProfiloCollaborazione(implementazionePdDSoggetto);
		if(value!=null && ( 
				CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(value) || 
				CostantiConfigurazione.DISABILITATO.toString().equalsIgnoreCase(value) ) 
		   ){
			if(CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(value))
				return true;
			else 
				return false;
		}
		
		if( this.configurazioneDinamica || ConfigurazionePdDReader.validazioneProfiloCollaborazione==null){
			
			Configurazione configurazione = null;
			try{
				configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("isValidazioneProfiloCollaborazione (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("isValidazioneProfiloCollaborazione",e);
			}
			if(configurazione == null || configurazione.getValidazioneBuste()==null)
				ConfigurazionePdDReader.validazioneProfiloCollaborazione = false; //default: CostantiConfigurazione.DISABILITATO;
			else if(CostantiConfigurazione.ABILITATO.equals(configurazione.getValidazioneBuste().getProfiloCollaborazione()))
				ConfigurazionePdDReader.validazioneProfiloCollaborazione = true;
			else
				ConfigurazionePdDReader.validazioneProfiloCollaborazione = false; //default: CostantiConfigurazione.DISABILITATO;
			
		}

		return ConfigurazionePdDReader.validazioneProfiloCollaborazione;
	}

	/**
	 * Restituisce true se la porta di dominio deve validare il manifest di eventuali messaggi con attachments. 
	 *
	 * @return Restituisce true se la porta di dominio deve validare il manifest di eventuali messaggi con attachments. 
	 * 
	 */
	private static Boolean validazioneManifestAttachments = null;
	protected boolean isValidazioneManifestAttachments(Connection connectionPdD, String implementazionePdDSoggetto){

		// ovverriding per implementazione porta di dominio
		String value = this.pddProperties.getValidazioneBuste_ManifestAttachments(implementazionePdDSoggetto);
		if(value!=null && ( 
				CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(value) || 
				CostantiConfigurazione.DISABILITATO.toString().equalsIgnoreCase(value) ) 
		   ){
			if(CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(value))
				return true;
			else 
				return false;
		}
		
		if( this.configurazioneDinamica || ConfigurazionePdDReader.validazioneManifestAttachments==null){
			
			Configurazione configurazione = null;
			try{
				configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("isValidazioneManifestAttachments (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.debug("isValidazioneManifestAttachments",e);
			}
			if(configurazione == null || configurazione.getValidazioneBuste()==null)
				ConfigurazionePdDReader.validazioneManifestAttachments = true; //default: CostantiConfigurazione.ABILITATO;
			else if(CostantiConfigurazione.DISABILITATO.equals(configurazione.getValidazioneBuste().getManifestAttachments()))
				ConfigurazionePdDReader.validazioneManifestAttachments = false;
			else
				ConfigurazionePdDReader.validazioneManifestAttachments = true; //default: CostantiConfigurazione.ABILITATO;
			
		}

		return ConfigurazionePdDReader.validazioneManifestAttachments;
	}

	/**
	 * Restituisce l'indicazione sulla connettivita' della PdD, in particolar modo sulla modalita' 
	 * di invio di risposte, la quale puo' variare da invio
	 * nella reply della connessione (return false) o su di una nuova connessione (return true)
	 *
	 * @return true se viene utilizzata una nuova connessione per effettuare l'invio di eventuali risposte.
	 * 
	 */
	private static Boolean newConnectionForResponse = null;
	protected boolean newConnectionForResponse(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.newConnectionForResponse==null){
			Configurazione configurazione = null;
			try{
				configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				
				/*if(configurazione.getRisposte()!=null){
					System.out.println("NEW CONNECTION FOR RESPONSE ["+configurazione.getRisposte().getConnessione()+"]");
				}*/
				
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("newConnectionForResponse (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("newConnectionForResponse",e);
			}
			if(configurazione == null || configurazione.getRisposte()==null)
				ConfigurazionePdDReader.newConnectionForResponse = false; //default: CostantiConfigurazione.CONNECTION_REPLY;
			else if(CostantiConfigurazione.NEW_CONNECTION.equals(configurazione.getRisposte().getConnessione()))
				ConfigurazionePdDReader.newConnectionForResponse = true;
			else
				ConfigurazionePdDReader.newConnectionForResponse = false; //default: CostantiConfigurazione.CONNECTION_REPLY;
		}

		return ConfigurazionePdDReader.newConnectionForResponse;
	}

	/**
	 * Restituisce true se la porta di dominio deve utilizzare eventuali indirizzi telematici presenti nelle buste ricevute. 
	 *
	 * @return true se la porta di dominio deve utilizzare eventuali indirizzi telematici presenti nelle buste.
	 * 
	 */
	private static Boolean utilizzoIndirizzoRisposta = null;
	protected boolean isUtilizzoIndirizzoTelematico(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.utilizzoIndirizzoRisposta==null){
			Configurazione configurazione = null;
			try{
				configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				
				/*if(configurazione.getIndirizzoRisposta()!=null){
					System.out.println("INDIRIZZO TELEMATICO ["+configurazione.getIndirizzoRisposta().getUtilizzo()+"]");
				}*/
				
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("isUtilizzoIndirizzoRisposta (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("isUtilizzoIndirizzoRisposta",e);
			}
			if(configurazione == null || configurazione.getIndirizzoRisposta() == null)
				ConfigurazionePdDReader.utilizzoIndirizzoRisposta = false; //default: CostantiConfigurazione.DISABILITATO;
			else if(CostantiConfigurazione.ABILITATO.equals(configurazione.getIndirizzoRisposta().getUtilizzo()))
				ConfigurazionePdDReader.utilizzoIndirizzoRisposta = true;
			else
				ConfigurazionePdDReader.utilizzoIndirizzoRisposta = false; //default: CostantiConfigurazione.DISABILITATO;
		}

		return ConfigurazionePdDReader.utilizzoIndirizzoRisposta;
	}

	/**
	 * Restituisce true se la porta di dominio deve gestire (creare/eliminare) manifest di eventuali messaggi con attachments. 
	 *
	 * @return Restituisce true se la porta di dominio deve gestire (creare/eliminare) manifest di eventuali messaggi con attachments. 
	 * 
	 */
	private static Boolean gestioneManifestAttachments = null;
	protected boolean isGestioneManifestAttachments(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.gestioneManifestAttachments==null){
			Configurazione configurazione = null;
			try{
				configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				
				/*if(configurazione.getAttachments()!=null){
					System.out.println("GESTIONE MANIFEST ["+configurazione.getAttachments().getGestioneManifest()+"]");
				}*/
				
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("isGestioneManifestAttachments (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("isGestioneManifestAttachments",e);
			}
			if(configurazione == null || configurazione.getAttachments()==null)
				ConfigurazionePdDReader.gestioneManifestAttachments = false;  //default: CostantiConfigurazione.DISABILITATO;
			else if(CostantiConfigurazione.ABILITATO.equals(configurazione.getAttachments().getGestioneManifest()))
				ConfigurazionePdDReader.gestioneManifestAttachments = true;
			else
				ConfigurazionePdDReader.gestioneManifestAttachments = false;  //default: CostantiConfigurazione.DISABILITATO;
		}

		return ConfigurazionePdDReader.gestioneManifestAttachments;
	}

	/**
	 * Restituisce i minuti dopo i quali una busta non riscontrata sara' rispedita.
	 * (nel caso in cui nella busta sia richiesto una confermaRicezione) 
	 *
	 * @return timeout riscontro.
	 * 
	 */
	private static Long timeoutRiscontro = null;
	protected long getTimeoutRiscontro(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.timeoutRiscontro==null){
			Configurazione configurazione = null;
			try{
				configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				
				/*if(configurazione.getInoltroBusteNonRiscontrate()!=null){
					System.out.println("RISCONTRI ["+configurazione.getInoltroBusteNonRiscontrate().getCadenza()+"]");
				}*/
				
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("getTimeoutRiscontro (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("getTimeoutRiscontro",e);
			}
			if(configurazione == null || configurazione.getInoltroBusteNonRiscontrate()==null)
				ConfigurazionePdDReader.timeoutRiscontro = -1L;
			else if(configurazione.getInoltroBusteNonRiscontrate().getCadenza() == null)
				ConfigurazionePdDReader.timeoutRiscontro = -1L;
			else{
				Long longValue = null;
				try{
					longValue = new Long(configurazione.getInoltroBusteNonRiscontrate().getCadenza());
				}catch(Exception e){
					//	Conversione fallita
				}

				if(longValue == null)
					ConfigurazionePdDReader.timeoutRiscontro = -1L;
				else
					ConfigurazionePdDReader.timeoutRiscontro = longValue;
			}
		}

		return ConfigurazionePdDReader.timeoutRiscontro;
	}


	/**
	 * Restituisce il Livello per il logger dei messaggi diagnostici. 
	 *
	 * @return Il livello di Log4J per il logger dei messaggi diagnostici (di default ritorna ALL). 
	 * 
	 */
	private static Level livello_msgDiagnostici = null;
	public static Level livello_msgDiagnosticiJMX = null;
	protected Level getLivello_msgDiagnostici(Connection connectionPdD){

		if(ConfigurazionePdDReader.livello_msgDiagnosticiJMX!=null)
			return ConfigurazionePdDReader.livello_msgDiagnosticiJMX;

		if( this.configurazioneDinamica || ConfigurazionePdDReader.livello_msgDiagnostici==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);					
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getLivello_msgDiagnostici (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getLivello_msgDiagnostici",e);
				}

				if(configurazione!=null && configurazione.getMessaggiDiagnostici()!=null){
					String readLevel = configurazione.getMessaggiDiagnostici().getSeverita().toString();	   
					ConfigurazionePdDReader.livello_msgDiagnostici = LogLevels.toLog4J(readLevel);
				}else{
					ConfigurazionePdDReader.livello_msgDiagnostici = LogLevels.LOG_LEVEL_INFO_PROTOCOL;
				}

			}catch(Exception e){
				ConfigurazionePdDReader.livello_msgDiagnostici = LogLevels.LOG_LEVEL_INFO_PROTOCOL;
			}
		}

		return ConfigurazionePdDReader.livello_msgDiagnostici;
	}

	/**
	 * Restituisce il Livello per il logger dei messaggi diagnostici in stile 'openspcoop'. 
	 *
	 * @return Il livello di Log4J per il logger dei messaggi diagnostici 'openspcoop' (di default ritorna OFF). 
	 * 
	 */
	private static Level livelloLog4J_msgDiagnostici = null;
	public static Level livelloLog4J_msgDiagnosticiJMX = null;
	protected Level getLivelloLog4J_msgDiagnostici(Connection connectionPdD){

		if(ConfigurazionePdDReader.livelloLog4J_msgDiagnosticiJMX!=null){
			return ConfigurazionePdDReader.livelloLog4J_msgDiagnosticiJMX;
		}

		if( this.configurazioneDinamica || ConfigurazionePdDReader.livelloLog4J_msgDiagnostici==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getLivelloLog4J_msgDiagnostici (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getLivelloLog4J_msgDiagnostici",e);
				}

				if(configurazione!=null && configurazione.getMessaggiDiagnostici()!=null){
					String readLevel =  configurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();	   
					ConfigurazionePdDReader.livelloLog4J_msgDiagnostici = LogLevels.toLog4J(readLevel);
				}else{
					ConfigurazionePdDReader.livelloLog4J_msgDiagnostici = LogLevels.LOG_LEVEL_INFO_PROTOCOL;
				}

			}catch(Exception e){
				ConfigurazionePdDReader.livelloLog4J_msgDiagnostici = LogLevels.LOG_LEVEL_INFO_PROTOCOL;
			}
		}

		return ConfigurazionePdDReader.livelloLog4J_msgDiagnostici;
	}

	/**
	 * Restituisce il Livello per il logger dei messaggi diagnostici . 
	 *
	 * @return Il livello per il logger dei messaggi diagnostici  (di default ritorna ALL). 
	 * 
	 */
	private static Integer severita_msgDiagnostici = null;
	public static Integer severita_msgDiagnosticiJMX = null;
	protected int getSeverita_msgDiagnostici(Connection connectionPdD){

		if(ConfigurazionePdDReader.severita_msgDiagnosticiJMX!=null){
			return ConfigurazionePdDReader.severita_msgDiagnosticiJMX;
		}

		if( this.configurazioneDinamica || ConfigurazionePdDReader.severita_msgDiagnostici==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
										
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getSeverita_msgDiagnostici (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getSeverita_msgDiagnostici",e);
				}

				if(configurazione!=null && configurazione.getMessaggiDiagnostici()!=null){
					String readLevel = configurazione.getMessaggiDiagnostici().getSeverita().toString();	   
					ConfigurazionePdDReader.severita_msgDiagnostici = LogLevels.toOpenSPCoop2(readLevel);
				}else{
					ConfigurazionePdDReader.severita_msgDiagnostici = LogLevels.SEVERITA_INFO_PROTOCOL;
				}

			}catch(Exception e){
				ConfigurazionePdDReader.severita_msgDiagnostici = LogLevels.SEVERITA_INFO_PROTOCOL;
			}
		}

		return ConfigurazionePdDReader.severita_msgDiagnostici;
	}

	/**
	 * Restituisce il Livello per il logger dei messaggi diagnostici che finiscono su log4j
	 *
	 * @return Il livello per il logger dei messaggi diagnostici che finiscono su log4j (di default ritorna OFF). 
	 * 
	 */
	private static Integer severitaLog4J_msgDiagnostici = null;
	public static Integer severitaLog4J_msgDiagnosticiJMX = null;
	public static Integer getSeveritaLog4J_msgDiagnosticiJMX() {
		return severitaLog4J_msgDiagnosticiJMX;
	}
	public static void setSeveritaLog4J_msgDiagnosticiJMX(
			Integer severitaLog4J_msgDiagnosticiJMX) {
		ConfigurazionePdDReader.severitaLog4J_msgDiagnosticiJMX = severitaLog4J_msgDiagnosticiJMX;
	}
	protected int getSeveritaLog4J_msgDiagnostici(Connection connectionPdD){

		if(ConfigurazionePdDReader.severitaLog4J_msgDiagnosticiJMX!=null){
			return ConfigurazionePdDReader.severitaLog4J_msgDiagnosticiJMX;
		}

		if( this.configurazioneDinamica || ConfigurazionePdDReader.severitaLog4J_msgDiagnostici==null){
			try{

				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getSeveritaOpenSPCoop_msgDiagnostici (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getSeveritaOpenSPCoop_msgDiagnostici",e);
				}

				if(configurazione!=null && configurazione.getMessaggiDiagnostici()!=null){
					String readLevel = configurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();	   
					ConfigurazionePdDReader.severitaLog4J_msgDiagnostici = LogLevels.toOpenSPCoop2(readLevel);
				}else{
					ConfigurazionePdDReader.severitaLog4J_msgDiagnostici = LogLevels.SEVERITA_INFO_PROTOCOL;
				}

			}catch(Exception e){
				ConfigurazionePdDReader.severitaLog4J_msgDiagnostici = LogLevels.SEVERITA_INFO_PROTOCOL;
			}
		}

		return ConfigurazionePdDReader.severitaLog4J_msgDiagnostici;
	}

	/**
	 * Restituisce gli appender personalizzati per la registrazione dei messaggi diagnostici. 
	 *
	 * @return Restituisce gli appender personalizzati per la registrazione dei messaggi diagnostici. 
	 */
	private static MessaggiDiagnostici openSPCoopAppender_MsgDiagnostici = null;
	private static Boolean openSPCoopAppender_MsgDiagnosticiLetto = false;
	protected MessaggiDiagnostici getOpenSPCoopAppender_MsgDiagnostici(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.openSPCoopAppender_MsgDiagnosticiLetto==false){
			try{

				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getOpenSPCoopAppender_MsgDiagnostici (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getOpenSPCoopAppender_MsgDiagnostici",e);
				}
				if(configurazione!=null)
					ConfigurazionePdDReader.openSPCoopAppender_MsgDiagnostici = configurazione.getMessaggiDiagnostici();
				else
					ConfigurazionePdDReader.openSPCoopAppender_MsgDiagnostici = null;

			}catch(Exception e){
				ConfigurazionePdDReader.openSPCoopAppender_MsgDiagnostici = null;
			}
			ConfigurazionePdDReader.openSPCoopAppender_MsgDiagnosticiLetto = true;
		}

		return ConfigurazionePdDReader.openSPCoopAppender_MsgDiagnostici;
	}


	/**
	 * Restituisce l'indicazione se effettuare o meno il dump dei messaggi. 
	 *
	 * @return Restituisce l'indicazione se effettuare o meno il dump dei messaggi.  (di default ritorna 'disabilitato'). 
	 * 
	 */
	private static Boolean dumpMessaggi = null;
	public static Boolean dumpMessaggiJMX = null;
	protected boolean dumpMessaggi(Connection connectionPdD){

		if(ConfigurazionePdDReader.dumpMessaggiJMX!=null)
			return ConfigurazionePdDReader.dumpMessaggiJMX;

		if( this.configurazioneDinamica || ConfigurazionePdDReader.dumpMessaggi==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("dumpMessaggi (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("dumpMessaggi",e);
				}

				if(configurazione!=null && configurazione.getTracciamento()!=null){
					StatoFunzionalita read = configurazione.getTracciamento().getDump();	   
					if(CostantiConfigurazione.ABILITATO.equals(read))
						ConfigurazionePdDReader.dumpMessaggi = true;
					else
						ConfigurazionePdDReader.dumpMessaggi = false;
				}else{
					ConfigurazionePdDReader.dumpMessaggi = false; // default CostantiConfigurazione.DISABILITATO
				}

			}catch(Exception e){
				ConfigurazionePdDReader.dumpMessaggi = false; // default CostantiConfigurazione.DISABILITATO
			}
		}

		return ConfigurazionePdDReader.dumpMessaggi;
	}
	
	private static Boolean dumpBinarioPD = null;
	public static Boolean dumpBinarioPDJMX = null;
	protected boolean dumpBinarioPD(Connection connectionPdD){

		if(ConfigurazionePdDReader.dumpBinarioPDJMX!=null)
			return ConfigurazionePdDReader.dumpBinarioPDJMX;

		if( this.configurazioneDinamica || ConfigurazionePdDReader.dumpBinarioPD==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("dumpBinarioPD (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("dumpBinarioPD",e);
				}

				if(configurazione!=null && configurazione.getTracciamento()!=null){
					StatoFunzionalita read = configurazione.getTracciamento().getDumpBinarioPortaDelegata();	   
					if(CostantiConfigurazione.ABILITATO.equals(read))
						ConfigurazionePdDReader.dumpBinarioPD = true;
					else
						ConfigurazionePdDReader.dumpBinarioPD = false;
				}else{
					ConfigurazionePdDReader.dumpBinarioPD = false; // default CostantiConfigurazione.DISABILITATO
				}

			}catch(Exception e){
				ConfigurazionePdDReader.dumpBinarioPD = false; // default CostantiConfigurazione.DISABILITATO
			}
		}

		return ConfigurazionePdDReader.dumpBinarioPD;
	}
	
	
	private static Boolean dumpBinarioPA = null;
	public static Boolean dumpBinarioPAJMX = null;
	protected boolean dumpBinarioPA(Connection connectionPdD){

		if(ConfigurazionePdDReader.dumpBinarioPAJMX!=null)
			return ConfigurazionePdDReader.dumpBinarioPAJMX;

		if( this.configurazioneDinamica || ConfigurazionePdDReader.dumpBinarioPA==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("dumpBinarioPA (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("dumpBinarioPA",e);
				}

				if(configurazione!=null && configurazione.getTracciamento()!=null){
					StatoFunzionalita read = configurazione.getTracciamento().getDumpBinarioPortaApplicativa();	   
					if(CostantiConfigurazione.ABILITATO.equals(read))
						ConfigurazionePdDReader.dumpBinarioPA = true;
					else
						ConfigurazionePdDReader.dumpBinarioPA = false;
				}else{
					ConfigurazionePdDReader.dumpBinarioPA = false; // default CostantiConfigurazione.DISABILITATO
				}

			}catch(Exception e){
				ConfigurazionePdDReader.dumpBinarioPA = false; // default CostantiConfigurazione.DISABILITATO
			}
		}

		return ConfigurazionePdDReader.dumpBinarioPA;
	}


	public static Boolean tracciamentoBusteJMX = null;
	private static Boolean tracciamentoBuste = null;
	/**
	 * Restituisce l'indicazione se effettuare o meno il tracciamento delle buste. 
	 *
	 * @return Restituisce l'indicazione se effettuare o meno il tracciamento delle buste.  (di default ritorna 'abilitato'). 
	 * 
	 */
	protected boolean tracciamentoBuste(Connection connectionPdD){

		if(ConfigurazionePdDReader.tracciamentoBusteJMX!=null)
			return ConfigurazionePdDReader.tracciamentoBusteJMX;

		if( this.configurazioneDinamica || ConfigurazionePdDReader.tracciamentoBuste==null){
			try{

				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
					
					/*if(configurazione.getTracciamento()!=null){
						System.out.println("TRACCIAMENTO BUSTE ["+configurazione.getTracciamento().getBuste()+"]");
						System.out.println("TRACCIAMENTO DUMP ["+configurazione.getTracciamento().getDump()+"]");
						for(int i=0;i<configurazione.getTracciamento().sizeOpenspcoopAppenderList();i++){
							System.out.println("TRACCIAMENTO APPENDER ["+i+"] tipo["+configurazione.getTracciamento().getOpenspcoopAppender(i).getTipo()+"]");
							List<org.openspcoop2.core.config.Property> properties = configurazione.getTracciamento().getOpenspcoopAppender(i).getPropertyList();
							if(properties!=null){
								for (int j = 0; j < properties.size(); j++) {
									System.out.println("TRACCIAMENTO APPENDER ["+i+"] NOME["+properties.get(j).getNome()+"]=VALORE["+properties.get(j).getValore()+"]");
								}
							}
						}
					}*/
					
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("tracciamentoBuste (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("tracciamentoBuste",e);
				}

				if(configurazione!=null && configurazione.getTracciamento()!=null){
					StatoFunzionalita read = configurazione.getTracciamento().getBuste();	   
					if(CostantiConfigurazione.DISABILITATO.equals(read))
						ConfigurazionePdDReader.tracciamentoBuste = false;
					else
						ConfigurazionePdDReader.tracciamentoBuste = true;
				}else{
					ConfigurazionePdDReader.tracciamentoBuste = true; // default ConfigReader.ABILITATO
				}
			}catch(Exception e){
				ConfigurazionePdDReader.tracciamentoBuste = true; // default ConfigReader.ABILITATO
			}
		}

		return ConfigurazionePdDReader.tracciamentoBuste;
	}

	/**
	 * Restituisce gli appender personalizzati per la registrazione delle buste. 
	 *
	 * @return Restituisce gli appender personalizzati per la registrazione delle buste. 
	 */
	private static Tracciamento openSPCoopAppender_Tracciamento = null;
	private static Boolean openSPCoopAppender_TracciamentoLetto = false;
	protected Tracciamento getOpenSPCoopAppender_Tracciamento(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.openSPCoopAppender_TracciamentoLetto==false){
			try{

				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getOpenSPCoopAppender_Tracciamento (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getOpenSPCoopAppender_Tracciamento",e);
				}

				if(configurazione!=null)
					ConfigurazionePdDReader.openSPCoopAppender_Tracciamento = configurazione.getTracciamento();	   

			}catch(Exception e){
				ConfigurazionePdDReader.openSPCoopAppender_Tracciamento = null;
			}
			ConfigurazionePdDReader.openSPCoopAppender_TracciamentoLetto = true;
		}

		return ConfigurazionePdDReader.openSPCoopAppender_Tracciamento;
	}

	/**
	 * Restituisce la politica di gestione della consegna tramite connettore per il componente di cooperazione
	 *
	 * @return la politica di gestione della consegna tramite connettore per il componente di cooperazione
	 * 
	 */
	private static GestioneErrore gestioneErroreConnettoreComponenteCooperazione = null;
	protected GestioneErrore getGestioneErroreConnettoreComponenteCooperazione(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazione==null){
			try{
				GestioneErrore gestione = null;
				try{
					gestione = this.configurazionePdD.getGestioneErroreComponenteCooperazione(connectionPdD);
					
					/*if(gestione!=null){
						System.out.println("GESTIONE COOPERAZIONE PRESENTE");
						System.out.println("COMPORTAMENTO: "+gestione.getComportamento());		
						System.out.println("CADENZA: "+gestione.getCadenzaRispedizione());
						for(int i=0;i<gestione.sizeCodiceTrasportoList();i++){
							System.out.println("TRASPORTO MIN["+gestione.getCodiceTrasporto(i).getValoreMinimo()
									+"] MAX["+gestione.getCodiceTrasporto(i).getValoreMassimo()
									+"] COMPORTMANETO["+gestione.getCodiceTrasporto(i).getComportamento()
									+"] CADENZA["+gestione.getCodiceTrasporto(i).getCadenzaRispedizione()
									+"]" );
						}
						for(int i=0;i<gestione.sizeSoapFaultList();i++){
							System.out.println("SOAP CODE["+gestione.getSoapFault(i).getFaultCode()
									+"] ACTOR["+gestione.getSoapFault(i).getFaultActor()
									+"] STRING["+gestione.getSoapFault(i).getFaultString()
									+"] COMPORTMANETO["+gestione.getSoapFault(i).getComportamento()
									+"] CADENZA["+gestione.getSoapFault(i).getCadenzaRispedizione()
									+"]" );
						}
					}*/
					
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getGestioneErroreConnettoreComponenteCooperazione (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getGestioneErroreConnettoreComponenteCooperazione",e);
				}
				if(gestione == null)
					ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazione = GestoreErroreConnettore.getGestioneErroreDefaultComponenteCooperazione();
				else
					ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazione = gestione;

			}catch(Exception e){
				ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazione = GestoreErroreConnettore.getGestioneErroreDefaultComponenteCooperazione();
			}  	
		}

		return ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazione;

	}

	/**
	 * Restituisce la politica di gestione della consegna tramite connettore per il componente di integrazione
	 *
	 * @return la politica di gestione della consegna tramite connettore per il componente di integrazione
	 * 
	 */
	private static GestioneErrore gestioneErroreConnettoreComponenteIntegrazione = null;
	protected GestioneErrore getGestioneErroreConnettoreComponenteIntegrazione(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazione==null){
			try{
				GestioneErrore gestione = null;
				try{
					gestione = this.configurazionePdD.getGestioneErroreComponenteIntegrazione(connectionPdD);
					
					/*if(gestione!=null){
						System.out.println("GESTIONE INTEGRAZIONE PRESENTE");
						System.out.println("COMPORTAMENTO: "+gestione.getComportamento());		
						System.out.println("CADENZA: "+gestione.getCadenzaRispedizione());
						for(int i=0;i<gestione.sizeCodiceTrasportoList();i++){
							System.out.println("TRASPORTO MIN["+gestione.getCodiceTrasporto(i).getValoreMinimo()
									+"] MAX["+gestione.getCodiceTrasporto(i).getValoreMassimo()
									+"] COMPORTMANETO["+gestione.getCodiceTrasporto(i).getComportamento()
									+"] CADENZA["+gestione.getCodiceTrasporto(i).getCadenzaRispedizione()
									+"]" );
						}
						for(int i=0;i<gestione.sizeSoapFaultList();i++){
							System.out.println("SOAP CODE["+gestione.getSoapFault(i).getFaultCode()
									+"] ACTOR["+gestione.getSoapFault(i).getFaultActor()
									+"] STRING["+gestione.getSoapFault(i).getFaultString()
									+"] COMPORTMANETO["+gestione.getSoapFault(i).getComportamento()
									+"] CADENZA["+gestione.getSoapFault(i).getCadenzaRispedizione()
									+"]" );
						}
					}*/
					
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getGestioneErroreConnettoreComponenteIntegrazione (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getGestioneErroreConnettoreComponenteIntegrazione",e);
				}
				if(gestione == null)
					ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazione = GestoreErroreConnettore.getGestioneErroreDefaultComponenteIntegrazione();
				else
					ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazione = gestione;

			}catch(Exception e){
				ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazione = GestoreErroreConnettore.getGestioneErroreDefaultComponenteIntegrazione();
			}  	
		}

		return ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazione;

	}



	/**
	 * Restituisce il tipo di autenticazione associato al servizio IntegrationManager
	 *
	 * @return Restituisce il tipo di autenticazione associato al servizio IntegrationManager (di default ritorna 'ssl'). 
	 * 
	 */
	private static String[] integrationManagerAuthentication = null;
	protected String[] getIntegrationManagerAuthentication(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.integrationManagerAuthentication==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
					
					/*if(configurazione.getIntegrationManager()!=null){
						System.out.println("IM ["+configurazione.getIntegrationManager().getAutenticazione()+"]");
					}*/
					
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getIntegrationManagerAuthentication (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getIntegrationManagerAuthentication",e);
				}
				if(configurazione == null || configurazione.getIntegrationManager()==null || configurazione.getIntegrationManager().getAutenticazione()==null){
					ConfigurazionePdDReader.integrationManagerAuthentication = new String [] { CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString(),
							CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_SSL.toString() };
				}else {
					
					String [] values =  configurazione.getIntegrationManager().getAutenticazione().split(",");
					Vector<String> v = new Vector<String>();
					ClassNameProperties classNameProperties = ClassNameProperties.getInstance();
					for(int i=0; i<values.length; i++){
						values[i] = values[i].trim();
						if(classNameProperties.getAutenticazione(values[i])==null){
							this.log.error("Meccanismo di autenticazione ["+values[i]+"] non registrato nella Porta di Dominio");
						}else{
							v.add(values[i]);
						}
					}
					if(v.size()>0)
						ConfigurazionePdDReader.integrationManagerAuthentication = v.toArray(new String[1]);
					else
						throw new Exception("Nessun meccanismo di autenticazione valido tra quelli esistenti");
				}

			}catch(Exception e){
				this.log.error("Errore durante la lettura del tipo di autenticazione associato al servizio di IntegrationManager: "+e.getMessage());
				ConfigurazionePdDReader.integrationManagerAuthentication = new String [] { CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString(),
						CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_SSL.toString() };
			}
		}

		return ConfigurazionePdDReader.integrationManagerAuthentication;

	}

	
	/**
	 * Restituisce il tipo di validazione xsd  attiva nella porta
	 * 
	 * @return Restituisce il tipo di validazione xsd attiva nella porta
	 * 
	 */
	private static ValidazioneContenutiApplicativi validazioneContenutiApplicativi = null;
	protected ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(Connection connectionPdD, String implementazionePdDSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		ValidazioneContenutiApplicativi valDefault = new ValidazioneContenutiApplicativi();
		valDefault.setStato(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO);
		valDefault.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD);
		
		// ovverriding per implementazione porta di dominio
		String stato = this.pddProperties.getValidazioneContenutiApplicativi_Stato(implementazionePdDSoggetto);
		String tipo = this.pddProperties.getValidazioneContenutiApplicativi_Tipo(implementazionePdDSoggetto);
		String acceptMtomMessage = this.pddProperties.getValidazioneContenutiApplicativi_AcceptMtomMessage(implementazionePdDSoggetto);
		boolean pddPropertiesPresente = false;
		if(stato!=null  && ( 
				CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.toString().equalsIgnoreCase(stato) || 
				CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.toString().equalsIgnoreCase(stato) || 
				CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.toString().equalsIgnoreCase(stato) ) ){
			if(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.toString().equalsIgnoreCase(stato))
				valDefault.setStato(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO);
			else if(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO.toString().equalsIgnoreCase(stato))
				valDefault.setStato(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO);
			else
				valDefault.setStato(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY);
			pddPropertiesPresente = true;
		}
		if(tipo!=null  && ( 
				CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.toString().equalsIgnoreCase(stato) || 
				CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.toString().equalsIgnoreCase(stato) || 
				CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.toString().equalsIgnoreCase(stato) ) ){
			if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.toString().equalsIgnoreCase(stato))
				valDefault.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL);
			else if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.toString().equalsIgnoreCase(stato))
				valDefault.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD);
			else
				valDefault.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP);
			pddPropertiesPresente = true;
		}
		if(acceptMtomMessage!=null  && ( 
				CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(acceptMtomMessage) || 
				CostantiConfigurazione.DISABILITATO.toString().equalsIgnoreCase(acceptMtomMessage) ) ){
			if(CostantiConfigurazione.ABILITATO.toString().equalsIgnoreCase(stato))
				valDefault.setAcceptMtomMessage(CostantiConfigurazione.ABILITATO);
			else if(CostantiConfigurazione.DISABILITATO.toString().equalsIgnoreCase(stato))
				valDefault.setAcceptMtomMessage(CostantiConfigurazione.DISABILITATO);
			else
				valDefault.setAcceptMtomMessage(CostantiConfigurazione.DISABILITATO);
			pddPropertiesPresente = true;
		}
		if(pddPropertiesPresente)
			return valDefault;

		
		if( this.configurazioneDinamica || ConfigurazionePdDReader.validazioneContenutiApplicativi==null){
			
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
					
					/*if(configurazione.getValidazioneContenutiApplicativi()!=null){
						System.out.println("VALIDAZIONE CONTENUTI STATO["+configurazione.getValidazioneContenutiApplicativi().getStato()+"]");
						System.out.println("VALIDAZIONE CONTENUTI TIPO["+configurazione.getValidazioneContenutiApplicativi().getTipo()+"]");
					}*/
					
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getTipoValidazioneContenutoApplicativo (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getTipoValidazioneContenutoApplicativo",e);
				}
				if(configurazione == null || configurazione.getValidazioneContenutiApplicativi()==null){
					ConfigurazionePdDReader.validazioneContenutiApplicativi = valDefault;
				}else{
					ValidazioneContenutiApplicativi val = new ValidazioneContenutiApplicativi();
					// Stato
					if(configurazione.getValidazioneContenutiApplicativi().getStato()==null){
						val.setStato(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO);
					}else if(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO.equals(configurazione.getValidazioneContenutiApplicativi().getStato())){
						val.setStato(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO);
					}else if(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY.equals(configurazione.getValidazioneContenutiApplicativi().getStato())){
						val.setStato(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY);
					}else{
						val.setStato(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO);
					}
					// Tipo
					if(configurazione.getValidazioneContenutiApplicativi().getTipo()==null){
						val.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD);
					}else if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL.equals(configurazione.getValidazioneContenutiApplicativi().getTipo())){
						val.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_WSDL);
					}else if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(configurazione.getValidazioneContenutiApplicativi().getTipo())){
						val.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP);
					}else{
						val.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD);
					}
					// AcceptMtomMessage
					if(configurazione.getValidazioneContenutiApplicativi().getAcceptMtomMessage()==null){
						val.setAcceptMtomMessage(CostantiConfigurazione.DISABILITATO);
					}else if(CostantiConfigurazione.ABILITATO.equals(configurazione.getValidazioneContenutiApplicativi().getAcceptMtomMessage())){
						val.setAcceptMtomMessage(CostantiConfigurazione.ABILITATO);
					}else if(CostantiConfigurazione.DISABILITATO.equals(configurazione.getValidazioneContenutiApplicativi().getAcceptMtomMessage())){
						val.setAcceptMtomMessage(CostantiConfigurazione.DISABILITATO);
					}else{
						val.setAcceptMtomMessage(CostantiConfigurazione.DISABILITATO);
					}
					
					ConfigurazionePdDReader.validazioneContenutiApplicativi = val;
				}
			}catch(Exception e){
				ConfigurazionePdDReader.validazioneContenutiApplicativi = valDefault;
			}
		}
		return ConfigurazionePdDReader.validazioneContenutiApplicativi;
	}
	
	
	
	
	/**
	 * Restituisce l'indicazione se il servizio porta delegata e' attivo
	 *
	 * @return Restituisce l'indicazione se il servizio porta delegata e' attivo
	 * 
	 */
	private static Boolean isPDServiceActive = null;
	protected Boolean isPDServiceActive(){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.isPDServiceActive==null){
			try{
				StatoServiziPdd stato = null;
				try{
					stato = this.configurazionePdD.getStatoServiziPdD();
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("isPDServiceActive (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("isPDServiceActive",e);
				}
				if(stato == null || stato.getPortaDelegata()==null || stato.getPortaDelegata().getStato()==null){
					ConfigurazionePdDReader.isPDServiceActive = true;
				}else {					
					ConfigurazionePdDReader.isPDServiceActive = !CostantiConfigurazione.DISABILITATO.equals(stato.getPortaDelegata().getStato());
				}

			}catch(Exception e){
				this.log.error("Errore durante la lettura dell'indicazione se il servizio porta delegata e' attivo: "+e.getMessage());
				ConfigurazionePdDReader.isPDServiceActive = false;
			}
		}

		return ConfigurazionePdDReader.isPDServiceActive;

	}
	
	private static List<TipoFiltroAbilitazioneServizi> getFiltriAbilitazionePDService = null;
	protected List<TipoFiltroAbilitazioneServizi> getFiltriAbilitazionePDService(){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.isPDServiceActive==null){
			try{
				StatoServiziPdd stato = null;
				try{
					stato = this.configurazionePdD.getStatoServiziPdD();
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getFiltriAbilitazionePDService (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getFiltriAbilitazionePDService",e);
				}
				if(stato == null || stato.getPortaDelegata()==null || stato.getPortaDelegata().sizeFiltroAbilitazioneList()<=0){
					ConfigurazionePdDReader.getFiltriAbilitazionePDService = new ArrayList<TipoFiltroAbilitazioneServizi>();
				}else {					
					ConfigurazionePdDReader.getFiltriAbilitazionePDService = stato.getPortaDelegata().getFiltroAbilitazioneList();
				}

			}catch(Exception e){
				this.log.error("Errore durante la raccolta dei filtri di abilitazione PD: "+e.getMessage());
				ConfigurazionePdDReader.getFiltriAbilitazionePDService = new ArrayList<TipoFiltroAbilitazioneServizi>();
			}
		}

		return ConfigurazionePdDReader.getFiltriAbilitazionePDService;

	}
	
	private static List<TipoFiltroAbilitazioneServizi> getFiltriDisabilitazionePDService = null;
	protected List<TipoFiltroAbilitazioneServizi> getFiltriDisabilitazionePDService(){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.isPDServiceActive==null){
			try{
				StatoServiziPdd stato = null;
				try{
					stato = this.configurazionePdD.getStatoServiziPdD();
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getFiltriDisabilitazionePDService (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getFiltriDisabilitazionePDService",e);
				}
				if(stato == null || stato.getPortaDelegata()==null || stato.getPortaDelegata().sizeFiltroDisabilitazioneList()<=0){
					ConfigurazionePdDReader.getFiltriDisabilitazionePDService = new ArrayList<TipoFiltroAbilitazioneServizi>();
				}else {					
					ConfigurazionePdDReader.getFiltriDisabilitazionePDService = stato.getPortaDelegata().getFiltroDisabilitazioneList();
				}

			}catch(Exception e){
				this.log.error("Errore durante la raccolta dei filtri di disabilitazione PD: "+e.getMessage());
				ConfigurazionePdDReader.getFiltriDisabilitazionePDService = new ArrayList<TipoFiltroAbilitazioneServizi>();
			}
		}

		return ConfigurazionePdDReader.getFiltriDisabilitazionePDService;

	}
	
	
	
	/**
	 * Restituisce l'indicazione se il servizio porta applicativa e' attivo
	 *
	 * @return Restituisce l'indicazione se il servizio porta applicativa e' attivo
	 * 
	 */
	private static Boolean isPAServiceActive = null;
	protected Boolean isPAServiceActive(){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.isPAServiceActive==null){
			try{
				StatoServiziPdd stato = null;
				try{
					stato = this.configurazionePdD.getStatoServiziPdD();
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("isPAServiceActive (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("isPAServiceActive",e);
				}
				if(stato == null || stato.getPortaApplicativa()==null || stato.getPortaApplicativa().getStato()==null){
					ConfigurazionePdDReader.isPAServiceActive = true;
				}else {					
					ConfigurazionePdDReader.isPAServiceActive = !CostantiConfigurazione.DISABILITATO.equals(stato.getPortaApplicativa().getStato());
				}

			}catch(Exception e){
				this.log.error("Errore durante la lettura dell'indicazione se il servizio porta applicativa e' attivo: "+e.getMessage());
				ConfigurazionePdDReader.isPAServiceActive = false;
			}
		}

		return ConfigurazionePdDReader.isPAServiceActive;

	}
	
	private static List<TipoFiltroAbilitazioneServizi> getFiltriAbilitazionePAService = null;
	protected List<TipoFiltroAbilitazioneServizi> getFiltriAbilitazionePAService(){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.isPAServiceActive==null){
			try{
				StatoServiziPdd stato = null;
				try{
					stato = this.configurazionePdD.getStatoServiziPdD();
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getFiltriAbilitazionePAService (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getFiltriAbilitazionePAService",e);
				}
				if(stato == null || stato.getPortaApplicativa()==null || stato.getPortaApplicativa().sizeFiltroAbilitazioneList()<=0){
					ConfigurazionePdDReader.getFiltriAbilitazionePAService = new ArrayList<TipoFiltroAbilitazioneServizi>();
				}else {					
					ConfigurazionePdDReader.getFiltriAbilitazionePAService = stato.getPortaApplicativa().getFiltroAbilitazioneList();
				}

			}catch(Exception e){
				this.log.error("Errore durante la raccolta dei filtri di abilitazione PA: "+e.getMessage());
				ConfigurazionePdDReader.getFiltriAbilitazionePAService = new ArrayList<TipoFiltroAbilitazioneServizi>();
			}
		}

		return ConfigurazionePdDReader.getFiltriAbilitazionePAService;

	}
	
	private static List<TipoFiltroAbilitazioneServizi> getFiltriDisabilitazionePAService = null;
	protected List<TipoFiltroAbilitazioneServizi> getFiltriDisabilitazionePAService(){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.isPAServiceActive==null){
			try{
				StatoServiziPdd stato = null;
				try{
					stato = this.configurazionePdD.getStatoServiziPdD();
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getFiltriDisabilitazionePAService (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getFiltriDisabilitazionePAService",e);
				}
				if(stato == null || stato.getPortaApplicativa()==null || stato.getPortaApplicativa().sizeFiltroDisabilitazioneList()<=0){
					ConfigurazionePdDReader.getFiltriDisabilitazionePAService = new ArrayList<TipoFiltroAbilitazioneServizi>();
				}else {					
					ConfigurazionePdDReader.getFiltriDisabilitazionePAService = stato.getPortaApplicativa().getFiltroDisabilitazioneList();
				}

			}catch(Exception e){
				this.log.error("Errore durante la raccolta dei filtri di disabilitazione PA: "+e.getMessage());
				ConfigurazionePdDReader.getFiltriDisabilitazionePAService = new ArrayList<TipoFiltroAbilitazioneServizi>();
			}
		}

		return ConfigurazionePdDReader.getFiltriDisabilitazionePAService;

	}
	
	
	/**
	 * Restituisce l'indicazione se il servizio integration manager e' attivo
	 *
	 * @return Restituisce l'indicazione se il servizio integration manager e' attivo
	 * 
	 */
	private static Boolean isIMServiceActive = null;
	protected Boolean isIMServiceActive(){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.isIMServiceActive==null){
			try{
				StatoServiziPdd stato = null;
				try{
					stato = this.configurazionePdD.getStatoServiziPdD();
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("isIMServiceActive (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("isIMServiceActive",e);
				}
				if(stato == null || stato.getIntegrationManager()==null || stato.getIntegrationManager().getStato()==null){
					ConfigurazionePdDReader.isIMServiceActive = true;
				}else {					
					ConfigurazionePdDReader.isIMServiceActive = !CostantiConfigurazione.DISABILITATO.equals(stato.getIntegrationManager().getStato());
				}

			}catch(Exception e){
				this.log.error("Errore durante la lettura dell'indicazione se il servizio porta applicativa e' attivo: "+e.getMessage());
				ConfigurazionePdDReader.isIMServiceActive = false;
			}
		}

		return ConfigurazionePdDReader.isIMServiceActive;

	}
	
	
	
	
	
	protected StatoServiziPdd getStatoServiziPdD() throws DriverConfigurazioneException{
		try{
			return this.configurazionePdD.getStatoServiziPdD();
		}catch(DriverConfigurazioneNotFound dNot){
			return null;
		}
	}

	protected void updateStatoServiziPdD(StatoServiziPdd servizi) throws DriverConfigurazioneException{
		this.configurazionePdD.updateStatoServiziPdD(servizi);
	}
	
	
	
	
	
	
	
	protected SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneException{
		try{
			return this.configurazionePdD.getSystemPropertiesPdD();
		}catch(DriverConfigurazioneNotFound dNot){
			return null;
		}
	}

	protected void updateSystemPropertiesPdD(SystemProperties systemProperties) throws DriverConfigurazioneException{
		this.configurazionePdD.updateSystemPropertiesPdD(systemProperties);
	}
	
	
	
	// Per la configurazione realizzo due versioni.
	//
	// Il metodo 'getExtendedInfoConfigurazione' e 'getSingleExtendedInfoConfigurazione' implementa la logica classica della configurazione della PdD
	// dove le informazioni sulla policy vengono lette solamente all'avvio della PdD (se la configurazione non è dinamica)
	//
	// Il metodo 'getExtendedInfoConfigurazioneFromCache' e 'getSingleExtendedInfoConfigurazioneFromCache' implementa la logica di prelieve sempre dalla cache
	
	private static List<Object> getExtendedInfoConfigurazione = null;
	protected List<Object> getExtendedInfoConfigurazione(Connection connectionPdD) throws DriverConfigurazioneException{

		if( this.configurazioneDinamica || ConfigurazionePdDReader.getExtendedInfoConfigurazione==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getExtendedInfoConfigurazione (not found): "+e.getMessage());
				}
//				}catch(Exception e){
//					this.log.error("getExtendedInfoConfigurazione",e);
//				}
				ConfigurazionePdDReader.getExtendedInfoConfigurazione = configurazione.getExtendedInfoList();

			}catch(Exception e){
				this.log.error("Errore durante la lettura delle informazioni extra della configurazione: "+e.getMessage(),e);
				throw new DriverConfigurazioneException("Errore durante la lettura delle informazioni extra della configurazione: "+e.getMessage(),e);
			}
		}

		return ConfigurazionePdDReader.getExtendedInfoConfigurazione;

	}
	
	private static Hashtable<String, Object> getSingleExtendedInfoConfigurazione = new Hashtable<String, Object>(); 
	protected Object getSingleExtendedInfoConfigurazione(String id, Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		
		if( this.configurazioneDinamica || ConfigurazionePdDReader.getSingleExtendedInfoConfigurazione.containsKey(id)==false){
			try{
				Object result = null;
				try{
					result = this.configurazionePdD.getSingleExtendedInfoConfigurazione(id, connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getSingleExtendedInfoConfigurazione (not found): "+e.getMessage());
				}
//				}catch(Exception e){
//					this.log.error("getExtendedInfoConfigurazione",e);
//				}
				
				if(result!=null){
					ConfigurazionePdDReader.getSingleExtendedInfoConfigurazione.put(id, result);
				}

			}catch(Exception e){
				this.log.error("Errore durante la lettura delle informazioni extra con id '"+id+"' della configurazione: "+e.getMessage(),e);
				throw new DriverConfigurazioneException("Errore durante la lettura delle informazioni extra con id '"+id+"' della configurazione: "+e.getMessage(),e);
			}
		}

		return ConfigurazionePdDReader.getSingleExtendedInfoConfigurazione.get(id);

	}
	
	protected List<Object> getExtendedInfoConfigurazioneFromCache(Connection connectionPdD) throws DriverConfigurazioneException{
		try{
			Configurazione configurazione = null;
			try{
				configurazione = this.configurazionePdD.getConfigurazioneWithOnlyExtendedInfo(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("getConfigurazioneWithOnlyExtendedInfo (not found): "+e.getMessage());
			}
//			}catch(Exception e){
//				this.log.error("getExtendedInfoConfigurazione",e);
//			}
			return configurazione.getExtendedInfoList();

		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni extra della configurazione (via cache): "+e.getMessage(),e);
			throw new DriverConfigurazioneException("Errore durante la lettura delle informazioni extra della configurazione  (via cache): "+e.getMessage(),e);
		}
	}
	
	protected Object getSingleExtendedInfoConfigurazioneFromCache(String id, Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try{
			Object result = null;
			try{
				result = this.configurazionePdD.getSingleExtendedInfoConfigurazione(id, connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("getSingleExtendedInfoConfigurazioneFromCache (not found): "+e.getMessage());
			}
//			}catch(Exception e){
//				this.log.error("getExtendedInfoConfigurazione",e);
//			}
			return result;

		}catch(Exception e){
			this.log.error("Errore durante la lettura delle informazioni extra con id '"+id+"' della configurazione (via cache): "+e.getMessage(),e);
			throw new DriverConfigurazioneException("Errore durante la lettura delle informazioni extra con id '"+id+"' della configurazione  (via cache): "+e.getMessage(),e);
		}
	}
	
}
