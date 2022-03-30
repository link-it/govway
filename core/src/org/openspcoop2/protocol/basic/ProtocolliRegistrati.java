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

import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.resources.MapReader;

/**	
 * ProtocolliRegistrati
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolliRegistrati {

	public ProtocolliRegistrati(){
		
	}
	public ProtocolliRegistrati(MapReader<String, IProtocolFactory<?>> protocolli){
		this.protocolli = protocolli;
	}	
	
	private MapReader<String, IProtocolFactory<?>> protocolli;
	
	public MapReader<String, IProtocolFactory<?>> getProtocolli() {
		return this.protocolli;
	}

	public void setProtocolli(MapReader<String, IProtocolFactory<?>> protocolli) {
		this.protocolli = protocolli;
	}
	
	public IProtocolFactory<?> getProtocolFactory(String protocollo) throws ProtocolException{
		if(this.protocolli.containsKey(protocollo)==false){
			throw new ProtocolException("Protocollo ["+protocollo+"] non registrato");
		}
		IProtocolFactory<?> pf = this.protocolli.get(protocollo);
		return pf;
	}
	
}
