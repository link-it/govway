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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.dimensione_messaggi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DbUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione.EventiUtils;
import org.openspcoop2.utils.Utilities;
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
	
	

	public static String getIdTransazione(String idApplicativo) throws Exception  {
		
		// La scrittura su database avviene dopo aver risposto al client
		
		Utilities.sleep(100); 
		try {
			return DBVerifier._getIdTransazione(idApplicativo);
		}catch(Throwable t) {
			Utilities.sleep(500);
			try {
				return DBVerifier._getIdTransazione(idApplicativo);
			}catch(Throwable t2) {
				Utilities.sleep(2000);
				try {
					return DBVerifier._getIdTransazione(idApplicativo);
				}catch(Throwable t3) {
					Utilities.sleep(5000);
					return DBVerifier._getIdTransazione(idApplicativo);
				}
			}
		}
	}
	
	private static String _getIdTransazione(String idApplicativo) throws Exception  {
		
		
		String query = "select id from transazioni where id_correlazione_applicativa = ?";
		log().info(query);
		
		String idTransazione = dbUtils().readValue(query, String.class, idApplicativo);
		assertNotNull("IdTransazione letto da idApplicativo: "+idApplicativo, idTransazione);

		return idTransazione;

	}
	
	
	public static void checkEventiConViolazioneRL(String idServizio, Optional<String> gruppo, LocalDateTime dataSpedizione,
			HttpResponse response, Logger log, String nomePolicy, boolean policyApi) {
				
		EventiUtils.waitForDbEvents();
		
		//  Devo trovare tra le transazioni generate dalle richieste, almeno una transazione che abbia entrambe le violazioni
		
		log.debug("======== VERIFICA checkEventiCongestioneAttivaConViolazioneRL ========");
		
		boolean found = EventiUtils.testCredenzialiMittenteTransazione(
				response.getHeaderFirstValue(Headers.TransactionId),
							evento -> {						
								log().info(evento.toString());
								String credenziale = (String) evento.get("credenziale");
								String tipoPolicy = "PolicyAPI";
								if(!policyApi) {
									tipoPolicy = "PolicyGlobale";
								}
								String check = "##RateLimiting_"+tipoPolicy+"_Violazione_"+nomePolicy+"##";
								boolean match = credenziale.contains(check);
								
								log.debug("credenziale='"+credenziale+"'"+
										"\n\tverifica contains("+check+")="+match);
								
								return match;
							});
		assertEquals(found, true);
	
		// 		Verifico che gli eventi di violazione e risoluzione siano stati scritti sul db
		
		try {
			checkEventRL(dataSpedizione, nomePolicy, idServizio, log, policyApi);
		}catch(Throwable t) {
			throw new RuntimeException(t.getMessage(),t);
		}
	}
	
	private static void checkEventRL(LocalDateTime dataSpedizione, String nomePolicy, String idServizio, Logger log, boolean policyApi) throws Throwable  {
		
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

			assertEquals(true, EventiUtils.findEventRLViolato(events, nomePolicy, idServizio, Optional.empty(), log, policyApi));
			assertEquals(true, EventiUtils.findEventRLViolazioneRisolta(events, nomePolicy, idServizio, Optional.empty(), log, policyApi));	
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

				assertEquals(true, EventiUtils.findEventRLViolato(events, nomePolicy, idServizio, Optional.empty(), log, policyApi));
				assertEquals(true, EventiUtils.findEventRLViolazioneRisolta(events, nomePolicy, idServizio, Optional.empty(), log, policyApi));	
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

					assertEquals(true, EventiUtils.findEventRLViolato(events, nomePolicy, idServizio, Optional.empty(), log, policyApi));
					assertEquals(true, EventiUtils.findEventRLViolazioneRisolta(events, nomePolicy, idServizio, Optional.empty(), log, policyApi));	
				}catch(Throwable t3) {
					Utilities.sleep(10000);
					List<Map<String, Object>> events = EventiUtils.getNotificheEventi(dataSpedizione);		
					if(events!=null) {
						log().info(events.toString());
					}
					else {
						log().info("events empty");
					}

					assertEquals(true, EventiUtils.findEventRLViolato(events, nomePolicy, idServizio, Optional.empty(), log, policyApi));
					assertEquals(true, EventiUtils.findEventRLViolazioneRisolta(events, nomePolicy, idServizio, Optional.empty(), log, policyApi));	
				}
			}
		}
	}
}
