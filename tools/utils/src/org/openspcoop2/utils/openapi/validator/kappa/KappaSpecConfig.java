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

package org.openspcoop2.utils.openapi.validator.kappa;

import org.openspcoop2.utils.rest.AbstractApiSpecConfig;

/**
 * Configurazione per il validatore di specifica basato su kappa
 * (com.github.erosb), che supporta sia OpenAPI 3.0 che 3.1.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 */
public class KappaSpecConfig extends AbstractApiSpecConfig {

	public static final String ENGINE = "kappa";

	@Override
	public String getEngine() {
		return ENGINE;
	}

}
