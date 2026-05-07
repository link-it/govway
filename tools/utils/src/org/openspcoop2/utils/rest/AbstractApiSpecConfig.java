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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Base condivisa per le configurazioni dei validator di specifica.
 * Espone la stessa convenzione di {@link ApiValidatorConfig}: le
 * sottoclassi popolano la mappa interna tramite
 * {@link #setProperty(String, String)} e ricostruiscono lo stato in
 * {@link #readProperties(UnaryOperator)}.
 *
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 */
public abstract class AbstractApiSpecConfig implements IApiSpecConfig {

	private final Map<String, String> properties = new HashMap<>();

	protected void setProperty(String name, String value) {
		this.properties.put(name, value);
	}

	/**
	 * @return la mappa delle proprietà raccolte (read-only).
	 *         La mappa può essere passata come provider a
	 *         {@link #readProperties(UnaryOperator)} su una nuova
	 *         istanza per ottenere un config equivalente.
	 */
	@Override
	public Map<String, String> mapProperties() {
		return Collections.unmodifiableMap(this.properties);
	}

	@Override
	public void readProperties(UnaryOperator<String> propertyProvider) {
		// no-op: le sottoclassi che hanno properties significative la sovrascrivono
	}

}
