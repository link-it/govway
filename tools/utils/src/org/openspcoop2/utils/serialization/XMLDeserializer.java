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


package org.openspcoop2.utils.serialization;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openspcoop2.utils.CopyCharStream;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;

import net.sf.ezmorph.MorpherRegistry;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;

/**	
 * Contiene utility per effettuare la de-serializzazione di un oggetto
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class XMLDeserializer implements IDeserializer{

	private net.sf.json.xml.XMLSerializer xmlSerializer;
	private JsonConfig jsonConfig;
	private List<String> morpherPackage = new ArrayList<String>();
	private boolean throwExceptionMorpherFailed = true;
	
	public XMLDeserializer(){
		this(new SerializationConfig());
	}
	public XMLDeserializer(SerializationConfig config) {
		this.xmlSerializer = new net.sf.json.xml.XMLSerializer();
		JsonConfig jsonConfig = new JsonConfig();
		if(config.getExcludes()!=null){
			//jsonConfig.setExcludes(excludes); non funziona l'exclude durante la deserializzazione, uso un filtro
			jsonConfig.setJavaPropertyFilter(new ExclusionPropertyFilter(config.getExcludes()));
		} 
		this.jsonConfig = jsonConfig;
		this.addMorpherPackage("org.openspcoop2");
	}
	
	public void addMorpherPackage(String p){
		this.morpherPackage.add(p);
	}
	
	public boolean isThrowExceptionMorpherFailed() {
		return this.throwExceptionMorpherFailed;
	}
	public void setThrowExceptionMorpherFailed(boolean throwExceptionMorpherFailed) {
		this.throwExceptionMorpherFailed = throwExceptionMorpherFailed;
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
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
//			byte [] reads = new byte[Utilities.DIMENSIONE_BUFFER];
//			int letti = 0;
//			while( (letti=is.read(reads)) != -1 ){
//				bout.write(reads, 0, letti);
//			}
			CopyStream.copy(is, bout);
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
//			char [] reads = new char[Utilities.DIMENSIONE_BUFFER];
//			int letti = 0;
//			StringBuilder bf = new StringBuilder();
//			while( (letti=reader.read(reads)) != -1 ){
//				bf.append(reads, 0, letti);
//			}
			StringWriter writer = new StringWriter();
			CopyCharStream.copy(reader, writer);
						
			return readObject_engine(writer.toString(), classType, elementsTypes);	
			
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
			
			Object oResult = this.xmlSerializer.read( object );
			Object en = null;
			if(oResult instanceof JSONObject){
				JSONObject jsonObject = (JSONObject)  oResult;
				en = jsonObject.get(jsonObject.keys().next());
			}else{
				JSONArray jsonArray = (JSONArray)  oResult;
				en = jsonArray.toArray()[0];
			}
			//System.out.println("TIPO ["+en.getClass().getName()+"]");
			Object [] o = classType.getEnumConstants();
			Class<?> enumeration = o[0].getClass();
			//System.out.println("OO ["+enumeration+"]");
			Method method = enumeration.getMethod("valueOf", String.class);
			Object value = method.invoke(enumeration, en);
			return value;
		}
		
		else if(classType.isArray()){

			Object oResult = this.xmlSerializer.read( object );
			if(oResult instanceof JSONObject){
				JSONObject jsonObject = (JSONObject)  oResult;
				Iterator<?> it = jsonObject.keys();
				Object[] array = new Object[jsonObject.values().size()];
				int i=0;
				while(it.hasNext()){
					Object key = it.next();
					Object value = jsonObject.get(key);
					JSONObject json = (JSONObject) value;
					array[i] =  fromJSONObjectToOriginalObject(classType.getComponentType(), json);
					i++;
				}
				return array;
			}else{
				JSONArray jsonArray = (JSONArray)  oResult;
				Object[] oarray = jsonArray.toArray();
				Object[] array = new Object[oarray.length];
				for(int i=0; i<oarray.length;i++){
					JSONObject json = (JSONObject) oarray[i];
					array[i] =  fromJSONObjectToOriginalObject(classType.getComponentType(), json);
				}
				return array;
			}

		}
		
		else if(list){
			
			if(elementsTypes==null){
				throw new Exception("elementsTypes non definito, e' obbligatorio per il tipo che si desidera de-serializzare: "+classType.getName());
			}
			
			Object oResult = this.xmlSerializer.read( object );
			if(oResult instanceof JSONObject){
				JSONObject jsonObject = (JSONObject)  oResult;
				Iterator<?> it = jsonObject.keys();
				@SuppressWarnings("unchecked")
				List<Object> listReturn = (List<Object>) ClassLoaderUtilities.newInstance(classType);		
				while(it.hasNext()){
					Object key = it.next();
					Object value = jsonObject.get(key);
					JSONObject json = (JSONObject) value;
					Object o =  fromJSONObjectToOriginalObject(elementsTypes, json);
					listReturn.add(o);
				}
				return listReturn;
			}else{
				JSONArray jsonArray = (JSONArray)  oResult;
				Object[] oarray = jsonArray.toArray();
				@SuppressWarnings("unchecked")
				List<Object> listReturn = (List<Object>) ClassLoaderUtilities.newInstance(classType);		
				for(int i=0; i<oarray.length;i++){
					JSONObject json = (JSONObject) oarray[i];
					Object o =  fromJSONObjectToOriginalObject(elementsTypes, json);
					listReturn.add(o);
				}
				return listReturn;
			}
			
		}
		
		else if(set){
			
			if(elementsTypes==null){
				throw new Exception("elementsTypes non definito, e' obbligatorio per il tipo che si desidera de-serializzare: "+classType.getName());
			}
			
			Object oResult = this.xmlSerializer.read( object );
			if(oResult instanceof JSONObject){
				JSONObject jsonObject = (JSONObject)  oResult;
				Iterator<?> it = jsonObject.keys();
				@SuppressWarnings("unchecked")
				Set<Object> setReturn = (Set<Object>) ClassLoaderUtilities.newInstance(classType);		
				while(it.hasNext()){
					Object key = it.next();
					Object value = jsonObject.get(key);
					JSONObject json = (JSONObject) value;
					Object o =  fromJSONObjectToOriginalObject(elementsTypes, json);
					setReturn.add(o);
				}
				return setReturn;
			}else{
				JSONArray jsonArray = (JSONArray)  oResult;
				Object[] oarray = jsonArray.toArray();
				@SuppressWarnings("unchecked")
				Set<Object> setReturn = (Set<Object>) ClassLoaderUtilities.newInstance(classType);			
				for(int i=0; i<oarray.length;i++){
					JSONObject json = (JSONObject) oarray[i];
					Object o =  fromJSONObjectToOriginalObject(elementsTypes, json);
					setReturn.add(o);
				}
				return setReturn;
			}
			
		}
		
		else{
			JSONObject jsonObject = (JSONObject) this.xmlSerializer.read( object );
			return fromJSONObjectToOriginalObject(classType, jsonObject);
		}
	}
	
	private Object fromJSONObjectToOriginalObject(Class<?> classType, JSONObject jsonObject) throws UtilsException{
		this.jsonConfig.setRootClass(classType);
		
		MorpherRegistry morpherRegistry = JSONUtils.getMorpherRegistry();
		org.openspcoop2.utils.serialization.Utilities.registerMorpher(classType, morpherRegistry, new ArrayList<String>(), this.morpherPackage);
		
		Object o = JSONObject.toBean( jsonObject, this.jsonConfig );

		org.openspcoop2.utils.serialization.Utilities.morpher(o, morpherRegistry, this.morpherPackage, this.throwExceptionMorpherFailed);
		
		return o;
	}
}
