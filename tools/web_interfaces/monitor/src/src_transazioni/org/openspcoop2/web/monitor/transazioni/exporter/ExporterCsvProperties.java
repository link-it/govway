package org.openspcoop2.web.monitor.transazioni.exporter;

import java.util.List;

public class ExporterCsvProperties extends ExporterProperties {

	
	private String formato =null;
	private List<String> colonneSelezionate= null;
	
	public String getFormato() {
		return this.formato;
	}
	public void setFormato(String formato) {
		this.formato = formato;
	}
	public List<String> getColonneSelezionate() {
		return this.colonneSelezionate;
	}
	public void setColonneSelezionate(List<String> colonneSelezionate) {
		this.colonneSelezionate = colonneSelezionate;
	}
	
	
}
