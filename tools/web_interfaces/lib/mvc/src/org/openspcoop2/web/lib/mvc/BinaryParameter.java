package org.openspcoop2.web.lib.mvc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class BinaryParameter {

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

}
