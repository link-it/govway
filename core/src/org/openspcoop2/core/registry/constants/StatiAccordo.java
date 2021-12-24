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


package org.openspcoop2.core.registry.constants;
/**
 *
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum StatiAccordo {

	bozza,operativo,finale;
		
	
	public static String[] toArray(){
		String[] t = new String[3];
		t[0] = bozza.toString();
		t[1] = operativo.toString();
		t[2] = finale.toString();
		return t;
	}
	
	public static String[] toLabel(){
		String[] t = new String[3];
		t[0] = upper(bozza.toString());
		t[1] = upper(operativo.toString());
		t[2] = upper(finale.toString());
		return t;
	}
	
	public static String upper(String s) {
		return (s.charAt(0)+"").toUpperCase()+s.substring(1);
	}
}


