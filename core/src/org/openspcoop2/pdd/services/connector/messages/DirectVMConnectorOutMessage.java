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
package org.openspcoop2.pdd.services.connector.messages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.services.DirectVMProtocolInfo;
import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.transport.TransportUtils;

/**
 * DirectVMConnectorOutMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DirectVMConnectorOutMessage implements ConnectorOutMessage {

	private DirectVMProtocolInfo directVMProtocolInfo;
	
	public DirectVMConnectorOutMessage() throws ConnectorException{
		try{
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}

	public DirectVMProtocolInfo getDirectVMProtocolInfo() {
		return this.directVMProtocolInfo;
	}
	public void setDirectVMProtocolInfo(DirectVMProtocolInfo directVMProtocolInfo) {
		this.directVMProtocolInfo = directVMProtocolInfo;
	}
	
	private OpenSPCoop2Message message;
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
	@Override
	public void sendResponse(OpenSPCoop2Message msg, boolean consume) throws ConnectorException {
		this.message = msg;
	}

	private DumpByteArrayOutputStream messageAsBytes;
	public DumpByteArrayOutputStream getMessageAsBytes() {
		return this.messageAsBytes;
	}
	@Override
	public void sendResponse(DumpByteArrayOutputStream message) throws ConnectorException{
		this.messageAsBytes = message;
	}
	
	private OpenSPCoop2Message responseHeaderMessage;
	public OpenSPCoop2Message getResponseHeaderMessage() {
		return this.responseHeaderMessage;
	}
	@Override
	public void sendResponseHeaders(OpenSPCoop2Message message) throws ConnectorException{
		this.responseHeaderMessage = message;
	}
	
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();
	public List<String> getHeaderValues(String key) throws ConnectorException{
		return TransportUtils.getRawObject(this.headers,key);
	}
	public Map<String,  List<String>> getHeaders(){
		Map<String,  List<String>> pH = new HashMap<String,  List<String>>();
		pH.putAll(this.headers);
		return pH;
	}
	@Override
	public void setHeader(String key,String value) throws ConnectorException{
		try{
			TransportUtils.setHeader(this.headers, key,value);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	@Override
	public void addHeader(String key,String value) throws ConnectorException{
		try{
			TransportUtils.addHeader(this.headers, key,value);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	private int length;
	public int getContentLength() {
		return this.length;
	}
	@Override
	public void setContentLength(int length) throws ConnectorException{
		this.length = length;
	}
	
	String type;
	public String getContentType() {
		return this.type;
	}
	@Override
	public void setContentType(String type) throws ConnectorException{
		this.type = type;
	}
	
	private int status = 200;
	@Override
	public void setStatus(int status) throws ConnectorException{
		this.status = status;
	}
	@Override
	public int getResponseStatus() throws ConnectorException{
		return this.status;
	}
	
	@Override
	public void flush(boolean throwException) throws ConnectorException{
		// nop
	}
	
	@Override
	public void close(AsyncResponseCallbackClientEvent clientEvent, boolean throwException) throws ConnectorException{
		// nop
	}
}
