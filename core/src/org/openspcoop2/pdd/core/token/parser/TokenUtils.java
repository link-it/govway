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
package org.openspcoop2.pdd.core.token.parser;

import java.math.BigInteger;
import java.util.Date;

/**     
 * TokenUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TokenUtils {

	public static Date convertLifeTimeInSeconds(Date now, String exp) {
		if(exp!=null) {
			
			// The lifetime in seconds of the access token.  For example, the value "3600" denotes that the access token will
			// expire in one hour from the time the response was generated.
			
			BigInteger expIn = new BigInteger(exp);
			long lMs = 0;
			try {
				@SuppressWarnings("unused")
				long lSeconds = expIn.longValueExact();
				expIn = expIn.multiply(BigInteger.valueOf(1000l)); // trasformo in millisecondi
				lMs = expIn.longValueExact();
			}catch(ArithmeticException ae) {
				lMs = Long.MAX_VALUE;
				expIn = BigInteger.valueOf(lMs);
			}
			
			if(lMs>0) {
				
				try {
					expIn = expIn.add(BigInteger.valueOf(now.getTime()));
					lMs = expIn.longValueExact();
				}catch(ArithmeticException ae) {
					lMs = Long.MAX_VALUE;
					expIn = BigInteger.valueOf(lMs);
				}
								
				return new Date(lMs);
			}
		}
		return null;
	}

	public static Date parseTimeInSecond(String dateS) {
		if(dateS!=null) {
			BigInteger date = new BigInteger(dateS);
			long lMs = 0;
			try {
				@SuppressWarnings("unused")
				long lSeconds = date.longValueExact();
				date = date.multiply(BigInteger.valueOf(1000l)); // trasformo in millisecondi
				lMs = date.longValueExact();
			}catch(ArithmeticException ae) {
				lMs = Long.MAX_VALUE;
				date = BigInteger.valueOf(lMs);
			}
			if(lMs>0) {
				return new Date(lMs);
			}
		}
		return null;
	}
	
}
