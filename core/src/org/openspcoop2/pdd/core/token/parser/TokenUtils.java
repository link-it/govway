/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.token.parser;

import java.math.BigDecimal;
import java.util.Date;

/**     
 * TokenUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TokenUtils {
	
	private TokenUtils() {}

	public static Date convertLifeTimeInSeconds(Date now, String exp) {
		if(exp!=null) {
			/**System.out.println("PARSE ["+exp+"]");*/
			
			// The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
			// expire in one hour from the time the response was generated.
			
			//BigInteger expIn = new BigInteger(exp); // Fix: per gestire formati con esponenti
			BigDecimal expIn = new BigDecimal(exp);
			long lMs = 0;
			try {
				@SuppressWarnings("unused")
				long lSeconds = expIn.longValueExact();
				expIn = expIn.multiply(BigDecimal.valueOf(1000l)); // trasformo in millisecondi
				lMs = expIn.longValueExact();
			}catch(ArithmeticException ae) {
				/**System.out.println("PARSE OVERFLOW ["+exp+"]");*/
				expIn = BigDecimal.valueOf(Long.MAX_VALUE);
				lMs = expIn.longValueExact();
				return new Date(lMs);
			}
			
			if(lMs>0) {
				
				try {
					expIn = expIn.add(BigDecimal.valueOf(now.getTime()));
					lMs = expIn.longValueExact();
				}catch(ArithmeticException ae) {
					/**System.out.println("PARSE OVERFLOW 2 ["+exp+"]");*/
					expIn = BigDecimal.valueOf(Long.MAX_VALUE);
					lMs = expIn.longValueExact();
				}
								
				return new Date(lMs);
			}
		}
		return null;
	}

	public static Date parseTimeInSecond(String dateS) {
		if(dateS!=null) {
			
			/**System.out.println("PARSE ["+dateS+"]");*/
			
			//BigInteger date = new BigInteger(dateS); // Fix: per gestire formati con esponenti: es. 1.676363172E9
			BigDecimal date = new BigDecimal(dateS);
			long lMs = 0;
			try {
				@SuppressWarnings("unused")
				long lSeconds = date.longValueExact();
				date = date.multiply(BigDecimal.valueOf(1000l)); // trasformo in millisecondi
				lMs = date.longValueExact();
			}catch(ArithmeticException ae) {
				/**System.out.println("PARSE OVERFLOW ["+dateS+"]");*/
				date = BigDecimal.valueOf(Long.MAX_VALUE);
				lMs = date.longValueExact();
			}
			if(lMs>0) {
				return new Date(lMs);
			}
		}
		return null;
	}
	
}
