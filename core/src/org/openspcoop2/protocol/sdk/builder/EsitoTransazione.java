/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.protocol.sdk.builder;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;

/**
* EsitoTransazione
*
* @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
*/
public class EsitoTransazione {

	public static EsitoTransazione ESITO_TRANSAZIONE_ERROR = null;
	static{
		ESITO_TRANSAZIONE_ERROR = new EsitoTransazione();
		ESITO_TRANSAZIONE_ERROR.code = 3;
		ESITO_TRANSAZIONE_ERROR.contextType = CostantiProtocollo.ESITO_TRANSACTION_CONTEXT_STANDARD;
		ESITO_TRANSAZIONE_ERROR.name=EsitoTransazioneName.ERRORE_GENERICO;
	}
	
	private EsitoTransazioneName name;
	private Integer code;
	private String contextType;
		
	private EsitoTransazione(){}
	public EsitoTransazione(EsitoTransazioneName name,Integer code,String contextType) throws ProtocolException{
		if(name==null){
			throw new ProtocolException("Parameter name is null");
		}
		if(code==null){
			throw new ProtocolException("Parameter code is null");
		}
		if(contextType==null){
			throw new ProtocolException("Parameter contextType is null");
		}
		this.name = name;
		this.code = code;
		this.contextType = contextType;
	}
	
	public EsitoTransazioneName getName() {
		return this.name;
	}
	public void setName(EsitoTransazioneName name) {
		this.name = name;
	}
	public Integer getCode() {
		return this.code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getContextType() {
		return this.contextType;
	}
	public void setContextType(String contextType) {
		this.contextType = contextType;
	}
	
}
