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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiGeneraliType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiGeneraliTypeModel extends AbstractModel<DatiGeneraliType> {

	public DatiGeneraliTypeModel(){
	
		super();
	
		this.DATI_GENERALI_DOCUMENTO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiGeneraliDocumentoTypeModel(new Field("DatiGeneraliDocumento",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_FATTURA_RETTIFICATA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiFatturaRettificataTypeModel(new Field("DatiFatturaRettificata",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType.class,"DatiGeneraliType",DatiGeneraliType.class));
	
	}
	
	public DatiGeneraliTypeModel(IField father){
	
		super(father);
	
		this.DATI_GENERALI_DOCUMENTO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiGeneraliDocumentoTypeModel(new ComplexField(father,"DatiGeneraliDocumento",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliDocumentoType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_FATTURA_RETTIFICATA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiFatturaRettificataTypeModel(new ComplexField(father,"DatiFatturaRettificata",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiFatturaRettificataType.class,"DatiGeneraliType",DatiGeneraliType.class));
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiGeneraliDocumentoTypeModel DATI_GENERALI_DOCUMENTO = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiFatturaRettificataTypeModel DATI_FATTURA_RETTIFICATA = null;
	 

	@Override
	public Class<DatiGeneraliType> getModeledClass(){
		return DatiGeneraliType.class;
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