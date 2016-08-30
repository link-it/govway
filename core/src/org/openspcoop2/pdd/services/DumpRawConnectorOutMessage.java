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

package org.openspcoop2.pdd.services;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.ParseException;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.ConnectorOutMessage;

/**
 * DumpRawConnectorOutMessage
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DumpRawConnectorOutMessage implements ConnectorOutMessage {

	private Logger log;
	private ConnectorOutMessage connectorOutMessage;
	private ByteArrayOutputStream bout = null;
	private ParseException parseException = null;
	private Properties trasporto = new Properties();
	private Integer contentLenght;
	private String contentType;
	private Integer status;
	
	public DumpRawConnectorOutMessage(Logger log,ConnectorOutMessage connectorOutMessage){
		this.log = log;
		this.connectorOutMessage = connectorOutMessage;
	}
	
	public ConnectorOutMessage getWrappedConnectorOutMessage() {
		return this.connectorOutMessage;
	}
	
	public byte[] getResponseAsByte(){
		if(this.bout!=null){
			return this.bout.toByteArray();
		}
		return null;
	}
	public String getResponseAsString(){
		if(this.bout!=null){
			return this.bout.toString();
		}
		return null;
	}
	public boolean isParsingResponseError(){
		return this.parseException!=null;
	}
	public String getParsingResponseErrorAsString(){
		if(this.parseException!=null){
			try{
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				PrintWriter pw = new PrintWriter(bout);
				this.parseException.getSourceException().printStackTrace(pw);
				pw.flush();
				bout.flush();
				pw.close();
				bout.close();
				return bout.toString();
			}catch(Exception e){
				return "ParsingResponseError, serializazione eccezione non riuscita: "+e.getMessage();
			}
		}
		return null;
	}
	
	public Properties getTrasporto() {
		return this.trasporto;
	}

	public Integer getContentLenght() {
		return this.contentLenght;
	}

	public String getContentType() {
		return this.contentType;
	}
	public Integer getStatus() {
		return this.status;
	}
	
	@Override
	public void sendResponse(OpenSPCoop2Message message, boolean consume)
			throws ConnectorException {
		
		try{
			// Prima lo registro e dopo serializzo
			if(this.bout!=null){
				this.bout = null;
			}
			this.bout = new ByteArrayOutputStream();
			// il save e' necessario con i connettori in caso di errori di validazione
			if(message!=null && message.getSOAPBody()!=null && message.getSOAPBody().hasFault()){
				message.saveChanges();
			}
			message.writeTo(this.bout, consume);
		}catch(Throwable t){
			this.bout = null;
			if(message.getParseException()!=null){
				this.parseException = message.getParseException();
			}
			else{
				this.parseException = MessageUtils.buildParseException(t);
			}
			this.log.error("SendResponse error: "+t.getMessage(),t);
			// Devo lanciare l'eccezione senno il servizio esce con 200
			throw new ConnectorException(this.parseException.getSourceException());
		}finally{
			try{
				if(this.bout!=null){
					this.bout.flush();
				}
			}catch(Throwable close){}
			try{
				if(this.bout!=null){
					this.bout.close();
				}
			}catch(Throwable close){}
		}
		
		// wrapped method
		//this.connectorOutMessage.sendResponse(message, consume); Nel caso di attachments genera un nuovo boundary
		if(this.bout!=null){
			this.connectorOutMessage.sendResponse(this.bout.toByteArray());
		}
	}

	@Override
	public void sendResponse(byte[] message) throws ConnectorException {
	
		try{
			// Prima lo registro e dopo serializzo
			if(this.bout!=null){
				this.bout = null;
			}
			this.bout = new ByteArrayOutputStream();
			this.bout.write(message);
		}catch(Throwable t){
			try{
				this.bout = new ByteArrayOutputStream();
				this.bout.write(("SendResponse byte[] error: "+t.getMessage()).getBytes());
			}catch(Throwable tWrite){}
			this.log.error("SendResponse byte[] error: "+t.getMessage(),t);
		}finally{
			try{
				if(this.bout!=null){
					this.bout.flush();
				}
			}catch(Throwable close){}
			try{
				if(this.bout!=null){
					this.bout.close();
				}
			}catch(Throwable close){}
		}
		
		// wrapped method
		this.connectorOutMessage.sendResponse(message);
	}

	@Override
	public void setHeader(String key, String value) throws ConnectorException {
		try{
			// Prima lo registro e dopo serializzo
			this.trasporto.put(key, value);
		}catch(Throwable t){
			try{
				this.bout = new ByteArrayOutputStream();
				this.bout.write(("setHeader ["+key+"] error: "+t.getMessage()).getBytes());
			}catch(Throwable tWrite){}
			this.log.error("Set Header ["+key+"]["+value+"] error: "+t.getMessage(),t);
		}
		
		// wrapped method
		this.connectorOutMessage.setHeader(key,value);
	}

	@Override
	public void setContentLength(int length) throws ConnectorException {
		
		// Prima lo registro
		this.contentLenght = length;
		
		// wrapped method
		this.connectorOutMessage.setContentLength(length);
	}

	@Override
	public void setContentType(String type) throws ConnectorException {
		
		// Prima lo registro
		this.contentType = type;
		
		// wrapped method
		this.connectorOutMessage.setContentType(type);
	}

	@Override
	public void setStatus(int status) throws ConnectorException {
	
		// Prima lo registro
		this.status = status;
		
		// wrapped method
		this.connectorOutMessage.setStatus(status);
	}

	
	// Wrapped Only
	
	@Override
	public int getResponseStatus() throws ConnectorException {
		// wrapped method
		return this.connectorOutMessage.getResponseStatus();
	}

	@Override
	public void flush(boolean throwException) throws ConnectorException {
		// wrapped method
		this.connectorOutMessage.flush(throwException);
	}

	@Override
	public void close(boolean throwException) throws ConnectorException {
		// wrapped method
		this.connectorOutMessage.close(throwException);
	}

}
