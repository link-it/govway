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
package org.openspcoop2.web.monitor.core.report;

import java.util.ArrayList;
import java.util.List;

/**
 * ReportDataSource
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
