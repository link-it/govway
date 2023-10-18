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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.pdd.core.controllo_traffico.PolicyTimeoutConfig;
import org.openspcoop2.pdd.core.controllo_traffico.ReadTimeoutConfigurationUtils;
import org.openspcoop2.pdd.core.controllo_traffico.policy.PolicyDati;
import org.slf4j.Logger;

/**
* EventiUtils
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class EventiUtils {


	public static boolean findEventTimeoutViolato(List<Map<String,Object>> events, String idServizio, TipoEvento tipoEvento, Optional<String> gruppo, Optional<String> connettore, PolicyTimeoutConfig config, 
			PolicyDati dati,
			String descrizioneEvento, Logger log) {
		
		log.debug("======== VERIFICA findEventTimeoutViolato ========");
		
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
			String configurazione = (String) ev.get("configurazione");
			String descrizione = (String) ev.get("descrizione");
			
			boolean contains1 = false;
			if(id_configurazione!=null) {
				contains1 = id_configurazione.contains(idServizio);
			}

			boolean match = tipo.equals(tipoEvento.toString()) &&
					codice.equals("Violazione") &&
					severita.equals(1) && 
					contains1;
			
			boolean contains2 = false;
			if (gruppo.isPresent()) {
				if(id_configurazione!=null) {
					contains2 = id_configurazione.contains("gruppo '"+gruppo.get()+"'");
				}
				match = match && contains2;
			}
			
			boolean contains3 = false;
			if (connettore.isPresent()) {
				if(id_configurazione!=null) {
					contains3 = id_configurazione.contains("connettore '"+connettore.get()+"'");
				}
				match = match && contains3;
			}
			
			boolean contains4 = false;
			String contains4String = null;
			if(config!=null) {
				StringBuilder sb = new StringBuilder();
				ReadTimeoutConfigurationUtils.addPolicyInfo(sb, config);
				contains4String = sb.toString();
				contains4 = id_configurazione.contains(contains4String);
				match = match && contains4;
			}
			
			if(descrizioneEvento!=null) {
				match = match && descrizioneEvento.equals(descrizione);
			}
			
			log.debug("tipo='"+tipo+"' confronto con '"+tipoEvento.toString()+"': "+tipo.equals(tipoEvento.toString()));
			log.debug("codice='"+codice+"' confronto con 'Violazione': "+codice.equals("Violazione"));
			log.debug("severita='"+severita+"' confronto con 1: "+severita.equals(1));
			if(id_configurazione!=null) {
				log.debug("id_configurazione='"+id_configurazione+"'"+
						"\n\tverifica1 contains("+idServizio+")="+contains1+"");
				if (gruppo.isPresent()) {
					log.debug("\n\tverifica2 containsGruppo("+gruppo.get()+")="+contains2+"");
				}
				if (connettore.isPresent()) {
					log.debug("\n\tverifica3 containsConnettore("+connettore.get()+")="+contains3+"");
				}
				if(config!=null) {
					log.debug("\n\tverifica4 containsPolicy("+contains4String+")="+contains4+"");
				}
			}
			else {
				log.debug("id_configurazione='"+id_configurazione+"'");
			}
			log.debug("descrizione='"+descrizione+"' confronto con '"+descrizioneEvento+"': "+descrizioneEvento.equals(descrizione));
			log.debug("match="+match);
			
			boolean configurazioneMatch = true;
			List<String> l = ReadTimeoutConfigurationUtils.buildConfigurazioneEventoAsList(dati, null, null);
			if(l!=null && !l.isEmpty()) {
				log.debug("cerco in configurazione ["+configurazione+"]");
				for (String evento : l) {
					boolean contains = configurazione!=null && configurazione.contains(evento);
					log.debug("verifica in configurazione ("+evento+")="+contains+"");
					if(!contains) {
						configurazioneMatch = false;
						break;
					}
				}
				log.debug("matchConfigurazione:"+configurazioneMatch);
			}
			
			return match && configurazioneMatch;
		});
	
	}


	public static boolean findEventTimeoutViolazioneRisolta(List<Map<String,Object>> events, String idServizio, TipoEvento tipoEvento, Optional<String> gruppo, Optional<String> connettore, PolicyTimeoutConfig config, 
			PolicyDati dati,  
			String descrizioneEvento, Logger log) {
		
		log.debug("======== VERIFICA findEventTimeoutViolazioneRisolta ========");
		
		return events.stream()
		.anyMatch( ev -> {
			Object tipo = ev.get("tipo");
			Object codice = ev.get("codice");
			Object severita = ev.get("severita");
			String id_configurazione = (String) ev.get("id_configurazione");
			String configurazione = (String) ev.get("configurazione");
			/**String descrizione = (String) ev.get("descrizione");*/
			
			boolean contains1 = false;
			if(id_configurazione!=null) {
				contains1 = id_configurazione.contains(idServizio);
			}

			boolean match = tipo.equals(tipoEvento.toString()) &&
					codice.equals("ViolazioneRisolta") &&
					severita.equals(3) && 
					contains1;
			
			boolean contains2 = false;
			if (gruppo.isPresent()) {
				if(id_configurazione!=null) {
					contains2 = id_configurazione.contains(" (gruppo '"+gruppo.get()+"')");
				}
				match = match && contains2;
			}
			
			boolean contains3 = false;
			if (connettore.isPresent()) {
				if(id_configurazione!=null) {
					contains3 = id_configurazione.contains(" (connettore '"+connettore.get()+"')");
				}
				match = match && contains3;
			}
			
			boolean contains4 = false;
			String contains4String = null;
			if(config!=null) {
				StringBuilder sb = new StringBuilder();
				ReadTimeoutConfigurationUtils.addPolicyInfo(sb, config);
				contains4String = sb.toString();
				contains4 = id_configurazione.contains(contains4String);
				match = match && contains4;
			}
			
			/** Solo per le violazione if(descrizioneEvento!=null) {
				match = match && descrizioneEvento.equals(descrizione);
			}*/
			
			log.debug("tipo='"+tipo+"' confronto con '"+tipoEvento.toString()+"': "+tipo.equals(tipoEvento.toString()));
			log.debug("codice='"+codice+"' confronto con 'ViolazioneRisolta': "+codice.equals("ViolazioneRisolta"));
			log.debug("severita='"+severita+"' confronto con 3: "+severita.equals(3));
			if(id_configurazione!=null) {
				log.debug("id_configurazione='"+id_configurazione+"'"+
						"\n\tverifica1 contains("+idServizio+")="+contains1+"");
				if (gruppo.isPresent()) {
					log.debug("\n\tverifica2 containsGruppo("+gruppo.get()+")="+contains2+"");
				}
				if (connettore.isPresent()) {
					log.debug("\n\tverifica3 containsConnettore("+connettore.get()+")="+contains3+"");
				}
				if(config!=null) {
					log.debug("\n\tverifica4 containsPolicy("+contains4String+")="+contains4+"");
				}
			}
			else {
				log.debug("id_configurazione='"+id_configurazione+"'");
			}
			/** Solo per le violazione log.debug("descrizione='"+descrizione+"' confronto con '"+descrizioneEvento+"': "+descrizioneEvento.equals(descrizione)); */
			log.debug("match="+match);
					
			boolean configurazioneMatch = true;
			List<String> l = ReadTimeoutConfigurationUtils.buildConfigurazioneEventoAsList(dati, null, null);
			if(l!=null && !l.isEmpty()) {
				log.debug("cerco in configurazione ["+configurazione+"]");
				for (String evento : l) {
					boolean contains = configurazione!=null && configurazione.contains(evento);
					log.debug("verifica in configurazione ("+evento+")="+contains+"");
					if(!contains) {
						configurazioneMatch = false;
						break;
					}
				}
				log.debug("matchConfigurazione:"+configurazioneMatch);
			}
			
			return match && configurazioneMatch;
		});
	
	}

}
