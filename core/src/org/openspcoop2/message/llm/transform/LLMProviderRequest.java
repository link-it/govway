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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Risultato della trasformazione canonical → formato provider, contenente:
 * <ul>
 *   <li>il {@code body} serializzato pronto per l'invio HTTP</li>
 *   <li>gli {@code headers} HTTP statici richiesti dal provider (es. per Anthropic
 *       l'header {@code anthropic-version} obbligatorio)</li>
 * </ul>
 * <p>
 * Non include header di sicurezza/autenticazione (API key, Bearer token): quelli
 * sono dominio della configurazione del connettore GovWay, non della trasformazione.
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMProviderRequest {

	private final byte[] body;
	private final Map<String, String> headers;

	public LLMProviderRequest(byte[] body) {
		this(body, new LinkedHashMap<>());
	}

	public LLMProviderRequest(byte[] body, Map<String, String> headers) {
		this.body = body;
		this.headers = headers != null ? headers : new LinkedHashMap<>();
	}

	public byte[] getBody() {
		return this.body;
	}

	public Map<String, String> getHeaders() {
		return this.headers;
	}
}
