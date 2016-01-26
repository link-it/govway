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
package org.openspcoop2.generic_project.expression.impl.test.model;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.expression.impl.test.beans.Book;
import org.openspcoop2.generic_project.expression.impl.test.beans.Version;

/**
 * BookModel
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BookModel extends AbstractModel<Book> {

	public BookModel(){
		
		super();
		
		this.TITLE = new Field("title", String.class, "book", Book.class);
		this.AUTHOR = new Field("author", String.class, "book", Book.class);
		this.ENUM_STRING = new Field("enum-string", String.class, "book", Book.class);
		this.ENUM_DOUBLE = new Field("enum-double", double.class, "book", Book.class);
		this.ENUM_WRAPPER_PRIMITIVE_INT = new Field("enum-wrapper-primitive-int", Integer.class, "book", Book.class);
		this.VERSION = new VersionModel(new Field("version",Version.class,"book", Book.class));
		this.REISSUE = new VersionModel(new Field("reissue",Version.class,"book", Book.class));
		
	}
	
	public IField TITLE = null;
	public IField AUTHOR = null;
	public IField ENUM_STRING = null;
	public IField ENUM_DOUBLE = null;
	public IField ENUM_WRAPPER_PRIMITIVE_INT = null;
	public VersionModel VERSION = null;
	public VersionModel REISSUE = null;
	
	@Override
	public Class<Book> getModeledClass(){
		return Book.class;
	}

}
