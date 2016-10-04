/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.core.config.utils;

import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
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
		
		while(servizioApplicativo.sizeRuoloList()>0){
			servizioApplicativo.removeRuolo(0);
		}
		
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
	}

	public void clean(PortaDelegata portaDelegata){

		portaDelegata.setIdSoggetto(null);
		portaDelegata.setIdAccordo(null);
		portaDelegata.setIdPortType(null);
		portaDelegata.setStatoMessageSecurity(null);
		portaDelegata.setTipoSoggettoProprietario(null);
		portaDelegata.setNomeSoggettoProprietario(null);
		
		if(portaDelegata.sizeServizioApplicativoList()>0){
			for (ServizioApplicativo servizioApplicativo : portaDelegata.getServizioApplicativoList()) {
				this.clean(servizioApplicativo);
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
		
		if(portaApplicativa.sizeServizioApplicativoList()>0){
			for (ServizioApplicativo servizioApplicativo : portaApplicativa.getServizioApplicativoList()) {
				this.clean(servizioApplicativo);
			}
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
	
}
