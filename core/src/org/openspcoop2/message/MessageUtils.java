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

package org.openspcoop2.message;

/**
 * MessageUtils
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageUtils {

	public static void registerParseException(OpenSPCoop2Message msg, Throwable e){
		registerParseException(msg, e, false);
	}

	public static void registerParseException(OpenSPCoop2Message o, Throwable e, boolean allException){
		if(o!=null && o.getParseException()==null){
			if(allException){
				o.setParseException(e);
			}
			else{
				Throwable t = org.openspcoop2.message.exception.ParseExceptionUtils.getParseException(e);
				if(t!=null){
					o.setParseException(t);
				}
			}
			
		}
	}
	
}
