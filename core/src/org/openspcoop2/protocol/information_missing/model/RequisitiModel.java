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

import org.openspcoop2.protocol.information_missing.Requisiti;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Requisiti 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequisitiModel extends AbstractModel<Requisiti> {

	public RequisitiModel(){
	
		super();
	
		this.PROTOCOLLO = new org.openspcoop2.protocol.information_missing.model.RequisitoProtocolloModel(new Field("protocollo",org.openspcoop2.protocol.information_missing.RequisitoProtocollo.class,"Requisiti",Requisiti.class));
		this.INPUT = new org.openspcoop2.protocol.information_missing.model.RequisitoInputModel(new Field("input",org.openspcoop2.protocol.information_missing.RequisitoInput.class,"Requisiti",Requisiti.class));
	
	}
	
	public RequisitiModel(IField father){
	
		super(father);
	
		this.PROTOCOLLO = new org.openspcoop2.protocol.information_missing.model.RequisitoProtocolloModel(new ComplexField(father,"protocollo",org.openspcoop2.protocol.information_missing.RequisitoProtocollo.class,"Requisiti",Requisiti.class));
		this.INPUT = new org.openspcoop2.protocol.information_missing.model.RequisitoInputModel(new ComplexField(father,"input",org.openspcoop2.protocol.information_missing.RequisitoInput.class,"Requisiti",Requisiti.class));
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.RequisitoProtocolloModel PROTOCOLLO = null;
	 
	public org.openspcoop2.protocol.information_missing.model.RequisitoInputModel INPUT = null;
	 

	@Override
	public Class<Requisiti> getModeledClass(){
		return Requisiti.class;
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