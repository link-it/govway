/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.protocol.sdk.archive;

/**
 *  ArchiveModeType
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ArchiveModeType {

	public ArchiveModeType(String type){
		this.type = type;
	}
	
	private String type;

	
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
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
		if( (o instanceof ArchiveModeType) ==false ){
			return false;
		}
		return this.toString().equals(((ArchiveModeType)o).toString());
	}
	
	@Override
	public String toString(){
		return this.type;
	}
	
}
