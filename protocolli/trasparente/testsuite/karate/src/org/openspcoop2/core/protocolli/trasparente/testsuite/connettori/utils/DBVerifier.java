/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DbUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils;
import org.openspcoop2.pdd.core.controllo_traffico.PolicyTimeoutConfig;
import org.openspcoop2.pdd.core.controllo_traffico.ReadTimeoutConfigurationUtils;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDati;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.sql.ISQLQueryObject;
import org.openspcoop2.utils.sql.LikeConfig;
import org.openspcoop2.utils.sql.SQLObjectFactory;
import org.openspcoop2.utils.sql.SQLQueryObjectException;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.slf4j.Logger;

/**
* DBVerifier
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class DBVerifier {
	
	private static DbUtils dbUtils() {
		return ConfigLoader.getDbUtils();
	}
	
	private static Logger log() {
		return ConfigLoader.getLoggerCore();
	}
	
	public static void verify(String idTransazione, 
			long esitoExpected) throws AssertionError, SQLQueryObjectException  {
		verify(idTransazione, esitoExpected, "", null);
	}
	
	public static void verify(String idTransazione, 
			long esitoExpected, HttpLibraryMode mode) throws AssertionError, SQLQueryObjectException  {
		verify(idTransazione, esitoExpected, "", mode);
	}
	
	public static void verify(String idTransazione, 
			long esitoExpected, String msgErrore, HttpLibraryMode mode) throws AssertionError, SQLQueryObjectException  {
		Pattern msgPattern = null;
		if (msgErrore != null && !msgErrore.isBlank())
			msgPattern = Pattern.compile(".*\\Q" + msgErrore + "\\E.*", Pattern.DOTALL);
		verify(idTransazione, esitoExpected, msgPattern, mode);
	}
	
	public static void verify(String idTransazione, 
			long esitoExpected, Pattern msgErrore, HttpLibraryMode mode) throws AssertionError, SQLQueryObjectException  {
		
		int[] timeouts = {100, 250, 500, 2000, 5000};
		int index = 0;
		boolean err = true;
		List<Pattern> patterns = new ArrayList<>();
		if (mode != null)
			patterns.add(mode.getExpectedMessage());
		if (msgErrore != null)
			patterns.add(msgErrore);
		
		// se non trovo nessun messaggio aspetto che magari govway non ha ancora scritto il messaggio sul db
		while (err && index < timeouts.length) {
			
			Utilities.sleep(timeouts[index++]); 
			
			try {
				DBVerifier.verifyConnettoreResponse(idTransazione, esitoExpected, patterns);
				err = false;
			} catch (Throwable e) {
				log().warn("tentativo fallito: {}", e.getCause(), e);
				err = true;
				if (index >= timeouts.length)
					throw e;
			}
		}
	}
	
	public static void verify(String idTransazione, 
			long esitoExpected, String msgErrore) throws Exception  {
		
		Utilities.sleep(100); 
		try {
			DBVerifier.engineVerify(idTransazione, 
					esitoExpected, msgErrore,
					false);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier.engineVerify(idTransazione, 
						esitoExpected, msgErrore,
						false);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier.engineVerify(idTransazione, 
							esitoExpected, msgErrore,
							false);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier.engineVerify(idTransazione, 
							esitoExpected, msgErrore,
							false);
				}
			}
		}
	}
	
	public static void verify(String idTransazione, 
			long esitoExpected, String msgErrore,
			boolean checkMsgErroreCaseInsensitive) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier.engineVerify(idTransazione, 
					esitoExpected, msgErrore,
					checkMsgErroreCaseInsensitive);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier.engineVerify(idTransazione, 
						esitoExpected, msgErrore,
						checkMsgErroreCaseInsensitive);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier.engineVerify(idTransazione, 
							esitoExpected, msgErrore,
							checkMsgErroreCaseInsensitive);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier.engineVerify(idTransazione, 
							esitoExpected, msgErrore,
							checkMsgErroreCaseInsensitive);
				}
			}
		}
	}
	
	
	private static void verifyIdTransazione(String idTransazione) throws SQLQueryObjectException {
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(ConfigLoader.getDbUtils().tipoDatabase);
		query.addFromTable(CostantiDB.TRANSAZIONI);
		query.addSelectCountField(CostantiDB.COLUMN_ID);
		query.addWhereCondition(CostantiDB.COLUMN_ID + " = ?");
		query.setANDLogicOperator(true);
		
		String queryStr = query.createSQLQuery();
		
		log().info("verifico id transazione[{}]: {}", idTransazione, queryStr);
		int count = dbUtils().readValue(queryStr, Integer.class, idTransazione);
		assertEquals("IdTransazione: "+idTransazione, 1, count);
	}
	
	private static void verifyEsito(String idTransazione, long esitoExpected) throws SQLQueryObjectException {
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(ConfigLoader.getDbUtils().tipoDatabase);
		query.addFromTable(CostantiDB.TRANSAZIONI);
		query.addSelectField("esito");
		query.addWhereCondition(CostantiDB.COLUMN_ID + " = ?");
		query.setANDLogicOperator(true);
		
		String queryStr = query.createSQLQuery();
		
		log().info("verifico esito[{}], transazione[{}]: {}", esitoExpected, idTransazione, queryStr);
		Object esito = dbUtils().readValue(queryStr, Object.class, idTransazione);
		
		assertTrue("transazione: " + idTransazione + " tipo: ["+(esito!=null ? esito.getClass().getName() : null)+"], esito intero o long o java.math.BigDecimal", 
				(esito instanceof Integer) || (esito instanceof Long) || (esito instanceof java.math.BigDecimal));
		
		Long esitoValue = null;
		if (esito instanceof java.math.BigDecimal bd) {
			esitoValue = bd.longValue();
		}
		else if (esito instanceof Integer integer) {
			esitoValue = Long.valueOf(integer);
		} else {
			esitoValue = (Long) esito;
		}
		
		assertEquals("transazione: " + idTransazione + ", esito:" + esitoExpected, esitoExpected, esitoValue.longValue());
	}
	
	private static void checkMsg(String idTransazione, List<Pattern> patternList) throws SQLQueryObjectException {
		ISQLQueryObject query = SQLObjectFactory.createSQLQueryObject(ConfigLoader.getDbUtils().tipoDatabase);
		query.addFromTable(CostantiDB.MSG_DIAGNOSTICI);
		query.addSelectField(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO);
		query.addWhereCondition(CostantiDB.MSG_DIAGNOSTICI_COLUMN_ID_TRANSAZIONE + " = ?");
		query.setANDLogicOperator(true);
		log().info(query.createSQLQuery());
		
		List<Map<String, Object>> msgs = ConfigLoader.getDbUtils().readRows(query.createSQLQuery(), idTransazione);
		Set<Pattern> patternSet = new HashSet<>(patternList);
		
		for (Map<String, Object> msg : msgs) {
			assertTrue(msg.containsKey(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO));
			String message = (String) msg.get(CostantiDB.MSG_DIAGNOSTICI_COLUMN_MESSAGGIO);
			
			log().debug(message);
			
			patternSet.removeIf(pattern -> pattern.matcher(message).matches());
		}
		
		if (log().isInfoEnabled())
			log().info("pattern non trovati: [{}]", String.join(", ", patternSet.stream().map(Pattern::toString).toList()));
		assertTrue("Alcuni messaggi diagnositici non sono stati trovati", patternSet.isEmpty());
	}
	
	private static void verifyConnettoreResponse(String idTransazione, 
			long esitoExpected, List<Pattern> patterns) throws SQLQueryObjectException {
		
		verifyIdTransazione(idTransazione);
		
		verifyEsito(idTransazione, esitoExpected);
		
		checkMsg(idTransazione, patterns);
	}
	
	private static void engineVerify(String idTransazione, 
			long esitoExpected, String msgErrore,
			boolean checkMsgErroreCaseInsensitive) throws Exception  {
		
		
		verifyIdTransazione(idTransazione);
		
		verifyEsito(idTransazione, esitoExpected);
		
		String query;
		// diagnostici
		
		if(msgErrore!=null) {
			if(checkMsgErroreCaseInsensitive) {
				ISQLQueryObject sql = SQLObjectFactory.createSQLQueryObject(dbUtils().tipoDatabase);
				sql.addFromTable("msgdiagnostici");
				sql.addSelectCountField("conteggio");
				sql.setANDLogicOperator(true);
				sql.addWhereCondition("id_transazione = ?");
				sql.addWhereLikeCondition("messaggio", msgErrore, LikeConfig.contains(true, true));
				query = sql.createSQLQuery();
			}
			else {
				query = "select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '%"+msgErrore+"%'";
			}
			log().info(query);
		
			int count = dbUtils().readValue(query, Integer.class, idTransazione);
			assertTrue(" Cerco dettaglio '"+msgErrore+"'; count trovati: "+count+"", (count>0));
		}

	}
	
	
	public static String getIdTransazioneByIdApplicativoRichiesta(String idApplicativo) throws Exception  {
		return getIdTransazioneByIdApplicativoRichiesta(idApplicativo, null, true);
	}
	public static String getIdTransazioneByIdApplicativoRichiesta(String idApplicativo, Date now) throws Exception  {
		return getIdTransazioneByIdApplicativoRichiesta(idApplicativo, now, true);
	}
	public static String getIdTransazioneByIdApplicativoRichiesta(String idApplicativo, boolean assertNotNull) throws Exception  {
		return getIdTransazioneByIdApplicativoRichiesta(idApplicativo, null, assertNotNull);
	}
	public static String getIdTransazioneByIdApplicativoRichiesta(String idApplicativo, Date now, boolean assertNotNull) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			return DBVerifier._getIdTransazione(idApplicativo, true, now, assertNotNull);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				return DBVerifier._getIdTransazione(idApplicativo, true, now, assertNotNull);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					return DBVerifier._getIdTransazione(idApplicativo, true, now, assertNotNull);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					return DBVerifier._getIdTransazione(idApplicativo, true, now, assertNotNull);
				}
			}
		}
	}
	public static String getIdTransazioneByIdApplicativoRisposta(String idApplicativo) throws Exception  {
		return getIdTransazioneByIdApplicativoRisposta(idApplicativo, null, true);
	}
	public static String getIdTransazioneByIdApplicativoRisposta(String idApplicativo, Date now) throws Exception  {
		return getIdTransazioneByIdApplicativoRisposta(idApplicativo, now, true);
	}
	public static String getIdTransazioneByIdApplicativoRisposta(String idApplicativo, boolean assertNotNull) throws Exception  {
		return getIdTransazioneByIdApplicativoRisposta(idApplicativo, null, assertNotNull);
	}
	public static String getIdTransazioneByIdApplicativoRisposta(String idApplicativo, Date now, boolean assertNotNull) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			return DBVerifier._getIdTransazione(idApplicativo, false, now, assertNotNull);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				return DBVerifier._getIdTransazione(idApplicativo, false, now, assertNotNull);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					return DBVerifier._getIdTransazione(idApplicativo, false, now, assertNotNull);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					return DBVerifier._getIdTransazione(idApplicativo, false, now, assertNotNull);
				}
			}
		}
	}
	
	private static String _getIdTransazione(String idApplicativo, boolean richiesta, Date now, boolean assertNotNull) throws Exception  {
		
		String colonna = richiesta ? "id_correlazione_applicativa" : "id_correlazione_risposta";
		String query = "select id from transazioni where "+colonna+" = ?";
		Timestamp t = null;
		if(now!=null) {
			t = new Timestamp(now.getTime());
			query = query + " and data_ingresso_richiesta>=?";
		}
		log().info(query);
		
		String idTransazione = null; 
		try {
			if(t!=null) {
				idTransazione = dbUtils().readValue(query, String.class, idApplicativo, t);
			}
			else {
				idTransazione = dbUtils().readValue(query, String.class, idApplicativo);
			}
		}catch(org.springframework.dao.EmptyResultDataAccessException tNotFound) {
			if(assertNotNull) {
				throw tNotFound;
			}
		}
		if(assertNotNull) {
			assertNotNull("IdTransazione letto da idApplicativo: "+idApplicativo, idTransazione);
		}

		return idTransazione;

	}
	
	
	
	
	public static List<String> getIdsTransazioneByIdApplicativoRichiesta(String idApplicativo) throws Exception  {
		return getIdsTransazioneByIdApplicativoRichiesta(idApplicativo, null);
	}
	public static List<String> getIdsTransazioneByIdApplicativoRichiesta(String idApplicativo, Date now) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			return DBVerifier._getIdsTransazione(idApplicativo, true, now);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				return DBVerifier._getIdsTransazione(idApplicativo, true, now);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					return DBVerifier._getIdsTransazione(idApplicativo, true, now);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					return DBVerifier._getIdsTransazione(idApplicativo, true, now);
				}
			}
		}
	}
	public static List<String> getIdsTransazioneByIdApplicativoRisposta(String idApplicativo) throws Exception  {
		return getIdsTransazioneByIdApplicativoRisposta(idApplicativo, null);
	}
	public static List<String> getIdsTransazioneByIdApplicativoRisposta(String idApplicativo, Date now) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			return DBVerifier._getIdsTransazione(idApplicativo, false, now);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				return DBVerifier._getIdsTransazione(idApplicativo, false, now);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					return DBVerifier._getIdsTransazione(idApplicativo, false, now);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					return DBVerifier._getIdsTransazione(idApplicativo, false, now);
				}
			}
		}
	}
	private static List<String> _getIdsTransazione(String idApplicativo, boolean richiesta, Date now) throws Exception  {
		
		String colonna = richiesta ? "id_correlazione_applicativa" : "id_correlazione_risposta";
		String query = "select id from transazioni where "+colonna+" = ?";
		Timestamp t = null;
		if(now!=null) {
			t = new Timestamp(now.getTime());
			query = query + " and data_ingresso_richiesta>=?";
		}
		query = query + " order by data_ingresso_richiesta ASC";
		log().info(query);
		
		List<Map<String, Object>> rows = null;
		if(t!=null) {
			rows = dbUtils().readRows(query, idApplicativo, t);
		}
		else {
			rows = dbUtils().readRows(query, idApplicativo);
		}
		List<String> ids = new ArrayList<>();
		if(rows!=null) {
			for (Map<String, Object> row : rows) {
				for (String key : row.keySet()) {
					log().debug("Row["+key+"]="+row.get(key));
					ids.add((String)row.get(key));
				}
			}
		}
		
		return ids;

	}
	
	
	public static String getIdCorrelazioneApplicativaRichiesta(String idTransazione) throws Exception  {
		return _getIdCorrelazioneApplicativa(idTransazione, true, true);
	}
	public static String getIdCorrelazioneApplicativaRichiesta(String idTransazione, boolean assertNotNull) throws Exception  {
		return _getIdCorrelazioneApplicativa(idTransazione, true, assertNotNull);
	}
	public static String getIdCorrelazioneApplicativaRisposta(String idTransazione) throws Exception  {
		return _getIdCorrelazioneApplicativa(idTransazione, false, true);
	}
	public static String getIdCorrelazioneApplicativaRisposta(String idTransazione, boolean assertNotNull) throws Exception  {
		return _getIdCorrelazioneApplicativa(idTransazione, false, assertNotNull);
	}
	public static String _getIdCorrelazioneApplicativa(String idTransazione, boolean richiesta, boolean assertNotNull) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			return DBVerifier._getIdCorrelazioneApplicativaEngine(idTransazione, richiesta, assertNotNull);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				return DBVerifier._getIdCorrelazioneApplicativaEngine(idTransazione, richiesta, assertNotNull);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					return DBVerifier._getIdCorrelazioneApplicativaEngine(idTransazione, richiesta, assertNotNull);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					return DBVerifier._getIdCorrelazioneApplicativaEngine(idTransazione, richiesta, assertNotNull);
				}
			}
		}
	}
	private static String _getIdCorrelazioneApplicativaEngine(String idTransazione, boolean richiesta, boolean assertNotNull) throws Exception  {
		
		String colonna = richiesta ? "id_correlazione_applicativa" : "id_correlazione_risposta";
		String query = "select "+colonna+" from transazioni where id = ?";
		log().info(query);
		
		String idCorrelazione = null;
		try {
			idCorrelazione = dbUtils().readValue(query, String.class, idTransazione);
		}catch(org.springframework.dao.EmptyResultDataAccessException tNotFound) {
			if(assertNotNull) {
				throw tNotFound;
			}
		}
		
		if(assertNotNull) {
			assertNotNull("IdCorrelazione letto da idTransazione: "+idTransazione, idCorrelazione);
		}
		
		return idCorrelazione;

	}
	
	
	
	
	
	
	
	
	
	public static void checkEventiConViolazioneTimeout(String idServizio, TipoEvento tipoEvento, Optional<String> gruppo, Optional<String> connettore, PolicyTimeoutConfig config, 
			PolicyDati dati,
			String descrizioneEvento, LocalDateTime dataSpedizione,
			HttpResponse response, Logger log) {
				
		EventiUtils.waitForDbEvents();
		
		//  Devo trovare tra le transazioni generate dalle richieste, almeno una transazione che abbia entrambe le violazioni
		
		log.debug("======== VERIFICA checkEventiConViolazioneTimeout ========");
		
		if(response!=null) {
			boolean found = EventiUtils.testCredenzialiMittenteTransazione(
					response.getHeaderFirstValue(Headers.TransactionId),
								evento -> {						
									log().info(evento.toString());
									String credenziale = (String) evento.get("credenziale");
									StringBuilder sb = new StringBuilder();
									sb.append("##"+tipoEvento.toString()+"_Violazione_"+idServizio);
									if(gruppo.isPresent()) {
										sb.append(" (gruppo '"+gruppo.get()+"')");
									}
									if(connettore.isPresent()) {
										sb.append(" (connettore '"+connettore.get()+"')");
									}
									if(config!=null) {
										ReadTimeoutConfigurationUtils.addPolicyInfo(sb, config);
									}
									sb.append("##");
									String check = sb.toString();
									boolean match = credenziale.contains(check);
									
									log.debug("credenziale='"+credenziale+"'"+
											"\n\tverifica contains("+check+")="+match);
									
									return match;
								});
			assertEquals(found, true);
		}
	
		// 		Verifico che gli eventi di violazione e risoluzione siano stati scritti sul db
		
		try {
			checkEventTimeout(dataSpedizione, idServizio, tipoEvento, gruppo, connettore, config, 
					dati,
					descrizioneEvento, log);
		}catch(Throwable t) {
			throw new RuntimeException(t.getMessage(),t);
		}
	}
	
	private static void checkEventTimeout(LocalDateTime dataSpedizione, String idServizio, TipoEvento tipoEvento, Optional<String> gruppo, Optional<String> connettore, PolicyTimeoutConfig config, 
			PolicyDati dati,
			String descrizioneEvento, Logger log) throws Throwable  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			List<Map<String, Object>> events = EventiUtils.getNotificheEventi(dataSpedizione);
			if(events!=null) {
				log().info(events.toString());
			}
			else {
				log().info("events empty");
			}

			assertEquals(true, org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.EventiUtils.findEventTimeoutViolato(events, idServizio, tipoEvento, gruppo, connettore, config, dati, descrizioneEvento, log));
			assertEquals(true, org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.EventiUtils.findEventTimeoutViolazioneRisolta(events, idServizio,tipoEvento, gruppo, connettore, config, dati, descrizioneEvento, log));	
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				List<Map<String, Object>> events = EventiUtils.getNotificheEventi(dataSpedizione);		
				if(events!=null) {
					log().info(events.toString());
				}
				else {
					log().info("events empty");
				}

				assertEquals(true, org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.EventiUtils.findEventTimeoutViolato(events, idServizio, tipoEvento, gruppo, connettore, config, dati, descrizioneEvento, log));
				assertEquals(true, org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.EventiUtils.findEventTimeoutViolazioneRisolta(events, idServizio,tipoEvento, gruppo, connettore, config, dati, descrizioneEvento, log));	
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					List<Map<String, Object>> events = EventiUtils.getNotificheEventi(dataSpedizione);		
					if(events!=null) {
						log().info(events.toString());
					}
					else {
						log().info("events empty");
					}

					assertEquals(true, org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.EventiUtils.findEventTimeoutViolato(events, idServizio, tipoEvento, gruppo, connettore, config, dati, descrizioneEvento, log));
					assertEquals(true, org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.EventiUtils.findEventTimeoutViolazioneRisolta(events, idServizio,tipoEvento, gruppo, connettore, config, dati, descrizioneEvento, log));	
				}catch(Throwable t3) {
					Utilities.sleep(10000);
					List<Map<String, Object>> events = EventiUtils.getNotificheEventi(dataSpedizione);		
					if(events!=null) {
						log().info(events.toString());
					}
					else {
						log().info("events empty");
					}

					assertEquals(true, org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.EventiUtils.findEventTimeoutViolato(events, idServizio, tipoEvento, gruppo, connettore, config, dati, descrizioneEvento, log));
					assertEquals(true, org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.EventiUtils.findEventTimeoutViolazioneRisolta(events, idServizio,tipoEvento, gruppo, connettore, config, dati, descrizioneEvento, log));	
				}
			}
		}
	}
}
