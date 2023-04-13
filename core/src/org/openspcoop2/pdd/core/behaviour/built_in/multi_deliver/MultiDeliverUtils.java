/*
 * GovWay - A customizable API Gateway
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativoConnettore;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;
import org.openspcoop2.pdd.core.behaviour.BehaviourPropertiesUtils;
import org.openspcoop2.utils.BooleanNullable;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;

/**
 * MultiDeliverUtils
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MultiDeliverUtils  {
	
	private MultiDeliverUtils() {}
	
	
	public static ConfigurazioneMultiDeliver read(PortaApplicativa pa) throws BehaviourException {
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
					/** le richieste scartate non arrivano alla gestione della consegna in smistatore e quindi non potranno nemmeno essere notifiate
					//else if(Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_RICHIESTA_SCARTATE.equals(nome)) {
					//	config.setNotificheByEsito_richiesteScartate("true".equals(valore));
					//}*/
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
	

	public static void save(PortaApplicativa pa, ConfigurazioneMultiDeliver configurazione, boolean differenziazioneConsegnaDaNotifiche) throws BehaviourException {
		
		if(pa==null) {
			throw new BehaviourException("Param pa is null");
		}
		if(pa.getBehaviour()==null) {
			throw new BehaviourException("Configurazione behaviour non abilitata");
		}
		if(configurazione==null) {
			throw new BehaviourException("Configurazione condizionale non fornita");
		}
		
		if(StringUtils.isNotEmpty(configurazione.getTransazioneSincrona_nomeConnettore())) {
			BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_CONNETTORE_API, configurazione.getTransazioneSincrona_nomeConnettore());
			if(differenziazioneConsegnaDaNotifiche &&
				pa.sizeServizioApplicativoList()>0) {
				saveNormalizeConfig(pa, configurazione);
			}
		}
		
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO, configurazione.isNotificheByEsito()+"");
				
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_OK, configurazione.isNotificheByEsito_ok()+"");
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_FAULT, configurazione.isNotificheByEsito_fault()+"");
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_CONSEGNA, configurazione.isNotificheByEsito_erroriConsegna()+"");
		/** le richieste scartate non arrivano alla gestione della consegna in smistatore e quindi non potranno nemmeno essere notifiate
		//BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_RICHIESTA_SCARTATE, configurazione.isNotificheByEsito_richiesteScartate()+""); */
		BehaviourPropertiesUtils.addProprieta(pa.getBehaviour(),Costanti.MULTI_DELIVER_NOTIFICHE_BY_ESITO_ERRORI_PROCESSAMENTO, configurazione.isNotificheByEsito_erroriProcessamento()+"");
	}
	private static void saveNormalizeConfig(PortaApplicativa pa, ConfigurazioneMultiDeliver configurazione) {
		for (PortaApplicativaServizioApplicativo paSA : pa.getServizioApplicativoList()) {
			if(paSA.getDatiConnettore()==null) {
				paSA.setDatiConnettore(new PortaApplicativaServizioApplicativoConnettore());
			}
			if(paSA.getDatiConnettore().getNome()==null) {
				paSA.getDatiConnettore().setNome(CostantiConfigurazione.NOME_CONNETTORE_DEFAULT);
			}
			paSA.getDatiConnettore().setNotifica(!(configurazione.getTransazioneSincrona_nomeConnettore().equals(paSA.getDatiConnettore().getNome())));
		}
	}
	
	
	public static ConfigurazioneGestioneConsegnaNotifiche read(PortaApplicativaServizioApplicativo pasa) throws BehaviourException {
		ConfigurazioneGestioneConsegnaNotifiche config = new ConfigurazioneGestioneConsegnaNotifiche();
		if(pasa==null || pasa.getDatiConnettore()==null || pasa.getDatiConnettore().sizeProprietaList()==0) {
			// Configurazione non disponibile
			return GestioneConsegnaNotificheUtils.getGestioneDefault();
		}
		
		for (Proprieta p : pasa.getDatiConnettore().getProprietaList()) {
			read(config, p);
		}	

		return config;
	}
	private static void read(ConfigurazioneGestioneConsegnaNotifiche config, Proprieta p) throws BehaviourException {
		try {
			String nome = p.getNome();
			String valore = p.getValore().trim();
			
			if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_CADENZA.equals(nome)) {
				config.setCadenzaRispedizione(Integer.valueOf(valore));
			}
			
			else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX.equals(nome)
					|| 
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_CODE_LIST.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_LEFT_INTERVAL.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_RIGHT_INTERVAL.equals(nome)) {
				read2xx(config, nome, valore);
			}
			
			else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX.equals(nome) 
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_CODE_LIST.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_LEFT_INTERVAL.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_RIGHT_INTERVAL.equals(nome)) {
				read3xx(config, nome, valore);
			}
			
			else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_CODE_LIST.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_LEFT_INTERVAL.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_RIGHT_INTERVAL.equals(nome)) {
				read4xx(config, nome, valore);
			}
			
			else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_CODE_LIST.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_LEFT_INTERVAL.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_RIGHT_INTERVAL.equals(nome)) {
				read5xx(config, nome, valore);
			}
			
			else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_CODE.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_ACTOR.equals(nome)
					||
					Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_MESSAGE.equals(nome)) {
				readFault(config, nome, valore);
			}
			
			else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_TIPO_MESSAGGIO_DA_NOTIFICARE.equals(nome)) {
				setMultiDeliverNotificheGestioneTipoMessaggioDaNotificare(config, valore);
			}
			else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_INIETTA_CONTESTO_SINCRONO.equals(nome)) {
				setMultiDeliverNotificheGestioneIniettaContestSincronoDaNotificare(config, valore);
			}
			else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_TIPO_HTTP_NOTIFICA.equals(nome)) {
				setMultiDeliverNotificheGestioneTipoHttpNotifica(config, valore);
			}
			
		}catch(Exception e) {
			throw new BehaviourException("Configurazione condizionale non corretta (proprietà:"+p.getNome()+" valore:'"+p.getValore()+"'): "+e.getMessage(),e);
		}
	}
	private static void read2xx(ConfigurazioneGestioneConsegnaNotifiche config, String nome, String valore) throws NotFoundException {
		if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX.equals(nome)) {
			config.setGestioneTrasporto2xx(TipoGestioneNotificaTrasporto.toEnumConstant(valore, true));
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_CODE_LIST.equals(nome)) {
			setMultiDeliverNotificheGestioneErroreTipoGestioneTrasporto2xxCodeList(config, valore);
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_LEFT_INTERVAL.equals(nome)) {
			config.setGestioneTrasporto2xx_leftInterval(Integer.valueOf(valore));
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_RIGHT_INTERVAL.equals(nome)) {
			config.setGestioneTrasporto2xx_rightInterval(Integer.valueOf(valore));
		}
	}
	private static void setMultiDeliverNotificheGestioneErroreTipoGestioneTrasporto2xxCodeList(ConfigurazioneGestioneConsegnaNotifiche config, String valore) {
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
	private static void read3xx(ConfigurazioneGestioneConsegnaNotifiche config, String nome, String valore) throws NotFoundException {
		if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX.equals(nome)) {
			config.setGestioneTrasporto3xx(TipoGestioneNotificaTrasporto.toEnumConstant(valore, true));
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_CODE_LIST.equals(nome)) {
			setMultiDeliverNotificheGestioneErroreTipoGestioneTrasporto3xxCodeList(config, valore);
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_LEFT_INTERVAL.equals(nome)) {
			config.setGestioneTrasporto3xx_leftInterval(Integer.valueOf(valore));
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_RIGHT_INTERVAL.equals(nome)) {
			config.setGestioneTrasporto3xx_rightInterval(Integer.valueOf(valore));
		}
	}
	private static void setMultiDeliverNotificheGestioneErroreTipoGestioneTrasporto3xxCodeList(ConfigurazioneGestioneConsegnaNotifiche config, String valore) {
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
	private static void read4xx(ConfigurazioneGestioneConsegnaNotifiche config, String nome, String valore) throws NotFoundException {
		if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX.equals(nome)) {
			config.setGestioneTrasporto4xx(TipoGestioneNotificaTrasporto.toEnumConstant(valore, true));
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_CODE_LIST.equals(nome)) {
			setMultiDeliverNotificheGestioneErroreTipoGestioneTrasporto4xxCodeList(config, valore);
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_LEFT_INTERVAL.equals(nome)) {
			config.setGestioneTrasporto4xx_leftInterval(Integer.valueOf(valore));
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_RIGHT_INTERVAL.equals(nome)) {
			config.setGestioneTrasporto4xx_rightInterval(Integer.valueOf(valore));
		}
	}
	private static void setMultiDeliverNotificheGestioneErroreTipoGestioneTrasporto4xxCodeList(ConfigurazioneGestioneConsegnaNotifiche config, String valore) {
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
	private static void read5xx(ConfigurazioneGestioneConsegnaNotifiche config, String nome, String valore) throws NotFoundException {
		if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX.equals(nome)) {
			config.setGestioneTrasporto5xx(TipoGestioneNotificaTrasporto.toEnumConstant(valore, true));
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_CODE_LIST.equals(nome)) {
			setMultiDeliverNotificheGestioneErroreTipoGestioneTrasporto5xxCodeList(config, valore);
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_LEFT_INTERVAL.equals(nome)) {
			config.setGestioneTrasporto5xx_leftInterval(Integer.valueOf(valore));
		}
		else if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_RIGHT_INTERVAL.equals(nome)) {
			config.setGestioneTrasporto5xx_rightInterval(Integer.valueOf(valore));
		}
	}
	private static void setMultiDeliverNotificheGestioneErroreTipoGestioneTrasporto5xxCodeList(ConfigurazioneGestioneConsegnaNotifiche config, String valore) {
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
	private static void readFault(ConfigurazioneGestioneConsegnaNotifiche config, String nome, String valore) throws NotFoundException {
		if(Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT.equals(nome)) {
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
	}
	private static void setMultiDeliverNotificheGestioneTipoMessaggioDaNotificare(ConfigurazioneGestioneConsegnaNotifiche config, String valore) throws NotFoundException {
		if(valore!=null) {
			MessaggioDaNotificare tipo = MessaggioDaNotificare.toEnumConstant(valore, false);
			if(tipo!=null) {
				config.setMessaggioDaNotificare(tipo);
			}
		}
	}
	private static void setMultiDeliverNotificheGestioneIniettaContestSincronoDaNotificare(ConfigurazioneGestioneConsegnaNotifiche config, String valore) throws NotFoundException {
		if(valore!=null) {
			config.setInjectTransactionSyncContext("true".equals(valore));
		}
	}
	private static void setMultiDeliverNotificheGestioneTipoHttpNotifica(ConfigurazioneGestioneConsegnaNotifiche config, String valore) {
		if(valore!=null) {
			HttpRequestMethod tipoHttp = null;
			if(!Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_TIPO_HTTP_NOTIFICA_USA_QUELLO_DELLA_RICHIESTA.equals(valore)) {
				try {
					tipoHttp = HttpRequestMethod.valueOf(valore.toUpperCase());
				}catch(Exception e) {
					// ignore
				}
			}
			if(tipoHttp!=null) {
				config.setHttpMethod(tipoHttp);
			}
		}
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
			save2xx(pasa, configurazione);
		}
		
		if(configurazione.getGestioneTrasporto3xx()!=null) {
			save3xx(pasa, configurazione);
		}
		
		if(configurazione.getGestioneTrasporto4xx()!=null) {
			save4xx(pasa, configurazione);
		}
		
		if(configurazione.getGestioneTrasporto5xx()!=null) {
			save5xx(pasa, configurazione);
		}
		
		if(configurazione.getFault()!=null) {
			saveFault(pasa, configurazione);
		}
		
		if(configurazione.getMessaggioDaNotificare()!=null) {
			BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_TIPO_MESSAGGIO_DA_NOTIFICARE, configurazione.getMessaggioDaNotificare().getValue());
		}else {
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_TIPO_MESSAGGIO_DA_NOTIFICARE);
		}
		
		if(configurazione.isInjectTransactionSyncContext()) {
			BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_INIETTA_CONTESTO_SINCRONO, "true");
		}
		else {
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_INIETTA_CONTESTO_SINCRONO);
		}
		
		if(configurazione.getHttpMethod()!=null) {
			BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_TIPO_HTTP_NOTIFICA, configurazione.getHttpMethod().name());
		}else {
			BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_TIPO_HTTP_NOTIFICA);
		}
	}
	private static void save2xx(PortaApplicativaServizioApplicativo pasa, ConfigurazioneGestioneConsegnaNotifiche configurazione) throws BehaviourException {
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
				BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_2XX_CODE_LIST, 
						convertAsStringValue(configurazione.getGestioneTrasporto2xx_codes()));
			}
			else {
				throw new BehaviourException("[2xx] Code undefined");
			}
			break;
		}
	}
	private static void save3xx(PortaApplicativaServizioApplicativo pasa, ConfigurazioneGestioneConsegnaNotifiche configurazione) throws BehaviourException {
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
				BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_3XX_CODE_LIST, 
						convertAsStringValue(configurazione.getGestioneTrasporto3xx_codes()));
			}
			else {
				throw new BehaviourException("[3xx] Code undefined");
			}
			break;
		}
	}
	private static void save4xx(PortaApplicativaServizioApplicativo pasa, ConfigurazioneGestioneConsegnaNotifiche configurazione) throws BehaviourException {
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
				BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_4XX_CODE_LIST, 
						convertAsStringValue(configurazione.getGestioneTrasporto4xx_codes()));
			}
			else {
				throw new BehaviourException("[4xx] Code undefined");
			}
			break;
		}
	}
	private static void save5xx(PortaApplicativaServizioApplicativo pasa, ConfigurazioneGestioneConsegnaNotifiche configurazione) throws BehaviourException {
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
				BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_TRASPORTO_5XX_CODE_LIST, 
						convertAsStringValue(configurazione.getGestioneTrasporto5xx_codes()));
			}
			else {
				throw new BehaviourException("[5xx] Code undefined");
			}
			break;
		}
	}
	private static void saveFault(PortaApplicativaServizioApplicativo pasa, ConfigurazioneGestioneConsegnaNotifiche configurazione) {
		BehaviourPropertiesUtils.addProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT, configurazione.getFault().getValue());
		BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_CODE);
		BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_ACTOR);
		BehaviourPropertiesUtils.removeProprieta(pasa.getDatiConnettore(),Costanti.MULTI_DELIVER_NOTIFICHE_GESTIONE_ERRORE_TIPO_GESTIONE_FAULT_MESSAGE);
		
		switch (configurazione.getFault()) {
		case CONSEGNA_COMPLETATA:
		case CONSEGNA_FALLITA:
			break;
		case CONSEGNA_COMPLETATA_PERSONALIZZATA:
		case CONSEGNA_FALLITA_PERSONALIZZATA:
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
	private static String convertAsStringValue(List<Integer> codes) {
		StringBuilder bf = new StringBuilder();
		for (Integer code : codes) {
			if(bf.length()>0) {
				bf.append(",");
			}
			bf.append(code);
		}
		return bf.toString();
	}
	

	
	public static MessaggioDaNotificare readMessaggiNotificabili(PortaApplicativa pa, List<String> serviziApplicativiAbilitatiForwardTo) throws BehaviourException {
		BooleanNullable richiesta = BooleanNullable.NULL();
		BooleanNullable risposta = BooleanNullable.NULL();
		if(serviziApplicativiAbilitatiForwardTo!=null && !serviziApplicativiAbilitatiForwardTo.isEmpty()) {
			for (PortaApplicativaServizioApplicativo pasa : pa.getServizioApplicativoList()) {
				if(serviziApplicativiAbilitatiForwardTo.contains(pasa.getNome())) {
					readMessaggioDaNotificare(pasa, richiesta, risposta);
					if(richiesta.getValue()!=null && richiesta.getValue().booleanValue() && 
							risposta.getValue()!=null && risposta.getValue().booleanValue()) {
						// trovati entrambi, e' inutile che continuo ad analizzare
						break;
					}
				}
			}
		}
		return convertToMessaggioDaNotificare(richiesta, risposta);
	}
	private static void readMessaggioDaNotificare(PortaApplicativaServizioApplicativo pasa,
			BooleanNullable richiesta, BooleanNullable risposta) throws BehaviourException {
		ConfigurazioneGestioneConsegnaNotifiche config = MultiDeliverUtils.read(pasa);
		if(config!=null) {
			MessaggioDaNotificare check = config.getMessaggioDaNotificare();
			if(check!=null) {
				switch (check) {
				case RICHIESTA:
					richiesta.setValue(true);
					break;
				case RISPOSTA:
					risposta.setValue(true);
					break;
				case ENTRAMBI:
					richiesta.setValue(true);
					risposta.setValue(true);
					break;
				}
			}
			else {
				// default: richiesta
				richiesta.setValue(true);
			}
		}
	}
	private static MessaggioDaNotificare convertToMessaggioDaNotificare(BooleanNullable richiesta, BooleanNullable risposta) {
		MessaggioDaNotificare tipiMessaggiNotificabili = null;
		if(richiesta.getValue()!=null && richiesta.getValue().booleanValue() && 
				risposta.getValue()!=null && risposta.getValue().booleanValue()) {
			tipiMessaggiNotificabili = MessaggioDaNotificare.ENTRAMBI;
		}
		else if(risposta.getValue()!=null && risposta.getValue().booleanValue()) {
			tipiMessaggiNotificabili = MessaggioDaNotificare.RISPOSTA;
		}
		else if(richiesta.getValue()!=null && richiesta.getValue().booleanValue()) {
			tipiMessaggiNotificabili = MessaggioDaNotificare.RICHIESTA;
		}
		return tipiMessaggiNotificabili;
	}
	
	public static boolean isSaveTransactionContext(PortaApplicativa pa, List<String> serviziApplicativiAbilitatiForwardTo) throws BehaviourException {
		if(serviziApplicativiAbilitatiForwardTo!=null && !serviziApplicativiAbilitatiForwardTo.isEmpty()) {
			for (PortaApplicativaServizioApplicativo pasa : pa.getServizioApplicativoList()) {
				if(serviziApplicativiAbilitatiForwardTo.contains(pasa.getNome())) {
					ConfigurazioneGestioneConsegnaNotifiche config = MultiDeliverUtils.read(pasa);
					if(config!=null && config.isInjectTransactionSyncContext()) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
