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
package org.openspcoop2.web.monitor.core.utils;

import org.openspcoop2.web.monitor.core.logger.LoggerManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

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

		//check esistenza oggetti da copiare.
		if (oggettoDestinazione == null || oggettoOriginale == null) {
			BeanUtils.log.debug("Parametri non validi.");
			return;
		}

		// elenco dei metodi passati null, lo inizializzo.
		if (metodiEsclusi == null) {
			metodiEsclusi = new ArrayList<BlackListElement>(0);
		}

		BeanUtils.log.debug(" Copia delle properties dell'oggetto di classe["
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

				//BeanUtils.log.debug("Metodo analizzato: " + setterName);

				// il metodo da invocare e' un setter, con un solo parametro.
				if (setterName.startsWith("set")
						&& oggettoDestinazioneMethod.getParameterTypes().length == 1) {
					// controllo di corrispondenza del tipo di parametro
					BlackListElement ble = new BlackListElement(setterName,
							oggettoDestinazioneMethod.getParameterTypes());
					if (!metodiEsclusi.contains(ble)) {
						String getPrefix = "get";

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
						String getterName = getPrefix
								+ setterName.substring(setterName
										.lastIndexOf("set") + 3);
				//		BeanUtils.log.debug("Nome Getter: " + getterName);
						
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
					}
				}
			}
		} catch (IllegalAccessException e) {
			BeanUtils.log.error("Errore: Illegal Access Exception! ", e);
		} catch (SecurityException e) {
			BeanUtils.log.error("Errore: Security Exception! ", e);
		} catch (NoSuchMethodException e) {
			BeanUtils.log.error("Errore: No such method Exception! ", e);
		} catch (IllegalArgumentException e) {
			BeanUtils.log.error("Errore: Illegal Argument Exception! ", e);
		} catch (InvocationTargetException e) {
			BeanUtils.log.error("Errore: Invocation Target Exception! ", e);
		}
	}
}
