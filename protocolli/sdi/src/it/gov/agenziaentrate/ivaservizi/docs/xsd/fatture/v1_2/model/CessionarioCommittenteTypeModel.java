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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.CessionarioCommittenteType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CessionarioCommittenteType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CessionarioCommittenteTypeModel extends AbstractModel<CessionarioCommittenteType> {

	public CessionarioCommittenteTypeModel(){
	
		super();
	
		this.DATI_ANAGRAFICI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiAnagraficiCessionarioTypeModel(new Field("DatiAnagrafici",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiAnagraficiCessionarioType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
		this.SEDE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel(new Field("Sede",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IndirizzoType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
		this.STABILE_ORGANIZZAZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel(new Field("StabileOrganizzazione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IndirizzoType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
		this.RAPPRESENTANTE_FISCALE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.RappresentanteFiscaleCessionarioTypeModel(new Field("RappresentanteFiscale",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.RappresentanteFiscaleCessionarioType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
	
	}
	
	public CessionarioCommittenteTypeModel(IField father){
	
		super(father);
	
		this.DATI_ANAGRAFICI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiAnagraficiCessionarioTypeModel(new ComplexField(father,"DatiAnagrafici",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiAnagraficiCessionarioType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
		this.SEDE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel(new ComplexField(father,"Sede",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IndirizzoType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
		this.STABILE_ORGANIZZAZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel(new ComplexField(father,"StabileOrganizzazione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IndirizzoType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
		this.RAPPRESENTANTE_FISCALE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.RappresentanteFiscaleCessionarioTypeModel(new ComplexField(father,"RappresentanteFiscale",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.RappresentanteFiscaleCessionarioType.class,"CessionarioCommittenteType",CessionarioCommittenteType.class));
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiAnagraficiCessionarioTypeModel DATI_ANAGRAFICI = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel SEDE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel STABILE_ORGANIZZAZIONE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.RappresentanteFiscaleCessionarioTypeModel RAPPRESENTANTE_FISCALE = null;
	 

	@Override
	public Class<CessionarioCommittenteType> getModeledClass(){
		return CessionarioCommittenteType.class;
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