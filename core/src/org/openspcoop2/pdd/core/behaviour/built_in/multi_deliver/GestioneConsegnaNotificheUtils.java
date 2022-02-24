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
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;
import org.openspcoop2.core.config.GestioneErroreSoapFault;
import org.openspcoop2.core.config.constants.GestioneErroreComportamento;
import org.openspcoop2.pdd.core.behaviour.BehaviourException;

/**
 * ConfigurazioneGestoneErrori
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestioneConsegnaNotificheUtils  {
	
	public final static String LABEL_CODE = "Code";
	public final static String LABEL_ACTOR = "Actor";
	public final static String LABEL_MESSAGE = "Message";
	public final static String LABEL_TYPE = "Type";
	public final static String LABEL_STATUS = "Status";
	public final static String LABEL_CLAIMS = "Claims";
	
	public static ConfigurazioneGestioneConsegnaNotifiche getGestioneDefault() throws BehaviourException {
		
		ConfigurazioneGestioneConsegnaNotifiche config = new ConfigurazioneGestioneConsegnaNotifiche();
		
		config.setGestioneTrasporto2xx(TipoGestioneNotificaTrasporto.CONSEGNA_COMPLETATA);
		config.setGestioneTrasporto3xx(TipoGestioneNotificaTrasporto.CONSEGNA_FALLITA);
		config.setGestioneTrasporto4xx(TipoGestioneNotificaTrasporto.CONSEGNA_FALLITA);
		config.setGestioneTrasporto5xx(TipoGestioneNotificaTrasporto.CONSEGNA_FALLITA);
		config.setFault(TipoGestioneNotificaFault.CONSEGNA_COMPLETATA);
		
		return config;
		
	}
	
	public static GestioneErrore toGestioneErrore(ConfigurazioneGestioneConsegnaNotifiche config) throws BehaviourException {
		
		GestioneErrore gestioneErrore = new GestioneErrore();
		gestioneErrore.setComportamento(GestioneErroreComportamento.RISPEDISCI);
		
		if(config.getCadenzaRispedizione()!=null) {
			gestioneErrore.setCadenzaRispedizione(config.getCadenzaRispedizione()+"");
		}
		
		if(config.getGestioneTrasporto2xx()!=null) {
			switch (config.getGestioneTrasporto2xx()) {
			case CONSEGNA_COMPLETATA:
				GestioneErroreCodiceTrasporto codiceTrasportoCompletata = new GestioneErroreCodiceTrasporto();
				codiceTrasportoCompletata.setValoreMinimo(200);
				codiceTrasportoCompletata.setValoreMassimo(299);
				codiceTrasportoCompletata.setComportamento(GestioneErroreComportamento.ACCETTA);
				gestioneErrore.addCodiceTrasporto(codiceTrasportoCompletata);
				break;
			case CONSEGNA_FALLITA:
				GestioneErroreCodiceTrasporto codiceTrasportoFallita = new GestioneErroreCodiceTrasporto();
				codiceTrasportoFallita.setValoreMinimo(200);
				codiceTrasportoFallita.setValoreMassimo(299);
				codiceTrasportoFallita.setComportamento(GestioneErroreComportamento.RISPEDISCI);
				gestioneErrore.addCodiceTrasporto(codiceTrasportoFallita);
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto2xx_codes()==null || config.getGestioneTrasporto2xx_codes().isEmpty()) {
					throw new BehaviourException("Lista dei codici accettati (classe 2xx) non fornita");
				}
				for (Integer code : config.getGestioneTrasporto2xx_codes()) {
					GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
					codiceTrasporto.setValoreMinimo(code);
					codiceTrasporto.setValoreMassimo(code);
					codiceTrasporto.setComportamento(GestioneErroreComportamento.ACCETTA);	
					gestioneErrore.addCodiceTrasporto(codiceTrasporto);
				}
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto2xx_leftInterval()==null) {
					throw new BehaviourException("Intervallo sinistro (classe 2xx) non definito");
				}
				if(config.getGestioneTrasporto2xx_rightInterval()==null) {
					throw new BehaviourException("Intervallo destro (classe 2xx) non definito");
				}
				GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
				codiceTrasporto.setValoreMinimo(config.getGestioneTrasporto2xx_leftInterval());
				codiceTrasporto.setValoreMassimo(config.getGestioneTrasporto2xx_rightInterval());
				codiceTrasporto.setComportamento(GestioneErroreComportamento.ACCETTA);	
				gestioneErrore.addCodiceTrasporto(codiceTrasporto);
				break;
			}
		}
		
		if(config.getGestioneTrasporto3xx()!=null) {
			switch (config.getGestioneTrasporto3xx()) {
			case CONSEGNA_COMPLETATA:
				GestioneErroreCodiceTrasporto codiceTrasportoCompletata = new GestioneErroreCodiceTrasporto();
				codiceTrasportoCompletata.setValoreMinimo(300);
				codiceTrasportoCompletata.setValoreMassimo(399);
				codiceTrasportoCompletata.setComportamento(GestioneErroreComportamento.ACCETTA);
				gestioneErrore.addCodiceTrasporto(codiceTrasportoCompletata);
				break;
			case CONSEGNA_FALLITA:
				GestioneErroreCodiceTrasporto codiceTrasportoFallita = new GestioneErroreCodiceTrasporto();
				codiceTrasportoFallita.setValoreMinimo(300);
				codiceTrasportoFallita.setValoreMassimo(399);
				codiceTrasportoFallita.setComportamento(GestioneErroreComportamento.RISPEDISCI);
				gestioneErrore.addCodiceTrasporto(codiceTrasportoFallita);
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto3xx_codes()==null || config.getGestioneTrasporto3xx_codes().isEmpty()) {
					throw new BehaviourException("Lista dei codici accettati (classe 3xx) non fornita");
				}
				for (Integer code : config.getGestioneTrasporto3xx_codes()) {
					GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
					codiceTrasporto.setValoreMinimo(code);
					codiceTrasporto.setValoreMassimo(code);
					codiceTrasporto.setComportamento(GestioneErroreComportamento.ACCETTA);	
					gestioneErrore.addCodiceTrasporto(codiceTrasporto);
				}
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto3xx_leftInterval()==null) {
					throw new BehaviourException("Intervallo sinistro (classe 3xx) non definito");
				}
				if(config.getGestioneTrasporto3xx_rightInterval()==null) {
					throw new BehaviourException("Intervallo destro (classe 3xx) non definito");
				}
				GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
				codiceTrasporto.setValoreMinimo(config.getGestioneTrasporto3xx_leftInterval());
				codiceTrasporto.setValoreMassimo(config.getGestioneTrasporto3xx_rightInterval());
				codiceTrasporto.setComportamento(GestioneErroreComportamento.ACCETTA);	
				gestioneErrore.addCodiceTrasporto(codiceTrasporto);
				break;
			}		
		}
		
		if(config.getGestioneTrasporto4xx()!=null) {
			switch (config.getGestioneTrasporto4xx()) {
			case CONSEGNA_COMPLETATA:
				GestioneErroreCodiceTrasporto codiceTrasportoCompletata = new GestioneErroreCodiceTrasporto();
				codiceTrasportoCompletata.setValoreMinimo(400);
				codiceTrasportoCompletata.setValoreMassimo(499);
				codiceTrasportoCompletata.setComportamento(GestioneErroreComportamento.ACCETTA);
				gestioneErrore.addCodiceTrasporto(codiceTrasportoCompletata);
				break;
			case CONSEGNA_FALLITA:
				GestioneErroreCodiceTrasporto codiceTrasportoFallita = new GestioneErroreCodiceTrasporto();
				codiceTrasportoFallita.setValoreMinimo(400);
				codiceTrasportoFallita.setValoreMassimo(499);
				codiceTrasportoFallita.setComportamento(GestioneErroreComportamento.RISPEDISCI);
				gestioneErrore.addCodiceTrasporto(codiceTrasportoFallita);
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto4xx_codes()==null || config.getGestioneTrasporto4xx_codes().isEmpty()) {
					throw new BehaviourException("Lista dei codici accettati (classe 4xx) non fornita");
				}
				for (Integer code : config.getGestioneTrasporto4xx_codes()) {
					GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
					codiceTrasporto.setValoreMinimo(code);
					codiceTrasporto.setValoreMassimo(code);
					codiceTrasporto.setComportamento(GestioneErroreComportamento.ACCETTA);	
					gestioneErrore.addCodiceTrasporto(codiceTrasporto);
				}
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto4xx_leftInterval()==null) {
					throw new BehaviourException("Intervallo sinistro (classe 4xx) non definito");
				}
				if(config.getGestioneTrasporto4xx_rightInterval()==null) {
					throw new BehaviourException("Intervallo destro (classe 4xx) non definito");
				}
				GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
				codiceTrasporto.setValoreMinimo(config.getGestioneTrasporto4xx_leftInterval());
				codiceTrasporto.setValoreMassimo(config.getGestioneTrasporto4xx_rightInterval());
				codiceTrasporto.setComportamento(GestioneErroreComportamento.ACCETTA);	
				gestioneErrore.addCodiceTrasporto(codiceTrasporto);
				break;
			}
		}
		
		if(config.getGestioneTrasporto5xx()!=null) {
			switch (config.getGestioneTrasporto5xx()) {
			case CONSEGNA_COMPLETATA:
				GestioneErroreCodiceTrasporto codiceTrasportoCompletata = new GestioneErroreCodiceTrasporto();
				codiceTrasportoCompletata.setValoreMinimo(500);
				codiceTrasportoCompletata.setValoreMassimo(599);
				codiceTrasportoCompletata.setComportamento(GestioneErroreComportamento.ACCETTA);
				gestioneErrore.addCodiceTrasporto(codiceTrasportoCompletata);
				break;
			case CONSEGNA_FALLITA:
				GestioneErroreCodiceTrasporto codiceTrasportoFallita = new GestioneErroreCodiceTrasporto();
				codiceTrasportoFallita.setValoreMinimo(500);
				codiceTrasportoFallita.setValoreMassimo(599);
				codiceTrasportoFallita.setComportamento(GestioneErroreComportamento.RISPEDISCI);
				gestioneErrore.addCodiceTrasporto(codiceTrasportoFallita);
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto5xx_codes()==null || config.getGestioneTrasporto5xx_codes().isEmpty()) {
					throw new BehaviourException("Lista dei codici accettati (classe 5xx) non fornita");
				}
				for (Integer code : config.getGestioneTrasporto5xx_codes()) {
					GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
					codiceTrasporto.setValoreMinimo(code);
					codiceTrasporto.setValoreMassimo(code);
					codiceTrasporto.setComportamento(GestioneErroreComportamento.ACCETTA);	
					gestioneErrore.addCodiceTrasporto(codiceTrasporto);
				}
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto5xx_leftInterval()==null) {
					throw new BehaviourException("Intervallo sinistro (classe 5xx) non definito");
				}
				if(config.getGestioneTrasporto5xx_rightInterval()==null) {
					throw new BehaviourException("Intervallo destro (classe 5xx) non definito");
				}
				GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
				codiceTrasporto.setValoreMinimo(config.getGestioneTrasporto5xx_leftInterval());
				codiceTrasporto.setValoreMassimo(config.getGestioneTrasporto5xx_rightInterval());
				codiceTrasporto.setComportamento(GestioneErroreComportamento.ACCETTA);	
				gestioneErrore.addCodiceTrasporto(codiceTrasporto);
				break;
			}
		}
		
		if(config.getFault()!=null) {
			switch (config.getFault()) {
			case CONSEGNA_COMPLETATA:
				GestioneErroreSoapFault faultCompletata = new GestioneErroreSoapFault();
				faultCompletata.setComportamento(GestioneErroreComportamento.ACCETTA);
				gestioneErrore.addSoapFault(faultCompletata);
				break;
			case CONSEGNA_FALLITA:
				GestioneErroreSoapFault faultFallita = new GestioneErroreSoapFault();
				faultFallita.setComportamento(GestioneErroreComportamento.RISPEDISCI);
				gestioneErrore.addSoapFault(faultFallita);
				break;
			case CONSEGNA_COMPLETATA_PERSONALIZZATA:
			case CONSEGNA_FALLITA_PERSONALIZZATA:
				GestioneErroreSoapFault faultCustom = new GestioneErroreSoapFault();
				if(TipoGestioneNotificaFault.CONSEGNA_FALLITA_PERSONALIZZATA.equals(config.getFault())) {
					faultCustom.setComportamento(GestioneErroreComportamento.RISPEDISCI);
				}
				else {
					faultCustom.setComportamento(GestioneErroreComportamento.ACCETTA);
				}
				if(config.getFaultCode()!=null && !"".equals(config.getFaultCode())) {
					faultCustom.setFaultCode(config.getFaultCode());
				}
				if(config.getFaultActor()!=null && !"".equals(config.getFaultActor())) {
					faultCustom.setFaultActor(config.getFaultActor());
				}
				if(config.getFaultMessage()!=null && !"".equals(config.getFaultMessage())) {
					faultCustom.setFaultString(config.getFaultMessage());
				}
				gestioneErrore.addSoapFault(faultCustom);
				break;
			}
		}
		
		return gestioneErrore;
	}
	
	
	public static String toString(ConfigurazioneGestioneConsegnaNotifiche config, boolean soap) throws BehaviourException {
		
		StringBuilder bf = new StringBuilder("Consegna completata con codice");
		
		boolean first = true;
		
		if(config.getGestioneTrasporto2xx()!=null) {
			switch (config.getGestioneTrasporto2xx()) {
			case CONSEGNA_COMPLETATA:
				bf.append(" 200-299");
				first = false;
				break;
			case CONSEGNA_FALLITA:
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto2xx_codes()==null || config.getGestioneTrasporto2xx_codes().isEmpty()) {
					throw new BehaviourException("Lista dei codici accettati (classe 2xx) non fornita");
				}
				bf.append(" ");
				int index = 0;
				for (Integer code : config.getGestioneTrasporto2xx_codes()) {
					if(index>0) {
						bf.append(",");
					}
					bf.append(code);
					index++;
				}
				first = false;
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto2xx_leftInterval()==null) {
					throw new BehaviourException("Intervallo sinistro (classe 2xx) non definito");
				}
				if(config.getGestioneTrasporto2xx_rightInterval()==null) {
					throw new BehaviourException("Intervallo destro (classe 2xx) non definito");
				}
				bf.append(" ").append(config.getGestioneTrasporto2xx_leftInterval()).append("-").append(config.getGestioneTrasporto2xx_rightInterval());
				first = false;
				break;
			}
		}
		
		if(config.getGestioneTrasporto3xx()!=null) {
			switch (config.getGestioneTrasporto3xx()) {
			case CONSEGNA_COMPLETATA:
				if(!first) {
					bf.append(" o");
				}
				bf.append(" 300-399");
				first = false;
				break;
			case CONSEGNA_FALLITA:
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto3xx_codes()==null || config.getGestioneTrasporto3xx_codes().isEmpty()) {
					throw new BehaviourException("Lista dei codici accettati (classe 3xx) non fornita");
				}
				if(!first) {
					bf.append(" o");
				}
				bf.append(" ");
				int index = 0;
				for (Integer code : config.getGestioneTrasporto3xx_codes()) {
					if(index>0) {
						bf.append(",");
					}
					bf.append(code);
					index++;
				}
				first = false;
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto3xx_leftInterval()==null) {
					throw new BehaviourException("Intervallo sinistro (classe 3xx) non definito");
				}
				if(config.getGestioneTrasporto3xx_rightInterval()==null) {
					throw new BehaviourException("Intervallo destro (classe 3xx) non definito");
				}
				if(!first) {
					bf.append(" o");
				}
				bf.append(" ").append(config.getGestioneTrasporto3xx_leftInterval()).append("-").append(config.getGestioneTrasporto3xx_rightInterval());
				first = false;
				break;
			}
		}
		
		if(config.getGestioneTrasporto4xx()!=null) {
			switch (config.getGestioneTrasporto4xx()) {
			case CONSEGNA_COMPLETATA:
				if(!first) {
					bf.append(" o");
				}
				bf.append(" 400-499");
				first = false;
				break;
			case CONSEGNA_FALLITA:
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto4xx_codes()==null || config.getGestioneTrasporto4xx_codes().isEmpty()) {
					throw new BehaviourException("Lista dei codici accettati (classe 4xx) non fornita");
				}
				if(!first) {
					bf.append(" o");
				}
				bf.append(" ");
				int index = 0;
				for (Integer code : config.getGestioneTrasporto4xx_codes()) {
					if(index>0) {
						bf.append(",");
					}
					bf.append(code);
					index++;
				}
				first = false;
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto4xx_leftInterval()==null) {
					throw new BehaviourException("Intervallo sinistro (classe 4xx) non definito");
				}
				if(config.getGestioneTrasporto4xx_rightInterval()==null) {
					throw new BehaviourException("Intervallo destro (classe 4xx) non definito");
				}
				if(!first) {
					bf.append(" o");
				}
				bf.append(" ").append(config.getGestioneTrasporto4xx_leftInterval()).append("-").append(config.getGestioneTrasporto4xx_rightInterval());
				first = false;
				break;
			}
		}
		
		if(config.getGestioneTrasporto5xx()!=null) {
			switch (config.getGestioneTrasporto5xx()) {
			case CONSEGNA_COMPLETATA:
				if(!first) {
					bf.append(" o");
				}
				bf.append(" 500-599");
				first = false;
				break;
			case CONSEGNA_FALLITA:
				break;
			case CODICI_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto5xx_codes()==null || config.getGestioneTrasporto5xx_codes().isEmpty()) {
					throw new BehaviourException("Lista dei codici accettati (classe 5xx) non fornita");
				}
				if(!first) {
					bf.append(" o");
				}
				bf.append(" ");
				int index = 0;
				for (Integer code : config.getGestioneTrasporto5xx_codes()) {
					if(index>0) {
						bf.append(",");
					}
					bf.append(code);
					index++;
				}
				first = false;
				break;
			case INTERVALLO_CONSEGNA_COMPLETATA:
				if(config.getGestioneTrasporto5xx_leftInterval()==null) {
					throw new BehaviourException("Intervallo sinistro (classe 5xx) non definito");
				}
				if(config.getGestioneTrasporto5xx_rightInterval()==null) {
					throw new BehaviourException("Intervallo destro (classe 5xx) non definito");
				}
				if(!first) {
					bf.append(" o");
				}
				bf.append(" ").append(config.getGestioneTrasporto5xx_leftInterval()).append("-").append(config.getGestioneTrasporto5xx_rightInterval());
				first = false;
				break;
			}
		}
		
		if(config.getFault()!=null) {
			switch (config.getFault()) {
			case CONSEGNA_COMPLETATA:
				if(!first) {
					bf.append(" o");
				}
				if(soap) {
					bf.append(" SOAP Fault");
				}
				else {
					bf.append(" Problem Detail");
				}
				break;
			case CONSEGNA_FALLITA:
				break;
			case CONSEGNA_FALLITA_PERSONALIZZATA:
				break;
			case CONSEGNA_COMPLETATA_PERSONALIZZATA:
				if(!first) {
					bf.append(" o");
				}
				if(soap) {
					bf.append(" SOAP Fault");
				}
				else {
					bf.append(" Problem Detail");
				}
				
				String tipoFaultCompletato = null;
				if(!StringUtils.isEmpty(config.getFaultCode())) { 
					String label = soap ? LABEL_CODE : LABEL_TYPE;
					tipoFaultCompletato = label;
				}
				if(!StringUtils.isEmpty(config.getFaultActor())) {
					String label = soap ? LABEL_ACTOR : LABEL_STATUS;
					if(tipoFaultCompletato!=null) {
						tipoFaultCompletato = tipoFaultCompletato +", ";
					}
					else {
						tipoFaultCompletato = "";
					}
					tipoFaultCompletato = tipoFaultCompletato + label;
				}
				if(!StringUtils.isEmpty(config.getFaultMessage())) {
					String label = soap ? LABEL_MESSAGE : LABEL_CLAIMS;
					if(tipoFaultCompletato!=null) {
						tipoFaultCompletato = tipoFaultCompletato +", ";
					}
					else {
						tipoFaultCompletato = "";
					}
					tipoFaultCompletato = tipoFaultCompletato + label;
				}
				String prefix = "";
				// Siamo costruendo una stringa che indica quando vi è una consegna completata.
				// Se il fault ha un match ed è configurato per fallire, non vi è chiamarente una consegna completata.
				// Se non si ha un fault con il match si passa ad analizzare il codice di trasporto, quindi non vale dire "non" contenente
//				if(TipoGestioneNotificaFault.CONSEGNA_FALLITA_PERSONALIZZATA.equals(config.getFault())) {
//					prefix = " non";
//				}
				if(tipoFaultCompletato!=null) {
					tipoFaultCompletato = prefix+" contenente le personalizzazioni definite per "+tipoFaultCompletato;
				}
				else {
					tipoFaultCompletato = prefix+" contenente le personalizzazioni indicate"; // configurazione in corso
				}
				bf.append(tipoFaultCompletato);
				
				break;
			}
		}
		
		String s = bf.toString();
		if(s!=null) {
			// check caso limite senza consegne completate in alcun codice http
			String errorMsg = null;
			String replaceMsg = null;
			if(soap) {
				errorMsg = "codice SOAP Fault";
				replaceMsg = "SOAP Fault";
			}
			else {
				errorMsg = "codice Problem Detail";
				replaceMsg = "Problem Detail";
			}
			if(s.contains(errorMsg)) {
				s = s.replace(errorMsg, replaceMsg);
			}
		}
		return s;
	}
}
