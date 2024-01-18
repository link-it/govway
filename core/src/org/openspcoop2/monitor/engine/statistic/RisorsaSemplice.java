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
package org.openspcoop2.monitor.engine.statistic;

import org.openspcoop2.monitor.sdk.statistic.StatisticResourceFilter;

import java.util.ArrayList;
import java.util.List;


/**
 * RisorsaSemplice
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RisorsaSemplice {

	private String idStatistica;
	private String idRisorsa;
	private List<StatisticResourceFilter> filtri = new ArrayList<StatisticResourceFilter>();
	
	public String getIdStatistica() {
		return this.idStatistica;
	}
	public void setIdStatistica(String idStatistica) {
		this.idStatistica = idStatistica;
	}
	public String getIdRisorsa() {
		return this.idRisorsa;
	}
	public void setIdRisorsa(String idRisorsa) {
		this.idRisorsa = idRisorsa;
	}
	public List<StatisticResourceFilter> getFiltri() {
		return this.filtri;
	}
	public void setFiltri(List<StatisticResourceFilter> filtri) {
		this.filtri = filtri;
	}
}
