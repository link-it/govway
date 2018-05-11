package org.openspcoop2.monitor.sdk.plugins;

import org.openspcoop2.monitor.sdk.condition.IStatisticFilter;
import org.openspcoop2.monitor.sdk.condition.StatisticsContext;
import org.openspcoop2.monitor.sdk.constants.StatisticType;
import org.openspcoop2.monitor.sdk.exceptions.StatisticException;
import org.openspcoop2.monitor.sdk.statistic.IStatistic;

import java.util.List;

/**
 * IStatisticProcessing
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IStatisticProcessing extends ISearchArguments {
	
	public List<StatisticType> getEnabledStatisticType();
	
	// popolamento
	
	public void createHourlyStatisticData(IStatistic context) throws StatisticException;
	public void createDailyStatisticData(IStatistic context) throws StatisticException;
	public void createWeeklyStatisticData(IStatistic context) throws StatisticException;
	public void createMonthlyStatisticData(IStatistic context) throws StatisticException;
	
	
	// estrazione
	public IStatisticFilter createSearchFilter(StatisticsContext context) throws StatisticException;
	
}
