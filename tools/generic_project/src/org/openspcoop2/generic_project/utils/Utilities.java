/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Utilities {

	public static String normalizedProjectName(String projectName){
		StringBuilder bf = new StringBuilder();
		boolean precedenteMaiuscolo = false;
		for (int i = 0; i < projectName.length(); i++) {
			char c = projectName.charAt(i);
			
			if(Utilities.isUpperCase(c)){
				if(!precedenteMaiuscolo && i>0){
					bf.append("_");
				}
				precedenteMaiuscolo = true;
			}else{
				precedenteMaiuscolo = false;
			}
			
			if( (bf.toString().length()>0) && (c=='_') && (bf.toString().charAt(bf.toString().length()-1)=='_') ){
				// nop, _ gia aggiunto
			}else{
				bf.append(Utilities.toLowerCase(c));
			}
		}
		return bf.toString().replaceAll("-", "");
	}
	private static boolean isUpperCase(char c){
		String sC = c + "";
		return sC.toUpperCase().equals(sC);
	}
	private static char toLowerCase(char c){
		String sC = c + "";
		return sC.toLowerCase().charAt(0);
	}
	
	
	public static boolean equals(Object o1,Object o2){
		if(o1==null){
			if(o2!=null){
				return false;
			}
			else{
				return true;
			}
		}
		else{
			if(o2==null){
				return false;
			}
			else{
				return o1.equals(o2);
			}
		}
	}
	
    public static List<?> newList(Object ... object){
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < object.length; i++) {
                list.add(object[i]);
        }
        return list;
    }
}
