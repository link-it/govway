package org.openspcoop2.pdd.services;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.ConnectorOutMessage;

public class DumpRawConnectorOutMessage implements ConnectorOutMessage {

	private Logger log;
	private ConnectorOutMessage connectorOutMessage;
	private ByteArrayOutputStream bout = null;
	private Properties trasporto = new Properties();
	private Integer contentLenght;
	private String contentType;
	private Integer status;
	
	public DumpRawConnectorOutMessage(Logger log,ConnectorOutMessage connectorOutMessage){
		this.log = log;
		this.connectorOutMessage = connectorOutMessage;
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
			message.writeTo(this.bout, false);
		}catch(Throwable t){
			try{
				this.bout = new ByteArrayOutputStream();
				this.bout.write(("SendResponse error: "+t.getMessage()).getBytes());
			}catch(Throwable tWrite){}
			this.log.error("SendResponse error: "+t.getMessage(),t);
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
		this.connectorOutMessage.sendResponse(message, consume);
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
