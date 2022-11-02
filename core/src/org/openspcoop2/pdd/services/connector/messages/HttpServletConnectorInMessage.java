/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.ParseExceptionUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.controllo_traffico.LimitExceededNotifier;
import org.openspcoop2.pdd.core.controllo_traffico.SogliaDimensioneMessaggio;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.LimitedInputStream;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TimeoutInputStream;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.Credential;
import org.openspcoop2.utils.transport.TransportUtils;
import org.slf4j.Logger;

/**
 * HttpServletConnectorInMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpServletConnectorInMessage implements ConnectorInMessage {

	protected RequestInfo requestInfo;
	protected HttpServletRequest req;
	protected OpenSPCoop2Properties openspcoopProperties;
	protected OpenSPCoop2Message message;
	protected InputStream is;
	protected LimitedInputStream _limitedIS;
	protected TimeoutInputStream _timeoutIS;
	protected DumpByteArrayOutputStream buffer;
	protected boolean buffered = false;
	protected OpenSPCoop2MessageSoapStreamReader soapReader;
	protected Logger log;
	protected String idModulo;
	private IDService idModuloAsIDService;
	private MessageType requestMessageType;
	protected Date dataIngressoRichiesta;

	private Context context;
	private String idTransazione;
	private int soglia;
	private File repositoryFile;
	
	private int requestReadTimeout;
	private SogliaDimensioneMessaggio requestLimitSize;
	
	public HttpServletConnectorInMessage(RequestInfo requestInfo, HttpServletRequest req,
			IDService idModuloAsIDService, String idModulo) throws ConnectorException{
		try{
			this.requestInfo = requestInfo;
			this.req = req;
			this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
			this.is = this.req.getInputStream();
			
			this.log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
			if(this.log==null)
				this.log = LoggerWrapperFactory.getLogger(HttpServletConnectorInMessage.class);
			
			this.idModuloAsIDService = idModuloAsIDService;
			this.idModulo = idModulo;
			
			if(IDService.PORTA_APPLICATIVA.equals(idModuloAsIDService)){
				this.requestMessageType = this.getRequestInfo().getProtocolRequestMessageType();
			}
			else{
				this.requestMessageType = this.getRequestInfo().getIntegrationRequestMessageType();
			}
			
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public void setThresholdContext(Context context,
			int soglia, File repositoryFile) {
		this.context = context;
		if(this.context!=null) {
			this.idTransazione = (String) this.context.getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		}
		this.soglia = soglia;
		this.repositoryFile = repositoryFile;
		
		if(this._timeoutIS!=null && this.context!=null) {
			this._timeoutIS.updateContext(this.context);
		}
		if(this._limitedIS!=null && this.context!=null) {
			this._limitedIS.updateContext(this.context);
		}
	}
	
	@Override
	public void setRequestReadTimeout(int timeout) {
		this.requestReadTimeout = timeout;
		if(this._timeoutIS!=null) {
			try {
				this._timeoutIS.updateThreshold(this.requestReadTimeout);
			}catch(Exception e) {
				throw new RuntimeException(e.getMessage(),e); // non dovrebbe mai succedere essendo chiamato il metodo solo se timeout e' maggiore di 0
			}
		}
	}
	@Override
	public void disableReadTimeout() {
		if(this._timeoutIS!=null) {
			this._timeoutIS.disableCheckTimeout();
		}
	}
	@Override
	public void setRequestLimitedStream(SogliaDimensioneMessaggio requestLimitSize) {
		this.requestLimitSize = requestLimitSize;
		if(this._limitedIS!=null && this.requestLimitSize!=null && this.requestLimitSize.getSogliaKb()>0) {
			try {
				long limitBytes = this.requestLimitSize.getSogliaKb()*1024; // trasformo kb in bytes
				this._limitedIS.updateThreshold(limitBytes);
			}catch(Exception e) {
				throw new RuntimeException(e.getMessage(),e); // non dovrebbe mai succedere essendo chiamato il metodo solo se la soglia e' maggiore di 0
			}
			LimitExceededNotifier notifier = new LimitExceededNotifier(this.context, this.requestLimitSize, this.log);
			this._limitedIS.updateNotifier(notifier);
		}
	}
	@Override
	public void disableLimitedStream() {
		if(this._limitedIS!=null) {
			this._limitedIS.disableCheck();
		}
	}
	private InputStream buildInputStream() throws IOException {
		
		if(this.buffered) {
			if(this.buffer!=null && this.buffer.size()>0) {
				return new ByteArrayInputStream(this.buffer.toByteArray());
			}
		}
		
		if(this.is!=null && this.soapReader!=null) {
			return this.is; // stream timeout gia' utilizzato per il soapReader
		}
		
		if(this.is!=null && this.requestLimitSize!=null && this.requestLimitSize.getSogliaKb()>0) {
			LimitExceededNotifier notifier = new LimitExceededNotifier(this.context, this.requestLimitSize, this.log);
			long limitBytes = this.requestLimitSize.getSogliaKb()*1024; // trasformo kb in bytes
			this._limitedIS = new LimitedInputStream(this.is, limitBytes,
					CostantiPdD.PREFIX_LIMITED_REQUEST,
					this.context,
					notifier);
			this.is = this._limitedIS;
		}
		if(this.is!=null && this.requestReadTimeout>0) {
			this._timeoutIS = new TimeoutInputStream(this.is, this.requestReadTimeout,
					CostantiPdD.PREFIX_TIMEOUT_REQUEST,
					this.context);
			this.is = this._timeoutIS;
		}
		return this.is;
	}
	
	@Override
	public IDService getIdModuloAsIDService(){
		return this.idModuloAsIDService;
	}
	
	@Override
	public String getIdModulo(){
		return this.idModulo;
	}
	
	@Override
	public void updateRequestInfo(RequestInfo requestInfo) throws ConnectorException{
		this.requestInfo = requestInfo;
		if(IDService.PORTA_APPLICATIVA.equals(this.idModuloAsIDService)){
			this.requestMessageType = this.getRequestInfo().getProtocolRequestMessageType();
		}
		else{
			this.requestMessageType = this.getRequestInfo().getIntegrationRequestMessageType();
		}
	}
	
	@Override
	public RequestInfo getRequestInfo(){
		return this.requestInfo;
	}
	
	@Override
	public MessageType getRequestMessageType() {
		return this.requestMessageType;
	}
	
	@Override
	public Object getAttribute(String key) throws ConnectorException {
		return this.req.getAttribute(key);
	}
	
	@Override
	public List<String> getHeaderValues(String key) throws ConnectorException{
		return TransportUtils.getHeaderValues(this.req, key);
	}
	
	@Override
	public List<String> getParameterValues(String key) throws ConnectorException{
		return TransportUtils.getParameterValues(this.req, key);
	}

	@Override
	public IProtocolFactory<?> getProtocolFactory() throws ConnectorException{
		return this.requestInfo.getProtocolFactory();
	}
	
	@Override
	public String getContentType() throws ConnectorException{
		try{
			return this.requestInfo.getProtocolContext().getContentType(true);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public String getSOAPAction() throws ConnectorException{
		try{
			String contentType = this.getContentType();
			return SoapUtils.getSoapAction(this.requestInfo.getProtocolContext(), this.requestMessageType, contentType);
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public OpenSPCoop2MessageSoapStreamReader getSoapReader() throws ConnectorException{
		try{
			if(this.openspcoopProperties.useSoapMessageReader()) {
				if(this.buffered) {
					return null; // deve essere chiamato prima
				}
				
				if(this.soapReader!=null) {
					return this.soapReader;
				}
				
				String contentType = getContentType();
				if(contentType!=null) {
					this.soapReader = new OpenSPCoop2MessageSoapStreamReader(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), contentType, 
							this.buildInputStream(), this.openspcoopProperties.getSoapMessageReaderBufferThresholdKb());
					try {
						this.soapReader.read();
					}finally {
						// anche in caso di eccezione devo cmq aggiornare is
						this.is = this.soapReader.getBufferedInputStream();
					}
				}
				return this.soapReader;
			}
			return null;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public OpenSPCoop2MessageParseResult getRequest(NotifierInputStreamParams notifierInputStreamParams) throws ConnectorException{
		try{
			OpenSPCoop2MessageParseResult pr = org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.log,this.requestInfo, MessageRole.REQUEST).createMessage(this.requestMessageType,
					this.requestInfo.getProtocolContext(),
					this.buildInputStream(),notifierInputStreamParams, this.soapReader,
					this.openspcoopProperties.getAttachmentsProcessingMode());
			this.dataIngressoRichiesta = DateManager.getDate();
			return pr;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}

	// Metodo utile per il dump
	public OpenSPCoop2MessageParseResult getRequest(DumpByteArrayOutputStream buffer,NotifierInputStreamParams notifierInputStreamParams) throws ConnectorException{
		try{
			InputStream in = null;
			try{
				Utilities.writeAsByteArrayOuputStream(buffer, this.buildInputStream(),false); // se l'input stream is empty ritorna null grazie al parametro false
				if(buffer.size()>0) {
					if(buffer.isSerializedOnFileSystem()) {
						in = new FileInputStream(buffer.getSerializedFile());
					}
					else {
						in = new ByteArrayInputStream(buffer.toByteArray());
					}
				}
			}catch(Throwable t){
				OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
				result.setParseException(ParseExceptionUtils.buildParseException(t,MessageRole.REQUEST));
				return result;
			}
			OpenSPCoop2MessageParseResult pr = org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.log,this.requestInfo, MessageRole.REQUEST).createMessage(this.requestMessageType,
					this.requestInfo.getProtocolContext(),
					in,notifierInputStreamParams,this.soapReader,
					this.openspcoopProperties.getAttachmentsProcessingMode());
			this.dataIngressoRichiesta = DateManager.getDate();
			return pr;
		}catch(Throwable t){
			//throw new ConnectorException(e.getMessage(),e);
			OpenSPCoop2MessageParseResult result = new OpenSPCoop2MessageParseResult();
			result.setParseException(ParseExceptionUtils.buildParseException(t,MessageRole.REQUEST));
			return result;
		}	
	}

	@Override
	public DumpByteArrayOutputStream getRequest() throws ConnectorException{
		return getRequest(true);
	}
	
	@Override
	public DumpByteArrayOutputStream getRequest(boolean consume) throws ConnectorException{
		if(this.buffered) {
			return this.buffer;
		}
		DumpByteArrayOutputStream bout = null; 
		try{
			this.dataIngressoRichiesta = DateManager.getDate();
			
			bout = new DumpByteArrayOutputStream(this.soglia, this.repositoryFile, this.idTransazione, 
					TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO.getValue());
			Utilities.writeAsByteArrayOuputStream(bout, this.buildInputStream(),false); // se l'input stream is empty ritorna null grazie al parametro false
			bout.flush();
			bout.close();
			return bout;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}finally {
			if(!consume) {
				this.buffer = bout;
				this.buffered = true;
			}
		}
	}
	
	@Override
	public Date getDataIngressoRichiesta(){	
		return this.dataIngressoRichiesta;
	}
	
	@Override
	public URLProtocolContext getURLProtocolContext() throws ConnectorException{
		try{
			return this.requestInfo.getProtocolContext();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}
	}
	
	@Override
	public Credential getCredential() throws ConnectorException{
		try{
			return this.requestInfo.getProtocolContext().getCredential();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public String getSource() throws ConnectorException{
		try{
			 return this.requestInfo.getProtocolContext().getSource();
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public String getProtocol() throws ConnectorException{
		return this.req.getProtocol();
	}
	
	@Override
	public int getContentLength() throws ConnectorException{
		return this.req.getContentLength();
	}
	
	@Override
	public void close() throws ConnectorException{
		try{
			if(this.is!=null){
				try{
					this.is.close();
					this.is = null;
				}catch(Exception e){}
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	
	@Override
	public String getRemoteAddress() throws ConnectorException{
		return this.req.getRemoteAddr();
	}
	
	public HttpServletRequest getHttpServletRequest(){
		return this.req;
	}
}
