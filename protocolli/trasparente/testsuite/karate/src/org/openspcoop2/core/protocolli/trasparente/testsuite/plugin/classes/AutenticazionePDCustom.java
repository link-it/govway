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

import org.openspcoop2.pdd.core.autenticazione.AutenticazioneException;
import org.openspcoop2.pdd.core.autenticazione.pd.DatiInvocazionePortaDelegata;
import org.openspcoop2.pdd.core.autenticazione.pd.EsitoAutenticazionePortaDelegata;

/**
* AutenticazionePDCustom
*
* @author Andrea Poli (poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class AutenticazionePDCustom extends org.openspcoop2.pdd.core.autenticazione.pd.AutenticazionePrincipal {

	@Override
	public EsitoAutenticazionePortaDelegata process(DatiInvocazionePortaDelegata datiInvocazione) throws AutenticazioneException{
		
		try {
			String id = Utilities.readIdentificativoTest();
			String tipoTest = Utilities.readTipoTest(datiInvocazione.getPd().getProprietaAutenticazioneList());
			Utilities.writeIdentificativoTest(id, tipoTest);
		}catch(Throwable t) {
			throw new AutenticazioneException(t.getMessage(),t);
		}
		
		return super.process(datiInvocazione);
	}

}
