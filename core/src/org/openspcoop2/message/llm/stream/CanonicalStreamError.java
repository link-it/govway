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
package org.openspcoop2.message.llm.stream;

import org.openspcoop2.message.llm.CanonicalError;

/**
 * Errore segnalato dal provider a stream già avviato (HTTP 200 seguito da un evento di
 * errore): Anthropic {@code event: error}, Bedrock event-type {@code internalServerException}
 * / {@code modelStreamErrorException} / ..., OpenAI chunk con campo {@code error}.
 * <p>
 * Viene propagato al client nell'envelope di errore del dialetto front-door: senza questo
 * evento lo stream si chiuderebbe senza alcuna segnalazione e il client leggerebbe una
 * risposta troncata come se fosse completa.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class CanonicalStreamError extends CanonicalStreamEvent {

	private final CanonicalError error;

	public CanonicalStreamError(CanonicalError error) {
		super("error");
		this.error = error;
	}

	public CanonicalError getError() {
		return this.error;
	}
}
