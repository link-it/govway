/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.AltriDatiIdentificativiType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AltriDatiIdentificativiType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AltriDatiIdentificativiTypeModel extends AbstractModel<AltriDatiIdentificativiType> {

	public AltriDatiIdentificativiTypeModel(){
	
		super();
	
		this.DENOMINAZIONE = new Field("Denominazione",java.lang.String.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class);
		this.NOME = new Field("Nome",java.lang.String.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class);
		this.COGNOME = new Field("Cognome",java.lang.String.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class);
		this.SEDE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel(new Field("Sede",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class));
		this.STABILE_ORGANIZZAZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel(new Field("StabileOrganizzazione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class));
		this.RAPPRESENTANTE_FISCALE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.RappresentanteFiscaleTypeModel(new Field("RappresentanteFiscale",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class));
	
	}
	
	public AltriDatiIdentificativiTypeModel(IField father){
	
		super(father);
	
		this.DENOMINAZIONE = new ComplexField(father,"Denominazione",java.lang.String.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class);
		this.NOME = new ComplexField(father,"Nome",java.lang.String.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class);
		this.COGNOME = new ComplexField(father,"Cognome",java.lang.String.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class);
		this.SEDE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel(new ComplexField(father,"Sede",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class));
		this.STABILE_ORGANIZZAZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel(new ComplexField(father,"StabileOrganizzazione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class));
		this.RAPPRESENTANTE_FISCALE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.RappresentanteFiscaleTypeModel(new ComplexField(father,"RappresentanteFiscale",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType.class,"AltriDatiIdentificativiType",AltriDatiIdentificativiType.class));
	
	}
	
	

	public IField DENOMINAZIONE = null;
	 
	public IField NOME = null;
	 
	public IField COGNOME = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel SEDE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel STABILE_ORGANIZZAZIONE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.RappresentanteFiscaleTypeModel RAPPRESENTANTE_FISCALE = null;
	 

	@Override
	public Class<AltriDatiIdentificativiType> getModeledClass(){
		return AltriDatiIdentificativiType.class;
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