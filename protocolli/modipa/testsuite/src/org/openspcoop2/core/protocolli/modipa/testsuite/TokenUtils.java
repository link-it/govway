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
package org.openspcoop2.core.protocolli.modipa.testsuite;

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
	
	// Metodo copiato dalla classe org.openspcoop2.pdd.core.token.parser.TokenUtils (riportato per compilarlo con jdk11)
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
