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
package org.openspcoop2.core.config.driver.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.byok.IDriverBYOK;
import org.openspcoop2.core.config.Connettore;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.driver.ConnettorePropertiesUtilities;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_connettoriDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_connettoriDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_connettoriDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	
	protected void createConnettore(Connettore connettore) throws DriverConfigurazioneException {
		if (connettore == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDConnettore type = 1");
			// creo connettore
			DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(1, connettore, con, this.driver.getDriverWrapBYOK());

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createConnettore] Errore durante la creazione del connettore : " + qe.getMessage(),qe);
		} finally {
			this.driver.closeConnection(error,con);
		}
	}

	protected void updateConnettore(Connettore connettore) throws DriverConfigurazioneException {
		if (connettore == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDConnettore type = 2");
			// update connettore
			DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(2, connettore, con, this.driver.getDriverWrapBYOK());

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateConnettore] Errore durante l'aggiornamento del connettore : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void deleteConnettore(Connettore connettore) throws DriverConfigurazioneException {
		if (connettore == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConnettore] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteConnettore");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDConnettore type = 3");
			// delete connettore
			DriverConfigurazioneDB_connettoriLIB.CRUDConnettore(3, connettore, con, this.driver.getDriverWrapBYOK());

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteConnettore] Errore durante la rimozione del connettore : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<String> connettoriList() throws DriverConfigurazioneException {
		String nomeMetodo = "connettoriList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<String> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI_PROPERTIES);
			sqlQueryObject.addSelectField("nome_connettore");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			while (risultato.next())
				lista.add(risultato.getString("nome_connettore"));

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected Property[] getPropertiesConnettore(String nomeConnettore) throws DriverConfigurazioneException {
		Connection con = null;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPropertiesConnettore");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverDB::getPropertiesConnettore] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;
		return getPropertiesConnettore(nomeConnettore,con);
	}
	protected Property[] getPropertiesConnettore(String nomeConnettore, Connection connection) throws DriverConfigurazioneException {
		try {
			List<Property> cList = ConnettorePropertiesUtilities.getPropertiesConnettore(nomeConnettore, connection,this.driver.tipoDB);
			return cList.toArray(new Property[cList.size()]);
		} catch (Exception e) {
			throw new DriverConfigurazioneException("[DriverDB::getPropertiesConnettore] DriverConfigurazioneException : " + e.getMessage(),e);
		}
	}
	
	
	protected boolean isPolicyNegoziazioneTokenUsedInConnettore(String nome) throws DriverConfigurazioneException{
		String nomeMetodo = "isPolicyNegoziazioneTokenUsedInConnettore";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addWhereCondition("token_policy=?");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nome);
			risultato = stmt.executeQuery();

			return risultato.next();

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(error,con);
		}
	}
	
	
	protected Connettore getConnettore(long idConnettore) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		String nomeMetodo = "getConnettore(id)";
		
		Connection con = null;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			Connettore connettore = DriverConfigurazioneDB_connettoriLIB.getConnettore(idConnettore, con, this.driver.getDriverUnwrapBYOK());
			if(connettore==null) {
				throw new DriverConfigurazioneNotFound("Connettore con id '"+idConnettore+"' non esistente");
			}
						
			// Recupero anche eventuale username e password in invocazione servizio.
			readCredenzialiBasicConnettore(connettore, idConnettore, con);
			
			return connettore;
			
		} catch (Exception qe) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(con);
		}
	}
	protected Connettore getConnettore(String nomeConnettore) throws DriverConfigurazioneException {
		String nomeMetodo = "getConnettore(nome)";
		
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

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
				throw new DriverConfigurazioneNotFound("Connettore con nome '"+nomeConnettore+"' non esistente");
			}
					
			Connettore connettore = DriverConfigurazioneDB_connettoriLIB.getConnettore(idConnettore, con, this.driver.getDriverUnwrapBYOK());
			if(connettore==null) {
				throw new DriverConfigurazioneNotFound("Connettore con id '"+idConnettore+"' non esistente");
			}
			
			risultato.close();
			stmt.close();
			
			// Recupero anche eventuale username e password in invocazione servizio.
			readCredenzialiBasicConnettore(connettore, idConnettore, con);
			
			return connettore;
			
		} catch (Exception qe) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(con);
		}
	}
	private void readCredenzialiBasicConnettore(Connettore connettore, long idConnettore,
			Connection con) throws DriverConfigurazioneException {
		String nomeMetodo = "getConnettore(nome)";
		
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		try {
			
			// Recupero anche eventuale username e password in invocazione servizio.
			if(!connettore.getProperties().containsKey(CostantiConnettori.CONNETTORE_USERNAME)) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField("utenteinv");
				sqlQueryObject.addSelectField("passwordinv");
				sqlQueryObject.addSelectField("enc_passwordinv");
				sqlQueryObject.addWhereCondition("id_connettore_inv=?");
				String queryString = sqlQueryObject.createSQLQuery();
				stmt = con.prepareStatement(queryString);
				stmt.setLong(1, idConnettore);
				risultato = stmt.executeQuery();
				String user = null;
				String encPassword = null;
				String plainPassword = null;
				if (risultato.next()) {
					user = risultato.getString("utenteinv");
					plainPassword = risultato.getString("passwordinv");
					encPassword = risultato.getString("enc_passwordinv");
				}
				else {
					// cerco come risposta asincrona
					risultato.close();
					stmt.close();
					
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
					sqlQueryObject.addSelectField("utenterisp");
					sqlQueryObject.addSelectField("passwordrisp");
					sqlQueryObject.addSelectField("enc_passwordrisp");
					sqlQueryObject.addWhereCondition("id_connettore_risp=?");
					queryString = sqlQueryObject.createSQLQuery();
					stmt = con.prepareStatement(queryString);
					stmt.setLong(1, idConnettore);
					risultato = stmt.executeQuery();
					if (risultato.next()) {
						user = risultato.getString("utenterisp");
						plainPassword = risultato.getString("passwordrisp");
						encPassword = risultato.getString("enc_passwordrisp");
					}
				}
				
				setUsernameProperty(user, connettore) ;
				setPasswordProperty(plainPassword, encPassword, connettore);

			}
			
		} catch (Exception qe) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

		}
	}
	
	private void setUsernameProperty(String user, Connettore connettore) {
		if(user!=null) {
			Property property = new Property();
			property.setNome(CostantiConnettori.CONNETTORE_USERNAME);
			property.setValore(user);
			connettore.addProperty(property);
		}
	}
	private void setPasswordProperty(String plainPassword, String encPassword, Connettore connettore) throws UtilsException {
		if(plainPassword!=null || encPassword!=null) {
			Property property = new Property();
			property.setNome(CostantiConnettori.CONNETTORE_PASSWORD);
			
			if(encPassword!=null && StringUtils.isNotEmpty(encPassword)) {
				IDriverBYOK driverBYOK = this.driver.getDriverUnwrapBYOK();
				if(driverBYOK!=null) {
					property.setValore(driverBYOK.unwrapAsString(encPassword));
				}
				else {
					property.setValore(encPassword);
				}
			}
			else {
				property.setValore(plainPassword);
			}
		
			connettore.addProperty(property);
		}
	}
}
