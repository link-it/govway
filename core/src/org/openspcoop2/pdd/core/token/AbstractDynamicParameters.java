/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.token;

import java.util.Map;

import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.protocol.sdk.state.RequestInfo;

/**
 * DynamicParameter
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractDynamicParameters {

	private Map<String, Object> dynamicMap;
	private PdDContext pddContext;
	private RequestInfo requestInfo;
	
	public AbstractDynamicParameters(Map<String, Object> dynamicMap, PdDContext pddContext, RequestInfo requestInfo) {
		this.dynamicMap = dynamicMap;
		this.pddContext = pddContext;
		this.requestInfo = requestInfo;
	}
	
	public Map<String, Object> getDynamicMap() {
		return this.dynamicMap;
	}

	public PdDContext getPddContext() {
		return this.pddContext;
	}
	
	public RequestInfo getRequestInfo() {
		return this.requestInfo;
	}
	
	@Override
	public String toString() {
		return toStringRepresentation();
	}
	protected abstract String toStringRepresentation();
}
