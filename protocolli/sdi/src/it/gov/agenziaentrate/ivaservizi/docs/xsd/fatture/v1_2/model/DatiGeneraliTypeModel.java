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
package it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiGeneraliType;

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
	
		this.DATI_GENERALI_DOCUMENTO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiGeneraliDocumentoTypeModel(new Field("DatiGeneraliDocumento",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiGeneraliDocumentoType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_ORDINE_ACQUISTO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel(new Field("DatiOrdineAcquisto",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDocumentiCorrelatiType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_CONTRATTO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel(new Field("DatiContratto",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDocumentiCorrelatiType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_CONVENZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel(new Field("DatiConvenzione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDocumentiCorrelatiType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_RICEZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel(new Field("DatiRicezione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDocumentiCorrelatiType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_FATTURE_COLLEGATE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel(new Field("DatiFattureCollegate",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDocumentiCorrelatiType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_SAL = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiSALTypeModel(new Field("DatiSAL",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiSALType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_DDT = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDDTTypeModel(new Field("DatiDDT",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDDTType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_TRASPORTO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiTrasportoTypeModel(new Field("DatiTrasporto",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiTrasportoType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.FATTURA_PRINCIPALE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.FatturaPrincipaleTypeModel(new Field("FatturaPrincipale",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.FatturaPrincipaleType.class,"DatiGeneraliType",DatiGeneraliType.class));
	
	}
	
	public DatiGeneraliTypeModel(IField father){
	
		super(father);
	
		this.DATI_GENERALI_DOCUMENTO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiGeneraliDocumentoTypeModel(new ComplexField(father,"DatiGeneraliDocumento",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiGeneraliDocumentoType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_ORDINE_ACQUISTO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel(new ComplexField(father,"DatiOrdineAcquisto",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDocumentiCorrelatiType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_CONTRATTO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel(new ComplexField(father,"DatiContratto",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDocumentiCorrelatiType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_CONVENZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel(new ComplexField(father,"DatiConvenzione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDocumentiCorrelatiType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_RICEZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel(new ComplexField(father,"DatiRicezione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDocumentiCorrelatiType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_FATTURE_COLLEGATE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel(new ComplexField(father,"DatiFattureCollegate",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDocumentiCorrelatiType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_SAL = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiSALTypeModel(new ComplexField(father,"DatiSAL",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiSALType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_DDT = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDDTTypeModel(new ComplexField(father,"DatiDDT",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiDDTType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.DATI_TRASPORTO = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiTrasportoTypeModel(new ComplexField(father,"DatiTrasporto",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiTrasportoType.class,"DatiGeneraliType",DatiGeneraliType.class));
		this.FATTURA_PRINCIPALE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.FatturaPrincipaleTypeModel(new ComplexField(father,"FatturaPrincipale",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.FatturaPrincipaleType.class,"DatiGeneraliType",DatiGeneraliType.class));
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiGeneraliDocumentoTypeModel DATI_GENERALI_DOCUMENTO = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel DATI_ORDINE_ACQUISTO = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel DATI_CONTRATTO = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel DATI_CONVENZIONE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel DATI_RICEZIONE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDocumentiCorrelatiTypeModel DATI_FATTURE_COLLEGATE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiSALTypeModel DATI_SAL = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiDDTTypeModel DATI_DDT = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiTrasportoTypeModel DATI_TRASPORTO = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.FatturaPrincipaleTypeModel FATTURA_PRINCIPALE = null;
	 

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