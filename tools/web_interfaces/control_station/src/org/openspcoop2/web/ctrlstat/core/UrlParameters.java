/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

package org.openspcoop2.web.ctrlstat.core;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.web.lib.mvc.Parameter;

/**
 * UrlParameters
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UrlParameters {

	private String url;
	private List<Parameter> parameter = new ArrayList<Parameter>();
	
	public String getUrl() {
		return this.url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public int sizeParameter() {
		return this.parameter.size();
	}
	public boolean addParameter(Parameter e) {
		return this.parameter.add(e);
	}
	public Parameter getParameter(int index) {
		return this.parameter.get(index);
	}
	public Parameter removeParameter(int index) {
		return this.parameter.remove(index);
	}
	public List<Parameter> getParameter() {
		return this.parameter;
	}
	
}
