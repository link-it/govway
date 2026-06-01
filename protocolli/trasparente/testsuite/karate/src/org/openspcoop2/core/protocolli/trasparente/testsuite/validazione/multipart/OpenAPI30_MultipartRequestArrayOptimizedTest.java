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
 * Variante ottimizzata di {@link OpenAPI30_MultipartRequestArrayTest}: esercita l'API
 * {@code OpenAPIValidazioneMultipartRequestOpt} con
 * {@code validation.openApi.validateMultipartOptimization=true} configurato sulle porte.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenAPI30_MultipartRequestArrayOptimizedTest extends OpenAPI30_MultipartRequestArrayTest {

	@Override
	protected String getApiName() {
		return "OpenAPIValidazioneMultipartRequestOpt";
	}

	/**
	 * Le risorse {@code /multipart/{form-data,mixed}-array-binary} hanno uno schema che dichiara
	 * solo proprietà di formato binary. Con l'ottimizzazione attiva, il loop di lettura non parte
	 * perché {@code notBinaries} è vuoto sin dall'inizio, il body risulta privo della proprietà
	 * {@code archivi} e la validazione fallisce. Il diagnostico atteso è quello di un campo
	 * dichiarato {@code array} ma trovato non array nel body parsato.
	 */
	@Override
	protected String errArrayBinaryOnlySchemaOptimization() {
		return "body.archivi: Type expected ''array'', found ''string''. (code: 1027)";
	}
}
