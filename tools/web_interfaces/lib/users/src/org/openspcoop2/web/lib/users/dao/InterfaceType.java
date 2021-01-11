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


package org.openspcoop2.web.lib.users.dao;


/**
 * InterfaceType
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public enum InterfaceType {
	COMPLETA,AVANZATA,STANDARD;
	
	public static InterfaceType convert(String type, boolean throwNotFound) throws Exception {
		if(InterfaceType.AVANZATA.toString().equals(type)){
			return InterfaceType.AVANZATA;
		}
		else if(InterfaceType.STANDARD.toString().equals(type)){
			return InterfaceType.STANDARD;
		}
		else if(InterfaceType.COMPLETA.toString().equals(type)){
			return InterfaceType.COMPLETA;
		}
		if(throwNotFound) {
			throw new Exception("Tipo ["+type+"] sconosciuto");
		}
		return null;
	}
	
	public static boolean equals(InterfaceType src, InterfaceType ... check) {
		if(src==null) {
			return false;
		}
		return src.equalsOr(check);
	}
		
	public boolean equalsOr(InterfaceType ... check) {
		if(check==null || check.length<0) {
			return false;
		}
		for (int i = 0; i < check.length; i++) {
			if(this.equals(check[i])) {
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(InterfaceType object){
		if(object==null)
			return false;
		if(object.name()==null)
			return false;
		return object.name().equals(this.name());	
	}
	public boolean equals(String object){
		if(object==null)
			return false;
		return object.equals(this.name());	
	}
}


