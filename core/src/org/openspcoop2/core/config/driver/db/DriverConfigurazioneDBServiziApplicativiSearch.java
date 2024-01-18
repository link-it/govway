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
public class DriverConfigurazioneDBServiziApplicativiSearch {

	private DriverConfigurazioneDB driver = null;
	private DriverConfigurazioneDBSoggetti soggettiDriver = null;
	private DriverConfigurazioneDB_serviziApplicativiDriver serviziApplicativiDriver = null;
	
	protected DriverConfigurazioneDBServiziApplicativiSearch(DriverConfigurazioneDB driver) {
		this.driver = driver;
		this.soggettiDriver = new DriverConfigurazioneDBSoggetti(driver);
		this.serviziApplicativiDriver = new DriverConfigurazioneDB_serviziApplicativiDriver(driver);
	}
	
	private static final String PREFIX_ATOMICA = "operazione this.driver.atomica = ";
	
	private String getPrefixError(String nomeMetodo) {
		return "[DriverConfigurazioneDB::" + nomeMetodo + "] "; 
	}
	private String getMessageError(String nomeMetodo, Exception e) {
		return getPrefixError(nomeMetodo)+"Exception: " + e.getMessage();
	}
	private String getMessageDatasourceError(String nomeMetodo, Exception e) {
		return getPrefixError(nomeMetodo)+"Exception accedendo al datasource :" + e.getMessage();
	}
	private String getMessageProprietarioNonEsiste(String nomeMetodo, IDSoggetto proprietario) {
		return getPrefixError(nomeMetodo)+"Il proprietario ["+proprietario.toString()+"] non esiste.";
	}
	
	private List<ServizioApplicativo> servizioApplicativoList(ISearch ricerca,Long idProprietario) throws DriverConfigurazioneException{
		String nomeMetodo = "servizioApplicativoList(search,idProprietario)";
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
		ArrayList<ServizioApplicativo> lista = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource("servizioApplicativoList");
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException(getMessageDatasourceError(nomeMetodo,  e),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(PREFIX_ATOMICA + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereLikeCondition("nome", search, true, true);

				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+"=?");

				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addSelectCountField("*", "cont");

				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+"=?");

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
				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+"=?");
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
				if(idProprietario!=null) sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+"=?");
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
				lista.add(sa);

			}

			return lista;

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException(getMessageError(nomeMetodo,  qe),qe);
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
				throw new DriverConfigurazioneException(getMessageDatasourceError(nomeMetodo,  e),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(PREFIX_ATOMICA + this.driver.atomica);

		try {

			return this.servizioApplicativoList(ricerca, DBUtils.getIdSoggetto(idSO.getNome(), idSO.getTipo(), con, this.driver.tipoDB));

		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException(getMessageError(nomeMetodo,  qe),qe);
		} finally {

			this.driver.closeConnection(error,con);
		}
	}
	
	private ISQLQueryObject buildSqlQueryObjectAutorizzazioniPorteDelegate(String nomeTabella,
			String aliasPDSA, String aliasPD, String aliasSERVIZI, String aliasACCORDI, String aliasACCORDIGRUPPI, String aliasGRUPPI, String aliasSOGGETTI,
			boolean isFilterGruppoFruizione, IDFruizione apiImplementazioneFruizione, String filterGruppo,
			List<Object> existsParameters) throws DriverConfigurazioneException {
		try {
			ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(nomeTabella,aliasPDSA);
			sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,aliasPD);
			if(isFilterGruppoFruizione) {
				sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
				sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
				sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
				sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
			}
			if(apiImplementazioneFruizione!=null) {
				sqlQueryObjectAutorizzazioniPorteDelegate.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
			}
			sqlQueryObjectAutorizzazioniPorteDelegate.addSelectAliasField(aliasPDSA, "id", aliasPDSA+"id");
			sqlQueryObjectAutorizzazioniPorteDelegate.setANDLogicOperator(true);
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPDSA+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF+" = "+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID);
			sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPDSA+"."+CostantiDB.PORTA_COLUMN_ID_REF+" = "+aliasPD+"."+CostantiDB.COLUMN_ID);
			if(isFilterGruppoFruizione) {
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_REF+" = "+aliasSERVIZI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasSERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_GRUPPO_REF+" = "+aliasGRUPPI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasGRUPPI+"."+CostantiDB.GRUPPI_COLUMN_NOME+" = ?");
				existsParameters.add(filterGruppo);
			}
			if(apiImplementazioneFruizione!=null) {
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_ID_SOGGETTO_REF+" = "+aliasSOGGETTI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+" = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+" = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_TIPO_SOGGETTO_EROGATORE+" = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_NOME_SOGGETTO_EROGATORE+" = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_TIPO_SERVIZIO+" = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_NOME_SERVIZIO+" = ?");
				sqlQueryObjectAutorizzazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_VERSIONE_SERVIZIO+" = ?");
				existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getTipo());
				existsParameters.add(apiImplementazioneFruizione.getIdFruitore().getNome());
				existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getTipo());
				existsParameters.add(apiImplementazioneFruizione.getIdServizio().getSoggettoErogatore().getNome());
				existsParameters.add(apiImplementazioneFruizione.getIdServizio().getTipo());
				existsParameters.add(apiImplementazioneFruizione.getIdServizio().getNome());
				existsParameters.add(apiImplementazioneFruizione.getIdServizio().getVersione());
			}
			return sqlQueryObjectAutorizzazioniPorteDelegate;
		} catch (Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
	}
	
	private ISQLQueryObject buildSqlQueryObjectAutorizzazioniPorteApplicative(String nomeTabella,
			String aliasPASA, String aliasPA, String aliasSERVIZI, String aliasACCORDI, String aliasACCORDIGRUPPI, String aliasGRUPPI, String aliasSOGGETTI,
			boolean isFilterGruppoErogazione, IDServizio apiImplementazioneErogazione, String filterGruppo,
			List<Object> existsParameters) throws DriverConfigurazioneException {
		try {
			ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(nomeTabella,aliasPASA);
			sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,aliasPA);
			if(isFilterGruppoErogazione) {
				sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
				sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
				sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
				sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
			}
			if(apiImplementazioneErogazione!=null) {
				sqlQueryObjectAutorizzazioniPorteApplicative.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
			}
			sqlQueryObjectAutorizzazioniPorteApplicative.addSelectAliasField(aliasPASA, "id", aliasPASA+"id");
			sqlQueryObjectAutorizzazioniPorteApplicative.setANDLogicOperator(true);
			sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPASA+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF+" = "+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID);
			sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPASA+"."+CostantiDB.PORTA_COLUMN_ID_REF+" = "+aliasPA+"."+CostantiDB.COLUMN_ID);
			if(isFilterGruppoErogazione) {
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_REF+" = "+aliasSERVIZI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasSERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_GRUPPO_REF+" = "+aliasGRUPPI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasGRUPPI+"."+CostantiDB.GRUPPI_COLUMN_NOME+" = ?");
				existsParameters.add(filterGruppo);
			}
			if(apiImplementazioneErogazione!=null) {
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_ID_SOGGETTO_REF+" = "+aliasSOGGETTI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+" = ?");
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+" = ?");
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_TIPO_SERVIZIO+" = ?");
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_SERVIZIO+" = ?");
				sqlQueryObjectAutorizzazioniPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_VERSIONE_SERVIZIO+" = ?");
				existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
				existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
				existsParameters.add(apiImplementazioneErogazione.getTipo());
				existsParameters.add(apiImplementazioneErogazione.getNome());
				existsParameters.add(apiImplementazioneErogazione.getVersione());
			}
			return sqlQueryObjectAutorizzazioniPorteApplicative;
		} catch (Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
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
		ArrayList<ServizioApplicativo> silList = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException(getMessageDatasourceError(nomeMetodo,  e),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(PREFIX_ATOMICA + this.driver.atomica);

		try {

			/* === Filtro Dominio === */

			ISQLQueryObject sqlQueryObjectPdd = null;
			if(pddTipologia!=null && PddTipologia.ESTERNO.equals(pddTipologia)) {
				ISQLQueryObject sqlQueryObjectExistsPdd = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectExistsPdd.addSelectField(CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_NOME);
				sqlQueryObjectExistsPdd.addFromTable(CostantiDB.PDD);
				sqlQueryObjectExistsPdd.setANDLogicOperator(true);
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_NOME+"="+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_SERVER);
				sqlQueryObjectExistsPdd.addWhereCondition(CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_TIPO+"=?");
				
				sqlQueryObjectPdd = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectPdd.setANDLogicOperator(false);
				sqlQueryObjectPdd.addWhereIsNullCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_SERVER);
				sqlQueryObjectPdd.addWhereExistsCondition(false, sqlQueryObjectExistsPdd);
			}	
			
			
			
			/* === Filtro Gruppo, Tipo Erogazione/Fruizione, Implementazione API === */
			
			List<String> existsConditions = new ArrayList<>();
			List<Object> existsParameters = new ArrayList<>();
			String aliasCT = "ct";
			String aliasALLARMI = "alarm";
			String aliasPDSA = "pdsa";
			String aliasPDTOKENSA = "pdtokensa";
			String aliasPD = "pd";
			String aliasPDTRASFORMAZIONISA = "pdtsa";
			String aliasPDTRASFORMAZIONI = "pdt";
			String aliasPASA = "pasa";
			String aliasPATOKENSA = "patokensa";
			String aliasPA = "pa";
			String aliasPATRASFORMAZIONISA = "patsa";
			String aliasPATRASFORMAZIONI = "pat";
			String aliasSERVIZI = "s";
			String aliasACCORDI = "a";
			String aliasACCORDIGRUPPI = "ag";
			String aliasGRUPPI = "g";
			String aliasSOGGETTI = "sProprietario";
			
	
			
			// autorizzazioni porte delegate
			/** select * from servizi_applicativi sa WHERE EXISTS (select pdsa.id from porte_delegate_sa pdsa, porte_delegate pd, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			     WHERE pdsa.id_servizio_applicativo=sa.id AND pdsa.id_porta=pd.id AND pd.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');*/
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = buildSqlQueryObjectAutorizzazioniPorteDelegate(CostantiDB.PORTE_DELEGATE_SA,
						aliasPDSA, aliasPD, aliasSERVIZI, aliasACCORDI, aliasACCORDIGRUPPI, aliasGRUPPI, aliasSOGGETTI,
						isFilterGruppoFruizione, apiImplementazioneFruizione, filterGruppo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteDelegate));
			} 
			
			// autorizzazioni token porte delegate
			/** select * from servizi_applicativi sa WHERE EXISTS (select pdsa.id from porte_delegate_sa pdsa, porte_delegate pd, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			     WHERE pdsa.id_servizio_applicativo=sa.id AND pdsa.id_porta=pd.id AND pd.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');*/
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteDelegate = buildSqlQueryObjectAutorizzazioniPorteDelegate(CostantiDB.PORTE_DELEGATE_TOKEN_SA,
						aliasPDTOKENSA, aliasPD, aliasSERVIZI, aliasACCORDI, aliasACCORDIGRUPPI, aliasGRUPPI, aliasSOGGETTI,
						isFilterGruppoFruizione, apiImplementazioneFruizione, filterGruppo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteDelegate.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteDelegate));
			} 
			
			// autorizzazioni porte applicative
			/** select * from servizi_applicativi sa WHERE EXISTS (select pasa.id from porte_applicative_sa_auth pasa, porte_applicative pa, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			    WHERE pasa.id_servizio_applicativo=sa.id AND pasa.id_porta=pa.id AND pa.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');*/
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = buildSqlQueryObjectAutorizzazioniPorteApplicative(CostantiDB.PORTE_APPLICATIVE_SA_AUTORIZZATI,
						aliasPASA, aliasPA, aliasSERVIZI, aliasACCORDI, aliasACCORDIGRUPPI, aliasGRUPPI, aliasSOGGETTI,
						isFilterGruppoErogazione, apiImplementazioneErogazione, filterGruppo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteApplicative));
			} 
			
			// autorizzazioni token porte applicative
			/** select * from servizi_applicativi sa WHERE EXISTS (select pasa.id from porte_applicative_sa_auth pasa, porte_applicative pa, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			    WHERE pasa.id_servizio_applicativo=sa.id AND pasa.id_porta=pa.id AND pa.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');*/
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectAutorizzazioniPorteApplicative = buildSqlQueryObjectAutorizzazioniPorteApplicative(CostantiDB.PORTE_APPLICATIVE_TOKEN_SA,
						aliasPATOKENSA, aliasPA, aliasSERVIZI, aliasACCORDI, aliasACCORDIGRUPPI, aliasGRUPPI, aliasSOGGETTI,
						isFilterGruppoErogazione, apiImplementazioneErogazione, filterGruppo,
						existsParameters);
				existsConditions.add(sqlQueryObjectAutorizzazioniPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectAutorizzazioniPorteApplicative));
			} 
			
			// server sulle porte applicative
			/** select * from servizi_applicativi sa WHERE EXISTS (select pasa.id from porte_applicative_sa pasa, porte_applicative pa, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			 WHERE pasa.id_servizio_applicativo=sa.id AND pasa.id_porta=pa.id AND pa.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');*/
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectServerPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA,aliasPASA);
				sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,aliasPA);
				if(isFilterGruppoErogazione) {
					sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
					sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
					sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
					sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
				}
				if(apiImplementazioneErogazione!=null) {
					sqlQueryObjectServerPorteApplicative.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
				}
				sqlQueryObjectServerPorteApplicative.addSelectAliasField(aliasPASA, "id", aliasPASA+"id");
				sqlQueryObjectServerPorteApplicative.setANDLogicOperator(true);
				sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasPASA+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF+" = "+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasPASA+"."+CostantiDB.PORTA_COLUMN_ID_REF+" = "+aliasPA+"."+CostantiDB.COLUMN_ID);
				if(isFilterGruppoErogazione) {
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_REF+" = "+aliasSERVIZI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasSERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_GRUPPO_REF+" = "+aliasGRUPPI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasGRUPPI+"."+CostantiDB.GRUPPI_COLUMN_NOME+" = ?");
					existsParameters.add(filterGruppo);
				}
				if(apiImplementazioneErogazione!=null) {
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_ID_SOGGETTO_REF+" = "+aliasSOGGETTI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+" = ?");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+" = ?");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_TIPO_SERVIZIO+" = ?");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_SERVIZIO+" = ?");
					sqlQueryObjectServerPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_VERSIONE_SERVIZIO+" = ?");
					existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getTipo());
					existsParameters.add(apiImplementazioneErogazione.getSoggettoErogatore().getNome());
					existsParameters.add(apiImplementazioneErogazione.getTipo());
					existsParameters.add(apiImplementazioneErogazione.getNome());
					existsParameters.add(apiImplementazioneErogazione.getVersione());
				}
				
				existsConditions.add(sqlQueryObjectServerPorteApplicative.getWhereExistsCondition(false, sqlQueryObjectServerPorteApplicative));
			} 
			
		
			// controllo del traffico
			/**
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
				sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY,aliasCT);
				if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null) {
					sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,aliasPD);
					if(isFilterGruppoFruizione) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
					}
					if(apiImplementazioneFruizione!=null) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
					}
				}
				sqlQueryObjectControlloTrafficoPorteDelegate.addSelectAliasField(aliasCT, "id", aliasCT+"id");
				sqlQueryObjectControlloTrafficoPorteDelegate.setANDLogicOperator(true);
				sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasCT+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_TIPO_FRUITORE+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
				sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasCT+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_NOME_FRUITORE+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
				sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasCT+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_SA_FRUITORE+" = "+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_NOME);
				sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasCT+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_RUOLO+" = 'delegata'");
				if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null) {
					sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasCT+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_PORTA+" = "+aliasPD+"."+CostantiDB.PORTA_COLUMN_NOME_PORTA);
					if(isFilterGruppoFruizione) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_REF+" = "+aliasSERVIZI+"."+CostantiDB.COLUMN_ID);
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasSERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_GRUPPO_REF+" = "+aliasGRUPPI+"."+CostantiDB.COLUMN_ID);
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasGRUPPI+"."+CostantiDB.GRUPPI_COLUMN_NOME+" = ?");
						existsParameters.add(filterGruppo);
					}
					if(apiImplementazioneFruizione!=null) {
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_ID_SOGGETTO_REF+" = "+aliasSOGGETTI+"."+CostantiDB.COLUMN_ID);
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+" = ?");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+" = ?");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_TIPO_SOGGETTO_EROGATORE+" = ?");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_NOME_SOGGETTO_EROGATORE+" = ?");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_TIPO_SERVIZIO+" = ?");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_NOME_SERVIZIO+" = ?");
						sqlQueryObjectControlloTrafficoPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_VERSIONE_SERVIZIO+" = ?");
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
				sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY,aliasCT);
				if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null) {
					sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,aliasPA);
					if(isFilterGruppoErogazione) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
					}
					if(apiImplementazioneErogazione!=null) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
					}
				}
				sqlQueryObjectControlloTrafficoPorteApplicative.addSelectAliasField(aliasCT, "id", aliasCT+"id");
				sqlQueryObjectControlloTrafficoPorteApplicative.setANDLogicOperator(true);
				sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasCT+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_TIPO_FRUITORE+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
				sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasCT+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_NOME_FRUITORE+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
				sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasCT+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_SA_FRUITORE+" = "+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_NOME);
				sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasCT+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_RUOLO+" = 'applicativa'");
				if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null) {
					sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasCT+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_PORTA+" = "+aliasPA+"."+CostantiDB.PORTA_COLUMN_NOME_PORTA);
					if(isFilterGruppoErogazione) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_REF+" = "+aliasSERVIZI+"."+CostantiDB.COLUMN_ID);
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasSERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_GRUPPO_REF+" = "+aliasGRUPPI+"."+CostantiDB.COLUMN_ID);
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasGRUPPI+"."+CostantiDB.GRUPPI_COLUMN_NOME+" = ?");
						existsParameters.add(filterGruppo);
					}
					if(apiImplementazioneErogazione!=null) {
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_ID_SOGGETTO_REF+" = "+aliasSOGGETTI+"."+CostantiDB.COLUMN_ID);
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+" = ?");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+" = ?");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_TIPO_SERVIZIO+" = ?");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_SERVIZIO+" = ?");
						sqlQueryObjectControlloTrafficoPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_VERSIONE_SERVIZIO+" = ?");
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
					sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ALLARMI,aliasALLARMI);
					if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null) {
						sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,aliasPD);
						if(isFilterGruppoFruizione) {
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
						}
						if(apiImplementazioneFruizione!=null) {
							sqlQueryObjectAllarmiPorteDelegate.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
						}
					}
					sqlQueryObjectAllarmiPorteDelegate.addSelectAliasField(aliasALLARMI, "id", aliasALLARMI+"id");
					sqlQueryObjectAllarmiPorteDelegate.setANDLogicOperator(true);
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasALLARMI+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_TIPO_FRUITORE+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasALLARMI+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_NOME_FRUITORE+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasALLARMI+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_SA_FRUITORE+" = "+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_NOME);
					sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasALLARMI+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_RUOLO+" = 'delegata'");
					if(isFilterGruppoFruizione || apiImplementazioneFruizione!=null) {
						sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasALLARMI+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_PORTA+" = "+aliasPD+"."+CostantiDB.PORTA_COLUMN_NOME_PORTA);
						if(isFilterGruppoFruizione) {
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_REF+" = "+aliasSERVIZI+"."+CostantiDB.COLUMN_ID);
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasSERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_GRUPPO_REF+" = "+aliasGRUPPI+"."+CostantiDB.COLUMN_ID);
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasGRUPPI+"."+CostantiDB.GRUPPI_COLUMN_NOME+" = ?");
							existsParameters.add(filterGruppo);
						}
						if(apiImplementazioneFruizione!=null) {
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_ID_SOGGETTO_REF+" = "+aliasSOGGETTI+"."+CostantiDB.COLUMN_ID);
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+" = ?");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+" = ?");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_TIPO_SOGGETTO_EROGATORE+" = ?");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_NOME_SOGGETTO_EROGATORE+" = ?");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_TIPO_SERVIZIO+" = ?");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_NOME_SERVIZIO+" = ?");
							sqlQueryObjectAllarmiPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_VERSIONE_SERVIZIO+" = ?");
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
					sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ALLARMI,aliasALLARMI);
					if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null) {
						sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,aliasPA);
						if(isFilterGruppoErogazione) {
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
						}
						if(apiImplementazioneErogazione!=null) {
							sqlQueryObjectAllarmiPorteApplicative.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
						}
					}
					sqlQueryObjectAllarmiPorteApplicative.addSelectAliasField(aliasALLARMI, "id", aliasALLARMI+"id");
					sqlQueryObjectAllarmiPorteApplicative.setANDLogicOperator(true);
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasALLARMI+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_TIPO_FRUITORE+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasALLARMI+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_NOME_FRUITORE+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasALLARMI+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_SA_FRUITORE+" = "+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_NOME);
					sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasALLARMI+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_RUOLO+" = 'applicativa'");
					if(isFilterGruppoErogazione || apiImplementazioneErogazione!=null) {
						sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasALLARMI+"."+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY_COLUMN_FILTRO_PORTA+" = "+aliasPA+"."+CostantiDB.PORTA_COLUMN_NOME_PORTA);
						if(isFilterGruppoErogazione) {
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_REF+" = "+aliasSERVIZI+"."+CostantiDB.COLUMN_ID);
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasSERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_GRUPPO_REF+" = "+aliasGRUPPI+"."+CostantiDB.COLUMN_ID);
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasGRUPPI+"."+CostantiDB.GRUPPI_COLUMN_NOME+" = ?");
							existsParameters.add(filterGruppo);
						}
						if(apiImplementazioneErogazione!=null) {
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_ID_SOGGETTO_REF+" = "+aliasSOGGETTI+"."+CostantiDB.COLUMN_ID);
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+" = ?");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+" = ?");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_TIPO_SERVIZIO+" = ?");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_SERVIZIO+" = ?");
							sqlQueryObjectAllarmiPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_VERSIONE_SERVIZIO+" = ?");
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
			/**
			 * select * from servizi_applicativi sa WHERE EXISTS (select pdtsa.id from pd_transform_sa pdtsa, pd_transform pdt, porte_delegate pd, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			 *    WHERE pdtsa.id_servizio_applicativo=sa.id AND pdtsa.id_trasformazione=pdt.id AND pdt.id_porta=pd.id AND pd.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');
			 **/
			if(isFilterGruppoFruizione  || TipoPdD.DELEGATA.equals(apiContesto) || apiImplementazioneFruizione!=null) {
				ISQLQueryObject sqlQueryObjectTrasformazioniPorteDelegate = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI_SA,aliasPDTRASFORMAZIONISA);
				sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE_TRASFORMAZIONI,aliasPDTRASFORMAZIONI);
				sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.PORTE_DELEGATE,aliasPD);
				if(isFilterGruppoFruizione) {
					sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
					sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
					sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
					sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
				}
				if(apiImplementazioneFruizione!=null) {
					sqlQueryObjectTrasformazioniPorteDelegate.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
				}
				sqlQueryObjectTrasformazioniPorteDelegate.addSelectAliasField(aliasPDTRASFORMAZIONISA, "id", aliasPDTRASFORMAZIONISA+"id");
				sqlQueryObjectTrasformazioniPorteDelegate.setANDLogicOperator(true);
				sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasPDTRASFORMAZIONISA+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF+" = "+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasPDTRASFORMAZIONISA+".id_trasformazione = "+aliasPDTRASFORMAZIONI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasPDTRASFORMAZIONI+"."+CostantiDB.PORTA_COLUMN_ID_REF+" = "+aliasPD+"."+CostantiDB.COLUMN_ID);
				if(isFilterGruppoFruizione) {
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_REF+" = "+aliasSERVIZI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasSERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_GRUPPO_REF+" = "+aliasGRUPPI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasGRUPPI+"."+CostantiDB.GRUPPI_COLUMN_NOME+" = ?");
					existsParameters.add(filterGruppo);
				}
				if(apiImplementazioneFruizione!=null) {
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_ID_SOGGETTO_REF+" = "+aliasSOGGETTI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+" = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+" = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_TIPO_SOGGETTO_EROGATORE+" = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_NOME_SOGGETTO_EROGATORE+" = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_TIPO_SERVIZIO+" = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_NOME_SERVIZIO+" = ?");
					sqlQueryObjectTrasformazioniPorteDelegate.addWhereCondition(aliasPD+"."+CostantiDB.PORTA_COLUMN_VERSIONE_SERVIZIO+" = ?");
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
			/**
			 * select * from servizi_applicativi sa WHERE EXISTS (select patsa.id from pa_transform_sa patsa, pa_transform pat, porte_applicative pa, servizi s, accordi a, accordi_gruppi ag, gruppi g 
			 * WHERE patsa.id_servizio_applicativo=sa.id AND patsa.id_trasformazione=pat.id AND pat.id_porta=pa.id AND pa.id_servizio=s.id AND s.id_accordo=a.id AND ag.id_accordo=a.id AND ag.id_gruppo=g.id AND g.nome='TAG');
			 **/
			
			if(isFilterGruppoErogazione  || TipoPdD.APPLICATIVA.equals(apiContesto) || apiImplementazioneErogazione!=null) {
				ISQLQueryObject sqlQueryObjectTrasformazioniPorteApplicative = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI_SA,aliasPATRASFORMAZIONISA);
				sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE_TRASFORMAZIONI,aliasPATRASFORMAZIONI);
				sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.PORTE_APPLICATIVE,aliasPA);
				if(isFilterGruppoErogazione) {
					sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.SERVIZI,aliasSERVIZI);
					sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI,aliasACCORDI);
					sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.ACCORDI_GRUPPI,aliasACCORDIGRUPPI);
					sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.GRUPPI,aliasGRUPPI);
				}
				if(apiImplementazioneErogazione!=null) {
					sqlQueryObjectTrasformazioniPorteApplicative.addFromTable(CostantiDB.SOGGETTI,aliasSOGGETTI);
				}
				sqlQueryObjectTrasformazioniPorteApplicative.addSelectAliasField(aliasPATRASFORMAZIONISA, "id", aliasPATRASFORMAZIONISA+"id");
				sqlQueryObjectTrasformazioniPorteApplicative.setANDLogicOperator(true);
				sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasPATRASFORMAZIONISA+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF+" = "+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasPATRASFORMAZIONISA+".id_trasformazione = "+aliasPATRASFORMAZIONI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasPATRASFORMAZIONI+"."+CostantiDB.PORTA_COLUMN_ID_REF+" = "+aliasPA+"."+CostantiDB.COLUMN_ID);
				if(isFilterGruppoErogazione) {
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_ID_SERVIZIO_REF+" = "+aliasSERVIZI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasSERVIZI+"."+CostantiDB.SERVIZI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_ACCORDO_REF+" = "+aliasACCORDI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasACCORDIGRUPPI+"."+CostantiDB.ACCORDI_GRUPPI_COLUMN_ID_GRUPPO_REF+" = "+aliasGRUPPI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasGRUPPI+"."+CostantiDB.GRUPPI_COLUMN_NOME+" = ?");
					existsParameters.add(filterGruppo);
				}
				if(apiImplementazioneErogazione!=null) {
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_ID_SOGGETTO_REF+" = "+aliasSOGGETTI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+" = ?");
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasSOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+" = ?");
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_TIPO_SERVIZIO+" = ?");
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_SERVIZIO+" = ?");
					sqlQueryObjectTrasformazioniPorteApplicative.addWhereCondition(aliasPA+"."+CostantiDB.PORTA_COLUMN_VERSIONE_SERVIZIO+" = ?");
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
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.COLUMN_ID);
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_SUPERUSER+" = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_NOME, search, true, true);
				if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+"", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+"<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+"<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+"=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+"=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_ID_SERVIZIO_APPLICATIVO_REF);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO+"=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" =?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+"=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_AS_CLIENT+" = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?",
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?"+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" "+CostantiDB.CONDITION_IS_NOT_NULL+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE+" "+CostantiDB.CONDITION_IS_NOT_NULL);
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE, 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer) &&
						CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false, 
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)),
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)));
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" = ?");
					}
				}
				if(pddTipologia!=null) {
					if(PddTipologia.ESTERNO.equals(pddTipologia)) {						
						sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
					}
					else {
						sqlQueryObject.addFromTable(CostantiDB.PDD);
						sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_NOME+"="+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_SERVER,CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_TIPO+"=?");
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
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.COLUMN_ID);
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_SUPERUSER+" = ?");
				if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+"", true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+"<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+"<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+"=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+"=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_ID_SERVIZIO_APPLICATIVO_REF);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO+"=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" =?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+"=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_AS_CLIENT+" = ?");
					}
					else { 
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?",
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?"+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" "+CostantiDB.CONDITION_IS_NOT_NULL+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE+" "+CostantiDB.CONDITION_IS_NOT_NULL);
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE, 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer) &&
						CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false, 
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)),
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)));
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" = ?");
					}
				}
				if(pddTipologia!=null) {
					if(PddTipologia.ESTERNO.equals(pddTipologia)) {						
						sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
					}
					else {
						sqlQueryObject.addFromTable(CostantiDB.PDD);
						sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_NOME+"="+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_SERVER,CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_TIPO+"=?");
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
/**				if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
//					// like
//				}*/
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
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO);
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO, aliasTipoSoggetto);
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO, aliasNomeSoggetto);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.COLUMN_ID);
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_SUPERUSER+" = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_NOME, search, true, true);
				if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO, true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+"<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+"<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+"=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+"=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_ID_SERVIZIO_APPLICATIVO_REF);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO+"=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" =?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+"=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_AS_CLIENT+" = ?");
					}
					else { 
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?",
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?"+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" "+CostantiDB.CONDITION_IS_NOT_NULL+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE+" "+CostantiDB.CONDITION_IS_NOT_NULL);
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE, 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer) &&
						CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false, 
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)),
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)));
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" = ?");
					}
				}
				if(pddTipologia!=null) {
					if(PddTipologia.ESTERNO.equals(pddTipologia)) {						
						sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
					}
					else {
						sqlQueryObject.addFromTable(CostantiDB.PDD);
						sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_NOME+"="+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_SERVER,CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_TIPO+"=?");
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
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO);
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO, aliasTipoSoggetto);
				sqlQueryObject.addSelectAliasField(CostantiDB.SOGGETTI,CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO, aliasNomeSoggetto);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.COLUMN_ID);
				if(this.driver.useSuperUser && superuser!=null && (!"".equals(superuser)))
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_SUPERUSER+" = ?");
				if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO, true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+"<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+"<>?");
				}
				if(filterSoggettoNome!=null && !"".equals(filterSoggettoNome)) {
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO+"=?");
					sqlQueryObject.addWhereCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO+"=?");
				}
				if(filterRuolo!=null && !"".equals(filterRuolo)) {
					sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_ID_SERVIZIO_APPLICATIVO_REF);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO+"=?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" =?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+"=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_AS_CLIENT+" = ?");
					}
					else { 
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?",
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?"+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" "+CostantiDB.CONDITION_IS_NOT_NULL+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE+" "+CostantiDB.CONDITION_IS_NOT_NULL);
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE, 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer) &&
						CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false, 
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)),
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)));
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" = ?");
					}
				}
				if(pddTipologia!=null) {
					if(PddTipologia.ESTERNO.equals(pddTipologia)) {						
						sqlQueryObject.addWhereCondition(sqlQueryObjectPdd.createSQLConditions());							
					}
					else {
						sqlQueryObject.addFromTable(CostantiDB.PDD);
						sqlQueryObject.addWhereCondition(true,CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_NOME+"="+CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_SERVER,CostantiDB.PDD+"."+CostantiDB.PDD_COLUMN_TIPO+"=?");
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
/**				if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
//					// like
//				}*/
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
			throw new DriverConfigurazioneException(getMessageError(nomeMetodo,  qe),qe);
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
		ArrayList<ServizioApplicativo> silList = new ArrayList<>();

		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException(getMessageDatasourceError(nomeMetodo,  e),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(PREFIX_ATOMICA + this.driver.atomica);

		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = ?");
				if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO, true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+"<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+"<>?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" =?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+"=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_AS_CLIENT+" = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?",
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?"+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" "+CostantiDB.CONDITION_IS_NOT_NULL+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE+" "+CostantiDB.CONDITION_IS_NOT_NULL);
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali)  || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE, 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer) &&
						CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false, 
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)),
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)));
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" = ?");
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
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_NOME, search, true, true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addSelectCountField("*", "cont");
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = ?");
				if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO, true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+"<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+"<>?");
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" =?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+"=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_AS_CLIENT+" = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?",
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?"+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" "+CostantiDB.CONDITION_IS_NOT_NULL+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE+" "+CostantiDB.CONDITION_IS_NOT_NULL);
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE, 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer) &&
						CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false, 
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)),
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)));
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" = ?");
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
/**				if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
//					// like
//				}*/
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
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO);
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = ?");
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_NOME, search, true, true);
				if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
					sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
					sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO, true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+"<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+"<>?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
					sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
					sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" =?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+"=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_AS_CLIENT+" = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?",
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?"+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" "+CostantiDB.CONDITION_IS_NOT_NULL+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE+" "+CostantiDB.CONDITION_IS_NOT_NULL);
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE, 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer) &&
						CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false, 
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)),
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)));
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" = ?");
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
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID);
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO);
				if (idSoggetto!=null)
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = ?");
				if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
					sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
					sqlQueryObject.addSelectField(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
					sqlQueryObject.addFromTable(CostantiDB.SOGGETTI);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+" = "+CostantiDB.SOGGETTI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObject.addWhereINCondition(CostantiDB.SOGGETTI+"."+CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO, true, tipoSoggettiProtocollo.toArray(new String[1]));
				}
				if(tipologiaFruizione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+"<>?");
				}
				else if(tipologiaErogazione!=null) {
					sqlQueryObject.addWhereCondition(true, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL, CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+"<>?");
				}
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy("nome");
				if(tipoSoggettiProtocollo!=null && !tipoSoggettiProtocollo.isEmpty()) {
					sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI_COLUMN_NOME_SOGGETTO);
					sqlQueryObject.addOrderBy(CostantiDB.SOGGETTI_COLUMN_TIPO_SOGGETTO);
				}
				if(filterTipoServizioApplicativo!=null && !"".equals(filterTipoServizioApplicativo)) {
					if(CostantiConfigurazione.CLIENT_OR_SERVER.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" =?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+"=?");
					}
					else if(CostantiConfigurazione.CLIENT.equals(filterTipoServizioApplicativo)) {
						sqlQueryObject.addWhereCondition(false, CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?", CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_AS_CLIENT+" = ?");
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPO+" = ?");
					}
				}
				if(filterTipoCredenziali!=null && !"".equals(filterTipoCredenziali)) {
					if(CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false,
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?",
								CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?"+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" "+CostantiDB.CONDITION_IS_NOT_NULL+
									CostantiDB.CONDITION_AND+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE+" "+CostantiDB.CONDITION_IS_NOT_NULL);
							
					}
					else {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" = ?");
					}
					if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
						if(CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereCondition(false, 
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)),
									sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_SUBJECT, filterCredenziale, 
											LikeConfig.contains(true,true)));
						}
						else if(CostantiConfigurazione.CREDENZIALE_BASIC.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_PRINCIPAL.toString().equals(filterTipoCredenziali) || 
								CostantiConfigurazione.CREDENZIALE_TOKEN.toString().equals(filterTipoCredenziali)) {
							sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_UTENTE, 
									filterCredenziale, LikeConfig.contains(true,true));
						}
					}
					if(filterCredenzialeIssuer!=null && !"".equals(filterCredenzialeIssuer) &&
						CostantiConfigurazione.CREDENZIALE_SSL.toString().equals(filterTipoCredenziali)) {
						sqlQueryObject.addWhereCondition(false, 
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_CN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)),
								sqlQueryObject.getWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ISSUER, filterCredenzialeIssuer, 
										LikeConfig.contains(true,true)));
					}
					if(filterCredenzialeTokenPolicy!=null && !"".equals(filterCredenzialeTokenPolicy)) {
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TOKEN_POLICY+" = ?");
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
/**				if(filterCredenziale!=null && !"".equals(filterCredenziale)) {
//					// like
//				}*/
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
			throw new DriverConfigurazioneException(getMessageError(nomeMetodo,  qe),qe);
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
				throw new DriverConfigurazioneException(getMessageDatasourceError(nomeMetodo,  e),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(PREFIX_ATOMICA + this.driver.atomica);

		List<String> listIdRuoli = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_ID_SERVIZIO_APPLICATIVO_REF);
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO, search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_ID_SERVIZIO_APPLICATIVO_REF);

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
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_ID_SERVIZIO_APPLICATIVO_REF);
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO, search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO);
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_RUOLI);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_ID_SERVIZIO_APPLICATIVO_REF);
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_RUOLI+"."+CostantiDB.SERVIZI_APPLICATIVI_RUOLI_COLUMN_RUOLO);
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
			throw new DriverConfigurazioneException(getMessageError(nomeMetodo,  qe),qe);
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
				throw new DriverConfigurazioneException(getMessageDatasourceError(nomeMetodo,  e),e);

			}

		} else
			con = this.driver.globalConnection;

		this.driver.logDebug(PREFIX_ATOMICA + this.driver.atomica);

		List<Proprieta> lista = null;
		try {

			if (!search.equals("")) {
				//query con search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.SERVIZI_APPLICATIVI_PROPS_ID_SERVIZIO_APPLICATIVO_REF);
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_NOME, search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
				sqlQueryObject.addSelectCountField("*", "cont");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.SERVIZI_APPLICATIVI_PROPS_ID_SERVIZIO_APPLICATIVO_REF);

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
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_NOME);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".valore");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.COLUMN_ID);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.SERVIZI_APPLICATIVI_PROPS_ID_SERVIZIO_APPLICATIVO_REF);
				sqlQueryObject.addWhereLikeCondition(CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_NOME, search, true, true);	
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_NOME);
				sqlQueryObject.setSortType(true);
				sqlQueryObject.setLimit(limit);
				sqlQueryObject.setOffset(offset);
				queryString = sqlQueryObject.createSQLQuery();
			} else {
				// senza search
				ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
				sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI_PROPS);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_NOME);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+".valore");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.COLUMN_ID);
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"=?");
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID+"="+CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.SERVIZI_APPLICATIVI_PROPS_ID_SERVIZIO_APPLICATIVO_REF);
				
				sqlQueryObject.setSelectDistinct(true);
				sqlQueryObject.setANDLogicOperator(true);
				sqlQueryObject.addOrderBy(CostantiDB.SERVIZI_APPLICATIVI_PROPS+"."+CostantiDB.SERVIZI_APPLICATIVI_PROPS_COLUMN_NOME);
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
			throw new DriverConfigurazioneException(getMessageError(nomeMetodo,  qe),qe);
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
		return serviziApplicativiListEngineCount(null, null, false, null, false, filters, false, null, null);
	}
	protected int countTipologieServiziApplicativi(IDSoggetto proprietario,ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, null, false, null, false, filters, false, null, null);
	}
	
	// fruizione
	
	protected int countTipologieFruizioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, null, false, null, true, filters, false, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, null, false, null, true, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, null, false, null, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, null, false, fruizione, true, filters, false, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, null, false, fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, null, false, fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, null, false, null, true, filters, false, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, null, false, null, true, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, null, false, null, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, null, false, fruizione, true, filters, false, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, null, false, fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, null, false, fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	// erogazione
	
	protected int countTipologieErogazioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, null, true, null, false, filters, false, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	protected int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, erogazione, true, null, false, filters, false, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, erogazione, true, null, false,  filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(null, erogazione, true, null, false,  filters, checkAssociazionePorta, null, isBound);
	}
	
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, erogazione, true,  null, false, filters, false, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, null, true, null, false, filters, false, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected int countTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngineCount(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	// engine
	
	private int serviziApplicativiListEngineCount(IDSoggetto proprietario, 
			RicercaTipologiaErogazione erogazione, boolean searchSoloErogazione,  
			RicercaTipologiaFruizione fruizione, boolean serchSoloFruizione, 
			ISearch filters,boolean checkAssociazionePorta,
			Boolean fruizioneIsBound, Boolean erogazioneIsBound) throws DriverConfigurazioneException {
	
		String nomeMetodo = "serviziApplicativiCount";
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
				throw new DriverConfigurazioneException(getMessageDatasourceError(nomeMetodo,  e),e);
	
			}
	
		} else
			con = this.driver.globalConnection;
	
		this.driver.logDebug(PREFIX_ATOMICA + this.driver.atomica);
	
		try {
	
			if(proprietario!=null){
				idProprietario=DBUtils.getIdSoggetto(proprietario.getNome(), proprietario.getTipo(), con, this.driver.tipoDB);
				if(idProprietario==null || idProprietario<0) 
					throw new DriverConfigurazioneException(getMessageProprietarioNonEsiste(nomeMetodo,  proprietario));
			}
	
	
			// *** count ***
			ISQLQueryObject sqlQueryObject = this.getServiziApplicativiSearchFiltratiTipologia(proprietario, 
					erogazione, searchSoloErogazione,
					fruizione, 	serchSoloFruizione,  
					checkAssociazionePorta, search, true,
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
				count =  risultato.getInt(CostantiDB.COLUMN_ALIAS_COUNT);
			}
			risultato.close();
			stmt.close();
	
			return count;
	
		} catch (Exception qe) {
			error = true;
			throw new DriverConfigurazioneException(getMessageError(nomeMetodo,  qe),qe);
		} finally {
	
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
	
			this.driver.closeConnection(error,con);
		}
	}
	
	
	
	
	
	// all
	protected List<TipologiaServizioApplicativo> getTipologieServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, null, false,  null, false, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieServiziApplicativi(IDSoggetto proprietario,ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, null, false,  null, false, filters, false, null, null);
	}
	
	// fruizione
	
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, null, false, null, true, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, null, false,  null, true, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, null, false,  null, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, null, false, fruizione, true, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, null, false,  fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, null, false,  fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, null, false, null, true, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, null, false, null, true, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, null, false, null, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, null, false, fruizione, true, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, null, false,  fruizione, true, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieFruizioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaFruizione fruizione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, null, false,  fruizione, true, filters, checkAssociazionePorta, isBound, null);
	}
	
	// erogazione
	
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, null, true, null, false, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, erogazione, true, null, false, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, erogazione, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(null, erogazione, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, erogazione, true, null, false, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, RicercaTipologiaErogazione erogazione, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, erogazione, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, null, true, null, false, filters, false, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, null);
	}
	protected List<TipologiaServizioApplicativo> getTipologieErogazioneServiziApplicativi(IDSoggetto proprietario, ISearch filters,boolean checkAssociazionePorta, Boolean isBound) throws DriverConfigurazioneException
	{
		return serviziApplicativiListEngine(proprietario, null, true, null, false, filters, checkAssociazionePorta, null, isBound);
	}
	
	// engine
	
	private List<TipologiaServizioApplicativo> serviziApplicativiListEngine(IDSoggetto proprietario, 
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
	
		ArrayList<TipologiaServizioApplicativo> lista = new ArrayList<>();
	
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException(getMessageDatasourceError(nomeMetodo,  e),e);
	
			}
	
		} else
			con = this.driver.globalConnection;
	
		this.driver.logDebug(PREFIX_ATOMICA + this.driver.atomica);
	
		try {
	
			if(proprietario!=null){
				idProprietario=DBUtils.getIdSoggetto(proprietario.getNome(), proprietario.getTipo(), con, this.driver.tipoDB);
				if(idProprietario==null || idProprietario<0) 
					throw new DriverConfigurazioneException(getMessageProprietarioNonEsiste(nomeMetodo,  proprietario));
			}
			filters.setNumEntries(idLista, this.serviziApplicativiListEngineCount(proprietario, 
					erogazione, searchSoloErogazione, 
					fruizione, serchSoloFruizione, 
					filters, checkAssociazionePorta,
					fruizioneIsBound, erogazioneIsBound));
	
	
	
			// *** select ***
			ISQLQueryObject sqlQueryObject = this.getServiziApplicativiSearchFiltratiTipologia(proprietario, 
					erogazione, searchSoloErogazione,
					fruizione, serchSoloFruizione,  
					checkAssociazionePorta, search, false,
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
					long idProprietarioLetto = risultato.getLong(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO);
					Soggetto s = this.soggettiDriver.getSoggetto(idProprietarioLetto);
					idSA.setIdSoggettoProprietario(new IDSoggetto(s.getTipo(), s.getNome()));
				}
				idSA.setNome(risultato.getString("nome"));
	
				TipologiaServizioApplicativo tipologia = new TipologiaServizioApplicativo();
				tipologia.setIdServizioApplicativo(idSA);
	
				String tipologiaFruizione = risultato.getString(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE);
				if(tipologiaFruizione!=null && !"".equals(tipologiaFruizione)){
					tipologia.setTipologiaFruizione(TipologiaFruizione.toEnumConstant(tipologiaFruizione));
				}
				String tipologiaErogazione = risultato.getString(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE);
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
			throw new DriverConfigurazioneException(getMessageError(nomeMetodo,  qe),qe);
		} finally {
	
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
	
			this.driver.closeConnection(error,con);
		}
	}
	
	private ISQLQueryObject getServiziApplicativiSearchFiltratiTipologia(IDSoggetto proprietario, 
			RicercaTipologiaErogazione erogazione, boolean searchSoloErogazione,  
			RicercaTipologiaFruizione fruizione, boolean serchSoloFruizione, 
			boolean checkAssociazionePorta,String search, boolean count,
			Boolean fruizioneIsBound, Boolean erogazioneIsBound) throws DriverConfigurazioneException{
	
		try {
		
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			if(count){
				sqlQueryObject.addSelectCountField("*",CostantiDB.COLUMN_ALIAS_COUNT);
			}
			else{
				sqlQueryObject.addSelectField("nome");
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE);
				sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE);
			}
		
			if(proprietario!=null){
				sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+"=?");
			}
			if(!"".equals(search)){
				sqlQueryObject.addWhereLikeCondition("nome", search,false,true,true); // escape a false, deve essere pilotato da chi lo usa!
			}
		
			if(serchSoloFruizione){
				//se fruizione non specificata voglio tutti i tipi di fruizione che non siano disabilitati
				if(fruizione==null){
					sqlQueryObject.addWhereCondition(true,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" <> ?");
				}else{
					if(RicercaTipologiaFruizione.DISABILITATO.toString().equals(fruizione.toString())){
						sqlQueryObject.addWhereCondition(false,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" "+CostantiDB.CONDITION_IS_NULL,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" = ?");
					}
					else if(RicercaTipologiaFruizione.ALL.toString().equals(fruizione.toString())){
						// Non devo fare niente (l'associazione porta seguente fara' si che verranno tornati solo i sa associati a PD)
					}
					else{
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE+" = ?");
					}
				}
				if(checkAssociazionePorta){
					
					ISQLQueryObject sqlQueryObjectIn = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					
					ISQLQueryObject sqlQueryObjectTrasportoIn = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectTrasportoIn.addFromTable(CostantiDB.PORTE_DELEGATE_SA);
					sqlQueryObjectTrasportoIn.addSelectField(CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF);
					sqlQueryObjectTrasportoIn.addWhereCondition(CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF+"="+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectIn.addWhereExistsCondition(false, sqlQueryObjectTrasportoIn);
				
					ISQLQueryObject sqlQueryObjectTokenIn = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectTokenIn.addFromTable(CostantiDB.PORTE_DELEGATE_TOKEN_SA);
					sqlQueryObjectTokenIn.addSelectField(CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF);
					sqlQueryObjectTokenIn.addWhereCondition(CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF+"="+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObjectIn.addWhereExistsCondition(false, sqlQueryObjectTokenIn);
					
					sqlQueryObjectIn.setANDLogicOperator(false);
					
					sqlQueryObjectIn.addWhereCondition(sqlQueryObjectIn.createSQLConditions());
				}
			}
		
			else if(searchSoloErogazione){
				//se erogazione non specificato voglio tutti i tipi di erogazione
				if(erogazione==null){
					sqlQueryObject.addWhereCondition(true,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" "+CostantiDB.CONDITION_IS_NOT_NULL,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" <> ?");
				}else{
					if(RicercaTipologiaErogazione.DISABILITATO.toString().equals(erogazione.toString())){
						sqlQueryObject.addWhereCondition(false,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" "+CostantiDB.CONDITION_IS_NULL,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" = ?");
					}
					else if(RicercaTipologiaErogazione.ALL.toString().equals(erogazione.toString())){
						// Non devo fare niente (l'associazione porta seguente fara' si che verranno tornati solo i sa associati a PD)
					}
					else{
						sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE+" = ?");
					}
				}
				if(checkAssociazionePorta){
					ISQLQueryObject sqlQueryObjectIn = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
					sqlQueryObjectIn.addFromTable(CostantiDB.PORTE_APPLICATIVE_SA);
					sqlQueryObjectIn.addSelectField(CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF);
					sqlQueryObjectIn.addWhereCondition(CostantiDB.PORTA_COLUMN_ID_SERVIZIO_APPLICATIVO_REF+"="+CostantiDB.SERVIZI_APPLICATIVI+"."+CostantiDB.COLUMN_ID);
					sqlQueryObject.addWhereExistsCondition(false, sqlQueryObjectIn);
				}
			}
			
			if(fruizioneIsBound!=null){
				if(fruizioneIsBound){
					sqlQueryObject.addWhereIsNotNullCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH);
					sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+"<>?");
				}
				else{
					sqlQueryObject.addWhereCondition(false,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+" "+CostantiDB.CONDITION_IS_NULL,CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOAUTH+"=?");
				}
			}
			
			if(erogazioneIsBound!=null){
				
				ISQLQueryObject sqlQueryObjectConnettore = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectConnettore.setANDLogicOperator(true);
				sqlQueryObjectConnettore.addFromTable(CostantiDB.CONNETTORI);
				sqlQueryObjectConnettore.addSelectField("id");
				sqlQueryObjectConnettore.addWhereCondition("id_connettore_inv="+CostantiDB.CONNETTORI+"."+CostantiDB.COLUMN_ID);
				
				// Fix OP-708
				ISQLQueryObject sqlQueryObjectNullCondition = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectNullCondition.addWhereIsNotNullCondition("url");
				ISQLQueryObject sqlQueryObjectEmptyCondition = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
				sqlQueryObjectEmptyCondition.addWhereIsNotEmptyCondition("url");
				
				sqlQueryObjectConnettore.addWhereCondition(false,
						"endpointtype<>?"+CostantiDB.CONDITION_AND+"endpointtype<>?",
						//"endpointtype=? AND url is not null AND url <> ?"); OP-708
						"endpointtype=?"+CostantiDB.CONDITION_AND+""+sqlQueryObjectNullCondition.createSQLConditions()+
										CostantiDB.CONDITION_AND+sqlQueryObjectEmptyCondition.createSQLConditions());
				
				sqlQueryObject.addWhereExistsCondition(!erogazioneIsBound, sqlQueryObjectConnettore);

			}
			
			sqlQueryObject.setANDLogicOperator(true);
		
			return sqlQueryObject;
			
		} catch (Exception e) {
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}
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
	
		ArrayList<IDServizioApplicativo> lista = new ArrayList<>();
	
		if (this.driver.atomica) {
			try {
				con = this.driver.getConnectionFromDatasource(nomeMetodo);
				con.setAutoCommit(false);
			} catch (Exception e) {
				throw new DriverConfigurazioneException(getMessageDatasourceError(nomeMetodo,  e),e);
	
			}
	
		} else
			con = this.driver.globalConnection;
	
		this.driver.logDebug(PREFIX_ATOMICA + this.driver.atomica);
	
		try {
	
			idProprietario=DBUtils.getIdSoggetto(proprietario.getNome(), proprietario.getTipo(), con, this.driver.tipoDB);
			if(idProprietario<0) 
				throw new DriverConfigurazioneException(getMessageProprietarioNonEsiste(nomeMetodo,  proprietario));
	
			//count
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectCountField("*",CostantiDB.COLUMN_ALIAS_COUNT);
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+"=?");
	
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
				filters.setNumEntries(idLista, risultato.getInt(CostantiDB.COLUMN_ALIAS_COUNT));
			}
			risultato.close();
			stmt.close();
	
			//select
			sqlQueryObject = SQLObjectFactory.createSQLQueryObject(this.driver.tipoDB);
			sqlQueryObject.addFromTable(CostantiDB.SERVIZI_APPLICATIVI);
			sqlQueryObject.addSelectField("nome");
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_FRUIZIONE);
			sqlQueryObject.addSelectField(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_TIPOLOGIA_EROGAZIONE);
	
			sqlQueryObject.addWhereCondition(CostantiDB.SERVIZI_APPLICATIVI_COLUMN_ID_SOGGETTO+"=?");
	
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
			throw new DriverConfigurazioneException(getMessageError(nomeMetodo,  qe),qe);
		} finally {
	
			//Chiudo statement and resultset
			JDBCUtilities.closeResources(risultato, stmt);
	
			this.driver.closeConnection(error,con);
		}
	}
}
