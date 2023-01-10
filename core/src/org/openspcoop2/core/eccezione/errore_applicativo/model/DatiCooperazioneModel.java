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
	
		this.SENDER = new org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel(new Field("sender",org.openspcoop2.core.eccezione.errore_applicativo.Soggetto.class,"dati-cooperazione",DatiCooperazione.class));
		this.PROVIDER = new org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel(new Field("provider",org.openspcoop2.core.eccezione.errore_applicativo.Soggetto.class,"dati-cooperazione",DatiCooperazione.class));
		this.SERVICE = new org.openspcoop2.core.eccezione.errore_applicativo.model.ServizioModel(new Field("service",org.openspcoop2.core.eccezione.errore_applicativo.Servizio.class,"dati-cooperazione",DatiCooperazione.class));
		this.ACTION = new Field("action",java.lang.String.class,"dati-cooperazione",DatiCooperazione.class);
		this.APPLICATION = new Field("application",java.lang.String.class,"dati-cooperazione",DatiCooperazione.class);
	
	}
	
	public DatiCooperazioneModel(IField father){
	
		super(father);
	
		this.SENDER = new org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel(new ComplexField(father,"sender",org.openspcoop2.core.eccezione.errore_applicativo.Soggetto.class,"dati-cooperazione",DatiCooperazione.class));
		this.PROVIDER = new org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel(new ComplexField(father,"provider",org.openspcoop2.core.eccezione.errore_applicativo.Soggetto.class,"dati-cooperazione",DatiCooperazione.class));
		this.SERVICE = new org.openspcoop2.core.eccezione.errore_applicativo.model.ServizioModel(new ComplexField(father,"service",org.openspcoop2.core.eccezione.errore_applicativo.Servizio.class,"dati-cooperazione",DatiCooperazione.class));
		this.ACTION = new ComplexField(father,"action",java.lang.String.class,"dati-cooperazione",DatiCooperazione.class);
		this.APPLICATION = new ComplexField(father,"application",java.lang.String.class,"dati-cooperazione",DatiCooperazione.class);
	
	}
	
	

	public org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel SENDER = null;
	 
	public org.openspcoop2.core.eccezione.errore_applicativo.model.SoggettoModel PROVIDER = null;
	 
	public org.openspcoop2.core.eccezione.errore_applicativo.model.ServizioModel SERVICE = null;
	 
	public IField ACTION = null;
	 
	public IField APPLICATION = null;
	 

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