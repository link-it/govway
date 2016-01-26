/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.pdd.core.integrazione;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.core.id.IDServizio;

/**
 * OutRequestPAMessage
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OutRequestPAMessage {

	private OpenSPCoop2Message message;
	private java.util.Properties proprietaTrasporto;
	private java.util.Properties proprietaUrlBased;
	private PortaApplicativa portaApplicativa;
	private PortaDelegata portaDelegata; // presente al posto della PA in caso di SCENARIO_ASINCRONO_SIMMETRICO_CONSEGNA_RISPOSTA
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
	public java.util.Properties getProprietaTrasporto() {
		return this.proprietaTrasporto;
	}
	public void setProprietaTrasporto(java.util.Properties proprietaTrasporto) {
		this.proprietaTrasporto = proprietaTrasporto;
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
	public PortaApplicativa getPortaApplicativa() {
		return this.portaApplicativa;
	}
	public void setPortaApplicativa(PortaApplicativa portaApplicativa) {
		this.portaApplicativa = portaApplicativa;
	}
	public PortaDelegata getPortaDelegata() {
		return this.portaDelegata;
	}
	public void setPortaDelegata(PortaDelegata portaDelegata) {
		this.portaDelegata = portaDelegata;
	}
	public java.util.Properties getProprietaUrlBased() {
		return this.proprietaUrlBased;
	}
	public void setProprietaUrlBased(java.util.Properties proprietaUrlBased) {
		this.proprietaUrlBased = proprietaUrlBased;
	}
	
}
