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


package org.openspcoop2.web.loader.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.apache.struts.upload.FormFile;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.constants.StatiAccordo;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.GestoreJNDI;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.xml.AbstractXMLUtils;
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
	private String loaderIMGNomeApplicazione = null;
	private String loaderLanguage = null;
	private boolean loaderUsaIMGNomeApplicazione = true;
	public String getLoaderNomeSintesi() {
		return this.loaderNomeSintesi;
	}
	public String getLoaderNomeEsteso() {
		if(this.loaderNomeEstesoSuffix!=null){
			return this.loaderNomeEsteso+this.loaderNomeEstesoSuffix;
		}
		else{
			return this.loaderNomeEsteso;
		}
	}
	public String getLoaderCSS() {
		return this.loaderCSS;
	}
	public String getLoaderIMGNomeApplicazione() {
		return this.loaderIMGNomeApplicazione;
	}
	public String getLoaderLanguage() {
		return this.loaderLanguage;
	}
	public boolean isLoaderUsaIMGNomeApplicazione(){
		return this.loaderUsaIMGNomeApplicazione;
	}
	
	private String nomePdDOperativaCtrlstatSinglePdD = null;
	public LoaderCore() throws Exception{
		// Log4J caricato tramite LoaderStartup
		LoaderCore.log = LoggerWrapperFactory.getLogger("openspcoop2.loader");

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
			this.loaderNomeEstesoSuffix = loaderProperties.getConsoleNomeEstesoSuffix();
			this.loaderCSS = loaderProperties.getConsoleCSS();
			this.loaderIMGNomeApplicazione = loaderProperties.getConsoleImmagineNomeApplicazione();
			this.loaderLanguage = loaderProperties.getConsoleLanguage();
			this.loaderUsaIMGNomeApplicazione = loaderProperties.isUsaConsoleImmagineNomeApplicazione();
			
		} catch (java.lang.Exception e) {
			LoaderCore.log.error("[OpenSPCoopLoader::initCore] Impossibile leggere i dati dal file loader.properties:" + e.toString());
			throw new Exception("[OpenSPCoopLoader::initCore] Impossibile leggere i dati dal file loader.properties:" + e.toString());
		} 
		
		this.xmlUtils = XMLUtils.getInstance();
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
	
	
	public boolean existsUserRegistro(String login) throws DriverUsersDBException {
		Connection con = null;
		String nomeMetodo = "existsUserRegistro";
		
		try {
			// prendo una connessione
			GestoreJNDI jndi = new GestoreJNDI(this.ctxDatasourceRegistroServizi);
			DataSource ds = (DataSource) jndi.lookup(this.dataSourceRegistroServizi);
			con = ds.getConnection();
			// istanzio il driver
			DriverUsersDB driver = new DriverUsersDB(con, this.tipoDatabaseRegistroServizi);
			
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
			DriverUsersDB driver = new DriverUsersDB(con, this.tipoDatabaseConfigurazionePdD);
			
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
			DriverUsersDB driver = new DriverUsersDB(con, this.tipoDatabaseRegistroServizi);
			
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
			DriverUsersDB driver = new DriverUsersDB(con, this.tipoDatabaseConfigurazionePdD);
			
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
			}catch(Exception eClose){}
			try{
				stmt.close();
			}catch(Exception eClose){}
			try{
				con.close();
			}catch(Exception eClose){}
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
