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
package org.openspcoop2.protocol.manifest.utils;

import org.openspcoop2.protocol.manifest.constants.InterfaceType;

/**
 * Utility per {@link InterfaceType}. L'enum è una classe generata da XSD
 * (jakarta.xml.bind), quindi i comportamenti accessori vivono qui.
 *
 * @author Andrea Poli (apoli@link.it)
 */
public class InterfaceTypeUtils {

	private InterfaceTypeUtils() {
		// utility class
	}

	/**
	 * Indica se l'enum identifica un dialetto LLM (es. OpenAI Chat Completions,
	 * Anthropic Messages).
	 */
	public static boolean isLLM(InterfaceType type) {
		return type == InterfaceType.OPENAI_CHAT_V1 || type == InterfaceType.ANTHROPIC_MESSAGES_V1;
	}

}
