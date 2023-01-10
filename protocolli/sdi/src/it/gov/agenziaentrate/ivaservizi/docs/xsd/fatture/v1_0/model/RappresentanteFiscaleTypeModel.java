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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RappresentanteFiscaleType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RappresentanteFiscaleTypeModel extends AbstractModel<RappresentanteFiscaleType> {

	public RappresentanteFiscaleTypeModel(){
	
		super();
	
		this.ID_FISCALE_IVA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IdFiscaleTypeModel(new Field("IdFiscaleIVA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType.class,"RappresentanteFiscaleType",RappresentanteFiscaleType.class));
		this.DENOMINAZIONE = new Field("Denominazione",java.lang.String.class,"RappresentanteFiscaleType",RappresentanteFiscaleType.class);
		this.NOME = new Field("Nome",java.lang.String.class,"RappresentanteFiscaleType",RappresentanteFiscaleType.class);
		this.COGNOME = new Field("Cognome",java.lang.String.class,"RappresentanteFiscaleType",RappresentanteFiscaleType.class);
	
	}
	
	public RappresentanteFiscaleTypeModel(IField father){
	
		super(father);
	
		this.ID_FISCALE_IVA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IdFiscaleTypeModel(new ComplexField(father,"IdFiscaleIVA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType.class,"RappresentanteFiscaleType",RappresentanteFiscaleType.class));
		this.DENOMINAZIONE = new ComplexField(father,"Denominazione",java.lang.String.class,"RappresentanteFiscaleType",RappresentanteFiscaleType.class);
		this.NOME = new ComplexField(father,"Nome",java.lang.String.class,"RappresentanteFiscaleType",RappresentanteFiscaleType.class);
		this.COGNOME = new ComplexField(father,"Cognome",java.lang.String.class,"RappresentanteFiscaleType",RappresentanteFiscaleType.class);
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IdFiscaleTypeModel ID_FISCALE_IVA = null;
	 
	public IField DENOMINAZIONE = null;
	 
	public IField NOME = null;
	 
	public IField COGNOME = null;
	 

	@Override
	public Class<RappresentanteFiscaleType> getModeledClass(){
		return RappresentanteFiscaleType.class;
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