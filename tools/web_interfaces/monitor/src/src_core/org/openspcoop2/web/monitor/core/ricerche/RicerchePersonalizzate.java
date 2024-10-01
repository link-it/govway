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
import java.util.List;

import org.openspcoop2.web.lib.users.dao.RicercaUtente;

/****
 * RicerchePersonalizzate
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class RicerchePersonalizzate implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<RicercaUtente> ricerche;
	

	public List<RicercaUtente> getRicerche() {
		return this.ricerche;
	}

	public void setRicerche(List<RicercaUtente> ricerche) {
		this.ricerche = ricerche;
	}
}
