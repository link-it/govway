/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.registry.driver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.IDriverBYOK;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.core.registry.Property;
import org.openspcoop2.core.registry.driver.ConnettorePropertiesUtilities;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_connettoriDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_connettoriDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_connettoriDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected Connettore getConnettore(long idConnettore) throws DriverRegistroServiziException, DriverRegistroServiziNotFound {
		String nomeMetodo = "getConnettore(id)";
		
		Connection con = null;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			Connettore connettore = getConnettore(idConnettore, con);
			if(connettore==null) {
				throw new DriverRegistroServiziNotFound("Connettore con id '"+idConnettore+"' non esistente");
			}
			return connettore;
			
		} catch (Exception qe) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(con);
		}
	}
	protected Connettore getConnettore(String nomeConnettore) throws DriverRegistroServiziException {
		String nomeMetodo = "getConnettore(nome)";
		
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("nome_connettore=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomeConnettore);
			risultato = stmt.executeQuery();

			Long idConnettore = null;
			if (risultato.next()) {
				idConnettore = risultato.getLong("id");
			}
			else {
				throw new DriverRegistroServiziNotFound("Connettore con nome '"+nomeConnettore+"' non esistente");
			}
					
			Connettore connettore = getConnettore(idConnettore, con);
			if(connettore==null) {
				throw new DriverRegistroServiziNotFound("Connettore con id '"+idConnettore+"' non esistente");
			}
			return connettore;
			
		} catch (Exception qe) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(con);
		}
	}
	
	protected Connettore getConnettore(long idConnettore, Connection connection) throws DriverRegistroServiziException {

		Connettore connettore = null;

		// accedo alla tab regserv_connettori

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			IDriverBYOK driverBYOK = this.driver.getDriverUnwrapBYOK();
			
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			if (rs.next()) {
				String endpoint = rs.getString(CostantiDB.CONNETTORI_COLUMN_ENDPOINT_TYPE);
				if (endpoint == null || endpoint.equals("") || endpoint.equals(TipiConnettore.DISABILITATO.getNome())) {
					connettore = new Connettore();
					connettore.setNome(rs.getString(CostantiDB.CONNETTORI_COLUMN_NOME));
					connettore.setTipo(TipiConnettore.DISABILITATO.getNome());
					connettore.setId(idConnettore);

				} else {
					Property prop = null;
					connettore = new Connettore();
					connettore.setNome(rs.getString(CostantiDB.CONNETTORI_COLUMN_NOME));
					connettore.setTipo(endpoint);
					//l'id del connettore e' quello passato come parametro
					connettore.setId(idConnettore);

					// Debug
					if(rs.getInt("debug")==1){
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_DEBUG);
						prop.setValore("true");
						connettore.addProperty(prop);
					}

					// Proxy
					readConnettoreProxy(rs, connettore, driverBYOK);
					
					// Tempi Risposta
					readConnettoreTempiRisposta(rs, connettore);
					
					// transfer_mode
					readConnettoreTransferMode(rs, connettore);
					
					// redirect_mode
					readConnettoreRedirectMode(rs, connettore);	
					
					// http_impl
					readConnettoreHttpImpl(rs, connettore);	
								
					// token policy
					String tokenPolicy = rs.getString("token_policy");
					if(tokenPolicy!=null && !"".equals(tokenPolicy)){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_TOKEN_POLICY);
						prop.setValore(tokenPolicy.trim());
						connettore.addProperty(prop);
						
					}
					
					// api key
					readAutenticazioneApiKey(rs, connettore, driverBYOK);
					
					if (endpoint.equals(CostantiDB.CONNETTORE_TIPO_HTTP)) {
						readConnettoreHttp(rs, connettore, driverBYOK);
					} else if (endpoint.equals(TipiConnettore.JMS.getNome())){
						readConnettoreJms(rs, connettore, driverBYOK);
					}else if(endpoint.equals(TipiConnettore.NULL.getNome())){
						//nessuna proprieta per connettore null
					}else if(endpoint.equals(TipiConnettore.NULLECHO.getNome())){
						//nessuna proprieta per connettore nullEcho
					}else if (!endpoint.equals(TipiConnettore.DISABILITATO.getNome())) {
						if(rs.getLong("custom")==1){
							// connettore custom
							readPropertiesConnettoreCustom(idConnettore,connettore,connection,driverBYOK);
							connettore.setCustom(true);
						}
						else{
							// legge da file properties
							connettore.setPropertyList(ConnettorePropertiesUtilities.getPropertiesConnettore(endpoint,connection,this.driver.tipoDB));
						}
					}

				}
			}
			
			// Extended Info
			this.readPropertiesConnettoreExtendedInfo(idConnettore,connettore,connection,driverBYOK);
			
			return connettore;
		} catch (SQLException sqle) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getConnettore] SQLException : " + sqle.getMessage(),sqle);
		} catch (CoreException e) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getConnettore] CoreException : " + e.getMessage(),e);
		}catch (Exception sqle) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getConnettore] Exception : " + sqle.getMessage(),sqle);
		}finally {
			// chiudo lo statement e resultset
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	
	private static void readConnettoreProxy(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		if(rs.getInt("proxy")==1){
			
			String tmp = rs.getString("proxy_type");
			if(tmp!=null && !"".equals(tmp)){
				Property prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_PROXY_TYPE);
				prop.setValore(tmp.trim());
				connettore.addProperty(prop);
			}
			
			tmp = rs.getString("proxy_hostname");
			if(tmp!=null && !"".equals(tmp)){
				Property prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
				prop.setValore(tmp.trim());
				connettore.addProperty(prop);
			}
			
			tmp = rs.getString("proxy_port");
			if(tmp!=null && !"".equals(tmp)){
				Property prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_PROXY_PORT);
				prop.setValore(tmp.trim());
				connettore.addProperty(prop);
			}
			
			readConnettoreProxyCredentials(rs, connettore, driverBYOK);
		}
	}
	private static void readConnettoreProxyCredentials(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		String tmp = rs.getString("proxy_username");
		if(tmp!=null && !"".equals(tmp)){
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_PROXY_USERNAME);
			prop.setValore(tmp.trim());
			connettore.addProperty(prop);
		}
		
		tmp = rs.getString("proxy_password");
		String encValue = rs.getString("enc_proxy_password");
		if(tmp!=null && !"".equals(tmp)){
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_PROXY_PASSWORD);
			
			if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
				if(driverBYOK!=null) {
					prop.setValore(driverBYOK.unwrapAsString(encValue));
				}
				else {
					prop.setValore(encValue);
				}
			}
			else {
				prop.setValore(tmp.trim());
			}
			
			connettore.addProperty(prop);
		}
	}
	
	private static void readConnettoreTempiRisposta(ResultSet rs, Connettore connettore) throws SQLException {
		int connectionTimeout = rs.getInt("connection_timeout");
		if(connectionTimeout>0){
			
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
			prop.setValore(connectionTimeout+"");
			connettore.addProperty(prop);
			
		}
		int readTimeout = rs.getInt("read_timeout");
		if(readTimeout>0){
			
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
			prop.setValore(readTimeout+"");
			connettore.addProperty(prop);
			
		}
		int avgResponseTime = rs.getInt("avg_response_time");
		if(avgResponseTime>0){
			
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
			prop.setValore(avgResponseTime+"");
			connettore.addProperty(prop);
			
		}
	}

	private static void readConnettoreTransferMode(ResultSet rs, Connettore connettore) throws SQLException {
		String transferMode = rs.getString("transfer_mode");
		if(transferMode!=null && !"".equals(transferMode)){
			
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
			prop.setValore(transferMode.trim());
			connettore.addProperty(prop);
			
			transferMode = rs.getString("transfer_mode_chunk_size");
			if(transferMode!=null && !"".equals(transferMode)){
				prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
				prop.setValore(transferMode.trim());
				connettore.addProperty(prop);
			}
		}
	}
	
	private static void readConnettoreRedirectMode(ResultSet rs, Connettore connettore) throws SQLException {
		String redirectMode = rs.getString("redirect_mode");
		if(redirectMode!=null && !"".equals(redirectMode)){
			
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_FOLLOW);
			prop.setValore(redirectMode.trim());
			connettore.addProperty(prop);
			
			redirectMode = rs.getString("redirect_max_hop");
			if(redirectMode!=null && !"".equals(redirectMode)){
				prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
				prop.setValore(redirectMode.trim());
				connettore.addProperty(prop);
			}
		}
	}
	
	private static void readConnettoreHttpImpl(ResultSet rs, Connettore connettore) throws SQLException {
		String httpImpl = rs.getString("http_impl");
		if(httpImpl!=null && !"".equals(httpImpl)){
			
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_HTTP_IMPL);
			prop.setValore(httpImpl.trim());
			connettore.addProperty(prop);
			
		}
	}
	
	private static void readAutenticazioneApiKey(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		String apiKey = rs.getString("api_key");
		if(apiKey!=null && !"".equals(apiKey)){
			
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_APIKEY);
			if(driverBYOK!=null) {
				prop.setValore(driverBYOK.unwrapAsString(apiKey));
			}
			else {
				prop.setValore(apiKey);
			}
			connettore.addProperty(prop);
			
			String apiKeyHeader = rs.getString("api_key_header");
			if(apiKeyHeader!=null && !"".equals(apiKeyHeader)){
				prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_APIKEY_HEADER);
				prop.setValore(apiKeyHeader.trim());
				connettore.addProperty(prop);
			}
			
			
			String appId = rs.getString("app_id");
			if(appId!=null && !"".equals(appId)){
				
				prop = new Property();
				prop.setNome(CostantiDB.CONNETTORE_APIKEY_APPID);
				prop.setValore(appId);
				connettore.addProperty(prop);
				
				String appIdHeader = rs.getString("app_id_header");
				if(appIdHeader!=null && !"".equals(appIdHeader)){
					prop = new Property();
					prop.setNome(CostantiDB.CONNETTORE_APIKEY_APPID_HEADER);
					prop.setValore(appIdHeader.trim());
					connettore.addProperty(prop);
				}
			}
		}
	}
	
	private static void readConnettoreHttp(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws DriverRegistroServiziException, SQLException, UtilsException {
		// url
		String value = rs.getString("url");
		if(value!=null)
			value = value.trim();
		if(value == null || "".equals(value) || " ".equals(value)){
			throw new DriverRegistroServiziException("Connettore di tipo http possiede una url non definita");
		}
		Property prop = new Property();
		prop.setNome(CostantiDB.CONNETTORE_HTTP_LOCATION);
		prop.setValore(value);
		connettore.addProperty(prop);
		
		// user
		String usr = rs.getString("utente");
		if (usr != null && !usr.trim().equals("")) {
			prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_USER);
			prop.setValore(usr);
			connettore.addProperty(prop);
		}
		// password
		String pwd = rs.getString("password");
		String encValue = rs.getString("enc_password");
		if (pwd != null && !pwd.trim().equals("")) {
			prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_PWD);
			
			if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
				if(driverBYOK!=null) {
					prop.setValore(driverBYOK.unwrapAsString(encValue));
				}
				else {
					prop.setValore(encValue);
				}
			}
			else {
				prop.setValore(pwd);
			}
			
			connettore.addProperty(prop);
		}
	}
	
	private static void readConnettoreJms(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws DriverRegistroServiziException, SQLException, UtilsException {
		// nome coda/topic
		String value = rs.getString("nome");
		if(value!=null)
			value = value.trim();
		if(value == null || "".equals(value) || " ".equals(value)){
			throw new DriverRegistroServiziException("Connettore di tipo jms possiede il nome della coda/topic non definito");
		}
		Property prop = new Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_NOME);
		prop.setValore(value);
		connettore.addProperty(prop);

		// tipo
		value = rs.getString("tipo");
		if(value!=null)
			value = value.trim();
		if(value == null || "".equals(value) || " ".equals(value)){
			throw new DriverRegistroServiziException("Connettore di tipo jms possiede il tipo della coda non definito");
		}
		prop = new Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_TIPO);
		prop.setValore(value);
		connettore.addProperty(prop);

		// connection-factory
		value = rs.getString("connection_factory");
		if(value!=null)
			value = value.trim();
		if(value == null || "".equals(value) || " ".equals(value)){
			throw new DriverRegistroServiziException("Connettore di tipo jms non possiede la definizione di una Connection Factory");
		}
		prop = new Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY);
		prop.setValore(value);
		connettore.addProperty(prop);

		// send_as
		value = rs.getString("send_as");
		if(value!=null)
			value = value.trim();
		if(value == null || "".equals(value) || " ".equals(value)){
			throw new DriverRegistroServiziException("Connettore di tipo jms possiede il tipo dell'oggetto JMS non definito");
		}
		prop = new Property();
		prop.setNome(CostantiDB.CONNETTORE_JMS_SEND_AS);
		prop.setValore(value);
		connettore.addProperty(prop);

		readConnettoreJmsCredentials(rs, connettore, driverBYOK);
		
		readConnettoreJmsContext(rs, connettore);
	}
	private static void readConnettoreJmsCredentials(ResultSet rs, Connettore connettore, IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		// user
		String usr = rs.getString("utente");
		if (usr != null && !usr.trim().equals("")) {
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_USER);
			prop.setValore(usr);
			connettore.addProperty(prop);
		}
		// password
		String pwd = rs.getString("password");
		String encValue = rs.getString("enc_password");
		if (pwd != null && !pwd.trim().equals("")) {
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_PWD);
			
			if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
				if(driverBYOK!=null) {
					prop.setValore(driverBYOK.unwrapAsString(encValue));
				}
				else {
					prop.setValore(encValue);
				}
			}
			else {
				prop.setValore(pwd);
			}
			
			connettore.addProperty(prop);
		}
	}
	private static void readConnettoreJmsContext(ResultSet rs, Connettore connettore) throws SQLException {
		// context-java.naming.factory.initial
		String initcont = rs.getString("initcont");
		if (initcont != null && !initcont.trim().equals("")) {
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
			prop.setValore(initcont);
			connettore.addProperty(prop);
		}
		// context-java.naming.factory.url.pkgs
		String urlpkg = rs.getString("urlpkg");
		if (urlpkg != null && !urlpkg.trim().equals("")) {
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
			prop.setValore(urlpkg);
			connettore.addProperty(prop);
		}
		// context-java.naming.provider.url
		String provurl = rs.getString("provurl");
		if (provurl != null && !provurl.trim().equals("")) {
			Property prop = new Property();
			prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
			prop.setValore(provurl);
			connettore.addProperty(prop);
		}
	}
	
	
	protected void readPropertiesConnettoreCustom(long idConnettore, Connettore connettore, Connection connection,
			IDriverBYOK driverBYOK) throws DriverRegistroServiziException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			while (rs.next()) {
				processPropertiesConnettoreCustom(rs, connettore, driverBYOK);
			}

			rs.close();
			stm.close();

		} catch (SQLException sqle) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreCustom] SQLException : " + sqle.getMessage(),sqle);
		}catch (Exception sqle) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreCustom] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	private static void processPropertiesConnettoreCustom(ResultSet rs, Connettore connettore, 
			IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		String nome = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME);
		String valore = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE);
		String encValue = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ENC_VALUE);

		if(CostantiDB.CONNETTORE_DEBUG.equals(nome)){ // lo posso aver aggiunto prima
			boolean found = false;
			for (int i = 0; i < connettore.sizePropertyList(); i++) {
				if(CostantiDB.CONNETTORE_DEBUG.equals(connettore.getProperty(i).getNome())){
					// already exists
					found = true;
					break;
				}
			}
			if(found){
				return; // è gia stato aggiunto.
			}
		}
		
		Property prop = new Property();
		prop.setNome(nome);
		if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
			if(driverBYOK!=null) {
				prop.setValore(driverBYOK.unwrapAsString(encValue));
			}
			else {
				prop.setValore(encValue);
			}
		}
		else {
			prop.setValore(valore);
		}
		connettore.addProperty(prop);
	}
	
	protected void readPropertiesConnettoreExtendedInfo(long idConnettore, Connettore connettore, Connection connection,
			IDriverBYOK driverBYOK) throws DriverRegistroServiziException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			sqlQueryObject.addWhereLikeCondition(CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME, CostantiConnettori.CONNETTORE_EXTENDED_PREFIX+"%");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			while (rs.next()) {
				processPropertiesConnettoreExtendedInfo(rs, connettore, 
						driverBYOK);
			}

			rs.close();
			stm.close();

		} catch (SQLException sqle) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreExtendedInfo] SQLException : " + sqle.getMessage(),sqle);
		}catch (Exception sqle) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreExtendedInfo] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
		}
	}
	private static void processPropertiesConnettoreExtendedInfo(ResultSet rs, Connettore connettore, 
			IDriverBYOK driverBYOK) throws SQLException, UtilsException {
		String nome = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_NAME);
		String valore = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_VALUE);
		String encValue = rs.getString(CostantiDB.CONNETTORI_CUSTOM_COLUMN_ENC_VALUE);

		// Le proprietà sono già state inserite in caso di connettore custom
		boolean found = false;
		for (int i = 0; i < connettore.sizePropertyList(); i++) {
			if(nome.equals(connettore.getProperty(i).getNome())){
				// already exists
				found = true;
				break;
			}
		}
		if(found){
			return; // è gia stato aggiunto.
		}
		
		Property prop = new Property();
		prop.setNome(nome);
		if(encValue!=null && StringUtils.isNotEmpty(encValue)) {
			if(driverBYOK!=null) {
				prop.setValore(driverBYOK.unwrapAsString(encValue));
			}
			else {
				prop.setValore(encValue);
			}
		}
		else {
			prop.setValore(valore);
		}
		connettore.addProperty(prop);
	}

	protected Property[] getPropertiesConnettore(String nomeConnettore) throws DriverRegistroServiziException {
		Connection con = null;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPropertiesConnettore");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getPropertiesConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;
		return getPropertiesConnettore(nomeConnettore,con);
	}
	protected Property[] getPropertiesConnettore(String nomeConnettore, Connection connection) throws DriverRegistroServiziException {
		try {
			List<Property> l = ConnettorePropertiesUtilities.getPropertiesConnettore(nomeConnettore, connection,this.driver.tipoDB);
			return l!=null && !l.isEmpty() ? l.toArray(new Property[1]) : null;
		} catch (CoreException e) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getPropertiesConnettore] CoreException : " + e.getMessage(),e);
		}
	}
	
	protected void createConnettore(Connettore connettore) throws DriverRegistroServiziException {
		if (connettore == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDConnettore type = 1");
			// creo connettore
			DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(1, connettore, con, this.driver.getDriverWrapBYOK());

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createConnettore] Errore durante la creazione del connettore : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void updateConnettore(Connettore connettore) throws DriverRegistroServiziException {
		if (connettore == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDConnettore type = 2");
			// update connettore
			DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(2, connettore, con, this.driver.getDriverWrapBYOK());

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateConnettore] Errore durante l'aggiornamento del connettore : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deleteConnettore(Connettore connettore) throws DriverRegistroServiziException {
		if (connettore == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDConnettore type = 3");
			// delete connettore
			DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(3, connettore, con, this.driver.getDriverWrapBYOK());

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteConnettore] Errore durante la rimozione del connettore : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
}
