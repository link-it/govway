/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.bean;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.openspcoop2.web.monitor.core.constants.Costanti;

/**
 * AbstractCoreSearchForm
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
	private boolean visualizzaSelezioneDimensionePagina = false;
	private boolean visualizzaFiltroAperto = true;
	
	public String aggiorna() {
		this.initParametriPaginazione();
		this.aggiornaStatoFiltroRicercaEseguiAggiorna();
		return this.eseguiAggiorna();
	}
	
	public String filtra() {
		this.initParametriPaginazione();
		this.aggiornaStatoFiltroRicercaEseguiFiltra();
		return this.eseguiFiltra();
	}

	public String ripulisci(){
		this.resetParametriPaginazione();
		this.initStatoFiltroRicerca();
		return this.ripulisciValori();
	}
	
	public void initParametriPaginazione(){
		this.executeQuery = true;
		this.restoreSearch = false;
		this.currentPage = 1;
		this.currentSearchSize = null;
		this.numeroPagine = 1;
		this.start = 0;
		//this.limit = 25;
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
		this.initStatoFiltroRicerca();
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
	
	
	public List<SelectItem> getListaNumeroRisultati(){
		return Costanti.SELECT_ITEM_ENTRIES;
	}
	
	public void limitSelected(ActionEvent ae) {
		
	}

	public boolean isVisualizzaSelezioneDimensionePagina() {
		return this.visualizzaSelezioneDimensionePagina;
	}

	public void setVisualizzaSelezioneDimensionePagina(boolean visualizzaSelezioneDimensionePagina) {
		this.visualizzaSelezioneDimensionePagina = visualizzaSelezioneDimensionePagina;
	}

	public boolean isVisualizzaFiltroAperto() {
		return this.visualizzaFiltroAperto;
	}

	public void setVisualizzaFiltroAperto(boolean visualizzaFiltroAperto) {
		this.visualizzaFiltroAperto = visualizzaFiltroAperto;
	}
	
	protected void initStatoFiltroRicerca () {
		this.setVisualizzaFiltroAperto(true);
	}
	
	protected void aggiornaStatoFiltroRicercaEseguiFiltra () {
		this.setVisualizzaFiltroAperto(false);
	}
	
	protected void aggiornaStatoFiltroRicercaEseguiAggiorna () {
		this.setVisualizzaFiltroAperto(false);
	}
}
