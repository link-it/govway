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

package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DbUtils;
import org.openspcoop2.utils.Utilities;
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
			long esitoExpected, String msgErrore) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			DBVerifier._verify(idTransazione, 
					esitoExpected, msgErrore);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				DBVerifier._verify(idTransazione, 
						esitoExpected, msgErrore);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					DBVerifier._verify(idTransazione, 
							esitoExpected, msgErrore);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					DBVerifier._verify(idTransazione, 
							esitoExpected, msgErrore);
				}
			}
		}
	}
	
	private static void _verify(String idTransazione, 
			long esitoExpected, String msgErrore) throws Exception  {
		
		
		String query = "select count(*) from transazioni where id = ?";
		log().info(query);
		
		int count = dbUtils().readValue(query, Integer.class, idTransazione);
		assertEquals("IdTransazione: "+idTransazione, 1, count);

		
		
		query = "select esito from transazioni where id = ?";
		log().info(query);
		
		String msg = "IdTransazione: "+idTransazione;
		
		List<Map<String, Object>> rows = dbUtils().readRows(query, idTransazione);
		assertNotNull(msg, rows);
		assertEquals(msg, 1, rows.size());
					
		Long esito = null;
		Map<String, Object> row = rows.get(0);
		for (String key : row.keySet()) {
			log().debug("Row["+key+"]="+row.get(key));
		}
	
		Object oEsito = row.get("esito");
		assertNotNull(msg,oEsito);
		assertTrue(msg+" oEsito classe '"+oEsito.getClass().getName()+"'", (oEsito instanceof Integer || oEsito instanceof Long));
		if(oEsito instanceof Integer) {
			esito = Long.valueOf( (Integer)oEsito );
		}
		else {
			esito = (Long)oEsito;
		}
		assertEquals(msg,esitoExpected, esito.longValue());

		
		
		// diagnostici
		
		if(msgErrore!=null) {
			query = "select count(*) from msgdiagnostici where id_transazione = ? and messaggio LIKE '%"+msgErrore+"%'";
			log().info(query);
		
			count = dbUtils().readValue(query, Integer.class, idTransazione);
			assertTrue(msg+" Cerco dettaglio '"+msgErrore+"'; count trovati: "+count+"", (count>0));
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
		List<String> ids = new ArrayList<String>();
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
}
