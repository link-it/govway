/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk;

import org.openspcoop2.message.OpenSPCoop2Message;

/**
 * ProtocolMessage
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProtocolMessage {

	private BustaRawContent<?> bustaRawContent;
	private boolean useBustaRawContentReadByValidation = false;
	private OpenSPCoop2Message message;
	private boolean phaseUnsupported = false;
	
	public boolean isPhaseUnsupported() {
		return this.phaseUnsupported;
	}
	public void setPhaseUnsupported(boolean phaseUnsupported) {
		this.phaseUnsupported = phaseUnsupported;
	}
	public BustaRawContent<?> getBustaRawContent() {
		return this.bustaRawContent;
	}
	public void setBustaRawContent(BustaRawContent<?> bustaRawContent) {
		this.bustaRawContent = bustaRawContent;
	}
	public boolean isUseBustaRawContentReadByValidation() {
		return this.useBustaRawContentReadByValidation;
	}
	public void setUseBustaRawContentReadByValidation(boolean useBustaRawContentReadByValidation) {
		this.useBustaRawContentReadByValidation = useBustaRawContentReadByValidation;
	}
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
}
