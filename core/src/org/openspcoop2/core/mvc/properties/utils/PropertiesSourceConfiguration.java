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
package org.openspcoop2.core.mvc.properties.utils;

import java.util.List;

public class PropertiesSourceConfiguration {

	private String id;
	private String directory;
	private boolean update;
	private List<byte[]> builtIn;
	private boolean updateBuiltIn;
	
	
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDirectory() {
		return this.directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public boolean isUpdate() {
		return this.update;
	}
	public void setUpdate(boolean update) {
		this.update = update;
	}
	public List<byte[]> getBuiltIn() {
		return this.builtIn;
	}
	public void setBuiltIn(List<byte[]> builtIn) {
		this.builtIn = builtIn;
	}
	public boolean isUpdateBuiltIn() {
		return this.updateBuiltIn;
	}
	public void setUpdateBuiltIn(boolean updateBuiltIn) {
		this.updateBuiltIn = updateBuiltIn;
	}
}
