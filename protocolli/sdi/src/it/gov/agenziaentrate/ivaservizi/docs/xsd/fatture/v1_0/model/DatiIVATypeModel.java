/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiIVAType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiIVATypeModel extends AbstractModel<DatiIVAType> {

	public DatiIVATypeModel(){
	
		super();
	
		this.IMPOSTA = new Field("Imposta",java.math.BigDecimal.class,"DatiIVAType",DatiIVAType.class);
		this.ALIQUOTA = new Field("Aliquota",java.math.BigDecimal.class,"DatiIVAType",DatiIVAType.class);
	
	}
	
	public DatiIVATypeModel(IField father){
	
		super(father);
	
		this.IMPOSTA = new ComplexField(father,"Imposta",java.math.BigDecimal.class,"DatiIVAType",DatiIVAType.class);
		this.ALIQUOTA = new ComplexField(father,"Aliquota",java.math.BigDecimal.class,"DatiIVAType",DatiIVAType.class);
	
	}
	
	

	public IField IMPOSTA = null;
	 
	public IField ALIQUOTA = null;
	 

	@Override
	public Class<DatiIVAType> getModeledClass(){
		return DatiIVAType.class;
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