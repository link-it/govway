/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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
package it.gov.spcoop.sica.wsbl.model;

import it.gov.spcoop.sica.wsbl.GuardType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model GuardType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GuardTypeModel extends AbstractModel<GuardType> {

	public GuardTypeModel(){
	
		super();
	
		this.DESCRIPTION = new Field("description",java.lang.String.class,"guardType",GuardType.class);
		this.RULE = new Field("rule",java.lang.String.class,"guardType",GuardType.class);
		this.NAME = new Field("name",java.lang.String.class,"guardType",GuardType.class);
	
	}
	
	public GuardTypeModel(IField father){
	
		super(father);
	
		this.DESCRIPTION = new ComplexField(father,"description",java.lang.String.class,"guardType",GuardType.class);
		this.RULE = new ComplexField(father,"rule",java.lang.String.class,"guardType",GuardType.class);
		this.NAME = new ComplexField(father,"name",java.lang.String.class,"guardType",GuardType.class);
	
	}
	
	

	public IField DESCRIPTION = null;
	 
	public IField RULE = null;
	 
	public IField NAME = null;
	 

	@Override
	public Class<GuardType> getModeledClass(){
		return GuardType.class;
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