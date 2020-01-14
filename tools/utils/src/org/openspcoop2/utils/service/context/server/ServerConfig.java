/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.utils.service.context.server;

import org.openspcoop2.utils.service.context.dump.DumpConfig;

/**
 * ServerConfig
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerConfig {

	private String serverId;
	private String operationId;
	private boolean dump;
	private DumpConfig dumpConfig;

	public String getServerId() {
		return this.serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}
	public String getOperationId() {
		return this.operationId;
	}
	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}
	
	public boolean isDump() {
		return this.dump;
	}
	public void setDump(boolean dump) {
		this.dump = dump;
	}
	public DumpConfig getDumpConfig() {
		return this.dumpConfig;
	}
	public void setDumpConfig(DumpConfig dumpConfig) {
		this.dumpConfig = dumpConfig;
	}
}
