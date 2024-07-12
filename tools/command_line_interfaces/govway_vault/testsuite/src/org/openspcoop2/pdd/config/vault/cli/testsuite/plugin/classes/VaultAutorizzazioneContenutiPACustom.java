/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.config.vault.cli.testsuite.plugin.classes;

import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.Proprieta;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.core.autorizzazione.pa.DatiInvocazionePortaApplicativa;
import org.openspcoop2.pdd.core.autorizzazione.pa.EsitoAutorizzazionePortaApplicativa;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**
* AutorizzazionePACustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class VaultAutorizzazioneContenutiPACustom extends org.openspcoop2.pdd.core.autorizzazione.pa.AutorizzazioneContenutoBuiltIn {

	private String valore;
	
	@Override
	public EsitoAutorizzazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione, OpenSPCoop2Message msg) throws AutorizzazioneException {
		
		try {
			if(datiInvocazione==null || datiInvocazione.getPa()==null || datiInvocazione.getPa().sizeProprietaAutorizzazione()<=0) {
				throw new CoreException("Valore proprietà cifrata non presente");
			}
			for (int i = 0; i < datiInvocazione.getPa().sizeProprietaAutorizzazioneContenuto(); i++) {
				Proprieta prop = datiInvocazione.getPa().getProprietaAutorizzazioneContenuto(i);
				if("vaultTestNomeCifratoAutorizzazioneContenuti".equals(prop.getNome())) {
					this.valore = prop.getValore();
				}
			}
			
			if(this.valore==null) {
				throw new CoreException("Valore proprietà cifrata non presente");
			}
			if(!"vaultTestValoreCifratoAutorizzazioneContenuti".equals(this.valore)) {
				throw new CoreException("Valore proprietà cifrata presente '"+this.valore+"' differente da quello atteso 'vaultTestValoreCifratoAutorizzazioneContenuti'");
			}

		}catch(Exception e) {
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione non riuscita",e);
    		String errore = "Errore durante il processo di autorizzazione: "+e.getMessage();
    		EsitoAutorizzazionePortaApplicativa esito = new EsitoAutorizzazionePortaApplicativa();
    		esito.setErroreCooperazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
    		return esito;
		}
		
		EsitoAutorizzazionePortaApplicativa esito = new EsitoAutorizzazionePortaApplicativa();
		esito.setAutorizzato(true);
		return esito;
		
	}

}
