/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.services;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;

/**
 * DirectVMProtocolInfo
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DirectVMProtocolInfo {

	public static final MapKey<String> ID = Map.newMapKey("DirectVM-IDTransazione");
	public static final MapKey<String> ID_MESSAGGIO_RICHIESTA = Map.newMapKey("DirectVM-IDMessaggioRichiesta");
	public static final MapKey<String> ID_MESSAGGIO_RISPOSTA = Map.newMapKey("DirectVM-IDMessaggioRisposta");
	
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
			busta.addProperty(ID.getValue(), this.idTransazione);
		}
		if(this.idMessaggioRichiesta!=null){
			busta.addProperty(ID_MESSAGGIO_RICHIESTA.getValue(), this.idMessaggioRichiesta);
		}
		if(this.idMessaggioRisposta!=null){
			busta.addProperty(ID_MESSAGGIO_RISPOSTA.getValue(), this.idMessaggioRisposta);
		}
	}
	
	public static void setInfoFromContext(PdDContext pddContext,Busta busta){
		
		Object oIdTransazione = pddContext.getObject(ID);
		if(oIdTransazione!=null){
			busta.addProperty(ID.getValue(), (String) oIdTransazione);
		}
		
		Object oIdMessaggioRichiesta = pddContext.getObject(ID_MESSAGGIO_RICHIESTA);
		if(oIdMessaggioRichiesta!=null){
			busta.addProperty(ID_MESSAGGIO_RICHIESTA.getValue(), (String) oIdMessaggioRichiesta);
		}
		
		Object oIdMessaggioRisposta = pddContext.getObject(ID_MESSAGGIO_RISPOSTA);
		if(oIdMessaggioRisposta!=null){
			busta.addProperty(ID_MESSAGGIO_RISPOSTA.getValue(), (String) oIdMessaggioRisposta);
		}
	}
}
