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
package org.openspcoop2.utils.serialization;

import java.text.DateFormat;
import java.util.List;

/**
 * @author Bussu Giovanni (bussu@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class SerializationConfig {

	private Filter filter;
	private IDBuilder idBuilder;
	private List<String> excludes;
	private List<String> includes;
	private DateFormat df;
	private Boolean ignoreNullValues;
	private boolean serializeEnumAsString = true; //default
	private boolean prettyPrint = false;
	
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
	public boolean isSerializeEnumAsString() {
		return this.serializeEnumAsString;
	}
	public void setSerializeEnumAsString(boolean serializeEnumAsString) {
		this.serializeEnumAsString = serializeEnumAsString;
	}
	public boolean isPrettyPrint() {
		return this.prettyPrint;
	}
	public void setPrettyPrint(boolean prettyPrint) {
		this.prettyPrint = prettyPrint;
	}
}
