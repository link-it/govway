/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.ResourceParameter;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model ResourceParameter 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ResourceParameterModel extends AbstractModel<ResourceParameter> {

	public ResourceParameterModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"resource-parameter",ResourceParameter.class);
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"resource-parameter",ResourceParameter.class);
		this.PARAMETER_TYPE = new Field("parameter-type",java.lang.String.class,"resource-parameter",ResourceParameter.class);
		this.REQUIRED = new Field("required",boolean.class,"resource-parameter",ResourceParameter.class);
		this.TIPO = new Field("tipo",java.lang.String.class,"resource-parameter",ResourceParameter.class);
	
	}
	
	public ResourceParameterModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"resource-parameter",ResourceParameter.class);
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"resource-parameter",ResourceParameter.class);
		this.PARAMETER_TYPE = new ComplexField(father,"parameter-type",java.lang.String.class,"resource-parameter",ResourceParameter.class);
		this.REQUIRED = new ComplexField(father,"required",boolean.class,"resource-parameter",ResourceParameter.class);
		this.TIPO = new ComplexField(father,"tipo",java.lang.String.class,"resource-parameter",ResourceParameter.class);
	
	}
	
	

	public IField NOME = null;
	 
	public IField DESCRIZIONE = null;
	 
	public IField PARAMETER_TYPE = null;
	 
	public IField REQUIRED = null;
	 
	public IField TIPO = null;
	 

	@Override
	public Class<ResourceParameter> getModeledClass(){
		return ResourceParameter.class;
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