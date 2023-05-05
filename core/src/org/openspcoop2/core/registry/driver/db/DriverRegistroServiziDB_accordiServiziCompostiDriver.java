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
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDAccordoCooperazione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioComposto;
import org.openspcoop2.core.registry.AccordoServizioParteComuneServizioCompostoServizioComponente;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Documento;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.ProprietariDocumento;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_accordiServiziCompostiDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_accordiServiziCompostiDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_accordiServiziCompostiDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected void readAccordoServizioComposto(AccordoServizioParteComune as,Connection conParam) throws DriverRegistroServiziException,DriverRegistroServiziNotFound{
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		PreparedStatement stm2 = null;
		ResultSet rs2 = null;
		String sqlQuery = null;

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if(conParam!=null)
				con = conParam;
			else if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("readAccordoServizioComposto");
			else
				con = this.driver.globalConnection;

			if(as.getId()==null || as.getId()<=0)
				throw new Exception("Accordo id non definito");

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("id_accordo = ?");
			sqlQueryObject.setANDLogicOperator(true);
			sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setLong(1, as.getId());

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, as.getId()));
			rs = stm.executeQuery();

			AccordoServizioParteComuneServizioComposto asComposto = null;
			if (rs.next()) {

				asComposto = new AccordoServizioParteComuneServizioComposto();
				asComposto.setId(rs.getLong("id"));
				asComposto.setIdAccordoCooperazione(rs.getLong("id_accordo_cooperazione"));

				IDAccordoCooperazione idAccordoCooperazione = this.driver.getIdAccordoCooperazione(asComposto.getIdAccordoCooperazione(), con);
				String uriAccordo = this.driver.idAccordoCooperazioneFactory.getUriFromIDAccordo(idAccordoCooperazione);
				asComposto.setAccordoCooperazione(uriAccordo);
			}
			rs.close();
			stm.close();

			if(asComposto!=null){

				// read servizi componenti
				sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
				sqlQueryObject.addSelectField("*");
				sqlQueryObject.addWhereCondition("id_servizio_composto = ?");
				sqlQueryObject.setANDLogicOperator(true);
				sqlQuery = sqlQueryObject.createSQLQuery();
				stm = con.prepareStatement(sqlQuery);
				stm.setLong(1, asComposto.getId());
				this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(sqlQuery, asComposto.getId()));
				rs = stm.executeQuery();

				while (rs.next()) {

					AccordoServizioParteComuneServizioCompostoServizioComponente asComponente = new AccordoServizioParteComuneServizioCompostoServizioComponente();
					asComponente.setIdServizioComponente(rs.getLong("id_servizio_componente"));
					asComponente.setAzione(rs.getString("azione"));

					AccordoServizioParteSpecifica aspsServizioComponente = this.driver.getAccordoServizioParteSpecifica(asComponente.getIdServizioComponente(),con);
					asComponente.setTipo(aspsServizioComponente.getTipo());
					asComponente.setNome(aspsServizioComponente.getNome());
					asComponente.setVersione(aspsServizioComponente.getVersione());
					asComponente.setTipoSoggetto(aspsServizioComponente.getTipoSoggettoErogatore());
					asComponente.setNomeSoggetto(aspsServizioComponente.getNomeSoggettoErogatore());

					asComposto.addServizioComponente(asComponente);
				}
				rs.close();
				stm.close();


				// setto all'interno dell'accordo
				as.setServizioComposto(asComposto);
			}

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::readPortTypes] Exception :" + se.getMessage(),se);
		} finally {

			JDBCUtilities.closeResources(rs2, stm2);

			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(conParam, con);

		}
	}

	
	protected List<AccordoServizioParteComune> accordiServizio_serviziComponentiConSoggettoErogatore(IDSoggetto idSoggetto) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiServizio_serviziComponentiConSoggettoErogatore";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteComune> idAccordoServizio = new ArrayList<AccordoServizioParteComune>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiServizio_serviziComponentiConSoggettoErogatore");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SERVIZI);

			sqlQueryObjectSoggetti.addSelectAliasField(CostantiDB.ACCORDI, "id","idAccordoServizio");

			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id="+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente="+CostantiDB.SERVIZI+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI+".id_soggetto");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			sqlQueryObjectSoggetti.setANDLogicOperator(true);
			queryString = sqlQueryObjectSoggetti.createSQLQuery();

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			stmt.setString(1,idSoggetto.getTipo());
			stmt.setString(2,idSoggetto.getNome());
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				long idAccordoServizioLong = risultato.getLong("idAccordoServizio");
				idAccordoServizio.add(this.driver.getAccordoServizioParteComune(idAccordoServizioLong));

			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(con);
		}


		return idAccordoServizio;
	}

	protected List<AccordoServizioParteComune> accordiServizio_serviziComponenti(IDServizio idServizio) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiServizio_serviziComponenti";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteComune> idAccordoServizio = new ArrayList<AccordoServizioParteComune>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiServizio_serviziComponenti");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SOGGETTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.SERVIZI);

			sqlQueryObjectSoggetti.addSelectAliasField(CostantiDB.ACCORDI, "id","idAccordoServizio");

			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id="+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente="+CostantiDB.SERVIZI+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SERVIZI+".id_soggetto");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI+".tipo_servizio=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI+".nome_servizio=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.SERVIZI+".versione_servizio=?");
			sqlQueryObjectSoggetti.setANDLogicOperator(true);
			queryString = sqlQueryObjectSoggetti.createSQLQuery();

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			stmt.setString(1,idServizio.getSoggettoErogatore().getTipo());
			stmt.setString(2,idServizio.getSoggettoErogatore().getNome());
			stmt.setString(3, idServizio.getTipo());
			stmt.setString(4, idServizio.getNome());
			stmt.setInt(5, idServizio.getVersione());
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				long idAccordoServizioLong = risultato.getLong("idAccordoServizio");
				idAccordoServizio.add(this.driver.getAccordoServizioParteComune(idAccordoServizioLong));

			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(con);
		}


		return idAccordoServizio;
	}


	protected List<AccordoServizioParteComune> accordiServizioWithAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiServizioWithAccordoCooperazione";

		String queryString;

		Connection con = null;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		ArrayList<AccordoServizioParteComune> idAccordoServizio = new ArrayList<AccordoServizioParteComune>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiServizioWithAccordoCooperazione");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {
			ISQLQueryObject sqlQueryObjectSoggetti = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);

			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObjectSoggetti.addFromTable(CostantiDB.ACCORDI_COOPERAZIONE);

			sqlQueryObjectSoggetti.addSelectAliasField(CostantiDB.ACCORDI, "id","idAccordoServizio");

			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI+".id="+CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo_cooperazione="+CostantiDB.ACCORDI_COOPERAZIONE+".id");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".nome=?");
			sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".versione=?");
			if(idAccordoCooperazione.getSoggettoReferente()!=null){
				sqlQueryObjectSoggetti.addWhereCondition(CostantiDB.ACCORDI_COOPERAZIONE+".id_referente=?");
			}
			sqlQueryObjectSoggetti.setANDLogicOperator(true);
			queryString = sqlQueryObjectSoggetti.createSQLQuery();

			this.driver.logDebug("eseguo query : " + DriverRegistroServiziDB_LIB.formatSQLString(queryString));

			stmt = con.prepareStatement(queryString);
			stmt.setString(1,idAccordoCooperazione.getNome());
			stmt.setInt(2,idAccordoCooperazione.getVersione());
			if(idAccordoCooperazione.getSoggettoReferente()!=null){
				stmt.setLong(3, DBUtils.getIdSoggetto(idAccordoCooperazione.getSoggettoReferente().getNome(), idAccordoCooperazione.getSoggettoReferente().getTipo(), con, this.driver.tipoDB));
			}
			risultato = stmt.executeQuery();

			while (risultato.next()) {

				long idAccordoServizioLong = risultato.getLong("idAccordoServizio");
				idAccordoServizio.add(this.driver.getAccordoServizioParteComune(idAccordoServizioLong));

			}

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(con);
		}


		return idAccordoServizio;
	}
	
	protected long getIdServizioCorrelato(String nomeServizio, String tipoServizio, String nomeProprietario, String tipoProprietario, Connection con) throws DriverRegistroServiziException {
		PreparedStatement stm = null;
		ResultSet rs = null;
		long idSoggetto;
		long idServizio = 0;
		try {
			idSoggetto = DBUtils.getIdSoggetto(nomeProprietario, tipoProprietario, con, this.driver.tipoDB, this.driver.tabellaSoggetti);

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("tipo_servizio = ?");
			sqlQueryObject.addWhereCondition("nome_servizio = ?");
			sqlQueryObject.addWhereCondition("id_soggetto = ?");
			sqlQueryObject.addWhereCondition("servizio_correlato = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String query = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(query);
			stm.setString(1, tipoServizio);
			stm.setString(2, nomeServizio);
			stm.setLong(3, idSoggetto);
			stm.setString(4, CostantiRegistroServizi.ABILITATO.toString());
			rs = stm.executeQuery();

			if (rs.next()) {
				idServizio = rs.getLong("id");
			}

			return idServizio;

		} catch (CoreException e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} catch (Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

		}
	}
	
	protected List<AccordoServizioParteComuneServizioCompostoServizioComponente> accordiComponentiList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiComponentiList";
		int idLista = Liste.ACCORDI_COMPONENTI;
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

		ArrayList<AccordoServizioParteComuneServizioCompostoServizioComponente> lista = new ArrayList<AccordoServizioParteComuneServizioCompostoServizioComponente>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiComponentiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {

			/*
			 *  SELECT * from acc_serv_composti composti, acc_serv_componenti componenti 
			 *  where composti.id_accordo=5 and composti.id=componenti.id_servizio_composto;

			 */
			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI+".id_soggetto");
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("tipo_soggetto",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome_soggetto",search,true,true),
						sqlQueryObject.getWhereLikeCondition("tipo_servizio",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome_servizio",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
				sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField(CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo = ?");
				sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI+".id_soggetto");
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

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPOSTO);
			sqlQueryObject.addFromTable(CostantiDB.ACCORDI_SERVIZI_COMPONENTI);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI);
			sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);

			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPONENTI,"id_servizio_componente");
			sqlQueryObject.addSelectField(CostantiDB.ACCORDI_SERVIZI_COMPONENTI,"azione");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI,"tipo_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SOGGETTI,"nome_soggetto");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI,"tipo_servizio");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI,"nome_servizio");

			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id_accordo = ?");
			sqlQueryObject.addWhereCondition(CostantiDB.ACCORDI_SERVIZI_COMPOSTO+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_composto");
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI+".id = "+CostantiDB.ACCORDI_SERVIZI_COMPONENTI+".id_servizio_componente");
			sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id = "+CostantiDB.SERVIZI+".id_soggetto");


			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereCondition(false, sqlQueryObject.getWhereLikeCondition("tipo_soggetto",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome_soggetto",search,true,true),
						sqlQueryObject.getWhereLikeCondition("tipo_servizio",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome_servizio",search,true,true));
			} 

			sqlQueryObject.setANDLogicOperator(true);

			sqlQueryObject.addOrderBy("tipo_servizio");
			sqlQueryObject.addOrderBy("nome_servizio");
			sqlQueryObject.addOrderBy("tipo_soggetto");
			sqlQueryObject.addOrderBy("nome_soggetto");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);

			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			risultato = stmt.executeQuery();

			while(risultato.next()){

				long idServizioComponente = risultato.getLong("id_servizio_componente");
				String azione = risultato.getString("azione");
				AccordoServizioParteComuneServizioCompostoServizioComponente asComponente = new AccordoServizioParteComuneServizioCompostoServizioComponente();
				asComponente.setAzione(azione);
				asComponente.setIdServizioComponente(idServizioComponente);
				asComponente.setTipo(risultato.getString("tipo_servizio"));
				asComponente.setNome(risultato.getString("nome_servizio"));
				asComponente.setTipoSoggetto(risultato.getString("tipo_soggetto"));
				asComponente.setNomeSoggetto(risultato.getString("nome_soggetto"));

				lista.add(asComponente);
			}


			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}

	protected List<Documento> accordiCoopAllegatiList(long idAccordo, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "accordiCoopAllegatiList";
		int idLista = Liste.ACCORDI_COOP_ALLEGATI;
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

		ArrayList<Documento> lista = new ArrayList<Documento>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("accordiCoopAllegatiList");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.addWhereCondition(false, 
						//sqlQueryObject.getWhereLikeCondition("ruolo",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_proprietario = ?");
				sqlQueryObject.addWhereCondition("tipo_proprietario = ?");
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1,idAccordo);
			stmt.setString(2, ProprietariDocumento.accordoCooperazione.toString());
			risultato = stmt.executeQuery();
			if (risultato.next())
				ricerca.setNumEntries(idLista,risultato.getInt("cont"));
			risultato.close();
			stmt.close();

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.DOCUMENTI);
			sqlQueryObject.addSelectField("id");
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("ruolo");
			sqlQueryObject.addSelectField("id_proprietario");
			sqlQueryObject.addSelectField("tipo_proprietario");
			//where
			sqlQueryObject.addWhereCondition("id_proprietario = ?");
			sqlQueryObject.addWhereCondition("tipo_proprietario = ?");

			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;

			if (!search.equals("")) { // con search
				sqlQueryObject.addWhereCondition(false, 
						//sqlQueryObject.getWhereLikeCondition("ruolo",search,true,true),
						sqlQueryObject.getWhereLikeCondition("nome",search,true,true));
			} 

			sqlQueryObject.setANDLogicOperator(true);
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setLimit(limit);
			sqlQueryObject.setOffset(offset);
			queryString = sqlQueryObject.createSQLQuery();

			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idAccordo);
			stmt.setString(2, ProprietariDocumento.accordoCooperazione.toString());
			risultato = stmt.executeQuery();

			while(risultato.next()){
				Documento doc = DriverRegistroServiziDB_documentiLIB.getDocumento(risultato.getLong("id"),false, con, this.driver.tipoDB); 
				lista.add(doc);
			}

			return lista;

		} catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception: " + se.getMessage(),se);
		} finally {
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
			this.driver.closeConnection(con);
		}
	}
}
