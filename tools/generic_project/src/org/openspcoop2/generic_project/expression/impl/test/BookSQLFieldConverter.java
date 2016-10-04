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
package org.openspcoop2.generic_project.expression.impl.test;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.generic_project.expression.impl.test.beans.Book;
import org.openspcoop2.utils.TipiDatabase;

/**
 * BookSQLFieldConverter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BookSQLFieldConverter extends AbstractSQLFieldConverter {

	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return Book.model();
	}

	@Override
	public TipiDatabase getDatabaseType() throws ExpressionException {
		return TipiDatabase.DEFAULT;
	}
	
	
	
	@Override
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException {
		
		if(field.equals(Book.model().AUTHOR)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".author";
			else
				return "author";
		}else if(field.equals(Book.model().TITLE)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".title";
			else
				return "title";
		}else if(field.equals(Book.model().ENUM_STRING)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".enum_string";
			else
				return "enum_string";
		}else if(field.equals(Book.model().ENUM_DOUBLE)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".enum_double";
			else
				return "enum_double";
		}else if(field.equals(Book.model().ENUM_WRAPPER_PRIMITIVE_INT)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".enum_wrapper_primitive_int";
			else
				return "enum_wrapper_primitive_int";
		}else if(field.equals(Book.model().VERSION.DATE)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".date";
			else
				return "date";
		}else if(field.equals(Book.model().VERSION.NUMBER)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".number";
			else
				return "number";
		}else if(field.equals(Book.model().REISSUE.DATE)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".date";
			else
				return "date";
		}else if(field.equals(Book.model().REISSUE.NUMBER)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".number";
			else
				return "number";
		}
		else{
			throw new ExpressionException("Field ["+field.toString()+"] not supported");
		}
		
	}

	@Override
	public String toTable(IField field,boolean returnAlias) throws ExpressionException {
		if(field.equals(Book.model().AUTHOR)){
			if(!returnAlias){
				return "book b";
			}else{
				return "b";
			}
		}else if(field.equals(Book.model().TITLE)){
			if(!returnAlias){
				return "book b";
			}else{
				return "b";
			}
		}else if(field.equals(Book.model().ENUM_STRING)){
			if(!returnAlias){
				return "book b";
			}else{
				return "b";
			}
		}else if(field.equals(Book.model().ENUM_DOUBLE)){
			if(!returnAlias){
				return "book b";
			}else{
				return "b";
			}
		}else if(field.equals(Book.model().ENUM_WRAPPER_PRIMITIVE_INT)){
			if(!returnAlias){
				return "book b";
			}else{
				return "b";
			}
		}else if(field.equals(Book.model().VERSION.DATE)){
			if(!returnAlias){
				return "version v";
			}else{
				return "v";
			}
		}else if(field.equals(Book.model().VERSION.NUMBER)){
			if(!returnAlias){
				return "version v";
			}else{
				return "v";
			}
		}else if(field.equals(Book.model().REISSUE.DATE)){
			if(!returnAlias){
				return "reissue r";
			}else{
				return "r";
			}
		}else if(field.equals(Book.model().REISSUE.NUMBER)){
			if(!returnAlias){
				return "reissue r";
			}else{
				return "r";
			}
		}
		else{
			throw new ExpressionException("Field ["+field.toString()+"] not supported");
		}
	}
	
	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		if(model.equals(Book.model())){
			if(!returnAlias){
				return "book b";
			}else{
				return "b";
			}
		}
		else if(model.equals(Book.model().VERSION)){
			if(!returnAlias){
				return "version v";
			}else{
				return "v";
			}
		}
		else if(model.equals(Book.model().REISSUE)){
			if(!returnAlias){
				return "reissue r";
			}else{
				return "r";
			}
		}
		else{
			return "book b";
		}
	}

}
