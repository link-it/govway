/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it).
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

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.CedentePrestatoreType;

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
	
		this.DATI_ANAGRAFICI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiAnagraficiCedenteTypeModel(new Field("DatiAnagrafici",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiAnagraficiCedenteType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.SEDE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel(new Field("Sede",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IndirizzoType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.STABILE_ORGANIZZAZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel(new Field("StabileOrganizzazione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IndirizzoType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.ISCRIZIONE_REA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IscrizioneREATypeModel(new Field("IscrizioneREA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IscrizioneREAType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.CONTATTI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.ContattiTypeModel(new Field("Contatti",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.ContattiType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.RIFERIMENTO_AMMINISTRAZIONE = new Field("RiferimentoAmministrazione",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
	
	}
	
	public CedentePrestatoreTypeModel(IField father){
	
		super(father);
	
		this.DATI_ANAGRAFICI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiAnagraficiCedenteTypeModel(new ComplexField(father,"DatiAnagrafici",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.DatiAnagraficiCedenteType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.SEDE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel(new ComplexField(father,"Sede",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IndirizzoType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.STABILE_ORGANIZZAZIONE = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel(new ComplexField(father,"StabileOrganizzazione",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IndirizzoType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.ISCRIZIONE_REA = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IscrizioneREATypeModel(new ComplexField(father,"IscrizioneREA",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.IscrizioneREAType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.CONTATTI = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.ContattiTypeModel(new ComplexField(father,"Contatti",it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.ContattiType.class,"CedentePrestatoreType",CedentePrestatoreType.class));
		this.RIFERIMENTO_AMMINISTRAZIONE = new ComplexField(father,"RiferimentoAmministrazione",java.lang.String.class,"CedentePrestatoreType",CedentePrestatoreType.class);
	
	}
	
	

	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.DatiAnagraficiCedenteTypeModel DATI_ANAGRAFICI = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel SEDE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IndirizzoTypeModel STABILE_ORGANIZZAZIONE = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.IscrizioneREATypeModel ISCRIZIONE_REA = null;
	 
	public it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.model.ContattiTypeModel CONTATTI = null;
	 
	public IField RIFERIMENTO_AMMINISTRAZIONE = null;
	 

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