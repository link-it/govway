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
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.core.registry.Proprieta;
import org.openspcoop2.core.registry.Soggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverRegistroServiziDB_soggettiDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverRegistroServiziDB_soggettiSearchDriver {

	private DriverRegistroServiziDB driver = null;
	
	protected DriverRegistroServiziDB_soggettiSearchDriver(DriverRegistroServiziDB driver) {
		this.driver = driver;
	}
	
	

	protected List<String> soggettiRuoliList(long idSoggetto, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "soggettiRuoliList";
		int idLista = Liste.SOGGETTI_RUOLI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("soggettiRuoliList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<String> listIdRuoli = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_ruolo");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.RUOLI+".nome", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_soggetto");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);

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
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.RUOLI+".nome");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_ruolo");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.RUOLI+".nome", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.RUOLI+".nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
				sqlQueryObject.addFromTable(CostantiDB.RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.RUOLI+".nome");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_soggetto");
				sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_ruolo");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.RUOLI+".nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);

			risultato = stmt.executeQuery();

			listIdRuoli = new ArrayList<>();
			while (risultato.next()) {

				listIdRuoli.add(risultato.getString(1));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return listIdRuoli;
	}
	
	protected List<Proprieta> soggettiProprietaList(long idSoggetto, ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "soggettiRuoliList";
		int idLista = Liste.SOGGETTI_PROP;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("soggettiRuoliList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<Proprieta> lista = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_PROPS);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_PROPS+".id_soggetto");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SOGGETTI_PROPS+".nome", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_PROPS);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_PROPS+".id_soggetto");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);

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
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_PROPS);
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_PROPS+".id");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_PROPS+".nome");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_PROPS+".valore");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_PROPS+".id_soggetto");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SOGGETTI_PROPS+".nome", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI_PROPS+".nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_PROPS);
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_PROPS+".id");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_PROPS+".nome");
				sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_PROPS+".valore");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_PROPS+".id_soggetto");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI_PROPS+".nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSoggetto);

			risultato = stmt.executeQuery();

			lista = new ArrayList<>();
			while (risultato.next()) {
				Proprieta proprieta = new Proprieta();
				proprieta.setId(risultato.getLong("id"));
				proprieta.setNome(risultato.getString("nome"));
				proprieta.setValore(risultato.getString("valore"));
				
				lista.add(proprieta );
			}

		} catch (Exception qe) {
			error = true;
			throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return lista;
	}
	

	protected List<Soggetto> soggettiRegistroListByTipo(String tipoSoggetto,ISearch ricerca) throws DriverRegistroServiziException{
		return soggettiRegistroList("", tipoSoggetto, ricerca);
	}

	protected List<Soggetto> soggettiRegistroList(String superuser, ISearch ricerca) throws DriverRegistroServiziException {
		return soggettiRegistroList(superuser, null, ricerca);
	}

	private List<Soggetto> soggettiRegistroList(String superuser, String tipoSoggetto,ISearch ricerca) throws DriverRegistroServiziException {
		String nomeMetodo = "soggettiRegistroList";
		int idLista = Liste.SOGGETTI;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		List<String> tipoSoggettiProtocollo = null;
		try {
			tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		
		String filterDominio = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_DOMINIO);
		PddTipologia pddTipologia = null;
		if(filterDominio!=null && !"".equals(filterDominio)) {
			pddTipologia = PddTipologia.toPddTipologia(filterDominio);
		}
		
		String filterSoggettoDefaultTmp = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_SOGGETTO_DEFAULT);
		boolean filterSoggettoDefault = false;
		if(filterSoggettoDefaultTmp!=null) {
			filterSoggettoDefault = "true".equalsIgnoreCase(filterSoggettoDefaultTmp.trim());
		}
		
		String filterTipoCredenziali = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_CREDENZIALI);
		String filterCredenziale = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_CREDENZIALE);
		String filterCredenzialeIssuer = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_CREDENZIALE_ISSUER);
		if(filterCredenzialeIssuer!=null && "".equals(filterCredenzialeIssuer)) {
			filterCredenzialeIssuer = null;
		}
		if(filterCredenzialeIssuer!=null && !"ssl".equals(filterTipoCredenziali)) {
			filterCredenzialeIssuer = null;
		}
		
		String filterRuolo = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_RUOLO);
		
		boolean isFilterTipologiaErogatore = true;
		boolean isFilterTipologiaFruitore = true;
		String filterTipologia = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_SOGGETTO);
		if(filterTipologia!=null && !"".equals(filterTipologia)) {
			if(CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_EROGATORE.equals(filterTipologia)) {
				isFilterTipologiaFruitore = false;
			}
			else if(CostantiRegistroServizi.SOGGETTO_TIPOLOGIA_FRUITORE.equals(filterTipologia)) {
				isFilterTipologiaErogatore = false;
			}
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
		
		String filterApiImplementazione = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_API_IMPLEMENTAZIONE);
		IDServizio apiImplementazioneErogazione = null;
		IDFruizione apiImplementazioneFruizione = null;
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
		
		String filtroProprietaNome = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROPRIETA_NOME);
		String filtroProprietaValore = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROPRIETA_VALORE);
		if((filtroProprietaNome!=null && "".equals(filtroProprietaNome))) {
			filtroProprietaNome=null;
		}
		if((filtroProprietaValore!=null && "".equals(filtroProprietaValore))) {
			filtroProprietaValore=null;
		}
		boolean filtroProprieta = filtroProprietaNome!=null || filtroProprietaValore!=null;
		
		this.driver.logDebug("search : " + search);
		this.driver.logDebug("filterProtocollo : " + filterProtocollo);
		this.driver.logDebug("filterProtocolli : " + filterProtocolli);
		this.driver.logDebug("filterDominio : " + filterDominio);
		this.driver.logDebug("filterSoggettoDefault : " + filterSoggettoDefault);
		this.driver.logDebug("filterRuolo : " + filterRuolo);
		this.driver.logDebug("filterTipoCredenziali : " + filterTipoCredenziali);
		this.driver.logDebug("filterCredenziale : " + filterCredenziale);
		this.driver.logDebug("filterCredenzialeIssuer : " + filterCredenzialeIssuer);
		this.driver.logDebug("filterTipologiaSoggetto : " + filterTipologia);
		this.driver.logDebug("filterGruppo : " + filterGruppo);
		this.driver.logDebug("filterApiContesto : " + filterApiContesto);
		this.driver.logDebug("filterApiImplementazione : " + filterApiImplementazione);
		this.driver.logDebug("filtroProprietaNome : " + filtroProprietaNome);
		this.driver.logDebug("filtroProprietaValore : " + filtroProprietaValore);
		
		Connection con = null;
		boolean error = false;
		PreparedStatement stmt = null;
		ResultSet risultato = null;
		ArrayList<Soggetto> lista = new ArrayList<Soggetto>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("soggettiRegistroList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverRegistroServiziException("[DriverRegistroServiziDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			/* === Filtro Dominio === */
			
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
			
			
			/* === Filtro Tipologia, Gruppo, Tipo Erogazione/Fruizione, Implementazione API === */
			
			List<String> existsConditions = new ArrayList<>();
			List<Object> existsParameters = new ArrayList<>();
			String alias_CT = "ct";
			String alias_ALLARMI = "alarm";
			String alias_PD = "pd";
			String alias_PA_SOGGETTI = "pasog";
			String alias_PA = "pa";
			String alias_PA_TRASFORMAZIONI_SOGGETTI = "patsog";
			String alias_PA_TRASFORMAZIONI = "pat";
			String alias_SERVIZI = "s";
			String alias_ACCORDI = "a";
			String alias_ACCORDI_GRUPPI = "ag";
			String alias_GRUPPI = "g";
			String alias_SOGGETTI = "sProprietario";
			
			
			// ** erogatore **
			
			if(isFilterTipologiaErogatore) {
			
				// indipedente da fruizione o erogazione
				if(apiContesto==null) {
					
					if(!isFilterTipologiaFruitore || (isFilterGruppoFruizione || isFilterGruppoErogazione) ) {
						ISQLQueryObject sqlQueryObjectErogatore = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObjectErogatore.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
						if(isFilterGruppoFruizione) {
							sqlQueryObjectErogatore.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
							sqlQueryObjectErogatore.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
							sqlQueryObjectErogatore.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
						}
						sqlQueryObjectErogatore.addSelectAliasField(alias_SERVIZI, "id", alias_SERVIZI+"id");
						sqlQueryObjectErogatore.setANDLogicOperator(true);
						sqlQueryObjectErogatore.addWhereCondition(alias_SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
						if(isFilterGruppoFruizione) {
							sqlQueryObjectErogatore.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectErogatore.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectErogatore.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
							sqlQueryObjectErogatore.addWhereCondition(alias_GRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						existsConditions.add(sqlQueryObjectErogatore.getWhereExistsCondition(false, sqlQueryObjectErogatore));
					}
					
				}
				else {
					
					// erogatore sulle porte delegate
					if(TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
						ISQLQueryObject sqlQueryObjectErogatorePorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObjectErogatorePorteDelegate.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
						if(isFilterGruppoFruizione) {
							sqlQueryObjectErogatorePorteDelegate.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
							sqlQueryObjectErogatorePorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
							sqlQueryObjectErogatorePorteDelegate.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
						}
						if(TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
							sqlQueryObjectErogatorePorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
							if(apiImplementazioneFruizione!=null) {
								sqlQueryObjectErogatorePorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
							}
						}
						sqlQueryObjectErogatorePorteDelegate.addSelectAliasField(alias_SERVIZI, "id", alias_SERVIZI+"id");
						sqlQueryObjectErogatorePorteDelegate.setANDLogicOperator(true);
						sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
						if(isFilterGruppoFruizione) {
							sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
							sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_GRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						if(TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
							sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_PD+".id_servizio = "+alias_SERVIZI+".id");
							if(apiImplementazioneFruizione!=null) {
								sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+alias_SOGGETTI+".id");
								sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
								sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_PD+".tipo_soggetto_erogatore = ?");
								sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_PD+".nome_soggetto_erogatore = ?");
								sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_PD+".tipo_servizio = ?");
								sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_PD+".nome_servizio = ?");
								sqlQueryObjectErogatorePorteDelegate.addWhereCondition(alias_PD+".versione_servizio = ?");
								existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
							}
						}					
						existsConditions.add(sqlQueryObjectErogatorePorteDelegate.getWhereExistsCondition(false, sqlQueryObjectErogatorePorteDelegate));
					} 
					
					// erogatore sulle porte applicative
					
					else if(TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
						ISQLQueryObject sqlQueryObjectErogatorePorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObjectErogatorePorteApplicative.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
						if(isFilterGruppoErogazione) {
							sqlQueryObjectErogatorePorteApplicative.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
							sqlQueryObjectErogatorePorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
							sqlQueryObjectErogatorePorteApplicative.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
						}
						if(TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
							sqlQueryObjectErogatorePorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
						}
						sqlQueryObjectErogatorePorteApplicative.addSelectAliasField(alias_SERVIZI, "id", alias_SERVIZI+"id");
						sqlQueryObjectErogatorePorteApplicative.setANDLogicOperator(true);
						sqlQueryObjectErogatorePorteApplicative.addWhereCondition(alias_SERVIZI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
						if(isFilterGruppoErogazione) {
							sqlQueryObjectErogatorePorteApplicative.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectErogatorePorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectErogatorePorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
							sqlQueryObjectErogatorePorteApplicative.addWhereCondition(alias_GRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						if(TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
							sqlQueryObjectErogatorePorteApplicative.addWhereCondition(alias_PA+".id_servizio = "+alias_SERVIZI+".id");
							if(apiImplementazioneErogazione!=null) {
								sqlQueryObjectErogatorePorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
								sqlQueryObjectErogatorePorteApplicative.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectErogatorePorteApplicative.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
								sqlQueryObjectErogatorePorteApplicative.addWhereCondition(alias_PA+".tipo_servizio = ?");
								sqlQueryObjectErogatorePorteApplicative.addWhereCondition(alias_PA+".servizio = ?");
								sqlQueryObjectErogatorePorteApplicative.addWhereCondition(alias_PA+".versione_servizio = ?");
								existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
								existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
								existsParameters.add(apiImplementazioneErogazione.getTipo());
								existsParameters.add(apiImplementazioneErogazione.getNome());
								existsParameters.add(apiImplementazioneErogazione.getVersione());
							}
						}					
						existsConditions.add(sqlQueryObjectErogatorePorteApplicative.getWhereExistsCondition(false, sqlQueryObjectErogatorePorteApplicative));
					} 
				}
			}
			

			
			// ** fruitore **
			
			if(isFilterTipologiaFruitore) {
				
				// porte delegate proprietario
				if( (!isFilterTipologiaErogatore && apiContesto==null) || isFilterGruppoFruizione || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
					ISQLQueryObject sqlQueryObjectProprietrioPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectProprietrioPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
					if(isFilterGruppoFruizione) {
						sqlQueryObjectProprietrioPorteDelegate.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
						sqlQueryObjectProprietrioPorteDelegate.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
						sqlQueryObjectProprietrioPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
						sqlQueryObjectProprietrioPorteDelegate.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
					}
					if(apiImplementazioneFruizione!=null) {
						sqlQueryObjectProprietrioPorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
					}
					sqlQueryObjectProprietrioPorteDelegate.addSelectAliasField(alias_PD, "id", alias_PD+"id");
					sqlQueryObjectProprietrioPorteDelegate.setANDLogicOperator(true);
					sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
					if(isFilterGruppoFruizione) {
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_PD+".id_servizio = "+alias_SERVIZI+".id");
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_GRUPPI+".nome = ?");
						existsParameters.add(filterGruppo);
					}
					if(apiImplementazioneFruizione!=null) {
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto = ?");
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_PD+".tipo_soggetto_erogatore = ?");
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_PD+".nome_soggetto_erogatore = ?");
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_PD+".tipo_servizio = ?");
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_PD+".nome_servizio = ?");
						sqlQueryObjectProprietrioPorteDelegate.addWhereCondition(alias_PD+".versione_servizio = ?");
						existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
						existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
						existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
						existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
						existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
						existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
						existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
					}
					
					existsConditions.add(sqlQueryObjectProprietrioPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectProprietrioPorteDelegate));
				} 
				
				// autorizzazioni soggetti porte applicative
				if( (!isFilterTipologiaErogatore && apiContesto==null) || isFilterGruppoErogazione || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
					ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE_SOGGETTI,alias_PA_SOGGETTI);
					sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
					if(isFilterGruppoErogazione) {
						sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
						sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
						sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
						sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
					}
					if(apiImplementazioneErogazione!=null) {
						sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
					}
					sqlQueryObjectAutorizzazioniPorteApplicative.addSelectAliasField(alias_PA_SOGGETTI, "id", alias_PA_SOGGETTI+"id");
					sqlQueryObjectAutorizzazioniPorteApplicative.setANDLogicOperator(true);
					sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA_SOGGETTI+".tipo_soggetto = "+CostantiDB.SOGGETTI+".tipo_soggetto");
					sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA_SOGGETTI+".nome_soggetto = "+CostantiDB.SOGGETTI+".nome_soggetto");
					sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA_SOGGETTI+".id_porta = "+alias_PA+".id");
					if(isFilterGruppoErogazione) {
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA+".id_servizio = "+alias_SERVIZI+".id");
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_GRUPPI+".nome = ?");
						existsParameters.add(filterGruppo);
					}
					if(apiImplementazioneErogazione!=null) {
						sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+alias_SOGGETTI+".id");
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
					
					existsConditions.add(sqlQueryObjectAutorizzazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteApplicative));
				} 
				
				// controllo del traffico (fruitore)
				
				// Controllo del traffico sulle porte delegate
				// e' cablato il soggetto fruitore e il proprietrio della porta delegata
				/*
				if( (!isFilterTipologiaErogatore && apiContesto==null) || isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
					ISQLQueryObject sqlQueryObjectControlloTrafficoPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY,alias_CT);
					if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
						if(isFilterGruppoFruizione) {
							sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
							sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
							sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
							sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
						}
						if(apiImplementazioneFruizione!=null) {
							sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
						}
					}
					sqlQueryObjectControlloTrafficoPorteDelegate.addSelectAliasField(alias_CT, "id", alias_CT+"id");
					sqlQueryObjectControlloTrafficoPorteDelegate.setANDLogicOperator(true);
					sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_CT+".filtro_tipo_fruitore = "+CostantiDB.SOGGETTI+".tipo_soggetto");
					sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_CT+".filtro_nome_fruitore = "+CostantiDB.SOGGETTI+".nome_soggetto");
					sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_CT+".filtro_ruolo = 'delegata'");
					if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_CT+".filtro_porta = "+alias_PD+".nome_porta");
						if(isFilterGruppoFruizione) {
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_PD+".id_servizio = "+alias_SERVIZI+".id");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_GRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						if(apiImplementazioneFruizione!=null) {
							sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+alias_SOGGETTI+".id");
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
					}
					
					existsConditions.add(sqlQueryObjectControlloTrafficoPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectControlloTrafficoPorteDelegate));
				} 
				*/
				
				// Controllo del traffico sulle porte applicative
				if( (!isFilterTipologiaErogatore && apiContesto==null) || isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
					ISQLQueryObject sqlQueryObjectControlloTrafficoPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY,alias_CT);
					if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
						if(isFilterGruppoErogazione) {
							sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
							sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
							sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
							sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
						}
						if(apiImplementazioneErogazione!=null) {
							sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
						}
					}
					sqlQueryObjectControlloTrafficoPorteApplicative.addSelectAliasField(alias_CT, "id", alias_CT+"id");
					sqlQueryObjectControlloTrafficoPorteApplicative.setANDLogicOperator(true);
					sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_CT+".filtro_tipo_fruitore = "+CostantiDB.SOGGETTI+".tipo_soggetto");
					sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_CT+".filtro_nome_fruitore = "+CostantiDB.SOGGETTI+".nome_soggetto");
					sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_CT+".filtro_ruolo = 'applicativa'");
					if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_CT+".filtro_porta = "+alias_PA+".nome_porta");
						if(isFilterGruppoErogazione) {
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_PA+".id_servizio = "+alias_SERVIZI+".id");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_GRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						if(apiImplementazioneErogazione!=null) {
							sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+alias_SOGGETTI+".id");
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
					}
					
					existsConditions.add(sqlQueryObjectControlloTrafficoPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectControlloTrafficoPorteApplicative));
				} 
				
				if(CostantiDB.isAllarmiEnabled()) {
					
					// allarmi (fruitore)
					
					// Allarmi sulle porte delegate
					// e' cablato il soggetto fruitore e il proprietrio della porta delegata
					/*
					if( (!isFilterTipologiaErogatore && apiContesto==null) || isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
						ISQLQueryObject sqlQueryObjectPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.ALLARMI,alias_ALLARMI);
						if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null) {
							sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
							if(isFilterGruppoFruizione) {
								sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
								sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
								sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
								sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
							}
							if(apiImplementazioneFruizione!=null) {
								sqlQueryObjectPorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
							}
						}
						sqlQueryObjectPorteDelegate.addSelectAliasField(alias_ALLARMI, "id", alias_ALLARMI+"id");
						sqlQueryObjectPorteDelegate.setANDLogicOperator(true);
						sqlQueryObjectPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_tipo_fruitore = "+CostantiDB.SOGGETTI+".tipo_soggetto");
						sqlQueryObjectPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_nome_fruitore = "+CostantiDB.SOGGETTI+".nome_soggetto");
						sqlQueryObjectPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_ruolo = 'delegata'");
						if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null) {
							sqlQueryObjectPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_porta = "+alias_PD+".nome_porta");
							if(isFilterGruppoFruizione) {
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".id_servizio = "+alias_SERVIZI+".id");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_GRUPPI+".nome = ?");
								existsParameters.add(filterGruppo);
							}
							if(apiImplementazioneFruizione!=null) {
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+alias_SOGGETTI+".id");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".tipo_soggetto_erogatore = ?");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".nome_soggetto_erogatore = ?");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".tipo_servizio = ?");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".nome_servizio = ?");
								sqlQueryObjectPorteDelegate.addWhereCondition(alias_PD+".versione_servizio = ?");
								existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
								existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
							}
						}
						
						existsConditions.add(sqlQueryObjectPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectPorteDelegate));
					} 
					*/
					
					// Allarmi sulle porte applicative
					if( (!isFilterTipologiaErogatore && apiContesto==null) || isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
						ISQLQueryObject sqlQueryObjectPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
						sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.ALLARMI,alias_ALLARMI);
						if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null) {
							sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
							if(isFilterGruppoErogazione) {
								sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
								sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
								sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
								sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
							}
							if(apiImplementazioneErogazione!=null) {
								sqlQueryObjectPorteApplicative.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
							}
						}
						sqlQueryObjectPorteApplicative.addSelectAliasField(alias_ALLARMI, "id", alias_ALLARMI+"id");
						sqlQueryObjectPorteApplicative.setANDLogicOperator(true);
						sqlQueryObjectPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_tipo_fruitore = "+CostantiDB.SOGGETTI+".tipo_soggetto");
						sqlQueryObjectPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_nome_fruitore = "+CostantiDB.SOGGETTI+".nome_soggetto");
						sqlQueryObjectPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_ruolo = 'applicativa'");
						if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null) {
							sqlQueryObjectPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_porta = "+alias_PA+".nome_porta");
							if(isFilterGruppoErogazione) {
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_PA+".id_servizio = "+alias_SERVIZI+".id");
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_GRUPPI+".nome = ?");
								existsParameters.add(filterGruppo);
							}
							if(apiImplementazioneErogazione!=null) {
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+alias_SOGGETTI+".id");
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_PA+".tipo_servizio = ?");
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_PA+".servizio = ?");
								sqlQueryObjectPorteApplicative.addWhereCondition(alias_PA+".versione_servizio = ?");
								existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
								existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
								existsParameters.add(apiImplementazioneErogazione.getTipo());
								existsParameters.add(apiImplementazioneErogazione.getNome());
								existsParameters.add(apiImplementazioneErogazione.getVersione());
							}
						}
						
						existsConditions.add(sqlQueryObjectPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectPorteApplicative));
					} 
				}
				
				// porte_applicative trasformazioni
				
				if((!isFilterTipologiaErogatore && apiContesto==null) ||  isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
					ISQLQueryObject sqlQueryObjectTrasformazioniPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SOGGETTI,alias_PA_TRASFORMAZIONI_SOGGETTI);
					sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI,alias_PA_TRASFORMAZIONI);
					sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
					if(isFilterGruppoErogazione) {
						sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
						sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
						sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
						sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
					}
					if(apiImplementazioneErogazione!=null) {
						sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
					}
					sqlQueryObjectTrasformazioniPorteApplicative.addSelectAliasField(alias_PA_TRASFORMAZIONI_SOGGETTI, "id", alias_PA_TRASFORMAZIONI_SOGGETTI+"id");
					sqlQueryObjectTrasformazioniPorteApplicative.setANDLogicOperator(true);
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA_TRASFORMAZIONI_SOGGETTI+".tipo_soggetto = "+CostantiDB.SOGGETTI+".tipo_soggetto");
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA_TRASFORMAZIONI_SOGGETTI+".nome_soggetto = "+CostantiDB.SOGGETTI+".nome_soggetto");
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA_TRASFORMAZIONI_SOGGETTI+".id_trasformazione = "+alias_PA_TRASFORMAZIONI+".id");
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA_TRASFORMAZIONI+".id_porta = "+alias_PA+".id");
					if(isFilterGruppoErogazione) {
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA+".id_servizio = "+alias_SERVIZI+".id");
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_GRUPPI+".nome = ?");
						existsParameters.add(filterGruppo);
					}
					if(apiImplementazioneErogazione!=null) {
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+alias_SOGGETTI+".id");
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA+".tipo_servizio = ?");
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA+".servizio = ?");
						sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA+".versione_servizio = ?");
						existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
						existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
						existsParameters.add(apiImplementazioneErogazione.getTipo());
						existsParameters.add(apiImplementazioneErogazione.getNome());
						existsParameters.add(apiImplementazioneErogazione.getVersione());
					}
					
					existsConditions.add(sqlQueryObjectTrasformazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectTrasformazioniPorteApplicative));
				} 
			}
			

	
			
			
			
			
			
			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(filterSoggettoDefault) {
					sqlQueryObject.addWhereCondition("is_default = ?");
				}
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(tipoSoggetto!=null){
					sqlQueryObject.addWhereCondition("tipo_soggetto=?");
					sqlQueryObject.addWhereLikeCondition("nome_soggetto", search, true, true);	
				}
				else{
					sqlQueryObject.addWhereCondition(false, 
							sqlQueryObject.getWhereLikeCondition("tipo_soggetto", search, true, true),
							sqlQueryObject.getWhereLikeCondition("nome_soggetto", search, true, true));
				}
				if(tipoSoggetto==null && tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
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
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
					sqlQueryObject.addFromTable(CostantiDB.RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_soggetto");
					sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_ruolo");
					sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".nome=?");
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipoauth = ?");
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SOGGETTI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
				}
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaSoggetto(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if(filterSoggettoDefault) {
					sqlQueryObject.addWhereCondition("is_default = ?");
				}
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(tipoSoggetto!=null)
					sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				if(tipoSoggetto==null && tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
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
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
					sqlQueryObject.addFromTable(CostantiDB.RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_soggetto");
					sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_ruolo");
					sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".nome=?");
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipoauth = ?");
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SOGGETTI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
				}
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaSoggetto(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(filterSoggettoDefault) {
				stmt.setInt(index++, 1);
			}
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals(""))){
				stmt.setString(index++, superuser);
			}
			if (!search.equals("")) {
				if(tipoSoggetto!=null){
					stmt.setString(index++, tipoSoggetto);
				}
			}else{
				if(tipoSoggetto!=null) 
					stmt.setString(index++, tipoSoggetto);
			}
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			if(filterRuolo!=null && !"".equals(filterRuolo)) {
				stmt.setString(index++, filterRuolo);
			}
			if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
				stmt.setString(index++, filterTipoCredenziali);
//				if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
//					// like
//				}
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
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"id","identificativoSoggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"descrizione","descrizioneSoggetto");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addSelectField("server");
				sqlQueryObject.addSelectField("id_connettore");
				sqlQueryObject.addSelectField("codice_ipa");
				sqlQueryObject.addSelectField("tipoauth");
				
				if(filterSoggettoDefault) {
					sqlQueryObject.addWhereCondition("is_default = ?");
				}
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(tipoSoggetto!=null){
					sqlQueryObject.addWhereCondition("tipo_soggetto=?");
					sqlQueryObject.addWhereLikeCondition("nome_soggetto", search, true, true);	
				}
				else{
					sqlQueryObject.addWhereCondition(false, 
							sqlQueryObject.getWhereLikeCondition("tipo_soggetto", search, true, true),
							sqlQueryObject.getWhereLikeCondition("nome_soggetto", search, true, true));
				}
				if(tipoSoggetto==null && tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
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
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
					sqlQueryObject.addFromTable(CostantiDB.RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_soggetto");
					sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_ruolo");
					sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".nome=?");
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipoauth = ?");
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SOGGETTI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
				}
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaSoggetto(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"id","identificativoSoggetto");
				sqlQueryObject.addSelectField("nome_soggetto");
				sqlQueryObject.addSelectField("tipo_soggetto");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"descrizione","descrizioneSoggetto");
				sqlQueryObject.addSelectField("identificativo_porta");
				sqlQueryObject.addSelectField("server");
				sqlQueryObject.addSelectField("id_connettore");
				sqlQueryObject.addSelectField("codice_ipa");
				sqlQueryObject.addSelectField("tipoauth");
				
				if(filterSoggettoDefault) {
					sqlQueryObject.addWhereCondition("is_default = ?");
				}
				if(this.driver.useSuperUser && superuser!=null && (!superuser.equals("")))
					sqlQueryObject.addWhereCondition("superuser = ?");
				if(tipoSoggetto!=null)
					sqlQueryObject.addWhereCondition("tipo_soggetto=?");
				if(tipoSoggetto==null && tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition("tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
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
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI_RUOLI);
					sqlQueryObject.addFromTable(CostantiDB.RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_soggetto");
					sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".id="+CostantiDB.SOGGETTI_RUOLI+".id_ruolo");
					sqlQueryObject.addWhereCondition(CostantiDB.RUOLI+".nome=?");
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipoauth = ?");
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SOGGETTI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SOGGETTI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
				}
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaSoggetto(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome_soggetto");
				sqlQueryObject.addOrderBy("tipo_soggetto");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			index = 1;
			if(filterSoggettoDefault) {
				stmt.setInt(index++, 1);
			}
			if(this.driver.useSuperUser && superuser!=null && (!superuser.equals(""))){
				stmt.setString(index++, superuser);
			}
			if (!search.equals("")) {
				if(tipoSoggetto!=null){
					stmt.setString(index++, tipoSoggetto);
				}
			}else{
				if(tipoSoggetto!=null) 
					stmt.setString(index++, tipoSoggetto);
			}
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
			}
			if(filterRuolo!=null && !"".equals(filterRuolo)) {
				stmt.setString(index++, filterRuolo);
			}
			if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
				stmt.setString(index++, filterTipoCredenziali);
//				if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
//					// like
//				}
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

			Soggetto sog;
			while (risultato.next()) {

				sog = new Soggetto();
				sog.setId(risultato.getLong("identificativoSoggetto"));
				sog.setNome(risultato.getString("nome_soggetto"));
				sog.setTipo(risultato.getString("tipo_soggetto"));
				sog.setDescrizione(risultato.getString("descrizioneSoggetto"));
				sog.setIdentificativoPorta(risultato.getString("identificativo_porta"));
				sog.setPortaDominio(risultato.getString("server"));
				sog.setCodiceIpa(risultato.getString("codice_ipa"));

				long idConnettore = risultato.getLong("id_connettore");
				sog.setConnettore(this.driver.getConnettore(idConnettore, con));

				String tipoAuth = risultato.getString("tipoauth");
				if(tipoAuth != null && !tipoAuth.equals("")){
					CredenzialiSoggetto credenziali = new CredenzialiSoggetto();
					credenziali.setTipo(DriverRegistroServiziDB_LIB.getEnumCredenzialeTipo(tipoAuth));
					sog.addCredenziali( credenziali );
				}
				
				lista.add(sog);

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
