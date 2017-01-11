/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiBeniServiziType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiBeniServiziType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiBeniServiziTypeModel extends AbstractModel<DatiBeniServiziType> {

	public DatiBeniServiziTypeModel(){
	
		super();
	
		this.DETTAGLIO_LINEE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DettaglioLineeTypeModel(new Field("DettaglioLinee",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DettaglioLineeType.class,"DatiBeniServiziType",DatiBeniServiziType.class));
		this.DATI_RIEPILOGO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiRiepilogoTypeModel(new Field("DatiRiepilogo",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiRiepilogoType.class,"DatiBeniServiziType",DatiBeniServiziType.class));
	
	}
	
	public DatiBeniServiziTypeModel(IField father){
	
		super(father);
	
		this.DETTAGLIO_LINEE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DettaglioLineeTypeModel(new ComplexField(father,"DettaglioLinee",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DettaglioLineeType.class,"DatiBeniServiziType",DatiBeniServiziType.class));
		this.DATI_RIEPILOGO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiRiepilogoTypeModel(new ComplexField(father,"DatiRiepilogo",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiRiepilogoType.class,"DatiBeniServiziType",DatiBeniServiziType.class));
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DettaglioLineeTypeModel DETTAGLIO_LINEE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiRiepilogoTypeModel DATI_RIEPILOGO = null;
	 

	@Override
	public Class<DatiBeniServiziType> getModeledClass(){
		return DatiBeniServiziType.class;
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