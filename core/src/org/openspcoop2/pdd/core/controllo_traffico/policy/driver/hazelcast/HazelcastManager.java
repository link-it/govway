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

import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.PolicyGroupByActiveThreadsInMemoryEnum;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;

import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryYamlConfig;
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

	public static HazelcastInstance hazelcast = null;
	
	public static synchronized void initialize(PolicyGroupByActiveThreadsInMemoryEnum type, String config, String groupId, Logger log) throws Exception {
		
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
		
		Config hazelcastConfig = null;
		
		String content = null;
		
		if(config != null) {
			File hazelcastConfigFile = new File(config);
			if(!hazelcastConfigFile.exists()) {
				throw new Exception("Hazelcast file config ["+hazelcastConfigFile.getAbsolutePath()+"] not exists");
			}
			if(!hazelcastConfigFile.canRead()) {
				throw new Exception("Hazelcast file config ["+hazelcastConfigFile.getAbsolutePath()+"] cannot read");
			}
			content = FileSystemUtilities.readFile(hazelcastConfigFile);
		}
		else {
			// default
			String name = "";
			switch (type) {
			case HAZELCAST:
				name = "govway.hazelcast.yaml";
				break;
			case HAZELCAST_NEAR_CACHE:
				name = "govway.hazelcast-near-cache.yaml";
				break;
			case HAZELCAST_LOCAL_CACHE:
				name = "govway.hazelcast-local-cache.yaml";
				break;
			default:
				throw new Exception("Hazelcast type '"+type+"' unsupported");
			}
			content = Utilities.getAsString(HazelcastManager.class.getResourceAsStream("/"+name),Charset.UTF_8.getValue());
		}
		
		log.debug("Inizializzo hazelcast con la seguente configurazione: " + content);
		log.debug("Il cluster-name sarà: " + groupId);
		
		hazelcastConfig = new InMemoryYamlConfig(content);
		hazelcastConfig.setClusterName(groupId);
				
		hazelcast = Hazelcast.newHazelcastInstance(hazelcastConfig);
		if(hazelcast==null) {
			throw new Exception("Hazelcast init failed");
		}
		
	}
	
	public static synchronized void close() {
		if(hazelcast!=null) {
			hazelcast.shutdown();
		}
	}
	
}
