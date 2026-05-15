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

/**
 * Costanti per gli identificativi di provider LLM. Sostituiranno, in futuro,
 * la lookup nel catalogo Provider Binding. Per il prototipo è una piccola
 * lista hardcoded con il solo Anthropic API diretta.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public final class LLMProviders {

	/** Anthropic API diretta (https://api.anthropic.com). */
	public static final String ANTHROPIC = "anthropic";

	private LLMProviders() {
		// utility class
	}
}
