/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.utils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * DynamicStringReplace
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicStringReplace {

	private static boolean containsKey(Map<?, ?> map, String key) {
		return map.containsKey(key) || map.containsKey(key.toLowerCase()) || map.containsKey(key.toUpperCase());
	}
	private static Object get(Map<?, ?> map, String key) {
		if(map.containsKey(key)) {
			return map.get(key);
		}
		else if(map.containsKey(key.toLowerCase())) {
			return map.get(key.toLowerCase());
		}
		else if(map.containsKey(key.toUpperCase())) {
			return map.get(key.toUpperCase());
		}
		return null;
	}
	
	public static void validate(String messaggioWithPlaceHolder, boolean startWithDollaro) throws UtilsException{
		
		String start = "{";
		if(startWithDollaro) {
			start = "${";
		}
		String end = "}";
		int length = start.length()+end.length();

		// Check di esistenza di almeno 1  '{' e '}'
		if(messaggioWithPlaceHolder!=null && messaggioWithPlaceHolder.length()>length){
			int index1 = messaggioWithPlaceHolder.indexOf(start);
			int index2 = messaggioWithPlaceHolder.indexOf(end,index1+start.length());
			if(index1<0 || index2<0){
				return; // non serve il replace
			}
		}
		
		StringBuilder keyword = new StringBuilder();
		boolean keywordInCorso = false;
		for(int i=0; i<messaggioWithPlaceHolder.length(); i++){
			char ch = messaggioWithPlaceHolder.charAt(i);
			boolean checkPossibleStart = false;
			if(startWithDollaro) {
				if(ch == '$') {
					checkPossibleStart = true;
					if(i+1 < messaggioWithPlaceHolder.length()) {
						i++;
						ch = messaggioWithPlaceHolder.charAt(i);
					}
				}
			}
			else {
				checkPossibleStart = true;
			}
			if( 
					( (checkPossibleStart && '{' == ch) ) 
					|| 
					( (keywordInCorso && '}' == ch) ) 
				){
				//char separatorChar = ch;
				if(keywordInCorso==false){
					// inizio keyword
					//keyword.append(separatorChar);
					keywordInCorso = true; // siamo nel caso di apertura, inizio keyword
				}
				else{
					// fine keyword
					//keyword.append(separatorChar);
					String key = keyword.toString();
					if(key.contains(":")==false){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: wrong format, required {key:value}");
					}
					if(key.startsWith(":") || key.endsWith(":") ){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: wrong format, required {key:value}");
					}
					String tipo = key.substring(0, key.indexOf(":"));
					String value = key.substring(key.indexOf(":")+1);
					if(tipo==null || "".equals(tipo.trim())){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: wrong format, required {key:value}");
					}
					if(value==null || "".equals(value.trim())){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: wrong format, required {key:value}");
					}
					
					keyword.delete(0, keyword.length());
					keywordInCorso=false;
					checkPossibleStart=false;
				}
			}else{
				if(keywordInCorso){
					// sto scrivendo la keyword
					keyword.append(ch);
				}
			}
		}
	}
	
	public static String replace(String messaggioWithPlaceHolder, Map<String, Object> map, boolean startWithDollaro) throws UtilsException{
		return replace(messaggioWithPlaceHolder, map, startWithDollaro, true);
	}
	public static String replace(String messaggioWithPlaceHolder, Map<String, Object> map, boolean startWithDollaro, boolean complexField) throws UtilsException{
		
		validate(messaggioWithPlaceHolder, startWithDollaro);
		
		String start = "{";
		if(startWithDollaro) {
			start = "${";
		}
		String end = "}";
		int length = start.length()+end.length();
		
		// Check di esistenza di almeno 1  '{' e '}'
		if(messaggioWithPlaceHolder!=null && messaggioWithPlaceHolder.length()>length){
			int index1 = messaggioWithPlaceHolder.indexOf(start);
			int index2 = messaggioWithPlaceHolder.indexOf(end,index1+start.length());
			if(index1<0 || index2<0){
				return messaggioWithPlaceHolder; // non serve il replace
			}
		}
		
		StringBuilder bf = new StringBuilder();
		StringBuilder keyword = new StringBuilder();
		boolean keywordInCorso = false;
		for(int i=0; i<messaggioWithPlaceHolder.length(); i++){
			char ch = messaggioWithPlaceHolder.charAt(i);
			Character chDollaro = null; 
			boolean checkPossibleStart = false;
			if(startWithDollaro) {
				if(ch == '$') {
					checkPossibleStart = true;
					if(i+1 < messaggioWithPlaceHolder.length()) {
						i++;
						chDollaro = ch;
						ch = messaggioWithPlaceHolder.charAt(i);
					}
				}
			}
			else {
				checkPossibleStart = true;
			}
			if( 
					( (checkPossibleStart && '{' == ch) ) 
					|| 
					( (keywordInCorso && '}' == ch) ) 
				){
				//char separatorChar = ch;
				if(keywordInCorso==false){
					// inizio keyword
					//keyword.append(separatorChar);
					keywordInCorso = true;  // siamo nel caso di apertura, inizio keyword
				}
				else{
					// fine keyword
					//keyword.append(separatorChar);
					String key = keyword.toString();
					if(key.contains(":")==false){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: wrong format, required {key:value}");
					}
					if(key.startsWith(":") || key.endsWith(":") ){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: wrong format, required {key:value}");
					}
					String tipo = key.substring(0, key.indexOf(":"));
					String value = key.substring(key.indexOf(":")+1);
					//if(map.containsKey(tipo)==false){
					if(containsKey(map, tipo)==false) {
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object with key ["+tipo+"] not found");
					}
					//Object object = map.get(tipo);
					Object object = get(map, tipo);
					if(object==null){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object with key ["+tipo+"] is null");
					}
					String valoreRimpiazzato = getValue(key, tipo, value, object, complexField);
					if(valoreRimpiazzato==null){
						// keyword non esistente, devo utilizzare l'originale
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed");
					}else{
						bf.append(valoreRimpiazzato);
					}
					keyword.delete(0, keyword.length());
					keywordInCorso=false;
					checkPossibleStart=false;
				}
			}else{
				if(keywordInCorso){
					// sto scrivendo la keyword
					keyword.append(ch);
				}else{
					if(chDollaro!=null) {
						bf.append(chDollaro);
					}
					bf.append(ch);
				}
			}
		}
		return bf.toString();
	}
	
	private static String getValue(String key, String tipo, String value, Object object, boolean complexField) throws UtilsException{
		
		// Se l'oggetto nella mappa è una data utilizzo il valore come format
		Date d = null;
		if(object instanceof Date){
			d = (Date) object;
		}
		else if(object instanceof java.sql.Date){
			d = (java.sql.Date) object;
		}
		else if(object instanceof Calendar){
			Calendar c = (Calendar) object;
			d = c.getTime();
		}
		if(d!=null){
			SimpleDateFormat sdf = new SimpleDateFormat(value);
			return sdf.format(d);
		}
		
		// Se l'oggetto è una mappa provo ad estrarre l'oggetto indicato.
		if(object instanceof List<?> || object instanceof Map<?,?> || object instanceof Object[]){
		
			String position = value;
			String newValue = value;
			if(value.contains(".")){
				position = value.substring(0, value.indexOf("."));
				try{
					newValue = value.substring(value.indexOf(".")+1);
				}catch(Exception e){}
			}
			
			Object oInternal = null;
			
			// Provo ad utilizzare  il valore come indice
			if(object instanceof List<?> || object instanceof Object[]){
				int index = -1;
				try{
					index = Integer.parseInt(position);
				}catch(Exception e){
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position position ["+position+"] (not integer?): "+e.getMessage());
				}
				if(object instanceof List<?>){
					List<?> list = (List<?>) object;
					if(list.size()<=index){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position position "+index+" (list size:"+list.size()+")");
					}
					oInternal = list.get(index);
				}
				else if(object instanceof Object[]){
					Object[] arrayObj = (Object[]) object;
					if(arrayObj.length<=index){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position position "+index+" (array size:"+arrayObj.length+")");
					}
					oInternal = arrayObj[index];
				}
			}
			else if(object instanceof Map<?,?>){
				Map<?,?> map = (Map<?,?>) object;
				//if(map.containsKey(position)==false){
				if(containsKey(map, position)==false) {
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position ["+position+"] not exists as key in map");
				}
				//oInternal = map.get(position);
				oInternal = get(map, position);
			}	
			
			if(newValue.contains(".") || (newValue.contains("[") && newValue.contains("]"))){
				if(oInternal==null){
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"]["+position+"] return null object");
				}
				else{
					return readValueInObject(key, oInternal, newValue, complexField);
				}
			}
			else{
				// finale
				String finalValue = null;
				if(oInternal!=null){
					if(oInternal instanceof Date){
						SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
						finalValue = dateformat.format((Date)oInternal);
					}
					else if(oInternal instanceof byte[]){
//						// 1024 = 1K
//						 // Visualizzo al massimo 1K
//						 int max = 1 * 1024;
//						 return org.openspcoop2.utils.Utilities.convertToPrintableText((byte[])ret, max);
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"]["+position+"] return object with type ["+oInternal.getClass().getName()+"]");
					}
					else{
						finalValue = oInternal.toString();
					}
				}
				else{
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"]["+position+"] return null object");
				}
				return finalValue;
			}	
			
		}
		else{
			
			// Altrimenti provo a navigarlo
			return readValueInObject(key, object, value, complexField);
		
		}
		
	}
	private static String readValueInObject(String key, Object o,String name, boolean complexField) throws UtilsException{
		//System.out.println("Invocato con oggetto["+o.getClass().getName()+"] nome["+name+"]");
		String fieldName = name;
		//String position = null;
		List<String> arrayMapPosition = new ArrayList<String>();
		if(name.contains(".") && complexField){
			fieldName = name.substring(0, name.indexOf("."));
		}
		String methodName = new String(fieldName);
		if(fieldName.endsWith("]") && fieldName.contains("[")){
			try{
				// fix [][]
				//position = fieldName.substring(fieldName.indexOf("[")+1,fieldName.length()-1);
				String tmp = new String(fieldName);
				//System.out.println("DEBUG ["+tmp+"]");
				while(tmp.endsWith("]") && tmp.contains("[")){
					int firstOpen = tmp.indexOf("[")+1;
					int lastOpen = tmp.indexOf("]", firstOpen);
					String position = tmp.substring(firstOpen, lastOpen);
					arrayMapPosition.add(position);
					//System.out.println("DEBUG ADD ["+position+"]");
					if(tmp.length()>(lastOpen+1)) {
						tmp = tmp.substring(lastOpen+1,tmp.length());
					}
					else {
						tmp="";
					}
					//System.out.println("DEBUG NUOVO VALORE ["+tmp+"]");
				}
				methodName = fieldName.substring(0, fieldName.indexOf("["));
			}catch(Exception e){
				throw new UtilsException("Placeholder [{"+key+"}] resolution failed: position error in field ["+fieldName+"]: "+e.getMessage(),e);
			}
		}
		//System.out.println("NAME ["+fieldName+"] position["+position+"]");
		String getMethod = "get"+((methodName.charAt(0)+"").toUpperCase());
		if(methodName.length()>1){
			getMethod = getMethod + methodName.substring(1);
		}
		//System.out.println("getMethod ["+getMethod+"]");
		List<String> parametersObject = null;
		List<Class<?>> parametersClass = null;
		try{
			if(getMethod.contains("(") && getMethod.endsWith(")")) { // supporta 1 solo parametro stringa
				int startParams = getMethod.indexOf("(");
				
				String sP = getMethod.substring(startParams, getMethod.length());
				if(sP.length()>2) {
					sP = sP.substring(1, sP.length()-1);
				}
				if(sP.contains(",")) {
					parametersObject = new ArrayList<String>();
					parametersClass = new ArrayList<Class<?>>();
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < sP.length(); i++) {
						String sAt = sP.charAt(i)+"";
						if(sAt.equals(",")) {
							parametersObject.add(sb.toString()); // gli spazi possono essere voluti (non fare trim)
							parametersClass.add(String.class);
							sb = new StringBuilder();
						}
						else {
							sb.append(sAt);
						}
					}
					parametersObject.add(sb.toString()); // gli spazi possono essere voluti (non fare trim)
					parametersClass.add(String.class);
				}
				else {
					parametersObject = new ArrayList<String>();
					parametersClass = new ArrayList<Class<?>>();
					parametersObject.add(sP);
					parametersClass.add(String.class);
				}
				getMethod = getMethod.substring(0, startParams);
			}
		}catch(Throwable e){
			throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] read parameter error; "+e.getMessage(),e);
		}
		Method m = null;
		boolean withoutParams = false;
		try{
			if(parametersClass!=null && !parametersClass.isEmpty()) {
				m = o.getClass().getMethod(getMethod, parametersClass.toArray(new Class[1]));
			}
			else {
				withoutParams = true;
				m = o.getClass().getMethod(getMethod);
			}
		}catch(Throwable e){
			
			UtilsException utilsException = new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] not found; "+e.getMessage(),e);
			
			// Fix per metodi boolean 'is'
			if(e instanceof java.lang.NoSuchMethodException && withoutParams) {
				try{
					String isMethod = getMethod.replaceFirst("get", "is");
					m = o.getClass().getMethod(isMethod);
					if(m!=null) {
						utilsException = null;
					}
				}catch(Throwable eIgnore){ 
					// rilancio eccezione originale
				}
			}
			
			if(utilsException!=null) {
				throw utilsException;
			}
		}
		Object ret = null;
		try{
			if(parametersObject!=null && !parametersObject.isEmpty()) {
				ret = m.invoke(o, parametersObject.toArray(new Object[1]));
			}
			else {
				ret = m.invoke(o);
			}
		}catch(Throwable e){
			throw new UtilsException("Placeholder [{"+key+"}] resolution failed: invocation method ["+o.getClass().getName()+"."+getMethod+"()] failed; "+e.getMessage(),e);
		}
		
		if(ret!=null){
			if(ret instanceof List<?> || ret instanceof Map<?,?> || ret instanceof Object[]){
				if(arrayMapPosition==null || arrayMapPosition.isEmpty()){
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object without position");
				}
				while(!arrayMapPosition.isEmpty()) {
					String position = arrayMapPosition.remove(0);
					//System.out.println("ARRAY ["+ret.getClass().getName()+"]");
					if(ret instanceof List<?> || ret instanceof Object[]){
						int index = -1;
						try{
							index = Integer.parseInt(position);
						}catch(Exception e){
							throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value (not integer?): "+e.getMessage());
						}
						if(ret instanceof List<?>){
							List<?> list = (List<?>) ret;
							if(list.size()<=index){
								throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value "+index+" (list size:"+list.size()+")");
							}
							ret = list.get(index);
						}
						else if(ret instanceof Object[]){
							Object[] arrayObj = (Object[]) ret;
							if(arrayObj.length<=index){
								throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value "+index+" (array size:"+arrayObj.length+")");
							}
							ret = arrayObj[index];
						}
					}
					else if(ret instanceof Map<?,?>){
						Map<?,?> map = (Map<?,?>) ret;
						//if(map.containsKey(position)==false){
						if(containsKey(map, position)==false) {
							throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position ["+position+"] not exists as key in map )");
						}
						//ret = map.get(position);
						ret = get(map, position);
					}		
				}
			}
		}
		if(name.contains(".") && complexField){
			if(ret==null){
				throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return null object");
			}
			else{
				return readValueInObject(key, ret, name.substring((fieldName+".").length(), name.length()), complexField);
			}
		}
		else{
			// finale
			String finalValue = null;
			if(ret!=null){
				if(ret instanceof Date){
					SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
					finalValue = dateformat.format((Date)ret);
				}
				else if(ret instanceof byte[]){
//					// 1024 = 1K
//					 // Visualizzo al massimo 1K
//					 int max = 1 * 1024;
//					 return org.openspcoop2.utils.Utilities.convertToPrintableText((byte[])ret, max);
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return object with type ["+ret.getClass().getName()+"]");
				}
				else{
					finalValue = ret.toString();
				}
			}
			else{
				throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return null object");
			}
			return finalValue;
		}
	}
}
