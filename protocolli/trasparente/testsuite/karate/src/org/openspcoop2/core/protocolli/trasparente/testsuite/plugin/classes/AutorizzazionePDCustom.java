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

package org.openspcoop2.core.protocolli.trasparente.testsuite.plugin.classes;

import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.pdd.core.autorizzazione.AutorizzazioneException;
import org.openspcoop2.pdd.core.autorizzazione.pd.EsitoAutorizzazionePortaDelegata;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;

/**
* AutorizzazionePACustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AutorizzazionePDCustom extends org.openspcoop2.pdd.core.autorizzazione.pd.AbstractAutorizzazioneBase {

	@Override
	public EsitoAutorizzazionePortaDelegata process(
			org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata datiInvocazione)
			throws AutorizzazioneException {
		
		EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
		
		String applicativoAtteso = null;
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = Utilities.readTipoTest(datiInvocazione.getPd().getProprietaAutorizzazioneList());
			Utilities.writeIdentificativoTest(id, tipoTest);
			applicativoAtteso = Utilities.readNomeApplicativo(datiInvocazione.getPd().getProprietaAutorizzazioneList());
		}catch(Exception e) {
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione non riuscita",e);
    		esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    				get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setAutorizzato(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		IDServizioApplicativo idSA = datiInvocazione.getIdServizioApplicativo();
		String identitaServizioApplicativoFruitore = null;
		if(idSA!=null){
			identitaServizioApplicativoFruitore = idSA.getNome();
		}
				
		if(identitaServizioApplicativoFruitore==null || !applicativoAtteso.equals(identitaServizioApplicativoFruitore)) {
			esito.setErroreIntegrazione(IntegrationFunctionError.AUTHORIZATION_DENY, ErroriIntegrazione.ERRORE_404_AUTORIZZAZIONE_FALLITA_SA.
					getErrore404_AutorizzazioneFallitaServizioApplicativo(identitaServizioApplicativoFruitore));
			esito.setAutorizzato(false);
		}
		else {
			esito.setAutorizzato(true);
		}
		
		return esito;
	}




}
