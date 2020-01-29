/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it). 
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

package org.openspcoop2.core.controllo_traffico.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdActivePolicy;
import org.openspcoop2.utils.date.DateUtils;

/**
 * UniqueIdentifierUtilities 
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UniqueIdentifierUtilities {

	private static final String separator = "#";
	
	public static String getUniqueId(AttivazionePolicy attivazionePolicy){
		String idActivePolicy = attivazionePolicy.getIdActivePolicy();
		
		SimpleDateFormat dateFormat = DateUtils.getSimpleDateFormatMs();
		String time = dateFormat.format(attivazionePolicy.getUpdateTime());
		
		return idActivePolicy + separator + time;
	}
	
	public static String getUniqueId(IdActivePolicy idActivePolicy){
		
		SimpleDateFormat dateFormat = DateUtils.getSimpleDateFormatMs();
		return idActivePolicy.getNome()+separator+dateFormat.format(idActivePolicy.getUpdateTime());
		
	}
	
	public static String extractIdActivePolicy(String id){
		return id.split(separator)[0];
	}
	
	public static Date extractUpdateTimeActivePolicy(String id) throws ParseException{
		SimpleDateFormat dateFormat = DateUtils.getSimpleDateFormatMs();
		return dateFormat.parse(id.split(separator)[1]);
	}
}
