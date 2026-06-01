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
 * Eredita tutti i test di {@link OpenAPI30_MultipartRequestArrayTest} ma li esercita sull'API
 * {@code OpenAPI31ValidazioneMultipartRequest}: profilo OpenAPI 3.1 (libreria kappa).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenAPI31_MultipartRequestArrayTest extends OpenAPI30_MultipartRequestArrayTest {

	@Override
	protected String getApiName() {
		return "OpenAPI31ValidazioneMultipartRequest";
	}

	/** kappa (json-sKema 2020-12) emette "required properties are missing" anziché "Field is required" di openapi4j. */
	@Override
	protected String errArrayMissingFieldAltro() {
		return "body.archivi.0: required properties are missing: altro";
	}
}
