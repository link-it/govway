/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.utils.date;

import java.util.Calendar;
import java.util.Date;

/**     
 * DateUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DateUtils {


	public static Date convertToLeftInterval(Date date, UnitaTemporale unitaTemporale) {
		
		Calendar calendar = null;
		try{
			calendar = DateManager.getCalendar();
		}catch(Exception e){
			calendar = Calendar.getInstance();
		}
		calendar.setTime(date);
		switch (unitaTemporale) {
		case SECONDI:
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case MINUTI:		
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case ORARIO:		
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case GIORNALIERO:	
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case SETTIMANALE:
			while(Calendar.MONDAY!=calendar.get(Calendar.DAY_OF_WEEK)){
				calendar.add(Calendar.DAY_OF_WEEK, -1);
			}
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			break;
		case MENSILE:
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
		
		return calendar.getTime();
	}
	
	public static Date convertToRightInterval(Date date, UnitaTemporale unitaTemporale) {
				
		Calendar calendar = null;
		try{
			calendar = DateManager.getCalendar();
		}catch(Exception e){
			calendar = Calendar.getInstance();
		}
		calendar.setTime(date);
		switch (unitaTemporale) {
		case SECONDI:
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		case MINUTI:		
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		case ORARIO:		
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		case GIORNALIERO:	
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		case SETTIMANALE:
			while(Calendar.SUNDAY!=calendar.get(Calendar.DAY_OF_WEEK)){
				calendar.add(Calendar.DAY_OF_WEEK, 1);
			}
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			break;
		case MENSILE:
			calendar.set(Calendar.HOUR_OF_DAY, 23);
			calendar.set(Calendar.MINUTE, 59);
			calendar.set(Calendar.SECOND, 59);
			calendar.set(Calendar.MILLISECOND, 999);
			// prelevo ultimo giorno del mese impostato nell'attuale data
			int lastDayActualMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH, lastDayActualMonth);
			break;
		}
		
		return calendar.getTime();
	}
		
	public static Date incrementDate(Date date, UnitaTemporale unitaTemporale, int increment) {
		
		if(increment==0){
			return date;
		}
		
		Calendar calendar = null;
		try{
			calendar = DateManager.getCalendar();
		}catch(Exception e){
			calendar = Calendar.getInstance();
		}
		calendar.setTime(date);
		switch (unitaTemporale) {
		case SECONDI:
			calendar.add(Calendar.SECOND, increment);
			break;
		case MINUTI:		
			calendar.add(Calendar.MINUTE, increment);
			break;
		case ORARIO:		
			calendar.add(Calendar.HOUR_OF_DAY, increment);
			break;
		case GIORNALIERO:	
			calendar.add(Calendar.DAY_OF_YEAR, increment);
			break;
		case SETTIMANALE:
			calendar.add(Calendar.DAY_OF_YEAR, (increment*7));
			break;
		case MENSILE:
			calendar.add(Calendar.MONTH, increment);
			// prelevo ultimo giorno del mese impostato nell'attuale data
			int lastDayActualMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			calendar.set(Calendar.DAY_OF_MONTH, lastDayActualMonth);
			break;
		}
		
		return calendar.getTime();
	}
	
}
