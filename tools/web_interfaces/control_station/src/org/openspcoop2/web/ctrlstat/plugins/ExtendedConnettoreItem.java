package org.openspcoop2.web.ctrlstat.plugins;

import org.openspcoop2.utils.regexp.RegularExpressionEngine;

public class ExtendedConnettoreItem {
	
	private String id;
	private String label;
	private String value;
	private boolean required;
	private String regularExpression;
	
	private static final String EXTENDED_PREFIX = "ExtCntItem";
	private static final int MAX_LENGTH = 95;
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) throws ExtendedException {
		if(id.length()>MAX_LENGTH){
			throw new ExtendedException("ExtendedInfoConnettore [id:"+id+"] troppo lungo (max-length:"+MAX_LENGTH+")");
		}
		try{
			if(!RegularExpressionEngine.isMatch(id,"^[0-9A-Za-z]+$")){
				throw new ExtendedException("ExtendedInfoConnettore [id:"+id+"] con caratteri non permessi. L'identificativo dev'essere formato solo da caratteri e cifre");
			}
		}catch(Exception e){
			throw new ExtendedException(e.getMessage(),e);
		}
		
		this.id = EXTENDED_PREFIX+id;
	}
	public String getLabel() {
		return this.label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isRequired() {
		return this.required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public String getRegularExpression() {
		return this.regularExpression;
	}
	public void setRegularExpression(String regularExpression) {
		this.regularExpression = regularExpression;
	}


}
