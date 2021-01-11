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

package org.openspcoop2.utils.service.context.server;


import org.apache.cxf.interceptor.Fault;
import org.openspcoop2.utils.logger.beans.context.core.Role;
import org.openspcoop2.utils.service.context.dump.DumpConfig;
import org.openspcoop2.utils.service.context.dump.DumpRequest;
import org.openspcoop2.utils.service.context.dump.DumpResponse;
import org.openspcoop2.utils.service.context.dump.DumpUtilities;

/**
 * ServerInfoContextManuallyAdd
 *
 * @author Lorenzo Nardi (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ServerInfoContextManuallyAdd {
	
	private ServerConfig serverConfig;
	private ServerInfoUtilities utilities;
	private DumpUtilities dumpUtilities;
	private DumpConfig dumpConfig;

	public ServerInfoContextManuallyAdd(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		this.utilities = new ServerInfoUtilities(this.serverConfig);
		
		this.dumpConfig = this.serverConfig.getDumpConfig();
		if(this.dumpConfig==null) {
			this.dumpConfig = new DumpConfig();
		}
		this.dumpConfig.setRole(Role.CLIENT);
		
		if(this.serverConfig.isDump() && this.serverConfig.getDumpConfig() == null) {
			this.serverConfig.setDumpConfig(this.dumpConfig);
		}
		
		this.dumpUtilities = new DumpUtilities(this.serverConfig);
	}

	public void processBeforeSend(ServerInfoRequest request) throws Fault {
		this.processBeforeSend(request, null);
	}
	public void processBeforeSend(ServerInfoRequest request, DumpRequest dumpRequest) throws Fault {
		
		this.utilities.processBeforeSend(request);
		if(this.serverConfig.isDump() && dumpRequest!=null) {
			this.dumpUtilities.processBeforeSend(dumpRequest);
		}
		
	}
	
	public void processAfterSend(ServerInfoResponse response) throws Fault {
		this.processAfterSend(response, null);
	}
	public void processAfterSend(ServerInfoResponse response, DumpResponse DumpResponse) throws Fault {
		
		this.utilities.processAfterSend(response);
		if(this.serverConfig.isDump() && DumpResponse!=null) {
			this.dumpUtilities.processAfterSend(DumpResponse);
		}
		
	}
}
