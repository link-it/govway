/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.slf4j.Logger;

/**     
 * GestorePolicyAttive
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestorePolicyAttive {

	private static Map<PolicyGroupByActiveThreadsType, IGestorePolicyAttive> staticMapInstance = null;
	private static Map<PolicyGroupByActiveThreadsType, String> staticPolicyRateLimitingImage = null;
	private static Map<PolicyGroupByActiveThreadsType, String> staticPolicyRateLimitingEventiImage = null;
	private static TipoGestorePolicy tipo;
	private static String urlService;
	private static Logger logStartup;
	private static Logger log;
	
	private static synchronized void initialize(PolicyGroupByActiveThreadsType type) throws PolicyException{
		if(!GestorePolicyAttive.staticMapInstance.containsKey(type)) {
			
			GestorePolicyAttive.logStartup.info("Inizializzazione Gestore Policy '"+type+"' ...");
			GestorePolicyAttive.log.info("Inizializzazione Gestore Policy '"+type+"' ...");
			
			IGestorePolicyAttive gestore = null;
			if(TipoGestorePolicy.IN_MEMORY.equals(GestorePolicyAttive.tipo)){
				gestore = new GestorePolicyAttiveInMemory();
				gestore.initialize(GestorePolicyAttive.log, type);
			}
			else if(TipoGestorePolicy.WS.equals(GestorePolicyAttive.tipo)){
				gestore = new GestorePolicyAttiveWS();
				gestore.initialize(GestorePolicyAttive.log,type,GestorePolicyAttive.urlService);
			}
			else{
				throw new PolicyException("Tipo GestorePolicyAttive ["+GestorePolicyAttive.tipo+"] non supportato");
			}
			GestorePolicyAttive.staticMapInstance.put(type, gestore);
			GestorePolicyAttive.staticPolicyRateLimitingImage.put(type, org.openspcoop2.core.controllo_traffico.constants.Costanti.getControlloTrafficoImage(type.name()));
			GestorePolicyAttive.staticPolicyRateLimitingEventiImage.put(type, org.openspcoop2.core.controllo_traffico.constants.Costanti.getControlloTrafficoEventiImage(type.name()));
			
			GestorePolicyAttive.logStartup.info("Inizializzazione Gestore Policy '"+type+"' effettuata con successo");
			GestorePolicyAttive.log.info("Inizializzazione Gestore Policy '"+type+"' effettuata con successo");
			
		}
	}
	
	public static synchronized void initialize(Logger logStartup, Logger log, TipoGestorePolicy tipo, String urlService, List<PolicyGroupByActiveThreadsType> inMemoryTypes) throws Exception{
		if(GestorePolicyAttive.staticMapInstance==null){
			if(inMemoryTypes==null || inMemoryTypes.isEmpty()) {
				throw new Exception("Almeno un tipo di gestore deve essere definito");
			}			
			GestorePolicyAttive.staticMapInstance = new HashMap<PolicyGroupByActiveThreadsType, IGestorePolicyAttive>();
			GestorePolicyAttive.staticPolicyRateLimitingImage = new HashMap<PolicyGroupByActiveThreadsType, String>();
			GestorePolicyAttive.staticPolicyRateLimitingEventiImage = new HashMap<PolicyGroupByActiveThreadsType, String>();
			GestorePolicyAttive.logStartup = logStartup;
			GestorePolicyAttive.log = log;
			GestorePolicyAttive.tipo = tipo;
			GestorePolicyAttive.urlService = urlService;
			
			for (PolicyGroupByActiveThreadsType type : inMemoryTypes) {
				initialize(type);
			}
		}
	}
	
	public static List<PolicyGroupByActiveThreadsType> getTipiGestoriAttivi() throws PolicyException{
		if(staticMapInstance==null){
			throw new PolicyException("GestorePolicyAttive non inizializzato");
		}
		List<PolicyGroupByActiveThreadsType> l = new ArrayList<PolicyGroupByActiveThreadsType>();
		l.addAll(staticMapInstance.keySet());
		return l;
	}
	
	public static boolean isAttivo(PolicyGroupByActiveThreadsType type) {
		if(staticMapInstance==null){
			return false;
		}
		return staticMapInstance.containsKey(type);
	}
	
	public static IGestorePolicyAttive getInstance(PolicyGroupByActiveThreadsType type) throws PolicyException{
		if(staticMapInstance==null){
			throw new PolicyException("GestorePolicyAttive non inizializzato");
		}
		IGestorePolicyAttive gestore = staticMapInstance.get(type);
		if(gestore==null) {
			GestorePolicyAttive.initialize(type);
			gestore = staticMapInstance.get(type);
		}
		if(gestore==null) {
			throw new PolicyException("GestorePolicyAttive '"+type+"' non inizializzato ??");
		}
		return gestore;
	}
	
	public static String getControlloTrafficoImage(PolicyGroupByActiveThreadsType type) throws PolicyException{
		if(staticPolicyRateLimitingImage==null){
			throw new PolicyException("GestorePolicyAttive non inizializzato");
		}
		if(!staticPolicyRateLimitingImage.containsKey(type)){
			throw new PolicyException("GestorePolicyAttive non inizializzato per il tipo '"+type+"'");
		}
		return staticPolicyRateLimitingImage.get(type);
	}
	
	public static String getControlloTrafficoEventiImage(PolicyGroupByActiveThreadsType type) throws PolicyException{
		if(staticPolicyRateLimitingEventiImage==null){
			throw new PolicyException("GestorePolicyAttive non inizializzato");
		}
		if(!staticPolicyRateLimitingEventiImage.containsKey(type)){
			throw new PolicyException("GestorePolicyAttive non inizializzato per il tipo '"+type+"'");
		}
		return staticPolicyRateLimitingEventiImage.get(type);
	}
}

