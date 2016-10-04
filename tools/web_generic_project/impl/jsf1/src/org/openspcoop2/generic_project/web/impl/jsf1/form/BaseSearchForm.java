/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.web.impl.jsf1.form;


import org.openspcoop2.generic_project.web.form.CostantiForm;
import org.openspcoop2.generic_project.web.form.SearchForm;

/***
 * 
 * Implementazione base di un form di ricerca.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public abstract class BaseSearchForm extends BaseForm implements SearchForm {

	private Integer numeroPagine = 1;
	
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
	
	@Override
	public void resetParametriPaginazione(){
		this.newSearch = true;
		this.restoreSearch = false;
		this.currentPage = 0;
		this.numeroPagine = 1;
		this.pageIndex = 1;
		this.start = 0;
		this.limit = 25;
		this.totalCount = 0;
	}
	
	@Override
	public Integer getNumeroPagine() {
		return this.numeroPagine;
	}

	@Override
	public void setNumeroPagine(Integer numeroPagine) {
		this.numeroPagine = numeroPagine;
		
	}

	public Integer getPageIndex() {
		return this.pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	@Override
	public Integer getPageSize() {
		return this.pageSize;
	}

	@Override
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public Integer getCurrentPage() {
		return this.currentPage;
	}

	@Override
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	@Override
	public Integer getStart() {
		return this.start;
	}

	@Override
	public void setStart(Integer start) {
		this.start = start;
	}

	@Override
	public Integer getLimit() {
		return this.limit;
	}

	@Override
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	// Eliminare
	
	public boolean isNewSearch() {
		return this.newSearch;
	}

	public void setNewSearch(boolean newSearch) {
		this.newSearch = newSearch;
	}

	@Override
	public boolean isRestoreSearch() {
		return this.restoreSearch;
	}

	@Override
	public void setRestoreSearch(boolean restoreSearch) {
		this.restoreSearch = restoreSearch;
	}

	

	@Override
	public Integer getTotalCount() {
		return this.totalCount;
	}

	@Override
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
		
		this.numeroPagine = this.totalCount / this.getPageSize();
		
		if (this.totalCount % this.getPageSize() != 0) {
			this.numeroPagine++;
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
		sb.append("\tTotale Pagine: [").append(this.numeroPagine).append("]\n");
		sb.append("*******************************\n");
		
		return sb.toString();
	}
	
	@Override
	public String getId() {
		if(this.id == null)
			this.id= CostantiForm.DEFAULT_SEARCH_FORM_ID;

		return this.id;
	}
 
	@Override
	public void setObject(Object object) throws Exception {

	}

	@Override
	public Object getObject() throws Exception {return null;
	}


}
