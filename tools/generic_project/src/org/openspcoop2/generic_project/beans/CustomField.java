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
 * CustomField
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CustomField extends Field {

	private String columnName;
	private String tableName;
	
	private String aliasColumnName;
	private String aliasTableName;
	
	public CustomField(String fieldName,Class<?> fieldType,String columnName,String tableName){
		super(fieldName, fieldType, CustomField.class.getSimpleName(), CustomField.class);
		this.columnName = columnName;
		this.tableName = tableName;
	}
	public CustomField(String fieldName,Class<?> fieldType,
			String columnName,String aliasColumnName,
			String tableName, String aliasTableName){
		super(fieldName, fieldType, CustomField.class.getSimpleName(), CustomField.class);
		this.columnName = columnName;
		this.aliasColumnName = aliasColumnName;
		this.tableName = tableName;
		this.aliasTableName = tableName;
	}

	public String getColumnName() {
		return this.columnName;
	}
	public String getAliasColumnName() {
		if(this.aliasColumnName!=null){
			return this.aliasColumnName;
		}
		else{
			return this.columnName;
		}
	}
	
	public String getTableName() {
		return this.tableName;
	}
	public String getAliasTableName() {
		if(this.aliasTableName!=null){
			return this.aliasTableName;
		}
		else{
			return this.tableName;
		}
	}

	@Override
	public String toString(int indent){
		
		StringBuffer indentBuffer = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			indentBuffer.append("	");
		}
		
		StringBuffer bf = new StringBuffer(super.toString(indent));
		
		bf.append(indentBuffer.toString());
		bf.append("- column name: "+this.columnName);
		bf.append("\n");
		
		bf.append(indentBuffer.toString());
		bf.append("- alias column name: "+this.aliasColumnName);
		bf.append("\n");
		
		bf.append(indentBuffer.toString());
		bf.append("- table name: "+this.tableName);
		bf.append("\n");
		
		bf.append(indentBuffer.toString());
		bf.append("- alias table name: "+this.aliasTableName);
		bf.append("\n");
		
		return bf.toString();
	}

}
