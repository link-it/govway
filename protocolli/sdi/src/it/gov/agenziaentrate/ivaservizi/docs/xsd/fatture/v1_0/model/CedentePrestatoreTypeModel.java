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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.CedentePrestatoreType;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model CedentePrestatoreType 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CedentePrestatoreTypeModel extends AbstractModel<CedentePrestatoreType> {

	public CedentePrestatoreTypeModel(){
	
		super();
	
		this.ID_FISCALE_IVA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IdFiscaleTypeModel(new Field("IdFiscaleIVA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.CODICE_FISCALE = new Field("CodiceFiscale",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
		this.DENOMINAZIONE = new Field("Denominazione",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
		this.NOME = new Field("Nome",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
		this.COGNOME = new Field("Cognome",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
		this.SEDE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel(new Field("Sede",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.STABILE_ORGANIZZAZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel(new Field("StabileOrganizzazione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.RAPPRESENTANTE_FISCALE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.RappresentanteFiscaleTypeModel(new Field("RappresentanteFiscale",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.ISCRIZIONE_REA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IscrizioneREATypeModel(new Field("IscrizioneREA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.REGIME_FISCALE = new Field("RegimeFiscale",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
	
	}
	
	public CedentePrestatoreTypeModel(IField father){
	
		super(father);
	
		this.ID_FISCALE_IVA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IdFiscaleTypeModel(new ComplexField(father,"IdFiscaleIVA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IdFiscaleType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.CODICE_FISCALE = new ComplexField(father,"CodiceFiscale",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
		this.DENOMINAZIONE = new ComplexField(father,"Denominazione",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
		this.NOME = new ComplexField(father,"Nome",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
		this.COGNOME = new ComplexField(father,"Cognome",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
		this.SEDE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel(new ComplexField(father,"Sede",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.STABILE_ORGANIZZAZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel(new ComplexField(father,"StabileOrganizzazione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IndirizzoType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.RAPPRESENTANTE_FISCALE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.RappresentanteFiscaleTypeModel(new ComplexField(father,"RappresentanteFiscale",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.RappresentanteFiscaleType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.ISCRIZIONE_REA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IscrizioneREATypeModel(new ComplexField(father,"IscrizioneREA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.IscrizioneREAType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.REGIME_FISCALE = new ComplexField(father,"RegimeFiscale",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IdFiscaleTypeModel ID_FISCALE_IVA = null;
	 
	public IField CODICE_FISCALE = null;
	 
	public IField DENOMINAZIONE = null;
	 
	public IField NOME = null;
	 
	public IField COGNOME = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel SEDE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IndirizzoTypeModel STABILE_ORGANIZZAZIONE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.RappresentanteFiscaleTypeModel RAPPRESENTANTE_FISCALE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.model.IscrizioneREATypeModel ISCRIZIONE_REA = null;
	 
	public IField REGIME_FISCALE = null;
	 

	@Override
	public Class<CedentePrestatoreType> getModeledClass(){
		return CedentePrestatoreType.class;
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