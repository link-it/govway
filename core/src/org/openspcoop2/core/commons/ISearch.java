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





package org.openspcoop2.core.commons;

/**
 * Interfaccia per Oggetto Search
 * Un oggetto Search e' in grado di gestire le informazioni di Ricerca relative a piu
 * liste.
 * 
 * @author Stefano Corallo - corallo@link.it
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface ISearch
{
	/**
	 * Ritorna la pageSize associata
	 * @param indexLista
	 * @return la dimensione della Page
	 */
	public int getPageSize(int indexLista);
	
	public void setPageSize(int indexLista,int valore);

	public int getIndexIniziale(int indexLista);
	
	public void setIndexIniziale(int indexLista, int valore);
	
	public String getSearchString(int indexLista);
	
	public void setSearchString(int indexLista, String valore);
	
	public int getNumEntries(int indexLista);
	public void setNumEntries(int indexLista, int numEntries);

	public void clearFilters(int indexLista);
	public void clearFilter(int indexLista, String filterName);
	public boolean existsFilter(int indexLista, String filterName);
	public String getFilter(int indexLista, String filterName);
	public void addFilter(int indexLista, String filterName, String filterValue);
}
