/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.sdk.transaction;

import org.openspcoop2.monitor.sdk.constants.ContentResourceNames;
import org.openspcoop2.monitor.sdk.constants.MessageType;

/**
 * SOAPEnvelopeResource
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SOAPEnvelopeResource extends AbstractContentResource {
	
	public SOAPEnvelopeResource(MessageType messageType) {
		super(messageType);
	}

	@Override
	public String getName() {
		if (this.isRequest())
			return ContentResourceNames.REQ_SOAP_ENVELOPE;
		else
			return ContentResourceNames.RES_SOAP_ENVELOPE;
	}
	
	@Override
	public String getValue() {
		return this.soapEnvelope;
	}
	
	private String soapEnvelope = "";
}
