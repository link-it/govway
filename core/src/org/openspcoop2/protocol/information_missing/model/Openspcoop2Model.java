/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.Openspcoop2;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Openspcoop2 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Openspcoop2Model extends AbstractModel<Openspcoop2> {

	public Openspcoop2Model(){
	
		super();
	
		this.WIZARD = new org.openspcoop2.protocol.information_missing.model.WizardModel(new Field("wizard",org.openspcoop2.protocol.information_missing.Wizard.class,"openspcoop2",Openspcoop2.class));
		this.SOGGETTO = new org.openspcoop2.protocol.information_missing.model.SoggettoModel(new Field("soggetto",org.openspcoop2.protocol.information_missing.Soggetto.class,"openspcoop2",Openspcoop2.class));
		this.INPUT = new org.openspcoop2.protocol.information_missing.model.InputModel(new Field("input",org.openspcoop2.protocol.information_missing.Input.class,"openspcoop2",Openspcoop2.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.protocol.information_missing.model.ServizioApplicativoModel(new Field("servizio-applicativo",org.openspcoop2.protocol.information_missing.ServizioApplicativo.class,"openspcoop2",Openspcoop2.class));
		this.ACCORDO_COOPERAZIONE = new org.openspcoop2.protocol.information_missing.model.AccordoCooperazioneModel(new Field("accordo-cooperazione",org.openspcoop2.protocol.information_missing.AccordoCooperazione.class,"openspcoop2",Openspcoop2.class));
		this.ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel(new Field("accordo-servizio-parte-comune",org.openspcoop2.protocol.information_missing.AccordoServizioParteComune.class,"openspcoop2",Openspcoop2.class));
		this.ACCORDO_SERVIZIO_PARTE_SPECIFICA = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteSpecificaModel(new Field("accordo-servizio-parte-specifica",org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica.class,"openspcoop2",Openspcoop2.class));
		this.ACCORDO_SERVIZIO_COMPOSTO = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel(new Field("accordo-servizio-composto",org.openspcoop2.protocol.information_missing.AccordoServizioParteComune.class,"openspcoop2",Openspcoop2.class));
		this.FRUITORE = new org.openspcoop2.protocol.information_missing.model.FruitoreModel(new Field("fruitore",org.openspcoop2.protocol.information_missing.Fruitore.class,"openspcoop2",Openspcoop2.class));
	
	}
	
	public Openspcoop2Model(IField father){
	
		super(father);
	
		this.WIZARD = new org.openspcoop2.protocol.information_missing.model.WizardModel(new ComplexField(father,"wizard",org.openspcoop2.protocol.information_missing.Wizard.class,"openspcoop2",Openspcoop2.class));
		this.SOGGETTO = new org.openspcoop2.protocol.information_missing.model.SoggettoModel(new ComplexField(father,"soggetto",org.openspcoop2.protocol.information_missing.Soggetto.class,"openspcoop2",Openspcoop2.class));
		this.INPUT = new org.openspcoop2.protocol.information_missing.model.InputModel(new ComplexField(father,"input",org.openspcoop2.protocol.information_missing.Input.class,"openspcoop2",Openspcoop2.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.protocol.information_missing.model.ServizioApplicativoModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.protocol.information_missing.ServizioApplicativo.class,"openspcoop2",Openspcoop2.class));
		this.ACCORDO_COOPERAZIONE = new org.openspcoop2.protocol.information_missing.model.AccordoCooperazioneModel(new ComplexField(father,"accordo-cooperazione",org.openspcoop2.protocol.information_missing.AccordoCooperazione.class,"openspcoop2",Openspcoop2.class));
		this.ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel(new ComplexField(father,"accordo-servizio-parte-comune",org.openspcoop2.protocol.information_missing.AccordoServizioParteComune.class,"openspcoop2",Openspcoop2.class));
		this.ACCORDO_SERVIZIO_PARTE_SPECIFICA = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteSpecificaModel(new ComplexField(father,"accordo-servizio-parte-specifica",org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica.class,"openspcoop2",Openspcoop2.class));
		this.ACCORDO_SERVIZIO_COMPOSTO = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel(new ComplexField(father,"accordo-servizio-composto",org.openspcoop2.protocol.information_missing.AccordoServizioParteComune.class,"openspcoop2",Openspcoop2.class));
		this.FRUITORE = new org.openspcoop2.protocol.information_missing.model.FruitoreModel(new ComplexField(father,"fruitore",org.openspcoop2.protocol.information_missing.Fruitore.class,"openspcoop2",Openspcoop2.class));
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.WizardModel WIZARD = null;
	 
	public org.openspcoop2.protocol.information_missing.model.SoggettoModel SOGGETTO = null;
	 
	public org.openspcoop2.protocol.information_missing.model.InputModel INPUT = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ServizioApplicativoModel SERVIZIO_APPLICATIVO = null;
	 
	public org.openspcoop2.protocol.information_missing.model.AccordoCooperazioneModel ACCORDO_COOPERAZIONE = null;
	 
	public org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel ACCORDO_SERVIZIO_PARTE_COMUNE = null;
	 
	public org.openspcoop2.protocol.information_missing.model.AccordoServizioParteSpecificaModel ACCORDO_SERVIZIO_PARTE_SPECIFICA = null;
	 
	public org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel ACCORDO_SERVIZIO_COMPOSTO = null;
	 
	public org.openspcoop2.protocol.information_missing.model.FruitoreModel FRUITORE = null;
	 

	@Override
	public Class<Openspcoop2> getModeledClass(){
		return Openspcoop2.class;
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