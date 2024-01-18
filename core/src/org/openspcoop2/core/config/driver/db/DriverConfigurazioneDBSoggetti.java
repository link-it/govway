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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.config.driver.FiltroRicercaSoggetti;
import org.openspcoop2.core.config.driver.IDServizioUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_soggettiDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDBSoggetti {

	private DriverConfigurazioneDB driver = null;

	private static final String ESEGUO_QUERY_PREFIX = "eseguo query : ";
	private static final String OPERAZIONE_ATOMICA_PREFIX = "operazione this.driver.atomica = ";
	
	protected DriverConfigurazioneDBSoggetti(DriverConfigurazioneDB driver) {
		this.driver = driver;
	}
	
	protected IDSoggetto getIdSoggetto(long idSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return getIdSoggetto(idSoggetto,null);
	}
	protected IDSoggetto getIdSoggetto(long idSoggetto,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		// conrollo consistenza
		if (idSoggetto <= 0)
			return null;

		IDSoggetto idSoggettoObject = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if(conParam!=null){
			con = conParam;
		}
		else if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getIdSoggetto(longId)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("DriverConfigurazioneDB::getIdSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.driver.logDebug(ESEGUO_QUERY_PREFIX + DriverConfigurazioneDBLib.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next()) {
				idSoggettoObject = new IDSoggetto();

				idSoggettoObject.setNome(rs.getString(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO));
				idSoggettoObject.setTipo(rs.getString(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO));

			}else{
				throw new DriverConfigurazioneNotFound("Nessun risultato trovat eseguendo: "+DriverConfigurazioneDBLib.formatSQLString(sqlQuery, idSoggetto));
			}

			return idSoggettoObject;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getIdSoggetto] SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound se) {
			throw se;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getIdSoggetto] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);
		}
	}
	
	
	
	
	
	protected Soggetto getSoggetto(IDSoggetto aSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if ((aSoggetto == null) || (aSoggetto.getNome() == null) || (aSoggetto.getTipo() == null)) {
			throw new DriverConfigurazioneException("[getSoggetto] Parametri Non Validi");
		}

		Soggetto soggetto = null;

		Connection con = null;

		String nomeSogg = aSoggetto.getNome();
		String tipoSogg = aSoggetto.getTipo();
		long idSoggetto= -1;
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggetto(idSoggetto)");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);

		try {
			idSoggetto=DBUtils.getIdSoggetto(nomeSogg, tipoSogg, con, this.driver.tipoDB,this.driver.tabellaSoggetti);
			if(idSoggetto==-1)
				throw new DriverConfigurazioneNotFound("Soggetto ["+aSoggetto.toString()+"] non esistente");

		} catch (DriverConfigurazioneNotFound se) {
			throw se;
		} catch (CoreException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] DriverException: " + se.getMessage(),se);
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception: " + se.getMessage(),se);
		}finally {

			this.driver.closeConnection(con);
		}


		soggetto=this.getSoggetto(idSoggetto);

		if(soggetto==null){
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Soggetto non Esistente.");
		}

		return soggetto;
	}

	protected void createSoggetto(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		if (soggetto == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSoggetto] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDSoggetto type = 1");
			// creo soggetto
			DriverConfigurazioneDBSoggettiLib.CRUDSoggetto(1, soggetto, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected void updateSoggetto(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		if (soggetto == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSoggetto] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDSoggetto type = 2");
			// UPDATE soggetto
			DriverConfigurazioneDBSoggettiLib.CRUDSoggetto(2, soggetto, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
		}finally {

			this.driver.closeConnection(error,con);
		}
	}

		
	protected void deleteSoggetto(org.openspcoop2.core.config.Soggetto soggetto) throws DriverConfigurazioneException {
		if (soggetto == null)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSoggetto] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteSoggetto");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDSoggetto type = 3");
			// DELETE soggetto
			DriverConfigurazioneDBSoggettiLib.CRUDSoggetto(3, soggetto, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
		}finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected Soggetto getRouter() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		Soggetto soggetto = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getRouter");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRouter] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);
		long idRouter = -1;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("is_router = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setInt(1, CostantiDB.TRUE);

			this.driver.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.ABILITATO));
			rs = stm.executeQuery();

			// prendo il primo router se c'e' altrimenti lancio eccezione.
			if (rs.next()) {
				idRouter = rs.getLong("id");
			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getRouter] Non esiste un Soggetto Router.");
			}

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRouter] SqlException: " + se.getMessage(),se);
		} catch (DriverConfigurazioneNotFound se) {
			throw se;
		} catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getRouter] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

		soggetto=this.getSoggetto(idRouter);
		// e' sicuramente un router
		soggetto.setRouter(true);
		return soggetto;
	}

	protected List<IDSoggetto> getSoggettiWithSuperuser(String user) throws DriverConfigurazioneException {

		List<IDSoggetto> idSoggetti = null;

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggettiWithSuperuser");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggettiWithSuperuser] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);

		try {
			List<IDSoggetto> idTrovati = new ArrayList<>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
			sqlQueryObject.addWhereCondition("superuser = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, user);

			this.driver.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.ABILITATO));
			rs = stm.executeQuery();

			// prendo il primo router se c'e' altrimenti lancio eccezione.
			while (rs.next()) {
				IDSoggetto id = new IDSoggetto();
				id.setTipo(rs.getString(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO));
				id.setNome(rs.getString(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO));
				idTrovati.add(id);
			}

			if(!idTrovati.isEmpty()){
				idSoggetti =  new ArrayList<>();
				idSoggetti.addAll(idTrovati);
			}

		} catch (SQLException se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggettiWithSuperuser] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggettiWithSuperuser] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
		return idSoggetti;
	}

	protected List<IDSoggetto> getSoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		List<IDSoggetto> soggettiVirtuali = new ArrayList<>();
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggettiVirtuali");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO_VIRTUALE);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO_VIRTUALE);
			sqlQueryObject.addWhereCondition("tipo_soggetto_virtuale IS NOT NULL");
			sqlQueryObject.addWhereCondition("nome_soggetto_virtuale IS NOT NULL");
			sqlQueryObject.addWhereCondition("tipo_soggetto_virtuale<>'\"\"'");
			sqlQueryObject.addWhereCondition("nome_soggetto_virtuale<>'\"\"'");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery));

			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			while (rs.next()) {
				IDSoggetto soggettoVirtuale = new IDSoggetto(rs.getString(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO_VIRTUALE) , rs.getString(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO_VIRTUALE));
				this.driver.logInfo("aggiunto Soggetto " + soggettoVirtuale + " alla lista dei Soggetti Virtuali");
				soggettiVirtuali.add(soggettoVirtuale);
			}
			rs.close();
			stm.close();

			if(soggettiVirtuali.isEmpty()){
				throw new DriverConfigurazioneNotFound("[getSoggettiVirtuali] Soggetti virtuali non esistenti");
			}

			this.driver.logInfo("aggiunti " + soggettiVirtuali.size() + " soggetti alla lista dei Soggetti Virtuali");
			return soggettiVirtuali;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("SqlException: " + se.getMessage(), se);
		} catch (DriverConfigurazioneNotFound se) {
			throw se;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("Exception: " + se.getMessage(), se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

	}
	
	/**
	 * Ritorna il {@linkplain Soggetto} utilizzando il {@link DriverConfigurazioneDB} che ha l'id passato come parametro 
	 */
	protected Soggetto getSoggetto(long idSoggetto) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {
		return getSoggetto(idSoggetto,null);
	}
	protected Soggetto getSoggetto(long idSoggetto,Connection conParam) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		if (idSoggetto <= 0)
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] L'id del soggetto deve essere > 0.");

		Soggetto soggetto = null;

		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		String sqlQuery = "";

		if(conParam!=null){
			con = conParam;
		}
		else if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getSoggetto(longId)");
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);
			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.driver.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next()) {
				soggetto = new Soggetto();

				soggetto.setId(rs.getLong("id"));

				soggetto.setNome(rs.getString(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO));

				soggetto.setTipo(rs.getString(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO));

				soggetto.setSuperUser(rs.getString("superuser"));

				String tmp = rs.getString("descrizione");
				soggetto.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("identificativo_porta");
				soggetto.setIdentificativoPorta(((tmp == null || tmp.equals("")) ? null : tmp));

				int defaultR = rs.getInt("is_default");
				boolean isDefault = false;
				if (defaultR == CostantiDB.TRUE)
					isDefault = true;
				soggetto.setDominioDefault(isDefault);
				
				int router = rs.getInt("is_router");
				boolean isrouter = false;
				if (router == CostantiDB.TRUE)
					isrouter = true;
				soggetto.setRouter(isrouter);

				tmp = rs.getString("pd_url_prefix_rewriter");
				soggetto.setPdUrlPrefixRewriter(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("pa_url_prefix_rewriter");
				soggetto.setPaUrlPrefixRewriter(((tmp == null || tmp.equals("")) ? null : tmp));

				// Creava OutOfMemory, non dovrebbe servire
/**				// Aggiungo i servizi applicativi
//				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
//				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
//				sqlQueryObject.addSelectField("id");
//				sqlQueryObject.addSelectField("nome");
//				sqlQueryObject.addWhereCondition("id_soggetto=?");
//				sqlQuery = sqlQueryObject.createSQLQuery();
//				stm1 = con.prepareStatement(sqlQuery);
//				stm1.setLong(1, rs.getLong("id"));
//				rs1 = stm1.executeQuery();
//
//				ServizioApplicativo servizioApplicativo = null;
//				while (rs1.next()) {
//					// setto solo il nome come da specifica
//					servizioApplicativo = new ServizioApplicativo();
//					servizioApplicativo.setId(rs1.getLong("id"));
//					servizioApplicativo.setNome(rs1.getString("nome"));
//					Soggetto.addServizioApplicativo(servizioApplicativo);
//				}
//				rs1.close();
//				stm1.close();*/

			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getSoggetto] Soggetto non Esistente.");
			}

			return soggetto;

		} catch (SQLException se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] SqlException: " + se.getMessage(),se);
		}catch (DriverConfigurazioneNotFound e) {
			throw new DriverConfigurazioneNotFound(e);
		} catch (Exception se) {

			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs1, stm1);
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(conParam, con);
		}
	}

	
	protected List<IDServizio> getServiziSoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		List<IDServizio> servizi = new ArrayList<>();
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getServiziSoggettiVirtuali");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO_VIRTUALE);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO_VIRTUALE);
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.setANDLogicOperator(false);
			sqlQueryObject.addWhereCondition("id_soggetto_virtuale<>-1");
			sqlQueryObject.addWhereCondition(true, "tipo_soggetto_virtuale is not null", "nome_soggetto_virtuale is not null");
			sqlQueryObject.setANDLogicOperator(false);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug(ESEGUO_QUERY_PREFIX + DBUtils.formatSQLString(sqlQuery));

			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			while (rs.next()) {
				IDServizio servizio = IDServizioUtils.buildIDServizio(rs.getString("tipo_servizio"), rs.getString("servizio"), 
						new IDSoggetto(rs.getString(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO_VIRTUALE), rs.getString(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO_VIRTUALE)), 
						rs.getInt("versione_servizio"));
				servizi.add(servizio);
				this.driver.logInfo("aggiunto Servizio " + servizio.toString() + " alla lista dei servizi erogati da Soggetti Virtuali");
			}

			if(servizi.isEmpty()){
				throw new DriverConfigurazioneNotFound("[getServiziSoggettiVirtuali] Servizi erogati da Soggetti virtuali non esistenti");
			}

			this.driver.logInfo("aggiunti " + servizi.size() + " servizi alla lista dei servizi erogati da Soggetti Virtuali");
			return servizi;
		} catch (SQLException se) {
			throw new DriverConfigurazioneException("SqlException: " + se.getMessage(), se);
		}  catch (DriverConfigurazioneNotFound se) {
			throw se;
		}catch (Exception se) {
			throw new DriverConfigurazioneException("Exception: " + se.getMessage(), se);
		}finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}

	}
	
	protected boolean existsSoggetto(IDSoggetto idSoggetto) throws DriverConfigurazioneException {

		if(idSoggetto==null)throw new DriverConfigurazioneException("[existsSoggetto::existsSoggetto] Soggetto non Impostato.");
		if(idSoggetto.getNome()==null || "".equals(idSoggetto.getNome()))throw new DriverConfigurazioneException("[existsSoggetto::existsServizioApplicativo] Nome Soggetto non Impostato.");
		if(idSoggetto.getTipo()==null || "".equals(idSoggetto.getTipo()))throw new DriverConfigurazioneException("[existsSoggetto::existsServizioApplicativo] Nome Soggetto non Impostato.");

		Connection con = null;

		try {
			
			if (this.driver.atomica) {
				try {
					con = this.driver.getConnectionFromDatasource("existsSoggetto");
				} catch (Exception e) {
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::existsSoggetto] Exception accedendo al datasource :" + e.getMessage(),e);

				}

			} else
				con = this.driver.globalConnection;
			
			return DBUtils.getIdSoggetto(idSoggetto.getNome(), idSoggetto.getTipo(), con, this.driver.tipoDB,this.driver.tabellaSoggetti)>0;
		} catch (Exception qe) {
			throw new DriverConfigurazioneException(qe);
		} finally {

			this.driver.closeConnection(con);

		}
	}
	
	protected List<Soggetto> getAllSoggetti() throws DriverConfigurazioneException {
		String nomeMetodo = "getAllSoggetti";

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<Soggetto> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(OPERAZIONE_ATOMICA_PREFIX + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField("id");
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				Long id = risultato.getLong("id");
				lista.add(this.getSoggetto(id));

			}

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
	
	protected List<IDSoggetto> getAllIdSoggetti(
			FiltroRicercaSoggetti filtroRicerca) throws DriverConfigurazioneException, DriverConfigurazioneNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.driver.logDebug("getAllIdSoggetti...");

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdSoggetti");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition("ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition("ora_registrazione < ?");
				if(filtroRicerca.getTipo()!=null)
					sqlQueryObject.addWhereCondition("tipo_soggetto = ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition("nome_soggetto = ?");
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.logDebug(ESEGUO_QUERY_PREFIX + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.driver.logDebug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.driver.logDebug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getTipo()!=null){
					this.driver.logDebug("tipoSoggetto stmt.setString("+filtroRicerca.getTipo()+")");
					stm.setString(indexStmt, filtroRicerca.getTipo());
					indexStmt++;
				}
				if(filtroRicerca.getNome()!=null){
					this.driver.logDebug("nomeSoggetto stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}	
			}
			rs = stm.executeQuery();
			List<IDSoggetto> idSoggetti = new ArrayList<>();
			while (rs.next()) {
				IDSoggetto idS = new IDSoggetto(rs.getString(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO),rs.getString(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO));
				idSoggetti.add(idS);
			}
			if(idSoggetti.isEmpty()){
				if(filtroRicerca!=null)
					throw new DriverConfigurazioneNotFound("Soggetti non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverConfigurazioneNotFound("Soggetti non trovati");
			}else{
				return idSoggetti;
			}
		}catch(DriverConfigurazioneNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException("getAllIdSoggetti error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}

	}
}
