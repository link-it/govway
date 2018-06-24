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
package org.openspcoop2.core.eccezione.errore_applicativo.model;

import org.openspcoop2.core.eccezione.errore_applicativo.DatiCooperazione;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model DatiCooperazione 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DatiCooperazioneModel extends AbstractModel<DatiCooperazione> {

	public DatiCooperazioneModel(){
	
		super();
	
		this.FRUITORE = new org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel(new Field("fruitore",org.openspcoop2.core.eccezione.errore_applicativo.Soggetto.class,"dati-cooperazione",DatiCooperazione.class));
		this.EROGATORE = new org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel(new Field("erogatore",org.openspcoop2.core.eccezione.errore_applicativo.Soggetto.class,"dati-cooperazione",DatiCooperazione.class));
		this.SERVIZIO = new org.openspcoop2.core.eccezione.errore_applicativo.model.ServizioModel(new Field("servizio",org.openspcoop2.core.eccezione.errore_applicativo.Servizio.class,"dati-cooperazione",DatiCooperazione.class));
		this.AZIONE = new Field("azione",java.lang.String.class,"dati-cooperazione",DatiCooperazione.class);
		this.SERVIZIO_APPLICATIVO = new Field("servizio-applicativo",java.lang.String.class,"dati-cooperazione",DatiCooperazione.class);
	
	}
	
	public DatiCooperazioneModel(IField father){
	
		super(father);
	
		this.FRUITORE = new org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel(new ComplexField(father,"fruitore",org.openspcoop2.core.eccezione.errore_applicativo.Soggetto.class,"dati-cooperazione",DatiCooperazione.class));
		this.EROGATORE = new org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel(new ComplexField(father,"erogatore",org.openspcoop2.core.eccezione.errore_applicativo.Soggetto.class,"dati-cooperazione",DatiCooperazione.class));
		this.SERVIZIO = new org.openspcoop2.core.eccezione.errore_applicativo.model.ServizioModel(new ComplexField(father,"servizio",org.openspcoop2.core.eccezione.errore_applicativo.Servizio.class,"dati-cooperazione",DatiCooperazione.class));
		this.AZIONE = new ComplexField(father,"azione",java.lang.String.class,"dati-cooperazione",DatiCooperazione.class);
		this.SERVIZIO_APPLICATIVO = new ComplexField(father,"servizio-applicativo",java.lang.String.class,"dati-cooperazione",DatiCooperazione.class);
	
	}
	
	

	public org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel FRUITORE = null;
	 
	public org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel EROGATORE = null;
	 
	public org.openspcoop2.core.eccezione.errore_applicativo.model.ServizioModel SERVIZIO = null;
	 
	public IField AZIONE = null;
	 
	public IField SERVIZIO_APPLICATIVO = null;
	 

	@Override
	public Class<DatiCooperazione> getModeledClass(){
		return DatiCooperazione.class;
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