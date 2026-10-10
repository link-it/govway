/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.pdnd.async;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.jdbc.CustomKeyGeneratorObject;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKey;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyJDBCType;
import org.openspcoop2.utils.jdbc.InsertAndGeneratedKeyObject;
import org.openspcoop2.utils.jdbc.JDBCUtilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;

/**
 * Accesso alla tabella PDND_INTERAZIONI_ASYNC che mantiene lo stato delle interazioni
 * relative agli scambi di dati asincroni PDND.
 * 
 * Gli aggiornamenti delle fasi sono condizionati allo stato attuale della riga (es. la conferma di ricezione
 * viene registrata solamente se non già presente), in modo da rilevare richieste concorrenti sulla stessa interazione:
 * il numero di righe aggiornate restituito permette al chiamante di capire se l'aggiornamento è avvenuto.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InterazioniAsincronePDNDDriverUtils {
	
	private InterazioniAsincronePDNDDriverUtils() {}

	private static final String COLUMN_ID = CostantiDB.PDND_INTERAZIONI_ASYNC_COLUMN_ID;
	private static final String COLUMN_INTERACTION_ID = "INTERACTION_ID";
	private static final String COLUMN_RUOLO = "RUOLO";
	private static final String COLUMN_FASE = "FASE";
	private static final String COLUMN_TIPO_FRUITORE = "TIPO_FRUITORE";
	private static final String COLUMN_FRUITORE = "FRUITORE";
	private static final String COLUMN_TIPO_EROGATORE = "TIPO_EROGATORE";
	private static final String COLUMN_EROGATORE = "EROGATORE";
	private static final String COLUMN_TIPO_SERVIZIO = "TIPO_SERVIZIO";
	private static final String COLUMN_SERVIZIO = "SERVIZIO";
	private static final String COLUMN_VERSIONE_SERVIZIO = "VERSIONE_SERVIZIO";
	private static final String COLUMN_TIPO_SERVIZIO_CALLBACK = "TIPO_SERVIZIO_CALLBACK";
	private static final String COLUMN_SERVIZIO_CALLBACK = "SERVIZIO_CALLBACK";
	private static final String COLUMN_VERSIONE_SERVIZIO_CALLBACK = "VERSIONE_SERVIZIO_CALLBACK";
	private static final String COLUMN_PURPOSE_ID = "PURPOSE_ID";
	private static final String COLUMN_CONSUMER_ID = "CONSUMER_ID";
	private static final String COLUMN_CLIENT_ID = "CLIENT_ID";
	private static final String COLUMN_URL_CALLBACK = "URL_CALLBACK";
	private static final String COLUMN_ENTITY_NUMBER = "ENTITY_NUMBER";
	private static final String COLUMN_TEMPO_MAX_CALLBACK = "TEMPO_MAX_CALLBACK";
	private static final String COLUMN_TEMPO_DISPONIBILITA = "TEMPO_DISPONIBILITA";
	private static final String COLUMN_CONFERMA_RICHIESTA = "CONFERMA_RICHIESTA";
	private static final String COLUMN_LIMITE_ENTITA = "LIMITE_ENTITA";
	private static final String COLUMN_NUMERO_GET_RESOURCE = "NUMERO_GET_RESOURCE";
	private static final String COLUMN_ID_TRANSAZIONE_START = "ID_TRANSAZIONE_START";
	private static final String COLUMN_DATA_START = "DATA_START";
	private static final String COLUMN_DATA_CALLBACK = "DATA_CALLBACK";
	private static final String COLUMN_DATA_GET_RESOURCE = "DATA_GET_RESOURCE";
	private static final String COLUMN_DATA_CONFIRMATION = "DATA_CONFIRMATION";
	private static final String COLUMN_DATA_SCADENZA = "DATA_SCADENZA";
	private static final String COLUMN_DATA_AGGIORNAMENTO = "DATA_AGGIORNAMENTO";
	
	private static final String CONDITION_PARAM = "=?";
	
	private static final String[] SELECT_COLUMNS = {
			COLUMN_ID, COLUMN_INTERACTION_ID, COLUMN_RUOLO, COLUMN_FASE,
			COLUMN_TIPO_FRUITORE, COLUMN_FRUITORE, COLUMN_TIPO_EROGATORE, COLUMN_EROGATORE,
			COLUMN_TIPO_SERVIZIO, COLUMN_SERVIZIO, COLUMN_VERSIONE_SERVIZIO,
			COLUMN_TIPO_SERVIZIO_CALLBACK, COLUMN_SERVIZIO_CALLBACK, COLUMN_VERSIONE_SERVIZIO_CALLBACK,
			COLUMN_PURPOSE_ID, COLUMN_CONSUMER_ID, COLUMN_CLIENT_ID, COLUMN_URL_CALLBACK, COLUMN_ENTITY_NUMBER,
			COLUMN_TEMPO_MAX_CALLBACK, COLUMN_TEMPO_DISPONIBILITA, COLUMN_CONFERMA_RICHIESTA, COLUMN_LIMITE_ENTITA,
			COLUMN_NUMERO_GET_RESOURCE, COLUMN_ID_TRANSAZIONE_START,
			COLUMN_DATA_START, COLUMN_DATA_CALLBACK, COLUMN_DATA_GET_RESOURCE, COLUMN_DATA_CONFIRMATION,
			COLUMN_DATA_SCADENZA, COLUMN_DATA_AGGIORNAMENTO
	};
	
	// Colonne elencate esplicitamente: con '*' la paginazione su Oracle (ROWNUM) produce una query non valida
	private static void addSelectFields(ISQLQueryObject sqlQueryObject) throws SQLQueryObjectException {
		for (String column : SELECT_COLUMNS) {
			sqlQueryObject.addSelectField(column);
		}
	}
	
	
	
	
	/* **** Inserimento **** */
	
	public static long insert(Connection con, String tipoDatabase, InterazioneAsincronaPDND interazione) throws InterazioniAsincronePDNDException {
		try {
			checkPerInserimento(interazione);
			
			Timestamp now = new Timestamp(interazione.getDataAggiornamento()!=null ? interazione.getDataAggiornamento().getTime() : interazione.getDataStart().getTime());
			IDServizio idServizio = interazione.getServizio();
			IDServizio idServizioCallback = interazione.getServizioCallback();
			
			List<InsertAndGeneratedKeyObject> list = new ArrayList<>();
			list.add( new InsertAndGeneratedKeyObject(COLUMN_INTERACTION_ID, interazione.getInteractionId(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_RUOLO, interazione.getRuolo().getTipo(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_FASE, interazione.getFase().getValore(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_TIPO_FRUITORE, interazione.getFruitore()!=null ? interazione.getFruitore().getTipo() : null, InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_FRUITORE, interazione.getFruitore()!=null ? interazione.getFruitore().getNome() : null, InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_TIPO_EROGATORE, idServizio.getSoggettoErogatore().getTipo(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_EROGATORE, idServizio.getSoggettoErogatore().getNome(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_TIPO_SERVIZIO, idServizio.getTipo(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_SERVIZIO, idServizio.getNome(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_VERSIONE_SERVIZIO, idServizio.getVersione(), InsertAndGeneratedKeyJDBCType.INT) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_TIPO_SERVIZIO_CALLBACK, idServizioCallback!=null ? idServizioCallback.getTipo() : null, InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_SERVIZIO_CALLBACK, idServizioCallback!=null ? idServizioCallback.getNome() : null, InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_VERSIONE_SERVIZIO_CALLBACK, idServizioCallback!=null ? idServizioCallback.getVersione() : null, InsertAndGeneratedKeyJDBCType.INT) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_PURPOSE_ID, interazione.getPurposeId(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_CONSUMER_ID, interazione.getConsumerId(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_CLIENT_ID, interazione.getClientId(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_URL_CALLBACK, interazione.getUrlCallback(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_ENTITY_NUMBER, interazione.getEntityNumber(), InsertAndGeneratedKeyJDBCType.INT) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_TEMPO_MAX_CALLBACK, interazione.getTempoMaxCallback(), InsertAndGeneratedKeyJDBCType.LONG) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_TEMPO_DISPONIBILITA, interazione.getTempoDisponibilita(), InsertAndGeneratedKeyJDBCType.LONG) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_CONFERMA_RICHIESTA, interazione.isConfermaRichiesta() ? CostantiDB.TRUE : CostantiDB.FALSE, InsertAndGeneratedKeyJDBCType.INT) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_LIMITE_ENTITA, interazione.getLimiteEntita(), InsertAndGeneratedKeyJDBCType.INT) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_NUMERO_GET_RESOURCE, interazione.getNumeroGetResource(), InsertAndGeneratedKeyJDBCType.INT) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_ID_TRANSAZIONE_START, interazione.getIdTransazioneStart(), InsertAndGeneratedKeyJDBCType.STRING) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_DATA_START, toTimestamp(interazione.getDataStart()), InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_DATA_CALLBACK, toTimestamp(interazione.getDataCallback()), InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_DATA_GET_RESOURCE, toTimestamp(interazione.getDataGetResource()), InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_DATA_CONFIRMATION, toTimestamp(interazione.getDataConfirmation()), InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_DATA_SCADENZA, toTimestamp(interazione.getDataScadenza()), InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
			list.add( new InsertAndGeneratedKeyObject(COLUMN_DATA_AGGIORNAMENTO, now, InsertAndGeneratedKeyJDBCType.TIMESTAMP) );
			
			long id = InsertAndGeneratedKey.insertAndReturnGeneratedKey(con, TipiDatabase.toEnumConstant(tipoDatabase), 
					new CustomKeyGeneratorObject(CostantiDB.PDND_INTERAZIONI_ASYNC, CostantiDB.PDND_INTERAZIONI_ASYNC_COLUMN_ID, 
							CostantiDB.PDND_INTERAZIONI_ASYNC_SEQUENCE, CostantiDB.PDND_INTERAZIONI_ASYNC_TABLE_FOR_ID),
					list.toArray(new InsertAndGeneratedKeyObject[1]));
			if(id<=0){
				throw new InterazioniAsincronePDNDException("ID (InterazioneAsincronaPDND) autoincrementale non ottenuto");
			}
			interazione.setId(id);
			return id;
		}
		catch(InterazioniAsincronePDNDException e) {
			throw e;
		}
		catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
	}
	private static void checkPerInserimento(InterazioneAsincronaPDND interazione) throws InterazioniAsincronePDNDException {
		if(interazione==null) {
			throw new InterazioniAsincronePDNDException("Interazione non fornita");
		}
		if(interazione.getInteractionId()==null) {
			throw new InterazioniAsincronePDNDException("InteractionId non fornito");
		}
		if(interazione.getRuolo()==null) {
			throw new InterazioniAsincronePDNDException("Ruolo non fornito");
		}
		if(interazione.getFase()==null) {
			throw new InterazioniAsincronePDNDException("Fase non fornita");
		}
		// lato erogatore il fruitore può non essere noto (es. consumer PDND non registrato come soggetto)
		if(interazione.getFruitore()==null && TipoPdD.DELEGATA.equals(interazione.getRuolo())) {
			throw new InterazioniAsincronePDNDException("Fruitore non fornito");
		}
		if(interazione.getServizio()==null || interazione.getServizio().getSoggettoErogatore()==null) {
			throw new InterazioniAsincronePDNDException("Servizio non fornito");
		}
		if(interazione.getDataStart()==null) {
			throw new InterazioniAsincronePDNDException("Data di start non fornita");
		}
	}
	
	
	
	
	/* **** Lettura **** */
	
	public static InterazioneAsincronaPDND get(Connection con, String tipoDatabase, String interactionId, TipoPdD ruolo) throws InterazioniAsincronePDNDException {
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.PDND_INTERAZIONI_ASYNC);
			addSelectFields(sqlQueryObject);
			sqlQueryObject.addWhereCondition(COLUMN_INTERACTION_ID+CONDITION_PARAM);
			sqlQueryObject.addWhereCondition(COLUMN_RUOLO+CONDITION_PARAM);
			sqlQueryObject.setANDLogicOperator(true);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setString(1, interactionId);
			selectStmt.setString(2, ruolo.getTipo());
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				return read(selectRS);
			}
			return null;
		}
		catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
	}
	
	public static InterazioneAsincronaPDND getById(Connection con, String tipoDatabase, long id) throws InterazioniAsincronePDNDException {
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.PDND_INTERAZIONI_ASYNC);
			addSelectFields(sqlQueryObject);
			sqlQueryObject.addWhereCondition(COLUMN_ID+CONDITION_PARAM);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			selectStmt.setLong(1, id);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				return read(selectRS);
			}
			return null;
		}
		catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
	}
	
	public static long count(Connection con, String tipoDatabase, InterazioniAsincronePDNDFiltro filtro) throws InterazioniAsincronePDNDException {
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.PDND_INTERAZIONI_ASYNC);
			sqlQueryObject.addSelectCountField("somma");
			List<Object> params = buildFiltro(sqlQueryObject, filtro);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			setParams(selectStmt, params);
			selectRS = selectStmt.executeQuery();
			if(selectRS.next()) {
				long size = selectRS.getLong("somma");
				return size<0 ? 0 : size;
			}
			return 0;
		}
		catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
	}
	
	public static List<InterazioneAsincronaPDND> find(Connection con, String tipoDatabase, InterazioniAsincronePDNDFiltro filtro) throws InterazioniAsincronePDNDException {
		List<InterazioneAsincronaPDND> list = new ArrayList<>();
		PreparedStatement selectStmt = null;
		ResultSet selectRS = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addFromTable(CostantiDB.PDND_INTERAZIONI_ASYNC);
			addSelectFields(sqlQueryObject);
			List<Object> params = buildFiltro(sqlQueryObject, filtro);
			sqlQueryObject.addOrderBy(COLUMN_DATA_START, false);
			int limit = filtro!=null && filtro.getLimit()>0 ? filtro.getLimit() : ISQLQueryObject.LIMIT_DEFAULT_VALUE;
			sqlQueryObject.setOffset(filtro!=null ? filtro.getOffset() : 0);
			sqlQueryObject.setLimit(limit);
			String sqlQuery = sqlQueryObject.createSQLQuery();
			selectStmt = con.prepareStatement(sqlQuery);
			setParams(selectStmt, params);
			selectRS = selectStmt.executeQuery();
			while(selectRS.next()) {
				list.add(read(selectRS));
			}
		}
		catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(selectRS, selectStmt);
		}
		return list;
	}
	
	private static List<Object> buildFiltro(ISQLQueryObject sqlQueryObject, InterazioniAsincronePDNDFiltro filtro) throws SQLQueryObjectException {
		List<Object> params = new ArrayList<>();
		if(filtro==null) {
			return params;
		}
		if(filtro.getInteractionId()!=null && !"".equals(filtro.getInteractionId())) {
			sqlQueryObject.addWhereLikeCondition(COLUMN_INTERACTION_ID, filtro.getInteractionId(), LikeConfig.contains(true));
		}
		if(filtro.getRuolo()!=null) {
			sqlQueryObject.addWhereCondition(COLUMN_RUOLO+CONDITION_PARAM);
			params.add(filtro.getRuolo().getTipo());
		}
		if(filtro.getFase()!=null) {
			sqlQueryObject.addWhereCondition(COLUMN_FASE+CONDITION_PARAM);
			params.add(filtro.getFase().getValore());
		}
		if(filtro.getFruitore()!=null) {
			addCondition(sqlQueryObject, params, COLUMN_TIPO_FRUITORE, filtro.getFruitore().getTipo());
			addCondition(sqlQueryObject, params, COLUMN_FRUITORE, filtro.getFruitore().getNome());
		}
		if(filtro.getServizio()!=null) {
			buildFiltroServizio(sqlQueryObject, params, filtro.getServizio());
		}
		if(filtro.getScadute()!=null) {
			buildFiltroScadenza(sqlQueryObject, params, filtro);
		}
		sqlQueryObject.setANDLogicOperator(true);
		return params;
	}
	private static void buildFiltroServizio(ISQLQueryObject sqlQueryObject, List<Object> params, IDServizio idServizio) throws SQLQueryObjectException {
		if(idServizio.getSoggettoErogatore()!=null) {
			addCondition(sqlQueryObject, params, COLUMN_TIPO_EROGATORE, idServizio.getSoggettoErogatore().getTipo());
			addCondition(sqlQueryObject, params, COLUMN_EROGATORE, idServizio.getSoggettoErogatore().getNome());
		}
		addCondition(sqlQueryObject, params, COLUMN_TIPO_SERVIZIO, idServizio.getTipo());
		addCondition(sqlQueryObject, params, COLUMN_SERVIZIO, idServizio.getNome());
		if(idServizio.getVersione()!=null) {
			sqlQueryObject.addWhereCondition(COLUMN_VERSIONE_SERVIZIO+CONDITION_PARAM);
			params.add(idServizio.getVersione());
		}
	}
	private static void buildFiltroScadenza(ISQLQueryObject sqlQueryObject, List<Object> params, InterazioniAsincronePDNDFiltro filtro) throws SQLQueryObjectException {
		Timestamp riferimento = new Timestamp(filtro.getDataRiferimentoScadenza()!=null ? filtro.getDataRiferimentoScadenza().getTime() : System.currentTimeMillis());
		if(filtro.getScadute().booleanValue()) {
			sqlQueryObject.addWhereCondition(COLUMN_DATA_SCADENZA+"<?");
		}
		else {
			sqlQueryObject.addWhereCondition(false, COLUMN_DATA_SCADENZA+" is null", COLUMN_DATA_SCADENZA+">=?");
		}
		params.add(riferimento);
	}
	private static void addCondition(ISQLQueryObject sqlQueryObject, List<Object> params, String column, String value) throws SQLQueryObjectException {
		if(value!=null && !"".equals(value)) {
			sqlQueryObject.addWhereCondition(column+CONDITION_PARAM);
			params.add(value);
		}
	}
	private static void setParams(PreparedStatement stmt, List<Object> params) throws SQLException {
		int index = 1;
		for (Object o : params) {
			switch (o) {
			case Timestamp t -> stmt.setTimestamp(index++, t);
			case Integer i -> stmt.setInt(index++, i);
			default -> stmt.setString(index++, (String) o);
			}
		}
	}
	
	private static InterazioneAsincronaPDND read(ResultSet rs) throws SQLException, InterazioniAsincronePDNDException {
		InterazioneAsincronaPDND interazione = new InterazioneAsincronaPDND();
		interazione.setId(rs.getLong(COLUMN_ID));
		interazione.setInteractionId(rs.getString(COLUMN_INTERACTION_ID));
		interazione.setRuolo(TipoPdD.toTipoPdD(rs.getString(COLUMN_RUOLO)));
		interazione.setFase(FaseInterazioneAsincronaPDND.toFase(rs.getString(COLUMN_FASE)));
		String tipoFruitore = rs.getString(COLUMN_TIPO_FRUITORE);
		String nomeFruitore = rs.getString(COLUMN_FRUITORE);
		if(tipoFruitore!=null && nomeFruitore!=null) {
			interazione.setFruitore(new IDSoggetto(tipoFruitore, nomeFruitore));
		}
		
		IDSoggetto erogatore = new IDSoggetto(rs.getString(COLUMN_TIPO_EROGATORE), rs.getString(COLUMN_EROGATORE));
		try {
			interazione.setServizio(IDServizioFactory.getInstance().getIDServizioFromValues(rs.getString(COLUMN_TIPO_SERVIZIO), rs.getString(COLUMN_SERVIZIO), 
					erogatore, rs.getInt(COLUMN_VERSIONE_SERVIZIO)));
			
			String tipoServizioCallback = rs.getString(COLUMN_TIPO_SERVIZIO_CALLBACK);
			String servizioCallback = rs.getString(COLUMN_SERVIZIO_CALLBACK);
			Integer versioneServizioCallback = readInteger(rs, COLUMN_VERSIONE_SERVIZIO_CALLBACK);
			if(tipoServizioCallback!=null && servizioCallback!=null && versioneServizioCallback!=null && interazione.getFruitore()!=null) {
				// l'API di callback viene erogata dal fruitore dell'e-service
				interazione.setServizioCallback(IDServizioFactory.getInstance().getIDServizioFromValues(tipoServizioCallback, servizioCallback, 
						interazione.getFruitore(), versioneServizioCallback));
			}
		}catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
		
		interazione.setPurposeId(rs.getString(COLUMN_PURPOSE_ID));
		interazione.setConsumerId(rs.getString(COLUMN_CONSUMER_ID));
		interazione.setClientId(rs.getString(COLUMN_CLIENT_ID));
		interazione.setUrlCallback(rs.getString(COLUMN_URL_CALLBACK));
		interazione.setEntityNumber(readInteger(rs, COLUMN_ENTITY_NUMBER));
		interazione.setTempoMaxCallback(readLong(rs, COLUMN_TEMPO_MAX_CALLBACK));
		interazione.setTempoDisponibilita(readLong(rs, COLUMN_TEMPO_DISPONIBILITA));
		interazione.setConfermaRichiesta(rs.getInt(COLUMN_CONFERMA_RICHIESTA) == CostantiDB.TRUE);
		interazione.setLimiteEntita(readInteger(rs, COLUMN_LIMITE_ENTITA));
		interazione.setNumeroGetResource(rs.getInt(COLUMN_NUMERO_GET_RESOURCE));
		interazione.setIdTransazioneStart(rs.getString(COLUMN_ID_TRANSAZIONE_START));
		interazione.setDataStart(rs.getTimestamp(COLUMN_DATA_START));
		interazione.setDataCallback(rs.getTimestamp(COLUMN_DATA_CALLBACK));
		interazione.setDataGetResource(rs.getTimestamp(COLUMN_DATA_GET_RESOURCE));
		interazione.setDataConfirmation(rs.getTimestamp(COLUMN_DATA_CONFIRMATION));
		interazione.setDataScadenza(rs.getTimestamp(COLUMN_DATA_SCADENZA));
		interazione.setDataAggiornamento(rs.getTimestamp(COLUMN_DATA_AGGIORNAMENTO));
		return interazione;
	}
	private static Integer readInteger(ResultSet rs, String column) throws SQLException {
		int v = rs.getInt(column);
		return rs.wasNull() ? null : v;
	}
	private static Long readLong(ResultSet rs, String column) throws SQLException {
		long v = rs.getLong(column);
		return rs.wasNull() ? null : v;
	}
	
	
	
	
	/* **** Aggiornamento delle fasi **** */
	
	/**
	 * Registra la fase callback_invocation. L'aggiornamento avviene solamente se la callback non è già stata registrata
	 * e l'interazione non risulta confermata.
	 * 
	 * @return numero di righe aggiornate (0 se la callback era già stata registrata da una richiesta concorrente)
	 */
	public static int registraCallback(Connection con, String tipoDatabase, long id, IDServizio servizioCallback, Integer entityNumber, 
			Date dataCallback, Date dataScadenza) throws InterazioniAsincronePDNDException {
		PreparedStatement updateStmt = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.PDND_INTERAZIONI_ASYNC);
			sqlQueryObject.addUpdateField(COLUMN_FASE, "?");
			sqlQueryObject.addUpdateField(COLUMN_TIPO_SERVIZIO_CALLBACK, "?");
			sqlQueryObject.addUpdateField(COLUMN_SERVIZIO_CALLBACK, "?");
			sqlQueryObject.addUpdateField(COLUMN_VERSIONE_SERVIZIO_CALLBACK, "?");
			sqlQueryObject.addUpdateField(COLUMN_ENTITY_NUMBER, "?");
			sqlQueryObject.addUpdateField(COLUMN_DATA_CALLBACK, "?");
			sqlQueryObject.addUpdateField(COLUMN_DATA_SCADENZA, "?");
			sqlQueryObject.addUpdateField(COLUMN_DATA_AGGIORNAMENTO, "?");
			sqlQueryObject.addWhereCondition(COLUMN_ID+CONDITION_PARAM);
			sqlQueryObject.addWhereIsNullCondition(COLUMN_DATA_CALLBACK);
			sqlQueryObject.addWhereIsNullCondition(COLUMN_DATA_CONFIRMATION);
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLUpdate();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			updateStmt.setString(index++, FaseInterazioneAsincronaPDND.CALLBACK_INVOCATION.getValore());
			updateStmt.setString(index++, servizioCallback!=null ? servizioCallback.getTipo() : null);
			updateStmt.setString(index++, servizioCallback!=null ? servizioCallback.getNome() : null);
			setInteger(updateStmt, index++, servizioCallback!=null ? servizioCallback.getVersione() : null);
			setInteger(updateStmt, index++, entityNumber);
			updateStmt.setTimestamp(index++, toTimestamp(dataCallback));
			updateStmt.setTimestamp(index++, toTimestamp(dataScadenza));
			updateStmt.setTimestamp(index++, toTimestamp(dataCallback));
			updateStmt.setLong(index++, id);
			return updateStmt.executeUpdate();
		}
		catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	
	/**
	 * Registra un'invocazione della fase get_resource, che può essere ripetuta più volte (es. scaricamento a blocchi).
	 * L'aggiornamento avviene solamente se l'interazione non risulta confermata; la scadenza non viene modificata,
	 * poiché il tempo di disponibilità della risorsa decorre dalla callback.
	 * 
	 * @return numero di righe aggiornate (0 se l'interazione è stata confermata da una richiesta concorrente)
	 */
	public static int registraGetResource(Connection con, String tipoDatabase, long id, Date dataGetResource) throws InterazioniAsincronePDNDException {
		PreparedStatement updateStmt = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.PDND_INTERAZIONI_ASYNC);
			sqlQueryObject.addUpdateField(COLUMN_FASE, "?");
			sqlQueryObject.addUpdateField(COLUMN_NUMERO_GET_RESOURCE, COLUMN_NUMERO_GET_RESOURCE+"+1");
			sqlQueryObject.addUpdateField(COLUMN_DATA_GET_RESOURCE, "?");
			sqlQueryObject.addUpdateField(COLUMN_DATA_AGGIORNAMENTO, "?");
			sqlQueryObject.addWhereCondition(COLUMN_ID+CONDITION_PARAM);
			sqlQueryObject.addWhereIsNullCondition(COLUMN_DATA_CONFIRMATION);
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLUpdate();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			updateStmt.setString(index++, FaseInterazioneAsincronaPDND.GET_RESOURCE.getValore());
			updateStmt.setTimestamp(index++, toTimestamp(dataGetResource));
			updateStmt.setTimestamp(index++, toTimestamp(dataGetResource));
			updateStmt.setLong(index++, id);
			return updateStmt.executeUpdate();
		}
		catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	
	/**
	 * Registra la fase confirmation, che chiude l'interazione. L'aggiornamento avviene solamente se l'interazione non risulta già confermata.
	 * La scadenza viene impostata alla data di conferma, in modo che la riga venga eliminata dal timer trascorso il periodo di conservazione.
	 * 
	 * @return numero di righe aggiornate (0 se l'interazione è stata confermata da una richiesta concorrente)
	 */
	public static int registraConfirmation(Connection con, String tipoDatabase, long id, Date dataConfirmation) throws InterazioniAsincronePDNDException {
		PreparedStatement updateStmt = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addUpdateTable(CostantiDB.PDND_INTERAZIONI_ASYNC);
			sqlQueryObject.addUpdateField(COLUMN_FASE, "?");
			sqlQueryObject.addUpdateField(COLUMN_DATA_CONFIRMATION, "?");
			sqlQueryObject.addUpdateField(COLUMN_DATA_SCADENZA, "?");
			sqlQueryObject.addUpdateField(COLUMN_DATA_AGGIORNAMENTO, "?");
			sqlQueryObject.addWhereCondition(COLUMN_ID+CONDITION_PARAM);
			sqlQueryObject.addWhereIsNullCondition(COLUMN_DATA_CONFIRMATION);
			sqlQueryObject.setANDLogicOperator(true);
			String updateQuery = sqlQueryObject.createSQLUpdate();
			updateStmt = con.prepareStatement(updateQuery);
			int index = 1;
			Timestamp t = toTimestamp(dataConfirmation);
			updateStmt.setString(index++, FaseInterazioneAsincronaPDND.CONFIRMATION.getValore());
			updateStmt.setTimestamp(index++, t);
			updateStmt.setTimestamp(index++, t);
			updateStmt.setTimestamp(index++, t);
			updateStmt.setLong(index++, id);
			return updateStmt.executeUpdate();
		}
		catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(updateStmt);
		}
	}
	
	
	
	
	/* **** Eliminazione **** */
	
	public static int delete(Connection con, String tipoDatabase, long id) throws InterazioniAsincronePDNDException {
		PreparedStatement deleteStmt = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addDeleteTable(CostantiDB.PDND_INTERAZIONI_ASYNC);
			sqlQueryObject.addWhereCondition(COLUMN_ID+CONDITION_PARAM);
			String deleteQuery = sqlQueryObject.createSQLDelete();
			deleteStmt = con.prepareStatement(deleteQuery);
			deleteStmt.setLong(1, id);
			return deleteStmt.executeUpdate();
		}
		catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(deleteStmt);
		}
	}
	
	/**
	 * Elimina le interazioni la cui scadenza è precedente alla data indicata; per le interazioni senza scadenza
	 * viene considerata la data dell'ultimo aggiornamento.
	 * 
	 * @param dataLimite data ottenuta sottraendo alla data attuale il periodo di conservazione
	 * @return numero di righe eliminate
	 */
	public static int deleteScadute(Connection con, String tipoDatabase, Date dataLimite) throws InterazioniAsincronePDNDException {
		PreparedStatement deleteStmt = null;
		try {
			ISQLQueryObject sqlQueryObject = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObject.addDeleteTable(CostantiDB.PDND_INTERAZIONI_ASYNC);
			
			ISQLQueryObject sqlQueryObjectScadenza = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectScadenza.addWhereIsNotNullCondition(COLUMN_DATA_SCADENZA);
			sqlQueryObjectScadenza.addWhereCondition(COLUMN_DATA_SCADENZA+"<?");
			sqlQueryObjectScadenza.setANDLogicOperator(true);
			
			ISQLQueryObject sqlQueryObjectSenzaScadenza = SQLObjectFactory.createSQLQueryObject(tipoDatabase);
			sqlQueryObjectSenzaScadenza.addWhereIsNullCondition(COLUMN_DATA_SCADENZA);
			sqlQueryObjectSenzaScadenza.addWhereCondition(COLUMN_DATA_AGGIORNAMENTO+"<?");
			sqlQueryObjectSenzaScadenza.setANDLogicOperator(true);
			
			sqlQueryObject.addWhereCondition(false, sqlQueryObjectScadenza.createSQLConditions(), sqlQueryObjectSenzaScadenza.createSQLConditions());
			String deleteQuery = sqlQueryObject.createSQLDelete();
			deleteStmt = con.prepareStatement(deleteQuery);
			Timestamp t = toTimestamp(dataLimite);
			deleteStmt.setTimestamp(1, t);
			deleteStmt.setTimestamp(2, t);
			return deleteStmt.executeUpdate();
		}
		catch(Exception e) {
			throw new InterazioniAsincronePDNDException(e.getMessage(),e);
		}
		finally {
			JDBCUtilities.closeResources(deleteStmt);
		}
	}
	
	
	
	
	/* **** Utilities **** */
	
	private static Timestamp toTimestamp(Date d) {
		return d!=null ? new Timestamp(d.getTime()) : null;
	}
	private static void setInteger(PreparedStatement stmt, int index, Integer value) throws SQLException {
		if(value!=null) {
			stmt.setInt(index, value);
		}
		else {
			stmt.setNull(index, Types.INTEGER);
		}
	}
}
