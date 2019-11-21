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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.Proprieta;
import org.slf4j.Logger;

/**
 * MultiDeliverUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultiDeliverUtils  {
	
	
	public static ConfigurazioneMultiDeliver read(PortaApplicativa pa, Logger log) throws CoreException {
		ConfigurazioneMultiDeliver config = new ConfigurazioneMultiDeliver();
		if(pa.getBehaviour()==null) {
			throw new CoreException("Configurazione non disponibile");
		}
		
		if(pa.getBehaviour().sizeProprietaList()>0) {
			for (Proprieta p : pa.getBehaviour().getProprietaList()) {
				
				String nome = p.getNome();
				String valore = p.getValore().trim();
				
				try {
					if(Costanti.MULTI_DELIVER_CONNETTORE_API.equals(nome)) {
						config.setTransazioneSincrona_nomeConnettore(valore);
					}
					
					else if(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO.equals(nome)) {
						config.setNotificheByEsito("true".equals(valore));
					}
					
					else if(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_OK.equals(nome)) {
						config.setNotificheByEsito_ok("true".equals(valore));
					}
					else if(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_FAULT.equals(nome)) {
						config.setNotificheByEsito_fault("true".equals(valore));
					}
					else if(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_CONSEGNA.equals(nome)) {
						config.setNotificheByEsito_erroriConsegna("true".equals(valore));
					}
					else if(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_RICHIESTA_SCARTATE.equals(nome)) {
						config.setNotificheByEsito_richiesteScartate("true".equals(valore));
					}
					else if(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_PROCESSAMENTO.equals(nome)) {
						config.setNotificheByEsito_erroriProcessamento("true".equals(valore));
					}
					
				}catch(Exception e) {
					throw new CoreException("Configurazione condizionale non corretta (proprietà:"+p.getNome()+" valore:'"+p.getValore()+"'): "+e.getMessage(),e);
				}
			}
		}	

		return config;
	}
	

	public static void save(PortaApplicativa pa, ConfigurazioneMultiDeliver configurazione) throws CoreException {
		
		if(pa.getBehaviour()==null) {
			throw new CoreException("Configurazione behaviour non abilitata");
		}
		if(configurazione==null) {
			throw new CoreException("Configurazione condizionale non fornita");
		}
		
		if(StringUtils.isNotEmpty(configurazione.getTransazioneSincrona_nomeConnettore())) {
			pa.getBehaviour().addProprieta(newP(Costanti.MULTI_DELIVER_CONNETTORE_API, configurazione.getTransazioneSincrona_nomeConnettore()));
		}
		
		pa.getBehaviour().addProprieta(newP(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO, configurazione.isNotificheByEsito()+""));
				
		pa.getBehaviour().addProprieta(newP(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_OK, configurazione.isNotificheByEsito_ok()+""));
		pa.getBehaviour().addProprieta(newP(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_FAULT, configurazione.isNotificheByEsito_fault()+""));
		pa.getBehaviour().addProprieta(newP(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_CONSEGNA, configurazione.isNotificheByEsito_erroriConsegna()+""));
		pa.getBehaviour().addProprieta(newP(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_RICHIESTA_SCARTATE, configurazione.isNotificheByEsito_richiesteScartate()+""));
		pa.getBehaviour().addProprieta(newP(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_PROCESSAMENTO, configurazione.isNotificheByEsito_erroriProcessamento()+""));
	}
	
	private static Proprieta newP(String nome, String valore) {
		Proprieta p = new Proprieta();
		p.setNome(nome);
		p.setValore(valore);
		return p;
	}
}
