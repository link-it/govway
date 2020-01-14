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

package org.openspcoop2.protocol.modipa.validator;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.protocol.modipa.config.ModIProperties;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * ModIRESTSecurity
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ModIRESTSecurity {

	private String tokenHeaderName;
	private boolean cleanDigest;
	private RuoloMessaggio ruoloMessaggio;
	
	public ModIRESTSecurity(String tokenHeaderName, boolean request) throws ProtocolException, Exception {
		this.tokenHeaderName = tokenHeaderName;
		this.ruoloMessaggio =  request ? RuoloMessaggio.RICHIESTA : RuoloMessaggio.RISPOSTA;
		switch (this.ruoloMessaggio) {
		case RICHIESTA:
			this.cleanDigest = ModIProperties.getInstance().isRestSecurityTokenRequestDigestClean();
			break;
		case RISPOSTA:
			this.cleanDigest = ModIProperties.getInstance().isRestSecurityTokenResponseDigestClean();
			break;
		}
	}
	
	public RuoloMessaggio getRuoloMessaggio() {
		return this.ruoloMessaggio;
	}

	public void setRuoloMessaggio(RuoloMessaggio ruoloMessaggio) {
		this.ruoloMessaggio = ruoloMessaggio;
	}

	public String getTokenHeaderName() {
		return this.tokenHeaderName;
	}

	public void setTokenHeaderName(String tokenHeaderName) {
		this.tokenHeaderName = tokenHeaderName;
	}

	public boolean isCleanDigest() {
		return this.cleanDigest;
	}

	public void setCleanDigest(boolean cleanDigest) {
		this.cleanDigest = cleanDigest;
	}
	
	
	public void clean(OpenSPCoop2Message msg) throws SecurityException, MessageException, MessageNotSupportedException {
		
		if(RuoloMessaggio.RICHIESTA.equals(this.ruoloMessaggio)) {
			if(msg!=null && msg.getTransportRequestContext()!=null) {
				msg.getTransportRequestContext().removeParameterTrasporto(this.tokenHeaderName);
				if(this.cleanDigest) {
					msg.getTransportRequestContext().removeParameterTrasporto(HttpConstants.DIGEST);
				}
			}
		}
		else {
			if(msg!=null && msg.getTransportResponseContext()!=null) {
				msg.getTransportResponseContext().removeParameterTrasporto(this.tokenHeaderName);
				if(this.cleanDigest) {
					msg.getTransportResponseContext().removeParameterTrasporto(HttpConstants.DIGEST);
				}
			}
		}

	}
	
}
