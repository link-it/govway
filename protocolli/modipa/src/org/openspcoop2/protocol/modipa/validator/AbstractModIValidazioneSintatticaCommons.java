/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.modipa.validator;

import java.security.cert.X509Certificate;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.validator.ValidazioneUtils;
import org.slf4j.Logger;

/**
 * ModIValidazioneSintatticaSoap
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AbstractModIValidazioneSintatticaCommons {

	protected Logger log;
	protected ModIProperties modiProperties;
	protected ValidazioneUtils validazioneUtils;
	protected IState state;
	protected Context context;
	protected IProtocolFactory<?> factory;
	protected RequestInfo requestInfo;
	public AbstractModIValidazioneSintatticaCommons(Logger log, IState state, Context context,  IProtocolFactory<?> factory, RequestInfo requestInfo,
			ModIProperties modiProperties, ValidazioneUtils validazioneUtils) {
		this.log = log;
		this.state = state;
		this.context = context;
		this.requestInfo = requestInfo;
		this.factory = factory;
		this.modiProperties = modiProperties;
		this.validazioneUtils = validazioneUtils;
	}
	
	protected void identificazioneApplicativoMittente(X509Certificate x509, OpenSPCoop2Message msg, Busta busta) throws Exception {
		// invocato in ModIValidazioneSintatticaRest e ModIValidazioneSintatticaSoap durante il trattamento del token di sicurezza
		IdentificazioneApplicativoMittenteUtils.identificazioneApplicativoMittenteByX509(this.log, this.state, x509, msg, busta, this.context, this.factory, this.requestInfo);
	}
		
}
