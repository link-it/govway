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
public class DriverConfigurazioneDB_soggettiDriver {

	private DriverConfigurazioneDB driver = null;
	
	protected DriverConfigurazioneDB_soggettiDriver(DriverConfigurazioneDB driver) {
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
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("id = ?");
			String sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.driver.logDebug("eseguo query : " + DriverConfigurazioneDB_LIB.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next()) {
				idSoggettoObject = new IDSoggetto();

				// String tmp = rs.getString("nomeprov");
				// soggetto.setNome(( (tmp==null || tmp.equals("") ) ?
				// null : tmp));
				idSoggettoObject.setNome(rs.getString("nome_soggetto"));
				// tmp = rs.getString("tipoprov");
				// soggetto.setTipo(( (tmp==null || tmp.equals("") ) ?
				// null : tmp));
				idSoggettoObject.setTipo(rs.getString("tipo_soggetto"));

			}else{
				throw new DriverConfigurazioneNotFound("Nessun risultato trovat eseguendo: "+DriverConfigurazioneDB_LIB.formatSQLString(sqlQuery, idSoggetto));
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

		// Devi cercare un soggetto con quel tipo e nome (la chiave univoca e'
		// formata da tipo+nome)
		//
		// 1. deve ritornare il soggetto con tipo, nome e dominio
		// registrato e descrizione.
		// 3. deve essere impostato anche l'eventuale abilita' di router.
		/*
		 * Soggetto sog = new Soggetto();
		 * sog.setDescrizione(descrizione);
		 * sog.setIdentificativoPorta(identificativoPorta); sog.setNome(nome);
		 * sog.setTipo(tipo); sog.setRouter(boolean);
		 * 
		 */
		Soggetto Soggetto = null;

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

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

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


		Soggetto=this.getSoggetto(idSoggetto);

		if(Soggetto==null){
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::getSoggetto] Soggetto non Esistente.");
		}

		return Soggetto;
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

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDSoggetto type = 1");
			// creo soggetto
			DriverConfigurazioneDB_soggettiLIB.CRUDSoggetto(1, soggetto, con);

		} catch (DriverConfigurazioneException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::createSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
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

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDSoggetto type = 2");
			// UPDATE soggetto
			DriverConfigurazioneDB_soggettiLIB.CRUDSoggetto(2, soggetto, con);

		} catch (DriverConfigurazioneException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::updateSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
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

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDSoggetto type = 3");
			// DELETE soggetto
			DriverConfigurazioneDB_soggettiLIB.CRUDSoggetto(3, soggetto, con);

		} catch (DriverConfigurazioneException qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
		}  catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::deleteSoggetto] Errore durante la creazione del soggetto : " + qe.getMessage(),qe);
		}finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected Soggetto getRouter() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		Soggetto Soggetto = null;

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

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);
		long idRouter = -1;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("is_router = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setInt(1, CostantiDB.TRUE);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.ABILITATO));
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

		Soggetto=this.getSoggetto(idRouter);
		// e' sicuramente un router
		Soggetto.setRouter(true);
		return Soggetto;
	}

	protected List<IDSoggetto> getSoggettiWithSuperuser(String user) throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

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

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			List<IDSoggetto> idTrovati = new ArrayList<IDSoggetto>();
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
			sqlQueryObject.addWhereCondition("superuser = ?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, user);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, CostantiConfigurazione.ABILITATO));
			rs = stm.executeQuery();

			// prendo il primo router se c'e' altrimenti lancio eccezione.
			while (rs.next()) {
				IDSoggetto id = new IDSoggetto();
				id.setTipo(rs.getString("tipo_soggetto"));
				id.setNome(rs.getString("nome_soggetto"));
				idTrovati.add(id);
			}

			if(idTrovati.size()>0){
				idSoggetti =  new ArrayList<IDSoggetto>();
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

		List<IDSoggetto> soggettiVirtuali = new ArrayList<IDSoggetto>();
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

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField("tipo_soggetto_virtuale");
			sqlQueryObject.addSelectField("nome_soggetto_virtuale");
			sqlQueryObject.addWhereCondition("tipo_soggetto_virtuale IS NOT NULL");
			sqlQueryObject.addWhereCondition("nome_soggetto_virtuale IS NOT NULL");
			sqlQueryObject.addWhereCondition("tipo_soggetto_virtuale<>'\"\"'");
			sqlQueryObject.addWhereCondition("nome_soggetto_virtuale<>'\"\"'");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery));

			stm = con.prepareStatement(sqlQuery);
			//stm.setString(1, "");
			//stm.setString(2, "");
			rs = stm.executeQuery();

			while (rs.next()) {
				IDSoggetto soggettoVirtuale = new IDSoggetto(rs.getString("tipo_soggetto_virtuale") , rs.getString("nome_soggetto_virtuale"));
				this.driver.log.info("aggiunto Soggetto " + soggettoVirtuale + " alla lista dei Soggetti Virtuali");
				soggettiVirtuali.add(soggettoVirtuale);
			}
			rs.close();
			stm.close();

			if(soggettiVirtuali.size()==0){
				throw new DriverConfigurazioneNotFound("[getSoggettiVirtuali] Soggetti virtuali non esistenti");
			}

			this.driver.log.info("aggiunti " + soggettiVirtuali.size() + " soggetti alla lista dei Soggetti Virtuali");
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

		Soggetto Soggetto = null;

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

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(this.driver.tabellaSoggetti);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id=?");
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

			stm = con.prepareStatement(sqlQuery);

			stm.setLong(1, idSoggetto);

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery, idSoggetto));
			rs = stm.executeQuery();

			if (rs.next()) {
				Soggetto = new Soggetto();

				Soggetto.setId(rs.getLong("id"));

				Soggetto.setNome(rs.getString("nome_soggetto"));

				Soggetto.setTipo(rs.getString("tipo_soggetto"));

				Soggetto.setSuperUser(rs.getString("superuser"));

				String tmp = rs.getString("descrizione");
				Soggetto.setDescrizione(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("identificativo_porta");
				Soggetto.setIdentificativoPorta(((tmp == null || tmp.equals("")) ? null : tmp));

				int defaultR = rs.getInt("is_default");
				boolean is_default = false;
				if (defaultR == CostantiDB.TRUE)
					is_default = true;
				Soggetto.setDominioDefault(is_default);
				
				int router = rs.getInt("is_router");
				boolean isrouter = false;
				if (router == CostantiDB.TRUE)
					isrouter = true;
				Soggetto.setRouter(isrouter);

				tmp = rs.getString("pd_url_prefix_rewriter");
				Soggetto.setPdUrlPrefixRewriter(((tmp == null || tmp.equals("")) ? null : tmp));

				tmp = rs.getString("pa_url_prefix_rewriter");
				Soggetto.setPaUrlPrefixRewriter(((tmp == null || tmp.equals("")) ? null : tmp));

				// Creava OutOfMemory, non dovrebbe servire
//				// Aggiungo i servizi applicativi
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
//				stm1.close();

			} else {
				throw new DriverConfigurazioneNotFound("[DriverConfigurazioneDB::getSoggetto] Soggetto non Esistente.");
			}

			return Soggetto;

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

	
	protected List<IDServizio> getServizi_SoggettiVirtuali() throws DriverConfigurazioneException,DriverConfigurazioneNotFound {

		List<IDServizio> servizi = new ArrayList<IDServizio>();
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		String sqlQuery = "";
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getServizi_SoggettiVirtuali");

			} catch (Exception e) {
				throw new DriverConfigurazioneException("Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
			sqlQueryObject.setSelectDistinct(true);
			sqlQueryObject.addSelectField("tipo_soggetto_virtuale");
			sqlQueryObject.addSelectField("nome_soggetto_virtuale");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.setANDLogicOperator(false);
			sqlQueryObject.addWhereCondition("id_soggetto_virtuale<>-1");
			sqlQueryObject.addWhereCondition(true, "tipo_soggetto_virtuale is not null", "nome_soggetto_virtuale is not null");
			sqlQueryObject.setANDLogicOperator(false);
			sqlQuery = sqlQueryObject.createSQLQuery();

			this.driver.logDebug("eseguo query : " + DBUtils.formatSQLString(sqlQuery));

			stm = con.prepareStatement(sqlQuery);
			rs = stm.executeQuery();

			while (rs.next()) {
				IDServizio servizio = IDServizioUtils.buildIDServizio(rs.getString("tipo_servizio"), rs.getString("servizio"), 
						new IDSoggetto(rs.getString("tipo_soggetto_virtuale"), rs.getString("nome_soggetto_virtuale")), 
						rs.getInt("versione_servizio"));
				servizi.add(servizio);
				this.driver.log.info("aggiunto Servizio " + servizio.toString() + " alla lista dei servizi erogati da Soggetti Virtuali");
			}

			if(servizi.size()==0){
				throw new DriverConfigurazioneNotFound("[getServizi_SoggettiVirtuali] Servizi erogati da Soggetti virtuali non esistenti");
			}

			this.driver.log.info("aggiunti " + servizi.size() + " servizi alla lista dei servizi erogati da Soggetti Virtuali");
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
		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

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
			sqlQueryObject.addSelectField("tipo_soggetto");
			sqlQueryObject.addSelectField("nome_soggetto");
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
			this.driver.logDebug("eseguo query : " + sqlQuery );
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
			List<IDSoggetto> idSoggetti = new ArrayList<IDSoggetto>();
			while (rs.next()) {
				IDSoggetto idS = new IDSoggetto(rs.getString("tipo_soggetto"),rs.getString("nome_soggetto"));
				idSoggetti.add(idS);
			}
			if(idSoggetti.size()==0){
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
