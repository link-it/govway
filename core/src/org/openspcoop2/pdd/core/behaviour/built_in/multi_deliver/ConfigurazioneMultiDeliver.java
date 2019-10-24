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

package org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver;

import java.util.HashMap;
import java.util.List;

import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;

/**
 * MultiDeliverBehaviour
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneMultiDeliver {

	public static ConfigurazioneMultiDeliver read(PortaApplicativa pa) {
		return new ConfigurazioneMultiDeliver();
	}
	
	
	private String transazioneSincrona_nomeConnettore;
	private List<Integer> transazioneSincrona_esitiPerSpedireNotifiche;
	
	private HashMap<String, GestioneErrore> mapConnettoreToGestioneErrore = new HashMap<>();
	
	
	public String getTransazioneSincrona_nomeConnettore() {
		return this.transazioneSincrona_nomeConnettore;
	}

	public void setTransazioneSincrona_nomeConnettore(String transazioneSincrona_nomeConnettore) {
		this.transazioneSincrona_nomeConnettore = transazioneSincrona_nomeConnettore;
	}

	public List<Integer> getTransazioneSincrona_esitiPerSpedireNotifiche() {
		return this.transazioneSincrona_esitiPerSpedireNotifiche;
	}

	public void setTransazioneSincrona_esitiPerSpedireNotifiche(
			List<Integer> transazioneSincrona_esitiPerSpedireNotifiche) {
		this.transazioneSincrona_esitiPerSpedireNotifiche = transazioneSincrona_esitiPerSpedireNotifiche;
	}

	public HashMap<String, GestioneErrore> getMapConnettoreToGestioneErrore() {
		return this.mapConnettoreToGestioneErrore;
	}

	public void setMapConnettoreToGestioneErrore(HashMap<String, GestioneErrore> mapConnettoreToGestioneErrore) {
		this.mapConnettoreToGestioneErrore = mapConnettoreToGestioneErrore;
	}
}
