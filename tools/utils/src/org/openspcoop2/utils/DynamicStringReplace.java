/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * DynamicStringReplace
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DynamicStringReplace {

	private static boolean containsKey(Map<?, ?> map, String key) {
		// NOTA: prima volutamente viene cercata la stringa esatta per supportare più entry
		boolean contains = map.containsKey(key) || map.containsKey(key.toLowerCase()) || map.containsKey(key.toUpperCase());
		if(contains) {
			return true;
		}
		else {
			Iterator<?> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				Object oKey = keys.next();
				if(oKey!=null && oKey instanceof String) {
					String keyIterator = (String) oKey;
					String keyIteratorCaseInsensitive = keyIterator.toLowerCase();
					String keyCaseInsensitive = key.toLowerCase();
					if(keyCaseInsensitive.equals( keyIteratorCaseInsensitive )) {
						return true;
					}
				}
			}
		}
		return false;
	}
	private static Object get(Map<?, ?> map, String key) {
		// NOTA: prima volutamente viene cercata la stringa esatta per supportare più entry
		if(map.containsKey(key)) {
			return map.get(key);
		}
		else if(map.containsKey(key.toLowerCase())) {
			return map.get(key.toLowerCase());
		}
		else if(map.containsKey(key.toUpperCase())) {
			return map.get(key.toUpperCase());
		}
		else {
			Iterator<?> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				Object oKey = keys.next();
				if(oKey!=null && oKey instanceof String) {
					String keyIterator = (String) oKey;
					String keyIteratorCaseInsensitive = keyIterator.toLowerCase();
					String keyCaseInsensitive = key.toLowerCase();
					if(keyCaseInsensitive.equals( keyIteratorCaseInsensitive )) {
						return map.get(oKey);
					}
				}
			}
		}
		return null;
	}
	
	public static void validate(String messaggioWithPlaceHolder, boolean startWithDollaro) throws UtilsException{
		
		String start = "{";
		String startOptional = "{";
		if(startWithDollaro) {
			start = "${"; 
			startOptional = "?{";
		}
		String end = "}";
		int length = start.length()+end.length();

		// Check di esistenza di almeno 1  '{' e '}'
		if(messaggioWithPlaceHolder!=null && messaggioWithPlaceHolder.length()>length){
			int index1 = messaggioWithPlaceHolder.indexOf(start);
			if(index1<0) {
				index1 = messaggioWithPlaceHolder.indexOf(startOptional);
			}
			int index2 = messaggioWithPlaceHolder.indexOf(end,index1+start.length());
			if(index1<0 || index2<0){
				return; // non serve il replace
			}
		}
		
		if(messaggioWithPlaceHolder==null) {
			return;
		}
		
		StringBuilder keyword = new StringBuilder();
		//boolean required = true;
		boolean keywordInCorso = false;
		for(int i=0; i<messaggioWithPlaceHolder.length(); i++){
			char ch = messaggioWithPlaceHolder.charAt(i);
			boolean checkPossibleStart = false;
			if(startWithDollaro) {
				if(ch == '$' || ch == '?') {
					//if(ch == '?') {
					//	required=false;
					//}
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
		String startOptional = "{";
		if(startWithDollaro) {
			start = "${"; 
			startOptional = "?{";
		}
		String end = "}";
		int length = start.length()+end.length();
		
		// Check di esistenza di almeno 1  '{' e '}'
		if(messaggioWithPlaceHolder!=null && messaggioWithPlaceHolder.length()>length){
			int index1 = messaggioWithPlaceHolder.indexOf(start);
			if(index1<0) {
				index1 = messaggioWithPlaceHolder.indexOf(startOptional);
			}
			int index2 = messaggioWithPlaceHolder.indexOf(end,index1+start.length());
			if(index1<0 || index2<0){
				return messaggioWithPlaceHolder; // non serve il replace
			}
		}
		
		if(messaggioWithPlaceHolder==null) {
			return messaggioWithPlaceHolder;
		}
		
		StringBuilder bf = new StringBuilder();
		StringBuilder keyword = new StringBuilder();
		boolean required = true;
		int countOptionalNotFoundBehaviour = 0;
		boolean keywordInCorso = false;
		for(int i=0; i<messaggioWithPlaceHolder.length(); i++){
			char ch = messaggioWithPlaceHolder.charAt(i);
			Character chDollaro = null; 
			boolean checkPossibleStart = false;
			if(startWithDollaro) {
				if(ch == '$' || ch == '?') {
					if(ch == '?') {
						required=false;
					}
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
					String valoreRimpiazzato = null;
					if(containsKey(map, tipo)==false) {
						if(required) {
							throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object with key '"+tipo+"' not found");
						}
						else {
							countOptionalNotFoundBehaviour++;
							valoreRimpiazzato = "";
						}
					}
					else {
						//Object object = map.get(tipo);
						Object object = get(map, tipo);
						if(object==null){
							if(required) {
								throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object with key '"+tipo+"' is null");
							}
							else {
								countOptionalNotFoundBehaviour++;
								valoreRimpiazzato = "";
							}
						}
						else {
							valoreRimpiazzato = getValue(key, tipo, value, object, complexField, required);
							if(valoreRimpiazzato==null){
								if(required) {
									throw new UtilsException("Placeholder [{"+key+"}] resolution failed");
								}
								else {
									countOptionalNotFoundBehaviour++;
									valoreRimpiazzato = "";
								}
							}
						}
					}
					bf.append(valoreRimpiazzato);
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
		String result = bf.toString();
		if(result==null) {
			return null;
		}
		if(countOptionalNotFoundBehaviour > 0 && StringUtils.isEmpty(result)) {
			return null;
		}
		return result;
	}
	
	private static String getValue(String key, String tipo, String value, Object object, boolean complexField, boolean required) throws UtilsException{
		
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
			boolean existsUlterioreParametro = false;
			boolean valueContainsPoint = value.contains(".");
			if(valueContainsPoint && complexField){
				
				// Devo convertire i punti dentro le quadre che potrebbero far parte delle chiavi
				StringBuilder sbFormat = new StringBuilder();
				boolean start = false;
				for (int i = 0; i < value.length(); i++) {
					char c = value.charAt(i);
					if((!start) && (c == '[')) {
						start = true;
					}
					if((start) && (c == ']')) {
						start = false;
					}
					if(start && c == '.') {
						sbFormat.append('_');
					}
					else {
						sbFormat.append(c);
					}
				}
				String internalNewValue = sbFormat.toString();
				
				// Devo convertire i punti dentro le tonde che potrebbero far parte delle chiavi
				sbFormat = new StringBuilder();
				start = false;
				for (int i = 0; i < internalNewValue.length(); i++) {
					char c = internalNewValue.charAt(i);
					if((!start) && (c == '(')) {
						start = true;
					}
					if((start) && (c == ')')) {
						start = false;
					}
					if(start && c == '.') {
						sbFormat.append('_');
					}
					else {
						sbFormat.append(c);
					}
				}
				internalNewValue = sbFormat.toString();
				
				//System.out.println("OLD ["+value+"]");
				//System.out.println("NEW ["+internalNewValue+"]");
				
				int indexOfPoint = internalNewValue.indexOf(".");
				if(indexOfPoint>0) {
					position = value.substring(0, indexOfPoint);
					try{
						newValue = value.substring(indexOfPoint+1);
					}catch(Throwable e){
						// ignore
					}
				}
				else {
					position = value;
					newValue = null;
				}
				if(position!=null) {
					if( (position.startsWith("[") || position.startsWith("("))
							&& 
							position.length()>1) {
						position = position.substring(1);
					}
					if( (position.endsWith("]") || position.endsWith(")"))
							&& 
							position.length()>1) {
						position = position.substring(0,(position.length()-1));
					}
					//System.out.println("position ["+position+"]");
				}
				if(newValue!=null && StringUtils.isNotEmpty(newValue.trim())) {
					existsUlterioreParametro=true;
				}
			}
			
			
			
			Object oInternal = null;
			
			// Provo ad utilizzare  il valore come indice
			if(object instanceof List<?> || object instanceof Object[]){
				int index = -1;
				try{
					index = Integer.parseInt(position);
				}catch(Exception e){
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position '"+position+"' (not integer?): "+e.getMessage());
				}
				if(object instanceof List<?>){
					List<?> list = (List<?>) object;
					if(list.size()<=index){
						if(required) {
							throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position '"+index+"' (list size:"+list.size()+")");
						}
					}
					else {
						oInternal = list.get(index);
					}
				}
				else if(object instanceof Object[]){
					Object[] arrayObj = (Object[]) object;
					if(arrayObj.length<=index){
						if(required) {
							throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] with wrong position '"+index+"' (array size:"+arrayObj.length+")");
						}
					}
					else {
						oInternal = arrayObj[index];
					}
				}
			}
			else if(object instanceof Map<?,?>){
				Map<?,?> map = (Map<?,?>) object;
				//if(map.containsKey(position)==false){
				if(containsKey(map, position)==false) {
					if(required) {
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"] '"+position+"' not exists in map");
					}
				}
				else {
					//oInternal = map.get(position);
					oInternal = get(map, position);
				}
			}	
			
			//if(newValue.contains(".") || (newValue.contains("[") && newValue.contains("]"))){
			if(existsUlterioreParametro) { // se ho definito un parametro dopo la chiave della mappa devo usare il metodo 'readValueInObject'
				if(oInternal==null){
					if(required) {
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"]["+position+"] return null object");
					}
					else {
						return null;
					}
				}
				else{
					return readValueInObject(key, oInternal, newValue, complexField, required);
				}
			}
			else{
				// finale
				if(oInternal!=null){
					String finalValue = null;
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
					return finalValue;
				}
				else{
					if(required) {
						throw new UtilsException("Placeholder [{"+key+"}] resolution failed: object ["+object.getClass().getName()+"]["+position+"] return null object");
					}
					else {
						return null;
					}
				}
			}	
			
		}
		else{
			
			// Altrimenti provo a navigarlo
			return readValueInObject(key, object, value, complexField, required);
		
		}
		
	}
	private static String readValueInObject(String key, Object o,String name, boolean complexField, boolean required) throws UtilsException{
		//System.out.println("Invocato con oggetto["+o.getClass().getName()+"] nome["+name+"]");
		String fieldName = name;
		//String position = null;
		List<String> arrayMapPosition = new ArrayList<String>();
		boolean nameContainsPoint = name.contains(".");
		if(nameContainsPoint && complexField){
			
			// Devo convertire i punti dentro le quadre che potrebbero far parte delle chiavi
			StringBuilder sbFormat = new StringBuilder();
			boolean start = false;
			for (int i = 0; i < name.length(); i++) {
				char c = name.charAt(i);
				if((!start) && (c == '[')) {
					start = true;
				}
				if((start) && (c == ']')) {
					start = false;
				}
				if(start && c == '.') {
					sbFormat.append('_');
				}
				else {
					sbFormat.append(c);
				}
			}
			String newName = sbFormat.toString();
			
			// Devo convertire i punti dentro le tonde che potrebbero far parte delle chiavi
			sbFormat = new StringBuilder();
			start = false;
			int doppieVirgoletteIndex = -1;
			boolean startDoppieVirgolette = false;
			for (int i = 0; i < newName.length(); i++) {
				char c = newName.charAt(i);
				if((!start) && (c == '(')) {
					start = true;
					if((i+1 < newName.length())) {
						char cc = newName.charAt(i+1);
						if((cc == '"')) {
							startDoppieVirgolette=true;
							doppieVirgoletteIndex=i+1;
						}
					}
				}
				if((start) && (c == ')')) {
					if(!startDoppieVirgolette) {
						start = false;
					}
				}
				if((startDoppieVirgolette) && (c == '"') && doppieVirgoletteIndex!=i) {
					startDoppieVirgolette = false;
					doppieVirgoletteIndex=i;
				}
				if(start && c == '.') {
					sbFormat.append('_');
				}
				else {
					sbFormat.append(c);
				}
			}
			newName = sbFormat.toString();
			
			//System.out.println("OLD ["+name+"]");
			//System.out.println("NEW ["+newName+"]");
			nameContainsPoint = newName.contains(".");
			if(nameContainsPoint){
				fieldName = name.substring(0, newName.indexOf("."));
			}
			//System.out.println("fieldName ["+fieldName+"]");
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
				//System.out.println("METODO getMethod["+getMethod+"] PARAMS["+sP+"] ");
				if(sP.contains(",")) {
					parametersObject = new ArrayList<String>();
					parametersClass = new ArrayList<Class<?>>();
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < sP.length(); i++) {
						String sAt = sP.charAt(i)+"";
						if(sAt.equals(",")) {
							String paramString = sb.toString();
							//System.out.println("paramString '"+paramString+"'");
							if(paramString!=null && paramString.length()>=2 && paramString.startsWith("\"") && paramString.endsWith("\"")) {
								if(paramString.length()==2) {
									paramString = ""; // stringa vuota
								}
								else {
									paramString = paramString.substring(1);
									paramString = paramString.substring(0,paramString.length()-1);
								}
								//System.out.println("paramString DOPO '"+paramString+"'");
							}
							parametersObject.add(paramString); // gli spazi possono essere voluti (non fare trim)
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
					String paramString = sP;
					//System.out.println("paramString '"+paramString+"'");
					if(paramString!=null && paramString.length()>=2 && paramString.startsWith("\"") && paramString.endsWith("\"")) {
						if(paramString.length()==2) {
							paramString = ""; // stringa vuota
						}
						else {
							paramString = paramString.substring(1);
							paramString = paramString.substring(0,paramString.length()-1);
						}
						//System.out.println("paramString DOPO '"+paramString+"'");
					}
					parametersObject.add(paramString); // gli spazi possono essere voluti (non fare trim)
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
			
			if(utilsException!=null && e instanceof java.lang.NoSuchMethodException && !withoutParams) {
				// Fix per metodi boolean senza 'get' o 'is' che contengono parametri
				try{
					String originalMethod = methodName;
					if(originalMethod.contains("(") && originalMethod.endsWith(")")) { // supporta 1 solo parametro stringa
						int startParams = originalMethod.indexOf("(");
						originalMethod = originalMethod.substring(0, startParams);
					}
					
					if(parametersClass!=null && !parametersClass.isEmpty()) {
						m = o.getClass().getMethod(originalMethod, parametersClass.toArray(new Class[1]));
					}
					// non si dovrebbe entrare in questo caso essendoci l'if sopra con !withoutParams
//					else {
//						m = o.getClass().getMethod(originalMethod);
//					}
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
		String debugParameters = "";
		Object ret = null;
		try{
			if(parametersObject!=null && !parametersObject.isEmpty()) {
				StringBuilder sb = new StringBuilder();
				for (String par : parametersObject) {
					if(sb.length()>0) {
						sb.append(",");
					}
					sb.append(par);
				}
				debugParameters=sb.toString();
				ret = m.invoke(o, parametersObject.toArray(new Object[1]));
			}
			else {
				ret = m.invoke(o);
			}
		}catch(Throwable e){
			Throwable tInternal = Utilities.getInnerNotEmptyMessageException(e);
			String msgErrore = tInternal!=null ? tInternal.getMessage() : e.getMessage();
			throw new UtilsException("Placeholder [{"+key+"}] resolution failed: invocation method ["+o.getClass().getName()+"."+getMethod+"("+debugParameters+")] failed; "+msgErrore,e);
		}
		
		if(ret!=null){
			if(ret instanceof List<?> || ret instanceof Map<?,?> || ret instanceof Object[]){
				if(arrayMapPosition==null || arrayMapPosition.isEmpty()){
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"("+debugParameters+")] return "+ret.getClass().getName()+" object without position");
				}
				while(!arrayMapPosition.isEmpty()) {
					String position = arrayMapPosition.remove(0);
					//System.out.println("ARRAY ["+ret.getClass().getName()+"]");
					if(ret instanceof List<?> || ret instanceof Object[]){
						int index = -1;
						try{
							index = Integer.parseInt(position);
						}catch(Exception e){
							throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"("+debugParameters+")] return "+ret.getClass().getName()+" object, wrong position value (not integer?): "+e.getMessage());
						}
						if(ret instanceof List<?>){
							List<?> list = (List<?>) ret;
							if(list.size()<=index){
								if(required) {
									throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"("+debugParameters+")] return "+ret.getClass().getName()+" object, wrong position '"+index+"' (list size:"+list.size()+")");
								}
								else {
									ret = null;
								}
							}
							else {
								ret = list.get(index);
							}
						}
						else if(ret instanceof Object[]){
							Object[] arrayObj = (Object[]) ret;
							if(arrayObj.length<=index){
								if(required) {
									throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"("+debugParameters+")] return "+ret.getClass().getName()+" object, wrong position '"+index+"' (array size:"+arrayObj.length+")");
								}
								else {
									ret = null;
								}
							}
							else {
								ret = arrayObj[index];
							}
						}
					}
					else if(ret instanceof Map<?,?>){
						Map<?,?> map = (Map<?,?>) ret;
						//if(map.containsKey(position)==false){
						if(containsKey(map, position)==false) {
							if(required) {
								throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"("+debugParameters+")] return "+ret.getClass().getName()+" object, '"+position+"' not exists in map");
							}
							else {
								ret = null;
							}
						}
						else {
							//ret = map.get(position);
							ret = get(map, position);
						}
					}		
				}
			}
		}
		if(nameContainsPoint && complexField){
			if(ret==null){
				if(required) {
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"("+debugParameters+")] return null object");
				}
				else {
					return null;
				}
			}
			else{
				return readValueInObject(key, ret, name.substring((fieldName+".").length(), name.length()), complexField, required);
			}
		}
		else{
			// finale
			if(ret!=null){
				String finalValue = null;
				if(ret instanceof Date){
					SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
					finalValue = dateformat.format((Date)ret);
				}
				else if(ret instanceof byte[]){
//					// 1024 = 1K
//					 // Visualizzo al massimo 1K
//					 int max = 1 * 1024;
//					 return org.openspcoop2.utils.Utilities.convertToPrintableText((byte[])ret, max);
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"("+debugParameters+")] return object with type ["+ret.getClass().getName()+"]");
				}
				else{
					finalValue = ret.toString();
				}
				return finalValue;
			}
			else{
				if(required) {
					throw new UtilsException("Placeholder [{"+key+"}] resolution failed: method ["+o.getClass().getName()+"."+getMethod+"("+debugParameters+")] return null object");
				}
				else {
					return null;
				}
			}
		}
	}
}
