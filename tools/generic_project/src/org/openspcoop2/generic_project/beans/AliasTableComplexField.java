/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
 * AliasTableField
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AliasTableComplexField extends ComplexField implements IAliasTableField {

	private String aliasTable;
	private ComplexField complexField;
	
	@Override
	public String getAliasTable() {
		return this.aliasTable;
	}
	@Override
	public IField getField(){
		return this.complexField;
	}

	public AliasTableComplexField(ComplexField iField, String aliasTable){
		super(iField.getFather(), iField.getFieldName(), iField.getFieldType(), iField.getClassName(), iField.getClassType());
		this.aliasTable = aliasTable;
		this.complexField = iField;
	}
	
	// Ridefinisco l'equals, altrimenti nei Converter, gli if che precendono la chiamata al metodo AbstractSQLFieldConverter.toColumn o toTable
	// vengono soddisfatti, e quindi non viene usato l'alias
	@Override
	public boolean equals(Object o){
		boolean superEquals = super.equals(o);
		if(superEquals){
			if(o instanceof AliasTableComplexField){
				AliasTableComplexField af = (AliasTableComplexField) o;
				return af.getAliasTable().equals(this.aliasTable);
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
		
		StringBuilder bf = new StringBuilder(this.complexField.toString(indent));
		
		bf.append(indentBuffer.toString());
		bf.append("- aliasTable(Complex): "+this.aliasTable);
		bf.append("\n");
				
		return bf.toString();
	}
}
