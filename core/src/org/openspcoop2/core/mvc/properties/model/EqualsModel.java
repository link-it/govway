/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.core.mvc.properties.model;

import org.openspcoop2.core.mvc.properties.Equals;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Equals 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EqualsModel extends AbstractModel<Equals> {

	public EqualsModel(){
	
		super();
	
		this.NAME = new Field("name",java.lang.String.class,"equals",Equals.class);
		this.VALUE = new Field("value",java.lang.String.class,"equals",Equals.class);
		this.NOT = new Field("not",boolean.class,"equals",Equals.class);
	
	}
	
	public EqualsModel(IField father){
	
		super(father);
	
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"equals",Equals.class);
		this.VALUE = new ComplexField(father,"value",java.lang.String.class,"equals",Equals.class);
		this.NOT = new ComplexField(father,"not",boolean.class,"equals",Equals.class);
	
	}
	
	

	public IField NAME = null;
	 
	public IField VALUE = null;
	 
	public IField NOT = null;
	 

	@Override
	public Class<Equals> getModeledClass(){
		return Equals.class;
	}
	
	@Override
	public String toString(){
		if(this.getModeledClass()!=null){
			return this.getModeledClass().getName();
		}else{
			return "N.D.";
		}
	}

}