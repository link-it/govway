/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.message.context.model;

import org.openspcoop2.message.context.SerializedParameter;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SerializedParameter 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SerializedParameterModel extends AbstractModel<SerializedParameter> {

	public SerializedParameterModel(){
	
		super();
	
		this.BASE = new Field("base",byte[].class,"serialized-parameter",SerializedParameter.class);
		this.NOME = new Field("nome",java.lang.String.class,"serialized-parameter",SerializedParameter.class);
		this.CLASSE = new Field("classe",java.lang.String.class,"serialized-parameter",SerializedParameter.class);
	
	}
	
	public SerializedParameterModel(IField father){
	
		super(father);
	
		this.BASE = new ComplexField(father,"base",byte[].class,"serialized-parameter",SerializedParameter.class);
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"serialized-parameter",SerializedParameter.class);
		this.CLASSE = new ComplexField(father,"classe",java.lang.String.class,"serialized-parameter",SerializedParameter.class);
	
	}
	
	

	public IField BASE = null;
	 
	public IField NOME = null;
	 
	public IField CLASSE = null;
	 

	@Override
	public Class<SerializedParameter> getModeledClass(){
		return SerializedParameter.class;
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