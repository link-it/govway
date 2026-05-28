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

package org.openspcoop2.utils.openapi.validator;

import java.io.Serializable;

/**
 * OpenAPILibrary
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum OpenAPILibrary implements Serializable {
	json_schema,
	openapi4j,
	swagger_request_validator,
	kappa;

	/**
	 * @return {@code true} se la libreria supporta le specifiche Swagger 2.0.
	 *         Le librerie {@link #openapi4j} e {@link #kappa} sono OpenAPI 3.x-only
	 *         e non sanno parsare un documento Swagger 2.
	 */
	public boolean supportsSwagger2() {
		switch (this) {
			case json_schema:
				return true;
			case swagger_request_validator:
				return true;
			case openapi4j:
				return false;
			case kappa:
				return false;
			default:
				throw unhandled();
		}
	}

	/**
	 * @return {@code true} se la libreria supporta le specifiche OpenAPI 3.0.
	 *         La libreria {@link #kappa} si basa sul parser JSON Schema 2020-12 strict
	 *         (json-sKema) che rifiuta alcuni costrutti 3.0 legacy (es.
	 *         {@code exclusiveMaximum: true} accoppiato a {@code maximum: <number>},
	 *         {@code nullable: true}); per questo motivo kappa è considerata 3.1-only
	 *         ai fini della selezione del motore.
	 */
	public boolean supportsOpenApi30() {
		switch (this) {
			case json_schema:
				return true;
			case openapi4j:
				return true;
			case swagger_request_validator:
				return true;
			case kappa:
				return false;
			default:
				throw unhandled();
		}
	}

	/**
	 * @return {@code true} se la libreria supporta le specifiche OpenAPI 3.1.
	 *         Le librerie {@link #openapi4j} e {@link #swagger_request_validator}
	 *         non supportano i costrutti introdotti in 3.1 (type-array, const,
	 *         exclusiveMinimum/Maximum numerici, prefixItems, if/then/else,
	 *         dependentRequired, unevaluatedProperties, $dynamicRef/$dynamicAnchor,
	 *         webhooks, ...).
	 */
	public boolean supportsOpenApi31() {
		switch (this) {
			case json_schema:
				return true;
			case kappa:
				return true;
			case openapi4j:
				return false;
			case swagger_request_validator:
				return false;
			default:
				throw unhandled();
		}
	}

	private IllegalStateException unhandled() {
		return new IllegalStateException("OpenAPILibrary value '" + this.name()
				+ "' non gestito: aggiornare i metodi supportsSwagger2/supportsOpenApi30/supportsOpenApi31 quando si aggiunge un nuovo engine.");
	}
}
