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
package org.openspcoop2.utils.logger.beans.context.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.utils.logger.beans.context.core.AbstractTransaction;
import org.openspcoop2.utils.logger.beans.context.core.BaseServer;

/**
 * BatchTransaction
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BatchTransaction extends AbstractTransaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<BaseServer> servers = new ArrayList<>();

	public List<BaseServer> getServers() {
		return this.servers;
	}
	
	public Map<String, BaseServer> getServersMap() {
		Map<String, BaseServer> map = new HashMap<>();
		int index = 1;
		for (BaseServer baseServer : this.servers) {
			if(baseServer.getName()!=null && !map.containsKey(baseServer.getName())) {
				map.put(baseServer.getName(), baseServer);
			}
			else {
				map.put("_server-"+index, baseServer);
			}
			index++;
		}
		return map;
	}
	
	public void addServer(BaseServer server) {
		this.servers.add(server);
	}
	
	public int sizeServers() {
		return this.servers.size();
	}
	
	public BaseServer getServer(int index) {
		return this.servers.get(index);
	}
	
	public BaseServer getServer(String name) {
		for (BaseServer baseServer : this.servers) {
			if(name.equals(baseServer.getName())) {
				return baseServer;
			}
		}
		return null;
	}
	
	public BaseServer getLastServer() {
		return this.servers.get(this.servers.size()-1);
	}
	
	public BaseServer getFirstServer() {
		return this.servers.get(0);
	}
	
}
