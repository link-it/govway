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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiBeniServiziType;

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
	
		this.DESCRIZIONE = new Field("Descrizione",java.lang.String.class,"DatiBeniServiziType",DatiBeniServiziType.class);
		this.IMPORTO = new Field("Importo",java.math.BigDecimal.class,"DatiBeniServiziType",DatiBeniServiziType.class);
		this.DATI_IVA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiIVATypeModel(new Field("DatiIVA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType.class,"DatiBeniServiziType",DatiBeniServiziType.class));
		this.NATURA = new Field("Natura",java.lang.String.class,"DatiBeniServiziType",DatiBeniServiziType.class);
		this.RIFERIMENTO_NORMATIVO = new Field("RiferimentoNormativo",java.lang.String.class,"DatiBeniServiziType",DatiBeniServiziType.class);
	
	}
	
	public DatiBeniServiziTypeModel(IField father){
	
		super(father);
	
		this.DESCRIZIONE = new ComplexField(father,"Descrizione",java.lang.String.class,"DatiBeniServiziType",DatiBeniServiziType.class);
		this.IMPORTO = new ComplexField(father,"Importo",java.math.BigDecimal.class,"DatiBeniServiziType",DatiBeniServiziType.class);
		this.DATI_IVA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiIVATypeModel(new ComplexField(father,"DatiIVA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiIVAType.class,"DatiBeniServiziType",DatiBeniServiziType.class));
		this.NATURA = new ComplexField(father,"Natura",java.lang.String.class,"DatiBeniServiziType",DatiBeniServiziType.class);
		this.RIFERIMENTO_NORMATIVO = new ComplexField(father,"RiferimentoNormativo",java.lang.String.class,"DatiBeniServiziType",DatiBeniServiziType.class);
	
	}
	
	

	public IField DESCRIZIONE = null;
	 
	public IField IMPORTO = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiIVATypeModel DATI_IVA = null;
	 
	public IField NATURA = null;
	 
	public IField RIFERIMENTO_NORMATIVO = null;
	 

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