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

/**
 * AliasTableField
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AliasTableField extends Field implements IAliasTableField {

	private String aliasTable;
	private Field iField;
	
	@Override
	public String getAliasTable() {
		return this.aliasTable;
	}
	@Override
	public IField getField(){
		return this.iField;
	}

	public AliasTableField(Field iField, String aliasTable){
		super(iField.getFieldName(), iField.getFieldType(), iField.getClassName(), iField.getClassType());
		this.aliasTable = aliasTable;
		this.iField = iField;
	}
	
	// Ridefinisco l'equals, altrimenti nei Converter, gli if che precendono la chiamata al metodo AbstractSQLFieldConverter.toColumn o toTable
	// vengono soddisfatti, e quindi non viene usato l'alias
	@Override
	public boolean equals(Object o){
		boolean superEquals = super.equals(o);
		if(superEquals){
			if(o instanceof AliasTableField){
				AliasTableField af = (AliasTableField) o;
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
	public String toString(){
		return toString(0);
	}
	@Override
	public String toString(int indent){
		
		StringBuffer indentBuffer = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			indentBuffer.append("	");
		}
		
		StringBuffer bf = new StringBuffer(this.iField.toString(indent));
		
		bf.append(indentBuffer.toString());
		bf.append("- aliasTable(Field): "+this.aliasTable);
		bf.append("\n");
				
		return bf.toString();
	}

}
