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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDScope;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.Scope;
import org.openspcoop2.core.registry.constants.ScopeContesto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaScope;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_scopeDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_scopeDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_scopeDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected Scope getScope(
			IDScope idScope) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		this.driver.log.debug("richiesto getScope: " + idScope);
		// conrollo consistenza
		if (idScope == null)
			throw new DriverRegistroServiziException("[getScope] Parametro idScope is null");
		if (idScope.getNome()==null || idScope.getNome().trim().equals(""))
			throw new DriverRegistroServiziException("[getScope] Parametro idScope.nome non e' definito");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getScope(nome)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getScope] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SCOPE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			String queryString = sqlQueryObject
					.createSQLQuery();
			stm = con.prepareStatement(queryString);
			stm.setString(1, idScope.getNome());
			rs = stm.executeQuery();
			Scope scope = null;
			if (rs.next()) {
				scope = new Scope();
				scope.setId(rs.getLong("id"));
				scope.setNome(rs.getString("nome"));
				scope.setDescrizione(rs.getString("descrizione"));
				String tipologia = rs.getString("tipologia");
				if(tipologia!=null){
					scope.setTipologia(tipologia);
				}
				scope.setNomeEsterno(rs.getString("nome_esterno"));
				String contesto_utilizzo = rs.getString("contesto_utilizzo");
				if(contesto_utilizzo!=null){
					scope.setContestoUtilizzo(ScopeContesto.toEnumConstant(contesto_utilizzo));
				}
				scope.setSuperUser(rs.getString("superuser"));

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					scope.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getScope] rs.next non ha restituito valori con la seguente interrogazione :\n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(queryString, idScope.getNome()));
			}

			return scope;

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getScope] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getScope] Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
	}

	protected Scope getScope(
			long idScope) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		this.driver.log.debug("richiesto getScope: " + idScope);
		// conrollo consistenza
		if (idScope <=0)
			throw new DriverRegistroServiziException("[getScope] Parametro idScope non valido");

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getScope(id)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getScope] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		IDScope idScopeObject = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SCOPE);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addWhereCondition("id = ?");
			String queryString = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(queryString);
			stm.setLong(1, idScope);
			rs = stm.executeQuery();
			if (rs.next()) {
				idScopeObject = new IDScope(rs.getString("nome"));
			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getScope] rs.next non ha restituito valori con la seguente interrogazione :\n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(queryString, idScope));
			}

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getScope] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getScope] Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
			this.driver.closeConnection(con);
		}
		
		return this.getScope(idScopeObject);
	}

	protected List<IDScope> getAllIdScope(
			FiltroRicercaScope filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		boolean filtroRicercaTipo = false;
		if(filtroRicerca!=null){
			filtroRicercaTipo = StringUtils.isNotEmpty(filtroRicerca.getTipologia());
		}
		List<String> listTipologia = null;
		if(filtroRicercaTipo){
			listTipologia = new ArrayList<>();
			listTipologia.add(filtroRicerca.getTipologia());
		}
		
		boolean filtroRicercaContesto = false;
		if(filtroRicerca!=null){
			filtroRicercaContesto = filtroRicerca.getContesto()!=null && !ScopeContesto.QUALSIASI.equals(filtroRicerca.getContesto());
		}
		List<String> listContesto = null;
		if(filtroRicercaContesto){
			listContesto = new ArrayList<>();
			listContesto.add(ScopeContesto.QUALSIASI.getValue());
			listContesto.add(filtroRicerca.getContesto().getValue());
		}
		
		this.driver.log.debug("getAllIdScope...");

		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdScope");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SCOPE);
			sqlQueryObject.addSelectField("nome");
			if(filtroRicerca!=null){
				// Filtro By Data
				if(filtroRicerca.getMinDate()!=null)
					sqlQueryObject.addWhereCondition("ora_registrazione > ?");
				if(filtroRicerca.getMaxDate()!=null)
					sqlQueryObject.addWhereCondition("ora_registrazione < ?");
				if(filtroRicerca.getNome()!=null)
					sqlQueryObject.addWhereCondition("nome = ?");
				if(filtroRicercaTipo){
					sqlQueryObject.addWhereCondition(false,"tipologia = ?","tipologia = ?");
				}
				if(filtroRicercaContesto){
					sqlQueryObject.addWhereCondition(false,"contesto_utilizzo = ?","contesto_utilizzo = ?");
				}
				sqlQueryObject.addOrderBy("nome");
			}

			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			this.driver.log.debug("eseguo query : " + sqlQuery );
			stm = con.prepareStatement(sqlQuery);
			int indexStmt = 1;
			if(filtroRicerca!=null){
				if(filtroRicerca.getMinDate()!=null){
					this.driver.log.debug("minDate stmt.setTimestamp("+filtroRicerca.getMinDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMinDate().getTime()));
					indexStmt++;
				}
				if(filtroRicerca.getMaxDate()!=null){
					this.driver.log.debug("maxDate stmt.setTimestamp("+filtroRicerca.getMaxDate()+")");
					stm.setTimestamp(indexStmt, new Timestamp(filtroRicerca.getMaxDate().getTime()));
					indexStmt++;
				}	
				if(filtroRicerca.getNome()!=null){
					this.driver.log.debug("nome stmt.setString("+filtroRicerca.getNome()+")");
					stm.setString(indexStmt, filtroRicerca.getNome());
					indexStmt++;
				}	
				if(filtroRicercaTipo){
					for (int i = 0; i < listTipologia.size(); i++) {
						this.driver.log.debug("tipo stmt.setString("+listTipologia.get(i)+")");
						stm.setString(indexStmt, listTipologia.get(i));
						indexStmt++;
					}
				}
				if(filtroRicercaContesto){
					for (int i = 0; i < listContesto.size(); i++) {
						this.driver.log.debug("contesto stmt.setString("+listContesto.get(i)+")");
						stm.setString(indexStmt, listContesto.get(i));
						indexStmt++;
					}
				}
			}
			rs = stm.executeQuery();
			List<IDScope> nomiScope = new ArrayList<IDScope>();
			while (rs.next()) {
				nomiScope.add(new IDScope(rs.getString("nome")));
			}
			if(nomiScope.size()==0){
				if(filtroRicerca!=null)
					throw new DriverRegistroServiziNotFound("Scope non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverRegistroServiziNotFound("Scope non trovati");
			}else{
				return nomiScope;
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdScope error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
	}

	protected void createScope(Scope scope) throws DriverRegistroServiziException{
		if (scope == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createScope] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createScope");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createScope] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDScope type = 1");
			DriverRegistroServiziDB_LIB.CRUDScope(CostantiDB.CREATE, scope, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createScope] Errore durante la creazione del scope : " + qe.getMessage(), qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected boolean existsScope(IDScope idScope) throws DriverRegistroServiziException{
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (idScope == null)
			throw new DriverRegistroServiziException("Parametro non valido");

		if (idScope.getNome()==null || idScope.getNome().equals(""))
			throw new DriverRegistroServiziException("Parametro vuoto non valido");

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsScope");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsScope] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SCOPE);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, idScope.getNome());
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.driver.log.error("Errore durante verifica esistenza scope: "+e.getMessage(), e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);
		}

		return exist;
	}

	protected void updateScope(Scope scope) throws DriverRegistroServiziException{
		if (scope == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateScope] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateScope");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateScope] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {

			this.driver.log.debug("CRUDScope type = 2");
			DriverRegistroServiziDB_LIB.CRUDScope(CostantiDB.UPDATE, scope, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateScope] Errore durante l'aggiornamento del scope : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}	

	protected void deleteScope(Scope scope) throws DriverRegistroServiziException{
		if (scope == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteScope] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteScope");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteScope] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDScope type = 3");
			DriverRegistroServiziDB_LIB.CRUDScope(CostantiDB.DELETE, scope, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteScope] Errore durante l'eliminazione del scope : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<Scope> scopeList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "scopeList";
		int idLista = Liste.SCOPE;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterScopeTipologia = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SCOPE_TIPOLOGIA);
		String scopeTipologia = null;
		if(filterScopeTipologia!=null) {
//			scopeTipologia = filterScopeTipologia;
			scopeTipologia = StringUtils.isNotEmpty(filterScopeTipologia) ? filterScopeTipologia : null;
		}
		
		String filterScopeContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SCOPE_CONTESTO);
		org.openspcoop2.core.registry.constants.ScopeContesto scopeContesto = null;
		if(filterScopeContesto!=null) {
			scopeContesto = org.openspcoop2.core.registry.constants.ScopeContesto.toEnumConstant(filterScopeContesto);
		}

		boolean isFilterGruppoErogazione = false;
		boolean isFilterGruppoFruizione = false;
		String filterGruppo = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_GRUPPO);
		if(filterGruppo!=null && !"".equals(filterGruppo)) {
			isFilterGruppoErogazione = true;
			isFilterGruppoFruizione = true;
		}

		TipoPdD apiContesto = null;
		String filterApiContesto = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_API_CONTESTO);
		if(filterApiContesto!=null && !"".equals(filterApiContesto)) {
			apiContesto = TipoPdD.toTipoPdD(filterApiContesto);
			if(TipoPdD.APPLICATIVA.equals(apiContesto)) {
				isFilterGruppoFruizione = false;
			}
			else if(TipoPdD.DELEGATA.equals(apiContesto)) {
				isFilterGruppoErogazione = false;
			}
			else {
				apiContesto = null;
			}
		}
		
		String filterProtocollo = null;
		String filterProtocolli = null;
		List<String> tipoSoggettiProtocollo = null;
		String filterSoggettoTipo = null;
		String filterSoggettoNome = null;
		boolean filterSoggettoProprietario = false;
		boolean filterTipoSoggettoProtocollo = false;
		if(apiContesto!=null) {
			filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
			filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
			try {
				tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
			}catch(Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
			if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
				filterTipoSoggettoProtocollo=true;
			}
			
			String filterSoggettoTipoNome = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_SOGGETTO);
			if(filterSoggettoTipoNome!=null && !"".equals(filterSoggettoTipoNome)) {
				filterSoggettoTipo = filterSoggettoTipoNome.split("/")[0];
				filterSoggettoNome = filterSoggettoTipoNome.split("/")[1];
				filterSoggettoProprietario=true;
				filterTipoSoggettoProtocollo=false; // piu' specifico il filtro sul soggetto proprietario
			}
		}
		
		String filterApiImplementazione = null;
		IDServizio apiImplementazioneErogazione = null;
		IDFruizione apiImplementazioneFruizione = null;
		if(apiContesto!=null) {
			filterApiImplementazione = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_API_IMPLEMENTAZIONE);
			if(filterApiImplementazione!=null && !"".equals(filterApiImplementazione)) {
				if(TipoPdD.APPLICATIVA.equals(apiContesto)) {
					try {
						apiImplementazioneErogazione = IDServizio.toIDServizio(filterApiImplementazione);
						isFilterGruppoErogazione=false; // non ha piu' senso ho selezionato una erogazione puntuale
					}catch(Exception e) {
						throw new DriverRegistroServiziException("Filtro API Implementazione '"+filterApiImplementazione+"' non valido: "+e.getMessage(),e);
					}
				}
				else if(TipoPdD.DELEGATA.equals(apiContesto)) {
					try {
						apiImplementazioneFruizione = IDFruizione.toIDFruizione(filterApiImplementazione);
						isFilterGruppoFruizione=false; // non ha piu' senso ho selezionato una fruizione puntuale
					}catch(Exception e) {
						throw new DriverRegistroServiziException("Filtro API Implementazione '"+filterApiImplementazione+"' non valido: "+e.getMessage(),e);
					}
				}
			}
		}
		
		this.driver.log.debug("search : " + search);
		this.driver.log.debug("filterScopeTipologia : " + filterScopeTipologia);
		this.driver.log.debug("filterScopeContesto : " + filterScopeContesto);
		this.driver.log.debug("filterGruppo : " + filterGruppo);
		this.driver.log.debug("filterApiContesto : " + filterApiContesto);
		this.driver.log.debug("filterProtocollo : " + filterProtocollo);
		this.driver.log.debug("filterProtocolli : " + filterProtocolli);
		this.driver.log.debug("filterSoggettoNome : " + filterSoggettoNome);
		this.driver.log.debug("filterSoggettoTipo : " + filterSoggettoTipo);
		this.driver.log.debug("filterApiImplementazione : " + filterApiImplementazione);
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<Scope> lista = new ArrayList<Scope>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("scopeList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		List<IDScope> listIdScope = null;
		try {

			/* === Filtro Gruppo, Tipo Erogazione/Fruizione, Implementazione API === */
			
			List<String> existsConditions = new ArrayList<String>();
			List<Object> existsParameters = new ArrayList<Object>();
			String alias_PD_SCOPE = "pdscope";
			String alias_PD = "pd";
			String alias_PA_SCOPE = "pascope";
			String alias_PA = "pa";
			String alias_SERVIZI = "s";
			String alias_ACCORDI = "a";
			String alias_ACCORDI_GRUPPI = "ag";
			String alias_GRUPPI = "g";
			String alias_SOGGETTI = "sProprietario";
			
			// controllo accesso porte delegate
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE_SCOPE,alias_PD_SCOPE);
				sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
				if(isFilterGruppoFruizione) {
					sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
					sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
					sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
					sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
				}
				if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
				}
				sqlQueryObjectAutorizzazioniPorteDelegate.addSelectAliasField(alias_PD_SCOPE, "id", alias_PD_SCOPE+"id");
				sqlQueryObjectAutorizzazioniPorteDelegate.setANDLogicOperator(true);
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD_SCOPE+".scope = "+CostantiDB.SCOPE+".nome");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD_SCOPE+".id_porta = "+alias_PD+".id");
				if(isFilterGruppoFruizione) {
					sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD+".id_servizio = "+alias_SERVIZI+".id");
					sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
					sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
					sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
					sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_GRUPPI+".nome = ?");
					existsParameters.add(filterGruppo);
				}
				if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+alias_SOGGETTI+".id");
					if(apiImplementazioneFruizione!=null) {
						sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
						sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD+".tipo_soggetto_erogatore = ?");
						sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD+".nome_soggetto_erogatore = ?");
						sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD+".tipo_servizio = ?");
						sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD+".nome_servizio = ?");
						sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD+".versione_servizio = ?");
						existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
						existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
						existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
						existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
						existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
						existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
						existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
					}
					else if(filterSoggettoProprietario) {
						sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
						existsParameters.add(filterSoggettoTipo);
						existsParameters.add(filterSoggettoNome);
					}
					else if(filterTipoSoggettoProtocollo) {
						sqlQueryObjectAutorizzazioniPorteDelegate.addWhereINCondition(alias_SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
					}
				}
				
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteDelegate));
			} 
			
			
			// controllo accesso porte applicative
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE_SCOPE,alias_PA_SCOPE);
				sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
				if(isFilterGruppoErogazione) {
					sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
					sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
					sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
					sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
				}
				if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
				}
				sqlQueryObjectAutorizzazioniPorteApplicative.addSelectAliasField(alias_PA_SCOPE, "id", alias_PA_SCOPE+"id");
				sqlQueryObjectAutorizzazioniPorteApplicative.setANDLogicOperator(true);
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA_SCOPE+".scope = "+CostantiDB.SCOPE+".nome");
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA_SCOPE+".id_porta = "+alias_PA+".id");
				if(isFilterGruppoErogazione) {
					sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA+".id_servizio = "+alias_SERVIZI+".id");
					sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
					sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
					sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
					sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_GRUPPI+".nome = ?");
					existsParameters.add(filterGruppo);
				}
				if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+alias_SOGGETTI+".id");
					if(apiImplementazioneErogazione!=null) {
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA+".tipo_servizio = ?");
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA+".servizio = ?");
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA+".versione_servizio = ?");
						existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
						existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
						existsParameters.add(apiImplementazioneErogazione.getTipo());
						existsParameters.add(apiImplementazioneErogazione.getNome());
						existsParameters.add(apiImplementazioneErogazione.getVersione());
					}
					else if(filterSoggettoProprietario) {
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
						existsParameters.add(filterSoggettoTipo);
						existsParameters.add(filterSoggettoNome);
					}
					else if(filterTipoSoggettoProtocollo) {
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereINCondition(alias_SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
					}
				}
				
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteApplicative));
			} 
			
			
			
			
			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SCOPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);	
				if(scopeContesto!=null) {
					sqlQueryObject.addWhereCondition("contesto_utilizzo = ?");
				}
				if(scopeTipologia!=null) {
					sqlQueryObject.addWhereCondition("tipologia = ?");
				}
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SCOPE);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(scopeContesto!=null) {
					sqlQueryObject.addWhereCondition("contesto_utilizzo = ?");
				}
				if(scopeTipologia!=null) {
					sqlQueryObject.addWhereCondition("tipologia = ?");
				}
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals(""))){
				stmt.setString(index++, superuser);
			}
			if(scopeContesto!=null) {
				stmt.setString(index++, scopeContesto.getValue());
			}
			if(scopeTipologia!=null) {
				stmt.setString(index++, scopeTipologia);
			}
			if(existsParameters!=null && !existsParameters.isEmpty()) {
				for (Object object : existsParameters) {
					if(object instanceof String) {
						stmt.setString(index++, (String) object);
					}
					else {
						stmt.setInt(index++, (Integer) object);
					}
				}
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
				sqlQueryObject.addFromTable(CostantiDB.SCOPE);
				sqlQueryObject.addSelectField("nome");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				if(scopeContesto!=null) {
					sqlQueryObject.addWhereCondition("contesto_utilizzo = ?");
				}
				if(scopeTipologia!=null) {
					sqlQueryObject.addWhereCondition("tipologia = ?");
				}
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
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
				sqlQueryObject.addFromTable(CostantiDB.SCOPE);
				sqlQueryObject.addSelectField("nome");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(scopeContesto!=null) {
					sqlQueryObject.addWhereCondition("contesto_utilizzo = ?");
				}
				if(scopeTipologia!=null) {
					sqlQueryObject.addWhereCondition("tipologia = ?");
				}
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
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
			if(scopeContesto!=null) {
				stmt.setString(index++, scopeContesto.getValue());
			}
			if(scopeTipologia!=null) {
				stmt.setString(index++, scopeTipologia);
			}
			if(existsParameters!=null && !existsParameters.isEmpty()) {
				for (Object object : existsParameters) {
					if(object instanceof String) {
						stmt.setString(index++, (String) object);
					}
					else {
						stmt.setInt(index++, (Integer) object);
					}
				}
			}
			risultato = stmt.executeQuery();

			listIdScope = new ArrayList<>();
			while (risultato.next()) {

				listIdScope.add(new IDScope(risultato.getString("nome")));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		
		if(listIdScope!=null){
			for (IDScope idScope : listIdScope) {
				try{
					lista.add(this.getScope(idScope));
				}catch(DriverRegistroServiziNotFound notFound){
					// non può capitare
					throw new DriverRegistroServiziException(notFound.getMessage(),notFound);
				}
			}
		}
		
		return lista;
	}
}
