/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.engine;

import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.protocol.basic.BasicEmptyRawContentFactory;
import org.openspcoop2.protocol.basic.BasicStaticInstanceConfig;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.config.IProtocolConfiguration;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.config.IProtocolVersionManager;
import org.slf4j.Logger;

/**
 * Protocol Factory di Base
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class BasicProtocolFactory extends BasicEmptyRawContentFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7718343905236594865L;

	public BasicProtocolFactory(Logger log){
		super.log = log;
	}
	
	@Override
	protected void initStaticInstance(BasicStaticInstanceConfig staticInstanceConfig) throws ProtocolException{
		super.initStaticInstance(staticInstanceConfig);
		if(staticInstanceConfig!=null) {
			if(staticInstanceConfig.isStaticConfig()) {
				if(staticInstanceProtocolManager==null) {
					initStaticInstanceProtocolManager();
				}
				createProtocolManager();
				if(staticInstanceProtocolVersionManager==null) {
					initStaticInstanceProtocolVersionManager();
				}
				createProtocolVersionManager("-");
			}
		}
	}

	private static Map<String, IProtocolManager> staticInstanceProtocolManager = null;
	private static synchronized void initStaticInstanceProtocolManager() {
		if(staticInstanceProtocolManager==null) {
			staticInstanceProtocolManager = new HashMap<String, IProtocolManager>();
		}
	}
	@Override
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		if(staticInstanceProtocolManager!=null) {
			if(!staticInstanceProtocolManager.containsKey(this.getProtocol())) {
				initProtocolManager(this.getProtocol());
			}
			return staticInstanceProtocolManager.get(this.getProtocol());
		}
		else {
			return new BasicProtocolManager(this);
		}
	}
	private synchronized void initProtocolManager(String protocol) throws ProtocolException {
		if(!staticInstanceProtocolManager.containsKey(protocol)) {
			staticInstanceProtocolManager.put(protocol, new BasicProtocolManager(this));
		}
	}
	
	private static Map<String, IProtocolVersionManager> staticInstanceProtocolVersionManager = null;
	private static synchronized void initStaticInstanceProtocolVersionManager() {
		if(staticInstanceProtocolVersionManager==null) {
			staticInstanceProtocolVersionManager = new HashMap<String, IProtocolVersionManager>();
		}
	}
	@Override
	public IProtocolVersionManager createProtocolVersionManager(String version)
			throws ProtocolException {
		if(staticInstanceProtocolVersionManager!=null) {
			if(!staticInstanceProtocolVersionManager.containsKey(this.getProtocol())) {
				initProtocolVersioneManager(this.getProtocol());
			}
			return staticInstanceProtocolVersionManager.get(this.getProtocol());
		}
		else {
			return new BasicProtocolVersionManager(this);
		}
	}
	private synchronized void initProtocolVersioneManager(String protocol) throws ProtocolException {
		if(!staticInstanceProtocolVersionManager.containsKey(protocol)) {
			staticInstanceProtocolVersionManager.put(protocol, new BasicProtocolVersionManager(this));
		}
	}

	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return null;
	}

}
