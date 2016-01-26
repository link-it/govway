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
package org.openspcoop2.generic_project.expression.impl.sql;

import org.openspcoop2.generic_project.beans.AliasField;
import org.openspcoop2.generic_project.beans.ConstantField;
import org.openspcoop2.generic_project.beans.CustomField;
import org.openspcoop2.generic_project.beans.IAliasTableField;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;

/**
 * AbstractSQLFieldConverter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractSQLFieldConverter implements ISQLFieldConverter {

	
	@Override
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException {
		
		if(field instanceof CustomField){
			CustomField cf = (CustomField) field;
			
			if(appendTablePrefix){
				String table =  this.toAliasTable(field);
				if(table!=null && !"".equals(table)){
					if(returnAlias){
						return table+"."+cf.getAliasColumnName();
					}else{
						return table+"."+cf.getColumnName();
					}
				}
				else{
					if(returnAlias){
						return cf.getAliasColumnName();
					}else{
						return cf.getColumnName();
					}
				}
			}else{
				if(returnAlias){
					return cf.getAliasColumnName();
				}else{
					return cf.getColumnName();
				}
			}
		}
		
		else if(field instanceof ConstantField){
			ConstantField cf = (ConstantField) field;
			if(returnAlias){
				return cf.getAlias();
			}
			else{
				return cf.getConstantValue(this.getDatabaseType());
			}
		}
		
		else if(field instanceof AliasField){
			AliasField af = (AliasField) field;
//			if(returnAlias){
//				return af.getAlias();
//			}
//			else{
//				return this.toColumn(af.getField(), returnAlias, appendTablePrefix); 
//			}
			return af.getAlias(); // un alias deve usare sempre l'alias
		}
		
		else if(field instanceof IAliasTableField){
			
			IAliasTableField atf = (IAliasTableField) field;
			if(appendTablePrefix){
				return atf.getAliasTable()+"."+this.toColumn(atf.getField(), returnAlias, false);
			}
			else{
				return this.toColumn(atf.getField(), returnAlias, false);
			}
			
		}
		
		else{
			
			throw new ExpressionException("Field ["+field.toString()+"] not supported by converter.toColumn: "+this.getClass().getName());
			
		}
		
	}
	
	@Override
	public String toColumn(IField field,boolean appendTablePrefix) throws ExpressionException{
		return this.toColumn(field, false, appendTablePrefix);
	}
	
	@Override
	public String toAliasColumn(IField field,boolean appendTablePrefix) throws ExpressionException{
		return this.toColumn(field, true, appendTablePrefix);
	}
	
	
	
	
	
	
	
	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		
		if(field instanceof CustomField){
			CustomField cf = (CustomField) field;
			if(returnAlias){
				return cf.getAliasTableName();
			}else{
				return cf.getTableName();
			}
		}

		else if(field instanceof ConstantField){
			return this.toTable(this.getRootModel(), returnAlias);
		}
		
		else if(field instanceof AliasField){
			AliasField af = (AliasField) field;
			return this.toTable(af.getField(), returnAlias);
		}
		
		else if(field instanceof IAliasTableField){
			IAliasTableField af = (IAliasTableField) field;
			if(returnAlias){
				return af.getAliasTable();
			}
			else{
				return this.toTable(af.getField());
			}
		}
		
		else{
			throw new ExpressionException("Field ["+field.toString()+"] not supported by converter.toTable: "+this.getClass().getName());
		}
	}
	
	@Override
	public String toTable(IField field) throws ExpressionException {
		return this.toTable(field, false);
	}

	@Override
	public String toAliasTable(IField field) throws ExpressionException{
		return this.toTable(field, true);
	}
	
	
	
	
	
	
	
	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		throw new ExpressionException("Model ["+model.toString()+"] not supported by converter.toTable: "+this.getClass().getName());
	}

	@Override
	public String toTable(IModel<?> model) throws ExpressionException {
		return this.toTable(model, false);
	}

	@Override
	public String toAliasTable(IModel<?> model) throws ExpressionException{
		return this.toTable(model, true);
	}
	


	

	

}
