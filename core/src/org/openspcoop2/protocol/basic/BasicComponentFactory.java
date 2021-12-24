/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.basic;

import org.openspcoop2.protocol.registry.CachedRegistryReader;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.slf4j.Logger;

/**
 * BasicComponentFactory
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BasicComponentFactory implements IComponentFactory {

	protected Logger log;
	protected IProtocolFactory<?> protocolFactory;
	protected IRegistryReader cachedRegistryReader;
	
	public BasicComponentFactory(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		try{
			this.protocolFactory = protocolFactory;
			this.log = this.protocolFactory.getLogger();
			this.cachedRegistryReader = new CachedRegistryReader(this.log, protocolFactory);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public Logger getLog() {
		return this.log;
	}
	
	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}
	
}
