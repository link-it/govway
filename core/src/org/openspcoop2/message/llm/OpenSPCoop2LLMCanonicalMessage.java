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
package org.openspcoop2.message.llm;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.rest.AbstractBaseOpenSPCoop2RestMessage;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.io.DumpByteArrayOutputStream;
import org.openspcoop2.utils.json.JSONUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

/**
 * Messaggio interno di GovLLM che ospita il modello canonical
 * (CanonicalChatRequest sul lato request, CanonicalChatResponse sul lato response).
 * <p>
 * Estende {@link AbstractBaseOpenSPCoop2RestMessage} con {@code T = Object}
 * perché lo stesso tipo Java ospita due forme distinte di payload. I metodi
 * helper {@link #asRequest()} / {@link #asResponse()} facilitano il cast.
 * </p>
 * <p>
 * Per la (de)serializzazione canonical viene usato l'ObjectMapper condiviso
 * di {@link JSONUtils}: il deserializzatore è arricchito con tolleranza alle
 * proprietà sconosciute per resistere all'evoluzione dei dialetti dei provider
 * (campo nuovo nel payload provider che oggi non mappiamo ancora).
 * </p>
 * <p>
 * La serializzazione provider-aware (canonical → JSON Anthropic/OpenAI) è
 * demandata ai trasformatori outbound, non vive in questa classe.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class OpenSPCoop2LLMCanonicalMessage extends AbstractBaseOpenSPCoop2RestMessage<Object> {

	public OpenSPCoop2LLMCanonicalMessage(OpenSPCoop2MessageFactory messageFactory) {
		super(messageFactory);
		this.supportReadOnly = true;
	}

	public OpenSPCoop2LLMCanonicalMessage(OpenSPCoop2MessageFactory messageFactory, InputStream is, String contentType) throws MessageException {
		super(messageFactory, is, contentType);
		this.supportReadOnly = true;
	}


	/* === parsing da InputStream / buffer === */

	@Override
	protected Object buildContent() throws MessageException {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			CopyStream.copy(this._getInputStream(), bout);
			bout.flush();
			bout.close();
			return parseCanonical(bout.toByteArray());
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		} finally {
			try {
				this._getInputStream().close();
			} catch (Exception eClose) {
				// close
			}
		}
	}

	@Override
	protected Object buildContent(DumpByteArrayOutputStream contentBuffer) throws MessageException {
		try {
			return parseCanonical(contentBuffer.toByteArray());
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}

	/**
	 * Parsa un payload JSON canonical-format in CanonicalChatRequest o CanonicalChatResponse,
	 * discriminando in base alla presenza di campi caratteristici:
	 * <ul>
	 *   <li>presenza di {@code messages} (top-level) → request</li>
	 *   <li>presenza di {@code stop_reason} o {@code content} (top-level) → response</li>
	 * </ul>
	 * Nota: la pipeline GovLLM normalmente non passa per questo parser; le istanze
	 * canonical sono costruite dai trasformatori inbound. Questo metodo è fallback
	 * per debug/audit/test (round-trip JSON).
	 */
	private Object parseCanonical(byte[] raw) throws MessageException {
		try {
			if (raw == null || raw.length == 0) {
				return null;
			}
			ObjectMapper mapper = JSONUtils.getObjectMapper();
			JsonNode root = mapper.readTree(raw);
			Class<?> target;
			if (root.has("messages")) {
				target = CanonicalChatRequest.class;
			} else if (root.has("stop_reason") || root.has("content")) {
				target = CanonicalChatResponse.class;
			} else {
				throw new MessageException("Payload canonical non riconosciuto: assenti i campi discriminanti 'messages'/'stop_reason'/'content'");
			}
			// tolleranza ai campi sconosciuti: applicata localmente, senza alterare il mapper globale
			ObjectReader reader = mapper.readerFor(target)
					.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			return reader.readValue(root);
		} catch (MessageException e) {
			throw e;
		} catch (Exception e) {
			throw new MessageException("Parsing canonical JSON fallito: " + e.getMessage(), e);
		}
	}


	/* === serializzazione === */

	@Override
	protected String buildContentAsString() throws MessageException {
		try {
			if (this.content == null) {
				return "";
			}
			return JSONUtils.getObjectMapper().writeValueAsString(this.content);
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}

	@Override
	protected byte[] buildContentAsByteArray() throws MessageException {
		try {
			if (this.content == null) {
				return new byte[0];
			}
			return JSONUtils.getObjectMapper().writeValueAsBytes(this.content);
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}

	@Override
	protected void serializeContent(OutputStream os, boolean consume) throws MessageException {
		try {
			if (this.content == null) {
				return;
			}
			JSONUtils.getObjectMapper().writeValue(os, this.content);
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}


	/* === helper di accesso typed === */

	public static OpenSPCoop2LLMCanonicalMessage forRequest(OpenSPCoop2MessageFactory factory, CanonicalChatRequest request) throws MessageException, org.openspcoop2.message.exception.MessageNotSupportedException {
		OpenSPCoop2LLMCanonicalMessage msg = new OpenSPCoop2LLMCanonicalMessage(factory);
		msg.updateContent(request);
		return msg;
	}

	public static OpenSPCoop2LLMCanonicalMessage forResponse(OpenSPCoop2MessageFactory factory, CanonicalChatResponse response) throws MessageException, org.openspcoop2.message.exception.MessageNotSupportedException {
		OpenSPCoop2LLMCanonicalMessage msg = new OpenSPCoop2LLMCanonicalMessage(factory);
		msg.updateContent(response);
		return msg;
	}

	public boolean isRequest() throws MessageException {
		try {
			return this.getContent() instanceof CanonicalChatRequest;
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}

	public boolean isResponse() throws MessageException {
		try {
			return this.getContent() instanceof CanonicalChatResponse;
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}

	public CanonicalChatRequest asRequest() throws MessageException {
		try {
			Object c = this.getContent();
			if (c == null) {
				return null;
			}
			if (c instanceof CanonicalChatRequest) {
				return (CanonicalChatRequest) c;
			}
			throw new MessageException("Il messaggio canonical non ospita una request (tipo effettivo: " + c.getClass().getName() + ")");
		} catch (MessageException e) {
			throw e;
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}

	public CanonicalChatResponse asResponse() throws MessageException {
		try {
			Object c = this.getContent();
			if (c == null) {
				return null;
			}
			if (c instanceof CanonicalChatResponse) {
				return (CanonicalChatResponse) c;
			}
			throw new MessageException("Il messaggio canonical non ospita una response (tipo effettivo: " + c.getClass().getName() + ")");
		} catch (MessageException e) {
			throw e;
		} catch (Exception e) {
			throw new MessageException(e.getMessage(), e);
		}
	}
}
