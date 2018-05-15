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
