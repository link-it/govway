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

package org.openspcoop2.pdd.core.behaviour.built_in.load_balance;

import java.util.concurrent.atomic.AtomicInteger;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.built_in.load_balance.health_check.HealthCheckConfigurazione;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.SemaphoreLock;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;

/**
 * LoadBalancerPool
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoadBalancerPool implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_WEIGHT = 1; 
	
	private HealthCheckConfigurazione healthCheck = null;
	private boolean debug = false;
	
	public LoadBalancerPool(HealthCheckConfigurazione healthCheck) {
		this.healthCheck = healthCheck;
		this.debug = OpenSPCoop2Properties.getInstance().isLoadBalancerDebug();
	}
	
	@Override
	public String toString() {
		SemaphoreLock lock = this.getLock().acquireThrowRuntime("toString");
		try {
			StringBuilder bf = new StringBuilder();
			bf.append("Connectors: ").append(this.connectorMap.size());
			bf.append("\nTotal Weight: ").append(this.totalWeight.get());
			bf.append("\nPosition: ").append(this.position.get());
			if(this.healthCheck!=null) {
				bf.append("\nPassiveHealtCheck: ").append(this.healthCheck.isPassiveCheckEnabled());
				if(this.healthCheck.isPassiveCheckEnabled()){
					bf.append("\n  Exclude for seconds: ").append(this.healthCheck.getPassiveHealthCheck_excludeForSeconds());
				}
			}
			for (Map.Entry<String,Integer> entry : this.connectorMap.entrySet()) {
				String name = entry.getKey();
				bf.append("\n");
				bf.append("- ").append(name).append(" : ").append(" ( weight:").append(this.connectorMap.get(name));
				if(this.connectorMapActiveConnections.containsKey(name)) {
					bf.append(" activeConnections:").append(this.connectorMapActiveConnections.get(name));
				}
				if(this.connectorMapErrorDate.containsKey(name)) {
					bf.append(" connectionError:").append(DateUtils.getSimpleDateFormatMs().format(this.connectorMapErrorDate.get(name)));
				}
				bf.append(" )");
			}
			return bf.toString();
		}finally {
			this.getLock().release(lock, "toString");
		}
	}
	
	
	private transient org.openspcoop2.utils.Semaphore lockInternal = null;
	private synchronized void initLock() {
		if(this.lockInternal==null) {
			this.lockInternal = new org.openspcoop2.utils.Semaphore("LoadBalancerPool"); 
		}
	}
	public org.openspcoop2.utils.Semaphore getLock(){
		if(this.lockInternal==null) {
			initLock();
		}
		return this.lockInternal;
	}
	private Map<String, Integer> connectorMap = new HashMap<>();
	private Map<String, Integer> connectorMapActiveConnections = new HashMap<>();
	private Map<String, Date> connectorMapErrorDate = new HashMap<>();
	private AtomicInteger totalWeight = new AtomicInteger(0);

	private AtomicInteger position = new AtomicInteger(-1);
	
	
	

	public int getNextPosition(boolean checkByWeight) throws BehaviourException {
		
		if(!isPassiveHealthCheck()) {
			SemaphoreLock lock = this.getLock().acquireThrowRuntime("getNextPosition(active)");
			try {
				return getNextPositionEngine(checkByWeight);
			}finally {
				this.getLock().release(lock, "getNextPosition(active)");
			}
		}
		else {
			SemaphoreLock lock = this.getLock().acquireThrowRuntime("getNextPosition(passive)");
			try {
				int pos = getNextPositionEngine(checkByWeight);
				
				Set<String> setOriginal = this.connectorMap.keySet();
				List<String> serverList = new ArrayList<>();
				if(checkByWeight) {
					serverList.addAll(this.getWeightList(false));
				}
				else {
					serverList.addAll(setOriginal);
				}
				
				Set<String> setAfterPassiveHealthCheck = passiveHealthCheck(setOriginal, false);
				
				// prima verifica
				String selectedConnector = serverList.get(pos);
				if(setAfterPassiveHealthCheck.contains(selectedConnector)) {
					return pos;
				}
				
				// controllo prossime posizioni fino a tornare a quella attuale
				int nextPos = getNextPositionEngine(checkByWeight);
				while(nextPos!=pos) {
					selectedConnector = serverList.get(nextPos);
					if(setAfterPassiveHealthCheck.contains(selectedConnector)) {
						return nextPos;
					}
					nextPos = getNextPositionEngine(checkByWeight);
				}
				
				throw new BehaviourException("Nessun connettore selezionabile (passive health check)");
			}finally {
				this.getLock().release(lock, "getNextPosition(passive)");
			}
		}
		
	}
	private int getNextPositionEngine(boolean checkByWeight) {
		int pos = this.position.incrementAndGet();
		if(checkByWeight) {
			if(pos==this.totalWeight.get()) {
				this.position.set(0);
				pos = 0;
			}
		}
		else {
			if(pos==this.connectorMap.size()) {
				this.position.set(0);
				pos = 0;
			}
		}
		return pos;
	}
	
	
	public List<String> getWeightList(boolean passiveHealthCheck) throws BehaviourException {
		Set<String> servers = this.getConnectorNames(passiveHealthCheck);
		if(servers.isEmpty()) {
			throw new BehaviourException("Nessun connettore selezionabile (passive health check)");
		}
		List<String> serverList = new ArrayList<>();    

		Iterator<String> iterator = servers.iterator();
		while (iterator.hasNext()) {
			String server = iterator.next();
			Integer weight = this.getWeight(server);
			if (weight == null || weight <= 0) {
				weight = LoadBalancerPool.DEFAULT_WEIGHT;
			}
			for (int i = 0; i < weight; i++) {
				serverList.add(server);
			}
		}

		debug("weightList (passiveHealthCheck:"+passiveHealthCheck+"): "+serverList);
		
		return serverList;
	}
	

	private transient org.openspcoop2.utils.Semaphore lockLeastConnectionsIndexInternal = null;
	private synchronized void initLockLeastConnectionsIndex() {
		if(this.lockLeastConnectionsIndexInternal==null) {
			this.lockLeastConnectionsIndexInternal = new org.openspcoop2.utils.Semaphore("LoadBalancerPoolLeastConnections"); 
		}
	}
	public org.openspcoop2.utils.Semaphore getLockLeastConnectionsIndex(){
		if(this.lockLeastConnectionsIndexInternal==null) {
			initLockLeastConnectionsIndex();
		}
		return this.lockLeastConnectionsIndexInternal;
	}
	private int leastConnectionsIndex = 0;
	private String getNextLeastConnectionsConnector(int min, List<String> listMin) {
		if(listMin==null || listMin.isEmpty()) {
			return null;
		}
		// Nel caso vi siano più connettori che sono con lo stesso numero di connessioni, viene effettuato un roundrobin 
		// Serve a evitare che se arrivano richieste simultanee prima della registrazione della nuova connessione (che avviene dopo non in maniera transazione)
		// viene scelto il solito connettore
		SemaphoreLock lock = this.getLockLeastConnectionsIndex().acquireThrowRuntime("getNextLeastConnectionsIndex");
		try {
			int c = 0;
			if(this.leastConnectionsIndex<listMin.size()) {
				c = this.leastConnectionsIndex;
			}
			this.leastConnectionsIndex++;
			
			debug("getNextConnectorLeastConnections minActiveConnections["+min+"] (ConnettoreSelezionato:"+c+"): "+listMin);

			return listMin.get(c);
		}finally{
			this.getLockLeastConnectionsIndex().release(lock, "getNextLeastConnectionsIndex");
		}
	}
	
	public String getNextConnectorLeastConnections() {
		SemaphoreLock lock = this.getLock().acquireThrowRuntime("getNextConnectorLeastConnections");
		try {
			debug("getNextConnectorLeastConnections situazione iniziale ("+this.connectorMapActiveConnections+")");
			Set<String> setKeys = passiveHealthCheck(this.connectorMap.keySet(), false);
			
			List<String> listMin = new ArrayList<>();
			int min = 0;
			if(!this.connectorMapActiveConnections.isEmpty()) {
				min = Integer.MAX_VALUE;
				for (String name : setKeys) {
					min = processNextConnectorLeastConnections(listMin, min, name);
				}
			}

			if(listMin.isEmpty()) {
				listMin.addAll(setKeys);
				debug("getNextConnectorLeastConnections: list is empty");
				
			}
			
			return getNextLeastConnectionsConnector(min, listMin);
			
		}finally{
			this.getLock().release(lock, "getNextConnectorLeastConnections");
		}
	}
	private int processNextConnectorLeastConnections(List<String> listMin, int min, String name) {
		if(!this.connectorMapActiveConnections.containsKey(name)) {
			if(min != 0) {
				min = 0;
				listMin.clear();
			}
			listMin.add(name);
		}
		else {
			int active = this.connectorMapActiveConnections.get(name);
			if(active<min) {
				min = active;
				listMin.clear();
				listMin.add(name);
			}
			else if(active==min) {
				listMin.add(name);
			}
		}
		return min;
	}
	
	public boolean isEmpty() {
		return this.connectorMap.isEmpty();
	}
	
	public Set<String> getConnectorNames(boolean passiveHealthCheck) {
		if(passiveHealthCheck) {
			return passiveHealthCheck(this.connectorMap.keySet(), true);
		}
		else {
			return this.connectorMap.keySet();
		}
	}
	
	public int getWeight(String name) {
		return this.connectorMap.get(name);
	}
	
	public void addConnector(String name) throws BehaviourException {
		this.addConnector(name, DEFAULT_WEIGHT);
	}
	public void addConnector(String name, int weight) throws BehaviourException {
		SemaphoreLock lock = this.getLock().acquireThrowRuntime("addConnector");
		try {
			if(this.connectorMap.containsKey(name)) {
				throw new BehaviourException("Already exists connector '"+name+"'");
			}
			this.connectorMap.put(name, weight);
			this.totalWeight.addAndGet(weight);
		}finally{
			this.getLock().release(lock, "addConnector");
		}
		
	}
	
	
	public void registerConnectionError(String name) {
		SemaphoreLock lock = this.getLock().acquireThrowRuntime("registerConnectionError");
		try {
			if(!this.connectorMapErrorDate.containsKey(name)) {
				// non aggiorniamo eventualmente la data, teniamo la prima
				debug("Registrazione errore di connessione per connettore ["+name+"]");
				this.connectorMapErrorDate.put(name, DateManager.getDate());
			}
			else {
				debug("Registrazione non effettuata dell'errore di connessione per connettore ["+name+"]: gia' presente una entry");
			}
		}finally {
			this.getLock().release(lock, "registerConnectionError");
		}
	}

	public void addActiveConnection(String name) {
		SemaphoreLock lock = this.getLock().acquireThrowRuntime("addActiveConnection");
		try {
			int activeConnections = 0;
			if(this.connectorMapActiveConnections.containsKey(name)) {
				activeConnections = this.connectorMapActiveConnections.remove(name);
			}
			activeConnections++;
			this.connectorMapActiveConnections.put(name, activeConnections);
			debug("Registrazione connessione attiva per connettore ["+name+"] (active:"+activeConnections+")");
		}finally {
			this.getLock().release(lock, "addActiveConnection");
		}
	}
	public void removeActiveConnection(String name) {
		SemaphoreLock lock = this.getLock().acquireThrowRuntime("removeActiveConnection");
		try {
			int activeConnections = 0;
			if(this.connectorMapActiveConnections.containsKey(name)) {
				activeConnections = this.connectorMapActiveConnections.remove(name);
			}
			activeConnections--;
			if(activeConnections>0) {
				this.connectorMapActiveConnections.put(name, activeConnections);
			}
			debug("Rimozione connessione attiva per connettore ["+name+"] (active:"+activeConnections+")");
		}finally {
			this.getLock().release(lock, "removeActiveConnection");
		}
	}
	
	
	protected boolean isPassiveHealthCheck() {
		return this.healthCheck!=null && this.healthCheck.isPassiveCheckEnabled() &&
				this.healthCheck.getPassiveHealthCheck_excludeForSeconds()!=null &&
				this.healthCheck.getPassiveHealthCheck_excludeForSeconds().intValue()>0;
	}
	
	private Set<String> passiveHealthCheck(Set<String> set, boolean syncErase){
		if(!isPassiveHealthCheck() || this.connectorMapErrorDate.isEmpty()) {
			return set;
		}
		
		Date now = DateManager.getDate();
		
		debug("Passive Health Check della lista: "+set);
		
		Set<String> newSet = new HashSet<>();
		List<String> listRimuoviDate = new ArrayList<>();
		
		for (String name : set) {
			if(this.connectorMapErrorDate.containsKey(name)) {
				Date registrationDate = this.connectorMapErrorDate.get(name);
				long registrationDateLong = registrationDate.getTime();
				long registrationDateExpired = registrationDateLong + (this.healthCheck.getPassiveHealthCheck_excludeForSeconds().intValue() * 1000);
				if(registrationDateExpired<now.getTime()) {
					debug("(PassiveHealthCheck) Rilevato errore di connessione scaduto per connettore ["+name+"]");
					listRimuoviDate.add(name);
				}
				else {
					debug("(PassiveHealthCheck) Rilevato errore di connessione non ancora scaduto per connettore ["+name+"]");
					continue; // non e' ancora scaduto
				}
			}
			else {
				debug("(PassiveHealthCheck) Non è presente alcun errore di connessione per il connettore ["+name+"]");
			}
						
			newSet.add(name);
		}
		
		cleanPassiveHealthCheck(listRimuoviDate, now, syncErase);
		
		if(newSet.isEmpty()) {
			// Se tutti i connettori vengono esclusi, non ha senso sospenderli tutti poichè si avrebbe un non servizio anche se poi qualcuno riprende.
			// Per questo motivo si ritornano tutti e se re-inizia il giro di verifica.
			debug("(PassiveHealthCheck) !!FULL!! tutti i connettori del pool risultano sospesi per errori di connessione: "+this.connectorMapErrorDate.keySet());
			Date dateCleaner = DateManager.getDate();
			//synchronized (this.semaphore) { // un altro thread potrebbe già averlo modificato
			SemaphoreLock lock = this.getLock().acquireThrowRuntime("passiveHealthCheck(cleanAllErrorDate)");
			try {
				cleanAllErrorDate(dateCleaner);
			}finally {
				this.getLock().release(lock, "passiveHealthCheck(cleanAllErrorDate)");
			}
			return set;
		}
		else {
			debug("(PassiveHealthCheck) lista di connettori validi: "+newSet);
		}
		
		return newSet;
	}
	private void cleanPassiveHealthCheck(List<String> listRimuoviDate, Date now, boolean syncErase){
		if(listRimuoviDate!=null && !listRimuoviDate.isEmpty()) {
			debug("(PassiveHealthCheck) lista di errori di connessione scaduti: "+listRimuoviDate);
			if(syncErase) {
				//synchronized (this.semaphore) { // un altro thread potrebbe già averlo modificato
				SemaphoreLock lock = this.getLock().acquireThrowRuntime("passiveHealthCheck(date)");
				try {
					cleanErrorDate(listRimuoviDate, now);
				}finally {
					this.getLock().release(lock, "passiveHealthCheck(date)");
				}
			}
			else {
				cleanErrorDate(listRimuoviDate, now);
			}
		}
	}
	private void cleanErrorDate(List<String> listRimuoviDate, Date now) {
		List<String> listDaRimuovere = new ArrayList<>();
		for (String name : listRimuoviDate) {
			if(this.connectorMapErrorDate.containsKey(name)) {
				Date registrationDate = this.connectorMapErrorDate.get(name);
				long registrationDateLong = registrationDate.getTime();
				long registrationDateExpired = registrationDateLong + (this.healthCheck.getPassiveHealthCheck_excludeForSeconds().intValue() * 1000);
				if(registrationDateExpired<now.getTime()) {
					debug("Registro da eliminare l'informazione sull'errore di connessione per il connettore ["+name+"]");
					listDaRimuovere.add(name);
				}
				else {
					debug("Non registro da eliminare l'informazione sull'errore di connessione per il connettore ["+name+"]: la data di registrazione e' stata aggiornata");
				}
			}
		}
		if(!listDaRimuovere.isEmpty()) {
			for (String name : listDaRimuovere) {
				debug("Elimino l'informazione sull'errore di connessione per il connettore ["+name+"]");
				this.connectorMapErrorDate.remove(name);
			}
		}
	}
	private void cleanAllErrorDate(Date now) {
		debug("(HealthCheck) lista di errori di connessione prima della pulizia totale: "+this.connectorMapErrorDate.keySet());
		List<String> listDaRimuovere = new ArrayList<>();
		for (Map.Entry<String,Date> entry : this.connectorMapErrorDate.entrySet()) {
			String name = entry.getKey();
			Date registrationDate = this.connectorMapErrorDate.get(name);
			if(registrationDate.before(now)) {
				debug("(HealthCheck) Registro da eliminare l'informazione sull'errore di connessione per il connettore ["+name+"]");
				listDaRimuovere.add(name);
			}
			else {
				debug("(HealthCheck) Non registro da eliminare l'informazione sull'errore di connessione per il connettore ["+name+"]: la data di registrazione e' stata aggiornata");
			}
		}
		if(!listDaRimuovere.isEmpty()) {
			for (String name : listDaRimuovere) {
				debug("(HealthCheck) Elimino l'informazione sull'errore di connessione per il connettore ["+name+"]");
				this.connectorMapErrorDate.remove(name);
			}
		}
		debug("(HealthCheck) lista di errori di connessione terminata la pulizia totale: "+this.connectorMapErrorDate.keySet());
	}
	
	
	private void debug(String msg) {
		if(this.debug) {
			OpenSPCoop2Logger.getLoggerOpenSPCoopConnettori().debug(msg);
		}
	}
}
