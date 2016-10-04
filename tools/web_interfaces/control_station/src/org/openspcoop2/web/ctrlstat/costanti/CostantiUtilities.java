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

package org.openspcoop2.web.ctrlstat.costanti;


/**
 * CostantiUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CostantiUtilities {

    public static String get_LOCAL_PATH(TipoProperties tipo, String prefix){
    	String p = prefix;
    	String v = null;
    	if(p!=null){
    		p = p.trim();
    	}
    	switch (tipo) {
		case CONSOLE:
			v = CostantiControlStation._OPENSPCOOP2_CONSOLE_LOCAL_PATH;
			break;
		case DATASOURCE:
			v = CostantiControlStation._OPENSPCOOP2_DATASOURCE_LOCAL_PATH;
			break;
		case REGISTRO:
			v = CostantiControlStation._OPENSPCOOP2_REGISTRO_SERVIZI_REMOTO_LOCAL_PATH;
			break;
		case LOGGER:
			v = CostantiControlStation._OPENSPCOOP2_LOGGER_LOCAL_PATH;
			break;
		}
    	
    	if(p==null || "".equals(p)){
    		v = v.replace(CostantiControlStation.OPENSPCOOP2_PREFIX_LOCAL_PATH, CostantiControlStation.DEFAULT_OPENSPCOOP2_PREFIX_LOCAL_PATH);
    	}
    	else{
    		v = v.replace(CostantiControlStation.OPENSPCOOP2_PREFIX_LOCAL_PATH, prefix);
    	}
    	//System.out.println("LOCALPATH["+tipo.name()+"]: "+v);
    	return v;
    }
    
    public static String get_PROPERTY_NAME(TipoProperties tipo, String prefix){
    	String p = prefix;
    	String v = null;
    	if(p!=null){
    		p = p.trim();
    	}
    	switch (tipo) {
		case CONSOLE:
			v = CostantiControlStation._OPENSPCOOP2_CONSOLE_PROPERTIES;
			break;
		case DATASOURCE:
			v = CostantiControlStation._OPENSPCOOP2_DATASOURCE_PROPERTIES;
			break;
		case REGISTRO:
			v = CostantiControlStation._OPENSPCOOP2_REGISTRO_SERVIZI_REMOTO_PROPERTIES;
			break;
		case LOGGER:
			v = CostantiControlStation._OPENSPCOOP2_LOGGER_PROPERTIES;
			break;
		}
    	
    	if(p==null || "".equals(p)){
    		v = v.replace(CostantiControlStation.OPENSPCOOP2_PROPERTIES_NAME, CostantiControlStation.DEFAULT_OPENSPCOOP2_PROPERTIES_NAME);
    	}
    	else{
    		v = v.replace(CostantiControlStation.OPENSPCOOP2_PROPERTIES_NAME, prefix);
    	}
    	//System.out.println("PROPERTY["+tipo.name()+"]: "+v);
    	return v;
    }
	
}
