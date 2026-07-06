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
package org.openspcoop2.pdd.core.llm.pii;

/**
 * Punto di estensione (plugin) del PII Masking: un provider per ciascun tipo di detector
 * ({@code regexp}, e in futuro tipi personalizzati caricati per nome-classe). Dato il
 * config di una Regola PII costruisce il relativo {@link PiiRuleMasker}.
 *
 * <p>Una classe plugin utente implementa questa interfaccia e viene istanziata per nome
 * (property {@code llmPiiMasking.pluginClassName}) dal {@link PiiMaskerRegistry}.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public interface PiiMaskerProvider {

	/** Tipo di detector gestito (per i provider built-in, es. {@code regexp}). */
	String getType();

	/** Costruisce un masker per la Regola PII indicata. */
	PiiRuleMasker create(PiiRuleConfig rule) throws PiiMaskingException;
}
