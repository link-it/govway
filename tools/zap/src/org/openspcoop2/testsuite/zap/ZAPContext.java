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
package org.openspcoop2.testsuite.zap;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.utils.random.RandomUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.zaproxy.clientapi.core.ApiResponse;
import org.zaproxy.clientapi.core.ApiResponseElement;
import org.zaproxy.clientapi.core.ApiResponseSet;
import org.zaproxy.clientapi.core.ClientApi;


/**
 * ZAPContext
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ZAPContext {

	public static String prefix = " zap-address zap-port zap-session zap-api-key zap-debug";
	public static int startArgs = 5;
		
	public ZAPContext(String[] args, String mainClass, String usage) throws Exception {
		String usageMsg = "\nUsage: "+mainClass+prefix+usage;
		if(args==null || args.length<=0) {
			throw new Exception("ERROR: arguments undefined"+usageMsg);
		}
		
		this.address = args[0];
		if(this.address==null || StringUtils.isEmpty(this.address)) {
			throw new Exception("ERROR: argument 'zap-address' undefined"+usageMsg);
		}
		
		String sPort = null;
		if(args.length>1) {
			sPort = args[1];
		}
		if(sPort==null || StringUtils.isEmpty(sPort)) {
			throw new Exception("ERROR: argument 'zap-port' undefined"+usageMsg);
		}
		try {
			this.port = Integer.valueOf(sPort);
		}catch(Throwable t) {
			throw new Exception("ERROR: argument 'zap-port' uncorrect ("+sPort+")"+usageMsg);
		}
		
		if(args.length>2) {
			this.session = args[2];
		}
		if(this.session==null || StringUtils.isEmpty(this.session)) {
			throw new Exception("ERROR: argument 'zap-session' undefined"+usageMsg);
		}
		File fSession = new File(this.session);
		if(fSession.getParentFile()!=null) {
			File parent = fSession.getParentFile();
			if(parent.exists()) {
				if(!parent.isDirectory()) {
					throw new Exception("ERROR: argument 'zap-session' '"+parent+"' isn't a directory"+usageMsg);
				}
				if(!parent.canWrite()) {
					throw new Exception("ERROR: argument 'zap-session' '"+parent+"' cannot write"+usageMsg);
				}
				if(!parent.canRead()) {
					throw new Exception("ERROR: argument 'zap-session' '"+parent+"' cannot read"+usageMsg);
				}
			}
			else {
				FileSystemUtilities.mkdir(parent);
			}
		}
		
		if(args.length>3) {
			this.apiKey = args[3];
		}
		if(this.apiKey==null || StringUtils.isEmpty(this.apiKey)) {
			throw new Exception("ERROR: argument 'zap-api-key' undefined"+usageMsg);
		}
		
		String sDebug = null;
		if(args.length>4) {
			sDebug = args[4];
		}
		if(sDebug==null || StringUtils.isEmpty(sDebug)) {
			throw new Exception("ERROR: argument 'zap-debug' undefined"+usageMsg);
		}
		try {
			this.debug = Boolean.valueOf(sDebug);
		}catch(Throwable t) {
			throw new Exception("ERROR: argument 'zap-debug' uncorrect ("+sDebug+")"+usageMsg);
		}
		
		this.clientApi = new ClientApi(this.address, this.port, this.apiKey, this.debug);
		
		this.contextName = this.apiKey+RandomUtilities.getRandom().nextInt();
		this.clientApi.context.newContext(this.contextName);
		ApiResponse response = this.clientApi.context.context(this.contextName);
		ApiResponseSet responseSet = (ApiResponseSet) response;
		this.contextId = ((ApiResponseElement) responseSet.getValue("id")).getValue();
		if(this.debug) {
			System.out.println("ContextName: "+this.contextName);
			System.out.println("ContextId: "+this.contextId);
		}
	}
	
	private String address;
	private int port;
	private String session;
	private String apiKey;
	private boolean debug;
	
	private ClientApi clientApi;
	private String contextName;
	private String contextId;

	public String getAddress() {
		return this.address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getPort() {
		return this.port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getSession() {
		return this.session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public String getApiKey() {
		return this.apiKey;
	}
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	public boolean isDebug() {
		return this.debug;
	}
	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public ClientApi getClientApi() {
		return this.clientApi;
	}
	public String getContextName() {
		return this.contextName;
	}
	public String getContextId() {
		return this.contextId;
	}
}
