package org.openspcoop2.monitor.engine.statistic;

import org.openspcoop2.monitor.sdk.statistic.StatisticResourceFilter;

import java.util.ArrayList;
import java.util.List;


/**
 * RisorsaSemplice
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RisorsaSemplice {

	private String idStatistica;
	private String idRisorsa;
	private List<StatisticResourceFilter> filtri = new ArrayList<StatisticResourceFilter>();
	
	public String getIdStatistica() {
		return this.idStatistica;
	}
	public void setIdStatistica(String idStatistica) {
		this.idStatistica = idStatistica;
	}
	public String getIdRisorsa() {
		return this.idRisorsa;
	}
	public void setIdRisorsa(String idRisorsa) {
		this.idRisorsa = idRisorsa;
	}
	public List<StatisticResourceFilter> getFiltri() {
		return this.filtri;
	}
	public void setFiltri(List<StatisticResourceFilter> filtri) {
		this.filtri = filtri;
	}
}
