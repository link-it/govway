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
package org.openspcoop2.protocol.information_missing.model;

import org.openspcoop2.protocol.information_missing.Operazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Operazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperazioneModel extends AbstractModel<Operazione> {

	public OperazioneModel(){
	
		super();
	
		this.SOGGETTO = new org.openspcoop2.protocol.information_missing.model.SoggettoModel(new Field("soggetto",org.openspcoop2.protocol.information_missing.Soggetto.class,"operazione",Operazione.class));
		this.INPUT = new org.openspcoop2.protocol.information_missing.model.InputModel(new Field("input",org.openspcoop2.protocol.information_missing.Input.class,"operazione",Operazione.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.protocol.information_missing.model.ServizioApplicativoModel(new Field("servizio-applicativo",org.openspcoop2.protocol.information_missing.ServizioApplicativo.class,"operazione",Operazione.class));
		this.ACCORDO_COOPERAZIONE = new org.openspcoop2.protocol.information_missing.model.AccordoCooperazioneModel(new Field("accordo-cooperazione",org.openspcoop2.protocol.information_missing.AccordoCooperazione.class,"operazione",Operazione.class));
		this.ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel(new Field("accordo-servizio-parte-comune",org.openspcoop2.protocol.information_missing.AccordoServizioParteComune.class,"operazione",Operazione.class));
		this.ACCORDO_SERVIZIO_PARTE_SPECIFICA = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteSpecificaModel(new Field("accordo-servizio-parte-specifica",org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica.class,"operazione",Operazione.class));
		this.ACCORDO_SERVIZIO_COMPOSTO = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel(new Field("accordo-servizio-composto",org.openspcoop2.protocol.information_missing.AccordoServizioParteComune.class,"operazione",Operazione.class));
		this.FRUITORE = new org.openspcoop2.protocol.information_missing.model.FruitoreModel(new Field("fruitore",org.openspcoop2.protocol.information_missing.Fruitore.class,"operazione",Operazione.class));
		this.PORTA_DELEGATA = new org.openspcoop2.protocol.information_missing.model.PortaDelegataModel(new Field("porta-delegata",org.openspcoop2.protocol.information_missing.PortaDelegata.class,"operazione",Operazione.class));
		this.PORTA_APPLICATIVA = new org.openspcoop2.protocol.information_missing.model.PortaApplicativaModel(new Field("porta-applicativa",org.openspcoop2.protocol.information_missing.PortaApplicativa.class,"operazione",Operazione.class));
	
	}
	
	public OperazioneModel(IField father){
	
		super(father);
	
		this.SOGGETTO = new org.openspcoop2.protocol.information_missing.model.SoggettoModel(new ComplexField(father,"soggetto",org.openspcoop2.protocol.information_missing.Soggetto.class,"operazione",Operazione.class));
		this.INPUT = new org.openspcoop2.protocol.information_missing.model.InputModel(new ComplexField(father,"input",org.openspcoop2.protocol.information_missing.Input.class,"operazione",Operazione.class));
		this.SERVIZIO_APPLICATIVO = new org.openspcoop2.protocol.information_missing.model.ServizioApplicativoModel(new ComplexField(father,"servizio-applicativo",org.openspcoop2.protocol.information_missing.ServizioApplicativo.class,"operazione",Operazione.class));
		this.ACCORDO_COOPERAZIONE = new org.openspcoop2.protocol.information_missing.model.AccordoCooperazioneModel(new ComplexField(father,"accordo-cooperazione",org.openspcoop2.protocol.information_missing.AccordoCooperazione.class,"operazione",Operazione.class));
		this.ACCORDO_SERVIZIO_PARTE_COMUNE = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel(new ComplexField(father,"accordo-servizio-parte-comune",org.openspcoop2.protocol.information_missing.AccordoServizioParteComune.class,"operazione",Operazione.class));
		this.ACCORDO_SERVIZIO_PARTE_SPECIFICA = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteSpecificaModel(new ComplexField(father,"accordo-servizio-parte-specifica",org.openspcoop2.protocol.information_missing.AccordoServizioParteSpecifica.class,"operazione",Operazione.class));
		this.ACCORDO_SERVIZIO_COMPOSTO = new org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel(new ComplexField(father,"accordo-servizio-composto",org.openspcoop2.protocol.information_missing.AccordoServizioParteComune.class,"operazione",Operazione.class));
		this.FRUITORE = new org.openspcoop2.protocol.information_missing.model.FruitoreModel(new ComplexField(father,"fruitore",org.openspcoop2.protocol.information_missing.Fruitore.class,"operazione",Operazione.class));
		this.PORTA_DELEGATA = new org.openspcoop2.protocol.information_missing.model.PortaDelegataModel(new ComplexField(father,"porta-delegata",org.openspcoop2.protocol.information_missing.PortaDelegata.class,"operazione",Operazione.class));
		this.PORTA_APPLICATIVA = new org.openspcoop2.protocol.information_missing.model.PortaApplicativaModel(new ComplexField(father,"porta-applicativa",org.openspcoop2.protocol.information_missing.PortaApplicativa.class,"operazione",Operazione.class));
	
	}
	
	

	public org.openspcoop2.protocol.information_missing.model.SoggettoModel SOGGETTO = null;
	 
	public org.openspcoop2.protocol.information_missing.model.InputModel INPUT = null;
	 
	public org.openspcoop2.protocol.information_missing.model.ServizioApplicativoModel SERVIZIO_APPLICATIVO = null;
	 
	public org.openspcoop2.protocol.information_missing.model.AccordoCooperazioneModel ACCORDO_COOPERAZIONE = null;
	 
	public org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel ACCORDO_SERVIZIO_PARTE_COMUNE = null;
	 
	public org.openspcoop2.protocol.information_missing.model.AccordoServizioParteSpecificaModel ACCORDO_SERVIZIO_PARTE_SPECIFICA = null;
	 
	public org.openspcoop2.protocol.information_missing.model.AccordoServizioParteComuneModel ACCORDO_SERVIZIO_COMPOSTO = null;
	 
	public org.openspcoop2.protocol.information_missing.model.FruitoreModel FRUITORE = null;
	 
	public org.openspcoop2.protocol.information_missing.model.PortaDelegataModel PORTA_DELEGATA = null;
	 
	public org.openspcoop2.protocol.information_missing.model.PortaApplicativaModel PORTA_APPLICATIVA = null;
	 

	@Override
	public Class<Operazione> getModeledClass(){
		return Operazione.class;
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