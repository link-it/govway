/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
