/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaHeaderType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model FatturaElettronicaHeaderType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FatturaElettronicaHeaderTypeModel extends AbstractModel<FatturaElettronicaHeaderType> {

	public FatturaElettronicaHeaderTypeModel(){
	
		super();
	
		this.DATI_TRASMISSIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiTrasmissioneTypeModel(new Field("DatiTrasmissione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.CEDENTE_PRESTATORE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.CedentePrestatoreTypeModel(new Field("CedentePrestatore",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.CESSIONARIO_COMMITTENTE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.CessionarioCommittenteTypeModel(new Field("CessionarioCommittente",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.SOGGETTO_EMITTENTE = new Field("SoggettoEmittente",java.lang.String.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class);
	
	}
	
	public FatturaElettronicaHeaderTypeModel(IField father){
	
		super(father);
	
		this.DATI_TRASMISSIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiTrasmissioneTypeModel(new ComplexField(father,"DatiTrasmissione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.DatiTrasmissioneType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.CEDENTE_PRESTATORE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.CedentePrestatoreTypeModel(new ComplexField(father,"CedentePrestatore",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.CESSIONARIO_COMMITTENTE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.CessionarioCommittenteTypeModel(new ComplexField(father,"CessionarioCommittente",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CessionarioCommittenteType.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class));
		this.SOGGETTO_EMITTENTE = new ComplexField(father,"SoggettoEmittente",java.lang.String.class,"FatturaElettronicaHeaderType",FatturaElettronicaHeaderType.class);
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.DatiTrasmissioneTypeModel DATI_TRASMISSIONE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.CedentePrestatoreTypeModel CEDENTE_PRESTATORE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.CessionarioCommittenteTypeModel CESSIONARIO_COMMITTENTE = null;
	 
	public IField SOGGETTO_EMITTENTE = null;
	 

	@Override
	public Class<FatturaElettronicaHeaderType> getModeledClass(){
		return FatturaElettronicaHeaderType.class;
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