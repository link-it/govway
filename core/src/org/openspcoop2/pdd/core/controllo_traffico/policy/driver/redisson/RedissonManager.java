package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson;

import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.PolicyGroupByActiveThreadsInMemoryEnum;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.slf4j.Logger;

public class RedissonManager {

		public static RedissonClient redisson = null;
		
		public static synchronized void initialize(PolicyGroupByActiveThreadsInMemoryEnum type, String config, String groupId, Logger log) throws Exception {
			Config redisConf = new Config();
			redisConf.useClusterServers()
				.addNodeAddress("redis://127.0.0.1:7000", "redis://127.0.0.1:7001", "redis://127.0.0.1:7002")				// E' necessario aggiungere solo un nodo master, gli altri vengono acquisiti durante l'avvio
																												// usare rediss:// per SSL (con due s)
				.setReadMode(ReadMode.MASTER_SLAVE);
			
			
			redisson = Redisson.create(redisConf);
		}
}
