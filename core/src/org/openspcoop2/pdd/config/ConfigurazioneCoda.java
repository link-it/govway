/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.config;

import java.util.Properties;

/**
 * ConfigurazioneCoda
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneCoda {

	private String name;

	private int poolSize;
	private int queueSize;
	
	private int nextMessages_limit;
	private int nextMessages_intervalloControllo;
	
	private Integer scheduleNewMessageAfter;
	
	private int nextMessages_consegnaFallita_intervalloControllo;
	private boolean nextMessages_consegnaFallita_calcolaDataMinimaRiconsegna;
	
	private int consegnaFallita_intervalloMinimoRiconsegna;
		
	private boolean debug = false;
	
	public ConfigurazioneCoda(String name, Properties p) throws Exception {
		this.name = name;
		
		this.poolSize = getIntProperty(this.name, p, "threads.poolSize");
		this.queueSize = getIntProperty(this.name, p, "threads.queueSize");
		
		this.nextMessages_limit = getIntProperty(this.name, p, "nextMessages.limit");
		this.nextMessages_intervalloControllo = getIntProperty(this.name, p, "nextMessages.intervalloControllo");
		
		String tmp = p.getProperty("nextMessages.scheduleNewMessageAfter");
		if(tmp!=null) {
			try {
				this.scheduleNewMessageAfter = Integer.valueOf(tmp);
			}catch(Throwable t) {
				throw new Exception("[Queue:"+this.name+"] Property 'nextMessages.scheduleNewMessageAfter' invalid: "+t.getMessage(),t);
			}
			if(this.scheduleNewMessageAfter<=0) {// lasciare per poter annullare da properties
				this.scheduleNewMessageAfter = null;
			}
		}
		
		this.nextMessages_consegnaFallita_intervalloControllo = getIntProperty(this.name, p, "nextMessages.consegnaFallita.intervalloControllo");
		this.nextMessages_consegnaFallita_calcolaDataMinimaRiconsegna = getBooleanProperty(this.name, p, "nextMessages.consegnaFallita.calcolaDataMinimaRiconsegna");
		
		this.consegnaFallita_intervalloMinimoRiconsegna  = getIntProperty(this.name, p, "consegnaFallita.intervalloMinimoRiconsegna");
		
		this.debug = getBooleanProperty(this.name, p, "debug");
	}
	
	@Override
	public String toString() {
		return this.toString(true, ", ");
	}
	public String toString(boolean printName, String separator) {
		StringBuilder sb = new StringBuilder();
		if(printName) {
			sb.append("name:").append(this.name).append(separator);
		}
		sb.append("poolSize:").append(this.poolSize).append(separator);
		sb.append("queueSize:").append(this.queueSize).append(separator);
		sb.append("nextMessages_limit:").append(this.nextMessages_limit).append(separator);
		sb.append("nextMessages_newCheckEverySeconds:").append(this.nextMessages_intervalloControllo).append(separator);
		if(this.scheduleNewMessageAfter!=null) {
			sb.append("nextMessages_scheduleNewMessageAfter:").append(this.scheduleNewMessageAfter).append(separator);
		}
		sb.append("nextMessages_checkFailedDelivery_everySeconds:").append(this.nextMessages_consegnaFallita_intervalloControllo).append(separator);
		sb.append("nextMessages_checkFailedDelivery_computeMinDate:").append(this.nextMessages_consegnaFallita_calcolaDataMinimaRiconsegna).append(separator);
		sb.append("nextFailedDeliveryAfterSeconds:").append(this.consegnaFallita_intervalloMinimoRiconsegna);
		return sb.toString();
	}
	
	private static String getProperty(String coda, Properties p, String name) throws Exception {
		String tmp = p.getProperty(name);
		if(tmp==null) {
			throw new Exception("[Queue:"+coda+"] Property '"+name+"' not found");
		}
		return tmp.trim();
	}
	private static int getIntProperty(String coda, Properties p, String name) throws Exception {
		String tmp = getProperty(coda, p, name);
		try {
			return Integer.valueOf(tmp);
		}catch(Exception e) {
			throw new Exception("[Queue:"+coda+"] Property '"+name+"' uncorrect (value:"+tmp+"): "+e.getMessage(),e);
		}
	}
	private static boolean getBooleanProperty(String coda, Properties p, String name) throws Exception {
		String tmp = getProperty(coda, p, name);
		try {
			return Boolean.valueOf(tmp);
		}catch(Exception e) {
			throw new Exception("[Queue:"+coda+"] Property '"+name+"' uncorrect (value:"+tmp+"): "+e.getMessage(),e);
		}
	}
	
	public String getName() {
		return this.name;
	}

	public int getPoolSize() {
		return this.poolSize;
	}

	public int getQueueSize() {
		return this.queueSize;
	}

	public int getNextMessages_limit() {
		return this.nextMessages_limit;
	}

	public int getNextMessages_intervalloControllo() {
		return this.nextMessages_intervalloControllo;
	}

	public Integer getScheduleMessageAfter() {
		return this.scheduleNewMessageAfter;
	}
	
	public int getNextMessages_consegnaFallita_intervalloControllo() {
		return this.nextMessages_consegnaFallita_intervalloControllo;
	}

	public boolean isNextMessages_consegnaFallita_calcolaDataMinimaRiconsegna() {
		return this.nextMessages_consegnaFallita_calcolaDataMinimaRiconsegna;
	}
	
	public int getConsegnaFallita_intervalloMinimoRiconsegna() {
		return this.consegnaFallita_intervalloMinimoRiconsegna;
	}
	
	public boolean isDebug() {
		return this.debug;
	}
	
}
