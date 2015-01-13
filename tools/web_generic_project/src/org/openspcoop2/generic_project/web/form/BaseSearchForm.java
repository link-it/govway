/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.web.form;


/**
* BaseSearchForm Class base per la gestione dei form di ricerca all'interno della console.
* 
* La classe definisce le informazioni di paginazione base dei risultati:
* maxPages: numero massimo di pagine di risultati da presentare;
* pageIndex: indice di pagina;
* pageSize: numero di risultati per pagina;
* currentPage: pagina corrente;
* start: numero di partenza del record da visualizzare;
* limit: start + pageSize;
* newSearch: indica se sto effettuando una nuova ricerca;
* totalCount: numero totale di risultati che la ricerca produce;
* 
* fields: mappa contenente i fields della ricerca.
* nomeForm: label della form da visualizzare;
* closable: indica se la form sara' visualizzata dentro un pannello che si puo' nascondere oppure no;
* idForm: id della form.
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public abstract class BaseSearchForm extends BaseForm{
	
	public static final String DEFAULT_FORM_ID = "formFiltra";

	private Integer maxPages = 1;
	
	private Integer pageIndex = 1;
	
	private Integer pageSize = 25;
	
	private Integer currentPage = 0;
	
	private Integer start = 0;
	
	private Integer limit = 25;
	
	private boolean newSearch = false;
	
	private boolean restoreSearch = false;
	
	private Integer totalCount = 0;
	
	public BaseSearchForm(){
		super();
	}
	
	public void resetParametriPaginazione(){
		this.newSearch = true;
		this.restoreSearch = false;
		this.currentPage = 0;
		this.maxPages = 1;
		this.pageIndex = 1;
		this.start = 0;
		this.limit = 25;
		this.totalCount = 0;
	}
	
	public Integer getMaxPages() {
		return this.maxPages;
	}

	public void setMaxPages(Integer maxPages) {
		this.maxPages = maxPages;
	}

	public Integer getPageIndex() {
		return this.pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getCurrentPage() {
		return this.currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getStart() {
		return this.start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLimit() {
		return this.limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	// Eliminare
	
	@Deprecated
	public boolean executeCount() {
		return this.newSearch;
	}
	
	@Deprecated
	public void setExecuteCount(boolean newSearch) {
		this.newSearch = newSearch;
	}
	
	

	public boolean isNewSearch() {
		return this.newSearch;
	}

	public void setNewSearch(boolean newSearch) {
		this.newSearch = newSearch;
	}

	public boolean isRestoreSearch() {
		return this.restoreSearch;
	}

	public void setRestoreSearch(boolean restoreSearch) {
		this.restoreSearch = restoreSearch;
	}

	

	public Integer getTotalCount() {
		return this.totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
		
		this.maxPages = this.totalCount / this.getPageSize();
		
		if (this.totalCount % this.getPageSize() != 0) {
			this.maxPages++;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("******* Stato Filtro *******\n");
		sb.append("\tNewSearch: [").append(this.newSearch).append("]\n");
		sb.append("\tRestoreSearch: [").append(this.restoreSearch).append("]\n");
		sb.append("\tTotaleRisultati: [").append(this.totalCount).append("]\n");
		sb.append("\tIndice Start: [").append(this.start).append("]\n");
		sb.append("\tLimit: [").append(this.limit).append("]\n");
		sb.append("\tPagina Corrente: [").append(this.currentPage).append("]\n");
		sb.append("\tTotale Pagine: [").append(this.maxPages).append("]\n");
		sb.append("*******************************\n");
		
		return sb.toString();
	}

}
