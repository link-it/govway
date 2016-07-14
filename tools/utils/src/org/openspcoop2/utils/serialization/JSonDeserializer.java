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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;

import net.sf.ezmorph.MorpherRegistry;
import net.sf.ezmorph.bean.BeanMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.EnumMorpher;
import net.sf.json.util.JSONUtils;

/**	
 * Contiene utility per effettuare la de-serializzazione di un oggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class JSonDeserializer implements IDeserializer{

	
	private JsonConfig jsonConfig;
	private List<String> morpherPackage = new ArrayList<String>();
	
	public static JSonDeserializer getJSonDeserializerForOpenSPCoop2Beans(){
		JSonDeserializer deserializer = new JSonDeserializer();
		deserializer.addMorpherPackage("org.openspcoop2");
		return deserializer;
	}
	
	public JSonDeserializer(){
		this(null);
	}
	public JSonDeserializer(String [] excludes){		
		JsonConfig jsonConfig = new JsonConfig();
		if(excludes!=null){
			//jsonConfig.setExcludes(excludes); non funziona l'exclude durante la deserializzazione, uso un filtro
			jsonConfig.setJavaPropertyFilter(new ExclusionPropertyFilter(excludes));
		} 
		this.jsonConfig = jsonConfig;
	}
	
	public void addMorpherPackage(String p){
		this.morpherPackage.add(p);
	}
	
	@Override
	public Object getObject(String s, Class<?> classType) throws IOException{
		return getObject(s, classType, null);
	}
	public Object getListObject(String s, Class<?> listType, Class<?> elementsTypes) throws IOException{
		Object o = getObject(s, listType, elementsTypes);
		if( !(o instanceof List) ){
			throw new IOException("Object de-serializzato di tipo non java.util.List: "+o.getClass().getName());
		}
		return o;
	}
	public Object getSetObject(String s, Class<?> setType, Class<?> elementsTypes) throws IOException{
		Object o = getObject(s, setType, elementsTypes);
		if( !(o instanceof Set) ){
			throw new IOException("Object de-serializzato di tipo non java.util.Set: "+o.getClass().getName());
		}
		return o;
	}
	private Object getObject(String s, Class<?> classType, Class<?> elementsTypes ) throws IOException{
		try{
			return readObject_engine(s, classType, elementsTypes);	
		}catch(Exception e){
			throw new IOException("Trasformazione in oggetto non riuscita: "+e.getMessage(),e);
		}
	}
	
	
	
	@Override
	public Object readObject(InputStream is, Class<?> classType) throws IOException{
		return readObject(is, classType, null);
	}
	public Object readListObject(InputStream is, Class<?> listType, Class<?> elementsTypes) throws IOException{
		Object o = readObject(is, listType, elementsTypes);
		if( !(o instanceof List) ){
			throw new IOException("Object de-serializzato di tipo non java.util.List: "+o.getClass().getName());
		}
		return o;
	}
	public Object readSetObject(InputStream is, Class<?> setType, Class<?> elementsTypes) throws IOException{
		Object o = readObject(is, setType, elementsTypes);
		if( !(o instanceof Set) ){
			throw new IOException("Object de-serializzato di tipo non java.util.Set: "+o.getClass().getName());
		}
		return o;
	}
	private Object readObject(InputStream is, Class<?> classType, Class<?> elementsTypes) throws IOException{
		try{
			byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
			int letti = 0;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			while( (letti=is.read(reads)) != -1 ){
				bout.write(reads, 0, letti);
			}
			bout.flush();
			bout.close();
			
			return readObject_engine(bout.toString(), classType, elementsTypes);	
			
		}catch(Exception e){
			throw new IOException("Trasformazione in oggetto non riuscita: "+e.getMessage(),e);
		}
	}
	
	
	
	@Override
	public Object readObject(Reader reader, Class<?> classType) throws IOException{
		return readObject(reader, classType, null);
	}
	public Object readListObject(Reader reader, Class<?> listType, Class<?> elementsTypes) throws IOException{
		Object o = readObject(reader, listType, elementsTypes);
		if( !(o instanceof List) ){
			throw new IOException("Object de-serializzato di tipo non java.util.List: "+o.getClass().getName());
		}
		return o;
	}
	public Object readSetObject(Reader reader, Class<?> setType, Class<?> elementsTypes) throws IOException{
		Object o = readObject(reader, setType, elementsTypes);
		if( !(o instanceof Set) ){
			throw new IOException("Object de-serializzato di tipo non java.util.Set: "+o.getClass().getName());
		}
		return o;
	}
	private Object readObject(Reader reader, Class<?> classType, Class<?> elementsTypes) throws IOException{
		try{
			char [] reads = new char[Utilities.DIMENSIONE_BUFFER];
			int letti = 0;
			StringBuffer bf = new StringBuffer();
			while( (letti=reader.read(reads)) != -1 ){
				bf.append(reads, 0, letti);
			}
						
			return readObject_engine(bf.toString(), classType, elementsTypes);	
			
		}catch(Exception e){
			throw new IOException("Trasformazione in oggetto non riuscita: "+e.getMessage(),e);
		}
	}

	
	
	private Object readObject_engine(String object,Class<?> classType, Class<?> elementsTypes) throws Exception{
		
		boolean list = false;
		try{
			classType.asSubclass(List.class);
			list = true;
		}catch(ClassCastException cc){}
		
		boolean set = false;
		try{
			classType.asSubclass(Set.class);
			set = true;
		}catch(ClassCastException cc){}
		
		//System.out.println("CLASS: "+classType.getName() +" ENUM["+classType.isEnum()+"] ARRAY["+classType.isArray()+"] LISTCAST["+list+"] SETCAST["+set+"]");
		
		if(classType.isEnum()){
			JSONArray jsonArray = JSONArray.fromObject( object ); 
			Object en = jsonArray.toArray()[0];
			//System.out.println("TIPO ["+en.getClass().getName()+"]");
			Object [] o = classType.getEnumConstants();
			Class<?> enumeration = o[0].getClass();
			//System.out.println("OO ["+enumeration+"]");
			Method method = enumeration.getMethod("valueOf", String.class);
			Object value = method.invoke(enumeration, en);
			return value;
		}
		
		else if(classType.isArray()){

			JSONArray jsonArray = JSONArray.fromObject( object ); 
			Object[] oarray = jsonArray.toArray();
			Object[] array = new Object[oarray.length];
			for(int i=0; i<oarray.length;i++){
				JSONObject json = (JSONObject) oarray[i];
				array[i] =  fromJSONObjectToOriginalObject(classType.getComponentType(), json);
			}
			return array;
		}
		
		else if(list){
			
			if(elementsTypes==null){
				throw new Exception("elementsTypes non definito, e' obbligatorio per il tipo che si desidera de-serializzare: "+classType.getName());
			}
			
			JSONArray jsonArray = JSONArray.fromObject( object ); 
			Object[] oarray = jsonArray.toArray();
			@SuppressWarnings("unchecked")
			List<Object> listReturn = (List<Object>) classType.newInstance();		
			for(int i=0; i<oarray.length;i++){
				JSONObject json = (JSONObject) oarray[i];
				Object o =  fromJSONObjectToOriginalObject(elementsTypes, json);
				listReturn.add(o);
			}
			return listReturn;
		}
		
		else if(set){
			
			if(elementsTypes==null){
				throw new Exception("elementsTypes non definito, e' obbligatorio per il tipo che si desidera de-serializzare: "+classType.getName());
			}
			
			JSONArray jsonArray = JSONArray.fromObject( object ); 
			Object[] oarray = jsonArray.toArray();
			@SuppressWarnings("unchecked")
			Set<Object> setReturn = (Set<Object>) classType.newInstance();		
			for(int i=0; i<oarray.length;i++){
				JSONObject json = (JSONObject) oarray[i];
				Object o =  fromJSONObjectToOriginalObject(elementsTypes, json);
				setReturn.add(o);
			}
			return setReturn;
		}
		
		else{
			JSONObject jsonObject = JSONObject.fromObject( object );  
			return fromJSONObjectToOriginalObject(classType, jsonObject);
		}
	}
	
	private Object fromJSONObjectToOriginalObject(Class<?> classType, JSONObject jsonObject) throws UtilsException{
		this.jsonConfig.setRootClass(classType);
		
		MorpherRegistry morpherRegistry = JSONUtils.getMorpherRegistry();
		registerMorpher(classType, morpherRegistry, new ArrayList<String>());
		
		Object o = JSONObject.toBean( jsonObject, this.jsonConfig );

		this.morpher(o, morpherRegistry);
		
		return o;
	}
	
	private void registerMorpher(Class<?> oClass,MorpherRegistry morpherRegistry, List<String> classRegistered) throws UtilsException{
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
						
						if(isMorpherPackage(tipoReturn)){
							if(tipoReturn.isEnum()){
								if(classRegistered.contains(tipoReturn.getName())==false){
									classRegistered.add(tipoReturn.getName());
									System.out.println("REGISTRATO ENUM ["+tipoReturn.getName()+"]");
									morpherRegistry.registerMorpher( new EnumMorpher( tipoReturn ) ); 
								}
							}
							else{
								if(classRegistered.contains(tipoReturn.getName())==false){
									classRegistered.add(tipoReturn.getName());
									System.out.println("REGISTRATO BEAN ["+tipoReturn.getName()+"]");
									morpherRegistry.registerMorpher( new BeanMorpher( tipoReturn, morpherRegistry) );
								}
								//System.out.println("RICORSIONE ["+tipoReturn.getName()+"]");
								registerMorpher(tipoReturn, morpherRegistry, classRegistered);
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
								
								if(classRegistered.contains(classGenericType.getName())==false){
									classRegistered.add(classGenericType.getName());
									System.out.println("REGISTRATO BEAN ["+classGenericType.getName()+"]");
									morpherRegistry.registerMorpher( new BeanMorpher( classGenericType, morpherRegistry) );
								}
								
								//System.out.println("RICORSIONE-LIST ["+classGenericType.getName()+"]");
								registerMorpher(classGenericType, morpherRegistry, classRegistered);
							}
						}
						
					}
					
				}
			}
		}catch(Exception e){
			throw new UtilsException("Normalizzazione date non riuscita: "+e.getMessage(),e);
		}
	}
	
	
	private void morpher(Object o,MorpherRegistry morpherRegistry) throws UtilsException{
		
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
						
						if(isMorpherPackage(tipoReturn)){
							Object oInternal = methods[i].invoke(o);
							if(oInternal!=null){
								if(oInternal instanceof net.sf.ezmorph.bean.MorphDynaBean){
									Object oDeserialized = morpherRegistry.morph( tipoReturn, oInternal );
									
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
										morpher(oDeserialized, morpherRegistry);
									}
								}
								else{
									//System.out.println("RICORSIONE ["+oInternal.getClass().getName()+"]");
									morpher(oInternal, morpherRegistry);
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
														Object oDeserialized = morpherRegistry.morph( classGenericType, oInternalList );
														
														if(oDeserialized!=null){
															
															newList.add(oDeserialized);
															
															//System.out.println("RICORSIONE-LIST MorphDynaBean ["+oDeserialized.getClass().getName()+"]");
															morpher(oDeserialized, morpherRegistry);
														}
													}
													else{
														//System.out.println("RICORSIONE-LIST ["+oInternalList.getClass().getName()+"]");
														morpher(oInternalList, morpherRegistry);
														
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
		}catch(Exception e){
			throw new UtilsException("Normalizzazione date non riuscita: "+e.getMessage(),e);
		}
	}
	
	
	private boolean isMorpherPackage(Class<?> c){
		if(this.morpherPackage!=null && this.morpherPackage.size()>0){
			if(c.getPackage()!=null){
				for (int i = 0; i < this.morpherPackage.size(); i++) {
					String p = this.morpherPackage.get(i);
					if(c.getPackage().getName().equals(p) ||
							c.getPackage().getName().startsWith(p+".")){
						return true;
					}
				}
			}
		}
		return false;
	}
}
