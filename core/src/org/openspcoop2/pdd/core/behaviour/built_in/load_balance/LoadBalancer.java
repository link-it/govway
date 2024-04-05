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

package org.openspcoop2.pdd.core.behaviour.built_in.load_balance;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;

/**
 * LoadBalancer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoadBalancer {

	private static java.util.Random _rnd = null;
	private static synchronized void initRandomInstance() {
		if(_rnd==null) {
			_rnd = new SecureRandom();
		}
	}
	public static java.util.Random getRandomInstance() {
		if(_rnd==null) {
			initRandomInstance();
		}
		return _rnd;
	}
	
	private PdDContext pddContext;
	private LoadBalancerPool pool;
	private LoadBalancerType type;
	
	public LoadBalancer(LoadBalancerType type, LoadBalancerPool pool, PdDContext pddContext) {
		this.pddContext = pddContext;
		this.type = type;
		this.pool = pool;
	}
	
	public String selectConnector() throws BehaviourException {
		switch (this.type) {
		case ROUND_ROBIN:
			return getRoundRobin();
		case WEIGHT_ROUND_ROBIN:
			return getWeightRoundRobin();
		case RANDOM:
			return getRandom();
		case WEIGHT_RANDOM:
			return getWeightRandom();
		case IP_HASH:
			return getIpSourceHash();
		case LEAST_CONNECTIONS:
			return getLeastConnections();
		}
		
		throw new BehaviourException("Type '"+this.type+"' unknown");
	}

	private String getRoundRobin() throws BehaviourException {
		Set<String> servers = this.pool.getConnectorNames(false); // il passive health check viene effettuato dentro il metodo nextPosition
		List<String> serverList = new ArrayList<>();
		serverList.addAll(servers);
		int position = this.pool.getNextPosition(false);
		String target = serverList.get(position);
		return target;
	}
	private String getWeightRoundRobin() throws BehaviourException {
		List<String> serverList = this.pool.getWeightList(false); // il passive health check viene effettuato dentro il metodo nextPosition
		int position = this.pool.getNextPosition(true);
		String target = serverList.get(position);
		return target;
	}


	private String getRandom() throws BehaviourException {
		Set<String> servers = this.pool.getConnectorNames(true);
		if(servers.isEmpty()) {
			throw new BehaviourException("Nessun connettore selezionabile (passive health check)");
		}
		List<String> serverList = new ArrayList<>();
		serverList.addAll(servers);
		int randomIndex = getRandomInstance().nextInt(serverList.size());
		String target = serverList.get(randomIndex);
		return target;
	}
	private String getWeightRandom() throws BehaviourException {
		List<String> serverList = this.pool.getWeightList(true);
		Integer index = getRandomInstance().nextInt(serverList.size());
		String target = serverList.get(index);
		return target;
	}

	public static String getIpSourceFromContet(PdDContext pddContext) {
		Object oIpAddressRemote = pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_REMOTE_ADDRESS);
		String ipAddressRemote = null;
		if(oIpAddressRemote!=null && (oIpAddressRemote instanceof String)){
			ipAddressRemote = (String)oIpAddressRemote;
		}
		if (ipAddressRemote == null) {
			ipAddressRemote = "127.0.0.1";
		}
		
		Object oIpAddressTransport = pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_TRANSPORT_ADDRESS);
		String ipAddressTransport = null;
		if(oIpAddressTransport!=null && (oIpAddressTransport instanceof String)){
			ipAddressTransport = (String)oIpAddressTransport;
		}
		if (ipAddressTransport == null) {
			ipAddressTransport = "-";
		}
		
		String clientIp = ipAddressRemote+" "+ipAddressTransport;
		
		return clientIp;
	}
	
	private String getIpSourceHash() throws BehaviourException {
		
		String clientIp = getIpSourceFromContet(this.pddContext);
		
		Set<String> servers = this.pool.getConnectorNames(false);
		List<String> serverList = new ArrayList<>();
		serverList.addAll(servers);
		String remoteId = clientIp;
		int hashCodeCalcolato = remoteId.hashCode();
		if(hashCodeCalcolato == Integer.MIN_VALUE) {
			hashCodeCalcolato = Integer.MIN_VALUE+1; // altrimenti viene negativo l'abs
		}
		int absoluteHashCode = java.lang.Math.abs(hashCodeCalcolato);
		Integer index = absoluteHashCode % serverList.size();
		String target = serverList.get(index);
		
		if(this.pool.isPassiveHealthCheck()) {
			
			Set<String> setAfterPassiveHealthCheck = this.pool.getConnectorNames(true);
			
			// prima verifica
			if(setAfterPassiveHealthCheck.contains(target)) {
				return target;
			}
			
			// controllo prossime posizioni fino a tornare a quella attuale
			int nextPos = index.intValue()+1;
			if(nextPos==serverList.size()) {
				nextPos = 0;
			}
			while(nextPos!=index.intValue()) {
				target = serverList.get(nextPos);
				if(setAfterPassiveHealthCheck.contains(target)) {
					return target;
				}
				nextPos++;
				if(nextPos==serverList.size()) {
					nextPos = 0;
				}
			}
			
			throw new BehaviourException("Nessun connettore selezionabile (passive health check)");
			
		}
		
		return target;
	}
	
	private String getLeastConnections() {
		return this.pool.getNextConnectorLeastConnections();
	}

}
