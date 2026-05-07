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

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Configurazione di un {@link IApiSpecValidator}. Ogni implementazione
 * dichiara l'engine a cui appartiene tramite {@link #getEngine()}; la
 * stringa restituita è la stessa usata da
 * {@link ApiFactory#newApiSpecValidator(String)} per istanziare il
 * validatore corrispondente.
 *
 * Le proprietà raccolte da {@link #mapProperties()} possono essere passate
 * come provider a {@link #readProperties(UnaryOperator)} su una nuova
 * istanza per ottenere un config equivalente.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 */
public interface IApiSpecConfig {

	String getEngine();

	Map<String, String> mapProperties();

	void readProperties(UnaryOperator<String> propertyProvider);

}
