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


package org.openspcoop2.web.ctrlstat.core;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.allarmi.Allarme;
import org.openspcoop2.core.allarmi.AllarmeHistory;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.AttributeAuthority;
import org.openspcoop2.core.config.CanaliConfigurazione;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.Credenziali;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.OpenspcoopSorgenteDati;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizio;
import org.openspcoop2.core.config.PortaApplicativaSoggettoVirtuale;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.config.utils.ConfigUtils;
import org.openspcoop2.core.config.utils.UpdateProprietaOggetto;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mvc.properties.Config;
import org.openspcoop2.core.mvc.properties.provider.ExternalResources;
import org.openspcoop2.core.mvc.properties.provider.ProviderException;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.FormatoSpecifica;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.core.registry.driver.IDAccordoCooperazioneFactory;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.registry.driver.db.DriverRegistroServiziDB;
import org.openspcoop2.core.registry.driver.db.IDAccordoDB;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.alarm.AlarmConfigProperties;
import org.openspcoop2.monitor.engine.alarm.AlarmEngineConfig;
import org.openspcoop2.monitor.engine.dynamic.CorePluginLoader;
import org.openspcoop2.monitor.engine.dynamic.PluginLoader;
import org.openspcoop2.pdd.config.ConfigurazioneNodiRuntime;
import org.openspcoop2.pdd.config.ConfigurazionePriorita;
import org.openspcoop2.pdd.config.InvokerNodiRuntime;
import org.openspcoop2.pdd.config.OpenSPCoop2ConfigurationException;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneBasic;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazionePrincipal;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.pdd.core.keystore.RemoteStoreKeyEntry;
import org.openspcoop2.pdd.core.keystore.RemoteStoreProviderDriverUtils;
import org.openspcoop2.pdd.logger.DriverMsgDiagnostici;
import org.openspcoop2.pdd.logger.DriverTracciamento;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.AzioniUtils;
import org.openspcoop2.protocol.engine.utils.NamingUtils;
import org.openspcoop2.protocol.manifest.constants.InterfaceType;
import org.openspcoop2.protocol.sdk.ConfigurazionePdD;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.FunzionalitaProtocollo;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.registry.IConfigIntegrationReader;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.utils.ProtocolUtils;
import org.openspcoop2.utils.IVersionInfo;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Semaphore;
import org.openspcoop2.utils.SortedMap;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.VersionUtilities;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.CryptType;
import org.openspcoop2.utils.crypt.ICrypt;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.config.DatasourceProperties;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.MultitenantSoggettiErogazioni;
import org.openspcoop2.web.ctrlstat.costanti.MultitenantSoggettiFruizioni;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.ctrlstat.driver.IDBuilder;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedCoreServlet;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedFormServlet;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedMenu;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.registro.GestoreRegistroServiziRemoto;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.AccordiServizioParteComuneCore;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCostanti;
import org.openspcoop2.web.ctrlstat.servlet.pdd.PddCore;
import org.openspcoop2.web.ctrlstat.servlet.soggetti.SoggettiCore;
import org.openspcoop2.web.lib.audit.DriverAudit;
import org.openspcoop2.web.lib.audit.appender.AuditAppender;
import org.openspcoop2.web.lib.audit.appender.AuditDBAppender;
import org.openspcoop2.web.lib.audit.appender.AuditDisabilitatoException;
import org.openspcoop2.web.lib.audit.appender.IDOperazione;
import org.openspcoop2.web.lib.audit.dao.Appender;
import org.openspcoop2.web.lib.audit.dao.AppenderProperty;
import org.openspcoop2.web.lib.audit.dao.Filtro;
import org.openspcoop2.web.lib.audit.log.constants.Stato;
import org.openspcoop2.web.lib.audit.log.constants.Tipologia;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.properties.beans.ConfigBean;
import org.openspcoop2.web.lib.mvc.properties.exception.ValidationException;
import org.openspcoop2.web.lib.mvc.properties.utils.ReadPropertiesUtilities;
import org.openspcoop2.web.lib.users.dao.User;
import org.slf4j.Logger;

/**
 * Questa classe e' l'entrypoint alla govwayConsole, fornisce cioe' ai
 * chiamanti la possibilita' di effettuare le operazioni che verranno riflesse
 * sia in locale che in remoto.
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ControlStationCore {

	/* ------- STATIC ----- */

	/** Logger */
	protected static Logger log = null;
	public static Logger getLog() {
		return ControlStationCore.log;
	}

	/** DBManager */
	protected static DBManager dbM = null;


	/* ---- STATIC INIT METHOD ----- */

	private static synchronized void initLogger(){
		// Log4J caricato tramite InitListener
		ControlStationCore.log = InitListener.log;
	}
	private static void checkInitLogger(){
		if(ControlStationCore.log==null){
			ControlStationCore.initLogger();
		}
	}

	/* ---- STATIC LOGGER ----  */

	public static void logInfo(String msg){
		ControlStationCore.checkInitLogger();
		ControlStationCore.log.info(msg);
	}
	public static void logWarn(String msg){
		ControlStationCore.checkInitLogger();
		ControlStationCore.log.warn(msg);
	}
	public static void logDebug(String msg){
		ControlStationCore.checkInitLogger();
		ControlStationCore.log.debug(msg);
	}
	public static void logDebug(String msg,Throwable e){
		ControlStationCore.checkInitLogger();
		ControlStationCore.log.debug(msg,e);
	}
	public static void logError(String msg){
		ControlStationCore.checkInitLogger();
		ControlStationCore.log.error(msg);
	}
	public static void logError(String msg,Throwable e){
		ControlStationCore.checkInitLogger();
		ControlStationCore.log.error(msg,e);
	}

	
	protected String getPrefixMethod(String nomeMetodo) {
		return "[ControlStationCore::" + nomeMetodo + "] ";
	}
	protected String getPrefixError(String nomeMetodo, Exception e) {
		return getPrefixMethod(nomeMetodo)+"Exception:" + e.getMessage();
	}
	protected String getPrefixNotFound(String nomeMetodo, Exception e) {
		return getPrefixMethod(nomeMetodo)+"NotFound:" + e.getMessage();
	}
	
	


	/* ------- INFO ----- */
	
	



	/* ------- VARIABLE ----- */

	/** Impostazioni grafiche */
	private String consoleNomeSintesi = null;
	private String consoleNomeEsteso = null;
	private String consoleCSS = null;
	private String consoleLanguage = null;
	private int consoleLunghezzaLabel = 50;
	private String logoHeaderImage = null;
	private String logoHeaderTitolo = null;
	private String logoHeaderLink = null;
	private boolean visualizzaLinkHomeHeader = false;
	private AffineTransform affineTransform = null;
	private FontRenderContext fontRenderContext = null;
	private Font defaultFont = null;
	
	private String getTitleSuffix(HttpServletRequest request, HttpSession session) {
		IVersionInfo versionInfoCheck = null;
		try {
			versionInfoCheck = getInfoVersion(request, session);
		}catch(Exception e) {
			ControlStationLogger.getPddConsoleCoreLogger().error("Errore durante la lettura delle informazioni sulla versione: "+e.getMessage(),e);
		}
		String consoleNomeEstesoSuffix = null;
		if(versionInfoCheck!=null) {
			if(!StringUtils.isEmpty(versionInfoCheck.getErrorTitleSuffix())) {
				consoleNomeEstesoSuffix = versionInfoCheck.getErrorTitleSuffix();
			}
			else if(!StringUtils.isEmpty(versionInfoCheck.getWarningTitleSuffix())) {
				consoleNomeEstesoSuffix = versionInfoCheck.getWarningTitleSuffix();
			}
		}
		return consoleNomeEstesoSuffix;
	}
	
	public String getConsoleNomeSintesi() {
		return this.consoleNomeSintesi;
	}
	public String getConsoleNomeEsteso(HttpServletRequest request, HttpSession session) {
		String titleSuffix = getTitleSuffix(request, session);
		if(!StringUtils.isEmpty(titleSuffix)){
			if(!titleSuffix.startsWith(" ")) {
				titleSuffix = " "+titleSuffix;
			}
			return this.consoleNomeEsteso+titleSuffix;
		}
		else{
			return this.consoleNomeEsteso;
		}
	}
	public String getProductVersion(){
		String pVersion = null;
		pVersion = "GovWay "+CostantiPdD.OPENSPCOOP2_VERSION;
		
		try {
			String version = VersionUtilities.readVersion();
			if(version!=null && !StringUtils.isEmpty(version)) {
				pVersion = version;
			}
		}catch(Exception e) {
			// ignore
		}
		
		String buildVersion = null;
		try {
			buildVersion = VersionUtilities.readBuildVersion();
		}catch(Exception e) {
			// ignore
		}
		if(buildVersion!=null) {
			pVersion = pVersion + " (build "+buildVersion+")";
		}
		
		return pVersion;
	}
	public String getConsoleCSS() {
		return this.consoleCSS;
	}
	public String getConsoleLanguage() {
		return this.consoleLanguage;
	}

	public int getConsoleLunghezzaLabel() {
		return this.consoleLunghezzaLabel;
	}
	
	public String getLogoHeaderImage() {
		return this.logoHeaderImage;
	}

	public String getLogoHeaderTitolo() {
		return this.logoHeaderTitolo;
	}

	public String getLogoHeaderLink() {
		return this.logoHeaderLink;
	}
	
	public boolean isVisualizzaLinkHomeHeader() {
		return this.visualizzaLinkHomeHeader;
	}

	/** Tipo del Database */
	protected String tipoDB = "";
	public String getTipoDatabase() {
		return this.tipoDB;
	}

	/** IDFactory */
	private IDAccordoFactory idAccordoFactory = null;
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = null;
	private IDServizioFactory idServizioFactory = null;

	/** Protocollo */
	private String protocolloDefault = null;
	private long jdbcSerializableAttesaAttiva = 0;
	private int jdbcSerializableCheck = 0;
	public String getProtocolloDefault(HttpServletRequest request, HttpSession session, List<String> listaProtocolliUtilizzabili) throws DriverRegistroServiziException {
		if(listaProtocolliUtilizzabili!=null && !listaProtocolliUtilizzabili.isEmpty()) {
			// cerco prima il default
			for (String protocolloUtilizzabile : listaProtocolliUtilizzabili) {
				if(protocolloUtilizzabile.equals(this.protocolloDefault)) {
					return this.protocolloDefault;
				}
			}
			return listaProtocolliUtilizzabili.get(0); // torno il primo
		}
		List<String> protocolli = this.getProtocolli(request, session);
		if(protocolli!=null && protocolli.size()==1) {
			return protocolli.get(0); // si tratta del protocollo selezionato se ce ne sono piu' di uno
		}
		return this.protocolloDefault;
	}
	public long getJdbcSerializableAttesaAttiva() {
		return this.jdbcSerializableAttesaAttiva;
	}
	public int getJdbcSerializableCheck() {
		return this.jdbcSerializableCheck;
	}
	protected ProtocolFactoryManager protocolFactoryManager = null;

	/** Visione oggetti globale o per utenti */
	private boolean visioneOggettiGlobale = true;
	private List<String> utentiConVisioneGlobale = new ArrayList<>();
	public boolean isVisioneOggettiGlobale(String user) {
		if(this.visioneOggettiGlobale){
			return true;
		}else{
			return this.utentiConVisioneGlobale.contains(user);
		}
	}
	public boolean isVisioneOggettiGlobaleIndipendenteUtente() {
		return this.visioneOggettiGlobale;
	}

	/** Tracciamento */
	private boolean tracceShowConfigurazioneCustomAppender = false;
	private boolean tracceSameDBWebUI = true;
	private boolean tracceShowSorgentiDatiDatabase = false;
	private String tracceDatasource = null;
	private String tracceTipoDatabase = null;
	private Properties tracceCtxDatasource = null;
	private DriverTracciamento driverTracciamento = null;
	public DriverTracciamento getDriverTracciamento()  throws DriverControlStationException {
		return this.getDriverTracciamento(null, false);
	}
	public DriverTracciamento getDriverTracciamento(String nomeDs)  throws DriverControlStationException {
		return this.getDriverTracciamento(nomeDs, false);
	}
	public DriverTracciamento getDriverTracciamento(String nomeDs, boolean forceChange)  throws DriverControlStationException {
		if(this.driverTracciamento==null || forceChange){
			initDriverTracciamento(nomeDs, forceChange);
		}
		return this.driverTracciamento;
	}
	public boolean isTracceShowConfigurazioneCustomAppender() {
		return this.tracceShowConfigurazioneCustomAppender;
	}
	public boolean isTracceShowSorgentiDatiDatabase() {
		return this.tracceShowSorgentiDatiDatabase;
	}
	public boolean isTracceSameDBWebUI() {
		return this.tracceSameDBWebUI;
	}

	/** MsgDiagnostici */
	private boolean msgDiagnosticiShowConfigurazioneCustomAppender = false;
	private boolean msgDiagnosticiSameDBWebUI = true;
	private boolean msgDiagnosticiShowSorgentiDatiDatabase = false;
	private String msgDiagnosticiDatasource = null;
	private String msgDiagnosticiTipoDatabase = null;
	private Properties msgDiagnosticiCtxDatasource = null;
	private DriverMsgDiagnostici driverMSGDiagnostici = null;
	public DriverMsgDiagnostici getDriverMSGDiagnostici()  throws DriverControlStationException {
		return this.getDriverMSGDiagnostici(null, false);
	}
	public DriverMsgDiagnostici getDriverMSGDiagnostici(String nomeDs)  throws DriverControlStationException {
		return this.getDriverMSGDiagnostici(nomeDs, false);
	}
	public DriverMsgDiagnostici getDriverMSGDiagnostici(String nomeDs, boolean forceChange)  throws DriverControlStationException {
		if(this.driverMSGDiagnostici==null || forceChange){
			initDriverMSGDiagnostici(nomeDs, forceChange);
		}
		return this.driverMSGDiagnostici;
	}
	public boolean isMsgDiagnosticiShowConfigurazioneCustomAppender() {
		return this.msgDiagnosticiShowConfigurazioneCustomAppender;
	}
	public boolean isMsgDiagnosticiShowSorgentiDatiDatabase() {
		return this.msgDiagnosticiShowSorgentiDatiDatabase;
	}
	
	/** Dump */
	
	private boolean dumpShowConfigurazioneCustomAppender = false;
	public boolean isDumpShowConfigurazioneCustomAppender() {
		return this.dumpShowConfigurazioneCustomAppender;
	}
	
	private boolean dumpShowConfigurazioneDumpRealtime = true;
	public boolean isDumpShowConfigurazioneDumpRealtime() {
		return this.dumpShowConfigurazioneDumpRealtime;
	}

	/** Porte di Dominio */
	private boolean gestionePddAbilitata = true;
	public boolean isGestionePddAbilitata(ConsoleHelper consoleHelper) {
		return this.singlePdD && this.registroServiziLocale && (this.gestionePddAbilitata || consoleHelper.isModalitaCompleta());
	}
	
	/** Registro Servizi */
	private boolean registroServiziLocale = true;
	public boolean isRegistroServiziLocale() {
		return this.registroServiziLocale;
	}

	/** Modalita' Single PdD */
	private boolean singlePdD;
	public boolean isSinglePdD() {
		return this.singlePdD;
	}

	/** J2EE Ambiente */
	private boolean showJ2eeOptions = true;
	public boolean isShowJ2eeOptions() {
		return this.showJ2eeOptions;
	}

	/** Utenze Console */
	
	private static CryptConfig utenzePasswordEncryptEngineApiMode = null;
	public static CryptConfig getUtenzePasswordEncryptEngineApiMode() {
		return utenzePasswordEncryptEngineApiMode;
	}
	public static void setUtenzePasswordEncryptEngineApiMode(CryptConfig utenzePasswordEncryptEngineApiMode) {
		ControlStationCore.utenzePasswordEncryptEngineApiMode = utenzePasswordEncryptEngineApiMode;
	}

	private String utenzePasswordConfiguration = null;
	public String getUtenzePasswordConfiguration() {
		return this.utenzePasswordConfiguration;
	}
	private PasswordVerifier utenzePasswordVerifierEngine = null;
	private CryptConfig utenzePasswordEncryptEngine = null;
	private synchronized void initUtenzePassword() throws UtilsException {
		if(this.utenzePasswordVerifierEngine==null){
			this.utenzePasswordVerifierEngine = new PasswordVerifier(this.utenzePasswordConfiguration); 
		}
		if(this.utenzePasswordEncryptEngine==null){
			this.utenzePasswordEncryptEngine = new CryptConfig(this.utenzePasswordConfiguration); 
		}
	}
	public PasswordVerifier getUtenzePasswordVerifier() {
		if(this.utenzePasswordConfiguration!=null){
			if(this.utenzePasswordVerifierEngine==null){
				try{
					this.initUtenzePassword();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
			if(this.utenzePasswordVerifierEngine!=null && this.utenzePasswordVerifierEngine.existsRestriction()){
				return this.utenzePasswordVerifierEngine;
			}
		}
		return null;
	}
	public CryptConfig getUtenzePasswordEncrypt() {
		if(this.utenzePasswordConfiguration!=null){
			if(this.utenzePasswordEncryptEngine==null){
				try{
					this.initUtenzePassword();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
			return this.utenzePasswordEncryptEngine;
		}
		return null;
	}
	public boolean isUtenzePasswordEncryptEnabled() {
		return this.getUtenzePasswordEncrypt()!=null && !CryptType.PLAIN.equals(this.getUtenzePasswordEncrypt().getCryptType());
	}
	private int utenzeLunghezzaPasswordGenerate;
	public int getUtenzeLunghezzaPasswordGenerate() {
		return this.utenzeLunghezzaPasswordGenerate;
	}
	
	protected ICrypt utenzePasswordManager;
	protected ICrypt utenzePasswordManagerBackwardCompatibility;
	public ICrypt getUtenzePasswordManager() {
		return this.utenzePasswordManager;
	}
	public ICrypt getUtenzePasswordManagerBackwardCompatibility() {
		return this.utenzePasswordManagerBackwardCompatibility;
	}
	
	public boolean isCheckPasswordExpire(PasswordVerifier passwordVerifier) { 
		if(passwordVerifier != null) {
			return this.isLoginApplication() && passwordVerifier.isCheckPasswordExpire();
		}
		
		return false;
	}
	
	private boolean utenzeModificaProfiloUtenteDaLinkAggiornaDB;
	public boolean isUtenzeModificaProfiloUtenteDaLinkAggiornaDB() {
		return this.utenzeModificaProfiloUtenteDaLinkAggiornaDB;
	}
	private boolean utenzeModificaProfiloUtenteDaFormAggiornaSessione;
	public boolean isUtenzeModificaProfiloUtenteDaFormAggiornaSessione() {
		return this.utenzeModificaProfiloUtenteDaFormAggiornaSessione;
	}
	
	/** Login */
	protected String loginTipo;
	protected boolean loginApplication;
	protected Properties loginProperties;
	protected String loginUtenteNonAutorizzatoRedirectUrl;
	protected String loginUtenteNonValidoRedirectUrl;
	protected String loginErroreInternoRedirectUrl;
	protected String loginSessioneScadutaRedirectUrl;
	protected boolean logoutMostraButton;
	protected String logoutUrlDestinazione;
	
	
	public String getLoginTipo() {
		return this.loginTipo;
	}

	public boolean isLoginApplication() {
		return this.loginApplication;
	}

	public Properties getLoginProperties() {
		return this.loginProperties;
	}
	
	public String getLoginUtenteNonAutorizzatoRedirectUrl() {
		return this.loginUtenteNonAutorizzatoRedirectUrl;
	}

	public String getLoginUtenteNonValidoRedirectUrl() {
		return this.loginUtenteNonValidoRedirectUrl;
	}
	
	public String getLoginErroreInternoRedirectUrl() {
		return this.loginErroreInternoRedirectUrl;
	}

	public String getLoginSessioneScadutaRedirectUrl() {
		return this.loginSessioneScadutaRedirectUrl;
	}

	public boolean isMostraButtonLogout() {
		return this.logoutMostraButton;
	}

	public String getLogoutUrlDestinazione() {
		return this.logoutUrlDestinazione;
	}
	
	
	/** Applicativi */
	
	private static CryptConfig applicativiPasswordEncryptEngineApiMode = null;
	public static CryptConfig getApplicativiPasswordEncryptEngineApiMode() {
		return applicativiPasswordEncryptEngineApiMode;
	}
	public static void setApplicativiPasswordEncryptEngineApiMode(CryptConfig applicativiPasswordEncryptEngineApiMode) {
		ControlStationCore.applicativiPasswordEncryptEngineApiMode = applicativiPasswordEncryptEngineApiMode;
	}
	private static Integer applicativiApiKeyPasswordGeneratedLengthApiMode = null;
	public static Integer getApplicativiApiKeyPasswordGeneratedLengthApiMode() {
		return applicativiApiKeyPasswordGeneratedLengthApiMode;
	}
	public static void setApplicativiApiKeyPasswordGeneratedLengthApiMode(Integer applicativiApiKeyPasswordGeneratedLengthApiMode) {
		ControlStationCore.applicativiApiKeyPasswordGeneratedLengthApiMode = applicativiApiKeyPasswordGeneratedLengthApiMode;
	}
	private static PasswordVerifier applicativiPasswordVerifierEngineApiMode = null;
	public static PasswordVerifier getApplicativiPasswordVerifierEngineApiMode() {
		return applicativiPasswordVerifierEngineApiMode;
	}
	public static void setApplicativiPasswordVerifierEngineApiMode(
			PasswordVerifier applicativiPasswordVerifierEngineApiMode) {
		ControlStationCore.applicativiPasswordVerifierEngineApiMode = applicativiPasswordVerifierEngineApiMode;
	}

	private String applicativiPwConfiguration = null;
	public String getApplicativiPasswordConfiguration() {
		return this.applicativiPwConfiguration;
	}
	private boolean applicativiBasicPwEnableConstraints = false;
	public boolean isApplicativiBasicPasswordEnableConstraints() {
		return this.applicativiBasicPwEnableConstraints;
	}
	private PasswordVerifier applicativiPwVerifierEngine = null;
	private CryptConfig applicativiPwEncryptEngine = null;
	private synchronized void initApplicativiPassword() throws UtilsException {
		if(this.applicativiBasicPwEnableConstraints && this.applicativiPwVerifierEngine==null){
			this.applicativiPwVerifierEngine = new PasswordVerifier(this.applicativiPwConfiguration); 
		}
		if(this.applicativiPwEncryptEngine==null){
			this.applicativiPwEncryptEngine = new CryptConfig(this.applicativiPwConfiguration); 
		}
	}
	public PasswordVerifier getApplicativiPasswordVerifier() {
		if(this.applicativiBasicPwEnableConstraints && this.applicativiPwConfiguration!=null){
			if(this.applicativiPwVerifierEngine==null){
				try{
					this.initApplicativiPassword();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
			if(this.applicativiPwVerifierEngine!=null && this.applicativiPwVerifierEngine.existsRestriction()){
				return this.applicativiPwVerifierEngine;
			}
		}
		return null;
	}
	public CryptConfig getApplicativiPasswordEncrypt() {
		if(this.applicativiPwConfiguration!=null){
			if(this.applicativiPwEncryptEngine==null){
				try{
					this.initApplicativiPassword();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
			return this.applicativiPwEncryptEngine;
		}
		return null;
	}
	public boolean isApplicativiPasswordEncryptEnabled() {
		return this.getApplicativiPasswordEncrypt()!=null && !CryptType.PLAIN.equals(this.getApplicativiPasswordEncrypt().getCryptType());
	}
	private int applicativiBasicLunghezzaPwGenerate;
	public int getApplicativiBasicLunghezzaPasswordGenerate() {
		return this.applicativiBasicLunghezzaPwGenerate;
	}
	private int applicativiApiKeyLunghezzaPwGenerate;
	protected int getApplicativiApiKeyLunghezzaPasswordGenerate() {
		return this.applicativiApiKeyLunghezzaPwGenerate;
	}
	
	protected ICrypt applicativiPwManager;
	public ICrypt getApplicativiPasswordManager() {
		return this.applicativiPwManager;
	}
	
	/** Soggetti */
	
	private static CryptConfig soggettiPasswordEncryptEngineApiMode = null;
	public static CryptConfig getSoggettiPasswordEncryptEngineApiMode() {
		return soggettiPasswordEncryptEngineApiMode;
	}
	public static void setSoggettiPasswordEncryptEngineApiMode(CryptConfig soggettiPasswordEncryptEngineApiMode) {
		ControlStationCore.soggettiPasswordEncryptEngineApiMode = soggettiPasswordEncryptEngineApiMode;
	}
	private static Integer soggettiApiKeyPasswordGeneratedLengthApiMode = null;
	public static Integer getSoggettiApiKeyPasswordGeneratedLengthApiMode() {
		return soggettiApiKeyPasswordGeneratedLengthApiMode;
	}
	public static void setSoggettiApiKeyPasswordGeneratedLengthApiMode(Integer soggettiApiKeyPasswordGeneratedLengthApiMode) {
		ControlStationCore.soggettiApiKeyPasswordGeneratedLengthApiMode = soggettiApiKeyPasswordGeneratedLengthApiMode;
	}
	private static PasswordVerifier soggettiPasswordVerifierEngineApiMode = null;
	public static PasswordVerifier getSoggettiPasswordVerifierEngineApiMode() {
		return soggettiPasswordVerifierEngineApiMode;
	}
	public static void setSoggettiPasswordVerifierEngineApiMode(
			PasswordVerifier soggettiPasswordVerifierEngineApiMode) {
		ControlStationCore.soggettiPasswordVerifierEngineApiMode = soggettiPasswordVerifierEngineApiMode;
	}
	
	private String soggettiPwConfiguration = null;
	public String getSoggettiPasswordConfiguration() {
		return this.soggettiPwConfiguration;
	}
	private boolean soggettiBasicPwEnableConstraints = false;
	public boolean isSoggettiBasicPasswordEnableConstraints() {
		return this.soggettiBasicPwEnableConstraints;
	}
	private PasswordVerifier soggettiPwVerifierEngine = null;
	private CryptConfig soggettiPwEncryptEngine = null;
	private synchronized void initSoggettiPassword() throws UtilsException {
		if(this.soggettiBasicPwEnableConstraints && this.soggettiPwVerifierEngine==null){
			this.soggettiPwVerifierEngine = new PasswordVerifier(this.soggettiPwConfiguration); 
		}
		if(this.soggettiPwEncryptEngine==null){
			this.soggettiPwEncryptEngine = new CryptConfig(this.soggettiPwConfiguration); 
		}
	}
	public PasswordVerifier getSoggettiPasswordVerifier() {
		if(this.soggettiBasicPwEnableConstraints && this.soggettiPwConfiguration!=null){
			if(this.soggettiPwVerifierEngine==null){
				try{
					this.initSoggettiPassword();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
			if(this.soggettiPwVerifierEngine!=null && this.soggettiPwVerifierEngine.existsRestriction()){
				return this.soggettiPwVerifierEngine;
			}
		}
		return null;
	}
	public CryptConfig getSoggettiPasswordEncrypt() {
		if(this.soggettiPwConfiguration!=null){
			if(this.soggettiPwEncryptEngine==null){
				try{
					this.initSoggettiPassword();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
			return this.soggettiPwEncryptEngine;
		}
		return null;
	}
	public boolean isSoggettiPasswordEncryptEnabled() {
		return this.getSoggettiPasswordEncrypt()!=null && !CryptType.PLAIN.equals(this.getSoggettiPasswordEncrypt().getCryptType());
	}
	private int soggettiBasicLunghezzaPwGenerate;
	public int getSoggettiBasicLunghezzaPasswordGenerate() {
		return this.soggettiBasicLunghezzaPwGenerate;
	}
	private int soggettiApiKeyLunghezzaPwGenerate;
	protected int getSoggettiApiKeyLunghezzaPasswordGenerate() {
		return this.soggettiApiKeyLunghezzaPwGenerate;
	}
	
	protected ICrypt soggettiPwManager;
	public ICrypt getSoggettiPasswordManager() {
		return this.soggettiPwManager;
	}
	
	/** MessageSecurity PropertiesSourceConfiguration */
	private PropertiesSourceConfiguration messageSecurityPropertiesSourceConfiguration = null;
	public PropertiesSourceConfiguration getMessageSecurityPropertiesSourceConfiguration() {
		return this.messageSecurityPropertiesSourceConfiguration;
	}
	
	/** PolicyGestioneToken PropertiesSourceConfiguration */
	private PropertiesSourceConfiguration policyGestioneTokenPropertiesSourceConfiguration = null;
	private boolean isPolicyGestioneTokenVerificaCertificati = false;
	private List<String> policyGestioneTokenPDND = null;
	public PropertiesSourceConfiguration getPolicyGestioneTokenPropertiesSourceConfiguration() {
		return this.policyGestioneTokenPropertiesSourceConfiguration;
	}
	public boolean isPolicyGestioneTokenVerificaCertificati() {
		return this.isPolicyGestioneTokenVerificaCertificati;
	}
	public List<String> getPolicyGestioneTokenPDND(){
		return this.policyGestioneTokenPDND;
	}
	public boolean isPolicyGestioneTokenPDND(String pdnd){
		return this.policyGestioneTokenPDND!=null && this.policyGestioneTokenPDND.contains(pdnd);
	}
	public String getDefaultPolicyGestioneTokenPDND(){
		if(this.policyGestioneTokenPDND!=null && !this.policyGestioneTokenPDND.isEmpty()) {
			return this.policyGestioneTokenPDND.get(0);
		}
		return null;
	}
	
	/** AttributeAuthority PropertiesSourceConfiguration */
	private PropertiesSourceConfiguration attributeAuthorityPropertiesSourceConfiguration = null;
	private boolean isAttributeAuthorityVerificaCertificati = false;
	public PropertiesSourceConfiguration getAttributeAuthorityPropertiesSourceConfiguration() {
		return this.attributeAuthorityPropertiesSourceConfiguration;
	}
	public boolean isAttributeAuthorityVerificaCertificati() {
		return this.isAttributeAuthorityVerificaCertificati;
	}
	
	/** ControlloTraffico */
	private boolean isControlloTrafficoPolicyGlobaleGroupByApi;
	private boolean isControlloTrafficoPolicyGlobaleFiltroApi;
	private boolean isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore;
	private List<PolicyGroupByActiveThreadsType> controlloTrafficoPolicyRateLimitingTipiGestori;
	public boolean isControlloTrafficoPolicyGlobaleGroupByApi() {
		return this.isControlloTrafficoPolicyGlobaleGroupByApi;
	}
	public boolean isControlloTrafficoPolicyGlobaleFiltroApi() {
		return this.isControlloTrafficoPolicyGlobaleFiltroApi;
	}
	public boolean isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore() {
		return this.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore;
	}
	public List<PolicyGroupByActiveThreadsType> getControlloTrafficoPolicyRateLimitingTipiGestori() {
		return this.controlloTrafficoPolicyRateLimitingTipiGestori;
	}
	
	/** Auditing */
	private boolean isAuditingRegistrazioneElementiBinari;
	public boolean isAuditingRegistrazioneElementiBinari() {
		return this.isAuditingRegistrazioneElementiBinari;
	}
	
	/** IntegrationManager */
	private boolean isIntegrationManagerEnabled;
	private boolean isIntegrationManagerTraceMessageBoxOperationEnabled;
	public boolean isIntegrationManagerEnabled() {
		return this.isIntegrationManagerEnabled;
	}
	public boolean isIntegrationManagerTraceMessageBoxOperationEnabled() {
		return this.isIntegrationManagerTraceMessageBoxOperationEnabled;
	}
	
	/** Soggetti */
	private Integer soggettiNomeMaxLength;
	private boolean isSoggettiVerificaCertificati;
	public Integer getSoggettiNomeMaxLength() {
		return this.soggettiNomeMaxLength;
	}
	public boolean isSoggettiVerificaCertificati() {
		return this.isSoggettiVerificaCertificati;
	}
	
	/** Applicativi */
	private boolean isApplicativiVerificaCertificati;
	public boolean isApplicativiVerificaCertificati() {
		return this.isApplicativiVerificaCertificati;
	}
	
	/** API */
	private boolean isApiResourcePathValidatorEnabled;
	private boolean isApiResourceHttpMethodAndPathQualsiasiEnabled;
	private List<String> getApiResourcePathQualsiasiSpecialChar;
	private boolean isApiOpenAPIValidateUriReferenceAsUrl;
	private boolean isApiRestResourceRepresentationMessageTypeOverride;
	private boolean isApiDescriptionTruncate255;
	private boolean isApiDescriptionTruncate4000;
	public boolean isApiResourcePathValidatorEnabled() {
		return this.isApiResourcePathValidatorEnabled;
	}
	public boolean isApiResourceHttpMethodAndPathQualsiasiEnabled() {
		return this.isApiResourceHttpMethodAndPathQualsiasiEnabled;
	}
	public List<String> getGetApiResourcePathQualsiasiSpecialChar() {
		return this.getApiResourcePathQualsiasiSpecialChar;
	}
	public boolean isApiOpenAPIValidateUriReferenceAsUrl() {
		return this.isApiOpenAPIValidateUriReferenceAsUrl;
	}
	public boolean isApiRestResourceRepresentationMessageTypeOverride() {
		return this.isApiRestResourceRepresentationMessageTypeOverride;
	}
	public boolean isApiDescriptionTruncate255() {
		return  this.isApiDescriptionTruncate255;
	}
	public boolean isApiDescriptionTruncate4000() {
		return  this.isApiDescriptionTruncate4000;
	}
	
	/** Accordi di Cooperazione */
	private boolean isAccordiCooperazioneEnabled;
	public boolean isAccordiCooperazioneEnabled() {
		return this.isAccordiCooperazioneEnabled;
	}
	
	/** API Impl */
	private boolean isErogazioniVerificaCertificati;
	private boolean isFruizioniVerificaCertificati;
	public boolean isErogazioniVerificaCertificati() {
		return this.isErogazioniVerificaCertificati;
	}
	public boolean isFruizioniVerificaCertificati() {
		return this.isFruizioniVerificaCertificati;
	}
	
	/** Message Engine */
	private List<String> messageEngines;
	public List<String> getMessageEngines() {
		return this.messageEngines;
	}
	
	/** Credenziali Basic */
	private boolean isSoggettiCredenzialiBasicCheckUniqueUsePassword;
	private boolean isApplicativiCredenzialiBasicCheckUniqueUsePassword;
	private static Boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentialsApiMode;
	public static void setIsSoggettiApplicativiCredenzialiBasicPermitSameCredentialsApiMode(
			Boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentialsApiMode) {
		ControlStationCore.isSoggettiApplicativiCredenzialiBasicPermitSameCredentialsApiMode = isSoggettiApplicativiCredenzialiBasicPermitSameCredentialsApiMode;
	}
	private boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentials;
	public boolean isSoggettiCredenzialiBasicCheckUniqueUsePassword() {
		return this.isSoggettiCredenzialiBasicCheckUniqueUsePassword;
	}
	public boolean isApplicativiCredenzialiBasicCheckUniqueUsePassword() {
		return this.isApplicativiCredenzialiBasicCheckUniqueUsePassword;
	}
	public boolean isSoggettiApplicativiCredenzialiBasicPermitSameCredentials() {
		if(ControlStationCore.isAPIMode() && isSoggettiApplicativiCredenzialiBasicPermitSameCredentialsApiMode!=null) {
			return isSoggettiApplicativiCredenzialiBasicPermitSameCredentialsApiMode;
		}
		return this.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials;
	}
	
	/** Credenziali Ssl */
	private static Boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentialsApiMode;
	public static void setIsSoggettiApplicativiCredenzialiSslPermitSameCredentialsApiMode(
			Boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentialsApiMode) {
		ControlStationCore.isSoggettiApplicativiCredenzialiSslPermitSameCredentialsApiMode = isSoggettiApplicativiCredenzialiSslPermitSameCredentialsApiMode;
	}
	private boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentials;
	public boolean isSoggettiApplicativiCredenzialiSslPermitSameCredentials() {
		if(ControlStationCore.isAPIMode() && isSoggettiApplicativiCredenzialiSslPermitSameCredentialsApiMode!=null) {
			return isSoggettiApplicativiCredenzialiSslPermitSameCredentialsApiMode;
		}
		return this.isSoggettiApplicativiCredenzialiSslPermitSameCredentials;
	}
	
	/** Credenziali Principal */
	private static Boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentialsApiMode;
	public static void setIsSoggettiApplicativiCredenzialiPrincipalPermitSameCredentialsApiMode(
			Boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentialsApiMode) {
		ControlStationCore.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentialsApiMode = isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentialsApiMode;
	}
	private boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials;
	public boolean isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials() {
		if(ControlStationCore.isAPIMode() && isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentialsApiMode!=null) {
			return isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentialsApiMode;
		}
		return this.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials;
	}
	
	/** Connettori */
	private boolean isConnettoriAllTypesEnabled;
	public boolean isConnettoriAllTypesEnabled() {
		return this.isConnettoriAllTypesEnabled;
	}
	
	/** Connettori Multipli */
	private boolean isConnettoriMultipliEnabled;
	public boolean isConnettoriMultipliEnabled() {
		return this.isConnettoriMultipliEnabled;
	}
	private boolean isConnettoriMultipliConsegnaCondizionaleStessFiltroPermesso;
	public boolean isConnettoriMultipliConsegnaCondizionaleStessFiltroPermesso() {
		return this.isConnettoriMultipliConsegnaCondizionaleStessFiltroPermesso;
	}
	private boolean isConnettoriMultipliConsegnaMultiplaEnabled;
	public boolean isConnettoriMultipliConsegnaMultiplaEnabled() {
		return this.isConnettoriMultipliConsegnaMultiplaEnabled;
	}
	
	/** Connettori Multipli */
	private boolean isApplicativiServerEnabled;
	public boolean isApplicativiServerEnabled(ConsoleHelper helper) {
		if(helper.isModalitaCompleta()) {
			return false;
		}
		return this.isApplicativiServerEnabled;
	}

	/** Gestione Consegne Asincrone */
	private List<String> consegnaNotificaCode;
	private Map<String, String> consegnaNotificaCodaLabel = new HashMap<>();
	private List<String> consegnaNotificaPriorita;
	private Map<String, ConfigurazionePriorita> consegnaNotificaConfigurazionePriorita = new HashMap<>();
	public List<String> getConsegnaNotificaCode() {
		return this.consegnaNotificaCode;
	}
	public String getConsegnaNotificaCodaLabel(String nome) {
		return this.consegnaNotificaCodaLabel.get(nome);
	}
	public List<String> getConsegnaNotificaPriorita() {
		return this.consegnaNotificaPriorita;
	}
	public ConfigurazionePriorita getConsegnaNotificaConfigurazionePriorita(String nome) {
		return this.consegnaNotificaConfigurazionePriorita.get(nome);
	}
	
	/** ModI */
	private boolean isModipaErogazioniVerificaCertificati;
	private boolean isModipaFruizioniVerificaCertificati;
	private boolean isModipaFruizioniConnettoreCheckHttps;
	private boolean isModipaFiltroRicercaProfiloQualsiasiVisualizzaDatiModi;
	public boolean isModipaErogazioniVerificaCertificati() {
		return this.isModipaErogazioniVerificaCertificati;
	}
	public boolean isModipaFruizioniVerificaCertificati() {
		return this.isModipaFruizioniVerificaCertificati;
	}
	public boolean isModipaFruizioniConnettoreCheckHttps() {
		return this.isModipaFruizioniConnettoreCheckHttps;
	}
	public boolean isModipaFiltroRicercaProfiloQualsiasiVisualizzaDatiModi() {
		return this.isModipaFiltroRicercaProfiloQualsiasiVisualizzaDatiModi;
	}
	
	/** Plugins */
	private boolean configurazionePluginsEnabled = false;
	private Integer configurazionePluginsSeconds = null;
	public boolean isConfigurazionePluginsEnabled() {
		return this.configurazionePluginsEnabled;
	}
	
	/** Handlers */
	private boolean configurazioneHandlersEnabled = false;
	public boolean isConfigurazioneHandlersEnabled() {
		return this.configurazioneHandlersEnabled;
	}
	
	/** Configurazione Allarmi */
	private boolean configurazioneAllarmiEnabled = false;
	public boolean isConfigurazioneAllarmiEnabled() {
		return this.configurazioneAllarmiEnabled;
	}
	private AlarmEngineConfig allarmiConfig = null;
	public AlarmEngineConfig getAllarmiConfig() {
		return this.allarmiConfig;
	}
	private boolean showAllarmiIdentificativoRuntime = false;
	public Boolean isShowAllarmiIdentificativoRuntime() {
		return this.showAllarmiIdentificativoRuntime;
	}
	private boolean showAllarmiFormNomeSuggeritoCreazione = false;
	public Boolean isShowAllarmiFormNomeSuggeritoCreazione() {
		return this.showAllarmiFormNomeSuggeritoCreazione;
	}
	private boolean showAllarmiFormStatoAllarme = false;
	public Boolean isShowAllarmiFormStatoAllarme() {
		return this.showAllarmiFormStatoAllarme;
	}
	private boolean showAllarmiFormStatoAllarmeHistory = false;
	public Boolean isShowAllarmiFormStatoAllarmeHistory() {
		return this.showAllarmiFormStatoAllarmeHistory;
	}
	private boolean showAllarmiSearchStatiAllarmi = false;
	public Boolean isShowAllarmiSearchStatiAllarmi() {
		return this.showAllarmiSearchStatiAllarmi;
	}
	private boolean showAllarmiElenchiStatiAllarmi = false;
	public Boolean isShowAllarmiElenchiStatiAllarmi() {
		return this.showAllarmiElenchiStatiAllarmi;
	}
	
	/** Registrazione Messaggi */
	private boolean isRegistrazioneMessaggiMultipartPayloadParsingEnabled = false;
	public Boolean isRegistrazioneMessaggiMultipartPayloadParsingEnabled() {
		return this.isRegistrazioneMessaggiMultipartPayloadParsingEnabled;
	}
	
	/** Cluster dinamico */
	private boolean isClusterDinamicoEnabled = false;
	public Boolean isClusterDinamicoEnabled() {
		return this.isClusterDinamicoEnabled;
	}
	
	/** OCSP */
	private boolean isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata = false;
	public boolean isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata() {
		return this.isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata;
	}
	
	/** Certificati */
	private int verificaCertificatiWarningExpirationDays;
	private boolean verificaCertificatiSceltaClusterId;
	public int getVerificaCertificatiWarningExpirationDays() {
		return this.verificaCertificatiWarningExpirationDays;
	}
	public boolean isVerificaCertificatiSceltaClusterId() {
		return this.verificaCertificatiSceltaClusterId;
	}
	
	/** Cluster */
	private boolean isClusterAsyncUpdate;
	private int clusterAsyncUpdateCheckInterval;
	public boolean isClusterAsyncUpdate() {
		return this.isClusterAsyncUpdate;
	}
	public int getClusterAsyncUpdateCheckInterval() {
		return this.clusterAsyncUpdateCheckInterval;
	}
	
	/** Opzioni di visualizzazione */
	private boolean showCorrelazioneAsincronaInAccordi = false;
	private boolean showFlagPrivato = false;
	private boolean showAllConnettori = false;
	private boolean showDebugOptionConnettore = true;
	private boolean showPulsantiImportExport = false;
	private int elenchiMenuIdentificativiLunghezzaMassima = 100;
	private boolean showCountElementInLinkList = false;
	private boolean conservaRisultatiRicerca = false;
	
	private static Boolean conservaRisultatiRicercaStaticInfoRead = null;
	private static boolean conservaRisultatiRicercaStaticInfo = false;
	public static Boolean getConservaRisultatiRicercaStaticInfoRead() {
		return conservaRisultatiRicercaStaticInfoRead;
	}
	public static void setConservaRisultatiRicercaStaticInfoRead(Boolean conservaRisultatiRicercaStaticInfoRead) {
		ControlStationCore.conservaRisultatiRicercaStaticInfoRead = conservaRisultatiRicercaStaticInfoRead;
	}
	public static boolean isConservaRisultatiRicercaStaticInfo() {
		return conservaRisultatiRicercaStaticInfo;
	}
	public static void setConservaRisultatiRicercaStaticInfo(boolean conservaRisultatiRicercaStaticInfo) {
		ControlStationCore.conservaRisultatiRicercaStaticInfo = conservaRisultatiRicercaStaticInfo;
	}
	
	private boolean showAccordiColonnaAzioni = false;
	private boolean showAccordiInformazioniProtocollo = false;
	private boolean showConfigurazioniPersonalizzate = false;
	private boolean showGestioneSoggettiRouter = false;
	private boolean showGestioneSoggettiVirtuali = false;
	private boolean showGestioneWorkflowStatoDocumenti = false;
	private boolean gestioneWorkflowStatoDocumentiVisualizzaStatoLista = false;
	private boolean gestioneWorkflowStatoDocumentiRipristinoStatoOperativoDaFinale = false;
	private boolean showInterfacceAPI = false;
	private boolean showAllegati = false;
	private boolean enableAutoMappingWsdlIntoAccordo = false;
	private boolean enableAutoMappingWsdlIntoAccordoEstrazioneSchemiInWsdlTypes = false;
	private boolean showMTOMVisualizzazioneCompleta = false;
	private int portaCorrelazioneApplicativaMaxLength = 255;
	private boolean showPortaDelegataLocalForward = false;
	private boolean isProprietaErogazioniShowModalitaStandard;
	private boolean isProprietaFruizioniShowModalitaStandard;
	private boolean isPortTypeObbligatorioImplementazioniSOAP = true;
	private boolean isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona = false;
	private boolean isVisualizzazioneConfigurazioneDiagnosticaLog4J = true;
	private String tokenPolicyForceId = null;
	private boolean tokenPolicyForceIdEnabled = false;
	private Properties tokenPolicyTipologia = null;
	private String attributeAuthorityForceId = null;
	private boolean attributeAuthorityForceIdEnabled = false;
	private Properties attributeAuthorityTipologia = null;
	private boolean showServiziVisualizzaModalitaElenco = false;
	private Integer selectListSoggettiOperativiNumeroMassimoSoggetti = null;
	private Integer selectListSoggettiOperativiDimensioneMassimaLabel = null;
	private Integer viewLunghezzaMassimaInformazione = null;
	private boolean isSetSearchAfterAdd = false;
	private boolean elenchiVisualizzaComandoResetCacheSingoloElemento = false;
	private Integer validitaTokenCsrf = null;
	private String cspHeaderValue = null;
	private String xContentTypeOptionsHeaderValue = null;
	private String xXssProtectionHeaderValue = null;
	private String xFrameOptionsHeaderValue = null;
	private Properties consoleSecurityConfiguration = null;
	
	public boolean isShowCorrelazioneAsincronaInAccordi() {
		return this.showCorrelazioneAsincronaInAccordi;
	}
	public boolean isShowFlagPrivato() {
		return this.showFlagPrivato;
	}
	public boolean isShowAllConnettori() {
		return this.showAllConnettori;
	}
	public boolean isShowDebugOptionConnettore() {
		return this.showDebugOptionConnettore;
	}	
	public boolean isShowPulsantiImportExport() {
		return this.showPulsantiImportExport;
	}
	public int getElenchiMenuIdentificativiLunghezzaMassima() {
		return this.elenchiMenuIdentificativiLunghezzaMassima;
	}
	public boolean isShowCountElementInLinkList() {
		return this.showCountElementInLinkList;
	}
	public boolean isConservaRisultatiRicerca() {
		return this.conservaRisultatiRicerca;
	}
	public boolean isShowAccordiColonnaAzioni() {
		return this.showAccordiColonnaAzioni;
	}
	public boolean isShowAccordiInformazioniProtocollo() {
		return this.showAccordiInformazioniProtocollo;
	}
	public boolean isShowConfigurazioniPersonalizzate() {
		return this.showConfigurazioniPersonalizzate;
	}
	public boolean isShowGestioneSoggettiRouter() {
		return this.showGestioneSoggettiRouter;
	}
	public boolean isShowGestioneSoggettiVirtuali() {
		return this.showGestioneSoggettiVirtuali;
	}
	public boolean isShowGestioneWorkflowStatoDocumenti(ConsoleHelper consoleHelper) {
		return this.showGestioneWorkflowStatoDocumenti && consoleHelper.isModalitaCompleta();
	}
	public boolean isGestioneWorkflowStatoDocumentiVisualizzaStatoLista() {
		return this.gestioneWorkflowStatoDocumentiVisualizzaStatoLista;
	}
	public boolean isGestioneWorkflowStatoDocumentiRipristinoStatoOperativoDaFinale() {
		return this.gestioneWorkflowStatoDocumentiRipristinoStatoOperativoDaFinale;
	}
	public boolean isShowInterfacceAPI() {
		return this.showInterfacceAPI;
	}
	public boolean isShowAllegati() {
		return this.showAllegati;
	}
	public boolean isEnableAutoMappingWsdlIntoAccordo() {
		return this.enableAutoMappingWsdlIntoAccordo;
	}
	public boolean isEnableAutoMappingWsdlIntoAccordoEstrazioneSchemiInWsdlTypes() {
		return this.enableAutoMappingWsdlIntoAccordoEstrazioneSchemiInWsdlTypes;
	}
	public boolean isShowMTOMVisualizzazioneCompleta() {
		return this.showMTOMVisualizzazioneCompleta;
	}
	public int getPortaCorrelazioneApplicativaMaxLength() {
		return this.portaCorrelazioneApplicativaMaxLength;
	}
	public boolean isShowPortaDelegataLocalForward() {
		return this.showPortaDelegataLocalForward;
	}
	public boolean isProprietaErogazioniShowModalitaStandard() {
		return this.isProprietaErogazioniShowModalitaStandard;
	}
	public boolean isProprietaFruizioniShowModalitaStandard() {
		return this.isProprietaFruizioniShowModalitaStandard;
	}
	public boolean isPortTypeObbligatorioImplementazioniSOAP() {
		return this.isPortTypeObbligatorioImplementazioniSOAP;
	}
	public boolean isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona() {
		return this.isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona;
	}
	public boolean isVisualizzazioneConfigurazioneDiagnosticaLog4J() {
		return this.isVisualizzazioneConfigurazioneDiagnosticaLog4J;
	}
	public String getTokenPolicyForceId() {
		return this.tokenPolicyForceId;
	}
	public boolean isTokenPolicyForceIdEnabled() {
		return this.tokenPolicyForceIdEnabled;
	}
	public Properties getTokenPolicyTipologia() {
		return this.tokenPolicyTipologia;
	}
	public String getAttributeAuthorityForceId() {
		return this.attributeAuthorityForceId;
	}
	public boolean isAttributeAuthorityForceIdEnabled() {
		return this.attributeAuthorityForceIdEnabled;
	}
	public Properties getAttributeAuthorityTipologia() {
		return this.attributeAuthorityTipologia;
	}
	public boolean isShowServiziVisualizzaModalitaElenco() {
		return this.showServiziVisualizzaModalitaElenco;
	}
	public Integer getNumeroMassimoSoggettiSelectListSoggettiOperatiti() {
		return this.selectListSoggettiOperativiNumeroMassimoSoggetti;
	}
	public Integer getLunghezzaMassimaLabelSoggettiOperativiMenuUtente() {
		return this.selectListSoggettiOperativiDimensioneMassimaLabel;
	}
	public Integer getViewLunghezzaMassimaInformazione() {
		return this.viewLunghezzaMassimaInformazione;
	}
	public boolean isSetSearchAfterAdd() {
		return this.isSetSearchAfterAdd;
	}
	public Integer getValiditaTokenCsrf() {
		return this.validitaTokenCsrf;
	}
	public String getCspHeaderValue() {
		return this.cspHeaderValue;
	}
	public String getXContentTypeOptionsHeaderValue() {
		return this.xContentTypeOptionsHeaderValue;
	}
	public String getXFrameOptionsHeaderValue() {
		return this.xFrameOptionsHeaderValue;
	}
	public String getXXssProtectionHeaderValue() {
		return this.xXssProtectionHeaderValue;
	}
	public Properties getConsoleSecurityConfiguration() {
		return this.consoleSecurityConfiguration;
	}
	public boolean showCodaMessage() {
		return this.isShowJ2eeOptions() || this.isIntegrationManagerEnabled();
	}
	public boolean isElenchiVisualizzaComandoResetCacheSingoloElemento() {
		return this.elenchiVisualizzaComandoResetCacheSingoloElemento;
	}


	/** Opzioni di importazione Archivi */
	private String importArchivi_tipoPdD;
	private boolean exportArchive_configurazione_soloDumpCompleto;
	private boolean exportArchive_servizi_standard;
	public String getImportArchivi_tipoPdD() {
		return this.importArchivi_tipoPdD;
	}
	public boolean isExportArchive_configurazione_soloDumpCompleto() {
		return this.exportArchive_configurazione_soloDumpCompleto;
	}
	public boolean isExportArchive_servizi_standard() {
		return this.exportArchive_servizi_standard;
	}

	/** Multitenant */
	private boolean multitenant = false;
	private MultitenantSoggettiErogazioni multitenantSoggettiErogazioni = null;
	private MultitenantSoggettiFruizioni multitenantSoggettiFruizioni = null;
	public boolean isMultitenant() {
		return this.multitenant;
	}
	public MultitenantSoggettiErogazioni getMultitenantSoggettiErogazioni() {
		return this.multitenantSoggettiErogazioni;
	}
	public MultitenantSoggettiFruizioni getMultitenantSoggettiFruizioni() {
		return this.multitenantSoggettiFruizioni;
	}
	
	/** Altro */
	private boolean enabledToken_generazioneAutomaticaPorteDelegate;
	private boolean enabledAutenticazione_generazioneAutomaticaPorteDelegate;
	private String autenticazione_generazioneAutomaticaPorteDelegate;
	private boolean enabledAutorizzazione_generazioneAutomaticaPorteDelegate;
	private String autorizzazione_generazioneAutomaticaPorteDelegate;
	private boolean enabledToken_generazioneAutomaticaPorteApplicative;
	private boolean enabledAutenticazione_generazioneAutomaticaPorteApplicative;
	private String autenticazione_generazioneAutomaticaPorteApplicative;
	private boolean enabledAutorizzazione_generazioneAutomaticaPorteApplicative;
	private String autorizzazione_generazioneAutomaticaPorteApplicative;
	private boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto;
	private boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto;
	public boolean isEnabledToken_generazioneAutomaticaPorteDelegate() {
		return this.enabledToken_generazioneAutomaticaPorteDelegate;
	}
	public boolean isEnabledAutenticazione_generazioneAutomaticaPorteDelegate() {
		return this.enabledAutenticazione_generazioneAutomaticaPorteDelegate;
	}
	public String getAutenticazione_generazioneAutomaticaPorteDelegate() {
		return this.autenticazione_generazioneAutomaticaPorteDelegate;
	}
	public boolean isEnabledAutorizzazione_generazioneAutomaticaPorteDelegate() {
		return this.enabledAutorizzazione_generazioneAutomaticaPorteDelegate;
	}
	public String getAutorizzazione_generazioneAutomaticaPorteDelegate() {
		return this.autorizzazione_generazioneAutomaticaPorteDelegate;
	}
	public boolean isEnabledToken_generazioneAutomaticaPorteApplicative() {
		return this.enabledToken_generazioneAutomaticaPorteApplicative;
	}
	public boolean isEnabledAutenticazione_generazioneAutomaticaPorteApplicative() {
		return this.enabledAutenticazione_generazioneAutomaticaPorteApplicative;
	}
	public String getAutenticazione_generazioneAutomaticaPorteApplicative() {
		return this.autenticazione_generazioneAutomaticaPorteApplicative;
	}
	public boolean isEnabledAutorizzazione_generazioneAutomaticaPorteApplicative(boolean erogazioneIsSupportatoAutenticazioneSoggetti) {
		if(this.enabledAutorizzazione_generazioneAutomaticaPorteApplicative) {
			return true;
		}
		else {
			if(erogazioneIsSupportatoAutenticazioneSoggetti) {
				// valore impostato
				return this.enabledAutorizzazione_generazioneAutomaticaPorteApplicative;
			}
			else {
				// Fix spcoop: se non e' supportata l'autenticazione dei soggetti, devo abilitare per default l'autorizzazione, altrimenti si crea un buco di sicurezza
				// ritorno quindi l'indicazione originale impostata per l'autenticazione
				return this.enabledAutenticazione_generazioneAutomaticaPorteApplicative;
			}
		}
	}
	public String getAutorizzazione_generazioneAutomaticaPorteApplicative() {
		return this.autorizzazione_generazioneAutomaticaPorteApplicative;
	}
	public boolean isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto() {
		return this.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto;
	}
	public boolean isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto() {
		return this.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto;
	}
	
	/** Opzioni per Plugins */
	private List<IExtendedMenu> pluginMenu;
	private List<IExtendedFormServlet> pluginConfigurazione;
	private Map<String, IExtendedListServlet> pluginConfigurazioneList = new HashMap<String, IExtendedListServlet>();
	private List<IExtendedConnettore> pluginConnettore;
	private IExtendedListServlet pluginPortaDelegata;
	private IExtendedListServlet pluginPortaApplicativa;
	private List<IExtendedMenu> newIExtendedMenu(String [] className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(className!=null){
			List<IExtendedMenu> list = new ArrayList<IExtendedMenu>();
			for (int i = 0; i < className.length; i++) {
				Class<?> c = Class.forName(className[i]);
				list.add( (IExtendedMenu) ClassLoaderUtilities.newInstance(c) );
			}
			return list;
		}
		return null;
	}
	private List<IExtendedFormServlet> newIExtendedFormServlet(String [] className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(className!=null){
			List<IExtendedFormServlet> list = new ArrayList<IExtendedFormServlet>();
			for (int i = 0; i < className.length; i++) {
				Class<?> c = Class.forName(className[i]);
				list.add( (IExtendedFormServlet) ClassLoaderUtilities.newInstance(c) );
			}
			return list;
		}
		return null;
	}
	private IExtendedListServlet newIExtendedListServlet(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(className!=null){
			Class<?> c = Class.forName(className);
			return (IExtendedListServlet) ClassLoaderUtilities.newInstance(c);
		}
		return null;
	}
	private List<IExtendedConnettore> newIExtendedConnettore(String [] className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(className!=null){
			List<IExtendedConnettore> list = new ArrayList<IExtendedConnettore>();
			for (int i = 0; i < className.length; i++) {
				Class<?> c = Class.forName(className[i]);
				list.add( (IExtendedConnettore) ClassLoaderUtilities.newInstance(c) );
			}
			return list;
		}
		return null;
	}
	
	/** Opzioni Accesso JMX della PdD */
	private boolean isVisualizzaLinkClearAllCachesRemoteCheckCacheStatus = false;
	private InvokerNodiRuntime invoker = null;
	private ConfigurazioneNodiRuntime configurazioneNodiRuntime = null;
	private List<String> jmxPdDAliases = new ArrayList<>();
	private Map<String,List<String>>  jmxPdDGruppiAliases = new HashMap<>();
	private Map<String, String> jmxPdDDescrizioni = new HashMap<>();
	private CertificateChecker jmxPdDCertificateChecker;
	private Map<String, String> jmxPdDConfigurazioneSistemaType = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeRisorsa = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoVersionePdD = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoVersioneBaseDati = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoVersioneJava = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoVendorJava = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoTipoDatabase = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniDatabase = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniSSL = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteSSL = new HashMap<>();
	private boolean jmxPdDConfigurazioneSistemaShowInformazioniCryptographyKeyLength = false;
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCryptographyKeyLength = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCharset = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniInternazionalizzazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteInternazionalizzazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniTimeZone = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteTimeZone = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaNetworking = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteProprietaJavaNetworking = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaAltro = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaSistema = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoMessageFactory = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoDirectoryConfigurazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoPluginProtocols = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoInformazioniInstallazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoUpdateFileTrace = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoConnessioniDB = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoConnessioniJMS = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoIdTransazioniAttive = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoIdProtocolloTransazioniAttive = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoConnessioniPD = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoConnessioniPA = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnostici = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnosticiLog4j = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTracciamento = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoDumpPD = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoDumpPA = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoLog4jDiagnostica = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoLog4jOpenspcoop = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoLog4jIntegrationManager = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoLog4jTracciamento = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoLog4jDump = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorStatusCode = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorInstanceId = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeBadResponse = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalResponseError = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalRequestError = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalError = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorDetails = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorUseStatusCodeAsFaultCode = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorGenerateHttpHeaderGovWayCode = new HashMap<>();	
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerConsegnaContenutiApplicativi = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerEventi = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerFileSystemRecovery = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteOnewayNonRiscontrate = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteAsincroneNonRiscontrate = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiEliminati = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiScaduti = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiNonGestiti = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaCorrelazioneApplicativa = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiVerificaConnessioniAttive = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestorePuliziaMessaggiAnomali = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreRepositoryBuste = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerMonitoraggioRisorseThread = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerRepositoryStatefulThread = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheOrarie = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheGiornaliere = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheSettimanali = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheMensili = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreChiaviPDND = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreCacheChiaviPDND = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreOperazioniRemote = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerSvecchiamentoOperazioniRemote = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoTimerThresholdThread = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreById = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyValidazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyNegoziazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreAttributeAuthority = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyValidazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyNegoziazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreAttributeAuthority = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoEnablePortaDelegata = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoDisablePortaDelegata = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoEnablePortaApplicativa = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoDisablePortaApplicativa = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoEnableConnettoreMultiplo = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoDisableConnettoreMultiplo = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiplo = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiplo = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiploRuntimeRepository = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiploRuntimeRepository = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAccordoCooperazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApi = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheErogazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheFruizione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheSoggetto = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApplicativo = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheRuolo = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheScope = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyValidazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyNegoziazione = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAttributeAuthority = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegata = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataAbilitazioniPuntuali = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataDisabilitazioniPuntuali = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativa = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaAbilitazioniPuntuali = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaDisabilitazioniPuntuali = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioIntegrationManager = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaDelegata = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaDelegata = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaApplicativa = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaApplicativa = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioIntegrationManager = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioIntegrationManager = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeAttributoNumeroDatasourceGW = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetDatasourcesGW = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetUsedConnectionsDatasourcesGW = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetInformazioniDatabaseDatasourcesGW = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetThreadPoolStatus  = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetQueueConfig  = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetApplicativiPrioritari  = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoGetConnettoriPrioritari  = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoUpdateConnettoriPrioritari  = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoResetConnettoriPrioritari  = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeRisorsaSystemPropertiesPdD = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRefreshPersistentConfiguration = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeRisorsaDatiRichieste = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingGlobalConfigCache = new HashMap<>();
	private Map<String, String> jmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingAPIConfigCache = new HashMap<>();
	private Map<String, List<String>> jmxPdDCaches = new HashMap<>();
	private Map<String, List<String>> jmxPdDCachesPrefill = new HashMap<>();
	private Map<String, String> jmxPdDCacheType = new HashMap<>();
	private Map<String, String> jmxPdDCacheNomeAttributoCacheAbilitata = new HashMap<>();
	private Map<String, String> jmxPdDCacheNomeMetodoStatoCache = new HashMap<>();
	private Map<String, String> jmxPdDCacheNomeMetodoResetCache = new HashMap<>();
	private Map<String, String> jmxPdDCacheNomeMetodoPrefillCache = new HashMap<>();
	
	public boolean isVisualizzaLinkClearAllCachesRemoteCheckCacheStatus() {
		return this.isVisualizzaLinkClearAllCachesRemoteCheckCacheStatus;
	}
	public List<String> getJmxPdDAliases() {
		return this.jmxPdDAliases;
	}
	public Map<String, List<String>> getJmxPdDGruppiAliases() {
		return this.jmxPdDGruppiAliases;
	}
	public String getJmxPdDDescrizione(String alias) throws DriverControlStationException, DriverControlStationNotFound {
		String descrizione = this.jmxPdDDescrizioni.get(alias);
		if(descrizione==null || "".equals(descrizione)){
			if(this.singlePdD){
				descrizione = alias; // uso lo stesso nome dell'alias
			}
			else{
				PddCore pddCore = new PddCore(this);
				PdDControlStation pdd = pddCore.getPdDControlStation(alias); // esiste per forza
				if(pdd.getDescrizione()!=null && "".equals(pdd.getDescrizione())){
					descrizione = pdd.getDescrizione();
				}
				else{
					descrizione = alias; // uso lo stesso nome dell'alias
				}
			}
		}
		return descrizione;
	}
	public CertificateChecker getJmxPdDCertificateChecker() {
		return this.jmxPdDCertificateChecker;
	}
	public CertificateChecker newJmxPdDCertificateChecker(List<String> alias) throws OpenSPCoop2ConfigurationException {
		return new CertificateChecker(log, this.invoker, this.configurazioneNodiRuntime, alias, ConsoleProperties.getInstance());
	}
	public String getJmxPdDConfigurazioneSistemaType(String alias) {
		return this.jmxPdDConfigurazioneSistemaType.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsa(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeRisorsa.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoVersionePdD(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoVersionePdD.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoVersioneBaseDati(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoVersioneBaseDati.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoVersioneJava(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoVersioneJava.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoVendorJava(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoVendorJava.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoTipoDatabase(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoTipoDatabase.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniDatabase(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniDatabase.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniSSL(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniSSL.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteSSL(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteSSL.get(alias);
	}
	public boolean isJmxPdD_configurazioneSistemaShowInformazioniCryptographyKeyLength() {
		return this.jmxPdDConfigurazioneSistemaShowInformazioniCryptographyKeyLength;
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCryptographyKeyLength(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCryptographyKeyLength.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCharset(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCharset.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniInternazionalizzazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniInternazionalizzazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteInternazionalizzazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteInternazionalizzazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniTimeZone(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniTimeZone.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteTimeZone(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteTimeZone.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaNetworking(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaNetworking.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteProprietaJavaNetworking(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteProprietaJavaNetworking.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaAltro(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaAltro.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaSistema(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaSistema.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoMessageFactory(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoMessageFactory.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDirectoryConfigurazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoDirectoryConfigurazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoPluginProtocols(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoPluginProtocols.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniInstallazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniInstallazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoUpdateFileTrace(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoUpdateFileTrace.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniDB(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniDB.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniJMS(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniJMS.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoIdTransazioniAttive(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoIdTransazioniAttive.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoIdProtocolloTransazioniAttive(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoIdProtocolloTransazioniAttive.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniPD(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniPD.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniPA(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniPA.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnostici(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnostici.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnosticiLog4j(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnosticiLog4j.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTracciamento(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTracciamento.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoDumpPD(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoDumpPD.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoDumpPA(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoDumpPA.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoLog4jDiagnostica(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jDiagnostica.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoLog4jOpenspcoop(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jOpenspcoop.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoLog4jIntegrationManager(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jIntegrationManager.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoLog4jTracciamento(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jTracciamento.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoLog4jDump(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jDump.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorStatusCode(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorStatusCode.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorInstanceId(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorInstanceId.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeBadResponse(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeBadResponse.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalResponseError(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalResponseError.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalRequestError(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalRequestError.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalError(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalError.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorDetails(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorDetails.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorUseStatusCodeAsFaultCode(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorUseStatusCodeAsFaultCode.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorGenerateHttpHeaderGovWayCode(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorGenerateHttpHeaderGovWayCode.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerConsegnaContenutiApplicativi(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerConsegnaContenutiApplicativi.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerEventi(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerEventi.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerFileSystemRecovery(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerFileSystemRecovery.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteOnewayNonRiscontrate(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteOnewayNonRiscontrate.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteAsincroneNonRiscontrate(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteAsincroneNonRiscontrate.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiEliminati(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiEliminati.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiScaduti(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiScaduti.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiNonGestiti(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiNonGestiti.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaCorrelazioneApplicativa(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaCorrelazioneApplicativa.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiVerificaConnessioniAttive(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiVerificaConnessioniAttive.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestorePuliziaMessaggiAnomali(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestorePuliziaMessaggiAnomali.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreRepositoryBuste(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreRepositoryBuste.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerMonitoraggioRisorseThread(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerMonitoraggioRisorseThread.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerRepositoryStatefulThread(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerRepositoryStatefulThread.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheOrarie(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheOrarie.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheGiornaliere(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheGiornaliere.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheSettimanali(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheSettimanali.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheMensili(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheMensili.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreChiaviPDND(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreChiaviPDND.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreCacheChiaviPDND(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreCacheChiaviPDND.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreOperazioniRemote(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreOperazioniRemote.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerSvecchiamentoOperazioniRemote(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerSvecchiamentoOperazioniRemote.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoTimerThresholdThread(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoTimerThresholdThread.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreById(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreById.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyValidazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyValidazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyNegoziazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyNegoziazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreAttributeAuthority(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreAttributeAuthority.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyValidazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyValidazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyNegoziazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyNegoziazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreAttributeAuthority(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreAttributeAuthority.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoEnablePortaDelegata(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoEnablePortaDelegata.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisablePortaDelegata(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoDisablePortaDelegata.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoEnablePortaApplicativa(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoEnablePortaApplicativa.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisablePortaApplicativa(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoDisablePortaApplicativa.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoEnableConnettoreMultiplo(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoEnableConnettoreMultiplo.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisableConnettoreMultiplo(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoDisableConnettoreMultiplo.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiplo(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiplo.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiplo(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiplo.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiploRuntimeRepository(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiploRuntimeRepository.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiploRuntimeRepository(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiploRuntimeRepository.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAccordoCooperazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAccordoCooperazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApi(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApi.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheErogazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheErogazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheFruizione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheFruizione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheSoggetto(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheSoggetto.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApplicativo(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApplicativo.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheRuolo(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheRuolo.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheScope(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheScope.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyValidazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyValidazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyNegoziazione(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyNegoziazione.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAttributeAuthority(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAttributeAuthority.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegata(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegata.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataAbilitazioniPuntuali(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataAbilitazioniPuntuali.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataDisabilitazioniPuntuali(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataDisabilitazioniPuntuali.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativa(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativa.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaAbilitazioniPuntuali(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaAbilitazioniPuntuali.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaDisabilitazioniPuntuali(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaDisabilitazioniPuntuali.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioIntegrationManager(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioIntegrationManager.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaDelegata(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaDelegata.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaDelegata(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaDelegata.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaApplicativa(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaApplicativa.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaApplicativa(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaApplicativa.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioIntegrationManager(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioIntegrationManager.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioIntegrationManager(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioIntegrationManager.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeAttributoNumeroDatasourceGW(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeAttributoNumeroDatasourceGW.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetDatasourcesGW(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetDatasourcesGW.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetUsedConnectionsDatasourcesGW(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetUsedConnectionsDatasourcesGW.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetInformazioniDatabaseDatasourcesGW(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetInformazioniDatabaseDatasourcesGW.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetThreadPoolStatus(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetThreadPoolStatus.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetQueueConfig(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetQueueConfig.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetApplicativiPrioritari(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetApplicativiPrioritari.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoGetConnettoriPrioritari(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoGetConnettoriPrioritari.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoUpdateConnettoriPrioritari(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoUpdateConnettoriPrioritari.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoResetConnettoriPrioritari(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoResetConnettoriPrioritari.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaSystemPropertiesPdD(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeRisorsaSystemPropertiesPdD.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRefreshPersistentConfiguration(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRefreshPersistentConfiguration.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeRisorsaDatiRichieste(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeRisorsaDatiRichieste.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingGlobalConfigCache(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingGlobalConfigCache.get(alias);
	}
	public String getJmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingAPIConfigCache(String alias) {
		return this.jmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingAPIConfigCache.get(alias);
	}
	public List<String> getJmxPdDCaches(String alias) {
		return this.jmxPdDCaches.get(alias);
	}
	public List<String> getJmxPdDCachesPrefill(String alias) {
		return this.jmxPdDCachesPrefill.get(alias);
	}
	public String getJmxPdDCacheType(String alias) {
		return this.jmxPdDCacheType.get(alias);
	}
	public String getJmxPdDCacheNomeAttributoCacheAbilitata(String alias) {
		return this.jmxPdDCacheNomeAttributoCacheAbilitata.get(alias);
	}
	public String getJmxPdDCacheNomeMetodoStatoCache(String alias) {
		return this.jmxPdDCacheNomeMetodoStatoCache.get(alias);
	}
	public String getJmxPdDCacheNomeMetodoResetCache(String alias) {
		return this.jmxPdDCacheNomeMetodoResetCache.get(alias);
	}
	public String getJmxPdDCacheNomeMetodoPrefillCache(String alias) {
		return this.jmxPdDCacheNomeMetodoPrefillCache.get(alias);
	}

	public InvokerNodiRuntime getInvoker() {
		return this.invoker;
	}
	
	

	/* --- COSTRUTTORI --- */

	public static Boolean API = null;
	public static synchronized void initAPIMode() {
		if(API==null) {
			API = true;
		}
	}
	public static boolean isAPIMode() {
		return API!=null && API.booleanValue();
	}
		
	protected boolean usedByApi = false;
	public boolean isUsedByApi() {
		return this.usedByApi;
	}
		
	public ControlStationCore() throws DriverControlStationException {
		this(false,null,null);
	}
	
	public ControlStationCore(boolean initForApi, String confDir, String protocolloDefault) throws DriverControlStationException {

		this.usedByApi = initForApi;
		
		if(initForApi) {
			ControlStationCore.log = LoggerWrapperFactory.getLogger(ControlStationCore.class);
			if(API==null) {
				ControlStationCore.initAPIMode();
			}
		}
		else {
			ControlStationCore.checkInitLogger();
		}

		try{
			if(!initForApi) {
				this.initCore();
			}

			// inizializzo il DBManager
			this.initConnections();

			// inizializzo DateManager
			DateManager.initializeDataManager(org.openspcoop2.utils.date.SystemDate.class.getName(), null, ControlStationCore.log);

			// inizializzo JMX
			if(!initForApi) {
				this.initCoreJmxResources();
			}

			// inizializza l'AuditManager
			ControlStationCore.initializeAuditManager(this.tipoDB);

			// inizializzo Core SICAContext
			//this.contextSICA = new SICAtoOpenSPCoopContext("SICA");

			this.idAccordoFactory = IDAccordoFactory.getInstance();
			this.idAccordoCooperazioneFactory = IDAccordoCooperazioneFactory.getInstance();
			this.idServizioFactory = IDServizioFactory.getInstance();

			ConfigurazionePdD configPdD = new ConfigurazionePdD();
			configPdD.setLog(ControlStationCore.log);
			configPdD.setLoader(org.openspcoop2.utils.resources.Loader.getInstance());
			if(!initForApi) {
				configPdD.setConfigurationDir(ConsoleProperties.getInstance().getConfDirectory());
			}
			else {
				configPdD.setConfigurationDir(confDir);
			}
			configPdD.setAttesaAttivaJDBC(this.jdbcSerializableAttesaAttiva);
			configPdD.setCheckIntervalJDBC(this.jdbcSerializableCheck);
			configPdD.setTipoDatabase(TipiDatabase.toEnumConstant(DatasourceProperties.getInstance().getTipoDatabase()));
			ProtocolFactoryManager.initialize(ControlStationCore.log, configPdD, this.protocolloDefault);
			this.protocolFactoryManager = ProtocolFactoryManager.getInstance();
			if(initForApi) {
				this.protocolloDefault = protocolloDefault;
			}
			if(this.protocolloDefault==null){
				this.protocolloDefault = this.protocolFactoryManager.getDefaultProtocolFactory().getProtocol();
			}

			// Leggo configurazione multitenant
			ConfigurazioneMultitenant confMultitenant = this.getConfigurazioneGenerale().getMultitenant();
			if(confMultitenant!=null) {
				
				this.multitenant = StatoFunzionalita.ABILITATO.equals(confMultitenant.getStato());
				
				if(confMultitenant.getErogazioneSceltaSoggettiFruitori()!=null) {
					switch (confMultitenant.getErogazioneSceltaSoggettiFruitori()) {
					case SOGGETTI_ESTERNI:
						this.multitenantSoggettiErogazioni = MultitenantSoggettiErogazioni.SOLO_SOGGETTI_ESTERNI;
						break;
					case ESCLUDI_SOGGETTO_EROGATORE:
						this.multitenantSoggettiErogazioni = MultitenantSoggettiErogazioni.ESCLUDI_SOGGETTO_EROGATORE;
						break;
					case TUTTI:
						this.multitenantSoggettiErogazioni = MultitenantSoggettiErogazioni.TUTTI;
						break;
					}
				}
				else {
					this.multitenantSoggettiErogazioni = MultitenantSoggettiErogazioni.SOLO_SOGGETTI_ESTERNI; // default
				}
				
				if(confMultitenant.getFruizioneSceltaSoggettiErogatori()!=null) {
					switch (confMultitenant.getFruizioneSceltaSoggettiErogatori()) {
					case SOGGETTI_ESTERNI:
						this.multitenantSoggettiFruizioni = MultitenantSoggettiFruizioni.SOLO_SOGGETTI_ESTERNI;
						break;
					case ESCLUDI_SOGGETTO_FRUITORE:
						this.multitenantSoggettiFruizioni = MultitenantSoggettiFruizioni.ESCLUDI_SOGGETTO_FRUITORE;
						break;
					case TUTTI:
						this.multitenantSoggettiFruizioni = MultitenantSoggettiFruizioni.TUTTI;
						break;
					}
				}
				else {
					this.multitenantSoggettiFruizioni = MultitenantSoggettiFruizioni.SOLO_SOGGETTI_ESTERNI; // default
				}
			}
			
			// Verifica Consistenza dei Protocolli
			verificaConsistenzaProtocolli(this);
			
			// Inizializzo password manager
			CryptConfig utenzeConfig = null;
			CryptConfig applicativiConfig = null;
			CryptConfig soggettiConfig = null;
			if(ControlStationCore.isAPIMode()) {
				utenzeConfig = ControlStationCore.getUtenzePasswordEncryptEngineApiMode();
				applicativiConfig = ControlStationCore.getApplicativiPasswordEncryptEngineApiMode();
				soggettiConfig = ControlStationCore.getSoggettiPasswordEncryptEngineApiMode();
				
				this.applicativiPwConfiguration = "APIMode";
				this.applicativiPwEncryptEngine = applicativiConfig;
				this.applicativiApiKeyLunghezzaPwGenerate = ControlStationCore.getApplicativiApiKeyPasswordGeneratedLengthApiMode();
				this.applicativiPwVerifierEngine = ControlStationCore.getApplicativiPasswordVerifierEngineApiMode();
				this.applicativiBasicPwEnableConstraints = (this.applicativiPwVerifierEngine!=null);
				
				this.soggettiPwConfiguration = "APIMode";
				this.soggettiPwEncryptEngine = soggettiConfig;
				this.soggettiApiKeyLunghezzaPwGenerate = ControlStationCore.getSoggettiApiKeyPasswordGeneratedLengthApiMode();
				this.soggettiPwVerifierEngine = ControlStationCore.getSoggettiPasswordVerifierEngineApiMode();
				this.soggettiBasicPwEnableConstraints = (this.soggettiPwVerifierEngine!=null);
			}
			else {
				utenzeConfig = this.getUtenzePasswordEncrypt();
				applicativiConfig = this.getApplicativiPasswordEncrypt();
				soggettiConfig = this.getSoggettiPasswordEncrypt();
			}
			this.utenzePasswordManager = CryptFactory.getCrypt(log, utenzeConfig);
			if(utenzeConfig.isBackwardCompatibility()) {
				this.utenzePasswordManagerBackwardCompatibility = CryptFactory.getOldMD5Crypt(log);
			}
			this.applicativiPwManager = CryptFactory.getCrypt(log, applicativiConfig);
			this.soggettiPwManager = CryptFactory.getCrypt(log, soggettiConfig);
			
			if(initForApi) {
				this.singlePdD = true;
			}

		}catch(Exception e){
			ControlStationCore.logError("Errore di inizializzazione: "+e.getMessage(), e);
			throw new DriverControlStationException(e.getMessage(),e);
		}
	}

	public ControlStationCore(ControlStationCore core) throws DriverControlStationException {

		/** Impostazioni grafiche */
		this.consoleNomeSintesi = core.consoleNomeSintesi;
		this.consoleNomeEsteso = core.consoleNomeEsteso;
		this.consoleCSS = core.consoleCSS;
		this.consoleLanguage = core.consoleLanguage;
		this.consoleLunghezzaLabel = core.consoleLunghezzaLabel;
		this.logoHeaderImage = core.logoHeaderImage;
		this.logoHeaderLink = core.logoHeaderLink;
		this.logoHeaderTitolo = core.logoHeaderTitolo;
		this.visualizzaLinkHomeHeader = core.visualizzaLinkHomeHeader;
		this.defaultFont = core.defaultFont;
		this.affineTransform = core.affineTransform;
		this.fontRenderContext = core.fontRenderContext;

		/** Tipo del Database */
		this.tipoDB = core.tipoDB;

		/** IDFactory */
		this.idAccordoFactory = core.idAccordoFactory;
		this.idAccordoCooperazioneFactory = core.idAccordoCooperazioneFactory;
		this.idServizioFactory = core.idServizioFactory;

		/** Protocollo */
		this.protocolloDefault = core.protocolloDefault;
		this.jdbcSerializableAttesaAttiva = core.jdbcSerializableAttesaAttiva;
		this.jdbcSerializableCheck = core.jdbcSerializableCheck;
		this.protocolFactoryManager = core.protocolFactoryManager;

		/** Visione oggetti globale o per utenti */
		this.visioneOggettiGlobale = core.visioneOggettiGlobale;
		this.utentiConVisioneGlobale = core.utentiConVisioneGlobale;

		/** Tracciamento */
		this.tracceShowConfigurazioneCustomAppender = core.tracceShowConfigurazioneCustomAppender;
		this.tracceSameDBWebUI = core.tracceSameDBWebUI;
		this.tracceShowSorgentiDatiDatabase = core.tracceShowSorgentiDatiDatabase;
		this.tracceDatasource = core.tracceDatasource;
		this.tracceTipoDatabase = core.tracceTipoDatabase;
		this.tracceCtxDatasource = core.tracceCtxDatasource;
		this.driverTracciamento = core.driverTracciamento;

		/** MsgDiagnostici */
		this.msgDiagnosticiShowConfigurazioneCustomAppender = core.msgDiagnosticiShowConfigurazioneCustomAppender;
		this.msgDiagnosticiSameDBWebUI = core.msgDiagnosticiSameDBWebUI;
		this.msgDiagnosticiShowSorgentiDatiDatabase = core.msgDiagnosticiShowSorgentiDatiDatabase;
		this.msgDiagnosticiDatasource = core.msgDiagnosticiDatasource;
		this.msgDiagnosticiTipoDatabase = core.msgDiagnosticiTipoDatabase;
		this.msgDiagnosticiCtxDatasource = core.msgDiagnosticiCtxDatasource;
		this.driverMSGDiagnostici = core.driverMSGDiagnostici;
		
		/** Dump */
		this.dumpShowConfigurazioneCustomAppender = core.dumpShowConfigurazioneCustomAppender;
		this.dumpShowConfigurazioneDumpRealtime = core.dumpShowConfigurazioneDumpRealtime;

		/** Gestione Pdd Abilitata */
		this.gestionePddAbilitata = core.gestionePddAbilitata;
		
		/** Registro Servizi locale/remoto */
		this.registroServiziLocale = core.registroServiziLocale;

		/** Modalita' Single PdD */
		this.singlePdD = core.singlePdD;

		/** J2EE Ambiente */
		this.showJ2eeOptions = core.showJ2eeOptions;

		/** Utenze Console */
		this.utenzePasswordConfiguration = core.utenzePasswordConfiguration;
		this.utenzeLunghezzaPasswordGenerate = core.utenzeLunghezzaPasswordGenerate;
		
		/** Utenze Console */
		this.utenzePasswordConfiguration = core.utenzePasswordConfiguration;
		this.utenzeLunghezzaPasswordGenerate = core.utenzeLunghezzaPasswordGenerate;
		this.utenzePasswordVerifierEngine = core.utenzePasswordVerifierEngine;
		this.utenzePasswordEncryptEngine = core.utenzePasswordEncryptEngine;
		this.utenzePasswordManager = core.utenzePasswordManager;
		this.utenzePasswordManagerBackwardCompatibility = core.utenzePasswordManagerBackwardCompatibility;
		this.utenzeModificaProfiloUtenteDaFormAggiornaSessione = core.utenzeModificaProfiloUtenteDaFormAggiornaSessione;
		this.utenzeModificaProfiloUtenteDaLinkAggiornaDB = core.utenzeModificaProfiloUtenteDaLinkAggiornaDB;
		
		/** Login */
		this.loginApplication = core.loginApplication;
		this.loginErroreInternoRedirectUrl = core.loginErroreInternoRedirectUrl;
		this.loginProperties = core.loginProperties;
		this.loginSessioneScadutaRedirectUrl = core.loginSessioneScadutaRedirectUrl;
		this.loginTipo = core.loginTipo;
		this.loginUtenteNonAutorizzatoRedirectUrl = core.loginUtenteNonAutorizzatoRedirectUrl;
		this.loginUtenteNonValidoRedirectUrl = core.loginUtenteNonValidoRedirectUrl;
		this.logoutMostraButton = core.logoutMostraButton;
		this.logoutUrlDestinazione = core.logoutUrlDestinazione;
		
		/** Applicativi Console */
		this.applicativiPwConfiguration = core.applicativiPwConfiguration;
		this.applicativiBasicPwEnableConstraints = core.applicativiBasicPwEnableConstraints;
		this.applicativiBasicLunghezzaPwGenerate = core.applicativiBasicLunghezzaPwGenerate;
		this.applicativiApiKeyLunghezzaPwGenerate = core.applicativiApiKeyLunghezzaPwGenerate;
		this.applicativiPwVerifierEngine = core.applicativiPwVerifierEngine;
		this.applicativiPwEncryptEngine = core.applicativiPwEncryptEngine;
		this.applicativiPwManager = core.applicativiPwManager;
		
		/** Soggetti Console */
		this.soggettiPwConfiguration = core.soggettiPwConfiguration;
		this.soggettiBasicPwEnableConstraints = core.soggettiBasicPwEnableConstraints;
		this.soggettiBasicLunghezzaPwGenerate = core.soggettiBasicLunghezzaPwGenerate;
		this.soggettiApiKeyLunghezzaPwGenerate = core.soggettiApiKeyLunghezzaPwGenerate;
		this.soggettiPwVerifierEngine = core.soggettiPwVerifierEngine;
		this.soggettiPwEncryptEngine = core.soggettiPwEncryptEngine;
		this.soggettiPwManager = core.soggettiPwManager;
		
		/** MessageSecurity PropertiesSourceConfiguration */
		this.messageSecurityPropertiesSourceConfiguration = core.messageSecurityPropertiesSourceConfiguration;
		
		/** PolicyGestioneToken PropertiesSourceConfiguration */
		this.policyGestioneTokenPropertiesSourceConfiguration = core.policyGestioneTokenPropertiesSourceConfiguration;
		this.isPolicyGestioneTokenVerificaCertificati = core.isPolicyGestioneTokenVerificaCertificati; 
		this.policyGestioneTokenPDND = core.policyGestioneTokenPDND;
		
		/** AttributeAuthority PropertiesSourceConfiguration */
		this.attributeAuthorityPropertiesSourceConfiguration = core.attributeAuthorityPropertiesSourceConfiguration;
		this.isAttributeAuthorityVerificaCertificati = core.isAttributeAuthorityVerificaCertificati; 
		
		/** ControlloTraffico */
		this.isControlloTrafficoPolicyGlobaleGroupByApi = core.isControlloTrafficoPolicyGlobaleGroupByApi;
		this.isControlloTrafficoPolicyGlobaleFiltroApi = core.isControlloTrafficoPolicyGlobaleFiltroApi;
		this.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore = core.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore;
		this.controlloTrafficoPolicyRateLimitingTipiGestori = core.controlloTrafficoPolicyRateLimitingTipiGestori;
		
		/** Auditing */
		this.isAuditingRegistrazioneElementiBinari = core.isAuditingRegistrazioneElementiBinari;
		
		/** IntegrationManager */
		this.isIntegrationManagerEnabled = core.isIntegrationManagerEnabled;
		this.isIntegrationManagerTraceMessageBoxOperationEnabled = core.isIntegrationManagerTraceMessageBoxOperationEnabled;
		
		/** Soggetti */
		this.soggettiNomeMaxLength = core.soggettiNomeMaxLength;
		this.isSoggettiVerificaCertificati = core.isSoggettiVerificaCertificati;
		
		/** Applicativi */
		this.isApplicativiVerificaCertificati = core.isApplicativiVerificaCertificati;
		
		/** API */
		this.isApiResourcePathValidatorEnabled = core.isApiResourcePathValidatorEnabled;
		this.isApiResourceHttpMethodAndPathQualsiasiEnabled = core.isApiResourceHttpMethodAndPathQualsiasiEnabled;
		this.getApiResourcePathQualsiasiSpecialChar = core.getApiResourcePathQualsiasiSpecialChar;
		this.isApiOpenAPIValidateUriReferenceAsUrl = core.isApiOpenAPIValidateUriReferenceAsUrl;
		this.isApiRestResourceRepresentationMessageTypeOverride = core.isApiRestResourceRepresentationMessageTypeOverride;
		this.isApiDescriptionTruncate255 = core.isApiDescriptionTruncate255;
		this.isApiDescriptionTruncate4000 = core.isApiDescriptionTruncate4000;
		
		/** Accordi di Cooperazione */
		this.isAccordiCooperazioneEnabled = core.isAccordiCooperazioneEnabled;
		
		/** API Impl */
		this.isErogazioniVerificaCertificati = core.isErogazioniVerificaCertificati;
		this.isFruizioniVerificaCertificati = core.isFruizioniVerificaCertificati;
		
		/** Message Engine */
		this.messageEngines = core.messageEngines;
		
		/** Credenziali Basic */
		this.isSoggettiCredenzialiBasicCheckUniqueUsePassword = core.isSoggettiCredenzialiBasicCheckUniqueUsePassword;
		this.isApplicativiCredenzialiBasicCheckUniqueUsePassword = core.isApplicativiCredenzialiBasicCheckUniqueUsePassword;
		this.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials = core.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials;
		
		/** Credenziali Ssl */
		this.isSoggettiApplicativiCredenzialiSslPermitSameCredentials = core.isSoggettiApplicativiCredenzialiSslPermitSameCredentials;
		
		/** Credenziali Principal */
		this.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials = core.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials;

		/** Connettori */
		this.isConnettoriAllTypesEnabled = core.isConnettoriAllTypesEnabled;
		
		/** Connettori Multipli */
		this.isConnettoriMultipliEnabled = core.isConnettoriMultipliEnabled;
		this.isConnettoriMultipliConsegnaCondizionaleStessFiltroPermesso = core.isConnettoriMultipliConsegnaCondizionaleStessFiltroPermesso; 
		this.isConnettoriMultipliConsegnaMultiplaEnabled = core.isConnettoriMultipliConsegnaMultiplaEnabled;
		
		/** Applicativi Server */
		this.isApplicativiServerEnabled = core.isApplicativiServerEnabled;		
		
		/** Gestione Consegne Asincrone */
		this.consegnaNotificaCode = core.consegnaNotificaCode;
		this.consegnaNotificaCodaLabel = core.consegnaNotificaCodaLabel;
		this.consegnaNotificaPriorita = core.consegnaNotificaPriorita;
		this.consegnaNotificaConfigurazionePriorita = core.consegnaNotificaConfigurazionePriorita;
		
		/** ModI */
		this.isModipaErogazioniVerificaCertificati = core.isModipaErogazioniVerificaCertificati;
		this.isModipaFruizioniVerificaCertificati = core.isModipaFruizioniVerificaCertificati;
		this.isModipaFruizioniConnettoreCheckHttps = core.isModipaFruizioniConnettoreCheckHttps;
		this.isModipaFiltroRicercaProfiloQualsiasiVisualizzaDatiModi = core.isModipaFiltroRicercaProfiloQualsiasiVisualizzaDatiModi;
		
		/** Plugins */
		this.configurazionePluginsEnabled = core.configurazionePluginsEnabled;
		this.configurazionePluginsSeconds = core.configurazionePluginsSeconds;
		
		/** Handlers */
		this.configurazioneHandlersEnabled = core.configurazioneHandlersEnabled;
		
		/** Configurazione Allarmi */
		this.configurazioneAllarmiEnabled = core.configurazioneAllarmiEnabled;
		this.allarmiConfig = core.allarmiConfig;
		this.showAllarmiIdentificativoRuntime = core.showAllarmiIdentificativoRuntime;
		this.showAllarmiFormNomeSuggeritoCreazione = core.showAllarmiFormNomeSuggeritoCreazione;
		this.showAllarmiFormStatoAllarme = core.showAllarmiFormStatoAllarme;
		this.showAllarmiFormStatoAllarmeHistory = core.showAllarmiFormStatoAllarmeHistory;
		this.showAllarmiSearchStatiAllarmi = core.showAllarmiSearchStatiAllarmi;
		this.showAllarmiElenchiStatiAllarmi = core.showAllarmiElenchiStatiAllarmi;
		
		/** Registrazione Messaggi */
		this.isRegistrazioneMessaggiMultipartPayloadParsingEnabled = core.isRegistrazioneMessaggiMultipartPayloadParsingEnabled;
		
		/** Cluster dinamico */
		this.isClusterDinamicoEnabled = core.isClusterDinamicoEnabled;
		
		/** OCSP */
		this.isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata = core.isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata; 
		
		/** Certificati */
		this.verificaCertificatiWarningExpirationDays = core.verificaCertificatiWarningExpirationDays;
		this.verificaCertificatiSceltaClusterId = core.verificaCertificatiSceltaClusterId; 
		
		/** Cluster */
		this.isClusterAsyncUpdate = core.isClusterAsyncUpdate;
		this.clusterAsyncUpdateCheckInterval = core.clusterAsyncUpdateCheckInterval;
		
		/** Opzioni di visualizzazione */
		this.showCorrelazioneAsincronaInAccordi = core.showCorrelazioneAsincronaInAccordi;
		this.showFlagPrivato = core.showFlagPrivato;
		this.showAllConnettori = core.showAllConnettori;
		this.showDebugOptionConnettore = core.showDebugOptionConnettore;
		this.showPulsantiImportExport = core.showPulsantiImportExport;
		this.elenchiMenuIdentificativiLunghezzaMassima = core.elenchiMenuIdentificativiLunghezzaMassima;
		this.showCountElementInLinkList = core.showCountElementInLinkList;
		this.conservaRisultatiRicerca = core.conservaRisultatiRicerca;
		this.showAccordiColonnaAzioni = core.showAccordiColonnaAzioni;
		this.showAccordiInformazioniProtocollo = core.showAccordiInformazioniProtocollo;
		this.showConfigurazioniPersonalizzate = core.showConfigurazioniPersonalizzate;
		this.showGestioneSoggettiRouter = core.showGestioneSoggettiRouter;
		this.showGestioneSoggettiVirtuali = core.showGestioneSoggettiVirtuali;
		this.showGestioneWorkflowStatoDocumenti = core.showGestioneWorkflowStatoDocumenti;
		this.gestioneWorkflowStatoDocumentiVisualizzaStatoLista = core.gestioneWorkflowStatoDocumentiVisualizzaStatoLista;
		this.gestioneWorkflowStatoDocumentiRipristinoStatoOperativoDaFinale = core.gestioneWorkflowStatoDocumentiRipristinoStatoOperativoDaFinale;
		this.showInterfacceAPI = core.showInterfacceAPI;
		this.showAllegati = core.showAllegati;
		this.enableAutoMappingWsdlIntoAccordo = core.enableAutoMappingWsdlIntoAccordo;
		this.enableAutoMappingWsdlIntoAccordoEstrazioneSchemiInWsdlTypes = core.enableAutoMappingWsdlIntoAccordoEstrazioneSchemiInWsdlTypes;
		this.showMTOMVisualizzazioneCompleta = core.showMTOMVisualizzazioneCompleta;
		this.portaCorrelazioneApplicativaMaxLength = core.portaCorrelazioneApplicativaMaxLength;
		this.showPortaDelegataLocalForward = core.showPortaDelegataLocalForward;
		this.isProprietaErogazioniShowModalitaStandard = core.isProprietaErogazioniShowModalitaStandard;
		this.isProprietaFruizioniShowModalitaStandard = core.isProprietaFruizioniShowModalitaStandard;
		this.isPortTypeObbligatorioImplementazioniSOAP = core.isPortTypeObbligatorioImplementazioniSOAP;
		this.isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona = core.isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona;
		this.isVisualizzazioneConfigurazioneDiagnosticaLog4J = core.isVisualizzazioneConfigurazioneDiagnosticaLog4J;
		this.tokenPolicyForceId = core.tokenPolicyForceId;
		this.tokenPolicyForceIdEnabled = core.tokenPolicyForceIdEnabled;
		this.tokenPolicyTipologia = core.tokenPolicyTipologia;
		this.attributeAuthorityForceId = core.attributeAuthorityForceId;
		this.attributeAuthorityForceIdEnabled = core.attributeAuthorityForceIdEnabled;
		this.attributeAuthorityTipologia = core.attributeAuthorityTipologia;
		this.showServiziVisualizzaModalitaElenco = core.showServiziVisualizzaModalitaElenco;
		this.selectListSoggettiOperativiNumeroMassimoSoggetti = core.selectListSoggettiOperativiNumeroMassimoSoggetti;
		this.selectListSoggettiOperativiDimensioneMassimaLabel = core.selectListSoggettiOperativiDimensioneMassimaLabel;
		this.viewLunghezzaMassimaInformazione = core.viewLunghezzaMassimaInformazione;
		this.isSetSearchAfterAdd = core.isSetSearchAfterAdd;
		this.elenchiVisualizzaComandoResetCacheSingoloElemento = core.elenchiVisualizzaComandoResetCacheSingoloElemento;
		this.validitaTokenCsrf = core.validitaTokenCsrf;
		this.cspHeaderValue = core.cspHeaderValue;
		this.xContentTypeOptionsHeaderValue = core.xContentTypeOptionsHeaderValue;
		this.xFrameOptionsHeaderValue = core.xFrameOptionsHeaderValue;
		this.xXssProtectionHeaderValue = core.xXssProtectionHeaderValue;
		this.consoleSecurityConfiguration = core.consoleSecurityConfiguration;

		/** Opzioni di importazione/esportazione Archivi */
		this.importArchivi_tipoPdD = core.importArchivi_tipoPdD;
		this.exportArchive_configurazione_soloDumpCompleto = core.exportArchive_configurazione_soloDumpCompleto;
		this.exportArchive_servizi_standard = core.exportArchive_servizi_standard;
		
		/** Multitenant */
		this.multitenant = core.multitenant;
		this.multitenantSoggettiErogazioni = core.multitenantSoggettiErogazioni;
		this.multitenantSoggettiFruizioni = core.multitenantSoggettiFruizioni;
		
		/** Altro */
		this.enabledToken_generazioneAutomaticaPorteDelegate = core.enabledToken_generazioneAutomaticaPorteDelegate;
		this.enabledAutenticazione_generazioneAutomaticaPorteDelegate = core.enabledAutenticazione_generazioneAutomaticaPorteDelegate;
		this.autenticazione_generazioneAutomaticaPorteDelegate = core.autenticazione_generazioneAutomaticaPorteDelegate;
		this.enabledAutorizzazione_generazioneAutomaticaPorteDelegate = core.enabledAutorizzazione_generazioneAutomaticaPorteDelegate;
		this.autorizzazione_generazioneAutomaticaPorteDelegate = core.autorizzazione_generazioneAutomaticaPorteDelegate;		
		this.enabledToken_generazioneAutomaticaPorteApplicative = core.enabledToken_generazioneAutomaticaPorteApplicative;
		this.enabledAutenticazione_generazioneAutomaticaPorteApplicative = core.enabledAutenticazione_generazioneAutomaticaPorteApplicative;
		this.autenticazione_generazioneAutomaticaPorteApplicative = core.autenticazione_generazioneAutomaticaPorteApplicative;
		this.enabledAutorizzazione_generazioneAutomaticaPorteApplicative = core.enabledAutorizzazione_generazioneAutomaticaPorteApplicative;
		this.autorizzazione_generazioneAutomaticaPorteApplicative = core.autorizzazione_generazioneAutomaticaPorteApplicative;		
		this.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto = core.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto;
		this.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto = core.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto;
		
		/** Opzioni per Plugins */
		this.pluginMenu = core.pluginMenu;
		this.pluginConfigurazione = core.pluginConfigurazione;
		this.pluginConfigurazioneList = core.pluginConfigurazioneList;
		this.pluginConnettore = core.pluginConnettore;
		this.pluginPortaDelegata = core.pluginPortaDelegata;
		this.pluginPortaApplicativa = core.pluginPortaApplicativa;
		
		/** Opzioni Accesso JMX della PdD */
		this.invoker = core.invoker;
		this.configurazioneNodiRuntime = core.configurazioneNodiRuntime;
		this.isVisualizzaLinkClearAllCachesRemoteCheckCacheStatus = core.isVisualizzaLinkClearAllCachesRemoteCheckCacheStatus;
		this.jmxPdDAliases = core.jmxPdDAliases;
		this.jmxPdDGruppiAliases = core.jmxPdDGruppiAliases;
		this.jmxPdDDescrizioni = core.jmxPdDDescrizioni;
		this.jmxPdDCertificateChecker = core.jmxPdDCertificateChecker;
		this.jmxPdDConfigurazioneSistemaType = core.jmxPdDConfigurazioneSistemaType;
		this.jmxPdDConfigurazioneSistemaNomeRisorsa = core.jmxPdDConfigurazioneSistemaNomeRisorsa;
		this.jmxPdDConfigurazioneSistemaNomeMetodoVersionePdD = core.jmxPdDConfigurazioneSistemaNomeMetodoVersionePdD;
		this.jmxPdDConfigurazioneSistemaNomeMetodoVersioneBaseDati = core.jmxPdDConfigurazioneSistemaNomeMetodoVersioneBaseDati;
		this.jmxPdDConfigurazioneSistemaNomeMetodoVersioneJava = core.jmxPdDConfigurazioneSistemaNomeMetodoVersioneJava;
		this.jmxPdDConfigurazioneSistemaNomeMetodoVendorJava = core.jmxPdDConfigurazioneSistemaNomeMetodoVendorJava;
		this.jmxPdDConfigurazioneSistemaNomeMetodoTipoDatabase = core.jmxPdDConfigurazioneSistemaNomeMetodoTipoDatabase;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniDatabase = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniDatabase;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniSSL = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniSSL;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteSSL = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteSSL;
		this.jmxPdDConfigurazioneSistemaShowInformazioniCryptographyKeyLength = core.jmxPdDConfigurazioneSistemaShowInformazioniCryptographyKeyLength;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCryptographyKeyLength = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCryptographyKeyLength;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCharset = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCharset;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniInternazionalizzazione = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniInternazionalizzazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteInternazionalizzazione = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteInternazionalizzazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniTimeZone = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniTimeZone;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteTimeZone = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteTimeZone;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaNetworking = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaNetworking;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteProprietaJavaNetworking = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteProprietaJavaNetworking;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaAltro = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaAltro;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaSistema = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaSistema;
		this.jmxPdDConfigurazioneSistemaNomeMetodoMessageFactory = core.jmxPdDConfigurazioneSistemaNomeMetodoMessageFactory;
		this.jmxPdDConfigurazioneSistemaNomeMetodoDirectoryConfigurazione = core.jmxPdDConfigurazioneSistemaNomeMetodoDirectoryConfigurazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoPluginProtocols = core.jmxPdDConfigurazioneSistemaNomeMetodoPluginProtocols;
		this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniInstallazione = core.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniInstallazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace = core.jmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace;
		this.jmxPdDConfigurazioneSistemaNomeMetodoUpdateFileTrace = core.jmxPdDConfigurazioneSistemaNomeMetodoUpdateFileTrace;
		this.jmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio = core.jmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio;
		this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniDB = core.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniDB;
		this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniJMS = core.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniJMS;
		this.jmxPdDConfigurazioneSistemaNomeMetodoIdTransazioniAttive = core.jmxPdDConfigurazioneSistemaNomeMetodoIdTransazioniAttive;
		this.jmxPdDConfigurazioneSistemaNomeMetodoIdProtocolloTransazioniAttive = core.jmxPdDConfigurazioneSistemaNomeMetodoIdProtocolloTransazioniAttive;
		this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniPD = core.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniPD;
		this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniPA = core.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniPA;
		this.jmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD = core.jmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD;
		this.jmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnostici = core.jmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnostici;
		this.jmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnosticiLog4j = core.jmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnosticiLog4j;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTracciamento = core.jmxPdDConfigurazioneSistemaNomeAttributoTracciamento;
		this.jmxPdDConfigurazioneSistemaNomeAttributoDumpPD = core.jmxPdDConfigurazioneSistemaNomeAttributoDumpPD;
		this.jmxPdDConfigurazioneSistemaNomeAttributoDumpPA = core.jmxPdDConfigurazioneSistemaNomeAttributoDumpPA;
		this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jDiagnostica = core.jmxPdDConfigurazioneSistemaNomeAttributoLog4jDiagnostica;
		this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jOpenspcoop = core.jmxPdDConfigurazioneSistemaNomeAttributoLog4jOpenspcoop;
		this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jIntegrationManager = core.jmxPdDConfigurazioneSistemaNomeAttributoLog4jIntegrationManager;
		this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jTracciamento = core.jmxPdDConfigurazioneSistemaNomeAttributoLog4jTracciamento;
		this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jDump = core.jmxPdDConfigurazioneSistemaNomeAttributoLog4jDump;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorStatusCode = core.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorStatusCode;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorInstanceId = core.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorInstanceId;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeBadResponse = core.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeBadResponse;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalResponseError = core.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalResponseError;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalRequestError = core.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalRequestError;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalError = core.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalError;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorDetails = core.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorDetails;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorUseStatusCodeAsFaultCode = core.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorUseStatusCodeAsFaultCode;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorGenerateHttpHeaderGovWayCode = core.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorGenerateHttpHeaderGovWayCode;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerConsegnaContenutiApplicativi = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerConsegnaContenutiApplicativi;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerEventi = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerEventi;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerFileSystemRecovery = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerFileSystemRecovery;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteOnewayNonRiscontrate = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteOnewayNonRiscontrate;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteAsincroneNonRiscontrate = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteAsincroneNonRiscontrate;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiEliminati = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiEliminati;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiScaduti = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiScaduti;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiNonGestiti = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiNonGestiti;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaCorrelazioneApplicativa = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaCorrelazioneApplicativa;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiVerificaConnessioniAttive = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiVerificaConnessioniAttive;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestorePuliziaMessaggiAnomali = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestorePuliziaMessaggiAnomali;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreRepositoryBuste = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreRepositoryBuste;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerMonitoraggioRisorseThread = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerMonitoraggioRisorseThread;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerRepositoryStatefulThread = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerRepositoryStatefulThread;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheOrarie = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheOrarie;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheGiornaliere = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheGiornaliere;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheSettimanali = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheSettimanali;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheMensili = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheMensili;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreChiaviPDND = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreChiaviPDND;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreCacheChiaviPDND = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreCacheChiaviPDND;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreOperazioniRemote = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreOperazioniRemote;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerSvecchiamentoOperazioniRemote = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerSvecchiamentoOperazioniRemote;
		this.jmxPdDConfigurazioneSistemaNomeAttributoTimerThresholdThread = core.jmxPdDConfigurazioneSistemaNomeAttributoTimerThresholdThread;
		this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById = core.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreById = core.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreById;
		this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyValidazione = core.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyValidazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyNegoziazione = core.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyNegoziazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreAttributeAuthority = core.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreAttributeAuthority;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyValidazione = core.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyValidazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyNegoziazione = core.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyNegoziazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreAttributeAuthority = core.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreAttributeAuthority;
		this.jmxPdDConfigurazioneSistemaNomeMetodoEnablePortaDelegata = core.jmxPdDConfigurazioneSistemaNomeMetodoEnablePortaDelegata;
		this.jmxPdDConfigurazioneSistemaNomeMetodoDisablePortaDelegata = core.jmxPdDConfigurazioneSistemaNomeMetodoDisablePortaDelegata;
		this.jmxPdDConfigurazioneSistemaNomeMetodoEnablePortaApplicativa = core.jmxPdDConfigurazioneSistemaNomeMetodoEnablePortaApplicativa;
		this.jmxPdDConfigurazioneSistemaNomeMetodoDisablePortaApplicativa = core.jmxPdDConfigurazioneSistemaNomeMetodoDisablePortaApplicativa;
		this.jmxPdDConfigurazioneSistemaNomeMetodoEnableConnettoreMultiplo = core.jmxPdDConfigurazioneSistemaNomeMetodoEnableConnettoreMultiplo;
		this.jmxPdDConfigurazioneSistemaNomeMetodoDisableConnettoreMultiplo = core.jmxPdDConfigurazioneSistemaNomeMetodoDisableConnettoreMultiplo;
		this.jmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiplo = core.jmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiplo;
		this.jmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiplo = core.jmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiplo;
		this.jmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiploRuntimeRepository = core.jmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiploRuntimeRepository;
		this.jmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiploRuntimeRepository = core.jmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiploRuntimeRepository;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAccordoCooperazione = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAccordoCooperazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApi = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApi;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheErogazione = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheErogazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheFruizione = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheFruizione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheSoggetto = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheSoggetto;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApplicativo = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApplicativo;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheRuolo = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheRuolo;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheScope = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheScope;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyValidazione = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyValidazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyNegoziazione = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyNegoziazione;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAttributeAuthority = core.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAttributeAuthority;
		this.jmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi = core.jmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi;
		this.jmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD = core.jmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD;
		this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegata = core.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegata;
		this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataAbilitazioniPuntuali = core.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataAbilitazioniPuntuali;
		this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataDisabilitazioniPuntuali = core.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataDisabilitazioniPuntuali;
		this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativa = core.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativa;
		this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaAbilitazioniPuntuali = core.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaAbilitazioniPuntuali;
		this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaDisabilitazioniPuntuali = core.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaDisabilitazioniPuntuali;
		this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioIntegrationManager = core.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioIntegrationManager;
		this.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaDelegata = core.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaDelegata;
		this.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaDelegata = core.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaDelegata;
		this.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaApplicativa = core.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaApplicativa;
		this.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaApplicativa = core.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaApplicativa;
		this.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioIntegrationManager = core.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioIntegrationManager;
		this.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioIntegrationManager = core.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioIntegrationManager;
		this.jmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW = core.jmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW;
		this.jmxPdDConfigurazioneSistemaNomeAttributoNumeroDatasourceGW = core.jmxPdDConfigurazioneSistemaNomeAttributoNumeroDatasourceGW;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetDatasourcesGW = core.jmxPdDConfigurazioneSistemaNomeMetodoGetDatasourcesGW;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetUsedConnectionsDatasourcesGW = core.jmxPdDConfigurazioneSistemaNomeMetodoGetUsedConnectionsDatasourcesGW;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetInformazioniDatabaseDatasourcesGW = core.jmxPdDConfigurazioneSistemaNomeMetodoGetInformazioniDatabaseDatasourcesGW;
		this.jmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi = core.jmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetThreadPoolStatus = core.jmxPdDConfigurazioneSistemaNomeMetodoGetThreadPoolStatus;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetQueueConfig = core.jmxPdDConfigurazioneSistemaNomeMetodoGetQueueConfig;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetApplicativiPrioritari = core.jmxPdDConfigurazioneSistemaNomeMetodoGetApplicativiPrioritari;
		this.jmxPdDConfigurazioneSistemaNomeMetodoGetConnettoriPrioritari = core.jmxPdDConfigurazioneSistemaNomeMetodoGetConnettoriPrioritari;
		this.jmxPdDConfigurazioneSistemaNomeMetodoUpdateConnettoriPrioritari = core.jmxPdDConfigurazioneSistemaNomeMetodoUpdateConnettoriPrioritari;
		this.jmxPdDConfigurazioneSistemaNomeMetodoResetConnettoriPrioritari = core.jmxPdDConfigurazioneSistemaNomeMetodoResetConnettoriPrioritari;
		this.jmxPdDConfigurazioneSistemaNomeRisorsaSystemPropertiesPdD = core.jmxPdDConfigurazioneSistemaNomeRisorsaSystemPropertiesPdD;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRefreshPersistentConfiguration = core.jmxPdDConfigurazioneSistemaNomeMetodoRefreshPersistentConfiguration;
		this.jmxPdDConfigurazioneSistemaNomeRisorsaDatiRichieste = core.jmxPdDConfigurazioneSistemaNomeRisorsaDatiRichieste;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingGlobalConfigCache = core.jmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingGlobalConfigCache;
		this.jmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingAPIConfigCache = core.jmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingAPIConfigCache;
		this.jmxPdDCaches = core.jmxPdDCaches;
		this.jmxPdDCachesPrefill = core.jmxPdDCachesPrefill;
		this.jmxPdDCacheType = core.jmxPdDCacheType;
		this.jmxPdDCacheNomeAttributoCacheAbilitata = core.jmxPdDCacheNomeAttributoCacheAbilitata;
		this.jmxPdDCacheNomeMetodoStatoCache = core.jmxPdDCacheNomeMetodoStatoCache;
		this.jmxPdDCacheNomeMetodoResetCache = core.jmxPdDCacheNomeMetodoResetCache;
		this.jmxPdDCacheNomeMetodoPrefillCache = core.jmxPdDCacheNomeMetodoPrefillCache;
	}




	/* --- INIT METHOD --- */

	/**
	 * Prova ad ottenere una istanza del DBManager per utilizzare le connessioni
	 * del pool
	 * 
	 * @throws DriverControlStationException
	 */
	private void initConnections() throws ControlStationCoreException {

		// Connessione al DB
		DatasourceProperties datasourceProperties = null;
		String jndiName = null;
		Properties jndiProp = null;
		try {
			datasourceProperties = DatasourceProperties.getInstance();
			
			jndiName = datasourceProperties.getDataSource();
			jndiProp = datasourceProperties.getDataSourceContext();
			
			this.tipoDB = datasourceProperties.getTipoDatabase();
			
			if(this.singlePdD){
				this.tracceSameDBWebUI = datasourceProperties.isSinglePddTracceStessoDBConsole();
				if(!this.tracceSameDBWebUI){
					this.tracceDatasource = datasourceProperties.getSinglePddTracceDataSource();
					this.tracceCtxDatasource = datasourceProperties.getSinglePddTracceDataSourceContext();
					this.tracceTipoDatabase = datasourceProperties.getSinglePddTracceTipoDatabase();
				}
				
				this.msgDiagnosticiSameDBWebUI = datasourceProperties.isSinglePddMessaggiDiagnosticiStessoDBConsole();
				if(!this.msgDiagnosticiSameDBWebUI){
					this.msgDiagnosticiDatasource = datasourceProperties.getSinglePddMessaggiDiagnosticiDataSource();
					this.msgDiagnosticiCtxDatasource = datasourceProperties.getSinglePddMessaggiDiagnosticiDataSourceContext();
					this.msgDiagnosticiTipoDatabase = datasourceProperties.getSinglePddMessaggiDiagnosticiTipoDatabase();
				}
			}
			
		} catch (java.lang.Exception e) {
			ControlStationCore.logError("[ControlStationCore::initConnections] Impossibile leggere i dati dal file console.datasource.properties[" + e.toString() + "]",e);
			throw new ControlStationCoreException("Impossibile leggere i dati dal file console.datasource.properties: " + e.getMessage(),e);
		} 

		if (!DBManager.isInitialized()) {
			int i = 0;
			while (!DBManager.isInitialized() && (i < 6)) {

				try {
					ControlStationCore.logDebug("jndiName=" + jndiName);
					ControlStationCore.logDebug("jndiProp=" + jndiProp.toString());
					DBManager.initialize(jndiName, jndiProp);
					ControlStationCore.logInfo("Inizializzazione DBManager Effettuata.");
				} catch (Exception e) {
					ControlStationCore.logError("Inizializzazione DBManager fallita.", e);
					ControlStationCore.logInfo("Ritento inizializzazione ...");
				}

				i++;
			}
		}

		if (!DBManager.isInitialized()) {
			throw new ControlStationCoreException("Inizializzazione DBManager fallita ripetutamente.");
		} 

		ControlStationCore.dbM = DBManager.getInstance();

	}

	private void initCore() throws ControlStationCoreException {
		this.tipoDB = "";

			
		// Leggo le informazioni da console.properties
		ConsoleProperties consoleProperties = null;
		try {
			consoleProperties = ConsoleProperties.getInstance();
			
			// Funzionalità Generiche
			this.protocolloDefault = consoleProperties.getProtocolloDefault();
			this.jdbcSerializableAttesaAttiva = consoleProperties.getGestioneSerializableDBattesaAttiva();
			this.jdbcSerializableCheck = consoleProperties.getGestioneSerializableDBcheckInterval();
			this.singlePdD = consoleProperties.isSinglePdD();
			this.enabledToken_generazioneAutomaticaPorteDelegate = consoleProperties.isTokenGenerazioneAutomaticaPorteDelegateEnabled();
			this.enabledAutenticazione_generazioneAutomaticaPorteDelegate = consoleProperties.isAutenticazioneGenerazioneAutomaticaPorteDelegateEnabled();
			this.autenticazione_generazioneAutomaticaPorteDelegate = consoleProperties.getAutenticazioneGenerazioneAutomaticaPorteDelegate();
			this.enabledAutorizzazione_generazioneAutomaticaPorteDelegate = consoleProperties.isAutorizzazioneGenerazioneAutomaticaPorteDelegateEnabled();
			this.autorizzazione_generazioneAutomaticaPorteDelegate = consoleProperties.getAutorizzazioneGenerazioneAutomaticaPorteDelegate();		
			this.enabledToken_generazioneAutomaticaPorteApplicative = consoleProperties.isTokenGenerazioneAutomaticaPorteApplicativeEnabled();
			this.enabledAutenticazione_generazioneAutomaticaPorteApplicative = consoleProperties.isAutenticazioneGenerazioneAutomaticaPorteApplicativeEnabled();
			this.autenticazione_generazioneAutomaticaPorteApplicative = consoleProperties.getAutenticazioneGenerazioneAutomaticaPorteApplicative();
			this.enabledAutorizzazione_generazioneAutomaticaPorteApplicative = consoleProperties.isAutorizzazioneGenerazioneAutomaticaPorteApplicativeEnabled();
			this.autorizzazione_generazioneAutomaticaPorteApplicative = consoleProperties.getAutorizzazioneGenerazioneAutomaticaPorteApplicative();
			this.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto = consoleProperties.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto();
			this.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto = consoleProperties.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto();
			this.utenzePasswordConfiguration = consoleProperties.getConsoleUtenzePassword();
			this.utenzeLunghezzaPasswordGenerate = consoleProperties.getConsoleUtenzeLunghezzaPasswordGenerate();
			this.utenzeModificaProfiloUtenteDaFormAggiornaSessione = consoleProperties.isConsoleUtenzeModificaProfiloUtenteDaFormAggiornaSessione();
			this.utenzeModificaProfiloUtenteDaLinkAggiornaDB = consoleProperties.isConsoleUtenzeModificaProfiloUtenteDaLinkAggiornaDB();
			this.applicativiPwConfiguration = consoleProperties.getConsoleApplicativiPassword();
			this.applicativiBasicPwEnableConstraints = consoleProperties.isConsoleApplicativiBasicPasswordEnableConstraints();
			this.applicativiBasicLunghezzaPwGenerate = consoleProperties.getConsoleApplicativiBasicLunghezzaPasswordGenerate();
			this.applicativiApiKeyLunghezzaPwGenerate = consoleProperties.getConsoleApplicativiApiKeyLunghezzaPasswordGenerate();
			this.soggettiPwConfiguration = consoleProperties.getConsoleSoggettiPassword();
			this.soggettiBasicPwEnableConstraints = consoleProperties.isConsoleSoggettiBasicPasswordEnableConstraints();
			this.soggettiBasicLunghezzaPwGenerate = consoleProperties.getConsoleSoggettiBasicLunghezzaPasswordGenerate();
			this.soggettiApiKeyLunghezzaPwGenerate = consoleProperties.getConsoleSoggettiApiKeyLunghezzaPasswordGenerate();			
			this.messageSecurityPropertiesSourceConfiguration = consoleProperties.getMessageSecurityPropertiesSourceConfiguration();
			this.policyGestioneTokenPropertiesSourceConfiguration = consoleProperties.getPolicyGestioneTokenPropertiesSourceConfiguration();
			this.isPolicyGestioneTokenVerificaCertificati = consoleProperties.isPolicyGestioneTokenVerificaCertificati();
			this.policyGestioneTokenPDND = consoleProperties.getPolicyGestioneTokenPDND();
			this.attributeAuthorityPropertiesSourceConfiguration = consoleProperties.getAttributeAuthorityPropertiesSourceConfiguration();
			this.isAttributeAuthorityVerificaCertificati = consoleProperties.isAttributeAuthorityVerificaCertificati(); 
			this.isControlloTrafficoPolicyGlobaleGroupByApi = consoleProperties.isControlloTrafficoPolicyGlobaleGroupByApi();
			this.isControlloTrafficoPolicyGlobaleFiltroApi = consoleProperties.isControlloTrafficoPolicyGlobaleFiltroApi();
			this.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore = consoleProperties.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore();
			this.controlloTrafficoPolicyRateLimitingTipiGestori = consoleProperties.getControlloTrafficoPolicyRateLimitingTipiGestori(); 
			this.isAuditingRegistrazioneElementiBinari = consoleProperties.isAuditingRegistrazioneElementiBinari();
			this.isIntegrationManagerEnabled = consoleProperties.isIntegrationManagerEnabled();
			this.isIntegrationManagerTraceMessageBoxOperationEnabled = consoleProperties.isIntegrationManagerTraceMessageBoxOperationEnabled();
			this.soggettiNomeMaxLength = consoleProperties.getSoggettiNomeMaxLength();
			this.isSoggettiVerificaCertificati = consoleProperties.isSoggettiVerificaCertificati();
			this.isApplicativiVerificaCertificati = consoleProperties.isApplicativiVerificaCertificati();
			this.isApiResourcePathValidatorEnabled = consoleProperties.isApiResourcePathValidatorEnabled();
			this.isApiResourceHttpMethodAndPathQualsiasiEnabled = consoleProperties.isApiResourceHttpMethodAndPathQualsiasiEnabled();
			this.getApiResourcePathQualsiasiSpecialChar = consoleProperties.getApiResourcePathQualsiasiSpecialChar();
			this.isApiOpenAPIValidateUriReferenceAsUrl = consoleProperties.isApiOpenAPIValidateUriReferenceAsUrl();
			this.isApiRestResourceRepresentationMessageTypeOverride = consoleProperties.isApiRestResourceRepresentationMessageTypeOverride();
			this.isApiDescriptionTruncate255 = consoleProperties.isApiDescriptionTruncate255();
			this.isApiDescriptionTruncate4000 = consoleProperties.isApiDescriptionTruncate4000();
			this.isAccordiCooperazioneEnabled = consoleProperties.isAccordiCooperazioneEnabled();
			this.isErogazioniVerificaCertificati = consoleProperties.isErogazioniVerificaCertificati();
			this.isFruizioniVerificaCertificati = consoleProperties.isFruizioniVerificaCertificati();
			this.messageEngines = consoleProperties.getMessageEngines();
			this.isSoggettiCredenzialiBasicCheckUniqueUsePassword = consoleProperties.isSoggettiCredenzialiBasicCheckUniqueUsePassword();
			this.isApplicativiCredenzialiBasicCheckUniqueUsePassword = consoleProperties.isApplicativiCredenzialiBasicCheckUniqueUsePassword();
			this.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials = consoleProperties.isSoggettiApplicativiCredenzialiBasicPermitSameCredentials();
			this.isSoggettiApplicativiCredenzialiSslPermitSameCredentials = consoleProperties.isSoggettiApplicativiCredenzialiSslPermitSameCredentials();
			this.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials = consoleProperties.isSoggettiApplicativiCredenzialiPrincipalPermitSameCredentials();
			this.isConnettoriAllTypesEnabled = consoleProperties.isConnettoriAllTypesEnabled();
			this.isConnettoriMultipliEnabled = consoleProperties.isConnettoriMultipliEnabled();
			this.isConnettoriMultipliConsegnaCondizionaleStessFiltroPermesso = consoleProperties.isConnettoriMultipliConsegnaCondizionaleStessFiltroPermesso();
			this.isConnettoriMultipliConsegnaMultiplaEnabled = consoleProperties.isConnettoriMultipliConsegnaMultiplaEnabled();
			this.isApplicativiServerEnabled = consoleProperties.isApplicativiServerEnabled();
			this.consegnaNotificaCode = consoleProperties.getConsegnaNotificaCode();
			for (String coda : this.consegnaNotificaCode) {
				this.consegnaNotificaCodaLabel.put(coda, consoleProperties.getConsegnaNotificaCodaLabel(coda));
			}
			this.consegnaNotificaPriorita = consoleProperties.getConsegnaNotificaPriorita();
			for (String priorita : this.consegnaNotificaPriorita) {
				this.consegnaNotificaConfigurazionePriorita.put(priorita, consoleProperties.getConsegnaNotificaConfigurazionePriorita(priorita));
			}
			this.isModipaErogazioniVerificaCertificati = consoleProperties.isModipaErogazioniVerificaCertificati();
			this.isModipaFruizioniVerificaCertificati = consoleProperties.isModipaFruizioniVerificaCertificati();
			this.isModipaFruizioniConnettoreCheckHttps = consoleProperties.isModipaFruizioniConnettoreCheckHttps();
			this.isModipaFiltroRicercaProfiloQualsiasiVisualizzaDatiModi = consoleProperties.isModipaFiltroRicercaProfiloQualsiasiVisualizzaDatiModi();
			this.configurazionePluginsEnabled = consoleProperties.isConfigurazionePluginsEnabled();
			this.configurazionePluginsSeconds = consoleProperties.getPluginsSeconds();
			this.configurazioneHandlersEnabled = consoleProperties.isConfigurazioneHandlersEnabled();
			this.configurazioneAllarmiEnabled = consoleProperties.isConfigurazioneAllarmiEnabled();
			if(this.configurazioneAllarmiEnabled) {
				this.allarmiConfig = AlarmConfigProperties.getAlarmConfiguration(ControlStationCore.getLog(), consoleProperties.getAllarmiConfigurazione(), consoleProperties.getConfDirectory());
				this.showAllarmiIdentificativoRuntime = consoleProperties.isShowAllarmiIdentificativoRuntime();
				this.showAllarmiFormNomeSuggeritoCreazione = consoleProperties.isShowAllarmiFormNomeSuggeritoCreazione();
				this.showAllarmiFormStatoAllarme = consoleProperties.isShowAllarmiFormStatoAllarme();
				this.showAllarmiFormStatoAllarmeHistory = consoleProperties.isShowAllarmiFormStatoAllarmeHistory();
				this.showAllarmiSearchStatiAllarmi = consoleProperties.isShowAllarmiSearchStatiAllarmi();
				this.showAllarmiElenchiStatiAllarmi = consoleProperties.isShowAllarmiElenchiStatiAllarmi();
			}
			this.isRegistrazioneMessaggiMultipartPayloadParsingEnabled = consoleProperties.isRegistrazioneMessaggiMultipartPayloadParsingEnabled();
			this.isClusterDinamicoEnabled = consoleProperties.isClusterDinamicoEnabled();
			this.isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata = consoleProperties.isOCSPPolicyChoiceConnettoreHTTPSVerificaServerDisabilitata();
			this.verificaCertificatiWarningExpirationDays = consoleProperties.getVerificaCertificatiWarningExpirationDays();
			this.verificaCertificatiSceltaClusterId = consoleProperties.isVerificaCertificatiSceltaClusterId();
			this.isClusterAsyncUpdate = consoleProperties.isClusterAsyncUpdate();
			this.clusterAsyncUpdateCheckInterval = consoleProperties.getClusterAsyncUpdateCheckInterval();
		
			// Impostazioni grafiche
			this.consoleNomeSintesi = consoleProperties.getConsoleNomeSintesi();
			this.consoleNomeEsteso = consoleProperties.getConsoleNomeEsteso();
			this.consoleCSS = consoleProperties.getConsoleCSS();
			this.consoleLanguage = consoleProperties.getConsoleLanguage();
			this.consoleLunghezzaLabel = consoleProperties.getConsoleLunghezzaLabel();
			this.logoHeaderImage = consoleProperties.getLogoHeaderImage();
			this.logoHeaderLink = consoleProperties.getLogoHeaderLink();
			this.logoHeaderTitolo = consoleProperties.getLogoHeaderTitolo();
			this.visualizzaLinkHomeHeader = consoleProperties.isVisualizzaLinkHomeHeader();
			String fontName = consoleProperties.getConsoleFontFamilyName();
			int fontStyle = consoleProperties.getConsoleFontStyle();
			this.defaultFont = new Font(fontName,fontStyle, 14);
			
			/** Login */
			this.loginApplication = consoleProperties.isLoginApplication();
			this.loginErroreInternoRedirectUrl = consoleProperties.getLoginErroreInternoRedirectUrl();
			this.loginProperties = consoleProperties.getLoginProperties();
			this.loginSessioneScadutaRedirectUrl = consoleProperties.getLoginSessioneScadutaRedirectUrl();
			this.loginTipo = consoleProperties.getLoginTipo();
			this.loginUtenteNonAutorizzatoRedirectUrl = consoleProperties.getLoginUtenteNonAutorizzatoRedirectUrl();
			this.loginUtenteNonValidoRedirectUrl = consoleProperties.getLoginUtenteNonValidoRedirectUrl();
			this.logoutMostraButton = consoleProperties.isMostraButtonLogout();
			this.logoutUrlDestinazione = consoleProperties.getLogoutUrlDestinazione();
			
			// Opzioni di Visualizzazione
			this.showJ2eeOptions = consoleProperties.isShowJ2eeOptions();
			this.showConfigurazioniPersonalizzate = consoleProperties.isConsoleConfigurazioniPersonalizzate();
			this.showGestioneSoggettiRouter = consoleProperties.isConsoleGestioneSoggettiRouter();
			this.showGestioneSoggettiVirtuali = consoleProperties.isConsoleGestioneSoggettiVirtuali();
			this.showGestioneWorkflowStatoDocumenti = consoleProperties.isConsoleGestioneWorkflowStatoDocumenti();
			this.gestioneWorkflowStatoDocumentiVisualizzaStatoLista = consoleProperties.isConsoleGestioneWorkflowStatoDocumentiVisualizzaStatoLista();
			this.gestioneWorkflowStatoDocumentiRipristinoStatoOperativoDaFinale = consoleProperties.isConsoleGestioneWorkflowStatoDocumentiRipristinoStatoOperativoDaFinale();
			this.showInterfacceAPI = consoleProperties.isConsoleInterfacceAPIVisualizza();
			this.showAllegati = consoleProperties.isConsoleAllegatiVisualizza();
			this.showFlagPrivato = consoleProperties.isMenuVisualizzaFlagPrivato();
			this.showAllConnettori = consoleProperties.isMenuVisualizzaListaCompletaConnettori();
			this.showDebugOptionConnettore = consoleProperties.isMenuVisualizzaOpzioneDebugConnettore();
			this.showCorrelazioneAsincronaInAccordi = consoleProperties.isMenuAccordiVisualizzaCorrelazioneAsincrona();
			this.showAccordiInformazioniProtocollo = consoleProperties.isMenuAccordiVisualizzazioneGestioneInformazioniProtocollo();
			this.showCountElementInLinkList = consoleProperties.isElenchiVisualizzaCountElementi();
			this.conservaRisultatiRicerca = consoleProperties.isElenchiRicercaConservaCriteri();
			if(conservaRisultatiRicercaStaticInfoRead==null) {
				conservaRisultatiRicercaStaticInfoRead = true;
				conservaRisultatiRicercaStaticInfo = this.conservaRisultatiRicerca;
			}
			this.showAccordiColonnaAzioni = consoleProperties.isElenchiAccordiVisualizzaColonnaAzioni();
			this.showPulsantiImportExport = consoleProperties.isElenchiMenuVisualizzazionePulsantiImportExportPackage();
			this.elenchiMenuIdentificativiLunghezzaMassima = consoleProperties.getElenchiMenuIdentificativiLunghezzaMassima();
			this.enableAutoMappingWsdlIntoAccordo = consoleProperties.isEnableAutoMappingWsdlIntoAccordo();
			this.enableAutoMappingWsdlIntoAccordoEstrazioneSchemiInWsdlTypes = consoleProperties.isEnableAutoMappingWsdlIntoAccordoEstrazioneSchemiInWsdlTypes();
			this.showMTOMVisualizzazioneCompleta = consoleProperties.isMenuMTOMVisualizzazioneCompleta();
			this.portaCorrelazioneApplicativaMaxLength = consoleProperties.getPortaCorrelazioneApplicativaMaxLength();
			this.showPortaDelegataLocalForward = consoleProperties.isMenuPortaDelegataLocalForward();
			this.isProprietaErogazioniShowModalitaStandard = consoleProperties.isProprietaErogazioniShowModalitaStandard();
			this.isProprietaFruizioniShowModalitaStandard = consoleProperties.isProprietaFruizioniShowModalitaStandard();
			this.isPortTypeObbligatorioImplementazioniSOAP = consoleProperties.isPortTypeObbligatorioImplementazioniSOAP();
			this.isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona = consoleProperties.isElenchiSAAsincroniNonSupportatiVisualizzaRispostaAsincrona();
			this.isVisualizzazioneConfigurazioneDiagnosticaLog4J = consoleProperties.isVisualizzazioneConfigurazioneDiagnosticaLog4J();
			this.tokenPolicyForceId = consoleProperties.getTokenPolicyForceId();
			this.tokenPolicyForceIdEnabled = StringUtils.isNotEmpty(this.tokenPolicyForceId);
			this.tokenPolicyTipologia = consoleProperties.getTokenPolicyTipologia();
			this.attributeAuthorityForceId = consoleProperties.getAttributeAuthorityForceId();
			this.attributeAuthorityForceIdEnabled = StringUtils.isNotEmpty(this.attributeAuthorityForceId);
			this.attributeAuthorityTipologia = consoleProperties.getAttributeAuthorityTipologia();
			this.showServiziVisualizzaModalitaElenco = consoleProperties.isEnableServiziVisualizzaModalitaElenco();
			this.selectListSoggettiOperativiNumeroMassimoSoggetti = consoleProperties.getNumeroMassimoSoggettiOperativiMenuUtente();
			this.selectListSoggettiOperativiDimensioneMassimaLabel = consoleProperties.getLunghezzaMassimaLabelSoggettiOperativiMenuUtente();
			this.viewLunghezzaMassimaInformazione = consoleProperties.getLunghezzaMassimaInformazioneView();
			this.isSetSearchAfterAdd = consoleProperties.isSetSearchAfterAdd();
			this.elenchiVisualizzaComandoResetCacheSingoloElemento = consoleProperties.isElenchiAbilitaResetCacheSingoloElemento();
			this.validitaTokenCsrf = consoleProperties.getValiditaTokenCsrf();
			this.cspHeaderValue = consoleProperties.getCSPHeaderValue();
			this.xContentTypeOptionsHeaderValue = consoleProperties.getXContentTypeOptionsHeaderValue();
			this.xFrameOptionsHeaderValue = consoleProperties.getXFrameOptionsHeaderValue();
			this.xXssProtectionHeaderValue = consoleProperties.getXXssProtectionHeaderValue();
			this.consoleSecurityConfiguration = consoleProperties.getConsoleSecurityConfiguration();
			
			// Gestione govwayConsole locale
			if(this.singlePdD){
				this.gestionePddAbilitata = consoleProperties.isSinglePddGestionePdd();
				
				this.registroServiziLocale = consoleProperties.isSinglePddRegistroServiziLocale();
				
				this.tracceShowConfigurazioneCustomAppender = consoleProperties.isSinglePddTracceConfigurazioneCustomAppender();
				this.tracceShowSorgentiDatiDatabase = consoleProperties.isSinglePddTracceGestioneSorgentiDatiPrelevataDaDatabase();
				
				this.msgDiagnosticiShowConfigurazioneCustomAppender = consoleProperties.isSinglePddMessaggiDiagnosticiConfigurazioneCustomAppender();
				this.msgDiagnosticiShowSorgentiDatiDatabase = consoleProperties.isSinglePddMessaggiDiagnosticiGestioneSorgentiDatiPrelevataDaDatabase();
				
				this.dumpShowConfigurazioneCustomAppender = consoleProperties.isSinglePddDumpConfigurazioneCustomAppender();
				this.dumpShowConfigurazioneDumpRealtime = consoleProperties.isSinglePddDumpConfigurazioneRealtime();
			}
			
			// Opzioni di importazione/esportazione Archivi
			this.importArchivi_tipoPdD = consoleProperties.getImportArchiveTipoPdD();
			this.exportArchive_configurazione_soloDumpCompleto = consoleProperties.isExportArchiveConfigurazioneSoloDumpCompleto();
			this.exportArchive_servizi_standard = consoleProperties.isExportArchiveServiziStandard();
			
			// Multitenant
			// Inizializzato dopo aver attivato il Database, per leggere la configurazione su DB
			
			// Gestione Visibilità utenti
			this.visioneOggettiGlobale = consoleProperties.isVisibilitaOggettiGlobale();
			this.utentiConVisioneGlobale.addAll(consoleProperties.getUtentiConVisibilitaGlobale());

			/// Opzioni per Plugins
			this.pluginMenu = this.newIExtendedMenu(consoleProperties.getPluginsMenu());
			this.pluginConfigurazione = this.newIExtendedFormServlet(consoleProperties.getPluginsConfigurazione());
			if(this.pluginConfigurazione!=null){
				for (IExtendedFormServlet formPluginConfigurazione : this.pluginConfigurazione) {
					IExtendedListServlet listPluginConfigurazione = formPluginConfigurazione.getExtendedInternalList();
					if(listPluginConfigurazione!=null){
						this.pluginConfigurazioneList.put(formPluginConfigurazione.getUniqueID(), listPluginConfigurazione);
					}
				}
			}
			this.pluginConnettore = this.newIExtendedConnettore(consoleProperties.getPluginsConnettore());
			this.pluginPortaDelegata = this.newIExtendedListServlet(consoleProperties.getPluginsPortaDelegata());
			this.pluginPortaApplicativa = this.newIExtendedListServlet(consoleProperties.getPluginsPortaApplicativa());

		} catch (java.lang.Exception e) {
			ControlStationCore.logError("[ControlStationCore::initCore] Impossibile leggere i dati dal file console.properties:" + e.toString(),e);
			throw new ControlStationCoreException("[ControlStationCore::initCore] Impossibile leggere i dati dal file console.properties:" + e.toString(),e);
		}
		
	}

	
	private void initCoreJmxResources() throws ControlStationCoreException {

		// Leggo le informazioni da console.properties
		ConsoleProperties consoleProperties = null;
		try {
			consoleProperties = ConsoleProperties.getInstance();
					
			// Opzioni Accesso JMX della PdD
			
			this.configurazioneNodiRuntime = consoleProperties.getConfigurazioneNodiRuntime();
			this.invoker = new InvokerNodiRuntime(log, this.configurazioneNodiRuntime);
			
			this.isVisualizzaLinkClearAllCachesRemoteCheckCacheStatus = consoleProperties.isVisualizzaLinkClearAllCachesRemoteCheckCacheStatus();
			this.jmxPdDAliases = consoleProperties.getJmxPdDAliases();
			if(this.singlePdD==false){
				// se esistono degli alias allora assegno poi come alias i nomi delle pdd operative
				if(this.jmxPdDAliases!=null && this.jmxPdDAliases.size()>0){
					this.jmxPdDAliases = new ArrayList<>();
					PddCore pddCore = new PddCore(this);
					try{
						List<PdDControlStation> pddList = pddCore.pddList(null, new ConsoleSearch(true));
						for (PdDControlStation pddControlStation : pddList) {
							if(PddTipologia.OPERATIVO.toString().equals(pddControlStation.getTipo())){
								this.jmxPdDAliases.add(pddControlStation.getNome());
							}
						}
					}catch(Exception e){
						// ignore
					}
				}
			}
			
			this.jmxPdDConfigurazioneSistemaShowInformazioniCryptographyKeyLength = consoleProperties.isJmxPdDConfigurazioneSistemaShowInformazioniCryptographyKeyLength();
			
			if(this.jmxPdDAliases!=null){
				
				this.jmxPdDCertificateChecker = new CertificateChecker(log, this.invoker, this.configurazioneNodiRuntime, this.jmxPdDAliases, consoleProperties);
								
				for (String alias : this.jmxPdDAliases) {
					String descrizione = consoleProperties.getJmxPdDDescrizione(alias);
					if(descrizione!=null)
						this.jmxPdDDescrizioni.put(alias,descrizione);
										
					this.jmxPdDConfigurazioneSistemaType.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaType(alias));
					this.jmxPdDConfigurazioneSistemaNomeRisorsa.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsa(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoVersionePdD.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoVersionePdD(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoVersioneBaseDati.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoVersioneBaseDati(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoVersioneJava.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoVersioneJava(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoVendorJava.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoVendorJava(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoTipoDatabase.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoTipoDatabase(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniDatabase.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniDatabase(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniSSL.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniSSL(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteSSL.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteSSL(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCryptographyKeyLength.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCryptographyKeyLength(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCharset.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCharset(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniInternazionalizzazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniInternazionalizzazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteInternazionalizzazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteInternazionalizzazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniTimeZone.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniTimeZone(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteTimeZone.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteTimeZone(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaNetworking.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaNetworking(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteProprietaJavaNetworking.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniCompleteProprietaJavaNetworking(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaAltro.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaJavaAltro(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaSistema.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniProprietaSistema(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoMessageFactory.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoMessageFactory(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoDirectoryConfigurazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoDirectoryConfigurazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoPluginProtocols.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoPluginProtocols(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoInformazioniInstallazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoInformazioniInstallazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetFileTrace(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoUpdateFileTrace.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoUpdateFileTrace(alias));
					this.jmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaMonitoraggio(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniDB.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniDB(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniJMS.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniJMS(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoIdTransazioniAttive.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoIdTransazioniAttive(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoIdProtocolloTransazioniAttive.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoIdProtocolloTransazioniAttive(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniPD.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniPD(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoConnessioniPA.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoConnessioniPA(alias));
					this.jmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaConfigurazionePdD(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnostici.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnostici(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnosticiLog4j.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoSeveritaDiagnosticiLog4j(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTracciamento.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTracciamento(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoDumpPD.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoDumpPD(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoDumpPA.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoDumpPA(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jDiagnostica.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoLog4jDiagnostica(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jOpenspcoop.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoLog4jOpenspcoop(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jIntegrationManager.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoLog4jIntegrationManager(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jTracciamento.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoLog4jTracciamento(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoLog4jDump.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoLog4jDump(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorStatusCode.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorStatusCode(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorInstanceId.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorInstanceId(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeBadResponse.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeBadResponse(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalResponseError.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalResponseError(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalRequestError.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalRequestError(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalError.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorTypeInternalError(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorDetails.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionSpecificErrorDetails(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorUseStatusCodeAsFaultCode.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorUseStatusCodeAsFaultCode(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorGenerateHttpHeaderGovWayCode.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTransactionErrorGenerateHttpHeaderGovWayCode(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerConsegnaContenutiApplicativi.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerConsegnaContenutiApplicativi(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerEventi.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerEventi(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerFileSystemRecovery.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerFileSystemRecovery(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteOnewayNonRiscontrate.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteOnewayNonRiscontrate(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteAsincroneNonRiscontrate.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreBusteAsincroneNonRiscontrate(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiEliminati.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiEliminati(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiScaduti.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiScaduti(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiNonGestiti.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaMessaggiNonGestiti(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaCorrelazioneApplicativa.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiPuliziaCorrelazioneApplicativa(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiVerificaConnessioniAttive.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreMessaggiVerificaConnessioniAttive(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestorePuliziaMessaggiAnomali.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestorePuliziaMessaggiAnomali(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreRepositoryBuste.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreRepositoryBuste(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerMonitoraggioRisorseThread.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerMonitoraggioRisorseThread(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerRepositoryStatefulThread.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerRepositoryStatefulThread(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheOrarie.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheOrarie(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheGiornaliere.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheGiornaliere(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheSettimanali.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheSettimanali(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheMensili.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerStatisticheMensili(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreChiaviPDND.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreChiaviPDND(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreCacheChiaviPDND.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreCacheChiaviPDND(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreOperazioniRemote.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerGestoreOperazioniRemote(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerSvecchiamentoOperazioniRemote.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerSvecchiamentoOperazioniRemote(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoTimerThresholdThread.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoTimerThresholdThread(alias));	
					this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreById(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreById.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreById(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyValidazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyValidazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyNegoziazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreTokenPolicyNegoziazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreAttributeAuthority.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoCheckConnettoreAttributeAuthority(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyValidazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyValidazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyNegoziazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreTokenPolicyNegoziazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreAttributeAuthority.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetCertificatiConnettoreAttributeAuthority(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoEnablePortaDelegata.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoEnablePortaDelegata(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoDisablePortaDelegata.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoDisablePortaDelegata(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoEnablePortaApplicativa.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoEnablePortaApplicativa(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoDisablePortaApplicativa.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoDisablePortaApplicativa(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoEnableConnettoreMultiplo.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoEnableConnettoreMultiplo(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoDisableConnettoreMultiplo.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoDisableConnettoreMultiplo(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiplo.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiplo(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiplo.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiplo(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiploRuntimeRepository.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoEnableSchedulingConnettoreMultiploRuntimeRepository(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiploRuntimeRepository.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoDisableSchedulingConnettoreMultiploRuntimeRepository(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAccordoCooperazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAccordoCooperazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApi.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApi(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheErogazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheErogazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheFruizione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheFruizione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheSoggetto.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheSoggetto(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApplicativo.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheApplicativo(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheRuolo.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheRuolo(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheScope.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheScope(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyValidazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyValidazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyNegoziazione.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheTokenPolicyNegoziazione(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAttributeAuthority.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRipulisciRiferimentiCacheAttributeAuthority(alias));
					this.jmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaAccessoRegistroServizi(alias));
					this.jmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaStatoServiziPdD(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegata.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegata(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataAbilitazioniPuntuali.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataAbilitazioniPuntuali(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataDisabilitazioniPuntuali.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaDelegataDisabilitazioniPuntuali(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativa.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativa(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaAbilitazioniPuntuali.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaAbilitazioniPuntuali(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaDisabilitazioniPuntuali.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioPortaApplicativaDisabilitazioniPuntuali(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoStatoServizioIntegrationManager.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoStatoServizioIntegrationManager(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaDelegata.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaDelegata(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaDelegata.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaDelegata(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaApplicativa.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioPortaApplicativa(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaApplicativa.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioPortaApplicativa(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioIntegrationManager.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoAbilitaServizioIntegrationManager(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioIntegrationManager.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoDisabilitaServizioIntegrationManager(alias));
					this.jmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaDatasourceGW(alias));
					this.jmxPdDConfigurazioneSistemaNomeAttributoNumeroDatasourceGW.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeAttributoNumeroDatasourceGW(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetDatasourcesGW.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetDatasourcesGW(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetUsedConnectionsDatasourcesGW.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetUsedConnectionsDatasourcesGW(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetInformazioniDatabaseDatasourcesGW.put(alias,consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetInformazioniDatabaseDatasourcesGW(alias));
					this.jmxPdDConfigurazioneSistemaNomeRisorsaConsegnaContenutiApplicativi.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaGestioneConsegnaApplicativi(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetThreadPoolStatus.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetThreadPoolStatus(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetQueueConfig.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetQueueConfig(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetApplicativiPrioritari.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetApplicativiPrioritari(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoGetConnettoriPrioritari.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoGetConnettoriPrioritari(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoUpdateConnettoriPrioritari.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoUpdateConnettoriPrioritari(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoResetConnettoriPrioritari.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoResetConnettoriPrioritari(alias));
					this.jmxPdDConfigurazioneSistemaNomeRisorsaSystemPropertiesPdD.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaSystemPropertiesPdD(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRefreshPersistentConfiguration.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRefreshPersistentConfiguration(alias));
					this.jmxPdDConfigurazioneSistemaNomeRisorsaDatiRichieste.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeRisorsaDatiRichieste(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingGlobalConfigCache.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingGlobalConfigCache(alias));
					this.jmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingAPIConfigCache.put(alias, consoleProperties.getJmxPdDConfigurazioneSistemaNomeMetodoRemoveRateLimitingAPIConfigCache(alias));
					this.jmxPdDCaches.put(alias, consoleProperties.getJmxPdDCaches(alias));
					this.jmxPdDCachesPrefill.put(alias, consoleProperties.getJmxPdDCachesPrefill(alias));
					this.jmxPdDCacheType.put(alias, consoleProperties.getJmxPdDCacheType(alias));
					this.jmxPdDCacheNomeAttributoCacheAbilitata.put(alias, consoleProperties.getJmxPdDCacheNomeAttributoCacheAbilitata(alias));
					this.jmxPdDCacheNomeMetodoStatoCache.put(alias, consoleProperties.getJmxPdDCacheNomeMetodoStatoCache(alias));
					this.jmxPdDCacheNomeMetodoResetCache.put(alias, consoleProperties.getJmxPdDCacheNomeMetodoResetCache(alias));
					if(!this.jmxPdDCachesPrefill.get(alias).isEmpty()){
						this.jmxPdDCacheNomeMetodoPrefillCache.put(alias, consoleProperties.getJmxPdDCacheNomeMetodoPrefillCache(alias));
					}
				}
			}
			
			this.jmxPdDGruppiAliases = consoleProperties.getJmxPdDGruppiAliases();
			
		} catch (java.lang.Exception e) {
			ControlStationCore.logError("[ControlStationCore::initCoreJmxResources] Impossibile leggere i dati dal file console.properties:" + e.toString(),e);
			throw new ControlStationCoreException("[ControlStationCore::initCoreJmxResources] Impossibile leggere i dati dal file console.properties:" + e.toString(),e);
		}
	}
	

	/**
	 * Inizializza il driver di tracciamento.
	 * 
	 * @throws DriverControlStationException
	 */
	private synchronized void initDriverTracciamento(String nomeDs, boolean forceChange) throws DriverControlStationException {
		if (this.driverTracciamento == null || forceChange) {
			try {
				if (nomeDs == null || nomeDs.equals("") || nomeDs.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
					if(this.tracceSameDBWebUI){
						this.driverTracciamento = new DriverTracciamento(ControlStationCore.dbM.getDataSourceName(),this.tipoDB,ControlStationCore.dbM.getDataSourceContext(),ControlStationCore.log);
					}
					else{
						this.driverTracciamento = new DriverTracciamento(this.tracceDatasource, this.tracceTipoDatabase, this.tracceCtxDatasource,ControlStationCore.log);
					}
				} else {

					Configurazione newConfigurazione = this.getConfigurazioneGenerale();
					Tracciamento t = newConfigurazione.getTracciamento();
					List<OpenspcoopSorgenteDati> lista = t.getOpenspcoopSorgenteDatiList();
					OpenspcoopSorgenteDati od = null;
					for (int j = 0; j < t.sizeOpenspcoopSorgenteDatiList(); j++) {
						od = lista.get(j);
						if (od!=null && nomeDs!=null && nomeDs.equals(od.getNome())) {
							break;
						}
					}
					
					if(od==null) {
						throw new Exception("Sorgente Dati non trovata");
					}

					//proprieta
					Properties prop = new Properties();

					if(od.getPropertyList()!=null && od.sizePropertyList()>0){
						for (Property p : od.getPropertyList()) {
							prop.put(p.getNome(), p.getValore());
						}
					}

					this.driverTracciamento = new DriverTracciamento(
							od.getNomeJndi(),
							od.getTipoDatabase(),
							prop,
							ControlStationCore.log); 
				}
			} catch (java.lang.Exception e) {
				ControlStationCore.logError("[pdd] Inizializzazione DriverTracciamento non riuscita : " + e.getMessage(),e);
				throw new DriverControlStationException("[pdd] Inizializzazione DriverTracciamento non riuscita : " + e.getMessage(),e);
			}

		}

	}

	/**
	 * Inizializza il driver della diagnostica.
	 * 
	 * @throws DriverControlStationException
	 */
	private synchronized void initDriverMSGDiagnostici(String nomeDs, boolean forceChange) throws DriverControlStationException {
		if (this.driverMSGDiagnostici == null || forceChange) {
			try {
				if (nomeDs == null || nomeDs.equals("") || nomeDs.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
					if(this.msgDiagnosticiSameDBWebUI){
						this.driverMSGDiagnostici = new DriverMsgDiagnostici(ControlStationCore.dbM.getDataSourceName(),this.tipoDB,ControlStationCore.dbM.getDataSourceContext(),ControlStationCore.log); 
					}
					else{
						this.driverMSGDiagnostici = new DriverMsgDiagnostici(this.msgDiagnosticiDatasource, this.msgDiagnosticiTipoDatabase, this.msgDiagnosticiCtxDatasource,ControlStationCore.log); 
					}
				} else {

					Configurazione newConfigurazione = this.getConfigurazioneGenerale();
					MessaggiDiagnostici md = newConfigurazione.getMessaggiDiagnostici();
					List<OpenspcoopSorgenteDati> lista = md.getOpenspcoopSorgenteDatiList();
					OpenspcoopSorgenteDati od = null;
					for (int j = 0; j < md.sizeOpenspcoopSorgenteDatiList(); j++) {
						od = lista.get(j);
						if (od!=null && nomeDs!=null && nomeDs.equals(od.getNome())) {
							break;
						}
					}
					
					if(od==null) {
						throw new DriverControlStationException("Sorgente Dati non trovata");
					}
					
					//proprieta
					Properties prop = new Properties();

					if(od.getPropertyList()!=null && od.sizePropertyList()>0){
						for (Property p : od.getPropertyList()) {
							prop.put(p.getNome(), p.getValore());
						}
					}

					this.driverMSGDiagnostici = new DriverMsgDiagnostici(
							od.getNomeJndi(),
							od.getTipoDatabase(),
							prop,
							ControlStationCore.log); 
				}
			} catch (java.lang.Exception e) {
				ControlStationCore.logError("[pdd] Inizializzazione DriverMSGDiagnostici non riuscita : " + e.getMessage(),e);
				throw new DriverControlStationException("[pdd] Inizializzazione DriverMSGDiagnostici non riuscita : " + e.getMessage(),e);
			}

		}

	}






	/* ----- SMISTAMENTO OPERAZIONE ------ */

	/**
	 * Effettua transazionalmente le operazioni utilizzando il driver Ogni
	 * oggetto passato ha un corrispondente tipo di operazione che deve essere
	 * effettuato su di esso in modo tale da poter effettuare diverse operazioni
	 * su diversi oggetti in maniera transazionale
	 * 
	 * @param <Type>
	 * @param operationType
	 * @param superUser
	 * @param oggetti
	 * @throws DriverConfigurazioneException
	 * @throws DriverControlStationException
	 * @throws DriverRegistroServiziException
	 * @throws ControlStationCoreException
	 */
	private void performOperation(int[] operationTypes, String superUser, boolean smista, Object... oggetti) throws DriverConfigurazioneNotFound, DriverRegistroServiziNotFound, DriverConfigurazioneException, DriverControlStationException, DriverRegistroServiziException, ControlStationCoreException {
		Connection con = null;
		DriverControlStationDB driver = null;

		try {

			con = ControlStationCore.dbM.getConnection();
			if(con==null) {
				throw new ControlStationCoreException("Connection is null");
			}

			// le operazioni da eseguire devono essere transazionali quindi
			// disabilito l'autocommit
			// e lo riabilito solo dopo aver terminato le operazioni
			con.setAutoCommit(false);
			
			// creo il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			for (int i = 0; i < oggetti.length; i++) {
				// prendo il tipo di operazione da effettuare su questo oggetto
				int operationType = operationTypes[i];
				Object oggetto = oggetti[i];
				
				IExtendedBean extendedBean = null;
				IExtendedCoreServlet extendedServlet = null;
				if(oggetto instanceof WrapperExtendedBean){
					WrapperExtendedBean w = (WrapperExtendedBean) oggetto;
					if(w.isManageOriginalBean()){
						oggetto = w.getOriginalBean();	
					}
					extendedBean = w.getExtendedBean();
					extendedServlet = w.getExtendedServlet();
				}
				
				
				switch (operationType) {
				case CostantiControlStation.PERFORM_OPERATION_CREATE:

					// Performing CREATE operations
					/* Eseguo l'operazione sull'oggetto */
					/**
					 * Operazioni su ctrlstat
					 */
					if (oggetto instanceof PdDControlStation) {
						PdDControlStation pdd = (PdDControlStation) oggetto;
						driver.createPdDControlStation(pdd);
					}

					if (oggetto instanceof MappingFruizionePortaDelegata) {
						MappingFruizionePortaDelegata mapping = (MappingFruizionePortaDelegata) oggetto;
						driver.createMappingFruizionePortaDelegata(mapping);
					}
					
					if (oggetto instanceof MappingErogazionePortaApplicativa) {
						MappingErogazionePortaApplicativa mapping = (MappingErogazionePortaApplicativa) oggetto;
						driver.createMappingErogazionePortaApplicativa(mapping);
					}

					/***********************************************************
					 * Caso Speciale dei Soggetti *
					 **********************************************************/

					// soggetto config/reg
					if (oggetto instanceof SoggettoCtrlStat) {
						SoggettoCtrlStat soggetto = (SoggettoCtrlStat) oggetto;
						Soggetto sogConf = soggetto.getSoggettoConf();
						if(this.registroServiziLocale){
							driver.getDriverRegistroServiziDB().createSoggetto(soggetto.getSoggettoReg());

							sogConf.setId(soggetto.getSoggettoReg().getId());
							IDSoggetto oldIDSoggettoForUpdate = new IDSoggetto(sogConf.getTipo(), sogConf.getNome());
							sogConf.setOldIDSoggettoForUpdate(oldIDSoggettoForUpdate);
							driver.getDriverConfigurazioneDB().updateSoggetto(sogConf);
						}
						else{
							driver.getDriverConfigurazioneDB().createSoggetto(sogConf);
						}
					}
					
					// soggetto config
					if (oggetto instanceof org.openspcoop2.core.config.Soggetto) {
						
						org.openspcoop2.core.config.Soggetto sogConf = (org.openspcoop2.core.config.Soggetto) oggetto;
						// anche se e' stato chiesta una create. Puo' darsi che serva una update essendomi arrivata una informazione puntuale su di un tipo di soggetto
						IDSoggetto idSoggetto = new IDSoggetto(sogConf.getTipo(),sogConf.getNome());
						if(driver.getDriverConfigurazioneDB().existsSoggetto(idSoggetto)){
							IDSoggetto oldIDSoggettoForUpdate = new IDSoggetto(sogConf.getTipo(), sogConf.getNome());
							sogConf.setOldIDSoggettoForUpdate(oldIDSoggettoForUpdate);
							driver.getDriverConfigurazioneDB().updateSoggetto(sogConf);
						}
						else{
							// lancio una eccezione.
							// Anche nel caso di gestione singola degli oggetti deve comunque essere prima creato il soggetto sul registro.
							throw new ControlStationCoreException("Impossibile gestire la richiesta di creazione di un soggetto ("+idSoggetto+") di tipo [org.openspcoop2.core.config.Soggetto] se il soggetto non esiste sulla base dati. La creazione e' permessa solo per il tipo org.openspcoop2.core.registry.Soggetto, mentre il config puo' essere chiamato per aggiornarne delle informazioni");
						}
					}
					
					// soggetto reg
					if (oggetto instanceof org.openspcoop2.core.registry.Soggetto) {
						
						org.openspcoop2.core.registry.Soggetto sogReg = (org.openspcoop2.core.registry.Soggetto) oggetto;
						if(this.registroServiziLocale){
							driver.getDriverRegistroServiziDB().createSoggetto(sogReg);
						}

					}

					
					/***********************************************************
					 * Operazioni su ConfigurazioneDB *
					 **********************************************************/
					
					// ServizioApplicativo
					if (oggetto instanceof ServizioApplicativo) {
						ServizioApplicativo sa = (ServizioApplicativo) oggetto;
						
						driver.getDriverConfigurazioneDB().createServizioApplicativo(sa);
						if (sa.getIdSoggetto() == null || sa.getIdSoggetto() < 0) {
							sa.setIdSoggetto(DBUtils.getIdSoggetto(sa.getNomeSoggettoProprietario(), sa.getTipoSoggettoProprietario(), con, this.tipoDB, CostantiDB.SOGGETTI));
						}

					}
					// PortaDelegata
					if (oggetto instanceof PortaDelegata) {
						PortaDelegata pd = (PortaDelegata) oggetto;
						if (pd.getIdSoggetto() == null || pd.getIdSoggetto() < 0) {
							pd.setIdSoggetto(DBUtils.getIdSoggetto(pd.getNomeSoggettoProprietario(), pd.getTipoSoggettoProprietario(), con, this.tipoDB, CostantiDB.SOGGETTI));
						}
						driver.getDriverConfigurazioneDB().createPortaDelegata(pd);
					}
					// PortaApplicativa
					if (oggetto instanceof PortaApplicativa) {
						PortaApplicativa pa = (PortaApplicativa) oggetto;
						if (pa.getIdSoggetto() == null || pa.getIdSoggetto() < 0) {
							pa.setIdSoggetto(DBUtils.getIdSoggetto(pa.getNomeSoggettoProprietario(), pa.getTipoSoggettoProprietario(), con, this.tipoDB, CostantiDB.SOGGETTI));
						}

						if(this.isRegistroServiziLocale()){
							// fix id_servizio = -1
							PortaApplicativaServizio paSE = pa.getServizio();
							// controllo solo se id < 0
							if (paSE != null && paSE.getId() <= 0) {
								String nomeSoggetto = pa.getNomeSoggettoProprietario();
								String tipoSoggetto = pa.getTipoSoggettoProprietario();
								String nomeServizio = paSE.getNome();
								String tipoServizio = paSE.getTipo();
								Integer versioneServizio = paSE.getVersione();

								// controllo presenza soggetto virtuale
								// se presente soggetto virtuale override di
								// tipoSoggetto e nomeSoggetto
								PortaApplicativaSoggettoVirtuale paSoggVirt = pa.getSoggettoVirtuale();
								if (paSoggVirt != null) {
									long idSoggettoVirt = paSoggVirt != null ? paSoggVirt.getId() : -1;

									if (idSoggettoVirt <= 0) {
										nomeSoggetto = paSoggVirt.getNome();
										tipoSoggetto = paSoggVirt.getTipo();
									} else {
										org.openspcoop2.core.registry.Soggetto soggVirt = driver.getDriverRegistroServiziDB().getSoggetto(idSoggettoVirt);
										nomeSoggetto = soggVirt.getNome();
										tipoSoggetto = soggVirt.getTipo();
									}
								}

								// recupero informazioni su servizio
								long idServizio = DBUtils.getIdServizio(nomeServizio, tipoServizio, versioneServizio, nomeSoggetto, tipoSoggetto, con, this.tipoDB, CostantiDB.SOGGETTI);

								paSE.setId(idServizio);
								pa.setServizio(paSE);
							}
						}

						driver.getDriverConfigurazioneDB().createPortaApplicativa(pa);
					}
					// RoutingTable
					if (oggetto instanceof RoutingTable) {
						RoutingTable rt = (RoutingTable) oggetto;
						driver.getDriverConfigurazioneDB().createRoutingTable(rt);
					}
					// GestioneErrore
					if (oggetto instanceof GestioneErrore) {
						GestioneErrore gestErrore = (GestioneErrore) oggetto;
						driver.getDriverConfigurazioneDB().createGestioneErroreComponenteCooperazione(gestErrore);
					}
					// Configurazione
					if (oggetto instanceof Configurazione) {
						Configurazione conf = (Configurazione) oggetto;
						driver.getDriverConfigurazioneDB().createConfigurazione(conf);
					}
					// ConfigurazioneAccessoRegistro
					if (oggetto instanceof AccessoRegistro) {
						AccessoRegistro cfgAccessoRegistro = (AccessoRegistro) oggetto;
						driver.getDriverConfigurazioneDB().createAccessoRegistro(cfgAccessoRegistro);
					}
					// ConfigurazioneAccessoRegistroRegistro
					if (oggetto instanceof AccessoRegistroRegistro) {
						AccessoRegistroRegistro carr = (AccessoRegistroRegistro) oggetto;
						driver.getDriverConfigurazioneDB().createAccessoRegistro(carr);
					}
					// ConfigurazioneAccessoConfigurazione
					if (oggetto instanceof AccessoConfigurazione) {
						AccessoConfigurazione accesso = (AccessoConfigurazione) oggetto;
						driver.getDriverConfigurazioneDB().createAccessoConfigurazione(accesso);
					}
					// ConfigurazioneAccessoDatiAutorizzazione
					if (oggetto instanceof AccessoDatiAutorizzazione) {
						AccessoDatiAutorizzazione accesso = (AccessoDatiAutorizzazione) oggetto;
						driver.getDriverConfigurazioneDB().createAccessoDatiAutorizzazione(accesso);
					}
					// ConfigurazioneSystemProperty
					if (oggetto instanceof SystemProperties) {
						SystemProperties sps = (SystemProperties) oggetto;
						driver.getDriverConfigurazioneDB().createSystemPropertiesPdD(sps);
					}
					// ConfigurazioneUrlInvocazioneRegola
					if (oggetto instanceof ConfigurazioneUrlInvocazioneRegola) {
						ConfigurazioneUrlInvocazioneRegola regola = (ConfigurazioneUrlInvocazioneRegola) oggetto;
						driver.getDriverConfigurazioneDB().createUrlInvocazioneRegola(regola);
					}

					/***********************************************************
					 * Operazioni su Registro *
					 **********************************************************/
					// PortaDominio
					if (oggetto instanceof PortaDominio && !(oggetto instanceof PdDControlStation)) {
						PortaDominio pdd = (PortaDominio) oggetto;
						driver.getDriverRegistroServiziDB().createPortaDominio(pdd);
					}
					
					// Gruppo
					if (oggetto instanceof Gruppo) {
						Gruppo gruppo = (Gruppo) oggetto;
						driver.getDriverRegistroServiziDB().createGruppo(gruppo);
					}
					
					// Ruolo
					if (oggetto instanceof Ruolo) {
						Ruolo ruolo = (Ruolo) oggetto;
						driver.getDriverRegistroServiziDB().createRuolo(ruolo);
					}
					
					// Scope
					if (oggetto instanceof Scope) {
						Scope scope = (Scope) oggetto;
						driver.getDriverRegistroServiziDB().createScope(scope);
					}

					// AccordoServizio
					if (oggetto instanceof AccordoServizioParteComune) {
						AccordoServizioParteComune as = (AccordoServizioParteComune) oggetto;
						driver.getDriverRegistroServiziDB().createAccordoServizioParteComune(as);
					}

					// AccordoCooperazione
					if (oggetto instanceof AccordoCooperazione) {
						AccordoCooperazione ac = (AccordoCooperazione) oggetto;
						driver.getDriverRegistroServiziDB().createAccordoCooperazione(ac);
					}

					// Servizio
					// Servizio Correlato
					if (oggetto instanceof AccordoServizioParteSpecifica) {
						AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) oggetto;
						driver.getDriverRegistroServiziDB().createAccordoServizioParteSpecifica(asps);
					}

					/***********************************************************
					 * Operazioni su Users *
					 **********************************************************/
					// User
					if (oggetto instanceof User) {
						User user = (User) oggetto;
						driver.getDriverUsersDB().createUser(user);
					}

					/***********************************************************
					 * Operazioni su Audit *
					 **********************************************************/
					// Filtro
					if (oggetto instanceof Filtro) {
						Filtro filtro = (Filtro) oggetto;
						driver.getDriverAuditDB().createFiltro(filtro);
					}
					
					/***********************************************************
					 * Operazioni su Controllo Traffico *
					 **********************************************************/
					// Configurazione Policy
					if(oggetto instanceof ConfigurazionePolicy) {
						ConfigurazionePolicy policy = (ConfigurazionePolicy) oggetto;
						driver.createConfigurazionePolicy(policy);
					}
					
					// Attivazione Policy
					if(oggetto instanceof AttivazionePolicy) {
						AttivazionePolicy policy = (AttivazionePolicy) oggetto;
						driver.createAttivazionePolicy(policy);
						
						updateProprietaOggettoPorta(policy, superUser, driver);
					}
					
					/***********************************************************
					 * Operazioni su Generic Properties *
					 **********************************************************/
					// Generic Propertie
					if(oggetto instanceof GenericProperties) {
						GenericProperties genericProperties = (GenericProperties) oggetto;
						driver.getDriverConfigurazioneDB().createGenericProperties(genericProperties);
					}
					
					/***********************************************************
					 * Operazioni su Registro Plugin *
					 **********************************************************/
					// Registro Plugin
					if(oggetto instanceof RegistroPlugin) {
						RegistroPlugin registroPlugin = (RegistroPlugin) oggetto;
						driver.getDriverConfigurazioneDB().createRegistroPlugin(registroPlugin);
					}
					
					// Registro Plugin Archivio
					if(oggetto instanceof RegistroPluginArchivio) {
						RegistroPluginArchivio registroPlugin = (RegistroPluginArchivio) oggetto;
						driver.getDriverConfigurazioneDB().createRegistroPluginArchivio(registroPlugin.getNomePlugin(), registroPlugin);
					}
					
					// Plugin Classi
					if(oggetto instanceof Plugin) {
						Plugin plugin = (Plugin) oggetto;
						driver.createPluginClassi(plugin);
					}
					
					/***********************************************************
					 * Operazioni su Allarmi *
					 **********************************************************/
					// Allarmi
					if(oggetto instanceof Allarme) {
						Allarme allarme = (Allarme) oggetto;
						driver.createAllarme(allarme);
						
						updateProprietaOggettoPorta(allarme, superUser, driver);
					}
					// Allarmi History
					if(oggetto instanceof AllarmeHistory) {
						AllarmeHistory allarme = (AllarmeHistory) oggetto;
						driver.createHistoryAllarme(allarme);
					}
					
					/***********************************************************
					 * Extended *
					 **********************************************************/
					if(extendedBean!=null && extendedServlet!=null){
						extendedServlet.performCreate(con, oggetto, extendedBean);
					}
					
					break;

				case CostantiControlStation.PERFORM_OPERATION_UPDATE:
					// Performing UPDATE operations

					/**
					 * Operazioni su ctrlstat
					 */
					if (oggetto instanceof PdDControlStation) {
						PdDControlStation pdd = (PdDControlStation) oggetto;
						driver.updatePdDControlStation(pdd);
					}

					
					/***********************************************************
					 * Caso Speciale di update di proprieta oggetto *
					 **********************************************************/
					if (oggetto instanceof UpdateProprietaOggetto) {
						UpdateProprietaOggetto update = (UpdateProprietaOggetto) oggetto;
						
						if(update.getIdPortaApplicativa()!=null) {
							driver.getDriverConfigurazioneDB().updateProprietaOggetto(update.getIdPortaApplicativa(), superUser);
						}
						else if(update.getIdPortaDelegata()!=null) {
							driver.getDriverConfigurazioneDB().updateProprietaOggetto(update.getIdPortaDelegata(), superUser);
						}
						else if(update.getIdServizioApplicativo()!=null) {
							driver.getDriverConfigurazioneDB().updateProprietaOggetto(update.getIdServizioApplicativo(), superUser);
						}
					}
					

					/***********************************************************
					 * Caso Speciale dei Soggetti *
					 **********************************************************/
					// soggetto ctrlstat
					if (oggetto instanceof SoggettoCtrlStat) {
						SoggettoCtrlStat soggetto = (SoggettoCtrlStat) oggetto;
						
						// prima chiamo il driver registro in quanto la
						// modifica
						// del soggetto riflette
						// i cambiamenti sui connettori
						if(this.registroServiziLocale){
							driver.getDriverRegistroServiziDB().updateSoggetto(soggetto.getSoggettoReg());
						}

						Soggetto sogConf = soggetto.getSoggettoConf();
						// imposto i valori old del soggetto configurazione
						// con
						// i nuovi valori
						// in quanto sono stati gia' modificati dalla
						// updateSoggetto (config) precedente
						if(this.registroServiziLocale){
							sogConf.setOldIDSoggettoForUpdate(new IDSoggetto(soggetto.getTipo(), soggetto.getNome()));
						}
						driver.getDriverConfigurazioneDB().updateSoggetto(sogConf);
					}
					
					
					// soggetto config
					if (oggetto instanceof org.openspcoop2.core.config.Soggetto) {
						
						org.openspcoop2.core.config.Soggetto sogConf = (org.openspcoop2.core.config.Soggetto) oggetto;
						driver.getDriverConfigurazioneDB().updateSoggetto(sogConf);

					}
					
					// soggetto reg
					if (oggetto instanceof org.openspcoop2.core.registry.Soggetto) {
						
						org.openspcoop2.core.registry.Soggetto sogReg = (org.openspcoop2.core.registry.Soggetto) oggetto;

						if(this.registroServiziLocale){
							driver.getDriverRegistroServiziDB().updateSoggetto(sogReg);
						}

					}
					
					/***********************************************************
					 * Operazioni su ConfigurazioneDB *
					 **********************************************************/
					
					// ServizioApplicativo
					if (oggetto instanceof ServizioApplicativo) {
						ServizioApplicativo sa = (ServizioApplicativo) oggetto;
						
						driver.getDriverConfigurazioneDB().updateServizioApplicativo(sa);
						if (sa.getIdSoggetto() == null || sa.getIdSoggetto() < 0) {
							sa.setIdSoggetto(DBUtils.getIdSoggetto(sa.getNomeSoggettoProprietario(), sa.getTipoSoggettoProprietario(), con, this.tipoDB, CostantiDB.SOGGETTI));
						}

					}
					// PortaDelegata
					if (oggetto instanceof PortaDelegata) {
						PortaDelegata pd = (PortaDelegata) oggetto;
						driver.getDriverConfigurazioneDB().updatePortaDelegata(pd);
						if (pd.getIdSoggetto() == null || pd.getIdSoggetto() < 0) {
							pd.setIdSoggetto(DBUtils.getIdSoggetto(pd.getNomeSoggettoProprietario(), pd.getTipoSoggettoProprietario(), con, this.tipoDB, CostantiDB.SOGGETTI));
						}
					}
					// PortaApplivativa
					if (oggetto instanceof PortaApplicativa) {
						PortaApplicativa pa = (PortaApplicativa) oggetto;
						driver.getDriverConfigurazioneDB().updatePortaApplicativa(pa);
						if (pa.getIdSoggetto() == null || pa.getIdSoggetto() < 0) {
							pa.setIdSoggetto(DBUtils.getIdSoggetto(pa.getNomeSoggettoProprietario(), pa.getTipoSoggettoProprietario(), con, this.tipoDB, CostantiDB.SOGGETTI));
						}
					}
					// RoutingTable
					if (oggetto instanceof RoutingTable) {
						RoutingTable rt = (RoutingTable) oggetto;
						driver.getDriverConfigurazioneDB().updateRoutingTable(rt);
					}
					// GestioneErrore
					if (oggetto instanceof GestioneErrore) {
						GestioneErrore gestErrore = (GestioneErrore) oggetto;
						driver.getDriverConfigurazioneDB().updateGestioneErroreComponenteCooperazione(gestErrore);
					}
					// Configurazione
					if (oggetto instanceof Configurazione) {
						Configurazione conf = (Configurazione) oggetto;
						driver.getDriverConfigurazioneDB().updateConfigurazione(conf);
					}
					// ConfigurazioneAccessoRegistro
					if (oggetto instanceof AccessoRegistro) {
						AccessoRegistro cfgAccessoRegistro = (AccessoRegistro) oggetto;
						driver.getDriverConfigurazioneDB().updateAccessoRegistro(cfgAccessoRegistro);
					}
					// ConfigurazioneAccessoRegistroRegistro
					if (oggetto instanceof AccessoRegistroRegistro) {
						AccessoRegistroRegistro carr = (AccessoRegistroRegistro) oggetto;
						driver.getDriverConfigurazioneDB().updateAccessoRegistro(carr);
					}
					// ConfigurazioneAccessoConfigurazione
					if (oggetto instanceof AccessoConfigurazione) {
						AccessoConfigurazione accesso = (AccessoConfigurazione) oggetto;
						driver.getDriverConfigurazioneDB().updateAccessoConfigurazione(accesso);
					}
					// ConfigurazioneAccessoDatiAutorizzazione
					if (oggetto instanceof AccessoDatiAutorizzazione) {
						AccessoDatiAutorizzazione accesso = (AccessoDatiAutorizzazione) oggetto;
						driver.getDriverConfigurazioneDB().updateAccessoDatiAutorizzazione(accesso);
					}
					// SystemProperties
					if (oggetto instanceof SystemProperties) {
						SystemProperties sps = (SystemProperties) oggetto;
						driver.getDriverConfigurazioneDB().updateSystemPropertiesPdD(sps);
					}
					// ConfigurazioneUrlInvocazioneRegola
					if (oggetto instanceof ConfigurazioneUrlInvocazioneRegola) {
						ConfigurazioneUrlInvocazioneRegola regola = (ConfigurazioneUrlInvocazioneRegola) oggetto;
						driver.getDriverConfigurazioneDB().updateUrlInvocazioneRegola(regola);
					}
					// ConfigurazioneUrlInvocazione
					if (oggetto instanceof ConfigurazioneUrlInvocazione) {
						ConfigurazioneUrlInvocazione confConfigurazioneUrlInvocazione = (ConfigurazioneUrlInvocazione) oggetto;
						
						Configurazione config = driver.getDriverConfigurazioneDB().getConfigurazioneGenerale();
						if(config.getUrlInvocazione()==null) {
							config.setUrlInvocazione(confConfigurazioneUrlInvocazione);
						}
						else {
							config.getUrlInvocazione().setBaseUrl(confConfigurazioneUrlInvocazione.getBaseUrl());
							config.getUrlInvocazione().setBaseUrlFruizione(confConfigurazioneUrlInvocazione.getBaseUrlFruizione());
						}
						
						driver.getDriverConfigurazioneDB().updateConfigurazione(config);
					}

					/***********************************************************
					 * Operazioni su Registro *
					 **********************************************************/
					// PortaDominio
					if (oggetto instanceof PortaDominio && !(oggetto instanceof PdDControlStation)) {
						PortaDominio pdd = (PortaDominio) oggetto;
						driver.getDriverRegistroServiziDB().updatePortaDominio(pdd);
					}
					
					// Gruppo
					if (oggetto instanceof Gruppo) {
						Gruppo gruppo = (Gruppo) oggetto;
						driver.getDriverRegistroServiziDB().updateGruppo(gruppo);
					}

					// Ruolo
					if (oggetto instanceof Ruolo) {
						Ruolo ruolo = (Ruolo) oggetto;
						driver.getDriverRegistroServiziDB().updateRuolo(ruolo);
					}
					
					// Scope
					if (oggetto instanceof Scope) {
						Scope scope = (Scope) oggetto;
						driver.getDriverRegistroServiziDB().updateScope(scope);
					}
					
					// AccordoServizio
					if (oggetto instanceof AccordoServizioParteComune) {
						AccordoServizioParteComune as = (AccordoServizioParteComune) oggetto;
						driver.getDriverRegistroServiziDB().updateAccordoServizioParteComune(as);
					}

					// AccordoCooperazione
					if (oggetto instanceof AccordoCooperazione) {
						AccordoCooperazione ac = (AccordoCooperazione) oggetto;
						driver.getDriverRegistroServiziDB().updateAccordoCooperazione(ac);
					}

					// Servizio
					// Servizio Correlato
					if (oggetto instanceof AccordoServizioParteSpecifica) {
						AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) oggetto;
						driver.getDriverRegistroServiziDB().updateAccordoServizioParteSpecifica(asps);
					}

					// PortType
					if (oggetto instanceof PortType) {
						PortType pt = (PortType) oggetto;
						driver.getDriverRegistroServiziDB().updatePortType(pt, superUser);
					}

					/***********************************************************
					 * Operazioni su Users *
					 **********************************************************/
					// User
					if (oggetto instanceof User) {
						User user = (User) oggetto;
						driver.getDriverUsersDB().updateUser(user);
					}

					/***********************************************************
					 * Operazioni su Audit *
					 **********************************************************/
					// Configurazione
					if (oggetto instanceof org.openspcoop2.web.lib.audit.dao.Configurazione) {
						org.openspcoop2.web.lib.audit.dao.Configurazione confAudit = 
								(org.openspcoop2.web.lib.audit.dao.Configurazione) oggetto;
						driver.getDriverAuditDB().updateConfigurazione(confAudit);
					}

					// Filtro
					if (oggetto instanceof Filtro) {
						Filtro filtro = (Filtro) oggetto;
						driver.getDriverAuditDB().updateFiltro(filtro);
					}
					
					/***********************************************************
					 * Operazioni su Controllo Traffico *
					 **********************************************************/

					// Configurazione Controllo Traffico 
					if(oggetto instanceof ConfigurazioneGenerale) {
						ConfigurazioneGenerale configurazioneControlloTraffico = (ConfigurazioneGenerale) oggetto;
						driver.updateConfigurazioneControlloTraffico(configurazioneControlloTraffico);
					}

					// Configurazione Policy
					if(oggetto instanceof ConfigurazionePolicy) {
						ConfigurazionePolicy policy = (ConfigurazionePolicy) oggetto;
						driver.updateConfigurazionePolicy(policy);
					}
					
					// Attivazione Policy
					if(oggetto instanceof AttivazionePolicy) {
						AttivazionePolicy policy = (AttivazionePolicy) oggetto;
						driver.updateAttivazionePolicy(policy);
						
						updateProprietaOggettoPorta(policy, superUser, driver);
					}
					
					/***********************************************************
					 * Operazioni su Generic Properties *
					 **********************************************************/
					// Generic Propertie
					if(oggetto instanceof GenericProperties) {
						GenericProperties genericProperties = (GenericProperties) oggetto;
						driver.getDriverConfigurazioneDB().updateGenericProperties(genericProperties);
					}
					
					/***********************************************************
					 * Operazioni su Registro Plugin *
					 **********************************************************/
					// Registro Plugin
					if(oggetto instanceof RegistroPlugin) {
						RegistroPlugin registroPlugin = (RegistroPlugin) oggetto;
						String nome = registroPlugin.getNome();
						if(registroPlugin.getOldNome()!=null) {
							nome = registroPlugin.getOldNome();
						}
						driver.getDriverConfigurazioneDB().updateDatiRegistroPlugin(nome,registroPlugin);
					}
					
					// Registro Plugin Archivio
					if(oggetto instanceof RegistroPluginArchivio) {
						RegistroPluginArchivio registroPlugin = (RegistroPluginArchivio) oggetto;
						driver.getDriverConfigurazioneDB().updateRegistroPluginArchivio(registroPlugin.getNomePlugin(),registroPlugin);
					}
					
					// Plugin Classi
					if(oggetto instanceof Plugin) {
						Plugin plugin = (Plugin) oggetto;
						driver.updatePluginClassi(plugin);
					}
					
					/***********************************************************
					 * Operazioni su Allarmi *
					 **********************************************************/
					// Allarmi
					if(oggetto instanceof Allarme) {
						Allarme allarme = (Allarme) oggetto;
						driver.updateAllarme(allarme);
						
						updateProprietaOggettoPorta(allarme, superUser, driver);
					}
					
					/***********************************************************
					 * Extended *
					 **********************************************************/
					if(extendedBean!=null && extendedServlet!=null){
						extendedServlet.performUpdate(con, oggetto, extendedBean);
					}
					
					
					break;

				case CostantiControlStation.PERFORM_OPERATION_DELETE:
					// Performing DELETE operations

					/**
					 * Operazioni su ctrlstat
					 */
					if (oggetto instanceof PdDControlStation) {
						PdDControlStation pdd = (PdDControlStation) oggetto;
						driver.deletePdDControlStation(pdd);
					}

					if (oggetto instanceof MappingFruizionePortaDelegata) {
						MappingFruizionePortaDelegata mapping = (MappingFruizionePortaDelegata) oggetto;
						driver.deleteMappingFruizionePortaDelegata(mapping);
						
						if(mapping.getIdServizio()!=null && mapping.getIdFruitore()!=null) {
							driver.updateProprietaOggettoFruizione(mapping.getIdServizio(), mapping.getIdFruitore() ,superUser ,false);
						}
					}
					
					if (oggetto instanceof MappingErogazionePortaApplicativa) {
						MappingErogazionePortaApplicativa mapping = (MappingErogazionePortaApplicativa) oggetto;
						driver.deleteMappingErogazionePortaApplicativa(mapping);
						
						if(mapping.getIdServizio()!=null) {
							driver.updateProprietaOggettoErogazione(mapping.getIdServizio() ,superUser ,false);
						}
					}
					
					
					/***********************************************************
					 * Caso Speciale dei Soggetti *
					 **********************************************************/
			
					// soggetto ctrlstat
					if (oggetto instanceof SoggettoCtrlStat) {
						SoggettoCtrlStat soggetto = (SoggettoCtrlStat) oggetto;

						// la delete basta farla solo una volta
						if(this.registroServiziLocale){
							driver.getDriverRegistroServiziDB().deleteSoggetto(soggetto.getSoggettoReg());
						}
						else{
							driver.getDriverConfigurazioneDB().deleteSoggetto(soggetto.getSoggettoConf());
						}

					}
					
					// soggetto config
					if (oggetto instanceof org.openspcoop2.core.config.Soggetto) {
						
						org.openspcoop2.core.config.Soggetto sogConf = (org.openspcoop2.core.config.Soggetto) oggetto;
						driver.getDriverConfigurazioneDB().deleteSoggetto(sogConf);

					}
					
					// soggetto reg
					if (oggetto instanceof org.openspcoop2.core.registry.Soggetto) {
						
						org.openspcoop2.core.registry.Soggetto sogReg = (org.openspcoop2.core.registry.Soggetto) oggetto;

						if(this.registroServiziLocale){
							driver.getDriverRegistroServiziDB().deleteSoggetto(sogReg);
						}

					}
					
					
					
					/***********************************************************
					 * Operazioni su ConfigurazioneDB *
					 **********************************************************/

					// ServizioApplicativo
					if (oggetto instanceof ServizioApplicativo) {
						ServizioApplicativo sa = (ServizioApplicativo) oggetto;
						driver.getDriverConfigurazioneDB().deleteServizioApplicativo(sa);
					}
					// PortaDelegata
					if (oggetto instanceof PortaDelegata) {
						PortaDelegata pd = (PortaDelegata) oggetto;
						driver.getDriverConfigurazioneDB().deletePortaDelegata(pd);
					}
					// PortaApplivativa
					if (oggetto instanceof PortaApplicativa) {
						PortaApplicativa pa = (PortaApplicativa) oggetto;
						driver.getDriverConfigurazioneDB().deletePortaApplicativa(pa);
					}
					// RoutingTable
					if (oggetto instanceof RoutingTable) {
						RoutingTable rt = (RoutingTable) oggetto;
						driver.getDriverConfigurazioneDB().deleteRoutingTable(rt);
					}
					// GestioneErrore
					if (oggetto instanceof GestioneErrore) {
						GestioneErrore gestErrore = (GestioneErrore) oggetto;
						driver.getDriverConfigurazioneDB().deleteGestioneErroreComponenteCooperazione(gestErrore);
					}
					// Configurazione
					if (oggetto instanceof Configurazione) {
						Configurazione conf = (Configurazione) oggetto;
						driver.getDriverConfigurazioneDB().deleteConfigurazione(conf);
					}
					// ConfigurazioneAccessoRegistro
					if (oggetto instanceof AccessoRegistro) {
						AccessoRegistro cfgAccessoRegistro = (AccessoRegistro) oggetto;
						driver.getDriverConfigurazioneDB().deleteAccessoRegistro(cfgAccessoRegistro);
					}
					// ConfigurazioneAccessoRegistroRegistro
					if (oggetto instanceof AccessoRegistroRegistro) {
						AccessoRegistroRegistro carr = (AccessoRegistroRegistro) oggetto;
						driver.getDriverConfigurazioneDB().deleteAccessoRegistro(carr);
					}
					// ConfigurazioneAccessoConfigurazione
					if (oggetto instanceof AccessoConfigurazione) {
						AccessoConfigurazione accesso = (AccessoConfigurazione) oggetto;
						driver.getDriverConfigurazioneDB().deleteAccessoConfigurazione(accesso);
					}
					// ConfigurazioneAccessoDatiAutorizzazione
					if (oggetto instanceof AccessoDatiAutorizzazione) {
						AccessoDatiAutorizzazione accesso = (AccessoDatiAutorizzazione) oggetto;
						driver.getDriverConfigurazioneDB().deleteAccessoDatiAutorizzazione(accesso);
					}
					// SystemProperties
					if (oggetto instanceof SystemProperties) {
						SystemProperties sps = (SystemProperties) oggetto;
						driver.getDriverConfigurazioneDB().deleteSystemPropertiesPdD(sps);
					}
					// ConfigurazioneUrlInvocazioneRegola
					if (oggetto instanceof ConfigurazioneUrlInvocazioneRegola) {
						ConfigurazioneUrlInvocazioneRegola regola = (ConfigurazioneUrlInvocazioneRegola) oggetto;
						driver.getDriverConfigurazioneDB().deleteUrlInvocazioneRegola(regola);
					}

					/***********************************************************
					 * Operazioni su Registro *
					 **********************************************************/
					// PortaDominio
					if (oggetto instanceof PortaDominio && !(oggetto instanceof PdDControlStation)) {
						PortaDominio pdd = (PortaDominio) oggetto;
						driver.getDriverRegistroServiziDB().deletePortaDominio(pdd);
					}
					
					// Ruolo
					if (oggetto instanceof Gruppo) {
						Gruppo gruppo = (Gruppo) oggetto;
						driver.getDriverRegistroServiziDB().deleteGruppo(gruppo);
					}

					// Ruolo
					if (oggetto instanceof Ruolo) {
						Ruolo ruolo = (Ruolo) oggetto;
						driver.getDriverRegistroServiziDB().deleteRuolo(ruolo);
					}
					
					// Scope
					if (oggetto instanceof Scope) {
						Scope scope = (Scope) oggetto;
						driver.getDriverRegistroServiziDB().deleteScope(scope);
					}
					
					// AccordoServizio
					if (oggetto instanceof AccordoServizioParteComune) {
						AccordoServizioParteComune as = (AccordoServizioParteComune) oggetto;
						driver.getDriverRegistroServiziDB().deleteAccordoServizioParteComune(as);
					}

					// AccordoCooperazione
					if (oggetto instanceof AccordoCooperazione) {
						AccordoCooperazione ac = (AccordoCooperazione) oggetto;
						driver.getDriverRegistroServiziDB().deleteAccordoCooperazione(ac);
					}

					// Servizio
					// Servizio Correlato
					if (oggetto instanceof AccordoServizioParteSpecifica) {
						AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) oggetto;
						driver.getDriverRegistroServiziDB().deleteAccordoServizioParteSpecifica(asps);
					}

					/***********************************************************
					 * Operazioni su Users *
					 **********************************************************/
					// User
					if (oggetto instanceof User) {
						User user = (User) oggetto;
						driver.getDriverUsersDB().deleteUser(user);
					}

					/***********************************************************
					 * Operazioni su Audit *
					 **********************************************************/
					// Filtro
					if (oggetto instanceof Filtro) {
						Filtro filtro = (Filtro) oggetto;
						driver.getDriverAuditDB().deleteFiltro(filtro);
					}

					// Operation
					if (oggetto instanceof org.openspcoop2.web.lib.audit.log.Operation) {
						org.openspcoop2.web.lib.audit.log.Operation auditOp = 
								(org.openspcoop2.web.lib.audit.log.Operation) oggetto;
						driver.getDriverAuditDBAppender().deleteOperation(auditOp);
					}
					
					/***********************************************************
					 * Operazioni su Controllo Traffico *
					 **********************************************************/
					// Configurazione Policy
					if(oggetto instanceof ConfigurazionePolicy) {
						ConfigurazionePolicy policy = (ConfigurazionePolicy) oggetto;
						driver.deleteConfigurazionePolicy(policy); 
					}
					
					// Attivazione Policy
					if(oggetto instanceof AttivazionePolicy) {
						AttivazionePolicy policy = (AttivazionePolicy) oggetto;
						// Il file importato potrebbe avere un identificativo diverso da quello effettivamente salvato
						if(policy.getAlias()==null) {
							driver.deleteAttivazionePolicy(policy); 
						}
						else {
							try {
								AttivazionePolicy att = driver.getPolicyByAlias(policy.getAlias(),
										(policy.getFiltro()!=null) ? policy.getFiltro().getRuoloPorta() : null,
										(policy.getFiltro()!=null) ? policy.getFiltro().getNomePorta() : null);
								driver.deleteAttivazionePolicy(att); 
							}catch(DriverControlStationNotFound notFound) {
								// ignore
							}
						}
						
						updateProprietaOggettoPorta(policy, superUser, driver);
					}
					
					/***********************************************************
					 * Operazioni su Generic Properties *
					 **********************************************************/
					// Generic Propertie
					if(oggetto instanceof GenericProperties) {
						GenericProperties genericProperties = (GenericProperties) oggetto;
						driver.getDriverConfigurazioneDB().deleteGenericProperties(genericProperties);
					}
					
					/***********************************************************
					 * Operazioni su Registro Plugin *
					 **********************************************************/
					// Registro Plugin
					if(oggetto instanceof RegistroPlugin) {
						RegistroPlugin registroPlugin = (RegistroPlugin) oggetto;
						driver.getDriverConfigurazioneDB().deleteRegistroPlugin(registroPlugin);
					}
					
					// Registro Plugin Archivio
					if(oggetto instanceof RegistroPluginArchivio) {
						RegistroPluginArchivio registroPlugin = (RegistroPluginArchivio) oggetto;
						driver.getDriverConfigurazioneDB().deleteRegistroPluginArchivio(registroPlugin.getNomePlugin(), registroPlugin);
					}
					
					// Plugin Classi
					if(oggetto instanceof Plugin) {
						Plugin plugin = (Plugin) oggetto;
						driver.deletePluginClassi(plugin);
					}
					
					/***********************************************************
					 * Operazioni su Allarmi *
					 **********************************************************/
					// Allarmi
					if(oggetto instanceof Allarme) {
						Allarme allarme = (Allarme) oggetto;
						// Il file importato potrebbe avere un identificativo diverso da quello effettivamente salvato
						if(allarme.getAlias()==null) {
							driver.deleteAllarme(allarme);
							
							updateProprietaOggettoPorta(allarme, superUser, driver);
						}
						else {
							try {
								Allarme all = driver.getAllarmeByAlias(allarme.getAlias(),
										(allarme.getFiltro()!=null) ? allarme.getFiltro().getRuoloPorta() : null,
										(allarme.getFiltro()!=null) ? allarme.getFiltro().getNomePorta() : null);
								driver.deleteAllarme(all); 
								
								updateProprietaOggettoPorta(all, superUser, driver);
							}catch(DriverControlStationNotFound notFound) {
								// ignore
							}
						}
					}
					
					/***********************************************************
					 * Extended *
					 **********************************************************/
					if(extendedBean!=null && extendedServlet!=null){
						extendedServlet.performDelete(con, oggetto, extendedBean);
					}
					
					/***********************************************************
					 * Operazioni su Remote Store Keys *
					 **********************************************************/
					// RemoteStoreKeys
					if(oggetto instanceof RemoteStoreKeyEntry) {
						RemoteStoreKeyEntry entry = (RemoteStoreKeyEntry) oggetto;
						RemoteStoreProviderDriverUtils.deleteRemoteStoreKeyEntry(driver.getDriverConfigurazioneDB(), entry.getIdRemoteStore(), entry.getId());
					}
										
					break;

				default:
					// Unkown operation type
					throw new ControlStationCoreException("[ControlStationCore::performOperation] Unkown operation type :" + operationType);

				}// fine switch


			}// chiudo for
			
			// ogni operazione e' andata a buon fine quindi faccio il commit
			con.commit();
			
		} catch (Exception e) {
			ControlStationCore.logError(e.getMessage(),e);

			try {
				ControlStationCore.logDebug("[ControlStationCore::performOperation] rollback on error :" + e.getMessage(), e);
				if(con!=null)
					con.rollback();
			} catch (Exception ex) {
				// ignore
			}

			throw new ControlStationCoreException(e.getMessage(),e);
		} finally {
			// qui posso riabilitare l'auto commit
			// e rilasciare la connessione
			try {
				if(con!=null) {
					con.setAutoCommit(true);
					ControlStationCore.dbM.releaseConnection(con);
				}
			} catch (Exception e) {
				// ignore
			}
		}

	}

	public void performOperationMultiTypes(String superUser, boolean smista,int[] operationTypes,Object ... oggetti) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverControlStationException, DriverRegistroServiziException, ControlStationCoreException, UtilsException {
		String nomeMetodo = "performOperationIbrido";
		ControlStationCore.logInfo(getPrefixMethod(nomeMetodo)+"performing operation on objects " + this.getClassNames(oggetti));
		Tipologia[] tipoOperazione = new Tipologia[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			if(operationTypes[i]==CostantiControlStation.PERFORM_OPERATION_CREATE) {
				tipoOperazione[i] = Tipologia.ADD;
			}
			else if(operationTypes[i]==CostantiControlStation.PERFORM_OPERATION_UPDATE) {
				tipoOperazione[i] = Tipologia.CHANGE;
			}
			else {
				tipoOperazione[i] = Tipologia.DEL;
			}
		}

		this.cryptPassword(tipoOperazione, oggetti);
		
		IDOperazione [] idOperazione = null;
		boolean auditDisabiltato = false;
		try{
			idOperazione = this.performAuditRequest(tipoOperazione, superUser, oggetti);
		}catch(AuditDisabilitatoException disabilitato){
			auditDisabiltato = true;
		}
		try{
			this.performOperation(operationTypes, superUser, smista, oggetti);
			if(!auditDisabiltato){
				this.performAuditComplete(idOperazione, tipoOperazione, superUser, oggetti);
			}
		}catch(Exception e){
			if(!auditDisabiltato){
				this.performAuditError(idOperazione, e.getMessage(), tipoOperazione, superUser, oggetti);
			}
			throw e;
		}

		ControlStationCore.logInfo(getPrefixMethod(nomeMetodo)+"performed operation on objects " + this.getClassNames(oggetti));
	}

	/**
	 * Crea un nuovo oggetto nella govwayConsole e si occupa di inoltrare
	 * l'operazione nella coda dello Smistatore
	 * @throws UtilsException 
	 */
	public void performCreateOperation(String superUser, boolean smista, Object ... oggetti) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverControlStationException, DriverRegistroServiziException, ControlStationCoreException, UtilsException {
		String nomeMetodo = "performCreateOperation";
		String operationType = "CREATE";
		ControlStationCore.logInfo(getPrefixMethod(nomeMetodo)+"performing operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));
		int[] operationTypes = new int[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			operationTypes[i] = CostantiControlStation.PERFORM_OPERATION_CREATE;
		}
		Tipologia[] tipoOperazione = new Tipologia[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			tipoOperazione[i] = Tipologia.ADD;
		}

		this.cryptPassword(tipoOperazione, oggetti);
		
		this.setProprietaOggetto(superUser, tipoOperazione, oggetti);
		
		IDOperazione [] idOperazione = null;
		boolean auditDisabiltato = false;
		try{
			idOperazione = this.performAuditRequest(tipoOperazione, superUser, oggetti);
		}catch(AuditDisabilitatoException disabilitato){
			auditDisabiltato = true;
		}
		try{
			this.performOperation(operationTypes, superUser, smista, oggetti);
			if(!auditDisabiltato){
				this.performAuditComplete(idOperazione, tipoOperazione, superUser, oggetti);
			}
		}catch(Exception e){
			if(!auditDisabiltato){
				this.performAuditError(idOperazione, e.getMessage(), tipoOperazione, superUser, oggetti);
			}
			throw e;
		}

		ControlStationCore.logInfo(getPrefixMethod(nomeMetodo)+"performed operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));
	}

	/**
	 * Aggiorna un oggetto nella govwayConsole e si occupa di inoltrare
	 * l'operazione nella coda dello Smistatore
	 * @throws UtilsException 
	 */
	public void performUpdateOperation(String superUser, boolean smista, Object ... oggetti) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverControlStationException, DriverRegistroServiziException, ControlStationCoreException, UtilsException {
		String nomeMetodo = "performUpdateOperation";
		String operationType = "UPDATE";

		ControlStationCore.logInfo(getPrefixMethod(nomeMetodo)+"performing operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));
		int[] operationTypes = new int[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			operationTypes[i] = CostantiControlStation.PERFORM_OPERATION_UPDATE;
		}
		Tipologia[] tipoOperazione = new Tipologia[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			tipoOperazione[i] = Tipologia.CHANGE;
		}

		this.cryptPassword(tipoOperazione, oggetti);
		
		this.setProprietaOggetto(superUser, tipoOperazione, oggetti);
		
		IDOperazione [] idOperazione = null;
		boolean auditDisabiltato = false;
		try{
			idOperazione = this.performAuditRequest(tipoOperazione, superUser, oggetti);
		}catch(AuditDisabilitatoException disabilitato){
			auditDisabiltato = true;
		}
		try{
			this.performOperation(operationTypes, superUser, smista, oggetti);
			if(!auditDisabiltato){
				this.performAuditComplete(idOperazione, tipoOperazione, superUser, oggetti);
			}
		}catch(Exception e){
			if(!auditDisabiltato){
				this.performAuditError(idOperazione, e.getMessage(), tipoOperazione, superUser, oggetti);
			}
			throw e;
		}

		ControlStationCore.logInfo(getPrefixMethod(nomeMetodo)+"performed operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));

	}

	/**
	 * Cancella un oggetto nella govwayConsole e si occupa di inoltrare
	 * l'operazione nella coda dello Smistatore
	 * @throws UtilsException 
	 */
	public void performDeleteOperation(String superUser, boolean smista, Object ... oggetti) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverControlStationException, DriverRegistroServiziException, ControlStationCoreException, UtilsException {
		String nomeMetodo = "performDeleteOperation";
		String operationType = "DELETE";

		ControlStationCore.logInfo(getPrefixMethod(nomeMetodo)+"performing operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));
		int[] operationTypes = new int[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			operationTypes[i] = CostantiControlStation.PERFORM_OPERATION_DELETE;
		}
		Tipologia[] tipoOperazione = new Tipologia[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			tipoOperazione[i] = Tipologia.DEL;
		}
		
		this.cryptPassword(tipoOperazione, oggetti);
		
		IDOperazione [] idOperazione = null;
		boolean auditDisabiltato = false;
		try{
			idOperazione = this.performAuditRequest(tipoOperazione, superUser, oggetti);
		}catch(AuditDisabilitatoException disabilitato){
			auditDisabiltato = true;
		}
		try{
			this.performOperation(operationTypes, superUser, smista, oggetti);
			if(!auditDisabiltato){
				this.performAuditComplete(idOperazione, tipoOperazione, superUser, oggetti);
			}
		}catch(Exception e){
			if(!auditDisabiltato){
				this.performAuditError(idOperazione, e.getMessage(), tipoOperazione, superUser, oggetti);
			}
			throw e;
		}

		ControlStationCore.logInfo(getPrefixMethod(nomeMetodo)+"performed operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));

	}

	private <T> ArrayList<String> getClassNames(T[] array) {
		ArrayList<String> c = new ArrayList<>();
		for (T type : array) {
			c.add(type.getClass().getName());
		}

		return c;
	}



	/* ************** VERIFICA CONSISTENZA PROTOCOLLI ***************** */

	private static Boolean verificaConsistenzaProtocolli = null;
	
	private static void verificaConsistenzaProtocolli(ControlStationCore core) {
		if(verificaConsistenzaProtocolli==null) {
			_verificaConsistenzaProtocolli(core);
		}
	}
	private static synchronized void _verificaConsistenzaProtocolli(ControlStationCore core) {
		if(verificaConsistenzaProtocolli==null) {
			StringBuilder verificaConfigurazioneProtocolliBuilder = new StringBuilder();
			boolean configurazioneCorretta = core.verificaConfigurazioneProtocolliRispettoSoggettiDefault(verificaConfigurazioneProtocolliBuilder); 
			
			if(!configurazioneCorretta) {
				log.error("il controllo di consistenza tra Profili di Interoperabilità attivati e la configurazione del Gateway ha rilevato inconsistenze: \n"+verificaConfigurazioneProtocolliBuilder.toString());
			}
			
			verificaConsistenzaProtocolli = true;
		}
	}
	
	public boolean verificaConfigurazioneProtocolliRispettoSoggettiDefault(StringBuilder error) {
		
		try {
			ProtocolFactoryManager protocolFactoryManager = ProtocolFactoryManager.getInstance();
			
			// Prima verifico che per ogni protocollo a bordo dell'archivio console vi sia il corrispettivo soggetto di default
			// Localizza eventuali protocolli aggiunti senza avere aggiunto anche il sql relativo al soggetto nel database
			Enumeration<String> protocolNames = protocolFactoryManager.getProtocolFactories().keys();
			while (protocolNames.hasMoreElements()) {
				String protocolName = (String) protocolNames.nextElement();
				IDSoggetto idSoggetto = this.getSoggettoOperativoDefault(null, protocolName);
				if(idSoggetto==null || idSoggetto.getTipo()==null || idSoggetto.getNome()==null) {
					if(error.length()>0) {
						error.append("\n");
					}
					error.append("Configurazione non corretta; non è stato rilevato un soggetto di default per il profilo di interoperabilità '"+NamingUtils.getLabelProtocollo(protocolName)+"'.");
					
				}
			}
			
			if(error.length()>0) {
				error.append("\n");
				error.append("Se sono stati aggiunti nuovi profili di interoperabilità rispetto alla precedente installazione, devono essere eseguiti gli script sql generati dall'installer 'dist/sql/profili/GovWay_upgrade_initialize-profilo-<new-profile>.sql");
			}
			
			if(error.length()<=0) {
				// se non vi sono stati errori, recupero tutti i tipi di soggetto di default dal database e verifico che vi sia il protocollo nell'archivio
				// Localizza eventuali aggiornamenti dove sono stati inseriti meno protocolli di quante configurazioni sono presenti sul database.
				List<IDSoggetto> soggettiDefault = this.getSoggettiDefault();
				if(soggettiDefault==null || soggettiDefault.isEmpty()) {
					error.append("Configurazione non corretta; non è stato rilevato alcun soggetto di default");
				}
				else {
					Map<String, List<IDSoggetto>> defaultByProtocol = new HashMap<String, List<IDSoggetto>>(); 
					List<String> tipi_con_protocolli_censiti = protocolFactoryManager.getOrganizationTypesAsList();
					for (IDSoggetto idSoggetto : soggettiDefault) {
						if(!tipi_con_protocolli_censiti.contains(idSoggetto.getTipo())) {
							error.append("Configurazione non corretta; Non esiste un profilo di interoperabilità che possa gestire il soggetto di default '"+idSoggetto.toString()+"' rilevato.");
						}
						String protocolName = protocolFactoryManager.getProtocolByOrganizationType(idSoggetto.getTipo());
						List<IDSoggetto> l = null;
						if(defaultByProtocol.containsKey(protocolName)) {
							l = defaultByProtocol.get(protocolName);
						}
						else {
							l = new ArrayList<IDSoggetto>();
							defaultByProtocol.put(protocolName, l);
						}
						l.add(idSoggetto);
					}
					
					if(!defaultByProtocol.isEmpty()) {
						for (String protocolName : defaultByProtocol.keySet()) {
							List<IDSoggetto> l = defaultByProtocol.get(protocolName);
							if(l.size()>1) {
								if(error.length()>0) {
									error.append("\n");
								}
								StringBuilder sbSoggetti = new StringBuilder();
								for (IDSoggetto id : l) {
									if(sbSoggetti.length()>0) {
										sbSoggetti.append(", ");
									}
									sbSoggetti.append(NamingUtils.getLabelSoggetto(id));
								}
								error.append("Sono stati riscontrati più soggetti di default associati al profilo di interoperabilità '"+NamingUtils.getLabelProtocollo(protocolName)+"': "+sbSoggetti.toString());		
							}
						}
					}
				}
			}
			
		}catch(Exception e) {
			if(error.length()>0) {
				error.append("\n");
			}
			error.append("Verifica fallita; "+e.getMessage());
		}
		
		return error.length()<=0;
	}




	/* ************** AUDIT ***************** */

	// Gestione audit
	private static AuditAppender auditManager = null;
	private static Semaphore semaphoreAuditLock = new Semaphore("ControlStationCoreAudit");
	public static void clearAuditManager(){
		ControlStationCore.semaphoreAuditLock.acquireThrowRuntime("clearAuditManager");
		try {
			ControlStationCore.auditManager = null;	
		}finally {
			ControlStationCore.semaphoreAuditLock.release("clearAuditManager");
		}
	}
	private static synchronized void initializeAuditManager(String tipoDatabase) throws DriverControlStationException{
		ControlStationCore.semaphoreAuditLock.acquireThrowRuntime("initializeAuditManager");
		try {
			if(ControlStationCore.auditManager==null){
				ControlStationCore.auditManager= new AuditAppender();

				Connection con = null;
				org.openspcoop2.web.lib.audit.dao.Configurazione configurazioneAuditing = null;
				try{
					con = ControlStationCore.dbM.getConnection();
					if (con == null)
						throw new Exception("Connessione non disponibile");

					DriverAudit driverAudit = new DriverAudit(con,tipoDatabase);
					configurazioneAuditing = driverAudit.getConfigurazione();
					ControlStationCore.checkAuditDBAppender(configurazioneAuditing, tipoDatabase);

				}catch(Exception e){
					throw new DriverControlStationException("Inizializzazione auditManager non riuscita (LetturaConfigurazione): "+e.getMessage(),e);
				}finally{
					try{
						if(con!=null)
							con.close();
					}catch(Exception e){
						// close
					}
				}

				try{
					ControlStationCore.auditManager.initializeAudit(configurazioneAuditing, IDBuilder.getIDBuilder());
				}catch(Exception e){
					throw new DriverControlStationException("Inizializzazione auditManager non riuscita: "+e.getMessage(),e);
				}
			}
		}finally {
			ControlStationCore.semaphoreAuditLock.release("initializeAuditManager");
		}
	}
	public static synchronized void updateAuditManagerConfiguration(String tipoDatabase) throws DriverControlStationException{
		if(ControlStationCore.auditManager==null){
			throw new DriverControlStationException("AuditManager non inizializzato");
		}
		ControlStationCore.semaphoreAuditLock.acquireThrowRuntime("updateAuditManagerConfiguration");
		try {
			Connection con = null;
			org.openspcoop2.web.lib.audit.dao.Configurazione configurazioneAuditing = null;
			try{
				con = ControlStationCore.dbM.getConnection();
				if (con == null)
					throw new DriverControlStationException("Connessione non disponibile");

				DriverAudit driverAudit = new DriverAudit(con,tipoDatabase);
				configurazioneAuditing = driverAudit.getConfigurazione();
				ControlStationCore.checkAuditDBAppender(configurazioneAuditing, tipoDatabase);

			}catch(Exception e){
				throw new DriverControlStationException("Aggiornamento configurazione auditManager non riuscita (LetturaConfigurazione): "+e.getMessage(),e);
			}finally{
				try{
					if(con!=null)
						con.close();
				}catch(Exception e){
					// close
				}
			}

			try{
				ControlStationCore.auditManager.updateConfigurazioneAuditing(configurazioneAuditing);
			}catch(Exception e){
				throw new DriverControlStationException("Aggiornamento configurazione auditManager non riuscita: "+e.getMessage(),e);
			}
		}finally {
			ControlStationCore.semaphoreAuditLock.release("updateAuditManagerConfiguration");
		}
	}
	private static void checkAuditDBAppender(org.openspcoop2.web.lib.audit.dao.Configurazione configurazioneAuditing,String tipoDatabase){
		for(int i=0; i<configurazioneAuditing.sizeAppender(); i++){
			Appender appender = configurazioneAuditing.getAppender(i);
			if(AuditDBAppender.class.getName().equals(appender.getClassName())){
				boolean findDBKeyword = false;
				for(int j=0; j<appender.sizeProperties(); j++){
					if("@DB_INTERFACCIA@".equals(appender.getProperty(j).getValue())){
						findDBKeyword = true;
					}
				}
				if(findDBKeyword){
					while(appender.sizeProperties()>0){
						appender.removeProperty(0);
					}
					AppenderProperty apDS = new AppenderProperty();
					apDS.setName("datasource");
					apDS.setValue(ControlStationCore.dbM.getDataSourceName());
					appender.addProperty(apDS);
					AppenderProperty apTipoDatabase = new AppenderProperty();
					apTipoDatabase.setName("tipoDatabase");
					apTipoDatabase.setValue(tipoDatabase);
					appender.addProperty(apTipoDatabase);
					Properties contextDS = ControlStationCore.dbM.getDataSourceContext();
					if(contextDS.size()>0){
						Enumeration<?> keys = contextDS.keys();
						while(keys.hasMoreElements()){
							String key = (String) keys.nextElement();
							AppenderProperty apCTX = new AppenderProperty();
							apCTX.setName(key);
							apCTX.setValue(contextDS.getProperty(key));
							appender.addProperty(apCTX);
						}
					}
				}
			}
		}
	}	
	public static AuditAppender getAuditManagerInstance(String tipoDatabase)throws DriverControlStationException{
		if(ControlStationCore.auditManager==null){
			ControlStationCore.initializeAuditManager(tipoDatabase);
		}
		return ControlStationCore.auditManager;
	}

	public void performAuditLogin(String user){

		String msg = user+":"+"completed:LOGIN";

		// loggo tramite auditManager
		boolean auditAbilitato = true;
		try{
			AuditAppender auditManager = ControlStationCore.getAuditManagerInstance(this.tipoDB);
			auditManager.registraOperazioneAccesso(Tipologia.LOGIN, user,msg, this.isAuditingRegistrazioneElementiBinari);
		}catch(AuditDisabilitatoException disabilitato){
			ControlStationCore.logDebug("Auditing dell'operazione ["+msg+"] non effettuato: "+disabilitato.getMessage());
			auditAbilitato = false;
		}catch(Exception e){
			ControlStationCore.logError("Auditing dell'operazione ["+msg+"] non riuscito: "+e.getMessage(),e);
		}

		// loggo su file dell'interfaccia (dopo chiamata ad auditManager, in modo che viene registrato il msg solo se abilitato l'audit)
		if(auditAbilitato)
			ControlStationCore.logInfo(msg);
	}

	public void performAuditLogout(String user){

		String msg = user+":"+"completed:LOGOUT";

		// loggo tramite auditManager
		boolean auditAbilitato = true;
		try{
			AuditAppender auditManager = ControlStationCore.getAuditManagerInstance(this.tipoDB);
			auditManager.registraOperazioneAccesso(Tipologia.LOGOUT, user,msg, this.isAuditingRegistrazioneElementiBinari);

		}catch(AuditDisabilitatoException disabilitato){
			ControlStationCore.logDebug("Auditing dell'operazione ["+msg+"] non effettuato: "+disabilitato.getMessage());
			auditAbilitato = false;
		}catch(Exception e){
			ControlStationCore.logError("Auditing dell'operazione ["+msg+"] non riuscito: "+e.getMessage(),e);
		}

		// loggo su file dell'interfaccia (dopo chiamata ad auditManager, in modo che viene registrato il msg solo se abilitato l'audit)
		if(auditAbilitato)
			ControlStationCore.logInfo(msg);
	}

	public IDOperazione[] performAuditRequest(Tipologia[] operationTypes, String user, Object ... obj) throws AuditDisabilitatoException{

		IDOperazione[] idOperazione = new IDOperazione[obj.length];
		for (int i = 0; i < obj.length; i++) {
			Tipologia tipoOperazione = operationTypes[i];
			Object oggetto = obj[i];

			if(oggetto instanceof WrapperExtendedBean){
				WrapperExtendedBean w = (WrapperExtendedBean) oggetto;
				if(w.isManageOriginalBean()){
					oggetto = w.getOriginalBean();	
				}
				// altrimenti viene gestito come IExtendedBean
				else{
					oggetto = w.getExtendedBean();
				}
			}
			
			String msg = null;
			try{
				msg = this.generaMsgAuditing(user, Stato.REQUESTING, tipoOperazione, oggetto);
			}catch(Exception e){
				ControlStationCore.logError("GenerazioneIDOperazione non riuscita: "+e.getMessage(),e);
				msg = "GenerazioneIDOperazione non riuscita: "+e.getMessage();
			}

			// loggo tramite auditManager
			boolean auditAbilitato = true;
			try{
				AuditAppender auditManager = ControlStationCore.getAuditManagerInstance(this.tipoDB);

				// clono oggetto se e' un accordo di servizio, un servizio o un accordo di cooperazione (oggetti che contengono allegati)
				if(oggetto instanceof AccordoServizioParteComune){

					AccordoServizioParteComune asClone = (AccordoServizioParteComune) ((AccordoServizioParteComune)oggetto).clone();
					for(int k=0; k<asClone.sizeAllegatoList(); k++){
						Documento d = asClone.getAllegato(k);
						if(d.getByteContenuto()==null){
							// Inserisco contenuto per checksum
							d.setByteContenuto(this.readContenutoAllegato(d.getId()));
						}
					}
					for(int k=0; k<asClone.sizeSpecificaSemiformaleList(); k++){
						Documento d = asClone.getSpecificaSemiformale(k);
						if(d.getByteContenuto()==null){
							// Inserisco contenuto per checksum
							d.setByteContenuto(this.readContenutoAllegato(d.getId()));
						}
					}
					if(asClone.getServizioComposto()!=null){
						for(int k=0; k<asClone.getServizioComposto().sizeSpecificaCoordinamentoList(); k++){
							Documento d = asClone.getServizioComposto().getSpecificaCoordinamento(k);
							if(d.getByteContenuto()==null){
								// Inserisco contenuto per checksum
								d.setByteContenuto(this.readContenutoAllegato(d.getId()));
							}
						}
					}

					idOperazione[i] = auditManager.registraOperazioneInFaseDiElaborazione(tipoOperazione, asClone, user,msg, this.isAuditingRegistrazioneElementiBinari);

				}else if(oggetto instanceof AccordoCooperazione){

					AccordoCooperazione acClone = (AccordoCooperazione) ((AccordoCooperazione)oggetto).clone();
					for(int k=0; k<acClone.sizeAllegatoList(); k++){
						Documento d = acClone.getAllegato(k);
						if(d.getByteContenuto()==null){
							// Inserisco contenuto per checksum
							d.setByteContenuto(this.readContenutoAllegato(d.getId()));
						}
					}
					for(int k=0; k<acClone.sizeSpecificaSemiformaleList(); k++){
						Documento d = acClone.getSpecificaSemiformale(k);
						if(d.getByteContenuto()==null){
							// Inserisco contenuto per checksum
							d.setByteContenuto(this.readContenutoAllegato(d.getId()));
						}
					}

					idOperazione[i] = auditManager.registraOperazioneInFaseDiElaborazione(tipoOperazione, acClone, user,msg, this.isAuditingRegistrazioneElementiBinari);

				}else if(oggetto instanceof AccordoServizioParteSpecifica){

					AccordoServizioParteSpecifica sClone = (AccordoServizioParteSpecifica) ((AccordoServizioParteSpecifica)oggetto).clone();
					for(int k=0; k<sClone.sizeAllegatoList(); k++){
						Documento d = sClone.getAllegato(k);
						if(d.getByteContenuto()==null){
							// Inserisco contenuto per checksum
							d.setByteContenuto(this.readContenutoAllegato(d.getId()));
						}
					}
					for(int k=0; k<sClone.sizeSpecificaSemiformaleList(); k++){
						Documento d = sClone.getSpecificaSemiformale(k);
						if(d.getByteContenuto()==null){
							// Inserisco contenuto per checksum
							d.setByteContenuto(this.readContenutoAllegato(d.getId()));
						}
					}
					for(int k=0; k<sClone.sizeSpecificaSicurezzaList(); k++){
						Documento d = sClone.getSpecificaSicurezza(k);
						if(d.getByteContenuto()==null){
							// Inserisco contenuto per checksum
							d.setByteContenuto(this.readContenutoAllegato(d.getId()));
						}
					}
					for(int k=0; k<sClone.sizeSpecificaLivelloServizioList(); k++){
						Documento d = sClone.getSpecificaLivelloServizio(k);
						if(d.getByteContenuto()==null){
							// Inserisco contenuto per checksum
							d.setByteContenuto(this.readContenutoAllegato(d.getId()));
						}
					}

					idOperazione[i] = auditManager.registraOperazioneInFaseDiElaborazione(tipoOperazione, sClone, user,msg, this.isAuditingRegistrazioneElementiBinari);

				}else {
					idOperazione[i] = auditManager.registraOperazioneInFaseDiElaborazione(tipoOperazione, oggetto, user,msg, this.isAuditingRegistrazioneElementiBinari);
				}

			}catch(AuditDisabilitatoException disabilitato){
				ControlStationCore.logDebug("Auditing dell'operazione ["+msg+"] non effettuato: "+disabilitato.getMessage());
				auditAbilitato = false;
				throw disabilitato;
			}catch(Exception e){
				ControlStationCore.logError("Auditing dell'operazione ["+msg+"] non riuscito: "+e.getMessage(),e);
			}

			// loggo su file dell'interfaccia (dopo chiamata ad auditManager, in modo che viene registrato il msg solo se abilitato l'audit)
			if(auditAbilitato)
				ControlStationCore.logInfo(msg);
		}
		return idOperazione;
	}

	private byte[] readContenutoAllegato(long idDocumento) throws DriverRegistroServiziException{

		Connection con = null;
		String nomeMetodo = "readContenutoAllegato";
		DriverRegistroServiziDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverRegistroServiziDB(con, ControlStationCore.log, this.tipoDB);

			Documento doc = driver.getDocumento(idDocumento, true);
			if(doc!=null)
				return doc.getByteContenuto();
			else
				return null;

		}  catch (DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public void performAuditComplete(IDOperazione[] idOperazione,Tipologia[] operationTypes,String user, Object ... obj){

		for (int i = 0; i < obj.length; i++) {
			Tipologia tipoOperazione = operationTypes[i];
			Object oggetto = obj[i];

			//loggo su file dell'interfaccia
			String msg = null;
			try{
				msg = this.generaMsgAuditing(user, Stato.COMPLETED, tipoOperazione, oggetto);
			}catch(Exception e){
				ControlStationCore.logError("GenerazioneIDOperazione non riuscita: "+e.getMessage(),e);
				msg = "GenerazioneIDOperazione non riuscita: "+e.getMessage();
			}

			// loggo tramite auditManager
			boolean auditAbilitato = true;
			try{
				AuditAppender auditManager = ControlStationCore.getAuditManagerInstance(this.tipoDB);
				auditManager.registraOperazioneCompletataConSuccesso(idOperazione[i],msg);

			}catch(AuditDisabilitatoException disabilitato){
				ControlStationCore.logDebug("Auditing dell'operazione ["+msg+"] non effettuato: "+disabilitato.getMessage());
				auditAbilitato = false;
			}catch(Exception e){
				ControlStationCore.logError("Auditing dell'operazione ["+msg+"] non riuscito: "+e.getMessage(),e);
			}

			// loggo su file dell'interfaccia (dopo chiamata ad auditManager, in modo che viene registrato il msg solo se abilitato l'audit)
			if(auditAbilitato)
				ControlStationCore.logInfo(msg);
		}
	}

	@SafeVarargs
	public final <Type> void performAuditError(IDOperazione[] idOperazione,String motivoErrore,Tipologia[] operationTypes,String user, Type... obj){

		for (int i = 0; i < obj.length; i++) {
			Tipologia tipoOperazione = operationTypes[i];
			Type oggetto = obj[i];

			//loggo su file dell'interfaccia
			String msg = null;
			try{
				msg = this.generaMsgAuditing(user, Stato.ERROR, tipoOperazione, oggetto);
			}catch(Exception e){
				ControlStationCore.logError("GenerazioneIDOperazione non riuscita: "+e.getMessage(),e);
				msg = "GenerazioneIDOperazione non riuscita: "+e.getMessage();
			}

			// loggo tramite auditManager
			boolean auditAbilitato = true;
			try{
				AuditAppender auditManager = ControlStationCore.getAuditManagerInstance(this.tipoDB);
				auditManager.registraOperazioneTerminataConErrore(idOperazione[i], motivoErrore,msg);

			}catch(AuditDisabilitatoException disabilitato){
				ControlStationCore.logDebug("Auditing dell'operazione ["+msg+"] non effettuato: "+disabilitato.getMessage());
				auditAbilitato = false;
			}catch(Exception e){
				ControlStationCore.logError("Auditing dell'operazione ["+msg+"] non riuscito: "+e.getMessage(),e);
			}

			// loggo su file dell'interfaccia (dopo chiamata ad auditManager, in modo che viene registrato il msg solo se abilitato l'audit)
			if(auditAbilitato)
				ControlStationCore.logError(msg+", errore avvenuto:"+motivoErrore);
		}
	}

	private String generaMsgAuditing(String user,Stato statoOperazione,Tipologia tipoOperazione,Object oggetto) throws DriverRegistroServiziException {
		String msg = user+":"+statoOperazione.toString()+":"+tipoOperazione.toString();

		// ControlStation
		if (oggetto instanceof PdDControlStation) {
			PdDControlStation pdd = (PdDControlStation) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+pdd.getNome()+">";
		}
		else if(oggetto instanceof MappingFruizionePortaDelegata){
			MappingFruizionePortaDelegata mapping = (MappingFruizionePortaDelegata) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("FR[");
			bf.append(mapping.getIdFruitore().getTipo()+"/"+mapping.getIdFruitore().getNome());
			bf.append("] SERV[");
			bf.append(mapping.getIdServizio().toString());
			bf.append("] PD["+mapping.getIdPortaDelegata().getNome());
			bf.append("]");
			msg+=":<"+bf.toString()+">";
		}
		else if(oggetto instanceof MappingErogazionePortaApplicativa){
			MappingErogazionePortaApplicativa mapping = (MappingErogazionePortaApplicativa) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("SERV[");
			bf.append(mapping.getIdServizio().toString());
			bf.append("] PA["+mapping.getIdPortaApplicativa().getNome());
			bf.append("]");
			msg+=":<"+bf.toString()+">";
		}
		else if (oggetto instanceof SoggettoCtrlStat) {
			SoggettoCtrlStat soggetto = (SoggettoCtrlStat) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+soggetto.getTipo()+"/"+soggetto.getNome()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				String oldTipo = soggetto.getOldTipoForUpdate()!=null ? soggetto.getOldTipoForUpdate() : soggetto.getTipo(); 
				String oldNome = soggetto.getOldNomeForUpdate()!=null ? soggetto.getOldNomeForUpdate() : soggetto.getNome();
				if( (oldTipo.equals(soggetto.getTipo())==false) || (oldNome.equals(soggetto.getNome())==false) )
					msg+=":OLD<"+oldTipo+"/"+oldNome+">";
			}
		}

		// RegistroServizi
		
		// Gruppo
		else if (oggetto instanceof Gruppo) {
			Gruppo g = (Gruppo) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+g.getNome()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				if(g.getOldIDGruppoForUpdate()!=null && g.getNome().equals(g.getOldIDGruppoForUpdate().getNome())==false){
					msg+=":OLD<"+g.getOldIDGruppoForUpdate().getNome()+">";
				}
			}
		}

		// Ruolo
		else if (oggetto instanceof Ruolo) {
			Ruolo r = (Ruolo) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+r.getNome()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				if(r.getOldIDRuoloForUpdate()!=null && r.getNome().equals(r.getOldIDRuoloForUpdate().getNome())==false){
					msg+=":OLD<"+r.getOldIDRuoloForUpdate().getNome()+">";
				}
			}
		}
		
		// Scope
		else if (oggetto instanceof Scope) {
			Scope r = (Scope) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+r.getNome()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				if(r.getOldIDScopeForUpdate()!=null && r.getNome().equals(r.getOldIDScopeForUpdate().getNome())==false){
					msg+=":OLD<"+r.getOldIDScopeForUpdate().getNome()+">";
				}
			}
		}
		
		// AccordoCooperazione
		else if (oggetto instanceof AccordoCooperazione) {
			AccordoCooperazione ac = (AccordoCooperazione) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+this.idAccordoCooperazioneFactory.getUriFromAccordo(ac)+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				String oldnome = ac.getOldIDAccordoForUpdate()!=null ? this.idAccordoCooperazioneFactory.getUriFromIDAccordo(ac.getOldIDAccordoForUpdate()) : this.idAccordoCooperazioneFactory.getUriFromAccordo(ac);
				if(oldnome.equals(this.idAccordoCooperazioneFactory.getUriFromAccordo(ac))==false)
					msg+=":OLD<"+oldnome+">";
			}
		}

		// AccordoServizio
		else if (oggetto instanceof AccordoServizioParteComune) {
			AccordoServizioParteComune as = (AccordoServizioParteComune) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+this.idAccordoFactory.getUriFromAccordo(as)+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				String oldnome = as.getOldIDAccordoForUpdate()!=null ? this.idAccordoFactory.getUriFromIDAccordo(as.getOldIDAccordoForUpdate()) : this.idAccordoFactory.getUriFromAccordo(as);
				if(oldnome.equals(this.idAccordoFactory.getUriFromAccordo(as))==false)
					msg+=":OLD<"+oldnome+">";
			}
		}

		// Soggetto
		else if (oggetto instanceof org.openspcoop2.core.registry.Soggetto) {
			org.openspcoop2.core.registry.Soggetto soggetto = (org.openspcoop2.core.registry.Soggetto) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+soggetto.getTipo()+"/"+soggetto.getNome()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				String oldTipo = (soggetto.getOldIDSoggettoForUpdate()!=null && soggetto.getOldIDSoggettoForUpdate().getTipo()!=null) ? 
						soggetto.getOldIDSoggettoForUpdate().getTipo() : soggetto.getTipo(); 
				String oldNome = (soggetto.getOldIDSoggettoForUpdate()!=null && soggetto.getOldIDSoggettoForUpdate().getNome()!=null) ? 
						soggetto.getOldIDSoggettoForUpdate().getNome() : soggetto.getNome(); 
				if( (oldTipo.equals(soggetto.getTipo())==false) || (oldNome.equals(soggetto.getNome())==false) )
					msg+=":OLD<"+oldTipo+"/"+oldNome+">";
			}
		}

		// Servizio
		else if (oggetto instanceof AccordoServizioParteSpecifica) {
			AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+this.idServizioFactory.getUriFromAccordo(asps)+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				String oldnome = asps.getOldIDServizioForUpdate()!=null ? this.idServizioFactory.getUriFromIDServizio(asps.getOldIDServizioForUpdate()) : this.idServizioFactory.getUriFromAccordo(asps);
				if(oldnome.equals(this.idServizioFactory.getUriFromAccordo(asps))==false)
					msg+=":OLD<"+oldnome+">";
			}
		}

		// PortaDominio
		else if (oggetto instanceof PortaDominio) {
			PortaDominio pdd = (PortaDominio) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+pdd.getNome()+">";

		}

		// PortType
		else if (oggetto instanceof PortType) {
			PortType pt = (PortType) oggetto;

			long idAcc = pt.getIdAccordo();
			String nomeAS = "notdefined";
			try{
				AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore(this);
				IDAccordo idAccordo = apcCore.getIdAccordoServizio(idAcc);
				nomeAS = idAccordo.getNome();
			}catch (Exception e) {
				// ignore
			}

			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+pt.getNome()+"_"+nomeAS+">";
		}



		// config
		//Soggetto
		if (oggetto instanceof Soggetto) {
			Soggetto soggetto = (Soggetto) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+soggetto.getTipo()+"/"+soggetto.getNome()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				String oldTipo = (soggetto.getOldIDSoggettoForUpdate()!=null && soggetto.getOldIDSoggettoForUpdate().getTipo()!=null) ? 
						soggetto.getOldIDSoggettoForUpdate().getTipo() : soggetto.getTipo(); 
				String oldNome = (soggetto.getOldIDSoggettoForUpdate()!=null && soggetto.getOldIDSoggettoForUpdate().getNome()!=null) ? 
						soggetto.getOldIDSoggettoForUpdate().getNome() : soggetto.getNome(); 
				if( (oldTipo.equals(soggetto.getTipo())==false) || (oldNome.equals(soggetto.getNome())==false) )
					msg+=":OLD<"+oldTipo+"/"+oldNome+">";
			}
		}

		// ServizioApplicativo
		else if (oggetto instanceof ServizioApplicativo) {
			ServizioApplicativo sa = (ServizioApplicativo) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+sa.getTipoSoggettoProprietario()+"/"+sa.getNomeSoggettoProprietario()+"_"+sa.getNome()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				String oldTipoProp = (sa.getOldIDServizioApplicativoForUpdate()!=null && sa.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario()!=null && 
						sa.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getTipo()!=null) ? 
						sa.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getTipo() : sa.getTipoSoggettoProprietario(); 
				String oldNomeProp = (sa.getOldIDServizioApplicativoForUpdate()!=null && sa.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario()!=null && 
						sa.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getNome()!=null) ? 
						sa.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getNome() : sa.getNomeSoggettoProprietario(); 
				String oldNome = (sa.getOldIDServizioApplicativoForUpdate()!=null && sa.getOldIDServizioApplicativoForUpdate().getNome()!=null) ? 
						sa.getOldIDServizioApplicativoForUpdate().getNome() : sa.getNome(); 
				if(  (oldNome.equals(sa.getNome())==false) || (oldTipoProp.equals(sa.getTipoSoggettoProprietario())==false) || (oldNomeProp.equals(sa.getNomeSoggettoProprietario())==false) )
					msg+=":OLD<"+oldTipoProp+"/"+oldNomeProp+"_"+oldNome+">";
			}
		}

		// PortaDelegata
		else if (oggetto instanceof PortaDelegata) {
			PortaDelegata pd = (PortaDelegata) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario()+"_"+pd.getNome()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				String oldNome = (pd.getOldIDPortaDelegataForUpdate()!=null && pd.getOldIDPortaDelegataForUpdate().getNome()!=null) ? 
						pd.getOldIDPortaDelegataForUpdate().getNome() : pd.getNome(); 
				if(  (oldNome.equals(pd.getNome())==false) )
					msg+=":OLD<"+pd.getTipoSoggettoProprietario()+"/"+pd.getNomeSoggettoProprietario()+"_"+oldNome+">";
			}
		}

		// PortaApplicativa
		else if (oggetto instanceof PortaApplicativa) {
			PortaApplicativa pa = (PortaApplicativa) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+pa.getTipoSoggettoProprietario()+"/"+pa.getNomeSoggettoProprietario()+"_"+pa.getNome()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				String oldNome = (pa.getOldIDPortaApplicativaForUpdate()!=null && pa.getOldIDPortaApplicativaForUpdate().getNome()!=null) ? 
						pa.getOldIDPortaApplicativaForUpdate().getNome() : pa.getNome(); 
				if(  (oldNome.equals(pa.getNome())==false) )
					msg+=":OLD<"+pa.getTipoSoggettoProprietario()+"/"+pa.getNomeSoggettoProprietario()+"_"+oldNome+">";
			}
		}

		// RoutingTable
		else if (oggetto instanceof RoutingTable) {
			//RoutingTable rt = (RoutingTable) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
		}

		// Configurazione
		else if (oggetto instanceof Configurazione) {
			//Configurazione conf = (Configurazione) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
		}

		// AccessoRegistro
		else if (oggetto instanceof AccessoRegistro) {
			//AccessoRegistro cfgAccessoRegistro = (AccessoRegistro) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
		}

		// User
		else if (oggetto instanceof User) {
			User utente = (User) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+utente.getLogin()+">";
		}

		// auditing
		else if(oggetto instanceof org.openspcoop2.web.lib.audit.dao.Configurazione){
			msg+=":"+oggetto.getClass().getSimpleName();
		}
		else if(oggetto instanceof org.openspcoop2.web.lib.audit.dao.Filtro){
			org.openspcoop2.web.lib.audit.dao.Filtro filtro = (org.openspcoop2.web.lib.audit.dao.Filtro) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+filtro.toString()+">";
		}

		// Monitoraggio Applicativo
		else if(oggetto instanceof org.openspcoop2.pdd.monitor.driver.FilterSearch){
			org.openspcoop2.pdd.monitor.driver.FilterSearch filtro = (org.openspcoop2.pdd.monitor.driver.FilterSearch) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":EliminazioneMessaggiBasatiSuFiltro<"+filtro.toString()+">";
		}
		
		// Configurazione Controllo Traffico
		else if(oggetto instanceof ConfigurazioneGenerale) {
			msg+=":"+oggetto.getClass().getSimpleName();
		}
		// Configurazione Policy
		else if(oggetto instanceof ConfigurazionePolicy) {
			ConfigurazionePolicy policy = (ConfigurazionePolicy) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			msg+=":<"+policy.getIdPolicy()+">";
		}
		// Attivazione Policy
		else if(oggetto instanceof AttivazionePolicy) {
			AttivazionePolicy policy = (AttivazionePolicy) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("IDActivePolicy[").append(policy.getIdActivePolicy()).append("] IDPolicy[").append(policy.getIdPolicy()).append("]");
			msg+=":<"+bf.toString()+">";
		}
		// Generic Properties
		else if(oggetto instanceof GenericProperties) {
			GenericProperties genericProperties = (GenericProperties) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("Nome[").append(genericProperties.getNome()).append("] Tipologia[").append(genericProperties.getTipologia()).append("]");
			msg+=":<"+bf.toString()+">";
		}
		// Registro Plugin
		else if(oggetto instanceof RegistroPlugin) {
			RegistroPlugin registroPlugins = (RegistroPlugin) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("Nome[").append(registroPlugins.getNome()).append("]");
			msg+=":<"+bf.toString()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				String oldNome = registroPlugins.getOldNome();
				if( oldNome!=null && (oldNome.equals(registroPlugins.getNome())==false) )
					msg+=":OLD<"+oldNome+">";
			}
		}
		// Registro Plugin Archivi 
		else if(oggetto instanceof RegistroPluginArchivio) {
			RegistroPluginArchivio registroPluginArchivio = (RegistroPluginArchivio) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("Nome[").append(registroPluginArchivio.getNome()).append("]");
			bf.append(" Nome Plugin[").append(registroPluginArchivio.getNomePlugin()).append("]");
			msg+=":<"+bf.toString()+">";
		}
		// Plugin Classi
		else if(oggetto instanceof Plugin) {
			Plugin plugin = (Plugin) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("Tipo Plugin[").append(plugin.getTipoPlugin()).append("]");
			bf.append(" Tipo[").append(plugin.getTipo()).append("]");
			bf.append(" Label[").append(plugin.getLabel()).append("]");
			msg+=":<"+bf.toString()+">";
			if(Tipologia.CHANGE.equals(tipoOperazione)){
				if((plugin.getOldIdPlugin().getTipoPlugin().equals(plugin.getTipoPlugin())==false) ||
						(plugin.getOldIdPlugin().getTipo().equals(plugin.getTipo())==false) ||
						(plugin.getOldIdPlugin().getLabel().equals(plugin.getLabel())==false) ) {
					StringBuilder bf2 = new StringBuilder();
					bf2.append("Tipo Plugin[").append(plugin.getTipoPlugin()).append("]");
					bf2.append(" Tipo[").append(plugin.getTipo()).append("]");
					bf2.append(" Label[").append(plugin.getLabel()).append("]");
					msg+=":OLD<"+bf2.toString()+">";
				}
			}
		}
		// Allarme
		else if(oggetto instanceof Allarme) {
			Allarme allarme = (Allarme) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("Nome[").append(allarme.getNome()).append("]");
			msg+=":<"+bf.toString()+">";
		}
		// AllarmeHistory
		else if(oggetto instanceof AllarmeHistory) {
			AllarmeHistory allarme = (AllarmeHistory) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("Nome[").append(allarme.getIdAllarme().getNome()).append("]");
			msg+=":<"+bf.toString()+">";
		}
		// IExtendedBean
		else if(oggetto instanceof IExtendedBean){
			IExtendedBean w = (IExtendedBean) oggetto;
			msg+=":"+w.getClass().getSimpleName();
			msg+=":<"+w.getHumanId()+">";
		}
		// RemoteStoreKey
		else if(oggetto instanceof RemoteStoreKeyEntry) {
			RemoteStoreKeyEntry key = (RemoteStoreKeyEntry) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("Kid[").append(key.getKid()).append("]");
			msg+=":<"+bf.toString()+">";
		}
		
		return msg;
	}

	











	/* ************** UTILITA' GENERALI A TUTTI I CORE ED HELPER ***************** */

	private static final String VERSION_INFO_READ = "VERSION_INFO_READ";
	private static final String VERSION_INFO = "VERSION_INFO";
	
	private IVersionInfo versionInfo = null;
	private Boolean versionInfoRead = null;
	private synchronized IVersionInfo initInfoVersion(HttpServletRequest request, HttpSession session, String tipoDB) throws UtilsException {
		
		if(this.versionInfoRead==null) {
		
			try {
				Boolean versionInfoReadFromSession = ServletUtils.getObjectFromSession(request, session, Boolean.class, VERSION_INFO_READ);
				if(versionInfoReadFromSession!=null) {
					this.versionInfoRead = versionInfoReadFromSession;
					this.versionInfo = ServletUtils.getObjectFromSession(request, session, IVersionInfo.class, VERSION_INFO);
				}
				else {
					IVersionInfo vInfo = VersionUtilities.readInfoVersion();
					if(vInfo!=null) {
						Connection con = null;
						try {
							// prendo una connessione
							con = ControlStationCore.dbM.getConnection();
							vInfo.init(ControlStationLogger.getPddConsoleCoreLogger(), con, tipoDB);
							this.versionInfo = vInfo;
						} 
						catch(Exception e) {
							ControlStationLogger.getPddConsoleCoreLogger().error(e.getMessage(),e);
						}
						finally {
							ControlStationCore.dbM.releaseConnection(con);
						}
					}
					ServletUtils.setObjectIntoSession(request, session, true, VERSION_INFO_READ);
					if(vInfo!=null) {
						ServletUtils.setObjectIntoSession(request, session, vInfo, VERSION_INFO);
					}
				}
			}finally {
				this.versionInfoRead = true;
			}
			
		}
		
		return this.versionInfo;
		
	}
	public IVersionInfo getInfoVersion(HttpServletRequest request, HttpSession session) throws UtilsException {
		if(this.versionInfoRead==null) {
			initInfoVersion(request, session, this.tipoDB);
		}
		return this.versionInfo;
	}
	public void updateInfoVersion(HttpServletRequest request, HttpSession session, String info) throws UtilsException {
		Connection con = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			IVersionInfo vInfo = getInfoVersion(request, session);
			if(vInfo!=null) {
				vInfo.set(info, ControlStationLogger.getPddConsoleCoreLogger(), con, this.tipoDB);
			}
		} 
		catch(Exception e) {
			ControlStationLogger.getPddConsoleCoreLogger().error(e.getMessage(),e);
			throw e;
		}
		finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public IRegistryReader getRegistryReader(IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException{
		String nomeMetodo = "getRegistryReader";
		
		try {
			// istanzio il driver
			DriverRegistroServiziDB driver = new DriverRegistroServiziDB(ControlStationCore.dbM.getDataSourceName(),ControlStationCore.dbM.getDataSourceContext(),ControlStationCore.log, this.tipoDB);
			return protocolFactory.getRegistryReader(driver);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} 
	}
	
	public IConfigIntegrationReader getConfigIntegrationReader(IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException{
		String nomeMetodo = "getConfigIntegrationReader";
		
		try {
			// istanzio il driver
			DriverConfigurazioneDB driver = new DriverConfigurazioneDB(ControlStationCore.dbM.getDataSourceName(),ControlStationCore.dbM.getDataSourceContext(),ControlStationCore.log, this.tipoDB);
			return protocolFactory.getConfigIntegrationReader(driver);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	

	public void initMappingErogazione(boolean forceMapping,Logger log) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "initMappingErogazione";
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			driver.initMappingErogazione(forceMapping,log);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public void initMappingFruizione(boolean forceMapping,Logger log) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "initMappingFruizione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			driver.initMappingFruizione(forceMapping,log);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	

	/**
	 * Accesso alla configurazione generale della Pdd
	 * 
	 * @return configurazione generale della Pdd
	 * @throws DriverConfigurazioneNotFound
	 * @throws DriverConfigurazioneException
	 */
	public Configurazione getConfigurazioneGenerale() throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getConfigurazioneGenerale";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			Configurazione config = driver.getDriverConfigurazioneDB().getConfigurazioneGenerale();
			StatoFunzionalita statoMultitenant = (config.getMultitenant()==null || config.getMultitenant().getStato()==null) ? StatoFunzionalita.DISABILITATO :  config.getMultitenant().getStato();
			if(StatoFunzionalita.DISABILITATO.equals(statoMultitenant)) {
				// Fix #20
				SoggettiCore soggettiCore = new SoggettiCore(this);
				List<IDSoggetto> l = soggettiCore.getIdSoggettiOperativi();
				boolean existsMoreThanOneSoggettoOperativoPerProtocollo = false;
				Map<String, Integer> countSoggettoOperativiByProtocol = new HashMap<>();
				if(l!=null && !l.isEmpty()) {
					for (IDSoggetto soggetto : l) {
						String protocol = soggettiCore.getProtocolloAssociatoTipoSoggetto(soggetto.getTipo());
						int count = 0;
						if(countSoggettoOperativiByProtocol.containsKey(protocol)) {
							count = countSoggettoOperativiByProtocol.remove(protocol);
						}
						count ++;
						if(count>1) {
							existsMoreThanOneSoggettoOperativoPerProtocollo = true;
							break;
						}
						countSoggettoOperativiByProtocol.put(protocol, count);
					}
				}
				if(existsMoreThanOneSoggettoOperativoPerProtocollo) {
					// può succedere nel passaggio dalla 3.0.0 alla 3.0.1 quando più soggetti operativi erano già stati creati in una gestione multitenant completamente differente
					if(config.getMultitenant()==null) {
						config.setMultitenant(new ConfigurazioneMultitenant());
					}
					config.getMultitenant().setStato(StatoFunzionalita.ABILITATO);
				}
			}
			
			return config;

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  de),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	/**
	 * Accesso alla configurazione dei canali
	 * 
	 * @return configurazione dei canali
	 * @throws DriverConfigurazioneNotFound
	 * @throws DriverConfigurazioneException
	 */
	public CanaliConfigurazione getCanaliConfigurazione(boolean readNodi) throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getCanaliConfigurazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			CanaliConfigurazione config = driver.getDriverConfigurazioneDB().getCanaliConfigurazione(readNodi);
			return config;

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  de),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<String> getAllGruppiOrdinatiPerDataRegistrazione() throws DriverRegistroServiziException {
		FiltroRicercaGruppi filtroRicercaGruppi = new FiltroRicercaGruppi();
		filtroRicercaGruppi.setOrdinaDataRegistrazione(true);
		List<String> returnList = new ArrayList<>();
		List<IDGruppo> list = this.getAllIdGruppi(filtroRicercaGruppi);
		for (IDGruppo idGruppo : list) {
			returnList.add(idGruppo.getNome());
		}
		return returnList;
	}
	
	public List<String> getAllGruppi(FiltroRicercaGruppi filtroRicerca) throws DriverRegistroServiziException {
		List<String> returnList = new ArrayList<>();
		List<IDGruppo> list = this.getAllIdGruppi(filtroRicerca);
		for (IDGruppo idGruppo : list) {
			returnList.add(idGruppo.getNome());
		}
		return returnList;
	}
	public List<IDGruppo> getAllIdGruppi(FiltroRicercaGruppi filtroRicerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAllIdGruppi";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);
	
				return driver.getDriverRegistroServiziDB().getAllIdGruppi(filtroRicerca);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAllIdGruppi(filtroRicerca);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			return new ArrayList<>();
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<String> getAllRuoli(FiltroRicercaRuoli filtroRicerca) throws DriverRegistroServiziException {
		List<String> returnList = new ArrayList<>();
		List<IDRuolo> list = this.getAllIdRuoli(filtroRicerca);
		for (IDRuolo idRuolo : list) {
			returnList.add(idRuolo.getNome());
		}
		return returnList;
	}
	public List<IDRuolo> getAllIdRuoli(FiltroRicercaRuoli filtroRicerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAllIdRuoli";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);
	
				return driver.getDriverRegistroServiziDB().getAllIdRuoli(filtroRicerca);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAllIdRuoli(filtroRicerca);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			return new ArrayList<>();
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<String> getAllScope(FiltroRicercaScope filtroRicerca) throws DriverRegistroServiziException {
		List<String> returnList = new ArrayList<>();
		List<IDScope> list = this.getAllIdScope(filtroRicerca);
		for (IDScope idScope : list) {
			returnList.add(idScope.getNome());
		}
		return returnList;
	}
	public List<IDScope> getAllIdScope(FiltroRicercaScope filtroRicerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getAllIdScope";
		DriverControlStationDB driver = null;

		try {
			if(this.isRegistroServiziLocale()){
				// prendo una connessione
				con = ControlStationCore.dbM.getConnection();
				// istanzio il driver
				driver = new DriverControlStationDB(con, null, this.tipoDB);
	
				return driver.getDriverRegistroServiziDB().getAllIdScope(filtroRicerca);
			}
			else{
				return GestoreRegistroServiziRemoto.getDriverRegistroServizi(ControlStationCore.log).getAllIdScope(filtroRicerca);
			}

		} catch (DriverRegistroServiziNotFound de) {
			ControlStationCore.logDebug(getPrefixError(nomeMetodo,  de),de);
			return new ArrayList<>();
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public int countProtocolli(HttpServletRequest request, HttpSession session) throws  DriverRegistroServiziException {
		return this.countProtocolli(request, session, false);
	}
	public int countProtocolli(HttpServletRequest request, HttpSession session, boolean ignoreProtocolloSelezionato) throws  DriverRegistroServiziException {
		List<String> l = this.getProtocolli(request, session, ignoreProtocolloSelezionato);
		return l.size();
	}
	public List<String> getProtocolli(HttpServletRequest request, HttpSession session) throws  DriverRegistroServiziException {
		return this.getProtocolli(request, session, false);
	}
	public List<String> getProtocolli(HttpServletRequest request, HttpSession session, boolean ignoreProtocolloSelezionato) throws  DriverRegistroServiziException {
		return this.getProtocolli(request, session, ignoreProtocolloSelezionato, false);
	}
	public List<String> getProtocolli(HttpServletRequest request, HttpSession session, boolean ignoreProtocolloSelezionato, 
			boolean consideraProtocolliCompatibiliSoggettoSelezionato) throws  DriverRegistroServiziException {
		String getProtocolli = "getProtocolli";
		try{

			List<String> protocolliList = new ArrayList<>();
			
			User u =ServletUtils.getUserFromSession(request, session);
			
			if(!ignoreProtocolloSelezionato) {
				if(u.getProtocolloSelezionatoPddConsole()!=null) {
					protocolliList.add(u.getProtocolloSelezionatoPddConsole());
					return protocolliList;
				}
				else if(consideraProtocolliCompatibiliSoggettoSelezionato &&
						u.getSoggettoSelezionatoPddConsole()!=null && !"".equals(u.getSoggettoSelezionatoPddConsole())) {
					String tipoSoggetto = u.getSoggettoSelezionatoPddConsole().split("/")[0];
					SoggettiCore soggettiCore = new SoggettiCore(this);
					String protocollo = soggettiCore.getProtocolloAssociatoTipoSoggetto(tipoSoggetto); 
					protocolliList.add(protocollo);
					return protocolliList;
				}
			}
			
			if(u.getProtocolliSupportati()!=null && u.getProtocolliSupportati().size()>0) {
				return ProtocolUtils.orderProtocolli(u.getProtocolliSupportati());
			}
			
			return this.getProtocolli(); // ordinato dentro il metodo

		}catch (Exception e) {
			String errorMsg = getPrefixError(getProtocolli,  e);
			ControlStationCore.logError(errorMsg, e);
			throw new DriverRegistroServiziException(errorMsg,e);
		}
	}
	public List<String> getProtocolli(){
		
		List<String> protocolliList = new ArrayList<>();
		
		MapReader<String, IProtocolFactory<?>> protocolFactories = this.protocolFactoryManager.getProtocolFactories();
		Enumeration<String> protocolli = protocolFactories.keys();
		while (protocolli.hasMoreElements()) {

			String protocollo = protocolli.nextElement();
			protocolliList.add(protocollo);
		}
		
		return ProtocolUtils.orderProtocolli(protocolliList);
	}
	public List<String> getProtocolliByFilter(HttpServletRequest request, HttpSession session, boolean filtraSoggettiEsistenti, 
			boolean filtraAccordiEsistenti) throws  DriverRegistroServiziException {
		return this.getProtocolliByFilter(request, session, filtraSoggettiEsistenti, filtraAccordiEsistenti, false);
	}
	public List<String> getProtocolliByFilter(HttpServletRequest request, HttpSession session, boolean filtraSoggettiEsistenti, 
			boolean filtraAccordiEsistenti, boolean filtraAccordiCooperazioneEsistenti) throws  DriverRegistroServiziException {
		return getProtocolliByFilter(request, session, filtraSoggettiEsistenti, null, 
				filtraAccordiEsistenti, filtraAccordiCooperazioneEsistenti);
	}
	public List<String> getProtocolliByFilter(HttpServletRequest request, HttpSession session, boolean filtraSoggettiEsistenti, PddTipologia dominio, 
			boolean filtraAccordiEsistenti) throws  DriverRegistroServiziException {
		return getProtocolliByFilter(request, session, filtraSoggettiEsistenti, dominio, 
				filtraAccordiEsistenti, false);
	}
	public List<String> getProtocolliByFilter(HttpServletRequest request, HttpSession session, boolean filtraSoggettiEsistenti, PddTipologia dominio, 
			boolean filtraAccordiEsistenti, boolean filtraAccordiCooperazioneEsistenti) throws  DriverRegistroServiziException {
		return this.getProtocolliByFilter(request, session, filtraSoggettiEsistenti, dominio, 
				filtraAccordiEsistenti, filtraAccordiCooperazioneEsistenti, 
				false);
	}
	public List<String> getProtocolliByFilter(HttpServletRequest request, HttpSession session, boolean filtraSoggettiEsistenti, PddTipologia dominio, 
			boolean filtraAccordiEsistenti, boolean filtraAccordiCooperazioneEsistenti, 
			boolean consideraProtocolliCompatibiliSoggettoSelezionato) throws  DriverRegistroServiziException {
		
		List<String> _listaTipiProtocollo = this.getProtocolli(request, session, false, consideraProtocolliCompatibiliSoggettoSelezionato);
		
		String userLogin = ServletUtils.getUserLoginFromSession(session);
		
		if(filtraSoggettiEsistenti) {
			
			// Verifico esistenza soggetti per i protocolli caricati
			List<String> listaTipiProtocollo = new ArrayList<>();
			for (String check : _listaTipiProtocollo) {
				if(this.existsAlmostOneOrganization(dominio, userLogin, check)) {
					listaTipiProtocollo.add(check);	
				}
			}
			_listaTipiProtocollo = listaTipiProtocollo;
			
		}
		
		if(filtraAccordiEsistenti) {
			
			// Verifico esistenza accordi per i protocolli caricati
			List<String> listaTipiProtocollo = new ArrayList<>();
			for (String check : _listaTipiProtocollo) {
			
				if(this.existsAlmostOneAPI(userLogin, check)) {
					listaTipiProtocollo.add(check);	
				}
			}
			_listaTipiProtocollo = listaTipiProtocollo;
			
		}
		
		if(filtraAccordiCooperazioneEsistenti) {
			
			// Verifico esistenza accordi cooperazione per i protocolli caricati
			List<String> listaTipiProtocollo = new ArrayList<>();
			for (String check : _listaTipiProtocollo) {
			
				if(this.existsAlmostOneAccordoCooperazione(userLogin, check)) {
					listaTipiProtocollo.add(check);	
				}
			}
			_listaTipiProtocollo = listaTipiProtocollo;
			
		}
		
		return _listaTipiProtocollo;
	}
	public boolean existsAlmostOneOrganization(PddTipologia dominio, String userLogin, String protocollo) throws DriverRegistroServiziException{
		ConsoleSearch s = new ConsoleSearch();
		s.setPageSize(Liste.SOGGETTI, 1); // serve solo per il count
		s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, protocollo); // imposto protocollo
		if(dominio!=null) {
			s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, dominio.toString()); // imposto dominio
		}
		List<org.openspcoop2.core.registry.Soggetto> lista = null;
		if(this.isVisioneOggettiGlobale(userLogin)){
			lista = this.soggettiRegistroList(null, s);
		}else{
			lista = this.soggettiRegistroList(userLogin, s);
		}
		if(lista!=null && lista.size()>0) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean existsAlmostOneAPI(String userLogin, String protocollo) throws DriverRegistroServiziException{
		ConsoleSearch s = new ConsoleSearch();
		s.setPageSize(Liste.ACCORDI, 1); // serve solo per il count
		s.addFilter(Liste.ACCORDI, Filtri.FILTRO_PROTOCOLLO, protocollo); // imposto protocollo
		List<AccordoServizioParteComuneSintetico> lista = null;
		if(this.isVisioneOggettiGlobale(userLogin)){
			lista = this.accordiList(null, s);
		}else{
			lista = this.accordiList(userLogin, s);
		}
		if(lista!=null && lista.size()>0) {
			return true;
		}
		else {
			return false;
		}
	}
	public boolean existsAlmostOneAccordoCooperazione(String userLogin, String protocollo) throws DriverRegistroServiziException{
		ConsoleSearch s = new ConsoleSearch();
		s.setPageSize(Liste.ACCORDI_COOPERAZIONE, 1); // serve solo per il count
		s.addFilter(Liste.ACCORDI_COOPERAZIONE, Filtri.FILTRO_PROTOCOLLO, protocollo); // imposto protocollo
		List<AccordoCooperazione> lista = null;
		if(this.isVisioneOggettiGlobale(userLogin)){
			lista = this.accordiCooperazioneList(null, s);
		}else{
			lista = this.accordiCooperazioneList(userLogin, s);
		}
		if(lista!=null && lista.size()>0) {
			return true;
		}
		else {
			return false;
		}
	}

	public IDSoggetto getSoggettoOperativoDefault(String userLogin, String protocollo) throws DriverRegistroServiziException{
		ConsoleSearch s = new ConsoleSearch();
		s.setPageSize(Liste.SOGGETTI, 1); // serve solo per il count
		s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, protocollo); // imposto protocollo
		s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, PddTipologia.OPERATIVO.toString()); // imposto dominio
		s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_SOGGETTO_DEFAULT, "true"); // imposto indicazione di volere il soggetto operativo di default
		List<org.openspcoop2.core.registry.Soggetto> lista = null;
		if(this.isVisioneOggettiGlobale(userLogin)){
			lista = this.soggettiRegistroList(null, s);
		}else{
			lista = this.soggettiRegistroList(userLogin, s);
		}
		if(lista!=null && lista.size()>0) {
			return new IDSoggetto(lista.get(0).getTipo(), lista.get(0).getNome());
		}
		else {
			return null;
		}
	}
	
	public List<org.openspcoop2.core.registry.Soggetto> getSoggettiOperativi() throws DriverRegistroServiziException{
		return this.getSoggettiOperativi(null);
	}
	public List<org.openspcoop2.core.registry.Soggetto> getSoggettiOperativi(String protocollo) throws DriverRegistroServiziException{
		ConsoleSearch s = new ConsoleSearch(true);
		if(protocollo!=null) {
			s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, protocollo); // imposto protocollo
		}
		s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_DOMINIO, PddTipologia.OPERATIVO.toString()); // imposto dominio
		return this.soggettiRegistroList(null, s);
	}
	
	public List<IDSoggetto> getIdSoggettiOperativi() throws DriverRegistroServiziException{
		return this.getIdSoggettiOperativi(null);
	}
	public List<IDSoggetto> getIdSoggettiOperativi(String protocollo) throws DriverRegistroServiziException{
		List<org.openspcoop2.core.registry.Soggetto> list = this.getSoggettiOperativi(protocollo);
		List<IDSoggetto> l = new ArrayList<>();
		if(list!=null && !list.isEmpty()) {
			for (org.openspcoop2.core.registry.Soggetto soggetto : list) {
				l.add(new IDSoggetto(soggetto.getTipo(), soggetto.getNome()));
			}
		}
		return l;
	}
	
	public List<org.openspcoop2.core.registry.Soggetto> getSoggetti() throws DriverRegistroServiziException{
		return this.getSoggetti(null);
	}
	public List<org.openspcoop2.core.registry.Soggetto> getSoggetti(String protocollo) throws DriverRegistroServiziException{
		ConsoleSearch s = new ConsoleSearch(true);
		if(protocollo!=null) {
			s.addFilter(Liste.SOGGETTI, Filtri.FILTRO_PROTOCOLLO, protocollo); // imposto protocollo
		}
		return this.soggettiRegistroList(null, s);
	}
	
	public List<IDSoggetto> getIdSoggetti() throws DriverRegistroServiziException{
		return this.getIdSoggetti(null);
	}
	public List<IDSoggetto> getIdSoggetti(String protocollo) throws DriverRegistroServiziException{
		List<org.openspcoop2.core.registry.Soggetto> list = this.getSoggetti(protocollo);
		List<IDSoggetto> l = new ArrayList<>();
		if(list!=null && !list.isEmpty()) {
			for (org.openspcoop2.core.registry.Soggetto soggetto : list) {
				l.add(new IDSoggetto(soggetto.getTipo(), soggetto.getNome()));
			}
		}
		return l;
	}
	
	public IDSoggetto convertSoggettoSelezionatoToID(String soggettoOperativoSelezionato) {
		return new IDSoggetto(soggettoOperativoSelezionato.split("/")[0], soggettoOperativoSelezionato.split("/")[1]);
	}
	
	public List<org.openspcoop2.core.registry.Soggetto> soggettiRegistroList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "soggettiRegistroList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().soggettiRegistroList(superuser, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<IDSoggetto> getSoggettiDefault() throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "getSoggettiDefault";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().getSoggettiDefault();

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<AccordoServizioParteComuneSintetico> accordiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiList(superuser, ricerca);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<IDAccordoDB> idAccordiList(String superuser, ISearch ricerca, 
			boolean soloAccordiConsistentiRest, boolean soloAccordiConsistentiSoap) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "idAccordiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().idAccordiList(superuser, ricerca, 
					soloAccordiConsistentiRest, soloAccordiConsistentiSoap);

		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<AccordoCooperazione> accordiCooperazioneList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		Connection con = null;
		String nomeMetodo = "accordiCooperazioneList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverRegistroServiziDB().accordiCooperazioneList(superuser, ricerca);
		} catch (DriverRegistroServiziException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public IDServizio getLabelNomeServizioApplicativo(String servizioApplicativo) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getLabelNomeServizioApplicativo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getLabelNomeServizioApplicativo(servizioApplicativo);
		} catch (DriverConfigurazioneException e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e), e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public TipoAutenticazionePrincipal getTipoAutenticazionePrincipal(List<Proprieta> list) {
		if(list==null || list.isEmpty()) {
			return null;
		}
		for (Proprieta proprieta : list) {
			if(ParametriAutenticazionePrincipal.TIPO_AUTENTICAZIONE.equals(proprieta.getNome())) {
				return TipoAutenticazionePrincipal.toEnumConstant(proprieta.getValore());
			}
		}
		return null;
	}
	public List<String> getParametroAutenticazione(String autenticazione, List<Proprieta> list) {
		if(list==null || list.isEmpty()) {
			return null;
		}
		
		 List<String> parametroAutenticazioneList = null;
		
		if(TipoAutenticazione.BASIC.equals(autenticazione)) {
			// posizione 0: clean
			for (Proprieta proprieta : list) {
				if(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION.equals(proprieta.getNome())) {
					parametroAutenticazioneList = new ArrayList<>();
					if(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION_TRUE.equals(proprieta.getValore())) {
						parametroAutenticazioneList.add(Costanti.CHECK_BOX_DISABLED);
					}
					else {
						parametroAutenticazioneList.add(Costanti.CHECK_BOX_ENABLED);
					}
					break;
				}
			}
		}
		else if(TipoAutenticazione.PRINCIPAL.equals(autenticazione)) {
			TipoAutenticazionePrincipal tipo = getTipoAutenticazionePrincipal(list);
			if(tipo==null) {
				return null;
			}
			
			// posizione 0: nome o pattern o tipoClaim
			switch (tipo) {
			case CONTAINER:
			case INDIRIZZO_IP:
			case INDIRIZZO_IP_X_FORWARDED_FOR:
				return null;
			case HEADER:
			case FORM:
				// posizione 0: nome
				for (Proprieta proprieta : list) {
					if(ParametriAutenticazionePrincipal.NOME.equals(proprieta.getNome())) {
						parametroAutenticazioneList = new ArrayList<>();
						parametroAutenticazioneList.add(proprieta.getValore());
						break;
					}
				}
				
				// posizione 1: clean
				if(parametroAutenticazioneList==null) {
					break;
				}
				for (Proprieta proprieta : list) {
					if(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL.equals(proprieta.getNome())) {
						if(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_TRUE.equals(proprieta.getValore())) {
							parametroAutenticazioneList.add(Costanti.CHECK_BOX_DISABLED);
						}
						else {
							parametroAutenticazioneList.add(Costanti.CHECK_BOX_ENABLED);
						}
						break;
					}
				}
				
				break;
			case URL:
				// posizione 0: pattern
				for (Proprieta proprieta : list) {
					if(ParametriAutenticazionePrincipal.PATTERN.equals(proprieta.getNome())) {
						parametroAutenticazioneList = new ArrayList<>();
						parametroAutenticazioneList.add(proprieta.getValore());
						break;
					}
				}
				break;
			case TOKEN:
				// posizione 0: tipoToken
				for (Proprieta proprieta : list) {
					if(ParametriAutenticazionePrincipal.TOKEN_CLAIM.equals(proprieta.getNome())) {
						parametroAutenticazioneList = new ArrayList<>();
						parametroAutenticazioneList.add(proprieta.getValore());
						break;
					}
				}
				
				// posizione 1: nome claim proprietario
				if(parametroAutenticazioneList==null) {
					break;
				}
				for (Proprieta proprieta : list) {
					if(ParametriAutenticazionePrincipal.NOME.equals(proprieta.getNome())) {
						parametroAutenticazioneList.add(proprieta.getValore());
						break;
					}
				}
			}
		}
		else if(TipoAutenticazione.APIKEY.equals(autenticazione)) {
			
			parametroAutenticazioneList = new ArrayList<>();
			
			// posizione 0: appId
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.APP_ID, parametroAutenticazioneList);
			
			// posizione 1: queryParameter
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.QUERY_PARAMETER, parametroAutenticazioneList);
			
			// posizione 2: header
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.HEADER, parametroAutenticazioneList);
			
			// posizione 3: cookie
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.COOKIE, parametroAutenticazioneList);
			
			// posizione 4: useOAS3Names
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.USE_OAS3_NAMES, parametroAutenticazioneList);
			
			// posizione 5: cleanApiKey
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.CLEAN_API_KEY, parametroAutenticazioneList);
			
			// posizione 6: cleanAppId
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.CLEAN_APP_ID, parametroAutenticazioneList);
			
			// posizione 7: queryParameterApiKey
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.NOME_QUERY_PARAMETER_API_KEY, parametroAutenticazioneList);
						
			// posizione 8: headerApiKey
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.NOME_HEADER_API_KEY, parametroAutenticazioneList);
						
			// posizione 9: cookieApiKey
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.NOME_COOKIE_API_KEY, parametroAutenticazioneList);
						
			// posizione 10: queryParameterAppId
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.NOME_QUERY_PARAMETER_APP_ID, parametroAutenticazioneList);
						
			// posizione 11: headerAppId
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.NOME_HEADER_APP_ID, parametroAutenticazioneList);
			
			// posizione 12: cookieAppId
			_addValoreProprieta(list, ParametriAutenticazioneApiKey.NOME_COOKIE_APP_ID, parametroAutenticazioneList);
			
		}
			
		return parametroAutenticazioneList;
	}
	
	private void _addValoreProprieta(List<Proprieta> list, String nome, List<String> parametroAutenticazioneList) {
		for (Proprieta proprieta : list) {
			if(nome.equals(proprieta.getNome())) {
				parametroAutenticazioneList.add(proprieta.getValore());
				break;
			}
		}
	}

	public List<Proprieta> convertToAutenticazioneProprieta(String autenticazione, TipoAutenticazionePrincipal autenticazionePrincipal,  List<String> autenticazioneParametroList){
		List<Proprieta> list = new ArrayList<>();
		if(TipoAutenticazione.BASIC.equals(autenticazione)) {
			if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty()) {
				for (int i = 0; i < autenticazioneParametroList.size(); i++) {
					String autenticazioneParametro = autenticazioneParametroList.get(i);
					if(autenticazioneParametro!=null && !"".equals(autenticazioneParametro)) {
						Proprieta proprieta = new Proprieta();
						
						// posizione 0: clean
						if(i==0) {
							proprieta.setNome(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION);
							if(ServletUtils.isCheckBoxEnabled(autenticazioneParametro)) {
								proprieta.setValore(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION_FALSE);
							}
							else {
								proprieta.setValore(ParametriAutenticazioneBasic.CLEAN_HEADER_AUTHORIZATION_TRUE);
							}
						}
						list.add(proprieta);
					}
				}
			}
		}
		else if(TipoAutenticazione.PRINCIPAL.equals(autenticazione)) {
			
			if(autenticazionePrincipal==null) {
				autenticazionePrincipal = TipoAutenticazionePrincipal.CONTAINER;	
			}
			Proprieta proprieta = new Proprieta();
			proprieta.setNome(ParametriAutenticazionePrincipal.TIPO_AUTENTICAZIONE);
			proprieta.setValore(autenticazionePrincipal.getValue());
			list.add(proprieta);
			
			switch (autenticazionePrincipal) {
			case CONTAINER:
			case INDIRIZZO_IP:
			case INDIRIZZO_IP_X_FORWARDED_FOR:
				break;
			case HEADER:
			case FORM:
				if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty()) {
					for (int i = 0; i < autenticazioneParametroList.size(); i++) {
						String autenticazioneParametro = autenticazioneParametroList.get(i);
						if(autenticazioneParametro!=null && !"".equals(autenticazioneParametro)) {
							Proprieta proprietaPar = new Proprieta();
							
							// posizione 0: nome
							if(i==0) {
								proprietaPar.setNome(ParametriAutenticazionePrincipal.NOME);
								proprietaPar.setValore(autenticazioneParametro);
							}
							
							// posizione 1: clean
							else if(i==1) {
								proprietaPar.setNome(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL);
								if(ServletUtils.isCheckBoxEnabled(autenticazioneParametro)) {
									proprietaPar.setValore(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_FALSE);
								}
								else {
									proprietaPar.setValore(ParametriAutenticazionePrincipal.CLEAN_PRINCIPAL_TRUE);
								}
							}
							list.add(proprietaPar);
						}
					}
				}
				break;
			case URL:
				if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty()) {
					for (int i = 0; i < autenticazioneParametroList.size(); i++) {
						String autenticazioneParametro = autenticazioneParametroList.get(i);
						if(autenticazioneParametro!=null && !"".equals(autenticazioneParametro)) {
							Proprieta proprietaPar = new Proprieta();
							
							// posizione 0: pattern
							if(i==0) {
								proprietaPar.setNome(ParametriAutenticazionePrincipal.PATTERN);
								proprietaPar.setValore(autenticazioneParametro);
							}
							list.add(proprietaPar);
						}
					}
				}
				break;
			case TOKEN:
				if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty()) {
					for (int i = 0; i < autenticazioneParametroList.size(); i++) {
						String autenticazioneParametro = autenticazioneParametroList.get(i);
						if(autenticazioneParametro!=null && !"".equals(autenticazioneParametro)) {
							Proprieta proprietaPar = new Proprieta();
							
							// posizione 0: tipoToken
							if(i==0) {
								proprietaPar.setNome(ParametriAutenticazionePrincipal.TOKEN_CLAIM);
								proprietaPar.setValore(autenticazioneParametro);
							}
							
							// posizione 1: nome claim proprietario
							else if(i==1) {
								proprietaPar.setNome(ParametriAutenticazionePrincipal.NOME);
								proprietaPar.setValore(autenticazioneParametro);
							}
							list.add(proprietaPar);
						}
					}
				}
				break;
			}
		}
		else if(TipoAutenticazione.APIKEY.equals(autenticazione)) {
			if(autenticazioneParametroList!=null && !autenticazioneParametroList.isEmpty()) {
				for (int i = 0; i < autenticazioneParametroList.size(); i++) {
					String autenticazioneParametro = autenticazioneParametroList.get(i);
					if(autenticazioneParametro!=null && !"".equals(autenticazioneParametro)) {
						Proprieta proprietaPar = new Proprieta();
						proprietaPar.setValore(autenticazioneParametro);
						
						// posizione 0: appId
						if(i==0) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.APP_ID);
						}
						// posizione 1: queryParameter
						else if(i==1) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.QUERY_PARAMETER);
						}
						// posizione 2: header
						else if(i==2) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.HEADER);
						}
						// posizione 3: cookie
						else if(i==3) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.COOKIE);
						}
						// posizione 4: useOAS3Names
						else if(i==4) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.USE_OAS3_NAMES);
						}
						// posizione 5: cleanApiKey
						else if(i==5) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.CLEAN_API_KEY);
						}
						// posizione 6: cleanAppId
						else if(i==6) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.CLEAN_APP_ID);
						}
						// posizione 7: queryParameterApiKey
						else if(i==7) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.NOME_QUERY_PARAMETER_API_KEY);
						}
						// posizione 8: headerApiKey
						else if(i==8) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.NOME_HEADER_API_KEY);
						}
						// posizione 9: cookieApiKey
						else if(i==9) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.NOME_COOKIE_API_KEY);
						}
						// posizione 10: queryParameterAppId
						else if(i==10) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.NOME_QUERY_PARAMETER_APP_ID);
						}
						// posizione 11: headerAppId
						else if(i==11) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.NOME_HEADER_APP_ID);
						}
						// posizione 12: cookieAppId
						else if(i==12) {
							proprietaPar.setNome(ParametriAutenticazioneApiKey.NOME_COOKIE_APP_ID);
						}
						
						list.add(proprietaPar);
					}
				}
			}				
		}
		return list;
	}
	
	public List<String> getVersioniProtocollo(String protocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getVersioniProtocollo";
		try{

			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getVersioni();

		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public String[] getProfiliDiCollaborazioneSupportatiDalProtocollo(String protocollo,ServiceBinding serviceBinding) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getProfiliDiCollaborazioneSupportatiDalProtocollo";
		List<String> lstProt = new ArrayList<>();
		try{
			if(isProfiloDiCollaborazioneSupportatoDalProtocollo(protocollo, serviceBinding, ProfiloDiCollaborazione.ONEWAY))
				lstProt.add(CostantiRegistroServizi.ONEWAY.toString());

			if(isProfiloDiCollaborazioneSupportatoDalProtocollo(protocollo, serviceBinding, ProfiloDiCollaborazione.SINCRONO))
				lstProt.add(CostantiRegistroServizi.SINCRONO.toString());
			
			if(isProfiloDiCollaborazioneSupportatoDalProtocollo(protocollo, serviceBinding, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO))
				lstProt.add(CostantiRegistroServizi.ASINCRONO_SIMMETRICO.toString());
			
			if(isProfiloDiCollaborazioneSupportatoDalProtocollo(protocollo, serviceBinding, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO))
				lstProt.add(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.toString());
			
			return lstProt.toArray(new String[lstProt.size()]);
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public String[] getProfiliDiCollaborazioneSupportatiDalProtocolloDefault(ServiceBinding serviceBinding) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getProfiliDiCollaborazioneSupportatiDalProtocolloDefault";
		List<String> lstProt = new ArrayList<>();
		try{
			if(isProfiloDiCollaborazioneSupportatoDalProtocolloDefault(serviceBinding, ProfiloDiCollaborazione.ONEWAY))
				lstProt.add(CostantiRegistroServizi.ONEWAY.toString());

			if(isProfiloDiCollaborazioneSupportatoDalProtocolloDefault(serviceBinding, ProfiloDiCollaborazione.SINCRONO))
				lstProt.add(CostantiRegistroServizi.SINCRONO.toString());
			
			if(isProfiloDiCollaborazioneSupportatoDalProtocolloDefault(serviceBinding, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO))
				lstProt.add(CostantiRegistroServizi.ASINCRONO_SIMMETRICO.toString());
			
			if(isProfiloDiCollaborazioneSupportatoDalProtocolloDefault(serviceBinding, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO))
				lstProt.add(CostantiRegistroServizi.ASINCRONO_ASIMMETRICO.toString());
			
			return lstProt.toArray(new String[lstProt.size()]);
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public String getVersioneDefaultProtocollo(String protocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getVersioneDefaultProtocollo";
		try{

			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getVersioneDefault();

		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public boolean isProfiloDiCollaborazioneAsincronoSupportatoDalProtocolloDefault(ServiceBinding serviceBinding) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isProfiloDiCollaborazioneAsincronoSupportatoDalProtocolloDefault";
		try{
			return this.protocolFactoryManager.getDefaultProtocolFactory().createProtocolConfiguration().isSupportato(serviceBinding,ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
					|| this.protocolFactoryManager.getDefaultProtocolFactory().createProtocolConfiguration().isSupportato(serviceBinding,ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO)
					;
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public boolean isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(String protocollo,ServiceBinding serviceBinding) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo";
		try{
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportato(serviceBinding,ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
					|| this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportato(serviceBinding,ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	
	public boolean isProfiloDiCollaborazioneSupportatoDalProtocolloDefault(ServiceBinding serviceBinding,ProfiloDiCollaborazione profiloCollaborazione) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isProfiloDiCollaborazioneSupportatoDalProtocolloDefault";
		try{
			return this.protocolFactoryManager.getDefaultProtocolFactory().createProtocolConfiguration().isSupportato(serviceBinding, profiloCollaborazione );
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public boolean isProfiloDiCollaborazioneSupportatoDalProtocollo(String protocollo,ServiceBinding serviceBinding, ProfiloDiCollaborazione profiloCollaborazione) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isProfiloDiCollaborazioneSupportatoDalProtocollo";
		try{
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportato(serviceBinding,profiloCollaborazione );
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
		
	public boolean isFunzionalitaProtocolloSupportataDalProtocollo(String protocollo,ServiceBinding serviceBinding, FunzionalitaProtocollo funzionalitaProtocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isFunzionalitaProtocolloSupportataDalProtocollo";
		try{
			if(this.isProfiloModIPA(protocollo)) {
				if(FunzionalitaProtocollo.FILTRO_DUPLICATI.equals(funzionalitaProtocollo)) {
					return false;
				}
			}
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportato(serviceBinding,funzionalitaProtocollo);
		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverRegistroServiziException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public List<ServiceBinding> getServiceBindingList(IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException{
		String nomeMetodo = "getServiceBindingList";
		List<ServiceBinding> lst = new ArrayList<>();
		try {
			ServiceBindingConfiguration defaultServiceBindingConfiguration = protocolFactory.createProtocolConfiguration().getDefaultServiceBindingConfiguration(null);
			if(defaultServiceBindingConfiguration.isServiceBindingSupported(ServiceBinding.REST))
				lst.add(ServiceBinding.REST);
			if(defaultServiceBindingConfiguration.isServiceBindingSupported(ServiceBinding.SOAP))
				lst.add(ServiceBinding.SOAP);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		}
		return lst;
	}
	
	public List<ServiceBinding> getServiceBindingListProtocollo(String protocollo) throws DriverConfigurazioneException{
		String nomeMetodo = "getServiceBindingListProtocollo";
		List<ServiceBinding> lst = new ArrayList<>();
		try {
			IProtocolFactory<?> protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName(protocollo); 
			ServiceBindingConfiguration defaultServiceBindingConfiguration = protocolFactory.createProtocolConfiguration().getDefaultServiceBindingConfiguration(null);
			if(defaultServiceBindingConfiguration.isServiceBindingSupported(ServiceBinding.REST))
				lst.add(ServiceBinding.REST);
			if(defaultServiceBindingConfiguration.isServiceBindingSupported(ServiceBinding.SOAP))
				lst.add(ServiceBinding.SOAP);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		}
		return lst;
	}
	
	public List<MessageType> getMessageTypeList(IProtocolFactory<?> protocolFactory,ServiceBinding serviceBinding) throws DriverConfigurazioneException{
		String nomeMetodo = "getMessageTypeList";
		List<MessageType> messageTypeSupported = new ArrayList<>();
		try {
			ServiceBindingConfiguration defaultServiceBindingConfiguration = protocolFactory.createProtocolConfiguration().getDefaultServiceBindingConfiguration(null);
			messageTypeSupported = defaultServiceBindingConfiguration.getMessageTypeSupported(serviceBinding);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		}
		return messageTypeSupported;
	}
	
	
	public List<InterfaceType> getInterfaceTypeList(IProtocolFactory<?> protocolFactory,ServiceBinding serviceBinding) throws DriverConfigurazioneException{
		String nomeMetodo = "getInterfaceTypeList";
		List<InterfaceType> interfacceSupportate = new ArrayList<>();
		try {
			interfacceSupportate = protocolFactory.createProtocolConfiguration().getInterfacceSupportate(serviceBinding);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		}
		return interfacceSupportate;
	}
	
	public List<InterfaceType> getInterfaceTypeList(String protocollo, ServiceBinding serviceBinding) throws DriverConfigurazioneException{
		String nomeMetodo = "getInterfaceTypeList";
		List<InterfaceType> interfacceSupportate = new ArrayList<>();
		try {
			interfacceSupportate = this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getInterfacceSupportate(serviceBinding);
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		}
		return interfacceSupportate;
	}
	
	public ServiceBinding getDefaultServiceBinding(IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException{
		String nomeMetodo = "getDefaultServiceBinding";
		try {
			ServiceBindingConfiguration defaultServiceBindingConfiguration = protocolFactory.createProtocolConfiguration().getDefaultServiceBindingConfiguration(null);
			return defaultServiceBindingConfiguration.getDefaultBinding();
		} catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new DriverConfigurazioneException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public List<String> getTipiSoggettiProtocollo(String protocollo) throws ControlStationCoreException {
		String nomeMetodo = "getTipiSoggettiProtocollo";
		try{

			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getTipiSoggetti();

		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new ControlStationCoreException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public List<String> getTipiServiziProtocollo(String protocollo, ServiceBinding serviceBinding) throws ControlStationCoreException {
		String nomeMetodo = "getTipiServiziProtocollo";
		try{

			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getTipiServizi(serviceBinding);

		}catch (Exception e) {
			ControlStationCore.logError(getPrefixError(nomeMetodo,  e), e);
			throw new ControlStationCoreException(getPrefixError(nomeMetodo,  e),e);
		}
	}
	
	public List<String> getAzioni(AccordoServizioParteSpecifica asps,AccordoServizioParteComuneSintetico aspc, 
			boolean addTrattinoSelezioneNonEffettuata, boolean throwException, List<String> filtraAzioniUtilizzate) throws DriverConfigurazioneException{
		return AzioniUtils.getAzioni(asps, aspc, 
				addTrattinoSelezioneNonEffettuata, throwException, filtraAzioniUtilizzate, 
				CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA, ControlStationCore.log);
	}
	
	public Map<String,String> getMapAzioni(AccordoServizioParteSpecifica asps,AccordoServizioParteComuneSintetico aspc, 
			boolean addTrattinoSelezioneNonEffettuata, boolean throwException, List<String> filtraAzioniUtilizzate, 
			boolean sortByLabel, boolean sortFirstByPath // per soap questi due parametri sono  ininfluenti
			) throws DriverConfigurazioneException{
		return AzioniUtils.getMapAzioni(asps, aspc, 
				addTrattinoSelezioneNonEffettuata, throwException, filtraAzioniUtilizzate, 
				sortByLabel, sortFirstByPath, 
				CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA, CostantiControlStation.DEFAULT_VALUE_AZIONE_RISORSA_NON_SELEZIONATA, ControlStationCore.log);
	}
	
	public Map<String,String> getAzioniConLabel(AccordoServizioParteSpecifica asps,AccordoServizioParteComuneSintetico aspc, 
			boolean addTrattinoSelezioneNonEffettuata, boolean throwException, List<String> filtraAzioniUtilizzate) throws DriverConfigurazioneException {
		return getMapAzioni(asps, aspc, addTrattinoSelezioneNonEffettuata, throwException, filtraAzioniUtilizzate,
				true, true);
	}
	
	public ServiceBinding toMessageServiceBinding(org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding) {
		if(serviceBinding == null)
			return null;
		
		switch (serviceBinding) {
		case REST:
			return ServiceBinding.REST;
		case SOAP:
		default:
			return ServiceBinding.SOAP;
		}			
	}
	
	public String convertPrefixConfigDelGruppo(String prefix) {
		String key = " di ";
		if(prefix.endsWith(key)) {
			return prefix.substring(0, prefix.length()-key.length())+CostantiControlStation.LABEL_DEL_GRUPPO;
		}
		return prefix;
	}
	
	public String getLabelGroup(String groupName) {
		return "'"+groupName+"'";
	}
	
	public String convertPrefixConfigDelConnettore(String prefix) {
		String key = " di ";
		if(prefix.endsWith(key)) {
			return prefix.substring(0, prefix.length()-key.length())+CostantiControlStation.LABEL_DEL_CONNETTORE;
		}
		return prefix;
	}
	
	public org.openspcoop2.core.registry.constants.ServiceBinding fromMessageServiceBinding(ServiceBinding serviceBinding) {
		if(serviceBinding == null)
			return null;
		
		switch (serviceBinding) {
		case REST:
			return org.openspcoop2.core.registry.constants.ServiceBinding.REST;
		case SOAP:
		default:
			return org.openspcoop2.core.registry.constants.ServiceBinding.SOAP;
		}			
	}
	
	public MessageType toMessageMessageType(org.openspcoop2.core.registry.constants.MessageType messageType) {
		if(messageType == null)
			return null;
		
		switch (messageType) {
		case BINARY:
			return MessageType.BINARY;
		case JSON:
			return MessageType.JSON;
		case MIME_MULTIPART:
			return MessageType.MIME_MULTIPART;
		case SOAP_11:
			return MessageType.SOAP_11;
		case SOAP_12:
			return MessageType.SOAP_12;
		case XML:
		default:
			return MessageType.XML;
		
		}			
	}
	
	public org.openspcoop2.core.registry.constants.MessageType fromMessageMessageType(MessageType messageType) {
		if(messageType == null)
			return null;
		
		switch (messageType) {
		case BINARY:
			return org.openspcoop2.core.registry.constants.MessageType.BINARY;
		case JSON:
			return org.openspcoop2.core.registry.constants.MessageType.JSON;
		case MIME_MULTIPART:
			return org.openspcoop2.core.registry.constants.MessageType.MIME_MULTIPART;
		case SOAP_11:
			return org.openspcoop2.core.registry.constants.MessageType.SOAP_11;
		case SOAP_12:
			return org.openspcoop2.core.registry.constants.MessageType.SOAP_12;
		case XML:
		default:
			return org.openspcoop2.core.registry.constants.MessageType.XML;
		
		}			
	}
	
	public InterfaceType formatoSpecifica2InterfaceType(org.openspcoop2.core.registry.constants.FormatoSpecifica formatoSpecifica) {
		if(formatoSpecifica == null)
			return null;
		
		switch (formatoSpecifica) {
		case SWAGGER_2:
			return InterfaceType.SWAGGER_2;
		case OPEN_API_3:
			return InterfaceType.OPEN_API_3;
			
		case WSDL_11:
		default:
			return InterfaceType.WSDL_11;
		}			
	}
	
	public org.openspcoop2.core.registry.constants.FormatoSpecifica interfaceType2FormatoSpecifica(InterfaceType formatoSpecifica) {
		if(formatoSpecifica == null)
			return null;
		
		switch (formatoSpecifica) {
		case SWAGGER_2:
			return org.openspcoop2.core.registry.constants.FormatoSpecifica.SWAGGER_2;
		case OPEN_API_3:
			return org.openspcoop2.core.registry.constants.FormatoSpecifica.OPEN_API_3;
			
		case WSDL_11:
		default:
			return org.openspcoop2.core.registry.constants.FormatoSpecifica.WSDL_11;
		}			
	}
	
	public List<IExtendedMenu> getExtendedMenu(){
		return this.pluginMenu;
	}
	
	public List<IExtendedFormServlet> getExtendedServletConfigurazione(){
		return this.pluginConfigurazione;
	}
	
	public IExtendedListServlet getExtendedServletConfigurazioneList(ConsoleHelper consoleHelper){
		String tmp = consoleHelper.getRequest().getParameter(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID);
		if(tmp!=null && !"".equals(tmp)){
			return this.pluginConfigurazioneList.get(tmp);
		}
		return null;
	}
	
	public List<IExtendedConnettore> getExtendedConnettore(){
		return this.pluginConnettore;
	}
	
	public IExtendedListServlet getExtendedServletPortaDelegata(){
		return this.pluginPortaDelegata;
	}
	
	public IExtendedListServlet getExtendedServletPortaApplicativa(){
		return this.pluginPortaApplicativa;
	}
	
	/***
	 * utilizzo Lucida sans come font di dafault poiche' e' generalmente presente nella jdk
	 * 
	 * @return Font di defualt dell'applicazione
	 */
	public Font getDefaultFont() {
		if(this.defaultFont == null)
			this.defaultFont = new Font("Lucida Sans", Font.PLAIN , 14);

		return this.defaultFont;
	}
	public void setDefaultFont(Font defaultFont) {
		this.defaultFont = defaultFont;
	}
	
	// UTILITIES misurazione dimensione text
	public Integer getFontWidth(String text){
		return getFontWidth(text, this.getDefaultFont());
	} 
	
	public Integer getFontWidth(String text, int fontSize){
		Font defaultFont2 = this.getDefaultFont();
		return getFontWidth(text, defaultFont2.getFontName(), defaultFont2.getStyle(), fontSize);
	} 
	
	public Integer getFontWidth(String text, int fontStyle, int fontSize){
		Font defaultFont2 = this.getDefaultFont();
		return getFontWidth(text, defaultFont2.getFontName(), fontStyle, fontSize);
	} 

	public Integer getFontWidth(String text, String fontName, int fontStyle, int fontSize){
		Font fontToCheck = new Font(fontName,  fontStyle , fontSize);
		return getFontWidth(text, fontToCheck);
	} 


	public Integer getFontWidth(String text, Font fontToCheck){
		if(this.fontRenderContext == null){
			if(this.affineTransform == null)
				this.affineTransform = new AffineTransform();

			this.fontRenderContext = new FontRenderContext(this.affineTransform,true,true);
		}

		Rectangle2D rectangle2d = fontToCheck.getStringBounds(text, this.fontRenderContext);
		return (int) rectangle2d.getWidth(); 
	}
	
	public boolean isProfiloModIPA(String protocollo) {
		try {
			return protocollo!=null && protocollo.equals(CostantiLabel.MODIPA_PROTOCOL_NAME);
		}catch(Throwable t) {
			return false;
		}
	}
	
	public void setSearchAfterAdd(int idLista, String search, HttpServletRequest request, HttpSession session, ISearch ricerca) {
		ricerca.setSearchString(idLista, search);
		ServletUtils.removeRisultatiRicercaFromSession(request, session, idLista);		
	}
	
	private void _cryptPassword(ServizioApplicativo sa) throws UtilsException {
		if(this.isApplicativiPasswordEncryptEnabled() && this.applicativiPwManager!=null) {
			if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>0) {
				for (Credenziali credenziali : sa.getInvocazionePorta().getCredenzialiList()) {
					if(StringUtils.isNotEmpty(credenziali.getPassword()) && !credenziali.isCertificateStrictVerification()) {
						credenziali.setPassword(this.applicativiPwManager.crypt(credenziali.getPassword()));
						credenziali.setCertificateStrictVerification(true); // viene salvata a true per salvare l'informazione che è cifrata
					}
				}
			}
		}
	} 
	
	private void _cryptPassword(org.openspcoop2.core.registry.Soggetto soggetto) throws UtilsException {
		if(this.isSoggettiPasswordEncryptEnabled() && this.soggettiPwManager!=null) {
			if(soggetto.sizeCredenzialiList()>0) {
				for (CredenzialiSoggetto credenziali : soggetto.getCredenzialiList()) {
					if(StringUtils.isNotEmpty(credenziali.getPassword()) && !credenziali.isCertificateStrictVerification()) {
						credenziali.setPassword(this.applicativiPwManager.crypt(credenziali.getPassword()));
						credenziali.setCertificateStrictVerification(true); // viene salvata a true per salvare l'informazione che è cifrata
					}
				}
			}
		}
	} 

	private void cryptPassword(Tipologia[] tipoOperazione, Object ... oggetti) throws UtilsException {
		if(oggetti!=null && oggetti.length>0) {
			for (int i = 0; i < oggetti.length; i++) {
				Object oggetto = oggetti[i];
				
				Tipologia tipo = tipoOperazione[i];
				
				if(!Tipologia.ADD.equals(tipo) && !Tipologia.CHANGE.equals(tipo)) {
					continue;
				}
				
				if (oggetto instanceof SoggettoCtrlStat) {
					SoggettoCtrlStat soggetto = (SoggettoCtrlStat) oggetto;
					if(soggetto.getSoggettoReg()!=null) {
						this._cryptPassword(soggetto.getSoggettoReg());
					}
				}
				else if (oggetto instanceof org.openspcoop2.core.registry.Soggetto) {
					org.openspcoop2.core.registry.Soggetto sogReg = (org.openspcoop2.core.registry.Soggetto) oggetto;
					this._cryptPassword(sogReg);
				}
				else if (oggetto instanceof ServizioApplicativo) {
					ServizioApplicativo sa = (ServizioApplicativo) oggetto;
					this._cryptPassword(sa);
				}
				
			}
		}
	}
	
	public void setProprietaOggetto(String superUser, Tipologia[] operationTypes, Object ... oggetti) {
		if(oggetti!=null && oggetti.length>0) {
			for (int i = 0; i < oggetti.length; i++) {
				Object oggetto = oggetti[i];
				Tipologia operationType = operationTypes[i];
				
				boolean create = Tipologia.ADD.equals(operationType);
				boolean update = Tipologia.CHANGE.equals(operationType);
				
				if(!create && !update) {
					continue;
				}
				
				setProprietaOggettoSoggetto(superUser, oggetto, create, update);
				
				setProprietaOggettoAccordoServizioParteComune(superUser, oggetto, create, update);
				
				setProprietaOggettoAccordoServizioParteSpecifica(superUser, oggetto, create, update);
				
				setProprietaOggettoRuolo(superUser, oggetto, create, update);
				
				setProprietaOggettoScope(superUser, oggetto, create, update);
				
				setProprietaOggettoGruppo(superUser, oggetto, create, update);
				
				setProprietaOggettoPortaDelegata(superUser, oggetto, create, update);
				
				setProprietaOggettoPortaApplicativa(superUser, oggetto, create, update);
				
				setProprietaOggettoServizioApplicativo(superUser, oggetto, create, update);
				
				setProprietaOggettoGenericProperties(superUser, oggetto, create, update);
				
			}
		}
	}
	private void setProprietaOggettoSoggetto(String superUser, Object oggetto, boolean create, boolean update) {
		org.openspcoop2.core.registry.ProprietaOggetto pOggetto = null;
		if (oggetto instanceof SoggettoCtrlStat) {
			SoggettoCtrlStat soggetto = (SoggettoCtrlStat) oggetto;
			if(soggetto.getSoggettoReg()!=null) {
				if(create && soggetto.getSoggettoReg().getProprietaOggetto()==null) {
					soggetto.getSoggettoReg().setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());	
				}
				pOggetto = soggetto.getSoggettoReg().getProprietaOggetto();
			}
		}
		else if (oggetto instanceof org.openspcoop2.core.registry.Soggetto) {
			org.openspcoop2.core.registry.Soggetto sogReg = (org.openspcoop2.core.registry.Soggetto) oggetto;
			if(create && sogReg.getProprietaOggetto()==null) {
				sogReg.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());	
			}
			pOggetto = sogReg.getProprietaOggetto();
		}
		setProprietaOggetto(superUser, pOggetto, create, update);
	}
	private void setProprietaOggettoAccordoServizioParteComune(String superUser, Object oggetto, boolean create, boolean update) {
		org.openspcoop2.core.registry.ProprietaOggetto pOggetto = null;
		if (oggetto instanceof AccordoServizioParteComune) {
			AccordoServizioParteComune a = (AccordoServizioParteComune) oggetto;
			if(create && a.getProprietaOggetto()==null) {
				a.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());	
			}
			pOggetto = a.getProprietaOggetto();
		}
		setProprietaOggetto(superUser, pOggetto, create, update);
	}
	private void setProprietaOggettoAccordoServizioParteSpecifica(String superUser, Object oggetto, boolean create, boolean update) {
		org.openspcoop2.core.registry.ProprietaOggetto pOggetto = null;
		if (oggetto instanceof AccordoServizioParteSpecifica) {
			AccordoServizioParteSpecifica a = (AccordoServizioParteSpecifica) oggetto;
			
			// per gli aggiornamenti i casi da gestire sono:
			// modifica solo dei dati di un fruizione (si usa setDataAggiornamentoFruitore)
			// modifica solo dei dati di una erogazione (si usa setDataAggiornamentoServizio)
			// modifica effettuata in una fruizione o in una erogazione che comunque impatta su tutte le fruizioni/erogazioni esistenti
			
			boolean isFruitoreSingolo = setProprietaOggettoAccordoServizioParteSpecificaFruitore(superUser, a);
			
			if(!isFruitoreSingolo) {
				if(create && a.getProprietaOggetto()==null) {
					a.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());	
				}
				pOggetto = a.getProprietaOggetto();
				
				if(isDataAggiornamentoServizio(a) ) {
					setProprietaOggettoAccordoServizioParteSpecificaResetFruitori(a);
				}
			}
		}
		setProprietaOggetto(superUser, pOggetto, create, update);
	}
	private void setProprietaOggettoAccordoServizioParteSpecificaResetFruitori(AccordoServizioParteSpecifica a) {
		if(a.sizeFruitoreList()>0) {
			for (Fruitore fr : a.getFruitoreList()) {
				fr.setProprietaOggetto(null); // per non far aggiornare visto che la modifica non riguarda il fruitore
			}
		}
	}
	
	private static final Date DATA_CREAZIONE = new Date(0);
	public void setDataCreazioneFruitore(Fruitore fr) {
		if(fr!=null) {
			if(fr.getProprietaOggetto()==null) {
				fr.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());
			}
			fr.getProprietaOggetto().setDataCreazione(DATA_CREAZIONE);
		}
	}
	private boolean isDataCreazioneFruitore(Fruitore fr) {
		return fr!=null && fr.getProprietaOggetto()!=null &&
				fr.getProprietaOggetto().getDataCreazione()!=null && 
				DATA_CREAZIONE.equals(fr.getProprietaOggetto().getDataCreazione());
	}
	
	private static final Date DATA_AGGIORNAMENTO = new Date(0);
	private boolean isDataAggiornamentoServizio(AccordoServizioParteSpecifica asps) {
		return asps!=null && asps.getProprietaOggetto()!=null &&
				asps.getProprietaOggetto().getDataUltimaModifica()!=null && 
				DATA_AGGIORNAMENTO.equals(asps.getProprietaOggetto().getDataUltimaModifica());
	}
	public void setDataAggiornamentoServizio(AccordoServizioParteSpecifica asps) {
		if(asps!=null) {
			if(asps.getProprietaOggetto()==null) {
				asps.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());
			}
			asps.getProprietaOggetto().setDataUltimaModifica(DATA_AGGIORNAMENTO);
		}
	}
	public void setDataAggiornamentoFruitore(Fruitore fr) {
		if(fr!=null) {
			if(fr.getProprietaOggetto()==null) {
				fr.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());
			}
			fr.getProprietaOggetto().setDataUltimaModifica(DATA_AGGIORNAMENTO);
		}
	}
	private boolean isDataAggiornamentoFruitore(Fruitore fr) {
		return fr!=null && fr.getProprietaOggetto()!=null &&
				fr.getProprietaOggetto().getDataUltimaModifica()!=null && 
				DATA_AGGIORNAMENTO.equals(fr.getProprietaOggetto().getDataUltimaModifica());
	}
	
	private boolean setProprietaOggettoAccordoServizioParteSpecificaFruitore(String superUser, AccordoServizioParteSpecifica a) {
		boolean isFruitoreSingolo = false;
		if(a.sizeFruitoreList()>0) {
			isFruitoreSingolo = setProprietaOggettoAccordoServizioParteSpecificaSingoloFruitore(superUser, a);
		}
		if(!isFruitoreSingolo && a.sizeFruitoreList()>0) {
			// se comunque esistono dei fruitori, si tratta di un aggiornamento massivo
			for (Fruitore fr : a.getFruitoreList()) {
				if(fr.getProprietaOggetto()==null) {
					fr.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());
				}
				setProprietaOggetto(superUser, fr.getProprietaOggetto(), false, true);
			}
		}
		return isFruitoreSingolo;
	}
	private boolean setProprietaOggettoAccordoServizioParteSpecificaSingoloFruitore(String superUser, AccordoServizioParteSpecifica a) {
		boolean isFruitoreSingolo = isProprietaOggettoAccordoServizioParteSpecificaSingoloFruitore(a);
		for (Fruitore fr : a.getFruitoreList()) {
			if(fr!=null) {
				setProprietaOggettoAccordoServizioParteSpecificaSingoloFruitore(superUser, fr, isFruitoreSingolo);
			}
		}
		return isFruitoreSingolo;
	}
	private void setProprietaOggettoAccordoServizioParteSpecificaSingoloFruitore(String superUser, Fruitore fr, boolean isFruitoreSingolo) {
		if(isDataAggiornamentoFruitore(fr)) {
			if(fr.getProprietaOggetto()==null) {
				fr.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());
			}
			setProprietaOggetto(superUser, fr.getProprietaOggetto(), false, true);
		}
		else if(isDataCreazioneFruitore(fr)) {
			if(fr.getProprietaOggetto()==null) {
				fr.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());
			}
			setProprietaOggetto(superUser, fr.getProprietaOggetto(), true, false);
		}
		else if(isFruitoreSingolo && fr!=null){
			fr.setProprietaOggetto(null); // per non far aggiornare visto che la modifica non riguarda il fruitore
		}
	}
	private boolean isProprietaOggettoAccordoServizioParteSpecificaSingoloFruitore(AccordoServizioParteSpecifica a) {
		boolean isFruitoreSingolo = false;
		for (Fruitore fr : a.getFruitoreList()) {
			if(fr!=null &&
				(isDataAggiornamentoFruitore(fr) || isDataCreazioneFruitore(fr)) 
				){
				isFruitoreSingolo = true;
				break;
			}
		}
		return isFruitoreSingolo;
	}
	private void setProprietaOggettoRuolo(String superUser, Object oggetto, boolean create, boolean update) {
		org.openspcoop2.core.registry.ProprietaOggetto pOggetto = null;
		if (oggetto instanceof Ruolo) {
			Ruolo r = (Ruolo) oggetto;
			if(create && r.getProprietaOggetto()==null) {
				r.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());	
			}
			pOggetto = r.getProprietaOggetto();
		}
		setProprietaOggetto(superUser, pOggetto, create, update);
	}
	private void setProprietaOggettoScope(String superUser, Object oggetto, boolean create, boolean update) {
		org.openspcoop2.core.registry.ProprietaOggetto pOggetto = null;
		if (oggetto instanceof Scope) {
			Scope s = (Scope) oggetto;
			if(create && s.getProprietaOggetto()==null) {
				s.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());	
			}
			pOggetto = s.getProprietaOggetto();
		}
		setProprietaOggetto(superUser, pOggetto, create, update);
	}
	private void setProprietaOggettoGruppo(String superUser, Object oggetto, boolean create, boolean update) {
		org.openspcoop2.core.registry.ProprietaOggetto pOggetto = null;
		if (oggetto instanceof Gruppo) {
			Gruppo g = (Gruppo) oggetto;
			if(create && g.getProprietaOggetto()==null) {
				g.setProprietaOggetto(new org.openspcoop2.core.registry.ProprietaOggetto());	
			}
			pOggetto = g.getProprietaOggetto();
		}
		setProprietaOggetto(superUser, pOggetto, create, update);
	}
	private void setProprietaOggettoPortaDelegata(String superUser, Object oggetto, boolean create, boolean update) {
		org.openspcoop2.core.config.ProprietaOggetto pOggetto = null;
		if (oggetto instanceof PortaDelegata) {
			PortaDelegata p = (PortaDelegata) oggetto;
			if(create && p.getProprietaOggetto()==null) {
				p.setProprietaOggetto(new org.openspcoop2.core.config.ProprietaOggetto());	
			}
			pOggetto = p.getProprietaOggetto();
		}
		setProprietaOggetto(superUser, pOggetto, create, update);
	}
	private void setProprietaOggettoPortaApplicativa(String superUser, Object oggetto, boolean create, boolean update) {
		org.openspcoop2.core.config.ProprietaOggetto pOggetto = null;
		if (oggetto instanceof PortaApplicativa) {
			PortaApplicativa p = (PortaApplicativa) oggetto;
			if(create && p.getProprietaOggetto()==null) {
				p.setProprietaOggetto(new org.openspcoop2.core.config.ProprietaOggetto());	
			}
			pOggetto = p.getProprietaOggetto();
		}
		setProprietaOggetto(superUser, pOggetto, create, update);
	}
	private void setProprietaOggettoServizioApplicativo(String superUser, Object oggetto, boolean create, boolean update) {
		org.openspcoop2.core.config.ProprietaOggetto pOggetto = null;
		if (oggetto instanceof ServizioApplicativo) {
			ServizioApplicativo s = (ServizioApplicativo) oggetto;
			if(create && s.getProprietaOggetto()==null) {
				s.setProprietaOggetto(new org.openspcoop2.core.config.ProprietaOggetto());	
			}
			pOggetto = s.getProprietaOggetto();
		}
		setProprietaOggetto(superUser, pOggetto, create, update);
	}
	private void setProprietaOggettoGenericProperties(String superUser, Object oggetto, boolean create, boolean update) {
		org.openspcoop2.core.config.ProprietaOggetto pOggetto = null;
		if (oggetto instanceof GenericProperties) {
			GenericProperties gp = (GenericProperties) oggetto;
			if(
				//create && 
				gp.getProprietaOggetto()==null) {
				gp.setProprietaOggetto(new org.openspcoop2.core.config.ProprietaOggetto());	
			}
			pOggetto = gp.getProprietaOggetto();
		}
		setProprietaOggetto(superUser, pOggetto, create, update);
	}
	private void setProprietaOggetto(String superUser, Object oggetto, boolean create, boolean update) {
		if(oggetto instanceof org.openspcoop2.core.registry.ProprietaOggetto) {
			org.openspcoop2.core.registry.ProprietaOggetto p = (org.openspcoop2.core.registry.ProprietaOggetto) oggetto;
			if(create) {
				p.setDataCreazione(DateManager.getDate());
				p.setUtenteRichiedente(superUser);
			}
			else if(update) {
				p.setDataUltimaModifica(DateManager.getDate());
				p.setUtenteUltimaModifica(superUser);
			}
		}
		else if(oggetto instanceof org.openspcoop2.core.config.ProprietaOggetto) {
			org.openspcoop2.core.config.ProprietaOggetto p = (org.openspcoop2.core.config.ProprietaOggetto) oggetto;
			if(create) {
				p.setDataCreazione(DateManager.getDate());
				p.setUtenteRichiedente(superUser);
			}
			else if(update) {
				p.setDataUltimaModifica(DateManager.getDate());
				p.setUtenteUltimaModifica(superUser);
			}
		}
	}
	
	private void updateProprietaOggettoPorta(AttivazionePolicy policy, String superUser, DriverControlStationDB driver) throws DriverConfigurazioneException {		
		if(policy.getFiltro()!=null && policy.getFiltro().getEnabled() && policy.getFiltro().getRuoloPorta()!=null && 
				policy.getFiltro().getNomePorta()!=null && StringUtils.isNotEmpty(policy.getFiltro().getNomePorta())) {
			if(org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy.APPLICATIVA.equals(policy.getFiltro().getRuoloPorta())) {
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(policy.getFiltro().getNomePorta());
				driver.getDriverConfigurazioneDB().updateProprietaOggetto(idPA, superUser);
			}
			else if(org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy.DELEGATA.equals(policy.getFiltro().getRuoloPorta())) {
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(policy.getFiltro().getNomePorta());
				driver.getDriverConfigurazioneDB().updateProprietaOggetto(idPD, superUser);
			}
		}
	}
	
	private void updateProprietaOggettoPorta(Allarme allarme, String superUser, DriverControlStationDB driver) throws DriverConfigurazioneException {		
		if(allarme!=null && allarme.getFiltro()!=null && allarme.getFiltro().getEnabled() && allarme.getFiltro().getRuoloPorta()!=null && 
				allarme.getFiltro().getNomePorta()!=null && StringUtils.isNotEmpty(allarme.getFiltro().getNomePorta())) {
			if(org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy.APPLICATIVA.equals(allarme.getFiltro().getRuoloPorta())) {
				IDPortaApplicativa idPA = new IDPortaApplicativa();
				idPA.setNome(allarme.getFiltro().getNomePorta());
				driver.getDriverConfigurazioneDB().updateProprietaOggetto(idPA, superUser);
			}
			else if(org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy.DELEGATA.equals(allarme.getFiltro().getRuoloPorta())) {
				IDPortaDelegata idPD = new IDPortaDelegata();
				idPD.setNome(allarme.getFiltro().getNomePorta());
				driver.getDriverConfigurazioneDB().updateProprietaOggetto(idPD, superUser);
			}
		}
	}
	
	public void updatePluginClassLoader() throws Exception  {
		// Aggiorno classLoader interno
		PluginLoader pluginLoader = ((PluginLoader)CorePluginLoader.getInstance());
		if(pluginLoader.isPluginManagerEnabled()) {
			pluginLoader.updateFromConsoleConfig(log);
		}
	}
	
	public boolean isInterfaceDefined(String wsdlS) {
		if(wsdlS == null || wsdlS.trim().replaceAll("\n", "").equals("")) {
			return false;
		}
		return true;
	}
	public byte[] getInterfaceAsByteArray(FormatoSpecifica formatoSpecifica,String wsdlS) {
		if(!isInterfaceDefined(wsdlS)) {
			return null;
		}
		
		if(FormatoSpecifica.OPEN_API_3.equals(formatoSpecifica) || FormatoSpecifica.SWAGGER_2.equals(formatoSpecifica)){
			byte [] wsdl = wsdlS.getBytes();
			if(YAMLUtils.getInstance().isYaml(wsdl)) {
				// non deve essere effettuato il trim, essendo lo yaml posizionale:
				return wsdl;
			}
		}

		return wsdlS.trim().getBytes();
		
	}
	
	public AttributeAuthority buildAttributeAuthority(int sizeAA, String aaName, String attributeAuthorityAttributi) {
		
		Properties properties = null;
		if(attributeAuthorityAttributi!=null && StringUtils.isNotEmpty(attributeAuthorityAttributi)) {
			properties = PropertiesUtilities.convertTextToProperties(attributeAuthorityAttributi);
		}
		
		AttributeAuthority aa = new AttributeAuthority();
		aa.setNome(aaName);
		if(properties!=null && properties.size()>0) {
			String p = properties.getProperty(aaName);
			List<String> attributi = DBUtils.convertToList(p);
			if(attributi!=null && !attributi.isEmpty()) {
				aa.setAttributoList(attributi);
			}
		}
		
		if(aa.sizeAttributoList()<=0 && sizeAA==1) {
			// sono stati forniti direttamente i valori
			List<String> attributi = DBUtils.convertToList(attributeAuthorityAttributi);
			if(attributi!=null && !attributi.isEmpty()) {
				aa.setAttributoList(attributi);
			}
		}
		
		return aa;
	}
	
	public String[] buildAuthorityArrayString(List<AttributeAuthority> list){
		if(list==null || list.isEmpty()) {
			return null;
		}
		List<String> l = new ArrayList<>();
		for (AttributeAuthority attributeAuthority : list) {
			l.add(attributeAuthority.getNome());
		}
		return l.toArray(new String[1]);
	}
	
	public String buildAttributesStringFromAuthority(List<AttributeAuthority> list) {
		if(list==null || list.isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		if(list.size()==1) {
			AttributeAuthority attributeAuthority = list.get(0);
			if(attributeAuthority.sizeAttributoList()>0) {
				for (int i = 0; i < attributeAuthority.sizeAttributoList(); i++) {
					if(i>0) {
						sb.append(",");
					}
					sb.append(attributeAuthority.getAttributo(i));
				}
			}
		}
		else {
			for (AttributeAuthority attributeAuthority : list) {
				if(attributeAuthority.sizeAttributoList()>0) {
					if(sb.length()>0) {
						sb.append("\n");
					}
					sb.append(attributeAuthority.getNome());
					sb.append("=");
					for (int i = 0; i < attributeAuthority.sizeAttributoList(); i++) {
						if(i>0) {
							sb.append(",");
						}
						sb.append(attributeAuthority.getAttributo(i));
					}
				}
			}
		}
		if(sb.length()>0) {
			return sb.toString();
		}
		return null;
	}
	
	public void invokeJmxMethodAllNodesAndSetResult(PageData pd, String risorsa, String metodo, 
			String msgSuccesso, String msgFallimento,
			Object ... parametri) throws DriverControlStationException, DriverControlStationNotFound {
		boolean rilevatoErrore = false;
		StringBuilder sbMessagePerOperazioneEffettuata = new StringBuilder();
		String msgSuccessoAlternativo = null;
		int index = 0;
		List<String> aliases = this.getJmxPdDAliases();
		for (String alias : aliases) {
			
			StringBuilder bfExternal = new StringBuilder();
			String descrizione = this.getJmxPdDDescrizione(alias);
			
			if(aliases.size()>1) {
				if(index>0) {
					bfExternal.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
				}
				bfExternal.append(ConfigurazioneCostanti.LABEL_PARAMETRO_CONFIGURAZIONE_SISTEMA_NODO_CLUSTER).append(" ").append(descrizione).append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
			}						
			try{
				String stato = this.getInvoker().invokeJMXMethod(alias, this.getJmxPdDConfigurazioneSistemaType(alias),
						risorsa, 
						metodo, 
						parametri);
				if(JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO.equals(stato)){
					bfExternal.append(msgSuccesso);
				}
				else if(stato!=null && stato.startsWith(JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO_PREFIX)){
					StringBuilder bfStateSuccess = new StringBuilder();
					
					String suffix = stato.length()>JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO_PREFIX.length() ? stato.substring(JMXUtils.MSG_OPERAZIONE_EFFETTUATA_SUCCESSO_PREFIX.length()) : "";
					bfStateSuccess.append(msgSuccesso);
					if(suffix!=null && StringUtils.isNotEmpty(suffix)) {
						bfStateSuccess.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						bfStateSuccess.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						bfStateSuccess.append(suffix);
					}
					if(msgSuccessoAlternativo==null) {
						msgSuccessoAlternativo = bfStateSuccess.toString(); // dovrebbe essere uguale per tutti i nodi
					}
					
					bfExternal.append(bfStateSuccess.toString());
				}
				else{
					rilevatoErrore = true;
					bfExternal.append(msgFallimento);
					if(stato!=null && stato.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)) {
						bfExternal.append(stato.substring(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA.length()));
					}
					else {
						bfExternal.append(stato);
					}
				}
			}catch(Exception e){
				ControlStationCore.logError("Errore durante l'invocazione del metodo '"+metodo+"' (jmxResource '"+risorsa+"') (node:"+alias+"): "+e.getMessage(),e);
				rilevatoErrore = true;
				String stato = e.getMessage();
				bfExternal.append(msgFallimento);
				if(stato.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)) {
					bfExternal.append(stato.substring(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA.length()));
				}
				else {
					bfExternal.append(stato);
				}
			}

			if(sbMessagePerOperazioneEffettuata.length()>0){
				sbMessagePerOperazioneEffettuata.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
			}
			sbMessagePerOperazioneEffettuata.append(bfExternal.toString());
			
			index++;
		}
		if(sbMessagePerOperazioneEffettuata.length()>0){
			if(rilevatoErrore) {
				pd.setMessage(sbMessagePerOperazioneEffettuata.toString());
			}else { 
				/**pd.setMessage(sbMessagePerOperazioneEffettuata.toString(),Costanti.MESSAGE_TYPE_INFO);*/
				// non riporto elenco dei nodi ma solamente che l'operazione è andata a buon fine
				pd.setMessage(msgSuccessoAlternativo!=null ? msgSuccessoAlternativo : msgSuccesso,Costanti.MESSAGE_TYPE_INFO);
			}
		}
	}
	
	public void formatVerificaCertificatiEsito(PageData pd, List<String> formatIds, 
			String errore, String extraErrore, String posizioneErrore,
			String warning, String extraWarning, String posizioneWarning,
			boolean piuCertificatiAssociatiEntita) {
		if(errore!=null && errore.length()>0) {
			String error = errore.replaceAll("\n", org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE).trim();
			if(!error.startsWith(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE)) {
				error = org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE + error;
			}
			for (String formatId : formatIds) {
				error = error.replace(formatId, formatId+org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
			}
			pd.setMessage(CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_ERROR_PREFIX+posizioneErrore+
					org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
					(extraErrore!=null ? (org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+extraErrore+org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE) : "")+
					error,
					Costanti.MESSAGE_TYPE_ERROR);
		}
		else if(warning!=null && warning.length()>0) {
			String w = warning.replaceAll("\n", org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE).trim();
			if(!w.startsWith(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE)) {
				w = org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE + w;
			}
			for (String formatId : formatIds) {
				w = w.replace(formatId, formatId+org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
			}
			String messaggio = CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_WARNING_PREFIX;
			if(piuCertificatiAssociatiEntita) {
				messaggio = CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_WARNING_ANCHE_SCADUTI_PREFIX;
			}
			pd.setMessage(messaggio+posizioneWarning+
					org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+
					(extraWarning!=null ? (org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE+extraWarning+org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE) : "")+
					w,
					Costanti.MESSAGE_TYPE_WARN);
		}
		else {
			pd.setMessage(CostantiControlStation.LABEL_VERIFICA_CERTIFICATI_SUCCESSO,
					Costanti.MESSAGE_TYPE_INFO);
		}
	}
	
	public SortedMap<List<String>> toSortedListMap(List<Proprieta> proprieta) throws UtilsException{
		return ConfigUtils.toSortedListMap(proprieta);
	}
	public void addFromSortedListMap(List<Proprieta> proprieta, SortedMap<List<String>> map){
		ConfigUtils.addFromSortedListMap(proprieta, map);
	}
	
	public ConfigBean leggiConfigurazione(Config config, Map<String, Properties> propertiesMap) throws ValidationException, CoreException, ProviderException {
		ExternalResources externalResources = new ExternalResources();
		externalResources.setLog(ControlStationCore.getLog());
		externalResources.setTipoDB(this.tipoDB);
		Connection con = ControlStationCore.dbM.getConnection();
		ConfigBean configurazioneBean = null;
		try {
			externalResources.setConnection(con);
			configurazioneBean = ReadPropertiesUtilities.leggiConfigurazione(config, propertiesMap, externalResources);
		}finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
		return configurazioneBean;
	}
}
