/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils.rest.problem;

import java.util.HashMap;
import java.util.Map;

/**
 * ProblemRFC7807
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProblemRFC7807 {

	private String type;
	private String title;
	private Integer status;
	private String detail;
	private String instance;
	
	private Map<String, Object> custom = new HashMap<>();

	private String raw;
	
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getStatus() {
		return this.status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getDetail() {
		return this.detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getInstance() {
		return this.instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}

	public Map<String, Object> getCustom() {
		return this.custom;
	}
	public void setCustom(Map<String, Object> custom) {
		this.custom = custom;
	}
	
	public String getRaw() {
		return this.raw;
	}
	public void setRaw(String raw) {
		this.raw = raw;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		JsonSerializer serializer = new JsonSerializer(true);
		try {
			sb.append(serializer.toString(this));
		}catch(Exception e) {
			sb.append("Serialization error: "+e.getMessage());
		}
		return sb.toString();
	}
}
