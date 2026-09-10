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

import org.openspcoop2.utils.Map;
import org.openspcoop2.utils.MapKey;

/**
 * Chiavi di contesto del gateway LLM che devono essere leggibili anche fuori dal modulo
 * pdd (es. dal builder degli errori applicativi, che vive nel modulo protocol e deve
 * riconoscere una transazione LLM per restituire l'errore nel dialetto del client).
 * <p>
 * {@link Map#newMapKey(String)} interna le chiavi per stringa, quindi la {@link MapKey}
 * qui definita è la stessa istanza usata dagli handler LLM
 * ({@code LLMHandlerConstants} vi delega).
 * </p>
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class LLMContextKeys {

	private LLMContextKeys() {
	}

	/** Dialetto front-door dell'API LLM: presente solo sulle transazioni LLM. */
	public static final MapKey<String> PDD_CTX_LLM_FORMATO = Map.newMapKey("llm.formatoSpecifica");
}
