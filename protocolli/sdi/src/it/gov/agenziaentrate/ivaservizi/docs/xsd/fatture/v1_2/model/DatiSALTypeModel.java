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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiSALType;

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
	
		this.RIFERIMENTO_FASE = new Field("RiferimentoFase",java.math.BigInteger.class,"DatiSALType",DatiSALType.class);
	
	}
	
	public DatiSALTypeModel(IField father){
	
		super(father);
	
		this.RIFERIMENTO_FASE = new ComplexField(father,"RiferimentoFase",java.math.BigInteger.class,"DatiSALType",DatiSALType.class);
	
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