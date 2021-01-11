/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.config.utils;

import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaApplicativaServizioApplicativo;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ProtocolProperty;
import org.openspcoop2.core.config.ServizioApplicativo;

/**
 * CleanerOpenSPCoop2Extensions
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CleanerOpenSPCoop2Extensions {

	public void clean(Configurazione configurazionePdD){

		if(configurazionePdD.getRoutingTable()!=null){
			configurazionePdD.getRoutingTable().setAbilitata(null);
		}	

		if(configurazionePdD.getAccessoRegistro()!=null){
			if(configurazionePdD.getAccessoRegistro().sizeRegistroList()>0){
				for (AccessoRegistroRegistro accessoRegistro : configurazionePdD.getAccessoRegistro().getRegistroList()) {
					accessoRegistro.setTipoDatabase(null);
					accessoRegistro.setGenericPropertiesMap(null);
				}
			}
		}
		
		if(configurazionePdD.getGestioneErrore()!=null){
			if(configurazionePdD.getGestioneErrore().getComponenteCooperazione()!=null){
				this.clean(configurazionePdD.getGestioneErrore().getComponenteCooperazione());
			}	
			if(configurazionePdD.getGestioneErrore().getComponenteIntegrazione()!=null){
				this.clean(configurazionePdD.getGestioneErrore().getComponenteIntegrazione());
			}
		}
		
		if(configurazionePdD.getSystemProperties()!=null && configurazionePdD.getSystemProperties().sizeSystemPropertyList()<=0) {
			configurazionePdD.setSystemProperties(null); // altrimeni da errore di validazione in fase di import
		}
		
	}

	public void clean(org.openspcoop2.core.config.GenericProperties policy) {
		
	}
	
	public void clean(org.openspcoop2.core.config.Soggetto soggettoConfig){
		
		soggettoConfig.setSuperUser(null);
		
		if(soggettoConfig.sizeConnettoreList()>0){
			for (org.openspcoop2.core.config.Connettore connettore : soggettoConfig.getConnettoreList()) {
				this.clean(connettore);	
			}
		}
	}

	public void clean(ServizioApplicativo servizioApplicativo){
		
		servizioApplicativo.setIdSoggetto(null);
		servizioApplicativo.setTipoSoggettoProprietario(null);
		servizioApplicativo.setNomeSoggettoProprietario(null);
		servizioApplicativo.setTipologiaErogazione(null);
		servizioApplicativo.setTipologiaFruizione(null);
		
		if(servizioApplicativo.getInvocazioneServizio()!=null){
			if(servizioApplicativo.getInvocazioneServizio().getConnettore()!=null){
				this.clean(servizioApplicativo.getInvocazioneServizio().getConnettore());	
			}
			if(servizioApplicativo.getInvocazioneServizio().getGestioneErrore()!=null){
				this.clean(servizioApplicativo.getInvocazioneServizio().getGestioneErrore());	
			}
		}
		
		if(servizioApplicativo.getRispostaAsincrona()!=null){
			if(servizioApplicativo.getRispostaAsincrona().getConnettore()!=null){
				this.clean(servizioApplicativo.getRispostaAsincrona().getConnettore());	
			}
			if(servizioApplicativo.getRispostaAsincrona().getGestioneErrore()!=null){
				this.clean(servizioApplicativo.getRispostaAsincrona().getGestioneErrore());	
			}
		}
		
		if(servizioApplicativo.sizeProtocolPropertyList()>0) {
			for (ProtocolProperty pp : servizioApplicativo.getProtocolPropertyList()) {
				this.clean(pp);
			}
		}
	}

	public void clean(PortaDelegata portaDelegata){

		portaDelegata.setIdSoggetto(null);
		portaDelegata.setIdAccordo(null);
		portaDelegata.setIdPortType(null);
		portaDelegata.setStatoMessageSecurity(null);
		portaDelegata.setTipoSoggettoProprietario(null);
		portaDelegata.setNomeSoggettoProprietario(null);
		
		if(portaDelegata.getMessageSecurity()!=null) {
			if(portaDelegata.getMessageSecurity().getRequestFlow()!=null && portaDelegata.getMessageSecurity().getRequestFlow().sizeParameterList()<=0) {
				portaDelegata.getMessageSecurity().setRequestFlow(null); // altrimeni da errore di validazione in fase di import
			}
			if(portaDelegata.getMessageSecurity().getResponseFlow()!=null && portaDelegata.getMessageSecurity().getResponseFlow().sizeParameterList()<=0) {
				portaDelegata.getMessageSecurity().setResponseFlow(null); // altrimeni da errore di validazione in fase di import
			}
		}
		
		if(portaDelegata.getRuoli()!=null && portaDelegata.getRuoli().sizeRuoloList()<=0) {
			portaDelegata.setRuoli(null); // altrimeni da errore di validazione in fase di import
		}
		
		if(portaDelegata.getScope()!=null && portaDelegata.getScope().sizeScopeList()<=0) {
			portaDelegata.setScope(null); // altrimeni da errore di validazione in fase di import
		}
		
		if(portaDelegata.getCorrelazioneApplicativa()!=null && portaDelegata.getCorrelazioneApplicativa().sizeElementoList()<=0) {
			portaDelegata.setCorrelazioneApplicativa(null); // altrimeni da errore di validazione in fase di import
		}
		if(portaDelegata.getCorrelazioneApplicativaRisposta()!=null && portaDelegata.getCorrelazioneApplicativaRisposta().sizeElementoList()<=0) {
			portaDelegata.setCorrelazioneApplicativaRisposta(null); // altrimeni da errore di validazione in fase di import
		}
		
		// Fix altrimenti viene serializzato un xml vuoto di local forward, e quando viene re-importato il default e' abilitato
		if(portaDelegata.getLocalForward()!=null) {
			if(portaDelegata.getLocalForward().getStato()==null) {
				portaDelegata.setLocalForward(null);
			}
		}
	}

	public void clean(PortaApplicativa portaApplicativa){

		portaApplicativa.setIdSoggetto(null);
		portaApplicativa.setIdAccordo(null);
		portaApplicativa.setIdPortType(null);
		portaApplicativa.setStatoMessageSecurity(null);
		portaApplicativa.setTipoSoggettoProprietario(null);
		portaApplicativa.setNomeSoggettoProprietario(null);
	
		if(portaApplicativa.sizeServizioApplicativoList()>0) {
			for (PortaApplicativaServizioApplicativo pasa : portaApplicativa.getServizioApplicativoList()) {
				pasa.setIdServizioApplicativo(null);
			}
		}
		
		if(portaApplicativa.getMessageSecurity()!=null) {
			if(portaApplicativa.getMessageSecurity().getRequestFlow()!=null && portaApplicativa.getMessageSecurity().getRequestFlow().sizeParameterList()<=0) {
				portaApplicativa.getMessageSecurity().setRequestFlow(null); // altrimeni da errore di validazione in fase di import
			}
			if(portaApplicativa.getMessageSecurity().getResponseFlow()!=null && portaApplicativa.getMessageSecurity().getResponseFlow().sizeParameterList()<=0) {
				portaApplicativa.getMessageSecurity().setResponseFlow(null); // altrimeni da errore di validazione in fase di import
			}
		}
		
		if(portaApplicativa.getSoggetti()!=null && portaApplicativa.getSoggetti().sizeSoggettoList()<=0) {
			portaApplicativa.setSoggetti(null); // altrimeni da errore di validazione in fase di import
		}
		
		if(portaApplicativa.getRuoli()!=null && portaApplicativa.getRuoli().sizeRuoloList()<=0) {
			portaApplicativa.setRuoli(null); // altrimeni da errore di validazione in fase di import
		}
			
		if(portaApplicativa.getScope()!=null && portaApplicativa.getScope().sizeScopeList()<=0) {
			portaApplicativa.setScope(null); // altrimeni da errore di validazione in fase di import
		}
		
		if(portaApplicativa.getCorrelazioneApplicativa()!=null && portaApplicativa.getCorrelazioneApplicativa().sizeElementoList()<=0) {
			portaApplicativa.setCorrelazioneApplicativa(null); // altrimeni da errore di validazione in fase di import
		}
		if(portaApplicativa.getCorrelazioneApplicativaRisposta()!=null && portaApplicativa.getCorrelazioneApplicativaRisposta().sizeElementoList()<=0) {
			portaApplicativa.setCorrelazioneApplicativaRisposta(null); // altrimeni da errore di validazione in fase di import
		}
	}

	

	
	// ---- UTILITY
	
	private void clean(org.openspcoop2.core.config.Connettore connettoreConfigurazione){
		connettoreConfigurazione.setNomeRegistro(null);
		connettoreConfigurazione.setTipoDestinatarioTrasmissioneBusta(null);
		connettoreConfigurazione.setNomeDestinatarioTrasmissioneBusta(null);
		connettoreConfigurazione.setCustom(null);
	}
	
	private void clean(org.openspcoop2.core.config.GestioneErrore gestioneErrore){
		gestioneErrore.setNome(null);
	}
	
	private void clean(ProtocolProperty pp){
		pp.setByteFile(null);
		pp.setIdProprietario(null);
		pp.setTipoProprietario(null);
	}
	
}
