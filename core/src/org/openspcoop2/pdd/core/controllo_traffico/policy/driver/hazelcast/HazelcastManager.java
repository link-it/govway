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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;

import com.hazelcast.config.InMemoryYamlConfig;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**     
 *  HazelcastManager
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HazelcastManager {

	public static Map<PolicyGroupByActiveThreadsType, HazelcastInstance> staticMapInstance = null;
	private static Map<PolicyGroupByActiveThreadsType,String> staticMapConfig = null;
	private static String groupId;
	private static Logger logStartup;
	private static Logger log;
	
	public static synchronized void initialize(Logger logStartup, Logger log, Map<PolicyGroupByActiveThreadsType,String> config, String groupId) throws Exception {
		
		if(HazelcastManager.staticMapInstance==null){
		
			/*
			 * This configuration disables the shutdown hook in hazelcast, which ensures that the hazelcast instance shuts down gracefully whenever the product node shuts down. 
			 * If the hazelcast shutdown hook is enabled (which is the default behavior of a product), 
			 * you will see errors such as " Hazelcast instance is not active! " at the time of shutting down the product node: 
			 * This is because the hazelcast instance shuts down too early when the shutdown hook is enabled.
			 **/
			System.setProperty("hazelcast.shutdownhook.enabled","false");
	
			/*
			 * This configuration sets the hazelcast logging type to log4j2, which allows hazelcast logs to be written to the govway_hazelcast.log file.
			 **/
			System.setProperty("hazelcast.logging.type", "log4j2");
			
			
			HazelcastManager.staticMapInstance = new HashMap<PolicyGroupByActiveThreadsType, HazelcastInstance>();
			HazelcastManager.staticMapConfig = new HashMap<PolicyGroupByActiveThreadsType, String>();
			HazelcastManager.groupId = groupId;
			HazelcastManager.logStartup = logStartup;
			HazelcastManager.log = log;
						
			if(config!=null && !config.isEmpty()) {
				for (PolicyGroupByActiveThreadsType type : config.keySet()) {
					String pathConfig = config.get(type);
					String content = null;
					if(pathConfig != null) {
						File hazelcastConfigFile = new File(pathConfig);
						if(!hazelcastConfigFile.exists()) {
							//consento di definire la proprietà senza che sia presente il file
							//throw new Exception("Hazelcast file config ["+hazelcastConfigFile.getAbsolutePath()+"] not exists");
						}
						else {
							if(!hazelcastConfigFile.canRead()) {
								throw new Exception("Hazelcast (type:"+type+") file config ["+hazelcastConfigFile.getAbsolutePath()+"] cannot read");
							}
							content = FileSystemUtilities.readFile(hazelcastConfigFile);
						}
					}
					if(content==null) {
						// default
						String name = "";
						switch (type) {
						case HAZELCAST:
							name = "govway.hazelcast.yaml";
							break;
						case HAZELCAST_NEAR_CACHE:
							name = "govway.hazelcast-near-cache.yaml";
							break;
						case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
							name = "govway.hazelcast-near-cache-unsafe-sync-map.yaml";
							break;
						case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
							name = "govway.hazelcast-near-cache-unsafe-async-map.yaml";
							break;
						case HAZELCAST_LOCAL_CACHE:
							name = "govway.hazelcast-local-cache.yaml";
							break;
						case HAZELCAST_REPLICATED_MAP:
							name= "govway.hazelcast-replicated-map.yaml";
							break;
						case HAZELCAST_PNCOUNTER:
							name = "govway.hazelcast-pncounter.yaml";
							break;
						case HAZELCAST_ATOMIC_LONG:
						case HAZELCAST_ATOMIC_LONG_ASYNC:
							name = "govway.hazelcast-atomic-long.yaml";
							break;
						default:
							throw new Exception("Hazelcast type '"+type+"' unsupported");
						}
						content = Utilities.getAsString(HazelcastManager.class.getResourceAsStream("/"+name),Charset.UTF_8.getValue());
					}
					if(content==null) {
						throw new Exception("Hazelcast (type:"+type+") config undefined");
					}
					else {
						HazelcastManager.staticMapConfig.put(type, content);
					}
				}
			}

		}
	}
	
	
	public static List<PolicyGroupByActiveThreadsType> getTipiGestoriHazelcastAttivi() throws PolicyException{
		if(staticMapInstance==null){
			throw new PolicyException("Nessun gestore Hazelcast inizializzato");
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
	
	public static HazelcastInstance getInstance(PolicyGroupByActiveThreadsType type) throws PolicyException{
		if(staticMapInstance==null){
			throw new PolicyException("Nessun gestore Hazelcast inizializzato");
		}
		HazelcastInstance gestore = staticMapInstance.get(type);
		if(gestore==null) {
			HazelcastManager.initialize(type);
			gestore = staticMapInstance.get(type);
		}
		if(gestore==null) {
			throw new PolicyException("Gestore Hazelcast '"+type+"' non inizializzato ??");
		}
		return gestore;
	}
	
	private static synchronized void initialize(PolicyGroupByActiveThreadsType type) throws PolicyException{
		if(!HazelcastManager.staticMapInstance.containsKey(type)) {
			
			HazelcastManager.logStartup.info("Inizializzazione Gestore Hazelcast '"+type+"' ...");
			HazelcastManager.log.info("Inizializzazione Gestore Hazelcast '"+type+"' ...");
			
			HazelcastManager.staticMapInstance.put(type, newInstance(type));
			
			HazelcastManager.logStartup.info("Inizializzazione Gestore Hazelcast '"+type+"' effettuata con successo");
			HazelcastManager.log.info("Inizializzazione Gestore Hazelcast '"+type+"' effettuata con successo");
			
		}
	}	
	
	private static synchronized HazelcastInstance newInstance(PolicyGroupByActiveThreadsType type) throws PolicyException {
		
		String content = HazelcastManager.staticMapConfig.get(type);
		if(content==null) {
			throw new PolicyException("Hazelcast config undefined for type '"+type+"'");
		}
		
		String groupId = null;
		switch (type) {
		case HAZELCAST:
			groupId = HazelcastManager.groupId;
			break;
		case HAZELCAST_NEAR_CACHE:
			groupId = HazelcastManager.groupId+"-near-cache";
			break;
		case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
			groupId = HazelcastManager.groupId+"-near-cache-unsafe-sync-map";
			break;
		case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
			groupId = HazelcastManager.groupId+"-near-cache-unsafe-async-map";
			break;
		case HAZELCAST_LOCAL_CACHE:
			groupId = HazelcastManager.groupId+"-local-cache";
			break;
		case HAZELCAST_REPLICATED_MAP:
			groupId = HazelcastManager.groupId+"-replicated-map"; 
			break;
		case HAZELCAST_PNCOUNTER:
			groupId = HazelcastManager.groupId+"-pncounter";
			break;
		case HAZELCAST_ATOMIC_LONG:
			groupId = HazelcastManager.groupId+"-atomic-long";
			break;
		case HAZELCAST_ATOMIC_LONG_ASYNC:
			groupId = HazelcastManager.groupId+"-atomic-long-async";
			break;
		default:
			throw new PolicyException("Hazelcast type '"+type+"' unsupported");
		}
		
		HazelcastManager.log.debug("Inizializzo hazelcast con la seguente configurazione (cluster-id"+groupId+"): " + content);
		HazelcastManager.logStartup.debug("Inizializzo hazelcast con la seguente configurazione (cluster-id"+groupId+"): " + content);
		
		InMemoryYamlConfig hazelcastConfig = new InMemoryYamlConfig(content);
		hazelcastConfig.setClusterName(groupId);
				
		HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance(hazelcastConfig);
		if(hazelcast==null) {
			throw new PolicyException("Hazelcast init failed");
		}
	
		return hazelcast;
	}
	
	public static synchronized void close() {
		if(HazelcastManager.staticMapInstance!=null && !HazelcastManager.staticMapInstance.isEmpty()) {
			for (PolicyGroupByActiveThreadsType type : HazelcastManager.staticMapInstance.keySet()) {
				HazelcastInstance hazelcast = HazelcastManager.staticMapInstance.get(type);
				try {
					hazelcast.shutdown();
				}catch(Throwable t) {
					HazelcastManager.log.debug("Hazelcast '"+type+"' shutdown failed: " + t.getMessage(),t);
				}
			}
		}
	}
	

}
