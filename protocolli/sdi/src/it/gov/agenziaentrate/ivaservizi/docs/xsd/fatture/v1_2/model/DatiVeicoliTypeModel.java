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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiVeicoliType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiVeicoliType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiVeicoliTypeModel extends AbstractModel<DatiVeicoliType> {

	public DatiVeicoliTypeModel(){
	
		super();
	
		this.DATA = new Field("Data",java.util.Date.class,"DatiVeicoliType",DatiVeicoliType.class);
		this.TOTALE_PERCORSO = new Field("TotalePercorso",java.lang.String.class,"DatiVeicoliType",DatiVeicoliType.class);
	
	}
	
	public DatiVeicoliTypeModel(IField father){
	
		super(father);
	
		this.DATA = new ComplexField(father,"Data",java.util.Date.class,"DatiVeicoliType",DatiVeicoliType.class);
		this.TOTALE_PERCORSO = new ComplexField(father,"TotalePercorso",java.lang.String.class,"DatiVeicoliType",DatiVeicoliType.class);
	
	}
	
	

	public IField DATA = null;
	 
	public IField TOTALE_PERCORSO = null;
	 

	@Override
	public Class<DatiVeicoliType> getModeledClass(){
		return DatiVeicoliType.class;
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