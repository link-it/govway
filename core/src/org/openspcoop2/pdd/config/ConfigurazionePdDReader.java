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



package org.openspcoop2.pdd.config;


import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaleConfigurazioneNodo;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.InvocazioneCredenziali;
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
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneGenerale;
import org.openspcoop2.core.config.RispostaAsincrona;
import org.openspcoop2.core.config.Route;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.Ruolo;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.ServizioApplicativoRuoli;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.StatoServiziPdd;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.TipoFiltroAbilitazioneServizi;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.ValidazioneContenutiApplicativi;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.constants.RuoloTipoMatch;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.StatoFunzionalitaConWarning;
import org.openspcoop2.core.config.driver.BeanUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteApplicative;
import org.openspcoop2.core.config.driver.FiltroRicercaPorteDelegate;
import org.openspcoop2.core.config.driver.FiltroRicercaServiziApplicativi;
import org.openspcoop2.core.config.driver.IDriverConfigurazioneGet;
import org.openspcoop2.core.config.driver.ValidazioneSemantica;
import org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.RuoliSoggetto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.mtom.MtomXomPackageInfo;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autorizzazione.canali.CanaliUtils;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.GestoreErroreConnettore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.engine.mapping.ModalitaIdentificazioneAzione;
import org.openspcoop2.protocol.engine.mapping.OperationFinder;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.slf4j.Logger;


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
			throw new DriverConfigurazioneException("IsCacheAbilitata, recupero informazione della cache della Configurazione non riuscita: "+e.getMessage(),e);
		}
	}
	public static void resetCache() throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				configurazionePdDReader.configurazionePdD.resetCache();
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Reset della cache della Configurazione non riuscita: "+e.getMessage(),e);
		}
	}
	public static void prefillCache(CryptConfig configApplicativi) throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				configurazionePdDReader.configurazionePdD.prefillCache(null, OpenSPCoop2Logger.getLoggerOpenSPCoopCore(), configApplicativi);
				configurazionePdDReader.configurazionePdD.prefillCacheConInformazioniRegistro(OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Prefill della cache della Configurazione non riuscita: "+e.getMessage(),e);
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
			throw new DriverConfigurazioneException("Visualizzazione Statistiche riguardante la cache della Configurazione non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache() throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				configurazionePdDReader.configurazionePdD.abilitaCache();
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Abilitazione cache della Configurazione non riuscita: "+e.getMessage(),e);
		}
	}
	public static void abilitaCache(Long dimensioneCache,Boolean algoritmoCacheLRU,Long itemIdleTime,Long itemLifeSecond, CryptConfig configApplicativi) throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				configurazionePdDReader.configurazionePdD.abilitaCache(dimensioneCache,algoritmoCacheLRU,itemIdleTime,itemLifeSecond, configApplicativi);
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Abilitazione cache della Configurazione non riuscita: "+e.getMessage(),e);
		}
	}
	public static void disabilitaCache() throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				configurazionePdDReader.configurazionePdD.disabilitaCache();
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Disabilitazione cache della Configurazione non riuscita: "+e.getMessage(),e);
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
			throw new DriverConfigurazioneException("Visualizzazione chiavi presenti nella cache della Configurazione non riuscita: "+e.getMessage(),e);
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
			throw new DriverConfigurazioneException("Visualizzazione oggetto presente nella cache della Configurazione non riuscita: "+e.getMessage(),e);
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
			throw new DriverConfigurazioneException("Rimozione oggetto presente nella cache della Configurazione non riuscita: "+e.getMessage(),e);
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
			boolean useOp2UtilsDatasource, boolean bindJMX, 
			boolean prefillCache, CryptConfig configApplicativi){

		try {
			ConfigurazionePdDReader.configurazionePdDReader = new ConfigurazionePdDReader(accessoConfigurazione,aLog,aLogconsole,localProperties,jndiNameDatasourcePdD, 
					forceDisableCache, useOp2UtilsDatasource, bindJMX, 
					prefillCache, configApplicativi);	
			return ConfigurazionePdDReader.initialize;
		}
		catch(Exception e) {
			aLog.error(e.getMessage(),e);
			aLogconsole.error(e.getMessage());
			return false;
		}
	}

	public static void prefillCacheConInformazioniRegistro(Logger aLogconsole){
		getInstance().configurazionePdD.prefillCacheConInformazioniRegistro(aLogconsole);
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

	public static IDriverConfigurazioneGet getDriverConfigurazionePdD() {
		return ConfigurazionePdDReader.configurazionePdDReader.configurazionePdD.getDriverConfigurazionePdD();
	} 










	/*   -------------- Costruttore -----------------  */ 

	/**
	 * Inizializza il reader
	 *
	 * @param accessoConfigurazione Informazioni per accedere alla configurazione della PdD OpenSPCoop.
	 */
	public ConfigurazionePdDReader(AccessoConfigurazionePdD accessoConfigurazione,Logger aLog,Logger aLogconsole,Properties localProperties, 
			String jndiNameDatasourcePdD, boolean forceDisableCache,
			boolean useOp2UtilsDatasource, boolean bindJMX, 
			boolean prefillCache, CryptConfig configApplicativi)throws DriverConfigurazioneException{
		try{
			if(aLog!=null)
				this.log = aLog;
			else
				this.log = LoggerWrapperFactory.getLogger(ConfigurazionePdDReader.class);
			this.configurazionePdD = new ConfigurazionePdD(accessoConfigurazione,this.log,aLogconsole,localProperties,jndiNameDatasourcePdD, forceDisableCache,
					useOp2UtilsDatasource, bindJMX, 
					prefillCache, configApplicativi);

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
	protected void validazioneSemantica(String[] tipiConnettori,String[] tipiMsgDiagnosticoAppender,String[] tipiTracciamentoAppender,String [] tipiDumpAppender,
			String[]tipoAutenticazionePortaDelegata,String[]tipoAutenticazionePortaApplicativa,
			String[]tipoAutorizzazionePortaDelegata,String[]tipoAutorizzazionePortaApplicativa,
			String[]tipoAutorizzazioneContenutoPortaDelegata,String[]tipoAutorizzazioneContenutoPortaApplicativa,
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
						tipiConnettori,ProtocolFactoryManager.getInstance().getOrganizationTypesAsArray(),
						ProtocolFactoryManager.getInstance().getServiceTypesAsArray(org.openspcoop2.protocol.manifest.constants.ServiceBinding.SOAP),
						ProtocolFactoryManager.getInstance().getServiceTypesAsArray(org.openspcoop2.protocol.manifest.constants.ServiceBinding.REST),
						tipiMsgDiagnosticoAppender,tipiTracciamentoAppender,tipiDumpAppender,
						tipoAutenticazionePortaDelegata, tipoAutenticazionePortaApplicativa,
						tipoAutorizzazionePortaDelegata, tipoAutorizzazionePortaApplicativa,
						tipoAutorizzazioneContenutoPortaDelegata, tipoAutorizzazioneContenutoPortaApplicativa, 
						tipiIntegrazionePD,tipiIntegrazionePA,validaConfigurazione);
				validazioneSemantica.validazioneSemantica(false);
				if(logConsole!=null){
					logConsole.info("Validazione semantica della configurazione effettuata.");
				}
			}
		}catch(Exception e){
			throw new CoreException(e.getMessage(),e);
		}
	}

	protected void setValidazioneSemanticaModificaConfigurazionePdDXML(String[] tipiConnettori,
			String[]tipoMsgDiagnosticiAppender,String[]tipoTracciamentoAppender,String [] tipiDumpAppender,
			String[]tipoAutenticazionePortaDelegata,String[]tipoAutenticazionePortaApplicativa,
			String[]tipoAutorizzazionePortaDelegata,String[]tipoAutorizzazionePortaApplicativa,
			String[]tipoAutorizzazioneContenutoPortaDelegata,String[]tipoAutorizzazioneContenutoPortaApplicativa,
			String[]tipoIntegrazionePD,String[]tipoIntegrazionePA) throws CoreException{
		try{
			Object o = this.configurazionePdD.getDriverConfigurazionePdD();
			if(o instanceof DriverConfigurazioneXML){
				DriverConfigurazioneXML driver = (DriverConfigurazioneXML) o;
				driver.abilitazioneValidazioneSemanticaDuranteModificaXML(tipiConnettori, 
						ProtocolFactoryManager.getInstance().getOrganizationTypesAsArray(),
						ProtocolFactoryManager.getInstance().getServiceTypesAsArray(org.openspcoop2.protocol.manifest.constants.ServiceBinding.SOAP),
						ProtocolFactoryManager.getInstance().getServiceTypesAsArray(org.openspcoop2.protocol.manifest.constants.ServiceBinding.REST),
						tipoMsgDiagnosticiAppender, tipoTracciamentoAppender, tipiDumpAppender, 
						tipoAutenticazionePortaDelegata, tipoAutenticazionePortaApplicativa,
						tipoAutorizzazionePortaDelegata, tipoAutorizzazionePortaApplicativa,
						tipoAutorizzazioneContenutoPortaDelegata, tipoAutorizzazioneContenutoPortaApplicativa, 
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
	 * Restituisce Il dominio di un soggetto  identificato da <var>idSoggetto</var>
	 *
	 * @param idSoggetto Identificativo di un soggetto
	 * @return Il dominio del Soggetto.
	 * 
	 */
	protected String getIdentificativoPorta(Connection connectionPdD, IDSoggetto idSoggetto,IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
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
		List<IDSoggetto> listaSoggettiVirtuali = null;
		try{
			listaSoggettiVirtuali = this.configurazionePdD.getSoggettiVirtuali(connectionPdD);
		}catch(DriverConfigurazioneNotFound de){
			this.log.info("Soggetti virtuali non presenti.");
			return false;
		}

		if(listaSoggettiVirtuali!=null && listaSoggettiVirtuali.size()>0) {
			for (int i = 0; i < listaSoggettiVirtuali.size(); i++) {
				if(listaSoggettiVirtuali.get(i).equals(idSoggetto)) {
					return true;
				}
			}
		}

		return false;
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
	protected  List<IDServizio> getServizi_SoggettiVirtuali(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
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
		return getForwardRoute(connectionPdD,registroServiziManager,null,this.buildIDServizioWithOnlySoggetto(idSoggettoDestinatario),functionAsRouter);
	}

	@SuppressWarnings("deprecation")
	private IDServizio buildIDServizioWithOnlySoggetto(IDSoggetto idSoggettoDestinatario){
		IDServizio idServizio = new IDServizio();
		idServizio.setSoggettoErogatore(idSoggettoDestinatario);
		return idServizio;
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
				if(idSoggettoMittente!=null && idServizio.getNome()!=null){
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

		StringBuilder bf = new StringBuilder();
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
							if(idSoggettoMittente!=null && idServizio.getNome()!=null){
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
						if(idSoggettoMittente!=null && idServizio.getNome()!=null){
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
	 * @return idSoggetto Identita
	 * 
	 */
	protected IDSoggetto getRouterIdentity(Connection connectionPdD,IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

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

	public IDPortaDelegata getIDPortaDelegata(Connection connectionPdD,String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getIDPortaDelegata(connectionPdD, nome);
	}

	protected PortaDelegata getPortaDelegata(Connection connectionPdD,IDPortaDelegata idPD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPortaDelegata(connectionPdD,idPD);
	}

	protected PortaDelegata getPortaDelegata_SafeMethod(Connection connectionPdD,IDPortaDelegata idPD)throws DriverConfigurazioneException{
		try{
			if(idPD.getNome()!=null)
				return this.getPortaDelegata(connectionPdD,idPD);
			else
				return null;
		}catch(DriverConfigurazioneNotFound e){
			return null;
		}
	}

	protected void updateStatoPortaDelegata(Connection connectionPdD,IDPortaDelegata idPD, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		this.configurazionePdD.updateStatoPortaDelegata(connectionPdD, idPD, stato);
	}
	
	protected Map<String, String> getProprietaConfigurazione(PortaDelegata pd) throws DriverConfigurazioneException {
		if (pd == null) {
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		} else if (pd.sizeProprietaList() <= 0) {
			return null;
		} else {
			Map<String, String> properties = new HashMap<String, String>();

			for(int i = 0; i < pd.sizeProprietaList(); ++i) {
				Proprieta p = pd.getProprieta(i);
				properties.put(p.getNome(), p.getValore());
			}

			return properties;
		}
	}

	protected boolean identificazioneContentBased(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}
		if( pd.getAzione() != null  ){
			if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_CONTENT_BASED.equals(pd.getAzione().getIdentificazione())){
				return true;
			}else{
				return false;
			}
		}else
			return false;
	}

	protected boolean identificazioneInputBased(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}
		if( pd.getAzione() != null  ){
			if(CostantiConfigurazione.PORTA_DELEGATA_AZIONE_INPUT_BASED.equals(pd.getAzione().getIdentificazione())){
				return true;
			}else{
				return false;
			}
		}else
			return false;
	}

	protected String getAzione(RegistroServiziManager registroServiziManager,PortaDelegata pd,URLProtocolContext urlProtocolContext,
			OpenSPCoop2Message message, HeaderIntegrazione headerIntegrazione, boolean readFirstHeaderIntegrazione,
			IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException, IdentificazioneDinamicaException { 

		try{

			if(pd==null){
				throw new DriverConfigurazioneException("Porta Delegata non fornita");
			}
			IDSoggetto soggettoErogatore = new IDSoggetto(pd.getSoggettoErogatore().getTipo(),pd.getSoggettoErogatore().getNome());
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pd.getServizio().getTipo(),pd.getServizio().getNome(), 
					soggettoErogatore, 
					pd.getServizio().getVersione()); 

			String azioneHeaderIntegrazione = null;
			if(headerIntegrazione!=null && headerIntegrazione.getBusta()!=null && headerIntegrazione.getBusta().getAzione()!=null){
				azioneHeaderIntegrazione = headerIntegrazione.getBusta().getAzione();
			}

			ModalitaIdentificazioneAzione modalitaIdentificazione = ModalitaIdentificazioneAzione.STATIC;
			String pattern = null;
			boolean forceRegistryBased = false;
			boolean forcePluginBased = false;
			if( pd.getAzione() != null  ){
				idServizio.setAzione(pd.getAzione().getNome());
				pattern = pd.getAzione().getPattern();
				if( pd.getAzione().getIdentificazione() != null  ){
					modalitaIdentificazione = ModalitaIdentificazioneAzione.convert(pd.getAzione().getIdentificazione());
				}
				forceRegistryBased = StatoFunzionalita.ABILITATO.equals(pd.getAzione().getForceInterfaceBased());
			}

			String azione = OperationFinder.getAzione(registroServiziManager, urlProtocolContext, message, soggettoErogatore, idServizio, 
					readFirstHeaderIntegrazione, azioneHeaderIntegrazione, protocolFactory, modalitaIdentificazione, 
					pattern, forceRegistryBased, forcePluginBased, this.log, false);

			// Se non ho riconosciuto una azione a questo punto, 
			// durante il processo standard di riconoscimento viene sollevata una eccezione IdentificazioneDinamicaException

			return azione;

		}catch(IdentificazioneDinamicaException e){
			throw e;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}

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

	protected String getAutenticazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}

		if(pd.getAutenticazione() == null || "".equals(pd.getAutenticazione()))
			return CostantiConfigurazione.CREDENZIALE_SSL.toString();
		else
			return pd.getAutenticazione();
	}

	protected boolean isAutenticazioneOpzionale(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}

		if(pd.getAutenticazioneOpzionale() == null)
			return false;
		else{
			if(StatoFunzionalita.ABILITATO.equals(pd.getAutenticazioneOpzionale())){
				return true;
			}
			else if(StatoFunzionalita.DISABILITATO.equals(pd.getAutenticazioneOpzionale())){
				return false;
			}
			else{
				return false;
			}
		}

	}

	protected String getGestioneToken(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}

		if(pd.getGestioneToken()!=null && pd.getGestioneToken().getPolicy()!=null) {
			return pd.getGestioneToken().getPolicy();
		}
		return null;
	}

	protected PolicyGestioneToken getPolicyGestioneToken(Connection connectionPdD, PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}

		if(pd.getGestioneToken()==null || pd.getGestioneToken().getPolicy()==null) {
			throw new DriverConfigurazioneException("Porta Delegata senza una policy di gestione token");
		}

		GenericProperties gp = this.getGenericProperties(connectionPdD, org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, pd.getGestioneToken().getPolicy());

		PolicyGestioneToken policy = null;
		try {
			policy = TokenUtilities.convertTo(gp, pd.getGestioneToken());
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

		return policy;
	}

	protected String getAutorizzazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}

		if(pd.getAutorizzazione() == null || "".equals(pd.getAutorizzazione()) )
			return CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED;
		else
			return pd.getAutorizzazione();
	}

	protected String getAutorizzazioneContenuto(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}

		if(pd.getAutorizzazioneContenuto() == null || "".equals(pd.getAutorizzazioneContenuto()))
			return CostantiConfigurazione.NONE;
		else
			return pd.getAutorizzazioneContenuto();
	}

	public CorsConfigurazione getConfigurazioneCORS(Connection connectionPdD, PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}

		CorsConfigurazione cors = pd.getGestioneCors();
		if(cors==null) {
			cors = this.getConfigurazioneCORS(connectionPdD);
		}
		return cors;
	}

	public ResponseCachingConfigurazione getConfigurazioneResponseCaching(Connection connectionPdD, PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}

		ResponseCachingConfigurazione c = pd.getResponseCaching();
		if(c==null) {
			c = this.getConfigurazioneResponseCaching(connectionPdD);
		}
		return c;
	}

	protected boolean ricevutaAsincronaSimmetricaAbilitata(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null)
			return true; // default Abilitata-CNIPA
		return !CostantiConfigurazione.DISABILITATO.equals(pd.getRicevutaAsincronaSimmetrica());
	}

	protected boolean ricevutaAsincronaAsimmetricaAbilitata(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null)
			return true; // default Abilitata-CNIPA
		return !CostantiConfigurazione.DISABILITATO.equals(pd.getRicevutaAsincronaAsimmetrica());
	}

	protected ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(Connection connectionPdD, PortaDelegata pd,String implementazionePdDSoggetto, boolean request) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
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
		else if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(pd.getValidazioneContenutiApplicativi().getTipo())  )
			valPD.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE);
		else if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(pd.getValidazioneContenutiApplicativi().getTipo())  )
			valPD.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD);

		if( CostantiConfigurazione.ABILITATO.equals(pd.getValidazioneContenutiApplicativi().getAcceptMtomMessage())  )
			valPD.setAcceptMtomMessage(CostantiConfigurazione.ABILITATO);
		else if( CostantiConfigurazione.DISABILITATO.equals(pd.getValidazioneContenutiApplicativi().getAcceptMtomMessage())  )
			valPD.setAcceptMtomMessage(CostantiConfigurazione.DISABILITATO);

		refreshByProperties(pd.getProprietaList(), request, valPD);
			
		return valPD;
	}
	
	private void refreshByProperties(List<Proprieta> list, boolean request, ValidazioneContenutiApplicativi val) {
		if(list!=null && !list.isEmpty()) {
			String pName = request? CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RICHIESTA_ENABLED : CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RISPOSTA_ENABLED;
			String pNameType = request? CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RICHIESTA_TIPO : CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RISPOSTA_TIPO;
			String pNameMtom = request? CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RICHIESTA_ACCEPT_MTOM_MESSAGE : CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RISPOSTA_ACCEPT_MTOM_MESSAGE;
			for (Proprieta p : list) {
				if(pName.equals(p.getNome())) {
					if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_ENABLED.equals(p.getValore())) {
						val.setStato(CostantiConfigurazione.STATO_CON_WARNING_ABILITATO);
					}
					else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_DISABLED.equals(p.getValore())) {
						val.setStato(CostantiConfigurazione.STATO_CON_WARNING_DISABILITATO);
					}
					else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_WARNING_ONLY.equals(p.getValore())) {
						val.setStato(CostantiConfigurazione.STATO_CON_WARNING_WARNING_ONLY);
					}
				}
				else if(pNameType.equals(p.getNome())) {
					if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.equals(p.getValore())) {
						val.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP);
					}
					else if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(p.getValore())) {
						val.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE);
					}
					else if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(p.getValore())) {
						val.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD);
					}
				}
				else if(pNameMtom.equals(p.getNome())) {
					if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_ENABLED.equals(p.getValore())) {
						val.setAcceptMtomMessage(CostantiConfigurazione.ABILITATO);
					}
					else if(CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_VALUE_DISABLED.equals(p.getValore())) {
						val.setAcceptMtomMessage(CostantiConfigurazione.DISABILITATO);
					}
				}
			}
		}
	}

	protected CorrelazioneApplicativa getCorrelazioneApplicativa(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}
		return pd.getCorrelazioneApplicativa();

	}

	protected CorrelazioneApplicativaRisposta getCorrelazioneApplicativaRisposta(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			throw new DriverConfigurazioneException("Porta Delegata non fornita");
		}
		return pd.getCorrelazioneApplicativaRisposta();

	}

	protected String[] getTipiIntegrazione(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		String[]tipi = null;
		if(pd!=null){
			if(pd.getIntegrazione() != null && ("".equals(pd.getIntegrazione())==false) ){
				tipi = pd.getIntegrazione().trim().split(",");
			}
		}  

		return tipi;
	}

	protected boolean isGestioneManifestAttachments(Connection connectionPdD, PortaDelegata pd, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pd==null){
			try{
				if(protocolFactory.createProtocolConfiguration().isSupportato(ServiceBinding.SOAP,FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
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
				if(protocolFactory.createProtocolConfiguration().isSupportato(ServiceBinding.SOAP,FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
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

	protected boolean isLocalForwardMode(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(pd==null || pd.getLocalForward()==null){
			// configurazione di default
			return false;
		}

		if( CostantiConfigurazione.ABILITATO.equals(pd.getLocalForward().getStato())  )
			return true;
		else if( CostantiConfigurazione.DISABILITATO.equals(pd.getLocalForward().getStato())  )
			return false;
		else {
			//configurazione di default
			return false;
		}
	}

	protected String getLocalForward_NomePortaApplicativa(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(pd==null || pd.getLocalForward()==null){
			// configurazione di default
			return null;
		}

		return pd.getLocalForward().getPortaApplicativa();
	}

	protected boolean isPortaAbilitata(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		if(pd==null){
			//configurazione di default
			return true; 
		}

		if( CostantiConfigurazione.ABILITATO.equals(pd.getStato())  )
			return true;
		else if( CostantiConfigurazione.DISABILITATO.equals(pd.getStato())  )
			return false;
		else {
			//configurazione di default
			return true; 
		}
	}

	protected DumpConfigurazione getDumpConfigurazione(Connection connectionPdD,PortaDelegata pd) throws DriverConfigurazioneException{
		if(pd==null){
			//configurazione di default
			return getDumpConfigurazione(connectionPdD);
		}

		if(pd.getDump()!=null) {
			return pd.getDump();
		}
		else {
			//configurazione di default
			return getDumpConfigurazione(connectionPdD);
		}
	}

	protected Trasformazioni getTrasformazioni(Connection connectionPdD,PortaDelegata pd) throws DriverConfigurazioneException{
		if(pd!=null && pd.getTrasformazioni()!=null) {
			return pd.getTrasformazioni();
		}
		return null;
	}

	protected List<Object> getExtendedInfo(PortaDelegata pd)throws DriverConfigurazioneException{

		if(pd == null || pd.sizeExtendedInfoList()<=0)
			return null;

		return pd.getExtendedInfoList();
	}











	/* ********  PORTE APPLICATIVE  (Interfaccia) ******** */

	public IDPortaApplicativa getIDPortaApplicativa(Connection connectionPdD,String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getIDPortaApplicativa(connectionPdD, nome);
	}

	protected Map<IDSoggetto,PortaApplicativa> getPorteApplicative_SoggettiVirtuali(Connection connectionPdD,IDServizio idServizio)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPorteApplicative_SoggettiVirtuali(connectionPdD,idServizio,null,false);
	}

	protected boolean existsPA(Connection connectionPdD,RichiestaApplicativa richiestaApplicativa) throws DriverConfigurazioneException{	

		// Se non c'e' un servizio non puo' esistere una porta applicativa
		if(richiestaApplicativa.getIDServizio()==null)
			return false;
		if( (richiestaApplicativa.getIDServizio().getNome()==null) || 
				(richiestaApplicativa.getIDServizio().getTipo()==null) || 
				(richiestaApplicativa.getIDServizio().getVersione()==null)  )
			return false;

		if( isSoggettoVirtuale(connectionPdD,richiestaApplicativa.getIDServizio().getSoggettoErogatore())  ){
			Map<IDSoggetto,PortaApplicativa> paConSoggetti = null;
			try{
				paConSoggetti = this.configurazionePdD.getPorteApplicative_SoggettiVirtuali(connectionPdD,richiestaApplicativa.getIDServizio(),richiestaApplicativa.getFiltroProprietaPorteApplicative(),true);
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
				IDPortaApplicativa idPA = richiestaApplicativa.getIdPortaApplicativa();
				if(idPA==null){
					return false;
				}
				pa = this.configurazionePdD.getPortaApplicativa(connectionPdD,idPA);
			}catch(DriverConfigurazioneNotFound e){
				return false;
			}
			return pa!=null;
		}
	}

	protected PortaApplicativa getPortaApplicativa(Connection connectionPdD,IDPortaApplicativa idPA) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		if(idPA==null){
			throw new DriverConfigurazioneException("[getPortaApplicativa]: Parametro non definito (idPA is null)");
		}
		return this.configurazionePdD.getPortaApplicativa(connectionPdD,idPA);
	}

	protected PortaApplicativa getPortaApplicativa_SafeMethod(Connection connectionPdD,IDPortaApplicativa idPA)throws DriverConfigurazioneException{
		try{
			if(idPA!=null && idPA.getNome()!=null)
				return this.getPortaApplicativa(connectionPdD,idPA);
			else
				return null;
		}catch(DriverConfigurazioneNotFound e){
			return null;
		}
	}

	protected void updateStatoPortaApplicativa(Connection connectionPdD,IDPortaApplicativa idPA, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		this.configurazionePdD.updateStatoPortaApplicativa(connectionPdD, idPA, stato);
	}
	
	public Map<String, String> getProprietaConfigurazione(PortaApplicativa pa) throws DriverConfigurazioneException {
		if (pa == null) {
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		} else if (pa.sizeProprietaList() <= 0) {
			return null;
		} else {
			Map<String, String> properties = new HashMap<>();

			for(int i = 0; i < pa.sizeProprietaList(); ++i) {
				Proprieta p = pa.getProprieta(i);
				properties.put(p.getNome(), p.getValore());
			}

			return properties;
		}
	}
	
	protected boolean identificazioneContentBased(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}
		if( pa.getAzione() != null  ){
			if(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_CONTENT_BASED.equals(pa.getAzione().getIdentificazione())){
				return true;
			}else{
				return false;
			}
		}else
			return false;
	}

	protected boolean identificazioneInputBased(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}
		if( pa.getAzione() != null  ){
			if(CostantiConfigurazione.PORTA_APPLICATIVA_AZIONE_INPUT_BASED.equals(pa.getAzione().getIdentificazione())){
				return true;
			}else{
				return false;
			}
		}else
			return false;
	}

	protected String getAzione(RegistroServiziManager registroServiziManager,PortaApplicativa pa,URLProtocolContext urlProtocolContext,
			OpenSPCoop2Message message, HeaderIntegrazione headerIntegrazione, boolean readFirstHeaderIntegrazione,
			IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException, IdentificazioneDinamicaException { 

		try{

			if(pa==null){
				throw new DriverConfigurazioneException("Porta Applicativa non fornita");
			}
			IDSoggetto soggettoErogatore = new IDSoggetto(pa.getTipoSoggettoProprietario(),pa.getNomeSoggettoProprietario());
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(),pa.getServizio().getNome(), 
					soggettoErogatore, pa.getServizio().getVersione()); 

			String azioneHeaderIntegrazione = null;
			if(headerIntegrazione!=null && headerIntegrazione.getBusta()!=null && headerIntegrazione.getBusta().getAzione()!=null){
				azioneHeaderIntegrazione = headerIntegrazione.getBusta().getAzione();
			}

			ModalitaIdentificazioneAzione modalitaIdentificazione = ModalitaIdentificazioneAzione.STATIC;
			String pattern = null;
			boolean forceRegistryBased = false;
			boolean forcePluginBased = true; // sulla PA si fa sempre questo controllo?
			if( pa.getAzione() != null  ){
				idServizio.setAzione(pa.getAzione().getNome());
				pattern = pa.getAzione().getPattern();
				if( pa.getAzione().getIdentificazione() != null  ){
					modalitaIdentificazione = ModalitaIdentificazioneAzione.convert(pa.getAzione().getIdentificazione());
				}
				forceRegistryBased = StatoFunzionalita.ABILITATO.equals(pa.getAzione().getForceInterfaceBased());
			}

			String azione = OperationFinder.getAzione(registroServiziManager, urlProtocolContext, message, soggettoErogatore, idServizio, 
					readFirstHeaderIntegrazione, azioneHeaderIntegrazione, protocolFactory, modalitaIdentificazione, 
					pattern, forceRegistryBased, forcePluginBased, this.log, true);

			// Se non ho riconosciuto una azione a questo punto, 
			// durante il processo standard di riconoscimento viene sollevata una eccezione IdentificazioneDinamicaException

			return azione;

		}catch(IdentificazioneDinamicaException e){
			throw e;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}

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

	protected SoggettoVirtuale getServiziApplicativi_SoggettiVirtuali(Connection connectionPdD,RichiestaApplicativa richiestaApplicativa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Map<IDSoggetto,PortaApplicativa> paConSoggetti = this.configurazionePdD.getPorteApplicative_SoggettiVirtuali(connectionPdD,richiestaApplicativa.getIDServizio()
				,richiestaApplicativa.getFiltroProprietaPorteApplicative(),true);
		if(paConSoggetti == null)
			throw new DriverConfigurazioneNotFound("PorteApplicative contenenti SoggettiVirtuali di ["+richiestaApplicativa.getIDServizio()+"] non trovate");
		if(paConSoggetti.size() ==0)
			throw new DriverConfigurazioneNotFound("PorteApplicative contenenti SoggettiVirtuali di ["+richiestaApplicativa.getIDServizio()+"] non trovate");

		java.util.List<SoggettoVirtualeServizioApplicativo> trovati = new java.util.ArrayList<SoggettoVirtualeServizioApplicativo>();

		Iterator<IDSoggetto> it = paConSoggetti.keySet().iterator();
		while (it.hasNext()) {
			IDSoggetto soggReale = (IDSoggetto) it.next();
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
			throw new DriverConfigurazioneNotFound("PorteApplicative contenenti SoggettiVirtuali di ["+richiestaApplicativa.getIDServizio()+"] non trovati soggetti virtuali");
		else{
			SoggettoVirtuale soggVirtuale = new SoggettoVirtuale();
			for (SoggettoVirtualeServizioApplicativo soggettoVirtualeServizioApplicativo : trovati) {
				soggVirtuale.addServizioApplicativo(soggettoVirtualeServizioApplicativo);
			}
			return soggVirtuale;
		}
	}

	protected List<PortaApplicativa> getPorteApplicative(Connection connectionPdD,IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPorteApplicative(connectionPdD, idServizio, ricercaPuntuale);
	}


	protected List<PortaApplicativa> getPorteApplicativeVirtuali(Connection connectionPdD,IDSoggetto idSoggettoVirtuale, IDServizio idServizio, boolean ricercaPuntuale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPorteApplicativeVirtuali(connectionPdD, idSoggettoVirtuale, idServizio, ricercaPuntuale);
	}

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


	protected String getAutenticazione(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}

		if(pa.getAutenticazione() == null || "".equals(pa.getAutenticazione()))
			return CostantiConfigurazione.CREDENZIALE_SSL.toString();
		else
			return pa.getAutenticazione();
	}

	protected boolean isAutenticazioneOpzionale(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}

		if(pa.getAutenticazioneOpzionale() == null)
			return false;
		else{
			if(StatoFunzionalita.ABILITATO.equals(pa.getAutenticazioneOpzionale())){
				return true;
			}
			else if(StatoFunzionalita.DISABILITATO.equals(pa.getAutenticazioneOpzionale())){
				return false;
			}
			else{
				return false;
			}
		}

	}

	protected String getGestioneToken(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}

		if(pa.getGestioneToken()!=null && pa.getGestioneToken().getPolicy()!=null) {
			return pa.getGestioneToken().getPolicy();
		}
		return null;
	}

	protected PolicyGestioneToken getPolicyGestioneToken(Connection connectionPdD, PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}

		if(pa.getGestioneToken()==null || pa.getGestioneToken().getPolicy()==null) {
			throw new DriverConfigurazioneException("Porta Applicativa senza una policy di gestione token");
		}

		GenericProperties gp = this.getGenericProperties(connectionPdD, org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, pa.getGestioneToken().getPolicy());

		PolicyGestioneToken policy = null;
		try {
			policy = TokenUtilities.convertTo(gp, pa.getGestioneToken());
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

		return policy;
	}

	protected String getAutorizzazione(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}

		if(pa.getAutorizzazione() == null || "".equals(pa.getAutorizzazione()) )
			return CostantiConfigurazione.AUTORIZZAZIONE_AUTHENTICATED;
		else
			return pa.getAutorizzazione();
	}

	protected String getAutorizzazioneContenuto(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}

		if(pa.getAutorizzazioneContenuto() == null || "".equals(pa.getAutorizzazioneContenuto()))
			return CostantiConfigurazione.NONE;
		else
			return pa.getAutorizzazioneContenuto();
	}

	public CorsConfigurazione getConfigurazioneCORS(Connection connectionPdD, PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}

		CorsConfigurazione cors = pa.getGestioneCors();
		if(cors==null) {
			cors = this.getConfigurazioneCORS(connectionPdD);
		}
		return cors;
	}

	public ResponseCachingConfigurazione getConfigurazioneResponseCaching(Connection connectionPdD, PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}

		ResponseCachingConfigurazione c = pa.getResponseCaching();
		if(c==null) {
			c = this.getConfigurazioneResponseCaching(connectionPdD);
		}
		return c;
	}

	protected boolean ricevutaAsincronaSimmetricaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pa==null)
			return true; // default Abilitata-CNIPA
		return !CostantiConfigurazione.DISABILITATO.equals(pa.getRicevutaAsincronaSimmetrica());
	}

	protected boolean ricevutaAsincronaAsimmetricaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pa==null)
			return true; // default Abilitata-CNIPA
		return !CostantiConfigurazione.DISABILITATO.equals(pa.getRicevutaAsincronaAsimmetrica());
	}

	protected ValidazioneContenutiApplicativi getTipoValidazioneContenutoApplicativo(Connection connectionPdD, PortaApplicativa pa,String implementazionePdDSoggetto, boolean request) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

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
		else if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(pa.getValidazioneContenutiApplicativi().getTipo())  )
			valPA.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE);
		else if( CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.equals(pa.getValidazioneContenutiApplicativi().getTipo())  )
			valPA.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD);

		if( CostantiConfigurazione.ABILITATO.equals(pa.getValidazioneContenutiApplicativi().getAcceptMtomMessage())  )
			valPA.setAcceptMtomMessage(CostantiConfigurazione.ABILITATO);
		else if( CostantiConfigurazione.DISABILITATO.equals(pa.getValidazioneContenutiApplicativi().getAcceptMtomMessage())  )
			valPA.setAcceptMtomMessage(CostantiConfigurazione.DISABILITATO);

		refreshByProperties(pa.getProprietaList(), request, valPA);
		
		return valPA;
	}

	protected CorrelazioneApplicativa getCorrelazioneApplicativa(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}
		return pa.getCorrelazioneApplicativa();

	}

	protected CorrelazioneApplicativaRisposta getCorrelazioneApplicativaRisposta(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		if(pa==null){
			throw new DriverConfigurazioneException("Porta Applicativa non fornita");
		}
		return pa.getCorrelazioneApplicativaRisposta();

	}

	protected String[] getTipiIntegrazione(PortaApplicativa pa) throws DriverConfigurazioneException,DriverConfigurazioneNotFound { 

		String[]tipi = null;
		if(pa!=null){
			if(pa.getIntegrazione() != null && ("".equals(pa.getIntegrazione())==false) ){
				tipi = pa.getIntegrazione().trim().split(",");
			}  
		}

		return tipi;
	}

	protected boolean isGestioneManifestAttachments(Connection connectionPdD, PortaApplicativa pa, IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		if(pa==null){
			try{
				if(protocolFactory.createProtocolConfiguration().isSupportato(ServiceBinding.SOAP,FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
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
				if(protocolFactory.createProtocolConfiguration().isSupportato(ServiceBinding.SOAP,FunzionalitaProtocollo.MANIFEST_ATTACHMENTS)){
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

	protected boolean autorizzazione(PortaApplicativa pa, IDSoggetto soggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		if( (pa == null) || (pa.getSoggetti()==null) )
			return false;
		if( (soggetto == null) || (soggetto.getTipo()==null) || (soggetto.getNome()==null) )
			return false;

		for(int j=0; j<pa.getSoggetti().sizeSoggettoList(); j++){
			if(soggetto.getTipo().equals(pa.getSoggetti().getSoggetto(j).getTipo()) &&
					soggetto.getNome().equals(pa.getSoggetti().getSoggetto(j).getNome())) {
				return true;
			}
		}

		return false;
	}

	protected boolean autorizzazione(PortaApplicativa pa, IDServizioApplicativo servizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		if( (pa == null) || (pa.getServiziApplicativiAutorizzati()==null) )
			return false;
		if( (servizioApplicativo == null) || (servizioApplicativo.getNome()==null) || 
				(servizioApplicativo.getIdSoggettoProprietario()==null) ||
				(servizioApplicativo.getIdSoggettoProprietario().getTipo()==null) ||
				(servizioApplicativo.getIdSoggettoProprietario().getNome()==null) )
			return false;

		for(int j=0; j<pa.getServiziApplicativiAutorizzati().sizeServizioApplicativoList(); j++){
			if(servizioApplicativo.getNome().equals(pa.getServiziApplicativiAutorizzati().getServizioApplicativo(j).getNome()) &&
					servizioApplicativo.getIdSoggettoProprietario().getTipo().equals(pa.getServiziApplicativiAutorizzati().getServizioApplicativo(j).getTipoSoggettoProprietario()) &&
					servizioApplicativo.getIdSoggettoProprietario().getNome().equals(pa.getServiziApplicativiAutorizzati().getServizioApplicativo(j).getNomeSoggettoProprietario())) {
				return true;
			}
		}

		return false;
	}

	protected boolean autorizzazioneRoles(RegistroServiziManager registroServiziManager, 
			PortaApplicativa pa, org.openspcoop2.core.registry.Soggetto soggetto, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if( (pa == null) || pa.getRuoli()==null || pa.getRuoli().sizeRuoloList()<=0 ){
			throw new DriverConfigurazioneNotFound("Non sono stati definiti i ruoli necessari a fruire della porta applicativa");
		}

		if(checkRuoloRegistro && !checkRuoloEsterno){
			if(soggetto==null && sa==null){
				throw new DriverConfigurazioneException("Identità soggetto e/o applicativo non disponibile; tale informazione è richiesta dall'autorizzazione");
			}
		}

		HttpServletRequest httpServletRequest = null;
		List<String> tokenRoles = null;
		if(checkRuoloEsterno){

			InformazioniToken informazioniTokenNormalizzate = null;
			Object oInformazioniTokenNormalizzate = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
			if(oInformazioniTokenNormalizzate!=null) {
				informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
			}
			if(informazioniTokenNormalizzate!=null) {
				tokenRoles = informazioniTokenNormalizzate.getRoles();
			}

			if(infoConnettoreIngresso==null ||
					infoConnettoreIngresso.getUrlProtocolContext()==null ||
					infoConnettoreIngresso.getUrlProtocolContext().getHttpServletRequest()==null){
				if(tokenRoles==null) {
					throw new DriverConfigurazioneException("HttpServletRequest non disponibile; risorsa richiesta dall'autorizzazione");
				}
			}
			httpServletRequest = infoConnettoreIngresso.getUrlProtocolContext().getHttpServletRequest();
		}

		RuoloTipoMatch ruoloMatch = pa.getRuoli().getMatch();
		if(ruoloMatch==null){
			ruoloMatch = RuoloTipoMatch.ALL;
		}

		for(int j=0; j<pa.getRuoli().sizeRuoloList(); j++){
			Ruolo ruolo = pa.getRuoli().getRuolo(j);

			boolean trovato = false;

			if(checkRuoloRegistro) {
				if(sa!=null) {
					if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().getRuoli()!=null){
						ServizioApplicativoRuoli ruoliSA = sa.getInvocazionePorta().getRuoli();
						for (int i = 0; i < ruoliSA.sizeRuoloList(); i++) {
							if(ruolo.getNome().equals(ruoliSA.getRuolo(i).getNome())){
								trovato = true;
								break;
							}
						}
					}
				}
				else if(soggetto!=null) {
					if(soggetto.getRuoli()!=null){
						RuoliSoggetto ruoliSoggetto = soggetto.getRuoli();
						for (int i = 0; i < ruoliSoggetto.sizeRuoloList(); i++) {
							if(ruolo.getNome().equals(ruoliSoggetto.getRuolo(i).getNome())){
								trovato = true;
								break;
							}
						}
					}
				}
			}

			if(!trovato && checkRuoloEsterno){
				String nomeRuoloDaVerificare = ruolo.getNome();
				try {
					org.openspcoop2.core.registry.Ruolo ruoloRegistro = registroServiziManager.getRuolo(ruolo.getNome(), null);
					if( (RuoloTipologia.ESTERNO.equals(ruoloRegistro.getTipologia()) || RuoloTipologia.QUALSIASI.equals(ruoloRegistro.getTipologia())) &&
							ruoloRegistro.getNomeEsterno()!=null && 
							!"".equals(ruoloRegistro.getNomeEsterno())) {
						nomeRuoloDaVerificare = ruoloRegistro.getNomeEsterno();
					}
				}catch(Exception e) {
					throw new DriverConfigurazioneException("Recupero del ruolo '"+ruolo.getNome()+"' fallito: "+e.getMessage(),e);
				}
				boolean foundInHttpServlet = false;
				boolean foundInToken = false;
				if(httpServletRequest!=null) {
					foundInHttpServlet = httpServletRequest.isUserInRole(nomeRuoloDaVerificare);
				}
				if(tokenRoles!=null && tokenRoles.size()>0) {
					foundInToken = tokenRoles.contains(nomeRuoloDaVerificare); 
				}
				if(foundInHttpServlet || foundInToken){
					trovato = true;
				}
			}

			if(trovato){
				if(RuoloTipoMatch.ANY.equals(ruoloMatch)){
					return true; // basta un ruolo
				}
			}
			else{
				if(RuoloTipoMatch.ALL.equals(ruoloMatch)){
					if(details!=null) {
						details.append("Role '"+ruolo.getNome()+"' not found");
					}
					return false; // deve possedere tutti i ruoli
				}
			}
		}

		if(RuoloTipoMatch.ANY.equals(ruoloMatch)){
			if(details!=null) {
				details.append("Roles not found");
			}
			return false; // non è stato trovato alcun ruolo
		}
		else{
			return true; // tutti i ruoli trovati
		}

	}

	protected boolean isPortaAbilitata(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		if(pa==null){
			//configurazione di default
			return true; 
		}

		if( CostantiConfigurazione.ABILITATO.equals(pa.getStato())  )
			return true;
		else if( CostantiConfigurazione.DISABILITATO.equals(pa.getStato())  )
			return false;
		else {
			//configurazione di default
			return true; 
		}
	}

	protected DumpConfigurazione getDumpConfigurazione(Connection connectionPdD,PortaApplicativa pa) throws DriverConfigurazioneException{
		if(pa==null){
			//configurazione di default
			return getDumpConfigurazione(connectionPdD);
		}

		if(pa.getDump()!=null) {
			return pa.getDump();
		}
		else {
			//configurazione di default
			return getDumpConfigurazione(connectionPdD);
		}
	}

	protected Trasformazioni getTrasformazioni(Connection connectionPdD,PortaApplicativa pa) throws DriverConfigurazioneException{
		if(pa!=null && pa.getTrasformazioni()!=null) {
			return pa.getTrasformazioni();
		}
		return null;
	}

	protected List<Object> getExtendedInfo(PortaApplicativa pa)throws DriverConfigurazioneException{

		if(pa == null || pa.sizeExtendedInfoList()<=0)
			return null;

		return pa.getExtendedInfoList();
	}







	/* ********  Servizi Applicativi (Interfaccia)  ******** */

	protected boolean existsServizioApplicativo(Connection connectionPdD,IDServizioApplicativo idSA) throws DriverConfigurazioneException{ 

		ServizioApplicativo servizioApplicativo = null;
		try{
			servizioApplicativo = this.configurazionePdD.getServizioApplicativo(connectionPdD,idSA);
		}catch(DriverConfigurazioneNotFound e){
			return false;
		}

		return servizioApplicativo!=null;

	}

	protected ServizioApplicativo getServizioApplicativo(Connection connectionPdD,IDServizioApplicativo idSA) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getServizioApplicativo(connectionPdD, idSA);
	}

	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiBasic(Connection connectionPdD,String aUser,String aPassword, CryptConfig config) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiBasic(connectionPdD, aUser, aPassword, config);
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
	
	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiApiKey(Connection connectionPdD,String aUser,String aPassword, boolean appId, CryptConfig config) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiApiKey(connectionPdD, aUser, aPassword, appId, config);
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

	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiSsl(Connection connectionPdD,String aSubject, String aIssuer) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiSsl(connectionPdD, aSubject, aIssuer);
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

	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiSsl(Connection connectionPdD,CertificateInfo certificate, boolean strictVerifier) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiSsl(connectionPdD, certificate, strictVerifier);
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

	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiPrincipal(Connection connectionPdD,String principal) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiPrincipal(connectionPdD, principal);
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

	protected boolean autorizzazione(PortaDelegata pd, String servizioApplicativo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if( (pd == null) || (servizioApplicativo==null) )
			return false;

		for(int j=0; j<pd.sizeServizioApplicativoList(); j++){
			if(servizioApplicativo.equals(pd.getServizioApplicativo(j).getNome()))
				return true;
		}

		return false;

	}

	protected boolean autorizzazioneRoles(RegistroServiziManager registroServiziManager, 
			PortaDelegata pd, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{ 

		if( (pd == null) || pd.getRuoli()==null || pd.getRuoli().sizeRuoloList()<=0 ){
			throw new DriverConfigurazioneNotFound("Non sono stati definiti i ruoli necessari a fruire della porta delegata");
		}

		if(checkRuoloRegistro && !checkRuoloEsterno){
			if(sa==null){
				throw new DriverConfigurazioneException("Identità servizio applicativo non disponibile; tale informazione è richiesta dall'autorizzazione");
			}
		}

		HttpServletRequest httpServletRequest = null;
		List<String> tokenRoles = null;
		if(checkRuoloEsterno){

			InformazioniToken informazioniTokenNormalizzate = null;
			Object oInformazioniTokenNormalizzate = pddContext.getObject(org.openspcoop2.pdd.core.token.Costanti.PDD_CONTEXT_TOKEN_INFORMAZIONI_NORMALIZZATE);
			if(oInformazioniTokenNormalizzate!=null) {
				informazioniTokenNormalizzate = (InformazioniToken) oInformazioniTokenNormalizzate;
			}
			if(informazioniTokenNormalizzate!=null) {
				tokenRoles = informazioniTokenNormalizzate.getRoles();
			}

			if(infoConnettoreIngresso==null ||
					infoConnettoreIngresso.getUrlProtocolContext()==null ||
					infoConnettoreIngresso.getUrlProtocolContext().getHttpServletRequest()==null){
				if(tokenRoles==null) {
					throw new DriverConfigurazioneException("HttpServletRequest non disponibile; risorsa richiesta dall'autorizzazione");
				}
			}
			httpServletRequest = infoConnettoreIngresso.getUrlProtocolContext().getHttpServletRequest();
		}

		RuoloTipoMatch ruoloMatch = pd.getRuoli().getMatch();
		if(ruoloMatch==null){
			ruoloMatch = RuoloTipoMatch.ALL;
		}

		for(int j=0; j<pd.getRuoli().sizeRuoloList(); j++){
			Ruolo ruolo = pd.getRuoli().getRuolo(j);

			boolean trovato = false;

			if(checkRuoloRegistro && sa!=null && sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().getRuoli()!=null){
				ServizioApplicativoRuoli ruoliSA = sa.getInvocazionePorta().getRuoli();
				for (int i = 0; i < ruoliSA.sizeRuoloList(); i++) {
					if(ruolo.getNome().equals(ruoliSA.getRuolo(i).getNome())){
						trovato = true;
						break;
					}
				}
			}

			if(!trovato && checkRuoloEsterno){
				String nomeRuoloDaVerificare = ruolo.getNome();
				try {
					org.openspcoop2.core.registry.Ruolo ruoloRegistro = registroServiziManager.getRuolo(ruolo.getNome(), null);
					if( (RuoloTipologia.ESTERNO.equals(ruoloRegistro.getTipologia()) || RuoloTipologia.QUALSIASI.equals(ruoloRegistro.getTipologia())) &&
							ruoloRegistro.getNomeEsterno()!=null && 
							!"".equals(ruoloRegistro.getNomeEsterno())) {
						nomeRuoloDaVerificare = ruoloRegistro.getNomeEsterno();
					}
				}catch(Exception e) {
					throw new DriverConfigurazioneException("Recupero del ruolo '"+ruolo.getNome()+"' fallito: "+e.getMessage(),e);
				}
				boolean foundInHttpServlet = false;
				boolean foundInToken = false;
				if(httpServletRequest!=null) {
					foundInHttpServlet = httpServletRequest.isUserInRole(nomeRuoloDaVerificare);
				}
				if(tokenRoles!=null && tokenRoles.size()>0) {
					foundInToken = tokenRoles.contains(nomeRuoloDaVerificare); 
				}
				if(foundInHttpServlet || foundInToken){
					trovato = true;
				}
			}

			if(trovato){
				if(RuoloTipoMatch.ANY.equals(ruoloMatch)){
					return true; // basta un ruolo
				}
			}
			else{
				if(RuoloTipoMatch.ALL.equals(ruoloMatch)){
					if(details!=null) {
						details.append("Role '"+ruolo.getNome()+"' not found");
					}
					return false; // deve possedere tutti i ruoli
				}
			}
		}

		if(RuoloTipoMatch.ANY.equals(ruoloMatch)){
			if(details!=null) {
				details.append("Roles not found");
			}
			return false; // non è stato trovato alcun ruolo
		}
		else{
			return true; // tutti i ruoli trovati
		}

	}

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
		if(proprietaSA.getFaultActor()!=null && StringUtils.isNotEmpty(proprietaSA.getFaultActor()))
			gestioneErrore.setFaultActor(proprietaSA.getFaultActor());
		// fault generic code abilitato/disabilitato
		if(CostantiConfigurazione.ABILITATO.equals(proprietaSA.getGenericFaultCode()))
			gestioneErrore.setFaultAsGenericCode(true);
		else if(CostantiConfigurazione.DISABILITATO.equals(proprietaSA.getGenericFaultCode()))
			gestioneErrore.setFaultAsGenericCode(false);
		// fault prefix code
		if(proprietaSA.getPrefixFaultCode()!=null && StringUtils.isNotEmpty(proprietaSA.getPrefixFaultCode()))
			gestioneErrore.setFaultPrefixCode(proprietaSA.getPrefixFaultCode());

		return;
	}

	protected boolean invocazionePortaDelegataPerRiferimento(ServizioApplicativo sa) throws DriverConfigurazioneException{
		if(sa==null)
			return false;
		if(sa.getInvocazionePorta()==null)
			return false;
		return CostantiConfigurazione.ABILITATO.equals(sa.getInvocazionePorta().getInvioPerRiferimento());
	}

	protected boolean invocazionePortaDelegataSbustamentoInformazioniProtocollo(ServizioApplicativo sa) throws DriverConfigurazioneException{
		if(sa==null)
			return true;
		if(sa.getInvocazionePorta()==null)
			return true;
		return !CostantiConfigurazione.DISABILITATO.equals(sa.getInvocazionePorta().getSbustamentoInformazioniProtocollo());
	}

	public List<String> getServiziApplicativiConsegnaNotifichePrioritarie(Connection connectionPdD, String queue) throws DriverConfigurazioneException{
		try {
			List<IDConnettore> list = this.configurazionePdD.getConnettoriConsegnaNotifichePrioritarie(connectionPdD, queue);
			if(list==null || list.isEmpty()) {
				throw new DriverConfigurazioneNotFound();
			}
			List<String> l = new ArrayList<String>();
			for (IDServizioApplicativo idSA : list) {
				if(l.contains(idSA.getNome())==false) {
					l.add(idSA.getNome());
				}
			}
			return l;
		}catch(DriverConfigurazioneNotFound notFound) {
			return new ArrayList<String>();
		}
	}
	
	public List<IDConnettore> getConnettoriConsegnaNotifichePrioritarie(Connection connectionPdD, String queue) throws DriverConfigurazioneException{
		try {
			return this.configurazionePdD.getConnettoriConsegnaNotifichePrioritarie(connectionPdD, queue);
		}catch(DriverConfigurazioneNotFound notFound) {
			return new ArrayList<IDConnettore>();
		}
	}
	
	public void resetConnettoriConsegnaNotifichePrioritarie(Connection connectionPdD, String queue) throws DriverConfigurazioneException{
		this.configurazionePdD.resetConnettoriConsegnaNotifichePrioritarie(connectionPdD, queue);
	}










	// INVOCAZIONE SERVIZIO

	protected boolean invocazioneServizioConGetMessage(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getInvocazioneServizio()==null)
			return false;
		InvocazioneServizio serv = sa.getInvocazioneServizio();
		return CostantiConfigurazione.ABILITATO.equals(serv.getGetMessage());
	}

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

	protected ConnettoreMsg getInvocazioneServizio(Connection connectionPdD,ServizioApplicativo sa,RichiestaApplicativa richiestaApplicativa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Servizio applicativo
		if(sa.getInvocazioneServizio()==null)
			throw new DriverConfigurazioneNotFound("Servizio applicativo ["+richiestaApplicativa.getServizioApplicativo()+"] del soggetto["+richiestaApplicativa.getIDServizio().getSoggettoErogatore()+"] non possieder l'elemento invocazione servizio");
		InvocazioneServizio serv = sa.getInvocazioneServizio();


		// Soggetto Erogatore
		IDSoggetto aSoggetto = richiestaApplicativa.getIDServizio().getSoggettoErogatore();
		Soggetto soggetto = this.configurazionePdD.getSoggetto(connectionPdD,aSoggetto);
		if(soggetto==null)
			throw new DriverConfigurazioneNotFound("[getInvocazioneServizio] Soggetto erogatore non trovato");


		// Porta Applicativa
		PortaApplicativa pa = this.configurazionePdD.getPortaApplicativa(connectionPdD,richiestaApplicativa.getIdPortaApplicativa());


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
		Map<String, String> protocol_properties = new HashMap<String, String>();
		for(int i=0;i<pa.sizeProprietaList();i++){
			protocol_properties.put(pa.getProprieta(i).getNome(),pa.getProprieta(i).getValore());
		}

		// Autenticazione
		String autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString();
		String tmp = null;
		if(serv.getAutenticazione()!=null){
			tmp = serv.getAutenticazione().toString();
		}
		if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString().equalsIgnoreCase(tmp)){
			autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString();
		}


		// Credenziali
		InvocazioneCredenziali credenziali = null;
		if(serv.getCredenziali()!=null){
			credenziali = new InvocazioneCredenziali();
			credenziali.setUser(serv.getCredenziali().getUser());
			credenziali.setPassword(serv.getCredenziali().getPassword());
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

	protected GestioneErrore getGestioneErroreConnettore_InvocazioneServizio(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding, Connection connectionPdD,ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		//  Servizio applicativo
		if(sa.getInvocazioneServizio()==null || 
				sa.getInvocazioneServizio().getGestioneErrore()==null)
			return getGestioneErroreConnettoreComponenteIntegrazione(protocolFactory, serviceBinding, connectionPdD);
		InvocazioneServizio invocazione = sa.getInvocazioneServizio();
		return invocazione.getGestioneErrore();
	}

	protected boolean invocazioneServizioPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		//	  Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getInvocazioneServizio()==null)
			return false;
		return CostantiConfigurazione.ABILITATO.equals(sa.getInvocazioneServizio().getInvioPerRiferimento());
	}

	protected boolean invocazioneServizioRispostaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		//		  Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getInvocazioneServizio()==null)
			return false;
		return CostantiConfigurazione.ABILITATO.equals(sa.getInvocazioneServizio().getRispostaPerRiferimento());
	}








	// RICEZIONE RISPOSTA ASINCRONA

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

	protected boolean consegnaRispostaAsincronaConGetMessage(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		// Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getRispostaAsincrona()==null)
			return false;
		RispostaAsincrona serv = sa.getRispostaAsincrona();
		return CostantiConfigurazione.ABILITATO.equals(serv.getGetMessage());
	}

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

	protected ConnettoreMsg getConsegnaRispostaAsincrona(Connection connectionPdD,ServizioApplicativo sa,RichiestaDelegata idPD)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Servizio applicativo
		if(sa.getRispostaAsincrona()==null)
			throw new DriverConfigurazioneNotFound("Servizio applicativo ["+idPD.getServizioApplicativo()+"] del soggetto["+idPD.getIdSoggettoFruitore()+"] non possiede una risposta Asincrona");
		RispostaAsincrona serv = sa.getRispostaAsincrona();


		// Soggetto che possiede il connettore
		IDSoggetto aSoggetto = idPD.getIdSoggettoFruitore();
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
		if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString().equalsIgnoreCase(tmp))
			autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString();


		// Credenziali
		InvocazioneCredenziali credenziali = null;
		if(serv.getCredenziali()!=null){
			credenziali = new InvocazioneCredenziali();
			credenziali.setUser(serv.getCredenziali().getUser());
			credenziali.setPassword(serv.getCredenziali().getPassword());
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

	protected ConnettoreMsg getConsegnaRispostaAsincrona(Connection connectionPdD,ServizioApplicativo sa,RichiestaApplicativa richiestaApplicativa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		//		 Servizio applicativo
		if(sa.getRispostaAsincrona()==null)
			throw new DriverConfigurazioneNotFound("Servizio applicativo ["+richiestaApplicativa.getServizioApplicativo()+"] del soggetto["+richiestaApplicativa.getSoggettoFruitore()+"] non possiede l'elemento invocazione servizio");
		RispostaAsincrona serv = sa.getRispostaAsincrona();


		// Soggetto che contiene il connettore
		IDSoggetto aSoggetto = richiestaApplicativa.getIDServizio().getSoggettoErogatore();
		Soggetto soggetto = this.configurazionePdD.getSoggetto(connectionPdD,aSoggetto);
		if(soggetto==null)
			throw new DriverConfigurazioneNotFound("[getConsegnaRispostaAsincrona] Soggetto non trovato");


		// Porta Applicativa
		PortaApplicativa pa = this.configurazionePdD.getPortaApplicativa(connectionPdD,richiestaApplicativa.getIdPortaApplicativa());


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
		Map<String, String> protocol_properties = new HashMap<String, String>();
		for(int i=0;i<pa.sizeProprietaList();i++){
			protocol_properties.put(pa.getProprieta(i).getNome(),pa.getProprieta(i).getValore());
		}

		// Autenticazione
		String autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_NONE.toString();
		String tmp = null;
		if(serv.getAutenticazione()!=null){
			tmp = serv.getAutenticazione().toString();
		}
		if(CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString().equalsIgnoreCase(tmp))
			autenticazione = CostantiConfigurazione.INVOCAZIONE_SERVIZIO_AUTENTICAZIONE_BASIC.toString();


		// Credenziali
		InvocazioneCredenziali credenziali = null;
		if(serv.getCredenziali()!=null){
			credenziali = new InvocazioneCredenziali();
			credenziali.setUser(serv.getCredenziali().getUser());
			credenziali.setPassword(serv.getCredenziali().getPassword());
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

	protected GestioneErrore getGestioneErroreConnettore_RispostaAsincrona(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding, Connection connectionPdD,ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		//  Servizio applicativo
		if(sa.getRispostaAsincrona()==null ||
				sa.getRispostaAsincrona().getGestioneErrore()==null	)
			return getGestioneErroreConnettoreComponenteIntegrazione(protocolFactory, serviceBinding, connectionPdD);
		RispostaAsincrona asincrona = sa.getRispostaAsincrona();
		return asincrona.getGestioneErrore();

	}

	protected boolean consegnaRispostaAsincronaPerRiferimento(ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		//	  Servizio applicativo
		if(sa==null)
			return false;
		if(sa.getRispostaAsincrona()==null)
			return false;
		return CostantiConfigurazione.ABILITATO.equals(sa.getRispostaAsincrona().getInvioPerRiferimento());
	}

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
	 * Restituisce le informazioni necessarie alla porta di dominio per accedere ai dati di autorizzazione
	 *
	 * @return informazioni
	 * 
	 */
	private static AccessoDatiAutenticazione accessoDatiAutenticazione = null;
	private static Boolean accessoDatiAutenticazioneLetto = false;
	protected AccessoDatiAutenticazione getAccessoDatiAutenticazione(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.accessoDatiAutenticazioneLetto==false){
			AccessoDatiAutenticazione tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoDatiAutenticazione(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("getAccessoDatiAutenticazione (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("getAccessoDatiAutenticazione",e);
			}

			ConfigurazionePdDReader.accessoDatiAutenticazione = tmp;
			ConfigurazionePdDReader.accessoDatiAutenticazioneLetto = true;
		}

		/*
		if(ConfigurazionePdDReader.accessoDatiAutenticazione.getCache()==null){
			System.out.println("ACCESSO_DATI_AUTHN CACHE DISABILITATA");
		}else{
			System.out.println("ACCESSO_DATI_AUTHN CACHE ABILITATA");
			System.out.println("ACCESSO_DATI_AUTHN CACHE ALGORITMO: "+ConfigurazionePdDReader.accessoDatiAutenticazione.getCache().getAlgoritmo());
			System.out.println("ACCESSO_DATI_AUTHN CACHE DIMENSIONE: "+ConfigurazionePdDReader.accessoDatiAutenticazione.getCache().getDimensione());
			System.out.println("ACCESSO_DATI_AUTHN CACHE ITEM IDLE: "+ConfigurazionePdDReader.accessoDatiAutenticazione.getCache().getItemIdleTime());
			System.out.println("ACCESSO_DATI_AUTHN CACHE ITEM LIFE SECOND: "+ConfigurazionePdDReader.accessoDatiAutenticazione.getCache().getItemLifeSecond());
		}
		 */

		return ConfigurazionePdDReader.accessoDatiAutenticazione;
	}

	/**
	 * Restituisce le informazioni necessarie alla porta di dominio per accedere ai dati di gestione dei token
	 *
	 * @return informazioni
	 * 
	 */
	private static AccessoDatiGestioneToken accessoDatiGestioneToken = null;
	private static Boolean accessoDatiGestioneTokenLetto = false;
	protected AccessoDatiGestioneToken getAccessoDatiGestioneToken(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.accessoDatiGestioneTokenLetto==false){
			AccessoDatiGestioneToken tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoDatiGestioneToken(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("getAccessoDatiGestioneToken (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("getAccessoDatiGestioneToken",e);
			}

			ConfigurazionePdDReader.accessoDatiGestioneToken = tmp;
			ConfigurazionePdDReader.accessoDatiGestioneTokenLetto = true;
		}

		/*
		if(ConfigurazionePdDReader.accessoDatiGestioneToken.getCache()==null){
			System.out.println("ACCESSO_DATI_TOKEN CACHE DISABILITATA");
		}else{
			System.out.println("ACCESSO_DATI_TOKEN CACHE ABILITATA");
			System.out.println("ACCESSO_DATI_TOKEN CACHE ALGORITMO: "+ConfigurazionePdDReader.accessoDatiGestioneToken.getCache().getAlgoritmo());
			System.out.println("ACCESSO_DATI_TOKEN CACHE DIMENSIONE: "+ConfigurazionePdDReader.accessoDatiGestioneToken.getCache().getDimensione());
			System.out.println("ACCESSO_DATI_TOKEN CACHE ITEM IDLE: "+ConfigurazionePdDReader.accessoDatiGestioneToken.getCache().getItemIdleTime());
			System.out.println("ACCESSO_DATI_TOKEN CACHE ITEM LIFE SECOND: "+ConfigurazionePdDReader.accessoDatiGestioneToken.getCache().getItemLifeSecond());
		}
		 */

		return ConfigurazionePdDReader.accessoDatiGestioneToken;
	}

	/**
	 * Restituisce le informazioni necessarie alla porta di dominio per accedere ai dati di gestione dei keystore
	 *
	 * @return informazioni
	 * 
	 */
	private static AccessoDatiKeystore accessoDatiKeystore = null;
	private static Boolean accessoDatiKeystoreLetto = false;
	protected AccessoDatiKeystore getAccessoDatiKeystore(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.accessoDatiKeystoreLetto==false){
			AccessoDatiKeystore tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoDatiKeystore(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.log.debug("getAccessoDatiKeystore (not found): "+e.getMessage());
			}catch(Exception e){
				this.log.error("getAccessoDatiKeystore",e);
			}

			ConfigurazionePdDReader.accessoDatiKeystore = tmp;
			ConfigurazionePdDReader.accessoDatiKeystoreLetto = true;
		}

		/*
		if(ConfigurazionePdDReader.accessoDatiKeystore.getCache()==null){
			System.out.println("ACCESSO_DATI_KEYSTORE CACHE DISABILITATA");
		}else{
			System.out.println("ACCESSO_DATI_KEYSTORE CACHE ABILITATA");
			System.out.println("ACCESSO_DATI_KEYSTORE CACHE ALGORITMO: "+ConfigurazionePdDReader.accessoDatiKeystore.getCache().getAlgoritmo());
			System.out.println("ACCESSO_DATI_KEYSTORE CACHE DIMENSIONE: "+ConfigurazionePdDReader.accessoDatiKeystore.getCache().getDimensione());
			System.out.println("ACCESSO_DATI_KEYSTORE CACHE ITEM IDLE: "+ConfigurazionePdDReader.accessoDatiKeystore.getCache().getItemIdleTime());
			System.out.println("ACCESSO_DATI_KEYSTORE CACHE ITEM LIFE SECOND: "+ConfigurazionePdDReader.accessoDatiKeystore.getCache().getItemLifeSecond());
			System.out.println("ACCESSO_DATI_KEYSTORE CACHE ITEM LIFE SECOND: "+ConfigurazionePdDReader.accessoDatiKeystore.getCrlItemLifeSecond());
		}
		 */

		return ConfigurazionePdDReader.accessoDatiKeystore;
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
					longValue = Long.valueOf(configurazione.getInoltroBusteNonRiscontrate().getCadenza());
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
					StatoFunzionalita read = configurazione.getTracciamento().getStato();	   
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


	private static Transazioni transazioniConfigurazione = null;
	public Transazioni getTransazioniConfigurazione(Connection connectionPdD) {

		if( this.configurazioneDinamica || ConfigurazionePdDReader.transazioniConfigurazione==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getTransazioniConfigurazione (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getTransazioniConfigurazione",e);
				}

				if(configurazione!=null && configurazione.getTransazioni()!=null){
					ConfigurazionePdDReader.transazioniConfigurazione = configurazione.getTransazioni();
				}else{
					ConfigurazionePdDReader.transazioniConfigurazione = new Transazioni(); // default
				}

			}catch(Exception e){
				ConfigurazionePdDReader.transazioniConfigurazione = new Transazioni(); // default
			}
		}

		return ConfigurazionePdDReader.transazioniConfigurazione;

	}

	private static DumpConfigurazione dumpConfigurazione = null;
	public DumpConfigurazione getDumpConfigurazione(Connection connectionPdD) {

		if( this.configurazioneDinamica || ConfigurazionePdDReader.dumpConfigurazione==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getDumpConfigurazione (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getDumpConfigurazione",e);
				}

				if(configurazione!=null && configurazione.getDump()!=null){
					ConfigurazionePdDReader.dumpConfigurazione = configurazione.getDump().getConfigurazione();
				}else{
					ConfigurazionePdDReader.dumpConfigurazione = new DumpConfigurazione(); // default tutto abilitato
				}

			}catch(Exception e){
				ConfigurazionePdDReader.dumpConfigurazione = new DumpConfigurazione(); // default tutto abilitato
			}
		}

		return ConfigurazionePdDReader.dumpConfigurazione;

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

				if(configurazione!=null && configurazione.getDump()!=null){
					StatoFunzionalita read = configurazione.getDump().getDumpBinarioPortaDelegata();	   
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

				if(configurazione!=null && configurazione.getDump()!=null){
					StatoFunzionalita read = configurazione.getDump().getDumpBinarioPortaApplicativa();	   
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

	/**
	 * Restituisce gli appender personalizzati per la registrazione dei contenuti
	 *
	 * @return Restituisce gli appender personalizzati per la registrazione dei contenuti
	 */
	private static Dump openSPCoopAppender_Dump = null;
	private static Boolean openSPCoopAppender_DumpLetto = false;
	protected Dump getOpenSPCoopAppender_Dump(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.openSPCoopAppender_DumpLetto==false){
			try{

				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.log.debug("getOpenSPCoopAppender_Dump (not found): "+e.getMessage());
				}catch(Exception e){
					this.log.error("getOpenSPCoopAppender_Dump",e);
				}

				if(configurazione!=null)
					ConfigurazionePdDReader.openSPCoopAppender_Dump = configurazione.getDump();	   

			}catch(Exception e){
				ConfigurazionePdDReader.openSPCoopAppender_Dump = null;
			}
			ConfigurazionePdDReader.openSPCoopAppender_DumpLetto = true;
		}

		return ConfigurazionePdDReader.openSPCoopAppender_Dump;
	}

	/**
	 * Restituisce la politica di gestione della consegna tramite connettore per il componente di cooperazione
	 *
	 * @return la politica di gestione della consegna tramite connettore per il componente di cooperazione
	 * 
	 */
	private static HashMap<String, GestioneErrore> gestioneErroreConnettoreComponenteCooperazioneMap = new HashMap<>();
	protected GestioneErrore getGestioneErroreConnettoreComponenteCooperazione(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding, Connection connectionPdD){

		String key = protocolFactory.getProtocol()+"_"+serviceBinding;
		
		if( this.configurazioneDinamica || !ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazioneMap.containsKey(key)){
			GestioneErrore gestione = null;
			try{
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
					gestione = GestoreErroreConnettore.getGestioneErroreDefaultComponenteCooperazione(protocolFactory, serviceBinding);
				
			}catch(Exception e){
				gestione = GestoreErroreConnettore.getGestioneErroreDefaultComponenteCooperazione(protocolFactory, serviceBinding);
			} 
			
			if(this.configurazioneDinamica) {
				return gestione;
			}
			else {
				synchronized (ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazioneMap) {
					if(!ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazioneMap.containsKey(key)) {
						ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazioneMap.put(key, gestione);
					}
				}
			}
			
		}

		return ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazioneMap.get(key);

	}

	/**
	 * Restituisce la politica di gestione della consegna tramite connettore per il componente di integrazione
	 *
	 * @return la politica di gestione della consegna tramite connettore per il componente di integrazione
	 * 
	 */
	private static HashMap<String, GestioneErrore> gestioneErroreConnettoreComponenteIntegrazioneMap = new HashMap<>();
	protected GestioneErrore getGestioneErroreConnettoreComponenteIntegrazione(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding, Connection connectionPdD){

		String key = protocolFactory.getProtocol()+"_"+serviceBinding;
		
		if( this.configurazioneDinamica || !ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazioneMap.containsKey(key)){
			GestioneErrore gestione = null;
			try{
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
					gestione = GestoreErroreConnettore.getGestioneErroreDefaultComponenteIntegrazione(protocolFactory, serviceBinding);

			}catch(Exception e){
				gestione = GestoreErroreConnettore.getGestioneErroreDefaultComponenteIntegrazione(protocolFactory, serviceBinding);
			}
			
			if(this.configurazioneDinamica) {
				return gestione;
			}
			else {
				synchronized (ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazioneMap) {
					if(!ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazioneMap.containsKey(key)) {
						ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazioneMap.put(key, gestione);
					}
				}
			}
		}

		return ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazioneMap.get(key);

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
					ConfigurazionePdDReader.integrationManagerAuthentication = new String [] { CostantiConfigurazione.CREDENZIALE_BASIC.toString(),
							CostantiConfigurazione.CREDENZIALE_SSL.toString() };
				}else {

					String [] values =  configurazione.getIntegrationManager().getAutenticazione().split(",");
					List<String> v = new ArrayList<String>();
					ClassNameProperties classNameProperties = ClassNameProperties.getInstance();
					for(int i=0; i<values.length; i++){
						values[i] = values[i].trim();
						if(classNameProperties.getAutenticazionePortaDelegata(values[i])==null){
							this.log.error("Meccanismo di autenticazione ["+values[i]+"] non registrato in GovWay");
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
						CostantiConfigurazione.CREDENZIALE_SSL.toString() };
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
				CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.toString().equalsIgnoreCase(stato) || 
				CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_XSD.toString().equalsIgnoreCase(stato) || 
				CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_OPENSPCOOP.toString().equalsIgnoreCase(stato) ) ){
			if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.toString().equalsIgnoreCase(stato))
				valDefault.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE);
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
					}else if(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE.equals(configurazione.getValidazioneContenutiApplicativi().getTipo())){
						val.setTipo(CostantiConfigurazione.VALIDAZIONE_CONTENUTI_APPLICATIVI_INTERFACE);
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



	protected PolicyNegoziazioneToken getPolicyNegoziazioneToken(Connection connectionPdD, boolean forceNoCache, String policyName) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(policyName==null){
			throw new DriverConfigurazioneException("Policy non fornita");
		}

		GenericProperties gp = this.configurazionePdD.getGenericProperties(connectionPdD, forceNoCache, org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA_RETRIEVE, policyName);

		PolicyNegoziazioneToken policy = null;
		try {
			policy = TokenUtilities.convertTo(gp);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

		return policy;
	}

	public GenericProperties getGenericProperties(Connection connectionPdD, String tipologia, String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		return this.configurazionePdD.getGenericProperties(connectionPdD, tipologia, nome);

	}

	public List<GenericProperties> getGenericProperties(Connection connectionPdD, String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		return this.configurazionePdD.getGenericProperties(connectionPdD, tipologia);

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



	public CorsConfigurazione getConfigurazioneCORS(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		CorsConfigurazione cors = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getGestioneCors();
		if(cors==null) {
			return new CorsConfigurazione();
		}
		return cors;
	}

	public ConfigurazioneMultitenant getConfigurazioneMultitenant(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		ConfigurazioneMultitenant conf = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getMultitenant();
		if(conf==null) {
			return new ConfigurazioneMultitenant();
		}
		return conf;
	}

	public ResponseCachingConfigurazione getConfigurazioneResponseCaching(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		ResponseCachingConfigurazioneGenerale c = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getResponseCaching();
		if(c==null) {
			c = new ResponseCachingConfigurazioneGenerale();
		}
		if(c.getConfigurazione()==null) {
			c.setConfigurazione(new ResponseCachingConfigurazione());
		}
		return c.getConfigurazione();
	}

	public Cache getConfigurazioneResponseCachingCache(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		ResponseCachingConfigurazioneGenerale c = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getResponseCaching();
		if(c==null) {
			c = new ResponseCachingConfigurazioneGenerale();
		}
		return c.getCache();
	}

	public Cache getConfigurazioneConsegnaApplicativiCache(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		AccessoDatiConsegnaApplicativi c = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getAccessoDatiConsegnaApplicativi();
		if(c==null) {
			c = new AccessoDatiConsegnaApplicativi();
		}
		return c.getCache();
	}

	public CanaliConfigurazione getCanaliConfigurazione(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		CanaliConfigurazione c = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getGestioneCanali();
		if(c==null) {
			c = new CanaliConfigurazione();
			c.setStato(StatoFunzionalita.DISABILITATO);
		}
		return c;
	}
	
	public ConfigurazioneCanaliNodo getConfigurazioneCanaliNodo(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
		CanaliConfigurazione c = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getGestioneCanali();
		boolean isEnabled = (c!=null && StatoFunzionalita.ABILITATO.equals(c.getStato()));
		
		ConfigurazioneCanaliNodo ccn = new ConfigurazioneCanaliNodo();
		ccn.setEnabled(isEnabled);
		if(isEnabled) {
			OpenSPCoop2Properties op2Prop = OpenSPCoop2Properties.getInstance();
			String idNodo = op2Prop.getClusterId(false);
			ccn.setIdNodo(idNodo);
			
			if(c.sizeCanaleList()<=0) {
				throw new DriverConfigurazioneException("Non risultano canali registrati nella configurazione"); 
			}
			for (CanaleConfigurazione canale : c.getCanaleList()) {
				if(canale.isCanaleDefault()) {
					ccn.setCanaleDefault(canale.getNome());
					break;
				}
			}
			if(ccn.getCanaleDefault()==null || "".equals(ccn.getCanaleDefault())) {
				throw new DriverConfigurazioneException("Non risulta definito un canale di default"); 
			}
			
			List<String> canaliNodo = new ArrayList<String>();
			if(ccn.getIdNodo()==null || "".equals(ccn.getIdNodo())) {
				canaliNodo.add(ccn.getCanaleDefault());
			}
			else if(c.sizeNodoList()<=0) {
				canaliNodo.add(ccn.getCanaleDefault());
			}
			else {
				for (CanaleConfigurazioneNodo nodo : c.getNodoList()) {
					if(ccn.getIdNodo().equals(nodo.getNome())) {
						if(nodo.sizeCanaleList()<=0) {
							throw new DriverConfigurazioneException("Non risultano canali associati al nodo '"+ccn.getIdNodo()+"'"); 
						}
						for (String canale : nodo.getCanaleList()) {
							canaliNodo.add(canale);
						}
						break;
					}
				}
				if(canaliNodo.isEmpty()) {
					canaliNodo.add(ccn.getCanaleDefault());
				}
			}
			ccn.setCanaliNodo(canaliNodo);
		}
		
		return ccn;
	}
	
	public UrlInvocazioneAPI getConfigurazioneUrlInvocazione(Connection connectionPdD, 
			IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, String interfaceName, IDSoggetto soggettoOperativo,
			AccordoServizioParteComune aspc) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		List<String> tags = new ArrayList<String>();
		if(aspc!=null && aspc.getGruppi()!=null && aspc.getGruppi().sizeGruppoList()>0) {
			for (int i = 0; i < aspc.getGruppi().sizeGruppoList(); i++) {
				tags.add(aspc.getGruppi().getGruppo(i).getNome());
			}
		}
		
		String canaleApi = null;
		if(aspc!=null) {
			canaleApi = aspc.getCanale();
		}

		return getConfigurazioneUrlInvocazione(connectionPdD, protocolFactory, ruolo, serviceBinding, interfaceName, soggettoOperativo, tags, canaleApi);
	}	
	public UrlInvocazioneAPI getConfigurazioneUrlInvocazione(Connection connectionPdD, 
			IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, String interfaceName, IDSoggetto soggettoOperativo,
			List<String> tags, 
			String canaleApi) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Configurazione config = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);

		ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = null;
		if(config!=null && config.getUrlInvocazione()!=null) {
			configurazioneUrlInvocazione = config.getUrlInvocazione();
		}

		String canale = null;
		if(config.getGestioneCanali()!=null && StatoFunzionalita.ABILITATO.equals(config.getGestioneCanali().getStato()) && config.getGestioneCanali().sizeCanaleList()>0) {
			if(RuoloContesto.PORTA_APPLICATIVA.equals(ruolo)) {
				PortaApplicativa pa = null;
				try {
					IDPortaApplicativa idPA = new IDPortaApplicativa();
					idPA.setNome(interfaceName);
					pa = this.configurazionePdD.getPortaApplicativa(connectionPdD, idPA);
				}catch(DriverConfigurazioneNotFound notFound) {}
				canale = CanaliUtils.getCanale(config.getGestioneCanali(), canaleApi, pa);
			}
			else {
				PortaDelegata pd = null;
				try {
					IDPortaDelegata idPD = new IDPortaDelegata();
					idPD.setNome(interfaceName);
					pd = this.configurazionePdD.getPortaDelegata(connectionPdD, idPD);
				}catch(DriverConfigurazioneNotFound notFound) {}
				canale = CanaliUtils.getCanale(config.getGestioneCanali(), canaleApi, pd);
			}
		}
		
		return UrlInvocazioneAPI.getConfigurazioneUrlInvocazione(configurazioneUrlInvocazione, protocolFactory, ruolo, serviceBinding, interfaceName, soggettoOperativo,
				tags, canale);
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

	private static Map<String, Object> getSingleExtendedInfoConfigurazione = new java.util.Hashtable<String, Object>(); 
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




	/* ********  R I C E R C A  I D   E L E M E N T I   P R I M I T I V I  ******** */

	public List<IDPortaDelegata> getAllIdPorteDelegate(FiltroRicercaPorteDelegate filtroRicerca,Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdD.getAllIdPorteDelegate(filtroRicerca,connectionPdD);
	}

	public List<IDPortaApplicativa> getAllIdPorteApplicative(FiltroRicercaPorteApplicative filtroRicerca,Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdD.getAllIdPorteApplicative(filtroRicerca,connectionPdD);
	}

	public List<IDServizioApplicativo> getAllIdServiziApplicativi(FiltroRicercaServiziApplicativi filtroRicerca,Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		return this.configurazionePdD.getAllIdServiziApplicativi(filtroRicerca,connectionPdD);
	}


	/* ******** CONTROLLO TRAFFICO ******** */

	public ConfigurazioneGenerale getConfigurazioneControlloTraffico(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getConfigurazioneControlloTraffico(connectionPdD);
	}

	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveAPI(Connection connectionPdD, boolean useCache, TipoPdD tipoPdD, String nomePorta) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getElencoIdPolicyAttiveAPI(connectionPdD, useCache, tipoPdD, nomePorta);
	}

	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveGlobali(Connection connectionPdD, boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getElencoIdPolicyAttiveGlobali(connectionPdD, useCache);
	}

	public AttivazionePolicy getAttivazionePolicy(Connection connectionPdD, boolean useCache, String id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getAttivazionePolicy(connectionPdD, useCache, id);
	}

	public ElencoIdPolicy getElencoIdPolicy(Connection connectionPdD, boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getElencoIdPolicy(connectionPdD, useCache);
	}

	public ConfigurazionePolicy getConfigurazionePolicy(Connection connectionPdD, boolean useCache, String id) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getConfigurazionePolicy(connectionPdD, useCache, id);
	}


	/* ******** MAPPING ******** */

	public List<MappingErogazionePortaApplicativa> getMappingErogazionePortaApplicativaList(IDServizio idServizio,Connection connectionPdD) throws DriverConfigurazioneException{
		return this.configurazionePdD.getMappingErogazionePortaApplicativaList(idServizio, connectionPdD);
	} 
	public List<MappingFruizionePortaDelegata> getMappingFruizionePortaDelegataList(IDSoggetto idFruitore, IDServizio idServizio,Connection connectionPdD) throws DriverConfigurazioneException{
		return this.configurazionePdD.getMappingFruizionePortaDelegataList(idFruitore, idServizio, connectionPdD);
	}
	
	
	/* ******** FORWARD PROXY ******** */
	
	public boolean isForwardProxyEnabled() {
		return this.configurazionePdD.isForwardProxyEnabled();
	}
	public ForwardProxy getForwardProxyConfigFruizione(IDSoggetto dominio, IDServizio idServizio) throws DriverConfigurazioneException{
		return this.configurazionePdD.getForwardProxyConfigFruizione(dominio, idServizio);
	}
	public ForwardProxy getForwardProxyConfigErogazione(IDSoggetto dominio, IDServizio idServizio ) throws DriverConfigurazioneException{
		return this.configurazionePdD.getForwardProxyConfigErogazione(dominio, idServizio);
	}
	
	
	/* ********  GENERIC FILE  ******** */

	public ContentFile getContentFile(File file) throws DriverConfigurazioneException{
		return this.configurazionePdD.getContentFile(file);
	}

}
