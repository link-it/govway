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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.RequisitoProtocollo;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RequisitoProtocollo 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RequisitoProtocolloModel extends AbstractModel<RequisitoProtocollo> {

	public RequisitoProtocolloModel(){
	
		super();
	
		this.NOME = new Field("nome",java.lang.String.class,"RequisitoProtocollo",RequisitoProtocollo.class);
	
	}
	
	public RequisitoProtocolloModel(IField father){
	
		super(father);
	
		this.NOME = new ComplexField(father,"nome",java.lang.String.class,"RequisitoProtocollo",RequisitoProtocollo.class);
	
	}
	
	

	public IField NOME = null;
	 

	@Override
	public Class<RequisitoProtocollo> getModeledClass(){
		return RequisitoProtocollo.class;
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