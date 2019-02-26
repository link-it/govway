/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 * 
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.monitor.sdk.transaction;

/**
 * SLATrace
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SLATrace {


	/** tempo che intercorre tra la ricezione del messaggio di richiesta e la consegna del
	* messaggio di risposta applicativa (anche su differenti transazioni)
	*/
	public long getResponseTime() {
		return this.responseTime;
	}
	
	/** numero di record presenti nel messaggio di risposta che compongono il risultato
	* dell'elaborazione richiesta valore -1 se il calcolo non è determinabile o non applicabile
	*/
	public int getReturnedRecordNumber() {
		return this.recordNumber;
	}
	
	public void setResponseTime(long time) {
		this.responseTime = time;
	}
	
	public void setReturnedRecordNumber(int num) {
		this.recordNumber = num;
	}

	private long responseTime = 0;
	private int recordNumber = -1;

}
