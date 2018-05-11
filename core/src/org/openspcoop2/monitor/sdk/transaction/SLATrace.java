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
