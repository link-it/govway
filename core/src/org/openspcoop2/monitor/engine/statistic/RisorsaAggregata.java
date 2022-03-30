/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.monitor.engine.exceptions.EngineException;
import org.openspcoop2.monitor.engine.transaction.TransactionContentUtils;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.statistic.StatisticFilter;

/**
 * RisorsaAggregata
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RisorsaAggregata {

	private IFilter filtro;
	private String valoreRisorsaAggregata;
	private List<StatisticFilter> filtri = new ArrayList<StatisticFilter>();
	
	public IFilter getFiltro() {
		return this.filtro;
	}
	public void setFiltro(IFilter filtro) {
		this.filtro = filtro;
	}
	public String getValoreRisorsaAggregata() {
		return this.valoreRisorsaAggregata;
	}
	public void setValoreRisorsaAggregata(String valoreRisorsaAggregata) throws EngineException {
		if(valoreRisorsaAggregata.length()>TransactionContentUtils.SOGLIA_VALUE_TOO_LONG){
			throw new EngineException("Valore è troppo grande (>"+TransactionContentUtils.SOGLIA_VALUE_TOO_LONG+") per essere utilizzato come filtro. Valore fornito: "+valoreRisorsaAggregata);
		}
		this.valoreRisorsaAggregata = valoreRisorsaAggregata;
	}
	public List<StatisticFilter> getFiltri() {
		return this.filtri;
	}
	public void setFiltri(List<StatisticFilter> filtri) {
		this.filtri = filtri;
	}
}
