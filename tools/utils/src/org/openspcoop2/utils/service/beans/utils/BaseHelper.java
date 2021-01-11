/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.service.beans.utils;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.cxf.jaxrs.validation.JAXRSParameterNameProvider;
import org.apache.cxf.validation.ValidationConfiguration;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.service.beans.ProfiloEnum;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.utils.service.fault.jaxrs.ProblemValidation;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * Helper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class BaseHelper {
	
	@FunctionalInterface
	public interface ThrowingSupplier<T> {
		
	    T get() throws Exception;
	}
	
	public interface ThrowingRunnable {
		
		void run() throws Exception;
	}
	
	public static final<T> T dropnull(ThrowingSupplier<T> r) {
		try {
			return r.get();
		} catch (NullPointerException e) {
			return null;
		} catch( Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static final <T> T evalnull(ThrowingSupplier<T> r) {
		try {
			return r.get();
		} catch (Exception e) {
			return null;
		}
	}
	
	public static final <T> T evalorElse(ThrowingSupplier<T> r, T orElse) {
		T ret = null;
		try {
			ret = r.get();	
		} catch (Exception e) {
			// Ignoring Exception
		}
		if (ret != null) return ret;
		else return orElse;
	}
		
	
	public static final <T> T supplyOrNotFound(ThrowingSupplier<T> s, String objName) {
		T ret = null;
		try {
			ret = s.get();
		} catch (Exception e) {	}
		
		if (ret == null)
			throw FaultCode.NOT_FOUND.toException(objName + " non presente nel registro.");
		
		return ret;
	}
	
	public static final <T> T supplyOrNonValida(ThrowingSupplier<T> s, String objName) {
		T ret = null;
		try {
			ret = s.get();
		} catch (Exception e) {	}
		
		if (ret == null)
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(objName + " non presente nel registro.");
		
		return ret;
	}
	
	
	public static final void runNull(ThrowingRunnable r) {
		try {
			r.run();
		} catch (NullPointerException e) {
			// Ignore
		} catch ( Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static <T> Optional<T> findFirst(Iterable<? extends T> collection, Predicate<? super T> test) {
	    T value = null;
	    if (collection != null ) {
		    for (Iterator<? extends T> it = collection.iterator(); it.hasNext();)
		        if (test.test(value = it.next())) {
		            return Optional.of(value);
		        }
	    }
	    return Optional.empty();
	}
	
	

	public static <T> T findAndRemoveFirst(Iterable<? extends T> collection, Predicate<? super T> test) {
	    T value = null;
	    for (Iterator<? extends T> it = collection.iterator(); it.hasNext();)
	        if (test.test(value = it.next())) {
	            it.remove();
	            return value;
	        }
	    return null;
	}
	
	
	
	public static <T> void throwIfNull(T body) {
		if (body == null)
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Specificare un body");
	}
	
	
	/*@SuppressWarnings("unchecked")
	public static <T extends Enum<T>> Object deserializeFromSwitch(Map<T,Class<?>> typeMap, T discr, Object body) throws UtilsException, InstantiationException, IllegalAccessException {
		if (body == null) return null;

		// TODO: Se tutto funziona, aggiungere eccezioni per discr non riconosciuto. 
		return fromMap((Map<String,Object>) body, typeMap.get(discr));
	}*/
	
	/**
	 * Deserializza una Map json in un oggetto
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static final <T> T fromMap(Map<String,Object> mapObject, Class<T> toClass) throws InstantiationException, IllegalAccessException {
		if (mapObject == null) return null;
		
		T ret = Utilities.newInstance_throwInstantiationException(toClass);
		fillFromMap(mapObject, ret);
	
		return ret;
	}
	
	/*
	 * §Prende un nome in cui le parole sono separate da trattini bassi, e lo trasforma in stile
	 * upperCamelCase
	 */
	public static final String jsonNameToUpperCC(String key) {
		
		StringBuilder sb = new StringBuilder();
		
		Matcher m = Pattern.compile("_(\\w)").matcher(key);
		
		while (m.find()) {
			m.appendReplacement(sb, m.group(1).toUpperCase());
		}
		m.appendTail(sb);
		return sb.toString();
	}
	
	public static final <T> T fromJson(Object json, Class<T> c) {
		if (json == null) return null;
		
		try {
			return JSONUtils.getInstance().getAsObject(((InputStream)json), c);
		} catch (Exception e) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(e);
		}
	}
	
	/**
	 * Questa funzione completa la deserializzazione di un oggetto jaxrs che arriva nella Api come una LinkedHashMap&lt;String, String | LinkedHashMap&lt;String, etc..&gt;&gt;
	 * 
	 * Da notare che questo metodo sebbene generico per i fini di govway, non è da considerare un metodo valido di deserializzazione da una linkedHashmap, rappresentazione
	 * di un json, in un oggetto destinazione.
	 * 
	 * @param mapObject
	 * @param toFill
	 */
	
	@SuppressWarnings({ "unchecked"})
	public static final <T> void fillFromMap(Map<String,Object> mapObject, T toFill) throws InstantiationException {
	
		mapObject.forEach( (k, v) -> {
			if (v == null) return;
			
			try {
				k = jsonNameToUpperCC(k);
				// La chiave k non è in upperCamelCase, segue lo  stile del json in cui tutto è minuscolo e i campi sono separati
				// da trattini bassi.
				Class<?> dest = PropertyUtils.getPropertyType(toFill, k);
				Class<?> source = v.getClass();

				if ( source == String.class ) {
					final String vs = (String) v;
					
					if ( dest == (new byte[0]).getClass() ) {					
						BeanUtils.setProperty(toFill, k, Base64Utilities.decode(vs.getBytes()));
					}
					else if ( dest == String.class ) {
						BeanUtils.setProperty(toFill, k, vs);
					}
					else if ( dest.isEnum() ) {
						boolean found = false;
						Object[] constants = dest.getEnumConstants();
						if  (constants == null)
							throw new IllegalArgumentException("La classe passata non è un'enumerazione");
						
						for ( Object e : constants) {
							if (String.valueOf(e.toString()).equals(vs.trim())) {
								found = true;
								BeanUtils.setProperty(toFill, k, e);
								break;
							}
						}
						if (!found)
							throw new IllegalArgumentException("Impossibile deserialzzare l'oggetto di valore: " + vs + " e classe destinazione " + dest.toGenericString());
					}
					else if ( dest == Integer.class ) {
						BeanUtils.setProperty(toFill, k, Integer.valueOf(vs));
					}
					else if (dest == Object.class ) {
						BeanUtils.setProperty(toFill, k, vs);
					}
					// TODO: Qui potrei sollevare un'eccezione se il campo non c'è nell'oggetto..
				}
				
				else if ( source == LinkedHashMap.class && dest != Object.class) {	
						// Se dobbiamo deserializzare una Map, abbiamo bisogno che la destinazione sia tipata e non un semplice object.
						BeanUtils.setProperty(toFill, k, fromMap((Map<String,Object>)v, dest));
				}
				else {	// Fallback, li assumo dello stesso tipo, se non lo sono, ci pensa setProperty a sollevare eccezione.
					BeanUtils.setProperty(toFill, k, v);
				}
				
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
				throw new RuntimeException(e); 	//TODO: log
			}
		});
		
	}
	
	public static final Map<ProfiloEnum,String> tipoProtocolloFromProfilo = ProfiloUtils.getMapProfiloToProtocollo();	
	public static final Map<String,ProfiloEnum> profiloFromTipoProtocollo = ProfiloUtils.getMapProtocolloToProfilo();
	

	public static <T> void validateBean(T bean) {
		if (bean == null) return;
		
		JAXRSParameterNameProvider parameterNameProvider = new JAXRSParameterNameProvider();
        Configuration<?> factoryCfg = Validation.byDefaultProvider().configure();
        ValidationConfiguration cfg = new ValidationConfiguration(parameterNameProvider);
        if (cfg != null) {
            factoryCfg.parameterNameProvider(cfg.getParameterNameProvider());
            factoryCfg.messageInterpolator(cfg.getMessageInterpolator());
            factoryCfg.traversableResolver(cfg.getTraversableResolver());
            factoryCfg.constraintValidatorFactory(cfg.getConstraintValidatorFactory());
            for (Map.Entry<String, String> entry : cfg.getProperties().entrySet()) {
                factoryCfg.addProperty(entry.getKey(), entry.getValue());
            }
        }

		ValidatorFactory factory = factoryCfg.buildValidatorFactory();
		Validator validator = factory.getValidator();
		Set<ConstraintViolation<T>> violations = validator.validate(bean);
		
		if (!violations.isEmpty()) {
        	ProblemValidation problem = new ProblemValidation(FaultCode.RICHIESTA_NON_VALIDA.toFault());
			
			for (ConstraintViolation<T> violation : violations) {
				String msg = bean.getClass().getSimpleName() + "." + violation.getPropertyPath(); 
				problem.addInvalidParam(msg, violation.getMessage(), null);
			}
			
			throw FaultCode.RICHIESTA_NON_VALIDA.toException(Response.status(problem.getStatus()).entity(problem).type(HttpConstants.CONTENT_TYPE_JSON_PROBLEM_DETAILS_RFC_7807).build());
		}
	}
	
	public static boolean validateAfterDeserialize = true;
	
	@SuppressWarnings("unchecked")
	public static <T> T deserialize(Object o, Class<T> dest) {
		T ret = null;
		
		if (o != null) {
			if(dest.isInstance(o))
			{
				ret = dest.cast(o);
			} 
			else
			{
				try {
					ret =  fromMap((Map<String, Object>) o,dest);
				} catch (Exception e) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Impossibile deserializzare l'oggetto " + dest.getName() + ", formato non valido: " + e.getMessage()); 
				}
			}
		}
		
		if(validateAfterDeserialize) {
			validateBean(ret);
		}
		return ret;
	}
	
	
	public static <T> Optional<T> deserializeOptional(Object o, Class<T> dest) {
		return Optional.ofNullable(deserialize(o,dest));
	}
	
	public static <T> T deserializeDefault(Object o, Class<T> dest) {		
		Optional<T> ret = deserializeOptional(o, dest);
		if (!ret.isPresent() || (ret.isPresent() && ret.get() == null)) {
			try {
				return Utilities.newInstance(dest);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return ret.get();
		}		
	}
	
	
}
