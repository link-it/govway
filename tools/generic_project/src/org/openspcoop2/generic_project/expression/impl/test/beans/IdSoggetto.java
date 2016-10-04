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
package org.openspcoop2.generic_project.expression.impl.test.beans;

import java.io.Serializable;


/**
 * IdSoggetto
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdSoggetto implements Serializable , Cloneable {
  private Long id;


  public IdSoggetto() {
  }

  public Long getId() {
    if(this.id!=null)
		return this.id;
	else
		return new Long(-1);
  }

  public void setId(Long id) {
    if(id!=null)
		this.id=id;
	else
		this.id=new Long(-1);
  }

  public java.lang.String getTipo() {
    return this.tipo;
  }

  public void setTipo(java.lang.String tipo) {
    this.tipo = tipo;
  }

  public java.lang.String getNome() {
    return this.nome;
  }

  public void setNome(java.lang.String nome) {
    this.nome = nome;
  }

  private static final long serialVersionUID = 1L;



  @Override
  public boolean equals(Object object){
		return this.equals(object,true);
  }
  @SuppressWarnings("unchecked")
  public boolean equals(Object object,boolean checkID){
    try{
		 if(object==null){
			return false;
		}
		java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
		for(int i=0; i<fields.length;i++){
			// field id non controllato se checkID==false
			if(checkID==false){
				if("id".equals(fields[i].getName()) || "idAccordo".equals(fields[i].getName()) || "idSoggetto".equals(fields[i].getName()) || "idPortType".equals(fields[i].getName()) || "idAccordoCooperazione".equals(fields[i].getName()) || "idServizioComponente".equals(fields[i].getName()) || "idProprietarioDocumento".equals(fields[i].getName()) || "idOperation".equals(fields[i].getName()) )
					continue;
			}
			if(fields[i].get(this)==null){
				// FIELD
				if(fields[i].get(object)!=null){
					return false;
				}
			}else{
				// ARRAY_LIST
				if(fields[i].getType().getName().equals("java.util.ArrayList") || fields[i].getType().getName().equals("java.util.List") ){
					java.util.List<?> lista_this = (java.util.List<?>) fields[i].get(this);
					java.util.List<?> lista_parameter = (java.util.List<?>) fields[i].get(object);
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
							java.lang.reflect.Method method =  c.getMethod("equals",Object.class,boolean.class);
							resultMethod = (Boolean) method.invoke(lista_thisSORT.get(j),lista_parameterSORT.get(j),checkID);
						}else{
							resultMethod = lista_thisSORT.get(j).equals(lista_parameterSORT.get(j));
						}
						if(resultMethod==false)
							return false;
					}
				}else{
					boolean resultMethod = false;
					if(fields[i].getType().getName().startsWith(this.getClass().getPackage().getName())){
						java.lang.reflect.Method method =  fields[i].getType().getMethod("equals",Object.class,boolean.class);
						resultMethod = (Boolean) method.invoke(fields[i].get(this),fields[i].get(object),checkID);
					}else{
						boolean checkUguaglianza = false;
  					    if("[B".equals(fields[i].getType().getName())){
							if(fields[i].get(object)!=null){
								byte[] origi = (byte[]) fields[i].get(this);
								byte[] dest = (byte[]) fields[i].get(object);
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
								calendarThis.setTime((java.util.Date)fields[i].get(this));
								java.util.Calendar calendarObject = new java.util.GregorianCalendar();
								calendarObject.setTime((java.util.Date)fields[i].get(object));
								checkUguaglianza = (  calendarThis.get(java.util.Calendar.YEAR) == calendarObject.get(java.util.Calendar.YEAR) ) && 
									( calendarThis.get(java.util.Calendar.MONTH) == calendarObject.get(java.util.Calendar.MONTH) ) && 
									( calendarThis.get(java.util.Calendar.DAY_OF_MONTH) == calendarObject.get(java.util.Calendar.DAY_OF_MONTH) ) && 
									( calendarThis.get(java.util.Calendar.HOUR_OF_DAY) == calendarObject.get(java.util.Calendar.HOUR_OF_DAY) ) && 
									( calendarThis.get(java.util.Calendar.MINUTE) == calendarObject.get(java.util.Calendar.MINUTE) ) && 
									( calendarThis.get(java.util.Calendar.SECOND) == calendarObject.get(java.util.Calendar.SECOND) );
							}else{
								checkUguaglianza = fields[i].get(this).equals(fields[i].get(object));
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
		return false;
	}
  }

  @Override
  public String toString(){
		return toString(false);
  }
  public String toString(boolean reportHTML){
		return toString(reportHTML,false);
  }
  public String toString(boolean reportHTML,boolean notIncludeID){
	try{
		StringBuffer bf = new StringBuffer();
		java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
		for(int i=0; i<fields.length;i++){
			
			if(notIncludeID){
				if("id".equals(fields[i].getName()))
					continue;
				if("idAccordo".equals(fields[i].getName()))
					continue;
				if("idSoggetto".equals(fields[i].getName()))
					continue;
				if("idPortType".equals(fields[i].getName()))
					continue;
				if("idAccordoCooperazione".equals(fields[i].getName()))
					continue;
				if("idServizioComponente".equals(fields[i].getName()))
					continue;
				if("idProprietarioDocumento".equals(fields[i].getName()))
					continue;
                               if("idOperation".equals(fields[i].getName()))
                                       continue;
				if("serialVersionUID".equals(fields[i].getName()))
					continue;
			}
			
			bf.append("---------- ");
			bf.append(this.getClass().getName());
			bf.append(".");
			bf.append(fields[i].getName());
			bf.append(" ----------");
			if(reportHTML) bf.append("<br>"); else bf.append("\n");
			if(fields[i].get(this)==null){
				bf.append("null");
			}else{
				if(fields[i].getType().getName().equals("java.util.ArrayList") || fields[i].getType().getName().equals("java.util.List")){
					java.util.List<?> lista_this = (java.util.List<?>) fields[i].get(this);
					bf.append("List size("+lista_this.size()+")");
					if(lista_this.size()>0){
			            if(reportHTML) bf.append("<br>"); else bf.append("\n");
                   }
                   java.util.List<String> sortLISTKEY = new java.util.ArrayList<String>();
                   for(int j=0; j<lista_this.size(); j++){
                   	String key = null;
                   	if(lista_this.get(j).getClass().getName().startsWith(this.getClass().getPackage().getName())){
                   		Class<?> c =  lista_this.get(j).getClass();
                   		java.lang.reflect.Method method =  c.getMethod("toString",boolean.class,boolean.class);
                   		key = (String) method.invoke(lista_this.get(j),reportHTML,notIncludeID);
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
					if(fields[i].get(this).getClass().getName().startsWith(this.getClass().getPackage().getName())){
						Class<?> c =  fields[i].get(this).getClass();
						java.lang.reflect.Method method =  c.getMethod("toString",boolean.class,boolean.class);
						bf.append(method.invoke(fields[i].get(this),reportHTML,notIncludeID));
					}else{
					   if("[B".equals(fields[i].getType().getName())){
					   		if(fields[i].get(this)!=null){
								byte[] array = (byte[])fields[i].get(this);
								for(int k=0; k<array.length;k++)
									bf.append(((char)array[k]));
							}
							else
								bf.append("null");
						}else if("java.util.Date".equals(fields[i].getType().getName())){
							java.util.Date date = (java.util.Date) fields[i].get(this);
					   		java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
					   		bf.append(format.format(date));
						}else{
							bf.append(fields[i].get(this).toString());
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
		return null;
	}
  }

  public String diff(Object object,StringBuffer bf,boolean reportHTML){
		return this.diff(object,bf,reportHTML,true);
  }
  @SuppressWarnings("unchecked")
  public String diff(Object object,StringBuffer bf,boolean reportHTML,boolean checkID){
    try{
    	if(object==null){
    		bf.append(this.getClass().getName());
    		bf.append("this is not null, parameter is null");
    		return bf.toString();
		}
		java.lang.reflect.Field[] fields = getClass().getDeclaredFields();
		for(int i=0; i<fields.length;i++){
			if(fields[i].getType().getName().equals("java.util.ArrayList") || fields[i].getType().getName().equals("java.util.List")){
				// LISTA
				java.util.List<?> lista_this = (java.util.List<?>) fields[i].get(this);
				java.util.List<?> lista_parameter = (java.util.List<?>) fields[i].get(object);
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
									bf.append(" Match non trovato in this(size:"+thisTmp.size()+") per l'elemento: "+key);
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
									bf.append(" Match non trovato in param(size:"+parameterTmp.size()+") per l'elemento: "+key);
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
										java.lang.reflect.Method method =  c.getMethod("diff",Object.class,bf.getClass(),boolean.class,boolean.class);
										method.invoke(lista_thisSORT.get(j),lista_parameterSORT.get(j),bf,reportHTML,checkID);
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
											}else{
												bf.append(lista_thisSORT.get(j));
											}
									    	bf.append(" parameter:");
									    	if("[B".equals(lista_thisSORT.get(j).getClass().getName())){
												if(lista_parameterSORT.get(j)!=null){
													byte[] array = (byte[])lista_parameterSORT.get(j);
													for(int k=0; k<array.length;k++)
														bf.append(((char)array[k]));
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
				// field id non controllato se checkID==false
				if(checkID==false){
					if("id".equals(fields[i].getName()) || "idAccordo".equals(fields[i].getName()) || "idSoggetto".equals(fields[i].getName())  || "idPortType".equals(fields[i].getName()) || "idAccordoCooperazione".equals(fields[i].getName()) || "idServizioComponente".equals(fields[i].getName()) || "idProprietarioDocumento".equals(fields[i].getName()) || "idOperation".equals(fields[i].getName()) )
						continue;
				}
				// ELEMENTO SINGOLO
				if(fields[i].get(this)==null){
					// this is null, parameter is not null
					if(fields[i].get(object)!=null){
						bf.append(this.getClass().getName());
						bf.append(".");
						bf.append(fields[i].getName());
						bf.append(" this:is null, parameter: is not null");
                       bf.append(" parameter_value:");
						if(reportHTML) bf.append("<br>"); else bf.append("\n");
                       if(fields[i].get(object).getClass().getName().startsWith(this.getClass().getPackage().getName())){
							Class<?> c =  fields[i].get(object).getClass();
							java.lang.reflect.Method method =  c.getMethod("toString",boolean.class);
							bf.append(method.invoke(fields[i].get(object),reportHTML));
                       }else{
                           if("[B".equals(fields[i].getType().getName())){
								if(fields[i].get(object)!=null){
									byte[] array = (byte[])fields[i].get(object);
									for(int k=0; k<array.length;k++)
										bf.append(array[k]);
								}
								else
									bf.append("null");
							}else{
								bf.append(fields[i].get(object));
							}
                       }
						if(reportHTML) bf.append("<br>"); else bf.append("\n");
					}
				}else{
					if(fields[i].getType().getName().startsWith(this.getClass().getPackage().getName())){
						java.lang.reflect.Method method =  fields[i].getType().getMethod("diff",Object.class,bf.getClass(),boolean.class,boolean.class);
						method.invoke(fields[i].get(this),fields[i].get(object),bf,reportHTML,checkID);
					}else{
						boolean checkUguaglianza = false;
						if("[B".equals(fields[i].getType().getName())){
							if(fields[i].get(object)!=null){
								byte[] origi = (byte[]) fields[i].get(this);
								byte[] dest = (byte[]) fields[i].get(object);
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
								calendarThis.setTime((java.util.Date)fields[i].get(this));
								java.util.Calendar calendarObject = new java.util.GregorianCalendar();
								calendarObject.setTime((java.util.Date)fields[i].get(object));
								checkUguaglianza = (  calendarThis.get(java.util.Calendar.YEAR) == calendarObject.get(java.util.Calendar.YEAR) ) && 
									( calendarThis.get(java.util.Calendar.MONTH) == calendarObject.get(java.util.Calendar.MONTH) ) && 
									( calendarThis.get(java.util.Calendar.DAY_OF_MONTH) == calendarObject.get(java.util.Calendar.DAY_OF_MONTH) ) && 
									( calendarThis.get(java.util.Calendar.HOUR_OF_DAY) == calendarObject.get(java.util.Calendar.HOUR_OF_DAY) ) && 
									( calendarThis.get(java.util.Calendar.MINUTE) == calendarObject.get(java.util.Calendar.MINUTE) ) && 
									( calendarThis.get(java.util.Calendar.SECOND) == calendarObject.get(java.util.Calendar.SECOND) );
							}else{
								checkUguaglianza = fields[i].get(this).equals(fields[i].get(object));
							}
						}
						if(checkUguaglianza==false){
							bf.append(this.getClass().getName());
							bf.append(".");
					    	bf.append(fields[i].getName());
					    	bf.append(" this:");
					    	if("[B".equals(fields[i].getType().getName())){
								if(fields[i].get(this)!=null){
									byte[] array = (byte[])fields[i].get(this);
									for(int k=0; k<array.length;k++)
										bf.append(((char)array[k]));
								}
								else
									bf.append("null");
							}else{
								bf.append(fields[i].get(this));
							}
					    	bf.append(" parameter:");
					    	if("[B".equals(fields[i].getType().getName())){
								if(fields[i].get(object)!=null){
									byte[] array = (byte[])fields[i].get(object);
									for(int k=0; k<array.length;k++)
										bf.append(((char)array[k]));
								}
								else
									bf.append("null");
							}else{
								bf.append(fields[i].get(object));
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
		bf.append("Errore durante l'analisi delle differenze: "+e.getMessage());
		return bf.toString();
	}
  }

  @Override
  public Object clone()  {
    Object clone = null;
    try{
        //clono l'oggetto
        clone = super.clone();
         /*
          * Per ogni field se il field presente nella classe implementa
          * l'interfaccia cloneable allora eseguo il metodo clone di quel field
          */
        java.lang.reflect.Field[] fields = this.getClass().getDeclaredFields();
        if(fields!=null && fields.length>0){
            for (int i = 0; i < fields.length; i++) {
                java.lang.reflect.Field field = fields[i];
				if("[B".equals(field.getType().getName())){
					//caso particolare arrya di byte
					byte[] originale = (byte[])field.get(this);
					if(originale!=null){
						byte[] arrayClone = new byte[originale.length];
						for (int j = 0; j < arrayClone.length; j++) {
							arrayClone[j]=originale[j];
						}
						java.lang.reflect.Field toSet = clone.getClass().getDeclaredField(field.getName());
						toSet.set(clone, arrayClone);
					}
					continue;
				}
                //recupero interfacce implementate da questo field
                Class<?>[] interfacce = field.getType().getInterfaces();
                for (int j = 0; j < interfacce.length; j++) {
						//se il field che sto controllando implementa l'interfaccia cloneable e il field che voglio clonare non e' null
						//richiamo il clone
						if(java.lang.Cloneable.class.getName().equals(interfacce[j].getName()) && field.get(this)!=null ){
                       		//recupero il metodo clone dal field
                        	java.lang.reflect.Method method =  field.getType().getMethod("clone");
                        	//effettuo il clone del field
                        	java.lang.Cloneable ris =  (java.lang.Cloneable) method.invoke(field.get(this));
                        	//setto il field clonato nel clone
                        	java.lang.reflect.Field toSet = clone.getClass().getDeclaredField(field.getName());
                        	toSet.set(clone, ris); 
						}
                }
            }
        }
        
    }catch (CloneNotSupportedException e) {
        // This should never happen
       throw new InternalError(e.toString());
    }catch (NoSuchMethodException nsme) {
       throw new InternalError(nsme.toString());
    }catch (NoSuchFieldException e) {
       throw new InternalError(e.toString());
    }catch (java.lang.reflect.InvocationTargetException ite) {
       throw new InternalError(ite.toString());
    }catch (IllegalAccessException e) {
       throw new InternalError(e.toString());
    }
    return clone;
  }

  protected java.lang.String tipo;

  protected java.lang.String nome;

}
