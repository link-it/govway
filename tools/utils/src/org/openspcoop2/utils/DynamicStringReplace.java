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
package org.openspcoop2.utils;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
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
		
		StringBuffer keyword = new StringBuffer();
		boolean separator = false;
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
					('}' == ch) 
				){
				//char separatorChar = ch;
				if(separator==false){
					// inizio keyword
					//keyword.append(separatorChar);
					separator = true;
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
					separator=false;
				}
			}else{
				if(separator){
					// sto scrivendo la keyword
					keyword.append(ch);
				}
			}
		}
	}
	
	public static String replace(String messaggioWithPlaceHolder, Map<String, Object> map, boolean startWithDollaro) throws UtilsException{
		
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
		
		StringBuffer bf = new StringBuffer();
		StringBuffer keyword = new StringBuffer();
		boolean separator = false;
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
					('}' == ch) 
				){
				//char separatorChar = ch;
				if(separator==false){
					// inizio keyword
					//keyword.append(separatorChar);
					separator = true;
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
					if(map.containsKey(tipo)==false){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object with key ["+tipo+"] not found");
					}
					Object object = map.get(tipo);
					if(object==null){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object with key ["+tipo+"] is null");
					}
					String valoreRimpiazzato = getValue(key, tipo, value, object);
					if(valoreRimpiazzato==null){
						// keyword non esistente, devo utilizzare l'originale
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed");
					}else{
						bf.append(valoreRimpiazzato);
					}
					keyword.delete(0, keyword.length());
					separator=false;
				}
			}else{
				if(separator){
					// sto scrivendo la keyword
					keyword.append(ch);
				}else{
					bf.append(ch);
				}
			}
		}
		return bf.toString();
	}
	
	private static String getValue(String key, String tipo, String value, Object object) throws UtilsException{
		
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
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position position ["+position+"] (not integer?): "+e.getMessage()+" )");
				}
				if(object instanceof List<?>){
					List<?> list = (List<?>) object;
					if(list.size()<=index){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position position "+index+" (list size:"+list.size()+") )");
					}
					oInternal = list.get(index);
				}
				else if(object instanceof Object[]){
					Object[] arrayObj = (Object[]) object;
					if(arrayObj.length<=index){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position position "+index+" (array size:"+arrayObj.length+") )");
					}
					oInternal = arrayObj[index];
				}
			}
			else if(object instanceof Map<?,?>){
				Map<?,?> map = (Map<?,?>) object;
				if(map.containsKey(position)==false){
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position ["+position+"] not exists as key in map )");
				}
				oInternal = map.get(position);
			}	
			
			if(newValue.contains(".")){
				if(oInternal==null){
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"]["+position+"] return null object");
				}
				else{
					return readValueInObject(key, oInternal, newValue);
				}
			}
			else{
				// finale
				String finalValue = null;
				if(oInternal!=null){
					if(oInternal instanceof Date){
						SimpleDateFormat dateformat = new SimpleDateFormat ("yyyyMMdd_HHmmssSSS"); // SimpleDateFormat non e' thread-safe
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
			return readValueInObject(key, object, value);
		
		}
		
	}
	private static String readValueInObject(String key, Object o,String name) throws UtilsException{
		//System.out.println("Invocato con oggetto["+o.getClass().getName()+"] nome["+name+"]");
		String fieldName = name;
		String position = null;
		if(name.contains(".")){
			fieldName = name.substring(0, name.indexOf("."));
		}
		String methodName = new String(fieldName);
		if(fieldName.endsWith("]") && fieldName.contains("[")){
			try{
				position = fieldName.substring(fieldName.indexOf("[")+1,fieldName.length()-1);
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
		Method m = null;
		try{
			m = o.getClass().getMethod(getMethod);
		}catch(Throwable e){
			throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] not found; "+e.getMessage(),e);
		}
		Object ret = null;
		try{
			ret = m.invoke(o);
		}catch(Throwable e){
			throw new UtilsException("Placeholder [{"+key+"}] resolution failed: invocation method ["+o.getClass().getName()+"."+getMethod+"()] failed; "+e.getMessage(),e);
		}
		
		if(ret!=null){
			if(ret instanceof List<?> || ret instanceof Map<?,?> || ret instanceof Object[]){
				if(position==null){
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object without position");
				}
				//System.out.println("ARRAY ["+ret.getClass().getName()+"]");
				if(ret instanceof List<?> || ret instanceof Object[]){
					int index = -1;
					try{
						index = Integer.parseInt(position);
					}catch(Exception e){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value (not integer?): "+e.getMessage()+" )");
					}
					if(ret instanceof List<?>){
						List<?> list = (List<?>) ret;
						if(list.size()<=index){
							throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value "+index+" (list size:"+list.size()+") )");
						}
						ret = list.get(index);
					}
					else if(ret instanceof Object[]){
						Object[] arrayObj = (Object[]) ret;
						if(arrayObj.length<=index){
							throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value "+index+" (array size:"+arrayObj.length+") )");
						}
						ret = arrayObj[index];
					}
				}
				else if(ret instanceof Map<?,?>){
					Map<?,?> map = (Map<?,?>) ret;
					if(map.containsKey(position)==false){
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position ["+position+"] not exists as key in map )");
					}
					ret = map.get(position);
				}				
			}
		}
		if(name.contains(".")){
			if(ret==null){
				throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"()] return null object");
			}
			else{
				return readValueInObject(key, ret, name.substring((fieldName+".").length(), name.length()));
			}
		}
		else{
			// finale
			String finalValue = null;
			if(ret!=null){
				if(ret instanceof Date){
					SimpleDateFormat dateformat = new SimpleDateFormat ("yyyyMMdd_HHmmssSSS"); // SimpleDateFormat non e' thread-safe
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
