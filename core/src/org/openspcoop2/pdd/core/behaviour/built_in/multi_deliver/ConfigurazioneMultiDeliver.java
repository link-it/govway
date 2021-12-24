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

package org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.utils.EsitiProperties;

/**
 * MultiDeliverBehaviour
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneMultiDeliver {

	private String transazioneSincrona_nomeConnettore;
	
	private boolean notificheByEsito = true;
	private boolean notificheByEsito_ok = true;
	private boolean notificheByEsito_fault = true;
	private boolean notificheByEsito_erroriConsegna = false;
	private boolean notificheByEsito_erroriProcessamento = false;
	private boolean notificheByEsito_richiesteScartate = false;
	private List<Integer> initTransazioneSincrona_esitiPerSpedireNotifiche;
	
	public List<Integer> getTransazioneSincrona_esitiPerSpedireNotifiche(EsitiProperties esitiProperties) throws ProtocolException {
		if(this.initTransazioneSincrona_esitiPerSpedireNotifiche==null) {
			initTransazioneSincrona_esitiPerSpedireNotifiche(esitiProperties);
		}
		return this.initTransazioneSincrona_esitiPerSpedireNotifiche;
	}
	private synchronized List<Integer> initTransazioneSincrona_esitiPerSpedireNotifiche(EsitiProperties esitiProperties) throws ProtocolException {
		if(this.initTransazioneSincrona_esitiPerSpedireNotifiche==null) {
			this.initTransazioneSincrona_esitiPerSpedireNotifiche = new ArrayList<Integer>();
			if(!this.notificheByEsito) {
				this.initTransazioneSincrona_esitiPerSpedireNotifiche = esitiProperties.getEsitiCode();
			}
			else {
				if(this.notificheByEsito_ok) {
					this.initTransazioneSincrona_esitiPerSpedireNotifiche.addAll(esitiProperties.getEsitiCodeOk_senzaFaultApplicativo());
				}
				if(this.notificheByEsito_fault) {
					this.initTransazioneSincrona_esitiPerSpedireNotifiche.addAll(esitiProperties.getEsitiCodeFaultApplicativo());
				}
				if(this.notificheByEsito_erroriConsegna) {
					this.initTransazioneSincrona_esitiPerSpedireNotifiche.addAll(esitiProperties.getEsitiCodeErroriConsegna());
				}
				if(this.notificheByEsito_richiesteScartate) {
					this.initTransazioneSincrona_esitiPerSpedireNotifiche.addAll(esitiProperties.getEsitiCodeRichiestaScartate());
				}
				if(this.notificheByEsito_erroriProcessamento) {
					List<Integer> esitiFallite = esitiProperties.getEsitiCodeKo();
					List<Integer> esitiConsegna = esitiProperties.getEsitiCodeErroriConsegna();
					List<Integer> richiesteScartate = esitiProperties.getEsitiCodeRichiestaScartate();
					for (Integer e : esitiFallite) {
						boolean foundInConsegna = false;
						for (Integer eConsegna : esitiConsegna) {
							if(eConsegna.intValue() == e.intValue()) {
								foundInConsegna = true;
								break;
							}
						}
						if(foundInConsegna) {
							continue;
						}
						boolean foundInRichiesteScartate = false;
						for (Integer eRichiestaScartata : richiesteScartate) {
							if(eRichiestaScartata.intValue() == e.intValue()) {
								foundInRichiesteScartate = true;
								break;
							}
						}
						if(foundInRichiesteScartate) {
							continue;
						}
						this.initTransazioneSincrona_esitiPerSpedireNotifiche.add(e);
					}
				}
			}
		}
		return this.initTransazioneSincrona_esitiPerSpedireNotifiche;
	}

	public String getTransazioneSincrona_nomeConnettore() {
		return this.transazioneSincrona_nomeConnettore;
	}

	public void setTransazioneSincrona_nomeConnettore(String transazioneSincrona_nomeConnettore) {
		this.transazioneSincrona_nomeConnettore = transazioneSincrona_nomeConnettore;
	}
		
	public boolean isNotificheByEsito() {
		return this.notificheByEsito;
	}

	public void setNotificheByEsito(boolean notificheByEsito) {
		this.notificheByEsito = notificheByEsito;
	}

	public boolean isNotificheByEsito_ok() {
		return this.notificheByEsito_ok;
	}

	public void setNotificheByEsito_ok(boolean notificheByEsito_ok) {
		this.notificheByEsito_ok = notificheByEsito_ok;
	}

	public boolean isNotificheByEsito_fault() {
		return this.notificheByEsito_fault;
	}

	public void setNotificheByEsito_fault(boolean notificheByEsito_fault) {
		this.notificheByEsito_fault = notificheByEsito_fault;
	}

	public boolean isNotificheByEsito_erroriConsegna() {
		return this.notificheByEsito_erroriConsegna;
	}

	public void setNotificheByEsito_erroriConsegna(boolean notificheByEsito_erroriConsegna) {
		this.notificheByEsito_erroriConsegna = notificheByEsito_erroriConsegna;
	}

	public boolean isNotificheByEsito_erroriProcessamento() {
		return this.notificheByEsito_erroriProcessamento;
	}

	public void setNotificheByEsito_erroriProcessamento(boolean notificheByEsito_erroriProcessamento) {
		this.notificheByEsito_erroriProcessamento = notificheByEsito_erroriProcessamento;
	}

	public boolean isNotificheByEsito_richiesteScartate() {
		return this.notificheByEsito_richiesteScartate;
	}

	public void setNotificheByEsito_richiesteScartate(boolean notificheByEsito_richiesteScartate) {
		this.notificheByEsito_richiesteScartate = notificheByEsito_richiesteScartate;
	}
}
