/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

import org.openspcoop2.pdd.core.pdnd.async.InterazioneAsincronaPDND;
import org.openspcoop2.pdd.core.pdnd.async.InterazioniAsincronePDNDFiltro;

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
	
	
	/* Sorgenti dati runtime (utilizzate anche dalle funzionalità che accedono alla base dati runtime al di fuori della coda messaggi) */
	
	public static List<String> getSorgentiDati() throws Exception{
		Monitor.checkInitMonitoraggio();
		return Monitor.sorgentiDriverMonitoraggioLocale;
	}
	public static List<String> getLabelSorgentiDati() throws Exception{
		Monitor.checkInitMonitoraggio();
		return Monitor.labelSorgentiDriverMonitoraggioLocale;
	}
	
	
	/* Interazioni relative agli scambi di dati asincroni PDND */
	
	public static long countInterazioniAsincronePDND(InterazioniAsincronePDNDFiltro filtro, String sorgenteDati) throws Exception{
		Monitor.checkInitMonitoraggio();
		return Monitor.driverMonitoraggioLocale.get(sorgenteDati).countInterazioniAsincronePDND(filtro);
	}
	public static List<InterazioneAsincronaPDND> getInterazioniAsincronePDND(InterazioniAsincronePDNDFiltro filtro, String sorgenteDati) throws Exception{
		Monitor.checkInitMonitoraggio();
		return Monitor.driverMonitoraggioLocale.get(sorgenteDati).getInterazioniAsincronePDND(filtro);
	}
	public static InterazioneAsincronaPDND getInterazioneAsincronaPDND(long id, String sorgenteDati) throws Exception{
		Monitor.checkInitMonitoraggio();
		return Monitor.driverMonitoraggioLocale.get(sorgenteDati).getInterazioneAsincronaPDND(id);
	}
	public static int deleteInterazioneAsincronaPDND(long id, String sorgenteDati) throws Exception{
		Monitor.checkInitMonitoraggio();
		return Monitor.driverMonitoraggioLocale.get(sorgenteDati).deleteInterazioneAsincronaPDND(id);
	}

}
