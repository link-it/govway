/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.basic.validator;

import java.util.List;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.basic.BasicStateComponentFactory;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * ValidazioneConSchema
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */

public class ValidazioneConSchema extends BasicStateComponentFactory implements
		org.openspcoop2.protocol.sdk.validator.IValidazioneConSchema {

	public ValidazioneConSchema(IProtocolFactory<?> factory,IState state) throws ProtocolException{
		super(factory, state);
	}

	@Override
	public List<Eccezione> getEccezioniValidazione() {
		return new java.util.ArrayList<Eccezione>();
	}

	@Override
	public List<Eccezione> getEccezioniProcessamento() {
		return new java.util.ArrayList<Eccezione>();
	}

	@Override
	public void valida(OpenSPCoop2Message message,
			boolean isErroreProcessamento,
			boolean isErroreIntestazione,
			boolean isMessaggioConAttachments, boolean validazioneManifestAttachments) throws ProtocolException {
		return;
	}

	@Override
	public boolean initialize() {
		return true;
	}

	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}

}
