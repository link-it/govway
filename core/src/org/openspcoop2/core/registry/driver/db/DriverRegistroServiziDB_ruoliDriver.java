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

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.registry.Ruolo;
import org.openspcoop2.core.registry.constants.RuoloContesto;
import org.openspcoop2.core.registry.constants.RuoloTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.driver.FiltroRicercaRuoli;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_ruoliDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_ruoliDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_ruoliDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	protected Ruolo getRuolo(
			IDRuolo idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		this.driver.log.debug("richiesto getRuolo: " + idRuolo);
		// conrollo consistenza
		if (idRuolo == null)
			throw new DriverRegistroServiziException("[getRuolo] Parametro idRuolo is null");
		if (idRuolo.getNome()==null || idRuolo.getNome().trim().equals(""))
			throw new DriverRegistroServiziException("[getRuolo] Parametro idRuolo.nome non e' definito");

		Connection con = null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getRuolo(nome)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getRuolo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {

			return getRuolo(con, idRuolo);

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		}catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getRuolo] Exception: " + se.getMessage(),se);
		} finally {
			this.driver.closeConnection(con);
		}
	}
	protected Ruolo getRuolo(Connection conParam,
			IDRuolo idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		this.driver.log.debug("richiesto getRuolo: " + idRuolo);
		// conrollo consistenza
		if (idRuolo == null)
			throw new DriverRegistroServiziException("[getRuolo] Parametro idRuolo is null");
		if (idRuolo.getNome()==null || idRuolo.getNome().trim().equals(""))
			throw new DriverRegistroServiziException("[getRuolo] Parametro idRuolo.nome non e' definito");

		PreparedStatement stm = null;
		ResultSet rs = null;

		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			String queryString = sqlQueryObject
					.createSQLQuery();
			stm = conParam.prepareStatement(queryString);
			stm.setString(1, idRuolo.getNome());
			rs = stm.executeQuery();
			Ruolo ruolo = null;
			if (rs.next()) {
				ruolo = new Ruolo();
				ruolo.setId(rs.getLong("id"));
				ruolo.setNome(rs.getString("nome"));
				ruolo.setDescrizione(rs.getString("descrizione"));
				String tipologia = rs.getString("tipologia");
				if(tipologia!=null){
					ruolo.setTipologia(RuoloTipologia.toEnumConstant(tipologia));
				}
				ruolo.setNomeEsterno(rs.getString("nome_esterno"));
				String contesto_utilizzo = rs.getString("contesto_utilizzo");
				if(contesto_utilizzo!=null){
					ruolo.setContestoUtilizzo(RuoloContesto.toEnumConstant(contesto_utilizzo));
				}
				ruolo.setSuperUser(rs.getString("superuser"));

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					ruolo.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}

			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getRuolo] rs.next non ha restituito valori con la seguente interrogazione :\n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(queryString, idRuolo.getNome()));
			}

			return ruolo;

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getRuolo] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getRuolo] Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
		}
	}

	protected Ruolo getRuolo(
			long idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		this.driver.log.debug("richiesto getRuolo: " + idRuolo);
		// conrollo consistenza
		if (idRuolo <=0)
			throw new DriverRegistroServiziException("[getRuolo] Parametro idRuolo non valido");

		Connection con = null;
		
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("getRuolo(id)");

			} catch (Exception e) {
				throw new DriverRegistroServiziException("DriverRegistroServiziDB::getRuolo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {

			return getRuolo(con, idRuolo);

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (Exception se) {
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getRuolo] Exception: " + se.getMessage(),se);
		} finally {
			this.driver.closeConnection(con);
		}
	}
	protected Ruolo getRuolo(Connection conParam,
			long idRuolo) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		this.driver.log.debug("richiesto getRuolo: " + idRuolo);
		// conrollo consistenza
		if (idRuolo <=0)
			throw new DriverRegistroServiziException("[getRuolo] Parametro idRuolo non valido");

		PreparedStatement stm = null;
		ResultSet rs = null;

		IDRuolo idRuoloObject = null;
		try {

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addWhereCondition("id = ?");
			String queryString = sqlQueryObject.createSQLQuery();
			stm = conParam.prepareStatement(queryString);
			stm.setLong(1, idRuolo);
			rs = stm.executeQuery();
			if (rs.next()) {
				idRuoloObject = new IDRuolo(rs.getString("nome"));
			} else {
				throw new DriverRegistroServiziNotFound("[DriverRegistroServiziDB::getRuolo] rs.next non ha restituito valori con la seguente interrogazione :\n" + 
						DriverRegistroServiziDB_LIB.formatSQLString(queryString, idRuolo));
			}

		}catch (DriverRegistroServiziNotFound e) {
			throw e;
		} catch (SQLException se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getRuolo] SqlException: " + se.getMessage(),se);
		}catch (Exception se) {

			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::getRuolo] Exception: " + se.getMessage(),se);
		} finally {
			JDBCUtilities.closeResources(rs, stm);
		}
		
		return this.getRuolo(conParam,idRuoloObject);
	}

	protected List<IDRuolo> getAllIdRuoli(
			FiltroRicercaRuoli filtroRicerca) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{

		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		boolean filtroRicercaTipo = false;
		if(filtroRicerca!=null){
			filtroRicercaTipo = filtroRicerca.getTipologia()!=null && !RuoloTipologia.QUALSIASI.equals(filtroRicerca.getTipologia());
		}
		List<String> listTipologia = null;
		if(filtroRicercaTipo){
			listTipologia = new ArrayList<>();
			listTipologia.add(RuoloTipologia.QUALSIASI.getValue());
			listTipologia.add(filtroRicerca.getTipologia().getValue());
		}
		
		boolean filtroRicercaContesto = false;
		if(filtroRicerca!=null){
			filtroRicercaContesto = filtroRicerca.getContesto()!=null && !RuoloContesto.QUALSIASI.equals(filtroRicerca.getContesto());
		}
		List<String> listContesto = null;
		if(filtroRicercaContesto){
			listContesto = new ArrayList<>();
			listContesto.add(RuoloContesto.QUALSIASI.getValue());
			listContesto.add(filtroRicerca.getContesto().getValue());
		}
		
		this.driver.log.debug("getAllIdRuoli...");

		try {
			this.driver.log.debug("operazione atomica = " + this.driver.atomica);
			// prendo la connessione dal pool
			if (this.driver.atomica)
				con = this.driver.getConnectionFromDatasource("getAllIdRuoli");
			else
				con = this.driver.globalConnection;

			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI);
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
			List<IDRuolo> nomiRuoli = new ArrayList<IDRuolo>();
			while (rs.next()) {
				nomiRuoli.add(new IDRuolo(rs.getString("nome")));
			}
			if(nomiRuoli.size()==0){
				if(filtroRicerca!=null)
					throw new DriverRegistroServiziNotFound("Ruoli non trovati che rispettano il filtro di ricerca selezionato: "+filtroRicerca.toString());
				else
					throw new DriverRegistroServiziNotFound("Ruoli non trovati");
			}else{
				return nomiRuoli;
			}
		}catch(DriverRegistroServiziNotFound de){
			throw de;
		}
		catch(Exception e){
			throw new DriverRegistroServiziException("getAllIdRuoli error",e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);

		}
	}

	protected void createRuolo(Ruolo ruolo) throws DriverRegistroServiziException{
		if (ruolo == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createRuolo] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("createRuolo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createRuolo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRuolo type = 1");
			DriverRegistroServiziDB_LIB.CRUDRuolo(CostantiDB.CREATE, ruolo, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::createRuolo] Errore durante la creazione del ruolo : " + qe.getMessage(), qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}

	protected boolean existsRuolo(IDRuolo idRuolo) throws DriverRegistroServiziException{
		boolean exist = false;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;

		if (idRuolo == null)
			throw new DriverRegistroServiziException("Parametro non valido");

		if (idRuolo.getNome()==null || idRuolo.getNome().equals(""))
			throw new DriverRegistroServiziException("Parametro vuoto non valido");

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("existsRuolo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::existsRuolo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.RUOLI);
			sqlQueryObject.addSelectField("*");
			sqlQueryObject.addWhereCondition("nome = ?");
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			stm = con.prepareStatement(sqlQuery);
			stm.setString(1, idRuolo.getNome());
			rs = stm.executeQuery();
			if (rs.next())
				exist = true;
			rs.close();
			stm.close();

		} catch (Exception e) {
			exist = false;
			this.driver.log.error("Errore durante verifica esistenza ruolo: "+e.getMessage(), e);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(rs, stm);

			this.driver.closeConnection(con);
		}

		return exist;
	}

	protected void updateRuolo(Ruolo ruolo) throws DriverRegistroServiziException{
		if (ruolo == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateRuolo] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("updateRuolo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateRuolo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {

			this.driver.log.debug("CRUDRuolo type = 2");
			DriverRegistroServiziDB_LIB.CRUDRuolo(CostantiDB.UPDATE, ruolo, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::updateRuolo] Errore durante l'aggiornamento del ruolo : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}	

	protected void deleteRuolo(Ruolo ruolo) throws DriverRegistroServiziException{
		if (ruolo == null)
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteRuolo] Parametro non valido.");

		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("deleteRuolo");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteRuolo] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.log.debug("CRUDRuolo type = 3");
			DriverRegistroServiziDB_LIB.CRUDRuolo(CostantiDB.DELETE, ruolo, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteRuolo] Errore durante l'eliminazione del ruolo : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	private ISQLQueryObject buildSqlQueryObjectAutorizzazioniPorteDelegate(String nomeTabella,
			String alias_PD_RUOLI, String alias_PD, String alias_SERVIZI, String alias_ACCORDI, String alias_ACCORDI_GRUPPI, String alias_GRUPPI, String alias_SOGGETTI,
			boolean isFilterGruppoFruizione, IDFruizione apiImplementazioneFruizione, boolean filterSoggettoProprietario, boolean filterTipoSoggettoProtocollo, String filterGruppo,
			String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoSoggettiProtocollo,
			List<Object> existsParameters) throws Exception {
		ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
		sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(nomeTabella,alias_PD_RUOLI);
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
		sqlQueryObjectAutorizzazioniPorteDelegate.addSelectAliasField(alias_PD_RUOLI, "id", alias_PD_RUOLI+"id");
		sqlQueryObjectAutorizzazioniPorteDelegate.setANDLogicOperator(true);
		sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD_RUOLI+".ruolo = "+CostantiDB.RUOLI+".nome");
		sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD_RUOLI+".id_porta = "+alias_PD+".id");
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
		return sqlQueryObjectAutorizzazioniPorteDelegate;
	}
	
	private ISQLQueryObject buildSqlQueryObjectAutorizzazioniPorteApplicative(String nomeTabella,
			String alias_PA_RUOLI, String alias_PA, String alias_SERVIZI, String alias_ACCORDI, String alias_ACCORDI_GRUPPI, String alias_GRUPPI, String alias_SOGGETTI,
			boolean isFilterGruppoErogazione, IDServizio apiImplementazioneErogazione, boolean filterSoggettoProprietario, boolean filterTipoSoggettoProtocollo, String filterGruppo,
			String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoSoggettiProtocollo,
			List<Object> existsParameters) throws Exception {

		ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
		sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(nomeTabella,alias_PA_RUOLI);
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
		sqlQueryObjectAutorizzazioniPorteApplicative.addSelectAliasField(alias_PA_RUOLI, "id", alias_PA_RUOLI+"id");
		sqlQueryObjectAutorizzazioniPorteApplicative.setANDLogicOperator(true);
		sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA_RUOLI+".ruolo = "+CostantiDB.RUOLI+".nome");
		sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA_RUOLI+".id_porta = "+alias_PA+".id");
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
		return sqlQueryObjectAutorizzazioniPorteApplicative;
	}
	
	protected List<Ruolo> ruoliList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "ruoliList";
		int idLista = Liste.RUOLI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterRuoloTipologia = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO_TIPOLOGIA);
		org.openspcoop2.core.registry.constants.RuoloTipologia ruoloTipologia = null;
		if(filterRuoloTipologia!=null) {
			ruoloTipologia = org.openspcoop2.core.registry.constants.RuoloTipologia.toEnumConstant(filterRuoloTipologia);
		}
		
		String filterRuoloContesto = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO_CONTESTO);
		org.openspcoop2.core.registry.constants.RuoloContesto ruoloContesto = null;
		if(filterRuoloContesto!=null) {
			ruoloContesto = org.openspcoop2.core.registry.constants.RuoloContesto.toEnumConstant(filterRuoloContesto);
		}
		
		boolean isFilterGruppoErogazione = false;
		boolean isFilterGruppoFruizione = false;
		String filterGruppo = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_GRUPPO);
		if(filterGruppo!=null && !"".equals(filterGruppo)) {
			isFilterGruppoErogazione = true;
			isFilterGruppoFruizione = true;
		}

		TipoPdD apiContesto = null;
		boolean filterServiziApplicativi = false;
		boolean filterSoggetti = false;
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
				if(Filtri.FILTRO_API_CONTESTO_VALUE_APPLICATIVI.equals(filterApiContesto)) {
					filterServiziApplicativi = true;
				}
				else if(Filtri.FILTRO_API_CONTESTO_VALUE_SOGGETTI.equals(filterApiContesto)) {
					filterSoggetti = true;
				}
			}
		}
		
		String filterProtocollo = null;
		String filterProtocolli = null;
		List<String> tipoSoggettiProtocollo = null;
		String filterSoggettoTipo = null;
		String filterSoggettoNome = null;
		boolean filterSoggettoProprietario = false;
		boolean filterTipoSoggettoProtocollo = false;
		if(apiContesto!=null || filterServiziApplicativi || filterSoggetti) {
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
		
		String filterServizioApplicativo = null;
		IDServizioApplicativo idServizioApplicativo = null;
		if(filterServiziApplicativi) {
			filterServizioApplicativo = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_SERVIZIO_APPLICATIVO);
			if(filterServizioApplicativo!=null && !"".equals(filterServizioApplicativo)) {
				try {
					idServizioApplicativo = IDServizioApplicativo.toIDServizioApplicativo(filterServizioApplicativo);
					isFilterGruppoErogazione=false; // non ha piu' senso ho selezionato un applicativo puntuale
					isFilterGruppoFruizione=false; // non ha piu' senso ho selezionato un applicativo puntuale
				}catch(Exception e) {
					throw new DriverRegistroServiziException("Filtro Applicativo '"+filterServizioApplicativo+"' non valido: "+e.getMessage(),e);
				}
			}
		}
		
		
		this.driver.log.debug("search : " + search);
		this.driver.log.debug("filterRuoloTipologia : " + filterRuoloTipologia);
		this.driver.log.debug("filterRuoloContesto : " + filterRuoloContesto);
		this.driver.log.debug("filterGruppo : " + filterGruppo);
		this.driver.log.debug("filterApiContesto : " + filterApiContesto);
		this.driver.log.debug("filterProtocollo : " + filterProtocollo);
		this.driver.log.debug("filterProtocolli : " + filterProtocolli);
		this.driver.log.debug("filterSoggettoNome : " + filterSoggettoNome);
		this.driver.log.debug("filterSoggettoTipo : " + filterSoggettoTipo);
		this.driver.log.debug("filterApiImplementazione : " + filterApiImplementazione);
		this.driver.log.debug("filterServizioApplicativo : " + filterServizioApplicativo);
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<Ruolo> lista = new ArrayList<Ruolo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("ruoliList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.log.debug("operazione this.driver.atomica = " + this.driver.atomica);

		List<IDRuolo> listIdRuoli = null;
		try {

			
			/* === Filtro Gruppo, Tipo Erogazione/Fruizione, Implementazione API === */
			
			List<String> existsConditions = new ArrayList<>();
			List<Object> existsParameters = new ArrayList<>();
			String alias_CT = "ct";
			String alias_ALLARMI = "alarm";
			String alias_SOGGETTI_RUOLI = "sogruoli";
			String alias_SA_RUOLI = "saruoli";
			String alias_SA = "sa";
			String alias_PD_RUOLI = "pdruoli";
			String alias_PD_TOKEN_RUOLI = "pdtokenruoli";
			String alias_PD = "pd";
			String alias_PA_RUOLI = "paruoli";
			String alias_PA_TOKEN_RUOLI = "patokenruoli";
			String alias_PA = "pa";
			String alias_SERVIZI = "s";
			String alias_ACCORDI = "a";
			String alias_ACCORDI_GRUPPI = "ag";
			String alias_GRUPPI = "g";
			String alias_SOGGETTI = "sProprietario";
	
			
			
			
			// soggetti con ruolo
			if(filterSoggetti) {
				ISQLQueryObject sqlQueryObjectServiziApplicativi = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectServiziApplicativi.addFromTable(CostantiDB.SOGGETTI_RUOLI,alias_SOGGETTI_RUOLI);
				sqlQueryObjectServiziApplicativi.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
				sqlQueryObjectServiziApplicativi.addSelectAliasField(alias_SOGGETTI_RUOLI, "id", alias_SOGGETTI_RUOLI+"id");
				sqlQueryObjectServiziApplicativi.setANDLogicOperator(true);
				sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SOGGETTI_RUOLI+".id_ruolo = "+CostantiDB.RUOLI+".id");
				sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SOGGETTI_RUOLI+".id_soggetto = "+alias_SOGGETTI+".id");
				if(filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					if(filterSoggettoProprietario) {
						sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
						existsParameters.add(filterSoggettoTipo);
						existsParameters.add(filterSoggettoNome);
					}
					else if(filterTipoSoggettoProtocollo) {
						sqlQueryObjectServiziApplicativi.addWhereINCondition(alias_SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
					}
				}
				
				existsConditions.add(sqlQueryObjectServiziApplicativi.getWhereExistsCondition(false, sqlQueryObjectServiziApplicativi));
			} 
			
			
			// servizi applicativi con ruolo
			if(filterServiziApplicativi) {
				ISQLQueryObject sqlQueryObjectServiziApplicativi = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectServiziApplicativi.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI,alias_SA_RUOLI);
				sqlQueryObjectServiziApplicativi.addFromTable(CostantiDB.SERVIZI_APPLICATIVI,alias_SA);
				if(idServizioApplicativo!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectServiziApplicativi.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
				}
				sqlQueryObjectServiziApplicativi.addSelectAliasField(alias_SA_RUOLI, "id", alias_SA_RUOLI+"id");
				sqlQueryObjectServiziApplicativi.setANDLogicOperator(true);
				sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SA_RUOLI+".ruolo = "+CostantiDB.RUOLI+".nome");
				sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SA_RUOLI+".id_servizio_applicativo = "+alias_SA+".id");
				if(idServizioApplicativo!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SA+".id_soggetto = "+alias_SOGGETTI+".id");
					if(idServizioApplicativo!=null) {
						sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
						sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SA+".nome = ?");
						existsParameters.add(idServizioApplicativo.getIdSoggettoProprietario().getTipo());
						existsParameters.add(idServizioApplicativo.getIdSoggettoProprietario().getNome());
						existsParameters.add(idServizioApplicativo.getNome());
					}
					else if(filterSoggettoProprietario) {
						sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectServiziApplicativi.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
						existsParameters.add(filterSoggettoTipo);
						existsParameters.add(filterSoggettoNome);
					}
					else if(filterTipoSoggettoProtocollo) {
						sqlQueryObjectServiziApplicativi.addWhereINCondition(alias_SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
					}
				}
				
				existsConditions.add(sqlQueryObjectServiziApplicativi.getWhereExistsCondition(false, sqlQueryObjectServiziApplicativi));
			} 
			
			
			// controllo accesso porte delegate
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = buildSqlQueryObjectAutorizzazioniPorteDelegate(CostantiDB.PORTE_DELEGATE_RUOLI,
						alias_PD_RUOLI, alias_PD, alias_SERVIZI, alias_ACCORDI, alias_ACCORDI_GRUPPI, alias_GRUPPI, alias_SOGGETTI,
						isFilterGruppoFruizione, apiImplementazioneFruizione, filterSoggettoProprietario, filterTipoSoggettoProtocollo, filterGruppo,
						filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteDelegate));
			} 
			
			// controllo accesso porte delegate (token)
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = buildSqlQueryObjectAutorizzazioniPorteDelegate(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI,
						alias_PD_TOKEN_RUOLI, alias_PD, alias_SERVIZI, alias_ACCORDI, alias_ACCORDI_GRUPPI, alias_GRUPPI, alias_SOGGETTI,
						isFilterGruppoFruizione, apiImplementazioneFruizione, filterSoggettoProprietario, filterTipoSoggettoProtocollo, filterGruppo,
						filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteDelegate));
			} 
			
			
			// controllo accesso porte applicative
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = buildSqlQueryObjectAutorizzazioniPorteApplicative(CostantiDB.PORTE_APPLICATIVE_RUOLI,
						alias_PA_RUOLI, alias_PA, alias_SERVIZI, alias_ACCORDI, alias_ACCORDI_GRUPPI, alias_GRUPPI, alias_SOGGETTI,
						isFilterGruppoErogazione, apiImplementazioneErogazione, filterSoggettoProprietario, filterTipoSoggettoProtocollo, filterGruppo,
						filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteApplicative));
			} 
			
			// controllo accesso porte applicative (token)
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = buildSqlQueryObjectAutorizzazioniPorteApplicative(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI,
						alias_PA_TOKEN_RUOLI, alias_PA, alias_SERVIZI, alias_ACCORDI, alias_ACCORDI_GRUPPI, alias_GRUPPI, alias_SOGGETTI,
						isFilterGruppoErogazione, apiImplementazioneErogazione, filterSoggettoProprietario, filterTipoSoggettoProtocollo, filterGruppo,
						filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteApplicative));
			} 
			

			// Controllo del traffico sulle porte delegate
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectControlloTrafficoPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY,alias_CT);
				if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
					if(isFilterGruppoFruizione) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
					}
					if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
					}
				}
				sqlQueryObjectControlloTrafficoPorteDelegate.addSelectAliasField(alias_CT, "id", alias_CT+"id");
				sqlQueryObjectControlloTrafficoPorteDelegate.setANDLogicOperator(true);
				sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_CT+".filtro_ruolo_fruitore = "+CostantiDB.RUOLI+".nome");
				sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_CT+".filtro_ruolo = 'delegata'");
				if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_CT+".filtro_porta = "+alias_PD+".nome_porta");
					if(isFilterGruppoFruizione) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_PD+".id_servizio = "+alias_SERVIZI+".id");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_GRUPPI+".nome = ?");
						existsParameters.add(filterGruppo);
					}
					if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+alias_SOGGETTI+".id");
						if(apiImplementazioneFruizione!=null) {
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_PD+".tipo_soggetto_erogatore = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_PD+".nome_soggetto_erogatore = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_PD+".tipo_servizio = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_PD+".nome_servizio = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_PD+".versione_servizio = ?");
							existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
							existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
							existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
							existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
							existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
							existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
							existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
						}
						else if(filterSoggettoProprietario) {
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
							existsParameters.add(filterSoggettoTipo);
							existsParameters.add(filterSoggettoNome);
						}
						else if(filterTipoSoggettoProtocollo) {
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereINCondition(alias_SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
						}
					}
				}
				
				existsConditions.add(sqlQueryObjectControlloTrafficoPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectControlloTrafficoPorteDelegate));
			} 
			
			// Controllo del traffico sulle porte applicative
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectControlloTrafficoPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY,alias_CT);
				if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
					if(isFilterGruppoErogazione) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
					}
					if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
					}
				}
				sqlQueryObjectControlloTrafficoPorteApplicative.addSelectAliasField(alias_CT, "id", alias_CT+"id");
				sqlQueryObjectControlloTrafficoPorteApplicative.setANDLogicOperator(true);
				sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_CT+".filtro_ruolo_fruitore = "+CostantiDB.RUOLI+".nome");
				sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_CT+".filtro_ruolo = 'applicativa'");
				if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_CT+".filtro_porta = "+alias_PA+".nome_porta");
					if(isFilterGruppoErogazione) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_PA+".id_servizio = "+alias_SERVIZI+".id");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_GRUPPI+".nome = ?");
						existsParameters.add(filterGruppo);
					}
					if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+alias_SOGGETTI+".id");
						if(apiImplementazioneErogazione!=null) {
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_PA+".tipo_servizio = ?");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_PA+".servizio = ?");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_PA+".versione_servizio = ?");
							existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
							existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
							existsParameters.add(apiImplementazioneErogazione.getTipo());
							existsParameters.add(apiImplementazioneErogazione.getNome());
							existsParameters.add(apiImplementazioneErogazione.getVersione());
						}
						else if(filterSoggettoProprietario) {
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
							existsParameters.add(filterSoggettoTipo);
							existsParameters.add(filterSoggettoNome);
						}
						else if(filterTipoSoggettoProtocollo) {
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereINCondition(alias_SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
						}
					}
				}
				
				existsConditions.add(sqlQueryObjectControlloTrafficoPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectControlloTrafficoPorteApplicative));
			} 
			
			if(CostantiDB.isAllarmiEnabled()) {
				
				// Allarmi sulle porte delegate
				if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
					ISQLQueryObject sqlQueryObjectAllarmiPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ALLARMI,alias_ALLARMI);
					if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
						if(isFilterGruppoFruizione) {
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
						}
						if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
						}
					}
					sqlQueryObjectAllarmiPorteDelegate.addSelectAliasField(alias_ALLARMI, "id", alias_ALLARMI+"id");
					sqlQueryObjectAllarmiPorteDelegate.setANDLogicOperator(true);
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_ruolo_fruitore = "+CostantiDB.RUOLI+".nome");
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_ruolo = 'delegata'");
					if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_porta = "+alias_PD+".nome_porta");
						if(isFilterGruppoFruizione) {
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_PD+".id_servizio = "+alias_SERVIZI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_GRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+alias_SOGGETTI+".id");
							if(apiImplementazioneFruizione!=null) {
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_PD+".tipo_soggetto_erogatore = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_PD+".nome_soggetto_erogatore = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_PD+".tipo_servizio = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_PD+".nome_servizio = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_PD+".versione_servizio = ?");
								existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
							}
							else if(filterSoggettoProprietario) {
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
								existsParameters.add(filterSoggettoTipo);
								existsParameters.add(filterSoggettoNome);
							}
							else if(filterTipoSoggettoProtocollo) {
								sqlQueryObjectAllarmiPorteDelegate.addWhereINCondition(alias_SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
							}
						}
					}
					
					existsConditions.add(sqlQueryObjectAllarmiPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAllarmiPorteDelegate));
				} 
				
				// Allarmi sulle porte applicative
				if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
					ISQLQueryObject sqlQueryObjectAllarmiPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ALLARMI,alias_ALLARMI);
					if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
						if(isFilterGruppoErogazione) {
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
						}
						if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
						}
					}
					sqlQueryObjectAllarmiPorteApplicative.addSelectAliasField(alias_ALLARMI, "id", alias_ALLARMI+"id");
					sqlQueryObjectAllarmiPorteApplicative.setANDLogicOperator(true);
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_ruolo_fruitore = "+CostantiDB.RUOLI+".nome");
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_ruolo = 'applicativa'");
					if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_porta = "+alias_PA+".nome_porta");
						if(isFilterGruppoErogazione) {
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_PA+".id_servizio = "+alias_SERVIZI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_GRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+alias_SOGGETTI+".id");
							if(apiImplementazioneErogazione!=null) {
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_PA+".tipo_servizio = ?");
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_PA+".servizio = ?");
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_PA+".versione_servizio = ?");
								existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
								existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
								existsParameters.add(apiImplementazioneErogazione.getTipo());
								existsParameters.add(apiImplementazioneErogazione.getNome());
								existsParameters.add(apiImplementazioneErogazione.getVersione());
							}
							else if(filterSoggettoProprietario) {
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
								existsParameters.add(filterSoggettoTipo);
								existsParameters.add(filterSoggettoNome);
							}
							else if(filterTipoSoggettoProtocollo) {
								sqlQueryObjectAllarmiPorteApplicative.addWhereINCondition(alias_SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
							}
						}
					}
					
					existsConditions.add(sqlQueryObjectAllarmiPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAllarmiPorteApplicative));
				} 
			}
	
			
			
			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);	
				if(ruoloContesto!=null) {
					sqlQueryObject.addWhereCondition("contesto_utilizzo = ?");
				}
				if(ruoloTipologia!=null) {
					sqlQueryObject.addWhereCondition("tipologia = ?");
				}
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(ruoloContesto!=null) {
					sqlQueryObject.addWhereCondition("contesto_utilizzo = ?");
				}
				if(ruoloTipologia!=null) {
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
			if(ruoloContesto!=null) {
				stmt.setString(index++, ruoloContesto.getValue());
			}
			if(ruoloTipologia!=null) {
				stmt.setString(index++, ruoloTipologia.getValue());
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
				sqlQueryObject.addFromTable(CostantiDB.RUOLI);
				sqlQueryObject.addSelectField("nome");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				if(ruoloContesto!=null) {
					sqlQueryObject.addWhereCondition("contesto_utilizzo = ?");
				}
				if(ruoloTipologia!=null) {
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
				sqlQueryObject.addFromTable(CostantiDB.RUOLI);
				sqlQueryObject.addSelectField("nome");
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(ruoloContesto!=null) {
					sqlQueryObject.addWhereCondition("contesto_utilizzo = ?");
				}
				if(ruoloTipologia!=null) {
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
			if(ruoloContesto!=null) {
				stmt.setString(index++, ruoloContesto.getValue());
			}
			if(ruoloTipologia!=null) {
				stmt.setString(index++, ruoloTipologia.getValue());
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
			listIdRuoli = new ArrayList<>();
			while (risultato.next()) {

				listIdRuoli.add(new IDRuolo(risultato.getString("nome")));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		
		if(listIdRuoli!=null){
			for (IDRuolo idRuolo : listIdRuoli) {
				try{
					lista.add(this.getRuolo(idRuolo));
				}catch(DriverRegistroServiziNotFound notFound){
					// non può capitare
					throw new DriverRegistroServiziException(notFound.getMessage(),notFound);
				}
			}
		}
		
		return lista;
	}
}
