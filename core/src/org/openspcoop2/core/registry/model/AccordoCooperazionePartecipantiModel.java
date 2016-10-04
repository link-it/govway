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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.AccordoCooperazionePartecipanti;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AccordoCooperazionePartecipanti 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordoCooperazionePartecipantiModel extends AbstractModel<AccordoCooperazionePartecipanti> {

	public AccordoCooperazionePartecipantiModel(){
	
		super();
	
		this.SOGGETTO_PARTECIPANTE = new org.openspcoop2.core.registry.model.IdSoggettoModel(new Field("soggetto-partecipante",org.openspcoop2.core.registry.IdSoggetto.class,"accordo-cooperazione-partecipanti",AccordoCooperazionePartecipanti.class));
	
	}
	
	public AccordoCooperazionePartecipantiModel(IField father){
	
		super(father);
	
		this.SOGGETTO_PARTECIPANTE = new org.openspcoop2.core.registry.model.IdSoggettoModel(new ComplexField(father,"soggetto-partecipante",org.openspcoop2.core.registry.IdSoggetto.class,"accordo-cooperazione-partecipanti",AccordoCooperazionePartecipanti.class));
	
	}
	
	

	public org.openspcoop2.core.registry.model.IdSoggettoModel SOGGETTO_PARTECIPANTE = null;
	 

	@Override
	public Class<AccordoCooperazionePartecipanti> getModeledClass(){
		return AccordoCooperazionePartecipanti.class;
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