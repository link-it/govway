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
public class VaultAutorizzazionePDCustom extends org.openspcoop2.pdd.core.autorizzazione.pd.AbstractAutorizzazioneBase {

	private String valore;
	
	@Override
	public EsitoAutorizzazionePortaDelegata process(
			org.openspcoop2.pdd.core.autorizzazione.pd.DatiInvocazionePortaDelegata datiInvocazione)
			throws AutorizzazioneException {
		
		try {
			if(datiInvocazione==null || datiInvocazione.getPd()==null || datiInvocazione.getPd().sizeProprietaAutorizzazione()<=0) {
				throw new CoreException("Valore proprietà cifrata non presente");
			}
			for (int i = 0; i < datiInvocazione.getPd().sizeProprietaAutorizzazione(); i++) {
				Proprieta prop = datiInvocazione.getPd().getProprietaAutorizzazione(i);
				if("vaultTestNomeCifratoAutorizzazione".equals(prop.getNome())) {
					this.valore = prop.getValore();
				}
			}
			
			if(this.valore==null) {
				throw new CoreException("Valore proprietà cifrata non presente");
			}
			if(!"vaultTestValoreCifratoAutorizzazione".equals(this.valore)) {
				throw new CoreException("Valore proprietà cifrata presente '"+this.valore+"' differente da quello atteso 'vaultTestValoreCifratoAutorizzazione'");
			}
		}catch(Exception e) {
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Autorizzazione non riuscita",e);
			EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
			
			esito.setErroreIntegrazione(IntegrationFunctionError.INTERNAL_REQUEST_ERROR, ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
    				get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE));
			esito.setAutorizzato(false);
			esito.setEccezioneProcessamento(e);
			return esito;
		}
		
		EsitoAutorizzazionePortaDelegata esito = new EsitoAutorizzazionePortaDelegata();
		esito.setAutorizzato(true);
		return esito;
	}

	@Override
	public boolean saveAuthorizationResultInCache() {
		return false;
	}



}
