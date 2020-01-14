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
package org.openspcoop2.protocol.abstraction.model;

import org.openspcoop2.protocol.abstraction.RiferimentoSoggetto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RiferimentoSoggetto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RiferimentoSoggettoModel extends AbstractModel<RiferimentoSoggetto> {

	public RiferimentoSoggettoModel(){
	
		super();
	
		this.ID_SOGGETTO = new org.openspcoop2.protocol.abstraction.model.SoggettoModel(new Field("id-soggetto",org.openspcoop2.protocol.abstraction.Soggetto.class,"RiferimentoSoggetto",RiferimentoSoggetto.class));
		this.NOT_EXISTS_BEHAVIOUR = new org.openspcoop2.protocol.abstraction.model.SoggettoNotExistsBehaviourModel(new Field("not-exists-behaviour",org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour.class,"RiferimentoSoggetto",RiferimentoSoggetto.class));
	
	}
	
	public RiferimentoSoggettoModel(IField father){
	
		super(father);
	
		this.ID_SOGGETTO = new org.openspcoop2.protocol.abstraction.model.SoggettoModel(new ComplexField(father,"id-soggetto",org.openspcoop2.protocol.abstraction.Soggetto.class,"RiferimentoSoggetto",RiferimentoSoggetto.class));
		this.NOT_EXISTS_BEHAVIOUR = new org.openspcoop2.protocol.abstraction.model.SoggettoNotExistsBehaviourModel(new ComplexField(father,"not-exists-behaviour",org.openspcoop2.protocol.abstraction.SoggettoNotExistsBehaviour.class,"RiferimentoSoggetto",RiferimentoSoggetto.class));
	
	}
	
	

	public org.openspcoop2.protocol.abstraction.model.SoggettoModel ID_SOGGETTO = null;
	 
	public org.openspcoop2.protocol.abstraction.model.SoggettoNotExistsBehaviourModel NOT_EXISTS_BEHAVIOUR = null;
	 

	@Override
	public Class<RiferimentoSoggetto> getModeledClass(){
		return RiferimentoSoggetto.class;
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