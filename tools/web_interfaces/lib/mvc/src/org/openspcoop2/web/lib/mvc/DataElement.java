/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;



/**
 * DataElement
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class DataElement {
	
	private static Map<String, String> escapeMap = null;
	
	private static int DATA_ELEMENT_SIZE = 50;
	private static int DATA_ELEMENT_COLS = 15;
	private static int DATA_ELEMENT_ROWS = 5;
	public static void initialize(DataElementParameter p){
		if(p.getSize()!= null && p.getSize().intValue() > 0) {
			DataElement.DATA_ELEMENT_SIZE = p.getSize();
		}
		
		if(p.getCols()!= null && p.getCols().intValue() > 0) {
			DataElement.DATA_ELEMENT_COLS = p.getCols();
		}
		
		if(p.getRows()!= null && p.getRows().intValue() > 0) {
			DataElement.DATA_ELEMENT_ROWS = p.getRows();
		}
	}
	
	
	static{
		DataElement.escapeMap = new HashMap<String,String>();
		
		// carico le stringhe da sostituire
		escapeMap.put("&lt;BR&gt;", "<BR>");
		escapeMap.put("&lt;BR/&gt;", "<BR/>");
		escapeMap.put("&lt;/BR&gt;", "</BR>");
		escapeMap.put("&lt;br&gt;", "<br>");
		escapeMap.put("&lt;br/&gt;", "<br/>");
		escapeMap.put("&lt;/br&gt;", "</br>");
	}

	String label, value, type, subType, url, target, name, onClick, onChange, selected,toolTip;
	String [] values = null;
	String [] labels = null;
	int size, cols, rows, id;
	boolean affiancato; // serve a gestire il successivo elemento se disegnarlo accanto o in verticale (default)
	boolean labelAffiancata=true; // indica se la label e poi l'elemento sono disegnati uno accanto all'altro in orizzontale (default) oppure in verticale (default per le text-area)
	String idToRemove;
	boolean required=false;
	boolean bold=false;
	boolean postBack=false;
	
	String note = null;
	String styleClass = null;

	public String getIdToRemove() {
		return this.idToRemove;
	}

	public void setIdToRemove(String idToRemove) {
		this.idToRemove = idToRemove;
	}

	public DataElement() {
		//int id = -1;
		this.label = "";
		this.value = "";
		this.type = "text";
		this.subType = "";
		this.url = "";
		this.target = "";
		this.name = "";
		this.onClick = "";
		this.onChange = "";
		this.selected = "";
		this.toolTip="";
		this.size = DataElement.DATA_ELEMENT_SIZE;
		this.cols = DataElement.DATA_ELEMENT_COLS;
		this.rows = DataElement.DATA_ELEMENT_ROWS;
		this.affiancato = false;
		this.labelAffiancata = true;
		this.note = "";
		this.styleClass = Costanti.INPUT_LONG_CSS_CLASS;
		
	}

	public void setId(int i) {
		this.id = i;
	}
	public int getId() {
		return this.id;
	}

	public void setLabel(String s) {
		this.label = s;
	}
	public String getLabel() {
		return this.getLabel(true);
	}
	public String getLabel(boolean elementsRequiredEnabled) {
		StringBuffer bf = new StringBuffer();
		if(this.bold){
			bf.append("<B>");
		}
		
		bf.append(getEscapedValue(checkNull(this.label)));
		if(elementsRequiredEnabled && this.required){
			//	bf.append(" (*)");
			bf.append(" <em>*</em>");
		}
		if(this.bold){
			bf.append("</B>");
		}
		return bf.toString();
	}

	public void setValue(String s) {
		this.value = s;
	}
	public String getValue() {
		return checkNull(this.value);
	}

	public void setType(DataElementType s) {
		this.setType(s.toString());
	}
	public void setType(String s) {
		this.type = s;
		/*if("hidden".equals(this.type)){
			this.required = false;
			this.bold = false;
		}
		if("text".equals(this.type)){
			this.required = false;
		}*/
		if(DataElementType.TEXT_AREA.toString().equals(s) || DataElementType.TEXT_AREA_NO_EDIT.toString().equals(s)){
			this.setLabelAffiancata(false);
		}
	}
	public String getType() {
		return checkNull(this.type);
	}
	
	public boolean isRequired() {
		return this.required;
	}

	public void setRequired(boolean required) {
		this.required = required;
		/*if(!"hidden".equals(this.type)){
			this.bold = required;
			if(!"text".equals(this.type)){
				this.required = required;
			}
		}*/
	}

	public void setSubType(String s) {
		this.subType = s;
	}
	public String getSubType() {
		return checkNull(this.subType);
	}

	public void setUrl(String s) {
		this.url = s;
	}
	public void setUrl(String servletName,Parameter ... parameter) {
		this.url = servletName;
		if(parameter!=null && parameter.length>0){
			this.url = this.url + "?";
			for (int i = 0; i < parameter.length; i++) {
				if(i>0){
					this.url = this.url + "&";
				}
				this.url = this.url + parameter[i].toString();
			}
		}
	}
	public String getUrl() {
		return checkNull(this.url);
	}

	public void setTarget(String s) {
		this.target = s;
	}
	public String getTarget() {
		return checkNull(this.target);
	}

	public void setName(String s) {
		this.name = s;
	}
	public String getName() {
		return checkNull(this.name);
	}

	public void setOnClick(String s) {
		this.onClick = s;
	}
	public String getOnClick() {
		return checkNull(this.onClick);
	}

	@Deprecated
	public void setOnChange(String s) {
		this.onChange = s;
	}
	public void setOnChangeAlternativePostBack(String s) {
		this.onChange = s;
	}
	public String getOnChange() {
		return checkNull(this.onChange);
	}

	public void setSelected(String s) {
		this.selected = s;
	}
	public void setSelectedAsNull() {
		this.selected = null;
	}
	public void setSelected(boolean isFlag) {
		if(isFlag)
			this.selected = Costanti.CHECK_BOX_ENABLED;
		else
			this.selected = Costanti.CHECK_BOX_DISABLED;
	}
	public void setSelected(CheckboxStatusType status) {
		switch (status) {
		case ABILITATO:
			this.selected = Costanti.CHECK_BOX_ENABLED;
			break;
		case DISABILITATO:
			this.selected = Costanti.CHECK_BOX_DISABLED;
			break;
		case WARNING_ONLY:
			this.selected = Costanti.CHECK_BOX_WARN;
			break;
		}
	}
	public String getSelected() {
		return checkNull(this.selected);
	}

	public void setSize(int i) {
		this.size = i;
	}
	public int getSize() {
		return this.size;
	}

	public void setCols(int i) {
		this.cols = i;
	}
	public int getCols() {
		return this.cols;
	}

	public void setRows(int i) {
		this.rows = i;
	}
	public int getRows() {
		return this.rows;
	}

	public void setAffiancato(boolean b) {
		this.affiancato = b;
	}
	public boolean getAffiancato() {
		return this.affiancato;
	}

	public void setValues(String [] s) {
		this.values = s;
	}
	public void setValues(List<String> s) {
		if(s==null || s.size()<=0){
			return;
		}
		this.setValues(s.toArray(new String[1]));
	}
	public String[] getValues() {
		return this.values;
	}

	public void setLabels(String [] s) {
		if( s != null && s.length > 0){
			this.labels = new String[ s.length];
			for (int i = 0; i < s.length; i++) {
				this.labels[i] = getEscapedValue( s[i]);
			}
		}else {
			this.labels = s;
		}
	}
	public void setLabels(List<String> s) {
		if(s==null || s.size()<=0){
			return;
		}
		this.setLabels(s.toArray(new String[1]));
	}
	public String[] getLabels() {
		return this.labels;
	}
	
	private static String checkNull(String toCheck)
	{
		return (toCheck==null ? "" : toCheck);
	}

	public String getToolTip() {
		return this.toolTip;
	}
	/**
	 * Il tooltip da visualizzare quando si passa con il mouse
	 * su di un link, se il tooltip non e' impostato e il valore del campo value
	 * e' > size allora il link viene troncato e viene 
	 * impostato come tooltip il valore originale di value
	 * @param toolTip
	 */
	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}
	
	public boolean isBold() {
		return this.bold;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}
	
	public boolean isLabelAffiancata() {
		return this.labelAffiancata;
	}

	public void setLabelAffiancata(boolean labelAffiancata) {
		this.labelAffiancata = labelAffiancata;
	}
	
	public boolean isPostBack() {
		return this.postBack;
	}
	public void setPostBack(boolean postBack) {
		this.setPostBack(postBack, true);
	}
	public void setPostBack(boolean postBack,boolean setElementName) {
		this.postBack = postBack;
		if (this.postBack) {
			if(setElementName){
				if(this.name==null || "".equals(this.name)){
					throw new RuntimeException("Per poter impostare il nome dell'element che scaturira' il postBack deve prima essere indicato tramite il metodo setName");
				}
				this.setOnClick(Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_START+this.name+Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_END);
				this.setOnChange(Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_START+this.name+Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_END);
			}
			else{
				this.setOnClick(Costanti.POSTBACK_FUNCTION);
				this.setOnChange(Costanti.POSTBACK_FUNCTION);
			}
		}
		else{
			this.setOnClick(null);
			this.setOnChange(null);
		}
	}
	
	public static String getEscapedValue(String value){
		String escaped = StringEscapeUtils.escapeHtml(StringEscapeUtils.unescapeHtml(checkNull(value)));
		
		// ripristino evenutali caratteri html
		for (String key : DataElement.escapeMap.keySet()) {
			if(escaped.contains(key)){
				escaped = escaped.replaceAll(key, DataElement.escapeMap.get(key));
			}
		}
		
		return escaped;
	}

	public String getNote() {
		return checkNull(this.note);
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getStyleClass() {
		return checkNull(this.styleClass);
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	
	
}
