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
 * {@code OpenAPI31ValidazioneMultipartRequest}: stessa logica funzionale, profilo OpenAPI 3.1
 * (libreria di validazione di default = kappa).
 * <p>
 * I messaggi di errore prodotti da kappa differiscono da openapi4j: i metodi {@code err*()}
 * sono overridati una volta scoperti i pattern reali dai log della testsuite.
 *
 * @author Poli Andrea (apoli@link.it)
 */
public class OpenAPI31_MultipartRequestTest extends OpenAPI30_MultipartRequestTest {

	@Override
	protected String getApiName() {
		return "OpenAPI31ValidazioneMultipartRequest";
	}

	// ---- Messaggi di errore prodotti da kappa (json-sKema 2020-12). ----
	// Differiscono dal formato openapi4j per fraseologia ma matchano lo stesso scenario funzionale.

	/** kappa emette "false schema always fails" per la proprietà extra (additionalProperties=false). */
	@Override
	protected String errAdditionalProperty() {
		return "body.docOther: false schema always fails";
	}

	@Override
	protected String errMissingFieldAltro() {
		return "body.metadati: required properties are missing: altro";
	}

	/**
	 * Il test contenuto2 invia un discriminator violato annidato in {@code metadati.pet}: kappa
	 * (patch nested-discriminator) effettua un walk pre-validazione e intercetta la violazione
	 * prima della validazione standard di json-sKema, emettendo un diagnostico esplicito sul path
	 * nested allineato al wording di openapi4j ("Schema selection can't be made...").
	 */
	@Override
	protected String errDiscriminator() {
		return "body.metadati.pet: Schema selection can''t be made for discriminator ''pet_type'' with value ''CatErrato''";
	}

	@Override
	protected String errMissingFieldDocPdf() {
		return "body: required properties are missing: docPdf";
	}

	/**
	 * kappa riporta il content type completo (incluso boundary): cerchiamo solo la parte fissa
	 * ("body: Content type ''multipart/<subtypeWrong>; boundary=") che precede il boundary dinamico.
	 * Gli apici sono raddoppiati per l'escape in SQL Oracle LIKE.
	 */
	@Override
	protected String errContentTypeNotAllowed(String subtypeWrong) {
		return "body: Content type ''multipart/"+subtypeWrong+"; boundary=";
	}
}
