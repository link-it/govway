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
package org.openspcoop2.pdd.services.connector.messages;

import java.util.Date;

import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.engine.RequestInfo;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.Credential;

/**
 * ConnectorInMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ConnectorInMessage {
	
	public IDService getIdModuloAsIDService();
	
	public String getIdModulo();

	public void updateRequestInfo(RequestInfo requestInfo) throws ConnectorException;
	public RequestInfo getRequestInfo();
	
	public Object getAttribute(String key) throws ConnectorException;
	
	public String getHeader(String key) throws ConnectorException;
	
	public String getParameter(String key) throws ConnectorException;
	
	public IProtocolFactory<?> getProtocolFactory() throws ConnectorException;
	
	public String getContentType() throws ConnectorException;
	
	public String getSOAPAction() throws ConnectorException;
	
	public OpenSPCoop2MessageParseResult getRequest(NotifierInputStreamParams notifierInputStreamParams) throws ConnectorException;
	
	public byte[] getRequest() throws ConnectorException;
	
	public Date getDataIngressoRichiesta();
	
	public URLProtocolContext getURLProtocolContext() throws ConnectorException;
	
	public Credential getCredential() throws ConnectorException;
		
	public String getSource() throws ConnectorException;
	
	public String getProtocol() throws ConnectorException;
	
	public int getContentLength() throws ConnectorException;
	
	public String getRemoteAddress() throws ConnectorException;
	
	public void close() throws ConnectorException;
	
}
