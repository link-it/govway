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
package org.openspcoop2.generic_project.expression.impl.test.model;


import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.expression.impl.test.beans.Author;

/**
 * AuthorModel
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AuthorModel extends AbstractModel<Author> {

	public AuthorModel(){
		
		super();
		
		this.NAME = new Field("name", String.class, "author", Author.class);
		this.SURNAME = new Field("surname", String.class, "author", Author.class);
		this.SINGLE = new Field("single", boolean.class, "author", Author.class);
		this.AGE = new Field("age", int.class, "author", Author.class);
		this.WEIGHT = new Field("weight", long.class, "author", Author.class);
		this.BANK_ACCOUNT = new Field("bankAccount", double.class, "author", Author.class);
		this.SECOND_BANK_ACCOUNT = new Field("secondBankAccount", float.class, "author", Author.class);
		this.DATE_OF_BIRTH = new Field("dateOfBirth", Date.class, "author", Author.class);
		this.FIRST_BOOK_RELEASE_DATE = new Field("firstBookReleaseDate", Calendar.class, "author", Author.class);
		this.LAST_BOOK_RELEASE_DATE = new Field("lastBookReleaseDate", Timestamp.class, "author", Author.class);
		
	}
	
	public IField NAME = null;
	public IField SURNAME = null;
	public IField SINGLE = null;
	public IField AGE = null;
	public IField WEIGHT = null;
	public IField BANK_ACCOUNT = null;
	public IField SECOND_BANK_ACCOUNT = null;
	public IField DATE_OF_BIRTH = null;
	public IField FIRST_BOOK_RELEASE_DATE = null;
	public IField LAST_BOOK_RELEASE_DATE = null;
	
	@Override
	public Class<Author> getModeledClass(){
		return Author.class;
	}

}
