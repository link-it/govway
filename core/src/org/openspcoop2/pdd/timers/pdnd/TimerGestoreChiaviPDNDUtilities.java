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
package org.openspcoop2.pdd.timers.pdnd;

import org.openspcoop2.pdd.timers.TimerException;
import org.openspcoop2.utils.certificate.remote.RemoteStoreConfig;
import org.openspcoop2.utils.transport.http.ExternalResourceUtils;

/**     
 * TimerGestoreChiaviPDNDUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TimerGestoreChiaviPDNDUtilities {

	private RemoteStoreConfig remoteStore;
	private String urlCheckEventi;
	
	private String parameterLastEventId;
	
	private String parameterLimit;
	private int limit;
	
	public TimerGestoreChiaviPDNDUtilities(RemoteStoreConfig remoteStore, String urlCheckEventi,
			String parameterLastEventId, String parameterLimit, int limit) {
		
		this.remoteStore = remoteStore;
		this.urlCheckEventi = urlCheckEventi;
		
		this.parameterLastEventId = parameterLastEventId;
		
		this.parameterLimit = parameterLimit;
		this.limit = limit;
	}
	
	public TimerGestoreChiaviPDNDEvents readNextEvents(long lastEventId) throws TimerException {
		
		String responseJson = null;
		try {
		
			StringBuilder sb = new StringBuilder(this.urlCheckEventi).append("?");
			sb.append(this.parameterLastEventId).append("=").append(lastEventId);
			sb.append("&");
			sb.append(this.parameterLimit).append("=").append(this.limit);
			
			String url = sb.toString();
			byte[] response = ExternalResourceUtils.readResource(url, this.remoteStore);
			responseJson = new String(response);
			
		}catch(Exception e) {
			throw new TimerException(e.getMessage(),e);
		}
		
		try {
			return TimerGestoreChiaviPDNDEvents.toEvents(responseJson);
		}catch(Exception e) {
			throw new TimerException("Uncorrect json format ("+responseJson+"): "+e.getMessage(),e);
		}
		
	}
	
}
