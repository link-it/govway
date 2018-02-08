/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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




package org.openspcoop2.web.lib.mvc;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * PageData
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PageData {

	String pageDescription;
	String search;
	String searchDescription;
	boolean searchNote = false;
	String searchLabel;
	int searchNumEntries;
	String mode;
	String message;
	String messageType;
	Vector<GeneralLink> titlelist;
	Vector<?> dati;
	Vector<?> menu;
	Vector<?> areaBottoni;
	Hashtable<String,String> hidden;
	String page;
	String op;
	String [] labels;
	String [][] bottoni = null; // x info-page
	boolean inserisciBottoni;
	boolean addButton;
	boolean removeButton;
	boolean select;
	List<DataElement> filter_names = null;
	List<DataElement> filter_values = null;
	int pageSize, index, numEntries;

	public PageData() {
		this.pageDescription = "";
		this.search = "auto";
		this.searchDescription = "";
		this.searchLabel = "Ricerca";
		this.searchNote = false;
		//this.searchNumEntries = 10;
		this.searchNumEntries = -1; // Per visualizzare sempre
		this.mode = "";
		this.message = "";
		this.messageType = MessageType.ERROR.toString();
		this.page = "";
		this.op = "";
		this.titlelist = new Vector<GeneralLink>();
		this.dati = new Vector<Object>();
		this.menu = new Vector<Object>();
		this.areaBottoni = new Vector<Object>();
		this.hidden = new Hashtable<String,String>();
		this.inserisciBottoni = true;
		this.addButton = true;
		this.removeButton = true;
		this.select = true;
		this.pageSize = 20;
		this.index = 0;
		this.numEntries = 0;
	}

	public void setPageDescription(String s) {
		this.pageDescription = s;
	}
	public String getPageDescription() {
		return this.pageDescription;
	}

	public int getSearchNumEntries() {
		return this.searchNumEntries;
	}
	public void setSearchNumEntries(int searchNumEntries) {
		this.searchNumEntries = searchNumEntries;
	}
	
	public String getSearchLabel() {
		return this.searchLabel;
	}
	public void setSearchLabel(String searchLabel) {
		this.searchLabel = searchLabel;
	}
	
	public boolean isSearchNote() {
		return this.searchNote;
	}
	public void setSearchNote(boolean searchNote) {
		this.searchNote = searchNote;
	}
	
	public void setSearch(String s) {
		this.search = s;
	}
	public String getSearch() {
		return this.search;
	}

	public void setSearchDescription(String s) {
		this.searchDescription = s;
	}
	public String getSearchDescription() {
		if(this.searchDescription != null && !this.searchDescription.equals("")){
			int idx1 = this.searchDescription.indexOf("'");
			int idx2 = this.searchDescription.lastIndexOf("'");

			if(idx1 > -1 && idx2 > -1){
				// elimino ' di destra
				String s = this.searchDescription.substring(0, idx2);
				// elimino ' di sinistra
				return s.substring(idx1 +1);    			 
			}
		}
		return this.searchDescription;
	}

	public void setMode(String s) {
		this.mode = s;
	}
	public void disableEditMode() {
		this.mode = Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME;
	}
	public void disableOnlyButton() {
		this.mode = Costanti.DATA_ELEMENT_DISABLE_ONLY_BUTTON;
	}
	public String getMode() {
		return this.mode;
	}

	public void setMessage(String s) {
		this.setMessage(s, MessageType.ERROR); 
	}
	public void setMessage(String s,MessageType type) {
		this.message = s;
		this.messageType = type.toString();
	}

	public String getMessage() {
		return this.message;
	}

	public String getMessageType() {
		return this.messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public void setPage(String s) {
		this.page = s;
	}
	public String getPage() {
		return this.page;
	}

	public void setOp(String s) {
		this.op = s;
	}
	public String getOp() {
		return this.op;
	}

	public void setTitleList(Vector<GeneralLink> v) {
		this.titlelist = v;
	}
	public Vector<GeneralLink> getTitleList() {
		return this.titlelist;
	}

	public void setDati(Vector<?> v) {
		this.dati = v;
	}
	public Vector<?> getDati() {
		return this.dati;
	}

	public void setMenu(Vector<?> v) {
		this.menu = v;
	}
	public Vector<?> getMenu() {
		return this.menu;
	}

	public void setAreaBottoni(Vector<?> v) {
		this.areaBottoni = v;
	}
	public Vector<?> getAreaBottoni() {
		return this.areaBottoni;
	}

	/* hidden */
	public void setHidden(Hashtable<String,String> v) {
		this.hidden = v;
	}
	public Hashtable<String,String> getHidden() {
		return this.hidden;
	}
	public void addHidden(String name, String value) {
		this.hidden.put(name,value);
	}
	public String getHidden(String name) {
		String value = this.hidden.get(name);
		return value;
	}
	public void clearHidden() {
		this.hidden.clear();
	}

	public void setLabels(String [] s) {
		if( s != null && s.length > 0){
			this.labels = new String[ s.length];
			for (int i = 0; i < s.length; i++) {
				this.labels[i] = DataElement.getEscapedValue( s[i]);
			}
		}else {
			this.labels = s;
		}
	}
	public String [] getLabels() {
		return this.labels;
	}

	public void setBottoni(String [][] s) {
		this.bottoni = s;
	}
	public String [][] getBottoni() {
		return this.bottoni;
	}

	public void setInserisciBottoni(boolean b) {
		this.inserisciBottoni = b;
	}
	public boolean getInserisciBottoni() {
		return this.inserisciBottoni;
	}

	public void setAddButton(boolean b) {
		this.addButton = b;
	}
	public boolean getAddButton() {
		return this.addButton;
	}

	public void setSelect(boolean b) {
		this.select = b;
	}
	public boolean getSelect() {
		return this.select;
	}

	public void setRemoveButton(boolean b) {
		this.removeButton = b;
	}
	public boolean getRemoveButton() {
		return this.removeButton;
	}

	private final static String PARAMETRO_FILTER_NAME = "filterName_";
	public static String GET_PARAMETRO_FILTER_NAME (int position) {
		return PARAMETRO_FILTER_NAME+position;
	}
	private final static String PARAMETRO_FILTER_VALUE = "filterValue_";
	public static String GET_PARAMETRO_FILTER_VALUE (int position) {
		return PARAMETRO_FILTER_VALUE+position;
	}
	
	public void addFilter(String name, String label, String valueSelected, String [] values, String [] labels, boolean postBack, int size) throws Exception{
		if(this.filter_names == null) {
			this.filter_names = new ArrayList<DataElement>();
			this.filter_values = new ArrayList<DataElement>();
		}
		
		DataElement deName = new DataElement();
		deName.setType(DataElementType.HIDDEN);
		deName.setName(GET_PARAMETRO_FILTER_NAME(this.filter_names.size()));
		if(name==null) {
			throw new Exception("Name not found");
		}
		deName.setValue(name);
		this.filter_names.add(deName);
		
		DataElement deValue = new DataElement();
		deValue.setType(DataElementType.SELECT);
		deValue.setName(GET_PARAMETRO_FILTER_VALUE(this.filter_values.size()));
		if(label==null) {
			throw new Exception("Label not found");
		}
		deValue.setLabel(label);
		deValue.setSelected(valueSelected);
		if(values==null || values.length<=0) {
			throw new Exception("Values not found");
		}
		deValue.setValues(values);
		deValue.setLabels(labels);
		deValue.setSize(size);
		deValue.setPostBack(postBack);
		this.filter_values.add(deValue);
		
	}
	public List<DataElement> getFilterNames() {
		return this.filter_names;
	}
	public List<DataElement> getFilterValues() {
		return this.filter_values;
	}
	public boolean hasAlmostOneFilterDefined() {
		if(this.filter_values!=null) {
			for (DataElement de : this.filter_values) {
				if(de.getValue()!=null && !"".equals(de.value)) {
					return true;
				}
			}
		}
		return false;
	}

	public void setPageSize(int i) {
		this.pageSize = i;
	}
	public int getPageSize() {
		return this.pageSize;
	}

	public void setIndex(int i) {
		this.index = i;
	}
	public int getIndex() {
		return this.index;
	}

	public void setNumEntries(int i) {
		this.numEntries = i;
	}
	public int getNumEntries() {
		return this.numEntries;
	}

	public boolean isPageBodyEmpty(){
		if(this.dati.size() > 0) {
			// conto i campi non hidden
			int nonHidden = 0;
			for(int i = 0; i < this.dati.size(); i++){
				DataElement de = (DataElement) this.dati.get(i);
				if(!de.getType().equals(DataElementType.HIDDEN.toString()))
					nonHidden ++;
			}

			return nonHidden == 0; // dati presenti se c'e' almeno un elemento non hidden.
		}

		if(this.mode.equals(Costanti.DATA_ELEMENT_VIEW_NAME))
			return false; // c'e' sempre qualcosa o bottoni o tasto edit

		if(this.mode.equals(Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME) || this.mode.equals(Costanti.DATA_ELEMENT_DISABLE_ONLY_BUTTON)){
			return true;
		}	else{ 
			return false; // bottoni invia/cancella
		}
	}
}
