/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.autenticazione.pd;

import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.credenziali.Credenziali;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;

/**
 * Classe che implementa una autenticazione BASIC.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutenticazioneSsl extends AbstractAutenticazioneBase {

    @Override
    public EsitoAutenticazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException{

    	EsitoAutenticazionePortaDelegata esito = new EsitoAutenticazionePortaDelegata();
    	
    	IDSoggetto soggettoFruitore = null;
    	if(datiInvocazione!=null && datiInvocazione.getPd()!=null) {
    		soggettoFruitore = new IDSoggetto(datiInvocazione.getPd().getTipoSoggettoProprietario(), datiInvocazione.getPd().getNomeSoggettoProprietario());
    	}
    	    	
    	Credenziali credenziali = datiInvocazione.getInfoConnettoreIngresso().getCredenziali();
		
    	String subject = credenziali.getSubject();

		// Controllo credenziali fornite
    	if( subject==null || "".equals(subject) ){
			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl("credenziali non fornite",subject));
			esito.setClientAuthenticated(false);
			esito.setClientIdentified(false);
			return esito;
		}
    	
    	// Essendoci l'identita' del chiamante, il client e' stato autenticato o da un frontend o dall'application server stesso
    	esito.setClientAuthenticated(true);
    	esito.setCredential(subject);
		
    	// Provo a identificare il chiamante rispetto ad una entita' del registro
		IDServizioApplicativo idServizioApplicativo = null;
		try{
			idServizioApplicativo = ConfigurazionePdDManager.getInstance(datiInvocazione.getState()).
						getIdServizioApplicativoByCredenzialiSsl(subject);
			if(idServizioApplicativo!=null && soggettoFruitore==null) {
				soggettoFruitore = idServizioApplicativo.getIdSoggettoProprietario();
			}
		}catch(Exception e){
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("AutenticazioneSsl non riuscita",e);
			
			esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setClientIdentified(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		if(idServizioApplicativo == null){
			// L'identificazione in ssl non e' obbligatoria
			//esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl("credenziali fornite non corrette",subject));
			esito.setClientIdentified(false);
		}
		else if(idServizioApplicativo.getIdSoggettoProprietario().equals(soggettoFruitore)==false) {
			// L'identificazione in ssl non e' obbligatoria
			//esito.setErroreIntegrazione(ErroriIntegrazione.ERRORE_402_AUTENTICAZIONE_FALLITA.getErrore402_AutenticazioneFallitaSsl("soggetto proprietario dell'applicativo identificato differente dal soggetto proprietario della porta invocata",subject));
			esito.setClientIdentified(false);
			return esito;
		}
		else {
			esito.setClientIdentified(true);
			esito.setIdServizioApplicativo(idServizioApplicativo);
		}
		
		return esito;
		
    }

}

