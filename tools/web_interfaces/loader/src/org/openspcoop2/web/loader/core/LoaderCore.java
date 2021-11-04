/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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


package org.openspcoop2.web.loader.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.IVersionInfo;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.VersionUtilities;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.users.DriverUsersDB;
import org.openspcoop2.web.lib.users.DriverUsersDBException;
import org.openspcoop2.web.lib.users.dao.User;
import org.openspcoop2.web.loader.config.DatasourceProperties;
import org.openspcoop2.web.loader.config.LoaderProperties;
import org.w3c.dom.Document;

/**
 * OpenSPCoopLoaderCore
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class LoaderCore{

	public static Logger log = null;
	
	/** Impostazioni grafiche */
	private String loaderNomeSintesi = null;
	private String loaderNomeEsteso = null;
	private String loaderNomeEstesoSuffix = null;
	private String loaderCSS = null;
	private String loaderLanguage = null;
	private int consoleLunghezzaLabel = 50;
	private String logoHeaderImage = null;
	private String logoHeaderTitolo = null;
	private String logoHeaderLink = null;
	private boolean visualizzaLinkHomeHeader = false;
	
	private String getTitleSuffix(HttpSession session) {
		IVersionInfo versionInfo = null;
		try {
			versionInfo = getInfoVersion(session);
		}catch(Exception e) {
			LoaderCore.log.error("Errore durante la lettura delle informazioni sulla versione: "+e.getMessage(),e);
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
	
	public String getLoaderNomeSintesi() {
		return this.loaderNomeSintesi;
	}
	public String getLoaderNomeEsteso(HttpSession session) {
		String titleSuffix = getTitleSuffix(session);
		if(!StringUtils.isEmpty(titleSuffix)){
			if(!titleSuffix.startsWith(" ")) {
				titleSuffix = " "+titleSuffix;
			}
			return this.loaderNomeEsteso+titleSuffix;
		}
		else{
			return this.loaderNomeEsteso;
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
	public String getLoaderCSS() {
		return this.loaderCSS;
	}
	public String getLoaderLanguage() {
		return this.loaderLanguage;
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
	
	private String nomePdDOperativaCtrlstatSinglePdD = null;
	public LoaderCore() throws Exception{
		// Log4J caricato tramite LoaderStartup
		LoaderCore.log = LoggerWrapperFactory.getLogger("govway.loader");

		this.initCore();

	}

	private boolean searchUserIntoRegistro = true;
	public boolean isSearchUserIntoRegistro() {
		return this.searchUserIntoRegistro;
	}

	private boolean gestioneSoggetti = true;
	private boolean mantieniFruitoriServizi = true;
	private StatiAccordo statoAccordo = null;
	private String tipoPdD = null;
	private String protocolloDefault;

	private String dataSourceRegistroServizi = null;
	private java.util.Properties ctxDatasourceRegistroServizi = null;
	private String tipoDatabaseRegistroServizi = null;

	private String dataSourceConfigurazionePdD = null;
	private java.util.Properties ctxDatasourceConfigurazionePdD = null;
	private String tipoDatabaseConfigurazionePdD = null;

	private AbstractXMLUtils xmlUtils = null;
	
	private void initCore() throws Exception {
		
		// Leggo le informazioni da loader.datasource.properties
		DatasourceProperties datasourceProperties = null;
		try {
			datasourceProperties = DatasourceProperties.getInstance();

			// RegistroServizi
			this.dataSourceRegistroServizi = datasourceProperties.getRegistroServizi_DataSource();
			this.tipoDatabaseRegistroServizi = datasourceProperties.getRegistroServizi_TipoDatabase();
			this.ctxDatasourceRegistroServizi = datasourceProperties.getRegistroServizi_DataSourceContext();

			// ConfigurazionePdD
			this.dataSourceConfigurazionePdD = datasourceProperties.getConfigurazione_DataSource();
			this.tipoDatabaseConfigurazionePdD = datasourceProperties.getConfigurazione_TipoDatabase();
			this.ctxDatasourceConfigurazionePdD = datasourceProperties.getConfigurazione_DataSourceContext();		

		} catch (java.lang.Exception e) {
			LoaderCore.log.error("[OpenSPCoopLoader::initCore] Impossibile leggere i dati dal file loader.datasource.properties:" + e.toString());
			throw new Exception("[OpenSPCoopLoader::initCore] Impossibile leggere i dati dal file loader.datasource.properties:" + e.toString());
		} 
		
		
		// Leggo le informazioni da loader.properties
		LoaderProperties loaderProperties = null;
		
		try {
			loaderProperties = LoaderProperties.getInstance();

			// Funzionalità Generiche
			this.nomePdDOperativaCtrlstatSinglePdD = loaderProperties.getNomePddOperativaConsoleSinglePdDMode();	
			if(this.nomePdDOperativaCtrlstatSinglePdD==null){
				this.nomePdDOperativaCtrlstatSinglePdD = this.getNomePddOperativa();
			}
			this.gestioneSoggetti = loaderProperties.isGestioneSoggetti();
			this.mantieniFruitoriServizi = loaderProperties.isMantieniFruitoriServiziEsistenti();
			this.searchUserIntoRegistro = loaderProperties.isAutenticazioneUtenti_UtilizzaDabaseRegistro();
			this.statoAccordo = loaderProperties.getStatoAccordi();
			this.tipoPdD = loaderProperties.getTipoPortaDiDominio();
			if(this.tipoPdD==null){
				this.tipoPdD = Costanti.PDD_TIPOLOGIA_ESTERNA;
			}
			this.protocolloDefault = loaderProperties.getProtocolloDefault();

			// Impostazioni grafiche
			this.loaderNomeSintesi = loaderProperties.getConsoleNomeSintesi();
			this.loaderNomeEsteso = loaderProperties.getConsoleNomeEsteso();
			this.loaderCSS = loaderProperties.getConsoleCSS();
			this.loaderLanguage = loaderProperties.getConsoleLanguage();
			this.consoleLunghezzaLabel = loaderProperties.getConsoleLunghezzaLabel();
			this.logoHeaderImage = loaderProperties.getLogoHeaderImage();
			this.logoHeaderLink = loaderProperties.getLogoHeaderLink();
			this.logoHeaderTitolo = loaderProperties.getLogoHeaderTitolo();
			this.visualizzaLinkHomeHeader = loaderProperties.visualizzaLinkHomeHeader;
			
		} catch (java.lang.Exception e) {
			LoaderCore.log.error("[OpenSPCoopLoader::initCore] Impossibile leggere i dati dal file loader.properties:" + e.toString());
			throw new Exception("[OpenSPCoopLoader::initCore] Impossibile leggere i dati dal file loader.properties:" + e.toString());
		} 
		
		this.xmlUtils = XMLUtils.DEFAULT;
	}
	
	public LoaderCore(LoaderCore core) throws Exception{
		// RegistroServizi
		this.dataSourceRegistroServizi = core.dataSourceRegistroServizi;
		this.tipoDatabaseRegistroServizi = core.tipoDatabaseRegistroServizi;
		this.ctxDatasourceRegistroServizi = core.ctxDatasourceRegistroServizi;

		// ConfigurazionePdD
		this.dataSourceConfigurazionePdD = core.dataSourceConfigurazionePdD;
		this.tipoDatabaseConfigurazionePdD = core.tipoDatabaseConfigurazionePdD;
		this.ctxDatasourceConfigurazionePdD = core.ctxDatasourceConfigurazionePdD;	
		
		this.nomePdDOperativaCtrlstatSinglePdD = core.nomePdDOperativaCtrlstatSinglePdD;	
		if(this.nomePdDOperativaCtrlstatSinglePdD==null){
			this.nomePdDOperativaCtrlstatSinglePdD = this.getNomePddOperativa();
		}
		this.gestioneSoggetti = core.gestioneSoggetti;
		this.mantieniFruitoriServizi = core.mantieniFruitoriServizi;
		this.searchUserIntoRegistro = core.searchUserIntoRegistro;
		this.statoAccordo = core.statoAccordo;
		this.tipoPdD = core.tipoPdD;
		if(this.tipoPdD==null){
			this.tipoPdD = Costanti.PDD_TIPOLOGIA_ESTERNA;
		}
		this.protocolloDefault = core.protocolloDefault;

		// Impostazioni grafiche
		this.loaderNomeSintesi =core.loaderNomeSintesi;
		this.loaderNomeEsteso = core.loaderNomeEsteso;
		this.loaderNomeEstesoSuffix = core.loaderNomeEstesoSuffix;
		this.loaderCSS = core.loaderCSS;
		this.loaderLanguage = core.loaderLanguage;
		this.consoleLunghezzaLabel = core.consoleLunghezzaLabel;
		this.logoHeaderImage = core.logoHeaderImage;
		this.logoHeaderLink = core.logoHeaderLink;
		this.logoHeaderTitolo =  core.logoHeaderTitolo;
		this.visualizzaLinkHomeHeader = core.visualizzaLinkHomeHeader;
		
		this.xmlUtils = XMLUtils.DEFAULT;
	}

	public static Logger getLog() {
		return LoaderCore.log;
	}

	public String getDataSourceRegistroServizi() {
		return this.dataSourceRegistroServizi;
	}

	public java.util.Properties getCtxDatasourceRegistroServizi() {
		return this.ctxDatasourceRegistroServizi;
	}

	public String getTipoDatabaseRegistroServizi() {
		return this.tipoDatabaseRegistroServizi;
	}

	public String getDataSourceConfigurazionePdD() {
		return this.dataSourceConfigurazionePdD;
	}

	public java.util.Properties getCtxDatasourceConfigurazionePdD() {
		return this.ctxDatasourceConfigurazionePdD;
	}

	public String getTipoDatabaseConfigurazionePdD() {
		return this.tipoDatabaseConfigurazionePdD;
	}

	public String getNomePdDOperativaCtrlstatSinglePdD() {
		return this.nomePdDOperativaCtrlstatSinglePdD;
	}

	public boolean isGestioneSoggetti() {
		return this.gestioneSoggetti;
	}

	public boolean isMantieniFruitoriServizi() {
		return this.mantieniFruitoriServizi;
	}

	public StatiAccordo getStatoAccordo() {
		return this.statoAccordo;
	}
	
	public String getTipoPdD() {
		return this.tipoPdD;
	}
	
	public String getProtocolloDefault() {
		return this.protocolloDefault;
	}
	
	
	
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
							GestoreJNDI jndi = new GestoreJNDI(this.ctxDatasourceRegistroServizi);
							DataSource ds = (DataSource) jndi.lookup(this.dataSourceRegistroServizi);
							con = ds.getConnection();
							vInfo.init(LoaderCore.log, con, tipoDB);
							this.versionInfo = vInfo;
						} 
						catch(Exception e) {
							LoaderCore.log.error(e.getMessage(),e);
						}
						finally {
							try{
								con.close();
							}catch(Exception eClose){}
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
			initInfoVersion(session, this.tipoDatabaseRegistroServizi);
		}
		return this.versionInfo;
	}
	
	
	public boolean existsUserRegistro(String login) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "existsUserRegistro";
		
		try {
			// prendo una connessione
			GestoreJNDI jndi = new GestoreJNDI(this.ctxDatasourceRegistroServizi);
			DataSource ds = (DataSource) jndi.lookup(this.dataSourceRegistroServizi);
			con = ds.getConnection();
			// istanzio il driver
			DriverUsersDB driver = new DriverUsersDB(con, this.tipoDatabaseRegistroServizi, LoaderCore.log);
			
			return driver.existsUser(login);

		} catch (Exception e) {
			LoaderCore.log.error("[LoaderCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverUsersDBException("[LoaderCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			try{
				con.close();
			}catch(Exception eClose){}
		}

	}
	
	public boolean existsUserConfig(String login) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "existsUserConfig";
		
		try {
			// prendo una connessione
			GestoreJNDI jndi = new GestoreJNDI(this.ctxDatasourceConfigurazionePdD);
			DataSource ds = (DataSource) jndi.lookup(this.dataSourceConfigurazionePdD);
			con = ds.getConnection();
			// istanzio il driver
			DriverUsersDB driver = new DriverUsersDB(con, this.tipoDatabaseConfigurazionePdD, LoaderCore.log);
			
			return driver.existsUser(login);

		} catch (Exception e) {
			LoaderCore.log.error("[LoaderCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverUsersDBException("[LoaderCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			try{
				con.close();
			}catch(Exception eClose){}
		}

	}
	
	public User getUserRegistro(String login) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "getUserRegistro";
		
		try {
			// prendo una connessione
			GestoreJNDI jndi = new GestoreJNDI(this.ctxDatasourceRegistroServizi);
			DataSource ds = (DataSource) jndi.lookup(this.dataSourceRegistroServizi);
			con = ds.getConnection();
			// istanzio il driver
			DriverUsersDB driver = new DriverUsersDB(con, this.tipoDatabaseRegistroServizi, LoaderCore.log);
			
			return driver.getUser(login);

		} catch (Exception e) {
			LoaderCore.log.error("[LoaderCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverUsersDBException("[LoaderCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			try{
				con.close();
			}catch(Exception eClose){}
		}

	}
	
	public User getUserConfig(String login) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "getUserConfig";
		
		try {
			// prendo una connessione
			GestoreJNDI jndi = new GestoreJNDI(this.ctxDatasourceConfigurazionePdD);
			DataSource ds = (DataSource) jndi.lookup(this.dataSourceConfigurazionePdD);
			con = ds.getConnection();
			// istanzio il driver
			DriverUsersDB driver = new DriverUsersDB(con, this.tipoDatabaseConfigurazionePdD, LoaderCore.log);
			
			return driver.getUser(login);

		} catch (Exception e) {
			LoaderCore.log.error("[LoaderCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverUsersDBException("[LoaderCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			try{
				con.close();
			}catch(Exception eClose){}
		}

	}
	
	public String getNomePddOperativa() throws Exception {
		Connection con = null;
		String nomeMetodo = "getNomePddOperativa";
		
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		try {
			// prendo una connessione
			GestoreJNDI jndi = new GestoreJNDI(this.ctxDatasourceRegistroServizi);
			DataSource ds = (DataSource) jndi.lookup(this.dataSourceRegistroServizi);
			con = ds.getConnection();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.tipoDatabaseRegistroServizi);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addWhereCondition("tipo=?");
			sqlQueryObject.setANDLogicOperator(true);
			
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, Costanti.PDD_TIPOLOGIA_OPERATIVA);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				String nomePdd = risultato.getString("nome");
				if (risultato.next()) {
					throw new Exception("Esiste piu' di una pdd con tipologia '"+Costanti.PDD_TIPOLOGIA_OPERATIVA+"'");
				}
				return nomePdd;
			}
			else{
				throw new Exception("Non esiste una pdd con tipologia '"+Costanti.PDD_TIPOLOGIA_OPERATIVA+"'");
			}
			
		} catch (Exception e) {
			LoaderCore.log.error("[LoaderCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new Exception("[LoaderCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			try{
				risultato.close();
			}catch(Throwable eClose){}
			try{
				stmt.close();
			}catch(Throwable eClose){}
			try{
				con.close();
			}catch(Throwable eClose){}
		}
	}
	
	public byte[] readBytes(FormFile ff) throws Exception{
		byte[] data = ff.getFileData();
		// Leggo il data[] in modo da correggere gli eventuali entity imports
		Document d = this.xmlUtils.newDocument(data);
		String xml = this.xmlUtils.toString(d, true);
		xml = org.openspcoop2.utils.Utilities.eraserXmlAttribute(xml, "xml:base=");
		data = xml.getBytes();
		return data;
	}


}
