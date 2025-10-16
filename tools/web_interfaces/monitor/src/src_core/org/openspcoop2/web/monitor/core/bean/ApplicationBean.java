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
package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.Search;
import org.openspcoop2.core.commons.dao.DAOFactoryProperties;
import org.openspcoop2.core.config.driver.db.DriverConfigurazioneDB;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.plugins.utils.PluginsDriverUtils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.utils.ModIUtils;
import org.openspcoop2.utils.IVersionInfo;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.VersionUtilities;
import org.openspcoop2.web.monitor.core.bean.UserDetailsBean.RuoloBean;
import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.core.PddMonitorProperties;
import org.openspcoop2.web.monitor.core.core.Utility;
import org.openspcoop2.web.monitor.core.listener.AbstractConsoleStartupListener;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo;
import org.openspcoop2.web.monitor.core.utils.BrowserInfo.BrowserFamily;
import org.slf4j.Logger;

/****
 * ApplicationBean
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ApplicationBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static ApplicationBean instance = null;

	public static ApplicationBean getInstance(){
		if(ApplicationBean.instance == null) {
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (ApplicationBean.class) {
				ApplicationBean.init();
			}
		}

		return ApplicationBean.instance;
	}

	private static synchronized void init(){
		if(ApplicationBean.instance == null){
			ApplicationBean.instance = new ApplicationBean();
		}
	}

	/* Funzionalita */
	public static final String FUNZIONALITA_ALLARMI = "allarmi";
	public static final String FUNZIONALITA_STATISTICHE_BASE = "statistiche_base";
	public static final String FUNZIONALITA_STATISTICHE_PERSONALIZZATE = "statistiche_personalizzate";
	public static final String FUNZIONALITA_TRANSAZIONI_BASE = "transazioni_base";
	public static final String FUNZIONALITA_TRANSAZIONI_LIVE = "transazioni_live";
	public static final String FUNZIONALITA_TRANSAZIONI_LIVE_OPERATORE = "transazioni_live_operatore";
	public static final String FUNZIONALITA_EXPORT_TRANSAZIONI = "export_transazioni";
	public static final String FUNZIONALITA_TRANSAZIONI_CONTENUTI = "transazioni_contenuti";
	public static final String FUNZIONALITA_RICERCHE_PERSONALIZZATE = "ricerche_personalizzate";
	public static final String FUNZIONALITA_ESITI_LIVE = "esiti_live";
	public static final String FUNZIONALITA_ESITI_LIVE_OPERATORE = "esiti_live_operatore";
	public static final String FUNZIONALITA_PROCESSI = "processi";
	public static final String FUNZIONALITA_EXPORT_PROCESSI = "export_processi";
	public static final String FUNZIONALITA_SONDE_APPLICATIVE = "sonde_applicative";
	public static final String FUNZIONALITA_EVENTI = "eventi";
	public static final String FUNZIONALITA_ANALISI_DATI = "analisi_dati";
	public static final String FUNZIONALITA_UTENTI = "utenti";
	public static final String FUNZIONALITA_STATUS_PDD = "status_pdd";
	public static final String FUNZIONALITA_GENERICHE = "funzionalita_generiche";
	public static final String FUNZIONALITA_ARCHIVIAZIONE_DATI = "dump_contenuti";
	public static final String FUNZIONALITA_GESTIONE_PASSWORD = "gestione_password";
	public static final String FUNZIONALITA_GRAFICI_SVG = "grafici_svg";
	public static final String FUNZIONALITA_REPORT = "report";

	/* Ruoli */
	public static final String RUOLO_USER = UserDetailsBean.RUOLO_USER;
	public static final String RUOLO_OPERATORE = UserDetailsBean.RUOLO_OPERATORE;
	public static final String RUOLO_CONFIGURATORE = UserDetailsBean.RUOLO_CONFIGURATORE;
	public static final String RUOLO_AMMINISTRATORE = UserDetailsBean.RUOLO_AMMINISTRATORE;

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger(); 

	private transient LoginBean loginBean;
	private Map<String, Boolean> funzionalita = new HashMap<>();
	private Map<String, Boolean> roles = null;
	
	private boolean permessoTransazioni = false;
	private boolean permessoStatistiche = false;
	
	private boolean loginApplication = true;
	
	private Locale locale;

	private static Map<String, Boolean> funzionalitaStaticInstance = null;
	private void initializeFunzionalita(PddMonitorProperties govwayMonitorProperties) throws UtilsException{
		_initializeFunzionalita(govwayMonitorProperties);
	}
	private static synchronized void _initializeFunzionalita(PddMonitorProperties govwayMonitorProperties) throws UtilsException {
		if(ApplicationBean.funzionalitaStaticInstance==null){
			ApplicationBean.funzionalitaStaticInstance = new HashMap<>();
			
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE, govwayMonitorProperties.isAttivoModuloTransazioniBase());
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_TRANSAZIONI_LIVE, govwayMonitorProperties.isAttivoModuloTransazioniBase());
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_TRANSAZIONI_LIVE_OPERATORE, govwayMonitorProperties.isAttivoModuloTransazioniBase() && govwayMonitorProperties.isAttivoLiveRuoloOperatore());
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_STATISTICHE_BASE, govwayMonitorProperties.isAttivoModuloTransazioniStatisticheBase());
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_ALLARMI, govwayMonitorProperties.isAllarmiEnabled());
			boolean attivoModuloTransazioniPersonalizzate = govwayMonitorProperties.isAttivoModuloTransazioniPersonalizzate();
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_TRANSAZIONI_CONTENUTI, attivoModuloTransazioniPersonalizzate); 
			boolean attivoModuloRicerchePersonalizzate = govwayMonitorProperties.isAttivoModuloRicerchePersonalizzate();
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_RICERCHE_PERSONALIZZATE, attivoModuloRicerchePersonalizzate);
			boolean attivoModuloTransazioniStatistichePersonalizzate = govwayMonitorProperties.isAttivoModuloTransazioniStatistichePersonalizzate();
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE,attivoModuloTransazioniStatistichePersonalizzate);
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_PROCESSI, govwayMonitorProperties.isAttivoModuloProcessi());
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_SONDE_APPLICATIVE, govwayMonitorProperties.isAttivoModuloSonde());
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_EVENTI, govwayMonitorProperties.isAttivoModuloEventi());
			// lazy initialization per i ruoli, dato che il bean login verra' // inizializzato solo dopo il login dell'utente.
			// Funzionalita analisi dei dati abilitata se almeno una delle categorie personalizzabili e' abilitata
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_ANALISI_DATI, (attivoModuloTransazioniPersonalizzate || attivoModuloRicerchePersonalizzate || attivoModuloTransazioniStatistichePersonalizzate ));
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_ESITI_LIVE, govwayMonitorProperties.isAttivoTransazioniEsitiLive());
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_ESITI_LIVE_OPERATORE, govwayMonitorProperties.isAttivoTransazioniEsitiLive() && govwayMonitorProperties.isAttivoLiveRuoloOperatore());
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_STATUS_PDD, govwayMonitorProperties.isStatusPdDEnabled());

			// Funzionalita' che non sono gestite dal file di properties, ma necessarie per il controllo dei contenuti
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_UTENTI,true);
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_GENERICHE,true);
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_ARCHIVIAZIONE_DATI, true);
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_EXPORT_TRANSAZIONI, true);
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_EXPORT_PROCESSI, true);
			
			// Funzionalita' gestione password, controlla la gestione della password quando la console viene utilizzata in modalita' login esterno.
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_GESTIONE_PASSWORD, govwayMonitorProperties.isGestionePasswordUtentiAttiva());

			// funzionalita utilizza grafici in modalita' svg
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_GRAFICI_SVG, govwayMonitorProperties.isGraficiSvgEnabled());
			
			// funzionalita visualizzazione dei report in formato PDF 
			ApplicationBean.funzionalitaStaticInstance.put(ApplicationBean.FUNZIONALITA_REPORT, govwayMonitorProperties.isAttivoModuloReports());
			
		}
	}
	
	public void disabilitaFunzionalita(String nomeFunzionalita){
		// devo rimuoverlo in entrambe le mappe
		this.funzionalita.remove(nomeFunzionalita);
		this.funzionalita.put(nomeFunzionalita,false);
		ApplicationBean.funzionalitaStaticInstance.remove(nomeFunzionalita);
		ApplicationBean.funzionalitaStaticInstance.put(nomeFunzionalita,false);
	}
	
	public ApplicationBean() {
		// inizializzazione, lasciare public il costruttore poichè usato in tools/web_interfaces/monitor/src/src_core/META-INF/faces-config.xml
		// altrimenti si ottiene errore: Caused by: com.sun.faces.mgbean.ManagedBeanCreationException: Unable to create managed bean applicationBean.  The following problems were found:
	    // - Managed bean class org.openspcoop2.web.monitor.core.bean.ApplicationBean for managed bean applicationBean doesnt declare a public no-argument constructor.
		try {
			PddMonitorProperties govwayMonitorProperties = PddMonitorProperties.getInstance(ApplicationBean.log);
			initializeFunzionalita(govwayMonitorProperties);
			this.funzionalita.putAll(ApplicationBean.funzionalitaStaticInstance);
			
			this.loginApplication = govwayMonitorProperties.isLoginApplication();
			
			this.locale = govwayMonitorProperties.getConsoleLocale();
			
		} catch (Exception e) {
			ApplicationBean.log.error("Errore durante l'inizializzazione del ApplicationBean.",e);
		}
	}

	private void initRoles() {
		try {
			if (this.loginBean != null && this.loginBean.getLoggedUser() != null) {
				UserDetailsBean u = this.loginBean.getLoggedUser();
				this.roles = getRuoliUtente(u);
				this.permessoStatistiche = u.getUtente().getPermessi().isReportistica();
				this.permessoTransazioni = u.getUtente().getPermessi().isDiagnostica();
			}
		} catch (Exception e) {
			ApplicationBean.log.error(e.getMessage(), e);
		}
	}

	public boolean isFunzionalitaAbilitata(String function) {
		return this.funzionalita!=null && this.funzionalita.get(function)!=null && this.funzionalita.get(function);
	}
	public boolean isRuoloAbilitato(String role) {
		return this.roles!=null && this.roles.get(role)!=null && this.roles.get(role);
	}
	
	public Map<String, Boolean>  getRuoliUtente(UserDetailsBean u) {
		Map<String, Boolean> ruoli = new HashMap<>();
		List<RuoloBean> auths = u.getAuthorities();
		if (auths != null && !auths.isEmpty()) {

			ruoli.put(ApplicationBean.RUOLO_AMMINISTRATORE, false);
			ruoli.put(ApplicationBean.RUOLO_CONFIGURATORE, false);
			ruoli.put(ApplicationBean.RUOLO_OPERATORE, false);
			ruoli.put(ApplicationBean.RUOLO_USER, false);

			Iterator<RuoloBean> itAuths = auths.iterator();
			while (itAuths.hasNext()) {
				RuoloBean grantedAuthority = itAuths.next();
				String a = grantedAuthority.getAuthority();
				if (UserDetailsBean.RUOLO_AMMINISTRATORE.equals(a))
					ruoli.put(ApplicationBean.RUOLO_AMMINISTRATORE, true);
				if (UserDetailsBean.RUOLO_CONFIGURATORE.equals(a))
					ruoli.put(ApplicationBean.RUOLO_CONFIGURATORE, true);
				if (UserDetailsBean.RUOLO_OPERATORE.equals(a))
					ruoli.put(ApplicationBean.RUOLO_OPERATORE, true);
				if (UserDetailsBean.RUOLO_USER.equals(a))
					ruoli.put(ApplicationBean.RUOLO_USER, true);
			}
		}

		return ruoli;
	}

	public LoginBean getLoginBean() {
		return this.loginBean;
	}

	public Map<String, Boolean> getModules() {
		return this.funzionalita;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public void setModules(Map<String, Boolean> modules) {
		this.funzionalita = modules;
	}

	private void checkRoles() {
		if (this.roles == null)
			initRoles();
	}

	/*
	 * Calcolo proprieta di render
	 */


	/********************************************************/

	/******** Voci del menu' sezione MONITORAGGIO   ********/	

	/*******************************************************/


	public boolean getShowTransazioniBase() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;
		
		if(!this.permessoTransazioni)
			return false;

		// le transazioni sono visualizzabili dall' operatore
		return(this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE));
	}

	public boolean getShowTransazioniContenuti() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_CONTENUTI))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;
		
		if(!this.permessoTransazioni)
			return false;

		// le transazioni sono visualizzabili dall' operatore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE)	|| 
				this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE));
	}

	public boolean getShowRicerchePersonalizzate() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_RICERCHE_PERSONALIZZATE))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;
		
		if(!this.permessoTransazioni)
			return false;

		// le ricerche sono visualizzabili dall' operatore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE));
	}



	public boolean getShowSonde() {

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_SONDE_APPLICATIVE))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// gli allarmi sono visualizzabili dall' operatore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE)	|| 
				this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE));
	}



	public boolean getShowAllarmi() {

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_ALLARMI))
			return false;

		if(!this.existsPluginAllarmi()) {
			return false;
		}
		
		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// gli allarmi sono visualizzabili dall' operatore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE)	|| 
				this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE));
	}
	
	private boolean existsPluginAllarmi() {
		try {
			Logger sqlLogger = LoggerManager.getPddMonitorSqlLogger();
			String tipoDatabase = DAOFactoryProperties.getInstance(sqlLogger).getTipoDatabase(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			String datasourceJNDIName = DAOFactoryProperties.getInstance(sqlLogger).getDatasourceJNDIName(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
			Properties datasourceJNDIContext = DAOFactoryProperties.getInstance(sqlLogger).getDatasourceJNDIContext(org.openspcoop2.core.commons.search.utils.ProjectInfo.getInstance());
	
			DriverConfigurazioneDB driverConfigDB = new DriverConfigurazioneDB(datasourceJNDIName,datasourceJNDIContext, sqlLogger, tipoDatabase);
			Connection con = null;
			try {
				con = driverConfigDB.getConnection("existsPluginAllarmi");
				ISearch ricercaPlugin = new Search(true);
				ricercaPlugin.addFilter( Liste.CONFIGURAZIONE_PLUGINS_CLASSI, Filtri.FILTRO_TIPO_PLUGIN_CLASSI, TipoPlugin.ALLARME.toString());
				List<Plugin> p = PluginsDriverUtils.pluginsClassiList(ricercaPlugin, con, sqlLogger, tipoDatabase);
				return p!=null && !p.isEmpty();
			}finally {
				driverConfigDB.releaseConnection("existsPluginAllarmi", con);
			}
		}catch(Exception e) {
			log.error(e.getMessage(),e);
			return false;
		}
	}


	public boolean getShowTransazioniLive() {
		checkRoles();

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;
		
		if(!this.permessoTransazioni)
			return false;

		/// sezione visibile solo all'operatore ed amministratore
		if(this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE)
				&& this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE)
				&& this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_LIVE)){
			return true;
		}
		//per il ruolo operatore bisogna verificare se e' abilitato il live per i non admin
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE) 
				&& this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE)
				&& this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_LIVE_OPERATORE) );
	}


	public boolean getShowEsitiLive() {
		checkRoles();

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		if(!this.permessoTransazioni)
			return false;
		
		/// sezione visibile solo all'operatore ed amministratore
		if(this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE)
				&& this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE)
				&& this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_ESITI_LIVE)){
			return true;
		}
		//per il ruolo operatore bisogna verificare se e' abilitato il live per i non admin
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE) 
				&& this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_BASE)
				&& this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_ESITI_LIVE_OPERATORE) );
	}

	public boolean getShowProcessi() {
		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_PROCESSI))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// processi visualizzabili solo da amministratore   
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE));			
	}

	public boolean getShowEventi() {
		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_EVENTI))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// eventi visualizzabili solo da amministratore   
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE));			
	}



	/********************************************************/

	/******** Voci del menu' sezione TRANSAZIONI GRID   ********/	

	/*******************************************************/

	public boolean getShowInformazioniContenutiTransazioniGrid() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_CONTENUTI))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;
		
		if(!this.permessoTransazioni)
			return false;

		// le informazioni sono visualizzabili dall' operatore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE));
	}

	public boolean getShowInformazioniEventiTransazioniGrid() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_EVENTI))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// le informazioni sono visualizzabili dall' operatore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE));
	}


	/********************************************************/

	/******** Voci del menu' sezione STATISTICHE   ********/	

	/*******************************************************/

	public boolean getShowStatisticheBase() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_STATISTICHE_BASE))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;
		
		if(!this.permessoStatistiche)
			return false;

		// le statistiche sono visualizzabili dall' operatore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE));
	}

	public boolean getShowStatistichePersonalizzate() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;
		
		if(!this.permessoStatistiche)
			return false;

		// le statistiche sono visualizzabili dall' operatore
		if (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE))
			return true;

		return this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_STATISTICHE_BASE);
	}


	public boolean getShowStatisticheConfigurazioniGenerali() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_STATISTICHE_BASE))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;
		
		if(!this.permessoStatistiche)
			return false;

		// le statistiche sono visualizzabili dall' amministratore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE)
				|| this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE));
	}
	
	public boolean getShowStatistichePdndTracing() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_STATISTICHE_BASE))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;
		
		if(!this.permessoStatistiche)
			return false;
		
		// non deve essere selezionato nessun profilo oppure solo il modI
		String loggedUtenteModalita = Utility.getLoggedUtenteModalita();

		boolean modalitaOk = (Costanti.VALUE_PARAMETRO_MODALITA_ALL.equals(loggedUtenteModalita) || CostantiLabel.MODIPA_PROTOCOL_NAME.equals(loggedUtenteModalita)); 
		
		try {
			modalitaOk = modalitaOk && ModIUtils.isTracingPDNDEnabled();
		}catch(Exception e) {
			throw new UtilsRuntimeException("isTracingPDNDEnabled failed: "+e.getMessage(),e);
		}
		
		// le statistiche sono visualizzabili dall' amministratore e operatore se la modalita' e' corretta
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE) || this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE)) && modalitaOk;
	
	}


	/********************************************************/

	/******** Voci del menu' sezione CONFIGURAZIONE ********/	

	/*******************************************************/

	public boolean getShowSezioneConfigurazione() {
		// la sezione configurazione puo essere vista da amministratore e
		// configuratore
		checkRoles();

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// I moduli di configurazione possono non essere presenti nella versione open 
		return ((this.getShowConfigurazioneAllarmi() || this.getShowConfigurazioneSonde() || this.getShowConfigurazioneLibreria() || this.getShowConfigurazioneProcessi()) 
				&& (this.isRuoloAbilitato(ApplicationBean.RUOLO_CONFIGURATORE)	|| this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE)));
	}



	public boolean getShowConfigStatistichePersonalizzate() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// la configurazione delle statistiche e' visibile al configuratore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_CONFIGURATORE));
	}

	public boolean getShowConfigRicerchePersonalizzate() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_RICERCHE_PERSONALIZZATE))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// le la configurazione delle ricerche e' visualizzabile dal configuratore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_CONFIGURATORE));
	}


	public boolean getShowConfigTransazioniContenuti() {

		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_CONTENUTI))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_CONFIGURATORE));
	}



	public boolean getShowAnalisiContenuti() {
		// l'analisi contenuti e' visibile solo all'amministratore
		checkRoles();

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_CONFIGURATORE));
	}

	public boolean getShowConfigurazioneSonde() {
		// la sezione delle sonde e' visibile solo all'amministratore
		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_SONDE_APPLICATIVE))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_CONFIGURATORE));
	}

	public boolean getShowConfigurazioneLibreria() {

		checkRoles();

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// sezione visibile solo al configuratore
		if (!this.isRuoloAbilitato(ApplicationBean.RUOLO_CONFIGURATORE))
			return false;

		return (this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_STATISTICHE_PERSONALIZZATE)
				|| this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_RICERCHE_PERSONALIZZATE)
				|| this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_TRANSAZIONI_CONTENUTI)
				|| this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_PROCESSI));
	}

	public boolean getShowCambiaPassword() {

		if(!this.loginApplication) {
			return false;
		}
		
		// se sono loggato con OAuth2, non posso cambiare la password
		if (Utility.isUtenteLoggatoOAuth2()) {
			return false;
		}
		
		checkRoles();
		
		// gestione password deve essere abilitata a prescindere dall'utenza
		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_GESTIONE_PASSWORD))
			return false;

		if(this.roles == null)
			return false;

		return (!this.roles.isEmpty());
	}


	public boolean getShowConfigurazioneProcessi() {
		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_PROCESSI))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// processi visualizzabili dal configuratore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_CONFIGURATORE));
	}

	public boolean getShowConfigurazioneAllarmi() {
		return false; // spostato sulla console di configurazione
	}
	
	public boolean getShowReport(){
		checkRoles();

		if (!this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_REPORT))
			return false;

		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// visualizzazione consentita solo all'amministratore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE));
	}

	public BrowserInfo getBrowserInfo(){
		BrowserInfo browserInfo = null;
		try {
			browserInfo = BrowserInfo.getBrowserInfo(FacesContext.getCurrentInstance());
		} catch (Exception e) {
			ApplicationBean.log.error("Errore durante la lettura delle info Browser:" + e.getMessage(),e);
		}

		return browserInfo;
	}
	
	public boolean isAmministratore() {
		checkRoles();
		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// controllo utente possiede ruolo utente
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_AMMINISTRATORE));
	}

	public void setAmministratore(boolean amministratore) { /* metodo vuoto creato per esigenze di framework */	}

	public boolean isUser() {
		checkRoles();
		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// controllo utente possiede ruolo utente
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_USER));
	}

	public void setUser(boolean user) {	/* metodo vuoto creato per esigenze di framework */ }

	public boolean isConfiguratore() {
		checkRoles();
		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// controllo utente possiede ruolo configuratore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_CONFIGURATORE));
	}

	public void setConfiguratore(boolean configuratore) { /* metodo vuoto creato per esigenze di framework */ }

	public boolean isOperatore() {
		checkRoles();
		if(this.roles == null)
			return false;

		if(this.roles.isEmpty())
			return false;

		// controllo utente possiede ruolo operatore
		return (this.isRuoloAbilitato(ApplicationBean.RUOLO_OPERATORE));
	}

	public void setOperatore(boolean operatore) { /* metodo vuoto creato per esigenze di framework */ }
	
	public boolean isGraficiSvgEnabled(){
		return this.isFunzionalitaAbilitata(ApplicationBean.FUNZIONALITA_GRAFICI_SVG);
	}

	public void cleanSVG(){
		BrowserInfo browserInfo = getBrowserInfo();
		try {
			if(browserInfo.getBrowserFamily().equals(BrowserFamily.IE)){
				// solo per <= 8
//				if(browserInfo.getVersion() != null && browserInfo.getVersion().intValue() <= 8){
//					HttpServletResponse response = BrowserInfo.getResponse(FacesContext.getCurrentInstance());
//					response.setHeader("X-UA-Compatible", "IE=EmulateIE8");
//				}
				
				// per tutte le versioni
				HttpServletResponse response = BrowserInfo.getResponse(FacesContext.getCurrentInstance());
				response.setHeader("X-UA-Compatible", "IE=edge");
			}
		} catch (Exception e) {
			ApplicationBean.log.error("Errore durante la lettura delle info Browser:" + e.getMessage(),e);
		}
	}

	public String getIdProdotto(){
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

	public String getSito(){
		IVersionInfo versionInfo = this.loginBean!=null ? this.loginBean.getvInfo() : null;
		String soggettoSelezionato = Utility.getSoggettoSelezionatoPerVersionInfo();
		if(versionInfo!=null && !StringUtils.isEmpty(versionInfo.getWebSite(soggettoSelezionato))) {
			return versionInfo.getWebSite(soggettoSelezionato);
		}
		else {
			return Costanti.LABEL_OPENSPCOOP2_WEB;
		}
	}

	public String getCopyright(){
		IVersionInfo versionInfo = this.loginBean!=null ? this.loginBean.getvInfo() : null;
		String soggettoSelezionato = Utility.getSoggettoSelezionatoPerVersionInfo();
		if(versionInfo!=null && !StringUtils.isEmpty(versionInfo.getCopyright(soggettoSelezionato))) {
			return versionInfo.getCopyright(soggettoSelezionato);
		}
		else {
			return CostantiPdD.OPENSPCOOP2_COPYRIGHT;
		}
	}

	public String getLicenza() {
		IVersionInfo versionInfo = this.loginBean!=null ? this.loginBean.getvInfo() : null;
		String soggettoSelezionato = Utility.getSoggettoSelezionatoPerVersionInfo();
		if(versionInfo!=null) {
			try {
				return versionInfo.getInfo(soggettoSelezionato);
			}catch(Exception e) {
				ApplicationBean.log.error(e.getMessage(),e);
				return CostantiPdD.OPENSPCOOP2_LICENSE;
			}
		}
		else {
			return CostantiPdD.OPENSPCOOP2_LICENSE;
		}
	}
	
	public int getLicenzaRows() {
		String licenza = getLicenza();
		String [] split = licenza.split("\n");
		if(split==null || split.length>11) {
			return 11;
		}
		else {
			return split.length+1;
		}
	}
	
	public String getLabelProfilo() {
		return org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO;
	}
	public String getLabelProfili() {
		return org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLI;
	}
	public String getLabelProfiloDi() {
		return org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_DI;
	}
	public String getLabelProfiliDi() {
		return org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLI_DI;
	}
	public String getLabelProfiloCompact() {
		return org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLO_COMPACT;
	}
	public String getLabelProfiliCompact() {
		return org.openspcoop2.core.constants.Costanti.LABEL_PARAMETRO_PROTOCOLLI_COMPACT;
	}
	public String getLabelSoggettoDi() {
		return org.openspcoop2.web.monitor.core.constants.Costanti.LABEL_PARAMETRO_SOGGETTO_OPERATIVO;
	}
	public String getLabelSoggettoCompact() {
		return org.openspcoop2.web.monitor.core.constants.Costanti.LABEL_PARAMETRO_SOGGETTO_COMPACT;
	}
	public String getLabelSoggettiCompact() {
		return org.openspcoop2.web.monitor.core.constants.Costanti.LABEL_PARAMETRO_SOGGETTI_COMPACT;
	}
	public String getLabelRisultatoSelezioneSoggetto() {
		if(this.loginBean.getSoggettoPddMonitor() == null || this.loginBean.getSoggettoPddMonitor().equals(Costanti.VALUE_PARAMETRO_MODALITA_ALL)) {
			return MessageFormat.format("{0} disponibili: {1}", getLabelSoggettiCompact(), this.loginBean.getLabelSoggettoSenzaPrefisso());
		} else {
			return MessageFormat.format("{0} attuale: {1}",getLabelSoggettoCompact(), this.loginBean.getLabelSoggettoSenzaPrefisso());
		}
	}
	
	private static final String CACHE_SEPARATOR = "\n";
	private String getCacheDetailsEngine(String stato, String param) {
		String [] split = stato.split(ApplicationBean.CACHE_SEPARATOR);
		for (int i = 0; i < split.length; i++) {
			String label = split[i];
			String value = "";
			if(split[i].contains(":")){
				label = split[i].split(":")[0];
				value = split[i].split(":")[1];
				if(label.equalsIgnoreCase(param)) {
					return value;
				}
			}
		}
		return null;
	}	
	
	public String resetAllCache() {
		this.resetCacheDatiConfigurazione();
		this.resetCacheRicercheConfigurazione();
		return null; // DEVE ESSERE NULL PER NON NAVIGARE
	}
	
	public boolean isCacheDatiConfigurazioneEnabled() {
		return (AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null);
	}
	public String getCacheDatiConfigurazioneStato() {
		return this.isCacheDatiConfigurazioneEnabled() ? "abilitata" : "disabilitata";
	}
	public String getCacheDatiConfigurazioneAlgoritmo() {
		return this.isCacheDatiConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheDatiConfigurazioneDetails(), "Algoritmo") : "-";
	}
	public String getCacheDatiConfigurazioneDimensione() {
		return this.isCacheDatiConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheDatiConfigurazioneDetails(), "Dimensione") : "-";
	}
	public String getCacheDatiConfigurazioneElementiInCache() {
		return this.isCacheDatiConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheDatiConfigurazioneDetails(), "ElementiInCache") : "-";
	}
	public String getCacheDatiConfigurazioneMemoriaOccupata() {
		return this.isCacheDatiConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheDatiConfigurazioneDetails(), "MemoriaOccupata") : "-";
	}
	public String getCacheDatiConfigurazioneIdleTime() {
		return this.isCacheDatiConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheDatiConfigurazioneDetails(), "IdleTime") : "-";
	}
	public String getCacheDatiConfigurazioneLifeTime() {
		return this.isCacheDatiConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheDatiConfigurazioneDetails(), "LifeTime") : "-";
	}
	public String getCacheDatiConfigurazioneDetails() {
		try {
			return AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.printStatsCache(ApplicationBean.CACHE_SEPARATOR);
		}catch(Exception e) {
			LoggerManager.getPddMonitorCoreLogger().error(e.getMessage(),e);
			return "Informazioni non disponibili";
		}
	}
	public int getCacheDatiConfigurazioneDetailsRows() {
		String stato = getCacheDatiConfigurazioneDetails();
		String [] split = stato.split(ApplicationBean.CACHE_SEPARATOR);
		if(split==null || split.length>11) {
			return 10;
		}
		else {
			return split.length+1;
		}
	}
	public String resetCacheDatiConfigurazione() {
		if(this.isCacheDatiConfigurazioneEnabled()) {
			try {
				AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.resetCache();
			}catch(Exception e) {
				LoggerManager.getPddMonitorCoreLogger().error("ResetCache 'dati': "+e.getMessage(),e);
			}
		}
		return null; // DEVE ESSERE NULL PER NON NAVIGARE
	}
	
	public boolean isCacheRicercheConfigurazioneEnabled() {
		return (AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione!=null);
	}
	public String getCacheRicercheConfigurazioneStato() {
		return this.isCacheRicercheConfigurazioneEnabled() ? "abilitata" : "disabilitata";
	}
	public String getCacheRicercheConfigurazioneAlgoritmo() {
		return this.isCacheRicercheConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheRicercheConfigurazioneDetails(), "Algoritmo") : "-";
	}
	public String getCacheRicercheConfigurazioneDimensione() {
		return this.isCacheRicercheConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheRicercheConfigurazioneDetails(), "Dimensione") : "-";
	}
	public String getCacheRicercheConfigurazioneElementiInCache() {
		return this.isCacheRicercheConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheRicercheConfigurazioneDetails(), "ElementiInCache") : "-";
	}
	public String getCacheRicercheConfigurazioneMemoriaOccupata() {
		return this.isCacheRicercheConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheRicercheConfigurazioneDetails(), "MemoriaOccupata") : "-";
	}
	public String getCacheRicercheConfigurazioneIdleTime() {
		return this.isCacheRicercheConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheRicercheConfigurazioneDetails(), "IdleTime") : "-";
	}
	public String getCacheRicercheConfigurazioneLifeTime() {
		return this.isCacheRicercheConfigurazioneEnabled() ? getCacheDetailsEngine(getCacheRicercheConfigurazioneDetails(), "LifeTime") : "-";
	}
	public String getCacheRicercheConfigurazioneDetails() {
		try {
			return AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione.printStatsCache(ApplicationBean.CACHE_SEPARATOR);
		}catch(Exception e) {
			LoggerManager.getPddMonitorCoreLogger().error(e.getMessage(),e);
			return "Informazioni non disponibili";
		}
	}
	public int getCacheRicercheConfigurazioneDetailsRows() {
		String licenza = getLicenza();
		String [] split = licenza.split(ApplicationBean.CACHE_SEPARATOR);
		if(split==null || split.length>11) {
			return 10;
		}
		else {
			return split.length+1;
		}
	}
	public String resetCacheRicercheConfigurazione() {
		if(this.isCacheRicercheConfigurazioneEnabled()) {
			try {
				AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione.resetCache();
			}catch(Exception e) {
				LoggerManager.getPddMonitorCoreLogger().error("ResetCache 'ricerche': "+e.getMessage(),e);
			}
		}
		return null; // DEVE ESSERE NULL PER NON NAVIGARE
	}

	public String getTimeZone() {
		return TimeZone.getDefault().getID();
	}
	
	public Locale getLocale() {
		if(this.locale == null)
			return Locale.getDefault();
		
		return this.locale;
	}
}
