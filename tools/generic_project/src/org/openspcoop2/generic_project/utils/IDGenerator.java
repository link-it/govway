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
package org.openspcoop2.generic_project.utils;

import org.openspcoop2.generic_project.beans.ComplexField;
import org.openspcoop2.generic_project.beans.IField;

/**
 * IDGenerator
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IDGenerator {

	private static long serialNumber = 0;
    public static synchronized String getUniqueID(){
            if((IDGenerator.serialNumber+1) > Long.MAX_VALUE){
            	IDGenerator.serialNumber = 0;
            } 
            IDGenerator.serialNumber++;
            return "T"+System.currentTimeMillis()+"_" +"S" +IDGenerator.serialNumber;
    }
    
    private static long sequence = 0;
    public static synchronized long getUniqueSequenceValue(){
        if((IDGenerator.sequence+1) > Long.MAX_VALUE){
        	IDGenerator.sequence = 0;
        } 
        IDGenerator.sequence++;
        return IDGenerator.sequence;
}
    
    public static String getUniqueID(IField field){
    	if(field instanceof ComplexField){
    		ComplexField cf = (ComplexField) field;
    		if(cf.getFather()!=null){
    			return IDGenerator.normalize(cf.getFather().getFieldName()+"_"+field.getFieldName()+"_"+IDGenerator.getUniqueID());
    		}
    	}
    	return IDGenerator.normalize(field.getClassName()+"_"+field.getFieldName()+"_"+IDGenerator.getUniqueID());
    }

    private static String normalize(String value){
    	return value.replaceAll("-", "_");
    }
}
