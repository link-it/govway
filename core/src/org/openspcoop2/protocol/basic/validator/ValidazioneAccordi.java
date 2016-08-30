/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.protocol.basic.validator;

import org.slf4j.Logger;
import org.openspcoop2.core.registry.AccordoCooperazione;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.validator.IValidazioneAccordi;
import org.openspcoop2.protocol.sdk.validator.ValidazioneResult;
import org.openspcoop2.utils.xml.AbstractXMLUtils;

/**
 * ValidazioneAccordi
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidazioneAccordi implements IValidazioneAccordi{

	protected IProtocolFactory protocolFactory;
	protected Logger log;
	protected AbstractXMLUtils xmlUtils = null;

	public ValidazioneAccordi(IProtocolFactory factory){
		this.log = factory.getLogger();
		this.protocolFactory = factory;
	}

	@Override
	public IProtocolFactory getProtocolFactory() {
		return this.protocolFactory;
	}

	@Override
	public ValidazioneResult valida(
			AccordoServizioParteComune accordoServizioParteComune) {
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(true);
		return result;
	}

	@Override
	public ValidazioneResult valida(
			AccordoServizioParteSpecifica accordoServizioParteSpecifica) {
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(true);
		return result;
	}

	@Override
	public ValidazioneResult valida(
			AccordoCooperazione accordoCooperazione) {
		ValidazioneResult result = new ValidazioneResult();
		result.setEsito(true);
		return result;
	}
}
