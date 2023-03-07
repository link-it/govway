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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Fruitore;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.constants.TipologiaServizio;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiParteSpecificaDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiParteSpecificaSearchDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_accordiParteSpecificaSearchDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
		
	protected List<AccordoServizioParteSpecifica> serviziList(String superuser,ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "serviziList";
		int idLista = Liste.SERVIZI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		this.driver.log.debug("search : " + search);

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<AccordoServizioParteSpecifica> lista = new ArrayList<AccordoServizioParteSpecifica>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("serviziList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereLikeCondition("nome_servizio", search, true, true);
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");

				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.setANDLogicOperator(true);
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				stmt.setString(1, superuser);
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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_servizio");
				sqlQueryObject.addSelectField("tipo_servizio");
				sqlQueryObject.addSelectField("versione_servizio");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addSelectField("servizio_correlato");
				sqlQueryObject.addSelectField("stato");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addWhereLikeCondition("nome_servizio", search, true, true);
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addOrderBy("tipo_servizio");
				sqlQueryObject.addOrderBy("nome_servizio");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome_servizio");
				sqlQueryObject.addSelectField("tipo_servizio");
				sqlQueryObject.addSelectField("versione_servizio");
				sqlQueryObject.addSelectField("id_soggetto");
				sqlQueryObject.addSelectField("id_accordo");
				sqlQueryObject.addSelectField("servizio_correlato");
				sqlQueryObject.addSelectField("stato");
				sqlQueryObject.setANDLogicOperator(true);
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addOrderBy("tipo_servizio");
				sqlQueryObject.addOrderBy("nome_servizio");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			this.driver.log.debug("query : " + queryString);

			stmt = con.prepareStatement(queryString);
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
				stmt.setString(1, superuser);
			risultato = stmt.executeQuery();

			AccordoServizioParteSpecifica asps;
			while (risultato.next()) {

				asps = new AccordoServizioParteSpecifica();
				
				asps.setId(risultato.getLong("id"));
				asps.setNome(risultato.getString("nome_servizio"));
				asps.setTipo(risultato.getString("tipo_servizio"));
				asps.setVersione(risultato.getInt("versione_servizio"));
				asps.setIdSoggetto(risultato.getLong("id_soggetto"));
				asps.setIdAccordo(risultato.getLong("id_accordo"));
				String servizio_correlato = risultato.getString("servizio_correlato");
				if ( (servizio_correlato != null) && 
						(servizio_correlato.equalsIgnoreCase(CostantiRegistroServizi.ABILITATO.toString()) || TipologiaServizio.CORRELATO.toString().equals(servizio_correlato)) 
						){
					asps.setTipologiaServizio(TipologiaServizio.CORRELATO);
				}else{
					asps.setTipologiaServizio(TipologiaServizio.NORMALE);
				}

				// informazioni su soggetto
				Soggetto sog = this.driver.getSoggetto(asps.getIdSoggetto(),con);
				String nomeErogatore = sog.getNome();
				String tipoErogatore = sog.getTipo();

				asps.setNomeSoggettoErogatore(nomeErogatore);
				asps.setTipoSoggettoErogatore(tipoErogatore);

				// informazioni su accordo
				AccordoServizioParteComune as = this.driver.getAccordoServizioParteComune(asps.getIdAccordo(),con);
				asps.setAccordoServizioParteComune(this.driver.idAccordoFactory.getUriFromAccordo(as));

				// Stato
				asps.setStatoPackage(risultato.getString("stato"));

				lista.add(asps);
			}

			this.driver.log.debug("size lista :" + ((lista == null) ? null : lista.size()));

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

	
	protected List<Soggetto> accordiErogatoriList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiErogatoriList";
		int idLista = Liste.ACCORDI_EROGATORI;
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
		PreparedStatement stmt2 = null;
		ResultSet risultato2 = null;

		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiErogatoriList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idAccordo);
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".descrizione");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".identificativo_porta");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".codice_ipa");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".id", "servid");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".nome_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".tipo_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".versione_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".servizio_correlato");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id_accordo");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI + ".id_soggetto = " + CostantiDB.SOGGETTI + ".id");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".nome_soggetto",search,true,true), 
						sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI + ".tipo_soggetto",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".tipo_soggetto");
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI + ".nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".nome_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".tipo_soggetto");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".id");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".descrizione");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".identificativo_porta");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI+".codice_ipa");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".id", "servid");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".nome_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".tipo_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".versione_servizio");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".servizio_correlato");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id_accordo");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();

			Soggetto sog = null;
			String oldNome = "";
			String oldTipo = "";
			while (risultato.next()) {
				String newNome = risultato.getString("nome_soggetto");
				String newTipo = risultato.getString("tipo_soggetto");

				// se il nome o il tipo sono diversi allora e' un soggetto nuovo
				// altrimenti e' sempre lo stesso soggetto
				// il controllo va fatto prima sul tipo in quanto il risultato
				// e' ordinato come tipo/nome
				if (sog == null || !oldTipo.equals(newTipo) || !oldNome.equals(newNome)) {

					oldTipo=newTipo;
					oldNome=newNome;
					// se sog e' null e' la prima volta che visito il result set
					// e non devo aggiunger nulla alla lista
					// altrimenti ho finito di aggiungere i servizi ad un
					// soggetto e devo metterlo nella lista
					// e iniziare ad aggiungere i servizi al nuovo soggetto
					// trovato
					if (sog != null)
						lista.add(sog);

					// creo il nuovo soggetto
					sog = new Soggetto();

					sog.setId(risultato.getLong("id"));
					sog.setNome(risultato.getString("nome_soggetto"));
					sog.setTipo(risultato.getString("tipo_soggetto"));
					sog.setDescrizione(risultato.getString("descrizione"));
					sog.setIdentificativoPorta(risultato.getString("identificativo_porta"));
					sog.setCodiceIpa(risultato.getString("codice_ipa"));
				}

				AccordoServizioParteSpecifica serv = new AccordoServizioParteSpecifica();
				serv.setId(risultato.getLong("servid"));
				serv.setNome(risultato.getString("nome_servizio"));
				serv.setTipo(risultato.getString("tipo_servizio"));
				serv.setVersione(risultato.getInt("versione_servizio"));

				if ( risultato.getString("servizio_correlato").equals(CostantiRegistroServizi.ABILITATO.toString()) ||
						TipologiaServizio.CORRELATO.toString().equals(risultato.getString("servizio_correlato"))) {
					serv.setTipologiaServizio(TipologiaServizio.CORRELATO);
				}
				else{
					serv.setTipologiaServizio(TipologiaServizio.NORMALE);
				}
				
				ISQLQueryObject sqlQueryObjectFruitori = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectFruitori.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObjectFruitori.addFromTable(CostantiDB.SERVIZI_FRUITORI);
				sqlQueryObjectFruitori.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectFruitori.addSelectField(CostantiDB.SOGGETTI,"tipo_soggetto");
				sqlQueryObjectFruitori.addSelectField(CostantiDB.SOGGETTI,"nome_soggetto");
				sqlQueryObjectFruitori.addWhereCondition(CostantiDB.SERVIZI+".id = ?");
				sqlQueryObjectFruitori.addWhereCondition(CostantiDB.SERVIZI+".id = "+CostantiDB.SERVIZI_FRUITORI+".id_servizio");
				sqlQueryObjectFruitori.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI_FRUITORI+".id_soggetto");
				sqlQueryObjectFruitori.setANDLogicOperator(true);
				sqlQueryObjectFruitori.setSelectDistinct(true);
				String queryStringFruitori = sqlQueryObjectFruitori.createSQLQuery();
				stmt2 = con.prepareStatement(queryStringFruitori);
				stmt2.setLong(1, serv.getId());
				risultato2 = stmt2.executeQuery();

				while (risultato2.next()) {
					String nome = risultato2.getString("nome_soggetto");
					String tipo = risultato2.getString("tipo_soggetto");
					Fruitore fruitore = new Fruitore();
					fruitore.setTipo(tipo);
					fruitore.setNome(nome);
					serv.addFruitore(fruitore);
				}
				risultato2.close();
				stmt2.close();

				serv.setTipoSoggettoErogatore(sog.getTipo());
				serv.setNomeSoggettoErogatore(sog.getNome());
				sog.addAccordoServizioParteSpecifica(serv);
			}

			//aggiungo l'ultimo soggetto alla lista
			if(sog!=null) lista.add(sog);

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			JDBCUtilities.closeResources(risultato2, stmt2);
			this.driver.closeConnection(con);
		}
	}

		
	protected List<AccordoServizioParteSpecifica> getServiziByIdErogatore(long idErogatore,ISearch filters) throws DriverRegistroServiziException {
		return getServiziByIdErogatoreAndFilters(idErogatore, filters);		
	}

	private List<AccordoServizioParteSpecifica> getServiziByIdErogatoreAndFilters(long idErogatore,ISearch filters) throws DriverRegistroServiziException {

		String nomeMetodo = "getServiziByIdErogatoreAndFilters";
		String queryString;
		int idLista = Liste.SERVIZI;
		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		int limit = filters.getPageSize(idLista);
		int offset = filters.getIndexIniziale(idLista);
		String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filters.getSearchString(idLista)) ? "" : filters.getSearchString(idLista));

		ArrayList<AccordoServizioParteSpecifica> lista = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getServiziByIdErogatoreAndFilters");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else {
			con = this.driver.globalConnection;
		}

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			//count
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectCountField("*", "cont");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.setANDLogicOperator(true);
			if (!search.equals("")) {
				//query con search
				sqlQueryObject.addWhereLikeCondition("nome_servizio", search, true, true);
			} 
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idErogatore);
			risultato = stmt.executeQuery();
			if (risultato.next())
				filters.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();



			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome_servizio");
			sqlQueryObject.addSelectField("tipo_servizio");
			sqlQueryObject.addSelectField("versione_servizio");
			sqlQueryObject.addSelectField("id_soggetto");	
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereLikeCondition("nome_servizio", search, true, true);
			} 

			sqlQueryObject.addOrderBy("nome_servizio");
			sqlQueryObject.addOrderBy("versione_servizio");
			sqlQueryObject.addOrderBy("tipo_servizio");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idErogatore);
			risultato = stmt.executeQuery();

			lista = new ArrayList<AccordoServizioParteSpecifica>();
			while (risultato.next()) {
				long id=risultato.getLong("id");
				AccordoServizioParteSpecifica se=this.driver.getAccordoServizioParteSpecifica(id, con);
				lista.add(se);
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

	protected List<AccordoServizioParteSpecifica> soggettiServizioList(String superuser, ISearch ricerca,boolean [] permessiUtente, 
			boolean gestioneFruitori, boolean gestioneErogatori) throws DriverRegistroServiziException {
		String nomeMetodo = "soggettiServizioList";
		int idLista = Liste.SERVIZI;
		int offset;
		int limit;
		String searchAPIErogazioneFruizione;
		String queryString;
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<AccordoServizioParteSpecifica> serviziList = new ArrayList<AccordoServizioParteSpecifica>();

		String aliasSoggettiFruitori = "soggettoFruitore";
		
		String aliasSoggettiReferenti = "soggettoReferente";
				
		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		searchAPIErogazioneFruizione = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
		ricerca.getSearchString(idLista);

		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoServiziProtocollo = null;
		try {
			tipoServiziProtocollo = Filtri.convertToTipiServizi(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}

		String filterTipoAPI = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SERVICE_BINDING);
		if(filterTipoAPI!=null && filterTipoAPI.equals("")) {
			filterTipoAPI = null;
		}
		
		String filterGruppo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_GRUPPO);
		if(filterGruppo!=null && filterGruppo.equals("")) {
			filterGruppo = null;
		}
		
		String filterApi = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_API);
		IDAccordo idAccordoApi = null;
		if(filterApi!=null && filterApi.equals("")) {
			filterApi = null;
		}
		if(filterApi!=null) {
			try {
				idAccordoApi = IDAccordoFactory.getInstance().getIDAccordoFromUri(filterApi);
			}catch(Exception e) {
				throw new DriverRegistroServiziException(e.getMessage(),e);
			}
		}
		
		String filterCanale = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CANALE);
		boolean searchCanale = false;
		boolean canaleDefault = false;
		if(gestioneFruitori || gestioneErogatori) {
			if(filterCanale!=null && !filterCanale.equals("")) {
				searchCanale = true;
				if(filterCanale.startsWith(Filtri.PREFIX_VALUE_CANALE_DEFAULT)) {
					filterCanale = filterCanale.substring(Filtri.PREFIX_VALUE_CANALE_DEFAULT.length());
					canaleDefault = true;
				}
			}
		}
		
		String filterDominio = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_DOMINIO);
		PddTipologia pddTipologia = null;
		if(filterDominio!=null && !"".equals(filterDominio)) {
			pddTipologia = PddTipologia.toPddTipologia(filterDominio);
		}
		
		String filterStatoAccordo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_STATO_ACCORDO);
		
		String filterSoggettoTipoNome = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_SOGGETTO);
		String filterSoggettoTipo = null;
		String filterSoggettoNome = null;
		if(filterSoggettoTipoNome!=null && !"".equals(filterSoggettoTipoNome)) {
			filterSoggettoTipo = filterSoggettoTipoNome.split("/")[0];
			filterSoggettoNome = filterSoggettoTipoNome.split("/")[1];
		}
		
		String filterSoggettoErogatoreContains = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_SOGGETTO_EROGATORE_CONTAINS);
		
		String filtroStatoAPIImpl = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_STATO);
		if((filtroStatoAPIImpl!=null && "".equals(filtroStatoAPIImpl))) {
			filtroStatoAPIImpl=null;
		}
		
		String filtroAutenticazioneTokenPolicy = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_AUTENTICAZIONE_TOKEN_TIPO);
		if((filtroAutenticazioneTokenPolicy!=null && "".equals(filtroAutenticazioneTokenPolicy))) {
			filtroAutenticazioneTokenPolicy=null;
		}
		
		String filtroAutenticazioneTrasportoTipo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_AUTENTICAZIONE_TRASPORTO_TIPO);
		if((filtroAutenticazioneTrasportoTipo!=null && "".equals(filtroAutenticazioneTrasportoTipo))) {
			filtroAutenticazioneTrasportoTipo=null;
		}
		String filtroAutenticazioneTrasportoTipoPlugin = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_AUTENTICAZIONE_TRASPORTO_TIPO_PLUGIN);
		if((filtroAutenticazioneTrasportoTipoPlugin!=null && "".equals(filtroAutenticazioneTrasportoTipoPlugin))) {
			filtroAutenticazioneTrasportoTipoPlugin=null;
		}
		if(filtroAutenticazioneTrasportoTipoPlugin!=null && !org.openspcoop2.core.constants.Costanti.VALUE_PARAMETRO_CUSTOM_IN_SELECT.equals(filtroAutenticazioneTrasportoTipo)) {
			filtroAutenticazioneTrasportoTipoPlugin=null;
		}
		String filtroAutenticazioneTrasporto = (filtroAutenticazioneTrasportoTipoPlugin!=null) ? filtroAutenticazioneTrasportoTipoPlugin : filtroAutenticazioneTrasportoTipo;
		
		String filtroRateLimitingStato = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_RATE_LIMITING_STATO);
		if((filtroRateLimitingStato!=null && "".equals(filtroRateLimitingStato))) {
			filtroRateLimitingStato=null;
		}
		
		String filtroValidazioneStato = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_VALIDAZIONE_STATO);
		if((filtroValidazioneStato!=null && "".equals(filtroValidazioneStato))) {
			filtroValidazioneStato=null;
		}
		
		String filtroCacheRispostaStato = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_CACHE_RISPOSTA_STATO);
		if((filtroCacheRispostaStato!=null && "".equals(filtroCacheRispostaStato))) {
			filtroCacheRispostaStato=null;
		}
		
		String filtroMessageSecurityStato = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_MESSAGE_SECURITY_STATO);
		if((filtroMessageSecurityStato!=null && "".equals(filtroMessageSecurityStato))) {
			filtroMessageSecurityStato=null;
		}
		
		String filtroMTOMStato = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_MTOM_STATO);
		if((filtroMTOMStato!=null && "".equals(filtroMTOMStato))) {
			filtroMTOMStato=null;
		}
		if(filtroMTOMStato!=null && "rest".equalsIgnoreCase(filterTipoAPI)) {
			filtroMTOMStato=null;
		}
		
		String filtroTrasformazione = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_TRASFORMAZIONE_STATO);
		if((filtroTrasformazione!=null && "".equals(filtroTrasformazione))) {
			filtroTrasformazione=null;
		}
		
		String filtroCorrelazioneApplicativa = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_CORRELAZIONE_APPLICATIVA_STATO);
		if((filtroCorrelazioneApplicativa!=null && "".equals(filtroCorrelazioneApplicativa))) {
			filtroCorrelazioneApplicativa=null;
		}
		
		String filtroConfigurazioneDumpTipo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_DUMP_TIPO);
		if((filtroConfigurazioneDumpTipo!=null && "".equals(filtroConfigurazioneDumpTipo))) {
			filtroConfigurazioneDumpTipo=null;
		}
		
		String filtroCORS = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_CORS_TIPO);
		if((filtroCORS!=null && "".equals(filtroCORS))) {
			filtroCORS=null;
		}
		String filtroCORS_origin = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONFIGURAZIONE_CORS_ORIGIN);
		if((filtroCORS_origin!=null && "".equals(filtroCORS_origin))) {
			filtroCORS_origin=null;
		}
		if((filtroCORS_origin!=null && !Filtri.FILTRO_CONFIGURAZIONE_CORS_TIPO_VALORE_RIDEFINITO_ABILITATO.equals(filtroCORS))) {
			filtroCORS_origin=null;
		}
		
		String filtroConnettoreTipo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_TIPO);
		String filtroConnettoreTipoPlugin = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_TIPO_PLUGIN);
		String filtroConnettoreTokenPolicy = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_TOKEN_POLICY);
		String filtroConnettoreEndpoint = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_ENDPOINT);
		String filtroConnettoreKeystore = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_KEYSTORE);
		String filtroConnettoreDebug = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_CONNETTORE_DEBUG);
		if((filtroConnettoreTipo!=null && "".equals(filtroConnettoreTipo))) {
			filtroConnettoreTipo=null;
		}
		if((filtroConnettoreTipoPlugin!=null && "".equals(filtroConnettoreTipoPlugin))) {
			filtroConnettoreTipoPlugin=null;
		}
		if((filtroConnettoreTokenPolicy!=null && "".equals(filtroConnettoreTokenPolicy))) {
			filtroConnettoreTokenPolicy=null;
		}
		if((filtroConnettoreEndpoint!=null && "".equals(filtroConnettoreEndpoint))) {
			filtroConnettoreEndpoint=null;
		}
		if((filtroConnettoreKeystore!=null && "".equals(filtroConnettoreKeystore))) {
			filtroConnettoreKeystore=null;
		}
		if((filtroConnettoreDebug!=null && "".equals(filtroConnettoreDebug))) {
			filtroConnettoreDebug=null;
		}
		boolean joinConnettore =  filtroConnettoreTipo!=null	|| filtroConnettoreTokenPolicy!=null || filtroConnettoreEndpoint!=null || filtroConnettoreKeystore!=null || filtroConnettoreDebug!=null;
		TipiConnettore tipoConnettore = null;
		String endpointType = null;
		boolean tipoConnettoreIntegrationManager = false; 
		if(filtroConnettoreTipo!=null && !"".equals(filtroConnettoreTipo)) {
			if(Filtri.FILTRO_CONNETTORE_TIPO_VALORE_IM.equals(filtroConnettoreTipo)) {
				tipoConnettoreIntegrationManager = true;
			}
			else {
				tipoConnettore = TipiConnettore.toEnumFromName(filtroConnettoreTipo);
				if(tipoConnettore!=null) {
					endpointType = (TipiConnettore.CUSTOM.equals(tipoConnettore)) ? filtroConnettoreTipoPlugin : tipoConnettore.getNome();
				}
			}
		}
		 
		String filtroModISicurezzaCanale = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_CANALE);
		String filtroModISicurezzaMessaggio = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_MESSAGGIO);
		String filtroModIKeystorePath = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_KEYSTORE_PATH);
		String filtroModIAudience = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_AUDIENCE);
		String filtroModIInfoUtente = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_INFORMAZIONI_UTENTE);
		String filtroModIDigestRichiesta = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_DIGEST_RICHIESTA);
		if((filtroModISicurezzaCanale!=null && "".equals(filtroModISicurezzaCanale))) {
			filtroModISicurezzaCanale=null;
		}
		if((filtroModISicurezzaMessaggio!=null && "".equals(filtroModISicurezzaMessaggio))) {
			filtroModISicurezzaMessaggio=null;
		}
		Boolean filtroModIDigestRichiestaEnabled = null;
		if(CostantiDB.STATO_FUNZIONALITA_ABILITATO.equals(filtroModIDigestRichiesta)) {
			filtroModIDigestRichiestaEnabled = true;
		}
		else if(CostantiDB.STATO_FUNZIONALITA_DISABILITATO.equals(filtroModIDigestRichiesta)) {
			filtroModIDigestRichiestaEnabled = false;
		}
		Boolean filtroModIInfoUtenteEnabled = null;
		if(CostantiDB.STATO_FUNZIONALITA_ABILITATO.equals(filtroModIInfoUtente)) {
			filtroModIInfoUtenteEnabled = true;
		}
		else if(CostantiDB.STATO_FUNZIONALITA_DISABILITATO.equals(filtroModIInfoUtente)) {
			filtroModIInfoUtenteEnabled = false;
		}
		if((filtroModIKeystorePath!=null && "".equals(filtroModIKeystorePath))) {
			filtroModIKeystorePath=null;
		}
		if((filtroModIAudience!=null && "".equals(filtroModIAudience))) {
			filtroModIAudience=null;
		}
		boolean filtroModI = filtroModISicurezzaCanale!=null || filtroModISicurezzaMessaggio!=null ||
				filtroModIDigestRichiestaEnabled!=null || filtroModIInfoUtenteEnabled!=null ||
				filtroModIKeystorePath!=null || filtroModIAudience!=null;
		
		String filtroProprietaNome = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROPRIETA_NOME);
		String filtroProprietaValore = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROPRIETA_VALORE);
		if((filtroProprietaNome!=null && "".equals(filtroProprietaNome))) {
			filtroProprietaNome=null;
		}
		if((filtroProprietaValore!=null && "".equals(filtroProprietaValore))) {
			filtroProprietaValore=null;
		}
		boolean filtroProprieta = filtroProprietaNome!=null || filtroProprietaValore!=null;
		
		this.driver.log.debug("search : " + searchAPIErogazioneFruizione);
		this.driver.log.debug("filterSoggettoErogatoreContains : "+filterSoggettoErogatoreContains);
		this.driver.log.debug("filterProtocollo : " + filterProtocollo);
		this.driver.log.debug("filterProtocolli : " + filterProtocolli);
		this.driver.log.debug("filterTipoAPI : " + filterTipoAPI);
		this.driver.log.debug("filterGruppo : " + filterGruppo);
		this.driver.log.debug("filterApi : " + filterApi);
		this.driver.log.debug("filterCanale : " + filterCanale);
		this.driver.log.debug("filterDominio : " + filterDominio);
		this.driver.log.debug("filterStatoAccordo : " + filterStatoAccordo);
		this.driver.log.debug("filterSoggettoNome : " + filterSoggettoNome);
		this.driver.log.debug("filterSoggettoTipo : " + filterSoggettoTipo);
		this.driver.log.debug("filtroStatoAPIImpl : " + filtroStatoAPIImpl);
		this.driver.log.debug("filtroAutenticazioneTokenPolicy : " + filtroAutenticazioneTokenPolicy);
		this.driver.log.debug("filtroAutenticazioneTrasporto : " + filtroAutenticazioneTrasporto);
		this.driver.log.debug("filtroRateLimitingStato : " + filtroRateLimitingStato);
		this.driver.log.debug("filtroValidazioneStato : " + filtroValidazioneStato);
		this.driver.log.debug("filtroCacheRispostaStato : " + filtroCacheRispostaStato);
		this.driver.log.debug("filtroMessageSecurityStato : " + filtroMessageSecurityStato);
		this.driver.log.debug("filtroMTOMStato : " + filtroMTOMStato);
		this.driver.log.debug("filtroTrasformazione : " + filtroTrasformazione);
		this.driver.log.debug("filtroCorrelazioneApplicativa : " + filtroCorrelazioneApplicativa);
		this.driver.log.debug("filtroConfigurazioneDumpTipo : " + filtroConfigurazioneDumpTipo);
		this.driver.log.debug("filtroCORS : " + filtroCORS);
		this.driver.log.debug("filtroCORS_origin : " + filtroCORS_origin);
		this.driver.log.debug("filtroConnettoreTipo : " + filtroConnettoreTipo);
		this.driver.log.debug("filtroConnettoreTipoPlugin : " + filtroConnettoreTipoPlugin);
		this.driver.log.debug("filtroConnettoreTokenPolicy : " + filtroConnettoreTokenPolicy);
		this.driver.log.debug("filtroConnettoreEndpoint : " + filtroConnettoreEndpoint);
		this.driver.log.debug("filtroConnettoreKeystore : " + filtroConnettoreKeystore);
		this.driver.log.debug("filtroConnettoreDebug : " + filtroConnettoreDebug);
		this.driver.log.debug("filtroConnettoreTipoPlugin : " + filtroConnettoreTipoPlugin);
		this.driver.log.debug("filtroModISicurezzaCanale : " + filtroModISicurezzaCanale);
		this.driver.log.debug("filtroModISicurezzaMessaggio : " + filtroModISicurezzaMessaggio);
		this.driver.log.debug("filtroModIKeystorePath : " + filtroModIKeystorePath);
		this.driver.log.debug("filtroModIAudience : " + filtroModIAudience);
		this.driver.log.debug("filtroModIInfoUtente : " + filtroModIInfoUtente);
		this.driver.log.debug("filtroModIDigestRichiesta : " + filtroModIDigestRichiesta);
		this.driver.log.debug("filtroProprietaNome : " + filtroProprietaNome);
		this.driver.log.debug("filtroProprietaValore : " + filtroProprietaValore);
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("soggettiServizioList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			ISQLQueryObject sqlQueryObjectAccordiComposti = null;			
			if (permessiUtente != null) {
				sqlQueryObjectAccordiComposti = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectAccordiComposti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObjectAccordiComposti.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPOSTO, "id");
				sqlQueryObjectAccordiComposti.setANDLogicOperator(true);
				sqlQueryObjectAccordiComposti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo="+CostantiDB.ACCORDI+".id");
			}

			ISQLQueryObject sqlQueryObjectSoggettiErogatoreContains = null;
			if (!filterSoggettoErogatoreContains.equals("")) {
				sqlQueryObjectSoggettiErogatoreContains = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectSoggettiErogatoreContains.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObjectSoggettiErogatoreContains.addSelectField(CostantiDB.SOGGETTI, "tipo_soggetto");
				sqlQueryObjectSoggettiErogatoreContains.addSelectField(CostantiDB.SOGGETTI, "nome_soggetto");
				sqlQueryObjectSoggettiErogatoreContains.setANDLogicOperator(true);
				sqlQueryObjectSoggettiErogatoreContains.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto="+CostantiDB.SOGGETTI+".id");
				sqlQueryObjectSoggettiErogatoreContains.addWhereCondition(false,
						//sqlQueryObjectSoggettiErogatoreContains.getWhereLikeCondition("tipo_soggetto", search, true, true),
						sqlQueryObjectSoggettiErogatoreContains.getWhereLikeCondition("nome_soggetto", filterSoggettoErogatoreContains, true, true));
			}

			ISQLQueryObject sqlQueryObjectPdd = null;
			if(pddTipologia!=null && PddTipologia.ESTERNO.equals(pddTipologia)) {
				ISQLQueryObject sqlQueryObjectExistsPdd = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectExistsPdd.addSelectField(CostantiDB.PDD+".nome");
				sqlQueryObjectExistsPdd.addFromTable(CostantiDB.PDD);
				sqlQueryObjectExistsPdd.setANDLogicOperator(true);
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server");
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+".tipo=?");
				
				sqlQueryObjectPdd = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectPdd.setANDLogicOperator(false);
				sqlQueryObjectPdd.addWhereIsNullCondition(CostantiDB.SOGGETTI+".server");
				sqlQueryObjectPdd.addWhereExistsCondition(false, sqlQueryObjectExistsPdd);
			}
						
			if (!searchAPIErogazioneFruizione.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				if(permessiUtente!=null || filterTipoAPI!=null || filterGruppo!=null || idAccordoApi!=null || searchCanale || filtroModI
						|| !searchAPIErogazioneFruizione.equals("") // aggiunto per cercare anche sul nome dell'API (parte comune)
					) {
					sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo="+CostantiDB.ACCORDI+".id");
					if(filterGruppo!=null) {
						sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
						sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo="+CostantiDB.ACCORDI+".id");
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo="+CostantiDB.GRUPPI+".id");
					}
					if(idAccordoApi!=null) {
						sqlQueryObject.addFromTable(CostantiDB.SOGGETTI, aliasSoggettiReferenti);
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+aliasSoggettiReferenti+".id");
					}
				}
				if(gestioneFruitori) {
					sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI, aliasSoggettiFruitori);
					if(searchCanale) {
						sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
					}
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione="+CostantiDB.SERVIZI_FRUITORI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".is_default=1");
					if(searchCanale) {
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
					}
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio="+CostantiDB.SERVIZI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto="+aliasSoggettiFruitori+".id");
					if(joinConnettore) {
						DBUtils.setFiltriConnettoreFruizione(sqlQueryObject, this.driver.tipoDB, 
								tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
								filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
					}
					if(filtroModI) {
						DBUtils.setFiltriModIFruizione(sqlQueryObject, this.driver.tipoDB,
								filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
								filtroModIDigestRichiestaEnabled, filtroModIInfoUtenteEnabled,
								filtroModIKeystorePath, filtroModIAudience);
					}
					if(filtroProprieta) {
						DBUtils.setFiltriProprietaFruizione(sqlQueryObject, this.driver.tipoDB, 
								filtroProprietaNome, filtroProprietaValore);
					}
					if(filtroStatoAPIImpl!=null || 
							filtroAutenticazioneTokenPolicy!=null || filtroAutenticazioneTrasporto!=null || 
							filtroRateLimitingStato!=null || 
							filtroValidazioneStato!=null || 
							filtroCacheRispostaStato!=null ||
							filtroMessageSecurityStato!=null ||
							filtroMTOMStato!=null ||
							filtroTrasformazione!=null ||
							filtroCorrelazioneApplicativa!=null ||
							filtroConfigurazioneDumpTipo!=null ||
							filtroCORS!=null) {
						DBUtils.setFiltriConfigurazioneFruizione(sqlQueryObject, this.driver.tipoDB,
								filtroStatoAPIImpl,
								filtroAutenticazioneTokenPolicy, filtroAutenticazioneTrasporto,
								filtroRateLimitingStato,
								filtroValidazioneStato,
								filtroCacheRispostaStato,
								filtroMessageSecurityStato,
								filtroMTOMStato,
								filtroTrasformazione,
								filtroCorrelazioneApplicativa,
								filtroConfigurazioneDumpTipo,
								filtroCORS, filtroCORS_origin);
					}					
				}
				if(gestioneErogatori) {
					sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
					if(searchCanale) {
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					}
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione="+CostantiDB.SERVIZI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".is_default=1");
					if(searchCanale) {
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
					}
					if(joinConnettore) {
						DBUtils.setFiltriConnettoreErogazione(sqlQueryObject, this.driver.tipoDB, 
								tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
								filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
					}
					if(filtroModI) {
						DBUtils.setFiltriModIErogazione(sqlQueryObject, this.driver.tipoDB,
								filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
								filtroModIDigestRichiestaEnabled, filtroModIInfoUtenteEnabled,
								filtroModIKeystorePath, filtroModIAudience);
					}
					if(filtroProprieta) {
						DBUtils.setFiltriProprietaErogazione(sqlQueryObject, this.driver.tipoDB, 
								filtroProprietaNome, filtroProprietaValore);
					}
					if(filtroStatoAPIImpl!=null || 
							filtroAutenticazioneTokenPolicy!=null || filtroAutenticazioneTrasporto!=null || 
							filtroRateLimitingStato!=null || 
							filtroValidazioneStato!=null || 
							filtroCacheRispostaStato!=null ||
							filtroMessageSecurityStato!=null ||
							filtroMTOMStato!=null ||
							filtroTrasformazione!=null ||
							filtroCorrelazioneApplicativa!=null ||
							filtroConfigurazioneDumpTipo!=null ||
							filtroCORS!=null) {
						DBUtils.setFiltriConfigurazioneErogazione(sqlQueryObject, this.driver.tipoDB,
								filtroStatoAPIImpl,
								filtroAutenticazioneTokenPolicy, filtroAutenticazioneTrasporto,
								filtroRateLimitingStato,
								filtroValidazioneStato,
								filtroCacheRispostaStato,
								filtroMessageSecurityStato,
								filtroMTOMStato,
								filtroTrasformazione,
								filtroCorrelazioneApplicativa,
								filtroConfigurazioneDumpTipo,
								filtroCORS, filtroCORS_origin);
					}
				}
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					if(gestioneFruitori) {
						sqlQueryObject.addWhereCondition(aliasSoggettiFruitori+".tipo_soggetto=?");
						sqlQueryObject.addWhereCondition(aliasSoggettiFruitori+".nome_soggetto=?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
					}
				}
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoServiziProtocollo!=null && tipoServiziProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SERVIZI+".tipo_servizio", true, tipoServiziProtocollo.toArray(new String[1]));
				}
				if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".service_binding = ?");
				}
				if(filterGruppo!=null && !filterGruppo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".nome = ?");
				}
				if(idAccordoApi!=null) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".nome = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".versione = ?");
					sqlQueryObject.addWhereCondition(aliasSoggettiReferenti+".tipo_soggetto = ?");
					sqlQueryObject.addWhereCondition(aliasSoggettiReferenti+".nome_soggetto = ?");
				}
				if(pddTipologia!=null) {
					if(PddTipologia.ESTERNO.equals(pddTipologia)) {
						sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
					}
					else {
						sqlQueryObject.addFromTable(CostantiDB.PDD);
						sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server",CostantiDB.PDD+".tipo=?");
					}
				}
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".stato = ?");
				}
				if(sqlQueryObjectSoggettiErogatoreContains!=null) {
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectSoggettiErogatoreContains);
				}
				sqlQueryObject.addWhereCondition(false, 
						
						// - ricerca su tipo/nome/versione servizio
						//sqlQueryObject.getWhereLikeCondition("tipo_servizio", search, true, true), 
						sqlQueryObject.getWhereLikeCondition("nome_servizio", searchAPIErogazioneFruizione, true, true),
						//sqlQueryObject.getWhereLikeCondition("versione_servizio", search, true, true),
						
						// - ricerca su nome dell'API (parte comune)
						sqlQueryObject.getWhereLikeCondition(CostantiDB.ACCORDI+".nome", searchAPIErogazioneFruizione, true, true)
						
					);

				if(permessiUtente != null){
					// solo S
					if(permessiUtente[0] && !permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectAccordiComposti);

					// solo P
					if(!permessiUtente[0] && permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectAccordiComposti);

					// P che S come ora non aggiungo la condizione
				}

				if(searchCanale) {
					String tabellaPorta = gestioneErogatori ? CostantiDB.PORTE_APPLICATIVE : CostantiDB.PORTE_DELEGATE;
					if(canaleDefault) {
						sqlQueryObject.addWhereCondition(false, 
								(tabellaPorta+".canale = ?"),
								("( "+ tabellaPorta+".canale is null AND ("+CostantiDB.ACCORDI+".canale = ?  OR "+CostantiDB.ACCORDI+".canale is null) )"));
					}
					else {
						sqlQueryObject.addWhereCondition(false, 
								(tabellaPorta+".canale = ?"),
								("( "+ tabellaPorta+".canale is null AND "+CostantiDB.ACCORDI+".canale = ? )"));
					}
				}
				
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				if(permessiUtente!=null || filterTipoAPI!=null || filterGruppo!=null || idAccordoApi!=null || searchCanale || filtroModI) {
					sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo="+CostantiDB.ACCORDI+".id");
					if(filterGruppo!=null) {
						sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
						sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo="+CostantiDB.ACCORDI+".id");
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo="+CostantiDB.GRUPPI+".id");
					}
					if(idAccordoApi!=null) {
						sqlQueryObject.addFromTable(CostantiDB.SOGGETTI, aliasSoggettiReferenti);
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+aliasSoggettiReferenti+".id");
					}
				}
				if(gestioneFruitori) {
					sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI, aliasSoggettiFruitori);
					if(searchCanale) {
						sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
					}
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione="+CostantiDB.SERVIZI_FRUITORI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".is_default=1");
					if(searchCanale) {
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
					}
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio="+CostantiDB.SERVIZI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto="+aliasSoggettiFruitori+".id");
					if(joinConnettore) {
						DBUtils.setFiltriConnettoreFruizione(sqlQueryObject, this.driver.tipoDB, 
								tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
								filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
					}
					if(filtroModI) {
						DBUtils.setFiltriModIFruizione(sqlQueryObject, this.driver.tipoDB,
								filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
								filtroModIDigestRichiestaEnabled, filtroModIInfoUtenteEnabled,
								filtroModIKeystorePath, filtroModIAudience);
					}
					if(filtroProprieta) {
						DBUtils.setFiltriProprietaFruizione(sqlQueryObject, this.driver.tipoDB, 
								filtroProprietaNome, filtroProprietaValore);
					}
					if(filtroStatoAPIImpl!=null || 
							filtroAutenticazioneTokenPolicy!=null || filtroAutenticazioneTrasporto!=null || 
							filtroRateLimitingStato!=null || 
							filtroValidazioneStato!=null || 
							filtroCacheRispostaStato!=null ||
							filtroMessageSecurityStato!=null ||
							filtroMTOMStato!=null ||
							filtroTrasformazione!=null ||
							filtroCorrelazioneApplicativa!=null ||
							filtroConfigurazioneDumpTipo!=null ||
							filtroCORS!=null) {
						DBUtils.setFiltriConfigurazioneFruizione(sqlQueryObject, this.driver.tipoDB,
								filtroStatoAPIImpl,
								filtroAutenticazioneTokenPolicy, filtroAutenticazioneTrasporto,
								filtroRateLimitingStato,
								filtroValidazioneStato,
								filtroCacheRispostaStato,
								filtroMessageSecurityStato,
								filtroMTOMStato,
								filtroTrasformazione,
								filtroCorrelazioneApplicativa,
								filtroConfigurazioneDumpTipo,
								filtroCORS, filtroCORS_origin);
					}
				}
				if(gestioneErogatori) {
					sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
					if(searchCanale) {
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					}
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione="+CostantiDB.SERVIZI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".is_default=1");
					if(searchCanale) {
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
					}
					if(joinConnettore) {
						DBUtils.setFiltriConnettoreErogazione(sqlQueryObject, this.driver.tipoDB, 
								tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
								filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
					}
					if(filtroModI) {
						DBUtils.setFiltriModIErogazione(sqlQueryObject, this.driver.tipoDB,
								filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
								filtroModIDigestRichiestaEnabled, filtroModIInfoUtenteEnabled,
								filtroModIKeystorePath, filtroModIAudience);
					}
					if(filtroProprieta) {
						DBUtils.setFiltriProprietaErogazione(sqlQueryObject, this.driver.tipoDB, 
								filtroProprietaNome, filtroProprietaValore);
					}
					if(filtroStatoAPIImpl!=null || 
							filtroAutenticazioneTokenPolicy!=null || filtroAutenticazioneTrasporto!=null || 
							filtroRateLimitingStato!=null || 
							filtroValidazioneStato!=null || 
							filtroCacheRispostaStato!=null ||
							filtroMessageSecurityStato!=null ||
							filtroMTOMStato!=null ||
							filtroTrasformazione!=null ||
							filtroCorrelazioneApplicativa!=null ||
							filtroConfigurazioneDumpTipo!=null ||
							filtroCORS!=null) {
						DBUtils.setFiltriConfigurazioneErogazione(sqlQueryObject, this.driver.tipoDB,
								filtroStatoAPIImpl,
								filtroAutenticazioneTokenPolicy, filtroAutenticazioneTrasporto,
								filtroRateLimitingStato,
								filtroValidazioneStato,
								filtroCacheRispostaStato,
								filtroMessageSecurityStato,
								filtroMTOMStato,
								filtroTrasformazione,
								filtroCorrelazioneApplicativa,
								filtroConfigurazioneDumpTipo,
								filtroCORS, filtroCORS_origin);
					}
				}
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					if(gestioneFruitori) {
						sqlQueryObject.addWhereCondition(aliasSoggettiFruitori+".tipo_soggetto=?");
						sqlQueryObject.addWhereCondition(aliasSoggettiFruitori+".nome_soggetto=?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
					}
				}
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoServiziProtocollo!=null && tipoServiziProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SERVIZI+".tipo_servizio", true, tipoServiziProtocollo.toArray(new String[1]));
				}
				if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".service_binding = ?");
				}
				if(filterGruppo!=null && !filterGruppo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".nome = ?");
				}
				if(idAccordoApi!=null) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".nome = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".versione = ?");
					sqlQueryObject.addWhereCondition(aliasSoggettiReferenti+".tipo_soggetto = ?");
					sqlQueryObject.addWhereCondition(aliasSoggettiReferenti+".nome_soggetto = ?");
				}
				if(pddTipologia!=null) {
					if(PddTipologia.ESTERNO.equals(pddTipologia)) {
						sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
					}
					else {
						sqlQueryObject.addFromTable(CostantiDB.PDD);
						sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server",CostantiDB.PDD+".tipo=?");
					}
				}
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".stato = ?");
				}
				if(sqlQueryObjectSoggettiErogatoreContains!=null) {
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectSoggettiErogatoreContains);
				}

				if(permessiUtente != null){
					// solo S
					if(permessiUtente[0] && !permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectAccordiComposti);

					// solo P
					if(!permessiUtente[0] && permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectAccordiComposti);

					// P che S come ora non aggiungo la condizione
				}

				if(searchCanale) {
					String tabellaPorta = gestioneErogatori ? CostantiDB.PORTE_APPLICATIVE : CostantiDB.PORTE_DELEGATE;
					if(canaleDefault) {
						sqlQueryObject.addWhereCondition(false, 
								(tabellaPorta+".canale = ?"),
								("( "+ tabellaPorta+".canale is null AND ("+CostantiDB.ACCORDI+".canale = ?  OR "+CostantiDB.ACCORDI+".canale is null) )"));
					}
					else {
						sqlQueryObject.addWhereCondition(false, 
								(tabellaPorta+".canale = ?"),
								("( "+ tabellaPorta+".canale is null AND "+CostantiDB.ACCORDI+".canale = ? )"));
					}
				}
				
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
				stmt.setString(index++, filterSoggettoTipo);
				stmt.setString(index++, filterSoggettoNome);
			}
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
				stmt.setString(index++, superuser);
			if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
				stmt.setString(index++, filterTipoAPI);
			}
			if(filterGruppo!=null && !filterGruppo.equals("")) {
				stmt.setString(index++, filterGruppo);
			}
			if(idAccordoApi!=null) {
				stmt.setString(index++, idAccordoApi.getNome());
				stmt.setInt(index++, idAccordoApi.getVersione());
				stmt.setString(index++, idAccordoApi.getSoggettoReferente().getTipo());
				stmt.setString(index++, idAccordoApi.getSoggettoReferente().getNome());
			}
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				stmt.setString(index++, filterStatoAccordo);
			}
			if(searchCanale) {
				stmt.setString(index++, filterCanale);
				stmt.setString(index++, filterCanale);
			}
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt(1));
			risultato.close();
			stmt.close();

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!searchAPIErogazioneFruizione.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				if(permessiUtente!=null || filterTipoAPI!=null || filterGruppo!=null || idAccordoApi!=null || searchCanale || filtroModI
						|| !searchAPIErogazioneFruizione.equals("") // aggiunto per cercare anche sul nome dell'API (parte comune)
					) {
					sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo="+CostantiDB.ACCORDI+".id");
					if(filterGruppo!=null) {
						sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
						sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo="+CostantiDB.ACCORDI+".id");
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo="+CostantiDB.GRUPPI+".id");
					}
					if(idAccordoApi!=null) {
						sqlQueryObject.addFromTable(CostantiDB.SOGGETTI, aliasSoggettiReferenti);
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+aliasSoggettiReferenti+".id");
					}
				}
				if(gestioneFruitori) {
					sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI, aliasSoggettiFruitori);
					if(searchCanale) {
						sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
					}
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione="+CostantiDB.SERVIZI_FRUITORI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".is_default=1");
					if(searchCanale) {
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
					}
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio="+CostantiDB.SERVIZI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto="+aliasSoggettiFruitori+".id");
					if(joinConnettore) {
						DBUtils.setFiltriConnettoreFruizione(sqlQueryObject, this.driver.tipoDB, 
								tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
								filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
					}
					if(filtroModI) {
						DBUtils.setFiltriModIFruizione(sqlQueryObject, this.driver.tipoDB,
								filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
								filtroModIDigestRichiestaEnabled, filtroModIInfoUtenteEnabled,
								filtroModIKeystorePath, filtroModIAudience);
					}
					if(filtroProprieta) {
						DBUtils.setFiltriProprietaFruizione(sqlQueryObject, this.driver.tipoDB, 
								filtroProprietaNome, filtroProprietaValore);
					}
					if(filtroStatoAPIImpl!=null || 
							filtroAutenticazioneTokenPolicy!=null || filtroAutenticazioneTrasporto!=null || 
							filtroRateLimitingStato!=null || 
							filtroValidazioneStato!=null || 
							filtroCacheRispostaStato!=null ||
							filtroMessageSecurityStato!=null ||
							filtroMTOMStato!=null ||
							filtroTrasformazione!=null ||
							filtroCorrelazioneApplicativa!=null ||
							filtroConfigurazioneDumpTipo!=null ||
							filtroCORS!=null) {
						DBUtils.setFiltriConfigurazioneFruizione(sqlQueryObject, this.driver.tipoDB,
								filtroStatoAPIImpl,
								filtroAutenticazioneTokenPolicy, filtroAutenticazioneTrasporto,
								filtroRateLimitingStato,
								filtroValidazioneStato,
								filtroCacheRispostaStato,
								filtroMessageSecurityStato,
								filtroMTOMStato,
								filtroTrasformazione,
								filtroCorrelazioneApplicativa,
								filtroConfigurazioneDumpTipo,
								filtroCORS, filtroCORS_origin);
					}
				}
				if(gestioneErogatori) {
					sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
					if(searchCanale) {
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					}
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione="+CostantiDB.SERVIZI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".is_default=1");
					if(searchCanale) {
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
					}
					if(joinConnettore) {
						DBUtils.setFiltriConnettoreErogazione(sqlQueryObject, this.driver.tipoDB, 
								tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
								filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
					}
					if(filtroModI) {
						DBUtils.setFiltriModIErogazione(sqlQueryObject, this.driver.tipoDB,
								filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
								filtroModIDigestRichiestaEnabled, filtroModIInfoUtenteEnabled,
								filtroModIKeystorePath, filtroModIAudience);
					}
					if(filtroProprieta) {
						DBUtils.setFiltriProprietaErogazione(sqlQueryObject, this.driver.tipoDB, 
								filtroProprietaNome, filtroProprietaValore);
					}
					if(filtroStatoAPIImpl!=null || 
							filtroAutenticazioneTokenPolicy!=null || filtroAutenticazioneTrasporto!=null || 
							filtroRateLimitingStato!=null || 
							filtroValidazioneStato!=null || 
							filtroCacheRispostaStato!=null ||
							filtroMessageSecurityStato!=null ||
							filtroMTOMStato!=null ||
							filtroTrasformazione!=null ||
							filtroCorrelazioneApplicativa!=null ||
							filtroConfigurazioneDumpTipo!=null ||
							filtroCORS!=null) {
						DBUtils.setFiltriConfigurazioneErogazione(sqlQueryObject, this.driver.tipoDB,
								filtroStatoAPIImpl,
								filtroAutenticazioneTokenPolicy, filtroAutenticazioneTrasporto,
								filtroRateLimitingStato,
								filtroValidazioneStato,
								filtroCacheRispostaStato,
								filtroMessageSecurityStato,
								filtroMTOMStato,
								filtroTrasformazione,
								filtroCorrelazioneApplicativa,
								filtroConfigurazioneDumpTipo,
								filtroCORS, filtroCORS_origin);
					}
				}
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".nome_servizio", "nomeServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".tipo_servizio", "tipoServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".versione_servizio", "versioneServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".id_soggetto","idSoggettoErogatore");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id_accordo");
				sqlQueryObject.addSelectField("servizio_correlato");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".stato","statoServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI,"descrizione","descrizioneServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI+".nome_soggetto","nomeSoggettoErogatore");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI+".tipo_soggetto","tipoSoggettoErogatore");
				sqlQueryObject.addSelectField("port_type");
				if(gestioneFruitori) {
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI+".id","idFruizione");
					sqlQueryObject.addSelectAliasField(aliasSoggettiFruitori+".id","idSoggettoFruitore");
					sqlQueryObject.addSelectAliasField(aliasSoggettiFruitori+".nome_soggetto","nomeSoggettoFruitore");
					sqlQueryObject.addSelectAliasField(aliasSoggettiFruitori+".tipo_soggetto","tipoSoggettoFruitore");
				}
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					if(gestioneFruitori) {
						sqlQueryObject.addWhereCondition(aliasSoggettiFruitori+".tipo_soggetto=?");
						sqlQueryObject.addWhereCondition(aliasSoggettiFruitori+".nome_soggetto=?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
					}
				}
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoServiziProtocollo!=null && tipoServiziProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SERVIZI+".tipo_servizio", true, tipoServiziProtocollo.toArray(new String[1]));
				}
				if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".service_binding = ?");
				}
				if(filterGruppo!=null && !filterGruppo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".nome = ?");
				}
				if(idAccordoApi!=null) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".nome = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".versione = ?");
					sqlQueryObject.addWhereCondition(aliasSoggettiReferenti+".tipo_soggetto = ?");
					sqlQueryObject.addWhereCondition(aliasSoggettiReferenti+".nome_soggetto = ?");
				}
				if(pddTipologia!=null) {
					if(PddTipologia.ESTERNO.equals(pddTipologia)) {
						sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
					}
					else {
						sqlQueryObject.addFromTable(CostantiDB.PDD);
						sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server",CostantiDB.PDD+".tipo=?");
					}
				}
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".stato = ?");
				}
				if(sqlQueryObjectSoggettiErogatoreContains!=null) {
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectSoggettiErogatoreContains);
				}
				sqlQueryObject.addWhereCondition(false, 
						
						// - ricerca su tipo/nome/versione servizio
						//sqlQueryObject.getWhereLikeCondition("tipo_servizio", search, true, true), 
						sqlQueryObject.getWhereLikeCondition("nome_servizio", searchAPIErogazioneFruizione, true, true),
						//sqlQueryObject.getWhereLikeCondition("versione_servizio", search, true, true),
						
						// - ricerca su nome dell'API (parte comune)
						sqlQueryObject.getWhereLikeCondition(CostantiDB.ACCORDI+".nome", searchAPIErogazioneFruizione, true, true)
					);
				
				if(permessiUtente != null){
					// solo S
					if(permessiUtente[0] && !permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectAccordiComposti);

					// solo P
					if(!permessiUtente[0] && permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectAccordiComposti);

					// P che S come ora non aggiungo la condizione
				}
				
				if(searchCanale) {
					String tabellaPorta = gestioneErogatori ? CostantiDB.PORTE_APPLICATIVE : CostantiDB.PORTE_DELEGATE;
					if(canaleDefault) {
						sqlQueryObject.addWhereCondition(false, 
								(tabellaPorta+".canale = ?"),
								("( "+ tabellaPorta+".canale is null AND ("+CostantiDB.ACCORDI+".canale = ?  OR "+CostantiDB.ACCORDI+".canale is null) )"));
					}
					else {
						sqlQueryObject.addWhereCondition(false, 
								(tabellaPorta+".canale = ?"),
								("( "+ tabellaPorta+".canale is null AND "+CostantiDB.ACCORDI+".canale = ? )"));
					}
				}
				
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nomeServizio");
				sqlQueryObject.addOrderBy("versioneServizio");
				sqlQueryObject.addOrderBy("nomeSoggettoErogatore");
				sqlQueryObject.addOrderBy("tipoServizio");
				sqlQueryObject.addOrderBy("tipoSoggettoErogatore");
				if(gestioneFruitori) {
					sqlQueryObject.addOrderBy("nomeSoggettoFruitore");
					sqlQueryObject.addOrderBy("tipoSoggettoFruitore");
				}

				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				if(permessiUtente!=null || filterTipoAPI!=null || filterGruppo!=null || idAccordoApi!=null || searchCanale || filtroModI) {
					sqlQueryObject.addFromTable(CostantiDB.ACCORDI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_accordo="+CostantiDB.ACCORDI+".id");
					if(filterGruppo!=null) {
						sqlQueryObject.addFromTable(CostantiDB.ACCORDI_GRUPPI);
						sqlQueryObject.addFromTable(CostantiDB.GRUPPI);
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_accordo="+CostantiDB.ACCORDI+".id");
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_GRUPPI+".id_gruppo="+CostantiDB.GRUPPI+".id");
					}
					if(idAccordoApi!=null) {
						sqlQueryObject.addFromTable(CostantiDB.SOGGETTI, aliasSoggettiReferenti);
						sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".id_referente="+aliasSoggettiReferenti+".id");
					}
				}
				if(gestioneFruitori) {
					sqlQueryObject.addFromTable(CostantiDB.MAPPING_FRUIZIONE_PD);
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_FRUITORI);
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI, aliasSoggettiFruitori);
					if(searchCanale) {
						sqlQueryObject.addFromTable(CostantiDB.PORTE_DELEGATE);
					}
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_fruizione="+CostantiDB.SERVIZI_FRUITORI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".is_default=1");
					if(searchCanale) {
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_FRUIZIONE_PD+".id_porta="+CostantiDB.PORTE_DELEGATE+".id");
					}
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_servizio="+CostantiDB.SERVIZI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_FRUITORI+".id_soggetto="+aliasSoggettiFruitori+".id");
					if(joinConnettore) {
						DBUtils.setFiltriConnettoreFruizione(sqlQueryObject, this.driver.tipoDB, 
								tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
								filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
					}
					if(filtroModI) {
						DBUtils.setFiltriModIFruizione(sqlQueryObject, this.driver.tipoDB,
								filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
								filtroModIDigestRichiestaEnabled, filtroModIInfoUtenteEnabled,
								filtroModIKeystorePath, filtroModIAudience);
					}
					if(filtroProprieta) {
						DBUtils.setFiltriProprietaFruizione(sqlQueryObject, this.driver.tipoDB, 
								filtroProprietaNome, filtroProprietaValore);
					}
					if(filtroStatoAPIImpl!=null || 
							filtroAutenticazioneTokenPolicy!=null || filtroAutenticazioneTrasporto!=null || 
							filtroRateLimitingStato!=null || 
							filtroValidazioneStato!=null || 
							filtroCacheRispostaStato!=null ||
							filtroMessageSecurityStato!=null ||
							filtroMTOMStato!=null ||
							filtroTrasformazione!=null ||
							filtroCorrelazioneApplicativa!=null ||
							filtroConfigurazioneDumpTipo!=null ||
							filtroCORS!=null) {
						DBUtils.setFiltriConfigurazioneFruizione(sqlQueryObject, this.driver.tipoDB,
								filtroStatoAPIImpl,
								filtroAutenticazioneTokenPolicy, filtroAutenticazioneTrasporto,
								filtroRateLimitingStato,
								filtroValidazioneStato,
								filtroCacheRispostaStato,
								filtroMessageSecurityStato,
								filtroMTOMStato,
								filtroTrasformazione,
								filtroCorrelazioneApplicativa,
								filtroConfigurazioneDumpTipo,
								filtroCORS, filtroCORS_origin);
					}
				}
				if(gestioneErogatori) {
					sqlQueryObject.addFromTable(CostantiDB.MAPPING_EROGAZIONE_PA);
					if(searchCanale) {
						sqlQueryObject.addFromTable(CostantiDB.PORTE_APPLICATIVE);
					}
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_erogazione="+CostantiDB.SERVIZI+".id");
					sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".is_default=1");
					if(searchCanale) {
						sqlQueryObject.addWhereCondition(CostantiDB.MAPPING_EROGAZIONE_PA+".id_porta="+CostantiDB.PORTE_APPLICATIVE+".id");
					}
					if(joinConnettore) {
						DBUtils.setFiltriConnettoreErogazione(sqlQueryObject, this.driver.tipoDB, 
								tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
								filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
					}
					if(filtroModI) {
						DBUtils.setFiltriModIErogazione(sqlQueryObject, this.driver.tipoDB,
								filtroModISicurezzaCanale, filtroModISicurezzaMessaggio,
								filtroModIDigestRichiestaEnabled, filtroModIInfoUtenteEnabled,
								filtroModIKeystorePath, filtroModIAudience);
					}
					if(filtroProprieta) {
						DBUtils.setFiltriProprietaErogazione(sqlQueryObject, this.driver.tipoDB, 
								filtroProprietaNome, filtroProprietaValore);
					}
					if(filtroStatoAPIImpl!=null || 
							filtroAutenticazioneTokenPolicy!=null || filtroAutenticazioneTrasporto!=null || 
							filtroRateLimitingStato!=null || 
							filtroValidazioneStato!=null || 
							filtroCacheRispostaStato!=null ||
							filtroMessageSecurityStato!=null ||
							filtroMTOMStato!=null ||
							filtroTrasformazione!=null ||
							filtroCorrelazioneApplicativa!=null ||
							filtroConfigurazioneDumpTipo!=null ||
							filtroCORS!=null) {
						DBUtils.setFiltriConfigurazioneErogazione(sqlQueryObject, this.driver.tipoDB,
								filtroStatoAPIImpl,
								filtroAutenticazioneTokenPolicy, filtroAutenticazioneTrasporto,
								filtroRateLimitingStato,
								filtroValidazioneStato,
								filtroCacheRispostaStato,
								filtroMessageSecurityStato,
								filtroMTOMStato,
								filtroTrasformazione,
								filtroCorrelazioneApplicativa,
								filtroConfigurazioneDumpTipo,
								filtroCORS, filtroCORS_origin);
					}
				}
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".nome_servizio", "nomeServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".tipo_servizio", "tipoServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".versione_servizio", "versioneServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".id_soggetto","idSoggettoErogatore");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI+".id_accordo");
				sqlQueryObject.addSelectField("servizio_correlato");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI+".stato","statoServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI,"descrizione","descrizioneServizio");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI+".nome_soggetto","nomeSoggettoErogatore");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI+".tipo_soggetto","tipoSoggettoErogatore");
				sqlQueryObject.addSelectField("port_type");
				if(gestioneFruitori) {
					sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_FRUITORI+".id","idFruizione");
					sqlQueryObject.addSelectAliasField(aliasSoggettiFruitori+".id","idSoggettoFruitore");
					sqlQueryObject.addSelectAliasField(aliasSoggettiFruitori+".nome_soggetto","nomeSoggettoFruitore");
					sqlQueryObject.addSelectAliasField(aliasSoggettiFruitori+".tipo_soggetto","tipoSoggettoFruitore");
				}
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					if(gestioneFruitori) {
						sqlQueryObject.addWhereCondition(aliasSoggettiFruitori+".tipo_soggetto=?");
						sqlQueryObject.addWhereCondition(aliasSoggettiFruitori+".nome_soggetto=?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
						sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
					}
				}
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				
				if(tipoServiziProtocollo!=null && tipoServiziProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SERVIZI+".tipo_servizio", true, tipoServiziProtocollo.toArray(new String[1]));
				}
				if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".service_binding = ?");
				}
				if(filterGruppo!=null && !filterGruppo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.GRUPPI+".nome = ?");
				}
				if(idAccordoApi!=null) {
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".nome = ?");
					sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI+".versione = ?");
					sqlQueryObject.addWhereCondition(aliasSoggettiReferenti+".tipo_soggetto = ?");
					sqlQueryObject.addWhereCondition(aliasSoggettiReferenti+".nome_soggetto = ?");
				}
				if(pddTipologia!=null) {
					if(PddTipologia.ESTERNO.equals(pddTipologia)) {
						sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
					}
					else {
						sqlQueryObject.addFromTable(CostantiDB.PDD);
						sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+".nome="+CostantiDB.SOGGETTI+".server",CostantiDB.PDD+".tipo=?");
					}
				}
				if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".stato = ?");
				}
				if(sqlQueryObjectSoggettiErogatoreContains!=null) {
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectSoggettiErogatoreContains);
				}
				
				if(permessiUtente != null){
					// solo S
					if(permessiUtente[0] && !permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectAccordiComposti);

					// solo P
					if(!permessiUtente[0] && permessiUtente[1])
						sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectAccordiComposti);

					// P che S come ora non aggiungo la condizione
				}
				
				if(searchCanale) {
					String tabellaPorta = gestioneErogatori ? CostantiDB.PORTE_APPLICATIVE : CostantiDB.PORTE_DELEGATE;
					if(canaleDefault) {
						sqlQueryObject.addWhereCondition(false, 
								(tabellaPorta+".canale = ?"),
								("( "+ tabellaPorta+".canale is null AND ("+CostantiDB.ACCORDI+".canale = ?  OR "+CostantiDB.ACCORDI+".canale is null) )"));
					}
					else {
						sqlQueryObject.addWhereCondition(false, 
								(tabellaPorta+".canale = ?"),
								("( "+ tabellaPorta+".canale is null AND "+CostantiDB.ACCORDI+".canale = ? )"));
					}
				}
				
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nomeServizio");
				sqlQueryObject.addOrderBy("versioneServizio");
				sqlQueryObject.addOrderBy("nomeSoggettoErogatore");
				sqlQueryObject.addOrderBy("tipoServizio");
				sqlQueryObject.addOrderBy("tipoSoggettoErogatore");
				if(gestioneFruitori) {
					sqlQueryObject.addOrderBy("nomeSoggettoFruitore");
					sqlQueryObject.addOrderBy("tipoSoggettoFruitore");
				}

				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}

			this.driver.log.debug("query : " + queryString);

			stmt = con.prepareStatement(queryString);
			index = 1;
			if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
				stmt.setString(index++, filterSoggettoTipo);
				stmt.setString(index++, filterSoggettoNome);
			}
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
				stmt.setString(index++, superuser);
			if(filterTipoAPI!=null && !filterTipoAPI.equals("")) {
				stmt.setString(index++, filterTipoAPI);
			}
			if(filterGruppo!=null && !filterGruppo.equals("")) {
				stmt.setString(index++, filterGruppo);
			}
			if(idAccordoApi!=null) {
				stmt.setString(index++, idAccordoApi.getNome());
				stmt.setInt(index++, idAccordoApi.getVersione());
				stmt.setString(index++, idAccordoApi.getSoggettoReferente().getTipo());
				stmt.setString(index++, idAccordoApi.getSoggettoReferente().getNome());
			}
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			if(filterStatoAccordo!=null && !filterStatoAccordo.equals("")) {
				stmt.setString(index++, filterStatoAccordo);
			}
			if(searchCanale) {
				stmt.setString(index++, filterCanale);
				stmt.setString(index++, filterCanale);
			}
			risultato = stmt.executeQuery();

			while (risultato.next()) {
				AccordoServizioParteSpecifica serv = new AccordoServizioParteSpecifica();
				
				serv.setId(risultato.getLong("id"));
				serv.setNome(risultato.getString("nomeServizio"));
				serv.setTipo(risultato.getString("tipoServizio"));
				serv.setVersione(risultato.getInt("versioneServizio"));
				serv.setPortType(risultato.getString("port_type"));
				serv.setIdSoggetto(risultato.getLong("idSoggettoErogatore"));
				serv.setIdAccordo(risultato.getLong("id_accordo"));
				String servizio_correlato = risultato.getString("servizio_correlato");
				if( (servizio_correlato != null) && 
						(servizio_correlato.equalsIgnoreCase(CostantiRegistroServizi.ABILITATO.toString()) ||
								TipologiaServizio.CORRELATO.toString().equals(servizio_correlato))){
					serv.setTipologiaServizio(TipologiaServizio.CORRELATO);
				}else{
					serv.setTipologiaServizio(TipologiaServizio.NORMALE);
				}
				serv.setDescrizione(risultato.getString("descrizioneServizio"));

				// informazioni su soggetto
				Soggetto sog = this.driver.getSoggetto(serv.getIdSoggetto(),con);
				String nomeErogatore = sog.getNome();
				String tipoErogatore = sog.getTipo();

				serv.setNomeSoggettoErogatore(nomeErogatore);
				serv.setTipoSoggettoErogatore(tipoErogatore);

				// informazioni su accordo
				IDAccordo idAccordo = this.driver.getIdAccordoServizioParteComune(serv.getIdAccordo(), con);
				serv.setAccordoServizioParteComune(this.driver.idAccordoFactory.getUriFromIDAccordo(idAccordo));

				// Stato
				serv.setStatoPackage(risultato.getString("statoServizio"));
				
				if(gestioneFruitori) {
					
					Fruitore fruitore = new Fruitore();
					fruitore.setId(risultato.getLong("idFruizione"));
					fruitore.setTipo(risultato.getString("tipoSoggettoFruitore"));
					fruitore.setNome(risultato.getString("nomeSoggettoFruitore"));
					fruitore.setIdServizio(serv.getId());
					fruitore.setIdSoggetto(risultato.getLong("idSoggettoFruitore"));
					
					serv.addFruitore(fruitore);
				}

				serviziList.add(serv);
			}

			return serviziList;

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
