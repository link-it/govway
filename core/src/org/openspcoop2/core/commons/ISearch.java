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
	
	public String getFilter(int indexLista);
	
	public void setFilter(int indexLista, String valore);
}
