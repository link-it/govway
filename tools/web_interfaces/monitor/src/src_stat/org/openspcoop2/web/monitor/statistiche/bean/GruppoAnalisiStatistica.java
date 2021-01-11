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

package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;
import java.util.List;

/**
 * GruppoAnalisiStatistica
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class GruppoAnalisiStatistica implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<AnalisiStatistica> listaAnalisiStatistica;
	private String label;
	
	public List<AnalisiStatistica> getListaAnalisiStatistica() {
		return this.listaAnalisiStatistica;
	}
	public void setListaAnalisiStatistica(List<AnalisiStatistica> listaAnalisiStatistica) {
		this.listaAnalisiStatistica = listaAnalisiStatistica;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
