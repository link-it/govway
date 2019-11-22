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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspcoop2.core.commons.CoreException;

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
	
	public static int DEFAULT_WEIGHT = 1; 
	
	@Override
	public String toString() {
		synchronized (this.semaphore) {
			StringBuffer bf = new StringBuffer();
			bf.append("Connectors: ").append(this.connectorMap.size());
			bf.append("\nTotal Weight: ").append(this.totalWeight);
			bf.append("\nPosition: ").append(this.position);
			for (String name : this.connectorMap.keySet()) {
				bf.append("\n");
				bf.append("- ").append(name).append(" : ").append(" ( weight:").append(this.connectorMap.get(name));
				if(this.connectorMap_activeConnections.containsKey(name)) {
					bf.append(" activeConnections:").append(this.connectorMap_activeConnections.get(name));
				}
				bf.append(" )");
			}
			return bf.toString();
		}
	}
	
	
	protected Boolean semaphore = true;
	protected Map<String, Integer> connectorMap = new HashMap<>();
	protected Map<String, Integer> connectorMap_activeConnections = new HashMap<>();
	private int totalWeight = 0;
	
	private int position = -1;
	
	public int getNextPosition(boolean checkByWeight) {
		synchronized (this.semaphore) {
			this.position++;
			if(checkByWeight) {
				if(this.position==this.totalWeight) {
					this.position = 0;
				}
			}
			else {
				if(this.position==this.connectorMap.size()) {
					this.position = 0;
				}
			}
			return this.position;
		}
	}
	

	public String getNextConnectorLeastConnections() {
		synchronized (this.semaphore) {
			List<String> listMin = new ArrayList<String>();
			int min = 0;
			if(!this.connectorMap_activeConnections.isEmpty()) {
				min = Integer.MAX_VALUE;
				for (String name : this.connectorMap.keySet()) {
					if(this.connectorMap_activeConnections.containsKey(name)==false) {
						if(min != 0) {
							min = 0;
							listMin.clear();
						}
						listMin.add(name);
					}
					else {
						int active = this.connectorMap_activeConnections.get(name);
						if(active<min) {
							min = active;
							listMin.clear();
							listMin.add(name);
						}
						else if(active==min) {
							listMin.add(name);
						}
					}
				}
			}

			if(listMin.isEmpty()) {
				listMin.addAll(this.connectorMap.keySet());
				//System.out.println("LISTA is EMPTY");
				
			}
			
			//System.out.println("LISTA min["+min+"]: "+listMin);
			
			return listMin.get(0);
			
		}
	}
	
	public boolean isEmpty() {
		return this.connectorMap.isEmpty();
	}
	
	public Set<String> getConnectorNames() {
		return this.connectorMap.keySet();
	}
	
	public int getWeight(String name) {
		return this.connectorMap.get(name);
	}
	
	public void addConnector(String name) throws CoreException {
		this.addConnector(name, DEFAULT_WEIGHT);
	}
	public void addConnector(String name, int weight) throws CoreException {
		synchronized (this.semaphore) {
			if(this.connectorMap.containsKey(name)) {
				throw new CoreException("Already exists connector '"+name+"'");
			}
			this.connectorMap.put(name, weight);
			this.totalWeight = this.totalWeight+weight;
		}
		
	}
	

	public void addActiveConnection(String name) throws Exception {
		synchronized (this.semaphore) {
			int activeConnections = 0;
			if(this.connectorMap_activeConnections.containsKey(name)) {
				activeConnections = this.connectorMap_activeConnections.remove(name);
			}
			activeConnections++;
			this.connectorMap_activeConnections.put(name, activeConnections);
			//System.out.println("ADD ["+name+"] ["+activeConnections+"]");
		}
	}
	public void removeActiveConnection(String name) throws Exception {
		synchronized (this.semaphore) {
			int activeConnections = 0;
			if(this.connectorMap_activeConnections.containsKey(name)) {
				activeConnections = this.connectorMap_activeConnections.remove(name);
			}
			activeConnections--;
			if(activeConnections>0) {
				this.connectorMap_activeConnections.put(name, activeConnections);
			}
			//System.out.println("REMOVE ["+name+"] ["+activeConnections+"]");
		}
	}
}
