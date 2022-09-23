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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.utils.Utilities;
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

	private static String GOVWAY_INSTANCE_PORT = "GOVWAY_INSTANCE_PORT";
	
	public static Map<PolicyGroupByActiveThreadsType, HazelcastInstance> staticMapInstance = null;
	private static Map<PolicyGroupByActiveThreadsType,String> staticMapConfig = null;
	private static String groupId;
	
	private static YAMLUtils yamlUtils = null;
	private static JsonPathExpressionEngine engine = null;
	
	private static JsonNode sharedConfigNode;
	private static File shareConfigFile;
	
	private static Logger logStartup;
	private static Logger log;
	
	public static synchronized void initialize(Logger logStartup, Logger log, Map<PolicyGroupByActiveThreadsType,String> config, String groupId, File shareConfigFile) throws Exception {
		
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
					debug("(cluster-id"+groupId+") override public-address: " + ncInstance.getPublicAddress());
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
						debug("(cluster-id"+groupId+") override outbound-ports: " + ncInstance.getOutboundPorts());
					}
					if((ncShared.getOutboundPortDefinitions()!=null && !ncShared.getOutboundPortDefinitions().isEmpty())) {
						ncInstance.setOutboundPortDefinitions(ncShared.getOutboundPortDefinitions());
						debug("(cluster-id"+groupId+") override outbound-ports (definitions): " + ncInstance.getOutboundPortDefinitions());
					}
				}
			}
			
			// If you set the reuse-address element to true, the TIME_WAIT state is ignored and you can bind the member to the same port again
			boolean definedShared = isDefined("reuse-address", yamlUtils, engine, HazelcastManager.sharedConfigNode);
			if(definedShared) {
				boolean definedInstance = isDefined("reuse-address", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setReuseAddress(ncShared.isReuseAddress());
					debug("(cluster-id"+groupId+") override reuse-address: " + ncInstance.isReuseAddress());
				}
			}
			
			// the ports that Hazelcast uses to communicate between cluster members
			//ncShared.getPort();
			//ncShared.getPortCount();
			//ncShared.isPortAutoIncrement();
			// L'unico elemento che viene ignorato se presente è il 'port'.
			// Anche se definito verrà ignorato poichè ogni singola configurazione utilizzata in GovWay richiede una porta dedicata.
		    // Per modificarla si deve agire nel file govway.hazelcast-*.yaml presente all'interno dell'archivio govway.ear, 
		    // modificandolo direttamente dentro l'archivio o riportandolo nella directory di configurazione esterna.

			// override join
			if(ncShared.getJoin()!=null) {
				boolean definedInstance = isDefined("join", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setJoin(ncShared.getJoin());
					if(ncInstance.getJoin().getTcpIpConfig()!=null) {
						if(portHazelcastConfigInstance!=null && portHazelcastConfigInstance.intValue()>0) {
							if(ncInstance.getJoin().getTcpIpConfig().getMembers()!=null && !ncInstance.getJoin().getTcpIpConfig().getMembers().isEmpty()) {
								// Se vengono usate le porte, devo clonare la lista e sistemarla con la porta usata sull'attuale istanza
								List<String> newList = new ArrayList<String>();
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
										/*
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
										error("(cluster-id"+groupId+") tpcip analisi member '"+member+"' fallita: " +t.getMessage(),t);
										//newList.add(member);
									}
								}
								ncInstance.getJoin().getTcpIpConfig().setMembers(newList);
							}
						}
					}
					debug("(cluster-id"+groupId+") override join: " + ncInstance.getJoin());
				}
			}
			
			// override interface
			if(ncShared.getInterfaces()!=null && ncShared.getInterfaces().isEnabled()) {
				boolean definedInstance = isDefined("interfaces", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setInterfaces(ncShared.getInterfaces());
					debug("(cluster-id"+groupId+") override interfaces: " + ncInstance.getInterfaces());
				}
			}

			
			// override altre configurazioni avanzate
			// example yaml: https://github.com/hazelcast/hazelcast/blob/master/hazelcast/src/test/java/com/hazelcast/config/YamlConfigBuilderTest.java
			if(ncShared.getIcmpFailureDetectorConfig()!=null && ncShared.getIcmpFailureDetectorConfig().isEnabled()) {
				boolean definedInstance = isDefined("failure-detector", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setIcmpFailureDetectorConfig(ncShared.getIcmpFailureDetectorConfig());
					debug("(cluster-id"+groupId+") override failure-detector icmp: " + ncInstance.getIcmpFailureDetectorConfig());
				}
			}
			if(ncShared.getMemberAddressProviderConfig()!=null && ncShared.getMemberAddressProviderConfig().isEnabled()) {
				boolean definedInstance = isDefined("member-address-provider", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setMemberAddressProviderConfig(ncShared.getMemberAddressProviderConfig());
					debug("(cluster-id"+groupId+") override member-address-provider: " + ncInstance.getMemberAddressProviderConfig());
				}
			}
			if(ncShared.getMemcacheProtocolConfig()!=null && ncShared.getMemcacheProtocolConfig().isEnabled()) {
				boolean definedInstance = isDefined("memcache-protocol", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setMemcacheProtocolConfig(ncShared.getMemcacheProtocolConfig());
					debug("(cluster-id"+groupId+") override memcache-protocol: " + ncInstance.getMemcacheProtocolConfig());
				}
			}
			if(ncShared.getRestApiConfig()!=null && ncShared.getRestApiConfig().isEnabled()) {
				boolean definedInstance = isDefined("rest-api", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setRestApiConfig(ncShared.getRestApiConfig());
					debug("(cluster-id"+groupId+") override rest-api: " + ncInstance.getRestApiConfig());
				}
			}
			if(ncShared.getSocketInterceptorConfig()!=null && ncShared.getSocketInterceptorConfig().getClassName()!=null) {
				boolean definedInstance = isDefined("socket-interceptor", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setSocketInterceptorConfig(ncShared.getSocketInterceptorConfig());
					debug("(cluster-id"+groupId+") override socket-interceptor: " + ncInstance.getSocketInterceptorConfig());
				}
			}
			if(ncShared.getSSLConfig()!=null && ncShared.getSSLConfig().isEnabled()) {
				boolean definedInstance = isDefined("ssl", yamlUtils, engine, instanceNode);
				if(!definedInstance) {
					ncInstance.setSSLConfig(ncShared.getSSLConfig());
					debug("(cluster-id"+groupId+") override ssl: " + ncInstance.getSSLConfig());
				}
			}
			/* Deprecata 
			if(ncShared.getSymmetricEncryptionConfig()!=null && ncShared.getSymmetricEncryptionConfig().isEnabled()) {
				boolean definedInstance = isDefined("symmetric-encryption", yamlUtils, engine, node);
				if(!definedInstance) {
					ncInstance.setSymmetricEncryptionConfig(ncShared.getSymmetricEncryptionConfig());
					debug("(cluster-id"+groupId+") override symmetric-encryption: " + ncInstance.getSymmetricEncryptionConfig());
				}
			}
			*/
	
		}
	}
	
	private static boolean isDefined(String pattern, YAMLUtils yamlUtils, JsonPathExpressionEngine engine, JsonNode node) throws PolicyException {
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
