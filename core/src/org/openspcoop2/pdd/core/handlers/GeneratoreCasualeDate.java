/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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


package org.openspcoop2.pdd.core.handlers;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

/**
 * GeneratoreCasualeDate
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GeneratoreCasualeDate {

	private static GeneratoreCasualeDate generatore = null;
	private static long sogliaMsMin = 1; 
	private static long sogliaMsMax = 20;

	public static synchronized void init(Date intervalloInizio,Date intervalloFine,Logger log){
		if(GeneratoreCasualeDate.generatore==null){
			GeneratoreCasualeDate.generatore = new GeneratoreCasualeDate(intervalloInizio,intervalloFine,log);
		}
	}

	public static GeneratoreCasualeDate getGeneratoreCasualeDate(){
		// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
		if (GeneratoreCasualeDate.generatore == null) {
	        synchronized (GeneratoreCasualeDate.class) {
	            if (GeneratoreCasualeDate.generatore == null) {
	                return null;
	            }
	        }
	    }
		return GeneratoreCasualeDate.generatore;
	}
	
	private static java.util.Random randomEngine = null;
	private static synchronized void initRandom() {
		if(randomEngine==null) {
			randomEngine = new SecureRandom();
		}
	}
	public static java.util.Random getRandom() {
		if(randomEngine==null) {
			initRandom();
		}
		return randomEngine;
	}
	
	private static long getRandom(long inizio, long fine){
		return inizio + 
			Math.round( getRandom().nextDouble() * (fine - inizio) );
	}
	

	/* ------- Istanza ---------- */
	
	private Date intervalloInizio = null;
	private Date intervalloFine = null;
	private GeneratoreCasualeDate(Date intervalloInizio,Date intervalloFine,Logger log){
		this.intervalloInizio = intervalloInizio;
		this.intervalloFine = intervalloFine;
		String msg = "Generatore di date causauli dei log attivato con intervallo ["+this.intervalloInizio+"] - ["+this.intervalloFine+"]";
		log.info(msg);
	}
	
	private Map<String, Date>  dateGenerate = new HashMap<>();
	
	private Date getNextDate(String idCluster){
		Date nextDate = null;
		if(this.dateGenerate.containsKey(idCluster)){
			Date data = this.dateGenerate.remove(idCluster);
			nextDate = new Date(data.getTime()+GeneratoreCasualeDate.getRandom(GeneratoreCasualeDate.sogliaMsMin, GeneratoreCasualeDate.sogliaMsMax));
		}
		else{
			nextDate = new Date(GeneratoreCasualeDate.getRandom(this.intervalloInizio.getTime(),this.intervalloFine.getTime()));	
		}		
		this.dateGenerate.put(idCluster, nextDate);
		return nextDate;
	}
	
	public Date getProssimaData(String idCluster){
		return getNextDate(idCluster);	
	}
	
	public void releaseResource(String idCluster){
		this.dateGenerate.remove(idCluster);
	}
	
}

