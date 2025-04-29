/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;

/**
 * HttpServletConnectorOutMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServletConnectorAsyncOutMessage extends HttpServletConnectorOutMessage {

	protected AsyncContext asyncContext;	
	private CompletableFuture<Boolean> asyncWriteTask = null;
	private boolean flowStream = false;
	
	public HttpServletConnectorAsyncOutMessage(RequestInfo requestInfo,IProtocolFactory<?> protocolFactory, AsyncContext ac,
			IDService idModuloAsIDService, String idModulo) throws ConnectorException{
		super(requestInfo, protocolFactory, getHttpServletResponse(ac), idModuloAsIDService, idModulo);
		this.asyncContext = ac;
	}
	public static HttpServletResponse getHttpServletResponse(AsyncContext ac) {
		return (HttpServletResponse) ac.getResponse();
	}

	private Throwable nioException;
	public void setNioException(Throwable nioException) {
		this.nioException = nioException;
	}
		
	@Override
	protected void writeTo(OutputStream out, OpenSPCoop2Message msg, boolean consume) throws MessageException {
		try {
			this.asyncWriteTask = new CompletableFuture<>();
			if ( this.flowStream ) {
				this.res.getOutputStream().setWriteListener( new WriteListener() {
					
					@Override
					public void onWritePossible() throws IOException {
						try {
							msg.writeTo( out, consume );
							HttpServletConnectorAsyncOutMessage.this.asyncWriteTask.complete( true );
						} catch ( MessageException e ) {
							/**OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante la scrittura della risposta asincrona: " + e.getMessage(), e);*/
							throw new IOException( e.getMessage(),e );
						}
					}
					
					@Override
					public void onError( Throwable t ) {
						HttpServletConnectorAsyncOutMessage.this.asyncWriteTask.complete( false );
						OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante la consegna della risposta asincrona: " + t.getMessage(), t);
				        HttpServletConnectorAsyncOutMessage.this.asyncContext.complete();
					}
				});
			} else {
				super.writeTo(out, msg, consume);
			}
		} catch (Exception e) {
			doMessageErrorWriteTo(e);
		} finally {
			if ( !this.flowStream ) {
				this.asyncWriteTask.complete( true );
			}
		}
	}
	private void doMessageErrorWriteTo(Exception e) throws MessageException {
		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante la scrittura della risposta asincrona: " + e.getMessage(), e);
		throw new MessageException( e.getMessage(),e );
	}
	
	@Override
	public void sendResponse(DumpByteArrayOutputStream message) throws ConnectorException{
		try {
			this.asyncWriteTask = new CompletableFuture<>();
			HttpServletConnectorOutMessage httpServletConnectorOutMessage = this;
			if ( this.flowStream ) {
				this.res.getOutputStream().setWriteListener( new WriteListener() {
					
					@Override
					public void onWritePossible() throws IOException {
						try {
							httpServletConnectorOutMessage.sendResponseByBuffer(message);
							HttpServletConnectorAsyncOutMessage.this.asyncWriteTask.complete( true );
						} catch ( ConnectorException e ) {
							/**OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante la scrittura della risposta asincrona (DumpByteArrayOutputStream): " + e.getMessage(), e);*/
							throw new IOException( e.getMessage(),e );
						}
					}
					
					@Override
					public void onError( Throwable t ) {
						HttpServletConnectorAsyncOutMessage.this.asyncWriteTask.complete( false );
						OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante la consegna della risposta asincrona (DumpByteArrayOutputStream): " + t.getMessage(), t);
				        HttpServletConnectorAsyncOutMessage.this.asyncContext.complete();
					}
				});
			} else {
				httpServletConnectorOutMessage.sendResponseByBuffer(message);
			}
		} catch (Exception e) {
			doConnectionErrorWriteTo(e);
		} finally {
			if ( !this.flowStream ) {
				this.asyncWriteTask.complete( true );
			}
		}
	}
	private void doConnectionErrorWriteTo(Exception e) throws ConnectorException {
		OpenSPCoop2Logger.getLoggerOpenSPCoopCore().error("Errore durante la scrittura della risposta asincrona (DumpByteArrayOutputStream): " + e.getMessage(), e);
		throw new ConnectorException( e.getMessage(),e );
	}
	
	
	
	/**
	private void asyncWrite(OutputStream os, byte[] content) {
		jakarta.servlet.ServletOutputStream servletOutputStream = (jakarta.servlet.ServletOutputStream) os;
		AsyncWriteListener writeListener = new AsyncWriteListener(this._ac, content, 
				servletOutputStream, this.protocolFactory.getLogger());
		servletOutputStream.setWriteListener(writeListener);
	}
	*/
	
	@Override
	public void flush(boolean throwException) throws ConnectorException {
		try {
			if ( this.asyncWriteTask != null ) {
				boolean taskRes = this.asyncWriteTask.get();
				this.asyncWriteTask = null;
				if(!taskRes && throwException) {
					throw new ConnectorException("Response write uncomplete (flush)?");
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			if(throwException) {
				Thread.currentThread().interrupt();
				throw new ConnectorException(e.getMessage(),e);
			}
		}
		if(this.nioException!=null && throwException) {
			throw new ConnectorException(this.nioException.getMessage(),this.nioException);
		}
		
		super.flush(throwException);
	}
	
	@Override
	public void close(AsyncResponseCallbackClientEvent clientEvent, boolean throwException) throws ConnectorException{
		
		super.close(clientEvent, throwException);
		
		try{
			if ( this.asyncWriteTask != null ) {
				boolean taskRes = this.asyncWriteTask.get();
				this.asyncWriteTask = null;
				if(!taskRes && throwException) {
					throw new ConnectorException("Response write uncomplete (close)?");
				}
			}
			
			if(this.asyncContext!=null) {
				asyncContextCompleteSafe(throwException);
			}
		}catch(Exception e){
			Thread.currentThread().interrupt();
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	private void asyncContextCompleteSafe(boolean throwException) {
		try{
			/**if(!AsyncResponseCallbackClientEvent.FAILED.equals(clientEvent)) {*/ 
			this.asyncContext.complete();
		}catch(Exception e){
			if(throwException){
				throw e;
			}
		}
	}
}

/**
class AsyncWriteListener implements jakarta.servlet.WriteListener {
	
	private AsyncContext ac;
	private int index = 0;
	private byte[] content;
	private jakarta.servlet.ServletOutputStream servletOutputStream;
	private org.slf4j.Logger log;
	
	public AsyncWriteListener(AsyncContext ac, byte[] content, jakarta.servlet.ServletOutputStream servletOutputStream, org.slf4j.Logger log){
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
