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

import org.openspcoop2.generic_project.exception.ExpressionException;

/**
 * CustomField
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AliasField extends ComplexField {

	private IField field;
	private String alias;
	
	public AliasField(IField field,String alias) throws ExpressionException{
		super(new ConstantField("AliasSimpleField", "AliasSimpleField", String.class), 
				field.getFieldName(), field.getFieldType(), field.getClassName(), field.getClassType());
		
		if(field instanceof ComplexField){
			ComplexField cf = (ComplexField) field;
			super.father = cf.father;
		}

		this.field = field;
		this.alias = alias;
	}
	
	@Override
	public Class<?> getFieldType() {
		return this.field.getFieldType();
	}
	@Override
	public Class<?> getClassType() {
		return this.field.getClassType();
	}
	@Override
	public String getFieldName() {
		return this.field.getFieldName();
	}
	@Override
	public String getClassName() {
		return this.field.getClassName();
	}
	
	public String getAlias() {
		return this.alias;
	}
	public IField getField() {
		return this.field;
	}
	
	// Ridefinisco l'equals, altrimenti nei Converter, gli if che precendono la chiamata al metodo AbstractSQLFieldConverter.toColumn
	// vengono soddisfatti, e quindi non viene usato l'alias
	@Override
	public boolean equals(Object o){
		boolean superEquals = super.equals(o);
		if(superEquals){
			if(o instanceof AliasField){
				AliasField af = (AliasField) o;
				return af.getAlias().equals(this.alias);
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
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
		
		StringBuffer bf = new StringBuffer(this.field.toString(indent));
		
		bf.append(indentBuffer.toString());
		bf.append("- alias: "+this.alias);
		bf.append("\n");
				
		return bf.toString();
	}
}
