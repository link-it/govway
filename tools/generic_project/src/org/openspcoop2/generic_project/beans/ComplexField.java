/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.beans;


/**
 * ComplexField
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ComplexField implements IField {

	protected String fieldName;
	protected Class<?> fieldType;
	
	protected String className;
	protected Class<?> classType;
	
	protected IField father;
	
	public ComplexField(IField father,
			String fieldName,Class<?> fieldType,String className,Class<?> classType){
		
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		
		this.className = className;
		this.classType = classType;
		
		this.father = father;
	}

	@Override
	public String getFieldName() {
		return this.fieldName;
	}

	@Override
	public Class<?> getFieldType() {
		return this.fieldType;
	}

	@Override
	public String getClassName() {
		return this.className;
	}

	@Override
	public Class<?> getClassType() {
		return this.classType;
	}

	public IField getFather() {
		return this.father;
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null){
			return false;
		}
		if(! (o instanceof ComplexField) ){
			if(o instanceof IField)
				return false;
			else
				throw new RuntimeException("Expected type ["+ComplexField.class.getName()+"] different from the type found ["+o.getClass().getName()+"]");
		}
		ComplexField f = (ComplexField) o;
		
		String thisClassTypeName = this.getClassType().getName() + "";
		String fClassTypeName = f.getClassType().getName() + "";
		
		String thisFieldTypeName = this.getFieldType().getName() + "";
		String fFieldTypeName = f.getFieldType().getName() + "";
		
		return this.getClassName().equals(f.getClassName()) && 
				thisClassTypeName.equals(fClassTypeName) &&
			this.getFieldName().equals(f.getFieldName()) &&
			thisFieldTypeName.equals(fFieldTypeName) && 
			this.father.equals(f.getFather());
	}
	
	@Override 
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public String toString(){
		return toString(0);
	}
	@Override
	public String toString(int indent){
		
		StringBuilder indentBuffer = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			indentBuffer.append("	");
		}
		
		StringBuilder bf = new StringBuilder();
		
		bf.append(indentBuffer.toString());
		bf.append("- field name: "+this.fieldName);
		bf.append("\n");
		bf.append(indentBuffer.toString());
		bf.append("- field type: "+this.fieldType);
		bf.append("\n");
		bf.append(indentBuffer.toString());
		bf.append("- class name: "+this.className);
		bf.append("\n");
		bf.append(indentBuffer.toString());
		bf.append("- class type: "+this.classType);
		bf.append("\n");
		
		bf.append(indentBuffer.toString());
		bf.append("- father: ");
		bf.append("\n");
		if(this.father instanceof ComplexField){
			bf.append( ((ComplexField)this.father).toString(++indent) );
		}else if(this.father instanceof Field){
			bf.append( ((Field)this.father).toString(++indent) );
		}else{
			throw new RuntimeException("Type ["+this.father.getClass().getName()+"] not supported");
		}
		
		return bf.toString();
	}
	
}
