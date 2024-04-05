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
package org.openspcoop2.utils.resources;

/**
 * FileRWXConfig
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FileRWXConfig {

	public FileRWXConfig() {}
	public FileRWXConfig(Boolean readable, Boolean readableOwnerOnly,
			   Boolean writable, Boolean writableOwnerOnly,
			   Boolean executable, Boolean executableOwnerOnly) {
		this.readable = readable;
		this.readableOwnerOnly = readableOwnerOnly;
		this.writable = writable;
		this.writableOwnerOnly = writableOwnerOnly;
		this.executable = executable;
		this.executableOwnerOnly = executableOwnerOnly;
	}
	
	private Boolean readable;
	private Boolean readableOwnerOnly;
	private Boolean writable;
	private Boolean writableOwnerOnly;
	private Boolean executable;
	private Boolean executableOwnerOnly;
	
	public Boolean getReadable() {
		return this.readable;
	}
	public void setReadable(Boolean readable) {
		this.readable = readable;
	}
	public Boolean getReadableOwnerOnly() {
		return this.readableOwnerOnly;
	}
	public void setReadableOwnerOnly(Boolean readableOwnerOnly) {
		this.readableOwnerOnly = readableOwnerOnly;
	}
	public Boolean getWritable() {
		return this.writable;
	}
	public void setWritable(Boolean writable) {
		this.writable = writable;
	}
	public Boolean getWritableOwnerOnly() {
		return this.writableOwnerOnly;
	}
	public void setWritableOwnerOnly(Boolean writableOwnerOnly) {
		this.writableOwnerOnly = writableOwnerOnly;
	}
	public Boolean getExecutable() {
		return this.executable;
	}
	public void setExecutable(Boolean executable) {
		this.executable = executable;
	}
	public Boolean getExecutableOwnerOnly() {
		return this.executableOwnerOnly;
	}
	public void setExecutableOwnerOnly(Boolean executableOwnerOnly) {
		this.executableOwnerOnly = executableOwnerOnly;
	}
	
}
