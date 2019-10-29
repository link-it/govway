/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.pdd.core.PdDContext;

/**
 * LoadBalancer
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LoadBalancer {

	private PdDContext pddContext;
	private LoadBalancerPool pool;
	private LoadBalancerType type;
	
	public LoadBalancer(LoadBalancerType type, LoadBalancerPool pool, PdDContext pddContext) {
		this.pddContext = pddContext;
		this.type = type;
		this.pool = pool;
	}
	
	public String selectConnector() throws CoreException {
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
		
		throw new CoreException("Type '"+this.type+"' unknown");
	}

	private String getRoundRobin() {
		Set<String> servers = this.pool.getConnectorNames();
		List<String> serverList = new ArrayList<>();
		serverList.addAll(servers);
		String target = serverList.get(this.pool.getNextPosition(false));
		return target;
	}
	private String getWeightRoundRobin() {
		List<String> serverList = getWeightList();
		String target = serverList.get(this.pool.getNextPosition(true));
		return target;
	}


	private String getRandom() {
		Set<String> servers = this.pool.getConnectorNames();
		List<String> serverList = new ArrayList<>();
		serverList.addAll(servers);
		int randomIndex = new Random().nextInt(serverList.size());
		String target = serverList.get(randomIndex);
		return target;
	}
	private String getWeightRandom() {
		List<String> serverList = getWeightList();
		Integer index = new Random().nextInt(serverList.size());
		String target = serverList.get(index);
		return target;
	}

	private String getIpSourceHash() {
		
		Object oIpAddressRemote = this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_REMOTE_ADDRESS);
		String ipAddressRemote = null;
		if(oIpAddressRemote!=null && (oIpAddressRemote instanceof String)){
			ipAddressRemote = (String)oIpAddressRemote;
		}
		if (ipAddressRemote == null) {
			ipAddressRemote = "127.0.0.1";
		}
		
		Object oIpAddressTransport = this.pddContext.getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_TRANSPORT_ADDRESS);
		String ipAddressTransport = null;
		if(oIpAddressTransport!=null && (oIpAddressTransport instanceof String)){
			ipAddressTransport = (String)oIpAddressTransport;
		}
		if (ipAddressTransport == null) {
			ipAddressTransport = "-";
		}
		
		String clientIp = ipAddressRemote+" "+ipAddressTransport;
		
		Set<String> servers = this.pool.getConnectorNames();
		List<String> serverList = new ArrayList<>();
		serverList.addAll(servers);
		String remoteId = clientIp;
		Integer index = remoteId.hashCode() % serverList.size();
		String target = serverList.get(index);
		return target;
	}
	
	private String getLeastConnections() {
		return this.pool.getNextConnectorLeastConnections();
	}

	private List<String> getWeightList() {
		Set<String> servers = this.pool.getConnectorNames();
		List<String> serverList = new ArrayList<>();    

		Iterator<String> iterator = servers.iterator();
		while (iterator.hasNext()) {
			String server = iterator.next();
			Integer weight = this.pool.getWeight(server);
			if (weight == null || weight <= 0) {
				weight = LoadBalancerPool.DEFAULT_WEIGHT;
			}
			for (int i = 0; i < weight; i++) {
				serverList.add(server);
			}
		}

		//System.out.println("LISTA: "+serverList);
		
		return serverList;
	}
}
