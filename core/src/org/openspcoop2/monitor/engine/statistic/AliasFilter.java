/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.monitor.sdk.statistic.StatisticFilterName;

import org.openspcoop2.generic_project.beans.IAliasTableField;

/**
 * AliasFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AliasFilter {

	IAliasTableField nomeFiltro;
	IAliasTableField valoreFiltro;
	StatisticFilterName statisticFilter;
	
	public IAliasTableField getNomeFiltro() {
		return this.nomeFiltro;
	}
	public void setNomeFiltro(IAliasTableField nomeFiltro) {
		this.nomeFiltro = nomeFiltro;
	}
	public IAliasTableField getValoreFiltro() {
		return this.valoreFiltro;
	}
	public void setValoreFiltro(IAliasTableField valoreFiltro) {
		this.valoreFiltro = valoreFiltro;
	}
	public StatisticFilterName getStatisticFilterName() {
		return this.statisticFilter;
	}
	public void setStatisticFilterName(StatisticFilterName statisticFilter) {
		this.statisticFilter = statisticFilter;
	}
}
