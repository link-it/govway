package org.openspcoop2.monitor.engine.statistic;

import org.openspcoop2.monitor.engine.statistic.CustomStatisticsSdkGenerator;
import org.openspcoop2.monitor.sdk.exceptions.StatisticException;
import org.openspcoop2.monitor.sdk.plugins.StatisticProcessing;
import org.openspcoop2.monitor.sdk.statistic.IStatistic;

/**
 * StatisticByState
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class StatisticByState extends StatisticProcessing {

	
	// plugins ConfigurazioneStatistica
	public static final String ID = "__StatisticByState__";
	public static final String LABEL = "Stati";
	public static final String DESCRIZIONE = "Informazione Statistica sugli Stati associati alle Transazioni";
	

	// popolamento

	@Override
	public void createHourlyStatisticData(IStatistic context) throws StatisticException {
		this.createStatisticData(context);
	}
	@Override
	public void createWeeklyStatisticData(IStatistic context) throws StatisticException {
		this.createStatisticData(context);
	}
	@Override
	public void createMonthlyStatisticData(IStatistic context) throws StatisticException {
		this.createStatisticData(context);
	}
	@Override
	public void createDailyStatisticData(IStatistic context) throws StatisticException {
		this.createStatisticData(context);
	}
	private void createStatisticData(IStatistic context) throws StatisticException {

		try{

			if(context instanceof CustomStatisticsSdkGenerator){
				((CustomStatisticsSdkGenerator)context).createStatisticsByStato();
			}
			
		} catch(Exception e) {
			throw new StatisticException(e.getMessage(),e);
		}
	}
	

}
