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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model SoggettoNotExistsBehaviour 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoggettoNotExistsBehaviourModel extends AbstractModel<SoggettoNotExistsBehaviour> {

	public SoggettoNotExistsBehaviourModel(){
	
		super();
	
		this.ENDPOINT = new Field("endpoint",java.lang.String.class,"SoggettoNotExistsBehaviour",SoggettoNotExistsBehaviour.class);
		this.PORTA_DOMINIO = new Field("porta-dominio",java.lang.String.class,"SoggettoNotExistsBehaviour",SoggettoNotExistsBehaviour.class);
		this.CREATE = new Field("create",boolean.class,"SoggettoNotExistsBehaviour",SoggettoNotExistsBehaviour.class);
	
	}
	
	public SoggettoNotExistsBehaviourModel(IField father){
	
		super(father);
	
		this.ENDPOINT = new ComplexField(father,"endpoint",java.lang.String.class,"SoggettoNotExistsBehaviour",SoggettoNotExistsBehaviour.class);
		this.PORTA_DOMINIO = new ComplexField(father,"porta-dominio",java.lang.String.class,"SoggettoNotExistsBehaviour",SoggettoNotExistsBehaviour.class);
		this.CREATE = new ComplexField(father,"create",boolean.class,"SoggettoNotExistsBehaviour",SoggettoNotExistsBehaviour.class);
	
	}
	
	

	public IField ENDPOINT = null;
	 
	public IField PORTA_DOMINIO = null;
	 
	public IField CREATE = null;
	 

	@Override
	public Class<SoggettoNotExistsBehaviour> getModeledClass(){
		return SoggettoNotExistsBehaviour.class;
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