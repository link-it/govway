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

import java.io.OutputStream;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;

/**
 * HttpServletConnectorOutMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServletConnectorAsyncOutMessage extends HttpServletConnectorOutMessage {

	protected AsyncContext _ac;	
	
	public HttpServletConnectorAsyncOutMessage(IProtocolFactory<?> protocolFactory, AsyncContext ac,
			IDService idModuloAsIDService, String idModulo) throws ConnectorException{
		super(protocolFactory, null, idModuloAsIDService, idModulo);
		this._ac = ac;
	}

	private HttpServletResponse _res;
	private synchronized void initHttpServletResponse() {
		if(this._res==null) {
			this._res = (HttpServletResponse) this._ac.getResponse();
		}
	}
	@Override
	protected HttpServletResponse getHttpServletResponse() {
		if(this._res==null) {
			this.initHttpServletResponse();
		}
		return this._res;
	}

	private Throwable nioException;
	public void setNioException(Throwable nioException) {
		this.nioException = nioException;
	}
	
	//private boolean bufferingResponse = false; // TODO PARAMETRO ? Siamo sicuri serva in caso di streaming
	
	@Override
	protected void responseWrite(OpenSPCoop2Message msg, OutputStream os, boolean consume) throws ConnectorException {
		//if(!this.bufferingResponse || !(os instanceof  javax.servlet.ServletOutputStream) ) {
		super.responseWrite(msg, os, consume);
		/*}
		else {
			try{
				java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
				msg.writeTo(bout,consume);
				bout.flush();
				bout.close();
				this.asyncWrite(os, bout.toByteArray());
			}catch(Exception e){
				throw new ConnectorException(e.getMessage(),e);
			}
		}*/
	}
	
	@Override
	protected void responseWrite(DumpByteArrayOutputStream message, OutputStream os) throws ConnectorException{
		//if(!this.bufferingResponse || !(os instanceof  javax.servlet.ServletOutputStream) ) {
		super.responseWrite(message, os);
		/*}
		else {
			try{
				this.asyncWrite(os, message.toByteArray());
			}catch(Exception e){
				throw new ConnectorException(e.getMessage(),e);
			}
		}
		*/
	}
	
	/*
	private void asyncWrite(OutputStream os, byte[] content) {
		javax.servlet.ServletOutputStream servletOutputStream = (javax.servlet.ServletOutputStream) os;
		AsyncWriteListener writeListener = new AsyncWriteListener(this._ac, content, 
				servletOutputStream, this.protocolFactory.getLogger());
		servletOutputStream.setWriteListener(writeListener);
	}
	*/
	
	@Override
	public void flush(boolean throwException) throws ConnectorException {
	
		if(this.nioException!=null && throwException) {
			throw new ConnectorException(this.nioException.getMessage(),this.nioException);
		}
		
		super.flush(throwException);
	}
	
	@Override
	public void close(AsyncResponseCallbackClientEvent clientEvent, boolean throwException) throws ConnectorException{
		
		super.close(clientEvent, throwException);
		
		try{
			//if(this._ac!=null && !this.bufferingResponse){
			if(this._ac!=null) {
				try{
					//if(!AsyncResponseCallbackClientEvent.FAILED.equals(clientEvent)) { 
					this._ac.complete();
					//}
				}catch(Exception e){
					if(throwException){
						throw e;
					}
				}
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
}

/*
class AsyncWriteListener implements javax.servlet.WriteListener {
	
	private AsyncContext ac;
	private int index = 0;
	private byte[] content;
	private javax.servlet.ServletOutputStream servletOutputStream;
	private org.slf4j.Logger log;
	
	public AsyncWriteListener(AsyncContext ac, byte[] content, javax.servlet.ServletOutputStream servletOutputStream, org.slf4j.Logger log){
		this.ac = ac;
		this.content = content;
		this.servletOutputStream = servletOutputStream;
		this.log = log;
	}
	
	@Override
	public void onWritePossible() throws java.io.IOException {
        while ( this.index < this.content.length && this.servletOutputStream.isReady() ) {
        	int toBeWrite = this.content.length - this.index;
        	if ( toBeWrite > 1024 )
        		toBeWrite = 1024;
        	this.servletOutputStream.write(this.content, this.index, toBeWrite);
        	this.index += toBeWrite;
        }
		if ( this.index >= this.content.length ) {
			this.ac.complete();
		}
	}

	@Override
	public void onError(Throwable t) {
        this.log.error( "Errore durante la consegna della risposta asincrona: "+t.getMessage(), t );
        this.ac.complete();
	}
}
*/
