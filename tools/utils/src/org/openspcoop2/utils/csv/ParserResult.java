/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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

package org.openspcoop2.utils.csv;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ParserResult
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParserResult {

	private Map<String, Integer> headerMap = null;
	private List<Record> records = new ArrayList<Record>();

	public List<Record> getRecords() {
		return this.records;
	}
	public void setRecords(List<Record> records) {
		this.records = records;
	}
	
	public boolean existsHeader(){
		return this.headerMap!=null && this.headerMap.size()>0;
	}
	public Map<String, Integer> getHeaderMap() {
		return this.headerMap;
	}
	public void setHeaderMap(Map<String, Integer> headerMap) {
		this.headerMap = headerMap;
	}
	
	
}
