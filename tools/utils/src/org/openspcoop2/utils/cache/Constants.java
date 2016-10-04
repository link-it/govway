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

package org.openspcoop2.utils.cache;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

/**
 * Constants
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Constants {

	/** Nomi attributi */
	public final static String CACHE_ATTRIBUTE_ABILITATA = "cache";
	
	/** Nomi metodi */
	public final static String CACHE_METHOD_NAME_RESET = "resetCache"; 
	public final static String CACHE_METHOD_NAME_PRINT_STATS = "printStatsCache"; 
	public final static String CACHE_METHOD_NAME_ABILITA = "enableCache";
	public final static String CACHE_METHOD_NAME_DISABILITA = "disableCache";
	public final static String CACHE_METHOD_NAME_LIST_KEYS = "listKeysCache";
	public final static String CACHE_METHOD_NAME_GET_OBJECT = "getObjectCache";
	public final static String CACHE_METHOD_NAME_REMOVE_OBJECT = "removeObjectCache";
	
	/** Messaggi */
	public final static String MSG_OPERAZIONE_NON_EFFETTUATA = "Operation failed: ";
	public final static String MSG_RESET_CACHE_EFFETTUATO_SUCCESSO = "Operation '"+CACHE_METHOD_NAME_RESET+"' done";
	public final static String MSG_ABILITAZIONE_CACHE_EFFETTUATA = "Operation '"+CACHE_METHOD_NAME_ABILITA+"' done";
	public final static String MSG_DISABILITAZIONE_CACHE_EFFETTUATA = "Operation '"+CACHE_METHOD_NAME_DISABILITA+"' done";

	/** Per determinare se l'attributo e' leggibile/scrivibile */
	public final static boolean JMX_ATTRIBUTE_READABLE = true;
	public final static boolean JMX_ATTRIBUTE_WRITABLE = true;
	/** Per determinare se l'attributo e' ricavabile nella forma booleana isAttribute() */
	public final static boolean JMX_ATTRIBUTE_IS_GETTER = true;
	
	/** MBean Attribute */
	public final static MBeanAttributeInfo MBEAN_ATTRIBUTE_INFO_CACHE_ABILITATA = 
			new MBeanAttributeInfo(CACHE_ATTRIBUTE_ABILITATA,boolean.class.getName(),
					"Cache enabled",
					JMX_ATTRIBUTE_READABLE,!JMX_ATTRIBUTE_WRITABLE,!JMX_ATTRIBUTE_IS_GETTER);
	
	/** MBean Operation */
	// Reset Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_RESET_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_RESET,"Empty cache",
				null,
				//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
				String.class.getName(),
				MBeanOperationInfo.ACTION);
			
	// Print Stats Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_PRINT_STATS_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_PRINT_STATS,"View cache statistics information",
			null,
			//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
			String.class.getName(),
			MBeanOperationInfo.ACTION);
			
	// Disabilita Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_DISABILITA_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_DISABILITA,"Disable cache",
			null,
			//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
			String.class.getName(),
			MBeanOperationInfo.ACTION);
			
	// Abilita Cache con parametri
	public final static MBeanOperationInfo MBEAN_OPERATION_ABILITA_CACHE_CON_PARAMETRI 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_ABILITA,"Enable cache",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("cacheSize",Integer.class.getName(),"Cache size"),
				new MBeanParameterInfo("isCacheAlgorithmLRU",Boolean.class.getName(),"Cache algorithm LRU"),
				new MBeanParameterInfo("itemIdleTimeSeconds",Integer.class.getName(),"Item Idle Time in seconds (with -1 infinite time)"),
				new MBeanParameterInfo("itemLifeTimeSeconds",Integer.class.getName(),"Item Life Time in seconds")
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
	// List keys Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_LIST_KEYS_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_LIST_KEYS,"View cache keys",
			null,
			//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
	// get Object Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_GET_OBJECT_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_GET_OBJECT,"Retrieve the object in cache with key parameter",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("key",String.class.getName(),"cache key"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
	// remove Object Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_REMOVE_OBJECT_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_REMOVE_OBJECT,"Remove the object in cache with key parameter",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("key",String.class.getName(),"cache key"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
}
