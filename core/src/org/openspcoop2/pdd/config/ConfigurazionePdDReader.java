/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.utils.FiltroRicercaAllarmi;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.IMonitoraggioRisorsa;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoConfigurazionePdD;
import org.openspcoop2.core.config.AccessoDatiAttributeAuthority;
import org.openspcoop2.core.config.AccessoDatiAutenticazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoDatiConsegnaApplicativi;
import org.openspcoop2.core.config.AccessoDatiGestioneToken;
import org.openspcoop2.core.config.AccessoDatiKeystore;
import org.openspcoop2.core.config.AccessoDatiRichieste;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AutorizzazioneRuoli;
import org.openspcoop2.core.config.Cache;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaleConfigurazioneNodo;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneGeneraleHandler;
import org.openspcoop2.core.config.ConfigurazioneHandler;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ConfigurazionePortaHandler;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.CorrelazioneApplicativa;
import org.openspcoop2.core.config.CorrelazioneApplicativaRisposta;
import org.openspcoop2.core.config.CorsConfigurazione;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.GestioneToken;
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
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
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
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.driver.xml.DriverConfigurazioneXML;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.StatoCheck;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicy;
import org.openspcoop2.core.controllo_traffico.ElencoIdPolicyAttive;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDConnettore;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.RuoliSoggetto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.IDriverRegistroServiziGet;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.wsdl.WSDLValidatorConfig;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.mtom.MtomXomPackageInfo;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.monitor.engine.dynamic.IRegistroPluginsReader;
import org.openspcoop2.monitor.sdk.alarm.AlarmStatus;
import org.openspcoop2.monitor.sdk.alarm.IAlarm;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.ValidatoreMessaggiApplicativiRest;
import org.openspcoop2.pdd.core.autorizzazione.canali.CanaliUtils;
import org.openspcoop2.pdd.core.byok.BYOKUnwrapPolicyUtilities;
import org.openspcoop2.pdd.core.connettori.ConnettoreCheck;
import org.openspcoop2.pdd.core.connettori.ConnettoreHTTPSProperties;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.GestoreErroreConnettore;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.pdd.core.controllo_traffico.DimensioneMessaggiConfigurationUtils;
import org.openspcoop2.pdd.core.controllo_traffico.ReadTimeoutConfigurationUtils;
import org.openspcoop2.pdd.core.controllo_traffico.ReadTimeoutContextParam;
import org.openspcoop2.pdd.core.controllo_traffico.SogliaReadTimeout;
import org.openspcoop2.pdd.core.controllo_traffico.SoglieDimensioneMessaggi;
import org.openspcoop2.pdd.core.controllo_traffico.policy.InterceptorPolicyUtilities;
import org.openspcoop2.pdd.core.controllo_traffico.policy.config.PolicyConfiguration;
import org.openspcoop2.pdd.core.dynamic.DynamicMapBuilderUtils;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.pdd.core.integrazione.HeaderIntegrazione;
import org.openspcoop2.pdd.core.token.AbstractPolicyToken;
import org.openspcoop2.pdd.core.token.Costanti;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.PolicyGestioneToken;
import org.openspcoop2.pdd.core.token.PolicyNegoziazioneToken;
import org.openspcoop2.pdd.core.token.TokenException;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.AttributeAuthorityUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.PolicyAttributeAuthority;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.constants.ModalitaIdentificazioneAzione;
import org.openspcoop2.protocol.engine.mapping.IdentificazioneDinamicaException;
import org.openspcoop2.protocol.engine.mapping.OperationFinder;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.registry.CertificateCheck;
import org.openspcoop2.protocol.registry.CertificateUtils;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.registry.RegistroServiziReader;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.jose.JOSEUtils;
import org.openspcoop2.security.message.utils.SecurityUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.cache.CacheType;
import org.openspcoop2.utils.certificate.CertificateInfo;
import org.openspcoop2.utils.certificate.KeystoreParams;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.IBYOKUnwrapManager;
import org.openspcoop2.utils.transport.http.SSLConfig;
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
	/**	private RegistroServiziReader registroServiziReader;*/

	/** Logger utilizzato per debug. */
	private Logger logger = null;
	private void logDebug(String msg) {
		this.logDebug(msg, null);
	}
	private void logDebug(String msg, Throwable e) {
		if(this.logger!=null) {
			if(e!=null) {
				this.logger.debug(msg, e);
			}
			else {
				this.logger.debug(msg);
			}
		}
	}
	private void logInfo(String msg) {
		this.logInfo(msg, null);
	}
	private void logInfo(String msg, Throwable e) {
		if(this.logger!=null) {
			if(e!=null) {
				this.logger.info(msg, e);
			}
			else {
				this.logger.info(msg);
			}
		}
	}
	@SuppressWarnings("unused")
	private void logWarn(String msg) {
		this.logWarn(msg, null);
	}
	private void logWarn(String msg, Throwable e) {
		if(this.logger!=null) {
			if(e!=null) {
				this.logger.warn(msg, e);
			}
			else {
				this.logger.warn(msg);
			}
		}
	}
	private void logError(String msg) {
		this.logError(msg, null);
	}
	private void logError(String msg, Throwable e) {
		if(this.logger!=null) {
			if(e!=null) {
				this.logger.error(msg, e);
			}
			else {
				this.logger.error(msg);
			}
		}
	}
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
	public static List<String> keysCache() throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				return configurazionePdDReader.configurazionePdD.keysCache();
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
	
	public static Object getRawObjectCache(String key) throws DriverConfigurazioneException{
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				return configurazionePdDReader.configurazionePdD.getRawObjectCache(key);
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
	
	public static org.openspcoop2.utils.cache.Cache getCache() throws DriverConfigurazioneException {
		try{
			ConfigurazionePdDReader configurazionePdDReader = org.openspcoop2.pdd.config.ConfigurazionePdDReader.getInstance();
			if(configurazionePdDReader!=null && configurazionePdDReader.configurazionePdD!=null){
				return configurazionePdDReader.configurazionePdD.getCache();
			}
			else{
				throw new DriverConfigurazioneException("ConfigurazionePdD Non disponibile");
			}
		}catch(Exception e){
			throw new DriverConfigurazioneException("Lettura cache della Configurazione non riuscita: "+e.getMessage(),e);
		}
	} 
	
	
	
	/*----------------- CLEANER --------------------*/
	
	public static void removeErogazione(IDServizio idServizio) throws Exception {
		removeApiImpl(null, idServizio, true);
	}
	public static void removeFruizione(IDSoggetto fruitore, IDServizio idServizio) throws Exception {
		removeApiImpl(fruitore, idServizio, false);
	}
	private static void removeApiImpl(IDSoggetto idFruitore, IDServizio idServizio, boolean erogazione) throws Exception {
		if(ConfigurazionePdDReader.isCacheAbilitata()) {
			
			boolean soggettiVirtuali = OpenSPCoop2Properties.getInstance().isSoggettiVirtualiEnabled();
			
			if(soggettiVirtuali) {
				String keyServizi_SoggettiVirtuali = ConfigurazionePdD.getKeyMethodGetServiziSoggettiVirtuali();
				Object oServizi_SoggettiVirtuali = ConfigurazionePdDReader.getRawObjectCache(keyServizi_SoggettiVirtuali);
				if(oServizi_SoggettiVirtuali!=null && oServizi_SoggettiVirtuali instanceof List<?>) {
					List<?> l = (List<?>) oServizi_SoggettiVirtuali;
					if(l!=null && !l.isEmpty()) {
						boolean checkAzione = true;
						for (Object object : l) {
							if(object!=null && object instanceof IDServizio) {
								IDServizio idS = (IDServizio) object;
								if(idS.equals(idServizio, !checkAzione)) {
									ConfigurazionePdDReader.removeObjectCache(keyServizi_SoggettiVirtuali);
									break;
								}
							}
						}
					}
				}
			}
			
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = ConfigurazionePdDReader.keysCache();
			if(keys!=null && !keys.isEmpty()) {
				
				String prefixPorta = null;
				if(erogazione) {
					prefixPorta = ConfigurazionePdD._getKey_MappingErogazionePortaApplicativaList(idServizio, false);
				}
				else {
					prefixPorta = ConfigurazionePdD._getKey_MappingFruizionePortaDelegataList(idFruitore, idServizio, false);
				}
				
				String prefixForwardProxy = ConfigurazionePdD._toKey_ForwardProxyConfigPrefix(!erogazione);
				String suffixForwardProxy = ConfigurazionePdD._toKey_ForwardProxyConfigSuffix(idServizio);
				
				String prefixPorteApplicativeRicercaPuntuale = null;
				String prefixPorteApplicativeRicercaNonPuntuale = null;
				if(erogazione) {
					prefixPorteApplicativeRicercaPuntuale = ConfigurazionePdD._toKey_getPorteApplicativePrefix(idServizio, true);
					prefixPorteApplicativeRicercaNonPuntuale = ConfigurazionePdD._toKey_getPorteApplicativePrefix(idServizio, false);
				}
				
				String prefixPorteApplicativeVirtualiRicercaPuntuale = null;
				String prefixPorteApplicativeVirtualiRicercaNonPuntuale = null;
				String porteApplicativeVirtualiIdServizio = null;
				if(soggettiVirtuali) {
					prefixPorteApplicativeVirtualiRicercaPuntuale = ConfigurazionePdD._toKey_getPorteApplicativeVirtualiPrefix(true);
					prefixPorteApplicativeVirtualiRicercaNonPuntuale = ConfigurazionePdD._toKey_getPorteApplicativeVirtualiPrefix(false);
					porteApplicativeVirtualiIdServizio = ConfigurazionePdD._toKey_getPorteApplicativeVirtuali_idServizio(idServizio);
				}
				
				String prefixPorteApplicativeSoggettiVirtuali = null;
				if(soggettiVirtuali) {
					prefixPorteApplicativeSoggettiVirtuali = ConfigurazionePdD.toKeyMethodGetPorteApplicativeSoggettiVirtualiPrefix(idServizio);
				}
				
				for (String key : keys) {
					if(key!=null) {
						if(key.startsWith(prefixPorta) ) {
							keyForClean.add(key);
						}
						else if(key.startsWith(prefixForwardProxy) && key.endsWith(suffixForwardProxy) ) {
							keyForClean.add(key);
						}
						else if(erogazione &&
								(key.startsWith(prefixPorteApplicativeRicercaPuntuale) || key.startsWith(prefixPorteApplicativeRicercaNonPuntuale)) ) {
							keyForClean.add(key);
						}
						else if( soggettiVirtuali &&
							((key.startsWith(prefixPorteApplicativeVirtualiRicercaPuntuale)) || (key.startsWith(prefixPorteApplicativeVirtualiRicercaNonPuntuale)))
							&&
							key.contains(porteApplicativeVirtualiIdServizio)) {
							keyForClean.add(key);
						}
						else if( soggettiVirtuali &&
								key.startsWith(prefixPorteApplicativeSoggettiVirtuali)) {
							keyForClean.add(key);
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					removeObjectCache(key);
				}
			}
			
		}
	}
	
	public static void removePortaDelegata(IDPortaDelegata idPD) throws Exception {
		if(ConfigurazionePdDReader.isCacheAbilitata()) {
			
			boolean removeControlloTraffico = true; 
			
			IProtocolFactory<?> protocolFactory = null;
			PorteNamingUtils namingUtils = null;
			try {
				ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
				String protocol = protocolFactoryManager.getProtocolByOrganizationType(idPD.getIdentificativiFruizione().getSoggettoFruitore().getTipo());
				protocolFactory = protocolFactoryManager.getProtocolFactoryByName(protocol);
				namingUtils = new PorteNamingUtils(protocolFactory);
			}catch(Exception t) {
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante la comprensione del protocol factory della PD ["+idPD+"]");
			}
						
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = ConfigurazionePdDReader.keysCache();
			if(keys!=null && !keys.isEmpty()) {
				
				String nomePorta = idPD.getNome(); 
				String nomePorta_normalized = null; 
				if(namingUtils!=null) {
					nomePorta_normalized = namingUtils.normalizePD(nomePorta);
				}
				
				String prefixKeyIdPD = ConfigurazionePdD._getKey_getIDPortaDelegata(nomePorta);
				String prefixKeyIdPD_normalized = null; 
				if(nomePorta_normalized!=null) {
					prefixKeyIdPD_normalized = ConfigurazionePdD._getKey_getIDPortaDelegata(nomePorta_normalized);
				}
				
				String prefixKeyPD = ConfigurazionePdD._getKey_getPortaDelegata(idPD);
				
				String prefixCT = ConfigurazionePdD.getKeyMethodElencoIdPolicyAttiveAPI(TipoPdD.DELEGATA, nomePorta);
				String prefixCTNormalized = null; 
				if(nomePorta_normalized!=null) {
					prefixCTNormalized  = ConfigurazionePdD.getKeyMethodElencoIdPolicyAttiveAPI(TipoPdD.DELEGATA, nomePorta_normalized);
				}
				
				String prefixCTDimensioneMessaggio = ConfigurazionePdD.getKeyMethodElencoIdPolicyAttiveAPIDimensioneMessaggio(TipoPdD.DELEGATA, nomePorta);
				String prefixCTDimensioneMessaggioNormalized = null; 
				if(nomePorta_normalized!=null) {
					prefixCTDimensioneMessaggioNormalized  = ConfigurazionePdD.getKeyMethodElencoIdPolicyAttiveAPIDimensioneMessaggio(TipoPdD.DELEGATA, nomePorta_normalized);
				}
				
				String prefixGetAllId = ConfigurazionePdD._toKey_getAllIdPorteDelegate_method();
				
				String prefixAttivazionePolicy = ConfigurazionePdD._toKey_AttivazionePolicyPrefix();
				
				for (String key : keys) {
					if(key!=null) {
						if(key.startsWith(prefixKeyIdPD)) {
							keyForClean.add(key);
						}
						else if(prefixKeyIdPD_normalized!=null && key.startsWith(prefixKeyIdPD_normalized)) {
							keyForClean.add(key);
						}
						else if(key.startsWith(prefixKeyPD)) {
							keyForClean.add(key);
						}
						if(removeControlloTraffico && key.startsWith(prefixCT)) {
							keyForClean.add(key);
						}
						else if(removeControlloTraffico && prefixCTNormalized!=null && key.startsWith(prefixCTNormalized)) {
							keyForClean.add(key);
						}
						if(removeControlloTraffico && key.startsWith(prefixCTDimensioneMessaggio)) {
							keyForClean.add(key);
						}
						else if(removeControlloTraffico && prefixCTDimensioneMessaggioNormalized!=null && key.startsWith(prefixCTDimensioneMessaggioNormalized)) {
							keyForClean.add(key);
						}
						else if(key.startsWith(prefixGetAllId)) {
							Object oCode = ConfigurazionePdDReader.getRawObjectCache(key);
							if(oCode!=null) {
								if(oCode instanceof List<?>) {
									List<?> l = (List<?>) oCode;
									if(l!=null && !l.isEmpty()) {
										for (Object object : l) {
											if(object!=null && object instanceof IDPortaDelegata) {
												IDPortaDelegata idPDcheck = (IDPortaDelegata) object;
												if(idPDcheck.getNome().equals(nomePorta)) {
													keyForClean.add(key);
													break;
												}
												else if(nomePorta_normalized!=null && idPDcheck.getNome().equals(nomePorta_normalized)) {
													keyForClean.add(key);
													break;
												}
											}
										}
									}
								}
								else if(oCode instanceof Exception) {
									Exception t = (Exception) oCode;
									String msg = t.getMessage();
									if(msg!=null) {
										String check = FiltroRicercaPorteDelegate.PREFIX_PORTA_DELEGANTE + nomePorta + FiltroRicercaPorteDelegate.SUFFIX_PORTA_DELEGANTE;
										if(msg.contains(check)){
											keyForClean.add(key);
										}
										else {
											if(nomePorta_normalized!=null) {
												String check_normalized = FiltroRicercaPorteDelegate.PREFIX_PORTA_DELEGANTE + nomePorta_normalized + FiltroRicercaPorteDelegate.SUFFIX_PORTA_DELEGANTE;
												if(msg.contains(check_normalized)){
													keyForClean.add(key);
												}
											}
										}
									}
								}
							}
						}
						else if(removeControlloTraffico && key.startsWith(prefixAttivazionePolicy)) {
							Object oCode = ConfigurazionePdDReader.getRawObjectCache(key);
							if(oCode!=null && oCode instanceof AttivazionePolicy) {
								AttivazionePolicy aPolicy = (AttivazionePolicy) oCode;
								if(aPolicy.getFiltro()!=null && aPolicy.getFiltro().isEnabled() && 
										RuoloPolicy.DELEGATA.equals(aPolicy.getFiltro().getRuoloPorta())){
									if(nomePorta.equals(aPolicy.getFiltro().getNomePorta())) {
										keyForClean.add(key);
									}
									else if(nomePorta_normalized!=null && nomePorta_normalized.equals(aPolicy.getFiltro().getNomePorta())) {
										keyForClean.add(key);
									}
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					removeObjectCache(key);
				}
			}
		}
	}
	
	public static void removePortaApplicativa(IDPortaApplicativa idPA) throws Exception {
		if(ConfigurazionePdDReader.isCacheAbilitata()) {
			
			boolean removeControlloTraffico = true; 
			
			IProtocolFactory<?> protocolFactory = null;
			PorteNamingUtils namingUtils = null;
			try {
				ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
				String protocol = protocolFactoryManager.getProtocolByOrganizationType(idPA.getIdentificativiErogazione().getIdServizio().getSoggettoErogatore().getTipo());
				protocolFactory = protocolFactoryManager.getProtocolFactoryByName(protocol);
				namingUtils = new PorteNamingUtils(protocolFactory);
			}catch(Exception t) {
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante la comprensione del protocol factory della PA ["+idPA+"]");
			}
							
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = ConfigurazionePdDReader.keysCache();
			if(keys!=null && !keys.isEmpty()) {
				
				String nomePorta = idPA.getNome(); 
				String nomePorta_normalized = null; 
				if(namingUtils!=null) {
					nomePorta_normalized = namingUtils.normalizePA(nomePorta);
				}
				
				String prefixKeyIdPA = ConfigurazionePdD._getKey_getIDPortaApplicativa(nomePorta);
				String prefixKeyIdPA_normalized = null; 
				if(nomePorta_normalized!=null) {
					prefixKeyIdPA_normalized = ConfigurazionePdD._getKey_getIDPortaApplicativa(nomePorta_normalized);
				}
				
				String prefixKeyPA = ConfigurazionePdD._getKey_getPortaApplicativa(idPA);
				
				String prefixCT = ConfigurazionePdD.getKeyMethodElencoIdPolicyAttiveAPI(TipoPdD.APPLICATIVA, nomePorta);
				String prefixCTNormalized = null; 
				if(nomePorta_normalized!=null) {
					prefixCTNormalized  = ConfigurazionePdD.getKeyMethodElencoIdPolicyAttiveAPI(TipoPdD.APPLICATIVA, nomePorta_normalized);
				}
				
				String prefixCTDimensioneMessaggio = ConfigurazionePdD.getKeyMethodElencoIdPolicyAttiveAPIDimensioneMessaggio(TipoPdD.APPLICATIVA, nomePorta);
				String prefixCTDimensioneMessaggioNormalized = null; 
				if(nomePorta_normalized!=null) {
					prefixCTDimensioneMessaggioNormalized  = ConfigurazionePdD.getKeyMethodElencoIdPolicyAttiveAPIDimensioneMessaggio(TipoPdD.APPLICATIVA, nomePorta_normalized);
				}
				
				String prefixGetAllId = ConfigurazionePdD._toKey_getAllIdPorteApplicative_method();
				
				String prefixAttivazionePolicy = ConfigurazionePdD._toKey_AttivazionePolicyPrefix();
				
				for (String key : keys) {
					if(key!=null) {
						if(key.startsWith(prefixKeyIdPA)) {
							keyForClean.add(key);
						}
						else if(prefixKeyIdPA_normalized!=null && key.startsWith(prefixKeyIdPA_normalized)) {
							keyForClean.add(key);
						}
						else if(key.startsWith(prefixKeyPA)) {
							keyForClean.add(key);
						}
						if(removeControlloTraffico && key.startsWith(prefixCT)) {
							keyForClean.add(key);
						}
						else if(removeControlloTraffico && prefixCTNormalized!=null && key.startsWith(prefixCTNormalized)) {
							keyForClean.add(key);
						}
						if(removeControlloTraffico && key.startsWith(prefixCTDimensioneMessaggio)) {
							keyForClean.add(key);
						}
						else if(removeControlloTraffico && prefixCTDimensioneMessaggioNormalized!=null && key.startsWith(prefixCTDimensioneMessaggioNormalized)) {
							keyForClean.add(key);
						}
						else if(key.startsWith(prefixGetAllId)) {
							Object oCode = ConfigurazionePdDReader.getRawObjectCache(key);
							if(oCode!=null) {								
								if(oCode instanceof List<?>) {
									List<?> l = (List<?>) oCode;
									if(l!=null && !l.isEmpty()) {
										for (Object object : l) {
											if(object!=null && object instanceof IDPortaApplicativa) {
												IDPortaApplicativa idPAcheck = (IDPortaApplicativa) object;
												if(idPAcheck.getNome().equals(nomePorta)) {
													keyForClean.add(key);
													break;
												}
												else if(nomePorta_normalized!=null && idPAcheck.getNome().equals(nomePorta_normalized)) {
													keyForClean.add(key);
													break;
												}
											}
										}
									}
								}
								else if(oCode instanceof Exception) {
									Exception t = (Exception) oCode;
									String msg = t.getMessage();
									if(msg!=null) {
										String check = FiltroRicercaPorteApplicative.PREFIX_PORTA_DELEGANTE + nomePorta + FiltroRicercaPorteApplicative.SUFFIX_PORTA_DELEGANTE;
										if(msg.contains(check)){
											keyForClean.add(key);
										}
										else {
											if(nomePorta_normalized!=null) {
												String check_normalized = FiltroRicercaPorteApplicative.PREFIX_PORTA_DELEGANTE + nomePorta_normalized + FiltroRicercaPorteApplicative.SUFFIX_PORTA_DELEGANTE;
												if(msg.contains(check_normalized)){
													keyForClean.add(key);
												}
											}
										}
									}
								}
							}
						}
						else if(removeControlloTraffico && key.startsWith(prefixAttivazionePolicy)) {
							Object oCode = ConfigurazionePdDReader.getRawObjectCache(key);
							if(oCode!=null && oCode instanceof AttivazionePolicy) {
								AttivazionePolicy aPolicy = (AttivazionePolicy) oCode;
								if(aPolicy.getFiltro()!=null && aPolicy.getFiltro().isEnabled() && 
										RuoloPolicy.APPLICATIVA.equals(aPolicy.getFiltro().getRuoloPorta())){
									if(nomePorta.equals(aPolicy.getFiltro().getNomePorta())) {
										keyForClean.add(key);
									}
									else if(nomePorta_normalized!=null && nomePorta_normalized.equals(aPolicy.getFiltro().getNomePorta())) {
										keyForClean.add(key);
									}
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					removeObjectCache(key);
				}
			}
		}
	}
	
	public static void removeConnettore(IDConnettore idConnettore) throws Exception {
		if(ConfigurazionePdDReader.isCacheAbilitata()) {
			
			OpenSPCoop2Properties openSPCoop2Properties = OpenSPCoop2Properties.getInstance();
			
			if(openSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitato()) {
				List<String> code = openSPCoop2Properties.getTimerConsegnaContenutiApplicativiCode();
				for (String coda : code) {
					String key = ConfigurazionePdD.getKey_getConnettoriConsegnaNotifichePrioritarie(coda);
					Object oCode = ConfigurazionePdDReader.getRawObjectCache(key);
					if(oCode!=null && oCode instanceof List<?>) {
						List<?> l = (List<?>) oCode;
						if(l!=null && !l.isEmpty()) {
							for (Object object : l) {
								if(object!=null && object instanceof IDConnettore) {
									IDConnettore idC = (IDConnettore) object;
									if(idC.equals(idConnettore)) {
										ConfigurazionePdDReader.removeObjectCache(key);
										break;
									}
								}
							}
						}
					}
				}
			}
					
		}
	}
	
	public static void removeSoggetto(IDSoggetto idSoggetto) throws Exception {
		if(ConfigurazionePdDReader.isCacheAbilitata()) {
			
			boolean soggettiVirtuali = OpenSPCoop2Properties.getInstance().isSoggettiVirtualiEnabled();
			
			String keySoggetto = ConfigurazionePdD._getKey_getSoggettoByID(idSoggetto);
			ConfigurazionePdDReader.removeObjectCache(keySoggetto);
			
			String keyRouter = ConfigurazionePdD._getKey_getRouter();
			Object oRouter = ConfigurazionePdDReader.getRawObjectCache(keyRouter);
			if(oRouter!=null && oRouter instanceof Soggetto) {
				Soggetto s = (Soggetto) oRouter;
				if(s.getTipo().equals(idSoggetto.getTipo()) 
						&&
					s.getNome().equals(idSoggetto.getNome()) ) {
					ConfigurazionePdDReader.removeObjectCache(keyRouter);
				}
			}
			
			if(soggettiVirtuali) {
				String keySoggettiVirtuali = ConfigurazionePdD._getKey_getSoggettiVirtuali();
				Object oSoggettiVirtuali = ConfigurazionePdDReader.getRawObjectCache(keySoggettiVirtuali);
				if(oSoggettiVirtuali!=null && oSoggettiVirtuali instanceof List<?>) {
					List<?> l = (List<?>) oSoggettiVirtuali;
					if(l!=null && !l.isEmpty()) {
						for (Object object : l) {
							if(object!=null && object instanceof IDSoggetto) {
								IDSoggetto idS = (IDSoggetto) object;
								if(idS.equals(idSoggetto)) {
									ConfigurazionePdDReader.removeObjectCache(keySoggettiVirtuali);
									break;
								}
							}
						}
					}
				}
			}
			
			if(soggettiVirtuali) {
				List<String> keyForClean = new ArrayList<>();
				List<String> keys = ConfigurazionePdDReader.keysCache();
				if(keys!=null && !keys.isEmpty()) {
					String prefixPorteApplicativeVirtualiRicercaPuntuale = ConfigurazionePdD._toKey_getPorteApplicativeVirtualiPrefix(true);
					String prefixPorteApplicativeVirtualiRicercaNonPuntuale = ConfigurazionePdD._toKey_getPorteApplicativeVirtualiPrefix(false);
					String porteApplicativeVirtualiIdSoggetto = ConfigurazionePdD._toKey_getPorteApplicativeVirtuali_idSoggettoVirtuale(idSoggetto);
					for (String key : keys) {
						if(key!=null) {
							if( 
								((key.startsWith(prefixPorteApplicativeVirtualiRicercaPuntuale)) || (key.startsWith(prefixPorteApplicativeVirtualiRicercaNonPuntuale)))
								&&
								key.contains(porteApplicativeVirtualiIdSoggetto)) {
								keyForClean.add(key);
							}
						}
					}
				}
				if(keyForClean!=null && !keyForClean.isEmpty()) {
					for (String key : keyForClean) {
						removeObjectCache(key);
					}
				}
			}

		}
	}
	
	public static void removeApplicativo(IDServizioApplicativo idApplicativo) throws Exception {
		if(ConfigurazionePdDReader.isCacheAbilitata()) {
			
			String keyApplicativo = ConfigurazionePdD._getKey_getServizioApplicativo(idApplicativo);
			ConfigurazionePdDReader.removeObjectCache(keyApplicativo);
			
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = ConfigurazionePdDReader.keysCache();
			if(keys!=null && !keys.isEmpty()) {
				
				String prefixCredenzialiBasic = ConfigurazionePdD._toKey_getServizioApplicativoByCredenzialiBasicPrefix();
				String prefixCredenzialiApiKey = ConfigurazionePdD._toKey_getServizioApplicativoByCredenzialiApiKeyPrefix(false);
				String prefixCredenzialiApiKeyAppId = ConfigurazionePdD._toKey_getServizioApplicativoByCredenzialiApiKeyPrefix(true);
				String prefixCredenzialiSsl = ConfigurazionePdD._toKey_getServizioApplicativoByCredenzialiSslPrefix(true);
				String prefixCredenzialiSslCert = ConfigurazionePdD._toKey_getServizioApplicativoByCredenzialiSslCertPrefix(true);
				String prefixCredenzialiPrincipal = ConfigurazionePdD._toKey_getServizioApplicativoByCredenzialiPrincipalPrefix();
				String prefixCredenzialiToken = ConfigurazionePdD._toKey_getServizioApplicativoByCredenzialiTokenPrefix();
				
				String prefixGetAllId = ConfigurazionePdD._toKey_getAllIdServiziApplicativi_method();
								
				for (String key : keys) {
					if(key!=null) {
						if(key.startsWith(prefixCredenzialiBasic) ||
								key.startsWith(prefixCredenzialiApiKey) || 
								key.startsWith(prefixCredenzialiApiKeyAppId) || 
								key.startsWith(prefixCredenzialiSsl) || 
								key.startsWith(prefixCredenzialiSslCert) || 
								key.startsWith(prefixCredenzialiPrincipal) || 
								key.startsWith(prefixCredenzialiToken)) {
							
							Object o = ConfigurazionePdDReader.getRawObjectCache(key);
							if(o!=null && o instanceof ServizioApplicativo) {
								ServizioApplicativo sa = (ServizioApplicativo) o;
								if(idApplicativo.getNome().equals(sa.getNome()) &&
										idApplicativo.getIdSoggettoProprietario().getTipo().equals(sa.getTipoSoggettoProprietario()) &&
										idApplicativo.getIdSoggettoProprietario().getNome().equals(sa.getNomeSoggettoProprietario())) {
									keyForClean.add(key);
								}
							}
							
						}
						else if(key.startsWith(prefixGetAllId)) {
							Object oCode = ConfigurazionePdDReader.getRawObjectCache(key);
							if(oCode!=null && oCode instanceof List<?>) {
								List<?> l = (List<?>) oCode;
								if(l!=null && !l.isEmpty()) {
									for (Object object : l) {
										if(object!=null && object instanceof IDServizioApplicativo) {
											IDServizioApplicativo idSAcheck = (IDServizioApplicativo) object;
											if(idSAcheck.equals(idApplicativo)) {
												keyForClean.add(key);
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					removeObjectCache(key);
				}
			}
			
			OpenSPCoop2Properties openSPCoop2Properties = OpenSPCoop2Properties.getInstance();
			
			if(openSPCoop2Properties.isTimerConsegnaContenutiApplicativiAbilitato()) {
				List<String> code = openSPCoop2Properties.getTimerConsegnaContenutiApplicativiCode();
				for (String coda : code) {
					String key = ConfigurazionePdD.getKey_getConnettoriConsegnaNotifichePrioritarie(coda);
					Object oCode = ConfigurazionePdDReader.getRawObjectCache(key);
					if(oCode!=null && oCode instanceof List<?>) {
						List<?> l = (List<?>) oCode;
						if(l!=null && !l.isEmpty()) {
							for (Object object : l) {
								if(object!=null && object instanceof IDConnettore) {
									IDConnettore idC = (IDConnettore) object;
									if(idC.getNome().equals(idApplicativo.getNome()) &&
											idC.getIdSoggettoProprietario().equals(idApplicativo.getIdSoggettoProprietario())) {
										ConfigurazionePdDReader.removeObjectCache(key);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void removeGenericProperties(IDGenericProperties idGP) throws Exception {
		if(ConfigurazionePdDReader.isCacheAbilitata()) {
			
			String keyGP = ConfigurazionePdD._getKey_getGenericProperties(idGP.getTipologia(), idGP.getNome());
			ConfigurazionePdDReader.removeObjectCache(keyGP);
			
			String keyGPTipologia = ConfigurazionePdD._getKey_getGenericProperties(idGP.getTipologia());
			ConfigurazionePdDReader.removeObjectCache(keyGPTipologia);
			
			String prefixForwardProxy = ConfigurazionePdD.PREFIX_FORWARD_PROXY;
			String forwardProxyGP = ConfigurazionePdD._toKey_ForwardProxyConfigSuffix(idGP);
			
			List<String> keyForClean = new ArrayList<>();
			List<String> keys = ConfigurazionePdDReader.keysCache();
			if(keys!=null && !keys.isEmpty()) {
				for (String key : keys) {
					if(key!=null) {
						if(forwardProxyGP!=null && key.startsWith(prefixForwardProxy)&& key.contains(forwardProxyGP)) {
							keyForClean.add(key);
						}
					}
				}
			}
			if(keyForClean!=null && !keyForClean.isEmpty()) {
				for (String key : keyForClean) {
					removeObjectCache(key);
				}
			}
			
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
			boolean prefillCache, CryptConfig configApplicativi,
			CacheType cacheType){

		try {
			ConfigurazionePdDReader.configurazionePdDReader = new ConfigurazionePdDReader(accessoConfigurazione,aLog,aLogconsole,localProperties,jndiNameDatasourcePdD, 
					forceDisableCache, useOp2UtilsDatasource, bindJMX, 
					prefillCache, configApplicativi,
					cacheType);	
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
			boolean prefillCache, CryptConfig configApplicativi,
			CacheType cacheType)throws DriverConfigurazioneException{
		try{
			if(aLog!=null)
				this.logger = aLog;
			else
				this.logger = LoggerWrapperFactory.getLogger(ConfigurazionePdDReader.class);
			this.configurazionePdD = new ConfigurazionePdD(accessoConfigurazione,this.logger,aLogconsole,localProperties,jndiNameDatasourcePdD, forceDisableCache,
					useOp2UtilsDatasource, bindJMX, 
					prefillCache, configApplicativi,
					cacheType);

			// OpenSPCoop Properties
			this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
			this.pddProperties = PddProperties.getInstance();

			// configurazioneDinamica
			this.configurazioneDinamica = this.openspcoopProperties.isConfigurazioneDinamica();

			// Server J2EE
			this.serverJ2EE = this.openspcoopProperties.isServerJ2EE();
			
			ConfigurazionePdDReader.initialize = true;
		}catch(Exception e){
			if(this.logger!=null)
				this.logError("Configurazione non inizializzata",e);
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
	protected String getIdentificativoPorta(Connection connectionPdD, IDSoggetto idSoggetto,IProtocolFactory<?> protocolFactory, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 
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
				return properties.getIdentificativoPortaDefault(protocolFactory.getProtocol(), requestInfo);
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

		if(!this.openspcoopProperties.isSoggettiVirtualiEnabled()) {
			return false;
		}
		
		// il soggetto virtuale e' stato registrato come tale?

		if(idSoggetto == null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null)
			return false;

		/** Lista di Soggetti Virtuali */
		List<IDSoggetto> listaSoggettiVirtuali = null;
		try{
			listaSoggettiVirtuali = this.configurazionePdD.getSoggettiVirtuali(connectionPdD);
		}catch(DriverConfigurazioneNotFound de){
			this.logInfo("Soggetti virtuali non presenti.");
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

	protected Soggetto getSoggetto(Connection connectionPdD, IDSoggetto idSoggetto)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{  
		Soggetto soggetto = this.configurazionePdD.getSoggetto(connectionPdD, idSoggetto);
		return soggetto;
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
			//this.logDebug("existsSoggetto (not found): "+e.getMessage());
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
	protected  List<IDServizio> getServiziSoggettiVirtuali(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getServiziSoggettiVirtuali(connectionPdD);
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
	protected Connettore getForwardRoute(Connection connectionPdD, RegistroServiziManager registroServiziManager ,IDSoggetto idSoggettoDestinatario,boolean functionAsRouter, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getForwardRoute(connectionPdD,registroServiziManager,null,this.buildIDServizioWithOnlySoggetto(idSoggettoDestinatario),functionAsRouter, requestInfo);
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
	protected Connettore getForwardRoute(Connection connectionPdD, RegistroServiziManager registroServiziManager , IDSoggetto idSoggettoMittente, IDServizio idServizio,boolean functionAsRouter, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{


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
				this.logDebug("getForwardRoute: routing table disabilitata");
			else
				this.logDebug("getForwardRoute: routing table senza rotte");

			Connettore connettoreDominio = null;
			try{
				if(idSoggettoMittente!=null && idServizio.getNome()!=null){
					connettoreDominio = registroServiziManager.getConnettore(idSoggettoMittente,idServizio,null, requestInfo); // null=allRegistri
					if(!functionAsRouter)
						setPDUrlPrefixRewriter(connectionPdD,connettoreDominio, idSoggettoMittente, requestInfo);
				}else{
					connettoreDominio = registroServiziManager.getConnettore(idServizio.getSoggettoErogatore(),null, requestInfo); // null=allRegistri
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
		this.logDebug("getForwardRoute: routing table abilitata");

		// Se la PdD contiene una forward route, utilizza la tabella di routing	
		// 2) Destinazioni specifiche
		for(int i=0;i<routingTable.sizeDestinazioneList();i++){
			if(nome.equals(routingTable.getDestinazione(i).getNome()) &&
					tipo.equals(routingTable.getDestinazione(i).getTipo())){	
				bf.append("\nRotta di destinazione ["+routingTable.getDestinazione(i).getTipo()+"/"+routingTable.getDestinazione(i).getNome()+"]...\n");
				this.logDebug("getForwardRoute: esamino routing table, destinazione ["+routingTable.getDestinazione(i).getTipo()+"/"+routingTable.getDestinazione(i).getNome()+"]");

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
							this.logDebug("getForwardRoute: esamino routing table, destinazione ["
									+routingTable.getDestinazione(i).getTipo()+"/"+routingTable.getDestinazione(i).getNome()+"] RegistroNome["+route.getRegistro().getNome()+"]");

							bf.append("\tRegistro nomeRegistro["+route.getRegistro().getNome()+"]: ");

							// Utilizzo del registro con l'identita reale della busta
							if(idSoggettoMittente!=null && idServizio.getNome()!=null){
								connettoreDominio = registroServiziManager.getConnettore(idSoggettoMittente,idServizio,route.getRegistro().getNome(), requestInfo);
								if(!functionAsRouter)
									setPDUrlPrefixRewriter(connectionPdD,connettoreDominio, idSoggettoMittente, requestInfo);
							}else{
								connettoreDominio = registroServiziManager.getConnettore(idServizio.getSoggettoErogatore(),route.getRegistro().getNome(), requestInfo);
							}
							// Registro da utilizzare anche per l'imbustamento
							if(connettoreDominio!=null && !CostantiConfigurazione.NONE.equals(connettoreDominio.getTipo()) &&
									!CostantiConfigurazione.DISABILITATO.equals(connettoreDominio.getTipo()) )
								connettoreDominio.setNomeRegistro(route.getRegistro().getNome());
						}else if(route.getGateway()!=null){ 
							this.logDebug("getForwardRoute: esamino routing table, destinazione ["
									+routingTable.getDestinazione(i).getTipo()+"/"+routingTable.getDestinazione(i).getNome()+"] GateWay["+route.getGateway().getTipo()+"/"+route.getGateway().getNome()+"]"); 
							bf.append("\tGateWay["+route.getGateway().getTipo()+"/"+route.getGateway().getNome()+"]: ");

							// Utilizzo del gateway
							IDSoggetto gateway = new IDSoggetto(route.getGateway().getTipo(),
									route.getGateway().getNome());
							soggettoGateway = " [Gateway:"+gateway.toString()+"]";
							connettoreDominio = registroServiziManager.getConnettore(gateway,null, requestInfo); //null=allRegistri
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
				this.logDebug("getForwardRoute: esamino routing table, rotta di default");
				bf.append("\nRotta di default");
				Route route = routingTable.getDefault().getRoute(i);
				Connettore connettoreDominio = null;
				boolean error = false;
				String soggettoGateway = "";
				try{
					if(route.getRegistro()!=null){ 
						this.logDebug("getForwardRoute: esamino routing table, rotta di default, Registro nomeRegistro["+route.getRegistro().getNome()+"]");
						bf.append(" Registro nomeRegistro["+route.getRegistro().getNome()+"]: ");

						// Utilizzo del registro con l'identita reale della busta
						if(idSoggettoMittente!=null && idServizio.getNome()!=null){
							connettoreDominio = registroServiziManager.getConnettore(idSoggettoMittente,idServizio,route.getRegistro().getNome(), requestInfo);
							if(!functionAsRouter)
								setPDUrlPrefixRewriter(connectionPdD,connettoreDominio, idSoggettoMittente, requestInfo);
						}else{
							connettoreDominio = registroServiziManager.getConnettore(idServizio.getSoggettoErogatore(),route.getRegistro().getNome(), requestInfo);
						}
						// Registro da utilizzare anche per l'imbustamento
						if(connettoreDominio!=null && !CostantiConfigurazione.NONE.equals(connettoreDominio.getTipo()) && 
								!CostantiConfigurazione.DISABILITATO.equals(connettoreDominio.getTipo()))
							connettoreDominio.setNomeRegistro(route.getRegistro().getNome());
					}else if(route.getGateway()!=null){ 
						this.logDebug("getForwardRoute: esamino routing table, rotta di default, GateWay["+route.getGateway().getTipo()+"/"+route.getGateway().getNome()+"]");
						bf.append(" GateWay["+route.getGateway().getTipo()+"/"+route.getGateway().getNome()+"]: ");

						// Utilizzo del gateway
						IDSoggetto gateway = new IDSoggetto(route.getGateway().getTipo(),
								route.getGateway().getNome());
						soggettoGateway = " [Gateway:"+gateway.toString()+"]";
						connettoreDominio = registroServiziManager.getConnettore(gateway,null, requestInfo);//null=allRegistri
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
	protected String getRegistroForImbustamento(Connection connectionPdD, RegistroServiziManager registroServiziManager , IDSoggetto idSoggettoMittente, IDServizio idServizio,boolean functionAsRouter, RequestInfo requestInfo)throws DriverConfigurazioneException{
		Connettore conn = null;
		try {
			conn = getForwardRoute(connectionPdD,registroServiziManager,idSoggettoMittente,idServizio,functionAsRouter, requestInfo);
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
			this.logDebug("routerFunctionActive[getRoutingTable]",e);
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
			//this.logDebug("routerFunctionActive[getRouter] (not found): "+e.getMessage());
		}	
		return router!=null;
	}

	/**
	 * Restituisce l'identita della PdD nel caso debba funzionare come router.
	 *
	 * @return idSoggetto Identita
	 * 
	 */
	protected IDSoggetto getRouterIdentity(Connection connectionPdD,IProtocolFactory<?> protocolFactory, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Per essere configurata come Router, una PdD deve possedere una tabella di routing:
		RoutingTable routingTable = null;
		try{
			routingTable = this.configurazionePdD.getRoutingTable(connectionPdD);
		}catch(Exception e){
			this.logDebug("getRouterIdentity[routingTable]",e);
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
				codicePorta = properties.getIdentificativoPortaDefault(protocolFactory.getProtocol(), requestInfo);
			}
		}
		return new IDSoggetto(router.getTipo(),router.getNome(),codicePorta);
	}











	/* ********  URLPrefixRewriter  ******** */

	protected void setPDUrlPrefixRewriter(Connection connectionPdD, org.openspcoop2.core.config.Connettore connettore, IDSoggetto idSoggettoFruitore, RequestInfo requestInfo) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		if(idSoggettoFruitore==null)
			return;
		
		Soggetto soggettoFruitore = null;
		if( requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggettoFruitore!=null) {
			if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null &&
					idSoggettoFruitore.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					idSoggettoFruitore.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				soggettoFruitore = requestInfo.getRequestConfig().getSoggettoFruitoreConfig();
			}
		}
		if(soggettoFruitore==null) {
			soggettoFruitore = this.configurazionePdD.getSoggetto(connectionPdD,idSoggettoFruitore);
		}
		setUrlPrefixRewriter(soggettoFruitore.getPdUrlPrefixRewriter(),"pdUrlPrefixRewriter", connettore);
	}
	protected void setPAUrlPrefixRewriter(Connection connectionPdD, org.openspcoop2.core.config.Connettore connettore, IDSoggetto idSoggettoErogatore, RequestInfo requestInfo) throws DriverConfigurazioneNotFound,DriverConfigurazioneException{
		if(idSoggettoErogatore==null)
			return;
		
		Soggetto soggettoErogatore = null;
		if( requestInfo!=null && requestInfo.getRequestConfig()!=null && idSoggettoErogatore!=null) {
			if(requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null &&
					idSoggettoErogatore.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					idSoggettoErogatore.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				soggettoErogatore = requestInfo.getRequestConfig().getSoggettoErogatoreConfig();
			}
		}
		if(soggettoErogatore==null) {
			if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
				if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig()!=null && idSoggettoErogatore!=null && 
						idSoggettoErogatore.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getTipo()) && 
						idSoggettoErogatore.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getNome())) {
					soggettoErogatore = requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig();
				}
			}
		}
		if(soggettoErogatore==null) {
			if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
				if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig()!=null && idSoggettoErogatore!=null && 
						idSoggettoErogatore.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getTipo()) && 
						idSoggettoErogatore.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getNome())) {
					soggettoErogatore = requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig();
				}
			}
		}
		if(soggettoErogatore==null) {
			soggettoErogatore = this.configurazionePdD.getSoggetto(connectionPdD,idSoggettoErogatore);
		}
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

			this.logDebug("["+funzione+"]  Originale["+originale+"] UrlPrefix["+urlPrefix+"] ...");

			String tmp = null;
			if(originale.contains("://")){
				tmp = originale.substring(originale.indexOf("://")+3);
			}else{
				return urlOriginale; // url prefix effettuato solo se definito un protocollo es. http://
			}

			this.logDebug("["+funzione+"]  eliminazioneProtocollo["+tmp+"] ...");

			if(tmp.contains("/")){
				tmp = tmp.substring(tmp.indexOf("/")+1);
				this.logDebug("["+funzione+"]  salvataggioContesto["+tmp+"] ...");
				if(urlFinale.endsWith("/")==false){
					urlFinale = urlFinale + "/"; 
				}
				urlFinale = urlFinale + tmp;
			}
			// else sostituisco completamente tutta la url, non avendo un contesto.

			this.logDebug("["+funzione+"]  nuova url: ["+urlFinale+"]");

			return urlFinale;

		}catch(Exception e){
			this.logError("Processo di ["+funzione+"]  fallito (urlOriginale:"+urlOriginale+") (urlPrefix:"+urlPrefix+")",e);
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

	protected PortaDelegata getPortaDelegataSafeMethod(Connection connectionPdD,IDPortaDelegata idPD)throws DriverConfigurazioneException{
		try{
			if(idPD!=null && idPD.getNome()!=null)
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
			Map<String, String> properties = new HashMap<>();

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

	protected String getAzione(RegistroServiziManager registroServiziManager,PortaDelegata pd,URLProtocolContext urlProtocolContext,RequestInfo requestInfo,
			OpenSPCoop2Message message, OpenSPCoop2MessageSoapStreamReader soapStreamReader, HeaderIntegrazione headerIntegrazione, boolean readFirstHeaderIntegrazione,
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

			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			
			boolean bufferMessageReadOnly = op2Properties.isReadByPathBufferEnabled();
			
			boolean rpcAcceptRootElementUnqualified = OpenSPCoop2Properties.getInstance().isValidazioneContenutiApplicativiRpcAcceptRootElementUnqualified();
			if(pd!=null && pd.getProprietaList()!=null && !pd.getProprietaList().isEmpty()) {
				boolean defaultRpcAcceptRootElementUnqualified = rpcAcceptRootElementUnqualified;
				rpcAcceptRootElementUnqualified = ValidatoreMessaggiApplicativiRest.readBooleanValueWithDefault(pd.getProprietaList(), CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RPC_ACCEPT_ROOT_ELEMENT_UNQUALIFIED_ENABLED, defaultRpcAcceptRootElementUnqualified);
			}
			
			WSDLValidatorConfig config = new WSDLValidatorConfig();
			config.setRpcAcceptRootElementUnqualified(rpcAcceptRootElementUnqualified);
			
			String azione = OperationFinder.getAzione(registroServiziManager, urlProtocolContext, requestInfo, message, soapStreamReader, soggettoErogatore, idServizio, 
					readFirstHeaderIntegrazione, azioneHeaderIntegrazione, protocolFactory, modalitaIdentificazione, 
					pattern, forceRegistryBased, forcePluginBased, this.logger, false,
					bufferMessageReadOnly, 
					headerIntegrazione!=null ? headerIntegrazione.getIdTransazione() : null,
					config);

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

	protected MTOMProcessorConfig getMTOMProcessorForSender(PortaDelegata pd) throws DriverConfigurazioneException{

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

	protected MTOMProcessorConfig getMTOMProcessorForReceiver(PortaDelegata pd) throws DriverConfigurazioneException{

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

	protected MessageSecurityConfig getMessageSecurityForSender(PortaDelegata pd) throws DriverConfigurazioneException{

		MessageSecurityConfig securityConfig = new MessageSecurityConfig();
		java.util.Map<String,Object> table = new java.util.HashMap<>();
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

	protected MessageSecurityConfig getMessageSecurityForReceiver(PortaDelegata pd) throws DriverConfigurazioneException{

		MessageSecurityConfig securityConfig = new MessageSecurityConfig();
		java.util.Map<String,Object> table = new java.util.HashMap<>();
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

	protected String getLocalForwardNomePortaApplicativa(PortaDelegata pd) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

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
			return getDumpConfigurazionePortaDelegata(connectionPdD);
		}

		if(pd.getDump()!=null) {
			return pd.getDump();
		}
		else {
			//configurazione di default
			return getDumpConfigurazionePortaDelegata(connectionPdD);
		}
	}
	
	protected boolean isTransazioniFileTraceEnabled(Connection connectionPdD,PortaDelegata pd) throws DriverConfigurazioneException{
		
		boolean defaultValue = this.openspcoopProperties.isTransazioniFileTraceEnabled();
		if(pd==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isFileTraceEnabled(pd.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	
	protected boolean isTransazioniFileTraceDumpBinarioHeadersEnabled(Connection connectionPdD,PortaDelegata pd) throws DriverConfigurazioneException{
		
		boolean defaultValue = this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPDEnabled() && this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPDHeadersEnabled();
		if(pd==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isFileTraceDumpBinarioHeadersEnabled(pd.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	protected boolean isTransazioniFileTraceDumpBinarioPayloadEnabled(Connection connectionPdD,PortaDelegata pd) throws DriverConfigurazioneException{
		
		boolean defaultValue = this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPDEnabled() && this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPDPayloadEnabled();
		if(pd==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isFileTraceDumpBinarioPayloadEnabled(pd.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	
	protected boolean isTransazioniFileTraceDumpBinarioConnettoreHeadersEnabled(Connection connectionPdD,PortaDelegata pd) throws DriverConfigurazioneException{
	
		boolean defaultValue = this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPDConnettoreEnabled() && this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPDConnettoreHeadersEnabled();
		if(pd==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isFileTraceDumpBinarioConnettoreHeadersEnabled(pd.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	protected boolean isTransazioniFileTraceDumpBinarioConnettorePayloadEnabled(Connection connectionPdD,PortaDelegata pd) throws DriverConfigurazioneException{
		
		boolean defaultValue = this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPDConnettoreEnabled() && this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPDConnettorePayloadEnabled();
		if(pd==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isFileTraceDumpBinarioConnettorePayloadEnabled(pd.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	
	protected File getFileTraceConfig(Connection connectionPdD,PortaDelegata pd) throws DriverConfigurazioneException{

		try {
			File defaultValue = this.openspcoopProperties.getTransazioniFileTraceConfig();
			if(pd==null){
				//configurazione di default
				return defaultValue;
			}
			return CostantiProprieta.getFileTraceConfig(pd.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	
	protected SoglieDimensioneMessaggi getSoglieLimitedInputStream(Connection connectionPdD,PortaDelegata pd, String azione, String idModulo,
			PdDContext pddContext, RequestInfo requestInfo, 
			IProtocolFactory<?> protocolFactory, Logger log) throws DriverConfigurazioneException{
		
		if(!this.openspcoopProperties.isControlloTrafficoEnabled()){
			return null;
		}
		
		URLProtocolContext urlProtocolContext = null;
		if(requestInfo!=null) {
			urlProtocolContext = requestInfo.getProtocolContext();
		}
		
		TipoPdD tipoPdD = null;
		String nomePorta = null;
		IDSoggetto idDominio = null;
		IDSoggetto soggettoFruitore = null;
		IDServizio idServizio = null;
		IDPortaDelegata idPD = null;
		IDPortaApplicativa idPA = null;
		if(pd!=null) {
			tipoPdD = TipoPdD.DELEGATA;
			nomePorta = pd.getNome();
			idDominio = new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario());
			soggettoFruitore = idDominio;
			try {
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pd.getServizio().getTipo(), pd.getServizio().getNome(), 
						pd.getSoggettoErogatore().getTipo(), pd.getSoggettoErogatore().getNome(),
						pd.getServizio().getVersione());
				idServizio.setAzione(azione);
			}catch(Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
			idPD = new IDPortaDelegata();
			idPD.setNome(nomePorta);
		}
		else {
			idDominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory!=null ? protocolFactory.getProtocol() : null, requestInfo);
		}
		 
		IState state = null;
		IDAccordo idAccordo = null; // viene calcolato all'interno del metodo
		String servizioApplicativoFruitore = null; 
		List<String> serviziApplicativiErogatori = null;
		try {
			DatiTransazione datiTransazione = InterceptorPolicyUtilities.readDatiTransazione(tipoPdD, idModulo, 
					pddContext, urlProtocolContext,
					protocolFactory, state, log,
					idDominio, soggettoFruitore, idServizio, idAccordo,
					idPD, idPA,
					servizioApplicativoFruitore, serviziApplicativiErogatori);
			return DimensioneMessaggiConfigurationUtils.readSoglieDimensioneMessaggi(tipoPdD, nomePorta, datiTransazione, log, urlProtocolContext, requestInfo, pddContext, protocolFactory);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	protected boolean isConnettoriUseTimeoutInputStream(Connection connectionPdD,PortaDelegata pd) throws DriverConfigurazioneException{
		
		boolean defaultValue = this.openspcoopProperties.isConnettoriUseTimeoutInputStream();
		if(pd==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isConnettoriUseTimeoutInputStream(pd.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	protected SogliaReadTimeout getRequestReadTimeout(PortaDelegata pd,
			RequestInfo requestInfo, 
			IProtocolFactory<?> protocolFactory,
			Context context,
			IState state) throws DriverConfigurazioneException{

		int defaultValue = this.openspcoopProperties.getReadConnectionTimeoutRicezioneContenutiApplicativi();
		if(pd==null){
			//configurazione di default
			return ReadTimeoutConfigurationUtils.buildSogliaRequestTimeout(defaultValue, true, protocolFactory);
		}
		try {
			boolean configurazioneGlobale = !CostantiProprieta.existsConnettoriRequestTimeout(pd.getProprietaList());
			int sogliaMs = defaultValue;
			if(!configurazioneGlobale) {
				sogliaMs = CostantiProprieta.getConnettoriRequestTimeout(pd.getProprietaList(), defaultValue);
			}
			return ReadTimeoutConfigurationUtils.buildSogliaRequestTimeout(sogliaMs, configurazioneGlobale, pd, 
					new ReadTimeoutContextParam(requestInfo, protocolFactory, context, state));
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}

	protected Trasformazioni getTrasformazioni(PortaDelegata pd) throws DriverConfigurazioneException{
		if(pd!=null && pd.getTrasformazioni()!=null) {
			return pd.getTrasformazioni();
		}
		return null;
	}
	
	protected List<String> getPreInRequestHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pd!=null && pd.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pd.getConfigurazioneHandler();
			if(confPorta.getRequest()!=null) {
				fillListHandlers(confPorta.getRequest().getPreInList(), list);
			}
		}
		return list;
	}
	protected List<String> getInRequestHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pd!=null && pd.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pd.getConfigurazioneHandler();
			if(confPorta.getRequest()!=null) {
				fillListHandlers(confPorta.getRequest().getInList(), list);
			}
		}
		return list;
	}
	protected List<String> getInRequestProtocolHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pd!=null && pd.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pd.getConfigurazioneHandler();
			if(confPorta.getRequest()!=null) {
				fillListHandlers(confPorta.getRequest().getInProtocolInfoList(), list);
			}
		}
		return list;
	}
	protected List<String> getOutRequestHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pd!=null && pd.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pd.getConfigurazioneHandler();
			if(confPorta.getRequest()!=null) {
				fillListHandlers(confPorta.getRequest().getOutList(), list);
			}
		}
		return list;
	}
	protected List<String> getPostOutRequestHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pd!=null && pd.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pd.getConfigurazioneHandler();
			if(confPorta.getRequest()!=null) {
				fillListHandlers(confPorta.getRequest().getPostOutList(), list);
			}
		}
		return list;
	}
	protected List<String> getPreInResponseHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pd!=null && pd.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pd.getConfigurazioneHandler();
			if(confPorta.getResponse()!=null) {
				fillListHandlers(confPorta.getResponse().getPreInList(), list);
			}
		}
		return list;
	}
	protected List<String> getInResponseHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pd!=null && pd.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pd.getConfigurazioneHandler();
			if(confPorta.getResponse()!=null) {
				fillListHandlers(confPorta.getResponse().getInList(), list);
			}
		}
		return list;
	}
	protected List<String> getOutResponseHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pd!=null && pd.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pd.getConfigurazioneHandler();
			if(confPorta.getResponse()!=null) {
				fillListHandlers(confPorta.getResponse().getOutList(), list);
			}
		}
		return list;
	}
	protected List<String> getPostOutResponseHandlers(PortaDelegata pd) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pd!=null && pd.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pd.getConfigurazioneHandler();
			if(confPorta.getResponse()!=null) {
				fillListHandlers(confPorta.getResponse().getPostOutList(), list);
			}
		}
		return list;
	}

	protected List<Object> getExtendedInfo(PortaDelegata pd)throws DriverConfigurazioneException{

		if(pd == null || pd.sizeExtendedInfoList()<=0)
			return null;

		return pd.getExtendedInfoList();
	}

	protected Template getTemplateTrasformazioneRichiesta(Connection connectionPdD,IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateTrasformazioneRichiesta(connectionPdD, idPD, nomeTrasformazione, richiesta, requestInfo);
	}
	protected Template getTemplateTrasformazioneSoapRichiesta(Connection connectionPdD,IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateTrasformazioneSoapRichiesta(connectionPdD, idPD, nomeTrasformazione, richiesta, requestInfo);
	}
	protected Template getTemplateTrasformazioneRisposta(Connection connectionPdD,IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateTrasformazioneRisposta(connectionPdD, idPD, nomeTrasformazione, risposta, requestInfo);
	}
	protected Template getTemplateTrasformazioneSoapRisposta(Connection connectionPdD,IDPortaDelegata idPD, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateTrasformazioneSoapRisposta(connectionPdD, idPD, nomeTrasformazione, risposta, requestInfo);
	}
	
	public Template getTemplateCorrelazioneApplicativaRichiesta(Connection connectionPdD,IDPortaDelegata idPD, String nomeRegola, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateCorrelazioneApplicativaRichiesta(connectionPdD, idPD, nomeRegola, template, requestInfo);
	}
	public Template getTemplateCorrelazioneApplicativaRisposta(Connection connectionPdD,IDPortaDelegata idPD, String nomeRegola, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateCorrelazioneApplicativaRisposta(connectionPdD, idPD, nomeRegola, template, requestInfo);
	}
	
	protected Template getTemplateIntegrazione(Connection connectionPdD,IDPortaDelegata idPD, File file, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateIntegrazione(connectionPdD, idPD, file, requestInfo);
	}











	/* ********  PORTE APPLICATIVE  (Interfaccia) ******** */

	public IDPortaApplicativa getIDPortaApplicativa(Connection connectionPdD,String nome) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getIDPortaApplicativa(connectionPdD, nome);
	}

	protected Map<IDSoggetto,PortaApplicativa> getPorteApplicativeSoggettiVirtuali(Connection connectionPdD,IDServizio idServizio)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPorteApplicativeSoggettiVirtuali(connectionPdD,idServizio,null,false);
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
				paConSoggetti = this.configurazionePdD.getPorteApplicativeSoggettiVirtuali(connectionPdD,richiestaApplicativa.getIDServizio(),richiestaApplicativa.getFiltroProprietaPorteApplicative(),true);
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

	protected PortaApplicativa getPortaApplicativaSafeMethod(Connection connectionPdD,IDPortaApplicativa idPA)throws DriverConfigurazioneException{
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
	
	protected String updateStatoConnettoreMultiplo(Connection connectionPdD,IDPortaApplicativa idPA, String nomeConnettore, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.updateStatoConnettoreMultiplo(connectionPdD, idPA, nomeConnettore, stato);
	}
	protected String updateStatoConnettoreMultiplo(Connection connectionPdD,IDPortaApplicativa idPA, String nomeConnettore, String user, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.updateStatoConnettoreMultiplo(connectionPdD, idPA, nomeConnettore, user, stato);
	}
	
	protected String updateSchedulingConnettoreMultiplo(Connection connectionPdD,IDPortaApplicativa idPA, String nomeConnettore, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.updateSchedulingConnettoreMultiplo(connectionPdD, idPA, nomeConnettore, stato);
	}
	protected String updateSchedulingConnettoreMultiplo(Connection connectionPdD,IDPortaApplicativa idPA, String nomeConnettore, String user, StatoFunzionalita stato) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.updateSchedulingConnettoreMultiplo(connectionPdD, idPA, nomeConnettore, user, stato);
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

	protected String getAzione(RegistroServiziManager registroServiziManager,PortaApplicativa pa,URLProtocolContext urlProtocolContext, RequestInfo requestInfo,
			OpenSPCoop2Message message, OpenSPCoop2MessageSoapStreamReader soapStreamReader, HeaderIntegrazione headerIntegrazione, boolean readFirstHeaderIntegrazione,
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

			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			
			boolean bufferMessageReadOnly =  op2Properties.isReadByPathBufferEnabled();
			
			boolean rpcAcceptRootElementUnqualified = OpenSPCoop2Properties.getInstance().isValidazioneContenutiApplicativiRpcAcceptRootElementUnqualified();
			if(pa!=null && pa.getProprietaList()!=null && !pa.getProprietaList().isEmpty()) {
				boolean defaultRpcAcceptRootElementUnqualified = rpcAcceptRootElementUnqualified;
				rpcAcceptRootElementUnqualified = ValidatoreMessaggiApplicativiRest.readBooleanValueWithDefault(pa.getProprietaList(), CostantiProprieta.VALIDAZIONE_CONTENUTI_PROPERTY_NAME_RPC_ACCEPT_ROOT_ELEMENT_UNQUALIFIED_ENABLED, defaultRpcAcceptRootElementUnqualified);
			}
			
			WSDLValidatorConfig config = new WSDLValidatorConfig();
			config.setRpcAcceptRootElementUnqualified(rpcAcceptRootElementUnqualified);
			
			String azione = OperationFinder.getAzione(registroServiziManager, urlProtocolContext, requestInfo, message, soapStreamReader, soggettoErogatore, idServizio, 
					readFirstHeaderIntegrazione, azioneHeaderIntegrazione, protocolFactory, modalitaIdentificazione, 
					pattern, forceRegistryBased, forcePluginBased, this.logger, true,
					bufferMessageReadOnly, 
					headerIntegrazione!=null ? headerIntegrazione.getIdTransazione() : null,
					config);

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

	protected SoggettoVirtuale getServiziApplicativiSoggettiVirtuali(Connection connectionPdD,RichiestaApplicativa richiestaApplicativa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Map<IDSoggetto,PortaApplicativa> paConSoggetti = this.configurazionePdD.getPorteApplicativeSoggettiVirtuali(connectionPdD,richiestaApplicativa.getIDServizio()
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

	protected MTOMProcessorConfig getMTOMProcessorForSender(PortaApplicativa pa)throws DriverConfigurazioneException{

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

	protected MTOMProcessorConfig getMTOMProcessorForReceiver(PortaApplicativa pa)throws DriverConfigurazioneException{

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

	protected MessageSecurityConfig getMessageSecurityForSender(PortaApplicativa pa)throws DriverConfigurazioneException{

		MessageSecurityConfig securityConfig = new MessageSecurityConfig();
		java.util.Map<String,Object> table = new java.util.HashMap<>();
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

	protected MessageSecurityConfig getMessageSecurityForReceiver(PortaApplicativa pa)throws DriverConfigurazioneException{

		MessageSecurityConfig securityConfig = new MessageSecurityConfig();
		java.util.Map<String,Object> table = new java.util.HashMap<>();
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

	protected boolean autorizzazioneTrasportoRoles(RegistroServiziManager registroServiziManager, 
			PortaApplicativa pa, org.openspcoop2.core.registry.Soggetto soggetto, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext, RequestInfo requestInfo,
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

		return _autorizzazioneRoles(registroServiziManager,
				soggetto, sa, 
				infoConnettoreIngresso, true,
				pddContext, requestInfo,
				checkRuoloRegistro, checkRuoloEsterno,
				details,
				pa.getRuoli().getMatch(), pa.getRuoli(),
				false);
	}
	
	protected boolean autorizzazioneTokenRoles(RegistroServiziManager registroServiziManager, 
			PortaApplicativa pa, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext,  RequestInfo requestInfo,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if( (pa == null) || (pa.getAutorizzazioneToken()==null) || 
				pa.getAutorizzazioneToken().getRuoli()==null || 
				pa.getAutorizzazioneToken().getRuoli().sizeRuoloList()<=0 ){
			throw new DriverConfigurazioneNotFound("Non sono stati definiti i ruoli necessari a fruire della porta applicativa");
		}

		if(checkRuoloRegistro && !checkRuoloEsterno){
			if(sa==null){
				throw new DriverConfigurazioneException("Identità soggetto e/o applicativo non disponibile; tale informazione è richiesta dall'autorizzazione");
			}
		}

		return _autorizzazioneRoles(registroServiziManager,
				null, sa, 
				infoConnettoreIngresso, true,
				pddContext, requestInfo,
				checkRuoloRegistro, checkRuoloEsterno,
				details,
				pa.getAutorizzazioneToken().getRuoli().getMatch(), pa.getAutorizzazioneToken().getRuoli(),
				false);
	}
	
	public static boolean _autorizzazioneRoles(RegistroServiziManager registroServiziManager,
			org.openspcoop2.core.registry.Soggetto soggetto, ServizioApplicativo sa, 
			InfoConnettoreIngresso infoConnettoreIngresso, boolean readInfoConnettoreIngresso,
			Context pddContext, RequestInfo requestInfo,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details,
			RuoloTipoMatch ruoloMatch, AutorizzazioneRuoli autorizzazioneRuoli,
			boolean modiCheck) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

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

			if(readInfoConnettoreIngresso) {
				if(infoConnettoreIngresso==null ||
						infoConnettoreIngresso.getUrlProtocolContext()==null ||
						infoConnettoreIngresso.getUrlProtocolContext().getHttpServletRequest()==null){
					if(tokenRoles==null) {
						throw new DriverConfigurazioneException("HttpServletRequest non disponibile; risorsa richiesta dall'autorizzazione");
					}
				}
				if(infoConnettoreIngresso!=null && infoConnettoreIngresso.getUrlProtocolContext()!=null) {
					httpServletRequest = infoConnettoreIngresso.getUrlProtocolContext().getHttpServletRequest();
				}
			}
		}

		if(ruoloMatch==null){
			ruoloMatch = RuoloTipoMatch.ALL;
		}

		String searchIn = "";
		if(checkRuoloRegistro) {
			if(sa!=null) {
				try {
					if(modiCheck) {
						searchIn = " nell'applicativo";
					}
					else {
						searchIn = " in application '"+sa.getNome()+NamingUtils.LABEL_DOMINIO+NamingUtils.getLabelSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()))+"'";
					}
				}catch(Exception t) {}
			}
		}
		if(checkRuoloEsterno) {
			if(checkRuoloRegistro && sa!=null) {
				if(modiCheck) {
					searchIn = searchIn + " e";
				}
				else {
					searchIn = searchIn + " and";
				}
			}
			if(modiCheck) {
				searchIn = searchIn + " nel contesto della richiesta";
			}
			else {
				searchIn = searchIn + " in request context";
			}
		}
		
		for(int j=0; j<autorizzazioneRuoli.sizeRuoloList(); j++){
			Ruolo ruolo = autorizzazioneRuoli.getRuolo(j);

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
					org.openspcoop2.core.registry.Ruolo ruoloRegistro = registroServiziManager.getRuolo(ruolo.getNome(), null, requestInfo);
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
						if(modiCheck) {
							details.append("Ruolo '"+ruolo.getNome()+"' non trovato").append(searchIn);
						}
						else {
							details.append("Role '"+ruolo.getNome()+"' not found").append(searchIn);
						}
					}
					return false; // deve possedere tutti i ruoli
				}
			}
		}

		if(RuoloTipoMatch.ANY.equals(ruoloMatch)){
			if(details!=null) {
				if(modiCheck) {
					details.append("Ruoli non trovati").append(searchIn);
				}
				else {
					details.append("Roles not found").append(searchIn);
				}
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
			return getDumpConfigurazionePortaApplicativa(connectionPdD);
		}

		if(pa.getDump()!=null) {
			return pa.getDump();
		}
		else {
			//configurazione di default
			return getDumpConfigurazionePortaApplicativa(connectionPdD);
		}
	}

	protected boolean isTransazioniFileTraceEnabled(Connection connectionPdD,PortaApplicativa pa) throws DriverConfigurazioneException{
		
		boolean defaultValue = this.openspcoopProperties.isTransazioniFileTraceEnabled();
		if(pa==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isFileTraceEnabled(pa.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	
    protected boolean isTransazioniFileTraceDumpBinarioHeadersEnabled(Connection connectionPdD,PortaApplicativa pa) throws DriverConfigurazioneException{
		
		boolean defaultValue = this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPAEnabled() && this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPAHeadersEnabled();
		if(pa==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isFileTraceDumpBinarioHeadersEnabled(pa.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	protected boolean isTransazioniFileTraceDumpBinarioPayloadEnabled(Connection connectionPdD,PortaApplicativa pa) throws DriverConfigurazioneException{
		
		boolean defaultValue = this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPAEnabled() && this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPAPayloadEnabled();
		if(pa==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isFileTraceDumpBinarioPayloadEnabled(pa.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}

	protected boolean isTransazioniFileTraceDumpBinarioConnettoreHeadersEnabled(Connection connectionPdD,PortaApplicativa pa) throws DriverConfigurazioneException{
	
		boolean defaultValue = this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPAConnettoreEnabled() && this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPAConnettoreHeadersEnabled();
		if(pa==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isFileTraceDumpBinarioConnettoreHeadersEnabled(pa.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	protected boolean isTransazioniFileTraceDumpBinarioConnettorePayloadEnabled(Connection connectionPdD,PortaApplicativa pa) throws DriverConfigurazioneException{
		
		boolean defaultValue = this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPAConnettoreEnabled() && this.openspcoopProperties.isTransazioniFileTraceDumpBinarioPAConnettorePayloadEnabled();
		if(pa==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isFileTraceDumpBinarioConnettorePayloadEnabled(pa.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	
	protected File getFileTraceConfig(Connection connectionPdD,PortaApplicativa pa) throws DriverConfigurazioneException{

		try {
			File defaultValue = this.openspcoopProperties.getTransazioniFileTraceConfig();
			if(pa==null){
				//configurazione di default
				return defaultValue;
			}
			return CostantiProprieta.getFileTraceConfig(pa.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	
	protected SoglieDimensioneMessaggi getSoglieLimitedInputStream(Connection connectionPdD,PortaApplicativa pa, String azione, String idModulo,
			PdDContext pddContext, RequestInfo requestInfo,
			IProtocolFactory<?> protocolFactory, Logger log) throws DriverConfigurazioneException{
		
		if(!this.openspcoopProperties.isControlloTrafficoEnabled()){
			return null;
		}
		
		URLProtocolContext urlProtocolContext = null;
		if(requestInfo!=null) {
			urlProtocolContext = requestInfo.getProtocolContext();
		}
		
		TipoPdD tipoPdD = null;
		String nomePorta = null;
		IDSoggetto idDominio = null;
		IDSoggetto soggettoFruitore = null;
		IDServizio idServizio = null;
		IDPortaDelegata idPD = null;
		IDPortaApplicativa idPA = null;
		if(pa!=null) {
			tipoPdD = TipoPdD.APPLICATIVA;
			nomePorta = pa.getNome();
			idDominio = new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario());
			try {
				idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(pa.getServizio().getTipo(), pa.getServizio().getNome(), 
						pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario(), 
						pa.getServizio().getVersione());
				idServizio.setAzione(azione);
			}catch(Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
			idPA = new IDPortaApplicativa();
			idPA.setNome(nomePorta);
		}
		else {
			idDominio = OpenSPCoop2Properties.getInstance().getIdentitaPortaDefault(protocolFactory!=null ? protocolFactory.getProtocol() : null, requestInfo);
		}
		 
		IState state = null;
		IDAccordo idAccordo = null; // viene calcolato all'interno del metodo
		String servizioApplicativoFruitore = null; 
		List<String> serviziApplicativiErogatori = null;
		try {
			DatiTransazione datiTransazione = InterceptorPolicyUtilities.readDatiTransazione(tipoPdD, idModulo, 
					pddContext, urlProtocolContext,
					protocolFactory, state, log,
					idDominio, soggettoFruitore, idServizio, idAccordo,
					idPD, idPA,
					servizioApplicativoFruitore, serviziApplicativiErogatori);
			return DimensioneMessaggiConfigurationUtils.readSoglieDimensioneMessaggi(tipoPdD, nomePorta, datiTransazione, log, urlProtocolContext, requestInfo, pddContext, protocolFactory);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	protected boolean isConnettoriUseTimeoutInputStream(Connection connectionPdD,PortaApplicativa pa) throws DriverConfigurazioneException{
		
		boolean defaultValue = this.openspcoopProperties.isConnettoriUseTimeoutInputStream();
		if(pa==null){
			//configurazione di default
			return defaultValue;
		}
		try {
			return CostantiProprieta.isConnettoriUseTimeoutInputStream(pa.getProprietaList(), defaultValue);
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	protected SogliaReadTimeout getRequestReadTimeout(PortaApplicativa pa,
			RequestInfo requestInfo, 
			IProtocolFactory<?> protocolFactory,
			Context context,
			IState state) throws DriverConfigurazioneException{

		int defaultValue = this.openspcoopProperties.getReadConnectionTimeoutRicezioneBuste();
		if(pa==null){
			//configurazione di default
			return ReadTimeoutConfigurationUtils.buildSogliaRequestTimeout(defaultValue, false, protocolFactory);
		}
		try {
			boolean configurazioneGlobale = !CostantiProprieta.existsConnettoriRequestTimeout(pa.getProprietaList());
			int sogliaMs = defaultValue;
			if(!configurazioneGlobale) {
				sogliaMs = CostantiProprieta.getConnettoriRequestTimeout(pa.getProprietaList(), defaultValue);
			}
			return ReadTimeoutConfigurationUtils.buildSogliaRequestTimeout(sogliaMs, configurazioneGlobale, pa, 
					new ReadTimeoutContextParam(requestInfo, protocolFactory, context, state));
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	
	
	protected Trasformazioni getTrasformazioni(PortaApplicativa pa) throws DriverConfigurazioneException{
		if(pa!=null && pa.getTrasformazioni()!=null) {
			return pa.getTrasformazioni();
		}
		return null;
	}
	
	protected List<String> getPreInRequestHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pa!=null && pa.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pa.getConfigurazioneHandler();
			if(confPorta.getRequest()!=null) {
				fillListHandlers(confPorta.getRequest().getPreInList(), list);
			}
		}
		return list;
	}
	protected List<String> getInRequestHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pa!=null && pa.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pa.getConfigurazioneHandler();
			if(confPorta.getRequest()!=null) {
				fillListHandlers(confPorta.getRequest().getInList(), list);
			}
		}
		return list;
	}
	protected List<String> getInRequestProtocolHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pa!=null && pa.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pa.getConfigurazioneHandler();
			if(confPorta.getRequest()!=null) {
				fillListHandlers(confPorta.getRequest().getInProtocolInfoList(), list);
			}
		}
		return list;
	}
	protected List<String> getOutRequestHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pa!=null && pa.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pa.getConfigurazioneHandler();
			if(confPorta.getRequest()!=null) {
				fillListHandlers(confPorta.getRequest().getOutList(), list);
			}
		}
		return list;
	}
	protected List<String> getPostOutRequestHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pa!=null && pa.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pa.getConfigurazioneHandler();
			if(confPorta.getRequest()!=null) {
				fillListHandlers(confPorta.getRequest().getPostOutList(), list);
			}
		}
		return list;
	}
	protected List<String> getPreInResponseHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pa!=null && pa.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pa.getConfigurazioneHandler();
			if(confPorta.getResponse()!=null) {
				fillListHandlers(confPorta.getResponse().getPreInList(), list);
			}
		}
		return list;
	}
	protected List<String> getInResponseHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pa!=null && pa.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pa.getConfigurazioneHandler();
			if(confPorta.getResponse()!=null) {
				fillListHandlers(confPorta.getResponse().getInList(), list);
			}
		}
		return list;
	}
	protected List<String> getOutResponseHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pa!=null && pa.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pa.getConfigurazioneHandler();
			if(confPorta.getResponse()!=null) {
				fillListHandlers(confPorta.getResponse().getOutList(), list);
			}
		}
		return list;
	}
	protected List<String> getPostOutResponseHandlers(PortaApplicativa pa) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		List<String> list = new ArrayList<>();
		if(pa!=null && pa.getConfigurazioneHandler()!=null) {
			ConfigurazionePortaHandler confPorta = pa.getConfigurazioneHandler();
			if(confPorta.getResponse()!=null) {
				fillListHandlers(confPorta.getResponse().getPostOutList(), list);
			}
		}
		return list;
	}
	
	protected List<Object> getExtendedInfo(PortaApplicativa pa)throws DriverConfigurazioneException{

		if(pa == null || pa.sizeExtendedInfoList()<=0)
			return null;

		return pa.getExtendedInfoList();
	}

	protected Template getTemplateTrasformazioneRichiesta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateTrasformazioneRichiesta(connectionPdD, idPA, nomeTrasformazione, richiesta, requestInfo);
	}
	protected Template getTemplateTrasformazioneSoapRichiesta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRichiesta richiesta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateTrasformazioneSoapRichiesta(connectionPdD, idPA, nomeTrasformazione, richiesta, requestInfo);
	}
	protected Template getTemplateTrasformazioneRisposta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateTrasformazioneRisposta(connectionPdD, idPA, nomeTrasformazione, risposta, requestInfo);
	}
	protected Template getTemplateTrasformazioneSoapRisposta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeTrasformazione, TrasformazioneRegolaRisposta risposta, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateTrasformazioneSoapRisposta(connectionPdD, idPA, nomeTrasformazione, risposta, requestInfo);
	}
	
	protected Template getTemplateConnettoreMultiploSticky(Connection connectionPdD,IDPortaApplicativa idPA, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateConnettoreMultiploSticky(connectionPdD, idPA, template, requestInfo);
	}
	protected Template getTemplateConnettoreMultiploCondizionale(Connection connectionPdD,IDPortaApplicativa idPA, String nomeRegola, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateConnettoreMultiploCondizionale(connectionPdD, idPA, nomeRegola, template, requestInfo);
	}
	
	public Template getTemplateCorrelazioneApplicativaRichiesta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeRegola, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateCorrelazioneApplicativaRichiesta(connectionPdD, idPA, nomeRegola, template, requestInfo);
	}
	public Template getTemplateCorrelazioneApplicativaRisposta(Connection connectionPdD,IDPortaApplicativa idPA, String nomeRegola, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateCorrelazioneApplicativaRisposta(connectionPdD, idPA, nomeRegola, template, requestInfo);
	}
	
	protected Template getTemplateIntegrazione(Connection connectionPdD,IDPortaApplicativa idPA, File file, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateIntegrazione(connectionPdD, idPA, file, requestInfo);
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
		return getIdServizioApplicativoByCredenzialiBasic(connectionPdD, aUser, aPassword, config, null);
	}
	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiBasic(Connection connectionPdD,String aUser,String aPassword, CryptConfig config,
    		List<String> tipiSoggetto) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			if(tipiSoggetto!=null) {
				servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiBasic(connectionPdD, aUser, aPassword, config, tipiSoggetto);
			}
			else {
				servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiBasic(connectionPdD, aUser, aPassword, config);
			}
		}catch(DriverConfigurazioneNotFound e){
			/**this.logDebug("autenticazioneHTTP (not found): "+e.getMessage());*/
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
		return getIdServizioApplicativoByCredenzialiApiKey(connectionPdD, aUser, aPassword, appId, config, null);
	}
	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiApiKey(Connection connectionPdD,String aUser,String aPassword, boolean appId, CryptConfig config,
    		List<String> tipiSoggetto) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			if(tipiSoggetto!=null) {
				servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiApiKey(connectionPdD, aUser, aPassword, appId, config, tipiSoggetto);
			}
			else {
				servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiApiKey(connectionPdD, aUser, aPassword, appId, config);
			}
		}catch(DriverConfigurazioneNotFound e){
			/**this.logDebug("autenticazioneHTTP (not found): "+e.getMessage());*/
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
		return getIdServizioApplicativoByCredenzialiSsl(connectionPdD, aSubject, aIssuer, 
				null);
	}
	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiSsl(Connection connectionPdD,String aSubject, String aIssuer,
    		List<String> tipiSoggetto) throws DriverConfigurazioneException{
		return getIdServizioApplicativoByCredenzialiSsl(connectionPdD, aSubject, aIssuer, 
				tipiSoggetto,
				false, false, false);
	}
	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiSsl(Connection connectionPdD,String aSubject, String aIssuer,
    		List<String> tipiSoggetto, 
			boolean includiApplicativiNonModI, boolean includiApplicativiModIEsterni, boolean includiApplicativiModIInterni) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			if(tipiSoggetto!=null) {
				if(includiApplicativiNonModI || includiApplicativiModIEsterni || includiApplicativiModIInterni) {
					servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiSsl(connectionPdD, aSubject, aIssuer, tipiSoggetto, 
							includiApplicativiNonModI, includiApplicativiModIEsterni, includiApplicativiModIInterni);
				}
				else {
					servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiSsl(connectionPdD, aSubject, aIssuer, tipiSoggetto);
				}
			}
			else {
				servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiSsl(connectionPdD, aSubject, aIssuer);
			}
		}catch(DriverConfigurazioneNotFound e){
			/**this.logDebug("autenticazioneHTTP (not found): "+e.getMessage());*/
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
		return getIdServizioApplicativoByCredenzialiSsl(connectionPdD, certificate, strictVerifier, 
				null);
	}
	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiSsl(Connection connectionPdD,CertificateInfo certificate, boolean strictVerifier,
    		List<String> tipiSoggetto) throws DriverConfigurazioneException{
		return getIdServizioApplicativoByCredenzialiSsl(connectionPdD, certificate, strictVerifier, 
				tipiSoggetto,
				false, false, false);
	}
	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiSsl(Connection connectionPdD,CertificateInfo certificate, boolean strictVerifier,
    		List<String> tipiSoggetto, 
			boolean includiApplicativiNonModI, boolean includiApplicativiModIEsterni, boolean includiApplicativiModIInterni) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			if(tipiSoggetto!=null) {
				if(includiApplicativiNonModI || includiApplicativiModIEsterni || includiApplicativiModIInterni) {
					servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiSsl(connectionPdD, certificate, strictVerifier, tipiSoggetto, 
							includiApplicativiNonModI, includiApplicativiModIEsterni, includiApplicativiModIInterni);
				}
				else {
					servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiSsl(connectionPdD, certificate, strictVerifier, tipiSoggetto);
				}
			}
			else {
				servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiSsl(connectionPdD, certificate, strictVerifier);
			}
		}catch(DriverConfigurazioneNotFound e){
			/**this.logDebug("autenticazioneHTTP (not found): "+e.getMessage());*/
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
		return getIdServizioApplicativoByCredenzialiPrincipal(connectionPdD, principal, null);
	}
	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiPrincipal(Connection connectionPdD,String principal,
    		List<String> tipiSoggetto) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			if(tipiSoggetto!=null) {
				servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiPrincipal(connectionPdD, principal, tipiSoggetto);
			}
			else {
				servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiPrincipal(connectionPdD, principal);
			}
		}catch(DriverConfigurazioneNotFound e){
			/**this.logDebug("autenticazioneHTTP (not found): "+e.getMessage());*/
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
	
	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiToken(Connection connectionPdD,String tokenPolicy, String tokenClientId) throws DriverConfigurazioneException{
		return getIdServizioApplicativoByCredenzialiToken(connectionPdD, tokenPolicy, tokenClientId, null);
	}
	protected IDServizioApplicativo getIdServizioApplicativoByCredenzialiToken(Connection connectionPdD,String tokenPolicy, String tokenClientId,
    		List<String> tipiSoggetto) throws DriverConfigurazioneException{
		ServizioApplicativo servizioApplicativo = null;
		try{
			if(tipiSoggetto!=null) {
				servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiToken(connectionPdD, tokenPolicy, tokenClientId, tipiSoggetto);
			}
			else {
				servizioApplicativo = this.configurazionePdD.getServizioApplicativoByCredenzialiToken(connectionPdD, tokenPolicy, tokenClientId);
			}
		}catch(DriverConfigurazioneNotFound e){
			/**this.logDebug("autenticazioneHTTP (not found): "+e.getMessage());*/
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

	protected boolean autorizzazioneTrasportoRoles(RegistroServiziManager registroServiziManager, 
			PortaDelegata pd, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext, RequestInfo requestInfo,
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
		
		return _autorizzazioneRoles(registroServiziManager,
				null, sa, 
				infoConnettoreIngresso, true,
				pddContext, requestInfo,
				checkRuoloRegistro, checkRuoloEsterno,
				details,
				pd.getRuoli().getMatch(), pd.getRuoli(),
				false);

	}
	
	protected boolean autorizzazioneTokenRoles(RegistroServiziManager registroServiziManager, 
			PortaDelegata pd, ServizioApplicativo sa, InfoConnettoreIngresso infoConnettoreIngresso,
			PdDContext pddContext, RequestInfo requestInfo,
			boolean checkRuoloRegistro, boolean checkRuoloEsterno,
			StringBuilder details) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{ 

		if( (pd == null) || pd.getAutorizzazioneToken()==null || 
				pd.getAutorizzazioneToken().getRuoli()==null || 
				pd.getAutorizzazioneToken().getRuoli().sizeRuoloList()<=0 ){
			throw new DriverConfigurazioneNotFound("Non sono stati definiti i ruoli necessari a fruire della porta delegata");
		}

		if(checkRuoloRegistro && !checkRuoloEsterno){
			if(sa==null){
				throw new DriverConfigurazioneException("Identità servizio applicativo non disponibile; tale informazione è richiesta dall'autorizzazione");
			}
		}
		
		return _autorizzazioneRoles(registroServiziManager,
				null, sa, 
				infoConnettoreIngresso, true,
				pddContext, requestInfo,
				checkRuoloRegistro, checkRuoloEsterno,
				details,
				pd.getAutorizzazioneToken().getRuoli().getMatch(), pd.getAutorizzazioneToken().getRuoli(),
				false);

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
			List<String> l = new ArrayList<>();
			for (IDServizioApplicativo idSA : list) {
				if(l.contains(idSA.getNome())==false) {
					l.add(idSA.getNome());
				}
			}
			return l;
		}catch(DriverConfigurazioneNotFound notFound) {
			return new ArrayList<>();
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
	
	public Map<String, String> getProprietaConfigurazione(ServizioApplicativo sa) throws DriverConfigurazioneException {
		if (sa == null) {
			throw new DriverConfigurazioneException("ServizioApplicativo non fornito");
		} else if (sa.sizeProprietaList() <= 0) {
			return null;
		} else {
			Map<String, String> properties = new HashMap<>();

			for(int i = 0; i < sa.sizeProprietaList(); ++i) {
				Proprieta p = sa.getProprieta(i);
				properties.put(p.getNome(), p.getValore());
			}

			return properties;
		}
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

	protected ConnettoreMsg getInvocazioneServizio(Connection connectionPdD,ServizioApplicativo sa,RichiestaApplicativa richiestaApplicativa, RequestInfo requestInfo)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Servizio applicativo
		if(sa.getInvocazioneServizio()==null)
			throw new DriverConfigurazioneNotFound("Servizio applicativo ["+richiestaApplicativa.getServizioApplicativo()+"] del soggetto["+richiestaApplicativa.getIDServizio().getSoggettoErogatore()+"] non possieder l'elemento invocazione servizio");
		InvocazioneServizio serv = sa.getInvocazioneServizio();


		// Soggetto Erogatore
		IDSoggetto aSoggetto = richiestaApplicativa.getIDServizio().getSoggettoErogatore();
		Soggetto soggetto = null;
		if( requestInfo!=null && requestInfo.getRequestConfig()!=null && aSoggetto!=null) {
			if(requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null &&
					aSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					aSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				soggetto = requestInfo.getRequestConfig().getSoggettoErogatoreConfig();
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null &&
					aSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					aSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				soggetto = requestInfo.getRequestConfig().getSoggettoFruitoreConfig();
			}
		}
		if(soggetto==null) {
			if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
				if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig()!=null && aSoggetto!=null && 
						aSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getTipo()) && 
						aSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getNome())) {
					soggetto = requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig();
				}
			}
		}
		if(soggetto==null) {
			if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
				if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig()!=null && aSoggetto!=null && 
						aSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getTipo()) && 
						aSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getNome())) {
					soggetto = requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig();
				}
			}
		}
		if(soggetto==null) {
			soggetto = this.configurazionePdD.getSoggetto(connectionPdD,aSoggetto);
		}
		if(soggetto==null)
			throw new DriverConfigurazioneNotFound("[getInvocazioneServizio] Soggetto erogatore non trovato");


		// Porta Applicativa
		PortaApplicativa pa = null;
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && richiestaApplicativa.getIdPortaApplicativa()!=null && richiestaApplicativa.getIdPortaApplicativa().getNome()!=null) {
			if( requestInfo.getRequestConfig().getPortaApplicativa()!=null && richiestaApplicativa.getIdPortaApplicativa().getNome().equals(requestInfo.getRequestConfig().getPortaApplicativa().getNome())) {
				pa = requestInfo.getRequestConfig().getPortaApplicativa();
			}
			else if( requestInfo.getRequestConfig().getPortaApplicativaDefault()!=null && richiestaApplicativa.getIdPortaApplicativa().getNome().equals(requestInfo.getRequestConfig().getPortaApplicativaDefault().getNome())) {
				pa = requestInfo.getRequestConfig().getPortaApplicativaDefault();
			}
		}
		if(pa==null) {
			pa = this.configurazionePdD.getPortaApplicativa(connectionPdD,richiestaApplicativa.getIdPortaApplicativa());
		}


		// CONNETTORE
		Connettore connettore = serv.getConnettore();
		java.util.Map<String,String> properties = null;
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
			setPAUrlPrefixRewriter(connectionPdD,connettore, aSoggetto, requestInfo);

			// Properties connettore
			properties = new java.util.HashMap<>();
			for(int i=0;i<connettore.sizePropertyList();i++){
				properties.put(connettore.getProperty(i).getNome(),connettore.getProperty(i).getValore());
			}
		}


		// PROTOCOL-PROPERTIES
		Map<String, List<String>> protocol_properties = new HashMap<>();
		for(int i=0;i<pa.sizeProprietaList();i++){
			TransportUtils.put(protocol_properties, pa.getProprieta(i).getNome(),pa.getProprieta(i).getValore(), false);
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

	protected GestioneErrore getGestioneErroreConnettoreInvocazioneServizio(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding, Connection connectionPdD,ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

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

	protected ConnettoreMsg getConsegnaRispostaAsincrona(Connection connectionPdD,ServizioApplicativo sa,RichiestaDelegata idPD, RequestInfo requestInfo)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		// Servizio applicativo
		if(sa.getRispostaAsincrona()==null)
			throw new DriverConfigurazioneNotFound("Servizio applicativo ["+idPD.getServizioApplicativo()+"] del soggetto["+idPD.getIdSoggettoFruitore()+"] non possiede una risposta Asincrona");
		RispostaAsincrona serv = sa.getRispostaAsincrona();


		// Soggetto che possiede il connettore
		IDSoggetto aSoggetto = idPD.getIdSoggettoFruitore();
		Soggetto soggetto = null;
		if( requestInfo!=null && requestInfo.getRequestConfig()!=null && aSoggetto!=null) {
			if(requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null &&
					aSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					aSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				soggetto = requestInfo.getRequestConfig().getSoggettoErogatoreConfig();
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null &&
					aSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					aSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				soggetto = requestInfo.getRequestConfig().getSoggettoFruitoreConfig();
			}
		}
		if(soggetto==null) {
			if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
				if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig()!=null && aSoggetto!=null && 
						aSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getTipo()) && 
						aSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getNome())) {
					soggetto = requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig();
				}
			}
		}
		if(soggetto==null) {
			if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
				if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig()!=null && aSoggetto!=null && 
						aSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getTipo()) && 
						aSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getNome())) {
					soggetto = requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig();
				}
			}
		}
		if(soggetto==null) {
			soggetto = this.configurazionePdD.getSoggetto(connectionPdD,aSoggetto);
		}
		if(soggetto==null)
			throw new DriverConfigurazioneNotFound("[getConsegnaRispostaAsincrona] Soggetto non trovato");


		// CONNETTORE
		Connettore connettore = serv.getConnettore();
		java.util.Map<String,String> properties = null;
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
			setPAUrlPrefixRewriter(connectionPdD,connettore, aSoggetto, requestInfo);

			// set properties
			properties = new java.util.HashMap<>();
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

	protected ConnettoreMsg getConsegnaRispostaAsincrona(Connection connectionPdD,ServizioApplicativo sa,RichiestaApplicativa richiestaApplicativa, RequestInfo requestInfo)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		//		 Servizio applicativo
		if(sa.getRispostaAsincrona()==null)
			throw new DriverConfigurazioneNotFound("Servizio applicativo ["+richiestaApplicativa.getServizioApplicativo()+"] del soggetto["+richiestaApplicativa.getSoggettoFruitore()+"] non possiede l'elemento invocazione servizio");
		RispostaAsincrona serv = sa.getRispostaAsincrona();


		// Soggetto che contiene il connettore
		IDSoggetto aSoggetto = richiestaApplicativa.getIDServizio().getSoggettoErogatore();
		Soggetto soggetto = null;
		if( requestInfo!=null && requestInfo.getRequestConfig()!=null && aSoggetto!=null) {
			if(requestInfo.getRequestConfig().getSoggettoErogatoreConfig()!=null &&
					aSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getTipo()) &&
					aSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoErogatoreConfig().getNome())	) {
				soggetto = requestInfo.getRequestConfig().getSoggettoErogatoreConfig();
			}
			else if( requestInfo.getRequestConfig().getSoggettoFruitoreConfig()!=null &&
					aSoggetto.getTipo().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getTipo()) &&
					aSoggetto.getNome().equals(requestInfo.getRequestConfig().getSoggettoFruitoreConfig().getNome())	) {
				soggetto = requestInfo.getRequestConfig().getSoggettoFruitoreConfig();
			}
		}
		if(soggetto==null) {
			if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo()!=null) {
				if(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig()!=null && aSoggetto!=null && 
						aSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getTipo()) && 
						aSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig().getNome())) {
					soggetto = requestInfo.getRequestThreadContext().getRequestFruitoreTrasportoInfo().getSoggettoFruitoreConfig();
				}
			}
		}
		if(soggetto==null) {
			if(requestInfo!=null && requestInfo.getRequestThreadContext()!=null && requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo()!=null) {
				if(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig()!=null && aSoggetto!=null && 
						aSoggetto.getTipo().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getTipo()) && 
						aSoggetto.getNome().equals(requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig().getNome())) {
					soggetto = requestInfo.getRequestThreadContext().getRequestFruitoreTokenInfo().getSoggettoFruitoreConfig();
				}
			}
		}
		if(soggetto==null) {
			soggetto = this.configurazionePdD.getSoggetto(connectionPdD,aSoggetto);
		}
		if(soggetto==null)
			throw new DriverConfigurazioneNotFound("[getConsegnaRispostaAsincrona] Soggetto non trovato");


		// Porta Applicativa
		PortaApplicativa pa = null;
		if(requestInfo!=null && requestInfo.getRequestConfig()!=null && richiestaApplicativa.getIdPortaApplicativa()!=null && richiestaApplicativa.getIdPortaApplicativa().getNome()!=null) {
			if( requestInfo.getRequestConfig().getPortaApplicativa()!=null && richiestaApplicativa.getIdPortaApplicativa().getNome().equals(requestInfo.getRequestConfig().getPortaApplicativa().getNome())) {
				pa = requestInfo.getRequestConfig().getPortaApplicativa();
			}
			else if( requestInfo.getRequestConfig().getPortaApplicativaDefault()!=null && richiestaApplicativa.getIdPortaApplicativa().getNome().equals(requestInfo.getRequestConfig().getPortaApplicativaDefault().getNome())) {
				pa = requestInfo.getRequestConfig().getPortaApplicativaDefault();
			}
		}
		if(pa==null) {
			pa = this.configurazionePdD.getPortaApplicativa(connectionPdD,richiestaApplicativa.getIdPortaApplicativa());
		}


		// CONNETTORE
		Connettore connettore = serv.getConnettore();
		java.util.Map<String,String> properties = null;
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
			setPAUrlPrefixRewriter(connectionPdD,connettore, aSoggetto, requestInfo);

			// Properties connettore
			properties = new java.util.HashMap<>();
			for(int i=0;i<connettore.sizePropertyList();i++){
				properties.put(connettore.getProperty(i).getNome(),connettore.getProperty(i).getValore());
			}
		}


		// PROTOCOL-PROPERTIES
		Map<String, List<String>> protocol_properties = new HashMap<>();
		for(int i=0;i<pa.sizeProprietaList();i++){
			TransportUtils.put(protocol_properties, pa.getProprieta(i).getNome(),pa.getProprieta(i).getValore(), false);
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

	protected GestioneErrore getGestioneErroreConnettoreRispostaAsincrona(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding, Connection connectionPdD,ServizioApplicativo sa)throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

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





	// CHECK CERTIFICATI
	
	protected CertificateCheck checkCertificatoApplicativo(Connection connectionPdD,boolean useCache,
			long idSA, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		ServizioApplicativo sa = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			sa = driverDB.getServizioApplicativo(idSA);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatoApplicativo(sa,sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	protected CertificateCheck checkCertificatoApplicativo(Connection connectionPdD,boolean useCache,
			IDServizioApplicativo idSA, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		ServizioApplicativo sa = null;
		if(useCache) {
			sa = this.configurazionePdD.getServizioApplicativo(connectionPdD, idSA);
		}
		else {
			sa = this.configurazionePdD.getDriverConfigurazionePdD().getServizioApplicativo(idSA);
		}
		return checkCertificatoApplicativo(sa, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static CertificateCheck checkCertificatoApplicativo(ServizioApplicativo sa, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(sa==null) {
			throw new DriverConfigurazioneException("Applicativo non fornito");
		}
		if(sa.getInvocazionePorta()==null || sa.getInvocazionePorta().sizeCredenzialiList()<=0) {
			throw new DriverConfigurazioneException("Nessuna credenziale risulta associata all'applicativo");
		}
		List<byte[]> certs = new ArrayList<>();
		List<Boolean> strictValidation = new ArrayList<>();
		for (int i = 0; i < sa.getInvocazionePorta().sizeCredenzialiList(); i++) {
			Credenziali c = sa.getInvocazionePorta().getCredenziali(i);
			if(!org.openspcoop2.core.config.constants.CredenzialeTipo.SSL.equals(c.getTipo())) {
				throw new DriverConfigurazioneException("La credenziale ("+c.getTipo()+") associata all'applicativo non è un certificato x509");
			}
			if(c.getCertificate()!=null) {
				certs.add(c.getCertificate());
				strictValidation.add(c.isCertificateStrictVerification());
			}
		}
		if(certs.isEmpty()) {
			throw new DriverConfigurazioneException("Nessun certificato risulta associata all'applicativo");
		}
		else {
			try {
				return CertificateUtils.checkCertificateClient(certs, strictValidation, sogliaWarningGiorni,  
						addCertificateDetails, separator, newLine,
						log);
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}

	}

	protected CertificateCheck checkCertificatoModiApplicativo(Connection connectionPdD,boolean useCache,
			long idSA, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		ServizioApplicativo sa = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			sa = driverDB.getServizioApplicativo(idSA);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatoModiApplicativo(sa,sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	protected CertificateCheck checkCertificatoModiApplicativo(Connection connectionPdD,boolean useCache,
			IDServizioApplicativo idSA, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		ServizioApplicativo sa = null;
		if(useCache) {
			sa = this.configurazionePdD.getServizioApplicativo(connectionPdD, idSA);
		}
		else {
			sa = this.configurazionePdD.getDriverConfigurazionePdD().getServizioApplicativo(idSA);
		}
		return checkCertificatoModiApplicativo(sa, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static CertificateCheck checkCertificatoModiApplicativo(ServizioApplicativo sa, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		String protocollo = null;
		try {
			protocollo = ProtocolFactoryManager.getInstance().getProtocolByOrganizationType(sa.getTipoSoggettoProprietario());
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		boolean modi = CostantiLabel.MODIPA_PROTOCOL_NAME.equals(protocollo);
		if(!modi) {
			throw new DriverConfigurazioneException("Il profilo di interoperabilità dell'applicativo non è "+CostantiLabel.MODIPA_PROTOCOL_LABEL);
		}
		
		KeystoreParams keystoreParams = ModIUtils.getApplicativoKeystoreParams(sa.getProtocolPropertyList());
		if(keystoreParams==null) {
			throw new DriverConfigurazioneException("Non risulta alcun keystore, da utilizzare per la firma "+CostantiLabel.MODIPA_PROTOCOL_LABEL+", associato all'applicativo");
		}
		
		CertificateCheck check = null;
		boolean classpathSupported = false;
		try {
			if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
				IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
				
				check = CertificateUtils.checkKeyPair(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyPairPublicKeyPath(), keystoreParams.getKeyPassword(), keystoreParams.getKeyPairAlgorithm(),
						byokUnwrapManager,
						false, //addCertificateDetails,  
						separator, newLine);
			}
			else if(SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
				throw new DriverConfigurazioneException("Nell'Applicativo "+sa.getNome()+" la configurazione ModI utilizza un keystore "+SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_LABEL+" non compatibile la firma dei messaggi");
			}
			else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
				IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
				
				check = CertificateUtils.checkKeystoreJWKs(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyAlias(), byokUnwrapManager,
						false, //addCertificateDetails,  
						separator, newLine);
			}
			else {
				IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
				
				if(keystoreParams.getStore()!=null) {
					check = CertificateUtils.checkKeyStore(CostantiLabel.STORE_CARICATO_BASEDATI, keystoreParams.getStore(), keystoreParams.getType(), keystoreParams.getPassword(), 
							byokUnwrapManager,
							keystoreParams.getKeyAlias(), keystoreParams.getKeyPassword(),
							sogliaWarningGiorni, 
							addCertificateDetails, separator, newLine,
							log);
				}
				else {
					check = CertificateUtils.checkKeyStore(keystoreParams.getPath(), classpathSupported, keystoreParams.getType(), keystoreParams.getPassword(), 
							byokUnwrapManager,
							keystoreParams.getKeyAlias(), keystoreParams.getKeyPassword(),
							sogliaWarningGiorni, 
							addCertificateDetails, separator, newLine,
							log);
				}
			}
		}catch(Exception t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}

		if(check==null || StatoCheck.OK.equals(check.getStatoCheck())) {
			byte[] cert = ModIUtils.getApplicativoKeystoreCertificate(sa.getProtocolPropertyList());
			if(cert!=null && cert.length>0) {
				try {
					return CertificateUtils.checkSingleCertificate("Certificato associato al keystore ModI", cert, 
							sogliaWarningGiorni, 
							separator, newLine);
				}catch(Exception t) {
					throw new DriverConfigurazioneException(t.getMessage(),t);
				}
			}
		}
		
		return check;
	}

	
	protected CertificateCheck checkCertificatiConnettoreHttpsById(Connection connectionPdD,boolean useCache,
			long idConnettore, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(connectionPdD!=null) {
			// nop
		}
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		Connettore connettore = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			connettore = driverDB.getConnettore(idConnettore);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatiConnettoreHttpsById(connettore,sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static CertificateCheck checkCertificatiConnettoreHttpsById(Connettore connettore, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		TipiConnettore tipo = TipiConnettore.toEnumFromName(connettore.getTipo());
		if( !TipiConnettore.HTTPS.equals(tipo)) {
			throw new DriverConfigurazioneException("Il connettore indicato non è di tipo https");
		}
		
		SSLConfig httpsProp = null;
		Map<String,Object> dynamicMap = null;
		try {
			httpsProp = ConnettoreHTTPSProperties.readProperties(connettore.getProperties());
			dynamicMap = DynamicMapBuilderUtils.buildDynamicMap(null, null, null, 
					log);
			httpsProp.setDynamicMap(dynamicMap);
		}catch(Exception t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		CertificateCheck check = null;
		boolean classpathSupported = false;
				
		String storeDetails = null; // per evitare duplicazione
		
		if(httpsProp.getKeyStoreLocation()!=null) {
			try {
				IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapPolicyUtilities.getBYOKUnwrapManager(httpsProp.getKeyStoreBYOKPolicy(), dynamicMap);
				
				check = CertificateUtils.checkKeyStore(httpsProp.getKeyStoreLocation(), classpathSupported, httpsProp.getKeyStoreType(), 
						httpsProp.getKeyStorePassword(), 
						byokUnwrapManager,
						httpsProp.getKeyAlias(), httpsProp.getKeyPassword(),
						sogliaWarningGiorni, 
						false, //addCertificateDetails,  
						separator, newLine,
						log);
				
				if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
					storeDetails = CertificateUtils.toStringKeyStore(httpsProp.getKeyStoreLocation(), httpsProp.getKeyStoreType(),
							httpsProp.getKeyStoreBYOKPolicy(),
							httpsProp.getKeyAlias(), 
							separator, newLine);
				}
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
		
		if(check==null || StatoCheck.OK.equals(check.getStatoCheck())) {
			if(!httpsProp.isTrustAllCerts() && httpsProp.getTrustStoreLocation()!=null) {
				try {
					check = CertificateUtils.checkTrustStore(httpsProp.getTrustStoreLocation(), classpathSupported, httpsProp.getTrustStoreType(), 
							httpsProp.getTrustStorePassword(), httpsProp.getTrustStoreCRLsLocation(), httpsProp.getTrustStoreOCSPPolicy(),
							sogliaWarningGiorni, 
							false, //addCertificateDetails, 
							separator, newLine,
							log);
					
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringTrustStore(httpsProp.getTrustStoreLocation(), httpsProp.getTrustStoreType(),
								httpsProp.getTrustStoreCRLsLocation(), httpsProp.getTrustStoreOCSPPolicy(),
								separator, newLine);
					}
				}catch(Exception t) {
					throw new DriverConfigurazioneException(t.getMessage(),t);
				}
			}
		}
		
		if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
			String id = RegistroServiziReader.ID_CONFIGURAZIONE_CONNETTORE_HTTPS;
			if(addCertificateDetails && storeDetails!=null) {
				id = id + newLine + storeDetails;
			}
			check.setConfigurationId(id);	
		}	
		
		if(check==null) {
			// connettore https con truststore 'all' senza client autentication
			check = new CertificateCheck();
			check.setStatoCheck(StatoCheck.OK);
		}
		
		return check;
	}

	public CertificateCheck checkCertificatiJvm(int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException{
		return checkCertificatiJvm(sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	
	public static CertificateCheck checkCertificatiJvm(int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		CertificateCheck check = null;
		boolean classpathSupported = false;
		
		String storeDetails = null; // per evitare duplicazione
		
		KeystoreParams keystoreParams = CertificateUtils.readKeyStoreParamsJVM();
		KeystoreParams truststoreParams = CertificateUtils.readTrustStoreParamsJVM();
		
		if(keystoreParams!=null) {
			try {
				IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
				
				check = CertificateUtils.checkKeyStore(keystoreParams.getPath(), classpathSupported, keystoreParams.getType(),
						keystoreParams.getPassword(), 
						byokUnwrapManager,
						keystoreParams.getKeyAlias(), keystoreParams.getKeyPassword(),
						sogliaWarningGiorni, 
						false, //addCertificateDetails,  
						separator, newLine,
						log);
				
				if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
					storeDetails = CertificateUtils.toStringKeyStore(keystoreParams, 
							separator, newLine);
				}
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
		
		if(check==null || StatoCheck.OK.equals(check.getStatoCheck())) {
			if(truststoreParams!=null) {
				try {
					check = CertificateUtils.checkTrustStore(truststoreParams.getPath(), classpathSupported, truststoreParams.getType(), 
							truststoreParams.getPassword(), truststoreParams.getCrls(), truststoreParams.getOcspPolicy(),
							sogliaWarningGiorni, 
							false, //addCertificateDetails, 
							separator, newLine,
							log);
					
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringTrustStore(truststoreParams,
								separator, newLine);
					}
				}catch(Exception t) {
					throw new DriverConfigurazioneException(t.getMessage(),t);
				}
			}
		}
		
		if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
			/**String id = "Configurazione https della JVM";*/
			String id = "";
			if(addCertificateDetails && storeDetails!=null) {
				id = id + newLine + storeDetails;
			}
			check.setConfigurationId(id);	
		}	
		
		if(check==null) {
			// connettore https con truststore 'all' senza client autentication
			check = new CertificateCheck();
			check.setStatoCheck(StatoCheck.OK);
		}
		
		return check;
	}
	
	protected CertificateCheck checkCertificatiConnettoreHttpsTokenPolicyValidazione(Connection connectionPdD,boolean useCache,
			String nome, String tipo, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(connectionPdD!=null) {
			// nop
		}
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		GenericProperties gp = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			gp = driverDB.getGenericProperties(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatiConnettoreHttpsTokenPolicyValidazione(gp, tipo, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static CertificateCheck checkCertificatiConnettoreHttpsTokenPolicyValidazione(GenericProperties gp, String tipo, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		GestioneToken gestioneToken = new GestioneToken();
		List<org.openspcoop2.core.config.Connettore> listConnettori = null;
		try {
			PolicyGestioneToken policy = TokenUtilities.convertTo(gp, gestioneToken);
			
			if( !policy.isEndpointHttps() ) {
				throw new DriverConfigurazioneException("La configurazione della policy "+gp.getNome()+" non utilizza un connettore di tipo https");
			}
			
			listConnettori = ConnettoreCheck.convertTokenPolicyValidazioneToConnettore(gp, log);
		}catch(Exception t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		
		CertificateCheck check = _checkConnettori(listConnettori, tipo, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
		if(check!=null) {
			return check;
		}
		
		throw new DriverConfigurazioneException("Nella policy "+gp.getNome()+" non risulta configurato un connettore per la funzionalità '"+tipo+"'");
		
	}
	
	protected CertificateCheck checkCertificatiValidazioneJwtTokenPolicyValidazione(Connection connectionPdD,boolean useCache,
			String nome, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(connectionPdD!=null) {
			// nop
		}
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		GenericProperties gp = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			gp = driverDB.getGenericProperties(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatiValidazioneJwtTokenPolicyValidazione(gp, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static final String ID_CONFIGURAZIONE_TOKEN_VALIDAZIONE_JWT = "Configurazione Validazione JWT";
	public static CertificateCheck checkCertificatiValidazioneJwtTokenPolicyValidazione(GenericProperties gp, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		GestioneToken gestioneToken = new GestioneToken();
		KeystoreParams keystoreParams = null;
		KeystoreParams truststoreParams = null;
		PolicyGestioneToken policy = null;
		try {
			policy = TokenUtilities.convertTo(gp, gestioneToken);
			if(!TokenUtilities.isValidazioneEnabled(gp)) {
				throw new DriverConfigurazioneException("La configurazione nella policy "+gp.getNome()+" non utilizza la funzionalità di validazione JWT");
			}
			String tokenType = policy.getTipoToken();
			if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_TYPE_JWS.equals(tokenType)) {
    			// JWS Compact   			
				truststoreParams = TokenUtilities.getValidazioneJwtKeystoreParams(policy);
			}
    		else if(org.openspcoop2.pdd.core.token.Costanti.POLICY_TOKEN_TYPE_JWE.equals(tokenType)) {
    			// JWE Compact
    			keystoreParams = TokenUtilities.getValidazioneJwtKeystoreParams(policy);
    		}
		}catch(Exception t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		
		CertificateCheck check = null;
		boolean classpathSupported = true;
				
		String storeDetails = null; // per evitare duplicazione
		
		if(keystoreParams!=null) {
			try {
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					throw new DriverConfigurazioneException("Nella configurazione della policy "+gp.getNome()+" la funzionalità di validazione JWT utilizza un keystore "+SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_LABEL+" non compatibile con il criterio di validazione dei certificati");
				}
				else if(SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					throw new DriverConfigurazioneException("Nella configurazione della policy "+gp.getNome()+" la funzionalità di validazione JWT utilizza un keystore "+SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_LABEL+" non compatibile con il criterio di validazione dei certificati");
					
				}
				else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					if(keystoreParams.getPath().startsWith(JOSEUtils.HTTP_PROTOCOL) || keystoreParams.getPath().startsWith(JOSEUtils.HTTPS_PROTOCOL)) {
						byte [] store = getStoreCertificatiTokenPolicy(policy, keystoreParams, log);
						check = CertificateUtils.checkKeystoreJWKs(keystoreParams.getPath(), new String(store), keystoreParams.getKeyAlias(), 
								byokUnwrapManager,
								false, //addCertificateDetails,  
								separator, newLine);
					}
					else{
						check = CertificateUtils.checkKeystoreJWKs(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyAlias(), 
								byokUnwrapManager,
								false, //addCertificateDetails,  
								separator, newLine);
					}
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeystoreJWKs(keystoreParams, 
								separator, newLine);
					}
				}
				else {	
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					if(keystoreParams.getPath().startsWith(JOSEUtils.HTTP_PROTOCOL) || keystoreParams.getPath().startsWith(JOSEUtils.HTTPS_PROTOCOL)) {
						byte [] store = getStoreCertificatiTokenPolicy(policy, keystoreParams, log);
						check = CertificateUtils.checkKeyStore(keystoreParams.getPath(), store, keystoreParams.getType(),
								keystoreParams.getPassword(), 
								byokUnwrapManager,
								keystoreParams.getKeyAlias(), keystoreParams.getKeyPassword(),
								sogliaWarningGiorni, 
								false, //addCertificateDetails,  
								separator, newLine,
								log);
					}
					else {
						check = CertificateUtils.checkKeyStore(keystoreParams.getPath(), classpathSupported, keystoreParams.getType(),
								keystoreParams.getPassword(), 
								byokUnwrapManager,
								keystoreParams.getKeyAlias(), keystoreParams.getKeyPassword(),
								sogliaWarningGiorni, 
								false, //addCertificateDetails,  
								separator, newLine,
								log);
					}
					
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeyStore(keystoreParams, 
								separator, newLine);
					}
				}
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
		
		if( (check==null || StatoCheck.OK.equals(check.getStatoCheck())) &&
			truststoreParams!=null
			) {
			try {
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(truststoreParams.getType())) {
					throw new DriverConfigurazioneException("Nella configurazione della policy "+gp.getNome()+" la funzionalità di validazione JWT utilizza un keystore "+SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_LABEL+" non compatibile con il criterio di validazione dei certificati");
				}
				else if(SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(truststoreParams.getType())) {
					if(truststoreParams.getPath().startsWith(JOSEUtils.HTTP_PROTOCOL) || truststoreParams.getPath().startsWith(JOSEUtils.HTTPS_PROTOCOL)) {
						byte [] store = getStoreCertificatiTokenPolicy(policy, truststoreParams, log);
						check = CertificateUtils.checkPublicKey(truststoreParams.getPath(), store, truststoreParams.getKeyPairAlgorithm(),
								false, //addCertificateDetails,  
								separator, newLine);
					}
					else {
						check = CertificateUtils.checkPublicKey(classpathSupported, truststoreParams.getPath(), truststoreParams.getKeyPairAlgorithm(),
								false, //addCertificateDetails,  
								separator, newLine);
					}
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringPublicKey(truststoreParams, 
								separator, newLine);
					}
				}
				else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(truststoreParams.getType())) {
					if(truststoreParams.getPath().startsWith(JOSEUtils.HTTP_PROTOCOL) || truststoreParams.getPath().startsWith(JOSEUtils.HTTPS_PROTOCOL)) {
						byte [] store = getStoreCertificatiTokenPolicy(policy, truststoreParams, log);
						check = CertificateUtils.checkTruststoreJWKs(truststoreParams.getPath(), new String(store), truststoreParams.getKeyAlias(), 
								false, //addCertificateDetails,  
								separator, newLine);
					}
					else {
						check = CertificateUtils.checkTruststoreJWKs(classpathSupported, truststoreParams.getPath(), truststoreParams.getKeyAlias(), 
								false, //addCertificateDetails,  
								separator, newLine);
					}
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringTruststoreJWKs(truststoreParams, 
								separator, newLine);
					}
				}
				else {		
					String alias = truststoreParams.getKeyAlias();
					if(alias!=null && 
							(
									alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C)
									||
									alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5T)
									||
									alias.equals(Costanti.POLICY_VALIDAZIONE_SPECIAL_CASE_USE_X5C_X5T)
							)
						) {
						alias=null; // special case, valido tutti i certificati nel truststore con cui validero' il certificato presente in x5c o x5t
					}
					
					if(truststoreParams.getPath().startsWith(JOSEUtils.HTTP_PROTOCOL) || truststoreParams.getPath().startsWith(JOSEUtils.HTTPS_PROTOCOL)) {
						byte [] store = getStoreCertificatiTokenPolicy(policy, truststoreParams, log);
						check = CertificateUtils.checkTrustStore(truststoreParams.getPath(), store, truststoreParams.getType(), 
								truststoreParams.getPassword(), truststoreParams.getCrls(), truststoreParams.getOcspPolicy(),
								alias,
								sogliaWarningGiorni, 
								false, //addCertificateDetails, 
								separator, newLine,
								log);
					}
					else {
						check = CertificateUtils.checkTrustStore(truststoreParams.getPath(), classpathSupported, truststoreParams.getType(), 
								truststoreParams.getPassword(), truststoreParams.getCrls(), truststoreParams.getOcspPolicy(),
								alias,
								sogliaWarningGiorni, 
								false, //addCertificateDetails, 
								separator, newLine,
								log);
					}
					
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringTrustStore(truststoreParams,
								separator, newLine);
					}
				}
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
		
		if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
			String id = ID_CONFIGURAZIONE_TOKEN_VALIDAZIONE_JWT;
			if(addCertificateDetails && storeDetails!=null) {
				id = id + newLine + storeDetails;
			}
			check.setConfigurationId(id);	
		}	
		
		if(check==null) {
			// connettore https con truststore 'all' senza client autentication
			check = new CertificateCheck();
			check.setStatoCheck(StatoCheck.OK);
		}
		
		return check;
	}
	
	
	private static byte[] getStoreCertificatiTokenPolicy(AbstractPolicyToken policy,KeystoreParams truststoreParams, Logger log) throws TokenException, SecurityException {
		Properties p = new Properties();
		p.put(SecurityConstants.JOSE_KEYSTORE_FILE, truststoreParams.getPath());
		p.put(SecurityConstants.JOSE_KEYSTORE_TYPE, truststoreParams.getType());
		if(truststoreParams.getPassword()!=null) {
			p.put(SecurityConstants.JOSE_KEYSTORE_PSWD, truststoreParams.getPassword());
		}
		TokenUtilities.injectJOSEConfigSsl(p, policy,
				null, null);
		boolean throwError = true;
		boolean forceNoCache = true;
		return JOSEUtils.readHttpStore(p, null, truststoreParams.getPath(), log, throwError, forceNoCache);
	}
	
	
	protected CertificateCheck checkCertificatiForwardToJwtTokenPolicyValidazione(Connection connectionPdD,boolean useCache,
			String nome, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(connectionPdD!=null) {
			// nop
		}
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		GenericProperties gp = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			gp = driverDB.getGenericProperties(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA, nome);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatiForwardToJwtTokenPolicyValidazione(gp, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static final String ID_CONFIGURAZIONE_TOKEN_VALIDAZIONE_FORWARD_TO_JWT = "Configurazione ForwardTo JWT";
	public static CertificateCheck checkCertificatiForwardToJwtTokenPolicyValidazione(GenericProperties gp, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		GestioneToken gestioneToken = new GestioneToken();
		KeystoreParams keystoreParams = null;
		KeystoreParams truststoreParams = null;
		try {
			PolicyGestioneToken policy = TokenUtilities.convertTo(gp, gestioneToken);
			if(!TokenUtilities.isTokenForwardEnabled(gp) || !policy.isForwardTokenInformazioniRaccolte()) {
				throw new DriverConfigurazioneException("La configurazione nella policy "+gp.getNome()+" non utilizza la funzionalità di forward delle informazioni raccolte del token");
			}
			String forwardInformazioniRaccolteMode = policy.getForwardTokenInformazioniRaccolteMode();
			
			if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_OP2_JWS.equals(forwardInformazioniRaccolteMode) ||
					Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWS.equals(forwardInformazioniRaccolteMode)) {
    			// JWS Compact   			
				keystoreParams = TokenUtilities.getForwardToJwtKeystoreParams(policy);
			}
			else if(Costanti.POLICY_TOKEN_FORWARD_INFO_RACCOLTE_MODE_JWE.equals(forwardInformazioniRaccolteMode)) {
    			// JWE Compact
    			truststoreParams = TokenUtilities.getForwardToJwtKeystoreParams(policy);
    			keystoreParams = TokenUtilities.getForwardToJwtKeystoreParams(policy); // verifico con keystoreParams per adesso poichè cifro con chiave privata
    			if(keystoreParams!=null) {
    				truststoreParams=null;
    			}
    		}
		}catch(Exception t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		
		CertificateCheck check = null;
		boolean classpathSupported = true;
		
		String storeDetails = null; // per evitare duplicazione
		
		if(keystoreParams!=null) {
			try {
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeyPair(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyPairPublicKeyPath(), keystoreParams.getKeyPassword(), keystoreParams.getKeyPairAlgorithm(),
							byokUnwrapManager,
							false, //addCertificateDetails,  
							separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeyPair(keystoreParams, 
								separator, newLine);
					}
				}
				else if(SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					throw new DriverConfigurazioneException("Nella configurazione della policy "+gp.getNome()+" la funzionalità di forward delle informazioni raccolte come JWT utilizza un keystore "+SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_LABEL+" non compatibile con il criterio di validazione dei certificati");
					
				}
				else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeystoreJWKs(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyAlias(),
							byokUnwrapManager,
							false, //addCertificateDetails,  
							separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeystoreJWKs(keystoreParams, 
								separator, newLine);
					}
				}
				else {
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeyStore(keystoreParams.getPath(), classpathSupported, keystoreParams.getType(),
							keystoreParams.getPassword(), 
							byokUnwrapManager,
							keystoreParams.getKeyAlias(), keystoreParams.getKeyPassword(),
							sogliaWarningGiorni, 
							false, //addCertificateDetails,  
							separator, newLine,
							log);
					
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeyStore(keystoreParams, 
								separator, newLine);
					}
				}
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
		
		if( (check==null || StatoCheck.OK.equals(check.getStatoCheck())) &&
			truststoreParams!=null
			) {
			try {
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(truststoreParams.getType())) {
					throw new DriverConfigurazioneException("Nella configurazione della policy "+gp.getNome()+" la funzionalità di forward delle informazioni raccolte come JWT utilizza un keystore "+SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_LABEL+" non compatibile con il criterio di validazione dei certificati");
				}
				else if(SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(truststoreParams.getType())) {
					check = CertificateUtils.checkPublicKey(classpathSupported, truststoreParams.getPath(), truststoreParams.getKeyPairAlgorithm(),
							false, //addCertificateDetails,  
							separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringPublicKey(truststoreParams, 
								separator, newLine);
					}
				}
				else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(truststoreParams.getType())) {
					check = CertificateUtils.checkTruststoreJWKs(classpathSupported, truststoreParams.getPath(), truststoreParams.getKeyAlias(), 
							false, //addCertificateDetails,  
							separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringTruststoreJWKs(truststoreParams, 
								separator, newLine);
					}
				}
				else {		
					check = CertificateUtils.checkTrustStore(truststoreParams.getPath(), classpathSupported, truststoreParams.getType(), 
							truststoreParams.getPassword(), truststoreParams.getCrls(), truststoreParams.getOcspPolicy(),
							truststoreParams.getKeyAlias(),
							sogliaWarningGiorni, 
							false, //addCertificateDetails, 
							separator, newLine,
							log);
					
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringTrustStore(truststoreParams,
								separator, newLine);
					}
				}
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
		
		if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
			String id = ID_CONFIGURAZIONE_TOKEN_VALIDAZIONE_FORWARD_TO_JWT;
			if(addCertificateDetails && storeDetails!=null) {
				id = id + newLine + storeDetails;
			}
			check.setConfigurationId(id);	
		}	
		
		if(check==null) {
			// connettore https con truststore 'all' senza client autentication
			check = new CertificateCheck();
			check.setStatoCheck(StatoCheck.OK);
		}
		
		return check;
	}


	protected CertificateCheck checkCertificatiConnettoreHttpsTokenPolicyNegoziazione(Connection connectionPdD,boolean useCache,
			String nome, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(connectionPdD!=null) {
			// nop
		}
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		GenericProperties gp = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			gp = driverDB.getGenericProperties(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA_RETRIEVE, nome);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatiConnettoreHttpsTokenPolicyNegoziazione(gp, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static CertificateCheck checkCertificatiConnettoreHttpsTokenPolicyNegoziazione(GenericProperties gp, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		List<org.openspcoop2.core.config.Connettore> listConnettori = null;
		try {
			PolicyNegoziazioneToken policy = TokenUtilities.convertTo(gp);
			
			if( !policy.isEndpointHttps() ) {
				throw new DriverConfigurazioneException("La configurazione della policy "+gp.getNome()+" non utilizza un connettore di tipo https");
			}
			
			listConnettori = ConnettoreCheck.convertTokenPolicyNegoziazioneToConnettore(gp, log); // sarà solo uno
		}catch(Exception t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		
		CertificateCheck check = _checkConnettori(listConnettori, null, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
		if(check!=null) {
			return check;
		}
		throw new DriverConfigurazioneException("Nella policy "+gp.getNome()+" non risulta configurato un connettore https");
		
	}
	
	protected CertificateCheck checkCertificatiSignedJwtTokenPolicyNegoziazione(Connection connectionPdD,boolean useCache,
			String nome, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(connectionPdD!=null) {
			// nop
		}
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		GenericProperties gp = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			gp = driverDB.getGenericProperties(org.openspcoop2.pdd.core.token.Costanti.TIPOLOGIA_RETRIEVE, nome);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatiSignedJwtTokenPolicyNegoziazione(gp, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static final String ID_CONFIGURAZIONE_TOKEN_NEGOZIAZIONE_SIGNED_JWT = "Configurazione SignedJWT";
	public static CertificateCheck checkCertificatiSignedJwtTokenPolicyNegoziazione(GenericProperties gp, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		KeystoreParams keystoreParams = null;
		try {
			PolicyNegoziazioneToken policy = TokenUtilities.convertTo(gp);
			if(!policy.isRfc7523x509Grant()) {
				throw new DriverConfigurazioneException("La configurazione nella policy "+gp.getNome()+" non utilizza la funzionalità SignedJWT (RFC 7523)");
			}
			// JWS Compact   			
			keystoreParams = TokenUtilities.getSignedJwtKeystoreParams(policy);
		}catch(Exception t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		
		CertificateCheck check = null;
		boolean classpathSupported = true;
		
		String storeDetails = null; // per evitare duplicazione
		
		if(keystoreParams!=null) {
			try {

				if(Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_VALUE.equalsIgnoreCase(keystoreParams.getPath())) {
					throw new DriverConfigurazioneException("Nella configurazione della policy "+gp.getNome()+" la funzionalità di SignedJWT utilizza la modalità '"+Costanti.KEYSTORE_TYPE_APPLICATIVO_MODI_LABEL+"'; la validazione dei certificati verrà effettuata su ogni singolo applicativo");
				}
				if(Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_VALUE.equalsIgnoreCase(keystoreParams.getPath())) {
					throw new DriverConfigurazioneException("Nella configurazione della policy "+gp.getNome()+" la funzionalità di SignedJWT utilizza la modalità '"+Costanti.KEYSTORE_TYPE_FRUIZIONE_MODI_LABEL+"'; la validazione dei certificati verrà effettuata sulla fruizione");
				}
				
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeyPair(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyPairPublicKeyPath(), keystoreParams.getKeyPassword(), keystoreParams.getKeyPairAlgorithm(),
							byokUnwrapManager,
							false, //addCertificateDetails,  
							separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeyPair(keystoreParams, 
								separator, newLine);
					}
				}
				else if(SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					throw new DriverConfigurazioneException("Nella configurazione della policy "+gp.getNome()+" la funzionalità di SignedJWT utilizza un keystore "+SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_LABEL+" non compatibile con il criterio di validazione dei certificati");
				}
				else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeystoreJWKs(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyAlias(), 
							byokUnwrapManager,
							false, //addCertificateDetails,  
							separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeystoreJWKs(keystoreParams, 
								separator, newLine);
					}
				}
				else {
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeyStore(keystoreParams.getPath(), classpathSupported, keystoreParams.getType(),
							keystoreParams.getPassword(), 
							byokUnwrapManager,
							keystoreParams.getKeyAlias(), keystoreParams.getKeyPassword(),
							sogliaWarningGiorni, 
							false, //addCertificateDetails,  
							separator, newLine,
							log);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeyStore(keystoreParams, 
								separator, newLine);
					}
				}
				
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
		
		if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
			String id = ID_CONFIGURAZIONE_TOKEN_NEGOZIAZIONE_SIGNED_JWT;
			if(addCertificateDetails && storeDetails!=null) {
				id = id + newLine + storeDetails;
			}
			check.setConfigurationId(id);	
		}	
		
		if(check==null) {
			// connettore https con truststore 'all' senza client autentication
			check = new CertificateCheck();
			check.setStatoCheck(StatoCheck.OK);
		}
		
		return check;
	}


	protected CertificateCheck checkCertificatiConnettoreHttpsAttributeAuthority(Connection connectionPdD,boolean useCache,
			String nome, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(connectionPdD!=null) {
			// nop
		}
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		GenericProperties gp = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			gp = driverDB.getGenericProperties(org.openspcoop2.pdd.core.token.Costanti.ATTRIBUTE_AUTHORITY, nome);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatiConnettoreHttpsAttributeAuthority(gp, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static CertificateCheck checkCertificatiConnettoreHttpsAttributeAuthority(GenericProperties gp, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		List<org.openspcoop2.core.config.Connettore> listConnettori = null;
		try {
			PolicyAttributeAuthority policy = AttributeAuthorityUtilities.convertTo(gp);
			
			if( !policy.isEndpointHttps() ) {
				throw new DriverConfigurazioneException("La configurazione della policy "+gp.getNome()+" non utilizza un connettore di tipo https");
			}
			
			listConnettori = ConnettoreCheck.convertAttributeAuthorityToConnettore(gp, log); // sarà solo uno
		}catch(Exception t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		
		CertificateCheck check = _checkConnettori(listConnettori, null, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				log);
		if(check!=null) {
			return check;
		}
		throw new DriverConfigurazioneException("Nell'AttributeAuthority "+gp.getNome()+" non risulta configurato un connettore https");
		
	}
		
	protected CertificateCheck checkCertificatiAttributeAuthorityJwtRichiesta(Connection connectionPdD,boolean useCache,
			String nome, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(connectionPdD!=null) {
			// nop
		}
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		GenericProperties gp = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			gp = driverDB.getGenericProperties(org.openspcoop2.pdd.core.token.Costanti.ATTRIBUTE_AUTHORITY, nome);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatiAttributeAuthorityJwtRichiesta(gp, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static final String ID_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_JWT_RICHIESTA = "Configurazione JWS Richiesta";
	public static CertificateCheck checkCertificatiAttributeAuthorityJwtRichiesta(GenericProperties gp, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		KeystoreParams keystoreParams = null;
		try {
			PolicyAttributeAuthority policy = AttributeAuthorityUtilities.convertTo(gp);
			if(!policy.isRequestJws()) {
				throw new DriverConfigurazioneException("La configurazione nell'AttributeAuthority "+gp.getNome()+" non definisce il tipo di richiesta come JWS");
			}
			// JWS Compact   			
			keystoreParams = AttributeAuthorityUtilities.getRequestJwsKeystoreParams(policy);
		}catch(Exception t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		
		CertificateCheck check = null;
		boolean classpathSupported = true;
		
		String storeDetails = null; // per evitare duplicazione
		
		if(keystoreParams!=null) {
			try {
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeyPair(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyPairPublicKeyPath(), keystoreParams.getKeyPassword(), keystoreParams.getKeyPairAlgorithm(),
							byokUnwrapManager,
							false, //addCertificateDetails,  
							separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeyPair(keystoreParams, 
								separator, newLine);
					}
				}
				else if(SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					throw new DriverConfigurazioneException("Nell'Attribute Authority "+gp.getNome()+" la configurazione della richiesta JWS utilizza un keystore "+SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_LABEL+" non compatibile con il criterio di validazione dei certificati");
				}
				else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeystoreJWKs(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyAlias(), 
							byokUnwrapManager,
							false, //addCertificateDetails,  
							separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeystoreJWKs(keystoreParams, 
								separator, newLine);
					}
				}
				else {
					
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeyStore(keystoreParams.getPath(), classpathSupported, keystoreParams.getType(),
							keystoreParams.getPassword(), 
							byokUnwrapManager,
							keystoreParams.getKeyAlias(), keystoreParams.getKeyPassword(),
							sogliaWarningGiorni, 
							false, //addCertificateDetails,  
							separator, newLine,
							log);
					
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeyStore(keystoreParams, 
								separator, newLine);
					}
				}
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
		
		if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
			String id = ID_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_JWT_RICHIESTA;
			if(addCertificateDetails && storeDetails!=null) {
				id = id + newLine + storeDetails;
			}
			check.setConfigurationId(id);	
		}	
		
		if(check==null) {
			// connettore https con truststore 'all' senza client autentication
			check = new CertificateCheck();
			check.setStatoCheck(StatoCheck.OK);
		}
		
		return check;
	}
	
	public static final String ID_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_JWT_RISPOSTA = "Configurazione JWS Risposta";
	protected CertificateCheck checkCertificatiAttributeAuthorityJwtRisposta(Connection connectionPdD,boolean useCache,
			String nome, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(connectionPdD!=null) {
			// nop
		}
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		GenericProperties gp = null;
		IDriverConfigurazioneGet driver = this.configurazionePdD.getDriverConfigurazionePdD();
		if(driver instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driverDB = (DriverConfigurazioneDB) driver;
			gp = driverDB.getGenericProperties(org.openspcoop2.pdd.core.token.Costanti.ATTRIBUTE_AUTHORITY, nome);
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
		}
		return checkCertificatiAttributeAuthorityJwtRisposta(gp, sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static CertificateCheck checkCertificatiAttributeAuthorityJwtRisposta(GenericProperties gp, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		KeystoreParams keystoreParams = null;
		PolicyAttributeAuthority policy = null;
		try {
			policy = AttributeAuthorityUtilities.convertTo(gp);
			if(!policy.isResponseJws()) {
				throw new DriverConfigurazioneException("La configurazione nell'AttributeAuthority "+gp.getNome()+" non definisce il tipo di risposta come JWS");
			}
			// JWS Compact   			
			keystoreParams = AttributeAuthorityUtilities.getResponseJwsKeystoreParams(policy);
		}catch(Exception t) {
			throw new DriverConfigurazioneException(t.getMessage(),t);
		}
		
		CertificateCheck check = null;
		boolean classpathSupported = true;
		
		String storeDetails = null; // per evitare duplicazione
		
		if(keystoreParams!=null) {
			try {
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					throw new DriverConfigurazioneException("Nell'Attribute Authority "+gp.getNome()+" la configurazione della risposta JWS utilizza un keystore "+SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_LABEL+" non compatibile con il criterio di validazione dei certificati");
				}
				else if(SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					if(keystoreParams.getPath().startsWith(JOSEUtils.HTTP_PROTOCOL) || keystoreParams.getPath().startsWith(JOSEUtils.HTTPS_PROTOCOL)) {
						byte [] store = getStoreCertificatiTokenPolicy(policy, keystoreParams, log);
						check = CertificateUtils.checkPublicKey(keystoreParams.getPath(), store, keystoreParams.getKeyPairAlgorithm(),
								false, //addCertificateDetails,  
								separator, newLine);
					}
					else {
						check = CertificateUtils.checkPublicKey(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyPairAlgorithm(),
								false, //addCertificateDetails,  
								separator, newLine);
					}
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringPublicKey(keystoreParams, 
								separator, newLine);
					}
				}
				else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					if(keystoreParams.getPath().startsWith(JOSEUtils.HTTP_PROTOCOL) || keystoreParams.getPath().startsWith(JOSEUtils.HTTPS_PROTOCOL)) {
						byte [] store = getStoreCertificatiTokenPolicy(policy, keystoreParams, log);
						check = CertificateUtils.checkTruststoreJWKs(keystoreParams.getPath(), new String(store), keystoreParams.getKeyAlias(), 
								false, //addCertificateDetails,  
								separator, newLine);
					}
					else {
						check = CertificateUtils.checkTruststoreJWKs(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyAlias(), 
								false, //addCertificateDetails,  
								separator, newLine);
					}
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringTruststoreJWKs(keystoreParams, 
								separator, newLine);
					}
				}
				else {	
					if(keystoreParams.getPath().startsWith(JOSEUtils.HTTP_PROTOCOL) || keystoreParams.getPath().startsWith(JOSEUtils.HTTPS_PROTOCOL)) {
						byte [] store = getStoreCertificatiTokenPolicy(policy, keystoreParams, log);
						check = CertificateUtils.checkTrustStore(keystoreParams.getPath(), store, keystoreParams.getType(), 
								keystoreParams.getPassword(), keystoreParams.getCrls(), keystoreParams.getOcspPolicy(),keystoreParams.getKeyAlias(),
								sogliaWarningGiorni, 
								false, //addCertificateDetails, 
								separator, newLine,
								log);
					}
					else {
						check = CertificateUtils.checkTrustStore(keystoreParams.getPath(), classpathSupported, keystoreParams.getType(), 
								keystoreParams.getPassword(), keystoreParams.getCrls(), keystoreParams.getOcspPolicy(),keystoreParams.getKeyAlias(),
								sogliaWarningGiorni, 
								false, //addCertificateDetails, 
								separator, newLine,
								log);
					}
					
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeyStore(keystoreParams, 
								separator, newLine);
					}
				}
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
		
		if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
			String id = ID_CONFIGURAZIONE_ATTRIBUTE_AUTHORITY_JWT_RISPOSTA;
			if(addCertificateDetails && storeDetails!=null) {
				id = id + newLine + storeDetails;
			}
			check.setConfigurationId(id);	
		}	
		
		if(check==null) {
			// connettore https con truststore 'all' senza client autentication
			check = new CertificateCheck();
			check.setStatoCheck(StatoCheck.OK);
		}
		
		return check;
	}
	
	public static final String ID_CONFIGURAZIONE_RICHIESTA_MESSAGE_SECURITY = "Configurazione Richiesta 'Message Security'";
	public static final String ID_CONFIGURAZIONE_RISPOSTA_MESSAGE_SECURITY = "Configurazione Risposta 'Message Security'";
	protected CertificateCheck checkCertificatiMessageSecurityErogazioneById(Connection connectionPdD,boolean useCache,
			long idAsps, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		if(connectionPdD!=null) {
			// nop
		}
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		AccordoServizioParteSpecifica asps = null;
		IDServizio idServizio = null;
		for (IDriverRegistroServiziGet driver : RegistroServiziReader.getDriverRegistroServizi().values()) {
			if(driver instanceof DriverRegistroServiziDB) {
				try {
					DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) driver;
					asps = driverDB.getAccordoServizioParteSpecifica(idAsps);
					idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);
				}catch(Exception e) {
					throw new DriverConfigurazioneException(e.getMessage(),e);
				}
				break;
			}
			else {
				throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
			}
		}
		
		DriverConfigurazioneDB driver = null;
		if(this.configurazionePdD.getDriverConfigurazionePdD() instanceof DriverConfigurazioneDB) {
			driver = (DriverConfigurazioneDB) this.configurazionePdD.getDriverConfigurazionePdD();
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+this.configurazionePdD.getDriverConfigurazionePdD().getClass().getName()+"'");
		}
		
		List<MappingErogazionePortaApplicativa> list = this.configurazionePdD._getMappingErogazionePortaApplicativaList(idServizio, connectionPdD); // usa direttamente, senza cache, DriverConfigurazioneDB
		List<PortaApplicativa> listPorta = new ArrayList<>();
		if(list!=null && !list.isEmpty()) {
			for (MappingErogazionePortaApplicativa mappingErogazionePortaApplicativa : list) {
				listPorta.add(driver.getPortaApplicativa(mappingErogazionePortaApplicativa.getIdPortaApplicativa()));
			}
		}
		
		return checkCertificatiMessageSecurityErogazioneById(list, listPorta,
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static CertificateCheck checkCertificatiMessageSecurityErogazioneById(List<MappingErogazionePortaApplicativa> listMapping, List<PortaApplicativa> listPorta, 
			int sogliaWarningGiorni,
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
				
		if(listPorta==null || listPorta.isEmpty()) {
			throw new DriverConfigurazioneException("Param listPorta is null or empty");
		}
		if(listMapping==null || listMapping.isEmpty()) {
			throw new DriverConfigurazioneException("Param listMapping is null or empty");
		}
		if(listMapping.size()!=listPorta.size()) {
			throw new DriverConfigurazioneException("Param listPorta and listMapping are different");
		}
		
		CertificateCheck check = null;
		
		for (int i = 0; i < listPorta.size(); i++) {
			PortaApplicativa portaApplicativa = listPorta.get(i);
			MappingErogazionePortaApplicativa mapping = listMapping.get(i);
			
			List<KeystoreParams> listKeystoreParams = SecurityUtils.readRequestKeystoreParams(portaApplicativa);
			check = checkCertificatiMessageSecurityErogazioneById(true, mapping, listKeystoreParams, 
					sogliaWarningGiorni,
					addCertificateDetails, separator, newLine,
					log);
			if(check!=null) {
				return check;
			}
			
			listKeystoreParams = SecurityUtils.readResponseKeystoreParams(portaApplicativa);
			check = checkCertificatiMessageSecurityErogazioneById(false, mapping, listKeystoreParams, 
					sogliaWarningGiorni,
					addCertificateDetails, separator, newLine,
					log);
			if(check!=null) {
				return check;
			}
			
		}
				
		check = new CertificateCheck();
		check.setStatoCheck(StatoCheck.OK);
		
		return check;
	}
	public static CertificateCheck checkCertificatiMessageSecurityErogazioneById(boolean request, MappingErogazionePortaApplicativa mapping, List<KeystoreParams> listKeystoreParams, 
			int sogliaWarningGiorni,
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		CertificateCheck check = null;
		
		if(listKeystoreParams!=null && !listKeystoreParams.isEmpty()) {
			for (KeystoreParams keystoreParams : listKeystoreParams) {
				
				String descrizioneGruppo = null;
				if(!mapping.isDefault() || (mapping.getDescrizione()!=null && StringUtils.isNotEmpty(mapping.getDescrizione()) )) {
					descrizioneGruppo = mapping.getDescrizione();
				}
				
				check = checkCertificatiMessageSecurity(request, descrizioneGruppo, keystoreParams, 
						sogliaWarningGiorni,
						addCertificateDetails, separator, newLine,
						log);
				if(check!=null) {
					return check;
				}
			}
		}
		
		return null;
	}
	
	protected CertificateCheck checkCertificatiMessageSecurityFruizioneById(Connection connectionPdD,boolean useCache,
			long idFruitore, 
			int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		
		if(connectionPdD!=null) {
			// nop
		}
		
		if(useCache) {
			throw new DriverConfigurazioneException("Not Implemented");
		}
		
		IDServizio idServizio = null;
		AccordoServizioParteSpecifica asps = null;
		IDSoggetto idFruitoreObject = null;
		Fruitore fruitore = null;
		for (IDriverRegistroServiziGet driver : RegistroServiziReader.getDriverRegistroServizi().values()) {
			if(driver instanceof DriverRegistroServiziDB) {
				try {
					DriverRegistroServiziDB driverDB = (DriverRegistroServiziDB) driver;
					fruitore = driverDB.getServizioFruitore(idFruitore);
					idFruitoreObject = new IDSoggetto(fruitore.getTipo(), fruitore.getNome());
					asps = driverDB.getAccordoServizioParteSpecifica(fruitore.getIdServizio());
					idServizio = IDServizioFactory.getInstance().getIDServizioFromAccordo(asps);		
				}catch(Exception e) {
					throw new DriverConfigurazioneException(e.getMessage(),e);
				}
				break;
			}
			else {
				throw new DriverConfigurazioneException("Not Implemented with driver '"+driver.getClass().getName()+"'");
			}
		}
		if(fruitore==null) {
			throw new DriverConfigurazioneException("Fruitore con id '"+idFruitore+"' non trovato");
		}
		
		DriverConfigurazioneDB driver = null;
		if(this.configurazionePdD.getDriverConfigurazionePdD() instanceof DriverConfigurazioneDB) {
			driver = (DriverConfigurazioneDB) this.configurazionePdD.getDriverConfigurazionePdD();
		}
		else {
			throw new DriverConfigurazioneException("Not Implemented with driver '"+this.configurazionePdD.getDriverConfigurazionePdD().getClass().getName()+"'");
		}
		
		List<MappingFruizionePortaDelegata> list = this.configurazionePdD._getMappingFruizionePortaDelegataList(idFruitoreObject, idServizio, connectionPdD); // usa direttamente, senza cache, DriverConfigurazioneDB
		List<PortaDelegata> listPorta = new ArrayList<>();
		if(list!=null && !list.isEmpty()) {
			for (MappingFruizionePortaDelegata mappingFruizionePortaDelegata : list) {
				listPorta.add(driver.getPortaDelegata(mappingFruizionePortaDelegata.getIdPortaDelegata()));
			}
		}
		
		return checkCertificatiMessageSecurityFruizioneById(list, listPorta,
				sogliaWarningGiorni, 
				addCertificateDetails, separator, newLine,
				this.logger);
	}
	public static CertificateCheck checkCertificatiMessageSecurityFruizioneById(List<MappingFruizionePortaDelegata> listMapping, List<PortaDelegata> listPorta,
			int sogliaWarningGiorni,
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
				
		if(listPorta==null || listPorta.isEmpty()) {
			throw new DriverConfigurazioneException("Param listPorta is null or empty");
		}
		if(listMapping==null || listMapping.isEmpty()) {
			throw new DriverConfigurazioneException("Param listMapping is null or empty");
		}
		if(listMapping.size()!=listPorta.size()) {
			throw new DriverConfigurazioneException("Param listPorta and listMapping are different");
		}
		
		CertificateCheck check = null;
		
		for (int i = 0; i < listPorta.size(); i++) {
			PortaDelegata portaDelegata = listPorta.get(i);
			MappingFruizionePortaDelegata mapping = listMapping.get(i);
			
			List<KeystoreParams> listKeystoreParams = SecurityUtils.readRequestKeystoreParams(portaDelegata);
			check = checkCertificatiMessageSecurityFruizioneById(true, mapping, listKeystoreParams, 
					sogliaWarningGiorni,
					addCertificateDetails, separator, newLine,
					log);
			if(check!=null) {
				return check;
			}
			
			listKeystoreParams = SecurityUtils.readResponseKeystoreParams(portaDelegata);
			check = checkCertificatiMessageSecurityFruizioneById(false, mapping, listKeystoreParams, 
					sogliaWarningGiorni,
					addCertificateDetails, separator, newLine,
					log);
			if(check!=null) {
				return check;
			}
			
		}
				
		check = new CertificateCheck();
		check.setStatoCheck(StatoCheck.OK);
		
		return check;
	}
	public static CertificateCheck checkCertificatiMessageSecurityFruizioneById(boolean request, MappingFruizionePortaDelegata mapping, List<KeystoreParams> listKeystoreParams, 
			int sogliaWarningGiorni,
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		CertificateCheck check = null;
		
		if(listKeystoreParams!=null && !listKeystoreParams.isEmpty()) {
			for (KeystoreParams keystoreParams : listKeystoreParams) {
				
				String descrizioneGruppo = null;
				if(!mapping.isDefault() || (mapping.getDescrizione()!=null && StringUtils.isNotEmpty(mapping.getDescrizione()) )) {
					descrizioneGruppo = mapping.getDescrizione();
				}
				
				check = checkCertificatiMessageSecurity(request, descrizioneGruppo, keystoreParams, 
						sogliaWarningGiorni,
						addCertificateDetails, separator, newLine,
						log);
				if(check!=null) {
					return check;
				}
			}
		}
		
		return null;
	}
		
	
	
	private static CertificateCheck checkCertificatiMessageSecurity(boolean request, String descrizioneGruppo, KeystoreParams keystoreParams, 
			int sogliaWarningGiorni,
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {
		
		boolean classpathSupported = true;
		
		String storeDetails = null;
		
		if(keystoreParams!=null) {
			try {
				CertificateCheck check = null;
				if(SecurityConstants.KEYSTORE_TYPE_KEY_PAIR_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeyPair(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyPairPublicKeyPath(), keystoreParams.getKeyPassword(), keystoreParams.getKeyPairAlgorithm(),
							byokUnwrapManager,
							false, //addCertificateDetails,  
							separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeyPair(keystoreParams, 
								separator, newLine);
					}
				}
				else if(SecurityConstants.KEYSTORE_TYPE_PUBLIC_KEY_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					check = CertificateUtils.checkPublicKey(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyPairAlgorithm(),
								false, //addCertificateDetails,  
								separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringPublicKey(keystoreParams, 
								separator, newLine);
					}
				}
				else if(SecurityConstants.KEYSTORE_TYPE_JWK_VALUE.equalsIgnoreCase(keystoreParams.getType())) {
					
					IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
					
					check = CertificateUtils.checkKeystoreJWKs(classpathSupported, keystoreParams.getPath(), keystoreParams.getKeyAlias(),
							byokUnwrapManager,
							false, //addCertificateDetails,  
							separator, newLine);
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringTruststoreJWKs(keystoreParams, 
								separator, newLine);
					}
				}
				else {	
					if(keystoreParams.getKeyPassword()!=null) {
						IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapFactory.getBYOKUnwrapManager(keystoreParams.getByokPolicy(), log);
						
						check = CertificateUtils.checkKeyStore(keystoreParams.getPath(), classpathSupported, keystoreParams.getType(),
								keystoreParams.getPassword(), 
								byokUnwrapManager,
								keystoreParams.getKeyAlias(), keystoreParams.getKeyPassword(),
								sogliaWarningGiorni, 
								false, //addCertificateDetails,  
								separator, newLine,
								log);
					}
					else {
						check = CertificateUtils.checkTrustStore(keystoreParams.getPath(), classpathSupported, keystoreParams.getType(), 
								keystoreParams.getPassword(), keystoreParams.getCrls(), keystoreParams.getOcspPolicy(),keystoreParams.getKeyAlias(),
								sogliaWarningGiorni, 
								false, //addCertificateDetails, 
								separator, newLine,
								log);
					}
					
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						storeDetails = CertificateUtils.toStringKeyStore(keystoreParams, 
								separator, newLine);
					}
				}
				
				if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
					String id = request ? ID_CONFIGURAZIONE_RICHIESTA_MESSAGE_SECURITY : ID_CONFIGURAZIONE_RISPOSTA_MESSAGE_SECURITY;
					if(descrizioneGruppo!=null && !org.openspcoop2.core.constants.Costanti.MAPPING_DESCRIZIONE_DEFAULT.equals(descrizioneGruppo)) {
						id = id + " (" +descrizioneGruppo+")";
					}
					if(keystoreParams.getDescription()!=null && StringUtils.isNotEmpty(keystoreParams.getDescription())) {
						id = id + " - " +keystoreParams.getDescription();
					}
					if(addCertificateDetails && storeDetails!=null) {
						id = id + newLine + storeDetails;
					}
					check.setConfigurationId(id);	
					
					return check;
				}	
				
			}catch(Exception t) {
				throw new DriverConfigurazioneException(t.getMessage(),t);
			}
		}
		
		return null;
	}

	
	private static CertificateCheck _checkConnettori(List<org.openspcoop2.core.config.Connettore> listConnettori, String tipo, int sogliaWarningGiorni, 
			boolean addCertificateDetails, String separator, String newLine,
			Logger log) throws DriverConfigurazioneException {

		boolean classpathSupported = true;
		
		if(listConnettori!=null && !listConnettori.isEmpty()) {
			for (org.openspcoop2.core.config.Connettore connettore : listConnettori) {
					
				String tipoC = ConnettoreCheck.getPropertyValue(connettore, ConnettoreCheck.POLICY_TIPO_ENDPOINT);
				if(tipo==null || tipo.equalsIgnoreCase(tipoC)) {
				
					SSLConfig httpsProp = null;
					Map<String,Object> dynamicMap = null;
					try {
						httpsProp = ConnettoreHTTPSProperties.readProperties(connettore.getProperties());
						dynamicMap = DynamicMapBuilderUtils.buildDynamicMap(null, null, null, 
								log);
						httpsProp.setDynamicMap(dynamicMap);
					}catch(Exception t) {
						throw new DriverConfigurazioneException(t.getMessage(),t);
					}
					CertificateCheck check = null;
							
					String storeDetails = null; // per evitare duplicazione
					
					if(httpsProp.getKeyStoreLocation()!=null) {
						try {
							IBYOKUnwrapManager byokUnwrapManager = BYOKUnwrapPolicyUtilities.getBYOKUnwrapManager(httpsProp.getKeyStoreBYOKPolicy(), dynamicMap);
							
							check = CertificateUtils.checkKeyStore(httpsProp.getKeyStoreLocation(), classpathSupported, httpsProp.getKeyStoreType(), 
									httpsProp.getKeyStorePassword(), 
									byokUnwrapManager,
									httpsProp.getKeyAlias(), httpsProp.getKeyPassword(),
									sogliaWarningGiorni, 
									false, //addCertificateDetails,  
									separator, newLine,
									log);
							
							if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
								storeDetails = CertificateUtils.toStringKeyStore(httpsProp.getKeyStoreLocation(), httpsProp.getKeyStoreType(),
										httpsProp.getKeyStoreBYOKPolicy(),
										httpsProp.getKeyAlias(), 
										separator, newLine);
							}
						}catch(Exception t) {
							throw new DriverConfigurazioneException(t.getMessage(),t);
						}
					}
					
					if(check==null || StatoCheck.OK.equals(check.getStatoCheck())) {
						if(!httpsProp.isTrustAllCerts() && httpsProp.getTrustStoreLocation()!=null) {
							try {
								check = CertificateUtils.checkTrustStore(httpsProp.getTrustStoreLocation(), classpathSupported, httpsProp.getTrustStoreType(), 
										httpsProp.getTrustStorePassword(), httpsProp.getTrustStoreCRLsLocation(), httpsProp.getTrustStoreOCSPPolicy(),
										sogliaWarningGiorni, 
										false, //addCertificateDetails, 
										separator, newLine,
										log);
								
								if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
									storeDetails = CertificateUtils.toStringTrustStore(httpsProp.getTrustStoreLocation(), httpsProp.getTrustStoreType(),
											httpsProp.getTrustStoreCRLsLocation(), httpsProp.getTrustStoreOCSPPolicy(),
											separator, newLine);
								}
							}catch(Exception t) {
								throw new DriverConfigurazioneException(t.getMessage(),t);
							}
						}
					}
					
					if(check!=null && !StatoCheck.OK.equals(check.getStatoCheck())) {
						String id = RegistroServiziReader.ID_CONFIGURAZIONE_CONNETTORE_HTTPS;
						if(addCertificateDetails && storeDetails!=null) {
							id = id + newLine + storeDetails;
						}
						check.setConfigurationId(id);	
					}	
					
					if(check==null) {
						// connettore https con truststore 'all' senza client autentication
						check = new CertificateCheck();
						check.setStatoCheck(StatoCheck.OK);
					}
					
					return check;
				}
					
			}
		}
		return null;
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
	public static void setAccessoRegistroServizi(AccessoRegistro accessoRegistroServizi) {
		ConfigurazionePdDReader.accessoRegistroServizi = accessoRegistroServizi;
	}
	public static void setAccessoRegistroServiziLetto(Boolean accessoRegistroServiziLetto) {
		ConfigurazionePdDReader.accessoRegistroServiziLetto = accessoRegistroServiziLetto;
	}
	protected AccessoRegistro getAccessoRegistroServizi(Connection connectionPdD){

		if( this.configurazioneDinamica || !ConfigurazionePdDReader.accessoRegistroServiziLetto.booleanValue()){
			AccessoRegistro configRegistro = null;
			try{
				configRegistro = this.configurazionePdD.getAccessoRegistro(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.logDebug("getAccessoRegistroServizi (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("getAccessoRegistroServizi",e);
			}

			ConfigurazionePdDReader.setAccessoRegistroServizi(configRegistro);
			ConfigurazionePdDReader.setAccessoRegistroServiziLetto(true);
		}

		/**
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
	public static void setAccessoConfigurazione(AccessoConfigurazione accessoConfigurazione) {
		ConfigurazionePdDReader.accessoConfigurazione = accessoConfigurazione;
	}
	public static void setAccessoConfigurazioneLetto(Boolean accessoConfigurazioneLetto) {
		ConfigurazionePdDReader.accessoConfigurazioneLetto = accessoConfigurazioneLetto;
	}
	protected AccessoConfigurazione getAccessoConfigurazione(Connection connectionPdD){

		if( this.configurazioneDinamica || !ConfigurazionePdDReader.accessoConfigurazioneLetto.booleanValue()){
			AccessoConfigurazione tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoConfigurazione(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.logDebug("getAccessoConfigurazione (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("getAccessoConfigurazione",e);
			}

			ConfigurazionePdDReader.setAccessoConfigurazione(tmp);
			ConfigurazionePdDReader.setAccessoConfigurazioneLetto(true);
		}

		/**
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
	public static void setAccessoDatiAutorizzazione(AccessoDatiAutorizzazione accessoDatiAutorizzazione) {
		ConfigurazionePdDReader.accessoDatiAutorizzazione = accessoDatiAutorizzazione;
	}
	public static void setAccessoDatiAutorizzazioneLetto(Boolean accessoDatiAutorizzazioneLetto) {
		ConfigurazionePdDReader.accessoDatiAutorizzazioneLetto = accessoDatiAutorizzazioneLetto;
	}
	protected AccessoDatiAutorizzazione getAccessoDatiAutorizzazione(Connection connectionPdD){

		if( this.configurazioneDinamica || !ConfigurazionePdDReader.accessoDatiAutorizzazioneLetto.booleanValue()){
			AccessoDatiAutorizzazione tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoDatiAutorizzazione(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.logDebug("getAccessoDatiAutorizzazione (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("getAccessoDatiAutorizzazione",e);
			}

			ConfigurazionePdDReader.setAccessoDatiAutorizzazione(tmp);
			ConfigurazionePdDReader.setAccessoDatiAutorizzazioneLetto(true);
		}

		/**
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
	public static void setAccessoDatiAutenticazione(AccessoDatiAutenticazione accessoDatiAutenticazione) {
		ConfigurazionePdDReader.accessoDatiAutenticazione = accessoDatiAutenticazione;
	}
	public static void setAccessoDatiAutenticazioneLetto(Boolean accessoDatiAutenticazioneLetto) {
		ConfigurazionePdDReader.accessoDatiAutenticazioneLetto = accessoDatiAutenticazioneLetto;
	}
	protected AccessoDatiAutenticazione getAccessoDatiAutenticazione(Connection connectionPdD){

		if( this.configurazioneDinamica || !ConfigurazionePdDReader.accessoDatiAutenticazioneLetto.booleanValue()){
			AccessoDatiAutenticazione tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoDatiAutenticazione(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.logDebug("getAccessoDatiAutenticazione (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("getAccessoDatiAutenticazione",e);
			}

			ConfigurazionePdDReader.setAccessoDatiAutenticazione(tmp);
			ConfigurazionePdDReader.setAccessoDatiAutenticazioneLetto(true);
		}

		/**
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
	public static void setAccessoDatiGestioneToken(AccessoDatiGestioneToken accessoDatiGestioneToken) {
		ConfigurazionePdDReader.accessoDatiGestioneToken = accessoDatiGestioneToken;
	}
	public static void setAccessoDatiGestioneTokenLetto(Boolean accessoDatiGestioneTokenLetto) {
		ConfigurazionePdDReader.accessoDatiGestioneTokenLetto = accessoDatiGestioneTokenLetto;
	}
	protected AccessoDatiGestioneToken getAccessoDatiGestioneToken(Connection connectionPdD){

		if( this.configurazioneDinamica || !ConfigurazionePdDReader.accessoDatiGestioneTokenLetto.booleanValue()){
			AccessoDatiGestioneToken tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoDatiGestioneToken(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.logDebug("getAccessoDatiGestioneToken (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("getAccessoDatiGestioneToken",e);
			}

			ConfigurazionePdDReader.setAccessoDatiGestioneToken(tmp);
			ConfigurazionePdDReader.setAccessoDatiGestioneTokenLetto(true);
		}

		/**
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
	 * Restituisce le informazioni necessarie alla porta di dominio per accedere ai dati raccolti via attribute authority
	 *
	 * @return informazioni
	 * 
	 */
	private static AccessoDatiAttributeAuthority accessoDatiAttributeAuthority = null;
	private static Boolean accessoDatiAttributeAuthorityLetto = false;
	public static void setAccessoDatiAttributeAuthority(AccessoDatiAttributeAuthority accessoDatiAttributeAuthority) {
		ConfigurazionePdDReader.accessoDatiAttributeAuthority = accessoDatiAttributeAuthority;
	}
	public static void setAccessoDatiAttributeAuthorityLetto(Boolean accessoDatiAttributeAuthorityLetto) {
		ConfigurazionePdDReader.accessoDatiAttributeAuthorityLetto = accessoDatiAttributeAuthorityLetto;
	}
	protected AccessoDatiAttributeAuthority getAccessoDatiAttributeAuthority(Connection connectionPdD){

		if( this.configurazioneDinamica || !ConfigurazionePdDReader.accessoDatiAttributeAuthorityLetto.booleanValue()){
			AccessoDatiAttributeAuthority tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoDatiAttributeAuthority(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.logDebug("getAccessoDatiAttributeAuthority (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("getAccessoDatiAttributeAuthority",e);
			}

			ConfigurazionePdDReader.setAccessoDatiAttributeAuthority(tmp);
			ConfigurazionePdDReader.setAccessoDatiAttributeAuthorityLetto(true);
		}

		/**
		if(ConfigurazionePdDReader.accessoDatiAttributeAuthority.getCache()==null){
			System.out.println("ACCESSO_DATI_ATTRIBUTE_AUTHORITY CACHE DISABILITATA");
		}else{
			System.out.println("ACCESSO_DATI_ATTRIBUTE_AUTHORITY CACHE ABILITATA");
			System.out.println("ACCESSO_DATI_ATTRIBUTE_AUTHORITY CACHE ALGORITMO: "+ConfigurazionePdDReader.accessoDatiAttributeAuthority.getCache().getAlgoritmo());
			System.out.println("ACCESSO_DATI_ATTRIBUTE_AUTHORITY CACHE DIMENSIONE: "+ConfigurazionePdDReader.accessoDatiAttributeAuthority.getCache().getDimensione());
			System.out.println("ACCESSO_DATI_ATTRIBUTE_AUTHORITY CACHE ITEM IDLE: "+ConfigurazionePdDReader.accessoDatiAttributeAuthority.getCache().getItemIdleTime());
			System.out.println("ACCESSO_DATI_ATTRIBUTE_AUTHORITY CACHE ITEM LIFE SECOND: "+ConfigurazionePdDReader.accessoDatiAttributeAuthority.getCache().getItemLifeSecond());
		}
		 */

		return ConfigurazionePdDReader.accessoDatiAttributeAuthority;
	}

	/**
	 * Restituisce le informazioni necessarie alla porta di dominio per accedere ai dati di gestione dei keystore
	 *
	 * @return informazioni
	 * 
	 */
	private static AccessoDatiKeystore accessoDatiKeystore = null;
	private static Boolean accessoDatiKeystoreLetto = false;
	public static void setAccessoDatiKeystore(AccessoDatiKeystore accessoDatiKeystore) {
		ConfigurazionePdDReader.accessoDatiKeystore = accessoDatiKeystore;
	}
	public static void setAccessoDatiKeystoreLetto(Boolean accessoDatiKeystoreLetto) {
		ConfigurazionePdDReader.accessoDatiKeystoreLetto = accessoDatiKeystoreLetto;
	}
	protected AccessoDatiKeystore getAccessoDatiKeystore(Connection connectionPdD){

		if( this.configurazioneDinamica || !ConfigurazionePdDReader.accessoDatiKeystoreLetto.booleanValue()){
			AccessoDatiKeystore tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoDatiKeystore(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.logDebug("getAccessoDatiKeystore (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("getAccessoDatiKeystore",e);
			}

			ConfigurazionePdDReader.setAccessoDatiKeystore(tmp);
			ConfigurazionePdDReader.setAccessoDatiKeystoreLetto(true);
		}

		/**
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
	 * Restituisce le informazioni necessarie alla porta di dominio per accedere ai dati di gestione delle richieste
	 *
	 * @return informazioni
	 * 
	 */
	private static AccessoDatiRichieste accessoDatiRichieste = null;
	private static Boolean accessoDatiRichiesteLetto = false;
	public static void setAccessoDatiRichieste(AccessoDatiRichieste accessoDatiRichieste) {
		ConfigurazionePdDReader.accessoDatiRichieste = accessoDatiRichieste;
	}
	public static void setAccessoDatiRichiesteLetto(Boolean accessoDatiRichiesteLetto) {
		ConfigurazionePdDReader.accessoDatiRichiesteLetto = accessoDatiRichiesteLetto;
	}
	protected AccessoDatiRichieste getAccessoDatiRichieste(Connection connectionPdD){

		if( this.configurazioneDinamica || !ConfigurazionePdDReader.accessoDatiRichiesteLetto.booleanValue()){
			AccessoDatiRichieste tmp = null;
			try{
				tmp = this.configurazionePdD.getAccessoDatiRichieste(connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.logDebug("getAccessoDatiRichieste (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("getAccessoDatiRichieste",e);
			}

			ConfigurazionePdDReader.setAccessoDatiRichieste(tmp);
			ConfigurazionePdDReader.setAccessoDatiRichiesteLetto(true);
		}

		/**
		if(ConfigurazionePdDReader.accessoDatiRichieste.getCache()==null){
			System.out.println("ACCESSO_DATI_RICHIESTE CACHE DISABILITATA");
		}else{
			System.out.println("ACCESSO_DATI_RICHIESTE CACHE ABILITATA");
			System.out.println("ACCESSO_DATI_RICHIESTE CACHE ALGORITMO: "+ConfigurazionePdDReader.accessoDatiRichieste.getCache().getAlgoritmo());
			System.out.println("ACCESSO_DATI_RICHIESTE CACHE DIMENSIONE: "+ConfigurazionePdDReader.accessoDatiRichieste.getCache().getDimensione());
			System.out.println("ACCESSO_DATI_RICHIESTE CACHE ITEM IDLE: "+ConfigurazionePdDReader.accessoDatiRichieste.getCache().getItemIdleTime());
			System.out.println("ACCESSO_DATI_RICHIESTE CACHE ITEM LIFE SECOND: "+ConfigurazionePdDReader.accessoDatiRichieste.getCache().getItemLifeSecond());
			System.out.println("ACCESSO_DATI_RICHIESTE CACHE ITEM LIFE SECOND: "+ConfigurazionePdDReader.accessoDatiRichieste.getCrlItemLifeSecond());
		}
		 */

		return ConfigurazionePdDReader.accessoDatiRichieste;
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
				this.logDebug("getTipoValidazione (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("getTipoValidazione",e);
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
				this.logDebug("isLivelloValidazioneNormale (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("isLivelloValidazioneNormale",e);
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
				this.logDebug("isLivelloValidazioneRigido (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("isLivelloValidazioneRigido",e);
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
				this.logDebug("isValidazioneProfiloCollaborazione (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("isValidazioneProfiloCollaborazione",e);
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
				this.logDebug("isValidazioneManifestAttachments (not found): "+e.getMessage());
			}catch(Exception e){
				this.logDebug("isValidazioneManifestAttachments",e);
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
				this.logDebug("newConnectionForResponse (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("newConnectionForResponse",e);
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
				this.logDebug("isUtilizzoIndirizzoRisposta (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("isUtilizzoIndirizzoRisposta",e);
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
				this.logDebug("isGestioneManifestAttachments (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("isGestioneManifestAttachments",e);
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
				this.logDebug("getTimeoutRiscontro (not found): "+e.getMessage());
			}catch(Exception e){
				this.logError("getTimeoutRiscontro",e);
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
	private static Level livelloMessaggiDiagnostici = null;
	public static Level livelloMessaggiDiagnosticiJMX = null;
	protected Level getLivelloMessaggiDiagnostici(Connection connectionPdD){

		if(ConfigurazionePdDReader.livelloMessaggiDiagnosticiJMX!=null)
			return ConfigurazionePdDReader.livelloMessaggiDiagnosticiJMX;

		if( this.configurazioneDinamica || ConfigurazionePdDReader.livelloMessaggiDiagnostici==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);					
				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getLivelloMessaggiDiagnostici (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getLivelloMessaggiDiagnostici",e);
				}

				if(configurazione!=null && configurazione.getMessaggiDiagnostici()!=null){
					String readLevel = configurazione.getMessaggiDiagnostici().getSeverita().toString();	   
					ConfigurazionePdDReader.livelloMessaggiDiagnostici = LogLevels.toLog4J(readLevel);
				}else{
					ConfigurazionePdDReader.livelloMessaggiDiagnostici = LogLevels.LOG_LEVEL_INFO_PROTOCOL;
				}

			}catch(Exception e){
				ConfigurazionePdDReader.livelloMessaggiDiagnostici = LogLevels.LOG_LEVEL_INFO_PROTOCOL;
			}
		}

		return ConfigurazionePdDReader.livelloMessaggiDiagnostici;
	}

	/**
	 * Restituisce il Livello per il logger dei messaggi diagnostici in stile 'openspcoop'. 
	 *
	 * @return Il livello di Log4J per il logger dei messaggi diagnostici 'openspcoop' (di default ritorna OFF). 
	 * 
	 */
	private static Level livelloLog4JMessaggiDiagnostici = null;
	public static Level livelloLog4JMessaggiDiagnosticiJMX = null;
	protected Level getLivelloLog4JMessaggiDiagnostici(Connection connectionPdD){

		if(ConfigurazionePdDReader.livelloLog4JMessaggiDiagnosticiJMX!=null){
			return ConfigurazionePdDReader.livelloLog4JMessaggiDiagnosticiJMX;
		}

		if( this.configurazioneDinamica || ConfigurazionePdDReader.livelloLog4JMessaggiDiagnostici==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getLivelloLog4JMessaggiDiagnostici (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getLivelloLog4JMessaggiDiagnostici",e);
				}

				if(configurazione!=null && configurazione.getMessaggiDiagnostici()!=null){
					String readLevel =  configurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();	   
					ConfigurazionePdDReader.livelloLog4JMessaggiDiagnostici = LogLevels.toLog4J(readLevel);
				}else{
					ConfigurazionePdDReader.livelloLog4JMessaggiDiagnostici = LogLevels.LOG_LEVEL_INFO_PROTOCOL;
				}

			}catch(Exception e){
				ConfigurazionePdDReader.livelloLog4JMessaggiDiagnostici = LogLevels.LOG_LEVEL_INFO_PROTOCOL;
			}
		}

		return ConfigurazionePdDReader.livelloLog4JMessaggiDiagnostici;
	}

	/**
	 * Restituisce il Livello per il logger dei messaggi diagnostici . 
	 *
	 * @return Il livello per il logger dei messaggi diagnostici  (di default ritorna ALL). 
	 * 
	 */
	private static Integer severitaMessaggiDiagnostici = null;
	public static Integer severitaMessaggiDiagnosticiJMX = null;
	protected int getSeveritaMessaggiDiagnostici(Connection connectionPdD){

		if(ConfigurazionePdDReader.severitaMessaggiDiagnosticiJMX!=null){
			return ConfigurazionePdDReader.severitaMessaggiDiagnosticiJMX;
		}

		if( this.configurazioneDinamica || ConfigurazionePdDReader.severitaMessaggiDiagnostici==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);

				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getSeveritaMessaggiDiagnostici (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getSeveritaMessaggiDiagnostici",e);
				}

				if(configurazione!=null && configurazione.getMessaggiDiagnostici()!=null){
					String readLevel = configurazione.getMessaggiDiagnostici().getSeverita().toString();	   
					ConfigurazionePdDReader.severitaMessaggiDiagnostici = LogLevels.toOpenSPCoop2(readLevel);
				}else{
					ConfigurazionePdDReader.severitaMessaggiDiagnostici = LogLevels.SEVERITA_INFO_PROTOCOL;
				}

			}catch(Exception e){
				ConfigurazionePdDReader.severitaMessaggiDiagnostici = LogLevels.SEVERITA_INFO_PROTOCOL;
			}
		}

		return ConfigurazionePdDReader.severitaMessaggiDiagnostici;
	}

	/**
	 * Restituisce il Livello per il logger dei messaggi diagnostici che finiscono su log4j
	 *
	 * @return Il livello per il logger dei messaggi diagnostici che finiscono su log4j (di default ritorna OFF). 
	 * 
	 */
	private static Integer severitaLog4JMessaggiDiagnostici = null;
	public static Integer severitaLog4JMessaggiDiagnosticiJMX = null;
	public static Integer getSeveritaLog4JMessaggiDiagnosticiJMX() {
		return severitaLog4JMessaggiDiagnosticiJMX;
	}
	public static void setSeveritaLog4JMessaggiDiagnosticiJMX(
			Integer severitaLog4JMessaggiDiagnosticiJMX) {
		ConfigurazionePdDReader.severitaLog4JMessaggiDiagnosticiJMX = severitaLog4JMessaggiDiagnosticiJMX;
	}
	protected int getSeveritaLog4JMessaggiDiagnostici(Connection connectionPdD){

		if(ConfigurazionePdDReader.severitaLog4JMessaggiDiagnosticiJMX!=null){
			return ConfigurazionePdDReader.severitaLog4JMessaggiDiagnosticiJMX;
		}

		if( this.configurazioneDinamica || ConfigurazionePdDReader.severitaLog4JMessaggiDiagnostici==null){
			try{

				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getSeveritaLog4JMessaggiDiagnostici (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getSeveritaLog4JMessaggiDiagnostici",e);
				}

				if(configurazione!=null && configurazione.getMessaggiDiagnostici()!=null){
					String readLevel = configurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();	   
					ConfigurazionePdDReader.severitaLog4JMessaggiDiagnostici = LogLevels.toOpenSPCoop2(readLevel);
				}else{
					ConfigurazionePdDReader.severitaLog4JMessaggiDiagnostici = LogLevels.SEVERITA_INFO_PROTOCOL;
				}

			}catch(Exception e){
				ConfigurazionePdDReader.severitaLog4JMessaggiDiagnostici = LogLevels.SEVERITA_INFO_PROTOCOL;
			}
		}

		return ConfigurazionePdDReader.severitaLog4JMessaggiDiagnostici;
	}

	/**
	 * Restituisce gli appender personalizzati per la registrazione dei messaggi diagnostici. 
	 *
	 * @return Restituisce gli appender personalizzati per la registrazione dei messaggi diagnostici. 
	 */
	private static MessaggiDiagnostici openSPCoopAppenderMessaggiDiagnostici = null;
	private static Boolean openSPCoopAppenderMessaggiDiagnosticiLetto = false;
	protected MessaggiDiagnostici getOpenSPCoopAppenderMessaggiDiagnostici(Connection connectionPdD){

		if( this.configurazioneDinamica || !ConfigurazionePdDReader.openSPCoopAppenderMessaggiDiagnosticiLetto){
			try{

				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getOpenSPCoopAppenderMessaggiDiagnostici (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getOpenSPCoopAppenderMessaggiDiagnostici",e);
				}
				if(configurazione!=null)
					ConfigurazionePdDReader.openSPCoopAppenderMessaggiDiagnostici = configurazione.getMessaggiDiagnostici();
				else
					ConfigurazionePdDReader.openSPCoopAppenderMessaggiDiagnostici = null;

			}catch(Exception e){
				ConfigurazionePdDReader.openSPCoopAppenderMessaggiDiagnostici = null;
			}
			ConfigurazionePdDReader.openSPCoopAppenderMessaggiDiagnosticiLetto = true;
		}

		return ConfigurazionePdDReader.openSPCoopAppenderMessaggiDiagnostici;
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
					this.logDebug("tracciamentoBuste (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("tracciamentoBuste",e);
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
	private static Tracciamento openSPCoopAppenderTracciamento = null;
	private static Boolean openSPCoopAppenderTracciamentoLetto = false;
	protected Tracciamento getOpenSPCoopAppenderTracciamento(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.openSPCoopAppenderTracciamentoLetto==false){
			try{

				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getOpenSPCoopAppenderTracciamento (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getOpenSPCoopAppenderTracciamento",e);
				}

				if(configurazione!=null)
					ConfigurazionePdDReader.openSPCoopAppenderTracciamento = configurazione.getTracciamento();	   

			}catch(Exception e){
				ConfigurazionePdDReader.openSPCoopAppenderTracciamento = null;
			}
			ConfigurazionePdDReader.openSPCoopAppenderTracciamentoLetto = true;
		}

		return ConfigurazionePdDReader.openSPCoopAppenderTracciamento;
	}


	private static Transazioni transazioniConfigurazione = null;
	public Transazioni getTransazioniConfigurazione(Connection connectionPdD) {

		if( this.configurazioneDinamica || ConfigurazionePdDReader.transazioniConfigurazione==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getTransazioniConfigurazione (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getTransazioniConfigurazione",e);
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

	private static DumpConfigurazione dumpConfigurazionePortaApplicativa = null;
	public DumpConfigurazione getDumpConfigurazionePortaApplicativa(Connection connectionPdD) {

		if( this.configurazioneDinamica || ConfigurazionePdDReader.dumpConfigurazionePortaApplicativa==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getDumpConfigurazione (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getDumpConfigurazione",e);
				}

				if(configurazione!=null && configurazione.getDump()!=null){
					ConfigurazionePdDReader.dumpConfigurazionePortaApplicativa = configurazione.getDump().getConfigurazionePortaApplicativa();
				}else{
					ConfigurazionePdDReader.dumpConfigurazionePortaApplicativa = new DumpConfigurazione(); // default tutto abilitato
				}

			}catch(Exception e){
				ConfigurazionePdDReader.dumpConfigurazionePortaApplicativa = new DumpConfigurazione(); // default tutto abilitato
			}
		}

		return ConfigurazionePdDReader.dumpConfigurazionePortaApplicativa;

	}
	
	private static DumpConfigurazione dumpConfigurazionePortaDelegata = null;
	public DumpConfigurazione getDumpConfigurazionePortaDelegata(Connection connectionPdD) {

		if( this.configurazioneDinamica || ConfigurazionePdDReader.dumpConfigurazionePortaDelegata==null){
			try{
				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getDumpConfigurazione (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getDumpConfigurazione",e);
				}

				if(configurazione!=null && configurazione.getDump()!=null){
					ConfigurazionePdDReader.dumpConfigurazionePortaDelegata = configurazione.getDump().getConfigurazionePortaDelegata();
				}else{
					ConfigurazionePdDReader.dumpConfigurazionePortaDelegata = new DumpConfigurazione(); // default tutto abilitato
				}

			}catch(Exception e){
				ConfigurazionePdDReader.dumpConfigurazionePortaDelegata = new DumpConfigurazione(); // default tutto abilitato
			}
		}

		return ConfigurazionePdDReader.dumpConfigurazionePortaDelegata;

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
					this.logDebug("dumpBinarioPD (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("dumpBinarioPD",e);
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
					this.logDebug("dumpBinarioPA (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("dumpBinarioPA",e);
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
	private static Dump openSPCoopAppenderDump = null;
	private static Boolean openSPCoopAppenderDumpLetto = false;
	protected Dump getOpenSPCoopAppenderDump(Connection connectionPdD){

		if( this.configurazioneDinamica || ConfigurazionePdDReader.openSPCoopAppenderDumpLetto==false){
			try{

				Configurazione configurazione = null;
				try{
					configurazione = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getOpenSPCoopAppenderDump (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getOpenSPCoopAppenderDump",e);
				}

				if(configurazione!=null)
					ConfigurazionePdDReader.openSPCoopAppenderDump = configurazione.getDump();	   

			}catch(Exception e){
				ConfigurazionePdDReader.openSPCoopAppenderDump = null;
			}
			ConfigurazionePdDReader.openSPCoopAppenderDumpLetto = true;
		}

		return ConfigurazionePdDReader.openSPCoopAppenderDump;
	}

	/**
	 * Restituisce la politica di gestione della consegna tramite connettore per il componente di cooperazione
	 *
	 * @return la politica di gestione della consegna tramite connettore per il componente di cooperazione
	 * 
	 */
	private static HashMap<String, GestioneErrore> gestioneErroreConnettoreComponenteCooperazioneMap = new HashMap<>();
	private static org.openspcoop2.utils.Semaphore semaphore_erroreConnettoreCooperazione = new org.openspcoop2.utils.Semaphore("ConfigurazionePdDReader_erroreConnettoreCooperazione");
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
					this.logDebug("getGestioneErroreConnettoreComponenteCooperazione (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getGestioneErroreConnettoreComponenteCooperazione",e);
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
				//synchronized (ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazioneMap) {
				SemaphoreLock lock = semaphore_erroreConnettoreCooperazione.acquireThrowRuntime("getGestioneErroreConnettoreComponenteCooperazione");
				try {				
					if(!ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazioneMap.containsKey(key)) {
						ConfigurazionePdDReader.gestioneErroreConnettoreComponenteCooperazioneMap.put(key, gestione);
					}
				}finally {
					semaphore_erroreConnettoreCooperazione.release(lock, "getGestioneErroreConnettoreComponenteCooperazione");
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
	private static org.openspcoop2.utils.Semaphore semaphore_erroreConnettoreIntegrazione = new org.openspcoop2.utils.Semaphore("ConfigurazionePdDReader_erroreConnettoreIntegrazione");
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
					this.logDebug("getGestioneErroreConnettoreComponenteIntegrazione (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getGestioneErroreConnettoreComponenteIntegrazione",e);
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
				//synchronized (ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazioneMap) {
				SemaphoreLock lock = semaphore_erroreConnettoreIntegrazione.acquireThrowRuntime("getGestioneErroreConnettoreComponenteIntegrazione");
				try {
					if(!ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazioneMap.containsKey(key)) {
						ConfigurazionePdDReader.gestioneErroreConnettoreComponenteIntegrazioneMap.put(key, gestione);
					}
				}finally {
					semaphore_erroreConnettoreIntegrazione.release(lock, "getGestioneErroreConnettoreComponenteIntegrazione");
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
					this.logDebug("getIntegrationManagerAuthentication (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getIntegrationManagerAuthentication",e);
				}
				if(configurazione == null || configurazione.getIntegrationManager()==null || configurazione.getIntegrationManager().getAutenticazione()==null){
					ConfigurazionePdDReader.integrationManagerAuthentication = new String [] { CostantiConfigurazione.CREDENZIALE_BASIC.toString(),
							CostantiConfigurazione.CREDENZIALE_SSL.toString() };
				}else {

					String [] values =  configurazione.getIntegrationManager().getAutenticazione().split(",");
					List<String> v = new ArrayList<>();
					ClassNameProperties classNameProperties = ClassNameProperties.getInstance();
					for(int i=0; i<values.length; i++){
						values[i] = values[i].trim();
						if(classNameProperties.getAutenticazionePortaDelegata(values[i])==null){
							this.logError("Meccanismo di autenticazione ["+values[i]+"] non registrato in GovWay");
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
				this.logError("Errore durante la lettura del tipo di autenticazione associato al servizio di IntegrationManager: "+e.getMessage());
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
					this.logDebug("getTipoValidazioneContenutoApplicativo (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getTipoValidazioneContenutoApplicativo",e);
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
			}catch(Throwable e){
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
					this.logDebug("isPDServiceActive (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("isPDServiceActive",e);
				}
				if(stato == null || stato.getPortaDelegata()==null || stato.getPortaDelegata().getStato()==null){
					ConfigurazionePdDReader.isPDServiceActive = true;
				}else {					
					ConfigurazionePdDReader.isPDServiceActive = !CostantiConfigurazione.DISABILITATO.equals(stato.getPortaDelegata().getStato());
				}

			}catch(Exception e){
				this.logError("Errore durante la lettura dell'indicazione se il servizio porta delegata e' attivo: "+e.getMessage());
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
					this.logDebug("getFiltriAbilitazionePDService (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getFiltriAbilitazionePDService",e);
				}
				if(stato == null || stato.getPortaDelegata()==null || stato.getPortaDelegata().sizeFiltroAbilitazioneList()<=0){
					ConfigurazionePdDReader.getFiltriAbilitazionePDService = new ArrayList<TipoFiltroAbilitazioneServizi>();
				}else {					
					ConfigurazionePdDReader.getFiltriAbilitazionePDService = stato.getPortaDelegata().getFiltroAbilitazioneList();
				}

			}catch(Exception e){
				this.logError("Errore durante la raccolta dei filtri di abilitazione PD: "+e.getMessage());
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
					this.logDebug("getFiltriDisabilitazionePDService (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getFiltriDisabilitazionePDService",e);
				}
				if(stato == null || stato.getPortaDelegata()==null || stato.getPortaDelegata().sizeFiltroDisabilitazioneList()<=0){
					ConfigurazionePdDReader.getFiltriDisabilitazionePDService = new ArrayList<TipoFiltroAbilitazioneServizi>();
				}else {					
					ConfigurazionePdDReader.getFiltriDisabilitazionePDService = stato.getPortaDelegata().getFiltroDisabilitazioneList();
				}

			}catch(Exception e){
				this.logError("Errore durante la raccolta dei filtri di disabilitazione PD: "+e.getMessage());
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
					this.logDebug("isPAServiceActive (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("isPAServiceActive",e);
				}
				if(stato == null || stato.getPortaApplicativa()==null || stato.getPortaApplicativa().getStato()==null){
					ConfigurazionePdDReader.isPAServiceActive = true;
				}else {					
					ConfigurazionePdDReader.isPAServiceActive = !CostantiConfigurazione.DISABILITATO.equals(stato.getPortaApplicativa().getStato());
				}

			}catch(Exception e){
				this.logError("Errore durante la lettura dell'indicazione se il servizio porta applicativa e' attivo: "+e.getMessage());
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
					this.logDebug("getFiltriAbilitazionePAService (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getFiltriAbilitazionePAService",e);
				}
				if(stato == null || stato.getPortaApplicativa()==null || stato.getPortaApplicativa().sizeFiltroAbilitazioneList()<=0){
					ConfigurazionePdDReader.getFiltriAbilitazionePAService = new ArrayList<TipoFiltroAbilitazioneServizi>();
				}else {					
					ConfigurazionePdDReader.getFiltriAbilitazionePAService = stato.getPortaApplicativa().getFiltroAbilitazioneList();
				}

			}catch(Exception e){
				this.logError("Errore durante la raccolta dei filtri di abilitazione PA: "+e.getMessage());
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
					this.logDebug("getFiltriDisabilitazionePAService (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("getFiltriDisabilitazionePAService",e);
				}
				if(stato == null || stato.getPortaApplicativa()==null || stato.getPortaApplicativa().sizeFiltroDisabilitazioneList()<=0){
					ConfigurazionePdDReader.getFiltriDisabilitazionePAService = new ArrayList<TipoFiltroAbilitazioneServizi>();
				}else {					
					ConfigurazionePdDReader.getFiltriDisabilitazionePAService = stato.getPortaApplicativa().getFiltroDisabilitazioneList();
				}

			}catch(Exception e){
				this.logError("Errore durante la raccolta dei filtri di disabilitazione PA: "+e.getMessage());
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
					this.logDebug("isIMServiceActive (not found): "+e.getMessage());
				}catch(Exception e){
					this.logError("isIMServiceActive",e);
				}
				if(stato == null || stato.getIntegrationManager()==null || stato.getIntegrationManager().getStato()==null){
					ConfigurazionePdDReader.isIMServiceActive = true;
				}else {					
					ConfigurazionePdDReader.isIMServiceActive = !CostantiConfigurazione.DISABILITATO.equals(stato.getIntegrationManager().getStato());
				}

			}catch(Exception e){
				this.logError("Errore durante la lettura dell'indicazione se il servizio porta applicativa e' attivo: "+e.getMessage());
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
	
	protected PolicyAttributeAuthority getPolicyAttributeAuthority(Connection connectionPdD, boolean forceNoCache, String policyName) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{ 

		if(policyName==null){
			throw new DriverConfigurazioneException("Policy non fornita");
		}

		GenericProperties gp = this.configurazionePdD.getGenericProperties(connectionPdD, forceNoCache, org.openspcoop2.pdd.core.token.Costanti.ATTRIBUTE_AUTHORITY, policyName);

		PolicyAttributeAuthority policy = null;
		try {
			policy = AttributeAuthorityUtilities.convertTo(gp);
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

	protected SystemProperties getSystemPropertiesPdDCached(Connection connectionPdD) throws DriverConfigurazioneException{
		try{
			return this.configurazionePdD.getSystemPropertiesPdDCached(connectionPdD);
		}catch(DriverConfigurazioneNotFound dNot){
			return null;
		}
	}

	protected List<String> getEncryptedSystemPropertiesPdD() throws DriverConfigurazioneException{
		return this.configurazionePdD.getEncryptedSystemPropertiesPdD();
	}
	protected SystemProperties getSystemPropertiesPdD(boolean forceDisableBYOKUse) throws DriverConfigurazioneException{
		try{
			return this.configurazionePdD.getSystemPropertiesPdD(forceDisableBYOKUse);
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
			String idNodo = null;
			if(op2Prop.isClusterDinamico()) {
				idNodo = op2Prop.getGroupId(false);
			}
			else {
				idNodo = op2Prop.getClusterId(false);
			}
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
			
			List<String> canaliNodo = new ArrayList<>();
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
			AccordoServizioParteComune aspc,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		
		List<String> tags = new ArrayList<>();
		if(aspc!=null && aspc.getGruppi()!=null && aspc.getGruppi().sizeGruppoList()>0) {
			for (int i = 0; i < aspc.getGruppi().sizeGruppoList(); i++) {
				tags.add(aspc.getGruppi().getGruppo(i).getNome());
			}
		}
		
		String canaleApi = null;
		if(aspc!=null) {
			canaleApi = aspc.getCanale();
		}

		return getConfigurazioneUrlInvocazione(connectionPdD, protocolFactory, ruolo, serviceBinding, interfaceName, soggettoOperativo, tags, canaleApi, requestInfo);
	}	
	public UrlInvocazioneAPI getConfigurazioneUrlInvocazione(Connection connectionPdD, 
			IProtocolFactory<?> protocolFactory, RuoloContesto ruolo, ServiceBinding serviceBinding, String interfaceName, IDSoggetto soggettoOperativo,
			List<String> tags, 
			String canaleApi,
			RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{

		Configurazione config = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD);

		ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = null;
		if(config!=null && config.getUrlInvocazione()!=null) {
			configurazioneUrlInvocazione = config.getUrlInvocazione();
		}

		String canale = null;
		if(config!=null && config.getGestioneCanali()!=null && StatoFunzionalita.ABILITATO.equals(config.getGestioneCanali().getStato()) && config.getGestioneCanali().sizeCanaleList()>0) {
			if(RuoloContesto.PORTA_APPLICATIVA.equals(ruolo)) {
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(interfaceName);
				PortaApplicativa pa = null;
				if(requestInfo!=null && requestInfo.getRequestConfig()!=null && interfaceName!=null) {
					if( requestInfo.getRequestConfig().getPortaApplicativa()!=null && idPA.getNome().equals(requestInfo.getRequestConfig().getPortaApplicativa().getNome())) {
						pa = requestInfo.getRequestConfig().getPortaApplicativa();
					}
					else if( requestInfo.getRequestConfig().getPortaApplicativaDefault()!=null && idPA.getNome().equals(requestInfo.getRequestConfig().getPortaApplicativaDefault().getNome())) {
						pa = requestInfo.getRequestConfig().getPortaApplicativaDefault();
					}
				}
				if(pa==null) {
					try {
						pa = this.configurazionePdD.getPortaApplicativa(connectionPdD, idPA);
					}catch(DriverConfigurazioneNotFound notFound) {}
				}
				canale = CanaliUtils.getCanale(config.getGestioneCanali(), canaleApi, pa);
			}
			else {
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(interfaceName);
				PortaDelegata pd = null;
				if(requestInfo!=null && requestInfo.getRequestConfig()!=null && interfaceName!=null) {
					if( requestInfo.getRequestConfig().getPortaDelegata()!=null && idPD.getNome().equals(requestInfo.getRequestConfig().getPortaDelegata().getNome())) {
						pd = requestInfo.getRequestConfig().getPortaDelegata();
					}
					else if( requestInfo.getRequestConfig().getPortaDelegataDefault()!=null && idPD.getNome().equals(requestInfo.getRequestConfig().getPortaDelegata().getNome())) {
						pd = requestInfo.getRequestConfig().getPortaDelegata();
					}
				}
				if(pd==null) {
					try {
						pd = this.configurazionePdD.getPortaDelegata(connectionPdD, idPD);
					}catch(DriverConfigurazioneNotFound notFound) {}
				}
				canale = CanaliUtils.getCanale(config.getGestioneCanali(), canaleApi, pd);
			}
		}
		
		return UrlInvocazioneAPI.getConfigurazioneUrlInvocazione(configurazioneUrlInvocazione, protocolFactory, ruolo, serviceBinding, interfaceName, soggettoOperativo,
				tags, canale);
	}

	protected List<PolicyGroupByActiveThreadsType> getTipiGestoreRateLimiting(Connection connectionPdD) throws DriverConfigurazioneException {
		
		List<PolicyGroupByActiveThreadsType> list = new ArrayList<PolicyGroupByActiveThreadsType>();
		try {
			PolicyConfiguration pc = this.configurazionePdD.getConfigurazionePolicyRateLimitingGlobali(connectionPdD); 
			list.add(pc.getType()); // default
		}catch(Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		if(this.configurazionePdD.getDriverConfigurazionePdD() instanceof DriverConfigurazioneDB) {
			DriverConfigurazioneDB driver = (DriverConfigurazioneDB) this.configurazionePdD.getDriverConfigurazionePdD();
			
			List<String> listPD =  driver.porteDelegateRateLimitingValoriUnivoci(org.openspcoop2.core.controllo_traffico.constants.Costanti.GESTORE);
			if(listPD!=null && !listPD.isEmpty()) {
				for (String type : listPD) {
					PolicyGroupByActiveThreadsType typeE = PolicyGroupByActiveThreadsType.valueOf(type);
					if(!list.contains(typeE)) {
						list.add(typeE);
					}
				}
			}
			
			List<String> listPA =  driver.porteApplicativeRateLimitingValoriUnivoci(org.openspcoop2.core.controllo_traffico.constants.Costanti.GESTORE);
			if(listPA!=null && !listPA.isEmpty()) {
				for (String type : listPA) {
					PolicyGroupByActiveThreadsType typeE = null;
					// per backward
					if(org.openspcoop2.core.controllo_traffico.constants.Costanti.GESTORE_HAZELCAST_MAP_BACKWARD_COMPATIBILITY.equalsIgnoreCase(type)) {
						typeE = PolicyGroupByActiveThreadsType.HAZELCAST_MAP;
					}
					else {
						typeE = PolicyGroupByActiveThreadsType.valueOf(type);
					}
					if(!list.contains(typeE)) {
						list.add(typeE);
					}
				}
			}
		}
		
		return list;
	}
	
	protected List<String> getInitHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getService()!=null) {
			fillListHandlers(confGenerale.getService().getInitList(), list);
		}
		return list;
	}
	protected List<String> getExitHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getService()!=null) {
			fillListHandlers(confGenerale.getService().getExitList(), list);
		}
		return list;
	}
	protected List<String> getIntegrationManagerRequestHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getService()!=null) {
			fillListHandlers(confGenerale.getService().getIntegrationManagerRequestList(), list);
		}
		return list;
	}
	protected List<String> getIntegrationManagerResponseHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getService()!=null) {
			fillListHandlers(confGenerale.getService().getIntegrationManagerResponseList(), list);
		}
		return list;
	}
	
	protected List<String> getPreInRequestHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getRequest()!=null) {
			fillListHandlers(confGenerale.getRequest().getPreInList(), list);
		}
		return list;
	}
	protected List<String> getInRequestHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getRequest()!=null) {
			fillListHandlers(confGenerale.getRequest().getInList(), list);
		}
		return list;
	}
	protected List<String> getInRequestProtocolHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getRequest()!=null) {
			fillListHandlers(confGenerale.getRequest().getInProtocolInfoList(), list);
		}
		return list;
	}
	protected List<String> getOutRequestHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getRequest()!=null) {
			fillListHandlers(confGenerale.getRequest().getOutList(), list);
		}
		return list;
	}
	protected List<String> getPostOutRequestHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getRequest()!=null) {
			fillListHandlers(confGenerale.getRequest().getPostOutList(), list);
		}
		return list;
	}
	protected List<String> getPreInResponseHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getResponse()!=null) {
			fillListHandlers(confGenerale.getResponse().getPreInList(), list);
		}
		return list;
	}
	protected List<String> getInResponseHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getResponse()!=null) {
			fillListHandlers(confGenerale.getResponse().getInList(), list);
		}
		return list;
	}
	protected List<String> getOutResponseHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getResponse()!=null) {
			fillListHandlers(confGenerale.getResponse().getOutList(), list);
		}
		return list;
	}
	protected List<String> getPostOutResponseHandlers(Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		ConfigurazioneGeneraleHandler confGenerale = this.configurazionePdD.getConfigurazioneGenerale(connectionPdD).getConfigurazioneHandler();
		List<String> list = new ArrayList<>();
		if(confGenerale!=null && confGenerale.getResponse()!=null) {
			fillListHandlers(confGenerale.getResponse().getPostOutList(), list);
		}
		return list;
	}

	private void fillListHandlers(List<ConfigurazioneHandler> handlers, List<String> list) {
		if(handlers!=null && !handlers.isEmpty()) {
			for (ConfigurazioneHandler handler : handlers) {
				if(StatoFunzionalita.ABILITATO.equals(handler.getStato())){
					list.add(handler.getTipo());
				}
			}
		}
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
					this.logDebug("getExtendedInfoConfigurazione (not found): "+e.getMessage());
				}
				//				}catch(Exception e){
				//					this.logError("getExtendedInfoConfigurazione",e);
				//				}
				if(configurazione!=null) {
					ConfigurazionePdDReader.getExtendedInfoConfigurazione = configurazione.getExtendedInfoList();
				}

			}catch(Exception e){
				this.logError("Errore durante la lettura delle informazioni extra della configurazione: "+e.getMessage(),e);
				throw new DriverConfigurazioneException("Errore durante la lettura delle informazioni extra della configurazione: "+e.getMessage(),e);
			}
		}

		return ConfigurazionePdDReader.getExtendedInfoConfigurazione;

	}

	private static Map<String, Object> getSingleExtendedInfoConfigurazione = new ConcurrentHashMap<String, Object>(); 
	protected Object getSingleExtendedInfoConfigurazione(String id, Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		if( this.configurazioneDinamica || ConfigurazionePdDReader.getSingleExtendedInfoConfigurazione.containsKey(id)==false){
			try{
				Object result = null;
				try{
					result = this.configurazionePdD.getSingleExtendedInfoConfigurazione(id, connectionPdD);
				}catch(DriverConfigurazioneNotFound e){
					this.logDebug("getSingleExtendedInfoConfigurazione (not found): "+e.getMessage());
				}
				//				}catch(Exception e){
				//					this.logError("getExtendedInfoConfigurazione",e);
				//				}

				if(result!=null){
					ConfigurazionePdDReader.getSingleExtendedInfoConfigurazione.put(id, result);
				}

			}catch(Exception e){
				this.logError("Errore durante la lettura delle informazioni extra con id '"+id+"' della configurazione: "+e.getMessage(),e);
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
				this.logDebug("getConfigurazioneWithOnlyExtendedInfo (not found): "+e.getMessage());
			}
			//			}catch(Exception e){
			//				this.logError("getExtendedInfoConfigurazione",e);
			//			}
			if(configurazione!=null) {
				return configurazione.getExtendedInfoList();
			}
			return null;

		}catch(Exception e){
			this.logError("Errore durante la lettura delle informazioni extra della configurazione (via cache): "+e.getMessage(),e);
			throw new DriverConfigurazioneException("Errore durante la lettura delle informazioni extra della configurazione  (via cache): "+e.getMessage(),e);
		}
	}

	protected Object getSingleExtendedInfoConfigurazioneFromCache(String id, Connection connectionPdD) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{
		try{
			Object result = null;
			try{
				result = this.configurazionePdD.getSingleExtendedInfoConfigurazione(id, connectionPdD);
			}catch(DriverConfigurazioneNotFound e){
				this.logDebug("getSingleExtendedInfoConfigurazioneFromCache (not found): "+e.getMessage());
			}
			//			}catch(Exception e){
			//				this.logError("getExtendedInfoConfigurazione",e);
			//			}
			return result;

		}catch(Exception e){
			this.logError("Errore durante la lettura delle informazioni extra con id '"+id+"' della configurazione (via cache): "+e.getMessage(),e);
			throw new DriverConfigurazioneException("Errore durante la lettura delle informazioni extra con id '"+id+"' della configurazione  (via cache): "+e.getMessage(),e);
		}
	}

	protected Template getTemplatePolicyNegoziazioneRequest(Connection connectionPdD, String policyName, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplatePolicyNegoziazioneRequest(connectionPdD, policyName, template, requestInfo);
	}
	
	protected Template getTemplateAttributeAuthorityRequest(Connection connectionPdD, String attributeAuthorityName, byte[] template, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateAttributeAuthorityRequest(connectionPdD, attributeAuthorityName, template, requestInfo);
	}
	
	protected Template getTemplateIntegrazione(Connection connectionPdD,File file, RequestInfo requestInfo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getTemplateIntegrazione(connectionPdD, file, requestInfo);
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

	public PolicyConfiguration getConfigurazionePolicyRateLimitingGlobali(Connection connectionPdD) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getConfigurazionePolicyRateLimitingGlobali(connectionPdD);
	}
	
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveAPI(Connection connectionPdD, boolean useCache, TipoPdD tipoPdD, String nomePorta) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getElencoIdPolicyAttiveAPI(connectionPdD, useCache, tipoPdD, nomePorta);
	}

	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveGlobali(Connection connectionPdD, boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getElencoIdPolicyAttiveGlobali(connectionPdD, useCache);
	}
	
	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveAPIDimensioneMessaggio(Connection connectionPdD, boolean useCache, TipoPdD tipoPdD, String nomePorta) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getElencoIdPolicyAttiveAPIDimensioneMessaggio(connectionPdD, useCache, tipoPdD, nomePorta);
	}

	public Map<TipoRisorsaPolicyAttiva, ElencoIdPolicyAttive> getElencoIdPolicyAttiveGlobaliDimensioneMessaggio(Connection connectionPdD, boolean useCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getElencoIdPolicyAttiveGlobaliDimensioneMessaggio(connectionPdD, useCache);
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
	
	
	/* ******** PLUGINS ******** */
	
	public IRegistroPluginsReader getRegistroPluginsReader() {
		return this.configurazionePdD.getRegistroPluginsReader();
	}
	
	public String getPluginClassName(Connection connectionPdD, String tipoPlugin, String tipo) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPluginClassName(connectionPdD, tipoPlugin, tipo);
	}
	public String getPluginClassNameByFilter(Connection connectionPdD, String tipoPlugin, String tipo, NameValue ... filtri) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPluginClassNameByFilter(connectionPdD, tipoPlugin, tipo, filtri);
	}
	public String getPluginTipo(Connection connectionPdD, String tipoPlugin, String className) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPluginTipo(connectionPdD, tipoPlugin, className);
	}
	public String getPluginTipoByFilter(Connection connectionPdD, String tipoPlugin, String className, NameValue ... filtri) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return this.configurazionePdD.getPluginTipoByFilter(connectionPdD, tipoPlugin, className, filtri);
	}
	
	
	/* ******** ALLARMI ******** */

	public Allarme getAllarme(Connection connectionPdD, String nomeAllarme, boolean searchInCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdD.getAllarme(connectionPdD, nomeAllarme, searchInCache);
	}
	public List<Allarme> searchAllarmi(Connection connectionPdD, FiltroRicercaAllarmi filtroRicerca, boolean searchInCache) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return this.configurazionePdD.searchAllarmi(connectionPdD, filtroRicerca, searchInCache);
	}
	public List<IAlarm> instanceAllarmi(Connection connectionPdD, List<Allarme> listAllarmi) throws DriverConfigurazioneException {
		return this.configurazionePdD.instanceAllarmi(connectionPdD, listAllarmi);
	}
	public void changeStatus(Connection connectionPdD, IAlarm alarm, AlarmStatus nuovoStatoAllarme) throws DriverConfigurazioneException {
		this.configurazionePdD.changeStatus(connectionPdD, alarm, nuovoStatoAllarme);
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
	public ForwardProxy getForwardProxyConfigFruizione(IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy, RequestInfo requestInfo) throws DriverConfigurazioneException{
		return this.configurazionePdD.getForwardProxyConfigFruizione(dominio, idServizio, policy, requestInfo);
	}
	public ForwardProxy getForwardProxyConfigErogazione(IDSoggetto dominio, IDServizio idServizio, IDGenericProperties policy, RequestInfo requestInfo) throws DriverConfigurazioneException{
		return this.configurazionePdD.getForwardProxyConfigErogazione(dominio, idServizio, policy, requestInfo);
	}
	
	
	/* ********  GENERIC FILE  ******** */

	public ContentFile getContentFile(File file) throws DriverConfigurazioneException{
		return this.configurazionePdD.getContentFile(file);
	}

}
