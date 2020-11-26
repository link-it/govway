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


package org.openspcoop2.core.commons;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.openspcoop2.utils.sql.ISQLQueryObject;

/**
 * Oggetto che si occupa di memorizzare i valori di ricerca per le liste
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class Search implements Serializable, ISearch {
	private static final long serialVersionUID = 1L;

	private Map<Integer, Integer> pageSize = null;
	private Map<Integer, Integer> indexIniziale = null;
	private Map<Integer, String> searchString = null;
	private Map<Integer, Integer> numEntries = null;
	private Map<Integer, HashMap<String,String>> filter = null;

	private Integer PAGE_SIZE_DEFAULT = 20;
	private Integer INDEX_DEFAULT = 0;
	private String SEARCH_DEFAULT = org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED;
	private Integer NUM_ENTRIES_DEFAULT = 0;
	//private String FILTER_DEFAULT = org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED;

	public Search() {
		this.reset();
	}
	
	public void reset() {
		this.pageSize = new HashMap<Integer, Integer>();
		this.indexIniziale = new HashMap<Integer, Integer>();
		this.searchString = new HashMap<Integer, String>();
		this.numEntries = new HashMap<Integer, Integer>();
		this.filter = new HashMap<Integer, HashMap<String,String>>();

		for (int i = 0; i < Liste.getTotaleListe(); i++) {
			this.pageSize.put(i, this.PAGE_SIZE_DEFAULT);
			this.indexIniziale.put(i, this.INDEX_DEFAULT);
			this.searchString.put(i, this.SEARCH_DEFAULT);
			this.numEntries.put(i, this.NUM_ENTRIES_DEFAULT);
			this.filter.put(i, new HashMap<String,String>());
		}

	}

	public Search(boolean showAllResult) {
		this(showAllResult, ISQLQueryObject.LIMIT_DEFAULT_VALUE);
	}
	public Search(boolean showAllResult, int initialLimit) {
		this.pageSize = new HashMap<Integer, Integer>();
		this.indexIniziale = new HashMap<Integer, Integer>();
		this.searchString = new HashMap<Integer, String>();
		this.numEntries = new HashMap<Integer, Integer>();
		this.filter = new HashMap<Integer, HashMap<String,String>>();

		for (int i = 0; i < Liste.getTotaleListe(); i++) {
			this.pageSize.put(i, initialLimit);// prendo
			// il
			// valore
			// massimo
			// di
			// default
			this.indexIniziale.put(i, this.INDEX_DEFAULT);
			this.searchString.put(i, this.SEARCH_DEFAULT);
			this.numEntries.put(i, this.NUM_ENTRIES_DEFAULT);
			this.filter.put(i, new HashMap<String,String>());
		}

	}

	@Override
	public int getNumEntries(int indexLista) {
		return this.numEntries.get(indexLista);
	}

	@Override
	public void setNumEntries(int indexLista, int numEntries) {
		this.numEntries.put(indexLista, numEntries);
	}

	@Override
	public int getPageSize(int indexLista) {
		return this.pageSize.get(indexLista);
	}

	@Override
	public void setPageSize(int indexLista, int valore) {
		this.pageSize.put(indexLista, valore);
	}

	@Override
	public int getIndexIniziale(int indexLista) {
		return this.indexIniziale.get(indexLista);
	}

	@Override
	public void setIndexIniziale(int indexLista, int valore) {
		this.indexIniziale.put(indexLista, valore);
	}

	@Override
	public String getSearchString(int indexLista) {
		return this.searchString.get(indexLista);
	}

	@Override
	public void setSearchString(int indexLista, String valore) {
		this.searchString.put(indexLista, valore);
	}

	@Override
	public void clearFilters(int indexLista) {
		HashMap<String,String> map = this.filter.get(indexLista);
		if(map!=null) {
			map.clear();
		}
	}
	
	@Override
	public void clearFilter(int indexLista, String filterName) {
		HashMap<String,String> map = this.filter.get(indexLista);
		if(map!=null) {
			map.remove(filterName);
		}
	}
	
	@Override
	public boolean existsFilter(int indexLista, String filterName) {
		HashMap<String,String> map = this.filter.get(indexLista);
		if(map==null || map.size()<=0) {
			return false;
		}
		return map.containsKey(filterName);
	}
	
	@Override
	public String getFilter(int indexLista, String filterName) {
		HashMap<String,String> map = this.filter.get(indexLista);
		if(map==null || map.size()<=0) {
			return null;
		}
		return map.get(filterName);
	}
	
	@Override
	public void addFilter(int indexLista, String filterName, String filterValue) {
		HashMap<String,String> map = this.filter.get(indexLista);
		if(map==null) {
			map = new HashMap<>();
			this.filter.put(indexLista, map);
		}
		if(map.containsKey(filterName)) {
			map.remove(filterName);
		}
		map.put(filterName, filterValue);
	}

}
