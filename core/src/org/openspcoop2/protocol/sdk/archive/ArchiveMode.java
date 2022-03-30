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

package org.openspcoop2.protocol.sdk.archive;

/**
 *  ArchiveMode
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveMode {

	public ArchiveMode(String name){
		this.name = name;
	}
	
	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean equals(String o){
		if(o==null){
			return false;
		}
		return this.toString().equals(o);
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null){
			return false;
		}
		if( (o instanceof ArchiveMode)==false ){
			return false;
		}
		return this.toString().equals(((ArchiveMode)o).toString());
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
	@Override
	public Object clone(){
		ArchiveMode archiveMode = new ArchiveMode(this.name);
		return archiveMode;
	}
}
