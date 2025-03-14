/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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

import org.openspcoop2.core.config.Trasformazioni;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Trasformazioni 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TrasformazioniModel extends AbstractModel<Trasformazioni> {

	public TrasformazioniModel(){
	
		super();
	
		this.REGOLA = new org.openspcoop2.core.config.model.TrasformazioneRegolaModel(new Field("regola",org.openspcoop2.core.config.TrasformazioneRegola.class,"trasformazioni",Trasformazioni.class));
	
	}
	
	public TrasformazioniModel(IField father){
	
		super(father);
	
		this.REGOLA = new org.openspcoop2.core.config.model.TrasformazioneRegolaModel(new ComplexField(father,"regola",org.openspcoop2.core.config.TrasformazioneRegola.class,"trasformazioni",Trasformazioni.class));
	
	}
	
	

	public org.openspcoop2.core.config.model.TrasformazioneRegolaModel REGOLA = null;
	 

	@Override
	public Class<Trasformazioni> getModeledClass(){
		return Trasformazioni.class;
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