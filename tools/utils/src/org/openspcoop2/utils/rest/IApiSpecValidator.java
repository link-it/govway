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

package org.openspcoop2.utils.rest;

import org.openspcoop2.utils.rest.api.Api;
import org.slf4j.Logger;

/**
 * Validatore della specifica di un'API (meta-schema). Distinto da
 * {@link IApiValidator}, che si occupa invece della validazione delle
 * richieste/risposte HTTP a runtime.
 *
 * Ogni implementazione è legata a un engine (es. openapi4j, kappa) ed è
 * istanziata da {@link ApiFactory#newApiSpecValidator(String)}. Le
 * implementazioni controllano in {@link #init(Logger, IApiSpecConfig)}
 * che il config ricevuto sia del sottotipo atteso e altrimenti lanciano
 * {@link ProcessingException}.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 */
public interface IApiSpecValidator {

	void init(Logger log, IApiSpecConfig config) throws ProcessingException;

	void validate(Logger log, Api api) throws ProcessingException, ParseWarningException;

	void close(Logger log) throws ProcessingException;

}
