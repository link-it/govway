/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.logger.info;

import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;

/**
 * DatiEsitoTransazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DatiEsitoTransazione {

	private Integer esito;
	private String protocollo;
	private IProtocolFactory<?> protocolFactory;
	
	private java.lang.String faultIntegrazione;
	private java.lang.String formatoFaultIntegrazione;

	private java.lang.String faultCooperazione;
	private java.lang.String formatoFaultCooperazione;
	
	private org.openspcoop2.core.transazioni.constants.PddRuolo pddRuolo;

	public Integer getEsito() {
		return this.esito;
	}
	public void setEsito(Integer esito) {
		this.esito = esito;
	}
	public String getProtocollo() {
		return this.protocollo;
	}
	public IProtocolFactory<?> getProtocolFactory() throws ProtocolException {
		if(this.protocolFactory!=null) {
			return this.protocolFactory;
		}
		else if(this.protocollo!=null){
			return ProtocolFactoryManager.getInstance().getProtocolFactoryByName(this.protocollo);
		}
		return null;
	}
	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}
	public void setProtocollo(IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;
		this.protocollo = this.protocolFactory.getProtocol();
	}
	public org.openspcoop2.core.transazioni.constants.PddRuolo getPddRuolo() {
		return this.pddRuolo;
	}
	public void setPddRuolo(org.openspcoop2.core.transazioni.constants.PddRuolo pddRuolo) {
		this.pddRuolo = pddRuolo;
	}
	public java.lang.String getFaultIntegrazione() {
		return this.faultIntegrazione;
	}
	public void setFaultIntegrazione(java.lang.String faultIntegrazione) {
		this.faultIntegrazione = faultIntegrazione;
	}
	public java.lang.String getFormatoFaultIntegrazione() {
		return this.formatoFaultIntegrazione;
	}
	public void setFormatoFaultIntegrazione(java.lang.String formatoFaultIntegrazione) {
		this.formatoFaultIntegrazione = formatoFaultIntegrazione;
	}
	public java.lang.String getFaultCooperazione() {
		return this.faultCooperazione;
	}
	public void setFaultCooperazione(java.lang.String faultCooperazione) {
		this.faultCooperazione = faultCooperazione;
	}
	public java.lang.String getFormatoFaultCooperazione() {
		return this.formatoFaultCooperazione;
	}
	public void setFormatoFaultCooperazione(java.lang.String formatoFaultCooperazione) {
		this.formatoFaultCooperazione = formatoFaultCooperazione;
	}
	
}
