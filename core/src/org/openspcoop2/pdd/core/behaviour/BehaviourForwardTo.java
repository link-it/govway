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
package org.openspcoop2.pdd.core.behaviour;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * BehaviourForwardTo
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BehaviourForwardTo {

	private OpenSPCoop2Message message;
	private String description;
	private BehaviourForwardToFilter filter;
	private BehaviourForwardToConfiguration config;
	private Busta busta;
	
	public Busta getBusta() {
		return this.busta;
	}
	public void setBusta(Busta busta) {
		this.busta = busta;
	}
	public BehaviourForwardToConfiguration getConfig() {
		return this.config;
	}
	public void setConfig(BehaviourForwardToConfiguration config) {
		this.config = config;
	}
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BehaviourForwardToFilter getFilter() {
		return this.filter;
	}
	public void setFilter(BehaviourForwardToFilter filter) {
		this.filter = filter;
	}
	

}
