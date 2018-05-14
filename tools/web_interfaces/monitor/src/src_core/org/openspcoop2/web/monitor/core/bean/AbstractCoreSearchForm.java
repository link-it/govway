package org.openspcoop2.web.monitor.core.bean;

import javax.faces.event.ActionEvent;

public abstract class AbstractCoreSearchForm {
	
	private Integer numeroPagine = 1;
	private Integer pageSize = 25;
	private Integer currentPage = 1;
	private Integer currentSearchSize = null;
	private Integer start = 0;
	private Integer limit = 25;
	private boolean restoreSearch = false;
	private Integer totalCount = 0;

	private boolean useCount = true;
	protected boolean executeQuery = false;
	private boolean aggiornamentoDatiAbilitato = false;
	
	public String aggiorna() {
		this.initParametriPaginazione();
		return this.eseguiAggiorna();
	}
	
	public String filtra() {
		this.initParametriPaginazione();
		return this.eseguiFiltra();
	}

	public String ripulisci(){
		this.resetParametriPaginazione();
		return this.ripulisciValori();
	}
	
	public void initParametriPaginazione(){
		this.executeQuery = true;
		this.restoreSearch = false;
		this.currentPage = 1;
		this.currentSearchSize = null;
		this.numeroPagine = 1;
		this.start = 0;
		this.limit = 25;
		this.totalCount = 0;
		this.aggiornamentoDatiAbilitato = false;
	}
	
	public void resetParametriPaginazione(){
		this.executeQuery = true;
		this.restoreSearch = false;
		this.currentPage = 1;
		this.currentSearchSize = null;
		this.numeroPagine = 1;
		this.start = 0;
		this.limit = 25;
		this.totalCount = 0;
		this.aggiornamentoDatiAbilitato = false;
	}
	
	protected abstract String ripulisciValori();
	protected abstract String eseguiFiltra();
	protected abstract String eseguiAggiorna();
	
	public void initSearchListener(ActionEvent ae){
		this.resetParametriPaginazione();
	}
	
	public Integer getNumeroPagine() {
		return this.numeroPagine;
	}

	public void setNumeroPagine(Integer numeroPagine) {
		this.numeroPagine = numeroPagine;
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
	}

	public void setExecuteQuery(boolean executeQuery) {
		this.executeQuery = executeQuery;
	}

	public boolean isExecuteQuery() {
		return this.executeQuery;
	}

	public boolean isUseCount() {
		return this.useCount;
	}

	public void setUseCount(boolean useCount) {
		this.useCount = useCount;
	}
	
	public boolean isAggiornamentoDatiAbilitato() {
		return this.aggiornamentoDatiAbilitato;
	}

	public void setAggiornamentoDatiAbilitato(boolean aggiornamentoDatiAbilitato) {
		this.aggiornamentoDatiAbilitato = aggiornamentoDatiAbilitato;
	}

	public Integer getCurrentSearchSize() {
		return this.currentSearchSize;
	}

	public void setCurrentSearchSize(Integer currentSearchSize) {
		this.currentSearchSize = currentSearchSize;
	}
	
}
