/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import jakarta.servlet.http.HttpServletRequest;

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
import org.openspcoop2.pdd.core.controllo_traffico.DimensioneMessaggiUtils;
import org.openspcoop2.pdd.core.controllo_traffico.LimitExceededNotifier;
import org.openspcoop2.pdd.core.controllo_traffico.SogliaDimensioneMessaggio;
import org.openspcoop2.pdd.core.controllo_traffico.SogliaReadTimeout;
import org.openspcoop2.pdd.core.controllo_traffico.TimeoutNotifier;
import org.openspcoop2.pdd.core.controllo_traffico.TimeoutNotifierType;
import org.openspcoop2.pdd.logger.DiagnosticInputStream;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.IDService;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.state.URLProtocolContext;
import org.openspcoop2.utils.LimitExceededIOException;
import org.openspcoop2.utils.LimitedInputStream;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.TimeoutInputStream;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.Credential;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
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
	protected LimitedInputStream internalLimitedIS;
	protected TimeoutInputStream internalTimeoutIS;
	protected DiagnosticInputStream internalDiagnosticIS;
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
	
	private SogliaReadTimeout requestReadTimeout;
	private SogliaDimensioneMessaggio requestLimitSize;
	private boolean requestLimitSizeDisabled = false;
	
	private boolean useDiagnosticInputStream;
	private MsgDiagnostico msgDiagnostico;
	
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
			
			if(IDService.PORTA_APPLICATIVA.equals(idModuloAsIDService) || IDService.PORTA_APPLICATIVA_NIO.equals(idModuloAsIDService)){
				this.requestMessageType = this.getRequestInfo().getProtocolRequestMessageType();
			}
			else{
				this.requestMessageType = this.getRequestInfo().getIntegrationRequestMessageType();
			}
			
			if(this.openspcoopProperties!=null) {
				if(IDService.PORTA_APPLICATIVA.equals(idModuloAsIDService) || IDService.PORTA_APPLICATIVA_NIO.equals(idModuloAsIDService)){
					this.useDiagnosticInputStream = this.openspcoopProperties.isConnettoriUseDiagnosticInputStreamRicezioneBuste();
				}
				else {
					this.useDiagnosticInputStream = this.openspcoopProperties.isConnettoriUseDiagnosticInputStreamRicezioneContenutiApplicativi();
				}
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
		
		if(this.internalTimeoutIS!=null && this.context!=null) {
			this.internalTimeoutIS.updateContext(this.context);
		}
		if(this.internalLimitedIS!=null && this.context!=null) {
			this.internalLimitedIS.updateContext(this.context);
		}
	}
	
	@Override
	public void setRequestReadTimeout(SogliaReadTimeout timeout) {
		this.requestReadTimeout = timeout;
		if(this.internalTimeoutIS!=null) {
			try {
				this.internalTimeoutIS.updateThreshold(this.requestReadTimeout.getSogliaMs());
			}catch(Exception e) {
				throw new UtilsRuntimeException(e.getMessage(),e); // non dovrebbe mai succedere essendo chiamato il metodo solo se timeout e' maggiore di 0
			}
			TimeoutNotifier notifier = new TimeoutNotifier(this.context, this.getProtocolFactory(), 
					this.requestReadTimeout, TimeoutNotifierType.REQUEST, this.log, true);
			this.internalTimeoutIS.updateNotifier(notifier);
		}
	}
	@Override
	public void disableReadTimeout() {
		if(this.internalTimeoutIS!=null) {
			this.internalTimeoutIS.disableCheckTimeout();
		}
	}
	@Override
	public void setRequestLimitedStream(SogliaDimensioneMessaggio requestLimitSize) {
		this.requestLimitSize = requestLimitSize;
		if(this.internalLimitedIS!=null && this.requestLimitSize!=null && this.requestLimitSize.getSogliaKb()>0) {
			try {
				long limitBytes = this.requestLimitSize.getSogliaKb()*1024; // trasformo kb in bytes
				this.internalLimitedIS.updateThreshold(limitBytes);
			}catch(Exception e) {
				throw new UtilsRuntimeException(e.getMessage(),e); // non dovrebbe mai succedere essendo chiamato il metodo solo se la soglia e' maggiore di 0
			}
			LimitExceededNotifier notifier = new LimitExceededNotifier(this.context, this.requestLimitSize, this.log);
			this.internalLimitedIS.updateNotifier(notifier);
		}
	}
	@Override
	public void disableLimitedStream() {
		if(this.internalLimitedIS!=null) {
			this.internalLimitedIS.disableCheck();
		}
		this.requestLimitSizeDisabled = true;
	}
	@Override
	public void checkContentLengthLimit() throws LimitExceededIOException {
		// !NOTA!
		// devo comunque verificare l'input stream per evitare che una informazione sbagliata nell'header faccia superare la policy
		// quindi questo controllo non è alternativo a quello sullo stream
		if(!this.requestLimitSizeDisabled && this.requestLimitSize!=null && this.requestLimitSize.getSogliaKb()>0 && this.requestLimitSize.isUseContentLengthHeader()) {
			List<String> l = TransportUtils.getHeaderValues(this.req, HttpConstants.CONTENT_LENGTH);
			if(l!=null && !l.isEmpty()) {
				LimitExceededNotifier notifier = new LimitExceededNotifier(this.context, this.requestLimitSize, this.log);
				DimensioneMessaggiUtils.verifyByContentLength(this.log, l, this.requestLimitSize, notifier, this.context, DimensioneMessaggiUtils.REQUEST);
			}
		}
	}
	@Override
	public void setDiagnosticProducer(Context context, MsgDiagnostico msgDiag) {
		if(this.context==null) {
			this.context = context;
		}
		this.msgDiagnostico = msgDiag;
	}
	private InputStream buildInputStream() throws IOException {
		
		if(this.buffered &&
			this.buffer!=null && this.buffer.size()>0) {
			return new ByteArrayInputStream(this.buffer.toByteArray());
		}
		
		if(this.is!=null && this.soapReader!=null) {
			return this.is; // stream timeout gia' utilizzato per il soapReader
		}
		
		if(this.is!=null && this.requestLimitSize!=null && this.requestLimitSize.getSogliaKb()>0) {
			LimitExceededNotifier notifier = new LimitExceededNotifier(this.context, this.requestLimitSize, this.log);
			long limitBytes = this.requestLimitSize.getSogliaKb()*1024; // trasformo kb in bytes
			this.internalLimitedIS = new LimitedInputStream(this.is, limitBytes,
					CostantiPdD.PREFIX_LIMITED_REQUEST,
					this.context,
					notifier);
			this.is = this.internalLimitedIS;
		}
		if(this.is!=null && this.requestReadTimeout!=null && this.requestReadTimeout.getSogliaMs()>0) {
			TimeoutNotifier notifier = new TimeoutNotifier(this.context, this.getProtocolFactory(), 
					this.requestReadTimeout, TimeoutNotifierType.REQUEST, this.log, true);
			this.internalTimeoutIS = new TimeoutInputStream(this.is, this.requestReadTimeout.getSogliaMs(),
					CostantiPdD.PREFIX_TIMEOUT_REQUEST,
					this.context,
					notifier);
			this.is = this.internalTimeoutIS;
		}
		if(this.is!=null && this.useDiagnosticInputStream && this.msgDiagnostico!=null) {
			String idModuloFunzionale = 
					IDService.PORTA_APPLICATIVA.equals(this.idModuloAsIDService) ? 
							MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE : MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI;
			this.internalDiagnosticIS = new DiagnosticInputStream(this.is, idModuloFunzionale, "letturaPayloadRichiesta", true, this.msgDiagnostico, 
					(this.log!=null) ? this.log : OpenSPCoop2Logger.getLoggerOpenSPCoopCore(),
					this.context);
			this.is = this.internalDiagnosticIS;
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
	public IProtocolFactory<?> getProtocolFactory() {
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
			/**throw new ConnectorException(e.getMessage(),e);*/
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
			return bout;
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}finally {
			try {
				if(bout!=null) {
					bout.close();
				}
			}catch(Throwable t) {
				// ignore
			}
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
				isCloseSafe();
			}
		}catch(Exception e){
			throw new ConnectorException(e.getMessage(),e);
		}	
	}
	private void isCloseSafe() {
		try{
			this.is.close();
			this.is = null;
		}catch(Exception e){
			// ignore
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
