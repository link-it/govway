/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import org.openspcoop2.protocol.information_missing.RequisitoInput;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RequisitoInput 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequisitoInputModel extends AbstractModel<RequisitoInput> {

	public RequisitoInputModel(){
	
		super();
	
		this.PROPRIETA = new org.openspcoop2.protocol.information_missing.model.ProprietaRequisitoInputModel(new Field("proprieta",org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput.class,"RequisitoInput",RequisitoInput.class));
		this.DESCRIZIONE = new Field("descrizione",java.lang.String.class,"RequisitoInput",RequisitoInput.class);
	
	}
	
	public RequisitoInputModel(IField father){
	
		super(father);
	
		this.PROPRIETA = new org.openspcoop2.protocol.information_missing.model.ProprietaRequisitoInputModel(new ComplexField(father,"proprieta",org.openspcoop2.protocol.information_missing.ProprietaRequisitoInput.class,"RequisitoInput",RequisitoInput.class));
		this.DESCRIZIONE = new ComplexField(father,"descrizione",java.lang.String.class,"RequisitoInput",RequisitoInput.class);
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.ProprietaRequisitoInputModel PROPRIETA = null;
	 
	public IField DESCRIZIONE = null;
	 

	@Override
	public Class<RequisitoInput> getModeledClass(){
		return RequisitoInput.class;
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