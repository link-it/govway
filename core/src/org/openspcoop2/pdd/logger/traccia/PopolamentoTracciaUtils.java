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

package org.openspcoop2.pdd.logger.traccia;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.transazioni.Transazione;

/**     
 * PopolamentoTracciaUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PopolamentoTracciaUtils {

	private PopolamentoTracciaUtils() {}

	public static IDSoggetto getIdSoggettoMittente(Transazione transazioneDTO, boolean richiesta) {

		if(richiesta) {
			return new IDSoggetto(transazioneDTO.getTipoSoggettoFruitore(), transazioneDTO.getNomeSoggettoFruitore(), transazioneDTO.getIdportaSoggettoFruitore());
		} else {
			return new IDSoggetto(transazioneDTO.getTipoSoggettoErogatore(), transazioneDTO.getNomeSoggettoErogatore(), transazioneDTO.getIdportaSoggettoErogatore());
		}
		
	}
	
	public static IDSoggetto getIdSoggettoDestinatario(Transazione transazioneDTO, boolean richiesta) {

		if(richiesta) {
			return new IDSoggetto(transazioneDTO.getTipoSoggettoErogatore(), transazioneDTO.getNomeSoggettoErogatore(), transazioneDTO.getIdportaSoggettoErogatore());
		} else {
			return new IDSoggetto(transazioneDTO.getTipoSoggettoFruitore(), transazioneDTO.getNomeSoggettoFruitore(), transazioneDTO.getIdportaSoggettoFruitore());
		}

	}

}
