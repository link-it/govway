package org.openspcoop2.monitor.sdk.statistic;

/**
 * StatisticResourceFilter
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticResourceFilter {

	private String resourceID;
	private StatisticFilterName statisticFilter;
	
	public String getResourceID() {
		return this.resourceID;
	}
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	public StatisticFilterName getStatisticFilterName() {
		return this.statisticFilter;
	}
	public void setStatisticFilterName(StatisticFilterName statisticFilter) {
		this.statisticFilter = statisticFilter;
	}
	
}
