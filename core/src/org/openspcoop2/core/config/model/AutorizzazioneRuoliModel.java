/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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
package org.openspcoop2.core.config.model;

import org.openspcoop2.core.config.AutorizzazioneRuoli;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model AutorizzazioneRuoli 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AutorizzazioneRuoliModel extends AbstractModel<AutorizzazioneRuoli> {

	public AutorizzazioneRuoliModel(){
	
		super();
	
		this.RUOLO = new org.openspcoop2.core.config.model.RuoloModel(new Field("ruolo",org.openspcoop2.core.config.Ruolo.class,"autorizzazione-ruoli",AutorizzazioneRuoli.class));
		this.MATCH = new Field("match",java.lang.String.class,"autorizzazione-ruoli",AutorizzazioneRuoli.class);
	
	}
	
	public AutorizzazioneRuoliModel(IField father){
	
		super(father);
	
		this.RUOLO = new org.openspcoop2.core.config.model.RuoloModel(new ComplexField(father,"ruolo",org.openspcoop2.core.config.Ruolo.class,"autorizzazione-ruoli",AutorizzazioneRuoli.class));
		this.MATCH = new ComplexField(father,"match",java.lang.String.class,"autorizzazione-ruoli",AutorizzazioneRuoli.class);
	
	}
	
	

	public org.openspcoop2.core.config.model.RuoloModel RUOLO = null;
	 
	public IField MATCH = null;
	 

	@Override
	public Class<AutorizzazioneRuoli> getModeledClass(){
		return AutorizzazioneRuoli.class;
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