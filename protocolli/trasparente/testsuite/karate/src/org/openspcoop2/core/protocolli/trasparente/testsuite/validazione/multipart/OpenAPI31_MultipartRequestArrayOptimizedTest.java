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
package org.openspcoop2.core.protocolli.trasparente.testsuite.validazione.multipart;

/**
 * Variante OpenAPI 3.1 + ottimizzazione di {@link OpenAPI30_MultipartRequestArrayTest}.
 * Esercita l'API {@code OpenAPI31ValidazioneMultipartRequestOpt}.
 *
 * @author Poli Andrea (apoli@link.it)
 */
public class OpenAPI31_MultipartRequestArrayOptimizedTest extends OpenAPI31_MultipartRequestArrayTest {

	@Override
	protected String getApiName() {
		return "OpenAPI31ValidazioneMultipartRequestOpt";
	}

	/**
	 * Vedi note in {@link OpenAPI30_MultipartRequestArrayOptimizedTest#errArrayBinaryOnlySchemaOptimization()}.
	 * kappa (json-sKema 2020-12) emette "expected type: array, actual: string" anziché la fraseologia
	 * "Type expected 'array', found 'string'" di openapi4j.
	 */
	@Override
	protected String errArrayBinaryOnlySchemaOptimization() {
		return "body.archivi: expected type: array, actual: string";
	}
}
