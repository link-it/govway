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
package org.openspcoop2.utils.logger;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.constants.LowSeverity;
import org.openspcoop2.utils.logger.constants.Severity;

/**
 * DiagnosticManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DiagnosticManager {
		
	private static final String CONTEXT = "context.";
	private static final String DYNAMIC_OBJECT = "object.";
	
	private DiagnosticProperties diagnosticProperties;
	
	private IContext context;
	
	private ILogger loggerForCallback;
	
	protected DiagnosticManager(Properties diagnosticProperties, IContext context, boolean throwExceptionPlaceholderFailedResolution, ILogger loggerForCallback) throws UtilsException{
		this.diagnosticProperties = DiagnosticProperties.getInstance(diagnosticProperties, throwExceptionPlaceholderFailedResolution);
		this.context = context;
		
		this.loggerForCallback = loggerForCallback;

	}
	private DiagnosticManager(){} // for clone
	
	public DiagnosticManager clone(IContext newContext){
		
		DiagnosticManager dmNew = new DiagnosticManager();
		
		dmNew.context = newContext;
		
		dmNew.diagnosticProperties = this.diagnosticProperties.clone();
				
		dmNew.loggerForCallback = this.loggerForCallback;
		
		return dmNew;
	}
	
	public String getDefaultFunction() {
		return this.diagnosticProperties.getDefaultFunction();
	}
	
	public String getDefaultCode(String function, LowSeverity severity) throws UtilsException{
		if(this.diagnosticProperties.getMappingFunctionToCode().containsKey(function)==false){
			throw new UtilsException("Function ["+function+"] undefined");
		}
		return this.diagnosticProperties.getMappingFunctionToCode().get(function)  + this.diagnosticProperties.getMappingSeverityToCode().get(severity);
	}
	
	public String getCode(String code) throws UtilsException{
		if(this.diagnosticProperties.getMappingFullCodeToFullString().containsKey(code)){
			// è già il codice
			return code;
		}
		if(this.diagnosticProperties.getMappingStringCodeToDiagnostic().containsKey(code)){
			// è un codice stringa, ritorno il codice numerico
			return this.diagnosticProperties.getMappingStringCodeToDiagnostic().get(code).functionCode+this.diagnosticProperties.getMappingStringCodeToDiagnostic().get(code).code;
		}
		throw new UtilsException("Diagnostic with code ["+code+"] undefined");
	}
	
	public String getHumanCode(String code) throws UtilsException{
		if(this.diagnosticProperties.getMappingStringCodeToDiagnostic().containsKey(code)){
			// è già il codice human
			return code;
		}
		if(this.diagnosticProperties.getMappingFullCodeToFullString().containsKey(code)){
			return this.diagnosticProperties.getMappingFullCodeToFullString().get(code);
		}
		throw new UtilsException("Diagnostic with code ["+code+"] undefined");
	}
	
	public Severity getSeverity(String code) throws UtilsException{
		String codeString = null;
		if(this.diagnosticProperties.getMappingFullCodeToFullString().containsKey(code)){
			codeString = this.diagnosticProperties.getMappingFullCodeToFullString().get(code);
		}
		if(codeString==null){
			if(this.diagnosticProperties.getMappingStringCodeToDiagnostic().containsKey(code)){
				// è gia codice stringa
				codeString = code;
			}
		}
		if(codeString==null)
			throw new UtilsException("Diagnostic with code ["+code+"] undefined");
		return this.diagnosticProperties.getMappingStringCodeToDiagnostic().get(codeString).severity;
	}
	
	public String getMessage(String code, String ... params) throws UtilsException{
		String codeString = null;
		if(this.diagnosticProperties.getMappingFullCodeToFullString().containsKey(code)){
			codeString = this.diagnosticProperties.getMappingFullCodeToFullString().get(code);
		}
		if(codeString==null){
			if(this.diagnosticProperties.getMappingStringCodeToDiagnostic().containsKey(code)){
				// è gia codice stringa
				codeString = code;
			}
		}
		if(codeString==null)
			throw new UtilsException("Diagnostic with code ["+code+"] undefined");
		return this.replacePlaceholders(code, this.diagnosticProperties.getMappingStringCodeToDiagnostic().get(codeString).message,null,params);
	}
	
	public String getMessage(String code, Object o) throws UtilsException{
		String codeString = null;
		if(this.diagnosticProperties.getMappingFullCodeToFullString().containsKey(code)){
			codeString = this.diagnosticProperties.getMappingFullCodeToFullString().get(code);
		}
		if(codeString==null){
			if(this.diagnosticProperties.getMappingStringCodeToDiagnostic().containsKey(code)){
				// è gia codice stringa
				codeString = code;
			}
		}
		if(codeString==null)
			throw new UtilsException("Diagnostic with code ["+code+"] undefined");
		return this.replacePlaceholders(code, this.diagnosticProperties.getMappingStringCodeToDiagnostic().get(codeString).message,o);
	}
	
	public String getFunction(String code) throws UtilsException{
		String codeString = null;
		if(this.diagnosticProperties.getMappingFullCodeToFullString().containsKey(code)){
			codeString = this.diagnosticProperties.getMappingFullCodeToFullString().get(code);
		}
		if(codeString==null){
			if(this.diagnosticProperties.getMappingStringCodeToDiagnostic().containsKey(code)){
				// è gia codice stringa
				codeString = code;
			}
		}
		if(codeString==null)
			throw new UtilsException("Diagnostic with code ["+code+"] undefined");
		return this.diagnosticProperties.getMappingStringCodeToDiagnostic().get(codeString).functionString;
		
	}
	
	
	private String replacePlaceholders(String diagnostic, String msg, Object dinamycObject, String ... params) throws UtilsException{
		
		/* Potenzialmente N^2	
		String tmp = msg;
		Enumeration<String> keys = this.keywordLogPersonalizzati.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			tmp = tmp.replace(key, this.keywordLogPersonalizzati.get(key));
		}
		return tmp;
		*/
		
		// Check di esistenza di almeno 2 '${' e }
		if(msg!=null && msg.length()>2){
			int index1 = msg.indexOf("{");
			int index2 = msg.indexOf("}",index1+1);
			if(index1<0 || index2<0){
				return msg; // non serve il replace
			}
		}
		
		StringBuffer bf = new StringBuffer();
		StringBuffer keyword = new StringBuffer();
		boolean separator = false;
		for(int i=0; i<msg.length(); i++){
			char ch = msg.charAt(i);
			if( ('{' == ch) || ('}' == ch) ){
				//char separatorChar = ch;
				if(separator==false){
					// inizio keyword
					//keyword.append(separatorChar);
					separator = true;
				}
				else{
					// fine keyword
					//keyword.append(separatorChar);
					String valoreRimpiazzato = this.getValue(diagnostic, keyword.toString(), dinamycObject, params);
					if(valoreRimpiazzato==null){
						// keyword non esistente, devo utilizzare l'originale
						if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
							throw new UtilsException("Placeholder [{"+keyword.toString()+"}] resolution failed");
						}
						else{
							//bf.append("{"+keyword.toString()+"}");
							bf.append("(???Placeholder ["+"{"+keyword.toString()+"] return null value)");
						}
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
	
	private String getValue(String diagnostic, String key, Object dinamycObject, String ... params) throws UtilsException{
		if(key.startsWith(CONTEXT)){
			if(this.context==null){
				if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
					throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+key+"] required context");
				}
				else{
					return "(???Placeholder ["+key+"] required context)";
				}
			}
			return this.readValueInObject(diagnostic, this.context, key.substring(CONTEXT.length(), key.length()), key);
		}
		else if(key.startsWith(DYNAMIC_OBJECT)){
			if(dinamycObject==null){
				if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
					throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+key+"] required dynamic parameter");
				}
				else{
					return "(???Placeholder ["+key+"] required dynamic parameter)";
				}
			}
			return this.readValueInObject(diagnostic, dinamycObject, key.substring(DYNAMIC_OBJECT.length(), key.length()), key);
		}
		else{
			try{
				int intValue = Integer.parseInt(key);
				if(intValue<0){
					if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
						throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+key+"] unsupported");
					}
					else{
						return "(???Placeholder ["+key+"] unsupported)";
					}
				}
				if(params==null || params.length<=0){
					if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
						throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+key+"] required parameters");
					}
					else{
						return "(???Placeholder ["+key+"] required parameters)";
					}
				}
				if(intValue>=params.length){
					if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
						throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+key+"] grather then parameters size:"+params.length);
					}
					else{
						return "(???Placeholder ["+key+"] grather then parameters size:"+params.length+")";
					}
				}
				return params[intValue];
			}catch(Exception e){
				if(e instanceof UtilsException){
					throw (UtilsException) e;
				}
				else{
					
					// provo a chiamare callback
					try{
						if(this.loggerForCallback!=null){
							String value = this.loggerForCallback.getLogParam(key);
							if(value!=null){
								return value;
							}
						}
					}catch(Exception eCallback){
						if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
							throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+key+"] callback resolution failed: "+eCallback.getMessage(),eCallback);
						}
						else{
							return "(???Placeholder ["+key+"] callback resolution failed: "+eCallback.getMessage()+")";
						}
					}
					
					if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
						throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+key+"] unsupported: "+e.getMessage(),e);
					}
					else{
						return "(???Placeholder ["+key+"] unsupported: "+e.getMessage()+")";
					}
				}
			}
		}
	}
	private String readValueInObject(String diagnostic, Object o,String name, String placeholderOriginale) throws UtilsException{
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
				if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
					throw new UtilsException("Errore durante la comprensione del parametro di posizionamento ["+fieldName+"]: "+e.getMessage(),e);
				}
				else{
					return "(???Placeholder ["+placeholderOriginale+"] uncorrect, errore durante la comprensione del parametro di posizionamento ["+fieldName+"]: "+e.getMessage()+")";
				}
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
			if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
				throw new UtilsException("(diagnostic:"+diagnostic+") Method ["+o.getClass().getName()+"."+getMethod+"()] not found: "+e.getMessage(),e);
			}
			else{
				return "(???Placeholder ["+placeholderOriginale+"] Method ["+o.getClass().getName()+"."+getMethod+"()] not found: "+e.getMessage()+")";
			}
		}
		Object ret = null;
		try{
			ret = m.invoke(o);
		}catch(Throwable e){
			if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
				throw new UtilsException("(diagnostic:"+diagnostic+") Invocation method ["+o.getClass().getName()+"."+getMethod+"()] failed: "+e.getMessage(),e);
			}
			else{
				return "(???Placeholder ["+placeholderOriginale+"] Invocation method ["+o.getClass().getName()+"."+getMethod+"()] failed: "+e.getMessage()+")";
			}
		}
		if(ret!=null){
			if(ret instanceof List<?> || ret instanceof Map<?,?> || ret instanceof Object[]){
				if(position==null){
					if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
						throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object without position");
					}
					else{
						return "(???Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object without position)";
					}
				}
				//System.out.println("ARRAY ["+ret.getClass().getName()+"]");
				if(ret instanceof List<?> || ret instanceof Object[]){
					int index = -1;
					try{
						index = Integer.parseInt(position);
					}catch(Exception e){
						if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
							throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value (not integer?): "+e.getMessage()+" )");
						}
						else{
							return "(???Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value (not integer?): "+e.getMessage()+" )";
						}
					}
					if(ret instanceof List<?>){
						List<?> list = (List<?>) ret;
						if(list.size()<=index){
							if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
								throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value "+index+" (list size:"+list.size()+") )");
							}
							else{
								return "(???Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value "+index+" (list size:"+list.size()+") )";
							}
						}
						ret = list.get(index);
					}
					else if(ret instanceof Object[]){
						Object[] arrayObj = (Object[]) ret;
						if(arrayObj.length<=index){
							if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
								throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value "+index+" (array size:"+arrayObj.length+") )");
							}
							else{
								return "(???Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position value "+index+" (array size:"+arrayObj.length+") )";
							}
						}
						ret = arrayObj[index];
					}
				}
				else if(ret instanceof Map<?,?>){
					Map<?,?> map = (Map<?,?>) ret;
					if(map.containsKey(position)==false){
						if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
							throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position ["+position+"] not exists as key in map )");
						}
						else{
							return "(???Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return "+ret.getClass().getName()+" object, wrong position ["+position+"] not exists as key in map )";
						}
					}
					ret = map.get(position);
				}
				
				
			}
		}
		if(name.contains(".")){
			if(ret==null){
				if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
					throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return null object");
				}
				else{
					return "(???Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return null object)";
				}
			}
			else{
				return this.readValueInObject(diagnostic, ret, name.substring((fieldName+".").length(), name.length()), placeholderOriginale);
			}
		}
		else{
			// finale
			String finalValue = null;
			if(ret!=null){
				if(ret instanceof Date){
					SimpleDateFormat dateformat = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss.SSS"); // SimpleDateFormat non e' thread-safe
					finalValue = dateformat.format((Date)ret);
				}
				else if(ret instanceof byte[]){
					// 1024 = 1K
					 // Visualizzo al massimo 5K
					 int max = 5 * 1024;
					 return org.openspcoop2.utils.Utilities.convertToPrintableText((byte[])ret, max);
				}
				else{
					finalValue = ret.toString();
				}
			}
			else{
				if(this.diagnosticProperties.isThrowExceptionPlaceholderFailedResolution()){
					throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return null object");
				}
				else{
					return "(???Placeholder ["+placeholderOriginale+"] uncorrect, method ["+o.getClass().getName()+"."+getMethod+"()] return null object)";
				}
			}
			return finalValue;
		}
	}
	
}

class DiagnosticInfo {
	
	String functionString;
	String functionCode;
	String code;
	Severity severity;
	String message;
	
}
