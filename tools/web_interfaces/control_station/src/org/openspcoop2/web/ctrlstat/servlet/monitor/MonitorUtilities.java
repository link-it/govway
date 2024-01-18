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

package org.openspcoop2.web.ctrlstat.servlet.monitor;

import java.util.List;

import org.openspcoop2.pdd.monitor.Messaggio;
import org.openspcoop2.pdd.monitor.StatoPdd;
import org.openspcoop2.pdd.monitor.driver.FilterSearch;
import org.openspcoop2.pdd.monitor.driver.FiltroStatoConsegnaAsincrona;
import org.openspcoop2.pdd.monitor.driver.StatoConsegneAsincrone;

/**
*
* MonitorUtilities
* 
* @author Andrea Poli (apoli@link.it)
* @author $Author$
* @version $Rev$, $Date$
* 
*/
public class MonitorUtilities {

	public static long countListaRichiestePendenti(FilterSearch filter,String pddName, String sorgenteDati) throws Exception{
		return Monitor.driverMonitoraggioLocale.get(sorgenteDati).countListaRichiestePendenti(filter);
	}
	
	public static List<Messaggio> getListaRichiestePendenti(FilterSearch filter,String pddName, String sorgenteDati) throws Exception{
		return Monitor.driverMonitoraggioLocale.get(sorgenteDati).getListaRichiestePendenti(filter);
	}
	
	public static long deleteRichiestePendenti(FilterSearch filter,String pddName, String sorgenteDati) throws Exception{
		return Monitor.driverMonitoraggioLocale.get(sorgenteDati).deleteRichiestePendenti(filter);
	}
	
	public static long aggiornaDataRispedizioneRichiestePendenti(FilterSearch filter,String pddName, String sorgenteDati) throws Exception{
		return Monitor.driverMonitoraggioLocale.get(sorgenteDati).aggiornaDataRispedizioneRichiestePendenti(filter);
	}
	
	public static StatoPdd getStatoRichiestePendenti(FilterSearch filter,String pddName, String sorgenteDati) throws Exception{
		return Monitor.driverMonitoraggioLocale.get(sorgenteDati).getStatoRichiestePendenti(filter);
	}
	
	public static StatoConsegneAsincrone getStatoConsegneAsincrone(FiltroStatoConsegnaAsincrona filtro, String sorgenteDati) throws Exception{
		return Monitor.driverMonitoraggioLocale.get(sorgenteDati).getStatoConsegneAsincrone(filtro);
	}

}
