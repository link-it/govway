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

package org.openspcoop2.web.lib.mvc;

/**
 * TipoOperazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipoOperazione {

	ADD("add"),CHANGE("change"),DEL("del"),LIST("list"),LOGIN("login"),LOGOUT("logout"),OTHER("");
	
	private String value;
	TipoOperazione(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	
	public boolean equals(TipoOperazione t){
		if(t==null){
			return false;
		}
		return t.toString().equals(this.toString());
	}

}
