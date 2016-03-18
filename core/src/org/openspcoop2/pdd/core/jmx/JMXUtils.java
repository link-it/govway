/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package org.openspcoop2.pdd.core.jmx;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

/**
 * JMXUtils
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class JMXUtils {

	/** Nomi attributi */
	public final static String CACHE_ATTRIBUTE_ABILITATA = "cacheAbilitata";
	
	/** Nomi metodi */
	public final static String CACHE_METHOD_NAME_RESET = "resetCache"; 
	public final static String CACHE_METHOD_NAME_PRINT_STATS = "printStatsCache"; 
	public final static String CACHE_METHOD_NAME_ABILITA = "abilitaCache";
	public final static String CACHE_METHOD_NAME_DISABILITA = "disabilitaCache";
	public final static String CACHE_METHOD_NAME_LIST_KEYS = "listKeysCache";
	public final static String CACHE_METHOD_NAME_GET_OBJECT = "getObjectCache";
	public final static String CACHE_METHOD_NAME_REMOVE_OBJECT = "removeObjectCache";
	
	/** Messaggi */
	public final static String MSG_OPERAZIONE_NON_EFFETTUATA = "Operazione non riuscita: ";
	public final static String MSG_RESET_CACHE_EFFETTUATO_SUCCESSO = "Operazione di reset effettuata con successo";
	public final static String MSG_ABILITAZIONE_CACHE_EFFETTUATA = "Abilitazione cache effettuata con successo";
	public final static String MSG_DISABILITAZIONE_CACHE_EFFETTUATA = "Disabilitazione cache effettuata con successo";
	public final static String MSG_RIMOZIONE_CACHE_EFFETTUATA = "Rimozione elemento dalla cache effettuata con successo";

	/** Per determinare se l'attributo e' leggibile/scrivibile */
	public final static boolean JMX_ATTRIBUTE_READABLE = true;
	public final static boolean JMX_ATTRIBUTE_WRITABLE = true;
	/** Per determinare se l'attributo e' ricavabile nella forma booleana isAttribute() */
	public final static boolean JMX_ATTRIBUTE_IS_GETTER = true;
	
	/** MBean Attribute */
	public final static MBeanAttributeInfo MBEAN_ATTRIBUTE_INFO_CACHE_ABILITATA = 
			new MBeanAttributeInfo(CACHE_ATTRIBUTE_ABILITATA,boolean.class.getName(),
					"Indicazione se e' abilita una cache",
					JMX_ATTRIBUTE_READABLE,JMX_ATTRIBUTE_WRITABLE,!JMX_ATTRIBUTE_IS_GETTER);
	
	/** MBean Operation */
	// Reset Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_RESET_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_RESET,"Svuota la cache",
				null,
				//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
				String.class.getName(),
				MBeanOperationInfo.ACTION);
			
	// Print Stats Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_PRINT_STATS_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_PRINT_STATS,"Visualizza le informazioni statistiche sugli oggetti presenti in cache",
			null,
			//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
			String.class.getName(),
			MBeanOperationInfo.ACTION);
			
	// Disabilita Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_DISABILITA_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_DISABILITA,"Disabilita la cache",
			null,
			//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
			String.class.getName(),
			MBeanOperationInfo.ACTION);
			
	// Abilita Cache con parametri
	public final static MBeanOperationInfo MBEAN_OPERATION_ABILITA_CACHE_CON_PARAMETRI 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_ABILITA,"Abilita la cache",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("dimensioneCache",long.class.getName(),"Dimensione della cache"),
				new MBeanParameterInfo("algoritmoCacheLRU",boolean.class.getName(),"Algoritmo utilizzato (lru/mru)"),
				new MBeanParameterInfo("itemIdleTime",long.class.getName(),"Indica il massimo intervallo di tempo che un item può esistere senza essere acceduto (con -1 verra' assegnato un tempo infinito)"),
				new MBeanParameterInfo("itemLifeSecond",long.class.getName(),"Vita di un elemento inserito in cache (secondi)")
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
	// List keys Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_LIST_KEYS_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_LIST_KEYS,"Visualizza le chiavi attualmente presenti nella cache",
			null,
			//new MBeanParameterInfo[]{new MBeanParameterInfo("param",String.class.getName())}
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
	// get Object Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_GET_OBJECT_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_GET_OBJECT,"Recupera l'oggetto registrato nella cache con chiave fornita come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("key",String.class.getName(),"Chiave dell'oggetto in cache"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
	// remove Object Cache
	public final static MBeanOperationInfo MBEAN_OPERATION_REMOVE_OBJECT_CACHE 
		= new MBeanOperationInfo(CACHE_METHOD_NAME_REMOVE_OBJECT,"Rimuove l'oggetto registrato nella cache con chiave fornita come parametro",
			new MBeanParameterInfo[]{
				new MBeanParameterInfo("key",String.class.getName(),"Chiave dell'oggetto in cache"),
			},
			String.class.getName(),
			MBeanOperationInfo.ACTION);
	
}
