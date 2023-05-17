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
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.core.commons.DBUtils;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.commons.SearchUtils;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.Soggetto;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.config.constants.RicercaTipologiaErogazione;
import org.openspcoop2.core.config.constants.RicercaTipologiaFruizione;
import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.config.constants.TipologiaErogazione;
import org.openspcoop2.core.config.constants.TipologiaFruizione;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.TipologiaServizioApplicativo;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDFruizione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.PddTipologia;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;

/**
 * DriverConfigurazioneDB_serviziApplicativiSearchDriver
 * 
 * 
 * @author Sandra Giangrandi (sandra@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DriverConfigurazioneDB_serviziApplicativiSearchDriver {

	private DriverConfigurazioneDB driver = null;
	private DriverConfigurazioneDBSoggetti soggettiDriver = null;
	private DriverConfigurazioneDB_serviziApplicativiDriver serviziApplicativiDriver = null;
	
	protected DriverConfigurazioneDB_serviziApplicativiSearchDriver(DriverConfigurazioneDB driver) {
		this.driver = driver;
		this.soggettiDriver = new DriverConfigurazioneDBSoggetti(driver);
		this.serviziApplicativiDriver = new DriverConfigurazioneDB_serviziApplicativiDriver(driver);
	}
	
	private List<ServizioApplicativo> servizioApplicativoList(ISearch ricerca,Long idProprietario) throws DriverConfigurazioneException{
		String nomeMetodo = "servizioApplicativoList";
		int idLista = Liste.SERVIZIO_APPLICATIVO;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));


		Connection con = null;
		boolean error = false;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		ArrayList<ServizioApplicativo> lista = new ArrayList<ServizioApplicativo>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("servizioApplicativoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);

				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto=?");

				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addSelectCountField("*", "cont");

				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto=?");

				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(idProprietario!=null) stmt.setLong(1, idProprietario);
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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);
				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto=?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addSelectField("id");
				sqlQueryObject.addSelectField("nome");
				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto=?");
				sqlQueryObject.addOrderBy("nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			if(idProprietario!=null) stmt.setLong(1, idProprietario);
			risultato = stmt.executeQuery();

			ServizioApplicativo sa;
			while (risultato.next()) {

				sa=this.serviziApplicativiDriver.getServizioApplicativo(risultato.getLong("id"));

				//				sa = new ServizioApplicativo();

				//				sa.setId(risultato.getLong("id"));
				//				sa.setNome(risultato.getString("nome"));
				//				sa.setDescrizione(risultato.getString("descrizione"));
				//				sa.setIdSoggetto(risultato.getLong("id_soggetto"));

				lista.add(sa);

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

	/**
	 * Recupera tutti i servizi applicativi in base ai parametri di ricerca passati
	 */
	protected List<ServizioApplicativo> servizioApplicativoList(ISearch ricerca) throws DriverConfigurazioneException {
		return this.servizioApplicativoList(ricerca, null);
	}

	protected List<ServizioApplicativo> servizioApplicativoList(IDSoggetto idSO,ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoList";
		Connection con = null;
		boolean error = false;

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("servizioApplicativoList(idSoggetto)");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		try {

			return this.servizioApplicativoList(ricerca, DBUtils.getIdSoggetto(idSO.getNome(), idSO.getTipo(), con, this.driver.tipoDB));

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	private ISQLQueryObject buildSqlQueryObjectAutorizzazioniPorteDelegate(String nomeTabella,
			String alias_PD_SA, String alias_PD, String alias_SERVIZI, String alias_ACCORDI, String alias_ACCORDI_GRUPPI, String alias_GRUPPI, String alias_SOGGETTI,
			boolean isFilterGruppoFruizione, IDFruizione apiImplementazioneFruizione, String filterGruppo,
			List<Object> existsParameters) throws Exception {
		ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
		sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(nomeTabella,alias_PD_SA);
		sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
		if(isFilterGruppoFruizione) {
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
		}
		if(apiImplementazioneFruizione!=null) {
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
		}
		sqlQueryObjectAutorizzazioniPorteDelegate.addSelectAliasField(alias_PD_SA, "id", alias_PD_SA+"id");
		sqlQueryObjectAutorizzazioniPorteDelegate.setANDLogicOperator(true);
		sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD_SA+".id_servizio_applicativo = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
		sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD_SA+".id_porta = "+alias_PD+".id");
		if(isFilterGruppoFruizione) {
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD+".id_servizio = "+alias_SERVIZI+".id");
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_GRUPPI+".nome = ?");
			existsParameters.add(filterGruppo);
		}
		if(apiImplementazioneFruizione!=null) {
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+alias_SOGGETTI+".id");
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
		return sqlQueryObjectAutorizzazioniPorteDelegate;
	}
	
	private ISQLQueryObject buildSqlQueryObjectAutorizzazioniPorteApplicative(String nomeTabella,
			String alias_PA_SA, String alias_PA, String alias_SERVIZI, String alias_ACCORDI, String alias_ACCORDI_GRUPPI, String alias_GRUPPI, String alias_SOGGETTI,
			boolean isFilterGruppoErogazione, IDServizio apiImplementazioneErogazione, String filterGruppo,
			List<Object> existsParameters) throws Exception {
		ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
		sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(nomeTabella,alias_PA_SA);
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
		sqlQueryObjectAutorizzazioniPorteApplicative.addSelectAliasField(alias_PA_SA, "id", alias_PA_SA+"id");
		sqlQueryObjectAutorizzazioniPorteApplicative.setANDLogicOperator(true);
		sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA_SA+".id_servizio_applicativo = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
		sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(alias_PA_SA+".id_porta = "+alias_PA+".id");
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
		return sqlQueryObjectAutorizzazioniPorteApplicative;
	}
	
	protected List<ServizioApplicativo> soggettiServizioApplicativoList(String superuser, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiServizioApplicativoList";
		int idLista = Liste.SERVIZIO_APPLICATIVO;
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
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
		
		String filterRuoloServizioApplicativo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO);
		TipologiaFruizione tipologiaFruizione = null;
		TipologiaErogazione tipologiaErogazione = null;
		if(filterRuoloServizioApplicativo!=null && !"".equals(filterRuoloServizioApplicativo)) {
			if(Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_EROGATORE.equals(filterRuoloServizioApplicativo)) {
				tipologiaErogazione = TipologiaErogazione.DISABILITATO;
			}
			else if(Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE.equals(filterRuoloServizioApplicativo)) {
				tipologiaFruizione = TipologiaFruizione.DISABILITATO;
			}
		}
		
		String filterSoggettoTipoNome = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_SOGGETTO);
		String filterSoggettoTipo = null;
		String filterSoggettoNome = null;
		if(filterSoggettoTipoNome!=null && !"".equals(filterSoggettoTipoNome)) {
			filterSoggettoTipo = filterSoggettoTipoNome.split("/")[0];
			filterSoggettoNome = filterSoggettoTipoNome.split("/")[1];
		}
		
		String filterDominio = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_DOMINIO);
		PddTipologia pddTipologia = null;
		if(filterDominio!=null && !"".equals(filterDominio)) {
			pddTipologia = PddTipologia.toPddTipologia(filterDominio);
		}
		
		String filterRuolo = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_RUOLO);
		
		String filterTipoServizioApplicativo = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO);
		
		String filterTipoCredenziali = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_CREDENZIALI);
		String filterCredenziale = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_CREDENZIALE);
		String filterCredenzialeIssuer = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_CREDENZIALE_ISSUER);
		if(filterCredenzialeIssuer!=null && "".equals(filterCredenzialeIssuer)) {
			filterCredenzialeIssuer = null;
		}
		if(filterCredenzialeIssuer!=null && !"ssl".equals(filterTipoCredenziali)) {
			filterCredenzialeIssuer = null;
		}
		String filterCredenzialeTokenPolicy = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_CREDENZIALE_TOKEN_POLICY);
		
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
					throw new DriverConfigurazioneException("Filtro API Implementazione '"+filterApiImplementazione+"' non valido: "+e.getMessage(),e);
				}
			}
			else if(TipoPdD.DELEGATA.equals(apiContesto)) {
				try {
					apiImplementazioneFruizione = IDFruizione.toIDFruizione(filterApiImplementazione);
					isFilterGruppoFruizione=false; // non ha piu' senso ho selezionato una fruizione puntuale
				}catch(Exception e) {
					throw new DriverConfigurazioneException("Filtro API Implementazione '"+filterApiImplementazione+"' non valido: "+e.getMessage(),e);
				}
			}
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
		
		String filtroModISicurezzaMessaggio = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_MESSAGGIO);
		String filtroModIKeystorePath = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_KEYSTORE_PATH);
		String filtroModIKeystoreSubject = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_KEYSTORE_SUBJECT);
		String filtroModIKeystoreIssuer = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_KEYSTORE_ISSUER);
		String filtroModISicurezzaToken = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_TOKEN);
		String filtroModITokenPolicy = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_TOKEN_POLICY);
		String filtroModITokenClientId = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_TOKEN_CLIENT_ID);
		String filtroModIAudience = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_AUDIENCE);
		Boolean filtroModISicurezzaMessaggioEnabled = null;
		if(CostantiDB.STATO_FUNZIONALITA_ABILITATO.equals(filtroModISicurezzaMessaggio)) {
			filtroModISicurezzaMessaggioEnabled = true;
		}
		else if(CostantiDB.STATO_FUNZIONALITA_DISABILITATO.equals(filtroModISicurezzaMessaggio)) {
			filtroModISicurezzaMessaggioEnabled = false;
		}
		if(filtroModISicurezzaMessaggioEnabled!=null && filtroModISicurezzaMessaggioEnabled) {
			if((filtroModIKeystorePath!=null && "".equals(filtroModIKeystorePath))) {
				filtroModIKeystorePath=null;
			}
			if((filtroModIKeystoreSubject!=null && "".equals(filtroModIKeystoreSubject))) {
				filtroModIKeystoreSubject=null;
			}
			if((filtroModIKeystoreIssuer!=null && "".equals(filtroModIKeystoreIssuer))) {
				filtroModIKeystoreIssuer=null;
			}
		}
		else {
			filtroModIKeystorePath=null;
			filtroModIKeystoreSubject=null;
			filtroModIKeystoreIssuer=null;
		}
		Boolean filtroModISicurezzaTokenEnabled = null;
		if(CostantiDB.STATO_FUNZIONALITA_ABILITATO.equals(filtroModISicurezzaToken)) {
			filtroModISicurezzaTokenEnabled = true;
		}
		else if(CostantiDB.STATO_FUNZIONALITA_DISABILITATO.equals(filtroModISicurezzaToken)) {
			filtroModISicurezzaTokenEnabled = false;
		}
		if(filtroModISicurezzaTokenEnabled!=null && filtroModISicurezzaTokenEnabled) {
			if((filtroModITokenPolicy!=null && "".equals(filtroModITokenPolicy))) {
				filtroModITokenPolicy=null;
			}
			if((filtroModITokenClientId!=null && "".equals(filtroModITokenClientId))) {
				filtroModITokenClientId=null;
			}
		}
		else {
			filtroModITokenPolicy=null;
			filtroModITokenClientId=null;
		}
		if((filtroModIAudience!=null && "".equals(filtroModIAudience))) {
			filtroModIAudience=null;
		}
		boolean filtroModI = filtroModISicurezzaMessaggioEnabled!=null || 
				filtroModIKeystorePath!=null || filtroModIKeystoreSubject!=null || filtroModIKeystoreIssuer!=null || 
				filtroModISicurezzaTokenEnabled!=null || 
				filtroModITokenPolicy!=null || filtroModITokenClientId!=null ||
				filtroModIAudience!=null;
		
		boolean checkCredenzialiBase = false;
		if( (filterTipoCredenziali==null || "".equals(filterTipoCredenziali)) 
				&&
				(filterCredenziale==null || "".equals(filterCredenziale))
				&&
				(filterCredenzialeIssuer==null || "".equals(filterCredenzialeIssuer))
			) {
			checkCredenzialiBase = true;
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
		this.driver.logDebug("filterSoggettoNome : " + filterSoggettoNome);
		this.driver.logDebug("filterSoggettoTipo : " + filterSoggettoTipo);
		this.driver.logDebug("filterRuoloServizioApplicativo : " + filterRuoloServizioApplicativo);
		this.driver.logDebug("filterRuolo : " + filterRuolo);
		this.driver.logDebug("filterTipoServizioApplicativo : " + filterTipoServizioApplicativo);
		this.driver.logDebug("filterTipoCredenziali : " + filterTipoCredenziali);
		this.driver.logDebug("filterCredenziale : " + filterCredenziale);
		this.driver.logDebug("filterCredenzialeIssuer : " + filterCredenzialeIssuer);
		this.driver.logDebug("filterCredenzialeTokenPolicy : " + filterCredenzialeTokenPolicy);
		this.driver.logDebug("filterGruppo : " + filterGruppo);
		this.driver.logDebug("filterApiContesto : " + filterApiContesto);
		this.driver.logDebug("filterApiImplementazione : " + filterApiImplementazione);
		this.driver.logDebug("filtroConnettoreTipo : " + filtroConnettoreTipo);
		this.driver.logDebug("filtroConnettoreTokenPolicy : " + filtroConnettoreTokenPolicy);
		this.driver.logDebug("filtroConnettoreEndpoint : " + filtroConnettoreEndpoint);
		this.driver.logDebug("filtroConnettoreKeystore : " + filtroConnettoreKeystore);
		this.driver.logDebug("filtroConnettoreDebug : " + filtroConnettoreDebug);
		this.driver.logDebug("filtroConnettoreTipoPlugin : " + filtroConnettoreTipoPlugin);
		this.driver.logDebug("filtroModISicurezzaMessaggio : " + filtroModISicurezzaMessaggio);
		this.driver.logDebug("filtroModIKeystorePath : " + filtroModIKeystorePath);
		this.driver.logDebug("filtroModIKeystoreSubject : " + filtroModIKeystoreSubject);
		this.driver.logDebug("filtroModIKeystoreIssuer : " + filtroModIKeystoreIssuer);
		this.driver.logDebug("filtroModISicurezzaToken : " + filtroModISicurezzaToken);
		this.driver.logDebug("filtroModITokenPolicy : " + filtroModITokenPolicy);
		this.driver.logDebug("filtroModITokenClientId : " + filtroModITokenClientId);
		this.driver.logDebug("filtroModIAudience : " + filtroModIAudience);
		this.driver.logDebug("filtroProprietaNome : " + filtroProprietaNome);
		this.driver.logDebug("filtroProprietaValore : " + filtroProprietaValore);
		
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<ServizioApplicativo> silList = new ArrayList<ServizioApplicativo>();

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
			
			
			
			/* === Filtro Gruppo, Tipo Erogazione/Fruizione, Implementazione API === */
			
			List<String> existsConditions = new ArrayList<>();
			List<Object> existsParameters = new ArrayList<>();
			String alias_CT = "ct";
			String alias_ALLARMI = "alarm";
			String alias_PD_SA = "pdsa";
			String alias_PD_TOKEN_SA = "pdtokensa";
			String alias_PD = "pd";
			String alias_PD_TRASFORMAZIONI_SA = "pdtsa";
			String alias_PD_TRASFORMAZIONI = "pdt";
			String alias_PA_SA = "pasa";
			String alias_PA_TOKEN_SA = "patokensa";
			String alias_PA = "pa";
			String alias_PA_TRASFORMAZIONI_SA = "patsa";
			String alias_PA_TRASFORMAZIONI = "pat";
			String alias_SERVIZI = "s";
			String alias_ACCORDI = "a";
			String alias_ACCORDI_GRUPPI = "ag";
			String alias_GRUPPI = "g";
			String alias_SOGGETTI = "sProprietario";
			
	
			
			// autorizzazioni porte delegate
			// select * from servizi_applicativi sa WHERE EXISTS (select pdsa.id from porte_delegate_sa pdsa, porte_delegate pd, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			//     WHERE pdsa.id_servizio_applicativo=sa.id AND pdsa.id_porta=pd.id AND pd.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = buildSqlQueryObjectAutorizzazioniPorteDelegate(CostantiDB.PORTE_DELEGATE_SA,
						alias_PD_SA, alias_PD, alias_SERVIZI, alias_ACCORDI, alias_ACCORDI_GRUPPI, alias_GRUPPI, alias_SOGGETTI,
						isFilterGruppoFruizione, apiImplementazioneFruizione, filterGruppo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteDelegate));
			} 
			
			// autorizzazioni token porte delegate
			// select * from servizi_applicativi sa WHERE EXISTS (select pdsa.id from porte_delegate_sa pdsa, porte_delegate pd, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			//     WHERE pdsa.id_servizio_applicativo=sa.id AND pdsa.id_porta=pd.id AND pd.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = buildSqlQueryObjectAutorizzazioniPorteDelegate(CostantiDB.PORTE_DELEGATE_TOKEN_SA,
						alias_PD_TOKEN_SA, alias_PD, alias_SERVIZI, alias_ACCORDI, alias_ACCORDI_GRUPPI, alias_GRUPPI, alias_SOGGETTI,
						isFilterGruppoFruizione, apiImplementazioneFruizione, filterGruppo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteDelegate));
			} 
			
			// autorizzazioni porte applicative
			// select * from servizi_applicativi sa WHERE EXISTS (select pasa.id from porte_applicative_sa_auth pasa, porte_applicative pa, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			//    WHERE pasa.id_servizio_applicativo=sa.id AND pasa.id_porta=pa.id AND pa.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = buildSqlQueryObjectAutorizzazioniPorteApplicative(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI,
						alias_PA_SA, alias_PA, alias_SERVIZI, alias_ACCORDI, alias_ACCORDI_GRUPPI, alias_GRUPPI, alias_SOGGETTI,
						isFilterGruppoErogazione, apiImplementazioneErogazione, filterGruppo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteApplicative));
			} 
			
			// autorizzazioni token porte applicative
			// select * from servizi_applicativi sa WHERE EXISTS (select pasa.id from porte_applicative_sa_auth pasa, porte_applicative pa, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			//    WHERE pasa.id_servizio_applicativo=sa.id AND pasa.id_porta=pa.id AND pa.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = buildSqlQueryObjectAutorizzazioniPorteApplicative(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA,
						alias_PA_TOKEN_SA, alias_PA, alias_SERVIZI, alias_ACCORDI, alias_ACCORDI_GRUPPI, alias_GRUPPI, alias_SOGGETTI,
						isFilterGruppoErogazione, apiImplementazioneErogazione, filterGruppo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteApplicative));
			} 
			
			// server sulle porte applicative
			// select * from servizi_applicativi sa WHERE EXISTS (select pasa.id from porte_applicative_sa pasa, porte_applicative pa, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			// WHERE pasa.id_servizio_applicativo=sa.id AND pasa.id_porta=pa.id AND pa.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectServerPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA,alias_PA_SA);
				sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
				if(isFilterGruppoErogazione) {
					sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
					sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
					sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
					sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
				}
				if(apiImplementazioneErogazione!=null) {
					sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
				}
				sqlQueryObjectServerPorteApplicative.addSelectAliasField(alias_PA_SA, "id", alias_PA_SA+"id");
				sqlQueryObjectServerPorteApplicative.setANDLogicOperator(true);
				sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_PA_SA+".id_servizio_applicativo = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_PA_SA+".id_porta = "+alias_PA+".id");
				if(isFilterGruppoErogazione) {
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_PA+".id_servizio = "+alias_SERVIZI+".id");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_GRUPPI+".nome = ?");
					existsParameters.add(filterGruppo);
				}
				if(apiImplementazioneErogazione!=null) {
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+alias_SOGGETTI+".id");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_PA+".tipo_servizio = ?");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_PA+".servizio = ?");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(alias_PA+".versione_servizio = ?");
					existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
					existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
					existsParameters.add(apiImplementazioneErogazione.getTipo());
					existsParameters.add(apiImplementazioneErogazione.getNome());
					existsParameters.add(apiImplementazioneErogazione.getVersione());
				}
				
				existsConditions.add(sqlQueryObjectServerPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectServerPorteApplicative));
			} 
			
		
			// controllo del traffico
			/*
			 * select * from servizi_applicativi sa, soggetti soggetti WHERE sa.id_soggetto=soggetti.id  
			 * AND EXISTS (select ct.id from ct_active_policy ct WHERE ct.filtro_tipo_fruitore=soggetti.tipo_soggetto AND ct.filtro_nome_fruitore=soggetti.nome_soggetto AND ct.filtro_sa_fruitore=sa.nome AND 
(
  (filtro_ruolo='applicativa' AND EXISTS (select pa.id from porte_applicative pa, servizi s, accordi a, accordi_gruppi ag, gruppi g WHERE pa.nome_porta=filtro_porta AND pa.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG'))
  OR
  (filtro_ruolo='delegata' AND EXISTS (select pd.id from porte_delegate pd, servizi s, accordi a, accordi_gruppi ag, gruppi g WHERE pd.nome_porta=filtro_porta AND pd.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG'))
));
			 * 
			 * */
			
			// Controllo del traffico sulle porte delegate
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
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
				sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(alias_CT+".filtro_sa_fruitore = "+CostantiDB.SERVIZI_APPLICATIVI+".nome");
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
			
			// Controllo del traffico sulle porte applicative
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
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
				sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(alias_CT+".filtro_sa_fruitore = "+CostantiDB.SERVIZI_APPLICATIVI+".nome");
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
				
				// Allarmi sulle porte delegate
				if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
					ISQLQueryObject sqlQueryObjectAllarmiPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ALLARMI,alias_ALLARMI);
					if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null) {
						sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
						if(isFilterGruppoFruizione) {
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
						}
						if(apiImplementazioneFruizione!=null) {
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
						}
					}
					sqlQueryObjectAllarmiPorteDelegate.addSelectAliasField(alias_ALLARMI, "id", alias_ALLARMI+"id");
					sqlQueryObjectAllarmiPorteDelegate.setANDLogicOperator(true);
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_tipo_fruitore = "+CostantiDB.SOGGETTI+".tipo_soggetto");
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_nome_fruitore = "+CostantiDB.SOGGETTI+".nome_soggetto");
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_sa_fruitore = "+CostantiDB.SERVIZI_APPLICATIVI+".nome");
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_ruolo = 'delegata'");
					if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null) {
						sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ALLARMI+".filtro_porta = "+alias_PD+".nome_porta");
						if(isFilterGruppoFruizione) {
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_PD+".id_servizio = "+alias_SERVIZI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_GRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						if(apiImplementazioneFruizione!=null) {
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+alias_SOGGETTI+".id");
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
					}
					
					existsConditions.add(sqlQueryObjectAllarmiPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAllarmiPorteDelegate));
				} 
				
				// Allarmi sulle porte applicative
				if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
					ISQLQueryObject sqlQueryObjectAllarmiPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ALLARMI,alias_ALLARMI);
					if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null) {
						sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,alias_PA);
						if(isFilterGruppoErogazione) {
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
						}
						if(apiImplementazioneErogazione!=null) {
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
						}
					}
					sqlQueryObjectAllarmiPorteApplicative.addSelectAliasField(alias_ALLARMI, "id", alias_ALLARMI+"id");
					sqlQueryObjectAllarmiPorteApplicative.setANDLogicOperator(true);
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_tipo_fruitore = "+CostantiDB.SOGGETTI+".tipo_soggetto");
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_nome_fruitore = "+CostantiDB.SOGGETTI+".nome_soggetto");
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_sa_fruitore = "+CostantiDB.SERVIZI_APPLICATIVI+".nome");
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_ruolo = 'applicativa'");
					if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null) {
						sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ALLARMI+".filtro_porta = "+alias_PA+".nome_porta");
						if(isFilterGruppoErogazione) {
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_PA+".id_servizio = "+alias_SERVIZI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_GRUPPI+".nome = ?");
							existsParameters.add(filterGruppo);
						}
						if(apiImplementazioneErogazione!=null) {
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(alias_PA+".id_soggetto = "+alias_SOGGETTI+".id");
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
					}
					
					existsConditions.add(sqlQueryObjectAllarmiPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAllarmiPorteApplicative));
				} 
			}
			
			
			// porte_delegate trasformazioni
			/*
			 * select * from servizi_applicativi sa WHERE EXISTS (select pdtsa.id from pd_transform_sa pdtsa, pd_transform pdt, porte_delegate pd, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			 *    WHERE pdtsa.id_servizio_applicativo=sa.id AND pdtsa.id_trasformazione=pdt.id AND pdt.id_porta=pd.id AND pd.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');
			 **/
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectTrasformazioniPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA,alias_PD_TRASFORMAZIONI_SA);
				sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI,alias_PD_TRASFORMAZIONI);
				sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,alias_PD);
				if(isFilterGruppoFruizione) {
					sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.SERVIZI,alias_SERVIZI);
					sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI,alias_ACCORDI);
					sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,alias_ACCORDI_GRUPPI);
					sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.GRUPPI,alias_GRUPPI);
				}
				if(apiImplementazioneFruizione!=null) {
					sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.SOGGETTI,alias_SOGGETTI);
				}
				sqlQueryObjectTrasformazioniPorteDelegate.addSelectAliasField(alias_PD_TRASFORMAZIONI_SA, "id", alias_PD_TRASFORMAZIONI_SA+"id");
				sqlQueryObjectTrasformazioniPorteDelegate.setANDLogicOperator(true);
				sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_PD_TRASFORMAZIONI_SA+".id_servizio_applicativo = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_PD_TRASFORMAZIONI_SA+".id_trasformazione = "+alias_PD_TRASFORMAZIONI+".id");
				sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_PD_TRASFORMAZIONI+".id_porta = "+alias_PD+".id");
				if(isFilterGruppoFruizione) {
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_PD+".id_servizio = "+alias_SERVIZI+".id");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_SERVIZI+".id_accordo = "+alias_ACCORDI+".id");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_accordo = "+alias_ACCORDI+".id");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_ACCORDI_GRUPPI+".id_gruppo = "+alias_GRUPPI+".id");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_GRUPPI+".nome = ?");
					existsParameters.add(filterGruppo);
				}
				if(apiImplementazioneFruizione!=null) {
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_PD+".id_soggetto = "+alias_SOGGETTI+".id");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_SOGGETTI+".tipo_soggetto = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_SOGGETTI+".nome_soggetto = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_PD+".tipo_soggetto_erogatore = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_PD+".nome_soggetto_erogatore = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_PD+".tipo_servizio = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_PD+".nome_servizio = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(alias_PD+".versione_servizio = ?");
					existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
					existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
					existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
					existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
					existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
					existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
					existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
				}
				
				existsConditions.add(sqlQueryObjectTrasformazioniPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectTrasformazioniPorteDelegate));
			} 
	
			// porte_applicative trasformazioni
			/*
			 * select * from servizi_applicativi sa WHERE EXISTS (select patsa.id from pa_transform_sa patsa, pa_transform pat, porte_applicative pa, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			 * WHERE patsa.id_servizio_applicativo=sa.id AND patsa.id_trasformazione=pat.id AND pat.id_porta=pa.id AND pa.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');
			 **/
			
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectTrasformazioniPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA,alias_PA_TRASFORMAZIONI_SA);
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
				sqlQueryObjectTrasformazioniPorteApplicative.addSelectAliasField(alias_PA_TRASFORMAZIONI_SA, "id", alias_PA_TRASFORMAZIONI_SA+"id");
				sqlQueryObjectTrasformazioniPorteApplicative.setANDLogicOperator(true);
				sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA_TRASFORMAZIONI_SA+".id_servizio_applicativo = "+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(alias_PA_TRASFORMAZIONI_SA+".id_trasformazione = "+alias_PA_TRASFORMAZIONI+".id");
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
						
			// QUERY FINALE
			
			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?",
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
					}
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
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}	
				if(joinConnettore) {
					DBUtils.setFiltriConnettoreApplicativo(sqlQueryObject, this.driver.tipoDB, 
							tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
							filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
				}
				if(filtroModI) {
					DBUtils.setFiltriModIApplicativi(sqlQueryObject, this.driver.tipoDB,
							filtroModISicurezzaMessaggioEnabled,
							filtroModIKeystorePath, filtroModIKeystoreSubject, filtroModIKeystoreIssuer,
							filtroModISicurezzaTokenEnabled,
							filtroModITokenPolicy, filtroModITokenClientId,
							filtroModIAudience,
							checkCredenzialiBase);
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaApplicativo(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else { 
						sqlQueryObject.addWhereCondition("tipo=?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?",
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
					}
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
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}
				if(joinConnettore) {
					DBUtils.setFiltriConnettoreApplicativo(sqlQueryObject, this.driver.tipoDB, 
							tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
							filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
				}
				if(filtroModI) {
					DBUtils.setFiltriModIApplicativi(sqlQueryObject, this.driver.tipoDB,
							filtroModISicurezzaMessaggioEnabled,
							filtroModIKeystorePath, filtroModIKeystoreSubject, filtroModIKeystoreIssuer, 
							filtroModISicurezzaTokenEnabled,
							filtroModITokenPolicy, filtroModITokenClientId,
							filtroModIAudience,
							checkCredenzialiBase);
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaApplicativo(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			this.driver.logDebug("Execute query: "+queryString);
			int index = 1;
			if(this.driver.useSuperUser && superuser!=null && !superuser.equals("")) {
				stmt.setString(index++, superuser);
			}
			if(tipologiaFruizione!=null) {
				stmt.setString(index++, tipologiaFruizione.getValue());
			}
			else if(tipologiaErogazione!=null) {
				stmt.setString(index++, tipologiaErogazione.getValue());
			}
			if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
				stmt.setString(index++, filterSoggettoTipo);
				stmt.setString(index++, filterSoggettoNome);
			}
			if(filterRuolo!=null && !"".equals(filterRuolo)) {
				stmt.setString(index++, filterRuolo);
			}
			if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
				if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
					stmt.setString(index++, CostantiConfigurazione.SERVER);
					stmt.setString(index++, CostantiConfigurazione.CLIENT);
				} else {
					stmt.setString(index++, filterTipoServizioApplicativo);
					if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						stmt.setInt(index++, CostantiDB.TRUE);
					}
				}
			}
			if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
				stmt.setString(index++, filterTipoCredenziali);
				if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
					stmt.setString(index++,CostantiConfigurazione.CREDENZIALE_SSL.toString());
				}
//				if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
//					// like
//				}
				if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
					stmt.setString(index++, filterCredenzialeTokenPolicy);
				}
			}
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
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
			String aliasNomeServizioApplicativo="appnome";
			String aliasTipoSoggetto="orgtipo";
			String aliasNomeSoggetto="orgnome";
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			if (!search.equals("")) { // con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI,"id");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI,"nome",aliasNomeServizioApplicativo);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI,"id_soggetto");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"tipo_soggetto", aliasTipoSoggetto);
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"nome_soggetto", aliasNomeSoggetto);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else { 
						sqlQueryObject.addWhereCondition("tipo=?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?",
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
					}
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
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}
				if(joinConnettore) {
					DBUtils.setFiltriConnettoreApplicativo(sqlQueryObject, this.driver.tipoDB, 
							tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
							filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
				}
				if(filtroModI) {
					DBUtils.setFiltriModIApplicativi(sqlQueryObject, this.driver.tipoDB,
							filtroModISicurezzaMessaggioEnabled,
							filtroModIKeystorePath, filtroModIKeystoreSubject, filtroModIKeystoreIssuer,
							filtroModISicurezzaTokenEnabled,
							filtroModITokenPolicy, filtroModITokenClientId,
							filtroModIAudience,
							checkCredenzialiBase);
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaApplicativo(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(aliasNomeServizioApplicativo);
				sqlQueryObject.addOrderBy(aliasNomeSoggetto);
				sqlQueryObject.addOrderBy(aliasTipoSoggetto);
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI,"id");
				sqlQueryObject.addSelectAliasField(CostantiDB.SERVIZI_APPLICATIVI,"nome",aliasNomeServizioApplicativo);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI,"id_soggetto");
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"tipo_soggetto", aliasTipoSoggetto);
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,"nome_soggetto", aliasNomeSoggetto);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id_soggetto = "+CostantiDB.SOGGETTI+".id");
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".superuser = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".tipo_soggetto=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+".nome_soggetto=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else { 
						sqlQueryObject.addWhereCondition("tipo=?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?",
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
					}
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
				if(!existsConditions.isEmpty()) {
					sqlQueryObject.addWhereCondition(false, existsConditions.toArray(new String[1]));
				}
				if(joinConnettore) {
					DBUtils.setFiltriConnettoreApplicativo(sqlQueryObject, this.driver.tipoDB, 
							tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
							filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
				}
				if(filtroModI) {
					DBUtils.setFiltriModIApplicativi(sqlQueryObject, this.driver.tipoDB,
							filtroModISicurezzaMessaggioEnabled,
							filtroModIKeystorePath, filtroModIKeystoreSubject, filtroModIKeystoreIssuer,
							filtroModISicurezzaTokenEnabled,
							filtroModITokenPolicy, filtroModITokenClientId,
							filtroModIAudience,
							checkCredenzialiBase);
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaApplicativo(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(aliasNomeServizioApplicativo);
				sqlQueryObject.addOrderBy(aliasNomeSoggetto);
				sqlQueryObject.addOrderBy(aliasTipoSoggetto);
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			this.driver.logDebug("Execute query: "+queryString);
			index = 1;
			if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser))) {
				stmt.setString(index++, superuser);
			}
			if(tipologiaFruizione!=null) {
				stmt.setString(index++, tipologiaFruizione.getValue());
			}
			else if(tipologiaErogazione!=null) {
				stmt.setString(index++, tipologiaErogazione.getValue());
			}
			if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
				stmt.setString(index++, filterSoggettoTipo);
				stmt.setString(index++, filterSoggettoNome);
			}
			if(filterRuolo!=null && !"".equals(filterRuolo)) {
				stmt.setString(index++, filterRuolo);
			}
			if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
				if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
					stmt.setString(index++, CostantiConfigurazione.SERVER);
					stmt.setString(index++, CostantiConfigurazione.CLIENT);
				} 
				else {
					stmt.setString(index++, filterTipoServizioApplicativo);
					if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						stmt.setInt(index++, CostantiDB.TRUE);
					}
				}
			}
			if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
				stmt.setString(index++, filterTipoCredenziali);
				if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
					stmt.setString(index++,CostantiConfigurazione.CREDENZIALE_SSL.toString());
				}
//				if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
//					// like
//				}
				if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
					stmt.setString(index++, filterCredenzialeTokenPolicy);
				}
			}
			if(pddTipologia!=null) {
				stmt.setString(index++, pddTipologia.toString());
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

			while (risultato.next()) {
				ServizioApplicativo sa = this.serviziApplicativiDriver.getServizioApplicativo(risultato.getLong("id"));
				silList.add(sa);
			}

			return silList;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}

	protected List<ServizioApplicativo> soggettiServizioApplicativoList(Long idSoggetto, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "soggettiServizioApplicativoList";
		int idLista = Liste.SERVIZI_APPLICATIVI_BY_SOGGETTO;
		int offset;
		int limit;
		String search;
		String queryString;

		limit = ricerca.getPageSize(idLista);
		offset = ricerca.getIndexIniziale(idLista);
		search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));

		List<String> tipoSoggettiProtocollo = null;
		String filterProtocollo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLO);
		String filterProtocolli = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_PROTOCOLLI);
		if(idSoggetto==null || idSoggetto <=0) {
			try {
				tipoSoggettiProtocollo = Filtri.convertToTipiSoggetti(filterProtocollo, filterProtocolli);
			}catch(Exception e) {
				throw new DriverConfigurazioneException(e.getMessage(),e);
			}
		}
		
		String filterRuoloServizioApplicativo = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_RUOLO_SERVIZIO_APPLICATIVO);
		TipologiaFruizione tipologiaFruizione = null;
		TipologiaErogazione tipologiaErogazione = null;
		if(filterRuoloServizioApplicativo!=null && !"".equals(filterRuoloServizioApplicativo)) {
			if(Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_EROGATORE.equals(filterRuoloServizioApplicativo)) {
				tipologiaErogazione = TipologiaErogazione.DISABILITATO;
			}
			else if(Filtri.VALUE_FILTRO_RUOLO_SERVIZIO_APPLICATIVO_FRUITORE.equals(filterRuoloServizioApplicativo)) {
				tipologiaFruizione = TipologiaFruizione.DISABILITATO;
			}
		}
		
		String filterTipoServizioApplicativo = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_SERVIZIO_APPLICATIVO);
		
		String filterTipoCredenziali = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_TIPO_CREDENZIALI);
		String filterCredenziale = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_CREDENZIALE);
		String filterCredenzialeIssuer = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_CREDENZIALE_ISSUER);
		if(filterCredenzialeIssuer!=null && "".equals(filterCredenzialeIssuer)) {
			filterCredenzialeIssuer = null;
		}
		if(filterCredenzialeIssuer!=null && !"ssl".equals(filterTipoCredenziali)) {
			filterCredenzialeIssuer = null;
		}
		String filterCredenzialeTokenPolicy = SearchUtils.getFilter(ricerca, idLista,  Filtri.FILTRO_CREDENZIALE_TOKEN_POLICY);
		
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
		
		String filtroModISicurezzaMessaggio = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_MESSAGGIO);
		String filtroModIKeystorePath = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_KEYSTORE_PATH);
		String filtroModIKeystoreSubject = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_KEYSTORE_SUBJECT);
		String filtroModIKeystoreIssuer = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_KEYSTORE_ISSUER);
		String filtroModISicurezzaToken = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_TOKEN);
		String filtroModITokenPolicy = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_TOKEN_POLICY);
		String filtroModITokenClientId = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_SICUREZZA_TOKEN_CLIENT_ID);
		String filtroModIAudience = SearchUtils.getFilter(ricerca, idLista, Filtri.FILTRO_MODI_AUDIENCE);
		Boolean filtroModISicurezzaMessaggioEnabled = null;
		if(CostantiDB.STATO_FUNZIONALITA_ABILITATO.equals(filtroModISicurezzaMessaggio)) {
			filtroModISicurezzaMessaggioEnabled = true;
		}
		else if(CostantiDB.STATO_FUNZIONALITA_DISABILITATO.equals(filtroModISicurezzaMessaggio)) {
			filtroModISicurezzaMessaggioEnabled = false;
		}
		if(filtroModISicurezzaMessaggioEnabled!=null && filtroModISicurezzaMessaggioEnabled) {
			if((filtroModIKeystorePath!=null && "".equals(filtroModIKeystorePath))) {
				filtroModIKeystorePath=null;
			}
			if((filtroModIKeystoreSubject!=null && "".equals(filtroModIKeystoreSubject))) {
				filtroModIKeystoreSubject=null;
			}
			if((filtroModIKeystoreIssuer!=null && "".equals(filtroModIKeystoreIssuer))) {
				filtroModIKeystoreIssuer=null;
			}
		}
		else {
			filtroModIKeystorePath=null;
			filtroModIKeystoreSubject=null;
			filtroModIKeystoreIssuer=null;
		}
		Boolean filtroModISicurezzaTokenEnabled = null;
		if(CostantiDB.STATO_FUNZIONALITA_ABILITATO.equals(filtroModISicurezzaToken)) {
			filtroModISicurezzaTokenEnabled = true;
		}
		else if(CostantiDB.STATO_FUNZIONALITA_DISABILITATO.equals(filtroModISicurezzaToken)) {
			filtroModISicurezzaTokenEnabled = false;
		}
		if(filtroModISicurezzaTokenEnabled!=null && filtroModISicurezzaTokenEnabled) {
			if((filtroModITokenPolicy!=null && "".equals(filtroModITokenPolicy))) {
				filtroModITokenPolicy=null;
			}
			if((filtroModITokenClientId!=null && "".equals(filtroModITokenClientId))) {
				filtroModITokenClientId=null;
			}
		}
		else {
			filtroModITokenPolicy=null;
			filtroModITokenClientId=null;
		}
		if((filtroModIAudience!=null && "".equals(filtroModIAudience))) {
			filtroModIAudience=null;
		}
		boolean filtroModI = filtroModISicurezzaMessaggioEnabled!=null || 
				filtroModIKeystorePath!=null || filtroModIKeystoreSubject!=null || filtroModIKeystoreIssuer!=null || 
				filtroModISicurezzaTokenEnabled!=null || 
				filtroModITokenPolicy!=null || filtroModITokenClientId!=null || 
				filtroModIAudience!=null;
		
		boolean checkCredenzialiBase = false;
		if( (filterTipoCredenziali==null || "".equals(filterTipoCredenziali)) 
				&&
				(filterCredenziale==null || "".equals(filterCredenziale))
				&&
				(filterCredenzialeIssuer==null || "".equals(filterCredenzialeIssuer))
			) {
			checkCredenzialiBase = true;
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
		this.driver.logDebug("filterRuoloServizioApplicativo : " + filterRuoloServizioApplicativo);
		this.driver.logDebug("filterTipoServizioApplicativo : " + filterTipoServizioApplicativo);
		this.driver.logDebug("filterTipoCredenziali : " + filterTipoCredenziali);
		this.driver.logDebug("filterCredenziale : " + filterCredenziale);
		this.driver.logDebug("filterCredenzialeIssuer : " + filterCredenzialeIssuer);
		this.driver.logDebug("filterCredenzialeTokenPolicy : " + filterCredenzialeTokenPolicy);
		this.driver.logDebug("filtroConnettoreTipo : " + filtroConnettoreTipo);
		this.driver.logDebug("filtroConnettoreTokenPolicy : " + filtroConnettoreTokenPolicy);
		this.driver.logDebug("filtroConnettoreEndpoint : " + filtroConnettoreEndpoint);
		this.driver.logDebug("filtroConnettoreKeystore : " + filtroConnettoreKeystore);
		this.driver.logDebug("filtroConnettoreDebug : " + filtroConnettoreDebug);
		this.driver.logDebug("filtroConnettoreTipoPlugin : " + filtroConnettoreTipoPlugin);
		this.driver.logDebug("filtroModISicurezzaMessaggio : " + filtroModISicurezzaMessaggio);
		this.driver.logDebug("filtroModIKeystorePath : " + filtroModIKeystorePath);
		this.driver.logDebug("filtroModIKeystoreSubject : " + filtroModIKeystoreSubject);
		this.driver.logDebug("filtroModIKeystoreIssuer : " + filtroModIKeystoreIssuer);
		this.driver.logDebug("filtroModISicurezzaToken : " + filtroModISicurezzaToken);
		this.driver.logDebug("filtroModITokenPolicy : " + filtroModITokenPolicy);
		this.driver.logDebug("filtroModITokenClientId : " + filtroModITokenClientId);
		this.driver.logDebug("filtroModIAudience : " + filtroModIAudience);
		this.driver.logDebug("filtroProprietaNome : " + filtroProprietaNome);
		this.driver.logDebug("filtroProprietaValore : " + filtroProprietaValore);
		
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		ArrayList<ServizioApplicativo> silList = new ArrayList<ServizioApplicativo>();

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

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?",
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali)  || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
					}
				}
				if(joinConnettore) {
					DBUtils.setFiltriConnettoreApplicativo(sqlQueryObject, this.driver.tipoDB, 
							tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
							filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
				}
				if(filtroModI) {
					DBUtils.setFiltriModIApplicativi(sqlQueryObject, this.driver.tipoDB,
							filtroModISicurezzaMessaggioEnabled,
							filtroModIKeystorePath, filtroModIKeystoreSubject, filtroModIKeystoreIssuer,
							filtroModISicurezzaTokenEnabled,
							filtroModITokenPolicy, filtroModITokenClientId,
							filtroModIAudience,
							checkCredenzialiBase);
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaApplicativo(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?",
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
					}
				}
				if(joinConnettore) {
					DBUtils.setFiltriConnettoreApplicativo(sqlQueryObject, this.driver.tipoDB, 
							tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
							filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
				}
				if(filtroModI) {
					DBUtils.setFiltriModIApplicativi(sqlQueryObject, this.driver.tipoDB,
							filtroModISicurezzaMessaggioEnabled,
							filtroModIKeystorePath, filtroModIKeystoreSubject, filtroModIKeystoreIssuer,
							filtroModISicurezzaTokenEnabled,
							filtroModITokenPolicy, filtroModITokenClientId,
							filtroModIAudience,
							checkCredenzialiBase);
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaApplicativo(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if (idSoggetto!=null) {
				stmt.setLong(index++, idSoggetto);
			}
			if(tipologiaFruizione!=null) {
				stmt.setString(index++, tipologiaFruizione.getValue());
			}
			else if(tipologiaErogazione!=null) {
				stmt.setString(index++, tipologiaErogazione.getValue());
			}
			if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
				if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
					stmt.setString(index++, CostantiConfigurazione.SERVER);
					stmt.setString(index++, CostantiConfigurazione.CLIENT);
				} 
				else {
					stmt.setString(index++, filterTipoServizioApplicativo);
					if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						stmt.setInt(index++, CostantiDB.TRUE);
					}
				}
			}
			if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
				stmt.setString(index++, filterTipoCredenziali);
				if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
					stmt.setString(index++,CostantiConfigurazione.CREDENZIALE_SSL.toString());
				}
//				if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
//					// like
//				}
				if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
					stmt.setString(index++, filterCredenzialeTokenPolicy);
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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_soggetto");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".nome", search, true, true);
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addSelectField("nome_soggetto");
					sqlQueryObject.addSelectField("tipo_soggetto");
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addOrderBy("nome_soggetto");
					sqlQueryObject.addOrderBy("tipo_soggetto");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?",
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
					}
				}
				if(joinConnettore) {
					DBUtils.setFiltriConnettoreApplicativo(sqlQueryObject, this.driver.tipoDB, 
							tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
							filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
				}
				if(filtroModI) {
					DBUtils.setFiltriModIApplicativi(sqlQueryObject, this.driver.tipoDB,
							filtroModISicurezzaMessaggioEnabled,
							filtroModIKeystorePath, filtroModIKeystoreSubject, filtroModIKeystoreIssuer,
							filtroModISicurezzaTokenEnabled,
							filtroModITokenPolicy, filtroModITokenClientId,
							filtroModIAudience,
							checkCredenzialiBase);
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaApplicativo(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField("id_soggetto");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition("id_soggetto = ?");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addSelectField("nome_soggetto");
					sqlQueryObject.addSelectField("tipo_soggetto");
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition("id_soggetto = "+CostantiDB.SOGGETTI+".id");
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+".tipo_soggetto", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_fruizione is not null", "tipologia_fruizione<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, "tipologia_erogazione is not null", "tipologia_erogazione<>?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				if(tipoSoggettiProtocollo!=null && tipoSoggettiProtocollo.size()>0) {
					sqlQueryObject.addOrderBy("nome_soggetto");
					sqlQueryObject.addOrderBy("tipo_soggetto");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo =?", CostantiDB.SERVIZI_APPLICATIVI+".tipo=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?", CostantiDB.SERVIZI_APPLICATIVI+".as_client = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipo = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?",
								CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ? AND "+CostantiDB.SERVIZI_APPLICATIVI+".token_policy IS NOT NULL AND "+CostantiDB.SERVIZI_APPLICATIVI+".utente IS NOT NULL");
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".tipoauth = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_subject", filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".subject", filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".utente", 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".cn_issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+".issuer", filterCredenzialeIssuer, 
											LikeConfig.contains(true,true)));
						}
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".token_policy = ?");
					}
				}
				if(joinConnettore) {
					DBUtils.setFiltriConnettoreApplicativo(sqlQueryObject, this.driver.tipoDB, 
							tipoConnettore, endpointType, tipoConnettoreIntegrationManager, 
							filtroConnettoreTokenPolicy, filtroConnettoreEndpoint, filtroConnettoreKeystore, filtroConnettoreDebug);
				}
				if(filtroModI) {
					DBUtils.setFiltriModIApplicativi(sqlQueryObject, this.driver.tipoDB,
							filtroModISicurezzaMessaggioEnabled,
							filtroModIKeystorePath, filtroModIKeystoreSubject, filtroModIKeystoreIssuer,
							filtroModISicurezzaTokenEnabled,
							filtroModITokenPolicy, filtroModITokenClientId,
							filtroModIAudience,
							checkCredenzialiBase);
				}
				if(filtroProprieta) {
					DBUtils.setFiltriProprietaApplicativo(sqlQueryObject, this.driver.tipoDB, 
							filtroProprietaNome, filtroProprietaValore);
				}
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			index = 1;
			if (idSoggetto!=null) {
				stmt.setLong(index++, idSoggetto);
			}
			if(tipologiaFruizione!=null) {
				stmt.setString(index++, tipologiaFruizione.getValue());
			}
			else if(tipologiaErogazione!=null) {
				stmt.setString(index++, tipologiaErogazione.getValue());
			}
			if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
				if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
					stmt.setString(index++, CostantiConfigurazione.SERVER);
					stmt.setString(index++, CostantiConfigurazione.CLIENT);
				} 
				else {
					stmt.setString(index++, filterTipoServizioApplicativo);
					if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						stmt.setInt(index++, CostantiDB.TRUE);
					}
				}
			}
			if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
				stmt.setString(index++, filterTipoCredenziali);
				if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
					stmt.setString(index++,CostantiConfigurazione.CREDENZIALE_SSL.toString());
				}
//				if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
//					// like
//				}
				if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
					stmt.setString(index++, filterCredenzialeTokenPolicy);
				}
			}
			risultato = stmt.executeQuery();

			while (risultato.next()) {
				ServizioApplicativo sa = this.serviziApplicativiDriver.getServizioApplicativo(risultato.getLong("id"));
				silList.add(sa);
			}

			return silList;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
	}
	
	protected List<String> servizioApplicativoRuoliList(long idSA, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "servizioApplicativoRuoliList";
		int idLista = Liste.SERVIZIO_APPLICATIVO_RUOLI;
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
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<String> listIdRuoli = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSA);

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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".id_servizio_applicativo");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+".ruolo");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSA);

			risultato = stmt.executeQuery();

			listIdRuoli = new ArrayList<>();
			while (risultato.next()) {

				listIdRuoli.add(risultato.getString(1));

			}

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return listIdRuoli;
	}
	
	protected List<Proprieta> serviziApplicativiProprietaList(int idSA, ISearch ricerca) throws DriverConfigurazioneException {
		String nomeMetodo = "serviziApplicativiProprietaList";
		int idLista = Liste.SERVIZI_APPLICATIVI_PROP;
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
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Exception accedendo al datasource :" + e.getMessage(),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug("operazione this.driver.atomica = " + this.driver.atomica);

		List<Proprieta> lista = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_PROPS+".id_servizio_applicativo");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".nome", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_PROPS+".id_servizio_applicativo");

				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSA);

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
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".nome");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".valore");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_PROPS+".id_servizio_applicativo");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".nome", search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".nome");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".valore");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".id");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+".id="+CostantiDB.SERVIZI_APPLICATIVI_PROPS+".id_servizio_applicativo");
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".nome");
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			}
			stmt = con.prepareStatement(queryString);
			stmt.setLong(1, idSA);

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
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {

			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);

			this.driver.closeConnection(error,con);
		}
		
		return lista;
	}
	
	
	// all
	protected int countTipologieServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, false, filters, false, null, null);
	}
	protected int countTipologieServiziApplicativi(IDSoggetto proprietario,ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, false, filters, false, null, null);
	}
	
	// fruizione
	
	protected int countTipologieFruizioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, true, filters, false, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, true, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, null, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, fruizione, true, filters, false, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, false, fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, true, filters, false, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, true, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, null, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, fruizione, true, filters, false, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, false, fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	// erogazione
	
	protected int countTipologieErogazioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, true, null, false, filters, false, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	protected int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, erogazione, true, null, false, filters, false, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, erogazione, true, null, false,  filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(null, erogazione, true, null, false,  filters, checkAssociazionePorta, null, isBound);
	}
	
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, erogazione, true,  null, false, filters, false, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, true, null, false, filters, false, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engineCount(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	// engine
	
	private int serviziApplicativiList_engineCount(IDSoggetto proprietario, 
			RicercaTipologiaErogazione erogazione, boolean searchSoloErogazione,  
			RicercaTipologiaFruizione fruizione, boolean serchSoloFruizione, 
			ISearch filters,boolean checkAssociazionePorta,
			Boolean fruizioneIsBound, Boolean erogazioneIsBound) throws DriverConfigurazioneException {
	
		String nomeMetodo = "serviziApplicativiList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		Long idProprietario = null;
	
		int idLista = Liste.SERVIZIO_APPLICATIVO;
		String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filters.getSearchString(idLista)) ? "" : filters.getSearchString(idLista));
	
	
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
	
			if(proprietario!=null){
				idProprietario=DBUtils.getIdSoggetto(proprietario.getNome(), proprietario.getTipo(), con, this.driver.tipoDB);
				if(idProprietario==null || idProprietario<0) 
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Il proprietario ["+proprietario.toString()+"] non esiste.");
			}
	
	
			// *** count ***
			ISQLQueryObject sqlQueryObject = this.getServiziApplicativiSearchFiltratiTipologia(proprietario, 
					erogazione, searchSoloErogazione,
					fruizione, 	serchSoloFruizione,  
					filters, checkAssociazionePorta, search, true,
					fruizioneIsBound, erogazioneIsBound);
			String queryString = sqlQueryObject.createSQLQuery();
			this.driver.logDebug("QueryCount=["+queryString+"]");
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(proprietario!=null){
				stmt.setLong(index++, idProprietario);
			}
			if(serchSoloFruizione){
				//se fruizione non specificata voglio tutti i tipi di fruizione
				if(fruizione==null){
					stmt.setString(index++, TipologiaFruizione.DISABILITATO.toString());
				}else{
					if(!RicercaTipologiaFruizione.ALL.toString().equals(fruizione.toString())){
						stmt.setString(index++,fruizione.toString());
					}
				}
			}else if(searchSoloErogazione){
				//se erogazione non specificato voglio tutti i tipi di erogazione
				if(erogazione==null){
					stmt.setString(index++,TipologiaErogazione.DISABILITATO.toString());
				}else{
					if(!RicercaTipologiaErogazione.ALL.toString().equals(erogazione.toString())){
						stmt.setString(index++,erogazione.toString());
					}
				}
			}
			
			if(fruizioneIsBound!=null){
				stmt.setString(index++,TipoAutenticazione.DISABILITATO.getValue());
			}
			
			if(erogazioneIsBound!=null){
				stmt.setString(index++,TipiConnettore.DISABILITATO.getNome());
				stmt.setString(index++,TipiConnettore.HTTP.getNome());
				stmt.setString(index++,TipiConnettore.HTTP.getNome());
				// stmt.setString(index++,"");  OP-708
			}
			
			risultato = stmt.executeQuery();
			int count = -1;
			if (risultato.next()){
				count =  risultato.getInt("count");
			}
			risultato.close();
			stmt.close();
	
			return count;
	
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Errore : " + qe.getMessage(),qe);
		} finally {
	
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
	
			this.driver.closeConnection(error,con);
		}
	}
	
	
	
	
	
	// all
	protected List<TipologiaServizioApplicativo> getTipologieServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  null, false, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieServiziApplicativi(IDSoggetto proprietario,ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false,  null, false, filters, false, null, null);
	}
	
	// fruizione
	
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false, null, true, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  null, true, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  null, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false, fruizione, true, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, false,  fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, null, true, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, null, true, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, null, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false, fruizione, true, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false,  fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, false,  fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	// erogazione
	
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, true, null, false, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, erogazione, true, null, false, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, erogazione, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(null, erogazione, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, erogazione, true, null, false, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, true, null, false, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiList_engine(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	// engine
	
	private List<TipologiaServizioApplicativo> serviziApplicativiList_engine(IDSoggetto proprietario, 
			RicercaTipologiaErogazione erogazione, boolean searchSoloErogazione,  
			RicercaTipologiaFruizione fruizione, boolean serchSoloFruizione, 
			ISearch filters,boolean checkAssociazionePorta,
			Boolean fruizioneIsBound, Boolean erogazioneIsBound) throws DriverConfigurazioneException {
	
		String nomeMetodo = "serviziApplicativiList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		Long idProprietario = null;
	
		int idLista = Liste.SERVIZIO_APPLICATIVO;
		int limit = filters.getPageSize(idLista);
		int offset = filters.getIndexIniziale(idLista);
		String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filters.getSearchString(idLista)) ? "" : filters.getSearchString(idLista));
	
		ArrayList<TipologiaServizioApplicativo> lista = new ArrayList<TipologiaServizioApplicativo>();
	
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
	
			if(proprietario!=null){
				idProprietario=DBUtils.getIdSoggetto(proprietario.getNome(), proprietario.getTipo(), con, this.driver.tipoDB);
				if(idProprietario==null || idProprietario<0) 
					throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Il proprietario ["+proprietario.toString()+"] non esiste.");
			}
			filters.setNumEntries(idLista, this.serviziApplicativiList_engineCount(proprietario, 
					erogazione, searchSoloErogazione, 
					fruizione, serchSoloFruizione, 
					filters, checkAssociazionePorta,
					fruizioneIsBound, erogazioneIsBound));
	
	
	
			// *** select ***
			ISQLQueryObject sqlQueryObject = this.getServiziApplicativiSearchFiltratiTipologia(proprietario, 
					erogazione, searchSoloErogazione,
					fruizione, serchSoloFruizione,  
					filters, checkAssociazionePorta, search, false,
					fruizioneIsBound, erogazioneIsBound);
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
	
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setOffset(offset);
			sqlQueryObject.setLimit(limit);
			String queryString = sqlQueryObject.createSQLQuery();
			this.driver.logDebug("Query=["+queryString+"]");
			stmt = con.prepareStatement(queryString);
			int index = 1;
			if(proprietario!=null)
				stmt.setLong(index++, idProprietario);
			if(serchSoloFruizione){
				//se fruizione non specificata voglio tutti i tipi di fruizione
				if(fruizione==null){
					stmt.setString(index++, TipologiaFruizione.DISABILITATO.toString());
				}else{
					if(!RicercaTipologiaFruizione.ALL.toString().equals(fruizione.toString())){
						stmt.setString(index++,fruizione.toString());
					}
				}
			}else if(searchSoloErogazione){
				//se erogazione non specificato voglio tutti i tipi di erogazione
				if(erogazione==null){
					stmt.setString(index++,TipologiaErogazione.DISABILITATO.toString());
				}else{
					if(!RicercaTipologiaErogazione.ALL.toString().equals(erogazione.toString())){
						stmt.setString(index++,erogazione.toString());
					}
				}
			}
			
			if(fruizioneIsBound!=null){
				stmt.setString(index++,TipoAutenticazione.DISABILITATO.getValue());
			}
			
			if(erogazioneIsBound!=null){
				stmt.setString(index++,TipiConnettore.DISABILITATO.getNome());
				stmt.setString(index++,TipiConnettore.HTTP.getNome());
				stmt.setString(index++,TipiConnettore.HTTP.getNome());
				// stmt.setString(index++,""); OP-708
			}
			
			risultato = stmt.executeQuery();
			while (risultato.next()){
	
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				if(proprietario!=null){
					idSA.setIdSoggettoProprietario(proprietario);
				}else{
					long idProprietarioLetto = risultato.getLong("id_soggetto");
					Soggetto s = this.soggettiDriver.getSoggetto(idProprietarioLetto);
					idSA.setIdSoggettoProprietario(new IDSoggetto(s.getTipo(), s.getNome()));
				}
				idSA.setNome(risultato.getString("nome"));
	
				TipologiaServizioApplicativo tipologia = new TipologiaServizioApplicativo();
				tipologia.setIdServizioApplicativo(idSA);
	
				String tipologiaFruizione = risultato.getString("tipologia_fruizione");
				if(tipologiaFruizione!=null && !"".equals(tipologiaFruizione)){
					tipologia.setTipologiaFruizione(TipologiaFruizione.toEnumConstant(tipologiaFruizione));
				}
				String tipologiaErogazione = risultato.getString("tipologia_erogazione");
				if(tipologiaErogazione!=null && !"".equals(tipologiaErogazione)){
					tipologia.setTipologiaErogazione(TipologiaErogazione.toEnumConstant(tipologiaErogazione));
				}
	
				lista.add(tipologia);
			}
			risultato.close();
			stmt.close();
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
	
	private ISQLQueryObject getServiziApplicativiSearchFiltratiTipologia(IDSoggetto proprietario, 
			RicercaTipologiaErogazione erogazione, boolean searchSoloErogazione,  
			RicercaTipologiaFruizione fruizione, boolean serchSoloFruizione, 
			ISearch filters,boolean checkAssociazionePorta,String search, boolean count,
			Boolean fruizioneIsBound, Boolean erogazioneIsBound) throws Exception{
	
		ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
		sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
		if(count){
			sqlQueryObject.addSelectCountField("*","count");
		}
		else{
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addSelectField("tipologia_fruizione");
			sqlQueryObject.addSelectField("tipologia_erogazione");
		}
	
		if(proprietario!=null){
			sqlQueryObject.addWhereCondition("id_soggetto=?");
		}
		if(!"".equals(search)){
			sqlQueryObject.addWhereLikeCondition("nome", search,false,true,true); // escape a false, deve essere pilotato da chi lo usa!
		}
	
		if(serchSoloFruizione){
			//se fruizione non specificata voglio tutti i tipi di fruizione che non siano disabilitati
			if(fruizione==null){
				sqlQueryObject.addWhereCondition(true,"tipologia_fruizione is not null","tipologia_fruizione <> ?");
			}else{
				if(RicercaTipologiaFruizione.DISABILITATO.toString().equals(fruizione.toString())){
					sqlQueryObject.addWhereCondition(false,"tipologia_fruizione is null","tipologia_fruizione = ?");
				}
				else if(RicercaTipologiaFruizione.ALL.toString().equals(fruizione.toString())){
					// Non devo fare niente (l'associazione porta seguente fara' si che verranno tornati solo i sa associati a PD)
				}
				else{
					sqlQueryObject.addWhereCondition("tipologia_fruizione = ?");
				}
			}
			if(checkAssociazionePorta){
				
				ISQLQueryObject sqlQueryObjectIn = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				
				ISQLQueryObject sqlQueryObjectTrasportoIn = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectTrasportoIn.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
				sqlQueryObjectTrasportoIn.addSelectField("id_servizio_applicativo");
				sqlQueryObjectTrasportoIn.addWhereCondition("id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObjectIn.addWhereExistsCondition(false, sqlQueryObjectTrasportoIn);
			
				ISQLQueryObject sqlQueryObjectTokenIn = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectTokenIn.addFromTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
				sqlQueryObjectTokenIn.addSelectField("id_servizio_applicativo");
				sqlQueryObjectTokenIn.addWhereCondition("id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObjectIn.addWhereExistsCondition(false, sqlQueryObjectTokenIn);
				
				sqlQueryObjectIn.setANDLogicOperator(false);
				
				sqlQueryObjectIn.addWhereCondition(sqlQueryObjectIn.createSQLConditions());
			}
		}
	
		else if(searchSoloErogazione){
			//se erogazione non specificato voglio tutti i tipi di erogazione
			if(erogazione==null){
				sqlQueryObject.addWhereCondition(true,"tipologia_erogazione is not null","tipologia_erogazione <> ?");
			}else{
				if(RicercaTipologiaErogazione.DISABILITATO.toString().equals(erogazione.toString())){
					sqlQueryObject.addWhereCondition(false,"tipologia_erogazione is null","tipologia_erogazione = ?");
				}
				else if(RicercaTipologiaErogazione.ALL.toString().equals(erogazione.toString())){
					// Non devo fare niente (l'associazione porta seguente fara' si che verranno tornati solo i sa associati a PD)
				}
				else{
					sqlQueryObject.addWhereCondition("tipologia_erogazione = ?");
				}
			}
			if(checkAssociazionePorta){
				ISQLQueryObject sqlQueryObjectIn = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectIn.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
				sqlQueryObjectIn.addSelectField("id_servizio_applicativo");
				sqlQueryObjectIn.addWhereCondition("id_servizio_applicativo="+CostantiDB.SERVIZI_APPLICATIVI+".id");
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectIn);
			}
		}
		
		if(fruizioneIsBound!=null){
			if(fruizioneIsBound){
				sqlQueryObject.addWhereIsNotNullCondition("tipoauth");
				sqlQueryObject.addWhereCondition("tipoauth<>?");
			}
			else{
				sqlQueryObject.addWhereCondition(false,"tipoauth is null","tipoauth=?");
			}
		}
		
		if(erogazioneIsBound!=null){
			
			ISQLQueryObject sqlQueryObjectConnettore = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectConnettore.setANDLogicOperator(true);
			sqlQueryObjectConnettore.addFromTable(CostantiDB.CONNETTORI);
			sqlQueryObjectConnettore.addSelectField("id");
			sqlQueryObjectConnettore.addWhereCondition("id_connettore_inv="+CostantiDB.CONNETTORI+".id");
			
			// Fix OP-708
			ISQLQueryObject sqlQueryObject_NullCondition = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject_NullCondition.addWhereIsNotNullCondition("url");
			ISQLQueryObject sqlQueryObject_EmptyCondition = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject_EmptyCondition.addWhereIsNotEmptyCondition("url");
			
			sqlQueryObjectConnettore.addWhereCondition(false,
					"endpointtype<>? AND endpointtype<>?",
					//"endpointtype=? AND url is not null AND url <> ?"); OP-708
					"endpointtype=? AND "+sqlQueryObject_NullCondition.createSQLConditions()+" AND "+sqlQueryObject_EmptyCondition.createSQLConditions());
			
			if(erogazioneIsBound){
				sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectConnettore);
			}
			else{
				sqlQueryObject.addWhereExistsCondition(true, sqlQueryObjectConnettore);
			}
		}
		
		sqlQueryObject.setANDLogicOperator(true);
	
		return sqlQueryObject;
	}
	
	/**
	 * Recupera i servizi applicativi che hanno come tipologia di erogazione una tra quelle passate come parametro
	 * @param filters
	 * @param proprietario
	 * @return List<IDServizioApplicativo>
	 * @throws DriverConfigurazioneException
	 */
	protected List<IDServizioApplicativo> serviziApplicativiList(ISearch filters, IDSoggetto proprietario, TipologiaErogazione... erogazione) throws DriverConfigurazioneException {
		String nomeMetodo = "serviziApplicativiList";
		Connection con = null;
		PreparedStatement stmt=null;
		ResultSet risultato=null;
		boolean error = false;
		long idProprietario = -1;
	
		int idLista = Liste.SERVIZIO_APPLICATIVO;
		int limit = filters.getPageSize(idLista);
		int offset = filters.getIndexIniziale(idLista);
		String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(filters.getSearchString(idLista)) ? "" : filters.getSearchString(idLista));
	
		ArrayList<IDServizioApplicativo> lista = new ArrayList<IDServizioApplicativo>();
	
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
	
			idProprietario=DBUtils.getIdSoggetto(proprietario.getNome(), proprietario.getTipo(), con, this.driver.tipoDB);
			if(idProprietario<0) 
				throw new DriverConfigurazioneException("[DriverConfigurazioneDB::" + nomeMetodo + "] Il proprietario ["+proprietario.toString()+"] non esiste.");
	
			//count
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectCountField("*","count");
			sqlQueryObject.addWhereCondition("id_soggetto=?");
	
			if(!"".equals(search)){
				sqlQueryObject.addWhereLikeCondition("nome", search,true,true);
			}
	
			//se erogazione non specificato voglio tutti i tipi di erogazione
			String[] conditions = new String[erogazione.length];
			for (int i = 0; i < erogazione.length; i++) {
				conditions[i] = "tipologia_erogazione = ?";
			}
			sqlQueryObject.addWhereCondition(false,conditions);
	
			sqlQueryObject.setANDLogicOperator(true);
	
	
			String queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
	
			stmt.setLong(1, idProprietario);
	
			int baseIdx = 1;
			for (int i = 0; i < erogazione.length; i++) {
				stmt.setString(++baseIdx, erogazione[i].toString());
			}
	
			risultato = stmt.executeQuery();
	
			if (risultato.next()){
				filters.setNumEntries(idLista, risultato.getInt("count"));
			}
			risultato.close();
			stmt.close();
	
			//select
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField("id_soggetto");
			sqlQueryObject.addSelectField("tipologia_fruizione");
			sqlQueryObject.addSelectField("tipologia_erogazione");
	
			sqlQueryObject.addWhereCondition("id_soggetto=?");
	
			if(!"".equals(search)){
				sqlQueryObject.addWhereLikeCondition("nome", search,true,true);
			}
	
			sqlQueryObject.addWhereCondition(false,conditions);
	
			sqlQueryObject.setANDLogicOperator(true);
			// ricavo le entries
			if (limit == 0) // con limit
				limit = ISQLQueryObject.LIMIT_DEFAULT_VALUE;
	
			sqlQueryObject.addOrderBy("nome");
			sqlQueryObject.setSortType(true);
			sqlQueryObject.setOffset(offset);
			sqlQueryObject.setLimit(limit);
	
			queryString = sqlQueryObject.createSQLQuery();
			stmt = con.prepareStatement(queryString);
	
			stmt.setLong(1, idProprietario);
	
			baseIdx = 1;
			for (int i = 0; i < erogazione.length; i++) {
				stmt.setString(++baseIdx, erogazione[i].toString());
			}
			risultato = stmt.executeQuery();
	
			while (risultato.next()){
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setIdSoggettoProprietario(proprietario);
				idSA.setNome(risultato.getString("nome"));				
				lista.add(idSA);
			}
			risultato.close();
			stmt.close();
	
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
}
