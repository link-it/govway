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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson;

import java.util.List;

import org.openspcoop2.utils.UtilsException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.slf4j.Logger;

/**     
 *  RedissonManager
 *
 * @author Francesco Scarlato (scarlato@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RedissonManager {
	
	private RedissonManager() {}

	private static Logger logStartup;
	private static Logger log;
	
	private static List<String> connectionUrl = null;
	
	private static final String INIZIALIZZAZIONE_FALLITA = "Inizializzazione RedissonClient fallita: ";
	
	public static synchronized void initialize(Logger logStartup, Logger log, List<String> connectionUrl) {
		
		if(RedissonManager.connectionUrl==null){
			RedissonManager.logStartup = logStartup;
			RedissonManager.log = log;
			RedissonManager.connectionUrl = connectionUrl;
		}
		
	}
	
	private static RedissonClient redisson = null;
	private static synchronized void initRedissonClient(boolean throwInitializingException) throws UtilsException {
		if(redisson==null) {
			
			RedissonManager.logStartup.info("Inizializzazione RedissonClient ...");
			RedissonManager.log.info("Inizializzazione RedissonClient ...");
			
			try {
				Config redisConf = new Config();
				redisConf.useClusterServers()
					.addNodeAddress(RedissonManager.connectionUrl.toArray(new String[1]))
					.setReadMode(ReadMode.MASTER_SLAVE)
					.setSslEnableEndpointIdentification(true);
				redisson = Redisson.create(redisConf);
				RedissonManager.logStartup.info("Inizializzazione RedissonClient effettuata con successo");
				RedissonManager.log.info("Inizializzazione RedissonClient effettuata con successo");
			}catch(Exception t) {
				if(throwInitializingException) {
					throw new UtilsException(INIZIALIZZAZIONE_FALLITA+t.getMessage(),t);
				}
				else {
					RedissonManager.logStartup.error(INIZIALIZZAZIONE_FALLITA+t.getMessage(),t);
					RedissonManager.log.error(INIZIALIZZAZIONE_FALLITA+t.getMessage(),t);
				}
			}
			
		}
	}
	public static RedissonClient getRedissonClient(boolean throwInitializingException) throws UtilsException {
		if(redisson==null) {
			initRedissonClient(throwInitializingException);
		}
		return RedissonManager.redisson;
	}
	public static boolean isRedissonClientInitialized() {
		return RedissonManager.redisson!=null;
	}

}
