package org.openspcoop2.core.config.utils;

import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;

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
