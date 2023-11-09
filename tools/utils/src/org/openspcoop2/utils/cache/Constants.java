/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
	
	private Constants() {}

	/** Nomi attributi */
	public static final String CACHE_ATTRIBUTE_ABILITATA = "cache";
	
	/** Nomi metodi */
	public static final String CACHE_METHOD_NAME_RESET = "resetCache"; 
	public static final String CACHE_METHOD_NAME_PRINT_STATS = "printStatsCache"; 
	public static final String CACHE_METHOD_NAME_ABILITA = "enableCache";
	public static final String CACHE_METHOD_NAME_DISABILITA = "disableCache";
	public static final String CACHE_METHOD_NAME_LIST_KEYS = "listKeysCache";
	public static final String CACHE_METHOD_NAME_GET_OBJECT = "getObjectCache";
	public static final String CACHE_METHOD_NAME_REMOVE_OBJECT = "removeObjectCache";
	
	/** Messaggi */
	public static final String MSG_OPERAZIONE_NON_EFFETTUATA = "Operation failed: ";
	public static final String MSG_OPERATION_PREFIX = "Operation '";
	public static final String MSG_OPERATION_DONE = "' done";
	public static final String MSG_RESET_CACHE_EFFETTUATO_SUCCESSO = MSG_OPERATION_PREFIX+CACHE_METHOD_NAME_RESET+MSG_OPERATION_DONE;
	public static final String MSG_ABILITAZIONE_CACHE_EFFETTUATA = MSG_OPERATION_PREFIX+CACHE_METHOD_NAME_ABILITA+MSG_OPERATION_DONE;
	public static final String MSG_DISABILITAZIONE_CACHE_EFFETTUATA = MSG_OPERATION_PREFIX+CACHE_METHOD_NAME_DISABILITA+MSG_OPERATION_DONE;
	
	public static final String MSG_CACHE_NON_ABILITATA = "Cache non abilitata";
	public static final String MSG_CACHE_GIA_ABILITATA = "Cache già abilitata";
	public static final String MSG_CACHE_GIA_DISABILITATA = "Cache già disabilitata";
	
	public static final String MSG_CACHE = "Cache";
	public static final String MSG_CACHE_PREFIX = "Cache-";

	/** Per determinare se l'attributo e' leggibile/scrivibile */
	public static final boolean JMX_ATTRIBUTE_READABLE = true;
	public static final boolean JMX_ATTRIBUTE_WRITABLE = true;
	/** Per determinare se l'attributo e' ricavabile nella forma booleana isAttribute() */
	public static final boolean JMX_ATTRIBUTE_IS_GETTER = true;
	
	/** MBean Attribute */
	public static final MBeanAttributeInfo MBEAN_ATTRIBUTE_INFO_CACHE_ABILITATA = 
			new MBeanAttributeInfo(CACHE_ATTRIBUTE_ABILITATA,boolean.class.getName(),
					"Cache enabled",
					JMX_ATTRIBUTE_READABLE,!JMX_ATTRIBUTE_WRITABLE,!JMX_ATTRIBUTE_IS_GETTER);
	
	/** MBean Operation */
	// Reset Cache
	public static final MBeanOperationInfo MBEAN_OPERATION_RESET_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_RESET,"Empty cache",
				null,
				String.class.getName(),
				MBeanOperationInfo.ACTION);
			
	// Print Stats Cache
	public static final MBeanOperationInfo MBEAN_OPERATION_PRINT_STATS_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_PRINT_STATS,"View cache statistics information",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
			
	// Disabilita Cache
	public static final MBeanOperationInfo MBEAN_OPERATION_DISABILITA_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_DISABILITA,"Disable cache",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
			
	// Abilita Cache con parametri
	public static final MBeanOperationInfo MBEAN_OPERATION_ABILITA_CACHE_CON_PARAMETRI 
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
	public static final MBeanOperationInfo MBEAN_OPERATION_LIST_KEYS_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_LIST_KEYS,"View cache keys",
			null,
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
	// get Object Cache
	public static final MBeanOperationInfo MBEAN_OPERATION_GET_OBJECT_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_GET_OBJECT,"Retrieve the object in cache with key parameter",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("key",String.class.getName(),"cache key"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
	// remove Object Cache
	public static final MBeanOperationInfo MBEAN_OPERATION_REMOVE_OBJECT_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_REMOVE_OBJECT,"Remove the object in cache with key parameter",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("key",String.class.getName(),"cache key"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
}
