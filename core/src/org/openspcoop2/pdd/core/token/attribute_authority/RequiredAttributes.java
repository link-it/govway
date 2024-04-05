/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

package org.openspcoop2.pdd.core.token.attribute_authority;

import java.util.List;

/**     
 * RequiredAttributes
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequiredAttributes {

	private List<String> attributes;
	
	public RequiredAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
	
	public List<String> getList() {
		return this.attributes;
	}
	
	public String getJsonList() {
		return jsonList();
	}
	public String jsonList() {
		return this.formatList("\"", "\"", ",", "");
	}
	// WRAPPER per invocare con jsonList()
	public String getJsonList(String unused) {
		return jsonList();
	}
	public String jsonList(String unused) {
		return jsonList();
	}
	
	public String getFormatList(String separator) {
		return this.formatList(separator);
	}
	public String formatList(String separator) {
		return this.formatList("", "", separator, "");
	}

	public String getFormatList(String prefix, String suffix) {
		return this.formatList(prefix, suffix);
	}
	public String formatList(String prefix, String suffix) {
		return this.formatList(prefix, suffix, ",", "");
	}
	
	public String getFormatList(String prefix, String suffix, String separator) {
		return this.formatList(prefix, suffix, separator);
	}
	public String formatList(String prefix, String suffix, String separator) {
		return this.formatList(prefix, suffix, separator, "");
	}
	
	public String getFormatList(String prefix, String suffix, String separator, String emptyResultReturn) {
		return this.formatList(prefix, suffix, separator, emptyResultReturn);
	}
	public String formatList(String prefix, String suffix, String separator, String emptyResultReturn) {
		if(this.attributes==null || this.attributes.isEmpty()) {
			return emptyResultReturn;
		}
		StringBuilder sb = new StringBuilder();
		for (String attr : this.attributes) {
			if(sb.length()>0) {
				sb.append(separator);
			}
			sb.append(prefix);
			sb.append(attr);
			sb.append(suffix);
		}
		return sb.toString();
	}
	
	public String getAtPosition(int index) {
		return atPosition(index);
	}
	public String atPosition(int index) {
		return this.attributes.get(index);
	}
	// wrapper
	public String getAtPosition(String index) {
		return atPosition(Integer.valueOf(index));
	}
	public String atPosition(String index) {
		return atPosition(Integer.valueOf(index));
	}
}
