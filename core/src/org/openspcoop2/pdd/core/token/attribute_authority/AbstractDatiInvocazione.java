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

package org.openspcoop2.pdd.core.token.attribute_authority;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreIngresso;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**
 * AbstractDatiInvocazione
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractDatiInvocazione {

	private InfoConnettoreIngresso infoConnettoreIngresso;
	
	private IState state;
	
	private String idModulo;
	
	private OpenSPCoop2Message message;
	
	private Busta busta;
	
	private RequestInfo requestInfo;
	
	private PolicyAttributeAuthority policyAttributeAuthority;
	
	public PolicyAttributeAuthority getPolicyAttributeAuthority() {
		return this.policyAttributeAuthority;
	}
	public void setPolicyAttributeAuthority(PolicyAttributeAuthority policyAttributeAuthority) {
		this.policyAttributeAuthority = policyAttributeAuthority;
	}
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	public Busta getBusta() {
		return this.busta;
	}
	public void setBusta(Busta busta) {
		this.busta = busta;
	}
	public InfoConnettoreIngresso getInfoConnettoreIngresso() {
		return this.infoConnettoreIngresso;
	}
	public void setInfoConnettoreIngresso(
			InfoConnettoreIngresso infoConnettoreIngresso) {
		this.infoConnettoreIngresso = infoConnettoreIngresso;
	}

	public IState getState() {
		return this.state;
	}
	public void setState(IState state) {
		this.state = state;
	}
	
	public String getIdModulo() {
		return this.idModulo;
	}
	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}

	public RequestInfo getRequestInfo() {
		return this.requestInfo;
	}
	public void setRequestInfo(RequestInfo requestInfo) {
		this.requestInfo = requestInfo;
	}
}
