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


package org.openspcoop2.utils.serialization;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openspcoop2.utils.UtilsException;

import net.sf.ezmorph.MorpherRegistry;
import net.sf.ezmorph.bean.BeanMorpher;
import net.sf.json.util.EnumMorpher;

/**	
 * Contiene delle utilities per questo package
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Utilities {

	public static void normalizeDateObjects(Object o) throws IOException{
		try{
			if(o!=null){
				//System.out.println("Inizio analisi per  ["+o.getClass().getName()+"]...");
				java.lang.reflect.Method[] methods = o.getClass().getMethods();
				for(int i=0; i<methods.length;i++){
					String nomeMetodo = methods[i].getName();
					//System.out.println("Esamino ["+nomeMetodo+"]...");
					if(nomeMetodo.length()>3 && 
							nomeMetodo.startsWith("get") && 
							methods[i].getParameterTypes()!=null && methods[i].getParameterTypes().length==0 &&
							methods[i].getReturnType()!=null && !("void".equals(methods[i].getReturnType().getName()))){
						Class<?> tipoReturn = methods[i].getReturnType();
						//System.out.println("ANALIZZO TIPO RETURN ["+tipoReturn.getName()+"]...");
												
						if(tipoReturn.isAssignableFrom(Date.class)){
																	
							//System.out.println("DATA creo oggetto time ["+methods[i].getName()+"]? check tipo per evitare object generici che sono assegnabili a Date");
							Object oDate = methods[i].invoke(o);
							if(oDate!=null){
								if(oDate instanceof Date){
									Date tmp = (Date) oDate;
									String nomeMetodoSet = "s"+nomeMetodo.subSequence(1, nomeMetodo.length());
									//System.out.println("DATA ("+o.getClass().getName()+") set con metodo ["+nomeMetodoSet+"]");
									java.lang.reflect.Method methodSet =  o.getClass().getMethod(nomeMetodoSet,Date.class);
									methodSet.invoke(o,new Date(tmp.getTime()));
									//System.out.println("DATA set effettuato");
								}
							}
						}
						else if(o.getClass().getPackage()!=null && tipoReturn.getName().startsWith(o.getClass().getPackage().getName())){
							if(tipoReturn.isEnum()==false){
								//System.out.println("Normalize per ["+o.getClass().getPackage().getName()+"]...");
								Utilities.normalizeDateObjects(methods[i].invoke(o));
								//System.out.println("Normalize per ["+o.getClass().getPackage().getName()+"] fatto");
							}/*else{
								System.out.println("ENUM!");
							}*/
						}
					}
					else if(nomeMetodo.length()>3 && 
							nomeMetodo.startsWith("size") &&  nomeMetodo.endsWith("List") &&  
							methods[i].getParameterTypes()!=null && methods[i].getParameterTypes().length==0 &&
							methods[i].getReturnType()!=null && "int".equals(methods[i].getReturnType().getName())){
						Object oLista = methods[i].invoke(o);
						//System.out.println("LISTA["+oLista.getClass().getName()+"]");
						int sizeLista = (Integer) oLista;
						for(int j=0; j<sizeLista; j++){
							String nomeMetodoGet = "get"+nomeMetodo.subSequence("size".length(), (nomeMetodo.length()-"List".length()));
							//System.out.println("NOME METODO["+nomeMetodoGet+"]");
							java.lang.reflect.Method methodGet =  null;
							try{
								methodGet = o.getClass().getMethod(nomeMetodoGet,int.class);
							}catch(Exception e){
								//System.out.println("NON ESISTE  IL METODO: "+e.getMessage());
							}
							if(methodGet!=null){
								//System.out.println("ANALIZZO TIPO RETURN METODO GET ["+methodGet.getReturnType().getName()+"]...");
								Class<?> tipoReturn = methodGet.getReturnType();
								if(tipoReturn.getName().startsWith(o.getClass().getPackage().getName())){
									//System.out.println("RINORMALIZZO IN RICORSIONE..... ");
									try{
										Utilities.normalizeDateObjects(methodGet.invoke(o,j));
									}catch(Exception e){
										//System.out.println("RINORMALIZZAZIONE NON RIUSCITA: "+e.getMessage());
									}	
									//System.out.println("RINORMALIZZO IN RICORSIONE FINE -------------------- ");
								}
							}/*else{
								System.out.println("NON ESISTE  IL METODO");
							}*/
						}
					}
				}
				//System.out.println("Fine analisi per  ["+o.getClass().getName()+"].");
			}
		}catch(Exception e){
			throw new IOException("Normalizzazione date non riuscita: "+e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	public static void registerMorpher(Class<?> oClass,MorpherRegistry morpherRegistry, List<String> classRegistered,List<String> morpherPackage) throws UtilsException{
		try{
			if(oClass!=null){
				java.lang.reflect.Method[] methods = oClass.getMethods();
				for(int i=0; i<methods.length;i++){
					String nomeMetodo = methods[i].getName();
				
					if(nomeMetodo.length()>3 && 
							nomeMetodo.startsWith("get") && 
							methods[i].getParameterTypes()!=null && methods[i].getParameterTypes().length==0 &&
							methods[i].getReturnType()!=null && !("void".equals(methods[i].getReturnType().getName()))){
						Class<?> tipoReturn = methods[i].getReturnType();
						//System.out.println("AAAAAA ["+nomeMetodo+"] ["+tipoReturn+"] primitive["+tipoReturn.isPrimitive()+"] enum["+tipoReturn.isEnum()+"] ["+isMorpherPackage(tipoReturn)+"]");
						
						if(isMorpherPackage(tipoReturn,morpherPackage)){
							if(tipoReturn.isEnum()){
								if(classRegistered.contains(tipoReturn.getName())==false){
									classRegistered.add(tipoReturn.getName());
									//System.out.println("REGISTRATO ENUM ["+tipoReturn.getName()+"]");
									morpherRegistry.registerMorpher( new EnumMorpher( tipoReturn ) ); 
								}
							}
							else{
								if(classRegistered.contains(tipoReturn.getName())==false){
									classRegistered.add(tipoReturn.getName());
									//System.out.println("REGISTRATO BEAN ["+tipoReturn.getName()+"]");
									morpherRegistry.registerMorpher( new BeanMorpher( tipoReturn, morpherRegistry ) );
								}
								//System.out.println("RICORSIONE ["+tipoReturn.getName()+"]");
								registerMorpher(tipoReturn, morpherRegistry, classRegistered,morpherPackage);
							}
						}
						
						else if(tipoReturn.isAssignableFrom(java.util.List.class)){
							
							// ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
						    
							Class<?> classGenericType = null;
							try{
								String genericType = methods[i].getGenericReturnType().toString().substring("java.util.List<".length(), methods[i].getGenericReturnType().toString().length()-1);
								//System.out.println("IS LIST ["+tipoReturn.getName()+"] ["+genericType+"]");
								classGenericType = Class.forName(genericType);
							}catch(Exception e){
								throw new UtilsException(e.getMessage(),e);
							}
							if(classGenericType!=null){
								
								if(isMorpherPackage(classGenericType,morpherPackage)){
								
									if(classRegistered.contains(classGenericType.getName())==false){
										classRegistered.add(classGenericType.getName());
										//System.out.println("REGISTRATO BEAN ["+classGenericType.getName()+"]");
										morpherRegistry.registerMorpher( new BeanMorpher( classGenericType, morpherRegistry) );
									}
									
									//System.out.println("RICORSIONE-LIST ["+classGenericType.getName()+"]");
									registerMorpher(classGenericType, morpherRegistry, classRegistered,morpherPackage);
									
								}
							}
						}
						
					}
					
				}
			}
		}catch(Exception e){
			throw new UtilsException("registerMorpher failed: "+e.getMessage(),e);
		}
	}
	
	
	public static void morpher(Object o,MorpherRegistry morpherRegistry,List<String> morpherPackage, boolean throwExceptionMorpherFailed) throws UtilsException{
		
		try{
			if(o!=null){
				//System.out.println("Inizio analisi per  ["+o.getClass().getName()+"]...");
				java.lang.reflect.Method[] methods = o.getClass().getMethods();
				
				// Prima registro i tipi
				for(int i=0; i<methods.length;i++){
					String nomeMetodo = methods[i].getName();
				
					if(nomeMetodo.length()>3 && 
							nomeMetodo.startsWith("get") && 
							methods[i].getParameterTypes()!=null && methods[i].getParameterTypes().length==0 &&
							methods[i].getReturnType()!=null && !("void".equals(methods[i].getReturnType().getName()))){
						Class<?> tipoReturn = methods[i].getReturnType();
						//System.out.println("AAAAAA ["+nomeMetodo+"] ["+tipoReturn+"] primitive["+tipoReturn.isPrimitive()+"] enum["+tipoReturn.isEnum()+"] ["+isMorpherPackage(tipoReturn)+"]");
						
						if(isMorpherPackage(tipoReturn,morpherPackage)){
							Object oInternal = methods[i].invoke(o);
							if(oInternal!=null){
								if(oInternal instanceof net.sf.ezmorph.bean.MorphDynaBean){
									Object oDeserialized = null;
									try{
										net.sf.ezmorph.bean.MorphDynaBean mdb = (net.sf.ezmorph.bean.MorphDynaBean)oInternal;
										normalizeByteArray(tipoReturn, mdb);
										oDeserialized = morpherRegistry.morph( tipoReturn, mdb );
									}catch(Exception e){
										if(throwExceptionMorpherFailed){
											throw e;
										}
									}
									
									if(oDeserialized!=null){
										// cerco metodo Set
										java.lang.reflect.Method setMethodRef = null;
										for(int k=0; k<methods.length;k++){
											String setMethod = "set"+nomeMetodo.substring("get".length(), nomeMetodo.length());
											if(methods[k].getName().equals(setMethod) &&
													methods[k].getParameterTypes()!=null && methods[k].getParameterTypes().length==1){
												setMethodRef = methods[k];
												break;
											}
										}
										setMethodRef.invoke(o, oDeserialized);
										
										//System.out.println("RICORSIONE MorphDynaBean ["+oDeserialized.getClass().getName()+"]");
										morpher(oDeserialized, morpherRegistry,morpherPackage,throwExceptionMorpherFailed);
									}
								}
								else{
									//System.out.println("RICORSIONE ["+oInternal.getClass().getName()+"]");
									morpher(oInternal, morpherRegistry,morpherPackage,throwExceptionMorpherFailed);
								}
							}
						}
						
						else if(tipoReturn.isAssignableFrom(java.util.List.class)){
							
							// ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
						    
							Class<?> classGenericType = null;
							try{
								//System.out.println("AAAAA ["+nomeMetodo+"] ["+tipoReturn+"] ["+methods[i].getGenericReturnType().toString()+"]");
								String m = methods[i].getGenericReturnType().toString();
								if(m.startsWith("java.util.List<")){
									String genericType = m.substring("java.util.List<".length(), methods[i].getGenericReturnType().toString().length()-1);
									//System.out.println("IS LIST ["+tipoReturn.getName()+"] ["+genericType+"]");
									classGenericType = Class.forName(genericType);
								}
							}catch(Exception e){
								throw new UtilsException(e.getMessage(),e);
							}
							if(classGenericType!=null){
								
								if(isMorpherPackage(classGenericType,morpherPackage)){
								
									Object oInternal = methods[i].invoke(o);
									if(oInternal!=null && (oInternal instanceof java.util.List)){
										java.util.List<?> l = (java.util.List<?>) oInternal;
										if(l.size()>0){
											
											// cerco metodo Set
											java.lang.reflect.Method setMethodList = null;
											for(int k=0; k<methods.length;k++){
												String setMethod = "set"+nomeMetodo.substring("get".length(), nomeMetodo.length());
												if(methods[k].getName().equals(setMethod) &&
														methods[k].getParameterTypes()!=null && methods[k].getParameterTypes().length==1){
													setMethodList = methods[k];
													break;
												}
											}
											
											if(setMethodList!=null){
												List<Object> newList = new ArrayList<Object>();
												
												for (Object oInternalList : l) {
													if(oInternalList!=null){
				
														if(oInternalList instanceof net.sf.ezmorph.bean.MorphDynaBean){
															Object oDeserialized = null;
															try{
																net.sf.ezmorph.bean.MorphDynaBean mdb = (net.sf.ezmorph.bean.MorphDynaBean)oInternalList;
																normalizeByteArray(classGenericType, mdb);
																oDeserialized = morpherRegistry.morph( classGenericType, mdb );
															}catch(Exception e){
																if(throwExceptionMorpherFailed){
																	throw e;
																}
															}
															
															if(oDeserialized!=null){
																
																newList.add(oDeserialized);
																
																//System.out.println("RICORSIONE-LIST MorphDynaBean ["+oDeserialized.getClass().getName()+"]");
																morpher(oDeserialized, morpherRegistry,morpherPackage,throwExceptionMorpherFailed);
															}
														}
														else{
															//System.out.println("RICORSIONE-LIST ["+oInternalList.getClass().getName()+"]");
															morpher(oInternalList, morpherRegistry,morpherPackage,throwExceptionMorpherFailed);
															
															newList.add(oInternalList);
															
														}
				
													}
												}
												
												setMethodList.invoke(o, newList);
											}
											
										}
									}
								}
							}
						}
					}
					
				}
				
			}
		}catch(Exception e){
			throw new UtilsException("morpher failed: "+e.getMessage(),e);
		}
	}
	
	
	private static boolean isMorpherPackage(Class<?> c,List<String> morpherPackage){
		if(morpherPackage!=null && morpherPackage.size()>0){
			if(c.getPackage()!=null){
				for (int i = 0; i < morpherPackage.size(); i++) {
					String p = morpherPackage.get(i);
					if(c.getPackage().getName().equals(p) ||
							c.getPackage().getName().startsWith(p+".")){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static void normalizeByteArray(Class<?> classGenericType, net.sf.ezmorph.bean.MorphDynaBean mdb){
		java.lang.reflect.Method[] methodsMDB = classGenericType.getMethods();
		for(int j=0; j<methodsMDB.length;j++){
			String nomeMetodoMDB = methodsMDB[j].getName();
			if(nomeMetodoMDB.length()>3 && 
					nomeMetodoMDB.startsWith("get") && 
					methodsMDB[j].getParameterTypes()!=null && methodsMDB[j].getParameterTypes().length==0 &&
							methodsMDB[j].getReturnType()!=null && !("void".equals(methodsMDB[j].getReturnType().getName()))){
				Class<?> tipoReturnMDB = methodsMDB[j].getReturnType();
				String variableName = (nomeMetodoMDB.charAt(3)+"").toLowerCase() + nomeMetodoMDB.substring(4);
				if(tipoReturnMDB.isAssignableFrom(byte[].class)){
					try{
						Object oF = mdb.get(variableName);
						if(oF!=null){
							if(oF.getClass().isAssignableFrom(java.util.ArrayList.class)){
								java.util.List<?> lf = (java.util.List<?>) oF;
								mdb.set(variableName, lf.toArray());
							}
						}
					}catch(Exception e){
						//System.out.println("not exists: "+e.getMessage());
					}
				}
			}
		}
	}
}
