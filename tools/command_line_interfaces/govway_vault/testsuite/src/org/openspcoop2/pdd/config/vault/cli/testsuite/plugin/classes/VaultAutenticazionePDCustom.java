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

import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.ParametriAutenticazione;
import org.openspcoop2.pdd.core.autenticazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autenticazione.pd.EsitoAutenticazionePortaDelegata;

/**
* AutenticazionePDCustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class VaultAutenticazionePDCustom extends org.openspcoop2.pdd.core.autenticazione.pd.AutenticazionePrincipal {

	private String valore;
	
	@Override
    public void initParametri(ParametriAutenticazione parametri) throws AutenticazioneException {
		super.initParametri(parametri);
		
		this.valore = parametri.get("vaultTestNomeCifratoAutenticazione");
	}
	
	@Override
	public EsitoAutenticazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException{
		
		if(this.valore==null) {
			throw new AutenticazioneException("Valore proprietà cifrata non presente");
		}
		if(!"vaultTestValoreCifratoAutenticazione".equals(this.valore)) {
			throw new AutenticazioneException("Valore proprietà cifrata presente '"+this.valore+"' differente da quello atteso 'vaultTestValoreCifratoAutenticazione'");
		}
		
		return super.process(datiInvocazione);
	}

	@Override
	public boolean saveAuthenticationResultInCache() {
		return false;
	}
}
