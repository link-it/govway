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

package org.openspcoop2.protocol.basic.validator;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.basic.BasicStateComponentFactory;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.constants.RuoloBusta;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ValidazioneSemanticaResult;
import org.openspcoop2.utils.digest.IDigestReader;

/**
 * ValidazioneSemantica
 * 
 * @author Nardi Lorenzo (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ValidazioneSemantica extends BasicStateComponentFactory implements
		org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica {

	protected Context context;
	
	public ValidazioneSemantica(IProtocolFactory<?> factory,IState state) throws ProtocolException{
		super(factory,state);
	}

	@Override
	public void setContext(Context context) {
		this.context = context;
	}
	
	@Override
	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}

	@Override
	public boolean validazioneID(String id, IDSoggetto dominio,
			ProprietaValidazione proprietaValidazione) {
		return true;
	}

	@Override
	public ValidazioneSemanticaResult valida(OpenSPCoop2Message msg, Busta busta, 
			ProprietaValidazione tipoValidazione, RuoloBusta tipoBusta)
			throws ProtocolException {
		return new ValidazioneSemanticaResult(null, null, null, null, null, null);
	}

	@Override
	public SecurityInfo readSecurityInformation(IDigestReader digestReader, OpenSPCoop2Message msg) throws ProtocolException{
		return null;
	}
}
