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


package org.openspcoop2.web.ctrlstat.core;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneMultitenant;
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
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mapping.MappingErogazionePortaApplicativa;
import org.openspcoop2.core.mapping.MappingFruizionePortaDelegata;
import org.openspcoop2.core.mvc.properties.utils.PropertiesSourceConfiguration;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
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
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneApiKey;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazioneBasic;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazionePrincipal;
import org.openspcoop2.pdd.core.jmx.JMXUtils;
import org.openspcoop2.pdd.logger.DriverMsgDiagnostici;
import org.openspcoop2.pdd.logger.DriverTracciamento;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.AzioniUtils;
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
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.VersionUtilities;
import org.openspcoop2.utils.crypt.CryptConfig;
import org.openspcoop2.utils.crypt.CryptFactory;
import org.openspcoop2.utils.crypt.CryptType;
import org.openspcoop2.utils.crypt.ICrypt;
import org.openspcoop2.utils.crypt.PasswordVerifier;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;
import org.openspcoop2.utils.resources.MapReader;
import org.openspcoop2.utils.resources.ScriptInvoker;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.web.ctrlstat.config.ConsoleProperties;
import org.openspcoop2.web.ctrlstat.config.DatasourceProperties;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.costanti.MultitenantSoggettiErogazioni;
import org.openspcoop2.web.ctrlstat.costanti.MultitenantSoggettiFruizioni;
import org.openspcoop2.web.ctrlstat.costanti.OperationsParameter;
import org.openspcoop2.web.ctrlstat.costanti.TipoOggettoDaSmistare;
import org.openspcoop2.web.ctrlstat.dao.PdDControlStation;
import org.openspcoop2.web.ctrlstat.dao.SoggettoCtrlStat;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.IDBuilder;
import org.openspcoop2.web.ctrlstat.gestori.GestorePdDInitThread;
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
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.queue.config.QueueProperties;
import org.openspcoop2.web.lib.queue.costanti.Operazione;
import org.openspcoop2.web.lib.users.dao.User;
//import org.openspcoop2.web.ctrlstat.gestori.GestorePdDInitThread;
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
	public static void logError(String msg){
		ControlStationCore.checkInitLogger();
		ControlStationCore.log.error(msg);
	}
	public static void logError(String msg,Exception e){
		ControlStationCore.checkInitLogger();
		ControlStationCore.log.error(msg,e);
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
	private transient AffineTransform affineTransform = null;
	private transient FontRenderContext fontRenderContext = null;
	private transient Font defaultFont = null;
	
	private String getTitleSuffix(HttpSession session) {
		IVersionInfo versionInfo = null;
		try {
			versionInfo = getInfoVersion(session);
		}catch(Exception e) {
			ControlStationLogger.getPddConsoleCoreLogger().error("Errore durante la lettura delle informazioni sulla versione: "+e.getMessage(),e);
		}
		String consoleNomeEstesoSuffix = null;
		if(versionInfo!=null) {
			if(!StringUtils.isEmpty(versionInfo.getErrorTitleSuffix())) {
				consoleNomeEstesoSuffix = versionInfo.getErrorTitleSuffix();
			}
			else if(!StringUtils.isEmpty(versionInfo.getWarningTitleSuffix())) {
				consoleNomeEstesoSuffix = versionInfo.getWarningTitleSuffix();
			}
		}
		return consoleNomeEstesoSuffix;
	}
	
	public String getConsoleNomeSintesi() {
		return this.consoleNomeSintesi;
	}
	public String getConsoleNomeEsteso(HttpSession session) {
		String titleSuffix = getTitleSuffix(session);
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
		}catch(Exception e) {}
		
		String buildVersion = null;
		try {
			buildVersion = VersionUtilities.readBuildVersion();
		}catch(Exception e) {}
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

	/** Tipo del Database */
	protected String tipoDB = "";
	public String getTipoDatabase() {
		return this.tipoDB;
	}

	/** Accesso alle code JMS: Smistatore */
	private String smistatoreQueue = "";
	private String cfName = "";
	private Properties cfProp = null;

	/** IDFactory */
	private IDAccordoFactory idAccordoFactory = null;
	private IDAccordoCooperazioneFactory idAccordoCooperazioneFactory = null;
	private IDServizioFactory idServizioFactory = null;

	/** Protocollo */
	private String protocolloDefault = null;
	private long jdbcSerializableAttesaAttiva = 0;
	private int jdbcSerializableCheck = 0;
	public String getProtocolloDefault(HttpSession session, List<String> listaProtocolliUtilizzabili) throws DriverRegistroServiziException {
		if(listaProtocolliUtilizzabili!=null && listaProtocolliUtilizzabili.size()>0) {
			// cerco prima il default
			for (String protocolloUtilizzabile : listaProtocolliUtilizzabili) {
				if(protocolloUtilizzabile.equals(this.protocolloDefault)) {
					return this.protocolloDefault;
				}
			}
			return listaProtocolliUtilizzabili.get(0); // torno il primo
		}
		List<String> protocolli = this.getProtocolli(session);
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
	private Vector<String> utentiConVisioneGlobale = new Vector<String>();
	public boolean isVisioneOggettiGlobale(String user) {
		if(this.visioneOggettiGlobale){
			return true;
		}else{
			return this.utentiConVisioneGlobale.contains(user);
		}
	}

	/** Tracciamento */
	private boolean tracce_showConfigurazioneCustomAppender = false;
	private boolean tracce_sameDBWebUI = true;
	private boolean tracce_showSorgentiDatiDatabase = false;
	private String tracce_datasource = null;
	private String tracce_tipoDatabase = null;
	private Properties tracce_ctxDatasource = null;
	private DriverTracciamento driverTracciamento = null;
	public DriverTracciamento getDriverTracciamento()  throws Exception {
		return this.getDriverTracciamento(null, false);
	}
	public DriverTracciamento getDriverTracciamento(String nomeDs)  throws Exception {
		return this.getDriverTracciamento(nomeDs, false);
	}
	public DriverTracciamento getDriverTracciamento(String nomeDs, boolean forceChange)  throws Exception {
		if(this.driverTracciamento==null || forceChange){
			initDriverTracciamento(nomeDs, forceChange);
		}
		return this.driverTracciamento;
	}
	public boolean isTracce_showConfigurazioneCustomAppender() {
		return this.tracce_showConfigurazioneCustomAppender;
	}
	public boolean isTracce_showSorgentiDatiDatabase() {
		return this.tracce_showSorgentiDatiDatabase;
	}
	public boolean isTracce_sameDBWebUI() {
		return this.tracce_sameDBWebUI;
	}

	/** MsgDiagnostici */
	private boolean msgDiagnostici_showConfigurazioneCustomAppender = false;
	private boolean msgDiagnostici_sameDBWebUI = true;
	private boolean msgDiagnostici_showSorgentiDatiDatabase = false;
	private String msgDiagnostici_datasource = null;
	private String msgDiagnostici_tipoDatabase = null;
	private Properties msgDiagnostici_ctxDatasource = null;
	private DriverMsgDiagnostici driverMSGDiagnostici = null;
	public DriverMsgDiagnostici getDriverMSGDiagnostici()  throws Exception {
		return this.getDriverMSGDiagnostici(null, false);
	}
	public DriverMsgDiagnostici getDriverMSGDiagnostici(String nomeDs)  throws Exception {
		return this.getDriverMSGDiagnostici(nomeDs, false);
	}
	public DriverMsgDiagnostici getDriverMSGDiagnostici(String nomeDs, boolean forceChange)  throws Exception {
		if(this.driverMSGDiagnostici==null || forceChange){
			initDriverMSGDiagnostici(nomeDs, forceChange);
		}
		return this.driverMSGDiagnostici;
	}
	public boolean isMsgDiagnostici_showConfigurazioneCustomAppender() {
		return this.msgDiagnostici_showConfigurazioneCustomAppender;
	}
	public boolean isMsgDiagnostici_showSorgentiDatiDatabase() {
		return this.msgDiagnostici_showSorgentiDatiDatabase;
	}
	
	/** Dump */
	
	private boolean dump_showConfigurazioneCustomAppender = false;
	public boolean isDump_showConfigurazioneCustomAppender() {
		return this.dump_showConfigurazioneCustomAppender;
	}
	
	private boolean dump_showConfigurazioneDumpRealtime = true;
	public boolean isDump_showConfigurazioneDumpRealtime() {
		return this.dump_showConfigurazioneDumpRealtime;
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
	
	private static CryptConfig utenzePasswordEncryptEngine_apiMode = null;
	public static CryptConfig getUtenzePasswordEncryptEngine_apiMode() {
		return utenzePasswordEncryptEngine_apiMode;
	}
	public static void setUtenzePasswordEncryptEngine_apiMode(CryptConfig utenzePasswordEncryptEngine_apiMode) {
		ControlStationCore.utenzePasswordEncryptEngine_apiMode = utenzePasswordEncryptEngine_apiMode;
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
	private int utenzeLunghezzaPasswordGenerate;
	public int getUtenzeLunghezzaPasswordGenerate() {
		return this.utenzeLunghezzaPasswordGenerate;
	}
	
	protected ICrypt utenzePasswordManager;
	protected ICrypt utenzePasswordManager_backwardCompatibility;
	public ICrypt getUtenzePasswordManager() {
		return this.utenzePasswordManager;
	}
	public ICrypt getUtenzePasswordManager_backwardCompatibility() {
		return this.utenzePasswordManager_backwardCompatibility;
	}
	
	/** Applicativi */
	
	private static CryptConfig applicativiPasswordEncryptEngine_apiMode = null;
	public static CryptConfig getApplicativiPasswordEncryptEngine_apiMode() {
		return applicativiPasswordEncryptEngine_apiMode;
	}
	public static void setApplicativiPasswordEncryptEngine_apiMode(CryptConfig applicativiPasswordEncryptEngine_apiMode) {
		ControlStationCore.applicativiPasswordEncryptEngine_apiMode = applicativiPasswordEncryptEngine_apiMode;
	}
	private static PasswordVerifier applicativiPasswordVerifierEngine_apiMode = null;
	public static PasswordVerifier getApplicativiPasswordVerifierEngine_apiMode() {
		return applicativiPasswordVerifierEngine_apiMode;
	}
	public static void setApplicativiPasswordVerifierEngine_apiMode(
			PasswordVerifier applicativiPasswordVerifierEngine_apiMode) {
		ControlStationCore.applicativiPasswordVerifierEngine_apiMode = applicativiPasswordVerifierEngine_apiMode;
	}

	private String applicativiPasswordConfiguration = null;
	public String getApplicativiPasswordConfiguration() {
		return this.applicativiPasswordConfiguration;
	}
	private boolean applicativiBasicPasswordEnableConstraints = false;
	public boolean isApplicativiBasicPasswordEnableConstraints() {
		return this.applicativiBasicPasswordEnableConstraints;
	}
	private PasswordVerifier applicativiPasswordVerifierEngine = null;
	private CryptConfig applicativiPasswordEncryptEngine = null;
	private synchronized void initApplicativiPassword() throws UtilsException {
		if(this.applicativiBasicPasswordEnableConstraints && this.applicativiPasswordVerifierEngine==null){
			this.applicativiPasswordVerifierEngine = new PasswordVerifier(this.applicativiPasswordConfiguration); 
		}
		if(this.applicativiPasswordEncryptEngine==null){
			this.applicativiPasswordEncryptEngine = new CryptConfig(this.applicativiPasswordConfiguration); 
		}
	}
	public PasswordVerifier getApplicativiPasswordVerifier() {
		if(this.applicativiBasicPasswordEnableConstraints && this.applicativiPasswordConfiguration!=null){
			if(this.applicativiPasswordVerifierEngine==null){
				try{
					this.initApplicativiPassword();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
			if(this.applicativiPasswordVerifierEngine!=null && this.applicativiPasswordVerifierEngine.existsRestriction()){
				return this.applicativiPasswordVerifierEngine;
			}
		}
		return null;
	}
	public CryptConfig getApplicativiPasswordEncrypt() {
		if(this.applicativiPasswordConfiguration!=null){
			if(this.applicativiPasswordEncryptEngine==null){
				try{
					this.initApplicativiPassword();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
			return this.applicativiPasswordEncryptEngine;
		}
		return null;
	}
	public boolean isApplicativiPasswordEncryptEnabled() {
		return this.getApplicativiPasswordEncrypt()!=null && !CryptType.PLAIN.equals(this.getApplicativiPasswordEncrypt().getCryptType());
	}
	private int applicativiBasicLunghezzaPasswordGenerate;
	public int getApplicativiBasicLunghezzaPasswordGenerate() {
		return this.applicativiBasicLunghezzaPasswordGenerate;
	}
	private int applicativiApiKeyLunghezzaPasswordGenerate;
	protected int getApplicativiApiKeyLunghezzaPasswordGenerate() {
		return this.applicativiApiKeyLunghezzaPasswordGenerate;
	}
	
	protected ICrypt applicativiPasswordManager;
	public ICrypt getApplicativiPasswordManager() {
		return this.applicativiPasswordManager;
	}
	
	/** Soggetti */
	
	private static CryptConfig soggettiPasswordEncryptEngine_apiMode = null;
	public static CryptConfig getSoggettiPasswordEncryptEngine_apiMode() {
		return soggettiPasswordEncryptEngine_apiMode;
	}
	public static void setSoggettiPasswordEncryptEngine_apiMode(CryptConfig soggettiPasswordEncryptEngine_apiMode) {
		ControlStationCore.soggettiPasswordEncryptEngine_apiMode = soggettiPasswordEncryptEngine_apiMode;
	}
	private static PasswordVerifier soggettiPasswordVerifierEngine_apiMode = null;
	public static PasswordVerifier getSoggettiPasswordVerifierEngine_apiMode() {
		return soggettiPasswordVerifierEngine_apiMode;
	}
	public static void setSoggettiPasswordVerifierEngine_apiMode(
			PasswordVerifier soggettiPasswordVerifierEngine_apiMode) {
		ControlStationCore.soggettiPasswordVerifierEngine_apiMode = soggettiPasswordVerifierEngine_apiMode;
	}
	
	private String soggettiPasswordConfiguration = null;
	public String getSoggettiPasswordConfiguration() {
		return this.soggettiPasswordConfiguration;
	}
	private boolean soggettiBasicPasswordEnableConstraints = false;
	public boolean isSoggettiBasicPasswordEnableConstraints() {
		return this.soggettiBasicPasswordEnableConstraints;
	}
	private PasswordVerifier soggettiPasswordVerifierEngine = null;
	private CryptConfig soggettiPasswordEncryptEngine = null;
	private synchronized void initSoggettiPassword() throws UtilsException {
		if(this.soggettiBasicPasswordEnableConstraints && this.soggettiPasswordVerifierEngine==null){
			this.soggettiPasswordVerifierEngine = new PasswordVerifier(this.soggettiPasswordConfiguration); 
		}
		if(this.soggettiPasswordEncryptEngine==null){
			this.soggettiPasswordEncryptEngine = new CryptConfig(this.soggettiPasswordConfiguration); 
		}
	}
	public PasswordVerifier getSoggettiPasswordVerifier() {
		if(this.soggettiBasicPasswordEnableConstraints && this.soggettiPasswordConfiguration!=null){
			if(this.soggettiPasswordVerifierEngine==null){
				try{
					this.initSoggettiPassword();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
			if(this.soggettiPasswordVerifierEngine!=null && this.soggettiPasswordVerifierEngine.existsRestriction()){
				return this.soggettiPasswordVerifierEngine;
			}
		}
		return null;
	}
	public CryptConfig getSoggettiPasswordEncrypt() {
		if(this.soggettiPasswordConfiguration!=null){
			if(this.soggettiPasswordEncryptEngine==null){
				try{
					this.initSoggettiPassword();
				}catch(Exception e){
					log.error(e.getMessage(),e);
				}
			}
			return this.soggettiPasswordEncryptEngine;
		}
		return null;
	}
	public boolean isSoggettiPasswordEncryptEnabled() {
		return this.getSoggettiPasswordEncrypt()!=null && !CryptType.PLAIN.equals(this.getSoggettiPasswordEncrypt().getCryptType());
	}
	private int soggettiBasicLunghezzaPasswordGenerate;
	public int getSoggettiBasicLunghezzaPasswordGenerate() {
		return this.soggettiBasicLunghezzaPasswordGenerate;
	}
	private int soggettiApiKeyLunghezzaPasswordGenerate;
	protected int getSoggettiApiKeyLunghezzaPasswordGenerate() {
		return this.soggettiApiKeyLunghezzaPasswordGenerate;
	}
	
	protected ICrypt soggettiPasswordManager;
	public ICrypt getSoggettiPasswordManager() {
		return this.soggettiPasswordManager;
	}
	
	/** MessageSecurity PropertiesSourceConfiguration */
	private PropertiesSourceConfiguration messageSecurityPropertiesSourceConfiguration = null;
	public PropertiesSourceConfiguration getMessageSecurityPropertiesSourceConfiguration() {
		return this.messageSecurityPropertiesSourceConfiguration;
	}
	
	/** PolicyGestioneToken PropertiesSourceConfiguration */
	private PropertiesSourceConfiguration policyGestioneTokenPropertiesSourceConfiguration = null;
	public PropertiesSourceConfiguration getPolicyGestioneTokenPropertiesSourceConfiguration() {
		return this.policyGestioneTokenPropertiesSourceConfiguration;
	}
	
	/** ControlloTraffico */
	private boolean isControlloTrafficoPolicyGlobaleGroupByApi;
	private boolean isControlloTrafficoPolicyGlobaleFiltroApi;
	private boolean isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore;
	public boolean isControlloTrafficoPolicyGlobaleGroupByApi() {
		return this.isControlloTrafficoPolicyGlobaleGroupByApi;
	}
	public boolean isControlloTrafficoPolicyGlobaleFiltroApi() {
		return this.isControlloTrafficoPolicyGlobaleFiltroApi;
	}
	public boolean isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore() {
		return this.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore;
	}
	
	/** Auditing */
	private boolean isAuditingRegistrazioneElementiBinari;
	public boolean isAuditingRegistrazioneElementiBinari() {
		return this.isAuditingRegistrazioneElementiBinari;
	}
	
	/** IntegrationManager */
	private boolean isIntegrationManagerEnabled;
	public boolean isIntegrationManagerEnabled() {
		return this.isIntegrationManagerEnabled;
	}
	
	/** API */
	private boolean isApiResourcePathValidatorEnabled;
	private boolean isApiResourceHttpMethodAndPathQualsiasiEnabled;
	private List<String> getApiResourcePathQualsiasiSpecialChar;
	public boolean isApiResourcePathValidatorEnabled() {
		return this.isApiResourcePathValidatorEnabled;
	}
	public boolean isApiResourceHttpMethodAndPathQualsiasiEnabled() {
		return this.isApiResourceHttpMethodAndPathQualsiasiEnabled;
	}
	public List<String> getGetApiResourcePathQualsiasiSpecialChar() {
		return this.getApiResourcePathQualsiasiSpecialChar;
	}
	
	/** Accordi di Cooperazione */
	private boolean isAccordiCooperazioneEnabled;
	public boolean isAccordiCooperazioneEnabled() {
		return this.isAccordiCooperazioneEnabled;
	}
	
	/** Message Engine */
	private List<String> messageEngines;
	public List<String> getMessageEngines() {
		return this.messageEngines;
	}
	
	/** Credenziali Basic */
	private boolean isSoggettiCredenzialiBasicCheckUniqueUsePassword;
	private boolean isApplicativiCredenzialiBasicCheckUniqueUsePassword;
	public boolean isSoggettiCredenzialiBasicCheckUniqueUsePassword() {
		return this.isSoggettiCredenzialiBasicCheckUniqueUsePassword;
	}
	public boolean isApplicativiCredenzialiBasicCheckUniqueUsePassword() {
		return this.isApplicativiCredenzialiBasicCheckUniqueUsePassword;
	}
	
	/** Connettori Multipli */
	private boolean isConnettoriMultipliEnabled;
	public boolean isConnettoriMultipliEnabled() {
		return this.isConnettoriMultipliEnabled;
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

	/** Parametri pdd */
	private int portaPubblica = 80;
	private int portaGestione = 80;
	private String indirizzoPubblico;
	private String indirizzoGestione;
	public int getPortaPubblica() {
		return this.portaPubblica;
	}
	public int getPortaGestione() {
		return this.portaGestione;
	}
	public String getIndirizzoPubblico() {
		return this.indirizzoPubblico;
	}
	public String getIndirizzoGestione() {
		return this.indirizzoGestione;
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
	public static Boolean conservaRisultatiRicerca_staticInfo_read = null;
	public static boolean conservaRisultatiRicerca_staticInfo = false;
	private boolean showAccordiColonnaAzioni = false;
	private boolean showAccordiInformazioniProtocollo = false;
	private boolean showConfigurazioniPersonalizzate = false;
	private boolean showGestioneSoggettiRouter = false;
	private boolean showGestioneSoggettiVirtuali = false;
	private boolean showGestioneWorkflowStatoDocumenti = false;
	private boolean gestioneWorkflowStatoDocumenti_visualizzaStatoLista = false;
	private boolean gestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale = false;
	private boolean showInterfacceAPI = false;
	private boolean showAllegati = false;
	private boolean enableAutoMappingWsdlIntoAccordo = false;
	private boolean enableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes = false;
	private boolean showMTOMVisualizzazioneCompleta = false;
	private int portaCorrelazioneApplicativaMaxLength = 255;
	private boolean showPortaDelegataLocalForward = false;
	private boolean isProprietaErogazioni_showModalitaStandard;
	private boolean isProprietaFruizioni_showModalitaStandard;
	private boolean isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona = false;
	private boolean isVisualizzazioneConfigurazioneDiagnosticaLog4J = true;
	private String tokenPolicyForceId = null;
	private boolean tokenPolicyForceIdEnabled = false;
	private Properties tokenPolicyTipologia = null;
	private boolean showServiziVisualizzaModalitaElenco = false;
	private Integer selectListSoggettiOperativi_numeroMassimoSoggetti = null;
	private Integer selectListSoggettiOperativi_dimensioneMassimaLabel = null;
	
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
	public boolean isGestioneWorkflowStatoDocumenti_visualizzaStatoLista() {
		return this.gestioneWorkflowStatoDocumenti_visualizzaStatoLista;
	}
	public boolean isGestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale() {
		return this.gestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale;
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
	public boolean isEnableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes() {
		return this.enableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes;
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
	public boolean isProprietaErogazioni_showModalitaStandard() throws UtilsException{
		return this.isProprietaErogazioni_showModalitaStandard;
	}
	public boolean isProprietaFruizioni_showModalitaStandard() throws UtilsException{
		return this.isProprietaFruizioni_showModalitaStandard;
	}
	public boolean isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona() {
		return this.isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona;
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
	public boolean isShowServiziVisualizzaModalitaElenco() {
		return this.showServiziVisualizzaModalitaElenco;
	}
	public Integer getNumeroMassimoSoggettiSelectListSoggettiOperatiti() {
		return this.selectListSoggettiOperativi_numeroMassimoSoggetti;
	}
	public Integer getLunghezzaMassimaLabelSoggettiOperativiMenuUtente() {
		return this.selectListSoggettiOperativi_dimensioneMassimaLabel;
	}
	public boolean showCodaMessage() {
		return this.isShowJ2eeOptions() || this.isIntegrationManagerEnabled();
	}

	/** Motori di Sincronizzazione */
	private boolean sincronizzazionePddEngineEnabled;
	private String sincronizzazionePddEngineEnabled_prefissoNomeCodaConfigurazionePdd;
	private String sincronizzazionePddEngineEnabled_scriptShell_Path;
	private String sincronizzazionePddEngineEnabled_scriptShell_Args;
	private boolean sincronizzazioneRegistroEngineEnabled;
	private boolean sincronizzazioneGEEngineEnabled;
	private String sincronizzazioneGE_TipoSoggetto;
	private String sincronizzazioneGE_NomeSoggetto;
	private String sincronizzazioneGE_NomeServizioApplicativo;
	public boolean isSincronizzazionePddEngineEnabled() {
		return this.sincronizzazionePddEngineEnabled;
	}
	public String getSincronizzazionePddEngineEnabled_prefissoNomeCodaConfigurazionePdd() {
		return this.sincronizzazionePddEngineEnabled_prefissoNomeCodaConfigurazionePdd;
	}
	public String getSincronizzazionePddEngineEnabled_scriptShell_Path() {
		return this.sincronizzazionePddEngineEnabled_scriptShell_Path;
	}
	public String getSincronizzazionePddEngineEnabled_scriptShell_Args() {
		return this.sincronizzazionePddEngineEnabled_scriptShell_Args;
	}
	public boolean isSincronizzazioneRegistroEngineEnabled() {
		return this.sincronizzazioneRegistroEngineEnabled;
	}
	public boolean isSincronizzazioneGEEngineEnabled() {
		return this.sincronizzazioneGEEngineEnabled;
	}
	public String getSincronizzazioneGE_TipoSoggetto() {
		return this.sincronizzazioneGE_TipoSoggetto;
	}
	public String getSincronizzazioneGE_NomeSoggetto() {
		return this.sincronizzazioneGE_NomeSoggetto;
	}
	public String getSincronizzazioneGE_NomeServizioApplicativo() {
		return this.sincronizzazioneGE_NomeServizioApplicativo;
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
	private String suffissoConnettoreAutomatico;
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
	public String getSuffissoConnettoreAutomatico() {
		return this.suffissoConnettoreAutomatico;
	}
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
	private Hashtable<String, IExtendedListServlet> pluginConfigurazioneList = new Hashtable<String, IExtendedListServlet>();
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
	private List<String> jmxPdD_aliases = new ArrayList<String>();
	private Map<String, String> jmxPdD_descrizioni = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_tipoAccesso = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_remoteAccess_username = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_remoteAccess_password = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_remoteAccess_as = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_remoteAccess_factory = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_remoteAccess_url = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_dominio = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_type = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeRisorsa = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_versionePdD = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_versioneBaseDati = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_versioneJava = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_vendorJava = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_tipoDatabase = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniSSL = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteSSL = new Hashtable<String, String>();
	private boolean jmxPdD_configurazioneSistema_showInformazioniCryptographyKeyLength = false;
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniCryptographyKeyLength = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniCharset = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniInternazionalizzazione = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteInternazionalizzazione = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniTimeZone = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteTimeZone = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaNetworking = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteProprietaJavaNetworking = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaAltro = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaSistema = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_messageFactory = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_informazioniInstallazione = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_getFileTrace = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_updateFileTrace = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_connessioniDB = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_connessioniJMS = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_idTransazioniAttive = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_idProtocolloTransazioniAttive = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_connessioniPD = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_connessioniPA = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_tracciamento = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_dumpPD = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_dumpPA = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_log4j_diagnostica = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_log4j_openspcoop = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_log4j_integrationManager = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_log4j_tracciamento = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_log4j_dump = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorStatusCode = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorInstanceId = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeBadResponse = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalResponseError = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalRequestError = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalError = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorDetails = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorUseStatusCodeAsFaultCode = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorGenerateHttpHeaderGovWayCode = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreById = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_enablePortaDelegata = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_disablePortaDelegata = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_enablePortaApplicativa = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_disablePortaApplicativa = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegata = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataAbilitazioniPuntuali = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataDisabilitazioniPuntuali = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativa = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaAbilitazioniPuntuali = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaDisabilitazioniPuntuali = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_statoServizioIntegrationManager = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeAttributo_numeroDatasourceGW = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_getDatasourcesGW = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_getUsedConnectionsDatasourcesGW = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_getInformazioniDatabaseDatasourcesGW = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_getThreadPoolStatus  = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeRisorsaSystemPropertiesPdD = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_configurazioneSistema_nomeMetodo_refreshPersistentConfiguration = new Hashtable<String, String>();
	private Map<String, List<String>> jmxPdD_caches = new Hashtable<String, List<String>>();
	private Map<String, List<String>> jmxPdD_caches_prefill = new Hashtable<String, List<String>>();
	private Map<String, String> jmxPdD_cache_type = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_cache_nomeAttributo_cacheAbilitata = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_cache_nomeMetodo_statoCache = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_cache_nomeMetodo_resetCache = new Hashtable<String, String>();
	private Map<String, String> jmxPdD_cache_nomeMetodo_prefillCache = new Hashtable<String, String>();
	
	public List<String> getJmxPdD_aliases() {
		return this.jmxPdD_aliases;
	}
	public String getJmxPdD_descrizione(String alias) throws Exception {
		String descrizione = this.jmxPdD_descrizioni.get(alias);
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
	public boolean isJmxPdD_tipoAccessoOpenSPCoop(String alias) {
		return CostantiControlStation.RESOURCE_JMX_PDD_TIPOLOGIA_ACCESSO_OPENSPCOOP.equals(this.jmxPdD_tipoAccesso.get(alias));
	}
	public String getJmxPdD_remoteAccess_username(String alias) {
		return this.jmxPdD_remoteAccess_username.get(alias);
	}
	public String getJmxPdD_remoteAccess_password(String alias) {
		return this.jmxPdD_remoteAccess_password.get(alias);
	}
	public String getJmxPdD_remoteAccess_as(String alias) {
		return this.jmxPdD_remoteAccess_as.get(alias);
	}
	public String getJmxPdD_remoteAccess_factory(String alias) {
		return this.jmxPdD_remoteAccess_factory.get(alias);
	}
	public String getJmxPdD_remoteAccess_url(String alias) throws Exception {
		String url = this.jmxPdD_remoteAccess_url.get(alias);
		if(url!=null && this.singlePdD==false){
			// replace con url del nodo
			PddCore pddCore = new PddCore(this);
			PdDControlStation pdd = pddCore.getPdDControlStation(alias); // esiste per forza
			url = url.replace(CostantiControlStation.PLACEHOLDER_INFORMAZIONI_PDD_IP_GESTIONE, pdd.getIpGestione());
			url = url.replace(CostantiControlStation.PLACEHOLDER_INFORMAZIONI_PDD_PORTA_GESTIONE, pdd.getPortaGestione()+"");
			url = url.replace(CostantiControlStation.PLACEHOLDER_INFORMAZIONI_PDD_PROTOCOLLO_GESTIONE, pdd.getProtocolloGestione());
			url = url.replace(CostantiControlStation.PLACEHOLDER_INFORMAZIONI_PDD_IP_PUBBLICO, pdd.getIp());
			url = url.replace(CostantiControlStation.PLACEHOLDER_INFORMAZIONI_PDD_PORTA_PUBBLICA, pdd.getPorta()+"");
			url = url.replace(CostantiControlStation.PLACEHOLDER_INFORMAZIONI_PDD_PROTOCOLLO_PUBBLICO, pdd.getProtocollo());
		}
		return url;
	}
	public String getJmxPdD_dominio(String alias) throws Exception {
		String dominio = this.jmxPdD_dominio.get(alias);
		if(dominio==null || "".equals(dominio)){
			throw new Exception("Configurazione errata (pdd:"+alias+") accesso via jmx. Non e' stata indicato il dominio");
		}
		return dominio;
	}
	public String getJmxPdD_configurazioneSistema_type(String alias) {
		return this.jmxPdD_configurazioneSistema_type.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsa(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeRisorsa.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_versionePdD(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_versionePdD.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_versioneBaseDati(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_versioneBaseDati.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_versioneJava(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_versioneJava.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_vendorJava(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_vendorJava.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_tipoDatabase(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_tipoDatabase.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniSSL(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniSSL.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteSSL(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteSSL.get(alias);
	}
	public boolean isJmxPdD_configurazioneSistema_showInformazioniCryptographyKeyLength() {
		return this.jmxPdD_configurazioneSistema_showInformazioniCryptographyKeyLength;
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCryptographyKeyLength(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCryptographyKeyLength.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCharset(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCharset.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniInternazionalizzazione(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniInternazionalizzazione.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteInternazionalizzazione(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteInternazionalizzazione.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniTimeZone(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniTimeZone.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteTimeZone(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteTimeZone.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaNetworking(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaNetworking.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteProprietaJavaNetworking(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteProprietaJavaNetworking.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaAltro(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaAltro.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaSistema(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaSistema.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_messageFactory(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_messageFactory.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_informazioniInstallazione(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniInstallazione.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getFileTrace(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_getFileTrace.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_updateFileTrace(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_updateFileTrace.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniDB(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniDB.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniJMS(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniJMS.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_idTransazioniAttive(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_idTransazioniAttive.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_idProtocolloTransazioniAttive(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_idProtocolloTransazioniAttive.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniPD(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniPD.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_connessioniPA(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniPA.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_tracciamento(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_tracciamento.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_dumpPD(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_dumpPD.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_dumpPA(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_dumpPA.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4j_diagnostica(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_diagnostica.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4j_openspcoop(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_openspcoop.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4j_integrationManager(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_integrationManager.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4j_tracciamento(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_tracciamento.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_log4j_dump(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_dump.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorStatusCode(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorStatusCode.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorInstanceId(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorInstanceId.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeBadResponse(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeBadResponse.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalResponseError(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalResponseError.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalRequestError(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalRequestError.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalError(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalError.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorDetails(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorDetails.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorUseStatusCodeAsFaultCode(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorUseStatusCodeAsFaultCode.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorGenerateHttpHeaderGovWayCode(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorGenerateHttpHeaderGovWayCode.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreById(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreById.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_enablePortaDelegata(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_enablePortaDelegata.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disablePortaDelegata(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_disablePortaDelegata.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_enablePortaApplicativa(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_enablePortaApplicativa.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disablePortaApplicativa(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_disablePortaApplicativa.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegata(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegata.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataAbilitazioniPuntuali(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataAbilitazioniPuntuali.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataDisabilitazioniPuntuali(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataDisabilitazioniPuntuali.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativa(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativa.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaAbilitazioniPuntuali(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaAbilitazioniPuntuali.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaDisabilitazioniPuntuali(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaDisabilitazioniPuntuali.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioIntegrationManager(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioIntegrationManager.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeAttributo_numeroDatasourceGW(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeAttributo_numeroDatasourceGW.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getDatasourcesGW(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_getDatasourcesGW.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getUsedConnectionsDatasourcesGW(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_getUsedConnectionsDatasourcesGW.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getInformazioniDatabaseDatasourcesGW(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_getInformazioniDatabaseDatasourcesGW.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_getThreadPoolStatus(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_getThreadPoolStatus.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeRisorsaSystemPropertiesPdD(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeRisorsaSystemPropertiesPdD.get(alias);
	}
	public String getJmxPdD_configurazioneSistema_nomeMetodo_refreshPersistentConfiguration(String alias) {
		return this.jmxPdD_configurazioneSistema_nomeMetodo_refreshPersistentConfiguration.get(alias);
	}
	public List<String> getJmxPdD_caches(String alias) {
		return this.jmxPdD_caches.get(alias);
	}
	public List<String> getJmxPdD_caches_prefill(String alias) {
		return this.jmxPdD_caches_prefill.get(alias);
	}
	public String getJmxPdD_cache_type(String alias) {
		return this.jmxPdD_cache_type.get(alias);
	}
	public String getJmxPdD_cache_nomeAttributo_cacheAbilitata(String alias) {
		return this.jmxPdD_cache_nomeAttributo_cacheAbilitata.get(alias);
	}
	public String getJmxPdD_cache_nomeMetodo_statoCache(String alias) {
		return this.jmxPdD_cache_nomeMetodo_statoCache.get(alias);
	}
	public String getJmxPdD_cache_nomeMetodo_resetCache(String alias) {
		return this.jmxPdD_cache_nomeMetodo_resetCache.get(alias);
	}
	public String getJmxPdD_cache_nomeMetodo_prefillCache(String alias) {
		return this.jmxPdD_cache_nomeMetodo_prefillCache.get(alias);
	}
	
	public Object getGestoreRisorseJMX(String alias)  throws Exception{
		try {
			if(this.isJmxPdD_tipoAccessoOpenSPCoop(alias)){
				//System.out.println("=================== REMOTA OPENSPCOOP =======================");
				String remoteUrl = this.getJmxPdD_remoteAccess_url(alias);
				if(remoteUrl==null){
					throw new Exception("Configurazione errata (pdd:"+alias+") accesso via check. Non e' stata indicata la url");
				}
				return remoteUrl;
			}
			else{
				org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX gestoreJMX = null;
				
				if(this.getJmxPdD_remoteAccess_url(alias)!=null && !"".equals(this.getJmxPdD_remoteAccess_url(alias)) 
						&& !"locale".equals(this.getJmxPdD_remoteAccess_url(alias)) ){
					//System.out.println("=================== REMOTA =======================");
					String remoteUrl = this.getJmxPdD_remoteAccess_url(alias);
					String factory = this.getJmxPdD_remoteAccess_factory(alias);
					if(factory==null){
						throw new Exception("Configurazione errata (pdd:"+alias+") per l'accesso alla url ["+remoteUrl+"] via jmx. Non e' stata indicata una factory");
					}
					String as = this.getJmxPdD_remoteAccess_as(alias);
					if(as==null){
						throw new Exception("Configurazione errata (pdd:"+alias+") per l'accesso alla url ["+remoteUrl+"] via jmx. Non e' stato indicato il tipo di application server");
					}
					gestoreJMX = new org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX(as, factory, remoteUrl, 
							this.getJmxPdD_remoteAccess_username(alias), 
							this.getJmxPdD_remoteAccess_password(alias), getLog());
				}
				else{
					//System.out.println("=================== LOCALE =======================");
					gestoreJMX = new org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX(getLog());
					
				}
				
				return gestoreJMX;
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String invokeJMXMethod(Object gestore, String alias, String type, String nomeRisorsa, String nomeMetodo) throws Exception{
		return invokeJMXMethod(gestore, alias, type, nomeRisorsa, nomeMetodo, null);
	}
	public String invokeJMXMethod(Object gestore, String alias, String type, String nomeRisorsa, String nomeMetodo, String parametro) throws Exception{
		try {
			if(gestore instanceof org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX){
				
				Object [] params = null;
				String [] signatures = null;
				if(parametro!=null && !"".equals(parametro)){
					params = new Object[] {parametro};
					signatures = new String[] {String.class.getName()};
				}
				
				String tmp = (String) ((org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX)gestore).invoke(this.getJmxPdD_dominio(alias), 
						type, nomeRisorsa, nomeMetodo, params, signatures);
				if(tmp.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)){
					throw new Exception(tmp); 
				}
				return tmp;
			}
			else if(gestore instanceof String){
				String url = (String) gestore;
				String username = this.getJmxPdD_remoteAccess_username(alias);
				String password = this.getJmxPdD_remoteAccess_password(alias);
				
				Map<String, String> p = new HashMap<String, String>();
				p.put(CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, nomeRisorsa);
				p.put(CostantiPdD.CHECK_STATO_PDD_METHOD_NAME, nomeMetodo);
				if(parametro!=null && !"".equals(parametro)){
					p.put(CostantiPdD.CHECK_STATO_PDD_PARAM_VALUE, parametro);
				}
				String urlWithParameters = TransportUtils.buildLocationWithURLBasedParameter(p, url);

				HttpResponse response = HttpUtilities.getHTTPResponse(urlWithParameters, username, password);
				if(response.getResultHTTPOperation()!=200){
					String error = "[httpCode "+response.getResultHTTPOperation()+"]";
					if(response.getContent()!=null){
						error+= " "+new String(response.getContent());
					}
					return error;
				}
				else{
					return new String(response.getContent());
				}
			}
			else {
				throw new Exception("Gestore di tipo ["+gestore.getClass().getName()+"] non gestito");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public String readJMXAttribute(Object gestore, String alias, String type, String nomeRisorsa, String nomeAttributo) throws Exception{
		try {
			if(gestore instanceof org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX){
				Object t = ((org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX)gestore).getAttribute(this.getJmxPdD_dominio(alias), type, nomeRisorsa, nomeAttributo);
				if(t instanceof String){
					String tmp = (String) t; 
					if(tmp.startsWith(JMXUtils.MSG_OPERAZIONE_NON_EFFETTUATA)){
						throw new Exception(tmp); 
					}
					return tmp;
				}
				else if(t instanceof Boolean){
					return ((Boolean)t).toString();
				}
				else{
					return t.toString();
				}
			}
			else if(gestore instanceof String){
				String url = (String) gestore;
				String username = this.getJmxPdD_remoteAccess_username(alias);
				String password = this.getJmxPdD_remoteAccess_password(alias);
				
				Map<String, String> p = new HashMap<String, String>();
				p.put(CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, nomeRisorsa);
				p.put(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_NAME, nomeAttributo);
				String urlWithParameters = TransportUtils.buildLocationWithURLBasedParameter(p, url);
				
				HttpResponse response = HttpUtilities.getHTTPResponse(urlWithParameters, username, password);
				if(response.getResultHTTPOperation()!=200){
					String error = "[httpCode "+response.getResultHTTPOperation()+"]";
					if(response.getContent()!=null){
						error+= " "+new String(response.getContent());
					}
					return error;
				}
				else{
					return new String(response.getContent());
				}
			}
			else {
				throw new Exception("Gestore di tipo ["+gestore.getClass().getName()+"] non gestito");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public void setJMXAttribute(Object gestore, String alias, String type, String nomeRisorsa, String nomeAttributo, Object value) throws Exception{
		try {
			if(gestore instanceof org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX){
				((org.openspcoop2.pdd.core.jmx.GestoreRisorseJMX)gestore).setAttribute(this.getJmxPdD_dominio(alias), type, nomeRisorsa, nomeAttributo, value);
			}
			else if(gestore instanceof String){
				String url = (String) gestore;
				String username = this.getJmxPdD_remoteAccess_username(alias);
				String password = this.getJmxPdD_remoteAccess_password(alias);
				
				Map<String, String> p = new HashMap<String, String>();
				p.put(CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, nomeRisorsa);
				p.put(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_NAME, nomeAttributo);
				if(value instanceof Boolean){
					p.put(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_BOOLEAN_VALUE, value.toString());
				}
				else{
					p.put(CostantiPdD.CHECK_STATO_PDD_ATTRIBUTE_VALUE, value.toString());
				}
				String urlWithParameters = TransportUtils.buildLocationWithURLBasedParameter(p, url);
				
				HttpResponse response = HttpUtilities.getHTTPResponse(urlWithParameters, username, password);
				if(response.getResultHTTPOperation()!=200){
					String error = "[httpCode "+response.getResultHTTPOperation()+"]";
					if(response.getContent()!=null){
						error+= " "+new String(response.getContent());
					}
					throw new Exception(error);
				}
			}
			else {
				throw new Exception("Gestore di tipo ["+gestore.getClass().getName()+"] non gestito");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	

	/* --- COSTRUTTORI --- */

	public static Boolean API = null;
	public static synchronized void initAPIMode() {
		if(API==null) {
			API = true;
		}
	}
	public static boolean isAPIMode() {
		return API!=null ? API : false;
	}
		
	protected boolean usedByApi = false;
	public boolean isUsedByApi() {
		return this.usedByApi;
	}
		
	public ControlStationCore() throws Exception {
		this(false,null,null);
	}
	
	public ControlStationCore(boolean initForApi, String confDir, String protocolloDefault) throws Exception {

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
				utenzeConfig = ControlStationCore.getUtenzePasswordEncryptEngine_apiMode();
				applicativiConfig = ControlStationCore.getApplicativiPasswordEncryptEngine_apiMode();
				soggettiConfig = ControlStationCore.getSoggettiPasswordEncryptEngine_apiMode();
				
				this.applicativiPasswordConfiguration = "APIMode";
				this.applicativiPasswordEncryptEngine = applicativiConfig;
				this.applicativiPasswordVerifierEngine = ControlStationCore.getApplicativiPasswordVerifierEngine_apiMode();
				this.applicativiBasicPasswordEnableConstraints = (this.applicativiPasswordVerifierEngine!=null);
				
				this.soggettiPasswordConfiguration = "APIMode";
				this.soggettiPasswordEncryptEngine = soggettiConfig;
				this.soggettiPasswordVerifierEngine = ControlStationCore.getSoggettiPasswordVerifierEngine_apiMode();
				this.soggettiBasicPasswordEnableConstraints = (this.soggettiPasswordVerifierEngine!=null);
			}
			else {
				utenzeConfig = this.getUtenzePasswordEncrypt();
				applicativiConfig = this.getApplicativiPasswordEncrypt();
				soggettiConfig = this.getSoggettiPasswordEncrypt();
			}
			this.utenzePasswordManager = CryptFactory.getCrypt(log, utenzeConfig);
			if(utenzeConfig.isBackwardCompatibility()) {
				this.utenzePasswordManager_backwardCompatibility = CryptFactory.getOldMD5Crypt(log);
			}
			this.applicativiPasswordManager = CryptFactory.getCrypt(log, applicativiConfig);
			this.soggettiPasswordManager = CryptFactory.getCrypt(log, soggettiConfig);
			
		}catch(Exception e){
			ControlStationCore.logError("Errore di inizializzazione: "+e.getMessage(), e);
			throw e;
		}
	}

	public ControlStationCore(ControlStationCore core) throws Exception {

		/** Impostazioni grafiche */
		this.consoleNomeSintesi = core.consoleNomeSintesi;
		this.consoleNomeEsteso = core.consoleNomeEsteso;
		this.consoleCSS = core.consoleCSS;
		this.consoleLanguage = core.consoleLanguage;
		this.consoleLunghezzaLabel = core.consoleLunghezzaLabel;
		this.logoHeaderImage = core.logoHeaderImage;
		this.logoHeaderLink = core.logoHeaderLink;
		this.logoHeaderTitolo = core.logoHeaderTitolo;
		this.defaultFont = core.defaultFont;
		this.affineTransform = core.affineTransform;
		this.fontRenderContext = core.fontRenderContext;

		/** Tipo del Database */
		this.tipoDB = core.tipoDB;

		/** Accesso alle code JMS: Smistatore */
		this.smistatoreQueue = core.smistatoreQueue;
		this.cfName = core.cfName;
		this.cfProp = core.cfProp;

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
		this.tracce_showConfigurazioneCustomAppender = core.tracce_showConfigurazioneCustomAppender;
		this.tracce_sameDBWebUI = core.tracce_sameDBWebUI;
		this.tracce_showSorgentiDatiDatabase = core.tracce_showSorgentiDatiDatabase;
		this.tracce_datasource = core.tracce_datasource;
		this.tracce_tipoDatabase = core.tracce_tipoDatabase;
		this.tracce_ctxDatasource = core.tracce_ctxDatasource;
		this.driverTracciamento = core.driverTracciamento;

		/** MsgDiagnostici */
		this.msgDiagnostici_showConfigurazioneCustomAppender = core.msgDiagnostici_showConfigurazioneCustomAppender;
		this.msgDiagnostici_sameDBWebUI = core.msgDiagnostici_sameDBWebUI;
		this.msgDiagnostici_showSorgentiDatiDatabase = core.msgDiagnostici_showSorgentiDatiDatabase;
		this.msgDiagnostici_datasource = core.msgDiagnostici_datasource;
		this.msgDiagnostici_tipoDatabase = core.msgDiagnostici_tipoDatabase;
		this.msgDiagnostici_ctxDatasource = core.msgDiagnostici_ctxDatasource;
		this.driverMSGDiagnostici = core.driverMSGDiagnostici;
		
		/** Dump */
		this.dump_showConfigurazioneCustomAppender = core.dump_showConfigurazioneCustomAppender;
		this.dump_showConfigurazioneDumpRealtime = core.dump_showConfigurazioneDumpRealtime;

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
		this.utenzePasswordManager_backwardCompatibility = core.utenzePasswordManager_backwardCompatibility;
		
		/** Applicativi Console */
		this.applicativiPasswordConfiguration = core.applicativiPasswordConfiguration;
		this.applicativiBasicPasswordEnableConstraints = core.applicativiBasicPasswordEnableConstraints;
		this.applicativiBasicLunghezzaPasswordGenerate = core.applicativiBasicLunghezzaPasswordGenerate;
		this.applicativiApiKeyLunghezzaPasswordGenerate = core.applicativiApiKeyLunghezzaPasswordGenerate;
		this.applicativiPasswordVerifierEngine = core.applicativiPasswordVerifierEngine;
		this.applicativiPasswordEncryptEngine = core.applicativiPasswordEncryptEngine;
		this.applicativiPasswordManager = core.applicativiPasswordManager;
		
		/** Soggetti Console */
		this.soggettiPasswordConfiguration = core.soggettiPasswordConfiguration;
		this.soggettiBasicPasswordEnableConstraints = core.soggettiBasicPasswordEnableConstraints;
		this.soggettiBasicLunghezzaPasswordGenerate = core.soggettiBasicLunghezzaPasswordGenerate;
		this.soggettiApiKeyLunghezzaPasswordGenerate = core.soggettiApiKeyLunghezzaPasswordGenerate;
		this.soggettiPasswordVerifierEngine = core.soggettiPasswordVerifierEngine;
		this.soggettiPasswordEncryptEngine = core.soggettiPasswordEncryptEngine;
		this.soggettiPasswordManager = core.soggettiPasswordManager;
		
		/** MessageSecurity PropertiesSourceConfiguration */
		this.messageSecurityPropertiesSourceConfiguration = core.messageSecurityPropertiesSourceConfiguration;
		
		/** PolicyGestioneToken PropertiesSourceConfiguration */
		this.policyGestioneTokenPropertiesSourceConfiguration = core.policyGestioneTokenPropertiesSourceConfiguration;
		
		/** ControlloTraffico */
		this.isControlloTrafficoPolicyGlobaleGroupByApi = core.isControlloTrafficoPolicyGlobaleGroupByApi;
		this.isControlloTrafficoPolicyGlobaleFiltroApi = core.isControlloTrafficoPolicyGlobaleFiltroApi;
		this.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore = core.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore;
		
		/** Auditing */
		this.isAuditingRegistrazioneElementiBinari = core.isAuditingRegistrazioneElementiBinari;
		
		/** IntegrationManager */
		this.isIntegrationManagerEnabled = core.isIntegrationManagerEnabled;
		
		/** API */
		this.isApiResourcePathValidatorEnabled = core.isApiResourcePathValidatorEnabled;
		this.isApiResourceHttpMethodAndPathQualsiasiEnabled = core.isApiResourceHttpMethodAndPathQualsiasiEnabled;
		this.getApiResourcePathQualsiasiSpecialChar = core.getApiResourcePathQualsiasiSpecialChar;
		
		/** Accordi di Cooperazione */
		this.isAccordiCooperazioneEnabled = core.isAccordiCooperazioneEnabled;
		
		/** Message Engine */
		this.messageEngines = core.messageEngines;
		
		/** Credenziali Basic */
		this.isSoggettiCredenzialiBasicCheckUniqueUsePassword = core.isSoggettiCredenzialiBasicCheckUniqueUsePassword;
		this.isApplicativiCredenzialiBasicCheckUniqueUsePassword = core.isApplicativiCredenzialiBasicCheckUniqueUsePassword;

		/** Connettori Multipli */
		this.isConnettoriMultipliEnabled = core.isConnettoriMultipliEnabled;
		this.isConnettoriMultipliConsegnaMultiplaEnabled = core.isConnettoriMultipliConsegnaMultiplaEnabled;
		
		/** Applicativi Server */
		this.isApplicativiServerEnabled = core.isApplicativiServerEnabled;		
		
		/** Parametri pdd */
		this.portaPubblica = core.portaPubblica;
		this.portaGestione = core.portaGestione;
		this.indirizzoPubblico = core.indirizzoPubblico;
		this.indirizzoGestione = core.indirizzoGestione;
		
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
		this.gestioneWorkflowStatoDocumenti_visualizzaStatoLista = core.gestioneWorkflowStatoDocumenti_visualizzaStatoLista;
		this.gestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale = core.gestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale;
		this.showInterfacceAPI = core.showInterfacceAPI;
		this.showAllegati = core.showAllegati;
		this.enableAutoMappingWsdlIntoAccordo = core.enableAutoMappingWsdlIntoAccordo;
		this.enableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes = core.enableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes;
		this.showMTOMVisualizzazioneCompleta = core.showMTOMVisualizzazioneCompleta;
		this.portaCorrelazioneApplicativaMaxLength = core.portaCorrelazioneApplicativaMaxLength;
		this.showPortaDelegataLocalForward = core.showPortaDelegataLocalForward;
		this.isProprietaErogazioni_showModalitaStandard = core.isProprietaErogazioni_showModalitaStandard;
		this.isProprietaFruizioni_showModalitaStandard = core.isProprietaFruizioni_showModalitaStandard;
		this.isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona = core.isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona;
		this.isVisualizzazioneConfigurazioneDiagnosticaLog4J = core.isVisualizzazioneConfigurazioneDiagnosticaLog4J;
		this.tokenPolicyForceId = core.tokenPolicyForceId;
		this.tokenPolicyForceIdEnabled = core.tokenPolicyForceIdEnabled;
		this.tokenPolicyTipologia = core.tokenPolicyTipologia;
		this.showServiziVisualizzaModalitaElenco = core.showServiziVisualizzaModalitaElenco;
		this.selectListSoggettiOperativi_numeroMassimoSoggetti = core.selectListSoggettiOperativi_numeroMassimoSoggetti;
		this.selectListSoggettiOperativi_dimensioneMassimaLabel = core.selectListSoggettiOperativi_dimensioneMassimaLabel;

		/** Motori di Sincronizzazione */
		this.sincronizzazionePddEngineEnabled = core.sincronizzazionePddEngineEnabled;
		this.sincronizzazionePddEngineEnabled_prefissoNomeCodaConfigurazionePdd = core.sincronizzazionePddEngineEnabled_prefissoNomeCodaConfigurazionePdd;
		this.sincronizzazionePddEngineEnabled_scriptShell_Path = core.sincronizzazionePddEngineEnabled_scriptShell_Path;
		this.sincronizzazionePddEngineEnabled_scriptShell_Args = core.sincronizzazionePddEngineEnabled_scriptShell_Args;
		this.sincronizzazioneRegistroEngineEnabled = core.sincronizzazioneRegistroEngineEnabled;
		this.sincronizzazioneGEEngineEnabled = core.sincronizzazioneGEEngineEnabled;
		this.sincronizzazioneGE_TipoSoggetto = core.sincronizzazioneGE_TipoSoggetto;
		this.sincronizzazioneGE_NomeSoggetto = core.sincronizzazioneGE_NomeSoggetto;
		this.sincronizzazioneGE_NomeServizioApplicativo = core.sincronizzazioneGE_NomeServizioApplicativo;

		/** Opzioni di importazione/esportazione Archivi */
		this.importArchivi_tipoPdD = core.importArchivi_tipoPdD;
		this.exportArchive_configurazione_soloDumpCompleto = core.exportArchive_configurazione_soloDumpCompleto;
		this.exportArchive_servizi_standard = core.exportArchive_servizi_standard;
		
		/** Multitenant */
		this.multitenant = core.multitenant;
		this.multitenantSoggettiErogazioni = core.multitenantSoggettiErogazioni;
		this.multitenantSoggettiFruizioni = core.multitenantSoggettiFruizioni;
		
		/** Altro */
		this.suffissoConnettoreAutomatico = core.suffissoConnettoreAutomatico;
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
		this.jmxPdD_aliases = core.jmxPdD_aliases;
		this.jmxPdD_descrizioni = core.jmxPdD_descrizioni;
		this.jmxPdD_tipoAccesso = core.jmxPdD_tipoAccesso;
		this.jmxPdD_remoteAccess_username = core.jmxPdD_remoteAccess_username;
		this.jmxPdD_remoteAccess_password = core.jmxPdD_remoteAccess_password;
		this.jmxPdD_remoteAccess_as = core.jmxPdD_remoteAccess_as;
		this.jmxPdD_remoteAccess_factory = core.jmxPdD_remoteAccess_factory;
		this.jmxPdD_remoteAccess_url = core.jmxPdD_remoteAccess_url;
		this.jmxPdD_dominio = core.jmxPdD_dominio;
		this.jmxPdD_configurazioneSistema_type = core.jmxPdD_configurazioneSistema_type;
		this.jmxPdD_configurazioneSistema_nomeRisorsa = core.jmxPdD_configurazioneSistema_nomeRisorsa;
		this.jmxPdD_configurazioneSistema_nomeMetodo_versionePdD = core.jmxPdD_configurazioneSistema_nomeMetodo_versionePdD;
		this.jmxPdD_configurazioneSistema_nomeMetodo_versioneBaseDati = core.jmxPdD_configurazioneSistema_nomeMetodo_versioneBaseDati;
		this.jmxPdD_configurazioneSistema_nomeMetodo_versioneJava = core.jmxPdD_configurazioneSistema_nomeMetodo_versioneJava;
		this.jmxPdD_configurazioneSistema_nomeMetodo_vendorJava = core.jmxPdD_configurazioneSistema_nomeMetodo_vendorJava;
		this.jmxPdD_configurazioneSistema_nomeMetodo_tipoDatabase = core.jmxPdD_configurazioneSistema_nomeMetodo_tipoDatabase;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniSSL = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniSSL;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteSSL = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteSSL;
		this.jmxPdD_configurazioneSistema_showInformazioniCryptographyKeyLength = core.jmxPdD_configurazioneSistema_showInformazioniCryptographyKeyLength;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCryptographyKeyLength = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCryptographyKeyLength;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCharset = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCharset;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniInternazionalizzazione = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniInternazionalizzazione;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteInternazionalizzazione = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteInternazionalizzazione;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniTimeZone = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniTimeZone;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteTimeZone = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteTimeZone;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaNetworking = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaNetworking;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteProprietaJavaNetworking = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteProprietaJavaNetworking;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaAltro = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaAltro;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaSistema = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaSistema;
		this.jmxPdD_configurazioneSistema_nomeMetodo_messageFactory = core.jmxPdD_configurazioneSistema_nomeMetodo_messageFactory;
		this.jmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione = core.jmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione;
		this.jmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols = core.jmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols;
		this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniInstallazione = core.jmxPdD_configurazioneSistema_nomeMetodo_informazioniInstallazione;
		this.jmxPdD_configurazioneSistema_nomeMetodo_getFileTrace = core.jmxPdD_configurazioneSistema_nomeMetodo_getFileTrace;
		this.jmxPdD_configurazioneSistema_nomeMetodo_updateFileTrace = core.jmxPdD_configurazioneSistema_nomeMetodo_updateFileTrace;
		this.jmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio = core.jmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio;
		this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniDB = core.jmxPdD_configurazioneSistema_nomeMetodo_connessioniDB;
		this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniJMS = core.jmxPdD_configurazioneSistema_nomeMetodo_connessioniJMS;
		this.jmxPdD_configurazioneSistema_nomeMetodo_idTransazioniAttive = core.jmxPdD_configurazioneSistema_nomeMetodo_idTransazioniAttive;
		this.jmxPdD_configurazioneSistema_nomeMetodo_idProtocolloTransazioniAttive = core.jmxPdD_configurazioneSistema_nomeMetodo_idProtocolloTransazioniAttive;
		this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniPD = core.jmxPdD_configurazioneSistema_nomeMetodo_connessioniPD;
		this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniPA = core.jmxPdD_configurazioneSistema_nomeMetodo_connessioniPA;
		this.jmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD = core.jmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD;
		this.jmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici = core.jmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici;
		this.jmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j = core.jmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j;
		this.jmxPdD_configurazioneSistema_nomeAttributo_tracciamento = core.jmxPdD_configurazioneSistema_nomeAttributo_tracciamento;
		this.jmxPdD_configurazioneSistema_nomeAttributo_dumpPD = core.jmxPdD_configurazioneSistema_nomeAttributo_dumpPD;
		this.jmxPdD_configurazioneSistema_nomeAttributo_dumpPA = core.jmxPdD_configurazioneSistema_nomeAttributo_dumpPA;
		this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_diagnostica = core.jmxPdD_configurazioneSistema_nomeAttributo_log4j_diagnostica;
		this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_openspcoop = core.jmxPdD_configurazioneSistema_nomeAttributo_log4j_openspcoop;
		this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_integrationManager = core.jmxPdD_configurazioneSistema_nomeAttributo_log4j_integrationManager;
		this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_tracciamento = core.jmxPdD_configurazioneSistema_nomeAttributo_log4j_tracciamento;
		this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_dump = core.jmxPdD_configurazioneSistema_nomeAttributo_log4j_dump;
		this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorStatusCode = core.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorStatusCode;
		this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorInstanceId = core.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorInstanceId;
		this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeBadResponse = core.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeBadResponse;
		this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalResponseError = core.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalResponseError;
		this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalRequestError = core.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalRequestError;
		this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalError = core.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalError;
		this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorDetails = core.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorDetails;
		this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorUseStatusCodeAsFaultCode = core.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorUseStatusCodeAsFaultCode;
		this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorGenerateHttpHeaderGovWayCode = core.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorGenerateHttpHeaderGovWayCode;
		this.jmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById = core.jmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById;
		this.jmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreById = core.jmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreById;
		this.jmxPdD_configurazioneSistema_nomeMetodo_enablePortaDelegata = core.jmxPdD_configurazioneSistema_nomeMetodo_enablePortaDelegata;
		this.jmxPdD_configurazioneSistema_nomeMetodo_disablePortaDelegata = core.jmxPdD_configurazioneSistema_nomeMetodo_disablePortaDelegata;
		this.jmxPdD_configurazioneSistema_nomeMetodo_enablePortaApplicativa = core.jmxPdD_configurazioneSistema_nomeMetodo_enablePortaApplicativa;
		this.jmxPdD_configurazioneSistema_nomeMetodo_disablePortaApplicativa = core.jmxPdD_configurazioneSistema_nomeMetodo_disablePortaApplicativa;
		this.jmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi = core.jmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi;
		this.jmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD = core.jmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD;
		this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegata = core.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegata;
		this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataAbilitazioniPuntuali = core.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataAbilitazioniPuntuali;
		this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataDisabilitazioniPuntuali = core.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataDisabilitazioniPuntuali;
		this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativa = core.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativa;
		this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaAbilitazioniPuntuali = core.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaAbilitazioniPuntuali;
		this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaDisabilitazioniPuntuali = core.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaDisabilitazioniPuntuali;
		this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioIntegrationManager = core.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioIntegrationManager;
		this.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata = core.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata;
		this.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata = core.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata;
		this.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa = core.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa;
		this.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa = core.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa;
		this.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager = core.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager;
		this.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager = core.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager;
		this.jmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW = core.jmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW;
		this.jmxPdD_configurazioneSistema_nomeAttributo_numeroDatasourceGW = core.jmxPdD_configurazioneSistema_nomeAttributo_numeroDatasourceGW;
		this.jmxPdD_configurazioneSistema_nomeMetodo_getDatasourcesGW = core.jmxPdD_configurazioneSistema_nomeMetodo_getDatasourcesGW;
		this.jmxPdD_configurazioneSistema_nomeMetodo_getUsedConnectionsDatasourcesGW = core.jmxPdD_configurazioneSistema_nomeMetodo_getUsedConnectionsDatasourcesGW;
		this.jmxPdD_configurazioneSistema_nomeMetodo_getInformazioniDatabaseDatasourcesGW = core.jmxPdD_configurazioneSistema_nomeMetodo_getInformazioniDatabaseDatasourcesGW;
		this.jmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi = core.jmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi;
		this.jmxPdD_configurazioneSistema_nomeMetodo_getThreadPoolStatus = core.jmxPdD_configurazioneSistema_nomeMetodo_getThreadPoolStatus;
		this.jmxPdD_configurazioneSistema_nomeRisorsaSystemPropertiesPdD = core.jmxPdD_configurazioneSistema_nomeRisorsaSystemPropertiesPdD;
		this.jmxPdD_configurazioneSistema_nomeMetodo_refreshPersistentConfiguration = core.jmxPdD_configurazioneSistema_nomeMetodo_refreshPersistentConfiguration;
		this.jmxPdD_caches = core.jmxPdD_caches;
		this.jmxPdD_caches_prefill = core.jmxPdD_caches_prefill;
		this.jmxPdD_cache_type = core.jmxPdD_cache_type;
		this.jmxPdD_cache_nomeAttributo_cacheAbilitata = core.jmxPdD_cache_nomeAttributo_cacheAbilitata;
		this.jmxPdD_cache_nomeMetodo_statoCache = core.jmxPdD_cache_nomeMetodo_statoCache;
		this.jmxPdD_cache_nomeMetodo_resetCache = core.jmxPdD_cache_nomeMetodo_resetCache;
		this.jmxPdD_cache_nomeMetodo_prefillCache = core.jmxPdD_cache_nomeMetodo_prefillCache;
	}




	/* --- INIT METHOD --- */

	/**
	 * Prova ad ottenere una istanza del DBManager per utilizzare le connessioni
	 * del pool
	 * 
	 * @throws Exception
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
				this.tracce_sameDBWebUI = datasourceProperties.isSinglePdD_TracceStessoDBConsole();
				if(this.tracce_sameDBWebUI==false){
					this.tracce_datasource = datasourceProperties.getSinglePdD_TracceDataSource();
					this.tracce_ctxDatasource = datasourceProperties.getSinglePdD_TracceDataSourceContext();
					this.tracce_tipoDatabase = datasourceProperties.getSinglePdD_TracceTipoDatabase();
				}
				
				this.msgDiagnostici_sameDBWebUI = datasourceProperties.isSinglePdD_MessaggiDiagnosticiStessoDBConsole();
				if(this.msgDiagnostici_sameDBWebUI==false){
					this.msgDiagnostici_datasource = datasourceProperties.getSinglePdD_MessaggiDiagnosticiDataSource();
					this.msgDiagnostici_ctxDatasource = datasourceProperties.getSinglePdD_MessaggiDiagnosticiDataSourceContext();
					this.msgDiagnostici_tipoDatabase = datasourceProperties.getSinglePdD_MessaggiDiagnosticiTipoDatabase();
				}
			}
			
		} catch (java.lang.Exception e) {
			ControlStationCore.log.error("[ControlStationCore::initConnections] Impossibile leggere i dati dal file console.datasource.properties[" + e.toString() + "]",e);
			throw new ControlStationCoreException("Impossibile leggere i dati dal file console.datasource.properties: " + e.getMessage(),e);
		} 

		if (!DBManager.isInitialized()) {
			int i = 0;
			while (!DBManager.isInitialized() && (i < 6)) {

				try {
					ControlStationCore.log.debug("jndiName=" + jndiName);
					ControlStationCore.log.debug("jndiProp=" + jndiProp.toString());
					DBManager.initialize(jndiName, jndiProp);
					ControlStationCore.log.info("Inizializzazione DBManager Effettuata.");
				} catch (Exception e) {
					ControlStationCore.log.error("Inizializzazione DBManager fallita.", e);
					ControlStationCore.log.info("Ritento inizializzazione ...");
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

		this.smistatoreQueue = "";
		this.cfName = "";
		this.cfProp = new Properties();

			
		// Leggo le informazioni da console.properties
		ConsoleProperties consoleProperties = null;
		try {
			consoleProperties = ConsoleProperties.getInstance();
			
			// Funzionalità Generiche
			this.protocolloDefault = consoleProperties.getProtocolloDefault();
			this.jdbcSerializableAttesaAttiva = consoleProperties.getGestioneSerializableDB_AttesaAttiva();
			this.jdbcSerializableCheck = consoleProperties.getGestioneSerializableDB_CheckInterval();
			this.singlePdD = consoleProperties.isSinglePdD();
			this.enabledToken_generazioneAutomaticaPorteDelegate = consoleProperties.isToken_GenerazioneAutomaticaPorteDelegate_enabled();
			this.enabledAutenticazione_generazioneAutomaticaPorteDelegate = consoleProperties.isAutenticazione_GenerazioneAutomaticaPorteDelegate_enabled();
			this.autenticazione_generazioneAutomaticaPorteDelegate = consoleProperties.getAutenticazione_GenerazioneAutomaticaPorteDelegate();
			this.enabledAutorizzazione_generazioneAutomaticaPorteDelegate = consoleProperties.isAutorizzazione_GenerazioneAutomaticaPorteDelegate_enabled();
			this.autorizzazione_generazioneAutomaticaPorteDelegate = consoleProperties.getAutorizzazione_GenerazioneAutomaticaPorteDelegate();		
			this.enabledToken_generazioneAutomaticaPorteApplicative = consoleProperties.isToken_GenerazioneAutomaticaPorteApplicative_enabled();
			this.enabledAutenticazione_generazioneAutomaticaPorteApplicative = consoleProperties.isAutenticazione_GenerazioneAutomaticaPorteApplicative_enabled();
			this.autenticazione_generazioneAutomaticaPorteApplicative = consoleProperties.getAutenticazione_GenerazioneAutomaticaPorteApplicative();
			this.enabledAutorizzazione_generazioneAutomaticaPorteApplicative = consoleProperties.isAutorizzazione_GenerazioneAutomaticaPorteApplicative_enabled();
			this.autorizzazione_generazioneAutomaticaPorteApplicative = consoleProperties.getAutorizzazione_GenerazioneAutomaticaPorteApplicative();
			this.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto = consoleProperties.isAbilitatoControlloUnicitaImplementazioneAccordoPerSoggetto();
			this.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto = consoleProperties.isAbilitatoControlloUnicitaImplementazionePortTypePerSoggetto();
			this.utenzePasswordConfiguration = consoleProperties.getConsoleUtenzePassword();
			this.utenzeLunghezzaPasswordGenerate = consoleProperties.getConsoleUtenzeLunghezzaPasswordGenerate();
			this.applicativiPasswordConfiguration = consoleProperties.getConsoleApplicativiPassword();
			this.applicativiBasicPasswordEnableConstraints = consoleProperties.isConsoleApplicativiBasicPasswordEnableConstraints();
			this.applicativiBasicLunghezzaPasswordGenerate = consoleProperties.getConsoleApplicativiBasicLunghezzaPasswordGenerate();
			this.applicativiApiKeyLunghezzaPasswordGenerate = consoleProperties.getConsoleApplicativiApiKeyLunghezzaPasswordGenerate();
			this.soggettiPasswordConfiguration = consoleProperties.getConsoleSoggettiPassword();
			this.soggettiBasicPasswordEnableConstraints = consoleProperties.isConsoleSoggettiBasicPasswordEnableConstraints();
			this.soggettiBasicLunghezzaPasswordGenerate = consoleProperties.getConsoleSoggettiBasicLunghezzaPasswordGenerate();
			this.soggettiApiKeyLunghezzaPasswordGenerate = consoleProperties.getConsoleSoggettiApiKeyLunghezzaPasswordGenerate();			
			this.messageSecurityPropertiesSourceConfiguration = consoleProperties.getMessageSecurityPropertiesSourceConfiguration();
			this.policyGestioneTokenPropertiesSourceConfiguration = consoleProperties.getPolicyGestioneTokenPropertiesSourceConfiguration();
			this.isControlloTrafficoPolicyGlobaleGroupByApi = consoleProperties.isControlloTrafficoPolicyGlobaleGroupByApi();
			this.isControlloTrafficoPolicyGlobaleFiltroApi = consoleProperties.isControlloTrafficoPolicyGlobaleFiltroApi();
			this.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore = consoleProperties.isControlloTrafficoPolicyGlobaleFiltroApiSoggettoErogatore();
			this.isAuditingRegistrazioneElementiBinari = consoleProperties.isAuditingRegistrazioneElementiBinari();
			this.isIntegrationManagerEnabled = consoleProperties.isIntegrationManagerEnabled();
			this.isApiResourcePathValidatorEnabled = consoleProperties.isApiResourcePathValidatorEnabled();
			this.isApiResourceHttpMethodAndPathQualsiasiEnabled = consoleProperties.isApiResourceHttpMethodAndPathQualsiasiEnabled();
			this.getApiResourcePathQualsiasiSpecialChar = consoleProperties.getApiResourcePathQualsiasiSpecialChar();
			this.isAccordiCooperazioneEnabled = consoleProperties.isAccordiCooperazioneEnabled();
			this.messageEngines = consoleProperties.getMessageEngines();
			this.isSoggettiCredenzialiBasicCheckUniqueUsePassword = consoleProperties.isSoggettiCredenzialiBasicCheckUniqueUsePassword();
			this.isApplicativiCredenzialiBasicCheckUniqueUsePassword = consoleProperties.isApplicativiCredenzialiBasicCheckUniqueUsePassword();
			this.isConnettoriMultipliEnabled = consoleProperties.isConnettoriMultipliEnabled();
			this.isConnettoriMultipliConsegnaMultiplaEnabled = consoleProperties.isConnettoriMultipliConsegnaMultiplaEnabled();
			this.isApplicativiServerEnabled = consoleProperties.isApplicativiServerEnabled();
			
			// Impostazioni grafiche
			this.consoleNomeSintesi = consoleProperties.getConsoleNomeSintesi();
			this.consoleNomeEsteso = consoleProperties.getConsoleNomeEsteso();
			this.consoleCSS = consoleProperties.getConsoleCSS();
			this.consoleLanguage = consoleProperties.getConsoleLanguage();
			this.consoleLunghezzaLabel = consoleProperties.getConsoleLunghezzaLabel();
			this.logoHeaderImage = consoleProperties.getLogoHeaderImage();
			this.logoHeaderLink = consoleProperties.getLogoHeaderLink();
			this.logoHeaderTitolo = consoleProperties.getLogoHeaderTitolo();
			String fontName = consoleProperties.getConsoleFontFamilyName();
			int fontStyle = consoleProperties.getConsoleFontStyle();
			this.defaultFont = new Font(fontName,fontStyle, 14);
			
			// Opzioni di Visualizzazione
			this.showJ2eeOptions = consoleProperties.isShowJ2eeOptions();
			this.showConfigurazioniPersonalizzate = consoleProperties.isConsoleConfigurazioniPersonalizzate();
			this.showGestioneSoggettiRouter = consoleProperties.isConsoleGestioneSoggettiRouter();
			this.showGestioneSoggettiVirtuali = consoleProperties.isConsoleGestioneSoggettiVirtuali();
			this.showGestioneWorkflowStatoDocumenti = consoleProperties.isConsoleGestioneWorkflowStatoDocumenti();
			this.gestioneWorkflowStatoDocumenti_visualizzaStatoLista = consoleProperties.isConsoleGestioneWorkflowStatoDocumenti_visualizzaStatoLista();
			this.gestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale = consoleProperties.isConsoleGestioneWorkflowStatoDocumenti_ripristinoStatoOperativoDaFinale();
			this.showInterfacceAPI = consoleProperties.isConsoleInterfacceAPI_visualizza();
			this.showAllegati = consoleProperties.isConsoleAllegati_visualizza();
			this.showFlagPrivato = consoleProperties.isMenuVisualizzaFlagPrivato();
			this.showAllConnettori = consoleProperties.isMenuVisualizzaListaCompletaConnettori();
			this.showDebugOptionConnettore = consoleProperties.isMenuVisualizzaOpzioneDebugConnettore();
			this.showCorrelazioneAsincronaInAccordi = consoleProperties.isMenuAccordiVisualizzaCorrelazioneAsincrona();
			this.showAccordiInformazioniProtocollo = consoleProperties.isMenuAccordiVisualizzazioneGestioneInformazioniProtocollo();
			this.showCountElementInLinkList = consoleProperties.isElenchiVisualizzaCountElementi();
			this.conservaRisultatiRicerca = consoleProperties.isElenchiRicercaConservaCriteri();
			if(conservaRisultatiRicerca_staticInfo_read==null) {
				conservaRisultatiRicerca_staticInfo_read = true;
				conservaRisultatiRicerca_staticInfo = this.conservaRisultatiRicerca;
			}
			this.showAccordiColonnaAzioni = consoleProperties.isElenchiAccordiVisualizzaColonnaAzioni();
			this.showPulsantiImportExport = consoleProperties.isElenchiMenuVisualizzazionePulsantiImportExportPackage();
			this.elenchiMenuIdentificativiLunghezzaMassima = consoleProperties.getElenchiMenuIdentificativiLunghezzaMassima();
			this.enableAutoMappingWsdlIntoAccordo = consoleProperties.isEnableAutoMappingWsdlIntoAccordo();
			this.enableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes = consoleProperties.isEnableAutoMappingWsdlIntoAccordo_estrazioneSchemiInWsdlTypes();
			this.showMTOMVisualizzazioneCompleta = consoleProperties.isMenuMTOMVisualizzazioneCompleta();
			this.portaCorrelazioneApplicativaMaxLength = consoleProperties.getPortaCorrelazioneApplicativaMaxLength();
			this.showPortaDelegataLocalForward = consoleProperties.isMenuPortaDelegataLocalForward();
			this.isProprietaErogazioni_showModalitaStandard = consoleProperties.isProprietaErogazioni_showModalitaStandard();
			this.isProprietaFruizioni_showModalitaStandard = consoleProperties.isProprietaFruizioni_showModalitaStandard();
			this.isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona = consoleProperties.isElenchiSA_asincroniNonSupportati_VisualizzaRispostaAsincrona();
			this.isVisualizzazioneConfigurazioneDiagnosticaLog4J = consoleProperties.isVisualizzazioneConfigurazioneDiagnosticaLog4J();
			this.tokenPolicyForceId = consoleProperties.getTokenPolicyForceId();
			this.tokenPolicyForceIdEnabled = StringUtils.isNotEmpty(this.tokenPolicyForceId);
			this.tokenPolicyTipologia = consoleProperties.getTokenPolicyTipologia();
			this.showServiziVisualizzaModalitaElenco = consoleProperties.isEnableServiziVisualizzaModalitaElenco();
			this.selectListSoggettiOperativi_numeroMassimoSoggetti = consoleProperties.getNumeroMassimoSoggettiOperativiMenuUtente();
			this.selectListSoggettiOperativi_dimensioneMassimaLabel = consoleProperties.getLunghezzaMassimaLabelSoggettiOperativiMenuUtente();
			
			// Gestione govwayConsole centralizzata
			if(this.singlePdD == false){
				this.sincronizzazionePddEngineEnabled = consoleProperties.isGestioneCentralizzata_SincronizzazionePdd();
				this.sincronizzazionePddEngineEnabled_prefissoNomeCodaConfigurazionePdd = consoleProperties.getGestioneCentralizzata_PrefissoNomeCodaConfigurazionePdd();
				this.sincronizzazionePddEngineEnabled_scriptShell_Path = consoleProperties.getGestioneCentralizzata_GestorePddd_ScriptShell_Path();
				this.sincronizzazionePddEngineEnabled_scriptShell_Args = consoleProperties.getGestioneCentralizzata_GestorePddd_ScriptShell_Args();
				this.sincronizzazioneRegistroEngineEnabled = consoleProperties.isGestioneCentralizzata_SincronizzazioneRegistro();
				this.sincronizzazioneGEEngineEnabled = consoleProperties.isGestioneCentralizzata_SincronizzazioneGestoreEventi();
				
				this.smistatoreQueue = consoleProperties.getGestioneCentralizzata_NomeCodaSmistatore();
				
				this.sincronizzazioneGE_TipoSoggetto = consoleProperties.getGestioneCentralizzata_GestoreEventiTipoSoggetto();
				this.sincronizzazioneGE_NomeSoggetto = consoleProperties.getGestioneCentralizzata_GestoreEventiNomeSoggetto();
				this.sincronizzazioneGE_NomeServizioApplicativo = consoleProperties.getGestioneCentralizzata_GestoreEventiNomeServizioApplicativo();
				
				this.suffissoConnettoreAutomatico = consoleProperties.getGestioneCentralizzata_URLContextCreazioneAutomaticaSoggetto();
				
				this.indirizzoPubblico = consoleProperties.getGestioneCentralizzata_PddIndirizzoIpPubblico();
				this.portaPubblica = consoleProperties.getGestioneCentralizzata_PddPortaPubblica();
				this.indirizzoGestione = consoleProperties.getGestioneCentralizzata_PddIndirizzoIpGestione();
				this.portaGestione = consoleProperties.getGestioneCentralizzata_PddPortaGestione();
			}
			
			// Gestione govwayConsole locale
			if(this.singlePdD){
				this.gestionePddAbilitata = consoleProperties.isSinglePdD_GestionePdd();
				
				this.registroServiziLocale = consoleProperties.isSinglePdD_RegistroServiziLocale();
				
				this.tracce_showConfigurazioneCustomAppender = consoleProperties.isSinglePdD_TracceConfigurazioneCustomAppender();
				this.tracce_showSorgentiDatiDatabase = consoleProperties.isSinglePdD_TracceGestioneSorgentiDatiPrelevataDaDatabase();
				
				this.msgDiagnostici_showConfigurazioneCustomAppender = consoleProperties.isSinglePdD_MessaggiDiagnosticiConfigurazioneCustomAppender();
				this.msgDiagnostici_showSorgentiDatiDatabase = consoleProperties.isSinglePdD_MessaggiDiagnosticiGestioneSorgentiDatiPrelevataDaDatabase();
				
				this.dump_showConfigurazioneCustomAppender = consoleProperties.isSinglePdD_DumpConfigurazioneCustomAppender();
				this.dump_showConfigurazioneDumpRealtime = consoleProperties.isSinglePdD_DumpConfigurazioneRealtime();
			}
			
			// Opzioni di importazione/esportazione Archivi
			this.importArchivi_tipoPdD = consoleProperties.getImportArchive_tipoPdD();
			this.exportArchive_configurazione_soloDumpCompleto = consoleProperties.isExportArchive_configurazione_soloDumpCompleto();
			this.exportArchive_servizi_standard = consoleProperties.isExportArchive_servizi_standard();
			
			// Multitenant
			// Inizializzato dopo aver attivato il Database, per leggere la configurazione su DB
			
			// Gestione Visibilità utenti
			this.visioneOggettiGlobale = consoleProperties.isVisibilitaOggettiGlobale();
			this.utentiConVisioneGlobale.addAll(consoleProperties.getUtentiConVisibilitaGlobale());

			/// Opzioni per Plugins
			this.pluginMenu = this.newIExtendedMenu(consoleProperties.getPlugins_Menu());
			this.pluginConfigurazione = this.newIExtendedFormServlet(consoleProperties.getPlugins_Configurazione());
			if(this.pluginConfigurazione!=null){
				for (IExtendedFormServlet formPluginConfigurazione : this.pluginConfigurazione) {
					IExtendedListServlet listPluginConfigurazione = formPluginConfigurazione.getExtendedInternalList();
					if(listPluginConfigurazione!=null){
						this.pluginConfigurazioneList.put(formPluginConfigurazione.getUniqueID(), listPluginConfigurazione);
					}
				}
			}
			this.pluginConnettore = this.newIExtendedConnettore(consoleProperties.getPlugins_Connettore());
			this.pluginPortaDelegata = this.newIExtendedListServlet(consoleProperties.getPlugins_PortaDelegata());
			this.pluginPortaApplicativa = this.newIExtendedListServlet(consoleProperties.getPlugins_PortaApplicativa());

		} catch (java.lang.Exception e) {
			ControlStationCore.log.error("[ControlStationCore::initCore] Impossibile leggere i dati dal file console.properties:" + e.toString(),e);
			throw new ControlStationCoreException("[ControlStationCore::initCore] Impossibile leggere i dati dal file console.properties:" + e.toString(),e);
		}
		
		
		
		// Leggo le informazioni da queue.properties
		QueueProperties queueProperties = null;
		if(this.singlePdD==false){
			try {
	
				queueProperties = QueueProperties.getInstance();
				
				this.cfName = queueProperties.getConnectionFactory();
				this.cfProp = queueProperties.getConnectionFactoryContext();
				
			} catch (java.lang.Exception e) {
				ControlStationCore.log.error("[ControlStationCore::initCore] Impossibile leggere i dati dal file queue.properties[" + e.toString() + "]", e);
				throw new ControlStationCoreException("ControlStationCore: Impossibile leggere i dati dal file queue.properties[" + e.toString() + "]",e);
			} 
		}
	}

	
	private void initCoreJmxResources() throws ControlStationCoreException {

		// Leggo le informazioni da console.properties
		ConsoleProperties consoleProperties = null;
		try {
			consoleProperties = ConsoleProperties.getInstance();
					
			// Opzioni Accesso JMX della PdD
			this.jmxPdD_aliases = consoleProperties.getJmxPdD_aliases();
			if(this.singlePdD==false){
				// se esistono degli alias allora assegno poi come alias i nomi delle pdd operative
				if(this.jmxPdD_aliases!=null && this.jmxPdD_aliases.size()>0){
					this.jmxPdD_aliases = new ArrayList<String>();
					PddCore pddCore = new PddCore(this);
					try{
						List<PdDControlStation> pddList = pddCore.pddList(null, new Search());
						for (PdDControlStation pddControlStation : pddList) {
							if(PddTipologia.OPERATIVO.toString().equals(pddControlStation.getTipo())){
								this.jmxPdD_aliases.add(pddControlStation.getNome());
							}
						}
					}catch(Exception e){}
				}
			}
			
			this.jmxPdD_configurazioneSistema_showInformazioniCryptographyKeyLength = consoleProperties.isJmxPdD_configurazioneSistema_showInformazioniCryptographyKeyLength();
			
			if(this.jmxPdD_aliases!=null){
				for (String alias : this.jmxPdD_aliases) {
					String descrizione = consoleProperties.getJmxPdD_descrizione(alias);
					if(descrizione!=null)
						this.jmxPdD_descrizioni.put(alias,descrizione);
					this.jmxPdD_tipoAccesso.put(alias,consoleProperties.getJmxPdD_tipoAccesso(alias));
					String username = consoleProperties.getJmxPdD_remoteAccess_username(alias);
					if(username!=null)
						this.jmxPdD_remoteAccess_username.put(alias,username);
					String password = consoleProperties.getJmxPdD_remoteAccess_password(alias);
					if(password!=null)
						this.jmxPdD_remoteAccess_password.put(alias,password);
					String as = consoleProperties.getJmxPdD_remoteAccess_applicationServer(alias);
					if(as!=null)
						this.jmxPdD_remoteAccess_as.put(alias,as);
					String factory = consoleProperties.getJmxPdD_remoteAccess_factory(alias);
					if(factory!=null)
						this.jmxPdD_remoteAccess_factory.put(alias,factory);
					String url = consoleProperties.getJmxPdD_remoteAccess_url(alias);
					if(url!=null)
						this.jmxPdD_remoteAccess_url.put(alias,url);
					this.jmxPdD_dominio.put(alias,consoleProperties.getJmxPdD_dominio(alias));
					this.jmxPdD_configurazioneSistema_type.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_type(alias));
					this.jmxPdD_configurazioneSistema_nomeRisorsa.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeRisorsa(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_versionePdD.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_versionePdD(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_versioneBaseDati.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_versioneBaseDati(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_versioneJava.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_versioneJava(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_vendorJava.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_vendorJava(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_tipoDatabase.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_tipoDatabase(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniDatabase(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniSSL.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniSSL(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteSSL.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteSSL(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCryptographyKeyLength.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCryptographyKeyLength(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCharset.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCharset(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniInternazionalizzazione.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniInternazionalizzazione(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteInternazionalizzazione.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteInternazionalizzazione(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniTimeZone.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniTimeZone(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteTimeZone.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteTimeZone(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaNetworking.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaNetworking(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteProprietaJavaNetworking.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniCompleteProprietaJavaNetworking(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaAltro.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaJavaAltro(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaSistema.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniProprietaSistema(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_messageFactory.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_messageFactory(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_directoryConfigurazione(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_pluginProtocols(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_informazioniInstallazione.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_informazioniInstallazione(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_getFileTrace.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_getFileTrace(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_updateFileTrace.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_updateFileTrace(alias));
					this.jmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeRisorsaMonitoraggio(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniDB.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_connessioniDB(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniJMS.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_connessioniJMS(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_idTransazioniAttive.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_idTransazioniAttive(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_idProtocolloTransazioniAttive.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_idProtocolloTransazioniAttive(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniPD.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_connessioniPD(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_connessioniPA.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_connessioniPA(alias));
					this.jmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeRisorsaConfigurazionePdD(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnostici(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_severitaDiagnosticiLog4j(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_tracciamento.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_tracciamento(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_dumpPD.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_dumpPD(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_dumpPA.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_dumpPA(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_diagnostica.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_log4jDiagnostica(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_openspcoop.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_log4jOpenspcoop(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_integrationManager.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_log4jIntegrationManager(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_tracciamento.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_log4jTracciamento(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_log4j_dump.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_log4jDump(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorStatusCode.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorStatusCode(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorInstanceId.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorInstanceId(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeBadResponse.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeBadResponse(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalResponseError.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalResponseError(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalRequestError.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalRequestError(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalError.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorTypeInternalError(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorDetails.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_transactionSpecificErrorDetails(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorUseStatusCodeAsFaultCode.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorUseStatusCodeAsFaultCode(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_transactionErrorGenerateHttpHeaderGovWayCode.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_transactionErrorGenerateHttpHeaderGovWayCode(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_checkConnettoreById(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreById.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_getCertificatiConnettoreById(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_enablePortaDelegata.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_enablePortaDelegata(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_disablePortaDelegata.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_disablePortaDelegata(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_enablePortaApplicativa.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_enablePortaApplicativa(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_disablePortaApplicativa.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_disablePortaApplicativa(alias));
					this.jmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeRisorsaAccessoRegistroServizi(alias));
					this.jmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeRisorsaStatoServiziPdD(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegata.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegata(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataAbilitazioniPuntuali.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataAbilitazioniPuntuali(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataDisabilitazioniPuntuali.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaDelegataDisabilitazioniPuntuali(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativa.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativa(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaAbilitazioniPuntuali.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaAbilitazioniPuntuali(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaDisabilitazioniPuntuali.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioPortaApplicativaDisabilitazioniPuntuali(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_statoServizioIntegrationManager.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_statoServizioIntegrationManager(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaDelegata(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaDelegata(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioPortaApplicativa(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioPortaApplicativa(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_abilitaServizioIntegrationManager(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_disabilitaServizioIntegrationManager(alias));
					this.jmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeRisorsaDatasourceGW(alias));
					this.jmxPdD_configurazioneSistema_nomeAttributo_numeroDatasourceGW.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeAttributo_numeroDatasourceGW(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_getDatasourcesGW.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_getDatasourcesGW(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_getUsedConnectionsDatasourcesGW.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_getUsedConnectionsDatasourcesGW(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_getInformazioniDatabaseDatasourcesGW.put(alias,consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_getInformazioniDatabaseDatasourcesGW(alias));
					this.jmxPdD_configurazioneSistema_nomeRisorsaConsegnaContenutiApplicativi.put(alias, consoleProperties.getJmxPdD_configurazioneSistema_nomeRisorsaGestioneConsegnaApplicativi(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_getThreadPoolStatus.put(alias, consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_getThreadPoolStatus(alias));
					this.jmxPdD_configurazioneSistema_nomeRisorsaSystemPropertiesPdD.put(alias, consoleProperties.getJmxPdD_configurazioneSistema_nomeRisorsaSystemPropertiesPdD(alias));
					this.jmxPdD_configurazioneSistema_nomeMetodo_refreshPersistentConfiguration.put(alias, consoleProperties.getJmxPdD_configurazioneSistema_nomeMetodo_refreshPersistentConfiguration(alias));
					this.jmxPdD_caches.put(alias, consoleProperties.getJmxPdD_caches(alias));
					this.jmxPdD_caches_prefill.put(alias, consoleProperties.getJmxPdD_caches_prefill(alias));
					this.jmxPdD_cache_type.put(alias, consoleProperties.getJmxPdD_cache_type(alias));
					this.jmxPdD_cache_nomeAttributo_cacheAbilitata.put(alias, consoleProperties.getJmxPdD_cache_nomeAttributo_cacheAbilitata(alias));
					this.jmxPdD_cache_nomeMetodo_statoCache.put(alias, consoleProperties.getJmxPdD_cache_nomeMetodo_statoCache(alias));
					this.jmxPdD_cache_nomeMetodo_resetCache.put(alias, consoleProperties.getJmxPdD_cache_nomeMetodo_resetCache(alias));
					if(this.jmxPdD_caches_prefill.get(alias).size()>0){
						this.jmxPdD_cache_nomeMetodo_prefillCache.put(alias, consoleProperties.getJmxPdD_cache_nomeMetodo_prefillCache(alias));
					}
				}
			}

		} catch (java.lang.Exception e) {
			ControlStationCore.log.error("[ControlStationCore::initCoreJmxResources] Impossibile leggere i dati dal file console.properties:" + e.toString(),e);
			throw new ControlStationCoreException("[ControlStationCore::initCoreJmxResources] Impossibile leggere i dati dal file console.properties:" + e.toString(),e);
		}
	}
	

	/**
	 * Inizializza il driver di tracciamento.
	 * 
	 * @throws Exception
	 */
	private synchronized void initDriverTracciamento(String nomeDs, boolean forceChange) throws Exception {
		if (this.driverTracciamento == null || forceChange) {
			try {
				if (nomeDs == null || nomeDs.equals("") || nomeDs.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
					if(this.tracce_sameDBWebUI){
						this.driverTracciamento = new DriverTracciamento(ControlStationCore.dbM.getDataSourceName(),this.tipoDB,ControlStationCore.dbM.getDataSourceContext(),ControlStationCore.log);
					}
					else{
						this.driverTracciamento = new DriverTracciamento(this.tracce_datasource, this.tracce_tipoDatabase, this.tracce_ctxDatasource,ControlStationCore.log);
					}
				} else {

					Configurazione newConfigurazione = this.getConfigurazioneGenerale();
					Tracciamento t = newConfigurazione.getTracciamento();
					List<OpenspcoopSorgenteDati> lista = t.getOpenspcoopSorgenteDatiList();
					OpenspcoopSorgenteDati od = null;
					for (int j = 0; j < t.sizeOpenspcoopSorgenteDatiList(); j++) {
						od = lista.get(j);
						if (nomeDs == od.getNome()) {
							break;
						}
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
				ControlStationCore.log.error("[pdd] Inizializzazione DriverTracciamento non riuscita : " + e.getMessage(),e);
				throw new Exception("[pdd] Inizializzazione DriverTracciamento non riuscita : " + e.getMessage(),e);
			}

		}

	}

	/**
	 * Inizializza il driver della diagnostica.
	 * 
	 * @throws Exception
	 */
	private synchronized void initDriverMSGDiagnostici(String nomeDs, boolean forceChange) throws Exception {
		if (this.driverMSGDiagnostici == null || forceChange) {
			try {
				if (nomeDs == null || nomeDs.equals("") || nomeDs.equals(CostantiControlStation.DEFAULT_VALUE_NON_SELEZIONATO)) {
					if(this.msgDiagnostici_sameDBWebUI){
						this.driverMSGDiagnostici = new DriverMsgDiagnostici(ControlStationCore.dbM.getDataSourceName(),this.tipoDB,ControlStationCore.dbM.getDataSourceContext(),ControlStationCore.log); 
					}
					else{
						this.driverMSGDiagnostici = new DriverMsgDiagnostici(this.msgDiagnostici_datasource, this.msgDiagnostici_tipoDatabase, this.msgDiagnostici_ctxDatasource,ControlStationCore.log); 
					}
				} else {

					Configurazione newConfigurazione = this.getConfigurazioneGenerale();
					MessaggiDiagnostici md = newConfigurazione.getMessaggiDiagnostici();
					List<OpenspcoopSorgenteDati> lista = md.getOpenspcoopSorgenteDatiList();
					OpenspcoopSorgenteDati od = null;
					for (int j = 0; j < md.sizeOpenspcoopSorgenteDatiList(); j++) {
						od = lista.get(j);
						if (nomeDs == od.getNome()) {
							break;
						}
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
				ControlStationCore.log.error("[pdd] Inizializzazione DriverMSGDiagnostici non riuscita : " + e.getMessage(),e);
				throw new Exception("[pdd] Inizializzazione DriverMSGDiagnostici non riuscita : " + e.getMessage(),e);
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
		boolean doSetDati = false;
		List<OperazioneDaSmistare> operazioneDaSmistareList = null;
		OperazioneDaSmistare operazioneDaSmistare = null;

		try {

			operazioneDaSmistareList = new ArrayList<OperazioneDaSmistare>();
			con = ControlStationCore.dbM.getConnection();

			// le operazioni da eseguire devono essere transazionali quindi
			// disabilito l'autocommit
			// e lo riabilito solo dopo aver terminato le operazioni
			con.setAutoCommit(false);
			PdDControlStation pddGestore = null;
			int pddGestoreTipoOperazione = -1;

			// creo il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			SoggettiCore soggettiCore = new SoggettiCore(this);

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

						// Una volta creata la PdD avvio il thread di
						// gestione (a meno che non sia esterna)
						pddGestore = pdd;
						pddGestoreTipoOperazione = operationType;

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(pdd.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pdd);
						operazioneDaSmistare.addParameter(OperationsParameter.PORTA_DOMINIO, pdd.getNome());

						doSetDati = true;
					}

					if (oggetto instanceof MappingFruizionePortaDelegata) {
						MappingFruizionePortaDelegata mapping = (MappingFruizionePortaDelegata) oggetto;
						driver.createMappingFruizionePortaDelegata(mapping);
						
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(Integer.parseInt("" + mapping.getTableId()));
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.mappingFruizionePD);

						doSetDati = false;

					}
					
					if (oggetto instanceof MappingErogazionePortaApplicativa) {
						MappingErogazionePortaApplicativa mapping = (MappingErogazionePortaApplicativa) oggetto;
						driver.createMappingErogazionePortaApplicativa(mapping);
						
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(Integer.parseInt("" + mapping.getTableId()));
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.mappingErogazionePA);

						doSetDati = false;

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

						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(sogConf.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.soggetto);
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, soggetto.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, soggetto.getNome());

						// operazioneDaSmistare.setTipoSoggetto(soggetto.getTipo());
						// operazioneDaSmistare.setNomeSoggetto(soggetto.getNome());
						if(soggetto.getSoggettoReg()!=null){
							operazioneDaSmistare.setPdd(soggetto.getSoggettoReg().getPortaDominio());
						}

						doSetDati = true;

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
						

						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(sogConf.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.soggetto);
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, sogConf.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, sogConf.getNome());

						doSetDati = true;

					}
					
					// soggetto reg
					if (oggetto instanceof org.openspcoop2.core.registry.Soggetto) {
						
						org.openspcoop2.core.registry.Soggetto sogReg = (org.openspcoop2.core.registry.Soggetto) oggetto;
						if(this.registroServiziLocale){
							driver.getDriverRegistroServiziDB().createSoggetto(sogReg);
						}

						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(sogReg.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.soggetto);
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, sogReg.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, sogReg.getNome());

						// operazioneDaSmistare.setTipoSoggetto(soggetto.getTipo());
						// operazioneDaSmistare.setNomeSoggetto(soggetto.getNome());
						operazioneDaSmistare.setPdd(sogReg.getPortaDominio());
						
						doSetDati = true;

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

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(sa.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.servizioApplicativo);
						if(this.isRegistroServiziLocale()){
							org.openspcoop2.core.registry.Soggetto sog = driver.getDriverRegistroServiziDB().getSoggetto(sa.getIdSoggetto());
							operazioneDaSmistare.setPdd(sog.getPortaDominio());
						}
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SERVIZIO_APPLICATIVO, sa.getNome());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, sa.getTipoSoggettoProprietario());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, sa.getNomeSoggettoProprietario());
						// operazioneDaSmistare.setNomeServizioApplicativo(sa.getNome());

						doSetDati = true;
					}
					// PortaDelegata
					if (oggetto instanceof PortaDelegata) {
						PortaDelegata pd = (PortaDelegata) oggetto;
						if (pd.getIdSoggetto() == null || pd.getIdSoggetto() < 0) {
							pd.setIdSoggetto(DBUtils.getIdSoggetto(pd.getNomeSoggettoProprietario(), pd.getTipoSoggettoProprietario(), con, this.tipoDB, CostantiDB.SOGGETTI));
						}
						driver.getDriverConfigurazioneDB().createPortaDelegata(pd);
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(pd.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pd);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_PD, pd.getNome());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, pd.getTipoSoggettoProprietario());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, pd.getNomeSoggettoProprietario());
						if(this.isRegistroServiziLocale()){
							org.openspcoop2.core.registry.Soggetto sogg = driver.getDriverRegistroServiziDB().getSoggetto(pd.getIdSoggetto());
							String server = sogg.getPortaDominio();
							operazioneDaSmistare.setPdd(server);
						}
						doSetDati = true;
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
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(pa.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pa);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_PA, pa.getNome());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, pa.getTipoSoggettoProprietario());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, pa.getNomeSoggettoProprietario());
						if(this.isRegistroServiziLocale()){
							org.openspcoop2.core.registry.Soggetto sogg = driver.getDriverRegistroServiziDB().getSoggetto(pa.getIdSoggetto());
							String server = sogg.getPortaDominio();
							operazioneDaSmistare.setPdd(server);
						}
						doSetDati = true;
					}
					// RoutingTable
					if (oggetto instanceof RoutingTable) {
						RoutingTable rt = (RoutingTable) oggetto;
						driver.getDriverConfigurazioneDB().createRoutingTable(rt);
						doSetDati = false;
					}
					// GestioneErrore
					if (oggetto instanceof GestioneErrore) {
						GestioneErrore gestErrore = (GestioneErrore) oggetto;
						driver.getDriverConfigurazioneDB().createGestioneErroreComponenteCooperazione(gestErrore);

						doSetDati = false;
					}
					// Configurazione
					if (oggetto instanceof Configurazione) {
						Configurazione conf = (Configurazione) oggetto;
						driver.getDriverConfigurazioneDB().createConfigurazione(conf);

						doSetDati = false;
					}
					// ConfigurazioneAccessoRegistro
					if (oggetto instanceof AccessoRegistro) {
						AccessoRegistro cfgAccessoRegistro = (AccessoRegistro) oggetto;
						driver.getDriverConfigurazioneDB().createAccessoRegistro(cfgAccessoRegistro);

						doSetDati = false;
					}
					// ConfigurazioneAccessoRegistroRegistro
					if (oggetto instanceof AccessoRegistroRegistro) {
						AccessoRegistroRegistro carr = (AccessoRegistroRegistro) oggetto;
						driver.getDriverConfigurazioneDB().createAccessoRegistro(carr);

						doSetDati = false;
					}
					// ConfigurazioneAccessoConfigurazione
					if (oggetto instanceof AccessoConfigurazione) {
						AccessoConfigurazione accesso = (AccessoConfigurazione) oggetto;
						driver.getDriverConfigurazioneDB().createAccessoConfigurazione(accesso);

						doSetDati = false;
					}
					// ConfigurazioneAccessoDatiAutorizzazione
					if (oggetto instanceof AccessoDatiAutorizzazione) {
						AccessoDatiAutorizzazione accesso = (AccessoDatiAutorizzazione) oggetto;
						driver.getDriverConfigurazioneDB().createAccessoDatiAutorizzazione(accesso);

						doSetDati = false;
					}
					// ConfigurazioneSystemProperty
					if (oggetto instanceof SystemProperties) {
						SystemProperties sps = (SystemProperties) oggetto;
						driver.getDriverConfigurazioneDB().createSystemPropertiesPdD(sps);

						doSetDati = false;
					}

					/***********************************************************
					 * Operazioni su Registro *
					 **********************************************************/
					// PortaDominio
					if (oggetto instanceof PortaDominio && !(oggetto instanceof PdDControlStation)) {
						PortaDominio pdd = (PortaDominio) oggetto;
						driver.getDriverRegistroServiziDB().createPortaDominio(pdd);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(pdd.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pdd);
						operazioneDaSmistare.addParameter(OperationsParameter.PORTA_DOMINIO, pdd.getNome());

						doSetDati = true;
					}
					
					// Gruppo
					if (oggetto instanceof Gruppo) {
						Gruppo gruppo = (Gruppo) oggetto;
						driver.getDriverRegistroServiziDB().createGruppo(gruppo);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(gruppo.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.gruppo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_GRUPPO, gruppo.getNome());

						doSetDati = true;
					}
					
					// Ruolo
					if (oggetto instanceof Ruolo) {
						Ruolo ruolo = (Ruolo) oggetto;
						driver.getDriverRegistroServiziDB().createRuolo(ruolo);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(ruolo.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.ruolo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_RUOLO, ruolo.getNome());

						doSetDati = true;
					}
					
					// Scope
					if (oggetto instanceof Scope) {
						Scope scope = (Scope) oggetto;
						driver.getDriverRegistroServiziDB().createScope(scope);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(scope.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.scope);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SCOPE, scope.getNome());

						doSetDati = true;
					}

					// AccordoServizio
					if (oggetto instanceof AccordoServizioParteComune) {
						AccordoServizioParteComune as = (AccordoServizioParteComune) oggetto;
						driver.getDriverRegistroServiziDB().createAccordoServizioParteComune(as);

						// voglio 2 operazioni in coda, 1 viene aggiunta
						// alla
						// fine dello switch e
						// l altra viene aggiunta subito

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(as.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, as.getNome());
						if(as.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, as.getVersione().intValue()+"");
						}
						if(as.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, as.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, as.getSoggettoReferente().getNome());
						}
						// inserisco in lista l'operazione
						operazioneDaSmistareList.add(operazioneDaSmistare);

						// OPERAZIONI PER RepositoryAutorizzazioni
						// preparo operazione da smistare
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(as.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordoRuolo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, as.getNome());
						if(as.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, as.getVersione().intValue()+"");
						}
						if(as.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, as.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, as.getSoggettoReferente().getNome());
						}
						// inserisco in lista l'operazione
						operazioneDaSmistareList.add(operazioneDaSmistare);

						// preparo l'altra operazione da aggiungere
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(as.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordoRuolo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, as.getNome() + "Correlato");
						if(as.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, as.getVersione().intValue()+"");
						}
						if(as.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, as.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, as.getSoggettoReferente().getNome());
						}
						doSetDati = true;

					}

					// AccordoCooperazione
					if (oggetto instanceof AccordoCooperazione) {
						AccordoCooperazione ac = (AccordoCooperazione) oggetto;
						driver.getDriverRegistroServiziDB().createAccordoCooperazione(ac);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(ac.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordoCooperazione);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, ac.getNome());
						if(ac.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, ac.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, ac.getSoggettoReferente().getNome());
						}
						if(ac.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, ac.getVersione().intValue()+"");
						}

						doSetDati = true;
					}

					// Servizio
					// Servizio Correlato
					if (oggetto instanceof AccordoServizioParteSpecifica) {
						AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) oggetto;

						driver.getDriverRegistroServiziDB().createAccordoServizioParteSpecifica(asps);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.add);
						operazioneDaSmistare.setIDTable(asps.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.servizio);
						
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, asps.getTipoSoggettoErogatore());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, asps.getNomeSoggettoErogatore());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SERVIZIO, asps.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SERVIZIO, asps.getNome());
						if(asps.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, asps.getVersione().intValue()+"");
						}

						doSetDati = true;
					}

					/***********************************************************
					 * Operazioni su Users *
					 **********************************************************/
					// User
					if (oggetto instanceof User) {
						User user = (User) oggetto;
						driver.getDriverUsersDB().createUser(user);
						doSetDati = false;
					}

					/***********************************************************
					 * Operazioni su Audit *
					 **********************************************************/
					// Filtro
					if (oggetto instanceof Filtro) {
						Filtro filtro = (Filtro) oggetto;
						driver.getDriverAuditDB().createFiltro(filtro);
						doSetDati = false;
					}
					
					/***********************************************************
					 * Operazioni su Controllo Traffico *
					 **********************************************************/
					// Configurazione Policy
					if(oggetto instanceof ConfigurazionePolicy) {
						ConfigurazionePolicy policy = (ConfigurazionePolicy) oggetto;
						driver.createConfigurazionePolicy(policy);
						doSetDati = false;
					}
					
					// Attivazione Policy
					if(oggetto instanceof AttivazionePolicy) {
						AttivazionePolicy policy = (AttivazionePolicy) oggetto;
						driver.createAttivazionePolicy(policy);
						doSetDati = false;
					}
					
					/***********************************************************
					 * Operazioni su Generic Properties *
					 **********************************************************/
					// Generic Propertie
					if(oggetto instanceof GenericProperties) {
						GenericProperties genericProperties = (GenericProperties) oggetto;
						driver.getDriverConfigurazioneDB().createGenericProperties(genericProperties);
						doSetDati = false;
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

						// Una volta modificata la PdD avvio il thread di
						// gestione (a meno che non sia esterna)
						pddGestore = pdd;
						pddGestoreTipoOperazione = operationType;
												
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(pdd.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pdd);
						operazioneDaSmistare.addParameter(OperationsParameter.PORTA_DOMINIO, pdd.getNome());
						doSetDati = true;
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

						// driver.getDriverConfigurazioneDB().updateSoggetto(soggetto.getSoggettoConf());

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

						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(soggetto.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.soggetto);
						if(this.registroServiziLocale){
							operazioneDaSmistare.setPdd(soggetto.getSoggettoReg().getPortaDominio());
						}

						operazioneDaSmistare.addParameter(OperationsParameter.OLD_TIPO_SOGGETTO, soggetto.getOldTipoForUpdate());
						operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_SOGGETTO, soggetto.getOldNomeForUpdate());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, soggetto.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, soggetto.getNome());

						// operazioneDaSmistare.setOldNomeSoggetto(soggetto.getSoggettoConf().getOldNomeForUpdate());
						// operazioneDaSmistare.setOldTipoSoggetto(soggetto.getSoggettoConf().getOldTipoForUpdate());
						// operazioneDaSmistare.setTipoSoggetto(soggetto.getTipo());
						// operazioneDaSmistare.setNomeSoggetto(soggetto.getNome());

						doSetDati = true;
					}
					
					
					// soggetto config
					if (oggetto instanceof org.openspcoop2.core.config.Soggetto) {
						
						org.openspcoop2.core.config.Soggetto sogConf = (org.openspcoop2.core.config.Soggetto) oggetto;
						driver.getDriverConfigurazioneDB().updateSoggetto(sogConf);

						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(sogConf.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.soggetto);
						if(sogConf.getOldIDSoggettoForUpdate()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_TIPO_SOGGETTO, sogConf.getOldIDSoggettoForUpdate().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_SOGGETTO, sogConf.getOldIDSoggettoForUpdate().getNome());
						}
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, sogConf.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, sogConf.getNome());

						doSetDati = true;

					}
					
					// soggetto reg
					if (oggetto instanceof org.openspcoop2.core.registry.Soggetto) {
						
						org.openspcoop2.core.registry.Soggetto sogReg = (org.openspcoop2.core.registry.Soggetto) oggetto;

						if(this.registroServiziLocale){
							driver.getDriverRegistroServiziDB().updateSoggetto(sogReg);
						}

						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(sogReg.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.soggetto);
						if(sogReg.getOldIDSoggettoForUpdate()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_TIPO_SOGGETTO, sogReg.getOldIDSoggettoForUpdate().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_SOGGETTO, sogReg.getOldIDSoggettoForUpdate().getNome());
						}
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, sogReg.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, sogReg.getNome());

						// operazioneDaSmistare.setTipoSoggetto(soggetto.getTipo());
						// operazioneDaSmistare.setNomeSoggetto(soggetto.getNome());
						operazioneDaSmistare.setPdd(sogReg.getPortaDominio());
						
						doSetDati = true;

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

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(sa.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.servizioApplicativo);
						
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SERVIZIO_APPLICATIVO, sa.getNome());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, sa.getTipoSoggettoProprietario());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, sa.getNomeSoggettoProprietario());
						
						if(sa.getOldIDServizioApplicativoForUpdate()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_SERVIZIO_APPLICATIVO, sa.getOldIDServizioApplicativoForUpdate().getNome());
							if(sa.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario()!=null){
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_TIPO_SOGGETTO, sa.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getTipo());
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_SOGGETTO, sa.getOldIDServizioApplicativoForUpdate().getIdSoggettoProprietario().getNome());
							}
						}

						if(this.isRegistroServiziLocale()){
							org.openspcoop2.core.registry.Soggetto sogg = driver.getDriverRegistroServiziDB().getSoggetto(sa.getIdSoggetto());
							operazioneDaSmistare.setPdd(sogg.getPortaDominio());
						}

						doSetDati = true;
					}
					// PortaDelegata
					if (oggetto instanceof PortaDelegata) {
						PortaDelegata pd = (PortaDelegata) oggetto;
						driver.getDriverConfigurazioneDB().updatePortaDelegata(pd);
						if (pd.getIdSoggetto() == null || pd.getIdSoggetto() < 0) {
							pd.setIdSoggetto(DBUtils.getIdSoggetto(pd.getNomeSoggettoProprietario(), pd.getTipoSoggettoProprietario(), con, this.tipoDB, CostantiDB.SOGGETTI));
						}
						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(pd.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pd);
						
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_PD, pd.getNome());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, pd.getNomeSoggettoProprietario());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, pd.getTipoSoggettoProprietario());
						
						if(pd.getOldIDPortaDelegataForUpdate()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_PD, pd.getOldIDPortaDelegataForUpdate().getNome());
						}
						
						if(this.isRegistroServiziLocale()){
							org.openspcoop2.core.registry.Soggetto sogg = driver.getDriverRegistroServiziDB().getSoggetto(pd.getIdSoggetto());
							String server = sogg.getPortaDominio();
							operazioneDaSmistare.setPdd(server);
						}
						doSetDati = true;
					}
					// PortaApplivativa
					if (oggetto instanceof PortaApplicativa) {
						PortaApplicativa pa = (PortaApplicativa) oggetto;
						driver.getDriverConfigurazioneDB().updatePortaApplicativa(pa);
						if (pa.getIdSoggetto() == null || pa.getIdSoggetto() < 0) {
							pa.setIdSoggetto(DBUtils.getIdSoggetto(pa.getNomeSoggettoProprietario(), pa.getTipoSoggettoProprietario(), con, this.tipoDB, CostantiDB.SOGGETTI));
						}
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(pa.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pa);
						
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_PA, pa.getNome());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, pa.getNomeSoggettoProprietario());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, pa.getTipoSoggettoProprietario());
						
						if(pa.getOldIDPortaApplicativaForUpdate()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_PA, pa.getOldIDPortaApplicativaForUpdate().getNome());
						}
						
						if(this.isRegistroServiziLocale()){
							org.openspcoop2.core.registry.Soggetto sogg = driver.getDriverRegistroServiziDB().getSoggetto(pa.getIdSoggetto());
							String server = sogg.getPortaDominio();
							operazioneDaSmistare.setPdd(server);
						}

						doSetDati = true;
					}
					// RoutingTable
					if (oggetto instanceof RoutingTable) {
						RoutingTable rt = (RoutingTable) oggetto;
						driver.getDriverConfigurazioneDB().updateRoutingTable(rt);
						doSetDati = false;
					}
					// GestioneErrore
					if (oggetto instanceof GestioneErrore) {
						GestioneErrore gestErrore = (GestioneErrore) oggetto;
						driver.getDriverConfigurazioneDB().updateGestioneErroreComponenteCooperazione(gestErrore);
						doSetDati = false;
					}
					// Configurazione
					if (oggetto instanceof Configurazione) {
						Configurazione conf = (Configurazione) oggetto;
						driver.getDriverConfigurazioneDB().updateConfigurazione(conf);
						doSetDati = false;
					}
					// ConfigurazioneAccessoRegistro
					if (oggetto instanceof AccessoRegistro) {
						AccessoRegistro cfgAccessoRegistro = (AccessoRegistro) oggetto;
						driver.getDriverConfigurazioneDB().updateAccessoRegistro(cfgAccessoRegistro);
						doSetDati = false;
					}
					// ConfigurazioneAccessoRegistroRegistro
					if (oggetto instanceof AccessoRegistroRegistro) {
						AccessoRegistroRegistro carr = (AccessoRegistroRegistro) oggetto;
						driver.getDriverConfigurazioneDB().updateAccessoRegistro(carr);
						doSetDati = false;
					}
					// ConfigurazioneAccessoConfigurazione
					if (oggetto instanceof AccessoConfigurazione) {
						AccessoConfigurazione accesso = (AccessoConfigurazione) oggetto;
						driver.getDriverConfigurazioneDB().updateAccessoConfigurazione(accesso);
						doSetDati = false;
					}
					// ConfigurazioneAccessoDatiAutorizzazione
					if (oggetto instanceof AccessoDatiAutorizzazione) {
						AccessoDatiAutorizzazione accesso = (AccessoDatiAutorizzazione) oggetto;
						driver.getDriverConfigurazioneDB().updateAccessoDatiAutorizzazione(accesso);
						doSetDati = false;
					}
					// SystemProperties
					if (oggetto instanceof SystemProperties) {
						SystemProperties sps = (SystemProperties) oggetto;
						driver.getDriverConfigurazioneDB().updateSystemPropertiesPdD(sps);
						doSetDati = false;
					}

					/***********************************************************
					 * Operazioni su Registro *
					 **********************************************************/
					// PortaDominio
					if (oggetto instanceof PortaDominio && !(oggetto instanceof PdDControlStation)) {
						PortaDominio pdd = (PortaDominio) oggetto;
						driver.getDriverRegistroServiziDB().updatePortaDominio(pdd);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(pdd.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pdd);
						operazioneDaSmistare.addParameter(OperationsParameter.PORTA_DOMINIO, pdd.getNome());

						doSetDati = true;
					}
					
					// Gruppo
					if (oggetto instanceof Gruppo) {
						Gruppo gruppo = (Gruppo) oggetto;
						driver.getDriverRegistroServiziDB().updateGruppo(gruppo);
						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(gruppo.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.gruppo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_GRUPPO, gruppo.getNome());
						IDGruppo idGruppoOLD = gruppo.getOldIDGruppoForUpdate();
						if(idGruppoOLD!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_GRUPPO, idGruppoOLD.getNome());
						}
						doSetDati = true;
					}

					// Ruolo
					if (oggetto instanceof Ruolo) {
						Ruolo ruolo = (Ruolo) oggetto;
						driver.getDriverRegistroServiziDB().updateRuolo(ruolo);
						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(ruolo.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.ruolo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_RUOLO, ruolo.getNome());
						IDRuolo idRuoloOLD = ruolo.getOldIDRuoloForUpdate();
						if(idRuoloOLD!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_RUOLO, idRuoloOLD.getNome());
						}
						doSetDati = true;
					}
					
					// Scope
					if (oggetto instanceof Scope) {
						Scope scope = (Scope) oggetto;
						driver.getDriverRegistroServiziDB().updateScope(scope);
						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(scope.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.scope);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SCOPE, scope.getNome());
						IDScope idScopeOLD = scope.getOldIDScopeForUpdate();
						if(idScopeOLD!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_SCOPE, idScopeOLD.getNome());
						}
						doSetDati = true;
					}
					
					// AccordoServizio
					if (oggetto instanceof AccordoServizioParteComune) {
						AccordoServizioParteComune as = (AccordoServizioParteComune) oggetto;
						driver.getDriverRegistroServiziDB().updateAccordoServizioParteComune(as);
						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(as.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, as.getNome());
						if(as.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, as.getVersione().intValue()+"");
						}
						if(as.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, as.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, as.getSoggettoReferente().getNome());
						}
						IDAccordo idAccordoOLD = as.getOldIDAccordoForUpdate();
						if(idAccordoOLD!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_ACCORDO, idAccordoOLD.getNome());
							if(idAccordoOLD.getVersione()!=null){
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_VERSIONE_ACCORDO, idAccordoOLD.getVersione().intValue()+"");
							}
							if(idAccordoOLD.getSoggettoReferente()!=null){
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_TIPO_REFERENTE, idAccordoOLD.getSoggettoReferente().getTipo());
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_REFERENTE, idAccordoOLD.getSoggettoReferente().getNome());
							}else{
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_TIPO_REFERENTE, null);
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_REFERENTE, null);
							}
						}
						doSetDati = true;
					}

					// AccordoCooperazione
					if (oggetto instanceof AccordoCooperazione) {
						AccordoCooperazione ac = (AccordoCooperazione) oggetto;
						driver.getDriverRegistroServiziDB().updateAccordoCooperazione(ac);

						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(ac.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordoCooperazione);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, ac.getNome());
						if(ac.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, ac.getVersione().intValue()+"");
						}
						if(ac.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, ac.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, ac.getSoggettoReferente().getNome());
						}

						IDAccordoCooperazione idAccordoOLD = ac.getOldIDAccordoForUpdate();
						if(idAccordoOLD!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_ACCORDO, idAccordoOLD.getNome());
							if(idAccordoOLD.getVersione()!=null){
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_VERSIONE_ACCORDO, idAccordoOLD.getVersione().intValue()+"");
							}
							if(idAccordoOLD.getSoggettoReferente()!=null){
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_TIPO_REFERENTE, idAccordoOLD.getSoggettoReferente().getTipo());
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_REFERENTE, idAccordoOLD.getSoggettoReferente().getNome());
							}else{
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_TIPO_REFERENTE, null);
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_REFERENTE, null);
							}
						}

						doSetDati = true;
					}

					// Servizio
					// Servizio Correlato
					if (oggetto instanceof AccordoServizioParteSpecifica) {
						AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) oggetto;

						driver.getDriverRegistroServiziDB().updateAccordoServizioParteSpecifica(asps);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(asps.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.servizio);
						
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, asps.getTipoSoggettoErogatore());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, asps.getNomeSoggettoErogatore());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SERVIZIO, asps.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SERVIZIO, asps.getNome());
						if(asps.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, asps.getVersione().intValue()+"");
						}
						
						if(asps.getOldIDServizioForUpdate()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_TIPO_SERVIZIO, asps.getOldIDServizioForUpdate().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_SERVIZIO, asps.getOldIDServizioForUpdate().getNome());
							if(asps.getOldIDServizioForUpdate().getSoggettoErogatore()!=null){
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_TIPO_SOGGETTO, asps.getOldIDServizioForUpdate().getSoggettoErogatore().getTipo());
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_NOME_SOGGETTO, asps.getOldIDServizioForUpdate().getSoggettoErogatore().getNome());
							}
							if(asps.getOldIDServizioForUpdate().getVersione()!=null){
								operazioneDaSmistare.addParameter(OperationsParameter.OLD_VERSIONE_ACCORDO, asps.getOldIDServizioForUpdate().getVersione().intValue()+"");
							}
						}

						doSetDati = true;

					}

					// PortType
					if (oggetto instanceof PortType) {
						PortType pt = (PortType) oggetto;
						driver.getDriverRegistroServiziDB().updatePortType(pt);

						// Chiedo la setDati per l'accordo servizio
						long idAcc = pt.getIdAccordo();
						AccordoServizioParteComune as = driver.getDriverRegistroServiziDB().getAccordoServizioParteComune(idAcc);
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.change);
						operazioneDaSmistare.setIDTable(pt.getIdAccordo().intValue());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, as.getNome());
						if(as.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, as.getVersione().intValue()+"");
						}
						if(as.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, as.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, as.getSoggettoReferente().getNome());
						}

						doSetDati = true;
					}

					/***********************************************************
					 * Operazioni su Users *
					 **********************************************************/
					// User
					if (oggetto instanceof User) {
						User user = (User) oggetto;
						driver.getDriverUsersDB().updateUser(user);
						doSetDati = false;
					}

					/***********************************************************
					 * Operazioni su Audit *
					 **********************************************************/
					// Configurazione
					if (oggetto instanceof org.openspcoop2.web.lib.audit.dao.Configurazione) {
						org.openspcoop2.web.lib.audit.dao.Configurazione confAudit = 
								(org.openspcoop2.web.lib.audit.dao.Configurazione) oggetto;
						driver.getDriverAuditDB().updateConfigurazione(confAudit);
						doSetDati = false;
					}

					// Filtro
					if (oggetto instanceof Filtro) {
						Filtro filtro = (Filtro) oggetto;
						driver.getDriverAuditDB().updateFiltro(filtro);
						doSetDati = false;
					}
					
					/***********************************************************
					 * Operazioni su Controllo Traffico *
					 **********************************************************/

					// Configurazione Controllo Traffico 
					if(oggetto instanceof ConfigurazioneGenerale) {
						ConfigurazioneGenerale configurazioneControlloTraffico = (ConfigurazioneGenerale) oggetto;
						driver.updateConfigurazioneControlloTraffico(configurazioneControlloTraffico);
						doSetDati = false;
					}

					// Configurazione Policy
					if(oggetto instanceof ConfigurazionePolicy) {
						ConfigurazionePolicy policy = (ConfigurazionePolicy) oggetto;
						driver.updateConfigurazionePolicy(policy);
						doSetDati = false;
					}
					
					// Attivazione Policy
					if(oggetto instanceof AttivazionePolicy) {
						AttivazionePolicy policy = (AttivazionePolicy) oggetto;
						driver.updateAttivazionePolicy(policy);
						doSetDati = false;
					}
					
					/***********************************************************
					 * Operazioni su Generic Properties *
					 **********************************************************/
					// Generic Propertie
					if(oggetto instanceof GenericProperties) {
						GenericProperties genericProperties = (GenericProperties) oggetto;
						driver.getDriverConfigurazioneDB().updateGenericProperties(genericProperties);
						doSetDati = false;
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

						// Una volta modificata la PdD avvio il thread di
						// gestione (a meno che non sia esterna)
						pddGestore = pdd;
						pddGestoreTipoOperazione = operationType;
						
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(pdd.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pdd);
						operazioneDaSmistare.addParameter(OperationsParameter.PORTA_DOMINIO, pdd.getNome());

						doSetDati = true;
					}

					if (oggetto instanceof MappingFruizionePortaDelegata) {
						MappingFruizionePortaDelegata mapping = (MappingFruizionePortaDelegata) oggetto;
						driver.deleteMappingFruizionePortaDelegata(mapping);
						
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						//operazioneDaSmistare.setIDTable(Integer.parseInt("" + mapping.getTableId()));
						operazioneDaSmistare.addParameter(OperationsParameter.MAPPING_ID_FRUITORE, "" + driver.getTableIdSoggetto(mapping.getIdFruitore()));
						operazioneDaSmistare.addParameter(OperationsParameter.MAPPING_ID_SERVIZIO, "" + driver.getTableIdAccordoServizioParteSpecifica(mapping.getIdServizio()));
						operazioneDaSmistare.addParameter(OperationsParameter.MAPPING_ID_PORTA_DELEGATA, "" + driver.getTableIdPortaDelegata(mapping.getIdPortaDelegata()));
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.mappingFruizionePD);

						doSetDati = false;

					}
					
					if (oggetto instanceof MappingErogazionePortaApplicativa) {
						MappingErogazionePortaApplicativa mapping = (MappingErogazionePortaApplicativa) oggetto;
						driver.deleteMappingErogazionePortaApplicativa(mapping);
						
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						//operazioneDaSmistare.setIDTable(Integer.parseInt("" + mapping.getTableId()));
						operazioneDaSmistare.addParameter(OperationsParameter.MAPPING_ID_SERVIZIO, "" + driver.getTableIdAccordoServizioParteSpecifica(mapping.getIdServizio()));
						operazioneDaSmistare.addParameter(OperationsParameter.MAPPING_ID_PORTA_APPLICATIVA, "" + driver.getTableIdPortaApplicativa(mapping.getIdPortaApplicativa()));
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.mappingErogazionePA);

						doSetDati = false;

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

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(soggetto.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.soggetto);
						if(soggetto.getSoggettoReg()!=null){
							operazioneDaSmistare.setPdd(soggetto.getSoggettoReg().getPortaDominio());
						}

						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, soggetto.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, soggetto.getNome());

						doSetDati = true;
					}
					
					// soggetto config
					if (oggetto instanceof org.openspcoop2.core.config.Soggetto) {
						
						org.openspcoop2.core.config.Soggetto sogConf = (org.openspcoop2.core.config.Soggetto) oggetto;
						driver.getDriverConfigurazioneDB().deleteSoggetto(sogConf);

						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(sogConf.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.soggetto);
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, sogConf.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, sogConf.getNome());

						doSetDati = true;

					}
					
					// soggetto reg
					if (oggetto instanceof org.openspcoop2.core.registry.Soggetto) {
						
						org.openspcoop2.core.registry.Soggetto sogReg = (org.openspcoop2.core.registry.Soggetto) oggetto;

						if(this.registroServiziLocale){
							driver.getDriverRegistroServiziDB().deleteSoggetto(sogReg);
						}

						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(sogReg.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.soggetto);
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, sogReg.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, sogReg.getNome());

						// operazioneDaSmistare.setTipoSoggetto(soggetto.getTipo());
						// operazioneDaSmistare.setNomeSoggetto(soggetto.getNome());
						operazioneDaSmistare.setPdd(sogReg.getPortaDominio());
						
						doSetDati = true;

					}
					
					
					
					/***********************************************************
					 * Operazioni su ConfigurazioneDB *
					 **********************************************************/

					// ServizioApplicativo
					if (oggetto instanceof ServizioApplicativo) {
						ServizioApplicativo sa = (ServizioApplicativo) oggetto;
						driver.getDriverConfigurazioneDB().deleteServizioApplicativo(sa);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(sa.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.servizioApplicativo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SERVIZIO_APPLICATIVO, sa.getNome());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, sa.getNomeSoggettoProprietario());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, sa.getTipoSoggettoProprietario());
						if(this.isRegistroServiziLocale()){
							org.openspcoop2.core.registry.Soggetto sogg = null;
							if(sa.getIdSoggetto()!=null && sa.getIdSoggetto()>0){
								sogg = driver.getDriverRegistroServiziDB().getSoggetto(sa.getIdSoggetto());
							}
							else{
								sogg = driver.getDriverRegistroServiziDB().getSoggetto(new IDSoggetto(sa.getTipoSoggettoProprietario(), sa.getNomeSoggettoProprietario()));
							}	
							operazioneDaSmistare.setPdd(sogg.getPortaDominio());
						}

						doSetDati = true;
					}
					// PortaDelegata
					if (oggetto instanceof PortaDelegata) {
						PortaDelegata pd = (PortaDelegata) oggetto;
						driver.getDriverConfigurazioneDB().deletePortaDelegata(pd);
						// Chiedo la setDati
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(pd.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pd);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_PD, pd.getNome());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, pd.getNomeSoggettoProprietario());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, pd.getTipoSoggettoProprietario());
						if(this.isRegistroServiziLocale()){
							org.openspcoop2.core.registry.Soggetto sogg = soggettiCore.getSoggettoRegistro(new IDSoggetto(pd.getTipoSoggettoProprietario(), pd.getNomeSoggettoProprietario()));
							String server = sogg.getPortaDominio();
							operazioneDaSmistare.setPdd(server);
						}
						doSetDati = true;
					}
					// PortaApplivativa
					if (oggetto instanceof PortaApplicativa) {
						PortaApplicativa pa = (PortaApplicativa) oggetto;
						driver.getDriverConfigurazioneDB().deletePortaApplicativa(pa);
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(pa.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pa);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_PA, pa.getNome());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, pa.getNomeSoggettoProprietario());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, pa.getTipoSoggettoProprietario());
						if(this.isRegistroServiziLocale()){
							org.openspcoop2.core.registry.Soggetto sogg = soggettiCore.getSoggettoRegistro(new IDSoggetto(pa.getTipoSoggettoProprietario(), pa.getNomeSoggettoProprietario()));
							String server = sogg.getPortaDominio();
							operazioneDaSmistare.setPdd(server);
						}

						doSetDati = true;
					}
					// RoutingTable
					if (oggetto instanceof RoutingTable) {
						RoutingTable rt = (RoutingTable) oggetto;
						driver.getDriverConfigurazioneDB().deleteRoutingTable(rt);
						doSetDati = false;
					}
					// GestioneErrore
					if (oggetto instanceof GestioneErrore) {
						GestioneErrore gestErrore = (GestioneErrore) oggetto;
						driver.getDriverConfigurazioneDB().deleteGestioneErroreComponenteCooperazione(gestErrore);
						doSetDati = false;
					}
					// Configurazione
					if (oggetto instanceof Configurazione) {
						Configurazione conf = (Configurazione) oggetto;
						driver.getDriverConfigurazioneDB().deleteConfigurazione(conf);
						doSetDati = false;
					}
					// ConfigurazioneAccessoRegistro
					if (oggetto instanceof AccessoRegistro) {
						AccessoRegistro cfgAccessoRegistro = (AccessoRegistro) oggetto;
						driver.getDriverConfigurazioneDB().deleteAccessoRegistro(cfgAccessoRegistro);
						doSetDati = false;
					}
					// ConfigurazioneAccessoRegistroRegistro
					if (oggetto instanceof AccessoRegistroRegistro) {
						AccessoRegistroRegistro carr = (AccessoRegistroRegistro) oggetto;
						driver.getDriverConfigurazioneDB().deleteAccessoRegistro(carr);
						doSetDati = false;
					}
					// ConfigurazioneAccessoConfigurazione
					if (oggetto instanceof AccessoConfigurazione) {
						AccessoConfigurazione accesso = (AccessoConfigurazione) oggetto;
						driver.getDriverConfigurazioneDB().deleteAccessoConfigurazione(accesso);
						doSetDati = false;
					}
					// ConfigurazioneAccessoDatiAutorizzazione
					if (oggetto instanceof AccessoDatiAutorizzazione) {
						AccessoDatiAutorizzazione accesso = (AccessoDatiAutorizzazione) oggetto;
						driver.getDriverConfigurazioneDB().deleteAccessoDatiAutorizzazione(accesso);
						doSetDati = false;
					}
					// SystemProperties
					if (oggetto instanceof SystemProperties) {
						SystemProperties sps = (SystemProperties) oggetto;
						driver.getDriverConfigurazioneDB().deleteSystemPropertiesPdD(sps);
						doSetDati = false;
					}

					/***********************************************************
					 * Operazioni su Registro *
					 **********************************************************/
					// PortaDominio
					if (oggetto instanceof PortaDominio && !(oggetto instanceof PdDControlStation)) {
						PortaDominio pdd = (PortaDominio) oggetto;
						driver.getDriverRegistroServiziDB().deletePortaDominio(pdd);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(pdd.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.pdd);
						operazioneDaSmistare.addParameter(OperationsParameter.PORTA_DOMINIO, pdd.getNome());

						doSetDati = true;
					}
					
					// Ruolo
					if (oggetto instanceof Gruppo) {
						Gruppo gruppo = (Gruppo) oggetto;
						driver.getDriverRegistroServiziDB().deleteGruppo(gruppo);
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(gruppo.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.gruppo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_GRUPPO, gruppo.getNome());
						operazioneDaSmistareList.add(operazioneDaSmistare);
					}

					// Ruolo
					if (oggetto instanceof Ruolo) {
						Ruolo ruolo = (Ruolo) oggetto;
						driver.getDriverRegistroServiziDB().deleteRuolo(ruolo);
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(ruolo.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.ruolo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_RUOLO, ruolo.getNome());
						operazioneDaSmistareList.add(operazioneDaSmistare);
					}
					
					// Scope
					if (oggetto instanceof Scope) {
						Scope scope = (Scope) oggetto;
						driver.getDriverRegistroServiziDB().deleteScope(scope);
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(scope.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.scope);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SCOPE, scope.getNome());
						operazioneDaSmistareList.add(operazioneDaSmistare);
					}
					
					// AccordoServizio
					if (oggetto instanceof AccordoServizioParteComune) {
						AccordoServizioParteComune as = (AccordoServizioParteComune) oggetto;
						driver.getDriverRegistroServiziDB().deleteAccordoServizioParteComune(as);
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(as.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, as.getNome());
						if(as.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, as.getVersione().intValue()+"");
						}
						if(as.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, as.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, as.getSoggettoReferente().getNome());
						}
						operazioneDaSmistareList.add(operazioneDaSmistare);

						// OPERAZIONI PER RepositoryAutorizzazioni
						// preparo operazione da smistare
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(as.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordoRuolo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, as.getNome());
						if(as.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, as.getVersione().intValue()+"");
						}
						if(as.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, as.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, as.getSoggettoReferente().getNome());
						}
						// inserisco in lista l'operazione
						operazioneDaSmistareList.add(operazioneDaSmistare);

						// preparo l'altra operazione da aggiungere
						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(as.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordoRuolo);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, as.getNome() + "Correlato");
						if(as.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, as.getVersione().intValue()+"");
						}
						if(as.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, as.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, as.getSoggettoReferente().getNome());
						}

						doSetDati = true;
					}

					// AccordoCooperazione
					if (oggetto instanceof AccordoCooperazione) {
						AccordoCooperazione ac = (AccordoCooperazione) oggetto;
						driver.getDriverRegistroServiziDB().deleteAccordoCooperazione(ac);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(ac.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.accordoCooperazione);
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_ACCORDO, ac.getNome());
						if(ac.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, ac.getVersione().intValue()+"");
						}
						if(ac.getSoggettoReferente()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.TIPO_REFERENTE, ac.getSoggettoReferente().getTipo());
							operazioneDaSmistare.addParameter(OperationsParameter.NOME_REFERENTE, ac.getSoggettoReferente().getNome());
						}

						doSetDati = true;
					}

					// Servizio
					// Servizio Correlato
					if (oggetto instanceof AccordoServizioParteSpecifica) {
						AccordoServizioParteSpecifica asps = (AccordoServizioParteSpecifica) oggetto;
						
						driver.getDriverRegistroServiziDB().deleteAccordoServizioParteSpecifica(asps);

						operazioneDaSmistare = new OperazioneDaSmistare();
						operazioneDaSmistare.setOperazione(Operazione.del);
						operazioneDaSmistare.setIDTable(asps.getId());
						operazioneDaSmistare.setSuperuser(superUser);
						operazioneDaSmistare.setOggetto(TipoOggettoDaSmistare.servizio);
						
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SOGGETTO, asps.getTipoSoggettoErogatore());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SOGGETTO, asps.getNomeSoggettoErogatore());
						operazioneDaSmistare.addParameter(OperationsParameter.TIPO_SERVIZIO, asps.getTipo());
						operazioneDaSmistare.addParameter(OperationsParameter.NOME_SERVIZIO, asps.getNome());
						if(asps.getVersione()!=null){
							operazioneDaSmistare.addParameter(OperationsParameter.VERSIONE_ACCORDO, asps.getVersione().intValue()+"");
						}

						doSetDati = true;

					}

					/***********************************************************
					 * Operazioni su Users *
					 **********************************************************/
					// User
					if (oggetto instanceof User) {
						User user = (User) oggetto;
						driver.getDriverUsersDB().deleteUser(user);
						doSetDati = false;
					}

					/***********************************************************
					 * Operazioni su Audit *
					 **********************************************************/
					// Filtro
					if (oggetto instanceof Filtro) {
						Filtro filtro = (Filtro) oggetto;
						driver.getDriverAuditDB().deleteFiltro(filtro);
						doSetDati = false;
					}

					// Operation
					if (oggetto instanceof org.openspcoop2.web.lib.audit.log.Operation) {
						org.openspcoop2.web.lib.audit.log.Operation auditOp = 
								(org.openspcoop2.web.lib.audit.log.Operation) oggetto;
						driver.getDriverAuditDBAppender().deleteOperation(auditOp);
						doSetDati = false;
					}
					
					/***********************************************************
					 * Operazioni su Controllo Traffico *
					 **********************************************************/
					// Configurazione Policy
					if(oggetto instanceof ConfigurazionePolicy) {
						ConfigurazionePolicy policy = (ConfigurazionePolicy) oggetto;
						driver.deleteConfigurazionePolicy(policy); 
						doSetDati = false;
					}
					
					// Attivazione Policy
					if(oggetto instanceof AttivazionePolicy) {
						AttivazionePolicy policy = (AttivazionePolicy) oggetto;
						driver.deleteAttivazionePolicy(policy); 
						doSetDati = false;
					}
					
					/***********************************************************
					 * Operazioni su Generic Properties *
					 **********************************************************/
					// Generic Propertie
					if(oggetto instanceof GenericProperties) {
						GenericProperties genericProperties = (GenericProperties) oggetto;
						driver.getDriverConfigurazioneDB().deleteGenericProperties(genericProperties);
						doSetDati = false;
					}
					
					/***********************************************************
					 * Extended *
					 **********************************************************/
					if(extendedBean!=null && extendedServlet!=null){
						extendedServlet.performDelete(con, oggetto, extendedBean);
					}
					
					break;

				default:
					// Unkown operation type
					throw new ControlStationCoreException("[ControlStationCore::performOperation] Unkown operation type :" + operationType);

				}// fine switch

				// inserisco l'operazione nella lista
				if (doSetDati) {
					operazioneDaSmistareList.add(operazioneDaSmistare);
				}

			}// chiudo for

			if (doSetDati && smista) {
				/* Inoltro ogni operazione nella coda dello smistatore */

				// controllo su idTable
				for (OperazioneDaSmistare operazione : operazioneDaSmistareList) {
					if ((operazione == null) || (operazione.getIDTable() <= 0)) {
						// se ci sono degli errori faccio il rollback
						ControlStationCore.log.error("[ControlStationCore::performOperation] Operazione da smistare non valida.");

						// il rollback viene gestito da dal blocco di catch
						throw new ControlStationCoreException("[ControlStationCore::performOperation]Operazione da smistare non valida.");
					} else {
						ControlStationJMSCore.setDati(operazione, this.smistatoreQueue, this.cfName, this.cfProp);
					}
				}

			}

			// Prima di committare provo a eseguire lo script se previsto
			if(pddGestore!=null && this.isSinglePdD()==false){
				String tipoPdd = pddGestore.getTipo();
				if (tipoPdd != null && PddTipologia.OPERATIVO.toString().equals(tipoPdd)) {
					if(this.getSincronizzazionePddEngineEnabled_scriptShell_Path()!=null){
						if (CostantiControlStation.PERFORM_OPERATION_CREATE == pddGestoreTipoOperazione || 
								CostantiControlStation.PERFORM_OPERATION_DELETE == pddGestoreTipoOperazione){
							ScriptInvoker scriptInvoker = new ScriptInvoker(this.getSincronizzazionePddEngineEnabled_scriptShell_Path());
							String tipoOperazione = null;
							if (CostantiControlStation.PERFORM_OPERATION_CREATE == pddGestoreTipoOperazione){
								tipoOperazione = CostantiControlStation.SCRIPT_PERFORM_OPERATION_CREATE;
							}else{
								tipoOperazione = CostantiControlStation.SCRIPT_PERFORM_OPERATION_DELETE;
							}
							String nomeCoda = this.getSincronizzazionePddEngineEnabled_prefissoNomeCodaConfigurazionePdd() + pddGestore.getNome();
							scriptInvoker.run(tipoOperazione,nomeCoda,this.getSincronizzazionePddEngineEnabled_scriptShell_Args());
							String msg = "Invocazione script ["+this.getSincronizzazionePddEngineEnabled_scriptShell_Path()+"] ha ritornato un codice di uscita ["+scriptInvoker.getExitValue()+
									"] (Param1:"+tipoOperazione+" Param2:"+nomeCoda+" Param3:"+this.getSincronizzazionePddEngineEnabled_scriptShell_Args()+
									").\nOutputStream: "+scriptInvoker.getOutputStream()+"\nErrorStream: "+scriptInvoker.getErrorStream();
							if(scriptInvoker.getExitValue()!=0){
								throw new Exception(msg);
							}
							else{
								ControlStationCore.log.debug(msg);
							}
						}
					}
				}
			}
			
			// ogni operazione e' andata a buon fine quindi faccio il commit
			con.commit();
			
			// Devo Fermare e ricreare. Potrebbero essere stati modificati dei parametri
			if(pddGestore!=null && this.isSinglePdD()==false){
				if (CostantiControlStation.PERFORM_OPERATION_CREATE == pddGestoreTipoOperazione){
					GestorePdDInitThread.addGestore(pddGestore);
				}
				else if (CostantiControlStation.PERFORM_OPERATION_UPDATE == pddGestoreTipoOperazione){
					GestorePdDInitThread.deleteGestore(pddGestore.getNome());
					GestorePdDInitThread.addGestore(pddGestore);
				}
				else if (CostantiControlStation.PERFORM_OPERATION_DELETE == pddGestoreTipoOperazione){
					GestorePdDInitThread.deleteGestore(pddGestore.getNome());	
				}
			}

		} catch (DriverConfigurazioneNotFound e) {
			// se ci sono degli errori faccio il rollback
			ControlStationCore.log.error(e.getMessage(),e);

			try {
				ControlStationCore.log.debug("[ControlStationCore::performOperation] rollback on error :" + e.getMessage(), e);
				if(con!=null)con.rollback();

			} catch (Exception ex) {
			}

			throw e;
		} catch (DriverConfigurazioneException e) {
			// se ci sono degli errori faccio il rollback
			ControlStationCore.log.error(e.getMessage(),e);

			try {
				ControlStationCore.log.debug("[ControlStationCore::performOperation] rollback on error :" + e.getMessage(), e);
				if(con!=null)con.rollback();

			} catch (Exception ex) {
			}

			throw e;
		} catch (DriverRegistroServiziNotFound e) {
			ControlStationCore.log.error(e.getMessage(),e);

			try {
				ControlStationCore.log.debug("[ControlStationCore::performOperation] rollback on error :" + e.getMessage(), e);
				if(con!=null)con.rollback();

			} catch (Exception ex) {
			}

			throw e;

		} catch (DriverRegistroServiziException e) {
			ControlStationCore.log.error(e.getMessage(),e);

			try {
				ControlStationCore.log.debug("[ControlStationCore::performOperation] rollback on error :" + e.getMessage(), e);
				if(con!=null)con.rollback();

			} catch (Exception ex) {
			}

			throw e;

		} catch (DriverControlStationException e) {
			ControlStationCore.log.error(e.getMessage(),e);

			try {
				ControlStationCore.log.debug("[ControlStationCore::performOperation] rollback on error :" + e.getMessage(), e);
				if(con!=null)con.rollback();

			} catch (Exception ex) {
			}

			throw new DriverControlStationException(e);
		} catch (Exception e) {
			ControlStationCore.log.error(e.getMessage(),e);

			try {
				ControlStationCore.log.debug("[ControlStationCore::performOperation] rollback on error :" + e.getMessage(), e);
				if(con!=null)con.rollback();

			} catch (Exception ex) {
			}

			throw new ControlStationCoreException(e.getMessage(),e);
		} finally {
			// qui posso riabilitare l'auto commit
			// e rilasciare la connessione
			try {
				con.setAutoCommit(true);
				ControlStationCore.dbM.releaseConnection(con);
			} catch (Exception e) {
				// ignore
			}
		}

	}

	public void performOperationMultiTypes(String superUser, boolean smista,int[] operationTypes,Object ... oggetti) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverControlStationException, DriverRegistroServiziException, ControlStationCoreException, Exception {
		String nomeMetodo = "performOperationIbrido";
		ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] performing operation on objects " + this.getClassNames(oggetti));
		Tipologia[] tipoOperazione = new Tipologia[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			if(operationTypes[i]==CostantiControlStation.PERFORM_OPERATION_CREATE)
				tipoOperazione[i] = Tipologia.ADD;
			else if(operationTypes[i]==CostantiControlStation.PERFORM_OPERATION_UPDATE)
				tipoOperazione[i] = Tipologia.CHANGE;
			else
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

		ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] performed operation on objects " + this.getClassNames(oggetti));
	}

	/**
	 * Crea un nuovo oggetto nella govwayConsole e si occupa di inoltrare
	 * l'operazione nella coda dello Smistatore
	 */
	public void performCreateOperation(String superUser, boolean smista, Object ... oggetti) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverControlStationException, DriverRegistroServiziException, ControlStationCoreException, Exception {
		String nomeMetodo = "performCreateOperation";
		String operationType = "CREATE";
		ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] performing operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));
		int[] operationTypes = new int[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			operationTypes[i] = CostantiControlStation.PERFORM_OPERATION_CREATE;
		}
		Tipologia[] tipoOperazione = new Tipologia[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			tipoOperazione[i] = Tipologia.ADD;
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

		ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] performed operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));
	}

	/**
	 * Aggiorna un oggetto nella govwayConsole e si occupa di inoltrare
	 * l'operazione nella coda dello Smistatore
	 */
	public void performUpdateOperation(String superUser, boolean smista, Object ... oggetti) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverControlStationException, DriverRegistroServiziException, ControlStationCoreException, Exception {
		String nomeMetodo = "performUpdateOperation";
		String operationType = "UPDATE";

		ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] performing operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));
		int[] operationTypes = new int[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			operationTypes[i] = CostantiControlStation.PERFORM_OPERATION_UPDATE;
		}
		Tipologia[] tipoOperazione = new Tipologia[oggetti.length];
		for (int i = 0; i < oggetti.length; i++) {
			tipoOperazione[i] = Tipologia.CHANGE;
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

		ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] performed operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));

	}

	/**
	 * Cancella un oggetto nella govwayConsole e si occupa di inoltrare
	 * l'operazione nella coda dello Smistatore
	 */
	public void performDeleteOperation(String superUser, boolean smista, Object ... oggetti) throws DriverConfigurazioneNotFound, DriverConfigurazioneException, DriverRegistroServiziNotFound, DriverControlStationException, DriverRegistroServiziException, ControlStationCoreException, Exception {
		String nomeMetodo = "performDeleteOperation";
		String operationType = "DELETE";

		ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] performing operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));
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

		ControlStationCore.log.info("[ControlStationCore::" + nomeMetodo + "] performed operation type [" + operationType + "] on objects " + this.getClassNames(oggetti));

	}

	private <Type> ArrayList<String> getClassNames(Type[] array) {
		ArrayList<String> c = new ArrayList<String>();
		for (Type type : array) {
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
	private synchronized static void _verificaConsistenzaProtocolli(ControlStationCore core) {
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
					error.append("Configurazione non corretta; non è stato rilevato un soggetto di default per il protocollo '"+protocolName+"'.");
					
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
					List<String> tipi_con_protocolli_censiti = protocolFactoryManager.getOrganizationTypesAsList();
					for (IDSoggetto idSoggeto : soggettiDefault) {
						if(!tipi_con_protocolli_censiti.contains(idSoggeto.getTipo())) {
							error.append("Configurazione non corretta; Non esiste un protocollo che possa gestire il soggetto di default '"+idSoggeto.toString()+"' rilevato.");
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
	private static Integer auditLock = 0;
	public static void clearAuditManager(){
		synchronized (ControlStationCore.auditLock) {
			ControlStationCore.auditManager = null;	
		}
	}
	private static synchronized void initializeAuditManager(String tipoDatabase) throws Exception{
		if(ControlStationCore.auditManager==null){
			synchronized (ControlStationCore.auditLock) {
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
					throw new Exception("Inizializzazione auditManager non riuscita (LetturaConfigurazione): "+e.getMessage(),e);
				}finally{
					try{
						if(con!=null)
							con.close();
					}catch(Exception e){}
				}

				try{
					ControlStationCore.auditManager.initializeAudit(configurazioneAuditing, IDBuilder.getIDBuilder());
				}catch(Exception e){
					throw new Exception("Inizializzazione auditManager non riuscita: "+e.getMessage(),e);
				}
			}
		}
	}
	public static synchronized void updateAuditManagerConfiguration(String tipoDatabase) throws Exception{
		if(ControlStationCore.auditManager==null){
			throw new Exception("AuditManager non inizializzato");
		}
		synchronized (ControlStationCore.auditLock) {
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
				throw new Exception("Aggiornamento configurazione auditManager non riuscita (LetturaConfigurazione): "+e.getMessage(),e);
			}finally{
				try{
					if(con!=null)
						con.close();
				}catch(Exception e){}
			}

			try{
				ControlStationCore.auditManager.updateConfigurazioneAuditing(configurazioneAuditing);
			}catch(Exception e){
				throw new Exception("Aggiornamento configurazione auditManager non riuscita: "+e.getMessage(),e);
			}
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
	public static AuditAppender getAuditManagerInstance(String tipoDatabase)throws Exception{
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
			ControlStationCore.log.debug("Auditing dell'operazione ["+msg+"] non effettuato: "+disabilitato.getMessage());
			auditAbilitato = false;
		}catch(Exception e){
			ControlStationCore.log.error("Auditing dell'operazione ["+msg+"] non riuscito: "+e.getMessage(),e);
		}

		// loggo su file dell'interfaccia (dopo chiamata ad auditManager, in modo che viene registrato il msg solo se abilitato l'audit)
		if(auditAbilitato)
			ControlStationCore.log.info(msg);
	}

	public void performAuditLogout(String user){

		String msg = user+":"+"completed:LOGOUT";

		// loggo tramite auditManager
		boolean auditAbilitato = true;
		try{
			AuditAppender auditManager = ControlStationCore.getAuditManagerInstance(this.tipoDB);
			auditManager.registraOperazioneAccesso(Tipologia.LOGOUT, user,msg, this.isAuditingRegistrazioneElementiBinari);

		}catch(AuditDisabilitatoException disabilitato){
			ControlStationCore.log.debug("Auditing dell'operazione ["+msg+"] non effettuato: "+disabilitato.getMessage());
			auditAbilitato = false;
		}catch(Exception e){
			ControlStationCore.log.error("Auditing dell'operazione ["+msg+"] non riuscito: "+e.getMessage(),e);
		}

		// loggo su file dell'interfaccia (dopo chiamata ad auditManager, in modo che viene registrato il msg solo se abilitato l'audit)
		if(auditAbilitato)
			ControlStationCore.log.info(msg);
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
				ControlStationCore.log.error("GenerazioneIDOperazione non riuscita: "+e.getMessage(),e);
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
				ControlStationCore.log.debug("Auditing dell'operazione ["+msg+"] non effettuato: "+disabilitato.getMessage());
				auditAbilitato = false;
				throw disabilitato;
			}catch(Exception e){
				ControlStationCore.log.error("Auditing dell'operazione ["+msg+"] non riuscito: "+e.getMessage(),e);
			}

			// loggo su file dell'interfaccia (dopo chiamata ad auditManager, in modo che viene registrato il msg solo se abilitato l'audit)
			if(auditAbilitato)
				ControlStationCore.log.info(msg);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] DriverRegistroServiziException :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
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
				ControlStationCore.log.error("GenerazioneIDOperazione non riuscita: "+e.getMessage(),e);
				msg = "GenerazioneIDOperazione non riuscita: "+e.getMessage();
			}

			// loggo tramite auditManager
			boolean auditAbilitato = true;
			try{
				AuditAppender auditManager = ControlStationCore.getAuditManagerInstance(this.tipoDB);
				auditManager.registraOperazioneCompletataConSuccesso(idOperazione[i],msg);

			}catch(AuditDisabilitatoException disabilitato){
				ControlStationCore.log.debug("Auditing dell'operazione ["+msg+"] non effettuato: "+disabilitato.getMessage());
				auditAbilitato = false;
			}catch(Exception e){
				ControlStationCore.log.error("Auditing dell'operazione ["+msg+"] non riuscito: "+e.getMessage(),e);
			}

			// loggo su file dell'interfaccia (dopo chiamata ad auditManager, in modo che viene registrato il msg solo se abilitato l'audit)
			if(auditAbilitato)
				ControlStationCore.log.info(msg);
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
				ControlStationCore.log.error("GenerazioneIDOperazione non riuscita: "+e.getMessage(),e);
				msg = "GenerazioneIDOperazione non riuscita: "+e.getMessage();
			}

			// loggo tramite auditManager
			boolean auditAbilitato = true;
			try{
				AuditAppender auditManager = ControlStationCore.getAuditManagerInstance(this.tipoDB);
				auditManager.registraOperazioneTerminataConErrore(idOperazione[i], motivoErrore,msg);

			}catch(AuditDisabilitatoException disabilitato){
				ControlStationCore.log.debug("Auditing dell'operazione ["+msg+"] non effettuato: "+disabilitato.getMessage());
				auditAbilitato = false;
			}catch(Exception e){
				ControlStationCore.log.error("Auditing dell'operazione ["+msg+"] non riuscito: "+e.getMessage(),e);
			}

			// loggo su file dell'interfaccia (dopo chiamata ad auditManager, in modo che viene registrato il msg solo se abilitato l'audit)
			if(auditAbilitato)
				ControlStationCore.log.error(msg+", errore avvenuto:"+motivoErrore);
		}
	}

	private String generaMsgAuditing(String user,Stato statoOperazione,Tipologia tipoOperazione,Object oggetto) throws Exception{
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
			}catch (Exception e) {}

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
		// Generic Propertie
		else if(oggetto instanceof GenericProperties) {
			GenericProperties genericProperties = (GenericProperties) oggetto;
			msg+=":"+oggetto.getClass().getSimpleName();
			StringBuilder bf = new StringBuilder();
			bf.append("Nome[").append(genericProperties.getNome()).append("] Tipologia[").append(genericProperties.getTipologia()).append("]");
			msg+=":<"+bf.toString()+">";
		}
		// IExtendedBean
		else if(oggetto instanceof IExtendedBean){
			IExtendedBean w = (IExtendedBean) oggetto;
			msg+=":"+w.getClass().getSimpleName();
			msg+=":<"+w.getHumanId()+">";
		}
		
		return msg;
	}

	











	/* ************** UTILITA' GENERALI A TUTTI I CORE ED HELPER ***************** */

	private static final String VERSION_INFO_READ = "VERSION_INFO_READ";
	private static final String VERSION_INFO = "VERSION_INFO";
	
	private IVersionInfo versionInfo = null;
	private Boolean versionInfoRead = null;
	private synchronized IVersionInfo initInfoVersion(HttpSession session, String tipoDB) throws UtilsException {
		
		if(this.versionInfoRead==null) {
		
			try {
				Boolean versionInfoReadFromSession = ServletUtils.getObjectFromSession(session, Boolean.class, VERSION_INFO_READ);
				if(versionInfoReadFromSession!=null) {
					this.versionInfoRead = versionInfoReadFromSession;
					this.versionInfo = ServletUtils.getObjectFromSession(session, IVersionInfo.class, VERSION_INFO);
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
					ServletUtils.setObjectIntoSession(session, true, VERSION_INFO_READ);
					if(vInfo!=null) {
						ServletUtils.setObjectIntoSession(session, vInfo, VERSION_INFO);
					}
				}
			}finally {
				this.versionInfoRead = true;
			}
			
		}
		
		return this.versionInfo;
		
	}
	public IVersionInfo getInfoVersion(HttpSession session) throws UtilsException {
		if(this.versionInfoRead==null) {
			initInfoVersion(session, this.tipoDB);
		}
		return this.versionInfo;
	}
	public void updateInfoVersion(HttpSession session, String info) throws UtilsException {
		Connection con = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			IVersionInfo vInfo = getInfoVersion(session);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} 
	}
	
	public IConfigIntegrationReader getConfigIntegrationReader(IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException{
		String nomeMetodo = "getConfigIntegrationReader";
		
		try {
			// istanzio il driver
			DriverConfigurazioneDB driver = new DriverConfigurazioneDB(ControlStationCore.dbM.getDataSourceName(),ControlStationCore.dbM.getDataSourceContext(),ControlStationCore.log, this.tipoDB);
			return protocolFactory.getConfigIntegrationReader(driver);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			return new ArrayList<IDGruppo>();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			return new ArrayList<IDRuolo>();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			return new ArrayList<IDScope>();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public int countProtocolli(HttpSession session) throws  DriverRegistroServiziException {
		return this.countProtocolli(session, false);
	}
	public int countProtocolli(HttpSession session, boolean ignoreProtocolloSelezionato) throws  DriverRegistroServiziException {
		List<String> l = this.getProtocolli(session, ignoreProtocolloSelezionato);
		return l.size();
	}
	public List<String> getProtocolli(HttpSession session) throws  DriverRegistroServiziException {
		return this.getProtocolli(session, false);
	}
	public List<String> getProtocolli(HttpSession session, boolean ignoreProtocolloSelezionato) throws  DriverRegistroServiziException {
		return this.getProtocolli(session, ignoreProtocolloSelezionato, false);
	}
	public List<String> getProtocolli(HttpSession session, boolean ignoreProtocolloSelezionato, 
			boolean consideraProtocolliCompatibiliSoggettoSelezionato) throws  DriverRegistroServiziException {
		String getProtocolli = "getProtocolli";
		try{

			List<String> protocolliList = new ArrayList<String>();
			
			User u =ServletUtils.getUserFromSession(session);
			
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
			ControlStationCore.log.error("[ControlStationCore::" + getProtocolli + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + getProtocolli + "] Error :" + e.getMessage(),e);
		}
	}
	public List<String> getProtocolli(){
		
		List<String> protocolliList = new ArrayList<String>();
		
		MapReader<String, IProtocolFactory<?>> protocolFactories = this.protocolFactoryManager.getProtocolFactories();
		Enumeration<String> protocolli = protocolFactories.keys();
		while (protocolli.hasMoreElements()) {

			String protocollo = protocolli.nextElement();
			protocolliList.add(protocollo);
		}
		
		return ProtocolUtils.orderProtocolli(protocolliList);
	}
	public List<String> getProtocolliByFilter(HttpSession session, boolean filtraSoggettiEsistenti, 
			boolean filtraAccordiEsistenti) throws  DriverRegistroServiziException {
		return this.getProtocolliByFilter(session, filtraSoggettiEsistenti, filtraAccordiEsistenti, false);
	}
	public List<String> getProtocolliByFilter(HttpSession session, boolean filtraSoggettiEsistenti, 
			boolean filtraAccordiEsistenti, boolean filtraAccordiCooperazioneEsistenti) throws  DriverRegistroServiziException {
		return getProtocolliByFilter(session, filtraSoggettiEsistenti, null, 
				filtraAccordiEsistenti, filtraAccordiCooperazioneEsistenti);
	}
	public List<String> getProtocolliByFilter(HttpSession session, boolean filtraSoggettiEsistenti, PddTipologia dominio, 
			boolean filtraAccordiEsistenti) throws  DriverRegistroServiziException {
		return getProtocolliByFilter(session, filtraSoggettiEsistenti, dominio, 
				filtraAccordiEsistenti, false);
	}
	public List<String> getProtocolliByFilter(HttpSession session, boolean filtraSoggettiEsistenti, PddTipologia dominio, 
			boolean filtraAccordiEsistenti, boolean filtraAccordiCooperazioneEsistenti) throws  DriverRegistroServiziException {
		return this.getProtocolliByFilter(session, filtraSoggettiEsistenti, dominio, 
				filtraAccordiEsistenti, filtraAccordiCooperazioneEsistenti, 
				false);
	}
	public List<String> getProtocolliByFilter(HttpSession session, boolean filtraSoggettiEsistenti, PddTipologia dominio, 
			boolean filtraAccordiEsistenti, boolean filtraAccordiCooperazioneEsistenti, 
			boolean consideraProtocolliCompatibiliSoggettoSelezionato) throws  DriverRegistroServiziException {
		
		List<String> _listaTipiProtocollo = this.getProtocolli(session, false, consideraProtocolliCompatibiliSoggettoSelezionato);
		
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
		Search s = new Search();
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
		Search s = new Search();
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
		Search s = new Search();
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
		Search s = new Search();
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
		Search s = new Search(true);
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
		Search s = new Search(true);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public String[] getProfiliDiCollaborazioneSupportatiDalProtocollo(String protocollo,ServiceBinding serviceBinding) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getProfiliDiCollaborazioneSupportatiDalProtocollo";
		List<String> lstProt = new ArrayList<String>();
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public String[] getProfiliDiCollaborazioneSupportatiDalProtocolloDefault(ServiceBinding serviceBinding) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getProfiliDiCollaborazioneSupportatiDalProtocolloDefault";
		List<String> lstProt = new ArrayList<String>();
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public String getVersioneDefaultProtocollo(String protocollo) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "getVersioneDefaultProtocollo";
		try{

			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getVersioneDefault();

		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public boolean isProfiloDiCollaborazioneAsincronoSupportatoDalProtocolloDefault(ServiceBinding serviceBinding) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isProfiloDiCollaborazioneAsincronoSupportatoDalProtocolloDefault";
		try{
			return this.protocolFactoryManager.getDefaultProtocolFactory().createProtocolConfiguration().isSupportato(serviceBinding,ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
					|| this.protocolFactoryManager.getDefaultProtocolFactory().createProtocolConfiguration().isSupportato(serviceBinding,ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO)
					;
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public boolean isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo(String protocollo,ServiceBinding serviceBinding) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isProfiloDiCollaborazioneAsincronoSupportatoDalProtocollo";
		try{
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportato(serviceBinding,ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO)
					|| this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportato(serviceBinding,ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO);
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	
	public boolean isProfiloDiCollaborazioneSupportatoDalProtocolloDefault(ServiceBinding serviceBinding,ProfiloDiCollaborazione profiloCollaborazione) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isProfiloDiCollaborazioneSupportatoDalProtocolloDefault";
		try{
			return this.protocolFactoryManager.getDefaultProtocolFactory().createProtocolConfiguration().isSupportato(serviceBinding, profiloCollaborazione );
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public boolean isProfiloDiCollaborazioneSupportatoDalProtocollo(String protocollo,ServiceBinding serviceBinding, ProfiloDiCollaborazione profiloCollaborazione) throws DriverRegistroServiziNotFound, DriverRegistroServiziException {
		String nomeMetodo = "isProfiloDiCollaborazioneSupportatoDalProtocollo";
		try{
			return this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().isSupportato(serviceBinding,profiloCollaborazione );
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverRegistroServiziException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
	}
	
	public List<ServiceBinding> getServiceBindingList(IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException{
		String nomeMetodo = "getServiceBindingList";
		List<ServiceBinding> lst = new ArrayList<ServiceBinding>();
		try {
			ServiceBindingConfiguration defaultServiceBindingConfiguration = protocolFactory.createProtocolConfiguration().getDefaultServiceBindingConfiguration(null);
			if(defaultServiceBindingConfiguration.isServiceBindingSupported(ServiceBinding.REST))
				lst.add(ServiceBinding.REST);
			if(defaultServiceBindingConfiguration.isServiceBindingSupported(ServiceBinding.SOAP))
				lst.add(ServiceBinding.SOAP);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
		return lst;
	}
	
	public List<ServiceBinding> getServiceBindingListProtocollo(String protocollo) throws DriverConfigurazioneException{
		String nomeMetodo = "getServiceBindingListProtocollo";
		List<ServiceBinding> lst = new ArrayList<ServiceBinding>();
		try {
			IProtocolFactory<?> protocolFactory = this.protocolFactoryManager.getProtocolFactoryByName(protocollo); 
			ServiceBindingConfiguration defaultServiceBindingConfiguration = protocolFactory.createProtocolConfiguration().getDefaultServiceBindingConfiguration(null);
			if(defaultServiceBindingConfiguration.isServiceBindingSupported(ServiceBinding.REST))
				lst.add(ServiceBinding.REST);
			if(defaultServiceBindingConfiguration.isServiceBindingSupported(ServiceBinding.SOAP))
				lst.add(ServiceBinding.SOAP);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
		return lst;
	}
	
	public List<MessageType> getMessageTypeList(IProtocolFactory<?> protocolFactory,ServiceBinding serviceBinding) throws DriverConfigurazioneException{
		String nomeMetodo = "getMessageTypeList";
		List<MessageType> messageTypeSupported = new ArrayList<MessageType>();
		try {
			ServiceBindingConfiguration defaultServiceBindingConfiguration = protocolFactory.createProtocolConfiguration().getDefaultServiceBindingConfiguration(null);
			messageTypeSupported = defaultServiceBindingConfiguration.getMessageTypeSupported(serviceBinding);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
		return messageTypeSupported;
	}
	
	
	public List<InterfaceType> getInterfaceTypeList(IProtocolFactory<?> protocolFactory,ServiceBinding serviceBinding) throws DriverConfigurazioneException{
		String nomeMetodo = "getInterfaceTypeList";
		List<InterfaceType> interfacceSupportate = new ArrayList<InterfaceType>();
		try {
			interfacceSupportate = protocolFactory.createProtocolConfiguration().getInterfacceSupportate(serviceBinding);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
		return interfacceSupportate;
	}
	
	public List<InterfaceType> getInterfaceTypeList(String protocollo, ServiceBinding serviceBinding) throws DriverConfigurazioneException{
		String nomeMetodo = "getInterfaceTypeList";
		List<InterfaceType> interfacceSupportate = new ArrayList<InterfaceType>();
		try {
			interfacceSupportate = this.protocolFactoryManager.getProtocolFactoryByName(protocollo).createProtocolConfiguration().getInterfacceSupportate(serviceBinding);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}
		return interfacceSupportate;
	}
	
	public ServiceBinding getDefaultServiceBinding(IProtocolFactory<?> protocolFactory) throws DriverConfigurazioneException{
		String nomeMetodo = "getDefaultServiceBinding";
		try {
			ServiceBindingConfiguration defaultServiceBindingConfiguration = protocolFactory.createProtocolConfiguration().getDefaultServiceBindingConfiguration(null);
			return defaultServiceBindingConfiguration.getDefaultBinding();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
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
			boolean addTrattinoSelezioneNonEffettuata, boolean throwException, List<String> filtraAzioniUtilizzate) throws Exception{
		Map<String,String> mapAzioni = getMapAzioni(asps, aspc, addTrattinoSelezioneNonEffettuata, throwException, filtraAzioniUtilizzate,
				true, true);
		return mapAzioni;
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
		case WADL:
			return InterfaceType.WADL;

			
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
		case WADL:
			return org.openspcoop2.core.registry.constants.FormatoSpecifica.WADL;
			
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
			Class<?> classCostanti = Class.forName("org.openspcoop2.protocol.modipa.constants.ModICostanti");
			Field f = classCostanti.getDeclaredField("MODIPA_PROTOCOL_NAME");
			return protocollo!=null && protocollo.equals(f.get(null));
		}catch(Throwable t) {
			return false;
		}
	}
	
	private void _cryptPassword(ServizioApplicativo sa) throws UtilsException {
		if(this.isApplicativiPasswordEncryptEnabled() && this.applicativiPasswordManager!=null) {
			if(sa.getInvocazionePorta()!=null && sa.getInvocazionePorta().sizeCredenzialiList()>0) {
				for (Credenziali credenziali : sa.getInvocazionePorta().getCredenzialiList()) {
					if(StringUtils.isNotEmpty(credenziali.getPassword()) && !credenziali.isCertificateStrictVerification()) {
						credenziali.setPassword(this.applicativiPasswordManager.crypt(credenziali.getPassword()));
						credenziali.setCertificateStrictVerification(true); // viene salvata a true per salvare l'informazione che è cifrata
					}
				}
			}
		}
	} 
	
	private void _cryptPassword(org.openspcoop2.core.registry.Soggetto soggetto) throws UtilsException {
		if(this.isSoggettiPasswordEncryptEnabled() && this.soggettiPasswordManager!=null) {
			if(soggetto.getCredenziali()!=null) {
				if(StringUtils.isNotEmpty(soggetto.getCredenziali().getPassword()) && !soggetto.getCredenziali().isCertificateStrictVerification()) {
					soggetto.getCredenziali().setPassword(this.applicativiPasswordManager.crypt(soggetto.getCredenziali().getPassword()));
					soggetto.getCredenziali().setCertificateStrictVerification(true); // viene salvata a true per salvare l'informazione che è cifrata
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
}
