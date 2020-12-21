/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
 * AlarmType
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum AlarmType {

	ACTIVE,
	PASSIVE;

	public static String[] toEnumNameArray(){
		String[] res = new String[AlarmType.values().length];
		int i=0;
		for (AlarmType tmp : AlarmType.values()) {
			res[i]=tmp.name();
			i++;
		}
		return res;
	}



	
	@Override
	public String toString(){
		return this.name();
	}
	public boolean equals(AlarmType esito){
		return this.toString().equals(esito.toString());
	}

}

