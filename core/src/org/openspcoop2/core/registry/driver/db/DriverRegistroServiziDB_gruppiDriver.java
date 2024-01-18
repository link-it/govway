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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.registry.Gruppo;
import org.openspcoop2.core.registry.constants.ServiceBinding;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaGruppi;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_gruppiDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_gruppiDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_gruppiDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected Gruppo getGruppo(
			IDGruppo idGruppo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		this.driver.logDebug("richiesto getGruppo: " + idGruppo);
		// conrollo consistenza
		if (idGruppo == null)
			throw new DriverRegistroServiziException("[getGruppo] Parametro idGruppo is null");
		if (idGruppo.getNome()==null || idGruppo.getNome().trim().equals(""))
			throw new DriverRegistroServiziException("[getGruppo] Parametro idGruppo.nome non e' definito");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getGruppo(nome)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getGruppo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			String queryString = sqlQueryObject
					.createSQLQuery();
			stm = con.prepareStatement(queryString);
			stm.setString(1, idGruppo.getNome());
			rs = stm.executeQuery();
			Gruppo gruppo = null;
			if (rs.next()) {
				gruppo = new Gruppo();
				gruppo.setId(rs.getLong("id"));
				gruppo.setNome(rs.getString("nome"));
				gruppo.setDescrizione(rs.getString("descrizione"));
				String serviceBinding = rs.getString("service_binding");
				if(serviceBinding!=null){
					gruppo.setServiceBinding(ServiceBinding.toEnumConstant(serviceBinding));
				}
				gruppo.setSuperUser(rs.getString("superuser"));

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					gruppo.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

				// Proprieta Oggetto
				gruppo.setProprietaOggetto(DriverRegistroServiziDB_utilsDriver.readProprietaOggetto(rs,false));
				
			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getGruppo] rs.next non ha restituito valori con la seguente interrogazione :\n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(queryString, idGruppo.getNome()));
			}

			return gruppo;

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getGruppo] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getGruppo] Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}

	protected Gruppo getGruppo(
			long idGruppo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		this.driver.logDebug("richiesto getGruppo: " + idGruppo);
		// conrollo consistenza
		if (idGruppo <=0)
			throw new DriverRegistroServiziException("[getGruppo] Parametro idGruppo non valido");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getGruppo(id)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getGruppo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		IDGruppo idGruppoObject = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addWhereCondition("id = ?");
			String queryString = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(queryString);
			stm.setLong(1, idGruppo);
			rs = stm.executeQuery();
			if (rs.next()) {
				idGruppoObject = new IDGruppo(rs.getString("nome"));
			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getGruppo] rs.next non ha restituito valori con la seguente interrogazione :\n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(queryString, idGruppo));
			}

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getGruppo] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getGruppo] Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
		
		return this.getGruppo(idGruppoObject);
	}

	protected List<IDGruppo> getAllIdGruppi(
			FiltroRicercaGruppi filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		boolean filtroRicercaTipo = false;
		if(filtroRicerca!=null){
			filtroRicercaTipo = filtroRicerca.getServiceBinding()!=null;
		}
		
		List<String> tipoSoggettiProtocollo = null;
		try {
			if(filtroRicerca!=null && (filtroRicerca.getProtocollo()!=null || (filtroRicerca.getProtocolli()!=null && !filtroRicerca.getProtocolli().isEmpty()))){
				tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filtroRicerca.getProtocollo(), Filtri.convertToString(filtroRicerca.getProtocolli()));
			}
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		boolean searchByTipoSoggetto = (tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty());
		
		this.driver.logDebug("getAllIdGruppi...");

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdGruppi");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
			if(searchByTipoSoggetto) {
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
			}
			sqlQueryObject.addSelectField(CostantiDB.GRUPPI, "nome");
			if(searchByTipoSoggetto) {
				sqlQueryObject.setSelectDistinct(true);
			}
			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".ora_registrazione < ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".nome = ?");
				if(filtroRicercaTipo){
					
					ISQLQueryObject sqlQueryObjectServiceBinding = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectServiceBinding.addWhereIsNullCondition(CostantiDB.GRUPPI+".service_binding");
					sqlQueryObjectServiceBinding.addWhereCondition(CostantiDB.GRUPPI+".service_binding= ?");
					sqlQueryObjectServiceBinding.setANDLogicOperator(false);
					sqlQueryObject.addWhereCondition(sqlQueryObjectServiceBinding.createSQLConditions());
					
				}
				if(searchByTipoSoggetto) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo="+CostantiDB.GRUPPI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo="+CostantiDB.ACCORDI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				
				if(filtroRicerca.isOrdinaDataRegistrazione())
					sqlQueryObject.addOrderBy(CostantiDB.GRUPPI+".ora_registrazione");
				
				sqlQueryObject.addOrderBy(CostantiDB.GRUPPI+".nome");
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
				if(filtroRicercaTipo){
					this.driver.logDebug("serviceBinding stmt.setString("+filtroRicerca.getServiceBinding().getValue()+")");
					stm.setString(indexStmt, filtroRicerca.getServiceBinding().getValue());
					indexStmt++;
				}
			}
			rs = stm.executeQuery();
			List<IDGruppo> nomiGruppi = new ArrayList<>();
			while (rs.next()) {
				nomiGruppi.add(new IDGruppo(rs.getString("nome")));
			}
			if(nomiGruppi.isEmpty()){
				if(filtroRicerca!=null)
					throw new DriverRegistroServiziNotFound("Gruppi non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverRegistroServiziNotFound("Gruppi non trovati");
			}else{
				return nomiGruppi;
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdGruppi error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);

		}
	}



	protected void createGruppo(Gruppo gruppo) throws DriverRegistroServiziException{
		if (gruppo == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createGruppo] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createGruppo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createGruppo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDGruppo type = 1");
			DriverRegistroServiziDB_LIB.CRUDGruppo(CostantiDB.CREATE, gruppo, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createGruppo] Errore durante la creazione del gruppo : " + qe.getMessage(), qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected boolean existsGruppo(IDGruppo idGruppo) throws DriverRegistroServiziException{
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (idGruppo == null)
			throw new DriverRegistroServiziException("Parametro non valido");

		if (idGruppo.getNome()==null || idGruppo.getNome().equals(""))
			throw new DriverRegistroServiziException("Parametro vuoto non valido");

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsGruppo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsGruppo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, idGruppo.getNome());
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.driver.log.error("Errore durante verifica esistenza gruppo: "+e.getMessage(), e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);
		}

		return exist;
	}

	protected void updateGruppo(Gruppo gruppo) throws DriverRegistroServiziException{
		if (gruppo == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateGruppo] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateGruppo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateGruppo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {

			this.driver.logDebug("CRUDGruppo type = 2");
			DriverRegistroServiziDB_LIB.CRUDGruppo(CostantiDB.UPDATE, gruppo, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateGruppo] Errore durante l'aggiornamento del gruppo : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}	

	protected void deleteGruppo(Gruppo gruppo) throws DriverRegistroServiziException{
		if (gruppo == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteGruppo] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteGruppo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteGruppo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDGruppo type = 3");
			DriverRegistroServiziDB_LIB.CRUDGruppo(CostantiDB.DELETE, gruppo, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteGruppo] Errore durante l'eliminazione del gruppo : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	
	protected List<Gruppo> gruppiList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "gruppiList";
		int idLista = Liste.GRUPPI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterServiceBinding = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVICE_BINDING);
		ServiceBinding serviceBinding = null;
		if(filterServiceBinding!=null) {
			serviceBinding = ServiceBinding.toEnumConstant(filterServiceBinding);
		}
				
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<Gruppo> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("gruppiList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<IDGruppo> listIdGruppi = null;
		try {

			ISQLQueryObject sqlQueryObjectServiceBinding = null;
			if(serviceBinding!=null) {
				sqlQueryObjectServiceBinding = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectServiceBinding.addWhereIsNullCondition("service_binding");
				sqlQueryObjectServiceBinding.addWhereCondition("service_binding= ?");
				sqlQueryObjectServiceBinding.setANDLogicOperator(false);
			}
			
			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);	
				if(serviceBinding!=null) {
					sqlQueryObject.addWhereCondition(sqlQueryObjectServiceBinding.createSQLConditions());
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(serviceBinding!=null) {
					sqlQueryObject.addWhereCondition(sqlQueryObjectServiceBinding.createSQLConditions());
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals(""))){
				stmt.setString(index++, superuser);
			}
			if(serviceBinding!=null) {
				stmt.setString(index++, serviceBinding.getValue());
			}

			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
				sqlQueryObject.addSelectField("nome");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				if(serviceBinding!=null) {
					sqlQueryObject.addWhereCondition(sqlQueryObjectServiceBinding.createSQLConditions());
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
				sqlQueryObject.addSelectField("nome");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(serviceBinding!=null) {
					sqlQueryObject.addWhereCondition(sqlQueryObjectServiceBinding.createSQLConditions());
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals(""))){
				stmt.setString(index++, superuser);
			}
			if(serviceBinding!=null) {
				stmt.setString(index++, serviceBinding.getValue());
			}
			risultato = stmt.executeQuery();

			listIdGruppi = new ArrayList<>();
			while (risultato.next()) {

				listIdGruppi.add(new IDGruppo(risultato.getString("nome")));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		
		if(listIdGruppi!=null){
			for (IDGruppo idGruppo : listIdGruppi) {
				try{
					lista.add(this.getGruppo(idGruppo));
				}catch(DriverRegistroServiziNotFound notFound){
					// non può capitare
					throw new DriverRegistroServiziException(notFound.getMessage(),notFound);
				}
			}
		}
		
		return lista;
	}
}
