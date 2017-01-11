/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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


package org.openspcoop2.web.lib.audit.costanti;


/**
 * Tipi di operazioni
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public enum TipoOperazione {

	LOGIN,LOGOUT,ADD,CHANGE,DEL;
	
	
	public static TipoOperazione parseString(String s){
		if(ADD.toString().equals(s)){
			return ADD;
		}
		else if(CHANGE.toString().equals(s)){
			return CHANGE;
		}
		else if(DEL.toString().equals(s)){
			return DEL;
		}
		if(LOGIN.toString().equals(s)){
			return LOGIN;
		}
		if(LOGOUT.toString().equals(s)){
			return LOGOUT;
		}
		else{
			return null;
		}
	}
}
