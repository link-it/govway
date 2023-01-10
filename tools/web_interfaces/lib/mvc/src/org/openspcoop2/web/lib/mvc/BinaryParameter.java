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
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * BinaryParameter
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BinaryParameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private byte[] value;
	private String filename;
	private String id;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getValue() {
		return this.value;
	}
	public void setValue(byte[] value) {
		this.value = value;
	}
	public String getFilename() {
		return this.filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public List<DataElement> getFileNameDataElement(){
		List<DataElement> dati = new ArrayList<DataElement>();
		
		DataElement de1 = new DataElement();
		DataElement de2 = null;
		de1.setName(Costanti.PARAMETER_FILENAME_PREFIX + this.name);
		de1.setValue(this.filename);
		de1.setType(DataElementType.HIDDEN);
		
		if(StringUtils.isNotBlank(this.filename)){
			de2 = new DataElement();
			de2.setName("_" + Costanti.PARAMETER_FILENAME_PREFIX + this.name);
			de2.setValue("<I>" + this.filename + "</I>");
			de2.setLabel("");
			de2.setType(DataElementType.TEXT);
			
			DataElementImage newImage = new DataElementImage();
			
			newImage.setImage(Costanti.ICON_ELIMINA_FILE);
			newImage.setTarget(TargetType.SELF);
			newImage.setUrl("#");
			newImage.setToolTip(MessageFormat.format(Costanti.TOOLTIP_ELIMINA_FILE, this.filename));
			
			StringBuilder onClickFunction = new StringBuilder();
			onClickFunction.append(Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_START);
			onClickFunction.append(this.name  + Costanti.PARAMETER_FILENAME_REMOVE_PLACEHOLDER + 0);
			onClickFunction.append(Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_END);
			newImage.setOnClick(onClickFunction.toString());
			
			de2.setImage(newImage);
		}  
		
		dati.add(de1);
		
		if(de2 != null)
			dati.add(de2);
		
		return dati;
	}
	
	public DataElement getFileIdDataElement(){
		DataElement de = new DataElement();
		
		de.setType(DataElementType.HIDDEN);
		de.setName(Costanti.PARAMETER_FILEID_PREFIX + this.name);
		de.setValue(this.id); 
		
		return de;
	}
	
	public DataElement getFileDataElement(String label, String value, int size){
		DataElement de = new DataElement();
		
		de = new DataElement();
		de.setLabel(label);
		de.setValue(value);
		de.setType(DataElementType.FILE);
		de.setName(this.name);
		de.setSize(size);
		de.setPostBack(true);
		
		return de;
	}
	
	public static List<DataElement> getFileNameDataElement(List<BinaryParameter> listaParametri) {
		List<DataElement> dati = new ArrayList<DataElement>();
		
		BinaryParameter bp0 = listaParametri.get(0);
		
		List<String> fileNames = new ArrayList<String>();
		
		for (BinaryParameter bp : listaParametri) {
			if(StringUtils.isNotBlank(bp.getFilename()))
				fileNames.add(bp.getFilename());
		}
		
		DataElement de1 = new DataElement();
		DataElement de2 = null;
		de1.setName(Costanti.PARAMETER_FILENAME_PREFIX + bp0.getName());
		de1.setValue(StringUtils.join(fileNames, ","));
		de1.setType(DataElementType.HIDDEN);
		
		dati.add(de1);
		
		int i=0;
		for (String fileName : fileNames) {
			if(StringUtils.isNotBlank(fileName)){
				de2 = new DataElement();
				de2.setName("_" + Costanti.PARAMETER_FILENAME_PREFIX + bp0.getName());
				de2.setValue("<I>" + fileName + "</I>");
				de2.setLabel("");
				de2.setType(DataElementType.TEXT);
				
				DataElementImage newImage = new DataElementImage();
				
				newImage.setImage(Costanti.ICON_ELIMINA_FILE);
				newImage.setTarget(TargetType.SELF);
				newImage.setUrl("#");
				newImage.setToolTip(MessageFormat.format(Costanti.TOOLTIP_ELIMINA_FILE, fileName));
				
				StringBuilder onClickFunction = new StringBuilder();
				onClickFunction.append(Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_START);
				onClickFunction.append(bp0.getName() + Costanti.PARAMETER_FILENAME_REMOVE_PLACEHOLDER + i);
				onClickFunction.append(Costanti.POSTBACK_FUNCTION_WITH_PARAMETER_END);
				newImage.setOnClick(onClickFunction.toString());
				
				de2.setImage(newImage);
				
				dati.add(de2);
				i++;
			} 
		}
		
		return dati;
	}

	public static DataElement getFileIdDataElement(List<BinaryParameter> listaParametri){
		DataElement de = new DataElement();
		
		BinaryParameter bp0 = listaParametri.get(0);
		
		List<String> ids = new ArrayList<String>();
		
		for (BinaryParameter bp : listaParametri) {
			if(StringUtils.isNotBlank(bp.getId()))
				ids.add(bp.getId());
		}
		
		de.setType(DataElementType.HIDDEN);
		de.setName(Costanti.PARAMETER_FILEID_PREFIX + bp0.getName());
		de.setValue(StringUtils.join(ids, ",")); 
		
		return de;
	}
}
