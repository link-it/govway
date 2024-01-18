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
package org.openspcoop2.utils.beans;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/****
 * 
 * Implementa funzionalita' di gestione dei bean.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class BeanUtils {

	private static Logger log =  LoggerWrapperFactory.getLogger(BeanUtils.class);

	/*****
	 * 
	 * Implementa la funzione di copia del valore delle properties dell'oggetto
	 * sorgente in quello destinazione.
	 * 
	 * E' possibile specificare un elenco di metodi setter che si vogliono non
	 * invocare.
	 * 
	 * @param oggettoDestinazione
	 * @param oggettoOriginale
	 * @param metodiEsclusi
	 */
	public static void copy(Object oggettoDestinazione,
			Object oggettoOriginale, List<BlackListElement> metodiEsclusi) {
		copy(BeanUtils.log, oggettoDestinazione, oggettoOriginale, metodiEsclusi);
	}
	public static void copy(Object oggettoDestinazione,
			Object oggettoOriginale) {
		copy(BeanUtils.log, oggettoDestinazione, oggettoOriginale, null);
	}
	public static void copy(Logger logParam, Object oggettoDestinazione,
			Object oggettoOriginale) {
		copy(logParam, oggettoDestinazione, oggettoOriginale, null);
	}
	public static void copy(Logger logParam, Object oggettoDestinazione,
			Object oggettoOriginale, List<BlackListElement> metodiEsclusi) {
		copy(logParam, oggettoDestinazione, oggettoOriginale, metodiEsclusi, false);
	}
	public static void copy(Logger logParam, Object oggettoDestinazione,
			Object oggettoOriginale, List<BlackListElement> metodiEsclusi,
			boolean throwRuntimeException) {

		//check esistenza oggetti da copiare.
		if (oggettoDestinazione == null || oggettoOriginale == null) {
			logParam.debug("Parametri non validi.");
			return;
		}

		// elenco dei metodi passati null, lo inizializzo.
		if (metodiEsclusi == null) {
			metodiEsclusi = new ArrayList<BlackListElement>(0);
		}

		logParam.debug(" Copia delle properties dell'oggetto di classe["
				+ oggettoOriginale.getClass().getName()
				+ "] all'interno dell'oggetto ["
				+ oggettoDestinazione.getClass().getName() + "]");

		// 1. prelevo l'oggetto Class dei due oggetti da manipolare.
		Class<?> oggettoOriginaleClass = oggettoOriginale.getClass();
		Class<?> oggettoDestinazioneClass = oggettoDestinazione.getClass();
		try {
			// 2. scorro i metodi a disposizione nell'oggetto destinazione.
			for (Method oggettoDestinazioneMethod : oggettoDestinazioneClass
					.getMethods()) {
				String setterName = oggettoDestinazioneMethod.getName();

				//logParam.debug("Metodo analizzato: " + setterName);

				// il metodo da invocare e' un setter, con un solo parametro.
				if (setterName.startsWith("set")
						&& oggettoDestinazioneMethod.getParameterTypes().length == 1) {
					// controllo di corrispondenza del tipo di parametro
					BlackListElement ble = new BlackListElement(setterName,
							oggettoDestinazioneMethod.getParameterTypes());
					if (!metodiEsclusi.contains(ble)) {
						String getPrefix = "get";

						String name = setterName.substring(setterName
										.lastIndexOf("set") + 3);
						try {
						
							// caso particolare: i getter che restituiscono boolean
							// o Boolean hanno il nome che inizia per 'is'
							if (oggettoDestinazioneMethod.getParameterTypes()[0]
									.equals(Boolean.class)
									|| oggettoDestinazioneMethod
									.getParameterTypes()[0]
											.equals(boolean.class)) {
								getPrefix = "is";
							}
	
							// calcolo il nome del metodo getter corrispondente nell'oggetto origine;
							String getterName = getPrefix+ name;
							//		logParam.debug("Nome Getter: " + getterName);
	
							// prelevo il metodo getter.
							Method oggettoOriginaleMethod = oggettoOriginaleClass
									.getMethod(getterName);
	
							// invoco il getter nell'oggetto origine per ottenere il valore da settare nell'oggetto destinazione.
							if (oggettoOriginaleMethod != null) {
								Object retObj = oggettoOriginaleMethod.invoke(
										oggettoOriginale);
	
								// prelevo il valore e lo utilizzo come parametro del metodo setter. 
								if (retObj != null) {
									oggettoDestinazioneMethod.invoke(
											oggettoDestinazione, retObj);
								}
							}
						}catch(Throwable e) {
							throw new Exception("[field '"+name+"'] "+e.getMessage(),e);
						}
			
					}
				}
			}
		}catch (Throwable e) {
			logParam.error(e.getMessage(), e);
			if(throwRuntimeException) {
				throw new RuntimeException(e.getMessage(),e);
			}
		}
	}
}
