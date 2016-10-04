/*
 * OpenSPCoop - Customizable API Gateway 
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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.openspcoop2.utils.UtilsException;


/**
 * Interfaccia per la produzione di date
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IDate {

	
	/**
	 * Impostazione delle proprieta' per il DateManager
	 */
	public void init(java.util.Properties properties) throws UtilsException;
	
	/**
	 * Close il DataManager
	 * 
	 * @throws UtilsException
	 */
	public void close() throws UtilsException;
	
	/**
	 * Ritorna la data corrente.
	 * 
	 * @return Data corrente
	 */
	public Date getDate() throws UtilsException;
	
	/**
	 * Ritorna la data corrente in millisecondi da Gennaio 1.4970.
	 * 
	 * @return Data corrente
	 */
	public long getTimeMillis() throws UtilsException;
	
	/**
	 * Ritorna la data corrente come timestamp SQL.
	 * 
	 * @return Data corrente
	 */
	public Timestamp getTimestamp() throws UtilsException;
	
	/**
	 * Ritorna la data corrente come Calendar
	 * 
	 * @return Data corrente
	 */
	public Calendar getCalendar() throws UtilsException;
	
}
