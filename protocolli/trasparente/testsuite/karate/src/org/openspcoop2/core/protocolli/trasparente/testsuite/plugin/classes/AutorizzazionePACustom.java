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

package org.openspcoop2.core.protocolli.trasparente.testsuite.plugin.classes;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.autorizzazione.pa.AbstractAutorizzazioneBase;
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
public class AutorizzazionePACustom extends org.openspcoop2.pdd.core.autorizzazione.pa.AbstractAutorizzazioneBase {

	@Override
	public EsitoAutorizzazionePortaApplicativa process(DatiInvocazionePortaApplicativa datiInvocazione) {
		
		EsitoAutorizzazionePortaApplicativa esito = new EsitoAutorizzazionePortaApplicativa();
		
		String applicativoAtteso = null;
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = Utilities.readTipoTest(datiInvocazione.getPa().getProprietaAutorizzazioneList());
			Utilities.writeIdentificativoTest(id, tipoTest);
			applicativoAtteso = Utilities.readNomeApplicativo(datiInvocazione.getPa().getProprietaAutorizzazioneList());
		}catch(Exception e) {
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione non riuscita",e);
    		String errore = "Errore durante il processo di autorizzazione: "+e.getMessage();
    		esito.setErroreCooperazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA));
    		esito.setAutorizzato(false);
    		esito.setEccezioneProcessamento(e);
		}
		
		IDServizioApplicativo idSA = datiInvocazione.getIdentitaServizioApplicativoFruitore();
		String identitaServizioApplicativoFruitore = null;
		if(idSA!=null){
			identitaServizioApplicativoFruitore = idSA.getNome();
		}
		
		IDSoggetto idSoggetto = datiInvocazione.getIdSoggettoFruitore();
		IDServizio idServizio = datiInvocazione.getIdServizio();
		
		if(identitaServizioApplicativoFruitore==null || !applicativoAtteso.equals(identitaServizioApplicativoFruitore)) {
			String errore = AbstractAutorizzazioneBase.getErrorString(idSA, idSoggetto, idServizio);
			esito.setErroreCooperazione(IntegrationFunctionError.AUTHORIZATION_DENY, ErroriCooperazione.AUTORIZZAZIONE_FALLITA.getErroreAutorizzazione(errore, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
			esito.setAutorizzato(false);
		}
		else {
			esito.setAutorizzato(true);
		}
		
		return esito;
	}

}
