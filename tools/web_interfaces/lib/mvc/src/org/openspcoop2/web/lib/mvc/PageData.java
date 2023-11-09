/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.web.lib.mvc.DataElement.STATO_APERTURA_SEZIONI;

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
public class PageData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String pageDescription;
	String search;
	String searchDescription;
	boolean searchNote = false;
	String searchLabel;
	int searchNumEntries;
	String mode;
	String message;
	String messageType;
	String messageTitle;
	List<GeneralLink> titlelist;
	List<?> dati;
	List<?> menu;
	List<?> areaBottoni;
	Map<String,String> hidden;
	String page;
	String op;
	String [] labels;
	String [][] bottoni = null; // x info-page
	boolean inserisciBottoni;
	boolean addButton;
	boolean removeButton;
	boolean select;
	List<DataElement> filterNames = null;
	List<DataElement> filterValues = null;
	int pageSize, index, numEntries;
	boolean mostraLinkHome = false;
	List<String> linkHomeLabels = null;
	String customListViewName = null;
	String labelBottoneInvia = null;
	String labelBottoneFiltra = null;
	String labelBottoneRipulsci = null;
	
	private boolean showAjaxStatusBottoneInvia = true;
	private boolean showAjaxStatusBottoneFiltra = true;
	private boolean showAjaxStatusBottoneRipulisci = true;
	
	private List<DataElement> comandiAzioneBarraTitoloDettaglioElemento;
	
	
	Dialog dialog = null;

	boolean postBackResult=false;
	boolean includiMenuLateraleSx = true;
	
	private boolean paginazione = true;
	
	private boolean inserisciSearch = true;
	
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
		this.messageTitle = "";
		this.page = "";
		this.op = "";
		this.titlelist = new ArrayList<>();
		this.dati = new ArrayList<>();
		this.menu = new ArrayList<>();
		this.areaBottoni = new ArrayList<>();
		this.hidden = new HashMap<>();
		this.inserisciBottoni = true;
		this.addButton = true;
		this.removeButton = true;
		this.select = true;
		this.pageSize = 20;
		this.index = 0;
		this.numEntries = 0;
		this.mostraLinkHome = false;
		this.customListViewName = null;
		this.labelBottoneInvia = Costanti.LABEL_MONITOR_BUTTON_INVIA;
		this.labelBottoneFiltra = Costanti.LABEL_MONITOR_BUTTON_FILTRA;
		this.labelBottoneRipulsci = Costanti.LABEL_MONITOR_BUTTON_RIPULISCI;
		this.postBackResult=false;
		this.includiMenuLateraleSx = true;
		this.paginazione = true;
		this.comandiAzioneBarraTitoloDettaglioElemento = new ArrayList<>();
		this.setInserisciSearch(true);
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
	public boolean isDisableEditMode() {
		return Costanti.DATA_ELEMENT_EDIT_MODE_DISABLE_NAME.equals(this.mode);
	}

	public void setMessage(String s) {
		this.setMessage(s, MessageType.ERROR); 
	}
	
	public void setMessage(String s,MessageType type) {
		setMessage(s, null, type);
	}
	public void setMessage(String s, String title, MessageType type) {
		this.message = s;
		this.messageType = type.toString();
		this.messageTitle = title;
		if(this.messageTitle == null) {
			switch (type) {
			case CONFIRM:
				this.messageTitle = Costanti.MESSAGE_TYPE_CONFIRM_TITLE;
				break;
			case ERROR:
				this.messageTitle = Costanti.MESSAGE_TYPE_ERROR_TITLE;
				break;
			case WARN:
				this.messageTitle = Costanti.MESSAGE_TYPE_WARN_TITLE;
				break;
			case INFO:
				this.messageTitle = Costanti.MESSAGE_TYPE_INFO_TITLE;
				break;
			case INFO_SINTETICO:
			case ERROR_SINTETICO:
			case WARN_SINTETICO:
				this.messageTitle = this.message;
				break;
			case DIALOG:
				this.messageTitle = Costanti.MESSAGE_TYPE_DIALOG_TITLE;
				break;
			}
		}
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
	
	public String getMessageTitle() {
		return this.messageTitle;
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

	public void setTitleList(List<GeneralLink> v) {
		this.titlelist = v;
	}
	public List<GeneralLink> getTitleList() {
		return this.titlelist;
	}

	public void setDati(List<?> v) {
		this.dati = v;
	}
	public List<?> getDati() {
		return this.dati;
	}

	public void setMenu(List<?> v) {
		this.menu = v;
	}
	public List<?> getMenu() {
		return this.menu;
	}

	public void setAreaBottoni(List<?> v) {
		this.areaBottoni = v;
	}
	public List<?> getAreaBottoni() {
		return this.areaBottoni;
	}

	/* hidden */
	public void setHidden(Map<String,String> v) {
		this.hidden = v;
	}
	public Map<String,String> getHidden() {
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

	public static String GET_PARAMETRO_FILTER_NAME (int position) {
		return Costanti.PARAMETRO_FILTER_NAME+position;
	}
	public static String GET_PARAMETRO_FILTER_VALUE (int position) {
		return Costanti.PARAMETRO_FILTER_VALUE+position;
	}
	
	public void addFilter(String name, String label, String valueSelected, String [] values, String [] labels, boolean postBack, int size) throws Exception{
		this.addFilter(name, label, valueSelected, values, labels, postBack, size, false);
	}
	
	public void addFilter(String name, String label, String valueSelected, String [] values, String [] labels, boolean postBack, int size, boolean disabilitaFiltroRisultati) throws Exception{
		if(this.filterNames == null) {
			this.filterNames = new ArrayList<DataElement>();
			this.filterValues = new ArrayList<DataElement>();
		}
		
		DataElement deName = new DataElement();
		deName.setType(DataElementType.HIDDEN);
		deName.setName(GET_PARAMETRO_FILTER_NAME(this.filterNames.size()));
		if(name==null) {
			throw new Exception("Name not found");
		}
		deName.setValue(name);
		this.filterNames.add(deName);
		
		DataElement deValue = new DataElement();
		deValue.setType(DataElementType.SELECT);
		deValue.setName(GET_PARAMETRO_FILTER_VALUE(this.filterValues.size()));
		if(label==null) {
			throw new Exception("Label not found");
		}
		deValue.setLabel(label);
		deValue.setSelected(valueSelected);
		deValue.setValue(valueSelected);
		if(values==null || values.length<=0) {
			throw new Exception("Values not found");
		}
		deValue.setValues(values);
		deValue.setLabels(labels);
		deValue.setSize(size);
		deValue.setPostBack(postBack);
		if(disabilitaFiltroRisultati)
			deValue.disabilitaFiltroOpzioniSelect();
		this.filterValues.add(deValue);
		
	}
	
	public void addTextFilter(String name, String label, String value, int size) throws Exception{
		if(this.filterNames == null) {
			this.filterNames = new ArrayList<DataElement>();
			this.filterValues = new ArrayList<DataElement>();
		}
		
		DataElement deName = new DataElement();
		deName.setType(DataElementType.HIDDEN);
		deName.setName(GET_PARAMETRO_FILTER_NAME(this.filterNames.size()));
		if(name==null) {
			throw new Exception("Name not found");
		}
		deName.setValue(name);
		this.filterNames.add(deName);
		
		DataElement deValue = new DataElement();
		deValue.setType(DataElementType.TEXT_EDIT);
		deValue.setName(GET_PARAMETRO_FILTER_VALUE(this.filterValues.size()));
		if(label==null) {
			throw new Exception("Label not found");
		}
		deValue.setLabel(label);
		deValue.setValue(value);
		deValue.setSize(size);
		this.filterValues.add(deValue);
	}
	
	public void addTextAreaFilter(String name, String label, String value, int size, Integer rows, Integer cols) throws Exception{
		if(this.filterNames == null) {
			this.filterNames = new ArrayList<DataElement>();
			this.filterValues = new ArrayList<DataElement>();
		}
		
		DataElement deName = new DataElement();
		deName.setType(DataElementType.HIDDEN);
		deName.setName(GET_PARAMETRO_FILTER_NAME(this.filterNames.size()));
		if(name==null) {
			throw new Exception("Name not found");
		}
		deName.setValue(name);
		this.filterNames.add(deName);
		
		DataElement deValue = new DataElement();
		deValue.setType(DataElementType.TEXT_AREA);
		deValue.setName(GET_PARAMETRO_FILTER_VALUE(this.filterValues.size()));
		if(label==null) {
			throw new Exception("Label not found");
		}
		deValue.setLabel(label);
		deValue.setValue(value);
		deValue.setSize(size);
		if(rows != null)
			deValue.setRows(rows.intValue());
		if(cols != null)
			deValue.setCols(cols.intValue());
		
		this.filterValues.add(deValue);
	}
	
	public void addNumberFilter(String name, String label, String value, int size, Integer min, Integer max) throws Exception{
		if(this.filterNames == null) {
			this.filterNames = new ArrayList<DataElement>();
			this.filterValues = new ArrayList<DataElement>();
		}
		
		DataElement deName = new DataElement();
		deName.setType(DataElementType.HIDDEN);
		deName.setName(GET_PARAMETRO_FILTER_NAME(this.filterNames.size()));
		if(name==null) {
			throw new Exception("Name not found");
		}
		deName.setValue(name);
		this.filterNames.add(deName);
		
		DataElement deValue = new DataElement();
		deValue.setType(DataElementType.NUMBER);
		deValue.setName(GET_PARAMETRO_FILTER_VALUE(this.filterValues.size()));
		if(label==null) {
			throw new Exception("Label not found");
		}
		deValue.setLabel(label);
		deValue.setValue(value);
		deValue.setSize(size);
		deValue.setMinValue(min);
		deValue.setMaxValue(max);
		this.filterValues.add(deValue);
	}
	
	public void addCheckboxFilter(String name, String label, String value, int size) throws Exception{
		if(this.filterNames == null) {
			this.filterNames = new ArrayList<DataElement>();
			this.filterValues = new ArrayList<DataElement>();
		}
		
		DataElement deName = new DataElement();
		deName.setType(DataElementType.HIDDEN);
		deName.setName(GET_PARAMETRO_FILTER_NAME(this.filterNames.size()));
		if(name==null) {
			throw new Exception("Name not found");
		}
		deName.setValue(name);
		this.filterNames.add(deName);
		
		DataElement deValue = new DataElement();
		deValue.setType(DataElementType.CHECKBOX);
		deValue.setName(GET_PARAMETRO_FILTER_VALUE(this.filterValues.size()));
		if(label==null) {
			throw new Exception("Label not found");
		}
		deValue.setLabel(label);
		deValue.setValue(value);
		deValue.setSelected(value);
		deValue.setSize(size);
		this.filterValues.add(deValue);
	}
	
	public void addSubtitleFilter(String name, String label, boolean visualizzaSottosezioneAperta) throws Exception{
		if(this.filterNames == null) {
			this.filterNames = new ArrayList<DataElement>();
			this.filterValues = new ArrayList<DataElement>();
		}
		
		// devo salvare anche l'elemento name perche' la jsp naviga sia names che values contemporaneamente per posizione.
		DataElement deName = new DataElement();
		deName.setType(DataElementType.HIDDEN);
		deName.setName(GET_PARAMETRO_FILTER_NAME(this.filterNames.size()));
		if(name==null) {
			throw new Exception("Name not found");
		}
		deName.setValue(name);
		this.filterNames.add(deName);
		
		DataElement deValue = new DataElement();
		deValue.setType(DataElementType.SUBTITLE);
		deValue.setName(GET_PARAMETRO_FILTER_VALUE(this.filterValues.size()));
		if(label==null) {
			throw new Exception("Label not found");
		}
		deValue.setLabel(label);
		deValue.setStatoAperturaSezioni(visualizzaSottosezioneAperta ? STATO_APERTURA_SEZIONI.APERTO : STATO_APERTURA_SEZIONI.CHIUSO);
		this.filterValues.add(deValue);
	}
	
	
	public void impostaAperturaSubtitle(String name, Boolean visualizzaSottosezioneAperta, String postbackElementName) throws Exception {
		
		if(name==null) {
			throw new Exception("Param name is null");
		}
		
		if(this.filterNames != null) {
			int idxSubtitle = -1;
			for (int i = 0; i < this.filterNames.size(); i++) {
				if(name.equals(this.filterNames.get(i).getValue())) {
					idxSubtitle = i;
					break;
				}
			}
			
			if(visualizzaSottosezioneAperta == null) {
				// se ho trovato il subtitle allora prendo i filtri successivi
				// finche non trovo un altro subtitle o finisce la lista
				if(idxSubtitle > -1) {
					List<DataElement> filter_values_to_check = new ArrayList<DataElement>();
					
					for (int i = idxSubtitle + 1; i < this.filterNames.size(); i++) {
						DataElement de = this.filterValues.get(i);
						if(de.getType().equals("subtitle")) {
							// ho trovato un'altra sezione mi fermo
							break;
						} else {
							filter_values_to_check.add(de);
						}
					}
					visualizzaSottosezioneAperta = this.hasAlmostOneFilterDefined(filter_values_to_check);
					
					// se c'e' stata una postback la sezione dell'elemento che ha provocato il reload deve restare aperta 
					if(postbackElementName != null) {
						for (int i = 0; i < filter_values_to_check.size(); i++) {
							if(filter_values_to_check.get(i).getName().equals(postbackElementName)) {
								visualizzaSottosezioneAperta = true;
								break;
							}
						}
					}
				}
			}
			
			if(visualizzaSottosezioneAperta!=null) {
				this.updateSubtitleFilter(name, visualizzaSottosezioneAperta);
			}
		}
	}
	
	public void updateSubtitleFilter(String name, boolean visualizzaSottosezioneAperta) {
		if(this.filterNames != null) {
			for (int i = 0; i < this.filterNames.size(); i++) {
				if(name.equals(this.filterNames.get(i).getValue())) {
					this.filterValues.get(i).setStatoAperturaSezioni(visualizzaSottosezioneAperta ? STATO_APERTURA_SEZIONI.APERTO : STATO_APERTURA_SEZIONI.CHIUSO);
					break;
				}
			}
		}
	}
	
	public void addHiddenFilter(String name, String value, int size) throws Exception{
		if(this.filterNames == null) {
			this.filterNames = new ArrayList<DataElement>();
			this.filterValues = new ArrayList<DataElement>();
		}
		
		DataElement deName = new DataElement();
		deName.setType(DataElementType.HIDDEN);
		deName.setName(GET_PARAMETRO_FILTER_NAME(this.filterNames.size()));
		if(name==null) {
			throw new Exception("Name not found");
		}
		deName.setValue(name);
		this.filterNames.add(deName);
		
		DataElement deValue = new DataElement();
		deValue.setType(DataElementType.HIDDEN);
		deValue.setName(GET_PARAMETRO_FILTER_VALUE(this.filterValues.size()));
		deValue.setLabel(deValue.getName());
		deValue.setValue(value);
		deValue.setSize(size);
		this.filterValues.add(deValue);
	}
	
	public void updateFilter(String name, String value) {
		if(this.filterNames != null) {
			for (int i = 0; i < this.filterNames.size(); i++) {
				if(name.equals(this.filterNames.get(i).getValue())) {
					this.filterValues.get(i).setValue(value);
					break;
				}
			}
		}
	}
	
	public void removeFilter(String name) {
		if(this.filterNames != null) {
			for (int i = 0; i < this.filterNames.size(); i++) {
				if(name.equals(this.filterNames.get(i).getValue())) {
					this.filterNames.remove(i);
					this.filterValues.remove(i);
					break;
				}
			}
		}
	}
	
	@Deprecated
	public void setFilterNames(List<DataElement> filterNames) {
		this.filterNames = filterNames;
	}
	public List<DataElement> getFilterNames() {
		return this.filterNames;
	}
	@Deprecated
	public void setFilterValues(List<DataElement> filterValues) {
		this.filterValues = filterValues;
	}
	public List<DataElement> getFilterValues() {
		return this.filterValues;
	}
	public boolean hasAlmostOneFilterDefined() {
		return this.hasAlmostOneFilterDefined(this.filterValues);
	}
	
	private boolean hasAlmostOneFilterDefined(List<DataElement> filter_values_to_check) {
		if(filter_values_to_check!=null) {
			for (DataElement de : filter_values_to_check) {
				if(!de.getType().equals("hidden") && de.getValue()!=null && !("".equals(de.value) || Costanti.SA_TIPO_DEFAULT_VALUE.equals(de.value))) {
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
				Object o = this.dati.get(i);
				if(o instanceof DataElement) {
					DataElement de = (DataElement) o;
					if(!de.getType().equals(DataElementType.HIDDEN.toString()))
						nonHidden ++;
				} else if(o instanceof List<?>) {
					List<?> v = (List<?>) o;
					for(int j = 0; j < v.size(); j++){
						Object o02 = v.get(j);
						if(o02 instanceof DataElement) {
							DataElement de = (DataElement) o02;
							if(!de.getType().equals(DataElementType.HIDDEN.toString()))
								nonHidden ++;
						} 
					}
				}
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

	public boolean isMostraLinkHome() {
		return this.mostraLinkHome;
	}

	public void setMostraLinkHome(boolean mostraLinkHome) {
		this.mostraLinkHome = mostraLinkHome;
	}
	
	public List<String> getLinkHomeLabels() {
		return this.linkHomeLabels;
	}

	public void setLinkHomeLabels(String pre, String labelLink, String post ) {
		this.linkHomeLabels = new ArrayList<>();

		this.linkHomeLabels.add(StringUtils.isNotBlank(pre) ? pre : "");
		this.linkHomeLabels.add(StringUtils.isNotBlank(labelLink) ? labelLink : Costanti.MESSAGGIO_SISTEMA_NON_DISPONIBILE_BACK_HOME);
		this.linkHomeLabels.add(StringUtils.isNotBlank(post) ? post : "");
	}
	
	public List<String> getDefaultLinkHomeLabels() {
		List<String> l = new ArrayList<>();
		l.add(Costanti.MESSAGGIO_SISTEMA_NON_DISPONIBILE_BACK_HOME_PRE);
		l.add(Costanti.MESSAGGIO_SISTEMA_NON_DISPONIBILE_BACK_HOME);
		l.add(Costanti.MESSAGGIO_SISTEMA_NON_DISPONIBILE_BACK_HOME_POST);
		return l;
	}

	public String getCustomListViewName() {
		return this.customListViewName;
	}

	public void setCustomListViewName(String customListViewName) {
		this.customListViewName = customListViewName;
	}
	
	public String getLabelBottoneInvia() {
		return this.labelBottoneInvia;
	}

	public void setLabelBottoneInvia(String labelBottoneInvia) {
		this.labelBottoneInvia = labelBottoneInvia;
	}
	
	public String getLabelBottoneFiltra() {
		return this.labelBottoneFiltra;
	}

	public void setLabelBottoneFiltra(String labelBottoneFiltra) {
		this.labelBottoneFiltra = labelBottoneFiltra;
	}

	public String getLabelBottoneRipulsci() {
		return this.labelBottoneRipulsci;
	}

	public void setLabelBottoneRipulsci(String labelBottoneRipulsci) {
		this.labelBottoneRipulsci = labelBottoneRipulsci;
	}

	public boolean isShowAjaxStatusBottoneInvia() {
		return this.showAjaxStatusBottoneInvia;
	}

	public void setShowAjaxStatusBottoneInvia(boolean showAjaxStatusBottoneInvia) {
		this.showAjaxStatusBottoneInvia = showAjaxStatusBottoneInvia;
	}
	
	public void setDisabilitaAjaxStatusBottoneInvia() {
		this.showAjaxStatusBottoneInvia = false;
	}

	public boolean isShowAjaxStatusBottoneFiltra() {
		return this.showAjaxStatusBottoneFiltra;
	}

	public void setShowAjaxStatusBottoneFiltra(boolean showAjaxStatusBottoneFiltra) {
		this.showAjaxStatusBottoneFiltra = showAjaxStatusBottoneFiltra;
	}
	
	public void setDisabilitaAjaxStatusBottoneFiltra() {
		this.showAjaxStatusBottoneFiltra = false;
	}

	public boolean isShowAjaxStatusBottoneRipulisci() {
		return this.showAjaxStatusBottoneRipulisci;
	}

	public void setShowAjaxStatusBottoneRipulisci(boolean showAjaxStatusBottoneRipulisci) {
		this.showAjaxStatusBottoneRipulisci = showAjaxStatusBottoneRipulisci;
	}
	
	public void setDisabilitaAjaxStatusBottoneRipulisci() {
		this.showAjaxStatusBottoneRipulisci = false;
	}
	
	public Dialog getDialog() {
		return this.dialog;
	}

	public void setDialog(Dialog dialog) {
		this.messageType = MessageType.DIALOG.toString();
		this.dialog = dialog;
		if(this.dialog != null) {
			this.message = this.dialog.getTitolo();
			this.messageTitle = this.dialog.getTitolo();
		}
	}

	
	public boolean isPostBackResult() {
		return this.postBackResult;
	}
	public void setPostBackResult(boolean postBackResult) {
		this.postBackResult = postBackResult;
	}

	public boolean isIncludiMenuLateraleSx() {
		return this.includiMenuLateraleSx;
	}

	public void setIncludiMenuLateraleSx(boolean includiMenuLateraleSx) {
		this.includiMenuLateraleSx = includiMenuLateraleSx;
	}
	
	public DataElement convertiSearchInTextFilterMetadata() throws Exception{
		DataElement deName = new DataElement();
		deName.setType(DataElementType.HIDDEN);
		deName.setName(Costanti.SEARCH_PARAMETER_NAME_FAKE_NAME);
		deName.setValue(Costanti.SEARCH_PARAMETER_NAME);
		
		return deName;
	}
	
	public DataElement convertiSearchInTextFilter() throws Exception{
		String searchDescription = this.getSearchDescription();
		String searchLabelName = this.getSearchLabel();
		boolean searchNote = this.isSearchNote();
		
		
		DataElement deValue = new DataElement();
		deValue.setType(DataElementType.TEXT_EDIT);
		deValue.setName(Costanti.SEARCH_PARAMETER_NAME);
		deValue.setLabel(searchLabelName);
		deValue.setValue(searchDescription);
		deValue.setSize(Costanti.SEARCH_PARAMETER_DEFAULT_SIZE);
		
		if(searchNote && !searchDescription.equals("")){
			deValue.setNote(MessageFormat.format(Costanti.SEARCH_PARAMETER_NOTE, searchDescription));
		}
		
		return deValue;
	}
	
	public boolean isPaginazione() {
		return this.paginazione;
	}

	public void setPaginazione(boolean paginazione) {
		this.paginazione = paginazione;
	}

	public List<DataElement> getComandiAzioneBarraTitoloDettaglioElemento() {
		return this.comandiAzioneBarraTitoloDettaglioElemento;
	}
	
	public void addComandoResetCacheElementoButton(String servletName, List<Parameter> parameters) {
		if(parameters == null) {
			parameters = new ArrayList<>();
		}
		// aggiungo parametri postback (come si fa in postback.js)
		parameters.add(new Parameter(Costanti.POSTBACK_ELEMENT_NAME, Costanti.PARAMETRO_ELIMINA_ELEMENTO_DALLA_CACHE));
		parameters.add(new Parameter(Costanti.PARAMETRO_IS_POSTBACK, "true"));
		parameters.add(new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS_POSTBACK));
		
		this.addAzioneBarraTitoloDettaglioElemento(this.getComandiAzioneBarraTitoloDettaglioElemento(), 
				DataElementType.IMAGE, Costanti.ICONA_RESET_CACHE_ELEMENTO_TOOLTIP, Costanti.ICONA_RESET_CACHE_ELEMENTO, servletName,parameters);
	}
	
	public void addComandoVerificaCertificatiElementoButton(String servletName, List<Parameter> parameters) {
		if(parameters == null) {
			parameters = new ArrayList<>();
		}
		// aggiungo parametri postback (come si fa in postback.js)
		parameters.add(new Parameter(Costanti.PARAMETRO_IS_POSTBACK, "true"));
		parameters.add(new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS_POSTBACK));
		
		this.addAzioneBarraTitoloDettaglioElemento(this.getComandiAzioneBarraTitoloDettaglioElemento(), 
				DataElementType.IMAGE, Costanti.ICONA_VERIFICA_CERTIFICATI_TOOLTIP, Costanti.ICONA_VERIFICA_CERTIFICATI, servletName,parameters);
	}
	
	public void addComandoVerificaConnettivitaElementoButton(String servletName, List<Parameter> parameters) {
		if(parameters == null) {
			parameters = new ArrayList<>();
		}
		// aggiungo parametri postback (come si fa in postback.js)
		parameters.add(new Parameter(Costanti.PARAMETRO_IS_POSTBACK, "true"));
		parameters.add(new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS_POSTBACK));
		
		this.addAzioneBarraTitoloDettaglioElemento(this.getComandiAzioneBarraTitoloDettaglioElemento(), 
				DataElementType.IMAGE, Costanti.ICONA_VERIFICA_CONNETTIVITA_TOOLTIP, Costanti.ICONA_VERIFICA, servletName,parameters);
	}
	
	public void addComandoVisualizzaRuntimeElementoButton(String servletName, List<Parameter> parameters) {
		if(parameters == null) {
			parameters = new ArrayList<>();
		}
		// aggiungo parametri postback (come si fa in postback.js)
		parameters.add(new Parameter(Costanti.PARAMETRO_IS_POSTBACK, "true"));
		parameters.add(new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS_POSTBACK));
		
		this.addAzioneBarraTitoloDettaglioElemento(this.getComandiAzioneBarraTitoloDettaglioElemento(), 
				DataElementType.IMAGE, Costanti.ICONA_VISUALIZZA_RUNTIME_ALLARME_TOOLTIP, Costanti.ICONA_VISUALIZZA_RUNTIME_ALLARME, servletName,parameters);
	}
	
	public void addComandoInUsoElementoButton(String servletName, 
			String titolo, String id, String inUsoType,
			String tooltip, String icon, String headerRiga1, 
			Boolean resizable, Boolean draggable) {			
		ServletUtils.addInUsoButton(servletName, this.getComandiAzioneBarraTitoloDettaglioElemento(), DataElementType.IMAGE, titolo, id, inUsoType,
				tooltip, icon, headerRiga1, 
				resizable, draggable);
	}

	public void addComandoAggiornaRicercaButton(String servletName, List<Parameter> parameters) {
		if(parameters == null) {
			parameters = new ArrayList<>();
		}
		// aggiungo parametri postback (come si fa in postback.js)
		parameters.add(new Parameter(Costanti.POSTBACK_ELEMENT_NAME, Costanti.PARAMETRO_AGGIORNA_RICERCA));
		parameters.add(new Parameter(Costanti.PARAMETRO_IS_POSTBACK, "true"));
		parameters.add(new Parameter(Costanti.DATA_ELEMENT_EDIT_MODE_NAME, Costanti.DATA_ELEMENT_EDIT_MODE_VALUE_EDIT_IN_PROGRESS_POSTBACK));
		
		this.addAzioneBarraTitoloDettaglioElemento(this.getComandiAzioneBarraTitoloDettaglioElemento(), DataElementType.IMAGE, Costanti.ICONA_AGGIORNA_RICERCA_TOOLTIP, Costanti.ICONA_AGGIORNA_RICERCA, servletName,parameters);
	}
	
	private void addAzioneBarraTitoloDettaglioElemento(List<DataElement> e, DataElementType deType, String tooltip, String icon, String servletName, List<Parameter> parameters) {
		DataElement de = new DataElement();
		de.setType(deType);
		de.setToolTip(tooltip);
		if(parameters != null && parameters.size() >0) {
			de.setUrl(servletName, parameters.toArray(new Parameter[parameters.size()]));
		} else {
			de.setUrl(servletName);
		}
		de.setIcon(icon);
		
		e.add(de);
	}

	public boolean isInserisciSearch() {
		return this.inserisciSearch;
	}
	
	public void nascondiTextFilterAutomatico() {
		this.setInserisciSearch(false);
	}

	public void setInserisciSearch(boolean inserisciSearch) {
		this.inserisciSearch = inserisciSearch;
	}
}
