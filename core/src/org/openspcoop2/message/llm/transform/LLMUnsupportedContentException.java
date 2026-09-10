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
package org.openspcoop2.message.llm.transform;

import java.util.Arrays;
import java.util.List;

/**
 * Contenuto della richiesta non gestito dal modello canonical (allegati: immagini, documenti,
 * audio, video). Distinta da {@link LLMTransformException} perché non è un errore di
 * elaborazione del gateway ma una richiesta che il gateway non supporta: va restituita al
 * client come errore 4xx nell'envelope del suo dialetto.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMUnsupportedContentException extends LLMTransformException {

	private static final long serialVersionUID = 1L;

	/** Tipi di contenuto che il client usa per veicolare un allegato, nei due dialetti front-door. */
	private static final List<String> ATTACHMENT_CONTENT_TYPES = Arrays.asList(
			"image", "image_url", "document", "file", "input_file", "input_audio", "audio", "video");

	private static final String CLIENT_MESSAGE_ATTACHMENTS = "Allegati non supportati";

	private final String contentType;
	private final String clientMessage;

	private LLMUnsupportedContentException(String contentType, String message, String clientMessage) {
		super(message);
		this.contentType = contentType;
		this.clientMessage = clientMessage;
	}

	public static LLMUnsupportedContentException forContentType(String contentType) {
		String type = contentType != null ? contentType : "<assente>";
		boolean attachment = contentType != null && ATTACHMENT_CONTENT_TYPES.contains(contentType);
		String clientMessage = attachment
				? CLIENT_MESSAGE_ATTACHMENTS
				: ("Tipo di contenuto non supportato: '" + type + "'");
		return new LLMUnsupportedContentException(contentType,
				"Tipo di contenuto non supportato nella richiesta: '" + type + "'", clientMessage);
	}

	public String getContentType() {
		return this.contentType;
	}

	/** Messaggio da restituire al client, senza dettagli interni del gateway. */
	public String getClientMessage() {
		return this.clientMessage;
	}
}
