package org.openspcoop2.web.monitor.core.report;

import java.util.ArrayList;
import java.util.List;

public class ReportDataSource {
	
	public ReportDataSource() {
		this.dati = new ArrayList<>();
		this.header = new ArrayList<>();
		this.colonne = new ArrayList<>();
	}
	
	public ReportDataSource(List<String> header) {
		this.dati = new ArrayList<>();
		this.header = header;
		this.colonne = new ArrayList<>();
	}

	private List<String> header;
	private List<List<String>> dati;
	private List<Colonna> colonne;
	
	public List<String> getHeader() {
		return this.header;
	}
	public void setHeader(List<String> header) {
		this.header = header;
	}
	public List<List<String>> getDati() {
		return this.dati;
	}
	public void setDati(List<List<String>> dati) {
		this.dati = dati;
	}
	public void add(List<String> line) {
		this.dati.add(line);
	}
	public List<String> getLabelColonne() {
		return this.colonne.stream().map(Colonna::getLabel).toList();
	}
	public List<Colonna> getColonne() {
		return this.colonne;
	}
	public void setColonne(List<Colonna> colonne) {
		this.colonne = colonne;
	}
}
