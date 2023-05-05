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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.registry.PortaDominio;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicerca;
import org.openspcoop2.utils.certificate.CertificateUtils;
import org.openspcoop2.utils.certificate.PrincipalType;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_pddDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_pddDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_pddDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected org.openspcoop2.core.registry.PortaDominio getPortaDominio(String nomePdD) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		this.driver.logDebug("richiesto getPortaDominio: " + nomePdD);
		// conrollo consistenza
		if (nomePdD == null)
			throw new DriverRegistroServiziException("[getPortaDominio] Parametro nomePdD is null");
		if (nomePdD.trim().equals(""))
			throw new DriverRegistroServiziException("[getPortaDominio] Parametro nomePdD non e' definito");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getPortaDominio(nome)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getPortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			String queryString = sqlQueryObject
					.createSQLQuery();
			stm = con.prepareStatement(queryString);
			stm.setString(1, nomePdD);
			rs = stm.executeQuery();
			PortaDominio pdd = null;
			if (rs.next()) {
				pdd = new PortaDominio();
				pdd.setId(rs.getLong("id"));
				pdd.setNome(rs.getString("nome"));
				pdd.setDescrizione(rs.getString("descrizione"));
				pdd.setImplementazione(rs.getString("implementazione"));
				pdd.setSubject(rs.getString("subject"));
				pdd.setClientAuth(DriverRegistroServiziDB_LIB.getEnumStatoFunzionalita(rs.getString("client_auth")));
				pdd.setSuperUser(rs.getString("superuser"));

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					pdd.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getPortaDominio] rs.next non ha restituito valori con la seguente interrogazione :\n" + DriverRegistroServiziDB_LIB.formatSQLString(queryString, nomePdD));
			}

			return pdd;

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getPortaDominio] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getPortaDominio] Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}


	protected List<String> getAllIdPorteDominio(FiltroRicerca filtroRicerca) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		this.driver.logDebug("getAllIdPorteDominio...");

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdPorteDominio");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("nome");
			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition("ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition("ora_registrazione < ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition("nome = ?");
				if(filtroRicerca.getTipo()!=null)
					sqlQueryObject.addWhereCondition("tipo = ?");
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
				if(filtroRicerca.getNome()!=null){
					this.driver.logDebug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}	
				if(filtroRicerca.getTipo()!=null){
					this.driver.logDebug("tipoPdd stmt.setString("+filtroRicerca.getTipo()+")");
					stm.setString(indexStmt, filtroRicerca.getTipo());
					indexStmt++;
				}
			}
			rs = stm.executeQuery();
			List<String> nomiPdd = new ArrayList<>();
			while (rs.next()) {
				nomiPdd.add(rs.getString("nome"));
			}
			if(nomiPdd.size()==0){
				if(filtroRicerca!=null)
					throw new DriverRegistroServiziNotFound("Porte di Dominio non trovate che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverRegistroServiziNotFound("Porte di Dominio non trovate");
			}else{
				return nomiPdd;
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdPorteDominio error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
	}



	protected void createPortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		if (pdd == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createPortaDominio] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createPortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createPortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDPortaDominio type = 1");
			DriverRegistroServiziDB_soggettiLIB.CRUDPortaDominio(CostantiDB.CREATE, pdd, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createPortaDominio] Errore durante la creazione della porta di dominio : " + qe.getMessage(), qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected boolean existsPortaDominio(String nome) throws DriverRegistroServiziException{
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (nome == null)
			throw new DriverRegistroServiziException("Parametro non valido");

		if (nome.equals(""))
			throw new DriverRegistroServiziException("Parametro vuoto non valido");

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsPortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsPortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.driver.log.error("Errore durante verifica esistenza porta di dominio :", e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);
		}

		return exist;
	}

	protected void updatePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		if (pdd == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortaDominio] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updatePortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {

			this.driver.logDebug("CRUDPortaDominio type = 2");
			DriverRegistroServiziDB_soggettiLIB.CRUDPortaDominio(CostantiDB.UPDATE, pdd, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortaDominio] Errore durante l'aggiornamento della porta di dominio : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}	


	protected void updateTipoPortaDominio(String nomePdd,String tipo) throws DriverRegistroServiziException{
		if (nomePdd == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateTipoPortaDominio] Parametro non valido.");
		if (tipo == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateTipoPortaDominio] Parametro.tipo non valido.");

		PreparedStatement stm=null;
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateTipoPortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addUpdateTable(CostantiDB.PDD);
			sqlQueryObject.addUpdateField("tipo", "?");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLUpdate();
			stm = con.prepareStatement(sqlQuery);

			stm.setString(1, tipo);
			stm.setString(2, nomePdd);

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, tipo, nomePdd));
			stm.executeUpdate();

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updatePortaDominio] Errore durante l'aggiornamento della porta di dominio : " + qe.getMessage(),qe);
		} finally {

			JDBCUtilities.closeResources(stm);

			this.driver.closeConnection(error,con);
		}
	}	
	
	protected String getTipoPortaDominio(String nome) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (nome == null)
			throw new DriverRegistroServiziException("Parametro non valido");

		if (nome.equals(""))
			throw new DriverRegistroServiziException("Parametro vuoto non valido");

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getTipoPortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getTipoPortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("tipo");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, nome);
			rs = stm.executeQuery();
			if (rs.next()){
				return rs.getString("tipo");
			}
			rs.close();
			stm.close();

			throw new DriverRegistroServiziNotFound("Porta di Dominio ["+nome+"] non esistente");
			
		} catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception e) {
			this.driver.log.error("Errore durante verifica esistenza porta di dominio :", e);
			throw new DriverRegistroServiziException(e.getMessage(),e); 
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);
		}

	}


	protected void deletePortaDominio(PortaDominio pdd) throws DriverRegistroServiziException{
		if (pdd == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deletePortaDominio] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deletePortaDominio");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deletePortaDominio] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDPortaDominio type = 3");
			DriverRegistroServiziDB_soggettiLIB.CRUDPortaDominio(CostantiDB.DELETE, pdd, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deletePortaDominio] Errore durante l'eliminazione della porta di dominio : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<PortaDominio> porteDominioList(String superuser, ISearch ricerca) throws DriverRegistroServiziException{
		return this.porteDominioList(superuser, null, ricerca);
	}
	protected List<PortaDominio> porteDominioList(String superuser,String tipo, ISearch ricerca) throws DriverRegistroServiziException{
		String nomeMetodo = "porteDominioList";
		int idLista = Liste.PDD;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<PortaDominio> lista = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDominioList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				// query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectCountField("*",
						"cont");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if (tipo!=null && (!tipo.equals("")))
					sqlQueryObject.addWhereCondition("tipo = ?");
				sqlQueryObject.addWhereLikeCondition(
						"nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject
						.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectCountField("*",
						"cont");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if (tipo!=null && (!tipo.equals("")))
					sqlQueryObject.addWhereCondition("tipo = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject
						.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				stmt.setString(index++, superuser);
			if (tipo!=null && (!tipo.equals("")))
				stmt.setString(index++, tipo);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			if (!search.equals("")) {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectField("nome");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if (tipo!=null && (!tipo.equals("")))
					sqlQueryObject.addWhereCondition("tipo = ?");
				sqlQueryObject.addWhereLikeCondition(
						"nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject
						.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.PDD);
				sqlQueryObject.addSelectField("nome");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if (tipo!=null && (!tipo.equals("")))
					sqlQueryObject.addWhereCondition("tipo = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject
						.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				stmt.setString(index++, superuser);
			if (tipo!=null && (!tipo.equals("")))
				stmt.setString(index++, tipo);
			risultato = stmt.executeQuery();

			PortaDominio pdd;
			lista = new ArrayList<PortaDominio>();
			while (risultato.next()) {
				String nome = risultato.getString("nome");
				pdd = this.getPortaDominio(nome);
				lista.add(pdd);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}

	protected boolean isPddInUso(PortaDominio pdd, List<String> whereIsInUso)
			throws DriverRegistroServiziException {
		String nomeMetodo = "pddInUso";

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		String queryString;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("isPddInUso");

			} catch (Exception e) {
				throw new DriverRegistroServiziException(
						"[DriverRegistroServiziDB::" + nomeMetodo
						+ "] Exception accedendo al datasource :"
						+ e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			// Controllo che il pdd non sia in uso
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject
			.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject
			.addWhereCondition("server = ?");
			queryString = sqlQueryObject
					.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, pdd.getNome());
			risultato = stmt.executeQuery();
			boolean isInUso = false;
			while (risultato.next()) {
				String tipo_soggetto = risultato.getString("tipo_soggetto");
				String nome_soggetto = risultato.getString("nome_soggetto");
				whereIsInUso.add(tipo_soggetto + "/" + nome_soggetto);
				isInUso = true;
			}

			return isInUso;

		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziException::"
					+ nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}

	protected List<Soggetto> pddSoggettiList(long idPdd, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "pddSoggettiList";
		int idLista = Liste.PDD_SOGGETTI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("pddSoggettiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException(
						"[DriverRegistroServiziDB::" + nomeMetodo
						+ "] Exception accedendo al datasource :"
						+ e.getMessage(), e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		ArrayList<Soggetto> lista = null;

		try {

			// Prendo il nome del pdd
			String nomePdd = "";
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id = ?");
			queryString = sqlQueryObject
					.createSQLQuery();
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idPdd);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				nomePdd = risultato.getString("nome");
			}
			risultato.close();
			stmt.close();

			if (!search.equals("")) {
				// query con search
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*",
						"cont");
				sqlQueryObject
				.addWhereCondition("server = ?");
				sqlQueryObject.addWhereCondition(false,
						sqlQueryObject
						.getWhereLikeCondition("nome_soggetto", search,
								true, true),
								sqlQueryObject
								.getWhereLikeCondition("tipo_soggetto", search,
										true, true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject
						.createSQLQuery();
			} else {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*",
						"cont");
				sqlQueryObject
				.addWhereCondition("server = ?");
				queryString = sqlQueryObject
						.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePdd);
			risultato = stmt.executeQuery();
			if (risultato.next()) {
				ricerca.setNumEntries(idLista, risultato.getInt(1));
			}
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) {
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			}
			if (!search.equals("")) {
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("server");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject
				.addWhereCondition("server = ?");
				sqlQueryObject.addWhereCondition(false,
						sqlQueryObject
						.getWhereLikeCondition("nome_soggetto", search,
								true, true),
								sqlQueryObject
								.getWhereLikeCondition("tipo_soggetto", search,
										true, true));
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject
				.addOrderBy("tipo_soggetto");
				sqlQueryObject
				.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject
						.createSQLQuery();
			} else {
				// senza search
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject
				.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("server");
				sqlQueryObject.addSelectField("id");
				sqlQueryObject
				.addWhereCondition("server = ?");
				sqlQueryObject
				.addOrderBy("tipo_soggetto");
				sqlQueryObject
				.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject
						.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setString(1, nomePdd);
			risultato = stmt.executeQuery();

			lista = new ArrayList<Soggetto>();
			Soggetto sog = null;
			while (risultato.next()) {
				long ids = risultato.getLong("id");
				sog = this.driver.getSoggetto(ids,con);
				lista.add(sog);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverControlStationDB::"
					+ nomeMetodo + "] Exception: " + se.getMessage(), se);
		} finally {
			// Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}

	}
	
	protected List<PortaDominio> porteDominioWithSubject(String subject)throws DriverRegistroServiziException {
		String nomeMetodo = "porteDominioWithSubject";
		String queryString;

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<PortaDominio> lista = new ArrayList<PortaDominio>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("porteDominioWithSubject");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.atomica = " + this.driver.atomica);

		try {

			Map<String, List<String>> hashSubject = CertificateUtils.getPrincipalIntoMap(subject, PrincipalType.SUBJECT);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.PDD);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("subject");
			for (String key : hashSubject.keySet()) {				
				List<String> listValues = hashSubject.get(key);
				for (String value : listValues) {
					sqlQueryObject.addWhereLikeCondition("subject", "/"+CertificateUtils.formatKeyPrincipal(key)+"="+CertificateUtils.formatValuePrincipal(value)+"/", true, false);
				}
			}
			sqlQueryObject.setANDLogicOperator(true);
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);

			risultato = stmt.executeQuery();

			PortaDominio pdd;
			while (risultato.next()) {

				// Possono esistere piu' pdd che hanno una porzione di subject uguale, devo quindi verificare che sia proprio quello che cerco
				String subjectPotenziale =  risultato.getString("subject");
				if(CertificateUtils.sslVerify(subjectPotenziale, subject, PrincipalType.SUBJECT, this.driver.log)){
					pdd=this.getPortaDominio(risultato.getString("nome"));
					lista.add(pdd);
				}
			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
}
