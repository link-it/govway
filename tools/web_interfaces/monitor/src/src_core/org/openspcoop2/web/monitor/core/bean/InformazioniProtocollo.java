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
package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;

/****
 * InformazioniProtocollo
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class InformazioniProtocollo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String descrizioneProtocollo = null;
	private String webSiteProtocollo = null;
	private String labelProtocollo = null;
	
	public String getDescrizioneProtocollo() {
		return this.descrizioneProtocollo;
	}
	public void setDescrizioneProtocollo(String descrizioneProtocollo) {
		this.descrizioneProtocollo = descrizioneProtocollo;
	}
	public String getWebSiteProtocollo() {
		return this.webSiteProtocollo;
	}
	public void setWebSiteProtocollo(String webSiteProtocollo) {
		this.webSiteProtocollo = webSiteProtocollo;
	}
	public String getLabelProtocollo() {
		return this.labelProtocollo;
	}
	public void setLabelProtocollo(String labelProtocollo) {
		this.labelProtocollo = labelProtocollo;
	} 

}
