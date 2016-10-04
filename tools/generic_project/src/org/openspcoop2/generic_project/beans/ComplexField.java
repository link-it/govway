/*
 * OpenSPCoop - Customizable API Gateway 
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
		return this.getClassName().equals(f.getClassName()) && 
			this.getClassType().getName().equals(f.getClassType().getName()) &&
			this.getFieldName().equals(f.getFieldName()) &&
			this.getFieldType().getName().equals(f.getFieldType().getName()) && 
			this.father.equals(f.getFather());
	}
	
	@Override
	public String toString(){
		return toString(0);
	}
	@Override
	public String toString(int indent){
		
		StringBuffer indentBuffer = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			indentBuffer.append("	");
		}
		
		StringBuffer bf = new StringBuffer();
		
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
