/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.protocol.sdk.validator;

import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.IProtocolFactory;


/**
 * Validatore degli accordi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public interface IValidazioneAccordi {

	/**
	 * Recupera l'implementazione della factory per il protocollo in uso
	 * @return protocolFactory in uso.
	 */
	public IProtocolFactory getProtocolFactory();

	/**
	 * Effettua la validazione di un accordo di servizio parte comune
	 * 
	 * @param accordoServizioParteComune accordo di servizio parte comune
	 * @return esito
	 */
	public ValidazioneResult valida(AccordoServizioParteComune accordoServizioParteComune);
	
	/**
	 * Effettua la validazione di un accordo di servizio parte specifica
	 * 
	 * @param accordoServizioParteSpecifica accordo di servizio parte specifica
	 * @return esito
	 */
	public ValidazioneResult valida(AccordoServizioParteSpecifica accordoServizioParteSpecifica);
	
	/**
	 * Effettua la validazione di un accordo di cooperazione
	 * 
	 * @param accordoCooperazione accordo di cooperazione
	 * @return esito
	 */
	public ValidazioneResult valida(AccordoCooperazione accordoCooperazione);
	
}
