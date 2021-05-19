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

package org.openspcoop2.message;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;

/**
 * AbstractBaseOpenSPCoop2RestMessage
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractBaseOpenSPCoop2MessageDynamicContent<T> extends AbstractBaseOpenSPCoop2Message {

	protected CountingInputStream countingInputStream; 
	protected String contentType;
	protected String contentTypeCharsetName = Charset.UTF_8.getValue();
	
	protected boolean supportReadOnly = true;
	
	protected boolean contentUpdatable=false;
	protected T content;
	protected boolean hasContent = false;
	
	protected OpenSPCoop2MessageSoapStreamReader soapStreamReader;
	
	protected DumpByteArrayOutputStream contentBuffer;
	private static int soglia;
	private static File repositoryFile;
	public static void setSoglia(int soglia) {
		AbstractBaseOpenSPCoop2MessageDynamicContent.soglia = soglia;
	}
	public static void setRepositoryFile(File repositoryFile) {
		AbstractBaseOpenSPCoop2MessageDynamicContent.repositoryFile = repositoryFile;
	}
	
	private static boolean soapReader = true; 
	private static int soapReaderBufferThresholdKb = 10;
	public static void setSoapReader(boolean soapReader) {
		AbstractBaseOpenSPCoop2MessageDynamicContent.soapReader = soapReader;
	}
	public static void setSoapReaderBufferThresholdKb(int bufferThresholdKb) {
		AbstractBaseOpenSPCoop2MessageDynamicContent.soapReaderBufferThresholdKb = bufferThresholdKb;
	}
	
	
	/* Costruttore */
	
	protected AbstractBaseOpenSPCoop2MessageDynamicContent(OpenSPCoop2MessageFactory messageFactory) {
		super(messageFactory);
		this.hasContent = false;
	}
	
	protected AbstractBaseOpenSPCoop2MessageDynamicContent(OpenSPCoop2MessageFactory messageFactory, InputStream isParam,String contentType, 
			boolean soap, OpenSPCoop2MessageSoapStreamReader soapStreamReader) throws MessageException {
		super(messageFactory);
		try{
			this.contentType = contentType;
			if(contentType!=null){
				String ch = ContentTypeUtilities.readCharsetFromContentType(contentType);
				if(ch!=null){
					this.contentTypeCharsetName = ch;
				}
			}
			if(isParam!=null){
				
				if(soap && soapStreamReader!=null) {
					soapStreamReader.checkException(); // senno si entra nell'if dello stream vuoto prima
				}
				
				// check se esiste del contenuto nello stream, lo stream può essere diverso da null però vuoto.
				byte[] b = new byte[1];
				if(isParam.read(b) == -1) {
					// stream vuoto
					this.hasContent = false;
				} else {
					InputStream seq = new SequenceInputStream(new ByteArrayInputStream(b),isParam);
					
					if(soap) {
						if(soapStreamReader==null) {
							if(AbstractBaseOpenSPCoop2MessageDynamicContent.soapReader) {
								this.soapStreamReader = new OpenSPCoop2MessageSoapStreamReader(messageFactory,
										this.contentType, seq, AbstractBaseOpenSPCoop2MessageDynamicContent.soapReaderBufferThresholdKb);
								seq = this.soapStreamReader.read();
							}
						}
						else {
							this.soapStreamReader = soapStreamReader;
							this.soapStreamReader.checkException();
						}
					}
					
					this.countingInputStream = new CountingInputStream(seq);
					this.hasContent = true;
				}

			}
		}catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	

	
	/* Metodi richiesti da chi implementa questa classe base */
	
	protected abstract T buildContent() throws MessageException;
	protected abstract T buildContent(DumpByteArrayOutputStream contentBuffer) throws MessageException;
	protected abstract String buildContentAsString() throws MessageException;
	protected abstract void serializeContent(OutputStream os, boolean consume) throws MessageException;
	

	
	/* Informazioni SOAP (senza costruire il DOM) */
	
	public OpenSPCoop2MessageSoapStreamReader getSoapReader() {
		return this.soapStreamReader;
	}
	
	
	/* Contenuto */
	
	private synchronized void initializeContent(boolean readOnly, String idTransazione) throws MessageException{
		if(this.hasContent){
			if(this.content==null){
				
				if(readOnly && this.supportReadOnly) {
					this.contentBuffer=new DumpByteArrayOutputStream(AbstractBaseOpenSPCoop2MessageDynamicContent.soglia, 
							AbstractBaseOpenSPCoop2MessageDynamicContent.repositoryFile, 
							idTransazione, this.getMessageRole().name());
					try {
						CopyStream.copy(this.countingInputStream, this.contentBuffer);
						this.content = this.buildContent(this.contentBuffer);
					}catch(Throwable t) {
						MessageUtils.registerParseException(this, t, true);
						throw new MessageException(t.getMessage(),t);
					}finally {
						try {
							this.countingInputStream.close();
						}catch(Exception eClose) {}
					}
				}
				else {
					try {
						this.content = this.buildContent();
					}catch(Throwable t) {
						MessageUtils.registerParseException(this, t, true);
						throw new MessageException(t.getMessage(),t);
					}
				}
				
			}
		}
	}

	@Override
	public boolean isContentBuilded() {
		return this.content!=null;
	}
	
	public InputStream getInputStream() {
		return this.countingInputStream;
	}
	
	public boolean hasContent() throws MessageException,MessageNotSupportedException{
		return this.hasContent;
	}

	public T getContent() throws MessageException,MessageNotSupportedException{
		return getContent(false, null);
	}
	public T getContent(boolean readOnly, String idTransazione) throws MessageException,MessageNotSupportedException{
		if(this.hasContent){
			if(this.content==null){
				this.initializeContent(readOnly, idTransazione);
			}
			
			if(!readOnly) {
				this.contentUpdatable = true;
			}
		}
		return this.content; // può tornare null
	}
	
	public String getContentAsString() throws MessageException,MessageNotSupportedException{
		return getContentAsString(false, null);
	}
	public String getContentAsString(boolean readOnly, String idTransazione) throws MessageException,MessageNotSupportedException{
		try{
			if(this.hasContent){
				if(this.content==null){
					this.initializeContent(readOnly, idTransazione);
				}
				
				if(!readOnly) {
					this.contentUpdatable = true;
				}
				
				return this.buildContentAsString();
			}
			return null;
		}catch(MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}
	}
	
	public void updateContent(T content) throws MessageException, MessageNotSupportedException{
		this.content = content;
		this.contentUpdatable = true;
		if(this.contentBuffer!=null) {
			this.contentBuffer.clearResources();
			this.contentBuffer = null;
		}
		if(this.content!=null) {
			this.hasContent = true;
		}
		else {
			this.hasContent = false;
			this.contentType = null;
		}
	}
	
	public void setContentUpdatable() {
		this.contentUpdatable = true;
	}
		
	
	/* ContentType */
	
	@Override
	public void updateContentType() throws MessageException {
		// nop;
	}

	@Override
	public void setContentType(String type) {
		this.contentType = type;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	
	/* WriteTo e Save */
	
	@Override
	public void writeTo(OutputStream os, boolean consume) throws MessageException {
		this.writeTo(os, consume, false, null);
	}
	public void writeTo(OutputStream os, boolean consume, boolean readOnly, String idTransazione) throws MessageException{
		try{
			if(this.hasContent){
				
				if(!consume && this.content==null) {
					this.initializeContent(readOnly, idTransazione); // per poi entrare nel ramo sotto serializeContent
				}
			
				CountingOutputStream cos = new CountingOutputStream(os);
				if(this.contentBuffer!=null && !this.contentUpdatable) {
					//System.out.println("SERIALIZE BUFFER");
					this.contentBuffer.writeTo(cos);
				}
				else if(this.content!=null){
					//System.out.println("SERIALIZE CONTENT");
					this.serializeContent(cos, consume);
				}
				else{
					//System.out.println("SERIALIZE STREAM");
					Utilities.copy(this.countingInputStream, cos);
					this.countingInputStream.close();
				}
				this.outgoingsize = cos.getByteCount();
			}
		}
		catch(MessageException e){
			throw e;
		}
		catch(Exception e){
			throw new MessageException(e.getMessage(),e);
		}finally {
			if(consume) {
				try {
					if(this.contentBuffer!=null) {
						this.contentBuffer.unlock();
						this.contentBuffer.clearResources();
						this.contentBuffer=null;
					}
				}catch(Throwable t) {}
			}
		}
	}
	
	@Override
	public void saveChanges() throws MessageException{
		// nop;
	}
	
	@Override
	public boolean saveRequired(){
		return false;
	}
	
	
	/* Content Length */
	
	@Override
	public long getIncomingMessageContentLength() {
		if(this.countingInputStream!=null) {
			return this.countingInputStream.getByteCount();
		}
		else {
			return super.getIncomingMessageContentLength();
		}
	}	
	

}
