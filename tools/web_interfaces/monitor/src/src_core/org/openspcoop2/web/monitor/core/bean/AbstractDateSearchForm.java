/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.faces.event.ActionEvent;

/****
 * AbstractDateSearchForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public abstract class AbstractDateSearchForm extends AbstractCoreSearchForm{

	private String lastPeriodo;
	protected String periodo;
	protected String periodoDefault;
	protected Date dataInizio;
	protected Date dataFine;
	private Date dataRicerca;
	private boolean useDataRicerca = false;

	public String getPeriodo() {
		if (this.periodo == null) {
			this.periodo = "Ultimo mese";
			this.setPeriodo(this.periodo);
		}

		return this.periodo;
	}

	public void periodoListener(ActionEvent ae){
		_setPeriodo();
	}

	public void setPeriodo(String periodo) {
		this.lastPeriodo = this.periodo;
		this.periodo = periodo;
	} 

	public void setPeriodoDefault(String periodoDefault) {
		this.periodoDefault = periodoDefault;
	}

	public String getPeriodoDefault() {
		return this.periodoDefault;
	}
	
	public abstract String getPrintPeriodo();
	
	protected String getDefaultPrintPeriodoBehaviour(){
		Date inizio = null;
		if(this.dataInizio!=null){
			inizio = (Date) this.dataInizio.clone();
		}
				
		Date fine = null;
		if(this.dataFine!=null && this.dataRicerca!=null && this.useDataRicerca){
			if(this.dataFine.before(this.dataRicerca)){
				fine = (Date) this.dataFine.clone();
			}
			else{
				fine = (Date) this.dataRicerca.clone();
			}
		}
		else if(this.dataFine!=null){
			fine = (Date) this.dataFine.clone();
		}
		else if(this.dataRicerca!=null && this.useDataRicerca){
			fine = (Date) this.dataRicerca.clone();
		}
			
		return AbstractDateSearchForm.printPeriodo(inizio, fine);
	}
	
	public static String printPeriodo(Date inizio,Date fine){
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.ITALIAN);
		StringBuilder bf = new StringBuilder();
		bf.append("[ ");
		if(inizio!=null){
			bf.append(sdf.format(inizio));
		}
		else{
			bf.append("*");
		}
		bf.append(" - ");
		if(fine!=null){
			bf.append(sdf.format(fine));
		}
		else{
			bf.append("*");
		}
		bf.append(" ]");
		return bf.toString();
	}
		
	protected void _setPeriodo() {
		Date lastDataInizio = this.dataInizio;
		Date lastDataFine = this.dataFine;

		Calendar today_without_second_and_ms = Calendar.getInstance();
		today_without_second_and_ms.set(Calendar.HOUR_OF_DAY, 23);
		today_without_second_and_ms.set(Calendar.MINUTE, 59);
		today_without_second_and_ms.clear(Calendar.SECOND);
		today_without_second_and_ms.clear(Calendar.MILLISECOND);
		
		if ("Ultima ora".equals(this.periodo)) {
			
			// Intervallo che definisce esattamente l'ultima ora a partire da adesso
			
			this.dataFine = Calendar.getInstance().getTime(); // now
			
			Calendar lasth = Calendar.getInstance();
			lasth.add(Calendar.HOUR_OF_DAY, -1);
			this.dataInizio = lasth.getTime();

		}
		else if ("Ultime 12 ore".equals(this.periodo)) {
			
			// Intervallo che definisce esattamente le ultime 12 ore a partire da adesso
			
			this.dataFine = Calendar.getInstance().getTime(); // now
			
			Calendar lasth = Calendar.getInstance();
			lasth.add(Calendar.HOUR_OF_DAY, -12);
			this.dataInizio = lasth.getTime();

		}else if ("Ultime 24 ore".equals(this.periodo)) {
			
			// Intervallo che definisce esattamente le ultime 24 ore a partire da adesso
			
			this.dataFine = Calendar.getInstance().getTime(); // now

			Calendar ieri = Calendar.getInstance();
			ieri.add(Calendar.DATE, -1);
			this.dataInizio = ieri.getTime();

		}else if ("Ieri".equals(this.periodo)) {
			
			// Intervallo che definisce esattamente la giornata precedente ad oggi
			
			Calendar ieri = (Calendar) today_without_second_and_ms.clone();
			ieri.add(Calendar.DATE, -1);
			ieri.set(Calendar.SECOND, 59);
			ieri.set(Calendar.MILLISECOND, 999);
			this.dataFine = ieri.getTime();

			ieri.set(Calendar.HOUR_OF_DAY, 0);
			ieri.set(Calendar.MINUTE, 0);
			ieri.clear(Calendar.SECOND);
			ieri.clear(Calendar.MILLISECOND);
			this.dataInizio = ieri.getTime();

		} else if ("Ultima settimana".equals(this.periodo)) {
			
			// Intervallo che definisce la settimana passato più la giornata di oggi parziale (un totale di 8 giorni)
			
			this.dataFine = Calendar.getInstance().getTime(); // now
			
			Calendar lastWeek = (Calendar) today_without_second_and_ms.clone();
			lastWeek.set(Calendar.HOUR_OF_DAY, 0);
			lastWeek.set(Calendar.MINUTE, 0);
			lastWeek.add(Calendar.DATE, -7);
			this.dataInizio = lastWeek.getTime();

		} else if ("Ultimo mese".equals(this.periodo)) {
			
			// Intervallo che definisce il mese passato più la giornata di oggi parziale
			
			this.dataFine = Calendar.getInstance().getTime(); // now
			
			Calendar lastMonth = (Calendar) today_without_second_and_ms.clone();
			lastMonth.set(Calendar.HOUR_OF_DAY, 0);
			lastMonth.set(Calendar.MINUTE, 0);
			lastMonth.add(Calendar.DATE, -30);
			this.dataInizio = lastMonth.getTime();

		} else if ("Ultimo anno".equals(this.periodo)) {
			
			// Intervallo che definisce l'ultimo anno passato più la giornata di oggi parziale
			
			this.dataFine = Calendar.getInstance().getTime(); // now
			
			Calendar lastyear = (Calendar) today_without_second_and_ms.clone();
			lastyear.set(Calendar.HOUR_OF_DAY, 0);
			lastyear.set(Calendar.MINUTE, 0);
			lastyear.add(Calendar.YEAR, -1);
			this.dataInizio = lastyear.getTime();

		} else {

			if(this.lastPeriodo == null){
				
				// Se entro nel personalizzato per la prima volta visualizzo
				// un intervallo che definisce esattamente le ultime 24 ore a partire da adesso
				
				this.dataFine = Calendar.getInstance().getTime(); // now

				Calendar ieri = Calendar.getInstance();
				ieri.add(Calendar.DATE, -1);
				this.dataInizio = ieri.getTime();
				
			}else{
				if(this.lastPeriodo.equals("Personalizzato")){
					// se il precedente era personalizzato lascio le date che c'erano
					this.dataInizio = lastDataInizio;
					this.dataFine = lastDataFine;
				}
				else if ("Ieri".equals(this.lastPeriodo)) {
					
					// Intervallo che definisce esattamente la giornata precedente ad oggi
					
					Calendar ieri = (Calendar) today_without_second_and_ms.clone();
					ieri.add(Calendar.DATE, -1);
					ieri.set(Calendar.SECOND, 59);
					ieri.set(Calendar.MILLISECOND, 999);
					this.dataFine = ieri.getTime();

					ieri.set(Calendar.HOUR_OF_DAY, 0);
					ieri.set(Calendar.MINUTE, 0);
					ieri.clear(Calendar.SECOND);
					ieri.clear(Calendar.MILLISECOND);
					this.dataInizio = ieri.getTime();
				}
				else if(this.lastPeriodo.equals("Ultima ora")){
					
					// Intervallo che definisce esattamente l'ultima ora a partire da adesso
					
					this.dataFine = Calendar.getInstance().getTime(); // now
					
					Calendar lasth = Calendar.getInstance();
					lasth.add(Calendar.HOUR_OF_DAY, -1);
					this.dataInizio = lasth.getTime();
					
				}
				else if(this.lastPeriodo.equals("Ultime 12 ore")){
						
					// Intervallo che definisce esattamente le ultime 12 ore a partire da adesso
					
					this.dataFine = Calendar.getInstance().getTime(); // now
					
					Calendar lasth = Calendar.getInstance();
					lasth.add(Calendar.HOUR_OF_DAY, -12);
					this.dataInizio = lasth.getTime();
					
				}
				else if ("Ultime 24 ore".equals(this.lastPeriodo)) {
					
					// Intervallo che definisce esattamente le ultime 24 ore a partire da adesso
					
					this.dataFine = Calendar.getInstance().getTime(); // now
					
					Calendar ieri = Calendar.getInstance();
					ieri.add(Calendar.DATE, -1);
					this.dataInizio = ieri.getTime();

				}
				else if ("Ultima settimana".equals(this.lastPeriodo)){
					
					// Intervallo che definisce la settimana passato più la giornata di oggi parziale (un totale di 8 giorni)
					
					this.dataFine = Calendar.getInstance().getTime(); // now
					
					Calendar lastWeek = (Calendar) today_without_second_and_ms.clone();
					lastWeek.set(Calendar.HOUR_OF_DAY, 0);
					lastWeek.set(Calendar.MINUTE, 0);
					lastWeek.add(Calendar.DATE, -7);
					this.dataInizio = lastWeek.getTime();
					
				} 
				else if ("Ultimo mese".equals(this.lastPeriodo)) {
					
					// Intervallo che definisce il mese passato più la giornata di oggi parziale
					
					this.dataFine = Calendar.getInstance().getTime(); // now
					
					Calendar lastMonth = (Calendar) today_without_second_and_ms.clone();
					lastMonth.set(Calendar.HOUR_OF_DAY, 0);
					lastMonth.set(Calendar.MINUTE, 0);
					lastMonth.add(Calendar.DATE, -30);
					this.dataInizio = lastMonth.getTime();
					
				} 
				else if(this.lastPeriodo.equals("Ultimo anno")){
					
					// Intervallo che definisce l'ultimo anno passato più la giornata di oggi parziale
					
					this.dataFine = Calendar.getInstance().getTime(); // now
					
					Calendar lastyear = (Calendar) today_without_second_and_ms.clone();
					lastyear.set(Calendar.HOUR_OF_DAY, 0);
					lastyear.set(Calendar.MINUTE, 0);
					lastyear.add(Calendar.YEAR, -1);
					this.dataInizio = lastyear.getTime();

				}
					
			}
		}


	}
	
	public boolean isPeriodoPersonalizzato() {
		return "Personalizzato".equals(this.periodo);
	}
	public boolean isPeriodoUltime12ore() {
		return "Ultime 12 ore".equals(this.periodo);
	}
	
	public boolean isLastPeriodoPersonalizzato() {
		return "Personalizzato".equals(this.lastPeriodo);
	}
	
	public Date getDataInizio() {
		return this.dataInizio;
	}

	public void setDataInizio(Date dataInizio) {
		//this.dataInizio = dataInizio;
		if(dataInizio!=null){
			Calendar c = Calendar.getInstance();
			c.setTime(dataInizio);
			c.clear(Calendar.SECOND);
			c.clear(Calendar.MILLISECOND);
			this.dataInizio = c.getTime();
		}
		else{
			this.dataInizio = null;
		}
	}

	public Date getDataFine() {
		return this.dataFine;
	}

	public void setDataFine(Date dataFine) {
		//this.dataFine = dataFine;
		if(dataFine!=null){
			Calendar c = Calendar.getInstance();
			c.setTime(dataFine);
			c.set(Calendar.SECOND,59);
			c.set(Calendar.MILLISECOND,999);
			this.dataFine = c.getTime();
		}
		else{
			this.dataFine = null;
		}
	}

	public void dataInizioChangeListener(javax.faces.event.ValueChangeEvent evt){
		//		log.debug(this.dataInizio); 
		this.setDataInizio((Date) evt.getNewValue());
	}

	public void dataFineChangeListener(javax.faces.event.ValueChangeEvent evt){
		//		log.debug(this.dataInizio); 
		this.setDataFine((Date) evt.getNewValue());
	}

	public void dataFineListener(ActionEvent ae){
		//<a4j:support status="mainStatus" event="onchanged" />
	}

	public void dataInizioListener(ActionEvent ae){
		//<a4j:support status="mainStatus" event="onchanged" />
	}

	public Date getDataRicerca() {
		// La gestione e' spostata sui due metodi sottostanti: aggiornaNuovaDataRicerca e congelaDataRicerca
		if(this.useDataRicerca){
			return this.dataRicerca;
		}
		else{
			return null;
		}
	}
	
	public Date getDataRicercaRaw() {
		return this.dataRicerca;
	}

	public void aggiornaNuovaDataRicerca() {
		this.dataRicerca = new Date();
		this.useDataRicerca = false;
	}
	
	public void congelaDataRicerca() {
		this.useDataRicerca = true;
	}
}
