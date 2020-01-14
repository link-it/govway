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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiAnagraficiCessionarioType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiAnagraficiCessionarioType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiAnagraficiCessionarioTypeModel extends AbstractModel<DatiAnagraficiCessionarioType> {

	public DatiAnagraficiCessionarioTypeModel(){
	
		super();
	
		this.ID_FISCALE_IVA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IdFiscaleTypeModel(new Field("IdFiscaleIVA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IdFiscaleType.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class));
		this.CODICE_FISCALE = new Field("CodiceFiscale",java.lang.String.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class);
		this.ANAGRAFICA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.AnagraficaTypeModel(new Field("Anagrafica",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.AnagraficaType.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class));
	
	}
	
	public DatiAnagraficiCessionarioTypeModel(IField father){
	
		super(father);
	
		this.ID_FISCALE_IVA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IdFiscaleTypeModel(new ComplexField(father,"IdFiscaleIVA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IdFiscaleType.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class));
		this.CODICE_FISCALE = new ComplexField(father,"CodiceFiscale",java.lang.String.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class);
		this.ANAGRAFICA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.AnagraficaTypeModel(new ComplexField(father,"Anagrafica",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.AnagraficaType.class,"DatiAnagraficiCessionarioType",DatiAnagraficiCessionarioType.class));
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IdFiscaleTypeModel ID_FISCALE_IVA = null;
	 
	public IField CODICE_FISCALE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.AnagraficaTypeModel ANAGRAFICA = null;
	 

	@Override
	public Class<DatiAnagraficiCessionarioType> getModeledClass(){
		return DatiAnagraficiCessionarioType.class;
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