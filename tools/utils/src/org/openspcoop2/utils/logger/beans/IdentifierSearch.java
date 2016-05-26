package org.openspcoop2.utils.logger.beans;

import java.io.Serializable;

import org.openspcoop2.utils.logger.constants.SearchType;

public class IdentifierSearch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private SearchType type = SearchType.EQUALS;
	private boolean caseInsensitive;
	private String value;
	
	public SearchType getType() {
		return this.type;
	}
	public void setType(SearchType type) {
		this.type = type;
	}
	public boolean isCaseInsensitive() {
		return this.caseInsensitive;
	}
	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
