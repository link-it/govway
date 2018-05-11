package org.openspcoop2.monitor.sdk.statistic;

import org.openspcoop2.monitor.sdk.statistic.StatisticFilterName;


/**
 * StatisticFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticFilter {

	private String resourceID;
	private Object value;
	private StatisticFilterName statisticFilter;
	
	public StatisticFilterName getStatisticFilterName() {
		return this.statisticFilter;
	}
	public void setStatisticFilterName(StatisticFilterName statisticFilter) {
		this.statisticFilter = statisticFilter;
	}
	public String getResourceID() {
		return this.resourceID;
	}
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	public Object getValue() {
		return this.value;
	}
	public void setValue(Object value) {
		this.value = value;
	}

}
