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
import org.openspcoop2.utils.sql.SQLQueryObjectException;

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

		this.driver.logDebug("richiesto getRuolo: " + idRuolo);
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

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

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

		this.driver.logDebug("richiesto getRuolo: " + idRuolo);
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
				String contestoUtilizzo = rs.getString("contesto_utilizzo");
				if(contestoUtilizzo!=null){
					ruolo.setContestoUtilizzo(RuoloContesto.toEnumConstant(contestoUtilizzo));
				}
				ruolo.setSuperUser(rs.getString("superuser"));

				// Ora Registrazione
				if(rs.getTimestamp("ora_registrazione")!=null){
					ruolo.setOraRegistrazione(new Date(rs.getTimestamp("ora_registrazione").getTime()));
				}
				
				// Proprieta Oggetto
				ruolo.setProprietaOggetto(DriverRegistroServiziDB_utilsDriver.readProprietaOggetto(rs,false));

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

		this.driver.logDebug("richiesto getRuolo: " + idRuolo);
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

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

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

		this.driver.logDebug("richiesto getRuolo: " + idRuolo);
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
		
		this.driver.logDebug("getAllIdRuoli...");

		try {
			this.driver.logDebug("operazione atomica = " + this.driver.atomica);
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
					for (int i = 0; i < listTipologia.size(); i++) {
						this.driver.logDebug("tipo stmt.setString("+listTipologia.get(i)+")");
						stm.setString(indexStmt, listTipologia.get(i));
						indexStmt++;
					}
				}
				if(filtroRicercaContesto){
					for (int i = 0; i < listContesto.size(); i++) {
						this.driver.logDebug("contesto stmt.setString("+listContesto.get(i)+")");
						stm.setString(indexStmt, listContesto.get(i));
						indexStmt++;
					}
				}
			}
			rs = stm.executeQuery();
			List<IDRuolo> nomiRuoli = new ArrayList<>();
			while (rs.next()) {
				nomiRuoli.add(new IDRuolo(rs.getString("nome")));
			}
			if(nomiRuoli.isEmpty()){
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

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDRuolo type = 1");
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

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {

			this.driver.logDebug("CRUDRuolo type = 2");
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

		this.driver.logDebug("operazione atomica = " + this.driver.atomica);

		try {
			this.driver.logDebug("CRUDRuolo type = 3");
			DriverRegistroServiziDB_LIB.CRUDRuolo(CostantiDB.DELETE, ruolo, con);

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::deleteRuolo] Errore durante l'eliminazione del ruolo : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	private ISQLQueryObject buildSqlQueryObjectAutorizzazioniPorteDelegate(String nomeTabella,
			String aliasPDRUOLI, String aliasPD, String aliasSERVIZI, String aliasACCORDI, String aliasACCORDIGRUPPI, String aliasGRUPPI, String aliasSOGGETTI,
			boolean isFilterGruppoFruizione, IDFruizione apiImplementazioneFruizione, boolean filterSoggettoProprietario, boolean filterTipoSoggettoProtocollo, String filterGruppo,
			String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoSoggettiProtocollo,
			List<Object> existsParameters) throws SQLQueryObjectException {
		ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
		sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(nomeTabella,aliasPDRUOLI);
		sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,aliasPD);
		if(isFilterGruppoFruizione) {
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
		}
		if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
		}
		sqlQueryObjectAutorizzazioniPorteDelegate.addSelectAliasField(aliasPDRUOLI, "id", aliasPDRUOLI+"id");
		sqlQueryObjectAutorizzazioniPorteDelegate.setANDLogicOperator(true);
		sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPDRUOLI+".ruolo = "+CostantiDB.RUOLI+".nome");
		sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPDRUOLI+".id_porta = "+aliasPD+".id");
		if(isFilterGruppoFruizione) {
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+".id_servizio = "+aliasSERVIZI+".id");
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasSERVIZI+".id_accordo = "+aliasACCORDI+".id");
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+".id_accordo = "+aliasACCORDI+".id");
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+".id_gruppo = "+aliasGRUPPI+".id");
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasGRUPPI+".nome = ?");
			existsParameters.add(filterGruppo);
		}
		if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+".id_soggetto = "+aliasSOGGETTI+".id");
			if(apiImplementazioneFruizione!=null) {
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+".tipo_soggetto_erogatore = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+".nome_soggetto_erogatore = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+".tipo_servizio = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+".nome_servizio = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+".versione_servizio = ?");
				existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
				existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
				existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
				existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
				existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
				existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
				existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
			}
			else if(filterSoggettoProprietario) {
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
				existsParameters.add(filterSoggettoTipo);
				existsParameters.add(filterSoggettoNome);
			}
			else if(filterTipoSoggettoProtocollo) {
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereINCondition(aliasSOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
			}
		}
		return sqlQueryObjectAutorizzazioniPorteDelegate;
	}
	
	private ISQLQueryObject buildSqlQueryObjectAutorizzazioniPorteApplicative(String nomeTabella,
			String aliasPARUOLI, String aliasPA, String aliasSERVIZI, String aliasACCORDI, String aliasACCORDIGRUPPI, String aliasGRUPPI, String aliasSOGGETTI,
			boolean isFilterGruppoErogazione, IDServizio apiImplementazioneErogazione, boolean filterSoggettoProprietario, boolean filterTipoSoggettoProtocollo, String filterGruppo,
			String filterSoggettoTipo, String filterSoggettoNome, List<String> tipoSoggettiProtocollo,
			List<Object> existsParameters) throws SQLQueryObjectException {

		ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
		sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(nomeTabella,aliasPARUOLI);
		sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,aliasPA);
		if(isFilterGruppoErogazione) {
			sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
			sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
			sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
			sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
		}
		if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
			sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
		}
		sqlQueryObjectAutorizzazioniPorteApplicative.addSelectAliasField(aliasPARUOLI, "id", aliasPARUOLI+"id");
		sqlQueryObjectAutorizzazioniPorteApplicative.setANDLogicOperator(true);
		sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPARUOLI+".ruolo = "+CostantiDB.RUOLI+".nome");
		sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPARUOLI+".id_porta = "+aliasPA+".id");
		if(isFilterGruppoErogazione) {
			sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPA+".id_servizio = "+aliasSERVIZI+".id");
			sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasSERVIZI+".id_accordo = "+aliasACCORDI+".id");
			sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+".id_accordo = "+aliasACCORDI+".id");
			sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+".id_gruppo = "+aliasGRUPPI+".id");
			sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasGRUPPI+".nome = ?");
			existsParameters.add(filterGruppo);
		}
		if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
			sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPA+".id_soggetto = "+aliasSOGGETTI+".id");
			if(apiImplementazioneErogazione!=null) {
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPA+".tipo_servizio = ?");
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPA+".servizio = ?");
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPA+".versione_servizio = ?");
				existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
				existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
				existsParameters.add(apiImplementazioneErogazione.getTipo());
				existsParameters.add(apiImplementazioneErogazione.getNome());
				existsParameters.add(apiImplementazioneErogazione.getVersione());
			}
			else if(filterSoggettoProprietario) {
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
				existsParameters.add(filterSoggettoTipo);
				existsParameters.add(filterSoggettoNome);
			}
			else if(filterTipoSoggettoProtocollo) {
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereINCondition(aliasSOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
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
			if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
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
		
		
		this.driver.logDebug("search : " + search);
		this.driver.logDebug("filterRuoloTipologia : " + filterRuoloTipologia);
		this.driver.logDebug("filterRuoloContesto : " + filterRuoloContesto);
		this.driver.logDebug("filterGruppo : " + filterGruppo);
		this.driver.logDebug("filterApiContesto : " + filterApiContesto);
		this.driver.logDebug("filterProtocollo : " + filterProtocollo);
		this.driver.logDebug("filterProtocolli : " + filterProtocolli);
		this.driver.logDebug("filterSoggettoNome : " + filterSoggettoNome);
		this.driver.logDebug("filterSoggettoTipo : " + filterSoggettoTipo);
		this.driver.logDebug("filterApiImplementazione : " + filterApiImplementazione);
		this.driver.logDebug("filterServizioApplicativo : " + filterServizioApplicativo);
		
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

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<IDRuolo> listIdRuoli = null;
		try {

			
			/* === Filtro Gruppo, Tipo Erogazione/Fruizione, Implementazione API === */
			
			List<String> existsConditions = new ArrayList<>();
			List<Object> existsParameters = new ArrayList<>();
			String aliasCT = "ct";
			String aliasALLARMI = "alarm";
			String aliasSOGGETTIRUOLI = "sogruoli";
			String aliasSARUOLI = "saruoli";
			String aliasSA = "sa";
			String aliasPDRUOLI = "pdruoli";
			String aliasPDTOKENRUOLI = "pdtokenruoli";
			String aliasPD = "pd";
			String aliasPARUOLI = "paruoli";
			String aliasPATOKENRUOLI = "patokenruoli";
			String aliasPA = "pa";
			String aliasSERVIZI = "s";
			String aliasACCORDI = "a";
			String aliasACCORDIGRUPPI = "ag";
			String aliasGRUPPI = "g";
			String aliasSOGGETTI = "sProprietario";
	
			
			
			
			// soggetti con ruolo
			if(filterSoggetti) {
				ISQLQueryObject sqlQueryObjectServiziApplicativi = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectServiziApplicativi.addFromTable(CostantiDB.SOGGETTI_RUOLI,aliasSOGGETTIRUOLI);
				sqlQueryObjectServiziApplicativi.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
				sqlQueryObjectServiziApplicativi.addSelectAliasField(aliasSOGGETTIRUOLI, "id", aliasSOGGETTIRUOLI+"id");
				sqlQueryObjectServiziApplicativi.setANDLogicOperator(true);
				sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSOGGETTIRUOLI+".id_ruolo = "+CostantiDB.RUOLI+".id");
				sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSOGGETTIRUOLI+".id_soggetto = "+aliasSOGGETTI+".id");
				if(filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					if(filterSoggettoProprietario) {
						sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
						existsParameters.add(filterSoggettoTipo);
						existsParameters.add(filterSoggettoNome);
					}
					else if(filterTipoSoggettoProtocollo) {
						sqlQueryObjectServiziApplicativi.addWhereINCondition(aliasSOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
					}
				}
				
				existsConditions.add(sqlQueryObjectServiziApplicativi.getWhereExistsCondition(false, sqlQueryObjectServiziApplicativi));
			} 
			
			
			// servizi applicativi con ruolo
			if(filterServiziApplicativi) {
				ISQLQueryObject sqlQueryObjectServiziApplicativi = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectServiziApplicativi.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI,aliasSARUOLI);
				sqlQueryObjectServiziApplicativi.addFromTable(CostantiDB.SERVIZI_APPLICATIVI,aliasSA);
				if(idServizioApplicativo!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectServiziApplicativi.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
				}
				sqlQueryObjectServiziApplicativi.addSelectAliasField(aliasSARUOLI, "id", aliasSARUOLI+"id");
				sqlQueryObjectServiziApplicativi.setANDLogicOperator(true);
				sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSARUOLI+".ruolo = "+CostantiDB.RUOLI+".nome");
				sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSARUOLI+".id_servizio_applicativo = "+aliasSA+".id");
				if(idServizioApplicativo!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSA+".id_soggetto = "+aliasSOGGETTI+".id");
					if(idServizioApplicativo!=null) {
						sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
						sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSA+".nome = ?");
						existsParameters.add(idServizioApplicativo.getIdSoggettoProprietario().getTipo());
						existsParameters.add(idServizioApplicativo.getIdSoggettoProprietario().getNome());
						existsParameters.add(idServizioApplicativo.getNome());
					}
					else if(filterSoggettoProprietario) {
						sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectServiziApplicativi.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
						existsParameters.add(filterSoggettoTipo);
						existsParameters.add(filterSoggettoNome);
					}
					else if(filterTipoSoggettoProtocollo) {
						sqlQueryObjectServiziApplicativi.addWhereINCondition(aliasSOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
					}
				}
				
				existsConditions.add(sqlQueryObjectServiziApplicativi.getWhereExistsCondition(false, sqlQueryObjectServiziApplicativi));
			} 
			
			
			// controllo accesso porte delegate
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = buildSqlQueryObjectAutorizzazioniPorteDelegate(CostantiDB.PORTE_DELEGATE_RUOLI,
						aliasPDRUOLI, aliasPD, aliasSERVIZI, aliasACCORDI, aliasACCORDIGRUPPI, aliasGRUPPI, aliasSOGGETTI,
						isFilterGruppoFruizione, apiImplementazioneFruizione, filterSoggettoProprietario, filterTipoSoggettoProtocollo, filterGruppo,
						filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteDelegate));
			} 
			
			// controllo accesso porte delegate (token)
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = buildSqlQueryObjectAutorizzazioniPorteDelegate(CostantiDB.PORTE_DELEGATE_TOKEN_RUOLI,
						aliasPDTOKENRUOLI, aliasPD, aliasSERVIZI, aliasACCORDI, aliasACCORDIGRUPPI, aliasGRUPPI, aliasSOGGETTI,
						isFilterGruppoFruizione, apiImplementazioneFruizione, filterSoggettoProprietario, filterTipoSoggettoProtocollo, filterGruppo,
						filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteDelegate));
			} 
			
			
			// controllo accesso porte applicative
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = buildSqlQueryObjectAutorizzazioniPorteApplicative(CostantiDB.PORTE_APPLICATIVE_RUOLI,
						aliasPARUOLI, aliasPA, aliasSERVIZI, aliasACCORDI, aliasACCORDIGRUPPI, aliasGRUPPI, aliasSOGGETTI,
						isFilterGruppoErogazione, apiImplementazioneErogazione, filterSoggettoProprietario, filterTipoSoggettoProtocollo, filterGruppo,
						filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteApplicative));
			} 
			
			// controllo accesso porte applicative (token)
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = buildSqlQueryObjectAutorizzazioniPorteApplicative(CostantiDB.PORTE_APPLICATIVE_TOKEN_RUOLI,
						aliasPATOKENRUOLI, aliasPA, aliasSERVIZI, aliasACCORDI, aliasACCORDIGRUPPI, aliasGRUPPI, aliasSOGGETTI,
						isFilterGruppoErogazione, apiImplementazioneErogazione, filterSoggettoProprietario, filterTipoSoggettoProtocollo, filterGruppo,
						filterSoggettoTipo, filterSoggettoNome, tipoSoggettiProtocollo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteApplicative));
			} 
			

			// Controllo del traffico sulle porte delegate
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectControlloTrafficoPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY,aliasCT);
				if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,aliasPD);
					if(isFilterGruppoFruizione) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
					}
					if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
					}
				}
				sqlQueryObjectControlloTrafficoPorteDelegate.addSelectAliasField(aliasCT, "id", aliasCT+"id");
				sqlQueryObjectControlloTrafficoPorteDelegate.setANDLogicOperator(true);
				sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasCT+".filtro_ruolo_fruitore = "+CostantiDB.RUOLI+".nome");
				sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasCT+".filtro_ruolo = 'delegata'");
				if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasCT+".filtro_porta = "+aliasPD+".nome_porta");
					if(isFilterGruppoFruizione) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+".id_servizio = "+aliasSERVIZI+".id");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasSERVIZI+".id_accordo = "+aliasACCORDI+".id");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+".id_accordo = "+aliasACCORDI+".id");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+".id_gruppo = "+aliasGRUPPI+".id");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasGRUPPI+".nome = ?");
						existsParameters.add(filterGruppo);
					}
					if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+".id_soggetto = "+aliasSOGGETTI+".id");
						if(apiImplementazioneFruizione!=null) {
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+".tipo_soggetto_erogatore = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+".nome_soggetto_erogatore = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+".tipo_servizio = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+".nome_servizio = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+".versione_servizio = ?");
							existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
							existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
							existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
							existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
							existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
							existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
							existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
						}
						else if(filterSoggettoProprietario) {
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
							existsParameters.add(filterSoggettoTipo);
							existsParameters.add(filterSoggettoNome);
						}
						else if(filterTipoSoggettoProtocollo) {
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereINCondition(aliasSOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
						}
					}
				}
				
				existsConditions.add(sqlQueryObjectControlloTrafficoPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectControlloTrafficoPorteDelegate));
			} 
			
			// Controllo del traffico sulle porte applicative
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectControlloTrafficoPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY,aliasCT);
				if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,aliasPA);
					if(isFilterGruppoErogazione) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
					}
					if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
					}
				}
				sqlQueryObjectControlloTrafficoPorteApplicative.addSelectAliasField(aliasCT, "id", aliasCT+"id");
				sqlQueryObjectControlloTrafficoPorteApplicative.setANDLogicOperator(true);
				sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasCT+".filtro_ruolo_fruitore = "+CostantiDB.RUOLI+".nome");
				sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasCT+".filtro_ruolo = 'applicativa'");
				if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
					sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasCT+".filtro_porta = "+aliasPA+".nome_porta");
					if(isFilterGruppoErogazione) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasPA+".id_servizio = "+aliasSERVIZI+".id");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasSERVIZI+".id_accordo = "+aliasACCORDI+".id");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+".id_accordo = "+aliasACCORDI+".id");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+".id_gruppo = "+aliasGRUPPI+".id");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasGRUPPI+".nome = ?");
						existsParameters.add(filterGruppo);
					}
					if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasPA+".id_soggetto = "+aliasSOGGETTI+".id");
						if(apiImplementazioneErogazione!=null) {
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasPA+".tipo_servizio = ?");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasPA+".servizio = ?");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasPA+".versione_servizio = ?");
							existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
							existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
							existsParameters.add(apiImplementazioneErogazione.getTipo());
							existsParameters.add(apiImplementazioneErogazione.getNome());
							existsParameters.add(apiImplementazioneErogazione.getVersione());
						}
						else if(filterSoggettoProprietario) {
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
							existsParameters.add(filterSoggettoTipo);
							existsParameters.add(filterSoggettoNome);
						}
						else if(filterTipoSoggettoProtocollo) {
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereINCondition(aliasSOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
						}
					}
				}
				
				existsConditions.add(sqlQueryObjectControlloTrafficoPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectControlloTrafficoPorteApplicative));
			} 
			
			if(CostantiDB.isAllarmiEnabled()) {
				
				// Allarmi sulle porte delegate
				if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
					ISQLQueryObject sqlQueryObjectAllarmiPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ALLARMI,aliasALLARMI);
					if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,aliasPD);
						if(isFilterGruppoFruizione) {
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
						}
						if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
						}
					}
					sqlQueryObjectAllarmiPorteDelegate.addSelectAliasField(aliasALLARMI, "id", aliasALLARMI+"id");
					sqlQueryObjectAllarmiPorteDelegate.setANDLogicOperator(true);
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasALLARMI+".filtro_ruolo_fruitore = "+CostantiDB.RUOLI+".nome");
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasALLARMI+".filtro_ruolo = 'delegata'");
					if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasALLARMI+".filtro_porta = "+aliasPD+".nome_porta");
						if(isFilterGruppoFruizione) {
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+".id_servizio = "+aliasSERVIZI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasSERVIZI+".id_accordo = "+aliasACCORDI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+".id_accordo = "+aliasACCORDI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+".id_gruppo = "+aliasGRUPPI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasGRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						if(apiImplementazioneFruizione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+".id_soggetto = "+aliasSOGGETTI+".id");
							if(apiImplementazioneFruizione!=null) {
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+".tipo_soggetto_erogatore = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+".nome_soggetto_erogatore = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+".tipo_servizio = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+".nome_servizio = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+".versione_servizio = ?");
								existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
							}
							else if(filterSoggettoProprietario) {
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
								existsParameters.add(filterSoggettoTipo);
								existsParameters.add(filterSoggettoNome);
							}
							else if(filterTipoSoggettoProtocollo) {
								sqlQueryObjectAllarmiPorteDelegate.addWhereINCondition(aliasSOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
							}
						}
					}
					
					existsConditions.add(sqlQueryObjectAllarmiPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAllarmiPorteDelegate));
				} 
				
				// Allarmi sulle porte applicative
				if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
					ISQLQueryObject sqlQueryObjectAllarmiPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ALLARMI,aliasALLARMI);
					if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,aliasPA);
						if(isFilterGruppoErogazione) {
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
						}
						if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
						}
					}
					sqlQueryObjectAllarmiPorteApplicative.addSelectAliasField(aliasALLARMI, "id", aliasALLARMI+"id");
					sqlQueryObjectAllarmiPorteApplicative.setANDLogicOperator(true);
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasALLARMI+".filtro_ruolo_fruitore = "+CostantiDB.RUOLI+".nome");
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasALLARMI+".filtro_ruolo = 'applicativa'");
					if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
						sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasALLARMI+".filtro_porta = "+aliasPA+".nome_porta");
						if(isFilterGruppoErogazione) {
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasPA+".id_servizio = "+aliasSERVIZI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasSERVIZI+".id_accordo = "+aliasACCORDI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+".id_accordo = "+aliasACCORDI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+".id_gruppo = "+aliasGRUPPI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasGRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						if(apiImplementazioneErogazione!=null || filterSoggettoProprietario || filterTipoSoggettoProtocollo) {
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasPA+".id_soggetto = "+aliasSOGGETTI+".id");
							if(apiImplementazioneErogazione!=null) {
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasPA+".tipo_servizio = ?");
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasPA+".servizio = ?");
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasPA+".versione_servizio = ?");
								existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
								existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
								existsParameters.add(apiImplementazioneErogazione.getTipo());
								existsParameters.add(apiImplementazioneErogazione.getNome());
								existsParameters.add(apiImplementazioneErogazione.getVersione());
							}
							else if(filterSoggettoProprietario) {
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasSOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasSOGGETTI+".nome_soggetto = ?");
								existsParameters.add(filterSoggettoTipo);
								existsParameters.add(filterSoggettoNome);
							}
							else if(filterTipoSoggettoProtocollo) {
								sqlQueryObjectAllarmiPorteApplicative.addWhereINCondition(aliasSOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
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
