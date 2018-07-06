/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

import java.util.Properties;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;

/**
 * ConnectorDispatcherErrorInfo
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 14183 $, $Date: 2018-06-24 12:22:25 +0200 (Sun, 24 Jun 2018) $
 */
public class ConnectorDispatcherErrorInfo {

	public static ConnectorDispatcherErrorInfo getGenericError(OpenSPCoop2Message errorMessage,int status, String contentType, Properties trasporto,
			RequestInfo requestInfo,IProtocolFactory<?> protocolFactory) throws Exception {
		ConnectorDispatcherErrorInfo c = new ConnectorDispatcherErrorInfo();
		EsitoTransazione esito = protocolFactory.createEsitoBuilder().getEsito(requestInfo.getProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
		c.setErrorMessage(errorMessage);
		c.setEsitoTransazione(esito);
		c.setStatus(status);
		c.setContentType(contentType);
		c.setTrasporto(trasporto);
		return c;
	}
	public static ConnectorDispatcherErrorInfo getClientError(OpenSPCoop2Message errorMessage,int status, String contentType, Properties trasporto,
			RequestInfo requestInfo,IProtocolFactory<?> protocolFactory) throws Exception {
		ConnectorDispatcherErrorInfo c = new ConnectorDispatcherErrorInfo();
		EsitoTransazione esito = protocolFactory.createEsitoBuilder().getEsito(requestInfo.getProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_4XX);
		c.setErrorMessage(errorMessage);
		c.setEsitoTransazione(esito);
		c.setStatus(status);
		c.setContentType(contentType);
		c.setTrasporto(trasporto);
		return c;
	}
	
	private ConnectorDispatcherErrorInfo() {
		
	}

	private OpenSPCoop2Message errorMessage;
	private EsitoTransazione esitoTransazione;
	private int status;
	private String contentType;
	private Properties trasporto;
	
	public Properties getTrasporto() {
		return this.trasporto;
	}
	public void setTrasporto(Properties trasporto) {
		this.trasporto = trasporto;
	}
	public OpenSPCoop2Message getErrorMessage() {
		return this.errorMessage;
	}
	public void setErrorMessage(OpenSPCoop2Message errorMessage) {
		this.errorMessage = errorMessage;
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
