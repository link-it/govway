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
package org.openspcoop2.pdd.core.controllo_traffico.policy;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openspcoop2.core.controllo_traffico.constants.TipoFinestra;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.utils.date.DateUtils;

/**     
 * PolicyDateUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PolicyDateUtils {

	public static String toStringIntervalloTemporale(TipoFinestra tipoFinestra, Date leftDate,Date rightDate,Date checkDate, boolean statistic) throws NotFoundException{
		StringBuilder bf = new StringBuilder("");
		
		SimpleDateFormat dateFormat = DateUtils.getSimpleDateFormatMs();
		
		if(statistic){
			bf.append("[campionamento statistico, finestra osservazione ");
		}
		else{
			bf.append("[campionamento realtime, finestra osservazione ");
		}
		bf.append(tipoFinestra.getValue());
		bf.append(" ");
		if(leftDate!=null && rightDate!=null){
			bf.append(dateFormat.format(leftDate));
			bf.append(" - ");
			bf.append(dateFormat.format(rightDate));
		}
		else{
			bf.append("non disponibile");
		}
		if(statistic){
			bf.append(" (ultimo aggiornamento:").
			append(dateFormat.format(checkDate)).
			append(")");
		}
		bf.append("]");

		return bf.toString();
	}	
	
}
