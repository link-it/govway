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
package org.openspcoop2.utils.beans;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.serialization.Filter;
import org.openspcoop2.utils.serialization.JSonSerializer;
import org.openspcoop2.utils.serialization.JavaSerializer;
import org.openspcoop2.utils.serialization.XMLSerializer;
import org.openspcoop2.utils.xml.JaxbUtils;
import org.openspcoop2.utils.xml.JiBXUtils;

/**
 * BaseBean
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
@XmlTransient
public abstract class BaseBean {

	
	private static final java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
	
	
	
	
	/* ********** GENERIC UTILS ********* */
	
	private Object getFieldValue(String fieldName,Object object) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Class<?> c = getClass();
		
		StringBuffer methodName = new StringBuffer("get");
		String firstChar = fieldName.charAt(0)+"";
		methodName.append(firstChar.toUpperCase());
		if(fieldName.length()>1){
			methodName.append(fieldName.substring(1));
		}
		
		Method method = c.getMethod(methodName.toString());
		return method.invoke(object);
	}
	
	private Object setFieldValue(String fieldName,Object object,Class<?> parameterType,Object parameterValue) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Class<?> c = getClass();
		
		StringBuffer methodName = new StringBuffer("set");
		String firstChar = fieldName.charAt(0)+"";
		methodName.append(firstChar.toUpperCase());
		if(fieldName.length()>1){
			methodName.append(fieldName.substring(1));
		}
		
		Method method = c.getMethod(methodName.toString(),parameterType);
		return method.invoke(object,parameterValue);
	}
	
	
	
	

	
	/* ********** EQUALS ********* */
	
	@Override
	public boolean equals(Object object){
		return this._equalsEngine(object,null);
	}
	public boolean equals(Object object,boolean checkLongId){
		List<String> listFieldsNotCheck = new ArrayList<String>();
		if(checkLongId==false){
			listFieldsNotCheck.add("id");
		}
		return this._equalsEngine(object, listFieldsNotCheck);
	}
	public boolean equals(Object object,List<String> fieldsNotCheck){
		return this._equalsEngine(object, fieldsNotCheck);
	}
	@SuppressWarnings("unchecked")
	private boolean _equalsEngine(Object object,List<String> fieldsNotCheck){
		try{
			if(object==null){
				return false;
			}
			java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
			for(int i=0; i<fields.length;i++){
				
				//System.out.println("["+fields[i].getName()+"]");
				if(java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())){
					//System.out.println("IS STATIC");
					continue;
				}
				
				if(org.openspcoop2.utils.jaxb.DecimalWrapper.class.getName().equals(fields[i].getType().getName())){
					continue;
				}
				
				// field id non controllato se in fieldsNotCheckList
				boolean ignoreField = false;
				if(fieldsNotCheck!=null && fieldsNotCheck.size()>0){
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
				
				Object fieldValue_this = this.getFieldValue(fields[i].getName(),this);
				Object fieldValue_object = this.getFieldValue(fields[i].getName(),object);
				//System.out.println("LETTO VALORE["+fieldValue+"]");
				
				if(fieldValue_this==null){
					// FIELD
					if(fieldValue_object!=null){
						return false;
					}
				}else{
					// ARRAY_LIST
					if(fields[i].getType().getName().equals("java.util.ArrayList") || fields[i].getType().getName().equals("java.util.List") ){
						java.util.List<?> lista_this = (java.util.List<?>) fieldValue_this;
						java.util.List<?> lista_parameter = (java.util.List<?>) fieldValue_object;
						if(lista_parameter==null)
							return false;
						if(lista_this.size() != lista_parameter.size())
							return false;
						for(int j=0; j<lista_this.size(); j++){
							if(lista_this.get(j)==null){
								if(lista_parameter.get(j)!=null)
									return false;
							}
						}
						// SORT
						java.util.List<Object> lista_thisSORT = new java.util.ArrayList<Object>();
						java.util.List<Object> lista_parameterSORT = new java.util.ArrayList<Object>();
						try{
							java.util.List<String> lista_thisTMP = new java.util.ArrayList<String>();
							java.util.Hashtable<String, Object> thisTmp = new java.util.Hashtable<String, Object>();
							java.util.Hashtable<String, Object> parameterTmp = new java.util.Hashtable<String, Object>();
							for(int k=0; k<lista_this.size(); k++){
								Object thisObject = lista_this.get(k);
								Object paramObject = lista_parameter.get(k);
								if(thisObject==null && paramObject!=null)
									throw new Exception("DIFF");
								if(thisObject!=null && paramObject==null)
									throw new Exception("DIFF");
								if(thisObject!=null && paramObject!=null){
									// THIS
									String key = null;
									if(thisObject.getClass().getName().startsWith(this.getClass().getPackage().getName())){
										Class<?> c =  thisObject.getClass();
										java.lang.reflect.Method method =  c.getMethod("toString",boolean.class,boolean.class);
										key = (String) method.invoke(thisObject,false,true);
									}else{
										key = thisObject.toString();
									}
									thisTmp.put(key, thisObject);
									lista_thisTMP.add(key);

									// PARAM
									key = null;
									if(paramObject.getClass().getName().startsWith(this.getClass().getPackage().getName())){
										Class<?> c =  paramObject.getClass();
										java.lang.reflect.Method method =  c.getMethod("toString",boolean.class,boolean.class);
										key = (String) method.invoke(paramObject,false,true);
									}else{
										key = paramObject.toString();
									}
									parameterTmp.put(key, paramObject);
								}
							}
							java.util.Collections.sort(lista_thisTMP);
							for(int k=0; k<lista_thisTMP.size(); k++){
								String key = lista_thisTMP.get(k);
								Object thisObject = thisTmp.get(key);
								Object paramObject = parameterTmp.get(key);
								if(thisObject==null || paramObject==null){
									// significa che manca un elemento
									return false;
								}
								lista_thisSORT.add(thisObject);
								lista_parameterSORT.add(paramObject);
							}
						}catch(Exception e){
							lista_thisSORT = (java.util.List<Object>) lista_this;
							lista_parameterSORT = (java.util.List<Object>) lista_parameter;
						}
						for(int j=0; j<lista_thisSORT.size(); j++){
							Class<?> c =  lista_thisSORT.get(j).getClass();
							boolean resultMethod = false;
							if(lista_this.get(j).getClass().getName().startsWith(this.getClass().getPackage().getName())){
								java.lang.reflect.Method method =  c.getMethod("equals",Object.class,List.class);
								resultMethod = (Boolean) method.invoke(lista_thisSORT.get(j),lista_parameterSORT.get(j),fieldsNotCheck);
							}else{
								resultMethod = lista_thisSORT.get(j).equals(lista_parameterSORT.get(j));
							}
							if(resultMethod==false)
								return false;
						}
					}else{
						boolean resultMethod = false;
						if(fields[i].getType().getName().startsWith(this.getClass().getPackage().getName())){
							java.lang.reflect.Method method =  fields[i].getType().getMethod("equals",Object.class,List.class);
							resultMethod = (Boolean) method.invoke(fieldValue_this,fieldValue_object,fieldsNotCheck);
						}else{
							boolean checkUguaglianza = false;
							if("[B".equals(fields[i].getType().getName())){
								if(fieldValue_object!=null){
									byte[] origi = (byte[]) fieldValue_this;
									byte[] dest = (byte[]) fieldValue_object;
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
								if("java.util.Date".equals(fields[i].getType().getName())){
									java.util.Calendar calendarThis = new java.util.GregorianCalendar();
									calendarThis.setTime((java.util.Date)fieldValue_this);
									java.util.Calendar calendarObject = new java.util.GregorianCalendar();
									calendarObject.setTime((java.util.Date)fieldValue_object);
									checkUguaglianza = (  calendarThis.get(java.util.Calendar.YEAR) == calendarObject.get(java.util.Calendar.YEAR) ) && 
											( calendarThis.get(java.util.Calendar.MONTH) == calendarObject.get(java.util.Calendar.MONTH) ) && 
											( calendarThis.get(java.util.Calendar.DAY_OF_MONTH) == calendarObject.get(java.util.Calendar.DAY_OF_MONTH) ) && 
											( calendarThis.get(java.util.Calendar.HOUR_OF_DAY) == calendarObject.get(java.util.Calendar.HOUR_OF_DAY) ) && 
											( calendarThis.get(java.util.Calendar.MINUTE) == calendarObject.get(java.util.Calendar.MINUTE) ) && 
											( calendarThis.get(java.util.Calendar.SECOND) == calendarObject.get(java.util.Calendar.SECOND) ) && 
											( calendarThis.get(java.util.Calendar.MILLISECOND) == calendarObject.get(java.util.Calendar.MILLISECOND) ) ;
								}else{
									checkUguaglianza = fieldValue_this.equals(fieldValue_object);
								}
							}
							resultMethod = checkUguaglianza;
						}
						if(resultMethod==false)
							return false;
					}
				}
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.toString(),e);
		}
	}

	
	
	
	
	
	
	
	/* ********** WRITE TO ********* */
	
	public void writeTo(OutputStream out) throws UtilsException{
		this.writeTo(out, WriteToSerializerType.JIBX);
	}
	public void writeTo(OutputStream out,WriteToSerializerType type) throws UtilsException{
		try{
			switch (type) {
				case JIBX:
					JiBXUtils.objToXml(out, getClass(), this, true);
					break;
				case JAXB:
					JaxbUtils.objToXml(out, getClass(), this, true);
					break;
				case JSON:
					JSonSerializer jsonSerializer = new JSonSerializer(new Filter());
					jsonSerializer.writeObject(this, out);
					break;
				case XML_JSON:
					XMLSerializer xmlSerializer = new XMLSerializer(new Filter());
					xmlSerializer.writeObject(this, out);
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
		return this.serialize(WriteToSerializerType.JIBX);
	}
	public String toXml_Jibx() throws UtilsException{
		return this.serialize(WriteToSerializerType.JIBX);
	}
	public String toXml_Jaxb() throws UtilsException{
		return this.serialize(WriteToSerializerType.JAXB);
	}
	public String toJson() throws UtilsException{
		return this.serialize(WriteToSerializerType.JSON);
	}
	public String toXml_Json() throws UtilsException{
		return this.serialize(WriteToSerializerType.XML_JSON);
	}
	// Non è utile. Al massimo si usa sopra come writeTo
//	public String toJava() throws UtilsException{
//		return this.serialize(WriteToSerializerType.JAVA);
//	}
	public String serialize(WriteToSerializerType type) throws UtilsException{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		this.writeTo(bout, type);
		return bout.toString();
	}
	
	
	
	
	
	
	
	
	/* ********** TO STRING ********* */
	
	@Override
	public String toString(){
		return _toStringEngine(false, null);
	}
	public String toString(boolean reportHTML){
		return _toStringEngine(reportHTML,null);
	}
	public String toString(List<String> fieldsNotIncluded){
		return _toStringEngine(false,fieldsNotIncluded);
	}
	public String toString(boolean reportHTML,boolean includeLongId){
		List<String> fieldsNotIncluded = new ArrayList<String>();
		if(includeLongId==false){
			fieldsNotIncluded.add("id");
		}
		return _toStringEngine(reportHTML,fieldsNotIncluded);
	}
	public String toString(boolean reportHTML,List<String> fieldsNotIncluded){
		return _toStringEngine(reportHTML,fieldsNotIncluded);
	}
	private String _toStringEngine(boolean reportHTML,List<String> fieldsNotIncluded){	
		try{
			StringBuffer bf = new StringBuffer();
			java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
			for(int i=0; i<fields.length;i++){
			
				//System.out.println("["+fields[i].getName()+"]");
				if(java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())){
					//System.out.println("IS STATIC");
					continue;
				}
				
				if(org.openspcoop2.utils.jaxb.DecimalWrapper.class.getName().equals(fields[i].getType().getName())){
					continue;
				}
				
				// field id non controllato se in fieldsNotCheckList
				boolean ignoreField = false;
				if(fieldsNotIncluded!=null && fieldsNotIncluded.size()>0){
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
				//System.out.println("LETTO VALORE["+fieldValue+"]");
				
				if(fieldValue==null){
					bf.append("null");
				}else{
					if(fields[i].getType().getName().equals("java.util.ArrayList") || fields[i].getType().getName().equals("java.util.List")){
						java.util.List<?> lista_this = (java.util.List<?>) fieldValue;
						bf.append("List size("+lista_this.size()+")");
						if(lista_this.size()>0){
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
						java.util.List<String> sortLISTKEY = new java.util.ArrayList<String>();
						for(int j=0; j<lista_this.size(); j++){
							String key = null;
							if(lista_this.get(j).getClass().getName().startsWith(this.getClass().getPackage().getName())){
								Class<?> c =  lista_this.get(j).getClass();
								java.lang.reflect.Method method =  c.getMethod("toString",boolean.class,List.class);
								key = (String) method.invoke(lista_this.get(j),reportHTML,fieldsNotIncluded);
							}else{
								key =  lista_this.get(j).toString();
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
							java.lang.reflect.Method method =  c.getMethod("toString",boolean.class,List.class);
							bf.append(method.invoke(fieldValue,reportHTML,fieldsNotIncluded));
						}else{
							if("[B".equals(fields[i].getType().getName())){
								byte[] array = (byte[])fieldValue;
								for(int k=0; k<array.length;k++){
									bf.append(((char)array[k]));
								}
							}else if("java.util.Date".equals(fields[i].getType().getName())){
								java.util.Date date = (java.util.Date) fieldValue;
								bf.append(DATE_FORMAT.format(date));
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
			e.printStackTrace();
			throw new RuntimeException(e.toString(),e);
		}
	}

	
	
	
	
	
	/* ********** DIFF ********* */
	
	public String diff(Object object,StringBuffer bf){
		return _diffEngine(object,bf,false, null);
	}
	public String diff(Object object,StringBuffer bf,boolean reportHTML){
		return _diffEngine(object,bf,reportHTML,null);
	}
	public String diff(Object object,StringBuffer bf,List<String> fieldsNotIncluded){
		return _diffEngine(object,bf,false,fieldsNotIncluded);
	}
	public String diff(Object object,StringBuffer bf,boolean reportHTML,boolean includeLongId){
		List<String> fieldsNotIncluded = new ArrayList<String>();
		if(includeLongId==false){
			fieldsNotIncluded.add("id");
		}
		return _diffEngine(object,bf,reportHTML,fieldsNotIncluded);
	}
	public String diff(Object object,StringBuffer bf,boolean reportHTML,List<String> fieldsNotIncluded){
		return _diffEngine(object,bf,reportHTML,fieldsNotIncluded);
	}
	@SuppressWarnings("unchecked")
	private String _diffEngine(Object object,StringBuffer bf,boolean reportHTML,List<String> fieldsNotIncluded){	
	
		try{
			if(object==null){
				bf.append(this.getClass().getName());
				bf.append("this is not null, parameter is null");
				return bf.toString();
			}
			java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
			for(int i=0; i<fields.length;i++){
				
				//System.out.println("["+fields[i].getName()+"]");
				if(java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())){
					//System.out.println("IS STATIC");
					continue;
				}
				
				if(org.openspcoop2.utils.jaxb.DecimalWrapper.class.getName().equals(fields[i].getType().getName())){
					continue;
				}
				
				Object fieldValue_this = this.getFieldValue(fields[i].getName(),this);
				Object fieldValue_object = this.getFieldValue(fields[i].getName(),object);
				
				if(fields[i].getType().getName().equals("java.util.ArrayList") || fields[i].getType().getName().equals("java.util.List")){
					// LISTA
					java.util.List<?> lista_this = (java.util.List<?>) fieldValue_this;
					java.util.List<?> lista_parameter = (java.util.List<?>) fieldValue_object;
					if(lista_this==null){
						// this_list is null, parameter list is not null
						if(lista_parameter!=null){
							bf.append(this.getClass().getName());
							bf.append(".");
							bf.append(fields[i].getName());
							bf.append(" this_list:is null, parameter_list: is not null");
							bf.append(" ,parameter_list_size:"+lista_parameter.size());
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
					}else{
						// this_list is not null, parameter list is null
						if(lista_parameter==null){
							bf.append(this.getClass().getName());
							bf.append(".");
							bf.append(fields[i].getName());
							bf.append(" this_list: is not null, parameter_list: is null");
							bf.append(" ,this_list_size:"+lista_this.size());
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
						// this_list different size from parameter_list
						else if(lista_this.size() != lista_parameter.size()){
							bf.append(this.getClass().getName());
							bf.append(".");
							bf.append(fields[i].getName());
							bf.append(" this_list_size:"+lista_this.size()+", parameter_list_size:"+lista_parameter.size());
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
						// Controllo elementi della lista
						else{
							// SORT
							java.util.List<Object> lista_thisSORT = new java.util.ArrayList<Object>();
							java.util.List<Object> lista_parameterSORT = new java.util.ArrayList<Object>();
							boolean listaNonCompleta = false;
							try{
								java.util.List<String> lista_thisTMP = new java.util.ArrayList<String>();
								java.util.Hashtable<String, Object> thisTmp = new java.util.Hashtable<String, Object>();
								java.util.Hashtable<String, Object> parameterTmp = new java.util.Hashtable<String, Object>();
								for(int k=0; k<lista_this.size(); k++){
									Object thisObject = lista_this.get(k);
									Object paramObject = lista_parameter.get(k);
									if(thisObject==null && paramObject!=null)
										throw new Exception("DIFF");
									if(thisObject!=null && paramObject==null)
										throw new Exception("DIFF");
									if(thisObject!=null && paramObject!=null){
										// THIS
										String key = null;
										if(thisObject.getClass().getName().startsWith(this.getClass().getPackage().getName())){
											Class<?> c =  thisObject.getClass();
											java.lang.reflect.Method method =  c.getMethod("toString",boolean.class,boolean.class);
											key = (String) method.invoke(thisObject,false,true);
										}else{
											key = thisObject.toString();
										}
										thisTmp.put(key, thisObject);
										lista_thisTMP.add(key);

										// PARAM
										key = null;
										if(paramObject.getClass().getName().startsWith(this.getClass().getPackage().getName())){
											Class<?> c =  paramObject.getClass();
											java.lang.reflect.Method method =  c.getMethod("toString",boolean.class,boolean.class);
											key = (String) method.invoke(paramObject,false,true);
										}else{
											key = paramObject.toString();
										}
										parameterTmp.put(key, paramObject);
									}
								}
								java.util.Collections.sort(lista_thisTMP);
								for(int k=0; k<lista_thisTMP.size(); k++){
									String key = lista_thisTMP.get(k);
									Object thisObject = thisTmp.get(key);
									Object paramObject = parameterTmp.get(key);
									if(thisObject==null){
										// significa che manca un elemento
										lista_thisSORT = (java.util.List<Object>) lista_this;
										lista_parameterSORT = (java.util.List<Object>) lista_parameter;
										bf.append("["+this.getClass().getName());
										bf.append(".");
										bf.append(fields[i].getName()+"] ");
										bf.append("Match non trovato in this(size:"+thisTmp.size()+") per l'elemento: "+key);
										java.util.Enumeration<String> keys =  thisTmp.keys();
										int j=0;
										while(keys.hasMoreElements()){
											String keyJ = keys.nextElement();
											bf.append("ELEMENTO_THIS_["+j+"]=["+keyJ+"]");
											j++;
										}
										listaNonCompleta = true;
										break;
									}
									if(paramObject==null){
										// significa che manca un elemento
										lista_thisSORT = (java.util.List<Object>) lista_this;
										lista_parameterSORT = (java.util.List<Object>) lista_parameter;
										bf.append("["+this.getClass().getName());
										bf.append(".");
										bf.append(fields[i].getName()+"] ");
										bf.append("Match non trovato in param(size:"+parameterTmp.size()+") per l'elemento: "+key);
										java.util.Enumeration<String> keys =  parameterTmp.keys();
										int j=0;
										while(keys.hasMoreElements()){
											String keyJ = keys.nextElement();
											bf.append("ELEMENTO_PARAM_["+j+"]=["+keyJ+"]");
											j++;
										}
										listaNonCompleta = true;
										break;
									}
									lista_thisSORT.add(thisObject);
									lista_parameterSORT.add(paramObject);
								}
							}catch(Exception e){
								lista_thisSORT = (java.util.List<Object>) lista_this;
								lista_parameterSORT = (java.util.List<Object>) lista_parameter;
							}
							if(listaNonCompleta==false){
								for(int j=0; j<lista_thisSORT.size(); j++){
									if(lista_thisSORT.get(j)==null){
										bf.append(this.getClass().getName());
										bf.append(".");
										bf.append(fields[i].getName()+"["+j+"]");
										bf.append(" this_list: is null, parameter: is not null");
										bf.append(" parameter_value:");
										if(reportHTML) bf.append("<br>"); else bf.append("\n");
										if(lista_parameterSORT.get(j).getClass().getName().startsWith(this.getClass().getPackage().getName())){
											Class<?> c =  lista_parameterSORT.get(j).getClass();
											java.lang.reflect.Method method =  c.getMethod("toString",boolean.class);
											bf.append(method.invoke(lista_parameterSORT.get(j),reportHTML));
										}else{
											bf.append(lista_parameterSORT.get(j).toString());
										}
										if(reportHTML) bf.append("<br>"); else bf.append("\n");
									}else{
										if(lista_thisSORT.get(j).getClass().getName().startsWith(this.getClass().getPackage().getName())){
											Class<?> c =  lista_thisSORT.get(j).getClass();
											java.lang.reflect.Method method =  c.getMethod("diff",Object.class,bf.getClass(),boolean.class,List.class);
											method.invoke(lista_thisSORT.get(j),lista_parameterSORT.get(j),bf,reportHTML,fieldsNotIncluded);
										}else{
											boolean checkUguaglianza = false;
											if("[B".equals(lista_thisSORT.get(j).getClass().getName())){
												if(lista_parameterSORT.get(j)!=null){
													byte[] origi = (byte[]) lista_thisSORT.get(j);
													byte[] dest = (byte[]) lista_parameterSORT.get(j);
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
												checkUguaglianza = lista_thisSORT.get(j).equals(lista_parameterSORT.get(j));
											}
											if(checkUguaglianza==false){
												bf.append(this.getClass().getName());
												bf.append(".");
												bf.append(fields[i].getName()+"["+j+"]");
												
												bf.append(" this:");
												if("[B".equals(lista_thisSORT.get(j).getClass().getName())){
													if(lista_thisSORT.get(j)!=null){
														byte[] array = (byte[])lista_thisSORT.get(j);
														for(int k=0; k<array.length;k++)
															bf.append(((char)array[k]));
													}
													else
														bf.append("null");
												}else if("java.util.Date".equals(lista_thisSORT.get(j).getClass().getName())){
													if(lista_thisSORT.get(j)!=null){
														java.util.Date date = (java.util.Date) lista_thisSORT.get(j);
														bf.append(DATE_FORMAT.format(date));
													}
													else
														bf.append("null");
												}else{
													bf.append(lista_thisSORT.get(j));
												}
												
												bf.append(" parameter:");
												if("[B".equals(lista_parameterSORT.get(j).getClass().getName())){
													if(lista_parameterSORT.get(j)!=null){
														byte[] array = (byte[])lista_parameterSORT.get(j);
														for(int k=0; k<array.length;k++)
															bf.append(((char)array[k]));
													}
													else
														bf.append("null");
												}else if("java.util.Date".equals(lista_parameterSORT.get(j).getClass().getName())){
													if(lista_parameterSORT.get(j)!=null){
														java.util.Date date = (java.util.Date) lista_parameterSORT.get(j);
														bf.append(DATE_FORMAT.format(date));
													}
													else
														bf.append("null");
												}else{
													bf.append(lista_parameterSORT.get(j));
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
					if(fieldsNotIncluded!=null && fieldsNotIncluded.size()>0){
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
					if(fieldValue_this==null){
						// this is null, parameter is not null
						if(fieldValue_object!=null){
							bf.append(this.getClass().getName());
							bf.append(".");
							bf.append(fields[i].getName());
							bf.append(" this:is null, parameter: is not null");
							bf.append(" parameter_value:");
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
							if(fieldValue_object.getClass().getName().startsWith(this.getClass().getPackage().getName())){
								Class<?> c =  fieldValue_object.getClass();
								java.lang.reflect.Method method =  c.getMethod("toString",boolean.class);
								bf.append(method.invoke(fieldValue_object,reportHTML));
							}else{
								if("[B".equals(fields[i].getType().getName())){
									byte[] array = (byte[])fieldValue_object;
									for(int k=0; k<array.length;k++)
										bf.append(array[k]);
								}else if("java.util.Date".equals(fields[i].getType().getName())){
									java.util.Date date = (java.util.Date) fieldValue_object;
									bf.append(DATE_FORMAT.format(date));
								}else{
									bf.append(fieldValue_object);
								}
							}
							if(reportHTML) bf.append("<br>"); else bf.append("\n");
						}
					}else{
						if(fields[i].getType().getName().startsWith(this.getClass().getPackage().getName())){
							java.lang.reflect.Method method =  fields[i].getType().getMethod("diff",Object.class,bf.getClass(),boolean.class,List.class);
							method.invoke(fieldValue_this,fieldValue_object,bf,reportHTML,fieldsNotIncluded);
						}else{
							boolean checkUguaglianza = false;
							if("[B".equals(fields[i].getType().getName())){
								if(fieldValue_object!=null){
									byte[] origi = (byte[]) fieldValue_this;
									byte[] dest = (byte[]) fieldValue_object;
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
								if("java.util.Date".equals(fields[i].getType().getName())){
									if(fieldValue_object!=null){
										java.util.Calendar calendarThis = new java.util.GregorianCalendar();
										calendarThis.setTime((java.util.Date)fieldValue_this);
										java.util.Calendar calendarObject = new java.util.GregorianCalendar();
										calendarObject.setTime((java.util.Date)fieldValue_object);
										checkUguaglianza = (  calendarThis.get(java.util.Calendar.YEAR) == calendarObject.get(java.util.Calendar.YEAR) ) && 
												( calendarThis.get(java.util.Calendar.MONTH) == calendarObject.get(java.util.Calendar.MONTH) ) && 
												( calendarThis.get(java.util.Calendar.DAY_OF_MONTH) == calendarObject.get(java.util.Calendar.DAY_OF_MONTH) ) && 
												( calendarThis.get(java.util.Calendar.HOUR_OF_DAY) == calendarObject.get(java.util.Calendar.HOUR_OF_DAY) ) && 
												( calendarThis.get(java.util.Calendar.MINUTE) == calendarObject.get(java.util.Calendar.MINUTE) ) && 
												( calendarThis.get(java.util.Calendar.SECOND) == calendarObject.get(java.util.Calendar.SECOND) ) && 
												( calendarThis.get(java.util.Calendar.MILLISECOND) == calendarObject.get(java.util.Calendar.MILLISECOND) );
									}
								}else{
									checkUguaglianza = fieldValue_this.equals(fieldValue_object);
								}
							}
							if(checkUguaglianza==false){
								bf.append(this.getClass().getName());
								bf.append(".");
								bf.append(fields[i].getName());
								
								bf.append(" this:");
								if("[B".equals(fields[i].getType().getName())){
									byte[] array = (byte[])fieldValue_this;
									for(int k=0; k<array.length;k++)
										bf.append(((char)array[k]));
								}else if("java.util.Date".equals(fields[i].getType().getName())){
									java.util.Date date = (java.util.Date) fieldValue_this;
									bf.append(DATE_FORMAT.format(date));
								}else{
									bf.append(fieldValue_this);
								}
								
								bf.append(" parameter:");
								if("[B".equals(fields[i].getType().getName())){
									if(fieldValue_object!=null){
										byte[] array = (byte[])fieldValue_object;
										for(int k=0; k<array.length;k++)
											bf.append(((char)array[k]));
									}
									else
										bf.append("null");
								}else if("java.util.Date".equals(fields[i].getType().getName())){
									if(fieldValue_object!=null){
										java.util.Date date = (java.util.Date) fieldValue_object;
										bf.append(DATE_FORMAT.format(date));
									}else
										bf.append("null");
								}else{
									bf.append(fieldValue_object);
								}
								if(reportHTML) bf.append("<br>"); else bf.append("\n");
							}
						}
					}
				}
			}
			return bf.toString();
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e.toString(),e);
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
					
					//System.out.println("["+fields[i].getName()+"]");
					if(java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())){
						//System.out.println("IS STATIC");
						continue;
					}
					
					if(org.openspcoop2.utils.jaxb.DecimalWrapper.class.getName().equals(fields[i].getType().getName())){
						continue;
					}
					
					
					java.lang.reflect.Field field = fields[i];
					Object fieldValue = this.getFieldValue(field.getName(), this);
					if(fieldValue==null){
						continue;
					}
					
					//System.out.println("ESAMINO FIELD ["+field.getName()+"] type["+field.getType().getName()+"] isEnum["+(field.getType().isEnum())+"]");
					
					if("[B".equals(field.getType().getName())){
						//caso particolare arrya di byte
						byte[] originale = (byte[]) fieldValue;
						if(originale!=null){
							byte[] arrayClone = new byte[originale.length];
							for (int j = 0; j < arrayClone.length; j++) {
								arrayClone[j]=originale[j];
							}
							this.setFieldValue(field.getName(), clone, field.getType(), arrayClone);
						}
						continue;
					}
					else if(field.getType().isEnum()){
						// Imposto nel nuovo oggetto il valore dell'enum
						//setto il field clonato nel clone
						this.setFieldValue(field.getName(), clone, field.getType(), fieldValue);
					}
					else {
						//recupero interfacce implementate da questo field
						Class<?>[] interfacce = field.getType().getInterfaces();
						for (int j = 0; j < interfacce.length; j++) {
							//se il field che sto controllando implementa l'interfaccia cloneable e il field che voglio clonare non e' null
							//richiamo il clone
							if(java.lang.Cloneable.class.getName().equals(interfacce[j].getName()) && fieldValue!=null ){
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
			e.printStackTrace();
			throw new RuntimeException(e.toString(),e);
		}
		return clone;
	}

}

