/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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
package org.openspcoop2.core.registry.model;

import org.openspcoop2.core.registry.RuoliSoggetto;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model RuoliSoggetto 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RuoliSoggettoModel extends AbstractModel<RuoliSoggetto> {

	public RuoliSoggettoModel(){
	
		super();
	
		this.RUOLO = new org.openspcoop2.core.registry.model.RuoloSoggettoModel(new Field("ruolo",org.openspcoop2.core.registry.RuoloSoggetto.class,"ruoli-soggetto",RuoliSoggetto.class));
	
	}
	
	public RuoliSoggettoModel(IField father){
	
		super(father);
	
		this.RUOLO = new org.openspcoop2.core.registry.model.RuoloSoggettoModel(new ComplexField(father,"ruolo",org.openspcoop2.core.registry.RuoloSoggetto.class,"ruoli-soggetto",RuoliSoggetto.class));
	
	}
	
	

	public org.openspcoop2.core.registry.model.RuoloSoggettoModel RUOLO = null;
	 

	@Override
	public Class<RuoliSoggetto> getModeledClass(){
		return RuoliSoggetto.class;
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