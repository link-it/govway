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
 * Eredita tutti i test di {@link OpenAPI30_MultipartRequestTest} ma li esercita sull'API
 * {@code OpenAPIValidazioneMultipartRequestOpt}: stessa spec OAS 3.0, stessa libreria
 * (openapi4j di default) ma con la property port-level
 * {@code validation.openApi.validateMultipartOptimization=true} sulle 4 porte applicative e
 * sulle 4 porte delegate. Verifica che con l'ottimizzazione attiva il comportamento
 * funzionale resti identico al caso standard (a parte la performance del parsing multipart).
 *
 * @author Poli Andrea (apoli@link.it)
 */
public class OpenAPI30_MultipartRequestOptimizedTest extends OpenAPI30_MultipartRequestTest {

	@Override
	protected String getApiName() {
		return "OpenAPIValidazioneMultipartRequestOpt";
	}

	/**
	 * Con l'ottimizzazione il loop termina dopo aver letto le parti non-binarie, senza scorrere
	 * fino in fondo lo stream multipart: eventuali parti aggiuntive non dichiarate nello schema
	 * (es. {@code docOther}) non vengono individuate, quindi additionalProperties=false non è
	 * applicato. La richiesta viene accettata invece di essere respinta.
	 */
	@Override
	protected String errAdditionalProperty() {
		return null;
	}

	/**
	 * Con l'ottimizzazione le proprietà binary mancanti dalla richiesta vengono comunque "iniettate"
	 * nel post-loop usando il nome dello schema come valore: validazione passa anche se la parte
	 * richiesta (es. {@code docPdf}) non è stata effettivamente inviata.
	 */
	@Override
	protected String errMissingFieldDocPdf() {
		return null;
	}
}
