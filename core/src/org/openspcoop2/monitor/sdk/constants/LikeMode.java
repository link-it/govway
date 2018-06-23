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
package org.openspcoop2.monitor.sdk.constants;


/**
 * LikeMode
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum LikeMode {

	ANYWHERE(org.openspcoop2.generic_project.expression.LikeMode.ANYWHERE),
	EXACT(org.openspcoop2.generic_project.expression.LikeMode.EXACT),
	END(org.openspcoop2.generic_project.expression.LikeMode.END),
	START(org.openspcoop2.generic_project.expression.LikeMode.START);

	private org.openspcoop2.generic_project.expression.LikeMode likeGenericProjectValue;
	
	public org.openspcoop2.generic_project.expression.LikeMode getLikeGenericProjectValue() {
		return this.likeGenericProjectValue;
	}

	LikeMode(org.openspcoop2.generic_project.expression.LikeMode likeGenericProjectValue){
		this.likeGenericProjectValue = likeGenericProjectValue;
	}
	
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

