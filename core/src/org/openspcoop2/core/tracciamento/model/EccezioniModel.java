/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.core.tracciamento.model;

import org.openspcoop2.core.tracciamento.Eccezioni;

import org.openspcoop2.generic_project.beans.AbstractModel;
import org.openspcoop2.generic_project.beans.IField;
import org.openspcoop2.generic_project.beans.Field;
import org.openspcoop2.generic_project.beans.ComplexField;


/**     
 * Model Eccezioni 
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EccezioniModel extends AbstractModel<Eccezioni> {

	public EccezioniModel(){
	
		super();
	
		this.ECCEZIONE = new org.openspcoop2.core.tracciamento.model.EccezioneModel(new Field("eccezione",org.openspcoop2.core.tracciamento.Eccezione.class,"eccezioni",Eccezioni.class));
	
	}
	
	public EccezioniModel(IField father){
	
		super(father);
	
		this.ECCEZIONE = new org.openspcoop2.core.tracciamento.model.EccezioneModel(new ComplexField(father,"eccezione",org.openspcoop2.core.tracciamento.Eccezione.class,"eccezioni",Eccezioni.class));
	
	}
	
	

	public org.openspcoop2.core.tracciamento.model.EccezioneModel ECCEZIONE = null;
	 

	@Override
	public Class<Eccezioni> getModeledClass(){
		return Eccezioni.class;
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