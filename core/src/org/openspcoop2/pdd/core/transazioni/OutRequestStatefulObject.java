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
package org.openspcoop2.pdd.core.transazioni;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**     
 * OutRequestStatefulObject
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OutRequestStatefulObject {

	private Date dataUscitaRichiesta;
	private String scenarioCooperazione;
	private String tipoConnettore;
	private String location;
	private List<String> serviziApplicativiErogatore = new ArrayList<String>();
	
	private List<String> eventiGestione = new ArrayList<String>();
	
	public List<String> getServiziApplicativiErogatore() {
		return this.serviziApplicativiErogatore;
	}
	public void addServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		this.serviziApplicativiErogatore.add(servizioApplicativoErogatore);
	}
	public Date getDataUscitaRichiesta() {
		return this.dataUscitaRichiesta;
	}
	public void setDataUscitaRichiesta(Date dataUscitaRichiesta) {
		this.dataUscitaRichiesta = dataUscitaRichiesta;
	}
	public String getScenarioCooperazione() {
		return this.scenarioCooperazione;
	}
	public void setScenarioCooperazione(String scenarioCooperazione) {
		this.scenarioCooperazione = scenarioCooperazione;
	}
	public String getTipoConnettore() {
		return this.tipoConnettore;
	}
	public void setTipoConnettore(String tipoConnettore) {
		this.tipoConnettore = tipoConnettore;
	}
	public String getLocation() {
		return this.location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public List<String> getEventiGestione() {
		return this.eventiGestione;
	}
	public void addEventoGestione(String evento) {
		if(evento.contains(",")){
			throw new RuntimeException("EventoGestione ["+evento+"] non deve contenere il carattere ','");
		}
		this.eventiGestione.add(evento);
	}
	
}
