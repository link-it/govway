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

package org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.raggruppamento;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils.PolicyAlias;
import org.slf4j.Logger;

/**
* EventiUtils
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class EventiUtils {

	private static Logger log() {
		return ConfigLoader.getLoggerRateLimiting();
	}
	
	public static void checkEventConViolazioneRL(List<Map<String, Object>> events, PolicyAlias policyAlias, String idServizio, LocalDateTime dataSpedizione, Optional<String> gruppo, Optional<String> groupBy,
			Logger log) {
						
		// 		Verifico che gli eventi di violazione e risoluzione siano stati scritti sul db
		
		log().info(events.toString());
		
		assertEquals(true, findEventRLViolato(events, policyAlias, idServizio, gruppo, groupBy, log, true));
		assertEquals(true, findEventRLViolazioneRisolta(events, policyAlias, idServizio, gruppo, groupBy, log, true));
	}
	
	

	public static boolean findEventRLViolato(List<Map<String,Object>> events, PolicyAlias policy, String idServizio, Optional<String> gruppo, Optional<String> groupBy, Logger log, boolean policyApi) {
		return findEventRLViolato(events, policy.toString(), idServizio, gruppo, groupBy, log, policyApi);
	}
	public static boolean findEventRLViolato(List<Map<String,Object>> events, String policyAlias, String idServizio, Optional<String> gruppo, Optional<String> groupBy, Logger log, boolean policyApi) {
		
		log.debug("======== VERIFICA findEventRLViolato ========");
		
		if(events==null) 
		{
			log.debug("Eventi null");
			return false;
		}
		
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
			
			boolean contains4 = false;
			if (groupBy.isPresent()) {
				if(id_configurazione!=null) {
					contains4 = id_configurazione.contains(groupBy.get());
				}
				match = match && contains4;
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
				if (groupBy.isPresent()) {
					log.debug("\n\tverifica4 containsGroupBy("+groupBy.get()+")="+contains4+"");
				}
			}
			else {
				log.debug("id_configurazione='"+id_configurazione+"'");
			}
			log.debug("match="+match);
			
			return match;
		});
	
	}


	public static boolean findEventRLViolazioneRisolta(List<Map<String,Object>> events, PolicyAlias policy, String idServizio, Optional<String> gruppo, Optional<String> groupBy, Logger log, boolean policyApi) {
		return findEventRLViolazioneRisolta(events, policy.toString(), idServizio, gruppo, groupBy, log, policyApi);
	}
	public static boolean findEventRLViolazioneRisolta(List<Map<String,Object>> events, String policyAlias, String idServizio, Optional<String> gruppo, Optional<String> groupBy, Logger log, boolean policyApi) {
		
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
			
			boolean contains4 = false;
			if (groupBy.isPresent()) {
				if(id_configurazione!=null) {
					contains4 = id_configurazione.contains(groupBy.get());
				}
				match = match && contains4;
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
				if (groupBy.isPresent()) {
					log.debug("\n\tverifica4 containsGroupBy("+groupBy.get()+")="+contains4+"");
				}
			}
			else {
				log.debug("id_configurazione='"+id_configurazione+"'");
			}
			log.debug("match="+match);
					
			return match;
		});
	
	}

}
