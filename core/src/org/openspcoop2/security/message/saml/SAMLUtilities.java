/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

package org.openspcoop2.security.message.saml;

import org.joda.time.DateTime;


/**
 * SAMLUtilities
 * 	
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SAMLUtilities {

	public static DateTime minutesOperator(DateTime dateTime,Integer intValue){
		DateTime dateTimeRes = dateTime;
		if(intValue!=null){
			if(intValue.intValue() == 0){
				dateTimeRes = dateTime;
			}
			else if(intValue.intValue() > 0){
				dateTimeRes = dateTime.plusMinutes(intValue.intValue());
			}
			else if(intValue.intValue() < 0){
				dateTimeRes = dateTime.minusMinutes(intValue.intValue()*-1);
			}
		}
		return dateTimeRes;
	}

}
