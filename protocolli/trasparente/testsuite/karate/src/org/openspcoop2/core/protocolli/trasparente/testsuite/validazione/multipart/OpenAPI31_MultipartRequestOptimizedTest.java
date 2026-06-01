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
 * Variante OpenAPI 3.1 + ottimizzazione di {@link OpenAPI30_MultipartRequestTest}: esercita
 * l'API {@code OpenAPI31ValidazioneMultipartRequestOpt} con
 * {@code validation.openApi.validateMultipartOptimization=true} sulle porte. Libreria di
 * validazione di default = kappa.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenAPI31_MultipartRequestOptimizedTest extends OpenAPI31_MultipartRequestTest {

	@Override
	protected String getApiName() {
		return "OpenAPI31ValidazioneMultipartRequestOpt";
	}

	/** Vedi note in {@link OpenAPI30_MultipartRequestOptimizedTest#errAdditionalProperty()}. */
	@Override
	protected String errAdditionalProperty() {
		return null;
	}

	/** Vedi note in {@link OpenAPI30_MultipartRequestOptimizedTest#errMissingFieldDocPdf()}. */
	@Override
	protected String errMissingFieldDocPdf() {
		return null;
	}
}
