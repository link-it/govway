/**
 * 
 */
package org.openspcoop2.utils.serialization;

import java.text.DateFormat;
import java.util.List;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author: bussu $
 * @version $ Rev: 12563 $, $Date: 11 mag 2018 $
 * 
 */
public class SerializationConfig {

	private Filter filter;
	private IDBuilder idBuilder;
	private List<String> excludes;
	private List<String> includes;
	private DateFormat df;
	private Boolean ignoreNullValues;
	
	public Filter getFilter() {
		return this.filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	public IDBuilder getIdBuilder() {
		return this.idBuilder;
	}
	public void setIdBuilder(IDBuilder idBuilder) {
		this.idBuilder = idBuilder;
	}
	public List<String> getExcludes() {
		return this.excludes;
	}
	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}
	public DateFormat getDf() {
		return this.df;
	}
	public void setDf(DateFormat df) {
		this.df = df;
	}
	public List<String> getIncludes() {
		return this.includes;
	}
	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}
	public Boolean getIgnoreNullValues() {
		return this.ignoreNullValues;
	}
	public void setIgnoreNullValues(Boolean ignoreNullValues) {
		this.ignoreNullValues = ignoreNullValues;
	}
}
