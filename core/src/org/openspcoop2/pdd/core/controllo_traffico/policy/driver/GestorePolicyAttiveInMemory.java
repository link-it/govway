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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.activation.FileDataSource;

import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.ConfigurazioneControlloTraffico;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.DatiTransazione;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreads;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreadsInMemory;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyShutdownException;
import org.openspcoop2.core.controllo_traffico.utils.serializer.JaxbDeserializer;
import org.openspcoop2.core.controllo_traffico.utils.serializer.JaxbSerializer;
import org.openspcoop2.pdd.config.DynamicClusterManager;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.HazelcastManager;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.PolicyGroupByActiveThreadsDistributedLocalCache;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.PolicyGroupByActiveThreadsDistributedNearCache;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.PolicyGroupByActiveThreadsDistributedNearCacheWithoutEntryProcessorPutAsync;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.PolicyGroupByActiveThreadsDistributedNearCacheWithoutEntryProcessorPutSync;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.PolicyGroupByActiveThreadsDistributedNoCache;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.hazelcast.PolicyGroupByActiveThreadsDistributedReplicatedMap;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.PolicyGroupByActiveThreadsDistributedRedis;
import org.openspcoop2.pdd.core.controllo_traffico.policy.driver.redisson.RedissonManager;
import org.openspcoop2.pdd.services.OpenSPCoop2Startup;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.slf4j.Logger;

/**     
 * GestorePolicyAttiveInMemory
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestorePolicyAttiveInMemory implements IGestorePolicyAttive {


	/** 
	 * Threads allocati sulle Policy. La chiave è l'active-policy-id
	 **/
	private Map<String, IPolicyGroupByActiveThreadsInMemory> mapActiveThreadsPolicy = 
			new HashMap<String, IPolicyGroupByActiveThreadsInMemory>();
	private final org.openspcoop2.utils.Semaphore lock = new org.openspcoop2.utils.Semaphore("GestorePolicyAttiveInMemory");
	
	private static final String IMPL_DESCR = "Implementazione InMemory IGestorePolicyAttive";
	public static String getImplDescr(){
		return IMPL_DESCR;
	}
	
	private Logger log;
	private PolicyGroupByActiveThreadsType type;
	@Override
	public void initialize(Logger log, PolicyGroupByActiveThreadsType type, Object ... params) throws PolicyException{
		this.log = log;
		this.type = type;
		if(this.type==null) {
			this.type = PolicyGroupByActiveThreadsType.LOCAL;
		}
		
		switch (this.type) {
		case LOCAL:
			break;
		case LOCAL_DIVIDED_BY_NODES:
			if(!DynamicClusterManager.isInitialized()) {
				try {
					DynamicClusterManager.initStaticInstance();
					DynamicClusterManager.getInstance().setRateLimitingGestioneCluster(true);
					DynamicClusterManager.getInstance().register(log);
					OpenSPCoop2Startup.startTimerClusterDinamicoThread();
				}catch(Exception e) {
					throw new PolicyException(e.getMessage(),e);
				}
			}
			else {
				try {
					DynamicClusterManager.getInstance().setRateLimitingGestioneCluster(true);
				}catch(Exception e) {
					throw new PolicyException(e.getMessage(),e);
				}
			}
			break;
			
		case DATABASE:
			break;
		
		case HAZELCAST:
		case HAZELCAST_NEAR_CACHE:
		case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
		case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
		case HAZELCAST_PNCOUNTER:
		case HAZELCAST_ATOMIC_LONG:
		case HAZELCAST_ATOMIC_LONG_ASYNC:
		case HAZELCAST_REPLICATED_MAP:
			HazelcastManager.getInstance(this.type);
			break;
		case HAZELCAST_LOCAL_CACHE:
			HazelcastManager.getInstance(this.type);
			if(!OpenSPCoop2Startup.isStartedTimerClusteredRateLimitingLocalCache()) {
				try {
					OpenSPCoop2Startup.startTimerClusteredRateLimitingLocalCache(this);
				}catch(Exception e) {
					throw new PolicyException(e.getMessage(),e);
				}
			}
			break;

		case REDISSON_MAP:
		case REDISSON_ATOMIC_LONG:
		case REDISSON_LONGADDER:
			RedissonManager.getRedissonClient();
			break;
		}
	}
	
	private boolean isStop = false;
	
	
	@Override
	public PolicyGroupByActiveThreadsType getType() {
		return this.type;
	}
	
	@Override
	public IPolicyGroupByActiveThreads getActiveThreadsPolicy(ActivePolicy activePolicy,DatiTransazione datiTransazione, Object state) throws PolicyShutdownException,PolicyException {		
		
		String uniqueIdMap = UniqueIdentifierUtilities.getUniqueId(activePolicy.getInstanceConfiguration());
				
		//synchronized (this.mapActiveThreadsPolicy) {
		this.lock.acquireThrowRuntime("getActiveThreadsPolicy(ActivePolicy)");
		try {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			IPolicyGroupByActiveThreadsInMemory active = null;
			//System.out.println("@@@ getActiveThreadsPolicy["+uniqueIdMap+"] contains["+this.mapActiveThreadsPolicy.containsKey(uniqueIdMap)+"]...");
			if(this.mapActiveThreadsPolicy.containsKey(uniqueIdMap)){
				active = this.mapActiveThreadsPolicy.get(uniqueIdMap);
				//System.out.println("@@@ getActiveThreadsPolicy["+uniqueIdMap+"] GET");
			}
			else{
				active = newPolicyGroupByActiveThreadsInMemory(activePolicy, uniqueIdMap, datiTransazione, state);
				this.mapActiveThreadsPolicy.put(uniqueIdMap, active);
				//System.out.println("@@@ getActiveThreadsPolicy["+uniqueIdMap+"] CREATE");
			}
			return active;
		}finally {
			this.lock.release("getActiveThreadsPolicy(ActivePolicy)");
		}
	}
	@Override
	public IPolicyGroupByActiveThreads getActiveThreadsPolicy(String uniqueIdMap) throws PolicyShutdownException,PolicyException,PolicyNotFoundException { // usata per la remove
		//synchronized (this.mapActiveThreadsPolicy) {
		this.lock.acquireThrowRuntime("getActiveThreadsPolicy(uniqueIdMap)");
		try {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			IPolicyGroupByActiveThreads active = null;
			//System.out.println("@@@ getActiveThreadsPolicy["+uniqueIdMap+"] contains["+this.mapActiveThreadsPolicy.containsKey(uniqueIdMap)+"]...");
			if(this.mapActiveThreadsPolicy.containsKey(uniqueIdMap)){
				active = this.mapActiveThreadsPolicy.get(uniqueIdMap);
				//System.out.println("@@@ getActiveThreadsPolicy["+uniqueIdMap+"] GET");
			}
			else{
				throw new PolicyNotFoundException("ActivePolicy ["+uniqueIdMap+"] notFound");
			}
			return active;
		}finally{
			this.lock.release("getActiveThreadsPolicy(uniqueIdMap)");
		}
	}
	
	@Override
	public long sizeActivePolicyThreads(boolean sum) throws PolicyShutdownException,PolicyException{
		//synchronized (this.mapActiveThreadsPolicy) {
		this.lock.acquireThrowRuntime("sizeActivePolicyThreads");
		try {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			if(sum){
				long sumLong = 0;
				if(this.mapActiveThreadsPolicy!=null && !this.mapActiveThreadsPolicy.isEmpty()) {
					for (String idPolicy : this.mapActiveThreadsPolicy.keySet()) {
						sumLong = sumLong +this.mapActiveThreadsPolicy.get(idPolicy).getActiveThreads();
					}
				}
				return sumLong;
			}else{
				return this.mapActiveThreadsPolicy.size();
			}
		}finally {
			this.lock.release("sizeActivePolicyThreads");
		}
	}
	
	@Override
	public String printKeysPolicy(String separator) throws PolicyShutdownException, PolicyException{
		//synchronized (this.mapActiveThreadsPolicy) {
		this.lock.acquireThrowRuntime("printKeysPolicy");
		try {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			StringBuilder bf = new StringBuilder();
			if(this.mapActiveThreadsPolicy!=null && !this.mapActiveThreadsPolicy.isEmpty()) {
				int i = 0;
				for (String idPolicy : this.mapActiveThreadsPolicy.keySet()) {
					String key = idPolicy;
					if(i>0){
						bf.append(separator);
					}
					bf.append("Cache-"+this.type+"["+i+"]=["+key+"]");
					i++;
				}
			}
			return bf.toString();
		}finally {
			this.lock.release("printKeysPolicy");
		}
	}
	
	@Override
	public String printInfoPolicy(String id, String separatorGroups) throws PolicyShutdownException,PolicyException,PolicyNotFoundException{
		IPolicyGroupByActiveThreadsInMemory activeThreads = (IPolicyGroupByActiveThreadsInMemory) this.getActiveThreadsPolicy(id);	
		try{
			return activeThreads.printInfos(this.log, separatorGroups);
		}catch(Exception e){
			throw new PolicyException(e.getMessage(),e);
		}
	}
	
	@Override
	public void removeActiveThreadsPolicy(String idActivePolicy) throws PolicyShutdownException, PolicyException{
		//synchronized (this.mapActiveThreadsPolicy) {
		this.lock.acquireThrowRuntime("removeActiveThreadsPolicy");
		try {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			if(this.mapActiveThreadsPolicy.containsKey(idActivePolicy)){
				this.mapActiveThreadsPolicy.remove(idActivePolicy);
			}
		}finally {
			this.lock.release("removeActiveThreadsPolicy");
		}
	}
	
	@Override
	public void removeActiveThreadsPolicyUnsafe(String idActivePolicy) throws PolicyShutdownException,PolicyException{
		if(this.isStop){
			throw new PolicyShutdownException("Policy Manager shutdown");
		}
		
		IPolicyGroupByActiveThreadsInMemory policy = this.mapActiveThreadsPolicy.remove(idActivePolicy);
		if(policy!=null) {
			try {
				policy.remove();
			}catch(Throwable e) {
				this.log.error("removeActiveThreadsPolicyUnsafe failed: "+e.getMessage(),e);
			}
		}
	}
	
	@Override
	public void removeAllActiveThreadsPolicy() throws PolicyShutdownException, PolicyException{
		//synchronized (this.mapActiveThreadsPolicy) {
		this.lock.acquireThrowRuntime("removeAllActiveThreadsPolicy");
		try {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			this.mapActiveThreadsPolicy.clear();
		}finally {
			this.lock.release("removeAllActiveThreadsPolicy");
		}
	}
	
	@Override
	public void resetCountersActiveThreadsPolicy(String idActivePolicy) throws PolicyShutdownException, PolicyException{
		//synchronized (this.mapActiveThreadsPolicy) {
		this.lock.acquireThrowRuntime("resetCountersActiveThreadsPolicy");
		try {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			if(this.mapActiveThreadsPolicy.containsKey(idActivePolicy)){
				this.mapActiveThreadsPolicy.get(idActivePolicy).resetCounters();
			}
		}finally {
			this.lock.release("resetCountersActiveThreadsPolicy");
		}
	}
	
	@Override
	public void resetCountersAllActiveThreadsPolicy() throws PolicyShutdownException, PolicyException{
		//synchronized (this.mapActiveThreadsPolicy) {
		this.lock.acquireThrowRuntime("resetCountersAllActiveThreadsPolicy");
		try {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			if(this.mapActiveThreadsPolicy.size()>0){
				for (String key : this.mapActiveThreadsPolicy.keySet()) {
					this.mapActiveThreadsPolicy.get(key).resetCounters();
				}
			}
		}finally {
			this.lock.release("resetCountersAllActiveThreadsPolicy");
		}
	}
	
	public Set<Entry<String, IPolicyGroupByActiveThreadsInMemory>> entrySet() throws PolicyShutdownException, PolicyException{
		Set<Entry<String, IPolicyGroupByActiveThreadsInMemory>> activeThreadsPolicies;
		
		this.lock.acquireThrowRuntime("updateLocalCacheMap");
		try {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			activeThreadsPolicies = this.mapActiveThreadsPolicy.entrySet();
		} finally {
			this.lock.release("updateLocalCacheMap");
		}
		
		return activeThreadsPolicies;
	}
	
	
	// ---- Per salvare
	
	private final static String ZIP_POLICY_PREFIX = "policy-";
	private final static String ZIP_POLICY_ID_ACTIVE_SUFFIX = "-id-active.txt";
	private final static String ZIP_POLICY_CONFIGURAZIONE_POLICY_SUFFIX = "ConfigurazionePolicy.xml";
	private final static String ZIP_POLICY_ATTIVAZIONE_POLICY_SUFFIX = "AttivazionePolicy.xml";
	private final static String ZIP_POLICY_ID_DATI_COLLEZIONATI_POLICY_SUFFIX = "-id-datiCollezionati.txt";
	private final static String ZIP_POLICY_DATI_COLLEZIONATI_POLICY_SUFFIX = "-datiCollezionati.txt";
	
	@Override
	public void serialize(OutputStream out) throws PolicyException{
				
		//synchronized (this.mapActiveThreadsPolicy) {
		try {
			this.lock.acquireThrowRuntime("serialize");
			
			if(this.isStop){
				throw new PolicyException("Already serialized");
			}
			this.isStop = true;
			
			if(this.mapActiveThreadsPolicy==null || this.mapActiveThreadsPolicy.size()<=0){
				return;
			}
			
			ZipOutputStream zipOut = null;
			try{
				zipOut = new ZipOutputStream(out);

				String rootPackageDir = "";
				// Il codice dopo fissa il problema di inserire una directory nel package.
				// Commentare la riga sotto per ripristinare il vecchio comportamento.
				rootPackageDir = Costanti.OPENSPCOOP2_ARCHIVE_ROOT_DIR+File.separatorChar;
				
				// indice 
				int index = 1;
				
				// Chiavi possiedono la policy id
				for (String idActivePolicy : this.mapActiveThreadsPolicy.keySet()) {
					
					// Id File
					String idFileActivePolicy = ZIP_POLICY_PREFIX+index;
					
					// File contenente l'identificativo della policy attivata
					String nomeFile = idFileActivePolicy+ZIP_POLICY_ID_ACTIVE_SUFFIX;
					zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
					zipOut.write(idActivePolicy.getBytes());
					
					// GroupByThread
					IPolicyGroupByActiveThreadsInMemory active = this.mapActiveThreadsPolicy.get(idActivePolicy);
					if(active!=null){
						
						ActivePolicy activePolicy = active.getActivePolicy();
						JaxbSerializer serializer = new JaxbSerializer();
						
						// ConfigurazionePolicy
						if(activePolicy.getConfigurazionePolicy()!=null){
							nomeFile = idFileActivePolicy+File.separatorChar+ZIP_POLICY_CONFIGURAZIONE_POLICY_SUFFIX;
							zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
							zipOut.write(serializer.toByteArray(activePolicy.getConfigurazionePolicy()));
						}
						
						// AttivazionePolicy
						if(activePolicy.getInstanceConfiguration()!=null){
							nomeFile = idFileActivePolicy+File.separatorChar+ZIP_POLICY_ATTIVAZIONE_POLICY_SUFFIX;
							zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
							zipOut.write(serializer.toByteArray(activePolicy.getInstanceConfiguration()));
						}
						
						Map<IDUnivocoGroupByPolicy, DatiCollezionati> map = active.getMapActiveThreads();
						if(map!=null && map.size()>0){
							
							// indice 
							int indexDatoCollezionato = 1;
							
							// Chiavi dei raggruppamenti
							for (IDUnivocoGroupByPolicy idUnivocoGroupByPolicy : map.keySet()) {
								
								// Id Raggruppamento
								String idFileRaggruppamento = idFileActivePolicy+File.separatorChar+"groupBy"+File.separatorChar+"groupBy-"+indexDatoCollezionato;
								
								// File contenente l'identificativo del raggruppamento
								nomeFile = idFileRaggruppamento+ZIP_POLICY_ID_DATI_COLLEZIONATI_POLICY_SUFFIX;
								zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
								zipOut.write(IDUnivocoGroupByPolicy.serialize(idUnivocoGroupByPolicy).getBytes());
								
								// DatiCollezionati
								// NOTA: l'ulteriore directory serve a garantire il corretto ordine di ricostruzione
								DatiCollezionati datiCollezionati = map.get(idUnivocoGroupByPolicy);
								nomeFile = idFileRaggruppamento+File.separatorChar+"dati"+ZIP_POLICY_DATI_COLLEZIONATI_POLICY_SUFFIX;
								zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
								zipOut.write(DatiCollezionati.serialize(datiCollezionati).getBytes());
								
								// increment
								indexDatoCollezionato++;
							}
							
						}
					}
					
					// increment
					index++;
				}
				
				zipOut.flush();

			}catch(Exception e){
				throw new PolicyException(e.getMessage(),e);
			}finally{
				try{
					if(zipOut!=null)
						zipOut.close();
				}catch(Exception eClose){}
			}

		}finally {
			this.lock.release("serialize");
		}
	}
	
	@Override
	public void initialize(InputStream in,ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws PolicyException{
		
		//synchronized (this.mapActiveThreadsPolicy) {
		try {
			this.lock.acquireThrowRuntime("initialize");
			
			if(in==null){
				return;
			}
			
			File f = null;
			ZipFile zipFile = null;
			String entryName = null;
			try{
				
				// Leggo InputStream
				byte [] bytesIn = Utilities.getAsByteArray(in);
				in.close();
				in = null;
				if(bytesIn==null || bytesIn.length<=0){
					return;
				}
				f = File.createTempFile("controlloTraffico", ".tmp");
				FileSystemUtilities.writeFile(f, bytesIn);
				
				// Leggo Struttura ZIP
				try {
					zipFile = new ZipFile(f);
				}catch(Throwable t) {
					this.log.error("Inizializzazione immagine ControlloTraffico precedente allo shutdown non ripristinabile, immagine corrotta: "+t.getMessage(),t);
					return;
				}
				
				JaxbDeserializer deserializer = new JaxbDeserializer();
				
				String rootPackageDir = Costanti.OPENSPCOOP2_ARCHIVE_ROOT_DIR+File.separatorChar;
				
				String rootDir = null;
				
				String idActivePolicy = null;
				ConfigurazionePolicy configurazionePolicy = null;
				AttivazionePolicy attivazionePolicy = null;
				Map<IDUnivocoGroupByPolicy, DatiCollezionati> map = null;
				
				IDUnivocoGroupByPolicy idDatiCollezionati = null;
				
				Iterator<ZipEntry> it = ZipUtilities.entries(zipFile, true);
				while (it.hasNext()) {
					ZipEntry zipEntry = (ZipEntry) it.next();
					entryName = ZipUtilities.operativeSystemConversion(zipEntry.getName());
					
					//System.out.println("FILE NAME:  "+entryName);
					//System.out.println("SIZE:  "+entry.getSize());

					// Il codice dopo fissa il problema di inserire una directory nel package.
					// Commentare la riga sotto per ripristinare il vecchio comportamento.
					if(rootDir==null){
						// Calcolo ROOT DIR
						rootDir=ZipUtilities.getRootDir(entryName);
					}
					
					if(zipEntry.isDirectory()) {
						continue; // directory
					}
					else {
						FileDataSource fds = new FileDataSource(entryName);
						String nome = fds.getName();
						String tipo = nome.substring(nome.lastIndexOf(".")+1,nome.length()); 
						tipo = tipo.toUpperCase();
						//System.out.println("VERIFICARE NAME["+nome+"] TIPO["+tipo+"]");
						
						InputStream inputStream = zipFile.getInputStream(zipEntry);
						byte[]content = Utilities.getAsByteArray(inputStream);
						
						try{
							
							if(entryName.startsWith((rootPackageDir+ZIP_POLICY_PREFIX)) ){
								
								if(entryName.endsWith(ZIP_POLICY_ID_ACTIVE_SUFFIX)){
									
									if(idActivePolicy!=null){
										// salvo precedente immagine
										this.mapActiveThreadsPolicy.put(idActivePolicy, 
												this.buildPolicyGroupByActiveThreads(configurazionePolicy, attivazionePolicy, map, configurazioneControlloTraffico));
										//System.out.println("@@@ RICOSTRUITO ID ACTIVE POLICY ["+idActivePolicy+"]");
										idActivePolicy = null;
										configurazionePolicy = null;
										attivazionePolicy = null;
										map = null;
										idDatiCollezionati = null;
									}
									
									idActivePolicy = new String(content);
									map=new HashMap<IDUnivocoGroupByPolicy, DatiCollezionati>();
									
									//System.out.println("ENTRY ["+idActivePolicy+"] NUOVO ID ["+entryName+"]");
									
								}
								else if(entryName.endsWith(ZIP_POLICY_CONFIGURAZIONE_POLICY_SUFFIX)){
								
									configurazionePolicy = deserializer.readConfigurazionePolicy(content);
									
									//System.out.println("ENTRY ["+idActivePolicy+"] CONFIGURAZIONE POLICY ["+entryName+"]");
									
								}
								else if(entryName.endsWith(ZIP_POLICY_ATTIVAZIONE_POLICY_SUFFIX)){
									
									attivazionePolicy = deserializer.readAttivazionePolicy(content);
									
									//System.out.println("ENTRY ["+idActivePolicy+"] ATTIVAZIONE POLICY ["+entryName+"]");
									
								}
								else if(entryName.endsWith(ZIP_POLICY_ID_DATI_COLLEZIONATI_POLICY_SUFFIX)){
									
									idDatiCollezionati  = IDUnivocoGroupByPolicy.deserialize( new String(content) );
									
									//System.out.println("ENTRY ["+idActivePolicy+"] ID DATI COLLEZIONATI POLICY ["+entryName+"]");
									
								}
								else if(entryName.endsWith(ZIP_POLICY_DATI_COLLEZIONATI_POLICY_SUFFIX)){
									
									if(idDatiCollezionati==null){
										throw new Exception("Identificativo di group by not found");
									}
									map.put(idDatiCollezionati, DatiCollezionati.deserialize( new String(content) ));
																		
									//System.out.println("ENTRY ["+idActivePolicy+"] DATI COLLEZIONATI POLICY ["+entryName+"]");
									
								}
								
							}
							else{
								throw new Exception("Entry unknown");
							}
							
						}finally{
							try{
								if(inputStream!=null){
									inputStream.close();
								}
							}catch(Exception eClose){}
						}
					}
					
				}
				
				if(idActivePolicy!=null){
					// salvo precedente immagine ?
					this.mapActiveThreadsPolicy.put(idActivePolicy, 
							this.buildPolicyGroupByActiveThreads(configurazionePolicy, attivazionePolicy, map, configurazioneControlloTraffico));
					//System.out.println("@@@ RICOSTRUITO FINALE ID ACTIVE POLICY ["+idActivePolicy+"]");
					idActivePolicy = null;
					configurazionePolicy = null;
					attivazionePolicy = null;
					map = null;
					idDatiCollezionati = null;
				}
				
			}catch(Exception e){
				throw new PolicyException("["+entryName+"] "+e.getMessage(),e);
			}
			finally{
				try{
					if(zipFile!=null)
						zipFile.close();
				}catch(Exception eClose){}
				try{
					if(f!=null)
						f.delete();
				}catch(Exception eClose){}
				try{
					if(in!=null)
						in.close();
				}catch(Exception eClose){}
			}
			
			

		}finally {
			this.lock.release("initialize");
		}
	}
	
	@Override
	public void cleanOldActiveThreadsPolicy() throws PolicyException{
		this.lock.acquireThrowRuntime("cleanOldActiveThreadsPolicy");
		try {
			if(this.mapActiveThreadsPolicy!=null && !this.mapActiveThreadsPolicy.isEmpty()) {
				for (String uniqueIdMap : this.mapActiveThreadsPolicy.keySet()) {
					for (PolicyGroupByActiveThreadsType tipo : GestorePolicyAttive.getTipiGestoriAttivi()) {
						if(!tipo.equals(this.type)) {
							//System.out.println("======== RIPULISCO DALLA PARTENZA in '"+tipo+"' ["+uniqueIdMap+"] !!!");
							GestorePolicyAttive.getInstance(tipo).removeActiveThreadsPolicyUnsafe(uniqueIdMap);
						}
					}
				}
			}
		}catch(Exception e){
			throw new PolicyException(e.getMessage(),e);
		}	
		finally {
			this.lock.release("cleanOldActiveThreadsPolicy");
		}
	}
	
	private IPolicyGroupByActiveThreadsInMemory buildPolicyGroupByActiveThreads(ConfigurazionePolicy configurazionePolicy,
			AttivazionePolicy attivazionePolicy, Map<IDUnivocoGroupByPolicy, DatiCollezionati> map,
			ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws Exception{
		
		if(configurazionePolicy==null){
			throw new PolicyException("ConfigurazionePolicy non presente");
		}
		if(attivazionePolicy==null){
			throw new PolicyException("AttivazionePolicy non presente");
		}
		if(configurazioneControlloTraffico==null){
			throw new PolicyException("ConfigurazioneControlloTraffico non presente");
		}
		
		ActivePolicy activePolicy = new ActivePolicy();
		activePolicy.setConfigurazioneControlloTraffico(configurazioneControlloTraffico);
		activePolicy.setConfigurazionePolicy(configurazionePolicy);
		activePolicy.setInstanceConfiguration(attivazionePolicy);
		activePolicy.setTipoRisorsaPolicy(TipoRisorsa.toEnumConstant(configurazionePolicy.getRisorsa(), true));		
		
		String uniqueIdMap = UniqueIdentifierUtilities.getUniqueId(activePolicy.getInstanceConfiguration());
		IPolicyGroupByActiveThreadsInMemory p = newPolicyGroupByActiveThreadsInMemory(activePolicy, uniqueIdMap,
				null, null);
		
		if(map!=null && map.size()>0){
			for (IDUnivocoGroupByPolicy id : map.keySet()) {
				map.get(id).initActiveRequestCounter();
			}
			p.initMap(map);
		}
		
		return p;
		
	}
	
	private IPolicyGroupByActiveThreadsInMemory newPolicyGroupByActiveThreadsInMemory(ActivePolicy activePolicy, String uniqueIdMap,
			DatiTransazione datiTransazione, Object state) throws PolicyException {
		switch (this.type) {
		case LOCAL:
			return new PolicyGroupByActiveThreads(activePolicy, this.type);
		case LOCAL_DIVIDED_BY_NODES:
			return new PolicyGroupByActiveThreads(activePolicy, this.type);
		case DATABASE:
			return new PolicyGroupByActiveThreadsDB(activePolicy, this.type, uniqueIdMap, 
					(state!=null && state instanceof IState) ? ((IState)state) : null, 
					datiTransazione!=null ? datiTransazione.getDominio() : null, 
				    datiTransazione!=null ? datiTransazione.getIdTransazione() : null);
		case HAZELCAST_LOCAL_CACHE:
			return new PolicyGroupByActiveThreadsDistributedLocalCache(activePolicy, uniqueIdMap, HazelcastManager.getInstance(this.type));
		case HAZELCAST_NEAR_CACHE:
			return new PolicyGroupByActiveThreadsDistributedNearCache(activePolicy, uniqueIdMap, HazelcastManager.getInstance(this.type));
		case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
			return new PolicyGroupByActiveThreadsDistributedNearCacheWithoutEntryProcessorPutSync(activePolicy, uniqueIdMap, HazelcastManager.getInstance(this.type));
		case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
			return new PolicyGroupByActiveThreadsDistributedNearCacheWithoutEntryProcessorPutAsync(activePolicy, uniqueIdMap, HazelcastManager.getInstance(this.type));
		case HAZELCAST:
			return new PolicyGroupByActiveThreadsDistributedNoCache(activePolicy, uniqueIdMap, HazelcastManager.getInstance(this.type));
		case HAZELCAST_REPLICATED_MAP:
			return new PolicyGroupByActiveThreadsDistributedReplicatedMap(activePolicy, uniqueIdMap, HazelcastManager.getInstance(this.type));
  	  	case HAZELCAST_PNCOUNTER:
  	  	case HAZELCAST_ATOMIC_LONG:
  	  	case HAZELCAST_ATOMIC_LONG_ASYNC:
  	  	case REDISSON_ATOMIC_LONG:
  	  	case REDISSON_LONGADDER:
  	  		 BuilderDatiCollezionatiDistributed builder = BuilderDatiCollezionatiDistributed.getBuilder(this.type);
             return new PolicyGroupByActiveThreadsDistributedContatoriSingoli(activePolicy, uniqueIdMap,  builder);
		case REDISSON_MAP:
			return new PolicyGroupByActiveThreadsDistributedRedis(activePolicy, uniqueIdMap, RedissonManager.getRedissonClient());
		}
		throw new PolicyException("Unsupported type '"+this.type+"'");
	}
	
}

