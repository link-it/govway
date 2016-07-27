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
package org.openspcoop2.pdd.services.connector;

import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.Identity;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

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
	
	public Object getAttribute(String key) throws ConnectorException;
	
	public String getHeader(String key) throws ConnectorException;
	
	public String getParameter(String key) throws ConnectorException;
	
	public IProtocolFactory getProtocolFactory() throws ConnectorException;
	
	public String getContentType() throws ConnectorException;
	
	public String getSOAPAction(SOAPVersion versioneSoap, String contentType) throws ConnectorException;
	
	public OpenSPCoop2MessageParseResult getRequest(NotifierInputStreamParams notifierInputStreamParams, String contentType) throws ConnectorException;
	
	public byte[] getRequest() throws ConnectorException;
	
	public URLProtocolContext getURLProtocolContext() throws ConnectorException;
	
	public Credenziali getCredenziali() throws ConnectorException;
	
	public String getLocation(Credenziali credenziali) throws ConnectorException;
	
	public String getProtocol() throws ConnectorException;
	
	public int getContentLength() throws ConnectorException;
	
	public void close() throws ConnectorException;
	
	public Identity getIdentity() throws ConnectorException;
	
}
