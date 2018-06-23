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

package org.openspcoop2.protocol.engine.builder;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.utils.date.DateManager;

/**
 * DateBuilder
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class DateBuilder {
	/**
	 * Metodo che si occupa di costruire una stringa formata da una data
	 * conforme alla specifica.
	 * La Data deve essere formato da :
	 * aaaa-mm-ggThh:mm:ss    (es. il 23/03/2005 alle 13:56:01 viene registrato come 2005-03-23T13:56:01)
	 * 
	 * @return un oggetto String contenente la data secondo specifica.
	 * 
	 */
	
	public static String getDate_Format(Date date) {
		if(date == null) 
			date = DateManager.getDate();
		SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
		return dateformat.format(date).replace('_','T');
	}
	
	public String getDate_ProtocolFormat() {
		return getDate_ProtocolFormat(null);
	}
	
	public String getDate_ProtocolFormat(Date date) {
		if(date == null) 
			date = DateManager.getDate();
		SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
		return dateformat.format(date).replace('_','T');
	}

}
