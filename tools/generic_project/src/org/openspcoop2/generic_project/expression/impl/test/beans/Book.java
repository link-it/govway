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
package org.openspcoop2.generic_project.expression.impl.test.beans;

import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.generic_project.expression.impl.test.constants.EnumerationDouble;
import org.openspcoop2.generic_project.expression.impl.test.constants.EnumerationString;
import org.openspcoop2.generic_project.expression.impl.test.constants.EnumerationWrapperPrimitiveInt;
import org.openspcoop2.generic_project.expression.impl.test.model.BookModel;

/**
 * Book
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Book {

	public Book(String title,String author){
		this.title = title;
		this.author = author;
	}

	private String title;
	private String author;
	private EnumerationString enumString;
	private EnumerationDouble enumDouble;
	private EnumerationWrapperPrimitiveInt enumWrapperPrimitiveInt;
	protected List<Version> version = new ArrayList<Version>();
	protected List<Version> reissue = new ArrayList<Version>();
	

	public void addVersion(Version version) {
		this.version.add(version);
	}
	
	public Version getVersion(int index) {
		return this.version.get( index );
	}

	public Version removeVersion(int index) {
		return this.version.remove( index );
	}

	public List<Version> getVersionList() {
		return this.version;
	}

	public void setVersionList(List<Version> version) {
		this.version=version;
	}

	public int sizeVersionList() {
		return this.version.size();
	}
	@Deprecated
	public List<Version> getVersion() {
		return this.version;
	} 
	@Deprecated
	public void setVersion(List<Version> version) {
		this.version=version;
	}
	@Deprecated
	public int sizeVersion() {
		return this.version.size();
	}
	
	public void addReissue(Version reissue) {
		this.reissue.add(reissue);
	}
	public Version getReissue(int index) {
		return this.reissue.get( index );
	}

	public Version removeReissue(int index) {
		return this.reissue.remove( index );
	}

	public List<Version> getReissueList() {
		return this.reissue;
	}

	public void setReissueList(List<Version> reissue) {
		this.reissue=reissue;
	}

	public int sizeReissueList() {
		return this.reissue.size();
	}
	@Deprecated
	public List<Version> getReissue() {
		return this.reissue;
	} 
	@Deprecated
	public void setReissue(List<Version> reissue) {
		this.reissue=reissue;
	}
	@Deprecated
	public int sizeReissue() {
		return this.reissue.size();
	}
	
	public static BookModel model(){
		return new BookModel();
	}
	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAuthor() {
		return this.author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public EnumerationString getEnumString() {
		return this.enumString;
	}
	public void setEnumString(EnumerationString enumString) {
		this.enumString = enumString;
	}
	public EnumerationDouble getEnumDouble() {
		return this.enumDouble;
	}
	public void setEnumDouble(EnumerationDouble enumDouble) {
		this.enumDouble = enumDouble;
	}
	public EnumerationWrapperPrimitiveInt getEnumWrapperPrimitiveInt() {
		return this.enumWrapperPrimitiveInt;
	}
	public void setEnumWrapperPrimitiveInt(
			EnumerationWrapperPrimitiveInt enumWrapperPrimitiveInt) {
		this.enumWrapperPrimitiveInt = enumWrapperPrimitiveInt;
	}
}
