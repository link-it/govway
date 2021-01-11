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

package org.openspcoop2.protocol.sdk.registry;

import org.openspcoop2.protocol.sdk.properties.ProtocolProperties;

/**
 *  FiltroRicercaPortTypeAzioni
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltroRicercaPortTypeAzioni extends FiltroRicercaPortType {

	private String nomeAzione;
	private ProtocolProperties protocolPropertiesAzione;

	public String getNomeAzione() {
		return this.nomeAzione;
	}
	public void setNomeAzione(String nomeAzione) {
		this.nomeAzione = nomeAzione;
	}
	public ProtocolProperties getProtocolPropertiesAzione() {
		return this.protocolPropertiesAzione;
	}
	public void setProtocolPropertiesAzione(ProtocolProperties protocolPropertiesAzione) {
		this.protocolPropertiesAzione = protocolPropertiesAzione;
	}


}
