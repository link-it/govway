/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 *
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.TrustManagerFactory;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.KeystoreUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.config.SslVerificationMode;
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
	private static RedissonConnectionMode connectionMode = RedissonConnectionMode.CLUSTER;
	private static RedissonSSLConfig sslConfig = null;

	private static final String INIZIALIZZAZIONE_FALLITA = "Inizializzazione RedissonClient fallita: ";

	public static synchronized void initialize(Logger logStartup, Logger log, List<String> connectionUrl,
			RedissonConnectionMode connectionMode, RedissonSSLConfig sslConfig) {

		if(RedissonManager.connectionUrl==null){
			RedissonManager.logStartup = logStartup;
			RedissonManager.log = log;
			RedissonManager.connectionUrl = connectionUrl;
			if(connectionMode!=null) {
				RedissonManager.connectionMode = connectionMode;
			}
			RedissonManager.sslConfig = sslConfig;
		}

	}

	private static RedissonClient redisson = null;
	private static synchronized void initRedissonClient(boolean throwInitializingException) throws UtilsException {
		if(redisson==null) {

			String msg = buildInitMessage();
			RedissonManager.logStartup.info(msg);
			RedissonManager.log.info(msg);

			try {
				Config redisConf = new Config();
				applySslConfig(redisConf);
				if(RedissonConnectionMode.SINGLE.equals(RedissonManager.connectionMode)) {
					String address = RedissonManager.connectionUrl.get(0);
					redisConf.useSingleServer()
						.setAddress(address);
				}
				else {
					redisConf.useClusterServers()
						.addNodeAddress(RedissonManager.connectionUrl.toArray(new String[1]))
						.setReadMode(ReadMode.MASTER_SLAVE);
				}
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

	private static String buildInitMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("Inizializzazione RedissonClient (mode: ").append(RedissonManager.connectionMode.getValue()).append(")");

		// URL (con pass mascherata)
		sb.append(" url: ");
		for(int i=0; i<RedissonManager.connectionUrl.size(); i++) {
			if(i>0) {
				sb.append(", ");
			}
			sb.append(maskPassword(RedissonManager.connectionUrl.get(i)));
		}

		// TLS
		addTLSMessage(sb);

		sb.append(" ...");
		return sb.toString();
	}
	private static void addTLSMessage(StringBuilder sb) {
		boolean tls = false;
		for(String url : RedissonManager.connectionUrl) {
			if(url.startsWith("rediss://")) {
				tls = true;
				break;
			}
		}
		sb.append(" tls: ").append(tls ? "enabled" : "disabled");

		if(tls && RedissonManager.sslConfig!=null) {
			addTLSConfigMessage(sb);
		}
	}
	private static void addTLSConfigMessage(StringBuilder sb) {
		if(RedissonManager.sslConfig.isTrustAll()) {
			sb.append(" (trustAll)");
		}
		else {
			if(RedissonManager.sslConfig.getTruststorePath()!=null) {
				sb.append(" (truststore: ").append(RedissonManager.sslConfig.getTruststorePath());
				sb.append(", type: ").append(RedissonManager.sslConfig.getTruststoreType());
				sb.append(", password: ").append(RedissonManager.sslConfig.getTruststorePassword()!=null ? "***" : "n/a");
				sb.append(")");
			}
			sb.append(" hostnameVerifier: ").append(RedissonManager.sslConfig.isHostnameVerifier());
		}
	}

	private static String maskPassword(String url) {
		// Maschera la pass in url del tipo redis://user:***@host:port
		int schemeEnd = url.indexOf("://");
		if(schemeEnd<0) {
			return url;
		}
		int atSign = url.indexOf('@', schemeEnd+3);
		if(atSign<0) {
			return url;
		}
		String credentials = url.substring(schemeEnd+3, atSign);
		int colonIdx = credentials.indexOf(':');
		if(colonIdx<0) {
			return url;
		}
		String user = credentials.substring(0, colonIdx);
		return url.substring(0, schemeEnd+3) + user + ":***@" + url.substring(atSign+1);
	}

	/*
	 * Mapping proprietà SSL -> SslVerificationMode di Redisson:
	 *   trustAll=true                  -> NONE     (nessuna verifica: ne' certificato ne' hostname)
	 *   hostnameVerifier=true (default)-> STRICT   (verifica certificato CA + verifica hostname CN/SAN)
	 *   hostnameVerifier=false         -> CA_ONLY  (verifica certificato CA, ma non l'hostname)
	 *
	 * In Redisson 4.x le proprietà SSL sono su Config (non su BaseConfig dei singoli server)
	 */
	private static void applySslConfig(Config redisConf) throws Exception {

		if(RedissonManager.sslConfig==null) {
			// backward compatibility: hostnameVerifier abilitato di default
			redisConf.setSslVerificationMode(SslVerificationMode.STRICT);
			return;
		}

		redisConf.setSslVerificationMode(RedissonManager.sslConfig.isHostnameVerifier() ? SslVerificationMode.STRICT : SslVerificationMode.CA_ONLY);

		if(RedissonManager.sslConfig.isTrustAll()) {
			redisConf.setSslVerificationMode(SslVerificationMode.NONE);
		}
		else if(RedissonManager.sslConfig.getTruststorePath()!=null) {
			KeyStore ks = KeystoreUtils.readKeystore(
					new FileInputStream(RedissonManager.sslConfig.getTruststorePath()),
					RedissonManager.sslConfig.getTruststoreType(),
					RedissonManager.sslConfig.getTruststorePassword());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(ks);
			redisConf.setSslTrustManagerFactory(tmf);
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
