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

package org.openspcoop2.protocol.sdi.config;

import org.openspcoop2.protocol.basic.config.BasicTraduttore;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.ErroreCooperazione;

/**
 * Classe che implementa, in base al protocollo SdI, l'interfaccia {@link org.openspcoop2.protocol.sdk.config.ITraduttore} 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDITraduttore extends BasicTraduttore {


	public SDITraduttore(IProtocolFactory protocolFactory){
		super(protocolFactory);
	}

	@Override
	public String toString(ErroreCooperazione msg){

		try{

			if(msg.getDescrizioneRawValue()!=null){
				return msg.getDescrizioneRawValue();
			}
			if(msg.getCodiceErrore()!=null){
				return msg.getCodiceErrore().name();
			}
			throw new Exception("Non è stato fornito ne una descrizione ne un codice dell'errore di cooperazione");

		}catch(Exception e){
			this.log.error("Errore durante la trasformazione del messaggio di cooperazione: "+e.getMessage(),e);
			return  msg.getDescrizioneRawValue();
		}
	}
}
