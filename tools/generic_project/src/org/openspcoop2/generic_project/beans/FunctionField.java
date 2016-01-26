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
package org.openspcoop2.generic_project.beans;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.exception.ExpressionException;

/**
 * FunctionField
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FunctionField {

	// campi per la funct
	private String functionValue;
	private Class<?> functionValueType;
	private List<IField> fields = new ArrayList<IField>();
	private String operator;
	
	// function
	private Function function;
	private boolean customFunction;
	private String prefixFunctionCustom;
	private String suffixFunctionCustom;
	
	// alias
	private String alias;
	
	
	private void checkAlias() throws ExpressionException{
		if(this.function!=null && this.alias!=null){
			
			// hsql le utilizza come chiavi
			
			if("count".equalsIgnoreCase(this.alias) && Function.COUNT.equals(this.function) ){
				throw new ExpressionException("Alias ["+this.alias+"] is not allowed with the function COUNT"); 
			}
			
			if("count".equalsIgnoreCase(this.alias) && Function.COUNT_DISTINCT.equals(this.function) ){
				throw new ExpressionException("Alias ["+this.alias+"] is not allowed with the function COUNT"); 
			}
			
			if("avg".equalsIgnoreCase(this.alias) && Function.AVG.equals(this.function) ){
				throw new ExpressionException("Alias ["+this.alias+"] is not allowed with the function AVG"); 
			}
			
			if("avg".equalsIgnoreCase(this.alias) && Function.AVG_DOUBLE.equals(this.function) ){
				throw new ExpressionException("Alias ["+this.alias+"] is not allowed with the function AVG_DOUBLE"); 
			}
			
			if("max".equalsIgnoreCase(this.alias) && Function.MAX.equals(this.function) ){
				throw new ExpressionException("Alias ["+this.alias+"] is not allowed with the function MAX"); 
			}
			
			if("min".equalsIgnoreCase(this.alias) && Function.MIN.equals(this.function) ){
				throw new ExpressionException("Alias ["+this.alias+"] is not allowed with the function MIN"); 
			}
			
			if("sum".equalsIgnoreCase(this.alias) && Function.SUM.equals(this.function) ){
				throw new ExpressionException("Alias ["+this.alias+"] is not allowed with the function SUM"); 
			}
		}
	}
	
	
	/**
	 * Esempio di utilizzo di una funzione conosciuta su di un field
	 **/
	public FunctionField(IField field,Function function,String alias) throws ExpressionException{
		if(field==null){
			throw new ExpressionException("IField is null");
		}
		if(function==null){
			throw new ExpressionException("Function is null");
		}
		if(alias==null){
			throw new ExpressionException("Alias is null");
		}

		this.fields.add(field);
		this.function = function;
		this.alias = alias;
		this.customFunction = false;
		this.checkAlias();
	}
	
	/**
	 * Esempio di utilizzo di una funzione conosciuta su piu' fields uniti da uno stesso operatore (F1 operator F2 operator ... FN)
	 **/
	public FunctionField(Function function,String alias,String operator,IField ... field) throws ExpressionException{

		if(function==null){
			throw new ExpressionException("Function is null");
		}
		if(alias==null){
			throw new ExpressionException("Alias is null");
		}
		if(operator==null){
			throw new ExpressionException("Operator is null");
		}

		if(field==null || field.length<=0){
			throw new ExpressionException("IField is null");
		}
		if(field.length<2){
			throw new ExpressionException("IField less then 2. With operator constructor is required almost two field");
		}
		Class<?> cField = null;
		for (int i = 0; i < field.length; i++) {
			if(i==0){
				cField = field[i].getFieldType();
			}
			else{
				if(cField.getName().equals(field[i].getFieldType().getName())==false){
					throw new ExpressionException("Fields aren't same type [0]="+cField.getClass().getName()+" ["+i+"]="+field[i].getFieldType().getName());
				}
			}
			this.fields.add(field[i]);
		}
		this.function = function;
		this.alias = alias;
		this.operator = operator;
		this.customFunction = false;
		this.checkAlias();
	}
	
	/**
	 * Esempio di utilizzo di una funzione conosciuta su dei valori input based (functionValue)
	 **/
	public FunctionField(String functionValue,Class<?> functionValueType,Function function,String alias) throws ExpressionException{
		if(functionValue==null){
			throw new ExpressionException("functionValue is null");
		}
		if(functionValueType==null){
			throw new ExpressionException("functionValueType is null");
		}
		if(function==null){
			throw new ExpressionException("Function is null");
		}
		if(alias==null){
			throw new ExpressionException("Alias is null");
		}
		this.functionValue = functionValue;
		this.functionValueType = functionValueType;
		this.function = function;
		this.alias = alias;
		this.customFunction = false;
		this.checkAlias();
	}
	
	/**
	 * Esempio di utilizzo di una funzione custom su di un field
	 **/
	public FunctionField(IField field,String prefixFunctionCustom,String suffixFunctionCustom,String alias) throws ExpressionException{
		if(field==null){
			throw new ExpressionException("IField is null");
		}
		if(prefixFunctionCustom==null){
			throw new ExpressionException("prefixFunctionCustom is null");
		}
		if(suffixFunctionCustom==null){
			throw new ExpressionException("suffixFunctionCustom is null");
		}
		if(alias==null){
			throw new ExpressionException("Alias is null");
		}
		this.fields.add(field);
		this.prefixFunctionCustom = prefixFunctionCustom;
		this.suffixFunctionCustom = suffixFunctionCustom;
		this.alias = alias;
		this.customFunction = true;
		this.checkAlias();
	}
	
	/**
	 * Esempio di utilizzo di una funzione custom su piu' fields uniti da uno stesso operatore (F1 operator F2 operator ... FN)
	 **/
	public FunctionField(String prefixFunctionCustom,String suffixFunctionCustom,String alias,String operator,IField ... field) throws ExpressionException{

		if(prefixFunctionCustom==null){
			throw new ExpressionException("prefixFunctionCustom is null");
		}
		if(suffixFunctionCustom==null){
			throw new ExpressionException("suffixFunctionCustom is null");
		}
		if(alias==null){
			throw new ExpressionException("Alias is null");
		}
		if(operator==null){
			throw new ExpressionException("Operator is null");
		}

		if(field==null || field.length<=0){
			throw new ExpressionException("IField is null");
		}
		if(field.length<2){
			throw new ExpressionException("IField less then 2. With operator constructor is required almost two field");
		}
		Class<?> cField = null;
		for (int i = 0; i < field.length; i++) {
			if(i==0){
				cField = field[i].getFieldType();
			}
			else{
				if(cField.getName().equals(field[i].getFieldType().getName())==false){
					throw new ExpressionException("Fields aren't same type [0]="+cField.getClass().getName()+" ["+i+"]="+field[i].getFieldType().getName());
				}
			}
			this.fields.add(field[i]);
		}
		this.prefixFunctionCustom = prefixFunctionCustom;
		this.suffixFunctionCustom = suffixFunctionCustom;
		this.alias = alias;
		this.operator = operator;
		this.customFunction = true;
		this.checkAlias();
	}
	
	/**
	 * Esempio di utilizzo di una funzione custom su dei valori input based (functionValue)
	 **/
	public FunctionField(String functionValue,Class<?> functionValueType,String prefixFunctionCustom,String suffixFunctionCustom,String alias) throws ExpressionException{
		if(functionValue==null){
			throw new ExpressionException("functionValue is null");
		}
		if(functionValueType==null){
			throw new ExpressionException("functionValueType is null");
		}
		if(prefixFunctionCustom==null){
			throw new ExpressionException("prefixFunctionCustom is null");
		}
		if(suffixFunctionCustom==null){
			throw new ExpressionException("suffixFunctionCustom is null");
		}
		if(alias==null){
			throw new ExpressionException("Alias is null");
		}
		this.functionValue = functionValue;
		this.functionValueType = functionValueType;
		this.prefixFunctionCustom = prefixFunctionCustom;
		this.suffixFunctionCustom = suffixFunctionCustom;
		this.alias = alias;
		this.customFunction = true;
		this.checkAlias();
	}
	
	
	
	public String getFunctionValue() {
		return this.functionValue;
	}
	public Class<?> getFunctionValueType() {
		return this.functionValueType;
	}
	public List<IField> getFields() {
		return this.fields;
	}
	public String getOperator() {
		return this.operator;
	}
	public Class<?> getFieldType(){
		if(this.functionValueType!=null){
			return this.functionValueType;
		}
		else{
			if(this.function!=null && Function.AVG_DOUBLE.equals(this.function)){
				return Double.class;
			}
			else{
				// devono essere tutti dello stesso tipo
				return this.fields.get(0).getFieldType();
			}
		}
	}
	
	
	public boolean isCustomFunction() {
		return this.customFunction;
	}
	public Function getFunction() {
		return this.function;
	}
	public String getPrefixFunctionCustom() {
		return this.prefixFunctionCustom;
	}
	public String getSuffixFunctionCustom() {
		return this.suffixFunctionCustom;
	}
	
		
	public String getAlias() {
		return this.alias;
	}



	
	@Override
	public boolean equals(Object o){
		if(o instanceof FunctionField){
			return this.equals((FunctionField)o);
		}
		else{
			return false;
		}
	}
	public boolean equals(FunctionField o){
		if(o==null){
			return false;
		}
		
		if(this.functionValue==null){
			if(o.getFunctionValue()!=null){
				return false;
			}
		}
		else {
			if(this.functionValue.equals(o.getFunctionValue())==false){
				return false;
			}
		}
		
		if(this.functionValueType==null){
			if(o.getFunctionValueType()!=null){
				return false;
			}
		}
		else {
			if(o.getFunctionValueType()==null){
				return false;
			}
			if(this.functionValueType.getName().equals(o.getFunctionValueType().getName())==false){
				return false;
			}
		}
		
		if(o.getFields().size()!=o.getFields().size()){
			return false;
		}
		for (IField field : this.fields) {
			boolean find = false;
			for (IField fieldParam : o.getFields()) {
				if(fieldParam.equals(field)){
					find = true;
					break;
				}
			}
			if(!find){
				return false;
			}
		}
		for (IField fieldParam : o.getFields()) {
			boolean find = false;
			for (IField field : this.fields) {
				if(fieldParam.equals(field)){
					find = true;
					break;
				}
			}
			if(!find){
				return false;
			}
		}

		
		if(o.getOperator()==null){
			if(this.operator!=null)
				return false;
		}
		else{
			if(o.getOperator().equals(this.operator)==false){
				return false;
			}
		}
		
		if(this.customFunction != o.isCustomFunction() ){
			return false;
		}

		if(this.function==null){
			if(o.getFunction()!=null){
				return false;
			}
		}
		else {
			if(o.getFunction()==null){
				return false;
			}
			if(this.function.name().equals(o.getFunction().name())==false){
				return false;
			}
		}

		if(this.prefixFunctionCustom==null){
			if(o.getPrefixFunctionCustom()!=null){
				return false;
			}
		}
		else {
			if(this.prefixFunctionCustom.equals(o.getPrefixFunctionCustom())==false){
				return false;
			}
		}
		
		if(this.suffixFunctionCustom==null){
			if(o.getSuffixFunctionCustom()!=null){
				return false;
			}
		}
		else {
			if(this.suffixFunctionCustom.equals(o.getSuffixFunctionCustom())==false){
				return false;
			}
		}
		
		if(this.alias==null){
			if(o.getAlias()!=null){
				return false;
			}
		}
		else {
			if(this.alias.equals(o.getAlias())==false){
				return false;
			}
		}
		
		
		return true;
	}
	
	@Override
	public String toString(){
				
		StringBuffer bf = new StringBuffer();
		
		bf.append("- Alias: "+this.alias);
		bf.append("\n");
		if(this.customFunction){
			bf.append("- CustomFunction: "+this.prefixFunctionCustom +" @VALUE@ "+this.suffixFunctionCustom);
		}else{
			bf.append("- Function: "+this.function);
		}
		bf.append("\n");
		if(this.functionValue!=null){
			bf.append("- FunctionValue (type:"+this.functionValueType+"): "+this.functionValue);
		}
		else{
			if(this.operator!=null){
				bf.append("- Operator: "+this.operator);
				bf.append("\n");
			}
			if(this.fields.size()==1){
				bf.append(this.fields.get(0).toString());
			}
			else{
				for (int i = 0; i < this.fields.size(); i++) {
					bf.append("- Field["+i+"]: "+this.fields.get(i));
					bf.append("\n");
				}
			}
		}
				
		return bf.toString();
	}
}
