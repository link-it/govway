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

package org.openspcoop2.protocol.sdk;

import org.openspcoop2.message.SOAPFaultCode;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;


/**
 * EccezioneIntegrazioneBuilderParameters
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EccezioneIntegrazioneBuilderParameters extends AbstractEccezioneBuilderParameter {

	private ErroreIntegrazione erroreIntegrazione;

	public ErroreIntegrazione getErroreIntegrazione() {
		return this.erroreIntegrazione;
	}

	public void setErroreIntegrazione(ErroreIntegrazione erroreIntegrazione) {
		this.erroreIntegrazione = erroreIntegrazione;
	}
	
	@Override
	public SOAPFaultCode getSoapFaultCode() {
		if(this.erroreIntegrazione.getSoapFaultCode() == null) { 
			if(this.getErroreIntegrazione().getCodiceErrore().getCodice() < 500) {
				return SOAPFaultCode.Sender;
			} else {
				return SOAPFaultCode.Receiver;
			}
		}
		return this.erroreIntegrazione.getSoapFaultCode();
	}

}
