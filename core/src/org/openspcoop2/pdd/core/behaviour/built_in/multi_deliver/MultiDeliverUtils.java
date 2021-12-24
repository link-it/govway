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

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.BehaviourPropertiesUtils;
import org.slf4j.Logger;

/**
 * MultiDeliverUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultiDeliverUtils  {
	
	
	public static ConfigurazioneMultiDeliver read(PortaApplicativa pa, Logger log) throws BehaviourException {
		ConfigurazioneMultiDeliver config = new ConfigurazioneMultiDeliver();
		if(pa.getBehaviour()==null) {
			throw new BehaviourException("Configurazione non disponibile");
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
					throw new BehaviourException("Configurazione condizionale non corretta (proprietà:"+p.getNome()+" valore:'"+p.getValore()+"'): "+e.getMessage(),e);
				}
			}
		}	

		return config;
	}
	

	public static void save(PortaApplicativa pa, ConfigurazioneMultiDeliver configurazione) throws BehaviourException {
		
		if(pa.getBehaviour()==null) {
			throw new BehaviourException("Configurazione behaviour non abilitata");
		}
		if(configurazione==null) {
			throw new BehaviourException("Configurazione condizionale non fornita");
		}
		
		if(StringUtils.isNotEmpty(configurazione.getTransazioneSincrona_nomeConnettore())) {
			BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_CONNETTORE_API, configurazione.getTransazioneSincrona_nomeConnettore());
		}
		
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO, configurazione.isNotificheByEsito()+"");
				
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_OK, configurazione.isNotificheByEsito_ok()+"");
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_FAULT, configurazione.isNotificheByEsito_fault()+"");
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_CONSEGNA, configurazione.isNotificheByEsito_erroriConsegna()+"");
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_RICHIESTA_SCARTATE, configurazione.isNotificheByEsito_richiesteScartate()+"");
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_PROCESSAMENTO, configurazione.isNotificheByEsito_erroriProcessamento()+"");
	}
	
	
	
	public static ConfigurazioneGestioneConsegnaNotifiche read(PortaApplicativaServizioApplicativo pasa, Logger log) throws BehaviourException {
		ConfigurazioneGestioneConsegnaNotifiche config = new ConfigurazioneGestioneConsegnaNotifiche();
		if(pasa==null || pasa.getDatiConnettore()==null || pasa.getDatiConnettore().sizeProprietaList()==0) {
	//		throw new CoreException("Configurazione non disponibile");
			return GestioneConsegnaNotificheUtils.getGestioneDefault();
		}
		
		for (Proprieta p : pasa.getDatiConnettore().getProprietaList()) {
			
			String nome = p.getNome();
			String valore = p.getValore().trim();
			
			try {
				if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_CADENZA.equals(nome)) {
					config.setCadenzaRispedizione(Integer.valueOf(valore));
				}
				
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX.equals(nome)) {
					config.setGestioneTrasporto2xx(TipoGestioneNotificaTrasporto.toEnumConstant(valore, true));
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_CODE_LIST.equals(nome)) {
					if(valore.contains(",")) {
						String [] tmp = valore.split(",");
						for (String t : tmp) {
							config.getGestioneTrasporto2xx_codes().add(Integer.valueOf(t.trim()));
						}
					}
					else {
						config.getGestioneTrasporto2xx_codes().add(Integer.valueOf(valore));
					}
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_LEFT_INTERVAL.equals(nome)) {
					config.setGestioneTrasporto2xx_leftInterval(Integer.valueOf(valore));
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_RIGHT_INTERVAL.equals(nome)) {
					config.setGestioneTrasporto2xx_rightInterval(Integer.valueOf(valore));
				}
				
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX.equals(nome)) {
					config.setGestioneTrasporto3xx(TipoGestioneNotificaTrasporto.toEnumConstant(valore, true));
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_CODE_LIST.equals(nome)) {
					if(valore.contains(",")) {
						String [] tmp = valore.split(",");
						for (String t : tmp) {
							config.getGestioneTrasporto3xx_codes().add(Integer.valueOf(t.trim()));
						}
					}
					else {
						config.getGestioneTrasporto3xx_codes().add(Integer.valueOf(valore));
					}
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_LEFT_INTERVAL.equals(nome)) {
					config.setGestioneTrasporto3xx_leftInterval(Integer.valueOf(valore));
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_RIGHT_INTERVAL.equals(nome)) {
					config.setGestioneTrasporto3xx_rightInterval(Integer.valueOf(valore));
				}
				
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX.equals(nome)) {
					config.setGestioneTrasporto4xx(TipoGestioneNotificaTrasporto.toEnumConstant(valore, true));
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_CODE_LIST.equals(nome)) {
					if(valore.contains(",")) {
						String [] tmp = valore.split(",");
						for (String t : tmp) {
							config.getGestioneTrasporto4xx_codes().add(Integer.valueOf(t.trim()));
						}
					}
					else {
						config.getGestioneTrasporto4xx_codes().add(Integer.valueOf(valore));
					}
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_LEFT_INTERVAL.equals(nome)) {
					config.setGestioneTrasporto4xx_leftInterval(Integer.valueOf(valore));
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_RIGHT_INTERVAL.equals(nome)) {
					config.setGestioneTrasporto4xx_rightInterval(Integer.valueOf(valore));
				}
				
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX.equals(nome)) {
					config.setGestioneTrasporto5xx(TipoGestioneNotificaTrasporto.toEnumConstant(valore, true));
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_CODE_LIST.equals(nome)) {
					if(valore.contains(",")) {
						String [] tmp = valore.split(",");
						for (String t : tmp) {
							config.getGestioneTrasporto5xx_codes().add(Integer.valueOf(t.trim()));
						}
					}
					else {
						config.getGestioneTrasporto5xx_codes().add(Integer.valueOf(valore));
					}
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_LEFT_INTERVAL.equals(nome)) {
					config.setGestioneTrasporto5xx_leftInterval(Integer.valueOf(valore));
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_RIGHT_INTERVAL.equals(nome)) {
					config.setGestioneTrasporto5xx_rightInterval(Integer.valueOf(valore));
				}
				
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT.equals(nome)) {
					config.setFault(TipoGestioneNotificaFault.toEnumConstant(valore, true));
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_CODE.equals(nome)) {
					config.setFaultCode(valore);
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_ACTOR.equals(nome)) {
					config.setFaultActor(valore);
				}
				else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_MESSAGE.equals(nome)) {
					config.setFaultMessage(valore);
				}
				
			}catch(Exception e) {
				throw new BehaviourException("Configurazione condizionale non corretta (proprietà:"+p.getNome()+" valore:'"+p.getValore()+"'): "+e.getMessage(),e);
			}
		}	

		return config;
	}
	

	public static void save(PortaApplicativaServizioApplicativo pasa, ConfigurazioneGestioneConsegnaNotifiche configurazione) throws BehaviourException {
		
		if(pasa==null || pasa.getDatiConnettore()==null) {
			throw new BehaviourException("Configurazione behaviour non disponibile");
		}
		if(configurazione==null) {
			throw new BehaviourException("Configurazione condizionale non fornita");
		}
		
		if(configurazione.getCadenzaRispedizione()!=null) {
			BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_CADENZA, configurazione.getCadenzaRispedizione().intValue()+"");
		} else {
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_CADENZA);
		}
		
		if(configurazione.getGestioneTrasporto2xx()!=null) {
			BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX, configurazione.getGestioneTrasporto2xx().getValue());
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_LEFT_INTERVAL);
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_RIGHT_INTERVAL);
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_CODE_LIST);
			switch (configurazione.getGestioneTrasporto2xx()) {
			case CONSEGNA_COMPLETATA:
			case CONSEGNA_FALLITA:
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(configurazione.getGestioneTrasporto2xx_leftInterval()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_LEFT_INTERVAL, configurazione.getGestioneTrasporto2xx_leftInterval().intValue()+"");
				}
				else {
					throw new BehaviourException("[2xx] Left Interval undefined");
				}
				if(configurazione.getGestioneTrasporto2xx_rightInterval()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_RIGHT_INTERVAL, configurazione.getGestioneTrasporto2xx_rightInterval().intValue()+"");
				}
				else {
					throw new BehaviourException("[2xx] Right Interval undefined");
				}
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(configurazione.getGestioneTrasporto2xx_codes()!=null && !configurazione.getGestioneTrasporto2xx_codes().isEmpty()) {
					StringBuilder bf = new StringBuilder();
					for (Integer code : configurazione.getGestioneTrasporto2xx_codes()) {
						if(bf.length()>0) {
							bf.append(",");
						}
						bf.append(code);
					}
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_CODE_LIST, bf.toString());
				}
				else {
					throw new BehaviourException("[2xx] Code undefined");
				}
				break;
			}
		}
		
		if(configurazione.getGestioneTrasporto3xx()!=null) {
			BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX, configurazione.getGestioneTrasporto3xx().getValue());
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_LEFT_INTERVAL);
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_RIGHT_INTERVAL);
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_CODE_LIST);
			
			switch (configurazione.getGestioneTrasporto3xx()) {
			case CONSEGNA_COMPLETATA:
			case CONSEGNA_FALLITA:
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(configurazione.getGestioneTrasporto3xx_leftInterval()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_LEFT_INTERVAL, configurazione.getGestioneTrasporto3xx_leftInterval().intValue()+"");
				}
				else {
					throw new BehaviourException("[3xx] Left Interval undefined");
				}
				if(configurazione.getGestioneTrasporto3xx_rightInterval()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_RIGHT_INTERVAL, configurazione.getGestioneTrasporto3xx_rightInterval().intValue()+"");
				}
				else {
					throw new BehaviourException("[3xx] Right Interval undefined");
				}
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(configurazione.getGestioneTrasporto3xx_codes()!=null && !configurazione.getGestioneTrasporto3xx_codes().isEmpty()) {
					StringBuilder bf = new StringBuilder();
					for (Integer code : configurazione.getGestioneTrasporto3xx_codes()) {
						if(bf.length()>0) {
							bf.append(",");
						}
						bf.append(code);
					}
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_CODE_LIST, bf.toString());
				}
				else {
					throw new BehaviourException("[3xx] Code undefined");
				}
				break;
			}
		}
		
		if(configurazione.getGestioneTrasporto4xx()!=null) {
			BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX, configurazione.getGestioneTrasporto4xx().getValue());
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_LEFT_INTERVAL);
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_RIGHT_INTERVAL);
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_CODE_LIST);
			
			switch (configurazione.getGestioneTrasporto4xx()) {
			case CONSEGNA_COMPLETATA:
			case CONSEGNA_FALLITA:
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(configurazione.getGestioneTrasporto4xx_leftInterval()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_LEFT_INTERVAL, configurazione.getGestioneTrasporto4xx_leftInterval().intValue()+"");
				}
				else {
					throw new BehaviourException("[4xx] Left Interval undefined");
				}
				if(configurazione.getGestioneTrasporto4xx_rightInterval()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_RIGHT_INTERVAL, configurazione.getGestioneTrasporto4xx_rightInterval().intValue()+"");
				}
				else {
					throw new BehaviourException("[4xx] Right Interval undefined");
				}
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(configurazione.getGestioneTrasporto4xx_codes()!=null && !configurazione.getGestioneTrasporto4xx_codes().isEmpty()) {
					StringBuilder bf = new StringBuilder();
					for (Integer code : configurazione.getGestioneTrasporto4xx_codes()) {
						if(bf.length()>0) {
							bf.append(",");
						}
						bf.append(code);
					}
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_CODE_LIST, bf.toString());
				}
				else {
					throw new BehaviourException("[4xx] Code undefined");
				}
				break;
			}
		}
		
		if(configurazione.getGestioneTrasporto5xx()!=null) {
			BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX, configurazione.getGestioneTrasporto5xx().getValue());
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_LEFT_INTERVAL);
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_RIGHT_INTERVAL);
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_CODE_LIST);
			
			switch (configurazione.getGestioneTrasporto5xx()) {
			case CONSEGNA_COMPLETATA:
			case CONSEGNA_FALLITA:
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(configurazione.getGestioneTrasporto5xx_leftInterval()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_LEFT_INTERVAL, configurazione.getGestioneTrasporto5xx_leftInterval().intValue()+"");
				}
				else {
					throw new BehaviourException("[5xx] Left Interval undefined");
				}
				if(configurazione.getGestioneTrasporto5xx_rightInterval()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_RIGHT_INTERVAL, configurazione.getGestioneTrasporto5xx_rightInterval().intValue()+"");
				}
				else {
					throw new BehaviourException("[5xx] Right Interval undefined");
				}
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(configurazione.getGestioneTrasporto5xx_codes()!=null && !configurazione.getGestioneTrasporto5xx_codes().isEmpty()) {
					StringBuilder bf = new StringBuilder();
					for (Integer code : configurazione.getGestioneTrasporto5xx_codes()) {
						if(bf.length()>0) {
							bf.append(",");
						}
						bf.append(code);
					}
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_CODE_LIST, bf.toString());
				}
				else {
					throw new BehaviourException("[5xx] Code undefined");
				}
				break;
			}
		}
		
		if(configurazione.getFault()!=null) {
			BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT, configurazione.getFault().getValue());
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_CODE);
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_ACTOR);
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_MESSAGE);
			
			switch (configurazione.getFault()) {
			case CONSEGNA_COMPLETATA:
			case CONSEGNA_FALLITA:
				break;
			case CUSTOM:
				if(configurazione.getFaultCode()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_CODE, configurazione.getFaultCode());
				}
				if(configurazione.getFaultActor()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_ACTOR, configurazione.getFaultActor());
				}
				if(configurazione.getFaultMessage()!=null) {
					BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_MESSAGE, configurazione.getFaultMessage());
				}
				break;
			}
		}
	}
	

}
