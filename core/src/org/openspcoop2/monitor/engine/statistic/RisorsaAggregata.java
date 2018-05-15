package org.openspcoop2.monitor.engine.statistic;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.monitor.engine.exceptions.EngineException;
import org.openspcoop2.monitor.engine.transaction.TransactionContentUtils;
import org.openspcoop2.monitor.sdk.condition.IFilter;
import org.openspcoop2.monitor.sdk.statistic.StatisticFilter;

/**
 * RisorsaAggregata
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RisorsaAggregata {

	private IFilter filtro;
	private String valoreRisorsaAggregata;
	private List<StatisticFilter> filtri = new ArrayList<StatisticFilter>();
	
	public IFilter getFiltro() {
		return this.filtro;
	}
	public void setFiltro(IFilter filtro) {
		this.filtro = filtro;
	}
	public String getValoreRisorsaAggregata() {
		return this.valoreRisorsaAggregata;
	}
	public void setValoreRisorsaAggregata(String valoreRisorsaAggregata) throws EngineException {
		if(valoreRisorsaAggregata.length()>TransactionContentUtils.SOGLIA_VALUE_TOO_LONG){
			throw new EngineException("Valore è troppo grande (>"+TransactionContentUtils.SOGLIA_VALUE_TOO_LONG+") per essere utilizzato come filtro. Valore fornito: "+valoreRisorsaAggregata);
		}
		this.valoreRisorsaAggregata = valoreRisorsaAggregata;
	}
	public List<StatisticFilter> getFiltri() {
		return this.filtri;
	}
	public void setFiltri(List<StatisticFilter> filtri) {
		this.filtri = filtri;
	}
}
