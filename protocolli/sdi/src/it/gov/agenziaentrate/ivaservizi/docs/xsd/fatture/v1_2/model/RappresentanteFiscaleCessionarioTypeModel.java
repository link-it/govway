/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.RappresentanteFiscaleCessionarioType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RappresentanteFiscaleCessionarioType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RappresentanteFiscaleCessionarioTypeModel extends AbstractModel<RappresentanteFiscaleCessionarioType> {

	public RappresentanteFiscaleCessionarioTypeModel(){
	
		super();
	
		this.ID_FISCALE_IVA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IdFiscaleTypeModel(new Field("IdFiscaleIVA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IdFiscaleType.class,"RappresentanteFiscaleCessionarioType",RappresentanteFiscaleCessionarioType.class));
		this.DENOMINAZIONE = new Field("Denominazione",java.lang.String.class,"RappresentanteFiscaleCessionarioType",RappresentanteFiscaleCessionarioType.class);
		this.NOME = new Field("Nome",java.lang.String.class,"RappresentanteFiscaleCessionarioType",RappresentanteFiscaleCessionarioType.class);
		this.COGNOME = new Field("Cognome",java.lang.String.class,"RappresentanteFiscaleCessionarioType",RappresentanteFiscaleCessionarioType.class);
	
	}
	
	public RappresentanteFiscaleCessionarioTypeModel(IField father){
	
		super(father);
	
		this.ID_FISCALE_IVA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IdFiscaleTypeModel(new ComplexField(father,"IdFiscaleIVA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IdFiscaleType.class,"RappresentanteFiscaleCessionarioType",RappresentanteFiscaleCessionarioType.class));
		this.DENOMINAZIONE = new ComplexField(father,"Denominazione",java.lang.String.class,"RappresentanteFiscaleCessionarioType",RappresentanteFiscaleCessionarioType.class);
		this.NOME = new ComplexField(father,"Nome",java.lang.String.class,"RappresentanteFiscaleCessionarioType",RappresentanteFiscaleCessionarioType.class);
		this.COGNOME = new ComplexField(father,"Cognome",java.lang.String.class,"RappresentanteFiscaleCessionarioType",RappresentanteFiscaleCessionarioType.class);
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IdFiscaleTypeModel ID_FISCALE_IVA = null;
	 
	public IField DENOMINAZIONE = null;
	 
	public IField NOME = null;
	 
	public IField COGNOME = null;
	 

	@Override
	public Class<RappresentanteFiscaleCessionarioType> getModeledClass(){
		return RappresentanteFiscaleCessionarioType.class;
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