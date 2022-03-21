/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.core.connettori;

import java.io.File;

/**
 * ConnettoreFile_outputConfig
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreFile_outputConfig {

	private File outputFile = null;
	private Boolean writable = null;
	private Boolean writable_ownerOnly = null;
	private Boolean readable = null;
	private Boolean readable_ownerOnly = null;
	private Boolean executable = null;
	private Boolean executable_ownerOnly = null;
	
	public File getOutputFile() {
		return this.outputFile;
	}
	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}
	public Boolean getWritable() {
		return this.writable;
	}
	public void setWritable(Boolean writable) {
		this.writable = writable;
	}
	public Boolean getWritable_ownerOnly() {
		return this.writable_ownerOnly;
	}
	public void setWritable_ownerOnly(Boolean writable_ownerOnly) {
		this.writable_ownerOnly = writable_ownerOnly;
	}
	public Boolean getReadable() {
		return this.readable;
	}
	public void setReadable(Boolean readable) {
		this.readable = readable;
	}
	public Boolean getReadable_ownerOnly() {
		return this.readable_ownerOnly;
	}
	public void setReadable_ownerOnly(Boolean readable_ownerOnly) {
		this.readable_ownerOnly = readable_ownerOnly;
	}
	public Boolean getExecutable() {
		return this.executable;
	}
	public void setExecutable(Boolean executable) {
		this.executable = executable;
	}
	public Boolean getExecutable_ownerOnly() {
		return this.executable_ownerOnly;
	}
	public void setExecutable_ownerOnly(Boolean executable_ownerOnly) {
		this.executable_ownerOnly = executable_ownerOnly;
	}
	
	public boolean isPermission() {
		return this.getReadable()!=null || 
				this.getWritable()!=null ||
				this.getExecutable()!=null;
	}
	public String getPermissionAsString() {
		StringBuilder sb = new StringBuilder();
		if(this.readable!=null) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			if(this.readable_ownerOnly!=null && this.readable_ownerOnly) {
				sb.append("o");
			}
			if(this.readable) {
				sb.append("+");
			}
			else {
				sb.append("-");
			}
			sb.append("r");
		}
		
		if(this.writable!=null) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			if(this.writable_ownerOnly!=null && this.writable_ownerOnly) {
				sb.append("o");
			}
			if(this.writable) {
				sb.append("+");
			}
			else {
				sb.append("-");
			}
			sb.append("w");
		}
		
		if(this.executable!=null) {
			if(sb.length()>0) {
				sb.append(" ");
			}
			if(this.executable_ownerOnly!=null && this.executable_ownerOnly) {
				sb.append("o");
			}
			if(this.executable) {
				sb.append("+");
			}
			else {
				sb.append("-");
			}
			sb.append("x");
		}
		return sb.toString();
	}
	
}
