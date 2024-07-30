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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.UtilsRuntimeException;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.serialization.ISerializer;
import org.openspcoop2.utils.serialization.JavaSerializer;
import org.openspcoop2.utils.serialization.SerializationConfig;
import org.openspcoop2.utils.serialization.SerializationFactory;
import org.openspcoop2.utils.serialization.SerializationFactory.SERIALIZATION_TYPE;
import org.openspcoop2.utils.xml.JaxbUtils;

/**
 * BaseBean
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlTransient
public abstract class BaseBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final String TO_STRING_METHOD = "toString";
	
	protected BaseBean(){
		// nop
	}
	
	private void logExceptionStackTrace(Exception e) {
		e.printStackTrace(System.err);
	}
	
	/* ********** GENERIC UTILS ********* */
	
	private Object getFieldValue(String fieldName,Object object) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Class<?> c = getClass();
		
		StringBuilder methodName = new StringBuilder("get");
		String firstChar = fieldName.charAt(0)+"";
		methodName.append(firstChar.toUpperCase());
		if(fieldName.length()>1){
			methodName.append(fieldName.substring(1));
		}
		
		Method method = null;
		try{
			method = c.getMethod(methodName.toString());
		}catch(java.lang.NoSuchMethodException nsme){
			if("get_default".equals(methodName.toString())){
				// provo a recuperare getDefault
				try{
					method = c.getMethod("getDefault");
				}catch(Exception t){
					throw nsme; // rilancio eccezione originale
				}
			}
			else{
				throw nsme; // rilancio eccezione originale
			}
		}
		return method.invoke(object);
	}
	
	private Object setFieldValue(String fieldName,Object object,Class<?> parameterType,Object parameterValue) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Class<?> c = getClass();
		
		StringBuilder methodName = new StringBuilder("set");
		String firstChar = fieldName.charAt(0)+"";
		methodName.append(firstChar.toUpperCase());
		if(fieldName.length()>1){
			methodName.append(fieldName.substring(1));
		}
		
		Method method = null;
		try{
			method = c.getMethod(methodName.toString(),parameterType);
		}catch(java.lang.NoSuchMethodException nsme){
			if("set_default".equals(methodName.toString())){
				// provo a recuperare setDefault
				try{
					method = c.getMethod("setDefault",parameterType);
				}catch(Exception t){
					throw nsme; // rilancio eccezione originale
				}
			}
			else{
				throw nsme; // rilancio eccezione originale
			}
		}
		return method.invoke(object,parameterValue);
	}
	
	
	
	
	
	// Non lo si vuole realizzare, si demanda eventualmente alla classe che lo implementa
	// Se lo si aggiunge poi viene invocato l'equals e per alcuni field per cui non esiste il metodo get si ottiene errore (es. transazioneBean)
	/**@Override
	public int hashCode(){
		return this.getClass().getName().hashCode();
	}*/
	
	/* ********** EQUALS ********* */
	
	@Override
	public boolean equals(Object object){
		return this.equalsEngine(object,null);
	}
	public boolean equals(Object object,boolean checkLongId){
		List<String> listFieldsNotCheck = new ArrayList<>();
		if(!checkLongId){
			listFieldsNotCheck.add("id");
		}
		return this.equalsEngine(object, listFieldsNotCheck);
	}
	public boolean equals(Object object,List<String> fieldsNotCheck){
		return this.equalsEngine(object, fieldsNotCheck);
	}
	@SuppressWarnings("unchecked")
	private boolean equalsEngine(Object object,List<String> fieldsNotCheck){
		try{
			if(object==null){
				return false;
			}
			java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
			for(int i=0; i<fields.length;i++){
				
				/**System.out.println("["+fields[i].getName()+"]");*/
				if(java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())){
					/**System.out.println("IS STATIC");*/
					continue;
				}
				
				if(fields[i].getType().isAssignableFrom(org.openspcoop2.utils.jaxb.DecimalWrapper.class)){
					continue;
				}
				
				// field id non controllato se in fieldsNotCheckList
				boolean ignoreField = false;
				if(fieldsNotCheck!=null && !fieldsNotCheck.isEmpty()){
					for (String fieldName : fieldsNotCheck) {
						if(fieldName.equals(fields[i].getName())){
							ignoreField = true;
							break;
						}
					}
				}
				if(ignoreField){
					continue;
				}
				
				Object fieldValueThis = this.getFieldValue(fields[i].getName(),this);
				Object fieldValueObject = this.getFieldValue(fields[i].getName(),object);
				/**System.out.println("LETTO VALORE["+fieldValue+"]");*/
				
				if(fieldValueThis==null){
					// FIELD
					if(fieldValueObject!=null){
						return false;
					}
				}else{
					// ARRAY_LIST
					if( (fields[i].getType().isAssignableFrom(java.util.ArrayList.class)) || (fields[i].getType().isAssignableFrom(java.util.List.class)) ){
						java.util.List<?> listaThis = (java.util.List<?>) fieldValueThis;
						java.util.List<?> listaParameter = (java.util.List<?>) fieldValueObject;
						if(listaParameter==null)
							return false;
						if(listaThis.size() != listaParameter.size())
							return false;
						for(int j=0; j<listaThis.size(); j++){
							if(listaThis.get(j)==null &&
								listaParameter.get(j)!=null) {
								return false;
							}
						}
						// SORT
						java.util.List<Object> listaThisSORT = new java.util.ArrayList<>();
						java.util.List<Object> listaParameterSORT = new java.util.ArrayList<>();
						try{
							java.util.List<String> listaThisTMP = new java.util.ArrayList<>();
							java.util.Map<String, Object> thisTmp = new java.util.HashMap<>();
							java.util.Map<String, Object> parameterTmp = new java.util.HashMap<>();
							for(int k=0; k<listaThis.size(); k++){
								Object thisObject = listaThis.get(k);
								Object paramObject = listaParameter.get(k);
								if(thisObject==null && paramObject!=null)
									throw new UtilsException("DIFF");
								if(thisObject!=null && paramObject==null)
									throw new UtilsException("DIFF");
								if(thisObject!=null && paramObject!=null){
									// THIS
									String key = null;
									if(thisObject.getClass().getName().startsWith(this.getClass().getPackage().getName())){
										Class<?> c =  thisObject.getClass();
										java.lang.reflect.Method method =  c.getMethod(TO_STRING_METHOD,boolean.class,boolean.class);
										key = (String) method.invoke(thisObject,false,true);
									}else{
										key = thisObject.toString();
									}
									thisTmp.put(key, thisObject);
									listaThisTMP.add(key);

									// PARAM
									if(paramObject.getClass().getName().startsWith(this.getClass().getPackage().getName())){
										Class<?> c =  paramObject.getClass();
										java.lang.reflect.Method method =  c.getMethod(TO_STRING_METHOD,boolean.class,boolean.class);
										key = (String) method.invoke(paramObject,false,true);
									}else{
										key = paramObject.toString();
									}
									parameterTmp.put(key, paramObject);
								}
							}
							java.util.Collections.sort(listaThisTMP);
							for(int k=0; k<listaThisTMP.size(); k++){
								String key = listaThisTMP.get(k);
								Object thisObject = thisTmp.get(key);
								Object paramObject = parameterTmp.get(key);
								if(thisObject==null || paramObject==null){
									// significa che manca un elemento
									return false;
								}
								listaThisSORT.add(thisObject);
								listaParameterSORT.add(paramObject);
							}
						}
						catch(RuntimeException e){
							// By SpotBugs
							listaParameterSORT = (java.util.List<Object>) listaParameter;
							listaThisSORT = (java.util.List<Object>) listaThis;
						}
						catch(Exception e){
							listaThisSORT = (java.util.List<Object>) listaThis;
							listaParameterSORT = (java.util.List<Object>) listaParameter;
						}
						for(int j=0; j<listaThisSORT.size(); j++){
							Class<?> c =  listaThisSORT.get(j).getClass();
							boolean resultMethod = false;
							if(listaThis.get(j).getClass().getName().startsWith(this.getClass().getPackage().getName())){
								java.lang.reflect.Method method =  c.getMethod("equals",Object.class,List.class);
								resultMethod = (Boolean) method.invoke(listaThisSORT.get(j),listaParameterSORT.get(j),fieldsNotCheck);
							}else{
								resultMethod = listaThisSORT.get(j).equals(listaParameterSORT.get(j));
							}
							if(!resultMethod)
								return false;
						}
					}else{
						boolean resultMethod = false;
						if(fields[i].getType().getName().startsWith(this.getClass().getPackage().getName())){
							java.lang.reflect.Method method =  fields[i].getType().getMethod("equals",Object.class,List.class);
							resultMethod = (Boolean) method.invoke(fieldValueThis,fieldValueObject,fieldsNotCheck);
						}else{
							boolean checkUguaglianza = false;
							if(fields[i].getType().isAssignableFrom(byte[].class)){
								if(fieldValueObject!=null){
									byte[] origi = (byte[]) fieldValueThis;
									byte[] dest = (byte[]) fieldValueObject;
									checkUguaglianza = (origi.length == dest.length);
									if(checkUguaglianza){
										for(int k=0;k<origi.length;k++){
											if(origi[k]!=dest[k]){
												checkUguaglianza = false;
												break;
											}
										}
									}
								}
							}else{
								if(fields[i].getType().isAssignableFrom(java.util.Date.class)){
									if(fieldValueObject==null) {
										checkUguaglianza=false; // che fieldValue_this non sia null e' gia' stato controllato sopra
									}
									else {
										java.util.Calendar calendarThis = new java.util.GregorianCalendar();
										calendarThis.setTime((java.util.Date)fieldValueThis);
										java.util.Calendar calendarObject = new java.util.GregorianCalendar();
										calendarObject.setTime((java.util.Date)fieldValueObject);
										checkUguaglianza = (  calendarThis.get(java.util.Calendar.YEAR) == calendarObject.get(java.util.Calendar.YEAR) ) && 
												( calendarThis.get(java.util.Calendar.MONTH) == calendarObject.get(java.util.Calendar.MONTH) ) && 
												( calendarThis.get(java.util.Calendar.DAY_OF_MONTH) == calendarObject.get(java.util.Calendar.DAY_OF_MONTH) ) && 
												( calendarThis.get(java.util.Calendar.HOUR_OF_DAY) == calendarObject.get(java.util.Calendar.HOUR_OF_DAY) ) && 
												( calendarThis.get(java.util.Calendar.MINUTE) == calendarObject.get(java.util.Calendar.MINUTE) ) && 
												( calendarThis.get(java.util.Calendar.SECOND) == calendarObject.get(java.util.Calendar.SECOND) ) && 
												( calendarThis.get(java.util.Calendar.MILLISECOND) == calendarObject.get(java.util.Calendar.MILLISECOND) ) ;
									}
								}else{
									checkUguaglianza = fieldValueThis.equals(fieldValueObject);
								}
							}
							resultMethod = checkUguaglianza;
						}
						if(!resultMethod)
							return false;
					}
				}
			}
			return true;
		}catch(Exception e){
			logExceptionStackTrace(e);
			throw new UtilsRuntimeException(e.toString(),e);
		}
	}

	
	
	
	
	
	
	
	/* ********** WRITE TO ********* */
	
	public void writeTo(OutputStream out) throws UtilsException{
		this.writeTo(out, WriteToSerializerType.XML_JAXB);
	}
	public void writeTo(OutputStream out,WriteToSerializerType type) throws UtilsException{
		this.writeToEngine(out, type, null);
	}
	public void writeTo(OutputStream out,WriteToSerializerType type, boolean pretty) throws UtilsException{
		this.writeToEngine(out, type, pretty);
	}
	private void writeToEngine(OutputStream out,WriteToSerializerType type, Boolean pretty) throws UtilsException{
		try{
			switch (type) {
				case XML_JAXB:
					boolean xmlPretty = true;  /** backward compatibility;*/
					if(pretty!=null) { 
						xmlPretty = pretty.booleanValue();
					}
					JaxbUtils.objToXml(out, getClass(), this, xmlPretty);
					break;
				case JSON_JACKSON:
					SerializationConfig config = new SerializationConfig();
					if(pretty!=null) {
						config.setPrettyPrint(pretty.booleanValue());
					}
					ISerializer jsonJacksonSerializer = SerializationFactory.getSerializer(SERIALIZATION_TYPE.JSON_JACKSON, config);
					jsonJacksonSerializer.writeObject(this, out);
					break;
				case JAVA:
					JavaSerializer javaSerializer = new JavaSerializer();
					javaSerializer.writeObject(this, out);
					break;
			}
		}catch(Exception e){
			throw new UtilsException(e.getMessage(), e);
		}
	}
	
	
	
	
	
	/* ********** serialize ********* */
	
	public String toXml() throws UtilsException{
		return this.serialize(WriteToSerializerType.XML_JAXB);
	}
	public String toJson() throws UtilsException{
		return this.serialize(WriteToSerializerType.JSON_JACKSON);
	}
	// Non è utile. Al massimo si usa sopra come writeTo
	/**public String toJava() throws UtilsException{
		return this.serialize(WriteToSerializerType.JAVA);
	}*/
	public String serialize(WriteToSerializerType type) throws UtilsException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(bout, type);
		return bout.toString();
	}
	
	
	
	
	
	
	
	
	/* ********** TO STRING ********* */
	
	private static ToStringStyle defaultToStringStyle =  ToStringStyle.MULTI_LINE_STYLE;
	private static boolean defaultToStringOutputTransients =  false;
	private static boolean defaultToStringOutputStatics =  false;
	public static void setDefaultStyleToString(ToStringStyle style,boolean outputTransients,boolean outputStatics){
		// Il default statico serve per impostare uno "stile" che viene ereditato da tutti gli oggetti che estendono il base bean.
		// In questa maniera gli oggetti interni all'oggetto stesso vengono serializzati con lo stile impostato.
		defaultToStringStyle = style;
		defaultToStringOutputTransients = outputTransients;
		defaultToStringOutputStatics = outputStatics;
	}
	@Override
	public String toString(){
		return this.toString(defaultToStringStyle, defaultToStringOutputTransients, defaultToStringOutputStatics, null);
	}
	public String toString(ToStringStyle style){
		return this.toString(style, false, false, null);
	}
	public String toString(ToStringStyle style,boolean outputTransients,boolean outputStatics){
		return this.toString(style, outputTransients, outputStatics, null);
	}
	public String toString(ToStringStyle style,boolean outputTransients,boolean outputStatics,Class<?>reflectUpToClass){
		StringBuilder buffer = new StringBuilder();
		this.toString(style, buffer, outputTransients, outputStatics, reflectUpToClass);
		return buffer.toString();
	}
	public void toString(ToStringStyle style,StringBuilder buffer,boolean outputTransients,boolean outputStatics){
		this.toString(style, buffer, outputTransients, outputStatics, null);
	}
	public void toString(ToStringStyle style,StringBuilder buffer,boolean outputTransients,boolean outputStatics,Class<?>reflectUpToClass){
		StringBuffer bf = new StringBuffer();
		ReflectionToStringBuilder builder = new ReflectionToStringBuilder(this, style, bf, reflectUpToClass, outputTransients, outputStatics);
		builder.toString();
		buffer.append(bf.toString());
	}
	
	
	// Old Method
	public String toString_oldMethod(){
		return toStringEngine(false, null);
	}
	public String toString(boolean reportHTML){
		return toStringEngine(reportHTML,null);
	}
	public String toString(List<String> fieldsNotIncluded){
		return toStringEngine(false,fieldsNotIncluded);
	}
	public String toString(boolean reportHTML,boolean includeLongId){
		List<String> fieldsNotIncluded = new ArrayList<>();
		if(!includeLongId){
			fieldsNotIncluded.add("id");
		}
		return toStringEngine(reportHTML,fieldsNotIncluded);
	}
	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
		return toStringEngine(reportHTML,fieldsNotIncluded);
	}
	private String toStringEngine(boolean reportHTML,List<String> fieldsNotIncluded){	
		try{
			StringBuilder bf = new StringBuilder();
			java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
			for(int i=0; i<fields.length;i++){
			
				/**System.out.println("["+fields[i].getName()+"]");*/
				if(java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())){
					/**System.out.println("IS STATIC");*/
					continue;
				}
				
				if(fields[i].getType().isAssignableFrom(org.openspcoop2.utils.jaxb.DecimalWrapper.class)){
					continue;
				}
				
				// field id non controllato se in fieldsNotCheckList
				boolean ignoreField = false;
				if(fieldsNotIncluded!=null && !fieldsNotIncluded.isEmpty()){
					for (String fieldName : fieldsNotIncluded) {
						if(fieldName.equals(fields[i].getName())){
							ignoreField = true;
							break;
						}
					}
				}
				if(ignoreField){
					continue;
				}

				bf.append("---------- ");
				bf.append(this.getClass().getName());
				bf.append(".");
				bf.append(fields[i].getName());
				bf.append(" ----------");
				if(reportHTML) bf.append("<br>"); else bf.append("\n");
				
				Object fieldValue = this.getFieldValue(fields[i].getName(),this);
				/**System.out.println("LETTO VALORE["+fieldValue+"]");*/
				
				if(fieldValue==null){
					bf.append("null");
				}else{
					if(fields[i].getType().isAssignableFrom(java.util.ArrayList.class) || fields[i].getType().isAssignableFrom(java.util.List.class)){
						java.util.List<?> listaThis = (java.util.List<?>) fieldValue;
						bf.append("List size("+listaThis.size()+")");
						if(!listaThis.isEmpty()){
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
						java.util.List<String> sortLISTKEY = new java.util.ArrayList<>();
						for(int j=0; j<listaThis.size(); j++){
							String key = null;
							if(listaThis.get(j).getClass().getName().startsWith(this.getClass().getPackage().getName())){
								Class<?> c =  listaThis.get(j).getClass();
								java.lang.reflect.Method method =  c.getMethod(TO_STRING_METHOD,boolean.class,List.class);
								key = (String) method.invoke(listaThis.get(j),reportHTML,fieldsNotIncluded);
							}else{
								key =  listaThis.get(j).toString();
							}
							sortLISTKEY.add(key);
						}
						java.util.Collections.sort(sortLISTKEY);
						for(int j=0; j<sortLISTKEY.size(); j++){
							String key = sortLISTKEY.get(j);
							bf.append("---------- ");
							bf.append(this.getClass().getName());
							bf.append(".");
							bf.append(fields[i].getName());
							bf.append("[");
							bf.append(j);
							bf.append("] ----------");
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
							bf.append(key);
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
							bf.append("----end--- ");
							bf.append(this.getClass().getName());
							bf.append(".");
							bf.append(fields[i].getName());
							bf.append("[");
							bf.append(j);
							bf.append("] ----end---");
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
					}else{
						if(fieldValue.getClass().getName().startsWith(this.getClass().getPackage().getName())){
							Class<?> c =  fieldValue.getClass();
							java.lang.reflect.Method method =  c.getMethod(TO_STRING_METHOD,boolean.class,List.class);
							bf.append(method.invoke(fieldValue,reportHTML,fieldsNotIncluded));
						}else{
							if(fields[i].getType().isAssignableFrom(byte[].class)){
								byte[] array = (byte[])fieldValue;
								for(int k=0; k<array.length;k++){
									bf.append(((char)array[k]));
								}
							}else if(fields[i].getType().isAssignableFrom(java.util.Date.class)){
								java.util.Date date = (java.util.Date) fieldValue;
								bf.append(DateUtils.getSimpleDateFormatMs().format(date));
							}else{
								bf.append(fieldValue.toString());
							}
						}
					}
				}
				if(reportHTML) bf.append("<br>"); else bf.append("\n");
				bf.append("---end---- ");
				bf.append(this.getClass().getName());
				bf.append(".");
				bf.append(fields[i].getName());
				bf.append(" ---end----");
				if(reportHTML) bf.append("<br>"); else bf.append("\n");
			}
			return bf.toString();
		}catch(Exception e){
			logExceptionStackTrace(e);
			throw new UtilsRuntimeException(e.toString(),e);
		}
	}

	
	
	
	
	
	/* ********** DIFF ********* */
	
	public String diff(Object object,StringBuilder bf){
		return diffEngine(object,bf,false, null);
	}
	public String diff(Object object,StringBuilder bf,boolean reportHTML){
		return diffEngine(object,bf,reportHTML,null);
	}
	public String diff(Object object,StringBuilder bf,List<String> fieldsNotIncluded){
		return diffEngine(object,bf,false,fieldsNotIncluded);
	}
	public String diff(Object object,StringBuilder bf,boolean reportHTML,boolean includeLongId){
		List<String> fieldsNotIncluded = new ArrayList<>();
		if(!includeLongId){
			fieldsNotIncluded.add("id");
		}
		return diffEngine(object,bf,reportHTML,fieldsNotIncluded);
	}
	public String diff(Object object,StringBuilder bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return diffEngine(object,bf,reportHTML,fieldsNotIncluded);
	}
	@SuppressWarnings("unchecked")
	private String diffEngine(Object object,StringBuilder bf,boolean reportHTML,List<String> fieldsNotIncluded){	
	
		try{
			if(object==null){
				bf.append(this.getClass().getName());
				bf.append("this is not null, parameter is null");
				return bf.toString();
			}
			java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
			for(int i=0; i<fields.length;i++){
				
				/**System.out.println("["+fields[i].getName()+"]");*/
				if(java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())){
					/**System.out.println("IS STATIC");*/
					continue;
				}
				
				if(fields[i].getType().isAssignableFrom(org.openspcoop2.utils.jaxb.DecimalWrapper.class)){
					continue;
				}
				
				Object fieldValueThis = this.getFieldValue(fields[i].getName(),this);
				Object fieldValueObject = this.getFieldValue(fields[i].getName(),object);
				
				if(fields[i].getType().isAssignableFrom(java.util.ArrayList.class) || fields[i].getType().isAssignableFrom(java.util.List.class)){
					// LISTA
					java.util.List<?> listaThis = (java.util.List<?>) fieldValueThis;
					java.util.List<?> listaParameter = (java.util.List<?>) fieldValueObject;
					if(listaThis==null){
						// this_list is null, parameter list is not null
						if(listaParameter!=null){
							bf.append(this.getClass().getName());
							bf.append(".");
							bf.append(fields[i].getName());
							bf.append(" this_list:is null, parameter_list: is not null");
							bf.append(" ,parameter_list_size:"+listaParameter.size());
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
					}else{
						// this_list is not null, parameter list is null
						if(listaParameter==null){
							bf.append(this.getClass().getName());
							bf.append(".");
							bf.append(fields[i].getName());
							bf.append(" this_list: is not null, parameter_list: is null");
							bf.append(" ,this_list_size:"+listaThis.size());
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
						// this_list different size from parameter_list
						else if(listaThis.size() != listaParameter.size()){
							bf.append(this.getClass().getName());
							bf.append(".");
							bf.append(fields[i].getName());
							bf.append(" this_list_size:"+listaThis.size()+", parameter_list_size:"+listaParameter.size());
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
						// Controllo elementi della lista
						else{
							// SORT
							java.util.List<Object> listaThisSORT = new java.util.ArrayList<>();
							java.util.List<Object> listaParameterSORT = new java.util.ArrayList<>();
							boolean listaNonCompleta = false;
							try{
								java.util.List<String> listaThisTMP = new java.util.ArrayList<>();
								java.util.Map<String, Object> thisTmp = new java.util.HashMap<>();
								java.util.Map<String, Object> parameterTmp = new java.util.HashMap<>();
								for(int k=0; k<listaThis.size(); k++){
									Object thisObject = listaThis.get(k);
									Object paramObject = listaParameter.get(k);
									if(thisObject==null && paramObject!=null)
										throw new UtilsException("DIFF");
									if(thisObject!=null && paramObject==null)
										throw new UtilsException("DIFF");
									if(thisObject!=null && paramObject!=null){
										// THIS
										String key = null;
										if(thisObject.getClass().getName().startsWith(this.getClass().getPackage().getName())){
											Class<?> c =  thisObject.getClass();
											java.lang.reflect.Method method =  c.getMethod(TO_STRING_METHOD,boolean.class,boolean.class);
											key = (String) method.invoke(thisObject,false,true);
										}else{
											key = thisObject.toString();
										}
										thisTmp.put(key, thisObject);
										listaThisTMP.add(key);

										// PARAM
										if(paramObject.getClass().getName().startsWith(this.getClass().getPackage().getName())){
											Class<?> c =  paramObject.getClass();
											java.lang.reflect.Method method =  c.getMethod(TO_STRING_METHOD,boolean.class,boolean.class);
											key = (String) method.invoke(paramObject,false,true);
										}else{
											key = paramObject.toString();
										}
										parameterTmp.put(key, paramObject);
									}
								}
								java.util.Collections.sort(listaThisTMP);
								for(int k=0; k<listaThisTMP.size(); k++){
									String key = listaThisTMP.get(k);
									Object thisObject = thisTmp.get(key);
									Object paramObject = parameterTmp.get(key);
									if(thisObject==null){
										// significa che manca un elemento
										listaThisSORT = (java.util.List<Object>) listaThis;
										listaParameterSORT = (java.util.List<Object>) listaParameter;
										bf.append("["+this.getClass().getName());
										bf.append(".");
										bf.append(fields[i].getName()+"] ");
										bf.append("Match non trovato in this(size:"+thisTmp.size()+") per l'elemento: "+key);
										if(!thisTmp.isEmpty()) {
											int j=0;
											for (String keyJ : thisTmp.keySet()) {
												bf.append("ELEMENTO_THIS_["+j+"]=["+keyJ+"]");
												j++;
											}
										}
										listaNonCompleta = true;
										break;
									}
									if(paramObject==null){
										// significa che manca un elemento
										listaThisSORT = (java.util.List<Object>) listaThis;
										listaParameterSORT = (java.util.List<Object>) listaParameter;
										bf.append("["+this.getClass().getName());
										bf.append(".");
										bf.append(fields[i].getName()+"] ");
										bf.append("Match non trovato in param(size:"+parameterTmp.size()+") per l'elemento: "+key);
										if(!parameterTmp.isEmpty()) {
											int j=0;
											for (String keyJ : parameterTmp.keySet()) {
												bf.append("ELEMENTO_PARAM_["+j+"]=["+keyJ+"]");
												j++;
											}
										}
										listaNonCompleta = true;
										break;
									}
									listaThisSORT.add(thisObject);
									listaParameterSORT.add(paramObject);
								}
							}
							catch(RuntimeException e){
								// By SpotBugs
								listaParameterSORT = (java.util.List<Object>) listaParameter;
								listaThisSORT = (java.util.List<Object>) listaThis;
							}
							catch(Exception e){
								listaThisSORT = (java.util.List<Object>) listaThis;
								listaParameterSORT = (java.util.List<Object>) listaParameter;
							}
							if(!listaNonCompleta){
								for(int j=0; j<listaThisSORT.size(); j++){
									if(listaThisSORT.get(j)==null){
										bf.append(this.getClass().getName());
										bf.append(".");
										bf.append(fields[i].getName()+"["+j+"]");
										bf.append(" this_list: is null, parameter: is not null");
										bf.append(" parameter_value:");
										if(reportHTML) bf.append("<br>"); else bf.append("\n");
										if(listaParameterSORT.get(j).getClass().getName().startsWith(this.getClass().getPackage().getName())){
											Class<?> c =  listaParameterSORT.get(j).getClass();
											java.lang.reflect.Method method =  c.getMethod(TO_STRING_METHOD,boolean.class);
											bf.append(method.invoke(listaParameterSORT.get(j),reportHTML));
										}else{
											bf.append(listaParameterSORT.get(j).toString());
										}
										if(reportHTML) bf.append("<br>"); else bf.append("\n");
									}else{
										if(listaThisSORT.get(j).getClass().getName().startsWith(this.getClass().getPackage().getName())){
											Class<?> c =  listaThisSORT.get(j).getClass();
											java.lang.reflect.Method method =  c.getMethod("diff",Object.class,bf.getClass(),boolean.class,List.class);
											method.invoke(listaThisSORT.get(j),listaParameterSORT.get(j),bf,reportHTML,fieldsNotIncluded);
										}else{
											boolean checkUguaglianza = false;
											if(listaThisSORT.get(j).getClass().isAssignableFrom(byte[].class)){
												if(listaParameterSORT.get(j)!=null){
													byte[] origi = (byte[]) listaThisSORT.get(j);
													byte[] dest = (byte[]) listaParameterSORT.get(j);
													checkUguaglianza = (origi.length == dest.length);
													if(checkUguaglianza){
														for(int k=0;k<origi.length;k++){
															if(origi[k]!=dest[k]){
																checkUguaglianza = false;
																break;
															}
														}
													}
												}
											}else{
												checkUguaglianza = listaThisSORT.get(j).equals(listaParameterSORT.get(j));
											}
											if(!checkUguaglianza){
												bf.append(this.getClass().getName());
												bf.append(".");
												bf.append(fields[i].getName()+"["+j+"]");
												
												bf.append(" this:");
												if(listaThisSORT.get(j).getClass().isAssignableFrom(byte[].class)){
													if(listaThisSORT.get(j)!=null){
														byte[] array = (byte[])listaThisSORT.get(j);
														for(int k=0; k<array.length;k++)
															bf.append(((char)array[k]));
													}
													else
														bf.append("null");
												}else if(listaThisSORT.get(j).getClass().isAssignableFrom(java.util.Date.class)){
													if(listaThisSORT.get(j)!=null){
														java.util.Date date = (java.util.Date) listaThisSORT.get(j);
														bf.append(DateUtils.getSimpleDateFormatMs().format(date));
													}
													else
														bf.append("null");
												}else{
													bf.append(listaThisSORT.get(j));
												}
												
												bf.append(" parameter:");
												if(listaParameterSORT.get(j).getClass().isAssignableFrom(byte[].class)){
													if(listaParameterSORT.get(j)!=null){
														byte[] array = (byte[])listaParameterSORT.get(j);
														for(int k=0; k<array.length;k++)
															bf.append(((char)array[k]));
													}
													else
														bf.append("null");
												}else if(listaParameterSORT.get(j).getClass().isAssignableFrom(java.util.Date.class)){
													if(listaParameterSORT.get(j)!=null){
														java.util.Date date = (java.util.Date) listaParameterSORT.get(j);
														bf.append(DateUtils.getSimpleDateFormatMs().format(date));
													}
													else
														bf.append("null");
												}else{
													bf.append(listaParameterSORT.get(j));
												}
												if(reportHTML) bf.append("<br>"); else bf.append("\n");
											}
										}
									}
								}
							}
						}
					}
				}
				else{
					// field id non controllato se in fieldsNotCheckList
					boolean ignoreField = false;
					if(fieldsNotIncluded!=null && !fieldsNotIncluded.isEmpty()){
						for (String fieldName : fieldsNotIncluded) {
							if(fieldName.equals(fields[i].getName())){
								ignoreField = true;
								break;
							}
						}
					}
					if(ignoreField){
						continue;
					}

					// ELEMENTO SINGOLO
					if(fieldValueThis==null){
						// this is null, parameter is not null
						if(fieldValueObject!=null){
							bf.append(this.getClass().getName());
							bf.append(".");
							bf.append(fields[i].getName());
							bf.append(" this:is null, parameter: is not null");
							bf.append(" parameter_value:");
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
							if(fieldValueObject.getClass().getName().startsWith(this.getClass().getPackage().getName())){
								Class<?> c =  fieldValueObject.getClass();
								java.lang.reflect.Method method =  c.getMethod(TO_STRING_METHOD,boolean.class);
								bf.append(method.invoke(fieldValueObject,reportHTML));
							}else{
								if(fields[i].getType().isAssignableFrom(byte[].class)){
									byte[] array = (byte[])fieldValueObject;
									for(int k=0; k<array.length;k++)
										bf.append(array[k]);
								}else if(fields[i].getType().isAssignableFrom(java.util.Date.class)){
									java.util.Date date = (java.util.Date) fieldValueObject;
									bf.append(DateUtils.getSimpleDateFormatMs().format(date));
								}else{
									bf.append(fieldValueObject);
								}
							}
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
					}else{
						if(fields[i].getType().getName().startsWith(this.getClass().getPackage().getName())){
							java.lang.reflect.Method method =  fields[i].getType().getMethod("diff",Object.class,bf.getClass(),boolean.class,List.class);
							method.invoke(fieldValueThis,fieldValueObject,bf,reportHTML,fieldsNotIncluded);
						}else{
							boolean checkUguaglianza = false;
							if(fields[i].getType().isAssignableFrom(byte[].class)){
								if(fieldValueObject!=null){
									byte[] origi = (byte[]) fieldValueThis;
									byte[] dest = (byte[]) fieldValueObject;
									checkUguaglianza = (origi.length == dest.length);
									if(checkUguaglianza){
										for(int k=0;k<origi.length;k++){
											if(origi[k]!=dest[k]){
												checkUguaglianza = false;
												break;
											}
										}
									}
								}
							}else{
								if(fields[i].getType().isAssignableFrom(java.util.Date.class)){
									if(fieldValueObject!=null){
										java.util.Calendar calendarThis = new java.util.GregorianCalendar();
										calendarThis.setTime((java.util.Date)fieldValueThis);
										java.util.Calendar calendarObject = new java.util.GregorianCalendar();
										calendarObject.setTime((java.util.Date)fieldValueObject);
										checkUguaglianza = (  calendarThis.get(java.util.Calendar.YEAR) == calendarObject.get(java.util.Calendar.YEAR) ) && 
												( calendarThis.get(java.util.Calendar.MONTH) == calendarObject.get(java.util.Calendar.MONTH) ) && 
												( calendarThis.get(java.util.Calendar.DAY_OF_MONTH) == calendarObject.get(java.util.Calendar.DAY_OF_MONTH) ) && 
												( calendarThis.get(java.util.Calendar.HOUR_OF_DAY) == calendarObject.get(java.util.Calendar.HOUR_OF_DAY) ) && 
												( calendarThis.get(java.util.Calendar.MINUTE) == calendarObject.get(java.util.Calendar.MINUTE) ) && 
												( calendarThis.get(java.util.Calendar.SECOND) == calendarObject.get(java.util.Calendar.SECOND) ) && 
												( calendarThis.get(java.util.Calendar.MILLISECOND) == calendarObject.get(java.util.Calendar.MILLISECOND) );
									}
								}else{
									checkUguaglianza = fieldValueThis.equals(fieldValueObject);
								}
							}
							if(!checkUguaglianza){
								bf.append(this.getClass().getName());
								bf.append(".");
								bf.append(fields[i].getName());
								
								bf.append(" this:");
								if(fields[i].getType().isAssignableFrom(byte[].class)){
									byte[] array = (byte[])fieldValueThis;
									for(int k=0; k<array.length;k++)
										bf.append(((char)array[k]));
								}else if(fields[i].getType().isAssignableFrom(java.util.Date.class)){
									java.util.Date date = (java.util.Date) fieldValueThis;
									bf.append(DateUtils.getSimpleDateFormatMs().format(date));
								}else{
									bf.append(fieldValueThis);
								}
								
								bf.append(" parameter:");
								if(fields[i].getType().isAssignableFrom(byte[].class)){
									if(fieldValueObject!=null){
										byte[] array = (byte[])fieldValueObject;
										for(int k=0; k<array.length;k++)
											bf.append(((char)array[k]));
									}
									else
										bf.append("null");
								}else if(fields[i].getType().isAssignableFrom(java.util.Date.class)){
									if(fieldValueObject!=null){
										java.util.Date date = (java.util.Date) fieldValueObject;
										bf.append(DateUtils.getSimpleDateFormatMs().format(date));
									}else
										bf.append("null");
								}else{
									bf.append(fieldValueObject);
								}
								if(reportHTML) bf.append("<br>"); else bf.append("\n");
							}
						}
					}
				}
			}
			return bf.toString();
		}catch(Exception e){
			logExceptionStackTrace(e);
			throw new UtilsRuntimeException(e.toString(),e);
		}
	}

	
	
	
	
	
	
	
	
	/* ********** CLONE ********* */
	
	@Override
	public Object clone()  {
		Object clone = null;
		try{
			//clono l'oggetto
			clone = super.clone();
			//clone = this.getClass().newInstance(); NON FUNZIONA NON VENGONO COPIATI I VARI FIELDS
			/*
			 * Per ogni field se il field presente nella classe implementa
			 * l'interfaccia cloneable allora eseguo il metodo clone di quel field
			 */
			java.lang.reflect.Field[] fields = this.getClass().getDeclaredFields();
			if(fields!=null && fields.length>0){
				for (int i = 0; i < fields.length; i++) {
					
					/**System.out.println("["+fields[i].getName()+"]");*/
					if(java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())){
						/**System.out.println("IS STATIC");*/
						continue;
					}
					
					if(fields[i].getType().isAssignableFrom(org.openspcoop2.utils.jaxb.DecimalWrapper.class)){
						continue;
					}
					
					
					java.lang.reflect.Field field = fields[i];
					Object fieldValue = this.getFieldValue(field.getName(), this);
					if(fieldValue==null){
						continue;
					}
					
					/**System.out.println("ESAMINO FIELD ["+field.getName()+"] type["+field.getType().getName()+"] isEnum["+(field.getType().isEnum())+"]");*/
					
					if(field.getType().isAssignableFrom(byte[].class)){
						//caso particolare arrya di byte
						byte[] originale = (byte[]) fieldValue;
						if(originale!=null){
							byte[] arrayClone = new byte[originale.length];
							for (int j = 0; j < arrayClone.length; j++) {
								arrayClone[j]=originale[j];
							}
							this.setFieldValue(field.getName(), clone, field.getType(), arrayClone);
						}
					}
					else if(field.getType().isEnum()){
						// Imposto nel nuovo oggetto il valore dell'enum
						//setto il field clonato nel clone
						this.setFieldValue(field.getName(), clone, field.getType(), fieldValue);
					}
					else if(isSupportedCloneList(field)){
						List<?> listOriginale = (List<?>) fieldValue;
						if(listOriginale!=null){
							List<?> listClone  = this.cloneList(listOriginale);
							//setto il field clonato nel clone
							this.setFieldValue(field.getName(), clone, field.getType(), listClone);
						}
					}
					else {
						//recupero interfacce implementate da questo field
						Class<?>[] interfacce = field.getType().getInterfaces();
						for (int j = 0; j < interfacce.length; j++) {
							//se il field che sto controllando implementa l'interfaccia cloneable e il field che voglio clonare non e' null
							//richiamo il clone
							if(interfacce[j].isAssignableFrom(java.lang.Cloneable.class) && fieldValue!=null ){
								//recupero il metodo clone dal field
								java.lang.reflect.Method method =  field.getType().getMethod("clone");
								//effettuo il clone del field
								java.lang.Cloneable ris =  (java.lang.Cloneable) method.invoke(fieldValue);
								//setto il field clonato nel clone
								this.setFieldValue(field.getName(), clone, field.getType(), ris);
							}
						}
					}
				}
			}

		}catch (Exception e) {
			logExceptionStackTrace(e);
			throw new UtilsRuntimeException(e.toString(),e);
		}
		return clone;
	}

	private boolean isSupportedCloneList(java.lang.reflect.Field field){
		return field.getType().isAssignableFrom(java.util.List.class) ||
				field.getType().isAssignableFrom(java.util.ArrayList.class)  ||
				field.getType().isAssignableFrom(java.util.Vector.class) ||
				field.getType().isAssignableFrom(java.util.Stack.class) ||
				field.getType().isAssignableFrom(java.util.LinkedList.class) ||
				field.getType().isAssignableFrom(java.util.concurrent.CopyOnWriteArrayList.class);
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List cloneList(List<?> listOriginale) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List listClone  = null;
		if(listOriginale.getClass().isAssignableFrom(java.util.ArrayList.class)){
			listClone = new java.util.ArrayList<>();
		}
		else if(listOriginale.getClass().isAssignableFrom(java.util.Vector.class)){
			listClone = new java.util.Vector<>();
		}
		else if(listOriginale.getClass().isAssignableFrom(java.util.Stack.class)){
			listClone = new java.util.Stack<>();
		}
		else if(listOriginale.getClass().isAssignableFrom(java.util.LinkedList.class)){
			listClone = new java.util.LinkedList<>();
		}
		else if(listOriginale.getClass().isAssignableFrom(java.util.concurrent.CopyOnWriteArrayList.class)){
			listClone = new java.util.concurrent.CopyOnWriteArrayList<>();
		}
		if(listClone!=null){
			for (int j = 0; j < listOriginale.size(); j++) {
				Object oOriginale = listOriginale.get(j);
				if(oOriginale!=null){
					if(oOriginale instanceof java.lang.Cloneable){
						//recupero il metodo clone dal field
						java.lang.reflect.Method method =  oOriginale.getClass().getMethod("clone");
						//effettuo il clone del field
						java.lang.Cloneable oCloned =  (java.lang.Cloneable) method.invoke(oOriginale);
						//aggiungo il field clonato nella lista
						listClone.add(oCloned);
					}
					else if(oOriginale instanceof String) {
						String sOriginale = (String) oOriginale;
						String sCloned = new String(sOriginale.toCharArray());
						//aggiungo il field clonato nella lista
						listClone.add(sCloned);
					}
				}
				else{
					listClone.add(null);
				}
			}
		}
		return listClone;
	}
}

