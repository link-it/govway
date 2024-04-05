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

package org.openspcoop2.pdd.services.connector;

import java.util.List;
import java.util.Map;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;

/**
 * ConnectorDispatcherInfo
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnectorDispatcherInfo {

	public static ConnectorDispatcherInfo getGeneric(OpenSPCoop2Message message,int status, String contentType, Map<String, List<String>> trasporto,EsitoTransazione esito) throws Exception {
		ConnectorDispatcherInfo c = new ConnectorDispatcherInfo();
		c.setMessage(message);
		c.setEsitoTransazione(esito);
		c.setStatus(status);
		c.setContentType(contentType);
		c.setTrasporto(trasporto);
		return c;
	}

	protected ConnectorDispatcherInfo() {
		
	}

	private OpenSPCoop2Message message;
	private EsitoTransazione esitoTransazione;
	private int status;
	private String contentType;
	private Map<String, List<String>> trasporto;
	
	public Map<String, List<String>> getTrasporto() {
		return this.trasporto;
	}
	public void setTrasporto(Map<String, List<String>> trasporto) {
		this.trasporto = trasporto;
	}
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	public EsitoTransazione getEsitoTransazione() {
		return this.esitoTransazione;
	}
	public void setEsitoTransazione(EsitoTransazione esitoTransazione) {
		this.esitoTransazione = esitoTransazione;
	}
	public int getStatus() {
		return this.status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getContentType() {
		return this.contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
