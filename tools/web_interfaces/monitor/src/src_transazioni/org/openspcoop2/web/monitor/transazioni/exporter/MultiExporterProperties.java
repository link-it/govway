package org.openspcoop2.web.monitor.transazioni.exporter;


public class MultiExporterProperties extends ExporterProperties{

	private int maxTransazioniPerFile;
	
	public int getMaxTransazioniPerFile() {
		return this.maxTransazioniPerFile;
	}
	
	public void setMaxTransazioniPerFile(int maxTransazioniPerFile) {
		this.maxTransazioniPerFile = maxTransazioniPerFile;
	}
}
