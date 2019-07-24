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


package org.openspcoop2.pdd.core.autorizzazione.pa;

import java.util.List;

import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.AbstractCore;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.core.autorizzazione.GestoreAutorizzazioneContenutiBuiltIn;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;

/**
 * Esempio di AutorizzazioneContenutoBuiltIn
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneContenutoBuiltIn extends AbstractCore implements IAutorizzazioneContenutoPortaApplicativa {

	@Override
	public EsitoAutorizzazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione, OpenSPCoop2Message msg) throws AutorizzazioneException {
		
		EsitoAutorizzazionePortaApplicativa esito = new EsitoAutorizzazionePortaApplicativa();
    	
    	// Autorizzazzione servizio applicativo
    	try{
    		
    		if(datiInvocazione.getPa()==null) {
				throw new Exception("Porta Applicativa non presente");
			}
    		List<Proprieta> regole = datiInvocazione.getPa().getProprietaAutorizzazioneContenutoList();
    		
    		GestoreAutorizzazioneContenutiBuiltIn gestore = new GestoreAutorizzazioneContenutiBuiltIn();
    		gestore.process(msg, datiInvocazione, this.getPddContext(), regole);
    		
    		if(!gestore.isAutorizzato()) {
    			
    			IDServizioApplicativo idSA = datiInvocazione.getIdentitaServizioApplicativoFruitore();
        		IDSoggetto soggettoFruitore = datiInvocazione.getIdSoggettoFruitore();
        		//IDServizio servizio = datiInvocazione.getIdServizio();
    			
        		String mittente = null;
        		if(idSA!=null && idSA.getNome()!=null) {
        			IDSoggetto soggettoProprietario = null;
        			if(idSA.getIdSoggettoProprietario()!=null) {
        				soggettoProprietario = idSA.getIdSoggettoProprietario();
        			}
        			else if(soggettoFruitore!=null) {
        				soggettoProprietario = soggettoFruitore;
        			}
        			if(soggettoProprietario!=null) {
        				mittente = "L'applicativo "+idSA.getNome()+" ("+soggettoProprietario.getTipo()+"/"+soggettoProprietario.getNome()+")";
        			}
        			else {
        				mittente = "L'applicativo "+idSA.getNome();
        			}
        		}
        		else if(soggettoFruitore!=null) {
        			mittente = "Il soggetto "+soggettoFruitore.getTipo()+"/"+soggettoFruitore.getNome(); 
        		}
        		else {
        			mittente = "Il chiamante";
        		}
        		
        		String errore = mittente+" non è autorizzato ad invocare l'API";
    			
        		esito.setErroreCooperazione(ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
    			esito.setAutorizzato(false);
    			esito.setDetails(gestore.getErrorMessage());
    		}
    		else {
    			esito.setAutorizzato(true);
    		}
			
        	return esito;
    		
    	}catch(Exception e){
    		esito.setEccezioneProcessamento(e);
    		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione per contenuto non riuscita",e);
    		throw new AutorizzazioneException("Errore inatteso durante la gestione dell'autorizzazione per contenuti: "+e.getMessage(),e);
    	}
	}

	
	
}
