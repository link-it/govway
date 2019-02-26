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

package org.openspcoop2.protocol.engine;

import org.openspcoop2.protocol.basic.BasicEmptyRawContentFactory;
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
	public IProtocolManager createProtocolManager()
			throws ProtocolException {
		return new BasicProtocolManager(this);
	}
	
	@Override
	public IProtocolVersionManager createProtocolVersionManager(String version)
			throws ProtocolException {
		return new BasicProtocolVersionManager(this);
	}

	@Override
	public IProtocolConfiguration createProtocolConfiguration()
			throws ProtocolException {
		return null;
	}

}
