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

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.logger.constants.LowSeverity;
import org.openspcoop2.utils.logger.constants.Severity;

/**
 * DiagnosticManager
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author: apoli $
 * @version $Rev: 11983 $, $Date: 2016-05-27 13:07:16 +0200 (Fri, 27 May 2016) $
 */
public class DiagnosticProperties {

	private static DiagnosticProperties staticInstance;
	private static synchronized void initialize(Properties diagnosticProperties, boolean throwExceptionPlaceholderFailedResolution) throws UtilsException{
		if(staticInstance==null){
			staticInstance = new DiagnosticProperties(diagnosticProperties, throwExceptionPlaceholderFailedResolution);
		}
	}
	protected static DiagnosticProperties getInstance(Properties diagnosticProperties, boolean throwExceptionPlaceholderFailedResolution) throws UtilsException{
		if(staticInstance==null){
			initialize(diagnosticProperties, throwExceptionPlaceholderFailedResolution);
		}
		return staticInstance;
	}
	
	
	private static final String PREFIX_FUNCTION = "function.";
	private static final String PREFIX_DIAGNOSTIC = "diagnostic.";
	private static final String SUFFIX_DIAGNOSTIC_CODE = ".code";
	private static final String SUFFIX_DIAGNOSTIC_MESSAGE = ".message";
	private static final String SUFFIX_DIAGNOSTIC_SEVERITY = ".severity";
	
	private static final String DEFAULT_FUNCTION = "default.function";
	
	private static final String DEFAULT_SEVERITY_CODE_PREFIX = "default.severity.";
	
	private Properties diagnosticProperties;
	
	private Hashtable<String, String> mappingFunctionToCode = new Hashtable<String, String>();
	private Hashtable<String, String> mappingFullCodeToFullString = new Hashtable<String, String>();
	private Hashtable<String, DiagnosticInfo> mappingStringCodeToDiagnostic = new Hashtable<String, DiagnosticInfo>(); 
	
	private String defaultFunction;
	private Hashtable<LowSeverity, String> mappingSeverityToCode = new Hashtable<LowSeverity, String>();
	
	private boolean throwExceptionPlaceholderFailedResolution;
	
	
	private DiagnosticProperties(Properties diagnosticProperties, boolean throwExceptionPlaceholderFailedResolution) throws UtilsException{
		this.diagnosticProperties = diagnosticProperties;
		
		this.throwExceptionPlaceholderFailedResolution = throwExceptionPlaceholderFailedResolution;
				
		this.init();
	}
	private DiagnosticProperties(){} // for clone
	
	@Override
	public DiagnosticProperties clone(){
		
		DiagnosticProperties dmNew = new DiagnosticProperties();
		
		if(this.defaultFunction!=null){
			dmNew.defaultFunction = new String(this.defaultFunction);
		}
		
		if(this.diagnosticProperties!=null && this.diagnosticProperties.size()>0){
			dmNew.diagnosticProperties = new Properties();
			Enumeration<Object> enKeys = this.diagnosticProperties.keys();
			while (enKeys.hasMoreElements()) {
				Object objKey = (Object) enKeys.nextElement();
				Object objValue = this.diagnosticProperties.get(objKey);	
				dmNew.diagnosticProperties.put(objKey, objValue);
			}
		}
		
		dmNew.throwExceptionPlaceholderFailedResolution = this.throwExceptionPlaceholderFailedResolution;
		
		if(this.mappingFullCodeToFullString!=null && this.mappingFullCodeToFullString.size()>0){
			dmNew.mappingFullCodeToFullString = new Hashtable<String, String>();
			Enumeration<String> keys = this.mappingFullCodeToFullString.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = this.mappingFullCodeToFullString.get(key);
				dmNew.mappingFullCodeToFullString.put(key, value);
			}
		}
		
		if(this.mappingFunctionToCode!=null && this.mappingFunctionToCode.size()>0){
			dmNew.mappingFunctionToCode = new Hashtable<String, String>();
			Enumeration<String> keys = this.mappingFunctionToCode.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				String value = this.mappingFunctionToCode.get(key);
				dmNew.mappingFunctionToCode.put(key, value);
			}
		}
		
		if(this.mappingSeverityToCode!=null && this.mappingSeverityToCode.size()>0){
			dmNew.mappingSeverityToCode = new Hashtable<LowSeverity, String>();
			Enumeration<LowSeverity> keys = this.mappingSeverityToCode.keys();
			while (keys.hasMoreElements()) {
				LowSeverity key = (LowSeverity) keys.nextElement();
				String value = this.mappingSeverityToCode.get(key);
				dmNew.mappingSeverityToCode.put(key, value);
			}
		}
		
		if(this.mappingStringCodeToDiagnostic!=null && this.mappingStringCodeToDiagnostic.size()>0){
			dmNew.mappingStringCodeToDiagnostic = new Hashtable<String, DiagnosticInfo>();
			Enumeration<String> keys = this.mappingStringCodeToDiagnostic.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				DiagnosticInfo value = this.mappingStringCodeToDiagnostic.get(key);
				dmNew.mappingStringCodeToDiagnostic.put(key, value);
			}
		}
		
		return dmNew;
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
	
	protected String getDefaultFunction() {
		return this.defaultFunction;
	}
	
	protected Hashtable<String, String> getMappingFunctionToCode() {
		return this.mappingFunctionToCode;
	}
	protected Hashtable<String, String> getMappingFullCodeToFullString() {
		return this.mappingFullCodeToFullString;
	}
	protected Hashtable<String, DiagnosticInfo> getMappingStringCodeToDiagnostic() {
		return this.mappingStringCodeToDiagnostic;
	}
	protected Hashtable<LowSeverity, String> getMappingSeverityToCode() {
		return this.mappingSeverityToCode;
	}
	protected boolean isThrowExceptionPlaceholderFailedResolution() {
		return this.throwExceptionPlaceholderFailedResolution;
	}
}

