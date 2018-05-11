package org.openspcoop2.monitor.sdk.condition;

import org.openspcoop2.monitor.sdk.constants.StatisticType;

/**
 * StatisticsContext
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface StatisticsContext extends Context {

	public StatisticType getStatisticType();
	
}
