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
package org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.json.JsonPathNotFoundException;
import org.openspcoop2.utils.json.YAMLUtils;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryYamlConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.config.YamlConfigBuilder;
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
	
	private HazelcastManager() {}

	private static final String GOVWAY_INSTANCE_PORT = "GOVWAY_INSTANCE_PORT";

	// Nome della IMap usata come registry per tracciare i contatori AtomicLong del CP Subsystem
	private static final String ATOMIC_LONG_REGISTRY_MAP_NAME = "govway-atomiclong-registry";

	private static Map<PolicyGroupByActiveThreadsType, HazelcastInstance> staticMapInstance = null;
	private static Map<PolicyGroupByActiveThreadsType,String> staticMapConfig = null;
	private static String groupId;
	
	private static YAMLUtils yamlUtils = null;
	private static JsonPathExpressionEngine engine = null;
	
	private static JsonNode sharedConfigNode;
	private static File shareConfigFile;
	
	private static Logger logStartup;
	private static Logger log;
	
	public static synchronized void initialize(Logger logStartup, Logger log, Map<PolicyGroupByActiveThreadsType,String> config, String groupId, File shareConfigFile) throws FileNotFoundException, UtilsException, PolicyException {
		
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
			
			
			HazelcastManager.staticMapInstance = new HashMap<>();
			HazelcastManager.staticMapConfig = new HashMap<>();
			HazelcastManager.groupId = groupId;
			HazelcastManager.logStartup = logStartup;
			HazelcastManager.log = log;
						
			if(shareConfigFile!=null) {
				
				HazelcastManager.shareConfigFile = shareConfigFile;
				HazelcastManager.yamlUtils = YAMLUtils.getInstance();
				HazelcastManager.engine = new JsonPathExpressionEngine();
				
				String sharedContentBytes = FileSystemUtilities.readFile(shareConfigFile); 
				
				try {
					HazelcastManager.sharedConfigNode = yamlUtils.getAsNode(sharedContentBytes.getBytes());
				}catch(Throwable t) {
					throw new PolicyException("Configuration '"+shareConfigFile.getAbsolutePath()+"' is not valid yaml config: "+t.getMessage()+"\n"+sharedContentBytes,t);
				}
				Config sharedConfig = null;
				try {
					// La valido ma poi la ricostruiro' tutte le volte per poi sostituire la porta
					try(ByteArrayInputStream bin = new ByteArrayInputStream(sharedContentBytes.getBytes())){
						YamlConfigBuilder builder = new  YamlConfigBuilder(bin);
						sharedConfig = builder.build();
					}
				}catch(Throwable t) {
					throw new PolicyException("Configuration '"+shareConfigFile.getAbsolutePath()+"' is not valid yaml config (YamlConfigBuilder): "+t.getMessage()+"\n"+sharedContentBytes,t);
				}
			
				if(sharedConfig!=null && 
						sharedConfig.getClusterName()!=null && 
						StringUtils.isNotEmpty(sharedConfig.getClusterName())) {
					// Se definita sovrascrive il valore indicato nella proprietà 'org.openspcoop2.pdd.controlloTraffico.gestorePolicy.inMemory.HAZELCAST.group_id' in govway_local.properties
					// passato come parametro 'groupId'
					HazelcastManager.groupId = sharedConfig.getClusterName();
				}
			}
			
			if(config!=null && !config.isEmpty()) {
				for (PolicyGroupByActiveThreadsType type : config.keySet()) {
					String pathConfig = config.get(type);
					String content = null;
					if(pathConfig != null) {
						File hazelcastConfigFile = new File(pathConfig);
						if(!hazelcastConfigFile.exists()) {
							//consento di definire la proprietà senza che sia presente il file
							/**throw new Exception("Hazelcast file config ["+hazelcastConfigFile.getAbsolutePath()+"] not exists");*/
						}
						else {
							if(!hazelcastConfigFile.canRead()) {
								throw new PolicyException("Hazelcast (type:"+type+") file config ["+hazelcastConfigFile.getAbsolutePath()+"] cannot read");
							}
							content = FileSystemUtilities.readFile(hazelcastConfigFile);
						}
					}
					if(content==null) {
						// default
						String name = "";
						switch (type) {
						case HAZELCAST_MAP:
							name = "govway.hazelcast-map.yaml";
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
						case HAZELCAST_ATOMIC_LONG:
							name = "govway.hazelcast-atomic-long-counters.yaml";
							break;
						case HAZELCAST_ATOMIC_LONG_ASYNC:
							name = "govway.hazelcast-atomic-long-async-counters.yaml";
							break;
						case HAZELCAST_PNCOUNTER:
							name = "govway.hazelcast-pn-counters.yaml";
							break;
						default:
							throw new PolicyException("Hazelcast type '"+type+"' unsupported");
						}
						content = Utilities.getAsString(HazelcastManager.class.getResourceAsStream("/"+name),Charset.UTF_8.getValue());
					}
					if(content==null) {
						throw new PolicyException("Hazelcast (type:"+type+") config undefined");
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
		List<PolicyGroupByActiveThreadsType> l = new ArrayList<>();
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
			
			info("Inizializzazione Gestore Hazelcast '"+type+"' ...");
			
			HazelcastManager.staticMapInstance.put(type, newInstance(type));
			
			info("Inizializzazione Gestore Hazelcast '"+type+"' effettuata con successo");
			
		}
	}	
	
	private static synchronized HazelcastInstance newInstance(PolicyGroupByActiveThreadsType type) throws PolicyException {
		
		String content = HazelcastManager.staticMapConfig.get(type);
		if(content==null) {
			throw new PolicyException("Hazelcast config undefined for type '"+type+"'");
		}
		
		String groupId = null;
		switch (type) {
		case HAZELCAST_MAP:
			groupId = HazelcastManager.groupId+"-map";
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
		
		content = content.replace("cluster-name:", "cluster-name: "+groupId+"\n#cluster-name:");
		
		debug("Inizializzo hazelcast con la seguente configurazione (cluster-id"+groupId+"): " + content);
		
		InMemoryYamlConfig hazelcastConfig = new InMemoryYamlConfig(content);
		hazelcastConfig.setClusterName(groupId);
		setNetwork(hazelcastConfig, content, groupId);
		
		HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance(hazelcastConfig);
		if(hazelcast==null) {
			throw new PolicyException("Hazelcast init failed");
		}
	
		return hazelcast;
	}
	
	private static final String PREFIX_CLUSTER_ID = "(cluster-id";
	private static void setNetwork(InMemoryYamlConfig hazelcastConfig, String hazelcastConfigContent, String groupId) throws PolicyException {
		
		Config sharedConfig = null;
		if(HazelcastManager.shareConfigFile!=null) {
			try {
				// La valido ma poi la ricostruiro' tutte le volte per poi sostituire la porta
				try(FileInputStream fin = new FileInputStream(HazelcastManager.shareConfigFile)){
					YamlConfigBuilder builder = new  YamlConfigBuilder(fin);
					sharedConfig = builder.build();
				}
			}catch(Throwable t) {
				// e' gia' stato validato in fase di inizializzazione. Un errore qua non dovrebbe succedere
				throw new PolicyException("Configuration '"+shareConfigFile.getAbsolutePath()+"' is not valid yaml config (YamlConfigBuilder): "+t.getMessage(),t);
			}
		}
		
		if(sharedConfig!=null && sharedConfig.getNetworkConfig()!=null) {
			NetworkConfig ncShared = sharedConfig.getNetworkConfig();
			NetworkConfig ncInstance = hazelcastConfig.getNetworkConfig();
			Integer portHazelcastConfigInstance = null;
			if(ncInstance==null) {
				// non dovrebbe mai esserlo, essendo definita una porta per ogni tipo di istanza
				ncInstance = new NetworkConfig();
				hazelcastConfig.setNetworkConfig(ncInstance);
			}
			else {
				portHazelcastConfigInstance = hazelcastConfig.getNetworkConfig().getPort();
			}
			
			
			JsonNode instanceNode = null;
			try {
				instanceNode = yamlUtils.getAsNode(hazelcastConfigContent);
			}catch(Throwable t) {
				throw new PolicyException("Configuration '"+groupId+"' is not valid yaml config:\n"+hazelcastConfigContent);
			}
			
			// overrides the public address of a member
			if(ncShared.getPublicAddress()!=null) {
				boolean definedInstance = isDefined("public-address", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setPublicAddress(ncShared.getPublicAddress());
					debug(PREFIX_CLUSTER_ID+groupId+") override public-address: " + ncInstance.getPublicAddress());
				}
			}
			
			// overrides only defined outbound ports
			if( (ncShared.getOutboundPorts()!=null && !ncShared.getOutboundPorts().isEmpty())
					||
				(ncShared.getOutboundPortDefinitions()!=null && !ncShared.getOutboundPortDefinitions().isEmpty())) {
				boolean definedInstance = isDefined("outbound-ports", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					if(ncShared.getOutboundPorts()!=null && !ncShared.getOutboundPorts().isEmpty()){
						ncInstance.setOutboundPorts(ncShared.getOutboundPorts());
						debug(PREFIX_CLUSTER_ID+groupId+") override outbound-ports: " + ncInstance.getOutboundPorts());
					}
					if((ncShared.getOutboundPortDefinitions()!=null && !ncShared.getOutboundPortDefinitions().isEmpty())) {
						ncInstance.setOutboundPortDefinitions(ncShared.getOutboundPortDefinitions());
						debug(PREFIX_CLUSTER_ID+groupId+") override outbound-ports (definitions): " + ncInstance.getOutboundPortDefinitions());
					}
				}
			}
			
			// If you set the reuse-address element to true, the TIME_WAIT state is ignored and you can bind the member to the same port again
			boolean definedShared = isDefined("reuse-address", yamlUtils, engine, HazelcastManager.sharedConfigNode);
			if(definedShared) {
				boolean definedInstance = isDefined("reuse-address", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setReuseAddress(ncShared.isReuseAddress());
					debug(PREFIX_CLUSTER_ID+groupId+") override reuse-address: " + ncInstance.isReuseAddress());
				}
			}
			
			/** the ports that Hazelcast uses to communicate between cluster members
			ncShared.getPort();
			ncShared.getPortCount();
			ncShared.isPortAutoIncrement();
			L'unico elemento che viene ignorato se presente è il 'port'.
			Anche se definito verrà ignorato poichè ogni singola configurazione utilizzata in GovWay richiede una porta dedicata.
		    Per modificarla si deve agire nel file govway.hazelcast-*.yaml presente all'interno dell'archivio govway.ear, 
		    modificandolo direttamente dentro l'archivio o riportandolo nella directory di configurazione esterna. */

			// override join
			if(ncShared.getJoin()!=null) {
				boolean definedInstance = isDefined("join", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setJoin(ncShared.getJoin());
					if(ncInstance.getJoin().getTcpIpConfig()!=null) {
						if(ncShared.getJoin().getTcpIpConfig().getConnectionTimeoutSeconds()>0) {
							ncInstance.getJoin().getTcpIpConfig().setConnectionTimeoutSeconds(ncShared.getJoin().getTcpIpConfig().getConnectionTimeoutSeconds());
						}
						if(portHazelcastConfigInstance!=null && portHazelcastConfigInstance.intValue()>0 &&
							ncInstance.getJoin().getTcpIpConfig().getMembers()!=null && !ncInstance.getJoin().getTcpIpConfig().getMembers().isEmpty()) {
							// Se vengono usate le porte, devo clonare la lista e sistemarla con la porta usata sull'attuale istanza
							List<String> newList = new ArrayList<>();
							for (String member : ncInstance.getJoin().getTcpIpConfig().getMembers()) {
								try {
									if(member.contains(GOVWAY_INSTANCE_PORT)) {
										newList.add(member.replace(GOVWAY_INSTANCE_PORT, portHazelcastConfigInstance.intValue()+""));
									}
									else if(member.contains(GOVWAY_INSTANCE_PORT.toLowerCase())) {
										newList.add(member.replace(GOVWAY_INSTANCE_PORT.toLowerCase(), portHazelcastConfigInstance.intValue()+""));
									}
									else {
										newList.add(member);
									}
									/**
									int indexOfFirst = member.indexOf(":");
									if(indexOfFirst>0 && indexOfFirst!=(member.length()-1)) {
										int indexOfSecondIpv6 = member.indexOf(":",(indexOfFirst+1));
										if(indexOfSecondIpv6<0) {
											usePortIpv4=true;
										}
									}
									if(usePortIpv4) {
										String [] split = member.split(":");
										String newIp = split[0] + ":" + portHazelcastConfigInstance.intValue();
										newList.add(newIp);
									}
									else {
										newList.add(member);
									}*/
								}catch(Throwable t) {
									error(PREFIX_CLUSTER_ID+groupId+") tpcip analisi member '"+member+"' fallita: " +t.getMessage(),t);
									/**newList.add(member);*/
								}
							}
							ncInstance.getJoin().getTcpIpConfig().setMembers(newList);
						}
					}
					debug(PREFIX_CLUSTER_ID+groupId+") override join: " + ncInstance.getJoin());
				}
			}
			
			// override interface
			if(ncShared.getInterfaces()!=null && ncShared.getInterfaces().isEnabled()) {
				boolean definedInstance = isDefined("interfaces", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setInterfaces(ncShared.getInterfaces());
					debug(PREFIX_CLUSTER_ID+groupId+") override interfaces: " + ncInstance.getInterfaces());
				}
			}

			
			// override altre configurazioni avanzate
			// example yaml: https://github.com/hazelcast/hazelcast/blob/master/hazelcast/src/test/java/com/hazelcast/config/YamlConfigBuilderTest.java
			if(ncShared.getIcmpFailureDetectorConfig()!=null && ncShared.getIcmpFailureDetectorConfig().isEnabled()) {
				boolean definedInstance = isDefined("failure-detector", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setIcmpFailureDetectorConfig(ncShared.getIcmpFailureDetectorConfig());
					debug(PREFIX_CLUSTER_ID+groupId+") override failure-detector icmp: " + ncInstance.getIcmpFailureDetectorConfig());
				}
			}
			if(ncShared.getMemberAddressProviderConfig()!=null && ncShared.getMemberAddressProviderConfig().isEnabled()) {
				boolean definedInstance = isDefined("member-address-provider", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setMemberAddressProviderConfig(ncShared.getMemberAddressProviderConfig());
					debug(PREFIX_CLUSTER_ID+groupId+") override member-address-provider: " + ncInstance.getMemberAddressProviderConfig());
				}
			}
			if(ncShared.getMemcacheProtocolConfig()!=null && ncShared.getMemcacheProtocolConfig().isEnabled()) {
				boolean definedInstance = isDefined("memcache-protocol", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setMemcacheProtocolConfig(ncShared.getMemcacheProtocolConfig());
					debug(PREFIX_CLUSTER_ID+groupId+") override memcache-protocol: " + ncInstance.getMemcacheProtocolConfig());
				}
			}
			if(ncShared.getRestApiConfig()!=null && ncShared.getRestApiConfig().isEnabled()) {
				boolean definedInstance = isDefined("rest-api", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setRestApiConfig(ncShared.getRestApiConfig());
					debug(PREFIX_CLUSTER_ID+groupId+") override rest-api: " + ncInstance.getRestApiConfig());
				}
			}
			if(ncShared.getSocketInterceptorConfig()!=null && ncShared.getSocketInterceptorConfig().getClassName()!=null) {
				boolean definedInstance = isDefined("socket-interceptor", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setSocketInterceptorConfig(ncShared.getSocketInterceptorConfig());
					debug(PREFIX_CLUSTER_ID+groupId+") override socket-interceptor: " + ncInstance.getSocketInterceptorConfig());
				}
			}
			if(ncShared.getSSLConfig()!=null && ncShared.getSSLConfig().isEnabled()) {
				boolean definedInstance = isDefined("ssl", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setSSLConfig(ncShared.getSSLConfig());
					debug(PREFIX_CLUSTER_ID+groupId+") override ssl: " + ncInstance.getSSLConfig());
				}
			}
			/** Deprecata 
			if(ncShared.getSymmetricEncryptionConfig()!=null && ncShared.getSymmetricEncryptionConfig().isEnabled()) {
				boolean definedInstance = isDefined("symmetric-encryption", yamlUtils, engine, node);
				if(!definedInstance) {
					ncInstance.setSymmetricEncryptionConfig(ncShared.getSymmetricEncryptionConfig());
					debug(PREFIX_CLUSTER_ID+groupId+") override symmetric-encryption: " + ncInstance.getSymmetricEncryptionConfig());
				}
			}
			*/
	
		}
	}
	
	private static boolean isDefined(String pattern, YAMLUtils yamlUtils, JsonPathExpressionEngine engine, JsonNode node) throws PolicyException {
		if(yamlUtils!=null) {
			// nop
		}
		String prefixPattern = "$.hazelcast.network.";
		try {
			JsonNode result = engine.getJsonNodeMatchPattern(node, prefixPattern+pattern);
			return result!=null;
		}catch(JsonPathNotFoundException notFound) {
			return false;
		}catch(Throwable t) {
			throw new PolicyException(t.getMessage(),t);
		}
	}
	
	private static void debug(String msg) {
		if(HazelcastManager.log!=null) {
			HazelcastManager.log.debug(msg);
		}
		if(HazelcastManager.logStartup!=null) {
			HazelcastManager.logStartup.debug(msg);
		}
	}
	private static void info(String msg) {
		if(HazelcastManager.log!=null) {
			HazelcastManager.log.info(msg);
		}
		if(HazelcastManager.logStartup!=null) {
			HazelcastManager.logStartup.info(msg);
		}
	}
	private static void error(String msg, Throwable e) {
		if(HazelcastManager.log!=null) {
			HazelcastManager.log.error(msg,e);
		}
		if(HazelcastManager.logStartup!=null) {
			HazelcastManager.logStartup.error(msg,e);
		}
	}
	
	private static final String PREFIX_HAZELCAST_INSTANCE_FOR_TYPE = "Hazelcast instance for type ";
	
	/**
	 * Pulisce i proxy Hazelcast orfani (contatori di intervalli passati).
	 *
	 * <h3>Perché si creano proxy orfani</h3>
	 * <p>
	 * Ogni policy di rate limiting ha un intervallo temporale (minutale, orario, giornaliero).
	 * Per ogni intervallo vengono creati contatori Hazelcast con nomi che includono il timestamp:
	 * <pre>pncounter-GROUPHASH--policyRequestCounter-i-TIMESTAMP-c-CONFIGTIMESTAMP-rl</pre>
	 * </p>
	 *
	 * <h3>Perché non vengono rimossi automaticamente</h3>
	 * <p>
	 * Il codice in DatiCollezionatiDistributed* usa un meccanismo di "cestino a due fasi":
	 * i contatori vengono eliminati al secondo cambio di intervallo per evitare race condition
	 * tra nodi del cluster. Tuttavia questo meccanismo funziona solo se la stessa istanza
	 * che ha creato il contatore riceve richieste anche negli intervalli successivi.
	 * </p>
	 *
	 * <h3>Scenari che creano orfani</h3>
	 * <ul>
	 *   <li><b>Restart del nodo</b>: dopo un restart la mappa locale è vuota, quindi non sa quali contatori eliminare</li>
	 *   <li><b>Traffico sporadico</b>: se un gruppo (es. un IP) fa richieste solo in un intervallo e poi smette</li>
	 *   <li><b>Bilanciamento del carico</b>: se le richieste successive vanno su un nodo diverso</li>
	 *   <li><b>Policy rimosse/modificate</b>: i vecchi contatori restano in Hazelcast</li>
	 * </ul>
	 *
	 * <p>
	 * Questo metodo risolve il problema facendo una scansione periodica basata sul timestamp
	 * nel nome del contatore, indipendentemente dallo stato locale del nodo.
	 * </p>
	 *
	 * @param thresholdMs soglia in millisecondi per considerare un proxy orfano
	 * @return numero di proxy rimossi
	 */
	public static int cleanupOrphanedProxies(long thresholdMs) {
		int removed = 0;
		if(staticMapInstance == null || staticMapInstance.isEmpty()) {
			return removed;
		}

		Logger logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(true);
		
		long now = org.openspcoop2.utils.date.DateManager.getTimeMillis();
		long threshold = now - thresholdMs;
		
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		if(logControlloTraffico!=null) {
			String msg = "Cleanup orphaned proxies: threshold=" + sdf.format(new java.util.Date(threshold)) + " (" + (thresholdMs / 3600000) + " hours ago)";
			logControlloTraffico.info(msg);
		}

		// Pulisco solo per i tipi counter
		PolicyGroupByActiveThreadsType[] counterTypes = {
			PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER,
			PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG,
			PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC
		};

		// Verifica se il registry per AtomicLong è abilitato
		boolean atomicLongRegistryEnabled = OpenSPCoop2Properties.getInstance().isControlloTrafficoGestorePolicyInMemoryHazelcastAtomicLongRegistryEnabled();

		// Verifica se il conteggio delle policy attive è abilitato
		boolean activePoliciesCountLogEnabled = OpenSPCoop2Properties.getInstance().isControlloTrafficoGestorePolicyInMemoryHazelcastActivePoliciesCountLogEnabled();

		for (PolicyGroupByActiveThreadsType type : counterTypes) {
			HazelcastInstance hazelcast = staticMapInstance.get(type);
			if(hazelcast == null) {
				continue;
			}

			// Verifica se l'istanza Hazelcast è ancora attiva (evita errori durante lo shutdown)
			if(!hazelcast.getLifecycleService().isRunning()) {
				if(logControlloTraffico!=null) {
					String msg = PREFIX_HAZELCAST_INSTANCE_FOR_TYPE + type + " is not active, skipping cleanup";
					logControlloTraffico.debug(msg);
				}
				continue;
			}

			// Per ATOMIC_LONG e ATOMIC_LONG_ASYNC, usa il registry se abilitato
			// (gli oggetti del CP Subsystem non sono enumerabili tramite getDistributedObjects())
			if((type == PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG ||
				type == PolicyGroupByActiveThreadsType.HAZELCAST_ATOMIC_LONG_ASYNC) && atomicLongRegistryEnabled) {
				removed += cleanupOrphanedAtomicLongCounters(type, thresholdMs, logControlloTraffico, sdf, activePoliciesCountLogEnabled);
				continue;
			}

			try {
				java.util.Collection<com.hazelcast.core.DistributedObject> objects = hazelcast.getDistributedObjects();

				int countWithInterval = 0;
				int countWithoutInterval = 0;
				int countRecent = 0;
				int removedForType = 0;
				int removedWithoutInterval = 0;
				long oldestInterval = Long.MAX_VALUE;
				long newestIntervalRemoved = Long.MIN_VALUE;
				long newestInterval = Long.MIN_VALUE;

				// Raccolta policy attive (solo quelle con contatori recenti)
				List<String> activePolicyIds = new ArrayList<>();
				// Raccolta oggetti senza intervallo per la seconda passata
				List<com.hazelcast.core.DistributedObject> objectsWithoutInterval = new ArrayList<>();
				boolean isPNCounter = (type == PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER);

				// Prima passata: rimuove i contatori con intervallo orfani e identifica le policy attive
				for (com.hazelcast.core.DistributedObject obj : objects) {
					String name = obj.getName();
					if(name == null) {
						continue;
					}

					// Ignora la IMap del registry (non è un contatore)
					if(ATOMIC_LONG_REGISTRY_MAP_NAME.equals(name)) {
						continue;
					}

					// Pulisce SOLO oggetti con intervallo (-i-)
					// Pattern: ...-policyRequestCounter-i-TIMESTAMP-c-...
					// Gli oggetti senza -i- (es. activeRequestCounter, updatePolicyDate) sono legati al gruppo
					// e devono essere distrutti solo quando il gruppo viene rimosso
					int intervalIndex = name.indexOf("-i-");
					if(intervalIndex < 0) {
						// Oggetto senza intervallo, lo salviamo per la seconda passata
						countWithoutInterval++;
						objectsWithoutInterval.add(obj);
						continue;
					}

					countWithInterval++;

					// Estrae il timestamp dell'intervallo
					int timestampStart = intervalIndex + 3;
					int timestampEnd = name.indexOf("-c-", timestampStart);
					if(timestampEnd < 0) {
						timestampEnd = name.length();
					}

					String timestampStr = name.substring(timestampStart, timestampEnd);
					try {
						long timestamp = Long.parseLong(timestampStr);

						// Traccia intervallo più vecchio e più recente
						if(timestamp < oldestInterval) {
							oldestInterval = timestamp;
						}
						if(timestamp > newestInterval) {
							newestInterval = timestamp;
						}

						if(timestamp < threshold) {
							// Il proxy è orfano (intervallo passato), lo distruggo
							try {
								obj.destroy();
								removed++;
								removedForType++;
								if(logControlloTraffico!=null) {
									String msg = "Destroyed orphaned proxy: " + name + " (intervalDate=" + sdf.format(new java.util.Date(timestamp)) + ")";
									logControlloTraffico.debug(msg);
								}
								if(timestamp > newestIntervalRemoved) {
									newestIntervalRemoved = timestamp;
								}
							} catch(Throwable t) {
								if(logControlloTraffico!=null) {
									String msg = "Error destroying orphaned proxy " + name + ": " + t.getMessage();
									logControlloTraffico.error(msg,t);
								}
							}
						} else {
							countRecent++;
							// Policy attiva: estrae e raccoglie il policyId se non già presente
							String policyId = extractPolicyId(name, isPNCounter);
							if(policyId != null && !activePolicyIds.contains(policyId)) {
								activePolicyIds.add(policyId);
							}
						}
					} catch(NumberFormatException nfe) {
						// Non riesco a parsare il timestamp, salto questo proxy
						if(logControlloTraffico!=null) {
							String msg = "Cannot parse timestamp from proxy name: " + name;
							logControlloTraffico.debug(msg);
						}
					}
				}

				// Seconda passata: rimuove i contatori senza intervallo per policy non più attive
				for (com.hazelcast.core.DistributedObject obj : objectsWithoutInterval) {
					String name = obj.getName();
					String policyId = extractPolicyId(name, isPNCounter);
					if(policyId != null && !activePolicyIds.contains(policyId)) {
						// Policy non più attiva, rimuove il contatore senza intervallo
						try {
							obj.destroy();
							removed++;
							removedWithoutInterval++;
							if(logControlloTraffico!=null) {
								String msg = "Destroyed orphaned counter without interval: " + name + " (policy no longer active)";
								logControlloTraffico.debug(msg);
							}
						} catch(Throwable t) {
							if(logControlloTraffico!=null) {
								String msg = "Error destroying orphaned counter without interval " + name + ": " + t.getMessage();
								logControlloTraffico.error(msg,t);
							}
						}
					}
				}

				StringBuilder sb = new StringBuilder();
				int finalWithInterval = countWithInterval - removedForType;
				int finalWithoutInterval = countWithoutInterval - removedWithoutInterval;
				int total = finalWithInterval + finalWithoutInterval;
				sb.append("Type ").append(type).append(": total=").append(total);
				sb.append(", withInterval=").append(finalWithInterval);
				sb.append(", withoutInterval=").append(finalWithoutInterval);
				sb.append(", recent=").append(countRecent);
				sb.append(", removedWithInterval=").append(removedForType);
				if(removedWithoutInterval > 0) {
					sb.append(", removedWithoutInterval=").append(removedWithoutInterval);
				}
				if(countWithInterval > 0 && oldestInterval != Long.MAX_VALUE) {
					sb.append(", oldest=").append(sdf.format(new java.util.Date(oldestInterval)));
				}
				if(countWithInterval > 0 && newestInterval != Long.MIN_VALUE) {
					sb.append(", newest=").append(sdf.format(new java.util.Date(newestInterval)));
				}
				if(countWithInterval > 0 && newestIntervalRemoved != Long.MIN_VALUE) {
					sb.append(", newest-removed=").append(sdf.format(new java.util.Date(newestIntervalRemoved)));
				}
				if(activePoliciesCountLogEnabled) {
					sb.append(", activePolicies=").append(activePolicyIds.size());
				}
				if(logControlloTraffico!=null) {
					String msg = sb.toString();
					logControlloTraffico.info(msg);
				}

				// Per PNCOUNTER, pulisce e mostra anche i contatori delle date dal registry (sono AtomicLong nel CP Subsystem)
				if(type == PolicyGroupByActiveThreadsType.HAZELCAST_PNCOUNTER && atomicLongRegistryEnabled) {
					removed += cleanupAndLogAtomicLongRegistryForPNCounter(type, activePolicyIds, logControlloTraffico, sdf);
				}

			} catch(Throwable t) {
				if(logControlloTraffico!=null) {
					String msg = "Error during cleanup of orphaned proxies for type " + type + ": " + t.getMessage();
					logControlloTraffico.error(msg,t);
				}
			}
		}

		if(logControlloTraffico!=null) {
			String msg = "Cleanup orphaned proxies finished: removed " + removed + " proxies";
			logControlloTraffico.info(msg);
		}

		return removed;
	}

	/**
	 * Registra un contatore AtomicLong nel registry IMap.
	 * Viene usato per tracciare i contatori del CP Subsystem che non sono enumerabili tramite getDistributedObjects().
	 *
	 * @param type tipo di policy (HAZELCAST_ATOMIC_LONG o HAZELCAST_ATOMIC_LONG_ASYNC)
	 * @param counterName nome del contatore
	 */
	public static void registerAtomicLongCounter(PolicyGroupByActiveThreadsType type, String counterName) {
		if(staticMapInstance == null || counterName == null) {
			return;
		}

		HazelcastInstance hazelcast = staticMapInstance.get(type);
		if(hazelcast == null) {
			return;
		}

		try {
			com.hazelcast.map.IMap<String, Long> registry = hazelcast.getMap(ATOMIC_LONG_REGISTRY_MAP_NAME);
			registry.put(counterName, org.openspcoop2.utils.date.DateManager.getTimeMillis());
		} catch(Throwable t) {
			if(log != null) {
				log.debug("Error registering AtomicLong counter '" + counterName + "': " + t.getMessage(), t);
			}
		}
	}

	/**
	 * Rimuove un contatore AtomicLong dal registry IMap.
	 *
	 * @param type tipo di policy (HAZELCAST_ATOMIC_LONG o HAZELCAST_ATOMIC_LONG_ASYNC)
	 * @param counterName nome del contatore
	 */
	public static void unregisterAtomicLongCounter(PolicyGroupByActiveThreadsType type, String counterName) {
		if(staticMapInstance == null || counterName == null) {
			return;
		}

		HazelcastInstance hazelcast = staticMapInstance.get(type);
		if(hazelcast == null) {
			return;
		}

		try {
			com.hazelcast.map.IMap<String, Long> registry = hazelcast.getMap(ATOMIC_LONG_REGISTRY_MAP_NAME);
			registry.remove(counterName);
		} catch(Throwable t) {
			if(log != null) {
				log.debug("Error unregistering AtomicLong counter '" + counterName + "': " + t.getMessage(), t);
			}
		}
	}

	/**
	 * Pulisce i contatori AtomicLong orfani usando il registry IMap.
	 *
	 * @param type tipo di policy (HAZELCAST_ATOMIC_LONG o HAZELCAST_ATOMIC_LONG_ASYNC)
	 * @param thresholdMs soglia in millisecondi per considerare un contatore orfano
	 * @param logControlloTraffico logger
	 * @param sdf formato data per il log
	 * @param activePoliciesCountLogEnabled true se il conteggio delle policy attive è abilitato
	 * @return numero di contatori rimossi
	 */
	private static int cleanupOrphanedAtomicLongCounters(PolicyGroupByActiveThreadsType type, long thresholdMs,
			Logger logControlloTraffico, java.text.SimpleDateFormat sdf, boolean activePoliciesCountLogEnabled) {
		int removed = 0;

		HazelcastInstance hazelcast = staticMapInstance.get(type);
		if(hazelcast == null) {
			return removed;
		}

		// Verifica se l'istanza Hazelcast è ancora attiva (evita errori durante lo shutdown)
		if(!hazelcast.getLifecycleService().isRunning()) {
			if(logControlloTraffico != null) {
				String msg = PREFIX_HAZELCAST_INSTANCE_FOR_TYPE + type + " is not active, skipping AtomicLong cleanup";
				logControlloTraffico.debug(msg);
			}
			return removed;
		}

		long now = org.openspcoop2.utils.date.DateManager.getTimeMillis();
		long threshold = now - thresholdMs;

		try {
			com.hazelcast.map.IMap<String, Long> registry = hazelcast.getMap(ATOMIC_LONG_REGISTRY_MAP_NAME);

			int countWithInterval = 0;
			int countWithoutInterval = 0;
			int countRecent = 0;
			int removedForType = 0;
			int removedWithoutInterval = 0;
			long oldestInterval = Long.MAX_VALUE;
			long newestIntervalRemoved = Long.MIN_VALUE;
			long newestInterval = Long.MIN_VALUE;

			// Raccolta policy attive (con almeno un contatore recente con intervallo)
			List<String> activePolicyIds = new ArrayList<>();
			// Raccolta contatori senza intervallo per la seconda passata
			List<String> countersWithoutInterval = new ArrayList<>();

			// Prima passata: elabora contatori CON intervallo e raccogli quelli SENZA
			for (java.util.Map.Entry<String, Long> entry : registry.entrySet()) {
				String name = entry.getKey();

				if(name == null) {
					continue;
				}

				int intervalIndex = name.indexOf("-i-");
				if(intervalIndex < 0) {
					// Contatore senza intervallo: lo raccogliamo per la seconda passata
					countWithoutInterval++;
					countersWithoutInterval.add(name);
					continue;
				}

				countWithInterval++;

				// Estrae il timestamp dell'intervallo
				int timestampStart = intervalIndex + 3;
				int timestampEnd = name.indexOf("-c-", timestampStart);
				if(timestampEnd < 0) {
					timestampEnd = name.length();
				}

				String timestampStr = name.substring(timestampStart, timestampEnd);
				try {
					long timestamp = Long.parseLong(timestampStr);

					if(timestamp < oldestInterval) {
						oldestInterval = timestamp;
					}
					if(timestamp > newestInterval) {
						newestInterval = timestamp;
					}

					if(timestamp < threshold) {
						// Il contatore è orfano, lo distruggo
						try {
							hazelcast.getCPSubsystem().getAtomicLong(name).destroy();
							registry.remove(name);
							removed++;
							removedForType++;
							if(logControlloTraffico != null) {
								String msg = "Destroyed orphaned AtomicLong counter: " + name + " (intervalDate=" + sdf.format(new java.util.Date(timestamp)) + ")";
								logControlloTraffico.debug(msg);
							}
							if(timestamp > newestIntervalRemoved) {
								newestIntervalRemoved = timestamp;
							}
						} catch(Throwable t) {
							if(logControlloTraffico != null) {
								String msg = "Error destroying orphaned AtomicLong counter " + name + ": " + t.getMessage();
								logControlloTraffico.error(msg, t);
							}
						}
					} else {
						countRecent++;
						// Contatore recente: la policy è attiva
						String policyId = extractPolicyId(name, false);
						if(policyId != null && !activePolicyIds.contains(policyId)) {
							activePolicyIds.add(policyId);
						}
					}
				} catch(NumberFormatException nfe) {
					if(logControlloTraffico != null) {
						String msg = "Cannot parse timestamp from counter name: " + name;
						logControlloTraffico.debug(msg);
					}
				}
			}

			// Seconda passata: rimuovi i contatori senza intervallo di policy non più attive
			for(String name : countersWithoutInterval) {
				String policyId = extractPolicyId(name, false);
				if(policyId != null && !activePolicyIds.contains(policyId)) {
					// La policy non ha più contatori recenti, rimuovi anche i contatori senza intervallo
					try {
						hazelcast.getCPSubsystem().getAtomicLong(name).destroy();
						registry.remove(name);
						removed++;
						removedWithoutInterval++;
						if(logControlloTraffico != null) {
							String msg = "Destroyed orphaned AtomicLong counter (no active policy): " + name;
							logControlloTraffico.debug(msg);
						}
					} catch(Throwable t) {
						if(logControlloTraffico != null) {
							String msg = "Error destroying orphaned AtomicLong counter " + name + ": " + t.getMessage();
							logControlloTraffico.error(msg, t);
						}
					}
				}
			}

			StringBuilder sb = new StringBuilder();
			int finalWithInterval = countWithInterval - removedForType;
			int finalWithoutInterval = countWithoutInterval - removedWithoutInterval;
			int total = finalWithInterval + finalWithoutInterval;
			sb.append("Type ").append(type).append(" (via registry): total=").append(total);
			sb.append(", withInterval=").append(finalWithInterval);
			sb.append(", withoutInterval=").append(finalWithoutInterval);
			sb.append(", recent=").append(countRecent);
			sb.append(", removedWithInterval=").append(removedForType);
			sb.append(", removedWithoutInterval=").append(removedWithoutInterval);
			if(countWithInterval > 0 && oldestInterval != Long.MAX_VALUE) {
				sb.append(", oldest=").append(sdf.format(new java.util.Date(oldestInterval)));
			}
			if(countWithInterval > 0 && newestInterval != Long.MIN_VALUE) {
				sb.append(", newest=").append(sdf.format(new java.util.Date(newestInterval)));
			}
			if(countWithInterval > 0 && newestIntervalRemoved != Long.MIN_VALUE) {
				sb.append(", newest-removed=").append(sdf.format(new java.util.Date(newestIntervalRemoved)));
			}
			if(activePoliciesCountLogEnabled) {
				sb.append(", activePolicies=").append(activePolicyIds.size());
			}
			if(logControlloTraffico != null) {
				String msg = sb.toString();
				logControlloTraffico.info(msg);
			}

		} catch(Throwable t) {
			if(logControlloTraffico != null) {
				String msg = "Error during cleanup of orphaned AtomicLong counters for type " + type + ": " + t.getMessage();
				logControlloTraffico.error(msg, t);
			}
		}

		return removed;
	}

	/**
	 * Estrae l'identificativo della policy (groupByPolicyMapIdHashCode) dal nome del contatore.
	 *
	 * @param counterName nome del contatore (es. "pncounter-1193533566--policyRequestCounter-i-..." o "1193533566-policyRequestCounter-i-...")
	 * @param isPNCounter true se è un contatore PNCounter (ha prefisso "pncounter-")
	 * @return l'hashcode della policy o null se non trovato
	 */
	private static String extractPolicyId(String counterName, boolean isPNCounter) {
		if(counterName == null) {
			return null;
		}
		String name = counterName;
		// Per PNCOUNTER il formato è: pncounter-HASHCODE--tipo-i-...
		// Per AtomicLong il formato è: HASHCODE-tipo-i-...
		if(isPNCounter) {
			String prefix = "pncounter-";
			if(name.startsWith(prefix)) {
				name = name.substring(prefix.length());
			}
			// Per PNCounter cerchiamo il doppio trattino "--" che separa l'hashcode dal tipo
			int doubleHyphenIndex = name.indexOf("--");
			if(doubleHyphenIndex > 0) {
				return name.substring(0, doubleHyphenIndex);
			}
		} else {
			// Per AtomicLong il formato è: HASHCODE-tipo... dove tipo inizia con una lettera
			// L'hashcode può essere negativo (es. -1193533566), quindi cerchiamo il primo "-" seguito da lettera
			for(int i = 0; i < name.length() - 1; i++) {
				if(name.charAt(i) == '-' && Character.isLetter(name.charAt(i + 1))) {
					return name.substring(0, i);
				}
			}
		}
		return null;
	}

	/**
	 * Pulisce e mostra nel log i contatori AtomicLong (date) registrati nel registry per PNCOUNTER.
	 * Questi sono i contatori delle date che non sono visibili tramite getDistributedObjects() perché
	 * sono nel CP Subsystem.
	 * I contatori delle date per policy non più attive vengono rimossi.
	 *
	 * @param type tipo di policy
	 * @param activePolicyIds lista delle policy attive (con contatori PNCounter recenti)
	 * @param logControlloTraffico logger
	 * @param sdf formato data per il log
	 * @return numero di contatori rimossi
	 */
	private static int cleanupAndLogAtomicLongRegistryForPNCounter(PolicyGroupByActiveThreadsType type,
			List<String> activePolicyIds, Logger logControlloTraffico, java.text.SimpleDateFormat sdf) {
		int removed = 0;

		if(staticMapInstance == null) {
			return removed;
		}

		HazelcastInstance hazelcast = staticMapInstance.get(type);
		if(hazelcast == null) {
			return removed;
		}

		// Verifica se l'istanza Hazelcast è ancora attiva (evita errori durante lo shutdown)
		if(!hazelcast.getLifecycleService().isRunning()) {
			if(logControlloTraffico != null) {
				String msg = PREFIX_HAZELCAST_INSTANCE_FOR_TYPE + type + " is not active, skipping AtomicLong dates cleanup";
				logControlloTraffico.debug(msg);
			}
			return removed;
		}

		try {
			com.hazelcast.map.IMap<String, Long> registry = hazelcast.getMap(ATOMIC_LONG_REGISTRY_MAP_NAME);
			if(registry != null && !registry.isEmpty()) {
				int countWithInterval = 0;
				int countWithoutInterval = 0;
				int removedWithInterval = 0;
				int removedWithoutInterval = 0;

				// Raccoglie i nomi per evitare ConcurrentModificationException
				List<String> counterNames = new ArrayList<>(registry.keySet());

				for (String name : counterNames) {
					if(name == null) {
						continue;
					}

					// Estrae il policyId (per AtomicLong delle date, il formato è lo stesso di PNCounter senza prefisso)
					// Il nome è tipo: "1193533566-updatePolicyDate" o "1193533566-updatePolicyDate-i-TIMESTAMP"
					String policyId = extractPolicyId(name, false);

					boolean hasInterval = name.contains("-i-");
					if(hasInterval) {
						countWithInterval++;
					} else {
						countWithoutInterval++;
					}

					// Se la policy non è più attiva, rimuove il contatore delle date
					if(policyId != null && !activePolicyIds.contains(policyId)) {
						try {
							hazelcast.getCPSubsystem().getAtomicLong(name).destroy();
							registry.remove(name);
							removed++;
							if(hasInterval) {
								removedWithInterval++;
							} else {
								removedWithoutInterval++;
							}
							if(logControlloTraffico != null) {
								String msg = "Destroyed orphaned AtomicLong date counter: " + name + " (policy no longer active)";
								logControlloTraffico.debug(msg);
							}
						} catch(Throwable t) {
							if(logControlloTraffico != null) {
								String msg = "Error destroying orphaned AtomicLong date counter " + name + ": " + t.getMessage();
								logControlloTraffico.error(msg, t);
							}
						}
					}
				}

				if(logControlloTraffico != null) {
					int finalWithInterval = countWithInterval - removedWithInterval;
					int finalWithoutInterval = countWithoutInterval - removedWithoutInterval;
					int total = finalWithInterval + finalWithoutInterval;
					String msg = "Type " + type + " (AtomicLong dates via registry): total=" + total +
							", withInterval=" + finalWithInterval + ", withoutInterval=" + finalWithoutInterval +
							", removed=" + (removedWithInterval + removedWithoutInterval);
					logControlloTraffico.info(msg);
				}
			}
		} catch(Throwable t) {
			if(logControlloTraffico != null) {
				logControlloTraffico.debug("Error during cleanup of AtomicLong dates registry for " + type + ": " + t.getMessage(), t);
			}
		}

		return removed;
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
