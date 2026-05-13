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
	 * @return {@code true} se la libreria supporta le specifiche OpenAPI 3.1.
	 *         Le librerie {@link #openapi4j} e {@link #swagger_request_validator}
	 *         non supportano i costrutti introdotti in 3.1 (type-array, const,
	 *         exclusiveMinimum/Maximum numerici, prefixItems, if/then/else,
	 *         dependentRequired, unevaluatedProperties, $dynamicRef/$dynamicAnchor,
	 *         webhooks, ...).
	 */
	public boolean supportsOpenApi31() {
		return this == kappa || this == json_schema;
	}
}
