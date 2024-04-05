/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.DescriptionType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DescriptionType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DescriptionTypeModel extends AbstractModel<DescriptionType> {

	public DescriptionTypeModel(){
	
		super();
	
		this.TIPO = new Field("tipo",java.lang.String.class,"DescriptionType",DescriptionType.class);
		this.VALORE = new Field("valore",java.lang.String.class,"DescriptionType",DescriptionType.class);
		this.LABEL = new Field("label",java.lang.String.class,"DescriptionType",DescriptionType.class);
		this.BOLD = new Field("bold",boolean.class,"DescriptionType",DescriptionType.class);
	
	}
	
	public DescriptionTypeModel(IField father){
	
		super(father);
	
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"DescriptionType",DescriptionType.class);
		this.VALORE = new ComplexField(father,"valore",java.lang.String.class,"DescriptionType",DescriptionType.class);
		this.LABEL = new ComplexField(father,"label",java.lang.String.class,"DescriptionType",DescriptionType.class);
		this.BOLD = new ComplexField(father,"bold",boolean.class,"DescriptionType",DescriptionType.class);
	
	}
	
	

	public IField TIPO = null;
	 
	public IField VALORE = null;
	 
	public IField LABEL = null;
	 
	public IField BOLD = null;
	 

	@Override
	public Class<DescriptionType> getModeledClass(){
		return DescriptionType.class;
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