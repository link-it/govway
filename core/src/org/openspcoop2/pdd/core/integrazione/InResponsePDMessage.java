/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.integrazione;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * InResponsePDMessage
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InResponsePDMessage {

	private OpenSPCoop2Message message;
	private Map<String, List<String>> headers;
	private PortaDelegata portaDelegata;
	private IDSoggetto soggettoMittente;
	private IDServizio servizio;
	private Busta bustaRichiesta;
	
	public Busta getBustaRichiesta() {
		return this.bustaRichiesta;
	}
	public void setBustaRichiesta(Busta busta) {
		this.bustaRichiesta = busta;
	}
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	public Map<String, List<String>> getHeaders() {
		return this.headers;
	}
	public void setHeaders(Map<String, List<String>> proprietaTrasporto) {
		this.headers = proprietaTrasporto;
	}
	public PortaDelegata getPortaDelegata() {
		return this.portaDelegata;
	}
	public void setPortaDelegata(PortaDelegata portaDelegata) {
		this.portaDelegata = portaDelegata;
	}
	public IDSoggetto getSoggettoMittente() {
		return this.soggettoMittente;
	}
	public void setSoggettoMittente(IDSoggetto soggettoMittente) {
		this.soggettoMittente = soggettoMittente;
	}
	public IDServizio getServizio() {
		return this.servizio;
	}
	public void setServizio(IDServizio servizio) {
		this.servizio = servizio;
	}
	
}
