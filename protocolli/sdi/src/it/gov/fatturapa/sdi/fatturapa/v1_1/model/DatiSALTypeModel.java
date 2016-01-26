/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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
package it.gov.fatturapa.sdi.fatturapa.v1_1.model;

import it.gov.fatturapa.sdi.fatturapa.v1_1.DatiSALType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiSALType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiSALTypeModel extends AbstractModel<DatiSALType> {

	public DatiSALTypeModel(){
	
		super();
	
		this.RIFERIMENTO_FASE = new Field("RiferimentoFase",java.lang.Integer.class,"DatiSALType",DatiSALType.class);
	
	}
	
	public DatiSALTypeModel(IField father){
	
		super(father);
	
		this.RIFERIMENTO_FASE = new ComplexField(father,"RiferimentoFase",java.lang.Integer.class,"DatiSALType",DatiSALType.class);
	
	}
	
	

	public IField RIFERIMENTO_FASE = null;
	 

	@Override
	public Class<DatiSALType> getModeledClass(){
		return DatiSALType.class;
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