package org.openspcoop2.web.monitor.statistiche.bean;

import java.io.Serializable;
import java.util.List;

public class GruppoAnalisiStatistica implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<AnalisiStatistica> listaAnalisiStatistica;
	private String label;
	
	public List<AnalisiStatistica> getListaAnalisiStatistica() {
		return this.listaAnalisiStatistica;
	}
	public void setListaAnalisiStatistica(List<AnalisiStatistica> listaAnalisiStatistica) {
		this.listaAnalisiStatistica = listaAnalisiStatistica;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
}
