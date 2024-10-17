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
package org.openspcoop2.web.monitor.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.web.monitor.core.constants.Costanti;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;

public class RicercheUtils {

	private RicercheUtils() {}

	private static Logger log =  LoggerManager.getPddMonitorCoreLogger();

	public static Map<String, Object> getNonNullFields(Object bean, List<String> fieldsToIgnore) {
		Map<String, Object> fieldMap = new HashMap<>();
		
		if(fieldsToIgnore == null) {
			fieldsToIgnore = Collections.emptyList();
		}

		// Ottieni la classe corrente del bean
		Class<?> currentClass = bean.getClass();

		// Continua finché non raggiungi la classe Object
		while (currentClass != null && currentClass != Object.class) {
			// Ottieni tutti i campi della classe corrente
			Field[] fields = currentClass.getDeclaredFields();

			for (Field field : fields) {
				field.setAccessible(true); // Rendi accessibile il campo privato

				try {
					String fieldName = field.getName();

					// Se il nome del campo è nella lista da ignorare, salta questo campo
					if (fieldsToIgnore.contains(fieldName)) {
						log.debug("Field [{}] da non salvare.", fieldName);
						continue;
					}

					Object value = field.get(bean); // Ottieni il valore del campo
					
					log.debug("Field [{}], Valore [{}]", fieldName, value);
					
					// Aggiungi alla mappa solo se il campo non è null
					if (value != null) {
						// Se è una stringa, controlla che non sia vuota
						if (value instanceof String && !((String) value).isEmpty()) {
			                if (Costanti.PERIODO_PARAMETER.equals(fieldName) && Costanti.PERIODO_PERSONALIZZATO.equals(value)) {
			                    log.debug("Field [{}] con valore '{}' viene ignorato.", fieldName, value);
			                    continue;
			                }
							
							fieldMap.put(fieldName, value);
						}
						// Se è una collezione, controlla che non sia vuota
						else if (value instanceof Collection && !((Collection<?>) value).isEmpty()) {
							fieldMap.put(fieldName, value);
						}
						// Se è un booleano, aggiungilo sempre (anche se false)
						else if (value instanceof Boolean) {
							fieldMap.put(fieldName, value);
						}
						// Se è un integer, aggiungilo sempre 
						else if (value instanceof Integer || value instanceof Double || value instanceof Long) {
							fieldMap.put(fieldName, value);
						}
						// Se è un enum, aggiungi sempre (enum non può essere null se istanziato)
						else if (value.getClass().isEnum()) {
							fieldMap.put(fieldName, value);
						} 
						// Gestione per array di stringhe
	                    else if (value instanceof String[]) {
	                        String[] array = (String[]) value;
	                        // Controlla che l'array non sia vuoto
	                        if (array.length > 0) {
	                            fieldMap.put(fieldName, array);
	                        }
	                    }
						else {
							log.debug("Field [{}], Tipo [{}] non gestito", fieldName, value.getClass());
						}
					} 
				} catch (IllegalAccessException e) {
					e.printStackTrace(); // Gestisci l'eccezione se il campo non è accessibile
				}
			}

			// Passa alla superclasse
			currentClass = currentClass.getSuperclass();
		}

		return fieldMap;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void applyFieldsToBean(Object bean, Map<String, Object> fieldMap) {
	    // Itera sulla mappa dei campi
	    for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
	        String fieldName = entry.getKey(); // Nome del campo
	        Object fieldValue = entry.getValue(); // Valore del campo

	        try {
	            // Trova il campo nella classe del bean o nelle sue superclassi
	            Field field = getFieldFromClassHierarchy(bean.getClass(), fieldName);
	            if (field != null) {
	                field.setAccessible(true); // Rendi accessibile il campo privato

	                // Gestisci i tipi Enum separatamente
	                if (field.getType().isEnum() && fieldValue instanceof String) {
	                	
	                	Object enumValue = null;
	                    try {
	                        // Verifica se la classe Enum ha un metodo statico "toEnumConstant"
	                        Method toEnumConstantMethod = field.getType().getMethod("toEnumConstant", String.class);
	                        
	                        if (toEnumConstantMethod != null && Modifier.isStatic(toEnumConstantMethod.getModifiers())) {
	                            // Invoca il metodo toEnumConstant dinamicamente
	                            enumValue = toEnumConstantMethod.invoke(null, (String) fieldValue);
	                        } else {
	                            // Usa Enum.valueOf come fallback
	                            enumValue = Enum.valueOf((Class<Enum>) field.getType(), (String) fieldValue);
	                        }
	                    } catch (NoSuchMethodException e) {
	                        // Se non esiste il metodo toEnumConstant, usa Enum.valueOf
	                        enumValue = Enum.valueOf((Class<Enum>) field.getType(), (String) fieldValue);
	                    } catch (Exception e) {
	                        throw new RuntimeException("Errore durante la conversione dell'Enum", e);
	                    }
	                    
	                    log.debug("Field di tipo Enum [{}], Valore [{}]", fieldName, enumValue);
	                    
	                    field.set(bean, enumValue);
	                } // Gestione per array di stringhe
	                else if (field.getType().isArray() && field.getType().getComponentType() == String.class && fieldValue instanceof Collection) {
	                    // Converte la collezione in un array di stringhe
	                    Collection<?> collection = (Collection<?>) fieldValue;
	                    String[] stringArray = collection.toArray(new String[0]);

	                    log.debug("Field di tipo Array di String [{}], Valore [{}]", fieldName, Arrays	.toString(stringArray));

	                    field.set(bean, stringArray);
	                } else {
	                	log.debug("Field [{}], Valore [{}]", fieldName, fieldValue);
	                    // Imposta il valore del campo
	                    field.set(bean, fieldValue);
	                }
	            } else {
	            	log.debug("Campo {} non trovato nella gerarchia delle classi.", fieldName);
	            }
	        } catch (IllegalAccessException e) {
	            e.printStackTrace(); // Gestisci eventuali errori di accesso
	        }
	    }
	}

	// Metodo di supporto per ottenere un campo dalla classe o dalla sua gerarchia di superclassi
	private static Field getFieldFromClassHierarchy(Class<?> clazz, String fieldName) {
	    while (clazz != null && clazz != Object.class) {
	        try {
	            return clazz.getDeclaredField(fieldName); // Cerca il campo nella classe corrente
	        } catch (NoSuchFieldException e) {
	            // Se il campo non esiste nella classe corrente, passa alla superclasse
	            clazz = clazz.getSuperclass();
	        }
	    }
	    return null; // Campo non trovato nella gerarchia delle classi
	}
}
