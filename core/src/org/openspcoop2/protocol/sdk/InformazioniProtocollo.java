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

/**
 * Classe utilizzata per rappresentare le informazioni su di un protocollo
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class InformazioniProtocollo implements java.io.Serializable{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String label;
	private String description;
	private String webSite;
	
	private boolean errorProtocol;
	private boolean envelopeErrorProtocol = true;
	private String labelErrorProtocol;
	private boolean externalFault;
	private String labelExternalFault;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getWebSite() {
		return this.webSite;
	}
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
	public boolean isErrorProtocol() {
		return this.errorProtocol;
	}
	public void setErrorProtocol(boolean errorProtocol) {
		this.errorProtocol = errorProtocol;
	}
	public boolean isEnvelopeErrorProtocol() {
		return this.envelopeErrorProtocol;
	}
	public void setEnvelopeErrorProtocol(boolean envelopeErrorProtocol) {
		this.envelopeErrorProtocol = envelopeErrorProtocol;
	}
	public String getLabelErrorProtocol() {
		return this.labelErrorProtocol;
	}
	public void setLabelErrorProtocol(String labelErrorProtocol) {
		this.labelErrorProtocol = labelErrorProtocol;
	}
	public boolean isExternalFault() {
		return this.externalFault;
	}
	public void setExternalFault(boolean externalFault) {
		this.externalFault = externalFault;
	}
	public String getLabelExternalFault() {
		return this.labelExternalFault;
	}
	public void setLabelExternalFault(String labelExternalFault) {
		this.labelExternalFault = labelExternalFault;
	}
}





