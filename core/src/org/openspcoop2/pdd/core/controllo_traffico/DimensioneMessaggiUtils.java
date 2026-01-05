/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.core.controllo_traffico;

import java.util.List;

import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.utils.LimitExceededIOException;
import org.openspcoop2.utils.LimitedInputStreamEngine;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.slf4j.Logger;

/**     
 * DimensioneMessaggiUtils
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DimensioneMessaggiUtils {

	private DimensioneMessaggiUtils() {}
		
	public static final boolean REQUEST = true;
	public static final boolean RESPONSE = false;
	
	private static final String PREFIX_RILEVATO_HEADER_CONTENT_LENGTH = "Rilevato header "+HttpConstants.CONTENT_LENGTH+" ";
	public static void verifyByContentLength(Logger log, List<String> l, SogliaDimensioneMessaggio requestLimitSize, LimitExceededNotifier notifier, 
			org.openspcoop2.utils.Map<Object> ctx, boolean request) throws LimitExceededIOException {
		// in presenza di più header utilizzo quello con il valore maggiore
		if(l!=null && !l.isEmpty()) {
			long size = readContentLength(log, l, requestLimitSize);
			if(l.size()>1) {
				String msgWarn = PREFIX_RILEVATO_HEADER_CONTENT_LENGTH+" multiplo "+l+"; viene usato il valore più grande '"+size+"'";
				log.warn(msgWarn);
			}
			long limitBytes = requestLimitSize.getSogliaKb()*1024; // trasformo kb in bytes
			if(size>limitBytes) {
				LimitedInputStreamEngine.contentLenghtLimitExceeded(request ? CostantiPdD.PREFIX_LIMITED_REQUEST : CostantiPdD.PREFIX_LIMITED_RESPONSE, 
						ctx, notifier, limitBytes);
			}
		}
	}
	private static long readContentLength(Logger log, List<String> l, SogliaDimensioneMessaggio requestLimitSize) {
		long size = -1;
		String error = null;
		Exception exp = null;
		for (String hdr : l) {
			long lX = -1;
			try {
				lX = Long.parseLong(hdr);
				if(lX<0) {
					error = PREFIX_RILEVATO_HEADER_CONTENT_LENGTH+"con un valore negativo '"+hdr+"'";
					// in caso di negative devo usare max length, sennò è un modo per superare la policy
					lX = Long.MAX_VALUE;
				}
				else if(lX==0l && !requestLimitSize.isUseContentLengthHeaderAcceptZeroValue()) {
					error = PREFIX_RILEVATO_HEADER_CONTENT_LENGTH+"con valore 0";
					// in caso di valore zero non accettato devo usare max length, sennò è un modo per superare la policy
					lX = Long.MAX_VALUE;
				}
			}catch(Exception e) {
				error = PREFIX_RILEVATO_HEADER_CONTENT_LENGTH+"con valore malformato '"+hdr+"': "+e.getMessage();
				exp = e;
				// in caso di errore devo usare max length, sennò è un modo per superare la policy
				lX = Long.MAX_VALUE;
			}
			if(lX > size) {
				size = lX;
			}
			if(error!=null) {
				break;
			}
		}
		return parseReadResultContentLength(log, size, error, exp);
	}
	private static long parseReadResultContentLength(Logger log, long size, String error, Exception exp) {
		if(size<0) {
			error = "Calcolo dimensione content lenght fallita? Rilevata dimensione minore di zero";
			// in caso di errore devo usare max length, sennò è un modo per superare la policy
			size = Long.MAX_VALUE;
		}
		if(error!=null) {
			if(exp!=null) {
				log.error(error,exp);
			}
			else {
				log.error(error);
			}
		}
		return size;
	}
}
