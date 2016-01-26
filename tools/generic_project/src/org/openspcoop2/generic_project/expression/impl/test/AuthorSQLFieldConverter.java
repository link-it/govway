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
package org.openspcoop2.generic_project.expression.impl.test;

import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.IModel;
import org.openspcoop2.generic_project.exception.ExpressionException;
import org.openspcoop2.generic_project.expression.impl.sql.AbstractSQLFieldConverter;
import org.openspcoop2.generic_project.expression.impl.test.beans.Author;
import org.openspcoop2.utils.TipiDatabase;

/**
 * AuthorSQLFieldConverter
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthorSQLFieldConverter extends AbstractSQLFieldConverter {

	@Override
	public IModel<?> getRootModel() throws ExpressionException {
		return Author.model();
	}

	@Override
	public TipiDatabase getDatabaseType() throws ExpressionException {
		return TipiDatabase.DEFAULT;
	}
	
	
	
	@Override
	public String toColumn(IField field,boolean returnAlias,boolean appendTablePrefix) throws ExpressionException {
		
		if(field.equals(Author.model().AGE)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".age";
			else{
				return "age";
			}
		}else if(field.equals(Author.model().BANK_ACCOUNT)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".bank_account";
			else
				return "bank_account";
		}else if(field.equals(Author.model().DATE_OF_BIRTH)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".date";
			else
				return "date";
		}else if(field.equals(Author.model().FIRST_BOOK_RELEASE_DATE)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".first_release";
			else
				return "first_release";
		}else if(field.equals(Author.model().LAST_BOOK_RELEASE_DATE)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".last_release";
			else 
				return "last_release";
		}else if(field.equals(Author.model().NAME)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".name";
			else
				return "name";
		}else if(field.equals(Author.model().SECOND_BANK_ACCOUNT)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".second_account";
			else
				return "second_account";
		}else if(field.equals(Author.model().SINGLE)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".single";
			else
				return "single";
		}else if(field.equals(Author.model().SURNAME)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".surname";
			else
				return "surname";
		}else if(field.equals(Author.model().WEIGHT)){
			if(appendTablePrefix)
				return this.toAliasTable(field)+".weight";
			else
				return "weight";
		}
		else{
			throw new ExpressionException("Field ["+field.toString()+"] not supported");
		}
		
	}



	@Override
	public String toTable(IField field,boolean returnAlias)
			throws ExpressionException {
		if(!returnAlias){
			return "author a";
		}else{
			return "a";
		}
	}
	
	@Override
	public String toTable(IModel<?> model,boolean returnAlias) throws ExpressionException {
		if(!returnAlias){
			return "author a";
		}else{
			return "a";
		}
	}



}
