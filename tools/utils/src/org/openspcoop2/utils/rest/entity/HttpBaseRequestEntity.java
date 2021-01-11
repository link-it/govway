/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.rest.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpBaseRequestEntity
 *
 *
 * @author Poli Andrea (a	poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class HttpBaseRequestEntity<T> extends HttpBaseEntity<T> {
	
	/* ---- Coppie nome/valori di invocazione della query --- */
	private Map<String, String> parametersQuery = new HashMap<>();
	
	public Map<String, String> getParametersQuery() {
		return this.parametersQuery;
	}
	public void setParametersQuery(Map<String, String> parametersQuery) {
		this.parametersQuery = parametersQuery;
	}
}
