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
package org.openspcoop2.web.monitor.core.ricerche;

import java.io.Serializable;
import java.util.Map;

/****
 * FiltriRicercaPersonalizzata
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class FiltriRicercaPersonalizzata implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Map<String, Object> filtri;
	
	public Map<String, Object> getFiltri() {
		return this.filtri;
	}

	public void setFiltri(Map<String, Object> filtri) {
		this.filtri = filtri;
	}
}
