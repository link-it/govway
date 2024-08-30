/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;

import org.apache.commons.io.input.BoundedInputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.openspcoop2.message.constants.Costanti;
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

	protected BoundedInputStream _countingInputStream;
	protected String contentType;
	protected String contentTypeCharsetName = Charset.UTF_8.getValue();

	protected boolean supportReadOnly = true;

	protected boolean contentUpdatable = false;
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

	private static boolean soapReader = false; // attivato sul gateway tramite il metodo sottostante; in modo che le
												// altre applicazioni non utilizzino questa funzionalità
												// (TestService,Console...)
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

	protected AbstractBaseOpenSPCoop2MessageDynamicContent(OpenSPCoop2MessageFactory messageFactory,
			InputStream isParam, String contentType, boolean soap, OpenSPCoop2MessageSoapStreamReader soapStreamReader)
			throws MessageException {
		super(messageFactory);
		try {
			this.contentType = contentType;
			if (contentType != null) {
				String ch = ContentTypeUtilities.readCharsetFromContentType(contentType);
				if (ch != null) {
					this.contentTypeCharsetName = ch;
				}
			}
			if (isParam != null) {

				if (soap && soapStreamReader != null) {
					soapStreamReader.checkException(); // senno si entra nell'if dello stream vuoto prima
				}

				// check se esiste del contenuto nello stream, lo stream può essere diverso da
				// null però vuoto.
				InputStream normalizedIs = Utilities.normalizeStream(isParam, false);
				if (normalizedIs == null) {
					// stream vuoto
					this.hasContent = false;
				} else {

					if (soap) {
						if (soapStreamReader == null) {
							if (AbstractBaseOpenSPCoop2MessageDynamicContent.soapReader) {
								this.soapStreamReader = new OpenSPCoop2MessageSoapStreamReader(messageFactory,
										this.contentType, normalizedIs,
										AbstractBaseOpenSPCoop2MessageDynamicContent.soapReaderBufferThresholdKb);
								try {
									this.soapStreamReader.read();
								} finally {
									// anche in caso di eccezione devo cmq aggiornare is
									normalizedIs = this.soapStreamReader.getBufferedInputStream();
								}
							}
						} else {
							this.soapStreamReader = soapStreamReader;
							if (this.soapStreamReader != null) {
								this.soapStreamReader.checkException();
							}
						}
					}

					this._countingInputStream = BoundedInputStream.builder().setInputStream(normalizedIs).get();
					this.hasContent = true;
				}

			}
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}

	/* Metodi richiesti da chi implementa questa classe base */

	protected abstract T buildContent() throws MessageException;

	protected abstract T buildContent(DumpByteArrayOutputStream contentBuffer) throws MessageException;

	protected abstract String buildContentAsString() throws MessageException;

	protected abstract byte[] buildContentAsByteArray() throws MessageException;

	protected abstract void serializeContent(OutputStream os, boolean consume) throws MessageException;

	protected void setUpdatableContent() throws MessageException {
	}

	/* Informazioni SOAP (senza costruire il DOM) */

	public OpenSPCoop2MessageSoapStreamReader getSoapReader() {
		return this.soapStreamReader;
	}

	public void releaseSoapReader() {
		if (this.soapStreamReader != null) {
			this.soapStreamReader.releaseBufferedInputStream();
			this.soapStreamReader.clearHeader();
			this.soapStreamReader = null;
		}
	}

	/* Input Stream con costruzione del buffer incrementale */
	
	public boolean setInputStreamLazyBuffer(String idTransazione) throws MessageException{
		
		if(this._countingInputStream==null) {
			return false;
		}
		if(this.content != null || this.contentBuffer!=null) {
			return false;
		}
		if(! (this._countingInputStream instanceof OpenSPCoop2InputStreamDynamicContent) ) {
			DumpByteArrayOutputStream contentBuffer = new DumpByteArrayOutputStream(
					AbstractBaseOpenSPCoop2MessageDynamicContent.soglia,
					AbstractBaseOpenSPCoop2MessageDynamicContent.repositoryFile, idTransazione,
					this.getMessageRole().name());
			this._countingInputStream = new OpenSPCoop2InputStreamDynamicContent(this._countingInputStream, contentBuffer);
		}
		return true; // se è già OpenSPCoop2InputStreamDynamicContent torno true

	}
	
	/* Contenuto */

	private synchronized void initializeContent(boolean readOnly, String idTransazione) throws MessageException {
		if (this.hasContent) {
			if (this.content == null) {

				if (readOnly && this.supportReadOnly) {
					if(this._countingInputStream instanceof OpenSPCoop2InputStreamDynamicContent) {
						// se e' un OpenSPCoop2InputStreamDynamicContent dovrebbe gia' essere stato inizializzato il buffer
						this._countingInputStream = ((OpenSPCoop2InputStreamDynamicContent)this._countingInputStream).getWrappedInputStream();
						this.contentBuffer = ((OpenSPCoop2InputStreamDynamicContent)this._countingInputStream).getBuffer();
					}
					else { 
						this.contentBuffer = new DumpByteArrayOutputStream(
								AbstractBaseOpenSPCoop2MessageDynamicContent.soglia,
								AbstractBaseOpenSPCoop2MessageDynamicContent.repositoryFile, idTransazione,
								this.getMessageRole().name());
					}
					try {
						CopyStream.copy(this._countingInputStream, this.contentBuffer); // se e' un OpenSPCoop2InputStreamDynamicContent riverso quello che rimane nel buffer
						this.content = this.buildContent(this.contentBuffer);
					} catch (Throwable t) {
						MessageUtils.registerParseException(this, t, true);
						throw new MessageException(t.getMessage(), t);
					} finally {
						try {
							this._countingInputStream.close();
						} catch (Exception eClose) {
						}
					}
				} else {
					try {
						this.content = this.buildContent();
					} catch (Throwable t) {
						MessageUtils.registerParseException(this, t, true);
						throw new MessageException(t.getMessage(), t);
					}
				}

			}
		}
	}

	@Override
	public boolean isContentBuilded() {
		return this.content != null;
	}

	public InputStream getInputStreamFromContentBuffer() throws MessageException {
		if(this.contentBuffer==null) {
			return null;
		}
		try {
			if(this.contentBuffer.isSerializedOnFileSystem()) {
				return new FileInputStream(this.contentBuffer.getSerializedFile());
			}
			else {
				return new ByteArrayInputStream(this.contentBuffer.toByteArray());
			}
		}catch(Exception e) {
			throw new MessageException(e.getMessage(),e);
		}
	}

	public InputStream getInputStream() {
		return this._countingInputStream;
	}

	protected InputStream _getInputStream() throws MessageException {
		if(this._countingInputStream instanceof OpenSPCoop2InputStreamDynamicContent) {
			OpenSPCoop2InputStreamDynamicContent di = ((OpenSPCoop2InputStreamDynamicContent)this._countingInputStream); 
			DumpByteArrayOutputStream bf = di.getBuffer();
			if(bf!=null && bf.size()>0) {
				this._countingInputStream = di.getWrappedInputStream();
				InputStream isBuffered = bf.getInputStream();
				try {
					InputStream isRemained = Utilities.normalizeStream(this._countingInputStream, false);
					if(di.isInputStreamConsumed() || (isRemained==null) ) {
						return isBuffered;
					}
					else {
						return new SequenceInputStream(isBuffered,isRemained);
					}
				}catch(Exception e) {
					throw new MessageException(e.getMessage(),e);
				}
			}
			else {
				return this._countingInputStream;
			}
		}
		else {
			return this._countingInputStream;
		}
	}
	
	public boolean hasContent() throws MessageException, MessageNotSupportedException {
		return this.hasContent;
	}

	public void initContent() throws MessageException,MessageNotSupportedException{
		getContent();
	}
	public void initContent(boolean readOnly, String idTransazione) throws MessageException,MessageNotSupportedException{
		getContent(readOnly, idTransazione);
	}
	
	public T getContent() throws MessageException, MessageNotSupportedException {
		return getContent(false, null);
	}

	public T getContent(boolean readOnly, String idTransazione) throws MessageException, MessageNotSupportedException {
		if (this.hasContent) {
			if (!readOnly) {
				boolean aggiornaContenuto = false;
				if (this.content != null && !this.contentUpdatable) {
					aggiornaContenuto = true;
				}
				this.contentUpdatable = true;
				if (aggiornaContenuto) {
					// contenuto precedentemente già creato in modalità read-only
					// il metodo ha bisogno che contentUpdatable sia a true
					setUpdatableContent();
				}
			}
			// nota l'assegnazione di contentUpdatable viene usata poi dentro
			// l'inizializzaizone del contenuto per rilasciare le risorse
			if (this.content == null) {
				this.initializeContent(readOnly, idTransazione);
			}
		}
		return this.content; // può tornare null
	}

	public String getContentAsString() throws MessageException, MessageNotSupportedException {
		return getContentAsString(false, null);
	}

	public String getContentAsString(boolean readOnly, String idTransazione)
			throws MessageException, MessageNotSupportedException {
		try {
			if (this.hasContent) {
				if (this.content == null) {
					this.initializeContent(readOnly, idTransazione);
				}

				if (!readOnly) {
					this.contentUpdatable = true;
				}

				return this.buildContentAsString();
			}
			return null;
		} catch (MessageException e) {
			throw e;
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}

	public byte[] getContentAsByteArray() throws MessageException, MessageNotSupportedException {
		return getContentAsByteArray(false, null);
	}

	public byte[] getContentAsByteArray(boolean readOnly, String idTransazione)
			throws MessageException, MessageNotSupportedException {
		try {
			if (this.hasContent) {
				if (this.content == null) {
					this.initializeContent(readOnly, idTransazione);
				}

				if (!readOnly) {
					this.contentUpdatable = true;
				}

				return this.buildContentAsByteArray();
			}
			return null;
		} catch (MessageException e) {
			throw e;
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}

	public void updateContent(T content) throws MessageException, MessageNotSupportedException {
		this.content = content;
		this.contentUpdatable = true;
		if (this.contentBuffer != null) {
			this.contentBuffer.clearResources();
			this.contentBuffer = null;
		}
		if (this.content != null) {
			this.hasContent = true;
		} else {
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

	public void writeTo(OutputStream os, boolean consume, boolean readOnly, String idTransazione)
			throws MessageException {
		writeTo(os, consume, readOnly, idTransazione, null);
	}

	public void writeTo(OutputStream os, boolean consume, boolean readOnly, String idTransazione, StringBuilder debug)
			throws MessageException {
		try {
			if (this.hasContent) {

				if (!consume && this.content == null) {
					if (!readOnly) {
						this.contentUpdatable = true; // riverso soap header eventuale nel content che verrà costruito
					}
					this.initializeContent(readOnly, idTransazione); // per poi entrare nel ramo sotto serializeContent
				}

				CountingOutputStream cos = new CountingOutputStream(os);
				if (this.contentBuffer != null && !this.contentUpdatable) {
					if (this.soapStreamReader != null && this.soapStreamReader.isSoapHeaderModified()
							&& this.contentType != null) {
						if (debug != null) {
							debug.append(Costanti.WRITE_MODE_SERIALIZE_BUFFER_WITH_HEADER);
						}
						this.soapStreamReader.writeOptimizedHeaderTo(this.contentBuffer.getInputStream(), cos, true);
					} else {
						if (debug != null) {
							debug.append(Costanti.WRITE_MODE_SERIALIZE_BUFFER);
						}
						this.contentBuffer.writeTo(cos);
					}
				} else if (this.content != null) {
					if (debug != null) {
						debug.append(Costanti.WRITE_MODE_SERIALIZE_CONTENT);
					}
					this.serializeContent(cos, consume);
				} else {
					if (this.soapStreamReader != null && this.soapStreamReader.isSoapHeaderModified()) {
						if (debug != null) {
							debug.append(Costanti.WRITE_MODE_SERIALIZE_STREAM_WITH_HEADER);
						}
						this.soapStreamReader.writeOptimizedHeaderTo(this._getInputStream(), cos, true);
					} else {
						if (debug != null) {
							debug.append(Costanti.WRITE_MODE_SERIALIZE_STREAM);
						}
						Utilities.copy(this._getInputStream(), cos);
						this._getInputStream().close();
					}
				}
				this.outgoingsize = cos.getByteCount();
			}
		} catch (MessageException e) {
			throw e;
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		} finally {
			if (consume) {
				try {
					if (this.contentBuffer != null) {
						this.contentBuffer.unlock();
						this.contentBuffer.clearResources();
						this.contentBuffer = null;
					}
				} catch (Throwable t) {
				}
			}
		}
	}

	@Override
	public void saveChanges() throws MessageException {
		// nop;
	}

	@Override
	public boolean saveRequired() {
		return false;
	}

	/* Content Length */

	@Override
	public long getIncomingMessageContentLength() {
		if (this._countingInputStream != null) {
			return this._countingInputStream.getCount();
		} else {
			return super.getIncomingMessageContentLength();
		}
	}

}
