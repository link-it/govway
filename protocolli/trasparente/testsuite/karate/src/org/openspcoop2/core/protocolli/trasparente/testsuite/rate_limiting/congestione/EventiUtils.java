/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.congestione;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Predicate;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.DbUtils;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Headers;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.slf4j.Logger;

/**
* EventiUtils
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class EventiUtils {

	
	public static void waitForDbEvents() {
		int eventi_db_delay = Integer.valueOf(System.getProperty("eventi_db_delay"));
		int to_sleep = eventi_db_delay*1000*4;
		
		log().debug("Attendo " + to_sleep/1000 + " secondi, affinchè vengano registrati gli eventi sul db...");
		org.openspcoop2.utils.Utilities.sleep(to_sleep);
	}
	
	
	
	public static Optional<String> getEventiGestione(String transactionId) {
		String query = "select eventi_gestione from transazioni WHERE id='"+transactionId+"'";
		log().info(query);
		
		return Optional.ofNullable((String) dbUtils().readRow(query).get("eventi_gestione"));
	}
	
	
	
	public static Map<String,Object> getCredenzialiMittenteById(String evento_gestione_id) {
		String query = "select * from credenziale_mittente where id="+evento_gestione_id;
		log().info(query);
		return dbUtils().readRow(query);
	}
	
	
	
	public static Optional<Map<String,Object>> getCredenzialiMittenteByTransactionId(String transactionId) {
		return getEventiGestione(transactionId)
			.map(EventiUtils::getCredenzialiMittenteById);
	}
	
	
	
	public static boolean testCredenzialiMittenteTransazione(String transactionId, Predicate<Map<String,Object>> predicate) {
		return getCredenzialiMittenteByTransactionId(transactionId)
				.filter( predicate )
				.isPresent();
		
	}
	
	
	public static List<Map<String,Object>> getNotificheEventi(LocalDateTime dataSpedizione) {
		
		ZonedDateTime zdt = dataSpedizione.atZone(ZoneId.systemDefault());
		java.sql.Timestamp filtroData = new java.sql.Timestamp(zdt.toInstant().toEpochMilli());
		
		String query = "select * from notifiche_eventi where ora_registrazione >= ?";
		log().info(query);
		
		return dbUtils().readRows(query, filtroData);
	}
	
	
	
	private static Logger log() {
		return ConfigLoader.getLoggerRateLimiting();
	}
	
	
	private static DbUtils dbUtils() {
		return ConfigLoader.getDbUtils();
	}



	public static boolean findEventCongestioneRisolta(List<Map<String, Object>> events, Logger log) {
		
		log.debug("======== VERIFICA findEventCongestioneRisolta ========");
		
		return events.stream()
			.anyMatch( ev -> {
				Object tipo = ev.get("tipo");
				Object codice = ev.get("codice");
				Object severita = ev.get("severita");
				boolean match = tipo.equals("ControlloTraffico_SogliaCongestione") &&
						codice.equals("ViolazioneRisolta") &&
						severita.equals(3);
				
				log.debug("tipo='"+tipo+"' confronto con 'ControlloTraffico_SogliaCongestione': "+tipo.equals("ControlloTraffico_SogliaCongestione"));
				log.debug("codice='"+codice+"' confronto con 'ViolazioneRisolta': "+codice.equals("ViolazioneRisolta"));
				log.debug("severita='"+severita+"' confronto con 3: "+severita.equals(3));
				log.debug("match="+match);
				
				return match;
			});
	}



	public static boolean findEventCongestioneViolata(List<Map<String, Object>> events, Logger log) {
		
		log.debug("======== VERIFICA findEventCongestioneViolata ========");
		
		String descrAttesa = "E' stata rilevata una congestione del sistema in seguito al superamento della soglia del";
		
		return events.stream()
				.anyMatch( ev -> {
					Object tipo = ev.get("tipo");
					Object codice = ev.get("codice");
					Object severita = ev.get("severita");
					String descrizione = ((String) ev.get("descrizione"));
					boolean descrizioneCheck = false;
					if(descrizione!=null) {
						descrizioneCheck = descrizione.startsWith(descrAttesa);
					}
					boolean match = tipo.equals("ControlloTraffico_SogliaCongestione") &&
							codice.equals("Violazione") &&
							severita.equals(2) &&
							descrizioneCheck;
					
					log.debug("tipo='"+tipo+"' confronto con 'ControlloTraffico_SogliaCongestione': "+tipo.equals("ControlloTraffico_SogliaCongestione"));
					log.debug("codice='"+codice+"' confronto con 'Violazione': "+codice.equals("Violazione"));
					log.debug("severita='"+severita+"' confronto con 2: "+severita.equals(2));
					if(descrizione!=null) {
						log.debug("descrizione='"+descrizione+"' confronto con '"+descrAttesa+"': "+descrizioneCheck);
					}
					else {
						log.debug("descrizione='"+descrizione+"'");
					}
					log.debug("match="+match);
					
					return match;
				});
	}



	public static boolean findEventRLViolato(List<Map<String,Object>> events, PolicyAlias policy, String idServizio, Optional<String> gruppo, Logger log, boolean policyApi) {
		return findEventRLViolato(events, policy.toString(), idServizio, gruppo, log, policyApi);
	}
	public static boolean findEventRLViolato(List<Map<String,Object>> events, String policyAlias, String idServizio, Optional<String> gruppo, Logger log, boolean policyApi) {
		
		log.debug("======== VERIFICA findEventRLViolato ========");
		
		return events.stream()
		.anyMatch( ev -> {
			Object tipo = ev.get("tipo");
			Object codice = ev.get("codice");
			Object severita = ev.get("severita");
			String id_configurazione = (String) ev.get("id_configurazione");
			boolean contains1 = false;
			boolean contains2 = false;
			if(id_configurazione!=null) {
				contains1 = id_configurazione.contains(policyAlias);
				if(policyApi) {
					contains2 = id_configurazione.contains(idServizio);
				}
				else {
					contains2 = true;
				}
			}
			String tipoPolicy = "PolicyAPI";
			if(!policyApi) {
				tipoPolicy = "PolicyGlobale";
			}
			boolean match = tipo.equals("RateLimiting_"+tipoPolicy) &&
					codice.equals("Violazione") &&
					severita.equals(1) && 
					contains1 &&
					contains2;
			
			boolean contains3 = false;
			if (gruppo.isPresent()) {
				if(id_configurazione!=null) {
					contains3 = id_configurazione.contains("gruppo '"+gruppo.get()+"'");
				}
				match = match && contains3;
			}
			
			log.debug("tipo='"+tipo+"' confronto con 'RateLimiting_"+tipoPolicy+"': "+tipo.equals("RateLimiting_"+tipoPolicy));
			log.debug("codice='"+codice+"' confronto con 'Violazione': "+codice.equals("Violazione"));
			log.debug("severita='"+severita+"' confronto con 1: "+severita.equals(1));
			if(id_configurazione!=null) {
				log.debug("id_configurazione='"+id_configurazione+"'"+
						"\n\tverifica1 contains("+policyAlias+")="+contains1+""+
						"\n\tverifica2 contains("+idServizio+")="+contains2+"");
				if (gruppo.isPresent()) {
					log.debug("\n\tverifica3 containsGruppo("+gruppo.get()+")="+contains3+"");
				}
			}
			else {
				log.debug("id_configurazione='"+id_configurazione+"'");
			}
			log.debug("match="+match);
			
			return match;
		});
	
	}


	public static boolean findEventRLViolazioneRisolta(List<Map<String,Object>> events, PolicyAlias policy, String idServizio, Optional<String> gruppo, Logger log, boolean policyApi) {
		return findEventRLViolazioneRisolta(events, policy.toString(), idServizio, gruppo, log, policyApi);
	}
	public static boolean findEventRLViolazioneRisolta(List<Map<String,Object>> events, String policyAlias, String idServizio, Optional<String> gruppo, Logger log, boolean policyApi) {
		
		log.debug("======== VERIFICA findEventRLViolazioneRisolta ========");
		
		return events.stream()
		.anyMatch( ev -> {
			Object tipo = ev.get("tipo");
			Object codice = ev.get("codice");
			Object severita = ev.get("severita");
			String id_configurazione = (String) ev.get("id_configurazione");
			boolean contains1 = false;
			boolean contains2 = false;
			if(id_configurazione!=null) {
				contains1 = id_configurazione.contains(policyAlias);
				if(policyApi) {
					contains2 = id_configurazione.contains(idServizio);
				}
				else {
					contains2 = true;
				}
			}
			String tipoPolicy = "PolicyAPI";
			if(!policyApi) {
				tipoPolicy = "PolicyGlobale";
			}
			boolean match = tipo.equals("RateLimiting_"+tipoPolicy) &&
					codice.equals("ViolazioneRisolta") &&
					severita.equals(3) && 
					contains1 &&
					contains2;
			
			boolean contains3 = false;
			if (gruppo.isPresent()) {
				if(id_configurazione!=null) {
					contains3 = id_configurazione.contains("gruppo '"+gruppo.get()+"'");
				}
				match = match && contains3;
			}
			
			log.debug("tipo='"+tipo+"' confronto con 'RateLimiting_"+tipoPolicy+"': "+tipo.equals("RateLimiting_"+tipoPolicy));
			log.debug("codice='"+codice+"' confronto con 'ViolazioneRisolta': "+codice.equals("ViolazioneRisolta"));
			log.debug("severita='"+severita+"' confronto con 3: "+severita.equals(3));
			if(id_configurazione!=null) {
				log.debug("id_configurazione='"+id_configurazione+"'"+
						"\n\tverifica1 contains("+policyAlias+")="+contains1+""+
						"\n\tverifica2 contains("+idServizio+")="+contains2+"");
				if (gruppo.isPresent()) {
					log.debug("\n\tverifica3 containsGruppo("+gruppo.get()+")="+contains3+"");
				}
			}
			else {
				log.debug("id_configurazione='"+id_configurazione+"'");
			}
			log.debug("match="+match);
					
			return match;
		});
	
	}



	public static void checkEventiCongestioneAttivaConViolazioneRL(String idServizio, LocalDateTime dataSpedizione, Optional<String> gruppo,
			Vector<HttpResponse> responses, Logger log) {
				
		waitForDbEvents();
		
		//  Devo trovare tra le transazioni generate dalle richieste, almeno una transazione che abbia entrambe le violazioni
		
		log.debug("======== VERIFICA checkEventiCongestioneAttivaConViolazioneRL ========");
		
		boolean found = responses
				.stream()
				.anyMatch( r -> {
					return testCredenzialiMittenteTransazione(
							r.getHeaderFirstValue(Headers.TransactionId),
							evento -> {						
								log().info(evento.toString());
								String credenziale = (String) evento.get("credenziale");
								boolean contains1 = credenziale.contains("##ControlloTraffico_SogliaCongestione_Violazione##");
								boolean contains2 = credenziale.contains("##RateLimiting_PolicyAPI_Violazione_RichiesteSimultanee##");
								boolean match = contains1 &&
										contains2;
								
								log.debug("credenziale='"+credenziale+"'"+
										"\n\tverifica1 contains(##ControlloTraffico_SogliaCongestione_Violazione##)="+contains1+""+
										"\n\tverifica2 contains(##RateLimiting_PolicyAPI_Violazione_RichiesteSimultanee##)="+contains2+"");
								log.debug("match="+match);
								
								return match;
							});
				
				});
		assertEquals(found, true);
	
		// 		Verifico che gli eventi di violazione e risoluzione siano stati scritti sul db
		
		List<Map<String, Object>> events = getNotificheEventi(dataSpedizione);		
		log().info(events.toString());
		
		assertEquals(true, findEventCongestioneViolata(events, log));
		assertEquals(true, findEventCongestioneRisolta(events, log));
		assertEquals(true, findEventRLViolato(events, PolicyAlias.RICHIESTE_SIMULTANEE, idServizio, gruppo, log, true));
		assertEquals(true, findEventRLViolazioneRisolta(events, PolicyAlias.RICHIESTE_SIMULTANEE, idServizio, gruppo, log, true));
	}



	public static void checkEventiCongestioneAttiva(LocalDateTime dataSpedizione, Vector<HttpResponse> responses, Logger log) {
	
		waitForDbEvents();
		
		//		Devo trovare tra le transazioni generate dalle richieste, almeno una transazione che abbia la violazione
		
		log.debug("======== VERIFICA checkEventiCongestioneAttiva ========");
		
		boolean found = responses
				.stream()
				.anyMatch( r -> {
					return testCredenzialiMittenteTransazione(
							r.getHeaderFirstValue(Headers.TransactionId),
							evento -> {						
								log().info(evento.toString());
								String credenziale = (String) evento.get("credenziale");
								boolean contains1 = credenziale.contains("##ControlloTraffico_SogliaCongestione_Violazione##");
								boolean match = contains1;
								
								log.debug("credenziale='"+credenziale+"'"+
										"\n\tverifica1 contains(##ControlloTraffico_SogliaCongestione_Violazione##)="+contains1+"");
								log.debug("match="+match);
								
								return match;
							});
				
				});
		
		assertEquals(found, true);
	
		// 		Devo verificare che gli eventi di violazione e risoluzione siano stati scritti sul db
		
		List<Map<String, Object>> events = getNotificheEventi(dataSpedizione);		
		log().info(events.toString());
		
		assertEquals(true, findEventCongestioneViolata(events, log));
		assertEquals(true, findEventCongestioneRisolta(events, log));		
	}

}
