/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.activation.FileDataSource;

import org.slf4j.Logger;
import org.openspcoop2.protocol.basic.Costanti;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.ActivePolicy;
import org.openspcoop2.core.controllo_traffico.beans.ConfigurazioneControlloTraffico;
import org.openspcoop2.core.controllo_traffico.beans.DatiCollezionati;
import org.openspcoop2.core.controllo_traffico.beans.IDUnivocoGroupByPolicy;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsa;
import org.openspcoop2.core.controllo_traffico.driver.IGestorePolicyAttive;
import org.openspcoop2.core.controllo_traffico.driver.IPolicyGroupByActiveThreads;
import org.openspcoop2.core.controllo_traffico.driver.PolicyException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyNotFoundException;
import org.openspcoop2.core.controllo_traffico.driver.PolicyShutdownException;
import org.openspcoop2.core.controllo_traffico.utils.serializer.JaxbDeserializer;
import org.openspcoop2.core.controllo_traffico.utils.serializer.JaxbSerializer;

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
	private Hashtable<String, PolicyGroupByActiveThreads> mapActiveThreadsPolicy = 
			new Hashtable<String, PolicyGroupByActiveThreads>();
	
	private static final String IMPL_DESCR = "Implementazione InMemory IGestorePolicyAttive";
	public static String getImplDescr(){
		return IMPL_DESCR;
	}
	
	private Logger log;
	@Override
	public void initialize(Logger log, Object ... params) throws PolicyException{
		this.log = log;
	}
	
	private boolean isStop = false;
	
	
	@Override
	public IPolicyGroupByActiveThreads getActiveThreadsPolicy(ActivePolicy activePolicy) throws PolicyShutdownException,PolicyException {		
		synchronized (this.mapActiveThreadsPolicy) {	
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			String uniqueIdMap = UniqueIdentifierUtilities.getUniqueId(activePolicy.getInstanceConfiguration());
			PolicyGroupByActiveThreads active = null;
			//System.out.println("@@@ getActiveThreadsPolicy["+uniqueIdMap+"] contains["+this.mapActiveThreadsPolicy.containsKey(uniqueIdMap)+"]...");
			if(this.mapActiveThreadsPolicy.containsKey(uniqueIdMap)){
				active = this.mapActiveThreadsPolicy.get(uniqueIdMap);
				//System.out.println("@@@ getActiveThreadsPolicy["+uniqueIdMap+"] GET");
			}
			else{
				active = new PolicyGroupByActiveThreads(activePolicy);
				this.mapActiveThreadsPolicy.put(uniqueIdMap, active);
				//System.out.println("@@@ getActiveThreadsPolicy["+uniqueIdMap+"] CREATE");
			}
			return active;
		}
	}
	@Override
	public IPolicyGroupByActiveThreads getActiveThreadsPolicy(String uniqueIdMap) throws PolicyShutdownException,PolicyException,PolicyNotFoundException { // usata per la remove
		synchronized (this.mapActiveThreadsPolicy) {	
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			PolicyGroupByActiveThreads active = null;
			//System.out.println("@@@ getActiveThreadsPolicy["+uniqueIdMap+"] contains["+this.mapActiveThreadsPolicy.containsKey(uniqueIdMap)+"]...");
			if(this.mapActiveThreadsPolicy.containsKey(uniqueIdMap)){
				active = this.mapActiveThreadsPolicy.get(uniqueIdMap);
				//System.out.println("@@@ getActiveThreadsPolicy["+uniqueIdMap+"] GET");
			}
			else{
				throw new PolicyNotFoundException("ActivePolicy ["+uniqueIdMap+"] notFound");
			}
			return active;
		}
	}
	
	@Override
	public long sizeActivePolicyThreads(boolean sum) throws PolicyShutdownException,PolicyException{
		synchronized (this.mapActiveThreadsPolicy) {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			if(sum){
				long sumLong = 0;
				Enumeration<String> keys = this.mapActiveThreadsPolicy.keys();
				while (keys.hasMoreElements()) {
					String idPolicy = 
							(String) keys.nextElement();
					sumLong = sumLong +this.mapActiveThreadsPolicy.get(idPolicy).getActiveThreads();
				}
				return sumLong;
			}else{
				return this.mapActiveThreadsPolicy.size();
			}
		}
	}
	
	@Override
	public String printKeysPolicy(String separator) throws PolicyShutdownException, PolicyException{
		synchronized (this.mapActiveThreadsPolicy) {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			StringBuilder bf = new StringBuilder();
			Enumeration<String> keys = this.mapActiveThreadsPolicy.keys();
			int i = 0;
			while (keys.hasMoreElements()) {
				String idPolicy = 
						(String) keys.nextElement();
				String key = idPolicy;
				if(i>0){
					bf.append(separator);
				}
				bf.append("Cache["+i+"]=["+key+"]");
				i++;
			}
			return bf.toString();
		}
	}
	
	@Override
	public String printInfoPolicy(String id, String separatorGroups) throws PolicyShutdownException,PolicyException,PolicyNotFoundException{
		PolicyGroupByActiveThreads activeThreads = (PolicyGroupByActiveThreads) this.getActiveThreadsPolicy(id);	
		try{
			return activeThreads.printInfos(this.log, separatorGroups);
		}catch(Exception e){
			throw new PolicyException(e.getMessage(),e);
		}
	}
	
	@Override
	public void removeActiveThreadsPolicy(String idActivePolicy) throws PolicyShutdownException, PolicyException{
		synchronized (this.mapActiveThreadsPolicy) {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			if(this.mapActiveThreadsPolicy.containsKey(idActivePolicy)){
				this.mapActiveThreadsPolicy.remove(idActivePolicy);
			}
		}
	}
	
	@Override
	public void removeAllActiveThreadsPolicy() throws PolicyShutdownException, PolicyException{
		synchronized (this.mapActiveThreadsPolicy) {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			this.mapActiveThreadsPolicy.clear();
		}
	}
	
	@Override
	public void resetCountersActiveThreadsPolicy(String idActivePolicy) throws PolicyShutdownException, PolicyException{
		synchronized (this.mapActiveThreadsPolicy) {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			if(this.mapActiveThreadsPolicy.containsKey(idActivePolicy)){
				this.mapActiveThreadsPolicy.get(idActivePolicy).resetCounters();
			}
		}
	}
	
	@Override
	public void resetCountersAllActiveThreadsPolicy() throws PolicyShutdownException, PolicyException{
		synchronized (this.mapActiveThreadsPolicy) {
			
			if(this.isStop){
				throw new PolicyShutdownException("Policy Manager shutdown");
			}
			
			if(this.mapActiveThreadsPolicy.size()>0){
				Enumeration<String> kesy = this.mapActiveThreadsPolicy.keys();
				while (kesy.hasMoreElements()) {
					String key = (String) kesy.nextElement();
					this.mapActiveThreadsPolicy.get(key).resetCounters();
					
				}
			}
		}
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
		synchronized (this.mapActiveThreadsPolicy) {
			
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
				Enumeration<String> enKeys = this.mapActiveThreadsPolicy.keys();
				while (enKeys.hasMoreElements()) {
					String idActivePolicy = (String) enKeys.nextElement();
					
					// Id File
					String idFileActivePolicy = ZIP_POLICY_PREFIX+index;
					
					// File contenente l'identificativo della policy attivata
					String nomeFile = idFileActivePolicy+ZIP_POLICY_ID_ACTIVE_SUFFIX;
					zipOut.putNextEntry(new ZipEntry(rootPackageDir+nomeFile));
					zipOut.write(idActivePolicy.getBytes());
					
					// GroupByThread
					PolicyGroupByActiveThreads active = this.mapActiveThreadsPolicy.get(idActivePolicy);
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
						
						Hashtable<IDUnivocoGroupByPolicy, DatiCollezionati> map = active.getMapActiveThreads();
						if(map!=null && map.size()>0){
							
							// indice 
							int indexDatoCollezionato = 1;
							
							// Chiavi dei raggruppamenti
							Enumeration<IDUnivocoGroupByPolicy> enRaggruppamentiKeys = map.keys();
							while (enRaggruppamentiKeys.hasMoreElements()) {
								IDUnivocoGroupByPolicy idUnivocoGroupByPolicy = 
										(IDUnivocoGroupByPolicy) enRaggruppamentiKeys.nextElement();
								
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

		}
	}
	
	@Override
	public void initialize(InputStream in,ConfigurazioneControlloTraffico configurazioneControlloTraffico) throws PolicyException{
		synchronized (this.mapActiveThreadsPolicy) {
			
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
				zipFile = new ZipFile(f);
				
				JaxbDeserializer deserializer = new JaxbDeserializer();
				
				String rootPackageDir = Costanti.OPENSPCOOP2_ARCHIVE_ROOT_DIR+File.separatorChar;
				
				String rootDir = null;
				
				String idActivePolicy = null;
				ConfigurazionePolicy configurazionePolicy = null;
				AttivazionePolicy attivazionePolicy = null;
				Hashtable<IDUnivocoGroupByPolicy, DatiCollezionati> map = null;
				
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
									map=new Hashtable<IDUnivocoGroupByPolicy, DatiCollezionati>();
									
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
			
			

		}
	}
	
	private PolicyGroupByActiveThreads buildPolicyGroupByActiveThreads(ConfigurazionePolicy configurazionePolicy,
			AttivazionePolicy attivazionePolicy, Hashtable<IDUnivocoGroupByPolicy, DatiCollezionati> map,
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
		
		PolicyGroupByActiveThreads p = new PolicyGroupByActiveThreads(activePolicy);
		if(map!=null && map.size()>0){
			Enumeration<IDUnivocoGroupByPolicy> enumID = map.keys();
			while (enumID.hasMoreElements()) {
				IDUnivocoGroupByPolicy id = (IDUnivocoGroupByPolicy) enumID.nextElement();
				map.get(id).initActiveRequestCounter();
			}
			p.getMapActiveThreads().putAll(map);
		}
		
		return p;
		
	}
}

