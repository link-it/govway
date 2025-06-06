/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaBodyType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FatturaElettronicaBodyType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FatturaElettronicaBodyTypeModel extends AbstractModel<FatturaElettronicaBodyType> {

	public FatturaElettronicaBodyTypeModel(){
	
		super();
	
		this.DATI_GENERALI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiGeneraliTypeModel(new Field("DatiGenerali",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.DATI_BENI_SERVIZI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiBeniServiziTypeModel(new Field("DatiBeniServizi",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.ALLEGATI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.AllegatiTypeModel(new Field("Allegati",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
	
	}
	
	public FatturaElettronicaBodyTypeModel(IField father){
	
		super(father);
	
		this.DATI_GENERALI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiGeneraliTypeModel(new ComplexField(father,"DatiGenerali",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiGeneraliType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.DATI_BENI_SERVIZI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiBeniServiziTypeModel(new ComplexField(father,"DatiBeniServizi",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
		this.ALLEGATI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.AllegatiTypeModel(new ComplexField(father,"Allegati",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AllegatiType.class,"FatturaElettronicaBodyType",FatturaElettronicaBodyType.class));
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiGeneraliTypeModel DATI_GENERALI = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiBeniServiziTypeModel DATI_BENI_SERVIZI = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.AllegatiTypeModel ALLEGATI = null;
	 

	@Override
	public Class<FatturaElettronicaBodyType> getModeledClass(){
		return FatturaElettronicaBodyType.class;
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