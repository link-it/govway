/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package org.openspcoop2.utils.logger.basic;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.IContext;
import org.openspcoop2.utils.logger.ILogger;
import org.openspcoop2.utils.logger.LowSeverity;
import org.openspcoop2.utils.logger.Severity;

/**
 * DiagnosticManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DiagnosticManager {

	private static final String PREFIX_FUNCTION = "function.";
	private static final String PREFIX_DIAGNOSTIC = "diagnostic.";
	private static final String SUFFIX_DIAGNOSTIC_CODE = ".code";
	private static final String SUFFIX_DIAGNOSTIC_MESSAGE = ".message";
	private static final String SUFFIX_DIAGNOSTIC_SEVERITY = ".severity";
	
	private static final String DEFAULT_FUNCTION = "default.function";
	
	private static final String DEFAULT_SEVERITY_CODE_PREFIX = "default.severity.";
	
	private static final String CONTEXT = "context.";
	private static final String DYNAMIC_OBJECT = "object.";
	
	private Properties diagnosticProperties;
	private IContext context;
	
	private Hashtable<String, String> mappingFunctionToCode = new Hashtable<String, String>();
	private Hashtable<String, String> mappingFullCodeToFullString = new Hashtable<String, String>();
	private Hashtable<String, DiagnosticInfo> mappingStringCodeToDiagnostic = new Hashtable<String, DiagnosticInfo>(); 
	
	private String defaultFunction;
	private Hashtable<LowSeverity, String> mappingSeverityToCode = new Hashtable<LowSeverity, String>();
	
	private boolean throwExceptionPlaceholderFailedResolution;
	
	private ILogger loggerForCallback;
	
	protected DiagnosticManager(Properties diagnosticProperties, IContext context, boolean throwExceptionPlaceholderFailedResolution, ILogger loggerForCallback) throws UtilsException{
		this.diagnosticProperties = diagnosticProperties;
		this.context = context;
		
		this.throwExceptionPlaceholderFailedResolution = throwExceptionPlaceholderFailedResolution;
		
		this.loggerForCallback = loggerForCallback;
		
		this.init();
	}
	
	public String getDefaultFunction() {
		return this.defaultFunction;
	}
	
	public String getDefaultCode(String function, LowSeverity severity) throws UtilsException{
		if(this.mappingFunctionToCode.containsKey(function)==false){
			throw new UtilsException("Function ["+function+"] undefined");
		}
		return this.mappingFunctionToCode.get(function)  + this.mappingSeverityToCode.get(severity);
	}
	
	public String getCode(String code) throws UtilsException{
		if(this.mappingFullCodeToFullString.containsKey(code)){
			// è già il codice
			return code;
		}
		if(this.mappingStringCodeToDiagnostic.containsKey(code)){
			// è un codice stringa, ritorno il codice numerico
			return this.mappingStringCodeToDiagnostic.get(code).functionCode+this.mappingStringCodeToDiagnostic.get(code).code;
		}
		throw new UtilsException("Diagnostic with code ["+code+"] undefined");
	}
	
	public Severity getSeverity(String code) throws UtilsException{
		String codeString = null;
		if(this.mappingFullCodeToFullString.containsKey(code)){
			codeString = this.mappingFullCodeToFullString.get(code);
		}
		if(codeString==null){
			if(this.mappingStringCodeToDiagnostic.containsKey(code)){
				// è gia codice stringa
				codeString = code;
			}
		}
		if(codeString==null)
			throw new UtilsException("Diagnostic with code ["+code+"] undefined");
		return this.mappingStringCodeToDiagnostic.get(codeString).severity;
	}
	
	public String getMessage(String code, String ... params) throws UtilsException{
		String codeString = null;
		if(this.mappingFullCodeToFullString.containsKey(code)){
			codeString = this.mappingFullCodeToFullString.get(code);
		}
		if(codeString==null){
			if(this.mappingStringCodeToDiagnostic.containsKey(code)){
				// è gia codice stringa
				codeString = code;
			}
		}
		if(codeString==null)
			throw new UtilsException("Diagnostic with code ["+code+"] undefined");
		return this.replacePlaceholders(code, this.mappingStringCodeToDiagnostic.get(codeString).message,null,params);
	}
	
	public String getMessage(String code, Object o) throws UtilsException{
		String codeString = null;
		if(this.mappingFullCodeToFullString.containsKey(code)){
			codeString = this.mappingFullCodeToFullString.get(code);
		}
		if(codeString==null){
			if(this.mappingStringCodeToDiagnostic.containsKey(code)){
				// è gia codice stringa
				codeString = code;
			}
		}
		if(codeString==null)
			throw new UtilsException("Diagnostic with code ["+code+"] undefined");
		return this.replacePlaceholders(code, this.mappingStringCodeToDiagnostic.get(codeString).message,o);
	}
	
	public String getFunction(String code) throws UtilsException{
		String codeString = null;
		if(this.mappingFullCodeToFullString.containsKey(code)){
			codeString = this.mappingFullCodeToFullString.get(code);
		}
		if(codeString==null){
			if(this.mappingStringCodeToDiagnostic.containsKey(code)){
				// è gia codice stringa
				codeString = code;
			}
		}
		if(codeString==null)
			throw new UtilsException("Diagnostic with code ["+code+"] undefined");
		return this.mappingStringCodeToDiagnostic.get(codeString).functionString;
		
	}
	
	private void init() throws UtilsException{
		
		// default function
		if(this.diagnosticProperties.containsKey(DEFAULT_FUNCTION)==false){
			throw new UtilsException("Property ["+DEFAULT_FUNCTION+"] undefined");
		}
		this.defaultFunction = this.diagnosticProperties.getProperty(DEFAULT_FUNCTION).trim();
		
		
		// default severity code prefix
		LowSeverity [] s = LowSeverity.values();
		for (int i = 0; i < s.length; i++) {
			String key = DEFAULT_SEVERITY_CODE_PREFIX+s[i].name();
			if(this.diagnosticProperties.containsKey(key)==false){
				throw new UtilsException("Property ["+key+"] undefined");
			}
			this.mappingSeverityToCode.put(s[i], this.diagnosticProperties.getProperty(key).trim());
		}
		
		// Mapping function to code
		Properties functionToCodeProperties = Utilities.readProperties(PREFIX_FUNCTION, this.diagnosticProperties); 
		if(functionToCodeProperties.size()<=0){
			throw new UtilsException("Functions undefined");
		}
		Enumeration<?> enFunction = functionToCodeProperties.keys();
		while (enFunction.hasMoreElements()) {
			String function = (String) enFunction.nextElement();
			String code = functionToCodeProperties.getProperty(function);
			if(this.mappingFunctionToCode.containsKey(function)){
				throw new UtilsException("Function ["+function+"] already exists");
			}
			if(function.contains(".")){
				throw new UtilsException("Function ["+function+"] contain character '.' not permitted");
			}
			if(code==null || code.trim().equals("")){
				throw new UtilsException("Code for function ["+function+"] undefined");
			}
			code = code.trim();
			if(this.mappingFunctionToCode.values().contains(code)){
				throw new UtilsException("Code ["+code+"] already used for other function");
			}
			this.mappingFunctionToCode.put(function, code);
			
			
			// Mapping diagnostic for function
			String diagnosticForFunctionPrefix = PREFIX_DIAGNOSTIC+function+".";
			Properties diagnosticForFunctionProperties = Utilities.readProperties(diagnosticForFunctionPrefix, this.diagnosticProperties);
			if(diagnosticForFunctionProperties.size()<=0){
				throw new UtilsException("Diagnostic for function ["+function+"] undefined");
			}
			Enumeration<?> enDiagnostic = diagnosticForFunctionProperties.keys();
			while (enDiagnostic.hasMoreElements()) {
				
				String diagnosticTmp = (String) enDiagnostic.nextElement();
				if(!diagnosticTmp.endsWith(SUFFIX_DIAGNOSTIC_CODE) && 
						!diagnosticTmp.endsWith(SUFFIX_DIAGNOSTIC_MESSAGE) &&
						!diagnosticTmp.endsWith(SUFFIX_DIAGNOSTIC_SEVERITY)){
					throw new UtilsException("Unexpected element ["+diagnosticForFunctionPrefix+diagnosticTmp+"]");
				}
				String diagnosticNameWithoutSuffix = null;
				if(diagnosticTmp.endsWith(SUFFIX_DIAGNOSTIC_CODE)){
					diagnosticNameWithoutSuffix = diagnosticTmp.substring(0, diagnosticTmp.length()-SUFFIX_DIAGNOSTIC_CODE.length());
				}
				else if(diagnosticTmp.endsWith(SUFFIX_DIAGNOSTIC_MESSAGE)){
					diagnosticNameWithoutSuffix = diagnosticTmp.substring(0, diagnosticTmp.length()-SUFFIX_DIAGNOSTIC_MESSAGE.length());
				}
				else if(diagnosticTmp.endsWith(SUFFIX_DIAGNOSTIC_SEVERITY)){
					diagnosticNameWithoutSuffix = diagnosticTmp.substring(0, diagnosticTmp.length()-SUFFIX_DIAGNOSTIC_SEVERITY.length());
				}
				
				String valueTmp =  diagnosticForFunctionProperties.getProperty(diagnosticTmp);
				if(valueTmp==null || valueTmp.trim().equals("")){
					throw new UtilsException("Value for diagnostic ["+diagnosticForFunctionPrefix+diagnosticTmp+"] undefined");
				}
				valueTmp = valueTmp.trim();
				
				String fullStringCode = function+"."+diagnosticNameWithoutSuffix;
				DiagnosticInfo diagInfo = null;
				if(this.mappingStringCodeToDiagnostic.containsKey(fullStringCode)){
					diagInfo = this.mappingStringCodeToDiagnostic.remove(fullStringCode);
				}
				else{
					diagInfo = new DiagnosticInfo();
					diagInfo.functionCode = code;
					diagInfo.functionString = function;
				}
				if(diagnosticTmp.endsWith(SUFFIX_DIAGNOSTIC_CODE)){
					if(diagInfo.code!=null){
						throw new UtilsException("Diagnostic ["+diagnosticForFunctionPrefix+diagnosticTmp+"] already defined");
					}
					diagInfo.code = valueTmp;
				}
				else if(diagnosticTmp.endsWith(SUFFIX_DIAGNOSTIC_MESSAGE)){
					if(diagInfo.message!=null){
						throw new UtilsException("Diagnostic ["+diagnosticForFunctionPrefix+diagnosticTmp+"] already defined");
					}
					diagInfo.message = valueTmp;
				}
				else if(diagnosticTmp.endsWith(SUFFIX_DIAGNOSTIC_SEVERITY)){
					if(diagInfo.severity!=null){
						throw new UtilsException("Diagnostic ["+diagnosticForFunctionPrefix+diagnosticTmp+"] already defined");
					}
					try{
						Severity severity = Severity.valueOf(valueTmp);
						diagInfo.severity = severity;
					}catch(Exception e){
						throw new UtilsException("Value for diagnostic ["+diagnosticForFunctionPrefix+diagnosticTmp+"] is not valid, expected: "+Arrays.asList(Severity.values()));
					}
				}
				this.mappingStringCodeToDiagnostic.put(fullStringCode, diagInfo);
			}
			
		}
		
		
		// Check ogni diagnostico abbia la tripla definita
		Enumeration<String> enStringCodes = this.mappingStringCodeToDiagnostic.keys();
		while (enStringCodes.hasMoreElements()) {
			String stringCode = (String) enStringCodes.nextElement();
			DiagnosticInfo diagInfo = this.mappingStringCodeToDiagnostic.get(stringCode);
			if(diagInfo.code==null){
				throw new UtilsException("Undefined code for diagnostic ["+PREFIX_DIAGNOSTIC+stringCode+"]");
			}
			if(diagInfo.message==null){
				throw new UtilsException("Undefined message for diagnostic ["+PREFIX_DIAGNOSTIC+stringCode+"]");
			}
			if(diagInfo.severity==null){
				throw new UtilsException("Undefined severity for diagnostic ["+PREFIX_DIAGNOSTIC+stringCode+"]");
			}
			//System.out.println("REGISTER ["+diagInfo.functionCode+diagInfo.code+"]["+stringCode+"]");
			this.mappingFullCodeToFullString.put(diagInfo.functionCode+diagInfo.code, stringCode);
		}
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
						if(this.throwExceptionPlaceholderFailedResolution){
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
			return this.readValueInObject(diagnostic, this.context, key.substring(CONTEXT.length(), key.length()), key);
		}
		else if(key.startsWith(DYNAMIC_OBJECT)){
			if(dinamycObject==null){
				if(this.throwExceptionPlaceholderFailedResolution){
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
					if(this.throwExceptionPlaceholderFailedResolution){
						throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+key+"] unsupported");
					}
					else{
						return "(???Placeholder ["+key+"] unsupported)";
					}
				}
				if(params==null || params.length<=0){
					if(this.throwExceptionPlaceholderFailedResolution){
						throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+key+"] required parameters");
					}
					else{
						return "(???Placeholder ["+key+"] required parameters)";
					}
				}
				if(intValue>=params.length){
					if(this.throwExceptionPlaceholderFailedResolution){
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
						String value = this.loggerForCallback.getLogParam(key);
						if(value!=null){
							return value;
						}
					}catch(Exception eCallback){
						if(this.throwExceptionPlaceholderFailedResolution){
							throw new UtilsException("(diagnostic:"+diagnostic+") Placeholder ["+key+"] callback resolution failed: "+eCallback.getMessage(),eCallback);
						}
						else{
							return "(???Placeholder ["+key+"] callback resolution failed: "+eCallback.getMessage()+")";
						}
					}
					
					if(this.throwExceptionPlaceholderFailedResolution){
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
		if(name.contains(".")){
			fieldName = name.substring(0, name.indexOf("."));
		}
		String getMethod = "get"+((fieldName.charAt(0)+"").toUpperCase());
		if(fieldName.length()>1){
			getMethod = getMethod + fieldName.substring(1);
		}
		Method m = null;
		try{
			m = o.getClass().getMethod(getMethod);
		}catch(Throwable e){
			if(this.throwExceptionPlaceholderFailedResolution){
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
			if(this.throwExceptionPlaceholderFailedResolution){
				throw new UtilsException("(diagnostic:"+diagnostic+") Invocation method ["+o.getClass().getName()+"."+getMethod+"()] failed: "+e.getMessage(),e);
			}
			else{
				return "(???Placeholder ["+placeholderOriginale+"] Invocation method ["+o.getClass().getName()+"."+getMethod+"()] failed: "+e.getMessage()+")";
			}
		}
		if(name.contains(".")){
			if(ret==null){
				if(this.throwExceptionPlaceholderFailedResolution){
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
				}else{
					finalValue = ret.toString();
				}
			}
			else{
				if(this.throwExceptionPlaceholderFailedResolution){
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
