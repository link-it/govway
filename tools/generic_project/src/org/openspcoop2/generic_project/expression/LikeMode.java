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
package org.openspcoop2.generic_project.expression;

/**
 * LikeMode
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum LikeMode{

	ANYWHERE,
	EXACT,
	END,
	START;

	public static String[] toEnumNameArray(){
		String[] res = new String[LikeMode.values().length];
		int i=0;
		for (LikeMode tmp : LikeMode.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}



	
	@Override
	public String toString(){
		return this.name();
	}
	public boolean equals(LikeMode esito){
		return this.toString().equals(esito.toString());
	}

}
