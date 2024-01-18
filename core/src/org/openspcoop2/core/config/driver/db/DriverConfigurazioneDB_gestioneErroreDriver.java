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
import java.sql.SQLException;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_gestioneErroreDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_gestioneErroreDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_gestioneErroreDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	


	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di cooperazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	protected GestioneErrore getGestioneErroreComponenteCooperazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getGestioneErrore(true);
	}

	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di Dominio per il componente di integrazione
	 *
	 * @return La gestione dell'errore
	 * 
	 */
	protected GestioneErrore getGestioneErroreComponenteIntegrazione() throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		return getGestioneErrore(false);
	}


	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di
	 * Dominio
	 * 
	 * @return La gestione dell'errore
	 * 
	 */
	private GestioneErrore getGestioneErrore(boolean cooperazione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		String sqlQuery = "";

		String tipoOperazione = "GestioneErrore per componente di Cooperazione";
		if(cooperazione==false)
			tipoOperazione = "GestioneErrore per componente di Integrazione";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getGestioneErrore("+cooperazione+")");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("["+tipoOperazione+"] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			GestioneErrore gestioneErrore = null;
			String nomeColonna = "id_ge_cooperazione";
			if(cooperazione==false)
				nomeColonna = "id_ge_integrazione";
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addSelectField(nomeColonna);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("eseguo query: " + DBUtils.formatSQLString(sqlQuery));
			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			if (rs.next()) {

				Long idGestioneErrore = rs.getLong(nomeColonna);
				if(idGestioneErrore>0){
					gestioneErrore = DriverConfigurazioneDB_gestioneErroreLIB.getGestioneErrore(idGestioneErrore, con);
				}

			} 

			rs.close();
			stm.close();

			if(gestioneErrore==null){
				throw new DriverConfigurazioneNotFound(tipoOperazione +" non presente.");
			}
			return gestioneErrore;

		} catch (SQLException se) {
			throw new DriverConfigurazioneException(tipoOperazione +" SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		}catch (Exception se) {
			throw new DriverConfigurazioneException(tipoOperazione +" Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

	}
	
	protected void createGestioneErroreComponenteCooperazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createGestioneErroreComponenteCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGestioneErroreComponenteCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		PreparedStatement pstmt = null;
		try {
			this.driver.logDebug("CRUDGestioneErrore type = 1");
			DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreComponenteCooperazione(CostantiDB.CREATE, gestione, con);

			// Aggiorno configurazione
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addUpdateField("id_ge_cooperazione", "?");
			String updateQuery = sqlQueryObject.createSQLUpdate();
			pstmt = con.prepareStatement(updateQuery);
			pstmt.setLong(1, gestione.getId());
			pstmt.execute();
			pstmt.close();

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGestioneErroreComponenteCooperazione] Errore: " + qe.getMessage(),qe);
		} finally {

			JDBCUtilities.closeResources(pstmt);

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Aggiorna le informazioni per la gestione dell'errore per il ComponenteCooperazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	protected void updateGestioneErroreComponenteCooperazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateGestioneErroreComponenteCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGestioneErroreComponenteCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDGestioneErrore type = 2");
			DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreComponenteCooperazione(CostantiDB.UPDATE, gestione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGestioneErroreComponenteCooperazione] Errore: " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Elimina le informazioni per la gestione dell'errore per il ComponenteCooperazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	protected void deleteGestioneErroreComponenteCooperazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteGestioneErroreComponenteCooperazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGestioneErroreComponenteCooperazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDGestioneErrore type = 3");
			DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreComponenteCooperazione(CostantiDB.DELETE, gestione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGestioneErroreComponenteCooperazione] Errore: " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Crea le informazioni per la gestione dell'errore per il ComponenteIntegrazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	protected void createGestioneErroreComponenteIntegrazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createGestioneErroreComponenteIntegrazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGestioneErroreComponenteIntegrazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		PreparedStatement pstmt = null;
		try {
			this.driver.logDebug("CRUDGestioneErrore type = 1");
			DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreComponenteIntegrazione(CostantiDB.CREATE, gestione, con);

			// Aggiorno configurazione
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.CONFIGURAZIONE);
			sqlQueryObject.addUpdateField("id_ge_integrazione", "?");
			String updateQuery = sqlQueryObject.createSQLUpdate();
			pstmt = con.prepareStatement(updateQuery);
			pstmt.setLong(1, gestione.getId());
			pstmt.execute();
			pstmt.close();

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createGestioneErroreComponenteIntegrazione] Errore: " + qe.getMessage(),qe);
		} finally {

			JDBCUtilities.closeResources(pstmt);

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Aggiorna le informazioni per la gestione dell'errore per il ComponenteIntegrazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	protected void updateGestioneErroreComponenteIntegrazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateGestioneErroreComponenteIntegrazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGestioneErroreComponenteIntegrazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDGestioneErrore type = 2");
			DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreComponenteIntegrazione(CostantiDB.UPDATE, gestione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateGestioneErroreComponenteIntegrazione] Errore: " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	/**
	 * Elimina le informazioni per la gestione dell'errore per il ComponenteIntegrazione
	 * 
	 * @param gestione
	 * @throws DriverConfigurazioneException
	 */
	protected void deleteGestioneErroreComponenteIntegrazione(GestioneErrore gestione) throws DriverConfigurazioneException{
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteGestioneErroreComponenteIntegrazione");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGestioneErroreComponenteIntegrazione] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDGestioneErrore type = 3");
			DriverConfigurazioneDB_gestioneErroreLIB.CRUDGestioneErroreComponenteIntegrazione(CostantiDB.DELETE, gestione, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteGestioneErroreComponenteIntegrazione] Errore: " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
}
