/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.generic_project.web.form;

/***
 * 
 * Estende il form base aggiungendo i metodi necessari per la gestione di un form di ricerca 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 *
 */
public interface SearchForm extends Form {

	// Reset dei valori di paginazione
	public void resetParametriPaginazione();
	
	// Numero di pagine di risultati da visualizzare
	public Integer getNumeroPagine();
	public void setNumeroPagine(Integer numeroPagine);

	// Numero di risultati per pagina
	public Integer getPageSize();
	public void setPageSize(Integer pageSize);

	// Pagina Corrente
	public Integer getCurrentPage();
	public void setCurrentPage(Integer currentPage);

	// Offset nell'insieme dei dati
	public Integer getStart();
	public void setStart(Integer start);

	// Limit della pagina 
	public Integer getLimit();
	public void setLimit(Integer limit);

	// Indica se e' una nuova ricerca oppure se utilizzare i risultati gia' calcolati
	public boolean isRestoreSearch();
	public void setRestoreSearch(boolean restoreSearch);
	
	// Numero totale di risultati per la ricerca
	public Integer getTotalCount();
	public void setTotalCount(Integer totalCount);
	
}
