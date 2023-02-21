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



package org.openspcoop2.core.registry.driver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	
	public DriverRegistroServiziDB_connettoriDriver(DriverRegistroServiziDB driver) {
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

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			Connettore connettore = getConnettore(idConnettore, con);
			if(connettore==null) {
				throw new DriverRegistroServiziNotFound("Connettore con id '"+idConnettore+"' non esistente");
			}
			return connettore;
			
		} catch (Exception qe) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
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

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

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
			try{
				if(risultato!=null) 
					risultato.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stmt!=null) 
					stmt.close();
			}catch (Exception e) {
				//ignore
			}

			try {
				if (this.driver.atomica) {
					this.driver.log.debug("rilascio connessioni...");
					con.close();
				}
			} catch (Exception e) {
				// ignore exception
			}
		}
	}
	
	protected Connettore getConnettore(long idConnettore, Connection connection) throws DriverRegistroServiziException {

		Connettore connettore = null;

		// accedo alla tab regserv_connettori

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			this.driver.log.debug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			if (rs.next()) {
				String endpoint = rs.getString("endpointtype");
				if (endpoint == null || endpoint.equals("") || endpoint.equals(TipiConnettore.DISABILITATO.getNome())) {
					connettore = new Connettore();
					connettore.setNome(rs.getString("nome_connettore"));
					connettore.setTipo(TipiConnettore.DISABILITATO.getNome());
					connettore.setId(idConnettore);

				} else {
					Property prop = null;
					connettore = new Connettore();
					connettore.setNome(rs.getString("nome_connettore"));
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
					if(rs.getInt("proxy")==1){
						
						String tmp = rs.getString("proxy_type");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_TYPE);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_hostname");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_HOSTNAME);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_port");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_PORT);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_username");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_USERNAME);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
						tmp = rs.getString("proxy_password");
						if(tmp!=null && !"".equals(tmp)){
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PROXY_PASSWORD);
							prop.setValore(tmp.trim());
							connettore.addProperty(prop);
						}
						
					}
					
					// Tempi Risposta
					int connectionTimeout = rs.getInt("connection_timeout");
					if(connectionTimeout>0){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT);
						prop.setValore(connectionTimeout+"");
						connettore.addProperty(prop);
						
					}
					int readTimeout = rs.getInt("read_timeout");
					if(readTimeout>0){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT);
						prop.setValore(readTimeout+"");
						connettore.addProperty(prop);
						
					}
					int avgResponseTime = rs.getInt("avg_response_time");
					if(avgResponseTime>0){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA);
						prop.setValore(avgResponseTime+"");
						connettore.addProperty(prop);
						
					}
					
					// transfer_mode
					String transferMode = rs.getString("transfer_mode");
					if(transferMode!=null && !"".equals(transferMode)){
						
						prop = new Property();
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
					
					// redirect_mode
					String redirectMode = rs.getString("redirect_mode");
					if(redirectMode!=null && !"".equals(redirectMode)){
						
						prop = new Property();
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
					
					// token policy
					String tokenPolicy = rs.getString("token_policy");
					if(tokenPolicy!=null && !"".equals(tokenPolicy)){
						
						prop = new Property();
						prop.setNome(CostantiDB.CONNETTORE_TOKEN_POLICY);
						prop.setValore(tokenPolicy.trim());
						connettore.addProperty(prop);
						
					}
					
					if (endpoint.equals(CostantiDB.CONNETTORE_TIPO_HTTP)) {

						// url
						String value = rs.getString("url");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverRegistroServiziException("Connettore di tipo http possiede una url non definita");
						}
						prop = new Property();
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
						if (pwd != null && !pwd.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PWD);
							prop.setValore(pwd);
							connettore.addProperty(prop);
						}

					} else if (endpoint.equals(TipiConnettore.JMS.getNome())){

						// nome coda/topic
						String value = rs.getString("nome");
						if(value!=null)
							value = value.trim();
						if(value == null || "".equals(value) || " ".equals(value)){
							throw new DriverRegistroServiziException("Connettore di tipo jms possiede il nome della coda/topic non definito");
						}
						prop = new Property();
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
						if (pwd != null && !pwd.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_PWD);
							prop.setValore(pwd);
							connettore.addProperty(prop);
						}
						// context-java.naming.factory.initial
						String initcont = rs.getString("initcont");
						if (initcont != null && !initcont.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL);
							prop.setValore(initcont);
							connettore.addProperty(prop);
						}
						// context-java.naming.factory.url.pkgs
						String urlpkg = rs.getString("urlpkg");
						if (urlpkg != null && !urlpkg.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG);
							prop.setValore(urlpkg);
							connettore.addProperty(prop);
						}
						// context-java.naming.provider.url
						String provurl = rs.getString("provurl");
						if (provurl != null && !provurl.trim().equals("")) {
							prop = new Property();
							prop.setNome(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL);
							prop.setValore(provurl);
							connettore.addProperty(prop);
						}

					}else if(endpoint.equals(TipiConnettore.NULL.getNome())){
						//nessuna proprieta per connettore null
					}else if(endpoint.equals(TipiConnettore.NULLECHO.getNome())){
						//nessuna proprieta per connettore nullEcho
					}else if (!endpoint.equals(TipiConnettore.DISABILITATO.getNome())) {
						if(rs.getLong("custom")==1){
							// connettore custom
							readPropertiesConnettoreCustom(idConnettore,connettore,connection);
							connettore.setCustom(true);
						}
						else{
							// legge da file properties
							Property[] ps = ConnettorePropertiesUtilities.getPropertiesConnettore(endpoint,connection,this.driver.tipoDB);
							List<Property> listCP = new ArrayList<Property>();
							if(ps!=null){
								for (int i = 0; i < ps.length; i++) {
									listCP.add(ps[i]);
								}
							}
							connettore.setPropertyList(listCP);
						}
					}

				}
			}
			
			// Extended Info
			this.readPropertiesConnettoreExtendedInfo(idConnettore,connettore,connection);
			
			return connettore;
		} catch (SQLException sqle) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getConnettore] SQLException : " + sqle.getMessage(),sqle);
		} catch (CoreException e) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getConnettore] CoreException : " + e.getMessage(),e);
		}catch (Exception sqle) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getConnettore] Exception : " + sqle.getMessage(),sqle);
		}finally {
			// chiudo lo statement e resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}

	protected void readPropertiesConnettoreCustom(long idConnettore, Connettore connettore, Connection connection) throws DriverRegistroServiziException {

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

			this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			while (rs.next()) {
				String nome = rs.getString("name");
				String valore = rs.getString("value");

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
						continue; // è gia stato aggiunto.
					}
				}
				
				Property prop = new Property();
				prop.setNome(nome);
				prop.setValore(valore);
				connettore.addProperty(prop);
			}

			rs.close();
			stm.close();

		} catch (SQLException sqle) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreCustom] SQLException : " + sqle.getMessage(),sqle);
		}catch (Exception sqle) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreCustom] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
	}
	
	protected void readPropertiesConnettoreExtendedInfo(long idConnettore, Connettore connettore, Connection connection) throws DriverRegistroServiziException {

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_CUSTOM);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_connettore = ?");
			sqlQueryObject.addWhereLikeCondition("name", CostantiConnettori.CONNETTORE_EXTENDED_PREFIX+"%");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = connection.prepareStatement(sqlQuery);
			stm.setLong(1, idConnettore);

			this.driver.log.debug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idConnettore));

			rs = stm.executeQuery();

			while (rs.next()) {
				String nome = rs.getString("name");
				String valore = rs.getString("value");

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
					continue; // è gia stato aggiunto.
				}
				
				Property prop = new Property();
				prop.setNome(nome);
				prop.setValore(valore);
				connettore.addProperty(prop);
			}

			rs.close();
			stm.close();

		} catch (SQLException sqle) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreExtendedInfo] SQLException : " + sqle.getMessage(),sqle);
		}catch (Exception sqle) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPropertiesConnettoreExtendedInfo] Exception : " + sqle.getMessage(),sqle);
		} finally {
			//Chiudo statement and resultset
			try{
				if(rs!=null) 
					rs.close();
			}catch (Exception e) {
				//ignore
			}
			try{
				if(stm!=null) 
					stm.close();
			}catch (Exception e) {
				//ignore
			}
		}
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
			return ConnettorePropertiesUtilities.getPropertiesConnettore(nomeConnettore, connection,this.driver.tipoDB);
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

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDConnettore type = 1");
			// creo connettore
			DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(1, connettore, con);

		} catch (DriverRegistroServiziException qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createConnettore] Errore durante la creazione del connettore : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createConnettore] Errore durante la creazione del connettore : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.driver.atomica) {
					this.driver.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.driver.atomica) {
					this.driver.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
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

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDConnettore type = 2");
			// update connettore
			DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(2, connettore, con);

		} catch (DriverRegistroServiziException qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateConnettore] Errore durante l'aggiornamento del connettore : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateConnettore] Errore durante l'aggiornamento del connettore : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.driver.atomica) {
					this.driver.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.driver.atomica) {
					this.driver.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
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

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDConnettore type = 3");
			// delete connettore
			DriverRegistroServiziDB_connettoriLIB.CRUDConnettore(3, connettore, con);

		} catch (DriverRegistroServiziException qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteConnettore] Errore durante la rimozione del connettore : " + qe.getMessage(),qe);
		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteConnettore] Errore durante la rimozione del connettore : " + qe.getMessage(),qe);
		} finally {

			try {
				if (error && this.driver.atomica) {
					this.driver.log.debug("eseguo rollback a causa di errori e rilascio connessioni...");
					con.rollback();
					con.setAutoCommit(true);
					con.close();

				} else if (!error && this.driver.atomica) {
					this.driver.log.debug("eseguo commit e rilascio connessioni...");
					con.commit();
					con.setAutoCommit(true);
					con.close();
				}

			} catch (Exception e) {
				// ignore exception
			}
		}
	}
}
