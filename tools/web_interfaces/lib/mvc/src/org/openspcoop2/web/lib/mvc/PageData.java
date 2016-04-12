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




package org.openspcoop2.web.lib.mvc;

import java.util.Vector;
import java.util.Hashtable;

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
    String mode;
    String message;
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
    DataElement filter = null;
    int pageSize, index, numEntries;

    public PageData() {
    	this.pageDescription = "";
    	this.search = "auto";
    	this.searchDescription = "";
    	this.mode = "";
    	this.message = "";
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
    	this.message = s;
    }
    public String getMessage() {
	return this.message;
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

    public void setFilter(DataElement d) {
    	this.filter = d;
    }
    public DataElement getFilter() {
	return this.filter;
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
}
