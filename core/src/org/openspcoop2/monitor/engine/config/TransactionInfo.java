package org.openspcoop2.monitor.engine.config;

import java.util.ArrayList;
import java.util.List;

/**
 * TransactionInfo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionInfo {

	private String stato;	
	private List<TransactionResource> risorse = new ArrayList<TransactionResource>();
	public List<TransactionResource> getRisorse() {
		return this.risorse;
	}
	public String getStato() {
		return this.stato;
	}
	protected void setStato(String stato) {
		this.stato = stato;
	}
	
}
