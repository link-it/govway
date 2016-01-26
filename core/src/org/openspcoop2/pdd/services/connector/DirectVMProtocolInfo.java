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

package org.openspcoop2.pdd.services.connector;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * DirectVMProtocolInfo
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DirectVMProtocolInfo {

	private static final String ID = "DirectVM-IDTransazione";
	private static final String ID_MESSAGGIO_RICHIESTA = "DirectVM-IDMessaggioRichiesta";
	private static final String ID_MESSAGGIO_RISPOSTA = "DirectVM-IDMessaggioRisposta";
	
	private String idTransazione;
	private String idMessaggioRichiesta;
	private String idMessaggioRisposta;
	
	public String getIdTransazione() {
		return this.idTransazione;
	}
	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
	public String getIdMessaggioRichiesta() {
		return this.idMessaggioRichiesta;
	}
	public void setIdMessaggioRichiesta(String idMessaggioRichiesta) {
		this.idMessaggioRichiesta = idMessaggioRichiesta;
	}
	public String getIdMessaggioRisposta() {
		return this.idMessaggioRisposta;
	}
	public void setIdMessaggioRisposta(String idMessaggioRisposta) {
		this.idMessaggioRisposta = idMessaggioRisposta;
	}
	
	
	public void setInfo(PdDContext pddContext){
		if(this.idTransazione!=null){
			pddContext.addObject(ID, this.idTransazione);
		}
		if(this.idMessaggioRichiesta!=null){
			pddContext.addObject(ID_MESSAGGIO_RICHIESTA, this.idMessaggioRichiesta);
		}
		if(this.idMessaggioRisposta!=null){
			pddContext.addObject(ID_MESSAGGIO_RISPOSTA, this.idMessaggioRisposta);
		}
	}
	
	public void setInfo(Busta busta){
		if(this.idTransazione!=null){
			busta.addProperty(ID, this.idTransazione);
		}
		if(this.idMessaggioRichiesta!=null){
			busta.addProperty(ID_MESSAGGIO_RICHIESTA, this.idMessaggioRichiesta);
		}
		if(this.idMessaggioRisposta!=null){
			busta.addProperty(ID_MESSAGGIO_RISPOSTA, this.idMessaggioRisposta);
		}
	}
	
	public static void setInfoFromContext(PdDContext pddContext,Busta busta){
		
		Object oIdTransazione = pddContext.getObject(ID);
		if(oIdTransazione!=null){
			busta.addProperty(ID, (String) oIdTransazione);
		}
		
		Object oIdMessaggioRichiesta = pddContext.getObject(ID_MESSAGGIO_RICHIESTA);
		if(oIdMessaggioRichiesta!=null){
			busta.addProperty(ID_MESSAGGIO_RICHIESTA, (String) oIdMessaggioRichiesta);
		}
		
		Object oIdMessaggioRisposta = pddContext.getObject(ID_MESSAGGIO_RISPOSTA);
		if(oIdMessaggioRisposta!=null){
			busta.addProperty(ID_MESSAGGIO_RISPOSTA, (String) oIdMessaggioRisposta);
		}
	}
}
